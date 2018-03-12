package com.melissadata.kettle.profiler.support;

import com.melissadata.kettle.profiler.MDProfilerMeta;

public interface MDTab {
	/**
	 * Called to update tab controls when there is a change to advanced configuration
	 */
	void advancedConfigChanged();

	/**
	 * Called to return the tab control settings to the configuration data
	 *
	 * @param data
	 */
	void getData(MDProfilerMeta meta);

	/**
	 * @return The URL for help information
	 */
	String getHelpURLKey();

	/**
	 * Called to initialize tab controls with configuration data
	 *
	 * @param data
	 */
	boolean init(MDProfilerMeta meta);
}
