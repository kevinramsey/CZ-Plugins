package com.melissadata.kettle.sm;

import java.util.List;

import com.melissadata.kettle.MDCheckData;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.sm.request.SmartMoverAddressVerifyWebRequestHandler;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.sm.service.NCOAService;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;

//import static junit.framework.Assert.assertTrue;

public class SmartMoverMeta implements Cloneable {
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

	public enum ProcessingType {
		Standard(BaseMessages.getString(PKG, "MDCheckMeta.ProcessingType.Standard")),
		IndividualAndBusiness(BaseMessages.getString(PKG, "MDCheckMeta.ProcessingType.IndividualAndBusiness")),
		Individual(BaseMessages.getString(PKG, "MDCheckMeta.ProcessingType.Individual")),
		Business(BaseMessages.getString(PKG, "MDCheckMeta.ProcessingType.Business")),
		Residential(BaseMessages.getString(PKG, "MDCheckMeta.ProcessingType.Residential")), ;
		public static ProcessingType decode(String value) throws KettleException {
			try {
				return ProcessingType.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.ProcessingType.Unknown") + value, e);
			}
		}
		private String	description;

		private ProcessingType(String description) {
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

	private static boolean isBlank(String string) {
		return (string == null) || (string.trim().length() == 0);
	}
	private static Class<?>		PKG										= SmartMoverMeta.class;
	public static final int		MAIL_FREQUENCY_MIN						= 1;
	public static final int		MAIL_FREQUENCY_DEFAULT					= 1;
	public static final int		MAIL_FREQUENCY_MAX						= 52;
	public static final int		MONTHS_REQUESTED_MIN					= 6;
	public static final int		MONTHS_REQUESTED_DEFAULT				= 48;
	public static final int		MONTHS_REQUESTED_MAX					= 48;
	private static final String	TAG_SMART_MOVER							= "smart_mover";
	private static final String	TAG_INPUT_USE_FULL_NAME					= "input_use_full_name";
	private static final String	TAG_INPUT_FULL_NAME						= "input_full_name";
	private static final String	TAG_INPUT_NAME_PREFIX					= "input_name_prefix";
	private static final String	TAG_INPUT_NAME_FIRST					= "input_name_first";
	private static final String	TAG_INPUT_NAME_MIDDLE					= "input_name_middle";
	private static final String	TAG_INPUT_NAME_LAST						= "input_name_last";
	private static final String	TAG_INPUT_NAME_SUFFIX					= "input_name_suffix";
	private static final String	TAG_INPUT_ADDR_COMPANY					= "input_addr_company";
	private static final String	TAG_INPUT_ADDR_LINE						= "input_addr_line";
	private static final String	TAG_INPUT_ADDR_LINE2					= "input_addr_line2";
	private static final String	TAG_INPUT_ADDR_SUITE					= "input_addr_suite";
	private static final String	TAG_INPUT_ADDR_PMB						= "input_addr_pmb";
	private static final String	TAG_INPUT_ADDR_URBANIZATION				= "input_addr_urbanization";
	private static final String	TAG_INPUT_ADDR_CITY						= "input_addr_city";
	private static final String	TAG_INPUT_ADDR_STATE					= "input_addr_state";
	private static final String	TAG_INPUT_ADDR_ZIP						= "input_addr_zip";
	private static final String	TAG_INPUT_ADDR_PLUS4					= "input_addr_plus4";
	private static final String	TAG_INPUT_ADDR_COUNTRY					= "input_addr_country";
	private static final String	TAG_OUTPUT_FULL_NAME					= "output_full_name";
	private static final String	TAG_OUTPUT_NAME_PREFIX					= "output_name_prefix";
	private static final String	TAG_OUTPUT_NAME_FIRST					= "output_name_first";
	private static final String	TAG_OUTPUT_NAME_MIDDLE					= "output_name_middle";
	private static final String	TAG_OUTPUT_NAME_LAST					= "output_name_last";
	private static final String	TAG_OUTPUT_NAME_SUFFIX					= "output_name_suffix";
	private static final String	TAG_OUTPUT_ADDR_COMPANY					= "output_addr_company";
	private static final String	TAG_OUTPUT_ADDR_LINE					= "output_addr_line";
	private static final String	TAG_OUTPUT_ADDR_LINE2					= "output_addr_line2";
	private static final String	TAG_OUTPUT_ADDR_SUITE					= "output_addr_suite";
	private static final String	TAG_OUTPUT_ADDR_PMB						= "output_addr_pmb";
	private static final String	TAG_OUTPUT_ADDR_URBANIZATION			= "output_addr_urbanization";
	private static final String	TAG_OUTPUT_ADDR_CITY					= "output_addr_city";
	private static final String	TAG_OUTPUT_ADDR_STATE					= "output_addr_state";
	private static final String	TAG_OUTPUT_ADDR_ZIP						= "output_addr_zip";
	private static final String	TAG_OUTPUT_ADDR_PLUS4					= "output_addr_plus4";
	private static final String	TAG_OUTPUT_ADDR_COUNTRY					= "output_addr_country";
	private static final String	TAG_OUTPUT_ADDR_DP_AND_CHECK_DIGIT		= "output_addr_dp_and_check_digit";
	private static final String	TAG_OUTPUT_ADDR_CARRIER_ROUTE			= "output_addr_carrier_route";
	// private static final String TAG_OUTPUT_ADDR_DPV_CMRA = "output_addr_dpv_cmra";
	private static final String	TAG_OUTPUT_ADDR_KEY						= "output_addr_key";
	private static final String	TAG_OUTPUT_MELISSA_ADDR_KEY						= "output_melissa_addr_key";
	private static final String	TAG_OUTPUT_BASE_MELISSAADDR_KEY						= "output_base_melissa_addr_key";
	// private static final String TAG_OUTPUT_ADDR_TYPE = "output_addr_type";
// private static final String TAG_OUTPUT_ADDR_TYPE_STRING = "output_addr_type_string";
// private static final String TAG_OUTPUT_ADDR_ZIP_TYPE = "output_addr_zip_type";
// private static final String TAG_OUTPUT_ADDR_ZIP_TYPE_STRING = "output_addr_zip_type_string";
	private static final String	TAG_OUTPUT_ADDR_CITY_ABBREVIATION		= "output_addr_city_abbreviation";
	private static final String	TAG_OUTPUT_ADDR_COUNTRY_ABBREVIATION	= "output_addr_country_abbreviation";
	private static final String	TAG_OUTPUT_PARSED_ADDR_RANGE			= "output_parsed_addr_range";
	private static final String	TAG_OUTPUT_PARSED_PRE_DIRECTIONAL		= "output_parsed_pre_directional";
	private static final String	TAG_OUTPUT_PARSED_STREET_NAME			= "output_parsed_street_name";
	private static final String	TAG_OUTPUT_PARSED_SUFFIX				= "output_parsed_suffix";
	private static final String	TAG_OUTPUT_PARSED_POST_DIRECTIONAL		= "output_parsed_post_directional";
	private static final String	TAG_OUTPUT_PARSED_SUITE_NAME			= "output_parsed_suite_name";
	private static final String	TAG_OUTPUT_PARSED_SUITE_RANGE			= "output_parsed_suite_range";
	private static final String	TAG_OUTPUT_PARSED_PMB_NAME				= "output_parsed_pmb_name";
	private static final String	TAG_OUTPUT_PARSED_PMB_RANGE				= "output_parsed_pmb_range";
	private static final String	TAG_OUTPUT_PARSED_EXTRA_INFO			= "output_parsed_extra_info";
	// private static final String TAG_OUTPUT_PARSED_ROUTE_SERVICE = "output_parsed_route_service";
// private static final String TAG_OUTPUT_PARSED_LOCK_BOX = "output_parsed_lock_box";
// private static final String TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION = "output_parsed_delivery_installation";
	private static final String	TAG_OUTPUT_DPV_FOOTNOTES				= "output_move_dpv_footnotes";
	private static final String	TAG_OUTPUT_MOVE_TYPE_CODE				= "output_move_type_code";
	private static final String	TAG_OUTPUT_MOVE_RETURN_CODE				= "output_move_retur_code";
	private static final String	TAG_OUTPUT_EFFECTIVE_DATE				= "output_effective_date";
	private static final String	TAG_OPT_COUNTRIES						= "opt_countries";
	private static final String	TAG_OPT_PROCESSING_TYPE					= "opt_processing_type";
	private static final String	TAG_OPT_MAIL_FREQUENCY					= "opt_mail_frequency";
	private static final String	TAG_OPT_MONTHS_REQUESTED				= "opt_months_requested";
	private static final String	TAG_OPT_SUMMARY_REPORTS					= "opt_summary_reports";
	private static final String	TAG_OPT_LIST_NAME						= "opt_list_name";
	private static final String	TAG_OPT_NCOA_FILE						= "opt_ncoa_file";
	private static final String	TAG_OPT_CASS_FILE						= "opt_cass_file";
	private static final String	TAG_OPT_JOB_OVERRIDE					= "opt_job_override";
	private static final String	TAG_OPT_JOB_OVERRIDE_ID					= "opt_job_override_id";
	private static final String	TAG_OPT_PAF_ID							= "opt_paf_id";
	// setup the default size for the outputs
	private static final int	MD_SIZE_FULLNAME						= 40;
	private static final int	MD_SIZE_PREFIX							= 20;
	private static final int	MD_SIZE_FIRSTNAME						= 35;
	private static final int	MD_SIZE_MIDDLENAME						= 20;
	private static final int	MD_SIZE_LASTNAME						= 35;
	private static final int	MD_SIZE_SUFFIX							= 20;
	private static final int	MD_SIZE_COMPANY							= 40;
	private static final int	MD_SIZE_ADDRESS							= 50;
	private static final int	MD_SIZE_ADDRESS2						= 50;
	private static final int	MD_SIZE_SUITE							= 20;
	private static final int	MD_SIZE_URBANIZATION					= 15;
	private static final int	MD_SIZE_CITY							= 35;
	private static final int	MD_SIZE_STATE							= 15;
	private static final int	MD_SIZE_ZIP								= 10;
	private static final int	MD_SIZE_PLUS4							= 4;
	private static final int	MD_SIZE_COUNTRY							= 30;
	private static final int	MD_SIZE_DELIVERYPOINT					= 3;
	private static final int	MD_SIZE_CRRT							= 4;
	private static final int	MD_SIZE_ADDRESSKEY						= 11;
	private static final int	MD_SIZE_MOVEDATE						= 8;
	private static final int	MD_SIZE_MOVETYPECODE					= 8;
	private static final int	MD_SIZE_MOVERETURNCODE					= 8;
	private static final int	MD_SIZE_DPVFOOTOTES						= 8;
	private static final int	MD_SIZE_PRIVATE_MAILBOX					= 15;
	private static final int	MD_SIZE_CITY_ABBR						= 13;
	private static final int	MD_SIZE_COUNTRY_ABBR					= 10;
	private static final int	MD_SIZE_PARSEDRANGE						= 10;
	private static final int	MD_SIZE_PARSEDPREDIR					= 5;
	private static final int	MD_SIZE_PARSEDNAME						= 40;
	private static final int	MD_SIZE_PARSEDSUFFIX					= 10;
	private static final int	MD_SIZE_PARSEDPOSTDIR					= 5;
	private static final int	MD_SIZE_PARSEDSUITENAME					= 10;
	private static final int	MD_SIZE_PARSEDSUITERANGE				= 10;
	private static final int	MD_SIZE_PARSEDPRIVATE_MAILBOX_NAME		= 10;
	private static final int	MD_SIZE_PARSEDPRIVATE_MAILBOX_RANGE		= 10;
	private static final int	MD_SIZE_PARSEDGARBAGE					= 30;
	private boolean				inputUseFullName;
	private String				inputFullName;
	private String				inputNamePrefix;
	private String				inputNameFirst;
	private String				inputNameMiddle;
	private String				inputNameLast;
	private String				inputNameSuffix;
	private String				inputAddrCompany;
	private String				inputAddrLine;
	private String				inputAddrLine2;
	private String				inputAddrSuite;
	private String				inputAddrPMB;
	private String				inputAddrUrbanization;
	private String				inputAddrCity;
	private String				inputAddrState;
	private String				inputAddrZip;
	private String				inputAddrPlus4;
	private String				inputAddrCountry;
	private String				outputFullName;
	private String				outputNamePrefix;
	private String				outputNameFirst;
	private String				outputNameMiddle;
	private String				outputNameLast;
	private String				outputNameSuffix;
	private String				outputAddrCompany;
	private String				outputAddrLine;
	private String				outputAddrLine2;
	private String				outputAddrSuite;
	private String				outputAddrPMB;
	private String				outputAddrUrbanization;
	private String				outputAddrCity;
	private String				outputAddrState;
	private String				outputAddrZip;
	private String				outputAddrPlus4;
	private String				outputAddrCountry;
	private String				outputAddrDPAndCheckDigit;
	private String				outputAddrCarrierRoute;
// private String outputAddrDPVCMRA;
	private String				outputAddrKey;
	private String				outputMelissaAddrKey;
	private String				outputBaseMelissaAddrKey;
	// private String outputAddrType;
// private String outputAddrTypeString;
// private String outputAddrZipType;
// private String outputAddrZipTypeString;
	private String				outputAddrCityAbbreviation;
	private String				outputAddrCountryAbbreviation;
	private String				outputParsedAddrRange;
	private String				outputParsedPreDirectional;
	private String				outputParsedStreetName;
	private String				outputParsedSuffix;
	private String				outputParsedPostDirectional;
	private String				outputParsedSuiteName;
	private String				outputParsedSuiteRange;
	private String				outputParsedPMBName;
	private String				outputParsedPMBRange;
	private String				outputParsedExtraInfo;
	// private String outputParsedRouteService;
// private String outputParsedLockBox;
// private String outputParsedDeliveryInstallation;
	private String				outputDPVFootnotes;
	private String				outputEffectiveDate;
	private String				outputMoveTypeCode;
	private String				outputMoveReturnCode;
	private Countries			optCountries;
	private ProcessingType		optProcessingType;
	private int					optMailFrequency;
	private int					optMonthsRequested;
	private boolean				optSummaryReports;
	private String				optListName;
	private String				optNCOAFile;
	private String				optCASSFile;
	private boolean				optJobOverride;
	private String				optJobOverrideId;
	private String				optPAFId;
	private String				jobID;
	private String				executionID;
	private int					fieldsAdded;
	// Info set during processing
	public String                         webSmMsg             = "";
	public String                         webSmVersion         = "";
	public NCOAService.NCOACustomerStatus ncoaCustomerStatus   = NCOAService.NCOACustomerStatus.Unknown;
	public NCOAService.NCOAPAFStatus      ncoaPAFStatus        = NCOAService.NCOAPAFStatus.Unknown;
	public NCOAService.NCOAPackageStatus  ncoaPackageStatus    = NCOAService.NCOAPackageStatus.Unknown;
	public String[]                       webSmartMoverMsg     = new String[] { "", "" };
	public String[]                       webSmartMoverVersion = new String[] { "", "" };
	// Exceptions detected during processing
	public KettleException		webSMException;
	public KettleException[]	webSmartMoverException					= new KettleException[2];


	public SmartMoverMeta() {
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
		// TODO: Do something here?
	}

	@Override
	public SmartMoverMeta clone() throws CloneNotSupportedException {
		return (SmartMoverMeta) super.clone();
	}

	public String getExecutionID() {
		return executionID;
	}

	/**
	 * Called to determine the address verifier output fields that will be included in the step outout record
	 *
	 * NOTE: Order of fields must match the order of fields in COA response processor
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {
		int start = row.size();
		// There are output fields only if the input is enabled
		if (isEnabled()) {
			// General output fields
			if (!Const.isEmpty(outputFullName)) {
				MDCheckMeta.getStringField(row, originName, space, outputFullName, MD_SIZE_FULLNAME);
			}
			if (!Const.isEmpty(outputNamePrefix)) {
				MDCheckMeta.getStringField(row, originName, space, outputNamePrefix, MD_SIZE_PREFIX);
			}
			if (!Const.isEmpty(outputNameFirst)) {
				MDCheckMeta.getStringField(row, originName, space, outputNameFirst, MD_SIZE_FIRSTNAME);
			}
			if (!Const.isEmpty(outputNameMiddle)) {
				MDCheckMeta.getStringField(row, originName, space, outputNameMiddle, MD_SIZE_MIDDLENAME);
			}
			if (!Const.isEmpty(outputNameLast)) {
				MDCheckMeta.getStringField(row, originName, space, outputNameLast, MD_SIZE_LASTNAME);
			}
			if (!Const.isEmpty(outputNameSuffix)) {
				MDCheckMeta.getStringField(row, originName, space, outputNameSuffix, MD_SIZE_SUFFIX);
			}
			if (!Const.isEmpty(outputAddrCompany)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrCompany, MD_SIZE_COMPANY);
			}
			if (!Const.isEmpty(outputAddrLine)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrLine, MD_SIZE_ADDRESS);
			}
			if (!Const.isEmpty(outputAddrLine2)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrLine2, MD_SIZE_ADDRESS2);
			}
			if (!Const.isEmpty(outputAddrSuite)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrSuite, MD_SIZE_SUITE);
			}
			if (!Const.isEmpty(outputAddrPMB)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrPMB, MD_SIZE_PRIVATE_MAILBOX);
			}
			if (!Const.isEmpty(outputAddrUrbanization)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrUrbanization, MD_SIZE_URBANIZATION);
			}
			if (!Const.isEmpty(outputAddrCity)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrCity, MD_SIZE_CITY);
			}
			if (!Const.isEmpty(outputAddrState)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrState, MD_SIZE_STATE);
			}
			if (!Const.isEmpty(outputAddrZip)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrZip, MD_SIZE_ZIP);
			}
			if (!Const.isEmpty(outputAddrPlus4)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrPlus4, MD_SIZE_PLUS4);
			}
			if (!Const.isEmpty(outputAddrCountry)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrCountry, MD_SIZE_COUNTRY);
			}
			if (!Const.isEmpty(outputAddrDPAndCheckDigit)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrDPAndCheckDigit, MD_SIZE_DELIVERYPOINT);
			}
			if (!Const.isEmpty(outputAddrCarrierRoute)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrCarrierRoute, MD_SIZE_CRRT);
			}
			// if (!Const.isEmpty(outputAddrDPVCMRA))
			// MDCheckMeta.getStringField(row, originName, space, outputAddrDPVCMRA);
			if (!Const.isEmpty(outputAddrKey)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrKey, MD_SIZE_ADDRESSKEY);
			}
			if (!Const.isEmpty(outputMelissaAddrKey)) {
				MDCheckMeta.getStringField(row, originName, space, outputMelissaAddrKey, MD_SIZE_ADDRESSKEY);
			}
			if (!Const.isEmpty(outputBaseMelissaAddrKey)) {
				MDCheckMeta.getStringField(row, originName, space, outputBaseMelissaAddrKey, MD_SIZE_ADDRESSKEY);
			}
			// if (!Const.isEmpty(outputAddrType))
			// MDCheckMeta.getStringField(row, originName, space, outputAddrType);
			// if (!Const.isEmpty(outputAddrTypeString))
			// MDCheckMeta.getStringField(row, originName, space, outputAddrTypeString);
			// if (!Const.isEmpty(outputAddrZipType))
			// MDCheckMeta.getStringField(row, originName, space, outputAddrZipType);
			// if (!Const.isEmpty(outputAddrZipTypeString))
			// MDCheckMeta.getStringField(row, originName, space, outputAddrZipTypeString);
			if (!Const.isEmpty(outputAddrCityAbbreviation)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrCityAbbreviation, MD_SIZE_CITY_ABBR);
			}
			if (!Const.isEmpty(outputAddrCountryAbbreviation)) {
				MDCheckMeta.getStringField(row, originName, space, outputAddrCountryAbbreviation, MD_SIZE_COUNTRY_ABBR);
			}
			if (!Const.isEmpty(outputParsedAddrRange)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedAddrRange, MD_SIZE_PARSEDRANGE);
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
			if (!Const.isEmpty(outputParsedExtraInfo)) {
				MDCheckMeta.getStringField(row, originName, space, outputParsedExtraInfo, MD_SIZE_PARSEDGARBAGE);
			}
			// if (!Const.isEmpty(outputParsedRouteService))
			// MDCheckMeta.getStringField(row, originName, space, outputParsedRouteService);
			// if (!Const.isEmpty(outputParsedLockBox))
			// MDCheckMeta.getStringField(row, originName, space, outputParsedLockBox);
			// if (!Const.isEmpty(outputParsedDeliveryInstallation))
			// MDCheckMeta.getStringField(row, originName, space, outputParsedDeliveryInstallation);
			if (!Const.isEmpty(outputEffectiveDate)) {
				MDCheckMeta.getStringField(row, originName, space, outputEffectiveDate, MD_SIZE_MOVEDATE);
			}
			if (!Const.isEmpty(outputMoveTypeCode)) {
				MDCheckMeta.getStringField(row, originName, space, outputMoveTypeCode, MD_SIZE_MOVETYPECODE);
			}
			if (!Const.isEmpty(outputMoveReturnCode)) {
				MDCheckMeta.getStringField(row, originName, space, outputMoveReturnCode, MD_SIZE_MOVERETURNCODE);
			}
			if (!Const.isEmpty(outputDPVFootnotes)) {
				MDCheckMeta.getStringField(row, originName, space, outputDPVFootnotes, MD_SIZE_DPVFOOTOTES);
			}
		}
		// Keep a count of the number of fields we add
		fieldsAdded = row.size() - start;
	}

	/**
	 * @return The number of fields added by this sub-step
	 */
	public int getFieldsAdded() {
		return fieldsAdded;
	}

	public String getInputAddrCity() {
		return inputAddrCity;
	}

	public String getInputAddrCompany() {
		return inputAddrCompany;
	}

	public String getInputAddrCountry() {
		return inputAddrCountry;
	}

	public String getInputAddrLine() {
		return inputAddrLine;
	}

	public String getInputAddrLine2() {
		return inputAddrLine2;
	}

	public String getInputAddrPlus4() {
		return inputAddrPlus4;
	}

	public String getInputAddrPMB() {
		return inputAddrPMB;
	}

	public String getInputAddrState() {
		return inputAddrState;
	}

	public String getInputAddrSuite() {
		return inputAddrSuite;
	}

	public String getInputAddrUrbanization() {
		return inputAddrUrbanization;
	}

	public String getInputAddrZip() {
		return inputAddrZip;
	}

	public String getInputFullName() {
		return inputFullName;
	}

	public String getInputNameFirst() {
		return inputNameFirst;
	}

	public String getInputNameLast() {
		return inputNameLast;
	}

	public String getInputNameMiddle() {
		return inputNameMiddle;
	}

	public String getInputNamePrefix() {
		return inputNamePrefix;
	}

	public String getInputNameSuffix() {
		return inputNameSuffix;
	}

	public boolean getInputUseFullName() {
		return inputUseFullName;
	}

	public String getJobID() {
		return jobID;
	}

	public String getMoveReturnCode() {
		return outputMoveReturnCode;
	}

	public String getMoveTypeCode() {
		return outputMoveTypeCode;
	}

	public String getOptionCASSFile() {
		return optCASSFile;
	}

	public Countries getOptionCountries() {
		return optCountries;
	}

	public String getOptionListName() {
		return optListName;
	}

	public int getOptionMailFrequency() {
		return optMailFrequency;
	}

	public int getOptionMonthsRequest() {
		return optMonthsRequested;
	}

	public String getOptionNCOAFile() {
		return optNCOAFile;
	}

	public ProcessingType getOptionProcessingType() {
		return optProcessingType;
	}

	public boolean getOptionSummaryReports() {
		return optSummaryReports;
	}

	public String getOptJobOverrideId() {
		return optJobOverrideId;
	}

	public String getOptPAFId() {
		return optPAFId;
	}

	public String getOutputAddrCarrierRoute() {
		return outputAddrCarrierRoute;
	}

	public String getOutputAddrCity() {
		return outputAddrCity;
	}

	public String getOutputAddrCityAbbreviation() {
		return outputAddrCityAbbreviation;
	}

	public String getOutputAddrCompany() {
		return outputAddrCompany;
	}

	public String getOutputAddrCountry() {
		return outputAddrCountry;
	}

	public String getOutputAddrCountryAbbreviation() {
		return outputAddrCountryAbbreviation;
	}

	public String getOutputAddrDPAndCheckDigit() {
		return outputAddrDPAndCheckDigit;
	}

	public String getOutputAddrKey() {
		return outputAddrKey;
	}

	public String getOutputMelissaAddrKey() {
		return outputMelissaAddrKey;
	}

	public String getOutputBaseMelissaAddrKey() {
		return outputBaseMelissaAddrKey;
	}

	public String getOutputAddrLine() {
		return outputAddrLine;
	}

	public String getOutputAddrLine2() {
		return outputAddrLine2;
	}

	public String getOutputAddrPlus4() {
		return outputAddrPlus4;
	}

	public String getOutputAddrPMB() {
		return outputAddrPMB;
	}

	public String getOutputAddrState() {
		return outputAddrState;
	}

	public String getOutputAddrSuite() {
		return outputAddrSuite;
	}

	public String getOutputAddrUrbanization() {
		return outputAddrUrbanization;
	}

	public String getOutputAddrZip() {
		return outputAddrZip;
	}

	public String getOutputDPVFootnotes() {
		return outputDPVFootnotes;
	}

	public String getOutputEffectiveDate() {
		return outputEffectiveDate;
	}

	public String getOutputFullName() {
		return outputFullName;
	}

	public String getOutputNameFirst() {
		return outputNameFirst;
	}

	public String getOutputNameLast() {
		return outputNameLast;
	}

	public String getOutputNameMiddle() {
		return outputNameMiddle;
	}

	public String getOutputNamePrefix() {
		return outputNamePrefix;
	}

	public String getOutputNameSuffix() {
		return outputNameSuffix;
	}

	public String getOutputParsedAddrRange() {
		return outputParsedAddrRange;
	}

	public String getOutputParsedExtraInfo() {
		return outputParsedExtraInfo;
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

	/**
	 * @return The handler for calls to the address verification service
	 */
	public WebRequestHandler getWebRequestHandler() {
		return new SmartMoverAddressVerifyWebRequestHandler(this);
	}


	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_SMART_MOVER)).append(Const.CR);
		// Input
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_USE_FULL_NAME, Boolean.toString(inputUseFullName)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_FULL_NAME, inputFullName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_NAME_PREFIX, inputNamePrefix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_NAME_FIRST, inputNameFirst));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_NAME_MIDDLE, inputNameMiddle));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_NAME_LAST, inputNameLast));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_NAME_SUFFIX, inputNameSuffix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_COMPANY, inputAddrCompany));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_LINE, inputAddrLine));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_LINE2, inputAddrLine2));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_SUITE, inputAddrSuite));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_PMB, inputAddrPMB));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_URBANIZATION, inputAddrUrbanization));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_CITY, inputAddrCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_STATE, inputAddrState));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_ZIP, inputAddrZip));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_PLUS4, inputAddrPlus4));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_ADDR_COUNTRY, inputAddrCountry));
		// Output
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_FULL_NAME, outputFullName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_NAME_PREFIX, outputNamePrefix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_NAME_FIRST, outputNameFirst));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_NAME_MIDDLE, outputNameMiddle));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_NAME_LAST, outputNameLast));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_NAME_SUFFIX, outputNameSuffix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_COMPANY, outputAddrCompany));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_LINE, outputAddrLine));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_LINE2, outputAddrLine2));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_SUITE, outputAddrSuite));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_PMB, outputAddrPMB));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_URBANIZATION, outputAddrUrbanization));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_CITY, outputAddrCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_STATE, outputAddrState));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_ZIP, outputAddrZip));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_PLUS4, outputAddrPlus4));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_COUNTRY, outputAddrCountry));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_DP_AND_CHECK_DIGIT, outputAddrDPAndCheckDigit));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_CARRIER_ROUTE, outputAddrCarrierRoute));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_KEY, outputAddrKey));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_MELISSA_ADDR_KEY, outputMelissaAddrKey));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_BASE_MELISSAADDR_KEY, outputBaseMelissaAddrKey));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_CITY_ABBREVIATION, outputAddrCityAbbreviation));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_ADDR_COUNTRY_ABBREVIATION, outputAddrCountryAbbreviation));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_ADDR_RANGE, outputParsedAddrRange));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_PRE_DIRECTIONAL, outputParsedPreDirectional));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_STREET_NAME, outputParsedStreetName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_SUFFIX, outputParsedSuffix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_POST_DIRECTIONAL, outputParsedPostDirectional));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_SUITE_NAME, outputParsedSuiteName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_SUITE_RANGE, outputParsedSuiteRange));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_PMB_NAME, outputParsedPMBName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_PMB_RANGE, outputParsedPMBRange));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PARSED_EXTRA_INFO, outputParsedExtraInfo));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_DPV_FOOTNOTES, outputDPVFootnotes));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_EFFECTIVE_DATE, outputEffectiveDate));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_MOVE_TYPE_CODE, outputMoveTypeCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_MOVE_RETURN_CODE, outputMoveReturnCode));
		// Options
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_COUNTRIES, optCountries.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PROCESSING_TYPE, optProcessingType.encode()));
		retval.append(tab).append("  ").append(XMLHandler.addTagValue(TAG_OPT_MAIL_FREQUENCY, optMailFrequency));
		retval.append(tab).append("  ").append(XMLHandler.addTagValue(TAG_OPT_MONTHS_REQUESTED, optMonthsRequested));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_SUMMARY_REPORTS, Boolean.toString(optSummaryReports)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_LIST_NAME, optListName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_NCOA_FILE, optNCOAFile));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_CASS_FILE, optCASSFile));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_JOB_OVERRIDE, Boolean.toString(optJobOverride)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_JOB_OVERRIDE_ID, optJobOverrideId));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PAF_ID, optPAFId));
		retval.append(tab).append(XMLHandler.closeTag(TAG_SMART_MOVER)).append(Const.CR);
		return retval.toString();
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// TODO: More to do check here?
		return true;
	}

	public boolean isOptJobOverride() {
		return optJobOverride;
	}

	/**
	 * @return true if any of the parsed output fields are defined
	 */
	public boolean needParsed() {
		return !Const.isEmpty(outputParsedAddrRange) || !Const.isEmpty(outputParsedPreDirectional) || !Const.isEmpty(outputParsedStreetName) || !Const.isEmpty(outputParsedSuffix) || !Const.isEmpty(outputParsedPostDirectional)
				|| !Const.isEmpty(outputParsedSuiteName) || !Const.isEmpty(outputParsedSuiteRange) || !Const.isEmpty(outputParsedPMBName) || !Const.isEmpty(outputParsedPMBRange) || !Const.isEmpty(outputParsedExtraInfo)
// || !Const.isEmpty(outputParsedRouteService)
// || !Const.isEmpty(outputParsedLockBox)
// || !Const.isEmpty(outputParsedDeliveryInstallation)
				;
	}

	/**
	 * Called to read name parsing meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_SMART_MOVER);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Input fields
			String value = MDCheckStepData.getTagValue(node, TAG_INPUT_USE_FULL_NAME);
			inputUseFullName = (value != null) ? Boolean.valueOf(value) : inputUseFullName;
			inputFullName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_FULL_NAME), inputFullName);
			inputNamePrefix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_NAME_PREFIX), inputNamePrefix);
			inputNameFirst = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_NAME_FIRST), inputNameFirst);
			inputNameMiddle = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_NAME_MIDDLE), inputNameMiddle);
			inputNameLast = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_NAME_LAST), inputNameLast);
			inputNameSuffix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_NAME_SUFFIX), inputNameSuffix);
			inputAddrCompany = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_COMPANY), inputAddrCompany);
			inputAddrLine = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_LINE), inputAddrLine);
			inputAddrLine2 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_LINE2), inputAddrLine2);
			inputAddrSuite = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_SUITE), inputAddrSuite);
			inputAddrPMB = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_PMB), inputAddrPMB);
			inputAddrUrbanization = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_URBANIZATION), inputAddrUrbanization);
			inputAddrCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_CITY), inputAddrCity);
			inputAddrState = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_STATE), inputAddrState);
			inputAddrZip = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_ZIP), inputAddrZip);
			inputAddrPlus4 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_PLUS4), inputAddrPlus4);
			inputAddrCountry = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_ADDR_COUNTRY), inputAddrCountry);
			// Output fields
			outputFullName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_FULL_NAME), outputFullName);
			outputNamePrefix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_NAME_PREFIX), outputNamePrefix);
			outputNameFirst = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_NAME_FIRST), outputNameFirst);
			outputNameMiddle = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_NAME_MIDDLE), outputNameMiddle);
			outputNameLast = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_NAME_LAST), outputNameLast);
			outputNameSuffix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_NAME_SUFFIX), outputNameSuffix);
			outputAddrCompany = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_COMPANY), outputAddrCompany);
			outputAddrLine = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_LINE), outputAddrLine);
			outputAddrLine2 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_LINE2), outputAddrLine2);
			outputAddrSuite = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_SUITE), outputAddrSuite);
			outputAddrPMB = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_PMB), outputAddrPMB);
			outputAddrUrbanization = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_URBANIZATION), outputAddrUrbanization);
			outputAddrCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_CITY), outputAddrCity);
			outputAddrState = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_STATE), outputAddrState);
			outputAddrZip = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_ZIP), outputAddrZip);
			outputAddrPlus4 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_PLUS4), outputAddrPlus4);
			outputAddrCountry = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_COUNTRY), outputAddrCountry);
			outputAddrDPAndCheckDigit = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_DP_AND_CHECK_DIGIT), outputAddrDPAndCheckDigit);
			outputAddrCarrierRoute = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_CARRIER_ROUTE), outputAddrCarrierRoute);
// outputAddrDPVCMRA = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_DPV_CMRA), outputAddrDPVCMRA);
			outputAddrKey = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_KEY), outputAddrKey);
			outputMelissaAddrKey = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_MELISSA_ADDR_KEY), outputMelissaAddrKey);
			outputBaseMelissaAddrKey = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_BASE_MELISSAADDR_KEY), outputBaseMelissaAddrKey);
// outputAddrType = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_TYPE), outputAddrType);
// outputAddrTypeString = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_TYPE_STRING),
// outputAddrTypeString);
// outputAddrZipType = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_ZIP_TYPE), outputAddrZipType);
// outputAddrZipTypeString = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_ZIP_TYPE_STRING),
// outputAddrZipTypeString);
			outputAddrCityAbbreviation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_CITY_ABBREVIATION), outputAddrCityAbbreviation);
			outputAddrCountryAbbreviation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_ADDR_COUNTRY_ABBREVIATION), outputAddrCountryAbbreviation);
			outputParsedAddrRange = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_ADDR_RANGE), outputParsedAddrRange);
			outputParsedPreDirectional = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_PRE_DIRECTIONAL), outputParsedPreDirectional);
			outputParsedStreetName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_STREET_NAME), outputParsedStreetName);
			outputParsedSuffix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_SUFFIX), outputParsedSuffix);
			outputParsedPostDirectional = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_POST_DIRECTIONAL), outputParsedPostDirectional);
			outputParsedSuiteName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_SUITE_NAME), outputParsedSuiteName);
			outputParsedSuiteRange = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_SUITE_RANGE), outputParsedSuiteRange);
			outputParsedPMBName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_PMB_NAME), outputParsedPMBName);
			outputParsedPMBRange = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_PMB_RANGE), outputParsedPMBRange);
			outputParsedExtraInfo = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_EXTRA_INFO), outputParsedExtraInfo);
// outputParsedRouteService = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_ROUTE_SERVICE),
// outputParsedRouteService);
// outputParsedLockBox = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PARSED_LOCK_BOX),
// outputParsedLockBox);
// outputParsedDeliveryInstallation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node,
// TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION), outputParsedDeliveryInstallation);
			outputDPVFootnotes = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_DPV_FOOTNOTES), outputDPVFootnotes);
			outputEffectiveDate = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_EFFECTIVE_DATE), outputEffectiveDate);
			outputMoveTypeCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_MOVE_TYPE_CODE), outputMoveTypeCode);
			outputMoveReturnCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_MOVE_RETURN_CODE), outputMoveReturnCode);
			value = MDCheckStepData.getTagValue(node, TAG_OPT_COUNTRIES);
			optCountries = (value != null) ? Countries.decode(value) : optCountries;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PROCESSING_TYPE);
			optProcessingType = (value != null) ? ProcessingType.decode(value) : optProcessingType;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_MAIL_FREQUENCY);
			optMailFrequency = (value != null) ? Integer.parseInt(value) : optMailFrequency;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_MONTHS_REQUESTED);
			optMonthsRequested = (value != null) ? Integer.parseInt(value) : optMonthsRequested;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_SUMMARY_REPORTS);
			optSummaryReports = (value != null) ? Boolean.valueOf(value) : optSummaryReports;
			optListName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OPT_LIST_NAME), optListName);
			optNCOAFile = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OPT_NCOA_FILE), optNCOAFile);
			optCASSFile = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OPT_CASS_FILE), optCASSFile);
			value = MDCheckStepData.getTagValue(node, TAG_OPT_JOB_OVERRIDE);
			optJobOverride = (value != null) ? Boolean.valueOf(value) : optJobOverride;
			optJobOverrideId = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OPT_JOB_OVERRIDE_ID), optJobOverrideId);
			optPAFId = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OPT_PAF_ID), optPAFId);
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
		String value = rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_USE_FULL_NAME);
		inputUseFullName = (value != null) ? Boolean.parseBoolean(value) : inputUseFullName;
		inputFullName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_FULL_NAME), inputFullName);
		inputNamePrefix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_PREFIX), inputNamePrefix);
		inputNameFirst = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_FIRST), inputNameFirst);
		inputNameMiddle = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_MIDDLE), inputNameMiddle);
		inputNameLast = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_LAST), inputNameLast);
		inputNameSuffix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_SUFFIX), inputNameSuffix);
		inputAddrCompany = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_COMPANY), inputAddrCompany);
		inputAddrLine = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_LINE), inputAddrLine);
		inputAddrLine2 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_LINE2), inputAddrLine2);
		inputAddrSuite = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_SUITE), inputAddrSuite);
		inputAddrPMB = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_PMB), inputAddrPMB);
		inputAddrUrbanization = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_URBANIZATION), inputAddrUrbanization);
		inputAddrCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_CITY), inputAddrCity);
		inputAddrState = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_STATE), inputAddrState);
		inputAddrZip = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_ZIP), inputAddrZip);
		inputAddrPlus4 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_PLUS4), inputAddrPlus4);
		inputAddrCountry = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_COUNTRY), inputAddrCountry);
		outputFullName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_FULL_NAME), outputFullName);
		outputNamePrefix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_PREFIX), inputNamePrefix);
		outputNameFirst = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_FIRST), outputNameFirst);
		outputNameMiddle = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_MIDDLE), outputNameMiddle);
		outputNameLast = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_LAST), outputNameLast);
		outputNameSuffix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_SUFFIX), outputNameSuffix);
		outputAddrCompany = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_COMPANY), outputAddrCompany);
		outputAddrLine = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_LINE), outputAddrLine);
		outputAddrLine2 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_LINE2), outputAddrLine2);
		outputAddrSuite = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_SUITE), outputAddrSuite);
		outputAddrPMB = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_PMB), outputAddrPMB);
		outputAddrUrbanization = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_URBANIZATION), outputAddrUrbanization);
		outputAddrCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_CITY), outputAddrCity);
		outputAddrState = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_STATE), outputAddrState);
		outputAddrZip = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_ZIP), outputAddrZip);
		outputAddrPlus4 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_PLUS4), outputAddrPlus4);
		outputAddrCountry = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_COUNTRY), outputAddrCountry);
		outputAddrDPAndCheckDigit = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_DP_AND_CHECK_DIGIT), outputAddrDPAndCheckDigit);
		outputAddrCarrierRoute = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_CARRIER_ROUTE), outputAddrCarrierRoute);
// outputAddrDPVCMRA = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." +
// TAG_OUTPUT_ADDR_DPV_CMRA), outputAddrDPVCMRA);
		outputAddrKey = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_KEY), outputAddrKey);

		outputMelissaAddrKey = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_MELISSA_ADDR_KEY), outputMelissaAddrKey);
		outputBaseMelissaAddrKey = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_BASE_MELISSAADDR_KEY), outputBaseMelissaAddrKey);
// outputAddrType = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_TYPE),
// outputAddrType);
// outputAddrTypeString = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." +
// TAG_OUTPUT_ADDR_TYPE_STRING), outputAddrTypeString);
// outputAddrZipType = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." +
// TAG_OUTPUT_ADDR_ZIP_TYPE), outputAddrZipType);
// outputAddrZipTypeString = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." +
// TAG_OUTPUT_ADDR_ZIP_TYPE_STRING), outputAddrZipTypeString);
		outputAddrCityAbbreviation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_CITY_ABBREVIATION), outputAddrCityAbbreviation);
		outputAddrCountryAbbreviation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_COUNTRY_ABBREVIATION), outputAddrCountryAbbreviation);
		outputParsedAddrRange = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_ADDR_RANGE), outputParsedAddrRange);
		outputParsedPreDirectional = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_PRE_DIRECTIONAL), outputParsedPreDirectional);
		outputParsedStreetName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_STREET_NAME), outputParsedStreetName);
		outputParsedSuffix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_SUFFIX), outputParsedSuffix);
		outputParsedPostDirectional = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_POST_DIRECTIONAL), outputParsedPostDirectional);
		outputParsedSuiteName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_SUITE_NAME), outputParsedSuiteName);
		outputParsedSuiteRange = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_SUITE_RANGE), outputParsedSuiteRange);
		outputParsedPMBName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_PMB_NAME), outputParsedPMBName);
		outputParsedPMBRange = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_PMB_RANGE), outputParsedPMBRange);
		outputParsedPMBRange = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_PMB_RANGE), outputParsedPMBRange);
		outputParsedExtraInfo = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_EXTRA_INFO), outputParsedExtraInfo);
// outputParsedRouteService = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." +
// TAG_OUTPUT_PARSED_ROUTE_SERVICE), outputParsedRouteService);
// outputParsedLockBox = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." +
// TAG_OUTPUT_PARSED_LOCK_BOX), outputParsedLockBox);
// outputParsedDeliveryInstallation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." +
// TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION), outputParsedDeliveryInstallation);
		outputDPVFootnotes = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_DPV_FOOTNOTES), outputDPVFootnotes);
		outputEffectiveDate = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_EFFECTIVE_DATE), outputEffectiveDate);
		outputMoveTypeCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_MOVE_TYPE_CODE), outputMoveTypeCode);
		outputMoveReturnCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_MOVE_RETURN_CODE), outputMoveReturnCode);
		value = rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_COUNTRIES);
		optCountries = (value != null) ? Countries.decode(value) : optCountries;
		value = rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_PROCESSING_TYPE);
		optProcessingType = (value != null) ? ProcessingType.decode(value) : optProcessingType;
		value = rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_MAIL_FREQUENCY);
		optMailFrequency = (value != null) ? Integer.parseInt(value) : optMailFrequency;
		value = rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_MONTHS_REQUESTED);
		optMonthsRequested = (value != null) ? Integer.parseInt(value) : optMonthsRequested;
		value = rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_SUMMARY_REPORTS);
		optSummaryReports = (value != null) ? Boolean.parseBoolean(value) : optSummaryReports;
		optListName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_LIST_NAME), optListName);
		optNCOAFile = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_NCOA_FILE), optNCOAFile);
		optCASSFile = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_CASS_FILE), optCASSFile);
		value = rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_JOB_OVERRIDE);
		optJobOverride = (value != null) ? Boolean.parseBoolean(value) : optJobOverride;
		optJobOverrideId = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_JOB_OVERRIDE_ID), optJobOverrideId);
		optPAFId = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_SMART_MOVER + "." + TAG_OPT_PAF_ID), optPAFId);
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
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_USE_FULL_NAME, Boolean.toString(inputUseFullName));
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_FULL_NAME, inputFullName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_PREFIX, inputNamePrefix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_FIRST, inputNameFirst);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_MIDDLE, inputNameMiddle);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_LAST, inputNameLast);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_NAME_SUFFIX, inputNameSuffix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_COMPANY, inputAddrCompany);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_LINE, inputAddrLine);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_LINE2, inputAddrLine2);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_SUITE, inputAddrSuite);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_PMB, inputAddrPMB);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_URBANIZATION, inputAddrUrbanization);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_CITY, inputAddrCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_STATE, inputAddrState);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_ZIP, inputAddrZip);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_PLUS4, inputAddrPlus4);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_INPUT_ADDR_COUNTRY, inputAddrCountry);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_FULL_NAME, outputFullName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_PREFIX, outputNamePrefix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_FIRST, outputNameFirst);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_MIDDLE, outputNameMiddle);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_LAST, outputNameLast);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_NAME_SUFFIX, outputNameSuffix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_COMPANY, outputAddrCompany);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_LINE, outputAddrLine);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_LINE2, outputAddrLine2);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_SUITE, outputAddrSuite);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_PMB, outputAddrPMB);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_URBANIZATION, outputAddrUrbanization);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_CITY, outputAddrCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_STATE, outputAddrState);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_ZIP, outputAddrZip);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_PLUS4, outputAddrPlus4);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_COUNTRY, outputAddrCountry);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_DP_AND_CHECK_DIGIT, outputAddrDPAndCheckDigit);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_CARRIER_ROUTE, outputAddrCarrierRoute);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_DPV_CMRA, outputAddrDPVCMRA);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_KEY, outputAddrKey);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_MELISSA_ADDR_KEY, outputMelissaAddrKey);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_BASE_MELISSAADDR_KEY, outputBaseMelissaAddrKey);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_TYPE, outputAddrType);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_TYPE_STRING,
// outputAddrTypeString);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_ZIP_TYPE, outputAddrZipType);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_ZIP_TYPE_STRING,
// outputAddrZipTypeString);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_CITY_ABBREVIATION, outputAddrCityAbbreviation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_ADDR_COUNTRY_ABBREVIATION, outputAddrCountryAbbreviation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_ADDR_RANGE, outputParsedAddrRange);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_PRE_DIRECTIONAL, outputParsedPreDirectional);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_STREET_NAME, outputParsedStreetName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_SUFFIX, outputParsedSuffix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_POST_DIRECTIONAL, outputParsedPostDirectional);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_SUITE_NAME, outputParsedSuiteName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_SUITE_RANGE, outputParsedSuiteRange);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_PMB_NAME, outputParsedPMBName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_PMB_RANGE, outputParsedPMBRange);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_EXTRA_INFO, outputParsedExtraInfo);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_ROUTE_SERVICE,
// outputParsedRouteService);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_LOCK_BOX, outputParsedLockBox);
// rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_PARSED_DELIVERY_INSTALLATION,
// outputParsedDeliveryInstallation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_DPV_FOOTNOTES, outputDPVFootnotes);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_EFFECTIVE_DATE, outputEffectiveDate);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_MOVE_TYPE_CODE, outputMoveTypeCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OUTPUT_MOVE_RETURN_CODE, outputMoveReturnCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_COUNTRIES, optCountries.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_PROCESSING_TYPE, optProcessingType.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_MAIL_FREQUENCY, optMailFrequency);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_MONTHS_REQUESTED, optMonthsRequested);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_SUMMARY_REPORTS, Boolean.toString(optSummaryReports));
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_LIST_NAME, optListName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_NCOA_FILE, optNCOAFile);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_CASS_FILE, optCASSFile);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_JOB_OVERRIDE, Boolean.toString(optJobOverride));
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_JOB_OVERRIDE_ID, optJobOverrideId);
		rep.saveStepAttribute(idTransformation, idStep, TAG_SMART_MOVER + "." + TAG_OPT_PAF_ID, optPAFId);
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault() {
		// input meta data (blank means not defined)
		inputUseFullName = true;
		inputFullName = "";
		inputNamePrefix = "";
		inputNameFirst = "";
		inputNameMiddle = "";
		inputNameLast = "";
		inputNameSuffix = "";
		inputAddrCompany = "";
		inputAddrLine = "";
		inputAddrLine2 = "";
		inputAddrSuite = "";
		inputAddrPMB = "";
		inputAddrUrbanization = "";
		inputAddrCity = "";
		inputAddrState = "";
		inputAddrZip = "";
		inputAddrPlus4 = "";
		inputAddrCountry = "";
		// output meta data (blank means not defined)
		if (MDCheckData.smDefaultsSet) {
			// name
			outputFullName = MDCheckData.sm_fullName;
			outputNamePrefix = MDCheckData.sm_prefix;
			outputNameFirst = MDCheckData.sm_firstName;
			outputNameMiddle = MDCheckData.sm_middleName;
			outputNameLast = MDCheckData.sm_lastName;
			outputNameSuffix = MDCheckData.sm_suffix;
			// address
			outputAddrCompany = MDCheckData.sm_company;
			outputAddrLine = MDCheckData.sm_address;
			outputAddrLine2 = MDCheckData.sm_address2;
			outputAddrCity = MDCheckData.sm_city;
			outputAddrState = MDCheckData.sm_state;
			outputAddrZip = MDCheckData.sm_zip;
			outputAddrKey = MDCheckData.sm_addressKey;
			outputMelissaAddrKey = MDCheckData.sm_MelissaAddressKey;
			outputBaseMelissaAddrKey = MDCheckData.sm_BaseMelissaAddressKey;
			outputEffectiveDate = MDCheckData.sm_effectiveDate;
			outputMoveTypeCode = MDCheckData.sm_moveTypeCode;
			outputMoveReturnCode = MDCheckData.sm_moveReturnCode;
			// additional info
			outputAddrSuite = MDCheckData.sm_Suite;
			outputAddrPMB = MDCheckData.sm_PrivateMailBox;
			outputAddrUrbanization = MDCheckData.sm_PrivateMailBox;
			outputAddrPlus4 = MDCheckData.sm_Plus4;
			outputAddrCountry = MDCheckData.sm_country;
			outputAddrDPAndCheckDigit = MDCheckData.sm_DPAndCheckDigit;
			outputAddrCarrierRoute = MDCheckData.sm_CarrierRoute;
			outputAddrCityAbbreviation = MDCheckData.sm_cityAbbrevation;
			outputAddrCountryAbbreviation = MDCheckData.sm_countryAbbreviation;
			outputParsedAddrRange = MDCheckData.sm_ParsedAddressRange;
			outputParsedPreDirectional = MDCheckData.sm_ParsedPreDirectional;
			outputParsedStreetName = MDCheckData.sm_ParsedStreetName;
			outputParsedSuffix = MDCheckData.sm_ParsedSuffix;
			outputParsedPostDirectional = MDCheckData.sm_ParsedPostDirectional;
			outputParsedSuiteName = MDCheckData.sm_ParsedSuiteName;
			outputParsedSuiteRange = MDCheckData.sm_ParsedSuiteRange;
			outputParsedPMBName = MDCheckData.sm_ParsedPMBName;
			outputParsedPMBRange = MDCheckData.sm_ParsedPMBRange;
			outputParsedExtraInfo = MDCheckData.sm_ParsedExtraInformation;
			outputDPVFootnotes = MDCheckData.sm_DpvFootnotes;
		} else {
			// name
			outputFullName = "SM_FullName";
			outputNamePrefix = "SM_Prefix";
			outputNameFirst = "SM_FirstName";
			outputNameMiddle = "SM_MiddleName";
			outputNameLast = "SM_LastName";
			outputNameSuffix = "SM_Suffix";
			// Address
			outputAddrCompany = "SM_Company";
			outputAddrLine = "SM_AddressLine1";
			outputAddrLine2 = "SM_AddressLine2";
			outputAddrCity = "SM_City";
			outputAddrState = "SM_State";
			outputAddrZip = "SM_PostalCode";
			outputAddrKey = "SM_AddressKey";
			outputMelissaAddrKey = "SM_MelissaAddressKey";
			outputBaseMelissaAddrKey = "SM_BaseMelissaAddressKey";
			outputEffectiveDate = "SM_EffectiveDate";
			outputMoveTypeCode = "SM_MoveTypeCode";
			outputMoveReturnCode = "SM_MoveReturnCode";
			// additional info
			outputAddrSuite = "SM_Suite";
			outputAddrPMB = "SM_PrivateMailbox";
			outputAddrUrbanization = "SM_Urbanization";
			outputAddrPlus4 = "SM_Plus4";
			outputAddrCountry = "SM_Country";
			outputAddrDPAndCheckDigit = "SM_DPAndCheckDigit";
			outputAddrCarrierRoute = "SM_CarrierRoute";
			// outputAddrDPVCMRA = "SM_DPVCMRA";
			// outputAddrType = "SM_AddressType";
			// outputAddrTypeString = "SM_AddressTypeString";
			// outputAddrZipType = "SM_AddressZipType";
			// outputAddrZipTypeString = "SM_AddressZipTypeString";
			outputAddrCityAbbreviation = "SM_CityAbbreviation";
			outputAddrCountryAbbreviation = "SM_CountryAbbreviation";
			outputParsedAddrRange = "SM_ParsedAddrRange";
			outputParsedPreDirectional = "SM_ParsedPreDirectional";
			outputParsedStreetName = "SM_ParsedStreetName";
			outputParsedSuffix = "SM_ParsedSuffix";
			outputParsedPostDirectional = "SM_ParsedPostDirectional";
			outputParsedSuiteName = "SM_ParsedSuiteName";
			outputParsedSuiteRange = "SM_ParsedSuiteRange";
			outputParsedPMBName = "SM_ParsedPMBName";
			outputParsedPMBRange = "SM_ParsedPMBRange";
			outputParsedExtraInfo = "SM_ParsedExtraInfo";
			// outputParsedRouteService = "SM_ParsedRouteService";
			// outputParsedLockBox = "SM_ParsedLockBox";
			// outputParsedDeliveryInstallation = "SM_ParsedDeliveryInstallation";
			outputDPVFootnotes = "SM_DPV_Footnotes";
		}
		optCountries = Countries.US;
		optProcessingType = ProcessingType.Standard;
		optMailFrequency = MAIL_FREQUENCY_DEFAULT;
		optMonthsRequested = MONTHS_REQUESTED_DEFAULT;
		optSummaryReports = false;
		optListName = "";
		optNCOAFile = "";
		optCASSFile = "";
		optJobOverride = false;
		optJobOverrideId = "";
		optPAFId = "";
	}

	public void setExecutionID(String executionID) {
		this.executionID = executionID;
	}

	public void setInputAddrCity(String s) {
		inputAddrCity = s;
	}

	public void setInputAddrCompany(String s) {
		inputAddrCompany = s;
	}

	public void setInputAddrCountry(String s) {
		inputAddrCountry = s;
	}

	public void setInputAddrLine(String s) {
		inputAddrLine = s;
	}

	public void setInputAddrLine2(String s) {
		inputAddrLine2 = s;
	}

	public void setInputAddrPlus4(String s) {
		inputAddrPlus4 = s;
	}

	public void setInputAddrPMB(String s) {
		inputAddrPMB = s;
	}

	public void setInputAddrState(String s) {
		inputAddrState = s;
	}

	public void setInputAddrSuite(String s) {
		inputAddrSuite = s;
	}

	public void setInputAddrUrbanization(String s) {
		inputAddrUrbanization = s;
	}

	public void setInputAddrZip(String s) {
		inputAddrZip = s;
	}

	public void setInputFullName(String s) {
		inputFullName = s;
	}

	public void setInputNameFirst(String s) {
		inputNameFirst = s;
	}

	public void setInputNameLast(String s) {
		inputNameLast = s;
	}

	public void setInputNameMiddle(String s) {
		inputNameMiddle = s;
	}

	public void setInputNamePrefix(String s) {
		inputNamePrefix = s;
	}

	public void setInputNameSuffix(String s) {
		inputNameSuffix = s;
	}

	public void setInputUseFullName(boolean b) {
		inputUseFullName = b;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public void setMoveReturnCode(String moveReturnCode) {
		outputMoveReturnCode = moveReturnCode;
	}

	public void setMoveTypeCode(String moveTypeCode) {
		outputMoveTypeCode = moveTypeCode;
	}

	public void setOptionCASSFile(String s) {
		optCASSFile = s;
	}

	public void setOptionCountries(Countries e) {
		optCountries = e;
	}

	public void setOptionListName(String s) {
		optListName = s;
	}

	public void setOptionMailFrequency(int i) {
		optMailFrequency = i;
	}

	public void setOptionMonthsRequested(int i) {
		optMonthsRequested = i;
	}

	public void setOptionNCOAFile(String s) {
		optNCOAFile = s;
	}

	public void setOptionProcessingType(ProcessingType e) {
		optProcessingType = e;
	}

	public void setOptionSummaryReports(boolean b) {
		optSummaryReports = b;
	}

	public void setOptJobOverride(boolean b) {
		optJobOverride = b;
	}

	public void setOptJobOverrideId(String s) {
		optJobOverrideId = s;
	}

	public void setOptPAFId(String s) {
		optPAFId = s;
	}

	public void setOutputAddrCarrierRoute(String s) {
		outputAddrCarrierRoute = s;
	}

//
// public String getOutputAddrDPVCMRA() {
// return outputAddrDPVCMRA;
// }
//
// public void setOutputAddrDPVCMRA(String s) {
// outputAddrDPVCMRA = s;
// }
	public void setOutputAddrCity(String s) {
		outputAddrCity = s;
	}

	public void setOutputAddrCityAbbreviation(String s) {
		outputAddrCityAbbreviation = s;
	}

	public void setOutputAddrCompany(String s) {
		outputAddrCompany = s;
	}

	public void setOutputAddrCountry(String s) {
		outputAddrCountry = s;
	}

	public void setOutputAddrCountryAbbreviation(String s) {
		outputAddrCountryAbbreviation = s;
	}

	public void setOutputAddrDPAndCheckDigit(String s) {
		outputAddrDPAndCheckDigit = s;
	}

	public void setOutputAddrKey(String s) {
		outputAddrKey = s;
	}

	public void setOutputMelissaAddrKey(String s) {
		outputMelissaAddrKey = s;
	}
	public void setOutputBaseMelissaAddrKey(String s) {
		outputBaseMelissaAddrKey = s;
	}

//
// public String getOutputAddrType() {
// return outputAddrType;
// }
//
// public void setOutputAddrType(String s) {
// outputAddrType = s;
// }
//
// public String getOutputAddrTypeString() {
// return outputAddrTypeString;
// }
//
// public void setOutputAddrTypeString(String s) {
// outputAddrTypeString = s;
// }
//
// public String getOutputAddrZipType() {
// return outputAddrZipType;
// }
//
// public void setOutputAddrZipType(String s) {
// outputAddrZipType = s;
// }
//
// public String getOutputAddrZipTypeString() {
// return outputAddrZipTypeString;
// }
//
// public void setOutputAddrZipTypeString(String s) {
// outputAddrZipTypeString = s;
// }
	public void setOutputAddrLine(String s) {
		outputAddrLine = s;
	}

	public void setOutputAddrLine2(String s) {
		outputAddrLine2 = s;
	}

	public void setOutputAddrPlus4(String s) {
		outputAddrPlus4 = s;
	}

	public void setOutputAddrPMB(String s) {
		outputAddrPMB = s;
	}

	public void setOutputAddrState(String s) {
		outputAddrState = s;
	}

	public void setOutputAddrSuite(String s) {
		outputAddrSuite = s;
	}

	public void setOutputAddrUrbanization(String s) {
		outputAddrUrbanization = s;
	}

	public void setOutputAddrZip(String s) {
		outputAddrZip = s;
	}

	public void setOutputDPVFootnotes(String outputDPVFootnotes) {
		this.outputDPVFootnotes = outputDPVFootnotes;
	}

	public void setOutputEffectiveDate(String s) {
		outputEffectiveDate = s;
	}

	public void setOutputFullName(String s) {
		outputFullName = s;
	}

	public void setOutputNameFirst(String s) {
		outputNameFirst = s;
	}

	public void setOutputNameLast(String s) {
		outputNameLast = s;
	}

	public void setOutputNameMiddle(String s) {
		outputNameMiddle = s;
	}

	public void setOutputNamePrefix(String s) {
		outputNamePrefix = s;
	}

	public void setOutputNameSuffix(String s) {
		outputNameSuffix = s;
	}

	public void setOutputParsedAddrRange(String s) {
		outputParsedAddrRange = s;
	}

	public void setOutputParsedExtraInfo(String s) {
		outputParsedExtraInfo = s;
	}

//
// public String getOutputParsedRouteService() {
// return outputParsedRouteService;
// }
//
// public void setOutputParsedRouteService(String s) {
// outputParsedRouteService = s;
// }
//
// public String getOutputParsedLockBox() {
// return outputParsedLockBox;
// }
//
// public void setOutputParsedLockBox(String s) {
// outputParsedLockBox = s;
// }
//
// public String getOutputParsedDeliveryInstallation() {
// return outputParsedDeliveryInstallation;
// }
//
// public void setOutputParsedDeliveryInstallation(String s) {
// outputParsedDeliveryInstallation = s;
// }
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

	/**
	 * Called to validate settings before saving
	 *
	 * @param warnings
	 * @param errors
	 */
	public void validate(List<String> warnings, List<String> errors) {
		// Wants a printout but didn't specify a list name?
		if (optSummaryReports && (!isBlank(optNCOAFile) || !isBlank(optCASSFile)) && isBlank(optListName)) {
			warnings.add(MDCheckDialog.getValidationMessage("MissingListName"));
		}
		// Not enough input fields
		boolean nameFieldBlank;
		if (inputUseFullName) {
			nameFieldBlank = isBlank(inputFullName);
		} else {
			nameFieldBlank = isBlank(inputNameFirst) && isBlank(inputNameLast);
		}
		if (((optProcessingType == ProcessingType.Standard) || (optProcessingType == ProcessingType.IndividualAndBusiness)) && nameFieldBlank && isBlank(inputAddrCompany)) {
			errors.add(MDCheckDialog.getValidationMessage("MissingInputColumnsResBus"));
		}
		if (((optProcessingType == ProcessingType.Individual) || (optProcessingType == ProcessingType.Residential)) && nameFieldBlank) {
			errors.add(MDCheckDialog.getValidationMessage("MissingInputColumnsRes"));
		}
		if ((optProcessingType == ProcessingType.Business) && isBlank(inputAddrCompany)) {
			errors.add(MDCheckDialog.getValidationMessage("MissingInputColumnsBus"));
		}
		if (!inputUseFullName) {
			if ((!isBlank(inputNameFirst) && isBlank(inputNameLast)) || (isBlank(inputNameFirst) && !isBlank(inputNameLast))) {
				errors.add(MDCheckDialog.getValidationMessage("MissingInputColumnsNameParts"));
			}
		}
		if (isBlank(inputAddrLine) && isBlank(inputAddrLine2)) {
			errors.add(MDCheckDialog.getValidationMessage("MissingInputColumnsAddressLine"));
		}
		if ((isBlank(inputAddrCity) || isBlank(inputAddrState)) && isBlank(inputAddrZip)) {
			errors.add(MDCheckDialog.getValidationMessage("MissingInputColumnsCityStateZip"));
		}
		if (optJobOverride && isBlank(optJobOverrideId)) {
			errors.add(MDCheckDialog.getValidationMessage("MissingOverrideId"));
		}
		// TODO: Other validations? PAF ID required?
	}

//	// methods stubs for unit testing ******************************
//	//execute before class
//	@BeforeClass
//	public static void beforeClass() {
//		System.out.println("in before class");
//	}
//
//	//execute after class
//	@AfterClass
//	public static void  afterClass() {
//		System.out.println("in after class");
//	}
//
//	//execute before test
//	@Before
//	public void before() {
//		System.out.println("in before");
//	}
//
//	//execute after test
//	@After
//	public void after() {
//		System.out.println("in after");
//	}
//
//	//test case
//	@Test
//	public void test() {
//		System.out.println("in test");
//
//	}
//
//	//***********************************  end test setup methods
}
