package com.melissadata.kettle.personator.web;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.melissadata.kettle.personator.MDPersonatorCVRequest;
import com.melissadata.kettle.personator.MDPersonatorData;
import com.melissadata.kettle.personator.MDPersonatorRequest;




public abstract class AbstractPersonatorWebRequestHandler implements WebRequestHandler {

	public AbstractPersonatorWebRequestHandler() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDPersonatorData)
	 */
	public URL getWebURL(MDPersonatorData data) {
		return data.realWebPersonatorURL;
	}
	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#buildWebRequest(org.w3c.dom.Document, com.melissadata.kettle.MDPersonatorData, boolean)
	 */
	public boolean buildWebRequest(Document xmlDoc, MDPersonatorData data, List<MDPersonatorRequest> requests, boolean testing) throws KettleException {
		Element root = xmlDoc.getDocumentElement();
		
		// Address Verifier options
		addWebOptions(xmlDoc, root);
		
		// Add records
		boolean sendRequest = false;

		Element records = xmlDoc.createElement("Records");

		// Otherwise, add real records
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDPersonatorCVRequest request = (MDPersonatorCVRequest) requests.get(recordID);

			// TODO: More complete validity checks
			boolean valid = true;

			// Add request if it is valid
			if (valid) {
				// Create new record object
				Element record = xmlDoc.createElement("RequestRecord");

				// Add unique record id
				PersonatorWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);

				// Add request fields
				if (addWebRequestFields(xmlDoc, request, record)) {
					// Add the record to the document
					records.appendChild(record);

					// There is at least one request with inputs
					sendRequest = true;
				}
			}
		}
		root.appendChild(records);

		return sendRequest;
	}

	/**
	 * Called to add options for the address verification request
	 * 
	 * @param xmlDoc
	 * @param root
	 * @throws DOMException
	 */
	protected abstract void addWebOptions(Document xmlDoc, Element root) throws DOMException;

	/**
	 * Called to add specific request element fields
	 * 
	 * @param xmlDoc
	 * @param request
	 * @param record
	 * @return True if at least one request field was added
	 * @throws KettleValueException
	 */
	protected abstract boolean addWebRequestFields(Document xmlDoc, MDPersonatorCVRequest request, Element record) throws KettleException;

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#processWebResponse(org.dom4j.Document, com.melissadata.kettle.MDPersonatorData, boolean)
	 */
	public String processWebResponse(org.dom4j.Document doc, MDPersonatorData data, List<MDPersonatorRequest> requests, boolean testing) throws KettleException {
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("Response")) 
			throw new KettleException("MDPersonator: Response not found in XML response string");
		
		// Check the general result
		String resultCodes = PersonatorWebService.getElementText(response, "Results");
		resultCodes += PersonatorWebService.getElementText(response, "TransmissionResults");
		if (!Const.isEmpty(resultCodes))
			return resultCodes;
		
		// Get interface info
		getInterfaceInfo(response);
		
		// Get the response records (ignore if testing)
		if (!testing) {
			@SuppressWarnings("unchecked")
			Iterator<org.dom4j.Element> i = response.elementIterator("Records");
			
			org.dom4j.Element records = null;
			if(i.hasNext())
				records = (org.dom4j.Element) i.next();
			
			@SuppressWarnings("unchecked")
			Iterator<org.dom4j.Element> responseArray = records.elementIterator("ResponseRecord");
			
			while (responseArray.hasNext()) {				
				org.dom4j.Element record = (org.dom4j.Element) responseArray.next();
				
				// This is used to index the request being processed
				int recordID = PersonatorWebService.getElementInteger(record, "RecordID");
				
				// Get the request object for the specified record id
				MDPersonatorCVRequest request = (MDPersonatorCVRequest) requests.get(recordID);
				
				// Process individual output results
				processWebResponseFields(record, request);
			}
		}
		
		return "";
	}

	/**
	 * Retrieve information about the interface from the web response
	 * 
	 * @param response
	 */
	protected abstract void getInterfaceInfo(org.dom4j.Element response);

	/**
	 * Retrieve fields from the web response
	 * 
	 * @param record
	 * @param request
	 * @throws KettleException
	 */
	protected abstract void processWebResponseFields(org.dom4j.Element record, MDPersonatorCVRequest request) throws KettleException;
}
