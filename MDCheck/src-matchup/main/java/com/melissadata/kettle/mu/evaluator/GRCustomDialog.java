package com.melissadata.kettle.mu.evaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;

import com.melissadata.cz.CZUtil;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;

public class GRCustomDialog extends MDAbstractDialog {

	private static final Class<?> PKG = MDCheckDialog.class;

	private static final String PRE_BUILT_MU_SURVIVOR_EXPRESSIOS_FILE = "md_pre_built_mu_survivor_expressions.xml";

	private File survivorExpressonFile;
	private Set<String> survivorExpressions;

	private Composite parentComp;

	private Group gExpression;
	private Group gPrecedence;
	private Group gElements;

	private Button rPreBuilt;
	private Button rSpecified;
	private Button bRemovePreBuilt;
	private Button bSavePreBuilt;
	private Button rHighestValue;
	private Button rLowestValue;

	private Button pTest;

	private CCombo ccPreBuilt;
	private Text txSpecified;

	private String[] colNames;
	private Map<String, String> strOperators;
	private Map<String, String> strFunctions;
	private Map<String, String> numOperators;
	private Map<String, String> numFunctions;
	private Map<String, String> boolOperators;
	private Map<String, String> boolFunctions;
	private Map<String, String> dateOperators;
	private Map<String, String> dateFunctions;
	private Map<String, String> micsOperators;
	private Map<String, String> micsFunctions;

	private Map<String, String> dataTypeCast;
	private Algorithm customAlg;
	private SurvivorField survivor;
	private boolean isSourceField;
	private boolean isPrioritizationField;
	
	@SuppressWarnings("unused")
	private RowMetaInterface rmi;
	
	private MDCheckDialog dialog1;

	public GRCustomDialog(MDCheckDialog dialog, MDCheckStepData mdStepData) {
		super(dialog, SWT.NONE);
		dialog1 = dialog;
	}

	@Override
	protected void createContents(Composite parent, Object data) {

		MDCheckStepData stepData = (MDCheckStepData) data;
		Image ime = new Image(this.getDisplay(), getClass().getResourceAsStream("/com/melissadata/kettle/images/PDI_MD_MatchUp_32.png"));
		
		this.getShell().setImage(ime);
		loadMaps();
		survivorExpressions = new HashSet<String>();
		if (stepData.getSourcePassThru().isSurvivorPass()) {
			int size = stepData.getSourcePassThru().getSurvivorPassThru().size();
			colNames = new String[size];

			for (int rr = 0; rr < size; rr++) {
				SurvivorField field = stepData.getSourcePassThru().getSurvivorPassThru().get(rr);
				colNames[rr] = field.getOutputName();
			}

		} else {
			colNames = stepData.getSourcePassThru().getPassThru().toArray(new String[stepData.getSourcePassThru().getPassThru().size()]);
		}
		
		parentComp = parent;

	}

	private void createGroups() {
		createExpressionGroup(parentComp, null);
		if (customAlg != null) {
			createPrecedenceGroup(parentComp, gExpression);
			createElementsGroup(parentComp, gPrecedence);
		} else {
			createElementsGroup(parentComp, gExpression);
		}

		this.getShell().layout(true, true);
		final Point newSize = this.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		this.getShell().setSize(newSize);
		Point pnt = getParent().toDisplay(getParent().getLocation().x, getParent().getLocation().y);
		pnt.x = pnt.x / 2;
		pnt.y = pnt.y / 2;
		this.getShell().setLocation(pnt);
	}

	private void createExpressionGroup(Composite parent, Control last) {

		gExpression = addGroup(parent, last, "ExpressionGroup");
		rPreBuilt = addButton(gExpression, null, "ExpressionGroup.PreBuiltExpression", SWT.RADIO);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(0, margin);
		// fd.right = new FormAttachment(20, -margin);
		rPreBuilt.setLayoutData(fd);

		rPreBuilt.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			public void widgetSelected(SelectionEvent arg0) {
				enable();
				changed = true;
			}

		});

		ccPreBuilt = new CCombo(gExpression, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(rPreBuilt, margin);
		fd.top = new FormAttachment(0, margin);
		fd.right = new FormAttachment(80, -margin);
		ccPreBuilt.setLayoutData(fd);
		setLook(ccPreBuilt);
		ccPreBuilt.setEditable(false);

		ccPreBuilt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				changed = true;
			}
		});

		bRemovePreBuilt = addButton(gExpression, null, "ExpressionGroup.RemoveExpression", SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(ccPreBuilt, margin);
		fd.top = new FormAttachment(null, margin);
		// fd.right = new FormAttachment(100, -margin);
		bRemovePreBuilt.setLayoutData(fd);

		bRemovePreBuilt.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			public void widgetSelected(SelectionEvent arg0) {
				survivorExpressions.remove(ccPreBuilt.getText());
				saveExpressions();
				loadPreBuiltExpression();
			}
		});

		rSpecified = addButton(gExpression, rPreBuilt, "ExpressionGroup.SpecifiedExpression", SWT.RADIO);
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(rPreBuilt, margin * 5);
		// fd.right = new FormAttachment(90, -margin);
		rSpecified.setLayoutData(fd);

		rSpecified.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			public void widgetSelected(SelectionEvent arg0) {
				enable();
				changed = true;
			}
		});

		txSpecified = new Text(gExpression, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(rSpecified, margin);
		fd.right = new FormAttachment(100, -margin);
		txSpecified.setLayoutData(fd);
		setLook(txSpecified);
		txSpecified.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				changed = true;
			}
		});

		pTest = addPushButton(gExpression, txSpecified, "ExpressionGroup.TestButton");
		pTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(rPreBuilt.getSelection()){
					testExpression(ccPreBuilt.getText());
				} else {

					testExpression(txSpecified.getText());
				}
			}
		});
		setLook(pTest);

		bSavePreBuilt = addButton(gExpression, null, "ExpressionGroup.SaveExpression", SWT.PUSH);
		fd = new FormData();
		// fd.left = new FormAttachment(ccPreBuilt, margin);
		fd.top = new FormAttachment(txSpecified, margin);
		fd.right = new FormAttachment(100, -margin);
		bSavePreBuilt.setLayoutData(fd);

		bSavePreBuilt.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			public void widgetSelected(SelectionEvent arg0) {

				survivorExpressions.add(txSpecified.getText());
				saveExpressions();
				loadPreBuiltExpression();

				ccPreBuilt.setText(txSpecified.getText());
				txSpecified.setText("");
				rPreBuilt.setSelection(true);
				rSpecified.setSelection(false);
				enable();

			}

		});

	}

	private void createPrecedenceGroup(Composite parent, Control last) {
		gPrecedence = addGroup(parent, last, "PrecedenceGroup");
		rHighestValue = addButton(gPrecedence, null, "PrecedenceGroup.HighestVal", SWT.RADIO);

		rHighestValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changed = true;
			}
		});

		rLowestValue = addButton(gPrecedence, rHighestValue, "PrecedenceGroup.LowestVal", SWT.RADIO);
		rLowestValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changed = true;
			}
		});
	}

	private void createElementsGroup(Composite parent, Control last) {
		gElements = addGroup(parent, last, "ElementsGroup");
		String[] arTest = new String[] { "Item1", "Item2", "Item3" };

		Control top;
		top = createTree(gElements, null, null, "Column Names:", colNames);
		arTest = dialog.getSpace().listVariables();
		last = createTree(gElements, null, top, "Variables:", arTest);
		last = createOprFuncTree(gElements, null, last, "String Functions/Operators:", strOperators.keySet().toArray(new String[strOperators.size()]),
				strFunctions.keySet().toArray(new String[strFunctions.size()]));
		last = createOprFuncTree(gElements, null, last, "Numeric Functions/Operators:", numOperators.keySet().toArray(new String[numOperators.size()]),
				numFunctions.keySet().toArray(new String[numFunctions.size()]));

		last = createOprFuncTree(gElements, top, null, "Boolean Functions/Operators:", boolOperators.keySet().toArray(new String[boolOperators.size()]),
				boolFunctions.keySet().toArray(new String[boolFunctions.size()]));
		last = createOprFuncTree(gElements, top, last, "Date/Time Functions/Operators:", dateOperators.keySet().toArray(new String[dateOperators.size()]),
				dateFunctions.keySet().toArray(new String[dateFunctions.size()]));
		last = createOprFuncTree(gElements, top, last, "Misc. Functions/Operators:", micsOperators.keySet().toArray(new String[micsOperators.size()]),
				micsFunctions.keySet().toArray(new String[micsFunctions.size()]));
		last = createTree(gElements, top, last, "Data Type Casts:", dataTypeCast.keySet().toArray(new String[dataTypeCast.size()]));

	}

	private Tree createTree(Composite parent, Control last, Control left, String tableName, String[] items) {

		final Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT | SWT.H_SCROLL);
		column1.setText(tableName);
		column1.setWidth(270);
		column1.setResizable(false);

		addToolTip(tree);

		for (String itName : items) {
			TreeItem item = new TreeItem(tree, SWT.NONE);

			item.setText(itName);
		}

		FormData fd = new FormData();
		fd.height = 200;
		fd.left = new FormAttachment(left, margin * 2);
		fd.top = new FormAttachment(last, margin);
		// fd.bottom = new FormAttachment(100, margin);
		tree.setLayoutData(fd);

		tree.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent arg0) {
				updateSpecified(tree);
			}

			public void mouseDown(MouseEvent arg0) {
				// Do nothing
			}

			public void mouseUp(MouseEvent arg0) {
				// Do nothing
			}

		});

		tree.addTraverseListener(new TraverseListener() {

			public void keyTraversed(TraverseEvent event) {

				if (event.detail == SWT.TRAVERSE_RETURN) {
					updateSpecified(tree);
				}
			}
		});

		setLook(tree);

		return tree;
	}

	private Tree createOprFuncTree(Composite parent, Control last, Control left, String tableName, String[] operators, String[] functions) {
		final Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT | SWT.H_SCROLL);
		column1.setText(tableName);
		column1.setWidth(270);
		column1.setResizable(false);

		addToolTip(tree);

		TreeItem opItem = new TreeItem(tree, SWT.NONE);
		opItem.setText(new String[] { "Operator " });
		opItem.setExpanded(true);

		for (String opName : operators) {
			TreeItem opSubItem = new TreeItem(opItem, SWT.None);
			opSubItem.setText(opName);
		}

		TreeItem fncItem = new TreeItem(tree, SWT.NONE);
		fncItem.setText(new String[] { "Function " });
		fncItem.setExpanded(true);

		for (String fncName : functions) {
			TreeItem fncSubItem = new TreeItem(fncItem, SWT.None);
			fncSubItem.setText(fncName);
		}

		FormData fd = new FormData();
		fd.height = 200;
		fd.left = new FormAttachment(left, margin * 2);
		fd.top = new FormAttachment(last, margin);
		// fd.bottom = new FormAttachment(100, margin);
		tree.setLayoutData(fd);

		fncItem.setExpanded(true);
		opItem.setExpanded(true);

		tree.setToolTipText("");
		tree.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent arg0) {
				if (tree.getSelection()[0].getParentItem() == null)
					return;

				updateSpecified(tree);
			}

			public void mouseDown(MouseEvent arg0) {
				// Do nothing
			}

			public void mouseUp(MouseEvent arg0) {
				// Do nothing
			}

		});

		tree.addTraverseListener(new TraverseListener() {

			public void keyTraversed(TraverseEvent event) {

				if (event.detail == SWT.TRAVERSE_RETURN) {
					updateSpecified(tree);
				}
			}
		});

		setLook(tree);
		return tree;
	}

	private String getPart2(String sel, String type, String assn) {

		if (type == null) {
			if (assn.equals("Colu")){
				
				return sel;
			}
			if (assn.equals("Data"))
				return dataTypeCast.get(sel);
			if (assn.equals("Vari"))
				return sel;

		}

		if (type.contains("Operator")) {

			if (assn.equals("Stri"))
				return strOperators.get(sel);
			if (assn.equals("Nume"))
				return numOperators.get(sel);
			if (assn.equals("Bool"))
				return boolOperators.get(sel);
			if (assn.equals("Date"))
				return dateOperators.get(sel);
			if (assn.equals("Misc"))
				return micsOperators.get(sel);

		} else if (type.contains("Function")) {

			if (assn.equals("Stri"))
				return strFunctions.get(sel);
			if (assn.equals("Nume"))
				return numFunctions.get(sel);
			if (assn.equals("Bool"))
				return boolFunctions.get(sel);
			if (assn.equals("Date"))
				return dateFunctions.get(sel);
			if (assn.equals("Misc"))
				return micsFunctions.get(sel);
		}

		return "";
	}

	private void updateSpecified(Tree tree) {

		int pos = txSpecified.getCaretPosition();
		String fullLine = txSpecified.getText();
		String selected = txSpecified.getSelectionText();
		if (selected.isEmpty()) {

			if (fullLine.substring(pos).startsWith("<string>")) {
				selected = "<string>";
			}

			if (fullLine.substring(pos).startsWith("<number>")) {
				selected = "<number>";
			}
		}

		String part1 = fullLine.substring(0, pos);
		String part2;
		if (tree.getSelection()[0].getParentItem() != null)
			part2 = getPart2(tree.getSelection()[0].getText(), tree.getSelection()[0].getParentItem().getText(), tree.getColumn(0).getText().substring(0, 4));
		else
			part2 = getPart2(tree.getSelection()[0].getText(), null, tree.getColumn(0).getText().substring(0, 4));

		String part3 = txSpecified.getText().substring(pos);

		String combinedParts = "";
		if (!selected.isEmpty()) {
			combinedParts = fullLine.replace(selected, part2);
		} else {
			combinedParts = part1 + part2 + part3;
		}

		txSpecified.setText(combinedParts);

		int endPos = 0;
		pos = combinedParts.length();

		if (combinedParts.contains("<string")) {
			pos = combinedParts.indexOf("<string");
		}
		if (combinedParts.contains("<number")) {
			pos = pos < combinedParts.indexOf("<number") ? pos : combinedParts.indexOf("<number");
		}
		if (combinedParts.contains("<expression")) {
			pos = pos < combinedParts.indexOf("<expression") ? pos : combinedParts.indexOf("<expression");
		}
		if (combinedParts.contains("<datetime")) {
			pos = pos < combinedParts.indexOf("<datetime") ? pos : combinedParts.indexOf("<datetime");
		}
		if (combinedParts.contains("<condition")) {
			pos = pos < combinedParts.indexOf("<condition") ? pos : combinedParts.indexOf("<condition");
		}

		if (pos != combinedParts.length()) {
			endPos = combinedParts.indexOf(">", pos) + 1;
		}

		txSpecified.setFocus();

		if (endPos > 1)
			txSpecified.setSelection(pos, endPos);
		else
			txSpecified.setSelection(txSpecified.getText().length());

	}

	private void addToolTip(final Tree tree) {
		// Implement a "fake" tooltip
		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event();
					e.item = (TreeItem) label.getData("_TABLEITEM");
					// Assuming table is single select, set the selection as if
					// the mouse down event went through to the table
					tree.setSelection(new TreeItem[] { (TreeItem) e.item });
					tree.notifyListeners(SWT.Selection, e);
					shell.dispose();
					tree.setFocus();
					break;
				case SWT.MouseExit:
					shell.dispose();
					break;
				}
			}
		};

		Listener treeListener = new Listener() {
			Shell tip = null;
			Label label = null;
			String tipText = "none";

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: {
					if (tip == null)
						break;
					tip.dispose();
					tip = null;
					label = null;
					break;
				}
				case SWT.MouseHover: {
					TreeItem item = tree.getItem(new Point(event.x, event.y));
					if (item != null) {
						if (tip != null && !tip.isDisposed())
							tip.dispose();
						tip = new Shell(dialog.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
						tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						FillLayout layout = new FillLayout();
						layout.marginWidth = 2;
						tip.setLayout(layout);
						label = new Label(tip, SWT.NONE);
						label.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
						label.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						label.setData("_TABLEITEM", item);

						tipText = getToolTipText(tree.getColumn(0).getText(), item.getText());
						label.setText(tipText);
						label.addListener(SWT.MouseExit, labelListener);
						label.addListener(SWT.MouseDown, labelListener);
						Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						Rectangle rect = item.getBounds(0);
						Point pt = tree.toDisplay(rect.x, rect.y);
						int lp = item.getText().length() * 5;
						tip.setBounds(pt.x + lp, pt.y - 15, size.x, size.y);
						if (!tipText.equals("none"))
							tip.setVisible(true);
						else
							tip.setVisible(false);

					}
				}
				}
			}
		};

		tree.addListener(SWT.Dispose, treeListener);
		tree.addListener(SWT.KeyDown, treeListener);
		tree.addListener(SWT.MouseMove, treeListener);
		tree.addListener(SWT.MouseHover, treeListener);
	}

	private String getToolTipText(String colName, String itemName) {

		String tipText = "Tip not set";

		if (colName.startsWith("Column")) {
			tipText = "\"" + itemName + "\" " + getDataType(itemName) ;
		} else if (colName.startsWith("Variable")) {
			tipText = "\"" + itemName + "\" value of var as string.";
		} else if (colName.startsWith("String")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function"))
				tipText = "none";
			else
				tipText = getString("String." + cleanTipItem(itemName));

		} else if (colName.startsWith("Numeric")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function"))
				tipText = "none";
			else
				tipText = getString("Numeric." + cleanTipItem(itemName));

		} else if (colName.startsWith("Boolean")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function"))
				tipText = "none";
			else
				tipText = getString("Boolean." + cleanTipItem(itemName));

		} else if (colName.startsWith("Date/Time")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function"))
				tipText = "none";
			else
				tipText = getString("DateTime." + cleanTipItem(itemName));

		} else if (colName.startsWith("Misc")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function"))
				tipText = "none";
			else
				tipText = getString("Misc." + cleanTipItem(itemName));

		} else if (colName.startsWith("Data")) {
			tipText = "\"" + itemName + "\" Type Cast";
		}

		return tipText;
	}
	
	private String getDataType(String colname){
		return null;
	}

	private String cleanTipItem(String orig) {
		String retVal = orig.substring(0, orig.indexOf("("));

		return retVal.replace(" ", "");
	}

	private void testExpression(String expression) {
		String result = "The expression looks good.";
		Evaluator mueval;
		MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION);
		
		try {
			
			mueval = new Evaluator(dialog1.getRowMetaInterface(), dialog.getSpace());
			mueval.setExpression(expression);
			mueval.parseExpression();
			mueval.testExpression();
		} catch (EvaluatorException e) {
			result = "Error in expression: " + e.getLocalizedMessage().trim();
			box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_ERROR);

		}
		box.setText("Evaluation");
		box.setMessage(result);

		if (box.open() == SWT.OK) {
			// do nothing
		}
	}

	@Override
	protected boolean getData(Object arg0) throws KettleException {
		if (customAlg != null) {
			if (rPreBuilt.getSelection())
				customAlg.setExpression(ccPreBuilt.getText());
			else
				customAlg.setExpression(txSpecified.getText());

			if (rHighestValue.getSelection()) {
				customAlg.setOption("Highest");
			} else {
				customAlg.setOption("Lowest");
			}
		}

		if (survivor != null) {

			if (isSourceField) {
				if (rPreBuilt.getSelection())
					survivor.setSource(ccPreBuilt.getText());
				else
					survivor.setSource(txSpecified.getText());

			} else if (isPrioritizationField) {
				if (rPreBuilt.getSelection())
					survivor.setPrioritization(ccPreBuilt.getText());
				else{
					if(!Const.isEmpty(txSpecified.getText()))
						survivor.setPrioritization(txSpecified.getText());
					else
						survivor.setPrioritization(survivor.defaultPriortization());
				}
			}

		}

		return true;
	}
	
	public void setRowMeta(RowMetaInterface row){
		this.rmi = row;
	}

	@Override
	protected String getHelpURLKey() {
		return null;
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.GRCustomDialog";
	}

	@Override
	protected void init(Object data) {
		try {
			survivorExpressonFile = new File(CZUtil.getCZWorkDirectory(), PRE_BUILT_MU_SURVIVOR_EXPRESSIOS_FILE);
		} catch (IOException e) {
			dialog.getLog().logError("Unable to load pre built expressios: " + survivorExpressonFile);
			e.printStackTrace();
		}
	}

	private void enable() {
		if (rPreBuilt.getSelection()) {
			bSavePreBuilt.setEnabled(false);
			txSpecified.setEnabled(false);
			bRemovePreBuilt.setEnabled(true);
			ccPreBuilt.setEnabled(true);

		} else {
			bSavePreBuilt.setEnabled(true);
			txSpecified.setEnabled(true);
			bRemovePreBuilt.setEnabled(false);
			ccPreBuilt.setEnabled(false);
		}
	}

	public void setAlgorythm(Algorithm algo) {
		customAlg = algo;

		createGroups();

		if (customAlg.getOption().equals("Highest")) {
			rHighestValue.setSelection(true);
			rLowestValue.setSelection(false);
		} else {
			rHighestValue.setSelection(false);
			rLowestValue.setSelection(true);
		}

		loadPreBuiltExpression();

		if (survivorExpressions.contains(customAlg.getExpression())) {
			ccPreBuilt.setText(customAlg.getExpression());
			rPreBuilt.setSelection(true);
			rSpecified.setSelection(false);

		} else {
			txSpecified.setText(customAlg.getExpression());
			rPreBuilt.setSelection(false);
			rSpecified.setSelection(true);
		}

		enable();
		changed = false;
	}

	public void setSurvivor(SurvivorField survivor, boolean bSource, boolean bPriortization) {
		this.survivor = survivor;
		this.isSourceField = bSource;
		this.isPrioritizationField = bPriortization;

		createGroups();

		loadPreBuiltExpression();

		if (isSourceField) {

			if (survivorExpressions.contains(survivor.getSource())) {
				ccPreBuilt.setText(survivor.getSource());
				rPreBuilt.setSelection(true);
				rSpecified.setSelection(false);

			} else {

				txSpecified.setText(survivor.getSource());
				rPreBuilt.setSelection(false);
				rSpecified.setSelection(true);
			}

		} else if (isPrioritizationField) {

			if (survivorExpressions.contains(survivor.getPrioritization())) {
				ccPreBuilt.setText(survivor.getPrioritization());
				rPreBuilt.setSelection(true);
				rSpecified.setSelection(false);

			} else {

				if(survivor.getPrioritization().equals(survivor.defaultPriortization()))
					txSpecified.setText("");
				else
					txSpecified.setText(survivor.getPrioritization());
				rPreBuilt.setSelection(false);
				rSpecified.setSelection(true);
			}
		}

		enable();
		changed = false;

	}

	private void loadPreBuiltExpression() {

		try {

			if (survivorExpressonFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(survivorExpressonFile));
				String line;
				while ((line = br.readLine()) != null) {
					survivorExpressions.add(line);
				}
				br.close();
			} else {

			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		ccPreBuilt.removeAll();
		if (survivorExpressions != null) {
			for (String rule : survivorExpressions) {
				ccPreBuilt.add(rule);
			}
		}

	}

	private void saveExpressions() {
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(survivorExpressonFile));

			for (String line : survivorExpressions) {
				br.write(line);
				br.newLine();

			}
			br.close();

		} catch (IOException e) {
			dialog.getLog().logError("Unable to save prebuilt expression file: "  + survivorExpressonFile);
			e.printStackTrace();
		}
	}

	private void loadMaps() {
		strOperators = new HashMap<String, String>();
		strOperators.put("Concatenate (+)", "+");
		strOperators.put("Equal (==)", "==");
		strOperators.put("Not Equal (!=)", "!=");
		strOperators.put("Greater (>)", ">");
		strOperators.put("Greater or Equal (>=)", ">=");
		strOperators.put("Less (<)", "<");
		strOperators.put("Less or Equal (<=)", "<=");

		strFunctions = new HashMap<String, String>();
		strFunctions.put("FindString()", "FindString(<string1>,<string2>,<number>)");
		strFunctions.put("Left()", "Left(<string>,<number>)");
		strFunctions.put("Len()", "Len(<string>)");
		strFunctions.put("Lower()", "Lower(<string>)");
		strFunctions.put("LTrim()", "LTrim(<string>)");
		strFunctions.put("PadLeft()", "PadLeft(<string>,<number>)");
		strFunctions.put("PadRight()", "PadRight(<string>,<number>)");
		strFunctions.put("Replace()", "Replace(<string1>,<string2>,<string3>)");
		strFunctions.put("Replicate()", "Replicate(<string>,<number>)");
		strFunctions.put("Reverse()", "Reverse(<string>)");
		strFunctions.put("Right()", "Right(<string>,<number>)");
		strFunctions.put("RTrim()", "RTrim(<string>)");
		strFunctions.put("SubString()", "SubString(<string>,<number1>,<number2>)");
		strFunctions.put("Trim()", "Trim(<string>)");
		strFunctions.put("Upper()", "Upper(<string>)");

		// Number
		numOperators = new HashMap<String, String>();
		numOperators.put("Add (+)", "+");
		numOperators.put("Subtract (-)", "-");
		numOperators.put("Multiply (*)", "*");
		numOperators.put("Divide (/)", "/");
		numOperators.put("Modulus (%)", "%");
		numOperators.put("Equal (==)", "==");
		numOperators.put("Not Equal (!=)", "!=");
		numOperators.put("Greater (>)", ">");
		numOperators.put("Greater or Equal (>=)", ">=");
		numOperators.put("Less (<)", "<");
		numOperators.put("Less or Equal (<=)", "<=");

		numFunctions = new HashMap<String, String>();
		numFunctions.put("Abs()", "Abs(<number>)");
		numFunctions.put("Exp()", "Exp(<number>)");
		numFunctions.put("Ceiling()", "Ceiling(<number>)");
		numFunctions.put("Floor()", "Floor(<number>)");
		numFunctions.put("Ln()", "Ln(<number>)");
		numFunctions.put("Log()", "Log(<number>)");
		numFunctions.put("Power()", "Power(<number>)");
		numFunctions.put("Round()", "Round(<number1>,<number2>)");
		numFunctions.put("Sign()", "Sign(<number>)");
		numFunctions.put("Sqrt()", "Sqrt(<number>)");
		numFunctions.put("Square()", "Square(<number>)");

		// Boolean
		boolOperators = new HashMap<String, String>();
		boolOperators.put("Logical AND(&&)", "&&");
		boolOperators.put("Logical OR(||)", "||");
		boolOperators.put("Logical NOT(!)", "!");
		boolOperators.put("Equal (==)", "==");
		boolOperators.put("Not Equal (!=)", "!=");
		boolOperators.put("Conditional (?:)", "<condition> ? <expression1> : <expression2>");

		boolFunctions = new HashMap<String, String>();
		boolFunctions.put("IsNull()", "IsNull(<expression>)");

		// Date Time
		dateOperators = new HashMap<String, String>();
		dateOperators.put("Equal (==)", "==");
		dateOperators.put("Not Equal (!=)", "!=");
		dateOperators.put("Greater (>)", ">");
		dateOperators.put("Greater or Equal (>=)", ">=");
		dateOperators.put("Less (<)", "<");
		dateOperators.put("Less or Equal (<=)", "<=");

		dateFunctions = new HashMap<String, String>();
		dateFunctions.put("DateAdd()", "DateAdd(<string>,<number>,<datetime>)");
		dateFunctions.put("DateDiff()", "DateDiff(<string>,<datetime1>,<datetime2>)");
		dateFunctions.put("DatePart()", "DatePart(<string>,<datetime>)");
		dateFunctions.put("Day()", "Day(<datetime>)");
		dateFunctions.put("getDate()", "getDate()");
		dateFunctions.put("getUTCDate()", "getUTCDate()");
		dateFunctions.put("Month()", "Month(<datetime>)");
		dateFunctions.put("Year()", "Year(<datetime>)");

		// Mics
		micsOperators = new HashMap<String, String>();
		micsOperators.put("Conditional (?:)", "<condition> ? <expression1> : <expression2>");

		micsFunctions = new HashMap<String, String>();
		micsFunctions.put("AddressScore()", "AddressScore(<expression>)");
		micsFunctions.put("DataQualityScore()", "DataQualityScore(<expression>)");
		micsFunctions.put("EmailScore()", "EmailScore(<expression>)");
		micsFunctions.put("GeoCodeScore()", "GeoCodeScore(<expression>)");
		micsFunctions.put("IsNull()", "IsNull(<expression>)");
		micsFunctions.put("NameScore()", "NameScore(<expression>)");
		micsFunctions.put("Null()", "Null(<expression>)");
		micsFunctions.put("PhoneScore()", "PhoneScore(<expression>)");

		// Casts
		dataTypeCast = new HashMap<String, String>();
		dataTypeCast.put("(DT_BOOL)", "(DT_BOOL)<expression>");
		dataTypeCast.put("(DT_TIMESTAMP)", "(DT_TIMESTAMP)<expression>");
		dataTypeCast.put("(DT_DATE)", "(DT_DATE,\"MMDDYYYY\")<expression>");
		dataTypeCast.put("(DT_DOUBLE)", "(DT_DOUBLE)<expression>");
		dataTypeCast.put("(DT_INT)", "(DT_INT)<expression>");
		dataTypeCast.put("(DT_BIGNUMBER)", "(DT_BIGNUMBER)<expression>");
		dataTypeCast.put("(DT_NULL)", "(DT_NULL)<expression>");
		dataTypeCast.put("(DT_STRING)", "(DT_STRING)<expression>");
		dataTypeCast.put("(DT_INETADDRESS)", "(DT_INETADDRESS)<expression>");
		//dataTypeCast.put("(DT_BINARY)", "(DT_BINARY)<expression>");
	}
}
