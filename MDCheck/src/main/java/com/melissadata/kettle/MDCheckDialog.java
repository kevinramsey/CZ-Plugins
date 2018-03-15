package com.melissadata.kettle;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.cz.ui.MDDialogParent;
import com.melissadata.kettle.iplocator.ui.IPLocatorTab;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;
import com.melissadata.kettle.MDSettings.AdvancedConfigurationDialog;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.address.AddressVerifyTab;
import com.melissadata.kettle.fieldmapping.FieldMappingDialog;
import com.melissadata.kettle.cv.geocode.GeoCoderTab;
import com.melissadata.kettle.installer.InstallRedistrib;
import com.melissadata.kettle.installer.PluginInstaller;
import com.melissadata.kettle.installer.WelcomeDialog;
import com.melissadata.kettle.cv.name.NameParseTab;
import com.melissadata.kettle.cv.phone.PhoneEmailTab;
import com.melissadata.kettle.mu.ui.*;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import com.melissadata.kettle.ui.*;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.validation.ValidationsDialog;
import com.melissadata.kettle.report.ReportingTab;
import com.melissadata.kettle.sm.ui.SmartMoverInputTab;
import com.melissadata.kettle.sm.ui.SmartMoverOptionsTab;
import com.melissadata.kettle.sm.ui.SmartMoverOutputTab;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public abstract class MDCheckDialog extends BaseStepDialog implements StepDialogInterface, MDDialogParent {

	private static final String TAG_FIRSTRUN        = "plugin_first_run";
	private static final String TAG_PRIMARY_LICENSE = "license";
	public static String licString;
	public static  boolean                            continueToAdvancedConfig = true;
	private static Class<?>                           PKG                      = MDCheck.class;
	private static boolean                            isOpen                   = false;
	private        MDCheckMeta                        mdcMeta                  = null;
	private        MDCheckStepData                    mdcStepData              = null;
	private        AdvancedConfigurationMeta          advancedConfigMeta       = null;
	private        int                                checkTypes               = 0;
	private        MDCheckHelper                      helper                   = null;
	private        ModifyListener                     lsModified               = null;
	private        CTabFolder                         wTabFolder               = null;
	private        List<MDTab>                        tabs                     = null;
	private        AddressVerifyTab                   addressVerifyTab         = null;
	private        GeoCoderTab                        geoCoderTab              = null;
	private        boolean                            doContactVerify          = false;
	private        boolean                            doSmartMover             = false;
	private        boolean                            doMatchUp                = false;
	private        boolean                            doMatchUpGlobal          = false;
	private        boolean                            doIPLocator              = false;
	private        boolean                            doChange                 = false;
	private        Button                             wMapFields               = null;
	private        Button                             wAdvanced                = null;
	private        PluginInterface                    plugin                   = null;
	private        SortedMap<String, SourceFieldInfo> sourceFields             = null;
	private        Label                              communityLabel           = null;
	private        Composite                          centerComp               = null;
	private        Composite                          communityComp            = null;

	/**
	 * @param parent
	 * @param in
	 * @param transMeta
	 * @param stepname  The types of checks we are running
	 */
	public MDCheckDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {

		super(parent, (BaseStepMeta) in, transMeta, stepname);
		// Keep reference to original mdcMeta
		mdcMeta = (MDCheckMeta) in;
		// Create a local copy of the mdcMeta's mdcStepData
		mdcStepData = mdcMeta.getData().clone();
		advancedConfigMeta = mdcStepData.getAdvancedConfiguration();
		checkTypes = mdcStepData.getCheckTypes();
	}

	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {

		return BaseMessages.getString(PKG, "MDCheckDialog.Validation." + name, args);
	}

	public void advancedChange() {

		if (doContactVerify) {
			doInitalizeCheck(mdcStepData);
		}
		for (MDTab tab : tabs) {
			tab.advancedConfigChanged();
		}
	}

	/**
	 * Sets the changed flag to false
	 */
	public void clearChanged() {

		changed = false;
	}

	@Override
	public void dispose() {
		// Dispose of tab resources
		for (MDTab tab : tabs) {
			tab.dispose();
		}
		// Dispose of the helper
		helper.dispose();
		// Call parent
		super.dispose();
	}

	/**
	 * @return Information about the next step
	 */
	public List<StepMeta> findNextSteps() {

		StepMeta stepInfo = transMeta.findStep(stepname);
		if (stepInfo != null) {
			return transMeta.findNextSteps(stepInfo);
		}
		return new ArrayList<StepMeta>();
	}

	/**
	 * @return Information about the previous steps
	 */
	public List<StepMeta> findPreviousSteps() {

		StepMeta stepInfo = transMeta.findStep(stepname);
		if (stepInfo != null) {
			return transMeta.findPreviousSteps(stepInfo);
		}
		return new ArrayList<StepMeta>();
	}

	/**
	 * @param stepname
	 * @return The step info for the given step name
	 */
	public StepMeta findStep(String stepname) {

		return transMeta.findStep(stepname);
	}

	/**
	 * @return The mdcStepData to this dialog
	 */
	public MDCheckStepData getData() {

		return mdcStepData;
	}

	public int getCheckTypes() {

		return checkTypes;
	}

	public AdvancedConfigurationMeta getAdvancedConfigMeta() {

		return advancedConfigMeta;
	}

	/**
	 * Loads the dialog mdcStepData into the meta structure
	 */
	public void getTabData(MDCheckStepData data) throws KettleException {

		for (MDTab tab : tabs) {
			tab.getData(data);
		}
	}

	/**
	 * @return The dialog helper object
	 */
	public MDCheckHelper getHelper() {

		return helper;
	}

	/**
	 * @return Access to the log interface of the transform meta mdcStepData
	 */
	public LogChannelInterface getLog() {

		return transMeta.getLogChannel();
	}

	/**
	 * Called to retrieve the source fields of the lookup step
	 *
	 * @return
	 */
	public SortedMap<String, SourceFieldInfo> getLookupFields(String lookupStepName) {

		log.logDebug("Getting lookup fields");
		// Allocate new source field map
		SortedMap<String, SourceFieldInfo> lookupFields = new TreeMap<String, SourceFieldInfo>();
		// Get the lookup step (if it is defined)
		if (Const.isEmpty(lookupStepName)) {
			return lookupFields;
		}
		// Retrieve the source fields from the previous step
		try {
			StepMeta stepMeta = transMeta.findStep(lookupStepName);
			if (stepMeta != null) {
				RowMetaInterface row = transMeta.getStepFields(stepMeta);
				// Remember these fields...
				for (int i = 0; i < row.size(); i++) {
					String          name  = row.getValueMeta(i).getName();
					SourceFieldInfo field = new SourceFieldInfo(name);
					lookupFields.put(name, field);
					log.logDebug("Found lookup field - " + name);
				}
				log.logDebug("Found " + lookupFields.size() + " lookup fields");
			}
		} catch (KettleException e) {
			log.logError(toString(), BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
		}
		return lookupFields;
	}

	/**
	 * @return A naming prefix that will be used when looking up label strings
	 */
	public String getNamingPrefix() {

		return "MDCheckDialog";
	}

	/**
	 * @return The properties for this dialog
	 */
	public PropsUI getProps() {

		return props;
	}

	/**
	 * @return The shell of the dialog
	 */
	public Shell getShell() {

		return shell;
	}

	/**
	 * Called to retrieve the source fields of the mdcMeta step
	 *
	 * @return
	 */
	public SortedMap<String, SourceFieldInfo> getSourceFields() {
		// If we already have source fields then return them
		if (sourceFields != null) {
			return sourceFields;
		}
		log.logDebug("Getting source fields");
		// Allocate new source field map
		sourceFields = new TreeMap<String, SourceFieldInfo>();
		// Retrieve the source fields from the previous step
		try {
			StepMeta stepMeta = transMeta.findStep(stepname);
			if (stepMeta != null) {
				RowMetaInterface row = transMeta.getPrevStepFields(stepMeta);
				// Remember these fields...
				for (int i = 0; i < row.size(); i++) {
					String          name  = row.getValueMeta(i).getName();
					SourceFieldInfo field = new SourceFieldInfo(name);
					sourceFields.put(name, field);
					log.logDebug("Found source field - " + name);
				}
				log.logDebug("Found " + sourceFields.size() + " source fields");
			}
		} catch (KettleException e) {
			log.logError(toString(), BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
		}
		return sourceFields;
	}

	/**
	 * @return Access to the variable space of the transform meta mdcStepData
	 */
	public VariableSpace getSpace() {

		return transMeta;
	}

	/**
	 * @return The folder into which tabs will be placed
	 */
	public CTabFolder getTabFolder() {

		return wTabFolder;
	}

	/**
	 * AdvancedConfigInterface
	 *
	 * @return Master title for the component
	 */
	public String getTitle() {

		if ((checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_SmartMover) && AdvancedConfigurationMeta.isCommunity()) {
				return BaseMessages.getString(PKG, "MDCheckDialog.Community.Shell.Title.SmartMover");
			} else {
				return BaseMessages.getString(PKG, "MDCheckDialog.Enterprise.Shell.Title.SmartMover");
			}
		} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			int prod = advancedConfigMeta.getProducts(true);
			// set tab title acording to type match up type
			String title = BaseMessages.getString(PKG, "MDCheckDialog.Enterprise.Shell.Title.MatchUp");
			if ((prod & AdvancedConfigurationMeta.MDLICENSE_MatchUp) != 0) {
				title += "";
			} else if ((prod & AdvancedConfigurationMeta.MDLICENSE_MatchUpLite) != 0) {
				title = BaseMessages.getString(PKG, "MDCheckDialog.Lite.Shell.Title.MatchUp");
			} else {
				title = BaseMessages.getString(PKG, "MDCheckDialog.Community.Shell.Title.MatchUp");
			}
			return title;
		} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_MatchUpIntl) && AdvancedConfigurationMeta.isCommunity()) {
				return BaseMessages.getString(PKG, "MDCheckDialog.Community.Shell.Title.MatchUpGlobal");
			} else {
				return BaseMessages.getString(PKG, "MDCheckDialog.Enterprise.Shell.Title.MatchUpGlobal");
			}
		} else if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject) && AdvancedConfigurationMeta.isCommunity()) {
				return BaseMessages.getString(PKG, "MDCheckDialog.Community.Shell.Title.IPLocator");
			} else {
				return BaseMessages.getString(PKG, "MDCheckDialog.Enterprise.Shell.Title.IPLocator");
			}
		} else {
			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject) && AdvancedConfigurationMeta.isCommunity()) {
				return BaseMessages.getString(PKG, "MDCheckDialog.Community.Shell.Title");
			} else {
				return BaseMessages.getString(PKG, "MDCheckDialog.Enterprise.Shell.Title");
			}
		}
	}

	public RowMetaInterface getRowMetaInterface() {

		try {
			StepMeta stepMeta = transMeta.findStep(stepname);
			if (stepMeta != null) {
				return transMeta.getPrevStepFields(stepMeta);
			}
		} catch (KettleException e) {
			log.logError(toString(), BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
		}
		return null;
	}

	public boolean isChanged() {

		return changed;
	}

	public boolean MinAddrInput() {

		return addressVerifyTab.minInput();
	}

	public void ok() {

		stepname = wStepname.getText();
		try {
			// Don't do anything if nothing changed
			if (changed) {
				// Get mdcStepData from dialog elements
				getTabData(mdcStepData);
				// Do validation. If failed then cancel the OK
				if (!valid(mdcStepData)) {
					return;
				}
				// Preserve the new mdcStepData in the meta mdcStepData
				mdcMeta.setData(mdcStepData);
				// Tell the meta mdcStepData that it has changed
				mdcMeta.setChanged();
			}
			// Close the dialog
			isOpen = false;
			dispose();
		} catch (KettleException e) {
			new ErrorDialog(shell, BaseMessages.getString(PKG, "MDCheckDialog.IllegalDialogSettings.Title"), BaseMessages.getString(PKG, "MDCheckDialog.IllegalDialogSettings.Message"), e);
		}
	}

	/*
	 * (non-Javadoc) space
	 * @see org.pentaho.di.trans.step.StepDialogInterface#open()
	 */
	public String open() {
		// reload props incase something changed in settings
		try {
			advancedConfigMeta.loadGlobal();
		} catch (KettleException e2) {
			logError(e2.getMessage());
		}
		// This helper allows for common functionality to be shared between
		// dialog elements
		helper = new MDCheckHelper(this);
		// Create the dialog
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, mdcMeta);
		// Set dialog title based on plugin type
		shell.setText(getTitle());
		FormLayout fl = new FormLayout();
		fl.marginWidth = Const.FORM_MARGIN;
		fl.marginHeight = Const.FORM_MARGIN;
		shell.setLayout(fl);
		// Modification will set the changed flag
		lsModified = new ModifyListener() {
			public void modifyText(ModifyEvent e) {

				changed = true;
			}
		};
		// Anytime they press <enter> it will act as if they clicked ok
		lsDef = new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				ok();
			}
		};
		// Create the version label
		Label    versionText = new Label(shell, SWT.LEFT);
		String   version     = BaseMessages.getString(PKG, "MDCheckDialog.VersionPrefix");
		Class<?> clazz       = this.getClass();
		String   className   = clazz.getSimpleName() + ".class";
		String   classPath   = clazz.getResource(className).toString();
		String   buildNum    = classPath.substring(classPath.indexOf("Build") + 5, classPath.indexOf(".jar") - 1);
		if (!classPath.startsWith("jar")) {
			version = BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest    = new Manifest(new URL(manifestPath).openStream());
				String   implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				if (implVersion == null) {
					version = BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
				} else {
					version = BaseMessages.getString(PKG, "MDCheckDialog.VersionPrefix") + " " + implVersion;
				}
			} catch (Exception e) {
				version = BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
			}
		}
		versionText.setText(version);
		props.setLook(versionText);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment((helper.colWidth[0] - 15), -helper.margin);
		versionText.setLayoutData(fd);
		// save version and build number in stepData for use in testDialogs
		if (version.equalsIgnoreCase("Version Not Found")) {
			mdcStepData.localLicenseVersion = version.substring(7, version.length());
		} else {
			mdcStepData.localLicenseVersion = version.substring(8, 11);
		}
		mdcStepData.localLicenseBuildNo = buildNum;
		// Create the step name line
		wlStepname = new Label(shell, SWT.LEFT);
		wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment((helper.colWidth[0] - 8), -helper.margin);
		fdlStepname.top = new FormAttachment(0, helper.margin);
		fdlStepname.right = new FormAttachment(helper.colWidth[0], -helper.margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsModified);
		wStepname.addSelectionListener(lsDef);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(helper.colWidth[0], helper.margin);
		fdStepname.top = new FormAttachment(0, helper.margin);
		fdStepname.right = new FormAttachment(100, -helper.margin);
		wStepname.setLayoutData(fdStepname);
		// Create the tab folder
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
		wTabFolder.setSimple(false);
		// Add tabs (based on check types)
		doContactVerify = (checkTypes & MDCheckMeta.MDCHECK_FULL) != 0;
		doSmartMover = (checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0;
		doMatchUp = (checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0;
		doMatchUpGlobal = (checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0;
		doIPLocator = (checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0;
		tabs = new ArrayList<MDTab>();
		// Contact Verification Tabs
		if ((checkTypes & MDCheckMeta.MDCHECK_NAME) != 0) {
			tabs.add(new NameParseTab(this));
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_ADDRESS) != 0) {
			tabs.add(addressVerifyTab = new AddressVerifyTab(this));
			tabs.add(geoCoderTab = new GeoCoderTab(this));
		}
		if ((checkTypes & (MDCheckMeta.MDCHECK_EMAIL | MDCheckMeta.MDCHECK_PHONE)) != 0) {
			tabs.add(new PhoneEmailTab(this));
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			tabs.add(new IPLocatorTab(this));
		}
		// Smart Mover Tabs
		if (doSmartMover) {
			tabs.add(new SmartMoverInputTab(this));
			tabs.add(new SmartMoverOutputTab(this));
			tabs.add(new SmartMoverOptionsTab(this));
		}
		// MatchUp Tabs
		MatchUpFieldMappingTab fieldMappingTab = null;
		if (doMatchUp) {
			MatchUpMatchcodeTab matchcodeTab = new MatchUpMatchcodeTab(this);
			fieldMappingTab = new MatchUpFieldMappingTab(this);
			MatchUpOptionsTab optionsTab = new MatchUpOptionsTab(this);
			matchcodeTab.setFieldMappingTab(fieldMappingTab);
			fieldMappingTab.setMatchcodeTab(matchcodeTab);
			fieldMappingTab.setOptionsTab(optionsTab);
			optionsTab.setFieldMappingTab(fieldMappingTab);
			tabs.add(matchcodeTab);
			tabs.add(fieldMappingTab);
			tabs.add(optionsTab);
		}
		if (doMatchUpGlobal) {
			MatchUpMatchcodeTab matchcodeTab = new MatchUpMatchcodeTab(this);
			fieldMappingTab = new MatchUpFieldMappingTab(this);
			MatchUpOptionsTab optionsTab = new MatchUpOptionsTab(this);
			matchcodeTab.setFieldMappingTab(fieldMappingTab);
			fieldMappingTab.setMatchcodeTab(matchcodeTab);
			fieldMappingTab.setOptionsTab(optionsTab);
			optionsTab.setFieldMappingTab(fieldMappingTab);
			tabs.add(matchcodeTab);
			tabs.add(fieldMappingTab);
			tabs.add(optionsTab);
		}
		// Main source pass thru tab
		if (doMatchUp) {
			tabs.add(new MatchUpSourcePassThruTab(this, transMeta));
		} else if (doMatchUpGlobal) {
			tabs.add(new MatchUpSourcePassThruTab(this, transMeta));
		} else {
			tabs.add(new SourcePassThruTab(this, transMeta));
		}
		// Optional lookup pass thru tab
		if (doMatchUp) {
			LookupPassThruTab lookupPassThruTab = new LookupPassThruTab(this, transMeta);
			fieldMappingTab.setLookupPassThruTab(lookupPassThruTab);
			lookupPassThruTab.setFieldMappingTab(fieldMappingTab);
			tabs.add(lookupPassThruTab);
		}
		if (doMatchUpGlobal) {
			LookupPassThruTab lookupPassThruTab = new LookupPassThruTab(this, transMeta);
			fieldMappingTab.setLookupPassThruTab(lookupPassThruTab);
			lookupPassThruTab.setFieldMappingTab(fieldMappingTab);
			tabs.add(lookupPassThruTab);
		}
		// Output filtering tab
		tabs.add(new OutputFilterTab(this));
		// run options tab
		if (!doMatchUp && !doMatchUpGlobal && !doSmartMover) {
			tabs.add(new RunOptionsTab(this));
		}
		// Enable reporting tab
		// TODO: enable for smart mover and matchup
		if (doContactVerify && advancedConfigMeta.getBasicReporting()) {
			tabs.add(new ReportingTab(this));
		}
		// Place the tab folder in the dialog
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wStepname, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(92, -50);
		wTabFolder.setLayoutData(fd);
		// THE BUTTONS
		wAdvanced = new Button(shell, SWT.PUSH);
		wAdvanced.setText(BaseMessages.getString(PKG, "MDCheckDialog.Button.Advanced.Label"));
		wAdvanced.setToolTipText(BaseMessages.getString(PKG, "MDCheckDialog.Button.Advanced.ToolTip"));
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); //$NON-NLS-1$

		plugin = PluginRegistry.getInstance().getPlugin(StepPluginType.class, stepMeta.getStepMetaInterface());
		createHelpButton(shell, stepMeta, plugin);
		if (!doMatchUp && !doMatchUpGlobal) {
			wMapFields = new Button(shell, SWT.PUSH);
			wMapFields.setText("Map Fields"); //$NON-NLS-1$
		}

		Control last = null;
		if (showCommunityMessage()) {
			last = createCommunityMessage();
		} else {
			last = wTabFolder;
		}

		if (wMapFields != null) {
			setButtonPositions(new Button[] { wOK, wCancel, wMapFields, wAdvanced }, helper.margin, last);
		} else {
			setButtonPositions(new Button[] { wOK, wCancel, wAdvanced }, helper.margin, last);
		}
		// Add listeners
		Listener lsAdvanced = new Listener() {
			public void handleEvent(Event arg0) {

				advancedConfiguration(null);
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {

				ok();
			}
		};
		lsCancel = new Listener() {
			public void handleEvent(Event e) {

				cancel();
			}
		};

		Listener lsMapFields = new Listener() {
			public void handleEvent(Event event) {

				openMappingDialog();
			}

			;
		};
		if (wAdvanced != null) {
			wAdvanced.addListener(SWT.Selection, lsAdvanced);
		}
		if (wMapFields != null) {
			wMapFields.addListener(SWT.Selection, lsMapFields);
		}
		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {

				if (!cancel()) {
					e.doit = false;
				}
			}
		});
		// Select first tab
		wTabFolder.setSelection(0);
		plugin.setDocumentationUrl(getHelpURL());
		SelectionListener lsTabChange = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			public void widgetSelected(SelectionEvent arg0) {

				if (plugin != null) {
					plugin.setDocumentationUrl(getHelpURL());
				}
				try {
					getTabData(mdcStepData);
				} catch (KettleException e) {
					e.printStackTrace();
				}
				if (wTabFolder.getSelection().getText().equals("GeoCode")) {
					geoCoderTab.refreshEnable(addressVerifyTab.minInput());
				}
				if (doContactVerify) {
					if (wTabFolder.getSelectionIndex() < 4) {
						wMapFields.setEnabled(true);
					} else {
						wMapFields.setEnabled(false);
					}
				} else if (doIPLocator) {
					if (wTabFolder.getSelectionIndex() < 1) {
						wMapFields.setEnabled(true);
					} else {
						wMapFields.setEnabled(false);
					}
				} else if (doSmartMover) {
					if (wTabFolder.getSelectionIndex() < 1) {
						wMapFields.setEnabled(true);
					} else {
						wMapFields.setEnabled(false);
					}
				}
				updateCommunitylabel();
			}
		};
		wTabFolder.addSelectionListener(lsTabChange);
		// Set the shell size, based upon previous time...
		setSize();
		// Load mdcStepData into dialog. Adjust changed flag according to init results.
		changed = init(mdcStepData);
		// Loading controls can inadvertently set the changed flag, so we reset
		// it here
		// doChange is a flag set to let us know changes were made as a result
		// of an initialization error the user needs to save the changes.
		changed = changed || doChange;
		// check if a dialog is already open
		// Open the dialog
		if (!isOpen) {
			shell.open();
			isOpen = true;
		}
		// if we have a plugin installer set
		// we need do set mdcStepData path
		if (!Const.isEmpty(advancedConfigMeta.getInstallError())) {
			String     message = advancedConfigMeta.getInstallError();
			MessageBox box     = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_ERROR);
			box.setText("installation Error");
			box.setMessage(message);
			if (box.open() == SWT.OK) {
				changed = false;
				cancel();
			}
		}

//		Start of code to determine what os is being used...
		boolean isLoad = true;
		if (advancedConfigMeta.isUpdateData() || !advancedConfigMeta.dataExists()) {
			// Check if we need a restart
			boolean restart;
			try {
				DQTObjectFactory.checkLicense();
				restart = false;
			} catch (DQTObjectException e1) {
				restart = true;
			}
			if (restart && Const.isLinux()) {
				String message;
				message = BaseMessages.getString(PKG, "MDCheckDialog.Linux.Welcome");
				MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText(BaseMessages.getString(PKG, "MDCheckDialog.Welcome.Title"));
				box.setMessage(message);
				if (box.open() == SWT.OK) {
					isLoad = false;
				}
			}
			String sy32  = System.getenv("WINDIR");
			File   sys32 = new File(sy32, "system32\\msvcr110.dll");
			if (!sys32.exists() && Const.isWindows()) {
				InstallRedistrib vcInstaller = new InstallRedistrib(this, SWT.NONE);
				vcInstaller.open();
			}
			if (isLoad && !doSmartMover && !doMatchUp && !doMatchUpGlobal) {
				PluginInstaller pi = advancedConfigMeta.getPluginInstaller();
				if (pi == null) {
					pi = new PluginInstaller(advancedConfigMeta.isContactZone(), log);
				}
				if (pi.showDataDialog()) {
					PluginInstaller.dataPathDialog piDialog = pi.new dataPathDialog(this, SWT.NONE);
					piDialog.open();
				}
			}
			advancedConfigMeta.setPluginInstaller(null);

			if (advancedConfigMeta.isUpdateData()) {

				String message;
				message = BaseMessages.getString(PKG, "MDCheckDialog.Windows.Welcome");

				if (Const.isOSX()) {

					String path        = MDCheckDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					String decodedPath = "";
					try {
						decodedPath = URLDecoder.decode(path, "UTF-8");
						decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("MDCheck"));
					} catch (UnsupportedEncodingException e1) {
						decodedPath = BaseMessages.getString(PKG, "MDCheckDialog.Mac.No.Path");
					}
					String osxMsg = BaseMessages.getString(PKG, "MDCheckDialog.Mac.Welcome", decodedPath);
					message = message + osxMsg;
				}

				MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText(BaseMessages.getString(PKG, "MDCheckDialog.Welcome.Title"));
				box.setMessage(message);
				if (box.open() == SWT.OK) {
					//
				}
			}
		}

		// Do some nagging, if needed
		if (isLoad) {
			if (!nag()) {
				dispose();
				isOpen = false;
			}
		} else {
			if (wAdvanced != null) {
				wAdvanced.setEnabled(false);
			}
		}

		if (showCommunityMessage()) {
			String message = "";
			if ((checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", "Smart Mover");
			} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.Available", "Match Up");
			} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", "Match Up Global");
			} else if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", "IP Locator");
			} else {
				// Contact Verify
				message = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.Available", "Contact Verify");
			}

			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDCheckDialog.Community.Title"));
			box.setMessage(message);
			if (box.open() == SWT.OK) {
				//
			}
		}

		// Wait for it to close
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// Cleanup
		if (fieldMappingTab != null) {
			fieldMappingTab.dispose();
		}
		return stepname;
	}

	private void updateCommunitylabel() {

		String msg = "";
		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.Available.Label", "Match Up");
		} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
			msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.NotAvailable.Label", "Match Up Global");
		} else if ((checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.NotAvailable.Label", "Smart Mover");
		} else if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.NotAvailable.Label", "IP Locator");
		} else if ((checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			if (wTabFolder.getSelection() != null && wTabFolder.getSelection().getText().equals("GeoCode")) {
				msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.NotAvailable.Label", "Contact Verify");
			} else {
				msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.Available.Label", "Contact Verify");
			}
		}

		if (communityLabel != null) {
			communityLabel.setText(msg);
			communityComp.layout();
			centerComp.layout();
		}
	}

	private Composite createCommunityMessage() {
		// Community Label
		communityComp = new Composite(shell, SWT.None);
		FormLayout fl = new FormLayout();
		fl.marginWidth = Const.FORM_MARGIN;
		fl.marginHeight = Const.FORM_MARGIN;
		communityComp.setLayout(fl);
		communityComp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
		FormData fd = new FormData();
		fd.left = new FormAttachment(wTabFolder, 0, SWT.CENTER);
		fd.top = new FormAttachment(wTabFolder, helper.margin);
		fd.bottom = new FormAttachment(100, -50);
		communityComp.setLayoutData(fd);

		centerComp = new Composite(communityComp, SWT.None);
		fl = new FormLayout();
		fl.marginWidth = Const.FORM_MARGIN;
		fl.marginHeight = Const.FORM_MARGIN;
		centerComp.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, -4);
		fd.top = new FormAttachment(0, -4);
		fd.right = new FormAttachment(100, 4);
		fd.bottom = new FormAttachment(100, 4);
		centerComp.setLayoutData(fd);

		communityLabel = new Label(centerComp, SWT.CENTER | SWT.WRAP);
		communityLabel.setText(BaseMessages.getString(PKG, "MDCheckDialog.Community.NotAvailable.Label", "Contact Verify"));
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		communityLabel.setLayoutData(fd);

		Link line3link = new Link(centerComp, SWT.None);
		line3link.setText(BaseMessages.getString(PKG, "MDCheckDialog.Community.Link"));
		fd = new FormData();
		fd.top = new FormAttachment(communityLabel, helper.margin);
		fd.left = new FormAttachment(communityLabel, 0, SWT.CENTER);
		line3link.setLayoutData(fd);
		line3link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Program.launch(BaseMessages.getString(PKG, "MDCheckDialog.Community.URL"));
			}
		});

		props.setLook(communityLabel);
		props.setLook(line3link);
		props.setLook(centerComp);
		updateCommunitylabel();

		return communityComp;
	}

	private boolean showCommunityMessage() {

		boolean showCommunity = false;

		if ((checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {

			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_SmartMover)) {
				if (AdvancedConfigurationMeta.isCommunity()) {
					showCommunity = true;
				}
			}
		}

		if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {

			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)) {
				if (AdvancedConfigurationMeta.isCommunity()) {
					showCommunity = true;
				}
			}
		}

		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {

			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_MatchUpObject)) {
				if (AdvancedConfigurationMeta.isCommunity()) {
					showCommunity = true;
				}
			}
		}

		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {

			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_MatchUpIntl)) {
				if (AdvancedConfigurationMeta.isCommunity()) {
					showCommunity = true;
				}
			}
		}

		if ((checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {

			if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject)) {
				if (AdvancedConfigurationMeta.isCommunity()) {
					showCommunity = true;
				}
			}
		}

		return showCommunity;
	}

	public void refreshTabs() {

		for (MDTab tab : tabs) {
			if ((tab.getClass() != SourcePassThruTab.class) && (tab.getClass() != MatchUpSourcePassThruTab.class)) {
				tab.init(mdcStepData);
			} else {
				tab.getData(mdcStepData);
				tab.init(mdcStepData);
			}
		}
	}

	/**
	 * called from MDCustomDefaults to load tab Data into meta so it can be saved as defaults
	 */
	public int saveDefaultsToMeta() {

		try {
			getTabData(mdcStepData);
		} catch (KettleException e) {
			// nothing
		}
		return checkTypes;
	}

	/**
	 * Indicates that something in the dialog has changed
	 */
	public void setChanged() {

		changed = true;
	}

	/**
	 * Called to display the advanced configuration dialog
	 */
	private void advancedConfiguration(String nagMsg) {

		String title   = "";
		String message = "";
		if ("SetLicenseWarning".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetLicenseWarning.Title");
			if (!MDCheckMeta.isPentahoPlugin()) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetLicenseWarning");
			} else {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetLicenseWarning.Plugin");
			}
		}
		if ("LicenseMissing".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseMissing.Title");
			if (!MDCheckMeta.isPentahoPlugin()) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseMissing");
			} else {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseMissing.Plugin");
			}
		}
		if ("LicenseExpired".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseMissing.Title");
			if (!MDCheckMeta.isPentahoPlugin()) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseExpired", getExpiration());
			} else {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseExpired.Plugin", getExpiration());
			}
		}
		if ("NotEnterprise".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.NotEnterprise.Title");
			String comp = "";
			if (doContactVerify) {
				comp = "Contact Verify";
			} else if (doMatchUp) {
				comp = "Match Up";
			} else if (doMatchUpGlobal) {
				comp = "Match Up Global";
			} else if (doSmartMover) {
				comp = "Smart Mover";
			} else if (doIPLocator) {
				comp = "IP Locator";
			}
			message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.NotEnterprise", comp);
		}
		if ("LicenseError".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseMissing.Title");
			message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.LicenseError");
		}
		if ("SetDataPathWarning".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetDataPathWarning.Title");
			if (!MDCheckMeta.isPentahoPlugin()) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetDataPathWarning");
			} else {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetDataPathWarning.Plugin");
			}
		}
		if ("BadDataPath".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetDataPathWarning.Title");
			if (!MDCheckMeta.isPentahoPlugin()) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.BadDataPathWarning", advancedConfigMeta.getLocalDataPath());
			} else {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.BadDataPathWarning.Plugin", advancedConfigMeta.getLocalDataPath());
			}
		}
		if ("CustomerIDWarning".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.CustomerIDWarning.Title");
			message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.CustomerIDWarning");
		}
		if ("SetServerURLWarning".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetServerURLWarning.Title");
			if (!MDCheckMeta.isPentahoPlugin()) {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetServerURLWarning");
			} else {
				message = BaseMessages.getString(PKG, "MDCheckDialog.WarningDialog.SetServerURLWarning.Plugin");
			}
		}
		if (nagMsg != null) {
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(title);
			box.setMessage(message);
			box.open();
		}
		if ((mdcStepData.getMdInterface() != null)) {
			String id = "";
			if (doContactVerify) {
				id = "ContactVerify";
			}
			if (doSmartMover) {
				id = "SmartMover";
			}
			if (doMatchUp) {
				id = "MatchUp";
			}
			if (doMatchUpGlobal) {
				id = "MatchUpGlobal";
			}
			if (doIPLocator) {
				id = "IpLocator";
			}
			mdcStepData.getMdInterface().setMenuId(id);
			AdvancedConfigurationDialog ad = new AdvancedConfigurationDialog(shell, this, mdcStepData.getMdInterface(), id);
			ad.open();
			advancedChange();
		}
	}

	/**
	 * @return true if cancelled
	 */
	private boolean cancel() {

		if (changed) {
			MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDCheckDialog.WarningDialogChanged.Title"));
			box.setMessage(BaseMessages.getString(PKG, "MDCheckDialog.WarningDialogChanged.Message", Const.CR));
			if (box.open() == SWT.NO) {
				return false;
			}
			// Revert persistent property changes
			MDProps.revert();
		}
		// Cancel change
		stepname = null;
		changed = false;
		isOpen = false;
		// Close the dialog
		dispose();
		return true;
	}

	private void checkShellSize(Shell sh) {

		sh.pack();
		int       x       = sh.getSize().x;
		int       y       = sh.getSize().y;
		Rectangle mBounds = getShell().getDisplay().getMonitors()[0].getClientArea();
		if (y >= mBounds.height) {
			y = (mBounds.height / 10) * 9;
		}
		if (x >= mBounds.width) {
			x = (mBounds.width / 10) * 9;
		}
		sh.setSize(x, y);
		// center shell
		int lx = (mBounds.width / 2) - (x / 2);
		int ly = (mBounds.height / 2) - (y / 2);
		sh.setLocation(lx, ly);
	}

	private void doInitalizeCheck(MDCheckStepData data) {
		// Reset initialization states
		data.getAddressVerify().setInitializeOK(true);
		data.getGeoCoder().setInitializeOK(true);
		data.getNameParse().setInitializeOK(true);
		data.getEmailVerify().setInitializeOK(true);
		data.getPhoneVerify().setInitializeOK(true);
		Validations checker = new Validations();
		// Call checker only for local objects in the contact verification
		// component
		if ((data.getAdvancedConfiguration().getServiceType() == ServiceType.Local) && doContactVerify) {
			// Do the initialization checks
			checker.checkInitialization(data);
			// The validator thinks a warning dialog should be shown
			if (checker.showDialog()) {
				ValidationsDialog dlg = new ValidationsDialog(getShell(), this, checker);
				dlg.open();
			}
			// Update persistent time stamps
			try {
				checker.saveTimeProps();
			} catch (KettleException e) {
				log.logError("Error saving time Props" + e.getMessage());
			}
			// See if something changed because of the validation
			doChange = checker.needChange();
		} else {
			// always check name because it is used for local and web
			if ((data.getAdvancedConfiguration().getProducts() & AdvancedConfigurationMeta.MDLICENSE_Name) != 0) {
				data.getNameParse().checkInit(checker);
			}
		}
	}

	private String getExpiration() {

		String exp = "";
		exp = MDProps.getProperty(AdvancedConfigurationMeta.TAG_PRIMARY_EXPIRATION, "").trim();
		if (Const.isEmpty(exp)) {
			exp = MDProps.getProperty(AdvancedConfigurationMeta.TAG_TRIAL_EXPIRATION, "").trim();
		}
		return exp;
	}

	private String getHelpURL() {

		CTabItem wTab = wTabFolder.getSelection();
		MDTab    tab  = (MDTab) wTab.getData();
		String   url  = BaseMessages.getString(PKG, tab.getHelpURLKey());
		return url;
	}

	/**
	 * Loads the mdcStepData into the dialog
	 */
	private boolean init(MDCheckStepData data) {
		// Do initialization checks only for the contact verify plugin
		if (doContactVerify) {
			doInitalizeCheck(data);
		}
		// Load the dialog tabs
		boolean changed = false;
		for (MDTab tab : tabs) {
			changed = tab.init(mdcStepData) || changed;
		}
		checkShellSize(getShell());
		return changed;
	}

	/**
	 * Called to do some nagging, if needed
	 *
	 * @return true if we can continue
	 */
	private boolean nag() {
		// See if there was a problem loading the properties file. If there was
		// then we cannot proceed.
		if (MDProps.getLoadException() != null) {
			new ErrorDialog(getShell(), BaseMessages.getString(PKG, "MDCheckDialog.BadProperties.Title"), BaseMessages.getString(PKG, "MDCheckDialog.BadProperties.Message"), MDProps.getLoadException());
			return false;
		}
		// See if there is some configuration property that should be set before
		// anything else
		String nagMsg       = advancedConfigMeta.shouldNag();
		int    welcomeCount = 0;
		welcomeCount = Integer.parseInt(MDProps.getProperty(TAG_FIRSTRUN, "0"));
		if ((welcomeCount < 3) && MDCheckMeta.isPentahoPlugin() && "SetLicenseWarning".equals(nagMsg)) {
			welcomeCount++;
			MDProps.setProperty(TAG_FIRSTRUN, String.valueOf(welcomeCount));
			try {
				MDProps.save();
			} catch (IOException e) {
				// ignore should not happen at this point
			}
			nagMsg = null;
			showWelcomDialog();
		}
		if (nagMsg != null) {
			advancedConfiguration(nagMsg);
		}
		return true;
	}

	private void openMappingDialog() {

		mdcStepData.getFieldMapping().setSourceFields(sourceFields);
		mdcStepData.getFieldMapping().setData(mdcStepData);
		FieldMappingDialog fd = new FieldMappingDialog(this);
		fd.open();
	}

	private void showWelcomDialog() {

		WelcomeDialog welcomeDialog = new WelcomeDialog(this, 0);
		welcomeDialog.open();
		if (!Const.isEmpty(licString)) {
			MDProps.setProperty(TAG_PRIMARY_LICENSE, licString);
			try {
				MDProps.save();
			} catch (IOException e) {
				// Ignore if happens
			}
		}
		if (mdcStepData.getMdInterface() != null) {
			String id = "";
			if (doContactVerify) {
				id = "ContactVerify";
			}
			if (doSmartMover) {
				id = "SmartMover";
			}
			if (doMatchUp) {
				id = "MatchUp";
			}
			if (doMatchUpGlobal) {
				id = "MatchUpGlobal";
			}
			if (doIPLocator) {
				id = "IpLocator";
			}
			mdcStepData.getMdInterface().setMenuId(id);
			AdvancedConfigurationDialog ad;
			if (continueToAdvancedConfig) {
				ad = new AdvancedConfigurationDialog(shell, this, mdcStepData.getMdInterface(), id);
				ad.open();
				advancedChange();
			}
		}
	}

	/**
	 * Called to validate settings. If there is a problem the user will be alerted and given the option of canceling.
	 */
	private boolean valid(MDCheckStepData data) {

		List<String> warnings = new ArrayList<String>();
		List<String> errors   = new ArrayList<String>();
		Validations  checker  = new Validations(); // used to cleaar lists after
		// checks are done
		// Validate step name
		if (Const.isEmpty(wStepname.getText().trim())) {
			errors.add(getValidationMessage("MissingStepName"));
		}
		// Validate individual settings
		data.validate(warnings, errors);
		checker.clearOutputList();
		checker.clearInputList();
		// If there were problems then display a dialog
		if ((warnings.size() > 0) || (errors.size() > 0)) {
			// The style of message will depend on whether there are fatal
			// errors or not
			String[] buttons;
			int      dialogType;
			int      cancelIdx;
			if (errors.size() > 0) {
				buttons = new String[] { IDialogConstants.CANCEL_LABEL };
				dialogType = MessageDialog.ERROR;
				cancelIdx = 0;
			} else {
				buttons = new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
				dialogType = MessageDialog.WARNING;
				cancelIdx = 1;
			}
			// Build the warning/error message
			StringBuffer message = new StringBuffer();
			message.append(getValidationMessage("MessageBoxPreamble")).append("\n");
			message.append("\n");
			if (warnings.size() > 0) {
				if (warnings.size() == 1) {
					message.append(getValidationMessage("MessageBoxWarning")).append("\n");
				} else {
					message.append(getValidationMessage("MessageBoxWarnings", "" + warnings.size())).append("\n");
				}
				for (String warning : warnings) {
					message.append("   -  ").append(warning).append("\n");
				}
				message.append("\n");
				// for result code verify
				if (dialogType == MessageDialog.WARNING) {
					message.append(getValidationMessage("MessageBoxOkCancel"));
				}
			}
			if (errors.size() > 0) {
				if (errors.size() == 1) {
					message.append(getValidationMessage("MessageBoxError")).append("\n");
				} else {
					message.append(getValidationMessage("MessageBoxErrors", "" + errors.size())).append("\n");
				}
				for (String error : errors) {
					message.append("   -  ").append(error).append("\n");
				}
				message.append("\n");
				message.append(getValidationMessage("MessageBoxErrorCaveat")).append("\n");
				message.append("\n");
			}
			// Display the message box
			MessageDialog box    = new MessageDialog(getShell(), getValidationMessage("MessageBoxTitle"), null, message.toString(), dialogType, buttons, 1);
			int           button = box.open() & 0xFF;
			if (button == cancelIdx) {
				return false;
			}
		}
		// Everything's good
		return true;
	}
}
