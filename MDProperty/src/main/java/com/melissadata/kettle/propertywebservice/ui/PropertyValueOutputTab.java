package com.melissadata.kettle.propertywebservice.ui;

import java.util.HashMap;

import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;
import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceDialog;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.support.MDPropertyWebServiceHelper;

public class PropertyValueOutputTab implements MDTab {

	private static Class<?> PKG = MDPropertyWebServiceMeta.class;
	private Label                      description;
	private MDPropertyWebServiceHelper helper;
	private Composite                  wComp;
	private Group                      currentDeedGroup;
	private Group                      estimatedValueGroup;
	private Group                      salesInfoGroup;
	private Group                      taxGroup;
	//	Current Deed
	private Button                     ckIncludeDeed;
	private Label                      currentDeedMoreLess;
	private Text                       wMortgageAmount;
	private Text                       wMortgageDate;
	private Text                       wMortgageLoanTypeCode;
	private Text                       wMortgageTermCode;
	private Text                       wMortgageTerm;
	private Text                       wMortgageDueDate;
	private Text                       wLenderCode;
	private Text                       wLenderName;
	private Text                       wSecondMortgageAmount;
	private Text                       wSecondMortgageLoanTypeCode;
	// Estimated Value
	private Button                     ckIncludeEstimatedValue;
	private Label                      estimatedValueMoreLess;
	private Text                       wEstimatedValue;
	private Text                       wEstimatedMinValue;
	private Text                       wEstimatedMaxValue;
	private Text                       wConfidenceScore;
	private Text                       wValuationDate;
	// Sale Info
	private Button                     ckIncludeSaleInfo;
	private Label                      saleInfoMoreLess;
	private Text                       wAssessorLastSaleDate;
	private Text                       wAssessorLastSaleAmount;
	private Text                       wAssessorPriorSaleDate;
	private Text                       wAssessorPriorSaleAmount;
	private Text                       wLastOwnershipTransferDate;
	private Text                       wLastOwnerTransferDocNumber;
	private Text                       wLastOwnershipTransferTaxID;
	private Text                       wDeedLastSaleDocBook;
	private Text                       wDeedLastSaleDocumentPage;
	private Text                       wDeedLastDocNumber;
	private Text                       wDeedLastSaleDate;
	private Text                       wDeedLastSalePrice;
	private Text                       wDeedLastSaleTaxID;
	// TAX
	private Button                     ckIncludeTax;
	private Label                      taxMoreLess;
	private Text                       wYearAssessed;
	private Text                       wAssessedValueTotal;
	private Text                       wAssessedValueImprovements;
	private Text                       wAssessedValueLand;
	private Text                       wAssessedImprovementsPerc;
	private Text                       wPreviousAssessedValue;
	private Text                       wMarketValueYear;
	private Text                       wMarketValueTotal;
	private Text                       wMarketValueImprovements;
	private Text                       wMarketValueLand;
	private Text                       wMarketImprovementsPerc;
	private Text                       wTaxFiscalYear;
	private Text                       wTaxRateArea;
	private Text                       wTaxBilledAmount;
	private Text                       wTaxDelinquentYear;
	private Text                       wLastTaxRollUpdate;
	private Text                       wAssrLastUpdated;
	private Text                       wTaxExemptionHomeowner;
	private Text                       wTaxExemptionDisabled;
	private Text                       wTaxExemptionSenior;
	private Text                       wTaxExemptionVeteran;
	private Text                       wTaxExemptionWidow;
	private Text                       wTaxExemptionAdditional;
	private MDPropertyWebServiceDialog dialog;

	public PropertyValueOutputTab(MDPropertyWebServiceDialog dialog) {

		this.dialog = dialog;
		helper = dialog.getHelper();

		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("Title"));
		wTab.setData(this);

		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());

		// Create the composite that will hold the contents of the tab
		wComp = new Composite(wSComp, SWT.NONE);
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
		description.setText("");

		// Create Groups

		createCurrentDeedGroup(wComp, description);
		createEstimatedValueGroup(wComp, currentDeedGroup);
		createSalesInfoGroup(wComp, estimatedValueGroup);
		createTaxGroup(wComp, salesInfoGroup);

		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, 0);
		fd.bottom = new FormAttachment(150, 0);
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

		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;

		// options
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_CURRENT_DEED).metaValue = String.valueOf(ckIncludeDeed.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_TAX).metaValue = String.valueOf(ckIncludeTax.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_ESTIMATED_VALUE).metaValue = String.valueOf(ckIncludeEstimatedValue.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_SALE_INFO).metaValue = String.valueOf(ckIncludeSaleInfo.getSelection());
		// Current Deed
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_AMOUNT).metaValue	= wMortgageAmount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DATE).metaValue	= wMortgageDate.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_LOAN_TYPE_CODE).metaValue	= wMortgageLoanTypeCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM_CODE).metaValue	= wMortgageTermCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM).metaValue	= wMortgageTerm.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DUE_DATE).metaValue	= wMortgageDueDate.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LENDER_CODE).metaValue	= wLenderCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LENDER_NAME).metaValue	= wLenderName.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_AMOUNT).metaValue	= wSecondMortgageAmount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_LOAN_TYPE_CODE).metaValue	= wSecondMortgageLoanTypeCode.getText();

		// Tax
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_ASSESSED).metaValue	= wYearAssessed.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_TOTAL).metaValue	= wAssessedValueTotal.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_IMPROVEMENTS).metaValue	= wAssessedValueImprovements.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_LAND).metaValue	= wAssessedValueLand.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_IMPROVEMENTS_PERC).metaValue	= wAssessedImprovementsPerc.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_ASSESSED_VALUE).metaValue	= wPreviousAssessedValue.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_YEAR).metaValue	= wMarketValueYear.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_TOTAL).metaValue	= wMarketValueTotal.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_IMPROVEMENTS).metaValue	= wMarketValueImprovements.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_LAND).metaValue	= wMarketValueLand.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_IMPROVEMENT_PREC).metaValue	= wMarketImprovementsPerc.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_FISCAL_YEAR).metaValue	= wTaxFiscalYear.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_RATE_AREA).metaValue	= wTaxRateArea.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_BILL_AMOUNT).metaValue	= wTaxBilledAmount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_DELINQUENT_YEAR).metaValue	= wTaxDelinquentYear.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_TAX_ROLL_UPDATE).metaValue	= wLastTaxRollUpdate.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSR_LAST_UPDATED).metaValue	= wAssrLastUpdated.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_HOMEOWNER).metaValue	= wTaxExemptionHomeowner.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_DISABLED).metaValue	= wTaxExemptionDisabled.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_SENIOR).metaValue	= wTaxExemptionSenior.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_VETERAN).metaValue	= wTaxExemptionVeteran.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_WIDOW).metaValue	= wTaxExemptionWidow.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_ADDITIONAL).metaValue	= wTaxExemptionAdditional.getText();

		// Estimated Value
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_VALUE).metaValue	= wEstimatedValue.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MIN_VALUE).metaValue	= wEstimatedMinValue.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MAX_VALUE).metaValue	= wEstimatedMaxValue.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONFIDENCE_SCORE).metaValue	= wConfidenceScore.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_VALUATION_DATE).metaValue	= wValuationDate.getText();

		// Sales info
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_DATE).metaValue	= wAssessorLastSaleDate.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_AMMOUNT).metaValue	= wAssessorLastSaleAmount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_PRIOR_SALE_DATE).metaValue	= wAssessorPriorSaleDate.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_PRIOR_SALE_AMOUNT).metaValue	= wAssessorPriorSaleAmount.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DATE).metaValue	= wLastOwnershipTransferDate.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DOCUMENT_NUMBER).metaValue	= wLastOwnerTransferDocNumber.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_TAX_ID).metaValue	= wLastOwnershipTransferTaxID.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_BOOK).metaValue	= wDeedLastSaleDocBook.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_PAGE).metaValue	= wDeedLastSaleDocumentPage.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_NUMBER).metaValue	= wDeedLastDocNumber.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DATE).metaValue	= wDeedLastSaleDate.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_PRICE).metaValue	= wDeedLastSalePrice.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_TAX_ID).metaValue	= wDeedLastSaleTaxID.getText();

	}

	@Override
	public String getHelpURLKey() {

		return BaseMessages.getString(PKG, "MDPropertyWebService.Plugin.Help.OutputFieldsTab.PropertyValueOutput");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;
		// options
		ckIncludeDeed.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_CURRENT_DEED).metaValue));
		ckIncludeTax.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_TAX).metaValue));
		ckIncludeEstimatedValue.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_ESTIMATED_VALUE).metaValue));
		ckIncludeSaleInfo.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_SALE_INFO).metaValue));
		// Current Deed
		wMortgageAmount .setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_AMOUNT).metaValue);
		wMortgageDate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DATE).metaValue);
		wMortgageLoanTypeCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_LOAN_TYPE_CODE).metaValue);
		wMortgageTermCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM_CODE).metaValue);
		wMortgageTerm.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_TERM).metaValue);
		wMortgageDueDate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MORTGAGE_DUE_DATE).metaValue);
		wLenderCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LENDER_CODE).metaValue);
		wLenderName.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LENDER_NAME).metaValue);
		wSecondMortgageAmount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_AMOUNT).metaValue);
		wSecondMortgageLoanTypeCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECOND_MORTGAGE_LOAN_TYPE_CODE).metaValue);
		// Tax
		wYearAssessed.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_ASSESSED).metaValue);
		wAssessedValueTotal.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_TOTAL).metaValue);
		wAssessedValueImprovements.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_IMPROVEMENTS).metaValue);
		wAssessedValueLand.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_VALUE_LAND).metaValue);
		wAssessedImprovementsPerc.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSED_IMPROVEMENTS_PERC).metaValue);
		wPreviousAssessedValue.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_ASSESSED_VALUE).metaValue);
		wMarketValueYear.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_YEAR).metaValue);
		wMarketValueTotal.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_TOTAL).metaValue);
		wMarketValueImprovements.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_IMPROVEMENTS).metaValue);
		wMarketValueLand.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_VALUE_LAND).metaValue);
		wMarketImprovementsPerc.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MARKET_IMPROVEMENT_PREC).metaValue);
		wTaxFiscalYear.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_FISCAL_YEAR).metaValue);
		wTaxRateArea.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_RATE_AREA).metaValue);
		wTaxBilledAmount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_BILL_AMOUNT).metaValue);
		wTaxDelinquentYear.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_DELINQUENT_YEAR).metaValue);
		wLastTaxRollUpdate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_TAX_ROLL_UPDATE).metaValue);
		wAssrLastUpdated.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSR_LAST_UPDATED).metaValue);
		wTaxExemptionHomeowner.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_HOMEOWNER).metaValue);
		wTaxExemptionDisabled.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_DISABLED).metaValue);
		wTaxExemptionSenior.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_SENIOR).metaValue);
		wTaxExemptionVeteran.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_VETERAN).metaValue);
		wTaxExemptionWidow.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_WIDOW).metaValue);
		wTaxExemptionAdditional.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TAX_EXEMPTION_ADDITIONAL).metaValue);
		// Estimated value
		wEstimatedValue.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_VALUE).metaValue);
		wEstimatedMinValue.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MIN_VALUE).metaValue);
		wEstimatedMaxValue.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_MAX_VALUE).metaValue);
		wConfidenceScore.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_CONFIDENCE_SCORE).metaValue);
		wValuationDate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_VALUATION_DATE).metaValue);
		// Sales Info
		wAssessorLastSaleDate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_DATE).metaValue);
		wAssessorLastSaleAmount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_AMMOUNT).metaValue);
		wAssessorPriorSaleDate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_PRIOR_SALE_DATE).metaValue);
		wAssessorPriorSaleAmount.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ASSESSORS_LAST_SALE_AMMOUNT).metaValue);
		wLastOwnershipTransferDate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DATE).metaValue);
		wLastOwnerTransferDocNumber.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DOCUMENT_NUMBER).metaValue);
		wLastOwnershipTransferTaxID.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_TAX_ID).metaValue);
		wDeedLastSaleDocBook.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_BOOK).metaValue);
		wDeedLastSaleDocumentPage.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_PAGE).metaValue);
		wDeedLastDocNumber.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_NUMBER).metaValue);
		wDeedLastSaleDate.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_DATE).metaValue);
	    wDeedLastSalePrice.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_PRICE).metaValue);
		wDeedLastSaleTaxID.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEED_LAST_SALE_TAX_ID).metaValue);




		enable();
		return true;
	}

	private void createCurrentDeedGroup(Composite parent, Control last) {

		currentDeedGroup = new Group(parent, SWT.NONE);
		currentDeedGroup.setText(getString("CurrentDeedGroup.Label"));
		helper.setLook(currentDeedGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		currentDeedGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		currentDeedGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeDeed = helper.addCheckBox(currentDeedGroup, last, "PropertyValueTab.IncludeCurrentDeed");
		ckIncludeDeed.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		currentDeedMoreLess = helper.addLabel(currentDeedGroup, last, "");

		Listener addrExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = currentDeedMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(1);
					currentDeedMoreLess.setText(getString("Less"));
				} else {
					collapse(1);
					currentDeedMoreLess.setText(getString("More"));
				}
			}
		};

		currentDeedMoreLess.addListener(SWT.MouseDown, addrExpandCollapse);

		currentDeedMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		currentDeedMoreLess.setText(getString("Less"));

		last = currentDeedMoreLess;

		last = top = helper.addSpacer(currentDeedGroup, last);

		last = wMortgageAmount = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.MortgageAmount");
		last = wMortgageDate = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.MortgageDate");
		last = wMortgageLoanTypeCode = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.MortgageLoanTypeCode");
		last = wMortgageTermCode = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.MortgageTermCode");
		last = wMortgageTerm = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.MortgageTerm");

		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wMortgageDueDate = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.MortgageDueDate");
		last = wLenderCode = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.LenderCode");
		last = wLenderName = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.LenderName");
		last = wSecondMortgageAmount = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.SecondMortgageAmount");
		last = wSecondMortgageLoanTypeCode = helper.addTextBox(currentDeedGroup, last, "PropertyValueTab.SecondMortgageLoanTypeCode");
	}

	private void createEstimatedValueGroup(Composite parent, Control last) {

		estimatedValueGroup = new Group(parent, SWT.NONE);
		estimatedValueGroup.setText(getString("EstimatedValueGroup.Label"));
		helper.setLook(estimatedValueGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		estimatedValueGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		estimatedValueGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeEstimatedValue = helper.addCheckBox(estimatedValueGroup, last, "PropertyValueTab.IncludeEstimatedValue");
		ckIncludeEstimatedValue.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		estimatedValueMoreLess = helper.addLabel(estimatedValueGroup, last, "");

		Listener addrExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = estimatedValueMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(2);
					estimatedValueMoreLess.setText(getString("Less"));
				} else {
					collapse(2);
					estimatedValueMoreLess.setText(getString("More"));
				}
			}
		};

		estimatedValueMoreLess.addListener(SWT.MouseDown, addrExpandCollapse);

		estimatedValueMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		estimatedValueMoreLess.setText(getString("Less"));

		last = estimatedValueMoreLess;
		last = top = helper.addSpacer(estimatedValueGroup, last);

		last = wEstimatedValue = helper.addTextBox(estimatedValueGroup, last, "PropertyValueTab.EstimatedValue");
		last = wEstimatedMinValue = helper.addTextBox(estimatedValueGroup, last, "PropertyValueTab.EstimatedMinValue");
		last = wEstimatedMaxValue = helper.addTextBox(estimatedValueGroup, last, "PropertyValueTab.EstimatedMaxValue");

		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;

		last = wConfidenceScore = helper.addTextBox(estimatedValueGroup, last, "PropertyValueTab.ConfidenceScore");
		last = wValuationDate = helper.addTextBox(estimatedValueGroup, last, "PropertyValueTab.ValuationDate");
	}

	private void createSalesInfoGroup(Composite parent, Control last) {

		salesInfoGroup = new Group(parent, SWT.NONE);
		salesInfoGroup.setText(getString("SalesInfoGroup.Label"));
		helper.setLook(salesInfoGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		salesInfoGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		salesInfoGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeSaleInfo = helper.addCheckBox(salesInfoGroup, last, "PropertyValueTab.IncludeSalesInfo");
		ckIncludeSaleInfo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		saleInfoMoreLess = helper.addLabel(salesInfoGroup, last, "");

		Listener addrExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = saleInfoMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(3);
					saleInfoMoreLess.setText(getString("Less"));
				} else {
					collapse(3);
					saleInfoMoreLess.setText(getString("More"));
				}
			}
		};

		saleInfoMoreLess.addListener(SWT.MouseDown, addrExpandCollapse);

		saleInfoMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		saleInfoMoreLess.setText(getString("Less"));

		last = saleInfoMoreLess;
		last = top = helper.addSpacer(salesInfoGroup, last);
		last = wAssessorLastSaleDate = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.AssessorsLastSaleDate");
		last = wAssessorLastSaleAmount = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.AssessorsLastSaleAmount");
		last = wAssessorPriorSaleDate = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.AssessorsPriorSaleDate");
		last = wAssessorPriorSaleAmount = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.AssessorsPriorSaleAmount");
		last = wLastOwnershipTransferDate = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.LastOwnershipTransferDate");
		last = wLastOwnerTransferDocNumber = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.LastOwnershipTransferDocNumber");
		last = wLastOwnershipTransferTaxID = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.LastOwnershipTransferTaxID");
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wDeedLastSaleDocBook = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.DeedLastSaleDocBook");
		last = wDeedLastSaleDocumentPage = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.DeedLastSaleDocumentPage");
		last = wDeedLastDocNumber = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.DeedLastSaleDocNumber");
		last = wDeedLastSaleDate = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.DeedLastSaleDate");
		last = wDeedLastSalePrice = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.DeedLastSalePrice");
		last = wDeedLastSaleTaxID = helper.addTextBox(salesInfoGroup, last, "PropertyValueTab.DeedLastSaleTaxID");
	}

	private void createTaxGroup(Composite parent, Control last) {
		taxGroup = new Group(parent, SWT.NONE);
		taxGroup.setText(getString("TaxGroup.Label"));
		helper.setLook(taxGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		taxGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		taxGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;

		last = ckIncludeTax = helper.addCheckBox(taxGroup, last, "PropertyValueTab.IncludeTax");
		ckIncludeTax.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		taxMoreLess = helper.addLabel(taxGroup, last, "");

		Listener addrExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = taxMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(4);
					taxMoreLess.setText(getString("Less"));
				} else {
					collapse(4);
					taxMoreLess.setText(getString("More"));
				}
			}
		};

		taxMoreLess.addListener(SWT.MouseDown, addrExpandCollapse);

		taxMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		taxMoreLess.setText(getString("Less"));

		last = taxMoreLess;
		last = top = helper.addSpacer(taxGroup, last);

		last = wYearAssessed = helper.addTextBox(taxGroup, last, "PropertyValueTab.YearAssessed");
		last = wAssessedValueTotal = helper.addTextBox(taxGroup, last, "PropertyValueTab.AssessedValueTotal");
		last = wAssessedValueImprovements = helper.addTextBox(taxGroup, last, "PropertyValueTab.AssessedValueImprovements");
		last = wAssessedValueLand = helper.addTextBox(taxGroup, last, "PropertyValueTab.AssessedValueLand");
		last = wAssessedImprovementsPerc = helper.addTextBox(taxGroup, last, "PropertyValueTab.AssessedImprovementsPerc");
		last = wPreviousAssessedValue = helper.addTextBox(taxGroup, last, "PropertyValueTab.PreviousAssessedValue");
		last = wMarketValueYear = helper.addTextBox(taxGroup, last, "PropertyValueTab.MarketValueYear");
		last = wMarketValueTotal = helper.addTextBox(taxGroup, last, "PropertyValueTab.MarketValueTotal");
		last = wMarketValueImprovements = helper.addTextBox(taxGroup, last, "PropertyValueTab.MarketValueImprovements");
		last = wMarketValueLand = helper.addTextBox(taxGroup, last, "PropertyValueTab.MarketValueLand");
		last = wMarketImprovementsPerc = helper.addTextBox(taxGroup, last, "PropertyValueTab.MarketImprovementsPerc");
		last = wTaxFiscalYear = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxFiscalYear");
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wTaxRateArea = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxRateArea");
		last = wTaxBilledAmount = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxBilledAmount");
		last = wTaxDelinquentYear = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxDelinquentYear");
		last = wLastTaxRollUpdate = helper.addTextBox(taxGroup, last, "PropertyValueTab.LastTaxRollUpdate");
		last = wAssrLastUpdated = helper.addTextBox(taxGroup, last, "PropertyValueTab.AssrLastUpdated");
		last = wTaxExemptionHomeowner = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxExemptionHomeowner");
		last = wTaxExemptionDisabled = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxExemptionDisabled");
		last = wTaxExemptionSenior = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxExemptionSenior");
		last = wTaxExemptionVeteran = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxExemptionVeteran");
		last = wTaxExemptionWidow = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxExemptionWidow");
		last = wTaxExemptionAdditional = helper.addTextBox(taxGroup, last, "PropertyValueTab.TaxExemptionAdditional");



	}

	private void expand(int group) {

		FormData currentDeedFormData = (FormData) currentDeedGroup.getLayoutData();
		FormData estimatedFormData  = (FormData) estimatedValueGroup.getLayoutData();
		FormData salesFormData  = (FormData) salesInfoGroup.getLayoutData();
		FormData taxFormData = (FormData) taxGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Current Deed expand
			currentDeedFormData.top = new FormAttachment(description, helper.margin);
			currentDeedFormData.bottom = null;
			wComp.layout();
			offset = (currentDeedGroup.getBounds().height + (helper.margin * 2));
			for (Control child : currentDeedGroup.getChildren()) {
				child.setVisible(true);
			}

			estimatedFormData.top = new FormAttachment(description, offset);
			if (estimatedValueMoreLess.getText().equals("less")) {
				estimatedFormData.bottom = null;
			} else {
				offset = (currentDeedGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				estimatedFormData.bottom = new FormAttachment(description, offset);
			}
		}

		if (group == 2) {
			// Estimated Values expand
			estimatedFormData.top = new FormAttachment(currentDeedGroup, helper.margin);
			estimatedFormData.bottom = null;
			for (Control child : estimatedValueGroup.getChildren()) {
				child.setVisible(true);
			}
			wComp.layout();
			offset = (estimatedValueGroup.getBounds().height + (helper.margin * 2));
			salesFormData.top = new FormAttachment(estimatedValueGroup, (helper.margin * 2));
			if (saleInfoMoreLess.getText().equals("less")) {
				salesFormData.bottom = null;
			} else {
				offset = (estimatedValueGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				salesFormData.bottom = new FormAttachment(estimatedValueGroup, offset);
			}
		}

		if (group == 3) {
			//Sale expand
			salesFormData.top = new FormAttachment(estimatedValueGroup, helper.margin * 2);
			salesFormData.bottom = null;
			for (Control child : salesInfoGroup.getChildren()) {
				child.setVisible(true);
			}

			wComp.layout();
			offset = (salesInfoGroup.getBounds().height + (helper.margin * 2));
			taxFormData.top = new FormAttachment(salesInfoGroup, (helper.margin * 2));
			if (taxMoreLess.getText().equals("less")) {
				taxFormData.bottom = null;
			} else {
				offset = (salesInfoGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				taxFormData.bottom = new FormAttachment(salesInfoGroup, offset);
			}


		}

		if (group == 4) {
			//tax expand
			taxFormData.top = new FormAttachment(salesInfoGroup, helper.margin * 2);
			taxFormData.bottom = null;
			for (Control child : taxGroup.getChildren()) {
				child.setVisible(true);
			}
		}

		enable();
	}

	private void collapse(int group) {

		FormData currentDeedFormData = (FormData) currentDeedGroup.getLayoutData();
		FormData estimatedFormData  = (FormData) estimatedValueGroup.getLayoutData();
		FormData salesFormData  = (FormData) salesInfoGroup.getLayoutData();
		FormData taxFormData = (FormData) taxGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Address collapse
			currentDeedFormData.top = new FormAttachment(description, helper.margin);
			currentDeedFormData.bottom = new FormAttachment(description, collapsedHeight);

			for (Control child : currentDeedGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			estimatedFormData.top = new FormAttachment(currentDeedGroup, helper.margin);
			if (estimatedValueMoreLess.getText().equals("less")) {
				estimatedFormData.bottom = null;
			} else {
				offset = (currentDeedGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				estimatedFormData.bottom = new FormAttachment(currentDeedGroup, offset);
			}
		}

		if (group == 2) {

			//parsed collapse
			estimatedFormData.top = new FormAttachment(description, currentDeedGroup.getBounds().height + (helper.margin * 2));
			estimatedFormData.bottom = new FormAttachment(description, currentDeedGroup.getBounds().height + (helper.margin * 2) + 80);
			for (Control child : estimatedValueGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			salesFormData.top = new FormAttachment(estimatedValueGroup, helper.margin);
			if (saleInfoMoreLess.getText().equals("less")) {
				salesFormData.bottom = null;
			} else {
				salesFormData.bottom = new FormAttachment(estimatedValueGroup, estimatedValueGroup.getBounds().height + 80);
			}
		}

		//parcel collapse
		if (group == 3) {
			for (Control child : salesInfoGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			salesFormData.top = new FormAttachment(estimatedValueGroup, helper.margin * 2);
			salesFormData.bottom = new FormAttachment(estimatedValueGroup, estimatedValueGroup.getBounds().height + 80);

			wComp.layout();
			taxFormData.top = new FormAttachment(salesInfoGroup, helper.margin);
			if (taxMoreLess.getText().equals("less")) {
				taxFormData.bottom = null;
			} else {
				taxFormData.bottom = new FormAttachment(salesInfoGroup, salesInfoGroup.getBounds().height + 80);
			}
		}

		//legal collapse
		if (group == 4) {
			for (Control child : taxGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			taxFormData.top = new FormAttachment(salesInfoGroup, helper.margin * 2);
			taxFormData.bottom = new FormAttachment(salesInfoGroup, salesInfoGroup.getBounds().height + 80);
		}

		enable();
	}

	private void enable() {

		// Current Deed
		if (ckIncludeDeed.getSelection()) {
			for (Control child : currentDeedGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : currentDeedGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		// Estimated Value
		if (ckIncludeEstimatedValue.getSelection()) {
			for (Control child : estimatedValueGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : estimatedValueGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//Sale
		if (ckIncludeSaleInfo.getSelection()) {
			for (Control child : salesInfoGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : salesInfoGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//Tax
		if (ckIncludeTax.getSelection()) {
			for (Control child : taxGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : taxGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		ckIncludeDeed.setEnabled(true);
		ckIncludeDeed.setVisible(true);
		ckIncludeEstimatedValue.setEnabled(true);
		ckIncludeEstimatedValue.setVisible(true);
		ckIncludeSaleInfo.setEnabled(true);
		ckIncludeSaleInfo.setVisible(true);
		ckIncludeTax.setVisible(true);
		ckIncludeTax.setEnabled(true);

		currentDeedMoreLess.setVisible(true);
		currentDeedMoreLess.setEnabled(true);
		estimatedValueMoreLess.setVisible(true);
		estimatedValueMoreLess.setEnabled(true);
		saleInfoMoreLess.setVisible(true);
		saleInfoMoreLess.setEnabled(true);
		taxMoreLess.setVisible(true);
		taxMoreLess.setEnabled(true);

		wComp.layout();
	}

	@Override
	public void dispose() {

	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.PropertyValueTab." + key, args);
	}
}
