package com.melissadata.kettle.globalverify;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;

import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.kettle.globalverify.request.AddressRequest;
import com.melissadata.kettle.globalverify.request.EmailRequest;
import com.melissadata.kettle.globalverify.request.NameRequest;
import com.melissadata.kettle.globalverify.request.PhoneRequest;

public class MDGlobalRequest {
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
	private String			serviceType;
	public NameRequest		nameRequest;
	public AddressRequest	addressRequest;
	public PhoneRequest		phoneRequest;
	public EmailRequest		emailRequest;
	// Result codes
	public List<String>		resultCodes	= new ArrayList<String>();

	/**
	 * Create one check request object.
	 *
	 * @param inputData
	 */
	public MDGlobalRequest(IOMetaHandler ioMeta, Object[] inputData) {
		this.ioMeta = ioMeta;
		// Get a copy of the input data
		inputMeta = ioMeta.inputMeta;
		this.inputData = RowDataUtil.createResizedCopy(inputData, inputMeta.size());
		inputDataSize = inputMeta.size();
		nameRequest = new NameRequest(ioMeta, inputData);
		addressRequest = new AddressRequest(ioMeta, inputData);
		emailRequest = new EmailRequest(ioMeta, inputData);
		phoneRequest = new PhoneRequest(ioMeta, inputData);
		// Create the initial output data
		outputMeta = ioMeta.outputMeta;
		outputData = RowDataUtil.allocateRowData(outputMeta.size());
		outputDataSize = 0;
	}

	/**
	 * Called to add one column to the value data for this request. The text is trimmed (if not null). If trimmed text is
	 * empty then convert to a null element
	 *
	 * @param value
	 */
	public void addOutputData(String value) {
		if ((value != null)) {
			value = value.trim();
		}
		outputData = RowDataUtil.addValueData(outputData, outputDataSize++, value);
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
}
