package com.melissadata.kettle.globalverify.requesthandler;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalRequest;
import com.melissadata.kettle.globalverify.MDGlobalVerify;
import com.melissadata.kettle.globalverify.data.EmailFields;
import com.melissadata.kettle.globalverify.request.EmailRequest.EmailResults;
import com.melissadata.kettle.globalverify.web.MDGlobalWebService;
import com.melissadata.kettle.globalverify.web.WebRequestHandler;

public class EmailRequestHandler implements WebRequestHandler, Cloneable {

	public EmailFields emailFields;
	public  String              webMsg       = "";
	public  String              webVersion   = "";
	public  KettleException     webException = null;
	private LogChannelInterface log          = null;

	public EmailRequestHandler(EmailFields eFields, LogChannelInterface log) {
		this.log = log;
		emailFields = eFields;
	}

	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {
		log.logRowlevel("- Email Request Handler add web options" );
		String value = "";
		for (String key : emailFields.optionFields.keySet()) {
			log.logRowlevel("- Email Request Handler add web option -> " +  emailFields.optionFields.get(key).webTag + ":" + emailFields.optionFields.get(key).metaValue);
			value += emailFields.optionFields.get(key).webTag + emailFields.optionFields.get(key).metaValue + ",";
		}
		// drop last comma
		value = value.substring(0, value.length() - 1);
		MDGlobalWebService.addTextNode(xmlDoc, root, "Options", value);
	}

	protected boolean addWebRequestFields(Document xmlDoc, MDGlobalRequest request, Element record) throws KettleException {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[]         inputData = request.inputData;
		int              inputs    = 0;
		log.logRowlevel("- Email Request Handler add request fields" );
		for (String key : emailFields.inputFields.keySet()) {
			String value = MDGlobalVerify.getFieldString(inputMeta, inputData, emailFields.inputFields.get(key).metaValue);
			MDGlobalWebService.addTextNode(xmlDoc, record, emailFields.inputFields.get(key).webTag, value);
			log.logRowlevel("- Email Request Handler add field -> " +  emailFields.inputFields.get(key).webTag + ":" + value);
			inputs++;
		}

		return inputs != 0;
	}

	@Override
	public boolean buildWebRequest(Document requestDoc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {

		log.logDebug("Email Request Handler build web request. Requests size = " + requests.size());
		Element root = requestDoc.getDocumentElement();
		// email options
		addWebOptions(requestDoc, root);
		// Add records
		boolean sendRequest = false;
		Element records     = requestDoc.createElement("Records");
		// Otherwise, add real records
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDGlobalRequest request = requests.get(recordID);
			// More complete validity checks ??
			boolean valid = true;
			// Add request if it is valid
			if (valid) {
				// Create new record object
				Element record = requestDoc.createElement("RequestRecord");
				// Add unique record id
				MDGlobalWebService.addTextNode(requestDoc, record, "RecordID", "" + recordID);
				// Add request fields
				if (addWebRequestFields(requestDoc, request, record)) {
					// Add the record to the document
					records.appendChild(record);
					// There is at least one request with inputs
					sendRequest = true;
				}
			}
		}
		root.appendChild(records);
		return sendRequest;
	}

	@Override
	public boolean buildWebRequest(JSONObject jsonRequest, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {
		// Email use xml 
		// USE method : buildWebRequest(Document requestDoc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing)
		throw new KettleException("Error Global Email Service uses XML ");
		//return false;
	}

	protected void getInterfaceInfo(org.dom4j.Element response) {
		// Get the interface version
		emailFields.webVersion = MDGlobalWebService.getElementText(response, "Version");
	}

	@Override
	public String getServiceName() {

		return "EMAIL";
	}

	@Override
	public URL getWebURL(MDGlobalData data) {

		return data.realWebGlobalEmailVerifierURL;
	}

	@Override
	public String processWebResponse(JSONObject jsonResponse, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {
		// Email use xml
		return null;
	}

	@Override
	public String processWebResponse(org.dom4j.Document doc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {

		log.logDebug("Email Request Handler process Web Response.  Requests size = " + requests.size() );
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("Response")) {
			throw new KettleException("MDGlobalEmail: Response not found in XML response string");
		}
		// Check the general result
		String resultCodes = MDGlobalWebService.getElementText(response, "Results");
		if (!Const.isEmpty(resultCodes)) {
			return resultCodes;
		}
		// Get interface info
		getInterfaceInfo(response);
		// Get the response records (ignore if testing)
		if (!testing) {
			@SuppressWarnings("unchecked") Iterator<org.dom4j.Element> i = response.elementIterator("Records");
			org.dom4j.Element records = null;
			if (i.hasNext()) {
				records = i.next();
			}
			@SuppressWarnings("unchecked") Iterator<org.dom4j.Element> responseArray = records.elementIterator("ResponseRecord");
			while (responseArray.hasNext()) {
				org.dom4j.Element record = responseArray.next();
				// This is used to index the request being processed
				int recordID = MDGlobalWebService.getElementInteger(record, "RecordID");
				// Get the request object for the specified record id
				MDGlobalRequest request = requests.get(recordID);
				// Process individual output results
				processWebResponseFields(record, request);
			}
		}
		return "";
	}

	protected void processWebResponseFields(org.dom4j.Element record, MDGlobalRequest request) throws KettleException {

		EmailResults emailResults = request.emailRequest.emailResults = new EmailResults();
		request.resultCodes.addAll(MDGlobalVerify.getResultCodes(MDGlobalWebService.getElementText(record, "Results")));
		// Get the address result element
		org.dom4j.Element name = record;
		log.logRowlevel("Email Request Handler process Web Response Fields. record: " + record.elementText("RecordID"));
		if (name == null) {
			throw new KettleException("Could not find Name element in response");
		}
		emailResults.email = MDGlobalWebService.getElementText(name, emailFields.outputFields.get(EmailFields.TAG_OUTPUT_EMAIL).webTag);
		emailResults.boxName = MDGlobalWebService.getElementText(name, emailFields.outputFields.get(EmailFields.TAG_OUTPUT_BOX_NAME).webTag);
		emailResults.domain = MDGlobalWebService.getElementText(name, emailFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN).webTag);
		emailResults.topDomain = MDGlobalWebService.getElementText(name, emailFields.outputFields.get(EmailFields.TAG_OUTPUT_TOP_LEVEL_DOMAIN).webTag);
		emailResults.topDomainDescription = MDGlobalWebService.getElementText(name, emailFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN_DESCRIPTION).webTag);
		emailResults.dateChecked = MDGlobalWebService.getElementText(name, emailFields.outputFields.get(EmailFields.TAG_OUTPUT_DATE_CHECKED).webTag);
		emailResults.valid = true;
	}
}
