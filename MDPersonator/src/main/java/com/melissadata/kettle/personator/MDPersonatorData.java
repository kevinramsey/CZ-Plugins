package com.melissadata.kettle.personator;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.w3c.dom.Node;

import com.melissadata.kettle.personator.MDPersonatorMeta.OutputPhoneFormat;

public class MDPersonatorData extends BaseStepData implements StepDataInterface {
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
	public IOMetaHandler					sourceIO;
	public RowMetaInterface					inputMeta;
	public RowMetaInterface					outputMeta;
	public List<List<MDPersonatorRequest>>	requests;
	public OutputPhoneFormat				phoneFormat;
	public boolean							freeForm;
	public boolean							parsedAddr;
	//public int								realCustomerID;
	public String							realPAFID;
	public int								maxRequests;
	public URL								realWebPersonatorURL;
	public int								realWebTimeout;
	public int								realWebRetries;
	public int								realCVSTimeout;
	public int								realCVSRetries;
	public boolean							webAbortOnError;
	public boolean							cvsAbortOnError;
	public String							realProxyHost;
	public int								realProxyPort;
	public String							realProxyPass;
	public String							realProxyUser;
	public String							realLicense;
	public String							realDataPath;
	public File								realWorkPath;
	public static String					webInitMsg	= "";
	public KettleException					webInitException;

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MDPersonatorData clone() {
		try {
			MDPersonatorData data = (MDPersonatorData) super.clone();
			return data;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected clone problem", e);
		}
	}
}
