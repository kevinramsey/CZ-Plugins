package com.melissadata.kettle.iplocator;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.request.LocalRequestHandler;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
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
import org.w3c.dom.Node;

import com.melissadata.mdIpLocator;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;
import com.melissadata.kettle.cv.MDCheckCVRequest.IPLocatorResults;


public class IPLocatorMeta implements WebRequestHandler, LocalRequestHandler, Cloneable {
	private MDCheckStepData data;
	private static final String	TAG_IP_LOCATOR			= "ip_locator";
	private static final String	TAG_IN_IPADDRESS		= "in_ipaddress";
	private static final String	TAG_OUT_IPADDRESS		= "out_ipaddress";
	private static final String	TAG_OUT_LATITUDE		= "out_latitude";
	private static final String	TAG_OUT_LONGITUDE		= "out_longitude";
	private static final String	TAG_OUT_ZIP				= "out_zip";
	private static final String	TAG_OUT_REGION			= "out_region";
	private static final String	TAG_OUT_NAME			= "out_name";
	private static final String	TAG_OUT_DOMAIN			= "out_domain";
	private static final String	TAG_OUT_CITYNAME		= "out_cityname";
	private static final String	TAG_OUT_COUNTRY			= "out_country";
	private static final String	TAG_OUT_ABBREVIATION	= "out_abbreviation";
	private static final String	TAG_OUT_CONNECT_SPEED   = "out_connection_speed";
	private static final String	TAG_OUT_CONNECT_TYPE	= "out_connection_type";
	private static final String	TAG_OUT_UTC				= "out_utc";
	private static final String	TAG_OUT_CONTINENT		= "out_continent";
	private static final int	MD_SIZE_OUTIPADDRESS	= 16;
	private static final int	MD_SIZE_LATITUDE		= 12;
	private static final int	MD_SIZE_LONGITUDE		= 12;
	private static final int	MD_SIZE_ZIP				= 10;
	private static final int	MD_SIZE_REGION			= 25;
	private static final int	MD_SIZE_NAME			= 30;
	private static final int	MD_SIZE_DOMAIN			= 20;
	private static final int	MD_SIZE_CITYNAME		= 35;
	private static final int	MD_SIZE_COUNTRY			= 25;
	private static final int	MD_SIZE_ABBREVIATION	= 2;
	private static final int	MD_SIZE_CONNECT_SPEED	= 12;
	private static final int	MD_SIZE_CONNECT_TYPE	= 12;
	private static final int	MD_SIZE_UTC				= 8;
	private static final int	MD_SIZE_CONTINENT		= 25;
	
	private String				inIPAddress;
	private String				outIPAddress;
	private String				outLatitude;
	private String				outLongitude;
	private String				outZip;
	private String				outRegion;
	private String				outName;
	private String				outDomain;
	private String				outCityName;
	private String				outCountry;
	private String				outAbbreviation;
	private String				outConnectionSpeed;
	private String				outConnectionType;
	private String				outUTC;
	private String				outContinent;
	// Info set during processing
	public String				localMsg				= "";
	public String				localDBDate				= "";
	public String				localDBExpiration		= "";
	public String				localDBBuildNo			= "";
	public String				webMsg					= "";
	public String				webVersion				= "";
	private int					fieldsAdded;
	// Exceptions detected during processing
	public KettleException		localException;
	public KettleException		webException;
	private LogChannelInterface log;
	

	public IPLocatorMeta(MDCheckStepData data) {
		this.data = data;
		log = data.getMeta().getLog();
		setDefault();
	}
	

	@SuppressWarnings("unchecked")
	public boolean buildWebRequest(JSONObject jsonRequest, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {

		addWebOptions(jsonRequest);
		// Add records
		boolean sendRequest = false;
		JSONArray jsonArray = new JSONArray();
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			
			MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
			// TODO: More complete validity checks
			boolean valid = true;
			// Add request if it is valid
			if (valid) {
				// Add request fields
				if (addWebRequestFields(jsonArray, request, recordID)) {
					sendRequest = true;
				}
			}
		}
		
		jsonRequest.put("Records", jsonArray);
		
		return sendRequest;
	}
	
	@SuppressWarnings("unchecked")
	private boolean addWebRequestFields(JSONArray jsonArray, MDCheckCVRequest request, int recID) throws KettleException {

		RowMetaInterface inputMeta = request.inputMeta;
		Object[] inputData = request.inputData;

		// Get input ip address (if defined)
		String ipAddress = null;
		if (!Const.isEmpty(inIPAddress)) {
			ipAddress = inputMeta.getString(inputData, inIPAddress, "");
		}
		// Valid if there is an email value
		boolean valid = !Const.isEmpty(ipAddress);
		JSONObject jo = null;
		// Add request if it is valid
		if (valid) {

			jo = new JSONObject();
			jo.put("RecordID", String.valueOf(recID));
			jo.put("IPAddress", ipAddress);
			jsonArray.add(jo);
		}

		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected void addWebOptions(JSONObject request){
		String value = "";
		request.put("Options", value);
	}

	public boolean buildWebRequest(Document requestDoc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		/* Not used.  Uses JSON with new service this is here as it is from
		 *  Interface WebRequestHandler
		 */
		return false;

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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IPLocatorMeta clone() throws CloneNotSupportedException {
		return (IPLocatorMeta) super.clone();
	}

	public synchronized void doLocalRequests(MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		if (!isEnabled()) { return; }
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Get the single request
			RowMetaInterface inputMeta = request.inputMeta;
			Object[] inputData = request.inputData;
			IPLocatorResults ipResults = request.ipLocatorResults = new IPLocatorResults();
			// Get ipAddress field (if defined)
			String ipAddr = "";
			if (!Const.isEmpty(inIPAddress)) {
				ipAddr = inputMeta.getString(inputData, inIPAddress, "");
			}
			// Valid if there is an IP value
			boolean valid = !Const.isEmpty(ipAddr);
			mdIpLocator ipLoc = data.ipLocator;
			if (valid) {
				ipLoc.LocateIpAddress(ipAddr);
				ipResults.resultCodes.addAll(MDCheck.getResultCodes(ipLoc.GetResults()));
				ipResults.ipAddress = ipAddr;
				ipResults.latitude = ipLoc.GetLatitude();
				ipResults.longitude = ipLoc.GetLongitude();
				ipResults.zip = ipLoc.GetZip();
				ipResults.region = ipLoc.GetRegion();
				ipResults.name = ipLoc.GetISP();
				ipResults.domain = ipLoc.GetDomainName();
				ipResults.cityName = ipLoc.GetCity();
				ipResults.country = ipLoc.GetCountry();
				ipResults.abbreviation = ipLoc.GetCountryAbbreviation();
				ipResults.valid = true;
			}
		}
	}

	/**
	 * @return IP Locator customer ID
	 */
	public String getCustomerID() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String customerID = acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_IPLocator);
		return customerID;
	}

	public URL getCVSURL(MDCheckData data) {
		return data.realCVSIPLocatorURL;
	}

	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {
		int start = row.size();
		// Don't add fields if input is not defined
		if (isEnabled()) {
			// General output fields
			if (!Const.isEmpty(outIPAddress)) {
				MDCheckMeta.getStringField(row, originName, space, outIPAddress, MD_SIZE_OUTIPADDRESS);
			}
			if (!Const.isEmpty(outLatitude)) {
				MDCheckMeta.getStringField(row, originName, space, outLatitude, MD_SIZE_LATITUDE);
			}
			if (!Const.isEmpty(outLongitude)) {
				MDCheckMeta.getStringField(row, originName, space, outLongitude, MD_SIZE_LONGITUDE);
			}
			if (!Const.isEmpty(outZip)) {
				MDCheckMeta.getStringField(row, originName, space, outZip, MD_SIZE_ZIP);
			}
			if (!Const.isEmpty(outRegion)) {
				MDCheckMeta.getStringField(row, originName, space, outRegion, MD_SIZE_REGION);
			}
			if (!Const.isEmpty(outName)) {
				MDCheckMeta.getStringField(row, originName, space, outName, MD_SIZE_NAME);
			}
			if (!Const.isEmpty(outDomain)) {
				MDCheckMeta.getStringField(row, originName, space, outDomain, MD_SIZE_DOMAIN);
			}
			if (!Const.isEmpty(outCityName)) {
				MDCheckMeta.getStringField(row, originName, space, outCityName, MD_SIZE_CITYNAME);
			}
			if (!Const.isEmpty(outCountry)) {
				MDCheckMeta.getStringField(row, originName, space, outCountry, MD_SIZE_COUNTRY);
			}
			if (!Const.isEmpty(outAbbreviation)) {
				MDCheckMeta.getStringField(row, originName, space, outAbbreviation, MD_SIZE_ABBREVIATION);
			}
			
			
			// These only come from Web
			AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
			if (((acMeta.getServiceType() == ServiceType.Web) || (acMeta.getServiceType() == ServiceType.CVS))) {
				if (!Const.isEmpty(outConnectionSpeed)) {
					MDCheckMeta.getStringField(row, originName, space, outConnectionSpeed, MD_SIZE_CONNECT_SPEED);
				}
				if (!Const.isEmpty(outConnectionType)) {
					MDCheckMeta.getStringField(row, originName, space, outConnectionType, MD_SIZE_CONNECT_TYPE);
				}
				if (!Const.isEmpty(outUTC)) {
					MDCheckMeta.getStringField(row, originName, space, outUTC, MD_SIZE_UTC);
				}
				if (!Const.isEmpty(outContinent)) {
					MDCheckMeta.getStringField(row, originName, space, outContinent, MD_SIZE_CONTINENT);
				}
			}
		}
		// Keep a count of the number of fields we add
		fieldsAdded = row.size() - start;
	}

	public String getInIPAddress() {
		return inIPAddress;
	}

	/**
	 * @return IP Locator license
	 */
	public String getLicense() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String license = acMeta.getProdutLicense(AdvancedConfigurationMeta.MDLICENSE_IPLocator);
		return license;
	}


	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_IP_LOCATOR)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_IN_IPADDRESS, inIPAddress));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_IPADDRESS, outIPAddress));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_LATITUDE, outLatitude));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_LONGITUDE, outLongitude));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_ZIP, outZip));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_REGION, outRegion));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_NAME, outName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_DOMAIN, outDomain));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_CITYNAME, outCityName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_COUNTRY, outCountry));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_ABBREVIATION, outAbbreviation));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_CONNECT_SPEED, outConnectionSpeed));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_CONNECT_TYPE, outConnectionType));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_UTC, outUTC));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUT_CONTINENT, outContinent));
		
		retval.append(tab).append(XMLHandler.closeTag(TAG_IP_LOCATOR)).append(Const.CR);
		return retval.toString();
	}

	public boolean isEnabled() {
		// Enabled only if licensed
		boolean isLicensed = isLicensed();
		// Enabled only if there is an input email
		boolean noInputFields = Const.isEmpty(inIPAddress);
		return isLicensed && !noInputFields;
	}

	public boolean isLicensed() {
		// Licensed if it is not a DLL
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		if (acMeta.getServiceType() == ServiceType.CVS) { return true; }
		if ((acMeta.getServiceType() == ServiceType.Web) && !Const.isEmpty(getCustomerID())) { return true; }
		// check product license
		if ((acMeta.getProducts() & MDPropTags.MDLICENSE_IPLocator) != 0 || (acMeta.getProducts() & MDPropTags.MDLICENSE_Community) != 0) { return true; }
		return false;
	}

	public void outputData(MDCheckData checkData, List<MDCheckRequest> requests) {
		// Skip if not enabled
		if (!isEnabled()) { return; }
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Output the results
			IPLocatorResults ipResults = request.ipLocatorResults;
			if ((ipResults != null) && ipResults.valid) {
				// output results
				if (!Const.isEmpty(outIPAddress)) {
					request.addOutputData(ipResults.ipAddress);
				}
				if (!Const.isEmpty(outLatitude)) {
					request.addOutputData(ipResults.latitude);
				}
				if (!Const.isEmpty(outLongitude)) {
					request.addOutputData(ipResults.longitude);
				}
				if (!Const.isEmpty(outZip)) {
					request.addOutputData(ipResults.zip);
				}
				if (!Const.isEmpty(outRegion)) {
					request.addOutputData(ipResults.region);
				}
				if (!Const.isEmpty(outName)) {
					request.addOutputData(ipResults.name);
				}
				if (!Const.isEmpty(outDomain)) {
					request.addOutputData(ipResults.domain);
				}
				if (!Const.isEmpty(outCityName)) {
					request.addOutputData(ipResults.cityName);
				}
				if (!Const.isEmpty(outCountry)) {
					request.addOutputData(ipResults.country);
				}
				if (!Const.isEmpty(outAbbreviation)) {
					request.addOutputData(ipResults.abbreviation);
				}
				if (!Const.isEmpty(outConnectionSpeed)) {
					request.addOutputData(ipResults.connectionSpeed);
				}
				if (!Const.isEmpty(outConnectionType)) {
					request.addOutputData(ipResults.connectionType);
				}
				if (!Const.isEmpty(outUTC)) {
					request.addOutputData(ipResults.utc);
				}
				if (!Const.isEmpty(outContinent)) {
					request.addOutputData(ipResults.continent);
				}
				
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
				// TODO: Reroute this record to an invalid output stream?
			}
			if (ipResults != null) {
				if(AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)) {
					request.resultCodes.addAll(ipResults.resultCodes);
				}
			}
		}
	}
	
	private  void getInterfaceInfo(JSONObject jsonObj) {

		// Get the interface version not used
		@SuppressWarnings("unused")
		String version = (String) jsonObj.get("Version");
	}
	
	/**
	 * @param jsonRecord
	 * @param name
	 * @return
	 * @throws KettleException
	 */
	public static int getElementInteger(JSONObject jsonRecord, String name) throws KettleException {
		try {
			String text = (String) jsonRecord.get(name);
			if (text == null)
				throw new KettleException("Could not find integer value for " + name);
			int value = Integer.valueOf(text);
			return value;
		} catch (NumberFormatException e) {
			throw new KettleException("Problem getting integer value", e);
		}
	}

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	public static String getElementText(JSONObject element, String name) {
		String text = (String) element.get(name);
		if (text == null)
			return "";
		return text.trim();
	}
	
	public String processWebResponse(JSONObject jsonResponse, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Get the response array
		if (!jsonResponse.containsKey("Records")) { throw new KettleException("MDBusinessCoder: Response not found in JSON response"); }
		// Check the general result
		String resultCodes = (String) jsonResponse.get("TransmissionResults");

		if (!Const.isEmpty(resultCodes)) { return resultCodes; }
		// Get interface info
		getInterfaceInfo(jsonResponse);
		// Get the response records
		JSONArray jArray = (JSONArray) jsonResponse.get("Records");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> i = jArray.iterator();
		while (i.hasNext()) {
			JSONObject innerObj = i.next();
			if (innerObj.containsKey("IPAddress")) {
				int recordID = 0;
				try {
					recordID = getElementInteger(innerObj, "RecordID");
				} catch (KettleException ke) {
					throw new KettleException("No record ID found in response ");
				}

				// Get the request object for the specified record id
				MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
				// Process individual output results
				// FIXME FE03 remove with new service
				if (resultCodes.contains("FE03"))
					processWebResponseFields(innerObj, request, "FE03");
				else
					processWebResponseFields(innerObj, request, "");
			}
		}
		return "";
	}
	
	/*
	 * (non-Javadoc)
	 * @see AbstractAddressVerifyWebRequestHandler#processWebResponseFields(org.dom4j.Element,
	 * com.melissadata.kettle.MDBusinessCoderCVRequest)
	 */
	private void processWebResponseFields(JSONObject record, MDCheckCVRequest request, String fe03) throws KettleException {
	
		if((log != null) && log.isDetailed()){
			log.logDetailed(" Process Response : recID " + getElementInteger(record, "RecordID") + " : " + record.toJSONString());
		}
		
		IPLocatorResults ipResults = request.ipLocatorResults = new IPLocatorResults();
		// Result code for the individual request
		ipResults.resultCodes.addAll(MDCheck.getResultCodes(getElementText(record, "Result")));
		String ipAddress = getElementText(record, "IPAddress");
		if (ipAddress == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.IPNotFoundInElement")); }
		ipResults.ipAddress = ipAddress;
		ipResults.latitude = getElementText(record, "Latitude");
		ipResults.longitude = getElementText(record, "Longitude");
		ipResults.zip = getElementText(record, "PostalCode");
		ipResults.region = getElementText(record, "Region");
		ipResults.name = getElementText(record, "ISPName");
		ipResults.domain = getElementText(record, "DomainName");
		ipResults.cityName = getElementText(record, "City");
		ipResults.country = getElementText(record, "CountryName");
		ipResults.abbreviation = getElementText(record, "CountryAbbreviation");
		ipResults.connectionSpeed = getElementText(record, "ConnectionSpeed");
		ipResults.connectionType = getElementText(record, "ConnectionType");
		ipResults.utc = getElementText(record, "UTC");
		ipResults.continent = getElementText(record, "Continent");
		
		ipResults.valid = true;
		
	}

	public String processWebResponse(org.dom4j.Document doc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		// NOT used uses JSON  included from Interface
		return "";
	}

	public void readData(Node node) {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_IP_LOCATOR);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Input fields
			inIPAddress = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_IN_IPADDRESS), inIPAddress);
			// Output fields
			outIPAddress = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_IPADDRESS), outIPAddress);
			outLatitude = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_LATITUDE), outLatitude);
			outLongitude = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_LONGITUDE), outLongitude);
			outZip = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_ZIP), outZip);
			outRegion = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_REGION), outRegion);
			outName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_NAME), outName);
			outDomain = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_DOMAIN), outDomain);
			outCityName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_CITYNAME), outCityName);
			outCountry = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_COUNTRY), outCountry);
			outAbbreviation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_ABBREVIATION), outAbbreviation);
			outConnectionSpeed = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_CONNECT_SPEED), outConnectionSpeed);
			outConnectionType = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_CONNECT_TYPE), outConnectionType);
			outUTC = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_UTC), outUTC);
			outContinent = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUT_CONTINENT), outContinent);
			
		} else {
			setDefault();
		}
	}

	public void readRep(Repository rep, ObjectId idStep) throws KettleException {
		inIPAddress = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_IN_IPADDRESS), inIPAddress);
		outIPAddress = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_IPADDRESS), outIPAddress);
		outLatitude = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_LATITUDE), outLatitude);
		outLongitude = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_LONGITUDE), outLongitude);
		outZip = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_ZIP), outZip);
		outRegion = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_REGION), outRegion);
		outName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_NAME), outName);
		outDomain = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_DOMAIN), outDomain);
		outCityName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CITYNAME), outCityName);
		outCountry = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_COUNTRY), outCountry);
		outAbbreviation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_ABBREVIATION), outAbbreviation);
		outConnectionSpeed = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CONNECT_SPEED), outConnectionSpeed);
		outConnectionType = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CONNECT_TYPE), outConnectionType);
		outUTC = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_UTC), outUTC);
		outContinent = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CONTINENT), outContinent);
	}

	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_IN_IPADDRESS, inIPAddress);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_IPADDRESS, outIPAddress);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_LATITUDE, outLatitude);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_LONGITUDE, outLongitude);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_ZIP, outZip);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_REGION, outRegion);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_NAME, outName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_DOMAIN, outDomain);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CITYNAME, outCityName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_COUNTRY, outCountry);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_ABBREVIATION, outAbbreviation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CONNECT_SPEED, outConnectionSpeed);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CONNECT_TYPE, outConnectionType);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_UTC, outUTC);
		rep.saveStepAttribute(idTransformation, idStep, TAG_IP_LOCATOR + "." + TAG_OUT_CONTINENT, outContinent);
		
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	public void setDefault() {
		inIPAddress = "";
		if (MDCheckData.ipDefaultsSet) {
			outIPAddress = MDCheckData.ip_Address;
			outLatitude = MDCheckData.ip_Latitude;
			outLongitude = MDCheckData.ip_Longitude;
			outZip = MDCheckData.ip_Zip;
			outRegion = MDCheckData.ip_Region;
			outName = MDCheckData.ip_Name;
			outDomain = MDCheckData.ip_Domain;
			outCityName = MDCheckData.ip_CityName;
			outCountry = MDCheckData.ip_Country;
			outAbbreviation = MDCheckData.ip_Abreviation;
			outConnectionSpeed = MDCheckData.ip_ConnectionSpeed;
			outConnectionType = MDCheckData.ip_ConnectionType;
			outUTC = MDCheckData.ip_UTC;
			outContinent = MDCheckData.ip_Continent;
			
		} else {
			outIPAddress = "MD_IP_Address";
			outLatitude = "MD_IP_Latitude";
			outLongitude = "MD_IP_Longitude";
			outZip = "MD_IP_Zip";
			outRegion = "MD_IP_Region";
			outName = "MD_IP_Name";
			outDomain = "MD_IP_Domain";
			outCityName = "MD_IP_CityName";
			outCountry = "MD_IP_Country";
			outAbbreviation = "MD_IP_Country_Abbreviation";
			outConnectionSpeed = "MD_IP_Connection_Speed";
			outConnectionType = "MD_IP_Connection_Type";
			outUTC = "MD_IP_UTC";
			outContinent = "MD_IP_Continent";
		}
	}
	
	public String getOutConnectionSpeed() {
		return outConnectionSpeed;
	}
	
	public void setOutConnectionSpeed(String outConnectionSpeed) {
		this.outConnectionSpeed = outConnectionSpeed;
	}
	
	public String getOutConnectionType() {
		return outConnectionType;
	}
	
	public void setOutConnectionType(String outConnectionType) {
		this.outConnectionType = outConnectionType;
	}
	
	public String getOutUTC() {
		return outUTC;
	}
	
	public void setOutUTC(String outUTC) {
		this.outUTC = outUTC;
	}
	
	public String getOutContinent() {
		return outContinent;
	}
	
	public void setOutContinent(String outContinent) {
		this.outContinent = outContinent;
	}

	public String getOutAbreviation() {
		return outAbbreviation;
	}

	public String getOutCityName() {
		return outCityName;
	}

	public String getOutCountry() {
		return outCountry;
	}

	public String getOutDomain() {
		return outDomain;
	}

	public String getOutIPAddress() {
		return outIPAddress;
	}

	public String getOutLatitude() {
		return outLatitude;
	}

	public String getOutLongitude() {
		return outLongitude;
	}

	public String getOutName() {
		return outName;
	}

	public String getOutRegion() {
		return outRegion;
	}

	public String getOutZip() {
		return outZip;
	}

	public String getServiceName() {
		return "IP Locator";
	}

	public URL getWebURL(MDCheckData data, int queue) {
		return data.realWebIPLocatorURL;
	}
	
	public void setInIPAddress(String inIPAddress) {
		this.inIPAddress = inIPAddress;
	}

	public void setOutAbreviation(String abbreviation) {
		outAbbreviation = abbreviation;
	}

	public void setOutCityName(String cityName) {
		outCityName = cityName;
	}

	public void setOutCountry(String country) {
		outCountry = country;
	}

	public void setOutDomain(String domain) {
		outDomain = domain;
	}

	public void setOutIPAddress(String ipAddress) {
		outIPAddress = ipAddress;
	}

	public void setOutLatitude(String latitude) {
		outLatitude = latitude;
	}

	public void setOutLongitude(String longitude) {
		outLongitude = longitude;
	}

	public void setOutName(String name) {
		outName = name;
	}

	public void setOutRegion(String region) {
		outRegion = region;
	}

	public void setOutZip(String zip) {
		outZip = zip;
	}

	public void validate(List<String> warnings, List<String> errors) {
	}
}
