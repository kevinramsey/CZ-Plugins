package com.melissadata.kettle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.melissadata.kettle.cv.address.AddressVerifyMeta;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta;
import com.melissadata.kettle.cv.name.NameParseMeta;
import com.melissadata.kettle.support.*;
import com.melissadata.kettle.report.ReportDataEngine;
import com.melissadata.kettle.report.ReportRunner;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.ui.spoon.Spoon;
import com.melissadata.cz.support.MessageBoxThread;
import com.melissadata.kettle.RequestManager.Request;

public class MDCheck extends BaseStep implements StepInterface {

	/**
	 * @param key
	 * @return
	 */
	public static String getErrorString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDCheck.Error." + key, args);
	}

	/**
	 * Utility method to retrieve field value from record.
	 *
	 * @param inputMeta
	 * @param inputData
	 * @param field
	 * @return field value if defined, null otherwise
	 * @throws KettleValueException
	 */
	public static String getFieldString(RowMetaInterface inputMeta, Object[] inputData, String field) throws KettleValueException {

		if (!Const.isEmpty(field)) {
			return inputMeta.getString(inputData, field, "");
		}
		return null;
	}

	/**
	 * @param key
	 * @return
	 */
	public static String getLogString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDCheck.Log." + key, args);
	}

	/**
	 * Converts a string of result codes to a set
	 *
	 * @param value
	 * @return
	 */
	public static Set<String> getResultCodes(String value) {

		if ((value == null) || (value.trim().length() == 0)) {
			return new HashSet<String>();
		}
		String[]    values      = value.split(",");
		Set<String> resultCodes = new HashSet<String>(values.length);
		for (String v : values) {
			resultCodes.add(v);
		}
		return resultCodes;
	}

	public static void showSleepMsg(String message, int checkType) {

		String name = "";

		if ((checkType & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			name = "Match Up";
		} else if ((checkType & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			name = "Smart Mover";
		} else if ((checkType & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			name = "IP Locator";
		} else if ((checkType & MDCheckMeta.MDCHECK_FULL) != 0) {
			name = "Contact Verify";
		}

		if (MDCheckMeta.isSpoon()) {
			if (shMsg) {
				shMsg = false;
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "sleep", name, message);
				Display.getDefault().asyncExec(mbt);
			}
		}
	}

	private static       Class<?>                       PKG           = MDCheck.class;
	private static final String                         osProp        = System.getProperty("os.name");
	// Service handler
	private              MDCheckService                 service       = null;
	// Output filter evaluators
	private              Map<String, MDBinaryEvaluator> evaluators    = new HashMap<String, MDBinaryEvaluator>();
	private              int                            copy          = 0;
	public static        boolean                        shMsg         = true;
	// Reporting stuff
	private              int                            numLines      = 0;
	private              long                           startDate     = 0;
	private              List<String>                   rooflatlongs  = null;
	private              List<String>                   plus4latlongs = null;
	private              List<String>                   zip5latlongs  = null;
	private              Map<String, List<String>>      listInfo      = null;
	private              ReportRunner                   repRunner     = null;
	private              ReportDataEngine               repData       = null;
	boolean continueReporting = false;
	protected VariableSpace   variableSpace  = null;
	// Request Manager
	private   IRequestManager requestManager = null;

	public MDCheck(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {

		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		copy = copyNr;
		continueReporting = true;
		variableSpace = transMeta.getParentVariableSpace();
		startDate = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#dispose(org.pentaho.di.trans.step.StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		logDebug("MDCheck.dispose(smi=" + smi + ", sdi=" + sdi);
		//
		// Always dispose of the request task manager
		if (requestManager != null) {
			requestManager.dispose();
		}
		// Dispose the service
		if (service != null) {
			service.dispose();
		}
		// If not stopped then run reporting
		if (!isStopped()) {
			List<StepMetaDataCombi> stepcombi = getTrans().getSteps();
			String                  stepId    = getStepID();
			// check to make sure this thread is the last running
			int numRunning = 0;
			for (int i = 0; i < stepcombi.size(); i++) {
				if (stepcombi.get(i).step.getStepID().equals(stepId)) {
					if (stepcombi.get(i).step.isRunning()) {
						numRunning = numRunning + 1;
					}
				}
			}
			// if I'm the last then run reports
			if (numRunning == 1) {
				if ((repRunner != null) && continueReporting) {
					logBasic("Running Reports");
					try {
						repRunner.generateReports();
					} catch (KettleException e) {
						logError("Problem running reports", e);
					}
				}
			}
		}
		// Call parent handler last
		super.dispose(smi, sdi);
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#init(org.pentaho.di.trans.step.StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		logDebug("MDCheck.init(smi=" + smi + ", sdi=" + sdi);

		shMsg = true;
		// Call parent handler first
		if (!super.init(smi, sdi)) {
			return false;
		}
		// Dereference the control structures
		MDCheckMeta     meta     = (MDCheckMeta) smi;
		MDCheckData     data     = (MDCheckData) sdi;
		MDCheckStepData stepData = meta.getData();

		// Allocate the service handler
		if ((service = MDCheckService.create(stepData, data, this, getLogChannel())) == null) {
			return false;
		}
		// Initialize it
		try {
			service.init();
		} catch (KettleException e) {
			logError(getErrorString("NotAllServices"));
			logError(getErrorString("InitFailed") + e.getMessage());
			return false;
		}
		// Create the request manager
		requestManager = service.createRequestManager();
		if (!service.checkProxy()) {
			logError(MDCheckStepData.webInitMsg);
			return false;
		}
		logDebug("MDCheck.init = true");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#processRow(org.pentaho.di.trans.step.StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		MDCheckMeta meta = (MDCheckMeta) smi;
		MDCheckData data = (MDCheckData) sdi;
		// Get next row of data to process
		// FIXME This is where our threading issue happens
		Object[] inputData = getRow();
		// First time...
		if ((inputData != null) && first) {
//			if (!AdvancedConfigurationMeta.isEnterprise()) {
//				logError("You are not running the Enterprise Edition of PDI.");
//				setErrors(1);
//				stopAll();
//				return false;
//			}
			logDebug("MDCheck.processRow(smi=" + smi + ", sdi=" + sdi + ")");
			numLines = 0;
			logBasic("Beginning processing");
			// Do process initialization
			if (!processInit(meta, data)) {
				setOutputDone();  // signal end to receiver(s)
				return false;
			}
			first = false;
		}
		// Process the request
		if (!processRequest(inputData, meta, data, data.sourceIO)) {
			setOutputDone();  // signal end to receiver(s)
			return false;
		}
		// No more input to be expected...
		if (inputData == null) {
			// ...then we are done
			if (!processComplete(meta, data)) {
				setOutputDone();  // signal end to receiver(s)
				return false;
			}
			// Handle reports (if any and only if processing has not been halted and we are running in spoon)
			if (!isStopped() && !meta.getData().getAdvancedConfiguration().isCluster) {// && meta.isSpoon()
				try {
					processReports(meta, data);
				} catch (KettleException e) {
					logError(e.toString());
					setErrors(1);
					// Don't stop if reports fail
					// stopAll();
				}
			}
			// We are done
			setOutputDone();
			return false;
		}
		if (checkFeedback(getLinesRead())) {
			if (log.isDetailed()) {
				logDetailed(getLogString("LineNumber") + getLinesRead());
			}
		}
		return true;
	}

	/**
	 * Runs the reporting data related tasks so as to be able to run reporting later
	 *
	 * @param data
	 */
	public void runReporting(MDCheckData data) {

		try {
			String                  stepId    = getStepID();
			List<StepMetaDataCombi> stepcombi = getTrans().getSteps();
			List<StepMeta>          steps     = getTransMeta().getSteps();
			// check the transform for other copies or instances of this stepid and if found
			// then handle them in the proper order of operations.
			for (StepMeta stepMeta : steps) {
				if (stepMeta.getStepID().equals(stepId)) {
					// if we have multiple copies then create database if we're the first
					// otherwise wait for the copy before us to finish before updating
					if (stepMeta.getCopies() > 1) {
						if (copy == 0) {
							// create the initial database
							continueReporting = repData.clearPreviousDatabases();
							System.out.println(" -- Continue to do reporting = " + continueReporting);
							if (continueReporting) {
								continueReporting = repData.generateStatDatabase(data.resultStats, false);
							}
							break;
						}
						if (!continueReporting) {
							break;
						}
						for (int j = 0; j < stepcombi.size(); j++) {
							StepMetaDataCombi stepMetaData = stepcombi.get(j);
							if (stepMetaData.step.getStepID().equals(getStepID())) {
								if (stepMetaData.copy == (copy - 1)) {
									while (stepMetaData.step.isRunning()) {
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
						// update the database
						continueReporting = repData.generateStatDatabase(data.resultStats, true);
						break;
					}
					if (!continueReporting) {
						break;
					}
					// if we have multiple instances in the same transform and we're the first listed create the database
					// otherwise wait for any other running copies to finish and update the database
					if (getStepname().equals(stepMeta.getName())) {
						// create the initial database
						continueReporting = repData.clearPreviousDatabases();
						if (continueReporting) {
							continueReporting = repData.generateStatDatabase(data.resultStats, false);
						}
						break;
					}
					for (int j = 0; j < stepcombi.size(); j++) {
						StepMetaDataCombi stepMetaData = stepcombi.get(j);
						if (stepMetaData.step.getStepID().equals(getStepID())) {
							if (stepMetaData.stepname.equals(stepMeta.getName())) {
								while (stepMetaData.step.isRunning()) {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					// update the database
					continueReporting = repData.generateStatDatabase(data.resultStats, true);
					break;
				}
				if (!continueReporting) {
					break;
				}
			}
		} catch (KettleException e) {
			logError(e.toString(), e);
			setErrors(1);
			stopAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#stopRunning(org.pentaho.di.trans.step.StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public void stopRunning(StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface) throws KettleException {
		// Tell the request manager, if any, to stop
		if (requestManager != null) {
			requestManager.stop();
		}
		// Contue with base class
		super.stopRunning(stepMetaInterface, stepDataInterface);
	}

	/**
	 * @param filter
	 * @return An evaluator for the current output filter expression
	 * @throws KettleException
	 */
	private MDBinaryEvaluator getEvaluator(FilterTarget filter) throws KettleException {
		// Retiever already defiend evaluator
		MDBinaryEvaluator evaluator = evaluators.get(filter.getName());
		if (evaluator == null) {
			// Build one using the final output filter expression
			String expression = filter.getRule();
			logDebug("Creating evaluator for expression: " + expression);
			evaluator = new MDBinaryEvaluator(expression);
			evaluators.put(filter.getName(), evaluator);
		}
		return evaluator;
	}

	/**
	 * Retrieve and process the results of threaded requests. This method will repeatedly ask for completed requests
	 * from the request manager until there are no more.
	 *
	 * @param meta
	 * @param data
	 * @param done
	 * @return true if we should continue processing, false if there was a problem
	 * @throws KettleStepException
	 */
	private boolean handleCompletedRequests(MDCheckMeta meta, MDCheckData data, boolean done) throws KettleStepException {
		// Get completed requests (if any) from the request manager
		Request result;
		while ((result = requestManager.getCompletedRequest(done)) != null) {
			// Get the requests returned by this result
			List<MDCheckRequest> requests = result.getRequests();
			try {
				// If this request had a problem then throw its exception now
				if (result.getException() != null) {
					throw result.getException();
				}
				// Add the result codes to the output data
				meta.getData().getOutputFilter().addResultCodes(requests);
				// Output the completed rows
				for (MDCheckRequest request : requests) {
					RowMetaInterface outputMeta = request.outputMeta;
					Object[]         outputData = request.outputData;
					for (int i = 0; i < request.ioMeta.targetRowSets.size(); i++) {
						FilterTarget filter = request.ioMeta.filterTargets.get(i);
						boolean      valid  = isValidResult(filter, request.resultCodes); // Check the results against the filter
						if (valid) {
							RowSet rowSet = request.ioMeta.targetRowSets.get(i);
							if (rowSet != null) {
								if (log.isRowLevel()) {
									logRowlevel("Sending row to :" + filter.getTargetStep().getName() + " : " + outputMeta.getString(outputData));
								}
								// This is done this way things write correctly there have been issues seen
								// with the "'" when it tries to write it is no recognized by some encoding
								GeoCoderMeta geoCoder = meta.getData().getGeoCoder();
								if (geoCoder != null) {
									String        name      = "";
									String        address   = "";
									String        city      = "";
									String        state     = "";
									String        zip       = "";
									String        latitude  = MDCheck.getFieldString(outputMeta, outputData, geoCoder.getLatitude());
									String        longitude = MDCheck.getFieldString(outputMeta, outputData, geoCoder.getLongitude());
									NameParseMeta nameParse = meta.getData().getNameParse();
									if (nameParse != null) {
										name = MDCheck.getFieldString(outputMeta, outputData, nameParse.getFirstName(0));
										String lastName = MDCheck.getFieldString(outputMeta, outputData, nameParse.getLastName(0));
										if ((name != null) && (name.length() > 0)) {
											name += " " + lastName;
										} else {
											name = lastName;
										}
										if (name != null) {
											name = name.replaceAll("'", "");
										}
									}
									AddressVerifyMeta addressVerify = meta.getData().getAddressVerify();
									if (addressVerify != null) {
										address = MDCheck.getFieldString(outputMeta, outputData, addressVerify.getOutputAddressLine1());
										city = MDCheck.getFieldString(outputMeta, outputData, addressVerify.getOutputCity());
										state = MDCheck.getFieldString(outputMeta, outputData, addressVerify.getOutputState());
										zip = MDCheck.getFieldString(outputMeta, outputData, addressVerify.getOutputZip());
										if (address != null) {
											address = address.replaceAll("'", "");
										}
										if (city != null) {
											city = city.replaceAll("'", "");
										}
										if (state != null) {
											state = state.replaceAll("'", "");
										}
										if (zip != null) {
											zip = zip.replaceAll("'", "");
										}
									}
									if (rooflatlongs == null) {
										rooflatlongs = new ArrayList<String>();
										plus4latlongs = new ArrayList<String>();
										zip5latlongs = new ArrayList<String>();
										listInfo = new HashMap<String, List<String>>();
									}
									if (request.resultCodes.contains("GS01") || request.resultCodes.contains("GS02")) {
										plus4latlongs.add(latitude + "," + longitude);
										String       key      = latitude + "," + longitude + ",plus4";
										List<String> infoList = listInfo.get(key);
										if (infoList == null) {
											infoList = new ArrayList<String>();
											listInfo.put(key, infoList);
										}
										infoList.add("<div>" + name + "</div>" + "<div>" + address + "</div>" + "<div>" + city + " " + state + " " + zip + "</div>");
									} else if (request.resultCodes.contains("GS03")) {
										zip5latlongs.add(latitude + "," + longitude);
										String       key      = latitude + "," + longitude + ",zip5";
										List<String> infoList = listInfo.get(key);
										if (infoList == null) {
											infoList = new ArrayList<String>();
											listInfo.put(key, infoList);
										}
										infoList.add("<div>" + name + "</div>" + "<div>" + address + "</div>" + "<div>" + city + " " + state + " " + zip + "</div>");
									} else if (request.resultCodes.contains("GS05") || request.resultCodes.contains("GS06")) {
										rooflatlongs.add(latitude + "," + longitude);
										String       key      = latitude + "," + longitude + ",roof";
										List<String> infoList = listInfo.get(key);
										if (infoList == null) {
											infoList = new ArrayList<String>();
											listInfo.put(key, infoList);
										}
										infoList.add("<div>" + name + "</div>" + "<div>" + address + "</div>" + "<div>" + city + " " + state + " " + zip + "</div>");
									}
								}
								// Output the row of data
								putRowTo(outputMeta, outputData, rowSet);
							}
							break;
						}
					}
				}
			} catch (KettleException e) {
				// If errors are not configured for an error handling step OR
				// this is an abort exception...
				if (!getStepMeta().isDoingErrorHandling() || (e instanceof MDAbortException)) {
					// ... then send it to the log...
					logError(getErrorString("InStepRunning") + e.toString()); //$NON-NLS-1$
					setErrors(1);
					logError(Const.getStackTracker(e));
					// Log the abort condition
					if (e instanceof MDAbortException) {
						logError("Aborting for general error: " + e.getMessage());
					}
					// ... and halt
					stopAll();
					return false;
				}
				// Add queued input rows to error stream
				for (MDCheckRequest request : requests) {
					putError(request.inputMeta, request.inputData, 1, e.toString(), null, "MDCheck001");
				}
			}
		}
		return true;
	}

	/**
	 * Called to determine if the request result was valid. Uses the output filter validation rules to test.
	 *
	 * @param filter
	 * @param resultCodes
	 * @return
	 * @throws KettleException
	 */
	protected boolean isValidResult(FilterTarget filter, List<String> resultCodes) throws KettleException {
		// No filter? Always valid.
		if (filter == null) {
			return true;
		}
		// Get an evaluator to use to test the result codes
		MDBinaryEvaluator evaluator = getEvaluator(filter);
		boolean           valid     = evaluator.evaluate(resultCodes);
		return valid;
	}

	/**
	 * Called after last row is processed. Completes overall processing operation
	 *
	 * @param meta
	 * @param data
	 * @return
	 * @throws KettleException
	 */
	protected boolean processComplete(MDCheckMeta meta, MDCheckData data) throws KettleException {
		// Wait for all outstanding requests to complete
		if (!handleCompletedRequests(meta, data, true)) {
			return false;
		}
		return true;
	}

	/**
	 * Called once before the first source row is processed.
	 *
	 * @param meta
	 * @param data
	 * @return
	 * @throws KettleException
	 */
	protected boolean processInit(MDCheckMeta meta, MDCheckData data) throws KettleException {

		MDCheckStepData stepData = meta.getData();
		stepData.getAdvancedConfiguration().getPropVals();
		// Define the source I/O data
		IOMeta sourceIO = data.sourceIO = new IOMeta();
		// Get meta information for input row
		sourceIO.inputMeta = getInputRowMeta();
		// Get meta information for output row.
		// Start with a copy of the input meta.
		// The Pass Thru step will filter this down to the fields that should be passed thru.
		sourceIO.outputMeta = sourceIO.inputMeta.clone();
		// Get the fields added by this step
		meta.getFields(sourceIO.outputMeta, getStepname(), null, null, this);
		// Retain a reference to the source pass thru handler
		sourceIO.passThruMeta = stepData.getSourcePassThru();
		// Get the index of the pass thru fields
		if (!sourceIO.passThruMeta.selectIndexes(this, data, sourceIO)) {
			return false;
		}
		// Cache the position of the RowSets for the filtered output.
		List<StreamInterface> targetStreams = meta.getStepIOMeta().getTargetStreams();
		List<FilterTarget>    filterTargets = stepData.getOutputFilter().getFilterTargets();
		for (int i = 0; i < filterTargets.size(); i++) {
			FilterTarget filterTarget = filterTargets.get(i);
			for (StreamInterface targetStream : targetStreams) {
				if ((filterTarget.getTargetStepname() != null) && filterTarget.getTargetStepname().equals(targetStream.getStepname())) {
					RowSet targetRowSet = findOutputRowSet(getStepname(), getCopy(), targetStream.getStepname(), 0);
					if (targetRowSet == null) {
						throw new KettleException(getErrorString("TargetStepInvalid", targetStream.getStepname()));
					}
					sourceIO.addTargetRow(targetRowSet, filterTarget);
					break;
				}
			}
		}
		return true;
	}

	/**
	 * Handles the generation/saving of reports at the end of the job
	 *
	 * @param meta
	 * @param data
	 * @throws KettleException
	 */
	protected void processReports(MDCheckMeta meta, MDCheckData data) throws KettleException {
		// Gather reporting data
		MDCheckStepData stepData = meta.getData();
		if (stepData.isReportEnabled()) {
			// pass off all the data to the reporting code
			repRunner = new ReportRunner(meta, this);
			repData = new ReportDataEngine(meta, log);
			repData.addrChangeReportCStat = meta.addrChangeReportCStat;
			repData.addrOverviewReportCStat = meta.addrOverviewReportCStat;
			repData.addrValidationReportCStat = meta.addrValidationReportCStat;
			repData.emailFormulaFields = meta.emailFormulaFields;
			repData.emailOverviewReportCStat = meta.emailOverviewReportCStat;
			repData.errorReportCStat = meta.errorReportCStat;
			repData.geoOverviewReportCStat = meta.geoOverviewReportCStat;
			repData.nameFormulaFields = meta.nameFormulaFields;
			repData.nameOverviewFields = meta.nameOverviewFields;
			repData.phoneFormulaFields = meta.phoneFormulaFields;
			repData.phoneOverviewReportCStat = meta.phoneOverviewReportCStat;
			repData.setNumLines(numLines);
			repData.setNumAddrOverview(data.numAddrOverview);
			data.numAddrValidations.putAll(data.numAddrErrors);
			data.numAddrValidations.putAll(data.numAddrChanges);
			repData.setNumAddrFormula(data.numAddrValidations);
			repData.setNumPhoneOverview(data.numPhoneOverview);
			repData.setNumEmailOverview(data.numEmailOverview);
			repData.setNumGeoOverview(data.numGeoOverview);
			repData.setNumNameOverview(data.numNameOverview);
			repData.numEmailBlanks = data.numEmailBlanks;
			repData.numNameBlanks = data.numNameBlanks;
			repData.numCompanyNameBlanks = data.numCompanyNameBlanks;
			repData.numPhoneErrors = data.numPhoneErrors;
			repData.numValidDomain = data.numValidDomain;
			long timeDiff = (System.currentTimeMillis() - startDate);
			repData.runTime = timeDiff;
			// save data and do processing for reporting
			runReporting(data);
		}
		// If the service has any reports, do them now
		// this is NCOA & CASS it is separate from regular reporting
		service.saveReports();
	}

	/**
	 * This method isolates the processing of one record of request data from the initialization and teardown
	 * aspects of row processing
	 *
	 * @param inputData
	 * @param meta
	 * @param data
	 * @param ioMeta
	 * @return
	 * @throws KettleException
	 */
	protected boolean processRequest(Object[] inputData, MDCheckMeta meta, MDCheckData data, IOMeta ioMeta) throws KettleException {

		if (inputData != null) {
			numLines++;
			// Build a request for this data
			MDCheckRequest request = service.buildRequest(ioMeta, inputData);
			// Determine which request queue it should be routed to
			int queue = service.determineRequestRoute(request);
			// Add it to the request queue
			data.requests.get(queue).add(request);
		}
		// Check request queues
		for (int queue = 0; queue < data.requests.size(); queue++) {
			// If the queue is full or we have reached the end of the input and there are outstanding requests
			// then process them
			List<MDCheckRequest> requests = data.requests.get(queue);
			if (((inputData == null) && (requests.size() > 0)) || (requests.size() >= data.maxRequests)) {
				// Add pass thru fields
				ioMeta.passThruMeta.selectValues(this, meta, data, requests, ioMeta);
				// Add request to the manager
				requestManager.addRequest(service, requests, queue);
				// Done with these requests
				requests.clear();
				// Handle any completed requests
				if (!handleCompletedRequests(meta, data, false)) {
					return false;
				}
			}
		}
		return true;
	}
}
