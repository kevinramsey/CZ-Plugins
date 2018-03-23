package com.melissadata.kettle.personator.web;

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

import org.dom4j.DocumentException;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
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


import com.melissadata.cz.MDProps;
import com.melissadata.kettle.personator.MDPersonator;
import com.melissadata.kettle.personator.MDPersonatorCVService;
import com.melissadata.kettle.personator.MDPersonatorData;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.MDPersonatorRequest;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.error.MDAbortException;
import com.melissadata.cz.support.MDPropTags;


public class PersonatorWebService extends MDPersonatorCVService {

	private static Class<?> PKG = MDPersonatorMeta.class;

	private DOMImplementation domImplentation;
	private Transformer serializer;
	private SAXReader saxReader;

	private WebClient webClient;
	private WebClient cvsClient;
	
	private static String errorMessage;
	
	public PersonatorWebService(MDPersonatorData checkData, MDPersonatorMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		super(checkData, checkMeta, space, log);
	}


	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPersonatorService#init()
	 */
	@Override
	public void init() throws KettleException {
		
		// Call parent handler first
		super.init();
		
		try {			
			initReporting();
			
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
				throw new KettleException(MDPersonator.getErrorString("BadCustomerID", ""));
			}
			
			// get maximum requests per batch
			try{
			checkData.maxRequests = Integer.parseInt( MDProps.getProperty(MDPropTags.TAG_PERSONATOR_REQUESTS, ""));
			
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting max requests property");
			}
			
			// Get service URLs
			try{
				checkData.realWebPersonatorURL = new URL(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PERSONATOR_URL, "")));
			} catch (MalformedURLException mle) {
				// Change the description
				throw new KettleException("Problem getting server url property");
			}

			// get timeout settings
			try{
				checkData.realWebTimeout = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WEB_TIMEOUT, "")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web timeout property");
			}

			try{
				checkData.realWebRetries = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WEB_RETRIES, "")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web retries property");
			}

			if(MDProps.getProperty(MDPropTags.TAG_WEB_OPT_ABORT, "").equals("True")){
				checkData.webAbortOnError = true;
			}
			else{
				checkData.webAbortOnError = false;
			}
			
			// get proxy settings
			checkData.realProxyHost = space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_HOST, ""));
			checkData.realProxyPort = Const.toInt(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PORT, "")), 8080);
			checkData.realProxyUser = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_USER, "");
			checkData.realProxyPass = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PASS, "");
			
		} catch (Throwable t) {
			initFailed = true;
			
			// If anything unusual happened, return an initialization failure
			MDPersonatorData.webInitMsg = MDPersonator.getErrorString("InitializeService", t.toString());
			checkData.webInitException = new KettleException(MDPersonatorData.webInitMsg, t);
			
			if (!testing)
				throw checkData.webInitException;
		}
	}

	private String getCustomerID(){
		String id = "";
		int retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, "0"));
		
		if((retVal & MDPropTags.MDLICENSE_Personator) != 0){
			id = MDProps.getProperty(MDPropTags.TAG_PRIMARY_ID, "");
		}else{
			retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RET_VAL, "0"));
			if((retVal & MDPropTags.MDLICENSE_Personator) != 0){
				id = MDProps.getProperty(MDPropTags.TAG_TRIAL_ID, "");
			}
		}

		return id;
	}

	private void initReporting(){
		// 
		// init if needed
		//
		/*
		if(checkData.numAddrChanges == null){
			checkData.numAddrChanges = new HashMap<String, Integer>();
			for(int i = 0; i < checkData.getMeta().addrChangeReportCStat.length; i++){
				checkData.numAddrChanges.put(checkData.getMeta().addrChangeReportCStat[i],new Integer(0));
			}
		}
		if(checkData.numAddrErrors == null){
			checkData.numAddrErrors = new HashMap<String, Integer>();
			for(int i = 0; i < checkData.getMeta().errorReportCStat.length; i++){
				checkData.numAddrErrors.put(checkData.getMeta().errorReportCStat[i],new Integer(0));
			}
		}
		if(checkData.numAddrOverview == null){
			checkData.numAddrOverview = new HashMap<String, Integer>();
			for(int i = 0; i < checkData.getMeta().addrOverviewReportCStat.length; i++){
				checkData.numAddrOverview.put(checkData.getMeta().addrOverviewReportCStat[i],new Integer(0));
			}
		}
	*/
	}
	
	@Override
	public boolean checkProxy() {
		StringBuffer xmlResponse = new StringBuffer(); // this is just a
		// place holder
		String prMsg = "";
		int statCode = 0;

		statCode = getWebClient().checkProxy(checkData.realWebPersonatorURL, "", xmlResponse);

		if (statCode != 200) {


			switch (statCode) {
			case 599:
				prMsg =  statCode + ": Could not locate proxy";
				break;
			case 407:
				prMsg = statCode + ": Could not authenticate with proxy";
				break;
			case 3:
				prMsg = statCode + ": Access denied to proxy";
				break;
			case 0:
				prMsg = MDPersonatorData.webInitMsg;
				break;
			default:
				prMsg = statCode + ": Could not connect to proxy";
				break;
			}
			
			if(Const.isEmpty(checkData.realProxyHost)){
				prMsg = statCode + ": Could not connect to " + checkData.realWebPersonatorURL;
			}

			MDPersonatorData.webInitMsg = prMsg;
			return false;
		}

		return true;
	}


	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPersonatorService#processRequests()
	 */
	@Override
	public void processRequests(List<MDPersonatorRequest> requests, int queue, int attempts) throws KettleException {
		// Process the address verifier requests
		processPersonatorRequests(requests, attempts);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPersonatorService#dispose()
	 */
	@Override
	public void dispose() {
		// Close the web clients (if there are any)
		if (webClient != null)
			webClient.close();
		if (cvsClient != null)
			cvsClient.close();

		// Call parent handler
		super.dispose();
	}

	/**
	 * Called to process address requests
	 * 
	 * @param requests
	 * @throws KettleException 
	 */
	private void processPersonatorRequests(List<MDPersonatorRequest> requests, int attempts) throws KettleException {
		PersonatorFields pFields = checkMeta.personatorFields;
		PersonatorRequestHandler personatorHandler = new PersonatorRequestHandler(pFields); 
		try {

			// process only if address verification is enabled
			if (pFields.hasMinRequirements())
				doWebRequests(personatorHandler, requests, attempts);
			
		} catch (KettleException e) {
			// Remember what went wrong
			if(e.toString().contains("GE05")){
				personatorHandler.webMsg = "Not Licensed";
			}else{
				personatorHandler.webMsg = e.toString();
			}
			personatorHandler.webException = e;
			if (!testing)
				throw personatorHandler.webException;
		}
	}


	/**
	 * This is a generic implementation of web requests processing
	 * 
	 * @param handler
	 * @param requests
	 * @throws KettleException
	 */
	private void doWebRequests(WebRequestHandler handler, List<MDPersonatorRequest> requests, int attempts) throws KettleException {
		try {
			// Create the request document
			Document requestDoc = createDocument();

			addTextNode(requestDoc, requestDoc.getDocumentElement(), "TransmissionReference", "" + checkMeta.getTransmissionReference());
			// Add customer id
			addTextNode(requestDoc, requestDoc.getDocumentElement(), "CustomerID", "" + checkData.realCustomerID);
			
			// Build the request document from the request data
			boolean sendRequest = handler.buildWebRequest(requestDoc, checkData, requests, testing);
			
			// Convert the document object to an XML request string
			String xmlRequest = sendRequest ? convertToXML(requestDoc) : null;
			
			//System.out.println("\n" + xmlRequest + "\n");
			
			// If there was at least one record then we send the request and process the response
			if (xmlRequest != null) {
				// See if we are in a fail over state (using the web service instead of the local appliance)
				// and if we should retry the appliance after the specified time interval.

				// Post the requests and get the responses
				StringBuffer xmlResponse = new StringBuffer();
				int statusCode = 0;
				// Call web service
				statusCode = getWebClient().call(handler.getWebURL(checkData), xmlRequest, xmlResponse, attempts);
				
				// Check for problem
				if (statusCode != 200){
					throw new KettleException(MDPersonator.getErrorString("StatusCodeNot200", "" + statusCode ) + "-" + handler.toString() + "-" + errorMessage);
					
				}
				
				// Process responses and extract results into request object
				org.dom4j.Document doc = readXML(xmlResponse);
				String resultCodes = handler.processWebResponse(doc, checkData, requests, testing);
				
				// TODO: if result codes contains SE01 then retry request
				if (resultCodes.contains("SE01")){
					// this is just to see where an error happens
					// sometimes we get no version# from web
					MDPersonatorData.webInitMsg = "Server Error SE01";
				}
				
				// Check for serious failure
				checkShowStoppingFault(resultCodes);
			}
			
		} catch (KettleException e) {
			// Re-throw
			throw e;
			
		} catch (Throwable t) {
			// If anything unusual happened
			throw new KettleException(MDPersonator.getErrorString("ProcessService", t.toString()), t);
		}
	}

	/**
	 * Called to determine if a result code returned by a service is potentiall fatal and, if it is, whether
	 * we should stop processing immediately.
	 * 
	 * @param resultCodes
	 * @throws KettleException 
	 */
	private void checkShowStoppingFault(String resultCodes) throws KettleException {
		// If there were any general result codes then throw an exception. 
		Set<String> codes = MDPersonator.getResultCodes(resultCodes);
		if (codes.size() > 0) {
			String message = PersonatorWebService.getGeneralResultCodeMessages(codes);
			
			// If not testing then check to see if we should abort the transform completely
			if (!testing) {
				if (checkData.webAbortOnError)
					throw new MDAbortException(message);
			}

			// The transform will either reroute the records to the log file or to 
			// the error stream (if the later is defined).
			throw new KettleException(message);
		}
	}

	/**
	 * Creates a new document in a thread save manner
	 * 
	 * @return 
	 */
	private synchronized Document createDocument() {
		return domImplentation.createDocument(null, "Request", null);
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
		serializer.transform(domSource, new StreamResult(sw));
		String xmlRequest = sw.toString();
		return xmlRequest;
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


	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPersonatorService#saveReports()
	 */
	@Override
	public void saveReports() {
		// No reports from web service
	}

	public static void setErrorMessage(String msg){
		errorMessage = msg;
	}

	/**
	 * 
	 * @param parent
	 * @param name
	 * @return
	 */
	public static org.dom4j.Element getElement(org.dom4j.Element parent, String name) {
		org.dom4j.Element element = parent.element(name);
		if (element == null)
			return new DOMElement(parent.getQName(name));
		return element;
	}

	/**
	 * @param xmlDoc
	 * @param parent
	 * @param tagName
	 * @param data
	 * @throws DOMException
	 */
	public static void addTextNode(Document xmlDoc, Element parent, String tagName, String data) throws DOMException {
//		data = stripInvalidXmlCharacters(data);
		Element e = xmlDoc.createElement(tagName);
		Node n = xmlDoc.createTextNode(data);
		e.appendChild(n);
		parent.appendChild(e);
	}

	/*
	private static String stripInvalidXmlCharacters(String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ((c != '/' && c != 0x005C) 
					&& (c < 0x00FD && c > 0x001F) || c == '\t' || c == '\n' || c == '\r' ) {
				sb.append(c);
			}
			else if((i > 0 || i == (input.length() - 1)) && input.charAt(i - 1) != ' ' && input.charAt(i + 1) != ' ' ){		
				sb.append(' ');
			}
		}

		return sb.toString();
	}
	*/
	
	/**
	 * @param element
	 * @param name
	 * @return
	 */
	public static String getElementText(org.dom4j.Element element, String name) {
		String text = element.elementText(name);
		if (text == null)
			return "";
		
		return text.trim();
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
			if (text == null)
				throw new KettleException("Could not find integer value for " + name);
			int value = Integer.valueOf(text);
			return value;
		} catch (NumberFormatException e) {
			throw new KettleException("Problem getting integer value", e);
		}
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
			String description = BaseMessages.getString(PKG, "MDPersonator.ResultCode." + prefix + "." + resultCode);
			message.append(sep).append(resultCode).append(" = ").append(description);
			sep = "; ";
		}
		return message.toString();
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

}
