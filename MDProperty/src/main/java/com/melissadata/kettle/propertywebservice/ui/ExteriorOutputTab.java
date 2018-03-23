package com.melissadata.kettle.propertywebservice.ui;

import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceDialog;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.support.MDPropertyWebServiceHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;

import java.util.HashMap;

/**
 * Created by Kevin on 1/16/2018.
 */
public class ExteriorOutputTab implements MDTab {

	private static Class<?> PKG = MDPropertyWebServiceMeta.class;
	private Label                      description;
	private MDPropertyWebServiceHelper helper;
	private MDPropertyWebServiceDialog dialog;
	private Composite wComp;
	private Group                      extStructGroup;
	private Group                      amenitiesGroup;
	private Group                      buildingsGroup;
	//	Exterior Structure
	private Button                     ckIncludeExtStruct;
	private Label                      extStructMoreLess;
	private Text                       wStructureStyle;
	private Text                       wExterior1Code;
	private Text                       wRoofMaterial;
	private Text                       wRoofConstruction;
	private Text                       wStormShutterFlag;
	private Text                       wOverheadDoorFlag;
	//	Exterior Amenities
	private Button                     ckIncludeAmenities;
	private Label                      amenitiesMoreLess;
	private Text                       wViewDescription;
	private Text                       wPorchCode;
	private Text                       wPorchArea;
	private Text                       wPatioArea;
	private Text                       wDeckFlag;
	private Text                       wDeckArea;
	private Text                       wFeatureBalconyFlag;
	private Text                       wBalconyArea;
	private Text                       wBreezewayFlag;
	//	Exterior Buildings
	private Button                     ckIncludeBuildings;
	private Label                      buildingsMoreLess;
	private Text                       wBuildingsCount;
	private Text                       wBathHouseArea;
	private Text                       wBathHouseFlag;
	private Text                       wBoatAccessFlag;
	private Text                       wBoatHouseArea;
	private Text                       wBoatHouseFlag;
	private Text                       wCabinArea;
	private Text                       wCabinFlag;
	private Text                       wCanopyArea;
	private Text                       wCanopyFlag;
	private Text                       wGazeboArea;
	private Text                       wGazeboFlag;
	private Text                       wGranaryArea;
	private Text                       wGranaryFlag;
	private Text                       wGreenHouseArea;
	private Text                       wGreenHouseFlag;
	private Text                       wGuestHouseArea;
	private Text                       wGuestHouseFlag;
	private Text                       wKennelArea;
	private Text                       wKennelFlag;
	private Text                       wLeanToArea;
	private Text                       wLeanToFlag;
	private Text                       wLoadingPlatformArea;
	private Text                       wLoadingPlatformFlag;
	private Text                       wMilkHouseArea;
	private Text                       wMilkHouseFlag;
	private Text                       wOutdoorKitchenFireplaceFlag;
	private Text                       wPoolHouseArea;
	private Text                       wPoolHouseFlag;
	private Text                       wPoultryHouseArea;
	private Text                       wPoultryHouseFlag;
	private Text                       wQuonsetArea;
	private Text                       wQuonsetFlag;
	private Text                       wShedArea;
	private Text                       wShedCode;
	private Text                       wSiloArea;
	private Text                       wSiloFlag;
	private Text                       wStableArea;
	private Text                       wStableFlag;
	private Text                       wStorageBuildingArea;
	private Text                       wStorageBuildingFlag;
	private Text                       wUtilityBuildingArea;
	private Text                       wUtilityBuildingFlag;
	private Text                       wPoleStructureArea;
	private Text                       wPoleStructureFlag;

	public ExteriorOutputTab(MDPropertyWebServiceDialog dialog) {

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
		createExteriorStructureGroup(wComp, description);
		createExteriorAmenitiesGroup(wComp, extStructGroup);
		createExteriorBuildingsGroup(wComp, amenitiesGroup);

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

	private void createExteriorStructureGroup(Composite parent, Control last) {

		extStructGroup = new Group(parent, SWT.NONE);
		extStructGroup.setText(getString("ExteriorStructGroup.Label"));
		helper.setLook(extStructGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		extStructGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		extStructGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeExtStruct = helper.addCheckBox(extStructGroup, last, "ExteriorOutputTab.IncludeExteriorStructure");
		ckIncludeExtStruct.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		extStructMoreLess = helper.addLabel(extStructGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = extStructMoreLess.getText();
				if (tx == getString("More")) {
					//expand(1);
					extStructMoreLess.setText(getString("Less"));
				} else {
					//collapse(1);
					extStructMoreLess.setText(getString("More"));
				}
			}
		};

		extStructMoreLess.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = extStructMoreLess.getText();
				if (tx == getString("More")) {
					expand(1);
					extStructMoreLess.setText(getString("Less"));
				} else {
					collapse(1);
					extStructMoreLess.setText(getString("More"));
				}
			}
		});

		extStructMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		extStructMoreLess.setText(getString("Less"));

		last = top = helper.addSpacer(extStructGroup, last);
		last = wStructureStyle = helper.addTextBox(extStructGroup, last, "ExteriorOutputTab.StructureStyle");
		last = wExterior1Code = helper.addTextBox(extStructGroup, last, "ExteriorOutputTab.Exterior1Code");
		last = wRoofMaterial = helper.addTextBox(extStructGroup, last, "ExteriorOutputTab.RoofMaterial");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;

		last = wRoofConstruction = helper.addTextBox(extStructGroup, last, "ExteriorOutputTab.RoofConstruction");
		last = wStormShutterFlag = helper.addTextBox(extStructGroup, last, "ExteriorOutputTab.StormShutterFlag");
		last = wOverheadDoorFlag = helper.addTextBox(extStructGroup, last, "ExteriorOutputTab.OverheadDoorFlag");
	}

	private void createExteriorAmenitiesGroup(Composite parent, Control last) {

		amenitiesGroup = new Group(parent, SWT.NONE);
		amenitiesGroup.setText(getString("AmenitiesGroup.Label"));
		helper.setLook(amenitiesGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		amenitiesGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		amenitiesGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeAmenities = helper.addCheckBox(amenitiesGroup, last, "ExteriorOutputTab.IncludeAmenities");
		ckIncludeAmenities.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		amenitiesMoreLess = helper.addLabel(amenitiesGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = amenitiesMoreLess.getText();
				if (tx == getString("More")) {
					//expand(1);
					amenitiesMoreLess.setText(getString("Less"));
				} else {
					//collapse(1);
					amenitiesMoreLess.setText(getString("More"));
				}
			}
		};

		amenitiesMoreLess.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = amenitiesMoreLess.getText();
				if (tx == getString("More")) {
					expand(2);
					amenitiesMoreLess.setText(getString("Less"));
				} else {
					collapse(2);
					amenitiesMoreLess.setText(getString("More"));
				}
			}
		});

		amenitiesMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		amenitiesMoreLess.setText(getString("Less"));

		last = top = helper.addSpacer(amenitiesGroup, last);
		last = wViewDescription = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.ViewDescription");
		last = wPorchCode = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.PorchCode");
		last = wPorchArea = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.PorchArea");
		last = wDeckFlag = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.DeckFlag");
		last = wDeckArea = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.DeckArea");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wPatioArea = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.PatioArea");
		last = wFeatureBalconyFlag = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.FeatureBalconyFlag");
		last = wBalconyArea = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.BalconyArea");
		last = wBreezewayFlag = helper.addTextBox(amenitiesGroup, last, "ExteriorOutputTab.BreezewayFlag");

	}

	private void createExteriorBuildingsGroup(Composite parent, Control last) {

		buildingsGroup = new Group(parent, SWT.NONE);
		buildingsGroup.setText(getString("BuildingsGroup.Label"));
		helper.setLook(buildingsGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		buildingsGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		buildingsGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeBuildings = helper.addCheckBox(buildingsGroup, last, "ExteriorOutputTab.IncludeBuildings");
		ckIncludeBuildings.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		buildingsMoreLess = helper.addLabel(buildingsGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = buildingsMoreLess.getText();
				if (tx == getString("More")) {
					//expand(1);
					buildingsMoreLess.setText(getString("Less"));
				} else {
					//collapse(1);
					buildingsMoreLess.setText(getString("More"));
				}
			}
		};

		buildingsMoreLess.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = buildingsMoreLess.getText();
				if (tx == getString("More")) {
					expand(3);
					buildingsMoreLess.setText(getString("Less"));
				} else {
					collapse(3);
					buildingsMoreLess.setText(getString("More"));
				}
			}
		});

		buildingsMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		buildingsMoreLess.setText(getString("Less"));

		last = top = helper.addSpacer(buildingsGroup, last);
		last = wBuildingsCount  = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.BuildingsCount");
		last = wBathHouseArea  = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.BathHouseArea");
		last = wBathHouseFlag  = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.BathHouseFlag");
		last = wBoatAccessFlag  = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.BoatAccessFlag");
		last = wBoatHouseArea  = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.BoatHouseArea");
		last = wBoatHouseFlag  = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.BoatHouseFlag");
		last =  wCabinArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.CabinArea");
		last =  wCabinFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.CabinFlag");
		last =  wCanopyArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.CanopyArea");
		last =  wCanopyFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.CanopyFlag");
		last =  wGazeboArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GazeboArea");
		last =  wGazeboFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GazeboFlag");
		last =  wGranaryArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GranaryArea");
		last =  wGranaryFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GranaryFlag");
		last =  wGreenHouseArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GreenHouseArea");
		last =  wGreenHouseFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GreenHouseFlag");
		last =  wGuestHouseArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GuestHouseArea");
		last =  wGuestHouseFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.GuestHouseFlag");
		last =  wKennelArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.KennelArea");
		last =  wKennelFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.KennelFlag");
		last =  wLeanToArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.LeanToArea");
		last =  wLeanToFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.LeanToFlag");
		last =  wOutdoorKitchenFireplaceFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.OutdoorKitchenFireplaceFlag");


		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last =  wLoadingPlatformArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.LoadingPlatformArea");
		last =  wLoadingPlatformFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.LoadingPlatformFlag");
		last =  wMilkHouseArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.MilkHouseArea");
		last =  wMilkHouseFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.MilkHouseFlag");
		last =  wPoolHouseArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.PoolHouseArea");
		last =  wPoolHouseFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.PoolHouseFlag");
		last =  wPoultryHouseArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.PoultryHouseArea");
		last =  wPoultryHouseFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.PoultryHouseFlag");
		last =  wQuonsetArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.QuonsetArea");
		last =  wQuonsetFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.QuonsetFlag");
		last =  wShedArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.ShedArea");
		last =  wShedCode = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.ShedCode");
		last =  wSiloArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.SiloArea");
		last =  wSiloFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.SiloFlag");
		last =  wStableArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.StableArea");
		last =  wStableFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.StableFlag");
		last =  wStorageBuildingArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.StorageBuildingArea");
		last =  wStorageBuildingFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.StorageBuildingFlag");
		last =  wUtilityBuildingArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.UtilityBuildingArea");
		last =  wUtilityBuildingFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.UtilityBuildingFlag");
		last =  wPoleStructureArea = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.PoleStructureArea");
		last =  wPoleStructureFlag = helper.addTextBox(buildingsGroup, last, "ExteriorOutputTab.PoleStructureFlag");


	}

	private void expand(int group) {

		FormData extStructFormData = (FormData) extStructGroup.getLayoutData();
		FormData amenitiesFormData  = (FormData) amenitiesGroup.getLayoutData();
		FormData buildingsFormData  = (FormData) buildingsGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Address expand
			extStructFormData.top = new FormAttachment(description, helper.margin);
			extStructFormData.bottom = null;
			wComp.layout();
			offset = (extStructGroup.getBounds().height + (helper.margin * 2));
			for (Control child : extStructGroup.getChildren()) {
				child.setVisible(true);
			}

			amenitiesFormData.top = new FormAttachment(description, offset);
			if (amenitiesMoreLess.getText().equals("less")) {
				amenitiesFormData.bottom = null;
			} else {
				offset = (extStructGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				amenitiesFormData.bottom = new FormAttachment(description, offset);
			}
		}


		if (group == 2) {
			// Amenities
			amenitiesFormData.top = new FormAttachment(extStructGroup, helper.margin);
			amenitiesFormData.bottom = null;
			for (Control child : amenitiesGroup.getChildren()) {
				child.setVisible(true);
			}
			wComp.layout();
			offset = (amenitiesGroup.getBounds().height + (helper.margin * 2));
			buildingsFormData.top = new FormAttachment(amenitiesGroup, (helper.margin * 2));
			if (buildingsMoreLess.getText().equals("less")) {
				buildingsFormData.bottom = null;
			} else {
				offset = (amenitiesGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				buildingsFormData.bottom = new FormAttachment(amenitiesGroup, offset);
			}
		}


		if (group == 3) {
			//buildings
			buildingsFormData.top = new FormAttachment(amenitiesGroup, helper.margin * 2);
			buildingsFormData.bottom = null;
			for (Control child : buildingsGroup.getChildren()) {
				child.setVisible(true);
			}
		}

		enable();
	}

	private void collapse(int group) {

		FormData intStructFormData = (FormData) extStructGroup.getLayoutData();
		FormData amenitiesXFormData  = (FormData) amenitiesGroup.getLayoutData();
		FormData buildingsFormData  = (FormData) buildingsGroup.getLayoutData();



		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			// Exterior Structure
			intStructFormData.top = new FormAttachment(description, helper.margin);
			intStructFormData.bottom = new FormAttachment(description, collapsedHeight);

			for (Control child : extStructGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			amenitiesXFormData.top = new FormAttachment(extStructGroup, helper.margin);
			if (amenitiesMoreLess.getText().equals("less")) {
				amenitiesXFormData.bottom = null;
			} else {
				offset = (extStructGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				amenitiesXFormData.bottom = new FormAttachment(extStructGroup, offset);
			}
		}

		if (group == 2) {

			amenitiesXFormData.top = new FormAttachment(description, extStructGroup.getBounds().height + (helper.margin * 2));
			amenitiesXFormData.bottom = new FormAttachment(description, extStructGroup.getBounds().height + (helper.margin * 2) + 80);
			for (Control child : amenitiesGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			buildingsFormData.top = new FormAttachment(amenitiesGroup, helper.margin);
			if (buildingsMoreLess.getText().equals("less")) {
				buildingsFormData.bottom = null;
			} else {
				buildingsFormData.bottom = new FormAttachment(amenitiesGroup, amenitiesGroup.getBounds().height + 80);
			}
		}

		if (group == 3) {
			wComp.layout();
			buildingsFormData.top = new FormAttachment(amenitiesGroup, helper.margin * 2);
			buildingsFormData.bottom = new FormAttachment(amenitiesGroup, amenitiesGroup.getBounds().height + 80);
			for (Control child : buildingsGroup.getChildren()) {
				child.setVisible(false);
			}
		}

		enable();
	}

	@Override
	public boolean init(BaseStepMeta baseStepMeta) {
		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) baseStepMeta;

		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;
		// include
		ckIncludeExtStruct.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_EXT_STRUCT_INFO).metaValue));
		ckIncludeAmenities.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_EXT_AMENITIES).metaValue));
		ckIncludeBuildings.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_EXT_BUILDINGS).metaValue));

		// exterior structure info
		wStructureStyle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STRUCTURE_STYLE).metaValue);
		wExterior1Code.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_EXTERIOR_1_CODE).metaValue);
		wRoofMaterial.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOF_MATERIAL).metaValue);
		wRoofConstruction.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOF_CONSTRUCTION).metaValue);
		wStormShutterFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORM_SHUTTER_FLAG).metaValue);
		wOverheadDoorFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OVERHEAD_DOOR_FLAG).metaValue);

		// exterion amenities
		wViewDescription.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_VIEW_DESCRIPTION).metaValue);
		wPorchCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PORCH_CODE).metaValue);
		wPorchArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PORCH_AREA).metaValue);
		wPatioArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PATIO_AREA).metaValue);
		wDeckFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DECK_FLAG).metaValue);
		wDeckArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DECK_AREA).metaValue);
		wFeatureBalconyFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BALCONY_FLAG).metaValue);
		wBalconyArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BALCONY_AREA).metaValue);
		wBreezewayFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BREEZEWAY_FLAG).metaValue);

		// exterior buildings
		wBuildingsCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BUILDINGS_COUNT).metaValue);
		wBathHouseArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_AREA).metaValue);
		wBathHouseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_FLAG).metaValue);
		wBoatAccessFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_ACCESS_FLAG).metaValue);
		wBoatHouseArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_AREA).metaValue);
		wBoatHouseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_FLAG).metaValue);
		wCabinArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CABIN_AREA).metaValue);
		wCabinFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CABIN_FLAG).metaValue);
		wCanopyArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CANOPY_AREA).metaValue);
		wCanopyFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CANOPY_FLAG).metaValue);
		wGazeboArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_AREA).metaValue);
		wGazeboFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_FLAG).metaValue);
		wGranaryArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_AREA).metaValue);
		wGranaryFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_FLAG).metaValue);
		wGreenHouseArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_AREA).metaValue);
		wGreenHouseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_FLAG).metaValue);
		wGuestHouseArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_AREA).metaValue);
		wGuestHouseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_FLAG).metaValue);
		wKennelArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_KENNEL_AREA).metaValue);
		wKennelFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_KENNEL_FLAG).metaValue);
		wLeanToArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_AREA).metaValue);
		wLeanToFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_FLAG).metaValue);
		wLoadingPlatformArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_AREA).metaValue);
		wLoadingPlatformFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_FLAG).metaValue);
		wMilkHouseArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_AREA).metaValue);
		wMilkHouseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_FLAG).metaValue);
		wOutdoorKitchenFireplaceFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OUTDOOR_KITCHEN_FIREPLACE_FLAG).metaValue);
		wPoolHouseArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_HOUSE_AREA).metaValue);
		wPoolHouseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_HOUSE_FLAG).metaValue);
		wPoultryHouseArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_AREA).metaValue);
		wPoultryHouseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_FLAG).metaValue);
		wQuonsetArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUONSET_AREA).metaValue);
		wQuonsetFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUONSET_FLAG).metaValue);
		wShedArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SHED_AREA).metaValue);
		wShedCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SHED_CODE).metaValue);
		wSiloArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SILO_AREA).metaValue);
		wSiloFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SILO_FLAG).metaValue);
		wStableArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STABLE_AREA).metaValue);
		wStableFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STABLE_FLAG).metaValue);
		wStorageBuildingArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_AREA).metaValue);
		wStorageBuildingFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_FLAG).metaValue);
		wUtilityBuildingArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_AREA).metaValue);
		wUtilityBuildingFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_FLAG).metaValue);
		wPoleStructureArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_AREA).metaValue);
		wPoleStructureFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_FLAG).metaValue);




		return true;
	}

	@Override
	public void advancedConfigChanged() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void getData(BaseStepMeta baseStepMeta) {
		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) baseStepMeta;

		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;
		// OPTIONS
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_EXT_STRUCT_INFO).metaValue = String.valueOf(ckIncludeExtStruct.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_EXT_AMENITIES).metaValue = String.valueOf(ckIncludeAmenities.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_EXT_BUILDINGS).metaValue = String.valueOf(ckIncludeBuildings.getSelection());

		// EXT STRUCTURE INFO
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STRUCTURE_STYLE).metaValue	= wStructureStyle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_EXTERIOR_1_CODE).metaValue	= wExterior1Code.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOF_MATERIAL).metaValue	= wRoofMaterial.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOF_CONSTRUCTION).metaValue	= wRoofConstruction.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORM_SHUTTER_FLAG).metaValue	= wStormShutterFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OVERHEAD_DOOR_FLAG).metaValue	= wOverheadDoorFlag.getText();


		// ext amenities
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_VIEW_DESCRIPTION).metaValue	= wViewDescription.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PORCH_CODE).metaValue	= wPorchCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PORCH_AREA).metaValue	= wPorchArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PATIO_AREA).metaValue	= wPatioArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DECK_FLAG).metaValue	= wDeckFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DECK_AREA).metaValue	= wDeckArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BALCONY_FLAG).metaValue	= wFeatureBalconyFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BALCONY_AREA).metaValue	= wBalconyArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BREEZEWAY_FLAG).metaValue	= wBreezewayFlag.getText();


		//  buildings
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BUILDINGS_COUNT).metaValue	= wBuildingsCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_AREA).metaValue	= wBathHouseArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_HOUSE_FLAG).metaValue	= wBathHouseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_ACCESS_FLAG).metaValue	= wBoatAccessFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_AREA).metaValue	= wBoatHouseArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BOAT_HOUSE_FLAG).metaValue	= wBoatHouseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CABIN_AREA).metaValue	= wCabinArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CABIN_FLAG).metaValue	= wCabinFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CANOPY_AREA).metaValue	= wCanopyArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CANOPY_FLAG).metaValue	= wCanopyFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_AREA).metaValue	= wGazeboArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAZEBO_FLAG).metaValue	= wGazeboFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_AREA).metaValue	= wGranaryArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GRAINERY_FLAG).metaValue	= wGranaryFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_AREA).metaValue	= wGreenHouseArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREEN_HOUSE_FLAG).metaValue	= wGreenHouseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_AREA).metaValue	= wGuestHouseArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GUEST_HOUSE_FLAG).metaValue	= wGuestHouseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_KENNEL_AREA).metaValue	= wKennelArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_KENNEL_FLAG).metaValue	= wKennelFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_AREA).metaValue	= wLeanToArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEAN_TO_FLAG).metaValue	= wLeanToFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_AREA).metaValue	= wLoadingPlatformArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOADING_PLATFORM_FLAG).metaValue	= wLoadingPlatformFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_AREA).metaValue	= wMilkHouseArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MILK_HOUSE_FLAG).metaValue	= wMilkHouseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OUTDOOR_KITCHEN_FIREPLACE_FLAG).metaValue	= wOutdoorKitchenFireplaceFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_HOUSE_AREA).metaValue	= wPoolHouseArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POOL_HOUSE_FLAG).metaValue	= wPoolHouseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_AREA).metaValue	= wPoultryHouseArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POULTRY_HOUSE_FLAG).metaValue	= wPoultryHouseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUONSET_AREA).metaValue	= wQuonsetArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUONSET_FLAG).metaValue	= wQuonsetFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SHED_AREA).metaValue	= wShedArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SHED_CODE).metaValue	= wShedCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SILO_AREA).metaValue	= wSiloArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SILO_FLAG).metaValue	= wSiloFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STABLE_AREA).metaValue	= wStableArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STABLE_FLAG).metaValue	= wStableFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_AREA).metaValue	= wStorageBuildingArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORAGE_BUILDING_FLAG).metaValue	= wStorageBuildingFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_AREA).metaValue	= wUtilityBuildingArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_BUILDING_FLAG).metaValue	= wUtilityBuildingFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_AREA).metaValue	= wPoleStructureArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POLE_STRUCTURE_FLAG).metaValue	= wPoleStructureFlag.getText();



	}

	@Override
	public String getHelpURLKey() {

		return null;
	}

	private void enable() {

		// Exterior Structure
		if (ckIncludeExtStruct.getSelection()) {
			for (Control child : extStructGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : extStructGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		// Amenities
		if (ckIncludeAmenities.getSelection()) {
			for (Control child : amenitiesGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : amenitiesGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//Buildings
		if (ckIncludeBuildings.getSelection()) {
			for (Control child : buildingsGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : buildingsGroup.getChildren()) {
				child.setEnabled(false);
			}
		}



		ckIncludeExtStruct.setEnabled(true);
		ckIncludeExtStruct.setVisible(true);
		ckIncludeAmenities.setEnabled(true);
		ckIncludeAmenities.setVisible(true);
		ckIncludeBuildings.setEnabled(true);
		ckIncludeBuildings.setVisible(true);


		extStructMoreLess.setVisible(true);
		extStructMoreLess.setEnabled(true);
		amenitiesMoreLess.setVisible(true);
		amenitiesMoreLess.setEnabled(true);
		buildingsMoreLess.setVisible(true);
		buildingsMoreLess.setEnabled(true);

		wComp.layout();

	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.ExteriorOutputTab." + key, args);
	}
}
