package com.melissadata.kettle;

import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.installer.PluginInstaller;
import com.melissadata.kettle.support.MDCustomDefaults;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class AdvancedConfigurationMeta implements Cloneable {

	public static final  String              TAG_MELISSADATA_SPECIAL_USAGE           = "melissadata_special_usage";
	// Primary License string and customer id
	public static final  String              TAG_PRIMARY_LICENSE                     = "license";
	public static final  String              TAG_PRIMARY_RET_VAL                     = "license_retVal";
	public static final  String              TAG_PRIMARY_EXPIRATION                  = "license_expiration";
	public static final  String              TAG_TRIAL_LICENSE                       = "trial_license";
	public static final  String              TAG_TRIAL_RET_VAL                       = "trial_retVal";
	public static final  String              TAG_TRIAL_EXPIRATION                    = "trial_license_expiration";
	// Additional Address output
	public static final  String              TAG_SUITE                               = "default_Suite";
	public static final  String              TAG_PRIVATE_MAILBOX                     = "default_Private_Mailbox";
	public static final  String              TAG_URBANIZATION                        = "default_Urbanization";
	public static final  String              TAG_PLUS4                               = "default_Plus4";
	public static final  String              TAG_DP_CHECK_DIGIT                      = "default_DP_Check_Digit";
	public static final  String              TAG_CARRIER_ROUTE                       = "default_Carrier_Route";
	public static final  String              TAG_ZIP_TYPE_CODE                       = "default_Zip_Type_Code";
	public static final  String              TAG_ADDRESS_TYPE_CODE                   = "default_Address_Type_Code";
	public static final  String              TAG_ADDRESS_TYPE_DESCRIPTION            = "default_Address_Type_Description";
	public static final  String              TAG_CMRA                                = "default_CMRA";
	public static final  String              TAG_ELOT_NUMBER                         = "default_Elot_Number";
	public static final  String              TAG_ELOT_ORDER                          = "default_Elot_Order";
	public static final  String              TAG_DELIVERY_INDICATION                 = "default_Delivery_Indication";
	// Address Geographic info
	public static final  String              TAG_CITY_ABBREVATION                    = "default_City_Abbreviation";
	public static final  String              TAG_COUNTY_NAME                         = "default_County_Name";
	public static final  String              TAG_COUNTY_FIPS                         = "default_County_Fips";
	public static final  String              TAG_CONGRESSIONAL_DISTRICT              = "default_Congressional_District";
	public static final  String              TAG_TIME_ZONE                           = "default_Timezone";
	public static final  String              TAG_TIME_ZONE_CODE                      = "default_Timezone_Code";
	// parsed address info
	public static final  String              TAG_PARSED_ADDRESS_RANGE                = "default_Parsed_Address_Range";
	public static final  String              TAG_PARSED_PRE_DIRECTIONAL              = "default_Parsed_Pre_Directional";
	public static final  String              TAG_PARSED_STREET_NAME                  = "default_Parsed_Street_Name";
	public static final  String              TAG_PARSED_SUFFIX                       = "default_Parsed_Suffix";
	/*
	 * EXTRA not curently used public static final String TAG_COMPANY =
	 * "default_Company"; public static final String TAG_STATE_NAME =
	 * "default_State_Name"; public static final String TAG_ZIP_TYPE_DESCRIPTION
	 * = "default_Zip_Type_Description"; public static final String
	 * TAG_COUNTRY_NAME = "default_Country_Name";
	 */
	public static final  String              TAG_PARSED_POST_DIRECTIONAL             = "default_Parsed_Post_Directional";
	public static final  String              TAG_PARSED_SUITE_NAME                   = "default_Parsed_Suite_Name";
	public static final  String              TAG_PARSED_SUITE_RANGE                  = "default_Parsed_Suite_Range";
	public static final  String              TAG_PARSED_PMB_NAME                     = "default_Parsed_PMB_Name";
	public static final  String              TAG_PARSED_PMB_RANGE                    = "default_Parsed_PMB_Range";
	public static final  String              TAG_PARSED_ROUTE_SERVICE                = "default_Parsed_Route_Service";
	public static final  String              TAG_PARSED_LOCK_BOX                     = "default_Parsed_Lock_Box";
	public static final  String              TAG_PARSED_DELIVERY_INSTALLATION        = "default_Parsed_Delivery_Installation";
	public static final  String              TAG_PARSED_EXTRA_INFORMATION            = "default_Parsed_Extra_Information";
	// GeoCoder
	public static final  String              TAG_GEO_LATITUDE                        = "default_latitude";
	public static final  String              TAG_GEO_LONGITUDE                       = "default_longitude";
	// Additional Geographic info
	public static final  String              TAG_GEO_COUNTY                          = "default_Geo_County";
	public static final  String              TAG_GEO_COUNTYFIPS                      = "default_Geo_County_FIPS";
	public static final  String              TAG_GEO_PLACE_CODE                      = "default_Geo_Place_Code";
	public static final  String              TAG_GEO_PLACE_NAME                      = "default_Geo_Place_Name";
	public static final  String              TAG_GEO_TIME_ZONE                       = "default_Geo_Time_Zone";
	public static final  String              TAG_GEO_TIME_ZONE_CODE                  = "default_Geo_Time_Zone_Code";
	// Census info
	public static final  String              TAG_GEO_CBSA_CODE                       = "default_Geo_CBSA_Code";
	public static final  String              TAG_GEO_CBSA_LEVEL                      = "default_Geo_CBSA_Level";
	public static final  String              TAG_GEO_CBSA_TITLE                      = "default_Geo_CBSA_Title";
	public static final  String              TAG_GEO_CBSA_DEVISION_CODE              = "default_Geo_CBSA_Division_Code";
	public static final  String              TAG_GEO_CBSA_DIVISION_LEVEL             = "default_Geo_CBSA_Division_Level";
	public static final  String              TAG_GEO_CBSA_DEVISION_TITLE             = "default_Geo_CBSA_Division_Title";
	public static final  String              TAG_GEO_CENSUS_BLOCK                    = "default_Geo_Census_Block";
	public static final  String              TAG_GEO_CENSUS_TRACT                    = "default_Geo_Census_Tract";
	// Phone/Email
	// phone output
	public static final  String              TAG_PHONE                               = "phone";
	public static final  String              TAG_PHONE_FORMAT                        = "phoneFormat";
	public static final  String              TAG_PHONE_AREA_CODE                     = "phoneAreaCode";
	public static final  String              TAG_PHONE_PREFIX                        = "phonePrefix";
	public static final  String              TAG_PHONE_SUFFIX                        = "phoneSuffix";
	public static final  String              TAG_PHONE_EXTENSION                     = "phoneExtension";
	// Additional phone output
	public static final  String              TAG_PHONE_CITY                          = "phoneCity";
	public static final  String              TAG_PHONE_STATE_PROVINCE                = "phoneStateProvince";
	public static final  String              TAG_PHONE_COUNTY_NAME                   = "phoneCountyName";
	public static final  String              TAG_PHONE_COUNTY_FIPS                   = "phoneCountyFIPS";
	public static final  String              TAG_PHONE_COUNTRY_CODE                  = "phoneCountryCode";
	public static final  String              TAG_PHONE_TIME_ZONE                     = "phoneTimeZone";
	public static final  String              TAG_PHONE_TIME_ZONE_CODE                = "phoneTimeZoneCode";
	// Email output
	public static final  String              TAG_EMAIL_ADDRESS                       = "EmailAddress";
	// Additional Email output
	public static final  String              TAG_EMAIL_MAIL_BOX_NAME                 = "emailMailBoxName";
	public static final  String              TAG_EMAIL_TOP_LEVEL_DOMAIN_DESCRIPTION  = "emailTopLevelDomainDescription";
	public static final  String              TAG_EMAIL_DOMAIN_NAME                   = "emailDomainName";
	public static final  String              TAG_EMAIL_TOP_LEVEL_DOMAIN              = "emailTopLevelDomain";
	// Smart Mover
	// output name
	public static final  String              TAG_SM_FULL_NAME                        = "sm_fullName";
	public static final  String              TAG_SM_PREFIX                           = "sm_prefix";
	public static final  String              TAG_SM_FIRST_NAME                       = "sm_firstName";
	public static final  String              TAG_SM_MIDDLE_NAME                      = "sm_middleName";
	public static final  String              TAG_SM_LAST_NAME                        = "sm_lastName";
	public static final  String              TAG_SM_SUFFIX                           = "sm_suffix";
	// output address
	public static final  String              TAG_SM_COMPANY                          = "sm_company";
	public static final  String              TAG_SM_ADDRESS                          = "sm_address";
	public static final  String              TAG_SM_ADDRESS2                         = "sm_address2";
	public static final  String              TAG_SM_CITY                             = "sm_city";
	public static final  String              TAG_SM_STATE                            = "sm_state";
	public static final  String              TAG_SM_ZIP                              = "sm_zip";
	public static final  String              TAG_SM_ADDRESS_KEY                      = "sm_addressKey";
	public static final  String              TAG_SM_MELISSA_ADDRESS_KEY              = "sm_MelissaAddressKey";
	public static final  String              TAG_SM_BASE_MELISSA_ADDRESS_KEY         = "sm_BaseMelissaAddressKey";
	public static final  String              TAG_SM_EFFICTIVE_DATE                   = "sm_effectiveDate";
	public static final  String              TAG_SM_MOVE_TYPE_CODE                   = "sm_moveType";
	public static final  String              TAG_SM_MOVE_RETURN_CODE                 = "sm_returnCode";
	// additional output
	public static final  String              TAG_SM_SUITE                            = "sm_Suite";
	public static final  String              TAG_SM_PMB                              = "sm_PrivateMailBox";
	public static final  String              TAG_SM_PLUS4                            = "sm_Plus4";
	public static final  String              TAG_SM_DP_CHECK_DIGIT                   = "sm_DPAndCheckDigit";
	public static final  String              TAG_SM_CARRIER_ROUTE                    = "sm_CarrierRoute";
	public static final  String              TAG_SM_URBANISATION                     = "sm_Urbanization";
	public static final  String              TAG_SM_CITY_ABBREVATION                 = "sm_cityAbbrevation";
	public static final  String              TAG_SM_COUNTRY                          = "sm_country";
	public static final  String              TAG_SM_COUNTRY_ABBREVATION              = "sm_countryAbbreviation";
	public static final  String              TAG_SM_DPV_FOOTNOTES                    = "sm_dpv_footnotes";
	// Parsed address info
	public static final  String              TAG_SM_PARSED_ADDRESS_RANGE             = "sm_ParsedAddressRange";
	public static final  String              TAG_SM_PARSED_PRE_DIRECTIONAL           = "sm_ParsedPreDirectional";
	public static final  String              TAG_SM_PARSED_STREET_NAME               = "sm_ParsedStreetName";
	public static final  String              TAG_SM_PARSED_SUFFIX                    = "sm_ParsedSuffix";
	public static final  String              TAG_SM_PARSED_POST_DIRECTIONAL          = "sm_ParsedPostDirectional";
	public static final  String              TAG_SM_PARSED_SUITE_NAME                = "sm_ParsedSuiteName";
	public static final  String              TAG_SM_PARSED_SUITE_RANGE               = "sm_ParsedSuiteRange";
	public static final  String              TAG_SM_PARSED_PMB_NAME                  = "sm_ParsedPMBName";
	public static final  String              TAG_SM_PARSED_PMB_RANGE                 = "sm_ParsedPMBRange";
	public static final  String              TAG_SM_PARSED_EXTRA_INFO                = "sm_ParsedExtraInformation";
	// mdLicense stuff
	public static final  String              MDLICENSE_PRODUCT_AddressObject         = "P1";
	public static final  String              MDLICENSE_PRODUCT_CanadianAddon         = "P2";
	public static final  String              MDLICENSE_PRODUCT_RBDIAddon             = "P3";
	public static final  String              MDLICENSE_PRODUCT_PhoneObject           = "P5";
	public static final  String              MDLICENSE_PRODUCT_NameObject            = "P6";
	public static final  String              MDLICENSE_PRODUCT_EmailObject           = "P7";
	public static final  String              MDLICENSE_PRODUCT_GeoCoder              = "P8";
	public static final  String              MDLICENSE_PRODUCT_GeoPoint              = "P9";
	public static final  String              MDLICENSE_PRODUCT_PresortObject         = "P10";
	public static final  String              MDLICENSE_PRODUCT_MatchUpObject         = "P11";
	public static final  String              MDLICENSE_PRODUCT_StyleListObject       = "P13";
	public static final  String              MDLICENSE_PRODUCT_IpLocatorObject       = "P14";
	public static final  String              MDLICENSE_PRODUCT_TelcoObject           = "P15";
	public static final  String              MDLICENSE_PRODUCT_RightFielderObject    = "P16";
	public static final  String              MDLICENSE_PRODUCT_SalesTaxObject        = "P17";
	public static final  String              MDLICENSE_PRODUCT_Mailers               = "P18";
	public static final  String              MDLICENSE_PRODUCT_SSISTier1             = "P19";
	public static final  String              MDLICENSE_PRODUCT_SSISTier2             = "P20";
	public static final  String              MDLICENSE_PRODUCT_GeoCoderCanadianAddon = "P21";
	public static final  String              MDLICENSE_PRODUCT_GlobalVerify          = "P22";
	public static final  String              MDLICENSE_PRODUCT_SmartMover            = "P23";
	public static final  String              MDLICENSE_PRODUCT_MatchUpLite           = "P25";
	public static final  String              MDLICENSE_PRODUCT_Personator            = "P26";
	public static final  String              MDLICENSE_PRODUCT_MatchUpIntl           = "P35";
	public static final  String              MDLICENSE_PRODUCT_Any                   = "P*";
	public static final  String              MDLICENSE_OPTS_NonExpiringDB            = "O1";
	public static final  String              MDLICENSE_OPTS_Enterprise               = "O2";
	public static final  String              MDLICENSE_OPTS_Any                      = "O*";
	public static final  int                 MDLICENSE_None                          = 0x0000;
	public static final  int                 MDLICENSE_Name                          = 0x0001;
	public static final  int                 MDLICENSE_Address                       = 0x0002;
	public static final  int                 MDLICENSE_Canada                        = 0x0004;
	public static final  int                 MDLICENSE_RBDI                          = 0x0008;
	public static final  int                 MDLICENSE_GeoCode                       = 0x0010;
	public static final  int                 MDLICENSE_GeoPoint                      = 0x0020;
	public static final  int                 MDLICENSE_Phone                         = 0x0040;
	public static final  int                 MDLICENSE_Email                         = 0x0080;
	public static final  int                 MDLICENSE_CanadaGeo                     = 0x0100;
	public static final  int                 MDLICENSE_MatchUp                       = 0x0200;
	public static final  int                 MDLICENSE_IPLocator                     = 0x0400;
	public static final  int                 MDLICENSE_SmartMover                    = 0x0800;
	public static final  int                 MDLICENSE_Personator                    = 0x10000;
	public static final  int                 MDLICENSE_MatchUpGlobal                 = 0x20000;
	public static final  int                 MDLICENSE_MatchUpLite                   = 0x08000;
	private static final String              TAG_ADVANCED_CONFIGURATION              = "advanced_configuration";
	private static final String              TAG_MDCHECK_VERSION                     = "md_contactverify_version";
	private static final String              TAG_CONTACT_ZONE_FILE                   = "contact_zone.prp";
	private static final String              TAG_PRIMARY_ID                          = "primary_customerID";
	private static final String              TAG_PRIMARY_TEST_RESULT                 = "primary_TestResult";
	private static final String              TAG_GEO_LEVEL                           = "geoLevel";
	private static final String              TAG_TRIAL_ID                            = "trial_customerID";
	private static final String              TAG_TRIAL_TEST_RESULT                   = "trial_TestResult";
	// Service type
	private static final String              TAG_SERVICE_TYPE                        = "service_type";
	// Local object tags
	private static final String              TAG_LOCAL_DATA_PATH                     = "data_path";
	private static final String              TAG_LOCAL_DATA_PATH_MATCHUP             = "data_path_mu";
	// Temporary file work directory
	private static final String              TAG_WORK_PATH                           = "work_path";
	private static final String              TAG_WORK_PATH_MATCHUP                   = "work_path_mu";
	// ContactVerify web/appliance tags
	private static final String              TAG_CONTACTVERIFY_MAX_THREADS           = "max_check_threads";
	private static final String              TAG_CONTACTVERIFY_MAX_REQUESTS          = "max_check_requests";
	private static final String              TAG_CONTACTVERIFY_WEB_TIMEOUT           = "check_timeout";
	private static final String              TAG_CONTACTVERIFY_WEB_RETRIES           = "check_retries";
	// IPLocator web/appliance tags
	private static final String              TAG_IPLOCATOR_MAX_THREADS               = "max_iplocator_threads";
	private static final String              TAG_IPLOCATOR_MAX_REQUESTS              = "max_iplocator_requests";
	private static final String              TAG_IPLOCATOR_WEB_TIMEOUT               = "iplocator_timeout";
	private static final String              TAG_IPLOCATOR_WEB_RETRIES               = "iplocator_retries";
	// ContactVerify web/appliance tags
	private static final String              TAG_SMARTMOVER_MAX_THREADS              = "max_smartmover_threads";
	private static final String              TAG_SMARTMOVER_MAX_REQUESTS             = "max_smartmover_requests";
	private static final String              TAG_SMARTMOVER_WEB_TIMEOUT              = "smartmover_timeout";
	private static final String              TAG_SMARTMOVER_WEB_RETRIES              = "smartmover_retries";
	// common setting
	private static final String              TAG_WEB_OPT_ABORT                       = "opt_abort";
	// Web service tags
	private static final String              TAG_WEB_PROXY_HOST                      = "proxy_host";
	private static final String              TAG_WEB_PROXY_PORT                      = "proxy_port";
	private static final String              TAG_WEB_PROXY_USER                      = "proxy_user";
	private static final String              TAG_WEB_PROXY_PASS                      = "proxy_pass";
	private static final String              TAG_WEB_NAME_PARSER_URL                 = "name_parser_url";
	private static final String              TAG_WEB_ADDRESS_VERIFIER_URL            = "address_verifier_url";
	private static final String              TAG_WEB_GEO_CODER_URL                   = "geo_coder_url";
	private static final String              TAG_WEB_RBDI_URL                        = "rbdi_url";
	private static final String              TAG_WEB_PHONE_VERIFIER_URL              = "phone_verifier_url";
	private static final String              TAG_WEB_EMAIL_VERIFIER_URL              = "email_verifier_url";
	private static final String              TAG_WEB_IP_LOCATOR_URL                  = "ip_locator_url";
	private static final String              TAG_WEB_SEND_LICENCE_URL                = "sendLicence_url";
	private static final String              TAG_WEB_NCOA_URL                        = "ncoa_url";
	private static final String              TAG_WEB_CCOA_URL                        = "ccoa_url";
	// Local appliance tags
	private static final String              TAG_CVS_SERVER_URL                      = "cvs_server_url";
	private static final String              TAG_CVS_OPT_FAILOVER                    = "cvs_opt_failover";
	private static final String              TAG_RETRY_APPLIANCE                     = "retry_appliance";
	private static final String              TAG_FAILOVER_INTERVAL                   = "failover_interval";
	private static final String              TAG_CVS_TIMEOUT                         = "cvs_timeout";
	private static final String              TAG_CVS_RETRIES                         = "cvs_retries";
	private static final String              TAG_CVS_OPT_ABORT                       = "cvs_opt_abort";
	// Reporting tags
	private static final String              TAG_BASIC_REPORTING                     = "basic_reporting";
	// Customer defined defaults
	private static final String              TAG_DEFAULTS_SET                        = "defaults_set";
	private static final String              TAG_SM_DEFAULTS_SET                     = "defaults_sm_set";
	private static final String              TAG_MU_DEFAULTS_SET                     = "defaults_mu_set";
	private static final String              TAG_IP_DEFAULTS_SET                     = "defaults_ip_set";
	// Name Tab
	private static final String              TAG_NAME_PREFIX1                        = "default_name_prefix1";
	private static final String              TAG_NAME_FIRSTNAME1                     = "default_name_first1";
	private static final String              TAG_NAME_MIDDLENAME1                    = "default_name_middle1";
	private static final String              TAG_NAME_LASTNAME1                      = "default_name_last1";
	private static final String              TAG_NAME_SUFFIX1                        = "default_name_suffix1";
	private static final String              TAG_NAME_GENDER1                        = "default_name_gender1";
	private static final String              TAG_NAME_PREFIX2                        = "default_name_prefix2";
	private static final String              TAG_NAME_FIRSTNAME2                     = "default_name_first2";
	private static final String              TAG_NAME_MIDDLENAME2                    = "default_name_last2";
	private static final String              TAG_NAME_LASTNAME2                      = "default_name_middle2";
	private static final String              TAG_NAME_SUFFIX2                        = "default_name_suffix2";
	private static final String              TAG_NAME_GENDER2                        = "default_name_gender2";
	private static final String              TAG_NAME_SALUTATION                     = "default_name_salutation";
	// Address Verify Tab
	private static final String              TAG_ADDRESS_ADDRESS                     = "default_address_address";
	private static final String              TAG_ADDRESS_ADDRESS2                    = "default_address_address2";
	private static final String              TAG_ADDRESS_CITY                        = "default_address_city";
	private static final String              TAG_ADDRESS_STATE                       = "default_address_state";
	private static final String              TAG_ADDRESS_ZIP                         = "default_address_zip";
	private static final String              TAG_ADDRESS_COUNTRY                     = "default_address_country";
	private static final String              TAG_ADDRESS_ADDRESS_KEY                 = "default_address_address_key";
	private static final String              TAG_ADDRESS_MAK                         = "default_address_mak";
	private static final String              TAG_ADDRESS_BASE_MAK                    = "default_address_base_mak";
	// MatchUp
	private static final String              TAG_MU_DUPE_GROUP                       = "mu_default_dupe_group";
	private static final String              TAG_MU_DUPE_COUNT                       = "mu_default_dupe_count";
	private static final String              TAG_MU_MATCHCODE_KEY                    = "mu_default_matchcode_key";
	private static final String              TAG_MU_RESULT_CODES                     = "mu_default_result_codes";
	// IP Locator
	private static final String              TAG_IP_ADDRESS                          = "md_ip_address";
	private static final String              TAG_IP_LONGITUDE                        = "md_ip_longitude";
	private static final String              TAG_IP_LATITUDE                         = "md_ip_latitude";
	private static final String              TAG_IP_ZIP                              = "md_ip_zip";
	private static final String              TAG_IP_REGION                           = "md_ip_region";
	private static final String              TAG_IP_NAME                             = "md_ip_name";
	private static final String              TAG_IP_DOMAIN                           = "md_ip_domain";
	private static final String              TAG_IP_CITYNAME                         = "md_ip_cityname";
	private static final String              TAG_IP_COUNTRY                          = "md_ip_country";
	private static final String              TAG_IP_ABREVIATION                      = "md_ip_abreviation";
	private static final String              TAG_IP_CONNECTION_SPEED                 = "md_ip_connection_speed";
	private static final String              TAG_IP_CONNECTION_TYPE                  = "md_ip_connection_type";
	private static final String              TAG_IP_UTC                              = "md_ip_utc";
	private static final String              TAG_IP_CONTINENT                        = "md_ip_continent";
	private static       Class<?>            PKG                                     = MDCheckMeta.class;
	private static       boolean             useMapping                              = false;
	private static       PluginInstaller     pluginInstaller                         = null;
	private static       String              templatePath                            = "";
	public               boolean             isCluster                               = false;
	private              MDCheckStepData     data                                    = null;
	private              ServiceType         serviceType                             = null;
	// Common web/appliance configurations
	private              int                 maxThreads                              = 0;
	private              int                 maxRequests                             = 0;
	// Web service configiurations
	private              String              webProxyHost                            = "";
	private              String              webProxyPort                            = "";
	private              String              webProxyUser                            = "";
	private              String              webProxyPass                            = "";
	private              String              webTimeout                              = "";
	private              String              webRetries                              = "";
	private              boolean             webAbortOnError                         = false;
	// Local appliance configurations
	private              String              cvsTimeout                              = "";
	private              String              cvsRetries                              = "";
	private              boolean             cvsAbortOnError                         = false;
	private              boolean             cvsFailover                             = false;
	private              boolean             updateData                              = false;
	private              boolean             licensedProductsSet                     = false;
	private              int                 licensedProducts                        = 0;
	private              String              installError                            = "";
	private              Exception           installException                        = null;
	private              boolean             isContactZone                           = false;
	private              boolean             isGlobalLoaded                          = false;
	private              LogChannelInterface log                                     = null;

	public AdvancedConfigurationMeta(MDCheckStepData data) throws KettleException {

		this.data = data;

		if (MDCheckMeta.isTest) {
			System.out.println("TEST - Advanced Config Meta: Check Type = " + data.checkTypes);
		} else {
			this.log = new LogChannel(this);
		}
		setDefault();
	}

	public static void addDir(String s) throws IOException {

		try {
			// This enables the java.library.path to be modified at runtime
			//
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (String path : paths) {
				if (s.equals(path)) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = s;
			field.set(null, tmp);
			System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
		} catch (IllegalAccessException e) {
			throw new IOException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.Error.LibPathPermissions"));
		} catch (NoSuchFieldException e) {
			throw new IOException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.Error.LibFieldHandle"));
		}
	}

	public static String getGeoLevel() {

		return MDProps.getProperty(TAG_GEO_LEVEL, "0");
	}

	/**
	 * Called to determine what products the user is licensed for
	 *
	 * @param
	 * @return
	 */
	public static int getProducts(String licenseType) {

		int retVal = MDLICENSE_None;
		if (licenseType.equals(TAG_PRIMARY_LICENSE)) {
			retVal = Integer.parseInt(MDProps.getProperty(TAG_PRIMARY_RET_VAL, String.valueOf(MDLICENSE_None)));
		} else if (licenseType.equals(TAG_TRIAL_LICENSE)) {
			retVal = Integer.parseInt(MDProps.getProperty(TAG_TRIAL_RET_VAL, String.valueOf(MDLICENSE_None)));
		}
		return retVal;
	}

	public static String getTemplatePath() {

		return templatePath;
	}

	public static void setTemplatePath(String path) {

		templatePath = path;
	}

	public static boolean isUseMapping() {

		return useMapping;
	}

	public static void setUseMapping(boolean useMapping) {

		AdvancedConfigurationMeta.useMapping = useMapping;
	}

	public static boolean isEnterprise(String pCode) {

		return MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(pCode) || MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_Any);
		//return Boolean.valueOf(MDProps.getProperty(TAG_PRIMARY_LICENSE_ENTERPRISE, "false"));
	}

	public static boolean isCommunity() {

		return (getProducts(TAG_PRIMARY_LICENSE) & MDPropTags.MDLICENSE_Community) != 0;
	}

	/**
	 * Checks the settings of this step and puts the findings in a remarks List.
	 *
	 * @param remarks  The list to put the remarks in @see org.pentaho.di.core.CheckResult
	 * @param stepMeta The stepMeta to help checking
	 * @param prev     The fields coming from the previous step
	 * @param input    The input step names
	 * @param output   The output step names
	 * @param info     The fields that are used as information by the step
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// TODO: Do something here?
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AdvancedConfigurationMeta clone() throws CloneNotSupportedException {

		return (AdvancedConfigurationMeta) super.clone();
	}

	public boolean dataExists() {

		File dPath    = new File(MDProps.getProperty(TAG_LOCAL_DATA_PATH, ""));
		File dataFile = new File(dPath, "mdName.dat");
		return dataFile.exists();
	}

	public boolean getBasicReporting() {

		return Boolean.valueOf(MDProps.getProperty(TAG_BASIC_REPORTING, "false"));
	}

	/**
	 * @return Customer id derived from license string
	 */
	public String getCustomerID(int product) {

		String CustomerID = "";
		if ((product & getProducts(TAG_PRIMARY_LICENSE)) != 0) {
			CustomerID = MDProps.getProperty(TAG_PRIMARY_ID, "");
		} else if ((product & getProducts(TAG_TRIAL_LICENSE)) != 0) {
			CustomerID = MDProps.getProperty(TAG_TRIAL_ID, "");
		}
		return CustomerID;
	}

	public String getTransmissionReference() {

		String product = isContactZone() ? "CZ" : "PENTAHO";
		String version = getVersionFromManifest();

		String transRef = "mdSrc:{product:" + product + ";version:" + version + "}";
		return transRef;
	}

	public String getCVSRetries() {

		return cvsRetries;
	}

	public void setCVSRetries(String s) {

		cvsRetries = s;
	}

	// Local appliance
	public String getCVSServerURL() {

		return MDProps.getProperty(TAG_CVS_SERVER_URL, "");
	}

	public void setCVSServerURL(String s) {

		MDProps.setProperty(TAG_CVS_SERVER_URL, s);
	}

	public String getCVSTimeout() {

		return cvsTimeout;
	}

	public void setCVSTimeout(String s) {

		cvsTimeout = s;
	}

	public int getFailoverInterval() {

		return Integer.valueOf(MDProps.getProperty(TAG_FAILOVER_INTERVAL, "600000"));
	}

	public String getIPLocatorURL() {

		return MDProps.getProperty(TAG_WEB_IP_LOCATOR_URL, "webIPLocatorURL");
	}

	public String getLicenceExpiration() {

		String primaryExp = MDProps.getProperty(TAG_PRIMARY_EXPIRATION, "");
		String trialExp   = MDProps.getProperty(TAG_TRIAL_EXPIRATION, "");
		if (!Const.isEmpty(primaryExp)) {
			return primaryExp;
		} else if (!Const.isEmpty(trialExp)) {
			return primaryExp;
		}
		return "";
	}

	/**
	 * @return The license string from props
	 */
	public String getLicense(String product) {

		return MDProps.getProperty(product, "").trim();
	}

	// Local object
	//FIXME MatchUp Global
	public String getLocalDataPath() {

		String ldp = "";
		if (doMatchUp() || doMatchUpGlobal()) {
			ldp = MDProps.getProperty(TAG_LOCAL_DATA_PATH_MATCHUP, System.getProperty("user.home") + Const.FILE_SEPARATOR + ".kettle" + Const.FILE_SEPARATOR + "matchup");
			if (doMatchUpGlobal()) {
				ldp = ldp + ".global";
			}
		} else {
			ldp = MDProps.getProperty(TAG_LOCAL_DATA_PATH, "");
		}

		return ldp;
	}

	public void setLocalDataPath(String s) {

		if (doMatchUp() || doMatchUpGlobal()) {
			MDProps.setProperty(TAG_LOCAL_DATA_PATH_MATCHUP, s);
		} else {
			MDProps.setProperty(TAG_LOCAL_DATA_PATH, s);
		}
	}

	public int getMaxRequests() {

		return maxRequests;
	}

	public void setMaxRequests(int i) {

		maxRequests = i;
	}

	// Common web/appliance
	public int getMaxThreads() {

		return maxThreads;
	}

	public void setMaxThreads(int i) {

		maxThreads = i;
	}

	public PluginInstaller getPluginInstaller() {

		return pluginInstaller;
	}

	public void setPluginInstaller(PluginInstaller pluginInstaller) {

		AdvancedConfigurationMeta.pluginInstaller = pluginInstaller;
	}

	/**
	 * @return Bit flags for the licensed products
	 */
	public int getProducts() {

		if (!licensedProductsSet) {
			licensedProducts = getProducts(TAG_PRIMARY_LICENSE);
			licensedProducts |= getProducts(TAG_TRIAL_LICENSE);
			licensedProductsSet = true;
		}
		return licensedProducts;
	}

	/**
	 * @return Bit flags for the licensed products
	 */
	public int getProducts(boolean newSet) {

		if (newSet) {
			licensedProducts = getProducts(TAG_PRIMARY_LICENSE);
			licensedProducts |= getProducts(TAG_TRIAL_LICENSE);
			licensedProductsSet = true;
		}
		return licensedProducts;
	}

	/**
	 * @return The license string for individual products
	 */
	public String getProdutLicense(int product) {

		String license = "";
		if ((product & getProducts(TAG_PRIMARY_LICENSE)) != 0) {
			license = getLicense(TAG_PRIMARY_LICENSE);
		} else if (isCommunity() && (product != MDPropTags.MDLICENSE_IPLocator) && (product != MDPropTags.MDLICENSE_GeoCode)) {
			license = MDPropTags.MD_COMMUNITY_LICENSE;
		}
		return license.trim();
	}

	public void getPropVals() {
		// Common web/appliance defaults
		// Web service defaults
		webProxyHost = MDProps.getProperty(TAG_WEB_PROXY_HOST, "");
		webProxyPort = MDProps.getProperty(TAG_WEB_PROXY_PORT, "");
		webProxyUser = MDProps.getProperty(TAG_WEB_PROXY_USER, "");
		webProxyPass = MDProps.getProperty(TAG_WEB_PROXY_PASS, "");
		getWebSettingsByProduct();
		webAbortOnError = Boolean.parseBoolean(MDProps.getProperty(TAG_WEB_OPT_ABORT, "false"));
		// Local appliance defaults
		cvsFailover = Boolean.parseBoolean(MDProps.getProperty(TAG_CVS_OPT_FAILOVER, "false"));
		cvsTimeout = MDProps.getProperty(TAG_CVS_TIMEOUT, "45");
		cvsRetries = MDProps.getProperty(TAG_CVS_RETRIES, "5");
		cvsAbortOnError = Boolean.parseBoolean(MDProps.getProperty(TAG_CVS_OPT_ABORT, "false"));
	}

	private void getWebSettingsByProduct() {

		if ((data.getCheckTypes() & MDCheckMeta.MDCHECK_FULL) != 0) {
			maxThreads = Integer.parseInt(MDProps.getProperty(TAG_CONTACTVERIFY_MAX_THREADS, "5"));
			maxRequests = Integer.parseInt(MDProps.getProperty(TAG_CONTACTVERIFY_MAX_REQUESTS, "100"));
			webTimeout = MDProps.getProperty(TAG_CONTACTVERIFY_WEB_TIMEOUT, "45");
			webRetries = MDProps.getProperty(TAG_CONTACTVERIFY_WEB_RETRIES, "5");
		} else if ((data.getCheckTypes() & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			maxThreads = Integer.parseInt(MDProps.getProperty(TAG_IPLOCATOR_MAX_THREADS, "5"));
			maxRequests = Integer.parseInt(MDProps.getProperty(TAG_IPLOCATOR_MAX_REQUESTS, "100"));
			webTimeout = MDProps.getProperty(TAG_IPLOCATOR_WEB_TIMEOUT, "45");
			webRetries = MDProps.getProperty(TAG_IPLOCATOR_WEB_RETRIES, "5");
		} else if ((data.getCheckTypes() & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			maxThreads = Integer.parseInt(MDProps.getProperty(TAG_SMARTMOVER_MAX_THREADS, "5"));
			maxRequests = Integer.parseInt(MDProps.getProperty(TAG_SMARTMOVER_MAX_REQUESTS, "100"));
			webTimeout = MDProps.getProperty(TAG_SMARTMOVER_WEB_TIMEOUT, "45");
			webRetries = MDProps.getProperty(TAG_SMARTMOVER_WEB_RETRIES, "5");
		} else {
			// Should not happen but if it does just revert to Full
			maxThreads = Integer.parseInt(MDProps.getProperty(TAG_CONTACTVERIFY_MAX_THREADS, "5"));
			maxRequests = Integer.parseInt(MDProps.getProperty(TAG_CONTACTVERIFY_MAX_REQUESTS, "100"));
			webTimeout = MDProps.getProperty(TAG_CONTACTVERIFY_WEB_TIMEOUT, "45");
			webRetries = MDProps.getProperty(TAG_CONTACTVERIFY_WEB_RETRIES, "5");
		}
	}

	public String getRBDIndicatorURL() {

		return MDProps.getProperty(TAG_WEB_RBDI_URL, "RBDIndicatorURL");
	}

	public String getSendLicenceURL() {

		return MDProps.getProperty(TAG_WEB_SEND_LICENCE_URL, "WebUpdateURL");
	}

	/* Configuration access methods */
	public ServiceType getServiceType() {

		return serviceType;
	}

	public void setServiceType(ServiceType e) {

		serviceType = e;
	}

	public String getWebAddressVerifierURL() {

		return MDProps.getProperty(TAG_WEB_ADDRESS_VERIFIER_URL, "AddressVerifierURL");
	}

	public String getWebCCOAURL() {

		return MDProps.getProperty(TAG_WEB_CCOA_URL, "CCOAURL");
	}

	public String getWebEmailVerifierURL() {

		return MDProps.getProperty(TAG_WEB_EMAIL_VERIFIER_URL, "EmailVerifierURL");
	}

	public String getWebGeoCoderURL() {

		return MDProps.getProperty(TAG_WEB_GEO_CODER_URL, "GeoCoderURL");
	}

	public String getWebNameParserURL() {

		return MDProps.getProperty(TAG_WEB_NAME_PARSER_URL, "NameParserURL");
	}

	public String getWebNCOAURL() {

		return MDProps.getProperty(TAG_WEB_NCOA_URL, "NCOURL");
	}

	public String getWebPhoneVerifierURL() {

		return MDProps.getProperty(TAG_WEB_PHONE_VERIFIER_URL, "PhoneVerifierURL");
	}

	public String getWebProxyHost() {

		return webProxyHost;
	}

	public void setWebProxyHost(String s) {

		webProxyHost = s;
	}

	public String getWebProxyPass() {

		return webProxyPass;
	}

	public void setWebProxyPass(String webProxyPass) {

		this.webProxyPass = webProxyPass;
	}

	public String getWebProxyPort() {

		return webProxyPort;
	}

	public void setWebProxyPort(String s) {

		webProxyPort = s;
	}

	public String getWebProxyUser() {

		return webProxyUser;
	}

	public void setWebProxyUser(String webProxyUser) {

		this.webProxyUser = webProxyUser;
	}

	public String getWebRetries() {

		return webRetries;
	}

	public void setWebRetries(String s) {

		webRetries = s;
	}

	// Web service
	public String getWebTimeout() {

		return webTimeout;
	}

	public void setWebTimeout(String s) {

		webTimeout = s;
	}

	// Work directory
	//FIXME matchUp Global
	public String getWorkPath() {

		return (doMatchUp() || doMatchUpGlobal()) ? MDProps.getProperty(TAG_WORK_PATH_MATCHUP, "") : MDProps.getProperty(TAG_WORK_PATH, "");
	}

	public void setWorkPath(String s) {

		if (doMatchUp() || doMatchUpGlobal()) {
			MDProps.setProperty(TAG_WORK_PATH_MATCHUP, s);
		} else {
			MDProps.setProperty(TAG_WORK_PATH, s);
		}
	}

	/**
	 * Returns the XML representation of the meta data
	 *
	 * @param tab
	 * @return
	 * @throws KettleException
	 */
	public String getXML(String tab) throws KettleException {
		// save global properties
		saveGlobal();
		// Save step properties
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_ADVANCED_CONFIGURATION)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SERVICE_TYPE, serviceType.toString()));
		// Local object (nothing for now)
/*
 * // Common web/appliance
 * retval.append(tab).append("  ").append(XMLHandler.addTagValue(TAG_MAX_THREADS, maxThreads));
 * retval.append(tab).append("  ").append(XMLHandler.addTagValue(TAG_MAX_REQUESTS, maxRequests));
 * // Web service
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_WEB_TIMEOUT, webTimeout));
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_WEB_RETRIES, webRetries));
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_WEB_OPT_ABORT, Boolean.toString(webAbortOnError)));
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_WEB_PROXY_HOST, webProxyHost));
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_WEB_PROXY_PORT, webProxyPort));
 * // Local appliance
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CVS_TIMEOUT, cvsTimeout));
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CVS_RETRIES, cvsRetries));
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CVS_OPT_ABORT, Boolean.toString(cvsAbortOnError)));
 * retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CVS_OPT_FAILOVER, Boolean.toString(cvsFailover)));
 */
		retval.append(tab).append(XMLHandler.closeTag(TAG_ADVANCED_CONFIGURATION)).append(Const.CR);
		return retval.toString();
	}

	public boolean isCVSAbortOnError() {

		return cvsAbortOnError;
	}

	public void setCVSAbortOnError(boolean b) {

		cvsAbortOnError = b;
	}

	public boolean isCVSFailover() {

		return cvsFailover;
	}

	public void setCVSFailover(boolean b) {

		cvsFailover = b;
	}

	public boolean isDemo() {

		return true;
	}

	public boolean isRetryAppliance() {

		return Boolean.valueOf(MDProps.getProperty(TAG_RETRY_APPLIANCE, "true"));
	}

	public boolean isWebAbortOnError() {

		return webAbortOnError;
	}

	public void setWebAbortOnError(boolean b) {

		webAbortOnError = b;
	}

	/**
	 * Called to load the global properties
	 *
	 * @throws KettleException
	 */
	public void loadGlobal() throws KettleException {

		if (isGlobalLoaded) {
			if (log != null) {
				log.logBasic("MDCheck - initialized");
			}
			return;
		}

		String fileSep = System.getProperty("file.separator");
		File   mdDir   = new File(Const.getKettleDirectory() + fileSep + "MD");
		if (log != null) {
			log.logBasic("MD objects location : " + mdDir.getAbsolutePath());
		}
		checkContactZone();

		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				MDCheckMeta.setSpoon(true);
			} else {
				MDCheckMeta.setSpoon(false);
			}
		}

		if (MDCheckMeta.isSpoon() && !isContactZone) {
			log.logBasic("Checking Pentaho Plugin Install ");
			moveTmp();
			if (!mdDir.exists()) {
				pluginInstaller = new PluginInstaller(isContactZone, log);
				if (MDCheckMeta.isSpoon()) {
					try {
						pluginInstaller.doInstall(false, false, getVersionFromManifest());
					} catch (Exception e) {
						//System.out.println(" Failed To Install MDCheck" + e.toString());
						installError += " Failed To Install MDCheck" + e.getMessage();
						installException = e;
						log.logError(" Failed To Install MDCheck" + e.getMessage());
					}
					setUpdateData(true);
					MDProps.load();
				}
			} else {
				setUpdateData(checkUpdate());
				if (MDCheckMeta.isSpoon() && isUpdateData()) {
					pluginInstaller = new PluginInstaller(isContactZone, log);
					try {
						pluginInstaller.doInstall(true, isUpdateData(), getVersionFromManifest());
					} catch (Exception e) {
						//System.out.println(" Failed To Install MDCheck" + e.toString());
						installError += " Failed To Install MDCheck" + e.getMessage();
						installException = e;
						log.logError(" Failed To Install MDCheck" + e.getMessage());
					}
				} else if (!MDCheckMeta.isSpoon() && isUpdateData()) {
					// TODO instal for non gui
				}
				//Reporting
				if (templatePath == null) {
					String decodedPath = "";
					File   ssdd        = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
					try {
						decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
						decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
						AdvancedConfigurationMeta.setTemplatePath(decodedPath + fileSep + "MDReporting");
					} catch (UnsupportedEncodingException e) {
						log.logError("Error getting source dir: " + e.getMessage());
					}
				}
			}
		} else if (MDCheckMeta.isSpoon() && isContactZone) {
			pluginInstaller = new PluginInstaller(isContactZone, log);
			pluginInstaller.checkCZInstall(getInstalledPluginVersion());
		}

		checkHadoopCluster();
		if (isCluster) {
			// sort of a hack normal method to check enterprise
			// does not work on the cluster so if it is on a hadoop cluster
			// we assume it is enterprise.
			//	enterprise = true;
		}

		if (MDCheckMeta.isTest) {
			loadClassFiles();
		} else {
			if (!isContactZone) {
				loadClassFiles();
			}

			DQTObjectFactory.setLogLevel(log.getLogLevel());

			MDProps.load();

			log.logBasic("Running Spoon = " + MDCheckMeta.isSpoon());
		}

		String pCode = "";
		if ((data.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			pCode = MDPropTags.MDLICENSE_PRODUCT_SmartMover;
		} else if ((data.checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
			pCode = MDPropTags.MDLICENSE_PRODUCT_MatchUpIntl;
		} else if ((data.checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			pCode = MDPropTags.MDLICENSE_PRODUCT_MatchUpObject;
		} else if ((data.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			pCode = MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject;
		} else if ((data.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			pCode = MDPropTags.MDLICENSE_PRODUCT_AddressObject;
		}

		if (MDCheckMeta.isTest) {
			System.out.println("TEST - Load Global Complete");
		} else {
			if (isEnterprise(pCode)) {
				log.logBasic("Mode = Enterprise Edition");
			} else if (isCommunity()) {
				log.logBasic("Mode = Community Edition");
			} else {
				log.logBasic("Mode = Not Licensed");
			}
		}
		isGlobalLoaded = true;
	}

	public boolean checkHadoopCluster() {

		if (isCluster) {
			return isCluster;
		}

		if (MDCheckMeta.isSpoon()) {
			return isCluster = false;
		}

		File clusterProp = new File("mdProps.prop");
		if (clusterProp.exists()) {
			isCluster = true;
		}

		if (isCluster) {
			log.logBasic(" - Running hadoop clustered ");
		} else {

		}

		return isCluster;
	}

	/**
	 * Called to read meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 */
	public void readData(Node node) throws KettleException {
		// Load global properties
		// loadGlobal();
		// Load step properties
		List<Node> nodes = XMLHandler.getNodes(node, TAG_ADVANCED_CONFIGURATION);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			String s = MDCheckStepData.getTagValue(node, TAG_SERVICE_TYPE);
			s = MDCheckStepData.safe(s, serviceType.toString());
			serviceType = ServiceType.valueOf(s);
			// Local Object data (none right now)
/*
 * // Common web/appliance data
 * String value = MDCheckStepData.getTagValue(node, TAG_MAX_THREADS);
 * maxThreads = (value != null) ? Integer.valueOf(value) : maxThreads;
 * value = MDCheckStepData.getTagValue(node, TAG_MAX_REQUESTS);
 * maxRequests = (value != null) ? Integer.valueOf(value) : maxRequests;
 * // Web service data
 * webProxyHost = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_WEB_PROXY_HOST), webProxyHost);
 * webProxyPort = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_WEB_PROXY_PORT), webProxyPort);
 * webTimeout = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_WEB_TIMEOUT), webTimeout);
 * webRetries = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_WEB_RETRIES), webRetries);
 * value = MDCheckStepData.getTagValue(node, TAG_WEB_OPT_ABORT);
 * webAbortOnError = (value != null) ? Boolean.valueOf(value) : webAbortOnError;
 * // Local appliance data
 * value = MDCheckStepData.getTagValue(node, TAG_CVS_OPT_FAILOVER);
 * cvsFailover = (value != null) ? Boolean.valueOf(value) : cvsFailover;
 * cvsTimeout = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CVS_TIMEOUT), cvsTimeout);
 * cvsRetries = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CVS_RETRIES), cvsRetries);
 * value = MDCheckStepData.getTagValue(node, TAG_CVS_OPT_ABORT);
 * cvsAbortOnError = (value != null) ? Boolean.valueOf(value) : cvsAbortOnError;
 */
		} else {
			setDefault();
		}
	}

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep) throws KettleException {
		// Load global properties
		loadGlobal();
		// Load step properties
		String s = rep.getStepAttributeString(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_SERVICE_TYPE);
		s = MDCheckStepData.safe(s, serviceType.toString());
		serviceType = ServiceType.valueOf(s);
		// Local object (nothing for now)
/*
 * // Common web/appliance
 * maxThreads = (int) rep.getStepAttributeInteger(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_MAX_THREADS);
 * maxRequests = (int) rep.getStepAttributeInteger(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_MAX_REQUESTS);
 * // Web service
 * webTimeout = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_TIMEOUT),
 * webTimeout);
 * webRetries = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_RETRIES),
 * webRetries);
 * webAbortOnError = rep.getStepAttributeBoolean(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_OPT_ABORT);
 * webProxyHost = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADVANCED_CONFIGURATION + "." +
 * TAG_WEB_PROXY_HOST), webProxyHost);
 * webProxyPort = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADVANCED_CONFIGURATION + "." +
 * TAG_WEB_PROXY_PORT), webProxyPort);
 * // Local appliance
 * cvsTimeout = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_TIMEOUT),
 * cvsTimeout);
 * cvsRetries = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_RETRIES),
 * cvsRetries);
 * cvsAbortOnError = rep.getStepAttributeBoolean(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_OPT_ABORT);
 * cvsFailover = rep.getStepAttributeBoolean(idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_OPT_FAILOVER);
 */
	}

	public void saveDefaults(Properties props) {
		// customer is setting new or changing defaults
		if ((data.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			props.setProperty(TAG_DEFAULTS_SET, String.valueOf(true));
			// nameParse
			props.setProperty(TAG_NAME_PREFIX1, data.getNameParse().getPrefix(0));
			props.setProperty(TAG_NAME_FIRSTNAME1, data.getNameParse().getFirstName(0));
			props.setProperty(TAG_NAME_MIDDLENAME1, data.getNameParse().getMiddleName(0));
			props.setProperty(TAG_NAME_LASTNAME1, data.getNameParse().getLastName(0));
			props.setProperty(TAG_NAME_SUFFIX1, data.getNameParse().getSuffix(0));
			props.setProperty(TAG_NAME_GENDER1, data.getNameParse().getGender(0));
			props.setProperty(TAG_NAME_PREFIX2, data.getNameParse().getPrefix(1));
			props.setProperty(TAG_NAME_FIRSTNAME2, data.getNameParse().getFirstName(1));
			props.setProperty(TAG_NAME_MIDDLENAME2, data.getNameParse().getMiddleName(1));
			props.setProperty(TAG_NAME_LASTNAME2, data.getNameParse().getLastName(1));
			props.setProperty(TAG_NAME_SUFFIX2, data.getNameParse().getSuffix(1));
			props.setProperty(TAG_NAME_GENDER2, data.getNameParse().getGender(1));
			props.setProperty(TAG_NAME_SALUTATION, data.getNameParse().getSalutation());
			// AddressVerify
			props.setProperty(TAG_ADDRESS_ADDRESS, data.getAddressVerify().getOutputAddressLine1());
			props.setProperty(TAG_ADDRESS_ADDRESS2, data.getAddressVerify().getOutputAddressLine2());
			props.setProperty(TAG_ADDRESS_CITY, data.getAddressVerify().getOutputCity());
			props.setProperty(TAG_ADDRESS_STATE, data.getAddressVerify().getOutputState());
			props.setProperty(TAG_ADDRESS_ZIP, data.getAddressVerify().getOutputZip());
			props.setProperty(TAG_ADDRESS_COUNTRY, data.getAddressVerify().getOutputCountry());
			props.setProperty(TAG_ADDRESS_ADDRESS_KEY, data.getAddressVerify().getOutputAddressKey());

			props.setProperty(TAG_ADDRESS_MAK, data.getAddressVerify().getOutputMAK());
			props.setProperty(TAG_ADDRESS_BASE_MAK, data.getAddressVerify().getOutputBaseMAK());
			// Additional Address output
			props.setProperty(TAG_SUITE, data.getAddressVerify().getOutputSuite());
			props.setProperty(TAG_PRIVATE_MAILBOX, data.getAddressVerify().getOutputPrivateMailBox());
			props.setProperty(TAG_URBANIZATION, data.getAddressVerify().getOutputUrbanization());
			props.setProperty(TAG_PLUS4, data.getAddressVerify().getOutputPlus4());
			props.setProperty(TAG_DP_CHECK_DIGIT, data.getAddressVerify().getOutputDPAndCheckDigit());
			props.setProperty(TAG_CARRIER_ROUTE, data.getAddressVerify().getOutputCarrierRoute());
			props.setProperty(TAG_ZIP_TYPE_CODE, data.getAddressVerify().getOutputZipTypeCode());
			props.setProperty(TAG_ADDRESS_TYPE_CODE, data.getAddressVerify().getOutputAddressTypeCode());
			props.setProperty(TAG_ADDRESS_TYPE_DESCRIPTION, data.getAddressVerify().getOutputAddressTypeDescription());
			props.setProperty(TAG_CMRA, data.getAddressVerify().getOutputCMRA());
			props.setProperty(TAG_ELOT_NUMBER, data.getAddressVerify().getOutputElotNumber());
			props.setProperty(TAG_ELOT_ORDER, data.getAddressVerify().getOutputElotOrder());
			props.setProperty(TAG_DELIVERY_INDICATION, data.getAddressVerify().getOutputDeliveryIndication());
			// Address Geographic info
			props.setProperty(TAG_CITY_ABBREVATION, data.getAddressVerify().getOutputCityAbbreviation());
			props.setProperty(TAG_COUNTY_NAME, data.getAddressVerify().getOutputCountyName());
			props.setProperty(TAG_COUNTY_FIPS, data.getAddressVerify().getOutputCountyFips());
			props.setProperty(TAG_CONGRESSIONAL_DISTRICT, data.getAddressVerify().getOutputCongressionalDistrict());
			props.setProperty(TAG_TIME_ZONE, data.getAddressVerify().getOutputTimezone());
			props.setProperty(TAG_TIME_ZONE_CODE, data.getAddressVerify().getOutputTimezoneCode());
			// parsed address info
			props.setProperty(TAG_PARSED_ADDRESS_RANGE, data.getAddressVerify().getOutputParsedAddressRange());
			props.setProperty(TAG_PARSED_PRE_DIRECTIONAL, data.getAddressVerify().getOutputParsedPreDirectional());
			props.setProperty(TAG_PARSED_STREET_NAME, data.getAddressVerify().getOutputParsedStreetName());
			props.setProperty(TAG_PARSED_SUFFIX, data.getAddressVerify().getOutputParsedSuffix());
			props.setProperty(TAG_PARSED_POST_DIRECTIONAL, data.getAddressVerify().getOutputParsedPostDirectional());
			props.setProperty(TAG_PARSED_SUITE_NAME, data.getAddressVerify().getOutputParsedSuiteName());
			props.setProperty(TAG_PARSED_SUITE_RANGE, data.getAddressVerify().getOutputParsedSuiteRange());
			props.setProperty(TAG_PARSED_PMB_NAME, data.getAddressVerify().getOutputParsedPMBName());
			props.setProperty(TAG_PARSED_PMB_RANGE, data.getAddressVerify().getOutputParsedPMBRange());
			props.setProperty(TAG_PARSED_ROUTE_SERVICE, data.getAddressVerify().getOutputParsedRouteService());
			props.setProperty(TAG_PARSED_LOCK_BOX, data.getAddressVerify().getOutputParsedLockBox());
			props.setProperty(TAG_PARSED_DELIVERY_INSTALLATION, data.getAddressVerify().getOutputParsedDeliveryInstallation());
			props.setProperty(TAG_PARSED_EXTRA_INFORMATION, data.getAddressVerify().getOutputParsedExtraInformation());
			/*
			 * EXTRA not curently used props.setProperty(TAG_COMPANY,
			 * MDCheckData.outputCompany); props.setProperty(TAG_STATE_NAME,
			 * MDCheckData.outputStateName);
			 * props.setProperty(TAG_ZIP_TYPE_DESCRIPTION,
			 * MDCheckData.outputZipTypeDescription);
			 * props.setProperty(TAG_COUNTRY_NAME,
			 * MDCheckData.outputCountryName);
			 */
			// GeoCoder
			props.setProperty(TAG_GEO_LATITUDE, data.getGeoCoder().getLatitude());
			props.setProperty(TAG_GEO_LONGITUDE, data.getGeoCoder().getLongitude());
			// Geo geographic info
			props.setProperty(TAG_GEO_COUNTY, data.getGeoCoder().getCountyName());
			props.setProperty(TAG_GEO_COUNTYFIPS, data.getGeoCoder().getCountyFIPS());
			props.setProperty(TAG_GEO_PLACE_CODE, data.getGeoCoder().getPlaceCode());
			props.setProperty(TAG_GEO_PLACE_NAME, data.getGeoCoder().getPlaceName());
			props.setProperty(TAG_GEO_TIME_ZONE, data.getGeoCoder().getTimeZone());
			props.setProperty(TAG_GEO_TIME_ZONE_CODE, data.getGeoCoder().getTimeZoneCode());
			// Geo Census info
			props.setProperty(TAG_GEO_CBSA_CODE, data.getGeoCoder().getCBSACode());
			props.setProperty(TAG_GEO_CBSA_LEVEL, data.getGeoCoder().getCBSALevel());
			props.setProperty(TAG_GEO_CBSA_TITLE, data.getGeoCoder().getCBSATitle());
			props.setProperty(TAG_GEO_CBSA_DEVISION_CODE, data.getGeoCoder().getCBSADivisionCode());
			props.setProperty(TAG_GEO_CBSA_DIVISION_LEVEL, data.getGeoCoder().getCBSADivisionLevel());
			props.setProperty(TAG_GEO_CBSA_DEVISION_TITLE, data.getGeoCoder().getCBSADivisionTitle());
			props.setProperty(TAG_GEO_CENSUS_BLOCK, data.getGeoCoder().getCensusBlock());
			props.setProperty(TAG_GEO_CENSUS_TRACT, data.getGeoCoder().getCensusTract());
			// Phone/Email
			// phone output
			props.setProperty(TAG_PHONE, data.getPhoneVerify().getOutputPhone());
			props.setProperty(TAG_PHONE_FORMAT, data.getPhoneVerify().getOptionFormat().encode());
			props.setProperty(TAG_PHONE_AREA_CODE, data.getPhoneVerify().getOutputAreaCode());
			props.setProperty(TAG_PHONE_PREFIX, data.getPhoneVerify().getOutputPrefix());
			props.setProperty(TAG_PHONE_SUFFIX, data.getPhoneVerify().getOutputSuffix());
			props.setProperty(TAG_PHONE_EXTENSION, data.getPhoneVerify().getOutputExtension());
			// additional phone
			props.setProperty(TAG_PHONE_CITY, data.getPhoneVerify().getOutputCity());
			props.setProperty(TAG_PHONE_STATE_PROVINCE, data.getPhoneVerify().getOutputState());
			props.setProperty(TAG_PHONE_COUNTY_NAME, data.getPhoneVerify().getOutputCounty());
			props.setProperty(TAG_PHONE_COUNTY_FIPS, data.getPhoneVerify().getOutputCountyFips());
			props.setProperty(TAG_PHONE_COUNTRY_CODE, data.getPhoneVerify().getOutputCountry());
			props.setProperty(TAG_PHONE_TIME_ZONE, data.getPhoneVerify().getOutputTimeZone());
			props.setProperty(TAG_PHONE_TIME_ZONE_CODE, data.getPhoneVerify().getOutputTZCode());
			// email output
			props.setProperty(TAG_EMAIL_ADDRESS, data.getEmailVerify().getOutputEmail());
			// additional email
			props.setProperty(TAG_EMAIL_MAIL_BOX_NAME, data.getEmailVerify().getOutputMailboxName());
			props.setProperty(TAG_EMAIL_TOP_LEVEL_DOMAIN_DESCRIPTION, data.getEmailVerify().getOutputTLDDescription());
			props.setProperty(TAG_EMAIL_DOMAIN_NAME, data.getEmailVerify().getOutputDomain());
			props.setProperty(TAG_EMAIL_TOP_LEVEL_DOMAIN, data.getEmailVerify().getOutputTLD());
		}
		// Smart Mover
		if ((data.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			props.setProperty(TAG_SM_DEFAULTS_SET, String.valueOf(true));
			// SM name
			props.setProperty(TAG_SM_FULL_NAME, data.getSmartMover().getOutputFullName());
			props.setProperty(TAG_SM_PREFIX, data.getSmartMover().getOutputNamePrefix());
			props.setProperty(TAG_SM_FIRST_NAME, data.getSmartMover().getOutputNameFirst());
			props.setProperty(TAG_SM_MIDDLE_NAME, data.getSmartMover().getOutputNameMiddle());
			props.setProperty(TAG_SM_LAST_NAME, data.getSmartMover().getOutputNameLast());
			props.setProperty(TAG_SM_SUFFIX, data.getSmartMover().getOutputNameSuffix());
			// SM address output
			props.setProperty(TAG_SM_COMPANY, data.getSmartMover().getOutputAddrCompany());
			props.setProperty(TAG_SM_ADDRESS, data.getSmartMover().getOutputAddrLine());
			props.setProperty(TAG_SM_ADDRESS2, data.getSmartMover().getOutputAddrLine2());
			props.setProperty(TAG_SM_CITY, data.getSmartMover().getOutputAddrCity());
			props.setProperty(TAG_SM_STATE, data.getSmartMover().getOutputAddrState());
			props.setProperty(TAG_SM_ZIP, data.getSmartMover().getOutputAddrZip());
			props.setProperty(TAG_SM_ADDRESS_KEY, data.getSmartMover().getOutputAddrKey());

			props.setProperty(TAG_SM_MELISSA_ADDRESS_KEY, data.getSmartMover().getOutputMelissaAddrKey());
			props.setProperty(TAG_SM_BASE_MELISSA_ADDRESS_KEY, data.getSmartMover().getOutputBaseMelissaAddrKey());

			props.setProperty(TAG_SM_EFFICTIVE_DATE, data.getSmartMover().getOutputEffectiveDate());
			props.setProperty(TAG_SM_MOVE_TYPE_CODE, data.getSmartMover().getMoveTypeCode());
			props.setProperty(TAG_SM_MOVE_RETURN_CODE, data.getSmartMover().getMoveReturnCode());
			// SM additional output
			props.setProperty(TAG_SM_SUITE, data.getSmartMover().getOutputAddrSuite());
			props.setProperty(TAG_SM_PMB, data.getSmartMover().getOutputAddrPMB());
			props.setProperty(TAG_SM_PLUS4, data.getSmartMover().getOutputAddrPlus4());
			props.setProperty(TAG_SM_DP_CHECK_DIGIT, data.getSmartMover().getOutputAddrDPAndCheckDigit());
			props.setProperty(TAG_SM_CARRIER_ROUTE, data.getSmartMover().getOutputAddrCarrierRoute());
			props.setProperty(TAG_SM_URBANISATION, data.getSmartMover().getOutputAddrUrbanization());
			props.setProperty(TAG_SM_CITY_ABBREVATION, data.getSmartMover().getOutputAddrCityAbbreviation());
			props.setProperty(TAG_SM_COUNTRY, data.getSmartMover().getOutputAddrCountry());
			props.setProperty(TAG_SM_COUNTRY_ABBREVATION, data.getSmartMover().getOutputAddrCountryAbbreviation());
			props.setProperty(TAG_SM_DPV_FOOTNOTES, data.getSmartMover().getOutputDPVFootnotes());
			props.setProperty(TAG_SM_PARSED_ADDRESS_RANGE, data.getSmartMover().getOutputParsedAddrRange());
			props.setProperty(TAG_SM_PARSED_PRE_DIRECTIONAL, data.getSmartMover().getOutputParsedPreDirectional());
			props.setProperty(TAG_SM_PARSED_STREET_NAME, data.getSmartMover().getOutputParsedStreetName());
			props.setProperty(TAG_SM_PARSED_SUFFIX, data.getSmartMover().getOutputParsedSuffix());
			props.setProperty(TAG_SM_PARSED_POST_DIRECTIONAL, data.getSmartMover().getOutputParsedPostDirectional());
			props.setProperty(TAG_SM_PARSED_SUITE_NAME, data.getSmartMover().getOutputParsedSuiteName());
			props.setProperty(TAG_SM_PARSED_SUITE_RANGE, data.getSmartMover().getOutputParsedSuiteRange());
			props.setProperty(TAG_SM_PARSED_PMB_NAME, data.getSmartMover().getOutputParsedPMBName());
			props.setProperty(TAG_SM_PARSED_PMB_RANGE, data.getSmartMover().getOutputParsedPMBRange());
			props.setProperty(TAG_SM_PARSED_EXTRA_INFO, data.getSmartMover().getOutputParsedExtraInfo());
		}
		// Match Up
		if ((data.checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			props.setProperty(TAG_MU_DEFAULTS_SET, String.valueOf(true));
			props.setProperty(TAG_MU_DUPE_GROUP, data.getMatchUp().getDupeGroup());
			props.setProperty(TAG_MU_DUPE_COUNT, data.getMatchUp().getDupeCount());
			props.setProperty(TAG_MU_MATCHCODE_KEY, data.getMatchUp().getMatchcodeKey());
			props.setProperty(TAG_MU_RESULT_CODES, data.getMatchUp().getResultCodes());
		}
		// MatchUp Global
		if ((data.checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
			props.setProperty(TAG_MU_DEFAULTS_SET, String.valueOf(true));
			props.setProperty(TAG_MU_DUPE_GROUP, data.getMatchUp().getDupeGroup());
			props.setProperty(TAG_MU_DUPE_COUNT, data.getMatchUp().getDupeCount());
			props.setProperty(TAG_MU_MATCHCODE_KEY, data.getMatchUp().getMatchcodeKey());
			props.setProperty(TAG_MU_RESULT_CODES, data.getMatchUp().getResultCodes());
		}
		// IP Locator
		if ((data.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			props.setProperty(TAG_IP_DEFAULTS_SET, String.valueOf(true));
			props.setProperty(TAG_IP_ADDRESS, data.getIPLocator().getOutIPAddress());
			props.setProperty(TAG_IP_LONGITUDE, data.getIPLocator().getOutLongitude());
			props.setProperty(TAG_IP_LATITUDE, data.getIPLocator().getOutLatitude());
			props.setProperty(TAG_IP_ZIP, data.getIPLocator().getOutZip());
			props.setProperty(TAG_IP_REGION, data.getIPLocator().getOutRegion());
			props.setProperty(TAG_IP_NAME, data.getIPLocator().getOutName());
			props.setProperty(TAG_IP_DOMAIN, data.getIPLocator().getOutDomain());
			props.setProperty(TAG_IP_CITYNAME, data.getIPLocator().getOutCityName());
			props.setProperty(TAG_IP_COUNTRY, data.getIPLocator().getOutCountry());
			props.setProperty(TAG_IP_ABREVIATION, data.getIPLocator().getOutAbreviation());
			props.setProperty(TAG_IP_CONNECTION_SPEED, data.getIPLocator().getOutConnectionSpeed());
			props.setProperty(TAG_IP_CONNECTION_TYPE, data.getIPLocator().getOutConnectionType());
			props.setProperty(TAG_IP_UTC, data.getIPLocator().getOutUTC());
			props.setProperty(TAG_IP_CONTINENT, data.getIPLocator().getOutContinent());
		}
	}

	/**
	 * Called to store data in a repository
	 *
	 * @param rep
	 * @param idTransformation
	 * @param idStep
	 * @throws KettleException
	 */
	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		// save global properties
		saveGlobal();
		// Save attributes
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_SERVICE_TYPE, serviceType.toString());
		// Local object (none for now)
/*
 * // Common web/appliance
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_MAX_THREADS, maxThreads);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_MAX_REQUESTS, maxRequests);
 * // Web service
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_TIMEOUT, webTimeout);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_RETRIES, webRetries);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_OPT_ABORT, webAbortOnError);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_PROXY_HOST, webProxyHost);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_WEB_PROXY_PORT, webProxyPort);
 * // Local appliance
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_TIMEOUT, cvsTimeout);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_RETRIES, cvsRetries);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_OPT_ABORT, cvsAbortOnError);
 * rep.saveStepAttribute(idTransformation, idStep, TAG_ADVANCED_CONFIGURATION + "." + TAG_CVS_OPT_FAILOVER, cvsFailover);
 */
	}

	public void setData(MDCheckStepData data) {

		this.data = data;
	}

	/**
	 * Called to initialized default values
	 *
	 * @throws KettleException
	 */
	public void setDefault() throws KettleException {
		// Load the persistent global properties now

		loadGlobal();
		// Get user defined defaults
		Properties userDefaults = (new MDCustomDefaults(this).initDefaults(data.checkTypes));
		// Start setting the defaults
		serviceType = (doMatchUp() || doMatchUpGlobal()) ? ServiceType.Local : ServiceType.Web;
		// Common web/appliance defaults
		getWebSettingsByProduct();
		// Web service defaults
		webProxyHost = MDProps.getProperty(TAG_WEB_PROXY_HOST, "");
		webProxyPort = MDProps.getProperty(TAG_WEB_PROXY_PORT, "");
		webProxyUser = MDProps.getProperty(TAG_WEB_PROXY_USER, "");
		webProxyPass = MDProps.getProperty(TAG_WEB_PROXY_PASS, "");
		webAbortOnError = Boolean.parseBoolean(MDProps.getProperty(TAG_WEB_OPT_ABORT, "false"));
		// Local appliance defaults
		cvsFailover = Boolean.parseBoolean(MDProps.getProperty(TAG_CVS_OPT_FAILOVER, "false"));
		cvsTimeout = MDProps.getProperty(TAG_CVS_TIMEOUT, "45");
		cvsRetries = MDProps.getProperty(TAG_CVS_RETRIES, "5");
		cvsAbortOnError = Boolean.parseBoolean(MDProps.getProperty(TAG_CVS_OPT_ABORT, "false"));
		// customer defined defaults
		// FIXME: Consolidate defaults into the MDProps architecture.
		if (userDefaults.getProperty(TAG_DEFAULTS_SET) != null) {
			MDCheckData.defaultsSet = Boolean.valueOf(userDefaults.getProperty(TAG_DEFAULTS_SET));
			if (MDCheckData.defaultsSet) {
				MDCheckData.prefix1 = userDefaults.getProperty(TAG_NAME_PREFIX1);
				MDCheckData.firstName1 = userDefaults.getProperty(TAG_NAME_FIRSTNAME1);
				MDCheckData.middleName1 = userDefaults.getProperty(TAG_NAME_MIDDLENAME1);
				MDCheckData.lastName1 = userDefaults.getProperty(TAG_NAME_LASTNAME1);
				MDCheckData.suffix1 = userDefaults.getProperty(TAG_NAME_SUFFIX1);
				MDCheckData.gender1 = userDefaults.getProperty(TAG_NAME_GENDER1);
				MDCheckData.prefix2 = userDefaults.getProperty(TAG_NAME_PREFIX2);
				MDCheckData.firstName2 = userDefaults.getProperty(TAG_NAME_FIRSTNAME2);
				MDCheckData.middleName2 = userDefaults.getProperty(TAG_NAME_MIDDLENAME2);
				MDCheckData.lastName2 = userDefaults.getProperty(TAG_NAME_LASTNAME2);
				MDCheckData.suffix2 = userDefaults.getProperty(TAG_NAME_SUFFIX2);
				MDCheckData.gender2 = userDefaults.getProperty(TAG_NAME_GENDER2);
				MDCheckData.salutation = userDefaults.getProperty(TAG_NAME_SALUTATION);
				// AddressVerify
				MDCheckData.address = userDefaults.getProperty(TAG_ADDRESS_ADDRESS);
				MDCheckData.address2 = userDefaults.getProperty(TAG_ADDRESS_ADDRESS2);
				MDCheckData.city = userDefaults.getProperty(TAG_ADDRESS_CITY);
				MDCheckData.state = userDefaults.getProperty(TAG_ADDRESS_STATE);
				MDCheckData.zip = userDefaults.getProperty(TAG_ADDRESS_ZIP);
				MDCheckData.country = userDefaults.getProperty(TAG_ADDRESS_COUNTRY);
				MDCheckData.addressKey = userDefaults.getProperty(TAG_ADDRESS_ADDRESS_KEY);
				// Additional address output
				MDCheckData.outputSuite = userDefaults.getProperty(TAG_SUITE);
				MDCheckData.outputPrivateMailBox = userDefaults.getProperty(TAG_PRIVATE_MAILBOX);
				MDCheckData.outputUrbanization = userDefaults.getProperty(TAG_URBANIZATION);
				MDCheckData.outputPlus4 = userDefaults.getProperty(TAG_PLUS4);
				MDCheckData.outputDPAndCheckDigit = userDefaults.getProperty(TAG_DP_CHECK_DIGIT);
				MDCheckData.outputCarrierRoute = userDefaults.getProperty(TAG_CARRIER_ROUTE);
				MDCheckData.outputZipTypeCode = userDefaults.getProperty(TAG_ZIP_TYPE_CODE);
				MDCheckData.outputAddressTypeCode = userDefaults.getProperty(TAG_ADDRESS_TYPE_CODE);
				MDCheckData.outputAddressTypeDescription = userDefaults.getProperty(TAG_ADDRESS_TYPE_DESCRIPTION);
				MDCheckData.outputCMRA = userDefaults.getProperty(TAG_CMRA);
				MDCheckData.outputElotNumber = userDefaults.getProperty(TAG_ELOT_NUMBER);
				MDCheckData.outputElotOrder = userDefaults.getProperty(TAG_ELOT_ORDER);
				MDCheckData.outputDeliveryIndication = userDefaults.getProperty(TAG_DELIVERY_INDICATION);
				// Address Geographic info
				MDCheckData.outputCityAbbreviation = userDefaults.getProperty(TAG_CITY_ABBREVATION);
				MDCheckData.outputCountyName = userDefaults.getProperty(TAG_COUNTY_NAME);
				MDCheckData.outputCountyFips = userDefaults.getProperty(TAG_COUNTY_FIPS);
				MDCheckData.outputCongressionalDistrict = userDefaults.getProperty(TAG_CONGRESSIONAL_DISTRICT);
				MDCheckData.outputTimezone = userDefaults.getProperty(TAG_TIME_ZONE);
				MDCheckData.outputTimezoneCode = userDefaults.getProperty(TAG_TIME_ZONE_CODE);
				// parsed address info
				MDCheckData.outputParsedAddressRange = userDefaults.getProperty(TAG_PARSED_ADDRESS_RANGE);
				MDCheckData.outputParsedPreDirectional = userDefaults.getProperty(TAG_PARSED_PRE_DIRECTIONAL);
				MDCheckData.outputParsedStreetName = userDefaults.getProperty(TAG_PARSED_STREET_NAME);
				MDCheckData.outputParsedSuffix = userDefaults.getProperty(TAG_PARSED_SUFFIX);
				MDCheckData.outputParsedPostDirectional = userDefaults.getProperty(TAG_PARSED_POST_DIRECTIONAL);
				MDCheckData.outputParsedSuiteName = userDefaults.getProperty(TAG_PARSED_SUITE_NAME);
				MDCheckData.outputParsedSuiteRange = userDefaults.getProperty(TAG_PARSED_SUITE_RANGE);
				MDCheckData.outputParsedPMBName = userDefaults.getProperty(TAG_PARSED_PMB_NAME);
				MDCheckData.outputParsedPMBRange = userDefaults.getProperty(TAG_PARSED_PMB_RANGE);
				MDCheckData.outputParsedRouteService = userDefaults.getProperty(TAG_PARSED_ROUTE_SERVICE);
				MDCheckData.outputParsedLockBox = userDefaults.getProperty(TAG_PARSED_LOCK_BOX);
				MDCheckData.outputParsedDeliveryInstallation = userDefaults.getProperty(TAG_PARSED_DELIVERY_INSTALLATION);
				MDCheckData.outputParsedExtraInformation = userDefaults.getProperty(TAG_PARSED_EXTRA_INFORMATION);
				/*
				 * EXTRA not curently used MDCheckData.outputCompany =
				 * userDefaults.getProperty(TAG_COMPANY);
				 * MDCheckData.outputStateName =
				 * userDefaults.getProperty(TAG_STATE_NAME);
				 * MDCheckData.outputZipTypeDescription =
				 * userDefaults.getProperty(TAG_ZIP_TYPE_DESCRIPTION);
				 * MDCheckData.outputCountryName =
				 * userDefaults.getProperty(TAG_COUNTRY_NAME);
				 */
				// GeoCoder
				MDCheckData.GeoLatitude = userDefaults.getProperty(TAG_GEO_LATITUDE);
				MDCheckData.GeoLongitude = userDefaults.getProperty(TAG_GEO_LONGITUDE);
				MDCheckData.GeoCounty = userDefaults.getProperty(TAG_GEO_COUNTY);
				MDCheckData.GeoCountyFIPS = userDefaults.getProperty(TAG_GEO_COUNTYFIPS);
				MDCheckData.GeoPlaceCode = userDefaults.getProperty(TAG_GEO_PLACE_CODE);
				MDCheckData.GeoPlaceName = userDefaults.getProperty(TAG_GEO_PLACE_NAME);
				MDCheckData.GeoTimeZone = userDefaults.getProperty(TAG_GEO_TIME_ZONE);
				MDCheckData.GeoTimeZoneCode = userDefaults.getProperty(TAG_GEO_TIME_ZONE_CODE);
				MDCheckData.GeoCBSACode = userDefaults.getProperty(TAG_GEO_CBSA_CODE);
				MDCheckData.GeoCBSALevel = userDefaults.getProperty(TAG_GEO_CBSA_LEVEL);
				MDCheckData.GeoCBSATitle = userDefaults.getProperty(TAG_GEO_CBSA_TITLE);
				MDCheckData.GeoCBSADivisionCode = userDefaults.getProperty(TAG_GEO_CBSA_DEVISION_CODE);
				MDCheckData.GeoCBSADivisionLevel = userDefaults.getProperty(TAG_GEO_CBSA_DIVISION_LEVEL);
				MDCheckData.GeoCBSADivisionTitle = userDefaults.getProperty(TAG_GEO_CBSA_DEVISION_TITLE);
				MDCheckData.GeoCensusBlock = userDefaults.getProperty(TAG_GEO_CENSUS_BLOCK);
				MDCheckData.GeoCensusTract = userDefaults.getProperty(TAG_GEO_CENSUS_TRACT);
				// Phone/Email
				// phone
				MDCheckData.phone = userDefaults.getProperty(TAG_PHONE);
				MDCheckData.phoneFormat = userDefaults.getProperty(TAG_PHONE_FORMAT);
				MDCheckData.phoneAreaCode = userDefaults.getProperty(TAG_PHONE_AREA_CODE);
				MDCheckData.phonePrefix = userDefaults.getProperty(TAG_PHONE_PREFIX);
				MDCheckData.phoneSuffix = userDefaults.getProperty(TAG_PHONE_SUFFIX);
				MDCheckData.phoneExtension = userDefaults.getProperty(TAG_PHONE_EXTENSION);
				MDCheckData.phoneCity = userDefaults.getProperty(TAG_PHONE_CITY);
				MDCheckData.phoneStateProvince = userDefaults.getProperty(TAG_PHONE_STATE_PROVINCE);
				MDCheckData.phoneCountyName = userDefaults.getProperty(TAG_PHONE_COUNTY_NAME);
				MDCheckData.phoneCountyFIPS = userDefaults.getProperty(TAG_PHONE_COUNTY_FIPS);
				MDCheckData.phoneCountryCode = userDefaults.getProperty(TAG_PHONE_COUNTRY_CODE);
				MDCheckData.phoneTimeZone = userDefaults.getProperty(TAG_PHONE_TIME_ZONE);
				MDCheckData.phoneTimeZoneCode = userDefaults.getProperty(TAG_PHONE_TIME_ZONE_CODE);
				// Email output
				MDCheckData.emailAddress = userDefaults.getProperty(TAG_EMAIL_ADDRESS);
				MDCheckData.emailMailBoxName = userDefaults.getProperty(TAG_EMAIL_MAIL_BOX_NAME);
				MDCheckData.emailTopLevelDomainDescription = userDefaults.getProperty(TAG_EMAIL_TOP_LEVEL_DOMAIN_DESCRIPTION);
				MDCheckData.emailDomainName = userDefaults.getProperty(TAG_EMAIL_DOMAIN_NAME);
				MDCheckData.emailTopLevelDomain = userDefaults.getProperty(TAG_EMAIL_TOP_LEVEL_DOMAIN);
			}
		}
		// Smart mover defaults
		if (userDefaults.getProperty(TAG_SM_DEFAULTS_SET) != null) {
			MDCheckData.smDefaultsSet = Boolean.valueOf(userDefaults.getProperty(TAG_SM_DEFAULTS_SET));
			if (MDCheckData.smDefaultsSet) {
				MDCheckData.sm_fullName = userDefaults.getProperty(TAG_SM_FULL_NAME);
				MDCheckData.sm_prefix = userDefaults.getProperty(TAG_SM_PREFIX);
				MDCheckData.sm_firstName = userDefaults.getProperty(TAG_SM_FIRST_NAME);
				MDCheckData.sm_middleName = userDefaults.getProperty(TAG_SM_MIDDLE_NAME);
				MDCheckData.sm_lastName = userDefaults.getProperty(TAG_SM_LAST_NAME);
				MDCheckData.sm_suffix = userDefaults.getProperty(TAG_SM_SUFFIX);
				// address
				MDCheckData.sm_company = userDefaults.getProperty(TAG_SM_COMPANY);
				MDCheckData.sm_address = userDefaults.getProperty(TAG_SM_ADDRESS);
				MDCheckData.sm_address2 = userDefaults.getProperty(TAG_SM_ADDRESS2);
				MDCheckData.sm_city = userDefaults.getProperty(TAG_SM_CITY);
				MDCheckData.sm_state = userDefaults.getProperty(TAG_SM_STATE);
				MDCheckData.sm_zip = userDefaults.getProperty(TAG_SM_ZIP);
				MDCheckData.sm_addressKey = userDefaults.getProperty(TAG_SM_ADDRESS_KEY);
				MDCheckData.sm_effectiveDate = userDefaults.getProperty(TAG_SM_EFFICTIVE_DATE);
				MDCheckData.sm_moveTypeCode = userDefaults.getProperty(TAG_SM_MOVE_TYPE_CODE);
				MDCheckData.sm_moveReturnCode = userDefaults.getProperty(TAG_SM_MOVE_RETURN_CODE);
				// additional info
				MDCheckData.sm_Suite = userDefaults.getProperty(TAG_SM_SUITE);
				MDCheckData.sm_PrivateMailBox = userDefaults.getProperty(TAG_SM_PMB);
				MDCheckData.sm_Plus4 = userDefaults.getProperty(TAG_SM_PLUS4);
				MDCheckData.sm_DPAndCheckDigit = userDefaults.getProperty(TAG_SM_DP_CHECK_DIGIT);
				MDCheckData.sm_CarrierRoute = userDefaults.getProperty(TAG_SM_CARRIER_ROUTE);
				MDCheckData.sm_Urbanization = userDefaults.getProperty(TAG_SM_URBANISATION);
				MDCheckData.sm_cityAbbrevation = userDefaults.getProperty(TAG_SM_CITY_ABBREVATION);
				MDCheckData.sm_country = userDefaults.getProperty(TAG_SM_COUNTRY);
				MDCheckData.sm_countryAbbreviation = userDefaults.getProperty(TAG_SM_COUNTRY_ABBREVATION);
				MDCheckData.sm_ParsedAddressRange = userDefaults.getProperty(TAG_SM_PARSED_ADDRESS_RANGE);
				MDCheckData.sm_ParsedPreDirectional = userDefaults.getProperty(TAG_SM_PARSED_PRE_DIRECTIONAL);
				MDCheckData.sm_ParsedStreetName = userDefaults.getProperty(TAG_SM_PARSED_STREET_NAME);
				MDCheckData.sm_ParsedSuffix = userDefaults.getProperty(TAG_SM_PARSED_SUFFIX);
				MDCheckData.sm_ParsedPostDirectional = userDefaults.getProperty(TAG_SM_PARSED_POST_DIRECTIONAL);
				MDCheckData.sm_ParsedSuiteName = userDefaults.getProperty(TAG_SM_PARSED_SUITE_NAME);
				MDCheckData.sm_ParsedSuiteRange = userDefaults.getProperty(TAG_SM_PARSED_SUITE_RANGE);
				MDCheckData.sm_ParsedPMBName = userDefaults.getProperty(TAG_SM_PARSED_PMB_NAME);
				MDCheckData.sm_ParsedPMBRange = userDefaults.getProperty(TAG_SM_PARSED_PMB_RANGE);
				MDCheckData.sm_ParsedExtraInformation = userDefaults.getProperty(TAG_SM_PARSED_EXTRA_INFO);
			}
		}
		// MatchUp defaults
		if (userDefaults.getProperty(TAG_MU_DEFAULTS_SET) != null) {
			MDCheckData.muDefaultsSet = Boolean.valueOf(userDefaults.getProperty(TAG_MU_DEFAULTS_SET));
			if (MDCheckData.muDefaultsSet) {
				MDCheckData.mu_DupeGroup = userDefaults.getProperty(TAG_MU_DUPE_GROUP);
				MDCheckData.mu_DupeCount = userDefaults.getProperty(TAG_MU_DUPE_COUNT);
				MDCheckData.mu_MatchcodeKey = userDefaults.getProperty(TAG_MU_MATCHCODE_KEY);
				MDCheckData.mu_ResultCodes = userDefaults.getProperty(TAG_MU_RESULT_CODES);
			}
		}
		// IP Locator defaults
		if (userDefaults.getProperty(TAG_IP_DEFAULTS_SET) != null) {
			MDCheckData.ipDefaultsSet = Boolean.valueOf(userDefaults.getProperty(TAG_IP_DEFAULTS_SET));
			if (MDCheckData.ipDefaultsSet) {
				MDCheckData.ip_Address = userDefaults.getProperty(TAG_IP_ADDRESS);
				MDCheckData.ip_Longitude = userDefaults.getProperty(TAG_IP_LONGITUDE);
				MDCheckData.ip_Latitude = userDefaults.getProperty(TAG_IP_LATITUDE);
				MDCheckData.ip_Zip = userDefaults.getProperty(TAG_IP_ZIP);
				MDCheckData.ip_Region = userDefaults.getProperty(TAG_IP_REGION);
				MDCheckData.ip_Name = userDefaults.getProperty(TAG_IP_NAME);
				MDCheckData.ip_Domain = userDefaults.getProperty(TAG_IP_DOMAIN);
				MDCheckData.ip_CityName = userDefaults.getProperty(TAG_IP_CITYNAME);
				MDCheckData.ip_Country = userDefaults.getProperty(TAG_IP_COUNTRY);
				MDCheckData.ip_Abreviation = userDefaults.getProperty(TAG_IP_ABREVIATION);
				MDCheckData.ip_ConnectionSpeed = userDefaults.getProperty(TAG_IP_CONNECTION_SPEED);
				MDCheckData.ip_ConnectionType = userDefaults.getProperty(TAG_IP_CONNECTION_TYPE);
				MDCheckData.ip_UTC = userDefaults.getProperty(TAG_IP_UTC);
				MDCheckData.ip_Continent = userDefaults.getProperty(TAG_IP_CONTINENT);
			}
		}

		if (MDCheckMeta.isTest) {
			System.out.println("TEST - setDefault - OK");
		}
	}

	public boolean isUpdateData() {

		return updateData;
	}

	public void setUpdateData(boolean updateData) {

		this.updateData = updateData;
	}

	/**
	 * @return true if the user should be nagged about something
	 */
	public String shouldNag() {

		return shouldNag(serviceType);
	}

	/**
	 * @return true if the user should be nagged about something for a specific
	 * service type
	 */
	public String shouldNag(ServiceType serviceType) {
		// Always nag about license string
		if ((getLicense(TAG_PRIMARY_LICENSE).trim().length() == 0) && (getLicense(TAG_TRIAL_LICENSE).trim().length() == 0) && !((data.getCheckTypes() & MDCheckMeta.MDCHECK_MATCHUP) != 0)) {
			return "SetLicenseWarning";
		}
		// Validate the license
		switch (testLicense(data.getCheckTypes())) {
			case LicenseError:
				return "LicenseError";
			case LicenseMissing:
				return "LicenseMissing";
			case LicenseExpired:
				return "LicenseExpired";
			case NotEnterprise:
				return "NotEnterprise";
		}
		// Service specific nagging
		switch (serviceType) {
			case Local:
				String dataPath = getLocalDataPath();
				// just check the directory incase its a web only install
				File f = new File(dataPath);
				if (dataPath.trim().length() == 0) {
					return "SetDataPathWarning";
				} else if (doCV() && (!f.exists() || !f.canRead())) {
					return "BadDataPath";
				}
				break;
			case Web:
				// nothing to nag about
				if ((MDProps.getProperty(TAG_PRIMARY_ID, "").trim().length() == 0) && (MDProps.getProperty(TAG_TRIAL_ID, "").trim().length() == 0) && !(isCommunity())) {
					return "CustomerIDWarning";
				}
				break;
			case CVS:
				if (getCVSServerURL().trim().length() == 0) {
					return "SetServerURLWarning";
				}
				break;
		}
		// Should definitely nag
		return null;
	}

	/**
	 * Called to test a license string and determine if it has a valid setting.
	 *
	 * @param
	 * @param checkTypes
	 * @return A result code
	 */
	public LicenseTestResult testLicense(int checkTypes) {

		String testResult = "";
		if ((checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			testResult = getTestResults("CV");
		} else if ((checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			testResult = getTestResults(AdvancedConfigurationMeta.MDLICENSE_PRODUCT_SmartMover);
		} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			testResult = LicenseTestResult.NoErrors.toString();// MU is always licensed
		} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
			testResult = getTestResults(AdvancedConfigurationMeta.MDLICENSE_PRODUCT_MatchUpIntl);
		} else if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			testResult = getTestResults(AdvancedConfigurationMeta.MDLICENSE_PRODUCT_IpLocatorObject);
		}
		if (Const.isEmpty(testResult)) {
			return LicenseTestResult.LicenseError;
		} else if ((testResult.contentEquals("tlicInvalidLicense")) || (testResult.contentEquals("tlicInvalidProduct"))) {
			return LicenseTestResult.LicenseMissing;
		} else if (testResult.contentEquals("tlicLicenseExpired")) {
			return LicenseTestResult.LicenseExpired;
		} else {
			return LicenseTestResult.NoErrors;
		}
	}

	/**
	 * Called to validate settings before saving
	 *
	 * @param warnings
	 * @param errors
	 */
	public void validate(List<String> warnings, List<String> errors) {
		// validated in mdSettings
	}

	private boolean checkContactZone() {

		File checkFile = new File("ui" + Const.FILE_SEPARATOR + TAG_CONTACT_ZONE_FILE);
		isContactZone = checkFile.exists();

		if (MDCheckMeta.isTest) {
			isContactZone = true;
			System.out.println("TEST - checkContactZone to true");
		} else {
			log.logBasic("ContactZone check file : " + checkFile.getAbsolutePath());
			log.logBasic("ContactZone = " + isContactZone);
		}

		return isContactZone;
	}

	private boolean checkUpdate() {

		if (isUpdateData()) {
			// If this is already set just return otherwise
			// we will get a a false result.
			return isUpdateData();
		}

		if (!Props.isInitialized()) {
			System.out.println("props not initalized: skipping check update");
			return false;
		}
		String insVer     = getInstalledPluginVersion();
		String curVersion = getVersionFromManifest();
		if (insVer.equals(curVersion)) {
			log.logBasic("MDCheck version is up to date : " + insVer);
			return false;
		} else {
			log.logBasic("MDCheck version update detected. Installed " + insVer + " updating to version " + curVersion);
		}
		return true;
	}

	/**
	 * @return true if configured for matchup
	 */
	private boolean doCV() {

		return (data.getCheckTypes() & MDCheckMeta.MDCHECK_FULL) != 0;
	}

	/**
	 * @return true if configured for matchup
	 */
	private boolean doMatchUp() {

		return (data.getCheckTypes() & MDCheckMeta.MDCHECK_MATCHUP) != 0;
	}

	private boolean doMatchUpGlobal() {

		return (data.getCheckTypes() & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0;
	}

	private String getVersionFromManifest() {

		String   version   = "0";
		Class<?> clazz     = this.getClass();
		String   className = clazz.getSimpleName() + ".class";
		String   classPath = clazz.getResource(className).toString();
		//String buildNum = classPath.substring(classPath.indexOf("Build") + 5, classPath.indexOf(".jar") - 1);
		if (!classPath.startsWith("jar")) {
			version = "0";//BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest    = new Manifest(new URL(manifestPath).openStream());
				String   implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				if (implVersion == null) {
					version = "0";
				} else {
					version = implVersion;
				}
			} catch (Exception e) {
				version = "0";
			}
		}
		return version;
	}

	private String getInstalledPluginVersion() {

		String ver = "0";
		if (Props.isInitialized()) {
			ver = Props.getInstance().getProperty(TAG_MDCHECK_VERSION);
			if (ver == null) {
				ver = "0";
			}
		}
		return ver;
	}

//	private String getCurrentPluginVersion() {
//		String fileSep = Const.FILE_SEPARATOR;
//		File srcDir = getSrcDir();
//		String version = "";
//		String versionPath = srcDir + fileSep + "version.xml";
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		FileReader reader = null;
//		try {
//			File file = new File(versionPath);
//			if (!file.exists()) {
//				version = "";
//			}
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			reader = new FileReader(versionPath);
//			Document dom = db.parse(new InputSource(reader));
//			NodeList versionElements = dom.getElementsByTagName("version");
//			if (versionElements.getLength() >= 1) {
//				Element versionElement = (Element) versionElements.item(0);
//				String build = 	versionElement.getAttribute("buildId");
//				version = versionElement.getTextContent() + "_" + build;
//			}
//		} catch (Exception e) {
//			System.out.println("Error reading version: " + e.getMessage());
//		} finally {
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//			} catch (Exception e) {
//				System.out.println("Error closing version file: " + e.getMessage());
//			}
//		}
//		return version;
//	}

	private String getPrimaryTestResult() {

		return MDProps.getProperty(TAG_PRIMARY_TEST_RESULT, "");
	}

	private int getPrimayrRetVal() {

		return Integer.parseInt(MDProps.getProperty(TAG_PRIMARY_RET_VAL, String.valueOf(MDLICENSE_None)));
	}

	private File getSrcDir() {

		String fileSep     = Const.FILE_SEPARATOR;
		String decodedPath = "";
		File   ssdd        = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error getting source dir: " + e.getMessage());
		}
		return new File(decodedPath);
	}

	private String getTestResults(String product) {

		if (product.equals("CV")) {
			if (((getPrimayrRetVal() & MDLICENSE_Name) != 0) || ((getPrimayrRetVal() & MDLICENSE_Address) != 0) || ((getPrimayrRetVal() & MDLICENSE_GeoCode) != 0) || ((getPrimayrRetVal() & MDLICENSE_GeoPoint) != 0) || (
					(getPrimayrRetVal() & MDLICENSE_Phone) != 0) || ((getPrimayrRetVal() & MDLICENSE_Email) != 0)) {
				return getPrimaryTestResult();
			} else if (((getTrialRetVal() & MDLICENSE_Name) != 0) || ((getTrialRetVal() & MDLICENSE_Address) != 0) || ((getTrialRetVal() & MDLICENSE_GeoCode) != 0) || ((getTrialRetVal() & MDLICENSE_GeoPoint) != 0) || (
					(getTrialRetVal() & MDLICENSE_Phone) != 0) || ((getTrialRetVal() & MDLICENSE_Email) != 0)) {
				return getTrialTestResult();
			} else {
				return getPrimaryTestResult();
			}
		} else if (product.equals(AdvancedConfigurationMeta.MDLICENSE_PRODUCT_SmartMover)) {
			if ((getPrimayrRetVal() & MDLICENSE_SmartMover) != 0) {
				return getPrimaryTestResult();
			} else if ((getTrialRetVal() & MDLICENSE_SmartMover) != 0) {
				return getTrialTestResult();
			} else {
				return getPrimaryTestResult();
			}
		} else if (product.equals(AdvancedConfigurationMeta.MDLICENSE_PRODUCT_MatchUpObject)) {
			if ((getPrimayrRetVal() & MDLICENSE_MatchUp) != 0) {
				return getPrimaryTestResult();
			} else if ((getTrialRetVal() & MDLICENSE_MatchUp) != 0) {
				return getTrialTestResult();
			} else {
				return getPrimaryTestResult();
			}
		} else if (product.equals(AdvancedConfigurationMeta.MDLICENSE_PRODUCT_MatchUpIntl)) {
			if ((getPrimayrRetVal() & MDLICENSE_MatchUpGlobal) != 0) {
				return getPrimaryTestResult();
			} else if ((getTrialRetVal() & MDLICENSE_MatchUpGlobal) != 0) {
				return getTrialTestResult();
			} else {
				return getPrimaryTestResult();
			}
		} else if (product.equals(AdvancedConfigurationMeta.MDLICENSE_PRODUCT_IpLocatorObject)) {
			if ((getPrimayrRetVal() & MDLICENSE_IPLocator) != 0) {
				return getPrimaryTestResult();
			} else if ((getTrialRetVal() & MDLICENSE_IPLocator) != 0) {
				return getTrialTestResult();
			} else {
				return getPrimaryTestResult();
			}
		} else {
			return getPrimaryTestResult();
		}
	}

	private int getTrialRetVal() {

		return Integer.parseInt(MDProps.getProperty(TAG_TRIAL_RET_VAL, String.valueOf(MDLICENSE_None)));
	}

	private String getTrialTestResult() {

		return MDProps.getProperty(TAG_TRIAL_TEST_RESULT, "");
	}

	private void loadClassFiles() throws KettleException {

		boolean loadExt    = true;
		File    libext_dir = null;

		if (MDCheckMeta.isTest) {
			// FIXME get this path from config file
			libext_dir = new File("lib-md");

		} else if (!isCluster) {
			libext_dir = new File(Const.getKettleDirectory(), "MD" + Const.FILE_SEPARATOR + "libext");
		} else {
			String path    = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			String libPath = path.substring(0, path.lastIndexOf("/"));
			libext_dir = new File(libPath, "MD" + Const.FILE_SEPARATOR + "libext");
		}

		if (libext_dir.exists()) {
			URLClassLoader spoonLoader = (URLClassLoader) KettleVFS.class.getClassLoader();
			URL[]          urls        = spoonLoader.getURLs();
			for (URL url : urls) {
				if (url.toString().contains("MDSettings")) {
					loadExt = false;
				}
			}

			if (log != null) {
				log.logBasic(" - Meta ext Class Files loaded = " + !loadExt);
			}
			if (loadExt) {
				Class<URLClassLoader> sysClass = URLClassLoader.class;
				Method                sysMethod;
				try {
					sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
					sysMethod.setAccessible(true);
					for (File file : libext_dir.listFiles()) {
						sysMethod.invoke(spoonLoader, new Object[] { file.toURI().toURL() });
						log.logBasic("Meta Loading external Class File - " + file.getAbsolutePath());
					}
				} catch (NoSuchMethodException e) {
					throw new KettleException(e.getMessage());
				} catch (SecurityException e) {
					throw new KettleException(e.getMessage());
				} catch (IllegalAccessException e) {
					throw new KettleException(e.getMessage());
				} catch (IllegalArgumentException e) {
					throw new KettleException(e.getMessage());
				} catch (InvocationTargetException e) {
					throw new KettleException(e.getMessage());
				} catch (MalformedURLException e) {
					throw new KettleException(e.getMessage());
				}
			}
		}
	}

	private void moveTmp() {

		Collection<File> files  = null;
		File             tmpDir = new File(new File(Const.getKettleDirectory()), "tmp");
		if (!tmpDir.exists()) {
			return;
		}
		String fileSep   = Const.FILE_SEPARATOR;
		File   kettleDir = new File(Const.getKettleDirectory());
		File   md_dir    = new File(kettleDir, "MD");
		File   dest32    = new File(kettleDir, "MD" + fileSep + "32_bit");
		File   dest64    = new File(kettleDir, "MD" + fileSep + "64_bit");
		File   tmp32bit  = new File(tmpDir, "32_bit");
		File   tmp64bit  = new File(tmpDir, "64_bit");
		File   tmpLibext = new File(tmpDir, "libext");
		if (tmpLibext.exists()) {
			boolean success = false;
			log.logBasic("Moving libext tmp files ");
			try {
				File destLibext = new File(md_dir, "libext");
				FileUtils.deleteDirectory(destLibext);
				FileUtils.copyDirectory(tmpLibext, destLibext, false);
				success = true;
			} catch (IOException e) {
				log.logError("Error copying libext: " + e.getMessage());
			}

			if (success) {
				try {
					FileUtils.deleteDirectory(tmpLibext);
				} catch (IOException e) {
					log.logError("Error deleting tmpLibext: " + e.getMessage());
				}
			}
		}
		if (tmp32bit.exists()) {
			log.logBasic("Moving 32_bit object files ");
			boolean success = false;
			files = FileUtils.listFiles(tmp32bit, new String[] { "dll", "so" }, false);
			for (File file : files) {
				try {
					FileUtils.copyFileToDirectory(file, dest32, false);
					success = true;
				} catch (IOException e) {
					log.logError("Error copying file: " + file + " - " + e.getMessage());
				}
			}
			if (success) {
				try {
					FileUtils.deleteDirectory(tmp32bit);
				} catch (IOException e) {
					log.logError("Error deleting dir: " + e.getMessage());
				}
			}
		}
		if (tmp64bit.exists()) {
			log.logBasic("Moving 64_bit object files ");
			boolean success = false;
			files = FileUtils.listFiles(tmp64bit, new String[] { "dll", "so" }, false);
			for (File file : files) {
				try {
					FileUtils.copyFileToDirectory(file, dest64, false);
					success = true;
				} catch (IOException e) {
					log.logError("Error copying file: " + file + " - " + e.getMessage());
				}
			}
			if (success) {
				try {
					FileUtils.deleteDirectory(tmp64bit);
				} catch (IOException e) {
					log.logError("Error deleting dir: " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Caled to save the global properties
	 *
	 * @throws KettleException
	 */
	private void saveGlobal() throws KettleException {
		// Make sure they are persisted
		try {
			MDProps.save();
		} catch (IOException e) {
			MDProps.revert();
			throw new KettleException(BaseMessages.getString(PKG, "MDCheck.Error.ProblemSavingGlobal"), e);
		}
	}

	public String getInstallError() {

		return installError;
	}

	public Exception getInstallException() {

		return installException;
	}

	public boolean isContactZone() {

		return isContactZone;
	}

	public enum LicenseTestResult {
		NoErrors, LicenseError, LicenseMissing, LicenseExpired, NotEnterprise
	}

	public enum ServiceType {
		Local, Web, CVS
	}
}
