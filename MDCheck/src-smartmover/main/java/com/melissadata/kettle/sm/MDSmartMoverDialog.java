package com.melissadata.kettle.sm;

import com.melissadata.kettle.MDCheckDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;

public class MDSmartMoverDialog extends MDCheckDialog {

	public MDSmartMoverDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {
		super(parent, in, transMeta, stepname);
	}

}
