package com.melissadata.kettle.globalverify.meta;

import java.util.List;



import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.data.AddressFields;

public class AddressMeta {

	private AddressFields addrFields;
	public String webVersion = "";

	public AddressMeta() {

		if (addrFields == null) {
			addrFields = new AddressFields();
			addrFields.init();
		}
	}

	public void loadXML(Node stepnode) throws KettleXMLException {

		if (addrFields == null) {
			addrFields = new AddressFields();
			addrFields.init();
		}
		List<Node> nodes = XMLHandler.getNodes(stepnode, AddressFields.TAG_GLOBAL_ADDRESS_WEB_OPTIONS);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : addrFields.webOptionFields.keySet()) {
				addrFields.webOptionFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), addrFields.webOptionFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(stepnode, AddressFields.TAG_GLOBAL_ADDRESS_INPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : addrFields.inputFields.keySet()) {
				addrFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), addrFields.inputFields.get(key).metaValue);
			}
			addrFields.defaultCountry = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, AddressFields.TAG_INPUT_DEFAULT_COUNTRY), addrFields.defaultCountry);
		}
		nodes = XMLHandler.getNodes(stepnode, AddressFields.TAG_GLOBAL_ADDRESS_WEB_OUTPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : addrFields.webOutputFields.keySet()) {
				addrFields.webOutputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), addrFields.webOutputFields.get(key).metaValue);
			}
		}

		Node nodx = XMLHandler.getSubNode(stepnode, AddressFields.TAG_GLOBAL_ADDRESS_OP_COUNTRIES);

		addrFields.setSelectedCountriesFromString(MDGlobalMeta.safe(nodx.getTextContent(), ""));

		nodx = XMLHandler.getSubNode(stepnode, AddressFields.TAG_GLOBAL_ADDRESS_COUNTRY_INDEX);
		if (nodx != null) {
			addrFields.setCountryFieldIndex(Integer.valueOf(MDGlobalMeta.safe(nodx.getTextContent(), "-1")));
		}

		nodx = XMLHandler.getSubNode(stepnode, AddressFields.TAG_GLOBAL_ADDRESS_PROCESS_OPTION);
		if (nodx != null) {
			addrFields.setProcessType(MDGlobalMeta.safe(nodx.getTextContent(), "WEB"));
		}

		nodx = XMLHandler.getSubNode(stepnode, AddressFields.TAG_GLOBAL_ADDRESS_ADD_CODES);
		if (nodx != null) {
			addrFields.addAdditionalCode = Boolean.valueOf(MDGlobalMeta.safe(nodx.getTextContent(), "true"));
		}
	}

	public void getXML(StringBuilder retval) throws KettleException {

		String tab = "      ";
		// Web Option fields
		retval.append(tab).append(XMLHandler.openTag(AddressFields.TAG_GLOBAL_ADDRESS_WEB_OPTIONS)).append(Const.CR);
		for (String key : addrFields.webOptionFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, addrFields.webOptionFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(AddressFields.TAG_GLOBAL_ADDRESS_WEB_OPTIONS)).append(Const.CR);
		// Internal Settings
		retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(AddressFields.TAG_GLOBAL_ADDRESS_PROCESS_OPTION, addrFields.getProcessType()));
		retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(AddressFields.TAG_GLOBAL_ADDRESS_ADD_CODES, String.valueOf(addrFields.addAdditionalCode)));

		retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(AddressFields.TAG_GLOBAL_ADDRESS_OP_COUNTRIES, addrFields.getSelectedCountriesAsString()));
		retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(AddressFields.TAG_GLOBAL_ADDRESS_COUNTRY_INDEX, String.valueOf(addrFields.getCountryFieldIndex())));

		// Input Fields
		retval.append(tab).append(XMLHandler.openTag(AddressFields.TAG_GLOBAL_ADDRESS_INPUT)).append(Const.CR);
		for (String key : addrFields.inputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, addrFields.inputFields.get(key).metaValue));
		}
		retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(AddressFields.TAG_INPUT_DEFAULT_COUNTRY, addrFields.defaultCountry));
		retval.append(tab).append(XMLHandler.closeTag(AddressFields.TAG_GLOBAL_ADDRESS_INPUT)).append(Const.CR);
		//Output Fields
		retval.append(tab).append(XMLHandler.openTag(AddressFields.TAG_GLOBAL_ADDRESS_WEB_OUTPUT)).append(Const.CR);
		for (String key : addrFields.webOutputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, addrFields.webOutputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(AddressFields.TAG_GLOBAL_ADDRESS_WEB_OUTPUT)).append(Const.CR);
	}

	public void readRep(Repository rep, ObjectId idStep) throws KettleException {
			if (addrFields == null) {
			addrFields = new AddressFields();
			addrFields.init();
		}
		for (String key : addrFields.webOptionFields.keySet()) {
			addrFields.webOptionFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_WEB_OPTIONS + "." + key), addrFields.webOptionFields.get(key).metaValue);
		}
		for (String key : addrFields.inputFields.keySet()) {
			addrFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_INPUT + "." + key), addrFields.inputFields.get(key).metaValue);
		}
		addrFields.defaultCountry = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_INPUT + "." + AddressFields.TAG_INPUT_DEFAULT_COUNTRY), addrFields.defaultCountry);

		for (String key : addrFields.webOutputFields.keySet()) {
			addrFields.webOutputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_WEB_OUTPUT + "." + key), addrFields.webOutputFields.get(key).metaValue);
		}

		addrFields.setCountryFieldIndex(Integer.valueOf(MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_COUNTRY_INDEX), "-1")));
		addrFields.setSelectedCountriesFromString(MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_OP_COUNTRIES), addrFields.getSelectedCountriesAsString()));
		addrFields.setProcessType(MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_PROCESS_OPTION), "WEB"));

		addrFields.addAdditionalCode = Boolean.valueOf(MDGlobalMeta.safe(rep.getStepAttributeString(idStep, AddressFields.TAG_GLOBAL_ADDRESS_ADD_CODES), "true"));
	}

	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		for (String key : addrFields.webOptionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_WEB_OPTIONS + "." + key, addrFields.webOptionFields.get(key).metaValue);
		}
		for (String key : addrFields.inputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_INPUT + "." + key, addrFields.inputFields.get(key).metaValue);
		}
		rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_INPUT + "." + AddressFields.TAG_INPUT_DEFAULT_COUNTRY, addrFields.defaultCountry);

		for (String key : addrFields.webOutputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_WEB_OUTPUT + "." + key, addrFields.webOutputFields.get(key).metaValue);
		}

		rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_COUNTRY_INDEX, String.valueOf(addrFields.getCountryFieldIndex()));
		rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_OP_COUNTRIES, addrFields.getSelectedCountriesAsString());
		rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_PROCESS_OPTION, addrFields.getProcessType());
		rep.saveStepAttribute(idTransformation, idStep, AddressFields.TAG_GLOBAL_ADDRESS_ADD_CODES, addrFields.addAdditionalCode);
	}

	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {

		for (String key : addrFields.webOutputFields.keySet()) {
			if (!Const.isEmpty(addrFields.webOutputFields.get(key).metaValue)) {
				MDGlobalMeta.getStringField(row, originName, space, addrFields.webOutputFields.get(key).metaValue, addrFields.webOutputFields.get(key).metaSize);
			}
		}
	}

	public boolean isLicensed() {

		return MDGlobalMeta.isLicensed();
	}

	public AddressFields getAddrFields() {

		return addrFields;
	}

	public void setAddrFields(AddressFields addrFields) {

		this.addrFields = addrFields;
	}
}
