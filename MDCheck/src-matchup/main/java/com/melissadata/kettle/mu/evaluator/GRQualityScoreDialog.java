package com.melissadata.kettle.mu.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.ui.util.ImageUtil;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.mu.MatchUpMeta;

public class GRQualityScoreDialog extends MDAbstractDialog {

	private static final Class<?> PKG = MDCheckDialog.class;
	private MDCheckHelper                              helper;
	private MatchUpMeta                                matchUp;

	private CCombo                                     cbColumns;

	private String[]                                   colNames;
	private String                                     selectedCol;

	private List<QualityScore>                         lsQuScores;

	private Button                                     pItemDown;
	private Button                                     pItemUp;
	private Map<QualityScore.QualityScoreType, Button> ckBtnMap;

	private Table                                      qualityScoreTable;

	public GRQualityScoreDialog(MDCheckDialog dialog, MDCheckStepData mdStepData) {
		super(dialog, SWT.NONE);

	}

	@Override
	protected void createContents(Composite parent, Object data) {
		MDCheckStepData stepData = (MDCheckStepData) data;
		matchUp = stepData.getMatchUp();
		lsQuScores = matchUp.getQualityScores();

		Image ime = new Image(this.getDisplay(), getClass().getResourceAsStream("/com/melissadata/kettle/images/PDI_MD_MatchUp_32.png"));
		this.getShell().setImage(ime);

		helper = ((MDCheckDialog) dialog).getHelper();

		if (stepData.getSourcePassThru().isSurvivorPass()) {
			int size = stepData.getSourcePassThru().getSurvivorPassThru().size();
			colNames = new String[size];
			for (int rr = 0; rr < size; rr++) {
				colNames[rr] = stepData.getSourcePassThru().getSurvivorPassThru().get(rr).getOutputName();
			}

		} else {
			colNames = stepData.getSourcePassThru().getPassThru().toArray(new String[stepData.getSourcePassThru().getPassThru().size()]);
		}

		Label lDescription = addLabel(parent, null, "Description");

		Label lblAvailibleColumns = new Label(parent, SWT.RIGHT);
		lblAvailibleColumns.setText(getString("ColumnName.Label"));
		helper.setLook(lblAvailibleColumns);
		cbColumns = new CCombo(parent, SWT.BORDER);

		cbColumns.setEditable(false);
		cbColumns.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedCol = cbColumns.getText();
				changed = true;
			}
		});

		FormData fd = new FormData();
		fd.left = new FormAttachment(20, margin);
		fd.top = new FormAttachment(lDescription, margin * 5);
		// fd.right = new FormAttachment(50, -margin);
		lblAvailibleColumns.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(lblAvailibleColumns, margin);
		fd.top = new FormAttachment(lDescription, margin * 5);
		fd.right = new FormAttachment(80, -margin);
		cbColumns.setLayoutData(fd);

		for (int ci = 0; ci < colNames.length; ci++) {
			cbColumns.add(colNames[ci]);
		}

		Label lInfo = addLabel(parent, lblAvailibleColumns, "Info");
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(cbColumns, margin * 5);
		fd.right = new FormAttachment(100, -margin);
		lInfo.setLayoutData(fd);

		Label spacer = addSpacer(parent, lInfo);

		Image iUp = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/UpArrow.gif");
		pItemUp = new Button(parent, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(spacer, helper.margin);
		pItemUp.setLayoutData(fd);
		pItemUp.setImage(iUp);
		pItemUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveItemUp(qualityScoreTable.getSelectionIndex());
				changed = true;
			}
		});

		Image iDn = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/DownArrow.gif");
		pItemDown = new Button(parent, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(pItemUp, helper.margin);
		pItemDown.setLayoutData(fd);
		pItemDown.setImage(iDn);
		pItemDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveItemDown(qualityScoreTable.getSelectionIndex());
				changed = true;
			}
		});

		createTable(parent, spacer);
	}

	private void createTable(Composite parent, Control last) {

		qualityScoreTable = new Table(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		qualityScoreTable.setHeaderVisible(true);
		qualityScoreTable.setLinesVisible(true);

		FormData fd = new FormData();
		fd.left = new FormAttachment(pItemDown, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		qualityScoreTable.setLayoutData(fd);

		// index
		TableColumn tableColumn = new TableColumn(qualityScoreTable, SWT.NONE);
		tableColumn.setWidth(20);
		tableColumn.setMoveable(false);
		tableColumn.setResizable(false);
		tableColumn.setText(" ");

		// check
		TableColumn tableColumn1 = new TableColumn(qualityScoreTable, SWT.NONE);
		tableColumn1.setWidth(25);
		tableColumn1.setMoveable(false);
		tableColumn1.setResizable(false);
		tableColumn1.setText(" ");

		// Algorithm
		TableColumn tableColumn2 = new TableColumn(qualityScoreTable, SWT.NONE);
		tableColumn2.setWidth(200);
		tableColumn2.setMoveable(false);
		tableColumn2.setResizable(false);
		tableColumn2.setText(getString("RuleName.Label"));

		loadBtnMaps();
		for (QualityScore qsc : lsQuScores) {

			final Button btn = ckBtnMap.get(qsc.getQualityScoreType());
			btn.setData(qsc);
			helper.setLook(btn);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {

					changed = true;
					qualityScoreTable.setSelection(((QualityScore) btn.getData()).getIndex() - 1);
				}
			});
		}

	}

	private TableItem addTableItem(QualityScore qScore) {

		final TableItem item = new TableItem(qualityScoreTable, SWT.NONE);
		item.setText(0, String.valueOf(qScore.getIndex()));
		item.setText(2, qScore.getQualityScoreType().getDescription());
		TableEditor editor = new TableEditor(qualityScoreTable);
		Button ckBtn = ckBtnMap.get(qScore.getQualityScoreType());
		ckBtn.pack();
		ckBtn.setSelection(qScore.isSelected());
		setLook(ckBtn);
		editor.minimumWidth = ckBtn.getSize().x;
		editor.horizontalAlignment = SWT.CENTER;
		editor.setEditor(ckBtn, item, 1);

		editor.setItem(item);

		return item;
	}

	private void moveItemUp(int selected) {
		if (selected < 1)
			return;

		// Index values start at 1 but selected starts at 0
		// so compensate
		lsQuScores.get(selected - 1).setIndex(selected + 1);
		lsQuScores.get(selected).setIndex(selected);
		Collections.sort(lsQuScores);
		refreshQualityScoreTable();
		qualityScoreTable.setSelection(selected - 1);

	}

	private void moveItemDown(int selected) {
		if (selected < 0)
			return;

		// Index values start at 1 but selected starts at 0
		// so compensate
		lsQuScores.get(selected).setIndex(selected + 2);
		lsQuScores.get(selected + 1).setIndex(selected + 1);
		Collections.sort(lsQuScores);

		refreshQualityScoreTable();
		qualityScoreTable.setSelection(selected + 1);

	}

	private void refreshQualityScoreTable() {
		qualityScoreTable.removeAll();
		for (QualityScore alg : lsQuScores) {
			addTableItem(alg);
		}

	}

	private void loadBtnMaps() {

		ckBtnMap = new HashMap<QualityScore.QualityScoreType, Button>();

		ckBtnMap.put(QualityScore.QualityScoreType.DataQualityScore, new Button(qualityScoreTable, SWT.CHECK));
		ckBtnMap.put(QualityScore.QualityScoreType.AddressQualityScore, new Button(qualityScoreTable, SWT.CHECK));
		ckBtnMap.put(QualityScore.QualityScoreType.NameQualityScore, new Button(qualityScoreTable, SWT.CHECK));
		ckBtnMap.put(QualityScore.QualityScoreType.PhoneQualityScore, new Button(qualityScoreTable, SWT.CHECK));
		ckBtnMap.put(QualityScore.QualityScoreType.EmailQualityScore, new Button(qualityScoreTable, SWT.CHECK));
		ckBtnMap.put(QualityScore.QualityScoreType.GeoCodeQualityScore, new Button(qualityScoreTable, SWT.CHECK));
	}

	private String parseSelectedField(String exp) {

		String retVal = exp.substring(exp.indexOf('(') + 1, exp.indexOf(')'));

		for (String name : colNames) {
			if (name.equals(retVal))
				return retVal;
		}

		return "";

	}

	@Override
	protected void init(Object data) {

		for (QualityScore qScore : lsQuScores) {
			addTableItem(qScore);
		}
		selectedCol = parseSelectedField(lsQuScores.get(0).getExpression());
		cbColumns.setText(selectedCol);

		this.getShell().layout(true, true);
		final Point newSize = this.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		this.getShell().setSize(newSize);

		Point pnt = getParent().toDisplay(getParent().getLocation().x, getParent().getLocation().y);
		pnt.x = pnt.x / 2 + (pnt.x / 2) / 4;
		pnt.y = pnt.y / 2 + (pnt.y / 2) / 4;
		this.getShell().setLocation(pnt);

	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		for (Button ckb : ckBtnMap.values()) {
			((QualityScore) ckb.getData()).setSelected(ckb.getSelection());
			((QualityScore) ckb.getData()).setResultField(selectedCol);
		}
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
		return "MDCheckDialog.GRQualityScoreDialog";
	}

}
