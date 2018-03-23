package com.melissadata.kettle.personator.support;

import com.melissadata.kettle.personator.MDPersonatorMeta;



public interface MDTab {

	/**
	 * Called to initialize tab controls with configuration data
	 * 
	 * @param data
	 */
	boolean init(MDPersonatorMeta meta);

	/**
	 * Called to update tab controls when there is a change to advanced configuration
	 */
	void advancedConfigChanged();

	/**
	 * Called to return the tab control settings to the configuration data
	 * 
	 * @param data
	 */
	void getData(MDPersonatorMeta meta);

	/**
	 * @return The URL for help information
	 */
	String getHelpURLKey();

}
