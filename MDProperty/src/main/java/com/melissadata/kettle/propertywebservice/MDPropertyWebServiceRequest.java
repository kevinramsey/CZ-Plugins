package com.melissadata.kettle.propertywebservice;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;

public class MDPropertyWebServiceRequest {
	public IOMetaHandler	ioMeta;
	public RowMetaInterface	inputMeta;
	public Object[]			inputData;
	public int				inputDataSize;
	public RowMetaInterface	outputMeta;
	public Object[]			outputData;
	public int				outputDataSize;
	public List<String>		resultCodes	= new ArrayList<String>();

	/**
	 * Create one check request object.
	 *
	 * @param inputData
	 */
	public MDPropertyWebServiceRequest(IOMetaHandler ioMeta, Object[] inputData) {
		this.ioMeta = ioMeta;
		// Get a copy of the input data
		inputMeta = ioMeta.inputMeta;
		this.inputData = RowDataUtil.createResizedCopy(inputData, inputMeta.size());
		inputDataSize = inputMeta.size();
		// Create the initial output data
		outputMeta = ioMeta.outputMeta;
		outputData = RowDataUtil.allocateRowData(outputMeta.size());
		outputDataSize = 0;
	}

	/**
	 * Called to add one column to the value data for this request.
	 * The text is trimmed (if not null). If trimmed text is empty then convert to a null element
	 *
	 * @param value
	 */
	public void addOutputData(String value) {
		if ((value != null)) {
			value = value.trim();
		}
		outputData = RowDataUtil.addValueData(outputData, outputDataSize++, value);
	}
}