package com.melissadata.kettle.cv.phone;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.cz.support.MDPropTags;
import org.eclipse.swt.SWT;
// import org.eclipse.swt.layout.FormAttachment;
// import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
// import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
// import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
// import org.eclipse.swt.widgets.Button;

import com.melissadata.cz.ui.MDAbstractDialog;

public class PhoneOutputDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= PhoneOutputDialog.class;
	private Text			wOutputCity;
	private Text			wOutputState;
	private Text			wOutputCounty;
	private Text			wOutputCountyFips;
	private Text			wOutputCountryCode;
	private Text			wOutputTimeZone;
	private Text			wOutputTimeZoneCode;

	/**
	 * Called to create the PhoneOutputDialog dialog box.
	 *
	 * @param dialog
	 */
	public PhoneOutputDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		// Phone Output Column Group
		Group phoneOutputGroup = addGroup(parent, null, "phoneOutputGroup");
		wOutputCity = addTextBox(phoneOutputGroup, null, "OutputCity");
		wOutputState = addTextBox(phoneOutputGroup, wOutputCity, "OutputState");
		wOutputCounty = addTextBox(phoneOutputGroup, wOutputState, "OutputCounty");
		wOutputCountyFips = addTextBox(phoneOutputGroup, wOutputCounty, "OutputCountyFips");
		wOutputCountryCode = addTextBox(phoneOutputGroup, wOutputCountyFips, "OutputCountryCode");
		wOutputTimeZone = addTextBox(phoneOutputGroup, wOutputCountryCode, "OutputTimeZone");
		wOutputTimeZoneCode = addTextBox(phoneOutputGroup, wOutputTimeZone, "OutputTimeZoneCode");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) {
		// Local version of input that is dialog specific
		PhoneVerifyMeta pvMeta = ((MDCheckStepData) data).getPhoneVerify();
		// Load data from controls
		pvMeta.setOutputCity(wOutputCity.getText());
		pvMeta.setOutputState(wOutputState.getText());
		pvMeta.setOutputCounty(wOutputCounty.getText());
		pvMeta.setOutputCountyFips(wOutputCountyFips.getText());
		pvMeta.setOutputCountry(wOutputCountryCode.getText());
		pvMeta.setOutputTimeZone(wOutputTimeZone.getText());
		pvMeta.setOutputTZCode(wOutputTimeZoneCode.getText());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
			return "MDCheck.Help.PhoneOutputDialog";
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
		return "MDCheckDialog.PhoneOutputDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		PhoneVerifyMeta pvMeta = ((MDCheckStepData) data).getPhoneVerify();
		wOutputCity.setText(pvMeta.getOutputCity());
		wOutputState.setText(pvMeta.getOutputState());
		wOutputCounty.setText(pvMeta.getOutputCounty());
		wOutputCountyFips.setText(pvMeta.getOutputCountyFips());
		wOutputCountryCode.setText(pvMeta.getOutputCountry());
		wOutputTimeZone.setText(pvMeta.getOutputTimeZone());
		wOutputTimeZoneCode.setText(pvMeta.getOutputTZCode());
		getShell().pack();

		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_PhoneObject)){
			wOutputCity.setEnabled(false);
			wOutputState.setEnabled(false);
			wOutputCounty.setEnabled(false);
			wOutputCountyFips.setEnabled(false);
			wOutputCountryCode.setEnabled(false);
			wOutputTimeZone.setEnabled(false);
			wOutputTimeZoneCode.setEnabled(false);
		}
	}
}
