package com.melissadata.cz.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.melissadata.cz.MDProps;

public abstract class MDAbstractDialog extends Dialog {
	
	protected MDDialogParent dialog;
	
	protected VariableSpace space;

	protected LogChannelInterface log;

	protected Object data;
	
	protected PropsUI props;

	protected int margin;

	protected int middlePct;

	protected Display display;

	private SelectionAdapter lsSelect;

	private ModifyListener lsModified;

	protected boolean changed;

	private Shell shell;

	protected Composite wComp;
	
	private Map<FontData, Font> fonts = new HashMap<FontData, Font>();

	public MDAbstractDialog(MDDialogParent dialog, int flags) {
		this(dialog.getShell(), dialog, dialog.getData(), flags);
	}

	public MDAbstractDialog(Shell parent, MDDialogParent dialog, int flags) {
		this(parent, dialog, dialog.getData(), flags);
	}

	public MDAbstractDialog(Shell parent, MDDialogParent dialog, Object data, int flags) {
		super(parent, flags);
		
		// Remember the dialog parent
		this.dialog = dialog;
		
		// Store the variable space and log interface
		this.space = dialog.getSpace();
		this.log = dialog.getLog();
		
		// Keep reference to the data
		this.data = data;
		
		// Initialize configuration
		props = PropsUI.getInstance();
		margin = Const.MARGIN;
		middlePct = 33; /*props.getMiddlePct();*/
		display = getParent().getDisplay();

		// Create the dialog controls
		create(getParent(), data);

		// Initialize with original data
		init(data);
		
		// Ignore any change flags raised by loading the data
		changed = false;
	}
		
	/**
	 * Called to create the dialog box.
	 * 
	 * @param parent
	 * @param data 
	 */
	protected void create(Shell parent, Object data) {
		// These handlers implement a virtual selection of OK when user presses <enter>
		lsSelect = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};
//		lsKey = new KeyAdapter() {
//			public void keyPressed(KeyEvent e) {
//				if (e.character == SWT.CR)
//					ok();
//			}
//		};
		
		// This handler tracks modification of configuration values
		lsModified = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				changed = true;
			}
		};
	
		// Create the dialog
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		String name = "Title";
		shell.setText(getString(name));
		shell.setImage(GUIResource.getInstance().getImageSpoon());
		setLook(shell);

		FormLayout fl = new FormLayout();
		fl.marginWidth = Const.FORM_MARGIN;
		fl.marginHeight = Const.FORM_MARGIN;
		shell.setLayout(fl);
		
		// Create scrolled composite to hold dialog contents
		ScrolledComposite wSComp = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());

		// Create composite that will contain all controls except the main buttons
		wComp = new Composite(wSComp, 0);
		setLook(wComp);
		wComp.setLayout(new FormLayout());

		// Create the dialog specific controls
		createContents(wComp, data);

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
		wSComp.setMinWidth(bounds.width + 2 * margin);
		wSComp.setMinHeight(bounds.height + 2 * margin);

		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, -50);
		wSComp.setLayoutData(fd);

		// Master dialog buttons
		Button[] buttons = getDialogButtons();
		BaseStepDialog.positionBottomButtons(shell, buttons, margin, wSComp);

		// Detect [X] or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (!cancel()) {
					e.doit = false;
				}
			}
		});

		// Initialize the size of the dialog box
		BaseStepDialog.setSize(shell);
	}

	/**
	 * Called to fill in the contents of the dialog composite
	 * 
	 * @param parent
	 */
	protected abstract void createContents(Composite parent, Object data);

	/**
	 * Called to get an array of buttons to put at the bottom of the dialog
	 * 
	 * @return
	 */
	protected Button[] getDialogButtons() {

		// Standard dialog buttons
		Button wOK = new Button(shell, SWT.PUSH);
		wOK.setText(getLocalString("System.Button.OK"));
		wOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		});

		Button wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(getLocalString("System.Button.Cancel"));
		wCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		});

		// help button
		Button wHelp = null;
		if (getHelpURLKey() != null) {
			wHelp = new Button(shell, SWT.PUSH);
			wHelp.setText(getLocalString("MDAbstractDialog.Button.Help"));
			wHelp.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					help();
				}
			});
		}

		if (wHelp != null)
			return new Button[] { wOK, wCancel, wHelp };
		else
			return new Button[] { wOK, wCancel };
	}

	/**
	 * Called to display the dialog
	 */
	public boolean open() {
		// Open the dialog 
		shell.open();

		// Wait for it to close
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		// Return changed flag
		return changed;
	}

	/**
	 * Called to fill in dialog controls with data 
	 * 
	 * @param data
	 */
	protected abstract void init(Object data);

	/**
	 * Called to return data from the dialog controls
	 * 
	 * @param data
	 * @throws KettleException 
	 */
	protected abstract boolean getData(Object data) throws KettleException;

	/**
	 * Called when dialog changes are accepted.
	 */
	public void ok() {

		try {
			// Don't update data unless something changed.
			// If there is any problem, cancel the ok
			if (changed && !getData(data))
				return;

			// Close the dialog
			dispose();

		} catch (KettleException e) {
			
			new ErrorDialog(shell, getLocalString("MDAbstractDialog.IllegalDialogSettings.Title"),
					getLocalString("MDAbstractDialog.IllegalDialogSettings.Message"), e);
		}
	}

	/**
	 * Called to handle cancelation of dialog actions
	 * 
	 * @return
	 */
	protected boolean cancel() {
		// If something changed the confirm that they want to cancel
		if (changed) {
			MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			
			box.setText(getLocalString("MDAbstractDialog.WarningDialogChanged.Title"));
			box.setMessage(getLocalString("MDAbstractDialog.WarningDialogChanged.Message", Const.CR));

			if (box.open() == SWT.NO)
				return false;
			
			// Revert persistent property changes
			MDProps.revert();
		}

		// Cancel change
		changed = false;

		// Close the dialog
		dispose();

		return true;
	}
	
	

	/**
	 * Display help information
	 */
	private void help() {
		String url = BaseMessages.getString(getPackage(), getHelpURLKey());
		BrowserDialog.displayURL(shell, dialog, url);
	}

	/**
	 * @return The URL for help information
	 */
	protected abstract String getHelpURLKey();

	/**
	 * Called to change the font style of a control
	 * 
	 * @param control 
	 * @param style
	 */
	public void setFontStyle(Control control, int style) {
		// Get the controls current font data
		FontData fontData = control.getFont().getFontData()[0];
		
		// Don't need to do anything if requested style is already set
		if (fontData.getStyle() == style)
			return;
		
		// Change the font style
		fontData.setStyle(style);
		
		// See if there is already a font for that style
		Font font = fonts.get(fontData);
		if (font == null) {
			// If not then create one
			font = new Font(shell.getDisplay(), fontData);
			fonts.put(fontData, font);
		}

		// Change the controls font
		control.setFont(font);
	}

	/**
	 * Close the dialog
	 */
	protected void dispose() {
		// Preserve dialog position and size
		props.setScreen(new WindowProperty(shell));

		// Dispose of the dialog
		shell.dispose();
		
		// Dispose of font resources
		for (Font font : fonts.values())
			font.dispose();
	}

	/**
	 * Called to enforce a common look on dialog controls
	 * 
	 * @param control
	 */
	protected void setLook(Control control) {
		props.setLook(control);
	}

	/**
	 * Called to enforce a common look on dialog controls
	 * 
	 * @param control
	 * @param style
	 */
	protected void setLook(Control control, int style) {
		props.setLook(control, style);
	}
	
	
	
	

	/**
	 * Called to add a spacer to the parent composite.
	 * 
	 * @param parent
	 * @param wLastLine
	 */
	protected Label addSpacer(Composite parent, Control wLastLine) {
		// The spacer is a blank label
		Label spacer = new Label(parent, SWT.LEFT);
		setLook(spacer);

		// Layout the spacer to be one margin high
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		fd.height = margin;
		spacer.setLayoutData(fd);
		
		return spacer;
	}
	
	/**
	 * Called to add a group
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	public Group addGroup(Composite parent, Control wLastLine, String name) {
	
		// Create group with a descriptive label
		Group wGroup = new Group(parent, SWT.NONE);
		if (name != null)
			wGroup.setText(getString(name + ".Label"));
		setLook(wGroup);
		
		FormLayout fl = new FormLayout();
		fl.marginHeight = margin;
		fl.marginWidth = margin;
		wGroup.setLayout(fl);
		
		// Place it below previous control
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		wGroup.setLayoutData(fd);
		
		return wGroup;
	}
	
	/**
	 * Special method to create two columns composites within a larger group.
	 * 
	 * All three composites will be returned to the caller in an array. The first element is the larger group.
	 * The two columns are the remaining two elements.
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param label
	 * @return
	 */
	protected Composite[] add2ColumnGroup(Composite parent, Control wLastLine, String label) {
	
		Composite[] wGroups = new Composite[3];
		
		// Create complete group
		wGroups[0] = addGroup(parent, wLastLine, label);
		
		// Create two invisible column groups
		wGroups[1] = new Composite(wGroups[0], SWT.NONE);
		setLook(wGroups[1]);
		wGroups[1].setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, -margin);
		wGroups[1].setLayoutData(fd);
		
		wGroups[2] = new Composite(wGroups[0], SWT.NONE);
		setLook(wGroups[2]);
		wGroups[2].setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(50, margin);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		wGroups[2].setLayoutData(fd);
		
		return wGroups;
	}

	/**
	 * Called to add a checkbox to the parent composite. It will be hooked into the regular facilities of
	 * dialog control. It will be placed after the last line.
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	protected Button addCheckBox(Composite parent, Control wLastLine, String name) {

		// Create the checkbox with a descriptive label
		Button wCheckBox = addDataButton(parent, wLastLine, name, SWT.CHECK);

		return wCheckBox;
	}
	
	

	/**
	 * Called to create a managed combo box.
	 */
	protected ComboViewer addEnumComboBox(Composite parent, Control wLastLine, String name, Enum<?>[] values) {
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
	 * Called to add a combo box to the parent composite. It will be hooked into the regular facilities of
	 * dialog control. It will be placed after the last line.
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	protected Control[] addComboBox(Composite parent, Control wLastLine, String name) {
		Control[] wControls = new Control[2];
		
		// Create the combox box with a descriptive label
		Label label = new Label(parent, SWT.RIGHT);
		wControls[0] = label;
		label.setText(getString(name + ".Label")); //$NON-NLS-1$
		setLook(label);
		CCombo wComboBox = new CCombo(parent, SWT.BORDER | SWT.READ_ONLY);
		setLook(wComboBox);
		wControls[1] = wComboBox;

		// Layout the label and combo box
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(middlePct, -margin);
		label.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(label, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		wComboBox.setLayoutData(fd);

		// Track modifications
		wComboBox.addModifyListener(lsModified);

		// Make sure it responds to <enter>
		wComboBox.addSelectionListener(lsSelect);

		return wControls;
	}

	/**
	 * Called to add a text box to the parent composite. It will be hooked into the regular facilities of
	 * dialog control. It will be placed after the last line.
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	protected TextVar addTextVarBox(Composite parent, Control wLastLine, String name) {
	
		// Create the text box with a descriptive label
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(getString(name + ".Label")); //$NON-NLS-1$
		setLook(label);
		final TextVar wTextBox = new TextVar(space, parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		setLook(wTextBox);

		// Layout the label and combo box
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(middlePct, -margin);
		label.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(label, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		wTextBox.setLayoutData(fd);

		// Track modifications
		wTextBox.addModifyListener(lsModified);

		// Make sure it responds to <enter>
		wTextBox.addSelectionListener(lsSelect);
		
		// Select all when text box receives focus
		wTextBox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				wTextBox.selectAll();
			}
		});

		return wTextBox;
	}

	/**
	 * Called to add a text box to the parent composite. It will be hooked into the regular facilities of
	 * dialog control. It will be placed after the last line.
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	protected Control[] addTextBoxWithLabel(Composite parent, Control wLastLine, String name) {
		
		Control[] textGroup = new Control[2];
		FormData fd = new FormData();
		
		// Create an (optional) descriptive label
		Label label = null;
		if (name != null) {
			label = new Label(parent, SWT.RIGHT);
			label.setText(getString(name + ".Label")); //$NON-NLS-1$
			setLook(label);

			fd.left = new FormAttachment(0, margin);
			fd.top = new FormAttachment(wLastLine, margin);
			fd.right = new FormAttachment(middlePct, -margin);
			label.setLayoutData(fd);

			textGroup[0] = label;
		}
		
		// Create the text box
		final Text wTextBox = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		setLook(wTextBox);

		// Layout the text box
		fd = new FormData();
		fd.left = new FormAttachment(label, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		wTextBox.setLayoutData(fd);

		// Track modifications
		wTextBox.addModifyListener(lsModified);

		// Make sure it responds to <enter>
		wTextBox.addSelectionListener(lsSelect);
		
		// Select all when text box receives focus
		wTextBox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				wTextBox.selectAll();
			}
		});

		textGroup[1] = wTextBox;
		return textGroup;
	}

	/**
	 * Called to add a text box to the parent composite. It will be hooked into the regular facilities of
	 * dialog control. It will be placed after the last line.
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	protected Text addTextBox(Composite parent, Control wLastLine, String name) {
		// Return just the text box
		return (Text) addTextBoxWithLabel(parent, wLastLine, name)[1];
	}

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
		label.setText(getString(name + ".Label")); //$NON-NLS-1$
		props.setLook(label);
		
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		fd.right = new FormAttachment(100, -margin);
		label.setLayoutData(fd);
		
		return label;
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
	public Button addButton(Composite parent, Control wLastLine, String name, int style) {
		// Create the button
		Button button = new Button(parent, style);
		if (name != null)
			button.setText(getString(name + ".Label")); //$NON-NLS-1$
		setLook(button);

		// Lay it out
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(wLastLine, margin);
		button.setLayoutData(fd);
		
		return button;
	}
	
	/**
	 * Called to create a button that contains data.
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @param style
	 * @return
	 */
	public Button addDataButton(Composite parent, Control wLastLine, String name, int style) {
	
		// Create the button
		Button button = addButton(parent, wLastLine, name, style);

		// Update dialog when selection changes
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				changed = true;
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				ok();
			}
		});
		
		return button;
	}
	
	
//	
//	/**
//	 * Called to create a single radio button
//	 * 
//	 * @param parent
//	 * @param wLastLine
//	 * @param name
//	 * @return
//	 */
//	public Button addRadioButton(Composite parent, Control wLastLine, String name) {
//		// Create the button
//		Button button = addDataButton(parent, wLastLine, name, SWT.RADIO);
//		
//		// Return
//		return button;
//	}



	/* MDSettings */
//	
//	/**
//	 * Called to create a single radio button
//	 * 
//	 * @param parent
//	 * @param wLastLine
//	 * @param name
//	 * @return
//	 */
//	public Button addRadioButton(Composite parent, Control wLastLine, String name) {
//		// Create the button
//		Button button = addDataButton(parent, wLastLine, name, SWT.RADIO);
//		
//		// Return
//		return button;
//	}



	/* MDGlobalAddress */
//	
//	/**
//	 * Called to create a single radio button
//	 * 
//	 * @param parent
//	 * @param wLastLine
//	 * @param name
//	 * @return
//	 */
//	public Button addRadioButton(Composite parent, Control wLastLine, String name) {
//		// Create the button
//		Button button = addDataButton(parent, wLastLine, name, SWT.RADIO);
//		
//		// Return
//		return button;
//	}

	/**
	 * Called to create a single regular button
	 * 
	 * @param parent
	 * @param wLastLine
	 * @param name
	 * @return
	 */
	public Button addPushButton(Composite parent, Control wLastLine, String name) {
	
		// Create the button
		Button button = addButton(parent, wLastLine, name, SWT.PUSH);
	
		// Handle CR
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				ok();
			}
		});
		
		// Return
		return button;
	}
	
	/**
	 * Called to add a combox that is populated with values from the input step
	 * 
	 * @param parent Composite parent for this control
	 * @param wLastLine	Control below which we should place this control
	 * @param name Text name to use in the control
	 * @param usageID ID to use when tracking usage of source fields
	 * @return
	 */
	public MDInputCombo addInputComboBox(Composite parent, Control wLastLine, String name, String usageID) {
		// Create a new combo box
		Control[] wControls = addComboBox(parent, wLastLine, name);
		
		// Return the new combo
		return new MDInputCombo(wControls, dialog.getSourceFields(), usageID);
	}
	
	/**
	 * Called to resolve an internationalization using a relative name
	 * 
	 * @param name
	 * @return
	 */
	protected String getString(String name, String... args) {
		return BaseMessages.getString(getPackage(), getStringPrefix() + "." + name, args);
	}

	/**
	 * Called to resolve internationalization using an absolute name
	 * 
	 * @param name
	 * @return
	 */
	protected String getAbsoluteString(String name, String... args) {
		return BaseMessages.getString(getPackage(), name, args);
	}
	
	/**
	 * Called to resolve stuff internal to the library
	 * 
	 * @param name
	 * @return
	 */
	protected String getLocalString(String name, String... args) {
		return BaseMessages.getString(MDAbstractDialog.class, name, args);
	}
	
	/**
	 * @return The internationalization package name
	 */
	protected abstract Class<?> getPackage();

	/**
	 * @return Return a prefix to use when resolving internationalizastions
	 */
	protected abstract String getStringPrefix();
	
	/**
	 * @return The shell for this dialog
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * @return The variable space for this dialog
	 */
	public VariableSpace getSpace() {
		return space;
	}

	/**
	 * @return The display for this dialog
	 */
	public Display getDisplay() {
		return display;
	}
}
