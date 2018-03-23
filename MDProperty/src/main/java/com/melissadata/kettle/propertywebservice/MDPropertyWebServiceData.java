package com.melissadata.kettle.propertywebservice;

import java.net.URL;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.w3c.dom.Node;

import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.kettle.propertywebservice.request.MDPropertyWebServiceRequest;

public class MDPropertyWebServiceData extends BaseStepData implements StepDataInterface {

	/**
	 * Wraps the XMLHandler.addTagValue(String, String) method.
	 * If the value is a zero-length string then it will be padded to one character. This is done so that the
	 * generated XML will have some value instead of the null that is currenly returned when it is read back in.
	 *
	 * @param tag
	 * @param val
	 * @return
	 */
	public static String addTagValue(String tag, String val) {

		if ((val != null) && (val.length() == 0)) {
			val = " ";
		}
		return XMLHandler.addTagValue(tag, val);
	}

	/**
	 * Warps the XMLHandler.getTagValue(Node, String) method.
	 * Reverses the special handling done in the above addTagValue() method by triming values before returning them.
	 *
	 * @param n
	 * @param tag
	 * @return
	 */
	public static String getTagValue(Node n, String tag) {

		String value = XMLHandler.getTagValue(n, tag);
		if (value != null) {
			value = value.trim();
		}
		return value;
	}

	// Structure for handling of input/output records
	public IOMetaHandler						sourceIO;
	// Structure of input and output records
	public RowMetaInterface						inputMeta;
	public RowMetaInterface						outputMeta;
	public List<MDPropertyWebServiceRequest>	requests;
	// Customer id
	public int									realCustomerID;
	/*
	 * TODO OptPropertyDetail find out if this should be
	 * used currently is just set to true
	 */
	public boolean								realOptPropertyDetail	= true;
	// Maximum requests per batch
	public int									maxRequests;
	// URL for the web services
	public URL									realPropertyWebServiceURL;
	// Timeout settings
	public int									realWebTimeout;
	public int									realWebRetries;
	public int									realCVSTimeout;
	public int									realCVSRetries;
	// Abort handling
	public boolean								webAbortOnError;
	public boolean								cvsAbortOnError;
	// Optional proxy settings
	public String								realProxyHost;
	public int									realProxyPort;
	public String								realProxyPass;
	public String								realProxyUser;
	// License and data path
	public String								realLicense;
	public static String						webInitMsg				= "";
	public KettleException						webInitException;

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MDPropertyWebServiceData clone() {

		try {
			MDPropertyWebServiceData data = (MDPropertyWebServiceData) super.clone();
			return data;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected clone problem", e);
		}
	}
}
