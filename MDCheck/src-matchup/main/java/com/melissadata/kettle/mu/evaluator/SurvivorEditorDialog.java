package com.melissadata.kettle.mu.evaluator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.MDCheckStepData;

public class SurvivorEditorDialog extends MDAbstractDialog {

	private static final Class<?> PKG = MDCheckDialog.class;
	private MDCheckHelper helper;

	private MDCheckStepData stepData;

	private Text txtSource;
	private Button rSourceDefault;
	private Button rSourceCustom;
	private Button pSourceEdit;

	private Text txtPrioritization;
	private Button rPrioritizationDefault;
	private Button rPrioritizationCustom;
	private Button pPrioritizationEdit;

	private Button rLowest;
	private Button rHighest;

	private SurvivorField clonedSurvivor;
	private SurvivorField survivor;

	private boolean defaultSource;
	private boolean defaultPriortization;

	private MDCheckDialog dialog1;

	public SurvivorEditorDialog(MDCheckDialog dialog, MDCheckStepData mdStepData) {
		super(dialog, SWT.NONE);
		dialog1 = dialog;

	}

	@Override
	protected void createContents(Composite parent, Object data) {
		stepData = (MDCheckStepData) data;
		Image ime = new Image(this.getDisplay(), getClass().getResourceAsStream("/com/melissadata/kettle/images/PDI_MD_MatchUp_32.png"));
		this.getShell().setImage(ime);

		helper = ((MDCheckDialog) dialog).getHelper();
		Composite last = createSourceGroup(parent, null);
		createPrioritizationGroup(parent, last);

	}

	private void editSource() {
		GRCustomDialog grDialog = new GRCustomDialog((MDCheckDialog) dialog, stepData);

		grDialog.setRowMeta(dialog1.getRowMetaInterface());
		grDialog.setSurvivor(clonedSurvivor, true, false);

		if (grDialog.open()) {
			txtSource.setText(clonedSurvivor.getSource());
		}

	}

	private void editPrioritization() {
		GRCustomDialog grDialog = new GRCustomDialog((MDCheckDialog) dialog, stepData);

		grDialog.setSurvivor(clonedSurvivor, false, true);

		if (grDialog.open()) {

			txtPrioritization.setText(clonedSurvivor.getPrioritization());
			changed = true;
		}
	}

	private Group createSourceGroup(Composite parent, Control last) {

		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText("Source");
		helper.setLook(wGroup);

		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//fd.bottom = new FormAttachment(50, -helper.margin);
		wGroup.setLayoutData(fd);

		last = null;
		Label description = helper.addLabel(wGroup, last, "SurvivorEditorDialog.Source.Description");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		description.setLayoutData(fd);

		last = helper.addSpacer(wGroup, description);
		last = helper.addSpacer(wGroup, last);
		last = helper.addSpacer(wGroup, last);
		last = rSourceDefault = helper.addRadioButton(wGroup, last, "SurvivorEditorDialog.Source.Default");
		last = rSourceCustom = helper.addRadioButton(wGroup, last, "SurvivorEditorDialog.Source.Custom");

		rSourceDefault.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			public void widgetSelected(SelectionEvent arg0) {
				boolean isSelected = ((Button) arg0.getSource()).getSelection();
				if (isSelected) {
					defaultSource = true;
				}

				updateSource();

			}

		});
		rSourceCustom.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// 
			}

			public void widgetSelected(SelectionEvent arg0) {
				boolean isSelected = ((Button) arg0.getSource()).getSelection();
				if (isSelected) {
					defaultSource = false;
				}
				updateSource();

			}

		});

		txtSource = new Text(wGroup, SWT.BORDER);
		helper.setLook(txtSource);
		fd = new FormData();
		fd.left = new FormAttachment(rSourceCustom, helper.margin);
		fd.top = new FormAttachment(rSourceDefault, helper.margin);
		fd.right = new FormAttachment(80, -helper.margin);

		txtSource.setLayoutData(fd);
		txtSource.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				changed = true;
			}

		});

		last = pSourceEdit = helper.addPushButton(wGroup, rSourceDefault, "SurvivorEditorDialog.Prioritization.Edit", new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			public void widgetSelected(SelectionEvent arg0) {
				editSource();
				enable();
				changed = true;

			}

		});
		fd = new FormData();
		fd.left = new FormAttachment(txtSource, helper.margin);
		fd.top = new FormAttachment(rSourceDefault, helper.margin);
		//fd.right = new FormAttachment(90, -helper.margin);		
		pSourceEdit.setLayoutData(fd);

		return wGroup;
	}

	private Group createPrioritizationGroup(Composite parent, Control last) {

		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText("Prioritization");//getString("OutputGroup.Label")
		helper.setLook(wGroup);

		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//	fd.bottom = new FormAttachment(90, helper.margin);
		wGroup.setLayoutData(fd);

		last = null;
		Label description = helper.addLabel(wGroup, last, "SurvivorEditorDialog.Prioritization.Description");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//	fd.bottom = new FormAttachment(50, -helper.margin);
		description.setLayoutData(fd);

		last = helper.addSpacer(wGroup, description);
		last = helper.addSpacer(wGroup, last);
		last = rPrioritizationDefault = helper.addRadioButton(wGroup, last, "SurvivorEditorDialog.Prioritization.Default");
		rPrioritizationDefault.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent arg0) {

				boolean isSelected = ((Button) arg0.getSource()).getSelection();
				if (isSelected) {
					defaultPriortization = true;
					updatePriortization();
					changed = true;
				}
			}

		});
		last = rPrioritizationCustom = helper.addRadioButton(wGroup, last, "SurvivorEditorDialog.Prioritization.Custom");
		rPrioritizationCustom.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent arg0) {
				boolean isSelected = ((Button) arg0.getSource()).getSelection();
				if (isSelected) {
					defaultPriortization = false;
					updatePriortization();
					changed = true;
				}
			}
		});

		last = txtPrioritization = new Text(wGroup, SWT.BORDER);
		helper.setLook(txtPrioritization);
		fd = new FormData();
		fd.left = new FormAttachment(rPrioritizationCustom, helper.margin);
		fd.top = new FormAttachment(rPrioritizationDefault, helper.margin);
		fd.right = new FormAttachment(80, -helper.margin);
		txtPrioritization.setLayoutData(fd);

		txtPrioritization.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				changed = true;
			}

		});

		last = pPrioritizationEdit = helper.addPushButton(wGroup, rPrioritizationDefault, "SurvivorEditorDialog.Prioritization.Edit", new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent arg0) {
				editPrioritization();
			}

		});
		fd = new FormData();
		fd.left = new FormAttachment(txtPrioritization, helper.margin);
		fd.top = new FormAttachment(rPrioritizationDefault, helper.margin);
		pPrioritizationEdit.setLayoutData(fd);

		last = helper.addSpacer(wGroup, last);

		Composite highLowGroup = new Composite(wGroup, SWT.NULL);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		highLowGroup.setLayout(fl);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		highLowGroup.setLayoutData(fd);
		helper.setLook(highLowGroup);

		Label highLowDescription = helper.addLabel(highLowGroup, null, "SurvivorEditorDialog.Prioritization.HighLow.Description");
		last = highLowDescription;

		last = rLowest = helper.addRadioButton(highLowGroup, last, "SurvivorEditorDialog.Prioritization.Lowest");
		rLowest.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent arg0) {
				changed = true;
			}

		});
		rHighest = helper.addRadioButton(highLowGroup, last, "SurvivorEditorDialog.Prioritization.Highest");
		rHighest.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent arg0) {
				changed = true;
			}
		});

		return wGroup;

	}

	public SurvivorField getSurvivorRecord() {
		return survivor;
	}

	public void setSurvivorRecord(SurvivorField survivorRecord) throws CloneNotSupportedException {
		this.survivor = survivorRecord;
		this.clonedSurvivor = survivorRecord.clone();
		getShell().setText("Survivorship: " + clonedSurvivor.getOutputName());
		init(null);
		enable();
	}

	private void updatePriortization() {

		if (defaultPriortization) {
			txtPrioritization.setText("");
		} else {
			if (clonedSurvivor.getPrioritization().equals(clonedSurvivor.defaultPriortization()))
				txtPrioritization.setText("");
			else
				txtPrioritization.setText(clonedSurvivor.getPrioritization());
		}

		enable();

	}

	private void updateSource() {

		if (defaultSource) {
			if (dialog.getSourceFields().containsKey(clonedSurvivor.getOutputName())) {
				clonedSurvivor.setSource(clonedSurvivor.getOutputName());
				txtSource.setText("");
			} else {
				noSource();
				rSourceDefault.setSelection(false);
				rSourceCustom.setSelection(true);
				txtSource.setText(clonedSurvivor.getSource());
			}

		} else {
			txtSource.setText(clonedSurvivor.getSource());
		}
		changed = true;

	}

	private void noSource() {
		MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
		box.setText("No input source field");
		box.setMessage("Can not match \"" + clonedSurvivor.getOutputName() + "\" to any Source input field\nUse Custom or select different Output name ");
		box.open();
	}

	private void enable() {

		if (defaultPriortization) {
			rLowest.setEnabled(false);
			rHighest.setEnabled(false);
			txtPrioritization.setEnabled(false);
			pPrioritizationEdit.setEnabled(false);

		} else {
			rLowest.setEnabled(true);
			rHighest.setEnabled(true);
			txtPrioritization.setEnabled(true);
			pPrioritizationEdit.setEnabled(true);

		}

		if (clonedSurvivor.isDescending()) {
			rLowest.setSelection(false);
			rHighest.setSelection(true);
		} else {
			rLowest.setSelection(true);
			rHighest.setSelection(false);
		}

	}

	@Override
	protected void init(Object data) {
		if (clonedSurvivor != null) {
			if (!clonedSurvivor.getSource().isEmpty()) {
				if (clonedSurvivor.getSource().equals(clonedSurvivor.getOutputName())) {
					rSourceDefault.setSelection(true);
					rSourceCustom.setSelection(false);
					defaultSource = true;
					txtSource.setText("");

				} else {
					rSourceDefault.setSelection(false);
					rSourceCustom.setSelection(true);
					txtSource.setText(clonedSurvivor.getSource());
				}

			} else {
				if (dialog.getSourceFields().keySet().contains(clonedSurvivor.getOutputName())) {

					clonedSurvivor.setSource(clonedSurvivor.getOutputName());
					rSourceDefault.setSelection(true);
					rSourceCustom.setSelection(false);

				} else {
					rSourceDefault.setSelection(false);
					rSourceCustom.setSelection(true);
				}
				txtSource.setText("");
			}

			if (!clonedSurvivor.getPrioritization().isEmpty() && !clonedSurvivor.getPrioritization().equals(clonedSurvivor.defaultPriortization())) {
				txtPrioritization.setText(clonedSurvivor.getPrioritization());
				rPrioritizationDefault.setSelection(false);
				rPrioritizationCustom.setSelection(true);
			} else {
				rPrioritizationDefault.setSelection(true);
				rPrioritizationCustom.setSelection(false);
				txtPrioritization.setText("");
				defaultPriortization = true;
			}

			if (clonedSurvivor.isDescending()) {
				rLowest.setSelection(false);
				rHighest.setSelection(true);
			} else {
				rLowest.setSelection(true);
				rHighest.setSelection(false);
			}
		}

		changed = false;
	}

	@Override
	protected boolean getData(Object data) throws KettleException {

		if (defaultPriortization) {
			clonedSurvivor.setPrioritization(clonedSurvivor.defaultPriortization());
		} else {
			if (txtPrioritization.getText().isEmpty()) {
				clonedSurvivor.setPrioritization(clonedSurvivor.defaultPriortization());
			} else {
				clonedSurvivor.setPrioritization(txtPrioritization.getText());
			}

		}

		if (!defaultSource) {
			if (txtSource.getText().isEmpty()) {
				MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText("No input source field");
				box.setMessage("Output Field \"" + clonedSurvivor.getOutputName() + "\" source field is not set. ");
				box.open();
				return false;
			}

			clonedSurvivor.setSource(txtSource.getText());
		}

		clonedSurvivor.setDescending(rHighest.getSelection());

		survivor = clonedSurvivor;

		return true;
	}

	@Override
	protected String getHelpURLKey() {
		// TODO GET HELP URL
		return null;
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.SurvivorEditorDialog";
	}

}
