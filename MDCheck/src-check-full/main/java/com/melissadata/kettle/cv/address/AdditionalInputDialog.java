package com.melissadata.kettle.cv.address;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDInputCombo;

public class AdditionalInputDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= AdditionalInputDialog.class;
	private MDInputCombo	wSuite;
	private MDInputCombo	wUrbanization;
	private MDInputCombo	wPlus4;

	/**
	 * Called to create additional input address fields dialog
	 *
	 * @param dialog
	 */
	public AdditionalInputDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		// Create a input combo boxes
		Control[] wControls = addComboBox(parent, null, "AdditionalInput.AInSuite");
		Control[] wControls2 = addComboBox(parent, wControls[1], "AdditionalInput.AInUrbanization");
		Control[] wControls3 = addComboBox(parent, wControls2[1], "AdditionalInput.AInPlus4");
		// Return the new combos
		MDCheckDialog checkDialog = (MDCheckDialog) dialog;
		wSuite = new MDInputCombo(wControls, checkDialog.getSourceFields(), "AInSuite");
		wUrbanization = new MDInputCombo(wControls2, checkDialog.getSourceFields(), "AInUrbanization");
		wPlus4 = new MDInputCombo(wControls3, checkDialog.getSourceFields(), "AInPlus4");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) {
		// Local version of input that is dialog specific
		AddressVerifyMeta avMeta = ((MDCheckStepData) data).getAddressVerify();
		// Load data from controls
		avMeta.setInputSuite(wSuite.getValue());
		avMeta.setInputUrbanization(wUrbanization.getValue());
		avMeta.setInputPlus4(wPlus4.getValue());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
			return "MDCheck.Help.AdditionalInputDialog";
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
		return "MDCheckDialog.AdditionalInputDialog";
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
		wSuite.setValue(avMeta.getInputSuite());
		wUrbanization.setValue(avMeta.getInputUrbanization());
		wPlus4.setValue(avMeta.getInputPlus4());
		getShell().pack();
	}
}
