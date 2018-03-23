package com.melissadata.kettle.personator.web;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.melissadata.kettle.personator.MDPersonator;
import com.melissadata.kettle.personator.MDPersonatorCVRequest;
import com.melissadata.kettle.personator.MDPersonatorCVRequest.PersonatorResults;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.MDPersonatorMeta.RowOutput;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.support.MetaVal;




public class PersonatorRequestHandler extends AbstractPersonatorWebRequestHandler {

	private PersonatorFields personatorFields;
	
	public String webMsg = "";
	public String webVersion = "";
	public KettleException webException;


	public PersonatorRequestHandler(PersonatorFields addrFields) {
		this.personatorFields = addrFields;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractAddressVerifyRequestHandler#addWebOptions(org.w3c.dom.Document, org.w3c.dom.Element)
	 */
	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {
		// Get te options list
		ConcurrentHashMap<String, MetaVal> optFields = personatorFields.getRealWebOptions();
		try {
			synchronized (optFields) {

				for (String key : optFields.keySet()) {
					PersonatorWebService.addTextNode(xmlDoc, root, optFields.get(key).webTag, optFields.get(key).metaValue);
				}
			}
		} catch (Exception e) {
			System.out.println("ADDING WEB OPTS ERROR " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractAddressVerifyRequestHandler#addWebRequestFields(org.w3c.dom.Document, com.melissadata.kettle.MDPersonatorRequest, org.w3c.dom.Element)
	 */
	@Override
	protected boolean addWebRequestFields(Document xmlDoc, MDPersonatorCVRequest request, Element record) throws KettleException {
		

		RowMetaInterface inputMeta = request.inputMeta;
		Object[] inputData = request.inputData;

		try {
			for (String key : personatorFields.inputFields.keySet()) {
				String value = MDPersonator.getFieldString(inputMeta, inputData, personatorFields.inputFields.get(key).metaValue);
				if (!Const.isEmpty(value)) {
					PersonatorWebService.addTextNode(xmlDoc, record, personatorFields.inputFields.get(key).webTag, value);
					// inputs++;
				}
			}
		} catch (Exception e) {
			System.out.println("ADDING INPUT ERROR " + e.getMessage());
			e.printStackTrace();
		}
		
		
		
		return (true);//inputs != 0
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractAddressVerifyRequestHandler#getInterfaceInfo(org.dom4j.Element)
	 */
	protected void getInterfaceInfo(org.dom4j.Element response) {
		// Get the interface version
		personatorFields.webVersion = PersonatorWebService.getElementText(response, "Version");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractAddressVerifyWebRequestHandler#processWebResponseFields(org.dom4j.Element, com.melissadata.kettle.MDPersonatorCVRequest)
	 */
	@Override
	protected void processWebResponseFields(org.dom4j.Element record, MDPersonatorCVRequest request) throws KettleException {
	
		
		PersonatorResults personatorResults = request.personatorResults = new PersonatorResults();

		// Result code for the individual request
		personatorResults.resultCodes.addAll(MDPersonator.getResultCodes(PersonatorWebService.getElementText(record, personatorFields.outputFields.get("output_results").webTag)));

		// Get the address result element
		org.dom4j.Element personatorRecord = record;
		if (personatorRecord == null)
			throw new KettleException("Could not find Personator element in response");
		
		// Extract the rest of the results
		HashMap<Integer, RowOutput> selectedOutput = MDPersonatorMeta.getOutputgroups();
		RowOutput ro;
		for(int n = 0; n < selectedOutput.size(); n++){
			ro = selectedOutput.get(n);
			if(ro.isAdded){
				personatorResults.outputFields.put(ro.outputName, PersonatorWebService.getElementText(personatorRecord, ro.outputName));
			}
		}
		
		// TODO: Do more complete validity checks
		personatorResults.valid = true;
	}

}
