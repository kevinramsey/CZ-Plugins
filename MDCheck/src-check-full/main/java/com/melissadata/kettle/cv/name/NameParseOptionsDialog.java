package com.melissadata.kettle.cv.name;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.cz.support.MDPropTags;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.util.ImageUtil;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class NameParseOptionsDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= MDCheckMeta.class;
	private Button			wCorrectMispellings;
	private ComboViewer		vNameOrderHint;
	private ComboViewer		vGenderAggression;
	private ComboViewer		vGenderPopulation;
	private Text			wSalutationPrefix;
	private Text			wSalutationSuffix;
	private Text			wSalutationSlug;
	private ComboViewer		vMiddleNameLogic;
	private Label			lblSalutationOrder;
	private ListViewer		vSalutationOrder;
	private Button			wUp;
	private Button			wDown;
	private ServiceType		serviceType;

	/**
	 * Called to create the Name Parse Options dialog box.
	 *
	 * @param dialog
	 */
	public NameParseOptionsDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
	}

	/**
	 * Called to create the salutation order list composite
	 *
	 * @param wLastLine
	 * @return
	 */
	private Composite createSalutationOrderContents(Composite parent, Control wLastLine) {
		// Create composite for the salutation order controls
		Composite wSalutationComp = new Composite(parent, SWT.NONE);
		setLook(wSalutationComp);
		FormLayout fl = new FormLayout();
		fl.marginHeight = margin;
		fl.marginWidth = margin;
		wSalutationComp.setLayout(fl);
		// Place it below previous control
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		wSalutationComp.setLayoutData(fd);
		// Create the controls
		lblSalutationOrder = new Label(wSalutationComp, SWT.LEFT | SWT.WRAP);
		lblSalutationOrder.setText(getString("SalutationOrder.Label")); //$NON-NLS-1$
		setLook(lblSalutationOrder);
		vSalutationOrder = new ListViewer(wSalutationComp, SWT.BORDER);
		final List wSalutationOrder = vSalutationOrder.getList();
		setLook(wSalutationOrder);
		wUp = new Button(wSalutationComp, SWT.PUSH);
		wUp.setImage(ImageUtil.getImage(display, this.getClass(), "com/melissadata/kettle/images/UpArrow.gif"));
		setLook(wUp);
		wDown = new Button(wSalutationComp, SWT.PUSH);
		wDown.setImage(ImageUtil.getImage(display, this.getClass(), "com/melissadata/kettle/images/DownArrow.gif"));
		setLook(wDown);
		// Layout controls
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(0, margin);
		lblSalutationOrder.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(lblSalutationOrder, margin);
		fd.right = new FormAttachment(wUp, -margin);
		fd.bottom = new FormAttachment(100, -margin);
		wSalutationOrder.setLayoutData(fd);
		fd = new FormData();
// fd.left = new FormAttachment(wSalutationOrder, margin);
		fd.top = new FormAttachment(lblSalutationOrder, margin);
		fd.right = new FormAttachment(100, -margin);
		fd.width = wUp.getImage().getImageData().width;
		fd.height = wUp.getImage().getImageData().height;
		wUp.setLayoutData(fd);
		fd = new FormData();
// fd.left = new FormAttachment(wSalutationOrder, margin);
		fd.top = new FormAttachment(wUp, margin);
		fd.right = new FormAttachment(100, -margin);
		fd.width = wDown.getImage().getImageData().width;
		fd.height = wDown.getImage().getImageData().height;
		wDown.setLayoutData(fd);
		// Add providers for list viewer
		vSalutationOrder.setContentProvider(new ArrayContentProvider());
		vSalutationOrder.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				NameParseMeta.Salutation salutation = (NameParseMeta.Salutation) element;
				String text = BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder." + salutation.name()) + " (\"" + BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder." + salutation.name() + ".Example") + "\")";
				return text;
			}
		});
		// Add enablement listener to the list
		vSalutationOrder.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				enableUpDown();
			}
		});
		// Add selection listeners for the up/down buttons
		wUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				moveSalutationOrder(true);
			}
		});
		wDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				moveSalutationOrder(false);
			}
		});
		return wSalutationComp;
	}

	/**
	 * Called to handle the enablement of controls
	 */
	private void enable() {
		// These controls are enabled only when in a local service mode
		boolean localService = serviceType == ServiceType.Local;
		Control wMiddleNameLogic = vMiddleNameLogic.getControl();
		Control lblMiddleNameLogic = ((FormData) wMiddleNameLogic.getLayoutData()).left.control;
		lblMiddleNameLogic.setEnabled(localService);
		wMiddleNameLogic.setEnabled(localService);
		lblSalutationOrder.setEnabled(localService);
		vSalutationOrder.getList().setEnabled(localService);
		wUp.setEnabled(localService);
		wDown.setEnabled(localService);
		// If in local service then enable up/down controls
		if (localService) {
			enableUpDown();
		}

		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_NameObject)){
			wCorrectMispellings.setEnabled(false);
			vNameOrderHint.getCCombo().setEnabled(false);
			vGenderAggression.getCCombo().setEnabled(false);
			vGenderPopulation.getCCombo().setEnabled(false);
			wSalutationPrefix.setEnabled(false);
			wSalutationSuffix.setEnabled(false);
			wSalutationSlug.setEnabled(false);
			vMiddleNameLogic.getCCombo().setEnabled(false);

			wUp.setEnabled(false);
			wDown.setEnabled(false);

			vSalutationOrder.getControl().setEnabled(false);
		}
	}

	/**
	 * Called to enable the up/down buttons based on the current salutation order selection
	 */
	private void enableUpDown() {
		// Enable up/down controls based on what is selected.
		IStructuredSelection selection = (IStructuredSelection) vSalutationOrder.getSelection();
		NameParseMeta.Salutation[] salutations = (NameParseMeta.Salutation[]) vSalutationOrder.getInput();
		wUp.setEnabled((selection.size() == 1) && (selection.getFirstElement() != salutations[0]));
		wDown.setEnabled((selection.size() == 1) && (selection.getFirstElement() != salutations[salutations.length - 1]));
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		wCorrectMispellings = addCheckBox(wComp, null, "CorrectMispellings");
		Label spacer = addSpacer(wComp, wCorrectMispellings);
		vNameOrderHint = addEnumComboBox(wComp, spacer, "NameOrderHint", NameParseMeta.NameOrderHint.values());
		vGenderAggression = addEnumComboBox(wComp, vNameOrderHint.getControl(), "GenderAggression", NameParseMeta.GenderAggression.values());
		vGenderPopulation = addEnumComboBox(wComp, vGenderAggression.getControl(), "GenderPopulation", NameParseMeta.GenderPopulation.values());
		wSalutationPrefix = addTextBox(wComp, vGenderPopulation.getControl(), "SalutationPrefix");
		wSalutationSuffix = addTextBox(wComp, wSalutationPrefix, "SalutationSuffix");
		wSalutationSlug = addTextBox(wComp, wSalutationSuffix, "SalutationSlug");
		vMiddleNameLogic = addEnumComboBox(wComp, wSalutationSlug, "MiddleNameLogic", NameParseMeta.MiddleNameLogic.values());
		// Create the salutation order list
		Composite wSalutationComp = createSalutationOrderContents(wComp, vMiddleNameLogic.getControl());
		// Local Service only options
		((FormData) wSalutationComp.getLayoutData()).bottom = new FormAttachment(100, -margin); // pin to bottom of container
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) {
		// Local version of input that is dialog specific
		NameParseMeta npMeta = ((MDCheckStepData) data).getNameParse();
		// Make sure salutations is taken care of
		// slugChange();
		// Load data from controls
		npMeta.setCorrectMispellings(wCorrectMispellings.getSelection());
		npMeta.setNameOrderHint((NameParseMeta.NameOrderHint) ((IStructuredSelection) vNameOrderHint.getSelection()).getFirstElement());
		npMeta.setGenderAggression((NameParseMeta.GenderAggression) ((IStructuredSelection) vGenderAggression.getSelection()).getFirstElement());
		npMeta.setGenderPopulation((NameParseMeta.GenderPopulation) ((IStructuredSelection) vGenderPopulation.getSelection()).getFirstElement());
		npMeta.setSalutationPrefix(wSalutationPrefix.getText());
		npMeta.setSalutationSuffix(wSalutationSuffix.getText());
		npMeta.setSalutationSlug(wSalutationSlug.getText());
		// Load data from local service controls
		ServiceType serviceType = ((MDCheckStepData) data).getAdvancedConfiguration().getServiceType();
		if (serviceType == ServiceType.Local) {
			npMeta.setMiddleNameLogic((NameParseMeta.MiddleNameLogic) ((IStructuredSelection) vMiddleNameLogic.getSelection()).getFirstElement());
			npMeta.setSalutationOrder((NameParseMeta.Salutation[]) vSalutationOrder.getInput());
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
			return "MDCheck.Help.NameParseOptionsDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractDialog#getPackage()
	 */
	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractDialog#getStringPrefix()
	 */
	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.NameParseOptionsDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		MDCheckStepData stepData = (MDCheckStepData) data;
		// Local version of input that is dialog specific
		NameParseMeta npMeta = stepData.getNameParse();
		// Get the service type
		serviceType = stepData.getAdvancedConfiguration().getServiceType();
		// Initialize controls
		wCorrectMispellings.setSelection(npMeta.getCorrectMispellings());
		vNameOrderHint.setSelection(new StructuredSelection(npMeta.getNameOrderHint()));
		vGenderAggression.setSelection(new StructuredSelection(npMeta.getGenderAggression()));
		vGenderPopulation.setSelection(new StructuredSelection(npMeta.getGenderPopulation()));
		wSalutationPrefix.setText(npMeta.getSalutationPrefix());
		wSalutationSuffix.setText(npMeta.getSalutationSuffix());
		wSalutationSlug.setText(npMeta.getSalutationSlug());
		vMiddleNameLogic.setSelection(new StructuredSelection(npMeta.getMiddleNameLogic()));
		vSalutationOrder.setInput(npMeta.getSalutationOrder().clone());
		// Initialize enablement
		enable();
		// this.getShell().pack();
		getShell().setSize(463, 525);
	}

	/**
	 * Called to move the currently selected salutation in the salutation order list.
	 *
	 * @param moveUp
	 *            true = up, false = down
	 */
	protected void moveSalutationOrder(boolean moveUp) {
		boolean moveDown = !moveUp;
		// Get the currently selected item
		IStructuredSelection selection = (IStructuredSelection) vSalutationOrder.getSelection();
		if (selection.size() != 1) { return; // There can be only one
		}
		NameParseMeta.Salutation curSalutation = (NameParseMeta.Salutation) selection.getFirstElement();
		// Get the currently selected item(s)
		NameParseMeta.Salutation[] salutations = (NameParseMeta.Salutation[]) vSalutationOrder.getInput();
		int oldIndex = 0;
		for (; (oldIndex < salutations.length) && (salutations[oldIndex] != curSalutation); oldIndex++) {
			;
		}
		int newIndex = -1;
		if (moveUp) {
			if (oldIndex == 0) { return; }
			newIndex = oldIndex - 1;
		}
		if (moveDown) {
			if (oldIndex == (salutations.length - 1)) { return; }
			newIndex = oldIndex + 1;
		}
		// Update the item list
		NameParseMeta.Salutation swap = salutations[oldIndex];
		salutations[oldIndex] = salutations[newIndex];
		salutations[newIndex] = swap;
		vSalutationOrder.setSelection(selection, true);
		vSalutationOrder.refresh();
		// Remember the change
		changed = true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.AbstractDialog#setInitialFocus()
	 */
	protected boolean setInitialFocus() {
		return wCorrectMispellings.setFocus();
	}
}
