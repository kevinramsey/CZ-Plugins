package com.melissadata.kettle.propertywebservice.web;

import com.melissadata.cz.MDProps;
import com.melissadata.kettle.MDSettings.SettingsTags;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceData;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.*;

public class WebClient {
	/*
	 * 598 (Informal convention) network read timeout error
	 * This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network read
	 * timeout behind the proxy to a client in front of the proxy.
	 */
	private static final int             NETWORK_READ_TIMEOUT    = 598;
	/*
	 * 599 (Informal convention) network connect timeout error
	 * This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network connect
	 * timeout behind the proxy to a client in front of the proxy.
	 */
	private static final int             NETWORK_CONNECT_TIMEOUT = 599;
	private static       ExecutorService threadPool              = Executors.newCachedThreadPool();
	private LogChannelInterface log;
	private int     timeout     = 0;
	private int     retries     = 0;
	private String  proxyHost   = null;
	private int     proxyPort   = 0;
	private String  proxyUser   = null;
	private String  proxyPass   = null;
	private boolean proxyCheck  = false;
	private String  webEncoding = "ISO-8859-1";// default

	public WebClient(LogChannelInterface log) {
		webEncoding = MDProps.getProperty(SettingsTags.TAG_WEB_ENCODING, "ISO-8859-1");
		this.log = log;
	}

	/**
	 * Called to do an HTTP request to a given URL and retrieve the response. If a request message is given then a POST
	 * will be performed. If not then a GET will be performed.
	 * <p>
	 * Operations is performed in a thread pool so that we can handle potential hang situations
	 *
	 * @param url
	 * @param request
	 * @param response
	 * @return
	 * @throws KettleException
	 */
	public int call(final URL url, final String request, final StringBuffer response, final int attempts) throws KettleException {
		String timeoutMessage;
		//	int myRetries = retries;
		int statusCode;

		try {
			if (log != null && log.isDetailed())
				log.logDetailed(MDPropertyWebService.getLogString("ConnectingToURL", url.toString(), "" + attempts));

			// Perform the call within a thread pool so that we can handle
			// all manners of timeouts that the http client cannot
			FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
				public Integer call() throws Exception {
					return callInThreadPool(url, request, response, attempts);
				}
			});

			threadPool.execute(task);

			// This call will not return until it has a result or it times out
			if (timeout > 0) {
				int myTime = timeout + getTimeIncrease(attempts);
				// Extend the length of the timeout by 2 seconds on each
				// 

				// We make this timeout slightly longer so as to allow the http
				// client timeouts to trigger first
				statusCode = task.get((myTime + (long) (myTime * 0.1) + 1), TimeUnit.SECONDS);
			} else {
				statusCode = task.get();
			}

			return statusCode;
		} catch (TimeoutException e) {
			// Thread timed out
			timeoutMessage = e.toString();

			// Can't tell at this point whether it timed out during connection
			// or read. Let's just assume reading.
			statusCode = NETWORK_READ_TIMEOUT;
		} catch (Throwable t) {
			// Get exception from thread process
			if (t instanceof ExecutionException)
				t = t.getCause();

			// timed out during connection attempt
			if (t instanceof ConnectTimeoutException) {
				timeoutMessage = t.toString();

				statusCode = NETWORK_CONNECT_TIMEOUT;
			}

			// timed out during HTTP read/writes
			else if (t instanceof SocketTimeoutException) {
				timeoutMessage = t.toString();

				statusCode = NETWORK_READ_TIMEOUT;
			} else {
				// Catchall
				timeoutMessage = t.toString();
				statusCode = 400;
			}
		}

		// Log the timeout message and fall thru to the retry check
		if (log != null && log.isError())
			log.logError(MDPropertyWebService.getErrorString("TimeoutDetected", timeoutMessage, url.toString()));

		// We only get here if there were timeouts ;
		MDPropertyWebService.setErrorMessage(timeoutMessage);
		return statusCode;
	}

	public int checkProxy(final URL url, final String request, final StringBuffer response) {

		int statCode = 0;
		proxyCheck = true;
		try {
			statCode = call(url, request, response, 0);
		} catch (KettleException e) {
			MDPropertyWebServiceData.webInitMsg = e.getMessage();
		}
		proxyCheck = false;
		return statCode;
	}

	public void downloadFile(URL link, String file) throws KettleException {
		if (log != null && log.isBasic())
			log.logBasic("Downloading file: " + file + " from" + link);

		// Open the output file
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)));

			// Do the call
			StringBuffer response = new StringBuffer();
			int statusCode = call(link, null, response, 0);
			if (statusCode != 200)
				throw new KettleException(MDPropertyWebService.getErrorString("StatusCodeNot200", "" + statusCode));

			// Write the response to the file
			writer.append(response);
		} catch (IOException e) {
			throw new KettleException(MDPropertyWebService.getErrorString("BadReportFile", file, e.getMessage()));
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException ignored) {
				}
		}
	}

	/**
	 * Called to close the web interface
	 */
	public void close() {

		// Currently does nothing
	}

	/**
	 * @param host Name of host to use for proxy (null if none)
	 * @param port Number of the port on that host
	 */
	public void setProxy(String host, int port, String user, String pass) {

		proxyHost = host;
		proxyPort = port;
		proxyUser = user;
		proxyPass = pass;
	}

	/**
	 * @param retries Number of times to attempt to retry a request
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}

	/**
	 * @param timeout Timeout value (<= 0 means disabled)
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * This method does the actual HTTP call. It is meant to be called from within the thread pool.
	 *
	 * @param url
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	private int callInThreadPool(URL url, String request, StringBuffer response, int attempts) throws HttpException, IOException {

		int statusCode = 0;
		// Prepare HTTP client
		HttpClient client = new HttpClient();
		int myTime = timeout;

		if (proxyCheck) {
			myTime = 5;
		} else {
			myTime = myTime + getTimeIncrease(attempts);
		}

		if (myTime > 0) {
			HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
			params.setConnectionTimeout(myTime * 1000);
			params.setSoTimeout(myTime * 1000);
		}

		// If there was a request string then prepare a POST, otherwise perpare a GET
		HttpMethod method;
		if (request != null) {
			// Prepare HTTP POST
			method = new PostMethod(url.toString());
			((PostMethod) method).setRequestEntity(new StringRequestEntity(request, "application/xml", "UTF-8"));//text
		} else {
			// Prepare HTTP GET
			method = new GetMethod(url.toString());
		}

		// Prepare execution configuration
		HostConfiguration hostConfiguration = new HostConfiguration();
		if (!Const.isEmpty(proxyHost)) {
			hostConfiguration.setProxy(proxyHost, proxyPort);
			client.getParams().setAuthenticationPreemptive(true);
			client.getState().setProxyCredentials(new AuthScope(proxyHost, proxyPort, null), new UsernamePasswordCredentials(proxyUser, proxyPass));
		}

		// Execute request
		InputStreamReader inputStreamReader = null;
		try {
			// Execute the POST method
			statusCode = client.executeMethod(hostConfiguration, method);

			if (log != null && log.isDebug())
				log.logDebug("REQUEST: " + request);

			// Display status code
			if (log != null && log.isDebug())
				log.logDebug(MDPropertyWebService.getLogString("ResponseCode", String.valueOf(statusCode)));
			if (statusCode != -1) {
				if (Const.isEmpty(webEncoding)) {
					inputStreamReader = new InputStreamReader(method.getResponseBodyAsStream());
					if ((log != null) && log.isDebug()) {
						log.logDebug(MDPropertyWebService.getLogString("Encoding", inputStreamReader.getEncoding()));
					}
				} else {
					inputStreamReader = new InputStreamReader(method.getResponseBodyAsStream(), webEncoding);
					if ((log != null) && log.isDebug()) {
						log.logDebug(MDPropertyWebService.getLogString("Encoding", inputStreamReader.getEncoding()));
					}
				}
				// Read in the response
				int c;
				while ((c = inputStreamReader.read()) != -1) {
					response.append((char) c);
				}
				// Display the response
				if ((log != null) && log.isDebug()) {
					log.logDebug(MDPropertyWebService.getLogString("ResponseBody", response.toString()));
				}
			}

			return statusCode;
		} catch (IOException ioe) {
			log.logDebug(ioe.toString());
			statusCode = 404; //
			throw ioe;
		} finally {
			if (inputStreamReader != null)
				try {
					inputStreamReader.close();
				} catch (IOException ignored) {
				}

			// Release current connection to the connection pool once you are done
			method.releaseConnection();
		}
	}

	/**
	 * Gets the amount of time we want
	 * to add to the timeout.
	 *
	 * @param attempts
	 * @return amount to increase timeout
	 */
	private int getTimeIncrease(int attempts) {

		int increase = 0;
		switch (attempts) {
			case 0:
				increase = 0;
				break;
			case 1:
				increase = 2;
				break;
			case 2:
				increase = 4;
				break;
			case 3:
				increase = 8;
				break;
			case 4:
				increase = 16;
				break;
			default:
				increase = 32;
				break;
		}
		return increase;
	}
}
