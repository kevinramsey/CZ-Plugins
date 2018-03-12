package com.melissadata.kettle.profiler;

import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.kettle.MDSettings.SettingsTags;
import com.melissadata.kettle.profiler.data.OutputFilterFields;
import com.melissadata.kettle.profiler.data.ProfilerEnum.AppendMode;
import com.melissadata.kettle.profiler.data.ProfilerEnum.ColumnType;
import com.melissadata.kettle.profiler.data.ProfilerEnum.ExpectedContent;
import com.melissadata.kettle.profiler.data.ProfilerFields;
import com.melissadata.kettle.profiler.support.MDPropTags;
import com.melissadata.kettle.profiler.support.PluginInstaller;
import com.melissadata.kettle.profiler.ui.ProfileRecord;
import com.melissadata.mdProfiler;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.*;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Step(id = "MDProfilerPlugin", image = "com/melissadata/kettle/profiler/images/PDI_MD_Profiler_V1.svg", description = "MDProfilerPlugin.FullStep.Description", name = "MDProfilerPlugin.FullStep.Name", categoryDescription = "MDProfilerPlugin.Category", i18nPackageName = "com.melissadata.kettle")
public class MDProfilerMeta extends BaseStepMeta implements StepMetaInterface {
	public static final  String                         TAG_MELISSADATA_SPECIAL_USAGE = "melissadata_special_usage";
	private static final String                         TAG_PROFILER_VERSION          = "md_profiler_version";
	private static final String                         TAG_LOCAL_DATA_PATH           = "data_path";
	private static final String                         TAG_CONTACT_ZONE_FILE         = "contact_zone.prp";
	private static       Class<?>                       PKG                           = MDProfilerMeta.class;
	public static final  String                         NOT_DEFINED                   = BaseMessages.getString(PKG, "MDProfilerMeta.InputData.NotDefined");
	private static       PluginInstaller                pluginInstaller               = null;
	public               ProfilerFields                 profilerFields                = null;
	public               OutputFilterFields             oFilterFields                 = null;
	public               boolean                        isCluster                     = false;
	private              VariableSpace                  space                         = null;
	private              mdProfiler                     profiler                      = null;
	private              ProfilerOutputFunctions        profilerOutputFunctions       = null;
	private              AppendMode                     appendMode                    = null;
	private              boolean                        updateData                    = false;
	private              HashMap<String, ProfileRecord> profileRecords                = null;
	private              MDProfilerData                 data                          = null;
	private              String                         installError                  = "";
	private              Exception                      installException              = null;
	private              boolean                        isContactZone                 = false;
	private              LogChannelInterface            log                           = null;

	public MDProfilerMeta() throws KettleException {
		this.log = new LogChannel(this);
		loadGlobal();
		profilerOutputFunctions = new ProfilerOutputFunctions(this);
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

	public static boolean isEnterprise() {
		return MDProps.getProperty(SettingsTags.TAG_PRIMARY_PRODUCT, "").contains(SettingsTags.MDLICENSE_PRODUCT_Profiler) || MDProps.getProperty(SettingsTags.TAG_PRIMARY_PRODUCT, "").contains(SettingsTags.MDLICENSE_PRODUCT_Any);
	}

	/**
	 * @param value
	 * @return empty string if null
	 */
	public static String safe(String value, String def) {
		return (value != null) ? value : def;
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
		}
		// Is this okay?
		else if (input.length > 1) {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, getCheckString("MultipleInputReceivedError"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		} else {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, getCheckString("InputOK"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		}
	}

	public void checkTarget(String safr) {
		List<StepMeta> usedSteps;
		try {
			usedSteps = parentStepMeta.getParentTransMeta().getUsedSteps();
		} catch (NullPointerException npe) {
			return;
		}
		List<String> usedNames = new ArrayList<String>();
		for (StepMeta sm : usedSteps) {
			usedNames.add(sm.getName());
		}
		List<StreamInterface> targetStreams = getStepIOMeta().getTargetStreams();
		List<StreamInterface> removeStreams = new ArrayList<StreamInterface>();
		for (FilterTarget fft : oFilterFields.filterTargets.values()) {
			if (fft.getTargetStep() != null) {
				if (!usedNames.contains(fft.getTargetStep().getName()) && !(fft.getTargetStep().getName().equals(safr))) {
					fft.setTargetStep(null);
				}
			}
		}
		for (StreamInterface targetStream : targetStreams) {
			if (!usedNames.contains(targetStream.getStepname()) && !(targetStream.getStepname().equals(safr))) {
				removeStreams.add(targetStream);
				String name = ((FilterTarget) targetStream.getSubject()).getName();
				oFilterFields.filterTargets.get(name).setTargetStep(null);
			}
		}
		targetStreams.removeAll(removeStreams);
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
			logBasic("MDProfiler is up to date " + curVersion);
			return false;
		}
		return true;
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

	public boolean dataExists() {
		File dPath    = new File(MDProps.getProperty(TAG_LOCAL_DATA_PATH, ""));
		File dataFile = new File(dPath, "mdProfiler.dat");
		return dataFile.exists();
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#excludeFromCopyDistributeVerification ()
	 */
	@Override
	public boolean excludeFromCopyDistributeVerification() {
		return true;
	}

	public AppendMode getAppendMode() {
		return appendMode;
	}

	public void setAppendMode(AppendMode appendMode) {
		this.appendMode = appendMode;
		profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_FILE_HANDLING).metaValue = this.appendMode.name();
	}

	/**
	 * @param name
	 * @param args
	 * @return
	 */
	private String getCheckString(String name, String... args) {
		return BaseMessages.getString(PKG, "MDProfilerMeta.CheckResult." + name, args);
	}

	public MDProfilerData getData() {
		return data;
	}

	public void setData(MDProfilerData data) {
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getFields(org.pentaho.di.core. row.RowMetaInterface, java.lang.String,
	 * org.pentaho.di.core.row.RowMetaInterface[],
	 * org.pentaho.di.trans.step.StepMeta, org.pentaho.di.core.variables.VariableSpace)
	 */
	@Override
	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		this.space = space;
		if (nextStep != null) {
			try {
				profilerOutputFunctions.getFields(row, originName, info, nextStep, space);
			} catch (KettleException e) {
				log.logError("Error gtting output fields: " + e.getMessage());
			}
		}
	}

	private String getInstalledPluginVersion() {
		String ver = "0";
		if (Props.isInitialized()) {
			ver = Props.getInstance().getProperty(TAG_PROFILER_VERSION);
			if (ver == null) {
				ver = "0";
			}
		}
		return ver;
	}

	@Override
	public List<StreamInterface> getOptionalStreams() {
		if (oFilterFields.filterTargets == null) {
			oFilterFields.init();
		}
		checkTarget("");
		return MDStreamHandler.getOptionalStreams(oFilterFields.filterTargets.values());
	}

	public mdProfiler getProfiler() throws KettleException {
		if (profiler == null) {
			try {
				getProfilerObj();
			} catch (KettleException e) {
				throw e;
			}
		}
		return profiler;
	}

	public HashMap<String, ProfileRecord> getProfileRecords() {
		return profileRecords;
	}

	public void setProfileRecords(HashMap<String, ProfileRecord> profileRecords) {
		this.profileRecords = profileRecords;
	}

	private boolean getProfilerObj() throws KettleException {
		// 1 create object
		try {
			profiler = DQTObjectFactory.newProfiler();
		} catch (Exception e) {
			throw new KettleException(e);
		}
		// 2 data path
		profiler.SetPathToProfilerDataFiles(data.getDataPath());
		// 3 output file name
		if (!(Const.isEmpty(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_OUTPUTFILE).metaValue))) {
			String outputFileName = profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_OUTPUTFILE).metaValue;
			String fileName       = getVariableValue(outputFileName, "");
			new File(fileName);
			if (Boolean.valueOf(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_APPEND_DATE).metaValue)) {
				String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
				fileName = fileName + "_" + date;
			}
			if (!Const.isEmpty(fileName)) {
				profiler.SetFileName(fileName);
			}
		}
		// 4
		profiler.SetAppendMode(mdProfiler.AppendMode.swigToEnum(appendMode.getSwigValue()));
		// 5 license
		profiler.SetLicenseString(data.getLicenseString());
		// 6
		profiler.SetSortAnalysis(Integer.valueOf(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_SORT).metaValue));
		profiler.SetMatchUpAnalysis(Integer.valueOf(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_MATCHUP).metaValue));
		profiler.SetRightFielderAnalysis(Integer.valueOf(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_RIGHTFIELDER).metaValue));
		profiler.SetDataAggregation(Integer.valueOf(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_DATA_AGGREGATION).metaValue));
		if (!(Const.isEmpty(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_TABLE_NAME).metaValue))) {
			profiler.SetTableName(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_TABLE_NAME).metaValue);
		}
		if (!(Const.isEmpty(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_USER_NAME).metaValue))) {
			profiler.SetUserName(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_USER_NAME).metaValue);
		}
		if (!(Const.isEmpty(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_NAME).metaValue))) {
			profiler.SetJobName(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_NAME).metaValue);
		}
		if (!(Const.isEmpty(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_DESCRIPTION).metaValue))) {
			profiler.SetJobDescription(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_DESCRIPTION).metaValue);
		}
		// Initialize Profiler session:
		if (profiler.InitializeDataFiles() != mdProfiler.ProgramStatus.ErrorNone) {
			throw new KettleException(String.format("Error Initializing Profiler: \"%1$s\"", profiler.GetInitializeErrorString()));
		}
		for (ProfileRecord pr : getProfileRecords().values()) {
			if (pr.isDoProfile()) {
				profiler.AddColumn(pr.getColumnName(), pr.getColumnType().getColumnType(), pr.getExpectedContent().getDataType());
				if (pr.isSetCustomPattern()) {
					profiler.SetColumnCustomPattern(pr.getColumnName(), (pr.getCustomPattern()));
				}
				if (pr.isSetScale()) {
					profiler.SetColumnScale(pr.getColumnName(), Integer.valueOf(pr.getScale()));
				}
				if (pr.isSetPrecision()) {
					profiler.SetColumnPrecision(pr.getColumnName(), Integer.valueOf(pr.getPrecision()));
				}
				if (pr.isSetLength()) {
					profiler.SetColumnSize(pr.getColumnName(), Integer.valueOf(pr.getLength()));
				}
				if (pr.isSetDefaultValue()) {
					profiler.SetColumnDefaultValue(pr.getColumnName(), pr.getDefaultValue());
				}
				if (pr.isSetBounds()) {
					profiler.SetColumnValueRange(pr.getColumnName(), pr.getLowerBound(), pr.getUpperBound());
				}
			}
		}
		return true;
	}

	public ProfilerOutputFunctions getProfilerOutputFunctions() {
		return profilerOutputFunctions;
	}

	public VariableSpace getSpace() {
		return space;
	}

	public void setSpace(VariableSpace space) {
		this.space = space;
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
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStep(org.pentaho.di.trans .step.StepMeta,
	 * org.pentaho.di.trans.step.StepDataInterface, int, org.pentaho.di.trans.TransMeta,
	 * org.pentaho.di.trans.Trans)
	 */
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		space = transMeta.getParentVariableSpace();
		return new MDProfilerStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStepData()
	 */
	@Override
	public StepDataInterface getStepData() {
		if (data == null) {
			data = new MDProfilerData();
		}
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getStepIOMeta()
	 */
	@Override
	public StepIOMetaInterface getStepIOMeta() {
		if (ioMeta == null) {
			ioMeta = new StepIOMeta(true, false, false, false, false, true);
			MDStreamHandler.getStepIOMeta(ioMeta, oFilterFields.filterTargets.values(), parentStepMeta.getParentTransMeta().getUsedSteps());
		}
		return ioMeta;
	}

	public String getVariableValue(String var, String def) {
		/*
		 * This is a hackish fix for the way variables are returned from pentaho
		 */
		String  realVal = var;
		String  val     = "";
		Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(var);
		while (matcher.find()) {
			val = space.getVariable(matcher.group(1));
			val = val.replaceAll("\\\\", "\\\\\\\\");
			realVal = realVal.replaceAll("\\$\\{" + matcher.group(1) + "\\}", val);
		}
		if (realVal.startsWith("file:")) {
			realVal = realVal.substring(8);
		}
		return realVal;
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
		retval.append(tab).append(XMLHandler.openTag(ProfilerFields.TAG_PROFILER_OPTIONS)).append(Const.CR);
		for (String key : profilerFields.optionFields.keySet()) {
			retval.append(tab).append("  ").append(addTagValue(key, profilerFields.optionFields.get(key).metaValue));
		}
		retval.append(tab).append(XMLHandler.closeTag(ProfilerFields.TAG_PROFILER_OPTIONS)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(MDPropTags.TAG_PROFILER_INPUT)).append(Const.CR);
		for (String key : profileRecords.keySet()) {
			retval.append(tab).append(XMLHandler.openTag(MDPropTags.TAG_PROFILER_INPUT_FIELD)).append(Const.CR);
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_DO_PROFILE, String.valueOf(profileRecords.get(key).isDoProfile())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_DO_PASSTHROUGH, String.valueOf(profileRecords.get(key).isDoPassThrough())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_DO_RESULTS, String.valueOf(profileRecords.get(key).isDoResults())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_COLUMN_NAME, profileRecords.get(key).getColumnName()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_EXPECTED_CONTENT, profileRecords.get(key).getExpectedContent().encode()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_DATA_TYPE, profileRecords.get(key).getColumnType().encode()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_SET_LENGTH, String.valueOf(profileRecords.get(key).isSetLength())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_LENGTH, profileRecords.get(key).getLength()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_SET_PERSICION, String.valueOf(profileRecords.get(key).isSetPrecision())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_PERCISION, profileRecords.get(key).getPrecision()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_SET_SCALE, String.valueOf(profileRecords.get(key).isSetScale())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_SCALE, profileRecords.get(key).getScale()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_SET_DEFAULT, String.valueOf(profileRecords.get(key).isSetDefaultValue())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_DEFAULT_VALUE, profileRecords.get(key).getDefaultValue()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_SET_BOUNDS, String.valueOf(profileRecords.get(key).isSetBounds())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_UPPER_BOUNDS, profileRecords.get(key).getUpperBound()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_LOWER_BOUNDS, profileRecords.get(key).getLowerBound()));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_SET_CUSTOM, String.valueOf(profileRecords.get(key).isSetCustomPattern())));
			retval.append(tab).append("  ").append(addTagValue(MDPropTags.TAG_INPUT_CUSTOM_PATTERN, profileRecords.get(key).getCustomPattern()));
			retval.append(tab).append(XMLHandler.closeTag(MDPropTags.TAG_PROFILER_INPUT_FIELD)).append(Const.CR);
		}
		retval.append(tab).append(XMLHandler.closeTag(MDPropTags.TAG_PROFILER_INPUT)).append(Const.CR);
		retval.append(tab).append(XMLHandler.openTag(OutputFilterFields.TAG_OUTPUT_FILTER)).append(Const.CR);
		retval.append(tab).append("  ").append(XMLHandler.openTag(OutputFilterFields.TAG_FILTERS));
		if ((oFilterFields != null) && (oFilterFields.filterTargets != null)) {
			for (FilterTarget target : oFilterFields.filterTargets.values()) {
				retval.append(tab).append("    ").append(XMLHandler.openTag(OutputFilterFields.TAG_FILTER));
				retval.append(tab).append("    ").append(MDProfilerData.addTagValue(OutputFilterFields.TAG_FILTER_NAME, target.getName()));
				retval.append(tab).append("    ").append(MDProfilerData.addTagValue(OutputFilterFields.TAG_FILTER_PIN, target.getPin().encode()));
				retval.append(tab).append("    ").append(MDProfilerData.addTagValue(OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null));
				retval.append(tab).append("    ").append(XMLHandler.closeTag(OutputFilterFields.TAG_FILTER));
			}
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(OutputFilterFields.TAG_FILTERS));
		retval.append(tab).append(XMLHandler.closeTag(OutputFilterFields.TAG_OUTPUT_FILTER)).append(Const.CR);
		return retval.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#handleStreamSelection(org.pentaho
	 * .di.trans.step.errorhandling.StreamInterface)
	 */
	@Override
	public void handleStreamSelection(StreamInterface stream) {
		FilterTarget ft1 = (FilterTarget) stream.getSubject();
		ft1.setTargetStep(stream.getStepMeta());
		stream.getStepMeta().setName(ft1.getName());
		stream.setSubject(ft1);
		checkTarget(stream.getStepname());
		List<StreamInterface> targetStreams = getStepIOMeta().getTargetStreams();
		targetStreams.add(stream);
		resetStepIoMeta(); // force stepIo to be recreated when it is next needed.
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
			URLClassLoader spoonLoader = (URLClassLoader) KettleVFS.class.getClassLoader();
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
				MDProfilerStep.setIsSpoon(true);
			} else {
				MDProfilerStep.setIsSpoon(false);
			}
		}
		if (MDProfilerStep.isSpoon() && !isContactZone) {
			logBasic("Checking Pentaho Plugin Install ");
			moveTmp();
			if (!mdDir.exists()) {
				pluginInstaller = new PluginInstaller(isContactZone, log);
				try {
					pluginInstaller.doInstall(false, false, getVersionFromManifest());
				} catch (Exception e) {
					installError += " Faild To Install MDProfiler" + e.getMessage();
					installException = e;
					log.logError(" Faild To Install MDProfiler" + e.getMessage());
				}
				setUpdateData(true);
				MDProps.load();
			} else {
				setUpdateData(checkUpdate());
				if (MDProfilerStep.isSpoon() && isUpdateData()) {
					pluginInstaller = new PluginInstaller(isContactZone, log);
					try {
						pluginInstaller.doInstall(true, isUpdateData(), getVersionFromManifest());
					} catch (Exception e) {
						installError += " Faild To Install MDProfiler" + e.getMessage();
						installException = e;
						log.logError(" Faild To Install MDProfiler" + e.getMessage());
					}
				} else if (!MDProfilerStep.isSpoon() && isUpdateData()) {
					// TODO instal for non gui
				}
			}
		} else if (MDProfilerStep.isSpoon() && isContactZone) {
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
			loadClassFiles(isCluster);
		}
		MDProps.load();
		DQTObjectFactory.setLogLevel(log.getLogLevel());
		logBasic("Running Spoon = " + MDProfilerStep.isSpoon());
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

	public boolean checkHadoopCluster() {
		if (isCluster) {
			return isCluster;
		}
		if (MDProfilerStep.isSpoon()) {
			return isCluster = false;
		}
		File clusterProp = new File("mdProps.prop");
		if (clusterProp.exists()) {
			isCluster = true;
		}
		return isCluster;
	}

	/**
	 * Called to read meta data from a document node
	 *
	 * @param stepnode
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		if (profilerFields == null) {
			profilerFields = new ProfilerFields();
			profilerFields.init();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
			oFilterFields.init();
		}
		if (profileRecords == null) {
			profileRecords = new HashMap<String, ProfileRecord>();
		}
		setDefault();
		List<Node> nodes = XMLHandler.getNodes(stepnode, ProfilerFields.TAG_PROFILER_OPTIONS);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode = nodes.get(0);
			for (String key : profilerFields.optionFields.keySet()) {
				profilerFields.optionFields.get(key).metaValue = safe(getTagValue(tempNode, key), profilerFields.optionFields.get(key).metaValue);
			}
			try {
				appendMode = AppendMode.decode(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_FILE_HANDLING).metaValue);
			} catch (KettleException e) {
				log.logBasic("Error getting apped mode: " + profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_FILE_HANDLING).metaValue);
				log.logBasic("Setting to : " + AppendMode.OVERWRITE);
				appendMode = AppendMode.OVERWRITE;
			}
		}
		nodes = XMLHandler.getNodes(stepnode, MDPropTags.TAG_PROFILER_INPUT);
		if ((nodes != null) && (nodes.size() > 0)) {
			Node tempNode   = nodes.get(0);
			int  nrMappings = XMLHandler.countNodes(tempNode, MDPropTags.TAG_PROFILER_INPUT_FIELD);
			for (int i = 0; i < nrMappings; i++) {
				Node            sourceNode   = XMLHandler.getSubNodeByNr(tempNode, MDPropTags.TAG_PROFILER_INPUT_FIELD, i);
				String          colName      = getTagValue(sourceNode, MDPropTags.TAG_INPUT_COLUMN_NAME);
				boolean         prof         = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_DO_PROFILE));
				boolean         pass         = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_DO_PASSTHROUGH));
				boolean         result       = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_DO_RESULTS));
				boolean         setDefault   = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_SET_DEFAULT));
				String          defaultVal   = getTagValue(sourceNode, MDPropTags.TAG_INPUT_DEFAULT_VALUE);
				boolean         setBounds    = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_SET_BOUNDS));
				String          uBounds      = getTagValue(sourceNode, MDPropTags.TAG_INPUT_UPPER_BOUNDS);
				String          lBounds      = getTagValue(sourceNode, MDPropTags.TAG_INPUT_LOWER_BOUNDS);
				boolean         setCustom    = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_SET_CUSTOM));
				String          customVal    = getTagValue(sourceNode, MDPropTags.TAG_INPUT_CUSTOM_PATTERN);
				ExpectedContent content      = ExpectedContent.decode(getTagValue(sourceNode, MDPropTags.TAG_INPUT_EXPECTED_CONTENT));
				ColumnType      type         = ColumnType.decode(getTagValue(sourceNode, MDPropTags.TAG_INPUT_DATA_TYPE));
				boolean         setLength    = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_SET_LENGTH));
				String          len          = getTagValue(sourceNode, MDPropTags.TAG_INPUT_LENGTH);
				boolean         setPrecision = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_SET_PERSICION));
				String          precision    = getTagValue(sourceNode, MDPropTags.TAG_INPUT_PERCISION);
				boolean         setScale     = Boolean.valueOf(getTagValue(sourceNode, MDPropTags.TAG_INPUT_SET_SCALE));
				String          scale        = getTagValue(sourceNode, MDPropTags.TAG_INPUT_SCALE);
				ProfileRecord   pr           = new ProfileRecord(null, prof, pass, result, setDefault, defaultVal, setBounds, uBounds, lBounds, setCustom, customVal, colName, content, type, setLength, len, setPrecision, precision, setScale, scale);
				profileRecords.put(colName, pr);
			}
		}
		Node dupNodes = XMLHandler.getSubNode(stepnode, OutputFilterFields.TAG_OUTPUT_FILTER);
		dupNodes = XMLHandler.getSubNode(dupNodes, OutputFilterFields.TAG_FILTERS);
		int nrFilters = XMLHandler.countNodes(dupNodes, OutputFilterFields.TAG_FILTER);
		oFilterFields.filterTargets = new HashMap<String, FilterTarget>(nrFilters);
		for (int i = 0; i < nrFilters; i++) {
			Node         pinNode = XMLHandler.getSubNodeByNr(dupNodes, OutputFilterFields.TAG_FILTER, i);
			FilterTarget target  = new FilterTarget();
			target.setName(getTagValue(pinNode, OutputFilterFields.TAG_FILTER_NAME));
			String pinName = getTagValue(pinNode, OutputFilterFields.TAG_FILTER_PIN);
			target.setOutputPin(pinName);
			String tname = getTagValue(pinNode, OutputFilterFields.TAG_FILTER_TARGET);
			target.setTargetStepname(tname);
			oFilterFields.filterTargets.put(pinName, target);
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

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	@Override
	public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {
		if (profilerFields == null) {
			profilerFields = new ProfilerFields();
			profilerFields.init();
		}
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
			oFilterFields.init();
		}
		if (profileRecords == null) {
			profileRecords = new HashMap<String, ProfileRecord>();
		}
		setDefault();
		String prefix = ProfilerFields.TAG_PROFILER_OPTIONS + ".";
		for (String key : profilerFields.optionFields.keySet()) {
			String val = rep.getStepAttributeString(idStep, prefix + key);
			profilerFields.optionFields.get(key).metaValue = val;
		}
		try {
			appendMode = AppendMode.decode(profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_FILE_HANDLING).metaValue);
		} catch (KettleException e) {
			log.logBasic("Error getting apped mode: " + profilerFields.optionFields.get(ProfilerFields.TAG_OPTION_FILE_HANDLING).metaValue);
			log.logBasic("Setting to : " + AppendMode.OVERWRITE);
			appendMode = AppendMode.OVERWRITE;
		}
		prefix = MDPropTags.TAG_PROFILER_INPUT + "." + MDPropTags.TAG_PROFILER_INPUT_FIELD + ".";
		int nrMappings = rep.countNrStepAttributes(idStep, prefix + MDPropTags.TAG_INPUT_COLUMN_NAME);
		for (int i = 0; i < nrMappings; i++) {
			String          colName      = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_COLUMN_NAME);
			boolean         prof         = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_DO_PROFILE));
			boolean         pass         = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_DO_PASSTHROUGH));
			boolean         result       = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_DO_RESULTS));
			boolean         setDefault   = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_SET_DEFAULT));
			String          defaultVal   = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_DEFAULT_VALUE);
			boolean         setBounds    = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_SET_BOUNDS));
			String          uBounds      = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_UPPER_BOUNDS);
			String          lBounds      = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_LOWER_BOUNDS);
			boolean         setCustom    = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_SET_CUSTOM));
			String          customVal    = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_CUSTOM_PATTERN);
			ExpectedContent content      = ExpectedContent.decode(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_EXPECTED_CONTENT));
			ColumnType      type         = ColumnType.decode(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_DATA_TYPE));
			boolean         setLength    = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_SET_LENGTH));
			String          len          = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_LENGTH);
			boolean         setPrecision = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_SET_PERSICION));
			String          precision    = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_PERCISION);
			boolean         setScale     = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_SET_SCALE));
			String          scale        = rep.getStepAttributeString(idStep, i, prefix + MDPropTags.TAG_INPUT_SCALE);
			ProfileRecord   pr           = new ProfileRecord(null, prof, pass, result, setDefault, defaultVal, setBounds, uBounds, lBounds, setCustom, customVal, colName, content, type, setLength, len, setPrecision, precision, setScale, scale);
			profileRecords.put(colName, pr);
		}
		prefix = OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER + ".";
		int nrFilters = rep.countNrStepAttributes(idStep, prefix + OutputFilterFields.TAG_FILTER_NAME);
		oFilterFields.filterTargets = new HashMap<String, FilterTarget>(nrFilters);
		for (int i = 0; i < nrFilters; i++) {
			FilterTarget target = new FilterTarget();
			target.setName(rep.getStepAttributeString(idStep, i, prefix + OutputFilterFields.TAG_FILTER_NAME));
			String pinName = rep.getStepAttributeString(idStep, i, prefix + OutputFilterFields.TAG_FILTER_PIN);
			target.setOutputPin(pinName);
			String tname = rep.getStepAttributeString(idStep, i, prefix + OutputFilterFields.TAG_FILTER_TARGET);
			target.setTargetStepname(tname);
			oFilterFields.filterTargets.put(pinName, target);
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
		for (String key : profilerFields.optionFields.keySet()) {
			rep.saveStepAttribute(idTransformation, idStep, ProfilerFields.TAG_PROFILER_OPTIONS + "." + key, profilerFields.optionFields.get(key).metaValue);
		}
		String prefix = MDPropTags.TAG_PROFILER_INPUT + "." + MDPropTags.TAG_PROFILER_INPUT_FIELD + ".";
		int    i      = 0;
		for (ProfileRecord profileRecord : profileRecords.values()) {
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_DO_PROFILE, String.valueOf(profileRecord.isDoProfile()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_DO_PASSTHROUGH, String.valueOf(profileRecord.isDoPassThrough()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_DO_RESULTS, String.valueOf(profileRecord.isDoResults()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_COLUMN_NAME, profileRecord.getColumnName());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_EXPECTED_CONTENT, profileRecord.getExpectedContent().encode());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_DATA_TYPE, profileRecord.getColumnType().encode());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_SET_LENGTH, String.valueOf(profileRecord.isSetLength()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_LENGTH, profileRecord.getLength());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_SET_PERSICION, String.valueOf(profileRecord.isSetPrecision()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_PERCISION, profileRecord.getPrecision());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_SET_SCALE, String.valueOf(profileRecord.isSetScale()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_SCALE, profileRecord.getScale());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_SET_DEFAULT, String.valueOf(profileRecord.isSetDefaultValue()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_DEFAULT_VALUE, profileRecord.getDefaultValue());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_SET_BOUNDS, String.valueOf(profileRecord.isSetBounds()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_UPPER_BOUNDS, profileRecord.getUpperBound());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_LOWER_BOUNDS, profileRecord.getLowerBound());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_SET_CUSTOM, String.valueOf(profileRecord.isSetCustomPattern()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + MDPropTags.TAG_INPUT_CUSTOM_PATTERN, profileRecord.getCustomPattern());
			i++;
		}
		prefix = OutputFilterFields.TAG_OUTPUT_FILTER + "." + OutputFilterFields.TAG_FILTER + ".";
		i = 0;
		if ((oFilterFields != null) && (oFilterFields.filterTargets != null)) {
			for (FilterTarget target : oFilterFields.filterTargets.values()) {
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + OutputFilterFields.TAG_FILTER_NAME, target.getName());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + OutputFilterFields.TAG_FILTER_PIN, target.getPin().encode());
				rep.saveStepAttribute(idTransformation, idStep, i, prefix + OutputFilterFields.TAG_FILTER_TARGET, target.getTargetStep() != null ? target.getTargetStep().getName() : null);
				i++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#searchInfoAndTargetSteps(java. util.List)
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
		// De-reference info steps (e.g. lookup step for Profiler
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
	public void selectValues(MDProfilerStep check, MDProfilerMeta meta, MDProfilerData data, List<MDProfilerRequest> requests, IOMetaHandler ioMeta) {
		// For each request...
		for (int recordID = 0; recordID < requests.size(); recordID++) {
			MDProfilerRequest request   = requests.get(recordID);
			RowMetaInterface  inputMeta = request.inputMeta;
			Object[]          inputData = request.inputData;
			for (int i = 0; i < inputMeta.size(); i++) {
				Object val = inputData[i];
				//FIXME null check
				String fieldName = inputMeta.getFieldNames()[i];
				if (profileRecords.get(fieldName).isDoPassThrough()) {
					request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, val);
				} else {
					request.outputDataSize++;
				}
			}
		}
	}

	/**
	 * Called to initialized default values
	 */
	@Override
	public void setDefault() {
		if (profilerFields == null) {
			profilerFields = new ProfilerFields();
		}
		profilerFields.init();
		if (oFilterFields == null) {
			oFilterFields = new OutputFilterFields();
			oFilterFields.init();
		}
		if (profileRecords == null) {
			profileRecords = new HashMap<String, ProfileRecord>();
		}
		appendMode = AppendMode.OVERWRITE;
	}

	private boolean specialUsage() {
		// used to allow Melissadata usage with out EE lic
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

	public String getInstallError() {
		return installError;
	}

	public Exception getInstallException() {
		return installException;
	}

	public boolean isContactZone() {
		return isContactZone;
	}
}
