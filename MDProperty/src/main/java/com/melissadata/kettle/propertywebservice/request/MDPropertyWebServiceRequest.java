package com.melissadata.kettle.propertywebservice.request;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
//import org.pentaho.pms.util.Const;

import com.melissadata.kettle.propertywebservice.request.MDPropertyWebServiceRequest.ProCoderResults;
import com.melissadata.cz.support.IOMetaHandler;

public class MDPropertyWebServiceRequest {

	public IOMetaHandler    ioMeta;
	// Input data received
	public RowMetaInterface inputMeta;
	public Object[]         inputData;
	public int              inputDataSize;
	// Output data being generated
	public RowMetaInterface outputMeta;
	public Object[]         outputData;
	public int              outputDataSize;

	public static class ProCoderResults {



		public String version;
		public String transmissionReference;
		public String transmissionResults;
		public String totalRecords;
		public String records;
		public String recordID;
		public String results;
		//GrpParcel
		public String fipsCode;
		public String county;
		public String unformattedAPN;
		public String formattedAPN;
		public String alternateAPN;
		public String aPNYearChange;
		public String previousAPN;
		public String accountNumber;
		public String yearAdded;
		public String mapBook;
		public String mapPage;
		//GrpLegal
		public String legalDescription;
		public String legalRange;
		public String township;
		public String section;
		public String quarter;
		public String quarterQuarter;
		public String subdivision;
		public String phase;
		public String tractNumber;
		public String block1;
		public String block2;
		public String lotNumber1;
		public String lotNumber2;
		public String lotNumber3;
		public String unit;
		//GrpPropertyAddress
		public String address;
		public String city;
		public String state;
		public String zip;
		public String addressKey;
		public String mak;
		public String baseMAK;
		public String latitude;
		public String longitude;
		//GrpParsedPropertyAddress
		public String parsedRange;
		public String preDirectional;
		public String streetName;
		public String suffix;
		public String postDirectional;
		public String suiteName;
		public String suiteRange;
		//GrpPrimaryOwner
		public String name1Full;
		public String name1First;
		public String name1Middle;
		public String name1Last;
		public String name1Suffix;
		public String ownerTrustFlag;
		public String ownerCompanyFlag;
		public String name2Full;
		public String name2First;
		public String name2Middle;
		public String name2Last;
		public String name2Suffix;
		public String ownerType;
		public String ownerVestingType;
		//GrpSecondaryOwner
		public String name3Full;
		public String name3First;
		public String name3Middle;
		public String name3Last;
		public String name3Suffix;
		public String name4Full;
		public String name4First;
		public String name4Middle;
		public String name4Last;
		public String name4Suffix;
		public String secondarytype;
		//GrpOwnerAddress
		public String ownerAddress;
		public String ownerCity;
		public String ownerState;
		public String ownerZip;
		public String ownerCarrierRoute;
		public String ownerMAK;
		public String ownerBaseMAK;
		//GrpLastDeedOwnerInfo
		public String ldoName1Full;
		public String ldoName1First;
		public String ldoName1Middle;
		public String ldoName1Last;
		public String ldoName1Suffix;
		public String ldoName2Full;
		public String ldoName2First;
		public String ldoName2Middle;
		public String ldoName2Last;
		public String ldoName2Suffix;
		public String ldoName3Full;
		public String ldoName3First;
		public String ldoName3Middle;
		public String ldoName3Last;
		public String ldoName3Suffix;
		public String ldoName4Full;
		public String ldoName4First;
		public String ldoName4Middle;
		public String ldoName4Last;
		public String ldoName4Suffix;
		//GrpCurrentDeed
		public String mortgageAmount;
		public String mortgageDate;
		public String mortgageLoanTypeCode;
		public String mortgageTermCode;
		public String mortgageTerm;
		public String mortgageDueDate;
		public String lenderCode;
		public String lenderName;
		public String secondMortgageAmount;
		public String secondMortgageLoanTypeCode;
		//GrpTax
		public String yearAssessed;
		public String assessedValueTotal;
		public String assessedValueImprovements;
		public String assessedValueLand;
		public String assessedImprovementsPerc;
		public String previousAssessedValue;
		public String marketValueYear;
		public String marketValueTotal;
		public String marketValueImprovements;
		public String marketValueLand;
		public String marketImprovementsPerc;
		public String taxFiscalYear;
		public String taxRateArea;
		public String taxBilledAmount;
		public String taxDelinquentYear;
		public String lastTaxRollUpdate;
		public String assrLastUpdated;
		public String taxExemptionHomeowner;
		public String taxExemptionDisabled;
		public String taxExemptionSenior;
		public String taxExemptionVeteran;
		public String taxExemptionWidow;
		public String taxExemptionAdditional;
		//GrpPropertyUseInfo
		public String yearBuilt;
		public String yearBuiltEffective;
		public String zonedCodeLocal;
		public String propertyUseMuni;
		public String propertyUseGroup;
		public String propertyUseStandardized;
		//GrpSaleInfo
		public String assessorLastSaleDate;
		public String assessorLastSaleAmount;
		public String assessorPriorSaleDate;
		public String assessorPriorSaleAmount;
		public String lastOwnershipTransferDate;
		public String lastOwnershipTransferDocumentNumber;
		public String lastOwnershipTransferTxID;
		public String deedLastSaleDocumentBook;
		public String deedLastSaleDocumentPage;
		public String deedLastDocumentNumber;
		public String deedLastSaleDate;
		public String deedLastSalePrice;
		public String deedLastSaleTxID;
		//GrpPropertySize
		public String areaBuilding;
		public String areaBuildingDefinitionCode;
		public String areaGross;
		public String area1stFloor;
		public String area2ndFloor;
		public String areaUpperFloors;
		public String areaLotAcres;
		public String areaLotSF;
		public String lotDepth;
		public String lotWidth;
		public String atticArea;
		public String atticFlag;
		public String basementArea;
		public String basementAreaFinished;
		public String basementAreaUnfinished;
		public String parkingGarage;
		public String parkingGarageArea;
		public String parkingCarport;
		public String parkingCarportArea;
		//GrpPool
		public String pool;
		public String poolArea;
		public String saunaFlag;
		public String foundation;
		//GrpIntStructInfo
		public String construction;
		public String interiorStructure;
		public String plumbingFixturesCount;
		public String constructionFireResistanceClass;
		public String safetyFireSprinklersFlag;
		public String flooringMaterialPrimary;
		public String bathCount;
		//GrpIntRoomIno
		public String bathPartialCount;
		public String bedroomsCount;
		public String roomsCount;
		public String storiesCount;
		public String unitsCount;
		public String bonusRoomFlag;
		public String breakfastNookFlag;
		public String cellarFlag;
		public String cellarWineFlag;
		public String excerciseFlag;
		public String familyCode;
		public String gameFlag;
		public String greatFlag;
		public String hobbyFlag;
		public String laundryFlag;
		public String mediaFlag;
		public String mudFlag;
		public String officeArea;
		public String officeFlag;
		public String safeRoomFlag;
		public String sittingFlag;
		public String stormFlag;
		public String studyFlag;
		public String sunroomFlag;
		public String utilityArea;
		public String utilityCode;
		//GrpIntAmenities
		public String fireplace;
		public String fireplaceCount;
		public String accessabilityElevatorFlag;
		public String accessabilityHandicapFlag;
		public String escalatorFlag;
		public String centralVacuumFlag;
		public String intercomFlag;
		public String soundSystemFlag;
		public String wetBarFlag;
		public String securityAlarmFlag;
		//GrpExtStructInfo
		public String structureStyle;
		public String exterior1Code;
		public String roofMaterial;
		public String roofConstruction;
		public String stormShutterFlag;
		public String overheadDoorFlag;
		//GrpExtAmenities
		public String viewDescription;

		public String porchCode;
		public String porchArea;
		public String patioArea;
		public String deckFlag;
		public String deckArea;
		public String featureBalconyFlag;
		public String balconyArea;
		public String breezewayFlag;

		//GrpExtBuildings
		public String buildingsCount;
		public String bathHouseArea;
		public String bathHouseFlag;
		public String boatAccessFlag;
		public String boatHouseArea;
		public String boatHouseFlag;
		public String cabinArea;
		public String cabinFlag;
		public String canopyArea;
		public String canopyFlag;
		public String gazeboArea;
		public String gazeboFlag;
		public String granaryArea;
		public String granaryFlag;
		public String greenHouseArea;
		public String greenHouseFlag;
		public String guestHouseArea;
		public String guestHouseFlag;
		public String kennelArea;
		public String kennelFlag;
		public String leanToArea;
		public String leanToFlag;
		public String loadingPlatformArea;
		public String loadingPlatformFlag;
		public String milkHouseArea;
		public String milkHouseFlag;
		public String outdoorKitchenFireplaceFlag;
		public String poolHouseArea;
		public String poolHouseFlag;
		public String poultryHouseArea;
		public String poultryHouseFlag;
		public String quonsetArea;
		public String quonsetFlag;
		public String shedArea;
		public String shedCode;
		public String siloArea;
		public String siloFlag;
		public String stableArea;
		public String stableFlag;
		public String storageBuildingArea;
		public String storageBuildingFlag;
		public String utilityBuildingArea;
		public String utilityBuildingFlag;
		public String poleStructureArea;
		public String poleStructureFlag;
		//GrpUtilities
		public String HVACCoolingDetail;
		public String HVACHeatingDetail;
		public String HVACHeatingFuel;
		public String sewageUsage;
		public String waterSource;
		public String mobileHomeHookupFlag;
		//GrpParking
		public String rvParkingFlag;

		public String parkingSpaceCount;
		public String drivewayArea;
		public String drivewayMaterial;
		//GrpYardGardenInfo
		public String topographyCode;

		public String fenceCode;
		public String fenceArea;
		public String courtyardFlag;
		public String courtyardArea;
		public String arborPergolaFlag;
		public String sprinklersFlag;
		public String golfCourseGreenFlag;
		public String tennisCourtFlag;
		public String sportsCourtFlag;
		public String arenaFlag;
		public String waterFeatureFlag;
		public String pondFlag;
		public String boatLiftFlag;
		//GrpEstimatedValue
		public String estimatedValue;

		public String  estimatedMinValue;
		public String  estimatedMaxValue;
		public String  confidenceScore;
		public String  valuationDate;
		// Shape
		public String  wellKnownText;


		public boolean valid;
	}

	public List<String> resultCodes = new ArrayList<String>();
	public ProCoderResults proCoderResults;

	/**
	 * Create one check request object.
	 *
	 * @param inputData
	 */
	public MDPropertyWebServiceRequest(IOMetaHandler ioMeta, Object[] inputData) {

		this.ioMeta = ioMeta;

		// Get a copy of the input data
		inputMeta = ioMeta.inputMeta;
		this.inputData = RowDataUtil.createResizedCopy(inputData, inputMeta.size());
		inputDataSize = inputMeta.size();

		// Create the initial output data
		outputMeta = ioMeta.outputMeta;
		outputData = RowDataUtil.allocateRowData(outputMeta.size());
		outputDataSize = 0;

		proCoderResults = new ProCoderResults();
	}

	/**
	 * Called to add one column to the value data for this request.
	 * The text is trimmed (if not null). If trimmed text is empty then convert to a null element
	 *
	 * @param value
	 */
	public void addOutputData(String value) {

		if ((value != null)) {
			value = value.trim();
		}

//		if(Const.isEmpty(value)){
//			value = "pos " + String.valueOf(outputDataSize);
//		} else {
//			value += "___" + String.valueOf(outputDataSize);
//		}

		outputData = RowDataUtil.addValueData(outputData, outputDataSize++, value);
	}
}
