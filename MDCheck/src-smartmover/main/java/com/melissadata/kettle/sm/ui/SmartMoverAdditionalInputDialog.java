package com.melissadata.kettle.sm.ui;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.sm.SmartMoverMeta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDInputCombo;

public class SmartMoverAdditionalInputDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= SmartMoverAdditionalInputDialog.class;
	private MDInputCombo	wInputAddrSuite;
	private MDInputCombo	wInputAddrPMB;
	private MDInputCombo	wInputAddrUrbanization;
	private MDInputCombo	wInputAddrPlus4;

	public SmartMoverAdditionalInputDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		wInputAddrSuite = addInputComboBox(parent, null, "InputAddrSuite", "InAddrSuite");
		wInputAddrPMB = addInputComboBox(parent, wInputAddrSuite.getComboBox(), "InputAddrPMB", "InAddrPMB");
		wInputAddrUrbanization = addInputComboBox(parent, wInputAddrPMB.getComboBox(), "InputAddrUrbanization", "InAddrUrbanization");
		wInputAddrPlus4 = addInputComboBox(parent, wInputAddrUrbanization.getComboBox(), "InputAddrPlus4", "InAddrPlus4");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) throws KettleException {
		// Local version of input that is dialog specific
		SmartMoverMeta smartMover = ((MDCheckStepData) data).getSmartMover();
		smartMover.setInputAddrSuite(wInputAddrSuite.getValue());
		smartMover.setInputAddrPMB(wInputAddrPMB.getValue());
		smartMover.setInputAddrUrbanization(wInputAddrUrbanization.getValue());
		smartMover.setInputAddrPlus4(wInputAddrPlus4.getValue());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
			return "MDCheck.Help.SmartMoverAdditionalInputDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getPackage()
	 */
	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.SmartMoverAdditionalInputDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// Local version of input that is dialog specific
		SmartMoverMeta smartMover = ((MDCheckStepData) data).getSmartMover();
		wInputAddrSuite.setValue(smartMover.getInputAddrSuite());
		wInputAddrPMB.setValue(smartMover.getInputAddrPMB());
		wInputAddrUrbanization.setValue(smartMover.getInputAddrUrbanization());
		wInputAddrPlus4.setValue(smartMover.getInputAddrPlus4());
	}
}
