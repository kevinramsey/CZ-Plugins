package com.melissadata.kettle.globalverify.requesthandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.melissadata.kettle.globalverify.support.CountryUtil;
import com.melissadata.mdGlobalAddr;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalRequest;
import com.melissadata.kettle.globalverify.MDGlobalVerify;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.request.AddressRequest.AddrResults;
import com.melissadata.kettle.globalverify.web.MDGlobalWebService;
import com.melissadata.kettle.globalverify.web.WebRequestHandler;

public class AddressVerifyRequestHandler implements WebRequestHandler {

	private AddressFields addrFields;
	public  String              webMsg       = "";
	public  String              webVersion   = "";
	public  KettleException     webException = null;
	private LogChannelInterface log          = null;

	public AddressVerifyRequestHandler(AddressFields addrFields, LogChannelInterface log) {

		this.log = log;
		this.addrFields = addrFields;
	}

	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {

		String optString = addrFields.webOptionFields.get(AddressFields.TAG_OPTION_DELIVERY_LINE).webTag + addrFields.webOptionFields.get(AddressFields.TAG_OPTION_DELIVERY_LINE).metaValue;
		optString += "," + addrFields.webOptionFields.get(AddressFields.TAG_OPTION_OUTPUT_SCRIPT).webTag + addrFields.webOptionFields.get(AddressFields.TAG_OPTION_OUTPUT_SCRIPT).metaValue;

		optString += "," + addrFields.webOptionFields.get(AddressFields.TAG_OPTION_LINE_SEPARATOR).webTag + addrFields.webOptionFields.get(AddressFields.TAG_OPTION_LINE_SEPARATOR).metaValue;
		optString += "," + addrFields.webOptionFields.get(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN).webTag + addrFields.webOptionFields.get(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN).metaValue;

		MDGlobalWebService.addTextNode(xmlDoc, root, "Options", optString);
	}

	protected boolean addWebRequestFields(Document xmlDoc, MDGlobalRequest request, Element record) throws KettleException {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[]         inputData = request.inputData;
		int              inputs    = 0;
		for (String key : addrFields.inputFields.keySet()) {
			String value = MDGlobalVerify.getFieldString(inputMeta, inputData, addrFields.inputFields.get(key).metaValue);
			if (!Const.isEmpty(value)) {
				MDGlobalWebService.addTextNode(xmlDoc, record, addrFields.inputFields.get(key).webTag, value);
				inputs++;
			}
			if (key.equals(AddressFields.TAG_INPUT_COUNTRY) && Const.isEmpty(value)) {
				if (!Const.isEmpty(addrFields.defaultCountry)) {
					MDGlobalWebService.addTextNode(xmlDoc, record, addrFields.inputFields.get(key).webTag, addrFields.defaultCountry);
					inputs++;
				}
			}
		}
		return inputs != 0;
	}

	@Override
	public boolean buildWebRequest(Document requestDoc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {

		Element root = requestDoc.getDocumentElement();
		// Address Verifier options
		addWebOptions(requestDoc, root);
		// Add records
		boolean sendRequest = false;
		// If testing, create a fake request record
		if (testing) {// testing
//			// Create new record object
//			Element record = requestDoc.createElement("Records");
//			MDGlobalWebService.addTextNode(requestDoc, record, "RecordID", "1");
//			MDGlobalWebService.addTextNode(requestDoc, record, "AddressLine1", "1600 Pennsylvania Ave.");
//			MDGlobalWebService.addTextNode(requestDoc, record, "Locality", "Washington");
//			MDGlobalWebService.addTextNode(requestDoc, record, "AdministrativeArea", "DC");
//			MDGlobalWebService.addTextNode(requestDoc, record, "PostalCode", "20500");
//			MDGlobalWebService.addTextNode(requestDoc, record, "CountryName", "US");
//			root.appendChild(record);
//			// There is at least one request
//			sendRequest = true;
		} else {
			Element records = requestDoc.createElement("Records");
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
		}
		return sendRequest;
	}

	@Override
	public boolean buildWebRequest(JSONObject jsonRequest, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {

		// Address uses XML
		return false;
	}

	protected void getInterfaceInfo(org.dom4j.Element response) {
		// Get the interface version
		addrFields.webVersion = MDGlobalWebService.getElementText(response, "Version");
	}

	@Override
	public String getServiceName() {

		return "Addr";
	}

	@Override
	public URL getWebURL(MDGlobalData data) {

		return data.realWebGlobalAddressVerifierURL;
	}

	@Override
	public String processWebResponse(JSONObject jsonResponse, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException {

		// Address uses XML
		return null;
	}

	@Override
	public String processWebResponse(org.dom4j.Document doc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("Response")) {
			throw new KettleException("MDGlobalAddress: Response not found in XML response string");
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
			@SuppressWarnings("unchecked") Iterator<org.dom4j.Element> i       = response.elementIterator("Records");
			org.dom4j.Element                                          records = null;
			if (i.hasNext()) {
				records = i.next();
			}
			@SuppressWarnings("unchecked") Iterator<org.dom4j.Element> responseArray = records.elementIterator("ResponseRecord");
			log.logDebug("Processing Addr Web Requests. Response Array size = " + records.nodeCount());
			while (responseArray.hasNext()) {
				org.dom4j.Element record = responseArray.next();
				// This is used to index the request being processed
				int recordID = MDGlobalWebService.getElementInteger(record, "RecordID");
				// Get the request object for the specified record id
				MDGlobalRequest request = requests.get(recordID);
				log.logRowlevel("Web Addr Request processed : " + recordID + request.outputData.toString());
				// Process individual output results
				processWebResponseFields(record, request);
			}
		}
		return "";
	}

	protected void processWebResponseFields(org.dom4j.Element record, MDGlobalRequest request) throws KettleException {

		AddrResults addrResults = request.addressRequest.addrResults = new AddrResults();
		// Result code for the individual request
		request.resultCodes.addAll(MDGlobalVerify.getResultCodes(MDGlobalWebService.getElementText(record, "Results")));
		if (addrFields.addAdditionalCode) {
			request.resultCodes.add("WP01");
		}
		// Get the address result element
		org.dom4j.Element address = record;
		if (address == null) {
			throw new KettleException("Could not find Address element in response");
		}
		// Extract the rest of the results
		//2 Output Address Parameters
		addrResults.Organization = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ORGANIZATION).webTag);
		addrResults.Address1 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE1).webTag);
		addrResults.Address2 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE2).webTag);
		addrResults.Address3 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE3).webTag);
		addrResults.Address4 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE4).webTag);
		addrResults.Address5 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE5).webTag);
		addrResults.Address6 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE6).webTag);
		addrResults.Address7 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE7).webTag);
		addrResults.Address8 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE8).webTag);
		addrResults.DeliveryLine = "";// no web
		addrResults.FormattedAddress = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_FORMATED_ADDRESS).webTag);
		// 3 Parsed Sub-Premises Parameters
		addrResults.Building = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_BUILDING).webTag);
		addrResults.SubBuilding = "";// no web
		addrResults.SubBuildingNumber = "";// no web
		addrResults.SubBuildingType = "";// no web
		addrResults.SubPremises = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES).webTag);
		addrResults.SubPremisesNumber = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_NUMBER).webTag);
		addrResults.SubPremisesType = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_TYPE).webTag);
		addrResults.SubPremisesLevel = "";// no web
		addrResults.SubPremisesLevelNumber = "";// no web
		addrResults.SubPremisesLevelType = "";// no web
		//4 Parsed Thoroughfare Parameters
		addrResults.Premises = "";// no web
		addrResults.PremisesNumber = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_NUMBER).webTag);
		addrResults.PremisesType = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_TYPE).webTag);
		addrResults.Thoroughfare = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE).webTag);
		addrResults.ThoroughfareLeadingType = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LEADING_TYPE).webTag);
		addrResults.ThoroughfareName = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE_NAME).webTag);
		addrResults.ThoroughfarePostDirection = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).webTag);
		addrResults.ThoroughfarePreDirection = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).webTag);
		addrResults.ThoroughfareTrailingType = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TRAILING_TYPE).webTag);
		addrResults.ThoroughfareTypeAttached = "";// no web
		//5 Parsed Dependent Thoroughfare Columns
		addrResults.DependentThoroughfare = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE).webTag);
		addrResults.DependentThoroughfareLeadingType = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE).webTag);
		addrResults.DependentThoroughfareName = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME).webTag);
		addrResults.DependentThoroughfarePostDirection = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).webTag);
		addrResults.DependentThoroughfarePreDirection = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).webTag);
		addrResults.DependentThoroughfareTrailingType = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE).webTag);
		addrResults.DependentThoroughfareTypeAttached = "";// no web
		//6 Parsed Postal Facility Columns
		addrResults.PostBox = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTBOX).webTag);
		addrResults.PostalCode = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTAL_CODE).webTag);
		addrResults.PersonalID = "";// no web
		addrResults.PostOfficeLocation = "";// no web
		//7 Parsed Regional Columns
		addrResults.AdministrativeArea = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADMINISTRATIVE_AREA).webTag);
		addrResults.CountyName = "";// no web
		addrResults.DependentLocality = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_LOCALITY).webTag);
		addrResults.DoubleDependentLocality = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY).webTag);
		addrResults.Locality = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LOCALITY).webTag);
		addrResults.SubAdministrativeArea = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA).webTag);
		addrResults.SubNationalArea = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_NATIONAL_AREA).webTag);
		//8 Extra Output Address Parameters
		addrResults.AddressTypeCode = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_TYPE).webTag);
		addrResults.Latitude = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LATITUDE).webTag);
		addrResults.Longitude = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LONGITUDE).webTag);
		addrResults.AddressKey = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_KEY).webTag);
		//9 Extra Output CountryName Parameters
		addrResults.CountryCode = "";// no web
		addrResults.CountrySubdivisionCode = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE).webTag);
		addrResults.CountryTimeZone = ""; // no web
		addrResults.CountryUTC = ""; // no web
		addrResults.CountryName = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_NAME).webTag);
		addrResults.CountryISOAlpha2 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA2).webTag);
		addrResults.CountryISOAlpha3 = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA3).webTag);
		addrResults.CountryISONumeric = MDGlobalWebService.getElementText(address, addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_NUMERIC).webTag);
		addrResults.CountryFormalName = "";// no web

		// TODO: Do more complete validity checks
		addrResults.valid = true;
	}

	public String processLocalResponse(mdGlobalAddr globalAddrObject, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException {
		// Get the request object for the specified record id
		log.logDebug("Processing Local Address Requests size = " + requests.size());
		int recordID = 0;
		for (recordID = 0; recordID < requests.size(); recordID++) {
			MDGlobalRequest request = requests.get(recordID);
			log.logRowlevel("- Submitting Local Address request # " + recordID);
			setInputs(globalAddrObject, request);
			globalAddrObject.VerifyAddress();
			log.logRowlevel("- Local Address Request processed : " + recordID);

			// Process individual output results
			processLocalResponseFields(globalAddrObject, request);
		}

		return "";
	}

	public void setInputs(mdGlobalAddr globalAddrObject, MDGlobalRequest request) {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[]         inputData = request.inputData;

		globalAddrObject.ClearProperties();
		if (!addrFields.webOptionFields.get(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN).metaValue.isEmpty()) {
			globalAddrObject.SetInputParameter(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN, addrFields.webOptionFields.get(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN).metaValue);
		}
		globalAddrObject.SetInputParameter("outputPreference", addrFields.webOptionFields.get(AddressFields.TAG_OPTION_OUTPUT_SCRIPT).metaValue);
		globalAddrObject.SetInputParameter("inputFormattedAddressSeparator", getLineSeperator(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_LINE_SEPARATOR).metaValue));

		log.logRowlevel("- globalAddrObject - setting input values . . .");
		String value = "";
		for (String key : addrFields.inputFields.keySet()) {
			try {
				value = MDGlobalVerify.getFieldString(inputMeta, inputData, addrFields.inputFields.get(key).metaValue);

				if (value != null && key.equals(addrFields.TAG_INPUT_COUNTRY)) {
					value = CountryUtil.getCountryISO2(value);
					//System.out.println(" *** ****** ****  Vaflue set to -- " + value);
				}
			} catch (KettleValueException kve) {
				value = "";
				System.out.println("Error reading value in Set Inputs: " + kve.getMessage());
			}

			if (!Const.isEmpty(value)) {
				log.logRowlevel("- globalAddrObject - Set Value ->  " + addrFields.inputFields.get(key).webTag + ":" + value);
				globalAddrObject.SetInputParameter("input" + addrFields.inputFields.get(key).webTag, value);
			}

			if (key.equals(AddressFields.TAG_INPUT_COUNTRY) && Const.isEmpty(value)) {
				if (!Const.isEmpty(addrFields.defaultCountry)) {
					log.logRowlevel("- globalAddrObject - (Default Country)Set Value ->  " + addrFields.inputFields.get(key).webTag + ":" + value);
					globalAddrObject.SetInputParameter("input" + addrFields.inputFields.get(key).webTag, addrFields.defaultCountry);
				}
			}
		}
	}

	public void processLocalResponseFields(mdGlobalAddr globalAddrObject, MDGlobalRequest request) throws KettleException {

		AddrResults addrResults = request.addressRequest.addrResults = new AddrResults();
		String      reCodes     = globalAddrObject.GetOutputParameter("resultCodes");
		request.resultCodes.addAll(Arrays.asList(reCodes.split(",")));
		if (addrFields.addAdditionalCode) {
			request.resultCodes.add("LP01");
		}
		log.logRowlevel("globalAddrObject - Get output parameters.");
		//2 Output Address Parameters
		addrResults.Organization = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ORGANIZATION).webTag);
		addrResults.Address1 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE1).webTag);
		addrResults.Address2 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE2).webTag);
		addrResults.Address3 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE3).webTag);
		addrResults.Address4 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE4).webTag);
		addrResults.Address5 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE5).webTag);
		addrResults.Address6 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE6).webTag);
		addrResults.Address7 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE7).webTag);
		addrResults.Address8 = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE8).webTag);
		//
		addrResults.CountryName = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_NAME).webTag);
		addrResults.DependentLocality = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_LOCALITY).webTag);
		addrResults.Locality = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LOCALITY).webTag);
		addrResults.AdministrativeArea = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADMINISTRATIVE_AREA).webTag);
		addrResults.PostalCode = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTAL_CODE).webTag);

		addrResults.FormattedAddress = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_FORMATED_ADDRESS).webTag);
		String cds = globalAddrObject.GetOutputParameter("addressTypeCode");
		if (cds.isEmpty()) {
			cds = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_TYPE).webTag);
		}
		addrResults.AddressTypeCode = cds;
		addrResults.AddressKey = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_KEY).webTag);
		// 3 Parsed Sub-Premises Parameters
		addrResults.Building = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_BUILDING).webTag);
		addrResults.SubBuilding = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING).webTag);
		addrResults.SubBuildingNumber = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_NUMBER).webTag);
		addrResults.SubBuildingType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_TYPE).webTag);
		addrResults.SubPremises = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES).webTag);
		addrResults.SubPremisesNumber = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_NUMBER).webTag);
		addrResults.SubPremisesType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_TYPE).webTag);
		addrResults.SubPremisesLevel = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL).webTag);
		addrResults.SubPremisesLevelNumber = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_NUMBER).webTag);
		addrResults.SubPremisesLevelType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_TYPE).webTag);
		//4 Parsed Thoroughfare Parameters
		addrResults.Premises = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES).webTag);
		addrResults.PremisesNumber = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_NUMBER).webTag);
		addrResults.PremisesType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_TYPE).webTag);
		addrResults.Thoroughfare = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE).webTag);
		addrResults.ThoroughfareLeadingType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LEADING_TYPE).webTag);
		addrResults.ThoroughfareName = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE_NAME).webTag);
		addrResults.ThoroughfarePostDirection = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_DIRECTION).webTag);
		addrResults.ThoroughfarePreDirection = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PRE_DIRECTION).webTag);
		addrResults.ThoroughfareTrailingType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TRAILING_TYPE).webTag);
		addrResults.ThoroughfareTypeAttached = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TYPE_ATTACHED).webTag);
		//5 Parsed Dependent Thoroughfare Columns
		addrResults.DependentThoroughfare = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE).webTag);
		addrResults.DependentThoroughfareLeadingType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE).webTag);
		addrResults.DependentThoroughfareName = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME).webTag);
		addrResults.DependentThoroughfarePostDirection = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).webTag);
		addrResults.DependentThoroughfarePreDirection = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).webTag);
		addrResults.DependentThoroughfareTrailingType = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE).webTag);
		addrResults.DependentThoroughfareTypeAttached = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TYPE_ATTACHED).webTag);
		//6 Parsed Postal Facility Columns
		addrResults.PostBox = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTBOX).webTag);
		addrResults.PersonalID = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PERSONAL_ID).webTag);
		addrResults.PostOfficeLocation = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_OFFICE_LOCATION).webTag);
		//7 Parsed Regional Columns
		addrResults.CountyName = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTY_NAME).webTag);
		addrResults.DoubleDependentLocality = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY).webTag);
		addrResults.SubAdministrativeArea = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA).webTag);
		addrResults.SubNationalArea = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_NATIONAL_AREA).webTag);
		//8 Extra Output Address Parameters
		addrResults.CountryFormalName = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_FORMAL_NAME).webTag);
		addrResults.CountryCode = "";// no Local
		addrResults.CountrySubdivisionCode = globalAddrObject.GetOutputParameter(addrFields.TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE);
		addrResults.CountryTimeZone = globalAddrObject.GetOutputParameter(addrFields.TAG_OUTPUT_COUNTRY_TIMEZONE);
		addrResults.CountryUTC = globalAddrObject.GetOutputParameter(addrFields.TAG_OUTPUT_COUNTRY_UTC);
		addrResults.CountryISOAlpha2 = globalAddrObject.GetOutputParameter(addrFields.TAG_LOCAL_COUNTRY_ISO_ALPHA2);
		addrResults.CountryISOAlpha3 = globalAddrObject.GetOutputParameter(addrFields.TAG_LOCAL_COUNTRY_ISO_ALPHA3);
		addrResults.CountryISONumeric = globalAddrObject.GetOutputParameter(addrFields.TAG_LOCAL_COUNTRY_ISO_NUMERIC);
		addrResults.Latitude = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LATITUDE).webTag);
		addrResults.Longitude = globalAddrObject.GetOutputParameter(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LONGITUDE).webTag);

		String sep   = addrFields.getRealLineSeporator();
		String dLine = "";
		if(!Const.isEmpty(addrResults.SubPremises)){
			dLine = addrResults.SubPremises;
		}
		if(!Const.isEmpty(addrResults.PremisesNumber)){
			if(Const.isEmpty(dLine)){
				dLine += addrResults.PremisesNumber;
			} else {
				dLine += sep + addrResults.PremisesNumber;
			}
		}
		if(!Const.isEmpty(addrResults.Thoroughfare)){
			dLine += " " + addrResults.Thoroughfare ;
		}
		if(!Const.isEmpty(addrResults.DependentLocality)){
			dLine += sep + addrResults.DependentLocality;
		}

		addrResults.DeliveryLine = dLine;

		log.logRowlevel("globalAddrObject  output -> " + addrResults.toString());
//		// TODO: Do more complete validity checks
		addrResults.valid = true;
	}

	private String getLineSeperator(String lineSeperator) {

		if ("SEMICOLON".equals(lineSeperator)) {
			lineSeperator = ";";
		} else if ("PIPE".equals(lineSeperator)) {
			lineSeperator = "|";
		} else if ("CR".equals(lineSeperator)) {
			lineSeperator = "\r";
		} else if ("LF".equals(lineSeperator)) {
			lineSeperator = "\f";
		} else if ("CRLF".equals(lineSeperator)) {
			lineSeperator = "\r\f";
		} else if ("TAB".equals(lineSeperator)) {
			lineSeperator = "\t";
		} else if ("<BR>".equals(lineSeperator)) {
			lineSeperator = "<BR>";
		} else {
			lineSeperator = ";";
		}
		return lineSeperator;
	}
}
