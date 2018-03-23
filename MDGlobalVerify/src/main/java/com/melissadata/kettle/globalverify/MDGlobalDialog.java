package com.melissadata.kettle.globalverify;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.melissadata.cz.support.MDPropTags;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.cz.ui.BrowserDialog;
import com.melissadata.cz.ui.MDDialogParent;
import com.melissadata.kettle.MDSettings.AdvancedConfigInterface;
import com.melissadata.kettle.MDSettings.AdvancedConfigurationDialog;
import com.melissadata.kettle.globalverify.data.EmailFields;
import com.melissadata.kettle.globalverify.installer.InstallRedistrib;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressHelper;
import com.melissadata.kettle.globalverify.support.MDTab;
import com.melissadata.kettle.globalverify.ui.AddressTab;
import com.melissadata.kettle.globalverify.ui.EmailTab;
import com.melissadata.kettle.globalverify.ui.NameTab;
import com.melissadata.kettle.globalverify.ui.OutputFilterTab;
import com.melissadata.kettle.globalverify.ui.PhoneTab;
import com.melissadata.kettle.globalverify.ui.SourcePassThruTab;
import com.melissadata.kettle.globalverify.ui.WelcomeDialog;

public class MDGlobalDialog extends BaseStepDialog implements StepDialogInterface , MDDialogParent {
	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {
		return BaseMessages.getString(PKG, "MDGlobalDialog.Validation." + name, args);
	}
	private static Class<?>						PKG							= MDGlobalDialog.class;
	private static final String					TAG_FIRSTRUN				= "plugin_first_run";
	private static final String					TAG_PRIMARY_LICENSE			= "license";
	private MDGlobalMeta						input;
	private MDGlobalAddressHelper				helper;
	private ModifyListener						lsModified;
	private CTabFolder							wTabFolder;
	private List<MDTab>							tabs;
	private static boolean						isOpen;
	private boolean								isOutputMode;
	private SortedMap<String, SourceFieldInfo>	sourceFields;
	private SortedMap<String, SourceFieldInfo>	inputFields;
	private AdvancedConfigInterface				mdInterface;
//	private HashMap<String, String>				countryDesc;
//	private List<String>						countryShortList;
	private PluginInterface						plugin;
	public static String						licString;
	private 		Button wAdvanced = null;

	public static boolean						continueToAdvancedConfig	= true;

	public MDGlobalDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
		// Keep reference to original input
		input = (MDGlobalMeta) baseStepMeta;
		isOutputMode = false;
	}

	/**
	 * @return true if canceled
	 */
	private void advancedConfig(String nagMsg) {
		String title = "";
		String message = "";
		boolean showAdv = false;
		//
		if ("LicenseNotSet".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDGlobalAddress.WarningDialog.SetLicenseWarning.Title");
			message = BaseMessages.getString(PKG, "MDGlobalAddress.WarningDialog.SetLicenseWarning");
			showAdv = true;
		} else if ("tlicInvalidLicense".equals(nagMsg) || "tlicInvalidProduct".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDGlobalAddress.WarningDialog.LicenseMissing.Title");
			message = BaseMessages.getString(PKG, "MDGlobalAddress.WarningDialog.LicenseMissing");
		} else if ("tlicLicenseExpired".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDGlobalAddress.WarningDialog.LicenseMissing.Title");
			message = BaseMessages.getString(PKG, "MDGlobalAddress.WarningDialog.LicenseExpired", getExpiration());
		} else if ("NotEnterprise".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDGlobalDialog.WarningDialog.NotEnterprise.Title");
			message = BaseMessages.getString(PKG, "MDGlobalDialog.WarningDialog.NotEnterprise");
		}
		if (!Const.isEmpty(message)) {
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(title);
			box.setMessage(message);
			box.open();
		}
		if (showAdv && MDGlobalMeta.isEnterprise()) {
			String id = "";
			id = "GlobalVerify";
			mdInterface.setMenuId(id);
			AdvancedConfigurationDialog ad = new AdvancedConfigurationDialog(shell, this, mdInterface, id);
			ad.open();
		}
	}

	/**
	 * @return true if canceled
	 */
	private boolean cancel() {
		if (changed) {
			MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDGlobalDialog.WarningDialogChanged.Title"));
			box.setMessage(BaseMessages.getString(PKG, "MDGlobalDialog.WarningDialogChanged.Message", Const.CR));
			if (box.open() == SWT.NO)
				return false;
		}
		// Cancel change
		stepname = null;
		changed = false;
		isOpen = false;
		// Close the dialog
		dispose();
		return true;
	}

	private String checkLicense() {
		String lic = MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "");
		String trialLic = MDProps.getProperty(MDPropTags.TAG_TRIAL_LICENSE, "");
		String licTestResult = getTestResult();
		if (Const.isEmpty(lic) && Const.isEmpty(trialLic))
			return "LicenseNotSet";
		if (!MDGlobalMeta.isEnterprise()) {
			licTestResult = "NotEnterprise";
		}
		return licTestResult;
	}

	private void checkShellSize() {
		// get screen size
		int screenWidth = 0;
		int screenHeight = 0;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		DisplayMode dm = gd.getDisplayMode();
		screenWidth = dm.getWidth();
		screenHeight = dm.getHeight();

		shell.pack();
		shell.setSize((screenWidth - (screenWidth / 4)), (screenHeight - (screenHeight / 4)));
		int lx = (screenWidth / 2) - (shell.getBounds().width / 2);
		int ly = (screenHeight / 2) - (shell.getBounds().height / 2);
		shell.setLocation(lx, ly);
	}

	/**
	 * @return Information about the next step
	 */
	public List<StepMeta> findNextSteps() {
		StepMeta stepInfo = transMeta.findStep(stepname);
		if (stepInfo != null)
			return transMeta.findNextSteps(stepInfo);
		return new ArrayList<StepMeta>();
	}

	/**
	 * @param stepname
	 * @return The step info for the given step name
	 */
	public StepMeta findStep(String stepname) {
		return transMeta.findStep(stepname);
	}

//	public void getCountries() {
//		ResourceBundle bundle = GlobalMessages.getBundle("com.melissadata.kettle.globalverify.messages.messages", PKG);
//		if (bundle != null) {
//			Enumeration<String> keys = bundle.getKeys();
//			while (keys.hasMoreElements()) {
//				String rc = keys.nextElement();
//				String categoryPrefix = "MDGlobalAddress.Country.Description.";
//				if (rc.startsWith(categoryPrefix)) {
//					String msg = BaseMessages.getString(PKG, rc);
//					String postfix = rc.substring(categoryPrefix.length());
//					if (countryDesc == null) {
//						countryDesc = new HashMap<String, String>();
//					}
//					if (countryShortList == null) {
//						countryShortList = new ArrayList<String>();
//					}
//					countryShortList.add(msg);
//					countryDesc.put(postfix, msg);
//				}
//			}
//		}
//		Collections.sort(countryShortList);
//	}

//	public HashMap<String, String> getCountryDesc() {
//		return countryDesc;
//	}
//
//	public List<String> getCountryShortList() {
//		return countryShortList;
//	}

	/**
	 * @return The data to this dialog
	 */
	@Override
	public Object getData() {
		return this;
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	private void getData(MDGlobalMeta data) throws KettleException {
		// Loads tab data into the meta structure
		for (MDTab tab : tabs) {
			tab.getData(data);
		}
	}

	private String getExpiration() {
		String exp = "";
		exp = MDProps.getProperty(MDPropTags.TAG_PRIMARY_EXPIRATION, "").trim();
		if (Const.isEmpty(exp)) {
			exp = MDProps.getProperty(MDPropTags.TAG_TRIAL_EXPIRATION, "").trim();
		}
		return exp;
	}

	public MDGlobalAddressHelper getHelper() {
		return helper;
	}

	private String getHelpURL() {
		CTabItem wTab = wTabFolder.getSelection();
		MDTab tab = (MDTab) wTab.getData();
		String url = "";
		if (tab != null)
			url = tab.getHelpURLKey();

		return url;
	}

	public MDGlobalMeta getInput() {
		return input;
	}

	/**
	 * Called to retrieve the selected input fields
	 *
	 * @return
	 */
	public SortedMap<String, SourceFieldInfo> getInputFields() {
		log.logDebug("Getting input fields");
		// If we already have source fields then return them
		if (inputFields != null)
			return inputFields;
		// Allocate new source field map
		inputFields = new TreeMap<String, SourceFieldInfo>();
		// Retrieve the source fields from the previous step
		try {
			String[] inputArray = input.getInputs();
			// Remember these fields...
			for (String name : inputArray) {
				if (name.length() > 0) {
					SourceFieldInfo field = new SourceFieldInfo(name);
					inputFields.put(name, field);
				}
			}
			log.logDebug("Got " + inputFields.size() + " input fields");
		} catch (Exception e) {
			log.logError(toString(), BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
		}
		return inputFields;
	}

	@Override
	public LogChannel getLog() {
		return log;
	}

	/**
	 * @return A naming prefix that will be used when looking up label strings
	 */
	public String getNamingPrefix() {
		return "MDGlobalDialog";
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
	@Override
	public Shell getShell() {
		return shell;
	}

	/**
	 * Called to retrieve the source fields of the input step
	 *
	 * @return
	 */
	@Override
	public SortedMap<String, SourceFieldInfo> getSourceFields() {
		// If we already have source fields then return them
		if (sourceFields != null)
			return sourceFields;
		// Allocate new source field map
		sourceFields = new TreeMap<String, SourceFieldInfo>();
		// Retrieve the source fields from the previous step
		log.logDebug("Getting source fields");
		try {
			StepMeta stepMeta = transMeta.findStep(stepname);
			if (stepMeta != null) {
				RowMetaInterface row = transMeta.getPrevStepFields(stepMeta);
				// Remember these fields...
				for (int i = 0; i < row.size(); i++) {
					String name = row.getValueMeta(i).getName();
					SourceFieldInfo field = new SourceFieldInfo(name);
					sourceFields.put(name, field);
				}
				log.logDebug("Got " + sourceFields.size() + " source fields");
			}
		} catch (KettleException e) {
			log.logError(toString(), BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
		}
		return sourceFields;
	}

	/**
	 * @return Access to the variable space of the transform meta data
	 */
	@Override
	public VariableSpace getSpace() {
		return transMeta;
	}

	public MDGlobalMeta getStepMeta() {
		return input;
	}

	public CTabFolder getTabFolder() {
		return wTabFolder;
	}

	private String getTestResult() {
		int nLicensedProduct = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RETVAL, "0"));
		int nTrialLicensedProduct = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RETVAL, "0"));
		if ((nLicensedProduct & MDPropTags.MDLICENSE_GlobalVerify) != 0)
			return MDProps.getProperty(MDPropTags.TAG_PRIMARY_TEST_RESULT, "");
		else if ((nTrialLicensedProduct & MDPropTags.MDLICENSE_GlobalVerify) != 0)
			return MDProps.getProperty(MDPropTags.TAG_TRIAL_TEST_RESULT, "");
		else
			return MDProps.getProperty(MDPropTags.TAG_PRIMARY_TEST_RESULT, "");
	}

	/**
	 * @return Master title for the component
	 */
	public String getTitle() {
		if(MDGlobalMeta.isEnterprise()) {
			return BaseMessages.getString(PKG, "MDGlobalDialog.Shell.Title");
		} else {
			return BaseMessages.getString(PKG, "MDGlobalDialog.Shell.Community.Title");
		}
	}

	/**
	 * Called to display context sensitive help information
	 */
	protected void help() {
		CTabItem wTab = wTabFolder.getSelection();
		MDTab tab = (MDTab) wTab.getData();
		String url = BaseMessages.getString(PKG, tab.getHelpURLKey());
		BrowserDialog.displayURL(getShell(), this, url);
	}

	/**
	 * Loads the data into the dialog
	 */
	private void init(MDGlobalMeta data) {
		//getCountries();
		mdInterface = new AdvancedConfigInterface();
		mdInterface.setDataValues();

		for (MDTab tab : tabs) {
			tab.init(data);
		}
		checkShellSize();
	}

	public boolean isOutputMode() {
		return isOutputMode;
	}

	/**
	 * Called to do some nagging, if needed
	 *
	 * @return true if we can continue
	 */
	private boolean nag() {
		// See if there was a problem loading the properties file. If there was then we cannot proceed.
		if (MDProps.getLoadException() != null) {
			new ErrorDialog(getShell(), BaseMessages.getString(PKG, "MDGlobalAddress.BadProperties.Title"), BaseMessages.getString(PKG, "MDGlobalAddress.BadProperties.Message"), MDProps.getLoadException());
			return false;
		}

		// Block the user from configuring this step to run in multiple copies.
		if (stepMeta.getCopies() > 1) {
			MessageDialog.openError(getShell(), getTitle(), BaseMessages.getString(PKG, "MDGlobalAddress.MultipleCopies.Message"));
			return false;
		}
		// See if there is some configuration property that should be set before anything else
		String nagMsg = checkLicense();
		int welcomeCount = 0;
		welcomeCount = Integer.parseInt(MDProps.getProperty(TAG_FIRSTRUN, "0"));
		if ((welcomeCount < 3) && "LicenseNotSet".equals(nagMsg)) {
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
			advancedConfig(nagMsg);
		}
		return true;
	}

	public void ok() {
		stepname = wStepname.getText(); // return value
		try {
			// Don't do anything if nothing changed
			if (changed) {
				// Get data from dialog elements
				getData(input);
				// Tell the meta data that it has changed
				input.setChanged();
			}
			isOpen = false;
			// validate
			if (valid(input)) {
				// Close the dialog
				dispose();
			}
		} catch (KettleException e) {
			new ErrorDialog(shell, BaseMessages.getString(PKG, "MDGlobalDialog.IllegalDialogSettings.Title"), BaseMessages.getString(PKG, "MDGlobalDialog.IllegalDialogSettings.Message"), e);
		}
	}

	@Override
	public String open() {
		// Initialize important values
		Shell parent = getParent();
		Display display = parent.getDisplay();
		// This helper allows for common functionality to be shared between dialog elements
		helper = new MDGlobalAddressHelper(this);
		// Create the dialog
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, input);
		// Set dialog title based on plugin type
		shell.setText(getTitle());
		FormLayout fl = new FormLayout();
		fl.marginWidth = Const.FORM_MARGIN;
		fl.marginHeight = Const.FORM_MARGIN;
		shell.setLayout(fl);
		// Modification will set the changed flag
		lsModified = new ModifyListener() {
			@Override
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
		Label versionText = new Label(shell, SWT.LEFT);
		String version = BaseMessages.getString(PKG, "MDGlobalDialog.VersionPrefix");
		Class<?> clazz = this.getClass();
		String className = clazz.getSimpleName() + ".class";
		String classPath = "";
		if ((clazz != null) && (className != null)) {
			classPath = clazz.getResource(className).toString();
		}
		if (!classPath.startsWith("jar")) {
			version = BaseMessages.getString(PKG, "MDGlobalDialog.VersionNotFound");
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest = new Manifest(new URL(manifestPath).openStream());
				String implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				if (implVersion == null) {
					version = BaseMessages.getString(PKG, "MDGlobalDialog.VersionNotFound");
				} else {
					version = BaseMessages.getString(PKG, "MDGlobalDialog.VersionPrefix") + " " + implVersion;
				}
			} catch (Exception e) {
				version = BaseMessages.getString(PKG, "MDGlobalDialog.VersionNotFound");
			}
		}
		versionText.setText(version);
		props.setLook(versionText);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment((helper.colWidth[0] - 15), -helper.margin);
		versionText.setLayoutData(fd);
		tabs = new ArrayList<MDTab>();
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
		wStepname.setText(BaseMessages.getString(PKG, "MDGlobalVerifyPlugin.FullStep.Name"));
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
		tabs.add(new NameTab(this));
		tabs.add(new AddressTab(this));
		tabs.add(new PhoneTab(this));
		tabs.add(new EmailTab(this));
		// tabs.add(new ReportingTab(this));
		tabs.add(new SourcePassThruTab(this, transMeta));
		tabs.add(new OutputFilterTab(this));
		// Place the tab folder in the dialog
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wStepname, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(92, -50);
		wTabFolder.setLayoutData(fd);
		// THE BUTTONS
		// only add advanced config button if not running ContactZone
		wAdvanced = new Button(shell, SWT.PUSH);
		wAdvanced.setText(BaseMessages.getString(PKG, "MDGlobalDialog.Button.Advanced")); //$NON-NLS-1$
		wAdvanced.setToolTipText(BaseMessages.getString(PKG, "MDGlobalDialog.Button.Advanced.ToolTip"));

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); //$NON-NLS-1$

		plugin = PluginRegistry.getInstance().getPlugin(StepPluginType.class, stepMeta.getStepMetaInterface());
		createHelpButton(shell, stepMeta, plugin);

		Control last = null;
		if(!MDGlobalMeta.isEnterprise()){
			last = createCommunityMessage();
		} else {
			last = wTabFolder;
		}

		if (wAdvanced != null) {
			setButtonPositions(new Button[] { wOK, wCancel, wAdvanced }, helper.margin, last);
		}

		// Add listeners
		Listener lsAdvanced = new Listener() {
			@Override
			public void handleEvent(Event e) {
				openAdvanced();
			}
		};
		lsOK = new Listener() {
			@Override
			public void handleEvent(Event e) {
				ok();
			}
		};
		lsCancel = new Listener() {
			@Override
			public void handleEvent(Event e) {
				cancel();
			}
		};

		if (wAdvanced != null) {
			wAdvanced.addListener(SWT.Selection, lsAdvanced);
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
		wTabFolder.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (plugin != null) {
					plugin.setDocumentationUrl(getHelpURL());
				}
			}
		});
		// Set the shell size, based upon previous time...
		setSize();
		// Load data into dialog
		init(input);
		// Loading controls can inadvertently set the changed flag, so we reset it here
		changed = false;
		boolean restart;
		boolean isLoad = true;
		try {
			DQTObjectFactory.checkLicense();
			restart = false;
		} catch (DQTObjectException e1) {
			restart = true;
		}
		if (restart && Const.isLinux()) {
			String message = BaseMessages.getString(PKG, "MDGlobalDialog.Linux.Welcome");
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDGlobalDialog.Welcome.Linux.Title"));
			box.setMessage(message);
			if (box.open() == SWT.NO) {
				isLoad = false;
			}
		}
		String sy32 = System.getenv("WINDIR");
		File sys32 = new File(sy32, "system32\\msvcr110.dll");
		if (!sys32.exists() && Const.isWindows()) {
			InstallRedistrib vcInstaller = new InstallRedistrib(this, SWT.NONE);
			vcInstaller.open();
		}

		// Open the dialog
		if (isOpen) {
			dispose();
		} else {
			shell.open();
			isOpen = true;
		}

		if (input.isUpdateData()) {

			String message;
			message = BaseMessages.getString(PKG, "MDGlobalDialog.Windows.Welcome");
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);

			if (Const.isOSX()) {

				String path = MDGlobalDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				String decodedPath = "";
				try {
					decodedPath = URLDecoder.decode(path, "UTF-8");
					decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("MDGlobal"));
				} catch (UnsupportedEncodingException e1) {
					decodedPath = "your MDGlobalPlugin Dir";
				}
				String osxMsg = "\n\nMac OS users:\nPlease see MacOS_ReadMe.txt located in\n" + decodedPath + "\nand follow instructions before restarting";
				message = message + osxMsg;
			}

			box.setText("Welcome");
			box.setMessage(message);
			if (box.open() == SWT.OK) {
				//
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

		if(!MDGlobalMeta.isEnterprise()){
			if(MDProps.getProperty(SettingsTags.TAG_PRIMARY_PRODUCT,"").contains(SettingsTags.MDLICENSE_PRODUCT_Community) ){
				MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText(BaseMessages.getString(PKG, "MDGlobalDialog.Community.Title"));
				box.setMessage(BaseMessages.getString(PKG,"MDGlobalDialog.Community.PopupMessage"));
				if (box.open() == SWT.OK) {

				}
			}
		}
		// Wait for it to close
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return stepname;
	}

	private Composite createCommunityMessage() {
		// Community Label
		Composite communityComp = new Composite(shell, SWT.None);
		FormLayout fl = new FormLayout();
		fl.marginWidth = Const.FORM_MARGIN;
		fl.marginHeight = Const.FORM_MARGIN;
		communityComp.setLayout(fl);
		communityComp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
		FormData fd = new FormData();
		fd.left = new FormAttachment(wTabFolder, 0, SWT.CENTER);
		fd.top = new FormAttachment(wTabFolder, helper.margin);
		//fd.right = new FormAttachment( 50,  0);
		fd.bottom = new FormAttachment(100, -50);
		communityComp.setLayoutData(fd);

		Composite centerComp = new Composite(communityComp, SWT.None);
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

		Label line1 = new Label(centerComp, SWT.CENTER | SWT.WRAP);
		line1.setText(BaseMessages.getString(PKG, "MDGlobalDialog.Community.Label"));
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//fd.bottom = new FormAttachment(100, 0);
		line1.setLayoutData(fd);

		Link line3link = new Link(centerComp, SWT.None);
		line3link.setText(BaseMessages.getString(PKG,"MDGlobalDialog.Community.Link"));
		fd = new FormData();
		fd.top = new FormAttachment(line1, 0);
		fd.left = new FormAttachment(line1, 0, SWT.CENTER);
		//fd.right = new FormAttachment(100, 0);
		//fd.bottom = new FormAttachment(100, 0);
		line3link.setLayoutData(fd);
		line3link.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				Program.launch(BaseMessages.getString(PKG,"MDGlobalDialog.Community.URL"));
			}
		});

		props.setLook(line1);
		props.setLook(line3link);
		props.setLook(centerComp);

		return communityComp;
	}

	private void openAdvanced() {
		if (MDGlobalMeta.isEnterprise()) {
			String id = "";
			id = "GlobalVerify";
			mdInterface.setMenuId(id);
			AdvancedConfigurationDialog ad = new AdvancedConfigurationDialog(shell, this, mdInterface, id);
			ad.open();
			for (MDTab tab : tabs) {
				tab.advancedConfigChanged();
			}
		}
	}

	public TransMeta getTransMeta(){
		return transMeta;
	}

	public String getStepName(){
		return stepname;
	}

	/**
	 * Indicates that something in the dialog has changed
	 */
	public void setChanged() {
		changed = true;
	}

	public void clearChanged() {
		changed = false;
	}

	public void setHelper(MDGlobalAddressHelper helper) {
		this.helper = helper;
	}

	public void setInput(MDGlobalMeta input) {
		this.input = input;
	}

	public void setOutputMode(boolean isOutputMode) {
		this.isOutputMode = isOutputMode;
	}

	public void setTabFolder(CTabFolder wTabFolder) {
		this.wTabFolder = wTabFolder;
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
		if (continueToAdvancedConfig) {
			String id = "";
			id = "GlobalVerify";
			mdInterface.setMenuId(id);
			AdvancedConfigurationDialog ad = new AdvancedConfigurationDialog(shell, this, mdInterface, id);
			ad.open();
		}
	}

	/*
	 *Update the help URl
	 */
	public void updatePluginDocumintation(){
		if (plugin != null)
			plugin.setDocumentationUrl(getHelpURL());
	}


	/**
	 * Called to validate settings. If there is a problem the user will be alerted and given the option of canceling.
	 */
	private boolean valid(MDGlobalMeta meta) {
		List<String> warnings = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();
		Validations checker = new Validations(); // used to cleaar lists after checks are done
		// Validate step name
		if (Const.isEmpty(wStepname.getText().trim())) {
			errors.add(getValidationMessage("MissingStepName"));
		}
		// Validate individual settings
		// checker.validate(meta, warnings, errors);
//		int dd = 0;
//		try {
//			dd = Integer.parseInt(input.getEmailMeta().emailFields.webOptionFields.get(EmailFields.TAG_OPTION_DAYS_SINCE).metaValue);
//			if ((dd < 3) || (dd > 365)) {
//				errors.add("Email: Days Since Last Verified must be a number 3 to 365");
//			}
//		} catch (NumberFormatException nfe) {
//			errors.add("Email: Days Since Last Verified must be a number 3 to 365");
//		}
		checker.clearOutputList();
		checker.clearInputList();
		// If there were problems then display a dialog
		if ((warnings.size() > 0) || (errors.size() > 0)) {
			// The style of message will depend on whether there are fatal errors or not
			String[] buttons;
			int dialogType;
			int cancelIdx;
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
			MessageDialog box = new MessageDialog(getShell(), getValidationMessage("MessageBoxTitle"), null, message.toString(), dialogType, buttons, 1);
			int button = box.open() & 0xFF;
			if (button == cancelIdx)
				return false;
		}
		// Everything's good
		return true;
	}
}
