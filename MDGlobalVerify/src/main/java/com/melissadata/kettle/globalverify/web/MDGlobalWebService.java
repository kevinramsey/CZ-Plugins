package com.melissadata.kettle.globalverify.web;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.kettle.globalverify.*;
import com.melissadata.kettle.globalverify.Local.MDGlobalLocalService;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.data.EmailFields;
import com.melissadata.kettle.globalverify.data.NameFields;
import com.melissadata.kettle.globalverify.data.PhoneFields;
import com.melissadata.kettle.globalverify.error.MDAbortException;
import com.melissadata.kettle.globalverify.requesthandler.AddressVerifyRequestHandler;
import com.melissadata.kettle.globalverify.requesthandler.EmailRequestHandler;
import com.melissadata.kettle.globalverify.requesthandler.NameParseRequestHandler;
import com.melissadata.kettle.globalverify.requesthandler.PhoneRequestHandler;
import com.melissadata.kettle.globalverify.support.CountryUtil;
import com.melissadata.kettle.globalverify.support.MDPropTags;
import org.dom4j.DocumentException;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class MDGlobalWebService extends MDGlobalService {

	private static Class<?>          PKG                 = MDGlobalWebService.class;
	private static String            errorMessage        = null;
	private        DOMImplementation domImplentation     = null;
	private        Transformer       serializer          = null;
	private        SAXReader         saxReader           = null;
	private        WebClient         webClient           = null;
	private        MDGlobalService   localAddressService = null;
	private         CountryUtil      countryUtil = null;

	public MDGlobalWebService(MDGlobalData checkData, MDGlobalMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		super(checkData, checkMeta, space, log);
		countryUtil = new CountryUtil();
	}

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
		Node    n = xmlDoc.createTextNode(data);
		e.appendChild(n);
		parent.appendChild(e);
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
			if (text == null) {
				throw new KettleException("Could not find integer value for " + name);
			}
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
		if (text == null) {
			return "";
		}
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
		String       sep     = "";
		for (String resultCode : resultCodes) {
			String description = BaseMessages.getString(PKG, "MDGlobalAddress.ResultCode." + prefix + "." + resultCode);
			if ((description == null) || description.startsWith("!")) {
				description = BaseMessages.getString(PKG, "MDGlobalAddress.ResultCode.Undefined");
			}
			message.append(sep).append(resultCode).append("=").append(description);
			sep = "; ";
		}
		return message.toString();
	}

	public static void setErrorMessage(String msg) {

		errorMessage = msg;
	}

	public void addResultCodes(List<MDGlobalRequest> requests) {
		// Add result codes for each request
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDGlobalRequest request           = requests.get(recordID);
			Set<String>     sortedResultCodes = new TreeSet<String>(request.resultCodes);
			StringBuffer    s                 = new StringBuffer();
			String          sep               = "";
			for (String result : sortedResultCodes) {
				s.append(sep).append(result);
				sep = ",";
			}
			request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, s.toString());
		}
	}

	@Override
	public MDGlobalRequest buildRequest(IOMetaHandler ioMeta, Object[] inputData) {

		return new MDGlobalRequest(ioMeta, inputData);
	}

	@Override
	public boolean checkProxy() {

		StringBuffer xmlResponse = new StringBuffer(); // this is just a place holder
		String       prMsg       = "";
		int          statCode    = 0;
		statCode = getWebClient().checkProxy(checkData.realWebGlobalAddressVerifierURL, "", xmlResponse);
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
					prMsg = MDGlobalData.webInitMsg;
					break;
				default:
					prMsg = statCode + ": Could not connect to proxy";
					break;
			}
			if (Const.isEmpty(checkData.realProxyHost)) {
				prMsg = statCode + ": Could not connect to " + checkData.realWebGlobalAddressVerifierURL;
			}
			MDGlobalData.webInitMsg = prMsg;
			return false;
		}
		return true;
	}

	/**
	 * Called to determine if a result code returned by a service is potentiall fatal and, if it is, whether we should stop
	 * processing immediately.
	 *
	 * @param resultCodes
	 * @throws KettleException
	 */
	private void checkShowStoppingFault(String resultCodes) throws KettleException {
		// If there were any general result codes then throw an exception.
		Set<String> codes = MDGlobalVerify.getResultCodes(resultCodes);
		if (codes.size() > 0) {
			String message = MDGlobalWebService.getGeneralResultCodeMessages(codes);
			// If not testing then check to see if we should abort the transform completely
			if (!testing) {
				if (checkData.webAbortOnError) {
					throw new MDAbortException(message);
				}
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
		StringWriter sw        = new StringWriter();
		DOMSource    domSource = new DOMSource(requestDoc);
		serializer.transform(domSource, new StreamResult(sw));
		String xmlRequest = sw.toString();
		return xmlRequest;
	}

	public void createLocalAddressService() {
		localAddressService = MDGlobalService.create(checkData, checkMeta, space, log, true);
	}

	/**
	 * Creates a new document in a thread save manner
	 *
	 * @return
	 */
	private synchronized Document createDocument() {

		return domImplentation.createDocument(null, "Request", null);
	}

	@Override
	public int determineRequestRoute(MDGlobalRequest request) throws KettleException {

		AddressFields addrFields = checkMeta.getAddrMeta().getAddrFields();

		if(addrFields.getProcessType().equals(AddressFields.TAG_PROCESS_LOCAL) && addrFields.hasMinRequirements()){
			return 1;
		}

		if (addrFields.getProcessType().equals(AddressFields.TAG_PROCESS_VARIED) && addrFields.hasSelectedCountries() && addrFields.hasMinRequirements()) {
			String country = request.inputMeta.getString(request.inputData, addrFields.getCountryFieldIndex());
			int countryIndex = countryUtil.getCountryIndex(country);
			if (addrFields.getOpSelectedCountriesIndex().contains(countryIndex)) {
				return 1;
			}
		}

		return 0;
	}

	@Override
	public void dispose() {
		// Close the web clients (if there are any)
		if (webClient != null) {
			webClient.close();
		}
		if (localAddressService != null) {
			localAddressService.dispose();
		}
		// Call parent handler
		super.dispose();
	}

	/**
	 * This is a generic implementation of web requests processing
	 *
	 * @param handler
	 * @param
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	private void doWebRequests(WebRequestHandler handler, List<MDGlobalRequest> requests, int attempts) throws KettleException {

		try {
			log.logDebug("Web service doWebRequests,  service = " + handler.getServiceName());
			// Create the request document
			Document requestDoc  = createDocument();
			boolean  sendRequest = false;
			String   xmlRequest  = null;

			if (handler.getServiceName().equals("PHONE")) {
				JSONObject mainObj = new JSONObject();
				String     id      = String.valueOf(checkData.realCustomerID);
				String     tranRef = checkMeta.getTransmissionReference();
				mainObj.put("CustomerID", id);
				mainObj.put("TransmissionReference", tranRef);
				sendRequest = handler.buildWebRequest(mainObj, checkData, requests);
				// Convert the document object to an XML request string
				xmlRequest = sendRequest ? mainObj.toJSONString() : null;
			} else {
				// Add customer id
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "CustomerID", "" + checkData.realCustomerID);
				// Build the request document from the request data
				sendRequest = handler.buildWebRequest(requestDoc, checkData, requests, testing);
				// Convert the document object to an XML request string
				try {
					xmlRequest = sendRequest ? convertToXML(requestDoc) : null;
				} catch (TransformerException te) {
					log.logError("Error converting requestDoc to xml  - " + te.getCause());
				}
			}
			// If there was at least one record then we send the request and process the response
			if (xmlRequest != null) {
				// See if we are in a fail over state (using the web service instead of the local appliance)
				// and if we should retry the appliance after the specified time interval.
				// Post the requests and get the responses
				StringBuffer xmlResponse = new StringBuffer();
				int          statusCode  = 0;
				// Call web service
				statusCode = getWebClient().call(handler.getWebURL(checkData), xmlRequest, xmlResponse, attempts);
				// Check for problem
				if (statusCode != 200) {
					throw new KettleException(MDGlobalVerify.getErrorString("StatusCodeNot200", "" + statusCode) + "-" + handler.toString() + "-" + errorMessage);
				}

				// Process responses and extract results into request object
				String resultCodes = "";
				if (handler.getServiceName().equals("PHONE")) {
					JSONObject jsonResponse = new JSONObject();
					JSONParser parser       = getParser();
					jsonResponse = (JSONObject) parser.parse(xmlResponse.toString());
					resultCodes = handler.processWebResponse(jsonResponse, checkData, requests);
				} else {

					org.dom4j.Document doc = readXML(xmlResponse);
					resultCodes = handler.processWebResponse(doc, checkData, requests, testing);
				}
				if (resultCodes.contains("SE01")) {
					// this is just to see where an error happens
					// sometimes we get no version# from web
					MDGlobalData.webInitMsg = "Server Error SE01";
				}
				// Check for serious failure
				checkShowStoppingFault(resultCodes);
			}
		} catch (KettleException e) {
			// Re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened
			throw new KettleException(MDGlobalVerify.getErrorString("ProcessService", t.toString()), t);
		}
	}

	private String getCustomerID() {

		String id     = "";
		int    retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RETVAL, "0"));
		if ((retVal & MDPropTags.MDLICENSE_GlobalVerify) != 0) {
			id = MDProps.getProperty(MDPropTags.TAG_PRIMARY_ID, "");
		} else {
			retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RETVAL, "0"));
			if ((retVal & MDPropTags.MDLICENSE_GlobalVerify) != 0) {
				id = MDProps.getProperty(MDPropTags.TAG_TRIAL_ID, "");
			}
		}
		return id;
	}

	private synchronized JSONParser getParser() {

		return new JSONParser();
	}

	/**
	 * @return The current instance of the web service client
	 */
	private WebClient getWebClient() {

		if (webClient == null) {
			// Create a web client
			webClient = new WebClient(log);
			webClient.setTimeout(checkData.realWebTimeout);
			webClient.setRetries(checkData.realWebRetries);
			webClient.setProxy(checkData.realProxyHost, checkData.realProxyPort, checkData.realProxyUser, checkData.realProxyPass);
		}
		return webClient;
	}

	@Override
	public void init() throws KettleException {
		// Call parent handler first
		super.init();
		try {
			// initReporting();
			MDProps.load();
			// Create DOM object to build requests
			domImplentation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
			// Create Serializer to use to translate DOM into XML string
			serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
			// Create SAX reader to use when parsing responses
			saxReader = new SAXReader();
			// Get the real Customer ids
			try {
				String customerID = getCustomerID();
				checkData.realCustomerID = Integer.parseInt(customerID);
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException(MDGlobalVerify.getErrorString("BadCustomerID", ""));
			}
			// get maximum requests per batch
			try {
				checkData.maxRequests = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_REQUESTS, "100"));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting max requests property");
			}
			// Get service URLs
			try {
				checkData.realWebGlobalAddressVerifierURL = new URL(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_GLOBAL_ADDRESS_URL, "")));
				checkData.realWebGlobalNameVerifierURL = new URL(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_GLOBAL_NAME_URL, "")));
				checkData.realWebGlobalPhoneVerifierURL = new URL(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_GLOBAL_PHONE_URL, "")));
				checkData.realWebGlobalEmailVerifierURL = new URL(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_GLOBAL_EMAIL_URL, "")));
			} catch (MalformedURLException mle) {
				// Change the description
				throw new KettleException("Problem getting server url property");
			}
			// get timeout settings
			try {
				checkData.realWebTimeout = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_WEB_TIMEOUT, "1")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web timeout property");
			}
			try {
				checkData.realWebRetries = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_WEB_RETRIES, "1")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web retries property");
			}
			if (MDProps.getProperty(MDPropTags.TAG_WEB_OPT_ABORT, "").equals("True")) {
				checkData.webAbortOnError = true;
			} else {
				checkData.webAbortOnError = false;
			}
			// get proxy settings
			checkData.realProxyHost = space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_HOST, ""));
			checkData.realProxyPort = Const.toInt(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PORT, "")), 8080);
			checkData.realProxyUser = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_USER, "");
			checkData.realProxyPass = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PASS, "");
			if (localAddressService != null) {
				localAddressService.init();
			}
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened, return an initialization failure
			MDGlobalData.webInitMsg = MDGlobalVerify.getErrorString("InitializeService", t.toString());
			checkData.webInitException = new KettleException(MDGlobalData.webInitMsg, t);
			if (!testing) {
				throw checkData.webInitException;
			}
		}
	}

	@Override
	public void outputData(List<MDGlobalRequest> requests, int queue) {

		if (checkMeta.getNameMeta().getNameFields().hasMinRequirements()) {
			checkMeta.getNameMeta().outputData(checkData, requests);
		}
		if (checkMeta.getAddrMeta().getAddrFields().hasMinRequirements()) {
			GlobalAddressEngine globalEngine = new GlobalAddressEngine(checkMeta.getAddrMeta().getAddrFields(), log);
			globalEngine.outputData(checkData, requests);
		}
		if (checkMeta.getPhoneMeta().getPhoneFields().hasMinRequirements()) {
			checkMeta.getPhoneMeta().outputData(checkData, requests);
		}
		if (checkMeta.getEmailMeta().getEmailFields().hasMinRequirements()) {
			checkMeta.getEmailMeta().outputData(checkData, requests);
		}
		addResultCodes(requests);
	}

	private void processAddrRequests(List<MDGlobalRequest> requests, int attempts, int queue) throws KettleException {

		AddressFields               gaf           = checkMeta.getAddrMeta().getAddrFields();
		AddressVerifyRequestHandler addressVerify = new AddressVerifyRequestHandler(gaf, log);

		try {
			// process only if address verification is enabled
			if (gaf.hasMinRequirements()) {
				if (queue == 0) {

					doWebRequests(addressVerify, requests, attempts);
				}
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				addressVerify.webMsg = "Not Licensed";
			} else {
				addressVerify.webMsg = e.toString();
			}
			addressVerify.webException = e;
			if (!testing) {
				throw addressVerify.webException;
			}
		}
	}

	private void processEmailRequests(List<MDGlobalRequest> requests, int attempts) throws KettleException {

		EmailFields         emailFields = checkMeta.getEmailMeta().getEmailFields();
		EmailRequestHandler handler     = new EmailRequestHandler(emailFields, log);
		try {
			// process only if address verification is enabled
			if (emailFields.hasMinRequirements()) {
				doWebRequests(handler, requests, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				handler.webMsg = "Not Licensed";
			} else {
				handler.webMsg = e.toString();
			}
			handler.webException = e;
			if (!testing) {
				throw handler.webException;
			}
		}
	}

	private void processNameRequests(List<MDGlobalRequest> requests, int attempts) throws KettleException {

		NameFields              nameFields = checkMeta.getNameMeta().getNameFields();
		NameParseRequestHandler handler    = new NameParseRequestHandler(nameFields, log);
		try {
			// process only if enabled
			if (nameFields.hasMinRequirements()) {
				doWebRequests(handler, requests, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				handler.webMsg = "Not Licensed";
			} else {
				handler.webMsg = e.toString();
			}
			handler.webException = e;
			if (!testing) {
				throw handler.webException;
			}
		}
	}

	private void processPhoneRequests(List<MDGlobalRequest> requests, int attempts) throws KettleException {

		PhoneFields ohoneFields = checkMeta.getPhoneMeta().getPhoneFields();

		PhoneRequestHandler handler = new PhoneRequestHandler(ohoneFields, log);
		try {
			// process only if address verification is enabled
			if (ohoneFields.hasMinRequirements()) {
				doWebRequests(handler, requests, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				handler.webMsg = "Not Licensed";
			} else {
				handler.webMsg = e.toString();
			}
			handler.webException = e;
			if (!testing) {
				throw handler.webException;
			}
		}
	}

	@Override
	public void processRequests(List<MDGlobalRequest> requests, int queue, int attempts) throws KettleException {

		// Start point ok
		// Process the address verifier requests
		if (checkMeta.getNameMeta().getNameFields().hasMinRequirements()) {
			processNameRequests(requests, attempts);
		}
		if (checkMeta.getAddrMeta().getAddrFields().hasMinRequirements()) {
			if (queue == 1) {
				localAddressService.processRequests(requests, queue, attempts);
			} else {
				processAddrRequests(requests, attempts, queue);
			}
		}
		if (checkMeta.getPhoneMeta().getPhoneFields().hasMinRequirements()) {
			processPhoneRequests(requests, attempts);
		}
		if (checkMeta.getEmailMeta().getEmailFields().hasMinRequirements()) {
			processEmailRequests(requests, attempts);
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

	@Override
	protected IRequestManager createRequestManager() {
		// If this is a local service then we only allow one thread in the thread pool.
		// Otherwise, we use the configured thread count.
		int maxThreads = 1;
		if (localAddressService == null) {
			// If we have no local processing we can go with more threads
			maxThreads = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_THREADS, "1"));
		}

		return new RequestManager(maxThreads);
	}
}
