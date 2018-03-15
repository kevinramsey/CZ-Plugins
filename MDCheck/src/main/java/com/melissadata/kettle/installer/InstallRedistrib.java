package com.melissadata.kettle.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.MDCheckMeta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDDialogParent;

public class InstallRedistrib extends MDAbstractDialog {
	private MDCheckHelper helper;
	private static Class<?>	PKG		= MDCheckMeta.class;
	private String			fileSep	= Const.FILE_SEPARATOR;

	public InstallRedistrib(MDDialogParent dialog, int flags) {
		super(dialog, flags);
	}

	private void doInstall(String bitSz) {
		String resultText = "";
		String vcCmd = getSrcDir() + fileSep + "VCredist" + fileSep + "vcredist_x" + bitSz + ".exe";
		String[] prms = new String[] { vcCmd, "/passive /norestart   /c:", "msiexec /q:a /i vcredist.msi" };
		ProcessBuilder pb = new ProcessBuilder(prms);
		try {
			Process p = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				resultText = resultText.concat(line);
			}
			while ((line = error.readLine()) != null) {
				resultText = resultText.concat(line);
			}
		} catch (Exception e) {
			System.out.println("Error Installing Redistribs:" + e.getMessage());
		}
		System.out.println("Result text:" + resultText);
	}

	private String getSrcDir() {
		String decodedPath = "";
		File ssdd = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error Installing VC++ Redistributables: " + e.getMessage());
		}
		return decodedPath;
	}

	@Override
	protected void createContents(Composite parent, Object data) {
		helper = new MDCheckHelper((MDCheckDialog) dialog);
		parent.getShell().setText(getString("DataDialog.Title"));
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(parent, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("DataDialog.Label"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		description.setLayoutData(fd);
		Button btn64bit = new Button(wComp, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(15, helper.margin);
		fd.top = new FormAttachment(description, helper.margin * 2);
		btn64bit.setLayoutData(fd);
		btn64bit.setText(getString("64Button.Label"));
		btn64bit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				doInstall("64");
				dispose();
			}
		});
		Button btn32bit = new Button(wComp, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(15, helper.margin);
		fd.top = new FormAttachment(btn64bit, helper.margin * 2);
		btn32bit.setLayoutData(fd);
		btn32bit.setText(getString("32Button.Label"));
		btn32bit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				doInstall("86");
				dispose();
			}
		});
		/*
		 * final Text dirTxt = new Text(wComp, SWT.BORDER);
		 * fd = new FormData();
		 * fd.left = new FormAttachment(0, 0);
		 * fd.right = new FormAttachment(100, helper.margin);
		 * fd.top = new FormAttachment(description, helper.margin);
		 * dirTxt.setLayoutData(fd);
		 * //dirTxt.setText(dataDir.toString());
		 * Button button = new Button(wComp, SWT.PUSH);
		 * fd = new FormData();
		 * fd.left = new FormAttachment(0, 0);
		 * fd.right = new FormAttachment(25, helper.margin);
		 * fd.top = new FormAttachment(dirTxt, helper.margin);
		 * button.setLayoutData(fd);
		 * button.setText("Browse...");
		 * button.addSelectionListener(new SelectionAdapter() {
		 * public void widgetSelected(SelectionEvent event) {
		 * DirectoryDialog dlg = new DirectoryDialog(helper.getShell());
		 * dlg.setFilterPath(dirTxt.getText());
		 * dlg.setText(getString("DirectoryDialog.Title"));
		 * dlg.setMessage(getString("DirectoryDialog.Label"));
		 * String dir = dlg.open();
		 * if (dir != null) {
		 * dirTxt.setText(dir);
		 * //dataDir = new File(dirTxt.getText());
		 * }
		 * }
		 * });
		 */
		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		wComp.pack();
	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		// not implemented
		return false;
	}

	@Override
	protected Button[] getDialogButtons() {
		// Call parent to get OK and Cancel buttons
		Button[] buttons = super.getDialogButtons();
		// Button wOK = buttons[0];
		Button wCancel = buttons[1];
		return new Button[] { wCancel };
	}

	@Override
	protected String getHelpURLKey() {
		// not implemented
		return null;
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.RedisInstaller";
	}

	@Override
	protected void init(Object data) {
		// not implemented
	}
}
