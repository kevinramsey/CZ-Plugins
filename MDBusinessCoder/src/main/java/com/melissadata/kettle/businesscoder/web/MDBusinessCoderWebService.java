package com.melissadata.kettle.businesscoder.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.MDProps;
import com.melissadata.kettle.businesscoder.MDBusinessCoderData;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.MDBusinessCoderStep;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.request.BusinessCoderRequestHandler;
import com.melissadata.kettle.businesscoder.request.MDBusinessCoderRequest;
import com.melissadata.kettle.businesscoder.request.MDBusinessCoderRequest.BusCoderResults;
import com.melissadata.kettle.businesscoder.request.RequestManager;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MDAbortException;
import com.melissadata.cz.support.MDPropTags;

public class MDBusinessCoderWebService{

	public static synchronized MDBusinessCoderWebService create(MDBusinessCoderData checkData, MDBusinessCoderMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		MDBusinessCoderWebService service = new MDBusinessCoderWebService(checkData, checkMeta, space, log);
		return service;
	}

	/**
	 * Given a list of general result codes, returns descriptions of those results codes
	 *
	 * @param resultCodes
	 * @return
	 */
	public static String getGeneralResultCodeMessages(Set<String> resultCodes) {
		return getResultCodeMessages("General", resultCodes);
	}

	/**
	 * Given a list of result codes, returns descriptions of those result codes
	 *
	 * @param prefix
	 * @param resultCodes
	 * @return
	 */
	public static String getResultCodeMessages(String prefix, Set<String> resultCodes) {
		StringBuffer message = new StringBuffer();
		String sep = "";
		for (String resultCode : resultCodes) {
			String description = BaseMessages.getString(PKG, "MDBusinessCoderMeta.ResultCode." + prefix + "." + resultCode);
			if ((description == null) || description.startsWith("!")) {
				description = BaseMessages.getString(PKG, "MDBusinessCoderMeta.ResultCode.Undefined");
			}
			message.append(sep).append(resultCode).append("=").append(description);
			sep = "; ";
		}
		return message.toString();
	}

	public static void setErrorMessage(String msg) {
		errorMessage = msg;
	}
	
	public static final String		TAG_BUSINESS_CODER_THREADS		= "business_coder_max_threads";
	public static final String		TAG_BUSINESS_CODER_REQUESTS		= "business_coder_max_requests";
	public static final String		TAG_BUSINESS_CODER_WEB_TIMEOUT	= "business_coder_timeout";
	public static final String		TAG_BUSINESS_CODER_WEB_RETRIES	= "business_coder_retries";
	private static final String		TAG_BUSINESS_CODER_URL			= "business_coder_url";

	public static final String		TAG_WEB_PROXY_HOST				= "proxy_host";
	public static final String		TAG_WEB_PROXY_PORT				= "proxy_port";
	public static final String		TAG_WEB_PROXY_USER				= "proxy_user";
	public static final String		TAG_WEB_PROXY_PASS				= "proxy_pass";
	public static final String		TAG_WEB_OPT_ABORT				= "opt_abort";
	
	private static Class<?>			PKG	= MDBusinessCoderMeta.class;
	private WebClient				webClient;
	private static String			errorMessage;
	public MDBusinessCoderData		busCoderData;
	protected MDBusinessCoderMeta	busCoderMeta;
	protected VariableSpace			space;
	public LogChannelInterface		log;
	protected boolean				initFailed;

	public MDBusinessCoderWebService(MDBusinessCoderData checkData, MDBusinessCoderMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		busCoderData = checkData;
		busCoderMeta = checkMeta;
		this.space = space;
		this.log = log;
		initFailed = false;
	}


	public MDBusinessCoderRequest buildRequest(IOMetaHandler ioMeta, Object[] inputData) {
		return new MDBusinessCoderRequest(ioMeta, inputData);
	}

	
	public boolean checkProxy() {
		
		StringBuffer xmlResponse = new StringBuffer(); // this is just a place holder
		String prMsg = "";
		int statCode = 0;
		statCode = getWebClient().checkProxy(busCoderData.realWebBusinessCoderURL, new StringBuffer(), xmlResponse);
		if (statCode != 200) {
			switch (statCode) {
				case 599:
					prMsg = statCode + ": Could not locate proxy";
					break;
				case 407:
					prMsg = statCode + ": Could not authenticate with proxy";
					break;
				case 3:
					prMsg = statCode + ": Access denied to proxy";
					break;
				case 0:
					prMsg = MDBusinessCoderData.webInitMsg;
					break;
				default:
					prMsg = statCode + ": Could not connect to proxy";
					break;
			}
			if (Const.isEmpty(busCoderData.realProxyHost)) {
				prMsg = statCode + ": Could not connect to " + busCoderData.realWebBusinessCoderURL;
			}
			MDBusinessCoderData.webInitMsg = prMsg;
			return false;
		}
		return true;
	}

	
	private void checkShowStoppingFault(String resultCodes) throws KettleException {
		// If there were any general result codes then throw an exception.
		Set<String> codes = MDBusinessCoderStep.getResultCodes(resultCodes);
		if (codes.size() > 0) {
			String message = MDBusinessCoderWebService.getGeneralResultCodeMessages(codes);
			// If not testing then check to see if we should abort the transform completely
			if (busCoderData.webAbortOnError)
				throw new MDAbortException(message);
			// The transform will either reroute the records to the log file or to
			// the error stream (if the later is defined).
			throw new KettleException(message);
		}
	}


	public RequestManager createRequestManager() throws KettleException {
		int maxThreads = Integer.parseInt(MDProps.getProperty(TAG_BUSINESS_CODER_THREADS, "1"));
		return new RequestManager(maxThreads);
	}

	
	public void dispose() {
		// Close the web clients (if there are any)
		if (webClient != null) {
			webClient.close();
		}
	}

	
	private void doWebRequests(BusinessCoderRequestHandler handler, List<MDBusinessCoderRequest> requests, int attempts) throws KettleException {

		try {
			// Create the main JSON Request 
			JSONObject jsonMain = new JSONObject();
			// Build the request document from the request data
			boolean sendRequest = handler.buildWebRequest(jsonMain, busCoderData, requests);
			// If there was at least one record then we send the request and process the response
			if (sendRequest) {
				// Post the requests and get the responses
				StringBuffer sbResponse = new StringBuffer();
				JSONObject jsonResponse = new JSONObject();
				int statusCode = 0;
				// Call web service
				statusCode = getWebClient().call(handler.getWebURL(busCoderData), jsonMain, sbResponse, attempts);
				// Check for problem
				if (statusCode != 200)
					throw new KettleException(MDBusinessCoderStep.getErrorString("StatusCodeNot200", "" + statusCode) + "-" + handler.toString() + "-" + errorMessage);
				
				JSONParser parser = new JSONParser();
				jsonResponse = (JSONObject) parser.parse(sbResponse.toString());

				// Process responses and extract results into request object
				String resultCodes = handler.processWebResponse(jsonResponse, busCoderData, requests);
				if (resultCodes.contains("SE01")) {
					// this is just to see where an error happens
					// sometimes we get no version# from web
					MDBusinessCoderData.webInitMsg = "Server Error SE01";
				}
				// Check for serious failure
				checkShowStoppingFault(resultCodes);
			}
		} catch (KettleException e) {
			// Re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened
			throw new KettleException(MDBusinessCoderStep.getErrorString("ProcessService", t.toString()), t);
		}
	}

	
	private WebClient getWebClient() {
		if (webClient == null) {
			// Create a web client
			webClient = new WebClient(log);
			webClient.setTimeout(busCoderData.realWebTimeout);
			webClient.setRetries(busCoderData.realWebRetries);
			webClient.setProxy(busCoderData.realProxyHost, busCoderData.realProxyPort, busCoderData.realProxyUser, busCoderData.realProxyPass);
		}
		return webClient;
	}

	
	public void init() throws KettleException {
		// Allocate request array
		busCoderData.requests = new ArrayList<MDBusinessCoderRequest>();
		try {
			MDProps.load();
			busCoderData.realLicense = MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "");
			// get maximum requests per batch
			try {
				busCoderData.maxRequests = Integer.parseInt(MDProps.getProperty(TAG_BUSINESS_CODER_REQUESTS, "1"));
				//FIXME change max requests
				if (busCoderData.maxRequests > 100)
					throw new KettleException("Business Coder batch capable to 1 records.  Please set max requests to 1");
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting max requests property");
			}
			// Get service URLs
			try {
				busCoderData.realWebBusinessCoderURL = new URL(space.environmentSubstitute(MDProps.getProperty(TAG_BUSINESS_CODER_URL, "")));
			} catch (MalformedURLException mle) {
				// Change the description
				throw new KettleException("Problem getting server url property: ");
			}
			// get timeout settings
			try {
				busCoderData.realWebTimeout = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(TAG_BUSINESS_CODER_WEB_TIMEOUT, "")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web timeout property");
			}
			try {
				busCoderData.realWebRetries = Integer.valueOf(space.environmentSubstitute(MDProps.getProperty(TAG_BUSINESS_CODER_WEB_RETRIES, "")));
			} catch (NumberFormatException e) {
				// Change the description
				throw new KettleException("Problem getting web retries property");
			}
			if (MDProps.getProperty(TAG_WEB_OPT_ABORT, "").equals("True")) {
				busCoderData.webAbortOnError = true;
			} else {
				busCoderData.webAbortOnError = false;
			}
			// get proxy settings
			busCoderData.realProxyHost = space.environmentSubstitute(MDProps.getProperty(TAG_WEB_PROXY_HOST, ""));
			busCoderData.realProxyPort = Const.toInt(space.environmentSubstitute(MDProps.getProperty(TAG_WEB_PROXY_PORT, "")), 8080);
			busCoderData.realProxyUser = MDProps.getProperty(TAG_WEB_PROXY_USER, "");
			busCoderData.realProxyPass = MDProps.getProperty(TAG_WEB_PROXY_PASS, "");
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened, return an initialization failure
			MDBusinessCoderData.webInitMsg = MDBusinessCoderStep.getErrorString("InitializeService", t.toString());
			busCoderData.webInitException = new KettleException(MDBusinessCoderData.webInitMsg, t);
			throw busCoderData.webInitException;
		}
	}

	
	public void outputData(List<MDBusinessCoderRequest> requests) {
		BusinessCoderFields bcf = busCoderMeta.businessCoderFields;
		for (MDBusinessCoderRequest request : requests) {
			BusCoderResults coderResults = request.busCoderResults;

			if ((coderResults != null) && coderResults.valid) {
				for (String key : bcf.outputFields.keySet()) {

					if ((key == BusinessCoderFields.TAG_OUTPUT_COMPANY_NAME) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_COMPANY_NAME).metaValue)) {
						request.addOutputData(coderResults.companyName);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_ADDRESS_LINE1) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue)) {
						request.addOutputData(coderResults.addressLine1);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_SUITE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SUITE).metaValue)) {
						request.addOutputData(coderResults.suite);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_CITY) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_CITY).metaValue)) {
						request.addOutputData(coderResults.city);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_STATE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_STATE).metaValue)) {
						request.addOutputData(coderResults.state);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_POSTAL_CODE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_POSTAL_CODE).metaValue)) {
						request.addOutputData(coderResults.postalCode);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_LOCATION_TYPE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCATION_TYPE).metaValue)) {
						request.addOutputData(coderResults.locationType);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_PHONE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_PHONE).metaValue)) {
						request.addOutputData(coderResults.phone);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_STOCK_TICKER) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_STOCK_TICKER).metaValue)) {
						request.addOutputData(coderResults.stockTicker);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_WEB_ADDRESS) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_WEB_ADDRESS).metaValue)) {
						request.addOutputData(coderResults.webAddress);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_EMPLOYEES_ESTIMATE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMPLOYEES_ESTIMATE).metaValue)) {
						request.addOutputData(coderResults.employeeEstimate);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_SALES_ESTIMATE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SALES_ESTIMATE).metaValue)) {
						request.addOutputData(coderResults.salesEstimate);
					}  else if ((key == BusinessCoderFields.TAG_OUTPUT_SIC_CODE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE).metaValue)) {
						request.addOutputData(coderResults.sicCode);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_SIC_CODE2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE2).metaValue)) {
						request.addOutputData(coderResults.sicCode2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_SIC_CODE3) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE3).metaValue)) {
						request.addOutputData(coderResults.sicCode3);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION).metaValue)) {
						request.addOutputData(coderResults.sicDescription);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION2).metaValue)) {
						request.addOutputData(coderResults.sicDescription2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION3) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION3).metaValue)) {
						request.addOutputData(coderResults.sicDescription3);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_NAICS_CODE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE).metaValue)) {
						request.addOutputData(coderResults.naicsCode);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_NAICS_CODE2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE2).metaValue)) {
						request.addOutputData(coderResults.naicsCode2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_NAICS_CODE3) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE3).metaValue)) {
						request.addOutputData(coderResults.naicsCode3);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION).metaValue)) {
						request.addOutputData(coderResults.naicsDescription);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION2).metaValue)) {
						request.addOutputData(coderResults.naicsDescription2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION3) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION3).metaValue)) {
						request.addOutputData(coderResults.naicsDescription3);
					} 	else if ((doCen()) && (key == BusinessCoderFields.TAG_OUTPUT_CENSUS_BLOCK) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_BLOCK).metaValue)) {
						request.addOutputData(coderResults.censusBlock);
					} else if ((doCen()) && (key == BusinessCoderFields.TAG_OUTPUT_CENSUS_TRACT) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_TRACT).metaValue)) {
						request.addOutputData(coderResults.censusTract);
					} else if ((doCen()) && (key == BusinessCoderFields.TAG_OUTPUT_COUNTY_FIPS) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_FIPS).metaValue)) {
						request.addOutputData(coderResults.countyFIPS);
					} else if ((doCen()) && (key == BusinessCoderFields.TAG_OUTPUT_COUNTY_NAME) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_NAME).metaValue)) {
						request.addOutputData(coderResults.countyName);
					} else if ((doCen()) && (key == BusinessCoderFields.TAG_OUTPUT_DELIVERY_INDICATOR) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_DELIVERY_INDICATOR).metaValue)) {
						request.addOutputData(coderResults.deliveryIndicator);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_LATITUDE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_LATITUDE).metaValue)) {
						request.addOutputData(coderResults.Latitude);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_LONGITUDE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_LONGITUDE).metaValue)) {
						request.addOutputData(coderResults.Longitude);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY).metaValue)) {
						request.addOutputData(coderResults.mdAdressKey);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY_BASE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY_BASE).metaValue)) {
						request.addOutputData(coderResults.mdAddressKeyBase);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_PLUS_4) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLUS_4).metaValue)) {
						request.addOutputData(coderResults.plus4);
					} else if ((doCen()) && (key == BusinessCoderFields.TAG_OUTPUT_PLACE_NAME) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_NAME).metaValue)) {
						request.addOutputData(coderResults.placeName);
					} else if ((doCen()) && (key == BusinessCoderFields.TAG_OUTPUT_PLACE_CODE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_CODE).metaValue)) {
						request.addOutputData(coderResults.placeCode);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_COUNTRY_CODE) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTRY_CODE).metaValue)) {
						request.addOutputData(coderResults.countryCode);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_COUNTRY_NAME) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTRY_NAME).metaValue)) {
						request.addOutputData(coderResults.countryName);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_EIN) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_EIN).metaValue)) {
						request.addOutputData(coderResults.ein);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_1) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_1).metaValue)) {
						request.addOutputData(coderResults.firstName1);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_LAST_NAME_1) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_1).metaValue)) {
						request.addOutputData(coderResults.lastName1);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_GENDER_1) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_1).metaValue)) {
						request.addOutputData(coderResults.gender1);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_TITLE_1) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_1).metaValue)) {
						request.addOutputData(coderResults.title1);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_EMAIL_1) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_1).metaValue)) {
						request.addOutputData(coderResults.email1);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_2).metaValue)) {
						request.addOutputData(coderResults.firstName2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_LAST_NAME_2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_2).metaValue)) {
						request.addOutputData(coderResults.lastName2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_GENDER_2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_2).metaValue)) {
						request.addOutputData(coderResults.gender2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_TITLE_2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_2).metaValue)) {
						request.addOutputData(coderResults.title2);
					} else if ((key == BusinessCoderFields.TAG_OUTPUT_EMAIL_2) && !Const.isEmpty(bcf.outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_2).metaValue)) {
						request.addOutputData(coderResults.email2);
					}

				}
			}
		}
	}

	//TODO Just a hack clean it up
	private boolean doCen(){
		//busCoderMeta.businessCoderFields
		return Boolean.valueOf(busCoderMeta.businessCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_INCLUDE_CENSUS).metaValue);
	}
	
	public void processRequests(List<MDBusinessCoderRequest> requests, int attempts) throws KettleException {
		BusinessCoderFields busCoderFields = busCoderMeta.businessCoderFields;
		BusinessCoderRequestHandler businessCoder = new BusinessCoderRequestHandler(busCoderFields);
		try {
			if (busCoderFields.hasMinRequirements()) {
				doWebRequests(businessCoder, requests, attempts);
			}
		} catch (KettleException e) {
			// Remember what went wrong
			if (e.toString().contains("GE05")) {
				businessCoder.webMsg = "Not Licensed";
			} else {
				businessCoder.webMsg = e.toString();
			}
			businessCoder.webException = e;
			throw businessCoder.webException;
		}
	}
}
