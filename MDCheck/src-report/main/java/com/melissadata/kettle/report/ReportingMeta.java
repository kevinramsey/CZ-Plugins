package com.melissadata.kettle.report;

import java.util.List;

import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.validation.Validations;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;

public class ReportingMeta implements Cloneable {

	private static final String TAG_OPT_GLOBAL_REPORTS = "opt_global_reports";

	private static final String TAG_OPT_TO_FILE = "opt_to_file";

	private static final String TAG_OPT_SUB_REPORTS = "opt_sub_reports";

	private static final String TAG_OPT_SUB_REPORT = "opt_sub_report";

	private static final String TAG_OUTPUT_REPORT_DIRNAME = "output_report_dirname";

	private static final String TAG_OUTPUT_REPORT_NAME = "output_report_name";

	private static final String TAG_REPORTS = "output_report";

	private MDCheckStepData data;

	private String          outputReportDirname;
	private String          outputReportName;

	private boolean[]       optsSubReports;

	private boolean         optGlobalReports;

	private boolean         optToFile;
	
	public ReportingMeta(MDCheckStepData data) {
		setData(data);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ReportingMeta clone() throws CloneNotSupportedException {
		return (ReportingMeta) super.clone();
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	/**
	 * Checks the settings of this step and puts the findings in a remarks List.
	 * 
	 * @param remarks The list to put the remarks in @see org.pentaho.di.core.CheckResult
	 * @param stepMeta The stepMeta to help checking
	 * @param prev The fields coming from the previous step
	 * @param input The input step names
	 * @param output The output step names
	 * @param info The fields that are used as information by the step
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta,
			RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// TODO: Do something here?
	}

	/**
	 * Called to read meta data from a document node
	 * 
	 * @param node
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_REPORTS);
		if (nodes != null && nodes.size() > 0) {
			node = nodes.get(0);
			
			String value = MDCheckStepData.getTagValue(node, TAG_OPT_GLOBAL_REPORTS);
			optGlobalReports = (value != null) ? Boolean.valueOf(value) : optGlobalReports;

			value = MDCheckStepData.getTagValue(node, TAG_OPT_TO_FILE);
			optToFile = (value != null) ? Boolean.valueOf(value) : optToFile;

			outputReportDirname = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_REPORT_DIRNAME), outputReportDirname);
			outputReportName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_REPORT_NAME), outputReportName);

			Node subNodes = XMLHandler.getSubNode(node, TAG_OPT_SUB_REPORTS);
			int nrSubs = XMLHandler.countNodes(subNodes, TAG_OPT_SUB_REPORT);
			optsSubReports = new boolean[nrSubs];
			for (int i = 0; i < nrSubs; i++) {
				Node optNode = XMLHandler.getSubNodeByNr(subNodes, TAG_OPT_SUB_REPORT, i);
				value = optNode.getTextContent();
				optsSubReports[i] = (value != null) ? Boolean.valueOf(value) : optsSubReports[i];
			}
			
		} else {
			setDefault();
		}

	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault() {
		optGlobalReports = false;
		outputReportDirname = "";
		outputReportName = "";
		optsSubReports = new boolean[1];
		optsSubReports[0] = false;
		optToFile = false;
	}

	/**
	 * Returns the XML representation of the meta data
	 * 
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		
		retval.append(tab).append(XMLHandler.openTag(TAG_REPORTS)).append(Const.CR);
		
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_GLOBAL_REPORTS, Boolean.toString(optGlobalReports)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_TO_FILE, Boolean.toString(optToFile)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_REPORT_DIRNAME, outputReportDirname));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_REPORT_NAME, outputReportName));

		retval.append(tab).append(XMLHandler.openTag(TAG_OPT_SUB_REPORTS)).append(Const.CR);
		for (int i = 0; i < optsSubReports.length; i++)
			retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_SUB_REPORT, Boolean.toString(optsSubReports[i])));
		retval.append(tab).append("  ").append(XMLHandler.closeTag(TAG_OPT_SUB_REPORTS));

		retval.append(tab).append(XMLHandler.closeTag(TAG_REPORTS)).append(Const.CR);

		return retval.toString();
	}

	/**
	 * Called to retrieve data from a repository
	 * 
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep) throws KettleException {
		String prefix = TAG_REPORTS + ".";
		
		optGlobalReports = rep.getStepAttributeBoolean(idStep, prefix + TAG_OPT_GLOBAL_REPORTS);
		optToFile = rep.getStepAttributeBoolean(idStep, prefix + TAG_OPT_TO_FILE);

		outputReportDirname = MDCheckStepData.safe(rep.getStepAttributeString(idStep, prefix + TAG_OUTPUT_REPORT_DIRNAME), outputReportDirname);
		outputReportName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, prefix + TAG_OUTPUT_REPORT_NAME), outputReportName);
		
		String subPrefix = prefix + TAG_OPT_SUB_REPORTS;
		int nrSubs = rep.countNrStepAttributes(idStep, subPrefix);
		optsSubReports = new boolean[nrSubs];
		for (int i = 0; i < nrSubs; i++)
			optsSubReports[i] = rep.getStepAttributeBoolean(idStep, i, subPrefix + "." + TAG_OPT_SUB_REPORT);
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
		String prefix = TAG_REPORTS + ".";
		
		rep.saveStepAttribute(idTransformation, idStep, prefix + TAG_OPT_GLOBAL_REPORTS, optGlobalReports);
		rep.saveStepAttribute(idTransformation, idStep, prefix + TAG_OPT_TO_FILE, optToFile);

		rep.saveStepAttribute(idTransformation, idStep, prefix + TAG_OUTPUT_REPORT_DIRNAME, outputReportDirname);
		rep.saveStepAttribute(idTransformation, idStep, prefix + TAG_OUTPUT_REPORT_NAME, outputReportName);

		String subPrefix = prefix + TAG_OPT_SUB_REPORTS;
		for (int i = 0; i < optsSubReports.length; i++)
			rep.saveStepAttribute(idTransformation, idStep, i, subPrefix + "." + TAG_OPT_SUB_REPORT, optsSubReports[i]);
	}

	/**
	 * Called to validate settings before saving
	 * 
	 * @param warnings
	 * @param errors
	 */
	public void validate(List<String> warnings, List<String> errors) {
		Validations checker = new Validations();
		checker.checkReportingOutputFields(data, warnings, errors);
	}

	public String getOutputReportDirname(TransMeta transMeta) {
		String converted;
		if(transMeta == null){
			return outputReportDirname;
		} else {
			converted = transMeta.environmentSubstitute(outputReportDirname);
			if (Const.isWindows()) {
				converted = converted.replaceFirst("file:///", "");
			} else {
				converted = converted.replaceFirst("file://", "");
			}
			return converted;
		}
		
	}

	public void setOutputReportDirname(String outputReportDirname) {
		this.outputReportDirname = outputReportDirname;
	}

	public boolean isOptGlobalReports() {
		return optGlobalReports;
	}

	public void setOptGlobalReports(boolean optGlobalReports) {
		this.optGlobalReports = optGlobalReports;
	}

	public boolean[] getOptsSubReports() {
		return optsSubReports;
	}

	public void setOptsSubReports(boolean[] optsSubReports) {
		this.optsSubReports = optsSubReports;
	}

	public boolean isOptToFile() {
		return optToFile;
	}

	public void setOptToFile(boolean optToFile) {
		this.optToFile = optToFile;
	}

	public String getOutputReportName(TransMeta transMeta) {
		if(transMeta == null){
			return outputReportName;
		} else {
			return transMeta.environmentSubstitute(outputReportName);
		}
	}

	public void setOutputReportName(String outputReportName) {
		this.outputReportName = outputReportName;
	}

}
