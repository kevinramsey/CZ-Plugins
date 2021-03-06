package com.melissadata.kettle.personator.web;

import java.net.URL;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.w3c.dom.Document;

import com.melissadata.kettle.personator.MDPersonatorData;
import com.melissadata.kettle.personator.MDPersonatorRequest;




public interface WebRequestHandler {

	/**
	 * Called to get the URL of the web service for this handler
	 * 
	 * @param data Where the web service URL is stored
	 * @return The URL needed
	 */
	URL getWebURL(MDPersonatorData data);

	/**
	 * Called to build the web service request as a document object
	 * 
	 * @param xmlDoc 
	 * @param data 
	 * @param testing 
	 * @return 
	 * @throws KettleException 
	 */
	boolean buildWebRequest(Document requestDoc, MDPersonatorData data, List<MDPersonatorRequest> requests, boolean testing) throws KettleException;

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
	String processWebResponse(org.dom4j.Document doc, MDPersonatorData data, List<MDPersonatorRequest> requests, boolean testing) throws KettleException;
	
}
