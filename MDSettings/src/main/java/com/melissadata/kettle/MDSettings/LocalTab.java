package com.melissadata.kettle.MDSettings;

import com.melissadata.cz.support.MDPropTags;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class LocalTab {

	/**
	 * Class that wraps the execution of the matchcode editror
	 */
	public class RunMatchcodeEditor implements IRunnableWithProgress {

		private File   mcEdit;
		private String localDataPath;
		private String matchcode;

		public RunMatchcodeEditor(File mcEdit, String localDataPath, String matchcode) {

			this.mcEdit = mcEdit;
			this.localDataPath = localDataPath;
			this.matchcode = matchcode;
		}

		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			// Start monitoring
			monitor.beginTask(BaseMessages.getString(PKG, "MDSettings.MatchUp.RunMatchcodeEditor.Message"), IProgressMonitor.UNKNOWN);
			// Execute the matchcode editor
			Process process = null;
			try {
				Runtime runtime    = Runtime.getRuntime();
				String  cmdArray[] = new String[3];
				cmdArray[0] = mcEdit.getAbsolutePath();
				cmdArray[1] = localDataPath;
				cmdArray[2] = matchcode;
				process = runtime.exec(cmdArray);
				process.waitFor();
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			} finally {
				if (process != null) {
					process.destroy();
				}
				monitor.done();
			}
		}
	}

	private static       Class<?> PKG                  = AdvancedConfigurationDialog.class;
	private static final String   DATA_PATH_NEEDED     = BaseMessages.getString(PKG, "MDSettings.DataPathNeeded");
	private static final String   DATA_PATH_NAME       = "DataPath";
	private static final String   WORK_PATH_NAME       = "WorkPath";
	private static final String   MATCHCODE_EDITOR_EXE = "MatchUpEditor.exe";
	private AdvancedConfigurationDialog dialog;
	private SettingsHelper              helper;
	// on premise settings
	private Text                        wLocalDataPath;
	private Text                        wMUDataPath;
	private Text                        wMUWorkPath;
	private Text                        wPresortDataPath;
	private Text                        wCleanserOperationPath;
	private Label                       description;
	private Button                      btnMatchcodeEditor;
	private boolean                     isMUglobal;
	private MDSettingsData              data;

	public LocalTab(AdvancedConfigurationDialog dialog) {

		this.dialog = dialog;
		this.helper = dialog.getHelper();
		this.data = dialog.getAcInterface().getSettingsData();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("OnPremiseTab.Title"));
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Description line
		description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("OnPremiseTab.Description") + dialog.getDialogTitle());
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		description.setLayoutData(fd);
		// Add Groups
		createOnPremiseGroup(wComp, description);
		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		// Pack the composite and get its size
		wComp.pack();
		Rectangle bounds = wComp.getBounds();
		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		wSComp.setMinWidth(bounds.width);
		wSComp.setMinHeight(bounds.height);
		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);
	}

	private Group createOnPremiseGroup(Composite parent, Control last) {

		AdvancedConfigInterface acInterface = dialog.getAcInterface();
		Group                   wGroup      = dialog.addGroup(parent, last, "LocalGroup");
		ModifyListener dataPanthChange = new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {

				getData(dialog.getAcInterface());
				dialog.setChanged();
			}
		};
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_GLOBALVERIFY
				.equals(acInterface.getMenuID())) {
			wLocalDataPath = helper.createPathControl(wGroup, null, "DataPath", false);
			wLocalDataPath.addModifyListener(dataPanthChange);
		}
		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			wMUDataPath = helper.createPathControl(wGroup, null, "MatchUp.DataPath", false);
			wMUWorkPath = helper.createPathControl(wGroup, wMUDataPath, "MatchUp.WorkPath", false);
			wMUDataPath.setEditable(false);
			wMUDataPath.addModifyListener(dataPanthChange);
			wMUWorkPath.addModifyListener(dataPanthChange);

			btnMatchcodeEditor = helper.addPushButton(wGroup, wMUWorkPath, "MatchUp.MatchcodeEditor", new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					matchcodeEditor();
				}
			});

			if (!Const.isWindows()) {
				btnMatchcodeEditor.setEnabled(false);
			}
		}
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			wPresortDataPath = helper.createPathControl(wGroup, null, "PreSort.DataPath", false);
			wPresortDataPath.addModifyListener(dataPanthChange);
		}

		if (MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
			wLocalDataPath = helper.createPathControl(wGroup, null, "DataPath", false);
			wLocalDataPath.addModifyListener(dataPanthChange);

			wCleanserOperationPath = helper.createPathControl(wGroup, wLocalDataPath, "CleanserOperationsPath", false);
			wCleanserOperationPath.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					dialog.setChanged();
				}
			});
		}

		return wGroup;
	}

	/**
	 * Open the match code editor
	 */
	private void matchcodeEditor() {
		// Find the matchcode executable
		String title         = "Match Up";
		String localDataPath = getPathContents(wMUDataPath, DATA_PATH_NAME);
		boolean enterprise = isEnterprise(MDPropTags.MDLICENSE_MatchUp);

		if (dialog.getAcInterface().getMenuID().equals(MDPropTags.MENU_ID_MATCHUP_GLOBAL)) {
			localDataPath = localDataPath + ".global";
			title = "Match Up Global";
		}

		File mcEdit = new File(localDataPath, MATCHCODE_EDITOR_EXE);
		if (!mcEdit.exists() || !mcEdit.isFile() || !mcEdit.canExecute()) {
			MessageDialog.openError(dialog.getShell(), title, BaseMessages.getString(PKG, "MDSettings.MatchUp.MatchcodeEditorNotFound.Message", mcEdit.getAbsolutePath()));
			return;
		}

		if (!enterprise) {
			MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION);
			box.setText(getString("MatchUp.MatchcodeEditor.Label"));
			box.setMessage(getString("MatchUp.MatchUpLiteInfo"));
			box.open();
		}

		try {
			new ProgressMonitorDialog(dialog.getShell()).run(true, false, new RunMatchcodeEditor(mcEdit, localDataPath, getMatchcodeName()));
		} catch (InvocationTargetException e) {
			// Something went wrong. Throw it back up.
			Throwable cause = e.getTargetException();
			MessageDialog.openError(dialog.getShell(), "Match Up", BaseMessages.getString(PKG, "MDSettings.MatchUp.MatchcodeEditorFailed.Message", cause.toString()));
			return;
		} catch (InterruptedException e) {
			MessageDialog.openError(dialog.getShell(), "Match Up", BaseMessages.getString(PKG, "MDSettings.MatchUp.MatchcodeEditorFailed.Message", e.toString()));
			return;
		}

		// revert to original .mc file if not Enterprise
		AdvancedConfigInterface.revertMCfile(localDataPath, enterprise);
	}

	public boolean isEnterprise(int nProduct) {

		int retVal = data.primeLicense.retVal;
		if ((retVal & nProduct) != 0) {
			return true;
		}
		return false;
	}

	public boolean isCommunity(int nProduct) {

		int retVal = data.primeLicense.retVal;
		if ((MDPropTags.MDLICENSE_Community & retVal) != 0) {
			return true;
		}

		return false;
	}

	private String getMatchcodeName() {

		return "";
	}

	public void init(AdvancedConfigInterface acInterface) {

		isMUglobal = MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID());
		MDSettingsData data = acInterface.getSettingsData();
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_GLOBALVERIFY
				.equals(acInterface.getMenuID())) {
			setLocalDataPathContents(data.realDataPath, wLocalDataPath);
		}
		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID())) {
			setPathContents(wMUDataPath, data.matchUpDataPath, DATA_PATH_NAME);
			setPathContents(wMUWorkPath, data.matchUpWorkPath, WORK_PATH_NAME);
		}
		if (MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			setPathContents(wMUDataPath, data.matchUpDataPath, DATA_PATH_NAME);
			setPathContents(wMUWorkPath, data.matchUpWorkPath, WORK_PATH_NAME);
		}
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			setLocalDataPathContents(data.preSortDataPath, wPresortDataPath);
		}

		if (MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
			setLocalDataPathContents(data.cleanserOpertionsPath, wCleanserOperationPath);
			setLocalDataPathContents(data.realDataPath, wLocalDataPath);
		}
	}

	public void getData(AdvancedConfigInterface acInterface) {

		MDSettingsData data = acInterface.getSettingsData();
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_GLOBALVERIFY
				.equals(acInterface.getMenuID())) {
			data.realDataPath = getLocalDataPathContents(wLocalDataPath);
		}

		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			data.matchUpDataPath = getPathContents(wMUDataPath, DATA_PATH_NAME);
			data.matchUpWorkPath = getPathContents(wMUWorkPath, WORK_PATH_NAME);
		}

		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			data.preSortDataPath = getLocalDataPathContents(wPresortDataPath);
		}

		if (MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
			data.realDataPath = getLocalDataPathContents(wLocalDataPath);
			data.cleanserOpertionsPath = getLocalDataPathContents(wCleanserOperationPath);
		}
	}

	/**
	 * Called to return the contents of a path control. Does special translation for needed value.
	 *
	 * @return
	 */
	//FIXME matchUp Global
	private String getPathContents(Text wDataPath, String name) {

		String dataPath = "";
		dataPath = wDataPath.getText();
		String pathNeededMsg = BaseMessages.getString(PKG, "MDSettings." + name + "Needed");
		if (!Const.isEmpty(dataPath) && dataPath.trim().equalsIgnoreCase(pathNeededMsg)) {
			dataPath = "";
		}
		if (dataPath.endsWith(".global")) {
			dataPath = dataPath.substring(0, dataPath.length() - 7);
		}
		return dataPath;
	}

	/**
	 * Called to set the contents of a path control. Does special translation for needed value.
	 *
	 * @param path
	 */
	private void setPathContents(Text wPath, String path, String name) {

		if (Const.isEmpty(path)) {
			path = BaseMessages.getString(PKG, "MDSettings." + name + "Needed");
		}

		if (isMUglobal && name == DATA_PATH_NAME) {
			path = path + ".global";
		}

		wPath.setText(path);
	}

	/**
	 * Called to return the contents of the local data path control. Does special translation for needed value.
	 *
	 * @return
	 */
	private String getLocalDataPathContents(Text box) {

		String dataPath = "";
		dataPath = box.getText();
		if (!Const.isEmpty(dataPath) && dataPath.trim().equalsIgnoreCase(DATA_PATH_NEEDED)) {
			dataPath = "";
		}
		return dataPath;
	}

	/**
	 * Called to set the contents of the local data path control. Does special translation for needed value.
	 *
	 * @param dataPath
	 */
	private void setLocalDataPathContents(String dataPath, Text box) {

		if (Const.isEmpty(dataPath)) {
			dataPath = DATA_PATH_NEEDED;
		}
		box.setText(dataPath);
	}

	private String getString(String name) {

		return BaseMessages.getString(PKG, "MDSettings." + name);
	}
}
