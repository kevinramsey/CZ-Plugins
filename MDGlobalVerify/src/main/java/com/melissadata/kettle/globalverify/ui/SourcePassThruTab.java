package com.melissadata.kettle.globalverify.ui;

import java.util.SortedMap;

import org.pentaho.di.trans.TransMeta;

import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.globalverify.MDGlobalDialog;

public class SourcePassThruTab extends PassThruTab {
	public SourcePassThruTab(MDGlobalDialog dialog, TransMeta transMeta) {
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
