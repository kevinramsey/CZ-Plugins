package com.melissadata.kettle.cleanser.data;

import java.util.HashMap;

import com.melissadata.kettle.cleanser.MDCleanserOperation;

public class CleanserFields {


	public static final String		TAG_CLEANSER_INPUT		= "cleanser_input";
	public static final String		TAG_CLEANSER_OUTPUT		= "cleanser_output";
	public static final String      TAG_OUTPUT_RESULTS		= "result_codes";


//	public HashMap<String, MetaVal>	outputFields;
	public HashMap<String, MDCleanserOperation> cleanserFieldOperations;
	public boolean appendField = false;
	
//	public String					webVersion						= "";
//	public int						fieldsAdded;

	/**
	 * checks to see if the minimum requirements to run are met
	 *
	 * @return
	 */
	public boolean hasMinRequirements() {
		// FIXME MIN input
		return true;
	}

	/**
	 * initializes all the default value
	 */
	public void init() {

		if (cleanserFieldOperations == null){
			cleanserFieldOperations = new HashMap<String, MDCleanserOperation>();
		}
	}
}
