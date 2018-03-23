package com.melissadata.kettle.cleanser.request;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;

import com.melissadata.cz.support.IOMetaHandler;

public class MDCleanserRequest {

	// I/O handling information
	public IOMetaHandler	ioMeta;
	// Input data received
	public RowMetaInterface	inputMeta;
	public Object[]			inputData;
	public int				inputDataSize;

	// Output data being generated
	public RowMetaInterface	outputMeta;
	public Object[]			outputData;
	public int				outputDataSize;

//	private boolean doPrint = false;
	// Result codes
	public List<String>		resultCodes	= new ArrayList<String>();

	/**
	 * Create one check request object.
	 *
	 * @param inputData
	 */
	public MDCleanserRequest(IOMetaHandler ioMeta, Object[] inputData) {


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
