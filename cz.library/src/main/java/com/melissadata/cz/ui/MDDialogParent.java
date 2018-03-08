package com.melissadata.cz.ui;

import java.util.SortedMap;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

import com.melissadata.cz.SourceFieldInfo;

public interface MDDialogParent {

	/**
	 * @return The SWT shell for the dialog parent
	 */
	Shell getShell();

	/**
	 * @return The object managed by the dialog
	 */
	Object getData();

	/**
	 * @return The variable space for the component
	 */
	VariableSpace getSpace();

	/**
	 * @return The logging interface for the component
	 */
	LogChannelInterface getLog();

	/**
	 * @return A sorted map of source field information
	 */
	SortedMap<String, SourceFieldInfo> getSourceFields();

}
