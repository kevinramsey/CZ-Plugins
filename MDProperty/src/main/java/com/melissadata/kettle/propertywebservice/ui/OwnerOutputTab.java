package com.melissadata.kettle.propertywebservice.ui;

import java.util.HashMap;

import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
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
import com.melissadata.kettle.propertywebservice.support.MDPropertyWebServiceHelper;

public class OwnerOutputTab implements MDTab {

	private static Class<?> PKG = MDPropertyWebServiceMeta.class;
	private Label                      description;
	private MDPropertyWebServiceHelper helper;
	private Composite wComp;
	private Group                      primaryOwnerGroup;
	private Group                      ownerAddressGroup;
	private Group                      secondaryOwnerGroup;
	private Group                      lastDeedOwnerGroup;
	//	Primary Owner
	private Button                     ckIncludePrimaryOwner;
	private Label                      primaryOwnerMoreLess;
	private Text                       wName1Full;
	private Text                       wName1First;
	private Text                       wName1Middle;
	private Text                       wName1Last;
	private Text                       wName1Suffix;
	private Text                       wTrustFlag;
	private Text                       wCompanyFlag;
	private Text                       wName2Full;
	private Text                       wName2First;
	private Text                       wName2Middle;
	private Text                       wName2Last;
	private Text                       wName2Suffix;
	private Text                       wPrimaryType;
	private Text                       wVestingType;
	//	Owner Address
	private Button                     ckIncludeOwnerAddress;
	private Label                      ownerAddressMoreLess;
	private Text                       wAddress;
	private Text                       wCity;
	private Text                       wState;
	private Text                       wZip;
	private Text                       wCarrierRoute;
	private Text                       wMAK;
	private Text                       wBaseMAK;
	// Secondary Owner
	private Button                     ckIncludeSecondaryOwner;
	private Label                      secondaryOwnerMoreLess;
	private Text                       wName3Full;
	private Text                       wName3First;
	private Text                       wName3Middle;
	private Text                       wName3Last;
	private Text                       wName3Suffix;
	private Text                       wName4Full;
	private Text                       wName4First;
	private Text                       wName4Middle;
	private Text                       wName4Last;
	private Text                       wName4Suffix;
	private Text                       wSecondaryType;
	// Last Deed Owner
	private Button                     ckIncludeLastDeedOwner;
	private Label                      lastDeedOwnerMoreLess;
	private Text                       wLDOName1Full;
	private Text                       wLDOName1First;
	private Text                       wLDOName1Middle;
	private Text                       wLDOName1Last;
	private Text                       wLDOName1Suffix;
	private Text                       wLDOName2Full;
	private Text                       wLDOName2First;
	private Text                       wLDOName2Middle;
	private Text                       wLDOName2Last;
	private Text                       wLDOName2Suffix;
	private Text                       wLDOName3Full;
	private Text                       wLDOName3First;
	private Text                       wLDOName3Middle;
	private Text                       wLDOName3Last;
	private Text                       wLDOName3Suffix;
	private Text                       wLDOName4Full;
	private Text                       wLDOName4First;
	private Text                       wLDOName4Middle;
	private Text                       wLDOName4Last;
	private Text                       wLDOName4Suffix;
	private MDPropertyWebServiceDialog dialog;

	public OwnerOutputTab(MDPropertyWebServiceDialog dialog) {

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

		createPrimaryOwnerGroup(wComp, description);
		createOwnerAddressGroup(wComp, primaryOwnerGroup);
		createSecondaryOwnerGroup(wComp,ownerAddressGroup);
		createLastDeedOwnerGroup(wComp, secondaryOwnerGroup);

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

		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PRIMARY_OWNER).metaValue = String.valueOf(ckIncludePrimaryOwner.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_OWNER_ADDRESS).metaValue = String.valueOf(ckIncludeOwnerAddress.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_SECONDARY_OWNER).metaValue = String.valueOf(ckIncludeSecondaryOwner.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_LAST_DEED_OWNER).metaValue = String.valueOf(ckIncludeLastDeedOwner.getSelection());

//
////		Primary OWNER
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FULL).metaValue	= wName1Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FIRST).metaValue	= wName1First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_MIDDLE).metaValue	= wName1Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_LAST).metaValue	= wName1Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_SUFFIX).metaValue	= wName1Suffix.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TRUST_FLAG).metaValue	= wTrustFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_COMPANY_FLAG).metaValue	= wCompanyFlag.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FULL).metaValue	= wName2Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FIRST).metaValue	= wName2First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_MIDDLE).metaValue	= wName2Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_LAST).metaValue	= wName2Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_SUFFIX).metaValue	= wName2Suffix.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TYPE).metaValue	= wPrimaryType.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_VESTING_TYPE).metaValue	= wVestingType.getText();

		// Owner Address
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ADDRESS).metaValue	= wAddress.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_CITY).metaValue	= wCity.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_STATE).metaValue	= wState.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ZIP).metaValue	= wZip.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_CARRIERROUTE).metaValue	= wCarrierRoute.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_MAK).metaValue	= wMAK.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_BASE_MAK).metaValue	= wBaseMAK.getText();

		// Secondary owner
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FULL).metaValue	= wName3Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FIRST).metaValue	= wName3First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_MIDDLE).metaValue	= wName3Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_LAST).metaValue	= wName3Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_SUFFIX).metaValue	= wName3Suffix.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FULL).metaValue	= wName4Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FIRST).metaValue	= wName4First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_MIDDLE).metaValue	= wName4Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_LAST).metaValue	= wName4Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_SUFFIX).metaValue	= wName4Suffix.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_TYPE).metaValue	= wSecondaryType.getText();

		// Last Deed
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FULL).metaValue	= wLDOName1Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FIRST).metaValue	= wLDOName1First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_MIDDLE).metaValue	= wLDOName1Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_LAST).metaValue	= wLDOName1Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_SUFFIX).metaValue	= wLDOName1Suffix.getText();

		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FULL).metaValue	= wLDOName2Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FIRST).metaValue	= wLDOName2First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_MIDDLE).metaValue	= wLDOName2Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_LAST).metaValue	= wLDOName2Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_SUFFIX).metaValue	= wLDOName2Suffix.getText();

		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FULL).metaValue	= wLDOName3Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FIRST).metaValue	= wLDOName3First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_MIDDLE).metaValue	= wLDOName3Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_LAST).metaValue	= wLDOName3Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_SUFFIX).metaValue	= wLDOName3Suffix.getText();

		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FULL).metaValue	= wLDOName4Full.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FIRST).metaValue	= wLDOName4First.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_MIDDLE).metaValue	= wLDOName4Middle.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_LAST).metaValue	= wLDOName4Last.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_SUFFIX).metaValue	= wLDOName4Suffix.getText();

	}

	@Override
	public String getHelpURLKey() {

		return BaseMessages.getString(PKG, "MDPropertyWebService.Plugin.Help.OutputFieldsTab.OwnerOutput");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDPropertyWebServiceMeta propertyWebServiceMeta = (MDPropertyWebServiceMeta) meta;

		HashMap<String, MetaVal> outputFields = propertyWebServiceMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = propertyWebServiceMeta.propertyWebServiceFields.optionFields;

		ckIncludePrimaryOwner.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PRIMARY_OWNER).metaValue));
		ckIncludeOwnerAddress.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_OWNER_ADDRESS).metaValue));
		ckIncludeSecondaryOwner.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_SECONDARY_OWNER).metaValue));
		ckIncludeLastDeedOwner.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_LAST_DEED_OWNER).metaValue));


// Owner	
		wName1Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FULL).metaValue);
		wName1First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_FIRST).metaValue);
		wName1Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_MIDDLE).metaValue);
		wName1Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_LAST).metaValue);
		wName1Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_1_SUFFIX).metaValue);
		wTrustFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TRUST_FLAG).metaValue);
		wCompanyFlag.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_COMPANY_FLAG).metaValue);
		wName2Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FULL).metaValue);
		wName2First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_FIRST).metaValue);
		wName2Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_MIDDLE).metaValue);
		wName2Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_LAST).metaValue);
		wName2Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_NAME_2_SUFFIX).metaValue);
		wPrimaryType.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_TYPE).metaValue);
		wVestingType.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_VESTING_TYPE).metaValue);

	//Owner Address
		wAddress.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ADDRESS).metaValue);
		wCity.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_CITY).metaValue);
		wState.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_STATE).metaValue);
		wZip.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ZIP).metaValue);
		wCarrierRoute.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_CARRIERROUTE).metaValue);
		wMAK.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_MAK).metaValue);
		wBaseMAK.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_OWNER_BASE_MAK).metaValue);

	//Secondary Owner
		wName3Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FULL).metaValue);
		wName3First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_FIRST).metaValue);
		wName3Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_MIDDLE).metaValue);
		wName3Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_LAST).metaValue);
		wName3Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_3_SUFFIX).metaValue);
		wName4Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FULL).metaValue);
		wName4First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_FIRST).metaValue);
		wName4Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_MIDDLE).metaValue);
		wName4Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_LAST).metaValue);
		wName4Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_NAME_4_SUFFIX).metaValue);
		wSecondaryType.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_TYPE).metaValue);
	// Last Deed
		wLDOName1Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FULL).metaValue);
		wLDOName1First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FIRST).metaValue);
		wLDOName1Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_MIDDLE).metaValue);
		wLDOName1Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_LAST).metaValue);
		wLDOName1Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_SUFFIX).metaValue);
		wLDOName2Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FULL).metaValue);
		wLDOName2First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FIRST).metaValue);
		wLDOName2Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_MIDDLE).metaValue);
		wLDOName2Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_LAST).metaValue);
		wLDOName2Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_SUFFIX).metaValue);
		wLDOName3Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FULL).metaValue);
		wLDOName3First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FIRST).metaValue);
		wLDOName3Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_MIDDLE).metaValue);
		wLDOName3Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_LAST).metaValue);
		wLDOName3Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_SUFFIX).metaValue);
		wLDOName4Full.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FULL).metaValue);
		wLDOName4First.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FIRST).metaValue);
		wLDOName4Middle.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_MIDDLE).metaValue);
		wLDOName4Last.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_LAST).metaValue);
		wLDOName4Suffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_SUFFIX).metaValue);

		enable();
		return true;
	}

	private void createPrimaryOwnerGroup(Composite parent, Control last) {

		primaryOwnerGroup = new Group(parent, SWT.NONE);
		primaryOwnerGroup.setText(getString("PrimaryOwnerGroup.Label"));
		helper.setLook(primaryOwnerGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		primaryOwnerGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		primaryOwnerGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludePrimaryOwner = helper.addCheckBox(primaryOwnerGroup, last, "OwnerOutputTab.IncludePrimaryOwner");
		ckIncludePrimaryOwner.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		primaryOwnerMoreLess = helper.addLabel(primaryOwnerGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = primaryOwnerMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(1);
					primaryOwnerMoreLess.setText(getString("Less"));
				} else {
					collapse(1);
					primaryOwnerMoreLess.setText(getString("More"));
				}
			}
		};

		primaryOwnerMoreLess.addListener(SWT.MouseDown, expandCollapse);

		primaryOwnerMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		primaryOwnerMoreLess.setText(getString("Less"));

		last = primaryOwnerMoreLess;
		last = top = helper.addSpacer(primaryOwnerGroup, last);
		last = wName1Full = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name1Full");
		last = wName1First = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name1First");
		last = wName1Middle = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name1Middle");
		last = wName1Last = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name1Last");
		last = wName1Suffix = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name1Suffix");
		last = wTrustFlag = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.TrustFlag");
		last = wCompanyFlag = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.CompanyFlag");

		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wName2Full = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name2Full");
		last = wName2First = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name2First");
		last = wName2Middle = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name2Middle");
		last = wName2Last = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name2Last");
		last = wName2Suffix = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.Name2Suffix");
		last = wPrimaryType = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.PrimaryType");
		last = wVestingType = helper.addTextBox(primaryOwnerGroup, last, "OwnerOutputTab.VestingType");
	}

	private void createOwnerAddressGroup(Composite parent, Control last) {

		ownerAddressGroup = new Group(parent, SWT.NONE);
		ownerAddressGroup.setText(getString("OwnerAddressGroup.Label"));
		helper.setLook(ownerAddressGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		ownerAddressGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		ownerAddressGroup.setLayoutData(fd);

		// set the column widths for proper placing
		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeOwnerAddress = helper.addCheckBox(ownerAddressGroup, last, "OwnerOutputTab.IncludeOwnerAddress");
		ckIncludeOwnerAddress.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		ownerAddressMoreLess = helper.addLabel(ownerAddressGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = ownerAddressMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(2);
					ownerAddressMoreLess.setText(getString("Less"));
				} else {
					collapse(2);
					ownerAddressMoreLess.setText(getString("More"));
				}
			}
		};

		ownerAddressMoreLess.addListener(SWT.MouseDown, expandCollapse);

		ownerAddressMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		ownerAddressMoreLess.setText(getString("Less"));

		last = top = helper.addSpacer(ownerAddressGroup, last);
		last = wAddress = helper.addTextBox(ownerAddressGroup, last, "OwnerOutputTab.Address");
		last = wCity = helper.addTextBox(ownerAddressGroup, last, "OwnerOutputTab.City");
		last = wState = helper.addTextBox(ownerAddressGroup, last, "OwnerOutputTab.State");
		last = wZip = helper.addTextBox(ownerAddressGroup, last, "OwnerOutputTab.Zip");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;

		last = wCarrierRoute = helper.addTextBox(ownerAddressGroup, last, "OwnerOutputTab.CarrierRoute");
		last = wMAK = helper.addTextBox(ownerAddressGroup, last, "OwnerOutputTab.MAK");
		last = wBaseMAK = helper.addTextBox(ownerAddressGroup, last, "OwnerOutputTab.BaseMAK");
	}

	private void createSecondaryOwnerGroup(Composite parent, Control last) {

		secondaryOwnerGroup = new Group(parent, SWT.NONE);
		secondaryOwnerGroup.setText(getString("SecondaryOwnerGroup.Label"));
		helper.setLook(secondaryOwnerGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		secondaryOwnerGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		secondaryOwnerGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeSecondaryOwner = helper.addCheckBox(secondaryOwnerGroup, last, "OwnerOutputTab.IncludeSecondaryOwner");
		ckIncludeSecondaryOwner.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		secondaryOwnerMoreLess = helper.addLabel(secondaryOwnerGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = secondaryOwnerMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(3);
					secondaryOwnerMoreLess.setText(getString("Less"));
				} else {
					collapse(3);
					secondaryOwnerMoreLess.setText(getString("More"));
				}
			}
		};

		secondaryOwnerMoreLess.addListener(SWT.MouseDown, expandCollapse);

		secondaryOwnerMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		secondaryOwnerMoreLess.setText(getString("Less"));

		last = secondaryOwnerMoreLess;
		last = top = helper.addSpacer(secondaryOwnerGroup, last);
		last = wName3Full = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name3Full");
		last = wName3First = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name3First");
		last = wName3Middle = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name3Middle");
		last = wName3Last = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name3Last");
		last = wName3Suffix = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name3Suffix");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wName4Full = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name4Full");
		last = wName4First = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name4First");
		last = wName4Middle = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name4Middle");
		last = wName4Last = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name4Last");
		last = wName4Suffix = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.Name4Suffix");
		last = wSecondaryType = helper.addTextBox(secondaryOwnerGroup, last, "OwnerOutputTab.SecondaryType");
	}

	private void createLastDeedOwnerGroup(Composite parent, Control last) {

		lastDeedOwnerGroup = new Group(parent, SWT.NONE);
		lastDeedOwnerGroup.setText(getString("LastDeedOwnerGroup.Label"));
		helper.setLook(lastDeedOwnerGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		lastDeedOwnerGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		lastDeedOwnerGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeLastDeedOwner = helper.addCheckBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LastDeedOwnerGroup");
		ckIncludeLastDeedOwner.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		lastDeedOwnerMoreLess = helper.addLabel(lastDeedOwnerGroup, last, "");

		Listener expandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = lastDeedOwnerMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(4);
					lastDeedOwnerMoreLess.setText(getString("Less"));
				} else {
					collapse(4);
					lastDeedOwnerMoreLess.setText(getString("More"));
				}
			}
		};

		lastDeedOwnerMoreLess.addListener(SWT.MouseDown, expandCollapse);

		lastDeedOwnerMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		lastDeedOwnerMoreLess.setText(getString("Less"));

		last = lastDeedOwnerMoreLess;
		last = top = helper.addSpacer(lastDeedOwnerGroup, last);
		last = wLDOName1Full = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName1Full");
		last = wLDOName1First = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName1First");
		last = wLDOName1Middle = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName1Middle");
		last = wLDOName1Last = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName1Last");
		last = wLDOName1Suffix = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName1Suffix");

		last = wLDOName2Full = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName2Full");
		last = wLDOName2First = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName2First");
		last = wLDOName2Middle = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName2Middle");
		last = wLDOName2Last = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName2Last");
		last = wLDOName2Suffix = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName2Suffix");
		// set the column widths for proper placing
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wLDOName3Full = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName3Full");
		last = wLDOName3First = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName3First");
		last = wLDOName3Middle = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName3Middle");
		last = wLDOName3Last = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName3Last");
		last = wLDOName3Suffix = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName3Suffix");

		last = wLDOName4Full = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName4Full");
		last = wLDOName4First = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName4First");
		last = wLDOName4Middle = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName4Middle");
		last = wLDOName4Last = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName4Last");
		last = wLDOName4Suffix = helper.addTextBox(lastDeedOwnerGroup, last, "OwnerOutputTab.LDOName4Suffix");
	}

	private void expand(int group) {

		FormData primaryFormData = (FormData) primaryOwnerGroup.getLayoutData();
		FormData ownerAddrFormData  = (FormData) ownerAddressGroup.getLayoutData();
		FormData secondaryFormData  = (FormData) secondaryOwnerGroup.getLayoutData();
		FormData lastDeedFormData = (FormData) lastDeedOwnerGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Address expand
			primaryFormData.top = new FormAttachment(description, helper.margin);
			primaryFormData.bottom = null;
			wComp.layout();
			offset = (primaryOwnerGroup.getBounds().height + (helper.margin * 2));
			for (Control child : primaryOwnerGroup.getChildren()) {
				child.setVisible(true);
			}

			ownerAddrFormData.top = new FormAttachment(description, offset);
			if (ownerAddressMoreLess.getText().equals("less")) {
				ownerAddrFormData.bottom = null;
			} else {
				offset = (primaryOwnerGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				ownerAddrFormData.bottom = new FormAttachment(description, offset);
			}
		}

		if (group == 2) {
			// Parsed expand
			ownerAddrFormData.top = new FormAttachment(primaryOwnerGroup, helper.margin);
			ownerAddrFormData.bottom = null;
			for (Control child : ownerAddressGroup.getChildren()) {
				child.setVisible(true);
			}
			wComp.layout();
			offset = (ownerAddressGroup.getBounds().height + (helper.margin * 2));
			secondaryFormData.top = new FormAttachment(ownerAddressGroup, (helper.margin * 2));
			if (secondaryOwnerMoreLess.getText().equals("less")) {
				secondaryFormData.bottom = null;
			} else {
				offset = (ownerAddressGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				secondaryFormData.bottom = new FormAttachment(ownerAddressGroup, offset);
			}
		}

		if (group == 3) {
			//parcel expand
			secondaryFormData.top = new FormAttachment(ownerAddressGroup, helper.margin * 2);
			secondaryFormData.bottom = null;
			for (Control child : secondaryOwnerGroup.getChildren()) {
				child.setVisible(true);
			}

			wComp.layout();
			offset = (secondaryOwnerGroup.getBounds().height + (helper.margin * 2));
			lastDeedFormData.top = new FormAttachment(secondaryOwnerGroup, (helper.margin * 2));
			if (lastDeedOwnerMoreLess.getText().equals("less")) {
				lastDeedFormData.bottom = null;
			} else {
				offset = (secondaryOwnerGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				lastDeedFormData.bottom = new FormAttachment(secondaryOwnerGroup, offset);
			}


		}

		if (group == 4) {
			//legal expand
			lastDeedFormData.top = new FormAttachment(secondaryOwnerGroup, helper.margin * 2);
			lastDeedFormData.bottom = null;
			for (Control child : lastDeedOwnerGroup.getChildren()) {
				child.setVisible(true);
			}
		}

		enable();
	}

	private void collapse(int group) {

		FormData addressFormData = (FormData) primaryOwnerGroup.getLayoutData();
		FormData parsedFormData  = (FormData) ownerAddressGroup.getLayoutData();
		FormData parcelFormData  = (FormData) secondaryOwnerGroup.getLayoutData();
		FormData legalFormData = (FormData) lastDeedOwnerGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Address collapse
			addressFormData.top = new FormAttachment(description, helper.margin);
			addressFormData.bottom = new FormAttachment(description, collapsedHeight);

			for (Control child : primaryOwnerGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			parsedFormData.top = new FormAttachment(primaryOwnerGroup, helper.margin);
			if (ownerAddressMoreLess.getText().equals("less")) {
				parsedFormData.bottom = null;
			} else {
				offset = (primaryOwnerGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				parsedFormData.bottom = new FormAttachment(primaryOwnerGroup, offset);
			}
		}

		if (group == 2) {

			//parsed collapse
			parsedFormData.top = new FormAttachment(description, primaryOwnerGroup.getBounds().height + (helper.margin * 2));
			parsedFormData.bottom = new FormAttachment(description, primaryOwnerGroup.getBounds().height + (helper.margin * 2) + 80);
			for (Control child : ownerAddressGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			parcelFormData.top = new FormAttachment(ownerAddressGroup, helper.margin);
			if (secondaryOwnerMoreLess.getText().equals("less")) {
				parcelFormData.bottom = null;
			} else {
				parcelFormData.bottom = new FormAttachment(ownerAddressGroup, ownerAddressGroup.getBounds().height + 80);
			}
		}

		//parcel collapse
		if (group == 3) {
			for (Control child : secondaryOwnerGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			parcelFormData.top = new FormAttachment(ownerAddressGroup, helper.margin * 2);
			parcelFormData.bottom = new FormAttachment(ownerAddressGroup, ownerAddressGroup.getBounds().height + 80);

			wComp.layout();
			legalFormData.top = new FormAttachment(secondaryOwnerGroup, helper.margin);
			if (lastDeedOwnerMoreLess.getText().equals("less")) {
				legalFormData.bottom = null;
			} else {
				legalFormData.bottom = new FormAttachment(secondaryOwnerGroup, secondaryOwnerGroup.getBounds().height + 80);
			}
		}

		//legal collapse
		if (group == 4) {
			for (Control child : lastDeedOwnerGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			legalFormData.top = new FormAttachment(secondaryOwnerGroup, helper.margin * 2);
			legalFormData.bottom = new FormAttachment(secondaryOwnerGroup, secondaryOwnerGroup.getBounds().height + 80);
		}

		enable();
	}
	
	
	private void enable() {


		// Address
		if (ckIncludePrimaryOwner.getSelection()) {
			for (Control child : primaryOwnerGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : primaryOwnerGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		// Parsed
		if (ckIncludeOwnerAddress.getSelection()) {
			for (Control child : ownerAddressGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : ownerAddressGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//PARCEL
		if (ckIncludeSecondaryOwner.getSelection()) {
			for (Control child : secondaryOwnerGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : secondaryOwnerGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//Legal
		if (ckIncludeLastDeedOwner.getSelection()) {
			for (Control child : lastDeedOwnerGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : lastDeedOwnerGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		ckIncludePrimaryOwner.setEnabled(true);
		ckIncludePrimaryOwner.setVisible(true);
		ckIncludeOwnerAddress.setEnabled(true);
		ckIncludeOwnerAddress.setVisible(true);
		ckIncludeSecondaryOwner.setEnabled(true);
		ckIncludeSecondaryOwner.setVisible(true);
		ckIncludeLastDeedOwner.setVisible(true);
		ckIncludeLastDeedOwner.setEnabled(true);

		primaryOwnerMoreLess.setVisible(true);
		primaryOwnerMoreLess.setEnabled(true);
		ownerAddressMoreLess.setVisible(true);
		ownerAddressMoreLess.setEnabled(true);
		secondaryOwnerMoreLess.setVisible(true);
		secondaryOwnerMoreLess.setEnabled(true);
		lastDeedOwnerMoreLess.setVisible(true);
		lastDeedOwnerMoreLess.setEnabled(true);

		wComp.layout();
	}

	@Override
	public void dispose() {

	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.OwnerOutputTab." + key, args);
	}
}
