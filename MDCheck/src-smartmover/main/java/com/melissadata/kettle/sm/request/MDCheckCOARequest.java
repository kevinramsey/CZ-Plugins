package com.melissadata.kettle.sm.request;

import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.support.IOMeta;

import java.util.HashSet;
import java.util.Set;

/**
 * Structure for change of address requests
 */
public class MDCheckCOARequest extends MDCheckCVRequest {
	public static class COAResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		FullName;
		public String		NamePrefix;
		public String		NameFirst;
		public String		NameMiddle;
		public String		NameLast;
		public String		NameSuffix;
		public String		CompanyName;
		public String		Address1;
		public String		Address2;
		public String		Suite;
		public String		PrivateMailBox;
		public String		Urbanization;
		public String		CityName;
		public String		StateAbbreviation;
		public String		Zip;
		public String		Plus4;
		public String		CountryName;
		public String		DeliveryPointCode;
		public String		DeliveryPointCheckDigit;
		public String		CarrierRoute;
		public String		DPVCMRA;
		public String		AddressKey;
		public String		MelissaAddressKey;
		public String		BaseMelissaAddressKey;
		public String		TypeAddressCode;
		public String		TypeAddressDescription;
		public String		TypeZipCode;
		public String		TypeZipDescription;
		public String		CityAbbreviation;
		public String		CountryAbbreviation;
		public String		DPVFootnotes;
		public String		ParsedAddressRange;					// **
		public String		ParsedDirectionPre;
		public String		ParsedStreetName;
		public String		ParsedSuffix;
		public String		ParsedDirectionPrePost;
		public String		ParsedSuiteName;
		public String		ParsedSuiteRange;
		public String		ParsedPMBName;
		public String		ParsedPMBRange;
		public String		ParsedExtraInformation;
		public String		ParsedRouteService;
		public String		ParsedLockBox;
		public String		ParsedDeliveryInstallation;
		public String		EffectiveDate;
		public String		MoveTypeCode;
		public String		MoveReturnCode;
		public boolean		valid;
	}
	public COAResults	coaResults;

	public MDCheckCOARequest(IOMeta ioMeta, Object[] inputData) {
		super(ioMeta, inputData);
	}
}
