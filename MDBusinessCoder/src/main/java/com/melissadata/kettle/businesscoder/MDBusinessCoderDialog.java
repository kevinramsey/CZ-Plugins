package com.melissadata.kettle.businesscoder;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.MDPropTags;
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
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.cz.ui.MDDialogParent;
import com.melissadata.kettle.MDSettings.AdvancedConfigInterface;
import com.melissadata.kettle.MDSettings.AdvancedConfigurationDialog;
import com.melissadata.kettle.businesscoder.support.MDBusinessCoderHelper;
import com.melissadata.kettle.businesscoder.ui.*;
import com.melissadata.cz.support.InstallRedistrib;
import com.melissadata.cz.support.MDTab;

public class MDBusinessCoderDialog extends BaseStepDialog implements StepDialogInterface , MDDialogParent {

	private static Class<?>						PKG	= MDBusinessCoderDialog.class;
	private MDBusinessCoderMeta					bcMeta;
	private MDBusinessCoderHelper				helper;
	private ModifyListener						lsModified;
	private AdvancedConfigInterface				mdInterface;
	private PluginInterface						plugin;
	private List<MDTab>							tabs;
	private CTabFolder							wTabFolder;
	private static boolean						isOpen;
	private SortedMap<String, SourceFieldInfo>	sourceFields;
	private 		Button wAdvanced = null;

	public MDBusinessCoderDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {

		super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
		bcMeta = (MDBusinessCoderMeta) baseStepMeta;
	}

	/**
	 * @return true if canceled
	 */
	private boolean cancel() {

		if (changed) {
			MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDBusinessCoderDialog.WarningDialogChanged.Title"));
			box.setMessage(BaseMessages.getString(PKG, "MDBusinessCoderDialog.WarningDialogChanged.Message", Const.CR));
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
		shell.setSize((screenWidth - (screenWidth / 3)), (screenHeight - (screenHeight / 5)));
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

	@Override
	public Object getData() {

		return bcMeta.getStepData();
	}

	public void getData(MDBusinessCoderMeta meta) {

		for (MDTab tab : tabs) {
			tab.getData(meta);
		}

	}

	public MDBusinessCoderHelper getHelper() {

		return helper;
	}

	private String getHelpURL() {


		CTabItem wTab = wTabFolder.getSelection();
		MDTab tab = (MDTab) wTab.getData();
		return tab.getHelpURLKey();

	}

	@Override
	public LogChannelInterface getLog() {

		return log;
	}

	/**
	 * @return A naming prefix that will be used when looking up label strings
	 */
	public String getNamingPrefix() {

		return "MDBusinessCoderDialog";
	}

	/**
	 * @return The properties for this dialog
	 */
	public PropsUI getProps() {

		return props;
	}

	@Override
	public Shell getShell() {

		return shell;
	}

	@Override
	public SortedMap<String, SourceFieldInfo> getSourceFields() {
		// If we already have source fields then return them
		if (sourceFields != null)
			return sourceFields;

		log.logDebug("Getting source fields - ");
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
					log.logDebug("source field : " + name);
					SourceFieldInfo field = new SourceFieldInfo(name);
					sourceFields.put(name, field);
				}

				log.logDebug("Found " + sourceFields.size() + " source fields");
			}
		} catch (KettleException e) {
			log.logError(toString(), BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message"));
		}

		return sourceFields;
	}

	@Override
	public VariableSpace getSpace() {

		return variables;
	}

	public CTabFolder getTabFolder() {

		return wTabFolder;
	}

	/**
	 * @return Master title for the component
	 */
	public String getTitle() {
		if (MDBusinessCoderMeta.isEnterprise())
			return BaseMessages.getString(PKG, "MDBusinessCoderDialog.Shell.Title");
		else
			return BaseMessages.getString(PKG, "MDBusinessCoderDialog.Shell.Community.Title");
	}

	/**
	 * Loads the data into the dialog
	 */
	private void init(MDBusinessCoderMeta meta) {

		for (MDTab tab : tabs) {
			tab.init(meta);
		}
		if (mdInterface == null) {
			mdInterface = new AdvancedConfigInterface();
		}
		mdInterface.setDataValues();
		checkShellSize();
	}

	private boolean nag() {

		// TODO NAG
		return true;
	}

	public void ok() {

		stepname = wStepname.getText(); // return value
		// Don't do anything if nothing changed
		if (changed) {
			// Get data from dialog elements
			getData(bcMeta);
			// Tell the meta data that it has changed
			bcMeta.setChanged();
		}
		isOpen = false;
		// validate
		if (valid()) {
			// Close the dialog
			dispose();
		}
	}

	@Override
	public String open() {

		// Initialize important values
		Shell parent = getParent();
		Display display = parent.getDisplay();
		helper = new MDBusinessCoderHelper(this);
		// Create the dialog
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, bcMeta);
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
		String version = BaseMessages.getString(PKG, "MDBusinessCoderDialog.VersionPrefix");
		Class<?> clazz = this.getClass();
		String className = clazz.getSimpleName() + ".class";
		String classPath = "";
		if ((clazz != null) && (className != null)) {
			classPath = clazz.getResource(className).toString();
		}
		if (!classPath.startsWith("jar")) {
			version = BaseMessages.getString(PKG, "MDBusinessCoderDialog.VersionNotFound");
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest = new Manifest(new URL(manifestPath).openStream());
				String implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				if (implVersion == null) {
					version = BaseMessages.getString(PKG, "MDBusinessCoderDialog.VersionNotFound");
				} else {
					version = BaseMessages.getString(PKG, "MDBusinessCoderDialog.VersionPrefix") + " " + implVersion;
				}
			} catch (Exception e) {
				version = BaseMessages.getString(PKG, "MDBusinessCoderDialog.VersionNotFound");
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
		tabs.add(new InputFieldsTab(this));
		tabs.add(new OutputContactTab(this));
		tabs.add(new OutputAddressTab(this));
		tabs.add(new OutputBusinessTab(this));
		tabs.add(new OptionsTab(this));
		tabs.add(new PassThruTab(this, transMeta));
		tabs.add(new OutputFilterTab(this));

		// Place the tab folder in the dialog
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wStepname, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(92, -50);
		wTabFolder.setLayoutData(fd);

		// THE BUTTONS
		wAdvanced = new Button(shell, SWT.PUSH);
		wAdvanced.setText(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Button.Advanced")); //$NON-NLS-1$
		wAdvanced.setToolTipText(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Button.Advanced.ToolTip"));
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); //$NON-NLS-1$
		plugin = PluginRegistry.getInstance().getPlugin(StepPluginType.class, stepMeta.getStepMetaInterface());
		createHelpButton(shell, stepMeta, plugin);

		Control last = null;
		if(!MDBusinessCoderMeta.isEnterprise()){
			last = createCommunityMessage();
		} else {
			last = wTabFolder;
		}

		setButtonPositions(new Button[] { wOK, wCancel, wAdvanced }, helper.margin, last);
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
		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);
		wAdvanced.addListener(SWT.Selection, lsAdvanced);
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
		init(bcMeta);
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
			String message = BaseMessages.getString(PKG, "MDBusinessCoderDialog.Linux.Welcome");
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Welcome.Linux.Title"));
			box.setMessage(message);
			if (box.open() == SWT.NO) {
				isLoad = false;
			}
		}
		String sy32 = System.getenv("WINDIR");
		File sys32 = new File(sy32, "system32\\msvcr110.dll");
		if (Const.isWindows() && !sys32.exists()) {
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
		
		if(bcMeta.isUpdateData()){
			
			String message;
			message = BaseMessages.getString(PKG, "MDBusinessCoderDialog.Windows.Welcome");
			MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			
			if(Const.isOSX()){
				
				String path = MDBusinessCoderDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				String decodedPath = "";
				try {
					decodedPath = URLDecoder.decode(path, "UTF-8");
					decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("MDBusinessCoder"));
				} catch (UnsupportedEncodingException e1) {
					decodedPath = "your MDBusinessCoderPlugin Dir";
				}
				String osxMsg = "\n\nMac OS users:\nPlease see MacOS_ReadMe.txt located in\n" + decodedPath + "\nand follow instructions before restarting";
				message = message + osxMsg;
			}
			
			box.setText("Welcome");
			box.setMessage(message);
			if (box.open() == SWT.OK) {
				//isLoad = false;
			}
			
		}

		if(!MDBusinessCoderMeta.isEnterprise()){
			if(MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT,"").contains(MDPropTags.MDLICENSE_PRODUCT_Community) ){
				MessageBox box = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Community.Title"));
				box.setMessage(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Community.PopupMessage"));
				if (box.open() == SWT.OK) {

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
		line1.setText(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Community.Label"));
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		line1.setLayoutData(fd);

		Link line3link = new Link(centerComp, SWT.None);
		line3link.setText(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Community.Link"));
		fd = new FormData();
		fd.top = new FormAttachment(line1, helper.margin);
		fd.left = new FormAttachment(line1, 0, SWT.CENTER);
		line3link.setLayoutData(fd);
		line3link.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				Program.launch(BaseMessages.getString(PKG, "MDBusinessCoderDialog.Community.URL"));
			}
		});

		props.setLook(line1);
		props.setLook(line3link);
		props.setLook(centerComp);

		return communityComp;
	}

	private void openAdvanced() {

		String id = "";
		id = "BusinessCoder";
		if (mdInterface == null) {
			mdInterface = new AdvancedConfigInterface();
		}
		mdInterface.setDataValues();
		mdInterface.setMenuId(id);
		AdvancedConfigurationDialog ad = new AdvancedConfigurationDialog(shell, this, mdInterface, id);
		ad.open();
	}

	/**
	 * Indicates that something in the dialog has changed
	 */
	public void setChanged() {

		changed = true;
	}

	private boolean valid() {

		// TODO validate
		return true;
	}
}
