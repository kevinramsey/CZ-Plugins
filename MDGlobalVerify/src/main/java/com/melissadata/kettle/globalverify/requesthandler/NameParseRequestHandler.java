package com.melissadata.kettle.globalverify.requesthandler;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalRequest;
import com.melissadata.kettle.globalverify.MDGlobalVerify;
import com.melissadata.kettle.globalverify.data.NameFields;
import com.melissadata.kettle.globalverify.request.NameRequest.NameResults;
import com.melissadata.kettle.globalverify.web.MDGlobalWebService;
import com.melissadata.kettle.globalverify.web.WebRequestHandler;

public class NameParseRequestHandler implements WebRequestHandler, Cloneable {

	public NameFields nameFields;
	public  String              webMsg       = "";
	public  String              webVersion   = "";
	public  KettleException     webException = null;
	private LogChannelInterface log          = null;

	public NameParseRequestHandler(NameFields nFields, LogChannelInterface log) {

		this.log = log;
		this.nameFields = nFields;
	}

	@Override
	public URL getWebURL(MDGlobalData data) {

		return data.realWebGlobalNameVerifierURL;
	}

	@Override
	public boolean buildWebRequest(Document requestDoc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {

		log.logDebug("Name Request Handler build web request. Requests size = " + requests.size());
		Element root = requestDoc.getDocumentElement();
		// Name Verifier options
		addWebOptions(requestDoc, root);
		// Add records
		boolean sendRequest = false;
		Element records     = requestDoc.createElement("Records");
		// Otherwise, add real records
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDGlobalRequest request = requests.get(recordID);
			// TODO: More complete validity checks
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
	public String processWebResponse(org.dom4j.Document doc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {

		log.logDebug("Name Parse process Web Response.  Requests size = " + requests.size() );
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("Response")) {
			throw new KettleException("MDGlobalName: Response not found in XML response string");
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

		log.logRowlevel("Name Parse process Web Response Fields. record: " + record.elementText("RecordID") + " " + record.toString());
		NameResults nameResults = request.nameRequest.nameResults = new NameResults();
		request.resultCodes.addAll(MDGlobalVerify.getResultCodes(MDGlobalWebService.getElementText(record, "Results")));
		// Get the address result element
		org.dom4j.Element name = record;
		if (name == null) {
			throw new KettleException("Could not find Name element in response");
		}
		nameResults.prefix1 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_PREFIX).webTag);
		nameResults.first1 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_FIRST).webTag);
		nameResults.middle1 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_MIDDLE).webTag);
		nameResults.last1 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_LAST).webTag);
		nameResults.suffix1 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_SUFFIX).webTag);
		nameResults.gender1 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_GENDER).webTag);
		nameResults.prefix2 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_PREFIX).webTag);
		nameResults.first2 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_FIRST).webTag);
		nameResults.middle2 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_MIDDLE).webTag);
		nameResults.last2 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_LAST).webTag);
		nameResults.suffix2 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_SUFFIX).webTag);
		nameResults.gender2 = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_GENDER).webTag);
		nameResults.companyName = MDGlobalWebService.getElementText(name, nameFields.outputFields.get(NameFields.TAG_OUTPUT_COMPANY_NAME).webTag);
		nameResults.valid = true;
	}

	protected boolean addWebRequestFields(Document xmlDoc, MDGlobalRequest request, Element record) throws KettleException {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[]         inputData = request.inputData;
		int              inputs    = 0;
		log.logRowlevel("- Name Request Handler add request fields" );
		for (String key : nameFields.inputFields.keySet()) {
			String value = MDGlobalVerify.getFieldString(inputMeta, inputData, nameFields.inputFields.get(key).metaValue);
			MDGlobalWebService.addTextNode(xmlDoc, record, nameFields.inputFields.get(key).webTag, value);
			log.logRowlevel("- Name Request Handler add field -> " +  nameFields.inputFields.get(key).webTag + ":" + value);
			inputs++;
		}

		return inputs != 0;
	}

	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {

		log.logRowlevel("- Name Request Handler add web options" );
		String value = "";
		for (String key : nameFields.optionFields.keySet()) {
			log.logRowlevel("- Name Request Handler add web option -> " +  nameFields.optionFields.get(key).webTag + ":" + nameFields.optionFields.get(key).metaValue);
			value += nameFields.optionFields.get(key).webTag + nameFields.optionFields.get(key).metaValue + ",";
		}
		// drop last comma
		value = value.substring(0, value.length() - 1);
		MDGlobalWebService.addTextNode(xmlDoc, root, "Options", value);
	}

	protected void getInterfaceInfo(org.dom4j.Element response) {
		// Get the interface version
		nameFields.webVersion = MDGlobalWebService.getElementText(response, "Version");
	}

	@Override
	public String getServiceName() {

		return "NAME";
	}

	@Override
	public boolean buildWebRequest(JSONObject jsonRequest, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {

		// Name uses XML
		return false;
	}

	@Override
	public String processWebResponse(JSONObject jsonResponse, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {

		// Name uses XML
		return null;
	}
}
