package com.melissadata.kettle.globalverify.data;

import java.util.HashMap;

import com.melissadata.kettle.globalverify.support.MetaVal;

public class GlobalVerifyConfigFields {
	public static final String		TAG_GLOBAL_ADDRESS_CONFIG		= "globaladdress_config";
	public static String			TAG_GLOBAL_ADDRESS_LICENSE		= "config_license";
	public static String			TAG_GLOBAL_ADDRESS_CUSTOMERID	= "config_customerid";
	public HashMap<String, MetaVal>	configFields;

	/**
	 * initializes all the default value
	 */
	public void init() {
		if (configFields == null) {
			configFields = new HashMap<String, MetaVal>();
		}
		// repository xml key tag, metavalue default, web tag, and size are set here
		configFields.put(TAG_GLOBAL_ADDRESS_LICENSE, new MetaVal("", "", 0));
		configFields.put(TAG_GLOBAL_ADDRESS_CUSTOMERID, new MetaVal("", "CustomerID", 0));
	}
}
