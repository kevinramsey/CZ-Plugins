package com.melissadata.kettle.profiler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;

import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;

public class MDProfilerRequest {
	// I/O handling information
	public IOMetaHandler		ioMeta;
	// Input data received
	public RowMetaInterface		inputMeta;
	public Object[]				inputData;
	public int					inputDataSize;
	// Output data being generated
	public RowMetaInterface		outputMeta;
	public Object[]				outputData;
	public int					outputDataSize;
	public OutputPin			outputPin;
	public Map<String, String>	resultCodes	= new LinkedHashMap<String, String>();


	public MDProfilerRequest(IOMetaHandler ioMeta, OutputPin outpin, Object[] inputData) {
		this.ioMeta = ioMeta;
		// Get a copy of the input data
		inputMeta = ioMeta.inputMeta;
		outputPin = outpin;
		if (outpin == OutputPin.PASSTHRU_RESULTCODE) {
			this.inputData = RowDataUtil.createResizedCopy(inputData, inputMeta.size());
			inputDataSize = inputMeta.size();
			// Create the initial output data
			outputMeta = ioMeta.outputMeta;
			outputData = RowDataUtil.allocateRowData(outputMeta.size());
			outputDataSize = 0;
		} else {
			this.inputData = new Object[1];
			inputDataSize = 1;
			ValueMetaInterface v = null;
			RowMetaInterface cleanRow = new RowMeta();
			for (String field : outpin.getOutputFields()) {
				v = new ValueMeta(field, ValueMetaInterface.TYPE_STRING);
				v.setOrigin(outpin.name());
				cleanRow.addValueMeta(v);
			}
			// Create the initial output data
			outputMeta = cleanRow.clone();
			outputData = RowDataUtil.allocateRowData(outputMeta.size());
			outputDataSize = 0;
		}
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
