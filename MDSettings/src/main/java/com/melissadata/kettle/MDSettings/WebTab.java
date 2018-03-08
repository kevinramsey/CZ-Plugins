package com.melissadata.kettle.MDSettings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
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
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;

public class WebTab {

	private static Class<?> PKG = AdvancedConfigurationDialog.class;
	private AdvancedConfigurationDialog dialog;
	private SettingsHelper              helper;
	// Web settings
	public  Text                        wWebMaxThreads;
	public  Text                        wWebMaxRequests;
	private TextVar                     wWebTimeout;
	private TextVar                     wWebRetries;
	private TextVar                     wWebProxyHost;
	private TextVar                     wWebProxyPort;
	private Text                        wProxyPass;
	private Text                        wProxyUser;
	private CCombo                      ccEncoding;
	private Button                      wWebAbortOnError;
	private Label                       description;
	private String[] arEncodings = { "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16" };

	public WebTab(AdvancedConfigurationDialog dialog) {

		this.dialog = dialog;
		this.helper = dialog.getHelper();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("WebTab.Title"));
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
		description.setText(getString("WebTab.Description") + dialog.getDialogTitle());
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		description.setLayoutData(fd);

		// Add Groups
		createWebGroup(wComp, description);
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

	private Group createWebGroup(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "WebGroup");
		// Add warning about changing settings
		Label warningLabel = helper.addLabel(wGroup, null, "SettingWarning");
		warningLabel.setForeground(new Color(wGroup.getDisplay(), new RGB(255, 0, 0)));
		((FormData) warningLabel.getLayoutData()).right = new FormAttachment(100, -helper.margin);
		// Maximum asynchronous threads
		wWebMaxThreads = helper.addTextBox(wGroup, warningLabel, "MaxThreads");
		// Maximum requests per batch
		wWebMaxRequests = helper.addTextBox(wGroup, wWebMaxThreads, "MaxRequests");
		// Timeouts and Retries
		wWebTimeout = helper.addTextVarBox(wGroup, wWebMaxRequests, "Timeout");
		wWebRetries = helper.addTextVarBox(wGroup, wWebTimeout, "Retries");
		// HTTP proxy
		wWebProxyHost = helper.addTextVarBox(wGroup, wWebRetries, "ProxyHost");
		wWebProxyPort = helper.addTextVarBox(wGroup, wWebProxyHost, "ProxyPort");
		wProxyUser = helper.addTextBox(wGroup, wWebProxyPort, "ProxyUser");
		wProxyPass = helper.addTextBox(wGroup, wProxyUser, "ProxyPass");
		// Abort option
		wWebAbortOnError = helper.addCheckBox(wGroup, wProxyPass, "Abort");

		Label lEncoding = new Label(wGroup, SWT.RIGHT);
		lEncoding.setText("Encoding");
		ccEncoding = new CCombo(wGroup, SWT.BORDER);
		ccEncoding.setVisibleItemCount(10);
		for (int i = 0; i < arEncodings.length; i++) {
			ccEncoding.add(arEncodings[i]);
		}
//
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wWebAbortOnError, helper.margin);
		//fd.right = new FormAttachment(20, helper.margin);
		//fd.bottom = new FormAttachment(20, helper.margin);
		lEncoding.setLayoutData(fd);
//
		fd = new FormData();
		fd.left = new FormAttachment(lEncoding, helper.margin);
		fd.top = new FormAttachment(wWebAbortOnError, helper.margin);
		fd.right = new FormAttachment(30, helper.margin);
		//fd.bottom = new FormAttachment(50, helper.margin);
		ccEncoding.setLayoutData(fd);
		ccEncoding.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {

				dialog.setChanged();
			}
		});

		return wGroup;
	}

	public void init(AdvancedConfigInterface acInterface) {

		MDSettingsData data = acInterface.getSettingsData();
		String         id   = acInterface.getMenuID();

		wWebProxyHost.setText(data.realProxyHost);
		if (data.realProxyPort > 0) {
			wWebProxyPort.setText(String.valueOf(data.realProxyPort));
		} else {
			wWebProxyPort.setText("");
		}

		wWebTimeout.setText(String.valueOf(data.getTimeout(id)));
		wWebMaxThreads.setText(String.valueOf(data.getThreads(id)));
		wWebMaxRequests.setText(String.valueOf(data.getRequests(id)));
		wWebRetries.setText(String.valueOf(data.getRetries(id)));

		wProxyUser.setText(data.realProxyUser);
		wProxyPass.setText(data.realProxyPass);
		wWebAbortOnError.setSelection(data.webAbortOnError);
		ccEncoding.setText(data.webEncoding);
	}

	public void getData(AdvancedConfigInterface acInterface) {

		MDSettingsData data = acInterface.getSettingsData();
		String         id   = acInterface.getMenuID();

		data.setTimeout(id, Integer.parseInt(wWebTimeout.getText()));
		data.setRetries(id, Integer.parseInt(wWebRetries.getText()));
		data.setThreads(id, Integer.parseInt(wWebMaxThreads.getText()));
		data.setRequests(id, Integer.parseInt(wWebMaxRequests.getText()));

		data.realProxyHost = wWebProxyHost.getText();
		String prxPort = !Const.isEmpty(wWebProxyPort.getText()) ? wWebProxyPort.getText() : "0";
		data.realProxyPort = Integer.parseInt(prxPort);
		data.realProxyUser = wProxyUser.getText();
		data.realProxyPass = wProxyPass.getText();
		data.webAbortOnError = wWebAbortOnError.getSelection();
		data.setWebEncoding(ccEncoding.getText());
	}

	private String getString(String name) {

		return BaseMessages.getString(PKG, "MDSettings." + name);
	}
}
