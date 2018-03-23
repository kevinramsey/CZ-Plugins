package com.melissadata.kettle.globalverify.support;

import com.melissadata.kettle.globalverify.MDGlobalMeta;

public interface MDTab {
	/**
	 * Called to initialize tab controls with configuration data
	 * 
	 * @param data
	 */
	boolean init(MDGlobalMeta meta);

	/**
	 * Called to update tab controls when there is a change to advanced configuration
	 */
	void advancedConfigChanged();

	/**
	 * Called to return the tab control settings to the configuration data
	 * 
	 * @param data
	 */
	void getData(MDGlobalMeta meta);

	/**
	 * @return The URL for help information
	 */
	String getHelpURLKey();
}
