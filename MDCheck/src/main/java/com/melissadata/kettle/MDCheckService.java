package com.melissadata.kettle;

import java.util.ArrayList;
import java.util.List;

import com.melissadata.kettle.iplocator.MDIPLocatorLocalService;
import com.melissadata.kettle.iplocator.MDIPLocatorWebService;
import com.melissadata.kettle.sm.service.MDCheckCOAService;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.support.IRequestManager;
import com.melissadata.kettle.cv.MDCheckLocalService;
import com.melissadata.kettle.cv.MDCheckWebService;
import com.melissadata.kettle.mu.MDCheckMUService;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public abstract class MDCheckService {

	/**
	 * Called to allocate the service handler based on the service type
	 *
	 * @param stepData
	 * @param checkData
	 * @param space
	 * @param log
	 * @return
	 */
	public static MDCheckService create(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {
		// Create the service based on the service type the service type and initialize for it
		MDCheckService service;
		//System.out.println(" - - - -    CREATE SERVICE : " + stepData.getAdvancedConfiguration().getServiceType() + "	-	" + stepData.getMatchUp());
		switch (stepData.getAdvancedConfiguration().getServiceType()) {
			case Web:
			case CVS:
				// Use different service handler for smart mover
				if (stepData.getSmartMover() != null) {
					service = new MDCheckCOAService(stepData, checkData, space, log);
				} else if (stepData.getIPLocator() != null) {
					service = new MDIPLocatorWebService(stepData, checkData, space, log);
				} else {
					service = new MDCheckWebService(stepData, checkData, space, log);
				}
				break;
			case Local:
				// Use different service handler for matchup
				//FIXME matchUp Global
				if (stepData.getMatchUp() != null) {
					service = new MDCheckMUService(stepData, checkData, space, log);
				} else if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
					service = new MDIPLocatorLocalService(stepData.clone(), checkData, space, log);
				} else {
					service = new MDCheckLocalService(stepData.clone(), checkData, space, log);
				}
				break;
			default:
				return null;
		}
		return service;
	}

	protected MDCheckStepData           stepData   = null;
	protected MDCheckData               checkData  = null;
	protected AdvancedConfigurationMeta acMeta     = null;
	protected VariableSpace             space      = null;
	protected LogChannelInterface       log        = null;
	protected boolean                   testing    = false;
	protected boolean                   initFailed = false;

	public MDCheckService(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {

		this.stepData = stepData;
		this.checkData = checkData;
		this.acMeta = stepData.getAdvancedConfiguration();
		this.space = space;
		this.log = log;
		initFailed = false;
	}

	/**
	 * Called to create a request object for this service type
	 *
	 * @param ioMeta
	 * @param inputData
	 * @return
	 */
	public abstract MDCheckRequest buildRequest(IOMeta ioMeta, Object[] inputData);

	public boolean checkProxy() {

		return true;
	}

	/**
	 * Called to determine the service route to use for this request. This is currently only used for change of address
	 * requests.
	 * US change of address requests are routed to the first queue while Canadian are routed to the second queue.
	 * <p>
	 * All other types of reuquests are routed to the first queue.
	 *
	 * @param request
	 * @return
	 * @throws KettleException
	 */
	public int determineRequestRoute(MDCheckRequest request) throws KettleException {
		// TODO: CV currently uses only one queue
		return 0;
	}

	/**
	 * Called to dispose of service resources
	 */
	public void dispose() {
		// No base resources
	}

	public String getproductName() {

		String name = "";
		if (stepData.getSmartMover() != null) {
			name = "MD Smart Mover";
		} else if (stepData.getAddressVerify() != null) {
			name = "MD Contact Verify";
		} else if (stepData.getIPLocator() != null) {
			name = "MD IP Locator";
		} else if (stepData.getMatchUp() != null) {
			name = "MD MatchUp";
		}
		return name;
	}

	/**
	 * Called to initialize the service handler
	 *
	 * @throws KettleException
	 */
	public void init() throws KettleException {
		// Allocate request array
		checkData.requests = new ArrayList<List<MDCheckRequest>>(2);
		checkData.requests.add(new ArrayList<MDCheckRequest>());
		checkData.requests.add(new ArrayList<MDCheckRequest>());
	}

	/**
	 * Called create the license object
	 *
	 * @throws KettleException
	 */
	public void initLicense() throws KettleException {
		// Called to initialize the licensed products fields
		if (acMeta == null) {
			acMeta = stepData.getAdvancedConfiguration();
		}
		acMeta.getProducts();
		if (acMeta.testLicense(stepData.getCheckTypes()) != AdvancedConfigurationMeta.LicenseTestResult.NoErrors) {
			throw new KettleException(acMeta.testLicense(stepData.getCheckTypes()).toString());
		}
	}

	/**
	 * Called to output the data from the queued results
	 *
	 * @param queue
	 */
	public abstract void outputData(List<MDCheckRequest> requests, int queue);

	/**
	 * Called to process the queued requests
	 *
	 * @param requests
	 * @param queue
	 * @throws KettleException
	 */
	public abstract void processRequests(List<MDCheckRequest> requests, int queue, boolean ckXML, int attempts) throws KettleException;

	/**
	 * Called to save any reports generated by the service
	 *
	 * @throws KettleException
	 */
	public abstract void saveReports() throws KettleException;

	/**
	 * Called to set the testing mode
	 *
	 * @param testing
	 */
	public void setTesting(boolean testing) {

		this.testing = testing;
	}

	/**
	 * Called to test the customer status
	 *
	 * @throws KettleException
	 */
	public void testCustomerStatus() throws KettleException {
		// Default is to do nothing
	}

	/**
	 * Create a request manager for processing requests in an asynchronous manner
	 *
	 * @return
	 */
	protected IRequestManager createRequestManager() {
		// If this is a local service then we only allow one thread in the thread pool.
		// Otherwise, we use the configured thread count.
		AdvancedConfigurationMeta acMeta     = stepData.getAdvancedConfiguration();
		int                       maxThreads = (acMeta.getServiceType() == ServiceType.Local) ? 1 : acMeta.getMaxThreads();
		log.logDetailed("Create RequestManager: Service Type = " + acMeta.getServiceType().name());
		return new RequestManager(maxThreads);
	}
}
