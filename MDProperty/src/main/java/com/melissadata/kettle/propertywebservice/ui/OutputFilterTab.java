package com.melissadata.kettle.propertywebservice.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.util.ImageUtil;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceDialog;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.support.MDPropertyWebServiceHelper;
import com.melissadata.kettle.propertywebservice.support.MDControlSpaceKeyAdapter;
import com.melissadata.kettle.propertywebservice.support.OutputFilterFields;
import com.melissadata.kettle.propertywebservice.support.PreBuiltRules;
import com.melissadata.kettle.propertywebservice.support.PreBuiltRules.Rule;
import com.melissadata.kettle.propertywebservice.support.Validations;
import com.melissadata.cz.support.FilterTarget;
import com.melissadata.cz.support.MDBinaryEvaluator;
import com.melissadata.cz.support.MDBinaryEvaluator.EvaluatorException;
import com.melissadata.cz.support.MDStreamHandler;
import com.melissadata.cz.support.MDTab;

public class OutputFilterTab implements MDTab {

	public class FilterRow {

		public Text     wFilterName;
		public CCombo   cbPreBuiltRules;
		public Text     wFilterRule;
		public CCombo   cbTargetName;
		public MenuItem miRemove;
		public MenuItem miSave;
	}

	private static Class<?> PKG = MDPropertyWebServiceMeta.class;
	private MDPropertyWebServiceDialog dialog;
	private MDPropertyWebServiceHelper helper;
	private Text                       wResultCodes;
	private MDPropertyWebServiceMeta   bcMeta;
	private FilterRow filterRows[] = new FilterRow[MDStreamHandler.MAX_TARGETS];
	private PreBuiltRules preBuiltRules;
	private Button        bValidate;

	public OutputFilterTab(MDPropertyWebServiceDialog dialog) {

		this.dialog = dialog;
		helper = dialog.getHelper();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);
		// Create a scrolling region within the tab 
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the tab contents
		Composite wComp = createControls(wSComp);
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
		// Initialzie the tab with the scrolled composite
		wTab.setControl(wSComp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDTab#advancedConfigChanged()
	 */
	@Override
	public void advancedConfigChanged() {

		// Update enablement
		enable();
	}

	/**
	 * Loads the dialog data into the bcMeta structure
	 */
	@Override
	public void getData(BaseStepMeta meta) {

		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;
		// Get the output filter bcMeta data
		OutputFilterFields outputFilter = bcMeta.oFilterFields;
		if (outputFilter.filterFields == null) {
			outputFilter.init();
		}
		// Set fields
		if (wResultCodes != null) {
			bcMeta.propertyWebServiceFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RESULTS).metaValue = wResultCodes.getText();
		}
		//outputFilter.filterFields.get(OutputFilterFields.TAG_RESULT_CODES).metaValue = wResultCodes.getText();
		// Build a new list of targets
		List<FilterTarget> targets = getFilterData();
		outputFilter.filterTargets = targets;
		// Filter targets could have changed
		meta.resetStepIoMeta();
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {

		return BaseMessages.getString(PKG, "MDPropertyWebService.Plugin.Help.OutputFilterTab");
	}

	/**
	 * Loads the bcMeta data into the dialog tab
	 *
	 * @param meta
	 * @return
	 */
	@Override
	public boolean init(BaseStepMeta meta) {

		boolean changed = false;
		// Save the data
		bcMeta = (MDPropertyWebServiceMeta) meta;
		// Get the output filter bcMeta data
		OutputFilterFields outputFilter = bcMeta.oFilterFields;
		if (outputFilter.filterTargets == null) {
			outputFilter.filterTargets = new ArrayList<FilterTarget>();
		}
		// Set result code field
		if ((wResultCodes != null) && (outputFilter.filterFields != null)) {
			wResultCodes.setText(bcMeta.propertyWebServiceFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RESULTS).metaValue);
		}
		// Set up filtering controls
		try {
			initFilteringControls(outputFilter.filterTargets);
		} catch (KettleException e) {
			// Can't recover from this. Wrap it in a runtime expression
			throw new RuntimeException("Problem initializing filter controls", e);
		}
		// Handle enablement
		enable();
		return changed;
	}

	/**
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param col4
	 */
	private void alignColumns(Control col1, Control col2, Control col3, Control col4) {

		((FormData) col1.getLayoutData()).left = new FormAttachment(0, 0);
		((FormData) col1.getLayoutData()).right = new FormAttachment(col2, -helper.margin);
		((FormData) col2.getLayoutData()).left = new FormAttachment(10, 0);
		((FormData) col2.getLayoutData()).right = new FormAttachment(col3, -helper.margin);
		((FormData) col3.getLayoutData()).left = new FormAttachment(40, 0);
		((FormData) col3.getLayoutData()).right = new FormAttachment(col4, -helper.margin - 20); // extra needed for control
// decoration
		((FormData) col4.getLayoutData()).left = new FormAttachment(70, 0);
		((FormData) col4.getLayoutData()).right = new FormAttachment(100, -helper.margin);
	}

	/**
	 * Called to create the tab controls
	 *
	 * @param parent
	 * @return
	 */
	private Composite createControls(Composite parent) {

		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(parent, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Result Codes group
		Control wLastLine = createResultCodesGroup(wComp, null);
		// Output Filter group
		wLastLine = createOutputFilterGroup(wComp, wLastLine);
		return wComp;
	}

	/**
	 * Called to create the controls for the filtering steps
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	private Composite createFilteringControls(Composite parent, Label wLastLine) {

		// Create a composite to hold the grid of filter rules
		Composite wComp = new Composite(parent, SWT.NONE);
		helper.setLook(wComp);
		wComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(wLastLine, 0);
		fd.right = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		// Create column for filter numbers
		Composite wNumberComp = new Composite(wComp, SWT.NONE);
		helper.setLook(wNumberComp);
		wNumberComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.width = 20;
		wNumberComp.setLayoutData(fd);
		// Fill it in
		Control wLastRow = helper.addSpacer(wNumberComp, null);
		for (int i = 0; i < filterRows.length; i++) {
			Label label = helper.addLabel(wNumberComp, wLastRow, null);
			label.setText("" + (i + 1) + ".");
			// This invisible control is created in order to align the number rows with the filter rows
			Control filler = helper.addComboBox(wNumberComp, wLastRow, null)[1];
			filler.setVisible(false);
			wLastRow = filler;
		}
		// Create column for filter rows
		Composite wRowComp = new Composite(wComp, SWT.NONE);
		helper.setLook(wRowComp);
		wRowComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(wNumberComp, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		wRowComp.setLayoutData(fd);
		// Create header row
		Label lblName     = helper.addLabel(wRowComp, null, "OutputFilterTab.FilterName");
		Label lblPreBuilt = helper.addLabel(wRowComp, null, "OutputFilterTab.PreBuiltFilter");
		Label lblCustom   = helper.addLabel(wRowComp, null, "OutputFilterTab.CustomFilterExpression");
		Label lblTarget   = helper.addLabel(wRowComp, null, "OutputFilterTab.TargetName");
		// Line them up
		alignColumns(lblName, lblPreBuilt, lblCustom, lblTarget);
		// Listener for removing a rule
		SelectionListener lsRemove = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				removeRule((MenuItem) e.getSource());
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				removeRule((MenuItem) e.getSource());
			}
		};
		// Listener for saving a rule
		SelectionListener lsSave = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				saveRule((MenuItem) e.getSource());
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				saveRule((MenuItem) e.getSource());
			}
		};
		wLastRow = lblName;
		for (int i = 0; i < filterRows.length; i++) {
			final FilterRow row = filterRows[i] = new FilterRow();
			// Create the controls
			// Create filter name field
			row.wFilterName = helper.addTextBox(wRowComp, wLastRow, null);
			// Create the pre-built rule selector
			row.cbPreBuiltRules = (CCombo) helper.addComboBox(wRowComp, wLastRow, null)[1];
			row.cbPreBuiltRules.setEditable(false);
			// Add context menu to remove filter
			Menu menuRemove = new Menu(row.cbPreBuiltRules);
			row.miRemove = new MenuItem(menuRemove, SWT.NONE);
			row.miRemove.setText(getString("RemoveRule.Label"));
			row.miRemove.setData(row);
			row.miRemove.addSelectionListener(lsRemove);
			row.cbPreBuiltRules.setMenu(menuRemove);
			// Create the custom rule text box
			row.wFilterRule = helper.addTextBox(wRowComp, wLastRow, null);
			// Add context menu to save filter
			Menu menuSave = new Menu(row.wFilterRule);
			row.miSave = new MenuItem(menuSave, SWT.NONE);
			row.miSave.setText(getString("SaveRule.Label"));
			row.miSave.setData(row);
			row.miSave.addSelectionListener(lsSave);
			row.wFilterRule.setMenu(menuSave);
			// Add a decoration to indicate special functionality
			ControlDecoration controlDecoration = new ControlDecoration(row.wFilterRule, SWT.TOP | SWT.RIGHT);
			controlDecoration.setImage(ImageUtil.getImage(dialog.getShell().getDisplay(), this.getClass(), "com/melissadata/kettle/propertywebservice/images/question-mark-small.png"));
			controlDecoration.setDescriptionText(getString("EditRuleHint"));
			// Create the target selector
			row.cbTargetName = (CCombo) helper.addComboBox(wRowComp, wLastRow, null)[1];
			// Line them up
			alignColumns(row.wFilterName, row.cbPreBuiltRules, row.wFilterRule, row.cbTargetName);
			// Editing the filter name can alter enablement of subsequent fields
			row.wFilterName.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {

					enable();
				}
			});
			// If the filter name is blanked out then scroll subsequent rows up
			final int index = i;
			row.wFilterName.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {

					if (Const.isEmpty(row.wFilterName.getText())) {
						// Scroll rows up
						for (int j = index + 1; j < filterRows.length; j++) {
							filterRows[j - 1].wFilterName.setText(filterRows[j].wFilterName.getText());
							filterRows[j - 1].cbPreBuiltRules.select(filterRows[j].cbPreBuiltRules.getSelectionIndex());
							filterRows[j - 1].wFilterRule.setText(filterRows[j].wFilterRule.getText());
							filterRows[j - 1].cbTargetName.select(filterRows[j].cbTargetName.getSelectionIndex());
						}
						// Blank out last row
						int j = filterRows.length - 1;
						filterRows[j].wFilterName.setText("");
						filterRows[j].cbPreBuiltRules.select(0);
						filterRows[j].wFilterRule.setText("");
						filterRows[j].cbTargetName.setText("");
					}
					// Adjust enablement
					enable();
				}
			});
			// Add ctrl-space detector to edit rule
			row.wFilterRule.addKeyListener(new MDControlSpaceKeyAdapter(row.wFilterRule));
			// Add listener to pre-built combo box that will set the custom rule text field
			// to the expression of the currently selected rule
			row.cbPreBuiltRules.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {

					int index = row.cbPreBuiltRules.getSelectionIndex();
					if (index != -1) {
						if (index == 0) {
							row.wFilterRule.setText("");
						} else if (index < (row.cbPreBuiltRules.getItemCount() - 1)) {
							Rule r = preBuiltRules.getRules().get(index - 1);
							row.wFilterRule.setText(r.expression);
						}
						// Selecting/Deselecting Custom filter can change enablement.
						enable();
					}
				}
			});
			wLastRow = row.wFilterName;
		}
		return wComp;
	}

	/**
	 * Create the output filter group
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	private Control createOutputFilterGroup(Composite parent, Control wLastLine) {

		// Handle matchup plugin customizations
		// Create the outer group
		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText(getString("OutputFilterGroup.Label"));
		helper.setLook(wGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wLastLine, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		wGroup.setLayoutData(fd);
		// Description line
		Label outputFilterDescription = helper.addLabel(wGroup, null, "OutputFilterTab.OutputFilterDescription");
		// Create filtering controls
		Composite filterControls = createFilteringControls(wGroup, outputFilterDescription);
		// Create the validation button
		bValidate = helper.addPushButton(wGroup, filterControls, "OutputFilterTab.Validate", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				validateRules();
			}
		});
		return wGroup;
	}

	/**
	 * Create the result codes group
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	private Control createResultCodesGroup(Composite parent, Control wLastLine) {

		// Result Codes group
		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText(getString("ResultCodesGroup.Label"));
		helper.setLook(wGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(null, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		wGroup.setLayoutData(fd);
		// Result Code field selector
		Label   resultCodeDescription = helper.addLabel(wGroup, null, "OutputFilterTab.ResultCodesDescription");
		Control filler                = helper.addSpacer(wGroup, resultCodeDescription);
		helper.colWidth[0] = dialog.getProps().getMiddlePct();
		wResultCodes = helper.addTextBox(wGroup, filler, "OutputFilterTab.ResultCodes");
		return wGroup;
	}

	/**
	 * Handle enablement of controls
	 */
	private void enable() {

		// Enable filter row controls based on current values
		boolean disableRows = false;
		boolean customRules = false;
		for (FilterRow row : filterRows) {
			// Shortcut
			if (disableRows) {
				row.wFilterName.setEnabled(false);
				row.cbPreBuiltRules.setEnabled(false);
				row.wFilterRule.setEnabled(false);
				row.cbTargetName.setEnabled(false);
				row.miRemove.setEnabled(false);
				row.miSave.setEnabled(false);
				continue;
			}
			// Enable the name field
			row.wFilterName.setEnabled(true);
			// If name is not defined then filter is not defined, disable all other controls on this row
			if (Const.isEmpty(row.wFilterName.getText())) {
				row.cbPreBuiltRules.setEnabled(false);
				row.wFilterRule.setEnabled(false);
				row.cbTargetName.setEnabled(false);
				row.miRemove.setEnabled(false);
				row.miSave.setEnabled(false);
				// All subsequent rows are disabled
				disableRows = true;
				continue;
			}
			// Enable the rule combo box System.out
			row.cbPreBuiltRules.setEnabled(true);
			// Enable the filter rule if customer filtering is selected
			boolean customRule = (row.cbPreBuiltRules.getSelectionIndex() == (row.cbPreBuiltRules.getItemCount() - 1));
			row.wFilterRule.setEnabled(customRule);
			customRules = customRules || customRule;
			// Enable the remove/save menu items based on customer filtering
			boolean noRule = (row.cbPreBuiltRules.getSelectionIndex() == 0);
			row.miRemove.setEnabled(!noRule && !customRule);
			row.miSave.setEnabled(customRule);
			// Enable the target selection
			row.cbTargetName.setEnabled(true);
		}
		// Enable validation if there is at least one custom rule
		bValidate.setEnabled(customRules);
	}

	/**
	 * @return A list of current filter targets as defined in the controls
	 */
	private List<FilterTarget> getFilterData() {

		List<FilterTarget> targets = new ArrayList<FilterTarget>();
		for (FilterRow filterRow : filterRows) {
			// We are done if it doesn't have a name
			if (Const.isEmpty(filterRow.wFilterName.getText())) {
				break;
			}
			FilterTarget target = new FilterTarget();
			target.setName(filterRow.wFilterName.getText().trim());
			target.setRule(filterRow.wFilterRule.getText().replaceAll("\\s+", " "));
			target.setTargetStepname(filterRow.cbTargetName.getText().trim());
			target.setTargetStep(helper.getDialog().findStep(target.getTargetStepname()));
			targets.add(target);
		}
		return targets;
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.OutputFilterTab." + key, args);
	}

	/**
	 * Initializes the filtering controls based on the current filtering rules.
	 * <p>
	 * If the rule is blank then "No Filtering" will be selected.
	 * <p>
	 * If the rule matches a pre-built rule then "Pre-Built Rule" filtering will be selected.
	 * <p>
	 * Otherwise the "Custom Rule" filtering will be selected.
	 *
	 * @param targets
	 * @throws KettleException
	 */
	private void initFilteringControls(List<FilterTarget> targets) throws KettleException {

		// Initialize the interface to the pre-built rules
		preBuiltRules = new PreBuiltRules(bcMeta);
		for (int i = 0; i < filterRows.length; i++) {
			FilterRow row = filterRows[i];
			// If there is a defined target then fill it in
			FilterTarget target = (i < targets.size()) ? targets.get(i) : new FilterTarget();
			// Set filter name
			row.wFilterName.setText(target.getName());
			// Set current filtering rule
			String newFilter = target.getRule().trim().replaceAll("\\s+", " ");
			row.wFilterRule.setText(newFilter);
			// Get a matching pre-built rule (if any)
			Rule rule = preBuiltRules.getRuleFromFilter(newFilter);
			// Populate the pre-built rules combo
			row.cbPreBuiltRules.removeAll();
			// First entry is always the no filter rule
			row.cbPreBuiltRules.add(getString("NoFilter"));
			if (newFilter.length() == 0) {
				row.cbPreBuiltRules.select(0);
			}
			// Fill out with pre-defined rules
			for (PreBuiltRules.Rule r : preBuiltRules.getRules()) {
				row.cbPreBuiltRules.add(r.name);
				// If a pre-built rule is being used then select it
				if (rule == r) {
					row.cbPreBuiltRules.select(row.cbPreBuiltRules.getItemCount() - 1);
				}
			}
			// Complete with custom rule
			row.cbPreBuiltRules.add(getString("CustomFilter"));
			if ((newFilter.length() != 0) && (rule == null)) {
				row.cbPreBuiltRules.select(row.cbPreBuiltRules.getItemCount() - 1);
			}
			// Fill in the targets
			List<StepMeta> nextSteps = dialog.findNextSteps();
			for (int j = 0; j < nextSteps.size(); j++) {
				StepMeta stepMeta = nextSteps.get(j);
				row.cbTargetName.add(stepMeta.getName());
				StepMeta targetStep = target.getTargetStep();
				if ((targetStep != null) && targetStep.getName().equals(stepMeta.getName())) {
					row.cbTargetName.select(row.cbTargetName.getItemCount() - 1);
				}
			}
		}
	}

	/**
	 * Called to remove the currently selected pre-built rule
	 *
	 * @param item
	 */
	private void removeRule(MenuItem item) {

		// Get the row we are modifying
		FilterRow row = (FilterRow) item.getData();
		// Get the current rule
		String filter = row.wFilterRule.getText();
		Rule   rule   = preBuiltRules.getRuleFromFilter(filter);
		// Should we remove it?
		MessageBox box = new MessageBox(helper.getShell(), SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
		box.setText(getString("RemoveRule.Title"));
		box.setMessage(getString("RemoveRule.Message", rule.name));
		if (box.open() == SWT.YES) {
			try {
				// Remove the rule
				preBuiltRules.removeRule(rule.name);
				// Re-initialize the filter controls
				initFilteringControls(getFilterData());
				// Update enablements
				enable();
			} catch (KettleException e) {
				// Warn the user and return
				new ErrorDialog(helper.getShell(), getString("RemoveRule.Error.Title"), getString("RemoveRule.Error.Message"), e);
				return;
			}
		}
	}

	/**
	 * Called to save a custom rule as a new pre-built rule
	 *
	 * @param item
	 */
	private void saveRule(MenuItem item) {

		// Get the row we are modifying
		FilterRow row = (FilterRow) item.getData();
		// Make sure the custom rule does not match an existing rule
		String newFilter = row.wFilterRule.getText().trim().replaceAll("\\s+", " ");
		Rule   rule      = preBuiltRules.getRuleFromFilter(newFilter);
		if (rule != null) {
			MessageBox box = new MessageBox(helper.getShell(), SWT.CANCEL | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText(getString("SaveRule.Title"));
			box.setMessage(getString("SaveRule.ExpressionAlreadyDefined", newFilter, rule.name));
			box.open();
			return;
		}
		// Get the name of the new rule
		InputDialog dialog = new InputDialog(helper.getShell(), getString("SaveRule.Title"), getString("SaveRule.Message"), null, new IInputValidator() {
			@Override
			public String isValid(String newName) {

				// Make sure the name does not match an existing name
				newName = newName.trim().replaceAll("\\s+", " ");
				for (Rule rule : preBuiltRules.getRules()) {
					if (rule.name.equalsIgnoreCase(newName)) {
						return getString("SaveRule.RuleAlreadyDefined", newName);
					}
				}
				return null;
			}
		});
		if (dialog.open() == Window.OK) {
			try {
				// Add the new rule to the current set of pre-built rules
				String newName = dialog.getValue().trim().replaceAll("\\s+", " ");
				preBuiltRules.addRule(newName, newFilter);
				// Re-initialize the filtering rule controls
				initFilteringControls(getFilterData());
				// Update enablements
				enable();
			} catch (KettleException e) {
				// Warn the user and return
				new ErrorDialog(helper.getShell(), getString("SaveRule.Error.Title"), getString("SaveRule.Error.Message"), e);
				return;
			}
			// Tell the world that the dialog has changed
			helper.getDialog().setChanged();
		}
	}

// public MDPropertyWebServiceDialog getDialog(){
// return dialog;
// }

	/**
	 * Called to validate custom rules
	 */
	private void validateRules() {

		String      tmpRule       = "";
		String      resultMessage = "";
		String[]    resultAr      = new String[20];
		Validations validator     = new Validations();
		try {
			for (FilterRow row : filterRows) {
				// Only validate defined custom rules
				if (!Const.isEmpty(row.wFilterName.getText())) {
					boolean customRule = (row.cbPreBuiltRules.getSelectionIndex() == (row.cbPreBuiltRules.getItemCount() - 1));
					if (customRule) {
						if (!Const.isEmpty(row.wFilterRule.getText())) {
							// add space between operator and [] i.e. NOT [AS01] vs NOT[AS01]
							// so MDBinaryEvaluator can properly tokenize. using replace
							row.wFilterRule.setText(row.wFilterRule.getText().replace("[", " [").replace("]", "] ").replaceAll("\\s+", " ").toUpperCase().trim());
							// format the rule for MDBinaryEvaluator and neatness
							tmpRule = row.wFilterRule.getText().replace("[", " [").replace("]", "] ").replaceAll("\\s+", " ").toUpperCase().trim();
							// Create an evaluator instance. Will throw an exception if there is a problem
							new MDBinaryEvaluator(tmpRule);
							resultAr = tmpRule.split(" "); // Separate into individual result codes
							int i = 0;
							while (i <= (resultAr.length - 1)) {
								if (resultAr[i].startsWith("[")) {// only check result codes
									// strip it down & check if it is a known result code
									if (!validator.verifyResultCode(resultAr[i].replace("[", "").replace("]", "").trim())) {
										resultMessage += resultAr[i] + getString("ValidateRule.UnknownCode.Message");//" Is an unknown result code\n";
									}
								}
								i++;
							}
						} else {
							resultMessage = "Custom Filter must contain at least one expression";
						}
					}
				}
			}
			if (resultMessage.length() < 1) {
				MessageDialog.openInformation(helper.getShell(), getString("ValidateRule.Ok.Title"), //$NON-NLS-1$
						getString("ValidateRule.Ok.Message"));//$NON-NLS-1$
			} else {
				MessageDialog.openWarning(helper.getShell(), getString("ValidateRule.Warning.Title"), getString("ValidateRule.Warning.Message", "\n") + resultMessage);
			}
		} catch (EvaluatorException e) {
			MessageDialog.openWarning(helper.getShell(), getString("ValidateRule.Warning.Title"), //$NON-NLS-1$
					getString("ValidateRule.Warning.Message", e.getMessage()));//$NON-NLS-1$
		} catch (KettleException e) {
			new ErrorDialog(helper.getShell(), getString("ValidateRule.Error.Title"), //$NON-NLS-1$
					getString("ValidateRule.Error.Message"), //$NON-NLS-1$
					e);
		}
	}

	@Override
	public void dispose() {

	}
}
