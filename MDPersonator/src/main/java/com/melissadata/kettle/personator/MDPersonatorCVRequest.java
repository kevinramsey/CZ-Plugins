package com.melissadata.kettle.personator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Structure for contact verification requests
 */
public class MDPersonatorCVRequest extends MDPersonatorRequest {
	public static class PersonatorResults {
		public Set<String>				resultCodes		= new HashSet<String>();
		public HashMap<String, String>	outputFields	= new HashMap<String, String>();	;
		public boolean					valid;
	}
	public PersonatorResults	personatorResults;

	public MDPersonatorCVRequest(IOMetaHandler ioData, Object[] inputData) {
		super(ioData, inputData);
	}
}
