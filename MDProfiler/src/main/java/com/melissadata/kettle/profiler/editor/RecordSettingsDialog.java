package com.melissadata.kettle.profiler.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.profiler.MDProfilerData;
import com.melissadata.kettle.profiler.MDProfilerDialog;
import com.melissadata.kettle.profiler.MDProfilerMeta;
import com.melissadata.kettle.profiler.MDProfilerStep;
import com.melissadata.kettle.profiler.support.MDProfilerHelper;
import com.melissadata.kettle.profiler.ui.ProfileRecord;

public class RecordSettingsDialog extends MDAbstractDialog {
	private static Class<?>			PKG	= MDProfilerStep.class;
	private MDProfilerData			prData;
	private MDProfilerMeta			prMeta;
	private MDProfilerHelper		helper;
	private Button					ckScale;
	private CCombo					cbScale;
	private Button					ckPrecision;
	private CCombo					cbPrecision;
	private Button					ckLength;
	private CCombo					cbLength;
	private Button					ckDefault;
	private Text					txDefault;
	private Button					ckBounds;
	private Text					txUpperBounds;
	private Text					txLowerBounds;
	private Button					ckRegEx;
	private Text					txRegEx;
	private Label					lSelectedProfile;
	private Button					pPrevious;
	private Button					pNext;
	private Button					pAdd;
	private Button					pDelete;
	private String					curretRecordColName;
	private int						curIndex;
	private Map<Integer, String>	indexes;
	private ProfileRecord			profileRecord;
	HashMap<String, ProfileRecord>	profileRecords;

	public RecordSettingsDialog(MDProfilerDialog dialog, int flags) {
		super(dialog, flags);
	}

	private void addRegEx() {
		RegExBuilderDialog red = new RegExBuilderDialog((MDProfilerDialog) dialog, prData);
		red.setProfile(profileRecord);
		red.open();
		setValues(profileRecord);
	}

	@Override
	protected void createContents(Composite parent, Object data) {
		prData = (MDProfilerData) data;
		helper = ((MDProfilerDialog) dialog).getHelper();
		Image ime = new Image(getDisplay(), getClass().getResourceAsStream("/com/melissadata/kettle/profiler/images/PDI_MD_Profiler_32.png"));
		getShell().setImage(ime);
		getShell().setText(getString("Title", "FieldName"));
		Composite wComp = new Composite(parent, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		description.setText(getString("Description"));
		Label spacer = helper.addSpacer(wComp, description);
		// SCALE
		ckScale = addCheckBox(wComp, spacer, "Scale");
		cbScale = new CCombo(wComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(20, helper.margin);
		fd.right = new FormAttachment(30, helper.margin);
		fd.top = new FormAttachment(spacer, helper.margin);
		cbScale.setLayoutData(fd);
		for (int i = 0; i <= 20; i++) {
			cbScale.add(String.valueOf(i));
		}
		setLook(cbScale);
		ckScale.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				profileRecord.setSetScale(ckScale.getSelection());
			}
		});
		cbScale.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				profileRecord.setScale(cbScale.getText());
			}
		});
		// PRECISION
		ckPrecision = addCheckBox(wComp, cbScale, "Precision");
		cbPrecision = new CCombo(wComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(20, helper.margin);
		fd.right = new FormAttachment(30, helper.margin);
		fd.top = new FormAttachment(cbScale, helper.margin);
		cbPrecision.setLayoutData(fd);
		for (int i = 0; i <= 20; i++) {
			cbPrecision.add(String.valueOf(i));
		}
		setLook(cbPrecision);
		ckPrecision.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				profileRecord.setSetPrecision(ckPrecision.getSelection());
			}
		});
		cbPrecision.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				profileRecord.setPrecision(cbPrecision.getText());
			}
		});
		// MAX Length
		ckLength = addCheckBox(wComp, cbPrecision, "Length");
		cbLength = new CCombo(wComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(20, helper.margin);
		fd.right = new FormAttachment(30, helper.margin);
		fd.top = new FormAttachment(cbPrecision, helper.margin);
		cbLength.setLayoutData(fd);
		for (int i = 0; i <= 200; i++) {
			cbLength.add(String.valueOf(i));
		}
		setLook(cbLength);
		ckLength.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				profileRecord.setSetLength(ckLength.getSelection());
			}
		});
		cbLength.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				profileRecord.setLength(cbLength.getText());
			}
		});
		Label spacer1 = addSpacer(wComp, cbLength);
		// Default Val
		ckDefault = addCheckBox(wComp, spacer1, "Default");
		txDefault = new Text(wComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(20, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(spacer1, helper.margin);
		txDefault.setLayoutData(fd);
		setLook(txDefault);
		ckDefault.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				profileRecord.setSetDefaultValue(ckDefault.getSelection());
			}
		});
		txDefault.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				profileRecord.setDefaultValue(txDefault.getText());
			}
		});
		// Bounds
		ckBounds = addCheckBox(wComp, txDefault, "Bounds");
		ckBounds.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				profileRecord.setSetBounds(ckBounds.getSelection());
			}
		});
		Label lLowerBound = addLabel(wComp, ckBounds, "LowerBounds");
		fd = new FormData();
		fd.left = new FormAttachment(10, helper.margin);
		fd.right = new FormAttachment(20, helper.margin);
		fd.top = new FormAttachment(ckBounds, helper.margin);
		lLowerBound.setLayoutData(fd);
		txLowerBounds = new Text(wComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(20, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(ckBounds, helper.margin);
		txLowerBounds.setLayoutData(fd);
		setLook(txLowerBounds);
		txLowerBounds.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				profileRecord.setLowerBound(txLowerBounds.getText());
			}
		});
		Label lUpperBound = addLabel(wComp, txLowerBounds, "UpperBounds");
		fd = new FormData();
		fd.left = new FormAttachment(10, helper.margin);
		fd.right = new FormAttachment(20, helper.margin);
		fd.top = new FormAttachment(txLowerBounds, helper.margin);
		lUpperBound.setLayoutData(fd);
		txUpperBounds = new Text(wComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(20, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(txLowerBounds, helper.margin);
		txUpperBounds.setLayoutData(fd);
		setLook(txUpperBounds);
		txUpperBounds.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				profileRecord.setUpperBound(txUpperBounds.getText());
			}
		});
		lSelectedProfile = addLabel(wComp, null/* txRegEx */, "");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(85, helper.margin);
		lSelectedProfile.setLayoutData(fd);
		lSelectedProfile.setText("Selected Profile: profile,profile");
		// RegEx
		ckRegEx = addCheckBox(wComp, txUpperBounds, "RegEx");
		txRegEx = new Text(wComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(5, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(ckRegEx, helper.margin);
		fd.bottom = new FormAttachment(lSelectedProfile, -helper.margin);
		txRegEx.setLayoutData(fd);
		txRegEx.setEditable(false);
		setLook(txRegEx);
		ckRegEx.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				profileRecord.setSetCustomPattern(ckRegEx.getSelection());
			}
		});
		txRegEx.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				profileRecord.setCustomPattern(txRegEx.getText());
			}
		});
		pAdd = addPushButton(wComp, txRegEx, "Add");
		pDelete = addPushButton(wComp, txRegEx, "Delete");
		fd = new FormData();
		fd.left = new FormAttachment(90, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(txRegEx, helper.margin);
		pDelete.setLayoutData(fd);
		pDelete.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				deleteRegEx();
			}
		});
		fd = new FormData();
		fd.left = new FormAttachment(80, helper.margin);
		fd.right = new FormAttachment(90, helper.margin);
		fd.top = new FormAttachment(txRegEx, helper.margin);
		pAdd.setLayoutData(fd);
		pAdd.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addRegEx();
			}
		});
		Label sp2 = addSpacer(wComp, lSelectedProfile);
		pPrevious = addPushButton(wComp, sp2, "Previous");
		pPrevious.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				previousProfile();
			}
		});
		pNext = addPushButton(wComp, sp2, "Next");
		fd = new FormData();
		fd.left = new FormAttachment(pPrevious, helper.margin);
		fd.right = new FormAttachment(20, helper.margin);
		fd.top = new FormAttachment(sp2, helper.margin);
		pNext.setLayoutData(fd);
		pNext.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				nextProfile();
			}
		});
	}

	private void deleteRegEx() {
		profileRecord.setCustomPattern("");
		profileRecord.setSetCustomPattern(false);
		setValues(profileRecord);
	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		prMeta.setProfileRecords(profileRecords);
		return true;
	}

	// TODO Help url
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
		return "MDProfilerStep.RecordSettingsDialog";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void init(Object data) {
		prMeta = ((MDProfilerDialog) dialog).getInput();
		profileRecords = (HashMap<String, ProfileRecord>) prMeta.getProfileRecords().clone();
		profileRecord = profileRecords.get(curretRecordColName);
		indexes = new HashMap<Integer, String>();
		int i = 0;
		for (String key : profileRecords.keySet()) {
			indexes.put(i, key);
			if (key.equals(curretRecordColName)) {
				curIndex = i;
			}
			i++;
		}
	}

	private void nextProfile() {
		if (curIndex < (indexes.size() - 1)) {
			curIndex++;
			curretRecordColName = indexes.get(curIndex);
		}
		profileRecord = profileRecords.get(curretRecordColName);
		setValues(profileRecord);
	}

	private void previousProfile() {
		if (curIndex > 0) {
			curIndex--;
			curretRecordColName = indexes.get(curIndex);
		}
		profileRecord = profileRecords.get(curretRecordColName);
		setValues(profileRecord);
	}

	public void setProfileRecord(String prField) {
		curretRecordColName = prField;
		profileRecord = profileRecords.get(curretRecordColName);
		setValues(profileRecord);
	}

	private void setValues(ProfileRecord pr) {
		ckScale.setSelection(pr.isSetScale());
		cbScale.setText(pr.getScale());
		ckPrecision.setSelection(pr.isSetPrecision());
		cbPrecision.setText(pr.getPrecision());
		ckLength.setSelection(pr.isSetLength());
		cbLength.setText(pr.getLength());
		ckDefault.setSelection(pr.isSetDefaultValue());
		txDefault.setText(pr.getDefaultValue() == null ? "" : pr.getDefaultValue());
		ckBounds.setSelection(pr.isSetBounds());
		txUpperBounds.setText(pr.getUpperBound() == null ? "" : pr.getUpperBound());
		txLowerBounds.setText(pr.getLowerBound() == null ? "" : pr.getLowerBound());
		ckRegEx.setSelection(pr.isSetCustomPattern());
		txRegEx.setText(pr.getCustomPattern() == null ? "" : pr.getCustomPattern());
		lSelectedProfile.setText("Selected Profile:	 " + pr.getColumnName());
		getShell().setText(getString("Title", pr.getColumnName()));
	}
}
