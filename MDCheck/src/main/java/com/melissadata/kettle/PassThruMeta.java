package com.melissadata.kettle;

import java.util.ArrayList;
import java.util.List;

import com.melissadata.kettle.support.IOMeta;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;
import com.melissadata.kettle.mu.evaluator.EvalItem.eDataType;
import com.melissadata.kettle.mu.evaluator.SurvivorField;
import com.melissadata.kettle.mu.evaluator.SurvivorField.ConsolidationMethod;

public class PassThruMeta implements Cloneable {

	private static       Class<?>            PKG                         = MDCheckMeta.class;
	private static final String              TAG_PASS_THRU               = "pass_thru";
	private static final String              TAG_SURVIVOR_PASS_THRU      = "suvivor_pass_thru";
	private static final String              TAG_FILTER_OUT              = "filter_out";
	public static final  String              TAG_SURVIVORSHIP            = "survivorship";
	public static final  String              TAG_SURVIVOR_LIST           = "survivor_list";
	public static final  String              TAG_SURVIVOR_FIELD          = "survivor_field";
	public static final  String              TAG_SURVIVOR_OUTPUT_NAME    = "survivor_output_name";
	public static final  String              TAG_SURVIVOR_CONSOLIDATION  = "survivor_consolidation";
	public static final  String              TAG_SURVIVOR_SOURCE         = "survivor_source";
	public static final  String              TAG_SURVIVOR_PRIORITIZATION = "survivor_prioritization";
	public static final  String              TAG_SURVIVOR_DECENDING      = "survivor_sort_descending";
	public static final  String              TAG_SURVIVOR_DATATYPE       = "survivor_datatype";
	public static final  String              TAG_SURVIVOR_STACK_IDX      = "survivor_stack_idx";
	private              String              categoryTag                 = null;
	private              boolean             isSurvivorPass              = false;
	private              List<String>        filterOut                   = new ArrayList<String>();
	private              List<String>        passThru                    = new ArrayList<String>();
	private              List<SurvivorField> survivorPassThru            = new ArrayList<SurvivorField>();

	public PassThruMeta(String categoryTag) {

		this.categoryTag = categoryTag;
		setDefault();
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

		CheckResult cr;
		if ((prev != null) && (prev.size() > 0)) {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.StepReceivingFields", prev.size() + ""), stepMeta); //$NON-NLS-1$ //$NON-NLS-2$
			remarks.add(cr);
			// Search for missing fields
			String  error_message = ""; //$NON-NLS-1$
			boolean error_found   = false;
			// Search for filterOut fields in input stream
			for (String field : filterOut) {
				int idx = prev.indexOfValue(field);
				if (idx < 0) {
					error_message += "\t\t" + field + Const.CR; //$NON-NLS-1$
					error_found = true;
				}
			}
			if (error_found) {
				error_message = BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.FilterOutFieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepMeta);
				remarks.add(cr);
			} else {
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.AllFilterOutFieldsFound"), stepMeta); //$NON-NLS-1$
				remarks.add(cr);
			}
			if (filterOut.size() > 0) {
				// Starting from prev...
				for (int i = 0; i < prev.size(); i++) {
					ValueMetaInterface pv = prev.getValueMeta(i);
					if (!filterOut.contains(pv.getName())) {
						error_message += "\t\t" + pv.getName() + " (" + pv.getTypeDesc() + ")" + Const.CR; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						error_found = true;
					}
				}
				if (error_found) {
					error_message = BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.FieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$
					cr = new CheckResult(CheckResultInterface.TYPE_RESULT_COMMENT, error_message, stepMeta);
					remarks.add(cr);
				}
			}
			// Search for passThru fields in input stream
			error_message = ""; //$NON-NLS-1$
			error_found = false;
			for (String field : passThru) {
				int idx = prev.indexOfValue(field);
				if (idx < 0) {
					error_message += "\t\t" + field + Const.CR; //$NON-NLS-1$
					error_found = true;
				}
			}
			if (error_found) {
				error_message = BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.PassThruFieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepMeta);
				remarks.add(cr);
			} else {
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.AllFilterOutFieldsFound"), stepMeta); //$NON-NLS-1$
				remarks.add(cr);
			}
			if (passThru.size() > 0) {
				// Starting from prev...
				for (int i = 0; i < prev.size(); i++) {
					ValueMetaInterface pv = prev.getValueMeta(i);
					if (!passThru.contains(pv.getName())) {
						error_message += "\t\t" + pv.getName() + " (" + pv.getTypeDesc() + ")" + Const.CR; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						error_found = true;
					}
				}
				if (error_found) {
					error_message = BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.FieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$
					cr = new CheckResult(CheckResultInterface.TYPE_RESULT_COMMENT, error_message, stepMeta);
					remarks.add(cr);
				}
			}
		} else {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "MDCheckMeta.CheckResult.FieldsNotFound2"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		}
	}

	/**
	 * Called to determine the output fields that will be included in the step outout record.
	 * <p>
	 * NOTE: Order of fields must match the order of fields in processResponses
	 *
	 * @param inputRowMeta
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface inputRowMeta, String originName, VariableSpace space, boolean lookup) {
		// On entry the row should have all the input streams meta data
		// Start with an empty row
		RowMetaInterface row = new RowMeta();
		// Add pass thru fields
		if (!isSurvivorPass) {
			if ((passThru != null) && (passThru.size() > 0)) {
				for (String field : passThru) {
					ValueMetaInterface v;
					// Look for that pass thru field in the input row meta
					if (!lookup) {
						v = inputRowMeta.searchValueMeta(field);
					} else {
						// We don't have reference for lookup so we create new value
						v = new ValueMeta(space.environmentSubstitute(field), ValueMetaInterface.TYPE_STRING);
						v.setOrigin(originName);
					}
					if (v != null) {
						// We found a value. Clone it and add to the result row
						v = v.clone();
						row.addValueMeta(v);
					}
				}
			}
		} else {
			if ((survivorPassThru != null) && (survivorPassThru.size() > 0)) {
				for (SurvivorField survivorField : survivorPassThru) {
					ValueMetaInterface v;
					// Look for that pass thru field in the input row meta
					if (!lookup) {
						v = inputRowMeta.searchValueMeta(survivorField.getOutputName());
						// if null it must be a survivor added field
						// We don't have reference so we create new value
						if (v == null) {
							v = new ValueMeta(space.environmentSubstitute(survivorField.getOutputName()), ValueMetaInterface.TYPE_STRING);
							v.setOrigin(originName);
						}
					} else {
						// We don't have reference for lookup so we create new value
						v = new ValueMeta(space.environmentSubstitute(survivorField.getOutputName()), ValueMetaInterface.TYPE_STRING);
						v.setOrigin(originName);
					}
					if (v != null) {
						// We found a value. Clone it and add to the result row
						v = v.clone();
						row.addValueMeta(v);
					}
				}
			}
		}
		// Remove all from input meta and re-add filtered row
		inputRowMeta.clear();
		inputRowMeta.addRowMeta(row);
	}

	public List<String> getFilterOut() {

		return filterOut;
	}

	public List<String> getPassThru() {

		return passThru;
	}

	public List<SurvivorField> getSurvivorPassThru() {

		return survivorPassThru;
	}

	/**
	 * Returns the XML representation of the meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {

		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(categoryTag)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_FILTER_OUT, toString(filterOut)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_PASS_THRU, toString(passThru)));
		retval.append(tab).append("  ").append(XMLHandler.openTag(TAG_SURVIVOR_LIST));
		for (SurvivorField svrRecord : survivorPassThru) {
			retval.append(tab).append("    ").append(XMLHandler.openTag(TAG_SURVIVOR_FIELD));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_CONSOLIDATION, svrRecord.getConsolidationMethod().encode()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_OUTPUT_NAME, svrRecord.getOutputName()));
			if (svrRecord.getDisplaySource().contains("Stack Group")) {
				retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_SOURCE, svrRecord.getDisplaySource() + ":" + svrRecord.getSource()));
				retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_PRIORITIZATION, svrRecord.getDisplayPrioritization() + ":" + svrRecord.getPrioritization()));
			} else {
				retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_SOURCE, svrRecord.getSource()));
				retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_PRIORITIZATION, svrRecord.getPrioritization()));
			}
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_DECENDING, String.valueOf(svrRecord.isDescending())));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_DATATYPE, svrRecord.getDataType().name()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_SURVIVOR_STACK_IDX, String.valueOf(svrRecord.getStackIndex())));
			retval.append(tab).append("    ").append(XMLHandler.closeTag(TAG_SURVIVOR_FIELD));
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(TAG_SURVIVOR_LIST));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SURVIVORSHIP, Boolean.toString(isSurvivorPass)));
		retval.append(tab).append(XMLHandler.closeTag(categoryTag)).append(Const.CR);
		return retval.toString();
	}

	public boolean isSurvivorPass() {

		return isSurvivorPass;
	}

	/**
	 * Called to read meta data from a document node
	 *
	 * @param node
	 */
	public void readData(Node node) {

		List<Node> nodes = XMLHandler.getNodes(node, categoryTag);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Get the list of fields to filter out and pass thru
			filterOut = toList(MDCheckStepData.getTagValue(node, TAG_FILTER_OUT));
			passThru = toList(MDCheckStepData.getTagValue(node, TAG_PASS_THRU));
			Node srNodes    = XMLHandler.getSubNode(node, TAG_SURVIVOR_LIST);
			int  nrSrscores = XMLHandler.countNodes(srNodes, TAG_SURVIVOR_FIELD);
			survivorPassThru = new ArrayList<SurvivorField>();
			for (int i = 0; i < nrSrscores; i++) {
				Node                sourceNode = XMLHandler.getSubNodeByNr(srNodes, TAG_SURVIVOR_FIELD, i);
				ConsolidationMethod csMethod;
				try {
					csMethod = ConsolidationMethod.decode(MDCheckStepData.getTagValue(sourceNode, TAG_SURVIVOR_CONSOLIDATION));
				} catch (KettleException e) {
					csMethod = ConsolidationMethod.FirstData;
				}
				String    outputName     = MDCheckStepData.getTagValue(sourceNode, TAG_SURVIVOR_OUTPUT_NAME);
				String    source         = MDCheckStepData.getTagValue(sourceNode, TAG_SURVIVOR_SOURCE);
				String    prioritization = MDCheckStepData.getTagValue(sourceNode, TAG_SURVIVOR_PRIORITIZATION);
				boolean   descend        = Boolean.valueOf(MDCheckStepData.getTagValue(sourceNode, TAG_SURVIVOR_DECENDING));
				eDataType dtype          = eDataType.valueOf(MDCheckStepData.getTagValue(sourceNode, TAG_SURVIVOR_DATATYPE));
				int       stackIndex     = Integer.valueOf(MDCheckStepData.getTagValue(sourceNode, TAG_SURVIVOR_STACK_IDX));
				survivorPassThru.add(new SurvivorField(outputName, csMethod, source, dtype, stackIndex, prioritization, descend));
			}
			String value = MDCheckStepData.getTagValue(node, TAG_SURVIVORSHIP);
			isSurvivorPass = (value != null) ? Boolean.valueOf(value) : isSurvivorPass;
		} else {
			setDefault();
		}
	}

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep) throws KettleException {

		filterOut = toList(rep.getStepAttributeString(idStep, categoryTag + "." + TAG_FILTER_OUT));
		passThru = toList(rep.getStepAttributeString(idStep, categoryTag + "." + TAG_PASS_THRU));
		if (categoryTag.equals("pass_thru")) {
			survivorPassThru = new ArrayList<SurvivorField>();
			String prefix     = TAG_SURVIVOR_PASS_THRU + "." + TAG_SURVIVOR_FIELD + ".";
			int    nrSrscores = rep.countNrStepAttributes(idStep, prefix + TAG_SURVIVOR_SOURCE);
			for (int i = 0; i < nrSrscores; i++) {
				ConsolidationMethod csMethod;
				try {
					csMethod = ConsolidationMethod.decode(rep.getStepAttributeString(idStep, i, prefix + TAG_SURVIVOR_CONSOLIDATION));
				} catch (KettleException e) {
					csMethod = ConsolidationMethod.FirstData;
				}
				String    outputName     = rep.getStepAttributeString(idStep, i, prefix + TAG_SURVIVOR_OUTPUT_NAME);
				String    source         = rep.getStepAttributeString(idStep, i, prefix + TAG_SURVIVOR_SOURCE);
				String    prioritization = rep.getStepAttributeString(idStep, i, prefix + TAG_SURVIVOR_PRIORITIZATION);
				boolean   decend         = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + TAG_SURVIVOR_DECENDING));
				eDataType dtype          = eDataType.valueOf(rep.getStepAttributeString(idStep, i, prefix + TAG_SURVIVOR_DATATYPE));
				int       stackIndex     = (int) rep.getStepAttributeInteger(idStep, i, prefix + TAG_SURVIVOR_STACK_IDX);

				survivorPassThru.add(new SurvivorField(outputName, csMethod, source, dtype, stackIndex, prioritization, decend));
			}
			String value = rep.getStepAttributeString(idStep, TAG_SURVIVOR_PASS_THRU + "." + TAG_SURVIVORSHIP);
			isSurvivorPass = (value != null) ? Boolean.valueOf(value) : isSurvivorPass;
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

		rep.saveStepAttribute(idTransformation, idStep, categoryTag + "." + TAG_FILTER_OUT, toString(filterOut));
		rep.saveStepAttribute(idTransformation, idStep, categoryTag + "." + TAG_PASS_THRU, toString(passThru));
		if (categoryTag.equals("pass_thru")) {
			String prefix = TAG_SURVIVOR_PASS_THRU + "." + TAG_SURVIVOR_FIELD + ".";
			int    i      = 0;
			for (SurvivorField svrRecord : survivorPassThru) {
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_CONSOLIDATION, svrRecord.getConsolidationMethod().encode());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_OUTPUT_NAME, svrRecord.getOutputName());
				if (svrRecord.getDisplaySource().contains("Stack Group")) {
					rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_SOURCE, svrRecord.getDisplaySource() + ":" + svrRecord.getSource());
					rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_PRIORITIZATION, svrRecord.getDisplayPrioritization() + ":" + svrRecord.getPrioritization());
				} else {
					rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_SOURCE, svrRecord.getSource());
					rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_PRIORITIZATION, svrRecord.getPrioritization());
				}
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_DECENDING, svrRecord.isDescending());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_DATATYPE, svrRecord.getDataType().name());

				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_SURVIVOR_STACK_IDX, svrRecord.getStackIndex());
				i++;
			}

			rep.saveStepAttribute(idTransformation, idStep, TAG_SURVIVOR_PASS_THRU + "." + TAG_SURVIVORSHIP, Boolean.toString(isSurvivorPass));
		}
	}

	/**
	 * Called during processing before the first row. It will lookup the index of the pass thru fields and
	 * detect any problems.
	 *
	 * @param ioMeta
	 * @return
	 */
	public boolean selectIndexes(MDCheck step, MDCheckData data, IOMeta ioMeta) {

		step.logDebug("Selecting " + passThru.size() + " pass thru fields");
		// Find the indexes of the selected fields in the source row.
		if (!isSurvivorPass) {
			int[] passThruFieldNrs = ioMeta.passThruFieldNrs = new int[passThru.size()];
			for (int i = 0; i < passThruFieldNrs.length; i++) {
				passThruFieldNrs[i] = ioMeta.inputMeta.indexOfValue(passThru.get(i));
				if (passThruFieldNrs[i] < 0) {
					step.logError(BaseMessages.getString(PKG, "MDCheck.Log.CouldNotFindField", passThru.get(i))); //$NON-NLS-1$
					step.setErrors(1);
					step.stopAll();
					return false;
				}
			}
		} else {
			int[] passThruFieldNrs = ioMeta.passThruFieldNrs = new int[survivorPassThru.size()];
			for (int i = 0; i < passThruFieldNrs.length; i++) {
				passThruFieldNrs[i] = ioMeta.inputMeta.indexOfValue(survivorPassThru.get(i).getSource());
				if (passThruFieldNrs[i] < 0) {
					// If this is survivor passthru we are ok because new fields may have been created
					if (!isSurvivorPass) {
						step.logError(BaseMessages.getString(PKG, "MDCheck.Log.CouldNotFindField", survivorPassThru.get(i))); //$NON-NLS-1$
						step.setErrors(1);
						step.stopAll();
						return false;
					} else if (!Const.isEmpty(survivorPassThru.get(i).getSource())) {
						ValueMetaInterface vMet = ioMeta.outputMeta.getValueMeta(i);
						vMet.setType(survivorPassThru.get(i).getValueMetaType());
						passThruFieldNrs[i] = i;
					} else {
						step.logError(BaseMessages.getString(PKG, "MDCheck.Log.CouldNotFindSource", survivorPassThru.get(i))); //$NON-NLS-1$
						step.setErrors(1);
						step.stopAll();
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Called during processing to add the pass-thru fields to the output data
	 *
	 * @param check
	 * @param meta
	 * @param data
	 * @param ioMeta
	 */
	public void selectValues(MDCheck check, MDCheckMeta meta, MDCheckData data, List<MDCheckRequest> requests, IOMeta ioMeta) {
		// For each request...
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDCheckRequest   request   = requests.get(recordID);
			RowMetaInterface inputMeta = request.inputMeta;
			Object[]         inputData = request.inputData;
			// Get the field values
			//
			for (int index : ioMeta.passThruFieldNrs) {
				// Normally this can't happen, except when streams are mixed with different number of fields.
				if (index < inputMeta.size()) {
					Object value = inputData[index];
					request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, value);
				} else {
					if (check.isDetailed()) {
						check.logDetailed(BaseMessages.getString(PKG, "MDCheck.Log.MixingStreamWithDifferentFields")); //$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault() {

		filterOut.clear();
		passThru.clear();
		survivorPassThru.clear();
	}

	public void setFilterOut(List<String> filterOut) {

		this.filterOut = filterOut;
	}

	public void setPassThru(List<String> passThru) {

		this.passThru = passThru;
	}

	public void setSurvivorPass(boolean isSurvivorPass) {

		this.isSurvivorPass = isSurvivorPass;
	}

	public void setSurvivorPassThru(List<SurvivorField> survivorPassThru) {

		this.survivorPassThru = survivorPassThru;
	}

	/**
	 * Called to validate settings before saving
	 *
	 * @param warnings
	 * @param errors
	 */
	public void validate(List<String> warnings, List<String> errors) {
		// nothing to validate
	}

	/**
	 * Called to convert a string to a list
	 *
	 * @param value
	 */
	private List<String> toList(String value) {

		List<String> l = new ArrayList<String>();
		if (value != null) {
			String[] split = value.split(",");
			for (String field : split) {
				l.add(field);
			}
		}
		return l;
	}

	/**
	 * Called to a list to a string
	 *
	 * @return
	 */
	private String toString(List<String> l) {

		if (l.size() == 0) {
			return null;
		}
		StringBuffer sb  = new StringBuffer();
		String       sep = "";
		for (String e : l) {
			sb.append(sep).append(e);
			sep = ",";
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected PassThruMeta clone() throws CloneNotSupportedException {

		PassThruMeta retval = (PassThruMeta) super.clone();
		retval.filterOut = new ArrayList<String>(filterOut);
		retval.passThru = new ArrayList<String>(passThru);
		List<SurvivorField> clonedSurvivors = new ArrayList<SurvivorField>(survivorPassThru.size());
		for (SurvivorField sr : survivorPassThru) {
			clonedSurvivors.add(sr.clone());
		}
		retval.survivorPassThru = clonedSurvivors;
		return retval;
	}
}
