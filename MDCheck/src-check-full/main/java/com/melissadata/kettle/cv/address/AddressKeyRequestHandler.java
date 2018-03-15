package com.melissadata.kettle.cv.address;

import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddressKeyRequestHandler extends AbstractAddressVerifyWebRequestHandler {
	private GeoCoderMeta gcMeta;

	public AddressKeyRequestHandler(GeoCoderMeta gcMeta) {
		this.gcMeta = gcMeta;
	}

	public String getServiceName() {
		return "Address Key";
	}

	/*
	 * (non-Javadoc)
	 * @see AbstractAddressVerifyWebRequestHandler#addWebOptions(org.w3c.dom.Document,
	 * org.w3c.dom.Element)
	 */
	@Override
	protected void addWebOptions(Document xmlDoc, Element root) throws DOMException {
		// nothing to do here
	}

	/*
	 * (non-Javadoc)
	 * @see AbstractAddressVerifyWebRequestHandler#addWebRequestFields(org.w3c.dom.Document,
	 * MDCheckCVRequest, org.w3c.dom.Element)
	 */
	@Override
	protected boolean addWebRequestFields(Document xmlDoc, MDCheckCVRequest request, Element record) throws KettleException {
		RowMetaInterface inputMeta = request.inputMeta;
		Object[] inputData = request.inputData;
		// Add the component fields for resolving the address key
		String value = MDCheck.getFieldString(inputMeta, inputData, gcMeta.getAddrCompLine1());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressLine1", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, gcMeta.getAddrCompLine2());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "AddressLine2", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, gcMeta.getAddrCompCity());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "City", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, gcMeta.getAddrCompState());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "State", value);
		}
		value = MDCheck.getFieldString(inputMeta, inputData, gcMeta.getAddrCompZip());
		if (!Const.isEmpty(value)) {
			MDCheckWebService.addTextNode(xmlDoc, record, "Zip", value);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void getInterfaceInfo(org.dom4j.Element response) {
		// Nothing to do here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processWebResponseFields(org.dom4j.Element record, MDCheckCVRequest request) throws KettleException {
		MDCheckCVRequest.AddrKeyResults akResults = request.addrKeyResults = new MDCheckCVRequest.AddrKeyResults();
		// Result code for the individual request
		akResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(record, "Results")));
		// Get the address result element
		org.dom4j.Element address = record.element("Address");
		if (address == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.AddressNotFoundInElement")); }
		// Extract the rest of the results
		akResults.AddressKey = MDCheckWebService.getElementText(address, "AddressKey");
		// TODO: Do more complete validity checks
		akResults.valid = true;
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
