package com.melissadata.cz.support;

import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.row.RowMetaInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to hold input and output meta
 */
public class IOMetaHandler {
	public RowMetaInterface inputMeta;
	public RowMetaInterface outputMeta;
	public List<RowSet>       targetRowSets = new ArrayList<RowSet>();
	public List<FilterTarget> filterTargets = new ArrayList<FilterTarget>();
	public int[] passThruFieldNrs;

	public void addTargetRow(RowSet targetRowSet, FilterTarget filterTarget) {
		targetRowSets.add(targetRowSet);
		filterTargets.add(filterTarget);
	}
}
