package com.melissadata.kettle.mu;

import com.melissadata.kettle.MDCheckDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;

public class MDMatchUpDialog extends MDCheckDialog {

	public MDMatchUpDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {
		super(parent, in, transMeta, stepname);
	}

}
