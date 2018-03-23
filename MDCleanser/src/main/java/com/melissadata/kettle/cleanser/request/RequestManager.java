package com.melissadata.kettle.cleanser.request;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;

import com.melissadata.kettle.cleanser.MDCleanserStep;
import com.melissadata.kettle.cleanser.service.MDCleanserService;



public class RequestManager {

	public class Request {

		private MDCleanserService			service;
		private ArrayList<MDCleanserRequest>	requests;
		private KettleException						exception;

		public Request(MDCleanserService service, List<MDCleanserRequest> requests) {

			this.service = service;
			this.requests = new ArrayList<MDCleanserRequest>(requests);
		}

		public KettleException getException() {

			return exception;
		}

		public List<MDCleanserRequest> getRequests() {

			return requests;
		}

		public void run() {

//			System.out.println(" RUN CALLED *********************** " + Thread.currentThread().getId());
			//FIXME Retries
			int retries = 3;//service.busCoderData.realWebRetries;

			do {
				exception = null;
				try {
					// Process the requests
					service.processRequests(requests/*, attempts*/);


				} catch (KettleException e) {
					// Register the exception
					exception = e;

				} catch (ThreadDeath t) {
					// Apparently needed to clean up thread correctly after a premature stop
					throw t;

				} catch (Throwable t) {
					// Wrap the exception
					exception = new KettleException(MDCleanserStep.getErrorString("RequestFailed"), t);
				}

			} while ((retries-- > 0) && (exception != null) && !stopped);
		}
	}

	private enum Status {
		FREE,
		CLAIMED,
		REQUESTED,
		RUNNING,
		COMPLETED,
		STOPPED
	}

	private interface Worker {

		Request getCompletedRequest();

		void interrupt();

		boolean isActive();

		boolean isAlive();

		void join(long delay) throws InterruptedException;

		void runRequest(Request request);

		void stop();
		
		boolean tryClaim();
	}

	/**
	 * Special worker used when there is only one worker in the pool. It will run the request
	 * in the same thread as the caller and will block until it completes.
	 */
	private class WorkerNoThread implements Worker {

		private Request	request;

		@Override
		public Request getCompletedRequest() {

			// Return the completed request
			Request done = request;
			request = null;
			return done;
		}

		@Override
		public void interrupt() {

			// only has meaning for threaded worker
		}

		@Override
		public boolean isActive() {

			// Active if there is an outstanding request
			return (request != null);
		}

		@Override
		public boolean isAlive() {

			// Alive if there is an outstanding request
			return (request != null);
		}

		@Override
		public void join(long delay) {

			// only has meaning for threaded worker
		}

		@Override
		public void runRequest(Request request) {

			// no queueing. Just run it immediately
			this.request = request;
			request.run();
		}

		@Override
		public void stop() {

			// Shut it down by clearing the outstanding request (if any)
			request = null;
		}

		@Override
		public boolean tryClaim() {

			// Can only claim if there is no outstanding request
			return (request == null);
		}


	}

	private class WorkerThread extends Thread implements Worker {

		private Status	status;

		private Request	request;

		public WorkerThread() {

			super("Request Worker - " + nextID++);
			status = Status.FREE;
		}

		@Override
		public Request getCompletedRequest() {

			synchronized (this) {
				// If request is completed then return and free the thread
				if (status == Status.COMPLETED) {
					Request request = this.request;
					this.request = null;
					status = Status.FREE;
					return request;
				}
				return null;
			}
		}

		@Override
		public boolean isActive() {

			synchronized (status) {
				// A thread is active if it is alive and not free
				return isAlive() && (status != Status.FREE);
			}
		}

		@Override
		public void run() {

			while (!stopped) {
				// Wait for a request
				synchronized (this) {
					while (!stopped && (status != Status.REQUESTED)) {
						try {
							this.wait();
						} catch (InterruptedException ignored) {
						}
					}

					// If we got a request then indicate that it is running
					if (request != null) {
						status = Status.RUNNING;
					}
				}

				// Got a request?
				if (request != null) {
					// Run the request
//					System.out.println(" \n#### Call request run - " + Thread.currentThread().getId());
					request.run();

					// We are completed
					synchronized (this) {
						status = Status.COMPLETED;
					}
				}
			}

			// Thread has stopped
			synchronized (this) {
				status = Status.STOPPED;
			}
		}

		@Override
		public void runRequest(Request request) {

			synchronized (this) {
				this.request = request;

				status = Status.REQUESTED;

				// Signal the thread that it process something
				notify();
			}
		}

		@Override
		public boolean tryClaim() {

			synchronized (this) {
				// Claim it only if it is free
				if (status == Status.FREE) {
					status = Status.CLAIMED;
					return true;
				}
				return false;
			}
		}

	}

	private long				ourGuyId;

	private boolean				fin				= false;		;

	private static int			nextID			= 0;

	private static final long	BUSY_WAIT		= 100L;		// 100 ms

	private static final long	STOP_TIMEOUT	= 30 * 1000L;	// 30 seconds

	private Worker				workers[];

	private boolean				stopped;

	public RequestManager(int maxRequests) {

		workers = new Worker[maxRequests];
	}

	public void addRequest(MDCleanserService service, List<MDCleanserRequest> requests) {

		// Find a worker to run the request. Blocks if none available.
		Worker worker = getWorker();
		// Tell the thread what it should work on
		if (!stopped) {
			// There will always be a thread if the manager has not been stopped
			worker.runRequest(new Request(service, requests));
		}
	}

	public void dispose() {

		// Stop the manager
		stop();
	}

	public Request getCompletedRequest(boolean done) {

		int active;
		while (!stopped) {
			// Loop through workers looking for completed requests
			active = 0;
			for (int i = 0; !stopped && (i < workers.length); i++) {
				Worker worker = workers[i];
				if (worker == null) {
					continue;
				}

				// Is it completed?
				Request request;
				if ((request = worker.getCompletedRequest()) != null) { return request; }

				// Remember if a worker is active
				if (worker.isActive()) {
					active++;
				}
			}

			// No completed requests were found.

			// We should wait if submissions are done or all workers are active
			// (addRequest assumes there will always be a free worker)
			boolean wait = done || (active == workers.length);

			// If not waiting or there are no active workers then return immediately
			if (stopped || !wait || (active == 0)) { return null; }

			// Pause a while and try again
			try {
				Thread.sleep(BUSY_WAIT);
			} catch (InterruptedException ignored) {
			}
		}

		return null;
	}

	public void stop() {
		// Only called once
		if (stopped) { return; }
		stopped = true;

		// Give the workers some time to shut down
		synchronized (workers) {
			long stopBy = System.currentTimeMillis() + STOP_TIMEOUT;
			int active;
			do {
				// Wake up the workers
				for (Worker worker : workers) {
					if (worker != null) {
						worker.interrupt();
					}
				}

				active = 0;
				for (Worker worker : workers) {
					if (worker != null) {
						long delay = stopBy - System.currentTimeMillis();
						if (delay > 0) {
							try {
								worker.join(delay);
							} catch (InterruptedException ignored) {
							}
						}

						// If the thread is still active then we might need to try again
						if (worker.isActive()) {
							active++;
						}
					}
				}
			} while ((active != 0) && (stopBy < System.currentTimeMillis()));
		}

		// Brute force kill any workers that are still running
		for (int i = 0; i < workers.length; i++) {
			Worker worker = workers[i];
			if ((worker != null) && worker.isAlive()) {
				worker.stop();
				workers[i] = null;
			}
		}
	}

	private Worker getWorker() {

		// Loop until we get a worker or the manager is stopped
		while (!stopped) {
//			System.out.println(" CREAT THREADS = " + workers.length);
			for (int i = 0; !stopped && (i < workers.length); i++) {
				Worker worker = workers[i];

				// Create new worker as needed
				if (worker == null) {
					// Create a threaded worker if the pool is greater than one
					if (workers.length > 1) {
						worker = new WorkerThread();
						((WorkerThread) worker).start();
					} else {
						worker = new WorkerNoThread();
					}
					workers[i] = worker;
				}

				// Try to claim it
//				if (worker.tryClaim())
//					System.out.println(" CLAIM THREAD : " + worker.tryClaim() + " -- " + worker);
				if (worker.tryClaim()) { return worker; }
			}

			// This should never happen. The code for retrieving a request will never
			// exit as long as there is no free threads.
			throw new RuntimeException("Could not find free thread to run request");
		}
		return null;
	}

	private void showSleepMsg(String message) {

		MDCleanserStep.showSleepMsg(message);

	}

}
