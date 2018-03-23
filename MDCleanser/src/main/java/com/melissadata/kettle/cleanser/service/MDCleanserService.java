package com.melissadata.kettle.cleanser.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import com.melissadata.kettle.cleanser.MDCleanserData;
import com.melissadata.kettle.cleanser.MDCleanserMeta;
import com.melissadata.kettle.cleanser.MDCleanserOperation;
import com.melissadata.kettle.cleanser.request.MDCleanserRequest;
import com.melissadata.kettle.cleanser.request.RequestManager;
import com.melissadata.cz.support.IOMetaHandler;

public class MDCleanserService {

	public static synchronized MDCleanserService create(MDCleanserData checkData, MDCleanserMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		MDCleanserService service = new MDCleanserService(checkData, checkMeta, space, log);
		return service;
	}


	public MDCleanserData		cleanserData;
	protected MDCleanserMeta	cleanserMeta;
	protected VariableSpace		space;
	public LogChannelInterface	log;
	protected boolean			initFailed;
	
	
	private HashMap<String, MDCleanserOperation> cleanserOperations;
	

	public MDCleanserService(MDCleanserData checkData, MDCleanserMeta checkMeta, VariableSpace space, LogChannelInterface log) {
		cleanserData = checkData;
		cleanserMeta = checkMeta;
		this.space = space;
		this.log = log;
		initFailed = true;
	}

	public MDCleanserRequest buildRequest(IOMetaHandler ioMeta, Object[] inputData) {
		return new MDCleanserRequest(ioMeta, inputData);
	}

	public RequestManager createRequestManager() throws KettleException {
		// TODO should this be configurable
		int maxThreads = 1;//Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_MAX_THREADS, "1"));
		return new RequestManager(maxThreads);
	}

	public void dispose() {
		for(MDCleanserOperation operation : cleanserOperations.values()){
			operation.dispose();
		}

	}

	private synchronized void doCleanserRequests(List<MDCleanserRequest> requests) throws KettleException {

		String[] myRes;

		for (MDCleanserRequest req : requests) {

			String name;
			String value;
			for (int h = 0; h < req.inputDataSize; h++) {
				name = req.inputMeta.getValueMeta(h).getName();
				if (req.inputData[h] != null)
					value = String.valueOf(req.inputData[h]);
				else
					value = "";

				MDCleanserOperation cleanseOperation = cleanserOperations.get(name);
				if (cleanseOperation == null) {
					//System.out.println(" Operation is null this shouldn't happen");
				} else {
					cleanseOperation.setLog(log);
					cleanseOperation.setColumnValues(req);
					// Do the cleansing
					myRes = cleanseOperation.cleanse(value, req);
					//FIXME Check box Add Pass thru
					if(cleanseOperation.isPassThrough()) {
						req.addOutputData(value);
					}
					if(!Const.isEmpty(cleanseOperation.getOperationName())) {
						req.addOutputData(myRes[0]);
					}
				}
			}
		}
	}

	public void init() throws KettleException {
		// Allocate request array
		cleanserData.requests = new ArrayList<MDCleanserRequest>();
		// TODO Should this be configurable
		cleanserData.maxRequests = 100;//Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_MAX_REQUESTS, "100"));
		initOperations();

	}
	
	private void initOperations() throws KettleException{
		
		cleanserOperations = cleanserMeta.getCleanserFieldOperations();
		
		for(MDCleanserOperation operation : cleanserOperations.values()){
			operation.init(log, cleanserMeta.cleanserFields.appendField, space);
		}
		
		
	}


	public void processRequests(List<MDCleanserRequest> requests/*, int attempts*/) throws KettleException {

		try {
			if (cleanserMeta.cleanserFields.hasMinRequirements()) {
				doCleanserRequests( requests);
			}
		} catch (Exception e) {
			// Remember what went wrong

			throw new KettleException(e);
		}
	}
}
