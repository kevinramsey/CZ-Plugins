package com.melissadata.cz.support;

import org.pentaho.di.trans.step.BaseStepMeta;

/**
 *  Interface fot MDTab item
 */
public interface MDTab {

	/**
	 * Called to initialize tab controls with configuration data
	 * 
	 * @param meta
	 */
	boolean init(BaseStepMeta meta);

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
	 * @param meta
	 */
	void getData(BaseStepMeta meta);

	/**
	 * @return The URL for help information
	 */
	String getHelpURLKey();
}
