package com.melissadata.kettle.request;

import java.net.URL;
import java.util.List;

import com.melissadata.kettle.MDCheckData;
import com.melissadata.kettle.MDCheckRequest;
import org.json.simple.JSONObject;
import org.pentaho.di.core.exception.KettleException;
import org.w3c.dom.Document;


public interface WebRequestHandler {
	/**
	 * Called to build the web service request as a document object
	 *
	 * @param requestDoc
	 * @param data
	 * @param testing
	 * @return
	 * @throws KettleException
	 */
	boolean buildWebRequest(Document requestDoc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException;
	
	boolean buildWebRequest(JSONObject jsonRequest, MDCheckData data, List<MDCheckRequest> requests) throws KettleException;

	/**
	 * Called to get the CVS URL of the web service for this handler
	 *
	 * @param data
	 *            Where the web service URL is stored
	 * @return The URL needed
	 */
	URL getCVSURL(MDCheckData data);

	/**
	 * Get the name of the service
	 *
	 */
	String getServiceName();

	/**
	 * Called to get the URL of the web service for this handler
	 *
	 * @param data
	 *            Where the web service URL is stored
	 * @return The URL needed
	 */
	URL getWebURL(MDCheckData data, int queue);

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
	String processWebResponse(org.dom4j.Document doc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException;
	
	//JSON
	String processWebResponse(JSONObject jsonResponse, MDCheckData data, List<MDCheckRequest> requests) throws KettleException;
}
