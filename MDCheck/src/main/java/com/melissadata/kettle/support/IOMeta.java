package com.melissadata.kettle.support;

import java.util.ArrayList;
import java.util.List;

import com.melissadata.kettle.PassThruMeta;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.row.RowMetaInterface;

public class IOMeta {
	public RowMetaInterface		inputMeta;
	public RowMetaInterface		outputMeta;
	public List<RowSet>			targetRowSets	= new ArrayList<RowSet>();
	public List<FilterTarget>	filterTargets	= new ArrayList<FilterTarget>();
	public PassThruMeta passThruMeta;
	public int[]        passThruFieldNrs;

	public void addTargetRow(RowSet targetRowSet, FilterTarget filterTarget) {
		targetRowSets.add(targetRowSet);
		filterTargets.add(filterTarget);
	}
}
