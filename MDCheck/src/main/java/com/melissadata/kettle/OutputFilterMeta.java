package com.melissadata.kettle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.melissadata.kettle.support.FilterTarget;
import com.melissadata.kettle.support.MDBinaryEvaluator;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.mu.LookupTarget;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.errorhandling.Stream;
import org.pentaho.di.trans.step.errorhandling.StreamIcon;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface.StreamType;
import org.w3c.dom.Node;

public class OutputFilterMeta implements Cloneable {

	private static       Class<?>           PKG                   = OutputFilterMeta.class;
	private static final String             TAG_OUTPUT_FILTER     = "output_filter";
	public static final  String             TAG_RESULT_CODES      = "result_codes";
	public static final  String             TAG_FILTERS           = "filters";
	public static final  String             TAG_FILTER            = "filter";
	public static final  String             TAG_FILTER_NAME       = "filter_name";
	public static final  String             TAG_FILTER_RULE       = "filter_rule";
	public static final  String             TAG_FILTER_TARGET     = "filter_target";
	public static final  int                MAX_TARGETS           = 4;
	private static final String             TAG_LOOKUP_TARGET     = "lookup_target";
	private              boolean            doMatchUp             = false;
	private              String             resultCodes           = null;
	private static final int                MD_SIZE_RESULTCODES   = 100;
	private              List<FilterTarget> filterTargets         = null;
	private              LookupTarget       lookupTarget          = null;
	private              int                chkType               = 0;
	private static       StreamInterface    newFilterTargetStream = new Stream(StreamType.TARGET, null, BaseMessages.getString(PKG, "MDCheckMeta.TargetStream.NewFilterTarget.Description"), StreamIcon.TARGET, null);
	private static       StreamInterface    newLookupTargetStream = new Stream(StreamType.TARGET, null, BaseMessages.getString(PKG, "MDCheckMeta.TargetStream.NewLookupTarget.Description"), StreamIcon.TARGET, null);

	public OutputFilterMeta(int checkType, boolean doMatchUp) {

		this.doMatchUp = doMatchUp;
		chkType = checkType;
		setDefault(chkType);
	}

	/**
	 * Called to add the result codes to the output data (if so requested)
	 *
	 * @param requests
	 */
	public void addResultCodes(List<MDCheckRequest> requests) {
		// Add result codes for each request
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDCheckRequest request = requests.get(recordID);
			if (!Const.isEmpty(resultCodes)) {
				Set<String>  sortedResultCodes = new TreeSet<String>(request.resultCodes);
				StringBuffer s                 = new StringBuffer();
				String       sep               = "";
				for (String result : sortedResultCodes) {
					s.append(sep).append(result);
					sep = ",";
				}
				request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, s.toString());
			}
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
		// TODO: Do something here?
	}

	/**
	 * Called to determine the output fields that will be included in the step output record
	 * <p>
	 * NOTE: Order of fields must match the order of fields in getResponseValues
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {

		if (!Const.isEmpty(resultCodes)) {
			MDCheckMeta.getStringField(row, originName, space, resultCodes, MD_SIZE_RESULTCODES);
		}
	}

	/**
	 * @return the filter targets
	 */
	public List<FilterTarget> getFilterTargets() {

		return filterTargets;
	}

	/**
	 * @return The name of the lookup target
	 */
	public LookupTarget getLookupTarget() {

		return lookupTarget;
	}

	/**
	 * Called to get an optional new target stream
	 *
	 * @return
	 */
	public List<StreamInterface> getOptionalStreams() {
		// If there are free filter target spots then add an optional stream for creating a new one
		List<StreamInterface> list = new ArrayList<StreamInterface>();
		if (filterTargets.size() < MAX_TARGETS) {
			list.add(newFilterTargetStream);
		}
		// If this is the matchup object then add an optional stream for the lookup target
		if (doMatchUp && (lookupTarget == null)) {
			list.add(newLookupTargetStream);
		}
		return list;
	}

	public String getResultCodes() {

		return resultCodes;
	}

	/**
	 * Called to define the meta data for the output targets
	 *
	 * @param ioMeta
	 */
	public void getStepIOMeta(StepIOMetaInterface ioMeta) {
		// Add the targets...
		//
		for (FilterTarget target : filterTargets) {
			if (!Const.isEmpty(target.getName())) {
				String          description = BaseMessages.getString(PKG, "MDCheckMeta.TargetStream.FilterTarget.Description", Const.NVL(target.getName(), ""));
				StreamInterface stream      = new Stream(StreamType.TARGET, target.getTargetStep(), description, StreamIcon.TARGET, target);
				ioMeta.addStream(stream);
			}
		}
		if (lookupTarget != null) {
			String          description = BaseMessages.getString(PKG, "MDCheckMeta.TargetStream.LookupTarget.Description");
			StreamInterface stream      = new Stream(StreamType.TARGET, lookupTarget.getTargetStep(), description, StreamIcon.TARGET, lookupTarget);
			ioMeta.addStream(stream);
		}
	}

	/**
	 * Returns the XML representation of the meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {

		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_OUTPUT_FILTER)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_RESULT_CODES, resultCodes));
		retval.append(tab).append("  ").append(XMLHandler.openTag(TAG_FILTERS));
		for (FilterTarget target : filterTargets) {
			retval.append(tab).append("    ").append(XMLHandler.openTag(TAG_FILTER));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_FILTER_NAME, target.getName()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_FILTER_RULE, target.getRule()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null));
			retval.append(tab).append("    ").append(XMLHandler.closeTag(TAG_FILTER));
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(TAG_FILTERS));
		if (lookupTarget != null) {
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_LOOKUP_TARGET, lookupTarget.getTargetStep() != null ? lookupTarget.getTargetStep().getName() : null));
		}
		retval.append(tab).append(XMLHandler.closeTag(TAG_OUTPUT_FILTER)).append(Const.CR);
		return retval.toString();
	}

	/**
	 * Called to handle external stream selection
	 *
	 * @param stream
	 */
	public void handleStreamSelection(StreamInterface stream) {
		// They selected the new filter target...
		if (stream == newFilterTargetStream) {
			// Create a new filter target step
			FilterTarget target = new FilterTarget();
			target.setTargetStep(stream.getStepMeta());
			target.setName(stream.getStepMeta().getName());
			filterTargets.add(target);
		}
		// They selected the lookup target...
		else if (stream == newLookupTargetStream) {
			// Create a new lookup target step
			lookupTarget = new LookupTarget();
			lookupTarget.setTargetStep(stream.getStepMeta());
		}
	}

	/**
	 * Called to read output filter meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		// Load meta data
		List<Node> nodes = XMLHandler.getNodes(node, TAG_OUTPUT_FILTER);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			resultCodes = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_RESULT_CODES), resultCodes);
			Node filterNodes = XMLHandler.getSubNode(node, TAG_FILTERS);
			int  nrFilters   = XMLHandler.countNodes(filterNodes, TAG_FILTER);
			filterTargets = new ArrayList<FilterTarget>(nrFilters);
			for (int i = 0; i < nrFilters; i++) {
				Node         filterNode = XMLHandler.getSubNodeByNr(filterNodes, TAG_FILTER, i);
				FilterTarget target     = new FilterTarget();
				target.setName(MDCheckStepData.getTagValue(filterNode, TAG_FILTER_NAME));
				target.setRule(MDCheckStepData.getTagValue(filterNode, TAG_FILTER_RULE));
				target.setTargetStepname(MDCheckStepData.getTagValue(filterNode, TAG_FILTER_TARGET));
				filterTargets.add(target);
			}
			String lookupTargetName = MDCheckStepData.getTagValue(node, TAG_LOOKUP_TARGET);
			if (lookupTargetName != null) {
				lookupTarget = new LookupTarget();
				lookupTarget.setTargetStepname(lookupTargetName);
			}
		} else {
			setDefault(chkType);
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
		// Load meta data
		resultCodes = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_OUTPUT_FILTER + "." + TAG_RESULT_CODES), resultCodes);
		int nrFilters = rep.countNrStepAttributes(idStep, TAG_OUTPUT_FILTER + "." + TAG_FILTER_NAME);
		filterTargets = new ArrayList<FilterTarget>(nrFilters);
		for (int i = 0; i < nrFilters; i++) {
			FilterTarget target = new FilterTarget();
			target.setName(rep.getStepAttributeString(idStep, i, TAG_OUTPUT_FILTER + "." + TAG_FILTER_NAME));
			target.setRule(rep.getStepAttributeString(idStep, i, TAG_OUTPUT_FILTER + "." + TAG_FILTER_RULE));
			target.setTargetStepname(rep.getStepAttributeString(idStep, i, TAG_OUTPUT_FILTER + "." + TAG_FILTER_TARGET));
			filterTargets.add(target);
		}
		String lookupTargetName = rep.getStepAttributeString(idStep, TAG_OUTPUT_FILTER + "." + TAG_LOOKUP_TARGET);
		if (lookupTargetName != null) {
			lookupTarget = new LookupTarget();
			lookupTarget.setTargetStepname(lookupTargetName);
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

		rep.saveStepAttribute(idTransformation, idStep, TAG_OUTPUT_FILTER + "." + TAG_RESULT_CODES, resultCodes);
		for (int i = 0; i < filterTargets.size(); i++) {
			FilterTarget target = filterTargets.get(i);
			rep.saveStepAttribute(idTransformation, idStep, i, TAG_OUTPUT_FILTER + "." + TAG_FILTER_NAME, target.getName());
			rep.saveStepAttribute(idTransformation, idStep, i, TAG_OUTPUT_FILTER + "." + TAG_FILTER_RULE, target.getRule());
			rep.saveStepAttribute(idTransformation, idStep, i, TAG_OUTPUT_FILTER + "." + TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null);
		}

		if (lookupTarget != null) {
			rep.saveStepAttribute(idTransformation, idStep, TAG_OUTPUT_FILTER + "." + TAG_LOOKUP_TARGET, lookupTarget.getTargetStep() != null ? lookupTarget.getTargetStep().getName() : null);
		}
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault(int chkType) {

		if ((chkType & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			resultCodes = "SM_Results";
		} else if ((chkType & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			resultCodes = "MU_Results";
		} else if ((chkType & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			resultCodes = "IP_Results";
		} else {
			resultCodes = "MD_Results";
		}
		filterTargets = new ArrayList<FilterTarget>(1);
// FilterTarget filterTarget = new FilterTarget();
// filterTarget.setName("");
// filterTarget.setRule("");
// filterTarget.setTargetStepname("");
// filterTargets.add(filterTarget);
		lookupTarget = null;
	}

	/**
	 * @param filterTargets the filter targets to set
	 */
	public void setFilterTargets(List<FilterTarget> filterTargets) {

		this.filterTargets = filterTargets;
	}

	/**
	 * @param target The new lookup target
	 */
	public void setLookupTarget(LookupTarget target) {

		lookupTarget = target;
	}

	public void setResultCodes(String s) {

		resultCodes = s;
	}

	/**
	 * Called to validate settings before saving
	 *
	 * @param warnings
	 * @param errors
	 * @param data
	 */
	public void validate(MDCheckStepData data, List<String> warnings, List<String> errors) {

		String      tmpRule  = "";    // holds rule for reformat and testing
		Validations rcv      = new Validations();  // obj to verify if using known result code
		String[]    resultAr = new String[20]; // array for separating rule into sub strings i.e. [AE11],OR,[AE10]
		rcv.checkFilterOrder(data, warnings, errors);
		for (FilterTarget target : filterTargets) {// go through filter rules
			// format the rule for MDBinaryEvaluator and neatness
			tmpRule = target.getRule().replace("[", " [").replace("]", "] ").replaceAll("\\s+", " ").toUpperCase().trim();
			try {
				new MDBinaryEvaluator(tmpRule); // checks rule for format & known operators i.e. OR, AND ...
			} catch (KettleException e) {
				// Add errors to list
				errors.add(BaseMessages.getString(PKG, "MDCheckMeta.BinaryEvaluator.Rule.Error") + tmpRule + e.getMessage());
			}
			resultAr = tmpRule.split(" "); // Separate into individual result codes
			int i = 0;
			while (i <= (resultAr.length - 1)) {
				if (resultAr[i].startsWith("[")) {// only check result codes
					// strip it down & check if it is a known result code
					if (!rcv.verifyResultCode(resultAr[i].replace("[", "").replace("]", "").trim(), data.checkTypes)) {
						warnings.add(resultAr[i] + BaseMessages.getString(PKG, "MDCheckMeta.BinaryEvaluator.UnknownCode"));// warn
// if code is unknown
					}
				}
				i++;
			}
			target.setRule(tmpRule);// reset formated and verified rule
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected OutputFilterMeta clone() throws CloneNotSupportedException {

		return (OutputFilterMeta) super.clone();
	}
}
