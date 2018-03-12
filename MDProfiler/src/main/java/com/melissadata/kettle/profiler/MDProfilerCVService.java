package com.melissadata.kettle.profiler;

import java.util.List;

import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

import com.melissadata.mdProfiler;
import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;

public abstract class MDProfilerCVService extends MDProfilerService {
	protected mdProfiler	profiler	= null;

	public MDProfilerCVService(MDProfilerData checkData, MDProfilerMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		super(checkData, checkMeta, space, log);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDGlobalAddressService#buildRequest(com.melissadata.kettle.IOMeta, java.lang.Object[])
	 */
	@Override
	public MDProfilerRequest buildRequest(IOMetaHandler ioMeta, OutputPin outpin, Object[] inputData) {
		return new MDProfilerRequest(ioMeta, outpin, inputData);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.melissadata.kettle.MDGlobalAddressService#determineRequestRoute(com.melissadata.kettle.MDGlobalAddressRequest)
	 */
	@Override
	public int determineRequestRoute(MDProfilerRequest request) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDGlobalAddressService#outputData(int)
	 */
	@Override
	public void outputData(List<MDProfilerRequest> requests, int queue) {
		// Nothing to do here
	}
}
