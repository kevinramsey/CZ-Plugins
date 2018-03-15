package com.melissadata.kettle.ui;

import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.PassThruMeta;
import org.pentaho.di.trans.TransMeta;

import java.util.SortedMap;

public class SourcePassThruTab extends PassThruTab {
	public SourcePassThruTab(MDCheckDialog dialog, TransMeta transMeta) {
		super(dialog, transMeta);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getDescription()
	 */
	@Override protected String getDescription() {
		if (compType == Service.MatchUp) {
			return getString("MatchupSourceDescription");
		} else {
			return getString("Description");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getPassThruMeta(com.melissadata.kettle.MDCheckStepData)
	 */
	@Override protected PassThruMeta getPassThruMeta(MDCheckStepData data) {
		return data.getSourcePassThru();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getSourceFields()
	 */
	@Override protected SortedMap<String, SourceFieldInfo> getSourceFields() {
		return dialog.getSourceFields();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getTabTitle()
	 */
	@Override protected String getTabTitle() {
		if (compType == Service.MatchUp) {
			return getString("MatchupSourceTabTitle");
		} else {
			return getString("TabTitle");
		}
	}
}
