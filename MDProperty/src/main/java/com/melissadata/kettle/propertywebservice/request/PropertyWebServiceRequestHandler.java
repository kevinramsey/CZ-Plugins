package com.melissadata.kettle.propertywebservice.request;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.melissadata.cz.support.MetaVal;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceData;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceStep;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.web.MDPropertyWebService;
import com.melissadata.kettle.propertywebservice.web.WebRequestHandler;

public class PropertyWebServiceRequestHandler implements WebRequestHandler {

	private PropertyWebServiceFields propertyFields;
	public String webMsg     = "";
	public String webVersion = "";
	public KettleException webException;

	public PropertyWebServiceRequestHandler(PropertyWebServiceFields addrFields) {

		this.propertyFields = addrFields;
	}

	public boolean buildWebRequest(Document xmlDoc, MDPropertyWebServiceData data, List<MDPropertyWebServiceRequest> requests, boolean testing) throws KettleException {

		Element root = xmlDoc.getDocumentElement();
		// place holder add nothing
		addWebOptions(xmlDoc, root);
		// Add records
		boolean sendRequest = false;
		//Turned on testing so as to hit web service with one record
		testing = false;

		// RequestArray
		// If testing, create a fake request record
		if (testing) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			MDPropertyWebService.addTextNode(xmlDoc, record, "RecordID", "1");
			MDPropertyWebService.addTextNode(xmlDoc, record, "AddressKey", "92688211282");
			MDPropertyWebService.addTextNode(xmlDoc, record, "FIPS", "");
			MDPropertyWebService.addTextNode(xmlDoc, record, "APN", "");
			MDPropertyWebService.addTextNode(xmlDoc, record, "FreeForm", "");
			root.appendChild(record);
			// There is at least one request
			sendRequest = true;
		} else {
			// Otherwise, add real records
			Element records = xmlDoc.createElement("Records");
			for (int recordID = 0; recordID < requests.size(); recordID++) {
				MDPropertyWebServiceRequest request = requests.get(recordID);

				// TODO: More complete validity checks
				boolean valid = true;
				// Add request if it is valid
				if (valid) {
					// Create new record object
					Element requestRecord = xmlDoc.createElement("RequestRecord");

					// Add unique record id
					MDPropertyWebService.addTextNode(xmlDoc, requestRecord, "RecordID", "" + recordID);

					// Add request fields

					if (addWebRequestFields(xmlDoc, request, requestRecord)) {

						// Add the record to the document
						records.appendChild(requestRecord);
						// There is at least one request with inputs
						sendRequest = true;
					}
				}
			}
			root.appendChild(records);
		}
		return sendRequest;
	}

	public String processWebResponse(org.dom4j.Document doc, MDPropertyWebServiceData data, List<MDPropertyWebServiceRequest> requests, boolean testing) throws KettleException {
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("LookupPropertyResponse")) {
			throw new KettleException("MDPropertyWebService: LookupPropertyResponse not found in XML response string");
		}

		// Check the general result
		org.dom4j.Node resultNode = response.element("TransmissionResults");
		//org.dom4j.Node totalNode = response.element("TransmissionReference");
		org.dom4j.Element records = response.element("Records");
//		Iterator
		String resultCodes = resultNode.getText();

		if (!Const.isEmpty(resultCodes)) {
			return resultCodes;
		}

		// Get interface info
		getInterfaceInfo(response);
		// Get the response records (ignore if testing)
		if (!testing) {
			@SuppressWarnings("unchecked") List<org.dom4j.Element> nodes = records.elements();
			for (org.dom4j.Element element : nodes) {
				// This is used to index the request being processed
				int recordID = Integer.parseInt(element.elementText("RecordID"));
//				System.out.println("--------------------top ------------------");
//				System.out.println(" *** *** ***   " + recordID); //   selectNode("Results"));
//				System.out.println(" - " + element.element("Parcel").elementText("FIPSCode"));
//				System.out.println(" ______________ Bottom ____________");
				// Get the request object for the specified record id
				MDPropertyWebServiceRequest request = requests.get(recordID);
				// Process individual output results
				processWebResponseFields(element, request);
			}
		}
		return "";
	}

	@SuppressWarnings("unused")
	private void getInterfaceInfo(org.dom4j.Element response) {
		//TODO I don't think this is used but we can leave it in for now

		String Version = response.elementText("Version");
	}

	protected boolean addWebRequestFields(Document xmlDoc, MDPropertyWebServiceRequest request, Element record) throws KettleException {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[]         inputData = request.inputData;
		int              inputs    = 0;
		try {
			for (String key : propertyFields.inputFields.keySet()) {
				String value = MDPropertyWebServiceStep.getFieldString(inputMeta, inputData, propertyFields.inputFields.get(key).metaValue);
				if (!Const.isEmpty(value)) {
					MDPropertyWebService.addTextNode(xmlDoc, record, propertyFields.inputFields.get(key).webTag, value);
					inputs++;
				}
			}
		} catch (Exception e) {
			System.out.println("ERROR adding request fields: " + e.getMessage());
			e.printStackTrace();
		}

		return true;//inputs != 0;
	}

	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {

	}

	protected void processWebResponseFields(org.dom4j.Element element, MDPropertyWebServiceRequest request) throws KettleException {

		request.proCoderResults.results = element.elementText("Results");

		// Parcel (11)
		org.dom4j.Element parcelElement = element.element("Parcel");
		if (parcelElement != null) {
			request.proCoderResults.fipsCode = parcelElement.elementText("FIPSCode");
			request.proCoderResults.county = parcelElement.elementText("County");
			request.proCoderResults.unformattedAPN = parcelElement.elementText("UnformattedAPN");
			request.proCoderResults.formattedAPN = parcelElement.elementText("FormattedAPN");
			request.proCoderResults.alternateAPN = parcelElement.elementText("alternateAPN");
			request.proCoderResults.aPNYearChange = parcelElement.elementText("APNYearChange");
			request.proCoderResults.previousAPN = parcelElement.elementText("PreviousAPN");
			request.proCoderResults.accountNumber = parcelElement.elementText("AccountNumber");
			request.proCoderResults.yearAdded = parcelElement.elementText("YearAdded");
			request.proCoderResults.mapBook = parcelElement.elementText("MapBook");
			request.proCoderResults.mapPage = parcelElement.elementText("MapPage");
		}

		// Legal 15
		org.dom4j.Element legalElement = element.element("Legal");
		if (legalElement != null) {
			request.proCoderResults.legalDescription = legalElement.elementText("LegalDescription");
			request.proCoderResults.legalRange = legalElement.elementText("Range");
			request.proCoderResults.township = legalElement.elementText("Township");
			request.proCoderResults.section = legalElement.elementText("Section");
			request.proCoderResults.quarter = legalElement.elementText("Quarter");
			request.proCoderResults.quarterQuarter = legalElement.elementText("QuarterQuarter");
			request.proCoderResults.subdivision = legalElement.elementText("Subdivision");
			request.proCoderResults.phase = legalElement.elementText("Phase");
			request.proCoderResults.tractNumber = legalElement.elementText("TractNumber");
			request.proCoderResults.block1 = legalElement.elementText("Block1");
			request.proCoderResults.block2 = legalElement.elementText("Block2");
			request.proCoderResults.lotNumber1 = legalElement.elementText("LotNumber1");
			request.proCoderResults.lotNumber2 = legalElement.elementText("LotNumber2");
			request.proCoderResults.lotNumber3 = legalElement.elementText("LotNumber3");
			request.proCoderResults.unit = legalElement.elementText("Unit");
		}

		//property address 9
		org.dom4j.Element addressElement = element.element("PropertyAddress");
		if (addressElement != null) {
			request.proCoderResults.address = addressElement.elementText("Address");
			request.proCoderResults.city = addressElement.elementText("City");
			request.proCoderResults.state = addressElement.elementText("State");
			request.proCoderResults.zip = addressElement.elementText("Zip");
			request.proCoderResults.addressKey = addressElement.elementText("AddressKey");
			request.proCoderResults.mak = addressElement.elementText("MAK");
			request.proCoderResults.baseMAK = addressElement.elementText("BaseMAK");
			request.proCoderResults.latitude = addressElement.elementText("Latitude");
			request.proCoderResults.longitude = addressElement.elementText("Longitude");
		}

		// parsed property address 7
		org.dom4j.Element parsedAddressElement = element.element("ParsedPropertyAddress");
		if (parsedAddressElement != null) {
			request.proCoderResults.parsedRange = parsedAddressElement.elementText("Range");
			request.proCoderResults.preDirectional = parsedAddressElement.elementText("PreDirectional");
			request.proCoderResults.streetName = parsedAddressElement.elementText("StreetName");
			request.proCoderResults.suffix = parsedAddressElement.elementText("Suffix");
			request.proCoderResults.postDirectional = parsedAddressElement.elementText("PostDirectional");
			request.proCoderResults.suiteName = parsedAddressElement.elementText("SuiteName");
			request.proCoderResults.suiteRange = parsedAddressElement.elementText("SuiteRange");
		}

		// primary owner 14
		org.dom4j.Element primaryOwnerElement = element.element("PrimaryOwner");
		if (primaryOwnerElement != null) {
			request.proCoderResults.name1Full = primaryOwnerElement.elementText("Name1Full");
			request.proCoderResults.name1First = primaryOwnerElement.elementText("Name1First");
			request.proCoderResults.name1Middle = primaryOwnerElement.elementText("Name1Middle");
			request.proCoderResults.name1Last = primaryOwnerElement.elementText("Name1Last");
			request.proCoderResults.name1Suffix = primaryOwnerElement.elementText("Name1Suffix");
			request.proCoderResults.ownerTrustFlag = primaryOwnerElement.elementText("TrustFlag");
			request.proCoderResults.ownerCompanyFlag = primaryOwnerElement.elementText("CompanyFlag");
			request.proCoderResults.name2Full = primaryOwnerElement.elementText("Name2Full");
			request.proCoderResults.name2First = primaryOwnerElement.elementText("Name2First");
			request.proCoderResults.name2Middle = primaryOwnerElement.elementText("Name2Middle");
			request.proCoderResults.name2Last = primaryOwnerElement.elementText("Name2Last");
			request.proCoderResults.name2Suffix = primaryOwnerElement.elementText("Name2Suffix");
			request.proCoderResults.ownerType = primaryOwnerElement.elementText("Type");
			request.proCoderResults.ownerVestingType = primaryOwnerElement.elementText("VestingType");
		}

		//secondary 11
		org.dom4j.Element secondaryOwnerElement = element.element("SecondaryOwner");
		if (secondaryOwnerElement != null) {
			request.proCoderResults.name3Full = secondaryOwnerElement.elementText("Name3Full");
			request.proCoderResults.name3First = secondaryOwnerElement.elementText("Name3First");
			request.proCoderResults.name3Middle = secondaryOwnerElement.elementText("Name3Middle");
			request.proCoderResults.name3Last = secondaryOwnerElement.elementText("Name3Last");
			request.proCoderResults.name3Suffix = secondaryOwnerElement.elementText("Name3Suffix");
			request.proCoderResults.name4Full = secondaryOwnerElement.elementText("Name4Full");
			request.proCoderResults.name4First = secondaryOwnerElement.elementText("Name4First");
			request.proCoderResults.name4Middle = secondaryOwnerElement.elementText("Name4Middle");
			request.proCoderResults.name4Last = secondaryOwnerElement.elementText("Name4Last");
			request.proCoderResults.name4Suffix = secondaryOwnerElement.elementText("Name4Suffix");
			request.proCoderResults.secondarytype = secondaryOwnerElement.elementText("Type");
		}
		// owne address  7
		org.dom4j.Element ownerAddressElement = element.element("OwnerAddress");
		if (ownerAddressElement != null) {
			request.proCoderResults.ownerAddress = ownerAddressElement.elementText("Address");
			request.proCoderResults.ownerCity = ownerAddressElement.elementText("City");
			request.proCoderResults.ownerState = ownerAddressElement.elementText("State");
			request.proCoderResults.ownerZip = ownerAddressElement.elementText("Zip");
			request.proCoderResults.ownerCarrierRoute = ownerAddressElement.elementText("CarrierRoute");
			request.proCoderResults.ownerMAK = ownerAddressElement.elementText("MAK");
			request.proCoderResults.ownerBaseMAK = ownerAddressElement.elementText("BaseMAK");
		}
		// last deed 20
		org.dom4j.Element lastDeedOwnerInfoElement = element.element("LastDeedOwnerInfo");
		if (lastDeedOwnerInfoElement != null) {
			request.proCoderResults.ldoName1Full = lastDeedOwnerInfoElement.elementText("Name1Full");
			request.proCoderResults.ldoName1First = lastDeedOwnerInfoElement.elementText("Name1First");
			request.proCoderResults.ldoName1Middle = lastDeedOwnerInfoElement.elementText("Name1Middle");
			request.proCoderResults.ldoName1Last = lastDeedOwnerInfoElement.elementText("Name1Last");
			request.proCoderResults.ldoName1Suffix = lastDeedOwnerInfoElement.elementText("Name1Suffix");
			request.proCoderResults.ldoName2Full = lastDeedOwnerInfoElement.elementText("Name2Full");
			request.proCoderResults.ldoName2First = lastDeedOwnerInfoElement.elementText("Name2First");
			request.proCoderResults.ldoName2Middle = lastDeedOwnerInfoElement.elementText("Name2Middle");
			request.proCoderResults.ldoName2Last = lastDeedOwnerInfoElement.elementText("Name2Last");
			request.proCoderResults.ldoName2Suffix = lastDeedOwnerInfoElement.elementText("Name2Suffix");
			request.proCoderResults.ldoName3Full = lastDeedOwnerInfoElement.elementText("Name3Full");
			request.proCoderResults.ldoName3First = lastDeedOwnerInfoElement.elementText("Name3First");
			request.proCoderResults.ldoName3Middle = lastDeedOwnerInfoElement.elementText("Name3Middle");
			request.proCoderResults.ldoName3Last = lastDeedOwnerInfoElement.elementText("Name3Last");
			request.proCoderResults.ldoName3Suffix = lastDeedOwnerInfoElement.elementText("Name3Suffix");
			request.proCoderResults.ldoName4Full = lastDeedOwnerInfoElement.elementText("Name4Full");
			request.proCoderResults.ldoName4First = lastDeedOwnerInfoElement.elementText("Name4First");
			request.proCoderResults.ldoName4Middle = lastDeedOwnerInfoElement.elementText("Name4Middle");
			request.proCoderResults.ldoName4Last = lastDeedOwnerInfoElement.elementText("Name4Last");
			request.proCoderResults.ldoName4Suffix = lastDeedOwnerInfoElement.elementText("Name4Suffix");
		}
		// current deed 10
		org.dom4j.Element currentDeedElement = element.element("CurrentDeed");
		if (currentDeedElement != null) {
			request.proCoderResults.mortgageAmount = currentDeedElement.elementText("MortgageAmount");
			request.proCoderResults.mortgageDate = currentDeedElement.elementText("MortgageDate");
			request.proCoderResults.mortgageLoanTypeCode = currentDeedElement.elementText("MortgageLoanTypeCode");
			request.proCoderResults.mortgageTermCode = currentDeedElement.elementText("MortgageTermCode");
			request.proCoderResults.mortgageTerm = currentDeedElement.elementText("MortgageTerm");
			request.proCoderResults.mortgageDueDate = currentDeedElement.elementText("MortgageDueDate");
			request.proCoderResults.lenderCode = currentDeedElement.elementText("LenderCode");
			request.proCoderResults.lenderName = currentDeedElement.elementText("LenderName");
			request.proCoderResults.secondMortgageAmount = currentDeedElement.elementText("SecondMortgageAmount");
			request.proCoderResults.secondMortgageLoanTypeCode = currentDeedElement.elementText("SecondMortgageLoanTypeCode");
		}
		// tax 23
		org.dom4j.Element taxElement = element.element("Tax");
		if (taxElement != null) {
			request.proCoderResults.yearAssessed = taxElement.elementText("YearAssessed");
			request.proCoderResults.assessedValueTotal = taxElement.elementText("AssessedValueTotal");
			request.proCoderResults.assessedValueImprovements = taxElement.elementText("AssessedValueImprovements");
			request.proCoderResults.assessedValueLand = taxElement.elementText("AssessedValueLand");
			request.proCoderResults.assessedImprovementsPerc = taxElement.elementText("AssessedImprovementsPerc");
			request.proCoderResults.previousAssessedValue = taxElement.elementText("PreviousAssessedValue");
			request.proCoderResults.marketValueYear = taxElement.elementText("MarketValueYear");
			request.proCoderResults.marketValueTotal = taxElement.elementText("MarketValueTotal");
			request.proCoderResults.marketValueImprovements = taxElement.elementText("marketValueImprovements");
			request.proCoderResults.marketValueLand = taxElement.elementText("MarketValueLand");
			request.proCoderResults.marketImprovementsPerc = taxElement.elementText("MarketImprovementsPerc");
			request.proCoderResults.taxFiscalYear = taxElement.elementText("TaxFiscalYear");
			request.proCoderResults.taxRateArea = taxElement.elementText("TaxRateArea");
			request.proCoderResults.taxBilledAmount = taxElement.elementText("TaxBilledAmmount");
			request.proCoderResults.taxDelinquentYear = taxElement.elementText("TaxDelinquentYear");
			request.proCoderResults.lastTaxRollUpdate = taxElement.elementText("LastTaxRollUpdate");
			request.proCoderResults.assrLastUpdated = taxElement.elementText("AssrLastUpdated");
			request.proCoderResults.taxExemptionHomeowner = taxElement.elementText("TaxExemptionHomeowner");
			request.proCoderResults.taxExemptionDisabled = taxElement.elementText("TaxExemptionDisabled");
			request.proCoderResults.taxExemptionSenior = taxElement.elementText("TaxExemptionSenior");
			request.proCoderResults.taxExemptionVeteran = taxElement.elementText("TaxExemptionVeteran");
			request.proCoderResults.taxExemptionWidow = taxElement.elementText("TaxExemptionWidow");
			request.proCoderResults.taxExemptionAdditional = taxElement.elementText("TaxExemptionAdditional");
		}
		// estimated value 5
		org.dom4j.Element estimatedValueElement = element.element("EstimatedValue");
		if (estimatedValueElement != null) {
			request.proCoderResults.estimatedValue = estimatedValueElement.elementText("EstimatedValue");
			request.proCoderResults.estimatedMinValue = estimatedValueElement.elementText("EstimatedMinValue");
			request.proCoderResults.estimatedMaxValue = estimatedValueElement.elementText("EstimatedMaxValue");
			request.proCoderResults.confidenceScore = estimatedValueElement.elementText("ConfidenceScore");
			request.proCoderResults.valuationDate = estimatedValueElement.elementText("ValuationDate");
		}
		//Sales info 13
		org.dom4j.Element saleInfoElement = element.element("SaleInfo");
		if (saleInfoElement != null) {
			request.proCoderResults.assessorLastSaleDate = saleInfoElement.elementText("AssessorLastSaleDate");
			request.proCoderResults.assessorLastSaleAmount = saleInfoElement.elementText("AssessorLastSaleAmount");
			request.proCoderResults.assessorPriorSaleDate = saleInfoElement.elementText("AssessorPriorSaleDate");
			request.proCoderResults.assessorPriorSaleAmount = saleInfoElement.elementText("AssessorPriorSaleAmount");
			request.proCoderResults.lastOwnershipTransferDate = saleInfoElement.elementText("LastOwnershipTransferDate");
			request.proCoderResults.lastOwnershipTransferDocumentNumber = saleInfoElement.elementText("LastOwnershipTransferDocumentNumber");
			request.proCoderResults.lastOwnershipTransferTxID = saleInfoElement.elementText("LastOwnershipTransferTxID");
			request.proCoderResults.deedLastSaleDocumentBook = saleInfoElement.elementText("DeedLastSaleDocumentBook");
			request.proCoderResults.deedLastSaleDocumentPage = saleInfoElement.elementText("DeedLastSaleDocumentPage");
			request.proCoderResults.deedLastDocumentNumber = saleInfoElement.elementText("DeedLastDocumentNumber");
			request.proCoderResults.deedLastSaleDate = saleInfoElement.elementText("DeedLastSaleDate");
			request.proCoderResults.deedLastSalePrice = saleInfoElement.elementText("DeedLastSalePrice");
			request.proCoderResults.deedLastSaleTxID = saleInfoElement.elementText("DeedLastSaleTxID");
		}

		//property use 6
		org.dom4j.Element propertyUseElement = element.element("PropertyUseInfo");
		if (propertyUseElement != null) {
			request.proCoderResults.yearBuilt = propertyUseElement.elementText("YearBuilt");
			request.proCoderResults.yearBuiltEffective = propertyUseElement.elementText("YearBuiltEffective");
			request.proCoderResults.zonedCodeLocal = propertyUseElement.elementText("ZonedCodeLocal");
			request.proCoderResults.propertyUseMuni = propertyUseElement.elementText("PropertyUseMuni");
			request.proCoderResults.propertyUseGroup = propertyUseElement.elementText("PropertyUseGroup");
			request.proCoderResults.propertyUseStandardized = propertyUseElement.elementText("PropertyUseStandardized");
		}
		// property size  19
		org.dom4j.Element propertySizeElement = element.element("PropertySize");
		if (propertySizeElement != null) {
			request.proCoderResults.areaBuilding = propertySizeElement.elementText("AreaBuilding");
			request.proCoderResults.areaBuildingDefinitionCode = propertySizeElement.elementText("AreaBuildingDefinitionCode");
			request.proCoderResults.areaGross = propertySizeElement.elementText("AreaGross");
			request.proCoderResults.area1stFloor = propertySizeElement.elementText("Area1stFloor");
			request.proCoderResults.area2ndFloor = propertySizeElement.elementText("Area2ndFloor");
			request.proCoderResults.areaUpperFloors = propertySizeElement.elementText("AreaUpperFloors");
			request.proCoderResults.areaLotAcres = propertySizeElement.elementText("AreaLotAcres");
			request.proCoderResults.areaLotSF = propertySizeElement.elementText("AreaLotSF");
			request.proCoderResults.lotDepth = propertySizeElement.elementText("LotDepth");
			request.proCoderResults.lotWidth = propertySizeElement.elementText("LotWidth");
			request.proCoderResults.atticArea = propertySizeElement.elementText("AtticArea");
			request.proCoderResults.atticFlag = propertySizeElement.elementText("AtticFlag");
			request.proCoderResults.basementArea = propertySizeElement.elementText("BasementArea");
			request.proCoderResults.basementAreaFinished = propertySizeElement.elementText("BasementAreaFinished");
			request.proCoderResults.basementAreaUnfinished = propertySizeElement.elementText("BasementAreaUnfinished");
			request.proCoderResults.parkingGarage = propertySizeElement.elementText("ParkingGarage");
			request.proCoderResults.parkingGarageArea = propertySizeElement.elementText("ParkingGarageArea");
			request.proCoderResults.parkingCarport = propertySizeElement.elementText("ParkingCarport");
			request.proCoderResults.parkingCarportArea = propertySizeElement.elementText("ParkingCarportArea");
		}
		//pool  3
		org.dom4j.Element poolElement = element.element("Pool");
		if (poolElement != null) {
			request.proCoderResults.pool = poolElement.elementText("Pool");
			request.proCoderResults.poolArea = poolElement.elementText("PoolArea");
			request.proCoderResults.saunaFlag = poolElement.elementText("SaunaFlag");
		}
		// utilities  6
		org.dom4j.Element utilitiesElement = element.element("Utilities");
		if (utilitiesElement != null) {
			request.proCoderResults.HVACCoolingDetail = utilitiesElement.elementText("HVACCoolingDetail");
			request.proCoderResults.HVACHeatingDetail = utilitiesElement.elementText("HVACHeatingDetail");
			request.proCoderResults.HVACHeatingFuel = utilitiesElement.elementText("HVACHeatingFuel");
			request.proCoderResults.sewageUsage = utilitiesElement.elementText("SewageUsage");
			request.proCoderResults.waterSource = utilitiesElement.elementText("WaterSource");
			request.proCoderResults.mobileHomeHookupFlag = utilitiesElement.elementText("MobileHomeHookupFlag");
		}
		// parking 4
		org.dom4j.Element parkingElement = element.element("Parking");
		if (parkingElement != null) {
			request.proCoderResults.rvParkingFlag = parkingElement.elementText("RVParkingFlag");
			request.proCoderResults.parkingSpaceCount = parkingElement.elementText("ParkingSpaceCount");
			request.proCoderResults.drivewayArea = parkingElement.elementText("DrivewayArea");
			request.proCoderResults.drivewayMaterial = parkingElement.elementText("DrivewayMaterial");
		}
		// yard and garden 14
		org.dom4j.Element yardGardenElement = element.element("YardGardenInfo");
		if (yardGardenElement != null) {
			request.proCoderResults.topographyCode = yardGardenElement.elementText("TopographyCode");
			request.proCoderResults.fenceCode = yardGardenElement.elementText("FenceCode");
			request.proCoderResults.fenceArea = yardGardenElement.elementText("FenceArea");
			request.proCoderResults.courtyardFlag = yardGardenElement.elementText("CourtyardFlag");
			request.proCoderResults.courtyardArea = yardGardenElement.elementText("CourtyardArea");
			request.proCoderResults.arborPergolaFlag = yardGardenElement.elementText("ArborPergolaFlag");
			request.proCoderResults.sprinklersFlag = yardGardenElement.elementText("SprinklersFlag");
			request.proCoderResults.golfCourseGreenFlag = yardGardenElement.elementText("GolfCourseGreenFlag");
			request.proCoderResults.tennisCourtFlag = yardGardenElement.elementText("TennisCourtFlag");
			request.proCoderResults.sportsCourtFlag = yardGardenElement.elementText("SportsCourtFlag");
			request.proCoderResults.arenaFlag = yardGardenElement.elementText("ArenaFlag");
			request.proCoderResults.waterFeatureFlag = yardGardenElement.elementText("WaterFeatureFlag");
			request.proCoderResults.pondFlag = yardGardenElement.elementText("PondFlag");
			request.proCoderResults.boatLiftFlag = yardGardenElement.elementText("BoatLiftFlag");
		}
		// shape 1
		org.dom4j.Element shapeElement = element.element("Shape");
		if (shapeElement != null) {
			request.proCoderResults.wellKnownText = shapeElement.elementText("WellKnownText");
		}

		// int struct info 7
		org.dom4j.Element intStructElement = element.element("IntStructInfo");
		if (intStructElement != null) {
			request.proCoderResults.foundation = intStructElement.elementText("Foundation");
			request.proCoderResults.construction = intStructElement.elementText("Construction");
			request.proCoderResults.interiorStructure = intStructElement.elementText("InteriorStructure");
			request.proCoderResults.plumbingFixturesCount = intStructElement.elementText("PlumbingFixtureCount");
			request.proCoderResults.constructionFireResistanceClass = intStructElement.elementText("ConstructionFireResistanceClass");
			request.proCoderResults.safetyFireSprinklersFlag = intStructElement.elementText("SafetyFireSprinklersFlag");
			request.proCoderResults.flooringMaterialPrimary = intStructElement.elementText("FlooringMaterialPrimary");
		}
		// int room info 27
		org.dom4j.Element intRoomElement = element.element("IntRoomInfo");
		if (intRoomElement != null) {
			request.proCoderResults.bathCount = intRoomElement.elementText("BathCount");
			request.proCoderResults.bathPartialCount = intRoomElement.elementText("BathPartialCount");
			request.proCoderResults.bedroomsCount = intRoomElement.elementText("BedroomsCount");
			request.proCoderResults.roomsCount = intRoomElement.elementText("RoomsCount");
			request.proCoderResults.storiesCount = intRoomElement.elementText("StoriesCount");
			request.proCoderResults.unitsCount = intRoomElement.elementText("UnitsCount");
			request.proCoderResults.bonusRoomFlag = intRoomElement.elementText("BonusRoomFlag");
			request.proCoderResults.breakfastNookFlag = intRoomElement.elementText("BreakfastNookFlag");
			request.proCoderResults.cellarFlag = intRoomElement.elementText("CellarFlag");
			request.proCoderResults.cellarWineFlag = intRoomElement.elementText("CellarWineFlag");
			request.proCoderResults.excerciseFlag = intRoomElement.elementText("excerciseFlag");
			request.proCoderResults.familyCode = intRoomElement.elementText("FamilyCode");
			request.proCoderResults.gameFlag = intRoomElement.elementText("GameFlag");
			request.proCoderResults.greatFlag = intRoomElement.elementText("GreatFlag");
			request.proCoderResults.hobbyFlag = intRoomElement.elementText("HobbyFlag");
			request.proCoderResults.laundryFlag = intRoomElement.elementText("LaundryFlag");
			request.proCoderResults.mediaFlag = intRoomElement.elementText("MediaFlag");
			request.proCoderResults.mudFlag = intRoomElement.elementText("MudFlag");
			request.proCoderResults.officeArea = intRoomElement.elementText("OfficeArea");
			request.proCoderResults.officeFlag = intRoomElement.elementText("OfficeFlag");
			request.proCoderResults.safeRoomFlag = intRoomElement.elementText("SafeRoomFlag");
			request.proCoderResults.sittingFlag = intRoomElement.elementText("SittingFlag");
			request.proCoderResults.stormFlag = intRoomElement.elementText("StormFlag");
			request.proCoderResults.studyFlag = intRoomElement.elementText("StudyFlag");
			request.proCoderResults.sunroomFlag = intRoomElement.elementText("SunroomFlag");
			request.proCoderResults.utilityArea = intRoomElement.elementText("UtilitiesArea");
			request.proCoderResults.utilityCode = intRoomElement.elementText("UtilitiesFlag");
		}

		// int amenities 10
		org.dom4j.Element intAmenitiesElement = element.element("IntAmenities");
		if (intAmenitiesElement != null) {
			request.proCoderResults.fireplace = intAmenitiesElement.elementText("Fireplace");
			request.proCoderResults.fireplaceCount = intAmenitiesElement.elementText("FireplaceCount");
			request.proCoderResults.accessabilityElevatorFlag = intAmenitiesElement.elementText("AccessabilityElevatorFlag");
			request.proCoderResults.accessabilityHandicapFlag = intAmenitiesElement.elementText("AccessabilityHandicapFlag");
			request.proCoderResults.escalatorFlag = intAmenitiesElement.elementText("EscalatorFlag");
			request.proCoderResults.centralVacuumFlag = intAmenitiesElement.elementText("CentralVacuumFlag");
			request.proCoderResults.intercomFlag = intAmenitiesElement.elementText("IntercomFlag");
			request.proCoderResults.soundSystemFlag = intAmenitiesElement.elementText("SoundSystemFlag");
			request.proCoderResults.wetBarFlag = intAmenitiesElement.elementText("WetBarFlag");
			request.proCoderResults.securityAlarmFlag = intAmenitiesElement.elementText("SecurityAlarmFlag");
		}
		// ext struct info  6
		org.dom4j.Element extStructElement = element.element("ExtStructInfo");
		if (extStructElement != null) {
			request.proCoderResults.structureStyle = extStructElement.elementText("StructureStyle");
			request.proCoderResults.exterior1Code = extStructElement.elementText("exterior1Code");
			request.proCoderResults.roofMaterial = extStructElement.elementText("RoofMaterial");
			request.proCoderResults.roofConstruction = extStructElement.elementText("RoofConstruction");
			request.proCoderResults.stormShutterFlag = extStructElement.elementText("StormShutterFlag");
			request.proCoderResults.overheadDoorFlag = extStructElement.elementText("OverheadDoorFlag");
		}
		// ext amenities  9
		org.dom4j.Element extAmenitiesElement = element.element("ExtAmenities");
		if (extAmenitiesElement != null) {
			request.proCoderResults.viewDescription = extAmenitiesElement.elementText("ViewDescription");
			request.proCoderResults.porchCode = extAmenitiesElement.elementText("PorchCode");
			request.proCoderResults.porchArea = extAmenitiesElement.elementText("PorchArea");
			request.proCoderResults.patioArea = extAmenitiesElement.elementText("PatioArea");
			request.proCoderResults.deckFlag = extAmenitiesElement.elementText("DeckFlag");
			request.proCoderResults.deckArea = extAmenitiesElement.elementText("DeckArea");
			request.proCoderResults.featureBalconyFlag = extAmenitiesElement.elementText("FeatureBalconyFlag");
			request.proCoderResults.balconyArea = extAmenitiesElement.elementText("BalconyArea");
			request.proCoderResults.breezewayFlag = extAmenitiesElement.elementText("BreezewayFlag");
		}
		// ext buildings  45
		org.dom4j.Element buildingsElement = element.element("ExtBuildings");
		if (buildingsElement != null) {
			request.proCoderResults.buildingsCount = buildingsElement.elementText("BuildingsCount");
			request.proCoderResults.bathHouseArea = buildingsElement.elementText("BathHouseArea");
			request.proCoderResults.bathHouseFlag = buildingsElement.elementText("BathHouseFlag");
			request.proCoderResults.boatAccessFlag = buildingsElement.elementText("BoatAccessFlag");
			request.proCoderResults.boatHouseArea = buildingsElement.elementText("BoatHouseArea");
			request.proCoderResults.boatHouseFlag = buildingsElement.elementText("BoatHouseFlag");
			request.proCoderResults.cabinArea = buildingsElement.elementText("CabinArea");
			request.proCoderResults.cabinFlag = buildingsElement.elementText("CabinFlag");
			request.proCoderResults.canopyArea = buildingsElement.elementText("CanopyArea");
			request.proCoderResults.canopyFlag = buildingsElement.elementText("CanopyFlag");
			request.proCoderResults.gazeboArea = buildingsElement.elementText("GazeboArea");
			request.proCoderResults.gazeboFlag = buildingsElement.elementText("GazeboFlag");
			request.proCoderResults.granaryArea = buildingsElement.elementText("GranaryArea");
			request.proCoderResults.granaryFlag = buildingsElement.elementText("GranaryFlag");
			request.proCoderResults.greenHouseArea = buildingsElement.elementText("GreenHouseArea");
			request.proCoderResults.greenHouseFlag = buildingsElement.elementText("GreenHouseFlag");
			request.proCoderResults.guestHouseArea = buildingsElement.elementText("GuestHouseArea");
			request.proCoderResults.guestHouseFlag = buildingsElement.elementText("GuestHouseFlag");
			request.proCoderResults.kennelArea = buildingsElement.elementText("KennelArea");
			request.proCoderResults.kennelFlag = buildingsElement.elementText("KennelFlag");
			request.proCoderResults.leanToArea = buildingsElement.elementText("LeanToArea");
			request.proCoderResults.leanToFlag = buildingsElement.elementText("LeanToFlag");
			request.proCoderResults.loadingPlatformArea = buildingsElement.elementText("LoadingPlatformArea");
			request.proCoderResults.loadingPlatformFlag = buildingsElement.elementText("LoadingPlatformFlag");
			request.proCoderResults.milkHouseArea = buildingsElement.elementText("MilkHouseArea");
			request.proCoderResults.milkHouseFlag = buildingsElement.elementText("MilkHouseFlag");
			request.proCoderResults.outdoorKitchenFireplaceFlag = buildingsElement.elementText("OutdoorKitchenFireplaceFlag");
			request.proCoderResults.poolHouseArea = buildingsElement.elementText("PoolHouseArea");
			request.proCoderResults.poolHouseFlag = buildingsElement.elementText("PoolHouseFlag");
			request.proCoderResults.poultryHouseArea = buildingsElement.elementText("PoultryHouseArea");
			request.proCoderResults.poultryHouseFlag = buildingsElement.elementText("PoultryHouseFlag");
			request.proCoderResults.quonsetArea = buildingsElement.elementText("QuonsetArea");
			request.proCoderResults.quonsetFlag = buildingsElement.elementText("QuonsetFlag");
			request.proCoderResults.shedArea = buildingsElement.elementText("ShedArea");
			request.proCoderResults.shedCode = buildingsElement.elementText("ShedCode");
			request.proCoderResults.siloArea = buildingsElement.elementText("SiloArea");
			request.proCoderResults.siloFlag = buildingsElement.elementText("SiloFlag");
			request.proCoderResults.stableArea = buildingsElement.elementText("StableArea");
			request.proCoderResults.stableFlag = buildingsElement.elementText("StableFlag");
			request.proCoderResults.storageBuildingArea = buildingsElement.elementText("StorageBuildingArea");
			request.proCoderResults.storageBuildingFlag = buildingsElement.elementText("StorageBuildingFlag");
			request.proCoderResults.utilityBuildingArea = buildingsElement.elementText("UtilityBuildingArea");
			request.proCoderResults.utilityBuildingFlag = buildingsElement.elementText("UtilityBuildingFlag");
			request.proCoderResults.poleStructureArea = buildingsElement.elementText("PoleStructureArea");
			request.proCoderResults.poleStructureFlag = buildingsElement.elementText("PoleStructureFlag");
		}

		// we may have to deal with result codes
		request.resultCodes = Arrays.asList(MDPropertyWebServiceStep.getResultCodes("").toArray(new String[0]));
		request.proCoderResults.valid = true;
	}

	public URL getWebURL(MDPropertyWebServiceData data) {

		return data.realPropertyWebServiceURL;
	}
}
