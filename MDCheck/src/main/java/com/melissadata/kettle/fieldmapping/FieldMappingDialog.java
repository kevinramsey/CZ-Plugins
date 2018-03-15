package com.melissadata.kettle.fieldmapping;

import java.util.SortedMap;
import java.util.TreeMap;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import com.melissadata.cz.ui.MDAbstractDialog;

public class FieldMappingDialog extends MDAbstractDialog {

	private static final Class<?>                  PKG             = FieldMappingDialog.class;
	private              MDCheckDialog             dialog          = null;
	private              FieldMapping              fieldMapping    = null;
	private              Group                     gName           = null;
	private              Group                     gAddress        = null;
	private              Group                     gPhone          = null;
	private              Group                     gEmail          = null;
	private              Group                     gSmartMoverName = null;
	private              Group                     gSmartMoverAddr = null;
	private              Group                     gIpLocator      = null;
	private              boolean                   addrLicensed    = true;
	private              SortedMap<String, CCombo> lCombos         = null;

	public FieldMappingDialog(MDCheckDialog dialog) {

		super(dialog, SWT.NONE);
		this.dialog = dialog;
	}

	private Group addAddressGroup(Composite parent, Control last) {

		Group addrGroup = addGroup(parent, last, "AddressGroup");
		last = addMapedFieldsLabels(addrGroup, null, fieldMapping.getInputLastName(), fieldMapping.fieldAddrLastName);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputCompany(), fieldMapping.fieldAddrCompanyName);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputAddressLine1(), fieldMapping.fieldAddressLine1);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputAddressLine2(), fieldMapping.fieldAddressLine2);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputCity(), fieldMapping.fieldCity);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputState(), fieldMapping.fieldState);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputZip(), fieldMapping.fieldZip);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputCountry(), fieldMapping.fieldCountry);
		Label lAditionalInput = new Label(addrGroup, SWT.LEFT);
		setLook(lAditionalInput);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(last, margin * 4);
		lAditionalInput.setLayoutData(fd);
		lAditionalInput.setText(getString("AddressAdditionalInput.Label"));
		last = lAditionalInput;
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputSuite(), fieldMapping.fieldSuite);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputUrbanization(), fieldMapping.fieldUrbanization);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputPlus4(), fieldMapping.fieldPlus4);
		return addrGroup;
	}

	private Group addEmailGroup(Composite parent, Control last) {

		Group emailGroup = addGroup(parent, last, "EmailGroup");
		addMapedFieldsLabels(emailGroup, null, fieldMapping.getInputEmail(), fieldMapping.fieldEmail);
		return emailGroup;
	}

	private Group addIPGroup(Composite parent, Control last) {

		Group ipGroup = addGroup(parent, null, "IPGroup");
		addMapedFieldsLabels(ipGroup, null, fieldMapping.getInputIPAddress(), fieldMapping.fieldIPAddress);
		return ipGroup;
	}

	private Label addMapedFieldsLabels(Composite parent, Control last, String source, String dest) {

		CCombo wSourceSelect = new CCombo(parent, SWT.BORDER);
		wSourceSelect.setEditable(false);
		wSourceSelect.setVisibleItemCount(10);
		wSourceSelect.add("");
		if (dest.equals(fieldMapping.fieldAddrLastName)) {
			wSourceSelect.add("[Name Parse Last Name 1]");
			wSourceSelect.add("[Name Parse Last Name 2]");
		}
		if (dest.equals(fieldMapping.fieldAddrCompanyName)) {
			wSourceSelect.add("[Name Parse Company]");
		}
		// Add possible source fields and to combo box
		for (String field : fieldMapping.getSourceFields().keySet()) {
			wSourceSelect.add(field);
		}
		Label  lArrow     = new Label(parent, SWT.CENTER);
		Label  lFieldName = new Label(parent, SWT.LEFT);
		Button bSkip      = new Button(parent, SWT.PUSH);
		setLook(wSourceSelect);
		setLook(lArrow);
		setLook(lFieldName);
		setLook(bSkip);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(last, margin * 3);
		fd.right = new FormAttachment(20, 0);
		lFieldName.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(lFieldName, 0);
		fd.top = new FormAttachment(last, margin * 3);
		lArrow.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(lArrow, margin * 3);
		fd.top = new FormAttachment(last, margin * 3);
		fd.right = new FormAttachment(80, -margin * 3);
		wSourceSelect.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(80, margin);
		fd.top = new FormAttachment(last, margin * 3);
		bSkip.setLayoutData(fd);
		bSkip.setText(getString("SkipMapping.Label"));
		lCombos.put(dest, wSourceSelect);
		bSkip.setData(wSourceSelect);
		wSourceSelect.setText(source);
		lArrow.setText(getString("RTArrow.Label"));
		lFieldName.setText(dest);
		SelectionListener lsChanged = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {

				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {

				checkNameFields((CCombo) event.widget);
			}
		};
		SelectionListener lsSkip = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {

				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {

				CCombo md = (CCombo) event.widget.getData();
				md.setText("");
				checkNameFields((CCombo) event.widget.getData());
			}
		};
		wSourceSelect.addSelectionListener(lsChanged);
		bSkip.addSelectionListener(lsSkip);
		return lFieldName;
	}

	private Group addNameGroup(Composite parent, Control last) {

		Group nameGroup = addGroup(parent, last, "NameGroup");
		last = addMapedFieldsLabels(nameGroup, null, fieldMapping.getInputFullName(), fieldMapping.fieldFullName);
		addMapedFieldsLabels(nameGroup, last, fieldMapping.getInputCompanyName(), fieldMapping.fieldCompanyName);
		return nameGroup;
	}

	private Group addPhoneGroup(Composite parent, Control last) {

		Group phoneGroup = addGroup(parent, last, "PhoneGroup");
		addMapedFieldsLabels(phoneGroup, null, fieldMapping.getInputPhone(), fieldMapping.fieldPhone);
		return phoneGroup;
	}

	private Group addSMAddressGroup(Composite parent, Control last) {

		Group smAddrGroup = addGroup(parent, last, "SMAddressGroup");
		last = addMapedFieldsLabels(smAddrGroup, null, fieldMapping.getInputCompany(), fieldMapping.fieldAddrCompanyName);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputAddressLine1(), fieldMapping.fieldAddressLine1);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputAddressLine2(), fieldMapping.fieldAddressLine2);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputCity(), fieldMapping.fieldCity);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputState(), fieldMapping.fieldState);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputZip(), fieldMapping.fieldZip);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputCountry(), fieldMapping.fieldCountry);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputSuite(), fieldMapping.fieldSuite);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputPrivateMailbox(), fieldMapping.fieldPrivateMailbox);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputUrbanization(), fieldMapping.fieldUrbanization);
		last = addMapedFieldsLabels(smAddrGroup, last, fieldMapping.getInputPlus4(), fieldMapping.fieldPlus4);
		return smAddrGroup;
	}

	private Group addSMNameGroup(Composite parent, Control last) {

		Group smNameGroup = addGroup(parent, last, "SMNameGroup");
		Label lNameInput  = new Label(smNameGroup, SWT.LEFT);
		setLook(lNameInput);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, margin * 4);
		lNameInput.setLayoutData(fd);
		lNameInput.setText("Use Full Name:");
		last = lNameInput;
		if (!Const.isEmpty(fieldMapping.getInputFullName())) {
			last = addMapedFieldsLabels(smNameGroup, last, fieldMapping.getInputFullName(), fieldMapping.fieldFullName);
		} else {
			lNameInput.setText("Use Parsed Components:");
			last = addMapedFieldsLabels(smNameGroup, last, fieldMapping.getInputPrefix(), fieldMapping.fieldSMPrefix);
			last = addMapedFieldsLabels(smNameGroup, last, fieldMapping.getInputFirstName(), fieldMapping.fieldSMFirst);
			last = addMapedFieldsLabels(smNameGroup, last, fieldMapping.getInputMiddleName(), fieldMapping.fieldSMMiddle);
			last = addMapedFieldsLabels(smNameGroup, last, fieldMapping.getInputLastName(), fieldMapping.fieldSMLast);
			last = addMapedFieldsLabels(smNameGroup, last, fieldMapping.getInputSuffix(), fieldMapping.fieldSMSuffix);
		}
		return smNameGroup;
	}

	private void checkNameFields(CCombo caller) {

		if ((((MDCheckStepData) data).checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			if (addrLicensed && ((caller.equals(lCombos.get(fieldMapping.fieldFullName)) || caller.equals(lCombos.get(fieldMapping.fieldCompanyName))))) {
				if (Const.isEmpty(lCombos.get(fieldMapping.fieldFullName).getText())) {
					lCombos.get(fieldMapping.fieldAddrLastName).setText(fieldMapping.getFieldGuesser().guessAddrLastNameInput(false));
				} else {
					lCombos.get(fieldMapping.fieldAddrLastName).setText(fieldMapping.getFieldGuesser().guessAddrLastNameInput(true));
				}
				if (Const.isEmpty(lCombos.get(fieldMapping.fieldCompanyName).getText())) {
					lCombos.get(fieldMapping.fieldAddrCompanyName).setText(fieldMapping.getFieldGuesser().guessAddrCompanyNameInput(false));
				} else {
					lCombos.get(fieldMapping.fieldAddrCompanyName).setText(fieldMapping.getFieldGuesser().guessAddrCompanyNameInput(true));
				}
			}
		}
	}

	private void enable(MDCheckStepData checkStepdata) {

		if ((checkStepdata.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			if (!checkStepdata.getNameParse().isLicensed()) {
				gName.setText("Name Parse Not Licensed");
				for (Control child : gName.getChildren()) {
					child.setEnabled(false);
				}
			}
			if (!checkStepdata.getAddressVerify().isLicensed()) {
				addrLicensed = false;
				gAddress.setText("Address Verify Not Licensed");
				for (Control child : gAddress.getChildren()) {
					child.setEnabled(false);
				}
			} else {
				addrLicensed = true;
			}
			if (!checkStepdata.getPhoneVerify().isLicensed()) {
				gPhone.setText("Phone Verify Not Licensed");
				for (Control child : gPhone.getChildren()) {
					child.setEnabled(false);
				}
			}
			if (!checkStepdata.getEmailVerify().isLicensed()) {
				gEmail.setText("Email Verify Not Licensed");
				for (Control child : gEmail.getChildren()) {
					child.setEnabled(false);
				}
			}
		}
		if ((checkStepdata.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			// being web only SM dosent have an isLicensed state so do nothing
			// here for now
		}
		if ((checkStepdata.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			if (!checkStepdata.getIPLocator().isLicensed()) {
				gIpLocator.setText("IP Locator Not Licensed");
				for (Control child : gIpLocator.getChildren()) {
					child.setEnabled(false);
				}
			}
		}
	}

	@Override
	protected void createContents(Composite parent, Object data) {

		MDCheckStepData msd = (MDCheckStepData) data;
		fieldMapping = msd.getFieldMapping();
		fieldMapping.mapFields();
		lCombos = new TreeMap<String, CCombo>();
		Label description = new Label(parent, SWT.LEFT | SWT.WRAP);
		setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(0, margin);
		description.setLayoutData(fd);
		description.setText(getString("Description.Label"));
		Group mappingGroup = addGroup(parent, description, "FieldMappingGroup");
		if ((msd.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			gName = addNameGroup(mappingGroup, null);
			gAddress = addAddressGroup(mappingGroup, gName);
			gPhone = addPhoneGroup(mappingGroup, gAddress);
			gEmail = addEmailGroup(mappingGroup, gPhone);
		}
		if ((msd.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			gSmartMoverName = addSMNameGroup(mappingGroup, null);
			gSmartMoverAddr = addSMAddressGroup(mappingGroup, gSmartMoverName);
		}
		if ((msd.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			gIpLocator = addIPGroup(mappingGroup, null);
		}
		mappingGroup.getParent().pack();
	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		// not used
		return false;
	}

	@Override
	protected Button[] getDialogButtons() {

		Button wAccept = new Button(getShell(), SWT.PUSH);
		wAccept.setText("Accept");
		Button wDecline = new Button(getShell(), SWT.PUSH);
		wDecline.setText("Decline");
		wAccept.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				fieldMapping.updateInput(lCombos);
				fieldMapping.setMapping();
				dialog.refreshTabs();
				dispose();
			}
		});
		wDecline.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				dispose();
			}
		});
		return new Button[] { wAccept, wDecline };
	}

	@Override
	protected String getHelpURLKey() {
		// TODO get help url for field Mapping
		return null;
	}

	@Override
	protected Class<?> getPackage() {

		return PKG;
	}

	@Override
	protected String getStringPrefix() {

		return "FieldMappingDialog";
	}

	@Override
	protected void init(Object data) {

		MDCheckStepData checkStepdata = (MDCheckStepData) data;
		enable(checkStepdata);
	}
}
