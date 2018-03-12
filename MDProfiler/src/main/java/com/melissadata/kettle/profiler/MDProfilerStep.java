package com.melissadata.kettle.profiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.melissadata.cz.support.MessageBoxThread;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
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

import com.melissadata.mdProfiler;
import com.melissadata.kettle.profiler.RequestManager.Request;
import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;
import com.melissadata.kettle.profiler.error.MDAbortException;
import org.pentaho.di.ui.spoon.Spoon;

public class MDProfilerStep extends BaseStep implements StepInterface {
	/**
	 * @param key
	 * @return
	 */
	public static String getErrorString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDProfilerStep.Error." + key, args);
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
		return BaseMessages.getString(PKG, "MDProfilerStep.Log." + key, args);
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

	private static Class<?>			PKG			= MDProfilerStep.class;
	public static boolean			shMsg		= true;
	private mdProfiler				profiler	= null;
	private MDProfilerLocalService	service;
	private MDProfilerMeta			pMeta;
	public int						numLines;
	private final int               communityRecordLimit = 50000;
	private static boolean			isSpoon		= false;
	private IRequestManager			requestManager;
	private boolean                 isEnterprise;

	private int rowCount = 0;

	public MDProfilerStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		stepMeta.getName();
		System.currentTimeMillis();
		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				;
			}
			isSpoon = true;
		}
		isEnterprise = MDProfilerMeta.isEnterprise();
	}

	/**
	 * Called to add the result codes to the output data (if so requested)
	 *
	 * @param request
	 */
	public void addResultCodes(MDProfilerRequest request) {
		String val;
		for (String key : request.resultCodes.keySet()) {
			if(isEnterprise) {
				val = request.resultCodes.get(key);
			} else {
				val = "";
			}
			ValueMetaInterface v = null;
			v = new ValueMeta(key, ValueMetaInterface.TYPE_STRING);
			request.outputMeta.addValueMeta(v);
			request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, val);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#dispose(org.pentaho.di.trans.step. StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		logDebug("MDProfilerStep.dispose(smi=" + smi + ", sdi=" + sdi);
		//
		// Always dispose of the request task manager
		if (requestManager != null) {
			requestManager.dispose();
		}
		// Dispose the service
		if (service != null) {
			service.dispose();
		}
		if (profiler != null) {
			profiler.delete();
		}
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
		}
		// Call parent handler last
		super.dispose(smi, sdi);
	}

	public MDProfilerLocalService getService() {
		return service;
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
	private boolean handleCompletedRequests(MDProfilerMeta meta, MDProfilerData data, boolean done) throws KettleStepException {
		// Get completed requests (if any) from the request manager
		Request result;
		while ((result = requestManager.getCompletedRequest(done)) != null) {
			// Get the requests returned by this result
			List<MDProfilerRequest> requests = result.getRequests();
			try {
				// If this request had a problem then throw its exception now
				if (result.getException() != null)
					throw result.getException();
				for (MDProfilerRequest request : requests) {
					if (!isStopped()) {
						for (int i = 0; i < request.ioMeta.targetRowSets.size(); i++) {
							FilterTarget filter = request.ioMeta.filterTargets.get(i);
							RowSet rowSet = request.ioMeta.targetRowSets.get(i);
							if ((rowSet != null) && filter.getName().equals(OutputPin.PASSTHRU_RESULTCODE.name())) {
								if (getLogChannel().isRowLevel()) {
									logRowlevel("Sending row to :" + filter.getTargetStep().getName() + " : " + request.outputMeta.getString(request.outputData));
								}
								if (rowSet != null) {
									addResultCodes(request);
									putRowTo(request.outputMeta, request.outputData, rowSet);
									break;
								}
							}
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
				for (MDProfilerRequest request : requests) {
					putError(request.inputMeta, request.inputData, 1, e.toString(), null, "MDProfiler001");
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStep#init(org.pentaho.di.trans.step. StepMetaInterface,
	 * org.pentaho.di.trans.step.StepDataInterface)
	 */
	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		logDebug("MDProfilerStep.init(smi=" + smi + ", sdi=" + sdi);
		// Call parent handler first
		if (!super.init(smi, sdi))
			return false;
		// Dereference the control structures
		MDProfilerMeta profilerMeta = (MDProfilerMeta) smi;
		MDProfilerData profilerData = (MDProfilerData) sdi;
		// Allocate the service handler
		if (profilerMeta.profilerFields.hasMinRequirements()) {
			try {
				service = MDProfilerService.create(profilerData, profilerMeta, this, getLogChannel());
			} catch (KettleException e) {
				logError(e.getMessage());
				return false;
			}
		} else {
			// TODO put in messages
			String fieldsNeeded = "Output File not set: ";
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
		return true;
	}

	/**
	 * This processes the statistics and stores the lines in a cache for actions in the future.
	 *
	 * @param inputData
	 * @param data
	 * @param meta
	 * @throws KettleValueException
	 */
	public boolean process(Object[] inputData, MDProfilerData data, MDProfilerMeta meta, IOMetaHandler ioMeta) throws KettleException {
		if (inputData != null) {
			numLines++;
			// Build a request for this data
			MDProfilerRequest request = service.buildRequest(ioMeta, OutputPin.PASSTHRU_RESULTCODE, inputData);
			// Determine which request queue it should be routed to
			int queue = service.determineRequestRoute(request);
			// Add it to the request queue
			data.requests.get(queue).add(request);
		}
		// Check request queues
		for (int queue = 0; queue < data.requests.size(); queue++) {
			// If the queue is full or we have reached the end of the input and there are outstanding requests then process
			List<MDProfilerRequest> requests = data.requests.get(queue);
			if (((inputData == null) && (requests.size() > 0)) || (requests.size() >= data.maxRequests)) {
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
	protected boolean processComplete(MDProfilerMeta meta, MDProfilerData data) throws KettleException {
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
	protected boolean processInit(MDProfilerMeta meta, MDProfilerData data) throws KettleException {
		IOMetaHandler sourceIO = data.sourceIO = new IOMetaHandler();
		sourceIO.inputMeta = getInputRowMeta();
		// Get meta information for output row. Start with a copy of the input meta.
		sourceIO.outputMeta = sourceIO.inputMeta.clone();
		// Get the fields added by this step
		meta.getFields(sourceIO.outputMeta, getStepname(), null, null, this);
		// Cache the position of the RowSets for the filtered output.
		List<StreamInterface> targetStreams = meta.getStepIOMeta().getTargetStreams();
		Collection<FilterTarget> filterTargets = meta.oFilterFields.filterTargets.values();
		for (FilterTarget filterTarget : filterTargets) {
			for (StreamInterface targetStream : targetStreams) {
				if ((filterTarget.getTargetStepname() != null) && filterTarget.getTargetStepname().equals(targetStream.getStepname())) {
					RowSet targetRowSet = findOutputRowSet(getStepname(), getCopy(), targetStream.getStepname(), 0);
					if (targetRowSet != null) {
						sourceIO.addTargetRow(targetRowSet, filterTarget);
						break;
					}
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
		pMeta = (MDProfilerMeta) smi;
		MDProfilerData data = (MDProfilerData) sdi;
		// Get next row of data to process
		Object[] inputData = getRow();
		// First time...
		if ((inputData != null) && first) {
			logDebug("MDCheck.processRow(smi=" + smi + ", sdi=" + sdi + ")");
			first = false;
//			if (!MDProfilerMeta.enterprise) {
//				logError("You are not running the Enterprise Edition of PDI.");
//				setErrors(1);
//				stopAll();
//				return false;
//			}
			// Get meta information for input row
			data.inputMeta = getInputRowMeta();
			data.outputMeta = data.inputMeta.clone();
			//TODO do we need to set type to string ?
//			for (int i = 0; i < data.outputMeta.size(); i++) {
//				data.outputMeta.getValueMeta(i).setType(ValueMetaInterface.TYPE_STRING);
//			}
			// Get the fields added by this step
			pMeta.getFields(data.outputMeta, getStepname(), null, null, this);
			if (!processInit(pMeta, data)) {
				setOutputDone();
				return false;
			}
			profiler = pMeta.getProfiler();
			logDebug("Profiler Start profiling ...");
			profiler.StartProfiling();
		}
		if (!process(inputData, data, pMeta, data.sourceIO)) {
			setOutputDone();
			return false;
		}

		if((!MDProfilerMeta.isEnterprise()) && (numLines > communityRecordLimit)){
			String message = BaseMessages.getString(PKG, "MDProfilerDialog.Community.Limit.Message");
			if(isSpoon()) {
				MessageBoxThread mbt = new MessageBoxThread(Spoon.getInstance().getShell(), "community", "MDProfiler", message);
				Display.getDefault().asyncExec(mbt);
			}
			logError(message);

			inputData = null;
		}
		if (inputData == null) {
			// ...then we are done
			if (!processComplete(pMeta, data)) {
				setOutputDone();
				return false;
			}
			if (profiler == null)
				throw new KettleException("No input data found :" + inputData);
			// We call profileData after all requests are done
			profiler.ProfileData();
			// If not passThru we need to create the requests
			for (FilterTarget ft : pMeta.oFilterFields.filterTargets.values()) {
				if ((ft.getTargetStep() != null) && (ft.getPin() != OutputPin.PASSTHRU_RESULTCODE)) {
					List<MDProfilerRequest> requests = new ArrayList<MDProfilerRequest>();
					pMeta.getProfilerOutputFunctions().handleCompletedRequests(this, requests, pMeta, data, true, OutputPin.decode(ft.getName()));
				}
			}
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
		if (requestManager != null) {
			requestManager.stop();
		}
		// Contue with base class
		super.stopRunning(stepMetaInterface, stepDataInterface);
	}
}
