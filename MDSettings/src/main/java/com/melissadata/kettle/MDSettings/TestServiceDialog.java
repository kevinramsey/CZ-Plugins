package com.melissadata.kettle.MDSettings;

import com.melissadata.cz.support.MDPropTags;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDDialogParent;

public class TestServiceDialog extends MDAbstractDialog {

	// private static final String PAF_ACCOUNT_PAGE = "https://www.melissadata.com/user/user_account.aspx";
	private enum TestService {
		Local, Cloud, SmartMover, MatchUp;

		@Override
		public String toString() {
			// only capitalize the first letter
			String s = super.toString();
			return s.substring(0, 1) + s.substring(1).toLowerCase();
		}
	}

	private static Class<?> PKG = TestServiceDialog.class;
	private Label                   wGeneralDescription;
	private Composite               wComponentsComp;
	private TestService             tstService;
	private AdvancedConfigInterface acInterface;

	public TestServiceDialog(Shell parent, MDDialogParent dialog, AdvancedConfigInterface acInterface) {

		super(parent, dialog, acInterface, SWT.NONE);
	}

	private Control createBusCoderContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "BusCoderGroupWeb");
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(settingsData.webBusCoderMsg));
		wVersion.setText(safeErrorMsg(settingsData.webBusCoderVersion));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {

		acInterface = (AdvancedConfigInterface) data;
		Control last = null;
		last = wGeneralDescription = addLabel(parent, last, "generalDescription");
		// change General description
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.CVdescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.CVdescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.CVdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.CVdescription"));
			}
		}
		if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.SMdescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.SMdescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.SMdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.SMdescription"));
			}
		}
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.PSdescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.PSdescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.PSdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.PSdescription"));
			}
		}
		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.MUdescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.MUdescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.MUdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.MUdescription"));
			}
		}
		if (MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText("MatchUp Global"/*getString("Products.MUdescription")*/);
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText("MatchUp Global"/*getString("OnPremise.MUdescription")*/);
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.MUdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.MUdescription"));
			}
		}

		if (MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.IPdescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.IPdescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.IPdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.IPdescription"));
			}
		}
		if (MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.PRFdescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.PRFdescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.PRFdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.PRFdescription"));
			}
		}
		if (MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.GVdescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.GVdescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.GVdescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.GVdescription"));
			}
		}
		if (MDPropTags.MENU_ID_PERSONATOR.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.PersonatorDescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.PersonatorDescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.PersonatorDescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.PersonatorDescription"));
			}
		}
		if (MDPropTags.MENU_ID_PERSONATOR_WORLD.equals(acInterface.getMenuID())) {
			if (acInterface.nTestType == 0) {
				wGeneralDescription.setText(getString("Products.PersonatorWorldDescription"));
			}
			if (acInterface.nTestType == 1) {
				wGeneralDescription.setText(getString("OnPremise.PersonatorWorldDescription"));
			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.PersonatorWorldDescription"));
			}
			if (acInterface.nTestType == 3) {
				wGeneralDescription.setText(getString("LocalAppliance.PersonatorWorldDescription"));
			}
		}
		if (MDPropTags.MENU_ID_PROPERTY.equals(acInterface.getMenuID())) {

//			if (acInterface.nTestType == 0) {
//				wGeneralDescription.setText(getString("Products.PersonatorDescription"));
//			}
//			if (acInterface.nTestType == 1) {
//				wGeneralDescription.setText(getString("OnPremise.PersonatorDescription"));
//			}
			if (acInterface.nTestType == 2) {
				wGeneralDescription.setText(getString("Web.PropertyDescription"));
			}
//			if (acInterface.nTestType == 3) {
//				wGeneralDescription.setText(getString("LocalAppliance.PersonatorDescription"));
//			}
		}
		if (MDPropTags.MENU_ID_BUSINESS_CODER.equals(acInterface.getMenuID())) {
			wGeneralDescription.setText(getString("Web.BusCoderDescription"));
		}
		if (MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
			wGeneralDescription.setText(getString("OnPremise.CleanserDescription"));
		}

		// Create composite that will contain all controls except the main buttons
		wComponentsComp = new Composite(parent, 0);
		setLook(wComponentsComp);
		wComponentsComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(last, 0);
		fd.right = new FormAttachment(100, 0);
		wComponentsComp.setLayoutData(fd);
		if (acInterface.nTestType == 1) {
			createLocalOnlyServiceContents(wComponentsComp);
		}
		if (acInterface.nTestType == 2) {
			createWebOnlyServiceContents(wComponentsComp, false);
		}
		if (acInterface.nTestType == 3) {
			createWebOnlyServiceContents(wComponentsComp, true);
		}
		parent.getShell().setSize(100, 200);
	}

	private void createLocalOnlyServiceContents(Composite parent) {

		Group group = addGroup(parent, null, "OnPremise");
		((FormData) group.getLayoutData()).right = new FormAttachment(100, -margin);
		Control lastControl = null;
		tstService = TestService.Local;
		MDSettingsData settingsData = acInterface.getSettingsData();
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID())) {
			// Create groups for configured objects
			lastControl = createLSNameContents(group, settingsData, lastControl);
			// if (acInterface.showLocal) {
			lastControl = createLSAddrContents(group, settingsData, lastControl);
			lastControl = createLSGeoContents(group, settingsData, lastControl);
			lastControl = createLSPhoneContents(group, settingsData, lastControl);
			lastControl = createLSEmailContents(group, settingsData, lastControl);
			// }
		}
		if (MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID())) {
			createLSIPLocatorContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID())) {
			createLSProfilerContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			createLSPreSortContents(group, lastControl);
		}
		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			createLSMatchUpContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID())) {
			createLSCleanserContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
			createLSGlobalAddrContents(group, settingsData, lastControl);
		}
	}

	/**
	 * Called to create address object info group for local service dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSAddrContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Composite group = addGroup(parent, lastControl, "AddrGroupOnPremise");
		// Create controls in group
		Text wMsg                = addTextBox(group, null, "InitMsg");
		Text wDBDate             = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration       = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wRBDIDBDate         = addTextBox(group, wDBExpiration, "RBDIDatabaseDate");
		Text wCanadaDBDate       = addTextBox(group, wMsg, "CanadaDatabaseDate");
		Text wCanadaDBExpiration = addTextBox(group, wCanadaDBDate, "CanadaDatabaseExpiration");
		Text wDBBuildNo          = addTextBox(group, wCanadaDBExpiration, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wCanadaDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wCanadaDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBExpiration, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wRBDIDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wRBDIDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wRBDIDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		// Set results
		wMsg.setText(safeErrorMsg(settingsData.addrMsg));
		wDBDate.setText(safeErrorMsg(settingsData.localAddressDBDate));
		wDBExpiration.setText(safeErrorMsg(settingsData.localAddressExpiration));
		wRBDIDBDate.setText(safeErrorMsg(settingsData.RBDIDate));
		if (isCanadaLicensed(settingsData)) {
			wCanadaDBDate.setText(safeErrorMsg(settingsData.localAddressCADBDate));
			wCanadaDBExpiration.setText(safeErrorMsg(settingsData.localAddressCAExpiration));
		} else {
			wCanadaDBDate.setText("Not Licensed");
		}
		wDBBuildNo.setText(safeErrorMsg(settingsData.localAddressBuild));
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wRBDIDBDate.setEditable(false);
		wCanadaDBDate.setEditable(false);
		wCanadaDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	/**
	 * Called to create email object info display group for local service dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSEmailContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "EmailGroupOnPremise");
		// Create controls in group
		Text wMsg          = addTextBox(group, null, "InitMsg");
		Text wDBDate       = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo    = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		if (settingsData.emailMsg != null) {
			wMsg.setText(settingsData.emailMsg);
		}
		if (settingsData.emailDate != null) {
			wDBDate.setText(settingsData.emailDate);
		}
		if (settingsData.emailExpiration != null) {
			wDBExpiration.setText(settingsData.emailExpiration);
		}
		if (settingsData.emailBuild != null) {
			wDBBuildNo.setText(settingsData.emailBuild);
		}
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	/**
	 * Called to create geocoder info group for local service dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSGeoContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		String groupLabel = "GeoGroupOnPremise";
		groupLabel += getGeoLevel(settingsData);
		Group group = addGroup(parent, lastControl, groupLabel);
		// Create controls in group
		Text wMsg          = addTextBox(group, null, "InitMsg");
		Text wDBDate       = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo    = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		if (settingsData.geoMsg != null) {
			wMsg.setText(settingsData.geoMsg);
		}
		if (settingsData.geoDate != null) {
			wDBDate.setText(settingsData.geoDate);
		}
		if (settingsData.geoExpiration != null) {
			wDBExpiration.setText(settingsData.geoExpiration);
		}
		if (settingsData.geoBuild != null) {
			wDBBuildNo.setText(settingsData.geoBuild);
		}
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	/**
	 * Called to create IPLocator object info display group for local service dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSIPLocatorContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "IPLocatorGroupOnPremise");
		// Create controls in group
		Text wMsg          = addTextBox(group, null, "InitMsg");
		Text wDBDate       = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo    = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeErrorMsg(settingsData.ipLocMsg));
		wDBDate.setText(settingsData.ipLocDate);
		wDBExpiration.setText(settingsData.ipLocExpiration);
		wDBBuildNo.setText(settingsData.ipLocBuild);
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	private Control createLSCleanserContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "CleanserGroupOnPremise");
		// Create controls in group
		Text wMsg          = addTextBox(group, null, "InitMsg");
		Text wDBDate       = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo    = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeErrorMsg(settingsData.cleanserMsg));
		wDBDate.setText(settingsData.cleanserDate);
		wDBExpiration.setText(settingsData.cleanserExpiration);
		wDBBuildNo.setText(settingsData.cleanserBuild);
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	/**
	 * Called to create the matchup object info box for the local services dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSMatchUpContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "MatchUpGroupOnPremise");
		// Create controls in group
		Text wMsg    = addTextBox(group, null, "InitMsg");
		Text wDBDate = addTextBox(group, wMsg, "DatabaseDate");
		// Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		// label = ((FormData)wDBExpiration.getLayoutData()).left.control;
		// ((FormData)label.getLayoutData()).right = new FormAttachment((int)(middlePct / 2), -margin);
		// ((FormData)wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeErrorMsg(settingsData.matchUPMsg));
		wDBDate.setText(safeErrorMsg(settingsData.matchUPDate));
		// wDBExpiration.setText(safeErrorMsg(settingsData.matchUPExpiration));
		wDBBuildNo.setText(safeErrorMsg(settingsData.matchUPBuild));
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		// wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	/**
	 * Called to create the name object info box for the local services dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSNameContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "NameGroupOnPremise");
		// Create controls in group
		Text wMsg          = addTextBox(group, null, "InitMsg");
		Text wDBDate       = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo    = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		if (settingsData.nameMsg != null) {
			wMsg.setText(safeErrorMsg(settingsData.nameMsg));
		}
		if (settingsData.nameDate != null) {
			wDBDate.setText(settingsData.nameDate);
		}
		if (settingsData.nameExpiration != null) {
			wDBExpiration.setText(settingsData.nameExpiration);
		}
		if (settingsData.nameBuild != null) {
			wDBBuildNo.setText(settingsData.nameBuild);
		}
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	/**
	 * Create phone object info group for local service dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSPhoneContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "PhoneGroupOnPremise");
		// Create controls in group
		Text wMsg       = addTextBox(group, null, "InitMsg");
		Text wDBDate    = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBBuildNo = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		// Set results
		if (settingsData.phoneMsg != null) {
			wMsg.setText(settingsData.phoneMsg);
		}
		if (settingsData.phoneDate != null) {
			wDBDate.setText(settingsData.phoneDate);
		}
		if (settingsData.phoneBuild != null) {
			wDBBuildNo.setText(settingsData.phoneBuild);
		}
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	private Control createLSPreSortContents(Composite parent, Control lastControl) {

		Group group = addGroup(parent, lastControl, "PresortGroupOnPremise");
		// Create controls in group
		Text wMsg = addTextBox(group, null, "InitMsg");
		// Text wDBDate = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration = addTextBox(group, wMsg, "DatabaseExpiration");
		Text wDBBuildNo    = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		// label = ((FormData)wDBDate.getLayoutData()).left.control;
		// ((FormData)label.getLayoutData()).right = new FormAttachment((int)(middlePct / 2), -margin);
		// ((FormData)wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBExpiration, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		// Set results
		wMsg.setText(safeErrorMsg(acInterface.getSettingsData().preSortMsg));
		// / wDBDate.setText(info.localDBDate);
		wDBExpiration.setText(safeErrorMsg(acInterface.getSettingsData().preSortExpiration));
		wDBBuildNo.setText(safeErrorMsg(acInterface.getSettingsData().preSortBuild));
		// Make results read-only
		wMsg.setEditable(false);
		// wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	/**
	 * Called to create IPLocator object info display group for local service dialog
	 *
	 * @param parent
	 * @param settingsData
	 * @param lastControl
	 * @return
	 */
	private Control createLSProfilerContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "ProfilerGroupOnPremise");
		// Create controls in group
		Text wMsg    = addTextBox(group, null, "InitMsg");
		Text wDBDate = addTextBox(group, wMsg, "DatabaseDate");
		// Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
// label = ((FormData)wDBExpiration.getLayoutData()).left.control;
// ((FormData)label.getLayoutData()).right = new FormAttachment((int)(middlePct / 2), -margin);
// ((FormData)wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeErrorMsg(settingsData.profilerMsg));
		wDBDate.setText(settingsData.profilerDate);
		// wDBExpiration.setText(settingsData.profilerExpiration);
		wDBBuildNo.setText(settingsData.profilerBuild);
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
// wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	private Control createLSGlobalAddrContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "GlobalAddrGroupOnPremise");
		// Create controls in group
		Text wMsg          = addTextBox(group, null, "InitMsg");
		Text wDBDate       = addTextBox(group, wMsg, "DatabaseDate");
		Text wDBExpiration = addTextBox(group, wDBDate, "DatabaseExpiration");
		Text wDBBuildNo    = addTextBox(group, wMsg, "BuildNo");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wDBDate.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBDate.getLayoutData()).right = new FormAttachment(50, -margin);
		label = ((FormData) wDBBuildNo.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).left = new FormAttachment(wDBDate, margin);
		((FormData) label.getLayoutData()).right = new FormAttachment(50 + middlePct / 2, -margin);
		label = ((FormData) wDBExpiration.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wDBExpiration.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		if (settingsData.globalAddrMsg != null) {
			wMsg.setText(safeErrorMsg(settingsData.globalAddrMsg));
		}
		if (settingsData.globalAddressDBDate != null) {
			wDBDate.setText(settingsData.globalAddressDBDate);
		}
		if (settingsData.globalAddressExpiration != null) {
			wDBExpiration.setText(settingsData.globalAddressExpiration);
		}
		if (settingsData.globalAddressBuild != null) {
			wDBBuildNo.setText(settingsData.globalAddressBuild);
		}
		// Make results read-only
		wMsg.setEditable(false);
		wDBDate.setEditable(false);
		wDBExpiration.setEditable(false);
		wDBBuildNo.setEditable(false);
		return group;
	}

	private Control createPersonatorContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "PersonatorGroupWeb");
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(settingsData.webPersonatorMsg));
		wVersion.setText(safeErrorMsg(settingsData.webPersonatorVersion));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createPersonatorWorldContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "PersonatorWorldGroupWeb");
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(settingsData.webPersonatorWorldMsg));
		wVersion.setText(safeErrorMsg(settingsData.webPersonatorWorldVersion));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createPropertyContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "PropertyGroupWeb");
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(settingsData.webPropertyMsg));
		wVersion.setText(safeErrorMsg(settingsData.webPropertyVersion));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private void createWebOnlyServiceContents(Composite parent, boolean isCVS) {

		Control lastControl = null;
		tstService = TestService.Cloud;
		MDSettingsData settingsData = acInterface.getSettingsData();
		Group          group        = addGroup(parent, null, "GeneralGroup");
		if (isCVS) {
			group.setText("Local Appliance");// getString("WebServices")
			Text wServerURL = addTextBox(group, null, "ServerURL");
			// Set results
			wServerURL.setText(settingsData.serverURL);
			// Make results read-only
			wServerURL.setEditable(false);
			lastControl = wServerURL;
		} else {
			group.setText(getString("WebServices"));
		}
		((FormData) group.getLayoutData()).left = new FormAttachment(0, margin);
		((FormData) group.getLayoutData()).right = new FormAttachment(100, -margin);
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID())) {
			// lastControl = createWSNameContents(group, settingsData, lastControl);
			lastControl = createWSAddrContents(group, settingsData, lastControl, false);
			lastControl = createWSGeoContents(group, settingsData, lastControl);
			lastControl = createWSRBDIContents(group, settingsData, lastControl);
			lastControl = createWSPhoneContents(group, settingsData, lastControl, false);
			lastControl = createWSEmailContents(group, settingsData, lastControl, false);
		}
		if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
			createWSSmartMoverContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID())) {
			lastControl = createWSIPLocatorContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
			group.setText(getString("WebGlobalServices"));
			// createGlobalVerifyContents(group,settingsData,lastControl);
			lastControl = createWSNameContents(group, settingsData, lastControl, true);
			lastControl = createWSAddrContents(group, settingsData, lastControl, true);
			lastControl = createWSPhoneContents(group, settingsData, lastControl, true);
			lastControl = createWSEmailContents(group, settingsData, lastControl, true);
		}
		if (MDPropTags.MENU_ID_PERSONATOR.equals(acInterface.getMenuID())) {
			createPersonatorContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_PERSONATOR_WORLD.equals(acInterface.getMenuID())) {
			createPersonatorWorldContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_PROPERTY.equals(acInterface.getMenuID())) {
			createPropertyContents(group, settingsData, lastControl);
		}
		if (MDPropTags.MENU_ID_BUSINESS_CODER.equals(acInterface.getMenuID())) {
			createBusCoderContents(group, settingsData, lastControl);
		}
	}

	private Control createWSAddrContents(Composite parent, MDSettingsData settingsData, Control lastControl, boolean isGlobal) {

		String groupName;
		String message;
		String version;
		if (isGlobal) {
			groupName = "AddressGroupGV";
			message = settingsData.webGlobalAddressMsg;
			version = settingsData.webGlobalAddressVersion;
		} else {
			groupName = "AddrGroupWeb";
			message = settingsData.webAddrMsg;
			version = settingsData.webAddrVersion;
		}
		Composite group = addGroup(parent, lastControl, groupName);
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(message));
		wVersion.setText(safeErrorMsg(version));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createWSEmailContents(Composite parent, MDSettingsData settingsData, Control lastControl, boolean isGlobal) {

		String groupName;
		String message;
		String version;
		if (isGlobal) {
			groupName = "EmailGroupGV";
			message = settingsData.webGlobalEmailMsg;
			version = settingsData.webGlobalEmailVersion;
		} else {
			groupName = "EmailGroupWeb";
			message = settingsData.webEmailMsg;
			version = settingsData.webEmailVersion;
		}
		Group group = addGroup(parent, lastControl, groupName);
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(message));
		wVersion.setText(safeErrorMsg(version));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createWSGeoContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "GeoGroupWeb");
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(settingsData.webGeoMsg));
		wVersion.setText(safeErrorMsg(settingsData.webGeoVersion));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createWSIPLocatorContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "IPGroupWeb");
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(settingsData.webIPMsg));
		wVersion.setText(safeErrorMsg(settingsData.webIPVersion));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createWSNameContents(Composite parent, MDSettingsData settingsData, Control lastControl, boolean isGlobal) {

		String groupName;
		String message;
		String version;
		if (isGlobal) {
			groupName = "NameGroupGV";
			message = settingsData.webGlobalNameMsg;
			version = settingsData.webGlobalNameVersion;
		} else {
			groupName = "NameGroupWeb";
			message = settingsData.webNameMsg;
			version = settingsData.webNameVersion;
		}
		Group group = addGroup(parent, lastControl, groupName);
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(message));
		wVersion.setText(safeErrorMsg(version));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createWSPhoneContents(Composite parent, MDSettingsData settingsData, Control lastControl, boolean isGlobal) {

		String groupName;
		String message;
		String version;
		if (isGlobal) {
			groupName = "PhoneGroupGV";
			message = settingsData.webGlobalPhoneMsg;
			version = settingsData.webGlobalPhoneVersion;
		} else {
			groupName = "PhoneGroupWeb";
			message = settingsData.webPhoneMsg;
			version = settingsData.webPhoneVersion;
		}
		Group group = addGroup(parent, lastControl, groupName);
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(message));
		wVersion.setText(safeErrorMsg(version));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createWSRBDIContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		Group group = addGroup(parent, lastControl, "RBDIGroupWeb");
		// Create controls in group
		Text wMsg     = addTextBox(group, null, "InitMsg");
		Text wVersion = addTextBox(group, wMsg, "VersionWeb");
		// Change layout
		Control label = ((FormData) wMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// Set results
		wMsg.setText(safeWebMsg(settingsData.webRBDIMsg));
		wVersion.setText(safeErrorMsg(settingsData.webRBDIVersion));
		// Make results read-only
		wMsg.setEditable(false);
		wVersion.setEditable(false);
		return group;
	}

	private Control createWSSmartMoverContents(Composite parent, MDSettingsData settingsData, Control lastControl) {

		tstService = TestService.SmartMover;
		// Group for display NCOA information
		Group group = addGroup(parent, lastControl, "SmartMoverGroupNCOA");
		// Create controls in group
		Text wNCOAMsg     = addTextBox(group, null, "NCOAInitMsg");
		Text wNCOAVersion = addTextBox(group, wNCOAMsg, "NCOAVersion");
		// Text wCCOAMsg = addTextBox(group, wNCOAVersion, "CCOAInitMsg");
		// Text wCCOAVersion = addTextBox(group, wCCOAMsg, "CCOAVersion");
		// Change layout
		Control label = ((FormData) wNCOAMsg.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		label = ((FormData) wNCOAVersion.getLayoutData()).left.control;
		((FormData) label.getLayoutData()).right = new FormAttachment(middlePct / 2, -margin);
		((FormData) wNCOAVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		// label = ((FormData)wCCOAMsg.getLayoutData()).left.control;
		// ((FormData)label.getLayoutData()).right = new FormAttachment((int)(middlePct / 2), -margin);
		// label = ((FormData)wCCOAVersion.getLayoutData()).left.control;
		// ((FormData)label.getLayoutData()).right = new FormAttachment((int)(middlePct / 2), -margin);
		// ((FormData)wCCOAVersion.getLayoutData()).right = new FormAttachment(50, -margin);
		Label spc     = addSpacer(group, wNCOAVersion);
		Text  wPAFMsg = addTextBox(group, spc, "PAFmsg");
		// Set results
		wNCOAMsg.setText(safeWebMsg(settingsData.webSMMsg));
		wNCOAVersion.setText(safeErrorMsg(settingsData.webSMVersion));
		wPAFMsg.setText(safePAFWebMsg(settingsData.webPAFMsg));
		// wCCOAMsg.setText(safeErrorMsg(info.webSmartMoverMsg[1]));
		// wCCOAVersion.setText(info.webSmartMoverVersion[1]);
		// Make results read-only
		wNCOAMsg.setEditable(false);
		wNCOAVersion.setEditable(false);
		wPAFMsg.setEditable(false);
		// wCCOAMsg.setEditable(false);
		// wCCOAVersion.setEditable(false);
		return group;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) {
		// Nothing to return since this is a display only dialog
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getDialogButtons()
	 */
	@Override
	protected Button[] getDialogButtons() {

		Button wOK = new Button(getShell(), SWT.PUSH);
		wOK.setText(getAbsoluteString("System.Button.OK"));
		wOK.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				getShell().dispose();
			}
		});
		return new Button[] { wOK };
	}

	private String getGeoLevel(MDSettingsData data) {

		int primeGeo = data.primeLicense.geoLevel;
		int trialGeo = data.trialLicense.geoLevel;
		if (primeGeo > 0) {
			return String.valueOf(primeGeo);
		} else if (trialGeo > 0) {
			return String.valueOf(trialGeo);
		} else {
			return String.valueOf(primeGeo);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {

		return "MDCheck.Help.TestServiceDialog." + tstService.toString();
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

		return "MDSettings.TestDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// not used done at creation
		// getShell().setSize(100, 200);
		getShell().pack();
	}

	private boolean isCanadaLicensed(MDSettingsData data) {

		if (data.primeLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_CanadianAddon) || data.primeLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_Any)) {
			return true;
		} else if (data.trialLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_CanadianAddon) || data.trialLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_Any)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Makes an error message safe. If it is null then it is changed to a "No Error" message.
	 *
	 * @param msg
	 * @return
	 */
	private String safeErrorMsg(String msg) {

		if (Const.isEmpty(msg)) {
			msg = "";
		}
		return msg;
	}

	private String safePAFWebMsg(String msg) {

		if (Const.isEmpty(msg)) {
			msg = "Success";
		} else {
			msg = getString("PAFString." + msg);
		}
		return msg;
	}

	private String safeWebMsg(String msg) {

		if (Const.isEmpty(msg)) {
			msg = "";
		}
		if ("SE01".equals(msg)) {
			msg += " - Internal Error";
		}
		if ("GE02".equals(msg)) {
			msg += " - Empty Request Record Structure";
		}
		if ("GE03".equals(msg)) {
			msg += " - Record count is more than allowed per request";
		}
		if ("GE04".equals(msg)) {
			msg = "Not Licensed";
		}
		if ("GE05".equals(msg)) {
			msg += " - CustomerID is not valid";
		}
		if ("GE06".equals(msg)) {
			msg += " - CustomerID is disabled";
		}
		if ("GE08".equals(msg)) {
			msg += " - CustomerID is not valid for this product";
		}
		if ("WSE26".equals(msg)) {
			msg += " - CustomerID is not valid";
		}
		if (msg.endsWith("333")) {
			msg = "Could not connect to server";
		}
		if (msg.contains("407")) {
			msg += " - Access denied by proxy";
		} else if (msg.contains("Error processing service:")) {
			msg = "407 - Access denied by proxy";
		}
		return msg;
	}
}
