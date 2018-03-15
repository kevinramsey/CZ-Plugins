package com.melissadata.kettle.businesscoder;

import java.net.URL;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.kettle.businesscoder.request.MDBusinessCoderRequest;


public class MDBusinessCoderData extends BaseStepData implements StepDataInterface {

	// Structure for handling of input/output records
	public IOMetaHandler				sourceIO;
	// Structure of input and output records
	public RowMetaInterface				inputMeta;
	public RowMetaInterface				outputMeta;
	public List<MDBusinessCoderRequest>	requests;
	// Customer id
	//public int							realCustomerID;
	// Maximum requests per batch
	public int							maxRequests;
	// URL for the web services
	public URL							realWebBusinessCoderURL;
	// Timeout settings
	public int							realWebTimeout;
	public int							realWebRetries;
	public int							realCVSTimeout;
	public int							realCVSRetries;
	// Abort handling
	public boolean						webAbortOnError;
	public boolean						cvsAbortOnError;
	// Optional proxy settings
	public String						realProxyHost;
	public int							realProxyPort;
	public String						realProxyPass;
	public String						realProxyUser;
	// License and data path
	public String						realLicense;
	public static String				webInitMsg	= "";
	public KettleException				webInitException;

}
