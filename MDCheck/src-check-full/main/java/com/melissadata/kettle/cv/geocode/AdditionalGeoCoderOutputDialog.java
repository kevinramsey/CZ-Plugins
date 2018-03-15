package com.melissadata.kettle.cv.geocode;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;

public class AdditionalGeoCoderOutputDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= AdditionalGeoCoderOutputDialog.class;
	private Text			wCountyName;
	private Text			wCountyFIPS;
	private Text			wPlaceCode;
	private Text			wPlaceName;
	private Text			wTimeZone;
	private Text			wTimeZoneCode;
	private Text			wCBSACode;
	private Text			wCBSALevel;
	private Text			wCBSATitle;
	private Text			wCBSADivisionCode;
	private Text			wCBSADivisionLevel;
	private Text			wCBSADivisionTitle;
	private Text			wCensusBlock;
	private Text			wCensusTract;

	/**
	 * Called to create additional output fields dialog for geocoder
	 *
	 * @param dialog
	 */
	public AdditionalGeoCoderOutputDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		// Create Geographic information group
		Composite[] geoInfoGroup = add2ColumnGroup(parent, null, "GeoInformationGroup");
		wCountyName = addTextBox(geoInfoGroup[1], null, "CountyName");
		wCountyFIPS = addTextBox(geoInfoGroup[1], wCountyName, "CountyFIPS");
		wPlaceCode = addTextBox(geoInfoGroup[2], null, "PlaceCode");
		wPlaceName = addTextBox(geoInfoGroup[2], wPlaceCode, "PlaceName");
		wTimeZone = addTextBox(geoInfoGroup[2], wPlaceName, "TimeZone");
		wTimeZoneCode = addTextBox(geoInfoGroup[2], wTimeZone, "TimeZoneCode");
		// Census Information groups
		Composite[] censusInfoGroup = add2ColumnGroup(parent, geoInfoGroup[0], "CensusInformationGroup");
		wCBSACode = addTextBox(censusInfoGroup[1], null, "CBSACode");
		wCBSALevel = addTextBox(censusInfoGroup[1], wCBSACode, "CBSALevel");
		wCBSATitle = addTextBox(censusInfoGroup[1], wCBSALevel, "CBSATitle");
		wCBSADivisionCode = addTextBox(censusInfoGroup[1], wCBSATitle, "CBSADivisionCode");
		wCBSADivisionLevel = addTextBox(censusInfoGroup[2], null, "CBSADivisionLevel");
		wCBSADivisionTitle = addTextBox(censusInfoGroup[2], wCBSADivisionLevel, "CBSADivisionTitle");
		wCensusBlock = addTextBox(censusInfoGroup[2], wCBSADivisionTitle, "CensusBlock");
		wCensusTract = addTextBox(censusInfoGroup[2], wCensusBlock, "CensusTract");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) throws KettleException {
		// Local version of input that is dialog specific
		GeoCoderMeta gcMeta = ((MDCheckStepData) data).getGeoCoder();
		gcMeta.setCountyName(wCountyName.getText());
		gcMeta.setCountyFIPS(wCountyFIPS.getText());
		gcMeta.setPlaceCode(wPlaceCode.getText());
		gcMeta.setPlaceName(wPlaceName.getText());
		gcMeta.setTimeZone(wTimeZone.getText());
		gcMeta.setTimeZoneCode(wTimeZoneCode.getText());
		gcMeta.setCBSACode(wCBSACode.getText());
		gcMeta.setCBSALevel(wCBSALevel.getText());
		gcMeta.setCBSATitle(wCBSATitle.getText());
		gcMeta.setCBSADivisionCode(wCBSADivisionCode.getText());
		gcMeta.setCBSADivisionLevel(wCBSADivisionLevel.getText());
		gcMeta.setCBSADivisionTitle(wCBSADivisionTitle.getText());
		gcMeta.setCensusBlock(wCensusBlock.getText());
		gcMeta.setCensusTract(wCensusTract.getText());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
		if (!MDCheckMeta.isPentahoPlugin()) {
			return "MDCheck.Help.AdditionalGeoCoderOutputDialog";
		} else {
			return "MDCheck.Plugin.Help.AdditionalGeoCoderOutputDialog";
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
		return "MDCheckDialog.AdditionalGeoCoderOutputDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// Local version of input that is dialog specific
		GeoCoderMeta gcMeta = ((MDCheckStepData) data).getGeoCoder();
		wCountyName.setText(gcMeta.getCountyName());
		wCountyFIPS.setText(gcMeta.getCountyFIPS());
		wPlaceCode.setText(gcMeta.getPlaceCode());
		wPlaceName.setText(gcMeta.getPlaceName());
		wTimeZone.setText(gcMeta.getTimeZone());
		wTimeZoneCode.setText(gcMeta.getTimeZoneCode());
		wCBSACode.setText(gcMeta.getCBSACode());
		wCBSALevel.setText(gcMeta.getCBSALevel());
		wCBSATitle.setText(gcMeta.getCBSATitle());
		wCBSADivisionCode.setText(gcMeta.getCBSADivisionCode());
		wCBSADivisionLevel.setText(gcMeta.getCBSADivisionLevel());
		wCBSADivisionTitle.setText(gcMeta.getCBSADivisionTitle());
		wCensusBlock.setText(gcMeta.getCensusBlock());
		wCensusTract.setText(gcMeta.getCensusTract());
	}
}
