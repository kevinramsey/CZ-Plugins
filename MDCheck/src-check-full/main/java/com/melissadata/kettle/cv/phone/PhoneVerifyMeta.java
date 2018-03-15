package com.melissadata.kettle.cv.phone;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.cv.MDCheckFullDialog;
import com.melissadata.kettle.request.LocalRequestHandler;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.cv.MDCheckWebService;
import com.melissadata.kettle.report.ReportStats;
import org.json.simple.JSONObject;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.melissadata.mdPhone;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class PhoneVerifyMeta implements WebRequestHandler, LocalRequestHandler, Cloneable {
	public enum OutputPhoneFormat {
		FORMAT1(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format1")),
		FORMAT2(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format2")),
		FORMAT3(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format3")),
		FORMAT4(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format4")),
		FORMAT5(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format5")),
		FORMAT6(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format6")), ;
		public static OutputPhoneFormat decode(String value) throws KettleException {
			try {
				return OutputPhoneFormat.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Unknown") + value, e);
			}
		}
		private String	format;

		private OutputPhoneFormat(String format) {
			this.format = format;
		}

		public String encode() {
			return name();
		}

		public String getFormat() {
			return format;
		}

		@Override
		public String toString() {
			return getFormat();
		}
	}
	private static Class<?>		PKG						= MDCheckFullDialog.class;
	private static final String	TAG_PHONE_VERIFY		= "phone_verify";
	private static final String	TAG_INPUT_PHONE			= "input_phone";
	private static final String	TAG_OUTPUT_PHONE		= "output_phone";
	private static final String	TAG_OUTPUT_CITY			= "output_city";
	private static final String	TAG_OUTPUT_STATE		= "output_state";
	private static final String	TAG_OUTPUT_COUNTRY		= "output_country";
	private static final String	TAG_OUTPUT_TIME_ZONE	= "output_time_zone";
	private static final String	TAG_OUTPUT_TZCODE		= "output_tzcode";
	private static final String	TAG_OUTPUT_AREA_CODE	= "output_area_code";
	private static final String	TAG_OUTPUT_PREFIX		= "output_prefix";
	private static final String	TAG_OUTPUT_SUFFIX		= "output_suffix";
	private static final String	TAG_OUTPUT_EXTENSION	= "output_extension";
	private static final String	TAG_OUTPUT_COUNTY		= "output_county";
	private static final String	TAG_OUTPUT_COUNTY_FIPS	= "output_county_fips";
	private static final String	TAG_OPT_PHONE_FORMAT	= "phone_verify";
	// extra
	private static final String	TAG_OUTPUT_COUNTRY_NAME	= "output_country_name";
	private static final String	TAG_OUTPUT_LATITUDE		= "output_latitude";
	// size length fields for output
	private static final String	TAG_OUTPUT_LONGITUDE	= "output_longitude";
	private static final int	MD_SIZE_PHONE			= 30;
	private static final int	MD_SIZE_AREACODE		= 3;
	private static final int	MD_SIZE_PREFIX			= 3;
	private static final int	MD_SIZE_SUFFIX			= 4;
	private static final int	MD_SIZE_EXTENSION		= 14;
	private static final int	MD_SIZE_CITY			= 28;
	private static final int	MD_SIZE_STATE			= 2;
	private static final int	MD_SIZE_COUNTY			= 25;
	private static final int	MD_SIZE_COUNTYFIPS		= 5;
	private static final int	MD_SIZE_COUNTRYCODE		= 2;
	private static final int	MD_SIZE_TIMEZONE		= 20;
	private static final int	MD_SIZE_TIMEZONE_CODE	= 2;
	private static final int	MD_SIZE_DEFAULT_EXTRA	= 50;
	private MDCheckStepData data;
	private boolean         initializeOK;
	private String          initializeWarn;
	private String          initializeError;
	private String          inputPhone;
	private String          outputPhone;
	private String          outputCity;
	private String          outputState;
	private String          outputCountry;
	private String          outputTimeZone;
	private String          outputTZCode;
	private String          outputAreaCode;
	private String          outputPrefix;
	private String          outputSuffix;
	private String          outputExtension;
	private String          outputCounty;
	private String				outputCountyFips;
	// extra
	private String				outputCountryName;
	private String				outputLatitude;
	private String				outputLongitude;
	private OutputPhoneFormat	optionFormat;
	private int					fieldsAdded;
	// Info set during processing
	public String				localMsg				= "";
	public String				localDBDate				= "";
	public String				localDBBuildNo			= "";
	public String				webMsg					= "";
	public String				webVersion				= "";
	// Exceptions detected during processing
	public KettleException		localException;
	public KettleException		webException;

	public PhoneVerifyMeta(MDCheckStepData data) {
		this.data = data;
		setDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#buildWebRequest(org.w3c.dom.Document, com.melissadata.kettle.MDCheckData,
	 * boolean)
	 */
	public boolean buildWebRequest(Document xmlDoc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		Element root = xmlDoc.getDocumentElement();
		// Add records
		boolean sendRequest = false;
		// If testing then create a fake record
		if (testing) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			root.appendChild(record);
			MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "1");
			MDCheckWebService.addTextNode(xmlDoc, record, "Phone", "8005551212");
			// There is at least one request
			sendRequest = true;
		} else {
			// Otherwise, add the real records
			for (int recordID = 0; recordID < requests.size(); recordID++) {
				MDCheckRequest request = requests.get(recordID);
				RowMetaInterface inputMeta = request.inputMeta;
				Object[] inputData = request.inputData;
				// Get input phone field (if defined)
				String phone = null;
				if (!Const.isEmpty(inputPhone)) {
					phone = inputMeta.getString(inputData, inputPhone, "");
				}
				if (Const.isEmpty(phone)) {
					phone = " ";
				}
				// Valid if there is a phone value
				boolean valid = !Const.isEmpty(phone);
				// Add request if it is valid
				if (valid) {
					// Create new record object
					Element record = xmlDoc.createElement("Record");
					root.appendChild(record);
					// Add unique record id
					MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);
					// Add request fields
					MDCheckWebService.addTextNode(xmlDoc, record, "Phone", phone);
					// There is at least one request
					sendRequest = true;
				}
			}
		}
		return sendRequest;
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

	/**
	 * Called to validate initialization of the phone object
	 *
	 * @param checker
	 */
	public void checkInit(Validations checker) {
		boolean initialOK = true;
		initializeError = "";
		mdPhone Phone = null;
		try {
			// Create a temporary phone object
			Phone = DQTObjectFactory.newPhone();
			// Configure it
			AdvancedConfigurationMeta advConfig = data.getAdvancedConfiguration();
			Phone.SetLicenseString(getLicense());
			// Initialize it
			if (Phone.Initialize(advConfig.getLocalDataPath()) != mdPhone.ProgramStatus.ErrorNone) {
				if (isEnabled()) {
					checker.errors.add("Phone: " + Phone.GetInitializeErrorString());
				} else {
					checker.warnings.add("Phone: " + Phone.GetInitializeErrorString());
				}
				if (checker.checkTimeStamp(checker.getPhoneTimeStamp())) {
					initializeError = "Phone: " + Phone.GetInitializeErrorString();
					checker.showErrors = true;
					checker.somethingToShow = true;
					checker.setPhoneTimeStamp(checker.getTodayStamp());
				}
				initialOK = false;
			}
			initializeOK = initialOK;
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			initializeError = "Phone: " + t.getLocalizedMessage();
			checker.showErrors = true;
			checker.somethingToShow = true;
			checker.setPhoneTimeStamp(checker.getTodayStamp());
		} finally {
			if (Phone != null) {
				Phone.delete();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PhoneVerifyMeta clone() throws CloneNotSupportedException {
		return (PhoneVerifyMeta) super.clone();
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
			// Get the intermediate phone results
			MDCheckCVRequest.PhoneResults phoneResults = request.phoneResults = new MDCheckCVRequest.PhoneResults();
			// Get input phone field (if defined)
			String phone = null;
			if (!Const.isEmpty(inputPhone)) {
				phone = inputMeta.getString(inputData, inputPhone, "");
			}
			if (Const.isEmpty(phone)) {
				phone = " ";
			}
			// Valid if there is a phone value
			boolean valid = !Const.isEmpty(phone);
			// Get reference to phone object
			mdPhone Phone = data.Phone;
			// Add request if it is valid
			if (valid) {
				// perform the lookup
				Phone.Lookup(phone);
				// Always get result codes for the individual request
				phoneResults.resultCodes.addAll(MDCheck.getResultCodes(Phone.GetResults()));
			}
			// Process results if valid
			if (valid) {
				// Extract results from Phone object
				phoneResults.NewAreaCode = Phone.GetNewAreaCode();
				phoneResults.AreaCode = Phone.GetAreaCode();
				phoneResults.Prefix = Phone.GetPrefix();
				phoneResults.Suffix = Phone.GetSuffix();
				phoneResults.City = Phone.GetCity();
				phoneResults.State = Phone.GetState();
				phoneResults.CountryAbbreviation = Phone.GetCountryCode();
				phoneResults.TZName = Phone.GetTimeZone();
				phoneResults.TZCode = Phone.GetTimeZoneCode();
				phoneResults.Extension = Phone.GetExtension();
				phoneResults.CountyName = Phone.GetCountyName();
				phoneResults.CountyFips = Phone.GetCountyFips();
				phoneResults.CountryName = phoneResults.CountryAbbreviation; // PO does not provide country name
				phoneResults.Latitude = Phone.GetLatitude();
				phoneResults.Longitude = Phone.GetLongitude();
				// TODO: more validation
				phoneResults.valid = true;
			}
		}
	}

	/**
	 * @return Phone Verification Customer ID
	 */
	public String getCustomerID() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String customerID = acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_Phone);
		return customerID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURLCVS(com.melissadata.kettle.MDCheckData)
	 */
	public URL getCVSURL(MDCheckData data) {
		return data.realCVSPhoneVerifierURL;
	}

	/**
	 * Called to determine the output fields that will be included in the step outout record
	 *
	 * NOTE: Order of fields must match the order of fields in processResponses
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {
		int start = row.size();
		// Don't add fields if not enabled
		if (isEnabled()) {
			// General output fields
			if (!Const.isEmpty(outputPhone)) {
				MDCheckMeta.getStringField(row, originName, space, outputPhone, MD_SIZE_PHONE);
			}
			if (!Const.isEmpty(outputCity)) {
				MDCheckMeta.getStringField(row, originName, space, outputCity, MD_SIZE_CITY);
			}
			if (!Const.isEmpty(outputState)) {
				MDCheckMeta.getStringField(row, originName, space, outputState, MD_SIZE_STATE);
			}
			if (!Const.isEmpty(outputCountry)) {
				MDCheckMeta.getStringField(row, originName, space, outputCountry, MD_SIZE_COUNTRYCODE);
			}
			if (!Const.isEmpty(outputTimeZone)) {
				MDCheckMeta.getStringField(row, originName, space, outputTimeZone, MD_SIZE_TIMEZONE);
			}
			if (!Const.isEmpty(outputTZCode)) {
				MDCheckMeta.getStringField(row, originName, space, outputTZCode, MD_SIZE_TIMEZONE_CODE);
			}
			if (!Const.isEmpty(outputAreaCode)) {
				MDCheckMeta.getStringField(row, originName, space, outputAreaCode, MD_SIZE_AREACODE);
			}
			if (!Const.isEmpty(outputPrefix)) {
				MDCheckMeta.getStringField(row, originName, space, outputPrefix, MD_SIZE_PREFIX);
			}
			if (!Const.isEmpty(outputSuffix)) {
				MDCheckMeta.getStringField(row, originName, space, outputSuffix, MD_SIZE_SUFFIX);
			}
			if (!Const.isEmpty(outputExtension)) {
				MDCheckMeta.getStringField(row, originName, space, outputExtension, MD_SIZE_EXTENSION);
			}
			if (!Const.isEmpty(outputCounty)) {
				MDCheckMeta.getStringField(row, originName, space, outputCounty, MD_SIZE_COUNTY);
			}
			if (!Const.isEmpty(outputCountyFips)) {
				MDCheckMeta.getStringField(row, originName, space, outputCountyFips, MD_SIZE_COUNTYFIPS);
			}
			// extra
			if (false) {
				if (!Const.isEmpty(outputCountryName)) {
					MDCheckMeta.getStringField(row, originName, space, outputCountryName, MD_SIZE_DEFAULT_EXTRA);
				}
				if (!Const.isEmpty(outputLatitude)) {
					MDCheckMeta.getStringField(row, originName, space, outputLatitude, MD_SIZE_DEFAULT_EXTRA);
				}
				if (!Const.isEmpty(outputLongitude)) {
					MDCheckMeta.getStringField(row, originName, space, outputLongitude, MD_SIZE_DEFAULT_EXTRA);
				}
			}
		}
		// Keep a count of the number of fields we add
		fieldsAdded = row.size() - start;
	}

	public String getInitializeError() {
		return initializeError;
	}

	public String getInitializeWarn() {
		return initializeWarn;
	}

	public String getInputPhone() {
		return inputPhone;
	}

	/**
	 * @return Phone Verification License
	 */
	public String getLicense() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String license = acMeta.getProdutLicense(AdvancedConfigurationMeta.MDLICENSE_Phone);
		return license;
	}

	public OutputPhoneFormat getOptionFormat() {
		return optionFormat;
	}

	public String getOutputAreaCode() {
		return outputAreaCode;
	}

	public String getOutputCity() {
		return outputCity;
	}

	public String getOutputCountry() {
		return outputCountry;
	}

	// extra
	public String getOutputCountryName() {
		return outputCountryName;
	}

	public String getOutputCounty() {
		return outputCounty;
	}

	public String getOutputCountyFips() {
		return outputCountyFips;
	}

	public String getOutputExtension() {
		return outputExtension;
	}

	public String getOutputLatitude() {
		return outputLatitude;
	}

	public String getOutputLongitude() {
		return outputLongitude;
	}

	public String getOutputPhone() {
		return outputPhone;
	}

	public String getOutputPrefix() {
		return outputPrefix;
	}

	public String getOutputState() {
		return outputState;
	}

	public String getOutputSuffix() {
		return outputSuffix;
	}

	public String getOutputTimeZone() {
		return outputTimeZone;
	}

	public String getOutputTZCode() {
		return outputTZCode;
	}

	public String getServiceName() {
		return "Phone Verify";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDCheckData)
	 */
	public URL getWebURL(MDCheckData data, int queue) {
		return data.realWebPhoneVerifierURL;
	}

	/**
	 * Returns the XML representation of the meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_PHONE_VERIFY)).append(Const.CR);
		// input
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_PHONE, inputPhone));
		// output
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PHONE, outputPhone));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_CITY, outputCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_STATE, outputState));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTRY, outputCountry));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_TIME_ZONE, outputTimeZone));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_TZCODE, outputTZCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_AREA_CODE, outputAreaCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_PREFIX, outputPrefix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_SUFFIX, outputSuffix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_EXTENSION, outputExtension));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTY, outputCounty));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTY_FIPS, outputCountyFips));
		// options
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PHONE_FORMAT, optionFormat.encode()));
		// extra
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_COUNTRY_NAME, outputCountryName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_LATITUDE, outputLatitude));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_LONGITUDE, outputLongitude));
		retval.append(tab).append(XMLHandler.closeTag(TAG_PHONE_VERIFY)).append(Const.CR);
		return retval.toString();
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if licensed
		boolean isLicensed = isLicensed();
		// Enabled only if there is an input phone
		boolean noInputFields = Const.isEmpty(inputPhone);
		return isLicensed && !noInputFields;
	}

	public boolean isInitializeOK() {
		return initializeOK;
	}

	/**
	 * @return true if product is licensd
	 */
	public boolean isLicensed() {
		// Licensed if it is not a DLL
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		if (acMeta.getServiceType() == ServiceType.CVS) { return true; }
		if ((acMeta.getServiceType() == ServiceType.Web) && !Const.isEmpty(getCustomerID())) { return true; }
		// check product licensing
		if ((acMeta.getProducts() & MDPropTags.MDLICENSE_Phone) != 0 || (acMeta.getProducts() & MDPropTags.MDLICENSE_Community) != 0) { return true; }
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
			// Output the phone results
			MDCheckCVRequest.PhoneResults phoneResults = request.phoneResults;
			if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_PhoneObject)
					&& AdvancedConfigurationMeta.isCommunity()){
				phoneResults = request.getPhoneCommunityResults(phoneResults);
			}
			if ((phoneResults != null) && phoneResults.valid) {
				// Output the results
				if (!Const.isEmpty(outputPhone)) {
					// The area code can come in two different fields. Given preference to the new one
					String newAreaCode = phoneResults.NewAreaCode;
					String npa = !Const.isEmpty(newAreaCode) ? newAreaCode : phoneResults.AreaCode;
					// Get other phone components
					String prefix = phoneResults.Prefix;
					String suffix = phoneResults.Suffix;
					// Generate a formatted output phone according to user specification
					if (!Const.isEmpty(npa) || !Const.isEmpty(prefix) || !Const.isEmpty(suffix)) {
						request.addOutputData(formatPhone(npa, prefix, suffix));
					} else {
						request.addOutputData("");
					}
				}
				if (!Const.isEmpty(outputCity)) {
					request.addOutputData(phoneResults.City);
				}
				if (!Const.isEmpty(outputState)) {
					request.addOutputData(phoneResults.State);
				}
				if (!Const.isEmpty(outputCountry)) {
					request.addOutputData(phoneResults.CountryAbbreviation);
				}
				if (!Const.isEmpty(outputTimeZone)) {
					request.addOutputData(phoneResults.TZName);
				}
				if (!Const.isEmpty(outputTZCode)) {
					request.addOutputData(phoneResults.TZCode);
				}
				if (!Const.isEmpty(outputAreaCode)) {
					String areaCode = phoneResults.NewAreaCode;
					if (Const.isEmpty(areaCode)) {
						areaCode = phoneResults.AreaCode;
					}
					request.addOutputData(areaCode);
				}
				if (!Const.isEmpty(outputPrefix)) {
					request.addOutputData(phoneResults.Prefix);
				}
				if (!Const.isEmpty(outputSuffix)) {
					request.addOutputData(phoneResults.Suffix);
				}
				if (!Const.isEmpty(outputExtension)) {
					request.addOutputData(phoneResults.Extension);
				}
				if (!Const.isEmpty(outputCounty)) {
					request.addOutputData(phoneResults.CountyName);
				}
				if (!Const.isEmpty(outputCountyFips)) {
					request.addOutputData(phoneResults.CountyFips);
				}
				// extra
				if (false) {
					if (!Const.isEmpty(outputCountryName)) {
						request.addOutputData(phoneResults.CountryName);
					}
					if (!Const.isEmpty(outputLatitude)) {
						request.addOutputData(phoneResults.Latitude);
					}
					if (!Const.isEmpty(outputLongitude)) {
						request.addOutputData(phoneResults.Longitude);
					}
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
				// TODO: Reroute this record to an invalid output stream?
			}
			if (phoneResults != null) {
				// Add the phone result codes to the overall result codes
				if(AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_PhoneObject)) {
					request.resultCodes.addAll(phoneResults.resultCodes);
				}
				// Update reporting stats
				if (data.isReportEnabled()) {
					updateStats(phoneResults, checkData);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#processWebResponse(org.dom4j.Document, com.melissadata.kettle.MDCheckData,
	 * boolean)
	 */
	public String processWebResponse(org.dom4j.Document doc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("ResponseArray")) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.ResponseNotFound")); }
		// Check the general result
		String resultCodes = MDCheckWebService.getElementText(response, "Results");
		if (!Const.isEmpty(resultCodes)) { return resultCodes.trim(); }
		// Get the interface version
		webVersion = MDCheckWebService.getElementText(response, "Version");
		// Get the response records (ignore if testing)
		if (!testing) {
			@SuppressWarnings("unchecked")
			Iterator<org.dom4j.Element> i = response.elementIterator("Record");
			while (i.hasNext()) {
				org.dom4j.Element record = i.next();
				// This is used to index the request being processed
				int recordID = MDCheckWebService.getElementInteger(record, "RecordID");
				// Get the request object for the specified record id
				MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
				// Get the intermediate phone results
				MDCheckCVRequest.PhoneResults phoneResults = request.phoneResults = new MDCheckCVRequest.PhoneResults();
				// Result code for the individual request
				phoneResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(record, "Results")));
				// Get the phone result element
				org.dom4j.Element phone = record.element("Phone");
				if (phone == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.PhoneNotFoundInElement")); }
				// Extract the phone results
				phoneResults.NewAreaCode = MDCheckWebService.getElementText(phone, "NewAreaCode");
				phoneResults.AreaCode = MDCheckWebService.getElementText(phone, "AreaCode");
				phoneResults.Prefix = MDCheckWebService.getElementText(phone, "Prefix");
				phoneResults.Suffix = MDCheckWebService.getElementText(phone, "Suffix");
				phoneResults.City = MDCheckWebService.getElementText(phone, "City");
				phoneResults.State = MDCheckWebService.getElementText(phone, "State");
				org.dom4j.Element country = MDCheckWebService.getElement(phone, "Country");
				phoneResults.CountryAbbreviation = MDCheckWebService.getElementText(country, "Abbreviation");
				org.dom4j.Element tz = MDCheckWebService.getElement(phone, "TimeZone");
				phoneResults.TZName = MDCheckWebService.getElementText(tz, "Name");
				phoneResults.TZCode = MDCheckWebService.getElementText(tz, "Code");
				phoneResults.Extension = MDCheckWebService.getElementText(phone, "Extension");
				phoneResults.CountyName = MDCheckWebService.getElementText(phone, "PhoneCountyName");
				phoneResults.CountyFips = MDCheckWebService.getElementText(phone, "PhoneCountyFips");
				phoneResults.CountryName = ""; // Web service does not supply country name
				phoneResults.Latitude = MDCheckWebService.getElementText(phone, "Latitude");
				phoneResults.Longitude = MDCheckWebService.getElementText(phone, "Longitude");
				// TODO: more validation
				phoneResults.valid = true;
			}
		}
		return "";
	}

	/**
	 * Called to read meta data from a document node
	 *
	 * @param node
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_PHONE_VERIFY);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Input fields
			inputPhone = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_PHONE), inputPhone);
			// Output fields
			outputPhone = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PHONE), outputPhone);
			outputCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_CITY), outputCity);
			outputState = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_STATE), outputState);
			outputCountry = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTRY), outputCountry);
			outputTimeZone = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_TIME_ZONE), outputTimeZone);
			outputTZCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_TZCODE), outputTZCode);
			outputAreaCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_AREA_CODE), outputAreaCode);
			outputPrefix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_PREFIX), outputPrefix);
			outputSuffix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_SUFFIX), outputSuffix);
			outputExtension = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_EXTENSION), outputExtension);
			outputCounty = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTY), outputCounty);
			outputCountyFips = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTY_FIPS), outputCountyFips);
			// Option fields
			String value = MDCheckStepData.getTagValue(node, TAG_OPT_PHONE_FORMAT);
			optionFormat = (value != null) ? OutputPhoneFormat.decode(value) : optionFormat;
			// extra
			outputCountryName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_COUNTRY_NAME), outputCountryName);
			outputLatitude = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_LATITUDE), outputLatitude);
			outputLongitude = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_LONGITUDE), outputLongitude);
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
		// input
		inputPhone = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_INPUT_PHONE), inputPhone);
		// output
		outputPhone = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_PHONE), outputPhone);
		outputCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_CITY), outputCity);
		outputState = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_STATE), outputState);
		outputCountry = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTRY), outputCountry);
		outputTimeZone = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_TIME_ZONE), outputTimeZone);
		outputTZCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_TZCODE), outputTZCode);
		outputAreaCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_AREA_CODE), outputAreaCode);
		outputPrefix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_PREFIX), outputPrefix);
		outputSuffix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_SUFFIX), outputSuffix);
		outputExtension = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_EXTENSION), outputExtension);
		outputCounty = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTY), outputCounty);
		outputCountyFips = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTY_FIPS), outputCountyFips);
		// options
		String value = rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OPT_PHONE_FORMAT);
		optionFormat = (value != null) ? OutputPhoneFormat.decode(value) : optionFormat;
		// extra
		outputCountryName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTRY_NAME), outputCountryName);
		outputLatitude = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_LATITUDE), outputLatitude);
		outputLongitude = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_LONGITUDE), outputLongitude);
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
		// inputs
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_INPUT_PHONE, inputPhone);
		// outputs
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_PHONE, outputPhone);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_CITY, outputCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_STATE, outputState);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTRY, outputCountry);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_TIME_ZONE, outputTimeZone);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_TZCODE, outputTZCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_AREA_CODE, outputAreaCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_PREFIX, outputPrefix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_SUFFIX, outputSuffix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_EXTENSION, outputExtension);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTY, outputCounty);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTY_FIPS, outputCountyFips);
		// options
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OPT_PHONE_FORMAT, optionFormat.encode());
		// extra
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_COUNTRY_NAME, outputCountryName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_LATITUDE, outputLatitude);
		rep.saveStepAttribute(idTransformation, idStep, TAG_PHONE_VERIFY + "." + TAG_OUTPUT_LONGITUDE, outputLongitude);
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	/**
	 * Called to initialized default valuesn
	 */
	public void setDefault() {
		// input meta data (blank means not defined)
		inputPhone = "";
		// output meta data
		if (MDCheckData.defaultsSet) {
			outputPhone = MDCheckData.phone;
			outputAreaCode = MDCheckData.phoneAreaCode;
			outputPrefix = MDCheckData.phonePrefix;
			outputSuffix = MDCheckData.phoneSuffix;
			outputExtension = MDCheckData.phoneExtension;
			try {
				optionFormat = OutputPhoneFormat.decode(MDCheckData.phoneFormat);
			} catch (KettleException e) {
				e.printStackTrace();
			}
			outputCity = MDCheckData.phoneCity;
			outputState = MDCheckData.phoneStateProvince;
			outputCountry = MDCheckData.phoneCountryCode;
			outputTimeZone = MDCheckData.phoneTimeZone;
			outputTZCode = MDCheckData.phoneTimeZoneCode;
			outputCounty = MDCheckData.phoneCountyName;
			outputCountyFips = MDCheckData.phoneCountyFIPS;
		} else {
			outputPhone = "MD_Phone";
			outputAreaCode = "MD_AreaCode";
			outputPrefix = "MD_PhonePrefix";
			outputSuffix = "MD_PhoneSuffix";
			outputExtension = "MD_PhoneExtension";
			// options
			optionFormat = OutputPhoneFormat.FORMAT1;
			outputCity = "MD_PhoneCity";
			outputState = "MD_PhoneState";
			outputCountry = "MD_PhoneCountry";
			outputTimeZone = "MD_PhoneTimeZone";
			outputTZCode = "MD_PhoneTZCode";
			outputCounty = "MD_PhoneCounty";
			outputCountyFips = "MD_PhoneCountyFips";
		}
		// extra
		outputCountryName = "MD_PhoneCountryName";
		outputLatitude = "MD_PhoneLatitude";
		outputLongitude = "MD_PhoneLongitude";
	}

	public void setInitializeOK(boolean initializeOK) {
		this.initializeOK = initializeOK;
	}

	public void setInputPhone(String s) {
		inputPhone = s;
	}

	public void setOptionFormat(OutputPhoneFormat optionFormat) {
		this.optionFormat = optionFormat;
	}

	public void setOutputAreaCode(String s) {
		outputAreaCode = s;
	}

	public void setOutputCity(String s) {
		outputCity = s;
	}

	public void setOutputCountry(String s) {
		outputCountry = s;
	}

	public void setOutputCountryName(String s) {
		outputCountryName = s;
	}

	public void setOutputCounty(String outputCounty) {
		this.outputCounty = outputCounty;
	}

	public void setOutputCountyFips(String outputCountyFips) {
		this.outputCountyFips = outputCountyFips;
	}

	public void setOutputExtension(String s) {
		outputExtension = s;
	}

	public void setOutputLatitude(String s) {
		outputLatitude = s;
	}

	public void setOutputLongitude(String s) {
		outputLongitude = s;
	}

	public void setOutputPhone(String outputPhone) {
		this.outputPhone = outputPhone;
	}

	public void setOutputPrefix(String s) {
		outputPrefix = s;
	}

	public void setOutputState(String s) {
		outputState = s;
	}

	public void setOutputSuffix(String s) {
		outputSuffix = s;
	}

	public void setOutputTimeZone(String s) {
		outputTimeZone = s;
	}

	public void setOutputTZCode(String s) {
		outputTZCode = s;
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
			checker.checkPhoneInputFields(data, warnings, errors);
		}
	}

	/**
	 * This will format a phone number according the specified format option
	 *
	 * @param npa
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	private String formatPhone(String npa, String prefix, String suffix) {
		String formatedPhone = "";
		boolean noNPA = Const.isEmpty(npa);
		switch (optionFormat) {
			case FORMAT1:
				if (noNPA) {
					formatedPhone = prefix + "-" + suffix;
				} else {
					formatedPhone = "(" + npa + ") " + prefix + "-" + suffix;
				}
				break;
			case FORMAT2:
				if (noNPA) {
					formatedPhone = prefix + " " + suffix;
				} else {
					formatedPhone = "(" + npa + ") " + prefix + " " + suffix;
				}
				break;
			case FORMAT3:
				if (noNPA) {
					formatedPhone = prefix + "-" + suffix;
				} else {
					formatedPhone = npa + "-" + prefix + "-" + suffix;
				}
				break;
			case FORMAT4:
				if (noNPA) {
					formatedPhone = prefix + " " + suffix;
				} else {
					formatedPhone = npa + " " + prefix + " " + suffix;
				}
				break;
			case FORMAT5:
				if (noNPA) {
					formatedPhone = prefix + "." + suffix;
				} else {
					formatedPhone = npa + "." + prefix + "." + suffix;
				}
				break;
			case FORMAT6:
				if (noNPA) {
					formatedPhone = prefix + suffix;
				} else {
					formatedPhone = npa + prefix + suffix;
				}
				break;
		}
		return formatedPhone;
	}

	/**
	 * Called to update reporting stats for a given result
	 *
	 * @param phoneResults
	 * @param checkData
	 */
	private synchronized void updateStats(MDCheckCVRequest.PhoneResults phoneResults, MDCheckData checkData) {
		// De-reference results and meta data
		Set<String> resultCodes = phoneResults.resultCodes;
		ReportStats numPhoneOverview = checkData.numPhoneOverview;
		MDCheckMeta checkMeta = data.getMeta();
		// Test for specific result codes
		boolean tenDigitMatch = resultCodes.contains("PS01");
		boolean sevenDigitMatch = resultCodes.contains("PS02");
		boolean correctedAreaCode = resultCodes.contains("PS03");
		boolean updatedAreaCode = resultCodes.contains("PS06");
		boolean blankPhone = resultCodes.contains("PE02");
		// Track matches ...
		if (tenDigitMatch || sevenDigitMatch) {
			// ... that did not require a change to the area code
			if (!(correctedAreaCode || updatedAreaCode)) {
				numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[0]);
			}
		}
		// Track non-matches (ignore blanks)
		else if (!blankPhone) {
			numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[3]);
			checkData.numPhoneErrors += 1;
		}
		// Track changes to the area code
		if (correctedAreaCode || updatedAreaCode) {
			numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[1]);
		}
		// Track blank phone numbers
		if (blankPhone) {
			numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[2]);
		}
		// Add result codes to reporting result stats
		for (String resultCode : resultCodes) {
			checkData.resultStats.inc(resultCode);
		}
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
