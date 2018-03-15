package com.melissadata.kettle.support;

import com.melissadata.kettle.MDCheckStepData;

public interface MDTab {
	/**
	 * Called to initialize tab controls with configuration data
	 *
	 * @param data
	 */
	boolean init(MDCheckStepData data);

	/**
	 * Called to update tab controls when there is a change to advanced configuration
	 */
	void advancedConfigChanged();

	/**
	 * Called to dispose tab resources
	 */
	void dispose();

	/**
	 * Called to return the tab control settings to the configuration data
	 *
	 * @param data
	 */
	void getData(MDCheckStepData data);

	/**
	 * @return The URL for help information
	 */
	String getHelpURLKey();
}
