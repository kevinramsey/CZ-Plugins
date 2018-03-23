package com.melissadata.kettle.cleanser.ui;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.CZUtil;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.cleanser.MDCleanserData;
import com.melissadata.kettle.cleanser.MDCleanserDialog;


public class RegExBuilderDialog extends MDAbstractDialog {
	private static final Class<?>	PKG								= RegExBuilderDialog.class;
	private static final String		PRE_BUILT_CLEANSER_REGEX_FILE	= "md_pre_built_cleanser_regex_expressions.xml";
	private File					preBuiltExpressonFile;
	private Set<String>				preBuiltExpressions;
	private Composite				parentComp;
	private Group					gExpression;
	private Group					gTest;
	private Group					gElements;
	private Button					rPreBuilt;
	private Button					rSpecified;
	private Button					bRemovePreBuilt;
	private Button					bSavePreBuilt;
	private boolean 				hasReplace;
	private CCombo					ccPreBuilt;
	private Text					txSpecified;
	private Text					txReplace;
	private Label					lReplace;
	private Text					txSampleIn;
	private Text					txSampleOut;
	private Label					lSampleOut;
	private Text					txFind;
	private Button					pTest;
	private String					expressionString;
	private String					replaceString;
	private String[]				expValues;
	private Map<String, String>		mCharEscapes;
	private Map<String, String>		mCharClasses;
	private Map<String, String>		mBoundryConditions;
	private Map<String, String>		mQuantifiers;
	private Map<String, String>		mGrouping;
	private Map<String, String>		mSubstitutions;
	private Map<String, String>		mCoditionals;
	private Map<String, String>		mOptions;

	public RegExBuilderDialog(MDCleanserDialog dialog, MDCleanserData mdStepData, boolean hasReplace) {
		super(dialog, SWT.NONE);
		this.hasReplace = hasReplace;
	}

	private void addToolTip(final Tree tree) {
		// Implement a "fake" tooltip
		final Listener labelListener = new Listener() {
			@Override
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
			Shell	tip		= null;
			Label	label	= null;
			String	tipText	= "none";

			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
					case SWT.Dispose:
					case SWT.KeyDown:
					case SWT.MouseMove: {
						if (tip == null) {
							break;
						}
						tip.dispose();
						tip = null;
						label = null;
						break;
					}
					case SWT.MouseHover: {
						TreeItem item = tree.getItem(new Point(event.x, event.y));
						if (item != null) {
							if ((tip != null) && !tip.isDisposed()) {
								tip.dispose();
							}
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
							if (!tipText.equals("none")) {
								tip.setVisible(true);
							} else {
								tip.setVisible(false);
							}
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

	private String cleanTipItem(String orig) {
		String retVal = orig.substring(0, orig.indexOf("("));
		return retVal.replace(" ", "");
	}

	@Override
	protected void createContents(Composite parent, Object data) {
//		Image ime = new Image(getDisplay(), getClass().getResourceAsStream("/com/melissadata/kettle/profiler/images/PDI_MD_Profiler_32.png"));
//		getShell().setImage(ime);
		loadMaps();
		preBuiltExpressions = new HashSet<String>();
		parentComp = parent;
		createGroups();
	}

	// TODO put strings in messages
	private void createElementsGroup(Composite parent, Control last) {
		gElements = addGroup(parent, last, "ElementsGroup");
		Control top;
		top = createTree(gElements, null, null, "Character Escapes:", mCharEscapes.keySet().toArray(new String[mCharEscapes.size()]));
		last = createTree(gElements, null, top, "Character Classes:", mCharClasses.keySet().toArray(new String[mCharClasses.size()]));
		last = createTree(gElements, null, last, "Boundry Conditions:", mBoundryConditions.keySet().toArray(new String[mBoundryConditions.size()]));
		last = createTree(gElements, null, last, "Quantifiers:", mQuantifiers.keySet().toArray(new String[mQuantifiers.size()]));
		last = createTree(gElements, top, null, "Grouping:", mGrouping.keySet().toArray(new String[mGrouping.size()]));
		last = createTree(gElements, top, last, "Substitutions:", mSubstitutions.keySet().toArray(new String[mSubstitutions.size()]));
		last = createTree(gElements, top, last, "Conditionals:", mCoditionals.keySet().toArray(new String[mCoditionals.size()]));
		last = createTree(gElements, top, last, "Options:", mOptions.keySet().toArray(new String[mOptions.size()]));
	}

	private void createExpressionGroup(Composite parent, Control last) {
		gExpression = addGroup(parent, last, "ExpressionGroup");
		rPreBuilt = addButton(gExpression, null, "ExpressionGroup.PreBuiltExpression", SWT.RADIO);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(0, margin);
		rPreBuilt.setLayoutData(fd);
		rPreBuilt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
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
			@Override
			public void modifyText(ModifyEvent arg0) {
				changed = true;
			}
		});
		bRemovePreBuilt = addButton(gExpression, null, "ExpressionGroup.RemoveExpression", SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(ccPreBuilt, margin);
		fd.top = new FormAttachment(null, margin);
		bRemovePreBuilt.setLayoutData(fd);
		bRemovePreBuilt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				preBuiltExpressions.remove(ccPreBuilt.getText());
				saveExpressions();
				loadPreBuiltExpression();
			}
		});
		rSpecified = addButton(gExpression, rPreBuilt, "ExpressionGroup.SpecifiedExpression", SWT.RADIO);
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(rPreBuilt, margin * 5);
		rSpecified.setLayoutData(fd);
		rSpecified.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				enable();
				changed = true;
			}
		});
		middlePct = 15;
		Control[] SearchCotrols = addTextBoxWithLabel(gExpression, rSpecified, "ExpressionGroup.SpecifiedExpression.Search");
		txSpecified = (Text) SearchCotrols[1];
		txSpecified.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				expressionString = txSpecified.getText();
			}
		});
		Control[] replaceCotrols = addTextBoxWithLabel(gExpression, txSpecified, "ExpressionGroup.SpecifiedExpression.Replace");
		lReplace = (Label) replaceCotrols[0];
		txReplace = (Text) replaceCotrols[1];
		txReplace.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				replaceString = txReplace.getText();
			}
		});
		middlePct = 33;
		bSavePreBuilt = addButton(gExpression, null, "ExpressionGroup.SaveExpression", SWT.PUSH);
		fd = new FormData();
		fd.top = new FormAttachment(txReplace, margin);
		fd.right = new FormAttachment(100, -margin);
		bSavePreBuilt.setLayoutData(fd);
		bSavePreBuilt.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				preBuiltExpressions.add(txSpecified.getText());
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

	private void createGroups() {
		createExpressionGroup(parentComp, null);
		createElementsGroup(parentComp, gExpression);
		createTestGroup(parentComp, gElements);
		getShell().layout(true, true);
		final Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		getShell().setSize(newSize);
		Point pnt = getParent().toDisplay(getParent().getLocation().x, getParent().getLocation().y);
		pnt.x = pnt.x / 2;
		pnt.y = pnt.y / 2;
		getShell().setLocation(pnt);
	}

	private void createTestGroup(Composite parent, Control last) {
		gTest = addGroup(parent, last, "TestGroup");
		middlePct = 20;
		Control[] inControls = addTextBoxWithLabel(gTest, null, "TestGroup.SampleIn");
		txSampleIn = (Text) inControls[1];
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, margin);
		fd.left = new FormAttachment(inControls[0], margin);
		fd.right = new FormAttachment(90, -margin);
		txSampleIn.setLayoutData(fd);
		setLook(txSampleIn);
		Control[] outControls = addTextBoxWithLabel(gTest, txSampleIn, "TestGroup.SampleOut");
		lSampleOut = (Label) outControls[0];
		txSampleOut = (Text) outControls[1];
		fd = new FormData();
		fd.top = new FormAttachment(txSampleIn, margin);
		fd.left = new FormAttachment(outControls[0], margin);
		fd.right = new FormAttachment(90, -margin);
		txSampleOut.setLayoutData(fd);
		
		Control[] foundControls = addTextBoxWithLabel(gTest, txSampleOut, "TestGroup.Find");
		txFind = (Text) foundControls[1];
		fd = new FormData();
		fd.top = new FormAttachment(txSampleOut, margin);
		fd.left = new FormAttachment(foundControls[0], margin);
		fd.right = new FormAttachment(40, -margin);
		txFind.setLayoutData(fd);
		
		
		
		
		
		middlePct = 33;
		pTest = addPushButton(gTest, txSampleOut, "TestGroup.Test");
		fd = new FormData();
		fd = new FormData();
		fd.top = new FormAttachment(0, margin);
		fd.left = new FormAttachment(txSampleIn, margin);
		fd.right = new FormAttachment(100, -margin);
		pTest.setLayoutData(fd);
		pTest.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				testExpression();
			}
		});
	}

	private Tree createTree(Composite parent, Control last, Control left, String tableName, String[] items) {
		final Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT | SWT.H_SCROLL);
		column1.setText(tableName);
		column1.setWidth(270);
		column1.setResizable(false);
		addToolTip(tree);
		if (items != null) {
			for (String itName : items) {
				TreeItem item = new TreeItem(tree, SWT.NONE);
				item.setText(itName);
			}
		}
		FormData fd = new FormData();
		fd.height = 150;
		fd.left = new FormAttachment(left, margin * 2);
		fd.top = new FormAttachment(last, margin);
		tree.setLayoutData(fd);
		tree.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				updateSpecified(tree);
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// Do nothing
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
				// Do nothing
			}
		});
		tree.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					updateSpecified(tree);
				}
			}
		});
		setLook(tree);
		return tree;
	}

	private void enable() {
		
		if(hasReplace){
			txReplace.setVisible(true);
			txSampleOut.setVisible(true);
			lReplace.setVisible(true);
			lSampleOut.setVisible(true);
		} else {
			txReplace.setVisible(false);
			txSampleOut.setVisible(false);
			lReplace.setVisible(false);
			lSampleOut.setVisible(false);
		}
	}

	@Override
	protected boolean getData(Object arg0) throws KettleException {
		if (rSpecified.getSelection()) {
				expValues[0] = txSpecified.getText();
				expValues[1] = txReplace.getText();
		}
		if (rPreBuilt.getSelection()) {
			expValues[0] = ccPreBuilt.getText();
			expValues[1] = txReplace.getText();
		}
		return true;
	}

	private String getDataType(String colname) {
		return null;
	}

	@Override
	protected String getHelpURLKey() {
		return null;
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	private String getPart2(String sel, String assn) {
		if (assn.contains("Character Escapes"))
			return mCharEscapes.get(sel);
		if (assn.contains("Character Classes"))
			return mCharClasses.get(sel);
		if (assn.contains("Boundry Conditions"))
			return mBoundryConditions.get(sel);
		if (assn.contains("Quantifiers"))
			return mQuantifiers.get(sel);
		if (assn.contains("Grouping"))
			return mGrouping.get(sel);
		if (assn.contains("Substitutions"))
			return mSubstitutions.get(sel);
		if (assn.contains("Conditionals"))
			return mCoditionals.get(sel);
		if (assn.contains("Options"))
			return mOptions.get(sel);
		return "not set";
	}

	@Override
	protected String getStringPrefix() {
		return "MDCleanserDialog.RegExDialog";
	}

	private String getToolTipText(String colName, String itemName) {
		String tipText = "Tip not set for " + colName + ":" + itemName;
		if (colName.startsWith("Column")) {
			tipText = "\"" + itemName + "\" " + getDataType(itemName);
		} else if (colName.startsWith("Variable")) {
			tipText = "\"" + itemName + "\" value of var as string.";
		} else if (colName.startsWith("String")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function")) {
				tipText = "none";
			} else {
				tipText = getString("String." + cleanTipItem(itemName));
			}
		} else if (colName.startsWith("Numeric")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function")) {
				tipText = "none";
			} else {
				tipText = getString("Numeric." + cleanTipItem(itemName));
			}
		} else if (colName.startsWith("Boolean")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function")) {
				tipText = "none";
			} else {
				tipText = getString("Boolean." + cleanTipItem(itemName));
			}
		} else if (colName.startsWith("Date/Time")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function")) {
				tipText = "none";
			} else {
				tipText = getString("DateTime." + cleanTipItem(itemName));
			}
		} else if (colName.startsWith("Misc")) {
			if (itemName.startsWith("Operator") || itemName.startsWith("Function")) {
				tipText = "none";
			} else {
				tipText = getString("Misc." + cleanTipItem(itemName));
			}
		} else if (colName.startsWith("Data")) {
			tipText = "\"" + itemName + "\" Type Cast";
		}
		return tipText;
	}

	// FIXME init preBuilt Expression & tool tips & radio select
	@Override
	protected void init(Object data) {
		try {
			preBuiltExpressonFile = new File(CZUtil.getCZWorkDirectory(), PRE_BUILT_CLEANSER_REGEX_FILE);
		} catch (IOException e) {
			dialog.getLog().logError("Unable to load pre built expressios: " + preBuiltExpressonFile);
			e.printStackTrace();
		}
		loadPreBuiltExpression();
		enable() ;
	}

	private void loadMaps() {
		mCharEscapes = new HashMap<String, String>();
		mCharEscapes.put("Bell=\\a", "\\a");
		mCharEscapes.put("Backspace=\\b", "\\b");
		mCharEscapes.put("Tab=\\t", "\\t");
		mCharEscapes.put("Carriage Return=\\r", "\\r");
		mCharEscapes.put("Newline=\\n", "\\");
		mCharEscapes.put("Form-feed=\\f", "\\f");
		mCharEscapes.put("Vertical tab=\\v", "\\v");
		mCharEscapes.put("Escape=\\e", "\\e");
		mCharEscapes.put("White-space=\\s", "\\s");
		mCharEscapes.put("Word=\\w", "\\w");
		mCharClasses = new HashMap<String, String>();
		mCharClasses.put("Any character=. ", ".");
		mCharClasses.put("Character in set=[]", "[]");
		mCharClasses.put("Character not in set=[^]", "[^]");
		mCharClasses.put("Character range=[-]", "[-]");
		mCharClasses.put("Character union=[-[-]]", "[-[-]]");
		mCharClasses.put("Character intersection=[-&&[]]", "<");
		mCharClasses.put("Character subtraction=[-&&[^]]", "[-&&[^]]");
		mBoundryConditions = new HashMap<String, String>();
		mBoundryConditions.put("Start of string=^", "^");
		mBoundryConditions.put("End of string=$", "$");
		mBoundryConditions.put("Boundry=\\b", "\\b");
		mBoundryConditions.put("Non-boundry=\\B", "\\B");
		// Number
		mQuantifiers = new HashMap<String, String>();
		mQuantifiers.put("Zero or more=*", "*");
		mQuantifiers.put("One or more=+", "+");
		mQuantifiers.put("Zero or One=?", "?");
		mQuantifiers.put("Exactly n times={n}", "{n}");
		mQuantifiers.put("At least n times={n,}", "{n,}");
		mQuantifiers.put("at least n but not more than m time n={n,m}", "{n,m}");
		mGrouping = new HashMap<String, String>();
		mGrouping.put("Group=()", "()");
		// Boolean
		mSubstitutions = new HashMap<String, String>();
		mSubstitutions.put("Contents of group number=$", "$");
		mCoditionals = new HashMap<String, String>();
		mCoditionals.put("Or=|", "|");
		mCoditionals.put("Conditional=(?|)", "(?|)");
		mCoditionals.put("Capture=(?|)", "(?|)");
		mOptions = new HashMap<String, String>();
		mOptions.put("Ignore Case on=(?i:)", "(?i:)");
		mOptions.put("Ignore Case off=(?-i:)", "(?-i:)");
		mOptions.put("Expliced Capture on=(?n:)", "(?n:)");
		mOptions.put("Expliced Capture off=(?-:)", "(?-:)");
	}

	private void loadPreBuiltExpression() {
		try {
			if (preBuiltExpressonFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(preBuiltExpressonFile));
				String line;
				while ((line = br.readLine()) != null) {
					preBuiltExpressions.add(line);
				}
				br.close();
			} else {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ccPreBuilt.removeAll();
		if (preBuiltExpressions != null) {
			for (String rule : preBuiltExpressions) {
				ccPreBuilt.add(rule);
			}
		}
	}

	private void saveExpressions() {
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(preBuiltExpressonFile));
			for (String line : preBuiltExpressions) {
				br.write(line);
				br.newLine();
			}
			br.close();
		} catch (IOException e) {
			dialog.getLog().logError("Unable to save prebuilt expression file: " + preBuiltExpressonFile);
			e.printStackTrace();
		}
	}
	
	public void setExpressionValues(String[] values){
		expValues = values;
		
		if(preBuiltExpressions.contains(values[0])){
			rPreBuilt.setSelection(true);
			rSpecified.setSelection(false);
		} else {
			rPreBuilt.setSelection(false);
			rSpecified.setSelection(true);
		}
		
		
		setExpressionString(values[0]);
		if(values[1] != null)
		setReplaceString(values[1]);
		
	}

	private void setExpressionString(String expression) {
		expressionString = expression;
		txSpecified.setText(expressionString);
	}
	
	private void setReplaceString(String replace){
		replaceString = replace;
		txReplace.setText(replaceString);
		
	}
	
	
//	public String getExpressionString(){
//		return expressionString;
//	}
//	
//	public String getReplaceString(){
//		return replaceString;
//	}
	

	private void testExpression() {
		if(Const.isEmpty(expressionString)){
			return;
		}
		String input = txSampleIn.getText();
		String exp = expressionString;
		String replacement = replaceString;
		String output = "";
		String found = "false";
		try{
		Pattern pattern = Pattern.compile(exp);
		Matcher matcher = pattern.matcher(input);
	//	if (Const.isEmpty(replacement)) {
			boolean y = matcher.find();
			found = String.valueOf(y);
		if (!Const.isEmpty(replacement)) {
			output = matcher.replaceAll(replacement);
		}
		txSampleOut.setText(output);
		txFind.setText(found);
		} catch (Exception e ){
			txSampleOut.setText(e.getMessage());
		}
	}

	private void updateSpecified(Tree tree) {
		int pos = txSpecified.getCaretPosition();
		String fullLine = txSpecified.getText();
		String selected = txSpecified.getSelectionText();
		String part1 = fullLine.substring(0, pos);
		String part2;
		if (tree.getSelection()[0].getParentItem() != null) {
			part2 = getPart2(tree.getSelection()[0].getText(), tree.getColumn(0).getText());
		} else {
			part2 = getPart2(tree.getSelection()[0].getText(), tree.getColumn(0).getText());
		}
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
		if (endPos > 1) {
			txSpecified.setSelection(pos, endPos);
		} else {
			txSpecified.setSelection(txSpecified.getText().length());
		}
	}
}
