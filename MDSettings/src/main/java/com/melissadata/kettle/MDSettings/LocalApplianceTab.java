package com.melissadata.kettle.MDSettings;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.util.ImageUtil;

public class LocalApplianceTab {

	private static Class<?> PKG = AdvancedConfigurationDialog.class;
	private AdvancedConfigurationDialog dialog;
	private SettingsHelper              helper;
	// Local Appliance Settings
	private Text                        wCVSServerURL;
	public  Text                        wCVSMaxThreads;
	public  Text                        wCVSMaxRequests;
	private TextVar                     wCVSTimeout;
	private TextVar                     wCVSRetries;
	private Button                      wCVSAbortOnError;
	private Button                      wCVSFailover;
	private Label                       description;

	public LocalApplianceTab(AdvancedConfigurationDialog dialog) {

		this.dialog = dialog;
		this.helper = dialog.getHelper();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("LocalApplianceTab.Title"));
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
		description.setText(getString("LocalApplianceTab.Description") + dialog.getDialogTitle());
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		description.setLayoutData(fd);
		// Add Groups
		createLocalAplianceGroup(wComp, description);
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

	public void init(AdvancedConfigInterface acInterface) {

		MDSettingsData data = acInterface.getSettingsData();
		String         id   = acInterface.getMenuID();
		// CV local appliance
		wCVSMaxThreads.setText(String.valueOf(data.getThreads(id)));
		wCVSMaxRequests.setText(String.valueOf(data.getRequests(id)));

		wCVSTimeout.setText(String.valueOf(data.realCVSTimeout));
		wCVSRetries.setText(String.valueOf(data.realCVSRetries));
		wCVSServerURL.setText(data.serverURL);
		wCVSAbortOnError.setSelection(data.cvsAbortOnError);
	}

	private Group createLocalAplianceGroup(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "LocalApGroup");
		// Server URL
		wCVSServerURL = helper.addTextBox(wGroup, null, "ServerURL");
		ControlDecoration controlDecoration = new ControlDecoration(wCVSServerURL, SWT.TOP | SWT.RIGHT);
		controlDecoration.setImage(ImageUtil.getImage(dialog.getDisplay(), this.getClass(), "com/melissadata/kettle/MDSettings/images/question-mark-small.png"));
		controlDecoration.setDescriptionText(getString("EditPersistentPropertyHint"));
		((FormData) wCVSServerURL.getLayoutData()).right.offset -= 20;
		// Maximum asynchronous threads
		wCVSMaxThreads = helper.addTextBox(wGroup, wCVSServerURL, "MaxThreads");
		// Maximum requests per batch
		wCVSMaxRequests = helper.addTextBox(wGroup, wCVSMaxThreads, "MaxRequests");
		// Timeouts & Retries
		wCVSTimeout = helper.addTextVarBox(wGroup, wCVSMaxRequests, "Timeout");
		wCVSRetries = helper.addTextVarBox(wGroup, wCVSTimeout, "Retries");
		// Abort option
		wCVSAbortOnError = helper.addCheckBox(wGroup, wCVSRetries, "Abort");
		// Failover option
		wCVSFailover = helper.addCheckBox(wGroup, wCVSAbortOnError, "Failover");
		helper.addLabel(wGroup, wCVSFailover, "FailoverDescription");
		// Special tracking for global properties
		wCVSServerURL.addModifyListener(helper.getChangedListener());
		return wGroup;
	}

	public void getData(AdvancedConfigInterface acInterface) {

		MDSettingsData data     = acInterface.getSettingsData();
		String         id       = acInterface.getMenuID();
		int            threads  = Integer.parseInt(wCVSMaxThreads.getText());
		int            requests = Integer.parseInt(wCVSMaxRequests.getText());

		data.setThreads(id, threads);
		data.setRequests(id, requests);

		data.serverURL = wCVSServerURL.getText();
		data.realCVSTimeout = Integer.parseInt(wCVSTimeout.getText());
		data.realCVSRetries = Integer.parseInt(wCVSRetries.getText());
		data.cvsAbortOnError = wCVSAbortOnError.getSelection();
		data.cvsFailover = wCVSFailover.getSelection();
	}

	private String getString(String name) {

		return BaseMessages.getString(PKG, "MDSettings." + name);
	}
}
