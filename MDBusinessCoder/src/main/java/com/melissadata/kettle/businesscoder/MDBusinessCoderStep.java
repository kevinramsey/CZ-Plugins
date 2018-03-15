package com.melissadata.kettle.businesscoder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.ui.spoon.Spoon;

import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.request.MDBusinessCoderRequest;
import com.melissadata.kettle.businesscoder.request.RequestManager;
import com.melissadata.kettle.businesscoder.request.RequestManager.Request;
import com.melissadata.kettle.businesscoder.web.MDBusinessCoderWebService;
import com.melissadata.cz.support.FilterTarget;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MDAbortException;
import com.melissadata.cz.support.MDBinaryEvaluator;
import com.melissadata.cz.support.MessageBoxThread;

public class MDBusinessCoderStep extends BaseStep implements StepInterface {

	public static String getErrorString(String string) {
		return BaseMessages.getString(PKG, "MDBusinessCoderMeta.Error." + string);
		
	}

	public static String getErrorString(String string, String string2) {
		return BaseMessages.getString(PKG, "MDBusinessCoderMeta.Error." + string, string2);
	}

	public static String getErrorString(String string, String timeoutMessage, String string2) {
		return BaseMessages.getString(PKG, "MDBusinessCoderMeta.Error." + string, timeoutMessage,string2);
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
		return BaseMessages.getString(PKG, "MDBusinessCoderMeta.Log." + string, valueOf);
	}

	public static String getLogString(String string, String string2, String string3) {
		return BaseMessages.getString(PKG, "MDBusinessCoderMeta.Log." + string, string2,string3);
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
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(),"sleep", "Business Coder", message);
				Display.getDefault().asyncExec(mbt);
			}
		}
	}
	private static Class<?>			PKG							= MDBusinessCoderMeta.class;
	private static boolean					isSpoon		= false;
	public static boolean					shMsg;
	private MDBusinessCoderWebService		service;
	// Output filter evaluators
	private Map<String, MDBinaryEvaluator>	evaluators	= new HashMap<String, MDBinaryEvaluator>();
	private MDBusinessCoderMeta				bcMeta;
	public int								numLines;
	int										copy;
	// Request Manager
	private RequestManager					requestManager;

	public MDBusinessCoderStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/**
	 * Called to add the result codes to the output data (if so requested)
	 *
	 * @param requests
	 */
	public void addResultCodes(List<MDBusinessCoderRequest> requests) {
		// Add result codes for each request
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDBusinessCoderRequest request = requests.get(recordID);
			if (!Const.isEmpty(bcMeta.businessCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_RESULTS).metaValue)) {
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
		logDebug("MDCheck.dispose(smi=" + smi + ", sdi=" + sdi);
		//
		// Always dispose of the request task manager
		if (requestManager != null) {
			requestManager.dispose();
		}
		// Dispose the service
		service.dispose();
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
	private boolean handleCompletedRequests(MDBusinessCoderMeta meta, MDBusinessCoderData data, boolean done) throws KettleStepException {
		// Get completed requests (if any) from the request manager
		Request result;
		while ((result = requestManager.getCompletedRequest(done)) != null) {
			// Get the requests returned by this result
			List<MDBusinessCoderRequest> requests = result.getRequests();
			try {
				// If this request had a problem then throw its exception now
				if (result.getException() != null)
					throw result.getException();
				// Add the result codes to the output data
				addResultCodes(requests);
				// Output the completed rows
				for (MDBusinessCoderRequest request : requests) {
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
				for (MDBusinessCoderRequest request : requests) {
					putError(request.inputMeta, request.inputData, 1, e.toString(), null, "MDBusinessCoder001");
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
		logDebug("MDBusinessCoder.init(smi=" + smi + ", sdi=" + sdi);
		shMsg = true;
		// Call parent handler first
		if (!super.init(smi, sdi))
			return false;
		// Dereference the control structures
		MDBusinessCoderMeta meta = (MDBusinessCoderMeta) smi.clone();
		MDBusinessCoderData data = (MDBusinessCoderData) sdi;
		// Allocate the service handler
		if ((service = MDBusinessCoderWebService.create(data, meta, this, getLogChannel())) == null)
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
		if (!service.checkProxy()) {
			logError(MDBusinessCoderData.webInitMsg);
			return false;
		}

		if(!MDBusinessCoderMeta.isEnterprise()){
			String message = BaseMessages.getString(PKG, "MDBusinessCoderDialog.Community.PopupMessage");
			if(isSpoon()) {
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "community", "Business Coder", message);
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
	 * This processes the statistics and stores the lines in a cache for actions in the future.
	 *
	 * @param inputData
	 * @param data
	 * @param meta
	 * @throws KettleValueException
	 */
	public boolean process(Object[] inputData, MDBusinessCoderData data, MDBusinessCoderMeta meta, IOMetaHandler ioMeta) throws KettleException {
		if (inputData != null) {
			// Build a request for this data
			MDBusinessCoderRequest request = service.buildRequest(ioMeta, inputData);
			// Add it to the request queue
			data.requests.add(request);
		}
		// Check request queues
		for (int queue = 0; queue < data.requests.size(); queue++) {
			// If the queue is full or we have reached the end of the input and there are outstanding requests
			// then process them
			List<MDBusinessCoderRequest> requests = data.requests;
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
	protected boolean processComplete(MDBusinessCoderMeta meta, MDBusinessCoderData data) throws KettleException {
		// Wait for all outstanding requests to complete
		if (!handleCompletedRequests(meta, data, true))
			return false;
		return true;
	}

	private synchronized IOMetaHandler getIOHandler() {
		return new IOMetaHandler();
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
	protected synchronized boolean processInit(MDBusinessCoderMeta meta, MDBusinessCoderData data) throws KettleException {
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
		bcMeta = (MDBusinessCoderMeta) smi;
		MDBusinessCoderData data = (MDBusinessCoderData) sdi;
		// Get next row of data to process
		Object[] inputData = getRow();
		// First time...
		if ((inputData != null) && first) {
			logDebug("MDCheck.processRow(smi=" + smi + ", sdi=" + sdi + ")");
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
			bcMeta.getFields(data.outputMeta, getStepname(), null, null, this);
			if (!processInit(bcMeta, data)) {
				setOutputDone();
				return false;
			}
		}
		if (!process(inputData, data, bcMeta, data.sourceIO)) {
			setOutputDone();
			return false;
		}
		if (inputData == null) {
			// logStats(data,bcMeta);
			// ...then we are done
			if (!processComplete(bcMeta, data)) {
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
		if (requestManager != null)
			requestManager.stop();
		
		// Contue with base class
		super.stopRunning(stepMetaInterface, stepDataInterface);
	}
}
