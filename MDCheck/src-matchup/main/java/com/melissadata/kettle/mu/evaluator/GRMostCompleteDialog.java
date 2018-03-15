package com.melissadata.kettle.mu.evaluator;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.mu.MatchUpMeta;

public class GRMostCompleteDialog extends MDAbstractDialog {
	
	private static final Class<?> PKG = MDCheckDialog.class;
	private MatchUpMeta matchUp;
	
	
	private String[] colNames;
	private Table goldenTable;
	private Set<String> selectedColumns;
	private Set<Button> ckColumns;
	private Algorithm mostCompleteAlg;
	
	public GRMostCompleteDialog(MDCheckDialog dialog, MDCheckStepData mdStepData) {
		super(dialog, SWT.NONE);
		
	}


	@Override
	protected void createContents(Composite parent, Object data) {
		MDCheckStepData stepData = (MDCheckStepData) data;
		matchUp = stepData.getMatchUp();

		Image ime = new Image(this.getDisplay(), getClass().getResourceAsStream("/com/melissadata/kettle/images/PDI_MD_MatchUp_32.png"));
		this.getShell().setImage(ime);
		
		
	//	colNames = stepData.getSourcePassThru().getPassThru().toArray(new String[stepData.getSourcePassThru().getPassThru().size()]);

		if(stepData.getSourcePassThru().isSurvivorPass()){
			List<SurvivorField> survivorList = stepData.getSourcePassThru().getSurvivorPassThru();
			int                 size         = survivorList.size();
			colNames = new String[size];

			for (int rr = 0; rr < size; rr++) {
				if (survivorList.get(rr).isStringType()) {
					colNames[rr] = survivorList.get(rr).getOutputName();
				}
			}
			
			
		} else {
			colNames = stepData.getSourcePassThru().getPassThru().toArray(new String[stepData.getSourcePassThru().getPassThru().size()]);
		}
		
		
		
		Label lDescription = addLabel(parent, null, "Description");
		Label lSpacer = addSpacer(parent, lDescription);
		Label lInfo = addLabel(parent, lSpacer, "Info");
		Label lSpacer2 = addSpacer(parent, lInfo);

		// Create composite that will contain all controls except the main
		// buttons
		Composite ggComp = new Composite(parent, 0);
		setLook(ggComp);
		ggComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(lSpacer2, margin);
		fd.bottom = new FormAttachment(90, margin);
		ggComp.setLayoutData(fd);

		createTable(ggComp, null);

		Button selAll = new Button(parent, SWT.PUSH);
		selAll.setText("Select All");
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(ggComp, margin);
		//fd.bottom = new FormAttachment(90, margin);
		selAll.setLayoutData(fd);
		selAll.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				for (Button itm : ckColumns) {
					itm.setSelection(true);
					for (String cName : colNames) {
						selectedColumns.add(cName);
					}
				}
				changed = true;
			}
		});

		Button rmAll = new Button(parent, SWT.PUSH);
		rmAll.setText("Remove All");
		fd = new FormData();
		fd.left = new FormAttachment(selAll, margin);
		fd.top = new FormAttachment(ggComp, margin);
		//fd.bottom = new FormAttachment(90, margin);
		rmAll.setLayoutData(fd);
		rmAll.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				for (Button itm : ckColumns) {
					itm.setSelection(false);
					for (String cName : colNames) {
						selectedColumns.remove(cName);
					}
				}
				changed = true;
			}
		});
	}

	private void createTable(Composite parent, Control last) {

		goldenTable = new Table(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		goldenTable.setHeaderVisible(true);
		goldenTable.setLinesVisible(true);

		//check
		TableColumn tableColumn1 = new TableColumn(goldenTable, SWT.NONE);
		tableColumn1.setWidth(25);
		tableColumn1.setMoveable(false);
		tableColumn1.setResizable(false);
		tableColumn1.setText(" ");

		//Algorithm
		TableColumn tableColumn2 = new TableColumn(goldenTable, SWT.NONE);
		tableColumn2.setWidth(200);
		tableColumn2.setMoveable(false);
		tableColumn2.setResizable(false);
		tableColumn2.setText("Column Name"/* getString("Algorithm.Label") */);

	}

	private TableItem addTableItem(String name, boolean selected) {

		final TableItem item = new TableItem(goldenTable, SWT.NONE);
		item.setText(1, name);

		TableEditor editor = new TableEditor(goldenTable);
		final Button ckBtn = new Button(goldenTable, SWT.CHECK);
		ckBtn.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event arg0) {
				if (ckBtn.getSelection()) {
					selectedColumns.add(item.getText(1));

				} else {
					selectedColumns.remove(item.getText(1));
				}
				changed = true;
			}
		});
		ckBtn.pack();
		setLook(ckBtn);
		ckBtn.setSelection(selected);
		ckColumns.add(ckBtn);
		editor.minimumWidth = ckBtn.getSize().x;
		editor.horizontalAlignment = SWT.CENTER;
		editor.setEditor(ckBtn, item, 0);

		editor.setItem(item);

		return item;

	}

	@Override
	protected void init(Object data) {

		for(Algorithm alg: matchUp.getAlgorithms()){
			if(alg.algorithmType == Algorithm.AlgorithmType.MOSTCOMPLETE){
				mostCompleteAlg = alg;
			}
		}
		
		selectedColumns = parseExpression();
		ckColumns = new HashSet<Button>();
		for(String cName:colNames){
			if(cName != null)
				addTableItem(cName,selectedColumns.contains(cName));
		}
		
		int ht = 0;
		int rcnt = goldenTable.getItemCount() + 1;
		if(rcnt < 10)
			ht = goldenTable.getItemHeight() * rcnt;
		else
			ht = goldenTable.getItemHeight() * 10;

		
		FormData fd = new FormData();
		fd.height = ht;
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(null, margin);
	//	fd.bottom = new FormAttachment(100, margin);
		goldenTable.setLayoutData(fd);
		
		this.getShell().layout(true, true);
		final Point newSize = this.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);  
		this.getShell().setSize(newSize);
		
		Point pnt = getParent().toDisplay(getParent().getLocation().x  , getParent().getLocation().y);
		pnt.x = pnt.x/2 + (pnt.x/2)/4;
		pnt.y = pnt.y/2 + (pnt.y/2)/4;
		this.getShell().setLocation(pnt);

	}
	
	private String buildExpression(){
		String	expression = "";
		for(String col:selectedColumns){
			if(!col.isEmpty())
				expression += "+((Len(" + col + ")>0)?1:0)";
		}

		if(Const.isEmpty(expression))
			return expression;
		else
			return expression.substring(1, expression.length());
	}
	
	private Set<String> parseExpression(){
		Set<String> tSet = new HashSet<String>();
		String exp = mostCompleteAlg.getExpression().replace(" ", "").replace("+", "").replace(")>0)?1:0)", "").replace("((Len(", ",");
		
		for (String cName : exp.split(",")) {
			if (!Const.isEmpty(cName)) {
				if (!Arrays.asList(colNames).contains(cName)) {
					return tSet;
				}
			}
		}
		
		for(String cName: exp.split(",")){
			if(!Const.isEmpty(cName)){		
				tSet.add(cName);
			}
		}
		
		return tSet;
	}

	@Override
	protected boolean getData(Object data) throws KettleException {

		mostCompleteAlg.setExpression(buildExpression());
	
		return true;
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
		return "MDCheckDialog.GRMostCompleteDialog";
	}

}
