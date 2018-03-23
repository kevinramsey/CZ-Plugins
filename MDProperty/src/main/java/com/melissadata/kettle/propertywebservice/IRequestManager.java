package com.melissadata.kettle.propertywebservice;

import java.util.List;

//import com.melissadata.kettle.propertywebservice.request.MDPropertyWebServiceRequest;
import com.melissadata.kettle.propertywebservice.request.RequestManager.Request;
import com.melissadata.kettle.propertywebservice.web.MDPropertyWebService;

public interface IRequestManager {
	/**
	 * Called to add requests to the manager's queue
	 *
	 * @param service
	 * @param requests
	 * @param queue
	 */
	void addRequest(MDPropertyWebService service, List<MDPropertyWebServiceRequest> requests, int queue);

	/**
	 * Called to clean up the request manager
	 */
	void dispose();

	/**
	 * Called to retrieve any requests that are completed
	 *
	 * @param done
	 * @return
	 */
	Request getCompletedRequest(boolean done);

	/**
	 * Called to stop request manager processing
	 */
	void stop();
}
