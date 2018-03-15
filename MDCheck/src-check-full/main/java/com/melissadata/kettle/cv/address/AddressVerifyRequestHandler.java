package com.melissadata.kettle.cv.address;

import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddressVerifyRequestHandler extends AbstractAddressVerifyWebRequestHandler {
	private AddressVerifyMeta	avMeta;

	public AddressVerifyRequestHandler(AddressVerifyMeta avMeta) {
		this.avMeta = avMeta;
	}

	public String getServiceName() {
		return "Address Verify";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractAddressVerifyRequestHandler#addWebOptions(org.w3c.dom.Document,
	 * org.w3c.dom.Element)
	 */
	@Override
	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {
		MDCheckWebService.addTextNode(xmlDoc, root, "OptAddressParsed", avMeta.getOptionAddressParsed() ? "True" : "False");
		if (false) {
			// These don't have web service equivalents
			MDCheckWebService.addTextNode(xmlDoc, root, "OptCountries", avMeta.getOptionCountries().toString());
			MDCheckWebService.addTextNode(xmlDoc, root, "OptPerformDPV", avMeta.getOptionPerformDPV() ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptPerformLACSLink", avMeta.getOptionPerformLACSLink() ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptPerformSuiteLink", avMeta.getOptionPerformSuiteLink() ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptPerformAddrPlus", avMeta.getOptionPerformAddrPlus() ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptPerformRBDI", avMeta.getOptionPerformRBDI() ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptUsePreferredCity", avMeta.getOptionUsePreferredCity() ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptCountries", avMeta.getOptionDiacriticMode().toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractAddressVerifyRequestHandler#addWebRequestFields(org.w3c.dom.Document,
	 * com.melissadata.kettle.MDCheckRequest, org.w3c.dom.Element)
	 */
	@Override
	protected boolean addWebRequestFields(Document xmlDoc, MDCheckCVRequest request, Element record) throws KettleException {
		RowMetaInterface inputMeta = request.inputMeta;
		Object[] inputData = request.inputData;
		if (!Const.isEmpty(avMeta.getInputLastName())) {
			// Optionally get last name from name parser results
			String lastname = null;
			if (avMeta.getInputLastName().equals(AddressVerifyMeta.NAME_PARSER_LAST_NAME1)) {
				lastname = (request.nameResults != null) ? request.nameResults.Last : null;
			} else if (avMeta.getInputLastName().equals(AddressVerifyMeta.NAME_PARSER_LAST_NAME2)) {
				lastname = (request.nameResults != null) ? request.nameResults.Last2 : null;
			} else {
				lastname = inputMeta.getString(inputData, avMeta.getInputLastName(), "");
			}
			if (!Const.isEmpty(lastname)) {
				MDCheckWebService.addTextNode(xmlDoc, record, "LastName", lastname);
			}
		}
		String value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputCompany());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Company", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputAddressLine1());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressLine1", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputAddressLine2());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressLine2", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputSuite());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Suite", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputUrbanization());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Urbanization", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputCity());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "City", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputState());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "State", value);
		}
		if (!Const.isEmpty(avMeta.getInputZip())) {
			String zip = inputMeta.getString(inputData, avMeta.getInputZip(), "");
			if (!Const.isEmpty(zip)) {
				// Include Plus4 and delivery point with ZIP
				if (!Const.isEmpty(avMeta.getInputPlus4())) {
					String plus4 = inputMeta.getString(inputData, avMeta.getInputPlus4(), "");
					if (!Const.isEmpty(plus4)) {
						zip += plus4;
						if (!Const.isEmpty(avMeta.getInputDeliveryPoint())) {
							String deliveryPoint = inputMeta.getString(inputData, avMeta.getInputDeliveryPoint(), "");
							if (!Const.isEmpty(deliveryPoint)) {
								zip += deliveryPoint;
							}
						}
					}
				}
				MDCheckWebService.addTextNode(xmlDoc, record, "Zip", zip);
			}
		}
		value = MDCheck.getFieldString(inputMeta, inputData, avMeta.getInputCountry());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Country", value);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractAddressVerifyRequestHandler#getInterfaceInfo(org.dom4j.Element)
	 */
	@Override
	protected void getInterfaceInfo(org.dom4j.Element response) {
		// Get the interface version
		avMeta.webVersion = MDCheckWebService.getElementText(response, "Version");
	}

	/*
	 * (non-Javadoc)
	 * @see AbstractAddressVerifyWebRequestHandler#processWebResponseFields(org.dom4j.Element,
	 * MDCheckCVRequest)
	 */
	@Override
	protected void processWebResponseFields(org.dom4j.Element record, MDCheckCVRequest request) throws KettleException {
		MDCheckCVRequest.AddrResults addrResults = request.addrResults = new MDCheckCVRequest.AddrResults();
		// Result code for the individual request
		addrResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(record, "Results")));
		// Get the address result element
		org.dom4j.Element address = record.element("Address");
		if (address == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.AddressNotFoundInElement")); }
		// Extract the rest of the results
		addrResults.Address1 = MDCheckWebService.getElementText(address, "Address1");
		addrResults.Address2 = MDCheckWebService.getElementText(address, "Address2");
		addrResults.PrivateMailBox = MDCheckWebService.getElementText(address, "PrivateMailBox");
		addrResults.Suite = MDCheckWebService.getElementText(address, "Suite");
		org.dom4j.Element urbanization = MDCheckWebService.getElement(address, "Urbanization");
		addrResults.UrbanizationName = MDCheckWebService.getElementText(urbanization, "Name");
		org.dom4j.Element city = MDCheckWebService.getElement(address, "City");
		addrResults.CityName = MDCheckWebService.getElementText(city, "Name");
		org.dom4j.Element state = MDCheckWebService.getElement(address, "State");
		addrResults.StateAbbreviation = MDCheckWebService.getElementText(state, "Abbreviation");
		addrResults.Zip = MDCheckWebService.getElementText(address, "Zip");
		addrResults.Plus4 = MDCheckWebService.getElementText(address, "Plus4");
		addrResults.DeliveryPointCode = MDCheckWebService.getElementText(address, "DeliveryPointCode");
		addrResults.DeliveryPointCheckDigit = MDCheckWebService.getElementText(address, "DeliveryPointCheckDigit");
		org.dom4j.Element country = MDCheckWebService.getElement(address, "Country");
		addrResults.CountryAbbreviation = MDCheckWebService.getElementText(country, "Abbreviation");
		addrResults.CountryName = MDCheckWebService.getElementText(country, "Name");
		addrResults.CMRA = MDCheckWebService.getElementText(address, "CMRA");
		addrResults.ELotNumber = ""; // Web service does not have ELot Number
		addrResults.ELotOrder = ""; // Web service does not have ELot Order
		addrResults.RBDI = MDCheckWebService.getElementText(address, "RBDI");
		org.dom4j.Element type = MDCheckWebService.getElement(address, "Type");
		org.dom4j.Element addressType = MDCheckWebService.getElement(type, "Address");
		addrResults.TypeAddressCode = MDCheckWebService.getElementText(addressType, "Code");
		addrResults.TypeAddressDescription = MDCheckWebService.getElementText(addressType, "Description");
		org.dom4j.Element zipType = MDCheckWebService.getElement(type, "Zip");
		addrResults.TypeZipCode = MDCheckWebService.getElementText(zipType, "Code");
		addrResults.TypeZipDescription = MDCheckWebService.getElementText(zipType, "Description");
		addrResults.CarrierRoute = MDCheckWebService.getElementText(address, "CarrierRoute");
		addrResults.CityAbbreviation = MDCheckWebService.getElementText(city, "Abbreviation");
		addrResults.CongressionalDistrict = MDCheckWebService.getElementText(address, "CongressionalDistrict");
		addrResults.Company = MDCheckWebService.getElementText(address, "Company");
		addrResults.StateName = MDCheckWebService.getElementText(state, "Name");
		org.dom4j.Element parsed = MDCheckWebService.getElement(address, "Parsed");
		addrResults.ParsedAddressRange = MDCheckWebService.getElementText(parsed, "AddressRange");
		org.dom4j.Element direction = MDCheckWebService.getElement(parsed, "Direction");
		addrResults.ParsedDirectionPre = MDCheckWebService.getElementText(direction, "Pre");
		addrResults.ParsedStreetName = MDCheckWebService.getElementText(parsed, "StreetName");
		addrResults.ParsedSuffix = MDCheckWebService.getElementText(parsed, "Suffix");
		addrResults.ParsedDirectionPost = MDCheckWebService.getElementText(direction, "Post");
		org.dom4j.Element suite = MDCheckWebService.getElement(parsed, "Suite");
		addrResults.ParsedSuiteName = MDCheckWebService.getElementText(suite, "Name");
		addrResults.ParsedSuiteRange = MDCheckWebService.getElementText(suite, "Range");
		org.dom4j.Element pmb = MDCheckWebService.getElement(parsed, "PrivateMailbox");
		addrResults.ParsedPMBName = MDCheckWebService.getElementText(pmb, "Name");
		addrResults.ParsedPMBRange = MDCheckWebService.getElementText(pmb, "Range");
		addrResults.ParsedRouteService = MDCheckWebService.getElementText(address, "RouteService");
		addrResults.ParsedLockBox = MDCheckWebService.getElementText(address, "LockBox");
		addrResults.ParsedDeliveryInstallation = MDCheckWebService.getElementText(address, "DeliveryInstallation");
		addrResults.ParsedExtraInformation = MDCheckWebService.getElementText(address, "ExtraInformation");
		addrResults.CountyName = ""; // Web service does not have County Name
		addrResults.CountyFips = ""; // Web service does not have County FIPS
		addrResults.TimeZone = ""; // Web service does not have time zone
		addrResults.TimeZoneCode = ""; // Web service does not have time zone code
		addrResults.AddressKey = MDCheckWebService.getElementText(address, "AddressKey");

		addrResults.MAK = ""; // Web service does not have MAK
		addrResults.BaseMAK = ""; // Web service does not have Base MAK
		// TODO: Do more complete validity checks
		addrResults.valid = true;
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
