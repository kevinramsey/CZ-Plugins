package com.melissadata.kettle.iplocator;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.support.MDAbortException;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * Created by Kevin on 5/1/2017.
 */
public class MDIPLocatorWebService extends MDCheckService {
	private static Class<?> PKG = MDCheckWebService.class;
	private static String    errorMessage;
	private        boolean   processUsingWebService;
	private        boolean   processTryFailover;
	private        boolean   processRetryPrimary;
	private        long      failoverRetryTime;
	private        long      failoverInterval;
	private        WebClient webClient;
	private        WebClient cvsClient;
	public MDIPLocatorWebService(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {
		super(stepData, checkData, space, log);
	}

	/**
	 * Given a list of general result codes, returns descriptions of those results codes
	 *
	 * @param resultCodes
	 * @return
	 */
	public static String getGeneralResultCodeMessages(Set<String> resultCodes) {
		return getResultCodeMessages("General", resultCodes);
	}

	/**
	 * Given a list of result codes, returns descriptions of those result codes
	 *
	 * @param prefix
	 * @param resultCodes
	 * @return
	 */
	public static String getResultCodeMessages(String prefix, Set<String> resultCodes) {
		StringBuffer message = new StringBuffer();
		String sep = "";
		for (String resultCode : resultCodes) {
			String description = BaseMessages.getString(PKG, "MDCheck.ResultCode." + prefix + "." + resultCode);
			if ((description == null) || description.startsWith("!")) {
				description = BaseMessages.getString(PKG, "MDCheck.ResultCode.Undefined");
			}
			message.append(sep).append(resultCode).append("=").append(description);
			sep = "; ";
		}
		return message.toString();
	}

	public static void setErrorMessage(String msg) {
		errorMessage = msg;
	}

	@Override public boolean checkProxy() {
		StringBuffer xmlResponse = new StringBuffer(); // this is just a place holder
		String prMsg = "";
		int statCode = 0;
		if ((stepData.getAdvancedConfiguration().getServiceType() == AdvancedConfigurationMeta.ServiceType.Web) || stepData.getAdvancedConfiguration().isCVSFailover()) {

			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
				statCode = getWebClient().checkProxy(checkData.realWebIPLocatorURL, "", xmlResponse);
			}
		} else {
			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
				statCode = getWebClient().checkProxy(checkData.realCVSIPLocatorURL, "", xmlResponse);
			}
		}
		if (statCode != 200) {
			switch (statCode) {
				case 599:
					prMsg = statCode + ": Could not locate proxy";
					break;
				case 407:
					prMsg = statCode + ": Could not authenticate with proxy";
					break;
				case 3:
					prMsg = statCode + ": Access denied to proxy";
					break;
				case 0:
					prMsg = MDCheckStepData.webInitMsg;
					break;
				default:
					prMsg = statCode + ": Could not connect to proxy";
					break;
			}
			if (Const.isEmpty(checkData.realProxyHost)) {
				prMsg = statCode + ": Could not connect to service ";
			}
			MDCheckStepData.webInitMsg = prMsg;
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#dispose()
	 */
	@Override public void dispose() {
		// Close the web clients (if there are any)
		if (webClient != null) {
			webClient.close();
		}
		if (cvsClient != null) {
			cvsClient.close();
		}
		// Call parent handler
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#init()
	 */
	@Override public void init() throws KettleException {
		// Call parent handler first
		boolean isCommunity = false;
		String name = "";

		if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject) && AdvancedConfigurationMeta.isCommunity()) {
				isCommunity = true;
				name = "IP Locator";
			}
		}

		if (isCommunity) {
			String msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", name);
			log.logBasic(msg);
			MDCheck.showSleepMsg(msg, stepData.getCheckTypes());
			return;
		}

		super.init();

		try {
			// Get advanced configuration
			AdvancedConfigurationMeta acMeta = stepData.getAdvancedConfiguration();
			// get maximum requests per batch
			checkData.maxRequests = acMeta.getMaxRequests();
			// Get web service settings if configured or local appliance failover is configured
			if ((acMeta.getServiceType() == AdvancedConfigurationMeta.ServiceType.Web) || acMeta.isCVSFailover()) {
				// Get service URLs
				if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
					checkData.realWebIPLocatorURL = new URL(space.environmentSubstitute(acMeta.getIPLocatorURL()));
				}
				// get timeout settings
				checkData.realWebTimeout = Integer.valueOf(space.environmentSubstitute(acMeta.getWebTimeout()));
				checkData.realWebRetries = Integer.valueOf(space.environmentSubstitute(acMeta.getWebRetries()));
				// get abort flag
				checkData.webAbortOnError = acMeta.isWebAbortOnError();
				// get proxy settings
				checkData.realProxyHost = space.environmentSubstitute(acMeta.getWebProxyHost());
				checkData.realProxyPort = Const.toInt(space.environmentSubstitute(acMeta.getWebProxyPort()), 8080);
				checkData.realProxyUser = acMeta.getWebProxyUser();
				checkData.realProxyPass = acMeta.getWebProxyPass();
			}
			// Get local appliance settings (if configured)
			if ((acMeta.getServiceType() == AdvancedConfigurationMeta.ServiceType.CVS)) {
				// Get the real CVS URLs
				URL url = new URL(space.environmentSubstitute(acMeta.getCVSServerURL()));
				checkData.realCVSIPLocatorURL = buildCVSURL(url, "iplocator", "doIPLocation");
				// get timeout settings
				checkData.realCVSTimeout = Integer.valueOf(space.environmentSubstitute(acMeta.getCVSTimeout()));
				checkData.realCVSRetries = Integer.valueOf(space.environmentSubstitute(acMeta.getCVSRetries()));
				// get abort flag
				checkData.cvsAbortOnError = acMeta.isCVSAbortOnError();
				// get proxy settings
				checkData.realProxyHost = space.environmentSubstitute(acMeta.getWebProxyHost());
				checkData.realProxyPort = Const.toInt(space.environmentSubstitute(acMeta.getWebProxyPort()), 8080);
				checkData.realProxyUser = acMeta.getWebProxyUser();
				checkData.realProxyPass = acMeta.getWebProxyPass();
			}
			// get processing and fail over modes based on service settings
			if ((acMeta.getServiceType() == AdvancedConfigurationMeta.ServiceType.CVS)) {
				if (acMeta.isCVSFailover()) {
					processTryFailover = true;
					if (acMeta.isRetryAppliance()) {
						processRetryPrimary = true;
						failoverInterval = acMeta.getFailoverInterval();
					}
				}
			} else {
				processUsingWebService = true;
			}
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened, return an initialization failure
			MDCheckStepData.webInitMsg = MDCheck.getErrorString("InitializeService", t.toString());
			stepData.webInitException = new KettleException(MDCheckStepData.webInitMsg, t);
			if (!testing) {
				throw stepData.webInitException;
			}
		}
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#buildRequest(com.melissadata.kettle.support.IOMeta, java.lang.Object[])
 */
	@Override public MDCheckRequest buildRequest(IOMeta ioMeta, Object[] inputData) {
		// FIXME: Use a pool of request objects?
		return new MDCheckCVRequest(ioMeta, inputData);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#processRequests()
	 */
	@Override public void processRequests(List<MDCheckRequest> requests, int queue, boolean chkXML, int attempts) throws KettleException {
		processIPLocatorRequests(requests, attempts);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#saveReports()
	 */
	@Override public void saveReports() throws KettleException {
		// No reports from web service
	}

	/**
	 * Constructs a new local appliance URL from a single server url by extracting its components and
	 * rebuilding it with the specified prefix and suffix
	 *
	 * @param url
	 * @param service
	 * @param suffix
	 * @return
	 * @throws MalformedURLException
	 */
	private URL buildCVSURL(URL url, String service, String suffix) throws MalformedURLException {// String prefix,
		String protocol = url.getProtocol();
		String host = url.getHost();
		int port = url.getPort();
		String path = url.getPath();
		String query = url.getQuery();
		host = host + "/" + service + "/xml/service.svc";
		query = (query != null) ? "?" + query : "";
		String file = path + "/" + suffix + query;
		if (port == -1) {
			return new URL(protocol, host, file);
		} else {
			return new URL(protocol, host, port, file);
		}
	}

	/**
	 * Called to determine if a result code returned by a service is potential fatal and, if it is, whether
	 * we should stop processing immediately.
	 *
	 * @param resultCodes
	 * @throws KettleException
	 */
	private void checkShowStoppingFault(String resultCodes) throws KettleException {
		// If there were any general result codes then throw an exception.
		Set<String> codes = MDCheck.getResultCodes(resultCodes);
		if (codes.size() > 0) {
			String message = MDCheckWebService.getGeneralResultCodeMessages(codes);
			if ((processUsingWebService ? checkData.webAbortOnError : checkData.cvsAbortOnError)) {
				throw new MDAbortException(message);
			}

			// The transform will either reroute the records to the log file or to
			// the error stream (if the later is defined).
			throw new KettleException(message);
		}
	}

	/**
	 * This is a generic implementation of web requests processing
	 *
	 * @param handler
	 * @param queue
	 * @throws KettleException
	 */
	private void doWebRequests(WebRequestHandler handler, List<MDCheckRequest> requests, int product, int queue, int attempts) throws KettleException {
		try {
			// Add customer id
			//String customerID = stepData.getAdvancedConfiguration().getCustomerID(product);
			String licenseStr = stepData.getAdvancedConfiguration().getProdutLicense(product);
			boolean sendRequest = false;
			String jsonRequest = null;
			if (!Const.isEmpty(licenseStr)) {

				if (product == AdvancedConfigurationMeta.MDLICENSE_IPLocator) {
					JSONObject mainObj = new JSONObject();
					mainObj.put("CustomerID", licenseStr);
					mainObj.put("TransmissionReference", "");
					sendRequest = handler.buildWebRequest(mainObj, checkData, requests);

					// Convert the document object to an XML request string
					jsonRequest = sendRequest ? mainObj.toJSONString() : null;
				}
			} else {
				throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.CustomerIDGE05"));
			}

			// If there was at least one record then we send the request and process the response
			if (jsonRequest != null) {
				// See if we are in a fail over state (using the web service instead of the local appliance)
				// and if we should retry the appliance after the specified time interval.
				if (processUsingWebService && processRetryPrimary && (System.currentTimeMillis() > failoverRetryTime)) {
					log.logBasic("Retrying local appliance");
					processUsingWebService = false;
				}
				// Post the requests and get the responses
				StringBuffer stringResponse = new StringBuffer();
				int statusCode = 0;
				if (processUsingWebService) {
					// Call web service
					statusCode = getWebClient().call(handler.getWebURL(checkData, queue), jsonRequest, stringResponse, attempts);
				} else {
					// Call local appliance
					statusCode = getCVSClient().call(handler.getCVSURL(checkData), jsonRequest, stringResponse, attempts);
					// If there was a problem then check to see if we should fail over to the web service
					if ((statusCode != 200) && processTryFailover) {
						log.logBasic("WARNING! Cannot connect to local appliance. Failing over to web service!");
						// Switch to web service
						processUsingWebService = true;
						// If retry is enabled then set the retry time
						if (processRetryPrimary) {
							failoverRetryTime = System.currentTimeMillis() + failoverInterval;
						}
						// Call web service
						statusCode = getWebClient().call(handler.getWebURL(checkData, queue), jsonRequest, stringResponse, attempts);
					}
				}
				// Check for problem
				if (statusCode != 200) {
					throw new KettleException(MDCheck.getErrorString("StatusCodeNot200", "" + statusCode) + "-" + handler.getServiceName() + "-" + errorMessage);
				}

				String resultCodes = "";
				if (product == AdvancedConfigurationMeta.MDLICENSE_IPLocator) {
					JSONObject jsonResponse = new JSONObject();
					JSONParser parser = getParser();
					jsonResponse = (JSONObject) parser.parse(stringResponse.toString());
					resultCodes = handler.processWebResponse(jsonResponse, checkData, requests);
				}
				// TODO: if result codes contains SE01 then retry request
				if (resultCodes.contains("SE01")) {
					// this is just to see where an error happens
					// sometimes we get no version# from web
					MDCheckStepData.webInitMsg = "Server Error SE01";
				}
				// Check for serious failure different
				checkShowStoppingFault(resultCodes);
			}
		} catch (KettleException e) {
			// Re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened
			// We usually get here due to malformed xml response
			// so we simplify the error message by reporting a malformed xml
			throw new KettleException(MDCheck.getErrorString("ProcessService", getErrorString("BadReply", handler.getServiceName())));
		}
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#outputData(int)
 */
	@Override public void outputData(List<MDCheckRequest> requests, int queue) {
		// Process IP Locator requests
		if (stepData.getIPLocator() != null) {
			stepData.getIPLocator().outputData(checkData, requests);
		}
	}

	private synchronized JSONParser getParser() {
		// FIXME is this enough for thread safety?retry
		return new JSONParser();
	}

	/**
	 * @return The current instance of the local appliance client
	 */
	private WebClient getCVSClient() {
		// TODO: combine with getWebClient() for a single interface
		if (cvsClient == null) {
			// Create a web client
			cvsClient = new WebClient(log);
			cvsClient.setTimeout(checkData.realCVSTimeout);
			cvsClient.setRetries(checkData.realCVSRetries);
		}
		return cvsClient;
	}

	private String getErrorString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheck.Error." + key, args);
	}

	/**
	 * @return The current instance of the web service client
	 */
	private WebClient getWebClient() {
		// TODO: combine with getCVSClient() for a single interface
		if (webClient == null) {
			// Create a web client
			webClient = new WebClient(log);
			webClient.setTimeout(checkData.realWebTimeout);
			webClient.setRetries(checkData.realWebRetries);
			webClient.setProxy(checkData.realProxyHost, checkData.realProxyPort, checkData.realProxyUser, checkData.realProxyPass);
		}
		return webClient;
	}

	private void processIPLocatorRequests(List<MDCheckRequest> requests, int attempts) throws KettleException {
		IPLocatorMeta ipMeta = stepData.getIPLocator();
		try {
			// Process only if ipLocator is enabled
			if ((ipMeta != null) && ipMeta.isEnabled()) {
				if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)) {
					log.logError("IP Locator not available in community edition.");
					return;
				}
				doWebRequests(ipMeta, requests, AdvancedConfigurationMeta.MDLICENSE_IPLocator, 0, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				ipMeta.webMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				ipMeta.webMsg = e.toString();
			}
			ipMeta.webException = e;
			if (!testing) {
				throw ipMeta.webException;
			}
		}
	}
}
