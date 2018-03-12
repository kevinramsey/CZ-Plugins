package com.melissadata.kettle.profiler;

import java.util.List;

import com.melissadata.kettle.profiler.RequestManager.Request;

public interface IRequestManager {
	/**
	 * Called to add requests to the manager's queue
	 *
	 * @param service
	 * @param requests
	 * @param queue
	 */
	void addRequest(MDProfilerService service, List<MDProfilerRequest> requests, int queue);

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
