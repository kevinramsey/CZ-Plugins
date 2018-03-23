package com.melissadata.kettle.personator;

import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.kettle.MDSettings.SettingsTags;
import com.melissadata.kettle.personator.data.*;
import com.melissadata.kettle.personator.mapping.FieldMapping;
import com.melissadata.kettle.personator.support.MDPropTags;
import com.melissadata.kettle.personator.support.MetaVal;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.pentaho.di.core.*;
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
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Step(id = "MDPersonatorPlugin", image = "com/melissadata/kettle/personator/images/PDI_MD_Personator_V1.svg", description = "MDPersonatorPlugin.FullStep.Description", name = "MDPersonatorPlugin.FullStep.Name", categoryDescription = "MDPersonatorPlugin.Category", i18nPackageName = "com.melissadata.kettle")

public class MDPersonatorMeta extends BaseStepMeta implements StepMetaInterface {
	public static final  String                      TAG_MELISSADATA_SPECIAL_USAGE  = "melissadata_special_usage";
	public static final  String                      TAG_PRIMARY_LICENSE_ENTERPRISE = "license_enterprise";
	private static final String                      TAG_PERSONATOR_VERSION         = "md_personator_version";
	private static final String                      TAG_CONTACT_ZONE_FILE          = "contact_zone.prp";
	private static final HashMap<Integer, RowOutput> outputGroups                   = new HashMap<Integer, RowOutput>();
	private static       Class<?>                    PKG                            = MDPersonatorMeta.class;
	public static final  String                      NOT_DEFINED                    = BaseMessages.getString(PKG, "MDPersonatorMeta.InputData.NotDefined");
	private static       PluginInstaller             pluginInstaller                = null;
	private static       OutputPhoneFormat           phoneFormat                    = null;
	public               PersonatorFields            personatorFields               = null;
	public               PersonatorConfigFields      personatorConfigFields         = null;
	public               PassThruFields              passThruFields                 = null;
	public               OutputFilterFields          oFilterFields                  = null;
	public               ReportingFields             reportFields                   = null;
	private              MDPersonatorData            data                           = null;
	private              FieldMapping                fieldMapping                   = null;
	private              boolean                     updateData                     = false;
	private              boolean                     isCluster                      = false;
	private              boolean                     isContactZone                  = false;

	public MDPersonatorMeta() throws KettleException, SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException, IllegalAccessException, InvocationTargetException {

		String fileSep = System.getProperty("file.separator");
		File mdDir = new File(Const.getKettleDirectory() + fileSep + "MD");

		log = this.getLog();
		logBasic("MD objects location : " + mdDir.getAbsolutePath());
		checkContactZone();

		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				MDPersonator.setIsSpoon(true);
			} else {
				MDPersonator.setIsSpoon(false);
			}
		}

		if (MDPersonator.isSpoon() && !isContactZone) {
			// Lets see if we need to install or update
			logBasic("Checking Pentaho Plugin Install ");
			moveTmp();
			if (!mdDir.exists()) {
				pluginInstaller = new PluginInstaller(isContactZone, log);
				pluginInstaller.doInstall(false, false, getVersionFromManifest());
				setUpdateData(true);
			} else {
				setUpdateData(checkUpdate());
				if (MDPersonator.isSpoon() && isUpdateData()) {
					pluginInstaller = new PluginInstaller(isContactZone, log);
					pluginInstaller.doInstall(true, isUpdateData(), getVersionFromManifest());
				} else if (!MDPersonator.isSpoon() && isUpdateData()) {
					// There is an external tool to install on systems
					// without a gui.
				}
			}
		} else if (MDPersonator.isSpoon() && isContactZone) {
			pluginInstaller = new PluginInstaller(isContactZone, log);
			pluginInstaller.checkCZInstall(getInstalledPluginVersion());
		}

		// See if we are running on a hadoop cluster
		checkHadoopCluster();


		if (!isContactZone) {
			loadClassFiles(isCluster);
		}
		MDProps.load();
		DQTObjectFactory.setLogLevel(log.getLogLevel());

		logBasic("Running Spoon = " + MDPersonator.isSpoon());

		if (isEnterprise()) {
			logBasic("Mode = Enterprise Edition");
		} else {
			logBasic("Mode = Community Edition");
		}
	}

	/**
	 * Wraps the XMLHandler.addTagValue(String, String) method.
	 * If the value is a zero-length string then it will be padded to one character. This is done so that the
	 * generated XML will have some value instead of the null that is currenly returned when it is read back in.
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

	public static HashMap<Integer, RowOutput> getOutputgroups() {
		return outputGroups;
	}

	public static OutputPhoneFormat getPhoneFormat() {
		return phoneFormat;
	}

	public void setPhoneFormat(OutputPhoneFormat phoneFormat) {
		MDPersonatorMeta.phoneFormat = phoneFormat;
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
		if (fieldValue.trim().length() == 0)
			return;
		// Build meta description of field
		ValueMetaInterface v = new ValueMeta(space.environmentSubstitute(fieldValue), ValueMetaInterface.TYPE_STRING);
		v.setOrigin(originName);
		v.setLength(fieldLength);
		row.addValueMeta(v);
	}

	/**
	 * Warps the XMLHandler.getTagValue(Node, String) method.
	 * Reverses the special handling done in the above addTagValue() method by triming values before returning them.
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
		return MDProps.getProperty(SettingsTags.TAG_PRIMARY_PRODUCT, "").contains(SettingsTags.MDLICENSE_PRODUCT_Personator) || MDProps.getProperty(SettingsTags.TAG_PRIMARY_PRODUCT, "").contains(SettingsTags.MDLICENSE_PRODUCT_Any);
	}

	private boolean checkContactZone() {
		File checkFile = new File("ui" + Const.FILE_SEPARATOR + TAG_CONTACT_ZONE_FILE);
		isContactZone = checkFile.exists();
		logBasic("ContactZone check file : " + checkFile.getAbsolutePath());
		logBasic("ContactZone = " + isContactZone);
		return isContactZone;
	}

	public boolean checkHadoopCluster() {

		if (isCluster) {
			return isCluster;
		}

		if (MDPersonator.isSpoon()) {
			return isCluster = false;
		}

		File clusterProp = new File("mdProps.prop");
		if (clusterProp.exists()) {
			isCluster = true;
		}
		return isCluster;
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
	@Override public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// See if we have an input stream leading to this step!
		if (input.length == 0) {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, getCheckString("NoInputReceivedError"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		} else if (input.length > 1) {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, getCheckString("MultipleInputReceivedError"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		} else {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, getCheckString("InputOK"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		}
		// check pass through fields
		passThruFields.check(remarks, transMeta, stepMeta, prev, input, output, info);
	}

	public String getTransmissionReference(){

		String product = isContactZone ? "CZ":"PENTAHO";
		String version = getVersionFromManifest();

		String transRef = "mdSrc:{product:" + product + ";version:" + version + "}";
		return transRef;
	}

	private boolean checkUpdate() {
		if (isUpdateData()) {
			// If this is already set just return otherwise
			// we will get a a false result.
			return isUpdateData();
		}

		String insVer     = getInstalledPluginVersion();
		String curVersion = getVersionFromManifest();
		if (insVer.equals(curVersion)) {
			logBasic("MDPersonator version is up to date : " + insVer);
			return false;
		} else {
			logBasic("MDPersonator version update detected. Installed " + insVer + " updating to version " + curVersion);
			return true;
		}
	}

	private String getVersionFromManifest() {
		Class<?> clazz = this.getClass();
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		String version = "0";
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

	private String getInstalledPluginVersion() {
		String ver = "0";
		if (Props.isInitialized()) {
			ver = Props.getInstance().getProperty(TAG_PERSONATOR_VERSION);
			if (ver == null) {
				ver = "0";
			}
		}
		return ver;
	}

	private void clearGroupsAdded() {
		for (int i = 0; i < outputGroups.size(); i++) {
			outputGroups.get(i).isAdded = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#excludeFromCopyDistributeVerification()
	 */
	@Override public boolean excludeFromCopyDistributeVerification() {
		return true;
	}

	/**
	 * @param name
	 * @param args
	 * @return
	 */
	private String getCheckString(String name, String... args) {
		return BaseMessages.getString(PKG, "MDPersonatorMeta.CheckResult." + name, args);
	}

	public MDPersonatorData getData() {
		return data;
	}

	public void setData(MDPersonatorData data) {
		this.data = data;
	}

	public FieldMapping getFieldMapping() {
		if (fieldMapping == null) {
			fieldMapping = new FieldMapping();
		}
		return fieldMapping;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getFields(org.pentaho.di.core.row.RowMetaInterface, java.lang.String,
	 * org.pentaho.di.core.row.RowMetaInterface[], org.pentaho.di.trans.step.StepMeta,
	 * org.pentaho.di.core.variables.VariableSpace)
	 */
	@Override public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
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
		HashMap<Integer, RowOutput> grpList = MDPersonatorMeta.getOutputgroups();
		RowOutput                   ro;
		for (int n = 0; n < grpList.size(); n++) {
			ro = grpList.get(n);
			if (ro.isAdded && (personatorFields.outputFields.get(ro.tag) != null)) {
				getStringField(row, originName, space, personatorFields.outputFields.get(ro.tag).metaValue, personatorFields.outputFields.get(ro.tag).metaSize);
			}
		}
		if (!Const.isEmpty(personatorFields.outputFields.get(PersonatorFields.TAG_OUTPUT_RESULTS).metaValue)) {
			getStringField(row, originName, space, personatorFields.outputFields.get(PersonatorFields.TAG_OUTPUT_RESULTS).metaValue, personatorFields.outputFields.get(PersonatorFields.TAG_OUTPUT_RESULTS).metaSize);
		}
	}

	private void getGroupsList() {
		outputGroups.clear();
		Class<?> clazz     = this.getClass();
		String   className = clazz.getSimpleName() + ".class";
		String   classPath = null;
		if ((clazz != null) && (className != null)) {
			classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			classPath = new File(classPath).getParent();
		}
		String       configFilePath = classPath + File.separator + "outGrp.txt";
		RowOutput    ro             = null;
		File         cfgFile        = new File(configFilePath);
		String[]     rowData;
		int          keyIndex       = 0;
		LineIterator it             = null;
		try {
			it = FileUtils.lineIterator(cfgFile);
			while (it.hasNext()) {
				ro = new RowOutput();
				String str = it.nextLine();
				if (!str.startsWith("#") && !Const.isEmpty(str)) {
					rowData = str.split(";");
					if (rowData.length == 4) {
						ro.groupName = rowData[0];
						ro.outputName = rowData[1];
						ro.groupDescription = rowData[2];
						ro.outputDescription = rowData[3];
						ro.index = keyIndex;
						ro.tag = "output_" + ro.outputName;
						ro.fieldName = "MD_" + ro.outputName;
						if ("Basic".equals(ro.groupName)) {
							ro.isAdded = true;
						}
						outputGroups.put(keyIndex, ro);
						keyIndex++;
					} else {
						//System.out.println(" BAD LENgth = " + rowData.length);
					}
				}
			}
		} catch (IOException e) {
			logError("Error reading output group config file -" + e.getMessage());
		} finally {
			it.close();
		}
	}

	public String[] getInputs() {
		String[] inputStrings = new String[personatorFields.inputFields.size()];
		int      counter      = 0;
		for (String key : personatorFields.inputFields.keySet()) {
			inputStrings[counter] = personatorFields.inputFields.get(key).metaValue;
			counter++;
		}
		return inputStrings;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getOptionalStreams()
	 */
	@Override public List<StreamInterface> getOptionalStreams() {
		return MDStreamHandler.getOptionalStreams(oFilterFields.filterTargets);
	}

//	private File getSrcDir() {
//		String fileSep     = Const.FILE_SEPARATOR;
//		String decodedPath = "";
//		File   ssdd        = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
//		try {
//			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
//			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
//		} catch (UnsupportedEncodingException e) {
//			System.out.println("Error getting source dir: " + e.getMessage());
//		}
//		return new File(decodedPath);
//	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStep(org.pentaho.di.trans.step.StepMeta,
	 * org.pentaho.di.trans.step.StepDataInterface, int, org.pentaho.di.trans.TransMeta, org.pentaho.di.trans.Trans)
	 */
	@Override public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		return new MDPersonator(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStepData()
	 */
	@Override public StepDataInterface getStepData() {
		return new MDPersonatorData();
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

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getXML()
	 */
	@Override public String getXML() throws KettleException {
		// Save data to XML
		StringBuilder retval = new StringBuilder(200);
		String        tab    = "      ";
		String optValString = "";
		retval.append(tab).append(XMLHandler.openTag(PersonatorFields.TAG_PERSONATOR_OPTIONS)).append(Const.CR);
		for (String key : personatorFields.optionFields.keySet()) {
			// hack to deal with ":" which causes error on MAC. The ":" should have never
			// been written but since it is used elsewhere we will just strip it here for storage purposes.
			optValString = personatorFields.optionFields.get(key).metaValue;
			if(key.endsWith(":")){
				key = key.substring(0,key.length() - 1);
			}
			//retval.append(tab).append("  ").append(addTagValue(key, personatorFields.optionFields.get(key).metaValue));
			retval.append(tab).append("  ").append(addTagValue(key, optValString));

		}
		retval.append(tab).append(XMLHandler.closeTag(PersonatorFields.TAG_PERSONATOR_OPTIONS)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(PersonatorFields.TAG_PERSONATOR_INPUT)).append(Const.CR);
		for (String key : personatorFields.inputFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, personatorFields.inputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PersonatorFields.TAG_PERSONATOR_INPUT)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(PersonatorFields.TAG_PERSONATOR_OUTPUT)).append(Const.CR);
		for (String key : personatorFields.outputFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, personatorFields.outputFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PersonatorFields.TAG_PERSONATOR_OUTPUT)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(PersonatorConfigFields.TAG_PERSONATOR_CONFIG)).append(Const.CR);
		for (String key : personatorConfigFields.configFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, personatorConfigFields.configFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(PersonatorConfigFields.TAG_PERSONATOR_CONFIG)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(PassThruFields.TAG_GLOBAL_FILTER)).append(Const.CR);
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT) != null) {
			retval.append(tab).append("  ").append(MDPersonatorData.addTagValue(PassThruFields.TAG_FILTER_OUT, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT))));
		}
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH) != null) {
			retval.append(tab).append("  ").append(MDPersonatorData.addTagValue(PassThruFields.TAG_PASS_THROUGH, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH))));
		}
		retval.append(tab).append(XMLHandler.closeTag(PassThruFields.TAG_GLOBAL_FILTER)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(OutputFilterFields.TAG_OUTPUT_FILTER)).append(Const.CR);
		retval.append(tab).append("  ").append(XMLHandler.openTag(OutputFilterFields.TAG_FILTERS));
		if ((oFilterFields != null) && (oFilterFields.filterTargets != null)) {
			for (FilterTarget target : oFilterFields.filterTargets) {
				retval.append(tab).append("    ").append(XMLHandler.openTag(OutputFilterFields.TAG_FILTER));
				retval.append(tab).append("    ").append(MDPersonatorData.addTagValue(OutputFilterFields.TAG_FILTER_NAME, target.getName()));
				retval.append(tab).append("    ").append(MDPersonatorData.addTagValue(OutputFilterFields.TAG_FILTER_RULE, target.getRule()));
				retval.append(tab).append("    ").append(MDPersonatorData.addTagValue(OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null));
				retval.append(tab).append("    ").append(XMLHandler.closeTag(OutputFilterFields.TAG_FILTER));
			}
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(OutputFilterFields.TAG_FILTERS));
		retval.append(tab).append(XMLHandler.closeTag(OutputFilterFields.TAG_OUTPUT_FILTER)).append(Const.CR);
		return retval.toString();
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

	public boolean isLicensed() {
		int retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RETVAL, "0"));
		//retVal |= Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RETVAL, "0"));
		if ((retVal & SettingsTags.MDLICENSE_Personator) != 0 || (retVal & SettingsTags.MDLICENSE_Community) != 0)
			return true;
		else
			return false;
	}

	private void loadClassFiles(boolean isCluster) throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException, IllegalAccessException, InvocationTargetException {

		boolean loadExt = true;
		File    lib     = null;
		if (!isCluster)
			lib = new File(Const.getKettleDirectory(), "MD" + Const.FILE_SEPARATOR + "libext");
		else {
			String path    = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			String libPath = path.substring(0, path.lastIndexOf("/"));
			lib = new File(libPath, "MD" + Const.FILE_SEPARATOR + "libext");
		}

		//TODO is there a better way to check if our jars are loaded
		if (lib.exists()) {
			URLClassLoader spoonLoader = (URLClassLoader) KettleVFS.class.getClassLoader();
			URL[]          urls        = spoonLoader.getURLs();
			for (URL url : urls) {
				if (url.toString().contains("MDSettings")) {
					loadExt = false;
				}
			}

			// FIXME filter files
			if (loadExt) {
//				String jars = "";
				Class<URLClassLoader> sysClass  = URLClassLoader.class;
				Method                sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
				sysMethod.setAccessible(true);
				for (File file : lib.listFiles()) {
					if (!file.getName().endsWith("crc")) {
//						jars += file.getName() + ";";
						sysMethod.invoke(spoonLoader, new Object[] { file.toURI().toURL() });
					}
				}
			}
		} else {
			log.logError("Faild to locate MD libext jar files at :" + lib.getAbsolutePath());
		}
	}

	/**
	 * Called to read meta data from a document node
	 *
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	//FIXME loadXML depreciated use loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore )
	@Override public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		if (personatorFields == null) {
			personatorFields = new PersonatorFields();
			personatorFields.init(this);
		}
		if (personatorConfigFields == null) {
			personatorConfigFields = new PersonatorConfigFields();
			personatorConfigFields.init();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
			oFilterFields.init();
		}
		if (passThruFields == null) {
			passThruFields = new PassThruFields();
			passThruFields.init();
		}
		setDefault();
		List<Node> nodes = XMLHandler.getNodes(stepnode, PersonatorFields.TAG_PERSONATOR_OPTIONS);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node   tempNode     = nodes.get(0);
			String optValString = "";
			String altKey       = "";
			for (String key : personatorFields.optionFields.keySet()) {
				optValString = getTagValue(tempNode, key);
				// hack to deal with ":" which causes error on MAC. The ":" should have never
				// been written but since it was for a long time we need to account for older versions
				if (optValString == null && key.endsWith(":")) {
					altKey = key.substring(0, key.length() - 1);
					optValString = getTagValue(tempNode, altKey);
				}
				personatorFields.optionFields.get(key).metaValue = safe(optValString, personatorFields.optionFields.get(key).metaValue);

				//personatorFields.optionFields.get(key).metaValue = safe(getTagValue(tempNode, key), personatorFields.optionFields.get(key).metaValue);
				if (PersonatorFields.TAG_OPTION_PHONE_FORMAT.equals(key)) {
					String value = getTagValue(tempNode, key);
					try {
						phoneFormat = (value != null) ? OutputPhoneFormat.decode(value) : phoneFormat;
					} catch (KettleException e) {
						logError("Error decoding phoneFormat-" + e.getMessage());
					}
				}
			}
		}
		nodes = XMLHandler.getNodes(stepnode, PersonatorConfigFields.TAG_PERSONATOR_CONFIG);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : personatorConfigFields.configFields.keySet()) {
				personatorConfigFields.configFields.get(key).metaValue = safe(getTagValue(tempNode, key), personatorConfigFields.configFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(stepnode, PersonatorFields.TAG_PERSONATOR_INPUT);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : personatorFields.inputFields.keySet()) {
				personatorFields.inputFields.get(key).metaValue = safe(getTagValue(tempNode, key), personatorFields.inputFields.get(key).metaValue);
			}
		}
		// reset all isAdded to false
		clearGroupsAdded();
		nodes = XMLHandler.getNodes(stepnode, PersonatorFields.TAG_PERSONATOR_OUTPUT);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node      tempNode = nodes.get(0);
			NodeList  ndList   = tempNode.getChildNodes();
			String    fName    = "";
			String    val      = "";
			RowOutput ro       = null;
			personatorFields.outputFields.clear();
			for (int i = 0; i < ndList.getLength(); i++) {
				fName = ndList.item(i).getNodeName().toString().trim();
				val = ndList.item(i).getTextContent().toString().trim();
				if (val.length() > 0) {
					for (int n = 0; n < outputGroups.size(); n++) {
						ro = outputGroups.get(n);
						if (ro.tag.equals(fName)) {
							ro.isAdded = true;
							ro.fieldName = val;
							personatorFields.outputFields.put(ro.tag, new MetaVal(ro.fieldName, ro.outputName, 50));
						}
					}
				}
			}
			personatorFields.outputFields.put(PersonatorFields.TAG_OUTPUT_RESULTS, new MetaVal(safe(getTagValue(tempNode, PersonatorFields.TAG_OUTPUT_RESULTS), "MD_Results"), "Results", 75));
		}
		nodes = XMLHandler.getNodes(stepnode, ReportingFields.TAG_REPORT_GLOBAL_REPORTS);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : reportFields.reportFields.keySet()) {
				reportFields.reportFields.get(key).metaValue = safe(getTagValue(tempNode, key), reportFields.reportFields.get(key).metaValue);
			}
		}
		nodes = XMLHandler.getNodes(stepnode, PassThruFields.TAG_GLOBAL_FILTER);
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

	private void moveTmp() {
		Collection<File> files  = null;
		File             tmpDir = new File(new File(Const.getKettleDirectory()), "tmp");
		if (!tmpDir.exists())
			return;
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

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	@Override public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {
		if (personatorFields == null) {
			personatorFields = new PersonatorFields();
			personatorFields.init(this);
		}
		if (passThruFields == null) {
			passThruFields = new PassThruFields();
			passThruFields.init();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
			oFilterFields.init();
		}
		if (personatorConfigFields == null) {
			personatorConfigFields = new PersonatorConfigFields();
			personatorConfigFields.init();
		}
		setDefault();
		for (String key : personatorFields.optionFields.keySet()) {
			personatorFields.optionFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, PersonatorFields.TAG_PERSONATOR_OPTIONS + "." + key), personatorFields.optionFields.get(key).metaValue);
			if (PersonatorFields.TAG_OPTION_PHONE_FORMAT.equals(key)) {
				String value = safe(rep.getStepAttributeString(idStep, PersonatorFields.TAG_PERSONATOR_OPTIONS + "." + key), personatorFields.optionFields.get(key).metaValue);
				try {
					phoneFormat = (value != null) ? OutputPhoneFormat.decode(value) : phoneFormat;
				} catch (KettleException e) {
					logError("Error decoding phoneFormat-" + e.getMessage());
				}
			}
		}
		for (String key : personatorFields.inputFields.keySet()) {
			personatorFields.inputFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, PersonatorFields.TAG_PERSONATOR_INPUT + "." + key), personatorFields.inputFields.get(key).metaValue);
		}
		String    val = "";
		RowOutput ro  = null;
		personatorFields.outputFields.clear();
		clearGroupsAdded();
		for (int i = 0; i < outputGroups.size(); i++) {
			ro = outputGroups.get(i);
			val = rep.getStepAttributeString(idStep, PersonatorFields.TAG_PERSONATOR_OUTPUT + "." + ro.tag);
			if (!Const.isEmpty(val)) {
				ro.isAdded = true;
				ro.fieldName = val;
				personatorFields.outputFields.put(ro.tag, new MetaVal(ro.fieldName, ro.outputName, 50));
			}
		}
		val = safe(rep.getStepAttributeString(idStep, PersonatorFields.TAG_PERSONATOR_OUTPUT + "." + PersonatorFields.TAG_OUTPUT_RESULTS), "MD_Results");
		personatorFields.outputFields.put(PersonatorFields.TAG_OUTPUT_RESULTS, new MetaVal(val, "Results", 75));
		for (String key : personatorConfigFields.configFields.keySet()) {
			personatorConfigFields.configFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, PersonatorConfigFields.TAG_PERSONATOR_CONFIG + "." + key), personatorConfigFields.configFields.get(key).metaValue);
		}
		for (String key : passThruFields.passFilterFields.keySet()) {
			passThruFields.passFilterFields.get(key).addAll(toList(rep.getStepAttributeString(idStep, PassThruFields.TAG_PASS_THROUGH + "." + key)));
		}
		for (String key : oFilterFields.filterFields.keySet()) {
			oFilterFields.filterFields.get(key).metaValue = rep.getStepAttributeString(idStep, OutputFilterFields.TAG_FILTER + "." + key);
		}
		int nrFilters = rep.countNrStepAttributes(idStep, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER_NAME);
		oFilterFields.filterTargets = new ArrayList<FilterTarget>(nrFilters);
		for (int i = 0; i < nrFilters; i++) {
			FilterTarget target = new FilterTarget();
			target.setName(rep.getStepAttributeString(idStep, i, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER_NAME));
			target.setRule(rep.getStepAttributeString(idStep, i, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER_RULE));
			target.setTargetStepname(rep.getStepAttributeString(idStep, i, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER_TARGET));
			oFilterFields.filterTargets.add(target);
		}
	}

	/**
	 * Called to save data to a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	@Override public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		for (String key : personatorFields.optionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, PersonatorFields.TAG_PERSONATOR_OPTIONS + "." + key, personatorFields.optionFields.get(key).metaValue);
		}
		for (String key : personatorFields.inputFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, PersonatorFields.TAG_PERSONATOR_INPUT + "." + key, personatorFields.inputFields.get(key).metaValue);
		}
		RowOutput ro  = null;
		String    val = null;
		for (int i = 0; i < outputGroups.size(); i++) {
			ro = outputGroups.get(i);
			if (ro.isAdded) {
				val = ro.fieldName;
			} else {
				val = "";
			}
			rep.saveStepAttribute(idTransformation, idStep, PersonatorFields.TAG_PERSONATOR_OUTPUT + "." + ro.tag, val);
		}
		rep.saveStepAttribute(idTransformation, idStep, PersonatorFields.TAG_PERSONATOR_OUTPUT + "." + PersonatorFields.TAG_OUTPUT_RESULTS, personatorFields.outputFields.get(PersonatorFields.TAG_OUTPUT_RESULTS).metaValue);
		for (String key : personatorConfigFields.configFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, PersonatorConfigFields.TAG_PERSONATOR_CONFIG + "." + key, personatorConfigFields.configFields.get(key).metaValue);
		}
		for (String key : passThruFields.passFilterFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, PassThruFields.TAG_PASS_THROUGH + "." + key, toString(passThruFields.passFilterFields.get(key)));
		}
		for (String key : oFilterFields.filterFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, OutputFilterFields.TAG_FILTER + "." + key, oFilterFields.filterFields.get(key).metaValue);
		}
		for (int i = 0; i < oFilterFields.filterTargets.size(); i++) {
			FilterTarget target = oFilterFields.filterTargets.get(i);
			rep.saveStepAttribute(idTransformation, idStep, i, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER_NAME, target.getName());
			rep.saveStepAttribute(idTransformation, idStep, i, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER_RULE, target.getRule());
			rep.saveStepAttribute(idTransformation, idStep, i, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null);
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
	public void selectValues(MDPersonator check, MDPersonatorMeta meta, MDPersonatorData data, List<MDPersonatorRequest> requests, IOMetaHandler ioMeta) {
		// For each request...
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDPersonatorRequest request   = requests.get(recordID);
			RowMetaInterface    inputMeta = request.inputMeta;
			Object[]            inputData = request.inputData;
			// Get the field values
			for (int index : ioMeta.passThruFieldNrs) {
				// Normally this can't happen, except when streams are mixed with different number of fields.
				if (index < inputMeta.size()) {
					Object value = inputData[index];
					request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, value);
				} else {
					if (check.isDetailed()) {
						check.logDetailed(BaseMessages.getString(PKG, "MDPersonator.Log.MixingStreamWithDifferentFields")); //$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * Called to initialized default values
	 */
	@Override public void setDefault() {
		getGroupsList();
		if (personatorFields == null) {
			personatorFields = new PersonatorFields();
		}
		if (passThruFields == null) {
			passThruFields = new PassThruFields();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
		}
		if (personatorConfigFields == null) {
			personatorConfigFields = new PersonatorConfigFields();
		}
		personatorConfigFields.init();
		setDefaultPassThruOptions();
		setDefaultOptions();
		setDefaultFilterOptions();
		setDefaultPassThruOptions();
	}

	public void setDefaultFilterOptions() {
		oFilterFields.init();
	}

	public void setDefaultOptions() {
		personatorFields.init(this);
	}

	public void setDefaultPassThruOptions() {
		passThruFields.init();
	}

	private boolean specialUsage() {
		// used to allow Melissadata usage with out EE lic
//		File tstFile = new File(Const.getKettleDirectory(), "md_usage.prop");
//		return tstFile.exists();
		return Boolean.valueOf(MDProps.getProperty(TAG_MELISSADATA_SPECIAL_USAGE, "false"));
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#supportsErrorHandling()
	 */
	@Override public boolean supportsErrorHandling() {
		return true;
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
		if (l.size() == 0)
			return null;
		StringBuffer sb  = new StringBuffer();
		String       sep = "";
		for (String e : l) {
			sb.append(sep).append(e);
			sep = ",";
		}
		return sb.toString();
	}

	public boolean isUpdateData() {
		return updateData;
	}

	public void setUpdateData(boolean updateData) {
		this.updateData = updateData;
	}

	public enum OutputPhoneFormat {
		FORMAT1(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Format1")), FORMAT2(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Format2")), FORMAT3(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Format3")), FORMAT4(
				BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Format4")), FORMAT5(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Format5")), FORMAT6(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Format6")),;
		private String format;

		private OutputPhoneFormat(String format) {
			this.format = format;
		}

		public static OutputPhoneFormat decode(String value) throws KettleException {
			try {
				return OutputPhoneFormat.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Unknown") + value, e);
			}
		}

		public String encode() {
			return name();
		}

		public String getFormat() {
			return format;
		}

		@Override public String toString() {
			return getFormat();
		}
	}


	public enum PersonatorActions {
		VERIFY(BaseMessages.getString(PKG, "MDPersonatorMeta.PersonatorActions.Verify")), APPEND(BaseMessages.getString(PKG, "MDPersonatorMeta.PersonatorActions.Append")), VERIFY_APPEND(
				BaseMessages.getString(PKG, "MDPersonatorMeta.PersonatorActions.VerifyAppend")),;
		private String action;

		private PersonatorActions(String format) {
			action = format;
		}

		public static PersonatorActions decode(String value) throws KettleException {
			try {
				return PersonatorActions.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PersonatorActions.Unknown") + value, e);
			}
		}

		public String encode() {
			return name();
		}

		public String getAction() {
			return action;
		}

		@Override public String toString() {
			return getAction();
		}
	}


	public class RowOutput {
		public int     index;
		public String  groupName;
		public String  groupDescription;
		public String  outputName;
		public String  outputDescription;
		public String  fieldName;
		public String  tag;
		public boolean isAdded;
	}
}
