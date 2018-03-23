package com.melissadata.kettle.cleanser;

import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.FilterTarget;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MDStreamHandler;
import com.melissadata.cz.support.OutputFilterFields;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cleanser.MDCleanserEnum.*;
import com.melissadata.kettle.cleanser.data.CleanserFields;
import com.melissadata.kettle.cleanser.request.MDCleanserRequest;
import com.melissadata.kettle.cleanser.support.PassThruFields;
import com.melissadata.kettle.cleanser.support.PluginInstaller;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogChannelInterface;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Step(id = "MDCleanserPlugin", image = "com/melissadata/kettle/cleanser/images/PDI_MD_GeneralCleanser_V1.svg", description = "MDCleanserPlugin.FullStep.Description", name = "MDCleanserPlugin.FullStep.Name", categoryDescription = "MDCleanserPlugin.Category", i18nPackageName = "com.melissadata.kettle")
public class MDCleanserMeta extends BaseStepMeta implements StepMetaInterface {

	public static final  String TAG_LOCAL_DATA_PATH                      = "data_path";
	public static final  String TAG_MELISSADATA_SPECIAL_USAGE            = "melissadata_special_usage";
	private static final String TAG_CONTACT_ZONE_FILE                    = "contact_zone.prp";
	private static final String TAG_CLEANSER_OPERATIONS                  = "md_cleanser_operations";
	private static final String TAG_CLEANSER_OPERATION                   = "md_cleanser_operation";
	private static final String TAG_CLEANSER_OPERATION_NAME              = "md_cleanser_operation_name";
	private static final String TAG_CLEANSER_OPERATION_FIELD             = "md_cleanser_operation_field";
	private static final String TAG_CLEANSER_OPERATION_PASS_THRU             = "md_cleanser_operation_pass_thru";
	private static final String TAG_CLEANSER_OPERATION_DESCRIPTION       = "md_cleanser_operation_description";
	private static final String TAG_CLEANSER_RULES_LIST                  = "md_cleanser_rules_list";
	private static final String TAG_CLEANSER_RULE                        = "md_cleanser_rule";
	private static final String TAG_CLEANSER_RULE_INDEX                  = "md_cleanser_rule_index";
	// ENUMS
	private static final String TAG_CLEANSER_RULE_OPERATION              = "md_cleanser_rule_operation";
	private static final String TAG_CLEANSER_RULE_CASING                 = "md_cleanser_rule_casing";
	private static final String TAG_CLEANSER_RULE_PUNCTION               = "md_cleanser_rule_punction";
	private static final String TAG_CLEANSER_RULE_ABBREVIATION           = "md_cleanser_rule_abbreviation";
	private static final String TAG_CLEANSER_RULE_FIELD_DATA             = "md_cleanser_rule_field_data";
	private static final String TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST    = "md_cleanser_rule_option_list";
	private static final String TAG_CLEANSER_RULE_CLEANSE_OPTION         = "md_cleanser_rule_option";
	private static final String TAG_CLEANSER_ABRIVIATE_TARGET_SIZE       = "md_cleanser_abbreviate_size";
	// Trigger
	private static final String TAG_CLEANSER_RULE_USE_TRIGGER            = "md_cleanser_rule_use_trigger";
	private static final String TAG_CLEANSER_RULE_USE_EXPRESSION_TRIGGER = "md_cleanser_rule_use_expression_trigger";
	private static final String TAG_CLEANSER_RULE_USE_REGEX_TRIGGER      = "md_cleanser_rule_use_regex_trigger";
	private static final String TAG_CLEANSER_RULE_EXPRESSION_TRIGGER     = "md_cleanser_rule_expression_trigger";
	private static final String TAG_CLEANSER_RULE_REGEX_TRIGGER          = "md_cleanser_rule_regex_trigger";
	// Reg Ex
	private static final String TAG_CLEANSER_RULE_REGEX_USE_EXPRESSION   = "md_cleanser_rule_regex_use_expression";
	private static final String TAG_CLEANSER_RULE_REGEX_SEARCH           = "md_cleanser_rule_regex_search";
	private static final String TAG_CLEANSER_RULE_REGEX_REPLACE          = "md_cleanser_rule_regex_replace";
	private static final String TAG_CLEANSER_RULE_REGEX_USE_TABLE        = "md_cleanser_rule_regex_use_table";
	private static final String TAG_CLEANSER_RULE_REGEX_TABLE            = "md_cleanser_rule_regex_table";
	// Search Replace Term
	private static final String TAG_CLEANSER_RULE_USE_SEARCH_TERM        = "md_cleanser_rule_use_search_term";
	private static final String TAG_CLEANSER_RULE_SEARCH_TERM            = "md_cleanser_rule_search_term";
	private static final String TAG_CLEANSER_RULE_SEARCH_REPLACE         = "md_cleanser_rule_search_replace";
	private static final String TAG_CLEANSER_RULE_USE_SEARCH_TABLE       = "md_cleanser_rule_use_search_table";
	private static final String TAG_CLEANSER_RULE_SEARCH_TABLE           = "md_cleanser_rule_search_table";
	// Expression
	private static final String TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION   = "md_cleanser_rule_transform_expression";
	private static final String TAG_CLEANSER_APPEND_FIELD                = "md_cleanser_append_field_names";
	private static final String TAG_CLEANSER_OPERATION_FILE              = "md_cleanser_operations.xml";
	private static final String TAG_CLEANSER_VERSION                     = "md_cleanser_version";
	private static PluginInstaller pluginInstaller;
	private static Class<?> PKG = MDCleanserMeta.class;
	public PassThruFields     passThruFields;
	public OutputFilterFields oFilterFields;
	public CleanserFields     cleanserFields;
	//	public static boolean			enterprise;
	public boolean isCluster = false;
	private VariableSpace  space;
	private MDCleanserData cleanserData;
	private boolean updateData = false;
	private LogChannelInterface log;
	private boolean isContactZone = false;

	public MDCleanserMeta() throws KettleException {
		super();
		log = super.getLog();
		loadGlobal();
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
		return MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_Cleanser) || MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_Any);
	}

	private static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

	public boolean checkHadoopCluster() {
		if (isCluster) {
			return isCluster;
		}
		if (MDCleanserStep.isSpoon()) {
			return isCluster = false;
		}
		/*
		 * If we are on a hadoop cluster our props
		 * file will be in our current dir.
		 */
		File clusterProp = new File("mdProps.prop");
		if (clusterProp.exists()) {
			isCluster = true;
		}
		return isCluster;
	}

	private boolean checkUpdate() {
		if (isUpdateData()) {
			// If this is already set just return otherwise
			// we will get a a false result.
			return isUpdateData();
		}

		if (!Props.isInitialized()) {
			System.out.println("props not initalized: skipping check update");
			return false;
		}
		String insVer     = getInstalledPluginVersion();
		String curVersion = getVersionFromManifest();

		if (insVer.equals(curVersion)) {
			log.logBasic("MDCleanser version is up to date : " + insVer);
			return false;
		} else {
			log.logBasic("MDCleanser version update detected. Installed " + insVer + " updating to version " + curVersion);
		}

		return true;
	}

	private String getInstalledPluginVersion() {
		String ver = "0";
		if (Props.isInitialized()) {
			ver = Props.getInstance().getProperty(TAG_CLEANSER_VERSION);
			if (ver == null) {
				ver = "0";
			}
		}
		return ver;
	}

	private String getVersionFromManifest() {
		Class<?> clazz     = this.getClass();
		String   className = clazz.getSimpleName() + ".class";
		String   classPath = clazz.getResource(className).toString();
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

	public boolean isUpdateData() {
		return updateData;
	}

	public void setUpdateData(boolean updateData) {
		this.updateData = updateData;
	}

	private File getJarFileDir() {
		String fileSep     = Const.FILE_SEPARATOR;
		String decodedPath = "";
		File   pluginDir   = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(pluginDir.toString(), "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
		} catch (UnsupportedEncodingException e) {
			logError("Error getting source dir: " + e.getMessage());
		}
		return new File(decodedPath);
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
		if (tmp32bit.exists() && dest32.exists()) {
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
			logBasic("Meta ext Class Files loaded = " + !loadExt);
			if (loadExt) {
				Class<URLClassLoader> sysClass = URLClassLoader.class;
				Method                sysMethod;
				try {
					sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
					sysMethod.setAccessible(true);
					for (File file : libext_dir.listFiles()) {
						logBasic("Meta Loading external Class File - " + file.getAbsolutePath());
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
				MDCleanserStep.setIsSpoon(true);
			} else {
				MDCleanserStep.setIsSpoon(false);
			}
		}
		if (MDCleanserStep.isSpoon() && !isContactZone) {
			logBasic("Checking Pentaho Plugin Install ");
			moveTmp();
			if (!mdDir.exists()) {
				setUpdateData(true);
				pluginInstaller = new PluginInstaller(isContactZone, log);
				pluginInstaller.doInstall(false, false, getVersionFromManifest());
				MDProps.load();
			} else {
				setUpdateData(checkUpdate());
				if (MDCleanserStep.isSpoon() && isUpdateData()) {
					pluginInstaller = new PluginInstaller(isContactZone, log);
					pluginInstaller.doInstall(true, isUpdateData(), getVersionFromManifest());
				}
			}
		} else if (MDCleanserStep.isSpoon() && isContactZone) {
			pluginInstaller = new PluginInstaller(isContactZone, log);
			pluginInstaller.checkCZInstall(getInstalledPluginVersion());
		}
		checkHadoopCluster();
		if (isCluster) {
			// sort of a hack normal method to check enterprise
			// does not work on the cluster so if it is on a hadoop cluster
			// we assume it is enterprise.
		}
		if (!isContactZone) {
			loadClassFiles();
		}
		MDProps.load();
		cleanserData = new MDCleanserData();
		cleanserData.init();
		DQTObjectFactory.setLogLevel(log.getLogLevel());
		logBasic("Running Spoon = " + MDCleanserStep.isSpoon());
		if (isEnterprise()) {
			logBasic("Mode = Enterprise Edition");
		} else {
			logBasic("Mode = Community Edition");
		}
		if (log != null) {
			//log.logBasic("Cleanser loadGlobal - isCluster=" + isCluster);
		}
	}

	private boolean checkContactZone() {
		File checkFile = new File("ui" + Const.FILE_SEPARATOR + TAG_CONTACT_ZONE_FILE);
		isContactZone = checkFile.exists();
		logBasic("ContactZone check file : " + checkFile.getAbsolutePath());
		logBasic("ContactZone = " + isContactZone);
		return isContactZone;
	}

	public HashMap<String, MDCleanserOperation> getCleanserFieldOperations() {
		return cleanserFields.cleanserFieldOperations;
	}

	public void setCleanserFieldOperations(HashMap<String, MDCleanserOperation> md_CleanserOperations) {
		cleanserFields.cleanserFieldOperations = md_CleanserOperations;
	}

	@Override
	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		RowMetaInterface   cleanRow       = new RowMeta();
		ValueMetaInterface valueMetaClone = null;
		String[]           names          = row.getFieldNames();
		if (cleanserFields.cleanserFieldOperations.size() != names.length) {
			logError("Error Operation Fields not set - " + names.length + " found and " + cleanserFields.cleanserFieldOperations.size() + " Operations found!");
			// This overridden method does not throw an error so intentionally
			// Cause null
			//return;
		}
		for (String name : names) {
			ValueMetaInterface v = row.searchValueMeta(name);
			// Add cleansed field
			if (v != null && !Const.isEmpty(cleanserFields.cleanserFieldOperations.get(name).getOperationName())) {
				valueMetaClone = v.clone();
				valueMetaClone.setName("MD_" + valueMetaClone.getName());
				cleanRow.addValueMeta(valueMetaClone);
				getStringField(row, originName, space, valueMetaClone.getName(), valueMetaClone.getLength());
			}

			// Add pass thru field
			if(cleanserFields.cleanserFieldOperations.get(name).isPassThrough()){
			//FIXME Check Box Pass thru
				ValueMetaInterface v1 = row.searchValueMeta(name);
				if (v1 != null) {
					valueMetaClone = v1.clone();
					cleanRow.addValueMeta(valueMetaClone);
					getStringField(row, originName, space, valueMetaClone.getName(), valueMetaClone.getLength());
				}
			}
		}
		row.clear();
		row.addRowMeta(cleanRow);
		if (!Const.isEmpty(oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue)) {
			getStringField(row, originName, space, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaSize);
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

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		space = transMeta.getParentVariableSpace();
		return new MDCleanserStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		if (cleanserData == null) {
			cleanserData = new MDCleanserData();
		}
		return cleanserData;
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
		retval.append(tab).append(XMLHandler.openTag(TAG_CLEANSER_OPERATIONS)).append(Const.CR);
		// OPERATIONS
		for (String operationKey : cleanserFields.cleanserFieldOperations.keySet()) {
			if (!Const.isEmpty(cleanserFields.cleanserFieldOperations.get(operationKey).getSourceFieldName())) {
				retval.append(tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_OPERATION)).append(Const.CR);
				retval.append(tab + tab + tab).append(addTagValue(TAG_CLEANSER_OPERATION_FIELD, cleanserFields.cleanserFieldOperations.get(operationKey).getSourceFieldName()));
				retval.append(tab + tab + tab).append(addTagValue(TAG_CLEANSER_OPERATION_NAME, cleanserFields.cleanserFieldOperations.get(operationKey).getOperationName()));
				retval.append(tab + tab + tab).append(addTagValue(TAG_CLEANSER_OPERATION_DESCRIPTION, cleanserFields.cleanserFieldOperations.get(operationKey).getOperationDescription()));
				retval.append(tab + tab + tab).append(addTagValue(TAG_CLEANSER_OPERATION_PASS_THRU, String.valueOf(cleanserFields.cleanserFieldOperations.get(operationKey).isPassThrough())));
				// Rules
				retval.append(tab + tab + tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_RULES_LIST)).append(Const.CR);
				HashMap<String, CleanserRule> objMap = cleanserFields.cleanserFieldOperations.get(operationKey).getCleanserRuleMap();
				for (String objectKey : objMap.keySet()) {
					retval.append(tab + tab + tab + tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_RULE)).append(Const.CR);
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_INDEX, objectKey));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_OPERATION, objMap.get(objectKey).getOperation().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_CASING, objMap.get(objectKey).getCaseMode().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_PUNCTION, objMap.get(objectKey).getPunctMode().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_ABBREVIATION, objMap.get(objectKey).getAbrivMode().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_FIELD_DATA, objMap.get(objectKey).getDataType().encode()));
					StringBuilder debugString = new StringBuilder(200);
					debugString.append(tab + tab + tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST)).append(Const.CR);
					retval.append(tab + tab + tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST)).append(Const.CR);
					for (RuleOption ruleOpt : objMap.get(objectKey).getOptionList()) {
						retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_CLEANSE_OPTION, ruleOpt.encode()));
						debugString.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_CLEANSE_OPTION, ruleOpt.encode()));
					}
					retval.append(tab + tab + tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST)).append(Const.CR);
					debugString.append(tab + tab + tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST)).append(Const.CR);
					//System.out.println("\n\n" + debugString.toString());
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_ABRIVIATE_TARGET_SIZE, objMap.get(objectKey).getAbrivTargetSize()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_TRIGGER, String.valueOf(objMap.get(objectKey).isUseTrigger())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_EXPRESSION_TRIGGER, String.valueOf(objMap.get(objectKey).isUseExpressionTrigger())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_EXPRESSION_TRIGGER, objMap.get(objectKey).getExpressionTrigger()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_REGEX_TRIGGER, String.valueOf(objMap.get(objectKey).isUseRegExTrigger())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_TRIGGER, objMap.get(objectKey).getRegExTrigger()));
					// REG EX
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_USE_EXPRESSION, String.valueOf(objMap.get(objectKey).isUseRegEx())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_SEARCH, objMap.get(objectKey).getRegExSearch()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_REPLACE, objMap.get(objectKey).getRegExReplace()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_USE_TABLE, String.valueOf(objMap.get(objectKey).isUseRegExTable())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_TABLE, objMap.get(objectKey).getRegExTablePath()));
					// Search Replace
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_SEARCH_TERM, String.valueOf(objMap.get(objectKey).isUseSearchTerm())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_SEARCH_TERM, objMap.get(objectKey).getSearch_SerchTerm()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_SEARCH_REPLACE, objMap.get(objectKey).getSearch_ReplaceTerm()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_SEARCH_TABLE, String.valueOf(objMap.get(objectKey).isUseSearchTable())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_SEARCH_TABLE, objMap.get(objectKey).getSearch_TablePath()));
					// TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION, objMap.get(objectKey).getTransformExpression()));
					retval.append(tab + tab + tab + tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_RULE)).append(Const.CR);
				}
				retval.append(tab + tab + tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_RULES_LIST)).append(Const.CR);
				// end Objects
				retval.append(tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_OPERATION)).append(Const.CR);
			}
		}
		// END OPERATION
		retval.append(tab).append(XMLHandler.closeTag(TAG_CLEANSER_OPERATIONS)).append(Const.CR);
		retval.append(tab).append("  ").append(addTagValue(TAG_CLEANSER_APPEND_FIELD, String.valueOf(cleanserFields.appendField)));
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
		// OPERATIONS
		String prefix = TAG_CLEANSER_OPERATION + ".";
		int    i      = 0;
		for (String operationKey : cleanserFields.cleanserFieldOperations.keySet()) {
			if (!Const.isEmpty(cleanserFields.cleanserFieldOperations.get(operationKey).getSourceFieldName())) {
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_CLEANSER_OPERATION_FIELD, cleanserFields.cleanserFieldOperations.get(operationKey).getSourceFieldName());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_CLEANSER_OPERATION_NAME, cleanserFields.cleanserFieldOperations.get(operationKey).getOperationName());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_CLEANSER_OPERATION_DESCRIPTION, cleanserFields.cleanserFieldOperations.get(operationKey).getOperationDescription());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_CLEANSER_OPERATION_PASS_THRU, String.valueOf(cleanserFields.cleanserFieldOperations.get(operationKey).isPassThrough()));
				String                        rulePrefix = TAG_CLEANSER_OPERATION + "." + operationKey + "." + TAG_CLEANSER_RULE + ".";
				HashMap<String, CleanserRule> objMap     = cleanserFields.cleanserFieldOperations.get(operationKey).getCleanserRuleMap();
				int                           y          = 0;
				for (String objectKey : objMap.keySet()) {
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_INDEX, objectKey);
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_OPERATION, objMap.get(objectKey).getOperation().encode());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_CASING, objMap.get(objectKey).getCaseMode().encode());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_PUNCTION, objMap.get(objectKey).getPunctMode().encode());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_ABBREVIATION, objMap.get(objectKey).getAbrivMode().encode());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_FIELD_DATA, objMap.get(objectKey).getDataType().encode());
					int on = 0;
					for (RuleOption ruleOption : objMap.get(objectKey).getOptionList()) {
						rep.saveStepAttribute(idTransformation, idStep, on, rulePrefix + TAG_CLEANSER_RULE_CLEANSE_OPTION, ruleOption.encode());
						on++;
					}
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_ABRIVIATE_TARGET_SIZE, objMap.get(objectKey).getAbrivTargetSize());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_TRIGGER, String.valueOf(objMap.get(objectKey).isUseTrigger()));
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_EXPRESSION_TRIGGER, String.valueOf(objMap.get(objectKey).isUseExpressionTrigger()));
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_EXPRESSION_TRIGGER, objMap.get(objectKey).getExpressionTrigger());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_REGEX_TRIGGER, String.valueOf(objMap.get(objectKey).isUseRegExTrigger()));
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_TRIGGER, objMap.get(objectKey).getRegExTrigger());
					// REG EX
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_USE_EXPRESSION, String.valueOf(objMap.get(objectKey).isUseRegEx()));
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_SEARCH, objMap.get(objectKey).getRegExSearch());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_REPLACE, objMap.get(objectKey).getRegExReplace());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_USE_TABLE, String.valueOf(objMap.get(objectKey).isUseRegExTable()));
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_TABLE, objMap.get(objectKey).getRegExTablePath());
					// Search Replace
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_SEARCH_TERM, String.valueOf(objMap.get(objectKey).isUseSearchTerm()));
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_SEARCH_TERM, objMap.get(objectKey).getSearch_SerchTerm());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_SEARCH_REPLACE, objMap.get(objectKey).getSearch_ReplaceTerm());
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_SEARCH_TABLE, String.valueOf(objMap.get(objectKey).isUseSearchTable()));
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_SEARCH_TABLE, objMap.get(objectKey).getSearch_TablePath());
					// TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION
					rep.saveStepAttribute(idTransformation, idStep, y, rulePrefix + TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION, objMap.get(objectKey).getTransformExpression());
					y++;
				}
				// End Operation
				i++;
			}
		}
		rep.saveStepAttribute(idTransformation, idStep, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_RESULT_CODES, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue);
		rep.saveStepAttribute(idTransformation, idStep, TAG_CLEANSER_APPEND_FIELD, cleanserFields.appendField);
		prefix = OutputFilterFields.TAG_FILTER + ".";
		if ((oFilterFields != null) && (oFilterFields.filterTargets != null)) {
			int ii = 0;
			for (FilterTarget target : oFilterFields.filterTargets) {
				rep.saveStepAttribute(idTransformation, idStep, ii, prefix + OutputFilterFields.TAG_FILTER_NAME, target.getName());
				rep.saveStepAttribute(idTransformation, idStep, ii, prefix + OutputFilterFields.TAG_FILTER_RULE, target.getRule());
				rep.saveStepAttribute(idTransformation, idStep, ii, prefix + OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null);
				ii++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pentaho.di.trans.step.BaseStepMeta#handleStreamSelection(org.pentaho.di.trans.step.errorhandling.StreamInterface)
	 */
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

	public void writeSavedOperations() {
		File                savedFile      = new File(cleanserData.cleanserOperationsPath, TAG_CLEANSER_OPERATION_FILE);
		MDCleanserOperation savedOperation = null;
		StringBuilder       retval         = new StringBuilder(200);
		String              tab            = "      ";
		retval.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(TAG_CLEANSER_OPERATIONS)).append(Const.CR);
		// OPERATIONS
		for (String operationKey : cleanserData.lsSavedOperations.keySet()) {
			savedOperation = cleanserData.lsSavedOperations.get(operationKey);
			if (savedOperation != null) {
				retval.append(tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_OPERATION)).append(Const.CR);
				retval.append(tab + tab + tab).append(addTagValue(TAG_CLEANSER_OPERATION_FIELD, ""));
				retval.append(tab + tab + tab).append(addTagValue(TAG_CLEANSER_OPERATION_NAME, savedOperation.getOperationName()));
				retval.append(tab + tab + tab).append(addTagValue(TAG_CLEANSER_OPERATION_DESCRIPTION, savedOperation.getOperationDescription()));
				// Rules
				retval.append(tab + tab + tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_RULES_LIST)).append(Const.CR);
				HashMap<String, CleanserRule> ruleMap = savedOperation.getCleanserRuleMap();
				for (String ruleKey : ruleMap.keySet()) {
					CleanserRule rule = ruleMap.get(ruleKey);
					retval.append(tab + tab + tab + tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_RULE)).append(Const.CR);
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_INDEX, ruleKey));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_OPERATION, rule.getOperation().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_CASING, rule.getCaseMode().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_PUNCTION, rule.getPunctMode().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_ABBREVIATION, rule.getAbrivMode().encode()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_FIELD_DATA, rule.getDataType().encode()));
					retval.append(tab + tab + tab + tab).append(XMLHandler.openTag(TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST)).append(Const.CR);
					for (RuleOption ruleOption : rule.getOptionList()) {
						retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_CLEANSE_OPTION, ruleOption.encode()));
					}
					retval.append(tab + tab + tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST)).append(Const.CR);
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_ABRIVIATE_TARGET_SIZE, rule.getAbrivTargetSize()));
					// Trigger
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_TRIGGER, String.valueOf(rule.isUseTrigger())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_EXPRESSION_TRIGGER, String.valueOf(rule.isUseExpressionTrigger())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_EXPRESSION_TRIGGER, rule.getExpressionTrigger()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_REGEX_TRIGGER, String.valueOf(rule.isUseRegExTrigger())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_EXPRESSION_TRIGGER, rule.getExpressionTrigger()));
					// REG EX
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_USE_EXPRESSION, String.valueOf(rule.isUseRegEx())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_SEARCH, rule.getRegExSearch()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_REPLACE, rule.getRegExReplace()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_USE_TABLE, String.valueOf(rule.isUseRegExTable())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_REGEX_TABLE, rule.getRegExTablePath()));
					// Search Replace
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_SEARCH_TERM, String.valueOf(rule.isUseSearchTerm())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_SEARCH_TERM, rule.getSearch_SerchTerm()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_SEARCH_REPLACE, rule.getSearch_ReplaceTerm()));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_USE_SEARCH_TABLE, String.valueOf(rule.isUseSearchTable())));
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_SEARCH_TABLE, rule.getSearch_TablePath()));
					// TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION
					retval.append(tab + tab + tab + tab + tab + tab).append(addTagValue(TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION, rule.getTransformExpression()));
					retval.append(tab + tab + tab + tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_RULE)).append(Const.CR);
				}
				retval.append(tab + tab + tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_RULES_LIST)).append(Const.CR);
				// end Objects
				retval.append(tab + tab).append(XMLHandler.closeTag(TAG_CLEANSER_OPERATION)).append(Const.CR);
				// }
			} else {
				System.out.println(" No saved Operation for : " + operationKey);
			}
		}
		// END OPERATION
		retval.append(tab).append(XMLHandler.closeTag(TAG_CLEANSER_OPERATIONS)).append(Const.CR);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(savedFile));
			writer.write(retval.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getSavedOperations() {
		cleanserData.lsSavedOperations = new HashMap<String, MDCleanserOperation>();
		File savedFile = new File(cleanserData.cleanserOperationsPath, TAG_CLEANSER_OPERATION_FILE);
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder        dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(savedFile);
			doc.getDocumentElement().normalize();
			List<Node> nodes = XMLHandler.getNodes(doc.getDocumentElement(), TAG_CLEANSER_OPERATION);
			if ((nodes != null) && (nodes.size() > 0)) {
				for (Node tempNode : nodes) {
					MDCleanserOperation cleanserOperation = new MDCleanserOperation(safe(getTagValue(tempNode, TAG_CLEANSER_OPERATION_FIELD), ""));
					cleanserOperation.setOperationName(safe(getTagValue(tempNode, TAG_CLEANSER_OPERATION_NAME), ""));
					cleanserOperation.setOperationDescription(safe(getTagValue(tempNode, TAG_CLEANSER_OPERATION_DESCRIPTION), ""));
					Node       objectsNode = XMLHandler.getSubNode(tempNode, TAG_CLEANSER_RULES_LIST);
					List<Node> objNodes    = XMLHandler.getNodes(objectsNode, TAG_CLEANSER_RULE);
					if ((objNodes != null) && (objNodes.size() > 0)) {
						HashMap<String, CleanserRule> objMap = new HashMap<String, CleanserRule>();
						for (Node objNode : objNodes) {
							CleanserRule rule = new CleanserRule(null);
							try {
								rule.setAbrivMode(AbbreviationMode.decode(getTagValue(objNode, TAG_CLEANSER_RULE_ABBREVIATION)));
								rule.setCaseMode(CasingMode.decode(getTagValue(objNode, TAG_CLEANSER_RULE_CASING)));
								rule.setDataType(FieldDataType.decode(getTagValue(objNode, TAG_CLEANSER_RULE_FIELD_DATA)));
								rule.setOperation(RuleOperation.decode(getTagValue(objNode, TAG_CLEANSER_RULE_OPERATION)));
								Node optionListNode = XMLHandler.getSubNode(objNode, TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST);
								//System.out.println("getting rule from saved -- " + nodeToString(optionListNode));
								List<Node> optionNodes = XMLHandler.getNodes(optionListNode, TAG_CLEANSER_RULE_CLEANSE_OPTION);
								for (Node optNode : optionNodes) {
									//System.out.println("getting saved rule option -- " + optNode.getTextContent());
									rule.getOptionList().add(RuleOption.decode(optNode.getTextContent()));
								}
								rule.setAbrivTargetSize(getTagValue(objNode, TAG_CLEANSER_ABRIVIATE_TARGET_SIZE));
								rule.setPunctMode(PunctuationMode.decode(getTagValue(objNode, TAG_CLEANSER_RULE_PUNCTION)));
								rule.setIndex(getTagValue(objNode, TAG_CLEANSER_RULE_INDEX));
								rule.setUseTrigger(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_TRIGGER)));
								rule.setUseRegExTrigger(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_REGEX_TRIGGER)));
								rule.setRegExTrigger(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_TRIGGER));
								rule.setUseExpressionTrigger(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_EXPRESSION_TRIGGER)));
								rule.setExpressionTrigger(getTagValue(objNode, TAG_CLEANSER_RULE_EXPRESSION_TRIGGER));
								rule.setUseRegEx(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_USE_EXPRESSION)));
								rule.setRegExSearch(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_SEARCH));
								rule.setRegExReplace(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_REPLACE));
								rule.setUseRegExTable(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_USE_TABLE)));
								rule.setRegExTablePath(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_TABLE));
								rule.setUseSearchTerm(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_SEARCH_TERM)));
								rule.setSearch_SerchTerm(getTagValue(objNode, TAG_CLEANSER_RULE_SEARCH_TERM));
								rule.setSearch_ReplaceTerm(getTagValue(objNode, TAG_CLEANSER_RULE_SEARCH_REPLACE));
								rule.setUseSearchTable(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_SEARCH_TABLE)));
								rule.setSearch_TablePath(getTagValue(objNode, TAG_CLEANSER_RULE_SEARCH_TABLE));
								rule.setTransformExpression(getTagValue(objNode, TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION));
								objMap.put(getTagValue(objNode, TAG_CLEANSER_RULE_INDEX), rule);
							} catch (KettleException ke) {
								logError("Error reading rule while reading XML : " + ke.getMessage());
							}
						}
						cleanserOperation.setClenserObjectsMap(objMap);
					}
					cleanserData.lsSavedOperations.put(cleanserOperation.getOperationName(), cleanserOperation);
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException ignored) {
			// ignore will get created if needed
		}
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		setDefault();
		cleanserFields.appendField = Boolean.valueOf(safe(getTagValue(stepnode, TAG_CLEANSER_APPEND_FIELD), "false"));
		Node       operationsNode = XMLHandler.getSubNode(stepnode, TAG_CLEANSER_OPERATIONS);
		List<Node> nodes          = XMLHandler.getNodes(operationsNode, TAG_CLEANSER_OPERATION);
		if ((nodes != null) && (nodes.size() > 0)) {
			for (Node tempNode : nodes) {
				MDCleanserOperation cleanserOperation = new MDCleanserOperation(safe(getTagValue(tempNode, TAG_CLEANSER_OPERATION_FIELD), ""));
				cleanserOperation.setOperationName(safe(getTagValue(tempNode, TAG_CLEANSER_OPERATION_NAME), ""));
				cleanserOperation.setOperationDescription(safe(getTagValue(tempNode, TAG_CLEANSER_OPERATION_DESCRIPTION), ""));
				cleanserOperation.setPassThrough(Boolean.valueOf(safe(getTagValue(tempNode, TAG_CLEANSER_OPERATION_PASS_THRU), "true")));

				Node       objectsNode = XMLHandler.getSubNode(tempNode, TAG_CLEANSER_RULES_LIST);
				List<Node> objNodes    = XMLHandler.getNodes(objectsNode, TAG_CLEANSER_RULE);
				if ((objNodes != null) && (objNodes.size() > 0)) {
					HashMap<String, CleanserRule> objMap = new HashMap<String, CleanserRule>();
					for (Node objNode : objNodes) {
						CleanserRule rule = new CleanserRule(null);
						try {
							rule.setAbrivMode(AbbreviationMode.decode(getTagValue(objNode, TAG_CLEANSER_RULE_ABBREVIATION)));
							rule.setCaseMode(CasingMode.decode(getTagValue(objNode, TAG_CLEANSER_RULE_CASING)));
							rule.setDataType(FieldDataType.decode(getTagValue(objNode, TAG_CLEANSER_RULE_FIELD_DATA)));
							rule.setOperation(RuleOperation.decode(getTagValue(objNode, TAG_CLEANSER_RULE_OPERATION)));
							Node optionListNode = XMLHandler.getSubNode(objNode, TAG_CLEANSER_RULE_CLEANSE_OPTION_LIST);
							//System.out.println("getting rule -- " + nodeToString(optionListNode));
							List<Node> optionNodes = XMLHandler.getNodes(optionListNode, TAG_CLEANSER_RULE_CLEANSE_OPTION);
							for (Node optNode : optionNodes) {
								//System.out.println("getting rule -- " + optNode.getTextContent());
								rule.getOptionList().add(RuleOption.decode(optNode.getTextContent()));
							}
							rule.setAbrivTargetSize(getTagValue(objNode, TAG_CLEANSER_ABRIVIATE_TARGET_SIZE));
							rule.setPunctMode(PunctuationMode.decode(getTagValue(objNode, TAG_CLEANSER_RULE_PUNCTION)));
							rule.setIndex(getTagValue(objNode, TAG_CLEANSER_RULE_INDEX));
							rule.setUseTrigger(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_TRIGGER)));
							rule.setUseRegExTrigger(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_REGEX_TRIGGER)));
							rule.setRegExTrigger(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_TRIGGER));
							rule.setUseExpressionTrigger(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_EXPRESSION_TRIGGER)));
							rule.setExpressionTrigger(getTagValue(objNode, TAG_CLEANSER_RULE_EXPRESSION_TRIGGER));
							rule.setUseRegEx(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_USE_EXPRESSION)));
							rule.setRegExSearch(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_SEARCH));
							rule.setRegExReplace(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_REPLACE));
							rule.setUseRegExTable(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_USE_TABLE)));
							rule.setRegExTablePath(getTagValue(objNode, TAG_CLEANSER_RULE_REGEX_TABLE));
							rule.setUseSearchTerm(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_SEARCH_TERM)));
							rule.setSearch_SerchTerm(getTagValue(objNode, TAG_CLEANSER_RULE_SEARCH_TERM));
							rule.setSearch_ReplaceTerm(getTagValue(objNode, TAG_CLEANSER_RULE_SEARCH_REPLACE));
							rule.setUseSearchTable(Boolean.valueOf(getTagValue(objNode, TAG_CLEANSER_RULE_USE_SEARCH_TABLE)));
							rule.setSearch_TablePath(getTagValue(objNode, TAG_CLEANSER_RULE_SEARCH_TABLE));
							rule.setTransformExpression(getTagValue(objNode, TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION));
							objMap.put(getTagValue(objNode, TAG_CLEANSER_RULE_INDEX), rule);
						} catch (KettleException ke) {
							logError("Error reading rule while reading XML : " + ke.getMessage());
						}
					}
					cleanserOperation.setClenserObjectsMap(objMap);
				}
				cleanserFields.cleanserFieldOperations.put(cleanserOperation.getSourceFieldName(), cleanserOperation);
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

	@Override
	public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {
		setDefault();
		String prefix = TAG_CLEANSER_OPERATION + ".";
		cleanserFields.appendField = rep.getStepAttributeBoolean(idStep, TAG_CLEANSER_APPEND_FIELD);
		int nrOperations = rep.countNrStepAttributes(idStep, prefix + TAG_CLEANSER_OPERATION_FIELD);
		for (int i = 0; i < nrOperations; i++) {
			MDCleanserOperation cleanserOperation = new MDCleanserOperation(rep.getStepAttributeString(idStep, i, prefix + TAG_CLEANSER_OPERATION_FIELD));
			cleanserOperation.setOperationName(rep.getStepAttributeString(idStep, i, prefix + TAG_CLEANSER_OPERATION_NAME));
			cleanserOperation.setOperationDescription(rep.getStepAttributeString(idStep, i, prefix + TAG_CLEANSER_OPERATION_DESCRIPTION));

			cleanserOperation.setPassThrough(Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + TAG_CLEANSER_OPERATION_PASS_THRU)));

			String rulePrefix = TAG_CLEANSER_OPERATION + "." + cleanserOperation.getSourceFieldName() + "." + TAG_CLEANSER_RULE + ".";
			int    nrRules    = rep.countNrStepAttributes(idStep, rulePrefix + TAG_CLEANSER_RULE_INDEX);
			if (nrRules > 0) {
				HashMap<String, CleanserRule> objMap = new HashMap<String, CleanserRule>();
				for (int y = 0; y < nrRules; y++) {
					CleanserRule rule = new CleanserRule(null);
					try {
						rule.setAbrivMode(AbbreviationMode.decode(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_ABBREVIATION)));
						rule.setCaseMode(CasingMode.decode(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_CASING)));
						rule.setDataType(FieldDataType.decode(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_FIELD_DATA)));
						rule.setOperation(RuleOperation.decode(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_OPERATION)));
						int nrOptions = rep.countNrStepAttributes(idStep, rulePrefix + TAG_CLEANSER_RULE_INDEX);
						if (nrOptions > 0) {
							for (int rn = 0; rn < nrOptions; rn++) {
								rule.getOptionList().add(RuleOption.decode(rep.getStepAttributeString(idStep, rn, rulePrefix + TAG_CLEANSER_RULE_CLEANSE_OPTION)));
							}
						}
						rule.setAbrivTargetSize(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_ABRIVIATE_TARGET_SIZE));
						rule.setPunctMode(PunctuationMode.decode(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_PUNCTION)));
						rule.setIndex(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_INDEX));
						rule.setUseTrigger(Boolean.valueOf(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_TRIGGER)));
						rule.setUseRegExTrigger(Boolean.valueOf(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_REGEX_TRIGGER)));
						rule.setRegExTrigger(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_TRIGGER));
						rule.setUseExpressionTrigger(Boolean.valueOf(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_EXPRESSION_TRIGGER)));
						rule.setExpressionTrigger(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_EXPRESSION_TRIGGER));
						rule.setUseRegEx(Boolean.valueOf(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_USE_EXPRESSION)));
						rule.setRegExSearch(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_SEARCH));
						rule.setRegExReplace(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_REPLACE));
						rule.setUseRegExTable(Boolean.valueOf(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_USE_TABLE)));
						rule.setRegExTablePath(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_REGEX_TABLE));
						rule.setUseSearchTerm(Boolean.valueOf(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_SEARCH_TERM)));
						rule.setSearch_SerchTerm(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_SEARCH_TERM));
						rule.setSearch_ReplaceTerm(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_SEARCH_REPLACE));
						rule.setUseSearchTable(Boolean.valueOf(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_USE_SEARCH_TABLE)));
						rule.setSearch_TablePath(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_SEARCH_TABLE));
						rule.setTransformExpression(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_TRANSFORM_EXPRESSION));
						objMap.put(rep.getStepAttributeString(idStep, y, rulePrefix + TAG_CLEANSER_RULE_INDEX), rule);
					} catch (KettleException ke) {
						logError("Error reading rule while reading XML : " + ke.getMessage());
					}
				}
				cleanserOperation.setClenserObjectsMap(objMap);
			}
			cleanserFields.cleanserFieldOperations.put(cleanserOperation.getSourceFieldName(), cleanserOperation);
		}
		oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue = rep.getStepAttributeString(idStep, OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_RESULT_CODES);
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
		// De-reference info steps
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
	public void selectValues(MDCleanserStep check, MDCleanserMeta meta, MDCleanserData data, List<MDCleanserRequest> requests, IOMetaHandler ioMeta) {
		// For each request...
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDCleanserRequest request   = requests.get(recordID);
			RowMetaInterface  inputMeta = request.inputMeta;
			Object[]          inputData = request.inputData;
			// Get the field values
			for (int u = 0; u < inputMeta.size(); u++) {
				// Normally this can't happen, except when streams are mixed with different number of fields.
				if (u < inputMeta.size()) {
					Object value = inputData[u];
					request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, value);
				} else {
					if (check.isDetailed()) {
						check.logDetailed(BaseMessages.getString(PKG, "MDCleanser.Log.MixingStreamWithDifferentFields", "")); //$NON-NLS-1$
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
		if (cleanserFields == null) {
			cleanserFields = new CleanserFields();
		}
		oFilterFields.init();
		oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue = "MD_Results";
		passThruFields.init();
		cleanserFields.init();
		//	cleanserData = new MDCleanserData();
		getSavedOperations();
	}

	private boolean specialUsage() {
		// used to allow Melissadata usage with out EE lic
//		File tstFile = new File(Const.getKettleDirectory(), "md_usage.prop");
//		return tstFile.exists();
		return Boolean.valueOf(MDProps.getProperty(TAG_MELISSADATA_SPECIAL_USAGE, "false"));
	}

	@Override
	public boolean supportsErrorHandling() {
		return true;
	}

	public boolean isContactZone() {
		return isContactZone;
	}
}
