package com.melissadata.kettle.propertywebservice.web;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.dom4j.DocumentException;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
import com.melissadata.cz.MDProps;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceData;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceStep;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.request.PropertyWebServiceRequestHandler;
import com.melissadata.kettle.propertywebservice.request.MDPropertyWebServiceRequest;
import com.melissadata.kettle.propertywebservice.request.MDPropertyWebServiceRequest.ProCoderResults;
import com.melissadata.kettle.propertywebservice.request.RequestManager;
import com.melissadata.cz.support.MDAbortException;
import com.melissadata.cz.support.MDPropTags;

public /*abstract*/ class MDPropertyWebService /* extends MDPropertyWebServiceService */ {

	public static synchronized MDPropertyWebService create(MDPropertyWebServiceData checkData, MDPropertyWebServiceMeta checkMeta, VariableSpace space, LogChannelInterface log) {

		MDPropertyWebService service = new MDPropertyWebService(checkData, checkMeta, space, log);
		return service;
	}

	private static Class<?> PKG = MDPropertyWebServiceMeta.class;//MDPropertyWebService.class;
	private        DOMImplementation        domImplentation;
	private        Transformer              serializer;
	private        SAXReader                saxReader;
	private        WebClient                webClient;
	private        WebClient                cvsClient;
	protected      VariableSpace            space;
	private static String                   errorMessage;
	public         MDPropertyWebServiceData serviceData;
	protected      MDPropertyWebServiceMeta serviceMeta;
	public         LogChannelInterface      log;
	protected      boolean                  initFailed;
	protected      boolean                  testing;

	public MDPropertyWebService(MDPropertyWebServiceData checkData, MDPropertyWebServiceMeta checkMeta, VariableSpace space, LogChannelInterface log) {

		serviceData = checkData;
		serviceMeta = checkMeta;
		this.space = space;
		this.log = log;
		initFailed = false;
	}

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

	public static String getElementText(org.dom4j.Element element, String name) {

		String text = element.elementText(name);
		if (text == null) {
			return "";
		}

		return text.trim();
	}

	public static void addTextNode(Document xmlDoc, Element parent, String tagName, String data) throws DOMException {
		// data = stripInvalidXmlCharacters(data);
		Element e = xmlDoc.createElement(tagName);

		Node n = xmlDoc.createTextNode(data);
		e.appendChild(n);
		parent.appendChild(e);
	}

	public static String getLogString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceMeta.Log." + key, args);
	}

	public static String getErrorString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceMeta.Error." + key, args);
	}

	public static org.dom4j.Element getElement(org.dom4j.Element parent, String name) {

		org.dom4j.Element element = parent.element(name);
		if (element == null) {
			return new DOMElement(parent.getQName(name));
		}
		return element;
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
			String description = BaseMessages.getString(PKG, "MDPropertyWebService.ResultCode." + prefix + "." + resultCode);
			if ((description == null) || description.startsWith("!")) {
				description = BaseMessages.getString(PKG, "MDPropertyWebService.ResultCode.Undefined");
			}
			message.append(sep).append(resultCode).append("=").append(description);
			sep = "; ";
		}
		return message.toString();
	}

	public static void setErrorMessage(String msg) {

		errorMessage = msg;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPropertyWebServiceService#buildRequest(com.melissadata.kettle.IOMeta, java.lang.Object[])
	 */
	public MDPropertyWebServiceRequest buildRequest(com.melissadata.cz.support.IOMetaHandler ioMeta, Object[] inputData) {

		return new MDPropertyWebServiceRequest(ioMeta, inputData);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPropertyWebServiceService#dispose()
	 */

	public boolean checkProxy() {

		StringBuffer xmlResponse = new StringBuffer(); // this is just a place holder
		String       prMsg       = "";
		int          statCode    = 0;
		statCode = getWebClient().checkProxy(serviceData.realPropertyWebServiceURL, new String(), xmlResponse);
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
					prMsg = MDPropertyWebServiceData.webInitMsg;
					break;
				default:
					prMsg = statCode + ": Could not connect to proxy";
					break;
			}
			if (Const.isEmpty(serviceData.realProxyHost)) {
				prMsg = statCode + ": Could not connect to " + serviceData.realPropertyWebServiceURL;
			}
			MDPropertyWebServiceData.webInitMsg = prMsg;
			return false;
		}
		return true;
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
		Set<String> codes = MDPropertyWebServiceStep.getResultCodes(resultCodes);
		if (codes.size() > 0) {
			String message = MDPropertyWebService.getGeneralResultCodeMessages(codes);
			// If not testing then check to see if we should abort the transform completely
			if (serviceData.webAbortOnError) {
				throw new MDAbortException(message);
			}
			// The transform will either reroute the records to the log file or to
			// the error stream (if the later is defined).
			throw new KettleException(message);
		}
	}

	/**
	 * Create a request manager for processing requests in an asynchronous manner
	 *
	 * @return
	 */
	public RequestManager createRequestManager() throws KettleException {

		int maxThreads = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PROPERTY_THREADS, "1"));
		return new RequestManager(maxThreads);
	}

	public void dispose() {
		// Close the web clients (if there are any)
		if (webClient != null) {
			webClient.close();
			if (cvsClient != null) {
				cvsClient.close();
			}

//		// Call parent handler
//		super.dispose();
		}
	}

	/**
	 * This is a generic implementation of web requests processing
	 *
	 * @param handler
	 * @param requests
	 * @param attempts
	 * @throws KettleException
	 */
	private void doWebRequests(PropertyWebServiceRequestHandler handler, List<MDPropertyWebServiceRequest> requests, int attempts) throws KettleException {

		try {
			// Create the request document
			Document requestDoc = createDocument();

			addTextNode(requestDoc, requestDoc.getDocumentElement(), "TransmissionReference", "" + serviceMeta.getTransmissionReference());

			// Add customer id
			addTextNode(requestDoc, requestDoc.getDocumentElement(), "CustomerId", "" + serviceData.realLicense);

			addTextNode(requestDoc, requestDoc.getDocumentElement(), "Columns", "" + serviceMeta.getGroups());

			addTextNode(requestDoc, requestDoc.getDocumentElement(), "TotalRecords", "" + String.valueOf(requests.size()));

			//addTextNode(requestDoc, requestDoc.getDocumentElement(), "Format", "" + "XML");

			//addTextNode(requestDoc, requestDoc.getDocumentElement(), "OptPropertyDetail", "" + serviceData.realOptPropertyDetail);

			// Build the request document from the request data
			boolean sendRequest = handler.buildWebRequest(requestDoc, serviceData, requests, testing);

			// Convert the document object to an XML request string
			String xmlRequest = sendRequest ? convertToXML(requestDoc) : null;

			// If there was at least one record then we send the request and process the response
			if (xmlRequest != null) {
				// See if we are in a fail over state (using the web service instead of the local appliance)
				// and if we should retry the appliance after the specified time interval.

				// Post the requests and get the responses
				StringBuffer xmlResponse = new StringBuffer();
				int          statusCode  = 0;
				// Call web service
				statusCode = getWebClient().call(handler.getWebURL(serviceData), xmlRequest, xmlResponse, attempts);
				// Check for problem
				if (statusCode != 200) {
					throw new KettleException(MDPropertyWebService.getErrorString("StatusCodeNot200", "" + statusCode) + "-" + handler.toString() + "-" + errorMessage);
				}

				// Process responses and extract results into request object
				org.dom4j.Document doc         = readXML(xmlResponse);
				String             resultCodes = handler.processWebResponse(doc, serviceData, requests, testing);

				// TODO: if result codes contains SE01 then retry request
				if (resultCodes.contains("SE01")) {
					// this is just to see where an error happens
					// sometimes we get no version# from web
					MDPropertyWebServiceData.webInitMsg = "Server Error SE01";
				}

				// Check for serious failure
				checkShowStoppingFault(resultCodes);
			}
		} catch (KettleException e) {
			// Re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened
			throw new KettleException(MDPropertyWebService.getErrorString("ProcessService", t.toString()), t);
		}
	}

	private synchronized Document createDocument() {

		Document doc = null;
		try {
			doc = domImplentation.createDocument(null, "LookupPropertyRequest", null);
		} catch (Exception w) {
			System.out.println("   DOC ERROR :" + w.getCause());
			w.printStackTrace();
		}
		return doc;
	}

	private synchronized String convertToXML(Document requestDoc) throws TransformerException {
		// Convert to XML
		StringWriter sw        = new StringWriter();
		DOMSource    domSource = new DOMSource(requestDoc);
		serializer.transform(domSource, new StreamResult(sw));
		String xmlRequest = sw.toString();
		return xmlRequest;
	}

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
			webClient.setTimeout(serviceData.realWebTimeout);
			webClient.setRetries(serviceData.realWebRetries);
			webClient.setProxy(serviceData.realProxyHost, serviceData.realProxyPort, serviceData.realProxyUser, serviceData.realProxyPass);
		}
		return webClient;
	}

	public void init() throws KettleException {
		// Allocate request array
		serviceData.requests = new ArrayList<MDPropertyWebServiceRequest>();

		// Create DOM object to build requests
		try {
			domImplentation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
			serializer = TransformerFactory.newInstance().newTransformer();
		} catch (ParserConfigurationException | TransformerConfigurationException | TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		}

		// Create Serializer to use to translate DOM into XML string
		serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$

		// Create SAX reader to use when parsing responses
		saxReader = new SAXReader();
		try {
			String customerID = getCustomerID();

			serviceData.realCustomerID = Integer.parseInt(customerID);
		} catch (NumberFormatException e) {
			// Change the description
			throw new KettleException(MDPropertyWebService.getErrorString("BadCustomerID", ""));
		}

		try {
			MDProps.load();
			// Get the real Customer ids
			try {
				serviceData.realLicense = MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "").trim();
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException(MDPropertyWebServiceStep.getErrorString("BadCustomerID", ""));
			}
			// get maximum requests per batch
			try {
				serviceData.maxRequests = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PROPERTY_REQUESTS, ""));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting max requests property");
			}
			// Get service URLs
			try {
				serviceData.realPropertyWebServiceURL = new URL(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PROPERTY_URL, "")));
			} catch (MalformedURLException mle) {
				// Change the description
				throw new KettleException("Problem getting server url property: ");
			}
			// get timeout settings
			try {
				serviceData.realWebTimeout = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_PROPERTY_WEB_TIMEOUT, "")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web timeout property");
			}
			try {
				serviceData.realWebRetries = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_PROPERTY_WEB_RETRIES, "")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web retries property");
			}
			if (MDProps.getProperty(MDPropTags.TAG_WEB_OPT_ABORT, "").equals("True")) {
				serviceData.webAbortOnError = true;
			} else {
				serviceData.webAbortOnError = false;
			}
			// get proxy settings
			serviceData.realProxyHost = space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_HOST, ""));
			serviceData.realProxyPort = Const.toInt(space.environmentSubstitute(MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PORT, "")), 8080);
			serviceData.realProxyUser = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_USER, "");
			serviceData.realProxyPass = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PASS, "");
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened, return an initialization failure
			MDPropertyWebServiceData.webInitMsg = MDPropertyWebServiceStep.getErrorString("InitializeService", t.toString());
			serviceData.webInitException = new KettleException(MDPropertyWebServiceData.webInitMsg, t);
			throw serviceData.webInitException;
		}
	}

	private String getCustomerID() {

		String id     = "";
		int    retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, "0"));

		if ((retVal & MDPropTags.MDLICENSE_Property) != 0) {
			id = MDProps.getProperty(MDPropTags.TAG_PRIMARY_ID, "");
		} else {
			retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RET_VAL, "0"));
			if ((retVal & MDPropTags.MDLICENSE_Property) != 0) {
				id = MDProps.getProperty(MDPropTags.TAG_TRIAL_ID, "");
			}
		}
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPropertyWebServiceService#outputData(int)
	 */
	public void outputData(List<MDPropertyWebServiceRequest> requests/* , int queue */) {
		// Process the global address verifier requests

		PropertyWebServiceFields propertyFields = serviceMeta.propertyWebServiceFields;
		for (MDPropertyWebServiceRequest request : requests) {

			ProCoderResults propertyResults = request.proCoderResults;

			if ((propertyResults != null) && propertyResults.valid) {


				for (String key : propertyFields.outputFields.keySet()) {

					//	System.out.println(" The key -> " + key);
					if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PARCEL_PREFIX)) {
						outputParcel(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_LEGAL_PREFIX)) {
						outputLegal(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_PREFIX)) {
						outputPropertyAddress(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PARSED_PROPERTY_PREFIX)) {
						outputParsedProperty(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_OWNER_PREFIX)) {
						outputPrimaryOwner(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_OWNER_PREFIX)) {
						outputSecondaryOwner(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ADDRESS_PREFIX)) {
						outputOwnerAddress(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_PREFIX)) {
						outputLastDeed(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_CURRENT_DEED_PREFIX)) {
						outputCurrentDeed(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_TAX_PREFIX)) {
						outputTax(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_VALUE_PREFIX)) {
						outputEstimatedValue(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_SALE_INFO_PREFIX)) {
						outputSaleInfo(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_PREFIX)) {
						outputPropertyUse(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_SIZE_PREFIX)) {
						outputPropertySize(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_POOL_PREFIX)) {
						outputPool(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_UTILITIES_PREFIX)) {
						outputUtilities(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PARKING_PREFIX)) {
						outputParking(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_YARD_GARDEN_PREFIX)) {
						outputYardGarden(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_INTER_STRUCT_PREFIX)) {
						outputIntStruct(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_INTER_ROOM_PREFIX)) {
						outputIntRoom(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_INT_AMMENITIES_PREFIX)) {
						outputIntAmenities(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX)) {
						outputExtStruct(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_EXT_AMMENITIES_PREFIX)) {
						outputExtAmenities(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_EXT_BUILDINGS_PREFIX)) {
						outputExtBuildings(key, request);
					} else if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_SHAPE_PREFIX)) {
						outputShape(key, request);
					} else {
//							System.out.println(" Well add Results .......................... " + key);
//						// Add result codes after all other fields
//						request.addOutputData(propertyResults.results);
					}
				}

				// Add result codes after all other fields
				request.addOutputData(propertyResults.results);
			}
		}
	}

	private void outputParcel(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;

		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FIPSCODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIPSCODE).metaValue)) {
			request.addOutputData(propertyResults.fipsCode);
		}

		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_COUNTY && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTY).metaValue)) {
			request.addOutputData(propertyResults.county);
		}

		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_UNFORMATTEDAPN && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNFORMATTEDAPN).metaValue)) {
			request.addOutputData(propertyResults.unformattedAPN);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FORMATTEDAPN && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FORMATTEDAPN).metaValue)) {
			request.addOutputData(propertyResults.formattedAPN);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ALTERNATEAPN && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ALTERNATEAPN).metaValue)) {
			request.addOutputData(propertyResults.alternateAPN);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_APN_YEAR_CHANGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_APN_YEAR_CHANGE).metaValue)) {
			request.addOutputData(propertyResults.aPNYearChange);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_APN && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_APN).metaValue)) {
			request.addOutputData(propertyResults.previousAPN);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ACCOUNT_NUMBER && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ACCOUNT_NUMBER).metaValue)) {
			request.addOutputData(propertyResults.accountNumber);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_YEAR_ADDED && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_ADDED).metaValue)) {
			request.addOutputData(propertyResults.yearAdded);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MAP_BOOK && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MAP_BOOK).metaValue)) {
			request.addOutputData(propertyResults.mapBook);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MAP_PAGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MAP_PAGE).metaValue)) {
			request.addOutputData(propertyResults.mapPage);
		}
	}

	private void outputLegal(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;

		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LEGAL_DESCRIPTION && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEGAL_DESCRIPTION).metaValue)) {
			request.addOutputData(propertyResults.legalDescription);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_RANGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RANGE).metaValue)) {
			request.addOutputData(propertyResults.legalRange);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TOWNSHIP && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TOWNSHIP).metaValue)) {
			request.addOutputData(propertyResults.township);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECTION && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECTION).metaValue)) {
			request.addOutputData(propertyResults.section);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_QUARTER && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUARTER).metaValue)) {
			request.addOutputData(propertyResults.quarter);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_QUARTER_QUATER && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUARTER_QUATER).metaValue)) {
			request.addOutputData(propertyResults.quarterQuarter);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SUBDIVISION && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUBDIVISION).metaValue)) {
			request.addOutputData(propertyResults.subdivision);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PHASE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PHASE).metaValue)) {
			request.addOutputData(propertyResults.phase);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TRACT_NUMBER && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TRACT_NUMBER).metaValue)) {
			request.addOutputData(propertyResults.tractNumber);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BLOCK_1 && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BLOCK_1).metaValue)) {
			request.addOutputData(propertyResults.block1);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BLOCK_2 && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BLOCK_2).metaValue)) {
			request.addOutputData(propertyResults.block2);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_1 && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_1).metaValue)) {
			request.addOutputData(propertyResults.lotNumber1);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_2 && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_2).metaValue)) {
			request.addOutputData(propertyResults.lotNumber2);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_3 && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_3).metaValue)) {
			request.addOutputData(propertyResults.lotNumber3);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_UNIT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNIT).metaValue)) {
			request.addOutputData(propertyResults.unit);
		}
	}

	private void outputPropertyAddress(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESS && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESS).metaValue)) {
			request.addOutputData(propertyResults.address);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_CITY && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_CITY).metaValue)) {
			request.addOutputData(propertyResults.city);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_STATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_STATE).metaValue)) {
			request.addOutputData(propertyResults.state);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ZIP && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ZIP).metaValue)) {
			request.addOutputData(propertyResults.zip);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESSKEY && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESSKEY).metaValue)) {
			request.addOutputData(propertyResults.addressKey);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_MAK && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_MAK).metaValue)) {
			request.addOutputData(propertyResults.mak);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_BASE_MAK && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_BASE_MAK).metaValue)) {
			request.addOutputData(propertyResults.baseMAK);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LATITUDE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LATITUDE).metaValue)) {
			request.addOutputData(propertyResults.latitude);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LONGITUDE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LONGITUDE).metaValue)) {
			request.addOutputData(propertyResults.longitude);
		}
	}

	private void outputParsedProperty(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;

		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARSED_RANGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_RANGE).metaValue)) {
			request.addOutputData(propertyResults.parsedRange);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARSED_PREDIRECTIONAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_PREDIRECTIONAL).metaValue)) {
			request.addOutputData(propertyResults.preDirectional);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARSED_STREETNAME && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_STREETNAME).metaValue)) {
			request.addOutputData(propertyResults.streetName);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARSED_POSTDIRECTIONAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_POSTDIRECTIONAL).metaValue)) {
			request.addOutputData(propertyResults.postDirectional);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITENAME && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITENAME).metaValue)) {
			request.addOutputData(propertyResults.suiteName);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITERANGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITERANGE).metaValue)) {
			request.addOutputData(propertyResults.suiteRange);
		}
	}

	private void outputPrimaryOwner(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FULL).metaValue)) {
			request.addOutputData(propertyResults.name1Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FIRST).metaValue)) {
			request.addOutputData(propertyResults.name1First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.name1Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_LAST).metaValue)) {
			request.addOutputData(propertyResults.name1Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.name1Suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TRUST_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TRUST_FLAG).metaValue)) {
			request.addOutputData(propertyResults.ownerTrustFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_COMPANY_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_COMPANY_FLAG).metaValue)) {
			request.addOutputData(propertyResults.ownerCompanyFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FULL).metaValue)) {
			request.addOutputData(propertyResults.name2Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FIRST).metaValue)) {
			request.addOutputData(propertyResults.name2First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.name2Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_LAST).metaValue)) {
			request.addOutputData(propertyResults.name2Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.name2Suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TYPE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TYPE).metaValue)) {
			request.addOutputData(propertyResults.ownerType);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_VESTING_TYPE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_VESTING_TYPE).metaValue)) {
			request.addOutputData(propertyResults.ownerVestingType);
		}
	}

	private void outputSecondaryOwner(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FULL).metaValue)) {
			request.addOutputData(propertyResults.name3Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FIRST).metaValue)) {
			request.addOutputData(propertyResults.name3First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.name3Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_LAST).metaValue)) {
			request.addOutputData(propertyResults.name3Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.name3Suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FULL).metaValue)) {
			request.addOutputData(propertyResults.name4Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FIRST).metaValue)) {
			request.addOutputData(propertyResults.name4First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.name4Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_LAST).metaValue)) {
			request.addOutputData(propertyResults.name4Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.name4Suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_TYPE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_TYPE).metaValue)) {
			request.addOutputData(propertyResults.secondarytype);
		}
	}

	private void outputOwnerAddress(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OWNER_ADDRESS && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ADDRESS).metaValue)) {
			request.addOutputData(propertyResults.ownerAddress);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OWNER_CITY && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_CITY).metaValue)) {
			request.addOutputData(propertyResults.ownerCity);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OWNER_STATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_STATE).metaValue)) {
			request.addOutputData(propertyResults.ownerState);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OWNER_ZIP && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ZIP).metaValue)) {
			request.addOutputData(propertyResults.ownerZip);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OWNER_CARRIERROUTE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_CARRIERROUTE).metaValue)) {
			request.addOutputData(propertyResults.ownerCarrierRoute);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OWNER_MAK && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_MAK).metaValue)) {
			request.addOutputData(propertyResults.ownerMAK);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OWNER_BASE_MAK && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_BASE_MAK).metaValue)) {
			request.addOutputData(propertyResults.ownerBaseMAK);
		}
	}

	private void outputLastDeed(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FULL).metaValue)) {
			request.addOutputData(propertyResults.ldoName1Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FIRST).metaValue)) {
			request.addOutputData(propertyResults.ldoName1First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.ldoName1Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_LAST).metaValue)) {
			request.addOutputData(propertyResults.ldoName1Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.ldoName1Suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FULL).metaValue)) {
			request.addOutputData(propertyResults.ldoName2Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FIRST).metaValue)) {
			request.addOutputData(propertyResults.ldoName2First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.ldoName2Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_LAST).metaValue)) {
			request.addOutputData(propertyResults.ldoName2Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.ldoName2Suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FULL).metaValue)) {
			request.addOutputData(propertyResults.ldoName3Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FIRST).metaValue)) {
			request.addOutputData(propertyResults.ldoName3First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.ldoName3Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_LAST).metaValue)) {
			request.addOutputData(propertyResults.ldoName3Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.ldoName3Suffix);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FULL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FULL).metaValue)) {
			request.addOutputData(propertyResults.ldoName4Full);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FIRST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FIRST).metaValue)) {
			request.addOutputData(propertyResults.ldoName4First);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_MIDDLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_MIDDLE).metaValue)) {
			request.addOutputData(propertyResults.ldoName4Middle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_LAST && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_LAST).metaValue)) {
			request.addOutputData(propertyResults.ldoName4Last);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_SUFFIX && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_SUFFIX).metaValue)) {
			request.addOutputData(propertyResults.ldoName4Suffix);
		}
	}

	private void outputCurrentDeed(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_AMOUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_AMOUNT).metaValue)) {
			request.addOutputData(propertyResults.mortgageAmount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DATE).metaValue)) {
			request.addOutputData(propertyResults.mortgageDate);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_LOAN_TYPE_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_LOAN_TYPE_CODE).metaValue)) {
			request.addOutputData(propertyResults.mortgageLoanTypeCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM_CODE).metaValue)) {
			request.addOutputData(propertyResults.mortgageTermCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM).metaValue)) {
			request.addOutputData(propertyResults.mortgageTerm);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DUE_DATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DUE_DATE).metaValue)) {
			request.addOutputData(propertyResults.mortgageDueDate);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LENDER_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LENDER_CODE).metaValue)) {
			request.addOutputData(propertyResults.lenderCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LENDER_NAME && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LENDER_NAME).metaValue)) {
			request.addOutputData(propertyResults.lenderName);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_AMOUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_AMOUNT).metaValue)) {
			request.addOutputData(propertyResults.secondMortgageAmount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_LOAN_TYPE_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_LOAN_TYPE_CODE).metaValue)) {
			request.addOutputData(propertyResults.secondMortgageLoanTypeCode);
		}
	}

	private void outputTax(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_YEAR_ASSESSED && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_ASSESSED).metaValue)) {
			request.addOutputData(propertyResults.yearAssessed);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_TOTAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_TOTAL).metaValue)) {
			request.addOutputData(propertyResults.assessedValueTotal);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_IMPROVEMENTS && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_IMPROVEMENTS).metaValue)) {
			request.addOutputData(propertyResults.assessedValueImprovements);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_LAND && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_LAND).metaValue)) {
			request.addOutputData(propertyResults.assessedValueLand);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_IMPROVEMENTS_PERC && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_IMPROVEMENTS_PERC).metaValue)) {
			request.addOutputData(propertyResults.assessedImprovementsPerc);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_ASSESSED_VALUE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_ASSESSED_VALUE).metaValue)) {
			request.addOutputData(propertyResults.previousAssessedValue);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_YEAR && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_YEAR).metaValue)) {
			request.addOutputData(propertyResults.marketValueYear);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_TOTAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_TOTAL).metaValue)) {
			request.addOutputData(propertyResults.marketValueTotal);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_IMPROVEMENTS && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_IMPROVEMENTS).metaValue)) {
			request.addOutputData(propertyResults.marketValueImprovements);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_LAND && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_LAND).metaValue)) {
			request.addOutputData(propertyResults.marketValueLand);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MARKET_IMPROVEMENT_PREC && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_IMPROVEMENT_PREC).metaValue)) {
			request.addOutputData(propertyResults.marketImprovementsPerc);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_FISCAL_YEAR && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_FISCAL_YEAR).metaValue)) {
			request.addOutputData(propertyResults.taxFiscalYear);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_RATE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_RATE_AREA).metaValue)) {
			request.addOutputData(propertyResults.taxRateArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_BILL_AMOUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_BILL_AMOUNT).metaValue)) {
			request.addOutputData(propertyResults.taxBilledAmount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_DELINQUENT_YEAR && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_DELINQUENT_YEAR).metaValue)) {
			request.addOutputData(propertyResults.taxDelinquentYear);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_TAX_ROLL_UPDATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_TAX_ROLL_UPDATE).metaValue)) {
			request.addOutputData(propertyResults.lastTaxRollUpdate);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSR_LAST_UPDATED && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSR_LAST_UPDATED).metaValue)) {
			request.addOutputData(propertyResults.assrLastUpdated);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_HOMEOWNER && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_HOMEOWNER).metaValue)) {
			request.addOutputData(propertyResults.taxExemptionHomeowner);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_DISABLED && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_DISABLED).metaValue)) {
			request.addOutputData(propertyResults.taxExemptionDisabled);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_SENIOR && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_SENIOR).metaValue)) {
			request.addOutputData(propertyResults.taxExemptionSenior);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_VETERAN && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_VETERAN).metaValue)) {
			request.addOutputData(propertyResults.taxExemptionVeteran);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_WIDOW && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_WIDOW).metaValue)) {
			request.addOutputData(propertyResults.taxExemptionWidow);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_ADDITIONAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_ADDITIONAL).metaValue)) {
			request.addOutputData(propertyResults.taxExemptionAdditional);
		}
	}

	private void outputEstimatedValue(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_VALUE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_VALUE).metaValue)) {
			request.addOutputData(propertyResults.estimatedValue);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MIN_VALUE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MIN_VALUE).metaValue)) {
			request.addOutputData(propertyResults.estimatedMinValue);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MAX_VALUE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MAX_VALUE).metaValue)) {
			request.addOutputData(propertyResults.estimatedMaxValue);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CONFIDENCE_SCORE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONFIDENCE_SCORE).metaValue)) {
			request.addOutputData(propertyResults.confidenceScore);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_VALUATION_DATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_VALUATION_DATE).metaValue)) {
			request.addOutputData(propertyResults.valuationDate);
		}
	}

	private void outputSaleInfo(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_DATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_DATE).metaValue)) {
			request.addOutputData(propertyResults.assessorLastSaleDate);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_AMMOUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_AMMOUNT).metaValue)) {
			request.addOutputData(propertyResults.assessorLastSaleAmount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_PRIOR_SALE_DATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_PRIOR_SALE_DATE).metaValue)) {
			request.addOutputData(propertyResults.assessorPriorSaleDate);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_PRIOR_SALE_AMOUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_PRIOR_SALE_AMOUNT).metaValue)) {
			request.addOutputData(propertyResults.assessorPriorSaleAmount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DATE).metaValue)) {
			request.addOutputData(propertyResults.lastOwnershipTransferDate);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DOCUMENT_NUMBER && !Const
				.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DOCUMENT_NUMBER).metaValue)) {
			request.addOutputData(propertyResults.lastOwnershipTransferDocumentNumber);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_TAX_ID && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_TAX_ID).metaValue)) {
			request.addOutputData(propertyResults.lastOwnershipTransferTxID);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_BOOK && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_BOOK).metaValue)) {
			request.addOutputData(propertyResults.deedLastSaleDocumentBook);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_PAGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_PAGE).metaValue)) {
			request.addOutputData(propertyResults.deedLastSaleDocumentPage);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_NUMBER && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_NUMBER).metaValue)) {
			request.addOutputData(propertyResults.deedLastDocumentNumber);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DATE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DATE).metaValue)) {
			request.addOutputData(propertyResults.deedLastSaleDate);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_PRICE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_PRICE).metaValue)) {
			request.addOutputData(propertyResults.deedLastSalePrice);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_TAX_ID && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_TAX_ID).metaValue)) {
			request.addOutputData(propertyResults.deedLastSaleTxID);
		}
	}

	private void outputPropertyUse(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;

		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT).metaValue)) {
			request.addOutputData(propertyResults.yearBuilt);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT_EFFECTIVE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT_EFFECTIVE).metaValue)) {
			request.addOutputData(propertyResults.yearBuiltEffective);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ZONED_CODE_LOCAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ZONED_CODE_LOCAL).metaValue)) {
			request.addOutputData(propertyResults.zonedCodeLocal);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_MUNI && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_MUNI).metaValue)) {
			request.addOutputData(propertyResults.propertyUseMuni);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_GROUP && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_GROUP).metaValue)) {
			request.addOutputData(propertyResults.propertyUseGroup);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_STANDARDIZED && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_STANDARDIZED).metaValue)) {
			request.addOutputData(propertyResults.propertyUseStandardized);
		}
	}

	private void outputPropertySize(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING).metaValue)) {
			request.addOutputData(propertyResults.areaBuilding);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING_DEFINITION_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING_DEFINITION_CODE).metaValue)) {
			request.addOutputData(propertyResults.areaBuildingDefinitionCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_GROSS && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_GROSS).metaValue)) {
			request.addOutputData(propertyResults.areaGross);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_1ST_FLOOR && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_1ST_FLOOR).metaValue)) {
			request.addOutputData(propertyResults.area1stFloor);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_2ND_FLOOR && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_2ND_FLOOR).metaValue)) {
			request.addOutputData(propertyResults.area2ndFloor);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_UPPER_FLOORS && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_UPPER_FLOORS).metaValue)) {
			request.addOutputData(propertyResults.areaUpperFloors);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_ACRES && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_ACRES).metaValue)) {
			request.addOutputData(propertyResults.areaLotAcres);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_SF && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_SF).metaValue)) {
			request.addOutputData(propertyResults.areaLotSF);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LOT_DEPTH && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_DEPTH).metaValue)) {
			request.addOutputData(propertyResults.lotDepth);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LOT_WIDTH && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_WIDTH).metaValue)) {
			request.addOutputData(propertyResults.lotWidth);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ATTIC_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ATTIC_AREA).metaValue)) {
			request.addOutputData(propertyResults.atticArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ATTIC_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ATTIC_FLAG).metaValue)) {
			request.addOutputData(propertyResults.atticFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA).metaValue)) {
			request.addOutputData(propertyResults.basementArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_FINISHED && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_FINISHED).metaValue)) {
			request.addOutputData(propertyResults.basementAreaFinished);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_UNFINISHED && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_UNFINISHED).metaValue)) {
			request.addOutputData(propertyResults.basementAreaUnfinished);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE).metaValue)) {
			request.addOutputData(propertyResults.parkingGarage);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE_AREA).metaValue)) {
			request.addOutputData(propertyResults.parkingGarageArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT).metaValue)) {
			request.addOutputData(propertyResults.parkingCarport);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT_AREA).metaValue)) {
			request.addOutputData(propertyResults.parkingCarportArea);
		}
	}

	private void outputPool(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POOL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL).metaValue)) {
			request.addOutputData(propertyResults.pool);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POOL_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_AREA).metaValue)) {
			request.addOutputData(propertyResults.poolArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SAUNA_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAUNA_FLAG).metaValue)) {
			request.addOutputData(propertyResults.saunaFlag);
		}
	}

	private void outputUtilities(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_HVAC_COOLING_DETAIL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_COOLING_DETAIL).metaValue)) {
			request.addOutputData(propertyResults.HVACCoolingDetail);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_DETAIL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_DETAIL).metaValue)) {
			request.addOutputData(propertyResults.HVACHeatingDetail);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_FULE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_FULE).metaValue)) {
			request.addOutputData(propertyResults.HVACHeatingFuel);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SEWAGE_USAGE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SEWAGE_USAGE).metaValue)) {
			request.addOutputData(propertyResults.sewageUsage);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_WATER_SOURCE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WATER_SOURCE).metaValue)) {
			request.addOutputData(propertyResults.waterSource);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MOBIL_HOME_HOOKUP_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MOBIL_HOME_HOOKUP_FLAG).metaValue)) {
			request.addOutputData(propertyResults.mobileHomeHookupFlag);
		}
	}

	private void outputParking(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_RV_PARKING_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RV_PARKING_FLAG).metaValue)) {
			request.addOutputData(propertyResults.rvParkingFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PARKING_SPACE_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_SPACE_COUNT).metaValue)) {
			request.addOutputData(propertyResults.parkingSpaceCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_AREA).metaValue)) {
			request.addOutputData(propertyResults.drivewayArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_MATERIAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_MATERIAL).metaValue)) {
			request.addOutputData(propertyResults.drivewayMaterial);
		}
	}

	private void outputYardGarden(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TOPOGRAPHY_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TOPOGRAPHY_CODE).metaValue)) {
			request.addOutputData(propertyResults.topographyCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FENCE_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FENCE_CODE).metaValue)) {
			request.addOutputData(propertyResults.fenceCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FENCE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FENCE_AREA).metaValue)) {
			request.addOutputData(propertyResults.fenceArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_AREA).metaValue)) {
			request.addOutputData(propertyResults.courtyardArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_FLAG).metaValue)) {
			request.addOutputData(propertyResults.courtyardFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ARBOR_PERGOLA_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ARBOR_PERGOLA_FLAG).metaValue)) {
			request.addOutputData(propertyResults.arborPergolaFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SPRINKLERS_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SPRINKLERS_FLAG).metaValue)) {
			request.addOutputData(propertyResults.sprinklersFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GOLF_COURSE_GREEN_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GOLF_COURSE_GREEN_FLAG).metaValue)) {
			request.addOutputData(propertyResults.golfCourseGreenFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_TENNIS_COURT_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TENNIS_COURT_FLAG).metaValue)) {
			request.addOutputData(propertyResults.tennisCourtFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SPORTS_COURSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SPORTS_COURSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.sportsCourtFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ARENA_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ARENA_FLAG).metaValue)) {
			request.addOutputData(propertyResults.arenaFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_WATER_FEATURE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WATER_FEATURE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.waterFeatureFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POND_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POND_FLAG).metaValue)) {
			request.addOutputData(propertyResults.pondFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BOAT_LIFT_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_LIFT_FLAG).metaValue)) {
			request.addOutputData(propertyResults.boatLiftFlag);
		}
	}

	private void outputShape(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;

		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_WELL_KNOWN_TEXT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WELL_KNOWN_TEXT).metaValue)) {
			request.addOutputData(propertyResults.wellKnownText);
		}
	}

	private void outputIntStruct(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FOUNDATION && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FOUNDATION).metaValue)) {
			request.addOutputData(propertyResults.foundation);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCTION && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCTION).metaValue)) {
			request.addOutputData(propertyResults.construction);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_INTERIOR_STRUCTURE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_INTERIOR_STRUCTURE).metaValue)) {
			request.addOutputData(propertyResults.interiorStructure);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PLUMBING_FIXTURE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PLUMBING_FIXTURE).metaValue)) {
			request.addOutputData(propertyResults.plumbingFixturesCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCT_FIRE_RESISTANCE_CLASS && !Const
				.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCT_FIRE_RESISTANCE_CLASS).metaValue)) {
			request.addOutputData(propertyResults.constructionFireResistanceClass);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SAFETY_FIRE_SPRINKLER_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAFETY_FIRE_SPRINKLER_FLAG).metaValue)) {
			request.addOutputData(propertyResults.safetyFireSprinklersFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FLOORING_MATERIAL_PRIMARY && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FLOORING_MATERIAL_PRIMARY).metaValue)) {
			request.addOutputData(propertyResults.flooringMaterialPrimary);
		}
	}

	private void outputIntRoom(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BATH_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_COUNT).metaValue)) {
			request.addOutputData(propertyResults.bathCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BATH_PARTIAL_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_PARTIAL_COUNT).metaValue)) {
			request.addOutputData(propertyResults.bathPartialCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BEDROOMS_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BEDROOMS_COUNT).metaValue)) {
			request.addOutputData(propertyResults.bedroomsCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ROOMS_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOMS_COUNT).metaValue)) {
			request.addOutputData(propertyResults.roomsCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STORIES_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORIES_COUNT).metaValue)) {
			request.addOutputData(propertyResults.storiesCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_UNITS_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNITS_COUNT).metaValue)) {
			request.addOutputData(propertyResults.unitsCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BONUS_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BONUS_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.bonusRoomFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BREAKFAST_NOOK_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BREAKFAST_NOOK_FLAG).metaValue)) {
			request.addOutputData(propertyResults.breakfastNookFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CELLAR_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CELLAR_FLAG).metaValue)) {
			request.addOutputData(propertyResults.cellarFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_WINE_CELLAR_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WINE_CELLAR_FLAG).metaValue)) {
			request.addOutputData(propertyResults.cellarWineFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_EXERCISE_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_EXERCISE_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.excerciseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FAMILY_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FAMILY_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.familyCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GAME_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAME_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.gameFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GREAT_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREAT_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.greatFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_HOBBY_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HOBBY_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.hobbyFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LAUNDRY_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAUNDRY_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.laundryFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MEDIA_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MEDIA_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.mediaFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MUD_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MUD_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.mudFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OFFICE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OFFICE_AREA).metaValue)) {
			request.addOutputData(propertyResults.officeArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OFFICE_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OFFICE_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.officeFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SAFE_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAFE_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.safeRoomFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SITTING_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SITTING_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.sittingFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STORM_SHELTER && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORM_SHELTER).metaValue)) {
			request.addOutputData(propertyResults.stormFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STUDY_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STUDY_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.studyFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SUN_ROOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUN_ROOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.sunroomFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_AREA).metaValue)) {
			request.addOutputData(propertyResults.utilityArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_CODE).metaValue)) {
			request.addOutputData(propertyResults.utilityCode);
		}
	}

	private void outputIntAmenities(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE).metaValue)) {
			request.addOutputData(propertyResults.fireplace);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE_COUNT).metaValue)) {
			request.addOutputData(propertyResults.fireplaceCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ELEVATOR_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ELEVATOR_FLAG).metaValue)) {
			request.addOutputData(propertyResults.accessabilityElevatorFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_HANDICAP_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HANDICAP_FLAG).metaValue)) {
			request.addOutputData(propertyResults.accessabilityHandicapFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ESCALATOR_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESCALATOR_FLAG).metaValue)) {
			request.addOutputData(propertyResults.escalatorFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CENTRAL_VACUUM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CENTRAL_VACUUM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.centralVacuumFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_INTERCOM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_INTERCOM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.intercomFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SOUND_SYSTEM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SOUND_SYSTEM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.soundSystemFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_WET_BAR_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WET_BAR_FLAG).metaValue)) {
			request.addOutputData(propertyResults.wetBarFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SECURITY_ALARM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECURITY_ALARM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.securityAlarmFlag);
		}
	}

	private void outputExtStruct(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STRUCTURE_STYLE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STRUCTURE_STYLE).metaValue)) {
			request.addOutputData(propertyResults.structureStyle);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_EXTERIOR_1_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_EXTERIOR_1_CODE).metaValue)) {
			request.addOutputData(propertyResults.exterior1Code);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ROOF_MATERIAL && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOF_MATERIAL).metaValue)) {
			request.addOutputData(propertyResults.roofMaterial);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_ROOF_CONSTRUCTION && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOF_CONSTRUCTION).metaValue)) {
			request.addOutputData(propertyResults.roofConstruction);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STORM_SHUTTER_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORM_SHUTTER_FLAG).metaValue)) {
			request.addOutputData(propertyResults.stormShutterFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OVERHEAD_DOOR_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OVERHEAD_DOOR_FLAG).metaValue)) {
			request.addOutputData(propertyResults.overheadDoorFlag);
		}
	}

	private void outputExtAmenities(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_VIEW_DESCRIPTION && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_VIEW_DESCRIPTION).metaValue)) {
			request.addOutputData(propertyResults.viewDescription);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PORCH_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PORCH_CODE).metaValue)) {
			request.addOutputData(propertyResults.porchCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PORCH_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PORCH_AREA).metaValue)) {
			request.addOutputData(propertyResults.porchArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_PATIO_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PATIO_AREA).metaValue)) {
			request.addOutputData(propertyResults.patioArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DECK_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DECK_AREA).metaValue)) {
			request.addOutputData(propertyResults.deckArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_DECK_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DECK_FLAG).metaValue)) {
			request.addOutputData(propertyResults.deckFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BALCONY_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BALCONY_FLAG).metaValue)) {
			request.addOutputData(propertyResults.featureBalconyFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BALCONY_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BALCONY_AREA).metaValue)) {
			request.addOutputData(propertyResults.balconyArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BREEZEWAY_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BREEZEWAY_FLAG).metaValue)) {
			request.addOutputData(propertyResults.breezewayFlag);
		}
	}

	private void outputExtBuildings(String key, MDPropertyWebServiceRequest request) {

		PropertyWebServiceFields propertyFields  = serviceMeta.propertyWebServiceFields;
		ProCoderResults          propertyResults = request.proCoderResults;
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BUILDINGS_COUNT && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BUILDINGS_COUNT).metaValue)) {
			request.addOutputData(propertyResults.buildingsCount);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_AREA).metaValue)) {
			request.addOutputData(propertyResults.bathHouseArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.bathHouseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BOAT_ACCESS_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_ACCESS_FLAG).metaValue)) {
			request.addOutputData(propertyResults.boatAccessFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_AREA).metaValue)) {
			request.addOutputData(propertyResults.boatHouseArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.boatHouseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CABIN_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CABIN_AREA).metaValue)) {
			request.addOutputData(propertyResults.cabinArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CABIN_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CABIN_FLAG).metaValue)) {
			request.addOutputData(propertyResults.cabinFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CANOPY_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CANOPY_AREA).metaValue)) {
			request.addOutputData(propertyResults.canopyArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_CANOPY_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CANOPY_FLAG).metaValue)) {
			request.addOutputData(propertyResults.canopyFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_AREA).metaValue)) {
			request.addOutputData(propertyResults.gazeboArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_FLAG).metaValue)) {
			request.addOutputData(propertyResults.gazeboFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_AREA).metaValue)) {
			request.addOutputData(propertyResults.granaryArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_FLAG).metaValue)) {
			request.addOutputData(propertyResults.granaryFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_AREA).metaValue)) {
			request.addOutputData(propertyResults.greenHouseArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.greenHouseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_AREA).metaValue)) {
			request.addOutputData(propertyResults.guestHouseArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.guestHouseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_KENNEL_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_KENNEL_AREA).metaValue)) {
			request.addOutputData(propertyResults.kennelArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_KENNEL_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_KENNEL_FLAG).metaValue)) {
			request.addOutputData(propertyResults.kennelFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_AREA).metaValue)) {
			request.addOutputData(propertyResults.leanToArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_FLAG).metaValue)) {
			request.addOutputData(propertyResults.leanToFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_AREA).metaValue)) {
			request.addOutputData(propertyResults.loadingPlatformArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_FLAG).metaValue)) {
			request.addOutputData(propertyResults.loadingPlatformFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_AREA).metaValue)) {
			request.addOutputData(propertyResults.milkHouseArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.milkHouseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_OUTDOOR_KITCHEN_FIREPLACE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OUTDOOR_KITCHEN_FIREPLACE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.outdoorKitchenFireplaceFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POOL_HOUSE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_AREA).metaValue)) {
			request.addOutputData(propertyResults.poolHouseArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POOL_HOUSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_HOUSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.poolHouseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_AREA).metaValue)) {
			request.addOutputData(propertyResults.poultryHouseArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.poultryHouseFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_QUONSET_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUONSET_AREA).metaValue)) {
			request.addOutputData(propertyResults.quonsetArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_QUONSET_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUONSET_FLAG).metaValue)) {
			request.addOutputData(propertyResults.quonsetFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SHED_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SHED_AREA).metaValue)) {
			request.addOutputData(propertyResults.shedArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SHED_CODE && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SHED_CODE).metaValue)) {
			request.addOutputData(propertyResults.shedCode);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SILO_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SILO_AREA).metaValue)) {
			request.addOutputData(propertyResults.siloArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_SILO_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SILO_FLAG).metaValue)) {
			request.addOutputData(propertyResults.siloFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STABLE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STABLE_AREA).metaValue)) {
			request.addOutputData(propertyResults.stableArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STABLE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STABLE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.stableFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_AREA).metaValue)) {
			request.addOutputData(propertyResults.storageBuildingArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_FLAG).metaValue)) {
			request.addOutputData(propertyResults.storageBuildingFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_AREA).metaValue)) {
			request.addOutputData(propertyResults.utilityBuildingArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_FLAG).metaValue)) {
			request.addOutputData(propertyResults.utilityBuildingFlag);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_AREA && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_AREA).metaValue)) {
			request.addOutputData(propertyResults.poleStructureArea);
		}
		if (propertyFields.included(key) && key == PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_FLAG && !Const.isEmpty(propertyFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_FLAG).metaValue)) {
			request.addOutputData(propertyResults.poleStructureFlag);
		}
	}

	public void processRequests(List<MDPropertyWebServiceRequest> requests, int attempts) throws KettleException {

		PropertyWebServiceFields         pFields                   = serviceMeta.propertyWebServiceFields;
		PropertyWebServiceRequestHandler propertywebserviceHandler = new PropertyWebServiceRequestHandler(pFields);
		try {
			if (pFields.hasMinRequirements()) {

				doWebRequests(propertywebserviceHandler, requests, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				propertywebserviceHandler.webMsg = "Not Licensed";
			} else {
				propertywebserviceHandler.webMsg = e.toString();
			}
			propertywebserviceHandler.webException = e;
			if (!testing) {
				throw propertywebserviceHandler.webException;
			}
		}
	}
}
