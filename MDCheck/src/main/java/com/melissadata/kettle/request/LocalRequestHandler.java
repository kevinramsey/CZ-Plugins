package com.melissadata.kettle.request;

import java.util.List;

import com.melissadata.kettle.MDCheckData;
import com.melissadata.kettle.MDCheckRequest;
import org.pentaho.di.core.exception.KettleException;

public interface LocalRequestHandler {
	/**
	 * Called to process a local service request
	 *
	 * @param data
	 * @throws KettleException
	 */
	void doLocalRequests(MDCheckData data, List<MDCheckRequest> requests) throws KettleException;
}
