package com.melissadata.kettle.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.melissadata.kettle.*;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.CZUtil;

public class MDCustomDefaults {
	// private static Class<?> PKG = MDCustomDefaults.class;
	/**
	 * Called if there is no properties file or it is formated badly.
	 *
	 * @return
	 */
	private static Properties getDefaultDefaults() {
		Properties props = new Properties();
		return props;
	}

	/**
	 * @return The location of the property file
	 *
	 * @throws IOException
	 */
	private static File getDefaultsFile(int chkType) throws IOException {
		File propertyPath = CZUtil.getCZWorkDirectory();
		File propFile = null;
		if ((chkType & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			propFile = new File(propertyPath, CUSTOM_SM_DEFAULTS_FILE);
		} else if ((chkType & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			propFile = new File(propertyPath, CUSTOM_MU_DEFAULTS_FILE);
		} else if ((chkType & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			propFile = new File(propertyPath, CUSTOM_IP_DEFAULTS_FILE);
		} else {
			propFile = new File(propertyPath, CUSTOM_DEFAULTS_FILE);
		}
		return propFile;
	}

	/**
	 * @return The location of the property file
	 */
	private static File getOldDefaultsFile(int chkType) {
		String propertyPath = MDCheckStepData.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		propertyPath = propertyPath.replaceAll("steps.*", "steps");
		File propFile = null;
		if ((chkType & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			propFile = new File(new File(propertyPath), CUSTOM_SM_DEFAULTS_FILE);
		} else if ((chkType & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			propFile = new File(new File(propertyPath), CUSTOM_MU_DEFAULTS_FILE);
		} else if ((chkType & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			propFile = new File(new File(propertyPath), CUSTOM_IP_DEFAULTS_FILE);
		} else {
			propFile = new File(new File(propertyPath), CUSTOM_DEFAULTS_FILE);
		}
		return propFile;
	}
	private static final String			CUSTOM_DEFAULTS_FILE	= "md_custom_defaults.prop";
	private static final String			CUSTOM_SM_DEFAULTS_FILE	= "md_custom_sm_defaults.prop";
	private static final String			CUSTOM_MU_DEFAULTS_FILE	= "md_custom_mu_defaults.prop";
	private static final String			CUSTOM_IP_DEFAULTS_FILE	= "md_custom_ip_defaults.prop";
	private Properties					defaultProps;
	private AdvancedConfigurationMeta advConfMeta;
	private MDCheckDialog mdCheckDialog;
	public static IOException			propException;

	public MDCustomDefaults(AdvancedConfigurationMeta advConfMeta) {
		this.advConfMeta = advConfMeta;
		defaultProps = new Properties();
	}

	public MDCustomDefaults(AdvancedConfigurationMeta advConfMeta, MDCheckDialog mdCheckDialog) {
		this.advConfMeta = advConfMeta;
		this.mdCheckDialog = mdCheckDialog;
		defaultProps = new Properties();
	}

	/**
	 * Called to load data from the properties file
	 */
	public Properties initDefaults(int chkTypes) {
		// Get the properties from the properties file
		Properties props = new Properties();
		InputStream in = null;
		try {
			// Get the properties file (either in the new or old location)
			File propFile = getDefaultsFile(chkTypes);
			if (!propFile.exists()) {
				propFile = getOldDefaultsFile(chkTypes);
			}
			// Read the properties file
			in = new BufferedInputStream(new FileInputStream(propFile));
			props.load(in);
		} catch (IOException e) {
			// Remember that there was a problem loading the properties
			propException = e;
			// Create a default default from hardcoded values
			props = getDefaultDefaults();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
		return props;
	}

	/**
	 * Called to save data to the properties file
	 *
	 * @throws KettleException
	 */
	public void saveDefaults() throws KettleException {
		// clear the properties object
		defaultProps.clear();
		// load the individual properties
		int chkType = mdCheckDialog.saveDefaultsToMeta();
		advConfMeta.saveDefaults(defaultProps);
		// Get the properties file location
		File defaultFile;
		try {
			defaultFile = getDefaultsFile(chkType);
		} catch (IOException e) {
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NoPropFile"), e);
		}
		// Save the new properties
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(defaultFile));
			defaultProps.store(out, "MD User defined output names");
		} catch (IOException e) {
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckDialog.WarningDialog.NoReadPropFile") + defaultFile, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
}
