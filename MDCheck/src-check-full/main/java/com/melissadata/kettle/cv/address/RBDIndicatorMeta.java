package com.melissadata.kettle.cv.address;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RBDIndicatorMeta implements WebRequestHandler, Cloneable {
	public String			webVersion	= "";
	public String			webMsg		= "";
	public KettleException	webException;

	public RBDIndicatorMeta(MDCheckStepData data) {
	}

	public boolean buildWebRequest(Document xmlDoc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		Element root = xmlDoc.getDocumentElement();
		boolean sendRequest = false;
		// If testing then add a fake record
		if (testing) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			root.appendChild(record);
			MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "1");
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressKey", "97223");
			// There is at least one request
			sendRequest = true;
		} else {
			// Otherwise add real records
			for (int recordID = 0; recordID < requests.size(); recordID++) {
				MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
				// Get the address key
				String addressKey = null;
				addressKey = (request.addrResults != null) ? request.addrResults.AddressKey : null;
				// Address key must be set and in a valid format
				boolean valid = !Const.isEmpty(addressKey);
				// Add request if it is valid
				if (valid) {
					// Create new record object
					Element record = xmlDoc.createElement("Record");
					root.appendChild(record);
					// Add unique record id
					MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);
					// Add request fields
					MDCheckWebService.addTextNode(xmlDoc, record, "AddressKey", addressKey);
					// There is at least one request
					sendRequest = true;
				}
			}
		}
		return sendRequest;
		// return false;
	}

	public URL getCVSURL(MDCheckData data) {
		return data.realCVSRBDIndicatorURL;
	}

	public String getServiceName() {
		return "RBDI Indicator";
	}

	public URL getWebURL(MDCheckData data, int queue) {
		return data.realWebRBDIndicatorURL;
	}

	public String processWebResponse(org.dom4j.Document doc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		String tmpResult = "";
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("ResponseArray")) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.ResponseNotFound")); }
		// Get the interface version
		webVersion = MDCheckWebService.getElementText(response, "Version");
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
				// Get the intermediate results
				MDCheckCVRequest.RBDIResults rbdiResults = request.rbdiResults = new MDCheckCVRequest.RBDIResults();
				// Get the address result element
				org.dom4j.Element address = record.element("Address");
				if (address == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.AddressNotFoundInElement")); }
				// dont add to result codes so it matches local obj
				// rbdiResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(address,
// "Results")));
				// Get the rbdiResult code and convert to indicator letter like local obj
				tmpResult = MDCheckWebService.getElementText(address, "Results");
				if (tmpResult.equals("DS01")) {
					tmpResult = "R";
				} else if (tmpResult.equals("DS02")) {
					tmpResult = "B";
				} else if (tmpResult.equals("DS03")) {
					tmpResult = "U";
				} else {
					tmpResult = "U";
				}
				rbdiResults.RDBIndicator = tmpResult;
				// TODO: More validation
				rbdiResults.valid = true;
			}
		}
		return "";
	}
	
	public String processWebResponse(JSONObject jsonResponse, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Not used 
		return null;
	}
	
	public boolean buildWebRequest(JSONObject jsonRequest, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Not used 
		return false;
	}
}
