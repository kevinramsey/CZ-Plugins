package com.melissadata.kettle.globalverify.request;

import java.util.HashSet;
import java.util.Set;

import com.melissadata.cz.support.IOMetaHandler;


public class NameRequest {
	public static class NameResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		companyName;
		public String		prefix1;
		public String		first1;
		public String		middle1;
		public String		last1;
		public String		suffix1;
		public String		gender1;
		public String		prefix2;
		public String		first2;
		public String		middle2;
		public String		last2;
		public String		suffix2;
		public String		gender2;
		public boolean		valid;
	}
	public NameResults	nameResults;

	public NameRequest(IOMetaHandler ioMeta, Object[] inputData) {
	}
}
