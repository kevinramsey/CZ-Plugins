package com.melissadata.kettle.globalverify.web;

import java.net.URL;
import java.util.List;

import org.json.simple.JSONObject;
import org.pentaho.di.core.exception.KettleException;
import org.w3c.dom.Document;

import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalRequest;

public interface WebRequestHandler {
	/**
	 * Called to get the URL of the web service for this handler
	 * 
	 * @param data
	 *            Where the web service URL is stored
	 * @return The URL needed
	 */
	URL getWebURL(MDGlobalData data);

	/**
	 * Called to build the web service request as a document object
	 * 
	 * @throws KettleException
	 */
	boolean buildWebRequest(Document requestDoc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException;

	boolean buildWebRequest(JSONObject jsonRequest, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException;

	
	/**
	 * Called to process web service response
	 * 
	 * NOTE: Order of fields must match the order of fields in getFields()
	 * 
	 * @param doc
	 * @param data
	 * @param testing
	 * @return the general results code from the response
	 * @throws KettleException
	 */
	String processWebResponse(org.dom4j.Document doc, MDGlobalData data, List<MDGlobalRequest> requests, boolean testing) throws KettleException;

	//JSON
	String processWebResponse(JSONObject jsonResponse, MDGlobalData data, List<MDGlobalRequest> requests) throws KettleException;
		
	/**
	 * Get the name of the service
	 * 
	 */
	String getServiceName();
}
