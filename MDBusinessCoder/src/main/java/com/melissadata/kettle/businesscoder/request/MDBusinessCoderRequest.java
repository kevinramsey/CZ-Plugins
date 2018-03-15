package com.melissadata.kettle.businesscoder.request;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;

import com.melissadata.cz.support.IOMetaHandler;

public class MDBusinessCoderRequest {

	public static class BusCoderResults {

		public String	addressLine1;
		public String	censusBlock;
		public String	censusTract;
		public String	city;
		public String	companyName;
		public String   countryName;
		public String   countryCode;
		public String	countyFIPS;
		public String	countyName;
		public String	deliveryIndicator;
		public String   ein;
		public String	employeeEstimate;
		public String	Latitude;
		public String	Longitude;
		public String	mdAdressKey;
		public String	mdAddressKeyBase;
		public String	salesEstimate;
		public String	locationType;
		public String	naicsCode;
		public String	naicsCode2;
		public String	naicsCode3;
		public String	naicsDescription;
		public String	naicsDescription2;
		public String	naicsDescription3;
		public String	phone;
		public String	placeCode;
		public String	placeName;
		public String	plus4;
		public String	postalCode;
		public String	sicCode;
		public String	sicCode2;
		public String	sicCode3;
		public String	sicDescription;
		public String	sicDescription2;
		public String	sicDescription3;
		public String	state;
		public String	suite;
		public String	stockTicker;
		public String	webAddress;

		public String   firstName1;
		public String   lastName1;
		public String   gender1;
		public String   title1;
		public String   email1;

		public String   firstName2;
		public String   lastName2;
		public String   gender2;
		public String   title2;
		public String   email2;

//		public String	femaleOwned;
//		public String	homeBasedBusiness;
// 		public String	smallBusiness;
//		public String	totalEmployeeEstimate;
//		public String	totalSalesEstimate;


		public boolean	valid;
	}

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

	public BusCoderResults	busCoderResults;

	// Result codes
	public List<String>		resultCodes	= new ArrayList<String>();

	/**
	 * Create one check request object.
	 *
	 * @param inputData
	 */
	public MDBusinessCoderRequest(IOMetaHandler ioMeta, Object[] inputData) {

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
