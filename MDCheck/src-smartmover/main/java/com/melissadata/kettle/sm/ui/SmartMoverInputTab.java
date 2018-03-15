package com.melissadata.kettle.sm.ui;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.sm.SmartMoverMeta;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.i18n.BaseMessages;

public class SmartMoverInputTab implements MDTab {
	private static Class<?> PKG = MDCheckMeta.class;
	private MDCheckDialog  dialog;
	private MDCheckHelper  helper;
	private SmartMoverMeta smMeta;
	private Button         rbFullName;
	private Button         rbParsedComponents;
	private MDInputCombo   wInputFullName;
	private MDInputCombo   wInputNamePrefix;
	private MDInputCombo   wInputNameFirst;
	private MDInputCombo   wInputNameMiddle;
	private MDInputCombo   wInputNameLast;
	private MDInputCombo   wInputNameSuffix;
	private MDInputCombo   wInputAddrCompany;
	private MDInputCombo   wInputAddrLine;
	private MDInputCombo   wInputAddrLine2;
	private MDInputCombo   wInputAddrCity;
	private MDInputCombo   wInputAddrState;
	private MDInputCombo   wInputAddrZip;
	private MDInputCombo   wInputAddrCountry;

	public SmartMoverInputTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		wTab.setData(this);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Add descriptive label
		Label description = helper.addLabel(wComp, null, "SmartMoverInputTab.Description");
		Label spacer = helper.addSpacer(wComp, description);
		// Add the input groups side-by-side in a single composite
		Group gInputName = new Group(wComp, SWT.NONE);
		gInputName.setText(getString("InputNameGroup.Label"));
		helper.setLook(gInputName);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gInputName.setLayout(fl);
		Group gInputAddress = new Group(wComp, SWT.NONE);
		gInputAddress.setText(getString("InputAddressGroup.Label"));
		helper.setLook(gInputAddress);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gInputAddress.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(spacer, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		gInputName.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(spacer, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		gInputAddress.setLayoutData(fd);
		// Name input can be either full name or parsed components
		Label lblNameFormat = helper.addLabel(gInputName, null, "SmartMoverInputTab.NameFormat");
		rbFullName = helper.addRadioButton(gInputName, lblNameFormat, "SmartMoverInputTab.InputFullName");
		rbParsedComponents = helper.addRadioButton(gInputName, rbFullName, "SmartMoverInputTab.InputParsedComponents");
		((FormData) rbParsedComponents.getLayoutData()).top.offset += 2 * helper.margin; // makes it look better
		// Listen for changes to the full name input selection buttons
		rbFullName.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		// Place full name combo on same line as radio button
		wInputFullName = helper.addInputComboBox(gInputName, lblNameFormat, null, "InFullName");
		((FormData) rbFullName.getLayoutData()).right = new FormAttachment(wInputFullName.getComboBox());
		// Place the component input combos below the parsed component button
		wInputNamePrefix = helper.addInputComboBox(gInputName, rbParsedComponents, "SmartMoverInputTab.InputNamePrefix", "InNamePrefix");
		wInputNameFirst = helper.addInputComboBox(gInputName, wInputNamePrefix.getComboBox(), "SmartMoverInputTab.InputNameFirst", "InNameFirst");
		wInputNameMiddle = helper.addInputComboBox(gInputName, wInputNameFirst.getComboBox(), "SmartMoverInputTab.InputNameMiddle", "InNameMiddle");
		wInputNameLast = helper.addInputComboBox(gInputName, wInputNameMiddle.getComboBox(), "SmartMoverInputTab.InputNameLast", "InNameLast");
		wInputNameSuffix = helper.addInputComboBox(gInputName, wInputNameLast.getComboBox(), "SmartMoverInputTab.InputNameSuffix", "InNameSuffix");
		// Add fields to the input address group
		wInputAddrCompany = helper.addInputComboBox(gInputAddress, null, "SmartMoverInputTab.InputAddrCompany", "InAddrCompany");
		wInputAddrLine = helper.addInputComboBox(gInputAddress, wInputAddrCompany.getComboBox(), "SmartMoverInputTab.InputAddrLine", "InAddrLine");
		wInputAddrLine2 = helper.addInputComboBox(gInputAddress, wInputAddrLine.getComboBox(), "SmartMoverInputTab.InputAddrLine2", "InAddrLine2");
		wInputAddrCity = helper.addInputComboBox(gInputAddress, wInputAddrLine2.getComboBox(), "SmartMoverInputTab.InputAddrCity", "InAddrCity");
		wInputAddrState = helper.addInputComboBox(gInputAddress, wInputAddrCity.getComboBox(), "SmartMoverInputTab.InputAddrState", "InAddrState");
		wInputAddrZip = helper.addInputComboBox(gInputAddress, wInputAddrState.getComboBox(), "SmartMoverInputTab.InputAddrZip", "InAddrZip");
		wInputAddrCountry = helper.addInputComboBox(gInputAddress, wInputAddrZip.getComboBox(), "SmartMoverInputTab.InputAddrCountry", "InAddrCountry");
		// Additional input columns
		helper.addPushButton(gInputAddress, wInputAddrCountry.getComboBox(), "SmartMoverInputTab.AdditionalInputColumns", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				additionalInputColumns();
			}
		});
		// Disable for now
		// wInputAddrCountry.setVisible(false);
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

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {

		SmartMoverMeta smartMoverMeta = data.getSmartMover();
		// Fill in the input meta data
		smartMoverMeta.setInputUseFullName(rbFullName.getSelection());
		smartMoverMeta.setInputFullName(wInputFullName.getValue());
		smartMoverMeta.setInputNamePrefix(wInputNamePrefix.getValue());
		smartMoverMeta.setInputNameFirst(wInputNameFirst.getValue());
		smartMoverMeta.setInputNameMiddle(wInputNameMiddle.getValue());
		smartMoverMeta.setInputNameLast(wInputNameLast.getValue());
		smartMoverMeta.setInputNameSuffix(wInputNameSuffix.getValue());
		smartMoverMeta.setInputAddrCompany(wInputAddrCompany.getValue());
		smartMoverMeta.setInputAddrLine(wInputAddrLine.getValue());
		smartMoverMeta.setInputAddrLine2(wInputAddrLine2.getValue());
		smartMoverMeta.setInputAddrCity(wInputAddrCity.getValue());
		smartMoverMeta.setInputAddrState(wInputAddrState.getValue());
		smartMoverMeta.setInputAddrZip(wInputAddrZip.getValue());
		smartMoverMeta.setInputAddrCountry(wInputAddrCountry.getValue());
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.SmartMoverInputTab";
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	public boolean init(MDCheckStepData data) {
		// Get the smart mover meta data
		smMeta = data.getSmartMover();
		// Load the input controls
		rbFullName.setSelection(smMeta.getInputUseFullName());
		rbParsedComponents.setSelection(!smMeta.getInputUseFullName());
		wInputFullName.setValue(smMeta.getInputFullName());
		wInputNamePrefix.setValue(smMeta.getInputNamePrefix());
		wInputNameFirst.setValue(smMeta.getInputNameFirst());
		wInputNameMiddle.setValue(smMeta.getInputNameMiddle());
		wInputNameLast.setValue(smMeta.getInputNameLast());
		wInputNameSuffix.setValue(smMeta.getInputNameSuffix());
		wInputAddrCompany.setValue(smMeta.getInputAddrCompany());
		wInputAddrLine.setValue(smMeta.getInputAddrLine());
		wInputAddrLine2.setValue(smMeta.getInputAddrLine2());
		wInputAddrCity.setValue(smMeta.getInputAddrCity());
		wInputAddrState.setValue(smMeta.getInputAddrState());
		wInputAddrZip.setValue(smMeta.getInputAddrZip());
		wInputAddrCountry.setValue(smMeta.getInputAddrCountry());
		// Handle initial enablement
		enable();
		return false;
	}

	/**
	 * Called to display the additional input columns dialog
	 */
	private void additionalInputColumns() {
		MDAbstractDialog smaid = new SmartMoverAdditionalInputDialog(dialog);
		if (smaid.open()) {
			dialog.setChanged();
		}
	}

	/**
	 * Called to enable components based on current state of input selection
	 */
	private void enable() {
		// Enable name input fields based on the current state of the input selection buttons
		boolean doFullName = rbFullName.getSelection();
		boolean doParsedComponents = rbParsedComponents.getSelection();
		wInputFullName.setEnabled(doFullName);
		wInputNamePrefix.setEnabled(doParsedComponents);
		wInputNameFirst.setEnabled(doParsedComponents);
		wInputNameMiddle.setEnabled(doParsedComponents);
		wInputNameLast.setEnabled(doParsedComponents);
		wInputNameSuffix.setEnabled(doParsedComponents);
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.SmartMoverInputTab." + key, args);
	}
}
