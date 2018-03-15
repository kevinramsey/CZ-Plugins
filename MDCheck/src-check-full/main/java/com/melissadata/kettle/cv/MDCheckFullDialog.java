package com.melissadata.kettle.cv;

import com.melissadata.kettle.MDCheckDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;

public class MDCheckFullDialog extends MDCheckDialog {

	public MDCheckFullDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {
		super(parent, in, transMeta, stepname);
	}

}
