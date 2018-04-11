package com.melissadata.kettle.personator;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import com.melissadata.kettle.personator.mapping.FieldMappingDialog;
import com.melissadata.kettle.personator.support.MDPersonatorHelper;
import com.melissadata.kettle.personator.support.MDTab;
import com.melissadata.kettle.personator.ui.InputPersonatorTab;
import com.melissadata.kettle.personator.ui.OutputFilterTab;
import com.melissadata.kettle.personator.ui.OutputPersonatorTab;
import com.melissadata.kettle.personator.ui.PersonatorOptionsTab;
import com.melissadata.kettle.personator.ui.SourcePassThruTab;
import com.melissadata.kettle.personator.ui.WelcomeDialog;

public class MDPersonatorDialog extends BaseStepDialog implements StepDialogInterface , MDDialogParent {
	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {
		return BaseMessages.getString(PKG, "MDPersonatorDialog.Validation." + name, args);
	}
	private static Class<?>						PKG							= MDPersonatorDialog.class;
	private static final String					TAG_CONTACTZONE				= "contact_zone";
	private static final String					TAG_FIRSTRUN				= "plugin_first_run";
	private static final String					TAG_PRIMARY_LICENSE			= "license";
	private MDPersonatorMeta					input;
	private MDPersonatorHelper					helper;
	private ModifyListener						lsModified;
	private CTabFolder							wTabFolder;
	private List<MDTab>							tabs;
	private static boolean						isOpen;
	private boolean								isOutputMode;
	private SortedMap<String, SourceFieldInfo>	sourceFields;
	private SortedMap<String, SourceFieldInfo>	inputFields;
	private AdvancedConfigInterface				mdInterface;
	private OutputPersonatorTab					outPersonatorTab;
	private PersonatorOptionsTab				optionsTab;
	private InputPersonatorTab					inputTab;
	public static String						licString;
	public static boolean						continueToAdvancedConfig	= true;
	private PluginInterface						plugin;
	private Button										wMapFields;
	private 		Button wAdvanced = null;

	public MDPersonatorDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
		input = (MDPersonatorMeta) baseStepMeta;
		isOutputMode = false;
	}

	/**
	 * @return true if canceled
	 */
	private void advancedConfig(String nagMsg) {
		String title = "";
		String message = "";
		boolean showAdv = false;
		if ("SetLicenseWarning".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseNotSet.Title");
			message = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseNotSet");
			showAdv = true;
		}
		if ("tlicLicenseExpired".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseInvalid.Title");
			message = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseExpired", getExpiration());
		}
		if ("tlicInvalidLicense".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseInvalid.Title");
			message = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseInvalid");
		}
		if ("tlicInvalidProduct".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseInvalid.Title");
			message = BaseMessages.getString(PKG, "MDPersonator.Error.LicenseInvalid");
		}
		if ("NotEnterprise".equals(nagMsg)) {
			title = BaseMessages.getString(PKG, "MDPersonator.Error.NotEnterprise.Title");
			message = BaseMessages.getString(PKG, "MDPersonator.Error.NotEnterprise");
		}
		if (!Const.isEmpty(message)) {
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(title);
			box.setMessage(message);
			box.open();
		}
		if (showAdv) {
			String id = "";
			id = "Personator";
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
			box.setText(BaseMessages.getString(PKG, "MDPersonatorDialog.WarningDialogChanged.Title"));
			box.setMessage(BaseMessages.getString(PKG, "MDPersonatorDialog.WarningDialogChanged.Message", Const.CR));
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
		String nagMessage = null;
		int retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, "0"));
		retVal |= Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RET_VAL, "0"));
		String lic = MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "").trim();
		String trialLic = MDProps.getProperty(MDPropTags.TAG_TRIAL_LICENSE, "").trim();
		if (Const.isEmpty(lic) && Const.isEmpty(trialLic)) {
			nagMessage = "SetLicenseWarning";
		} else if ((retVal & MDPropTags.MDLICENSE_Personator) == 0) {
			nagMessage = MDProps.getProperty(MDPropTags.TAG_PRIMARY_TEST_RESULT, "");
			// if this is ok they must have trial license
			if ("tlicNoError".equals(nagMessage)) {
				nagMessage = MDProps.getProperty(MDPropTags.TAG_TRIAL_TEST_RESULT, "");
			}
		}
		if (!MDPersonatorMeta.isEnterprise()) {
			nagMessage = "NotEnterprise";
		}
		return nagMessage;
	}

	private void checkShellSize() {
		// get screen size
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		int screenWidth = 0;
		int screenHeight = 0;
		for (GraphicsDevice element : gs) {
			DisplayMode dm = element.getDisplayMode();
			screenWidth = dm.getWidth();
			screenHeight = dm.getHeight();
		}
		shell.pack();
		shell.setSize((screenWidth - (screenWidth / 10)), (screenHeight - (screenHeight / 10)));
		int lx = (screenWidth / 2) - (shell.getBounds().width / 2);
		int ly = (screenHeight / 2) - (shell.getBounds().height / 2);
		shell.setLocation(lx, ly);
	}

	public void disableMapping() {
		wMapFields.setEnabled(false);
	}

	public void enableMapping() {
		wMapFields.setEnabled(true);
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

	/**
	 * @return The data to this dialog
	 */
	@Override
	public Object getData() {
		return input;
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	private void getData(MDPersonatorMeta data) throws KettleException {
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

	public MDPersonatorHelper getHelper() {
		return helper;
	}

	private String getHelpURL() {
		CTabItem wTab = wTabFolder.getSelection();
		MDTab tab = (MDTab) wTab.getData();
		String url = tab.getHelpURLKey();
		return url;
	}

	public MDPersonatorMeta getInput() {
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
		return "MDPersonatorDialog";
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

	public MDPersonatorMeta getStepMeta() {
		return input;
	}

	public CTabFolder getTabFolder() {
		return wTabFolder;
	}

	/**
	 * @return Master title for the component
	 */
	public String getTitle() {
		if (MDPersonatorMeta.isEnterprise()) {
			return BaseMessages.getString(PKG, "MDPersonatorDialog.Shell.Title");
		} else {
			return BaseMessages.getString(PKG, "MDPersonatorDialog.Shell.Community.Title");
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
	private void init(MDPersonatorMeta data) {
		for (MDTab tab : tabs) {
			tab.init(data);
		}
		if (!Boolean.valueOf(MDProps.getProperty(TAG_CONTACTZONE, "false"))) {
			mdInterface = new AdvancedConfigInterface();
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
			new ErrorDialog(getShell(), BaseMessages.getString(PKG, "MDPersonator.BadProperties.Title"), BaseMessages.getString(PKG, "MDPersonator.BadProperties.Message"), MDProps.getLoadException());
			return false;
		}
		// Block the user from configuring this step to run in multiple copies.
		if (stepMeta.getCopies() > 1) {
			MessageDialog.openError(getShell(), getTitle(), BaseMessages.getString(PKG, "MDPersonator.MultipleCopies.Message"));
			return false;
		}
		// See if there is some configuration property that should be set before anything else
		String nagMsg = checkLicense();
		int welcomeCount = 0;
		welcomeCount = Integer.parseInt(MDProps.getProperty(TAG_FIRSTRUN, "0"));
		if ((welcomeCount < 3) && "SetLicenseWarning".equals(nagMsg)) {
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
			new ErrorDialog(shell, BaseMessages.getString(PKG, "MDPersonatorDialog.IllegalDialogSettings.Title"), BaseMessages.getString(PKG, "MDPersonatorDialog.IllegalDialogSettings.Message"), e);
		}
	}

	@Override
	public String open() {
		// Initialize important values
		Shell parent = getParent();
		Display display = parent.getDisplay();
		// This helper allows for common functionality to be shared between dialog elements
		helper = new MDPersonatorHelper(this);
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
		String version = BaseMessages.getString(PKG, "MDPersonatorDialog.VersionPrefix");
		Class<?> clazz = this.getClass();
		String className = clazz.getSimpleName() + ".class";
		String classPath = "";
		if ((clazz != null) && (className != null)) {
			classPath = clazz.getResource(className).toString();
		}
		if (!classPath.startsWith("jar")) {
			version = BaseMessages.getString(PKG, "MDPersonatorDialog.VersionNotFound");
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest = new Manifest(new URL(manifestPath).openStream());
				String implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				if (implVersion == null) {
					version = BaseMessages.getString(PKG, "MDPersonatorDialog.VersionNotFound");
				} else {
					version = BaseMessages.getString(PKG, "MDPersonatorDialog.VersionPrefix") + " " + implVersion;
				}
			} catch (Exception e) {
				version = BaseMessages.getString(PKG, "MDPersonatorDialog.VersionNotFound");
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
		inputTab = new InputPersonatorTab(this);
		tabs.add(inputTab);
		optionsTab = new PersonatorOptionsTab(this);
		tabs.add(optionsTab);
		outPersonatorTab = new OutputPersonatorTab(this);
		tabs.add(outPersonatorTab);
		tabs.add(new SourcePassThruTab(this, transMeta));
		tabs.add(new OutputFilterTab(this));
		// Place the tab folder in the dialog
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wStepname, helper.margin * 6);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(92, -50);
		wTabFolder.setLayoutData(fd);
		// THE BUTTONS
		wAdvanced = new Button(shell, SWT.PUSH);
		wAdvanced.setText(BaseMessages.getString(PKG, "MDPersonatorDialog.Button.Advanced")); //$NON-NLS-1$
		wAdvanced.setToolTipText(BaseMessages.getString(PKG, "MDPersonatorDialog.Button.Advanced.ToolTip"));
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); //$NON-NLS-1$
		plugin = PluginRegistry.getInstance().getPlugin(StepPluginType.class, stepMeta.getStepMetaInterface());
		createHelpButton(shell, stepMeta, plugin);
		wMapFields = new Button(shell, SWT.PUSH);
		wMapFields.setText("Map Fields"); //$NON-NLS-1$

		Control last = null;
		if(!MDPersonatorMeta.isEnterprise()){
			last = createCommunityMessage();
		} else {
			last = wTabFolder;
		}

		setButtonPositions(new Button[] { wOK, wCancel, wMapFields, wAdvanced }, helper.margin, last);
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
		Listener lsMapFields = new Listener() {
			@Override
			public void handleEvent(Event event) {
				openMappingDialog();
			};
		};
		if (wAdvanced != null) {
			wAdvanced.addListener(SWT.Selection, lsAdvanced);
		}
		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);
		wMapFields.addListener(SWT.Selection, lsMapFields);
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
			String message = BaseMessages.getString(PKG, "MDPersonatorDialog.Welcome.Linux");
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDPersonatorDialog.Welcome.Linux.Title"));
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
		
		if(input.isUpdateData()){
			
			String message = BaseMessages.getString(PKG, "MDPersonatorDialog.Windows.Welcome");
			String title = BaseMessages.getString(PKG, "MDPersonatorDialog.Windows.Welcome.Title");
			
			if(Const.isOSX()){
				
				String path = MDPersonatorDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				String decodedPath = "";
				try {
					decodedPath = URLDecoder.decode(path, "UTF-8");
					decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("MDPersonator"));
				} catch (UnsupportedEncodingException e1) {
					decodedPath = "your MDPersonatorPlugin Dir";
				}
				String osxMsg = "\n\nMac OS users:\nPlease see MacOS_ReadMe.txt located in\n" + decodedPath + "\nand follow instructions before restarting";
				message = message + osxMsg;
			}
			
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(title);
			box.setMessage(message);
			if (box.open() == SWT.OK) {
				//isLoad = false;
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

		if(!MDPersonatorMeta.isEnterprise()){
			if(MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT,"").contains(MDPropTags.MDLICENSE_PRODUCT_Community) ){
				MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText(BaseMessages.getString(PKG, "MDPersonatorDialog.Community.Title"));
				box.setMessage(BaseMessages.getString(PKG,"MDPersonatorDialog.Community.PopupMessage"));
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
		//fd.right = new FormAttachment( wAdvanced,  150);
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
		line1.setText(BaseMessages.getString(PKG,"MDPersonatorDialog.Community.Label"));
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//fd.bottom = new FormAttachment(100, 0);
		line1.setLayoutData(fd);


		Link line3link = new Link(centerComp, SWT.None);
		line3link.setText(BaseMessages.getString(PKG,"MDPersonatorDialog.Community.Link"));
		fd = new FormData();
		fd.top = new FormAttachment(line1, helper.margin);
		fd.left = new FormAttachment(line1, 0, SWT.CENTER);
		//fd.right = new FormAttachment(100, 0);
		//fd.bottom = new FormAttachment(100, 0);
		line3link.setLayoutData(fd);
		line3link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(BaseMessages.getString(PKG,"MDPersonatorDialog.Community.URL"));
			}

		});

		props.setLook(line1);
		props.setLook(line3link);
		props.setLook(centerComp);

		return communityComp;
	}

	private void openAdvanced() {
		String id = "Personator";
		mdInterface.setMenuId(id);
		AdvancedConfigurationDialog ad = new AdvancedConfigurationDialog(shell, this, mdInterface, id);
		ad.open();
	}

	private void openMappingDialog() {
		inputTab.setParsedFieldSelection(input);
		input.getFieldMapping().setSourceFields(sourceFields);
		input.getFieldMapping().setData(input);
		FieldMappingDialog fd = new FieldMappingDialog(this);
		fd.open();
	}

	public void refreshOutputTab() {
		outPersonatorTab.init(input);
	}

	public void refreshTabs() {
		for (MDTab tab : tabs) {
			if (tab.getClass() != SourcePassThruTab.class) {
				tab.init(input);
			} else {
				tab.getData(input);
				tab.init(input);
			}
		}
	}

	/**
	 * Indicates that something in the dialog has changed
	 */
	public void setChanged() {
		changed = true;
	}

	public void setHelper(MDPersonatorHelper helper) {
		this.helper = helper;
	}

	public void setInput(MDPersonatorMeta input) {
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
			MDProps.setProperty(TAG_PRIMARY_LICENSE, licString.trim());
			try {
				MDProps.save();
			} catch (IOException e) {
				// Ignore if happens
			}
		}
		if (continueToAdvancedConfig) {
			openAdvanced();
		}
	}

	/**
	 * Called to validate settings. If there is a problem the user will be alerted and given the option of canceling.
	 */
	private boolean valid(MDPersonatorMeta meta) {
		List<String> warnings = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();
		Validations checker = new Validations(); // used to clear lists after checks are done
		// Validate step name
		if (Const.isEmpty(wStepname.getText().trim())) {
			errors.add(getValidationMessage("MissingStepName"));
		}
		// Validate individual settings
		checker.validate(meta, warnings, errors);
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
