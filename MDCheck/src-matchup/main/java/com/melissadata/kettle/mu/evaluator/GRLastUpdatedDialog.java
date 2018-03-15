package com.melissadata.kettle.mu.evaluator;

import java.util.List;

import com.melissadata.kettle.mu.MatchUpMeta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.MDCheckStepData;

public class GRLastUpdatedDialog extends MDAbstractDialog {

	private static final Class<?> PKG = MDCheckDialog.class;
	private MDCheckHelper helper;

	private CCombo cbColumns;
	private Button rHighestValue;
	private Button rLowestValue;

	private Algorithm algLastUpdated;
	private String colName;

	private RowMetaInterface row;

	private String[] colNames;

	public GRLastUpdatedDialog(MDCheckDialog dialog, MDCheckStepData mdStepData) {
		super(dialog, SWT.NONE);

	}

	public void setRow(RowMetaInterface row) {
		this.row = row;

	}

	@Override
	protected void createContents(Composite parent, Object data) {
		MDCheckStepData stepData = (MDCheckStepData) data;
		Image ime = new Image(this.getDisplay(), getClass().getResourceAsStream("/com/melissadata/kettle/images/PDI_MD_MatchUp_32.png"));
		this.getShell().setImage(ime);

		helper = ((MDCheckDialog) dialog).getHelper();

		if (stepData.getSourcePassThru().isSurvivorPass()) {
			List<SurvivorField> survivorList = stepData.getSourcePassThru().getSurvivorPassThru();
			colNames = new String[survivorList.size()];

			for (int n = 0; n < colNames.length; n++) {
				if (survivorList.get(n).isDateType())
					colNames[n] = survivorList.get(n).getOutputName();
			}

		}

		Label lDescription = addLabel(parent, null, "Description");

		Label lLastUpdated = new Label(parent, SWT.RIGHT);
		lLastUpdated.setText(getString("ColumnName.Label"));
		cbColumns = new CCombo(parent, SWT.BORDER);
		cbColumns.setEditable(false);

		FormData fd = new FormData();
		fd.left = new FormAttachment(20, margin);
		fd.top = new FormAttachment(lDescription, margin * 5);
		// fd.right = new FormAttachment(50, -margin);
		lLastUpdated.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(lLastUpdated, margin);
		fd.top = new FormAttachment(lDescription, margin * 5);
		fd.right = new FormAttachment(80, -margin);
		cbColumns.setLayoutData(fd);

		cbColumns.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changed = true;
				colName = cbColumns.getText();
			}
		});

		helper.setLook(cbColumns);
		helper.setLook(lLastUpdated);

		Label lInfo = addLabel(parent, cbColumns, "Info");
		fd = new FormData();
		fd.left = new FormAttachment(0, margin);
		fd.top = new FormAttachment(cbColumns, margin * 5);
		fd.right = new FormAttachment(100, -margin);
		lInfo.setLayoutData(fd);

		Label spacer = addSpacer(parent, lInfo);

		rHighestValue = addButton(parent, spacer, "HighestVal", SWT.RADIO);

		rHighestValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changed = true;
			}
		});

		rLowestValue = addButton(parent, rHighestValue, "LowestVal", SWT.RADIO);
		rLowestValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changed = true;
			}
		});
	}

	private String buildExpression() {
		return colName;
	}

	private void parseExpression() {

		String exp = algLastUpdated.getExpression();
		if (!Const.isEmpty(exp)) {
			colName = exp;
		} else {
			colName = "";
		}
	}

	@Override
	protected void init(Object data) {
		MDCheckStepData stepData = (MDCheckStepData) data;
		MatchUpMeta muMeta = stepData.getMatchUp();

		for (Algorithm alg : muMeta.getAlgorithms()) {
			if (alg.algorithmType == Algorithm.AlgorithmType.LASTUPDATED) {
				algLastUpdated = alg;
			}
		}

		parseExpression();

		if (algLastUpdated.getOption().equals("Latest")) {
			rHighestValue.setSelection(true);
			rLowestValue.setSelection(false);
		} else {
			rHighestValue.setSelection(false);
			rLowestValue.setSelection(true);
		}

		cbColumns.setText(colName);
		changed = false;

		this.getShell().layout(true, true);
		final Point newSize = this.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		this.getShell().setSize(newSize);

		Point pnt = getParent().toDisplay(getParent().getLocation().x, getParent().getLocation().y);
		pnt.x = pnt.x / 2 + (pnt.x / 2) / 4;
		pnt.y = pnt.y / 2 + (pnt.y / 2) / 4;
		this.getShell().setLocation(pnt);

	}

	public boolean loadColNames(MDCheckStepData stepData) {
		//String[] tmp = stepData.getSourcePassThru().getPassThru().toArray(new String[stepData.getSourcePassThru().getPassThru().size()]);
		 dialog.getSourceFields().keySet();
		colNames = new String[row.getFieldNames().length];
		int in = 0;
		for (String cName : dialog.getSourceFields().keySet()) {
			String name = row.getValueMeta(row.indexOfValue(cName)).getName();
			int type = row.getValueMeta(row.indexOfValue(cName)).getType();
			if (type == 3 || type == 9) {
				colNames[in] = name;
				in++;
			}
		}
		for (int ci = 0; ci < colNames.length; ci++) {
			if (colNames[ci] != null)
				cbColumns.add(colNames[ci]);
		}
		return in > 0;
	}

	@Override
	protected boolean getData(Object data) throws KettleException {

		algLastUpdated.setExpression(buildExpression());

		if (rHighestValue.getSelection()) {
			algLastUpdated.setOption("Latest");
		} else {
			algLastUpdated.setOption("Oldest");
		}

		return true;
	}

	@Override
	protected String getHelpURLKey() {
		return "MDCheck.Plugin.Help.GRLastUpdatedDialog.Matchup";
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.GRLastUpdatedDialog";
	}

}
