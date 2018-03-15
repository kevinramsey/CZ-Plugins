package com.melissadata.kettle.cv;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.melissadata.kettle.*;
import com.melissadata.kettle.cv.address.AddressKeyRequestHandler;
import com.melissadata.kettle.cv.address.AddressVerifyMeta;
import com.melissadata.kettle.cv.address.RBDIndicatorMeta;
import com.melissadata.kettle.cv.email.EmailVerifyMeta;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta;
import com.melissadata.kettle.cv.name.NameParseMeta;
import com.melissadata.kettle.cv.phone.PhoneVerifyMeta;
import com.melissadata.kettle.iplocator.IPLocatorMeta;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.support.MDAbortException;
import com.melissadata.kettle.report.ReportStats;
import com.melissadata.kettle.sm.SmartMoverMeta;
import org.dom4j.DocumentException;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class MDCheckWebService extends MDCheckService {
	/**
	 * @param xmlDoc
	 * @param parent
	 * @param tagName
	 * @param data
	 * @throws DOMException
	 */
	public static void addTextNode(Document xmlDoc, Element parent, String tagName, String data) throws DOMException {
// data = stripInvalidXmlCharacters(data);
		Element e = xmlDoc.createElement(tagName);
		Node n = xmlDoc.createTextNode(data);
		e.appendChild(n);
		parent.appendChild(e);
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @return
	 */
	public static org.dom4j.Element getElement(org.dom4j.Element parent, String name) {
		org.dom4j.Element element = parent.element(name);
		if (element == null) { return new DOMElement(parent.getQName(name)); }
		return element;
	}

	/**
	 * @param record
	 * @param name
	 * @return
	 * @throws KettleException
	 */
	public static int getElementInteger(org.dom4j.Element record, String name) throws KettleException {
		try {
			String text = record.elementText(name);
			if (text == null) { throw new KettleException("Could not find integer value for " + name); }
			int value = Integer.valueOf(text);
			return value;
		} catch (NumberFormatException e) {
			throw new KettleException("Problem getting integer value", e);
		}
	}

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	public static String getElementText(org.dom4j.Element element, String name) {
		String text = element.elementText(name);
		if (text == null) { return ""; }
		return text.trim();
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
	private static Class<?>		PKG	= MDCheckWebService.class;
	private        DOMImplementation   domImplentation;
	private        Transformer         serializer;
	private        SAXReader           saxReader;
	private        boolean             processUsingWebService;
	private        boolean             processTryFailover;
	private        boolean             processRetryPrimary;
	private        long                failoverRetryTime;
	private        long                failoverInterval;
	private        WebClient           webClient;
	private        WebClient           cvsClient;
	private        MDCheckLocalService localNameService;
	private static String              errorMessage;
	private        boolean             doName;

	public MDCheckWebService(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {
		super(stepData, checkData, space, log);
		localNameService = new MDCheckLocalService(stepData, checkData, space, log);
	}

	@Override
	public boolean checkProxy() {
		StringBuffer xmlResponse = new StringBuffer(); // this is just a place holder
		String prMsg = "";
		int statCode = 0;
		if ((stepData.getAdvancedConfiguration().getServiceType() == ServiceType.Web) || stepData.getAdvancedConfiguration().isCVSFailover()) {
			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_ADDRESS) != 0) {
				statCode = getWebClient().checkProxy(checkData.realWebAddressVerifierURL, "", xmlResponse);
			}
			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
				statCode = getWebClient().checkProxy(checkData.realWebIPLocatorURL, "", xmlResponse);
			}
			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
				statCode = getWebClient().checkProxy(checkData.realNCOAURL, "", xmlResponse);
			}
		} else {
			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_ADDRESS) != 0) {
				statCode = getWebClient().checkProxy(checkData.realCVSAddressVerifierURL, "", xmlResponse);
			}
			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
				statCode = getWebClient().checkProxy(checkData.realCVSIPLocatorURL, "", xmlResponse);
				// No CVS for SmartMover
				// if ((stepData.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0)
				// statCode = getWebClient().checkProxy(checkData.realNCOAURL, "", xmlResponse);
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
	@Override
	public void dispose() {
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
	@Override
	public void init() throws KettleException {
		// Call parent handler first
		boolean isCommunity = false;
		String name = "";

		if((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0){
			if( !AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)
					&& AdvancedConfigurationMeta.isCommunity()){
				isCommunity = true;
				name = "IP Locator";
			}

		} else if((stepData.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0){
			if( !AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject)
					&& AdvancedConfigurationMeta.isCommunity()){
				isCommunity = true;
				name = "Contact Verify Web Services";
			}
		}


		if(isCommunity){
			String msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", name);
			log.logBasic(msg);
			MDCheck.showSleepMsg(msg, stepData.getCheckTypes());
			return;
		}

		super.init();

		try {
			initReporting();
			// Create DOM object to build requests
			domImplentation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
			// Create Serializer to use to translate DOM into XML string
			serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
			// Create SAX reader to use when parsing responses
			saxReader = new SAXReader();
			// Get advanced configuration
			AdvancedConfigurationMeta acMeta = stepData.getAdvancedConfiguration();
			// get maximum requests per batch
			checkData.maxRequests = acMeta.getMaxRequests();
			// Get web service settings if configured or local appliance failover is configured
			if ((acMeta.getServiceType() == ServiceType.Web) || acMeta.isCVSFailover()) {
				// Get service URLs
				if ((stepData.checkTypes & MDCheckMeta.MDCHECK_NAME) != 0) {
					// dont bother if we are not doing name parse
					if (!Const.isEmpty(stepData.getNameParse().getFullName()) || !Const.isEmpty(stepData.getNameParse().getInputCompanyName())) {
						try {
							localNameService.initLicense();
							localNameService.initName();
							doName = true;
						} catch (KettleException e) {
							log.logError("Name Parse - " + stepData.getNameParse().localMsg);
							doName = false;
						}
					} else {
						doName = false;
					}
					// checkData.realWebNameParserURL = new URL(space.environmentSubstitute(acMeta.getWebNameParserURL()));
				}
				if ((stepData.checkTypes & MDCheckMeta.MDCHECK_ADDRESS) != 0) {
					checkData.realWebAddressVerifierURL = new URL(space.environmentSubstitute(acMeta.getWebAddressVerifierURL()));
					checkData.realWebGeoCoderURL = new URL(space.environmentSubstitute(acMeta.getWebGeoCoderURL()));
					checkData.realWebRBDIndicatorURL = new URL(space.environmentSubstitute(acMeta.getRBDIndicatorURL()));
				}
				if ((stepData.checkTypes & MDCheckMeta.MDCHECK_PHONE) != 0) {
					checkData.realWebPhoneVerifierURL = new URL(space.environmentSubstitute(acMeta.getWebPhoneVerifierURL()));
				}
				if ((stepData.checkTypes & MDCheckMeta.MDCHECK_EMAIL) != 0) {
					checkData.realWebEmailVerifierURL = new URL(space.environmentSubstitute(acMeta.getWebEmailVerifierURL()));
				}
				if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
					checkData.realWebIPLocatorURL = new URL(space.environmentSubstitute(acMeta.getIPLocatorURL()));
				}
				if ((stepData.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
					checkData.realNCOAURL = new URL(space.environmentSubstitute(acMeta.getWebNCOAURL()));
					checkData.realCCOAURL = new URL(space.environmentSubstitute(acMeta.getWebCCOAURL()));
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
			if ((acMeta.getServiceType() == ServiceType.CVS)) {
				// Get the real CVS URLs
				if (!Const.isEmpty(stepData.getNameParse().getFullName()) || !Const.isEmpty(stepData.getNameParse().getInputCompanyName())) {
					try {
						localNameService.initLicense();
						localNameService.initName();
						doName = true;
					} catch (KettleException e) {
						log.logError("Name Parse - " + stepData.getNameParse().localMsg);
						doName = false;
					}
				} else {
					doName = false;
				}
				URL url = new URL(space.environmentSubstitute(acMeta.getCVSServerURL()));
				// checkData.realCVSNameParserURL = buildCVSURL(url, "name", "doNameCheck");
				checkData.realCVSAddressVerifierURL = buildCVSURL(url, "addresscheck", "doAddressCheck");
				checkData.realCVSGeoCoderURL = buildCVSURL(url, "geocoder", "doGeoCode");
				checkData.realCVSRBDIndicatorURL = buildCVSURL(url, "rbdi", "doRBDI");
				checkData.realCVSPhoneVerifierURL = buildCVSURL(url, "phonecheck", "doPhoneCheck");
				checkData.realCVSEmailVerifierURL = buildCVSURL(url, "email", "doEmailCheck");
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
			if ((acMeta.getServiceType() == ServiceType.CVS)) {
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
			if (!testing) { throw stepData.webInitException; }
		}
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#buildRequest(com.melissadata.kettle.support.IOMeta, java.lang.Object[])
 */
	@Override
	public MDCheckRequest buildRequest(IOMeta ioMeta, Object[] inputData) {
		// FIXME: Use a pool of request objects?
		return new MDCheckCVRequest(ioMeta, inputData);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#processRequests()
	 */
	@Override
	public void processRequests(List<MDCheckRequest> requests, int queue, boolean chkXML, int attempts) throws KettleException {
		// Process the name parser requests
		if (doName) {
			processNameRequests(requests);
		}
		// Process the address verifier requests
		processAddrRequests(requests, attempts);
		// Process the geo coder requests
		processGeoRequests(requests, attempts);
		// Process RBDI if option selected
		if (stepData.getAddressVerify() != null) {
			if ((stepData.getAddressVerify().getOptionPerformRBDI() && (stepData.getAddressVerify().getOptionCountries() != AddressVerifyMeta.Countries.Canada)) || testing) {
				processRBDIndicatorRequests(requests, attempts);
			}
		}
		// Process the phone verifier requests
		processPhoneRequests(requests, attempts);
		// Process the email verifier requests
		processEmailRequests(requests, attempts);
		processIPLocatorRequests(requests, attempts);
		processSmartMoverRequests(requests, queue, attempts);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#saveReports()
	 */
	@Override
	public void saveReports() throws KettleException {
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
			// If not testing then check to see if we should abort the transform completely
			if (!testing) {
				if ((processUsingWebService ? checkData.webAbortOnError : checkData.cvsAbortOnError)) { throw new MDAbortException(message); }
			}
			// The transform will either reroute the records to the log file or to
			// the error stream (if the later is defined).
			throw new KettleException(message);
		}
	}

	/**
	 * Called to convert a DOM object to XML in a thread safe manner
	 *
	 * @param requestDoc
	 * @return
	 * @throws TransformerException
	 */
	private synchronized String convertToXML(Document requestDoc) throws TransformerException {
		// Convert to XML
		StringWriter sw = new StringWriter();
		DOMSource domSource = new DOMSource(requestDoc);
		if (serializer == null) {
			// Create Serializer to use to translate DOM into XML string
			serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
		}
		serializer.transform(domSource, new StreamResult(sw));
		String xmlRequest = sw.toString();
		return xmlRequest;
	}

	/**
	 * Creates a new document in a thread save manner
	 *
	 * @return
	 */
	synchronized private Document createDocument(boolean isSmartMover) {
		if (isSmartMover) {
			return domImplentation.createDocument(null, "Request", null);
		} else {
			return domImplentation.createDocument(null, "RequestArray", null);
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
			
			
			// Create the request document
			Document requestDoc = null;
			if(!(product == AdvancedConfigurationMeta.MDLICENSE_IPLocator)){
				if (product == AdvancedConfigurationMeta.MDLICENSE_SmartMover) {
					requestDoc = createDocument(true);
				} else {
					requestDoc = createDocument(false);
				}
			}

			
			// Add customer id
			String customerID = stepData.getAdvancedConfiguration().getCustomerID(product);
			String transmissionRef = stepData.getAdvancedConfiguration().getTransmissionReference();
			boolean sendRequest = false;
			String xmlRequest = null;
			if (!Const.isEmpty(customerID)) {

				if(product == AdvancedConfigurationMeta.MDLICENSE_IPLocator){
					JSONObject mainObj = new JSONObject();
					mainObj.put("CustomerID", customerID);
					mainObj.put("TransmissionReference", transmissionRef);
					sendRequest = handler.buildWebRequest(mainObj, checkData, requests);

					// Convert the document object to an XML request string
					xmlRequest = sendRequest ? mainObj.toJSONString() : null;
					
				} else {
					//TrasmissionReference
					addTextNode(requestDoc, requestDoc.getDocumentElement(), "CustomerID", "" + customerID);
					addTextNode(requestDoc, requestDoc.getDocumentElement(), "TrasmissionReference", "" + transmissionRef);
					// Build the request document from the request data
					sendRequest = handler.buildWebRequest(requestDoc, checkData, requests, testing);
					// Convert the document object to an XML request string
					xmlRequest = sendRequest ? convertToXML(requestDoc) : null;
					
					
				}
			} else {
				throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.CustomerIDGE05"));
			}

			// If there was at least one record then we send the request and process the response
			if (xmlRequest != null) {
				// See if we are in a fail over state (using the web service instead of the local appliance)
				// and if we should retry the appliance after the specified time interval.
				if (processUsingWebService && processRetryPrimary && (System.currentTimeMillis() > failoverRetryTime)) {
					log.logBasic("Retrying local appliance");
					processUsingWebService = false;
				}
				// Post the requests and get the responses
				StringBuffer xmlResponse = new StringBuffer();
				int statusCode = 0;
				if (processUsingWebService) {
					// Call web service
					statusCode = getWebClient().call(handler.getWebURL(checkData, queue), xmlRequest, xmlResponse, attempts);
				} else {
					// Call local appliance
					statusCode = getCVSClient().call(handler.getCVSURL(checkData), xmlRequest, xmlResponse, attempts);
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
						statusCode = getWebClient().call(handler.getWebURL(checkData, queue), xmlRequest, xmlResponse, attempts);
					}
				}
				// Check for problem
				if (statusCode != 200) { throw new KettleException(MDCheck.getErrorString("StatusCodeNot200", "" + statusCode) + "-" + handler.getServiceName() + "-" + errorMessage); }
				
				String resultCodes = "";
				if(product == AdvancedConfigurationMeta.MDLICENSE_IPLocator){
					JSONObject jsonResponse = new JSONObject();
					JSONParser parser = getParser();
					jsonResponse = (JSONObject) parser.parse(xmlResponse.toString());
					resultCodes = handler.processWebResponse(jsonResponse, checkData, requests);

				} else {
					// Process responses and extract results into request object
					org.dom4j.Document doc = readXML(xmlResponse);
					resultCodes = handler.processWebResponse(doc, checkData, requests, testing);
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
	@Override
	public void outputData(List<MDCheckRequest> requests, int queue) {
		// All the testing for the local service takes place in the initialization phase
		if (testing) { return; }
		// Process the name parser requests
		if (stepData.getNameParse() != null) {
			stepData.getNameParse().outputData(checkData, requests);
		}
		// Process the address verifier requests
		if (stepData.getAddressVerify() != null) {
			stepData.getAddressVerify().outputData(checkData, requests);
		}
		// Process the geo coder requests
		if (stepData.getGeoCoder() != null) {
			stepData.getGeoCoder().outputData(checkData, requests);
		}
		// Process the phone verifier requests
		if (stepData.getPhoneVerify() != null) {
			stepData.getPhoneVerify().outputData(checkData, requests);
		}
		// Process the email verifier requests
		if (stepData.getEmailVerify() != null) {
			stepData.getEmailVerify().outputData(checkData, requests);
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

	private void initReporting() {
		//
		// init if needed
		//
		MDCheckMeta checkMeta = stepData.getMeta();
		if (checkData.numAddrValidations == null) {
			checkData.numAddrValidations = ReportStats.create(checkMeta.addrValidationReportCStat);
		}
		if (checkData.numAddrChanges == null) {
			checkData.numAddrChanges = ReportStats.create(checkMeta.addrChangeReportCStat);
		}
		if (checkData.numAddrErrors == null) {
			checkData.numAddrErrors = ReportStats.create(checkMeta.errorReportCStat);
		}
		if (checkData.numAddrOverview == null) {
			checkData.numAddrOverview = ReportStats.create(checkMeta.addrOverviewReportCStat);
		}
		if (checkData.numEmailOverview == null) {
			checkData.numEmailOverview = ReportStats.create(checkMeta.emailOverviewReportCStat);
		}
		if (checkData.numPhoneOverview == null) {
			checkData.numPhoneOverview = ReportStats.create(checkMeta.phoneOverviewReportCStat);
		}
		if (checkData.numGeoOverview == null) {
			checkData.numGeoOverview = ReportStats.create(checkMeta.geoOverviewReportCStat);
		}
		if (checkData.numNameOverview == null) {
			checkData.numNameOverview = ReportStats.create(checkMeta.nameOverviewFields);
		}
		if (checkData.resultStats == null) {
			checkData.resultStats = new ReportStats();
		}
	}

	/**
	 * Called to process address requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processAddrRequests(List<MDCheckRequest> requests, int attempts) throws KettleException {
		AddressVerifyMeta addressVerify = stepData.getAddressVerify();
		try {
			// process only if address verification is enabled
			if ((addressVerify != null) && addressVerify.isEnabled()) {
				doWebRequests(addressVerify.getWebRequestHandler(), requests, AdvancedConfigurationMeta.MDLICENSE_Address, 0, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				addressVerify.webMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				addressVerify.webMsg = e.toString();
			}
			addressVerify.webException = e;
			if (!testing) { throw addressVerify.webException; }
		}
	}

	/**
	 * Called to process email requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processEmailRequests(List<MDCheckRequest> requests, int attempts) throws KettleException {
		EmailVerifyMeta emailVerify = stepData.getEmailVerify();
		try {
			// Process only if email verifier is enabled
			if ((emailVerify != null) && emailVerify.isEnabled()) {
				doWebRequests(emailVerify, requests, AdvancedConfigurationMeta.MDLICENSE_Email, 0, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				emailVerify.webMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				emailVerify.webMsg = e.toString();
			}
			emailVerify.webException = e;
			if (!testing) { throw emailVerify.webException; }
		}
	}

	/**
	 * Called to process geocoder requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processGeoRequests(List<MDCheckRequest> requests, int attempts) throws KettleException {
		GeoCoderMeta geoCoder = stepData.getGeoCoder();
		try {
			// Process only if geo coder is enabled
			if ((geoCoder != null) && geoCoder.isEnabled()) {
				// If we need to dip into the address verifier for the address key then do so now
				AddressKeyRequestHandler addrKeyHandler = geoCoder.getWebRequestForAddrKeyHandler();
				if (addrKeyHandler != null) {
					doWebRequests(addrKeyHandler, requests, AdvancedConfigurationMeta.MDLICENSE_Address, 0, attempts);
				}
				// Do the regular geo coder request
				doWebRequests(geoCoder, requests, AdvancedConfigurationMeta.MDLICENSE_GeoCode, 0, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				geoCoder.webMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				geoCoder.webMsg = e.toString();
			}
			geoCoder.webException = e;
			if (!testing) { throw geoCoder.webException; }
		}
	}

	private void processIPLocatorRequests(List<MDCheckRequest> requests, int attempts) throws KettleException {
		IPLocatorMeta ipMeta = stepData.getIPLocator();
		try {
			// Process only if ipLocator is enabled
			if ((ipMeta != null) && ipMeta.isEnabled()) {
				if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)){
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
			if (!testing) { throw ipMeta.webException; }
		}
	}

	/**
	 * Called to process name requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processNameRequests(List<MDCheckRequest> requests) throws KettleException {
		NameParseMeta nameParse = stepData.getNameParse();
		try {
			// Process only if name parsing is enabled
			/*
			 * if (nameParse != null) {
			 * doWebRequests(nameParse, requests);
			 * }
			 */
			// local only for now
			if (nameParse != null) {
				if (Const.isEmpty(nameParse.initializeError)) {
					localNameService.processNameRequests(requests);
				} else {
					log.logBasic(nameParse.initializeError);
				}
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				nameParse.webMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				nameParse.webMsg = e.toString();
			}
			nameParse.webException = e;
			if (!testing) { throw nameParse.webException; }
		}
	}

	/**
	 * Called to process phone requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processPhoneRequests(List<MDCheckRequest> requests, int attempts) throws KettleException {
		PhoneVerifyMeta phoneVerify = stepData.getPhoneVerify();
		try {
			// Process only if phone verifier is enabled
			if ((phoneVerify != null) && phoneVerify.isEnabled()) {
				doWebRequests(phoneVerify, requests, AdvancedConfigurationMeta.MDLICENSE_Phone, 0, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				phoneVerify.webMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				phoneVerify.webMsg = e.toString();
			}
			phoneVerify.webException = e;
			if (!testing) { throw phoneVerify.webException; }
		}
	}

	// private static String stripInvalidXmlCharacters(String input) {
// StringBuilder sb = new StringBuilder();
// for (int i = 0; i < input.length(); i++) {
// char c = input.charAt(i);
// if ((c != '/' && c != 0x005C && c != ':' && c != ';' && c != ',')
// && (c < 0x00FD && c > 0x001F) || c == '\t' || c == '\n' || c == '\r' ) {
// sb.append(c);
// }
// else if((i > 0 || i == (input.length() - 1)) && input.charAt(i - 1) != ' ' && input.charAt(i + 1) != ' ' ){
// sb.append(' ');
// }
// }
//
// return sb.toString();
// }
	/**
	 * Called to process RBDIndicator requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processRBDIndicatorRequests(List<MDCheckRequest> requests, int attempts) throws KettleException {
		RBDIndicatorMeta rbdIndicator = stepData.getRBDIndicator();
		try {
			// Process only if enabled
			if (rbdIndicator != null) {
				doWebRequests(rbdIndicator, requests, AdvancedConfigurationMeta.MDLICENSE_Address, 0, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				rbdIndicator.webMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				rbdIndicator.webMsg = "Unknown Error " + e.toString();
			}
			rbdIndicator.webException = e;
			if (!testing) { throw rbdIndicator.webException; }
		}
	}

	/**
	 * Called to process address requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processSmartMoverRequests(List<MDCheckRequest> requests, int queue, int attempts) throws KettleException {
		SmartMoverMeta smVerify = stepData.getSmartMover();
		try {
			// process
			if ((smVerify != null) && smVerify.isEnabled()) {
				doWebRequests(smVerify.getWebRequestHandler(), requests, AdvancedConfigurationMeta.MDLICENSE_SmartMover, queue, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				smVerify.webSmMsg = BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NotLicensed.Label");
			} else {
				smVerify.webSmMsg = e.toString();
			}
			smVerify.webSMException = e;
			if (!testing) { throw smVerify.webSMException; }
		}
	}

	/**
	 * Transform XML to as DOM object in a thread safe manner
	 *
	 * @param xmlResponse
	 * @return
	 * @throws DocumentException
	 */
	private synchronized org.dom4j.Document readXML(StringBuffer xmlResponse) throws DocumentException {
		return saxReader.read(new StringReader(xmlResponse.toString()));
	}
}
