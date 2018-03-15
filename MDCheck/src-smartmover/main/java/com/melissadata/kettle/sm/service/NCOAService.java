package com.melissadata.kettle.sm.service;

import java.net.URL;
// import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import com.melissadata.kettle.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
// import javax.xml.rpc.ServiceException;
// import mdSmartMoverService.CustomerIDRequest;
// import mdSmartMoverService.CustomerIDResponse;
// import mdSmartMoverService.RespNCOALinkReportLink;
// import mdSmartMoverService.RspFault;
// import mdSmartMoverService.SmartMoverLocator;
// import mdSmartMoverService.SmartMoverSoap;
// import mdSmartMoverService.SmartMoverSoapStub;

public class NCOAService extends COAService {
	public enum NCOACustomerStatus {
		Unknown,
		Valid,
		Invalid
	}

	public enum NCOAPackageStatus {
		Unknown,
		Valid,
		NoPackage
	}

	public enum NCOAPAFStatus {
		Unknown,
		Valid,
		NoPAF,
		PAFExpired,
		PAFInvalid,
		PAFIdRequired
	}
	private String	jobID;
	private int		executionID;
	private int		recordsProcessed	= 0;
	public boolean	chkXML				= false;

	public NCOAService(MDCheckStepData stepData, MDCheckData checkData, boolean testing, LogChannelInterface log, VariableSpace space) throws KettleException {
		super(stepData, checkData, testing, log, space);
		// A common job id and execution id used for interaction with the NCOA service
		UUID guid = UUID.randomUUID();
		if (smartMover.isOptJobOverride()) {
			jobID = smartMover.getOptJobOverrideId();
		} else {
			jobID = guid.toString();
		}
		jobID = jobID.replace("-", "");
		if (jobID.length() > 15) {
			jobID = jobID.substring(0, 15);
		}
		smartMover.setJobID(jobID);
		executionID = 0;
		if (smartMover.isOptJobOverride() || smartMover.getOptionSummaryReports()) {
			String guidString = guid.toString().replace("-", "");
			for (int i = 0; i < 32; i += 4) {
				int guidNibble = Integer.parseInt(guidString.substring(i, i + 4), 16);
				executionID ^= guidNibble;
			}
		}
		smartMover.setExecutionID(String.valueOf(executionID));
		// Tell the user what the JobID is so they can use it later if they want:
		// TODO: Add some way to more prominently tell the customer what this job id is.
		log.logBasic(MDCheck.getLogString("USJobID", jobID));
		log.logBasic(MDCheck.getLogString("USExecutionID", "" + executionID));
		super.init();
	}

	/**
	 * @return The number of records processed by this service
	 */
	public int getRecordsProcessed() {
		return recordsProcessed;
	}

	/*
	 * (non-Javadoc)
	 * @see COAService#getVersion()
	 */
	@Override
	public String getVersion() {
		return smartMover.webSmVersion;
	}

	@Override
	public void incrTimeout() {
		// Do Nothing
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
		// If jobID not specified then use previous job id
		if (jobID == null) {
			jobID = this.jobID;
		}
		WebClient webClient = null;
		try {
			// Initialize the web client
			webClient = new WebClient(log);
			webClient.setTimeout(checkData.realWebTimeout);
			webClient.setRetries(checkData.realWebRetries);
			webClient.setProxy(checkData.realProxyHost, checkData.realProxyPort, checkData.realProxyUser, checkData.realProxyPass);
			// Get links to the report files
			org.dom4j.Document report = webClient.getSummeryLink(String.valueOf(customerID), pafID, String.valueOf(jobID));
			String result = getElementText(report.getRootElement(), "TransmissionResults");
			if (result.startsWith("SE")) {
				log.logError("WARRNING: Unable to get summery reports link for Jod ID " + jobID);
				log.logError(result + " - " + BaseMessages.getString(MDCheck.class, "MDCheck.ResultCode.General." + result));
				String path = ncoaFile.substring(0, ncoaFile.lastIndexOf(Const.FILE_SEPARATOR) + 1);
				log.logError("See " + path + "SmartMover_dowload_result.txt  for details");
				throw new KettleException("Unable to get summery reports link for Jod ID: " + jobID + "\nReason: " + result + " - " + BaseMessages.getString(MDCheck.class, "MDCheck.ResultCode.General." + result));
			}
			String cassUrl = getElementText(report.getRootElement(), "CASSReportLink");
			String ncoasummeryUrl = getElementText(report.getRootElement(), "NCOAReportLink");
			// Download the files
			if (!Const.isEmpty(ncoaFile) && !ncoasummeryUrl.isEmpty()) {
				if (log.isBasic()) {
					log.logBasic("Downloading NCOA Summary Report to " + ncoaFile);
				}
				webClient.downloadFile(new URL(ncoasummeryUrl), ncoaFile);
			}
			if (!Const.isEmpty(cassFile) && !cassUrl.isEmpty()) {
				if (log.isBasic()) {
					log.logBasic("Downloading CASS Summary Report to " + cassFile);
				}
				webClient.downloadFile(new URL(cassUrl), cassFile);
			}
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			// Wrap any problem in a standard exception
			String message = MDCheck.getErrorString("SaveReports", t.toString());
			log.logError(message);
			throw new KettleException(message, t);
		} finally {
			if (webClient != null) {
				webClient.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see COAService#submitRecordSet(java.util.List)
	 */
	@Override
	public String submitRecordSet(List<MDCheckRequest> requests, int queue, int attempts) throws KettleException {
		try {
			// Done if there are no records or we are not testing
			int recordCount = testing ? 1 : requests.size();
			if (recordCount == 0) { return ""; }
			recordsProcessed += recordCount;
			// 0 is the queue for ncoa
			processRequests(requests, queue, false, attempts);
			return "";
		} catch (KettleException e) {
			// Re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened
			throw new KettleException(MDCheck.getErrorString("ProcessService", t.toString()), t);
		}
	}

	/**
	 * Test the status of the customer id.
	 *
	 * @throws KettleException
	 */
	@Override
	public void testCustomerStatus() throws KettleException {
		/*
		 * try {
		 * //FIXME this is currently not implemented but if at
		 * //some point we do want to test PAF status we will need to implement
		 * // Make the request
		 * final CustomerIDRequest request = new CustomerIDRequest();
		 * request.setCustomerId("" + customerID);
		 * request.setMelissaId(CUSTOMERIDCHECK_ID);
		 * request.setPafId(pafID);
		 * // Perform it in a thread pool
		 * final RemoteException re[] = new RemoteException[1];
		 * final CustomerIDResponse response = (CustomerIDResponse) call(new COARunnable() {
		 * public Object run() {
		 * return null;
		 * //try {
		 * // return null;//soapService.getCustomerIdStatus(request, pafID);
		 * //} catch (RemoteException e) {
		 * // re[0] = e;
		 * // return null;
		 * //}
		 * }
		 * }, 0);
		 * if (re[0] != null)
		 * throw re[0];
		 * // Get the customer status
		 * StringBuffer temp = new StringBuffer();
		 * if (!GetCustomerProperty(response.getRequestedProperties(), response.getProperties(), "Status", temp)) {
		 * smartMover.ncoaCustomerStatus = NCOACustomerStatus.Unknown;
		 * }
		 * else {
		 * String status = temp.toString();
		 * if (!status.equals("1"))
		 * smartMover.ncoaCustomerStatus = NCOACustomerStatus.Invalid;
		 * else
		 * smartMover.ncoaCustomerStatus = NCOACustomerStatus.Valid;
		 * }
		 * // Get the exiration date (TODO: not currently checked)
		 * //if (!GetCustomerProperty(response.getRequestedProperties(), response.getProperties(), "ExpirationDate", temp)) {
		 * // smartMover.ncoaCustomerStatus = NCOACustomerStatus.Unknown;
		 * // return;
		 * //}
		 * //String expirationDate = temp.toString();
		 * // Get the PAF status
		 * temp = new StringBuffer();
		 * if (!GetCustomerProperty(response.getRequestedStatuses(), response.getStatuses(), "PafStatus", temp)) {
		 * smartMover.ncoaPAFStatus = NCOAPAFStatus.Unknown;
		 * }
		 * else {
		 * String pafStatus = temp.toString();
		 * if (pafStatus.equals("0"))
		 * smartMover.ncoaPAFStatus = NCOAPAFStatus.NoPAF;
		 * else if (pafStatus.equals("2") || pafStatus.equals("4"))
		 * smartMover.ncoaPAFStatus = NCOAPAFStatus.PAFExpired;
		 * else if (pafStatus.equals("3") || pafStatus.equals("5") || pafStatus.equals("6"))
		 * smartMover.ncoaPAFStatus = NCOAPAFStatus.PAFInvalid;
		 * else if (pafStatus.equals("7"))
		 * smartMover.ncoaPAFStatus = NCOAPAFStatus.PAFIdRequired;
		 * else if (!pafStatus.equals("1"))
		 * smartMover.ncoaPAFStatus = NCOAPAFStatus.NoPAF;
		 * else
		 * smartMover.ncoaPAFStatus = NCOAPAFStatus.Valid;
		 * }
		 * // Get the package status
		 * temp = new StringBuffer();
		 * if (!GetCustomerProperty(response.getRequestedPackages(), response.getPackages(), "pkgSmartMover", temp)) {
		 * smartMover.ncoaPackageStatus = NCOAPackageStatus.Unknown;
		 * }
		 * else {
		 * String pkgSmartMover = temp.toString();
		 * if (!pkgSmartMover.toString().equals("1"))
		 * smartMover.ncoaPackageStatus = NCOAPackageStatus.NoPackage;
		 * else
		 * smartMover.ncoaPackageStatus = NCOAPackageStatus.Valid;
		 * }
		 * } catch (KettleException e) {
		 * // Re-throw
		 * throw e;
		 * } catch (Throwable t) {
		 * // If anything unusual happened
		 * throw new KettleException(MDCheck.getErrorString("ProcessService", t.toString()), t);
		 * }
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see COAService#getURL()
	 */
	@Override
	protected URL getURL() {
		return checkData.realNCOAURL;
	}
}
