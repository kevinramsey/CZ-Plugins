package com.melissadata.kettle.sm.ui;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.sm.SmartMoverMeta;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;

// import org.pentaho.pms.util.Const;


public class SmartMoverOutputTab implements MDTab {
	private static Class<?> PKG = MDCheckMeta.class;
	private MDCheckDialog  dialog;
	private MDCheckHelper  helper;
	private SmartMoverMeta smMeta;
	private Text           wOutputFullName;
	private Text           wOutputNamePrefix;
	private Text           wOutputNameFirst;
	private Text           wOutputNameMiddle;
	private Text           wOutputNameLast;
	private Text           wOutputNameSuffix;
	private Text           wOutputAddrCompany;
	private Text           wOutputAddrLine;
	private Text           wOutputAddrLine2;
	private Text           wOutputAddrCity;
	private Text           wOutputAddrState;
	private Text           wOutputAddrZip;
	private Text           wOutputAddrKey;
	private Text           wOutputMelissaAddrKey;
	private Text           wOutputMelissaBaseAddrKey;
	private Text           wOutputEffectiveDate;
	private Text           wMoveTypeCode;
	private Text           wMoveReturnCode;
	private Group          gOutputName;

	public SmartMoverOutputTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		wTab.setData(this);
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Add descriptive label
		Label description = helper.addLabel(wComp, null, "SmartMoverOutputTab.Description");
		Label spacer = helper.addSpacer(wComp, description);
		spacer = helper.addSpacer(wComp, spacer);
		// Create columns for groups of controls
		Composite wLeftComp = new Composite(wComp, SWT.NONE);
		helper.setLook(wLeftComp);
		wLeftComp.setLayout(new FormLayout());
		Composite wRightComp = new Composite(wComp, SWT.NONE);
		helper.setLook(wRightComp);
		wRightComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin / 2);
		fd.top = new FormAttachment(spacer, helper.margin / 2);
		fd.right = new FormAttachment(50, 0);
		wLeftComp.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(spacer, helper.margin / 2);
		fd.right = new FormAttachment(100, 0);
		wRightComp.setLayoutData(fd);
		// Add the output groups
		gOutputName = new Group(wLeftComp, SWT.NONE);
		gOutputName.setText(getString("OutputNameGroup.Label"));
		helper.setLook(gOutputName);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gOutputName.setLayout(fl);
		Group gOutputAddress = new Group(wRightComp, SWT.NONE);
		gOutputAddress.setText(getString("OutputAddressGroup.Label"));
		helper.setLook(gOutputAddress);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gOutputAddress.setLayout(fl);
		Group gMoveInformation = new Group(wRightComp, SWT.NONE);
		gMoveInformation.setText(getString("MoveInformationGroup.Label"));
		helper.setLook(gMoveInformation);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gMoveInformation.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin / 2);
		fd.top = new FormAttachment(0, helper.margin / 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		gOutputName.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin / 2);
		fd.top = new FormAttachment(0, helper.margin / 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		gOutputAddress.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin / 2);
		fd.top = new FormAttachment(gOutputAddress, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		gMoveInformation.setLayoutData(fd);
		// Additional button for control input for output transparency
		Button btnUseInputForOutput = helper.addPushButton(wLeftComp, gOutputName, "SmartMoverOutputTab.UseInputForOutput", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				useInputForOutput();
			}
		});
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin / 2);
		fd.top = new FormAttachment(gOutputName, helper.margin);
		btnUseInputForOutput.setLayoutData(fd);
		// Add fields to the output name group
		wOutputFullName = helper.addTextBox(gOutputName, null, "SmartMoverOutputTab.OutputFullName");
		wOutputNamePrefix = helper.addTextBox(gOutputName, wOutputFullName, "SmartMoverOutputTab.OutputNamePrefix");
		wOutputNameFirst = helper.addTextBox(gOutputName, wOutputNamePrefix, "SmartMoverOutputTab.OutputNameFirst");
		wOutputNameMiddle = helper.addTextBox(gOutputName, wOutputNameFirst, "SmartMoverOutputTab.OutputNameMiddle");
		wOutputNameLast = helper.addTextBox(gOutputName, wOutputNameMiddle, "SmartMoverOutputTab.OutputNameLast");
		wOutputNameSuffix = helper.addTextBox(gOutputName, wOutputNameLast, "SmartMoverOutputTab.OutputNameSuffix");
		// Add fields to the output address group
		wOutputAddrCompany = helper.addTextBox(gOutputAddress, null, "SmartMoverOutputTab.OutputAddrCompany");
		wOutputAddrLine = helper.addTextBox(gOutputAddress, wOutputAddrCompany, "SmartMoverOutputTab.OutputAddrLine");
		wOutputAddrLine2 = helper.addTextBox(gOutputAddress, wOutputAddrLine, "SmartMoverOutputTab.OutputAddrLine2");
		wOutputAddrCity = helper.addTextBox(gOutputAddress, wOutputAddrLine2, "SmartMoverOutputTab.OutputAddrCity");
		wOutputAddrState = helper.addTextBox(gOutputAddress, wOutputAddrCity, "SmartMoverOutputTab.OutputAddrState");
		wOutputAddrZip = helper.addTextBox(gOutputAddress, wOutputAddrState, "SmartMoverOutputTab.OutputAddrZip");
		wOutputAddrKey = helper.addTextBox(gOutputAddress, wOutputAddrZip, "SmartMoverOutputTab.OutputAddrKey");

		wOutputMelissaAddrKey = helper.addTextBox(gOutputAddress, wOutputAddrKey, "SmartMoverOutputTab.OutputMelissaAddrKey");
		wOutputMelissaBaseAddrKey = helper.addTextBox(gOutputAddress, wOutputMelissaAddrKey, "SmartMoverOutputTab.OutputMelissaBaseAddrKey");

		// Additional output columns
		helper.addPushButton(gOutputAddress, wOutputMelissaBaseAddrKey, "SmartMoverOutputTab.AdditionalOutputColumns", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				additionalOutputColumns();
			}
		});
		// Add fields to the move information group
		wOutputEffectiveDate = helper.addTextBox(gMoveInformation, null, "SmartMoverOutputTab.OutputEffectiveDate");
		wMoveTypeCode = helper.addTextBox(gMoveInformation, wOutputEffectiveDate, "SmartMoverOutputTab.MoveTypeCode");
		wMoveReturnCode = helper.addTextBox(gMoveInformation, wMoveTypeCode, "SmartMoverOutputTab.MoveReturnCode");
		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
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
		// Initialzie the tab with the scrolled composite
		wTab.setControl(wSComp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Nothing to do here
	}

	public void dispose() {
		// Nothing to do
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {
		SmartMoverMeta smartMoverMeta = data.getSmartMover();
		// Fill in the input meta data
		smartMoverMeta.setOutputFullName(wOutputFullName.getText());
		smartMoverMeta.setOutputNamePrefix(wOutputNamePrefix.getText());
		smartMoverMeta.setOutputNameFirst(wOutputNameFirst.getText());
		smartMoverMeta.setOutputNameMiddle(wOutputNameMiddle.getText());
		smartMoverMeta.setOutputNameLast(wOutputNameLast.getText());
		smartMoverMeta.setOutputNameSuffix(wOutputNameSuffix.getText());
		smartMoverMeta.setOutputAddrCompany(wOutputAddrCompany.getText());
		smartMoverMeta.setOutputAddrLine(wOutputAddrLine.getText());
		smartMoverMeta.setOutputAddrLine2(wOutputAddrLine2.getText());
		smartMoverMeta.setOutputAddrCity(wOutputAddrCity.getText());
		smartMoverMeta.setOutputAddrState(wOutputAddrState.getText());
		smartMoverMeta.setOutputAddrZip(wOutputAddrZip.getText());
		smartMoverMeta.setOutputAddrKey(wOutputAddrKey.getText());

		smartMoverMeta.setOutputMelissaAddrKey(wOutputMelissaAddrKey.getText());
		smartMoverMeta.setOutputBaseMelissaAddrKey(wOutputMelissaBaseAddrKey.getText());

		smartMoverMeta.setOutputEffectiveDate(wOutputEffectiveDate.getText());
		smartMoverMeta.setMoveTypeCode(wMoveTypeCode.getText());
		smartMoverMeta.setMoveReturnCode(wMoveReturnCode.getText());
		// we call this here so it will reflect if there is a change in another tab.
		enable();
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		if (!MDCheckMeta.isPentahoPlugin()) {
			return "MDCheck.Help.SmartMoverOutputTab";
		} else {
			return "MDCheck.Plugin.Help.SmartMoverOutputTab";
		}
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 */
	public boolean init(MDCheckStepData data) {
		// Get the smart mover meta data
		smMeta = data.getSmartMover();
		// Load the output controls
		wOutputFullName.setText(smMeta.getOutputFullName());
		wOutputNamePrefix.setText(smMeta.getOutputNamePrefix());
		wOutputNameFirst.setText(smMeta.getOutputNameFirst());
		wOutputNameMiddle.setText(smMeta.getOutputNameMiddle());
		wOutputNameLast.setText(smMeta.getOutputNameLast());
		wOutputNameSuffix.setText(smMeta.getOutputNameSuffix());
		wOutputAddrCompany.setText(smMeta.getOutputAddrCompany());
		wOutputAddrLine.setText(smMeta.getOutputAddrLine());
		wOutputAddrLine2.setText(smMeta.getOutputAddrLine2());
		wOutputAddrCity.setText(smMeta.getOutputAddrCity());
		wOutputAddrState.setText(smMeta.getOutputAddrState());
		wOutputAddrZip.setText(smMeta.getOutputAddrZip());
		wOutputAddrKey.setText(smMeta.getOutputAddrKey());

		wOutputMelissaAddrKey.setText(smMeta.getOutputMelissaAddrKey());
		wOutputMelissaBaseAddrKey.setText(smMeta.getOutputBaseMelissaAddrKey());

		wOutputEffectiveDate.setText(smMeta.getOutputEffectiveDate());
		wMoveTypeCode.setText(smMeta.getMoveTypeCode());
		wMoveReturnCode.setText(smMeta.getMoveReturnCode());
		enable();
		return false;
	}

	/**
	 * Displays the additional output columns dialog
	 */
	private void additionalOutputColumns() {
		MDAbstractDialog smaod = new SmartMoverAdditionalOutputDialog(dialog);
		if (smaod.open()) {
			dialog.setChanged();
		}
	}

	private void enable() {
		//SmartMoverMeta smMeta = dialog.getTabData().getSmartMover();
		boolean isEnable = true;
		if (smMeta.getInputUseFullName()) {
			if (Const.isEmpty(smMeta.getInputFullName())) {
				isEnable = false;
			}
		}
		if (!smMeta.getInputUseFullName()) {
			if (Const.isEmpty(smMeta.getInputNameFirst()) && Const.isEmpty(smMeta.getInputNameLast())) {
				isEnable = false;
			}
		}
		if (!isEnable) {
			for (Control child : gOutputName.getChildren()) {
				child.setEnabled(false);
			}
			wOutputFullName.setText("");
			wOutputNamePrefix.setText("");
			wOutputNameFirst.setText("");
			wOutputNameMiddle.setText("");
			wOutputNameLast.setText("");
			wOutputNameSuffix.setText("");
		} else {
			for (Control child : gOutputName.getChildren()) {
				child.setEnabled(true);
			}
			if (Const.isEmpty(smMeta.getOutputFullName()) && Const.isEmpty(smMeta.getOutputNamePrefix()) && Const.isEmpty(smMeta.getOutputNameFirst()) && Const.isEmpty(smMeta.getOutputNameMiddle()) && Const.isEmpty(smMeta.getOutputNameLast())
					&& Const.isEmpty(smMeta.getOutputNameSuffix())) {
				wOutputFullName.setText("SM_FullName");
				wOutputNamePrefix.setText("SM_Prefix");
				wOutputNameFirst.setText("SM_FirstName");
				wOutputNameMiddle.setText("SM_MiddleName");
				wOutputNameLast.setText("SM_LastName");
				wOutputNameSuffix.setText("SM_Suffix");
			}
		}
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.SmartMoverOutputTab." + key, args);
	}

	/**
	 * Process the use input for output control
	 */
	private void useInputForOutput() {
		// Update data from controls
		getData(dialog.getData());
		// Copy names of input fields to output fields
		smMeta.setOutputFullName(smMeta.getInputFullName());
		smMeta.setOutputNamePrefix(smMeta.getInputNamePrefix());
		smMeta.setOutputNameFirst(smMeta.getInputNameFirst());
		smMeta.setOutputNameMiddle(smMeta.getInputNameMiddle());
		smMeta.setOutputNameLast(smMeta.getInputNameLast());
		smMeta.setOutputNameSuffix(smMeta.getInputNameSuffix());
		smMeta.setOutputAddrCompany(smMeta.getInputAddrCompany());
		smMeta.setOutputAddrLine(smMeta.getInputAddrLine());
		smMeta.setOutputAddrLine2(smMeta.getInputAddrLine2());
		smMeta.setOutputAddrSuite(smMeta.getInputAddrSuite());
		smMeta.setOutputAddrPMB(smMeta.getInputAddrPMB());
		smMeta.setOutputAddrUrbanization(smMeta.getInputAddrUrbanization());
		smMeta.setOutputAddrCity(smMeta.getInputAddrCity());
		smMeta.setOutputAddrState(smMeta.getInputAddrState());
		smMeta.setOutputAddrZip(smMeta.getInputAddrZip());
		smMeta.setOutputAddrPlus4(smMeta.getInputAddrPlus4());
		smMeta.setOutputAddrCountry(smMeta.getInputAddrCountry());
		// Refresh controls from data
		init(dialog.getData());
		// Flag change
		dialog.setChanged();
	}
}
