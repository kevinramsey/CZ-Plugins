package com.melissadata.kettle;

import com.melissadata.kettle.support.IOMeta;
import com.melissadata.*;
import com.melissadata.kettle.report.ReportStats;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import java.io.File;
import java.net.URL;
import java.util.List;

public class MDCheckData extends BaseStepData implements StepDataInterface {

	// Customer defined defaults
	public static boolean                    defaultsSet                      = false;
	public static boolean                    smDefaultsSet                    = false;
	public static boolean                    muDefaultsSet                    = false;
	public static boolean                    ipDefaultsSet                    = false;
	// Name Tab
	public static String                     prefix1                          = null;
	public static String                     firstName1                       = null;
	public static String                     middleName1                      = null;
	public static String                     lastName1                        = null;
	public static String                     suffix1                          = null;
	public static String                     gender1                          = null;
	public static String                     prefix2                          = null;
	public static String                     firstName2                       = null;
	public static String                     middleName2                      = null;
	public static String                     lastName2                        = null;
	public static String                     suffix2                          = null;
	public static String                     gender2                          = null;
	public static String                     salutation                       = null;
	public static String                     standardizedCompany              = null;
	// Address Verify Tab
	public static String                     address                          = null;
	public static String                     address2                         = null;
	public static String                     city                             = null;
	public static String                     state                            = null;
	public static String                     zip                              = null;
	public static String                     country                          = null;
	public static String                     addressKey                       = null;
	public static String                     MAK                              = null;
	public static String                     baseMAK                          = null;
	// Additional output
	public static String                     outputSuite                      = null;
	public static String                     outputPrivateMailBox             = null;
	public static String                     outputUrbanization               = null;
	public static String                     outputPlus4                      = null;
	public static String                     outputDPAndCheckDigit            = null;
	public static String                     outputCarrierRoute               = null;
	public static String                     outputZipTypeCode                = null;
	public static String                     outputAddressTypeCode            = null;
	public static String                     outputAddressTypeDescription     = null;
	public static String                     outputCMRA                       = null;
	public static String                     outputElotNumber                 = null;
	public static String                     outputElotOrder                  = null;
	public static String                     outputDeliveryIndication         = null;
	// Geographic info
	public static String                     outputCityAbbreviation           = null;
	public static String                     outputCountyName                 = null;
	public static String                     outputCountyFips                 = null;
	public static String                     outputCongressionalDistrict      = null;
	public static String                     outputTimezone                   = null;
	public static String                     outputTimezoneCode               = null;
	// Parsed address info
	public static String                     outputParsedAddressRange         = null;
	public static String                     outputParsedPreDirectional       = null;
	public static String                     outputParsedStreetName           = null;
	public static String                     outputParsedSuffix               = null;
	public static String                     outputParsedPostDirectional      = null;
	public static String                     outputParsedSuiteName            = null;
	public static String                     outputParsedSuiteRange           = null;
	public static String                     outputParsedPMBName              = null;
	public static String                     outputParsedPMBRange             = null;
	public static String                     outputParsedRouteService         = null;
	public static String                     outputParsedLockBox              = null;
	public static String                     outputParsedDeliveryInstallation = null;
	public static String                     outputParsedExtraInformation     = null;
	/*
	 * EXTRA not currently used
	 * public static String outputCompany;
	 * public static String outputStateName;
	 * public static String outputZipTypeDescription;
	 * public static String outputCountryName;
	 */
	// Geo Coder
	public static String                     GeoLatitude                      = null;
	public static String                     GeoLongitude                     = null;
	// Additional Geographic info
	public static String                     GeoCounty                        = null;
	public static String                     GeoCountyFIPS                    = null;
	public static String                     GeoPlaceCode                     = null;
	public static String                     GeoPlaceName                     = null;
	public static String                     GeoTimeZone                      = null;
	public static String                     GeoTimeZoneCode                  = null;
	// Census info
	public static String                     GeoCBSACode                      = null;
	public static String                     GeoCBSALevel                     = null;
	public static String                     GeoCBSATitle                     = null;
	public static String                     GeoCBSADivisionCode              = null;
	public static String                     GeoCBSADivisionLevel             = null;
	public static String                     GeoCBSADivisionTitle             = null;
	public static String                     GeoCensusBlock                   = null;
	public static String                     GeoCensusTract                   = null;
	// Phone/Email
	// phone output
	public static String                     phone                            = null;
	public static String                     phoneFormat                      = null;
	public static String                     phoneAreaCode                    = null;
	public static String                     phonePrefix                      = null;
	public static String                     phoneSuffix                      = null;
	public static String                     phoneExtension                   = null;
	// Additional phone output
	public static String                     phoneCity                        = null;
	public static String                     phoneStateProvince               = null;
	public static String                     phoneCountyName                  = null;
	public static String                     phoneCountyFIPS                  = null;
	public static String                     phoneCountryCode                 = null;
	public static String                     phoneTimeZone                    = null;
	public static String                     phoneTimeZoneCode                = null;
	// Email output
	public static String                     emailAddress                     = null;
	// Additional Email output
	public static String                     emailMailBoxName                 = null;
	public static String                     emailTopLevelDomainDescription   = null;
	public static String                     emailDomainName                  = null;
	public static String                     emailTopLevelDomain              = null;
	// Smart Mover
	// output name
	public static String                     sm_fullName                      = null;
	public static String                     sm_prefix                        = null;
	public static String                     sm_firstName                     = null;
	public static String                     sm_middleName                    = null;
	public static String                     sm_lastName                      = null;
	public static String                     sm_suffix                        = null;
	// output address
	public static String                     sm_company                       = null;
	public static String                     sm_address                       = null;
	public static String                     sm_address2                      = null;
	public static String                     sm_city                          = null;
	public static String                     sm_state                         = null;
	public static String                     sm_zip                           = null;
	public static String                     sm_addressKey                    = null;
	public static String                     sm_MelissaAddressKey             = null;
	public static String                     sm_BaseMelissaAddressKey         = null;
	public static String                     sm_effectiveDate                 = null;
	public static String                     sm_moveTypeCode                  = null;
	public static String                     sm_moveReturnCode                = null;
	// additional output
	public static String                     sm_Suite                         = null;
	public static String                     sm_PrivateMailBox                = null;
	public static String                     sm_Plus4                         = null;
	public static String                     sm_DPAndCheckDigit               = null;
	public static String                     sm_CarrierRoute                  = null;
	public static String                     sm_Urbanization                  = null;
	public static String                     sm_cityAbbrevation               = null;
	public static String                     sm_country                       = null;
	public static String                     sm_countryAbbreviation           = null;
	// Parsed address info
	public static String                     sm_ParsedAddressRange            = null;
	public static String                     sm_ParsedPreDirectional          = null;
	public static String                     sm_ParsedStreetName              = null;
	public static String                     sm_ParsedSuffix                  = null;
	public static String                     sm_ParsedPostDirectional         = null;
	public static String                     sm_ParsedSuiteName               = null;
	public static String                     sm_ParsedSuiteRange              = null;
	public static String                     sm_ParsedPMBName                 = null;
	public static String                     sm_ParsedPMBRange                = null;
	public static String                     sm_ParsedExtraInformation        = null;
	public static String                     sm_DpvFootnotes                  = null;
	// MatchUp info
	public static String                     mu_DupeGroup                     = null;
	public static String                     mu_DupeCount                     = null;
	public static String                     mu_MatchcodeKey                  = null;
	public static String                     mu_ResultCodes                   = null;
	// IP Locator info
	public static String                     ip_Address                       = null;
	public static String                     ip_Longitude                     = null;
	public static String                     ip_Latitude                      = null;
	public static String                     ip_Zip                           = null;
	public static String                     ip_Region                        = null;
	public static String                     ip_Name                          = null;
	public static String                     ip_Domain                        = null;
	public static String                     ip_CityName                      = null;
	public static String                     ip_Country                       = null;
	public static String                     ip_Abreviation                   = null;
	public static String                     ip_ConnectionSpeed               = null;
	public static String                     ip_ConnectionType                = null;
	public static String                     ip_UTC                           = null;
	public static String                     ip_Continent                     = null;
	// Structure for handling of input/output records
	public        IOMeta                     sourceIO                         = null;
	public        IOMeta                     lookupIO                         = null;
	// Customer id
	public        int                        realCustomerID                   = 0;
	// PAF id
	public        String                     realPAFID                        = null;
	// Maximum requests per batch
	public        int                        maxRequests                      = 0;
	// URL for the web services
	public        URL                        realWebNameParserURL             = null;
	public        URL                        realWebAddressVerifierURL        = null;
	public        URL                        realWebGeoCoderURL               = null;
	public        URL                        realWebPhoneVerifierURL          = null;
	public        URL                        realWebEmailVerifierURL          = null;
	public        URL                        realWebRBDIndicatorURL           = null;
	public        URL                        realWebIPLocatorURL              = null;
	public        URL                        realCVSNameParserURL             = null;
	public        URL                        realCVSAddressVerifierURL        = null;
	public        URL                        realCVSGeoCoderURL               = null;
	public        URL                        realCVSPhoneVerifierURL          = null;
	public        URL                        realCVSEmailVerifierURL          = null;
	public        URL                        realCVSRBDIndicatorURL           = null;
	public        URL                        realCVSIPLocatorURL              = null;
	public        URL                        realNCOAURL                      = null;
	public        URL                        realCCOAURL                      = null;
	// Timeout settings
	public        int                        realWebTimeout                   = 0;
	public        int                        realWebRetries                   = 0;
	public        int                        realCVSTimeout                   = 0;
	public        int                        realCVSRetries                   = 0;
	// Abort handling
	public        boolean                    webAbortOnError                  = false;
	public        boolean                    cvsAbortOnError                  = false;
	// Optional proxy settings
	public        String                     realProxyHost                    = null;
	public        int                        realProxyPort                    = 0;
	public        String                     realProxyUser                    = null;
	public        String                     realProxyPass                    = null;
	// License and data path
// public String realLicense = null;
	public        String                     realDataPath                     = null;
	// Path for temporary work files
	public        File                       realWorkPath                     = null;
	// Object references
	public        mdName                     Name                             = null;
	public        mdAddr                     Addr                             = null;
	public        mdGeo                      Geo                              = null;
	public        mdZip                      Zip                              = null;
	public        mdPhone                    Phone                            = null;
	public        mdEmail                    Email                            = null;
	public        mdIpLocator                ipLocator                        = null;
	// Matchup stuff
	public        boolean                    UsingLookup                      = false;
	public        mdMUReadWrite              ReadWrite                        = null;
	public        mdMUHybrid                 Hybrid                           = null;
	// for statistical purposes
	public        int                        numNameBlanks                    = 0;
	public        int                        numCompanyNameBlanks             = 0;
	public        int                        numPhoneErrors                   = 0;
	public        int                        numEmailBlanks                   = 0;
	public        int                        numValidDomain                   = 0;
	public        ReportStats                numAddrChanges                   = null;
	public        ReportStats                numAddrErrors                    = null;
	public        ReportStats                numAddrValidations               = null;
	public        ReportStats                numGeoOverview                   = null;
	public        ReportStats                numAddrOverview                  = null;
	public        ReportStats                numPhoneOverview                 = null;
	public        ReportStats                numEmailOverview                 = null;
	public        ReportStats                numNameOverview                  = null;
	public        ReportStats                resultStats                      = null;
	// Contains the tracking objects for the requests being sent and the 
	// responses being received
	public        List<List<MDCheckRequest>> requests                         = null;
}
