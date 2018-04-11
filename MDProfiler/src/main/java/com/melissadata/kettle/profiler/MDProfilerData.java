package com.melissadata.kettle.profiler;

import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.w3c.dom.Node;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.MDPropTags;

public class MDProfilerData extends BaseStepData implements StepDataInterface {
	/**
	 * Wraps the XMLHandler.addTagValue(String, String) method. If the value is
	 * a zero-length string then it will be padded to one character. This is
	 * done so that the generated XML will have some value instead of the null
	 * that is currenly returned when it is read back in.
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
	 * Warps the XMLHandler.getTagValue(Node, String) method. Reverses the
	 * special handling done in the above addTagValue() method by triming values
	 * before returning them.
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
	public List<List<MDProfilerRequest>>	requests;
	public int								maxRequests	= 100;	// FIXME get max requests
//	private String							realLicense;
	public String							realDataPath;

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MDProfilerData clone() {
		try {
			MDProfilerData data = (MDProfilerData) super.clone();
			return data;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected clone problem", e);
		}
	}

	public String getDataPath() {
		if (!Const.isEmpty(realDataPath))
			return realDataPath;
		String dp = MDProps.getProperty(MDPropTags.TAG_LOCAL_DATA_PATH, "");
		realDataPath = dp;
		return dp;
	}

	public String getLicenseString() {
//		if (!Const.isEmpty(realLicense))
//			return realLicense;

		String lic = "";
		int retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, String.valueOf(MDPropTags.MDLICENSE_None)));
		if((retVal & MDPropTags.MDLICENSE_Profiler) != 0){
			lic = MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "");
		} else if ((retVal & MDPropTags.MDLICENSE_Community) != 0){
			lic = MDPropTags.MD_COMMUNITY_LICENSE;
		}

		if (Const.isEmpty(lic)) {
			lic = "DEMO";
		}
	//	realLicense = lic;
		return lic.trim();
	}
}
