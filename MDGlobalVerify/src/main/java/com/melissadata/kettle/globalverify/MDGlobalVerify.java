package com.melissadata.kettle.globalverify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.melissadata.kettle.globalverify.web.MDGlobalWebService;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
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
import com.melissadata.cz.support.FilterTarget;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MessageBoxThread;
import com.melissadata.kettle.globalverify.RequestManager.Request;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.error.MDAbortException;
import com.melissadata.kettle.globalverify.support.MDBinaryEvaluator;

public class MDGlobalVerify extends BaseStep implements StepInterface {

	/**
	 * @param key
	 * @return
	 */
	public static String getErrorString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDGlobalAddress.Error." + key, args);
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
			String val = inputMeta.getString(inputData, field, "");
			return val;
		}
		return null;
	}

	/**
	 * @param key
	 * @return
	 */
	public static String getLogString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDGlobalAddress.Log." + key, args);
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

	public static boolean isSpoon() {

		return isSpoon;
	}

	public static void setIsSpoon(boolean b) {

		isSpoon = b;
	}

	public static void showSleepMsg(String message) {

		if (isSpoon()) {
			if (shMsg) {
				shMsg = false;
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "sleep", "Global Verify", message);
				Display.getDefault().asyncExec(mbt);
			}
		}
	}

	private static Class<?>                       PKG        = MDGlobalVerify.class;
	public static  boolean                        shMsg      = true;
	private        MDGlobalService                webService = null;
	//private        MDGlobalService                localService = null;
	// Output filter evaluators
	private        Map<String, MDBinaryEvaluator> evaluators = new HashMap<String, MDBinaryEvaluator>();
	private        TransMeta                      tMeta      = null;
	private        String                         stepname   = null;
	private        MDGlobalMeta                   globalMeta = null;
	public         int                            numLines   = 0;
	private        long                           startDate  = 0;
	int copy = 0;
	private static boolean         isSpoon             = false;
	// Request Manager
	private        IRequestManager webRequestManager   = null;
	private        IRequestManager localRequestManager = null;

	public MDGlobalVerify(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {

		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		tMeta = transMeta;
		stepname = stepMeta.getName();
		copy = copyNr;
		startDate = System.currentTimeMillis();
		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				isSpoon = true;
			} else {
				isSpoon = false;
			}
		}
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		logDebug("MDGlobalAddress.dispose(smi=" + smi + ", sdi=" + sdi);
		//
		// Always dispose of the request task manager
		if (webRequestManager != null) {
			webRequestManager.dispose();
		}
		if (localRequestManager != null) {
			localRequestManager.dispose();
		}
		// Dispose the service
		webService.dispose();
//		if(localService != null){
//			localService.dispose();
//		}
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
//				if (repRunner != null) {
//					logBasic("Running Reports");
//					try {
//						repRunner.generateReports();
//					} catch (KettleException e) {
//						logError("Problem running reports", e);
//					}
//				}
			}
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
	 * Retrieve and process the results of threaded requests. This method will repeatedly ask for completed requests from the
	 * request manager until there are no more.
	 *
	 * @param meta
	 * @param data
	 * @param done
	 * @return true if we should continue processing, false if there was a problem
	 * @throws KettleStepException
	 */
	private boolean handleCompletedRequests(MDGlobalMeta meta, MDGlobalData data, boolean done) throws KettleStepException {
		// Get completed requests (if any) from the request manager
		Request result = null;

		while ((result = webRequestManager.getCompletedRequest(done)) != null) {
			logDebug("Global Verify -  Handle Completed Web Requests results size = " + result.getRequests().size());
			// Get the requests returned by this result
			List<MDGlobalRequest> requests = result.getRequests();
			try {
				// If this request had a problem then throw its exception now
				if (result.getException() != null) {
					throw result.getException();
				}
				// Output the completed rows
				for (MDGlobalRequest request : requests) {
					for (int i = 0; i < request.ioMeta.targetRowSets.size(); i++) {
						FilterTarget filter = request.ioMeta.filterTargets.get(i);
						boolean      valid  = isValidResult(filter, request.resultCodes); // Check the results against the filter
						if (valid) {
							RowSet rowSet = request.ioMeta.targetRowSets.get(i);
							if (rowSet != null) {
								if (log.isRowLevel()) {
									logRowlevel(" - Global Verify - Sending row to: " + filter.getTargetStep().getName() + "  record: " + request.outputData.toString());
								}
								putRowTo(request.outputMeta, request.outputData, rowSet);
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
				for (MDGlobalRequest request : requests) {
					putError(request.inputMeta, request.inputData, 1, e.toString(), null, "MDGlobalAddress001");
				}
			}
		}

		//
		if (localRequestManager != null) {
			while ((result = localRequestManager.getCompletedRequest(done)) != null) {
				logDebug("Global Verify -  Handle Completed Local Requests results size = " + result.getRequests().size());
				// Get the requests returned by this result
				List<MDGlobalRequest> requests = result.getRequests();
				try {
					// If this request had a problem then throw its exception now
					if (result.getException() != null) {
						throw result.getException();
					}
					// Output the completed rows
					for (MDGlobalRequest request : requests) {
						for (int i = 0; i < request.ioMeta.targetRowSets.size(); i++) {
							FilterTarget filter = request.ioMeta.filterTargets.get(i);
							boolean      valid  = isValidResult(filter, request.resultCodes); // Check the results against the filter
							if (valid) {
								RowSet rowSet = request.ioMeta.targetRowSets.get(i);
								if (rowSet != null) {
									if (log.isRowLevel()) {
										logRowlevel(" - Global Verify - Sending row to: " + filter.getTargetStep().getName() + "  record: " + request.outputData.toString());
									}
									putRowTo(request.outputMeta, request.outputData, rowSet);
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
					for (MDGlobalRequest request : requests) {
						putError(request.inputMeta, request.inputData, 1, e.toString(), null, "MDGlobalAddress001");
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		logDebug("MDGlobalVerify.init(smi=" + smi + ", sdi=" + sdi);
		// Call parent handler first
		if (!super.init(smi, sdi)) {
			return false;
		}
		// Dereference the control structures
		MDGlobalMeta meta = (MDGlobalMeta) smi;
		MDGlobalData data = (MDGlobalData) sdi;

		if (!MDGlobalMeta.isEnterprise()) {
			String message = BaseMessages.getString(PKG, "MDGlobalDialog.Community.PopupMessage");
			if (isSpoon()) {
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "community", "Global Verify", message);
				Display.getDefault().asyncExec(mbt);
			}
			logError(message);
			stopAll();
			return false;
		}

		//initReporting(meta, data);
		// Allocate the service handler
		if (meta.getAddrMeta().getAddrFields().hasMinRequirements() || meta.getNameMeta().getNameFields().hasMinRequirements() || meta.getPhoneMeta().getPhoneFields().hasMinRequirements() || meta.getEmailMeta().getEmailFields()
				.hasMinRequirements()) {
			webService = MDGlobalService.create(data, meta, this, getLogChannel(), false);
			if (/*meta.getAddrMeta().getAddrFields().hasSelectedCountries()*/
					(meta.getAddrMeta().getAddrFields().getProcessType().equals(AddressFields.TAG_PROCESS_LOCAL) || meta.getAddrMeta().getAddrFields().getProcessType().equals(AddressFields.TAG_PROCESS_VARIED)) && meta.getAddrMeta().getAddrFields()
							.hasMinRequirements()) {
				((MDGlobalWebService) webService).createLocalAddressService();
				//	localService = MDGlobalService.create(data, meta, this, getLogChannel(), true);
			}
		} else {
			String fieldsNeeded = "Minimum Requirements not met of: ";
			if (Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE1).metaValue)) {
				fieldsNeeded = fieldsNeeded.concat(" Input Address; ");
			}
			if (Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_COUNTRY).metaValue) && Const.isEmpty(meta.getAddrMeta().getAddrFields().defaultCountry)) {
				fieldsNeeded = fieldsNeeded.concat(" CountryName or Default CountryName; ");
			}
			logError(fieldsNeeded);
			return false;
		}
		// Initialize it
		try {
			webService.init();
//			if (localService != null) {
//				localService.init();
//			}
		} catch (KettleException e) {
			logError(getErrorString("NotAllServices"));
			logError(getErrorString("InitFailed") + e.getMessage());
			return false;
		}
		// Create the request manager
		try {
			webRequestManager = webService.createRequestManager();
//			if (localService != null) {
//				localRequestManager = localService.createRequestManager();
//			}
		} catch (NumberFormatException nfe) {
			logError("Problem gaining max threads property: " + nfe.toString());
		}
		if (!webService.checkProxy()) {
			logError(MDGlobalData.webInitMsg);
			return false;
		}
		if (!MDGlobalMeta.isEnterprise()) {
			String message = "Global Verify processing is not available in Community Edition";
			if (isSpoon()) {
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "community", "Global Verify", message);
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
		if (filter == null) {
			return true;
		}
		// Get an evaluator to use to test the result codes
		MDBinaryEvaluator evaluator = getEvaluator(filter);
		boolean           valid     = evaluator.evaluate(resultCodes);
		return valid;
	}

	/**
	 * This logs the statistics needed to provide for the profiler in the output tabs.
	 *
	 * @param data
	 * @param meta
	 */
	public void logStats(MDGlobalData data, MDGlobalMeta meta) {

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
					MDGlobalDialog dia = new MDGlobalDialog(Spoon.getInstance().getShell(), globalMeta, tMeta, stepname);
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
	public boolean process(Object[] inputData, MDGlobalData data, MDGlobalMeta meta, IOMetaHandler ioMeta) throws KettleException {

		if (inputData != null) {
			numLines++;

			// Build a request for this data
			MDGlobalRequest request = (MDGlobalRequest) webService.buildRequest(ioMeta, inputData);
			// Determine which request queue it should be routed to
			int queue = webService.determineRequestRoute(request);

			if (log.isDebug()) {
				int idx = meta.getAddrMeta().getAddrFields().getCountryFieldIndex();
				if (idx > -1) {
					if(log.isRowLevel()) {
						logDetailed("Global Verify - The CountryName is : " + request.inputMeta.getString(inputData, idx)+ "  Send to queue : " + queue);
					}
				}
			}

			// Add it to the request queue
			data.requests.get(queue).add(request);
		}
		// Check request queues
		for (int queue = 0; queue < data.requests.size(); queue++) {
			// If the queue is full or we have reached the end of the input and there are outstanding requests
			// then process them
			List<MDGlobalRequest> requests = data.requests.get(queue);
			if (((inputData == null) && (requests.size() > 0)) || (requests.size() >= data.maxRequests)) {
				// Add pass thru fields
				meta.selectValues(this, meta, data, requests, ioMeta);
				// Add request to the manager

				webRequestManager.addRequest(webService, requests, queue);
				logRowlevel("Global Verify - adding requests. size: " + requests.size() + "  queue: " + queue);
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

	/**
	 * Called after last row is processed. Completes overall processing operation
	 *
	 * @param meta
	 * @param data
	 * @return
	 * @throws KettleException
	 */
	protected boolean processComplete(MDGlobalMeta meta, MDGlobalData data) throws KettleException {
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
	protected boolean processInit(MDGlobalMeta meta, MDGlobalData data) throws KettleException {
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
		if (!meta.passThruFields.selectIndexes(this, data, sourceIO)) {
			return false;
		}
		// Cache the position of the RowSets for the filtered output.
		List<StreamInterface> targetStreams = meta.getStepIOMeta().getTargetStreams();
		List<FilterTarget>    filterTargets = meta.oFilterFields.filterTargets;
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

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#processRow(org.pentaho.di.trans.step .StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		globalMeta = (MDGlobalMeta) smi;
		MDGlobalData data = (MDGlobalData) sdi;
		// Get next row of data to process
		Object[] inputData = getRow();
		// First time...
		if ((inputData != null) && first) {
			logDebug("MDGlobalVerify.processRow(smi=" + smi + ", sdi=" + sdi + ")");
			first = false;
			if (!MDGlobalMeta.isEnterprise()) {
				logError("You are not running the Enterprise Edition of PDI.");
				setErrors(1);
				stopAll();
				return false;
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
			globalMeta.getFields(data.outputMeta, getStepname(), null, null, this);
			if (!processInit(globalMeta, data)) {
				setOutputDone();
				return false;
			}
		}
		if (!process(inputData, data, globalMeta, data.sourceIO)) {
			setOutputDone();
			return false;
		}
		if (inputData == null) {
			logStats(data, globalMeta);
			// ...then we are done
			if (!processComplete(globalMeta, data)) {
				setOutputDone();
				return false;
			}
			// Handle reports (if any and only if processing has not been halted and we are running in spoon)
//			if (!isStopped()) {
//				try {
//					processReports(globalMeta, data);
//				} catch (KettleException e) {
//					logError(e.toString());
//					setErrors(1);
//					stopAll();
//				}
//			}
			// We are done
			setOutputDone();
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#stopRunning(org.pentaho.di.trans.step .StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public void stopRunning(StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface) throws KettleException {
		// Tell the request manager, if any, to stop
		if (webRequestManager != null) {
			webRequestManager.stop();
		}
		if (localRequestManager != null) {
			localRequestManager.stop();
		}
		// Contue with base class
		super.stopRunning(stepMetaInterface, stepDataInterface);
	}
}
