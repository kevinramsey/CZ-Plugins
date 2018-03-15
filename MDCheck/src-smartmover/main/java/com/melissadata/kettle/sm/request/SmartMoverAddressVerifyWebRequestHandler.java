package com.melissadata.kettle.sm.request;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.cv.MDCheckWebService;
import com.melissadata.kettle.cv.address.AbstractAddressVerifyWebRequestHandler;
import com.melissadata.kettle.sm.SmartMoverMeta;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SmartMoverAddressVerifyWebRequestHandler extends AbstractAddressVerifyWebRequestHandler {
	private SmartMoverMeta smMeta;

	public SmartMoverAddressVerifyWebRequestHandler(SmartMoverMeta smMeta) {
		this.smMeta = smMeta;
	}

	@Override
	public boolean buildWebRequest(Document xmlDoc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		Element root = xmlDoc.getDocumentElement();
		addWebOptions(xmlDoc, root);
		boolean sendRequest = false;
		Element recordSet = xmlDoc.createElement("Records");
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDCheckCOARequest request = (MDCheckCOARequest) requests.get(recordID);
			// TODO: More complete validity checks
			boolean valid = true;
			// Add request if it is valid
			if (valid) {
				// Create new record object
				Element record = xmlDoc.createElement("RequestRecord");
				// Add unique record id
				MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);
				// Add request fields
				if (addSMWebRequestFields(xmlDoc, request, record)) {
					// Add the record to the document
					recordSet.appendChild(record);
					// There is at least one request with inputs
					sendRequest = true;
				}
			}
		}
		root.appendChild(recordSet);
		return sendRequest;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURLCVS(com.melissadata.kettle.MDCheckData)
	 */
	@Override
	public URL getCVSURL(MDCheckData data) {
		// FIXME no cvs for smart mover should we throw error here
		return null;
	}

	public String getServiceName() {
		return "Smart Mover Address Verify";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDCheckData)
	 */
	@Override
	public URL getWebURL(MDCheckData data, int queue) {
		if (queue == 0) {
			return data.realNCOAURL;
		} else {
			return data.realCCOAURL;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#processWebResponse(org.dom4j.Document, com.melissadata.kettle.MDCheckData,
	 * boolean)
	 */
	@Override
	public String processWebResponse(org.dom4j.Document doc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("Response")) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.ResponseNotFound")); }
		// Check the general result
		String resultCodes = MDCheckWebService.getElementText(response, "TransmissionResults");
		if (!Const.isEmpty(resultCodes)) { return resultCodes; }
		// Get interface info
		getInterfaceInfo(response);
		// Get the response records (ignore if testing)
		if (!testing) {
			org.dom4j.Element responseX = response.element("Records");
			@SuppressWarnings("unchecked")
			Iterator<org.dom4j.Element> i = responseX.elementIterator("ResponseRecord");
			while (i.hasNext()) {
				org.dom4j.Element record = i.next();
				// This is used to index the request being processed
				int recordID = MDCheckWebService.getElementInteger(record, "RecordID");
				// Get the request object for the specified record id
				MDCheckCOARequest request = (MDCheckCOARequest) requests.get(recordID);
				// Process individual output results
				processWebResponseFields(record, request);
			}
		}
		return "";
	}

	protected boolean addSMWebRequestFields(Document xmlDoc, MDCheckCOARequest request, Element record) throws KettleException {
		RowMetaInterface inputMeta = request.inputMeta;
		Object[] inputData = request.inputData;
		String value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputFullName());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "NameFull", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputNameFirst());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "NameFirst", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputNameMiddle());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "NameMiddle", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputNameLast());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "NameLast", value);
		}
		//
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputNamePrefix());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "NamePrefix", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputNameSuffix());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "NameSuffix", value);
		}
		//
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrCompany());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Company", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrLine());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressLine1", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrLine2());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressLine2", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrSuite());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Suite", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrPMB());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "PrivateMailbox", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrUrbanization());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Urbanization", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrCity());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "City", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrState());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "State", value);
		}
		if (!Const.isEmpty(smMeta.getInputAddrZip())) {
			String zip = inputMeta.getString(inputData, smMeta.getInputAddrZip(), "");
			if (!Const.isEmpty(zip)) {
				// Include Plus4 and delivery point with ZIP
				if (!Const.isEmpty(smMeta.getInputAddrPlus4())) {
					String plus4 = inputMeta.getString(inputData, smMeta.getInputAddrPlus4(), "");
					if (!Const.isEmpty(plus4)) {
						zip += plus4;
					}
				}
				MDCheckWebService.addTextNode(xmlDoc, record, "PostalCode", zip);
			}
		}
		value = MDCheck.getFieldString(inputMeta, inputData, smMeta.getInputAddrCountry());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Country", value);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see AbstractAddressVerifyWebRequestHandler#addWebOptions(org.w3c.dom.Document,
	 * org.w3c.dom.Element)
	 */
	@Override
	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {
		String opts = "ProcessingType:Standard," + "ListOwnerFreqProcessing:" + smMeta.getOptionMailFrequency() + ",NumOfMonthRequested:" + smMeta.getOptionMonthsRequest();
		MDCheckWebService.addTextNode(xmlDoc, root, "Options", opts);
		// not used leave blank
		//MDCheckWebService.addTextNode(xmlDoc, root, "TransmissionReference", "");
		// we only do ncoa right now so I think it can be left blank
		MDCheckWebService.addTextNode(xmlDoc, root, "Actions", "");// NCOA,CCOA
		MDCheckWebService.addTextNode(xmlDoc, root, "Columns", "grpStandardized,grpName,grpParsed,Plus4,PrivateMailBox,Suite,DPVFootNotes,MoveReturnCode");
		// Leave blank this is retrieved by the service
		MDCheckWebService.addTextNode(xmlDoc, root, "PAFId", "");
		MDCheckWebService.addTextNode(xmlDoc, root, "JobID", smMeta.getJobID());
		MDCheckWebService.addTextNode(xmlDoc, root, "ExecutionID", smMeta.getExecutionID());
		MDCheckWebService.addTextNode(xmlDoc, root, "OptSmartMoverListName", smMeta.getOptionListName());
	}

	/*
	 * (non-Javadoc)
	 * @see AbstractAddressVerifyWebRequestHandler#addWebRequestFields(org.w3c.dom.Document,
	 * com.melissadata.kettle.MDCheckRequest, org.w3c.dom.Element)
	 */
	@Override
	protected boolean addWebRequestFields(Document xmlDoc, MDCheckCVRequest request, Element record) throws KettleException {
		// Not used use addSMRequestFields
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see AbstractAddressVerifyWebRequestHandler#getInterfaceInfo(org.dom4j.Element)
	 */
	@Override
	protected void getInterfaceInfo(org.dom4j.Element response) {
		// Get the smartMover interface version
		smMeta.webSmVersion = MDCheckWebService.getElementText(response, "Version");
	}

	protected void processWebResponseFields(org.dom4j.Element record, MDCheckCOARequest request) throws KettleException {
		MDCheckCOARequest.COAResults smResults = request.coaResults = new MDCheckCOARequest.COAResults();
		smResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(record, "Results")));
		smResults.FullName = MDCheckWebService.getElementText(record, "NameFull");
		smResults.NamePrefix = MDCheckWebService.getElementText(record, "NamePrefix");
		smResults.NameSuffix = MDCheckWebService.getElementText(record, "NameSuffix");
		smResults.NameFirst = MDCheckWebService.getElementText(record, "NameFirst");
		smResults.NameMiddle = MDCheckWebService.getElementText(record, "NameMiddle");
		smResults.NameLast = MDCheckWebService.getElementText(record, "NameLast");
		smResults.CompanyName = MDCheckWebService.getElementText(record, "CompanyName");
		smResults.Address1 = MDCheckWebService.getElementText(record, "AddressLine1");
		smResults.Address2 = MDCheckWebService.getElementText(record, "AddressLine2");
		smResults.Suite = MDCheckWebService.getElementText(record, "Suite");
		smResults.PrivateMailBox = MDCheckWebService.getElementText(record, "PrivateMailbox");
		smResults.CityName = MDCheckWebService.getElementText(record, "City");
		smResults.CityAbbreviation = MDCheckWebService.getElementText(record, "CityAbbreviation");
		smResults.StateAbbreviation = MDCheckWebService.getElementText(record, "State");
		smResults.Zip = MDCheckWebService.getElementText(record, "PostalCode");
		smResults.Plus4 = MDCheckWebService.getElementText(record, "Plus4");
		smResults.CountryName = MDCheckWebService.getElementText(record, "CountryName");
		smResults.CountryAbbreviation = MDCheckWebService.getElementText(record, "CountryCode");
		smResults.DPVFootnotes = MDCheckWebService.getElementText(record, "DPVFootNotes");
		smResults.AddressKey = MDCheckWebService.getElementText(record, "AddressKey");

		smResults.MelissaAddressKey = MDCheckWebService.getElementText(record, "StandardizedMelissaAddressKey");
		smResults.BaseMelissaAddressKey = MDCheckWebService.getElementText(record, "StandardizedBaseMelissaAddressKey");

		smResults.CarrierRoute = MDCheckWebService.getElementText(record, "CarrierRoute");
		smResults.DeliveryPointCode = MDCheckWebService.getElementText(record, "DeliveryPointCode");
		smResults.DeliveryPointCheckDigit = MDCheckWebService.getElementText(record, "DeliveryPointCheckDigit");
		smResults.Urbanization = MDCheckWebService.getElementText(record, "Urbanization");
		smResults.EffectiveDate = MDCheckWebService.getElementText(record, "MoveEffectiveDate");
		smResults.MoveTypeCode = MDCheckWebService.getElementText(record, "MoveTypeCode");
		smResults.MoveReturnCode = MDCheckWebService.getElementText(record, "MoveReturnCode");
		smResults.ParsedExtraInformation = MDCheckWebService.getElementText(record, "AddressExtras");
		smResults.ParsedAddressRange = MDCheckWebService.getElementText(record, "AddressHouseNumber");
		smResults.ParsedDirectionPre = MDCheckWebService.getElementText(record, "AddressPreDirection");
		smResults.ParsedStreetName = MDCheckWebService.getElementText(record, "AddressStreetName");
		smResults.ParsedSuffix = MDCheckWebService.getElementText(record, "AddressStreetSuffix");
		smResults.ParsedDirectionPrePost = MDCheckWebService.getElementText(record, "AddressPostDirection");
		smResults.ParsedSuiteName = MDCheckWebService.getElementText(record, "AddressSuiteName");
		smResults.ParsedSuiteRange = MDCheckWebService.getElementText(record, "AddressSuiteNumber");
		smResults.ParsedPMBName = MDCheckWebService.getElementText(record, "AddressPrivateMailboxName");
		smResults.ParsedPMBRange = MDCheckWebService.getElementText(record, "AddressPrivateMailboxRange");
		smResults.valid = true;
	}

	@Override
	protected void processWebResponseFields(org.dom4j.Element record, MDCheckCVRequest request) throws KettleException {
		// FIXME SmartMover this is not used so throw error
	}
	
	public String processWebResponse(JSONObject jsonResponse, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Not used 
		return null;
	}
	
	public boolean buildWebRequest(JSONObject jsonRequest, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Not used 
		return false;
	}
}
