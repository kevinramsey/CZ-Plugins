package com.melissadata.kettle.businesscoder.ui;

/**
 * Created by Kevin on 3/16/2017.
 */

import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.support.MDBusinessCoderHelper;
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

public class OutputContactTab implements MDTab {

	private static Class<?>			PKG	= MDBusinessCoderMeta.class;
	private Label                 description;
	private MDBusinessCoderHelper helper;



	private Group                 contactsGroup;
	private Group                 orgGroup;

	private Text[]                wFirstName;
	private Text[]                wLastName;
	private Text[]                wGender;
	private Text[]                wTitle;
	private Text[]                wEmail;

	private Text                  wCompanyName;
	private Text                  wCompanyPhone;
	private Text                  wCompanyWeb;




	public OutputContactTab(MDBusinessCoderDialog dialog) {

		helper = dialog.getHelper();

		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("Title"));
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
		description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		description.setText("Map");

		// Create Groups
		createContactGroup(wComp, description);
		createOrgGroup(wComp, contactsGroup);

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
		description.setText(getString("Description"));// This is done here due to sizing of dialog
	}

	@Override
	public void advancedConfigChanged() {

		// DO nothing

	}

	@Override
	public void getData(BaseStepMeta meta) {

		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.businessCoderFields.outputFields;

		outputFields.get(BusinessCoderFields.TAG_OUTPUT_COMPANY_NAME).metaValue = wCompanyName.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PHONE).metaValue = wCompanyPhone.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_WEB_ADDRESS).metaValue = wCompanyWeb.getText();

		outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_1).metaValue = wFirstName[0].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_1).metaValue = wLastName[0].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_1).metaValue = wGender[0].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_1).metaValue = wTitle[0].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_1).metaValue = wEmail[0].getText();

		outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_2).metaValue = wFirstName[1].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_2).metaValue = wLastName[1].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_2).metaValue = wGender[1].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_2).metaValue = wTitle[1].getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_2).metaValue = wEmail[1].getText();


	}


	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDBusinessCoder.Plugin.Help.OutputContactTab");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;
		HashMap<String, MetaVal> outputFields = bcMeta.businessCoderFields.outputFields;

		wCompanyName.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_COMPANY_NAME).metaValue);
		wCompanyPhone.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PHONE).metaValue);
		wCompanyWeb.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_WEB_ADDRESS).metaValue);

		wFirstName[0].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_1).metaValue);
		wLastName[0].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_1).metaValue);
		wGender[0].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_1).metaValue);
		wTitle[0].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_1).metaValue);
		wEmail[0].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_1).metaValue);

		wFirstName[1].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_FIRST_NAME_2).metaValue);
		wLastName[1].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LAST_NAME_2).metaValue);
		wGender[1].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_GENDER_2).metaValue);
		wTitle[1].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_TITLE_2).metaValue);
		wEmail[1].setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMAIL_2).metaValue);


		enable();
		return true;
	}

	private void createContactGroup(Composite parent, Control last) {

		contactsGroup = new Group(parent, SWT.NONE);
		contactsGroup.setText(getString("OutputContactsGroup.Label"));
		helper.setLook(contactsGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		contactsGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(nicsGroup, 0);
		contactsGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[1] =  (helper.colWidth[0] / 2) + 45;
	//	helper.colWidth[0] = 20;
		last = null;
		wFirstName = helper.addTextBoxes(contactsGroup,null,"OutputContactTab.FirstName");
		wLastName = helper.addTextBoxes(contactsGroup,wFirstName[0],"OutputContactTab.LastName");
		wGender = helper.addTextBoxes(contactsGroup,wLastName[0],"OutputContactTab.Gender");
		wTitle = helper.addTextBoxes(contactsGroup,wGender[0],"OutputContactTab.Title");
		wEmail = helper.addTextBoxes(contactsGroup,wTitle[0],"OutputContactTab.Email");


	}


	private void createOrgGroup(Composite parent, Control last) {

		orgGroup = new Group(parent, SWT.NONE);
		orgGroup.setText(getString("OutputOrgGroup.Label"));
		helper.setLook(orgGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		orgGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(nicsGroup, 0);
		orgGroup.setLayoutData(fd);

		// set the column widths for proper placing
	//	helper.colWidth[0] = 30;
		last = null;
		last = wCompanyName = helper.addTextBox(orgGroup, last, "OutputContactTab.CompanyName");
		last = wCompanyPhone = helper.addTextBox(orgGroup, last, "OutputContactTab.CompanyPhone");
		last = wCompanyWeb = helper.addTextBox(orgGroup, last, "OutputContactTab.CompanyWeb");

	}



	private void enable() {


	}

	public void dispose(){
		// nothing to dispose
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDBusinessCoderDialog.OutputContactTab." + key, args);
	}

}
