package com.melissadata.cz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.pentaho.di.core.Const;

/**
 *  Utility class used to retrive ContactZone/MD Pentaho work paths
 */

public class CZUtil {

	/**
	 * The file name that contains the Presort work path.
	 */
	private static final String PS_EXE_PATH = "psexepath.txt";
	/**
	 * Directory holding required files.  Equivalent to .kettle path
	 */
	private static       File   workPath    = null;
	/**
	 * String path to the Presort executible. Read from PS_EXE_PATH
	 */
	private static       String psExePath   = null;

	/**
	 * Retrieves CZ work path.
	 * @return The data path defined in the installation data path file
	 *
	 * @throws IOException
	 */
	public static File getCZWorkDirectory() throws IOException  {

		if (workPath == null) {

			if (!checkClusterPath()) {

				workPath = new File(Const.getKettleDirectory());
			}
		}
		return workPath;
	}

	/**
	 * Checks to see if the application is running on a Hadoop cluster.
	 * Sets the workPath variable if true.
	 *
	 * @return True when the clusterProp file is found indicating running on Hadoop.
	 */
	private static boolean checkClusterPath() {

		File clusterProp = new File("mdProps.prop");

		if (clusterProp.exists()) {
			String path = clusterProp.getAbsolutePath();
			path = path.substring(0, path.lastIndexOf(Const.FILE_SEPARATOR));
			workPath = new File(path);
			return true;
		}
		return false;
	}

	/**
	 * Reads the presort executable path from file.
	 * @return Path string read from PS_EXE_PATH file.
	 * @throws IOException if PS_EXE_PATH file does not exist‼.
	 */
	public static String getPresortExePath() throws IOException {

		if (psExePath == null) {
			BufferedReader czDPReader = new BufferedReader(new FileReader(PS_EXE_PATH));
			try {
				psExePath = czDPReader.readLine();
			} finally {
				czDPReader.close();
			}
		}

		return psExePath;
	}
}
