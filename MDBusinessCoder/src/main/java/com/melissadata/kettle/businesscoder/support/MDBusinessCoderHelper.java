package com.melissadata.kettle.businesscoder.support;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.TextVar;

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;

public class MDBusinessCoderHelper {

	private static Class<?>			PKG	= MDBusinessCoderMeta.class;

	private MDBusinessCoderDialog	dialog;

	public int						margin;

	public int[]					colWidth;

	public int[]					colHeight;

	public PropsUI					props;

	public MDBusinessCoderHelper(MDBusinessCoderDialog dialog) {

		this.dialog = dialog;

		props = PropsUI.getInstance();
		margin = Const.MARGIN;

		colWidth = new int[3];
		colWidth[0] = dialog.getProps().getMiddlePct();
		colWidth[1] = (colWidth[0] / 2) + 50;
		colWidth[2] = 100;

		colHeight = new int[4];

		colHeight[0] = 25;
		colHeight[1] = 50;
		colHeight[2] = 75;
		colHeight[3] = 100;
	}

	/**
	 * Called to add a checkbox to the parent composite. It will be placed after the last line.
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	public Button addCheckBox(Composite parent, Control wLastLine, String name) {

		// Create the checkbox with a descriptive label
		Button wCheckBox = addDataButton(parent, wLastLine, name, SWT.CHECK);

		// Handle carriage return
		wCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				dialog.ok();
			}
		});

		return wCheckBox;
	}

	/**
	 * Called to a single combo box with label
	 *
	 * @param parent
	 *            Composite parent for this control
	 * @param wLastLine
	 *            Control below which we should place this control
	 * @param name
	 *            Text name to use in the control
	 */
	public Control[] addComboBox(Composite parent, Control wLastLine, String name) {

		return addComboBoxes(parent, wLastLine, name, true);
	}

	/**
	 * Called to add one or more combo boxes with a label
	 *
	 * @param parent
	 *            Composite parent for this control
	 * @param wLastLine
	 *            Control below which we should place this control
	 * @param name
	 *            Text name to use in the control
	 * @param createOnlyOne
	 */
	public Control[] addComboBoxes(Composite parent, Control wLastLine, String name, boolean createOnlyOne) {

		Control wControls[] = new Control[createOnlyOne ? 2 : 3];

		// Create a label (if there is a name)
		Label label = new Label(parent, SWT.RIGHT);
		props.setLook(label);
		wControls[0] = label;
		if (name != null) {
			label.setText(getLabelString(name));
		} else {
			label.setVisible(false);
		}

		// Place label in first column
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(colWidth[0], -margin);
		label.setLayoutData(fd);

		// Create one or two combo boxes
		for (int i = 1; i < wControls.length; i++) {
			CCombo wCombo = new CCombo(parent, SWT.BORDER);
			wControls[i] = wCombo;
			wCombo.setVisibleItemCount(10);

			// Update dialog when something changes
			wCombo.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent arg0) {

					dialog.setChanged();
				}
			});

			// Handle someone pressing CR
			wCombo.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetDefaultSelected(SelectionEvent event) {

					dialog.ok();
				}

				@Override
				public void widgetSelected(SelectionEvent event) {

					dialog.setChanged();
				}
			});

			fd = new FormData();
			fd.left = new FormAttachment(colWidth[i - 1], margin);
			fd.top = new FormAttachment(wLastLine, margin);
			fd.right = new FormAttachment(createOnlyOne ? colWidth[2] : colWidth[i], -margin);
			wCombo.setLayoutData(fd);
		}

		// Return created controls
		return wControls;
	}

	/**
	 * Called to create a managed combo box.
	 */
	public ComboViewer addEnumComboBox(Composite parent, Control wLastLine, String name, Enum<?>[] values) {

		// Create the combo box
		Control[] wControls = addComboBox(parent, wLastLine, name);
		CCombo wCombo = (CCombo) wControls[1];

		// Can't edit it
		wCombo.setEditable(false);

		// Create the viewer around the box
		ComboViewer viewer = new ComboViewer(wCombo);
		viewer.setContentProvider(new ArrayContentProvider());

		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				return element.toString();
			}
		});

		// Initialize its values
		viewer.setInput(values);

		return viewer;
	}

	/**
	 * Called to add a combox that is populated with values from the input step
	 *
	 * @param parent
	 *            Composite parent for this control
	 * @param wLastLine
	 *            Control below which we should place this control
	 * @param name
	 *            Text name to use in the control
	 * @param usageID
	 *            ID to use when tracking usage of source fields
	 * @return
	 */
	public MDInputCombo addInputComboBox(Composite parent, Control wLastLine, String name, String usageID) {

		// Create a new combo box
		Control[] wControls = addComboBox(parent, wLastLine, name);

		// Return the new combo
		return new MDInputCombo(wControls, dialog.getSourceFields(), usageID);
	}

	/**
	 * Called to add a combox that is populated with values from the input step
	 *
	 * @param parent
	 *            Composite parent for this control
	 * @param wLastLine
	 *            Control below which we should place this control
	 * @param name
	 *            Text name to use in the control
	 * @param usageID
	 *            ID to use when tracking usage of source fields
	 * @return
	 */
//	public MDInputCombo addInputSelectedComboBox(Composite parent, Control wLastLine, String name, String usageID) {
//
//		// Create a new combo box
//		Control[] wControls = addComboBox(parent, wLastLine, name);
//
//		// Return the new combo
//		return new MDInputCombo(wControls, dialog.getInputFields(), usageID);
//	}

	/**
	 * Called to create a single label line
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	public Label addLabel(Composite parent, Control wLastLine, String name) {

		// Create a label
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText(getLabelString(name));
		props.setLook(label);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		label.setLayoutData(fd);

		return label;
	}

	/**
	 * Called to create a single push button
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @param selectionListener
	 * @return
	 */
	public Button addPushButton(Composite parent, Control wLastLine, String name, SelectionListener selectionListener) {

		// Create the button
		Button button = addButton(parent, wLastLine, name, SWT.PUSH);

		// Add listener
		button.addSelectionListener(selectionListener);

		// return
		return button;
	}

	/**
	 * Called to create a single radio button
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	public Button addRadioButton(Composite parent, Control wLastLine, String name) {

		// Create the button
		Button button = addDataButton(parent, wLastLine, name, SWT.RADIO);

		// Update dialog when radio selection changes
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {

				dialog.ok();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {

				dialog.setChanged();
			}
		});

		// Return
		return button;
	}

	/**
	 * Create a spacer line
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	public Label addSpacer(Composite parent, Control wLastLine) {

		// Create a label
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		props.setLook(label);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		label.setLayoutData(fd);

		return label;
	}

	/**
	 * Called to create a single spinner
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @param description
	 * @return
	 */
	public Spinner addSpinner(Group parent, Control wLastLine, String name, String description) {

		// Create a label (if there is a name)
		if (name != null) {
			Label label = new Label(parent, SWT.RIGHT);
			label.setText(getLabelString(name));
			props.setLook(label);

			// Place label in first column
			FormData fd = new FormData();
			fd.left = new FormAttachment(0, margin);
			fd.top = new FormAttachment(wLastLine, margin);
			fd.right = new FormAttachment(colWidth[0], -margin);
			label.setLayoutData(fd);
		}

		// Create the spinner
		Spinner spinner = new Spinner(parent, SWT.BORDER);
		setLook(spinner);

		// Lay it out
		FormData fd = new FormData();
		fd.left = new FormAttachment(colWidth[0], margin);
		fd.top = new FormAttachment(wLastLine, margin);
		spinner.setLayoutData(fd);

		// Update dialog when radio selection changes
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {

				dialog.ok();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {

				dialog.setChanged();
			}
		});

		// Create a description (if there is one)
		if (description != null) {
			Label label = new Label(parent, SWT.LEFT);
			label.setText(getLabelString(description));
			props.setLook(label);

			// Place label in first column
			fd = new FormData();
			fd.left = new FormAttachment(spinner, margin);
			fd.top = new FormAttachment(wLastLine, margin);
			fd.right = new FormAttachment(100, -margin);
			label.setLayoutData(fd);
		}

		// Return
		return spinner;
	}

	/**
	 * Called to add a single text boxes with a label
	 *
	 * @param parent
	 *            The parent control that will hold this control
	 * @param wLastLine
	 *            the control below which this control should be placed
	 * @param lastLineMargin
	 * @param name
	 *            The text identifier for this control
	 */
	public Text addTextBox(Composite parent, Control wLastLine, int lastLineMargin, String name) {

		return addTextBoxes(parent, wLastLine, lastLineMargin, name, true)[0];
	}

	/**
	 * Called to add a single text boxes with a label
	 *
	 * @param parent
	 *            The parent control that will hold this control
	 * @param wLastLine
	 *            the control below which this control should be placed
	 * @param name
	 *            The text identifier for this control
	 */
	public Text addTextBox(Composite parent, Control wLastLine, String name) {

		return addTextBoxes(parent, wLastLine, margin, name, true)[0];
	}

	/**
	 * Called to add multiple text boxes with a label
	 *
	 * @param parent
	 *            The parent control that will hold this control
	 * @param wLastLine
	 *            the control below which this control should be placed
	 * @param lastLineMargin
	 * @param name
	 *            The text identifier for this control
	 */
	public Text[] addTextBoxes(Composite parent, Control wLastLine, int lastLineMargin, String name) {

		return addTextBoxes(parent, wLastLine, lastLineMargin, name, false);
	}

	/**
	 * Called to add one or more text boxes with a label
	 *
	 * @param parent
	 *            The parent control that will hold this control
	 * @param wLastLine
	 *            the control below which this control should be placed
	 * @param lastLineMargin
	 * @param name
	 *            The text identifier for this control
	 * @param createOnlyOne
	 */
	public Text[] addTextBoxes(Composite parent, Control wLastLine, int lastLineMargin, String name, boolean createOnlyOne) {

		// Don't create label if name is not supplied
		if (name != null) {
			// Create a label
			Label label = new Label(parent, SWT.RIGHT);
			label.setText(getLabelString(name));
			props.setLook(label);

			// Place label in first column
			FormData fd = new FormData();
			fd.left = new FormAttachment(0, margin);
			fd.top = new FormAttachment(wLastLine, lastLineMargin);
			fd.right = new FormAttachment(colWidth[0], -margin);
			label.setLayoutData(fd);
		}

		// Create one or two text boxes
		Text wBoxes[] = new Text[createOnlyOne ? 1 : 2];
		for (int i = 0; i < wBoxes.length; i++) {
			wBoxes[i] = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);

			// Inform dialog when something changes
			wBoxes[i].addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent arg0) {

					dialog.setChanged();
				}
			});

			// Handle CR
			wBoxes[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetDefaultSelected(SelectionEvent event) {

					dialog.ok();
				}
			});

			// Select all text when focus gained
			final Text wBox = wBoxes[i];
			wBoxes[i].addFocusListener(new FocusAdapter() {

				@Override
				public void focusGained(FocusEvent arg0) {

					wBox.selectAll();
				}
			});

			FormData fd = new FormData();
			fd.left = new FormAttachment(colWidth[i], margin);
			fd.top = new FormAttachment(wLastLine, lastLineMargin);
			fd.right = new FormAttachment(createOnlyOne ? colWidth[2] : colWidth[i + 1], -margin);
			wBoxes[i].setLayoutData(fd);
		}

		// Return created controls
		return wBoxes;
	}

	/**
	 * Called to add multiple text boxes with a label
	 *
	 * @param parent
	 *            The parent control that will hold this control
	 * @param wLastLine
	 *            the control below which this control should be placed
	 * @param name
	 *            The text identifier for this control
	 */
	public Text[] addTextBoxes(Composite parent, Control wLastLine, String name) {

		return addTextBoxes(parent, wLastLine, margin, name, false);
	}

	/**
	 * Called to create a text box that can take environment variables
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	public TextVar addTextVarBox(Composite parent, Control wLastLine, String name) {

		// Don't create label if name is not supplied
		if (name != null) {
			// Create a label
			Label label = new Label(parent, SWT.RIGHT);
			label.setText(getLabelString(name));
			props.setLook(label);

			// Place label in first column
			FormData fd = new FormData();
			fd.left = new FormAttachment(0, margin);
			fd.top = new FormAttachment(wLastLine, margin);
			fd.right = new FormAttachment(colWidth[0], -margin);
			label.setLayoutData(fd);
		}

		// Create one or two text boxes
		final TextVar wBox;
		wBox = new TextVar(dialog.getSpace(), parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);

		// Inform dialog when something changes
		wBox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				dialog.setChanged();
			}
		});

		// Handle CR
		wBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {

				dialog.ok();
			}
		});

		// Select all text when focus gained
		wBox.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent arg0) {

				wBox.selectAll();
			}
		});

		FormData fd = new FormData();
		fd.left = new FormAttachment(colWidth[0], margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		wBox.setLayoutData(fd);

		// Return created controls
		return wBox;
	}

	/**
	 * @return The dialog we are helping with
	 */
	public MDBusinessCoderDialog getDialog() {

		return dialog;
	}

	/**
	 * @return Display for this helper
	 */
	public Display getDisplay() {

		return getShell().getDisplay();
	}

	/**
	 * @return Current ui shell
	 */
	public Shell getShell() {

		return dialog.getShell();
	}

	/**
	 * Configures the look of a control
	 *
	 * @param control
	 */
	public void setLook(Control control) {

		props.setLook(control);
	}

	/**
	 * Configures the look of a control to a specific style
	 *
	 * @param control
	 * @param style
	 */
	public void setLook(Control control, int style) {

		props.setLook(control, style);
	}

	/**
	 * Called to create a single button of a given type
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @param style
	 * @return
	 */
	private Button addButton(Composite parent, Control wLastLine, String name, int style) {

		// Create the button
		Button button = new Button(parent, style);
		if (name != null) {
			button.setText(getLabelString(name));
		}
		setLook(button);

		// Lay it out
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		button.setLayoutData(fd);
		return button;
	}

	/**
	 * Called to create a button that contains data
	 *
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @param style
	 * @return
	 */
	private Button addDataButton(Composite parent, Control wLastLine, String name, int style) {

		// Create the button
		Button button = addButton(parent, wLastLine, name, style);

		// Update dialog when radio selection changes
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {

				dialog.ok();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {

				dialog.setChanged();
			}
		});

		return button;
	}

	/**
	 * @param name
	 * @return
	 */
	private String getLabelString(String name) {

		if (name == null) { return ""; }
		return BaseMessages.getString(PKG, dialog.getNamingPrefix() + "." + name + ".Label");
	}
}
