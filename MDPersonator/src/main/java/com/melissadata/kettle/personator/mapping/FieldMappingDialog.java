package com.melissadata.kettle.personator.mapping;

import java.util.SortedMap;
import java.util.TreeMap;

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
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.personator.MDPersonatorDialog;
import com.melissadata.kettle.personator.MDPersonatorMeta;

public class FieldMappingDialog extends MDAbstractDialog {
	private static final Class<?> PKG = MDPersonatorMeta.class;

	private MDPersonatorDialog dialog;
	private FieldMapping fieldMapping;

	private Group gName;
	private Group gAddress;
	private Group gPhone;
	@SuppressWarnings("unused")
	private Group gEmail;

	private SortedMap<String, CCombo> lCombos;

	public FieldMappingDialog(MDPersonatorDialog dialog) {
		super(dialog, SWT.NONE);
		this.dialog = dialog;
	}

	@Override
	protected void createContents(Composite parent, Object data) {

		MDPersonatorMeta pMeta = (MDPersonatorMeta)data;
		this.fieldMapping = pMeta.getFieldMapping();
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

		gName = addNameGroup(mappingGroup, null);
		gAddress = addAddressGroup(mappingGroup, gName);
		gPhone = addPhoneGroup(mappingGroup, gAddress);
		gEmail = addEmailGroup(mappingGroup, gPhone);

		mappingGroup.getParent().pack();
	}

	private Group addNameGroup(Composite parent, Control last) {
		Group nameGroup = addGroup(parent, last, "NameGroup");

		last = addMapedFieldsLabels(nameGroup, null, fieldMapping.getInputCompanyName(), fieldMapping.fieldCompanyName);
		last = addMapedFieldsLabels(nameGroup, last, fieldMapping.getInputFullName(), fieldMapping.fieldFullName);
		
		last = addMapedFieldsLabels(nameGroup, last, fieldMapping.getInputFirstName(), fieldMapping.fieldFirstName);
		last = addMapedFieldsLabels(nameGroup, last, fieldMapping.getInputLastName(), fieldMapping.fieldLastName);
		

		return nameGroup;
	}

	private Group addAddressGroup(Composite parent, Control last) {
		Group addrGroup = addGroup(parent, last, "AddressGroup");

		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputAddressLine1(), fieldMapping.fieldAddressLine1);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputAddressLine2(), fieldMapping.fieldAddressLine2);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputCity(), fieldMapping.fieldCity);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputState(), fieldMapping.fieldState);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputZip(), fieldMapping.fieldZip);
		last = addMapedFieldsLabels(addrGroup, last, fieldMapping.getInputCountry(), fieldMapping.fieldCountry);

		return addrGroup;
	}

	private Group addPhoneGroup(Composite parent, Control last) {
		Group phoneGroup = addGroup(parent, last, "PhoneGroup");
		addMapedFieldsLabels(phoneGroup, null, fieldMapping.getInputPhone(), fieldMapping.fieldPhone);

		return phoneGroup;
	}

	private Group addEmailGroup(Composite parent, Control last) {
		Group emailGroup = addGroup(parent, last, "EmailGroup");
		addMapedFieldsLabels(emailGroup, null, fieldMapping.getInputEmail(), fieldMapping.fieldEmail);

		return emailGroup;
	}

	private Label addMapedFieldsLabels(Composite parent, Control last, String source, String dest) {

		CCombo wSourceSelect = new CCombo(parent, SWT.BORDER);
		wSourceSelect.setEditable(false);
		wSourceSelect.setVisibleItemCount(10);

		wSourceSelect.add("");

		// Add possible source fields and to combo box
		for (String field : fieldMapping.getSourceFields().keySet())
			wSourceSelect.add(field);

		Label lArrow = new Label(parent, SWT.CENTER);
		Label lFieldName = new Label(parent, SWT.LEFT);
		Button bSkip = new Button(parent, SWT.PUSH);

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
		bSkip.setText("Skip Mapping");

		lCombos.put(dest, wSourceSelect);
		bSkip.setData(wSourceSelect);

		wSourceSelect.setText(source);
		lArrow.setText("  -->  ");
		lFieldName.setText(dest);

		SelectionListener lsSkip = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				CCombo md = (CCombo) event.widget.getData();
				md.setText("");
			}

		};

		bSkip.addSelectionListener(lsSkip);

		return lFieldName;
	}

	@Override
	protected Button[] getDialogButtons() {
		Button wAccept = new Button(getShell(), SWT.PUSH);
		wAccept.setText("Accept");

		Button wDecline = new Button(getShell(), SWT.PUSH);
		wDecline.setText("Decline");

		wAccept.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				fieldMapping.updatePersonatorInput(lCombos);
				fieldMapping.setPersonatorMapping();
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

	private void enable() {
		//none right now
	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		// not used
		return false;
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
		return "MDPersonatorDialog.FieldMappingDialog";
	}

	@Override
	protected void init(Object data) {
		enable();
	}

}
