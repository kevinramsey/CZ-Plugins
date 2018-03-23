package com.melissadata.kettle.globalverify.request;

import java.util.HashSet;
import java.util.Set;

import com.melissadata.cz.support.IOMetaHandler;


public class PhoneRequest {
	public static class PhoneResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		phoneNumber;
		public String		subscriber;
		public String		carrier;
		public String		callerID;
		public String		country;
		public String		countryAbbriviation;
		public String		dialingCode;
		public String		internationalPrefix;
		public String		nationalPrefix;
		public String		destCode;
		public String		locality;
		public String		AdminArea;
		public String		language;
		public String		utc;
		public String		dst;
		public String		latitude;
		public String		longitude;
		public String 		internationalNumber;
		public String 		postalCode;
		public String		Suggestions;
		public boolean		valid;
	}
	public PhoneResults	phoneResults;

	public PhoneRequest(IOMetaHandler ioMeta, Object[] inputData) {
	}
}
