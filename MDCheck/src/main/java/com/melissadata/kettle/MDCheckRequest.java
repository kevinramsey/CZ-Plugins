package com.melissadata.kettle;

import java.util.ArrayList;
import java.util.List;

import com.melissadata.kettle.support.IOMeta;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;

public class MDCheckRequest {

	// I/O handling information
	public IOMeta           ioMeta         = null;
	// Input data received
	public RowMetaInterface inputMeta      = null;
	public Object[]         inputData      = null;
	public int              inputDataSize  = 0;
	// Output data being generated
	public RowMetaInterface outputMeta     = null;
	public Object[]         outputData     = null;
	public int              outputDataSize = 0;
	// Result codes
	public List<String>     resultCodes    = new ArrayList<String>();

	/**
	 * Create one check request object.
	 *
	 * @param inputData
	 */
	public MDCheckRequest(IOMeta ioMeta, Object[] inputData) {

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

		if (value != null) {
			value = value.trim();
		}
		outputData = RowDataUtil.addValueData(outputData, outputDataSize++, value);
	}
}
