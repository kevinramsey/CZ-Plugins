package com.melissadata.kettle.sm.service;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.sm.SmartMoverMeta;
import com.melissadata.kettle.sm.request.MDCheckCOARequest;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.lang.StringEscapeUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.variables.VariableSpace;

import com.melissadata.kettle.sm.request.MDCheckCOARequest.COAResults;

public abstract class COAService extends MDCheckWebService {
	public interface COARunnable {
		Object run();
	}

	/**
	 * Called to decode safely transmitted data
	 *
	 * @param value
	 * @return
	 */
	protected static String decode(String value) {
		if (value == null) { return null; }
		// This is silly, but there appears to be a bug in the smart mover return code that incorrecly capitalizes some
// escape sequences.
		value = value.replaceAll("&Amp;", "&amp;");
		value = value.replaceAll("&Apos;", "&apos;");
		value = value.replaceAll("&Gt;", "&gt;");
		value = value.replaceAll("&Lt;", "&lt;");
		value = value.replaceAll("&Quot;", "&quot;");
		String newValue = StringEscapeUtils.unescapeXml(value);
		if (!newValue.equals(value)) { return newValue; }
		return newValue;
	}

	/**
	 * Called to encode data for safe transmission
	 *
	 * @param value
	 * @return
	 */
	protected static String encode(String value) {
		String newValue = StringEscapeUtils.escapeXml(value);
		if (!newValue.equals(value)) { return newValue; }
		return newValue;
	}

	/**
	 * Called to encode Address data for safe transmission
	 *
	 * @param value
	 * @return
	 */
	protected static String encodeAddr(String value) {
		// new code to remove last tab( kinda ugly)
		int mark = 0;
		StringBuilder sb = new StringBuilder();
		String newValue = StringEscapeUtils.escapeXml(value);
		char[] ccar = newValue.toCharArray();
		for (int e = 0; e < ccar.length; e++) {
			if ((ccar[e] == '&') && (ccar[e + 1] == '#') && (ccar[e + 2] == '1') && (ccar[e + 3] == '6') && (ccar[e + 4] == '0')) {
				mark = e;
			}
		}
		sb.append(ccar);
		if (mark > 0) {
			sb.delete(mark, mark + 6);
		}
		newValue = new String(sb);
		if (!newValue.equals(value)) { return newValue; }
		return newValue;
	}
	private static ExecutorService	threadPool	= Executors.newCachedThreadPool();
	protected MDCheckStepData     stepData;
	protected MDCheckData         checkData;
	protected boolean             testing;
	protected LogChannelInterface log;
	protected int                 customerID;
	protected String              pafID;
	protected int                 maxRequests;
	protected int                 timeout;
	protected int                 retries;
	protected SmartMoverMeta      smartMover;
	protected boolean             needParsed;

	public COAService(MDCheckStepData stepData, MDCheckData checkData, boolean testing, LogChannelInterface log, VariableSpace space) {
		super(stepData, checkData, space, log);
		this.stepData = stepData;
		this.checkData = checkData;
		this.testing = testing;
		this.log = log;
		customerID = checkData.realCustomerID;
		pafID = checkData.realPAFID;
		maxRequests = checkData.maxRequests;
		timeout = checkData.realWebTimeout;
		retries = checkData.realWebRetries;
		smartMover = stepData.getSmartMover();
		needParsed = smartMover.needParsed();
	}

	/**
	 * @return The determined version of the service
	 */
	public abstract String getVersion();

	public abstract void incrTimeout();

	/**
	 * Called to output data from the queued results
	 *
	 * @param Xrequests
	 */
	public void outputData(List<MDCheckRequest> requests) {
		// Output each request's results
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCOARequest request = (MDCheckCOARequest) mdCheckRequest;
			// Output the email results
			COAResults coaResults = request.coaResults;
			if ((coaResults != null) && coaResults.valid) {
				// Add the output fields
				if (!Const.isEmpty(smartMover.getOutputFullName())) {
					request.addOutputData(coaResults.FullName);
				}
				if (!Const.isEmpty(smartMover.getOutputNamePrefix())) {
					request.addOutputData(coaResults.NamePrefix);
				}
				if (!Const.isEmpty(smartMover.getOutputNameFirst())) {
					request.addOutputData(coaResults.NameFirst);
				}
				if (!Const.isEmpty(smartMover.getOutputNameMiddle())) {
					request.addOutputData(coaResults.NameMiddle);
				}
				if (!Const.isEmpty(smartMover.getOutputNameLast())) {
					request.addOutputData(coaResults.NameLast);
				}
				if (!Const.isEmpty(smartMover.getOutputNameSuffix())) {
					request.addOutputData(coaResults.NameSuffix);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrCompany())) {
					request.addOutputData(coaResults.CompanyName);
				}
				// Special handling for address lines:
				// Suites and private mailboxes added to address line 2 if not separately configured
				// Address line 2 added to address line 1 if address line 2 not separately configured
				String address = coaResults.Address1;
				String address2 = coaResults.Address2;
				if (Const.isEmpty(smartMover.getOutputAddrSuite())) {
					address2 += " " + coaResults.Suite;
				}
				if (Const.isEmpty(smartMover.getOutputAddrPMB())) {
					address2 += " " + coaResults.PrivateMailBox;
				}
				if (Const.isEmpty(smartMover.getOutputAddrLine2())) {
					address += " " + address2;
				}
				if (!Const.isEmpty(smartMover.getOutputAddrLine())) {
					request.addOutputData(address);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrLine2())) {
					request.addOutputData(address2);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrSuite())) {
					request.addOutputData(coaResults.Suite);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrPMB())) {
					request.addOutputData(coaResults.PrivateMailBox);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrUrbanization())) {
					request.addOutputData(coaResults.Urbanization);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrCity())) {
					request.addOutputData(coaResults.CityName);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrState())) {
					request.addOutputData(coaResults.StateAbbreviation);
				}
				// Add plus 4 to zip if not separately configured
				String zip = coaResults.Zip;
				String plus4 = coaResults.Plus4;
				if (Const.isEmpty(smartMover.getOutputAddrPlus4())) {
					if (!Const.isEmpty(plus4)) {
						zip += "-" + plus4;
					}
				}
				if (!Const.isEmpty(smartMover.getOutputAddrZip())) {
					request.addOutputData(zip);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrPlus4())) {
					request.addOutputData(plus4);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrCountry())) {
					request.addOutputData(coaResults.CountryName);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrDPAndCheckDigit())) {
					// Combine Delivery Point and DP Check Digit
					request.addOutputData(coaResults.DeliveryPointCode + coaResults.DeliveryPointCheckDigit);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrCarrierRoute())) {
					request.addOutputData(coaResults.CarrierRoute);
				}
// if (!Const.isEmpty(smartMover.getOutputAddrDPVCMRA()))
// request.addOutputData(ncoaResults.DPVCMRA);
				if (!Const.isEmpty(smartMover.getOutputAddrKey())) {
					request.addOutputData(coaResults.AddressKey);
				}
				if (!Const.isEmpty(smartMover.getOutputMelissaAddrKey())) {
					request.addOutputData(coaResults.MelissaAddressKey);
				}
				if (!Const.isEmpty(smartMover.getOutputBaseMelissaAddrKey())) {
					request.addOutputData(coaResults.BaseMelissaAddressKey);
				}
// if (!Const.isEmpty(smartMover.getOutputAddrType()))
// request.addOutputData(ncoaResults.TypeAddressCode);
// if (!Const.isEmpty(smartMover.getOutputAddrTypeString()))
// request.addOutputData(ncoaResults.TypeAddressDescription);
// if (!Const.isEmpty(smartMover.getOutputAddrZipType()))
// request.addOutputData(ncoaResults.TypeZipCode);
// if (!Const.isEmpty(smartMover.getOutputAddrZipTypeString()))
// request.addOutputData(ncoaResults.TypeZipDescription);
				if (!Const.isEmpty(smartMover.getOutputAddrCityAbbreviation())) {
					request.addOutputData(coaResults.CityAbbreviation);
				}
				if (!Const.isEmpty(smartMover.getOutputAddrCountryAbbreviation())) {
					request.addOutputData(coaResults.CountryAbbreviation);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedAddrRange())) {
					request.addOutputData(coaResults.ParsedAddressRange);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedPreDirectional())) {
					request.addOutputData(coaResults.ParsedDirectionPre);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedStreetName())) {
					request.addOutputData(coaResults.ParsedStreetName);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedSuffix())) {
					request.addOutputData(coaResults.ParsedSuffix);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedPostDirectional())) {
					request.addOutputData(coaResults.ParsedDirectionPrePost);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedSuiteName())) {
					request.addOutputData(coaResults.ParsedSuiteName);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedSuiteRange())) {
					request.addOutputData(coaResults.ParsedSuiteRange);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedPMBName())) {
					request.addOutputData(coaResults.ParsedPMBName);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedPMBRange())) {
					request.addOutputData(coaResults.ParsedPMBRange);
				}
				if (!Const.isEmpty(smartMover.getOutputParsedExtraInfo())) {
					request.addOutputData(coaResults.ParsedExtraInformation);
				}
// if (!Const.isEmpty(smartMover.getOutputParsedRouteService()))
// request.addOutputData(ncoaResults.ParsedRouteService);
// if (!Const.isEmpty(smartMover.getOutputParsedLockBox()))
// request.addOutputData(ncoaResults.ParsedLockBox);
// if (!Const.isEmpty(smartMover.getOutputParsedDeliveryInstallation()))
// request.addOutputData(ncoaResults.ParsedDeliveryInstallation);
				if (!Const.isEmpty(smartMover.getOutputEffectiveDate())) {
					request.addOutputData(coaResults.EffectiveDate);
				}
				if (!Const.isEmpty(smartMover.getMoveTypeCode())) {
					request.addOutputData(coaResults.MoveTypeCode);
				}
				if (!Const.isEmpty(smartMover.getMoveReturnCode())) {
					request.addOutputData(coaResults.MoveReturnCode);
				}
				if (!Const.isEmpty(smartMover.getOutputDPVFootnotes())) {
					request.addOutputData(coaResults.DPVFootnotes);
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += smartMover.getFieldsAdded();
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
				// TODO: Reroute this record to an invalid output stream?
			}
			// Add the SmartMover result codes to the overall result codes
			if(AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_SmartMover)) {
				request.resultCodes.addAll(coaResults.resultCodes);
			}
		}
	}

	/**
	 * Called to submit records to the service and to get results back
	 * BadCustomerID
	 *
	 * @param requests
	 * @throws KettleException
	 */
	public abstract String submitRecordSet(List<MDCheckRequest> requests, int queue, int attempts) throws KettleException;

	/**
	 * Called to call the service within a thread pool
	 *
	 * @param runnable
	 * @return
	 * @throws KettleException
	 */
	protected Object call(final COARunnable runnable, int attempts) throws KettleException {
		String timeoutMessage;
		try {
			if (log.isDetailed()) {
				log.logDetailed(MDCheck.getLogString("ConnectingToURL", getURL().toString(), "" + retries));
			}
			// Perform the call within a thread pool so that we can handle
			// all manners of timeouts that the service cannot
			FutureTask<Object> task = new FutureTask<Object>(new Callable<Object>() {
				public Object call() throws Exception {
					return runnable.run();
				}
			});
			threadPool.execute(task);
			// This call will not return until it has a result or it times out
			Object response;
			if (timeout > 0) {
				int myTime = timeout + getTimeIncrease(attempts);
				// Extend the length of the timeout by 2 seconds on each
				// retry. We make this timeout slightly longer so as to allow the http
				// client timeouts to trigger first
				response = task.get((myTime + (long) (myTime * 0.1) + 1), TimeUnit.SECONDS);
			} else {
				response = task.get();
			}
			return response;
		} catch (TimeoutException e) {
			// Thread timed out
			timeoutMessage = MDCheck.getErrorString("ConnectTimeout");;
		} catch (Throwable t) {
			// Get exception from thread process
			if (t instanceof ExecutionException) {
				t = t.getCause();
			}
			// timed out during connection attempt
			if (t instanceof ConnectTimeoutException) {
				timeoutMessage = MDCheck.getErrorString("ConnectTimeout");
				// timed out during HTTP read/writes
			} else if (t instanceof SocketTimeoutException) {
				timeoutMessage = MDCheck.getErrorString("SocketTimeout");
			} else {
				// Catchall
				throw new KettleException(MDCheck.getErrorString("ProblemInCall", t.toString()));
			}
		}
		// Log the timeout message and fall thru to the retry check
		if (log.isError()) {
			log.logError(MDCheck.getErrorString("TimeoutDetected", timeoutMessage, getURL().toString()));
		}
		// Retry?
		retries--;
		// We throw here so all retries can hapen in one place
		throw new KettleException(MDCheck.getErrorString("TimeoutDetected", timeoutMessage, getURL().toString()));
	}

	protected int getTimeIncrease(int attempts) {
		int increase = 0;
		switch (attempts) {
			case 0:
				increase = 0;
				break;
			case 1:
				increase = 2;
				break;
			case 2:
				increase = 4;
				break;
			case 3:
				increase = 8;
				break;
			case 4:
				increase = 16;
				break;
			default:
				increase = 32;
				break;
		}
		return increase;
	}

	/**
	 * @return The url of this service
	 */
	protected abstract URL getURL();
}
