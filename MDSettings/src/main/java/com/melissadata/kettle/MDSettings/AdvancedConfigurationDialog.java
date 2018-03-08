package com.melissadata.kettle.MDSettings;

import com.melissadata.cz.support.MDPropTags;
import com.melissadata.cz.ui.BrowserDialog;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDDialogParent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.util.ImageUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

public class AdvancedConfigurationDialog extends MDAbstractDialog {

	private static       Class<?> PKG            = AdvancedConfigurationDialog.class;
	private static final String   LICENSE_NEEDED = BaseMessages.getString(PKG, "MDSettings.LicenseNeeded");
	//	private static final String		TAG_CONTACTZONE	= "contact_zone";
	private CTabFolder        wTabFolder;
	private Composite         wLicenseComp;
	private LocalTab          localTab;
	private WebTab            webTab;
	private LocalApplianceTab localApplianceTab;
	private Text              wPrimaryLicense;
	private Text              wPrimaryExpiration;
	private Text              wPrimaryCustID;
	//	private Text					wTrialLicense;
//	private Text					wTrialExpiration;
//	private Text					wTrialCustID;
	// Licensing Labels
	private Label             lNameLicensed;
	private Label             lAddressLicensed;
	private Label             lAddrName;
	private Label             lMUname;
	private Label             lGeoName;
	private Label             lGeoLicensed;
	private Label             lPhoneLicensed;
	private Label             lEmailLicensed;
	private Label             lRBDILicensed;
	private Label             lProfilerLicensed;
	private Label             lPropertyLicensed;
	private Label             lSmartMoverLicensed;
	private Label             lPresortLicensed;
	private Label             lMatchUpLicensed;
	private Label             lIpLocatorLicensed;
	private Label             lGlobalVerifyLicensed;
	private Label             lPersonatorLicensed;
	private Label             lPersonatorWorldLicensed;
	private Label             lBusinessCoderLicensed;
	private Label             lCleanserLicensed;
	private Label             lVersion;
	private Button            wShowDetails;
	private SettingsHelper    helper;
	private boolean           syncingFields;
	private boolean           isPersonator;
	private boolean           isPersonatorWorld;
	private boolean           isSmartMover;
	private boolean           isBusinessCoder;
	private String dialogTitle = "";
	private boolean                 changedWebProp;
	private AdvancedConfigInterface acInterface;

	public AdvancedConfigurationDialog(Shell parent, MDDialogParent dialog, AdvancedConfigInterface acInterface, String menuID) {

		super(parent, dialog, acInterface, SWT.NONE);
	}

	private void addPrimaryLicenseControls(Composite parent) {
		// License
		Label lLicense = new Label(parent, SWT.RIGHT);
		lLicense.setText(getString("License.Label")); //$NON-NLS-1$
		props.setLook(lLicense);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(null, margin);
		lLicense.setLayoutData(fd);
		wPrimaryLicense = new Text(parent, SWT.LEFT | SWT.BORDER);
		props.setLook(wPrimaryLicense);
		// TODO put into messages
		ControlDecoration controlDecoration = new ControlDecoration(wPrimaryLicense, SWT.TOP | SWT.RIGHT);
		controlDecoration.setImage(ImageUtil.getImage(display, this.getClass(), "com/melissadata/kettle/MDSettings/images/question-mark-small.png"));
		controlDecoration.setDescriptionText(getString("PrimaryHint"));
		fd = new FormData();
		fd.left = new FormAttachment(lLicense, margin);
		fd.top = new FormAttachment(null, margin);
		fd.right = new FormAttachment(100, -margin);
		fd.right.offset -= 20;
		wPrimaryLicense.setLayoutData(fd);
		// Customer ID
		Label lCustID = new Label(parent, SWT.RIGHT);
		lCustID.setText(getString("CustID.Label")); //$NON-NLS-1$
		props.setLook(lCustID);
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wPrimaryLicense, margin);
		fd.right = new FormAttachment(wPrimaryLicense, -margin);
		lCustID.setLayoutData(fd);
		wPrimaryCustID = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wPrimaryCustID);
		fd = new FormData();
		fd.left = new FormAttachment(lCustID, margin);
		fd.top = new FormAttachment(wPrimaryLicense, margin);
		wPrimaryCustID.setLayoutData(fd);
		Link link = new Link(parent, SWT.LEFT);
		// TODO put into messages
		link.setText("To provision a new license string visit Melissa Data's sign up page at: <a href=\"http://www.melissadata.com/free-trials/pentaho.htm\">www.melissadata.com/free-trials/pentaho.htm</a>");
		props.setLook(link);
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wPrimaryCustID, 2 * margin);
		link.setLayoutData(fd);
		link.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				String url = "http://www.melissadata.com/free-trials/pentaho.htm";
				BrowserDialog.displayURL(getShell(), dialog, url);
			}
		});
		// Expiration
		Label lExpiration = new Label(parent, SWT.RIGHT);
		lExpiration.setText(getString("LicenseExp.Label")); //$NON-NLS-1$
		props.setLook(lExpiration);
		fd = new FormData();
		fd.left = new FormAttachment(wPrimaryCustID, 5 * margin);
		fd.top = new FormAttachment(wPrimaryLicense, margin);
		lExpiration.setLayoutData(fd);
		wPrimaryExpiration = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wPrimaryExpiration);
		fd = new FormData();
		fd.left = new FormAttachment(lExpiration, margin);
		fd.top = new FormAttachment(wPrimaryLicense, margin);
		wPrimaryExpiration.setLayoutData(fd);
		// Inform dialog when something changes
		wPrimaryLicense.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {

				updatePrimeLicenseInfo();
				setChanged();
			}
		});
		// Handle CR
		wPrimaryLicense.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {

				ok();
			}
		});
		// Select all text when focus gained
		wPrimaryLicense.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {

				wPrimaryLicense.selectAll();
			}
		});
	}

	/**
	 * Called to handle cancelation of dialog actions
	 *
	 * @return
	 */
	@Override
	protected boolean cancel() {
		// If something changed the confirm that they want to cancel
		if (changed) {
			MessageBox box = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(getAbsoluteString("MDSettings.WarningDialogChanged.Title"));
			box.setMessage(getAbsoluteString("MDSettings.WarningDialogChanged.Message", Const.CR));
			if (box.open() == SWT.NO) {
				return false;
			}
		}
		// Cancel change
		changed = false;
		// Close the dialog
		dispose();
		return true;
	}

	@Override
	protected void createContents(Composite parent, Object data) {

		acInterface = (AdvancedConfigInterface) data;
		helper = new SettingsHelper(this);
		dialogTitle = "";
		if (MDPropTags.MENU_ID_PERSONATOR.equals(acInterface.getMenuID())) {
			isPersonator = true;
		} else {
			isPersonator = false;
		}
		if (MDPropTags.MENU_ID_PERSONATOR_WORLD.equals(acInterface.getMenuID())) {
			isPersonatorWorld = true;
		} else {
			isPersonatorWorld = false;
		}
		if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
			isSmartMover = true;
		} else {
			isSmartMover = false;
		}
		// Get dialog title from the menu ID
		if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
			dialogTitle = "Smart Mover Component";
		}
		if (MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
			dialogTitle = "Global Verify Component";
		}
		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID())) {
			dialogTitle = "Match Up Component";
		}
		if (MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			dialogTitle = "Match Up Global Component";
		}
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			dialogTitle = "Presort Component";
		}
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID())) {
			dialogTitle = "Contact Verify Component";
		}
		if (MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID())) {
			dialogTitle = "Ip Locator Component";
		}
		if (MDPropTags.MENU_ID_PERSONATOR.equals(acInterface.getMenuID())) {
			dialogTitle = "Personator Component";
		}
		if (MDPropTags.MENU_ID_PERSONATOR_WORLD.equals(acInterface.getMenuID())) {
			dialogTitle = "Personator World Component";
		}
		if (MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID())) {
			dialogTitle = "Profiler Component";
		}
		if (MDPropTags.MENU_ID_PROPERTY.equals(acInterface.getMenuID())) {
			dialogTitle = "Property Component";
		}
		if (MDPropTags.MENU_ID_BUSINESS_CODER.equals(acInterface.getMenuID())) {
			dialogTitle = "Business Coder Component";
			isBusinessCoder = true;
		}
		if (MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
			dialogTitle = "Cleanser Component";
		}

		acInterface.setDataValues();
		wLicenseComp = createLicenseComp(parent, null);

		if (!MDPropTags.MENU_ID_LICENSE.equals(acInterface.getMenuID())) {
			// Create the tab folder
			wTabFolder = new CTabFolder(parent, SWT.BORDER);
			props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
			wTabFolder.setSimple(false);
			if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_BUSINESS_CODER.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROPERTY.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PERSONATOR
					.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PERSONATOR_WORLD.equals(acInterface.getMenuID())) {
				webTab = new WebTab(this);
			} else if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROFILER
					.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
				localTab = new LocalTab(this);
			} else {
				localTab = new LocalTab(this);
				webTab = new WebTab(this);
				if (!MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
					localApplianceTab = new LocalApplianceTab(this);
				}
			}
			wTabFolder.setSelection(0);
			SelectionListener lsTabChange = new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {

					widgetSelected(arg0);
				}

				@Override
				public void widgetSelected(SelectionEvent arg0) {

					enable();
				}
			};
			wTabFolder.addSelectionListener(lsTabChange);
			Label spacer = helper.addSpacer(parent, wLicenseComp);
			// Place the tab folder in the dialog
			FormData fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(spacer, helper.margin);
			fd.right = new FormAttachment(100, -helper.margin);
			wTabFolder.setLayoutData(fd);
		}
	}

	private Composite createLicenseComp(Composite parent, Control last) {

		Composite wLicComp = new Composite(parent, 0);
		setLook(wLicComp);
		wLicComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		wLicComp.setLayoutData(fd);
		// prinary License
		Group gPrimary = addGroup(wLicComp, last, "PrimaryLicense.Group");
		setLook(gPrimary);
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.right = new FormAttachment(100, -margin);
		gPrimary.setLayoutData(fd);
		addPrimaryLicenseControls(gPrimary);
		// trial License
//		Group gTrial = addGroup(wLicComp, last, "TrialLicense.Group");
//		setLook(gTrial);
//		fd = new FormData();
//		fd.left = new FormAttachment(50, margin);
//		fd.right = new FormAttachment(100, -margin);
//		gTrial.setLayoutData(fd);
//		addTrialLicenseControls(gTrial);
		createProductGroup(wLicComp, gPrimary);
		return wLicComp;
	}

	private Label[] createObjLabels(Composite parent, Control last, int nProduct) {

		Label lName          = new Label(parent, SWT.LEFT | SWT.WRAP);
		Label lLicTestResult = new Label(parent, SWT.LEFT | SWT.WRAP);
		lName.setText(getProductName(nProduct));
		lLicTestResult.setText(setLicenseResult(nProduct));
		helper.setLook(lName);
		helper.setLook(lLicTestResult);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 5 * helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		lName.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(60, 0);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		lLicTestResult.setLayoutData(fd);
		return new Label[] { lName, lLicTestResult };
	}

	private Group createProductGroup(Composite parent, Control last) {

		Group gProducts = addGroup(parent, last, "LicenseProducts.Group");
		setLook(gProducts);
		FormData fd = new FormData();
		fd.top = new FormAttachment(last, 3 * margin);
		fd.left = new FormAttachment(0, margin);
		fd.right = new FormAttachment(100, -margin);
		gProducts.setLayoutData(fd);
		Composite wLeftComp = new Composite(gProducts, 0);
		setLook(wLeftComp);
		wLeftComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, 0);
		wLeftComp.setLayoutData(fd);
		last = lNameLicensed = createObjLabels(wLeftComp, null, MDPropTags.MDLICENSE_Name)[1];
		last = lPhoneLicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_Phone)[1];
		last = lEmailLicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_Email)[1];
		/* 
		 * just to get the name labels for addr and geo so we
		 * can change name to account for Canada, geoPoint etc
		 */
		Label[] tmpHolder;
		tmpHolder = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_GeoCode);
		lGeoName = tmpHolder[0];
		last = lGeoLicensed = tmpHolder[1];
		tmpHolder = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_Address);
		lAddrName = tmpHolder[0];
		last = lAddressLicensed = tmpHolder[1];
		last = lRBDILicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_RBDI)[1];
		last = lProfilerLicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_Profiler)[1];
		last = lPropertyLicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_Property)[1];
		Composite wRightComp = new Composite(gProducts, 0);
		setLook(wRightComp);
		wRightComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(50, 0);
		fd.right = new FormAttachment(100, 0);
		wRightComp.setLayoutData(fd);
		last = lSmartMoverLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_SmartMover)[1];
		last = lPresortLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_Presort)[1];
		tmpHolder = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_MatchUp);
		lMUname = tmpHolder[0];
		last = lMatchUpLicensed = tmpHolder[1];
		last = lIpLocatorLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_IPLocator)[1];
		last = lGlobalVerifyLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_GlobalVerify)[1];
		last = lPersonatorLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_Personator)[1];
		//last = lPersonatorWorldLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_PersonatorWorld)[1];
		last = lBusinessCoderLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_BusinessCoder)[1];
		last = lCleanserLicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_Cleanser)[1];
		return gProducts;
	}

	public void enable() {

		if (wTabFolder != null) {
			if (getString("ProductsTab.Title").equals(wTabFolder.getSelection().getText())) {
				wShowDetails.setEnabled(false);
			} else if (wShowDetails != null) {
				wShowDetails.setEnabled(true);
			}
		}
		updateLicensedLabels();
	}

	public AdvancedConfigInterface getAcInterface() {

		return acInterface;
	}

	private String getAddrLabel() {

		MDSettingsData settingsData = acInterface.getSettingsData();
		if (settingsData.primeLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_AddressObject)) {
			if (settingsData.primeLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_CanadianAddon)) {
				return getString("AddressUSCA.Label");
			} else {
				return getString("AddressUS.Label");
			}
		} else if (settingsData.primeLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_Any)) {
			return getString("AddressUSCA.Label");
		} else if (settingsData.trialLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_AddressObject)) {
			if (settingsData.trialLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_CanadianAddon)) {
				return getString("AddressUSCA.Label");
			} else {
				return getString("AddressUS.Label");
			}
		} else if (settingsData.trialLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_Any)) {
			return getString("AddressUSCA.Label");
		}
		return getString("AddressObj.Label");
	}

	@Override
	protected boolean getData(Object ignore) throws KettleException {

		MDSettingsData data = acInterface.getSettingsData();
		if (MDPropTags.MENU_ID_LICENSE.equals(acInterface.getMenuID())) {
			data.primeLicense.licenseString = getLicenseContent(wPrimaryLicense);
			//data.trialLicense.licenseString = getLicenseContent(wTrialLicense);
		}
		if (localTab != null) {
			localTab.getData(acInterface);
		}
		if (webTab != null) {
			webTab.getData(acInterface);
		}
		if (localApplianceTab != null) {
			localApplianceTab.getData(acInterface);
		}
		return true;
	}

	@Override
	protected Button[] getDialogButtons() {
		// Call parent to get OK and Cancel buttons
		Button[] buttons = super.getDialogButtons();
		Button   wOK     = buttons[0];
		Button   wCancel = buttons[1];
		// Get test button
		if (!MDPropTags.MENU_ID_LICENSE.equals(acInterface.getMenuID())) {
			wShowDetails = new Button(getShell(), SWT.PUSH);
			wShowDetails.setText(getString("Details"));
			wShowDetails.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event e) {

					try {
						test();
					} catch (KettleException ke) {
						throw new RuntimeException("Unexpected exception in test", ke);
					}
				}
			});
			return new Button[] { wShowDetails, wOK, wCancel };
		}
		return new Button[] { wOK, wCancel };
	}

	public String getDialogTitle() {

		return dialogTitle;
	}

	private String getGeoLabel() {

		MDSettingsData settingsData = acInterface.getSettingsData();
		int            primeGeo     = settingsData.primeLicense.geoLevel;
		int            trialGeo     = settingsData.trialLicense.geoLevel;
		if (primeGeo > 0) {
			return getString("Geo" + String.valueOf(primeGeo) + ".Label");
		} else if (trialGeo > 0) {
			return getString("Geo" + String.valueOf(trialGeo) + ".Label");
		} else {
			return getString("Geo" + String.valueOf(primeGeo) + ".Label");
		}
	}

	public SettingsHelper getHelper() {

		return helper;
	}

	@Override
	protected String getHelpURLKey() {
		// no help context for this dialog
		return null;
	}

	public AdvancedConfigInterface getInteface() {

		return acInterface;
	}

	private String getLicenseContent(Text wLic) {

		String license = wLic.getText().trim();
		if (!Const.isEmpty(license) && license.trim().equalsIgnoreCase(LICENSE_NEEDED)) {
			license = "";
		}
		return license;
	}

	private String getMUlabel() {

		return "MatchUP:";

//		MDSettingsData settingsData = acInterface.getSettingsData();
//		if (((settingsData.primeLicense.retVal & MDPropTags.MDLICENSE_MatchUp) != 0) || ((settingsData.trialLicense.retVal & MDPropTags.MDLICENSE_MatchUp) != 0))
//			return "MatchUP:";
//		else if (((settingsData.primeLicense.retVal & MDPropTags.MDLICENSE_MatchUpLite) != 0) || ((settingsData.trialLicense.retVal & MDPropTags.MDLICENSE_MatchUpLite) != 0))
//			return "MatchUP: Lite";
//		else
//			return "MatchUP: Community";
	}

	/**
	 * @return A naming prefix that will be used when looking up label strings
	 */
	public String getNamingPrefix() {

		return "MDSettings";
	}

	@Override
	protected String getStringPrefix() {

		return "MDSettings";
	}

	@Override
	protected Class<?> getPackage() {

		return PKG;
	}

	private String getProductName(int nProduct) {

		String sProduct = "";
		if (nProduct == MDPropTags.MDLICENSE_Name) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_NameObject;
		}
		if (nProduct == MDPropTags.MDLICENSE_Address) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_AddressObject;
		}
		if (nProduct == MDPropTags.MDLICENSE_RBDI) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_RBDIAddon;
		}
		if (nProduct == MDPropTags.MDLICENSE_GeoCode) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_GeoCoder;
		}
		if (nProduct == MDPropTags.MDLICENSE_Phone) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_PhoneObject;
		}
		if (nProduct == MDPropTags.MDLICENSE_Email) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_EmailObject;
		}
		if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_SmartMover;
		}
		if (nProduct == MDPropTags.MDLICENSE_MatchUp) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_MatchUpObject;
		}
		if (nProduct == MDPropTags.MDLICENSE_IPLocator) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject;
		}
		if (nProduct == MDPropTags.MDLICENSE_Presort) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_PresortObject;
		}
		if (nProduct == MDPropTags.MDLICENSE_GlobalVerify) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_GlobalVerify;
		}
		if (nProduct == MDPropTags.MDLICENSE_Personator) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_Personator;
		}
		if (nProduct == MDPropTags.MDLICENSE_PersonatorWorld) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_PersonatorWorld;
		}
		if (nProduct == MDPropTags.MDLICENSE_Profiler) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_Profiler;
		}
		if (nProduct == MDPropTags.MDLICENSE_BusinessCoder) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_BusinessCoder;
		}
		if (nProduct == MDPropTags.MDLICENSE_Property) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_Property;
		}
		if (nProduct == MDPropTags.MDLICENSE_Cleanser) {
			sProduct = MDPropTags.MDLICENSE_PRODUCT_Cleanser;
		}

		return getString(sProduct);
	}

	public PropsUI getProps() {

		return props;
	}

	public CTabFolder getTabFolder() {

		return wTabFolder;
	}

	@Override
	protected void init(Object _acInterface) {

		acInterface = (AdvancedConfigInterface) _acInterface;
		MDSettingsData data = acInterface.getSettingsData();
		setLicenseContent(data.primeLicense.licenseString, wPrimaryLicense);
		wPrimaryCustID.setText(data.primeLicense.CustomerID);
		wPrimaryExpiration.setText(data.primeLicense.expiration);
		wPrimaryExpiration.setEditable(false);
		wPrimaryCustID.setEditable(false);

		if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_BUSINESS_CODER.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROPERTY.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PERSONATOR
				.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PERSONATOR_WORLD.equals(acInterface.getMenuID())) {
			webTab.init(acInterface);
		} else if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROFILER
				.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
			if (localTab != null) {
				localTab.init(acInterface);
			}
		} else if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
			if (localTab != null) {
				localTab.init(acInterface);
			}
			webTab.init(acInterface);
			if (localApplianceTab != null) {
				localApplianceTab.init(acInterface);
				syncFields();
			}
		}
		enable();
		getShell().pack();
	}

	public boolean isBusinessCoder() {

		return isBusinessCoder;
	}

	public boolean isPersonator() {

		return isPersonator;
	}

	public boolean isSmartMover() {

		return isSmartMover;
	}

	/**
	 * Called when dialog changes are accepted.
	 */
	@Override
	public void ok() {

		try {
			// Don't update data unless something changed.
			// If there is any problem, cancel the ok
			if (changed) {
				getData(acInterface);
			}
			acInterface.getSettingsData().errorList = new ArrayList<String>();
			acInterface.getSettingsData().warnList = new ArrayList<String>();
			acInterface.checkDataPaths();
			if (!MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) && !MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID()) && !MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID()) && !MDPropTags.MENU_ID_LICENSE
					.equals(acInterface.getMenuID())) {
				acInterface.checkWebSettings(acInterface.getMenuID());
			}
			if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
				acInterface.checkWorkPaths(acInterface.getSettingsData().matchUpWorkPath);
			}
			if (MDPropTags.MENU_ID_LICENSE.equals(acInterface.getMenuID())) {
				acInterface.checkLicences();
			}
			if ((acInterface.getSettingsData().errorList.size() > 0) || (acInterface.getSettingsData().warnList.size() > 0)) {
				String           erMsg = "";
				Iterator<String> ei    = acInterface.getSettingsData().errorList.iterator();
				while (ei.hasNext()) {
					erMsg += ei.next();
				}
				ei = acInterface.getSettingsData().warnList.iterator();
				while (ei.hasNext()) {
					erMsg += ei.next();
				}
				// get the proper box title
				String title = "";
				if (acInterface.getSettingsData().errorList.size() > 0) {
					title = getAbsoluteString("MDSettings.AdvancedConfigDialog.Error.Title");
				} else {
					title = getAbsoluteString("MDSettings.AdvancedConfigDialog.Warning.Title");
					//TODO add to messages
					erMsg += "\nContinue to save or cancel to change settings.";
				}
				MessageBox ebox = null;
				if (acInterface.getSettingsData().errorList.size() > 0) {
					ebox = new MessageBox(getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_ERROR);
				} else {
					ebox = new MessageBox(getShell(), SWT.OK | SWT.CANCEL | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				}
				ebox.setText(title);
				ebox.setMessage(erMsg);
				if (ebox.open() == SWT.CANCEL) {
					return;
				}
				// only stop them if there is an error
				if (acInterface.getSettingsData().errorList.size() > 0) {
					return;
				}
			}

			if (changed) {
				MessageBox box = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText(getAbsoluteString("MDSettings.WarningDialogChanged.Title"));
				//FIXME change to reflect individual products

				String message;
				if (changedWebProp) {
					message = getString("AdvancedConfigDialog.PersistentPropertyChangeWarning.WEB", acInterface.getMenuID());
				} else {
					message = getString("AdvancedConfigDialog.PersistentPropertyChangeWarning");
				}

				box.setMessage(message);
				if (box.open() == SWT.YES) {
					acInterface.setProperties();
					acInterface.saveGlobal();
					if (Const.isWindows()) {
						try {
							if (!Const.isEmpty(acInterface.getSettingsData().primeLicense.licenseString)) {
								acInterface.savePrimeToReg(acInterface.getSettingsData().primeLicense.licenseString);
							}
							if (!Const.isEmpty(acInterface.getSettingsData().trialLicense.licenseString)) {
								acInterface.saveTrialToReg(acInterface.getSettingsData().trialLicense.licenseString);
							}
						} catch (Exception e) {
							System.out.println("Error writing to Registery " + e.getMessage());
						}
					}
				}
			}
			// Close the dialog
			dispose();
		} catch (KettleException e) {
			System.out.println(" Error in OK = " + e.getMessage());
		}
	}

	/**
	 * Indicates that something in the dialog has changed
	 */
	public void setChanged() {

		changed = true;
	}

	public void setDialogTitle(String dialogTitle) {

		this.dialogTitle = dialogTitle;
	}

	/**
	 * Called to set the contents of the license string control. Does special
	 * translation for needed value.
	 *
	 * @param license
	 */
	private void setLicenseContent(String license, Text wLic) {

		boolean setPos = true;
		if (Const.isEmpty(license)) {
			license = LICENSE_NEEDED;
			setPos = false;
		}
		wLic.setText(license.trim());
		if (setPos) {
			wLic.setSelection(wLic.getText().length());
		}
	}

	public String setLicenseResult(int nProduct) {

		String         result       = "";
		MDSettingsData settingsData = acInterface.getSettingsData();
		// Check the result
		if ("tlicNoError".equals(settingsData.primeLicense.testResult)) {
			if (nProduct == MDPropTags.MDLICENSE_MatchUp) {
				if ((settingsData.primeLicense.retVal & MDPropTags.MDLICENSE_MatchUp) != 0) {
					result = "Licensed";
				} else if ((settingsData.primeLicense.retVal & MDPropTags.MDLICENSE_MatchUpLite) != 0) {
					result = "Lite Edition";
				} else {
					result = "Community";
				}
				return result;
			}
			if ((settingsData.primeLicense.retVal & nProduct) != 0) {
				result = "Licensed";
			} else if ((settingsData.primeLicense.retVal & MDPropTags.MDLICENSE_Community) != 0) {
				result = "Community";
			} else {
				result = "Not Licensed";
			}
		} else if ("tlicInvalidLicense".equals(settingsData.primeLicense.testResult) || "tlicInvalidProduct".equals(settingsData.primeLicense.testResult)) {
			result = "Not Licensed";
		} else if ("tlicLicenseExpired".equals(settingsData.primeLicense.testResult)) {
			result = "Licensed Expired";
		} else if ("tlicDatabaseExpired".equals(settingsData.primeLicense.testResult)) {
			result = "Database Expired";
		} else {
			result = "Not Licensed";
		}
		return result;
	}

	public void setPropertyChanged() {
		// FIXME get rid of this
	}

	public void setVersionLabel(String topLine) {

		String labelString = topLine + "\n" + lVersion.getText();
		lVersion.setText(labelString);
	}

	/**
	 * ui fields that are common between the web service and the local appliance
	 * modes need to be synchronized.
	 */
	private void syncFields() {
		// Link these fields so that changes to one will update the other
		Text[][] fields = new Text[][] { new Text[] { webTab.wWebMaxThreads, localApplianceTab.wCVSMaxThreads }, new Text[] { webTab.wWebMaxRequests, localApplianceTab.wCVSMaxRequests }, };
		for (Text[] field : fields) {
			final Text field1 = field[0];
			final Text field2 = field[1];
			field1.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {

					if (!syncingFields) {
						syncingFields = true;
						field2.setText(field1.getText());
						syncingFields = false;
					}
				}
			});
			field2.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {

					if (!syncingFields) {
						syncingFields = true;
						field1.setText(field2.getText());
						syncingFields = false;
					}
				}
			});
		}
	}

	/**
	 * Called to perform tests on the configuration
	 *
	 * @throws KettleException
	 */
	protected void test() throws KettleException {
		// get new data if anything has changed
		if (changed) {
			getData("");
		}
		acInterface.getSettingsData().errorList = new ArrayList<String>();
		// get the test we are running by which tab we are on
		int testType = 0;
		if (getString("ProductsTab.Title").equals(wTabFolder.getSelection().getText())) {
			testType = 0;
		}
		// "On Premise"
		if (getString("OnPremiseTab.Title").equals(wTabFolder.getSelection().getText())) {
			testType = 1;
		}
		if (getString("WebTab.Title").equals(wTabFolder.getSelection().getText())) {
			testType = 2;
		}
		if (getString("LocalApplianceTab.Title").equals(wTabFolder.getSelection().getText())) {
			testType = 3;
		}

		acInterface.nTestType = testType;
		if ((!Const.isEmpty(acInterface.getSettingsData().primeLicense.licenseString) || !Const.isEmpty(acInterface.getSettingsData().trialLicense.licenseString)) || MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID())
		    || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID())) {
			if ((testType == 3) && Const.isEmpty(acInterface.getSettingsData().serverURL)) {
				MessageBox ebox = new MessageBox(getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				ebox.setText(getAbsoluteString("MDSettings.WarningDialog.Title"));
				ebox.setMessage(getAbsoluteString("MDSettings.WarningDialog.ServerURL"));
				ebox.open();
				return;
			}
			// 0 means we are just showing license details and don't need to
			// test
			if (testType != 0) {
				try {
					// Perform the test inside a progress monitor dialog
					new ProgressMonitorDialog(getShell()).run(true, true, new TestService(acInterface, testType));
				} catch (InvocationTargetException e) {
					// Something went wrong. Throw it back up.
					Throwable cause = e.getTargetException();
					if (cause instanceof RuntimeException) {
						if (cause.getCause() instanceof KettleException) {
							throw (KettleException) cause.getCause();
						}
						throw (RuntimeException) cause;
					}
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					// Testing was interrupted, just return
					return;
				}
			}
			if (acInterface.getSettingsData().errorList.size() > 0) {
				String           erMsg = "";
				Iterator<String> ei    = acInterface.getSettingsData().errorList.iterator();
				while (ei.hasNext()) {
					erMsg += ei.next() + "\n";
				}
				MessageBox ebox = new MessageBox(getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				ebox.setText(getAbsoluteString("MDSettings.WarningDialogChanged.Title"));
				ebox.setMessage(erMsg);
				ebox.open();
				return;
			}
			// Display the results
			MDAbstractDialog testDialog = new TestServiceDialog(getShell(), dialog, acInterface);
			testDialog.open();
		} else {
			MessageBox ebox = new MessageBox(getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			ebox.setText(getAbsoluteString("MDSettings.WarningDialog.Title"));
			//TODO put in messages
			ebox.setMessage("No License string set.");
			ebox.open();
			if (wPrimaryLicense != null) {
				wPrimaryLicense.setFocus();
			}
			return;
		}
	}

	private void updateLicensedLabels() {

		if (lNameLicensed != null) {
			lNameLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Name));
		}
		if (lAddrName != null) {
			lAddrName.setText(getAddrLabel());
		}
		if (lAddressLicensed != null) {
			lAddressLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Address));
		}
		if (lRBDILicensed != null) {
			lRBDILicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_RBDI));
		}
		if (lGeoName != null) {
			lGeoName.setText(getGeoLabel());
		}
		if (lGeoLicensed != null) {
			lGeoLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_GeoCode));
		}
		if (lPhoneLicensed != null) {
			lPhoneLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Phone));
		}
		if (lEmailLicensed != null) {
			lEmailLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Email));
		}
		if (lSmartMoverLicensed != null) {
			lSmartMoverLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_SmartMover));
		}
		if (lPresortLicensed != null) {
			lPresortLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Presort));
		}
		if (lMUname != null) {
			lMUname.setText(getMUlabel());
		}
		if (lMatchUpLicensed != null) {
			lMatchUpLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_MatchUp));
		}
		if (lIpLocatorLicensed != null) {
			lIpLocatorLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_IPLocator));
		}
		if (lGlobalVerifyLicensed != null) {
			lGlobalVerifyLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_GlobalVerify));
		}
		if (lPersonatorLicensed != null) {
			lPersonatorLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Personator));
		}
		if (lPersonatorWorldLicensed != null) {
			lPersonatorWorldLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_PersonatorWorld));
		}
		if (lPropertyLicensed != null) {
			lPropertyLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Property));
		}
		if (lProfilerLicensed != null) {
			lProfilerLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Profiler));
		}
		if (lBusinessCoderLicensed != null) {
			lBusinessCoderLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_BusinessCoder));
		}
		if (lCleanserLicensed != null) {
			lCleanserLicensed.setText(setLicenseResult(MDPropTags.MDLICENSE_Cleanser));
		}
	}

	private void updatePrimeLicenseInfo() {

		MDSettingsData data = acInterface.getSettingsData();
		if (!data.primeLicense.licenseString.equals(getLicenseContent(wPrimaryLicense))) {
			data.primeLicense.licenseString = getLicenseContent(wPrimaryLicense);
			acInterface.validateLicense(data.primeLicense, false, false);
			setLicenseContent(data.primeLicense.licenseString, wPrimaryLicense);
			wPrimaryCustID.setText(data.primeLicense.CustomerID);
			wPrimaryExpiration.setText(data.primeLicense.expiration);
			data.primeLicense.hasBusCoder = "";
			updateLicensedLabels();
		}
	}

	public boolean isChangedWebProp() {

		return changedWebProp;
	}

	public void setChangedWebProp(boolean changedWebProp) {

		this.changedWebProp = changedWebProp;
	}
}
