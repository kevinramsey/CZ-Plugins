package com.melissadata.kettle.personator.ui;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.ui.util.ImageUtil;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.ui.BrowserDialog;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDDialogParent;
import com.melissadata.kettle.personator.MDPersonatorDialog;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.support.MDPersonatorHelper;

public class WelcomeDialog  extends MDAbstractDialog {
	
	private static Class<?> PKG = MDPersonatorMeta.class;

	private MDPersonatorHelper helper;
	
	private Text wLicense;

	public WelcomeDialog(MDDialogParent dialog, int flags) {
		super(dialog, flags);

	}

	@Override
	protected void createContents(Composite parent, Object arg1) {
		this.helper = new MDPersonatorHelper((MDPersonatorDialog) dialog);
		parent.getShell().setText(getString("Title"));

		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(parent, SWT.NONE);
		helper.setLook(wComp);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		
		Label logo =  new Label(wComp, SWT.None);
		logo.setImage(ImageUtil.getImage(display, this.getClass(),"com/melissadata/kettle/personator/images/MelissaData.gif"));
		helper.setLook(logo);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(0, margin);
		logo.setLayoutData(fd);

		Label header = new Label(wComp, SWT.LEFT | SWT.WRAP);
		header.setText(getString("Header"));
		helper.setLook(header);
		FontData[] fD = header.getFont().getFontData();
		fD[0].setHeight(18);
		//fD[0].setStyle(SWT.BOLD);
		header.setFont( new Font(display,fD[0]));
		header.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
		fd = new FormData();
		fd.left = new FormAttachment(logo, 10);
		fd.top = new FormAttachment(logo, margin);
		header.setLayoutData(fd);

		Label welcome = new Label(wComp, SWT.LEFT | SWT.WRAP);
		welcome.setText(getString("WelcomeMsg"));
		helper.setLook(welcome);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(header, 3*margin);
		welcome.setLayoutData(fd);
		
		Link supportLink = new Link(wComp, SWT.LEFT);
		supportLink.setText(getString("LinkMsg"));
		props.setLook(supportLink);
		fd = new FormData();
		fd.left = new FormAttachment(0,0);
		fd.top = new FormAttachment(welcome,  5 *  margin);
		supportLink.setLayoutData(fd);
		
		supportLink.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String url = getString("LinkURL");
	    		BrowserDialog.displayURL(getShell(),dialog, url);
			}
		});

		Label haveLic = new Label(wComp, SWT.LEFT | SWT.WRAP);
		haveLic.setText(getString("HaveLicense.Label"));
		helper.setLook(haveLic);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(supportLink, 5*margin);
		haveLic.setLayoutData(fd);
		
		wLicense = new Text(wComp, SWT.BORDER);

		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, - 5*margin);
		fd.top = new FormAttachment(haveLic, margin);
		wLicense.setLayoutData(fd);
		
		Label needLic = new Label(wComp, SWT.LEFT | SWT.WRAP);
		needLic.setText(getString("NeedLicense.Label"));
		helper.setLook(needLic);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(wLicense, 4*margin);
		needLic.setLayoutData(fd);
		
		Label difficulties = new Label(wComp, SWT.LEFT | SWT.WRAP);
		difficulties.setText(getString("Difficulties.Label"));
		helper.setLook(difficulties);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(needLic, 2*margin);
		difficulties.setLayoutData(fd);
		
		


		Button licBtn = new Button(wComp, SWT.PUSH);
		licBtn.setText(getString("NeedLicenseBtn.Text"));
		helper.setLook(licBtn);
		
		fd = new FormData();
		fd.left = new FormAttachment(10, 0);
		fd.right = new FormAttachment(50, 0);
		fd.top = new FormAttachment(difficulties, helper.margin);
		licBtn.setLayoutData(fd);

		
		licBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String url = "http://www.melissadata.com/free-trials/pentaho.htm";
	    		BrowserDialog.displayURL(getShell(),dialog, url);
			}
		});
		
		Label licInfo = new Label(wComp, SWT.LEFT | SWT.WRAP);
		licInfo.setText(getString("LicenseInfo.Label"));
		helper.setLook(licInfo);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(licBtn, 2*margin);
		licInfo.setLayoutData(fd);
		
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description.Label"));
		helper.setLook(description);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(licInfo, 6*margin);
		description.setLayoutData(fd);
		

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
	protected Button[] getDialogButtons() {
		// Call parent to get OK and Cancel buttons
		Button[] buttons = super.getDialogButtons();
		Button wOK = buttons[0];
		Button wCancel = buttons[1];
		
		wCancel.setText(getString("EnterLicenseLaterBtn.Text"));
		wOK.setText(getString("GetStartedBtn.Text"));
		
		return new Button[] { wCancel, wOK };
	}

	@Override
	public void ok() {
		MDPersonatorDialog.licString = wLicense.getText();
		MDPersonatorDialog.continueToAdvancedConfig = true;
		dispose();

	}
	@Override
	public boolean cancel(){
		MDPersonatorDialog.continueToAdvancedConfig = false;
		dispose();
		return true;
	}

	@Override
	protected boolean getData(Object arg0) throws KettleException {
		return false;
	}

	@Override
	protected String getHelpURLKey() {
		return null;
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "WelcomeDialog";
	}

	@Override
	protected void init(Object arg0) {
		// nothing to init

	}

}
