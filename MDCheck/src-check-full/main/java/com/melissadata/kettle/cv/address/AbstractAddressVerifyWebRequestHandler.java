package com.melissadata.kettle.cv.address;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractAddressVerifyWebRequestHandler implements WebRequestHandler {
	public AbstractAddressVerifyWebRequestHandler() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#buildWebRequest(org.w3c.dom.Document, com.melissadata.kettle.MDCheckData,
	 * boolean)
	 */
	public boolean buildWebRequest(Document xmlDoc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		Element root = xmlDoc.getDocumentElement();
		// Address Verifier options
		addWebOptions(xmlDoc, root);
		// Add records
		boolean sendRequest = false;
		// RequestArray
		// If testing, create a fake request record
		if (testing) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "1");
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressLine1", "1600 Pennsylvania Ave.");
			MDCheckWebService.addTextNode(xmlDoc, record, "City", "Washington");
			MDCheckWebService.addTextNode(xmlDoc, record, "State", "DC");
			root.appendChild(record);
			// There is at least one request
			sendRequest = true;
		} else {
			// Otherwise, add real records
			for (int recordID = 0; recordID < requests.size(); recordID++) {
				MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
				// TODO: More complete validity checks
				boolean valid = true;
				// Add request if it is valid
				if (valid) {
					// Create new record object
					Element record = xmlDoc.createElement("Record");
					// Add unique record id
					MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);
					// Add request fields
					if (addWebRequestFields(xmlDoc, request, record)) {
						// Add the record to the document
						root.appendChild(record);
						// There is at least one request with inputs
						sendRequest = true;
					}
				}
			}
		}
		return sendRequest;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURLCVS(com.melissadata.kettle.MDCheckData)
	 */
	public URL getCVSURL(MDCheckData data) {
		return data.realCVSAddressVerifierURL;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDCheckData)
	 */
	public URL getWebURL(MDCheckData data, int queue) {
		return data.realWebAddressVerifierURL;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#processWebResponse(org.dom4j.Document, com.melissadata.kettle.MDCheckData,
	 * boolean)
	 */
	public String processWebResponse(org.dom4j.Document doc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("ResponseArray")) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.ResponseNotFound")); }
		// Check the general result
		String resultCodes = MDCheckWebService.getElementText(response, "Results");
		if (!Const.isEmpty(resultCodes)) { return resultCodes; }
		// Get interface info
		getInterfaceInfo(response);
		// Get the response records (ignore if testing)
		if (!testing) {
			@SuppressWarnings("unchecked")
			Iterator<org.dom4j.Element> i = response.elementIterator("Record");
			while (i.hasNext()) {
				org.dom4j.Element record = i.next();
				// This is used to index the request being processed
				int recordID = MDCheckWebService.getElementInteger(record, "RecordID");
				// Get the request object for the specified record id
				MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
				// Process individual output results
				processWebResponseFields(record, request);
			}
		}
		return "";
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
	protected abstract boolean addWebRequestFields(Document xmlDoc, MDCheckCVRequest request, Element record) throws KettleException;

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
	protected abstract void processWebResponseFields(org.dom4j.Element record, MDCheckCVRequest request) throws KettleException;
}
