package com.melissadata.kettle.MDSettings;

import com.melissadata.cz.support.MDPropTags;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.i18n.BaseMessages;

public class ProductsTab {

	private static Class<?> PKG = AdvancedConfigurationDialog.class;
	private                             AdvancedConfigurationDialog dialog;
	private                             SettingsHelper              helper;
	@SuppressWarnings("unused") private Group                       gProducts;
	private                             MDSettingsData              settingsData;
	private                             Label                       lNameLicensed;
	private                             Label                       lAddressLicensed;
	private                             Label                       lAddrName;                                    // we use the so we can change the obj
	// labet title for canada, geoPoint etc.
	private                             Label                       lGeoName;
	private                             Label                       lGeoLicensed;
	private                             Label                       lPhoneLicensed;
	private                             Label                       lEmailLicensed;
	private                             Label                       lRBDILicensed;
	private                             Label                       lSmartMoverLicensed;
	private                             Label                       lPresortLicensed;
	private                             Label                       lMatchUpLicensed;
	private                             Label                       lIpLocatorLicensed;
	private                             Label                       lGlobalVerifyLicensed;

	public ProductsTab(AdvancedConfigurationDialog dialog) {

		this.dialog = dialog;
		this.helper = dialog.getHelper();
		settingsData = dialog.getInteface().getSettingsData();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("ProductsTab.Title"));
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Add Groups
		gProducts = createProductsGroup(wComp, null);// wLicComp
		// Fit the composite within its container (the scrolled composite)
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
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
	}

	private Group createProductsGroup(Composite parent, Control last) {

		AdvancedConfigInterface acInterface = dialog.getAcInterface();
		Group                   wGroup      = dialog.addGroup(parent, last, "ProductsGroup");
		last = null;
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID())) {
			last = createCVcomp(wGroup, last);
		}
		if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
			last = createSMcomp(wGroup, last);
		}
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			last = createPScomp(wGroup, last);
		}
		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			last = createMUcomp(wGroup, last);
		}
		if (MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID())) {
			last = createIPcomp(wGroup, last);
		}
		if (MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
			last = createGVcomp(wGroup, last);
		}
		return wGroup;
	}

	private Group createCVcomp(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "ProductsGroup");
		wGroup.setText(getString("ContactVerify.Group.Title"));
		Composite wComponentsComp = new Composite(wGroup, 0);
		helper.setLook(wComponentsComp);
		wComponentsComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, 0);
		wComponentsComp.setLayoutData(fd);
		Composite wLeftComp = new Composite(wComponentsComp, 0);
		helper.setLook(wLeftComp);
		wLeftComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, 0);
		fd.right = new FormAttachment(50, 0);
		wLeftComp.setLayoutData(fd);
		last = lNameLicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_PRODUCT_NameObject)[1];
		last = lPhoneLicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_PRODUCT_PhoneObject)[1];
		last = lEmailLicensed = createObjLabels(wLeftComp, last, MDPropTags.MDLICENSE_PRODUCT_EmailObject)[1];
		Composite wRightComp = new Composite(wComponentsComp, 0);
		helper.setLook(wRightComp);
		wRightComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(50, 0);
		fd.top = new FormAttachment(null, 0);
		fd.right = new FormAttachment(100, 0);
		wRightComp.setLayoutData(fd);
		Label[] tmpHolder; // just to get the name labels for addr and geo so we can change name to account for Canada,
// geoPoint etc
		tmpHolder = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_PRODUCT_GeoCoder);
		lGeoName = tmpHolder[0];
		last = lGeoLicensed = tmpHolder[1];
		tmpHolder = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_PRODUCT_AddressObject);
		lAddrName = tmpHolder[0];
		last = lAddressLicensed = tmpHolder[1];
		lRBDILicensed = createObjLabels(wRightComp, last, MDPropTags.MDLICENSE_PRODUCT_RBDIAddon)[1];
		return wGroup;
	}

	private Label[] createObjLabels(Composite parent, Control last, String product) {

		Label lName          = new Label(parent, SWT.LEFT | SWT.WRAP);
		Label lLicTestResult = new Label(parent, SWT.LEFT | SWT.WRAP);
		lName.setText(getString(product));
		lLicTestResult.setText(getString(product));
		helper.setLook(lName);
		helper.setLook(lLicTestResult);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 5 * helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		if (MDPropTags.MDLICENSE_PRODUCT_PresortObject.equals(product) || MDPropTags.MDLICENSE_PRODUCT_MatchUpObject.equals(product) || MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject.equals(product) || MDPropTags.MDLICENSE_PRODUCT_GlobalVerify
				.equals(product) || MDPropTags.MDLICENSE_PRODUCT_SmartMover.equals(product)) {
			fd.right = new FormAttachment(15, -helper.margin);
		} else {
			fd.right = new FormAttachment(45, -helper.margin);
		}
		lName.setLayoutData(fd);
		fd = new FormData();
		// set position depending on product
		if (MDPropTags.MDLICENSE_PRODUCT_PresortObject.equals(product) || MDPropTags.MDLICENSE_PRODUCT_MatchUpObject.equals(product) || MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject.equals(product) || MDPropTags.MDLICENSE_PRODUCT_GlobalVerify
				.equals(product) || MDPropTags.MDLICENSE_PRODUCT_SmartMover.equals(product)) {
			fd.left = new FormAttachment(15, 0);
		} else {
			fd.left = new FormAttachment(45, 0);
		}
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		lLicTestResult.setLayoutData(fd);
		return new Label[] { lName, lLicTestResult };
	}

	private Group createSMcomp(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "ProductsGroup");
		wGroup.setText(getString("SmartMover.Group.Title"));
		Composite wComponentsComp = new Composite(wGroup, 0);
		helper.setLook(wComponentsComp);
		wComponentsComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, 0);
		wComponentsComp.setLayoutData(fd);
		lSmartMoverLicensed = createObjLabels(wComponentsComp, null, MDPropTags.MDLICENSE_PRODUCT_SmartMover)[1];
		return wGroup;
	}

	private Group createPScomp(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "ProductsGroup");
		wGroup.setText(getString("PreSort.Group.Title"));
		Composite wComponentsComp = new Composite(wGroup, 0);
		helper.setLook(wComponentsComp);
		wComponentsComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, 0);
		wComponentsComp.setLayoutData(fd);
		lPresortLicensed = createObjLabels(wComponentsComp, null, MDPropTags.MDLICENSE_PRODUCT_PresortObject)[1];
		return wGroup;
	}

	private Group createMUcomp(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "ProductsGroup");
		wGroup.setText(getString("MatchUp.Group.Title"));
		Composite wComponentsComp = new Composite(wGroup, 0);
		helper.setLook(wComponentsComp);
		wComponentsComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, 0);
		wComponentsComp.setLayoutData(fd);
		lMatchUpLicensed = createObjLabels(wComponentsComp, null, MDPropTags.MDLICENSE_PRODUCT_MatchUpObject)[1];
		return wGroup;
	}

	private Group createIPcomp(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "ProductsGroup");
		wGroup.setText(getString("IpLocator.Group.Title"));
		Composite wComponentsComp = new Composite(wGroup, 0);
		helper.setLook(wComponentsComp);
		wComponentsComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, 0);
		wComponentsComp.setLayoutData(fd);
		lIpLocatorLicensed = createObjLabels(wComponentsComp, null, MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)[1];
		return wGroup;
	}

	private Group createGVcomp(Composite parent, Control last) {

		Group wGroup = dialog.addGroup(parent, last, "ProductsGroup");
		wGroup.setText(getString("GlobalVerify.Group.Title"));
		Composite wComponentsComp = new Composite(wGroup, 0);
		helper.setLook(wComponentsComp);
		wComponentsComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, 0);
		wComponentsComp.setLayoutData(fd);
		lGlobalVerifyLicensed = createObjLabels(wComponentsComp, null, MDPropTags.MDLICENSE_PRODUCT_GlobalVerify)[1];
		return wGroup;
	}

	public void init(AdvancedConfigInterface acInterface) {

		enable();
	}

	private void enable() {

		if (lNameLicensed != null) {
			lNameLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_Name));
		}
		if (lAddrName != null) {
			lAddrName.setText(getAddrLabel());
		}
		if (lAddressLicensed != null) {
			lAddressLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_Address));
		}
		if (lRBDILicensed != null) {
			lRBDILicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_RBDI));
		}
		if (lGeoName != null) {
			lGeoName.setText(getGeoLabel());
		}
		if (lGeoLicensed != null) {
			lGeoLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_GeoCode));
		}
		if (lPhoneLicensed != null) {
			lPhoneLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_Phone));
		}
		if (lEmailLicensed != null) {
			lEmailLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_Email));
		}
		if (lSmartMoverLicensed != null) {
			lSmartMoverLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_SmartMover));
		}
		if (lPresortLicensed != null) {
			lPresortLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_Presort));
		}
		if (lMatchUpLicensed != null) {
			lMatchUpLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_MatchUp));
		}
		if (lIpLocatorLicensed != null) {
			lIpLocatorLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_IPLocator));
		}
		if (lGlobalVerifyLicensed != null) {
			lGlobalVerifyLicensed.setText(dialog.setLicenseResult(MDPropTags.MDLICENSE_GlobalVerify));
		}
	}

	private String getAddrLabel() {

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

	private String getGeoLabel() {

		int primeGeo = settingsData.primeLicense.geoLevel;
		int trialGeo = settingsData.trialLicense.geoLevel;
		if (primeGeo > 0) {
			return getString("Geo" + String.valueOf(primeGeo) + ".Label");
		} else if (trialGeo > 0) {
			return getString("Geo" + String.valueOf(trialGeo) + ".Label");
		} else {
			return getString("Geo" + String.valueOf(primeGeo) + ".Label");
		}
	}

	private String getString(String name) {

		return BaseMessages.getString(PKG, "MDSettings." + name);
	}
}
