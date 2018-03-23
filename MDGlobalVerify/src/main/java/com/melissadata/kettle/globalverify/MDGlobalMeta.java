package com.melissadata.kettle.globalverify;

import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.FilterTarget;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MDStreamHandler;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.data.GlobalVerifyConfigFields;
import com.melissadata.kettle.globalverify.data.OutputFilterFields;
import com.melissadata.kettle.globalverify.data.PassThruFields;
import com.melissadata.kettle.globalverify.installer.PluginInstaller;
import com.melissadata.kettle.globalverify.meta.AddressMeta;
import com.melissadata.kettle.globalverify.meta.EmailMeta;
import com.melissadata.kettle.globalverify.meta.NameMeta;
import com.melissadata.kettle.globalverify.meta.PhoneVerifyMeta;
import org.apache.commons.io.FileUtils;
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
import org.pentaho.di.i18n.GlobalMessages;
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
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@SuppressWarnings("deprecation")
@Step(id = "MDGlobalVerifyPlugin", image = "com/melissadata/kettle/globalverify/images/PDI_MD_GlobalVerify_V1.svg", description = "MDGlobalVerifyPlugin.FullStep.Description", name = "MDGlobalVerifyPlugin.FullStep.Name", categoryDescription = "MDGlobalAddressPlugin.Category", i18nPackageName = "com.melissadata.kettle")
public class MDGlobalMeta extends BaseStepMeta implements StepMetaInterface {

	public static final  String                   TAG_MELISSADATA_SPECIAL_USAGE  = "melissadata_special_usage";
	public static final  String                   TAG_PRIMARY_LICENSE_ENTERPRISE = "license_enterprise";
	private static final String                   TAG_CONTACT_ZONE_FILE          = "contact_zone.prp";
	public static        String                   MDGLOBAL_ADDRESS_REPORT_ARRAY  = "MDGlobalAddressMeta.ReportData.";
	public static final String	TAG_GLOBALVERIFY_VERSION	= "md_global_verify_version";
	private static       Class<?>                 PKG                            = MDGlobalMeta.class;
	public static final  String                   NOT_DEFINED                    = BaseMessages.getString(PKG, "MDGlobalAddressMeta.InputData.NotDefined");
	private static       PluginInstaller          pluginInstaller                = null;
	private static       String                   templatePath                   = "";
	public               GlobalVerifyConfigFields gaConfigFields                 = null;
	public               PassThruFields           passThruFields                 = null;
	public               OutputFilterFields       oFilterFields                  = null;
	//	public               ReportingFields          reportFields                   = null;
	private              AddressMeta              addrMeta                       = null;
	private              NameMeta                 nameMeta                       = null;
	private              PhoneVerifyMeta          phoneMeta                      = null;
	private              EmailMeta                emailMeta                      = null;
	private              boolean                  updateData                     = false;
	private              MDGlobalData             data                           = null;
	private              boolean                  isCluster                      = false;
	private              boolean                  isContactZone                  = false;

	public MDGlobalMeta() throws KettleException {

		loadGlobal();
		if (nameMeta == null) {
			nameMeta = new NameMeta();
		}
		if (addrMeta == null) {
			addrMeta = new AddressMeta();
		}
		if (phoneMeta == null) {
			phoneMeta = new PhoneVerifyMeta();
		}
		if (emailMeta == null) {
			emailMeta = new EmailMeta();
		}
	}

	/**
	 * Wraps the XMLHandler.addTagValue(String, String) method. If the value is a zero-length string then it will be padded
	 * to one character. This is done so that the generated XML will have some value instead of the null that is currenly
	 * returned
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

	public static String getTemplatePath() {

		return templatePath;
	}

	public static void setTemplatePath(String path) {

		templatePath = path;
	}

	public static boolean isLicensed() {

		String lic = MDProps.getProperty("licensed_products", "");
		if (!Const.isEmpty(lic)) {
			return lic.contains(MDPropTags.MDLICENSE_PRODUCT_GlobalVerify) || lic.contains(MDPropTags.MDLICENSE_PRODUCT_Any) || lic.contains(MDPropTags.MDLICENSE_PRODUCT_Community);
		} else {
			return false;
		}
	}

	/**
	 * @param value
	 * @return empty string if null
	 */
	public static String safe(String value, String def) {

		return (value != null) ? value : def;
	}

	public static boolean isEnterprise() {

		return MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_GlobalVerify) || MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "").contains(MDPropTags.MDLICENSE_PRODUCT_Any);
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
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
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

	public boolean checkHadoopCluster() {

		if (isCluster) {
			return isCluster;
		}
		if (MDGlobalVerify.isSpoon()) {
			return isCluster = false;
		}
		File clusterProp = new File("mdProps.prop");
		if (clusterProp.exists()) {
			isCluster = true;
		}
		return isCluster;
	}

	private boolean checkUpdate() {

		String insVer     = getInstalledPluginVersion();
		String curVersion = getVersionFromManifest();
		if (insVer.equals(curVersion)) {
			return false;
		}
		return true;
	}

	/**
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#excludeFromCopyDistributeVerification()
	 */
	@Override
	public boolean excludeFromCopyDistributeVerification() {

		return true;
	}

	public AddressMeta getAddrMeta() {

		return addrMeta;
	}

	public void setAddrMeta(AddressMeta addrMeta) {

		this.addrMeta = addrMeta;
	}

	/**
	 * @param name
	 * @param args
	 * @param args
	 * @return
	 */
	private String getCheckString(String name, String... args) {

		return BaseMessages.getString(PKG, "MDGlobalAddressMeta.CheckResult." + name, args);
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

	public MDGlobalData getData() {

		return data;
	}

	public void setData(MDGlobalData data) {

		this.data = data;
	}

	public EmailMeta getEmailMeta() {

		return emailMeta;
	}

	public void setEmailMeta(EmailMeta emailMeta) {

		this.emailMeta = emailMeta;
	}

	/**
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getFields(org.pentaho.di.core.row.RowMetaInterface, java.lang.String,
	 * org.pentaho.di.core.row.RowMetaInterface[], org.pentaho.di.trans.step.StepMeta,
	 * org.pentaho.di.core.variables.VariableSpace)
	 */
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
		if (nameMeta.getNameFields().hasMinRequirements()) {
			nameMeta.getFields(row, originName, info, nextStep, space);
		}
		if (addrMeta.getAddrFields().hasMinRequirements()) {
			addrMeta.getFields(row, originName, info, nextStep, space);
		}
		if (phoneMeta.getPhoneFields().hasMinRequirements()) {
			phoneMeta.getFields(row, originName, info, nextStep, space);
		}
		if (emailMeta.getEmailFields().hasMinRequirements()) {
			emailMeta.getFields(row, originName, info, nextStep, space);
		}
		getStringField(row, originName, space, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue, oFilterFields.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaSize);
	}

	public String[] getInputs() {

		String[] inputStrings = new String[addrMeta.getAddrFields().inputFields.size()];
		int      counter      = 0;
		for (String key : addrMeta.getAddrFields().inputFields.keySet()) {
			inputStrings[counter] = addrMeta.getAddrFields().inputFields.get(key).metaValue;
			counter++;
		}
		return inputStrings;
	}

	private String getInstalledPluginVersion() {

		String ver = "0";
		if (Props.isInitialized()) {
			ver = Props.getInstance().getProperty(TAG_GLOBALVERIFY_VERSION);
			if (ver == null) {
				ver = "0";
			}
		}
		return ver;
	}

	public NameMeta getNameMeta() {

		return nameMeta;
	}

	public void setNameMeta(NameMeta nameMeta) {

		this.nameMeta = nameMeta;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getOptionalStreams()
	 */
	@Override
	public List<StreamInterface> getOptionalStreams() {

		return MDStreamHandler.getOptionalStreams(oFilterFields.filterTargets);
	}

	public PhoneVerifyMeta getPhoneMeta() {

		return phoneMeta;
	}

	public void setPhoneMeta(PhoneVerifyMeta phoneMeta) {

		this.phoneMeta = phoneMeta;
	}

	public String[] getReportArray(String category, String prefix) {

		SortedMap<Integer, String> rcs    = new TreeMap<Integer, String>();
		ResourceBundle             bundle = GlobalMessages.getBundle(PKG.getPackage().getName() + ".messages.messages", PKG);
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String rc             = keys.nextElement();
				String categoryPrefix = MDGLOBAL_ADDRESS_REPORT_ARRAY + category + ".";
				if (rc.startsWith(categoryPrefix + prefix)) {
					rc = rc.substring(categoryPrefix.length());
					int i = rc.indexOf(".");
					if (i != -1) {
						rc = rc.substring(0, i);
					}
					Integer newInt = new Integer(rc.substring(prefix.length()));
					newInt = newInt - 1;
					String msg = BaseMessages.getString(PKG, categoryPrefix + rc);
					rcs.put(newInt, msg);
				}
			}
		}
		String[] returnArray = new String[rcs.size()];
		for (Integer i : rcs.keySet()) {
			returnArray[i] = rcs.get(i);
		}
		return returnArray;
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

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStep(org.pentaho.di.trans.step.StepMeta,
	 * org.pentaho.di.trans.step.StepDataInterface, int, org.pentaho.di.trans.TransMeta, org.pentaho.di.trans.Trans)
	 */
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {

		return new MDGlobalVerify(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStepData()
	 */
	@Override
	public StepDataInterface getStepData() {

		return new MDGlobalData();
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

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getXML()
	 */
	@Override
	public String getXML() throws KettleException {
		// Save data to XML
		StringBuilder retval = new StringBuilder(200);
		String        tab    = "      ";
		nameMeta.getXML(retval);
		addrMeta.getXML(retval);
		phoneMeta.getXML(retval);
		emailMeta.getXML(retval);
		retval.append(tab).append(XMLHandler.openTag(GlobalVerifyConfigFields.TAG_GLOBAL_ADDRESS_CONFIG)).append(Const.CR);
		for (String key : gaConfigFields.configFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, gaConfigFields.configFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(GlobalVerifyConfigFields.TAG_GLOBAL_ADDRESS_CONFIG)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(PassThruFields.TAG_GLOBAL_FILTER)).append(Const.CR);
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT) != null) {
			retval.append(tab).append("  ").append(addTagValue(PassThruFields.TAG_FILTER_OUT, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT))));
		}
		if (passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH) != null) {
			retval.append(tab).append("  ").append(addTagValue(PassThruFields.TAG_PASS_THROUGH, toString(passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH))));
		}
		retval.append(tab).append(XMLHandler.closeTag(PassThruFields.TAG_GLOBAL_FILTER)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(OutputFilterFields.TAG_OUTPUT_FILTER)).append(Const.CR);
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

	public boolean isSpoon() {

		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isUpdateData() {

		return updateData;
	}

	public void setUpdateData(boolean updateData) {

		this.updateData = updateData;
	}

	private void loadClassFiles(boolean isCluster) throws KettleException {

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
			URLClassLoader spoonLoader = (URLClassLoader) KettleVFS.class.getClassLoader();// (URLClassLoader)ClassLoader.getSystemClassLoader();
			URL[]          urls        = spoonLoader.getURLs();
			for (URL url : urls) {
				if (url.toString().contains("MDSettings")) {
					loadExt = false;
				}
				if (url.toString().contains("cz")) {
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
		} else {
			log.logError("Error : Could not find MD libext dir : " + libext_dir.getAbsolutePath());
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
				MDGlobalVerify.setIsSpoon(true);
			} else {
				MDGlobalVerify.setIsSpoon(false);
			}
		}
		if (MDGlobalVerify.isSpoon() && !isContactZone) {
			logBasic("Checking Pentaho Plugin Install ");
			moveTmp();
			if (!isCluster) {
				if (!mdDir.exists()) {
					pluginInstaller = new PluginInstaller(isContactZone, log);
					pluginInstaller.doInstall(false, false, getVersionFromManifest());
					setUpdateData(true);
				} else {
					// this or another is installed
					setUpdateData(checkUpdate());
					if (MDGlobalVerify.isSpoon() && isUpdateData()) {
						pluginInstaller = new PluginInstaller(isContactZone, log);
						pluginInstaller.doInstall(true, isUpdateData(), getVersionFromManifest());
					} else if (!MDGlobalVerify.isSpoon() && isUpdateData()) {
						// use external installer
					}
				}
			}
		} else if (MDGlobalVerify.isSpoon() && isContactZone) {
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
			loadClassFiles(isCluster);
		}
		MDProps.load();
		DQTObjectFactory.setLogLevel(log.getLogLevel());
		logBasic("Running Spoon = " + MDGlobalVerify.isSpoon());
		if (isEnterprise()) {
			logBasic("Mode = Enterprise Edition");
		} else {
			logBasic("Mode = Community Edition");
		}
		if (log != null) {
			log.logBasic("Global Verify loadGlobal - isCluster=" + isCluster);
		}
	}

	public String getTransmissionReference() {

		String product               = isContactZone ? "CZ" : "PENTAHO";
		String version               = getVersionFromManifest();
		String transmissionReference = "mdSrc:{product:" + product + ";version:" + version + "}";
		return transmissionReference;
	}

	private boolean checkContactZone() {

		File checkFile = new File("ui" + Const.FILE_SEPARATOR + TAG_CONTACT_ZONE_FILE);
		isContactZone = checkFile.exists();
		logBasic("ContactZone check file : " + checkFile.getAbsolutePath());
		logBasic("ContactZone = " + isContactZone);
		return isContactZone;
	}

	/**
	 * Called to read meta data from a document node
	 *
	 * @param stepnode
	 * @throws NumberFormatException
	 */
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {

		nameMeta.loadXML(stepnode);
		addrMeta.loadXML(stepnode);
		phoneMeta.loadXML(stepnode);
		emailMeta.loadXML(stepnode);
		if (gaConfigFields == null) {
			gaConfigFields = new GlobalVerifyConfigFields();
			gaConfigFields.init();
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
		List<Node> nodes = XMLHandler.getNodes(stepnode, GlobalVerifyConfigFields.TAG_GLOBAL_ADDRESS_CONFIG);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : gaConfigFields.configFields.keySet()) {
				gaConfigFields.configFields.get(key).metaValue = safe(getTagValue(tempNode, key), gaConfigFields.configFields.get(key).metaValue);
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

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	@Override
	public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {

		if (passThruFields == null) {
			passThruFields = new PassThruFields();
			passThruFields.init();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
			oFilterFields.init();
		}
		if (gaConfigFields == null) {
			gaConfigFields = new GlobalVerifyConfigFields();
			gaConfigFields.init();
		}
		setDefault();
		addrMeta.readRep(rep, idStep);
		nameMeta.readRep(rep, idStep);
		phoneMeta.readRep(rep, idStep);
		emailMeta.readRep(rep, idStep);
//		for (String key : reportFields.reportFields.keySet()) {
//			reportFields.reportFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, ReportingFields.TAG_REPORT_GLOBAL_REPORTS + "." + key), reportFields.reportFields.get(key).metaValue);
//		}
//		for (String key : reportFields.reportOptions.keySet()) {
//			reportFields.reportOptions.put(key, Boolean.valueOf(safe(rep.getStepAttributeString(idStep, ReportingFields.TAG_REPORT_OPTION_GLOBAL_REPORTS + "." + key), reportFields.reportOptions.get(key).toString())));
//		}
		for (String key : gaConfigFields.configFields.keySet()) {
			gaConfigFields.configFields.get(key).metaValue = safe(rep.getStepAttributeString(idStep, GlobalVerifyConfigFields.TAG_GLOBAL_ADDRESS_CONFIG + "." + key), gaConfigFields.configFields.get(key).metaValue);
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
	@Override
	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {

		addrMeta.saveRep(rep, idTransformation, idStep);
		nameMeta.saveRep(rep, idTransformation, idStep);
		phoneMeta.saveRep(rep, idTransformation, idStep);
		emailMeta.saveRep(rep, idTransformation, idStep);
//		for (String key : reportFields.reportFields.keySet()) {
//			rep.saveStepAttribute(idTransformation, idStep, ReportingFields.TAG_REPORT_GLOBAL_REPORTS + "." + key, reportFields.reportFields.get(key).metaValue);
//		}
//		for (String key : reportFields.reportOptions.keySet()) {
//			rep.saveStepAttribute(idTransformation, idStep, ReportingFields.TAG_REPORT_OPTION_GLOBAL_REPORTS + "." + key, reportFields.reportOptions.get(key).toString());
//		}
		for (String key : gaConfigFields.configFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, GlobalVerifyConfigFields.TAG_GLOBAL_ADDRESS_CONFIG + "." + key, gaConfigFields.configFields.get(key).metaValue);
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
	public void selectValues(MDGlobalVerify check, MDGlobalMeta meta, MDGlobalData data, List<MDGlobalRequest> requests, IOMetaHandler ioMeta) {
		// For each request...
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDGlobalRequest  request   = requests.get(recordID);
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
	@Override
	public void setDefault() {

		if (passThruFields == null) {
			passThruFields = new PassThruFields();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
		}
		if (gaConfigFields == null) {
			gaConfigFields = new GlobalVerifyConfigFields();
		}
//		if (reportFields == null) {
//			reportFields = new ReportingFields();
//		}
//		reportFields.init();
		gaConfigFields.init();
		setDefaultPassThruOptions();
		setDefaultFilterOptions();
		setDefaultPassThruOptions();
	}

	public void setDefaultFilterOptions() {

		oFilterFields.init();
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
	@Override
	public boolean supportsErrorHandling() {

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
