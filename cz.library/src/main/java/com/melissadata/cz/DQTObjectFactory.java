package com.melissadata.cz;

import java.io.File;

import com.melissadata.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.i18n.BaseMessages;

/**
 * Class used to manage the loading of native objects
 */
public class DQTObjectFactory {

	private static Class<?> PKG = DQTObjectFactory.class;

	private static final String osProp = System.getProperty("os.name");
	private static final String osArch = System.getProperty("os.arch");

	private static  LogChannelInterface log = new LogChannel("DQTObjectFactory");
	

	/**
	 * Create a new instance of the License object
	 * 
	 * @return mdLicense object
	 */
	public static mdLicense newLicense() throws DQTObjectException {
		// Make sure it is loaded first
		checkLicense();

		// Create the object
		return new mdLicense();

	}

	/**
	 * Make sure the license object libraries are loaded correctly
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkLicense() throws DQTObjectException {
		checkLibrary("mdLicense");
		checkLibrary("mdLicenseJavaWrapper");

	}
	

	/**
	 * Create a new instance of the address object
	 * 
	 * @return mdAddress object
	 * @throws DQTObjectException
	 */
	public static mdAddr newAddr() throws DQTObjectException {
		// Make sure it is loaded first
		checkAddr();

		// Create the object
		return new mdAddr();
	}

	/**
	 * Create a new instance of the global address object
	 *
	 * @return mdGlobalAddress object
	 * @throws DQTObjectException
	 */
	public static mdGlobalAddr newGlobalAddr() throws DQTObjectException {
		// Make sure it is loaded first
		checkGlobalAddr();

		// Create the object
		return new mdGlobalAddr();
	}

	/**
	 * Create a new instance of the zip object
	 * 
	 * @return mdZip object
	 * @throws DQTObjectException
	 */
	public static mdZip newZip() throws DQTObjectException {
		// Make sure it is loaded first
		checkAddr();

		// Create the object
		return new mdZip();
	}

	/**
	 * Make sure all of the address object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkAddr() throws DQTObjectException {
		checkLibrary("mdAddr");
		checkLibrary("mdAddrJavaWrapper");
	}

	/**
	 * Make sure all of the global address object is loaded
	 *
	 * @throws DQTObjectException
	 */
	public static void checkGlobalAddr() throws DQTObjectException {
		checkLibrary("mdGeo");
		checkLibrary("mdAddr");
		checkLibrary("mdRightFielder");
		checkLibrary("mdGlobalAddr");
		checkLibrary("mdGlobalAddrJavaWrapper");
	}

	/**
	 * Create a new instance of the email object
	 * 
	 * @return mdEmail object
	 * @throws DQTObjectException
	 */
	public static mdEmail newEmail() throws DQTObjectException {
		// Make sure it is loaded first
		checkEmail();

		// Create the object
		return new mdEmail();
	}

	/**
	 * Make sure all of the email object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkEmail() throws DQTObjectException {
		checkLibrary("mdEmail");
		checkLibrary("mdEmailJavaWrapper");
	}
	
	/**
	 * Create a new instance of the cleanser object
	 * 
	 * @return mdCleanser object
	 * @throws DQTObjectException
	 */
	public static mdCleanser newCleanser() throws DQTObjectException {
		// Make sure it is loaded first
		checkCleanser();

		// Create the object
		return new mdCleanser();
	}

	/**
	 * Make sure all of the Cleanser object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkCleanser() throws DQTObjectException {
		checkLibrary("mdCleanser");
		checkLibrary("mdCleanserJavaWrapper");
	}

	/**
	 * Create a new instance of the geo coder object
	 * 
	 * @return mdGeoCoder object
	 * @throws DQTObjectException
	 */
	public static mdGeo newGeo() throws DQTObjectException {
		// Make sure it is loaded first
		checkGeo();

		// Create the object
		return new mdGeo();
	}

	/**
	 * Make sure all of the geo coder object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkGeo() throws DQTObjectException {
		checkLibrary("mdGeo");
		checkLibrary("mdGeoJavaWrapper");
	}

	/**
	 * Create a new instance of the IP Locator object
	 * 
	 * @return mdIpLocator object
	 * @throws DQTObjectException
	 */
	public static mdIpLocator newIpLocator() throws DQTObjectException {
		// Make sure it is loaded first
		checkIpLocator();

		// Create the object
		return new mdIpLocator();
	}

	/**
	 * Make sure all of the IP Locator object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkIpLocator() throws DQTObjectException {
		checkLibrary("mdIpLocator");
		checkLibrary("mdIpLocatorJavaWrapper");
	}

	/**
	 * Create a new instance of the name object
	 * 
	 * @return mdName object
	 * @throws DQTObjectException
	 */
	public static mdName newName() throws DQTObjectException {
		// Make sure it is loaded first
		checkName();

		// Create the object
		return new mdName();
	}

	/**
	 * Make sure all of the name object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkProfiler() throws DQTObjectException {
		checkLibrary("mdProfiler");
		checkLibrary("mdProfilerJavaWrapper");
	}

	/**
	 * Create a new instance of the name object
	 * 
	 * @return mdProfiler object
	 * @throws DQTObjectException
	 */
	public static mdProfiler newProfiler() throws DQTObjectException {
		// Make sure it is loaded first
		checkProfiler();

		// Create the object
		return new mdProfiler();
	}

	/**
	 * Make sure all of the name object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkName() throws DQTObjectException {
		checkLibrary("mdName");
		checkLibrary("mdNameJavaWrapper");
	}

	/**
	 * Create a new instance of the phone object
	 * 
	 * @return mdPhone object
	 * @throws DQTObjectException
	 */
	public static mdPhone newPhone() throws DQTObjectException {
		// Make sure it is loaded first
		checkPhone();

		// Create the object
		return new mdPhone();
	}

	/**
	 * Make sure all of the name object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkPhone() throws DQTObjectException {
		checkLibrary("mdPhone");
		checkLibrary("mdPhoneJavaWrapper");
	}

	/**
	 * Create a new instance of the Matchup MatchcodeList object
	 * 
	 * @return mdMUMatchcodeList object
	 * @throws DQTObjectException
	 */
	public static mdMUMatchcodeList newMatchcodeList() throws DQTObjectException {
		// Make sure it is loaded first
		checkMatchup();

		// Create the object
		return new mdMUMatchcodeList();
	}

	/**
	 * Create a new instance of the Matchup Read/Write object
	 * 
	 * @return mdMUReadWrite object
	 * @throws DQTObjectException
	 */
	public static mdMUReadWrite newMatchupReadWrite() throws DQTObjectException {
		// Make sure it is loaded first
		checkMatchup();

		// Create the object
		return new mdMUReadWrite();
	}

	/**
	 * Create a new instance of the Matchup Hybrid object
	 * 
	 * @return mdMUHybrid object
	 * @throws DQTObjectException
	 */
	public static mdMUHybrid newMatchupHybrid() throws DQTObjectException {
		// Make sure it is loaded first
		checkMatchup();

		// Create the object
		return new mdMUHybrid();
	}

	/**
	 * Create a new instance of the Matchcode object
	 * 
	 * @return mdMUMatchcode object
	 * @throws DQTObjectException
	 */
	public static mdMUMatchcode newMatchcode() throws DQTObjectException {
		// Make sure it is loaded first
		checkMatchup();

		// Create the object
		return new mdMUMatchcode();
	}

	/**
	 * Make sure all of the matchup object is loaded
	 * 
	 * @throws DQTObjectException
	 */
	public static void checkMatchup() throws DQTObjectException {
		if (Const.isLinux())
			checkLibrary("mdMatchup");
		else
			checkLibrary("mdMatchUp");

		checkLibrary("mdMatchUpJavaWrapper");
	}

	/**
	 * Make sure the libraries for an object exist and are loaded
	 * 
	 * @param name name of the object to load.
	 * @throws DQTObjectException
	 */
	public static void checkLibrary(String name) throws DQTObjectException {

		String fileSep = System.getProperty("file.separator");
		String prefix = "";

		// Base object directory
		File dir = null;
		dir = new File(Const.getKettleDirectory() + fileSep + "MD");
		if (dir.exists()) {
			// If this dir exists it means we are running in spoon,kitchen, or pan
			// so we need architecture
			dir = new File(dir, (osArch.contains("64") ? "64_bit" : "32_bit"));
		} else {
			// If it did not exist we are running in a hadoop environment
			// So only one set of object files will exist.
			dir = null;
		}

		// operating system suffix
		String suffix = osProp.contains("Windows") ? ".dll" : ".so"; 

		if (Const.isLinux())
			prefix = "lib";

		if (Const.isOSX())
			prefix = "lib";

		if (Const.isOSX() && name.equals("mdLicenseJavaWrapper")) {
			suffix = ".jnilib";
		}

		File mainLib = null;

		if (dir != null) {
			// primary and wrapper libraries are prefixed with
			// the dir when we run in spoon,kitchen, or pan
			mainLib = new File(dir, prefix + name + suffix);
		} else {
			// If dir is null we are in hadoop env. we do not prefix the file 
			// in this case. The hadoop system copies the cache files to the
			// same dir as the jar files on the node.
			mainLib = new File(prefix + name + suffix);
		}

		// Load them
		try {
			logDebug("Loading : " + mainLib.getAbsolutePath());
			//System.out.println("Loading " + name + " - path = " + mainLib.getAbsolutePath());
			System.load(mainLib.getAbsolutePath());
		} catch (UnsatisfiedLinkError e) {

			String msg = getErrorString("BadDll", mainLib.getAbsolutePath());
			e.printStackTrace();
			throw new DQTObjectException(getErrorString("InitializeService", msg), e);
		}

	}

	/**
	 * Called to set the log level
	 * @param ll the Loging Level
	 */
	public static void setLogLevel(LogLevel ll){
		log.setLogLevel(ll);
	}

	/**
	 * Called to write to the debug log.
	 * @param msg  message to write.
	 */
	private static void logDebug(String msg){
		log.logDebug(msg );
	}

	/**
	 * @param key
	 * @return The Error String
	 */
	private static String getErrorString(String key, String... args) {
		return BaseMessages.getString(PKG, "CZLibrary.Error." + key, args);
	}
}
