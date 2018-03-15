package com.melissadata.kettle.cv;

import com.melissadata.kettle.MDCheckRequest;
import com.melissadata.kettle.support.IOMeta;

import java.util.HashSet;
import java.util.Set;

/**
 * Structure for contact verification requests
 */
public class MDCheckCVRequest extends MDCheckRequest {
	// FIXME: Pool of result objects?
	public static class AddrKeyResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		AddressKey;
		public boolean		valid;
	}

	public static class AddrResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		Address1;
		public String		Address2;
		public String		PrivateMailBox;
		public String		Suite;
		public String		UrbanizationName;
		public String		CityName;
		public String		StateAbbreviation;
		public String		Zip;
		public String		Plus4;
		public String		DeliveryPointCode;
		public String		DeliveryPointCheckDigit;
		public String		CountryAbbreviation;
		public String		CountryName;
		public String		CMRA;
		public String		ELotNumber;
		public String		ELotOrder;
		public String		RBDI;
		public String		TypeAddressCode;
		public String		TypeAddressDescription;
		public String		TypeZipCode;
		public String		TypeZipDescription;
		public String		CarrierRoute;
		public String		CityAbbreviation;
		public String		CongressionalDistrict;
		public String		Company;
		public String		StateName;
		public String		ParsedAddressRange;
		public String		ParsedDirectionPre;
		public String		ParsedStreetName;
		public String		ParsedSuffix;
		public String		ParsedDirectionPost;
		public String		ParsedSuiteName;
		public String		ParsedSuiteRange;
		public String		ParsedPMBName;
		public String		ParsedPMBRange;
		public String		ParsedRouteService;
		public String		ParsedLockBox;
		public String		ParsedDeliveryInstallation;
		public String		ParsedExtraInformation;
		public String		CountyName;
		public String		CountyFips;
		public String		AddressKey;
		public String		MAK;
		public String		BaseMAK;
		public String		TimeZone;
		public String		TimeZoneCode;
		public boolean		valid;
	}

	public static class EmailResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		MailboxName;
		public String		DomainName;
		public String		EmailAddress;
		public String		TopLevelDomainName;
		public String		TopLevelDomainDescription;
		public boolean		valid;
	}

	public static class GeoCoderResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		CountyName;
		public String		CountyFips;
		public String		PlaceCode;
		public String		PlaceName;
		public String		TZName;
		public String		TZCode;
		public String		Latitude;
		public String		Longitude;
		public String		CBSACode;
		public String		CBSALevel;
		public String		CBSATitle;
		public String		CBSADivisionCode;
		public String		CBSADivisionLevel;
		public String		CBSADivisionTitle;
		public String		CensusBlock;
		public String		CensusTract;
		public boolean		valid;
	}

	public static class IPLocatorResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		ipAddress;
		public String		latitude;
		public String		longitude;
		public String		zip;
		public String		region;
		public String		name;
		public String		domain;
		public String		cityName;
		public String		country;
		public String		abbreviation;
		public String 		connectionSpeed;
		public String		connectionType;
		public String		utc;
		public String		continent;
		public boolean		valid;
	}

	public static class NameResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		Prefix;
		public String		First;
		public String		Middle;
		public String		Last;
		public String		Suffix;
		public String		Gender;
		public String		Prefix2;
		public String		First2;
		public String		Middle2;
		public String		Last2;
		public String		Suffix2;
		public String		Gender2;
		public String		Salutation;
		public String		StandardCompanyName;
		public boolean		valid;
	}

	public static class PhoneResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		NewAreaCode;
		public String		AreaCode;
		public String		Prefix;
		public String		Suffix;
		public String		City;
		public String		State;
		public String		CountryAbbreviation;
		public String		TZName;
		public String		TZCode;
		public String		Extension;
		public String		CountyName;
		public String		CountyFips;
		public String		CountryName;
		public String		Latitude;
		public String		Longitude;
		public boolean		valid;
	}

	public static class RBDIResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		RDBIndicator;
		public boolean		valid;
	}
	// Intermediate results
	public NameResults		nameResults;
	public AddrResults		addrResults;
	public EmailResults		emailResults;
	public GeoCoderResults	geoResults;
	public RBDIResults		rbdiResults;
	public PhoneResults		phoneResults;
	public AddrKeyResults	addrKeyResults;
	public IPLocatorResults	ipLocatorResults;

	public NameResults getNameCommunityResults(NameResults nr){

		nr.First = "";
		nr.First2 = "";
		nr.Gender = "";
		nr.Gender2 = "";
		nr.Middle = "";
		nr.Middle2 = "";
		nr.StandardCompanyName = "";

		return nr;
	}

	public AddrResults getAddrCommunityResults(AddrResults ar){
//		 Address1;
//		 Address2;
//		 PrivateMailBox;
//		 Suite;
		 ar.UrbanizationName  = "";
//		 CityName;
//		 StateAbbreviation;
//		 Zip;
//		 Plus4;
		ar.DeliveryPointCode  = "";
		ar.DeliveryPointCheckDigit  = "";
//		 CountryAbbreviation;
//		 CountryName;
		ar.CMRA  = "";
		ar.ELotNumber  = "";
		ar.ELotOrder  = "";
		ar.RBDI  = "";
		ar.TypeAddressCode  = "";
		ar.TypeAddressDescription  = "";
		ar.TypeZipCode  = "";
		ar.TypeZipDescription  = "";
		ar.CarrierRoute  = "";
//		 CityAbbreviation;
		ar.CongressionalDistrict  = "";
		ar.Company  = "";
//		 StateName;
//		 ParsedAddressRange;
//		 ParsedDirectionPre;
//		 ParsedStreetName;
//		 ParsedSuffix;
//		 ParsedDirectionPost;
//		 ParsedSuiteName;
//		 ParsedSuiteRange;
//		 ParsedPMBName;
//		 ParsedPMBRange;
//		 ParsedRouteService;
//		 ParsedLockBox;
//		 ParsedDeliveryInstallation;
//		 ParsedExtraInformation;
//		 CountyName;
		ar.CountyFips  = "";
		ar.AddressKey = "";
		ar.MAK  = "";
		ar.BaseMAK = "";
		ar.TimeZone  = "";
		ar.TimeZoneCode  = "";

		return ar;

	}

	public EmailResults getEmailCommunityResults(EmailResults er){

		er.MailboxName = "";
		er.DomainName = "";
		//er.EmailAddress = "";
		er.TopLevelDomainName = "";
		er.TopLevelDomainDescription = "";
		return er;
	}

	public GeoCoderResults getGeoCommunityResults(GeoCoderResults gcr){

		  gcr.CountyName = "";
		  gcr.CountyFips = "";
		  gcr.PlaceCode = "";
		  gcr.PlaceName = "";
		  gcr.TZName = "";
		  gcr.TZCode = "";
		  gcr.Latitude = "";
		  gcr.Longitude = "";
		  gcr.CBSACode = "";
		  gcr.CBSALevel = "";
		  gcr.CBSATitle = "";
		  gcr.CBSADivisionCode = "";
		  gcr.CBSADivisionLevel = "";
		  gcr.CBSADivisionTitle = "";
		  gcr.CensusBlock = "";
		  gcr.CensusTract = "";

		  return gcr;
	}

	public RBDIResults getRBDICommunityResults(RBDIResults rbr){

		rbr.RDBIndicator = "";
		return rbr;
	}

	public PhoneResults getPhoneCommunityResults(PhoneResults pr){

		pr.NewAreaCode = "";
//		pr.AreaCode = "";
//		pr.Prefix = "";
//		pr.Suffix = "";
		pr.City = "";
		pr.State = "";
		pr.CountryAbbreviation = "";
		pr.TZName = "";
		pr.TZCode = "";
//		pr.Extension = "";
		pr.CountyName = "";
		pr.CountyFips = "";
		pr.CountryName = "";
		pr.Latitude = "";
		pr.Longitude = "";

		return pr;
	}

	public AddrKeyResults getAddrKeyCommunity(AddrKeyResults akr){

		akr.AddressKey = "";
		return akr;
	}

	public MDCheckCVRequest(IOMeta ioData, Object[] inputData) {
		super(ioData, inputData);
	}
}
