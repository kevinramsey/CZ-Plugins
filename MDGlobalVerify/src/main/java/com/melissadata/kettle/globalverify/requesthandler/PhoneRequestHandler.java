package com.melissadata.kettle.globalverify.requesthandler;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalRequest;
import com.melissadata.kettle.globalverify.MDGlobalVerify;
import com.melissadata.kettle.globalverify.data.PhoneFields;
import com.melissadata.kettle.globalverify.request.PhoneRequest.PhoneResults;
import com.melissadata.kettle.globalverify.web.WebRequestHandler;

public class PhoneRequestHandler implements Cloneable, WebRequestHandler {

	public static int getElementInteger(JSONObject jsonRecord, String name) throws KettleException {

		try {
			String text = (String) jsonRecord.get(name);
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
	public static String getElementText(JSONObject element, String name) {

		String text = (String) element.get(name);
		if (text == null) {
			return "";
		}
		return text.trim();
	}

	public  PhoneFields         phoneFields  = null;
	public  String              webMsg       = "";
	public  String              webVersion   = "";
	public  KettleException     webException = null;
	private LogChannelInterface log          = null;

	public PhoneRequestHandler(PhoneFields pFields, LogChannelInterface log) {
		this.log = log;
		phoneFields = pFields;
	}

	@SuppressWarnings("unchecked")
	protected void addWebOptions(JSONObject jsonRequest) throws DOMException {
		log.logRowlevel("- Phone Request Handler add web options" );
		String value = "";
		for (String key : phoneFields.optionFields.keySet()) {
			log.logRowlevel("- Email Request Handler add web option -> " +  phoneFields.optionFields.get(key).webTag + ":" + phoneFields.optionFields.get(key).metaValue);
			value += phoneFields.optionFields.get(key).webTag + ":" + phoneFields.optionFields.get(key).metaValue + ",";
		}

		value = value.substring(0, value.length() - 1);
		jsonRequest.put("Options", value);
	}

	@SuppressWarnings("unchecked")
	protected boolean addWebRequestFields(JSONArray jsonArray, MDGlobalRequest request, int recID) throws KettleException {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[]         inputData = request.inputData;
		int              inputs    = 0;
		log.logRowlevel("- Phone Request Handler add request fields" );
		JSONObject jo = new JSONObject();
		String     id = String.valueOf(recID);
		jo.put("RecordID", id);
		for (String key : phoneFields.inputFields.keySet()) {
			String value = null;
			if (key.equals(PhoneFields.TAG_INPUT_DIALING_COUNTRY)) {
				value = phoneFields.inputFields.get(key).metaValue;
			} else {
				value = MDGlobalVerify.getFieldString(inputMeta, inputData, phoneFields.inputFields.get(key).metaValue);
			}

			if (!Const.isEmpty(value)) {
				log.logRowlevel("- Phone Request Handler add field -> " +  phoneFields.inputFields.get(key).webTag + ":" + value);
				jo.put(phoneFields.inputFields.get(key).webTag, value);
				inputs++;
			}
			if (key.equals(PhoneFields.TAG_INPUT_COUNTRY) && Const.isEmpty(value)) {
				if (!Const.isEmpty(phoneFields.inputFields.get(PhoneFields.TAG_INPUT_DEFAULT_COUNTRY).metaValue)) {
					log.logRowlevel("- Phone Request Handler add (Default Country) field -> " +  phoneFields.inputFields.get(key).webTag + ":" + value);
					jo.put(phoneFields.inputFields.get(PhoneFields.TAG_INPUT_COUNTRY).webTag, phoneFields.inputFields.get(PhoneFields.TAG_INPUT_DEFAULT_COUNTRY).metaValue);
					inputs++;
				}
			}
		}

		if (inputs > 0) {
			jsonArray.add(jo);
		}

		return inputs != 0;
	}

	@Override
	public boolean buildWebRequest(Document requestDoc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {
		// Global Phone V4 use JSON
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean buildWebRequest(JSONObject jsonRequest, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {

		log.logDebug("Phone Request Handler build web request. Requests size = " + requests.size());
		addWebOptions(jsonRequest);
		boolean   sendRequest = false;
		JSONArray jsonArray   = new JSONArray();
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDGlobalRequest request = requests.get(recordID);
			// TODO: More complete validity checks
			boolean valid = true;
			// Add request if it is valid
			if (valid) {
				// Add request fields
				if (addWebRequestFields(jsonArray, request, recordID)) {
					sendRequest = true;
				}
			}
		}

		jsonRequest.put("Records", jsonArray);

		return sendRequest;
	}

	protected void getInterfaceInfo(JSONObject response) {
		// Get the interface version
		phoneFields.webVersion = getElementText(response, "Version");
	}

	@Override
	public String getServiceName() {

		return "PHONE";
	}

	@Override
	public URL getWebURL(MDGlobalData data) {

		return data.realWebGlobalPhoneVerifierURL;
	}

	@Override
	public String processWebResponse(JSONObject jsonResponse, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {
		log.logDebug("Phone Request Handler process Web Response.  Requests size = " + requests.size() );
		// Get the response array
		if (!jsonResponse.containsKey("Records")) {
			throw new KettleException("MDGlobalVerify: Response not found in JSON response");
		}
		// Check the general result
		String resultCodes = (String) jsonResponse.get("TransmissionResults");
		if (!Const.isEmpty(resultCodes)) {
			return resultCodes;
		}
		// Get interface info
		getInterfaceInfo(jsonResponse);
		// Get the response records
		JSONArray                                           jArray = (JSONArray) jsonResponse.get("Records");
		@SuppressWarnings("unchecked") Iterator<JSONObject> i      = jArray.iterator();
		while (i.hasNext()) {
			JSONObject innerObj = i.next();
			if (innerObj.containsKey("PhoneNumber")) {
				// This is used to index the request being processed
				int recordID = 0;
				try {
					recordID = getElementInteger(innerObj, "RecordID");
				} catch (KettleException ke) {
					recordID = 0;
				}

				// Get the request object for the specified record id
				MDGlobalRequest request = requests.get(recordID);

				// Process individual output results
				processWebResponseFields(innerObj, request);
			}
		}
		return resultCodes;
	}

	@Override
	public String processWebResponse(org.dom4j.Document doc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {
		// Global Phone V4 use JSON
		return "";
	}

	protected void processWebResponseFields(JSONObject record, MDGlobalRequest request) throws KettleException {

		PhoneResults phoneResults = request.phoneRequest.phoneResults = new PhoneResults();
		log.logRowlevel("Phone Request Handler process Web Response Fields. record: " + getElementInteger(record, "RecordID"));
		// Result code for the individual request
		request.resultCodes.addAll(MDGlobalVerify.getResultCodes(getElementText(record, "Results")));
		// Get the address result element
		phoneResults.phoneNumber = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_PHONE).webTag);
		phoneResults.subscriber = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUBSCRIBER).webTag);
		phoneResults.carrier = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_CARRIER).webTag);
		phoneResults.country = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY).webTag);
		phoneResults.countryAbbriviation = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_ABBREVIATION).webTag);
		phoneResults.dialingCode = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_CODE).webTag);
		phoneResults.internationalPrefix = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_PREFIX).webTag);
		phoneResults.nationalPrefix = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_NATIONAL_PREFIX).webTag);
		phoneResults.destCode = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_DEST_CODE).webTag);
		phoneResults.locality = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LOCALITY).webTag);
		phoneResults.AdminArea = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_ADMIN_AREA).webTag);
		phoneResults.language = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LANGUAGE).webTag);
		phoneResults.utc = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_UTC).webTag);
		phoneResults.dst = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_DST).webTag);
		phoneResults.latitude = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LATITUDE).webTag);
		phoneResults.longitude = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LONGITUDE).webTag);

		phoneResults.callerID = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_CALLER_ID).webTag);

		phoneResults.internationalNumber = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_NUMBER).webTag);
		phoneResults.postalCode = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_POSTAL_CODE).webTag);

		phoneResults.Suggestions = getElementText(record, phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUGGESTIONS).webTag);
		phoneResults.valid = true;
	}

	protected void processWebResponseFields(org.dom4j.Element record, MDGlobalRequest request) throws KettleException {
		// Phone V4 use JSON
	}
}
