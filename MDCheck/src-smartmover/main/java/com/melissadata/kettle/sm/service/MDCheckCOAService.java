package com.melissadata.kettle.sm.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.Set;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.sm.SmartMoverMeta;
import com.melissadata.kettle.sm.request.MDCheckCOARequest;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.support.MDAbortException;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;

public class MDCheckCOAService extends MDCheckWebService {
	private static Class<?>					PKG			= MDCheckCOAService.class;
	private NCOAService	ncoaService;
	private CCOAService	ccoaService;

	public MDCheckCOAService(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {
		super(stepData, checkData, space, log);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#buildRequest(com.melissadata.kettle.support.IOMeta, java.lang.Object[])
	 */
	@Override
	public MDCheckRequest buildRequest(IOMeta ioMeta, Object[] inputData) {
		// FIXME: Use a pool of request objects?
		return new MDCheckCOARequest(ioMeta, inputData);
	}

	/**
	 * Called to determine if the fault code should immediate stop processing.
	 *
	 * @param resultCodes
	 * @throws KettleException
	 */
	public void checkShowStoppingFault(String resultCodes) throws KettleException {
		// If there were any general result codes then throw an exception.
		Set<String> codes = MDCheck.getResultCodes(resultCodes);
		if (codes.size() > 0) {
			String message = MDCheckWebService.getGeneralResultCodeMessages(codes);
			// If not testing then check to see if we should abort entirely
			if (!testing && checkData.webAbortOnError) { throw new MDAbortException(message); }
			// The transform will either reroute the records to the log file or to
			// the error stream (if the later is defined).
			throw new KettleException(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#determineRequestRoute(com.melissadata.kettle.MDCheckRequest)
	 */
	@Override
	public int determineRequestRoute(MDCheckRequest request) throws KettleValueException {
		SmartMoverMeta smartMover = stepData.getSmartMover();
		// See if the routing was explicitly configured
		if (smartMover.getOptionCountries() == SmartMoverMeta.Countries.US) { return 0; }
		if (smartMover.getOptionCountries() == SmartMoverMeta.Countries.Canada) { return 1; }
		// See if we can get the country field from the input data
		String country = MDCheck.getFieldString(request.inputMeta, request.inputData, smartMover.getInputAddrCountry());
		if (!Const.isEmpty(country)) {
			// Shortcut for common country names
			if (country.equalsIgnoreCase("US") || country.equalsIgnoreCase("USA") || country.equalsIgnoreCase("United States") || country.equalsIgnoreCase("United States of America")) {
				return 0;
			} else if (country.equalsIgnoreCase("CA") || country.equalsIgnoreCase("Can") || country.equalsIgnoreCase("Canada")) { return 1; }
		}
		// See if we can get the postal code from the input data
		String zip = MDCheck.getFieldString(request.inputMeta, request.inputData, smartMover.getInputAddrZip());
		if (!Const.isEmpty(zip)) {
			zip = zip.trim();
			// Shortcut for common zip formats
			if (((zip.length() == 5) || (zip.length() == 9) || (zip.length() == 10)) && Character.isDigit(zip.charAt(0)) && Character.isDigit(zip.charAt(1)) && Character.isDigit(zip.charAt(2)) && Character.isDigit(zip.charAt(3))
					&& Character.isDigit(zip.charAt(4))) {
				return 0;
			} else if ((zip.length() == 6) && Character.isLetter(zip.charAt(0)) && Character.isDigit(zip.charAt(1)) && Character.isLetter(zip.charAt(2)) && Character.isDigit(zip.charAt(4)) && Character.isLetter(zip.charAt(5))
					&& Character.isDigit(zip.charAt(6))) {
				return 1;
			} else if ((zip.length() == 7) && Character.isLetter(zip.charAt(0)) && Character.isDigit(zip.charAt(1)) && Character.isLetter(zip.charAt(2)) && Character.isDigit(zip.charAt(4)) && Character.isLetter(zip.charAt(5))
					&& Character.isDigit(zip.charAt(6))) { return 1; }
		}
		// TODO: Make request of address verifier to determine the country
		return 0;
		// int attempts;
		// mdAddressCheck.ResponseArray response = new mdAddressCheck.ResponseArray();
		//
		//
		// mdAddressCheck.RequestArray request = new mdAddressCheck.RequestArray();
		// request.CustomerID = mdLicense.DecryptString(SmartMoverConstants.ADDRESSCHECK_CUSTOMER_ID);
		// request.Record = new mdAddressCheck.RequestArrayRecord[1];
		// request.Record[0] = new mdAddressCheck.RequestArrayRecord();
		// request.Record[0].Company = input_.Company;
		// request.Record[0].AddressLine1 = input_.Address;
		// request.Record[0].AddressLine2 = input_.Address2;
		// request.Record[0].Suite = input_.Suite + " " + input_.PrivateMailbox;
		// request.Record[0].City = input_.City;
		// request.Record[0].State = input_.State;
		// request.Record[0].Zip = input_.Zip;
		// request.Record[0].Plus4 = input_.Plus4;
		// request.Record[0].Country = input_.Country;
		//
		// for (attempts = 0; attempts <= Retries; attempts++) {
		// try {
		// response = Service.doAddressCheck(request);
		// break;
		// } catch (Exception) {
		// }
		// }
		//
		// if (attempts <= Retries && response.Results.Trim() == "") {
		// if (response.Record[0].Address.Country.Abbreviation == "US")
		// return eCountry.US;
		// else if (response.Record[0].Address.Country.Abbreviation == "CA")
		// return eCountry.Canada;
		// }
		// return eCountry.US;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#init()
	 */
	@Override
	public void init() throws KettleException {
		// Call parent handler first
		super.init();
		try {
			// Get configuration
			AdvancedConfigurationMeta acMeta = stepData.getAdvancedConfiguration();
			SmartMoverMeta smartMover = stepData.getSmartMover();
			// Get the real Customer id
			try {
				checkData.realCustomerID = Integer.parseInt(acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_SmartMover));
			} catch (NumberFormatException e) {
				String exception = MDCheck.getErrorString("BadCustomerID", acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_SmartMover));
				// Change the description
				if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_SmartMover)
						&& AdvancedConfigurationMeta.isCommunity()){
					exception = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", "Smart Mover");
					MDCheck.showSleepMsg(exception, stepData.getCheckTypes());
				}

				throw new NumberFormatException(exception);
			}
			// Get the real PAF id
			checkData.realPAFID = smartMover.getOptPAFId();
			// get maximum requests per batch
			checkData.maxRequests = acMeta.getMaxRequests();
			// Get the real URLs
			checkData.realNCOAURL = new URL(space.environmentSubstitute(acMeta.getWebNCOAURL()));
			checkData.realCCOAURL = new URL(space.environmentSubstitute(acMeta.getWebCCOAURL()));
			// get timeout settings
			checkData.realWebTimeout = Integer.valueOf(space.environmentSubstitute(acMeta.getWebTimeout()));
			checkData.realWebRetries = Integer.valueOf(space.environmentSubstitute(acMeta.getWebRetries()));
			// get proxy settings
			checkData.realProxyHost = space.environmentSubstitute(acMeta.getWebProxyHost());
			checkData.realProxyPort = Const.toInt(space.environmentSubstitute(acMeta.getWebProxyPort()), 8080);
			checkData.realProxyUser = acMeta.getWebProxyUser();
			checkData.realProxyPass = acMeta.getWebProxyPass();
			// abort handling
			checkData.webAbortOnError = acMeta.isWebAbortOnError();
			// Allocate COA service handlers
			// TODO Do we always need both handlers ?
			ncoaService = new NCOAService(stepData, checkData, testing, log, space);
			ccoaService = new CCOAService(stepData, checkData, testing, log, space);
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened, return an initialization failure
			MDCheckStepData.webInitMsg = MDCheck.getErrorString("InitializeService", t.toString());
			stepData.webInitException = new KettleException(MDCheckStepData.webInitMsg, t);
			if (!testing) { throw stepData.webInitException; }
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#outputData(int)
	 */
	@Override
	public void outputData(List<MDCheckRequest> requests, int queue) {
		// Get handler based on the queue
		COAService service = (queue == 0) ? ncoaService : ccoaService;
		// Process the requests
		service.outputData(requests);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#processRequests(int)
	 */
	@Override
	public void processRequests(List<MDCheckRequest> requests, int queue, boolean ckXML, int attempts) throws KettleException {
		// Process the smart mover requests
		SmartMoverMeta smartMover = stepData.getSmartMover();
		ncoaService.chkXML = ckXML;
		try {
			// Get handler based on the queue
			COAService service = (queue == 0) ? ncoaService : ccoaService;
			service.submitRecordSet(requests, queue, attempts);
			// TODO: if result code includes WSE01 then do a retry
			// Check for serious failure
			// checkShowStoppingFault(resultCodes);
			// Remember the version
			smartMover.webSmartMoverVersion[queue] = service.getVersion();
		} catch (KettleException e) {
			// Remember what went wrong
			smartMover.webSmartMoverMsg[queue] = e.toString();
			smartMover.webSmartMoverException[queue] = e;
			if (!testing) { throw smartMover.webSmartMoverException[queue]; }
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#saveReports()
	 */
	@Override
	public void saveReports() throws KettleException {
		// Handle smart mover reports
		SmartMoverMeta smartMover = stepData.getSmartMover();
		if (smartMover.getOptionSummaryReports()) {
			// Dereference the COA service and call it tell it to download the reports
			String ncoaFile = smartMover.getOptionNCOAFile();
			String cassFile = smartMover.getOptionCASSFile();
			// Only download if records were processed
			if ((ncoaService.getRecordsProcessed() > 0) && (!Const.isEmpty(ncoaFile) || !Const.isEmpty(cassFile))) {
				saveReports(null, ncoaFile, cassFile);
			}
		}
	}

	/**
	 * Called during finalization to request and save summary reports. Will not be called if they weren't generated.
	 *
	 * @param jobID
	 * @param ncoaFile
	 * @param cassFile
	 * @throws KettleException
	 */
	public void saveReports(String jobID, String ncoaFile, String cassFile) throws KettleException {
		// Only done on NCOA (United States)
		boolean retry = true;
		int trys = 2;
		do {
			try {
				stepData.getSmartMover().webSMException = null;
				ncoaService.saveReports(jobID, ncoaFile, cassFile);
				retry = false;
			} catch (KettleException ke) {
				// Already Logged so ignore
				trys--;
				stepData.getSmartMover().webSMException = ke;
				stepData.getSmartMover().webSmMsg = ke.getMessage();
				if (trys > 0) {
					log.logBasic("Error getting reports retrying");
				}
			}
		} while (retry && (trys > 0));
		writeDownloadReport(jobID, ncoaFile, cassFile);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#testCustomerStatus()
	 */
	@Override
	public void testCustomerStatus() throws KettleException {
		ncoaService.testCustomerStatus();
	}

	public void writeDownloadReport(String jobID, String ncoaFile, String cassFile) {
		// getOptionListName()
		String listName = stepData.getSmartMover().getOptionListName();
		String locPath = ncoaFile.substring(0, ncoaFile.lastIndexOf(Const.FILE_SEPARATOR) + 1);
		File dlReport = new File(locPath + "SmartMover_dowload_result.txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(dlReport));
			writer.write("Download Report: " + stepData.getSmartMover().webSmMsg);
			writer.newLine();
			writer.write("List Name: " + listName);
			writer.newLine();
			writer.write("Job ID: " + stepData.getSmartMover().getJobID());
			writer.newLine();
			writer.write("Forms: " + ncoaFile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}
}
