package com.melissadata.cz.support;

/**
 * Helper class containing all the static tags used throughout CZ projects
 */
public class MDPropTags {
	public static final String TAG_MELISSADATA_SPECIAL_USAGE           = "melissadata_special_usage";
	public static final String MD_COMMUNITY_LICENSE                    = "WZsWSghF6LukTW9Rcw9nCQ==VCbJ0Enrx7KJXnEbJl9UUF==Wx/9yGmnDSRClngXTKDHlK==";
	// License string and customer id
	// License
	public static final String TAG_PRIMARY_LICENSE                     = "license";
	public static final String TAG_PRIMARY_LICENSE_ENTERPRISE          = "license_enterprise";
	public static final String TAG_PRIMARY_RET_VAL                     = "license_retVal";
	public static final String TAG_PRIMARY_EXPIRATION                  = "license_expiration";
	public static final String TAG_PRIMARY_ID                          = "primary_customerID";
	public static final String TAG_PRIMARY_PRODUCT                     = "primary_product";
	public static final String TAG_PRIMARY_TEST_RESULT                 = "primary_TestResult";
	public static final String TAG_PRIMARY_MDAT_LIC_CODE               = "primary_mdat_lic_code";
	public static final String TAG_PRODUCT_LIST                        = "licensed_products";
	public static final String TAG_GEO_LEVEL                           = "geoLevel";
	public static final String TAG_MATCHUP_COMMUNITY                   = "muCommunity";
	public static final String TAG_MATCHUP_LITE                        = "muLite";
	public static final String TAG_MATCHUP_ENTERPRISE                  = "muEnterprise";
	public static final String TAG_TRIAL_LICENSE                       = "trial_license";
	public static final String TAG_TRIAL_LICENSE_ENTERPRISE            = "trial_enterprise";
	public static final String TAG_TRIAL_RET_VAL                       = "trial_retVal";
	public static final String TAG_TRIAL_EXPIRATION                    = "trial_license_expiration";
	public static final String TAG_TRIAL_ID                            = "trial_customerID";
	public static final String TAG_TRIAL_PRODUCT                       = "trial_product";
	public static final String TAG_TRIAL_TEST_RESULT                   = "trial_TestResult";
	public static final String TAG_TRIAL_MDAT_LIC_CODE                 = "trial_mdat_lic_code";
	// Product Codes
	public static final String MDLICENSE_PRODUCT_AddressObject         = "P1";
	public static final String MDLICENSE_PRODUCT_CanadianAddon         = "P2";
	public static final String MDLICENSE_PRODUCT_RBDIAddon             = "P3";
	public static final String MDLICENSE_PRODUCT_PhoneObject           = "P5";
	public static final String MDLICENSE_PRODUCT_NameObject            = "P6";
	public static final String MDLICENSE_PRODUCT_EmailObject           = "P7";
	public static final String MDLICENSE_PRODUCT_GeoCoder              = "P8";
	public static final String MDLICENSE_PRODUCT_GeoPoint              = "P9";
	public static final String MDLICENSE_PRODUCT_PresortObject         = "P10";
	public static final String MDLICENSE_PRODUCT_MatchUpObject         = "P11";
	public static final String MDLICENSE_PRODUCT_MatchUpGUI            = "P12";
	public static final String MDLICENSE_PRODUCT_StyleListObject       = "P13";
	public static final String MDLICENSE_PRODUCT_IpLocatorObject       = "P14";
	public static final String MDLICENSE_PRODUCT_TelcoObject           = "P15";
	public static final String MDLICENSE_PRODUCT_RightFielderObject    = "P16";
	public static final String MDLICENSE_PRODUCT_SalesTaxObject        = "P17";
	public static final String MDLICENSE_PRODUCT_Mailers               = "P18";
	public static final String MDLICENSE_PRODUCT_SSISTier1             = "P19";
	public static final String MDLICENSE_PRODUCT_SSISTier2             = "P20";
	public static final String MDLICENSE_PRODUCT_GeoCoderCanadianAddon = "P21";
	public static final String MDLICENSE_PRODUCT_GlobalVerify          = "P22";
	public static final String MDLICENSE_PRODUCT_SmartMover            = "P23";
	public static final String MDLICENSE_PRODUCT_MatchUpLite           = "P25";
	public static final String MDLICENSE_PRODUCT_Personator            = "P26";
	public static final String MDLICENSE_PRODUCT_ExpressEntry          = "P27";
	public static final String MDLICENSE_PRODUCT_IdOnly                = "P28";
	public static final String MDLICENSE_PRODUCT_MailersPro            = "P29";
	public static final String MDLICENSE_PRODUCT_MailersStandard       = "P30";
	public static final String MDLICENSE_PRODUCT_Profiler              = "P31";
	public static final String MDLICENSE_PRODUCT_ReverseGeo            = "P32";
	public static final String MDLICENSE_PRODUCT_ExpressEntryDeskTop   = "P33";
	public static final String MDLICENSE_PRODUCT_Property              = "P34";
	public static final String MDLICENSE_PRODUCT_MatchUpIntl           = "P35";
	public static final String MDLICENSE_PRODUCT_PreSortPallets        = "P36";
	public static final String MDLICENSE_PRODUCT_GeoPointNew           = "P37";
	public static final String MDLICENSE_PRODUCT_Community             = "P38";
	public static final String MDLICENSE_PRODUCT_Cleanser              = "P39";
	public static final String MDLICENSE_PRODUCT_BusinessCoder         = "P40";
	public static final String MDLICENSE_PRODUCT_GeoCoderNew           = "P41";
	//FIXME change Pcode for Personator World
	public static final String MDLICENSE_PRODUCT_PersonatorWorld       = "P42";
	public static final String MDLICENSE_PRODUCT_Any                   = "P*";

	public static final String MDLICENSE_OPTS_NonExpiringDB            = "O1";
	public static final String MDLICENSE_OPTS_Enterprise               = "O2";
	public static final String MDLICENSE_OPTS_Any                      = "O*";
	//
	public static final int    MDLICENSE_None                          = 0x0000;
	public static final int    MDLICENSE_Name                          = 0x0001;
	public static final int    MDLICENSE_Address                       = 0x0002;
	public static final int    MDLICENSE_Canada                        = 0x0004;
	public static final int    MDLICENSE_RBDI                          = 0x0008;
	public static final int    MDLICENSE_GeoCode                       = 0x0010;
	public static final int    MDLICENSE_GeoPoint                      = 0x0020;
	public static final int    MDLICENSE_Phone                         = 0x0040;
	public static final int    MDLICENSE_Email                         = 0x0080;
	public static final int    MDLICENSE_CanadaGeo                     = 0x0100;
	public static final int    MDLICENSE_MatchUp                       = 0x0200;
	public static final int    MDLICENSE_IPLocator                     = 0x0400;
	public static final int    MDLICENSE_SmartMover                    = 0x0800;
	public static final int    MDLICENSE_GlobalVerify                  = 0x01000;
	public static final int    MDLICENSE_Presort                       = 0x02000;
	// public static final int MDLICENSE_MatchUpCommunity = 0x04000;
	public static final int    MDLICENSE_MatchUpLite                   = 0x08000;
	public static final int    MDLICENSE_Personator                    = 0x10000;
	public static final int    MDLICENSE_Profiler                      = 0x20000;
	public static final int    MDLICENSE_BusinessCoder                 = 0x40000;
	public static final int    MDLICENSE_Property                      = 0x80000;
	public static final int    MDLICENSE_Cleanser                      = 0x100000;
	public static final int    MDLICENSE_Community                     = 0x200000;
	public static final int    MDLICENSE_PersonatorWorld               = 0x400000;

	//Menu ID
	public static final String MENU_ID_CONTACTVERIFY                   = "ContactVerify";
	public static final String MENU_ID_PRESORT                         = "PreSort";
	public static final String MENU_ID_MATCHUP                         = "MatchUp";
	public static final String MENU_ID_MATCHUP_GLOBAL                  = "MatchUpGlobal";
	public static final String MENU_ID_PROFILER                        = "Profiler";
	public static final String MENU_ID_IPLOCATOR                       = "IpLocator";
	public static final String MENU_ID_SMARTMOVER                      = "SmartMover";
	public static final String MENU_ID_GLOBALVERIFY                    = "GlobalVerify";
	public static final String MENU_ID_PERSONATOR                      = "Personator";
	public static final String MENU_ID_PERSONATOR_WORLD                = "PersonatorWorld";
	public static final String MENU_ID_BUSINESS_CODER                  = "BusinessCoder";
	public static final String MENU_ID_PROPERTY                        = "Property";
	public static final String MENU_ID_CLEANSER                        = "Cleanser";
	public static final String MENU_ID_LICENSE                         = "License";

	// Local object tags
	public static final String TAG_LOCAL_DATA_PATH                     = "data_path";
	public static final String TAG_LOCAL_DATA_PATH_MATCHUP             = "data_path_mu";
	public static final String TAG_LOCAL_DATA_PATH_PRESORT             = "presort_datapath";
	public static final String TAG_CLEANSER_OPERATION_PATH             = "cleanser_operations_path";
	// Temporary file work directory
	public static final String TAG_WORK_PATH_MATCHUP                   = "work_path_mu";
	public static final String TAG_WEB_PROXY_HOST                      = "proxy_host";
	public static final String TAG_WEB_PROXY_PORT                      = "proxy_port";
	public static final String TAG_WEB_PROXY_USER                      = "proxy_user";
	public static final String TAG_WEB_PROXY_PASS                      = "proxy_pass";
	// Web service tags
	public static final String TAG_WEB_NAME_PARSER_URL                 = "name_parser_url";
	public static final String TAG_WEB_ADDRESS_VERIFIER_URL            = "address_verifier_url";
	public static final String TAG_WEB_GLOBAL_ADDRESS_URL              = "global_address_url";
	public static final String TAG_WEB_GLOBAL_NAME_URL                 = "global_name_url";
	public static final String TAG_WEB_GLOBAL_PHONE_URL                = "global_phone_url";
	public static final String TAG_WEB_GLOBAL_EMAIL_URL                = "global_email_url";
	public static final String TAG_WEB_GEO_CODER_URL                   = "geo_coder_url";
	public static final String TAG_WEB_RBDI_URL                        = "rbdi_url";
	public static final String TAG_WEB_PHONE_VERIFIER_URL              = "phone_verifier_url";
	public static final String TAG_WEB_EMAIL_VERIFIER_URL              = "email_verifier_url";
	public static final String TAG_WEB_IP_LOCATOR_URL                  = "ip_locator_url";
	public static final String TAG_WEB_SEND_LICENCE_URL                = "sendLicence_url";
	public static final String TAG_WEB_PERSONATOR_URL                  = "personator_url";
	public static final String TAG_WEB_PERSONATOR_WORLD_URL            = "personatorWorld_url";
	public static final String TAG_WEB_PROPERTY_URL                    = "propertywebservice_url";
	public static final String TAG_WEB_BUSINESS_CODER_URL              = "business_coder_url";
	public static final String TAG_WEB_NCOA_URL                        = "ncoa_url";
	public static final String TAG_WEB_CCOA_URL                        = "ccoa_url";
	// web/appliance tags
	// Check
	public static final String TAG_CONTACTVERIFY_THREADS               = "max_check_threads";
	public static final String TAG_CONTACTVERIFY_REQUESTS              = "max_check_requests";
	public static final String TAG_CONTACTVERIFY_WEB_TIMEOUT           = "check_timeout";
	public static final String TAG_CONTACTVERIFY_WEB_RETRIES           = "check_retries";
	public static final int    CONTACTVERIFY_THREAD_LIMIT              = 10;
	public static final int    CONTACTVERIFY_REQUEST_LIMIT             = 100;
	// Smart Mover
	public static final String TAG_SMARTMOVER_THREADS                  = "max_smartmover_threads";
	public static final String TAG_SMARTMOVER_REQUESTS                 = "max_smartmover_requests";
	public static final String TAG_SMARTMOVER_WEB_TIMEOUT              = "smartmover_timeout";
	public static final String TAG_SMARTMOVER_WEB_RETRIES              = "smartmover_retries";
	public static final int    SMARTMOVER_THREAD_LIMIT                 = 10;
	public static final int    SMARTMOVER_REQUEST_LIMIT                = 100;
	// Global Verify
	public static final String TAG_GLOBALVERIFY_THREADS                = "max_globalverify_threads";
	public static final String TAG_GLOBALVERIFY_REQUESTS               = "max_globalverify_requests";
	public static final String TAG_GLOBALVERIFY_WEB_TIMEOUT            = "globalverify_timeout";
	public static final String TAG_GLOBALVERIFY_WEB_RETRIES            = "globalverify_retries";
	public static final int    GLOBALVERIFY_THREAD_LIMIT               = 3;
	public static final int    GLOBALVERIFY_REQUEST_LIMIT              = 10;
	// IP Locator
	public static final String TAG_IPLOCATOR_THREADS                   = "max_iplocator_threads";
	public static final String TAG_IPLOCATOR_REQUESTS                  = "max_iplocator_requests";
	public static final String TAG_IPLOCATOR_WEB_TIMEOUT               = "iplocator_timeout";
	public static final String TAG_IPLOCATOR_WEB_RETRIES               = "iplocator_retries";
	public static final int    IPLOCATOR_THREAD_LIMIT                  = 10;
	public static final int    IPLOCATOR_REQUEST_LIMIT                 = 100;
	// Property Service
	public static final String TAG_PROPERTY_THREADS                    = "max_property_threads";
	public static final String TAG_PROPERTY_REQUESTS                   = "max_property_requests";
	public static final String TAG_PROPERTY_WEB_TIMEOUT                = "property_timeout";
	public static final String TAG_PROPERTY_WEB_RETRIES                = "property_retries";
	public static final int    PROPERTY_THREAD_LIMIT                   = 10;
	public static final int    PROPERTY_REQUEST_LIMIT                  = 100;
	// Personator
	public static final String TAG_PERSONATOR_THREADS                  = "personator_max_threads";
	public static final String TAG_PERSONATOR_REQUESTS                 = "personator_max_requests";
	public static final String TAG_PERSONATOR_WEB_TIMEOUT              = "personator_timeout";
	public static final String TAG_PERSONATOR_WEB_RETRIES              = "personator_retries";
	public static final int    PERSONATOR_THREAD_LIMIT                 = 10;
	public static final int    PERSONATOR_REQUEST_LIMIT                = 100;
	// Personator World
	public static final String TAG_PERSONATOR_WORLD_THREADS            = "personatorWorld_max_threads";
	public static final String TAG_PERSONATOR_WORLD_REQUESTS           = "personatorWorld_max_requests";
	public static final String TAG_PERSONATOR_WORLD_WEB_TIMEOUT        = "personatorWorld_timeout";
	public static final String TAG_PERSONATOR_WORLD_WEB_RETRIES        = "personatorWorld_retries";
	public static final int    PERSONATOR_WORLD_THREAD_LIMIT           = 10;
	public static final int    PERSONATOR_WORLD_REQUEST_LIMIT          = 100;
	// Business Coder
	public static final String TAG_BUSINESS_CODER_THREADS              = "business_coder_max_threads";
	public static final String TAG_BUSINESS_CODER_REQUESTS             = "business_coder_max_requests";
	public static final String TAG_BUSINESS_CODER_WEB_TIMEOUT          = "business_coder_timeout";
	public static final String TAG_BUSINESS_CODER_WEB_RETRIES          = "business_coder_retries";
	public static final int    BUSINESS_CODER_THREAD_LIMIT             = 10;
	public static final int    BUSINESS_CODER_REQUEST_LIMIT            = 1;
	public static final String TAG_WEB_OPT_ABORT                       = "opt_abort";
	// Local appliance tags
	public static final String TAG_CVS_SERVER_URL                      = "cvs_server_url";
	public static final String TAG_CVS_OPT_FAILOVER                    = "cvs_opt_failover";
	public static final String TAG_RETRY_APPLIANCE                     = "retry_appliance";
	public static final String TAG_FAILOVER_INTERVAL                   = "failover_interval";
	public static final String TAG_CVS_TIMEOUT                         = "cvs_timeout";
	public static final String TAG_CVS_RETRIES                         = "cvs_retries";
	public static final String TAG_CVS_OPT_ABORT                       = "cvs_opt_abort";
	public static final String TAG_WEB_ENCODING                        = "web_encoding";
	//
	public static final String TAG_MDCHECK_VERSION                     = "md_contactverify_version";

}
