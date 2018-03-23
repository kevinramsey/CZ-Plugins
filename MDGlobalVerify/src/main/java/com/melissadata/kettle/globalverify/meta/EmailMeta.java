package com.melissadata.kettle.globalverify.meta;

import java.util.Iterator;
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

import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.MDGlobalRequest;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.data.EmailFields;
import com.melissadata.kettle.globalverify.request.EmailRequest.EmailResults;

public class EmailMeta {
	// Info set during processing
	public String			localMsg		= "";
	public String			localDBDate		= "";
	public String			localDBBuildNo	= "";
	public String			webMsg			= "";
	public String			webVersion		= "";
	// Exceptions detected during processing
	public KettleException	localException;
	public KettleException	webException;
	public EmailFields		emailFields;

	public EmailMeta() {
		if (emailFields == null) {
			emailFields = new EmailFields();
			emailFields.init();
		}
	}

	public void readRep(Repository rep, ObjectId idStep) throws KettleException {
		if (emailFields == null) {
			emailFields = new EmailFields();
			emailFields.init();
		}
		for (String key : emailFields.optionFields.keySet()) {
			emailFields.optionFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, EmailFields.TAG_GLOBAL_EMAIL_OPTIONS + "." + key), emailFields.optionFields.get(key).metaValue);
		}
		for (String key : emailFields.inputFields.keySet()) {
			emailFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, EmailFields.TAG_GLOBAL_EMAIL_INPUT + "." + key), emailFields.inputFields.get(key).metaValue);
		}
		for (String key : emailFields.outputFields.keySet()) {
			emailFields.outputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, EmailFields.TAG_GLOBAL_EMAIL_OUTPUT + "." + key), emailFields.outputFields.get(key).metaValue);
		}
	}

	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		for (String key : emailFields.optionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, EmailFields.TAG_GLOBAL_EMAIL_OPTIONS + "." + key, emailFields.optionFields.get(key).metaValue);
		}
		for (String key : emailFields.inputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, EmailFields.TAG_GLOBAL_EMAIL_INPUT + "." + key, emailFields.inputFields.get(key).metaValue);
		}
		for (String key : emailFields.outputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, EmailFields.TAG_GLOBAL_EMAIL_OUTPUT + "." + key, emailFields.outputFields.get(key).metaValue);
		}
	}

	public void loadXML(Node node) throws KettleXMLException {
		List<Node> nodes = XMLHandler.getNodes(node, EmailFields.TAG_GLOBAL_EMAIL_OPTIONS);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : emailFields.optionFields.keySet()) {
				emailFields.optionFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), emailFields.optionFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(node, EmailFields.TAG_GLOBAL_EMAIL_INPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : emailFields.inputFields.keySet()) {
				emailFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), emailFields.inputFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(node, EmailFields.TAG_GLOBAL_EMAIL_OUTPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : emailFields.outputFields.keySet()) {
				emailFields.outputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), emailFields.outputFields.get(key).metaValue);
			}
		}
	}

	public String getXML(StringBuilder retval) {
		String tab = "      ";
		// Name Options
		retval.append(tab).append(XMLHandler.openTag(EmailFields.TAG_GLOBAL_EMAIL_OPTIONS)).append(Const.CR);
		for (String key : emailFields.optionFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, emailFields.optionFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(EmailFields.TAG_GLOBAL_EMAIL_OPTIONS)).append(Const.CR);
		// Name Inputs
		retval.append(tab).append(XMLHandler.openTag(EmailFields.TAG_GLOBAL_EMAIL_INPUT)).append(Const.CR);
		for (String key : emailFields.inputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, emailFields.inputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(EmailFields.TAG_GLOBAL_EMAIL_INPUT)).append(Const.CR);
		// Name Outputs
		retval.append(tab).append(XMLHandler.openTag(EmailFields.TAG_GLOBAL_EMAIL_OUTPUT)).append(Const.CR);
		for (String key : emailFields.outputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, emailFields.outputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(EmailFields.TAG_GLOBAL_EMAIL_OUTPUT)).append(Const.CR);
		return "";
	}

	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		// Get fields defined by the data.
		for (String key : emailFields.outputFields.keySet()) {
			if (!Const.isEmpty(emailFields.outputFields.get(key).metaValue)/* && !key.equals(emailFields.TAG_OUTPUT_RESULTS) */) {
				MDGlobalMeta.getStringField(row, originName, space, emailFields.outputFields.get(key).metaValue, emailFields.outputFields.get(key).metaSize);
			}
		}
	}

	public EmailFields getEmailFields() {
		return emailFields;
	}

	public void setEmailFields(EmailFields emailFields) {
		this.emailFields = emailFields;
	}

	public void outputData(MDGlobalData checkData, List<MDGlobalRequest> requests) {
		// Output each request's results
		for (Iterator<MDGlobalRequest> iterator = requests.iterator(); iterator.hasNext();) {
			MDGlobalRequest request = iterator.next();
			// Output the name results
			EmailResults emailResults = request.emailRequest.emailResults;
			if (emailResults != null && emailResults.valid) {
				for (String key : emailFields.outputFields.keySet()) {
					if (key == EmailFields.TAG_OUTPUT_EMAIL && !Const.isEmpty(emailFields.outputFields.get(EmailFields.TAG_OUTPUT_EMAIL).metaValue))
						request.addOutputData(emailResults.email);
					if (key == EmailFields.TAG_OUTPUT_BOX_NAME && !Const.isEmpty(emailFields.outputFields.get(EmailFields.TAG_OUTPUT_BOX_NAME).metaValue))
						request.addOutputData(emailResults.boxName);
					if (key == EmailFields.TAG_OUTPUT_DOMAIN && !Const.isEmpty(emailFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN).metaValue))
						request.addOutputData(emailResults.domain);
					if (key == EmailFields.TAG_OUTPUT_TOP_LEVEL_DOMAIN && !Const.isEmpty(emailFields.outputFields.get(EmailFields.TAG_OUTPUT_TOP_LEVEL_DOMAIN).metaValue))
						request.addOutputData(emailResults.topDomain);
					if (key == EmailFields.TAG_OUTPUT_DOMAIN_DESCRIPTION && !Const.isEmpty(emailFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN_DESCRIPTION).metaValue))
						request.addOutputData(emailResults.topDomainDescription);
					if (key == EmailFields.TAG_OUTPUT_DATE_CHECKED && !Const.isEmpty(emailFields.outputFields.get(EmailFields.TAG_OUTPUT_DATE_CHECKED).metaValue))
						request.addOutputData(emailResults.dateChecked);
				}
			}
		}
	}

	public boolean isLicensed() {
		return MDGlobalMeta.isLicensed();
	}
}
