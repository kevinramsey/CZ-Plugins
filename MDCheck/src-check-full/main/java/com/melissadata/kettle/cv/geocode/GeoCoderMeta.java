package com.melissadata.kettle.cv.geocode;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.cv.address.AddressKeyRequestHandler;
import com.melissadata.kettle.cv.MDCheckWebService;
import com.melissadata.kettle.request.LocalRequestHandler;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.report.ReportStats;
import org.json.simple.JSONObject;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
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

import com.melissadata.mdAddr;
import com.melissadata.mdGeo;
import com.melissadata.mdZip;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class GeoCoderMeta implements WebRequestHandler, LocalRequestHandler, Cloneable {
	public enum AddrKeySource {
		None,
		AddrVerifyResults,
		InputColumn,
		ComponentColumns, ;
		public static AddrKeySource decode(String value) throws KettleException {
			try {
				return AddrKeySource.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.KeySourceUnknown") + value, e);
			}
		}

		public String encode() {
			return name();
		}
	}
	private static final String	TAG_GEO_CODER				= "geo_coder";
	private static final String	TAG_ADDR_KEY_SOURCE			= "addr_key_source";
	private static final String	TAG_ADDR_KEY				= "addr_key";
	private static final String	TAG_ADDR_COMP_LINE1			= "addr_comp_line1";
	private static final String	TAG_ADDR_COMP_LINE2			= "addr_comp_line2";
	private static final String	TAG_ADDR_COMP_CITY			= "addr_comp_city";
	private static final String	TAG_ADDR_COMP_STATE			= "addr_comp_state";
	private static final String	TAG_ADDR_COMP_ZIP			= "addr_comp_zip";
	private static final String	TAG_COUNTY_NAME				= "county_name";
	private static final String	TAG_COUNTY_FIPS				= "county_fips";
	private static final String	TAG_PLACE_CODE				= "place_code";
	private static final String	TAG_PLACE_NAME				= "place_name";
	private static final String	TAG_TIME_ZONE				= "time_zone";
	private static final String	TAG_TIME_ZONE_CODE			= "time_zone_code";
	private static final String	TAG_LATITUDE				= "latitude";
	private static final String	TAG_LONGITUDE				= "longitude";
	private static final String	TAG_CBSA_CODE				= "cbsa_code";
	private static final String	TAG_CBSA_LEVEL				= "cbsa_level";
	private static final String	TAG_CBSA_TITLE				= "cbsa_title";
	private static final String	TAG_CBSA_DIVISION_CODE		= "cbsa_division_code";
	private static final String	TAG_CBSA_DIVISION_LEVEL		= "cbsa_division_level";
	private static final String	TAG_CBSA_DIVISION_TITLE		= "cbsa_division_title";
	private static final String	TAG_CENSUS_BLOCK			= "census_block";
	private static final String	TAG_CENSUS_TRACT			= "census_tract";
	// set the default output sizes
	private static final int	MD_SIZE_LATITUDE			= 12;
	private static final int	MD_SIZE_LONGITUDE			= 12;
	private static final int	MD_SIZE_COUNTY				= 25;
	private static final int	MD_SIZE_COUNTY_FIPS			= 5;
	private static final int	MD_SIZE_PLACECODE			= 7;
	private static final int	MD_SIZE_PLACENAME			= 60;
	private static final int	MD_SIZE_TIMEZONE			= 20;
	private static final int	MD_SIZE_TIMEZONECODE		= 2;
	private static final int	MD_SIZE_CBSACODE			= 5;
	private static final int	MD_SIZE_CBSALEVEL			= 35;
	private static final int	MD_SIZE_CBSATITLE			= 55;
	private static final int	MD_SIZE_CBSADIVISION_CODE	= 5;
	private static final int	MD_SIZE_CBSADIVISION_LEVEL	= 35;
	private static final int	MD_SIZE_CBSADIVISION_TITLE	= 55;
	private static final int	MD_SIZE_CENSUS_BLOCK		= 4;
	private static final int	MD_SIZE_CENSUS_TRACT		= 6;
	private MDCheckStepData data;
	private boolean         initializeOK;
	private String          initializeWarn;
	private String          initializeError;
	private AddrKeySource   addrKeySource;
	private String          addrKey;
	private String          addrCompLine1;
	private String          addrCompLine2;
	private String          addrCompCity;
	private String          addrCompState;
	private String          addrCompZip;
	private String          countyName;
	private String          countyFIPS;
	private String          placeCode;
	private String          placeName;
	private String          timeZone;
	private String          timeZoneCode;
	private String				latitude;
	private String				longitude;
	private String				cbsaCode;
	private String				cbsaLevel;
	private String				cbsaTitle;
	private String				cbsaDivisionCode;
	private String				cbsaDivisionLevel;
	private String				cbsaDivisionTitle;
	private String				censusBlock;
	private String				censusTract;
	private int					fieldsAdded;
	// Info set during processing
	public String				localMsg					= "";
	public String				localDBDate					= "";
	public String				localDBExpiration			= "";
	public String				localDBBuildNo				= "";
	public String				localZipDBDate				= "";
	public String				localZipDBExpiration		= "";
	public String				localZipDBBuildNo			= "";
	public String				webMsg						= "";
	public String				webVersion					= "";
	// Exceptions detected during processing
	public KettleException		localException;
	public KettleException		webException;

	public GeoCoderMeta(MDCheckStepData data) {
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
		// If testing then add a fake record
		if (testing) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			root.appendChild(record);
			MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "1");
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressKey", "97223");
			// There is at least one request
			sendRequest = true;
		} else {
			// Otherwise add real records
			for (int recordID = 0; recordID < requests.size(); recordID++) {
				MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
				// Get the address key from the specified source
				String addressKey = null;
				switch (addrKeySource) {
					case None:
						// Don't perform the local request if no address key defined
						return false;
					case AddrVerifyResults:
						// Get address key from address verify step
						addressKey = (request.addrResults != null) ? request.addrResults.AddressKey : null;
						break;
					case InputColumn:
						// Get address key from an input column
						addressKey = MDCheck.getFieldString(request.inputMeta, request.inputData, addrKey);
						break;
					case ComponentColumns:
						// We should have already performed a dip into the address verifier to get the address key
						addressKey = (request.addrKeyResults != null) ? request.addrKeyResults.AddressKey : null;
						break;
				}
				if (Const.isEmpty(addressKey)) {
					addressKey = " ";
				}
				// Address key must be set and in a valid format
				boolean valid = !Const.isEmpty(addressKey);
				// Add request if it is valid
				if (valid) {
					// Create new record object
					Element record = xmlDoc.createElement("Record");
					root.appendChild(record);
					// Add unique record id
					MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);
					// Add request fields
					MDCheckWebService.addTextNode(xmlDoc, record, "AddressKey", addressKey);
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
	 * Called to validate initialization of the geo-coder
	 *
	 * @param checker
	 */
	public void checkInit(Validations checker) {
		initializeError = "";
		initializeWarn = "";
		boolean initialOK = true;
		mdGeo Geo = null;
		try {
			// Get instance of the Geo coder object
			Geo = DQTObjectFactory.newGeo();
			// Condigure it
			AdvancedConfigurationMeta advConfig = data.getAdvancedConfiguration();
			Geo.SetLicenseString(getLicense());
			Geo.SetPathToGeoCodeDataFiles(advConfig.getLocalDataPath());
			int licensedProducts = advConfig.getProducts();
			if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_GeoPoint) != 0) {
				Geo.SetPathToGeoPointDataFiles(advConfig.getLocalDataPath());
			}
			if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_CanadaGeo) != 0) {
				Geo.SetPathToGeoCanadaDataFiles(advConfig.getLocalDataPath());
			}
			// Initialize it
			if (Geo.InitializeDataFiles() == mdGeo.ProgramStatus.ErrorNone) {
				// Check for expiration of geo-coder data
				if (Validations.isDataExpiring(Geo.GetExpirationDate())) {
					checker.warnings.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.DataExpiring") + Geo.GetExpirationDate());
					if (checker.checkTimeStamp(checker.getGeoTimeStamp())) {
						initializeWarn = BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.DataExpiring") + Geo.GetExpirationDate();
						checker.showWarnings = true;
						checker.somethingToShow = true;
						checker.setGeoTimeStamp(checker.getTodayStamp());
					}
				}
			} else {
				if (isEnabled()) {
					checker.errors.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.GeoObject") + Geo.GetInitializeErrorString());
				} else {
					checker.warnings.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.GeoObject") + Geo.GetInitializeErrorString());
				}
				if (checker.checkTimeStamp(checker.getGeoTimeStamp())) {
					initializeError = BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.GeoObject") + Geo.GetInitializeErrorString();
					checker.showErrors = true;
					checker.somethingToShow = true;
					checker.setGeoTimeStamp(checker.getTodayStamp());
				}
				initialOK = false;
			}
			initializeOK = initialOK;
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			initializeError = BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.GeoObject") + t.getLocalizedMessage();
			checker.showErrors = true;
			checker.somethingToShow = true;
			checker.setGeoTimeStamp(checker.getTodayStamp());
		} finally {
			if (Geo != null) {
				Geo.delete();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GeoCoderMeta clone() throws CloneNotSupportedException {
		return (GeoCoderMeta) super.clone();
	}

	/**
	 * Called to do a local request to the address object to retrieve JUST the address key for the geocoder.
	 *
	 * @param data
	 * @param request
	 * @return
	 * @throws KettleException
	 */
	public String doLocalRequestForAddrKey(MDCheckData data, MDCheckCVRequest request) throws KettleException {
		RowMetaInterface inputMeta = request.inputMeta;
		Object[] inputData = request.inputData;
		// TODO: More complete validity checks
		boolean valid = true;
		// Get reference to address object
		mdAddr Addr = data.Addr;
		// Perform request if it's input is valid
		if (valid) {
			// Initialize address object
			Addr.ClearProperties();
			// Set request fields
			String value = MDCheck.getFieldString(inputMeta, inputData, addrCompLine1);
			if (!Const.isEmpty(value)) {
				Addr.SetAddress(value);
			}
			value = MDCheck.getFieldString(inputMeta, inputData, addrCompLine2);
			if (!Const.isEmpty(value)) {
				Addr.SetAddress2(value);
			}
			value = MDCheck.getFieldString(inputMeta, inputData, addrCompCity);
			if (!Const.isEmpty(value)) {
				Addr.SetCity(value);
			}
			value = MDCheck.getFieldString(inputMeta, inputData, addrCompState);
			if (!Const.isEmpty(value)) {
				Addr.SetState(value);
			}
			value = MDCheck.getFieldString(inputMeta, inputData, addrCompZip);
			if (!Const.isEmpty(value)) {
				Addr.SetZip(value);
			}
			// Perform the request
			Addr.VerifyAddress();
			// Always get the result codes for the request
// request.geoResults.resultCodes.addAll(MDCheck.getResultCodes(Addr.GetResults()));
		}
		// If request was valid then get result fields
		String addressKey = "";
		if (valid) {
			// Address Object does not have a direct API for returning the address key
// addressKey = Addr.GetZip().trim() + Addr.GetPlus4().trim() + Addr.GetDeliveryPointCode().trim();
			addressKey = Addr.GetAddressKey();
		}
		return addressKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#doLocalRequest(com.melissadata.kettle.MDCheckData)
	 */
	public synchronized void doLocalRequests(MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Skip if not enabled
		if (!isEnabled()) { return; }
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Get the single request
			RowMetaInterface inputMeta = request.inputMeta;
			Object[] inputData = request.inputData;
			// Get the intermediate geocoder results
			MDCheckCVRequest.GeoCoderResults geoResults = request.geoResults = new MDCheckCVRequest.GeoCoderResults();
			// Determine where to get the address key from
			String addressKey = "";
			switch (addrKeySource) {
				case None:
					// Don't perform the local request if no address key defined
					return;
				case AddrVerifyResults:
					// Get address key from address verify step
					addressKey = (request.addrResults != null) ? request.addrResults.AddressKey : null;
					break;
				case InputColumn:
					addressKey = MDCheck.getFieldString(inputMeta, inputData, addrKey);
					break;
				case ComponentColumns:
					// Need to call address verifier to get the key for the specified columns
					// TODO: move this up like we moved up the addr verifier dip in the web request?
					addressKey = doLocalRequestForAddrKey(data, request);
					break;
			}
			if (Const.isEmpty(addressKey)) {
				addressKey = " ";
			}
			// Address key must be set and in a valid format
			boolean valid = !Const.isEmpty(addressKey);
			// Get reference to geocoder and zip objects
			mdGeo Geo = data.Geo;
			mdZip Zip = data.Zip;
			// Perform request if its input is valid
			if (valid) {
				// Use geocoder if available
				if (Geo != null) {
					// Break address key into components based on its length
					String zip = "";
					String plus4 = "";
					String delPt = "";
					int keyLen = addressKey.length();
					// US 5 digit zip
					if (keyLen == 5) {
						zip = addressKey;
					} else if (((keyLen == 6) || (keyLen == 7)) /* && isCanadaLicensed() */) {
						if (keyLen == 6) {
							zip = addressKey;
						} else {
							zip = addressKey.substring(0, 3) + addressKey.substring(4, 7);
						}
					}
					// US 9 digit zip
					else if (keyLen == 9) {
						zip = addressKey.substring(0, 5);
						plus4 = addressKey.substring(5, 9);
					}
					// US 11 digit zip
					else if (keyLen == 11) {
						zip = addressKey.substring(0, 5);
						plus4 = addressKey.substring(5, 9);
						delPt = addressKey.substring(9, 11);
					}
					// call the geo-coder
					int rc = 0;
					if (!Const.isEmpty(delPt)) {
						// If delivery point is available then try to do geo coding to the rooftop level
						rc = Geo.GeoPoint(zip, plus4, delPt);
						// If this fails then we fall thru to the plus4 level geo code call
					}
					if ((rc == 0) && !Const.isEmpty(plus4)) {
						rc = Geo.GeoCode(zip, plus4);
						// If this fails then we fall thru to zip level geo code call
					}
					if ((rc == 0) && !Const.isEmpty(zip)) {
						rc = Geo.GeoCode(zip);
						// If this fails then we fall thru to invalid result handling
					}
					//
					// Even if we fail to geocode we still need to 
					// output or our columns will be off
//					if (rc == 0) {
//						// No valid results, therefore no output
//						valid = false;
//					}
					// Always get the result codes for the individual request
					geoResults.resultCodes.addAll(MDCheck.getResultCodes(Geo.GetResults()));
				} else if (Zip != null) {
					// Fall back to ZIP object
					Zip.FindZip(addressKey, false);
				} else {
					// This shouldn't happen
					throw new RuntimeException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.UnexpectedNoZipGeo"));
				}
			}
			// Process the results
			if (valid) {
				// Extract the results from the object(s)
				geoResults.CountyName = (Geo != null) ? Geo.GetCountyName() : Zip.GetCountyName();
				geoResults.CountyFips = (Geo != null) ? Geo.GetCountyFips() : Zip.GetCountyFips();
				geoResults.PlaceCode = (Geo != null) ? Geo.GetPlaceCode() : "";
				geoResults.PlaceName = (Geo != null) ? Geo.GetPlaceName() : "";
				geoResults.TZName = (Geo != null) ? Geo.GetTimeZone() : Zip.GetTimeZone();
				geoResults.TZCode = (Geo != null) ? Geo.GetTimeZoneCode() : Zip.GetTimeZoneCode();
				geoResults.Latitude = (Geo != null) ? Geo.GetLatitude() : Zip.GetLatitude();
				if(geoResults.Latitude.equals("0.0"))
					geoResults.Latitude = " ";
				geoResults.Longitude = (Geo != null) ? Geo.GetLongitude() : Zip.GetLongitude();
				if(geoResults.Longitude.equals("0.0"))
					geoResults.Longitude = " ";
				geoResults.CBSACode = (Geo != null) ? Geo.GetCBSACode() : "";
				geoResults.CBSALevel = (Geo != null) ? Geo.GetCBSALevel() : "";
				geoResults.CBSATitle = (Geo != null) ? Geo.GetCBSATitle().trim() : "";
				geoResults.CBSADivisionCode = (Geo != null) ? Geo.GetCBSADivisionCode() : "";
				geoResults.CBSADivisionLevel = (Geo != null) ? Geo.GetCBSADivisionLevel() : "";
				geoResults.CBSADivisionTitle = (Geo != null) ? Geo.GetCBSADivisionTitle().trim() : "";
				geoResults.CensusBlock = (Geo != null) ? Geo.GetCensusBlock() : "";
				geoResults.CensusTract = (Geo != null) ? Geo.GetCensusTract() : "";
				// TODO: additional validation?
				geoResults.valid = true;
			}
		}
	}

	public String getAddrCompCity() {
		return addrCompCity;
	}

	public String getAddrCompLine1() {
		return addrCompLine1;
	}

	public String getAddrCompLine2() {
		return addrCompLine2;
	}

	public String getAddrCompState() {
		return addrCompState;
	}

	public String getAddrCompZip() {
		return addrCompZip;
	}

	public String getAddrKey() {
		return addrKey;
	}

	public AddrKeySource getAddrKeySource() {
		return addrKeySource;
	}

	public String getCBSACode() {
		return cbsaCode;
	}

	public String getCBSADivisionCode() {
		return cbsaDivisionCode;
	}

	public String getCBSADivisionLevel() {
		return cbsaDivisionLevel;
	}

	public String getCBSADivisionTitle() {
		return cbsaDivisionTitle;
	}

	public String getCBSALevel() {
		return cbsaLevel;
	}

	public String getCBSATitle() {
		return cbsaTitle;
	}

	public String getCensusBlock() {
		return censusBlock;
	}

	public String getCensusTract() {
		return censusTract;
	}

	public String getCountyFIPS() {
		return countyFIPS;
	}

	public String getCountyName() {
		return countyName;
	}

	/**
	 * @return GeoCoder Customer ID
	 */
	public String getCustomerID() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String customerID = acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_GeoCode);
		return customerID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURLCVS(com.melissadata.kettle.MDCheckData)
	 */
	public URL getCVSURL(MDCheckData data) {
		return data.realCVSGeoCoderURL;
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
		// There are no output fields if geocoding is not enabled
		if (isEnabled()) {
			// No results if address key source is not defined
			if (addrKeySource != AddrKeySource.None) {
				if (!Const.isEmpty(countyName)) {
					MDCheckMeta.getStringField(row, originName, space, countyName, MD_SIZE_COUNTY);
				}
				if (!Const.isEmpty(countyFIPS)) {
					MDCheckMeta.getStringField(row, originName, space, countyFIPS, MD_SIZE_COUNTY_FIPS);
				}
				if (!Const.isEmpty(placeCode)) {
					MDCheckMeta.getStringField(row, originName, space, placeCode, MD_SIZE_PLACECODE);
				}
				if (!Const.isEmpty(placeName)) {
					MDCheckMeta.getStringField(row, originName, space, placeName, MD_SIZE_PLACENAME);
				}
				if (!Const.isEmpty(timeZone)) {
					MDCheckMeta.getStringField(row, originName, space, timeZone, MD_SIZE_TIMEZONE);
				}
				if (!Const.isEmpty(timeZoneCode)) {
					MDCheckMeta.getStringField(row, originName, space, timeZoneCode, MD_SIZE_TIMEZONECODE);
				}
				if (!Const.isEmpty(latitude)) {
					MDCheckMeta.getStringField(row, originName, space, latitude, MD_SIZE_LATITUDE);
				}
				if (!Const.isEmpty(longitude)) {
					MDCheckMeta.getStringField(row, originName, space, longitude, MD_SIZE_LONGITUDE);
				}
				if (!Const.isEmpty(cbsaCode)) {
					MDCheckMeta.getStringField(row, originName, space, cbsaCode, MD_SIZE_CBSACODE);
				}
				if (!Const.isEmpty(cbsaLevel)) {
					MDCheckMeta.getStringField(row, originName, space, cbsaLevel, MD_SIZE_CBSALEVEL);
				}
				if (!Const.isEmpty(cbsaTitle)) {
					MDCheckMeta.getStringField(row, originName, space, cbsaTitle, MD_SIZE_CBSATITLE);
				}
				if (!Const.isEmpty(cbsaDivisionCode)) {
					MDCheckMeta.getStringField(row, originName, space, cbsaDivisionCode, MD_SIZE_CBSADIVISION_CODE);
				}
				if (!Const.isEmpty(cbsaDivisionLevel)) {
					MDCheckMeta.getStringField(row, originName, space, cbsaDivisionLevel, MD_SIZE_CBSADIVISION_LEVEL);
				}
				if (!Const.isEmpty(cbsaDivisionTitle)) {
					MDCheckMeta.getStringField(row, originName, space, cbsaDivisionTitle, MD_SIZE_CBSADIVISION_TITLE);
				}
				if (!Const.isEmpty(censusBlock)) {
					MDCheckMeta.getStringField(row, originName, space, censusBlock, MD_SIZE_CENSUS_BLOCK);
				}
				if (!Const.isEmpty(censusTract)) {
					MDCheckMeta.getStringField(row, originName, space, censusTract, MD_SIZE_CENSUS_TRACT);
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

	public String getLatitude() {
		return latitude;
	}

	/**
	 * @return GeoCoder License
	 */
	public String getLicense() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String license = acMeta.getProdutLicense(AdvancedConfigurationMeta.MDLICENSE_GeoCode);
		return license;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getPlaceCode() {
		return placeCode;
	}

	public String getPlaceName() {
		return placeName;
	}

	public String getServiceName() {
		return "Geo Coder";
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getTimeZoneCode() {
		return timeZoneCode;
	}

	/**
	 * If the address key has to be retrieved by dipping into the address verifier then this handler will take
	 * care of that.
	 *
	 * @return The handler that will process it. null if no dip needs to be performed.
	 */
	public AddressKeyRequestHandler getWebRequestForAddrKeyHandler() {
		if (addrKeySource == AddrKeySource.ComponentColumns) { return new AddressKeyRequestHandler(this); }
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDCheckData)
	 */
	public URL getWebURL(MDCheckData data, int queue) {
		return data.realWebGeoCoderURL;
	}

	/**
	 * Returns the XML representation of the Address Verifier meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_GEO_CODER)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_ADDR_KEY_SOURCE, addrKeySource.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_ADDR_KEY, addrKey));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_ADDR_COMP_LINE1, addrCompLine1));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_ADDR_COMP_LINE2, addrCompLine2));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_ADDR_COMP_CITY, addrCompCity));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_ADDR_COMP_STATE, addrCompState));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_ADDR_COMP_ZIP, addrCompZip));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_COUNTY_NAME, countyName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_COUNTY_FIPS, countyFIPS));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_PLACE_CODE, placeCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_PLACE_NAME, placeName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_TIME_ZONE, timeZone));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_TIME_ZONE_CODE, timeZoneCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_LATITUDE, latitude));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_LONGITUDE, longitude));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CBSA_CODE, cbsaCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CBSA_LEVEL, cbsaLevel));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CBSA_TITLE, cbsaTitle));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CBSA_DIVISION_CODE, cbsaDivisionCode));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CBSA_DIVISION_LEVEL, cbsaDivisionLevel));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CBSA_DIVISION_TITLE, cbsaDivisionTitle));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CENSUS_BLOCK, censusBlock));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CENSUS_TRACT, censusTract));
		retval.append(tab).append(XMLHandler.closeTag(TAG_GEO_CODER)).append(Const.CR);
		return retval.toString();
	}

	/**
	 * @return
	 */
	public boolean isCanadaLicensed() {
		// Licensed if it is not a DLL
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		if (acMeta.getServiceType() != ServiceType.Local) { return true; }
		// Check product licensing
		if ((acMeta.getProducts() & AdvancedConfigurationMeta.MDLICENSE_CanadaGeo) != 0) { return true; }
		return false;
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if licensed
		boolean isLicensed = isLicensed();
		// Enabled only if address key source is defined
		boolean noInputFields = true;
		switch (addrKeySource) {
			case AddrVerifyResults:
				noInputFields = (data.getAddressVerify() != null) && !data.getAddressVerify().isEnabled();
				break;
			case InputColumn:
				noInputFields = Const.isEmpty(addrKey);
				break;
			case ComponentColumns:
				noInputFields = Const.isEmpty(addrCompLine1) && Const.isEmpty(addrCompLine2) && Const.isEmpty(addrCompCity) && Const.isEmpty(addrCompState) && Const.isEmpty(addrCompZip);
				break;
		}
		return isLicensed && !noInputFields;
	}

	public boolean isInitializeOK() {
		return initializeOK;
	}

	/**
	 * @return
	 */
	public boolean isLicensed() {
		// Licensed if it is not a DLL
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		if (acMeta.getServiceType() == ServiceType.CVS) {
			return true;
		}
		if ((acMeta.getServiceType() == ServiceType.Web) && (!Const.isEmpty(getCustomerID()) || !Const.isEmpty(data.getAddressVerify().getCustomerID()))) {
			return true;
		}
		// Check product licensing
		if (((acMeta.getProducts() & MDPropTags.MDLICENSE_GeoPoint) != 0)
				|| ((acMeta.getProducts() & MDPropTags.MDLICENSE_GeoCode) != 0)
				|| ((acMeta.getProducts() & MDPropTags.MDLICENSE_Address) != 0)
				|| ((acMeta.getProducts() & MDPropTags.MDLICENSE_CanadaGeo) != 0)
				|| ((acMeta.getProducts() & MDPropTags.MDLICENSE_Community) != 0)) {
			return true;
		}
		return false;
	}

	/**
	 * Called to process the results of either the local or web services
	 *
	 * @param checkData
	 * @param requests
	 */
	public void outputData(MDCheckData checkData, List<MDCheckRequest> requests) {
		if (!isEnabled()) { return; }
		// Output each request's results
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Output the geocoder results
			MDCheckCVRequest.GeoCoderResults geoResults = request.geoResults;
			if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_GeoCoder)
					&& AdvancedConfigurationMeta.isCommunity()){
				geoResults = request.getGeoCommunityResults(geoResults);
			}
			if ((geoResults != null) && geoResults.valid) {
				// output results
				if (!Const.isEmpty(countyName)) {
					request.addOutputData(geoResults.CountyName);
				}
				if (!Const.isEmpty(countyFIPS)) {
					request.addOutputData(geoResults.CountyFips);
				}
				if (!Const.isEmpty(placeCode)) {
					request.addOutputData(geoResults.PlaceCode);
				}
				if (!Const.isEmpty(placeName)) {
					request.addOutputData(geoResults.PlaceName);
				}
				if (!Const.isEmpty(timeZone)) {
					request.addOutputData(geoResults.TZName);
				}
				if (!Const.isEmpty(timeZoneCode)) {
					request.addOutputData(geoResults.TZCode);
				}
				if (!Const.isEmpty(latitude)) {
					request.addOutputData(geoResults.Latitude);
				}
				if (!Const.isEmpty(longitude)) {
					request.addOutputData(geoResults.Longitude);
				}
				if (!Const.isEmpty(cbsaCode)) {
					request.addOutputData(geoResults.CBSACode);
				}
				if (!Const.isEmpty(cbsaLevel)) {
					request.addOutputData(geoResults.CBSALevel);
				}
				if (!Const.isEmpty(cbsaTitle)) {
					request.addOutputData(geoResults.CBSATitle);
				}
				if (!Const.isEmpty(cbsaDivisionCode)) {
					request.addOutputData(geoResults.CBSADivisionCode);
				}
				if (!Const.isEmpty(cbsaDivisionLevel)) {
					request.addOutputData(geoResults.CBSADivisionLevel);
				}
				if (!Const.isEmpty(cbsaDivisionTitle)) {
					request.addOutputData(geoResults.CBSADivisionTitle);
				}
				if (!Const.isEmpty(censusBlock)) {
					request.addOutputData(geoResults.CensusBlock);
				}
				if (!Const.isEmpty(censusTract)) {
					request.addOutputData(geoResults.CensusTract);
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
				// TODO: Reroute this record to an invalid output stream?
			}
			// Add the email result codes to the overall result codes
			if (geoResults != null) {
				// Add geocoder result codes to over all result codes
				if(AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_GeoCoder)) {
					request.resultCodes.addAll(geoResults.resultCodes);
				}
				// If reporting is enabled then gather some stats
				if (data.isReportEnabled()) {
					updateStats(geoResults, checkData);
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
		if (!Const.isEmpty(resultCodes)) { return resultCodes; }
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
				// Get the intermediate email results
				MDCheckCVRequest.GeoCoderResults geoResults = request.geoResults = new MDCheckCVRequest.GeoCoderResults();
				// Result code for the individual request
				geoResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(record, "Results")));
				// Get the address result element
				org.dom4j.Element address = record.element("Address");
				if (address == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.AddressNotFoundInElement")); }
				// Get the geocoder results
				org.dom4j.Element county = MDCheckWebService.getElement(address, "County");
				geoResults.CountyName = MDCheckWebService.getElementText(county, "Name");
				geoResults.CountyFips = MDCheckWebService.getElementText(county, "Fips");
				org.dom4j.Element place = MDCheckWebService.getElement(address, "Place");
				geoResults.PlaceCode = MDCheckWebService.getElementText(place, "Code");
				geoResults.PlaceName = MDCheckWebService.getElementText(place, "Name");
				org.dom4j.Element tz = MDCheckWebService.getElement(address, "TimeZone");
				geoResults.TZName = MDCheckWebService.getElementText(tz, "Name");
				geoResults.TZCode = MDCheckWebService.getElementText(tz, "Code");
				geoResults.Latitude = MDCheckWebService.getElementText(address, "Latitude");
				geoResults.Longitude = MDCheckWebService.getElementText(address, "Longitude");
				org.dom4j.Element cbsa = MDCheckWebService.getElement(address, "CBSA");
				geoResults.CBSACode = MDCheckWebService.getElementText(cbsa, "Code");
				geoResults.CBSALevel = MDCheckWebService.getElementText(cbsa, "Level");
				geoResults.CBSATitle = MDCheckWebService.getElementText(cbsa, "Title");
				geoResults.CBSADivisionCode = MDCheckWebService.getElementText(cbsa, "CBSADivisionCode");
				geoResults.CBSADivisionLevel = MDCheckWebService.getElementText(cbsa, "CBSADivisionLevel");
				geoResults.CBSADivisionTitle = MDCheckWebService.getElementText(cbsa, "CBSADivisionTitle");
				org.dom4j.Element census = MDCheckWebService.getElement(address, "Census");
				geoResults.CensusBlock = MDCheckWebService.getElementText(census, "Block");
				geoResults.CensusTract = MDCheckWebService.getElementText(census, "Tract");
				// TODO: More validation
				geoResults.valid = true;
			}
		}
		return "";
	}

	/**
	 * Called to read name parsing meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_GEO_CODER);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Input fields
			String value = MDCheckStepData.getTagValue(node, TAG_ADDR_KEY_SOURCE);
			addrKeySource = (value != null) ? AddrKeySource.decode(value) : addrKeySource;
			addrKey = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_ADDR_KEY), addrKey);
			addrCompLine1 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_ADDR_COMP_LINE1), addrCompLine1);
			addrCompLine2 = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_ADDR_COMP_LINE2), addrCompLine2);
			addrCompCity = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_ADDR_COMP_CITY), addrCompCity);
			addrCompState = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_ADDR_COMP_STATE), addrCompState);
			addrCompZip = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_ADDR_COMP_ZIP), addrCompZip);
			// Output fields
			countyName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_COUNTY_NAME), countyName);
			countyFIPS = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_COUNTY_FIPS), countyFIPS);
			placeCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_PLACE_CODE), placeCode);
			placeName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_PLACE_NAME), placeName);
			timeZone = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_TIME_ZONE), timeZone);
			timeZoneCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_TIME_ZONE_CODE), timeZoneCode);
			latitude = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_LATITUDE), latitude);
			longitude = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_LONGITUDE), longitude);
			cbsaCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CBSA_CODE), cbsaCode);
			cbsaLevel = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CBSA_LEVEL), cbsaLevel);
			cbsaTitle = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CBSA_TITLE), cbsaTitle);
			cbsaDivisionCode = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CBSA_DIVISION_CODE), cbsaDivisionCode);
			cbsaDivisionLevel = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CBSA_DIVISION_LEVEL), cbsaDivisionLevel);
			cbsaDivisionTitle = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CBSA_DIVISION_TITLE), cbsaDivisionTitle);
			censusBlock = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CENSUS_BLOCK), censusBlock);
			censusTract = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_CENSUS_TRACT), censusTract);
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
		String value = rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_ADDR_KEY_SOURCE);
		addrKeySource = (value != null) ? AddrKeySource.decode(value) : addrKeySource;
		addrKey = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_ADDR_KEY), addrKey);
		addrCompLine1 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_LINE1), addrCompLine1);
		addrCompLine2 = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_LINE2), addrCompLine2);
		addrCompCity = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_CITY), addrCompCity);
		addrCompState = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_STATE), addrCompState);
		addrCompZip = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_ZIP), addrCompZip);
		countyName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_COUNTY_NAME), countyName);
		countyFIPS = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_COUNTY_FIPS), countyFIPS);
		placeCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_PLACE_CODE), placeCode);
		placeName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_PLACE_NAME), placeName);
		timeZone = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_TIME_ZONE), timeZone);
		timeZoneCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_TIME_ZONE_CODE), timeZoneCode);
		latitude = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_LATITUDE), latitude);
		longitude = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_LONGITUDE), longitude);
		cbsaCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CBSA_CODE), cbsaCode);
		cbsaLevel = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CBSA_LEVEL), cbsaLevel);
		cbsaTitle = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CBSA_TITLE), cbsaTitle);
		cbsaDivisionCode = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CBSA_DIVISION_CODE), cbsaDivisionCode);
		cbsaDivisionLevel = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CBSA_DIVISION_LEVEL), cbsaDivisionLevel);
		cbsaDivisionTitle = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CBSA_DIVISION_TITLE), cbsaDivisionTitle);
		censusBlock = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CENSUS_BLOCK), censusBlock);
		censusTract = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_GEO_CODER + "." + TAG_CENSUS_TRACT), censusTract);
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
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_ADDR_KEY_SOURCE, addrKeySource.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_ADDR_KEY, addrKey);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_LINE1, addrCompLine1);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_LINE2, addrCompLine2);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_CITY, addrCompCity);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_STATE, addrCompState);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_ADDR_COMP_ZIP, addrCompZip);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_COUNTY_NAME, countyName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_COUNTY_FIPS, countyFIPS);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_PLACE_CODE, placeCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_PLACE_NAME, placeName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_TIME_ZONE, timeZone);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_TIME_ZONE_CODE, timeZoneCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_LATITUDE, latitude);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_LONGITUDE, longitude);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CBSA_CODE, cbsaCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CBSA_LEVEL, cbsaLevel);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CBSA_TITLE, cbsaTitle);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CBSA_DIVISION_CODE, cbsaDivisionCode);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CBSA_DIVISION_LEVEL, cbsaDivisionLevel);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CBSA_DIVISION_TITLE, cbsaDivisionTitle);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CENSUS_BLOCK, censusBlock);
		rep.saveStepAttribute(idTransformation, idStep, TAG_GEO_CODER + "." + TAG_CENSUS_TRACT, censusTract);
	}

	public void setAddrCompCity(String s) {
		addrCompCity = s;
	}

	public void setAddrCompLine1(String s) {
		addrCompLine1 = s;
	}

	public void setAddrCompLine2(String s) {
		addrCompLine2 = s;
	}

	public void setAddrCompState(String s) {
		addrCompState = s;
	}

	public void setAddrCompZip(String s) {
		addrCompZip = s;
	}

	public void setAddrKey(String s) {
		addrKey = s;
	}

	public void setAddrKeySource(AddrKeySource e) {
		addrKeySource = e;
	}

	public void setCBSACode(String s) {
		cbsaCode = s;
	}

	public void setCBSADivisionCode(String s) {
		cbsaDivisionCode = s;
	}

	public void setCBSADivisionLevel(String s) {
		cbsaDivisionLevel = s;
	}

	public void setCBSADivisionTitle(String s) {
		cbsaDivisionTitle = s;
	}

	public void setCBSALevel(String s) {
		cbsaLevel = s;
	}

	public void setCBSATitle(String s) {
		cbsaTitle = s;
	}

	public void setCensusBlock(String s) {
		censusBlock = s;
	}

	public void setCensusTract(String s) {
		censusTract = s;
	}

	public void setCountyFIPS(String s) {
		countyFIPS = s;
	}

	public void setCountyName(String s) {
		countyName = s;
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault() {
		// Inputs
		addrKeySource = AddrKeySource.None;
		addrKey = "";
		addrCompLine1 = "";
		addrCompLine2 = "";
		addrCompCity = "";
		addrCompState = "";
		addrCompZip = "";
		// Outputs
		if (MDCheckData.defaultsSet) {
			latitude = MDCheckData.GeoLatitude;
			longitude = MDCheckData.GeoLongitude;
			countyName = MDCheckData.GeoCounty;
			countyFIPS = MDCheckData.GeoCountyFIPS;
			placeCode = MDCheckData.GeoPlaceCode;
			placeName = MDCheckData.GeoPlaceName;
			timeZone = MDCheckData.GeoTimeZone;
			timeZoneCode = MDCheckData.GeoTimeZoneCode;
			cbsaCode = MDCheckData.GeoCBSACode;
			cbsaLevel = MDCheckData.GeoCBSALevel;
			cbsaTitle = MDCheckData.GeoCBSATitle;
			cbsaDivisionCode = MDCheckData.GeoCBSADivisionCode;
			cbsaDivisionLevel = MDCheckData.GeoCBSADivisionLevel;
			cbsaDivisionTitle = MDCheckData.GeoCBSADivisionTitle;
			censusBlock = MDCheckData.GeoCensusBlock;
			censusTract = MDCheckData.GeoCensusTract;
		} else {
			latitude = "MD_Latitude";
			longitude = "MD_Longitude";
			countyName = "MD_GeoCounty";
			countyFIPS = "MD_GeoCountyFIPS";
			placeCode = "MD_PlaceCode";
			placeName = "MD_PlaceName";
			timeZone = "MD_TimeZone";
			timeZoneCode = "MD_TZCode";
			latitude = "MD_Latitude";
			longitude = "MD_Longitude";
			cbsaCode = "MD_CBSACode";
			cbsaLevel = "MD_CBSALevel";
			cbsaTitle = "MD_CBSATitle";
			cbsaDivisionCode = "MD_CBSADivisionCode";
			cbsaDivisionLevel = "MD_CBSADivisionLevel";
			cbsaDivisionTitle = "MD_CBSADivisionTitle";
			censusBlock = "MD_CensusBlock";
			censusTract = "MD_CensusTract";
		}
	}

	public void setInitializeOK(boolean initializeOK) {
		this.initializeOK = initializeOK;
	}

	public void setLatitude(String s) {
		latitude = s;
	}

	public void setLongitude(String s) {
		longitude = s;
	}

	public void setPlaceCode(String s) {
		placeCode = s;
	}

	public void setPlaceName(String s) {
		placeName = s;
	}

	public void setTimeZone(String s) {
		timeZone = s;
	}

	public void setTimeZoneCode(String s) {
		timeZoneCode = s;
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
			checker.checkGeoCodeInputFields(data, warnings, errors);
		}
	}

	/**
	 * Called to update reporting stats for a given result
	 *
	 * @param checkData
	 * @param geoResults
	 */
	private synchronized void updateStats(MDCheckCVRequest.GeoCoderResults geoResults, MDCheckData checkData) {
		// De-reference results and meta data
		Set<String> resultCodes = geoResults.resultCodes;
		ReportStats numGeoOverview = checkData.numGeoOverview;
		MDCheckMeta checkMeta = data.getMeta();
		// Test for specific result codes
		boolean streetCentroid = resultCodes.contains("GS01");
		boolean zip7Centroid = resultCodes.contains("GS02");
		boolean communityCentroid = resultCodes.contains("GS03");
		boolean rooftop = resultCodes.contains("GS05");
		boolean interpolatedRooftop = resultCodes.contains("GS06");
// boolean wireCenter = resultCodes.contains("GS10");
		boolean invalidZip = resultCodes.contains("GE01");
		boolean zipNotFound = resultCodes.contains("GE02");
// boolean dataFilesExpired = resultCodes.contains("GE04");
		boolean noLicenseForCountry = resultCodes.contains("GE05");
		// Track rooftop precision
		if (rooftop || interpolatedRooftop) {
			numGeoOverview.inc(checkMeta.geoOverviewReportCStat[0]);
		}
		// Track street precision
		if (streetCentroid) {
			numGeoOverview.inc(checkMeta.geoOverviewReportCStat[1]);
		}
		// Track zip/community precision
		if (zip7Centroid || communityCentroid) {
			numGeoOverview.inc(checkMeta.geoOverviewReportCStat[2]);
		}
		// Track errors
		if (invalidZip || zipNotFound || noLicenseForCountry) {
			numGeoOverview.inc(checkMeta.geoOverviewReportCStat[3]);
		}
		// Assign result codes to reporting resultStats
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
