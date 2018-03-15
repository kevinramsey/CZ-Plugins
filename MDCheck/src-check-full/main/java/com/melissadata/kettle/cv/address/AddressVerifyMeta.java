package com.melissadata.kettle.cv.address;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.request.LocalRequestHandler;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.report.ReportStats;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;

import com.melissadata.mdAddr;
import com.melissadata.mdZip;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class AddressVerifyMeta implements LocalRequestHandler, Cloneable {
	public enum Countries {
		US(BaseMessages.getString(PKG, "MDCheckMeta.Countries.US")),
		Canada(BaseMessages.getString(PKG, "MDCheckMeta.Countries.Canada")),
		USCanada(BaseMessages.getString(PKG, "MDCheckMeta.Countries.USCanada")), ;
		public static Countries decode(String value) throws KettleException {
			try {
				return Countries.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.Countries.Unknown") + value, e);
			}
		}
		private String	description;

		private Countries(String description) {
			this.description = description;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum DiacriticMode {
		AlwaysAdd(BaseMessages.getString(PKG, "MDCheckMeta.DiacriticMode.AlwaysAdd")),
		AlwaysStrip(BaseMessages.getString(PKG, "MDCheckMeta.DiacriticMode.AlwaysStrip")),
		Automatic(BaseMessages.getString(PKG, "MDCheckMeta.DiacriticMode.Automatic")), ;
		public static DiacriticMode decode(String value) throws KettleException {
			try {
				return DiacriticMode.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.DiacriticMode.Unknown") + value, e);
			}
		}
		private String	description;

		private DiacriticMode(String description) {
			this.description = description;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}
	private static Class<?>				PKG										= AddressVerifyMeta.class;
	private static final String			TAG_ADDRESS_VERIFY						= "address_verify";
	private static final String			TAG_OPT_COUNTRIES						= "opt_countries";
	private static final String			TAG_OPT_PERFORM_DPV						= "opt_perform_dpv";
	private static final String			TAG_OPT_PERFORM_LACSLINK				= "opt_perform_lacslink";
	private static final String			TAG_OPT_PERFORM_SUITELINK				= "opt_perform_suitelink";
	private static final String			TAG_OPT_PERFORM_ADDRPLUS				= "opt_perform_addrplus";
	private static final String			TAG_OPT_PERFORM_RBDI					= "opt_perform_rbdi";
	private static final String			TAG_OPT_USE_PREFERRED_CITY				= "opt_preferred_city";
	private static final String			TAG_OPT_ADDRESS_PARSED					= "opt_address_parsed";
	private static final String			TAG_OPT_DIACRITIC_MODE					= "opt_diacritic_mode";
	private static final String			TAG_INPUT_LAST_NAME						= "input_last_name";
	private static final String			TAG_INPUT_COMPANY						= "input_company";
	private static final String			TAG_INPUT_ADDRESS_LINE_1				= "input_address_line_1";
	private static final String			TAG_INPUT_ADDRESS_LINE_2				= "input_address_line_2";
	private static final String			TAG_INPUT_SUITE							= "input_suite";
	private static final String			TAG_INPUT_URBANIZATION					= "input_urbanization";
	private static final String			TAG_INPUT_CITY							= "input_city";
	private static final String			TAG_INPUT_STATE							= "input_state";
	private static final String			TAG_INPUT_ZIP							= "input_zip";
	private static final String			TAG_INPUT_PLUS4							= "input_plus4";
	private static final String			TAG_INPUT_DELIVERY_POINT				= "input_delivery_point";
	private static final String			TAG_INPUT_COUNTRY						= "input_country";
	private static final String			TAG_OUTPUT_ADDRESS_LINE_1				= "output_address_line_1";
	private static final String			TAG_OUTPUT_ADDRESS_LINE_2				= "output_address_line_2";
	private static final String			TAG_OUTPUT_SUITE						= "output_suite";
	private static final String			TAG_OUTPUT_URBANIZATION					= "output_urbanization";
	private static final String			TAG_OUTPUT_CITY							= "output_city";
	private static final String			TAG_OUTPUT_STATE						= "output_state";
	private static final String			TAG_OUTPUT_ZIP							= "output_zip";
	private static final String			TAG_OUTPUT_PLUS4						= "output_plus4";
	private static final String			TAG_OUTPUT_DP_AND_CHECK_DIGIT			= "output_dp_and_check_digit";
	private static final String			TAG_OUTPUT_PRIVATE_MAIL_BOX				= "output_private_mail_box";
	private static final String			TAG_OUTPUT_COUNTRY						= "output_country";
	private static final String			TAG_OUTPUT_ADDRESS_TYPE_CODE			= "output_address_type_code";
	private static final String			TAG_OUTPUT_ZIP_TYPE_CODE				= "output_zip_type_code";
	private static final String			TAG_OUTPUT_CARRIER_ROUTE				= "output_carrier_route";
	private static final String			TAG_OUTPUT_CITY_ABBREVIATION			= "output_city_abbreviation";
	private static final String			TAG_OUTPUT_CONGRESSIONAL_DISTRICT		= "output_congressional_district";
	private static final String			TAG_OUTPUT_COMPANY						= "output_company";
	private static final String			TAG_OUTPUT_STATE_NAME					= "output_state_name";
	private static final String			TAG_OUTPUT_ADDRESS_TYPE_DESCRIPTION		= "output_address_type_description";
	private static final String			TAG_OUTPUT_ZIP_TYPE_DESCRIPTION			= "output_zip_type_description";
	private static final String			TAG_OUTPUT_COUNTRY_NAME					= "output_country_name";
	private static final String			TAG_OUTPUT_MAK		        			= "output_mak";
	private static final String			TAG_OUTPUT_BASE_MAK					    = "output_base_mak";
	private static final String			TAG_OUTPUT_ADDRESS_KEY					= "output_address_key";
	private static final String			TAG_OUTPUT_CMRA							= "output_cmra";
	private static final String			TAG_OUTPUT_ELOT_NUMBER					= "output_elot_number";
	private static final String			TAG_OUTPUT_ELOT_ORDER					= "output_delivery_elot_order";
	private static final String			TAG_OUTPUT_DELIVERY_INDICATION			= "output_delivery_indication";
	private static final String			TAG_OUTPUT_PARSED_ADDRESS_RANGE			= "output_parsed_address_range";
	private static final String			TAG_OUTPUT_PARSED_PRE_DIRECTIONAL		= "output_parsed_pre_directional";
	private static final String			TAG_OUTPUT_PARSED_STREET_NAME			= "output_parsed_street_name";
	private static final String			TAG_OUTPUT_PARSED_SUFFIX				= "output_parsed_suffix";
	private static final String			TAG_OUTPUT_PARSED_POST_DIRECTIONAL		= "output_parsed_post_directional";
	private static final String			TAG_OUTPUT_PARSED_SUITE_NAME			= "output_parsed_suite_name";
	private static final String			TAG_OUTPUT_PARSED_SUITE_RANGE			= "output_parsed_suite_range";
	private static final String			TAG_OUTPUT_PARSED_PMB_NAME				= "output_parsed_pmb_name";
	private static final String			TAG_OUTPUT_PARSED_PMB_RANGE				= "output_parsed_pmb_range";
	private static final String			TAG_OUTPUT_PARSED_ROUTE_SERVICE			= "output_parsed_route_service";
	private static final String			TAG_OUTPUT_PARSED_LOCK_BOX				= "output_parsed_lock_box";
	private static final String			TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION	= "output_parsed_delivery_installation";
	private static final String			TAG_OUTPUT_PARSED_EXTRA_INFORMATION		= "output_parsed_extra_information";
	private static final String			TAG_OUTPUT_COUNTY_NAME					= "output_county_name";
	private static final String			TAG_OUTPUT_COUNTY_FIPS					= "output_county_fips";
	private static final String			TAG_OUTPUT_TIMEZONE						= "output_timezone";
	private static final String			TAG_OUTPUT_TIMEZONE_CODE				= "output_county_timezone_code";
	private static final String			TAG_CASS_SAVE_TO_FILE					= "cass_save_to_file";
	private static final String			TAG_CASS_PROCESSOR_NAME					= "cass_processor_name";
	private static final String			TAG_CASS_LIST_NAME						= "cass_list_name";
	private static final String			TAG_CASS_NAME							= "cass_name";
	private static final String			TAG_CASS_COMPANY						= "cass_company";
	private static final String			TAG_CASS_ADDRESS						= "cass_address";
	private static final String			TAG_CASS_CITY							= "cass_city";
	private static final String			TAG_CASS_STATE							= "cass_state";
	private static final String			TAG_CASS_ZIP							= "cass_zip";
	private static final String			TAG_CASS_FILENAME						= "cass_filename";
	private static final String			TAG_SOA_SAVE_TO_FILE					= "soa_save_to_file";
	private static final String			TAG_SOA_PROCESSOR_NAME					= "soa_processor_name";
	private static final String			TAG_SOA_ADDRESS							= "soa_address";
	private static final String			TAG_SOA_CITY							= "soa_city";
	private static final String			TAG_SOA_PROVINCE						= "soa_province";
	private static final String			TAG_SOA_POSTAL_CODE						= "soa_postal_code";
	private static final String			TAG_SOA_FILENAME						= "soa_filename";
	// Special address verify last name field values
	public static final String			NAME_PARSER_LAST_NAME1					= "[NameParserLastName1]";
	// define the size outputs for various fields
	public static final String			NAME_PARSER_LAST_NAME2					= "[NameParserLastName2]";
	public static final String			NAME_PARSER_COMPANY						= "[NameParserCompany]";
	private static final int			MD_SIZE_ADDRESS							= 50;
	private static final int			MD_SIZE_ADDRESS2						= 50;
	private static final int			MD_SIZE_SUITE							= 20;
	private static final int			MD_SIZE_URBANIZATION					= 15;
	private static final int			MD_SIZE_CITY							= 35;
	private static final int			MD_SIZE_STATE							= 15;
	private static final int			MD_SIZE_ZIP								= 10;
	private static final int			MD_SIZE_PLUS4							= 4;
	private static final int			MD_SIZE_PRIVATE_MAILBOX					= 15;
	private static final int			MD_SIZE_COUNTRY_CODE					= 10;
	private static final int			MD_SIZE_ADDRESS_TYPE					= 1;
	private static final int			MD_SIZE_ADDRESS_TYPE_STRING				= 30;
	private static final int			MD_SIZE_ZIP_TYPE						= 1;
	private static final int			MD_SIZE_CRRT							= 4;
	private static final int			MD_SIZE_CMRA							= 1;
	private static final int			MD_SIZE_DP								= 3;
	private static final int			MD_SIZE_ELOT_NO							= 4;
	private static final int			MD_SIZE_ELOT_ORDER						= 1;
	private static final int			MD_SIZE_CITY_ABBREVIATION				= 13;
	private static final int			MD_SIZE_COUNTY							= 25;
	private static final int			MD_SIZE_COUNTY_FIPS						= 5;
	private static final int			MD_SIZE_CONGRESSIONAL_DISTRICT			= 2;
	private static final int			MD_SIZE_TIMEZONE						= 20;
	private static final int			MD_SIZE_TIMEZONE_CODE					= 2;
	private static final int			MD_SIZE_PARSEDRANGE						= 10;
	private static final int			MD_SIZE_PARSEDPREDIR					= 5;
	private static final int			MD_SIZE_PARSEDNAME						= 40;
	private static final int			MD_SIZE_PARSEDSUFFIX					= 10;
	private static final int			MD_SIZE_PARSEDPOSTDIR					= 5;
	private static final int			MD_SIZE_PARSEDSUITENAME					= 10;
	private static final int			MD_SIZE_PARSEDSUITERANGE				= 10;
	private static final int			MD_SIZE_PARSEDPRIVATE_MAILBOX_NAME		= 10;
	private static final int			MD_SIZE_PARSEDPRIVATE_MAILBOX_RANGE		= 10;
	private static final int			MD_SIZE_PARSEDROUTE_SERVICE				= 20;
	private static final int			MD_SIZE_PARSEDLOCKBOX					= 20;
	private static final int			MD_SIZE_PARSEDDELIVERY_INSTALLATION		= 20;
	private static final int			MD_SIZE_PARSEDGARBAGE					= 30;
	private static final int			MD_SIZE_ADDRESSKEY						= 11;
	private static final int			MD_SIZE_MAK		        				= 11;
	private static final int			MD_SIZE_BASE_MAK						= 11;
	private static final int			MD_SIZE_DEFAULT_EXTRA					= 50;
	private MDCheckStepData data;
	private boolean         initializeOK;
	private String          initializeWarn;
	private String          initializeError;
	private String          initializeCaWarn;
	private String          initializeCaError;
	private String          initializeZipWarn;
	private String          initializeZipError;
	private Countries       optCountries;
	private boolean         optPerformDPV;
	private boolean         optPerformLACSLink;
	private boolean         optPerformSuiteLink;
	private boolean         optPerformAddrPlus;
	private boolean         optPerformRBDI;
	private boolean						optUsePreferredCity;
	private boolean						optAddressParsed;
	private DiacriticMode				optDiacriticMode;
	private String						inputLastName;
	private String						inputCompany;
	private String						inputAddressLine1;
	private String						inputAddressLine2;
	private String						inputSuite;
	private String						inputUrbanization;
	private String						inputCity;
	private String						inputState;
	private String						inputZip;
	private String						inputPlus4;
	private String						inputDeliveryPoint;
	private String						inputCountry;
	private String						outputAddressLine1;
	private String						outputAddressLine2;
	private String						outputSuite;
	private String						outputUrbanization;
	private String						outputCity;
	private String						outputState;
	private String						outputZip;
	private String						outputPlus4;
	private String						outputDPAndCheckDigit;
	private String						outputPrivateMailBox;
	private String						outputCountry;
	private String						outputAddressTypeCode;
	private String						outputZipTypeCode;
	private String						outputCarrierRoute;
	private String						outputCityAbbreviation;
	private String						outputCongressionalDistrict;
	private String						outputCompany;
	private String						outputStateName;
	private String						outputAddressTypeDescription;
	private String						outputZipTypeDescription;
	private String						outputCountryName;
	private String						outputAddressKey;
	private String						outputMAK;
	private String						outputBaseMAK;
	private String						outputCMRA;
	private String						outputElotNumber;
	private String						outputElotOrder;
	private String						outputDeliveryIndication;
	private String						outputParsedAddressRange;
	private String						outputParsedPreDirectional;
	private String						outputParsedStreetName;
	private String						outputParsedSuffix;
	private String						outputParsedPostDirectional;
	private String						outputParsedSuiteName;
	private String						outputParsedSuiteRange;
	private String						outputParsedPMBName;
	private String						outputParsedPMBRange;
	private String						outputParsedRouteService;
	private String						outputParsedLockBox;
	private String						outputParsedDeliveryInstallation;
	private String						outputParsedExtraInformation;
	private String						outputCountyName;
	private String						outputCountyFips;
	private String						outputTimezone;
	private String						outputTimezoneCode;
	private boolean						cassSaveToFile;
	private String						cassProcessorName;
	private String						cassListName;
	private String						cassName;
	private String						cassCompany;
	private String						cassAddress;
	private String						cassCity;
	private String						cassState;
	private String						cassZip;
	private String						cassFilename;
	private boolean						soaSaveToFile;
	private String						soaProcessorName;
	private String						soaAddress;
	private String						soaCity;
	private String						soaProvince;
	private String						soaPostalCode;
	private String						soaFilename;
	private int							fieldsAdded;
	// Info set during processing
	public String						localMsg								= "";
	public String						localDBDate								= "";
	public String						localDBExpiration						= "";
	public String						localRBDIDate							= "";
	public String						localCDBDate							= "";
	public String						localCDBExpiration						= "";
	public String						localDBBuildNo							= "";
	public String						webMsg									= "";
	public String						webVersion								= "";
	private static final String[]		CHANGECODES								= { "AS01", "AS02", "AS03", "AS09", "AS10", "AS12", "AS13", "AS14", "AS15", "AS16", "AS17", "AS18", "AS20", "AS22", "AS23", "AC01", "AC02", "AC03", "AC04", "AC05",
		"AC06", "AC07", "AC08", "AC09", "AC10", "AC11", "AC12", "AC13", "AC14", "AC15", "AC16", "AC17", "AC18", "AC19", "AC20" };
	private static final String[]		ERRORCODES								= { "AE01", "AE02", "AE03", "AE04", "AE05", "AE07", "AE08", "AE09", "AE10", "AE11", "AE12", "AE13", "AE14", "AE17", "AE19", "AE20" };
	private static final Set<String>	changeCodes								= new HashSet<String>();
	private static final Set<String>	errorCodes								= new HashSet<String>();
	static {
		Collections.addAll(changeCodes, CHANGECODES);
		Collections.addAll(errorCodes, ERRORCODES);
	}
	// Exceptions detected during processing
	public KettleException				localException;
	public KettleException				webException;

	public AddressVerifyMeta(MDCheckStepData data) {
		this.data = data;
		setDefault();
	}

	/**
	 * Checks the settings of this step and puts the findings in a remarks List.
	 *
	 * @param remarks
	 *            The list to put the remarks in @see org.pentaho.di.core.CheckResult
	 * @param stepMeta
	 *            The stepMeta to help checking
	 * @param prev
	 *            The fields coming from the previous step
	 * @param input
	 *            The input step names
	 * @param output
	 *            The output step names
	 * @param info
	 *            The fields that are used as information by the step
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// Handled in Valiation
	}

	/**
	 * Called to validate initialization of the address object
	 *
	 * @param checker
	 */
	public void checkInit(Validations checker) {
		boolean initialOK = true;
		mdAddr Addr = null;
		try {
			// Create temporary address object
			Addr = DQTObjectFactory.newAddr();
			// Configure it with license string
			AdvancedConfigurationMeta advConfig = data.getAdvancedConfiguration();
			if (!Addr.SetLicenseString(getLicense())) {
				checker.errors.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.SetLicense"));
			}
			// Get configuration options
			boolean isUSLicensed = (advConfig.getProducts() & AdvancedConfigurationMeta.MDLICENSE_Address) != 0 || AdvancedConfigurationMeta.isCommunity();
			boolean isCanadaLicensed = (advConfig.getProducts() & AdvancedConfigurationMeta.MDLICENSE_Canada) != 0;
			boolean isRBDILicensed = (advConfig.getProducts() & AdvancedConfigurationMeta.MDLICENSE_RBDI) != 0;
			// Set path to US and Canadian data
			String localDataPath = advConfig.getLocalDataPath();

			if ((AdvancedConfigurationMeta.getProducts(MDPropTags.TAG_PRIMARY_LICENSE) & MDPropTags.MDLICENSE_Address) != 0) {
				// we are fully licensed
				if (isUSLicensed) {
					Addr.SetPathToUSFiles(localDataPath);
					Addr.SetPathToAddrKeyDataFiles(localDataPath);
				}
				if (isCanadaLicensed) {
					Addr.SetPathToCanadaFiles(localDataPath);
				}
				// Set other paths
				Addr.SetPathToDPVDataFiles(localDataPath);
				Addr.SetPathToLACSLinkDataFiles(localDataPath);
				Addr.SetPathToSuiteLinkDataFiles(localDataPath);
				if (isRBDILicensed) {
					Addr.SetPathToRBDIFiles(localDataPath);
				}
				Addr.SetPathToSuiteFinderDataFiles(localDataPath);
				// If a CASS form is to be generated then turn on CASS now
				Addr.SetCASSEnable(1);
			} else if(AdvancedConfigurationMeta.isCommunity()){
				Addr.SetPathToUSFiles(localDataPath);
			}

			Addr.InitializeDataFiles();
			// Reset the initialization fields
			initializeError = "";
			initializeWarn = "";
			initializeCaError = "";
			initializeCaWarn = "";
			// Check for initialization errors
			if (!Addr.GetInitializeErrorString().equalsIgnoreCase("No error.")) {
				boolean ignore = false;
				if(Addr.GetInitializeErrorString().contains("The Canadian database has expired")){
					ignore = true;
				}
				if (isEnabled() && !ignore) {
					checker.errors.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.AddrObject") + Addr.GetInitializeErrorString());
				} else {
					checker.warnings.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.AddrObject") + Addr.GetInitializeErrorString());
				}
				if (checker.checkTimeStamp(checker.getAddressTimeStamp())) {
					initializeError = BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.AddrObject") + Addr.GetInitializeErrorString();
					checker.setAddressTimeStamp(checker.getTodayStamp());
					checker.showErrors = true;
					checker.somethingToShow = true;
				}
				initialOK = false && ignore;;
			} else if (Validations.isDataExpiring(Addr.GetExpirationDate())) {
				checker.warnings.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.DataExpiring") + Addr.GetExpirationDate());
				if (checker.checkTimeStamp(checker.getAddressTimeStamp())) {
					initializeWarn = BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.DataExpiring") + Addr.GetExpirationDate();
					checker.setAddressTimeStamp(checker.getTodayStamp());
					checker.showWarnings = true;
					checker.somethingToShow = true;
				}
			}
			if (isCanadaLicensed()) {
				if (Validations.isDataExpiring(Addr.GetCanadianExpirationDate())) {
					if (Validations.dateExpired(Addr.GetCanadianExpirationDate())) {
						checker.errors.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.CaDataExpired"));
						if (checker.checkTimeStamp(checker.getCaAddressTimeStamp())) {
							initializeCaError = BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.CaDataExpired");
							checker.somethingToShow = true;
							checker.showErrors = true;
							checker.setCaAddressTimeStamp(checker.getTodayStamp());
						}
					} else {
						checker.warnings.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.CaDataExpiring") + Addr.GetCanadianExpirationDate());
						if (checker.checkTimeStamp(checker.getCaAddressTimeStamp())) {
							initializeCaWarn = BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.CaDataExpiring") + Addr.GetCanadianExpirationDate();
							checker.somethingToShow = true;
							checker.showWarnings = true;
							checker.setCaAddressTimeStamp(checker.getTodayStamp());
						}
					}
				}
			}
			initializeOK = initialOK;
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			initializeError = BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.AddrObject") + t.getLocalizedMessage();
			checker.setAddressTimeStamp(checker.getTodayStamp());
			checker.showErrors = true;
			checker.somethingToShow = true;
		} finally {
			if (Addr != null) {
				Addr.delete();
			}
		}
	}

	/**
	 * Called to validate initialziation of the zip object
	 *
	 * @param checker
	 */
	public void checkZipInit(Validations checker) {
		mdZip Zip = null;
		try {
			// Create a temporary ZIP object
			Zip = DQTObjectFactory.newZip();
			// Configure it with a license string
			AdvancedConfigurationMeta advConfig = data.getAdvancedConfiguration();
			String dataPath = advConfig.getLocalDataPath();
			if (!Zip.SetLicenseString(getLicense())) {
				checker.errors.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.SetLicenseZip"));
				// showErrors = true;
				// somethingToShow = true;
			} else if (Zip.Initialize(dataPath, dataPath, dataPath) != mdZip.ProgramStatus.ErrorNone) {
				if (checker.checkTimeStamp(checker.getAddressTimeStamp())) {
					checker.errors.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.ZipObject") + MDCheck.getErrorString("InitializeService", Zip.GetInitializeErrorString()));
					// showErrors = true;
				}
			}
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			checker.errors.add(BaseMessages.getString(PKG, "MDCheckMeta.AddressMeta.Error.ZipObject") + MDCheck.getErrorString("InitializeService", t.getLocalizedMessage()));
		} finally {
			if (Zip != null) {
				Zip.delete();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AddressVerifyMeta clone() throws CloneNotSupportedException {
		return (AddressVerifyMeta) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#doLocalRequest(com.melissadata.kettle.MDCheckData)
	 */
	public synchronized void doLocalRequests(MDCheckData data, List<MDCheckRequest> requests) throws KettleValueException {
		// Skip if not enabled
		if (!isEnabled()) { return; }
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Get the single request
			RowMetaInterface inputMeta = request.inputMeta;
			Object[] inputData = request.inputData;
			// Create the address results
			MDCheckCVRequest.AddrResults addrResults = request.addrResults = new MDCheckCVRequest.AddrResults();
			// TODO: More complete validity checks
			boolean valid = true;
			// Get reference to address object
			mdAddr Addr = data.Addr;
			// Perform request if it's input is valid
			if (valid) {
				// Initialize address object
				Addr.ClearProperties();
				// Set request fields
				if (!Const.isEmpty(inputLastName)) {
					// Optionally get last name from name parser results
					String lastname = null;
					if (inputLastName.equals(NAME_PARSER_LAST_NAME1)) {
						lastname = (request.nameResults != null) ? request.nameResults.Last : null;
					} else if (inputLastName.equals(NAME_PARSER_LAST_NAME2)) {
						lastname = (request.nameResults != null) ? request.nameResults.Last2 : null;
					} else {
						lastname = inputMeta.getString(inputData, inputLastName, "");
					}
					if (!Const.isEmpty(lastname)) {
						Addr.SetLastName(lastname);
					}
				}
				if (!Const.isEmpty(inputCompany)) {
					// Optionally get last name from name parser results
					String company = null;
					if (inputCompany.equals(NAME_PARSER_COMPANY)) {
						company = (request.nameResults != null) ? request.nameResults.StandardCompanyName : null;
					} else {
						company = inputMeta.getString(inputData, inputCompany, "");
					}
					if (!Const.isEmpty(company)) {
						Addr.SetLastName(company);
					}
				}
				// String value = MDCheck.getFieldString(inputMeta, inputData, inputCompany);
				// if (!Const.isEmpty(value))
				// Addr.SetCompany(value);
				String value = MDCheck.getFieldString(inputMeta, inputData, inputAddressLine1);
				if (!Const.isEmpty(value)) {
					Addr.SetAddress(value);
				}
				value = MDCheck.getFieldString(inputMeta, inputData, inputAddressLine2);
				if (!Const.isEmpty(value)) {
					Addr.SetAddress2(value);
				}
				value = MDCheck.getFieldString(inputMeta, inputData, inputSuite);
				if (!Const.isEmpty(value)) {
					Addr.SetSuite(value);
				}
				value = MDCheck.getFieldString(inputMeta, inputData, inputUrbanization);
				if (!Const.isEmpty(value)) {
					Addr.SetUrbanization(value);
				}
				value = MDCheck.getFieldString(inputMeta, inputData, inputCity);
				if (!Const.isEmpty(value)) {
					Addr.SetCity(value);
				}
				value = MDCheck.getFieldString(inputMeta, inputData, inputState);
				if (!Const.isEmpty(value)) {
					Addr.SetState(value);
				}
				if (!Const.isEmpty(inputZip)) {
					String zip = inputMeta.getString(inputData, inputZip, "");
					if (!Const.isEmpty(zip)) {
						// Include Plus4 and delivery point with ZIP
						if (!Const.isEmpty(inputPlus4)) {
							String plus4 = inputMeta.getString(inputData, inputPlus4, "");
							if (!Const.isEmpty(plus4)) {
								zip += plus4;
								if (!Const.isEmpty(inputDeliveryPoint)) {
									String deliveryPoint = inputMeta.getString(inputData, inputDeliveryPoint, "");
									if (!Const.isEmpty(deliveryPoint)) {
										zip += deliveryPoint;
									}
								}
							}
						}
						Addr.SetZip(zip);
					}
				}
				value = MDCheck.getFieldString(inputMeta, inputData, inputCountry);
				if (!Const.isEmpty(value)) {
					Addr.SetCountryCode(value);
				}
				// Perform the request
				Addr.VerifyAddress();
				// Always get the result codes for the request
				addrResults.resultCodes.addAll(MDCheck.getResultCodes(Addr.GetResults()));
				// Filter out result codes that don't make sense for the given options
				/*
				 * if (!optPerformSuiteLink)
				 * addrResults.resultCodes.remove("AS14");
				 * if (!optPerformAddrPlus)
				 * addrResults.resultCodes.remove("AS15");
				 */
			}
			// If request was valid then get result fields
			if (valid) {
				addrResults.Address1 = Addr.GetAddress();
				addrResults.Address2 = Addr.GetAddress2();
				addrResults.PrivateMailBox = Addr.GetPrivateMailbox();
				// If SuiteLINK or Addr+ enabled then get the suite from the object, otherwise copy input to output
				// if (optPerformSuiteLink || optPerformAddrPlus)
				addrResults.Suite = Addr.GetSuite();
				// else
				// addrResults.Suite = MDCheck.getFieldString(inputMeta, inputData, inputSuite);
				addrResults.UrbanizationName = Addr.GetUrbanization();
				addrResults.CityName = Addr.GetCity();
				addrResults.StateAbbreviation = Addr.GetState();
				addrResults.Zip = Addr.GetZip();
				addrResults.Plus4 = Addr.GetPlus4();
				addrResults.DeliveryPointCode = Addr.GetDeliveryPointCode();
				addrResults.DeliveryPointCheckDigit = Addr.GetDeliveryPointCheckDigit();
				addrResults.CountryAbbreviation = Addr.GetCountryCode();
				addrResults.CountryName = addrResults.CountryAbbreviation; // AO does not have country name
				addrResults.CMRA = Addr.GetCMRA();
				addrResults.ELotNumber = Addr.GetELotNumber();
				addrResults.ELotOrder = Addr.GetELotOrder();
				// If RBDI is selected and enabled then get delivery indicator from object. Otherwise return nothing
				if (optPerformRBDI && (optCountries != Countries.Canada) && isRBDILicensed()) {
					addrResults.RBDI = Addr.GetRBDI();
				} else {
					addrResults.RBDI = "";
				}
				addrResults.TypeAddressCode = Addr.GetAddressTypeCode();
				addrResults.TypeAddressDescription = Addr.GetAddressTypeString();
				addrResults.TypeZipCode = Addr.GetZipType();
				addrResults.TypeZipDescription = ""; // AO does not have zip description
				addrResults.CarrierRoute = Addr.GetCarrierRoute();
				addrResults.CityAbbreviation = Addr.GetCityAbbreviation();
				addrResults.CongressionalDistrict = Addr.GetCongressionalDistrict();
				addrResults.Company = Addr.GetCompany();
				addrResults.StateName = addrResults.StateAbbreviation; // AO does not have state name
				addrResults.ParsedAddressRange = Addr.GetParsedAddressRange();
				addrResults.ParsedDirectionPre = Addr.GetParsedPreDirection();
				addrResults.ParsedStreetName = Addr.GetParsedStreetName();
				addrResults.ParsedSuffix = Addr.GetParsedSuffix();
				addrResults.ParsedDirectionPost = Addr.GetParsedPostDirection();
				// If SuiteLINK or Addr+ enabled then get the parsed suite name and range from the object. Otherwise leave
// blank.
				// if (optPerformSuiteLink || optPerformAddrPlus) {
				addrResults.ParsedSuiteName = Addr.GetParsedSuiteName();
				addrResults.ParsedSuiteRange = Addr.GetParsedSuiteRange();
				// } else {
				// addrResults.ParsedSuiteName = "";
				// addrResults.ParsedSuiteRange = "";
				// }
				addrResults.ParsedPMBName = Addr.GetParsedPrivateMailboxName();
				addrResults.ParsedPMBRange = Addr.GetParsedPrivateMailboxNumber();
				addrResults.ParsedRouteService = Addr.GetParsedRouteService();
				addrResults.ParsedLockBox = Addr.GetParsedLockBox();
				addrResults.ParsedDeliveryInstallation = Addr.GetParsedDeliveryInstallation();
				addrResults.ParsedExtraInformation = Addr.GetParsedGarbage();
				addrResults.CountyName = Addr.GetCountyName();
				addrResults.CountyFips = Addr.GetCountyFips();
				addrResults.TimeZone = Addr.GetTimeZone();
				addrResults.TimeZoneCode = Addr.GetTimeZoneCode();
				addrResults.AddressKey = Addr.GetAddressKey();
				addrResults.MAK = Addr.GetMelissaAddressKey();
				addrResults.BaseMAK = Addr.GetMelissaAddressKeyBase();
				// TODO: Do more complete validity checks?
				addrResults.valid = true;
			}
		}
	}

	public String getCASSAddress() {
		return cassAddress;
	}

	public String getCASSCity() {
		return cassCity;
	}

	public String getCASSCompany() {
		return cassCompany;
	}

	public String getCASSFilename() {
		return cassFilename;
	}

	public String getCASSListName() {
		return cassListName;
	}

	public String getCASSName() {
		return cassName;
	}

	public String getCASSProcessorName() {
		return cassProcessorName;
	}

	public boolean getCASSSaveToFile() {
		return cassSaveToFile;
	}

	public String getCASSState() {
		return cassState;
	}

	public String getCASSZip() {
		return cassZip;
	}

	/**
	 * @return The customer id for address verification
	 */
	public String getCustomerID() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String customerID = acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_Address);
		return customerID;
	}

	/**
	 * Called to determine the address verifier output fields that will be included in the step outout record
	 *
	 * NOTE: Order of fields must match the order of fields in processResponses
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {
		int start = row.size();
		// There are no output fields if it is not enabled
		if (isEnabled()) {
			// General output fields
			if (!Const.isEmpty(outputAddressLine1)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddressLine1, MD_SIZE_ADDRESS);
			}
			if (!Const.isEmpty(outputAddressLine2)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddressLine2, MD_SIZE_ADDRESS2);
			}
			if (!Const.isEmpty(outputSuite)) {
				MDCheckMeta.getStringField(row, originName, space, outputSuite, MD_SIZE_SUITE);
			}
			if (!Const.isEmpty(outputUrbanization)) {
				MDCheckMeta.getStringField(row, originName, space, outputUrbanization, MD_SIZE_URBANIZATION);
			}
			if (!Const.isEmpty(outputCity)) {
				MDCheckMeta.getStringField(row, originName, space, outputCity, MD_SIZE_CITY);
			}
			if (!Const.isEmpty(outputState)) {
				MDCheckMeta.getStringField(row, originName, space, outputState, MD_SIZE_STATE);
			}
			if (!Const.isEmpty(outputZip)) {
				MDCheckMeta.getStringField(row, originName, space, outputZip, MD_SIZE_ZIP);
			}
			if (!Const.isEmpty(outputPlus4)) {
				MDCheckMeta.getStringField(row, originName, space, outputPlus4, MD_SIZE_PLUS4);
			}
			if (!Const.isEmpty(outputDPAndCheckDigit)) {
				MDCheckMeta.getStringField(row, originName, space, outputDPAndCheckDigit, MD_SIZE_DP);
			}
			if (!Const.isEmpty(outputPrivateMailBox)) {
				MDCheckMeta.getStringField(row, originName, space, outputPrivateMailBox, MD_SIZE_PRIVATE_MAILBOX);
			}
			if (!Const.isEmpty(outputCountry)) {
				MDCheckMeta.getStringField(row, originName, space, outputCountry, MD_SIZE_COUNTRY_CODE);
			}
			if (!Const.isEmpty(outputCMRA)) {
				MDCheckMeta.getStringField(row, originName, space, outputCMRA, MD_SIZE_CMRA);
			}
			if (!Const.isEmpty(outputElotNumber)) {
				MDCheckMeta.getStringField(row, originName, space, outputElotNumber, MD_SIZE_ELOT_NO);
			}
			if (!Const.isEmpty(outputElotOrder)) {
				MDCheckMeta.getStringField(row, originName, space, outputElotOrder, MD_SIZE_ELOT_ORDER);
			}
			if (!Const.isEmpty(outputDeliveryIndication)) {
				MDCheckMeta.getStringField(row, originName, space, outputDeliveryIndication, MD_SIZE_PARSEDDELIVERY_INSTALLATION);
			}
			// Additional info fields
			if (!Const.isEmpty(outputAddressTypeCode)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddressTypeCode, MD_SIZE_ADDRESS_TYPE);
			}
			if (!Const.isEmpty(outputAddressTypeDescription)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddressTypeDescription, MD_SIZE_ADDRESS_TYPE_STRING);
			}
			if (!Const.isEmpty(outputZipTypeCode)) {
				MDCheckMeta.getStringField(row, originName, space, outputZipTypeCode, MD_SIZE_ZIP_TYPE);
			}
			if (!Const.isEmpty(outputCarrierRoute)) {
				MDCheckMeta.getStringField(row, originName, space, outputCarrierRoute, MD_SIZE_CRRT);
			}
			if (!Const.isEmpty(outputCityAbbreviation)) {
				MDCheckMeta.getStringField(row, originName, space, outputCityAbbreviation, MD_SIZE_CITY_ABBREVIATION);
			}
			if (!Const.isEmpty(outputCongressionalDistrict)) {
				MDCheckMeta.getStringField(row, originName, space, outputCongressionalDistrict, MD_SIZE_CONGRESSIONAL_DISTRICT);
			}
			// extra
			if (false) {
				if (!Const.isEmpty(outputCompany)) {
					MDCheckMeta.getStringField(row, originName, space, outputCompany, MD_SIZE_DEFAULT_EXTRA);
				}
				if (!Const.isEmpty(outputStateName)) {
					MDCheckMeta.getStringField(row, originName, space, outputStateName, MD_SIZE_DEFAULT_EXTRA);
				}
				if (!Const.isEmpty(outputZipTypeCode)) {
					MDCheckMeta.getStringField(row, originName, space, outputZipTypeCode, MD_SIZE_DEFAULT_EXTRA);
				}
				if (!Const.isEmpty(outputCountryName)) {
					MDCheckMeta.getStringField(row, originName, space, outputCountryName, MD_SIZE_DEFAULT_EXTRA);
				}
			}
			// Parsed address fields
			if (!Const.isEmpty(outputParsedAddressRange)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedAddressRange, MD_SIZE_PARSEDRANGE);
			}
			if (!Const.isEmpty(outputParsedPreDirectional)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedPreDirectional, MD_SIZE_PARSEDPREDIR);
			}
			if (!Const.isEmpty(outputParsedStreetName)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedStreetName, MD_SIZE_PARSEDNAME);
			}
			if (!Const.isEmpty(outputParsedSuffix)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedSuffix, MD_SIZE_PARSEDSUFFIX);
			}
			if (!Const.isEmpty(outputParsedPostDirectional)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedPostDirectional, MD_SIZE_PARSEDPOSTDIR);
			}
			if (!Const.isEmpty(outputParsedSuiteName)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedSuiteName, MD_SIZE_PARSEDSUITENAME);
			}
			if (!Const.isEmpty(outputParsedSuiteRange)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedSuiteRange, MD_SIZE_PARSEDSUITERANGE);
			}
			if (!Const.isEmpty(outputParsedPMBName)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedPMBName, MD_SIZE_PARSEDPRIVATE_MAILBOX_NAME);
			}
			if (!Const.isEmpty(outputParsedPMBRange)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedPMBRange, MD_SIZE_PARSEDPRIVATE_MAILBOX_RANGE);
			}
			if (!Const.isEmpty(outputParsedRouteService)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedRouteService, MD_SIZE_PARSEDROUTE_SERVICE);
			}
			if (!Const.isEmpty(outputParsedLockBox)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedLockBox, MD_SIZE_PARSEDLOCKBOX);
			}
			if (!Const.isEmpty(outputParsedDeliveryInstallation)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedDeliveryInstallation, MD_SIZE_PARSEDDELIVERY_INSTALLATION);
			}
			if (!Const.isEmpty(outputParsedExtraInformation)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedExtraInformation, MD_SIZE_PARSEDGARBAGE);
			}
			if (!Const.isEmpty(outputCountyName)) {
				MDCheckMeta.getStringField(row, originName, space, outputCountyName, MD_SIZE_COUNTY);
			}
			if (!Const.isEmpty(outputCountyFips)) {
				MDCheckMeta.getStringField(row, originName, space, outputCountyFips, MD_SIZE_COUNTY_FIPS);
			}
			if (!Const.isEmpty(outputTimezone)) {
				MDCheckMeta.getStringField(row, originName, space, outputTimezone, MD_SIZE_TIMEZONE);
			}
			if (!Const.isEmpty(outputTimezoneCode)) {
				MDCheckMeta.getStringField(row, originName, space, outputTimezoneCode, MD_SIZE_TIMEZONE_CODE);
			}
			if (!Const.isEmpty(outputAddressKey)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddressKey, MD_SIZE_ADDRESSKEY);
			}

			if (!Const.isEmpty(outputMAK)) {
				MDCheckMeta.getStringField(row, originName, space, outputMAK, MD_SIZE_MAK);
			}
			if (!Const.isEmpty(outputBaseMAK)) {
				MDCheckMeta.getStringField(row, originName, space, outputBaseMAK, MD_SIZE_BASE_MAK);
			}
		}
		// Keep a count of the number of fields we add
		fieldsAdded = row.size() - start;
	}

	public String getInitializeCaError() {
		return initializeCaError;
	}

	public String getInitializeCaWarn() {
		return initializeCaWarn;
	}

	public String getInitializeError() {
		return initializeError;
	}

	public String getInitializeWarn() {
		return initializeWarn;
	}

	public String getInitializeZipError() {
		return initializeZipError;
	}

	public String getInitializeZipWarn() {
		return initializeZipWarn;
	}

	public String getInputAddressLine1() {
		return inputAddressLine1;
	}

	public String getInputAddressLine2() {
		return inputAddressLine2;
	}

	public String getInputCity() {
		return inputCity;
	}

	public String getInputCompany() {
		return inputCompany;
	}

	public String getInputCountry() {
		return inputCountry;
	}

	public String getInputDeliveryPoint() {
		return inputDeliveryPoint;
	}

	public String getInputLastName() {
		return inputLastName;
	}

	public String getInputPlus4() {
		return inputPlus4;
	}

	public String getInputState() {
		return inputState;
	}

	public String getInputSuite() {
		return inputSuite;
	}

	public String getInputUrbanization() {
		return inputUrbanization;
	}

	public String getInputZip() {
		return inputZip;
	}

	/**
	 * @return The license string for address verification
	 */
	public String getLicense() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String license = acMeta.getProdutLicense(AdvancedConfigurationMeta.MDLICENSE_Address);
		return license;
	}

	public boolean getOptionAddressParsed() {
		return optAddressParsed;
	}

	public Countries getOptionCountries() {
		return optCountries;
	}

	public DiacriticMode getOptionDiacriticMode() {
		return optDiacriticMode;
	}

	public boolean getOptionPerformAddrPlus() {
		return optPerformAddrPlus;
	}

	public boolean getOptionPerformDPV() {
		return optPerformDPV;
	}

	public boolean getOptionPerformLACSLink() {
		return optPerformLACSLink;
	}

	public boolean getOptionPerformRBDI() {
		return optPerformRBDI;
	}

	public boolean getOptionPerformSuiteLink() {
		return optPerformSuiteLink;
	}

	public boolean getOptionUsePreferredCity() {
		return optUsePreferredCity;
	}

	public String getOutputAddressKey() {
		return outputAddressKey;
	}

	public String getOutputMAK() {
		return outputMAK;
	}
	public String getOutputBaseMAK() {
		return outputBaseMAK;
	}

	public String getOutputAddressLine1() {
		return outputAddressLine1;
	}

	public String getOutputAddressLine2() {
		return outputAddressLine2;
	}

	public String getOutputAddressTypeCode() {
		return outputAddressTypeCode;
	}

	public String getOutputAddressTypeDescription() {
		return outputAddressTypeDescription;
	}

	public String getOutputCarrierRoute() {
		return outputCarrierRoute;
	}

	public String getOutputCity() {
		return outputCity;
	}

	public String getOutputCityAbbreviation() {
		return outputCityAbbreviation;
	}

	public String getOutputCMRA() {
		return outputCMRA;
	}

	public String getOutputCompany() {
		return outputCompany;
	}

	public String getOutputCongressionalDistrict() {
		return outputCongressionalDistrict;
	}

	public String getOutputCountry() {
		return outputCountry;
	}

	public String getOutputCountryName() {
		return outputCountryName;
	}

	public String getOutputCountyFips() {
		return outputCountyFips;
	}

	public String getOutputCountyName() {
		return outputCountyName;
	}

	public String getOutputDeliveryIndication() {
		return outputDeliveryIndication;
	}

	public String getOutputDPAndCheckDigit() {
		return outputDPAndCheckDigit;
	}

	public String getOutputElotNumber() {
		return outputElotNumber;
	}

	public String getOutputElotOrder() {
		return outputElotOrder;
	}

	public String getOutputParsedAddressRange() {
		return outputParsedAddressRange;
	}

	public String getOutputParsedDeliveryInstallation() {
		return outputParsedDeliveryInstallation;
	}

	public String getOutputParsedExtraInformation() {
		return outputParsedExtraInformation;
	}

	public String getOutputParsedLockBox() {
		return outputParsedLockBox;
	}

	public String getOutputParsedPMBName() {
		return outputParsedPMBName;
	}

	public String getOutputParsedPMBRange() {
		return outputParsedPMBRange;
	}

	public String getOutputParsedPostDirectional() {
		return outputParsedPostDirectional;
	}

	public String getOutputParsedPreDirectional() {
		return outputParsedPreDirectional;
	}

	public String getOutputParsedRouteService() {
		return outputParsedRouteService;
	}

	public String getOutputParsedStreetName() {
		return outputParsedStreetName;
	}

	public String getOutputParsedSuffix() {
		return outputParsedSuffix;
	}

	public String getOutputParsedSuiteName() {
		return outputParsedSuiteName;
	}

	public String getOutputParsedSuiteRange() {
		return outputParsedSuiteRange;
	}

	public String getOutputPlus4() {
		return outputPlus4;
	}

	public String getOutputPrivateMailBox() {
		return outputPrivateMailBox;
	}

	public String getOutputState() {
		return outputState;
	}

	public String getOutputStateName() {
		return outputStateName;
	}

	public String getOutputSuite() {
		return outputSuite;
	}

	public String getOutputTimezone() {
		return outputTimezone;
	}

	public String getOutputTimezoneCode() {
		return outputTimezoneCode;
	}

	public String getOutputUrbanization() {
		return outputUrbanization;
	}

	public String getOutputZip() {
		return outputZip;
	}

	public String getOutputZipTypeCode() {
		return outputZipTypeCode;
	}

	public String getOutputZipTypeDescription() {
		return outputZipTypeDescription;
	}

	public String getSOAAddress() {
		return soaAddress;
	}

	public String getSOACity() {
		return soaCity;
	}

	public String getSOAFilename() {
		return soaFilename;
	}

	public String getSOAPostalCode() {
		return soaPostalCode;
	}

	public String getSOAProcessorName() {
		return soaProcessorName;
	}

	public String getSOAProvince() {
		return soaProvince;
	}

	public boolean getSOASaveToFile() {
		return soaSaveToFile;
	}

	/**
	 * @return The request handler for the address verification service
	 */
	public AddressVerifyRequestHandler getWebRequestHandler() {
		return new AddressVerifyRequestHandler(this);
	}

	/**
	 * Returns the XML representation of the Address Verifier meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_ADDRESS_VERIFY)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_COUNTRIES, optCountries.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PERFORM_DPV, Boolean.toString(optPerformDPV)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PERFORM_LACSLINK, Boolean.toString(optPerformLACSLink)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PERFORM_SUITELINK, Boolean.toString(optPerformSuiteLink)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PERFORM_ADDRPLUS, Boolean.toString(optPerformAddrPlus)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PERFORM_RBDI, Boolean.toString(optPerformRBDI)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_USE_PREFERRED_CITY, Boolean.toString(optUsePreferredCity)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_ADDRESS_PARSED, Boolean.toString(optAddressParsed)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_DIACRITIC_MODE, optDiacriticMode.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_LAST_NAME, inputLastName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_COMPANY, inputCompany));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDRESS_LINE_1, inputAddressLine1));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDRESS_LINE_2, inputAddressLine2));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_SUITE, inputSuite));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_URBANIZATION, inputUrbanization));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_CITY, inputCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_STATE, inputState));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ZIP, inputZip));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_PLUS4, inputPlus4));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_DELIVERY_POINT, inputDeliveryPoint));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_COUNTRY, inputCountry));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDRESS_LINE_1, outputAddressLine1));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDRESS_LINE_2, outputAddressLine2));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_SUITE, outputSuite));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_URBANIZATION, outputUrbanization));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_CITY, outputCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_STATE, outputState));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ZIP, outputZip));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PLUS4, outputPlus4));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_DP_AND_CHECK_DIGIT, outputDPAndCheckDigit));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PRIVATE_MAIL_BOX, outputPrivateMailBox));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTRY, outputCountry));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_CMRA, outputCMRA));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ELOT_NUMBER, outputElotNumber));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ELOT_ORDER, outputElotOrder));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_DELIVERY_INDICATION, outputDeliveryIndication));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDRESS_TYPE_CODE, outputAddressTypeCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ZIP_TYPE_CODE, outputZipTypeCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_CARRIER_ROUTE, outputCarrierRoute));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_CITY_ABBREVIATION, outputCityAbbreviation));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_CONGRESSIONAL_DISTRICT, outputCongressionalDistrict));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COMPANY, outputCompany));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_STATE_NAME, outputStateName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDRESS_TYPE_DESCRIPTION, outputAddressTypeDescription));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ZIP_TYPE_DESCRIPTION, outputZipTypeDescription));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTRY_NAME, outputCountryName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDRESS_KEY, outputAddressKey));

		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_MAK, outputMAK));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_BASE_MAK, outputBaseMAK));

		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_ADDRESS_RANGE, outputParsedAddressRange));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_PRE_DIRECTIONAL, outputParsedPreDirectional));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_STREET_NAME, outputParsedStreetName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_SUFFIX, outputParsedSuffix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_POST_DIRECTIONAL, outputParsedPostDirectional));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_SUITE_NAME, outputParsedSuiteName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_SUITE_RANGE, outputParsedSuiteRange));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_PMB_NAME, outputParsedPMBName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_PMB_RANGE, outputParsedPMBRange));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_ROUTE_SERVICE, outputParsedRouteService));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_LOCK_BOX, outputParsedLockBox));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION, outputParsedDeliveryInstallation));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_EXTRA_INFORMATION, outputParsedExtraInformation));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTY_NAME, outputCountyName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTY_FIPS, outputCountyFips));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_TIMEZONE, outputTimezone));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_TIMEZONE_CODE, outputTimezoneCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_SAVE_TO_FILE, Boolean.toString(cassSaveToFile)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_PROCESSOR_NAME, cassProcessorName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_LIST_NAME, cassListName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_NAME, cassName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_COMPANY, cassCompany));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_ADDRESS, cassAddress));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_CITY, cassCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_STATE, cassState));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_ZIP, cassZip));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CASS_FILENAME, cassFilename));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SOA_SAVE_TO_FILE, Boolean.toString(soaSaveToFile)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SOA_PROCESSOR_NAME, soaProcessorName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SOA_ADDRESS, soaAddress));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SOA_CITY, soaCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SOA_PROVINCE, soaProvince));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SOA_POSTAL_CODE, soaPostalCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SOA_FILENAME, soaFilename));
		retval.append(tab).append(XMLHandler.closeTag(TAG_ADDRESS_VERIFY)).append(Const.CR);
		return retval.toString();
	}

	public boolean isCanadaLicensed() {
		// Check if product is licensed
		if ((data.getAdvancedConfiguration().getProducts() & AdvancedConfigurationMeta.MDLICENSE_Canada) != 0) { return true; }
		return false;
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if licensed
		boolean isLicensed = isLicensed();
		// Enabled only if there are input fields
		boolean noInputFields = Const.isEmpty(inputLastName) && Const.isEmpty(inputCompany) && Const.isEmpty(inputAddressLine1) && Const.isEmpty(inputAddressLine2) && Const.isEmpty(inputCity) && Const.isEmpty(inputState) && Const.isEmpty(inputZip)
				&& Const.isEmpty(inputCountry);
		boolean minFields = true;
		if (Const.isEmpty(inputAddressLine1) && Const.isEmpty(inputAddressLine2)) {
			minFields = false;
		}
		if (Const.isEmpty(inputCity) || Const.isEmpty(inputState)) {
			if (Const.isEmpty(inputZip)) {
				minFields = false;
			}
		}
		return isLicensed && !noInputFields && minFields;
	}

	public boolean isInitializeOK() {
		return initializeOK;
	}

	/**
	 * @return true if address verification is licensed
	 */
	public boolean isLicensed() {
		// Licensed if it is not a DLL
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		if (acMeta.getServiceType() == ServiceType.CVS) {
			return true;
		}
		if ((acMeta.getServiceType() == ServiceType.Web) && !Const.isEmpty(getCustomerID())) {
			return true;
		}
		// Check if product is licensed
		if ((acMeta.getProducts() & MDPropTags.MDLICENSE_Address) != 0 || (acMeta.getProducts() & MDPropTags.MDLICENSE_Community) != 0) {
			return true;
		}
		;
		return false;
	}

	public boolean isRBDILicensed() {
		// Check if product is licensed
		if ((data.getAdvancedConfiguration().getProducts() & AdvancedConfigurationMeta.MDLICENSE_RBDI) != 0) { return true; }
		return false;
	}

	/**
	 * Called to process the results of either the local or web services
	 *
	 * @param checkData
	 * @param requests
	 */
	public void outputData(MDCheckData checkData, List<MDCheckRequest> requests) {
		// Skip if not enabled
		if (!isEnabled()) { return; }
		// Output each request's results
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Output the address results
			MDCheckCVRequest.AddrResults addrResults = request.addrResults;
			if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject)
					&& AdvancedConfigurationMeta.isCommunity()){
				addrResults = request.getAddrCommunityResults(addrResults);
			}
			if ((addrResults != null) && addrResults.valid) {
				// output results
				if (!Const.isEmpty(outputAddressLine1)) {
					String tempAddress = addrResults.Address1;
					if (Const.isEmpty(outputAddressLine2)) {
						if (Const.isEmpty(outputPrivateMailBox)) {
							tempAddress = (tempAddress + " " + addrResults.PrivateMailBox).trim();
						}
						if (Const.isEmpty(outputSuite)) {
							tempAddress = (tempAddress + " " + (addrResults.Suite != null ? addrResults.Suite : "")).trim();
						}
					}
					request.addOutputData(tempAddress);
				}
				if (!Const.isEmpty(outputAddressLine2)) {
					String tempAddress = addrResults.Address2;
					if (Const.isEmpty(outputPrivateMailBox)) {
						tempAddress = (tempAddress + " " + addrResults.PrivateMailBox).trim();
					}
					if (Const.isEmpty(outputSuite)) {
						tempAddress = (tempAddress + " " + addrResults.Suite).trim();
					}
					request.addOutputData(tempAddress);
				}
				if (!Const.isEmpty(outputSuite)) {
					request.addOutputData(addrResults.Suite);
				}
				if (!Const.isEmpty(outputUrbanization)) {
					request.addOutputData(addrResults.UrbanizationName);
				}
				if (!Const.isEmpty(outputCity)) {
					request.addOutputData(addrResults.CityName);
				}
				if (!Const.isEmpty(outputState)) {
					request.addOutputData(addrResults.StateAbbreviation);
				}
				if (!Const.isEmpty(outputZip)) {
					String zip = addrResults.Zip;
					String plus4 = addrResults.Plus4;
					// Include plus4 optional suffix
					if (Const.isEmpty(outputPlus4) && !Const.isEmpty(plus4)) {
						zip += "-" + plus4;
					}
					request.addOutputData(zip);
				}
				if (!Const.isEmpty(outputPlus4)) {
					request.addOutputData(addrResults.Plus4);
				}
				if (!Const.isEmpty(outputDPAndCheckDigit)) {
					String dpCode = addrResults.DeliveryPointCode;
					String checkDigit = addrResults.DeliveryPointCheckDigit;
					// Combine two fields if found. Return nothing if one or the other is blank
					dpCode = dpCode + checkDigit;
					request.addOutputData(dpCode);
				}
				if (!Const.isEmpty(outputPrivateMailBox)) {
					request.addOutputData(addrResults.PrivateMailBox);
				}
				if (!Const.isEmpty(outputCountry)) {
					request.addOutputData(addrResults.CountryAbbreviation);
				}
				if (!Const.isEmpty(outputCMRA)) {
					request.addOutputData(addrResults.CMRA);
				}
				if (!Const.isEmpty(outputElotNumber)) {
					request.addOutputData(addrResults.ELotNumber);
				}
				if (!Const.isEmpty(outputElotOrder)) {
					request.addOutputData(addrResults.ELotOrder);
				}
				if (!Const.isEmpty(outputDeliveryIndication)) {
					if (optPerformRBDI) {
						if (!data.getAdvancedConfiguration().getServiceType().toString().equalsIgnoreCase("Local")) {
							request.addOutputData(request.rbdiResults.RDBIndicator);
						} else {
							request.addOutputData(addrResults.RBDI);
						}
					} else {
						request.addOutputData("");
					}
				}
				// info results
				if (!Const.isEmpty(outputAddressTypeCode)) {
					request.addOutputData(addrResults.TypeAddressCode);
				}
				if (!Const.isEmpty(outputAddressTypeDescription)) {
					request.addOutputData(addrResults.TypeAddressDescription);
				}
				if (!Const.isEmpty(outputZipTypeCode)) {
					request.addOutputData(addrResults.TypeZipCode);
				}
				if (!Const.isEmpty(outputCarrierRoute)) {
					request.addOutputData(addrResults.CarrierRoute);
				}
				if (!Const.isEmpty(outputCityAbbreviation)) {
					request.addOutputData(addrResults.CityAbbreviation);
				}
				if (!Const.isEmpty(outputCongressionalDistrict)) {
					request.addOutputData(addrResults.CongressionalDistrict);
				}
				// TODO: Figure out what, if anything, to do with these values
				if (false) {
					if (!Const.isEmpty(outputCompany)) {
						request.addOutputData(addrResults.Company);
					}
					if (!Const.isEmpty(outputStateName)) {
						request.addOutputData(addrResults.StateName);
					}
					if (!Const.isEmpty(outputZipTypeDescription)) {
						request.addOutputData(addrResults.TypeZipDescription);
					}
					if (!Const.isEmpty(outputCountryName)) {
						request.addOutputData(addrResults.CountryName);
					}
				}
				// parsed results
				if (!Const.isEmpty(outputParsedAddressRange)) {
					request.addOutputData(addrResults.ParsedAddressRange);
				}
				if (!Const.isEmpty(outputParsedPreDirectional)) {
					request.addOutputData(addrResults.ParsedDirectionPre);
				}
				if (!Const.isEmpty(outputParsedStreetName)) {
					request.addOutputData(addrResults.ParsedStreetName);
				}
				if (!Const.isEmpty(outputParsedSuffix)) {
					request.addOutputData(addrResults.ParsedSuffix);
				}
				if (!Const.isEmpty(outputParsedPostDirectional)) {
					request.addOutputData(addrResults.ParsedDirectionPost);
				}
				if (!Const.isEmpty(outputParsedSuiteName)) {
					request.addOutputData(addrResults.ParsedSuiteName);
				}
				if (!Const.isEmpty(outputParsedSuiteRange)) {
					request.addOutputData(addrResults.ParsedSuiteRange);
				}
				if (!Const.isEmpty(outputParsedPMBName)) {
					request.addOutputData(addrResults.ParsedPMBName);
				}
				if (!Const.isEmpty(outputParsedPMBRange)) {
					request.addOutputData(addrResults.ParsedPMBRange);
				}
				if (!Const.isEmpty(outputParsedRouteService)) {
					request.addOutputData(addrResults.ParsedRouteService);
				}
				if (!Const.isEmpty(outputParsedLockBox)) {
					request.addOutputData(addrResults.ParsedLockBox);
				}
				if (!Const.isEmpty(outputParsedDeliveryInstallation)) {
					request.addOutputData(addrResults.ParsedDeliveryInstallation);
				}
				if (!Const.isEmpty(outputParsedExtraInformation)) {
					request.addOutputData(addrResults.ParsedExtraInformation);
				}
				// Extra stuff
				if (!Const.isEmpty(outputCountyName)) {
					request.addOutputData(addrResults.CountyName);
				}
				if (!Const.isEmpty(outputCountyFips)) {
					request.addOutputData(addrResults.CountyFips);
				}
				if (!Const.isEmpty(outputTimezone)) {
					request.addOutputData(addrResults.TimeZone);
				}
				if (!Const.isEmpty(outputTimezoneCode)) {
					request.addOutputData(addrResults.TimeZoneCode);
				}
				// Address key
				if (!Const.isEmpty(outputAddressKey)) {
					// make sure there is an address key not all 0's
					if (addrResults.AddressKey.length() > 7) {
						try {
							long tst = Long.parseLong(addrResults.AddressKey);
							if (tst == 0) {
								addrResults.AddressKey = "";
							}
						} catch (NumberFormatException nfe) {
						}
					}
					request.addOutputData(addrResults.AddressKey);
				}

				// MAK
				if (!Const.isEmpty(outputMAK)) {
					// make sure there is an address key not all 0's
					if (addrResults.MAK.length() > 7) {
						try {
							long tst = Long.parseLong(addrResults.MAK);
							if (tst == 0) {
								addrResults.MAK = "";
							}
						} catch (NumberFormatException nfe) {
						}
					}
					request.addOutputData(addrResults.MAK);
				}

				// BASE MAK
				if (!Const.isEmpty(outputBaseMAK)) {
					// make sure there is an address key not all 0's
					if (addrResults.BaseMAK.length() > 7) {
						try {
							long tst = Long.parseLong(addrResults.BaseMAK);
							if (tst == 0) {
								addrResults.BaseMAK = "";
							}
						} catch (NumberFormatException nfe) {
						}
					}
					request.addOutputData(addrResults.BaseMAK);
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
				// TODO: Reroute this record to an invalid output stream?
			}
			// Add the address result codes to the overall result codes
			if (addrResults != null) {
				// Filter out some result codes on blank results
				if (Const.isEmpty(addrResults.Address1) && Const.isEmpty(addrResults.Address2) && Const.isEmpty(addrResults.Suite) && Const.isEmpty(addrResults.CityName) && Const.isEmpty(addrResults.StateAbbreviation)
						&& Const.isEmpty(addrResults.Zip)) {
					addrResults.resultCodes.remove("AS01");
				}
				if (addrResults.resultCodes.contains("DE")) {
					addrResults.resultCodes.remove("DE");
				}
				if (addrResults.resultCodes.contains("AE01") && addrResults.resultCodes.contains("AE07")) {
					addrResults.resultCodes.remove("AE01");
				}
				// Add address result codes to overall result codes
				if(AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject)) {
					request.resultCodes.addAll(addrResults.resultCodes);
				}
				// If reporting is enabled then gather some stats
				if (data.isReportEnabled()) {
					updateStats(checkData, addrResults);
				}
			}
		}
	}

	/**
	 * Called to read name parsing meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_ADDRESS_VERIFY);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Option fields
			String value = MDCheckStepData.getTagValue(node, TAG_OPT_COUNTRIES);
			optCountries = (value != null) ? Countries.decode(value) : optCountries;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PERFORM_DPV);
			optPerformDPV = (value != null) ? Boolean.valueOf(value) : optPerformDPV;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PERFORM_LACSLINK);
			optPerformLACSLink = (value != null) ? Boolean.valueOf(value) : optPerformLACSLink;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PERFORM_SUITELINK);
			optPerformSuiteLink = (value != null) ? Boolean.valueOf(value) : optPerformSuiteLink;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PERFORM_ADDRPLUS);
			optPerformAddrPlus = (value != null) ? Boolean.valueOf(value) : optPerformAddrPlus;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PERFORM_RBDI);
			optPerformRBDI = (value != null) ? Boolean.valueOf(value) : optPerformRBDI;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_USE_PREFERRED_CITY);
			optUsePreferredCity = (value != null) ? Boolean.valueOf(value) : optUsePreferredCity;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_ADDRESS_PARSED);
			optAddressParsed = (value != null) ? Boolean.valueOf(value) : optAddressParsed;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_DIACRITIC_MODE);
			optDiacriticMode = (value != null) ? DiacriticMode.decode(value) : optDiacriticMode;
			// Input fields
			inputLastName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_LAST_NAME), inputLastName);
			inputCompany = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_COMPANY), inputCompany);
			inputAddressLine1 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDRESS_LINE_1), inputAddressLine1);
			inputAddressLine2 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDRESS_LINE_2), inputAddressLine2);
			inputSuite = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_SUITE), inputSuite);
			inputUrbanization = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_URBANIZATION), inputUrbanization);
			inputCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_CITY), inputCity);
			inputState = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_STATE), inputState);
			inputZip = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ZIP), inputZip);
			inputPlus4 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_PLUS4), inputPlus4);
			inputDeliveryPoint = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_DELIVERY_POINT), inputDeliveryPoint);
			inputCountry = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_COUNTRY), inputCountry);
			// Output fields
			outputAddressLine1 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDRESS_LINE_1), outputAddressLine1);
			outputAddressLine2 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDRESS_LINE_2), outputAddressLine2);
			outputSuite = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_SUITE), outputSuite);
			outputUrbanization = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_URBANIZATION), outputUrbanization);
			outputCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_CITY), outputCity);
			outputState = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_STATE), outputState);
			outputZip = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ZIP), outputZip);
			outputPlus4 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PLUS4), outputPlus4);
			outputDPAndCheckDigit = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_DP_AND_CHECK_DIGIT), outputDPAndCheckDigit);
			outputPrivateMailBox = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PRIVATE_MAIL_BOX), outputPrivateMailBox);
			outputCountry = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTRY), outputCountry);
			outputCMRA = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_CMRA), outputCMRA);
			outputElotNumber = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ELOT_NUMBER), outputElotNumber);
			outputElotOrder = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ELOT_ORDER), outputElotOrder);
			outputDeliveryIndication = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_DELIVERY_INDICATION), outputDeliveryIndication);
			// Additional address fields
			outputAddressTypeCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDRESS_TYPE_CODE), outputAddressTypeCode);
			outputZipTypeCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ZIP_TYPE_CODE), outputZipTypeCode);
			outputCarrierRoute = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_CARRIER_ROUTE), outputCarrierRoute);
			outputCityAbbreviation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_CITY_ABBREVIATION), outputCityAbbreviation);
			outputCongressionalDistrict = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_CONGRESSIONAL_DISTRICT), outputCongressionalDistrict);
			outputCompany = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COMPANY), outputCompany);
			outputStateName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_STATE_NAME), outputStateName);
			outputAddressTypeDescription = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDRESS_TYPE_DESCRIPTION), outputAddressTypeDescription);
			outputZipTypeDescription = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ZIP_TYPE_DESCRIPTION), outputZipTypeDescription);
			outputCountryName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTRY_NAME), outputCountryName);
			outputAddressKey = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDRESS_KEY), outputAddressKey);

			outputMAK = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_MAK), outputMAK);
			outputBaseMAK = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_BASE_MAK), outputBaseMAK);
			// Parsed address fields
			outputParsedAddressRange = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_ADDRESS_RANGE), outputParsedAddressRange);
			outputParsedPreDirectional = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_PRE_DIRECTIONAL), outputParsedPreDirectional);
			outputParsedStreetName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_STREET_NAME), outputParsedStreetName);
			outputParsedSuffix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_SUFFIX), outputParsedSuffix);
			outputParsedPostDirectional = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_POST_DIRECTIONAL), outputParsedPostDirectional);
			outputParsedSuiteName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_SUITE_NAME), outputParsedSuiteName);
			outputParsedSuiteRange = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_SUITE_RANGE), outputParsedSuiteRange);
			outputParsedPMBName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_PMB_NAME), outputParsedPMBName);
			outputParsedPMBRange = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_PMB_RANGE), outputParsedPMBRange);
			outputParsedRouteService = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_ROUTE_SERVICE), outputParsedRouteService);
			outputParsedLockBox = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_LOCK_BOX), outputParsedLockBox);
			outputParsedDeliveryInstallation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION), outputParsedDeliveryInstallation);
			outputParsedExtraInformation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_EXTRA_INFORMATION), outputParsedExtraInformation);
			outputCountyName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTY_NAME), outputCountyName);
			outputCountyFips = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTY_FIPS), outputCountyFips);
			outputTimezone = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_TIMEZONE), outputTimezone);
			outputTimezoneCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_TIMEZONE_CODE), outputTimezoneCode);
			// CASS Form
			value = MDCheckStepData.getTagValue(node, TAG_CASS_SAVE_TO_FILE);
			cassSaveToFile = (value != null) ? Boolean.valueOf(value) : cassSaveToFile;
			cassProcessorName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_PROCESSOR_NAME), cassProcessorName);
			cassListName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_LIST_NAME), cassListName);
			cassName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_NAME), cassName);
			cassCompany = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_COMPANY), cassCompany);
			cassAddress = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_ADDRESS), cassAddress);
			cassCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_CITY), cassCity);
			cassState = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_STATE), cassState);
			cassZip = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_ZIP), cassZip);
			cassFilename = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CASS_FILENAME), cassFilename);
			// SOA Form
			value = MDCheckStepData.getTagValue(node, TAG_SOA_SAVE_TO_FILE);
			soaSaveToFile = (value != null) ? Boolean.valueOf(value) : soaSaveToFile;
			soaProcessorName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SOA_PROCESSOR_NAME), soaProcessorName);
			soaAddress = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SOA_ADDRESS), soaAddress);
			soaCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SOA_CITY), soaCity);
			soaProvince = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SOA_PROVINCE), soaProvince);
			soaPostalCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SOA_POSTAL_CODE), soaPostalCode);
			soaFilename = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SOA_FILENAME), soaFilename);
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
		String value = rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_COUNTRIES);
		optCountries = (value != null) ? Countries.decode(value) : optCountries;
		optPerformDPV = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_DPV);
		optPerformLACSLink = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_LACSLINK);
		optPerformSuiteLink = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_SUITELINK);
		optPerformAddrPlus = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_ADDRPLUS);
		optPerformRBDI = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_RBDI);
		optUsePreferredCity = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_USE_PREFERRED_CITY);
		optAddressParsed = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_ADDRESS_PARSED);
		value = rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_DIACRITIC_MODE);
		optDiacriticMode = (value != null) ? DiacriticMode.decode(value) : optDiacriticMode;
		inputLastName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_LAST_NAME), inputLastName);
		inputCompany = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_COMPANY), inputCompany);
		inputAddressLine1 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_ADDRESS_LINE_1), inputAddressLine1);
		inputAddressLine2 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_ADDRESS_LINE_2), inputAddressLine2);
		inputSuite = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_SUITE), inputSuite);
		inputUrbanization = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_URBANIZATION), inputUrbanization);
		inputCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_CITY), inputCity);
		inputState = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_STATE), inputState);
		inputZip = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_ZIP), inputZip);
		inputPlus4 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_PLUS4), inputPlus4);
		inputDeliveryPoint = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_DELIVERY_POINT), inputDeliveryPoint);
		inputCountry = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_COUNTRY), inputCountry);
		outputAddressLine1 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_LINE_1), outputAddressLine1);
		outputAddressLine2 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_LINE_2), outputAddressLine2);
		outputSuite = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_SUITE), outputSuite);
		outputUrbanization = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_URBANIZATION), outputUrbanization);
		outputCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CITY), outputCity);
		outputState = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_STATE), outputState);
		outputZip = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ZIP), outputZip);
		outputPlus4 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PLUS4), outputPlus4);
		outputDPAndCheckDigit = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_DP_AND_CHECK_DIGIT), outputDPAndCheckDigit);
		outputPrivateMailBox = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PRIVATE_MAIL_BOX), outputPrivateMailBox);
		outputCountry = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTRY), outputCountry);
		outputCMRA = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CMRA), outputCMRA);
		outputElotNumber = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ELOT_NUMBER), outputElotNumber);
		outputElotOrder = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ELOT_ORDER), outputElotOrder);
		outputDeliveryIndication = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_DELIVERY_INDICATION), outputDeliveryIndication);
		outputAddressTypeCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_TYPE_CODE), outputAddressTypeCode);
		outputZipTypeCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ZIP_TYPE_CODE), outputZipTypeCode);
		outputCarrierRoute = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CARRIER_ROUTE), outputCarrierRoute);
		outputCityAbbreviation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CITY_ABBREVIATION), outputCityAbbreviation);
		outputCongressionalDistrict = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CONGRESSIONAL_DISTRICT), outputCongressionalDistrict);
		outputCompany = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COMPANY), outputCompany);
		outputStateName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_STATE_NAME), outputStateName);
		outputAddressTypeDescription = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_TYPE_DESCRIPTION), outputAddressTypeDescription);
		outputZipTypeDescription = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ZIP_TYPE_DESCRIPTION), outputZipTypeDescription);
		outputCountryName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTRY_NAME), outputCountryName);
		outputAddressKey = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_KEY), outputAddressKey);

		outputMAK = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_MAK), outputMAK);
		outputBaseMAK = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_BASE_MAK), outputBaseMAK);

		outputParsedAddressRange = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_ADDRESS_RANGE), outputParsedAddressRange);
		outputParsedPreDirectional = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_PRE_DIRECTIONAL), outputParsedPreDirectional);
		outputParsedStreetName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_STREET_NAME), outputParsedStreetName);
		outputParsedSuffix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_SUFFIX), outputParsedSuffix);
		outputParsedPostDirectional = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_POST_DIRECTIONAL), outputParsedPostDirectional);
		outputParsedSuiteName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_SUITE_NAME), outputParsedSuiteName);
		outputParsedSuiteRange = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_SUITE_RANGE), outputParsedSuiteRange);
		outputParsedPMBName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_PMB_NAME), outputParsedPMBName);
		outputParsedPMBRange = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_PMB_RANGE), outputParsedPMBRange);
		outputParsedRouteService = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_ROUTE_SERVICE), outputParsedRouteService);
		outputParsedLockBox = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_LOCK_BOX), outputParsedLockBox);
		outputParsedDeliveryInstallation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION), outputParsedDeliveryInstallation);
		outputParsedExtraInformation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_EXTRA_INFORMATION), outputParsedExtraInformation);
		outputCountyName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTY_NAME), outputCountyName);
		outputCountyFips = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTY_FIPS), outputCountyFips);
		outputTimezone = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_TIMEZONE), outputTimezone);
		outputTimezoneCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_TIMEZONE_CODE), outputTimezoneCode);
		cassSaveToFile = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_SAVE_TO_FILE);
		cassProcessorName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_PROCESSOR_NAME), cassProcessorName);
		cassListName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_LIST_NAME), cassListName);
		cassName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_NAME), cassName);
		cassCompany = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_COMPANY), cassCompany);
		cassAddress = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_ADDRESS), cassAddress);
		cassCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_CITY), cassCity);
		cassState = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_STATE), cassState);
		cassZip = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_ZIP), cassZip);
		cassFilename = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_FILENAME), cassFilename);
		soaSaveToFile = rep.getStepAttributeBoolean(idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_SAVE_TO_FILE);
		soaProcessorName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_PROCESSOR_NAME), soaProcessorName);
		soaAddress = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_ADDRESS), soaAddress);
		soaCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_CITY), soaCity);
		soaProvince = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_PROVINCE), soaProvince);
		soaPostalCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_POSTAL_CODE), soaPostalCode);
		soaFilename = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_FILENAME), soaFilename);
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
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_COUNTRIES, optCountries.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_DPV, optPerformDPV);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_LACSLINK, optPerformLACSLink);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_SUITELINK, optPerformSuiteLink);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_ADDRPLUS, optPerformAddrPlus);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_PERFORM_RBDI, optPerformRBDI);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_USE_PREFERRED_CITY, optUsePreferredCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_ADDRESS_PARSED, optAddressParsed);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OPT_DIACRITIC_MODE, optDiacriticMode.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_LAST_NAME, inputLastName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_COMPANY, inputCompany);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_ADDRESS_LINE_1, inputAddressLine1);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_ADDRESS_LINE_2, inputAddressLine2);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_SUITE, inputSuite);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_URBANIZATION, inputUrbanization);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_CITY, inputCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_STATE, inputState);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_ZIP, inputZip);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_PLUS4, inputPlus4);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_DELIVERY_POINT, inputDeliveryPoint);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_INPUT_COUNTRY, inputCountry);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_LINE_1, outputAddressLine1);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_LINE_2, outputAddressLine2);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_SUITE, outputSuite);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_URBANIZATION, outputUrbanization);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CITY, outputCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_STATE, outputState);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ZIP, outputZip);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PLUS4, outputPlus4);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_DP_AND_CHECK_DIGIT, outputDPAndCheckDigit);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PRIVATE_MAIL_BOX, outputPrivateMailBox);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTRY, outputCountry);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CMRA, outputCMRA);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ELOT_NUMBER, outputElotNumber);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ELOT_ORDER, outputElotOrder);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_DELIVERY_INDICATION, outputDeliveryIndication);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_TYPE_CODE, outputAddressTypeCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ZIP_TYPE_CODE, outputZipTypeCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CARRIER_ROUTE, outputCarrierRoute);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CITY_ABBREVIATION, outputCityAbbreviation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_CONGRESSIONAL_DISTRICT, outputCongressionalDistrict);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COMPANY, outputCompany);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_STATE_NAME, outputStateName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_TYPE_DESCRIPTION, outputAddressTypeDescription);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ZIP_TYPE_DESCRIPTION, outputZipTypeDescription);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTRY_NAME, outputCountryName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_ADDRESS_KEY, outputAddressKey);

		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_MAK, outputMAK);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_BASE_MAK, outputBaseMAK);

		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_ADDRESS_RANGE, outputParsedAddressRange);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_PRE_DIRECTIONAL, outputParsedPreDirectional);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_STREET_NAME, outputParsedStreetName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_SUFFIX, outputParsedSuffix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_POST_DIRECTIONAL, outputParsedPostDirectional);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_SUITE_NAME, outputParsedSuiteName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_SUITE_RANGE, outputParsedSuiteRange);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_PMB_NAME, outputParsedPMBName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_PMB_RANGE, outputParsedPMBRange);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_ROUTE_SERVICE, outputParsedRouteService);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_LOCK_BOX, outputParsedLockBox);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION, outputParsedDeliveryInstallation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_PARSED_EXTRA_INFORMATION, outputParsedExtraInformation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTY_NAME, outputCountyName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_COUNTY_FIPS, outputCountyFips);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_TIMEZONE, outputTimezone);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_OUTPUT_TIMEZONE_CODE, outputTimezoneCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_SAVE_TO_FILE, cassSaveToFile);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_PROCESSOR_NAME, cassProcessorName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_LIST_NAME, cassListName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_NAME, cassName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_COMPANY, cassCompany);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_ADDRESS, cassAddress);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_CITY, cassCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_STATE, cassState);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_ZIP, cassZip);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_CASS_FILENAME, cassFilename);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_SAVE_TO_FILE, soaSaveToFile);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_PROCESSOR_NAME, soaProcessorName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_ADDRESS, soaAddress);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_CITY, soaCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_PROVINCE, soaProvince);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_POSTAL_CODE, soaPostalCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_ADDRESS_VERIFY + "." + TAG_SOA_FILENAME, soaFilename);
	}

	/**
	 * Called to save the CASS and/or SOA reports (if configured to do so)
	 *
	 * @param data
	 * @throws KettleException
	 */
	public void saveReports(MDCheckData data) throws KettleException {
		// Skip not enabled
		if (!isEnabled()) { return; }
		// Get reference to address object
		mdAddr Addr = data.Addr;
		// Get CASS report if address object configured for US data and the report is requested
		if (cassSaveToFile && ((optCountries == Countries.US) || (optCountries == Countries.USCanada))) {
			// Configure the form
			Addr.SetPS3553_B1_ProcessorName(cassProcessorName);
			Addr.SetPS3553_B4_ListName(cassListName);
			Addr.SetPS3553_D3_Name(cassName);
			Addr.SetPS3553_D3_Company(cassCompany);
			Addr.SetPS3553_D3_Address(cassAddress);
			Addr.SetPS3553_D3_City(cassCity);
			Addr.SetPS3553_D3_State(cassState);
			Addr.SetPS3553_D3_ZIP(cassZip);
			// Save the form
			if (!Addr.SaveFormPS3553(cassFilename)) { throw new KettleException(BaseMessages.getString(PKG, "MDCheck.Error.CouldNotSaveCASS", cassFilename)); }
		}
		// Get SOA report if address object configured for Canada data and the report is requested
		if ((optCountries == Countries.Canada) || ((optCountries == Countries.USCanada) && soaSaveToFile)) {
			// Configure the form
			String address;
			if (!Const.isEmpty(soaAddress) && !Const.isEmpty(soaCity + soaProcessorName + soaPostalCode)) {
				address = soaAddress + ", " + soaCity + " " + soaProvince + " " + soaPostalCode;
			} else {
				address = soaAddress + " " + soaCity + " " + soaProvince + " " + soaPostalCode;
			}
			address = address.trim().replaceAll("  ", " ");
			Addr.SetSOACustomerInfo(soaProcessorName, address);
			// Save the form (TODO: no way to check for failure?)
			Addr.SaveFormSOA(soaFilename);
		}
	}

	public void setCASSAddress(String s) {
		cassAddress = s;
	}

	public void setCASSCity(String s) {
		cassCity = s;
	}

	public void setCASSCompany(String s) {
		cassCompany = s;
	}

	public void setCASSFilename(String s) {
		cassFilename = s;
	}

	public void setCASSListName(String s) {
		cassListName = s;
	}

	public void setCASSName(String s) {
		cassName = s;
	}

	public void setCASSProcessorName(String s) {
		cassProcessorName = s;
	}

	public void setCASSSaveToFile(boolean b) {
		cassSaveToFile = b;
	}

	public void setCASSState(String s) {
		cassState = s;
	}

	public void setCASSZip(String s) {
		cassZip = s;
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault() {
		// options
		// if(isCanadaLicensed())
		// optCountries = Countries.USCanada;// is this what we want?
		// else
		optCountries = Countries.US;  // TODO: Correct? Our DQT doesn't have Canada.
		optPerformDPV = true;
		optPerformLACSLink = true;
		optPerformSuiteLink = false;
		optPerformAddrPlus = false;
		optPerformRBDI = false;
		optUsePreferredCity = false;
		optAddressParsed = true;
		optDiacriticMode = DiacriticMode.Automatic;
		// input meta data (blank means not defined)
		if (!AdvancedConfigurationMeta.isUseMapping()) {
			inputLastName = "";
			inputCompany = "";
			inputAddressLine1 = "";
			inputAddressLine2 = "";
			inputSuite = "";
			inputUrbanization = "";
			inputCity = "";
			inputState = "";
			inputZip = "";
			inputPlus4 = "";
			inputDeliveryPoint = "";
			inputCountry = "";
		}
		// Customer defined defaults
		if (MDCheckData.defaultsSet) {
			outputAddressLine1 = MDCheckData.address;
			outputAddressLine2 = MDCheckData.address2;
			outputCity = MDCheckData.city;
			outputState = MDCheckData.state;
			outputZip = MDCheckData.zip;
			outputCountry = MDCheckData.country;
			outputAddressKey = MDCheckData.addressKey;

			outputMAK = MDCheckData.MAK;
			outputBaseMAK = MDCheckData.baseMAK;

			outputSuite = MDCheckData.outputSuite;
			outputPrivateMailBox = MDCheckData.outputPrivateMailBox;
			outputUrbanization = MDCheckData.outputUrbanization;
			outputPlus4 = MDCheckData.outputPlus4;
			outputDPAndCheckDigit = MDCheckData.outputDPAndCheckDigit;
			outputCarrierRoute = MDCheckData.outputCarrierRoute;
			outputZipTypeCode = MDCheckData.outputZipTypeCode;
			outputAddressTypeCode = MDCheckData.outputAddressTypeCode;
			outputAddressTypeDescription = MDCheckData.outputAddressTypeDescription;
			outputCMRA = MDCheckData.outputCMRA;
			outputElotNumber = MDCheckData.outputElotNumber;
			outputElotOrder = MDCheckData.outputElotOrder;
			outputDeliveryIndication = MDCheckData.outputDeliveryIndication;
			// Geographic info
			outputCityAbbreviation = MDCheckData.outputCityAbbreviation;
			outputCountyName = MDCheckData.outputCountyName;
			outputCountyFips = MDCheckData.outputCountyFips;
			outputCongressionalDistrict = MDCheckData.outputCongressionalDistrict;
			outputTimezone = MDCheckData.outputTimezone;
			outputTimezoneCode = MDCheckData.outputTimezoneCode;
			// Parsed address info
			outputParsedAddressRange = MDCheckData.outputParsedAddressRange;
			outputParsedPreDirectional = MDCheckData.outputParsedPreDirectional;
			outputParsedStreetName = MDCheckData.outputParsedStreetName;
			outputParsedSuffix = MDCheckData.outputParsedSuffix;
			outputParsedPostDirectional = MDCheckData.outputParsedPostDirectional;
			outputParsedSuiteName = MDCheckData.outputParsedSuiteName;
			outputParsedSuiteRange = MDCheckData.outputParsedSuiteRange;
			outputParsedPMBName = MDCheckData.outputParsedPMBName;
			outputParsedPMBRange = MDCheckData.outputParsedPMBRange;
			outputParsedRouteService = MDCheckData.outputParsedRouteService;
			outputParsedLockBox = MDCheckData.outputParsedLockBox;
			outputParsedDeliveryInstallation = MDCheckData.outputParsedDeliveryInstallation;
			outputParsedExtraInformation = MDCheckData.outputParsedExtraInformation;
			/*
			 * EXTRA not currently used
			 * outputCompany = MDCheckData.outputCompany;
			 * outputStateName = MDCheckData.outputStateName;
			 * outputZipTypeDescription = MDCheckData.outputZipTypeDescription;
			 * outputCountryName = MDCheckData.outputCountryName;
			 */
		} else {
			outputAddressLine1 = "MD_Address";
			outputAddressLine2 = "MD_Address2";
			outputCity = "MD_City";
			outputState = "MD_State";
			outputZip = "MD_Zip";
			outputCountry = "MD_Country";
			outputAddressKey = "MD_AddressKey";

			outputMAK = "MD_MAK";
			outputBaseMAK = "MD_BaseMAK";
			// Address Output
			outputSuite = "MD_Suite";
			outputUrbanization = "MD_Urbanization";
			outputPlus4 = "MD_Plus4";
			outputDPAndCheckDigit = "MD_DPAndCheckDigit";
			outputPrivateMailBox = "MD_PrivateMailbox";
			outputCMRA = "MD_CMRA";
			outputElotNumber = "MD_ElotNumber";
			outputElotOrder = "MD_ElotOrder";
			outputDeliveryIndication = "MD_DeliveryIndication";
			// Address additional info
			outputAddressTypeCode = "MD_AddressType";
			outputZipTypeCode = "MD_ZipType";
			outputCarrierRoute = "MD_CRRT";
			outputCityAbbreviation = "MD_CityAbbrev";
			outputCongressionalDistrict = "MD_District";
			outputCompany = "MD_Company";
			outputStateName = "MD_StateName";
			outputAddressTypeDescription = "MD_AddressTypeDesc";
			outputZipTypeDescription = "MD_ZipTypeDesc";
			outputCountryName = "MD_CountryName";
			// Address parsed info
			outputParsedAddressRange = "MD_AddrRange";
			outputParsedPreDirectional = "MD_AddrPreDir";
			outputParsedStreetName = "MD_AddrName";
			outputParsedSuffix = "MD_AddrSuffix";
			outputParsedPostDirectional = "MD_AddrPostDir";
			outputParsedSuiteName = "MD_AddrSuiteName";
			outputParsedSuiteRange = "MD_AddrSuiteRange";
			outputParsedPMBName = "MD_AddrPMBName";
			outputParsedPMBRange = "MD_AddrPMBRange";
			outputParsedRouteService = "MD_AddrRouteService";
			outputParsedLockBox = "MD_AddrLockBox";
			outputParsedDeliveryInstallation = "MD_AddrDeliveryInstallation";
			outputParsedExtraInformation = "MD_AddrExtraInformation";
			outputCountyName = "MD_AddrCountyName";
			outputCountyFips = "MD_AddrCountyFips";
			outputTimezone = "MD_AddrTimezone";
			outputTimezoneCode = "MD_AddrTimezoneCode";
		}
		// CASS Form
		cassSaveToFile = false;
		cassProcessorName = "";
		cassListName = "";
		cassName = "";
		cassCompany = "";
		cassAddress = "";
		cassCity = "";
		cassState = "";
		cassZip = "";
		cassFilename = "";
		// SOA Form
		soaSaveToFile = false;
		soaProcessorName = "";
		soaAddress = "";
		soaCity = "";
		soaProvince = "";
		soaPostalCode = "";
		soaFilename = "";
	}

	public void setInitializeOK(boolean initializeOK) {
		this.initializeOK = initializeOK;
	}

	public void setInputAddressLine1(String s) {
		inputAddressLine1 = s;
	}

	public void setInputAddressLine2(String s) {
		inputAddressLine2 = s;
	}

	public void setInputCity(String s) {
		inputCity = s;
	}

	public void setInputCompany(String s) {
		inputCompany = s;
	}

	public void setInputCountry(String s) {
		inputCountry = s;
	}

	public void setInputDeliveryPoint(String s) {
		inputDeliveryPoint = s;
	}

	public void setInputLastName(String s) {
		inputLastName = s;
	}

	public void setInputPlus4(String s) {
		inputPlus4 = s;
	}

	public void setInputState(String s) {
		inputState = s;
	}

	public void setInputSuite(String s) {
		inputSuite = s;
	}

	public void setInputUrbanization(String s) {
		inputUrbanization = s;
	}

	public void setInputZip(String s) {
		inputZip = s;
	}

	public void setOptionAddressParsed(boolean b) {
		optAddressParsed = b;
	}

	public void setOptionCountries(Countries e) {
		optCountries = e;
	}

	public void setOptionDiacriticMode(DiacriticMode e) {
		optDiacriticMode = e;
	}

	public void setOptionPerformAddrPlus(boolean b) {
		optPerformAddrPlus = b;
	}

	public void setOptionPerformDPV(boolean b) {
		optPerformDPV = b;
	}

	public void setOptionPerformLACSLink(boolean b) {
		optPerformLACSLink = b;
	}

	public void setOptionPerformRBDI(boolean b) {
		optPerformRBDI = b;
	}

	public void setOptionPerformSuiteLink(boolean b) {
		optPerformSuiteLink = b;
	}

	public void setOptionUsePreferredCity(boolean b) {
		optUsePreferredCity = b;
	}

	public void setOutputAddressKey(String s) {
		outputAddressKey = s;
	}

	public void setOutputMAK(String s) {
		outputMAK = s;
	}
	public void setOutputBaseMAK(String s) {
		outputBaseMAK = s;
	}

	public void setOutputAddressLine1(String s) {
		outputAddressLine1 = s;
	}

	public void setOutputAddressLine2(String s) {
		outputAddressLine2 = s;
	}

	public void setOutputAddressTypeCode(String s) {
		outputAddressTypeCode = s;
	}

	public void setOutputAddressTypeDescription(String s) {
		outputAddressTypeDescription = s;
	}

	public void setOutputCarrierRoute(String s) {
		outputCarrierRoute = s;
	}

	public void setOutputCity(String s) {
		outputCity = s;
	}

	public void setOutputCityAbbreviation(String s) {
		outputCityAbbreviation = s;
	}

	public void setOutputCMRA(String outputCMRA) {
		this.outputCMRA = outputCMRA;
	}

	public void setOutputCompany(String s) {
		outputCompany = s;
	}

	public void setOutputCongressionalDistrict(String s) {
		outputCongressionalDistrict = s;
	}

	public void setOutputCountry(String s) {
		outputCountry = s;
	}

	public void setOutputCountryName(String s) {
		outputCountryName = s;
	}

	public void setOutputCountyFips(String outputCountyFips) {
		this.outputCountyFips = outputCountyFips;
	}

	public void setOutputCountyName(String outputCountyName) {
		this.outputCountyName = outputCountyName;
	}

	public void setOutputDeliveryIndication(String outputDeliveryIndication) {
		this.outputDeliveryIndication = outputDeliveryIndication;
	}

	public void setOutputDPAndCheckDigit(String s) {
		outputDPAndCheckDigit = s;
	}

	public void setOutputElotNumber(String outputElotNumber) {
		this.outputElotNumber = outputElotNumber;
	}

	public void setOutputElotOrder(String outputElotOrder) {
		this.outputElotOrder = outputElotOrder;
	}

	public void setOutputParsedAddressRange(String s) {
		outputParsedAddressRange = s;
	}

	public void setOutputParsedDeliveryInstallation(String s) {
		outputParsedDeliveryInstallation = s;
	}

	public void setOutputParsedExtraInformation(String s) {
		outputParsedExtraInformation = s;
	}

	public void setOutputParsedLockBox(String s) {
		outputParsedLockBox = s;
	}

	public void setOutputParsedPMBName(String s) {
		outputParsedPMBName = s;
	}

	public void setOutputParsedPMBRange(String s) {
		outputParsedPMBRange = s;
	}

	public void setOutputParsedPostDirectional(String s) {
		outputParsedPostDirectional = s;
	}

	public void setOutputParsedPreDirectional(String s) {
		outputParsedPreDirectional = s;
	}

	public void setOutputParsedRouteService(String s) {
		outputParsedRouteService = s;
	}

	public void setOutputParsedStreetName(String s) {
		outputParsedStreetName = s;
	}

	public void setOutputParsedSuffix(String s) {
		outputParsedSuffix = s;
	}

	public void setOutputParsedSuiteName(String s) {
		outputParsedSuiteName = s;
	}

	public void setOutputParsedSuiteRange(String s) {
		outputParsedSuiteRange = s;
	}

	public void setOutputPlus4(String s) {
		outputPlus4 = s;
	}

	public void setOutputPrivateMailBox(String s) {
		outputPrivateMailBox = s;
	}

	public void setOutputState(String s) {
		outputState = s;
	}

	public void setOutputStateName(String s) {
		outputStateName = s;
	}

	public void setOutputSuite(String s) {
		outputSuite = s;
	}

	public void setOutputTimezone(String outputTimezone) {
		this.outputTimezone = outputTimezone;
	}

	public void setOutputTimezoneCode(String outputTimezoneCode) {
		this.outputTimezoneCode = outputTimezoneCode;
	}

	public void setOutputUrbanization(String s) {
		outputUrbanization = s;
	}

	public void setOutputZip(String s) {
		outputZip = s;
	}

	public void setOutputZipTypeCode(String s) {
		outputZipTypeCode = s;
	}

	public void setOutputZipTypeDescription(String s) {
		outputZipTypeDescription = s;
	}

	public void setSOAAddress(String s) {
		soaAddress = s;
	}

	public void setSOACity(String s) {
		soaCity = s;
	}

	public void setSOAFilename(String s) {
		soaFilename = s;
	}

	public void setSOAPostalCode(String s) {
		soaPostalCode = s;
	}

	public void setSOAProcessorName(String s) {
		soaProcessorName = s;
	}

	public void setSOAProvince(String s) {
		soaProvince = s;
	}

	public void setSOASaveToFile(boolean b) {
		soaSaveToFile = b;
	}

	/**
	 * Called to validate settings before saving
	 *
	 * @param warnings
	 * @param errors
	 */
	public void validate(List<String> warnings, List<String> errors) {
		if (isLicensed()) {
			Validations checker = new Validations();
			checker.checkAddressInputFields(data, warnings, errors);
		}
	}

	/**
	 * Called to update reporting stats for a given result
	 *
	 * @param checkData
	 * @param addrResults
	 */
	private synchronized void updateStats(MDCheckData checkData, MDCheckCVRequest.AddrResults addrResults) {
		// De-reference results and meta data
		Set<String> resultCodes = addrResults.resultCodes;
		ReportStats numAddrValidations = checkData.numAddrValidations;
		ReportStats numAddrChanges = checkData.numAddrChanges;
		ReportStats numAddrErrors = checkData.numAddrErrors;
		ReportStats numAddrOverview = checkData.numAddrOverview;
		MDCheckMeta checkMeta = data.getMeta();
		// Test for specific result codes
		boolean USPSVerified = resultCodes.contains("AS01");
		boolean partialUSPSVerified = resultCodes.contains("AS02");
		boolean deliveryPlusVerified = resultCodes.contains("AS03");
		boolean unverifiedGlobal = resultCodes.contains("AS09");
		boolean addressUpdated = resultCodes.contains("AS13");
		boolean suiteAppended = resultCodes.contains("AS14");
		boolean apartmentAppended = resultCodes.contains("AS15");
		boolean vacant = resultCodes.contains("AS16");
		boolean doesNotReceiveMail = resultCodes.contains("AS17");
		boolean altNameChanged = resultCodes.contains("AC04");
		boolean aliasChanged = resultCodes.contains("AC05");
		boolean addrLines1And2Swapped = resultCodes.contains("AC06");
		boolean addrLine1CompanySwapped = resultCodes.contains("AC07");
		boolean badPostalCode = resultCodes.contains("AE01");
		boolean unknownStreet = resultCodes.contains("AE02");
		boolean unknownAddrComponent = resultCodes.contains("AE03");
		boolean nonDeliverableAddr = resultCodes.contains("AE04");
		boolean multipleAddrsMatched = resultCodes.contains("AE05");
		boolean minimumAddrNotFound = resultCodes.contains("AE07");
		boolean suiteAptBad = resultCodes.contains("AE08");
		boolean suiteAptMissing = resultCodes.contains("AE09");
		boolean houseNumBad = resultCodes.contains("AE10");
		boolean houseNumMissing = resultCodes.contains("AE11");
		boolean boxNumBad = resultCodes.contains("AE12");
		boolean boxNumMissing = resultCodes.contains("AE13");
		boolean pmbMissing = resultCodes.contains("AE14");
		boolean suiteAptNotRequired = resultCodes.contains("AE17");
		boolean receivesMail = !doesNotReceiveMail;
		boolean notVacant = !vacant;
		// Check for address country
		boolean inUS = addrResults.CountryAbbreviation.contains("US");
		boolean inCanada = addrResults.CountryAbbreviation.contains("CA");
		/* Gather stats on the results */
		// Track verified US addresses that are not vacant and receive mail
		if (USPSVerified && notVacant && receivesMail && inUS) {
			numAddrValidations.inc(checkMeta.addrValidationReportCStat[0]);
		}
		// Track partially verified addresses that are not vacant and receive mail
		// TODO: Same test below?
		if (partialUSPSVerified && notVacant && receivesMail) {
			numAddrValidations.inc(checkMeta.addrValidationReportCStat[1]);
			numAddrOverview.inc(checkMeta.addrOverviewReportCStat[3]);
		}
		// Track verified Canadian addresses
		if (USPSVerified && inCanada) {
			numAddrValidations.inc(checkMeta.addrValidationReportCStat[2]);
		}
		// Track residencies that do not receive mail
		if (doesNotReceiveMail && notVacant) {
			numAddrValidations.inc(checkMeta.addrValidationReportCStat[3]);
		}
		// Track changes that involved appending a suite or apartment number
		if (suiteAppended || apartmentAppended) {
			numAddrChanges.inc(checkMeta.addrChangeReportCStat[2]);
		}
		// Track address updates
		if (addressUpdated || altNameChanged || aliasChanged) {
			numAddrChanges.inc(checkMeta.addrChangeReportCStat[3]);
		}
		// Track address line swaps
		if (addrLines1And2Swapped || addrLine1CompanySwapped) {
			numAddrChanges.inc(checkMeta.addrChangeReportCStat[4]);
		}
		// Track malformed addresses and/or postal codes
		if (badPostalCode || minimumAddrNotFound) {
			numAddrErrors.inc(checkMeta.errorReportCStat[1]);
			numAddrOverview.inc(checkMeta.addrOverviewReportCStat[5]);
		}
		// Track other misc. address problems
		if (unknownStreet || unknownAddrComponent || nonDeliverableAddr || multipleAddrsMatched || houseNumBad || houseNumMissing || boxNumBad || boxNumMissing) {
			numAddrErrors.inc(checkMeta.errorReportCStat[2]);
		}
		// Track suite/apartment/private mailbox problems
		if (suiteAptBad || suiteAptMissing || pmbMissing || suiteAptNotRequired) {
			numAddrErrors.inc(checkMeta.errorReportCStat[3]);
		}
		// Track global addresses
		if (unverifiedGlobal) {
			numAddrOverview.inc(checkMeta.addrOverviewReportCStat[2]);
		}
		// Track partially verified addresses that are not vacant and receive mail
		// TODO: Same test above?
		if (partialUSPSVerified && notVacant && receivesMail) {
			numAddrOverview.inc(checkMeta.addrOverviewReportCStat[3]);
		}
		// Track addresses that are vacant or do not receive mail
		if (vacant || doesNotReceiveMail) {
			numAddrOverview.inc(checkMeta.addrOverviewReportCStat[4]);
		}
		// Deal with errors and changes
		boolean errorFound = false;
		boolean changeFound = false;
		for (String code : resultCodes) {
			if (changeCodes.contains(code)) {
				// Track total number of changes found (multiple per record possible)
				numAddrChanges.inc(checkMeta.addrChangeReportCStat[1]);
				changeFound = true;
			}
			if (errorCodes.contains(code)) {
				errorFound = true;
			}
		}
		/*
		 * if(errorFound){
		 * Integer newNum = checkData.numAddrOverview.get(data.getMeta().addrOverviewReportCStat[6]);
		 * if(newNum != null){
		 * newNum = newNum + 1;
		 * }
		 * else{
		 * newNum = new Integer(1);
		 * }
		 * checkData.numAddrOverview.remove(data.getMeta().addrOverviewReportCStat[6]);
		 * checkData.numAddrOverview.put(data.getMeta().addrOverviewReportCStat[6], newNum);
		 * }
		 */
		// Track a record with one or more errors
		if (errorFound) {
			numAddrErrors.inc(checkMeta.errorReportCStat[0]);
		}
		// Track a record with one or more changes
		if (changeFound) {
			numAddrChanges.inc(checkMeta.addrChangeReportCStat[0]);
		}
		// A deliverable address with at least one change detected
		if ((USPSVerified || deliveryPlusVerified) && (notVacant && receivesMail)) {
			// ... with at least one change detected
			if (changeFound) {
				numAddrOverview.inc(checkMeta.addrOverviewReportCStat[1]);
			} else {
				numAddrOverview.inc(checkMeta.addrOverviewReportCStat[0]);
			}
		}
		// Add result codes to reporting result stats
		for (String resultCode : resultCodes) {
			checkData.resultStats.inc(resultCode);
		}
	}
}
