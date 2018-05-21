package com.melissadata.kettle.personator.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import com.melissadata.kettle.personator.MDPersonatorDialog;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.support.MDPersonatorHelper;
import com.melissadata.kettle.personator.support.MDTab;

public class PersonatorOptionsTab implements MDTab {

	private static Class<?> PKG = MDPersonatorMeta.class;
	private MDPersonatorHelper    helper;
	private Group                 actionsGroup;
	private Group                 addressOptionsGroup;
	private Group                 demographicsMaskGroup;
	private Button                ckCheck;
	private Button                ckVerify;
	private Button                ckAppend;
	//private Button rVerifyAppend;
	private Button                ckMove;
	//	private Button rLoose;
//	private Button rStrict;
	private Button                rBlank;
	private Button                rAlways;
	private Button                rCheckError;
	private Button                ckAdvancedAddressCorrection;
	private Button                ckDemoMask;
	private Combo                 comboDemoMask;
	private CCombo                ccDemoMask;
	private AdvancedOptionsDialog advanceOptionDialog;
	private MDPersonatorDialog    dialog;

	public PersonatorOptionsTab(MDPersonatorDialog dialog) {

		this.dialog = dialog;
		helper = dialog.getHelper();
		// Create the tab
		final CTabFolder wTabFolder = dialog.getTabFolder();
		final CTabItem   wTab       = new CTabItem(dialog.getTabFolder(), SWT.NONE);
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

		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description"));
		helper.setLook(description);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);

		actionsGroup = createActionsGroup(wComp, description);
		addressOptionsGroup = createAddressGroup(wComp, actionsGroup);
		demographicsMaskGroup = createDemographiscmaskGroup(wComp, addressOptionsGroup);

		helper.addPushButton(wComp, demographicsMaskGroup, "AdvancedOpt.Button", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				advancedOptions();
			}
		});

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

		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);

		wTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {

				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {

				if (wTabFolder.getSelection() == wTab) {
					setMapping();
				}
			}
		});
	}

	private void advancedOptions() {

		advanceOptionDialog = new AdvancedOptionsDialog(dialog);
		advanceOptionDialog.open();
	}

	private Group createActionsGroup(Composite parent, Control last) {

		Group actionGroup = new Group(parent, SWT.NONE);
		actionGroup.setText(getString("ActionGroup.Label"));
		helper.setLook(actionGroup);
		int offset = 10;

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		actionGroup.setLayoutData(fd);
		actionGroup.setLayout(fl);

		Composite wActionsComp = new Composite(actionGroup, 0);
		helper.setLook(wActionsComp);
		wActionsComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		wActionsComp.setLayoutData(fd);
		last = null;
		/*
		*Check
		*/
		ckCheck = helper.addCheckBox(wActionsComp, last, "OptionsTab.Action.Check");
		fd = new FormData();
		fd.top = new FormAttachment(null, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(offset, helper.margin);
		ckCheck.setLayoutData(fd);

		Label lCheck = helper.addLabel(wActionsComp, last, "OptionsTab.Action.Check.Description");
		fd = new FormData();
		fd.top = new FormAttachment(null, helper.margin);
		fd.left = new FormAttachment(ckCheck, helper.margin);
		lCheck.setLayoutData(fd);
		last = lCheck;
		/*
		*Verify
		*/
		ckVerify = helper.addCheckBox(wActionsComp, last, "OptionsTab.Action.Verify");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(offset, helper.margin);
		ckVerify.setLayoutData(fd);

		Label lVerify = helper.addLabel(wActionsComp, last, "OptionsTab.Action.Verify.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(ckVerify, helper.margin);
		lVerify.setLayoutData(fd);
		last = lVerify;
		/*
			Move
		 */
		ckMove = helper.addCheckBox(wActionsComp, last, "OptionsTab.Action.Move");
		fd = new FormData();
		fd.top = new FormAttachment(last, 3 * helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(offset, helper.margin);
		ckMove.setLayoutData(fd);

		Label lMove = helper.addLabel(wActionsComp, last, "OptionsTab.Action.Move.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, 3 * helper.margin);
		fd.left = new FormAttachment(ckMove, helper.margin);
		lMove.setLayoutData(fd);
		//last = lMove;

//		rLoose = helper.addRadioButton(wActionsComp, last, "OptionsTab.Action.Move.Loose");
//		fd = new FormData();
//		fd.top = new FormAttachment(last, helper.margin);
//		fd.left = new FormAttachment(0, helper.margin * 5);
//		fd.right = new FormAttachment(offset, helper.margin);
//		rLoose.setLayoutData(fd);
//		last = rLoose;
//
//		rStrict = helper.addRadioButton(wActionsComp, last, "OptionsTab.Action.Move.Strict");
//		fd = new FormData();
//		fd.top = new FormAttachment(last, helper.margin);
//		fd.left = new FormAttachment(0, helper.margin * 5);
//		fd.right = new FormAttachment(offset, helper.margin);
//		rStrict.setLayoutData(fd);
//		last = rStrict;

		ckVerify.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				enable();
			}
		});
		ckMove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				enable();
			}
		});
		helper.colWidth[2] = 100;
		Label spacer1 = helper.addSpacer(wActionsComp, last);
		last = spacer1;

		Composite optGroup = new Composite(actionGroup, 0);
		helper.setLook(optGroup);
		optGroup.setLayout(new FormLayout());
		fd = new FormData();
		fd.top = new FormAttachment(wActionsComp, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		optGroup.setLayoutData(fd);

		/*
			Append
		 */
		last = null;
		ckAppend = helper.addCheckBox(optGroup, last, "OptionsTab.Action.Append");
		fd = new FormData();
		fd.top = new FormAttachment(last, 3 * helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(offset, helper.margin);
		ckAppend.setLayoutData(fd);

		Label lAppend = helper.addLabel(optGroup, last, "OptionsTab.Action.Append.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, 3 * helper.margin);
		fd.left = new FormAttachment(ckAppend, helper.margin);
		lAppend.setLayoutData(fd);
		last = lAppend;

		ckAppend.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				enable();
			}
		});

		Label spacer2 = helper.addSpacer(optGroup, last);
		last = spacer2;
		Label lAppendType = helper.addLabel(optGroup, last, "OptionsTab.AppendType");
		last = lAppendType;
		Label spacer3 = helper.addSpacer(optGroup, last);
		last = spacer3;

		// Blank
		rBlank = helper.addRadioButton(optGroup, last, "OptionsTab.Blank");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(offset, helper.margin);
		rBlank.setLayoutData(fd);

		Label lBlank = helper.addLabel(optGroup, spacer2, "OptionsTab.Blank.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(rBlank, helper.margin);
		lBlank.setLayoutData(fd);
		last = lBlank;

		//Always
		rAlways = helper.addRadioButton(optGroup, last, "OptionsTab.Always");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(offset, helper.margin);
		rAlways.setLayoutData(fd);

		Label lAlways = helper.addLabel(optGroup, last, "OptionsTab.Always.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(rAlways, helper.margin);
		lAlways.setLayoutData(fd);
		last = lAlways;

		// Check Error
		rCheckError = helper.addRadioButton(optGroup, last, "OptionsTab.CheckError");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(offset, helper.margin);
		rCheckError.setLayoutData(fd);

		Label lCheckError = helper.addLabel(optGroup, spacer2, "OptionsTab.CheckError.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(rCheckError, helper.margin);
		lCheckError.setLayoutData(fd);
		// end
		return actionGroup;
	}

	private Group createAddressGroup(Composite parent, Control last) {

		Group addressGroup = new Group(parent, SWT.NONE);
		addressGroup.setText(getString("AddressGroup.Label"));
		helper.setLook(addressGroup);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		addressGroup.setLayoutData(fd);

		addressGroup.setLayout(fl);

		Composite wAddressComp = new Composite(addressGroup, 0);
		helper.setLook(wAddressComp);
		wAddressComp.setLayout(new FormLayout());

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wAddressComp.setLayoutData(fd);

		last = null;
		last = helper.addLabel(wAddressComp, last, "OptionsTab.AdvancedAddressCorrection.Description");
		Label spacer1 = helper.addSpacer(wAddressComp, last);
		last = spacer1;
		last = ckAdvancedAddressCorrection = helper.addCheckBox(wAddressComp, last, "OptionsTab.AdvancedAddressCorrection");

		return addressGroup;
	}

	private Group createDemographiscmaskGroup(Composite parent, Control last) {

		String[] maskingOptions = { PersonatorFields.DEMOGRAPHICS_MASK_OPTION_NONE, PersonatorFields.DEMOGRAPHICS_MASK_OPTION_YES, PersonatorFields.DEMOGRAPHICS_MASK_OPTION_MASK, PersonatorFields.DEMOGRAPHICS_MASK_OPTION_MASKONLY, PersonatorFields.DEMOGRAPHICS_MASK_OPTION_VALUEONLY };
		Label lDescriptionSpacer;
		Label lYes;
		Label lYesDescription;
		Label lMask;
		Label lMaskDescription;
		Label lMaskOnly;
		Label lMaskOnlyDescription;
		Label lValueOnly;
		Label lValueOnlyDescription;
		Label lCCSpacer;

		ModifyListener changeListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				dialog.setChanged();
			}
		};

		Group demoMaskGroup = new Group(parent, SWT.NONE);
		demoMaskGroup.setText(getString("DemoMaskGroup.Label"));
		helper.setLook(demoMaskGroup);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		demoMaskGroup.setLayoutData(fd);

		demoMaskGroup.setLayout(fl);

		Composite wMaskComp = new Composite(demoMaskGroup, 0);
		helper.setLook(wMaskComp);
		wMaskComp.setLayout(new FormLayout());

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wMaskComp.setLayoutData(fd);

		last = null;
		last = helper.addLabel(wMaskComp, last, "OptionsTab.DemoMask.Description");
		last = lDescriptionSpacer = helper.addSpacer(wMaskComp, last);

		lYes = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lDescriptionSpacer, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(5, 0);
		lYes.setLayoutData(fd);
		helper.setLook(lYes);
		lYes.setText(getString("DemoMask.Yes.Label"));
		//lYes.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		lYesDescription = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lDescriptionSpacer, 0);
		fd.left = new FormAttachment(lYes, 0);
		lYesDescription.setLayoutData(fd);
		helper.setLook(lYesDescription);
		lYesDescription.setText(getString("DemoMask.Yes.Description.Label"));

		lMask = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lYes, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(5, 0);
		lMask.setLayoutData(fd);
		helper.setLook(lMask);
		lMask.setText(getString("DemoMask.Mask.Label"));
		//lMask.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		lMaskDescription = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lYes, 0);
		fd.left = new FormAttachment(lMask, 0);
		lMaskDescription.setLayoutData(fd);
		helper.setLook(lMaskDescription);
		lMaskDescription.setText(getString("DemoMask.Mask.Description.Label"));

		lMaskOnly = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lMask, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(5, 0);
		lMaskOnly.setLayoutData(fd);
		helper.setLook(lMaskOnly);
		lMaskOnly.setText(getString("DemoMask.MaskOnly.Label"));
		//lMaskOnly.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		lMaskOnlyDescription = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lMask, 0);
		fd.left = new FormAttachment(lMaskOnly, 0);
		lMaskOnlyDescription.setLayoutData(fd);
		helper.setLook(lMaskOnlyDescription);
		lMaskOnlyDescription.setText(getString("DemoMask.MaskOnly.Description.Label"));

		lValueOnly = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lMaskOnly, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(5, 0);
		lValueOnly.setLayoutData(fd);
		helper.setLook(lValueOnly);
		lValueOnly.setText(getString("DemoMask.ValueOnly.Label"));
		//lValueOnly.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		lValueOnlyDescription = new Label(wMaskComp, SWT.None);
		fd = new FormData();
		fd.top = new FormAttachment(lMaskOnly, 0);
		fd.left = new FormAttachment(lValueOnly, 0);
		lValueOnlyDescription.setLayoutData(fd);
		helper.setLook(lValueOnlyDescription);
		lValueOnlyDescription.setText(getString("DemoMask.ValueOnly.Description.Label"));

		lCCSpacer = helper.addSpacer(wMaskComp, lValueOnlyDescription);
		ccDemoMask = new CCombo(wMaskComp, SWT.BORDER);
		fd = new FormData();
		fd.top = new FormAttachment(lCCSpacer, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(10, 0);
		//fd.bottom = new FormAttachment(100, 0);
		ccDemoMask.setLayoutData(fd);
		helper.setLook(ccDemoMask);
		ccDemoMask.setItems(maskingOptions);
		ccDemoMask.addModifyListener(changeListener);
		return demoMaskGroup;
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param metaPersonator
	 * @return
	 */
	public boolean init(MDPersonatorMeta metaPersonator) {
		// Load the controls

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ACTION).metaValue)) {
			//String
			setAction(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ACTION).metaValue);
		}

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_APPEND).metaValue)) {
			String appendType = metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_APPEND).metaValue;
			refreshAppendType(appendType);
		}

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_DEMOGRAPHICS).metaValue)) {
			String masking = metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_DEMOGRAPHICS).metaValue;
			ccDemoMask.setText(masking);
		}

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_ADVANCED_ADDR_CORRECT).metaValue)) {
			ckAdvancedAddressCorrection.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_ADVANCED_ADDR_CORRECT).metaValue));
		}

		ckCheck.setSelection(true);
		ckCheck.setEnabled(false);

		enable();

		return true;
	}

	private void setMapping() {

		dialog.disableMapping();
	}

	private void setAction(String action) {

		ckVerify.setSelection(false);
		ckAppend.setSelection(false);
		ckMove.setSelection(false);

		if (action.contains(PersonatorFields.ACTION_VERIFY)) {
			ckVerify.setSelection(true);
		}
		if (action.contains(PersonatorFields.ACTION_APPEND)) {
			ckAppend.setSelection(true);
		}
		if (action.contains(PersonatorFields.ACTION_MOVE)) {
			ckMove.setSelection(true);
		}

		/*
		if ("Verify".equals(action)) {
			rVerify.setSelection(true);
		}
		if ("Append".equals(action)) {
			rAppend.setSelection(true);
		}
		// if("VerifyAppend".equals(action)){
		// rVerifyAppend.setSelection(true);
		// }
		if ("Move".equals(action)) {
			rMove.setSelection(true);
		}
		*/
	}

	private String getAction() {

		String defaultAction = PersonatorFields.ACTION_VERIFY;
		String actions       = "";

		if (ckVerify.getSelection()) {
			actions += PersonatorFields.ACTION_VERIFY;
		}
		if (ckAppend.getSelection()) {
			actions += PersonatorFields.ACTION_APPEND;
		}
		//if(rVerifyAppend.getSelection()){
		//	actions +=  "VerifyAppend";
		//}
		if (ckMove.getSelection()) {
			actions += PersonatorFields.ACTION_MOVE;
		}

		return actions;

//		if(!Const.isEmpty(actions)){
//			return actions;
//		}else{
//		return defaultAction;
//		}
	}

	private void refreshAppendType(String appendType) {

		rAlways.setSelection(false);
		rBlank.setSelection(false);
		rCheckError.setSelection(false);

		if (PersonatorFields.APPEND_ALWAYS.equals(appendType)) {
			rAlways.setSelection(true);
		}
		if (PersonatorFields.APPEND_BLANK.equals(appendType)) {
			rBlank.setSelection(true);
		}
		if (PersonatorFields.APPEND_CHECK_ERROR.equals(appendType)) {
			rCheckError.setSelection(true);
		}
	}

//	private void refreshMoveConfidence(String confidence) {
//		rLoose.setSelection(false);
//		rStrict.setSelection(false);
//
//
//		if (PersonatorFields.MOVE_LOOSE.equals(confidence)) {
//			rLoose.setSelection(true);
//		}
//		if (PersonatorFields.MOOVE_STRICT.equals(confidence)) {
//			rStrict.setSelection(true);
//		}
//
//	}

	private String getAppendType() {

		String type = "";

		if (rAlways.getSelection()) {
			type = PersonatorFields.APPEND_ALWAYS;
		}
		if (rBlank.getSelection()) {
			type = PersonatorFields.APPEND_BLANK;
		}
		if (rCheckError.getSelection()) {
			type = PersonatorFields.APPEND_CHECK_ERROR;
		}

		return type;
	}

//	private String getMoveConfidence() {
//		String type = "";
//
//		if (rLoose.getSelection()) {
//			type = PersonatorFields.MOVE_LOOSE;
//		}
//		if (rStrict.getSelection()) {
//			type = PersonatorFields.MOOVE_STRICT;
//		}
//
//		return type;
//	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDPersonatorMeta metaPersonator) {

		// Fill it in
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ACTION).metaValue = getAction();
		//		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue = ccVerifyCentricHint.getText();

		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_APPEND).metaValue = getAppendType();
//		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_MOVE_CONFIDENCE).metaValue = getMoveConfidence();

		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_ADVANCED_ADDR_CORRECT).metaValue = String.valueOf(ckAdvancedAddressCorrection.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_DEMOGRAPHICS).metaValue = ccDemoMask.getText();//getMaskingValue();//ckDemoMask.getSelection() ? PersonatorFields.DEMOGRAPHICS_MASK_ON : PersonatorFields.DEMOGRAPHICS_MASK_OFF;
	}

	private String  getMaskingValue(){
		String val = "";

		return val;
	}

	private boolean actionSelected() {

		if (!ckMove.getSelection() && !ckVerify.getSelection() && !ckAppend.getSelection()) {

			MessageBox box = new MessageBox(helper.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(getString("ActionSelect.Label"));
			box.setMessage(getString("ActionSelect.Message"));
			box.open();

			//ckVerify.setSelection(true);

			return true;
		} else {

			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Check for enablement change
		enable();
	}

	/**
	 * Called to handle enable of controls based on output settings
	 */
	public void enable() {

		if (ckVerify.getSelection() && !ckAppend.getSelection() && !ckMove.getSelection()) {
			rAlways.setSelection(false);// fase
			rBlank.setSelection(false);
			rCheckError.setSelection(false);

			rAlways.setEnabled(false);
			rBlank.setEnabled(false);
			rCheckError.setEnabled(false);
		}

		if (ckAppend.getSelection()) {
			rAlways.setEnabled(true); // true
			rBlank.setEnabled(true);
			rCheckError.setEnabled(true);

			if (Const.isEmpty(getAppendType())) {
				rBlank.setSelection(true);
			}
		} else {
			rAlways.setSelection(false);// fase
			rBlank.setSelection(false);
			rCheckError.setSelection(false);

			rAlways.setEnabled(false);
			rBlank.setEnabled(false);
			rCheckError.setEnabled(false);
		}


		//actionSelected();
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPersonatorDialog.OptionsTab." + key, args);
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
//		if(!MDPersonatorMeta.isPlugin)
//			return "MDPersonatorDialog.Help.OptionsTab";
//		else
		return BaseMessages.getString(PKG, "MDPersonatorDialog.Plugin.Help.OptionsTab");
	}
}
