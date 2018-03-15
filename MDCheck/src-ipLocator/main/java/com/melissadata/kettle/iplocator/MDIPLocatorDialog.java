package com.melissadata.kettle.iplocator;

import com.melissadata.kettle.MDCheckDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.trans.TransMeta;

public class MDIPLocatorDialog extends MDCheckDialog {

	public MDIPLocatorDialog(Shell parent, Object in, TransMeta transMeta, String stepname) {
		super(parent, in, transMeta, stepname);
	}

}
