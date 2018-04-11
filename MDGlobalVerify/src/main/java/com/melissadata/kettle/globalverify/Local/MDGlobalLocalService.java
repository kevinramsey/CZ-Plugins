package com.melissadata.kettle.globalverify.Local;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.kettle.globalverify.*;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.data.EmailFields;
import com.melissadata.kettle.globalverify.data.NameFields;
import com.melissadata.kettle.globalverify.data.PhoneFields;
import com.melissadata.kettle.globalverify.error.MDAbortException;
import com.melissadata.kettle.globalverify.requesthandler.AddressVerifyRequestHandler;
import com.melissadata.kettle.globalverify.requesthandler.EmailRequestHandler;
import com.melissadata.kettle.globalverify.requesthandler.NameParseRequestHandler;
import com.melissadata.kettle.globalverify.requesthandler.PhoneRequestHandler;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.globalverify.web.MDGlobalWebService;
import com.melissadata.kettle.globalverify.web.WebClient;
import com.melissadata.kettle.globalverify.web.WebRequestHandler;
import com.melissadata.mdGlobalAddr;
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
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Kevin on 8/28/2017.
 */
public class MDGlobalLocalService extends MDGlobalService {

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

	private static Class<?>          PKG             = MDGlobalWebService.class;
	private        DOMImplementation domImplentation = null;
	private        Transformer       serializer      = null;
	private        SAXReader         saxReader       = null;
	private        WebClient         webClient       = null;
	private        WebClient         cvsClient       = null;
	private static String            errorMessage    = null;
	private        mdGlobalAddr      globalAddrObj   = null;

	public MDGlobalLocalService(MDGlobalData checkData, MDGlobalMeta checkMeta, VariableSpace space, LogChannelInterface log) {

		super(checkData, checkMeta, space, log);
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
	public IRequestManager createRequestManager() {
		// If this is a local service then we only allow one thread in the thread pool.
		return new RequestManager(1);
	}

	@Override
	public MDGlobalRequest buildRequest(IOMetaHandler ioMeta, Object[] inputData) {

		return new MDGlobalRequest(ioMeta, inputData);
	}

	@Override
	public boolean checkProxy() {

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
		//
		return 0;
	}

	@Override
	public void dispose() {
		// Close the web clients (if there are any)
		if (webClient != null) {
			webClient.close();
		}
		if (cvsClient != null) {
			cvsClient.close();
		}
		log.logDebug("Local Service Deleting globalAddr object");
		if (globalAddrObj != null) {
			globalAddrObj.delete();
			globalAddrObj = null;
		}
		// Call parent handler
		super.dispose();
	}

	private void doLocalRequests(WebRequestHandler handler, List<MDGlobalRequest> requests, int attempts) throws KettleException {

		((AddressVerifyRequestHandler) handler).processLocalResponse(globalAddrObj, checkData, requests, testing);
	}

	private String getCustomerID() {

		String id     = "";
		int    retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, "0"));
		if ((retVal & MDPropTags.MDLICENSE_GlobalVerify) != 0) {
			id = MDProps.getProperty(MDPropTags.TAG_PRIMARY_ID, "");
		} else {
			retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RET_VAL, "0"));
			if ((retVal & MDPropTags.MDLICENSE_GlobalVerify) != 0) {
				id = MDProps.getProperty(MDPropTags.TAG_TRIAL_ID, "");
			}
		}
		return id;
	}

	private synchronized JSONParser getParser() {

		return new JSONParser();
	}

	@Override
	public void init() throws KettleException {
		// Call parent handler first
		super.init();
		globalAddrInit();
	}

	private void globalAddrInit() throws KettleException {

		try {
			log.logBasic("Initializing  globalAddrObject ");
			globalAddrObj = DQTObjectFactory.newGlobalAddr();

			globalAddrObj.SetLicenseString(getLicenseString());
			globalAddrObj.SetPathToGlobalAddrFiles(getDataPath());
			mdGlobalAddr.ProgramStatus status = globalAddrObj.InitializeDataFiles();
			if (status != mdGlobalAddr.ProgramStatus.ErrorNone) {
				throw new KettleException("Failed to initialize Global Address Object : " + globalAddrObj.GetOutputParameter("initializeErrorString"));
			}
		} catch (DQTObjectException dqtE) {
			throw new KettleException("Global Address Local Service failed to intiialize", dqtE);
		}
	}

	private String getLicenseString() {

		return MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "").trim();
	}

	private String getDataPath() {

		return MDProps.getProperty(MDPropTags.TAG_LOCAL_DATA_PATH, "");
	}

	@Override
	public void outputData(List<MDGlobalRequest> requests, int queue) {
//		if (checkMeta.getNameMeta().getNameFields().hasMinRequirements()) {
//			checkMeta.getNameMeta().outputData(checkData, requests);
//		}
		if (checkMeta.getAddrMeta().getAddrFields().hasMinRequirements()) {
			GlobalAddressEngine globalEngine = new GlobalAddressEngine(checkMeta.getAddrMeta().getAddrFields(), log);
			globalEngine.outputData(checkData, requests);
		}
//		if (checkMeta.getPhoneMeta().getPhoneFields().hasMinRequirements()) {
//			checkMeta.getPhoneMeta().outputData(checkData, requests);
//		}
//		if (checkMeta.getEmailMeta().getEmailFields().hasMinRequirements()) {
//			checkMeta.getEmailMeta().outputData(checkData, requests);
//		}
		addResultCodes(requests);
	}

	/**
	 * Called to process address requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processAddrRequests(List<MDGlobalRequest> requests, int attempts) throws KettleException {

		AddressFields               gaf           = checkMeta.getAddrMeta().getAddrFields();
		AddressVerifyRequestHandler addressVerify = new AddressVerifyRequestHandler(gaf, log);

		try {
			// process only if address verification is enabled
			if (gaf.hasMinRequirements()) {
				doLocalRequests(addressVerify, requests, attempts);
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
				doLocalRequests(handler, requests, attempts);
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
				doLocalRequests(handler, requests, attempts);
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
				doLocalRequests(handler, requests, attempts);
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

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDGlobalAddressService#processRequests()
	 */
	@Override
	public void processRequests(List<MDGlobalRequest> requests, int queue, int attempts) throws KettleException {
		// Start point ok
		// Process the address verifier requests
//		if (checkMeta.getNameMeta().getNameFields().hasMinRequirements()) {
//			processNameRequests(requests, attempts);
//		}
		if (checkMeta.getAddrMeta().getAddrFields().hasMinRequirements()) {
			processAddrRequests(requests, attempts);
		}
//		if (checkMeta.getPhoneMeta().getPhoneFields().hasMinRequirements()) {
//			processPhoneRequests(requests, attempts);
//		}
//		if (checkMeta.getEmailMeta().getEmailFields().hasMinRequirements()) {
//			processEmailRequests(requests, attempts);
//		}
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
