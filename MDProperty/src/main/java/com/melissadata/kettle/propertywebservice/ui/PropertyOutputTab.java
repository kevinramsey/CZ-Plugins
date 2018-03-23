package com.melissadata.kettle.propertywebservice.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;
import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceDialog;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.support.MDPropertyWebServiceHelper;

public class PropertyOutputTab implements MDTab {

	private static Class<?> PKG = MDPropertyWebServiceMeta.class;
	private Label                      description;
	private MDPropertyWebServiceHelper helper;
	private Composite wComp;
	private Group                      propertyUseGroup;
	private Group                      propertySizeGroup;
	private Group                      poolGroup;
	private Group                      utilitiesGroup;
	private Group                      parkingGroup;
	private Group                      yardGardenGroup;
	private Group                      shapeGroup;

	// Property Use
	private Button                     ckIncludePropertyUse;
	private Label                      propUseMoreLess;
	private Text   wYearBuilt;
	private Text   wEffectiveYearBuilt;
	private Text   wZonedCodeLocal;
	private Text   wPropertyUseMuni;
	private Text   wPropertyUseGroup;
	private Text   wPropertyUseStandardized;

	// Property Size
	private Button ckIncludePropertySize;
	private Label  propSizeMoreLess;
	private Text   wAreaBuilding;
	private Text   wAreaBuildingDefinitionCode;
	private Text   wAreaGross;
	private Text   wArea1stFloor;
	private Text   wArea2ndFloor;
	private Text   wAreaUpperFloors;
	private Text   wAreaLotAcres;
	private Text   wAreaLotSF;
	private Text   wLotDepth;
	private Text   wLotWidth;
	private  Text  wAtticArea;
	private Text   wAtticFlag;
	private Text   wBasementArea;
	private Text   wBasementAreaFinished;
	private Text   wBasementAreaUnfinished;
	private Text   wParkingGarage;
	private Text   wParkingGarageArea;
	private Text   wParkingCarPort;
	private Text   wParkingCarPortArea;

	// Pool
	private Button ckIncludePool;
	private Label poolMoreLess;
	private Text wPool;
	private Text wPoolArea;
	private Text wSaunaFlag;

	// Utilities
	private Button ckIncludeUtilities;
	private Label utilitiesMoreLess;
	private Text  wHVACCoolingDetail;
	private Text wHVACHeatingDetail;
	private Text wHVACHeatingFuel;
	private Text wSewageUsage;
	private Text wWaterSource;
	private Text wMobileHomeHookupFlag;

	// Parking
	private Button ckIncludeParking;
	private Label parkingMoreLess;
	private Text wRVParkingFlag;
	private Text wParkingSpaceCount;
	private Text wDrivewayArea;
	private Text wDrivewayMaterial;

	// yard and Garden
	private Button ckIncludeYardGarden;
	private Label yardGardenMoreLess;
	private Text wTopographyCode;
	private Text wFenceCode;
	private Text wFenceArea;
	private Text wCourtyardFlag;
	private Text wCourtyardArea;
	private Text wArborPergolaFlag;
	private Text wSprinklersFlag;
	private Text wGolfCourseGreenFlag;
	private Text wTennisCourtFlag;
	private Text wSportsCourtFlag;
	private Text wArenaFlag;
	private Text wWaterFeatureFlag;
	private Text wPondFlag;
	private Text wBoatLiftFlag;
	//Shape
	private Button ckIncludeShape;
	private Label shapeMoreLess;
	private Text wShape;

	private MDPropertyWebServiceDialog dialog;


	public PropertyOutputTab(MDPropertyWebServiceDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();

		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("Title"));
		wTab.setData(this);

		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());

		// Create the composite that will hold the contents of the tab
		wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);

		// Description line
		description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		description.setText("");

		// Create Groups
		createPropertyUseGroup(wComp, description);
		createPropertySizeGroup(wComp,propertyUseGroup);
		createPoolGroup(wComp, propertySizeGroup);
		createUtilitiesGroup(wComp, poolGroup);
		createParkingGroup(wComp, utilitiesGroup);
		createYardGardenGroup(wComp, parkingGroup);
		createShapeGroup(wComp, yardGardenGroup);


		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, 0);
		fd.bottom = new FormAttachment(150, 0);
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
		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);
		description.setText(getString("Description"));// This is done here due to sizing of dialog
	}

	@Override
	public void advancedConfigChanged() {

		// DO nothing
	}

	@Override
	public void getData(BaseStepMeta meta) {

		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;

		// options
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PROPERTY_USE_INFO).metaValue = String.valueOf(ckIncludePropertyUse.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PROPERTY_SIZE).metaValue = String.valueOf(ckIncludePropertySize.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_POOL).metaValue = String.valueOf(ckIncludePool.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_UTILITIES).metaValue = String.valueOf(ckIncludeUtilities.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PARKING).metaValue = String.valueOf(ckIncludeParking.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_YARD_GARDEN_INFO).metaValue = String.valueOf(ckIncludeYardGarden.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_SHAPE).metaValue = String.valueOf(ckIncludeShape.getSelection());

		// property use
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT).metaValue	= wYearBuilt.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT_EFFECTIVE).metaValue	= wEffectiveYearBuilt.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ZONED_CODE_LOCAL).metaValue	= wZonedCodeLocal.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_MUNI).metaValue	= wPropertyUseMuni.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_GROUP).metaValue	= wPropertyUseGroup.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_STANDARDIZED).metaValue	= wPropertyUseStandardized.getText();


		// property size
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING).metaValue	= wAreaBuilding.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING_DEFINITION_CODE).metaValue	= wAreaBuildingDefinitionCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_GROSS).metaValue	= wAreaGross.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_1ST_FLOOR).metaValue	= wArea1stFloor.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_2ND_FLOOR).metaValue	= wArea2ndFloor.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_UPPER_FLOORS).metaValue	= wAreaUpperFloors.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_ACRES).metaValue	= wAreaLotAcres.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_SF).metaValue	= wAreaLotSF.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_DEPTH).metaValue	= wLotDepth.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_WIDTH).metaValue	= wLotWidth.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ATTIC_AREA).metaValue	= wAtticArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ATTIC_FLAG).metaValue	= wAtticFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA).metaValue	= wBasementArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_FINISHED).metaValue	= wBasementAreaFinished.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_UNFINISHED).metaValue	= wBasementAreaUnfinished.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE).metaValue	= wParkingGarage.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE_AREA).metaValue	= wParkingGarageArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT).metaValue	= wParkingCarPort.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT_AREA).metaValue	= wParkingCarPortArea.getText();

		// pool
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL).metaValue	= wPool.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_AREA).metaValue	= wPoolArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAUNA_FLAG).metaValue	= wSaunaFlag.getText();

		//utilities
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_COOLING_DETAIL).metaValue	= wHVACCoolingDetail.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_DETAIL).metaValue	= wHVACHeatingDetail.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_FULE).metaValue	= wHVACHeatingFuel.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SEWAGE_USAGE).metaValue	= wSewageUsage.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WATER_SOURCE).metaValue	= wWaterSource.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MOBIL_HOME_HOOKUP_FLAG).metaValue	= wMobileHomeHookupFlag.getText();

		// parking
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RV_PARKING_FLAG).metaValue	= wRVParkingFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_SPACE_COUNT).metaValue	= wParkingSpaceCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_AREA).metaValue	= wDrivewayArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_MATERIAL).metaValue	= wDrivewayMaterial.getText();

		// yard/garden
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TOPOGRAPHY_CODE).metaValue	= wTopographyCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FENCE_AREA).metaValue	= wFenceArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FENCE_CODE).metaValue	= wFenceCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_FLAG).metaValue	= wCourtyardFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_AREA).metaValue	= wCourtyardArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ARBOR_PERGOLA_FLAG).metaValue	= wArborPergolaFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SPRINKLERS_FLAG).metaValue	= wSprinklersFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GOLF_COURSE_GREEN_FLAG).metaValue	= wGolfCourseGreenFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TENNIS_COURT_FLAG).metaValue	= wTennisCourtFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SPORTS_COURSE_FLAG).metaValue	= wSportsCourtFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ARENA_FLAG).metaValue	= wArenaFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WATER_FEATURE_FLAG).metaValue	= wWaterFeatureFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POND_FLAG).metaValue	= wPondFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_LIFT_FLAG).metaValue	= wBoatLiftFlag.getText();

		// shape
		//outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WELL_KNOWN_TEXT).metaValue	= wShape.getText();



	}

	@Override
	public String getHelpURLKey() {

		return BaseMessages.getString(PKG, "MDPropertyWebService.Plugin.Help.OutputFieldsTab.BuildingOutput");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;

		ckIncludePropertyUse.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PROPERTY_USE_INFO).metaValue));
		ckIncludePropertySize.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PROPERTY_SIZE).metaValue));
		ckIncludePool.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_POOL).metaValue));
		ckIncludeUtilities.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_UTILITIES).metaValue));
		ckIncludeParking.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PARKING).metaValue));
		ckIncludeYardGarden.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_YARD_GARDEN_INFO).metaValue));
		ckIncludeShape.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_SHAPE).metaValue));

		// property use
		wYearBuilt.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT).metaValue);
		wEffectiveYearBuilt.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_BUILT_EFFECTIVE).metaValue);
		wZonedCodeLocal.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ZONED_CODE_LOCAL).metaValue);
		wPropertyUseMuni.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_MUNI).metaValue);
		wPropertyUseGroup.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_GROUP).metaValue);
		wPropertyUseStandardized.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_STANDARDIZED).metaValue);

		// property size
		wAreaBuilding.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING).metaValue);
		wAreaBuildingDefinitionCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_BUILDING_DEFINITION_CODE).metaValue);
		wAreaGross.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_GROSS).metaValue);
		wArea1stFloor.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_1ST_FLOOR).metaValue);
		wArea2ndFloor.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_2ND_FLOOR).metaValue);
		wAreaUpperFloors.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_UPPER_FLOORS).metaValue);
		wAreaLotAcres.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_ACRES).metaValue);
		wAreaLotSF.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_AREA_LOT_SF).metaValue);
		wLotDepth.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_DEPTH).metaValue);
		wLotWidth.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_WIDTH).metaValue);
		wAtticArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ATTIC_AREA).metaValue);
		wAtticFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ATTIC_FLAG).metaValue);
		wBasementArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA).metaValue);
		wBasementAreaFinished.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_FINISHED).metaValue);
		wBasementAreaUnfinished.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BASEMENT_AREA_UNFINISHED).metaValue);
		wParkingGarage.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE).metaValue);
		wParkingGarageArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_GARAGE_AREA).metaValue);
		wParkingCarPort.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT).metaValue);
		wParkingCarPortArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_CARPORT_AREA).metaValue);

		// pool
		wPool.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL).metaValue);
		wPoolArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_AREA).metaValue);
		wSaunaFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAUNA_FLAG).metaValue);

		// utilities
		wHVACCoolingDetail.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_COOLING_DETAIL).metaValue);
		wHVACHeatingDetail.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_DETAIL).metaValue);
		wHVACHeatingFuel.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HVAC_HEATING_FULE).metaValue);
		wSewageUsage.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SEWAGE_USAGE).metaValue);
		wWaterSource.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WATER_SOURCE).metaValue);
		wMobileHomeHookupFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MOBIL_HOME_HOOKUP_FLAG).metaValue);

		// parking
		wRVParkingFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RV_PARKING_FLAG).metaValue);
		wParkingSpaceCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARKING_SPACE_COUNT).metaValue);
		wDrivewayArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_AREA).metaValue);
		wDrivewayMaterial.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DRIVEWAY_MATERIAL).metaValue);

		// yard garden
		wTopographyCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TOPOGRAPHY_CODE).metaValue);
		wFenceCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FENCE_CODE).metaValue);
		wFenceArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FENCE_AREA).metaValue);
		wCourtyardArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_AREA).metaValue);
		wCourtyardFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COURTYARD_FLAG).metaValue);
		wArborPergolaFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ARBOR_PERGOLA_FLAG).metaValue);
		wSprinklersFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SPRINKLERS_FLAG).metaValue);
		wGolfCourseGreenFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GOLF_COURSE_GREEN_FLAG).metaValue);
		wTennisCourtFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TENNIS_COURT_FLAG).metaValue);
		wSportsCourtFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SPORTS_COURSE_FLAG).metaValue);
		wArenaFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ARENA_FLAG).metaValue);
		wWaterFeatureFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WATER_FEATURE_FLAG).metaValue);
		wPondFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POND_FLAG).metaValue);
		wBoatLiftFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_LIFT_FLAG).metaValue);

		//shape
		wShape.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WELL_KNOWN_TEXT).metaValue);

		enable();
		return true;
	}

	private void createPropertyUseGroup(Composite parent, Control last) {

		propertyUseGroup = new Group(parent, SWT.NONE);
		propertyUseGroup.setText(getString("PropertyUseGroup.Label"));
		helper.setLook(propertyUseGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		propertyUseGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		propertyUseGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludePropertyUse = helper.addCheckBox(propertyUseGroup, last, "PropertyOutputTab.IncludePropertyUse");
		ckIncludePropertyUse.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		propUseMoreLess = helper.addLabel(propertyUseGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = propUseMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(1);
					propUseMoreLess.setText(getString("Less"));
				} else {
					collapse(1);
					propUseMoreLess.setText(getString("More"));
				}
			}
		};

		propUseMoreLess.addListener(SWT.MouseDown, expandCollapse);

		propUseMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		propUseMoreLess.setText(getString("Less"));

		last = propUseMoreLess;

		last = top = helper.addSpacer(propertyUseGroup, last);
		last = wYearBuilt = helper.addTextBox(propertyUseGroup, last, "PropertyOutputTab.YearBuilt");
		last = wEffectiveYearBuilt = helper.addTextBox(propertyUseGroup, last, "PropertyOutputTab.EffectiveYearBuilt");
		last = wZonedCodeLocal = helper.addTextBox(propertyUseGroup, last,"PropertyOutputTab.ZonedCodeLocal");

		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wPropertyUseMuni = helper.addTextBox(propertyUseGroup, last, "PropertyOutputTab.PropertyUseMuni");
		last = wPropertyUseGroup = helper.addTextBox(propertyUseGroup, last, "PropertyOutputTab.PropertyUseGrp");
		last = wPropertyUseStandardized = helper.addTextBox(propertyUseGroup, last, "PropertyOutputTab.PropertyUseStandardized");
	}

	private void createPropertySizeGroup(Composite parent, Control last) {

		propertySizeGroup = new Group(parent, SWT.NONE);
		propertySizeGroup.setText(getString("PropertySizeGroup.Label"));
		helper.setLook(propertySizeGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		propertySizeGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		propertySizeGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludePropertySize = helper.addCheckBox(propertySizeGroup, last, "PropertyOutputTab.IncludePropertySize");
		ckIncludePropertySize.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		propSizeMoreLess = helper.addLabel(propertySizeGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = propSizeMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(2);
					propSizeMoreLess.setText(getString("Less"));
				} else {
					collapse(2);
					propSizeMoreLess.setText(getString("More"));
				}
			}
		};

		propSizeMoreLess.addListener(SWT.MouseDown, expandCollapse);
		propSizeMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		propSizeMoreLess.setText(getString("Less"));

		last = propSizeMoreLess;
		last = top = helper.addSpacer(propertySizeGroup, last);
		last = wAreaBuilding = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AreaBuilding");
		last = wAreaBuildingDefinitionCode = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AreaBuildingDefinitionCode");
		last = wAreaGross = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AreaGross");
		last = wArea1stFloor = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.Area1stFloor");
		last = wArea2ndFloor = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.Area2ndFloor");
		last = wAreaUpperFloors = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AreaUpperFloors");
		last = wAreaLotAcres = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AreaLotAcres");
		last = wAreaLotSF = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AreaLotSF");
		last = wLotDepth = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.LotDepth");
		last = wLotWidth = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.LotWidth");

		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wAtticArea = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AtticArea");
		last = wAtticFlag = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.AtticFlag");
		last = wBasementArea = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.BasementArea");
		last = wBasementAreaFinished = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.BasementAreaFinished");
		last = wBasementAreaUnfinished = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.BasementAreaUnfinished");
		last = wParkingGarage = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.ParkingGarage");
		last = wParkingGarageArea = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.ParkingGarageArea");
		last = wParkingCarPort = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.ParkingCarport");
		last = wParkingCarPortArea = helper.addTextBox(propertySizeGroup, last, "PropertyOutputTab.ParkingCarportArea");



	}

	private void createPoolGroup(Composite parent, Control last) {

		poolGroup = new Group(parent, SWT.NONE);
		poolGroup.setText(getString("PoolGroup.Label"));
		helper.setLook(poolGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		poolGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		poolGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludePool = helper.addCheckBox(poolGroup, last, "PropertyOutputTab.IncludePoolGroup");
		ckIncludePool.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		poolMoreLess = helper.addLabel(poolGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = poolMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(3);
					poolMoreLess.setText(getString("Less"));
				} else {
					collapse(3);
					poolMoreLess.setText(getString("More"));
				}
			}
		};

		poolMoreLess.addListener(SWT.MouseDown, expandCollapse);

		poolMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		poolMoreLess.setText(getString("Less"));

		last = poolMoreLess;

		last = top = helper.addSpacer(poolGroup, last);
		last = wPool = helper.addTextBox(poolGroup, last, "PropertyOutputTab.Pool");
		last = wPoolArea = helper.addTextBox(poolGroup, last, "PropertyOutputTab.PoolArea");

		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wSaunaFlag = helper.addTextBox(poolGroup, last, "PropertyOutputTab.SaunaFlag");
	}

	private void createUtilitiesGroup(Composite parent, Control last) {

		utilitiesGroup = new Group(parent, SWT.NONE);
		utilitiesGroup.setText(getString("UtilitiesGroup.Label"));
		helper.setLook(utilitiesGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		utilitiesGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		utilitiesGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeUtilities = helper.addCheckBox(utilitiesGroup, last, "PropertyOutputTab.IncludeUtilities");
		ckIncludeUtilities.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		utilitiesMoreLess = helper.addLabel(utilitiesGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = utilitiesMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(4);
					utilitiesMoreLess.setText(getString("Less"));
				} else {
					collapse(4);
					utilitiesMoreLess.setText(getString("More"));
				}
			}
		};

		utilitiesMoreLess.addListener(SWT.MouseDown, expandCollapse);

		utilitiesMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		utilitiesMoreLess.setText(getString("Less"));

		last = utilitiesMoreLess;

		last = top = helper.addSpacer(utilitiesGroup, last);
		last = wHVACCoolingDetail = helper.addTextBox(utilitiesGroup, last, "PropertyOutputTab.HVACCoolingDetail");
		last = wHVACHeatingDetail = helper.addTextBox(utilitiesGroup, last, "PropertyOutputTab.HVACHeatingDetail");
		last = wHVACHeatingFuel = helper.addTextBox(utilitiesGroup, last, "PropertyOutputTab.HVACHeatingFuel");


		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wSewageUsage = helper.addTextBox(utilitiesGroup, last, "PropertyOutputTab.SewageUsage");
		last = wWaterSource = helper.addTextBox(utilitiesGroup, last, "PropertyOutputTab.WaterSource");
		last = wMobileHomeHookupFlag = helper.addTextBox(utilitiesGroup, last, "PropertyOutputTab.MobileHomeHookupFlag");
	}

	private void createParkingGroup(Composite parent, Control last) {

		parkingGroup = new Group(parent, SWT.NONE);
		parkingGroup.setText(getString("ParkingGroup.Label"));
		helper.setLook(parkingGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		parkingGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		parkingGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeParking = helper.addCheckBox(parkingGroup, last, "PropertyOutputTab.IncludeParking");
		ckIncludeParking.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		parkingMoreLess = helper.addLabel(parkingGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = parkingMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(5);
					parkingMoreLess.setText(getString("Less"));
				} else {
					collapse(5);
					parkingMoreLess.setText(getString("More"));
				}
			}
		};

		parkingMoreLess.addListener(SWT.MouseDown, expandCollapse);

		parkingMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		parkingMoreLess.setText(getString("Less"));

		last = parkingMoreLess;

		last = top = helper.addSpacer(parkingGroup, last);
		last = wRVParkingFlag = helper.addTextBox(parkingGroup, last, "PropertyOutputTab.RVParkingFlag");
		last = wParkingSpaceCount = helper.addTextBox(parkingGroup, last, "PropertyOutputTab.ParkingSpaceCount");


		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wDrivewayArea = helper.addTextBox(parkingGroup, last, "PropertyOutputTab.DrivewayArea");
		last = wDrivewayMaterial = helper.addTextBox(parkingGroup, last, "PropertyOutputTab.DrivewayMaterial");
	}

	private void createYardGardenGroup(Composite parent, Control last) {

		yardGardenGroup = new Group(parent, SWT.NONE);
		yardGardenGroup.setText(getString("YardGardenGroup.Label"));
		helper.setLook(yardGardenGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		yardGardenGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		yardGardenGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeYardGarden = helper.addCheckBox(yardGardenGroup, last, "PropertyOutputTab.IncludeYardGarden");
		ckIncludeYardGarden.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		yardGardenMoreLess = helper.addLabel(yardGardenGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = yardGardenMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(6);
					yardGardenMoreLess.setText(getString("Less"));
				} else {
					collapse(6);
					yardGardenMoreLess.setText(getString("More"));
				}
			}
		};

		yardGardenMoreLess.addListener(SWT.MouseDown, expandCollapse);

		yardGardenMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		yardGardenMoreLess.setText(getString("Less"));

		last = yardGardenMoreLess;

		last = top = helper.addSpacer(yardGardenGroup, last);
		last = wTopographyCode = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.TopographyCode");
		last = wFenceCode = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.FenceCode");
		last = wFenceArea = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.FenceArea");
		last = wCourtyardFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.CourtyardFlag");
		last = wCourtyardArea = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.CourtyardArea");
		last = wArborPergolaFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.ArborPergolaFlag");
		last = wSprinklersFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.SprinklersFlag");


		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wGolfCourseGreenFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.GolfCourseGreenFlag");
		last = wTennisCourtFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.TennisCourtFlag");
		last = wSportsCourtFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.SportsCourtFlag");
		last = wArenaFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.ArenaFlag");
		last = wWaterFeatureFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.WaterFeatureFlag");
		last = wPondFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.PondFlag");
		last = wBoatLiftFlag = helper.addTextBox(yardGardenGroup, last, "PropertyOutputTab.BoatLiftFlag");
	}

	private void createShapeGroup(Composite parent, Control last) {

		shapeGroup = new Group(parent, SWT.NONE);
		shapeGroup.setText(getString("ShapeGroup.Label"));
		helper.setLook(shapeGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		shapeGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		shapeGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeShape = helper.addCheckBox(shapeGroup, last, "PropertyOutputTab.IncludeShape");
		ckIncludeShape.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		shapeMoreLess = helper.addLabel(shapeGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = shapeMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(7);
					shapeMoreLess.setText(getString("Less"));
				} else {
					collapse(7);
					shapeMoreLess.setText(getString("More"));
				}
			}
		};

		shapeMoreLess.addListener(SWT.MouseDown, expandCollapse);

		shapeMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		shapeMoreLess.setText(getString("Less"));

		last = shapeMoreLess;

	//	last = top = helper.addSpacer(shapeGroup, last);
		Label description = helper.addLabel(shapeGroup,last,"PropertyOutputTab.LotShapeDescription");
		last = description;
		last = top = helper.addSpacer(shapeGroup, last);

		last = wShape = helper.addTextBox(shapeGroup, last, "PropertyOutputTab.LotShape");


		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
	//	last =  = helper.addTextBox(shapeGroup, last, "PropertyOutputTab.");
	}

	private void expand(int group) {

		FormData propertyUseFormData = (FormData) propertyUseGroup.getLayoutData();
		FormData propertySizeFormData  = (FormData) propertySizeGroup.getLayoutData();
		FormData poolFormData  = (FormData) poolGroup.getLayoutData();
		FormData utilitiesFormData = (FormData) utilitiesGroup.getLayoutData();
		FormData parkingFormData = (FormData) parkingGroup.getLayoutData();
		FormData yarGardenFormData = (FormData) yardGardenGroup.getLayoutData();
		FormData shapeFormData = (FormData) shapeGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			// Property Use
			propertyUseFormData.top = new FormAttachment(description, helper.margin);
			propertyUseFormData.bottom = null;
			wComp.layout();
			offset = (propertyUseGroup.getBounds().height + (helper.margin * 2));
			for (Control child : propertyUseGroup.getChildren()) {
				child.setVisible(true);
			}

			propertySizeFormData.top = new FormAttachment(description, offset);
			if (propSizeMoreLess.getText().equals("less")) {
				propertySizeFormData.bottom = null;
			} else {
				offset = (propertyUseGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				propertySizeFormData.bottom = new FormAttachment(description, offset);
			}
		}

		if (group == 2) {
			// Property Size
			propertySizeFormData.top = new FormAttachment(propertyUseGroup, helper.margin);
			propertySizeFormData.bottom = null;
			for (Control child : propertySizeGroup.getChildren()) {
				child.setVisible(true);
			}
			wComp.layout();
			offset = (propertySizeGroup.getBounds().height + (helper.margin * 2));
			poolFormData.top = new FormAttachment(propertySizeGroup, (helper.margin * 2));
			if (poolMoreLess.getText().equals("less")) {
				poolFormData.bottom = null;
			} else {
				offset = (propertySizeGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				poolFormData.bottom = new FormAttachment(propertySizeGroup, offset);
			}
		}

		if (group == 3) {
			// Pool
			poolFormData.top = new FormAttachment(propertySizeGroup, helper.margin * 2);
			poolFormData.bottom = null;
			for (Control child : poolGroup.getChildren()) {
				child.setVisible(true);
			}

			wComp.layout();
			offset = (poolGroup.getBounds().height + (helper.margin * 2));
			utilitiesFormData.top = new FormAttachment(poolGroup, (helper.margin * 2));
			if (utilitiesMoreLess.getText().equals("less")) {
				utilitiesFormData.bottom = null;
			} else {
				offset = (poolGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				utilitiesFormData.bottom = new FormAttachment(poolGroup, offset);
			}


		}

		if (group == 4) {
			//Utilities
			utilitiesFormData.top = new FormAttachment(poolGroup, helper.margin * 2);
			utilitiesFormData.bottom = null;
			for (Control child : utilitiesGroup.getChildren()) {
				child.setVisible(true);
			}

			// Reset the group below
			wComp.layout();
			offset = (utilitiesGroup.getBounds().height + (helper.margin * 2));
			parkingFormData.top = new FormAttachment(utilitiesGroup, (helper.margin * 2));
			if (parkingMoreLess.getText().equals("less")) {
				parkingFormData.bottom = null;
			} else {
				offset = (utilitiesGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				parkingFormData.bottom = new FormAttachment(utilitiesGroup, offset);
			}

		}

		if (group == 5) {
			// Parking
			parkingFormData.top = new FormAttachment(utilitiesGroup, helper.margin * 2);
			parkingFormData.bottom = null;
			for (Control child : parkingGroup.getChildren()) {
				child.setVisible(true);
			}

			// Reset the group below
			wComp.layout();
			offset = (parkingGroup.getBounds().height + (helper.margin * 2));
			yarGardenFormData.top = new FormAttachment(parkingGroup, (helper.margin * 2));
			if (yardGardenMoreLess.getText().equals("less")) {
				yarGardenFormData.bottom = null;
			} else {
				offset = (parkingGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				yarGardenFormData.bottom = new FormAttachment(parkingGroup, offset);
			}
		}

		if (group == 6) {
			//Yard Garden
			yarGardenFormData.top = new FormAttachment(parkingGroup, helper.margin * 2);
			yarGardenFormData.bottom = null;
			for (Control child : yardGardenGroup.getChildren()) {
				child.setVisible(true);
			}

			// Reset the group below
			wComp.layout();
			offset = (yardGardenGroup.getBounds().height + (helper.margin * 2));
			shapeFormData.top = new FormAttachment(yardGardenGroup, (helper.margin * 2));
			if (shapeMoreLess.getText().equals("less")) {
				shapeFormData.bottom = null;
			} else {
				offset = (yardGardenGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				shapeFormData.bottom = new FormAttachment(yardGardenGroup, offset);
			}
		}

		if (group == 7) {
			// Shape
			shapeFormData.top = new FormAttachment(yardGardenGroup, helper.margin * 2);
			shapeFormData.bottom = null;
			for (Control child : shapeGroup.getChildren()) {
				child.setVisible(true);
			}
		}

		enable();
	}

	private void collapse(int group) {

		FormData propUseFormData = (FormData) propertyUseGroup.getLayoutData();
		FormData propSizeFormData  = (FormData) propertySizeGroup.getLayoutData();
		FormData poolFormData  = (FormData) poolGroup.getLayoutData();
		FormData utilitiesFormData = (FormData) utilitiesGroup.getLayoutData();
		FormData parkingFormData = (FormData) parkingGroup.getLayoutData();
		FormData yarGardenFormData = (FormData) yardGardenGroup.getLayoutData();
		FormData shapeFormData = (FormData) shapeGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;
		wComp.layout();
		if (group == 1) {
			//Property Use
			propUseFormData.top = new FormAttachment(description, helper.margin);
			propUseFormData.bottom = new FormAttachment(description, collapsedHeight);

			for (Control child : propertyUseGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			propSizeFormData.top = new FormAttachment(propertyUseGroup, helper.margin);
			if (propSizeMoreLess.getText().equals("less")) {
				propSizeFormData.bottom = null;
			} else {
				offset = (propertyUseGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				propSizeFormData.bottom = new FormAttachment(propertyUseGroup, offset);
			}
		}

		if (group == 2) {
		// Property Size
			propSizeFormData.top = new FormAttachment(description, propertyUseGroup.getBounds().height + (helper.margin * 2));
			propSizeFormData.bottom = new FormAttachment(description, propertyUseGroup.getBounds().height + (helper.margin * 2) + 80);
			for (Control child : propertySizeGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			poolFormData.top = new FormAttachment(propertySizeGroup, helper.margin);
			if (poolMoreLess.getText().equals("less")) {
				poolFormData.bottom = null;
			} else {
				poolFormData.bottom = new FormAttachment(propertySizeGroup, propertySizeGroup.getBounds().height + 80);
			}
		}

		if (group == 3) {
			// Pool
			wComp.layout();
			poolFormData.top = new FormAttachment(propertySizeGroup, helper.margin * 2);
			poolFormData.bottom = new FormAttachment(propertySizeGroup, propertySizeGroup.getBounds().height + 80);
			for (Control child : poolGroup.getChildren()) {
				child.setVisible(false);
			}

			wComp.layout();
			utilitiesFormData.top = new FormAttachment(poolGroup, helper.margin);
			if (utilitiesMoreLess.getText().equals("less")) {
				utilitiesFormData.bottom = null;
			} else {
				utilitiesFormData.bottom = new FormAttachment(poolGroup, poolGroup.getBounds().height + 80);
			}
		}


		if (group == 4) {
			// Utilities
			wComp.layout();
			utilitiesFormData.top = new FormAttachment(poolGroup, helper.margin * 2);
			utilitiesFormData.bottom = new FormAttachment(poolGroup, poolGroup.getBounds().height + 80);
			for (Control child : utilitiesGroup.getChildren()) {
				child.setVisible(false);
			}

			wComp.layout();
			parkingFormData.top = new FormAttachment(utilitiesGroup, helper.margin);
			if (parkingMoreLess.getText().equals("less")) {
				parkingFormData.bottom = null;
			} else {
				parkingFormData.bottom = new FormAttachment(utilitiesGroup, utilitiesGroup.getBounds().height + 80);
			}

		}

		if (group == 5) {
			// Parking
			wComp.layout();
			parkingFormData.top = new FormAttachment(utilitiesGroup, helper.margin * 2);
			parkingFormData.bottom = new FormAttachment(utilitiesGroup, utilitiesGroup.getBounds().height + 80);
			for (Control child : parkingGroup.getChildren()) {
				child.setVisible(false);
			}

			wComp.layout();
			yarGardenFormData.top = new FormAttachment(parkingGroup, helper.margin);
			if (yardGardenMoreLess.getText().equals("less")) {
				yarGardenFormData.bottom = null;
			} else {
				yarGardenFormData.bottom = new FormAttachment(parkingGroup, parkingGroup.getBounds().height + 80);
			}
		}

		if (group == 6) {
			// Yard Garden
			wComp.layout();
			yarGardenFormData.top = new FormAttachment(parkingGroup, helper.margin * 2);
			yarGardenFormData.bottom = new FormAttachment(parkingGroup, parkingGroup.getBounds().height + 80);
			for (Control child : yardGardenGroup.getChildren()) {
				child.setVisible(false);
			}

			wComp.layout();
			shapeFormData.top = new FormAttachment(yardGardenGroup, helper.margin);
			if (shapeMoreLess.getText().equals("less")) {
				shapeFormData.bottom = null;
			} else {
				shapeFormData.bottom = new FormAttachment(yardGardenGroup, yardGardenGroup.getBounds().height + 80);
			}
		}

		if (group == 7) {
			// Shape
			wComp.layout();
			shapeFormData.top = new FormAttachment(yardGardenGroup, helper.margin * 2);
			shapeFormData.bottom = new FormAttachment(yardGardenGroup, yardGardenGroup.getBounds().height + 80);
			for (Control child : shapeGroup.getChildren()) {
				child.setVisible(false);
			}
		}

		enable();
	}

	private void enable() {

		// Include enablement
		if (ckIncludePropertyUse.getSelection()) {
			for (Control child : propertyUseGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : propertyUseGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		if (ckIncludePropertySize.getSelection()) {
			for (Control child : propertySizeGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : propertySizeGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		if (ckIncludePool.getSelection()) {
			for (Control child : poolGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : poolGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		if (ckIncludeUtilities.getSelection()) {
			for (Control child : utilitiesGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : utilitiesGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		if (ckIncludeParking.getSelection()) {
			for (Control child : parkingGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : parkingGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		if (ckIncludeYardGarden.getSelection()) {
			for (Control child : yardGardenGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : yardGardenGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		if (ckIncludeShape.getSelection()) {
			for (Control child : shapeGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : shapeGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		ckIncludePropertyUse.setVisible(true);
		ckIncludePropertyUse.setEnabled(true);
		ckIncludePropertySize.setVisible(true);
		ckIncludePropertySize.setEnabled(true);
		ckIncludePool.setVisible(true);
		ckIncludePool.setEnabled(true);
		ckIncludeUtilities.setVisible(true);
		ckIncludeUtilities.setEnabled(true);
		ckIncludeParking.setVisible(true);
		ckIncludeParking.setEnabled(true);
		ckIncludeYardGarden.setVisible(true);
		ckIncludeYardGarden.setEnabled(true);
		ckIncludeShape.setVisible(true);
		ckIncludeShape.setEnabled(true);

		propUseMoreLess.setVisible(true);
		propUseMoreLess.setEnabled(true);
		propSizeMoreLess.setVisible(true);
		propSizeMoreLess.setEnabled(true);
		poolMoreLess.setVisible(true);
		poolMoreLess.setEnabled(true);
		utilitiesMoreLess.setVisible(true);
		utilitiesMoreLess.setEnabled(true);
		parkingMoreLess.setVisible(true);
		parkingMoreLess.setEnabled(true);
		yardGardenMoreLess.setVisible(true);
		yardGardenMoreLess.setEnabled(true);
		shapeMoreLess.setVisible(true);
		shapeMoreLess.setEnabled(true);
		wComp.layout();

	}


	@Override
	public void dispose() {

	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.PropertyOutputTab." + key, args);
	}
}
