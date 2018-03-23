package com.melissadata.kettle.globalverify;

import java.util.ArrayList;
import java.util.List;

import com.melissadata.kettle.globalverify.Local.MDGlobalLocalService;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.kettle.globalverify.support.MDPropTags;
import com.melissadata.kettle.globalverify.web.MDGlobalWebService;

public abstract class MDGlobalService {
	protected MDGlobalData			checkData;
	protected MDGlobalMeta			checkMeta;
	protected VariableSpace			space;
	protected LogChannelInterface	log;
	protected boolean				testing;
	protected boolean				initFailed;

	public MDGlobalService(MDGlobalData checkData, MDGlobalMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		this.checkData = checkData;
		this.checkMeta = checkMeta;
		this.space = space;
		this.log = log;
		initFailed = false;
	}

	/**
	 * Called to allocate the service handler based on the service type
	 *
	 * @param checkData
	 * @param checkMeta
	 * @param space
	 * @param log
	 * @return
	 */
	public static MDGlobalService create(MDGlobalData checkData, MDGlobalMeta checkMeta, VariableSpace space, LogChannelInterface log, boolean local) {

		MDGlobalService service;
		if (local) {
			service = new MDGlobalLocalService(checkData, checkMeta, space, log);
		} else {
			service = new MDGlobalWebService(checkData, checkMeta, space, log);
		}

		return service;
	}

	/**
	 * Called to set the testing mode
	 * 
	 * @param testing
	 */
	public void setTesting(boolean testing) {
		this.testing = testing;
	}

	/**
	 * Called to initialize the service handler
	 * 
	 * @throws KettleException
	 */
	public void init() throws KettleException {
		// Allocate request array
		checkData.requests = new ArrayList<List<MDGlobalRequest>>(2);
		checkData.requests.add(new ArrayList<MDGlobalRequest>());
		checkData.requests.add(new ArrayList<MDGlobalRequest>());
	}

	/**
	 * Create a request manager for processing requests in an asynchronous manner
	 *
	 */
	protected IRequestManager createRequestManager() {
		// If this is a local service then we only allow one thread in the thread pool.
		// Otherwise, we use the configured thread count.
		int maxThreads = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_THREADS, "1"));
		return new RequestManager(maxThreads);
	}

	/**
	 * Called to create a request object for this service type
	 * 
	 * @param ioMeta
	 * @param inputData
	 * @return
	 */
	public abstract Object buildRequest(IOMetaHandler ioMeta, Object[] inputData);

	/**
	 * Called to determine the service route to use for this request. This is currently only used for change of address
	 * requests.
	 * US change of address requests are routed to the first queue while Canadian are routed to the second queue.
	 * 
	 * All other types of reuquests are routed to the first queue.
	 * 
	 * @param request
	 * @return
	 * @throws KettleException
	 */
	public abstract int determineRequestRoute(MDGlobalRequest request) throws KettleException;

	/**
	 * Called to test the customer status
	 * 
	 * @throws KettleException
	 */
	public void testCustomerStatus() throws KettleException {
		// Default is to do nothing
	}

	/**
	 * Called to process the queued requests
	 * 
	 * @param requests
	 * @param queue
	 * 
	 * @throws KettleException
	 */
	public abstract void processRequests(List<MDGlobalRequest> requests, int queue, int attempts) throws KettleException;

	/**
	 * Called to output the data from the queued results
	 * 
	 * @param queue
	 */
	public abstract void outputData(List<MDGlobalRequest> requests, int queue);


	/**
	 * Called to dispose of service resources
	 */
	public void dispose() {
		// No base resources
	}

	public boolean checkProxy() {
		return true;
	}
}
