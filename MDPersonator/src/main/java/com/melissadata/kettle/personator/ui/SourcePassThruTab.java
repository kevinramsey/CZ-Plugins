package com.melissadata.kettle.personator.ui;

import java.util.SortedMap;

import org.pentaho.di.trans.TransMeta;


import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.personator.MDPersonatorDialog;


public class SourcePassThruTab extends PassThruTab {

	public SourcePassThruTab(MDPersonatorDialog dialog, TransMeta transMeta) {
		super(dialog, transMeta);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.PassThruTab#getTabTitle()
	 */
	@Override
	protected String getTabTitle() {
			return getString("TabTitle");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.PassThruTab#getDescription()
	 */
	@Override
	protected String getDescription() {
			return getString("Description");
	}


	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.PassThruTab#getSourceFields()
	 */
	@Override
	protected SortedMap<String, SourceFieldInfo> getSourceFields() {
		return dialog.getSourceFields();
	}
}
