package com.melissadata.kettle.propertywebservice;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.FilterTarget;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MDStreamHandler;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.request.MDPropertyWebServiceRequest;
import com.melissadata.kettle.propertywebservice.support.OutputFilterFields;
import com.melissadata.kettle.propertywebservice.support.PassThruFields;
import com.melissadata.kettle.propertywebservice.support.PluginInstaller;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
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

@SuppressWarnings("deprecation")
@Step(id = "MDPropertyWebServicePlugin", image = "com/melissadata/kettle/propertywebservice/images/PDI_MD_Property_V1.svg", description = "MDPropertyWebServicePlugin.FullStep.Description", name = "MDPropertyWebServicePlugin.FullStep.Name", categoryDescription = "MDPropertyWebServicePlugin.Category", i18nPackageName = "com.melissadata.kettle")
public class MDPropertyWebServiceMeta extends BaseStepMeta implements StepMetaInterface {

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

		return MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_Property) || MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_Any);
	}

	public static final  String                   TAG_MELISSADATA_SPECIAL_USAGE    = "melissadata_special_usage";
	public static final  String                   TAG_PRIMARY_LICENSE_ENTERPRISE   = "license_enterprise";
	private static final String                   TAG_PROPERTY_WEB_SERVICE_VERSION = "md_propertywebservice_version";
	private static final String                   TAG_CONTACT_ZONE_FILE            = "contact_zone.prp";
	//	public static boolean enterprise;
	private static       PluginInstaller          pluginInstaller                  = null;
	private static       Class<?>                 PKG                      = MDPropertyWebServiceMeta.class;
	public               PassThruFields           passThruFields           = null;
	public               OutputFilterFields       oFilterFields            = null;
	public               PropertyWebServiceFields propertyWebServiceFields = null;
	public               boolean                  isCluster                = false;
	private              VariableSpace            space                    = null;
	private              MDPropertyWebServiceData propertyWebServiceData   = null;
	private              boolean                  updateData               = false;
	private              boolean                  isContactZone            = false;

	public MDPropertyWebServiceMeta() throws KettleException {

		loadGlobal();
	}

	public MDPropertyWebServiceMeta(boolean testing) throws KettleException {


	}

	public boolean checkHadoopCluster() {

		if (isCluster) {
			return isCluster;
		}

		if (MDPropertyWebServiceStep.isSpoon()) {
			return isCluster = false;
		}

		File clusterProp = new File("mdProps.prop");

		if (clusterProp.exists()) {
			isCluster = true;
		}

		return isCluster;
	}

	@Override
	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {

		List<String>     passThru = passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH);
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

		for (String key : propertyWebServiceFields.outputFields.keySet()) {

			if (propertyWebServiceFields.included(key) && !Const.isEmpty(propertyWebServiceFields.outputFields.get(key).metaValue) && !key.equals(PropertyWebServiceFields.TAG_OUTPUT_RESULTS)) {

				getStringField(row, originName, space, propertyWebServiceFields.outputFields.get(key).metaValue, propertyWebServiceFields.outputFields.get(key).metaSize);
			}
		}

		// Result codes
		if (!Const.isEmpty(propertyWebServiceFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RESULTS).metaValue)) {
			getStringField(row, originName, space, propertyWebServiceFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RESULTS).metaValue, propertyWebServiceFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RESULTS).metaSize);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getOptionalStreams()
	 */
	@Override
	public List<StreamInterface> getOptionalStreams() {

		return MDStreamHandler.getOptionalStreams(oFilterFields.filterTargets);
	}

	public VariableSpace getSpace() {

		return space;
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {

		setSpace(transMeta.getParentVariableSpace());
		return new MDPropertyWebServiceStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	// FIXME REPOSITORY get set

	@Override
	public StepDataInterface getStepData() {

		if (propertyWebServiceData == null) {
			propertyWebServiceData = new MDPropertyWebServiceData();
		}
		return propertyWebServiceData;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getStepIOMeta()
	 */
	@Override
	public StepIOMetaInterface getStepIOMeta() {

		if (ioMeta == null) {
			ioMeta = new StepIOMeta(true, false, false, false, false, true);
			// Add filter targets
			MDStreamHandler.getStepIOMeta(ioMeta, oFilterFields.filterTargets);
		}
		return ioMeta;
	}

	@Override
	public String getXML() throws KettleException {

		// Save data to XML
		StringBuilder retval = new StringBuilder(200);
		String        tab    = "      ";
		retval.append(tab).append(XMLHandler.openTag(PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OPTIONS)).append(Const.CR);
		for (String key : propertyWebServiceFields.optionFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, propertyWebServiceFields.optionFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OPTIONS)).append(Const.CR);

		retval.append(tab).append(XMLHandler.openTag(PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_INPUT)).append(Const.CR);
		for (String key : propertyWebServiceFields.inputFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, propertyWebServiceFields.inputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_INPUT)).append(Const.CR);

		retval.append(tab).append(XMLHandler.openTag(PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OUTPUT)).append(Const.CR);
		for (String key : propertyWebServiceFields.outputFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, propertyWebServiceFields.outputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OUTPUT)).append(Const.CR);

		retval.append(tab).append(XMLHandler.openTag(PassThruFields.TAG_PROPERTY_WEB_SERVICE_FILTER)).append(Const.CR);
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT) != null) {
			retval.append(tab).append("  ").append(addTagValue(PassThruFields.TAG_FILTER_OUT, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT))));
		}
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH) != null) {
			retval.append(tab).append("  ").append(addTagValue(PassThruFields.TAG_PASS_THROUGH, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH))));
		}
		retval.append(tab).append(XMLHandler.closeTag(PassThruFields.TAG_PROPERTY_WEB_SERVICE_FILTER)).append(Const.CR);

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

	@Override
	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {

		// OPTIONS
		String tag = PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OPTIONS;
		for (String key : propertyWebServiceFields.optionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, tag + "." + key, propertyWebServiceFields.optionFields.get(key).metaValue);
		}

		// INPUT
		tag = PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_INPUT;
		for (String key : propertyWebServiceFields.inputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, tag + "." + key, propertyWebServiceFields.inputFields.get(key).metaValue);
		}

		// OUTPUT
		tag = PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OUTPUT;
		for (String key : propertyWebServiceFields.outputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, tag + "." + key, propertyWebServiceFields.outputFields.get(key).metaValue);
		}

		tag = ""; //clear it 
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT) != null) {
			rep.saveStepAttribute(idTransformation, idStep, PassThruFields.TAG_FILTER_OUT, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT)));
		}
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH) != null) {
			rep.saveStepAttribute(idTransformation, idStep, PassThruFields.TAG_PASS_THROUGH, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH)));
		}

		tag = OutputFilterFields.TAG_FILTER + ".";
		int i = 0;
		if ((oFilterFields != null) && (oFilterFields.filterTargets != null)) {
			for (FilterTarget target : oFilterFields.filterTargets) {
				rep.saveStepAttribute(idTransformation, idStep, i, tag + OutputFilterFields.TAG_FILTER_NAME, target.getName());
				rep.saveStepAttribute(idTransformation, idStep, i, tag + OutputFilterFields.TAG_FILTER_RULE, target.getRule());
				rep.saveStepAttribute(idTransformation, idStep, i, tag + OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null);
				i++;
			}
		}
	}

	@Override
	public void handleStreamSelection(StreamInterface stream) {

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

	public boolean isUpdateData() {

		return updateData;
	}

	/**
	 * Called to load the global properties
	 *
	 * @throws KettleException
	 */
	public void loadGlobal() throws KettleException {

		String fileSep = System.getProperty("file.separator");
		File   mdDir   = new File(Const.getKettleDirectory() + fileSep + "MD");
		logBasic("MD objects location : " + mdDir.getAbsolutePath());
		checkContactZone();
		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				MDPropertyWebServiceStep.setIsSpoon(true);
			} else {
				MDPropertyWebServiceStep.setIsSpoon(false);
			}
		}

		//enterprise = true;

		if (MDPropertyWebServiceStep.isSpoon() && !isContactZone) {
			logBasic("Checking Pentaho Plugin Install ");
			moveTmp();
			if (!mdDir.exists()) {
				pluginInstaller = new PluginInstaller(isContactZone, log);
				pluginInstaller.doInstall(false, false, getCurrentPluginVersion());
				MDProps.load();
			} else {
				setUpdateData(checkUpdate());
				if (MDPropertyWebServiceStep.isSpoon() && isUpdateData()) {
					pluginInstaller = new PluginInstaller(isContactZone, log);
					pluginInstaller.doInstall(true, isUpdateData(), getCurrentPluginVersion());
				}
			}
		} else if (MDPropertyWebServiceStep.isSpoon() && isContactZone) {
			pluginInstaller = new PluginInstaller(isContactZone, log);
			pluginInstaller.checkCZInstall(getInstalledPluginVersion());
		}

		checkHadoopCluster();
		if (isCluster) {
			// sort of a hack normal method to check enterprise
			// does not work on the cluster so if it is on a hadoop cluster
			// we assume it is enterprise.
			//enterprise = true;
		}

		if (!isContactZone) {
			loadClassFiles();
		}
		MDProps.load();
		logBasic("Running Spoon = " + MDPropertyWebServiceStep.isSpoon());
		if (isEnterprise()) {
			logBasic("Mode = Enterprise Edition");
		} else {
			logBasic("Mode = Community Edition");
		}
	}

	private boolean checkContactZone() {

		File checkFile = new File("ui" + Const.FILE_SEPARATOR + TAG_CONTACT_ZONE_FILE);
		isContactZone = checkFile.exists();
		logBasic("ContactZone check file : " + checkFile.getAbsolutePath());
		logBasic("ContactZone = " + isContactZone);
		return isContactZone;
	}

	public String getTransmissionReference() {

		String product = isContactZone ? "CZ" : "PENTAHO";
		String version = getVersionFromManifest();

		String transRef = "mdSrc:{product:" + product + ";version:" + version + "}";
		return transRef;
	}

	private String getVersionFromManifest() {

		Class<?> clazz     = this.getClass();
		String   className = clazz.getSimpleName() + ".class";
		String   classPath = clazz.getResource(className).toString();
		String   version   = "0";
		if (!classPath.startsWith("jar")) {
			version = "0";
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest    = new Manifest(new URL(manifestPath).openStream());
				String   implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
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

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {

		setDefault();
		// OPTIONS
		List<Node> nodes = XMLHandler.getNodes(stepnode, PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OPTIONS);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : propertyWebServiceFields.optionFields.keySet()) {
				propertyWebServiceFields.optionFields.get(key).metaValue = safe(getTagValue(tempNode, key), propertyWebServiceFields.optionFields.get(key).metaValue);
			}
		}
		// INPUT FIELDS
		nodes = XMLHandler.getNodes(stepnode, PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_INPUT);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : propertyWebServiceFields.inputFields.keySet()) {
				propertyWebServiceFields.inputFields.get(key).metaValue = safe(getTagValue(tempNode, key), propertyWebServiceFields.inputFields.get(key).metaValue);
			}
		}
		// OUTPUT FIELDS
		nodes = XMLHandler.getNodes(stepnode, PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OUTPUT);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : propertyWebServiceFields.outputFields.keySet()) {
				propertyWebServiceFields.outputFields.get(key).metaValue = safe(getTagValue(tempNode, key), propertyWebServiceFields.outputFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(stepnode, PassThruFields.TAG_PROPERTY_WEB_SERVICE_FILTER);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node         tempNode  = nodes.get(0);
			List<String> passThru  = toList(getTagValue(tempNode, PassThruFields.TAG_PASS_THROUGH));
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
			Node         dupNode = XMLHandler.getSubNodeByNr(dupNodes, OutputFilterFields.TAG_FILTER, i);
			FilterTarget target  = new FilterTarget();
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

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	@Override
	public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {

		setDefault();
		// OPTIONS
		String prefix = PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OPTIONS;
		for (String key : propertyWebServiceFields.optionFields.keySet()) {
			propertyWebServiceFields.optionFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, prefix + "." + key), propertyWebServiceFields.optionFields.get(key).metaValue);
		}
		// INPUT FIELDS
		prefix = PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_INPUT;
		for (String key : propertyWebServiceFields.inputFields.keySet()) {
			propertyWebServiceFields.inputFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, prefix + "." + key), propertyWebServiceFields.inputFields.get(key).metaValue);
		}

		// OUTPUT FIELDS
		prefix = PropertyWebServiceFields.TAG_PROPERTY_WEB_SERVICE_OUTPUT;
		for (String key : propertyWebServiceFields.outputFields.keySet()) {
			propertyWebServiceFields.outputFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, prefix + "." + key), propertyWebServiceFields.outputFields.get(key).metaValue);
		}
		List<String> passThru  = toList(rep.getStepAttributeString(idStep, PassThruFields.TAG_PASS_THROUGH));
		List<String> filterOut = toList(rep.getStepAttributeString(idStep, PassThruFields.TAG_FILTER_OUT));
		if (passThru != null) {
			passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH).addAll(passThru);
		}
		if (filterOut != null) {
			passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT).addAll(filterOut);
		}

		oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue = safe(rep.getStepAttributeString(idStep, OutputFilterFields.TAG_RESULT_CODES), oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue);
		prefix = OutputFilterFields.TAG_FILTER;
		int nrFilters = rep.countNrStepAttributes(idStep, prefix + "." + OutputFilterFields.TAG_FILTER_NAME);
		oFilterFields.filterTargets = new ArrayList<FilterTarget>(nrFilters);
		for (int i = 0; i < nrFilters; i++) {
			FilterTarget target = new FilterTarget();
			target.setName(rep.getStepAttributeString(idStep, i, prefix + "." + OutputFilterFields.TAG_FILTER_NAME));
			target.setRule(rep.getStepAttributeString(idStep, i, prefix + "." + OutputFilterFields.TAG_FILTER_RULE));
			target.setTargetStepname(rep.getStepAttributeString(idStep, i, prefix + "." + OutputFilterFields.TAG_FILTER_TARGET));
			oFilterFields.filterTargets.add(target);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#searchInfoAndTargetSteps(java.util.List)
	 */
	@Override
	public void searchInfoAndTargetSteps(List<StepMeta> steps) {

		// De-reference the filter target steps
		for (StreamInterface stream : getStepIOMeta().getTargetStreams()) {
			if (stream.getSubject() instanceof FilterTarget) {
				FilterTarget target   = (FilterTarget) stream.getSubject();
				StepMeta     stepMeta = StepMeta.findStep(steps, target.getTargetStepname());
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
	public void selectValues(MDPropertyWebServiceStep check, MDPropertyWebServiceMeta meta, MDPropertyWebServiceData data, List<MDPropertyWebServiceRequest> requests, IOMetaHandler ioMeta) {

		// For each request...
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDPropertyWebServiceRequest request   = requests.get(recordID);
			RowMetaInterface            inputMeta = request.inputMeta;
			Object[]                    inputData = request.inputData;
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

	@Override
	public void setDefault() {

		if (passThruFields == null) {
			passThruFields = new PassThruFields();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
		}
		if (propertyWebServiceFields == null) {
			propertyWebServiceFields = new PropertyWebServiceFields();
		}
		oFilterFields.init();
		passThruFields.init();
		propertyWebServiceFields.init();
	}

	public void setSpace(VariableSpace space) {

		this.space = space;
	}

	public void setUpdateData(boolean updateData) {

		this.updateData = updateData;
	}

	@Override
	public boolean supportsErrorHandling() {

		return true;
	}

	private boolean checkUpdate() {

		if (isUpdateData()) {
			// If this is already set just return otherwise
			// we will get a a false result.
			return isUpdateData();
		}

		String insVer     = getInstalledPluginVersion();
		String curVersion = getCurrentPluginVersion();
		if (insVer.equals(curVersion)) {
			logBasic("MDProperty version is up to date : " + insVer);
			return false;
		} else {
			logBasic("MDProperty version update detected. Installed " + insVer + " updating to version " + curVersion);
			return true;
		}
	}

	private String getCurrentPluginVersion() {

		String                 fileSep     = Const.FILE_SEPARATOR;
		File                   srcDir      = getSrcDir();
		String                 version     = "";
		String                 versionPath = srcDir + fileSep + "version.xml";
		DocumentBuilderFactory dbf         = DocumentBuilderFactory.newInstance();
		FileReader             reader      = null;
		try {
			File file = new File(versionPath);
			if (!file.exists()) {
				version = "";
			}
			DocumentBuilder db = dbf.newDocumentBuilder();
			reader = new FileReader(versionPath);
			Document dom             = db.parse(new InputSource(reader));
			NodeList versionElements = dom.getElementsByTagName("version");
			if (versionElements.getLength() >= 1) {
				Element versionElement = (Element) versionElements.item(0);
				String  build          = versionElement.getAttribute("buildId");
				version = versionElement.getTextContent() + "_" + build;
			}
		} catch (Exception e) {
			System.out.println("Error reading version: " + e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				System.out.println("Error closing version file: " + e.getMessage());
			}
		}
		return version;
	}

	private String getInstalledPluginVersion() {

		String ver = "0";
		if (Props.isInitialized()) {
			ver = Props.getInstance().getProperty(TAG_PROPERTY_WEB_SERVICE_VERSION);
			if (ver == null) {
				ver = "0";
			}
		}
		return ver;
	}

	private File getSrcDir() {

		String fileSep     = Const.FILE_SEPARATOR;
		String decodedPath = "";
		File   ssdd        = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error getting source dir: " + e.getMessage());
		}
		return new File(decodedPath);
	}

	public String getGroups() {

		return propertyWebServiceFields.getGroups();
	}

	private void loadClassFiles() throws KettleException {

		boolean loadExt    = true;
		File    libext_dir = null;

		if (!isCluster) {
			libext_dir = new File(Const.getKettleDirectory(), "MD" + Const.FILE_SEPARATOR + "libext");
		} else {
			String path    = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			String libPath = path.substring(0, path.lastIndexOf("/"));
			libext_dir = new File(libPath, "MD" + Const.FILE_SEPARATOR + "libext");
		}

		if (libext_dir.exists()) {
			URLClassLoader spoonLoader = (URLClassLoader) KettleVFS.class.getClassLoader();
			URL[]          urls        = spoonLoader.getURLs();
			for (URL url : urls) {
				if (url.toString().contains("MDSettings")) {
					loadExt = false;
				}
			}

			if (loadExt) {
				Class<URLClassLoader> sysClass = URLClassLoader.class;
				Method                sysMethod;
				try {
					sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
					sysMethod.setAccessible(true);
					for (File file : libext_dir.listFiles()) {
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

		Collection<File> files  = null;
		File             tmpDir = new File(new File(Const.getKettleDirectory()), "tmp");
		if (!tmpDir.exists()) {
			return;
		}
		String fileSep   = Const.FILE_SEPARATOR;
		File   kettleDir = new File(Const.getKettleDirectory());
		File   md_dir    = new File(kettleDir, "MD");
		File   dest32    = new File(kettleDir, "MD" + fileSep + "32_bit");
		File   dest64    = new File(kettleDir, "MD" + fileSep + "64_bit");
		File   tmp32bit  = new File(tmpDir, "32_bit");
		File   tmp64bit  = new File(tmpDir, "64_bit");
		File   tmpLibext = new File(tmpDir, "libext");
		if (tmpLibext.exists()) {
			logBasic("Moving libext tmp files ");
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
			logBasic("Moving 32_bit object files ");
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
			logBasic("Moving 64_bit object files ");
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

	private boolean specialUsage() {
		// used to allow Melissadata usage with out EE lic
//		File tstFile = new File(Const.getKettleDirectory(), "md_usage.prop");
//		return tstFile.exists();
		return Boolean.valueOf(MDProps.getProperty(TAG_MELISSADATA_SPECIAL_USAGE, "false"));
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
}
