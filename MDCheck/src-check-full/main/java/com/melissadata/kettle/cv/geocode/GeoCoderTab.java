package com.melissadata.kettle.cv.geocode;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.cv.MDCheckFullDialog;
import com.melissadata.kettle.cv.address.AddressVerifyMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.util.ImageUtil;

public class GeoCoderTab implements MDTab {
	private static Class<?> PKG = MDCheckFullDialog.class;
	private MDCheckDialog   dialog;
	private MDCheckHelper   helper;
	private MDCheckStepData mdcStepData;
	private GeoCoderMeta    gcMeta;
	private Group           gInput;
	private Button          rbSourceNotGeoCoding;
	private Button          rbSourceAVResults;
	private Button          rbSourceAddrKeyColumn;
	private Button          rbSourceAddrComponents;
	private MDInputCombo    cbAddrKeyColumn;
	private MDInputCombo    cbAddrCompLine1Column;
	private MDInputCombo    cbAddrCompLine2Column;
	private MDInputCombo    cbAddrCompCityColumn;
	private MDInputCombo    cbAddrCompStateColumn;
	private MDInputCombo    cbAddrCompZipColumn;
	private Group           gOutput;
	private Text            wOutputLongitude;
	private Text            wOutputLatitude;
	private Label           wResolutionImageLabelUS;
	private Label           wResolutionLabelUS;
	private Label           wResolutionImageLabelCanada;
	private Label           wResolutionLabelCanada;
	private Button          bAdditionalOutput;
	private boolean           requiredFields = true;
	private SelectionListener enableListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			enable();
		}
	};

	public GeoCoderTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		mdcStepData = dialog.getData();
		// Create the tab
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
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description"));
		helper.setLook(description);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Add the input/output groups side-by-side in a single composite just below the description
		Composite wIOComp = new Composite(wComp, 0);
		helper.setLook(wIOComp);
		wIOComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.top = new FormAttachment(description, 4 * helper.margin);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		wIOComp.setLayoutData(fd);
		gInput = new Group(wIOComp, SWT.NONE);
		gInput.setText(getString("InputAddressGroup.Label"));
		helper.setLook(gInput);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gInput.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(60, -helper.margin / 2);
		fd.bottom = new FormAttachment(100, -helper.margin);
		gInput.setLayoutData(fd);
		gOutput = new Group(wIOComp, SWT.NONE);
		gOutput.setText(getString("OutputAddressGroup.Label"));
		helper.setLook(gOutput);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gOutput.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(60, helper.margin / 2);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		gOutput.setLayoutData(fd);
		// Add fields to the input group
		// Define the source of the input address
		Label sourceDescription = helper.addLabel(gInput, null, "GeoCoderTab.SourceDescription");
		rbSourceNotGeoCoding = helper.addRadioButton(gInput, sourceDescription, "GeoCoderTab.SourceNotGeocoding");
		rbSourceAVResults = helper.addRadioButton(gInput, rbSourceNotGeoCoding, "GeoCoderTab.SourceAVResults");
		rbSourceAddrKeyColumn = helper.addRadioButton(gInput, rbSourceAVResults, "GeoCoderTab.SourceAddrKeyColumn");
		rbSourceAddrComponents = helper.addRadioButton(gInput, rbSourceAddrKeyColumn, "GeoCoderTab.SourceAddrComponents");
		// Create input combo boxes
		cbAddrKeyColumn = addInputCombo(gInput, rbSourceAVResults, null, "GInAddrKey");
		cbAddrCompLine1Column = addInputCombo(gInput, rbSourceAddrComponents, "GeoCoderTab.AddrCompLine1", "GInAddrLine1");
		cbAddrCompLine2Column = addInputCombo(gInput, cbAddrCompLine1Column.getComboBox(), "GeoCoderTab.AddrCompLine2", "GInAddrLine2");
		cbAddrCompCityColumn = addInputCombo(gInput, cbAddrCompLine2Column.getComboBox(), "GeoCoderTab.AddrCompCity", "GInAddrCity");
		cbAddrCompStateColumn = addInputCombo(gInput, cbAddrCompCityColumn.getComboBox(), "GeoCoderTab.AddrCompState", "GInAddrState");
		cbAddrCompZipColumn = addInputCombo(gInput, cbAddrCompStateColumn.getComboBox(), "GeoCoderTab.AddrCompZip", "GInAddrZip");
		// Add fields to the output group
		wOutputLongitude = helper.addTextBox(gOutput, null, "GeoCoderTab.OutputLongitude");
		wOutputLatitude = helper.addTextBox(gOutput, wOutputLongitude, "GeoCoderTab.OutputLatitude");
		// Additional Address Info button
		bAdditionalOutput = helper.addPushButton(gOutput, wOutputLatitude, "GeoCoderTab.AdditionalOutput", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent arg0) {
				additionalOutput();
			}
		});
		bAdditionalOutput.setToolTipText(getString("AdditionalOutput.ToolTip"));
		((FormData) bAdditionalOutput.getLayoutData()).top = null;
		((FormData) bAdditionalOutput.getLayoutData()).bottom = new FormAttachment(100, 0);
		// Create the geo-code resolution group
		Group gResolution = new Group(wComp, SWT.NONE);
		gResolution.setText(getString("ResolutionGroup.Label"));
		helper.setLook(gResolution);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gResolution.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wIOComp, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		gResolution.setLayoutData(fd);
		wResolutionImageLabelUS = helper.addSpacer(gResolution, null);
		((FormData) wResolutionImageLabelUS.getLayoutData()).right = new FormAttachment(30, helper.margin);
		wResolutionLabelUS = helper.addLabel(gResolution, wResolutionImageLabelUS, null);
		((FormData) wResolutionLabelUS.getLayoutData()).right = new FormAttachment(30, helper.margin);
// updateResolutionUS();
		wResolutionImageLabelCanada = helper.addSpacer(gResolution, null);
		((FormData) wResolutionImageLabelCanada.getLayoutData()).left = new FormAttachment(wResolutionImageLabelUS, helper.margin);
		wResolutionLabelCanada = helper.addLabel(gResolution, wResolutionImageLabelCanada, null);
		((FormData) wResolutionLabelCanada.getLayoutData()).left = new FormAttachment(wResolutionImageLabelUS, helper.margin);
// updateResolutionCanada();
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

	/*
	 * private void saveDefaults(){
	 * MDCheckData.geoCodeDefaultsSet = "true";
	 * MDCheckData.GeoLatitude = wOutputLatitude.getText().trim();
	 * MDCheckData.GeoLongitude = wOutputLongitude.getText().trim();
	 * }
	 */
	private static boolean isBlank(String value) {
		return (value == null) || (value.trim().length() == 0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Update enablement
		enable();
	}

	public void dispose() {
		// Nothing to do
	}

	/**
	 * Loads the dialog mdcStepData into the meta structure
	 */
	public void getData(MDCheckStepData data) {
		GeoCoderMeta geoCoder = data.getGeoCoder();

		if (rbSourceNotGeoCoding.getSelection()) {
			geoCoder.setAddrKeySource(GeoCoderMeta.AddrKeySource.None);
		} else if (rbSourceAVResults.getSelection()) {
			geoCoder.setAddrKeySource(GeoCoderMeta.AddrKeySource.AddrVerifyResults);
		} else if (rbSourceAddrKeyColumn.getSelection()) {
			geoCoder.setAddrKeySource(GeoCoderMeta.AddrKeySource.InputColumn);
		} else if (rbSourceAddrComponents.getSelection()) {
			geoCoder.setAddrKeySource(GeoCoderMeta.AddrKeySource.ComponentColumns);
		}
		// Fill in the input meta mdcStepData
		geoCoder.setAddrKey(cbAddrKeyColumn.getValue());
		geoCoder.setAddrCompLine1(cbAddrCompLine1Column.getValue());
		geoCoder.setAddrCompLine2(cbAddrCompLine2Column.getValue());
		geoCoder.setAddrCompCity(cbAddrCompCityColumn.getValue());
		geoCoder.setAddrCompState(cbAddrCompStateColumn.getValue());
		geoCoder.setAddrCompZip(cbAddrCompZipColumn.getValue());
		// Fill in the output meta mdcStepData
		geoCoder.setLongitude(wOutputLongitude.getText());
		geoCoder.setLatitude(wOutputLatitude.getText());
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.GeoCoderTab";
	}

	public boolean getSourceNotGeoCoding() {
		return rbSourceNotGeoCoding.getSelection();
	}

	/**
	 * Loads the meta mdcStepData into the dialog tab
	 *
	 * @param data
	 */
	public boolean init(MDCheckStepData data) {
		// Get the geocoder meta mdcStepData
		mdcStepData = data;
		gcMeta = mdcStepData.getGeoCoder();
		// clear buttons so we can refresh
		rbSourceNotGeoCoding.setSelection(false);
		rbSourceAVResults.setSelection(false);
		rbSourceAddrKeyColumn.setSelection(false);
		rbSourceAddrComponents.setSelection(false);
		// If not intiialized correctly or not licensed then reset to default
		// values
		if (!gcMeta.isInitializeOK() || !gcMeta.isLicensed()) {
			gcMeta.setAddrKeySource(GeoCoderMeta.AddrKeySource.None);
		}
		// Load the input controls
		switch (gcMeta.getAddrKeySource()) {
			case None:
				rbSourceNotGeoCoding.setSelection(true);
				break;
			case AddrVerifyResults:
				rbSourceAVResults.setSelection(true);
				break;
			case InputColumn:
				rbSourceAddrKeyColumn.setSelection(true);
				break;
			case ComponentColumns:
				rbSourceAddrComponents.setSelection(true);
				break;
		}
		cbAddrKeyColumn.setValue(gcMeta.getAddrKey());
		cbAddrCompLine1Column.setValue(gcMeta.getAddrCompLine1());
		cbAddrCompLine2Column.setValue(gcMeta.getAddrCompLine2());
		cbAddrCompCityColumn.setValue(gcMeta.getAddrCompCity());
		cbAddrCompStateColumn.setValue(gcMeta.getAddrCompState());
		cbAddrCompZipColumn.setValue(gcMeta.getAddrCompZip());
		// Load the output controls
		wOutputLongitude.setText(gcMeta.getLongitude());
		wOutputLatitude.setText(gcMeta.getLatitude());
		// Set initial enablement
		enable();
		// Add listeners to track enablement
		rbSourceNotGeoCoding.addSelectionListener(enableListener);
		rbSourceAVResults.addSelectionListener(enableListener);
		rbSourceAddrKeyColumn.addSelectionListener(enableListener);
		rbSourceAddrComponents.addSelectionListener(enableListener);
		cbAddrKeyColumn.addSelectionListener(enableListener);
		cbAddrCompLine1Column.addSelectionListener(enableListener);
		cbAddrCompLine2Column.addSelectionListener(enableListener);
		cbAddrCompCityColumn.addSelectionListener(enableListener);
		cbAddrCompStateColumn.addSelectionListener(enableListener);
		cbAddrCompZipColumn.addSelectionListener(enableListener);
		return false;
	}

	// called from MDCheckDialog to get required field and update enablement
	// when geoCoderTab is selected
	public void refreshEnable(boolean reqFields) {
		requiredFields = reqFields;
		requiredFields(requiredFields);
		enable();
	}

	/**
	 * Specialized method for adding input combo boxes. Varies from normal method in that it adjust the width of the combo
	 * box.
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @param usageID
	 * @return
	 */
	private MDInputCombo addInputCombo(Composite parent, Control wLastLine, String name, String usageID) {
		// Create the combo box
		Control[] boxes = helper.addComboBoxes(parent, wLastLine, name, true);
		// Adjust the position of the label and combo box
		Label label = (Label) boxes[0];
		CCombo combo = (CCombo) boxes[1];
		((FormData) label.getLayoutData()).right = new FormAttachment(50, -helper.margin / 2);
		((FormData) combo.getLayoutData()).left = new FormAttachment(50, helper.margin / 2);
		// Associate it with the input
		return new MDInputCombo(boxes, dialog.getSourceFields(), usageID);
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void enable() {
		// Ignore if controls are already disposed
		if (rbSourceAddrComponents.isDisposed()) {
			return;
		}
		// See if geocoding is licensed
//		GeoCoderMeta gcMeta = dialog.getTabData().getGeoCoder();
		boolean isLicensed = gcMeta.isLicensed();
		// Check inputs to see if they are set
		boolean inputSelected = false;
		if (rbSourceNotGeoCoding.getSelection()) {
			inputSelected = false;
		} else if (rbSourceAVResults.getSelection()) {
			inputSelected = requiredFields;
		} else if (rbSourceAddrKeyColumn.getSelection()) {
			inputSelected = !isBlank(cbAddrKeyColumn.getValue());
		} else if (rbSourceAddrComponents.getSelection()) {
			boolean A1 = !isBlank(cbAddrCompLine1Column.getValue());
			boolean A2 = !isBlank(cbAddrCompLine2Column.getValue());
			boolean C = !isBlank(cbAddrCompCityColumn.getValue());
			boolean S = !isBlank(cbAddrCompStateColumn.getValue());
			boolean Z = !isBlank(cbAddrCompZipColumn.getValue());
			inputSelected = (A1 || A2) && ((C && S) || Z);
		}
		// Update display based on enabled features
		if (!isLicensed) {
			gInput.setText(getString("GeoCodeObjNotLicensed.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			gOutput.setText(getString("GeoCodeObjNotLicensed.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (!gcMeta.isInitializeOK()) {
			gInput.setText(getString("GeoCodeObjNotInitialized.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			gOutput.setText(getString("GeoCodeObjNotInitialized.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (!inputSelected) {
			gInput.setText(getString("InputAddressGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			gOutput.setText(getString("NoInputAddressSpecified.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else {
			gInput.setText(getString("InputAddressGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			gOutput.setText(getString("OutputAddressGroup.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(true);
			}
		}
		// Enable address key column
		boolean isEnabled = isLicensed && rbSourceAddrKeyColumn.getSelection() && gcMeta.isInitializeOK();
		cbAddrKeyColumn.setEnabled(isEnabled);
		// Enable address component columns
		isEnabled = isLicensed && rbSourceAddrComponents.getSelection();
		cbAddrCompLine1Column.setEnabled(isEnabled);
		cbAddrCompLine2Column.setEnabled(isEnabled);
		cbAddrCompCityColumn.setEnabled(isEnabled);
		cbAddrCompStateColumn.setEnabled(isEnabled);
		cbAddrCompZipColumn.setEnabled(isEnabled);
		// Enable selection buttons
		AddressVerifyMeta avMeta = mdcStepData.getAddressVerify();
		rbSourceAVResults.setEnabled(isLicensed && avMeta.isLicensed() && requiredFields && gcMeta.isInitializeOK());
		rbSourceAddrKeyColumn.setEnabled(isLicensed && gcMeta.isInitializeOK());
		rbSourceAddrComponents.setEnabled(isLicensed && avMeta.isInitializeOK() && gcMeta.isInitializeOK());
		if (!rbSourceAVResults.isEnabled()) {
			if (rbSourceAVResults.getSelection()) {
				rbSourceNotGeoCoding.setSelection(true);
			}
			rbSourceAVResults.setSelection(false);
		}
		if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_GeoCoder)) {
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}

			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		}

		// Update the geocoding resolution display
		updateResolutionUS();
		updateResolutionCanada();
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.GeoCoderTab." + key, args);
	}

	private void requiredFields(boolean enoughFields) {
		if (rbSourceAVResults.getSelection()) {
			// make sure appropriate address fields have input from addressverifyTab
			if (Const.isEmpty(mdcStepData.getAddressVerify().getInputAddressLine1()) && Const.isEmpty(mdcStepData.getAddressVerify().getInputAddressLine2())) {
				enoughFields = false;
			}
			if (Const.isEmpty(mdcStepData.getAddressVerify().getInputCity()) || Const.isEmpty(mdcStepData.getAddressVerify().getInputState())) {
				if (Const.isEmpty(mdcStepData.getAddressVerify().getInputZip())) {
					enoughFields = false;
				}
			}
		}
		if (rbSourceAddrComponents.getSelection()) {
			// make sure appropriate address fields have input from GeoCodeTab
			if (Const.isEmpty(cbAddrCompLine1Column.getValue()) && Const.isEmpty(cbAddrCompLine2Column.getValue())) {
				enoughFields = false;
			}
			if (Const.isEmpty(cbAddrCompCityColumn.getValue()) || Const.isEmpty(cbAddrCompStateColumn.getValue())) {
				if (Const.isEmpty(cbAddrCompStateColumn.getValue())) {
					enoughFields = false;
				}
			}
		}
		requiredFields = enoughFields;
	}

	private void updateResolutionCanada() {
		AdvancedConfigurationMeta acMeta = mdcStepData.getAdvancedConfiguration();
		// Determine the level of geocoding resolution available
		Resolution resolution = Resolution.NONE;
		// The Canada does not do rooftop level geocoding.
		if (((acMeta.getServiceType() == ServiceType.Web) || (acMeta.getServiceType() == ServiceType.CVS))) {
			if (Integer.parseInt(AdvancedConfigurationMeta.getGeoLevel()) >= 4) {
				resolution = Resolution.MEDIUM;
			} else {
				resolution = Resolution.NONE;
			}
		} else {
			// Determine possible resolution level from the licensed products
			if ((Integer.parseInt(AdvancedConfigurationMeta.getGeoLevel()) >= 4) && (gcMeta.isInitializeOK())) {
				resolution = Resolution.MEDIUM;
			} else {
				resolution = Resolution.NONE;
			}
		}
		// Update the controls
		Image image;
		String descPrefix = getString("ResolutionDescriptionCanada.Label");
		String descSuffix = getString("ResolutionLevelsCanada.Label");
		switch (resolution) {
			case HIGH:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter100.bmp");
				wResolutionImageLabelCanada.setImage(image);
				wResolutionLabelCanada.setText(descPrefix + " " + getString("ResolutionHigh.Label") + descSuffix);
				break;
			case MEDIUM:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter67.bmp");
				wResolutionImageLabelCanada.setImage(image);
				wResolutionLabelCanada.setText(descPrefix + " " + getString("ResolutionMedium.Label") + descSuffix);
				break;
			case LOW:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter33.bmp");
				wResolutionImageLabelCanada.setImage(image);
				wResolutionLabelCanada.setText(descPrefix + " " + getString("ResolutionLow.Label") + descSuffix);
				break;
			case NONE:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter0.bmp");
				wResolutionImageLabelCanada.setImage(image);
				wResolutionLabelCanada.setText(descPrefix + " " + getString("ResolutionNone.Label") + descSuffix);
				break;
		}
	}

	/**
	 * Called to update the geo code resolution level display
	 */
	private void updateResolutionUS() {
		AdvancedConfigurationMeta acMeta = mdcStepData.getAdvancedConfiguration();
		// Determine the level of geocoding resolution available
		Resolution resolution = Resolution.NONE;
		// The web service always does 5zip level geocoding.
		if (((acMeta.getServiceType() == ServiceType.Web) || (acMeta.getServiceType() == ServiceType.CVS))) {
			resolution = Resolution.MEDIUM;
		} else {
			// Determine possible resolution level from the licensed products
// acMeta.getLicense(AdvancedConfigurationMeta.TAG_PRIMARY_LICENSE)
			int products = acMeta.getProducts();
			// Determine the resolution level based on the licnesed products (GeoPoint > GeoCode > Addr)
			//GeoCoderMeta gcMeta = dialog.getTabData().getGeoCoder();
			if (((products & AdvancedConfigurationMeta.MDLICENSE_GeoPoint) != 0) && gcMeta.isInitializeOK()) {
				resolution = Resolution.HIGH;
			} else if (((products & AdvancedConfigurationMeta.MDLICENSE_GeoCode) != 0) && gcMeta.isInitializeOK()) {
				resolution = Resolution.MEDIUM;
			} else if (((products & AdvancedConfigurationMeta.MDLICENSE_Address) != 0) && mdcStepData.getAddressVerify().isInitializeOK() && gcMeta.isInitializeOK()) {
				resolution = Resolution.LOW;
			} else {
				resolution = Resolution.NONE;
			}
		}
		// Update the controls
		Image image;
		String descPrefix = getString("ResolutionDescriptionUS.Label");
		String descSuffix = getString("ResolutionLevelsUS.Label");
		switch (resolution) {
			case HIGH:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter100.bmp");
				wResolutionImageLabelUS.setImage(image);
				wResolutionLabelUS.setText(descPrefix + " " + getString("ResolutionHigh.Label") + descSuffix);
				break;
			case MEDIUM:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter67.bmp");
				wResolutionImageLabelUS.setImage(image);
				wResolutionLabelUS.setText(descPrefix + " " + getString("ResolutionMedium.Label") + descSuffix);
				break;
			case LOW:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter33.bmp");
				wResolutionImageLabelUS.setImage(image);
				wResolutionLabelUS.setText(descPrefix + " " + getString("ResolutionLow.Label") + descSuffix);
				break;
			case NONE:
				image = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/Meter0.bmp");
				wResolutionImageLabelUS.setImage(image);
				wResolutionLabelUS.setText(descPrefix + " " + getString("ResolutionNone.Label") + descSuffix);
				break;
		}
	}

	/**
	 * Called to display Additional Output Fields
	 */
	protected void additionalOutput() {
		MDAbstractDialog aind = new AdditionalGeoCoderOutputDialog(dialog);
		if (aind.open()) {
			dialog.setChanged();
		}
	}

	private enum Resolution {
		HIGH, MEDIUM, LOW, NONE
	}
}
