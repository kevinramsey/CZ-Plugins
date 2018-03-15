package com.melissadata.kettle.iplocator;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.mdIpLocator;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;

import java.util.List;

/**
 * Created by Kevin on 5/1/2017.
 */
public class MDIPLocatorLocalService extends MDCheckService {

	private static Class<?>				PKG	= MDIPLocatorLocalService.class;
	//private AdvancedConfigurationMeta	acMeta;

	public MDIPLocatorLocalService(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {
		super(stepData, checkData, space, log);
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#buildRequest(com.melissadata.kettle.support.IOMeta, java.lang.Object[])
 */
	@Override
	public MDCheckRequest buildRequest(IOMeta ioMeta, Object[] inputData) {
		// TODO: Use a pool of request objects?
		return new MDCheckCVRequest(ioMeta, inputData);
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#init()
 */
	@Override
	public void init() throws KettleException {
		// Call parent first
		super.init();
		log.logDetailed("MDCheck Local service init called");
		// Perform initialiation checks
		Validations checker = new Validations();
		checker.checkInitialization(stepData);
//		// Get the real Customer id and data path
//		acMeta = stepData.getAdvancedConfiguration();

		checkData.realDataPath = acMeta.getLocalDataPath();
		log.logDetailed("MDCheck Local service data path = " + checkData.realDataPath);

		// We process only one request at a time
		checkData.maxRequests = acMeta.getMaxRequests();
		// Create license object
		initLicense();

		log.logDetailed("IPLocator Local service initializing mdObjects ...");
		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)
				&& AdvancedConfigurationMeta.isCommunity()){
			String msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", "IP Locator") + "Local";
			log.logBasic(msg);
			MDCheck.showSleepMsg(msg, stepData.getCheckTypes());
		}

		initIPLocator();
	}

	/**
	 * Called to initialize the IPLocaror object
	 *
	 * @throws KettleException
	 */
	private void initIPLocator() throws KettleException {
		// Skip if not present
		IPLocatorMeta ipMeta = stepData.getIPLocator();
		if (ipMeta == null) { return; }
		// Fail testing if not licensed
		if (!ipMeta.isLicensed()) {
			ipMeta.localMsg = MDCheck.getErrorString("NotLicensed");
			ipMeta.localException = new KettleException(MDCheck.getErrorString("InitializeService", ipMeta.localMsg));
			if (!testing && ipMeta.isEnabled()) { throw ipMeta.localException; }
			return;
		}
		try {
			// Allocate IPLocator object and set it's license string
			mdIpLocator IPL;
			try {
				IPL = checkData.ipLocator = DQTObjectFactory.newIpLocator();
			} catch (DQTObjectException e) {
				// Handle failure to create object
				ipMeta.localMsg = e.getMessage();
				ipMeta.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw ipMeta.localException; }
				return;
			}
			String ipLicense = ipMeta.getLicense();
			if (IPL.SetLicenseString(ipLicense) == false) {
				initFailed = true;
				// Handle failure to set license string
				ipMeta.localMsg = MDCheck.getErrorString("SettingLicense", ipLicense);
				ipMeta.localException = new KettleException(MDCheck.getErrorString("InitializeService", ipMeta.localMsg));
				if (!testing) { throw ipMeta.localException; }
				return;
			}
			// Initialize the object
			IPL.SetPathToIpLocatorFiles(checkData.realDataPath);
			if (IPL.InitializeDataFiles() != mdIpLocator.ProgramStatus.ErrorNone) {
				initFailed = true;
				// Handle failure to initialize
				ipMeta.localMsg = MDCheck.getErrorString("InitializeService", IPL.GetInitializeErrorString());
				ipMeta.localException = new KettleException(ipMeta.localMsg);
				if (!testing) { throw ipMeta.localException; }
				return;
			}
			// Get database information
			ipMeta.localDBDate = IPL.GetDatabaseDate();
			ipMeta.localDBBuildNo = IPL.GetBuildNumber();
		} catch (UnsatisfiedLinkError e) {
			System.err.println(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NativeLibFail") + e);
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = false;
			// If anything unusual happened then return an initialization failure
			ipMeta.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			ipMeta.localException = new KettleException(ipMeta.localMsg, t);
			if (!testing) { throw ipMeta.localException; }
		}
	}

//	/**
//	 * Called create the license object
//	 *
//	 * @throws KettleException
//	 */
//	public void initLicense() throws KettleException {
//		// Called to initialize the licensed products fields
//		if (acMeta == null) {
//			acMeta = stepData.getAdvancedConfiguration();
//		}
//		acMeta.getProducts();
//		if (acMeta.testLicense(stepData.getCheckTypes()) != AdvancedConfigurationMeta.LicenseTestResult.NoErrors) { throw new KettleException(acMeta.testLicense(stepData.getCheckTypes()).toString()); }
//	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#processRequests()
 */
	@Override
	public void processRequests(List<MDCheckRequest> requests, int queue, boolean chkXML, int attempts) throws KettleException {
		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)){
			return;
		}
		if (stepData.getIPLocator() != null) {
			stepData.getIPLocator().doLocalRequests(checkData, requests);
		}
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#outputData(int)
 */
	@Override
	public void outputData(List<MDCheckRequest> requests, int queue) {
		// Process IP Locator requests
		if (stepData.getIPLocator() != null) {
			stepData.getIPLocator().outputData(checkData, requests);
		}
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#determineRequestRoute(com.melissadata.kettle.MDCheckRequest)
 */
	@Override
	public int determineRequestRoute(MDCheckRequest request) throws KettleValueException {
		// currently uses only one queue
		return 0;
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#saveReports()
 */
	@Override
	public void saveReports() throws KettleException {
		// No reporting forIP Locator
	}
}
