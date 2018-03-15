package com.melissadata.kettle.mu.ui;

import java.util.List;
import java.util.SortedMap;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.PassThruMeta;
import com.melissadata.kettle.ui.PassThruTab;
import org.pentaho.di.core.Const;
import org.pentaho.di.trans.TransMeta;

import com.melissadata.cz.SourceFieldInfo;

public class LookupPassThruTab extends PassThruTab {
	private MatchUpFieldMappingTab	fieldMappingTab;

	public LookupPassThruTab(MDCheckDialog dialog, TransMeta transMeta) {
		super(dialog, transMeta);
	}

	/**
	 * @return The URL for help information
	 */
	@Override
	public String getHelpURLKey() {
			return "MDCheck.Help.MatchUpLookUpPassThroughTab";
	}

	/**
	 * Called when the lookup input changes
	 */
	public void lookupChanged() {
		// Reset the tables
		@SuppressWarnings("unchecked")
		List<SourceFieldInfo> passThru = (List<SourceFieldInfo>) tvPassThru.getInput();
		passThru.clear();
		tvPassThru.refresh();
		@SuppressWarnings("unchecked")
		List<SourceFieldInfo> filterOut = (List<SourceFieldInfo>) tvFilterOut.getInput();
		filterOut.clear();
		tvFilterOut.refresh();
		// Refresh the enablement
		enable();
	}

	/**
	 * Add reference to field mapping tab
	 *
	 * @param fieldMappingTab
	 */
	public void setFieldMappingTab(MatchUpFieldMappingTab fieldMappingTab) {
		this.fieldMappingTab = fieldMappingTab;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getDescription()
	 */
	@Override
	protected String getDescription() {
		return getString("MatchupLookupDescription");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getPassThruMeta(com.melissadata.kettle.MDCheckStepData)
	 */
	@Override
	protected PassThruMeta getPassThruMeta(MDCheckStepData data) {
		return data.getLookupPassThru();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getSourceFields()
	 */
	@Override
	protected SortedMap<String, SourceFieldInfo> getSourceFields() {
		return dialog.getLookupFields(fieldMappingTab.getLookupStepName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.ui.PassThruTab#getTabTitle()
	 */
	@Override
	protected String getTabTitle() {
		return getString("MatchupLookupTabTitle");
	}

	/**
	 * tables will be disabled if no lookup defined
	 */
	@Override
	protected boolean isInputDefined() {
		boolean defined = !Const.isEmpty(fieldMappingTab.getLookupStepName());
		return defined;
	}
}
