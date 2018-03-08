package com.melissadata.kettle.MDSettings;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

public class WebClient {

	private static       Class<?>        PKG                     = WebClient.class;
	private static       ExecutorService threadPool              = Executors.newCachedThreadPool();
	/*
	 * 598 (Informal convention) network read timeout error
	 * This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network read timeout
	 * behind the proxy to a client in front of the proxy.
	 */
	private static final int             NETWORK_READ_TIMEOUT    = 598;
	/*
	 * 599 (Informal convention) network connect timeout error
	 * This status code is not specified in any RFCs, but is used by some HTTP proxies to signal a network connect timeout
	 * behind the proxy to a client in front of the proxy.
	 */
	private static final int             NETWORK_CONNECT_TIMEOUT = 599;
	// private LogChannelInterface log;
	private              int             timeout                 = 0;
	private              int             retries                 = 0;
	private              String          proxyHost               = null;
	private              int             proxyPort               = 0;
	private              String          proxyUser               = null;
	private              String          proxyPass               = null;
	private              boolean         proxyCheck              = false;

	public WebClient() {
		// this.log = log;
	}

	/**
	 * Called to do an HTTP request to a given URL and retrieve the response. If a request message is given then a POST will
	 * be performed. If not then a GET will be performed.
	 * <p>
	 * Operations is performed in a thread pool so that we can handle potential hang situations
	 *
	 * @param url
	 * @param request
	 * @param response
	 * @return
	 * @throws KettleException
	 */
	public int call(final URL url, final String request, final StringBuffer response) throws KettleException {
		//FIXME How many time to retry for testing
		int myRetries = 0;//retries;
		int statusCode;
		do {
			try {
				// Perform the call within a thread pool so that we can handle
				// all manners of timeouts that the http client cannot
				FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {

						return callInThreadPool(url, request, response);
					}
				});
				threadPool.execute(task);
				// This call will not return until it has a result or it times out
				if (timeout > 0) {
					// We make this timeout slightly longer so as to allow the http client timeouts to trigger first
					statusCode = task.get((timeout + (long) (timeout * 0.1) + 1), TimeUnit.SECONDS);
				} else {
					statusCode = task.get();
				}
				return statusCode;
			} catch (TimeoutException e) {
				// Can't tell at this point whether it timed out during connection or read. Let's just assume reading.
				statusCode = NETWORK_READ_TIMEOUT;
			} catch (Throwable t) {
				// Get exception from thread process
				if (t instanceof ExecutionException) {
					t = t.getCause();
				}
				// timed out during connection attempt
				if (t instanceof ConnectTimeoutException) {
					getErrorString("ConnectTimeout");
					statusCode = NETWORK_CONNECT_TIMEOUT;
				}
				// timed out during HTTP read/writes
				else if (t instanceof SocketTimeoutException) {
					getErrorString("SocketTimeout", Integer.toString(timeout));
					statusCode = NETWORK_READ_TIMEOUT;
				} else {
					t.toString();
					System.out.println(" ERROR --- " + t.toString());
					statusCode = 333; // TODO better status code
				}
			}
			// Retry?
		} while ((myRetries-- > 0) && !proxyCheck);
		return statusCode;
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
	private int callInThreadPool(URL url, String request, StringBuffer response) throws HttpException, IOException {

		int     statusCode = 0;
		boolean doBusCoder = url.toString().contains("doBusinessCoder");
		// Prepare HTTP client
		HttpClient client = new HttpClient();
		int        myTime = timeout;
		if (proxyCheck) {
			myTime = 5;
		}

		if (myTime > 0) {
			HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
			params.setConnectionTimeout(myTime * 1000);
			params.setSoTimeout(myTime * 1000);
		}
		// If there was a request string then prepare a POST, otherwise perpare a GET
		HttpMethod method = null;
		if (request != null) {
			if (doBusCoder) {

				method = new PostMethod(url.toString() + request);
			} else {
				method = new PostMethod(url.toString());
				((PostMethod) method).setRequestEntity(new StringRequestEntity(request, "application/xml", "UTF-8"));
			}
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
			// Display status code
			if (statusCode != -1) {
				// Try to determine encoding from the content type value
				String encoding    = null;
				String contentType = method.getResponseHeader("Content-Type").getValue();
				if ((contentType != null) && contentType.contains("charset")) {
					encoding = contentType.replaceFirst("^.*;\\s*charset\\s*=\\s*", "").replace("\"", "").trim();
				}
				// Get the response, but only specify encoding if we've got one
				// otherwise the default charset ISO-8859-1 is used by HttpClient
				if (Const.isEmpty(encoding)) {
					inputStreamReader = new InputStreamReader(method.getResponseBodyAsStream());
				} else {
					inputStreamReader = new InputStreamReader(method.getResponseBodyAsStream());
				}
				// Read in the response
				int c;
				while ((c = inputStreamReader.read()) != -1) {
					response.append((char) c);
				}
			}
			return statusCode;
		} catch (IOException ioe) {
			statusCode = 404;
			throw ioe;
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException ignored) {
				}
			}
			// Release current connection to the connection pool once you are done
			method.releaseConnection();
		}
	}

	public int checkProxy(final URL url, final String request, final StringBuffer response) {

		int statCode = 0;
		proxyCheck = true;
		try {
			statCode = call(url, request, response);
		} catch (KettleException e) {
			MDSettingsData.proxyMsg = e.getMessage();
		}
		proxyCheck = false;
		return statCode;
	}

	/**
	 * Called to close the web interface
	 */
	public void close() {
		// Currently does nothing
	}

	/**
	 * Called to download one file from the given URL link
	 *
	 * @param link
	 * @param file
	 * @throws KettleException
	 */
	public void downloadFile(URL link, String file) throws KettleException {
		// Open the output file
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)));
			// Do the call
			StringBuffer response   = new StringBuffer();
			int          statusCode = call(link, null, response);
			if (statusCode != 200) {
				throw new KettleException("StatusCodeNot200");
			}
			// Write the response to the file
			writer.append(response);
		} catch (IOException e) {
			throw new KettleException("BadReportFile");
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	private String getErrorString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDCheck.Error." + key, args);
	}

	public String getProxyHost() {

		return proxyHost;
	}

	public String getProxyPass() {

		return proxyPass;
	}

	public int getProxyPort() {

		return proxyPort;
	}

	public String getProxyUser() {

		return proxyUser;
	}

	public int getRetries() {

		return retries;
	}

	public int getTimeout() {

		return timeout;
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

	public void setProxyHost(String proxyHost) {

		this.proxyHost = proxyHost;
	}

	public void setProxyPass(String proxyPass) {

		this.proxyPass = proxyPass;
	}

	public void setProxyPort(int proxyPort) {

		this.proxyPort = proxyPort;
	}

	public void setProxyUser(String proxyUser) {

		this.proxyUser = proxyUser;
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
}
