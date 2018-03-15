package com.melissadata.kettle.businesscoder;

import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.FilterTarget;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MDStreamHandler;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.request.MDBusinessCoderRequest;
import com.melissadata.kettle.businesscoder.support.OutputFilterFields;
import com.melissadata.kettle.businesscoder.support.PassThruFields;
import com.melissadata.kettle.businesscoder.support.PluginInstaller;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.*;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Step(id = "MDBusinessCoderPlugin", image = "com/melissadata/kettle/businesscoder/images/PDI_MD_BusinessCoder_V1.svg", description = "MDBusinessCoderPlugin.FullStep.Description", name = "MDBusinessCoderPlugin.FullStep.Name", categoryDescription = "MDBusinessCoderPlugin.Category", i18nPackageName = "com.melissadata.kettle")
public class MDBusinessCoderMeta extends BaseStepMeta implements StepMetaInterface {
	public static final  String              TAG_MELISSADATA_SPECIAL_USAGE = "melissadata_special_usage";
	private static final String              TAG_BUSINESSCODER_VERSION     = "md_businesscoder_version";
	private static final String              TAG_CONTACT_ZONE_FILE         = "contact_zone.prp";
	private static       String              transmissionReference         = "";
	private static       PluginInstaller     pluginInstaller               = null;
	private static       Class<?>            PKG                           = MDBusinessCoderMeta.class;
	public               PassThruFields      passThruFields                = null;
	public               OutputFilterFields  oFilterFields                 = null;
	public               BusinessCoderFields businessCoderFields           = null;
	public               boolean             isCluster                     = false;
	private              VariableSpace       space                         = null;
	private              MDBusinessCoderData bcData                        = null;
	private              boolean             updateData                    = false;
	private              boolean             isContactZone                 = false;
	private              LogChannelInterface log                           = null;
	private              boolean             isGlobalLoad                  = false;
	public MDBusinessCoderMeta() throws KettleException {
		this.log = getLog();
		LogLevel ll = log.getLogLevel();
		if (!isGlobalLoad) {
			log.setLogLevel(LogLevel.DEBUG);
			loadGlobal();
			log.setLogLevel(ll);
		}
	}

	/**
	 * Wraps the XMLHandler.addTagValue(String, String) method. If the value is a zero-length string then it will be padded
	 * to one character. This is done so that the generated XML will have some
	 * value instead of the null that is currently returned
	 * when it is read back in.
	 *
	 * @param tag
	 * @param val
	 * @return
	 */
	public static String addTagValue(String tag, String val) {
		if ((val != null) && (val.length() == 0)) {
			val = " ";
		}
		return XMLHandler.addTagValue(tag, val);
	}

	/**
	 * @param row
	 * @param originName
	 * @param space
	 * @param fieldValue
	 * @param fieldLength
	 */
	public static void getStringField(RowMetaInterface row, String originName, VariableSpace space, String fieldValue, int fieldLength) {
		// Don't add it if it is blank
		if (fieldValue.trim().length() == 0) {
			return;
		}
		// Build meta description of field
		ValueMetaInterface v = new ValueMeta(space.environmentSubstitute(fieldValue), ValueMetaInterface.TYPE_STRING);
		v.setOrigin(originName);
		v.setLength(fieldLength);
		row.addValueMeta(v);
	}

	/**
	 * Warps the XMLHandler.getTagValue(Node, String) method. Reverses the special handling done in the above addTagValue()
	 * method by triming values before returning them.
	 *
	 * @param n
	 * @param tag
	 * @return
	 */
	public static String getTagValue(Node n, String tag) {
		String value = XMLHandler.getTagValue(n, tag);
		if (value != null) {
			value = value.trim();
		}
		return value;
	}

	/**
	 * @param value
	 * @return empty string if null
	 */
	public static String safe(String value, String def) {
		return (value != null) ? value : def;
	}

	public static boolean isEnterprise() {
		return MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_BusinessCoder) || MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_Any);
	}

	public static String getTransmissionReference() {
		return transmissionReference;
	}

	@Override public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		List<String> passThru = passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH);
		RowMetaInterface cleanRow = new RowMeta();
		// Add pass thru fields
		if ((passThru != null) && (passThru.size() > 0)) {
			for (String field : passThru) {
				// Look for that pass thru field in the input row meta
				ValueMetaInterface v = row.searchValueMeta(field);
				if (v != null) {
					// We found a value. Clone it and add to the result row
					v = v.clone();
					cleanRow.addValueMeta(v);
				}
			}
		}
		row.clear();
		row.addRowMeta(cleanRow);
		// Get fields defined by the data.
		for (String key : businessCoderFields.outputFields.keySet()) {
			if (!(businessCoderFields.outputFields.get(key).metaValue.startsWith(BusinessCoderFields.DISABLE_STRING)) && !Const.isEmpty(businessCoderFields.outputFields.get(key).metaValue) && !key.equals(BusinessCoderFields.TAG_OUTPUT_RESULTS)) {
				getStringField(row, originName, space, businessCoderFields.outputFields.get(key).metaValue, businessCoderFields.outputFields.get(key).metaSize);
			}
		}
		if (!Const.isEmpty(oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue)) {
			getStringField(row, originName, space, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaSize);
		}
		if (!Const.isEmpty(businessCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_RESULTS).metaValue)) {
			getStringField(row, originName, space, businessCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_RESULTS).metaValue, businessCoderFields.outputFields.get(BusinessCoderFields.TAG_OUTPUT_RESULTS).metaSize);
		}
	}

	private void setTransmissionMessage() {
		String product = isContactZone ? "CZ" : "PENTAHO";
		String version = getVersionFromManifest();
		transmissionReference = "mdSrc:{product:" + product + ";version:" + version + "}";
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getOptionalStreams()
	 */
	@Override public List<StreamInterface> getOptionalStreams() {
		return MDStreamHandler.getOptionalStreams(oFilterFields.filterTargets);
	}

	@Override public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		space = transMeta.getParentVariableSpace();
		return new MDBusinessCoderStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override public StepDataInterface getStepData() {
		if (bcData == null) {
			bcData = new MDBusinessCoderData();
		}
		return bcData;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getStepIOMeta()
	 */
	@Override public StepIOMetaInterface getStepIOMeta() {
		if (ioMeta == null) {
			ioMeta = new StepIOMeta(true, false, false, false, false, true);
			// Add filter targets
			MDStreamHandler.getStepIOMeta(ioMeta, oFilterFields.filterTargets);
		}
		return ioMeta;
	}

	@Override public String getXML() throws KettleException {
		// Save data to XML
		StringBuilder retval = new StringBuilder(200);
		String tab = "      ";
		retval.append(tab).append(XMLHandler.openTag(BusinessCoderFields.TAG_BUSINESS_CODER_OPTIONS)).append(Const.CR);
		for (String key : businessCoderFields.optionFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, businessCoderFields.optionFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(BusinessCoderFields.TAG_BUSINESS_CODER_OPTIONS)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(BusinessCoderFields.TAG_BUSINESS_CODER_INPUT)).append(Const.CR);
		for (String key : businessCoderFields.inputFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, businessCoderFields.inputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(BusinessCoderFields.TAG_BUSINESS_CODER_INPUT)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(BusinessCoderFields.TAG_BUSINESS_CODER_OUTPUT)).append(Const.CR);
		for (String key : businessCoderFields.outputFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, businessCoderFields.outputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(BusinessCoderFields.TAG_BUSINESS_CODER_OUTPUT)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(PassThruFields.TAG_BUSINESS_CODER_FILTER)).append(Const.CR);
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT) != null) {
			retval.append(tab).append("  ").append(addTagValue(PassThruFields.TAG_FILTER_OUT, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT))));
		}
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH) != null) {
			retval.append(tab).append("  ").append(addTagValue(PassThruFields.TAG_PASS_THROUGH, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH))));
		}
		retval.append(tab).append(XMLHandler.closeTag(PassThruFields.TAG_BUSINESS_CODER_FILTER)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(OutputFilterFields.TAG_OUTPUT_FILTER)).append(Const.CR);
		retval.append(tab).append("  ").append(addTagValue(OutputFilterFields.TAG_RESULT_CODES, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue));
		retval.append(tab).append("  ").append(XMLHandler.openTag(OutputFilterFields.TAG_FILTERS));
		if ((oFilterFields != null) && (oFilterFields.filterTargets != null)) {
			for (FilterTarget target : oFilterFields.filterTargets) {
				retval.append(tab).append("    ").append(XMLHandler.openTag(OutputFilterFields.TAG_FILTER));
				retval.append(tab).append("    ").append(addTagValue(OutputFilterFields.TAG_FILTER_NAME, target.getName()));
				retval.append(tab).append("    ").append(addTagValue(OutputFilterFields.TAG_FILTER_RULE, target.getRule()));
				retval.append(tab).append("    ").append(addTagValue(OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null));
				retval.append(tab).append("    ").append(XMLHandler.closeTag(OutputFilterFields.TAG_FILTER));
			}
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(OutputFilterFields.TAG_FILTERS));
		retval.append(tab).append(XMLHandler.closeTag(OutputFilterFields.TAG_OUTPUT_FILTER)).append(Const.CR);
		return retval.toString();
	}

	@Override public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		String prefix = BusinessCoderFields.TAG_BUSINESS_CODER_OPTIONS;
		for (String key : businessCoderFields.optionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, prefix + "." + key, businessCoderFields.optionFields.get(key).metaValue);
		}
		prefix = BusinessCoderFields.TAG_BUSINESS_CODER_INPUT;
		for (String key : businessCoderFields.inputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, prefix + "." + key, businessCoderFields.inputFields.get(key).metaValue);
		}
		prefix = BusinessCoderFields.TAG_BUSINESS_CODER_OUTPUT;
		for (String key : businessCoderFields.outputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, prefix + "." + key, businessCoderFields.outputFields.get(key).metaValue);
		}
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT) != null) {
			rep.saveStepAttribute(idTransformation, idStep, PassThruFields.TAG_FILTER_OUT, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT)));
		}
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH) != null) {
			rep.saveStepAttribute(idTransformation, idStep, PassThruFields.TAG_PASS_THROUGH, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH)));
		}
		rep.saveStepAttribute(idTransformation, idStep, OutputFilterFields.TAG_RESULT_CODES, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue);
		prefix = OutputFilterFields.TAG_FILTER;
		if ((oFilterFields != null) && (oFilterFields.filterTargets != null)) {
			int i = 0;
			for (FilterTarget target : oFilterFields.filterTargets) {
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + "." + OutputFilterFields.TAG_FILTER_NAME, target.getName());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + "." + OutputFilterFields.TAG_FILTER_RULE, target.getRule());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + "." + OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null);
				i++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pentaho.di.trans.step.BaseStepMeta#handleStreamSelection(org.pentaho.di.trans.step.errorhandling.StreamInterface)
	 */
	@Override public void handleStreamSelection(StreamInterface stream) {
		MDStreamHandler.handleStreamSelection(stream, oFilterFields.filterTargets);
		List<StreamInterface> targetStreams = getStepIOMeta().getTargetStreams();
		for (int i = 0; i < targetStreams.size(); i++) {
			if (stream == targetStreams.get(i)) {
				if (stream.getSubject() instanceof FilterTarget) {
					FilterTarget target = (FilterTarget) stream.getSubject();
					target.setTargetStep(stream.getStepMeta());
				}
			}
		}
		resetStepIoMeta(); // force stepIo to be recreated when it is next needed.
	}

	/**
	 * Called to load the global properties
	 *
	 * @throws KettleException
	 */
	public void loadGlobal() throws KettleException {
		String fileSep = System.getProperty("file.separator");
		File mdDir = new File(Const.getKettleDirectory() + fileSep + "MD");
		logDebug("MD objects location : " + mdDir.getAbsolutePath());
		checkContactZone();
		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				MDBusinessCoderStep.setIsSpoon(true);
			} else {
				MDBusinessCoderStep.setIsSpoon(false);
			}
		}
		if (MDBusinessCoderStep.isSpoon() && !isContactZone) {
			logDebug("Checking Pentaho Plugin Install ");
			moveTmp();
			if (!mdDir.exists()) {
				logBasic("Business Coder No MD dir Fresh Install");
				pluginInstaller = new PluginInstaller(isContactZone, log);
				pluginInstaller.doInstall(false, false, getVersionFromManifest());
				setUpdateData(true);
				MDProps.load();
			} else {
				setUpdateData(checkUpdate());
				if (MDBusinessCoderStep.isSpoon() && isUpdateData()) {
					logBasic("Business Coder Running Update Install");
					pluginInstaller = new PluginInstaller(isContactZone, log);
					pluginInstaller.doInstall(true, isUpdateData(), getVersionFromManifest());
				} else if (!MDBusinessCoderStep.isSpoon() && isUpdateData()) {
					// TODO instal for non gui
				}
			}
		} else if (MDBusinessCoderStep.isSpoon() && isContactZone) {
			pluginInstaller = new PluginInstaller(isContactZone, log);
			pluginInstaller.checkCZInstall(getInstalledPluginVersion());
		}
		checkHadoopCluster();
//		if(isCluster){
//			// sort of a hack normal method to check enterprise
//			// does not work on the cluster so if it is on a hadoop cluster
//			// we assume it is enterprise.
//			enterprise = true;
//		}
		if (!isContactZone) {
			loadClassFiles(isCluster);
		}
		MDProps.load();
		DQTObjectFactory.setLogLevel(log.getLogLevel());
		logDebug("Running Spoon = " + MDBusinessCoderStep.isSpoon());
		if (isEnterprise()) {
			logDebug("Mode = Enterprise Edition");
		} else {
			logDebug("Mode = Community Edition");
		}
		setTransmissionMessage();
		isGlobalLoad = true;
	}

	private boolean checkContactZone() {
		File checkFile = new File("ui" + Const.FILE_SEPARATOR + TAG_CONTACT_ZONE_FILE);
		isContactZone = checkFile.exists();
		logDebug("ContactZone check file : " + checkFile.getAbsolutePath());
		logDebug("ContactZone = " + isContactZone);
		return isContactZone;
	}

	public boolean checkHadoopCluster() {
		if (isCluster) {
			return isCluster;
		}
		if (MDBusinessCoderStep.isSpoon()) {
			return isCluster = false;
		}
		File clusterProp = new File("mdProps.prop");
		if (clusterProp.exists()) {
			isCluster = true;
		}
		if (isCluster) {
			log.logDebug(" - Running hadoop clustered ");
		}
		return isCluster;
	}

	@Override public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		setDefault();
		// OPTIONS
		List<Node> nodes = XMLHandler.getNodes(stepnode, BusinessCoderFields.TAG_BUSINESS_CODER_OPTIONS);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : businessCoderFields.optionFields.keySet()) {
				businessCoderFields.optionFields.get(key).metaValue = safe(getTagValue(tempNode, key), businessCoderFields.optionFields.get(key).metaValue);
			}
		}
		// INPUT FIELDS
		nodes = XMLHandler.getNodes(stepnode, BusinessCoderFields.TAG_BUSINESS_CODER_INPUT);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : businessCoderFields.inputFields.keySet()) {
				businessCoderFields.inputFields.get(key).metaValue = safe(getTagValue(tempNode, key), businessCoderFields.inputFields.get(key).metaValue);
			}
		}
		// OUTPUT FIELDS
		nodes = XMLHandler.getNodes(stepnode, BusinessCoderFields.TAG_BUSINESS_CODER_OUTPUT);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : businessCoderFields.outputFields.keySet()) {
				businessCoderFields.outputFields.get(key).metaValue = safe(getTagValue(tempNode, key), businessCoderFields.outputFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(stepnode, PassThruFields.TAG_BUSINESS_CODER_FILTER);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			List<String> passThru = toList(getTagValue(tempNode, PassThruFields.TAG_PASS_THROUGH));
			List<String> filterOut = toList(getTagValue(tempNode, PassThruFields.TAG_FILTER_OUT));
			if (passThru != null) {
				passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH).addAll(passThru);
			}
			if (filterOut != null) {
				passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT).addAll(filterOut);
			}
		}
		Node dupNodes = XMLHandler.getSubNode(stepnode, OutputFilterFields.TAG_OUTPUT_FILTER);
		dupNodes = XMLHandler.getSubNode(dupNodes, OutputFilterFields.TAG_FILTERS);
		int nrFilters = XMLHandler.countNodes(dupNodes, OutputFilterFields.TAG_FILTER);
		oFilterFields.filterTargets = new ArrayList<FilterTarget>(nrFilters);
		for (int i = 0; i < nrFilters; i++) {
			Node dupNode = XMLHandler.getSubNodeByNr(dupNodes, OutputFilterFields.TAG_FILTER, i);
			FilterTarget target = new FilterTarget();
			target.setName(getTagValue(dupNode, OutputFilterFields.TAG_FILTER_NAME));
			target.setRule(getTagValue(dupNode, OutputFilterFields.TAG_FILTER_RULE));
			target.setTargetStepname(getTagValue(dupNode, OutputFilterFields.TAG_FILTER_TARGET));
			oFilterFields.filterTargets.add(target);
		}
		nodes = XMLHandler.getNodes(stepnode, OutputFilterFields.TAG_OUTPUT_FILTER);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : oFilterFields.filterFields.keySet()) {
				oFilterFields.filterFields.get(key).metaValue = safe(getTagValue(tempNode, key), oFilterFields.filterFields.get(key).metaValue);
			}
		}
	}

	@Override public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {
		setDefault();
		String prefix = BusinessCoderFields.TAG_BUSINESS_CODER_OPTIONS;
		for (String key : businessCoderFields.optionFields.keySet()) {
			businessCoderFields.optionFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, prefix + "." + key), businessCoderFields.optionFields.get(key).metaValue);
		}
		// INPUT FIELDS
		prefix = BusinessCoderFields.TAG_BUSINESS_CODER_INPUT;
		for (String key : businessCoderFields.inputFields.keySet()) {
			businessCoderFields.inputFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, prefix + "." + key), businessCoderFields.inputFields.get(key).metaValue);
		}
		// OUTPUT FIELDS
		prefix = BusinessCoderFields.TAG_BUSINESS_CODER_OUTPUT;
		for (String key : businessCoderFields.outputFields.keySet()) {
			businessCoderFields.outputFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, prefix + "." + key), businessCoderFields.outputFields.get(key).metaValue);
		}
		List<String> passThru = toList(rep.getStepAttributeString(idStep, PassThruFields.TAG_PASS_THROUGH));
		List<String> filterOut = toList(rep.getStepAttributeString(idStep, PassThruFields.TAG_FILTER_OUT));
		if (passThru != null) {
			passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH).addAll(passThru);
		}
		if (filterOut != null) {
			passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT).addAll(filterOut);
		}
		oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue = safe(rep.getStepAttributeString(idStep, OutputFilterFields.TAG_RESULT_CODES), oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue);
		prefix = OutputFilterFields.TAG_FILTER + ".";
		int nrFilters = rep.countNrStepAttributes(idStep, prefix + OutputFilterFields.TAG_FILTER_NAME);
		oFilterFields.filterTargets = new ArrayList<FilterTarget>(nrFilters);
		for (int i = 0; i < nrFilters; i++) {
			FilterTarget target = new FilterTarget();
			target.setName(rep.getStepAttributeString(idStep, i, prefix + OutputFilterFields.TAG_FILTER_NAME));
			target.setRule(rep.getStepAttributeString(idStep, i, prefix + OutputFilterFields.TAG_FILTER_RULE));
			target.setTargetStepname(rep.getStepAttributeString(idStep, i, prefix + OutputFilterFields.TAG_FILTER_TARGET));
			oFilterFields.filterTargets.add(target);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#searchInfoAndTargetSteps(java.util.List)
	 */
	@Override public void searchInfoAndTargetSteps(List<StepMeta> steps) {
		// De-reference the filter target steps
		for (StreamInterface stream : getStepIOMeta().getTargetStreams()) {
			if (stream.getSubject() instanceof FilterTarget) {
				FilterTarget target = (FilterTarget) stream.getSubject();
				StepMeta stepMeta = StepMeta.findStep(steps, target.getTargetStepname());
				target.setTargetStep(stepMeta);
			}
		}
		// De-reference info steps (e.g. lookup step for matchup
		for (StreamInterface stream : getStepIOMeta().getInfoStreams()) {
			stream.setStepMeta(StepMeta.findStep(steps, (String) stream.getSubject()));
		}
		resetStepIoMeta();
	}

	/**
	 * Called during processing to add the pass-thru fields to the output data
	 *
	 * @param check
	 * @param meta
	 * @param data
	 * @param ioMeta
	 */
	public void selectValues(MDBusinessCoderStep check, MDBusinessCoderMeta meta, MDBusinessCoderData data, List<MDBusinessCoderRequest> requests, IOMetaHandler ioMeta) {
		// For each request...
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDBusinessCoderRequest request = requests.get(recordID);
			RowMetaInterface inputMeta = request.inputMeta;
			Object[] inputData = request.inputData;
			// Get the field values
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

	@Override public void setDefault() {
		if (passThruFields == null) {
			passThruFields = new PassThruFields();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
		}
		if (businessCoderFields == null) {
			businessCoderFields = new BusinessCoderFields();
		}
		oFilterFields.init();
		passThruFields.init();
		businessCoderFields.init();
	}

	@Override public boolean supportsErrorHandling() {
		return true;
	}

	private boolean checkUpdate() {
		if (isUpdateData()) {
			// If this is already set just return otherwise
			// we will get a a false result.
			return isUpdateData();
		}
		String insVer = getInstalledPluginVersion();
		String curVersion = getVersionFromManifest();
		if (insVer.equals(curVersion)) {
			logDebug("MDBusinessCoder version is up to date : " + insVer);
			return false;
		} else {
			logDebug("MDBusinessCoder version update detected. Installed " + insVer + " updating to version " + curVersion);
		}
		return true;
	}

	private String getVersionFromManifest() {
		String version = "0";
		Class<?> clazz = this.getClass();
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
			version = "0";
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest = new Manifest(new URL(manifestPath).openStream());
				String implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				if (implVersion == null) {
					version = "0";
				} else {
					version = implVersion;
				}
			} catch (Exception e) {
				version = "0";
			}
		}
		return version;
	}

	private String getInstalledPluginVersion() {
		String ver = "0";
		if (Props.isInitialized()) {
			ver = Props.getInstance().getProperty(TAG_BUSINESSCODER_VERSION);
			if (ver == null) {
				ver = "0";
			}
		}
		return ver;
	}

	private boolean specialUsage() {
		// used to allow Melissadata usage with out EE lic
		return Boolean.valueOf(MDProps.getProperty(TAG_MELISSADATA_SPECIAL_USAGE, "false"));
	}

	public boolean isUpdateData() {
		return updateData;
	}

	public void setUpdateData(boolean updateData) {
		this.updateData = updateData;
	}

	private File getSrcDir() {
		String fileSep = Const.FILE_SEPARATOR;
		String decodedPath = "";
		File ssdd = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error getting source dir: " + e.getMessage());
		}
		return new File(decodedPath);
	}

	private void loadClassFiles(boolean isCluster) throws KettleException {
		boolean loadExt = true;
		File libext_dir = null;
		if (!isCluster)
			libext_dir = new File(Const.getKettleDirectory(), "MD" + Const.FILE_SEPARATOR + "libext");
		else {
			String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			String libPath = path.substring(0, path.lastIndexOf("/"));
			libext_dir = new File(libPath, "MD" + Const.FILE_SEPARATOR + "libext");
		}
		if (libext_dir.exists()) {
			URLClassLoader spoonLoader = (URLClassLoader) KettleVFS.class.getClassLoader();
			URL[] urls = spoonLoader.getURLs();
			for (URL url : urls) {
				if (url.toString().contains("MDSettings")) {
					loadExt = false;
				}
				if (url.toString().contains("cz")) {
					loadExt = false;
				}
			}
			logDebug(" - Meta ext Class Files loaded = " + !loadExt);
			if (loadExt) {
				Class<URLClassLoader> sysClass = URLClassLoader.class;
				Method sysMethod;
				try {
					sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
					sysMethod.setAccessible(true);
					for (File file : libext_dir.listFiles()) {
						logDebug("Meta Loading external Class File - " + file.getAbsolutePath());
						sysMethod.invoke(spoonLoader, new Object[] { file.toURI().toURL() });
					}
				} catch (NoSuchMethodException e) {
					throw new KettleException(e.getMessage());
				} catch (SecurityException e) {
					throw new KettleException(e.getMessage());
				} catch (IllegalAccessException e) {
					throw new KettleException(e.getMessage());
				} catch (IllegalArgumentException e) {
					throw new KettleException(e.getMessage());
				} catch (InvocationTargetException e) {
					throw new KettleException(e.getMessage());
				} catch (MalformedURLException e) {
					throw new KettleException(e.getMessage());
				}
			}
		}
	}

	private void moveTmp() {
		Collection<File> files = null;
		File tmpDir = new File(new File(Const.getKettleDirectory()), "tmp");
		if (!tmpDir.exists()) {
			return;
		}
		String fileSep = Const.FILE_SEPARATOR;
		File kettleDir = new File(Const.getKettleDirectory());
		File md_dir = new File(kettleDir, "MD");
		File dest32 = new File(kettleDir, "MD" + fileSep + "32_bit");
		File dest64 = new File(kettleDir, "MD" + fileSep + "64_bit");
		File tmp32bit = new File(tmpDir, "32_bit");
		File tmp64bit = new File(tmpDir, "64_bit");
		File tmpLibext = new File(tmpDir, "libext");
		if (tmpLibext.exists()) {
			logDebug("Moving libext tmp files ");
			boolean success = false;
			try {
				File destLibext = new File(md_dir, "libext");
				FileUtils.deleteDirectory(destLibext);
				FileUtils.copyDirectory(tmpLibext, destLibext, false);
				success = true;
			} catch (IOException e) {
				logError("Error copying libext: " + e.getMessage());
			}
			if (success) {
				try {
					FileUtils.deleteDirectory(tmpLibext);
				} catch (IOException e) {
					logError("Error deleting tmpLibext: " + e.getMessage());
				}
			}
		}
		if (tmp32bit.exists()) {
			logDebug("Moving 32_bit object files ");
			boolean success = false;
			files = FileUtils.listFiles(tmp32bit, new String[] { "dll", "so" }, false);
			for (File file : files) {
				try {
					FileUtils.copyFileToDirectory(file, dest32, false);
					success = true;
				} catch (IOException e) {
					logError("Error copying file: " + file + " - " + e.getMessage());
				}
			}
			if (success) {
				try {
					FileUtils.deleteDirectory(tmp32bit);
				} catch (IOException e) {
					logError("Error deleting dir: " + e.getMessage());
				}
			}
		}
		if (tmp64bit.exists()) {
			logDebug("Moving 64_bit object files ");
			boolean success = false;
			files = FileUtils.listFiles(tmp64bit, new String[] { "dll", "so" }, false);
			for (File file : files) {
				try {
					FileUtils.copyFileToDirectory(file, dest64, false);
					success = true;
				} catch (IOException e) {
					logError("Error copying file: " + file + " - " + e.getMessage());
				}
			}
			if (success) {
				try {
					FileUtils.deleteDirectory(tmp64bit);
				} catch (IOException e) {
					logError("Error deleting dir: " + e.getMessage());
				}
			}
		}
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
		StringBuffer sb = new StringBuffer();
		String sep = "";
		for (String e : l) {
			sb.append(sep).append(e);
			sep = ",";
		}
		return sb.toString();
	}
}
