package com.melissadata.kettle.mu.evaluator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;

public class AddColumnDialog extends MDAbstractDialog {

	private static final Class<?> PKG = MDCheckDialog.class;
//	private MDCheckHelper helper;
	
	private Text txtOutputName;
	private SurvivorField survivor;
	
	
	public AddColumnDialog(MDCheckDialog dialog, MDCheckStepData mdStepData) {
		super(dialog, SWT.NONE);
		
	}
	
	
	@Override
	protected void createContents(Composite parent, Object data) {
		//MDCheckStepData stepData = (MDCheckStepData) data;
		Image ime = new Image(this.getDisplay(),getClass().getResourceAsStream("/com/melissadata/kettle/images/PDI_MD_MatchUp_32.png"));
		this.getShell().setImage(ime);
		
//		helper = ((MDCheckDialog) dialog).getHelper();
		
		Control last = addSpacer(parent, null);
		txtOutputName = addTextBox(parent, last, "Name");
		txtOutputName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent arg0) {
				changed = true;
			}
			
		});
		

	}

	@Override
	protected void init(Object data) {
		//nothing to init

	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		survivor.setOutputName(txtOutputName.getText());
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
		return "MDCheckDialog.AddColumnDialog";
	}


	public SurvivorField getSurvivor() {
		return survivor;
	}


	public void setSurvivor(SurvivorField survivor) {
		this.survivor = survivor;
	}

}
