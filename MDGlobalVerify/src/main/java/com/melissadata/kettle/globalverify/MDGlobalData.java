package com.melissadata.kettle.globalverify;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.melissadata.cz.support.IOMetaHandler;

public class MDGlobalData extends BaseStepData implements StepDataInterface {
	// Structure for handling of input/output records
	public IOMetaHandler				sourceIO;
	// Structure of input and output records
	public RowMetaInterface				inputMeta;
	public RowMetaInterface				outputMeta;
	public List<List<MDGlobalRequest>>	requests;
	// Customer id
	//public int							realCustomerID;
	// PAF id
	public String						realPAFID;
	// Maximum requests per batch
	public int							maxRequests;
	// URL for the web services
	public URL							realWebGlobalNameVerifierURL;
	public URL							realWebGlobalAddressVerifierURL;
	public URL							realWebGlobalPhoneVerifierURL;
	public URL							realWebGlobalEmailVerifierURL;
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
	public String						realDataPath;

	public static String				webInitMsg	= "";

	public KettleException				webInitException;


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MDGlobalData clone() {
		try {
			MDGlobalData data = (MDGlobalData) super.clone();
			return data;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected clone problem", e);
		}
	}
}
