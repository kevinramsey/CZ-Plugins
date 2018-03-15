package com.melissadata.kettle.iplocator.ui;

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.iplocator.IPLocatorMeta;
import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;

public class IPLocatorTab implements MDTab {
	private static Class<?> PKG = IPLocatorMeta.class;
	private MDCheckDialog dialog;
	private IPLocatorMeta ipMeta;
	private MDCheckHelper helper;
	private Group         gInput;
	private Group         gOutput;
	private MDInputCombo  wIPInput;
	private Text          wOutputIPAddress;
	private Text          wOutputLatitude;
	private Text          wOutputLongitude;
	private Text          wOutputZip;
	private Text          wOutputRegion;
	private Text          wOutputName;
	private Text          wOutputDomain;
	private Text          wOutputCityName;
	private Text          wOutputCountry;
	private Text          wOutputAbbreviation;
	private Text          wOutputConnectionSpeed;
	private Text          wOutputConnectionType;
	private Text          wOutputUTC;
	private Text          wOutputContinent;
	private SelectionListener enableListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			enable();
		}
	};

	public IPLocatorTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
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
		// Fit the composite within its container (the scrolled composite)
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description.Label"));
		helper.setLook(description);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Add the input/output groups
		Composite wIOComp = new Composite(wComp, 0);
		helper.setLook(wIOComp);
		wIOComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.top = new FormAttachment(description, 4 * helper.margin);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		wIOComp.setLayoutData(fd);
		gInput = createInputGroup(wIOComp);
		gOutput = createOutputGroup(wIOComp);
		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);
	}

	public void advancedConfigChanged() {
		enable();
	}

	public void dispose() {
		// Nothing to do
	}

	public void getData(MDCheckStepData data) {
		IPLocatorMeta ipLocatorMeta = data.getIPLocator();
		if (ipLocatorMeta != null) {
			ipLocatorMeta.setInIPAddress(wIPInput.getValue());
			ipLocatorMeta.setOutIPAddress(wOutputIPAddress.getText());
			ipLocatorMeta.setOutLatitude(wOutputLatitude.getText());
			ipLocatorMeta.setOutLongitude(wOutputLongitude.getText());
			ipLocatorMeta.setOutZip(wOutputZip.getText());
			ipLocatorMeta.setOutRegion(wOutputRegion.getText());
			ipLocatorMeta.setOutName(wOutputName.getText());
			ipLocatorMeta.setOutDomain(wOutputDomain.getText());
			ipLocatorMeta.setOutCityName(wOutputCityName.getText());
			ipLocatorMeta.setOutCountry(wOutputCountry.getText());
			ipLocatorMeta.setOutAbreviation(wOutputAbbreviation.getText());
			ipLocatorMeta.setOutConnectionSpeed(wOutputConnectionSpeed.getText());
			ipLocatorMeta.setOutConnectionType(wOutputConnectionType.getText());
			ipLocatorMeta.setOutUTC(wOutputUTC.getText());
			ipLocatorMeta.setOutContinent(wOutputContinent.getText());
		}
	}

	public String getHelpURLKey() {
		return "MDCheck.Help.IpLocatorTab";
	}

	public boolean init(MDCheckStepData data) {
		ipMeta = data.getIPLocator();
		wIPInput.setValue(ipMeta.getInIPAddress());
		wOutputIPAddress.setText(ipMeta.getOutIPAddress());
		wOutputLatitude.setText(ipMeta.getOutLatitude());
		wOutputLongitude.setText(ipMeta.getOutLongitude());
		wOutputZip.setText(ipMeta.getOutZip());
		wOutputRegion.setText(ipMeta.getOutRegion());
		wOutputName.setText(ipMeta.getOutName());
		wOutputDomain.setText(ipMeta.getOutDomain());
		wOutputCityName.setText(ipMeta.getOutCityName());
		wOutputCountry.setText(ipMeta.getOutCountry());
		wOutputAbbreviation.setText(ipMeta.getOutAbreviation());
		wOutputConnectionSpeed.setText(ipMeta.getOutConnectionSpeed());
		wOutputConnectionType.setText(ipMeta.getOutConnectionType());
		wOutputUTC.setText(ipMeta.getOutUTC());
		wOutputContinent.setText(ipMeta.getOutContinent());

		enable();
		wIPInput.getComboBox().addSelectionListener(enableListener);
		return false;
	}

	private Group createInputGroup(Composite parent) {
		Group inGroup = new Group(parent, SWT.NONE);
		inGroup.setText(getString("InputIPGroup.Label"));
		helper.setLook(inGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		inGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		fd.bottom = new FormAttachment(100, -helper.margin);
		inGroup.setLayoutData(fd);
		wIPInput = helper.addInputComboBox(inGroup, null, "IPLocatorTab.IPInput", "INIPAddress");
		return inGroup;
	}

	private Group createOutputGroup(Composite parent) {
		Group oGroup = new Group(parent, SWT.NONE);
		oGroup.setText(getString("OutputIPGroup.Label"));
		helper.setLook(oGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		oGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		oGroup.setLayoutData(fd);
		wOutputIPAddress = helper.addTextBox(oGroup, null, "IPLocatorTab.OutputIPAddress");
		wOutputLatitude = helper.addTextBox(oGroup, wOutputIPAddress, "IPLocatorTab.OutputLatitude");
		wOutputLongitude = helper.addTextBox(oGroup, wOutputLatitude, "IPLocatorTab.OutputLongitude");
		wOutputZip = helper.addTextBox(oGroup, wOutputLongitude, "IPLocatorTab.OutputZip");
		wOutputRegion = helper.addTextBox(oGroup, wOutputZip, "IPLocatorTab.OutputRegion");
		wOutputName = helper.addTextBox(oGroup, wOutputRegion, "IPLocatorTab.OutputName");
		wOutputDomain = helper.addTextBox(oGroup, wOutputName, "IPLocatorTab.OutputDomain");
		wOutputCityName = helper.addTextBox(oGroup, wOutputDomain, "IPLocatorTab.OutputCityName");
		wOutputCountry = helper.addTextBox(oGroup, wOutputCityName, "IPLocatorTab.OutputCountry");
		wOutputAbbreviation = helper.addTextBox(oGroup, wOutputCountry, "IPLocatorTab.OutputAbreviation");
		wOutputConnectionSpeed = helper.addTextBox(oGroup, wOutputAbbreviation, "IPLocatorTab.OutputConnectionSpeed");
		wOutputConnectionType = helper.addTextBox(oGroup, wOutputConnectionSpeed, "IPLocatorTab.OutputConnectionType");
		wOutputUTC = helper.addTextBox(oGroup, wOutputConnectionType, "IPLocatorTab.OutputUTC");
		wOutputContinent = helper.addTextBox(oGroup, wOutputUTC, "IPLocatorTab.OutputContinent");

		return oGroup;
	}

	private void enable() {
		if (!ipMeta.isLicensed()) {
			gInput.setText(getString("NotLicensedOutputIPGroup.Label"));
			wIPInput.setEnabled(false);
			gOutput.setText(getString("NotLicensedOutputIPGroup.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (Const.isEmpty(wIPInput.getValue())) {
			gInput.setText(getString("InputIPGroup.Label"));
			wIPInput.setEnabled(true);
			gOutput.setText(getString("NoValueOutputIPGroup.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else {
			gInput.setText(getString("InputIPGroup.Label"));
			wIPInput.setEnabled(true);
			gOutput.setText(getString("OutputIPGroup.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(true);
			}
		}

		AdvancedConfigurationMeta acMeta = dialog.getAdvancedConfigMeta();
		if (((acMeta.getServiceType() == ServiceType.Web) || (acMeta.getServiceType() == ServiceType.CVS))) {

			wOutputConnectionSpeed.setEnabled(true);
			wOutputConnectionType.setEnabled(true);
			wOutputUTC.setEnabled(true);
			wOutputContinent.setEnabled(true);
		} else {
			wOutputConnectionSpeed.setEnabled(false);
			wOutputConnectionType.setEnabled(false);
			wOutputUTC.setEnabled(false);
			wOutputContinent.setEnabled(false);
		}
	}

	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.IPLocatorTab." + key, args);
	}
}
