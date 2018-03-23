package com.melissadata.kettle.personator;

import java.util.List;

import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

public abstract class MDPersonatorCVService extends MDPersonatorService {
	public MDPersonatorCVService(MDPersonatorData checkData, MDPersonatorMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		super(checkData, checkMeta, space, log);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPersonatorService#buildRequest(com.melissadata.kettle.IOMeta, java.lang.Object[])
	 */
	@Override
	public MDPersonatorRequest buildRequest(IOMetaHandler ioMeta, Object[] inputData) {
		return new MDPersonatorCVRequest(ioMeta, inputData);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPersonatorService#determineRequestRoute(com.melissadata.kettle.MDPersonatorRequest)
	 */
	@Override
	public int determineRequestRoute(MDPersonatorRequest request) {
		// currently uses only one queue
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDPersonatorService#outputData(int)
	 */
	@Override
	public void outputData(List<MDPersonatorRequest> requests, int queue) {
		// All the testing for the local service takes place in the initialization phase
		if (testing)
			return;
		// Process the global address verifier requests
		if (checkMeta.personatorFields.hasMinRequirements()) {
			MDPersonatorEngine globalEngine = new MDPersonatorEngine(checkMeta.personatorFields);
			globalEngine.outputData(checkData, requests);
		}
	}
}
