package com.melissadata.kettle.globalverify.ui;

import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.globalverify.MDGlobalDialog;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.meta.AddressMeta;
import com.melissadata.kettle.globalverify.support.CountryUtil;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressHelper;
import com.melissadata.kettle.globalverify.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class AddressTab implements MDTab {

	//Declarations
	private static Class<?>              PKG                                    = MDGlobalMeta.class;
	private        Table                 tableSelected                          = null;
	private        Table                 tableAvailible                         = null;
	private        SortedSet<String>     sSelected                              = null;
	private        SortedSet<String>     sAvailable                             = null;
	private        SortedSet<String>     sCountryList                           = null;
	//
	private        MDGlobalDialog        dialog                                 = null;
	private        MDGlobalAddressHelper helper                                 = null;
	private        Group                 addressGroup                           = null;
	private        Group                 addressOptionsGroup                    = null;
	private        AddressMeta           metaAddress                            = null;
	private        CountryUtil           countryUtil                            = null;
	// Inputs
	private        MDInputCombo          wOrganization                          = null;
	private        MDInputCombo          wInAddressLine1                        = null;
	private        MDInputCombo          wInAddressLine2                        = null;
	private        MDInputCombo          wInAddressLine3                        = null;
	private        MDInputCombo          wInAddressLine4                        = null;
	private        MDInputCombo          wInAddressLine5                        = null;
	private        MDInputCombo          wInAddressLine6                        = null;
	private        MDInputCombo          wInAddressLine7                        = null;
	private        MDInputCombo          wInAddressLine8                        = null;
	private        MDInputCombo          wInDoubleDependentLocality             = null;//When we need to use a dependent locality to identify a thoroughfare, it is also possible that it exists twice within the same locality. In this case the thoroughfare needs to be identified by a further locality, the double dependent locality. This is typically a village or hamlet.
	private        MDInputCombo          wInDependentLocality                   = null; // When a thoroughfare exists more than once in a Post town, it is not always possible to make it dependent on another thoroughfare. In this case the thoroughfare is dependent on a locality
	private        MDInputCombo          wInLocality                            = null; // City
	private        MDInputCombo          wInSubAdministrativeArea               = null; //County / District
	private        MDInputCombo          wInAdministrativeArea                  = null; // State / Province / Region (ISO code when available)
	private        MDInputCombo          wInSubNationalArea                     = null; //  region within a nation
	private        MDInputCombo          wInPostalCode                          = null;
	private        MDInputCombo          wInCountry                             = null;
	private        Control[]             wInDefaultCountry                      = null;
	private        CCombo                wDisplayOption                         = null;
	private        String                displayOption                          = "";
	//Output Address Parameters
	private        Text                  wOutOrganization                       = null;
	private        Text                  wOutAddressLine1                       = null;
	private        Text                  wOutAddressLine2                       = null;
	private        Text                  wOutAddressLine3                       = null;
	private        Text                  wOutAddressLine4                       = null;
	private        Text                  wOutAddressLine5                       = null;
	private        Text                  wOutAddressLine6                       = null;
	private        Text                  wOutAddressLine7                       = null;
	private        Text                  wOutAddressLine8                       = null;
	private        Text                  wOutFullAddress                        = null;
	private        Text                  wOutDeliveryLine                       = null; // local only
	//Parsed Sub-Premises Parameters
	private        Text                  wOutBuilding                           = null;
	private        Text                  wOutSubBuilding                        = null; // local only
	private        Text                  wOutSubBuildingNumber                  = null; // local only
	private        Text                  wOutSubBuildingType                    = null; // local only
	private        Text                  wOutSubPremises                        = null;
	private        Text                  wOutSubPremisesNumber                  = null;
	private        Text                  wOutSubPremisesType                    = null;
	private        Text                  wOutSubPremiseLevel                    = null; // local only
	private        Text                  wOutSubPremiseLevelNumber              = null; // local only
	private        Text                  wOutSubPremiseLevelType                = null; // local only
	//Parsed Thoroughfare Parameters
	private        Text                  wOutPremises                           = null; // local only
	private        Text                  wOutPremisesNumber                     = null;
	private        Text                  wOutPremisesType                       = null;
	private        Text                  wOutThoroughfare                       = null;
	private        Text                  wOutThoroughfareLeadingType            = null;
	private        Text                  wOutThoroughfareName                   = null;
	private        Text                  wOutThoroughfarePostDirection          = null;
	private        Text                  wOutThoroughfarePreDirection           = null;
	private        Text                  wOutThoroughfareTrailingType           = null;
	private        Text                  wOutThoroughfareTypeAttached           = null; // local only
	//Parsed Dependent Thoroughfare Columns
	private        Text                  wOutDependentThoroughfare              = null;
	private        Text                  wOutDependentThoroughfareLeadingType   = null;
	private        Text                  wOutDependentThoroughfareName          = null;
	private        Text                  wOutDependentThoroughfarePostDirection = null;
	private        Text                  wOutDependentThoroughfarePreDirection  = null;
	private        Text                  wOutDependentThoroughfareTrailingType  = null;
	private        Text                  wOutDependentThoroughfareTypeAttached  = null; // local only
	//Parsed Postal Facility Columns
	private        Text                  wOutPostBox                            = null;
	private        Text                  wOutPostalCode                         = null;
	private        Text                  wOutPersonalID                         = null; // local only
	private        Text                  wOutPostOfficeLocation                 = null; // local only
	//Parsed Regional Columns
	private        Text                  wOutAdministrativeArea                 = null;
	private        Text                  wOutCountyName                         = null; // local Only
	private        Text                  wOutDependentLocality                  = null;
	private        Text                  wOutDoubleDependentLocality            = null;
	private        Text                  wOutLocality                           = null;
	private        Text                  wOutSubAdministrativeArea              = null;
	private        Text                  wOutSubNationalArea                    = null;
	//Extra Output Address Parameters
	private        Text                  wOutLatitude                           = null;
	private        Text                  wOutLongitude                          = null;
	private        Text                  wOutAddressTypeCodes                   = null;
	//Extra Output CountryName Parameters
	private        Text                  wOutAddressKey                         = null; // web only
	private        Text                  wOutCountryCode                        = null; // local only
	private        Text                  wOutCountrySubdivisionCode             = null; // web only
	private        Text                  wOutCountryTimeZone                    = null; // local only
	private        Text                  wOutCountryUTC                         = null; // local only
	private        Text                  wOutCountryName                        = null;
	private        Text                  wOutCountryAlpha2                      = null;
	private        Text                  wOutCountryAlpha3                      = null;
	private        Text                  wOutCountryNumeric                     = null;
	private        Text                  wOutCountryFormalName                  = null; // local only
	// Options
	private        Control[]             wOptLineSeparator                      = null;
	private        String[]              lineSeparatorOptions                   =  BaseMessages.getString(PKG, "MDGlobalDialog.AddressTab.LineSeparators").split(",");
	private        Control[]             wOptOutputScript                       = null;
	private        String[]              webOutputScriptOptions                 =  BaseMessages.getString(PKG, "MDGlobalDialog.AddressTab.OutputScriptOptions").split(",");
	private        Control[]             wCountryOfOrigion                      = null;
	private        Control[]             wWebOptDeliveryLine                    = null;
	private        String[]              deliveryLineOptions                    =  BaseMessages.getString(PKG, "MDGlobalDialog.AddressTab.DeliveryLineOptions").split(",");
	//
	private        SelectionListener     lsnEnable                              = null;
	private        Composite             wInputComp                             = null;
	private        Composite             wOutputComp                            = null;
	private        Composite             wParsedComp                            = null;
	private        Composite             wExtraComp                             = null;
	private        Label                 inputDescription                       = null;
	private        Button                bnNext                                 = null;
	private        Button                bnBack                                 = null;
	private        Button                bnLocal                                = null;
	private        Button                bnWeb                                  = null;
	private        Button                bnVaried                               = null;
	private        Button                bnAddCodes                             = null;
	private        int                   pgNum                                  = 1;

	public AddressTab(MDGlobalDialog dialog) {

		this.dialog = dialog;
		helper = dialog.getHelper();
		lsnEnable = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				enable();
			}
		};
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		Composite wBackComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wBackComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wBackComp.setLayout(fl);
		createInputComp(wBackComp);
		createOutputComp(wBackComp);
		createParsedComp(wBackComp);
		createExtraOutputComp(wBackComp);
		// Fit the composite within its container (the scrolled composite)
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(95, 0);
		wInputComp.setLayoutData(fd);
		wOutputComp.setLayoutData(fd);
		wParsedComp.setLayoutData(fd);
		wExtraComp.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wBackComp.setLayoutData(fd);
		//helper.setLook(wBackComp);
		bnNext = helper.addPushButton(wBackComp, null, "AddressTab.Address.Next", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				doNextBack(true);
			}
		});
		fd = new FormData();
		fd.left = new FormAttachment(90, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		bnNext.setLayoutData(fd);
		bnBack = helper.addPushButton(wBackComp, null, "AddressTab.Address.Next", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				doNextBack(false);
			}
		});
		bnBack.setText("<- Back");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		// fd.top = new FormAttachment(wOutputComp, helper.margin);
		fd.right = new FormAttachment(10, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		bnBack.setLayoutData(fd);
		// Pack the composite and get its size
		wInputComp.pack();
		wBackComp.pack();
		wOutputComp.pack();
		wParsedComp.pack();
		wExtraComp.pack();
		Rectangle bounds = wBackComp.getBounds();
		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wBackComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		wSComp.setMinWidth(bounds.width);
		wSComp.setMinHeight(bounds.height);
		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);
		inputDescription.setText(getString("Description"));// This is done here due to sizing of dialog
		doNextBack(false);
	}

	@Override
	public void advancedConfigChanged() {
		// Check for enable change
		enable();
	}

	private void createInputComp(Composite wSComp) {
		// Create the composite that will hold the contents of the tab
		wInputComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wInputComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wInputComp.setLayout(fl);
		// Description line
		inputDescription = new Label(wInputComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(inputDescription);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		inputDescription.setLayoutData(fd);
		inputDescription.setText("Map");
		// input address group
		addressGroup = helper.addGroup(wInputComp, inputDescription, getString("InputAddressGroup.Label"));
		//
		wOrganization = helper.addInputComboBox(addressGroup, null, "AddressTab.Input.Organization", "NInOrganization");
		wOrganization.getComboBox().setToolTipText(getString("Input.Organization.Tooltip"));
		wInAddressLine1 = helper.addInputComboBox(addressGroup, wOrganization.getComboBox(), "AddressTab.Input.AddressLine1", "NInAddressLine1");
		wInAddressLine1.addSelectionListener(lsnEnable);
		wInAddressLine1.getComboBox().setToolTipText(getString("Input.AddressLine1.Tooltip"));
		wInAddressLine2 = helper.addInputComboBox(addressGroup, wInAddressLine1.getComboBox(), "AddressTab.Input.AddressLine2", "NInAddressLine2");
		wInAddressLine2.getComboBox().setToolTipText(getString("Input.AddressLine2.Tooltip"));
		wInAddressLine3 = helper.addInputComboBox(addressGroup, wInAddressLine2.getComboBox(), "AddressTab.Input.AddressLine3", "NInAddressLine3");
		wInAddressLine3.getComboBox().setToolTipText(getString("Input.AddressLine3.Tooltip"));
		wInAddressLine4 = helper.addInputComboBox(addressGroup, wInAddressLine3.getComboBox(), "AddressTab.Input.AddressLine4", "NInAddressLine4");
		wInAddressLine4.getComboBox().setToolTipText(getString("Input.AddressLine4.Tooltip"));
		wInAddressLine5 = helper.addInputComboBox(addressGroup, wInAddressLine4.getComboBox(), "AddressTab.Input.AddressLine5", "NInAddressLine5");
		wInAddressLine5.getComboBox().setToolTipText(getString("Input.AddressLine5.Tooltip"));
		wInAddressLine6 = helper.addInputComboBox(addressGroup, wInAddressLine5.getComboBox(), "AddressTab.Input.AddressLine6", "NInAddressLine6");
		wInAddressLine6.getComboBox().setToolTipText(getString("Input.AddressLine6.Tooltip"));
		wInAddressLine7 = helper.addInputComboBox(addressGroup, wInAddressLine6.getComboBox(), "AddressTab.Input.AddressLine7", "NInAddressLine7");
		wInAddressLine7.getComboBox().setToolTipText(getString("Input.AddressLine7.Tooltip"));
		wInAddressLine8 = helper.addInputComboBox(addressGroup, wInAddressLine7.getComboBox(), "AddressTab.Input.AddressLine8", "NInAddressLine8");
		wInAddressLine8.getComboBox().setToolTipText(getString("Input.AddressLine8.Tooltip"));
		//
		wInDoubleDependentLocality = helper.addInputComboBox(addressGroup, null, "AddressTab.Input.DoubleDependentLocality", "NInDoubleDependentLocality");
		wInDoubleDependentLocality.getComboBox().setToolTipText(getString("Input.DoubleDependentLocality.Tooltip"));
		wInDependentLocality = helper.addInputComboBox(addressGroup, wInDoubleDependentLocality.getComboBox(), "AddressTab.Input.DependentLocality", "NInDependentLocality");
		wInDependentLocality.getComboBox().setToolTipText(getString("Input.DependentLocality.Tooltip"));
		wInLocality = helper.addInputComboBox(addressGroup, wInDependentLocality.getComboBox(), "AddressTab.Input.Locality", "NInLocality");
		wInLocality.getComboBox().setToolTipText(getString("Input.Locality.Tooltip"));
		wInLocality.addSelectionListener(lsnEnable);
		wInSubAdministrativeArea = helper.addInputComboBox(addressGroup, wInLocality.getComboBox(), "AddressTab.Input.SubAdministrativeArea", "NInSubAdministrativeArea");
		wInSubAdministrativeArea.getComboBox().setToolTipText(getString("Input.SubAdministrativeArea.Tooltip"));
		wInAdministrativeArea = helper.addInputComboBox(addressGroup, wInSubAdministrativeArea.getComboBox(), "AddressTab.Input.AdministrativeArea", "NInAdministrativeArea");
		wInAdministrativeArea.getComboBox().setToolTipText(getString("Input.AdministrativeArea.Tooltip"));
		wInAdministrativeArea.addSelectionListener(lsnEnable);
		wInSubNationalArea = helper.addInputComboBox(addressGroup, wInAdministrativeArea.getComboBox(), "AddressTab.Input.SubNationalArea", "NInSubNationalArea");
		wInSubNationalArea.getComboBox().setToolTipText(getString("Input.SubNationalArea.Tooltip"));
		wInPostalCode = helper.addInputComboBox(addressGroup, wInSubNationalArea.getComboBox(), "AddressTab.Input.PostalCode", "NInZip");
		wInPostalCode.getComboBox().setToolTipText(getString("Input.PostalCode.Tooltip"));
		wInPostalCode.addSelectionListener(lsnEnable);
		wInCountry = helper.addInputComboBox(addressGroup, wInPostalCode.getComboBox(), "AddressTab.Input.Country", "NInCountry");
		wInCountry.getComboBox().setToolTipText(getString("Input.Country.Tooltip"));
		wInCountry.addSelectionListener(lsnEnable);

		wInDefaultCountry = helper.addComboBox(addressGroup, wInAddressLine8.getComboBox(), "AddressTab.Input.Countries");
		wInDefaultCountry[1].setToolTipText(getString("Input.Countries.Tooltip"));
		((CCombo) wInDefaultCountry[1]).addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				//defaultCountryIndex = countryUtil.getCountryIndex(((CCombo) wInDefaultCountry[1]).getText());
				enable();
			}
		});
		int leftSide  = 25;
		int rightSide = 50;
		//	((FormData) wOrganization.getComboBox().getLayoutData()).top = null;
		setLeftColumn(wOrganization, leftSide, rightSide);
		setLeftColumn(wInAddressLine1, leftSide, rightSide);
		setLeftColumn(wInAddressLine2, leftSide, rightSide);
		setLeftColumn(wInAddressLine3, leftSide, rightSide);
		setLeftColumn(wInAddressLine4, leftSide, rightSide);
		setLeftColumn(wInAddressLine5, leftSide, rightSide);
		setLeftColumn(wInAddressLine6, leftSide, rightSide);
		setLeftColumn(wInAddressLine7, leftSide, rightSide);
		setLeftColumn(wInAddressLine8, leftSide, rightSide);
		leftSide = 50;
		rightSide = 75;
		((FormData) wInDoubleDependentLocality.getLabel().getLayoutData()).top = null;
		setRightColumn(wInDoubleDependentLocality, leftSide, rightSide);
		setRightColumn(wInDependentLocality, leftSide, rightSide);
		setRightColumn(wInLocality, leftSide, rightSide);
		setRightColumn(wInSubAdministrativeArea, leftSide, rightSide);
		setRightColumn(wInAdministrativeArea, leftSide, rightSide);
		setRightColumn(wInSubNationalArea, leftSide, rightSide);
		setRightColumn(wInPostalCode, leftSide, rightSide);
		setRightColumn(wInCountry, leftSide, rightSide);
		// default country option
		((Label) wInDefaultCountry[0]).setAlignment(SWT.LEFT);
		((FormData) wInDefaultCountry[0].getLayoutData()).left = new FormAttachment(rightSide, helper.margin);
		((FormData) wInDefaultCountry[0].getLayoutData()).right = new FormAttachment(100, -helper.margin);
		((FormData) wInDefaultCountry[0].getLayoutData()).top = new FormAttachment(wInCountry.getComboBox(), 25);
		((FormData) wInDefaultCountry[1].getLayoutData()).left = new FormAttachment(rightSide, 5 * helper.margin);
		((FormData) wInDefaultCountry[1].getLayoutData()).right = new FormAttachment(100, -helper.margin);
		((FormData) wInDefaultCountry[1].getLayoutData()).top = new FormAttachment(wInDefaultCountry[0], helper.margin);
		//
		Label spc = helper.addSpacer(addressGroup, wInAddressLine8.getComboBox());
		// Display Option
		Control[] optControls = helper.addComboBox(addressGroup, spc, "AddressTab.DisplayOptions");
		wDisplayOption = (CCombo) optControls[1];
		Label optLabel = (Label) optControls[0];
		wDisplayOption.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				displayOption = wDisplayOption.getText();
				refreshDisplayInput();
				enable();
			}
		});

		optLabel.setAlignment(SWT.LEFT);
		((FormData) optLabel.getLayoutData()).left = new FormAttachment(0, helper.margin);
		((FormData) optLabel.getLayoutData()).top = new FormAttachment(spc, helper.margin);
		((FormData) wDisplayOption.getLayoutData()).left = new FormAttachment(0, helper.margin);
		((FormData) wDisplayOption.getLayoutData()).right = new FormAttachment(20, -helper.margin);
		((FormData) wDisplayOption.getLayoutData()).top = new FormAttachment(optLabel, helper.margin);

		// Option group
		addressOptionsGroup = helper.addGroup(wInputComp, addressGroup, getString("AddressOptionsGroup.Label"));

		Label addressOptionsDescription = new Label(addressOptionsGroup, SWT.LEFT | SWT.WRAP);
		helper.setLook(addressOptionsDescription);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		addressOptionsDescription.setLayoutData(fd);
		addressOptionsDescription.setText(getString("AddressOptionsGroup.Description"));

		// On premise Options
		wCountryOfOrigion = helper.addComboBox(addressOptionsGroup, addressOptionsDescription, "AddressTab.OriginCountry");
		setOptionsColumn(wCountryOfOrigion, "OriginCountry.Tooltip");
		wOptOutputScript = helper.addComboBox(addressOptionsGroup, wCountryOfOrigion[1], "AddressTab.OutputScript");
		setOptionsColumn(wOptOutputScript, "OutputScript.Tooltip");
		wOptLineSeparator = helper.addComboBox(addressOptionsGroup, wOptOutputScript[1], "AddressTab.LineSeparator");
		setOptionsColumn(wOptLineSeparator, "LineSeparator.Tooltip");
		wWebOptDeliveryLine = helper.addComboBox(addressOptionsGroup, wOptLineSeparator[1], "AddressTab.DeliveryLine");
		setOptionsColumn(wWebOptDeliveryLine, "DeliveryLine.Tooltip");

		((CCombo) wCountryOfOrigion[1]).addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				//origionCountryIndex = countryUtil.getCountryIndex(((CCombo) wCountryOfOrigion[1]).getText());
			}
		});

		Label spacerBottom = helper.addSpacer(addressOptionsGroup, wWebOptDeliveryLine[1]);
		fd = new FormData();
		fd.top = new FormAttachment(wWebOptDeliveryLine[1], 30);
//		fd.left = new FormAttachment(0, helper.margin);
//		fd.right = new FormAttachment(50, -helper.margin);
		spacerBottom.setLayoutData(fd);

		bnLocal = new Button(addressOptionsGroup, SWT.RADIO);
		fd = new FormData();
		fd.top = new FormAttachment(addressOptionsDescription, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		bnLocal.setLayoutData(fd);
		bnLocal.setText("Local Processing");
		helper.setLook(bnLocal);
		bnLocal.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				dialog.setChanged();
				enable();
			}
		});

		bnWeb = new Button(addressOptionsGroup, SWT.RADIO);
		fd = new FormData();
		fd.top = new FormAttachment(bnLocal, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		bnWeb.setLayoutData(fd);
		bnWeb.setText("Web Processing");
		helper.setLook(bnWeb);
		bnWeb.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				dialog.setChanged();
				enable();
			}
		});

		bnVaried = new Button(addressOptionsGroup, SWT.RADIO);
		fd = new FormData();
		fd.top = new FormAttachment(addressOptionsDescription, helper.margin);
		fd.left = new FormAttachment(25, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		bnVaried.setLayoutData(fd);
		bnVaried.setText("Varied Processing");
		helper.setLook(bnVaried);
		bnVaried.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				dialog.setChanged();
				enable();
			}
		});

		bnAddCodes = new Button(addressOptionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.top = new FormAttachment(bnVaried, helper.margin);
		fd.left = new FormAttachment(25, 5 * helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		bnAddCodes.setLayoutData(fd);
		bnAddCodes.setText("Add Process Type Codes (LP01, WP01)");
		helper.setLook(bnAddCodes);
		bnAddCodes.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				dialog.setChanged();
			}
		});

		// Select Countries
		Label lOnPremises = new Label(addressOptionsGroup, SWT.None);
		fd = new FormData();
		//fd.top = new FormAttachment(addressOptionsDescription, helper.margin);
		fd.top = new FormAttachment(bnWeb, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		lOnPremises.setLayoutData(fd);
		lOnPremises.setText("Select which countries should be processed on Premises");

		// Available Countries list
		sSelected = new TreeSet<String>();
		sAvailable = new TreeSet<String>();

		tableAvailible = createTable(addressOptionsGroup, "Available Countries");
		fd = new FormData();
		fd.top = new FormAttachment(lOnPremises, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		fd.bottom = new FormAttachment(spacerBottom, 10);
		tableAvailible.setLayoutData(fd);
		helper.setLook(lOnPremises);
		helper.setLook(tableAvailible);
		tableAvailible.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				TableItem[] selection = tableAvailible.getSelection();
				String      string    = selection[0].getText();
				sAvailable.remove(string);
				sSelected.add(string);
				refreshLocalProcessingTables();
				dialog.setChanged();
			}
		});
		//
		tableSelected = createTable(addressOptionsGroup, "Selected Countries ");
		fd = new FormData();
		fd.top = new FormAttachment(lOnPremises, helper.margin);
		fd.left = new FormAttachment(25, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		fd.bottom = new FormAttachment(spacerBottom, 10);
		tableSelected.setLayoutData(fd);
		tableSelected.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				TableItem[] selection = tableSelected.getSelection();
				String      string    = selection[0].getText();
				sAvailable.add(string);
				sSelected.remove(string);
				refreshLocalProcessingTables();
				dialog.setChanged();
			}
		});
	}

	private Table createTable(Composite parent, String name) {

		final Table table = new Table(parent, SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);

		final TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText(name);
		column1.setWidth(300);
		return table;
	}

	private void refreshLocalProcessingTables() {

		tableAvailible.removeAll();
		tableSelected.removeAll();
		TableItem item = null;
		for (String country : sAvailable) {
			item = new TableItem(tableAvailible, SWT.NONE);
			item.setText(country);
		}
		for (String country : sSelected) {
			item = new TableItem(tableSelected, SWT.NONE);
			item.setText(country);
		}
	}

	private void createOutputComp(Composite wSComp) {
		// Create the composite that will hold the contents of the tab
		wOutputComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wOutputComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wOutputComp.setLayout(fl);
		// Description line
		Label description = new Label(wOutputComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("OutputAddressGroup.Description"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(95, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// output address group
		Group outAddressGroup = new Group(wOutputComp, SWT.NONE);
		outAddressGroup.setText(getString("OutputAddressGroup.Label"));
		helper.setLook(outAddressGroup);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(80, -helper.margin);
		outAddressGroup.setLayoutData(fd);
		outAddressGroup.setLayout(fl);
		helper.colWidth[0] = 10;
		wOutOrganization = helper.addTextBox(outAddressGroup, null, "AddressTab.Output.Organization");
		wOutAddressLine1 = helper.addTextBox(outAddressGroup, wOutOrganization, "AddressTab.Output.AddressLine1");
		wOutAddressLine2 = helper.addTextBox(outAddressGroup, wOutAddressLine1, "AddressTab.Output.AddressLine2");
		wOutAddressLine3 = helper.addTextBox(outAddressGroup, wOutAddressLine2, "AddressTab.Output.AddressLine3");
		wOutAddressLine4 = helper.addTextBox(outAddressGroup, wOutAddressLine3, "AddressTab.Output.AddressLine4");
		wOutAddressLine5 = helper.addTextBox(outAddressGroup, wOutAddressLine4, "AddressTab.Output.AddressLine5");
		wOutAddressLine6 = helper.addTextBox(outAddressGroup, wOutAddressLine5, "AddressTab.Output.AddressLine6");
		wOutAddressLine7 = helper.addTextBox(outAddressGroup, wOutAddressLine6, "AddressTab.Output.AddressLine7");
		wOutAddressLine8 = helper.addTextBox(outAddressGroup, wOutAddressLine7, "AddressTab.Output.AddressLine8");
		//
		helper.colWidth[0] = 55;
		wOutCountryName = helper.addTextBox(outAddressGroup, null, "AddressTab.Output.Country");
		wOutDependentLocality = helper.addTextBox(outAddressGroup, wOutCountryName, "AddressTab.Output.DependentLocality");
		wOutLocality = helper.addTextBox(outAddressGroup, wOutDependentLocality, "AddressTab.Output.Locality");
		wOutAdministrativeArea = helper.addTextBox(outAddressGroup, wOutLocality, "AddressTab.Output.AdminArea");
		wOutPostalCode = helper.addTextBox(outAddressGroup, wOutAdministrativeArea, "AddressTab.Output.PostalCode");
		wOutDeliveryLine = helper.addTextBox(outAddressGroup, wOutPostalCode, "AddressTab.Output.DeliveryLine");
		wOutFullAddress = helper.addTextBox(outAddressGroup, wOutDeliveryLine, "AddressTab.Output.FormattedAddress");
		wOutAddressTypeCodes = helper.addTextBox(outAddressGroup, wOutFullAddress, "AddressTab.Output.USAddressTypeCode");
		wOutAddressKey = helper.addTextBox(outAddressGroup, wOutAddressTypeCodes, "AddressTab.Output.AddressKey");
		//
		wOutOrganization.setToolTipText(getString("Output.Organization.Tooltip"));
		wOutAddressLine1.setToolTipText(getString("Output.AddressLine1.Tooltip"));
		wOutAddressLine2.setToolTipText(getString("Output.AddressLine2.Tooltip"));
		wOutAddressLine3.setToolTipText(getString("Output.AddressLine3.Tooltip"));
		wOutAddressLine4.setToolTipText(getString("Output.AddressLine4.Tooltip"));
		wOutAddressLine5.setToolTipText(getString("Output.AddressLine5.Tooltip"));
		wOutAddressLine6.setToolTipText(getString("Output.AddressLine6.Tooltip"));
		wOutAddressLine7.setToolTipText(getString("Output.AddressLine7.Tooltip"));
		wOutAddressLine8.setToolTipText(getString("Output.AddressLine8.Tooltip"));

		wOutCountryName.setToolTipText(getString("Output.CountryName.Tooltip"));
		wOutDependentLocality.setToolTipText(getString("Output.DependentLocality.Tooltip"));
		wOutLocality.setToolTipText(getString("Output.Locality.Tooltip"));
		wOutAdministrativeArea.setToolTipText(getString("Output.AdminArea.Tooltip"));
		wOutPostalCode.setToolTipText(getString("Output.PostalCode.Tooltip"));
		wOutDeliveryLine.setToolTipText(getString("Output.DeliveryLine.Tooltip"));
		wOutFullAddress.setToolTipText(getString("Output.FormattedAddress.Tooltip"));
		wOutAddressTypeCodes.setToolTipText(getString("Output.USAddressTypeCode.Tooltip"));
		wOutAddressKey.setToolTipText(getString("Output.AddressKey.Tooltip"));

		// Address information group
		setCol0(wOutOrganization);
		setCol0(wOutAddressLine1);
		setCol0(wOutAddressLine2);
		setCol0(wOutAddressLine3);
		setCol0(wOutAddressLine4);
		setCol0(wOutAddressLine5);
		setCol0(wOutAddressLine6);
		setCol0(wOutAddressLine7);
		setCol0(wOutAddressLine8);

		setCol1(wOutCountryName);
		setCol1(wOutDependentLocality);
		setCol1(wOutLocality);
		setCol1(wOutAdministrativeArea);
		setCol1(wOutPostalCode);
		setCol1(wOutDeliveryLine);
		setCol1(wOutFullAddress);
		setCol1(wOutAddressTypeCodes);
		setCol1(wOutAddressKey);
	}

	private void createParsedComp(Composite wSComp) {
		// Create the composite that will hold the contents of the tab
		wParsedComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wParsedComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wParsedComp.setLayout(fl);
		// Description line
		Label description = new Label(wParsedComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("ExtraOutputAddressGroup.Description"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(95, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);

		// Parsed Thoroughfare
		Group parsedThoroughGroup = new Group(wParsedComp, SWT.NONE);
		parsedThoroughGroup.setText(getString("ParsedThoroughFareGroup.Label"));
		parsedThoroughGroup.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		// Parsed Thorofare
		parsedThoroughGroup.setLayoutData(fd);
		wOutPremises = helper.addTextBox(parsedThoroughGroup, null, "AddressTab.Output.Premises");
		wOutPremisesType = helper.addTextBox(parsedThoroughGroup, wOutPremises, "AddressTab.Output.PremisesType");
		wOutPremisesNumber = helper.addTextBox(parsedThoroughGroup, wOutPremisesType, "AddressTab.Output.PremisesNumber");
		wOutThoroughfare = helper.addTextBox(parsedThoroughGroup, wOutPremisesNumber, "AddressTab.Output.Thoroughfare");
		wOutThoroughfareName = helper.addTextBox(parsedThoroughGroup, wOutThoroughfare, "AddressTab.Output.ThoroughfareName");
		wOutThoroughfarePreDirection = helper.addTextBox(parsedThoroughGroup, wOutThoroughfareName, "AddressTab.Output.ThoroughfarePreDirection");
		wOutThoroughfareLeadingType = helper.addTextBox(parsedThoroughGroup, wOutThoroughfarePreDirection, "AddressTab.Output.ThoroughfareLeadingType");
		wOutThoroughfarePostDirection = helper.addTextBox(parsedThoroughGroup, wOutThoroughfareLeadingType, "AddressTab.Output.ThoroughfarePostDirection");
		wOutThoroughfareTrailingType = helper.addTextBox(parsedThoroughGroup, wOutThoroughfarePostDirection, "AddressTab.Output.ThoroughfareTrailingType");
		wOutThoroughfareTypeAttached = helper.addTextBox(parsedThoroughGroup, wOutThoroughfareTrailingType, "AddressTab.Output.ThoroughfareTypeAttached");
		//  tool tips
		wOutPremises.setToolTipText(getString("Output.Premises.Tooltip"));
		wOutPremisesType.setToolTipText(getString("Output.PremisesType.Tooltip"));
		wOutPremisesNumber.setToolTipText(getString("Output.PremisesNumber.Tooltip"));
		wOutThoroughfare.setToolTipText(getString("Output.Thoroughfare.Tooltip"));
		wOutThoroughfareName.setToolTipText(getString("Output.ThoroughfareName.Tooltip"));
		wOutThoroughfarePreDirection.setToolTipText(getString("Output.ThoroughfarePreDirection.Tooltip"));
		wOutThoroughfareLeadingType.setToolTipText(getString("Output.ThoroughfareLeadingType.Tooltip"));
		wOutThoroughfarePostDirection.setToolTipText(getString("Output.ThoroughfarePostDirection.Tooltip"));
		wOutThoroughfareTrailingType.setToolTipText(getString("Output.ThoroughfareTrailingType.Tooltip"));
		wOutThoroughfareTypeAttached.setToolTipText(getString("Output.ThoroughfareTypeAttached.Tooltip"));

		//Parsed Dependent Thoroughfare Columns
		Group parsedDepThoroughGroup = new Group(wParsedComp, SWT.NONE);
		parsedDepThoroughGroup.setText(getString("ParsedDepThoroughFareGroup.Label"));
		parsedDepThoroughGroup.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(parsedThoroughGroup, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		parsedDepThoroughGroup.setLayoutData(fd);
		wOutDependentThoroughfare = helper.addTextBox(parsedDepThoroughGroup, null, "AddressTab.Output.DependentThoroughfare");
		wOutDependentThoroughfareName = helper.addTextBox(parsedDepThoroughGroup, wOutDependentThoroughfare, "AddressTab.Output.DependentThoroughfareName");
		wOutDependentThoroughfareLeadingType = helper.addTextBox(parsedDepThoroughGroup, wOutDependentThoroughfareName, "AddressTab.Output.DependentThoroughfareLeadingType");
		wOutDependentThoroughfarePreDirection = helper.addTextBox(parsedDepThoroughGroup, wOutDependentThoroughfareLeadingType, "AddressTab.Output.DependentThoroughfarePreDirection");
		wOutDependentThoroughfareTrailingType = helper.addTextBox(parsedDepThoroughGroup, wOutDependentThoroughfarePreDirection, "AddressTab.Output.DependentThoroughfareTrailingType");
		wOutDependentThoroughfarePostDirection = helper.addTextBox(parsedDepThoroughGroup, wOutDependentThoroughfareTrailingType, "AddressTab.Output.DependentThoroughfarePostDirection");
		wOutDependentThoroughfareTypeAttached = helper.addTextBox(parsedDepThoroughGroup, wOutDependentThoroughfarePostDirection, "AddressTab.Output.DependentThoroughfareTypeAttached");
		// tool tips
		wOutDependentThoroughfare.setToolTipText(getString("Output.DependentThoroughfare.Tooltip"));
		wOutDependentThoroughfarePreDirection.setToolTipText(getString("Output.DependentThoroughfarePreDirection.Tooltip"));
		wOutDependentThoroughfareLeadingType.setToolTipText(getString("Output.DependentThoroughfareLeadingType.Tooltip"));
		wOutDependentThoroughfareName.setToolTipText(getString("Output.DependentThoroughfareName.Tooltip"));
		wOutDependentThoroughfareTrailingType.setToolTipText(getString("Output.DependentThoroughfareTrailingType.Tooltip"));
		wOutDependentThoroughfarePostDirection.setToolTipText(getString("Output.DependentThoroughfarePostDirection.Tooltip"));
		wOutDependentThoroughfareTypeAttached.setToolTipText(getString("Output.DependentThoroughfareTypeAttached.Tooltip"));

		// Parsed Sub premises
		Group parsedSubPremGroup = new Group(wParsedComp, SWT.NONE);
		parsedSubPremGroup.setText(getString("ParsedSubPremGroup.Label"));
		parsedSubPremGroup.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(description, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		parsedSubPremGroup.setLayoutData(fd);
		wOutBuilding = helper.addTextBox(parsedSubPremGroup, null, "AddressTab.Output.Building");
		wOutSubBuilding = helper.addTextBox(parsedSubPremGroup, wOutBuilding, "AddressTab.Output.SubBuilding");
		wOutSubBuildingNumber = helper.addTextBox(parsedSubPremGroup, wOutSubBuilding, "AddressTab.Output.SubBuildingNumber");
		wOutSubBuildingType = helper.addTextBox(parsedSubPremGroup, wOutSubBuildingNumber, "AddressTab.Output.SubBuildingType");
		wOutSubPremises = helper.addTextBox(parsedSubPremGroup, wOutSubBuildingType, "AddressTab.Output.SubPremises");
		wOutSubPremisesType = helper.addTextBox(parsedSubPremGroup, wOutSubPremises, "AddressTab.Output.SubPremisesType");
		wOutSubPremisesNumber = helper.addTextBox(parsedSubPremGroup, wOutSubPremisesType, "AddressTab.Output.SubPremisesNumber");
		wOutSubPremiseLevel = helper.addTextBox(parsedSubPremGroup, wOutSubPremisesNumber, "AddressTab.Output.SubPremiseLevel");
		wOutSubPremiseLevelType = helper.addTextBox(parsedSubPremGroup, wOutSubPremiseLevel, "AddressTab.Output.SubPremiseLevelType");
		wOutSubPremiseLevelNumber = helper.addTextBox(parsedSubPremGroup, wOutSubPremiseLevelType, "AddressTab.Output.SubPremiseLevelNumber");

		//tool tip
		wOutBuilding.setToolTipText(getString("Output.Building.Tooltip"));
		wOutSubBuilding.setToolTipText(getString("Output.SubBuilding.Tooltip"));
		wOutSubBuildingNumber.setToolTipText(getString("Output.SubBuildingNumber.Tooltip"));
		wOutSubBuildingType.setToolTipText(getString("Output.SubBuildingType.Tooltip"));
		wOutSubPremises.setToolTipText(getString("Output.SubPremises.Tooltip"));
		wOutSubPremisesType.setToolTipText(getString("Output.SubPremisesType.Tooltip"));
		wOutSubPremisesNumber.setToolTipText(getString("Output.SubPremisesNumber.Tooltip"));
		wOutSubPremiseLevel.setToolTipText(getString("Output.SubPremiseLevel.Tooltip"));
		wOutSubPremiseLevelType.setToolTipText(getString("Output.SubPremiseLevelType.Tooltip"));
		wOutSubPremiseLevelNumber.setToolTipText(getString("Output.SubPremiseLevelNumber.Tooltip"));

		for (Control child : wParsedComp.getChildren()) {
			helper.setLook(child);
		}
	}

	private void createExtraOutputComp(Composite wSComp) {
		// Create the composite that will hold the contents of the tab
		wExtraComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wExtraComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wExtraComp.setLayout(fl);
		// Description line
		Label description = new Label(wExtraComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("ExtraOutputAddressGroup.Description"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(95, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);

		//Extra Output Address
		Group extraAddressGroup = new Group(wExtraComp, SWT.NONE);
		extraAddressGroup.setText(getString("ExtraAddressGroup.Label"));
		extraAddressGroup.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		fd.bottom = new FormAttachment(60, -helper.margin);
		extraAddressGroup.setLayoutData(fd);

		wOutCountryFormalName = helper.addTextBox(extraAddressGroup, null, "AddressTab.Output.Country.FormalName");
		wOutCountryCode = helper.addTextBox(extraAddressGroup, wOutCountryFormalName, "AddressTab.Output.Country.Code");
		wOutCountrySubdivisionCode = helper.addTextBox(extraAddressGroup, wOutCountryCode, "AddressTab.Output.Country.SubdivisionCode");
		wOutCountryTimeZone = helper.addTextBox(extraAddressGroup, wOutCountrySubdivisionCode, "AddressTab.Output.Country.TimeZone");
		wOutCountryUTC = helper.addTextBox(extraAddressGroup, wOutCountryTimeZone, "AddressTab.Output.Country.UTC");
		wOutCountryAlpha2 = helper.addTextBox(extraAddressGroup, wOutCountryUTC, "AddressTab.Output.Country.Alpha2");
		wOutCountryAlpha3 = helper.addTextBox(extraAddressGroup, wOutCountryAlpha2, "AddressTab.Output.Country.Alpha3");
		wOutCountryNumeric = helper.addTextBox(extraAddressGroup, wOutCountryAlpha3, "AddressTab.Output.Country.Numeric");
		wOutLatitude = helper.addTextBox(extraAddressGroup, wOutCountryNumeric, "AddressTab.Output.Latitude");
		wOutLongitude = helper.addTextBox(extraAddressGroup, wOutLatitude, "AddressTab.Output.Longitude");
		// Tool tip
		wOutCountryFormalName.setToolTipText(getString("Output.Country.FormalName.Tooltip"));
		wOutCountryCode.setToolTipText(getString("Output.Country.Code.Tooltip"));
		wOutCountrySubdivisionCode.setToolTipText(getString("Output.Country.SubdivisionCode.Tooltip"));
		wOutCountryTimeZone.setToolTipText(getString("Output.Country.TimeZone.Tooltip"));
		wOutCountryUTC.setToolTipText(getString("Output.Country.UTC.Tooltip"));
		wOutCountryAlpha2.setToolTipText(getString("Output.Country.Alpha2.Tooltip"));
		wOutCountryAlpha3.setToolTipText(getString("Output.Country.Alpha3.Tooltip"));
		wOutCountryNumeric.setToolTipText(getString("Output.Country.Numeric.Tooltip"));
		wOutLatitude.setToolTipText(getString("Output.Latitude.Tooltip"));
		wOutLongitude.setToolTipText(getString("Output.Longitude.Tooltip"));

		// Parsed Postal Group
		Group parsedPostalFacGroup = new Group(wExtraComp, SWT.NONE);
		parsedPostalFacGroup.setText(getString("ParsedPostalFacilityGroup.Label"));
		parsedPostalFacGroup.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(description, helper.margin + 4);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(30, -helper.margin);
		parsedPostalFacGroup.setLayoutData(fd);

		wOutPersonalID = helper.addTextBox(parsedPostalFacGroup, null, "AddressTab.Output.PersonalID");
		wOutPostBox = helper.addTextBox(parsedPostalFacGroup, wOutPersonalID, "AddressTab.Output.PostBox");
		wOutPostOfficeLocation = helper.addTextBox(parsedPostalFacGroup, wOutPostBox, "AddressTab.Output.PostOfficeLocation");
		// tool tip
		wOutPersonalID.setToolTipText(getString("Output.PersonalID.Tooltip"));
		wOutPostBox.setToolTipText(getString("Output.PostBox.Tooltip"));
		wOutPostOfficeLocation.setToolTipText(getString("Output.PostOfficeLocation.Tooltip"));

		helper.addSpacer(parsedPostalFacGroup, wOutPostBox);

		// Parsed Regional Group
		Group parsedRegionalColGroup = new Group(wExtraComp, SWT.NONE);
		parsedRegionalColGroup.setText(getString("ParsedRegionalGroup.Label"));
		parsedRegionalColGroup.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(parsedPostalFacGroup, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(60, -helper.margin);
		parsedRegionalColGroup.setLayoutData(fd);

		wOutCountyName = helper.addTextBox(parsedRegionalColGroup, null, "AddressTab.Output.CountyName");
		wOutDoubleDependentLocality = helper.addTextBox(parsedRegionalColGroup, wOutCountyName, "AddressTab.Output.DoubleDependentLocality");
		wOutSubAdministrativeArea = helper.addTextBox(parsedRegionalColGroup, wOutDoubleDependentLocality, "AddressTab.Output.SubAdminArea");
		wOutSubNationalArea = helper.addTextBox(parsedRegionalColGroup, wOutSubAdministrativeArea, "AddressTab.Output.SubNationalArea");
		// tool tips
		wOutCountyName.setToolTipText(getString("Output.CountyName.Tooltip"));
		wOutDoubleDependentLocality.setToolTipText(getString("Output.DoubleDependentLocality.Tooltip"));
		wOutSubAdministrativeArea.setToolTipText(getString("Output.SubAdminArea.Tooltip"));
		wOutSubNationalArea.setToolTipText(getString("Output.SubNationalArea.Tooltip"));

		for (Control child : wExtraComp.getChildren()) {
			helper.setLook(child);
		}
	}

	private int getCountryFieldIndex() {

		int                                countryFieldIndex = -1;
		String                             fieldName         = wInCountry.getValue();
		SortedMap<String, SourceFieldInfo> inFields          = dialog.getSourceFields();

		TransMeta transMeta = dialog.getTransMeta();

		StepMeta stepMeta = transMeta.findStep(dialog.getStepName());
		if (stepMeta != null) {
			try {
				RowMetaInterface row = transMeta.getPrevStepFields(stepMeta);
				for (int i = 0; i < row.size(); i++) {
					String name = row.getValueMeta(i).getName();
					if (name.equals(fieldName)) {
						countryFieldIndex = i;
						break;
					}
				}
			} catch (KettleStepException kse) {
				//Ignore just return the -1
			}
		}
		return countryFieldIndex;
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param metaGlobal
	 * @return
	 */
	@Override
	public boolean init(MDGlobalMeta metaGlobal) {

		this.metaAddress = metaGlobal.getAddrMeta();
		countryUtil = new CountryUtil();
		sCountryList = countryUtil.getSortedSetCountryNames();
		AddressFields addrFields = metaAddress.getAddrFields();

		initInputs(addrFields);
		initOutputs(addrFields);
		initProcessingOptions(addrFields);
		initLocalProcessingCountries(addrFields);

		// Set default country
		for (String country : sCountryList) {
			if (country != null && !country.isEmpty()) {

				((CCombo) wInDefaultCountry[1]).add(country);
			}
		}
		((CCombo) wInDefaultCountry[1]).setText(countryUtil.getCountryName(addrFields.defaultCountry));

		// Set the display options
		for (String opt : addrFields.inputOpts) {
			wDisplayOption.add(opt);
		}
		wDisplayOption.setEditable(false);
		wDisplayOption.setText(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_INPUT_DISPLAY).metaValue);
		displayOption = addrFields.webOptionFields.get(AddressFields.TAG_OPTION_INPUT_DISPLAY).metaValue;

		// Set initial enable
		refreshDisplayInput();
		refreshLocalProcessingTables();
		enable();
		dialog.clearChanged();
		return true;
	}

	private void initInputs(AddressFields addrFields) {

		wOrganization.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ORGANIZATION).metaValue);
		wInAddressLine1.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE1).metaValue);
		wInAddressLine2.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE2).metaValue);
		wInAddressLine3.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE3).metaValue);
		wInAddressLine4.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE4).metaValue);
		wInAddressLine5.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE5).metaValue);
		wInAddressLine6.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE6).metaValue);
		wInAddressLine7.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE7).metaValue);
		wInAddressLine8.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE8).metaValue);
		wInDoubleDependentLocality.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue);
		wInDependentLocality.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_DEPENDENT_LOCALITY).metaValue);
		wInLocality.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_LOCALITY).metaValue);
		wInSubAdministrativeArea.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_SUB_ADMINISTRATIVE_AREA).metaValue);
		wInAdministrativeArea.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADMINISTRATIVE_AREA).metaValue);
		wInSubNationalArea.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_SUB_NATIONAL_AREA).metaValue);
		wInPostalCode.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_POSTAL_CODE).metaValue);
		wInCountry.setValue(addrFields.inputFields.get(AddressFields.TAG_INPUT_COUNTRY).metaValue);
	}

	private void initOutputs(AddressFields addrFields) {

		wOutOrganization.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ORGANIZATION).metaValue);
		wOutAddressLine1.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue);
		wOutAddressLine2.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE2).metaValue);
		wOutAddressLine3.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE3).metaValue);
		wOutAddressLine4.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE4).metaValue);
		wOutAddressLine5.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE5).metaValue);
		wOutAddressLine6.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE6).metaValue);
		wOutAddressLine7.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE7).metaValue);
		wOutAddressLine8.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE8).metaValue);

		wOutCountryName.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_NAME).metaValue);
		wOutDependentLocality.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_LOCALITY).metaValue);
		wOutLocality.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LOCALITY).metaValue);
		wOutAdministrativeArea.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADMINISTRATIVE_AREA).metaValue);
		wOutPostalCode.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTAL_CODE).metaValue);
		wOutDeliveryLine.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DELIVERY_LINE).metaValue);
		wOutFullAddress.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_FORMATED_ADDRESS).metaValue);
		wOutAddressTypeCodes.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_TYPE).metaValue);
		wOutAddressKey.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_KEY).metaValue);

		//Parsed Thoroughfare
		wOutPremises.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES).metaValue);
		wOutPremisesType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_TYPE).metaValue);
		wOutPremisesNumber.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_NUMBER).metaValue);
		wOutThoroughfare.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE).metaValue);
		wOutThoroughfareName.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE_NAME).metaValue);
		wOutThoroughfarePreDirection.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PRE_DIRECTION).metaValue);
		wOutThoroughfareLeadingType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LEADING_TYPE).metaValue);
		wOutThoroughfarePostDirection.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_DIRECTION).metaValue);
		wOutThoroughfareTrailingType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TRAILING_TYPE).metaValue);
		wOutThoroughfareTypeAttached.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TYPE_ATTACHED).metaValue);

		// parsed dependant thoroughfare
		wOutDependentThoroughfare.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE).metaValue);
		wOutDependentThoroughfareName.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME).metaValue);
		wOutDependentThoroughfareLeadingType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE).metaValue);
		wOutDependentThoroughfarePreDirection.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).metaValue);
		wOutDependentThoroughfareTrailingType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE).metaValue);
		wOutDependentThoroughfarePostDirection.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).metaValue);
		wOutDependentThoroughfareTypeAttached.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TYPE_ATTACHED).metaValue);

		// parsed sub premises
		wOutBuilding.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_BUILDING).metaValue);
		wOutSubBuilding.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING).metaValue);
		wOutSubBuildingNumber.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_NUMBER).metaValue);
		wOutSubBuildingType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_TYPE).metaValue);
		wOutSubPremises.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES).metaValue);
		wOutSubPremisesType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_TYPE).metaValue);
		wOutSubPremisesNumber.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_NUMBER).metaValue);
		wOutSubPremiseLevel.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL).metaValue);
		wOutSubPremiseLevelType.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_TYPE).metaValue);
		wOutSubPremiseLevelNumber.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_NUMBER).metaValue);

		// Extra address
		wOutCountryFormalName.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_FORMAL_NAME).metaValue);
		wOutCountryCode.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_CODE).metaValue);

		wOutCountrySubdivisionCode.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE).metaValue);
		wOutCountryTimeZone.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_TIMEZONE).metaValue);
		wOutCountryUTC.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_UTC).metaValue);

		wOutCountryAlpha2.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA2).metaValue);
		wOutCountryAlpha3.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA3).metaValue);
		wOutCountryNumeric.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_NUMERIC).metaValue);
		wOutLatitude.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LATITUDE).metaValue);
		wOutLongitude.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LONGITUDE).metaValue);

		// Parsed Postal
		wOutPersonalID.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PERSONAL_ID).metaValue);
		wOutPostBox.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTBOX).metaValue);
		wOutPostOfficeLocation.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_OFFICE_LOCATION).metaValue);

		// parsed Regional
		wOutCountyName.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTY_NAME).metaValue);
		wOutDoubleDependentLocality.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue);
		wOutSubAdministrativeArea.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA).metaValue);
		wOutSubNationalArea.setText(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_NATIONAL_AREA).metaValue);
	}

	private void initProcessingOptions(AddressFields addrFields) {

		for (String lineSeparatorOption : lineSeparatorOptions) {
			((CCombo) wOptLineSeparator[1]).add(lineSeparatorOption);
		}
		for (String outputScriptOption : webOutputScriptOptions) {
			((CCombo) wOptOutputScript[1]).add(outputScriptOption);
		}
		for (String deliveryLineOption : deliveryLineOptions) {
			((CCombo) wWebOptDeliveryLine[1]).add(deliveryLineOption);
		}
		((CCombo) wCountryOfOrigion[1]).add("");
		for (String country : sCountryList) {
			if (country != null && !country.isEmpty()) {
				((CCombo) wCountryOfOrigion[1]).add(country);
			}
		}

		if (!Const.isEmpty(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_OUTPUT_SCRIPT).metaValue)) {
			((CCombo) wOptOutputScript[1]).setText(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_OUTPUT_SCRIPT).metaValue);
		}
		if (!Const.isEmpty(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_LINE_SEPARATOR).metaValue)) {
			((CCombo) wOptLineSeparator[1]).setText(getLineSeperator(metaAddress));
		}
		if (!Const.isEmpty(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN).metaValue)) {
			((CCombo) wCountryOfOrigion[1]).setText(countryUtil.getCountryName(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN).metaValue));
		}
		if (!Const.isEmpty(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_DELIVERY_LINE).metaValue)) {
			((CCombo) wWebOptDeliveryLine[1]).setText(addrFields.webOptionFields.get(AddressFields.TAG_OPTION_DELIVERY_LINE).metaValue);
		}

		setProcessType(addrFields.getProcessType());

		bnAddCodes.setSelection(addrFields.addAdditionalCode);
	}

	private void initLocalProcessingCountries(AddressFields addrFields) {

		for (String country : sCountryList) {
			// add to list of available on premises processing countries
			if (country != null && !country.isEmpty()) {
				sAvailable.add(country);
			}
		}

		for (int i : addrFields.getOpSelectedCountriesIndex()) {
			// Add to the list of countries for on premises processing
			sSelected.add(countryUtil.getCountryNameFromIndex(i));
		}
		// Clean up Available
		for (String c : sSelected) {
			sAvailable.remove(c);
		}
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	@Override
	public void getData(MDGlobalMeta metaGlobal) {

		AddressMeta metaAddress = metaGlobal.getAddrMeta();
		AddressFields addrFields = metaAddress.getAddrFields();
		// Fill it in
		getDataInput(addrFields);
		getDataOutput(addrFields);
		getDataProcessingOptions(addrFields);
		getDataLocalProcessingCountries(addrFields);

		// Input display option
		addrFields.webOptionFields.get(AddressFields.TAG_OPTION_INPUT_DISPLAY).metaValue = wDisplayOption.getText();
		// index of the input field that holds the country value
		addrFields.setCountryFieldIndex(getCountryFieldIndex());
	}

	private void getDataInput(AddressFields addrFields) {

		addrFields.inputFields.get(AddressFields.TAG_INPUT_ORGANIZATION).metaValue = wOrganization.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE1).metaValue = wInAddressLine1.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE2).metaValue = wInAddressLine2.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE3).metaValue = wInAddressLine3.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE4).metaValue = wInAddressLine4.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE5).metaValue = wInAddressLine5.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE6).metaValue = wInAddressLine6.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE7).metaValue = wInAddressLine7.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE8).metaValue = wInAddressLine8.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue = wInDoubleDependentLocality.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_DEPENDENT_LOCALITY).metaValue = wInDependentLocality.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_LOCALITY).metaValue = wInLocality.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_SUB_ADMINISTRATIVE_AREA).metaValue = wInSubAdministrativeArea.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_ADMINISTRATIVE_AREA).metaValue = wInAdministrativeArea.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_SUB_NATIONAL_AREA).metaValue = wInSubNationalArea.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_POSTAL_CODE).metaValue = wInPostalCode.getValue();
		addrFields.inputFields.get(AddressFields.TAG_INPUT_COUNTRY).metaValue = wInCountry.getValue();
	}

	private void getDataOutput(AddressFields addrFields) {

		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ORGANIZATION).metaValue = wOutOrganization.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue = wOutAddressLine1.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE2).metaValue = wOutAddressLine2.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE3).metaValue = wOutAddressLine3.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE4).metaValue = wOutAddressLine4.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE5).metaValue = wOutAddressLine5.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE6).metaValue = wOutAddressLine6.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE7).metaValue = wOutAddressLine7.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE8).metaValue = wOutAddressLine8.getText();

		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_NAME).metaValue = wOutCountryName.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_LOCALITY).metaValue = wOutDependentLocality.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LOCALITY).metaValue = wOutLocality.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADMINISTRATIVE_AREA).metaValue = wOutAdministrativeArea.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTAL_CODE).metaValue = wOutPostalCode.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DELIVERY_LINE).metaValue = wOutDeliveryLine.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_FORMATED_ADDRESS).metaValue = wOutFullAddress.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_TYPE).metaValue = wOutAddressTypeCodes.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_KEY).metaValue = wOutAddressKey.getText();

		// parsed thoroughfare
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES).metaValue = wOutPremises.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_TYPE).metaValue = wOutPremisesType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_NUMBER).metaValue = wOutPremisesNumber.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE).metaValue = wOutThoroughfare.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE_NAME).metaValue = wOutThoroughfareName.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PRE_DIRECTION).metaValue = wOutThoroughfarePreDirection.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LEADING_TYPE).metaValue = wOutThoroughfareLeadingType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_DIRECTION).metaValue = wOutThoroughfarePostDirection.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TRAILING_TYPE).metaValue = wOutThoroughfareTrailingType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TYPE_ATTACHED).metaValue = wOutThoroughfareTypeAttached.getText();

		// parsed dependant thoroughfare
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE).metaValue = wOutDependentThoroughfare.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME).metaValue = wOutDependentThoroughfareName.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE).metaValue = wOutDependentThoroughfareLeadingType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).metaValue = wOutDependentThoroughfarePreDirection.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE).metaValue = wOutDependentThoroughfareTrailingType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).metaValue = wOutDependentThoroughfarePostDirection.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TYPE_ATTACHED).metaValue = wOutDependentThoroughfareTypeAttached.getText();

		//parsed sub premises
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_BUILDING).metaValue = wOutBuilding.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING).metaValue = wOutSubBuilding.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_NUMBER).metaValue = wOutSubBuildingNumber.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_TYPE).metaValue = wOutSubBuildingType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES).metaValue = wOutSubPremises.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_TYPE).metaValue = wOutSubPremisesType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_NUMBER).metaValue = wOutSubPremisesNumber.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL).metaValue = wOutSubPremiseLevel.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_TYPE).metaValue = wOutSubPremiseLevelType.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_NUMBER).metaValue = wOutSubPremiseLevelNumber.getText();

		// Extra address
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_FORMAL_NAME).metaValue = wOutCountryFormalName.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_CODE).metaValue = wOutCountryCode.getText();

		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE).metaValue = wOutCountrySubdivisionCode.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_TIMEZONE).metaValue = wOutCountryTimeZone.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_UTC).metaValue = wOutCountryUTC.getText();

		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA2).metaValue = wOutCountryAlpha2.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA3).metaValue = wOutCountryAlpha3.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_NUMERIC).metaValue = wOutCountryNumeric.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LATITUDE).metaValue = wOutLatitude.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LONGITUDE).metaValue = wOutLongitude.getText();

		// Parsed Postal
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PERSONAL_ID).metaValue = wOutPersonalID.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTBOX).metaValue = wOutPostBox.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_OFFICE_LOCATION).metaValue = wOutPostOfficeLocation.getText();

		// parsed Regional
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTY_NAME).metaValue = wOutCountyName.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue = wOutDoubleDependentLocality.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA).metaValue = wOutSubAdministrativeArea.getText();
		addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_NATIONAL_AREA).metaValue = wOutSubNationalArea.getText();
	}

	private void getDataProcessingOptions(AddressFields addrFields) {
		// get default country
		addrFields.defaultCountry = countryUtil.getCountryISO3(((CCombo) wInDefaultCountry[1]).getText());
		// Country of Origion
		addrFields.webOptionFields.get(AddressFields.TAG_OPTION_COUNTRY_OF_ORIGIN).metaValue = countryUtil.getCountryISO3(((CCombo) wCountryOfOrigion[1]).getText());
		//
		addrFields.webOptionFields.get(AddressFields.TAG_OPTION_LINE_SEPARATOR).metaValue = setLineSeperator(((CCombo) wOptLineSeparator[1]).getText());
		addrFields.webOptionFields.get(AddressFields.TAG_OPTION_OUTPUT_SCRIPT).metaValue = ((CCombo) wOptOutputScript[1]).getText();
		addrFields.webOptionFields.get(AddressFields.TAG_OPTION_DELIVERY_LINE).metaValue = ((CCombo) wWebOptDeliveryLine[1]).getText();
		addrFields.setProcessType(getProcessType());

		if(bnAddCodes.isEnabled()) {
			addrFields.addAdditionalCode = bnAddCodes.getSelection();
		} else {
			addrFields.addAdditionalCode = false;
		}
	}

	private void getDataLocalProcessingCountries(AddressFields addrFields) {
		// get countries for on premises processing
		addrFields.getOpSelectedCountriesIndex().clear();
		for (String s : sSelected) {
			addrFields.getOpSelectedCountriesIndex().add(countryUtil.getCountryIndex(s));
		}
	}

	/**
	 * Sets the input fields to show depending on selection.
	 * FullGlobal, Simplified Global, US
	 */
	private void refreshDisplayInput() {

		for (Control con : addressGroup.getChildren()) {
			con.setVisible(true);
		}

		if (metaAddress.getAddrFields().inputOpts[0].equals(displayOption)/* US Address */) {

			wInAddressLine4.setVisible(false);
			wInAddressLine5.setVisible(false);
			wInAddressLine6.setVisible(false);
			wInAddressLine7.setVisible(false);
			wInAddressLine8.setVisible(false);

			wInDoubleDependentLocality.setVisible(false);
			wInDependentLocality.setVisible(false);
			wInSubAdministrativeArea.setVisible(false);
			wInSubNationalArea.setVisible(false);

			wInLocality.getLabel().setText("City:");
			((FormData) wInLocality.getLabel().getLayoutData()).top = null;
			((FormData) wInLocality.getComboBox().getLayoutData()).top = null;

			wInAdministrativeArea.getLabel().setText("State:");
			((FormData) wInAdministrativeArea.getLabel().getLayoutData()).top = new FormAttachment(wInLocality.getComboBox(), helper.margin);
			((FormData) wInAdministrativeArea.getComboBox().getLayoutData()).top = new FormAttachment(wInLocality.getComboBox(), helper.margin);

			((FormData) wInPostalCode.getLabel().getLayoutData()).top = new FormAttachment(wInAdministrativeArea.getComboBox(), helper.margin);
			((FormData) wInPostalCode.getComboBox().getLayoutData()).top = new FormAttachment(wInAdministrativeArea.getComboBox(), helper.margin);
		} else if (metaAddress.getAddrFields().inputOpts[1].equals(displayOption) /* Simplified International*/) {

			wInAddressLine4.setVisible(false);
			wInAddressLine5.setVisible(false);
			wInAddressLine6.setVisible(false);
			wInAddressLine7.setVisible(false);
			wInAddressLine8.setVisible(false);

			wInDoubleDependentLocality.setVisible(false);
			wInSubAdministrativeArea.setVisible(false);
			wInSubNationalArea.setVisible(false);

			((FormData) wInDependentLocality.getLabel().getLayoutData()).top = null;
			((FormData) wInDependentLocality.getComboBox().getLayoutData()).top = null;

			wInLocality.getLabel().setText("Locality:");
			((FormData) wInLocality.getLabel().getLayoutData()).top = new FormAttachment(wInDependentLocality.getComboBox(), helper.margin);
			((FormData) wInLocality.getComboBox().getLayoutData()).top = new FormAttachment(wInDependentLocality.getComboBox(), helper.margin);

			wInAdministrativeArea.getLabel().setText("Administrative Area:");
			((FormData) wInAdministrativeArea.getLabel().getLayoutData()).top = new FormAttachment(wInLocality.getComboBox(), helper.margin);
			((FormData) wInAdministrativeArea.getComboBox().getLayoutData()).top = new FormAttachment(wInLocality.getComboBox(), helper.margin);

			((FormData) wInPostalCode.getLabel().getLayoutData()).top = new FormAttachment(wInAdministrativeArea.getComboBox(), helper.margin);
			((FormData) wInPostalCode.getComboBox().getLayoutData()).top = new FormAttachment(wInAdministrativeArea.getComboBox(), helper.margin);
		} else if (metaAddress.getAddrFields().inputOpts[2].equals(displayOption)/* International*/) {

			((FormData) wInDoubleDependentLocality.getLabel().getLayoutData()).top = null;
			((FormData) wInDoubleDependentLocality.getComboBox().getLayoutData()).top = null;

			((FormData) wInDependentLocality.getLabel().getLayoutData()).top = new FormAttachment(wInDoubleDependentLocality.getComboBox(), helper.margin);
			;
			((FormData) wInDependentLocality.getComboBox().getLayoutData()).top = new FormAttachment(wInDoubleDependentLocality.getComboBox(), helper.margin);
			;

			wInLocality.getLabel().setText("Locality:");
			((FormData) wInLocality.getLabel().getLayoutData()).top = new FormAttachment(wInDependentLocality.getComboBox(), helper.margin);
			((FormData) wInLocality.getComboBox().getLayoutData()).top = new FormAttachment(wInDependentLocality.getComboBox(), helper.margin);

			wInAdministrativeArea.getLabel().setText("Administrative Area:");
			((FormData) wInAdministrativeArea.getLabel().getLayoutData()).top = new FormAttachment(wInSubAdministrativeArea.getComboBox(), helper.margin);
			((FormData) wInAdministrativeArea.getComboBox().getLayoutData()).top = new FormAttachment(wInSubAdministrativeArea.getComboBox(), helper.margin);

			((FormData) wInPostalCode.getLabel().getLayoutData()).top = new FormAttachment(wInSubNationalArea.getComboBox(), helper.margin);
			((FormData) wInPostalCode.getComboBox().getLayoutData()).top = new FormAttachment(wInSubNationalArea.getComboBox(), helper.margin);
		}
	}

	private void doNextBack(boolean doNext) {

		if (doNext) {
			if (pgNum < 4) {
				pgNum += 1;
			}
		} else {
			if (pgNum > 1) {
				pgNum -= 1;
			}
		}
		wInputComp.setVisible(false);
		wOutputComp.setVisible(false);
		wParsedComp.setVisible(false);
		wExtraComp.setVisible(false);
		bnNext.setVisible(false);
		bnBack.setVisible(false);
		switch (pgNum) {
			case 1:
				wInputComp.setVisible(true);
				bnNext.setVisible(true);
				break;
			case 2:
				wOutputComp.setVisible(true);
				bnNext.setVisible(true);
				bnBack.setVisible(true);
				break;
			case 3:
				wParsedComp.setVisible(true);
				bnNext.setVisible(true);
				bnBack.setVisible(true);
				break;
			case 4:
				wExtraComp.setVisible(true);
				bnBack.setVisible(true);
				break;
		}

		dialog.updatePluginDocumintation();
	}

	/**
	 * Called to handle enable of controls based on input settings
	 */
	public void enable() {

		if(addressGroup.isDisposed()){
			return;
		}

		boolean isLicensed = dialog.getInput().getAddrMeta().isLicensed();
		if (!isLicensed) {
			addressGroup.setText(getString("InputAddressGroup.NoLicensed.Label"));
			for (Control child : addressGroup.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : addressOptionsGroup.getChildren()) {
				child.setEnabled(false);
			}
			bnNext.setEnabled(false);
		} else if (!hasMinInput()) {
			addressGroup.setText(getString("InputAddressGroup.Label"));
			for (Control child : addressGroup.getChildren()) {
				child.setEnabled(true);
			}
			addressOptionsGroup.setText(getString("AddressOptionsGroup.MissingInput.Label"));
			for (Control child : addressOptionsGroup.getChildren()) {
				child.setEnabled(false);
			}
			bnNext.setEnabled(false);
		} else {
			addressGroup.setText(getString("InputAddressGroup.Label"));
			for (Control child : addressGroup.getChildren()) {
				child.setEnabled(true);
			}
			addressOptionsGroup.setText(getString("AddressOptionsGroup.Label"));
			for (Control child : addressOptionsGroup.getChildren()) {
				child.setEnabled(true);
			}
			bnNext.setEnabled(true);
		}

		if ( bnVaried.isEnabled() && bnVaried.getSelection() && !wInCountry.getValue().isEmpty()) {
			tableAvailible.setEnabled(true);
			tableSelected.setEnabled(true);
			bnAddCodes.setEnabled(true);
		} else {
			tableAvailible.setEnabled(false);
			tableSelected.setEnabled(false);
			bnAddCodes.setEnabled(false);
		}

		addressGroup.layout();
	}

	/**
	 * @return The URL for help information
	 */
	@Override
	public String getHelpURLKey() {

		if (pgNum == 1) {
			return BaseMessages.getString(PKG, "MDGlobalVerify.Help.AddressTab.Input");
		} else if (pgNum == 2) {
			return BaseMessages.getString(PKG, "MDGlobalVerify.Help.AddressTab.Output");
		} else if (pgNum == 3) {
			return BaseMessages.getString(PKG, "MDGlobalVerify.Help.AddressTab.AdditionalOutput");
		} else {
			return BaseMessages.getString(PKG, "MDGlobalVerify.Help.AddressTab.General");
		}
	}

	private String getLineSeperator(AddressMeta metaGlobalAddress) {

		String lineSeperator = metaGlobalAddress.getAddrFields().webOptionFields.get(AddressFields.TAG_OPTION_LINE_SEPARATOR).metaValue;
		// "SEMICOLON(;)","PIPE(|)","Carriage Return(CR)","Line Feed(LF)","Carriage Return & Line Feed(CRLF)","TAB","HTML Break(<BR>)"
		if ("SEMICOLON".equals(lineSeperator)) {
			lineSeperator = "SEMICOLON(;)";
		} else if ("PIPE".equals(lineSeperator)) {
			lineSeperator = "PIPE(|)";
		} else if ("CR".equals(lineSeperator)) {
			lineSeperator = "Carriage Return(CR)";
		} else if ("LF".equals(lineSeperator)) {
			lineSeperator = "Line Feed(LF)";
		} else if ("CRLF".equals(lineSeperator)) {
			lineSeperator = "Carriage Return & Line Feed(CRLF)";
		} else if ("TAB".equals(lineSeperator)) {
			lineSeperator = "TAB";
		} else if ("BR".equals(lineSeperator)) {
			lineSeperator = "HTML Break(<BR>)";
		} else {
			lineSeperator = "SEMICOLON(;)";
		}
		return lineSeperator;
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDGlobalDialog.AddressTab." + key, args);
	}

	boolean hasMinInput() {

		if (!Const.isEmpty(wInAddressLine1.getValue())) {
			return true;
		}

		return false;
	}

	private void setCol0(Text wComp) {

		((FormData) wComp.getLayoutData()).left = new FormAttachment(10, helper.margin);
		((FormData) wComp.getLayoutData()).right = new FormAttachment(40, helper.margin);
	}

	private void setCol1(Text wComp) {

		((FormData) wComp.getLayoutData()).left = new FormAttachment(55, helper.margin);
		((FormData) wComp.getLayoutData()).right = new FormAttachment(85, helper.margin);
	}

	private void setLeftRight(Control control, int left, int right) {

		((FormData) control.getLayoutData()).left = new FormAttachment(left, helper.margin);
		((FormData) control.getLayoutData()).right = new FormAttachment(right, -helper.margin);
	}

	private void setOptionsColumn(Control[] controls, String toolTip) {

		controls[1].setToolTipText(getString(toolTip));
		//Label
		setLeftRight(controls[0], 50, 60);
		// Combo
		((FormData) controls[1].getLayoutData()).left = new FormAttachment(controls[0], helper.margin);
		((FormData) controls[1].getLayoutData()).right = new FormAttachment(100, -helper.margin);
	}

	private void setLeftColumn(MDInputCombo inputCombo, int leftSide, int rightSide) {

		((FormData) inputCombo.getLabel().getLayoutData()).left = new FormAttachment(0, helper.margin);
		((FormData) inputCombo.getLabel().getLayoutData()).right = new FormAttachment(leftSide, helper.margin);
		((FormData) inputCombo.getComboBox().getLayoutData()).left = new FormAttachment(leftSide, 2 * helper.margin);
		((FormData) inputCombo.getComboBox().getLayoutData()).right = new FormAttachment(rightSide, helper.margin);
	}

	private void setRightColumn(MDInputCombo inputCombo, int leftSide, int rightSide) {

		((FormData) inputCombo.getLabel().getLayoutData()).left = new FormAttachment(leftSide, helper.margin);
		((FormData) inputCombo.getLabel().getLayoutData()).right = new FormAttachment(rightSide, helper.margin);
		((FormData) inputCombo.getComboBox().getLayoutData()).left = new FormAttachment(rightSide, 2 * helper.margin);
	}

	private String setLineSeperator(String lineSeperator) {

		if ("SEMICOLON(;)".equals(lineSeperator)) {
			lineSeperator = "SEMICOLON";
		} else if ("PIPE(|)".equals(lineSeperator)) {
			lineSeperator = "PIPE";
		} else if ("Carriage Return(CR)".equals(lineSeperator)) {
			lineSeperator = "CR";
		} else if ("Line Feed(LF)".equals(lineSeperator)) {
			lineSeperator = "LF";
		} else if ("Carriage Return & Line Feed(CRLF)".equals(lineSeperator)) {
			lineSeperator = "CRLF";
		} else if ("TAB".equals(lineSeperator)) {
			lineSeperator = "TAB";
		} else if ("HTML Break(<BR>)".equals(lineSeperator)) {
			lineSeperator = "BR";
		} else {
			MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(BaseMessages.getString(PKG, "MDGlobalAddressDialog.WarningDialog.Title", Const.CR));
			box.setMessage(BaseMessages.getString(PKG, "MDGlobalAddressDialog.WarningDialogLineSeperator.Message", Const.CR));
			box.open();
			lineSeperator = ";";
		}
		return lineSeperator;
	}

	private void setProcessType(String type) {

		if (type.equals(AddressFields.TAG_PROCESS_LOCAL)) {
			bnLocal.setSelection(true);
			bnWeb.setSelection(false);
			bnVaried.setSelection(false);
		} else if (type.equals(AddressFields.TAG_PROCESS_WEB)) {
			bnLocal.setSelection(false);
			bnWeb.setSelection(true);
			bnVaried.setSelection(false);
		} else if (type.equals(AddressFields.TAG_PROCESS_VARIED)) {
			bnLocal.setSelection(false);
			bnWeb.setSelection(false);
			bnVaried.setSelection(true);
		}
	}

	private String getProcessType() {

		if (bnLocal.getSelection()) {
			return AddressFields.TAG_PROCESS_LOCAL;
		} else if (bnWeb.getSelection()) {
			return AddressFields.TAG_PROCESS_WEB;
		} else if (bnVaried.getSelection()) {
			return AddressFields.TAG_PROCESS_VARIED;
		}

		return "ERROR";
	}
}
