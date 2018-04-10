package com.melissadata.kettle.MDSettings;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.MDSettings.MDSettingsData.License;
import com.melissadata.mdAddr;
import com.melissadata.mdIpLocator;
import com.melissadata.mdLicense;
import com.melissadata.mdLicense.TestLicenseResult;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;

import java.io.*;
import java.util.Calendar;
import java.util.Collection;

public class AdvancedConfigInterface {

	public enum ServiceType {
		Local, Web, CVS
	}

	public static String decrypt(String encryptKey) {

		return encryptKey;
	}

	public static String encrypt(String userId) {

		return userId;
	}

	public static String getErrorString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDSettings.Error." + key, args);
	}

	public static String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDSettings." + key, args);
	}

	public static  boolean  settingDone = false;
	private static Class<?> PKG         = AdvancedConfigInterface.class;
	private static String              productList;
	private static int                 geoCoderLevel;// for display info of geoCoder licence level in TestServiceDialog
	public         int                 nTestType;
	private        MDSettingsData      settingsData;
	private        String              menuID;
	private        VariableSpace       space;
	private        LogChannelInterface log;

	public AdvancedConfigInterface() {

		this.log = new LogChannel(this);
		//moveTmp();
		settingsData = new MDSettingsData();
		space = new Variables();
		loadContactZoneProperties();
	}

	public void checkDataPaths() {

		File f;
		// Local data path
		if (!Const.isEmpty(settingsData.realDataPath) && (MDPropTags.MENU_ID_CONTACTVERIFY.equals(getMenuID()) || MDPropTags.MENU_ID_IPLOCATOR.equals(getMenuID()) || MDPropTags.MENU_ID_CLEANSER.equals(getMenuID()))) {

			f = new File(settingsData.realDataPath);
			if (!f.exists() || !f.isDirectory() || !f.canRead()) {
				settingsData.errorList.add("Failed to find required files for local data\n");
			}
		}
		// MatchUp data Path
		if (!Const.isEmpty(settingsData.matchUpDataPath) && MDPropTags.MENU_ID_MATCHUP.equals(getMenuID())) {
			f = new File(settingsData.matchUpDataPath);
			if (!f.exists() || !f.isDirectory() || !f.canRead()) {
				settingsData.errorList.add("Failed to find required files for Match Up data\n");
			}
		}
		// MatchUpGlobal data Path
		if (!Const.isEmpty(settingsData.matchUpDataPath) && MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(getMenuID())) {
			f = new File(settingsData.matchUpDataPath + ".global");
			if (!f.exists() || !f.isDirectory() || !f.canRead()) {
				settingsData.errorList.add("Failed to find required files for Match Up data\n");
			}
		}
		// presort data path
		if (!Const.isEmpty(settingsData.preSortDataPath) && MDPropTags.MENU_ID_PRESORT.equals(getMenuID())) {
			f = new File(settingsData.preSortDataPath);
			if (!f.exists() || !f.isDirectory() || !f.canRead()) {
				settingsData.errorList.add("Failed to find required files for Presort data\n");
			}
		}

		if (!Const.isEmpty(settingsData.cleanserOpertionsPath) && MDPropTags.MENU_ID_CLEANSER.equals(getMenuID())) {
			f = new File(settingsData.cleanserOpertionsPath);
			if (!f.exists() || !f.isDirectory() || !f.canRead()) {
				settingsData.errorList.add("Unable to save to Cleanser Operations Path\n");
			}
		}
	}

	public boolean checkLicences() {

		validateLicense(settingsData.primeLicense, true, false);
		validateLicense(settingsData.trialLicense, true, true);
		return true;
	}

	public void checkWebSettings(String id) {

		if (settingsData.getRequests(id) > settingsData.getRequestLimit(id)) {
			String errorMsg = getString("AdvancedConfigDialog.MaxRequestsWarning", id, String.valueOf(settingsData.getRequestLimit(id)));

			//FIXME change limit
			if (id == MDPropTags.MENU_ID_BUSINESS_CODER) {
				errorMsg = getString("AdvancedConfigDialog.MaxBusCoderRequestsWarning", id, String.valueOf(settingsData.getRequestLimit(id)));
			}

			settingsData.errorList.add(errorMsg + "\n\n");
		}
		if (settingsData.getThreads(id) > settingsData.getThreadLimit(id)) {
			String errorMsg = getString("AdvancedConfigDialog.MaxThreadsWarning", id, String.valueOf(settingsData.getThreadLimit(id)));
			settingsData.errorList.add(errorMsg + "\n\n");
		}
	}

	public void checkWorkPaths(String filePath) {

		String message = "";
		if (settingsData.primeLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_MatchUpObject) || settingsData.trialLicense.products.contains(MDPropTags.MDLICENSE_PRODUCT_MatchUpObject)) {
			if (!Const.isEmpty(filePath)) {
				filePath += File.separator + "Test.txt";
				File tstFile = new File(filePath);
				try {
					BufferedWriter tstWriter = new BufferedWriter(new FileWriter(filePath));
					tstWriter.write("test");
					tstWriter.close();
					tstFile.delete();
				} catch (IOException e) {
					message = e.getMessage();
					message = message.replaceAll("Test.txt", " ");
					settingsData.errorList.add("Match Up work path error:\n" + message);
				}
			}
		}
	}

	public String getIPLocatorURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_IP_LOCATOR_URL, "webIPLocatorURL");
	}

	public void getLicenseValuesFromProperties(String key, License lic) {

		if (key == MDPropTags.TAG_PRIMARY_LICENSE) {
			lic.licenseString = MDProps.getProperty(key, "");
			lic.CustomerID = MDProps.getProperty(MDPropTags.TAG_PRIMARY_ID, "");
			lic.expiration = MDProps.getProperty(MDPropTags.TAG_PRIMARY_EXPIRATION, "");
			lic.retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, "0"));
			lic.products = MDProps.getProperty(MDPropTags.TAG_PRIMARY_PRODUCT, "");
			lic.licCode = MDProps.getProperty(MDPropTags.TAG_PRIMARY_MDAT_LIC_CODE, "");
			lic.testResult = "";
		}
		if (key == MDPropTags.TAG_TRIAL_LICENSE) {
			lic.licenseString = MDProps.getProperty(key, "");
			lic.CustomerID = MDProps.getProperty(MDPropTags.TAG_TRIAL_ID, "");
			lic.expiration = MDProps.getProperty(MDPropTags.TAG_TRIAL_EXPIRATION, "");
			lic.retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RET_VAL, "0"));
			lic.products = MDProps.getProperty(MDPropTags.TAG_TRIAL_PRODUCT, "");
			lic.licCode = MDProps.getProperty(MDPropTags.TAG_TRIAL_MDAT_LIC_CODE, "");
			lic.testResult = "";
		}
	}

	// Local object
	public String getLocalDataPath() {

		return MDProps.getProperty(MDPropTags.TAG_LOCAL_DATA_PATH, "");
	}

	public String getCleanserOperationsPath() {

		return MDProps.getProperty(MDPropTags.TAG_CLEANSER_OPERATION_PATH, Const.getKettleDirectory());
	}

	// Local Match Up object
	public String getLocalDataPathMU() {

		return MDProps.getProperty(MDPropTags.TAG_LOCAL_DATA_PATH_MATCHUP, System.getProperty("user.home") + Const.FILE_SEPARATOR + ".kettle" + Const.FILE_SEPARATOR + "matchup");
	}

	// Local PreSort object
	public String getLocalDataPathPreSort() {

		return MDProps.getProperty(MDPropTags.TAG_LOCAL_DATA_PATH_PRESORT, "");
	}

	public int getRequests(String id) {

		return settingsData.getRequests(id);
	}

	// Common web/appliance
	public int getThreads(String id) {

		return settingsData.getThreads(id);
	}

	public String getMenuID() {

		return menuID;
	}

	public String getMUWorkPath() {

		return MDProps.getProperty(MDPropTags.TAG_WORK_PATH_MATCHUP, "");
	}

	// determine what we are licensed for
	public boolean getProducts(License license) {

		mdLicense mdLicense = null;
		int       retVal    = MDPropTags.MDLICENSE_None;
		geoCoderLevel = 0;
		try {
			mdLicense = DQTObjectFactory.newLicense();
			mdLicense.SetLicense(license.licenseString, "NOVALUE", "NOVALUE");
			mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_Any);
			if ((mdLicense.GetProductOptions().indexOf(MDPropTags.MDLICENSE_OPTS_Enterprise) >= 0) || (mdLicense.GetProductOptions().indexOf(MDPropTags.MDLICENSE_OPTS_Any) >= 0) || (
					mdLicense.GetProduct().indexOf(MDPropTags.MDLICENSE_PRODUCT_Community) >= 0)) {
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_NameObject) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Name : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_AddressObject) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Address : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_CanadianAddon) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Canada : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_RBDIAddon) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_RBDI : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_GeoCoder) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_GeoCode : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_GeoPoint) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_GeoPoint : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_PhoneObject) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Phone : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_EmailObject) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Email : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_GeoCoderCanadianAddon) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_CanadaGeo : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_MatchUpObject) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_MatchUp : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_MatchUpLite) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_MatchUpLite : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_Personator) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Personator : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_Profiler) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Profiler : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_IPLocator : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_SmartMover) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_SmartMover : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_GlobalVerify) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_GlobalVerify : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_PresortObject) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Presort : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_BusinessCoder) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_BusinessCoder : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_Property) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Property : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_Community) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Community : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_Cleanser) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_Cleanser : 0);
				retVal |= (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_PersonatorWorld) == TestLicenseResult.tlicNoError ? MDPropTags.MDLICENSE_PersonatorWorld : 0);
			}

			// get license info
			if (mdLicense.GetLicenseExpirationDate() != "") {
				license.expiration = mdLicense.GetLicenseExpirationDate();
			}
			license.options = mdLicense.GetProductOptions();
			if (license.options.indexOf(MDPropTags.MDLICENSE_OPTS_NonExpiringDB) >= 0) {
				license.isNonExpiring = true;
			}

			license.CustomerID = mdLicense.GetSerialNumber();
			license.products = mdLicense.GetProduct();

			// to get which geoproducts are enabled uses a linux like
			// permissions number system
			if (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_GeoCoder) == TestLicenseResult.tlicNoError) {
				geoCoderLevel += 1;
			}
			if (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_GeoPoint) == TestLicenseResult.tlicNoError) {
				geoCoderLevel += 2;
			}
			if (mdLicense.TestLicense(MDPropTags.MDLICENSE_PRODUCT_GeoCoderCanadianAddon) == TestLicenseResult.tlicNoError) {
				geoCoderLevel += 4;
			}
			license.geoLevel = geoCoderLevel;
		} catch (Throwable t) {
			// For debug purposes
			log.logError("Error getting licensed products " + t.getMessage());
			t.printStackTrace(System.err);
			retVal = MDPropTags.MDLICENSE_None;
		} finally {
			if (mdLicense != null) {
				mdLicense.delete();
			}
		}
		license.retVal = retVal;

		// set mu flags
		if ((retVal & MDPropTags.MDLICENSE_MatchUp) != 0) {
			settingsData.muEnterprise = true;
			settingsData.muLite = false;
			settingsData.muCommunity = false;
		} else if ((retVal & MDPropTags.MDLICENSE_MatchUpLite) != 0) {
			settingsData.muEnterprise = false;
			settingsData.muLite = true;
			settingsData.muCommunity = false;
		} else {
			settingsData.muEnterprise = false;
			settingsData.muLite = false;
			settingsData.muCommunity = true;
		}
		if (retVal == MDPropTags.MDLICENSE_None) {
			return false;
		} else {
			return true;
		}
	}

	public String getRBDIndicatorURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_RBDI_URL, "RBDIndicatorURL");
	}

	public String getSendLicenceURL() {
		// TODO Leave this in until we determine how to check for updates
		return MDProps.getProperty(MDPropTags.TAG_WEB_SEND_LICENCE_URL, "WebUpdateURL");
	}

	public MDSettingsData getSettingsData() {

		return settingsData;
	}

	public VariableSpace getSpace() {

		return space;
	}

	public String getWebAddressVerifierURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_ADDRESS_VERIFIER_URL, "AddressVerifierURL");
	}

	public String getWebBusCoderURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_BUSINESS_CODER_URL, "BusinessCoderURL");
	}

	public String getWebCCOAURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_CCOA_URL, "CCOAURL");
	}

	public String getWebEmailVerifierURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_EMAIL_VERIFIER_URL, "EmailVerifierURL");
	}

	public String getWebGeoCoderURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_GEO_CODER_URL, "GeoCoderURL");
	}

	public String getWebGlobalAddressURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_GLOBAL_ADDRESS_URL, "GlobalAddressURL");
	}

	public String getWebGlobalEmailURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_GLOBAL_EMAIL_URL, "GlobalEmaiURL");
	}

	public String getWebGlobalNameURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_GLOBAL_NAME_URL, "GlobalNameURL");
	}

	public String getWebGlobalPhoneURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_GLOBAL_PHONE_URL, "GlobalPhoneURL");
	}

	public String getWebNameParserURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_NAME_PARSER_URL, "NameParserURL");
	}

	public String getWebNCOAURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_NCOA_URL, "NCOURL");
	}

	public String getWebPersonatorURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_PERSONATOR_URL, "PersonatorURL");
	}

	public String getWebPersonatorWorldURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_PERSONATOR_WORLD_URL, "PersonatorWorldURL");
	}

	public String getWebPhoneVerifierURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_PHONE_VERIFIER_URL, "PhoneVerifierURL");
	}

	public String getWebPropertyURL() {

		return MDProps.getProperty(MDPropTags.TAG_WEB_PROPERTY_URL, "PersonatorURL");
	}

	public String getWebProxyHost() {

		return settingsData.realProxyHost;
	}

	public String getWebProxyPass() {

		String pass = settingsData.realProxyPass;
		return pass;
	}

	public int getWebProxyPort() {

		int port = settingsData.realProxyPort == 0 ? 8080 : settingsData.realProxyPort;
		return port;
	}

	public String getWebProxyUser() {

		String usr = settingsData.realProxyUser;
		return usr;
	}

	public int getWebRetries(String id) {

		return settingsData.getRetries(id);
	}

	// Web service
	public int getWebTimeout(String id) {

		return settingsData.getTimeout(id);
	}

	public boolean isCVSAbortOnError() {

		return settingsData.cvsAbortOnError;
	}

	public boolean isCVSFailover() {

		return settingsData.cvsFailover;
	}

	public boolean isRetryAppliance() {

		return Boolean.valueOf(MDProps.getProperty(MDPropTags.TAG_RETRY_APPLIANCE, "true"));
	}

	public boolean isWebAbortOnError() {

		return settingsData.webAbortOnError;
	}

	/**
	 * Caled to save the global properties
	 *
	 * @throws KettleException
	 */
	public void saveGlobal() throws KettleException {

		// Make sure they are persisted
		try {
			MDProps.save();
		} catch (IOException e) {
			MDProps.revert();
			throw new KettleException(BaseMessages.getString(PKG, "MDCheckDialog.AdvancedConfigurationDialog.ProblemSavingGlobal"), e);
		}
	}

	public void savePrimeToReg(String licenseString) throws IOException {

		if (Const.isWindows()) {
			// Assign value of HKEY_LOCAL_MACHINE\SOFTWARE\Classes\CZlic\licval =
			if (!Const.isEmpty(licenseString)) {
				Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Melissa Data\\CZlic", "licval", licenseString);
			}
		} else if (Const.isLinux()) {
			String      sep         = System.getProperty("file.separator");
			File        launcherDir = new File(System.getProperty("user.home"), ".local" + sep + "share" + sep + "ContactZone");
			File        insProp     = new File(launcherDir, ".installed");
			PrintWriter out         = new PrintWriter(new BufferedWriter(new FileWriter(insProp)));
			out.println("license=" + licenseString);
			out.flush();
			out.close();
		}
	}

	public void saveTrialToReg(String licenseString) {

		if (Const.isWindows()) {
			// Assign value of HKEY_LOCAL_MACHINE\SOFTWARE\Classes\CZlic\triallicval
			if (!Const.isEmpty(licenseString)) {
				Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Melissa Data\\CZlic", "triallicval", licenseString);
			}
		}
	}

	public void setComplete() {

		settingDone = true;
	}

	public void setCVSAbortOnError(boolean b) {

		settingsData.cvsAbortOnError = b;
	}

	public void setCVSFailover(boolean b) {

		settingsData.cvsFailover = b;
	}

	public void setCVSRetries(int s) {

		settingsData.realCVSRetries = s;
	}

	public void setCVSServerURL(String s) {

		MDProps.setProperty(MDPropTags.TAG_CVS_SERVER_URL, s);
	}

	public void setCVSTimeout(int s) {

		settingsData.realCVSTimeout = s;
	}

	public void setDataValues() {

		if (Const.isLinux() && isMDCheckInstalled()) {
			log.logBasic("Running in Linux initalize Ip and Address objects");
			initializeIP();
			initializeAddr();
		}

		// Match Up
		settingsData.matchUpDataPath = MDProps.getProperty(MDPropTags.TAG_LOCAL_DATA_PATH_MATCHUP, "");
		settingsData.matchUpWorkPath = MDProps.getProperty(MDPropTags.TAG_WORK_PATH_MATCHUP, "");
		getLicenseValues();
		setDefaultValues();
		setProperties();
		try {
			saveGlobal();
		} catch (KettleException e) {
			log.logError("Warnning - Unable to save mdProps.prop file " + e.getCause());
		}
	}

	private boolean isMDCheckInstalled() {

		if (Props.isInitialized()) {
			return Props.getInstance().getProperty(MDPropTags.TAG_MDCHECK_VERSION) != null;
		} else {
			log.logError("Error checking MDCheck install: Props not initialized.");
		}

		return true;
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefaultValues() {

		settingsData.productList = "";
		settingsData.realDataPath = getLocalDataPath();
		settingsData.matchUpDataPath = getLocalDataPathMU();
		settingsData.preSortDataPath = getLocalDataPathPreSort();
		settingsData.matchUpWorkPath = getMUWorkPath();
		settingsData.cleanserOpertionsPath = getCleanserOperationsPath();
		// Common web/appliance defaults
		// MDCheck
		settingsData.setThreads(MDPropTags.MENU_ID_CONTACTVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_CONTACTVERIFY_THREADS, "5")));
		settingsData.setRequests(MDPropTags.MENU_ID_CONTACTVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_CONTACTVERIFY_REQUESTS, "100")));
		settingsData.setTimeout(MDPropTags.MENU_ID_CONTACTVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_CONTACTVERIFY_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_CONTACTVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_CONTACTVERIFY_WEB_RETRIES, "200")));
		// SmartMover
		settingsData.setThreads(MDPropTags.MENU_ID_SMARTMOVER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_SMARTMOVER_THREADS, "5")));
		settingsData.setRequests(MDPropTags.MENU_ID_SMARTMOVER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_SMARTMOVER_REQUESTS, "100")));
		settingsData.setTimeout(MDPropTags.MENU_ID_SMARTMOVER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_SMARTMOVER_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_SMARTMOVER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_SMARTMOVER_WEB_RETRIES, "200")));
		// GlobalVerify
		settingsData.setThreads(MDPropTags.MENU_ID_GLOBALVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_THREADS, "2")));
		settingsData.setRequests(MDPropTags.MENU_ID_GLOBALVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_REQUESTS, "10")));
		settingsData.setTimeout(MDPropTags.MENU_ID_GLOBALVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_GLOBALVERIFY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_GLOBALVERIFY_WEB_RETRIES, "200")));
		// IPLocator
		settingsData.setThreads(MDPropTags.MENU_ID_IPLOCATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_IPLOCATOR_THREADS, "5")));
		settingsData.setRequests(MDPropTags.MENU_ID_IPLOCATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_IPLOCATOR_REQUESTS, "100")));
		settingsData.setTimeout(MDPropTags.MENU_ID_IPLOCATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_IPLOCATOR_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_IPLOCATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_IPLOCATOR_WEB_RETRIES, "200")));
		// Property Service
		settingsData.setThreads(MDPropTags.MENU_ID_PROPERTY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PROPERTY_THREADS, "5")));
		settingsData.setRequests(MDPropTags.MENU_ID_PROPERTY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PROPERTY_REQUESTS, "100")));
		settingsData.setTimeout(MDPropTags.MENU_ID_PROPERTY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PROPERTY_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_PROPERTY, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PROPERTY_WEB_RETRIES, "200")));
		// Personator 
		settingsData.setThreads(MDPropTags.MENU_ID_PERSONATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_THREADS, "5")));
		settingsData.setRequests(MDPropTags.MENU_ID_PERSONATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_REQUESTS, "100")));
		settingsData.setTimeout(MDPropTags.MENU_ID_PERSONATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_PERSONATOR, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WEB_RETRIES, "200")));
		// Personator World
		settingsData.setThreads(MDPropTags.MENU_ID_PERSONATOR_WORLD, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WORLD_THREADS, "5")));
		settingsData.setRequests(MDPropTags.MENU_ID_PERSONATOR_WORLD, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WORLD_REQUESTS, "100")));
		settingsData.setTimeout(MDPropTags.MENU_ID_PERSONATOR_WORLD, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WORLD_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_PERSONATOR_WORLD, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PERSONATOR_WORLD_WEB_RETRIES, "200")));
		// Business Coder
		settingsData.setThreads(MDPropTags.MENU_ID_BUSINESS_CODER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_BUSINESS_CODER_THREADS, "5")));
		settingsData.setRequests(MDPropTags.MENU_ID_BUSINESS_CODER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_BUSINESS_CODER_REQUESTS, "1")));
		settingsData.setTimeout(MDPropTags.MENU_ID_BUSINESS_CODER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_BUSINESS_CODER_WEB_TIMEOUT, "45")));
		settingsData.setRetries(MDPropTags.MENU_ID_BUSINESS_CODER, Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_BUSINESS_CODER_WEB_RETRIES, "100")));

		// Web service defaults
		settingsData.webAbortOnError = Boolean.parseBoolean(MDProps.getProperty(MDPropTags.TAG_WEB_OPT_ABORT, "false"));
		settingsData.realProxyHost = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_HOST, "");
		settingsData.realProxyPort = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PORT, "0"));
		settingsData.realProxyUser = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_USER, "");
		String pas = MDProps.getProperty(MDPropTags.TAG_WEB_PROXY_PASS, "");
		if (!Const.isEmpty(pas) && ((pas.length() % 16) == 0)) {
			settingsData.realProxyPass = decrypt(pas);
		} else {
			settingsData.realProxyPass = pas;
		}
		// Local appliance defaults
		settingsData.cvsFailover = Boolean.parseBoolean(MDProps.getProperty(MDPropTags.TAG_CVS_OPT_FAILOVER, "false"));
		settingsData.realCVSTimeout = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_CVS_TIMEOUT, "45"));
		settingsData.realCVSRetries = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_CVS_RETRIES, "5"));
		settingsData.cvsAbortOnError = Boolean.parseBoolean(MDProps.getProperty(MDPropTags.TAG_CVS_OPT_ABORT, "false"));
		settingsData.retryAppliance = Boolean.valueOf(MDProps.getProperty(MDPropTags.TAG_RETRY_APPLIANCE, "true"));
		settingsData.failoverInterval = Integer.valueOf(MDProps.getProperty(MDPropTags.TAG_FAILOVER_INTERVAL, "600000"));
		settingsData.serverURL = MDProps.getProperty(MDPropTags.TAG_CVS_SERVER_URL, "");
		settingsData.webEncoding = MDProps.getProperty(MDPropTags.TAG_WEB_ENCODING, "ISO-8859-1");
	}

	public void setLocalDataPath(String s) {

		MDProps.setProperty(MDPropTags.TAG_LOCAL_DATA_PATH, s);
	}

	public void setLocalDataPathMU(String s) {

		MDProps.setProperty(MDPropTags.TAG_LOCAL_DATA_PATH_MATCHUP, s);
	}

	public void setLocalDataPathPreSort(String s) {

		MDProps.setProperty(MDPropTags.TAG_LOCAL_DATA_PATH_PRESORT, s);
	}

	public void setCleanserOperationsPath(String s) {

		MDProps.setProperty(MDPropTags.TAG_CLEANSER_OPERATION_PATH, s);
	}

	public void setMaxRequests(String id, int i) {

		settingsData.setRequests(id, i);
	}

	public void setMaxThreads(String id, int i) {

		settingsData.setThreads(id, i);
	}

	public void setMenuId(String id) {

		menuID = id;
	}

	public void setMUWorkPath(String s) {

		MDProps.setProperty(MDPropTags.TAG_WORK_PATH_MATCHUP, s);
	}

	public void setProperties() {

		MDProps.setProperty(MDPropTags.TAG_PRIMARY_LICENSE, settingsData.primeLicense.licenseString);
		//MDProps.setProperty(MDPropTags.TAG_PRIMARY_LICENSE_ENTERPRISE, String.valueOf(settingsData.primeLicense.isEnterprise));
		MDProps.setProperty(MDPropTags.TAG_PRIMARY_ID, settingsData.primeLicense.CustomerID);
		MDProps.setProperty(MDPropTags.TAG_PRIMARY_RET_VAL, String.valueOf(settingsData.primeLicense.retVal));
		MDProps.setProperty(MDPropTags.TAG_PRIMARY_TEST_RESULT, settingsData.primeLicense.testResult);
		MDProps.setProperty(MDPropTags.TAG_PRIMARY_EXPIRATION, settingsData.primeLicense.expiration);
		MDProps.setProperty(MDPropTags.TAG_PRIMARY_PRODUCT, settingsData.primeLicense.products);
		MDProps.setProperty(MDPropTags.TAG_PRIMARY_MDAT_LIC_CODE, settingsData.primeLicense.licCode);
		MDProps.setProperty(MDPropTags.TAG_TRIAL_LICENSE, settingsData.trialLicense.licenseString);
		// This is for internal usage without Pentaho Enterprise
		if (settingsData.trialLicense.licenseString.equals("MD_92688")) {
			MDProps.setProperty(MDPropTags.TAG_MELISSADATA_SPECIAL_USAGE, "true");
		}
		// prod list & geoLevel
		geoCoderLevel = settingsData.primeLicense.geoLevel >= settingsData.trialLicense.geoLevel ? settingsData.primeLicense.geoLevel : settingsData.trialLicense.geoLevel;
		MDProps.setProperty(MDPropTags.TAG_GEO_LEVEL, String.valueOf(geoCoderLevel));
		productList = settingsData.primeLicense.products.concat(settingsData.trialLicense.products);
		MDProps.setProperty(MDPropTags.TAG_PRODUCT_LIST, productList);
		// MU type
		MDProps.setProperty(MDPropTags.TAG_MATCHUP_COMMUNITY, String.valueOf(settingsData.muCommunity));
		MDProps.setProperty(MDPropTags.TAG_MATCHUP_LITE, String.valueOf(settingsData.muLite));
		MDProps.setProperty(MDPropTags.TAG_MATCHUP_ENTERPRISE, String.valueOf(settingsData.muEnterprise));
		//MDCheck
		MDProps.setProperty(MDPropTags.TAG_CONTACTVERIFY_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_CONTACTVERIFY)));
		MDProps.setProperty(MDPropTags.TAG_CONTACTVERIFY_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_CONTACTVERIFY)));
		MDProps.setProperty(MDPropTags.TAG_CONTACTVERIFY_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_CONTACTVERIFY)));
		MDProps.setProperty(MDPropTags.TAG_CONTACTVERIFY_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_CONTACTVERIFY)));
		//Smart Mover
		MDProps.setProperty(MDPropTags.TAG_SMARTMOVER_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_SMARTMOVER)));
		MDProps.setProperty(MDPropTags.TAG_SMARTMOVER_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_SMARTMOVER)));
		MDProps.setProperty(MDPropTags.TAG_SMARTMOVER_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_SMARTMOVER)));
		MDProps.setProperty(MDPropTags.TAG_SMARTMOVER_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_SMARTMOVER)));
		//GlobalVerify
		MDProps.setProperty(MDPropTags.TAG_GLOBALVERIFY_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_GLOBALVERIFY)));
		MDProps.setProperty(MDPropTags.TAG_GLOBALVERIFY_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_GLOBALVERIFY)));
		MDProps.setProperty(MDPropTags.TAG_GLOBALVERIFY_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_GLOBALVERIFY)));
		MDProps.setProperty(MDPropTags.TAG_GLOBALVERIFY_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_GLOBALVERIFY)));
		//IPLocator
		MDProps.setProperty(MDPropTags.TAG_IPLOCATOR_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_IPLOCATOR)));
		MDProps.setProperty(MDPropTags.TAG_IPLOCATOR_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_IPLOCATOR)));
		MDProps.setProperty(MDPropTags.TAG_IPLOCATOR_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_IPLOCATOR)));
		MDProps.setProperty(MDPropTags.TAG_IPLOCATOR_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_IPLOCATOR)));
		//Property Service
		MDProps.setProperty(MDPropTags.TAG_PROPERTY_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_PROPERTY)));
		MDProps.setProperty(MDPropTags.TAG_PROPERTY_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_PROPERTY)));
		MDProps.setProperty(MDPropTags.TAG_PROPERTY_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_PROPERTY)));
		MDProps.setProperty(MDPropTags.TAG_PROPERTY_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_PROPERTY)));

		// Personator
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_PERSONATOR)));
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_PERSONATOR)));
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_PERSONATOR)));
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_PERSONATOR)));
		// Personator World
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_WORLD_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_PERSONATOR_WORLD)));
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_WORLD_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_PERSONATOR_WORLD)));
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_WORLD_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_PERSONATOR_WORLD)));
		MDProps.setProperty(MDPropTags.TAG_PERSONATOR_WORLD_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_PERSONATOR_WORLD)));
		// Business Coder
		MDProps.setProperty(MDPropTags.TAG_BUSINESS_CODER_THREADS, String.valueOf(getThreads(MDPropTags.MENU_ID_BUSINESS_CODER)));
		MDProps.setProperty(MDPropTags.TAG_BUSINESS_CODER_REQUESTS, String.valueOf(getRequests(MDPropTags.MENU_ID_BUSINESS_CODER)));
		MDProps.setProperty(MDPropTags.TAG_BUSINESS_CODER_WEB_RETRIES, String.valueOf(settingsData.getRetries(MDPropTags.MENU_ID_BUSINESS_CODER)));
		MDProps.setProperty(MDPropTags.TAG_BUSINESS_CODER_WEB_TIMEOUT, String.valueOf(settingsData.getTimeout(MDPropTags.MENU_ID_BUSINESS_CODER)));

		MDProps.setProperty(MDPropTags.TAG_WEB_PROXY_HOST, settingsData.realProxyHost);
		MDProps.setProperty(MDPropTags.TAG_WEB_PROXY_PORT, String.valueOf(settingsData.realProxyPort));
		MDProps.setProperty(MDPropTags.TAG_WEB_PROXY_USER, settingsData.realProxyUser);
		if (!Const.isEmpty(settingsData.realProxyPass)) {
			MDProps.setProperty(MDPropTags.TAG_WEB_PROXY_PASS, encrypt(settingsData.realProxyPass));
		} else {
			MDProps.setProperty(MDPropTags.TAG_WEB_PROXY_PASS, settingsData.realProxyPass);
		}
		MDProps.setProperty(MDPropTags.TAG_WEB_OPT_ABORT, String.valueOf(settingsData.webAbortOnError));
		MDProps.setProperty(MDPropTags.TAG_CVS_SERVER_URL, settingsData.serverURL);
		MDProps.setProperty(MDPropTags.TAG_WEB_ENCODING, settingsData.webEncoding);
		MDProps.setProperty(MDPropTags.TAG_CVS_TIMEOUT, String.valueOf(settingsData.realCVSTimeout));
		MDProps.setProperty(MDPropTags.TAG_CVS_RETRIES, String.valueOf(settingsData.realCVSRetries));
		MDProps.setProperty(MDPropTags.TAG_CVS_OPT_ABORT, String.valueOf(settingsData.cvsAbortOnError));
		MDProps.setProperty(MDPropTags.TAG_CVS_OPT_FAILOVER, String.valueOf(settingsData.cvsFailover));
		MDProps.setProperty(MDPropTags.TAG_RETRY_APPLIANCE, String.valueOf(settingsData.retryAppliance));
		MDProps.setProperty(MDPropTags.TAG_FAILOVER_INTERVAL, String.valueOf(settingsData.failoverInterval));
		MDProps.setProperty(MDPropTags.TAG_LOCAL_DATA_PATH, settingsData.realDataPath);
		MDProps.setProperty(MDPropTags.TAG_CLEANSER_OPERATION_PATH, settingsData.cleanserOpertionsPath);
		MDProps.setProperty(MDPropTags.TAG_LOCAL_DATA_PATH_PRESORT, settingsData.preSortDataPath);
		MDProps.setProperty(MDPropTags.TAG_LOCAL_DATA_PATH_MATCHUP, settingsData.matchUpDataPath);
		MDProps.setProperty(MDPropTags.TAG_WORK_PATH_MATCHUP, settingsData.matchUpWorkPath);
		MDProps.setProperty(MDPropTags.TAG_CLEANSER_OPERATION_PATH, settingsData.cleanserOpertionsPath);
	}

	public void setWebAbortOnError(boolean b) {

		settingsData.webAbortOnError = b;
	}

	public void setWebProxyHost(String s) {

		settingsData.realProxyHost = s;
	}

	public void setWebProxyPass(String pass) {

		settingsData.realProxyPass = pass;
	}

	public void setWebProxyPort(int s) {

		settingsData.realProxyPort = s;
	}

	public void setWebProxyUser(String user) {

		settingsData.realProxyUser = user;
	}

	public void setWebRetries(String id, int s) {

		settingsData.setRetries(id, s);
	}

	public void setWebTimeout(String id, int s) {

		settingsData.setTimeout(id, s);
	}

	// method subtracts 1 from month to accommodate
	// Calendar starting at 0
	public Calendar stringToDate(String sDate) {

		Calendar date       = Calendar.getInstance();
		String[] YMD        = new String[3];
		int      year       = 2011;
		int      month      = 5;
		int      dayOfMonth = 15;
		if (sDate != null) {
			if (sDate.indexOf("-") == 4) {
				YMD = sDate.split("-");
				year = Integer.parseInt(YMD[0]);
				month = Integer.parseInt(YMD[1]) - 1;
				dayOfMonth = Integer.parseInt(YMD[2]);
				date.set(year, month, dayOfMonth);
			} else if (sDate.indexOf("-") == 2) {
				YMD = sDate.split("-");
				month = Integer.parseInt(YMD[0]) - 1;
				dayOfMonth = Integer.parseInt(YMD[1]);
				year = Integer.parseInt(YMD[2]);
				date.set(year, month, dayOfMonth);
			}
		}
		if (sDate.equalsIgnoreCase("NEVER")) {
			date.add(Calendar.MONTH, 1);
		}
		return date;
	}

	public void validateLicense(License objLicense, boolean validate, boolean isTrialLic) {

		mdLicense mdLicense = null;
		if (!Const.isEmpty(objLicense.licenseString)) {
			String products = "";
			if (getProducts(objLicense)) {
				products = objLicense.products;
				try {
					mdLicense = DQTObjectFactory.newLicense();
					mdLicense.SetLicense(objLicense.licenseString, "NOVALUE", "NOVALUE");
					objLicense.testResult = mdLicense.TestLicense(products).toString();
					if (objLicense.testResult == TestLicenseResult.tlicNoError.toString()) {
						objLicense.CustomerID = mdLicense.GetSerialNumber();
						objLicense.expiration = mdLicense.GetLicenseExpirationDate();
						objLicense.options = mdLicense.GetProductOptions();
						if (Const.isWindows()) {
							objLicense.licCode = mdLicense.GetCustomerCode();
						}
						if (objLicense.options.indexOf(MDPropTags.MDLICENSE_OPTS_NonExpiringDB) >= 0) {
							objLicense.isNonExpiring = true;
						}
					}
				} catch (Throwable t) {
					System.out.println("Error validating: " + t.getMessage());
				} finally {
					if (mdLicense != null) {
						mdLicense.delete();
					}
				}
			} else {
				objLicense.testResult = TestLicenseResult.tlicInvalidLicense.toString();
			}
			if (isExpired(objLicense.expiration)) {
				objLicense.testResult = TestLicenseResult.tlicLicenseExpired.toString();
			}
			if (validate && (!Const.isEmpty(objLicense.licenseString))) {
				String msg;
				if ((objLicense.testResult == TestLicenseResult.tlicInvalidLicense.toString()) || (objLicense.testResult == TestLicenseResult.tlicInvalidProduct.toString())) {
					if (isTrialLic) {
						msg = " Trial License is Invalid or Missing\n";
					} else {
						msg = " Primary License is Invalid or Missing\n";
					}
					settingsData.errorList.add(msg);
				} else if (objLicense.testResult == TestLicenseResult.tlicLicenseExpired.toString()) {
					if (isTrialLic) {
						msg = " Trial License is Expired\n";
					} else {
						msg = " Primary License is Expired\n";
					}
					settingsData.warnList.add(msg);
				}
			}
		} else {
			objLicense.CustomerID = "";
			objLicense.options = "";
			objLicense.expiration = "";
			objLicense.geoLevel = 0;
			objLicense.products = "";
			objLicense.retVal = MDPropTags.MDLICENSE_None;
			objLicense.isNonExpiring = false;
			objLicense.licCode = "";
			objLicense.testResult = "tlicInvalidLicense";
		}
	}

	private void getLicenseValues() {

		// get primary license info first
		if (settingsData.primeLicense == null) {
			settingsData.primeLicense = settingsData.new License();
		}
		getLicenseValuesFromProperties(MDPropTags.TAG_PRIMARY_LICENSE, settingsData.primeLicense);
		validateLicense(settingsData.primeLicense, false, false);
		// get trial license info
		settingsData.trialLicense = settingsData.new License();
		getLicenseValuesFromProperties(MDPropTags.TAG_TRIAL_LICENSE, settingsData.trialLicense);
		validateLicense(settingsData.trialLicense, false, true);
		if (Props.isInitialized()) {
			try {
				MDProps.updateCZ();
			} catch (java.io.IOException ioe) {
				log.logError("Error failed to update ContactZone Version : " + ioe.getMessage());
			}
		} else {
			log.logError("Error failed to update ContactZone Version : " + "Props not initialized");
		}
	}

	private void initializeAddr() {

		mdAddr ao           = null;
		String dataFilePath = getLocalDataPath();
		try {
			ao = DQTObjectFactory.newAddr();
			if (!ao.SetLicenseString(MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, ""))) {
				// Handle failure to set license string
				log.logError("Error setting license for Addr OBJ: " + ao.GetErrorString());
			}
			// Set path to US and Canadian data
			ao.SetPathToUSFiles(dataFilePath);
			ao.SetPathToCanadaFiles(dataFilePath);
			// Set other paths
			ao.SetPathToDPVDataFiles(dataFilePath);
			ao.SetPathToLACSLinkDataFiles(dataFilePath);
			ao.SetPathToSuiteLinkDataFiles(dataFilePath);
			ao.SetPathToRBDIFiles(dataFilePath);
			ao.SetPathToSuiteFinderDataFiles(dataFilePath);
			// If a CASS form is to be generated then turn on CASS now
			// ao.SetCASSEnable(addressVerify.getCASSSaveToFile() ? 1 : 0);
			// Initialize it
			mdAddr.ProgramStatus result = ao.InitializeDataFiles();
			if (result != mdAddr.ProgramStatus.ErrorNone) {
				// Handle failure to initialize
			}
		} catch (DQTObjectException e) {
			log.logError("DQT Error - Unable to load Addr : " + e.getMessage());
		}
		if (ao != null) {
			ao.delete();
		}
	}

	private void initializeIP() {

		mdIpLocator ipLoc = null;
		try {
			ipLoc = DQTObjectFactory.newIpLocator();
			ipLoc.SetLicenseString(MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, ""));
			ipLoc.SetPathToIpLocatorFiles(getLocalDataPath());
			ipLoc.InitializeDataFiles().toString();
		} catch (DQTObjectException e) {
			log.logError("DQT Error - Unable to load IpLocator : " + e.getMessage());
		}
		if (ipLoc != null) {
			ipLoc.delete();
		}
	}

	private boolean isExpired(String sDate) {

		Calendar date  = stringToDate(sDate);
		Calendar today = Calendar.getInstance();
		if (date.before(today)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Called to load the global properties Error setting
	 */
	private void loadContactZoneProperties() {

		MDProps.load();
	}

	public static void revertMCfile(String dataPath, boolean isEnterprise) {
		// if we are running community or lite we rewrite the .mc file so they
		// can only use the limited
		// choices of match codes. We allow them to get here so they can see
		// what extras the match code editor offers.
		if (!isEnterprise) {
			File copyOfmcFile   = new File(dataPath + Const.FILE_SEPARATOR + "mdMatchupLite.mc");
			File originalMcFile = new File(dataPath + Const.FILE_SEPARATOR + "mdMatchup.mc");
			try {
				FileUtils.copyFile(copyOfmcFile, originalMcFile);
			} catch (IOException e) {
				System.out.println("IO error revert .mc file. " + e.getMessage());
			}
		}
	}
}
