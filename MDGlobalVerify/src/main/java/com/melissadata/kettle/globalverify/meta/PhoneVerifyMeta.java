package com.melissadata.kettle.globalverify.meta;

import java.util.Iterator;
import java.util.List;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;

import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.MDGlobalRequest;
import com.melissadata.kettle.globalverify.data.MDGlobalEnum.OutputPhoneFormat;
import com.melissadata.kettle.globalverify.data.NameFields;
import com.melissadata.kettle.globalverify.data.PhoneFields;
import com.melissadata.kettle.globalverify.request.PhoneRequest.PhoneResults;

public class PhoneVerifyMeta {
	// private static Class<?> PKG = PhoneVerifyMeta.class;
	private OutputPhoneFormat	optionFormat;
	// Info set during processing
	public String				localMsg		= "";
	public String				localDBDate		= "";
	public String				localDBBuildNo	= "";
	public String				webMsg			= "";
	public String				webVersion		= "";
	// Exceptions detected during processing
	public KettleException		localException;
	public KettleException		webException;
	public PhoneFields			phoneFields;

	public PhoneVerifyMeta() {
		if (phoneFields == null) {
			phoneFields = new PhoneFields();
			phoneFields.init();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PhoneVerifyMeta clone() throws CloneNotSupportedException {
		return (PhoneVerifyMeta) super.clone();
	}

	/**
	 * Called to read meta data from a document node
	 * 
	 * @param node
	 */
	public void loadXML(Node node) throws KettleXMLException {
		List<Node> nodes = XMLHandler.getNodes(node, PhoneFields.TAG_GLOBAL_PHONE_INPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : phoneFields.inputFields.keySet()) {
				phoneFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), phoneFields.inputFields.get(key).metaValue);
			}
		}
		
		nodes = XMLHandler.getNodes(node, PhoneFields.TAG_GLOBAL_PHONE_OUTPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : phoneFields.outputFields.keySet()) {
				phoneFields.outputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), phoneFields.outputFields.get(key).metaValue);
			}
		}
		
		nodes = XMLHandler.getNodes(node, PhoneFields.TAG_GLOBAL_PHONE_OPTION);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : phoneFields.optionFields.keySet()) {
				phoneFields.optionFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), phoneFields.optionFields.get(key).metaValue);
			}
		}
	}

	/**
	 * Returns the XML representation of the meta data
	 * 
	 * @param retval
	 * @return
	 */
	public void getXML(StringBuilder retval) {
		String tab = "      ";
		// Phone Inputs
		retval.append(tab).append(XMLHandler.openTag(PhoneFields.TAG_GLOBAL_PHONE_INPUT)).append(Const.CR);
		for (String key : phoneFields.inputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, phoneFields.inputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PhoneFields.TAG_GLOBAL_PHONE_INPUT)).append(Const.CR);
		
		// Phone Outputs
		retval.append(tab).append(XMLHandler.openTag(PhoneFields.TAG_GLOBAL_PHONE_OUTPUT)).append(Const.CR);
		for (String key : phoneFields.outputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, phoneFields.outputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PhoneFields.TAG_GLOBAL_PHONE_OUTPUT)).append(Const.CR);
		
		// Phone Options
		retval.append(tab).append(XMLHandler.openTag(PhoneFields.TAG_GLOBAL_PHONE_OPTION)).append(Const.CR);
		for (String key : phoneFields.optionFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, phoneFields.optionFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PhoneFields.TAG_GLOBAL_PHONE_OPTION)).append(Const.CR);
	}

	/**
	 * Called to retrieve data from a repository
	 * 
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep) throws KettleException {
		if (phoneFields == null) {
			phoneFields = new PhoneFields();
			phoneFields.init();
		}
		for (String key : phoneFields.inputFields.keySet()) {
			phoneFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, PhoneFields.TAG_GLOBAL_PHONE_INPUT + "." + key), phoneFields.inputFields.get(key).metaValue);
		}
		for (String key : phoneFields.outputFields.keySet()) {
			phoneFields.outputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, PhoneFields.TAG_GLOBAL_PHONE_OUTPUT + "." + key), phoneFields.outputFields.get(key).metaValue);
		}
		for (String key : phoneFields.optionFields.keySet()) {
			phoneFields.optionFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, PhoneFields.TAG_GLOBAL_PHONE_OPTION + "." + key), phoneFields.optionFields.get(key).metaValue);
		}
	}

	/**
	 * Called to store data in a repository
	 * 
	 * @param rep
	 * @param idTransformation
	 * @param idStep
	 * @throws KettleException
	 */
	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		for (String key : phoneFields.inputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, PhoneFields.TAG_GLOBAL_PHONE_INPUT + "." + key, phoneFields.inputFields.get(key).metaValue);
		}
		for (String key : phoneFields.outputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, PhoneFields.TAG_GLOBAL_PHONE_OUTPUT + "." + key, phoneFields.outputFields.get(key).metaValue);
		}
		
		for (String key : phoneFields.optionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, PhoneFields.TAG_GLOBAL_PHONE_OPTION + "." + key, phoneFields.optionFields.get(key).metaValue);
		}
	}

	/**
	 * Checks the settings of this step and puts the findings in a remarks List.
	 * 
	 * @param remarks
	 *            The list to put the remarks in @see org.pentaho.di.core.CheckResult
	 * @param stepMeta
	 *            The stepMeta to help checking
	 * @param prev
	 *            The fields coming from the previous step
	 * @param input
	 *            The input step names
	 * @param output
	 *            The output step names
	 * @param info
	 *            The fields that are used as information by the step
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// handled in validation
	}

	/**
	 * @return true if product is licensd
	 */
	public boolean isLicensed() {
		return MDGlobalMeta.isLicensed();
	}

	/**
	 * Called to determine the output fields that will be included in the step outout record
	 * 
	 * NOTE: Order of fields must match the order of fields in processResponses
	 * 
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		// Get fields defined by the data.
		for (String key : phoneFields.outputFields.keySet()) {
			if (!Const.isEmpty(phoneFields.outputFields.get(key).metaValue) && !key.equals(PhoneFields.TAG_OUTPUT_RESULTS)) {
				MDGlobalMeta.getStringField(row, originName, space, phoneFields.outputFields.get(key).metaValue, phoneFields.outputFields.get(key).metaSize);
			}
		}
	}

	public void outputData(MDGlobalData checkData, List<MDGlobalRequest> requests) {
		// Output each request's results
		for (Iterator<MDGlobalRequest> iterator = requests.iterator(); iterator.hasNext();) {
			MDGlobalRequest request = iterator.next();
			// Output the name results
			PhoneResults phoneResults = request.phoneRequest.phoneResults;
			if (phoneResults != null && phoneResults.valid) {
				for (String key : phoneFields.outputFields.keySet()) {
					if (key == PhoneFields.TAG_OUTPUT_PHONE && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_PHONE).metaValue))
						request.addOutputData(phoneResults.phoneNumber);
					if (key == PhoneFields.TAG_OUTPUT_SUBSCRIBER && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUBSCRIBER).metaValue))
						request.addOutputData(phoneResults.subscriber);
					if (key == PhoneFields.TAG_OUTPUT_CARRIER && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_CARRIER).metaValue))
						request.addOutputData(phoneResults.carrier);
					if (key == PhoneFields.TAG_OUTPUT_CALLER_ID && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_CALLER_ID).metaValue))
						request.addOutputData(phoneResults.callerID);
					if (key == PhoneFields.TAG_OUTPUT_COUNTRY && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY).metaValue))
						request.addOutputData(phoneResults.country);
					if (key == PhoneFields.TAG_OUTPUT_COUNTRY_ABBREVIATION && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_ABBREVIATION).metaValue))
						request.addOutputData(phoneResults.countryAbbriviation);	
					if (key == PhoneFields.TAG_OUTPUT_COUNTRY_CODE && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_CODE).metaValue))
						request.addOutputData(phoneResults.dialingCode);
					if (key == PhoneFields.TAG_OUTPUT_INTERNATIONAL_PREFIX && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_PREFIX).metaValue))
						request.addOutputData(phoneResults.internationalPrefix);
					if (key == PhoneFields.TAG_OUTPUT_NATIONAL_PREFIX && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_NATIONAL_PREFIX).metaValue))
						request.addOutputData(phoneResults.nationalPrefix);
					if (key == PhoneFields.TAG_OUTPUT_DEST_CODE && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_DEST_CODE).metaValue))
						request.addOutputData(phoneResults.destCode);
					if (key == PhoneFields.TAG_OUTPUT_LOCALITY && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LOCALITY).metaValue))
						request.addOutputData(phoneResults.locality);
					if (key == PhoneFields.TAG_OUTPUT_ADMIN_AREA && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_ADMIN_AREA).metaValue))
						request.addOutputData(phoneResults.AdminArea);
					if (key == PhoneFields.TAG_OUTPUT_LANGUAGE && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LANGUAGE).metaValue))
						request.addOutputData(phoneResults.language);
					if (key == PhoneFields.TAG_OUTPUT_UTC && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_UTC).metaValue))
						request.addOutputData(phoneResults.utc);
					if (key == PhoneFields.TAG_OUTPUT_DST && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_DST).metaValue))
						request.addOutputData(phoneResults.dst);
					if (key == PhoneFields.TAG_OUTPUT_LATITUDE && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LATITUDE).metaValue))
						request.addOutputData(phoneResults.latitude);
					if (key == PhoneFields.TAG_OUTPUT_LONGITUDE && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_LONGITUDE).metaValue))
						request.addOutputData(phoneResults.longitude);
					
					if (key == PhoneFields.TAG_OUTPUT_INTERNATIONAL_NUMBER && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_NUMBER).metaValue))
						request.addOutputData(phoneResults.internationalNumber);
					if (key == PhoneFields.TAG_OUTPUT_POSTAL_CODE && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_POSTAL_CODE).metaValue))
						request.addOutputData(phoneResults.postalCode);
					
					if (key == PhoneFields.TAG_OUTPUT_SUGGESTIONS && !Const.isEmpty(phoneFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUGGESTIONS).metaValue))
						request.addOutputData(phoneResults.Suggestions);
				}
			}
		}
	}

	/**
	 * Called to update reporting stats for a given result
	 * 
	 * @param checkData
	 * @param checkData
	 */
	@SuppressWarnings("unused")
	private synchronized void updateStats(/* PhoneResults phoneResults, */MDGlobalData checkData) {
		/*
		 * // De-reference results and meta data Set<String> resultCodes = phoneResults.resultCodes; ReportStats
		 * numPhoneOverview = checkData.numPhoneOverview; MDCheckMeta checkMeta = data.getMeta(); // Test for specific result
		 * codes boolean
		 * tenDigitMatch = resultCodes.contains("PS01"); boolean sevenDigitMatch = resultCodes.contains("PS02"); boolean
		 * correctedAreaCode = resultCodes.contains("PS03"); boolean updatedAreaCode = resultCodes.contains("PS06"); boolean
		 * blankPhone =
		 * resultCodes.contains("PE02"); // Track matches ... if (tenDigitMatch || sevenDigitMatch) { // ... that did not
		 * require a change to the area code if (!(correctedAreaCode || updatedAreaCode))
		 * numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[0]); } // Track non-matches (ignore blanks) else if
		 * (!blankPhone) { numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[3]); checkData.numPhoneErrors += 1; } //
		 * Track changes to
		 * the area code if (correctedAreaCode || updatedAreaCode)
		 * numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[1]); // Track blank phone numbers if (blankPhone)
		 * numPhoneOverview.inc(checkMeta.phoneOverviewReportCStat[2]); // Add result
		 * codes to reporting result stats for (String resultCode : resultCodes) checkData.resultStats.inc(resultCode);
		 */
	}

	/**
	 * This will format a phone number according the specified format option
	 * 
	 * @param npa
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	@SuppressWarnings("unused")
	private String formatPhone(String npa, String prefix, String suffix) {
		String formatedPhone = "";
		boolean noNPA = Const.isEmpty(npa);
		/*
		 * switch (optionFormat) { case FORMAT1: if(noNPA){ formatedPhone = prefix + "-" + suffix; }else formatedPhone = "("
		 * + npa + ") " + prefix + "-" + suffix; break; case FORMAT2: if(noNPA){ formatedPhone = prefix + " " + suffix; }else
		 * formatedPhone = "(" + npa + ") " + prefix + " " + suffix; break; case FORMAT3: if(noNPA){ formatedPhone = prefix +
		 * "-" + suffix; }else formatedPhone = npa + "-" + prefix + "-" + suffix; break; case FORMAT4: if(noNPA){
		 * formatedPhone =
		 * prefix + " " + suffix; }else formatedPhone = npa + " " + prefix + " " + suffix; break; case FORMAT5: if(noNPA){
		 * formatedPhone = prefix + "." + suffix; }else formatedPhone = npa + "." + prefix + "." + suffix; break; case
		 * FORMAT6: if(noNPA){
		 * formatedPhone = prefix + suffix; }else formatedPhone = npa + prefix + suffix; break; }
		 */
		return formatedPhone;
	}

	public OutputPhoneFormat getOptionFormat() {
		return optionFormat;
	}

	public void setOptionFormat(OutputPhoneFormat optionFormat) {
		this.optionFormat = optionFormat;
	}

	public PhoneFields getPhoneFields() {
		return phoneFields;
	}

	public void setPhoneFields(PhoneFields phoneFields) {
		this.phoneFields = phoneFields;
	}
}
