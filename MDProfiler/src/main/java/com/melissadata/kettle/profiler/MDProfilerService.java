package com.melissadata.kettle.profiler;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;

public abstract class MDProfilerService {
	/**
	 * Called to allocate the service handler based on the service type
	 *
	 * @param checkData
	 * @param checkMeta
	 * @param space
	 * @param log
	 * @return
	 * @throws KettleException
	 */
	public static MDProfilerLocalService create(MDProfilerData checkData, MDProfilerMeta checkMeta, VariableSpace space, LogChannelInterface log) throws KettleException {
		MDProfilerLocalService service = new MDProfilerLocalService(checkData, checkMeta, space, log);
		return service;
	}
	protected MDProfilerData		checkData;
	protected MDProfilerMeta		checkMeta;
	protected VariableSpace			space;
	protected LogChannelInterface	log;
	protected boolean				testing;
	protected boolean				initFailed;

	public MDProfilerService(MDProfilerData checkData, MDProfilerMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		this.checkData = checkData;
		this.checkMeta = checkMeta;
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
	public abstract MDProfilerRequest buildRequest(IOMetaHandler ioMeta, OutputPin outpin, Object[] inputData);

	public boolean checkProxy() {
		return true;
	}

	/**
	 * Create a request manager for processing requests in an asynchronous manner
	 *
	 * @return
	 */
	protected IRequestManager createRequestManager() {
		// FIXME add multi threading
		int maxThreads = 1;// Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_MAX_THREADS, ""));
		return new RequestManager(maxThreads);
	}

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
	public abstract int determineRequestRoute(MDProfilerRequest request) throws KettleException;

	/**
	 * Called to dispose of service resources
	 */
	public void dispose() {
		// No base resources
	}

	/**
	 * Called to initialize the service handler
	 *
	 * @throws KettleException
	 */
	public void init() throws KettleException {
		// Allocate request array
		checkData.requests = new ArrayList<List<MDProfilerRequest>>(2);
		checkData.requests.add(new ArrayList<MDProfilerRequest>());
		checkData.requests.add(new ArrayList<MDProfilerRequest>());
	}

	/**
	 * Called to output the data from the queued results
	 *
	 * @param queue
	 */
	public abstract void outputData(List<MDProfilerRequest> requests, int queue);

	/**
	 * Called to process the queued requests
	 *
	 * @param requests
	 * @param queue
	 *
	 * @throws KettleException
	 */
	public abstract void processRequests(List<MDProfilerRequest> requests, int queue) throws KettleException;

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
}
