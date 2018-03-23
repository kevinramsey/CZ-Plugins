package com.melissadata.kettle.personator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
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

// import com.melissadata.kettle.ReportDataEngine;
// import com.melissadata.kettle.ReportRunner;
import com.melissadata.kettle.personator.RequestManager.Request;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.error.MDAbortException;
import com.melissadata.kettle.personator.support.MDBinaryEvaluator;

import com.melissadata.cz.support.MessageBoxThread;

public class MDPersonator extends BaseStep implements StepInterface {
	/**
	 * @param key
	 * @return
	 */
	public static String getErrorString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDPersonator.Error." + key, args);
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
		if (!Const.isEmpty(field))
			return inputMeta.getString(inputData, field, "");
		return null;
	}

	/**
	 * @param key
	 * @return
	 */
	public static String getLogString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDPersonator.Log." + key, args);
	}

	/**
	 * Converts a string of result codes to a set
	 *
	 * @param value
	 * @return
	 */
	public static Set<String> getResultCodes(String value) {
		if ((value == null) || (value.trim().length() == 0))
			return new HashSet<String>();
		String[] values = value.split(",");
		Set<String> resultCodes = new HashSet<String>(values.length);
		for (String v : values) {
			resultCodes.add(v);
		}
		return resultCodes;
	}

	public static boolean isSpoon() {
		return isSpoon;
	}

	public static void setIsSpoon(boolean b) {
		// TODO Auto-generated method stub
		isSpoon = b;
	}

	public static void showSleepMsg(String message) {
		if (isSpoon()) {
			if (shMsg) {
				shMsg = false;
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "sleep", "Personator", message);
				Display.getDefault().asyncExec(mbt);
			}
		}
	}
	private static Class<?>					PKG				= MDPersonator.class;
	public static boolean					shMsg			= true;
// private ReportRunner repRunner;
// private ReportDataEngine repData;
	private MDPersonatorService				service;
	// Output filter evaluators
	private Map<String, MDBinaryEvaluator>	evaluators		= new HashMap<String, MDBinaryEvaluator>();
	private TransMeta						tMeta;
	private String							stepname;
	MDPersonatorMeta						pMeta;
	/*
	 * private void initReporting(MDPersonatorMeta meta, MDPersonatorData checkData) {
	 * //
	 * // init if needed
	 * //
	 * checkData.addrValidationReportCStat = meta.getReportArray("Addr","Validation");
	 * checkData.addrChangeReportCStat = meta.getReportArray("Addr","Changes");
	 * checkData.errorReportCStat = meta.getReportArray("Addr","Errors");
	 * checkData.addrOverviewReportCStat = meta.getReportArray("Addr","Overview");
	 * checkData.geoOverviewReportCStat = meta.getReportArray("Geo","Overview");
	 * if (checkData.numAddrValidations == null)
	 * checkData.numAddrValidations = ReportStats.create(checkData.addrValidationReportCStat);
	 * if (checkData.numAddrChanges == null)
	 * checkData.numAddrChanges = ReportStats.create(checkData.addrChangeReportCStat);
	 * if (checkData.numAddrErrors == null)
	 * checkData.numAddrErrors = ReportStats.create(checkData.errorReportCStat);
	 * if (checkData.numAddrOverview == null)
	 * checkData.numAddrOverview = ReportStats.create(checkData.addrOverviewReportCStat);
	 * if (checkData.numGeoOverview == null)
	 * checkData.numGeoOverview = ReportStats.create(checkData.geoOverviewReportCStat);
	 * if (checkData.resultStats == null)
	 * checkData.resultStats = new ReportStats();
	 * }
	 */
	@SuppressWarnings("unused")
	private long							startDate;
	/**
	 * Handles the generation/saving of reports at the end of the job
	 *
	 * @param meta
	 * @param data
	 * @throws KettleException
	 *
	 */
	/*
	 * protected void processReports(MDPersonatorMeta meta, MDPersonatorData data) throws KettleException {
	 * // Gather reporting data
	 * if (meta.reportFields.reportOptions.get(ReportingFields.TAG_REPORT_OPTION_REPORTS)) {
	 * // pass off all the data to the reporting code
	 * repRunner = new ReportRunner(meta, this);
	 * repData = new ReportDataEngine(meta);
	 * repData.addrChangeReportCStat = data.addrChangeReportCStat;
	 * repData.addrOverviewReportCStat = data.addrOverviewReportCStat;
	 * repData.addrValidationReportCStat = data.addrValidationReportCStat;
	 * repData.errorReportCStat = data.errorReportCStat;
	 * repData.geoOverviewReportCStat = data.geoOverviewReportCStat;
	 * repData.setNumLines(numLines);
	 * repData.setNumAddrOverview(data.numAddrOverview);
	 * repData.setNumGeoOverview(data.numGeoOverview);
	 * data.numAddrValidations.putAll(data.numAddrErrors);
	 * data.numAddrValidations.putAll(data.numAddrChanges);
	 * repData.setNumAddrFormula(data.numAddrValidations);
	 * long timeDiff = (System.currentTimeMillis() - startDate);
	 * repData.runTime = timeDiff;
	 * // save data and do processing for reporting
	 * runReporting(data);
	 * // If the service has any reports, do them now
	 * // Should this really be inside this conditional? -- Chris
	 * service.saveReports();
	 * }
	 * }
	 */
	/**
	 * Runs the reporting data related tasks so as to be able to run reporting later
	 *
	 * @param data
	 *
	 */
	/*
	 * public void runReporting(MDPersonatorData data) {
	 * try {
	 * String stepId = this.getStepID();
	 * List<StepMetaDataCombi> stepcombi = getTrans().getSteps();
	 * List<StepMeta> steps = this.getTransMeta().getSteps();
	 * // check the transform for other copies or instances of this stepid and if found
	 * // then handle them in the proper order of operations.
	 * for (StepMeta stepMeta : steps) {
	 * if (stepMeta.getStepID().equals(stepId)) {
	 * // if we have multiple copies then create database if we're the first
	 * // otherwise wait for the copy before us to finish before updating
	 * if (stepMeta.getCopies() > 1) {
	 * if (copy == 0) {
	 * // create the initial database
	 * repData.clearPreviousDatabases();
	 * repData.generateStatDatabase(data.resultStats, false);
	 * break;
	 * }
	 * for (int j = 0; j < stepcombi.size(); j++) {
	 * StepMetaDataCombi stepMetaData = stepcombi.get(j);
	 * if (stepMetaData.step.getStepID().equals(getStepID())) {
	 * if (stepMetaData.copy == (copy - 1)) {
	 * while (stepMetaData.step.isRunning()) {
	 * try {
	 * Thread.sleep(1000);
	 * } catch (InterruptedException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 * }
	 * }
	 * }
	 * // update the database
	 * repData.generateStatDatabase(data.resultStats, true);
	 * break;
	 * }
	 * // if we have multiple instances in the same transform and we're the first listed create the database
	 * // otherwise wait for any other running copies to finish and update the database
	 * if (this.getStepname().equals(stepMeta.getName())) {
	 * // create the initial database
	 * repData.clearPreviousDatabases();
	 * repData.generateStatDatabase(data.resultStats, false);
	 * break;
	 * }
	 * for (int j = 0; j < stepcombi.size(); j++) {
	 * StepMetaDataCombi stepMetaData = stepcombi.get(j);
	 * if (stepMetaData.step.getStepID().equals(getStepID())) {
	 * if (stepMetaData.stepname.equals(stepMeta.getName())) {
	 * while (stepMetaData.step.isRunning()) {
	 * try {
	 * Thread.sleep(1000);
	 * } catch (InterruptedException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 * }
	 * }
	 * }
	 * // update the database
	 * repData.generateStatDatabase(data.resultStats, true);
	 * break;
	 * }
	 * }
	 * } catch (KettleException e) {
	 * logError(e.toString(), e);
	 * setErrors(1);
	 * stopAll();
	 * }
	 * }
	 */
	private int								rowsProcessed	= 0;
	private static boolean					isSpoon			= false;
	// Request Manager
	private IRequestManager					requestManager;

	public MDPersonator(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		tMeta = transMeta;
		stepname = stepMeta.getName();
		startDate = System.currentTimeMillis();
		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				;
			}
			isSpoon = true;
		}
	}

	/**
	 * Called to add the result codes to the output data (if so requested)
	 *
	 * @param requests
	 */
	public void addResultCodes(List<MDPersonatorRequest> requests) {
		// Add result codes for each request
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDPersonatorRequest request = requests.get(recordID);
			if (!Const.isEmpty(pMeta.personatorFields.outputFields.get(PersonatorFields.TAG_OUTPUT_RESULTS).metaValue)) {
				Set<String> sortedResultCodes = new TreeSet<String>(request.resultCodes);
				StringBuffer s = new StringBuffer();
				String sep = "";
				for (String result : sortedResultCodes) {
					s.append(sep).append(result);
					sep = ",";
				}
				request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, s.toString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#dispose(org.pentaho.di.trans.step.StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		logDebug("MDPersonator.dispose(smi=" + smi + ", sdi=" + sdi);
		// Always dispose of the request task manager
		if (requestManager != null) {
			requestManager.dispose();
		}
		// Dispose the service
		service.dispose();
		// If not stopped then run reporting
		if (!isStopped()) {
			List<StepMetaDataCombi> stepcombi = getTrans().getSteps();
			String stepId = getStepID();
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
			/*
			 * if (numRunning == 1) {
			 * if (repRunner != null) {
			 * logBasic("Running Reports");
			 * try {
			 * repRunner.generateReports();
			 * } catch (KettleException e) {
			 * logError("Problem running reports", e);
			 * }
			 * }
			 * }
			 */
		}
		// Call parent handler last
		super.dispose(smi, sdi);
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
	private boolean handleCompletedRequests(MDPersonatorMeta meta, MDPersonatorData data, boolean done) throws KettleStepException {
		// Get completed requests (if any) from the request manager
		Request result;
		while ((result = requestManager.getCompletedRequest(done)) != null) {
			// Get the requests returned by this result
			List<MDPersonatorRequest> requests = result.getRequests();
			try {
				// If this request had a problem then throw its exception now
				if (result.getException() != null)
					throw result.getException();
				// Add the result codes to the output data
				addResultCodes(requests);
				// Output the completed rows
				for (MDPersonatorRequest request : requests) {
					for (int i = 0; i < request.ioMeta.targetRowSets.size(); i++) {
						FilterTarget filter = request.ioMeta.filterTargets.get(i);
						boolean valid = isValidResult(filter, request.resultCodes); // Check the results against the filter
						if (valid) {
							RowSet rowSet = request.ioMeta.targetRowSets.get(i);
							if (rowSet != null) {
								if (log.isRowLevel()) {
									logRowlevel("Sending row to :" + filter.getTargetStep().getName() + " : " + request.outputMeta.getString(request.outputData));
								}
								putRowTo(request.outputMeta, request.outputData, rowSet);
// numLines++;
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
				for (MDPersonatorRequest request : requests) {
					putError(request.inputMeta, request.inputData, 1, e.toString(), null, "MDPersonator001");
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#init(org.pentaho.di.trans.step.StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		logDebug("MDPersonator.init(smi=" + smi + ", sdi=" + sdi);
		// Call parent handler first
		if (!super.init(smi, sdi))
			return false;
		// Dereference the control structures
		MDPersonatorMeta meta = (MDPersonatorMeta) smi;
		MDPersonatorData data = (MDPersonatorData) sdi;
		if(!MDPersonatorMeta.isEnterprise()){
			String message = BaseMessages.getString(PKG,"MDPersonatorDialog.Community.PopupMessage");
			if(isSpoon()) {
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "community", "Personator", message);
				Display.getDefault().asyncExec(mbt);
			}
			logError(message);
			stopAll();
			return false;
		}

// initReporting(meta,data);
		// Allocate the service handler
		if (meta.personatorFields.hasMinRequirements()) {
			service = MDPersonatorService.create(data, meta, this, getLogChannel());
		} else {
			String fieldsNeeded = "Minimum Requirements not met of: ";
			if (Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue)) {
				fieldsNeeded = fieldsNeeded.concat(" Input Address; ");
			}
			logError(fieldsNeeded);
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
		try {
			requestManager = service.createRequestManager();
		} catch (NumberFormatException nfe) {
			logError("Problem gaining max threads property: " + nfe.toString());
		}
		if (!service.checkProxy()) {
			logError(MDPersonatorData.webInitMsg);
			return false;
		}

		if(!MDPersonatorMeta.isEnterprise()){
			String message = "Personator processing is not available in Community Edition";
			if(isSpoon()){
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "community", "Personator", message);
				Display.getDefault().asyncExec(mbt);
			}
			logError(message);
			stopAll();
			return false;
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
		if (filter == null)
			return true;
		// Get an evaluator to use to test the result codes
		MDBinaryEvaluator evaluator = getEvaluator(filter);
		boolean valid = evaluator.evaluate(resultCodes);
		return valid;
	}

	/**
	 * This logs the statistics needed to provide for the profiler in the output tabs.
	 *
	 * @param data
	 * @param meta
	 */
	public void logStats(MDPersonatorData data, MDPersonatorMeta meta) {
		meta.setData(data);
	}

	/**
	 * This opens the output options dialog during processing in Spoon.
	 */
	public void openOutputDialog() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					MDPersonatorDialog dia = new MDPersonatorDialog(Spoon.getInstance().getShell(), pMeta, tMeta, stepname);
					dia.setOutputMode(true);
					dia.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * This processes the statistics and stores the lines in a cache for actions in the future.
	 *
	 * @param inputData
	 * @param data
	 * @param meta
	 * @throws KettleValueException
	 */
	public boolean process(Object[] inputData, MDPersonatorData data, MDPersonatorMeta meta, IOMetaHandler ioMeta) throws KettleException {
		if (inputData != null) {
			// Build a request for this data
			MDPersonatorRequest request = service.buildRequest(ioMeta, inputData);
			// Determine which request queue it should be routed to
			int queue = service.determineRequestRoute(request);
			// Add it to the request queue
			data.requests.get(queue).add(request);
		}
		// Check request queues
		for (int queue = 0; queue < data.requests.size(); queue++) {
			// If the queue is full or we have reached the end of the input and there are outstanding requests
			// then process them
			List<MDPersonatorRequest> requests = data.requests.get(queue);
			if (((inputData == null) && (requests.size() > 0)) || (requests.size() >= data.maxRequests)) {
				// Add pass thru fields
				meta.selectValues(this, meta, data, requests, ioMeta);
				// Add request to the manager
				requestManager.addRequest(service, requests, queue);
				// Done with these requests
				requests.clear();
				// Handle any completed requests
				if (!handleCompletedRequests(meta, data, false))
					return false;
			}
		}
		return true;
	}

	/**
	 * Called after last row is processed. Completes overall processing operation
	 *
	 * @param meta
	 * @param data
	 *
	 * @return
	 * @throws KettleException
	 */
	protected boolean processComplete(MDPersonatorMeta meta, MDPersonatorData data) throws KettleException {
		// Wait for all outstanding requests to complete
		if (!handleCompletedRequests(meta, data, true))
			return false;
		return true;
	}

	/**
	 * Called once before the first source row is processed.
	 *
	 * @param meta
	 * @param data
	 *
	 * @return
	 * @throws KettleException
	 */
	protected boolean processInit(MDPersonatorMeta meta, MDPersonatorData data) throws KettleException {
		// Define the source I/O data
		IOMetaHandler sourceIO = data.sourceIO = new IOMetaHandler();
		// Get meta information for input row
		sourceIO.inputMeta = getInputRowMeta();
		// Get meta information for output row.
		// Start with a copy of the input meta.
		// The Pass Thru step will filter this down to the fields that should be passed thru.
		sourceIO.outputMeta = sourceIO.inputMeta.clone();
		// Get the fields added by this step
		meta.getFields(sourceIO.outputMeta, getStepname(), null, null, this);
		// Get the index of the pass thru fields
		if (!meta.passThruFields.selectIndexes(this, data, sourceIO))
			return false;
		// Cache the position of the RowSets for the filtered output.
		List<StreamInterface> targetStreams = meta.getStepIOMeta().getTargetStreams();
		List<FilterTarget> filterTargets = meta.oFilterFields.filterTargets;
		for (int i = 0; i < filterTargets.size(); i++) {
			FilterTarget filterTarget = filterTargets.get(i);
			for (StreamInterface targetStream : targetStreams) {
				if ((filterTarget.getTargetStepname() != null) && filterTarget.getTargetStepname().equals(targetStream.getStepname())) {
					RowSet targetRowSet = findOutputRowSet(getStepname(), getCopy(), targetStream.getStepname(), 0);
					if (targetRowSet == null)
						throw new KettleException(getErrorString("TargetStepInvalid", targetStream.getStepname()));
					sourceIO.addTargetRow(targetRowSet, filterTarget);
					break;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#processRow(org.pentaho.di.trans.step.StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		pMeta = (MDPersonatorMeta) smi;
		MDPersonatorData data = (MDPersonatorData) sdi;
// if (first) {
// first = false;
// try {
// logDebug("Checking for PDI Enterprise Edition...");
// Class.forName("com.pentaho.util.SubscriptionVersionHelper");
// logDebug("You are running the Enterprise Edition of PDI.");
// } catch (Throwable ex) {
// logError("You are not running the Enterprise Edition of PDI.");
// setErrors(1);
// stopAll();
// return false;
// }
// }
		// Get next row of data to process
		Object[] inputData = getRow();
		// First time...
		if ((inputData != null) && first) {
			logDebug("MDPersonator.processRow(smi=" + smi + ", sdi=" + sdi + ")");
			first = false;
			rowsProcessed = 0;
			if (!MDPersonatorMeta.isEnterprise()) {
				logBasic("You are not running the Enterprise Edition of PDI. Max rows 250,000");
			}
			// Get meta information for input row
			data.inputMeta = getInputRowMeta();
			// Get meta information for output row.
			// Start with a copy of the input meta.
			// The Pass Thru step will filter this down to the fields that should be passed thru.
			data.outputMeta = data.inputMeta.clone();
			for (int i = 0; i < data.outputMeta.size(); i++) {
				data.outputMeta.getValueMeta(i).setType(ValueMetaInterface.TYPE_STRING);
			}
			// Get the fields added by this step
			pMeta.getFields(data.outputMeta, getStepname(), null, null, this);
			if (!processInit(pMeta, data)) {
				setOutputDone();
				return false;
			}
		}
		if (!MDPersonatorMeta.isEnterprise()) {
			rowsProcessed++;
			if (rowsProcessed > 250000) {
				logBasic("Max limit reached: " + (rowsProcessed - 1));
				inputData = null;
			}
		}
		if (!process(inputData, data, pMeta, data.sourceIO)) {
			setOutputDone();
			return false;
		}
		if (inputData == null) {
			logStats(data, pMeta);
			// ...then we are done
			if (!processComplete(pMeta, data)) {
				setOutputDone();
				return false;
			}
			// Handle reports (if any and only if processing has not been halted and we are running in spoon)
			if (!isStopped() && isSpoon) {
				/*
				 * try {
				 * processReports(pMeta, data);
				 * } catch (KettleException e) {
				 * logError(e.toString());
				 * setErrors(1);
				 * stopAll();
				 * }
				 */
			}
			// We are done
			setOutputDone();
			return false;
		}
		return true;
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
}
