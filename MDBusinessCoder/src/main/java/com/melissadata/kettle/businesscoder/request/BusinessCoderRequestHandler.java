package com.melissadata.kettle.businesscoder.request;


import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.w3c.dom.DOMException;

import com.melissadata.kettle.businesscoder.MDBusinessCoderData;
import com.melissadata.kettle.businesscoder.MDBusinessCoderStep;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.request.MDBusinessCoderRequest.BusCoderResults;
import com.melissadata.cz.support.MetaVal;

public class BusinessCoderRequestHandler {

	private BusinessCoderFields	busCoderFields;
	public String				webMsg		= "";
	public String				webVersion	= "";
	public KettleException		webException;

	public BusinessCoderRequestHandler(BusinessCoderFields addrFields) {

		busCoderFields = addrFields;
	}

	
	
	@SuppressWarnings("unchecked")
	public boolean buildWebRequest(JSONObject jsonMain, MDBusinessCoderData data, List<MDBusinessCoderRequest> requests) throws KettleException {

		addWebOptions(jsonMain, data);
		JSONArray jsonRequests = new JSONArray();
		// Add records
		boolean sendRequest = false;
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDBusinessCoderRequest request = requests.get(recordID);
			// TODO: More complete validity checks
			boolean valid = true;
			// Add request if it is valid
			if (valid) {
				// Add request fields
				JSONObject jo = new JSONObject();
				//FIXME Add REC ID to do batch when enabled
				//System.out.println("Bus Coder adding record: " + recordID);
				jo.put("rec",  String.valueOf(recordID) );
				if (addWebRequestFields(jo, request)) {
					sendRequest = true;
				}
			// add it to the array
				jsonRequests.add(jo);
			}
		}
		jsonMain.put("Records", jsonRequests);
		return sendRequest;
	}


	public URL getWebURL(MDBusinessCoderData data) {

		return data.realWebBusinessCoderURL;
	}


	public String processWebResponse(JSONObject jsonResponse, MDBusinessCoderData data, List<MDBusinessCoderRequest> requests) throws KettleException {
		// Get the response array
		if (!jsonResponse.containsKey("Records")) { throw new KettleException("MDBusinessCoder: Response not found in XML response string"); }
		// Check the general result
		String resultCodes = (String) jsonResponse.get("TransmissionResults");
// FIXME FE03 remove FE03 part
		if (!Const.isEmpty(resultCodes) /*&& (!resultCodes.contains("FE03"))*/) { return resultCodes; }
		// Get interface info
		getInterfaceInfo(jsonResponse);
		// Get the response records
		JSONArray jArray = (JSONArray) jsonResponse.get("Records");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> i = jArray.iterator();
		while (i.hasNext()) {
			JSONObject innerObj = i.next();
			if (innerObj.containsKey("CompanyName")) {
				// This is used to index the request being processed
				int recordID = 0;
				//FIXME JSON currently no record ID returned
				try {
					recordID = getElementInteger(innerObj, "RecordID");
				} catch (KettleException ke) {
					recordID = 0;
				}

				// Get the request object for the specified record id
				MDBusinessCoderRequest request = requests.get(recordID);
				// Process individual output results
				
				processWebResponseFields(innerObj, request);
//				// FIXME FE03 remove with new service
//				if (resultCodes.contains("FE03"))
//					processWebResponseFields(innerObj, request, "FE03");
//				else
//					processWebResponseFields(innerObj, request, "");
			}
		}
		return "";
	}

	
	@SuppressWarnings("unchecked")
	private boolean addWebRequestFields(JSONObject jObj, MDBusinessCoderRequest request) throws KettleException {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[] inputData = request.inputData;
		int inputs = 0;
		for (String key : busCoderFields.inputFields.keySet()) {
			String value = MDBusinessCoderStep.getFieldString(inputMeta, inputData, busCoderFields.inputFields.get(key).metaValue);
			if(value == null)
				value = "";
			jObj.put(busCoderFields.inputFields.get(key).webTag, value.trim());
			inputs++;

		}
		return inputs != 0;

	}

	
	private void processWebResponseFields(JSONObject record, MDBusinessCoderRequest request) throws KettleException {
		HashMap<String, MetaVal> outputFields = busCoderFields.outputFields;
		BusCoderResults busCoderResults = request.busCoderResults = new BusCoderResults();


		JSONArray contacts = (JSONArray)record.get("Contacts");
		JSONObject contact1 = contacts.size() > 0 ?  (JSONObject)contacts.get(0): null;
		JSONObject contact2 = contacts.size() > 1 ?  (JSONObject)contacts.get(1): null;

		if(contact1 != null){
			busCoderResults.firstName1 =   getElementText(contact1,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_1).webTag);
			busCoderResults.lastName1 =   getElementText(contact1,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_1).webTag);
			busCoderResults.gender1 =   getElementText(contact1,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_1).webTag);
			busCoderResults.title1 =   getElementText(contact1,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_1).webTag);
			busCoderResults.email1 =   getElementText(contact1,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_1).webTag);
		} else {
			busCoderResults.firstName1 =  "";
			busCoderResults.lastName1 =   "";
			busCoderResults.gender1 =   "";
			busCoderResults.title1 =   "";
			busCoderResults.email1 =   "";
		}

		if(contact2 != null){
			busCoderResults.firstName2 =   getElementText(contact2,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_2).webTag);
			busCoderResults.lastName2 =   getElementText(contact2,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_2).webTag);
			busCoderResults.gender2 =   getElementText(contact2,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_2).webTag);
			busCoderResults.title2 =   getElementText(contact2,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_2).webTag);
			busCoderResults.email2 =   getElementText(contact2,busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_2).webTag);
		} else {
			busCoderResults.firstName2 = "";
			busCoderResults.lastName2 = "";
			busCoderResults.gender2 = "";
			busCoderResults.title2 = "";
			busCoderResults.email2 = "";
	}

		// Result code for the individual request
		// FIXME FE03 remove fe03 param
//		if (fe03.length() > 1)
//			request.resultCodes.add(fe03);
		request.resultCodes.addAll(MDBusinessCoderStep.getResultCodes( getElementText(record, busCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_RESULTS).webTag)));

		busCoderResults.addressLine1 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_ADDRESS_LINE1).webTag);
		busCoderResults.suite =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SUITE).webTag);
		busCoderResults.city =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_CITY).webTag);
		busCoderResults.state =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_STATE).webTag);
		busCoderResults.postalCode =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_POSTAL_CODE).webTag);
		busCoderResults.plus4 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLUS_4).webTag);
		busCoderResults.phone =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_PHONE).webTag);
		busCoderResults.webAddress =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_WEB_ADDRESS).webTag);

		busCoderResults.companyName =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_COMPANY_NAME).webTag);
		busCoderResults.employeeEstimate =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMPLOYEES_ESTIMATE).webTag);
		busCoderResults.salesEstimate =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SALES_ESTIMATE).webTag);
		busCoderResults.locationType =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCATION_TYPE).webTag);
		busCoderResults.stockTicker =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_STOCK_TICKER).webTag);
		busCoderResults.ein =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_EIN).webTag);

		busCoderResults.censusBlock =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_BLOCK).webTag);
		busCoderResults.censusTract =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_TRACT).webTag);
		busCoderResults.countyFIPS =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_FIPS).webTag);
		busCoderResults.countyName =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_NAME).webTag);
		busCoderResults.deliveryIndicator =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_DELIVERY_INDICATOR).webTag);
		busCoderResults.Latitude =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_LATITUDE).webTag);
		busCoderResults.Longitude =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_LONGITUDE).webTag);
		busCoderResults.mdAdressKey =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY).webTag);
		busCoderResults.mdAddressKeyBase =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY_BASE).webTag);
		busCoderResults.countryCode =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTRY_CODE).webTag);
		busCoderResults.countryName =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTRY_NAME).webTag);

		busCoderResults.placeName =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_NAME).webTag);
		busCoderResults.placeCode =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_CODE).webTag);

		busCoderResults.naicsCode =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE).webTag);
		busCoderResults.naicsCode2 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE2).webTag);
		busCoderResults.naicsCode3 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE3).webTag);
		busCoderResults.naicsDescription =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION).webTag);
		busCoderResults.naicsDescription2 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION2).webTag);
		busCoderResults.naicsDescription3 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION3).webTag);

		busCoderResults.sicCode =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE).webTag);
		busCoderResults.sicCode2 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE2).webTag);
		busCoderResults.sicCode3 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE3).webTag);
		busCoderResults.sicDescription =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION).webTag);
		busCoderResults.sicDescription2 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION2).webTag);
		busCoderResults.sicDescription3 =  getElementText(record, outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION3).webTag);

		// TODO: Do more complete validity checks
		busCoderResults.valid = true;
	}

	
	@SuppressWarnings("unchecked")
	protected void addWebOptions(JSONObject jObj, MDBusinessCoderData data) throws DOMException {
		String value = "";

		jObj.put("t", MDBusinessCoderMeta.getTransmissionReference());// Transmission Reference
		jObj.put("id", data.realLicense);
		value = busCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_DOMINANT_BUSINESS).webTag + busCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_DOMINANT_BUSINESS).metaValue;
		//value = value + ",MaxContacts:2";
		jObj.put("opt", value);
		value = "GrpAll,Contacts";
		if (Boolean.valueOf(busCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_INCLUDE_CENSUS).metaValue)) {
			value = value + "," + busCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_INCLUDE_CENSUS).webTag;
		}
		jObj.put("cols", value);

	}

	
	public int getElementInteger(JSONObject jsonRecord, String name) throws KettleException {
		try {
			String text = (String) jsonRecord.get(name);
			if (text == null)
				throw new KettleException("Could not find integer value for " + name);
			int value = Integer.valueOf(text);
			return value;
		} catch (NumberFormatException e) {
			throw new KettleException("Problem getting integer value", e);
		}
	}


	public String getElementText(JSONObject element, String name) {
		String text = (String) element.get(name);
		if (text == null)
			return "";
		return text.trim();
	}
	
	
	protected void getInterfaceInfo(JSONObject jsonObj) {
		// Get the interface version
		busCoderFields.webVersion = (String) jsonObj.get("Version");
	}
}
