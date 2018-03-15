package com.melissadata.kettle.cv.name;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
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

public class NameParseTab implements MDTab {
	private static Class<?> PKG = MDCheckMeta.class;
	private MDCheckDialog   dialog;
	private MDCheckHelper   helper;
	private MDCheckStepData mdcStepData;
	private NameParseMeta   npMeta;
	private Group           gInput;
	private MDInputCombo    wFullName;
	private MDInputCombo    wCompanyName;
	private Group           gOutput;
	private Text[]          wPrefix;
	private Text[]          wFirstName;
	private Text[]          wMiddleName;
	private Text[]          wLastName;
	private Text[]          wSuffix;
	private Text[]          wGender;
	private Text            wSalutation;
	private Text            wStandardizedCompanyName;
	private Button          bNameParse;
	private boolean         isEnterprise;

	public NameParseTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		mdcStepData = dialog.getData();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
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
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Input Name group
		gInput = new Group(wComp, SWT.NONE);
		gInput.setText(getString("InputNameGroup.Label"));
		helper.setLook(gInput);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		gInput.setLayoutData(fd);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gInput.setLayout(fl);
		// Full Name selector
		wFullName = helper.addInputComboBox(gInput, null, "NameParseTab.FullName", "NInFullName");
		wCompanyName = helper.addInputComboBox(gInput, wFullName.getComboBox(), "NameParseTab.CompanyName", "NInCompanyName");
		// Output Components group
		gOutput = new Group(wComp, SWT.NONE);
		gOutput.setText(getString("OutputComponentsGroup.Label"));
		helper.setLook(gOutput);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(gInput, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		gOutput.setLayoutData(fd);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gOutput.setLayout(fl);
		// Output elements
		Label header1 = new Label(gOutput, SWT.LEFT);
		header1.setText(getString("Header1.Label")); //$NON-NLS-1$
		helper.setLook(header1);
		Label header2 = new Label(gOutput, SWT.LEFT);
		header2.setText(getString("Header2.Label")); //$NON-NLS-1$
		helper.setLook(header2);
		fd = new FormData();
		fd.left = new FormAttachment(helper.colWidth[0], helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(helper.colWidth[1], -helper.margin);
		header1.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(helper.colWidth[1], helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(helper.colWidth[2], -helper.margin);
		header2.setLayoutData(fd);
		// Output components
		wPrefix = helper.addTextBoxes(gOutput, header1, "NameParseTab.Prefix");
		wFirstName = helper.addTextBoxes(gOutput, wPrefix[0], "NameParseTab.FirstName");
		wMiddleName = helper.addTextBoxes(gOutput, wFirstName[0], "NameParseTab.MiddleName");
		wLastName = helper.addTextBoxes(gOutput, wMiddleName[0], "NameParseTab.LastName");
		wSuffix = helper.addTextBoxes(gOutput, wLastName[0], "NameParseTab.Suffix");
		wGender = helper.addTextBoxes(gOutput, wSuffix[0], "NameParseTab.Gender");
		wSalutation = helper.addTextBox(gOutput, wGender[0], "NameParseTab.Salutation");
		wStandardizedCompanyName = helper.addTextBox(gOutput, wSalutation, "NameParseTab.StandardizedCompany");
		// Name Parse Options button
		bNameParse = helper.addPushButton(wComp, gOutput, "NameParseTab.ParseOptions", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent arg0) {
				nameParseOptions();
			}
		});
		bNameParse.setToolTipText(BaseMessages.getString(PKG, "MDCheckDialog.NameParseTab.ParseOptions.ToolTip"));
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
		// Initialzie the tab with the scrolled composite
		wTab.setControl(wSComp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Update enablement
		enable();
	}

	public void dispose() {
		// Nothing to do
	}

	public String getCompanyName() {
		return wCompanyName.getValue();
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {
		NameParseMeta nameParse = data.getNameParse();
		// Fill it in
		nameParse.setFullName(wFullName.getValue());
		nameParse.setInputCompanyName(wCompanyName.getValue());
		for (int i = 0; i < 2; i++) {
			nameParse.setPrefix(i, wPrefix[i].getText());
			nameParse.setFirstName(i, wFirstName[i].getText());
			nameParse.setMiddleName(i, wMiddleName[i].getText());
			nameParse.setLastName(i, wLastName[i].getText());
			nameParse.setSuffix(i, wSuffix[i].getText());
			nameParse.setGender(i, wGender[i].getText());
		}
		nameParse.setSalutation(wSalutation.getText());
		nameParse.setOutputStandardizedCompany(wStandardizedCompanyName.getText());
	}

	public String getFullName() {
		return wFullName.getValue();
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.NameParseTab";
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	public boolean init(MDCheckStepData data) {
		// Get the name parsing meta data
		mdcStepData = data;
		npMeta = mdcStepData.getNameParse();
		isEnterprise = mdcStepData.getAdvancedConfiguration().isEnterprise(MDPropTags.MDLICENSE_PRODUCT_NameObject);

		if (!npMeta.isInitializeOK() || !npMeta.isLicensed()) {
			// Load the controls
			wFullName.setValue("");
			wCompanyName.setValue("");
		} else {
			// Load the controls
			wFullName.setValue(npMeta.getFullName());
			wCompanyName.setValue(npMeta.getInputCompanyName());
		}
		for (int i = 0; i < 2; i++) {
			wPrefix[i].setText(npMeta.getPrefix(i));
			wFirstName[i].setText(npMeta.getFirstName(i));
			wMiddleName[i].setText(npMeta.getMiddleName(i));
			wLastName[i].setText(npMeta.getLastName(i));
			wSuffix[i].setText(npMeta.getSuffix(i));
			wGender[i].setText(npMeta.getGender(i));
		}
		wSalutation.setText(npMeta.getSalutation());
		if (npMeta.getOutputStandardizedCompany() != null) {
			wStandardizedCompanyName.setText(npMeta.getOutputStandardizedCompany());
		}
		// Set initial enablement
		enable();
		// Add listeners to track enablement
		wFullName.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent arg0) {
				enable();
			}
		});
		wCompanyName.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent arg0) {
				enable();
			}
		});
		return false;
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void enable() {
		// Ignore if controls are already disposed
		if (wPrefix[0].isDisposed()) {
			return;
		}
		// See if name parsing is enabled
		boolean isLicensed = npMeta.isLicensed();
		// Check all inputs to see if any are set
		boolean noInputFields = false;
		if (Const.isEmpty(wFullName.getValue())) {
			noInputFields = true;
		}
		// Update display based on enabled features
		if (!isLicensed) {
			gInput.setText(getString("NameObjNotLicensed.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			gOutput.setText(getString("NameObjNotLicensed.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (!npMeta.isInitializeOK()) {
			gInput.setText(getString("NameObjNotInitialized.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			gOutput.setText(getString("NameObjNotInitialized.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (noInputFields) {
			gInput.setText(getString("InputNameGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			if (Const.isEmpty(wCompanyName.getValue())) {
				gOutput.setText(getString("NoInputNameSpecified.Label"));
			} else {
				gOutput.setText(getString("OutputComponentsGroup.Label"));
			}
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else {
			gInput.setText(getString("InputNameGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			gOutput.setText(getString("OutputComponentsGroup.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(true);
			}
		}
		if (!Const.isEmpty(wCompanyName.getValue())) {
			wStandardizedCompanyName.setEnabled(true);
		} else {
			wStandardizedCompanyName.setEnabled(false);
		}
		bNameParse.setEnabled(isLicensed && !noInputFields);

		if (!isEnterprise) {
			wCompanyName.getComboBox().setEnabled(false);
			//wPrefix[0].setEnabled(false);
			//wPrefix[1].setEnabled(false);
			wFirstName[0].setEnabled(false);
			wFirstName[1].setEnabled(false);
			wMiddleName[0].setEnabled(false);
			wMiddleName[1].setEnabled(false);
			wGender[0].setEnabled(false);
			wGender[1].setEnabled(false);
		}
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.NameParseTab." + key, args);
	}

	/**
	 * Called to display the name parse options dialog
	 */
	private void nameParseOptions() {
		MDAbstractDialog npod = new NameParseOptionsDialog(dialog);
		if (npod.open()) {
			dialog.setChanged();
		}
	}
}
