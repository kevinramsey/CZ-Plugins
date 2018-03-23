package com.melissadata.kettle.globalverify.request;

import java.util.HashSet;
import java.util.Set;

import com.melissadata.cz.support.IOMetaHandler;


public class EmailRequest {
	public static class EmailResults {
		public Set<String>	resultCodes	= new HashSet<String>();
		public String		email;
		public String		boxName;
		public String		domain;
		public String		topDomain;
		public String		topDomainDescription;
		public String		dateChecked;
		public boolean		valid;
	}
	public EmailResults	emailResults;

	public EmailRequest(IOMetaHandler ioMeta, Object[] inputData) {
	}
}
