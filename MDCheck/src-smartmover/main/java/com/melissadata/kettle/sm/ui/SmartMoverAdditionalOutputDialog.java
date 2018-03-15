package com.melissadata.kettle.sm.ui;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.sm.SmartMoverMeta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;

public class SmartMoverAdditionalOutputDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= SmartMoverAdditionalOutputDialog.class;
	private Text			wAddrSuite;
	private Text			wAddrPMB;
	private Text			wAddrPlus4;
	private Text			wAddrDPAndCheckDigit;
	private Text			wAddrCarrierRoute;
	private Text			wAddrUrbanization;
	private Text			wAddrCityAbbreviation;
	private Text			wAddrCountry;
	private Text			wAddrCountryAbbreviation;
	private Text			wDPVFootnotes;
	private Text			wParsedAddrRange;
	private Text			wParsedPreDirectional;
	private Text			wParsedStreetName;
	private Text			wParsedSuffix;
	private Text			wParsedPostDirectional;
	private Text			wParsedSuiteName;
	private Text			wParsedSuiteRange;
	private Text			wParsedPMBName;
	private Text			wParsedPMBRange;
	private Text			wParsedExtraInfo;

// private Text wParsedLockBox;
// private Text wParsedDeliveryInstallation;
	public SmartMoverAdditionalOutputDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		// Create the groups to hold the controls
		Group gAddressInfo = addGroup(parent, null, "AddressInfoGroup");
		Group gParsedAddress = addGroup(parent, gAddressInfo, "ParsedAddressGroup");
		// Fill in the address info group (two columns)
		Composite wLeft = new Composite(gAddressInfo, SWT.NONE);
		setLook(wLeft);
		wLeft.setLayout(new FormLayout());
		Composite wRight = new Composite(gAddressInfo, SWT.NONE);
		setLook(wRight);
		wRight.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, 0);
		fd.bottom = new FormAttachment(100, 0);
		wLeft.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(50, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wRight.setLayoutData(fd);
		wAddrSuite = addTextBox(wLeft, null, "OutputAddrSuite");
		wAddrPMB = addTextBox(wLeft, wAddrSuite, "OutputAddrPMB");
		wAddrPlus4 = addTextBox(wLeft, wAddrPMB, "OutputAddrPlus4");
		wAddrDPAndCheckDigit = addTextBox(wLeft, wAddrPlus4, "OutputAddrDPAndCheckDigit");
		wAddrCarrierRoute = addTextBox(wLeft, wAddrDPAndCheckDigit, "OutputAddrCarrierRoute");
		wAddrUrbanization = addTextBox(wRight, null, "OutputAddrUrbanization");
		wAddrCityAbbreviation = addTextBox(wRight, wAddrUrbanization, "OutputAddrCityAbbreviation");
		wAddrCountry = addTextBox(wRight, wAddrCityAbbreviation, "OutputAddrCountry");
		wAddrCountryAbbreviation = addTextBox(wRight, wAddrCountry, "OutputAddrCountryAbbreviation");
		wDPVFootnotes = addTextBox(wRight, wAddrCountryAbbreviation, "OutputDPVFootnotes");
		// Fill in the parsed address group (two columns)
		wLeft = new Composite(gParsedAddress, SWT.NONE);
		setLook(wLeft);
		wLeft.setLayout(new FormLayout());
		wRight = new Composite(gParsedAddress, SWT.NONE);
		setLook(wRight);
		wRight.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, 0);
		fd.bottom = new FormAttachment(100, 0);
		wLeft.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(50, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wRight.setLayoutData(fd);
		wParsedAddrRange = addTextBox(wLeft, null, "ParsedAddrRange");
		wParsedPreDirectional = addTextBox(wLeft, wParsedAddrRange, "ParsedPreDirectional");
		wParsedStreetName = addTextBox(wLeft, wParsedPreDirectional, "ParsedStreetName");
		wParsedSuffix = addTextBox(wLeft, wParsedStreetName, "ParsedSuffix");
		wParsedPostDirectional = addTextBox(wLeft, wParsedSuffix, "ParsedPostDirectional");
		wParsedSuiteName = addTextBox(wLeft, wParsedPostDirectional, "ParsedSuiteName");
		wParsedSuiteRange = addTextBox(wLeft, wParsedSuiteName, "ParsedSuiteRange");
		wParsedPMBName = addTextBox(wRight, null, "ParsedPMBName");
		wParsedPMBRange = addTextBox(wRight, wParsedPMBName, "ParsedPMBRange");
		wParsedExtraInfo = addTextBox(wRight, wParsedPMBRange, "ParsedExtraInfo");
// wParsedLockBox = addTextBox(wRight, wParsedExtraInfo, "ParsedLockBox");
// wParsedDeliveryInstallation = addTextBox(wRight, wParsedLockBox, "ParsedDeliveryInstallation");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) throws KettleException {
		// Local version of input that is dialog specific
		SmartMoverMeta smartMover = ((MDCheckStepData) data).getSmartMover();
		smartMover.setOutputAddrSuite(wAddrSuite.getText());
		smartMover.setOutputAddrPMB(wAddrPMB.getText());
		smartMover.setOutputAddrPlus4(wAddrPlus4.getText());
		smartMover.setOutputAddrDPAndCheckDigit(wAddrDPAndCheckDigit.getText());
		smartMover.setOutputAddrCarrierRoute(wAddrCarrierRoute.getText());
		smartMover.setOutputAddrUrbanization(wAddrUrbanization.getText());
		smartMover.setOutputAddrCityAbbreviation(wAddrCityAbbreviation.getText());
		smartMover.setOutputAddrCountry(wAddrCountry.getText());
		smartMover.setOutputAddrCountryAbbreviation(wAddrCountryAbbreviation.getText());
		smartMover.setOutputParsedAddrRange(wParsedAddrRange.getText());
		smartMover.setOutputParsedPreDirectional(wParsedPreDirectional.getText());
		smartMover.setOutputParsedStreetName(wParsedStreetName.getText());
		smartMover.setOutputParsedSuffix(wParsedSuffix.getText());
		smartMover.setOutputParsedPostDirectional(wParsedPostDirectional.getText());
		smartMover.setOutputParsedSuiteName(wParsedSuiteName.getText());
		smartMover.setOutputParsedSuiteRange(wParsedSuiteRange.getText());
		smartMover.setOutputParsedPMBName(wParsedPMBName.getText());
		smartMover.setOutputParsedPMBRange(wParsedPMBRange.getText());
		smartMover.setOutputParsedExtraInfo(wParsedExtraInfo.getText());
		smartMover.setOutputDPVFootnotes(wDPVFootnotes.getText());
// smartMover.setOutputParsedLockBox(wParsedLockBox.getText());
// smartMover.setOutputParsedDeliveryInstallation(wParsedDeliveryInstallation.getText());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
		if (!MDCheckMeta.isPentahoPlugin()) {
			return "MDCheck.Help.SmartMoverAdditionalOutputDialog";
		} else {
			return "MDCheck.Plugin.Help.SmartMoverAdditionalOutputDialog";
		}
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
		return "MDCheckDialog.SmartMoverAdditionalOutputDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// Local version of input that is dialog specific
		SmartMoverMeta smartMover = ((MDCheckStepData) data).getSmartMover();
		// Initialize controls
		wAddrSuite.setText(smartMover.getOutputAddrSuite());
		wAddrPMB.setText(smartMover.getOutputAddrPMB());
		wAddrPlus4.setText(smartMover.getOutputAddrPlus4());
		wAddrDPAndCheckDigit.setText(smartMover.getOutputAddrDPAndCheckDigit());
		wAddrCarrierRoute.setText(smartMover.getOutputAddrCarrierRoute());
		wAddrUrbanization.setText(smartMover.getOutputAddrUrbanization());
		wAddrCityAbbreviation.setText(smartMover.getOutputAddrCityAbbreviation());
		wAddrCountry.setText(smartMover.getOutputAddrCountry());
		wAddrCountryAbbreviation.setText(smartMover.getOutputAddrCountryAbbreviation());
		wParsedAddrRange.setText(smartMover.getOutputParsedAddrRange());
		wParsedPreDirectional.setText(smartMover.getOutputParsedPreDirectional());
		wParsedStreetName.setText(smartMover.getOutputParsedStreetName());
		wParsedSuffix.setText(smartMover.getOutputParsedSuffix());
		wParsedPostDirectional.setText(smartMover.getOutputParsedPostDirectional());
		wParsedSuiteName.setText(smartMover.getOutputParsedSuiteName());
		wParsedSuiteRange.setText(smartMover.getOutputParsedSuiteRange());
		wParsedPMBName.setText(smartMover.getOutputParsedPMBName());
		wParsedPMBRange.setText(smartMover.getOutputParsedPMBRange());
		wParsedExtraInfo.setText(smartMover.getOutputParsedExtraInfo());
		wDPVFootnotes.setText(smartMover.getOutputDPVFootnotes());
// wParsedLockBox.setText(smartMover.getOutputParsedLockBox());
// wParsedDeliveryInstallation.setText(smartMover.getOutputParsedDeliveryInstallation());
		getShell().pack();
	}
}
