package com.melissadata.kettle.cv.address;

import java.io.File;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.cz.support.MDPropTags;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.ui.util.ImageUtil;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class AddressVerifyOptionsDialog extends MDAbstractDialog {
	private static final Class<?>	PKG					= AddressVerifyOptionsDialog.class;
	private static final String		REPORT_EXTENSION	= ".html";
	private static final String[]	REPORT_EXTENSIONS	= new String[] { "*" + REPORT_EXTENSION };
	private ComboViewer				vCountries;
	private Button					wPerformDPV;
	private Button					wPerformLACSLink;
	private Button					wPerformSuiteLink;
	private Button					wPerformAddrPlus;
	private Button					wPerformRBDI;
	private Button					wUsePreferredCity;
	private ComboViewer				vDiacriticMode;
	private Button					wCASSSaveToFile;
	private Group					gCASSForm;
	private Text					wCASSProcessorName;
	private Text					wCASSListName;
	private Text					wCASSName;
	private Text					wCASSCompany;
	private Text					wCASSAddress;
	private Text					wCASSCity;
	private Text					wCASSState;
	private Text					wCASSZip;
	private Text					wCASSFilename;
	private Button					wSOASaveToFile;
	private Group					gSOAForm;
	private Text					wSOAProcessorName;
	private Text					wSOAAddress;
	private Text					wSOACity;
	private Text					wSOAProvince;
	private Text					wSOAPostalCode;
	private Text					wSOAFilename;
	private boolean					previousSuiteLink;

	public AddressVerifyOptionsDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
		// Override the countries setting if canada is not allowed
		AdvancedConfigurationMeta advConfig = ((MDCheckStepData) data).getAdvancedConfiguration();
		boolean isDll = advConfig.getServiceType() == ServiceType.Local;
		int products = advConfig.getProducts();
		if (isDll && ((products & AdvancedConfigurationMeta.MDLICENSE_Canada) == 0)) {
			AddressVerifyMeta.Countries curCountries = (AddressVerifyMeta.Countries) (((IStructuredSelection) vCountries.getSelection()).getFirstElement());
			if (curCountries != AddressVerifyMeta.Countries.US) {
				vCountries.setSelection(new StructuredSelection(AddressVerifyMeta.Countries.US));
				enable();
			}
		}
	}

	public boolean isPreviousSuiteLink() {
		return previousSuiteLink;
	}

	public void setPreviousSuiteLink(boolean previousSuiteLink) {
		this.previousSuiteLink = previousSuiteLink;
	}

	/**
	 * Called to browse for a directory
	 *
	 * @param wFile
	 */
	private void browse(Text wFile) {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
		String dataPath = (wFile.getText() != null) ? wFile.getText() : "";
		String oldPath = dialog.getSpace().environmentSubstitute(dataPath);
		fileDialog.setFilterPath(oldPath);
		fileDialog.setFileName(oldPath);
		fileDialog.setFilterExtensions(REPORT_EXTENSIONS);
		if (fileDialog.open() != null) {
			String fileName = fileDialog.getFileName();
			if (!fileName.toLowerCase().endsWith(REPORT_EXTENSION)) {
				fileName += REPORT_EXTENSION;
			}
			String newPath = new File(new File(fileDialog.getFilterPath()), fileName).getAbsolutePath();
			if (!newPath.equalsIgnoreCase(oldPath)) {
				wFile.setText(newPath);
			}
		}
	}

	/**
	 * Called to enable controls based on option settings
	 */
	private void enable() {
		// Get configuration information
		AdvancedConfigurationMeta advConfig = ((MDCheckStepData) data).getAdvancedConfiguration();
		ServiceType serviceType = advConfig.getServiceType();
		boolean isDll = serviceType == ServiceType.Local;
		boolean isWeb = (serviceType == ServiceType.Web) || (serviceType == ServiceType.CVS);
		int products = advConfig.getProducts();
		// If the local service license does not allow canada then disable the countries control.
		boolean isCanadaLicensed = (products & AdvancedConfigurationMeta.MDLICENSE_Canada) != 0;
		boolean enabled = !isDll || isCanadaLicensed;
		vCountries.getControl().setEnabled(enabled);
		// What countries are we checking?
		AddressVerifyMeta.Countries curCountries = (AddressVerifyMeta.Countries) (((IStructuredSelection) vCountries.getSelection()).getFirstElement());
		boolean isUS = (curCountries == AddressVerifyMeta.Countries.US) || (curCountries == AddressVerifyMeta.Countries.USCanada);
		boolean isCanada = (curCountries == AddressVerifyMeta.Countries.Canada) || (curCountries == AddressVerifyMeta.Countries.USCanada);
		// See if RBDI is licensed for the local object
		boolean isRBDI = (products & AdvancedConfigurationMeta.MDLICENSE_RBDI) != 0;
		// Enable controls based on the above settings
		wPerformRBDI.setEnabled(isUS && (isRBDI || isWeb) && ((MDCheckDialog) dialog).MinAddrInput());
		wUsePreferredCity.setEnabled(isDll);
		wPerformDPV.setEnabled(isDll && isUS);
		wPerformLACSLink.setEnabled(isDll && isUS);
		// Suite link enablement depends on address plus
		wPerformAddrPlus.setEnabled(isUS && isDll);
		wPerformSuiteLink.setEnabled(wPerformAddrPlus.isEnabled());
		if (!wPerformAddrPlus.isEnabled()) {
			wPerformAddrPlus.setSelection(false);
		}
		if (!wPerformSuiteLink.isEnabled()) {
			wPerformSuiteLink.setSelection(false);
		}
		// Diacritics enabled only for canada
		Control cbDiacriticMode = vDiacriticMode.getControl();
		Control lbDiacriticMode = ((FormData) cbDiacriticMode.getLayoutData()).left.control;
		lbDiacriticMode.setEnabled(isCanada && isDll);
		cbDiacriticMode.setEnabled(isCanada && isDll);
		// CASS and SOA forms
		wCASSSaveToFile.setEnabled(isDll && isUS);
		gCASSForm.setEnabled(isUS);
		for (Control child : gCASSForm.getChildren()) {
			child.setEnabled(isUS && wCASSSaveToFile.getSelection());
		}
		wSOASaveToFile.setEnabled(isDll && isCanada);
		gSOAForm.setEnabled(isCanada);
		for (Control child : gSOAForm.getChildren()) {
			child.setEnabled(isCanada && wSOASaveToFile.getSelection());
		}

		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject)){
			wPerformRBDI.setEnabled(false);
			wUsePreferredCity.setEnabled(false);
			wPerformDPV.setEnabled(false);
			wPerformLACSLink.setEnabled(false);
			// Suite link enablement depends on address plus
			wPerformAddrPlus.setEnabled(false);
			wPerformSuiteLink.setEnabled(false);

			cbDiacriticMode.setEnabled(false);
			// CASS and SOA forms
			wCASSSaveToFile.setEnabled(false);
			gCASSForm.setEnabled(false);
			for (Control child : gCASSForm.getChildren()) {
				child.setEnabled(false);
			}
			wSOASaveToFile.setEnabled(false);
			gSOAForm.setEnabled(false);
			for (Control child : gSOAForm.getChildren()) {
				child.setEnabled(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		// Group for the main options
		Group gAVOptions = addGroup(parent, null, "AVOptionsGroup");
		// Main option controls
		vCountries = addEnumComboBox(gAVOptions, null, "Countries", AddressVerifyMeta.Countries.values());
		wPerformDPV = addCheckBox(gAVOptions, vCountries.getControl(), "PerformDPV");
		wPerformLACSLink = addCheckBox(gAVOptions, wPerformDPV, "PerformLACSLink");
		wPerformSuiteLink = addCheckBox(gAVOptions, wPerformLACSLink, "PerformSuiteLink");
		wPerformAddrPlus = addCheckBox(gAVOptions, wPerformSuiteLink, "PerformAddrPlus");
		wPerformRBDI = addCheckBox(gAVOptions, vCountries.getControl(), "PerformRBDI");
		wUsePreferredCity = addCheckBox(gAVOptions, wPerformRBDI, "UsePreferredCity");
		vDiacriticMode = addEnumComboBox(gAVOptions, wUsePreferredCity, "DiacriticMode", AddressVerifyMeta.DiacriticMode.values());
		// Cleanup layout
		Control label = ((FormData) vCountries.getControl().getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = null;
		((FormData) vCountries.getControl().getLayoutData()).right = new FormAttachment(33, -margin);
		((FormData) wPerformDPV.getLayoutData()).right = new FormAttachment(50, -margin);
		((FormData) wPerformLACSLink.getLayoutData()).right = new FormAttachment(50, -margin);
		((FormData) wPerformSuiteLink.getLayoutData()).right = new FormAttachment(50, -margin);
		((FormData) wPerformAddrPlus.getLayoutData()).right = new FormAttachment(50, -margin);
		((FormData) wPerformRBDI.getLayoutData()).left = new FormAttachment(50, margin);
		((FormData) wUsePreferredCity.getLayoutData()).left = new FormAttachment(50, margin);
		label = ((FormData) vDiacriticMode.getControl().getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(50, margin);
		((FormData) label.getLayoutData()).right = null;
		// Control enablement changes depending on the configured countries
		vCountries.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				enable();
			}
		});
		// When address plus is selected then suite link is always turned on.
		/*
		 * wPerformAddrPlus.addSelectionListener(new SelectionAdapter() {
		 * @Override
		 * public void widgetSelected(SelectionEvent e) {
		 * if (wPerformAddrPlus.getSelection()) {
		 * wPerformSuiteLink.setSelection(true);
		 * }
		 * }
		 * });
		 * wPerformSuiteLink.addSelectionListener(new SelectionAdapter() {
		 * @Override
		 * public void widgetSelected(SelectionEvent e) {
		 * if (!wPerformSuiteLink.getSelection()) {
		 * wPerformAddrPlus.setSelection(false);
		 * }
		 * }
		 * });
		 */
		// A composite for the forms
		Composite wForms = new Composite(parent, SWT.NONE);
		setLook(wForms);
		wForms.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(gAVOptions, 0);
		fd.right = new FormAttachment(100, 0);
		wForms.setLayoutData(fd);
		// CASS Form group
		wCASSSaveToFile = addCheckBox(wForms, gAVOptions, "CASSFormGroup");
		((FormData) wCASSSaveToFile.getLayoutData()).left.offset += 8;
		wCASSSaveToFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		gCASSForm = addGroup(wForms, wCASSSaveToFile, null);
		((FormData) gCASSForm.getLayoutData()).right = new FormAttachment(50, -margin);
		((FormData) gCASSForm.getLayoutData()).bottom = new FormAttachment(100, -margin);
		((FormData) gCASSForm.getLayoutData()).top.offset -= 14;
		// Rest of form controls
		int middlePctSaved = middlePct; // hack: makes the form display a little more compact
		middlePct = (middlePct * 2) / 3;
		wCASSProcessorName = addTextBox(gCASSForm, null, "CASSProcessorName");
		// ((FormData)wCASSProcessorName.getLayoutData()).top.offset +=16;//******
		wCASSListName = addTextBox(gCASSForm, wCASSProcessorName, "CASSListName");
		wCASSName = addTextBox(gCASSForm, wCASSListName, "CASSName");
		wCASSCompany = addTextBox(gCASSForm, wCASSName, "CASSCompany");
		wCASSAddress = addTextBox(gCASSForm, wCASSCompany, "CASSAddress");
		wCASSCity = addTextBox(gCASSForm, wCASSAddress, "CASSCity");
		wCASSState = addTextBox(gCASSForm, wCASSCity, "CASSState");
		wCASSZip = addTextBox(gCASSForm, wCASSCity, "CASSZip");
		wCASSFilename = addTextBox(gCASSForm, wCASSState, "CASSFilename");
		middlePct = middlePctSaved;
		// State and Zip are on same line
		((FormData) wCASSState.getLayoutData()).right = new FormAttachment((int) (1.5 * middlePct), -margin);
		label = ((FormData) wCASSZip.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wCASSState, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment((int) (2.0 * middlePct), -margin);
		// Button for browsing for a CASS file
		Button btnBrowse = addPushButton(gCASSForm, wCASSState, null);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browse(wCASSFilename);
			}
		});
		btnBrowse.setImage(ImageUtil.getImage(display, PKG, "com/melissadata/kettle/images/OpenFolder.gif"));
		label = ((FormData) wCASSFilename.getLayoutData()).left.control;
		((FormData) wCASSFilename.getLayoutData()).right = new FormAttachment(btnBrowse, -margin);
		((FormData) btnBrowse.getLayoutData()).left = null;
		((FormData) btnBrowse.getLayoutData()).right = new FormAttachment(100, -margin);
		// SOA Form group
		wSOASaveToFile = addCheckBox(wForms, gAVOptions, "SOAFormGroup");
		((FormData) wSOASaveToFile.getLayoutData()).left = new FormAttachment(50, margin + 8);
		// ((FormData)wSOASaveToFile.getLayoutData()).top.offset -=8;//******
		wSOASaveToFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		gSOAForm = addGroup(wForms, wSOASaveToFile, null);
		((FormData) gSOAForm.getLayoutData()).left = new FormAttachment(50, margin);
		((FormData) gSOAForm.getLayoutData()).bottom = new FormAttachment(100, -margin);
		((FormData) gSOAForm.getLayoutData()).top.offset -= 14;
		// Rest of form controls
		middlePctSaved = middlePct; // hack: makes the form display a little more compact
		middlePct = (middlePct * 2) / 3;
		wSOAProcessorName = addTextBox(gSOAForm, null, "SOAProcessorName");
		((FormData) wSOAProcessorName.getLayoutData()).top.offset += 16;// ******
		wSOAAddress = addTextBox(gSOAForm, wSOAProcessorName, "SOAAddress");
		wSOACity = addTextBox(gSOAForm, wSOAAddress, "SOACity");
		wSOAProvince = addTextBox(gSOAForm, wSOACity, "SOAProvince");
		wSOAPostalCode = addTextBox(gSOAForm, wSOACity, "SOAPostalCode");
		wSOAFilename = addTextBox(gSOAForm, wSOAProvince, "SOAFilename");
		middlePct = middlePctSaved;
		// Province and Postal Code are on same line
		((FormData) wSOAProvince.getLayoutData()).right = new FormAttachment((int) (1.5 * middlePct), -margin);
		label = ((FormData) wSOAPostalCode.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wSOAProvince, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment((int) (2.0 * middlePct), -margin);
		// Button for browsing for an SOA file
		btnBrowse = addPushButton(gSOAForm, wSOAProvince, null);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browse(wSOAFilename);
			}
		});
		btnBrowse.setImage(ImageUtil.getImage(display, PKG, "com/melissadata/kettle/images/OpenFolder.gif"));
		label = ((FormData) wSOAFilename.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).top = null;
		((FormData) label.getLayoutData()).bottom = new FormAttachment(100, -margin - 4);
		((FormData) wSOAFilename.getLayoutData()).top = null;
		((FormData) wSOAFilename.getLayoutData()).right = new FormAttachment(btnBrowse, -margin);
		((FormData) wSOAFilename.getLayoutData()).bottom = new FormAttachment(100, -margin);
		((FormData) btnBrowse.getLayoutData()).top = null;
		((FormData) btnBrowse.getLayoutData()).left = null;
		((FormData) btnBrowse.getLayoutData()).right = new FormAttachment(100, -margin);
		((FormData) btnBrowse.getLayoutData()).bottom = new FormAttachment(100, -margin + 4);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) throws KettleException {
		// Local version of input that is dialog specific
		AddressVerifyMeta avMeta = ((MDCheckStepData) data).getAddressVerify();
		avMeta.setOptionCountries((AddressVerifyMeta.Countries) (((IStructuredSelection) vCountries.getSelection()).getFirstElement()));
		avMeta.setOptionPerformDPV(wPerformDPV.getSelection());
		avMeta.setOptionPerformLACSLink(wPerformLACSLink.getSelection());
		avMeta.setOptionPerformSuiteLink(wPerformSuiteLink.getSelection());
		avMeta.setOptionPerformAddrPlus(wPerformAddrPlus.getSelection());
		avMeta.setOptionPerformRBDI(wPerformRBDI.getSelection());
		avMeta.setOptionUsePreferredCity(wUsePreferredCity.getSelection());
		avMeta.setOptionDiacriticMode((AddressVerifyMeta.DiacriticMode) (((IStructuredSelection) vDiacriticMode.getSelection()).getFirstElement()));
		avMeta.setCASSSaveToFile(wCASSSaveToFile.getSelection());
		avMeta.setCASSProcessorName(wCASSProcessorName.getText());
		avMeta.setCASSListName(wCASSListName.getText());
		avMeta.setCASSName(wCASSName.getText());
		avMeta.setCASSCompany(wCASSCompany.getText());
		avMeta.setCASSAddress(wCASSAddress.getText());
		avMeta.setCASSCity(wCASSCity.getText());
		avMeta.setCASSState(wCASSState.getText());
		avMeta.setCASSZip(wCASSZip.getText());
		avMeta.setCASSFilename(wCASSFilename.getText());
		avMeta.setSOASaveToFile(wSOASaveToFile.getSelection());
		avMeta.setSOAProcessorName(wSOAProcessorName.getText());
		avMeta.setSOAAddress(wSOAAddress.getText());
		avMeta.setSOACity(wSOACity.getText());
		avMeta.setSOAProvince(wSOAProvince.getText());
		avMeta.setSOAPostalCode(wSOAPostalCode.getText());
		avMeta.setSOAFilename(wSOAFilename.getText());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
			return "MDCheck.Help.AddressVerifyOptionsDialog";
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
		return "MDCheckDialog.AddressVerifyOptionsDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// Local version of input that is dialog specific
		AddressVerifyMeta avMeta = ((MDCheckStepData) data).getAddressVerify();
		// Initialize controls
		vCountries.setSelection(new StructuredSelection(avMeta.getOptionCountries()));
		wPerformDPV.setSelection(avMeta.getOptionPerformDPV());
		wPerformLACSLink.setSelection(avMeta.getOptionPerformLACSLink());
		wPerformSuiteLink.setSelection(avMeta.getOptionPerformSuiteLink());
		wPerformAddrPlus.setSelection(avMeta.getOptionPerformAddrPlus());
		wPerformRBDI.setSelection(avMeta.getOptionPerformRBDI());
		wUsePreferredCity.setSelection(avMeta.getOptionUsePreferredCity());
		vDiacriticMode.setSelection(new StructuredSelection(avMeta.getOptionDiacriticMode()));
		wCASSSaveToFile.setSelection(avMeta.getCASSSaveToFile());
		wCASSProcessorName.setText(avMeta.getCASSProcessorName());
		wCASSListName.setText(avMeta.getCASSListName());
		wCASSName.setText(avMeta.getCASSName());
		wCASSCompany.setText(avMeta.getCASSCompany());
		wCASSAddress.setText(avMeta.getCASSAddress());
		wCASSCity.setText(avMeta.getCASSCity());
		wCASSState.setText(avMeta.getCASSState());
		wCASSZip.setText(avMeta.getCASSZip());
		wCASSFilename.setText(avMeta.getCASSFilename());
		wSOASaveToFile.setSelection(avMeta.getSOASaveToFile());
		wSOAProcessorName.setText(avMeta.getSOAProcessorName());
		wSOAAddress.setText(avMeta.getSOAAddress());
		wSOACity.setText(avMeta.getSOACity());
		wSOAProvince.setText(avMeta.getSOAProvince());
		wSOAPostalCode.setText(avMeta.getSOAPostalCode());
		wSOAFilename.setText(avMeta.getSOAFilename());
		// Remember values for previous value logic
		setPreviousSuiteLink(avMeta.getOptionPerformSuiteLink());
		// Handle initial enablement
		enable();
		getShell().pack();
	}
}
