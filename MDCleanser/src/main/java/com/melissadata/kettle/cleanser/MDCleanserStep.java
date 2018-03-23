package com.melissadata.kettle.cleanser;

import com.melissadata.cz.support.*;
import com.melissadata.kettle.cleanser.request.MDCleanserRequest;
import com.melissadata.kettle.cleanser.request.RequestManager;
import com.melissadata.kettle.cleanser.request.RequestManager.Request;
import com.melissadata.kettle.cleanser.service.MDCleanserService;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.Const;
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
import org.pentaho.di.trans.step.*;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.*;

public class MDCleanserStep extends BaseStep implements StepInterface {

	public static String getErrorString(String string) {
		return BaseMessages.getString(PKG, "MDCleanserMeta.Error." + string, "");

	}

	public static String getErrorString(String string, String string2) {
		return BaseMessages.getString(PKG, "MDCleanserMeta.Error." + string, string2);
	}

	public static String getErrorString(String string, String timeoutMessage, String string2) {
		return BaseMessages.getString(PKG, "MDCleanserMeta.Error." + string, timeoutMessage, string2);
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

	public static String getLogString(String string, String valueOf) {
		return BaseMessages.getString(PKG, "MDCleanserMeta.Log." + string, valueOf);
	}

	public static String getLogString(String string, String string2, String string3) {
		return BaseMessages.getString(PKG, "MDCleanserMeta.Log." + string, string2, string3);
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

	public static void setIsSpoon(boolean spn) {
		isSpoon = spn;
	}

	public static void showSleepMsg(String message) {
		if (isSpoon()) {
			if (shMsg) {
				shMsg = false;
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "sleep", "MDCleanser", message);
				Display.getDefault().asyncExec(mbt);
			}
		}
	}

	public static  boolean                        shMsg                = false;
	private static Class<?>                       PKG                  = MDCleanserMeta.class;
	private static boolean                        isSpoon              = false;
	private final  int                            communityRecordLimit = 100000;
	public         int                            numLines             = 0;
	private        MDCleanserService              service              = null;
	// Output filter evaluators
	private        Map<String, MDBinaryEvaluator> evaluators           = new HashMap<String, MDBinaryEvaluator>();
	private        MDCleanserMeta                 cleanserMeta         = null;
	private        RequestManager                 requestManager       = null;

	public MDCleanserStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/**
	 * Called to add the result codes to the output data (if so requested)
	 *
	 * @param requests
	 */
	public synchronized void addResultCodes(List<MDCleanserRequest> requests) {
		// Add result codes for each request
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDCleanserRequest request = requests.get(recordID);

			if (!Const.isEmpty(cleanserMeta.oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue)) {
				if (request.resultCodes != null) {

					StringBuffer s = new StringBuffer();
					String sep = "";

					for (String result : request.resultCodes) {
						s.append(sep).append(result);
						sep = ",";
					}
					request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, s.toString());
				}
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
		logDebug("MDCleanser.dispose(smi=" + smi + ", sdi=" + sdi);
		//
		// Always dispose of the request task manager
		if (requestManager != null) {
			requestManager.dispose();
		}
		// Dispose the service
		if(service != null) {
			service.dispose();
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

	private synchronized IOMetaHandler getIOHandler() {
		return new IOMetaHandler();
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
	private boolean handleCompletedRequests(MDCleanserMeta meta, MDCleanserData data, boolean done) throws KettleStepException {
		// Get completed requests (if any) from the request manager
		Request result;
		while ((result = requestManager.getCompletedRequest(done)) != null) {
			// Get the requests returned by this result
			List<MDCleanserRequest> requests = result.getRequests();
			try {
				// If this request had a problem then throw its exception now
				if (result.getException() != null)
					throw result.getException();
				// Add the result codes to the output data
				addResultCodes(requests);
				// Output the completed rows
				for (MDCleanserRequest request : requests) {
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
							}
							break;
						}
					}
				}
			} catch (KettleException e) {
				// If errors are not configured for an error handling step OR Addr Requ
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
				for (MDCleanserRequest request : requests) {
					putError(request.inputMeta, request.inputData, 1, e.toString(), null, "MDCleanser001");
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
	public synchronized boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		logDebug("MDCleanser.init(smi=" + smi + ", sdi=" + sdi);
		shMsg = true;

		if(!Const.isWindows()){
			String message = "Melissa Generalized Cleanser component is not currently available for " + Const.getOS();
			MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(),"community", "MD Generalized Cleanser", message);
			Display.getDefault().asyncExec(mbt);
			logError(message);
			return false;
		}

		// Call parent handler first
		if (!super.init(smi, sdi))
			return false;
		// Dereference the control structures
		MDCleanserMeta meta = (MDCleanserMeta) smi.clone();
		MDCleanserData data = (MDCleanserData) sdi;
		// Allocate the service handler
		if ((service = MDCleanserService.create(data, meta, this, getLogChannel())) == null)
			return false;
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
		} catch (KettleException e) {
			logError(getErrorString("NotAllServices"));
			logError(getErrorString("InitFailed") + e.getMessage());
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
		
		List<String> cleanCodes = new ArrayList<String>(resultCodes.size());
		String holder = "";
		for(String code : resultCodes){
			
			holder = code.substring(code.length() - 4);
			cleanCodes.add(holder);

		}
		
		
		// Get an evaluator to use to test the result codes
		MDBinaryEvaluator evaluator = getEvaluator(filter);
		boolean valid = evaluator.evaluate(cleanCodes);
	
		return valid;
	}

	/**
	 * This processes the statistics and stores the lines in a cache for actions in the future.
	 *
	 * @param inputData
	 * @param data
	 * @param meta
	 * @throws KettleValueException
	 */
	public boolean process(Object[] inputData, MDCleanserData data, MDCleanserMeta meta, IOMetaHandler ioMeta) throws KettleException {

		if (inputData != null) {
			numLines++;
			// Build a request for this data
			MDCleanserRequest request = service.buildRequest(ioMeta, inputData);
			// Add it to the request queue
			data.requests.add(request);
		}
		// Check request queues Currently there is really only one que
		for (int queue = 0; queue < data.requests.size(); queue++) {
			// If the queue is full or we have reached the end of the input and there are outstanding requests
			// then process them
			List<MDCleanserRequest> requests = data.requests;
			if (((inputData == null) && (requests.size() > 0)) || (requests.size() >= data.maxRequests)) {
				// Add request to the manager
				requestManager.addRequest(service, requests);
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
	protected boolean processComplete(MDCleanserMeta meta, MDCleanserData data) throws KettleException {
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
	protected synchronized boolean processInit(MDCleanserMeta meta, MDCleanserData data) throws KettleException {
		// Define the source I/O data
		IOMetaHandler sourceIO = data.sourceIO = getIOHandler();
		// Get meta information for input row
		sourceIO.inputMeta = getInputRowMeta();
		// Get meta information for output row.
		// Start with a copy of the input meta.
		// The Pass Thru step will filter this down to the fields that should be passed thru.
		sourceIO.outputMeta = sourceIO.inputMeta.clone();
		// Get the fields added by this step
		meta.getFields(sourceIO.outputMeta, getStepname(), null, null, this);

		// Get the index of the pass thru fields
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
		cleanserMeta = (MDCleanserMeta) smi;

		MDCleanserData data = (MDCleanserData) sdi;
		// Get next row of data to process
		Object[] inputData = getRow();
		// First time...
		if ((inputData != null) && first) {
			logDebug("MDCleanser.processRow(smi=" + smi + ", sdi=" + sdi + ")");
			first = false;
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
			cleanserMeta.getFields(data.outputMeta, getStepname(), null, null, this);
			if (!processInit(cleanserMeta, data)) {
				setOutputDone();
				return false;
			}
		}
		if (!process(inputData, data, cleanserMeta, data.sourceIO)) {
			setOutputDone();
			return false;
		}

		if(!MDCleanserMeta.isEnterprise() && (numLines > communityRecordLimit )){
			String message = BaseMessages.getString(PKG, "MDCleanserDialog.Community.Limit.Message");
			if(isSpoon()) {
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "community", "MDCleanser", message);
				Display.getDefault().asyncExec(mbt);
			}
			logError(message);

			inputData = null;
		}

		if (inputData == null) {
			// logStats(data,bcMeta);
			// ...then we are done
			if (!processComplete(cleanserMeta, data)) {
				setOutputDone();
				return false;
			}
			// We are done
			setOutputDone();
			return false;
		}
		return true;
	}

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
