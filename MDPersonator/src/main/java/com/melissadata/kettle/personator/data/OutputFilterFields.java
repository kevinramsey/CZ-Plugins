package com.melissadata.kettle.personator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.melissadata.kettle.personator.FilterTarget;
import com.melissadata.kettle.personator.support.MetaVal;




public class OutputFilterFields implements Cloneable {

	public static final String TAG_FILTER = "filter_fields";
	public static final String TAG_FILTER_NAME = "filter_name";
	public static final String TAG_FILTER_RULE = "filter_rule";
	public static final String TAG_FILTER_TARGET = "filter_target";
	public static final String TAG_FILTERS = "filters";
	public static final String TAG_OUTPUT_FILTER = "output_filter";
	
	public List<FilterTarget> filterTargets;
	
	public HashMap<String,MetaVal> filterFields;

	public void init(){
		filterFields = new HashMap<String,MetaVal>();
		filterTargets = new ArrayList<FilterTarget>(1);

	}

}
