package com.melissadata.kettle.personator.data;

import java.util.HashMap;

import com.melissadata.kettle.personator.support.MetaVal;




public class PersonatorConfigFields {
	public static final String TAG_PERSONATOR_CONFIG = "personator_config";

	public static String TAG_PERSONATOR_LICENSE = "config_license";
	public static String TAG_PERSONATOR_CUSTOMERID = "config_customerid";

	
	public HashMap<String,MetaVal> configFields;

	/**
	 *  initializes all the default value
	 */
	public void init(){	
		if(configFields == null){
			configFields = new HashMap<String, MetaVal>();
		}
		// repository xml key tag, metavalue default, web tag, and size are set here
		configFields.put(TAG_PERSONATOR_LICENSE, new MetaVal("","",0));
		configFields.put(TAG_PERSONATOR_CUSTOMERID, new MetaVal("","CustomerID",0));

	}
}
