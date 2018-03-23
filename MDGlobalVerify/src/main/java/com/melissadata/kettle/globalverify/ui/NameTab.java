package com.melissadata.kettle.globalverify.ui;

import java.util.HashMap;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.globalverify.MDGlobalDialog;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.data.NameFields;
import com.melissadata.kettle.globalverify.meta.NameMeta;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressHelper;
import com.melissadata.kettle.globalverify.support.MDTab;
import com.melissadata.kettle.globalverify.support.MetaVal;

public class NameTab implements MDTab {
	private static Class<?>			PKG				= MDGlobalMeta.class;
	private MDGlobalDialog			dialog;
	private MDGlobalAddressHelper	helper;
	private Group					gInput;
	private Group					gNameOutput;
	private Group					gNameOptions;
	private Group					gCompanyOutput;
	private MDInputCombo			wFullName;
	private MDInputCombo			wCompanyName;
	private Text					wPrefix;
	private Text					wFirstName;
	private Text					wMiddleName;
	private Text					wLastName;
	private Text					wSuffix;
	private Text					wGender;
	private Text					wPrefix2;
	private Text					wFirstName2;
	private Text					wMiddleName2;
	private Text					wLastName2;
	private Text					wSuffix2;
	private Text					wGender2;
	private Text					wStandardizedCompanyName;
	private Button					wCorrectMispellings;
	private ComboViewer				vNameOrderHint;
	private ComboViewer				vMiddleNameLogic;
	private String[]				nameOrderHints	= { "DefinitelyFull", "VeryLikelyFull", "ProbablyFull", "Varying", "ProbablyInverse", "VeryLikelyInverse", "DefinitelyInverse", "MixedFirstName", "MixedLastName" };
	private String[]				middleNameLogic	= { "ParseLogic", "HyphenatedLast", "MiddleName" };

	public NameTab(MDGlobalDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
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
		fd.right = new FormAttachment(50, -helper.margin);
		gInput.setLayoutData(fd);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gInput.setLayout(fl);
		wFullName = helper.addInputComboBox(gInput, null, "NameParseTab.FullName", "NInFullName");
		wCompanyName = helper.addInputComboBox(gInput, wFullName.getComboBox(), "NameParseTab.CompanyName", "NInCompanyName");
		wFullName.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				enable();
			}
		});
		wCompanyName.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				enable();
			}
		});
		helper.setLook(wFullName.getComboBox());
		helper.setLook(wCompanyName.getComboBox());
		// Output Components group
		gNameOutput = new Group(wComp, SWT.NONE);
		gNameOutput.setText(getString("OutputComponentsGroup.Label"));
		helper.setLook(gNameOutput);
		fd = new FormData();
		fd.left = new FormAttachment(gInput, helper.margin);
		fd.top = new FormAttachment(description, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(80, -helper.margin);
		gNameOutput.setLayoutData(fd);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gNameOutput.setLayout(fl);
		// Output elements
		Label header1 = new Label(gNameOutput, SWT.LEFT);
		header1.setText(getString("Header1.Label")); //$NON-NLS-1$
		helper.setLook(header1);
		fd = new FormData();
		fd.left = new FormAttachment(10, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		header1.setLayoutData(fd);
		// Output components
		wPrefix = helper.addTextBox(gNameOutput, header1, "NameParseTab.Prefix");
		wFirstName = helper.addTextBox(gNameOutput, wPrefix, "NameParseTab.FirstName");
		wMiddleName = helper.addTextBox(gNameOutput, wFirstName, "NameParseTab.MiddleName");
		wLastName = helper.addTextBox(gNameOutput, wMiddleName, "NameParseTab.LastName");
		wSuffix = helper.addTextBox(gNameOutput, wLastName, "NameParseTab.Suffix");
		wGender = helper.addTextBox(gNameOutput, wSuffix, "NameParseTab.Gender");
		Label header2 = new Label(gNameOutput, SWT.LEFT);
		header2.setText(getString("Header2.Label")); //$NON-NLS-1$
		helper.setLook(header2);
		fd = new FormData();
		fd.left = new FormAttachment(10, helper.margin);
		fd.top = new FormAttachment(wGender, helper.margin);
		// fd.right = new FormAttachment(helper.colWidth[2], -helper.margin);
		header2.setLayoutData(fd);
		wPrefix2 = helper.addTextBox(gNameOutput, header2, "NameParseTab.Prefix");
		wFirstName2 = helper.addTextBox(gNameOutput, wPrefix2, "NameParseTab.FirstName");
		wMiddleName2 = helper.addTextBox(gNameOutput, wFirstName2, "NameParseTab.MiddleName");
		wLastName2 = helper.addTextBox(gNameOutput, wMiddleName2, "NameParseTab.LastName");
		wSuffix2 = helper.addTextBox(gNameOutput, wLastName2, "NameParseTab.Suffix");
		wGender2 = helper.addTextBox(gNameOutput, wSuffix2, "NameParseTab.Gender");
		// Output Components group
		gNameOptions = new Group(wComp, SWT.NONE);
		gNameOptions.setText(getString("ParseOptions.Label"));
		helper.setLook(gNameOutput);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(gInput, 2 * helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		gNameOptions.setLayoutData(fd);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gNameOptions.setLayout(fl);
		helper.setLook(gNameOptions);
		Label nameOptDescription = helper.addLabel(gNameOptions, null, "NameParseOptions.Description");
		Label spacer = helper.addSpacer(gNameOptions, nameOptDescription);
		vNameOrderHint = helper.addEnumComboBox(gNameOptions, spacer, "NameParseOptions.NameOrderHint", null);
		helper.setLook(vNameOrderHint.getControl());
		vMiddleNameLogic = helper.addEnumComboBox(gNameOptions, vNameOrderHint.getControl(), "NameParseOptions.MiddleNameLogic", null);
		helper.setLook(vMiddleNameLogic.getControl());
		Label spacer2 = helper.addSpacer(gNameOptions, vMiddleNameLogic.getControl());
		wCorrectMispellings = helper.addCheckBox(gNameOptions, spacer2, "NameParseOptions.CorrectMispellings");
		helper.addSpacer(gNameOptions, wCorrectMispellings);
		gCompanyOutput = new Group(wComp, SWT.NONE);
		gCompanyOutput.setText(getString("CompanyNameGroup.Label"));
		helper.setLook(gCompanyOutput);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(gNameOptions, 2 * helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		fd.bottom = new FormAttachment(80, -helper.margin);
		gCompanyOutput.setLayoutData(fd);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gCompanyOutput.setLayout(fl);
		helper.setLook(gCompanyOutput);
		wStandardizedCompanyName = helper.addTextBox(gCompanyOutput, null, "NameParseTab.StandardizedCompany");
		helper.addSpacer(gCompanyOutput, wStandardizedCompanyName);
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
	 * @see com.melissadata.kettle.MDTab#advancedConfigChanged()
	 */
	@Override
	public void advancedConfigChanged() {
		// Update enablement
		enable();
	}

	public void dispose() {
		// Nothing to do
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void enable() {
		// Ignore if controls are already disposed
		if (wPrefix.isDisposed())
			return;
		// See if name parsing is enabled
		NameMeta nameParse = dialog.getInput().getNameMeta();
		boolean isLicensed = nameParse.isLicensed();
		// Check all inputs to see if any are set
		boolean noInputFields = false;
		if (Const.isEmpty(wFullName.getValue()) && Const.isEmpty(wCompanyName.getValue())) {
			noInputFields = true;
		}
		// Update display based on enabled features
		if (!isLicensed) {
			gInput.setText(getString("NameObjNotLicensed.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gNameOutput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gNameOptions.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gCompanyOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (noInputFields) {
			gInput.setText(getString("InputNameGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			gCompanyOutput.setText(getString("NoCompanyNameSpecified.Label"));
			gNameOutput.setText(getString("NoInputNameSpecified.Label"));
			gNameOptions.setText(getString("NoInputNameSpecified.Label"));
			for (Control child : gCompanyOutput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gNameOutput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gNameOptions.getChildren()) {
				child.setEnabled(false);
			}
		} else {
			gInput.setText(getString("InputNameGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			if (Const.isEmpty(wFullName.getValue())) {
				gNameOutput.setText(getString("NoInputNameSpecified.Label"));
				for (Control child : gNameOutput.getChildren()) {
					child.setEnabled(false);
				}
				gNameOptions.setText(getString("NoInputNameSpecified.Label"));
				for (Control child : gNameOptions.getChildren()) {
					child.setEnabled(false);
				}
			} else {
				gNameOutput.setText(getString("OutputComponentsGroup.Label"));
				for (Control child : gNameOutput.getChildren()) {
					child.setEnabled(true);
				}
				gNameOptions.setText(getString("ParseOptions.Label"));
				for (Control child : gNameOptions.getChildren()) {
					child.setEnabled(true);
				}
			}
		}
		if (!Const.isEmpty(wCompanyName.getValue())) {
			gCompanyOutput.setText(getString("CompanyNameGroup.Label"));
			for (Control child : gCompanyOutput.getChildren()) {
				child.setEnabled(true);
				// wStandardizedCompanyName.setEnabled(true);
			}
		} else {
			gCompanyOutput.setText(getString("NoCompanyNameSpecified.Label"));
			for (Control child : gCompanyOutput.getChildren()) {
				child.setEnabled(false);
			}
		}
	}

	public String getCompanyName() {
		return wCompanyName.getValue();
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	@Override
	public void getData(MDGlobalMeta data) {
		HashMap<String, MetaVal> optFields = data.getNameMeta().getNameFields().optionFields;
		HashMap<String, MetaVal> inFields = data.getNameMeta().getNameFields().inputFields;
		HashMap<String, MetaVal> outFields = data.getNameMeta().getNameFields().outputFields;
		optFields.get(NameFields.TAG_OPTION_NAME_ORDER).metaValue = ((IStructuredSelection) vNameOrderHint.getSelection()).getFirstElement().toString();
		optFields.get(NameFields.TAG_OPTION_MIDDLE_NAME_LOGIC).metaValue = ((IStructuredSelection) vMiddleNameLogic.getSelection()).getFirstElement().toString();
		optFields.get(NameFields.TAG_OPTION_CORRECT_MISSPELLING).metaValue = wCorrectMispellings.getSelection() ? "ON" : "OFF";
		inFields.get(NameFields.TAG_INPUT_FULLNAME).metaValue = wFullName.getValue();
		inFields.get(NameFields.TAG_INPUT_COMPANY_NAME).metaValue = wCompanyName.getValue();
		outFields.get(NameFields.TAG_OUTPUT_NAME1_PREFIX).metaValue = wPrefix.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME1_FIRST).metaValue = wFirstName.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME1_MIDDLE).metaValue = wMiddleName.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME1_LAST).metaValue = wLastName.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME1_SUFFIX).metaValue = wSuffix.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME1_GENDER).metaValue = wGender.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME2_PREFIX).metaValue = wPrefix2.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME2_FIRST).metaValue = wFirstName2.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME2_MIDDLE).metaValue = wMiddleName2.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME2_LAST).metaValue = wLastName2.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME2_SUFFIX).metaValue = wSuffix2.getText();
		outFields.get(NameFields.TAG_OUTPUT_NAME2_GENDER).metaValue = wGender2.getText();
		outFields.get(NameFields.TAG_OUTPUT_COMPANY_NAME).metaValue = wStandardizedCompanyName.getText();
	}

	public String getFullName() {
		return wFullName.getValue();
	}

	/**
	 * @return The URL for help information
	 */
	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDGlobalVerify.Help.NameTab");
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDGlobalDialog.NameParseTab." + key, args);
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	@Override
	public boolean init(MDGlobalMeta data) {
		// Get the name parsing meta data
		HashMap<String, MetaVal> optFields = data.getNameMeta().getNameFields().optionFields;
		HashMap<String, MetaVal> inFields = data.getNameMeta().getNameFields().inputFields;
		HashMap<String, MetaVal> outFields = data.getNameMeta().getNameFields().outputFields;
		for (String hint : nameOrderHints) {
			vNameOrderHint.add(hint);
		}
		vNameOrderHint.setSelection(new StructuredSelection(optFields.get(NameFields.TAG_OPTION_NAME_ORDER).metaValue));
		for (String logic : middleNameLogic) {
			vMiddleNameLogic.add(logic);
		}
		vMiddleNameLogic.setSelection(new StructuredSelection(optFields.get(NameFields.TAG_OPTION_MIDDLE_NAME_LOGIC).metaValue));
		wCorrectMispellings.setSelection(optFields.get(NameFields.TAG_OPTION_CORRECT_MISSPELLING).metaValue.equals("ON"));
		wFullName.setValue(inFields.get(NameFields.TAG_INPUT_FULLNAME).metaValue);
		wCompanyName.setValue(inFields.get(NameFields.TAG_INPUT_COMPANY_NAME).metaValue);
		wPrefix.setText(outFields.get(NameFields.TAG_OUTPUT_NAME1_PREFIX).metaValue);
		wFirstName.setText(outFields.get(NameFields.TAG_OUTPUT_NAME1_FIRST).metaValue);
		wMiddleName.setText(outFields.get(NameFields.TAG_OUTPUT_NAME1_MIDDLE).metaValue);
		wLastName.setText(outFields.get(NameFields.TAG_OUTPUT_NAME1_LAST).metaValue);
		wSuffix.setText(outFields.get(NameFields.TAG_OUTPUT_NAME1_SUFFIX).metaValue);
		wGender.setText(outFields.get(NameFields.TAG_OUTPUT_NAME1_GENDER).metaValue);
		wPrefix2.setText(outFields.get(NameFields.TAG_OUTPUT_NAME2_PREFIX).metaValue);
		wFirstName2.setText(outFields.get(NameFields.TAG_OUTPUT_NAME2_FIRST).metaValue);
		wMiddleName2.setText(outFields.get(NameFields.TAG_OUTPUT_NAME2_MIDDLE).metaValue);
		wLastName2.setText(outFields.get(NameFields.TAG_OUTPUT_NAME2_LAST).metaValue);
		wSuffix2.setText(outFields.get(NameFields.TAG_OUTPUT_NAME2_SUFFIX).metaValue);
		wGender2.setText(outFields.get(NameFields.TAG_OUTPUT_NAME2_GENDER).metaValue);
		wStandardizedCompanyName.setText(outFields.get(NameFields.TAG_OUTPUT_COMPANY_NAME).metaValue);
		enable();
		return true;
	}
}
