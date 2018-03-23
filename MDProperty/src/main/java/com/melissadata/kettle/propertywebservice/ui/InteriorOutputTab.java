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
public class InteriorOutputTab implements MDTab {

	private static Class<?> PKG = MDPropertyWebServiceMeta.class;
	private Label                      description;
	private MDPropertyWebServiceHelper helper;
	private MDPropertyWebServiceDialog dialog;
	private Composite                  wComp;
	private Group                      intStructGroup;
	private Group                      roomInfoGroup;
	private Group                      amenitiesGroup;
	//	Interior Structure
	private Button                     ckIncludeIntStruct;
	private Label                      intStructMoreLess;
	private Text                       wFoundation;
	private Text                       wConstruction;
	private Text                       wInteriorStructure;
	private Text                       wPlumbingFixtureCount;
	private Text                       wFireResistanceClass;
	private Text                       wFireSprinklers;
	private Text                       wFlooringMaterial;
	// Interior Room Info
	private Button                     ckIncludeRoomInfo;
	private Label                      roomInfoMoreLess;
	private Text                       wBathCount;
	private Text                       wBathPartialCount;
	private Text                       wBedroomsCount;
	private Text                       wRoomsCount;
	private Text                       wStoriesCount;
	private Text                       wUnitsCount;
	private Text                       wBonusRoomFlag;
	private Text                       wBreakfastNookFlag;
	private Text                       wCellarFlag;
	private Text                       wCellarWineFlag;
	private Text                       wExcerciseFlag;
	private Text                       wFamilyCode;
	private Text                       wGameFlag;
	private Text                       wGreatFlag;
	private Text                       wHobbyFlag;
	private Text                       wLaundryFlag;
	private Text                       wMediaFlag;
	private Text                       wMudFlag;
	private Text                       wOfficeArea;
	private Text                       wOfficeFlag;
	private Text                       wSafeRoomFlag;
	private Text                       wSittingFlag;
	private Text                       wStormFlag;
	private Text                       wStudyFlag;
	private Text                       wSunRoomFlag;
	private Text                       wUtilityArea;
	private Text                       wUtilityCode;
	// Interior Amenities
	private Button                     ckIncludeAmenities;
	private Label                      amenitiesMoreLess;
	private Text                       wFireplace;
	private Text                       wFireplaceCount;
	private Text                       wAccessibilityElevatorFlag;
	private Text                       wAccessibilityHandicapFlag;
	private Text                       wEscalatorFlag;
	private Text                       wCentralVacuumFlag;
	private Text                       wIntercomFlag;
	private Text                       wSoundSystemFlag;
	private Text                       wWetBarFlag;
	private Text                       wSecurityAlarmFlag;

	public InteriorOutputTab(MDPropertyWebServiceDialog dialog) {

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
		createInteriorStructureGroup(wComp, description);
		createRoomInfoGroup(wComp, intStructGroup);
		createAmenitiesGroup(wComp, roomInfoGroup);

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

	private void createInteriorStructureGroup(Composite parent, Control last) {

		intStructGroup = new Group(parent, SWT.NONE);
		intStructGroup.setText(getString("InteriorStructGroup.Label"));
		helper.setLook(intStructGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		intStructGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		intStructGroup.setLayoutData(fd);

		// set the column widths for proper placing
		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeIntStruct = helper.addCheckBox(intStructGroup, last, "InteriorOutputTab.IncludeInteriorStructure");
		ckIncludeIntStruct.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		intStructMoreLess = helper.addLabel(intStructGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = intStructMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(1);
					intStructMoreLess.setText(getString("Less"));
				} else {
					collapse(1);
					intStructMoreLess.setText(getString("More"));
				}
			}
		};

		intStructMoreLess.addListener(SWT.MouseDown, expandCollapse);

		intStructMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		intStructMoreLess.setText(getString("Less"));

		last = top = helper.addSpacer(intStructGroup, last);
		last = wFoundation = helper.addTextBox(intStructGroup, last, "InteriorOutputTab.Foundation");
		last = wConstruction = helper.addTextBox(intStructGroup, last, "InteriorOutputTab.Construction");
		last = wInteriorStructure = helper.addTextBox(intStructGroup, last, "InteriorOutputTab.InteriorStructure");
		last = wPlumbingFixtureCount = helper.addTextBox(intStructGroup, last, "InteriorOutputTab.PlumbingFixturesCount");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;

		last = wFireResistanceClass = helper.addTextBox(intStructGroup, last, "InteriorOutputTab.ConstructionFireResistanceClass");
		last = wFireSprinklers = helper.addTextBox(intStructGroup, last, "InteriorOutputTab.SafetyFireSprinklersFlag");
		last = wFlooringMaterial = helper.addTextBox(intStructGroup, last, "InteriorOutputTab.FlooringMaterialPrimary");
	}

	private void createRoomInfoGroup(Composite parent, Control last) {

		roomInfoGroup = new Group(parent, SWT.NONE);
		roomInfoGroup.setText(getString("InteriorRoomInfoGroup.Label"));
		helper.setLook(roomInfoGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		roomInfoGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		roomInfoGroup.setLayoutData(fd);

		// set the column widths for proper placing
		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeRoomInfo = helper.addCheckBox(roomInfoGroup, last, "InteriorOutputTab.IncludeInteriorRoomInfo");
		ckIncludeRoomInfo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		roomInfoMoreLess = helper.addLabel(roomInfoGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = roomInfoMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(2);
					roomInfoMoreLess.setText(getString("Less"));
				} else {
					collapse(2);
					roomInfoMoreLess.setText(getString("More"));
				}
			}
		};

		roomInfoMoreLess.addListener(SWT.MouseDown, expandCollapse);

		roomInfoMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		roomInfoMoreLess.setText(getString("Less"));

		last = top = helper.addSpacer(roomInfoGroup, last);
		last = wBathCount = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.BathCount");
		last = wBathPartialCount = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.BathPartialCount");
		last = wBedroomsCount = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.BedroomsCount");
		last = wRoomsCount = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.RoomsCount");
		last = wStoriesCount = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.StoriesCount");
		last = wUnitsCount = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.UnitsCount");
		last = wBonusRoomFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.BonusRoomFlag");
		last = wBreakfastNookFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.BreakfastNookFlag");
		last = wCellarFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.CellarFlag");
		last = wCellarWineFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.CellarWineFlag");
		last = wExcerciseFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.ExcerciseFlag");
		last = wFamilyCode = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.FamilyCode");
		last = wGameFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.GameFlag");
		last = wGreatFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.GreatFlag");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;

		last = wHobbyFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.HobbyFlag");
		last = wLaundryFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.LaundryFlag");
		last = wMediaFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.MediaFlag");
		last = wMudFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.MudFlag");
		last = wOfficeArea = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.OfficeArea");
		last = wOfficeFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.OfficeFlag");
		last = wSafeRoomFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.SafeRoomFlag");
		last = wSittingFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.SittingFlag");
		last = wStormFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.StormFlag");
		last = wStudyFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.StudyFlag");
		last = wSunRoomFlag = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.SunRoomFlag");
		last = wUtilityArea = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.UtilityArea");
		last = wUtilityCode = helper.addTextBox(roomInfoGroup, last, "InteriorOutputTab.UtilityCode");
	}

	private void createAmenitiesGroup(Composite parent, Control last) {

		amenitiesGroup = new Group(parent, SWT.NONE);
		amenitiesGroup.setText(getString("InteriorAmenitiesGroup.Label"));
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

		last = ckIncludeAmenities = helper.addCheckBox(amenitiesGroup, last, "InteriorOutputTab.IncludeInteriorInteriorAmenities");
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
				if (tx.equals(getString("More"))) {
					expand(3);
					amenitiesMoreLess.setText(getString("Less"));
				} else {
					collapse(3);
					amenitiesMoreLess.setText(getString("More"));
				}
			}
		};

		amenitiesMoreLess.addListener(SWT.MouseDown, expandCollapse);

		amenitiesMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		amenitiesMoreLess.setText(getString("Less"));

		last = top = helper.addSpacer(amenitiesGroup, last);
		last = wFireplace = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.Fireplace");
		last = wFireplaceCount = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.FireplaceCount");
		last = wAccessibilityElevatorFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.AccessibilityElevatorFlag");
		last = wAccessibilityHandicapFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.AccessibilityHandicapFlag");
		last = wEscalatorFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.EscalatorFlag");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;

		last = wCentralVacuumFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.CentralVacuumFlag");
		last = wIntercomFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.IntercomFlag");
		last = wSoundSystemFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.SoundSystemFlag");
		last = wWetBarFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.WetBarFlag");
		last = wSecurityAlarmFlag = helper.addTextBox(amenitiesGroup, last, "InteriorOutputTab.SecurityAlarmFlag");
	}

	private void expand(int group) {

		FormData intStructFormData = (FormData) intStructGroup.getLayoutData();
		FormData roomInfoFormData  = (FormData) roomInfoGroup.getLayoutData();
		FormData amenitiesFormData = (FormData) amenitiesGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Address expand
			intStructFormData.top = new FormAttachment(description, helper.margin);
			intStructFormData.bottom = null;
			wComp.layout();
			offset = (intStructGroup.getBounds().height + (helper.margin * 2));
			for (Control child : intStructGroup.getChildren()) {
				child.setVisible(true);
			}

			roomInfoFormData.top = new FormAttachment(description, offset);
			if (roomInfoMoreLess.getText().equals("less")) {
				roomInfoFormData.bottom = null;
			} else {
				offset = (intStructGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				roomInfoFormData.bottom = new FormAttachment(description, offset);
			}
		}

		if (group == 2) {
			// Parsed expand
			roomInfoFormData.top = new FormAttachment(intStructGroup, helper.margin);
			roomInfoFormData.bottom = null;
			for (Control child : roomInfoGroup.getChildren()) {
				child.setVisible(true);
			}
			wComp.layout();
			offset = (roomInfoGroup.getBounds().height + (helper.margin * 2));
			amenitiesFormData.top = new FormAttachment(roomInfoGroup, (helper.margin * 2));
			if (amenitiesMoreLess.getText().equals("less")) {
				amenitiesFormData.bottom = null;
			} else {
				offset = (roomInfoGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				amenitiesFormData.bottom = new FormAttachment(roomInfoGroup, offset);
			}
		}

		if (group == 3) {
			//legal expand
			amenitiesFormData.top = new FormAttachment(roomInfoGroup, helper.margin * 2);
			amenitiesFormData.bottom = null;
			for (Control child : amenitiesGroup.getChildren()) {
				child.setVisible(true);
			}
		}

		enable();
	}

	private void collapse(int group) {

		FormData intStructFormData = (FormData) intStructGroup.getLayoutData();
		FormData roomInfoFormData  = (FormData) roomInfoGroup.getLayoutData();
		FormData amenitiesFormData = (FormData) amenitiesGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			intStructFormData.top = new FormAttachment(description, helper.margin);
			intStructFormData.bottom = new FormAttachment(description, collapsedHeight);

			for (Control child : intStructGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			roomInfoFormData.top = new FormAttachment(intStructGroup, helper.margin);
			if (roomInfoMoreLess.getText().equals("less")) {
				roomInfoFormData.bottom = null;
			} else {
				offset = (intStructGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				roomInfoFormData.bottom = new FormAttachment(intStructGroup, offset);
			}
		}

		if (group == 2) {

			roomInfoFormData.top = new FormAttachment(description, intStructGroup.getBounds().height + (helper.margin * 2));
			roomInfoFormData.bottom = new FormAttachment(description, intStructGroup.getBounds().height + (helper.margin * 2) + 80);
			for (Control child : roomInfoGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			amenitiesFormData.top = new FormAttachment(roomInfoGroup, helper.margin);
			if (amenitiesMoreLess.getText().equals("less")) {
				amenitiesFormData.bottom = null;
			} else {
				amenitiesFormData.bottom = new FormAttachment(roomInfoGroup, roomInfoGroup.getBounds().height + 80);
			}
		}

		if (group == 3) {
			wComp.layout();
			amenitiesFormData.top = new FormAttachment(roomInfoGroup, helper.margin * 2);
			amenitiesFormData.bottom = new FormAttachment(roomInfoGroup, roomInfoGroup.getBounds().height + 80);
			for (Control child : amenitiesGroup.getChildren()) {
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
		ckIncludeIntStruct.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_INT_STRUCT_INFO).metaValue));
		ckIncludeRoomInfo.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_INT_ROOM_INFO).metaValue));
		ckIncludeAmenities.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_INT_AMENITIES).metaValue));

		// interior struct info
		wFoundation.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FOUNDATION).metaValue);
		wConstruction.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCTION).metaValue);
		wInteriorStructure.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_INTERIOR_STRUCTURE).metaValue);
		wPlumbingFixtureCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PLUMBING_FIXTURE).metaValue);
		wFireResistanceClass.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCT_FIRE_RESISTANCE_CLASS).metaValue);
		wFireSprinklers.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAFETY_FIRE_SPRINKLER_FLAG).metaValue);
		wFlooringMaterial.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FLOORING_MATERIAL_PRIMARY).metaValue);

		//interior room info
		wBathCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_COUNT).metaValue);
		wBathPartialCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_PARTIAL_COUNT).metaValue);
		wBedroomsCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BEDROOMS_COUNT).metaValue);
		wRoomsCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOMS_COUNT).metaValue);
		wStoriesCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORIES_COUNT).metaValue);
		wUnitsCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNITS_COUNT).metaValue);
		wBonusRoomFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BONUS_ROOM_FLAG).metaValue);
		wBreakfastNookFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BREAKFAST_NOOK_FLAG).metaValue);
		wCellarFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CELLAR_FLAG).metaValue);
		wCellarWineFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WINE_CELLAR_FLAG).metaValue);
		wExcerciseFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_EXERCISE_ROOM_FLAG).metaValue);
		wFamilyCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FAMILY_ROOM_FLAG).metaValue);
		wGameFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAME_ROOM_FLAG).metaValue);
		wGreatFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREAT_ROOM_FLAG).metaValue);
		wHobbyFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HOBBY_ROOM_FLAG).metaValue);
		wLaundryFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAUNDRY_ROOM_FLAG).metaValue);
		wMediaFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MEDIA_ROOM_FLAG).metaValue);
		wMudFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MUD_ROOM_FLAG).metaValue);
		wOfficeArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OFFICE_AREA).metaValue);
		wOfficeFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OFFICE_ROOM_FLAG).metaValue);
		wSafeRoomFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAFE_ROOM_FLAG).metaValue);
		wSittingFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SITTING_ROOM_FLAG).metaValue);
		wStormFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORM_SHELTER).metaValue);
		wStudyFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STUDY_ROOM_FLAG).metaValue);
		wSunRoomFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUN_ROOM_FLAG).metaValue);
		wUtilityArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_AREA).metaValue);
		wUtilityCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_CODE).metaValue);

		//interior amenities
		wFireplace.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE).metaValue);
		wFireplaceCount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE_COUNT).metaValue);
		wAccessibilityElevatorFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ELEVATOR_FLAG).metaValue);
		wAccessibilityHandicapFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HANDICAP_FLAG).metaValue);
		wEscalatorFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESCALATOR_FLAG).metaValue);
		wCentralVacuumFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CENTRAL_VACUUM_FLAG).metaValue);
		wIntercomFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_INTERCOM_FLAG).metaValue);
		wSoundSystemFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SOUND_SYSTEM_FLAG).metaValue);
		wWetBarFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WET_BAR_FLAG).metaValue);
		wSecurityAlarmFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECURITY_ALARM_FLAG).metaValue);

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

		// options
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_INT_STRUCT_INFO).metaValue = String.valueOf(ckIncludeIntStruct.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_INT_ROOM_INFO).metaValue = String.valueOf(ckIncludeRoomInfo.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_INT_AMENITIES).metaValue = String.valueOf(ckIncludeAmenities.getSelection());

		// int structure info
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FOUNDATION).metaValue = wFoundation.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCTION).metaValue = wConstruction.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_INTERIOR_STRUCTURE).metaValue = wInteriorStructure.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PLUMBING_FIXTURE).metaValue = wPlumbingFixtureCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONSTRUCT_FIRE_RESISTANCE_CLASS).metaValue = wFireResistanceClass.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAFETY_FIRE_SPRINKLER_FLAG).metaValue = wFireSprinklers.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FLOORING_MATERIAL_PRIMARY).metaValue = wFlooringMaterial.getText();

		// room info
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_COUNT).metaValue = wBathCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BATH_PARTIAL_COUNT).metaValue = wBathPartialCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BEDROOMS_COUNT).metaValue = wBedroomsCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ROOMS_COUNT).metaValue = wRoomsCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORIES_COUNT).metaValue = wStoriesCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNITS_COUNT).metaValue = wUnitsCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BONUS_ROOM_FLAG).metaValue = wBonusRoomFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BREAKFAST_NOOK_FLAG).metaValue = wBreakfastNookFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CELLAR_FLAG).metaValue = wCellarFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WINE_CELLAR_FLAG).metaValue = wCellarWineFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_EXERCISE_ROOM_FLAG).metaValue = wExcerciseFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FAMILY_ROOM_FLAG).metaValue = wFamilyCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GAME_ROOM_FLAG).metaValue = wGameFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_GREAT_ROOM_FLAG).metaValue = wGreatFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HOBBY_ROOM_FLAG).metaValue = wHobbyFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAUNDRY_ROOM_FLAG).metaValue = wLaundryFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MEDIA_ROOM_FLAG).metaValue = wMediaFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MUD_ROOM_FLAG).metaValue = wMudFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OFFICE_AREA).metaValue = wOfficeArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OFFICE_ROOM_FLAG).metaValue = wOfficeFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SAFE_ROOM_FLAG).metaValue = wSafeRoomFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SITTING_ROOM_FLAG).metaValue = wSittingFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STORM_SHELTER).metaValue = wStormFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_STUDY_ROOM_FLAG).metaValue = wStudyFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUN_ROOM_FLAG).metaValue = wSunRoomFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_AREA).metaValue = wUtilityArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UTILITY_ROOM_CODE).metaValue = wUtilityCode.getText();

		// int amenities
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE).metaValue = wFireplace.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIREPLACE_COUNT).metaValue = wFireplaceCount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ELEVATOR_FLAG).metaValue = wAccessibilityElevatorFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_HANDICAP_FLAG).metaValue = wAccessibilityHandicapFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESCALATOR_FLAG).metaValue = wEscalatorFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CENTRAL_VACUUM_FLAG).metaValue = wCentralVacuumFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_INTERCOM_FLAG).metaValue = wIntercomFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SOUND_SYSTEM_FLAG).metaValue = wSoundSystemFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_WET_BAR_FLAG).metaValue = wWetBarFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECURITY_ALARM_FLAG).metaValue = wSecurityAlarmFlag.getText();
	}

	@Override
	public String getHelpURLKey() {

		return null;
	}

	private void enable() {

		// Interior Structure
		if (ckIncludeIntStruct.getSelection()) {
			for (Control child : intStructGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : intStructGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		// Room Info
		if (ckIncludeRoomInfo.getSelection()) {
			for (Control child : roomInfoGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : roomInfoGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//Amenities
		if (ckIncludeAmenities.getSelection()) {
			for (Control child : amenitiesGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : amenitiesGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		ckIncludeIntStruct.setEnabled(true);
		ckIncludeIntStruct.setVisible(true);
		ckIncludeRoomInfo.setEnabled(true);
		ckIncludeRoomInfo.setVisible(true);
		ckIncludeAmenities.setEnabled(true);
		ckIncludeAmenities.setVisible(true);

		intStructMoreLess.setVisible(true);
		intStructMoreLess.setEnabled(true);
		roomInfoMoreLess.setVisible(true);
		roomInfoMoreLess.setEnabled(true);
		amenitiesMoreLess.setVisible(true);
		amenitiesMoreLess.setEnabled(true);

		wComp.layout();
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.InteriorOutputTab." + key, args);
	}
}
