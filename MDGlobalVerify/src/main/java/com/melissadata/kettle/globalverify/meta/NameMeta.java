package com.melissadata.kettle.globalverify.meta;

import com.melissadata.kettle.globalverify.MDGlobalData;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.MDGlobalRequest;
import com.melissadata.kettle.globalverify.data.NameFields;
import com.melissadata.kettle.globalverify.request.NameRequest.NameResults;
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

import java.util.Iterator;
import java.util.List;



public class NameMeta {

	// Info set during processing
	public String localMsg          = "";
	public String localDBDate       = "";
	public String localDBExpiration = "";
	public String localDBBuildNo    = "";
	public String webMsg            = "";
	public String webVersion        = "";
	// Exceptions detected during processing
	public KettleException localException;
	public KettleException webException;
	private NameFields nameFields;

	public NameMeta() {

		if (nameFields == null) {
			nameFields = new NameFields();
			nameFields.init();
		}
	}

	/**
	 * Called to read name parsing meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void loadXML(Node node) throws KettleXMLException {

		List<Node> nodes = XMLHandler.getNodes(node, NameFields.TAG_GLOBAL_NAME_OPTIONS);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : nameFields.optionFields.keySet()) {
				nameFields.optionFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), nameFields.optionFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(node, NameFields.TAG_GLOBAL_NAME_INPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : nameFields.inputFields.keySet()) {
				nameFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), nameFields.inputFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(node, NameFields.TAG_GLOBAL_NAME_OUTPUT);
		if (nodes != null && nodes.size() > 0) {
			Node tempNode = nodes.get(0);
			for (String key : nameFields.outputFields.keySet()) {
				nameFields.outputFields.get(key).metaValue = MDGlobalMeta.safe(MDGlobalMeta.getTagValue(tempNode, key), nameFields.outputFields.get(key).metaValue);
			}
		}
	}

	public void getXML(StringBuilder retval) {
		// StringBuilder retval = new StringBuilder(200);
		String tab = "      ";
		// Name Options
		retval.append(tab).append(XMLHandler.openTag(NameFields.TAG_GLOBAL_NAME_OPTIONS)).append(Const.CR);
		for (String key : nameFields.optionFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, nameFields.optionFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(NameFields.TAG_GLOBAL_NAME_OPTIONS)).append(Const.CR);
		// Name Inputs
		retval.append(tab).append(XMLHandler.openTag(NameFields.TAG_GLOBAL_NAME_INPUT)).append(Const.CR);
		for (String key : nameFields.inputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, nameFields.inputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(NameFields.TAG_GLOBAL_NAME_INPUT)).append(Const.CR);
		// Name Outputs
		retval.append(tab).append(XMLHandler.openTag(NameFields.TAG_GLOBAL_NAME_OUTPUT)).append(Const.CR);
		for (String key : nameFields.outputFields.keySet()) {
			retval.append(tab).append("  ").append(MDGlobalMeta.addTagValue(key, nameFields.outputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(NameFields.TAG_GLOBAL_NAME_OUTPUT)).append(Const.CR);
	}

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep) throws KettleException {

		if (nameFields == null) {
			nameFields = new NameFields();
			nameFields.init();
		}
		for (String key : nameFields.optionFields.keySet()) {
			nameFields.optionFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, NameFields.TAG_GLOBAL_NAME_OPTIONS + "." + key), nameFields.optionFields.get(key).metaValue);
		}
		for (String key : nameFields.inputFields.keySet()) {
			nameFields.inputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, NameFields.TAG_GLOBAL_NAME_INPUT + "." + key), nameFields.inputFields.get(key).metaValue);
		}
		for (String key : nameFields.outputFields.keySet()) {
			nameFields.outputFields.get(key).metaValue = MDGlobalMeta.safe(rep.getStepAttributeString(idStep, NameFields.TAG_GLOBAL_NAME_OUTPUT + "." + key), nameFields.outputFields.get(key).metaValue);
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

		for (String key : nameFields.optionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, NameFields.TAG_GLOBAL_NAME_OPTIONS + "." + key, nameFields.optionFields.get(key).metaValue);
		}
		for (String key : nameFields.inputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, NameFields.TAG_GLOBAL_NAME_INPUT + "." + key, nameFields.inputFields.get(key).metaValue);
		}
		for (String key : nameFields.outputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, NameFields.TAG_GLOBAL_NAME_OUTPUT + "." + key, nameFields.outputFields.get(key).metaValue);
		}
	}

	/**
	 * Checks the settings of this step and puts the findings in a remarks List.
	 *
	 * @param remarks  The list to put the remarks in @see org.pentaho.di.core.CheckResult
	 * @param stepMeta The stepMeta to help checking
	 * @param prev     The fields coming from the previous step
	 * @param input    The input step names
	 * @param output   The output step names
	 * @param info     The fields that are used as information by the step
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// Do something here?
	}

	/**
	 * @return true if name parsing is licensed for this customer
	 */
	public boolean isLicensed() {

		return MDGlobalMeta.isLicensed();
	}

	/**
	 * Called to determine the name parser output fields that will be included in the step outout record
	 *
	 * NOTE: Order of fields must match the order of fields in getResponseValues
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		// Get fields defined by the data.
		for (String key : nameFields.outputFields.keySet()) {
			if (!Const.isEmpty(nameFields.inputFields.get(NameFields.TAG_INPUT_FULLNAME).metaValue)) {
				// If we have a Name input we get all fields otherwise just Company output
				if (!Const.isEmpty(nameFields.outputFields.get(key).metaValue) && !key.equals(NameFields.TAG_OUTPUT_RESULTS) && !key.equals(NameFields.TAG_OUTPUT_COMPANY_NAME)) {
					MDGlobalMeta.getStringField(row, originName, space, nameFields.outputFields.get(key).metaValue, nameFields.outputFields.get(key).metaSize);
				}
			}
			if (!Const.isEmpty(nameFields.inputFields.get(NameFields.TAG_INPUT_COMPANY_NAME).metaValue)) {
				if (key.equals(NameFields.TAG_OUTPUT_COMPANY_NAME)) {
					MDGlobalMeta.getStringField(row, originName, space, nameFields.outputFields.get(key).metaValue, nameFields.outputFields.get(key).metaSize);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private synchronized void updateStats(NameResults nameResults, MDGlobalMeta checkData) {
		// Add result codes to reporting result stats
		// for (String resultCode : nameResults.resultCodes)
		// checkData.resultStats.inc(resultCode);
	}

	public NameFields getNameFields() {

		return nameFields;
	}

	public void setNameFields(NameFields nameFields) {

		this.nameFields = nameFields;
	}

	public void outputData(MDGlobalData checkData, List<MDGlobalRequest> requests) {
		// Output each request's results
		for (Iterator<MDGlobalRequest> iterator = requests.iterator(); iterator.hasNext(); ) {
			MDGlobalRequest request = iterator.next();
			// Output the name results
			NameResults nameResults = request.nameRequest.nameResults;
			if (nameResults != null && nameResults.valid) {
				for (String key : nameFields.outputFields.keySet()) {
					if (nameFields.doFullName()) {

						if (key == NameFields.TAG_OUTPUT_NAME1_PREFIX && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_PREFIX).metaValue)) {
							request.addOutputData(nameResults.prefix1);
						} else if (key == NameFields.TAG_OUTPUT_NAME1_FIRST && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_FIRST).metaValue)) {
							request.addOutputData(nameResults.first1);
						} else if (key == NameFields.TAG_OUTPUT_NAME1_MIDDLE && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_MIDDLE).metaValue)) {
							request.addOutputData(nameResults.middle1);
						} else if (key == NameFields.TAG_OUTPUT_NAME1_LAST && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_LAST).metaValue)) {
							request.addOutputData(nameResults.last1);
						} else if (key == NameFields.TAG_OUTPUT_NAME1_SUFFIX && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_SUFFIX).metaValue)) {
							request.addOutputData(nameResults.suffix1);
						} else if (key == NameFields.TAG_OUTPUT_NAME1_GENDER && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME1_GENDER).metaValue)) {
							request.addOutputData(nameResults.gender1);
						} else if (key == NameFields.TAG_OUTPUT_NAME2_PREFIX && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_PREFIX).metaValue)) {
							request.addOutputData(nameResults.prefix2);
						} else if (key == NameFields.TAG_OUTPUT_NAME2_FIRST && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_FIRST).metaValue)) {
							request.addOutputData(nameResults.first2);
						} else if (key == NameFields.TAG_OUTPUT_NAME2_MIDDLE && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_MIDDLE).metaValue)) {
							request.addOutputData(nameResults.middle2);
						} else if (key == NameFields.TAG_OUTPUT_NAME2_LAST && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_LAST).metaValue)) {
							request.addOutputData(nameResults.last2);
						} else if (key == NameFields.TAG_OUTPUT_NAME2_SUFFIX && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_SUFFIX).metaValue)) {
							request.addOutputData(nameResults.suffix2);
						} else if (key == NameFields.TAG_OUTPUT_NAME2_GENDER && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_NAME2_GENDER).metaValue)) {
							request.addOutputData(nameResults.gender2);
						}
					}
					if (nameFields.doCompany()) {
						if (key == NameFields.TAG_OUTPUT_COMPANY_NAME && !Const.isEmpty(nameFields.outputFields.get(NameFields.TAG_OUTPUT_COMPANY_NAME).metaValue)) {
							request.addOutputData(nameResults.companyName);
						}
					}

					request.resultCodes.addAll(nameResults.resultCodes);
				}
			}
		}
	}
}
