package com.melissadata.kettle.businesscoder.ui;

import java.util.HashMap;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;

import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.support.MDBusinessCoderHelper;

public class OptionsTab implements MDTab {

	private static Class<?>			PKG	= MDBusinessCoderMeta.class;
	private MDBusinessCoderHelper	helper;
	private Label					description;
	
	private Group					optionsGroup;
	private Button					ckDominantBusiness;
	
	
	public OptionsTab(MDBusinessCoderDialog dialog) {
		
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
		fd.top = new FormAttachment(0, helper.margin*5);
		description.setLayoutData(fd);
		description.setText("Map");

		// Create Groups
		createOptionsGroup(wComp);

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
	
	private void createOptionsGroup(Composite parent) {

		optionsGroup = new Group(parent, SWT.NONE);
		optionsGroup.setText(getString("OptionsGroup.Label"));
		helper.setLook(optionsGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		optionsGroup.setLayout(fl);
		// set the column widths for proper placing
		helper.colWidth[0] = 30;
		
		Label spacer = helper.addSpacer(optionsGroup, null);
			
		ckDominantBusiness = helper.addCheckBox(optionsGroup, spacer, "OptionsTab.DominantBusiness");

		// Set Group Location
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(description, helper.margin * 5);
		fd.right = new FormAttachment(100, 0);
		// fd.bottom = new FormAttachment(100, 0);
		optionsGroup.setLayoutData(fd);
	}
	
	
	@Override
	public void advancedConfigChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getData(BaseStepMeta meta) {
		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;
		
		bcMeta.businessCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_DOMINANT_BUSINESS).metaValue = ckDominantBusiness.getSelection() ? "yes":"no";

	}

	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDBusinessCoder.Plugin.Help.OptionsTab");
	}

	@Override
	public boolean init(BaseStepMeta meta) {
		
		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;

		HashMap<String, MetaVal> optionFields = bcMeta.businessCoderFields.optionFields;
	
		ckDominantBusiness.setSelection(optionFields.get(BusinessCoderFields.TAG_OPTION_DOMINANT_BUSINESS).metaValue.equals("yes"));
		
		return true;
	}

	public void dispose(){
		// nothing to dispose
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDBusinessCoderDialog.OptionsTab." + key, args);
	}

}
