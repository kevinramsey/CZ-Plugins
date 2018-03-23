package com.melissadata.kettle.globalverify;

import java.util.List;

import com.melissadata.kettle.globalverify.RequestManager.Request;

public interface IRequestManager {
	/**
	 * Called to add requests to the manager's queue
	 * 
	 * @param service
	 * @param requests
	 * @param queue
	 */
	void addRequest(MDGlobalService service, List<MDGlobalRequest> requests, int queue);

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

	/**
	 * Called to clean up the request manager
	 */
	void dispose();
}
