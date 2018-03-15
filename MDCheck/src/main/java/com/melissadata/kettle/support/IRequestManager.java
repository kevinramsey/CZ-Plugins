package com.melissadata.kettle.support;

import java.util.List;

import com.melissadata.kettle.MDCheckRequest;
import com.melissadata.kettle.MDCheckService;
import com.melissadata.kettle.RequestManager.Request;

public interface IRequestManager {
	/**
	 * Called to add requests to the manager's queue
	 *
	 * @param service
	 * @param requests
	 * @param queue
	 */
	void addRequest(MDCheckService service, List<MDCheckRequest> requests, int queue);

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
