package com.melissadata.kettle.muglobal;

import com.melissadata.kettle.MDCheckDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;

public class MDMatchUpGlobalDialog extends MDCheckDialog {

	public MDMatchUpGlobalDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {
		super(parent, in, transMeta, stepname);
	}

}
