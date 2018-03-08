package com.melissadata.cz.ui;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.melissadata.cz.ui.MDAbstractDialog;

public class BrowserDialog extends MDAbstractDialog {
	private static Class<?> PKG = BrowserDialog.class;

	private Browser browser;

	public BrowserDialog(Shell parent, MDDialogParent dialog) {
		super(parent, dialog, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		// Create the composite to hold the buttons and text field
		Composite controls = new Composite(wComp, SWT.NONE);
		setLook(controls);
		controls.setLayout(new FormLayout());

		FormData fd = new FormData();
		fd.top = new FormAttachment();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		controls.setLayoutData(fd);
		
		controls.getShell().setBounds(300, 300, 640, 480);
		
		// Create the web browser
		browser = new Browser(controls, SWT.NONE);
		fd = new FormData();
		fd.top = new FormAttachment(parent);
		fd.bottom = new FormAttachment(100, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		browser.setLayoutData(fd);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getDialogButtons()
	 */
	@Override
	protected Button[] getDialogButtons() {
		Button[] buttons = super.getDialogButtons();
		
		// Return just the OK button
		return new Button[] { buttons[0] };
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		browser.setEnabled(true);
		this.getShell().pack();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) {
		return true;
	}

	/**
	 * @param url URL to of page to display
	 */
	public void setURL(String url) {
		this.browser.setUrl(url);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getPackage()
	 */
	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getStringPrefix()
	 */
	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.BrowserDialog";
	}

	/**
	 * @param url
	 */
	public static void displayURL(Shell parent, MDDialogParent dialog, String url) {
		try {
			//attempt to use Desktop library from JDK 1.6+
			Class<?> desktopClass = Class.forName("java.awt.Desktop");
			
			// this code mimicks:  java.awt.Desktop.getDesktop().browse()
			Method browseMethod = desktopClass.getDeclaredMethod("browse", new Class[] { java.net.URI.class });
			Method getDesktopMethod = desktopClass.getDeclaredMethod("getDesktop");
			browseMethod.invoke(getDesktopMethod.invoke(null), new Object[] { java.net.URI.create(url) });

		} catch (Exception e) {
			// Library not found or failed, fall back to a browser dialog
			e.printStackTrace();
			
			BrowserDialog dialogBrowse = new BrowserDialog(parent, dialog);
			dialogBrowse.setURL(url);
			dialogBrowse.getShell().setSize(500, 500);
			dialogBrowse.open();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
		return "nothing to return because there is no help key";
	}
}
