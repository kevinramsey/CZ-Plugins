package com.melissadata.kettle.MDSettings;

import com.melissadata.cz.CZUtil;
import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.*;
import com.melissadata.cz.support.MDPropTags;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

import java.io.*;
import java.net.URLDecoder;


public class TestService implements IRunnableWithProgress {
	private static Class<?>			PKG									= TestService.class;
	private final static String		TEST_CONFIG_INPUT					= "TEST";
	private final static String		TEST_CONFIG_BUILD					= "BUILDNO";
	private final static String		TEST_CONFIG_EXPIRATION				= "EXPIRE";
	private final static String		SETTING_FILE_NAME					= "md_presortexec_settings.set";
	private static final boolean	TEST_MATCHUP_USING_MATCHCODELIST	= false;
	private AdvancedConfigInterface	acInterface;
	private MDSettingsData			settingData;
	private String					dataFilePath;
	private int						testType;

	public TestService(AdvancedConfigInterface acInterface, int testType) {
		this.acInterface = acInterface;
		settingData = acInterface.getSettingsData();
		this.testType = testType;
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID())) {
			dataFilePath = settingData.preSortDataPath;
		} else if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID())) {
			dataFilePath = settingData.matchUpDataPath;
		}  else if (MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID())) {
			dataFilePath = settingData.matchUpDataPath + ".global";
		}else {
			dataFilePath = settingData.realDataPath;
		}
	}

	public void chkLocal(boolean isUpdating) {
		// Map<String,License> product = settingData.mLicenses;
		String license;
		// test CV components
		if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID()) || isUpdating) {
			license = getLicenseString(MDPropTags.MDLICENSE_Name);
			testNameLocal(license);
			license = getLicenseString(MDPropTags.MDLICENSE_GeoCode);
			testGeoCoderLocal(license);
			license = getLicenseString(MDPropTags.MDLICENSE_Phone);
			testPhoneLocal(license);
			license = getLicenseString(MDPropTags.MDLICENSE_Email);
			testEmailLocal(license);
			license = getLicenseString(MDPropTags.MDLICENSE_Address);
			try {
				testAddressLocal(license);
			} catch (KettleException e) {
				settingData.errorList.add(AdvancedConfigInterface.getErrorString("LocalAddrError", e.toString()));
			}
		}
		// test presort
		if (MDPropTags.MENU_ID_PRESORT.equals(acInterface.getMenuID()) || isUpdating) {
			testPreSortLocal();
		}
		// test match up
		if (MDPropTags.MENU_ID_MATCHUP.equals(acInterface.getMenuID()) || MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID()) || isUpdating) {
			testMULocal();
		}
		// test profiler
		if (MDPropTags.MENU_ID_PROFILER.equals(acInterface.getMenuID()) || isUpdating) {
			license = getProfilerLicense();
			testProfilerLocal(license);
		}
		// test ipLocator
		if (MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID()) || isUpdating) {
			license = getLicenseString(MDPropTags.MDLICENSE_IPLocator);
			testIpLocatorLocal(license);
		}
		// test Cleanser
		if (MDPropTags.MENU_ID_CLEANSER.equals(acInterface.getMenuID()) || isUpdating) {
			license = getLicenseString(MDPropTags.MDLICENSE_Cleanser);
			testCleanserLocal(license);
		}

		// test Cleanser
		if (MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID()) || isUpdating) {
			license = getLicenseString(MDPropTags.MDLICENSE_GlobalVerify);
			try {
				testGlobalAddressLocal(license);
			} catch (KettleException e) {
				// FIXME propper error message
				settingData.errorList.add(AdvancedConfigInterface.getErrorString("LocalAddrError", e.toString()));
			}

		}
	}

	private void chkWeb(boolean isLocalAppliance) /* throws KettleException */{
		MDSettingsWebService ws = new MDSettingsWebService(acInterface, settingData);
		try {
			ws.init();
			ws.processRequests(testType);
			if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
				ws.saveReports();
			}
		} catch (KettleException e) {
			System.out.println(" ERROR in check web ......" + e.getMessage());
			settingData.errorList.add(e.getMessage());
		}
	}

	private String getExeDir() {
		String decodedPath = "";
		String fileSep = Const.FILE_SEPARATOR;
		File ssdd = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error getting exe path: " + e.getMessage());
		}
		decodedPath = Const.getKettleDirectory() + fileSep + "plugins" + fileSep + "steps" + fileSep + "MDPresort";
		return decodedPath;
	}

	private String getLicenseString(int nProduct) {
		String license = "";
		if ((settingData.primeLicense.retVal & nProduct) != 0) {
			license = settingData.primeLicense.licenseString;
		} else if ((settingData.primeLicense.retVal & MDPropTags.MDLICENSE_Community) != 0) {
			license = MDPropTags.MD_COMMUNITY_LICENSE;
		}
		return license.trim();
	}

	private String getMULicense() {
		// For now if lite or community we use hard coded license till MU is changed to recognize new license
		String tmpLic = MDPropTags.MD_COMMUNITY_LICENSE;
		if ((settingData.primeLicense.retVal & MDPropTags.MDLICENSE_MatchUp) != 0)
			return getLicenseString(MDPropTags.MDLICENSE_MatchUp);
		if ((settingData.primeLicense.retVal & MDPropTags.MDLICENSE_MatchUpLite) != 0)
			return tmpLic;
		if ((settingData.trialLicense.retVal & MDPropTags.MDLICENSE_MatchUp) != 0)
			return getLicenseString(MDPropTags.MDLICENSE_MatchUp);
		if ((settingData.trialLicense.retVal & MDPropTags.MDLICENSE_MatchUpLite) != 0)
			return tmpLic;
		// must be community
		return tmpLic.trim();
	}

	private String getProfilerLicense() {
		String license = getLicenseString(MDPropTags.MDLICENSE_Profiler);
		if (Const.isEmpty(license)) {
			license = "DEMO";
		}
		return license.trim();
	}

	private boolean isLicensed(int nProduct) {
		if ((settingData.primeLicense.retVal & nProduct) != 0)
			return true;
		else if ((settingData.trialLicense.retVal & nProduct) != 0)
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InterruptedException {
		// Start monitoring
		monitor.beginTask(BaseMessages.getString(PKG, "MDSettings.TestDialog.MonitorMsg"), IProgressMonitor.UNKNOWN);
		if (acInterface.nTestType == 1) {
			monitor.subTask(AdvancedConfigInterface.getString("CheckLocal"));
			chkLocal(false);
		}
		if (acInterface.nTestType == 2) {
			monitor.subTask(AdvancedConfigInterface.getString("CheckWeb"));
			chkWeb(false);
		}
		if (acInterface.nTestType == 3) {
			monitor.subTask(AdvancedConfigInterface.getString("CheckWeb"));
			chkWeb(true);
		}
		// Stop monitoring
		monitor.done();
		if (monitor.isCanceled())
			throw new InterruptedException(AdvancedConfigInterface.getErrorString("TestCanceled"));
	}

	private void testAddressLocal(String lic) throws KettleException {
		if (Const.isEmpty(lic)) {
			settingData.addrMsg = "Not Licensed";
			settingData.localAddressBuild = "";
			settingData.localAddressExpiration = "";
			settingData.localAddressDBDate = "";
			return;
		}
		mdAddr ao = null;
		try {
			ao = DQTObjectFactory.newAddr();
			if (!ao.SetLicenseString(lic)) {
				// Handle failure to set license string
				settingData.addrMsg = "Error setting license";
			}
			// Set path to US and Canadian data
			ao.SetPathToUSFiles(dataFilePath);

			if ((settingData.primeLicense.retVal & MDPropTags.MDLICENSE_Address) != 0) {
				// only set these if licensed so we can test in community
				ao.SetPathToCanadaFiles(dataFilePath);
				ao.SetPathToDPVDataFiles(dataFilePath);
				ao.SetPathToLACSLinkDataFiles(dataFilePath);
				ao.SetPathToSuiteLinkDataFiles(dataFilePath);
				ao.SetPathToRBDIFiles(dataFilePath);
				ao.SetPathToSuiteFinderDataFiles(dataFilePath);
				ao.SetPathToAddrKeyDataFiles(dataFilePath);
			}
			// If a CASS form is to be generated then turn on CASS now
			// ao.SetCASSEnable(addressVerify.getCASSSaveToFile() ? 1 : 0);
			// Initialize it
			mdAddr.ProgramStatus result = ao.InitializeDataFiles();
			if (result != mdAddr.ProgramStatus.ErrorNone) {
				// Handle failure to initialize
				settingData.addrMsg = ao.GetInitializeErrorString();
				settingData.localAddressBuild = ao.GetBuildNumber();
				settingData.localAddressExpiration = ao.GetExpirationDate();
				settingData.localAddressDBDate = ao.GetDatabaseDate();
				settingData.localAddressCADBDate = ao.GetCanadianDatabaseDate();
				settingData.localAddressCAExpiration = ao.GetCanadianExpirationDate();
				settingData.RBDIDate = ao.GetRBDIDatabaseDate();
			} else {
				settingData.addrMsg = AdvancedConfigInterface.getString("Error.NoError");
				settingData.localAddressBuild = ao.GetBuildNumber();
				settingData.localAddressExpiration = ao.GetExpirationDate();
				settingData.localAddressDBDate = ao.GetDatabaseDate();
				settingData.localAddressCADBDate = ao.GetCanadianDatabaseDate();
				settingData.localAddressCAExpiration = ao.GetCanadianExpirationDate();
				settingData.RBDIDate = ao.GetRBDIDatabaseDate();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
		if (ao != null) {
			ao.delete();
		}
	}

	private void testEmailLocal(String lic) {
		if (Const.isEmpty(lic)) {
			settingData.emailMsg = "Not Licensed";
			settingData.emailBuild = "";
			settingData.emailExpiration = "";
			settingData.emailDate = "";
			return;
		}
		mdEmail Email = null;
		try {
			Email = DQTObjectFactory.newEmail();
			Email.SetLicenseString(lic);
			Email.SetPathToEmailFiles(dataFilePath);
			if (Email.InitializeDataFiles() == mdEmail.ProgramStatus.ErrorNone) {
				settingData.emailBuild = Email.GetBuildNumber();
				settingData.emailDate = Email.GetDatabaseDate();
				settingData.emailExpiration = Email.GetDatabaseExpirationDate();
				settingData.emailMsg = AdvancedConfigInterface.getString("Error.NoError");
			} else {
				settingData.emailMsg = Email.GetInitializeErrorString();
				settingData.emailBuild = Email.GetBuildNumber();
				settingData.emailDate = Email.GetDatabaseDate();
				settingData.emailExpiration = Email.GetDatabaseExpirationDate();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
		if (Email != null) {
			Email.delete();
		}
	}

	private void testGeoCoderLocal(String lic) {
		if (Const.isEmpty(lic)) {
			settingData.geoMsg = "Not Licensed";
			settingData.geoBuild = "";
			settingData.geoExpiration = "";
			settingData.geoDate = "";
			return;
		}
		mdGeo Geo = null;
		try {
			Geo = DQTObjectFactory.newGeo();
			Geo.SetLicenseString(lic);
			Geo.SetPathToGeoCodeDataFiles(dataFilePath);
			int licensedProducts = settingData.primeLicense.retVal;
			if ((licensedProducts & MDPropTags.MDLICENSE_GeoPoint) != 0) {
				Geo.SetPathToGeoPointDataFiles(dataFilePath);
			}
			if ((licensedProducts & MDPropTags.MDLICENSE_CanadaGeo) != 0) {
				Geo.SetPathToGeoCanadaDataFiles(dataFilePath);
			}
			if (Geo.InitializeDataFiles() == mdGeo.ProgramStatus.ErrorNone) {
				settingData.geoMsg = AdvancedConfigInterface.getString("Error.NoError");
				settingData.geoBuild = Geo.GetBuildNumber();
				settingData.geoDate = Geo.GetDatabaseDate();
				settingData.geoExpiration = Geo.GetExpirationDate();
			} else {
				settingData.geoMsg = Geo.GetInitializeErrorString();
				settingData.geoBuild = Geo.GetBuildNumber();
				settingData.geoDate = Geo.GetDatabaseDate();
				settingData.geoExpiration = Geo.GetExpirationDate();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
		if (Geo != null) {
			Geo.delete();
		}
	}

	private void testIpLocatorLocal(String lic) {
		if (Const.isEmpty(lic)) {
			settingData.ipLocMsg = "Not Licensed";
			settingData.ipLocBuild = "";
			settingData.ipLocExpiration = "";
			settingData.ipLocDate = "";
			return;
		}
		mdIpLocator ipLoc = null;
		try {
			ipLoc = DQTObjectFactory.newIpLocator();
			String msg = "";
			ipLoc.SetLicenseString(lic);
			ipLoc.SetPathToIpLocatorFiles(dataFilePath);
			msg = ipLoc.InitializeDataFiles().toString();
			if (msg == mdIpLocator.ProgramStatus.ErrorNone.toString()) {
				settingData.ipLocMsg = AdvancedConfigInterface.getString("Error.NoError");
				settingData.ipLocBuild = ipLoc.GetBuildNumber();
				settingData.ipLocExpiration = ipLoc.GetDatabaseExpirationDate();
				settingData.ipLocDate = ipLoc.GetDatabaseDate();
			} else {
				settingData.ipLocMsg = ipLoc.GetInitializeErrorString();
				settingData.ipLocBuild = ipLoc.GetBuildNumber();
				settingData.ipLocExpiration = ipLoc.GetDatabaseExpirationDate();
				settingData.ipLocDate = ipLoc.GetDatabaseDate();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
		if (ipLoc != null) {
			ipLoc.delete();
		}
	}

	private void testMULocal() {
		// String muLicense;
		// Optional testing mechanism using just matchcode list object.
		if (TEST_MATCHUP_USING_MATCHCODELIST) {
			mdMUMatchcodeList MatchcodeList = null;
			try {
				try {
					MatchcodeList = DQTObjectFactory.newMatchcodeList();
				} catch (DQTObjectException e) {
					// Handle failure to initialize
					settingData.matchUPMsg = e.getMessage();
					return;
				}
				MatchcodeList.SetPathToMatchUpFiles(dataFilePath);
				if (MatchcodeList.InitializeDataFiles() != mdMUMatchcodeList.ProgramStatus.ErrorNone) {
					// Handle failure to initialize
					settingData.matchUPMsg = AdvancedConfigInterface.getErrorString("InitializeService", MatchcodeList.GetInitializeErrorString());
					return;
				} else {
					settingData.matchUPMsg = "Success";
					settingData.matchUPDate = "";
					settingData.matchUPExpiration = "";
					settingData.matchUPBuild = "";
				}
			} finally {
				if (MatchcodeList != null) {
					MatchcodeList.delete();
				}
			}
		} else {// Use ReadWrite object to test matchup
			File keyFile;
			File workFile;
			String workPath = settingData.matchUpWorkPath;
			if (Const.isEmpty(workPath)) {
				workFile = null;
			} else {
				workFile = new File(settingData.matchUpWorkPath);
			}
			String muLicense = getMULicense();
			// Initialize the Read/Write deduper
			mdMUReadWrite ReadWrite;
			try {
				ReadWrite = DQTObjectFactory.newMatchupReadWrite();
			} catch (DQTObjectException e) {
				// Handle failure to initialize
				settingData.matchUPMsg = e.getMessage();
				return;
			}
			ReadWrite.SetLicenseString(muLicense);
			ReadWrite.SetPathToMatchUpFiles(dataFilePath/* "/home/kevin/Melissa-Data/common/matchup" */);// settingData.matchUpDataPath
			if((MDPropTags.MENU_ID_MATCHUP_GLOBAL.equals(acInterface.getMenuID()))) {
				ReadWrite.SetMatchcodeName("Global Address");
			} else {
				//Global Address
				ReadWrite.SetMatchcodeName("Address");
			}
			// Create a temporary key file
			try {
				keyFile = File.createTempFile("muKey", null, workFile);
			} catch (IOException e) {
				// Handle failure to create key file
				settingData.matchUPMsg = AdvancedConfigInterface.getErrorString("BadWorkPath", (settingData.matchUpDataPath != null) ? settingData.matchUpDataPath.toString() : "");
				return;
			}
			keyFile.deleteOnExit();
			ReadWrite.SetKeyFile(keyFile.getAbsolutePath());
			// Initialize
			if (ReadWrite.InitializeDataFiles() != mdMUReadWrite.ProgramStatus.ErrorNone) {
				// Handle failure to initialize
				settingData.matchUPMsg = AdvancedConfigInterface.getErrorString("InitializeService", ReadWrite.GetInitializeErrorString());
				settingData.matchUPDate = ReadWrite.GetDatabaseDate();
				settingData.matchUPExpiration = ReadWrite.GetDatabaseExpirationDate();
				settingData.matchUPBuild = ReadWrite.GetBuildNumber();
				// Sometimes when ReadWrite fails to initialize it can be
				// put into a state where the delete()
				// method will crash the JVM. We de-reference the object so
				// this won't happen.
				ReadWrite = null;
				return;
			} else {
				settingData.matchUPMsg = "Success";
				settingData.matchUPDate = ReadWrite.GetDatabaseDate();
				settingData.matchUPExpiration = ReadWrite.GetDatabaseExpirationDate();
				settingData.matchUPBuild = ReadWrite.GetBuildNumber();
			}
			if (ReadWrite != null) {
				ReadWrite.delete();
			}
		}
	}

	private void testNameLocal(String lic) {
		if (Const.isEmpty(lic)) {
			settingData.nameMsg = "Not Licensed";
			settingData.nameBuild = "";
			settingData.nameExpiration = "";
			settingData.nameDate = "";
			return;
		}
		mdName Name = null;
		try {
			Name = DQTObjectFactory.newName();
			String msg = "";
			Name.SetLicenseString(lic);
			Name.SetPathToNameFiles(dataFilePath);
			msg = Name.InitializeDataFiles().toString();
			if (msg == mdName.ProgramStatus.NoError.toString()) {
				settingData.nameMsg = AdvancedConfigInterface.getString("Error.NoError");
				settingData.nameBuild = Name.GetBuildNumber();
				settingData.nameExpiration = Name.GetDatabaseExpirationDate();
				settingData.nameDate = Name.GetDatabaseDate();
			} else {
				settingData.nameMsg = Name.GetInitializeErrorString();
				settingData.nameBuild = Name.GetBuildNumber();
				settingData.nameExpiration = Name.GetDatabaseExpirationDate();
				settingData.nameDate = Name.GetDatabaseDate();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
		if (Name != null) {
			Name.delete();
		}
	}

	private void testPhoneLocal(String lic) {
		if (Const.isEmpty(lic)) {
			settingData.phoneMsg = "Not Licensed";
			settingData.phoneBuild = "";
			settingData.phoneExpiration = "";
			settingData.phoneDate = "";
			return;
		}
		mdPhone Phone = null;
		try {
			Phone = DQTObjectFactory.newPhone();
			Phone.SetLicenseString(lic);
			if (Phone.Initialize(dataFilePath) == mdPhone.ProgramStatus.ErrorNone) {
				settingData.phoneBuild = Phone.GetBuildNumber();
				settingData.phoneDate = Phone.GetDatabaseDate();
				settingData.phoneExpiration = Phone.GetLicenseExpirationDate();
				settingData.phoneMsg = AdvancedConfigInterface.getString("Error.NoError");
			} else {
				settingData.phoneMsg = Phone.GetInitializeErrorString();
				settingData.phoneBuild = Phone.GetBuildNumber();
				settingData.phoneDate = Phone.GetDatabaseDate();
				settingData.phoneExpiration = Phone.GetLicenseExpirationDate();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
		if (Phone != null) {
			Phone.delete();
		}
	}

	private void testPreSortLocal() {
		String settingPath;
		String resultText = "";
		if (isLicensed(MDPropTags.MDLICENSE_Presort) && Const.isWindows()) {
			try {
				settingPath = CZUtil.getCZWorkDirectory().getPath() + Const.FILE_SEPARATOR + "presort";
				File settingsFile = new File(settingPath, SETTING_FILE_NAME);
				BufferedWriter propFile = new BufferedWriter(new FileWriter(settingsFile));
				propFile.write("License=" + getLicenseString(MDPropTags.MDLICENSE_Presort).trim());
				propFile.newLine();
				propFile.write("DataPath=" + settingData.preSortDataPath);
				propFile.newLine();
				propFile.write("MailerID=000000");
				propFile.newLine();
				propFile.write("POMZip=46058");
				propFile.newLine();
				propFile.write("SortCode=101");
				propFile.newLine();
				propFile.write("PieceHeight=4.5");
				propFile.newLine();
				propFile.write("PieceWidth=9");
				propFile.newLine();
				propFile.write("SampleThick=0.041999999999999996");
				propFile.newLine();
				propFile.write("SampleWeight=1.5");
				propFile.newLine();
				propFile.close();
				//String exePath = System.getenv("KETTLE_DIR") + File.separator + "plugins" + File.separator + "steps" + File.separator + "MDPresort";
				String exePath = CZUtil.getCZWorkDirectory().getPath() + Const.FILE_SEPARATOR + "presort";
//				File executablePath = new File(exePath);
//				if (!executablePath.exists()) {
//					exePath = getExeDir();
//					executablePath = new File(exePath);
//				}
//				String[] prms = new String[] { executablePath.toString() + "\\mdPresortExec.exe", settingPath, TEST_CONFIG_INPUT };
				String[] prms = new String[] { exePath + "\\mdPresortExec.exe", settingPath, TEST_CONFIG_INPUT };
				ProcessBuilder pb = new ProcessBuilder(prms);
				Process p = pb.start();
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
					resultText = resultText.concat(line);
				}
				while ((line = error.readLine()) != null) {
					resultText = resultText.concat(line);
				}
				if (Const.isEmpty(resultText)) {
					resultText = AdvancedConfigInterface.getString("Error.NoError");
				}
				settingData.preSortMsg = resultText;
				prms[2] = TEST_CONFIG_EXPIRATION;
				resultText = "";
				pb = new ProcessBuilder(prms);
				p = pb.start();
				in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				line = null;
				while ((line = in.readLine()) != null) {
					resultText = resultText.concat(line);
				}
				while ((line = error.readLine()) != null) {
					resultText = resultText.concat(line);
				}
				if (resultText.equals(settingData.preSortMsg)) {
					settingData.preSortExpiration = "";
				} else {
					settingData.preSortExpiration = resultText;
				}
				resultText = "";
				prms[2] = TEST_CONFIG_BUILD;
				pb = new ProcessBuilder(prms);
				p = pb.start();
				in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				line = null;
				while ((line = in.readLine()) != null) {
					resultText = resultText.concat(line);
				}
				while ((line = error.readLine()) != null) {
					resultText = resultText.concat(line);
				}
				if (resultText.equals(settingData.preSortMsg)) {
					settingData.preSortBuild = "";
				} else {
					settingData.preSortBuild = resultText;
				}
			} catch (Exception e) {
				resultText = AdvancedConfigInterface.getErrorString("PresortError", e.toString());
				settingData.preSortMsg = resultText;
			}
		} else {
			if(Const.isLinux()){
				settingData.preSortMsg = "Presort is not available for Linux";
			}else if(Const.isOSX()){
				settingData.preSortMsg = "Presort is not available for OSX";
			}else {
				settingData.preSortMsg = "Not Licensed";
			}
		}
	}

	private void testProfilerLocal(String lic) {
		if (Const.isEmpty(lic)) {
			settingData.profilerMsg = "Not Licensed";
			settingData.profilerBuild = "";
			settingData.profilerDate = "";
			settingData.profilerExpiration = "";
			return;
		}
		mdProfiler profiler = null;
		try {

			String testFile = System.getProperty("user.home") + Const.FILE_SEPARATOR + "testFile.prf";
			profiler = DQTObjectFactory.newProfiler();
			profiler.SetFileName(testFile);
			profiler.SetAppendMode(mdProfiler.AppendMode.Overwrite);
			profiler.SetLicenseString(lic);
			profiler.SetPathToProfilerDataFiles(dataFilePath);
			// Initialize Profiler session:
			if (profiler.InitializeDataFiles() != mdProfiler.ProgramStatus.ErrorNone) {
				settingData.profilerMsg = profiler.GetInitializeErrorString();
				settingData.profilerBuild = profiler.GetBuildNumber();
				settingData.profilerDate = profiler.GetDatabaseDate();
			} else {
				settingData.profilerMsg = "Success";
				settingData.profilerBuild = profiler.GetBuildNumber();
				settingData.profilerDate = profiler.GetDatabaseDate();
			}
			if (profiler != null) {
				profiler.delete();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
	}
	
	private void testCleanserLocal(String lic) {

		if(!Const.isWindows()){
			settingData.cleanserMsg = "MD Generalized Cleanser not available for " + Const.getOS();
			settingData.cleanserBuild = "";
			settingData.cleanserDate = "";
			settingData.cleanserExpiration = "";
			return;
		}

		mdCleanser cleanser;

		try {
			cleanser = DQTObjectFactory.newCleanser();
			cleanser.SetLicenseString(lic);
			cleanser.SetPathToCleanserDataFiles(dataFilePath);
			cleanser.SetOperationMode(mdCleanser.CleanseOperation.OperationSearchReplace);
			cleanser.SetFieldDataType(mdCleanser.FieldDataType.DataTypeGeneral);
			cleanser.SetCasingMode(mdCleanser.CasingMode.CaseLower);
			cleanser.SetAbbreviationMode(mdCleanser.AbbreviationMode.AbbreviationContractAggressive);
			cleanser.SetPunctuationMode(mdCleanser.PunctuationMode.PunctuationAdd);
			cleanser.AddSearchReplace("SerchTerm", "ReplaceTerm");

			mdCleanser.ProgramStatus programStatus = cleanser.InitializeDataFiles();
			if (programStatus != mdCleanser.ProgramStatus.ErrorNone) {
				settingData.cleanserMsg = cleanser.GetInitializeErrorString();
				settingData.cleanserBuild = cleanser.GetBuildNumber();
				settingData.cleanserDate = cleanser.GetDatabaseDate();
				settingData.cleanserExpiration = cleanser.GetDatabaseExpirationDate();
			} else {
				// TODO put in messages
				settingData.cleanserMsg = "Success";
				settingData.cleanserBuild = cleanser.GetBuildNumber();
				settingData.cleanserDate = cleanser.GetDatabaseDate();
				settingData.cleanserExpiration = cleanser.GetDatabaseExpirationDate();
			}

			if (cleanser != null) {
				cleanser.delete();
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
	}

	private void testGlobalAddressLocal(String lic) throws KettleException {
		if (Const.isEmpty(lic)) {
			settingData.globalAddrMsg = "Not Licensed";
			settingData.globalAddressBuild = "";
			settingData.globalAddressExpiration = "";
			settingData.globalAddressDBDate = "";
			return;
		}

		mdGlobalAddr gao = null;

		try {
			gao = DQTObjectFactory.newGlobalAddr();
			if (!gao.SetLicenseString(lic)) {
				// Handle failure to set license string
				settingData.globalAddrMsg = "Error setting license";
			}
			// Set path to Global data
			gao.SetPathToGlobalAddrFiles(dataFilePath);

//			if ((settingData.primeLicense.retVal & MDPropTags.MDLICENSE_GlobalVerify) != 0) {
//				// only set these if licensed so we can test in community
//				ao.SetPathToCanadaFiles(dataFilePath);
//				ao.SetPathToDPVDataFiles(dataFilePath);
//				ao.SetPathToLACSLinkDataFiles(dataFilePath);
//				ao.SetPathToSuiteLinkDataFiles(dataFilePath);
//				ao.SetPathToRBDIFiles(dataFilePath);
//				ao.SetPathToSuiteFinderDataFiles(dataFilePath);
//				ao.SetPathToAddrKeyDataFiles(dataFilePath);
//			}
			// If a CASS form is to be generated then turn on CASS now
			// ao.SetCASSEnable(addressVerify.getCASSSaveToFile() ? 1 : 0);
			// Initialize it
			mdGlobalAddr.ProgramStatus result = gao.InitializeDataFiles();
			if (result != mdGlobalAddr.ProgramStatus.ErrorNone) {
				// Handle failure to initialize
				settingData.globalAddrMsg = gao.GetOutputParameter("initializeErrorString");
				settingData.globalAddressBuild = gao.GetOutputParameter("BuildNumber");
				settingData.globalAddressExpiration = gao.GetOutputParameter("databaseExpirationDate");
				settingData.globalAddressDBDate = gao.GetOutputParameter("databaseDate");

			} else {
				settingData.globalAddrMsg = AdvancedConfigInterface.getString("Error.NoError");
				settingData.globalAddressBuild = gao.GetOutputParameter("BuildNumber");
				settingData.globalAddressExpiration = gao.GetOutputParameter("databaseExpirationDate");
				settingData.globalAddressDBDate = gao.GetOutputParameter("databaseDate");
			}
		} catch (DQTObjectException e) {
			settingData.errorList.add(e.getMessage());
		}
		if (gao != null) {
			gao.delete();
		}
	}
}
