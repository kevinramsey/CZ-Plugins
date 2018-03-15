package com.melissadata.kettle.cv;

import java.io.File;
import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.kettle.iplocator.IPLocatorMeta;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.address.AddressVerifyMeta;
import com.melissadata.kettle.cv.email.EmailVerifyMeta;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta;
import com.melissadata.kettle.cv.name.NameParseMeta;
import com.melissadata.kettle.cv.phone.PhoneVerifyMeta;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.report.ReportStats;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.mdAddr;
import com.melissadata.mdAddr.DiacriticsMode;
import com.melissadata.mdEmail;
import com.melissadata.mdGeo;
import com.melissadata.mdIpLocator;
import com.melissadata.mdName;
import com.melissadata.mdPhone;
import com.melissadata.mdZip;
import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.AdvancedConfigurationMeta.LicenseTestResult;

public class MDCheckLocalService extends MDCheckService {
	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.Validation." + name, args);
	}
	private static Class<?>				PKG	= MDCheckLocalService.class;
	private LogChannelInterface       log;
	private AdvancedConfigurationMeta acMeta;

	public MDCheckLocalService(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {
		super(stepData, checkData, space, log);
		this.log = log;
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#buildRequest(com.melissadata.kettle.support.IOMeta, java.lang.Object[])
 */
	@Override
	public MDCheckRequest buildRequest(IOMeta ioMeta, Object[] inputData) {
		// FIXME: Use a pool of request objects?
		return new MDCheckCVRequest(ioMeta, inputData);
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#determineRequestRoute(com.melissadata.kettle.MDCheckRequest)
 */
	@Override
	public int determineRequestRoute(MDCheckRequest request) throws KettleValueException {
		// TODO: CV currently uses only one queue
		return 0;
	}

	@Override
	public boolean checkProxy() {
		// Not used in Local service just return true
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#dispose()
	 */
	@Override
	public void dispose() {
		// Dispose of any objects created
		if (checkData.Name != null) {
			checkData.Name.delete();
		}
		if (checkData.Addr != null) {
			checkData.Addr.delete();
		}
		if (checkData.Geo != null) {
			checkData.Geo.delete();
		}
		if (checkData.Zip != null) {
			checkData.Zip.delete();
		}
		if (checkData.Phone != null) {
			checkData.Phone.delete();
		}
		if (checkData.Email != null) {
			checkData.Email.delete();
		}
		if (checkData.ipLocator != null) {
			checkData.ipLocator.delete();
		}
		// Call parent last
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#init()
	 */
	@Override
	public void init() throws KettleException {
		// Call parent first
		super.init();
		log.logDetailed("MDCheck Local service init called");
		// Perform initialiation checks
		Validations checker = new Validations();
		checker.checkInitialization(stepData);
		// Get the real Customer id and data path
		acMeta = stepData.getAdvancedConfiguration();

		checkData.realDataPath = acMeta.getLocalDataPath();
		log.logDetailed("MDCheck Local service data path = " + checkData.realDataPath);

		// We process only one request at a time
		checkData.maxRequests = acMeta.getMaxRequests();
		// Create license object
		initLicense();
		// Setup reporting
		initReporting();
		if ((stepData.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			log.logDetailed("MDCheck Local service initializing mdObjects ...");
			// Create name object
			if (testing || (stepData.getNameParse().isInitializeOK())) {
				initName();
			}
			// Create address object
			if (testing || (stepData.getAddressVerify().isInitializeOK())) {
				initAddr();
			}
			// Create GeoCoder object
			if (testing || (stepData.getGeoCoder().isInitializeOK())) {
				initGeo();
			}
			// Create the Zip object
			if (testing || (stepData.getAddressVerify().isInitializeOK())) {
				initZip();
			}
			// Create phone object
			if (testing || (stepData.getPhoneVerify().isInitializeOK())) {
				initPhone();
			}
			// Create email object
			if (testing || (stepData.getEmailVerify().isInitializeOK())) {
				initEmail();
			}
		} else {
			// Create IPLocator object
			// TODO Initialization check for IP locator
			log.logDetailed("IPLocator Local service initializing mdObjects ...");
			if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)
					&& AdvancedConfigurationMeta.isCommunity()){
				String msg = BaseMessages.getString(PKG, "MDCheckDialog.Community.PopupMessage.NotAvailable", "IP Locator") + "Local";
				log.logBasic(msg);
				MDCheck.showSleepMsg(msg, stepData.getCheckTypes());
			}
			initIPLocator();
		}
		// If not testing then check for more errors
		if (!testing && ((stepData.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0)) {
			checker.checkErrors(stepData, log);
		}
	}

	/**
	 * Called create the license object
	 *
	 * @throws KettleException
	 */
	public void initLicense() throws KettleException {
		// Called to initialize the licensed products fields
		if (acMeta == null) {
			acMeta = stepData.getAdvancedConfiguration();
		}
		acMeta.getProducts();
		if (acMeta.testLicense(stepData.getCheckTypes()) != LicenseTestResult.NoErrors) { throw new KettleException(acMeta.testLicense(stepData.getCheckTypes()).toString()); }
	}

	/**
	 * Called to initialize the name object
	 *
	 * @throws KettleException
	 */
	public void initName() throws KettleException {
		// Skip if not present
		NameParseMeta nameParse = stepData.getNameParse();
		if (nameParse == null) { return; }
		// to make local work in web mode
		// Get the real Customer id and data path
		if (checkData.realDataPath == null) {
			AdvancedConfigurationMeta acMeta = stepData.getAdvancedConfiguration();
			checkData.realDataPath = acMeta.getLocalDataPath();
			// We process only one request at a time
			checkData.maxRequests = acMeta.getMaxRequests();
		}
		// Fail testing if not licensed
		if (!nameParse.isLicensed()) {
			nameParse.localMsg = MDCheck.getErrorString("NotLicensed");
			nameParse.localException = new KettleException(MDCheck.getErrorString("InitializeService", nameParse.localMsg));
			if (!testing && (nameParse.isEnabled() || nameParse.isCompanyEnabled())) { throw nameParse.localException; }
			return;
		}
		try {
			// Allocate name object and set it's license string
			mdName Name;
			try {
				Name = checkData.Name = DQTObjectFactory.newName();
			} catch (DQTObjectException e) {
				// Handle failure to create object
				nameParse.localMsg = e.getMessage();
				nameParse.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw nameParse.localException; }
				return;
			}
			String nameLicense = nameParse.getLicense();
			if (Name.SetLicenseString(nameLicense) == 0) {
				initFailed = true;
				// Handle failure to set license string
				nameParse.localMsg = MDCheck.getErrorString("SettingLicense", nameLicense);
				nameParse.localException = new KettleException(MDCheck.getErrorString("InitializeService", nameParse.localMsg));
				if (!testing) { throw nameParse.localException; }
				return;
			}

			// FIXME hadoopcheck if cluster
//			if(Const.isEmpty(acMeta.clusterDataPath) && acMeta.isCluster())
//				acMeta.clusterDataPath = "/hadoop/yarn/local/DQT/data"; // get real path
				
			// Initialize the name object
//			if(!Const.isEmpty(acMeta.clusterDataPath))
//				Name.SetPathToNameFiles(acMeta.clusterDataPath);
//			else
				Name.SetPathToNameFiles(checkData.realDataPath);
			if (Name.InitializeDataFiles() != mdName.ProgramStatus.NoError) {
				initFailed = true;
				// Handle failure to initialize
				nameParse.localMsg = MDCheck.getErrorString("InitializeService", Name.GetInitializeErrorString());
				nameParse.localException = new KettleException(nameParse.localMsg);
				if (!testing) { throw nameParse.localException; }
				return;
			}

			// Get database information
			nameParse.localDBDate = Name.GetDatabaseDate();
			nameParse.localDBBuildNo = Name.GetBuildNumber();
			// Otherwise, complete name object configuration
			Name.SetFirstNameSpellingCorrection(nameParse.getCorrectMispellings() ? 1 : 0);
			Name.SetPrimaryNameHint(nameParse.getNameOrderHint().getMDNameHint());
			Name.SetGenderAggression(nameParse.getGenderAggression().getMDAggression());
			Name.SetGenderPopulation(nameParse.getGenderPopulation().getMDPopulation());
			Name.SetSalutationPrefix(nameParse.getSalutationPrefix());
			Name.SetSalutationSuffix(nameParse.getSalutationSuffix());
			Name.SetSalutationSlug(nameParse.getSalutationSlug());
			Name.SetMiddleNameLogic(nameParse.getMiddleNameLogic().getMDMiddleNameLogic());
			for (NameParseMeta.Salutation salutation : nameParse.getSalutationOrder()) {
				Name.AddSalutation(salutation.getMDSalutation());
			}
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = false;
			// If anything unusual happened then return an initialization failure
			nameParse.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			nameParse.localException = new KettleException(nameParse.localMsg, t);
			if (!testing) { throw nameParse.localException; }
		}
	}

	/**
	 * Called to process name requests
	 *
	 * @param requests
	 * @throws KettleValueException
	 */
	public void processNameRequests(List<MDCheckRequest> requests) throws KettleValueException {
		if (stepData.getNameParse() != null) {
			stepData.getNameParse().doLocalRequests(checkData, requests);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#processRequests()
	 */
	@Override
	public void processRequests(List<MDCheckRequest> requests, int queue, boolean chkXML, int attempts) throws KettleException {
		try {
			// All the testing for the local service takes place in the
			// initialization phase
			if (testing) { return; }
			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
				// Process the name parser requests
				if (stepData.getNameParse().isInitializeOK()) {
					processNameRequests(requests);
				}
				// Process the address verifier requests
				if (stepData.getAddressVerify().isInitializeOK()) {
					processAddrRequests(requests);
				}
				// Process the geo coder requests
				if (stepData.getGeoCoder().isInitializeOK() || stepData.getAddressVerify().isInitializeOK()) {
					processGeoRequests(requests);
				}
				// Process the phone verifier requests
				if (stepData.getPhoneVerify().isInitializeOK()) {
					processPhoneRequests(requests);
				}
				// Process the email verifier requests
				if (stepData.getEmailVerify().isInitializeOK()) {
					processEmailRequests(requests);
				}
			}
//			if ((stepData.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
//				processIPLocatorRequests(requests);
//			}
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened then wrap in a kettle exception
			throw new KettleException(MDCheck.getErrorString("ProcessService", t.toString()), t);
		}
	}

	/*
 * (non-Javadoc)
 * @see com.melissadata.kettle.MDCheckService#outputData(int)
 */
	@Override
	public void outputData(List<MDCheckRequest> requests, int queue) {
		// All the testing for the local service takes place in the initialization phase
		if (testing) { return; }
		// Process the name parser requests
		if (stepData.getNameParse() != null) {
			stepData.getNameParse().outputData(checkData, requests);
		}
		// Process the address verifier requests
		if (stepData.getAddressVerify() != null) {
			stepData.getAddressVerify().outputData(checkData, requests);
		}
		// Process the geo coder requests
		if (stepData.getGeoCoder() != null) {
			stepData.getGeoCoder().outputData(checkData, requests);
		}
		// Process the phone verifier requests
		if (stepData.getPhoneVerify() != null) {
			stepData.getPhoneVerify().outputData(checkData, requests);
		}
		// Process the email verifier requests
		if (stepData.getEmailVerify() != null) {
			stepData.getEmailVerify().outputData(checkData, requests);
		}
//		// Process IP Locator requests
//		if (stepData.getIPLocator() != null) {
//			stepData.getIPLocator().outputData(checkData, requests);
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#saveReports()
	 */
	@Override
	public void saveReports() throws KettleException {
		// Only the address object generates reports (for now)
		if (stepData.getAddressVerify() != null) {
			stepData.getAddressVerify().saveReports(checkData);
		}
	}

	/**
	 * Called to initialize the address object
	 *
	 * @throws KettleException
	 */
	private void initAddr() throws KettleException {
		// Skip if not present
		AddressVerifyMeta addressVerify = stepData.getAddressVerify();
		if (addressVerify == null) { return; }
		// Fail if not licensed
		if (!addressVerify.isLicensed()) {
			addressVerify.localMsg = MDCheck.getErrorString("NotLicensed");
			addressVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", addressVerify.localMsg));
			if (!testing && addressVerify.isEnabled()) { throw addressVerify.localException; }
			return;
		}
		try {
			// create address object
			mdAddr Addr;
			try {
				Addr = checkData.Addr = DQTObjectFactory.newAddr();
			} catch (DQTObjectException e) {
				// Handle failure to create object
				addressVerify.localMsg = e.getMessage();
				addressVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw addressVerify.localException; }
				return;
			}
			// Configure it with license string
			String addrLicense = addressVerify.getLicense();
			if (!Addr.SetLicenseString(addrLicense)) {
				// Handle failure to set license string
				addressVerify.localMsg = MDCheck.getErrorString("SettingLicense", addrLicense);
				addressVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", addressVerify.localMsg));
				// If the license string was blank then this is probably the first time the plugin is
				// being used. We have to allow the customer at least to get to the configuration
				// dialog in order to set it.
				if (addrLicense.trim().length() == 0) { return; }
				throw addressVerify.localException;
			}
			// Set configuration properties
			Addr.SetUseUSPSPreferredCityNames(addressVerify.getOptionUsePreferredCity() ? 1 : 0);
			// Set diacritic mode
			switch (addressVerify.getOptionDiacriticMode()) {
				case Automatic:
					Addr.SetDiacritics(DiacriticsMode.Auto);
					break;
				case AlwaysAdd:
					Addr.SetDiacritics(DiacriticsMode.On);
					break;
				case AlwaysStrip:
					Addr.SetDiacritics(DiacriticsMode.Off);
					break;
			}
			
			File f = new File(checkData.realDataPath);
			if (f.exists() || f.isDirectory() || !f.canRead()) {
				AddressVerifyMeta.Countries countries = addressVerify.getOptionCountries();
				boolean isUSEnabled = (countries == AddressVerifyMeta.Countries.US) || (countries == AddressVerifyMeta.Countries.USCanada);
				boolean isCanadaEnabled = (countries == AddressVerifyMeta.Countries.Canada) || (countries == AddressVerifyMeta.Countries.USCanada);
				boolean isRBDILicensed = (stepData.getAdvancedConfiguration().getProducts() & AdvancedConfigurationMeta.MDLICENSE_RBDI) != 0;
				boolean useRBDI = addressVerify.getOptionPerformRBDI() && isRBDILicensed;
				// Set path to US and Canadian data
				if ((AdvancedConfigurationMeta.getProducts(MDPropTags.TAG_PRIMARY_LICENSE) & MDPropTags.MDLICENSE_Address) != 0) {
					// we are fully licensed
					if (isUSEnabled) {
						Addr.SetPathToUSFiles(checkData.realDataPath);
						Addr.SetPathToAddrKeyDataFiles(checkData.realDataPath);
					}
					if (isCanadaEnabled) {
						Addr.SetPathToCanadaFiles(checkData.realDataPath);
					}
					// Set other paths
					if (addressVerify.getOptionPerformDPV()) {
						Addr.SetPathToDPVDataFiles(checkData.realDataPath);
					}
					if (addressVerify.getOptionPerformLACSLink()) {
						Addr.SetPathToLACSLinkDataFiles(checkData.realDataPath);
					}
					if (addressVerify.getOptionPerformSuiteLink()) {
						Addr.SetPathToSuiteLinkDataFiles(checkData.realDataPath);
					}
					if (useRBDI) {
						Addr.SetPathToRBDIFiles(checkData.realDataPath);
					}
					if (addressVerify.getOptionPerformAddrPlus()) {
						Addr.SetPathToSuiteFinderDataFiles(checkData.realDataPath);
					}
				} else if(AdvancedConfigurationMeta.isCommunity()){
					// Community edition
					Addr.SetPathToUSFiles(checkData.realDataPath);
				}
				// If a CASS form is to be generated then turn on CASS now
				Addr.SetCASSEnable(addressVerify.getCASSSaveToFile() ? 1 : 0);
				// Initialize it
				mdAddr.ProgramStatus result = Addr.InitializeDataFiles();
				if (result != mdAddr.ProgramStatus.ErrorNone) {
					// Handle failure to initialize
					addressVerify.localMsg = MDCheck.getErrorString("InitializeService", Addr.GetInitializeErrorString());
					addressVerify.localException = new KettleException(addressVerify.localMsg);
				}
			} else {
				// TODO handel this better. currently shows up as null pointer.
				// we just need to say cant find file.
				throw addressVerify.localException;
			}
			// Get underlying database information
			addressVerify.localDBDate = Addr.GetDatabaseDate();
			if (addressVerify.isRBDILicensed()) {
				addressVerify.localRBDIDate = Addr.GetRBDIDatabaseDate();
			} else {
				addressVerify.localRBDIDate = MDCheck.getErrorString("NotLicensed");
			}
			addressVerify.localDBBuildNo = Addr.GetBuildNumber();
			if (addressVerify.isCanadaLicensed()) {
				addressVerify.localCDBDate = Addr.GetCanadianDatabaseDate();
			} else {
				addressVerify.localCDBDate = MDCheck.getErrorString("NotLicensed");
			}
			if (!Addr.GetInitializeErrorString().equalsIgnoreCase("No error.")) {
				addressVerify.localMsg = MDCheck.getErrorString("InitializeService", Addr.GetInitializeErrorString());
				addressVerify.localException = new KettleException(addressVerify.localMsg);
				throw addressVerify.localException;
			}
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened then return an initialization failure
			addressVerify.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			addressVerify.localException = new KettleException(addressVerify.localMsg, t);
			if (!testing) { throw addressVerify.localException; }
		}
	}

	/**
	 * Called to initialize the email object
	 *
	 * @throws KettleException
	 */
	private void initEmail() throws KettleException {
		// Skip if not present
		EmailVerifyMeta emailVerify = stepData.getEmailVerify();
		if (emailVerify == null) { return; }
		// Fail if not licensed
		if (!emailVerify.isLicensed()) {
			emailVerify.localMsg = MDCheck.getErrorString("NotLicensed");
			emailVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", emailVerify.localMsg));
			if (!testing && emailVerify.isEnabled()) { throw emailVerify.localException; }
			return;
		}
		try {
			// Create email object
			mdEmail Email;
			try {
				Email = checkData.Email = DQTObjectFactory.newEmail();
			} catch (DQTObjectException e) {
				// Handle failure to create object
				emailVerify.localMsg = e.getMessage();
				emailVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw emailVerify.localException; }
				return;
			}
			// Set the license
			String emailLicense = emailVerify.getLicense();
			if (!Email.SetLicenseString(emailLicense)) {
				initFailed = true;
				// handle failre to set license string
				emailVerify.localMsg = MDCheck.getErrorString("SettingLicense", emailLicense);
				emailVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", emailVerify.localMsg));
				if (!testing) { throw emailVerify.localException; }
				return;
			}
			// Initialize the object
			Email.SetPathToEmailFiles(checkData.realDataPath);
			if (Email.InitializeDataFiles() != mdEmail.ProgramStatus.ErrorNone) {
				initFailed = true;
				// Handle failure to initialize the object
				emailVerify.localMsg = MDCheck.getErrorString("InitializeService", Email.GetInitializeErrorString());
				emailVerify.localException = new KettleException(emailVerify.localMsg);
				if (!testing) { throw emailVerify.localException; }
				return;
			}
			// Set a few processing options:
			if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_EmailObject)){
				Email.SetCorrectSyntax(true);
				Email.SetDatabaseLookup(false);
				Email.SetMXLookup(false);
				Email.SetStandardizeCasing(true);
				Email.SetUpdateDomain(false);
				Email.SetFuzzyLookup(false);
				Email.SetWSLookup(false);
			} else {
				Email.SetCorrectSyntax(emailVerify.getOptionCorrectEmailSyntax());
				Email.SetDatabaseLookup(emailVerify.getOptionPerformDBLookup());
				Email.SetMXLookup(emailVerify.getOptionPerformDNSLookup());
				Email.SetStandardizeCasing(emailVerify.getOptionStandardizeCasing());
				Email.SetUpdateDomain(emailVerify.getOptionUpdateDomains());
				Email.SetFuzzyLookup(emailVerify.getOptionFuzzyLookup());
				Email.SetWSLookup(emailVerify.getOptionWebServiceLookup());
			}
			// Get database info
			emailVerify.localDBDate = Email.GetDatabaseDate();
			emailVerify.localDBBuildNo = Email.GetBuildNumber();
		} catch (UnsatisfiedLinkError e) {
			System.err.println(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NativeLibFail") + e);
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened then return an initialization failure
			emailVerify.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			emailVerify.localException = new KettleException(emailVerify.localMsg, t);
			if (!testing) { throw emailVerify.localException; }
		}
	}

	/**
	 * Called to initialize the GeoCoder object
	 *
	 * @throws KettleException
	 */
	private void initGeo() throws KettleException {
		// Skip if not present
		GeoCoderMeta geoCoder = stepData.getGeoCoder();
		if (geoCoder == null) { return; }
		// Fail if not licensed
		if (!geoCoder.isLicensed()) {
			geoCoder.localMsg = MDCheck.getErrorString("NotLicensed");
			geoCoder.localException = new KettleException(MDCheck.getErrorString("InitializeService", geoCoder.localMsg));
			if (!testing && geoCoder.isEnabled()) { throw geoCoder.localException; }
			return;
		}
		// get the geo license string
		String geoLicense = geoCoder.getLicense();
		try {
			mdGeo Geo = null;
			String errorMsg = null;
			// Create object for geo-point level accuracy?
			int licensedProducts = stepData.getAdvancedConfiguration().getProducts();
			if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_GeoPoint) != 0) {
				try {
					Geo = DQTObjectFactory.newGeo();
				} catch (DQTObjectException e) {
					// Handle failure to create object
					geoCoder.localMsg = e.getMessage();
					geoCoder.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
					if (!testing) { throw geoCoder.localException; }
					return;
				}
				if (!Geo.SetLicenseString(geoLicense)) {
					errorMsg = MDCheck.getErrorString("SettingLicense", geoLicense);
					Geo.delete();
					Geo = null;
				} else {
					Geo.SetPathToGeoPointDataFiles(checkData.realDataPath);
					// Add path to canada files if licensed
					if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_CanadaGeo) != 0) {
						Geo.SetPathToGeoCanadaDataFiles(checkData.realDataPath);
					}
					if (Geo.InitializeDataFiles() != mdGeo.ProgramStatus.ErrorNone) {
						errorMsg = MDCheck.getErrorString("InitializeService", Geo.GetInitializeErrorString());
						Geo.delete();
						Geo = null;
					}
				}
			}
			// Create object for geo-code level accuracy?
			if ((Geo == null) && ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_GeoCode) != 0)) {
				Geo = DQTObjectFactory.newGeo();
				if (!Geo.SetLicenseString(geoLicense)) {
					errorMsg = MDCheck.getErrorString("SettingLicense", geoLicense);
					Geo.delete();
					Geo = null;
				} else {
					Geo.SetPathToGeoCodeDataFiles(checkData.realDataPath);
					// Add path to canada files if licensed
					if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_CanadaGeo) != 0) {
						Geo.SetPathToGeoCanadaDataFiles(checkData.realDataPath);
					}
					if (Geo.InitializeDataFiles() != mdGeo.ProgramStatus.ErrorNone) {
						errorMsg = MDCheck.getErrorString("InitializeService", Geo.GetInitializeErrorString());
						Geo.delete();
						Geo = null;
					}
				}
			}
			// Problem?
			if (errorMsg != null) {
				initFailed = true;
				geoCoder.localMsg = errorMsg;
				geoCoder.localException = new KettleException(errorMsg);
				if (!testing) { throw geoCoder.localException; }
				return;
			}
			// Get underlying database information
			if (Geo != null) {
				geoCoder.localDBDate = Geo.GetDatabaseDate();
				geoCoder.localDBBuildNo = Geo.GetBuildNumber();
			}
			// remember the geo coder object
			checkData.Geo = Geo;
		} catch (UnsatisfiedLinkError e) {
			System.err.println(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NativeLibFail") + e);
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened then return an initialization failure
			geoCoder.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			geoCoder.localException = new KettleException(geoCoder.localMsg, t);
			if (!testing) { throw geoCoder.localException; }
		}
	}

	/**
	 * Called to initialize the IPLocaror object
	 *
	 * @throws KettleException
	 */
	private void initIPLocator() throws KettleException {
		// Skip if not present
		IPLocatorMeta ipMeta = stepData.getIPLocator();
		if (ipMeta == null) { return; }
		// Fail testing if not licensed
		if (!ipMeta.isLicensed()) {
			ipMeta.localMsg = MDCheck.getErrorString("NotLicensed");
			ipMeta.localException = new KettleException(MDCheck.getErrorString("InitializeService", ipMeta.localMsg));
			if (!testing && ipMeta.isEnabled()) { throw ipMeta.localException; }
			return;
		}
		try {
			// Allocate IPLocator object and set it's license string
			mdIpLocator IPL;
			try {
				IPL = checkData.ipLocator = DQTObjectFactory.newIpLocator();
			} catch (DQTObjectException e) {
				// Handle failure to create object
				ipMeta.localMsg = e.getMessage();
				ipMeta.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw ipMeta.localException; }
				return;
			}
			String ipLicense = ipMeta.getLicense();
			if (IPL.SetLicenseString(ipLicense) == false) {
				initFailed = true;
				// Handle failure to set license string
				ipMeta.localMsg = MDCheck.getErrorString("SettingLicense", ipLicense);
				ipMeta.localException = new KettleException(MDCheck.getErrorString("InitializeService", ipMeta.localMsg));
				if (!testing) { throw ipMeta.localException; }
				return;
			}
			// Initialize the object
			IPL.SetPathToIpLocatorFiles(checkData.realDataPath);
			if (IPL.InitializeDataFiles() != mdIpLocator.ProgramStatus.ErrorNone) {
				initFailed = true;
				// Handle failure to initialize
				ipMeta.localMsg = MDCheck.getErrorString("InitializeService", IPL.GetInitializeErrorString());
				ipMeta.localException = new KettleException(ipMeta.localMsg);
				if (!testing) { throw ipMeta.localException; }
				return;
			}
			// Get database information
			ipMeta.localDBDate = IPL.GetDatabaseDate();
			ipMeta.localDBBuildNo = IPL.GetBuildNumber();
		} catch (UnsatisfiedLinkError e) {
			System.err.println(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NativeLibFail") + e);
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = false;
			// If anything unusual happened then return an initialization failure
			ipMeta.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			ipMeta.localException = new KettleException(ipMeta.localMsg, t);
			if (!testing) { throw ipMeta.localException; }
		}
	}

	/**
	 * Called to initialize the phone object
	 *
	 * @throws KettleException
	 */
	private void initPhone() throws KettleException {
		// Skip if not present
		PhoneVerifyMeta phoneVerify = stepData.getPhoneVerify();
		if (phoneVerify == null) { return; }
		// Fail if not licensed
		if (!phoneVerify.isLicensed()) {
			phoneVerify.localMsg = MDCheck.getErrorString("NotLicensed");
			phoneVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", phoneVerify.localMsg));
			if (!testing && phoneVerify.isEnabled()) { throw phoneVerify.localException; }
			return;
		}
		try {
			// Create the phone object
			mdPhone Phone;
			try {
				Phone = checkData.Phone = DQTObjectFactory.newPhone();
			} catch (DQTObjectException e) {
				// Handle failure to create object
				phoneVerify.localMsg = e.getMessage();
				phoneVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw phoneVerify.localException; }
				return;
			}
			String phoneLicense = phoneVerify.getLicense();
			if (!Phone.SetLicenseString(phoneLicense)) {
				initFailed = true;
				// Handle failure to set license string
				phoneVerify.localMsg = MDCheck.getErrorString("SettingLicense", phoneLicense);
				phoneVerify.localException = new KettleException(MDCheck.getErrorString("InitializeService", phoneVerify.localMsg));
				if (!testing) { throw phoneVerify.localException; }
				return;
			}
			// Initialize object
			if (Phone.Initialize(checkData.realDataPath) != mdPhone.ProgramStatus.ErrorNone) {
				initFailed = true;
				// handle failure to initialize
				phoneVerify.localMsg = MDCheck.getErrorString("InitializeService", Phone.GetInitializeErrorString());
				phoneVerify.localException = new KettleException(phoneVerify.localMsg);
				if (!testing) { throw phoneVerify.localException; }
				return;
			}
			// Return database info
			phoneVerify.localDBDate = Phone.GetDatabaseDate();
			phoneVerify.localDBBuildNo = Phone.GetBuildNumber();
		} catch (UnsatisfiedLinkError e) {
			System.err.println(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NativeLibFail") + e);
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened then return an initialization failure
			phoneVerify.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			phoneVerify.localException = new KettleException(phoneVerify.localMsg, t);
			if (!testing) { throw phoneVerify.localException; }
		}
	}

	private void initReporting() {
		//
		// init if needed
		//
		MDCheckMeta checkMeta = stepData.getMeta();
		if (checkData.numAddrValidations == null) {
			checkData.numAddrValidations = ReportStats.create(checkMeta.addrValidationReportCStat);
		}
		if (checkData.numAddrChanges == null) {
			checkData.numAddrChanges = ReportStats.create(checkMeta.addrChangeReportCStat);
		}
		if (checkData.numAddrErrors == null) {
			checkData.numAddrErrors = ReportStats.create(checkMeta.errorReportCStat);
		}
		if (checkData.numAddrOverview == null) {
			checkData.numAddrOverview = ReportStats.create(checkMeta.addrOverviewReportCStat);
		}
		if (checkData.numEmailOverview == null) {
			checkData.numEmailOverview = ReportStats.create(checkMeta.emailOverviewReportCStat);
		}
		if (checkData.numPhoneOverview == null) {
			checkData.numPhoneOverview = ReportStats.create(checkMeta.phoneOverviewReportCStat);
		}
		if (checkData.numGeoOverview == null) {
			checkData.numGeoOverview = ReportStats.create(checkMeta.geoOverviewReportCStat);
		}
		if (checkData.numNameOverview == null) {
			checkData.numNameOverview = ReportStats.create(checkMeta.nameOverviewFields);
		}
		if (checkData.resultStats == null) {
			checkData.resultStats = new ReportStats();
		}
	}

	/**
	 * Called to initialize the zip object
	 *
	 * @throws KettleException
	 */
	private void initZip() throws KettleException {
		// Skip if geocoder not present
		GeoCoderMeta geoCoder = stepData.getGeoCoder();
		if (geoCoder == null) { return; }
		// Ignore if geo coder object was created
		if (checkData.Geo != null) { return; }
		try {
			mdZip Zip = null;
			String errorMsg = null;
			// Ignore if address object not licensed
			int licensedProducts = stepData.getAdvancedConfiguration().getProducts();
			if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Address) != 0 || AdvancedConfigurationMeta.isCommunity()) {
				try {
					Zip = DQTObjectFactory.newZip();
				} catch (DQTObjectException e) {
					// Handle failure to create object
					geoCoder.localMsg = e.getMessage();
					geoCoder.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
					if (!testing) { throw geoCoder.localException; }
					return;
				}
				String zipLicense = stepData.getAddressVerify().getLicense();
				if (!Zip.SetLicenseString(zipLicense)) {
					errorMsg = MDCheck.getErrorString("SettingLicense", zipLicense);
					Zip.delete();
					Zip = null;
				} else if (Zip.Initialize(checkData.realDataPath, checkData.realDataPath, checkData.realDataPath) != mdZip.ProgramStatus.ErrorNone) {
					errorMsg = MDCheck.getErrorString("InitializeService", Zip.GetInitializeErrorString());
					Zip.delete();
					Zip = null;
				}
			}
			// Problem?
			if (errorMsg != null) {
				initFailed = true;
				geoCoder.localMsg = errorMsg;
				geoCoder.localException = new KettleException(errorMsg);
				if (!testing) { throw geoCoder.localException; }
				return;
			}
			// Get underlying database information
			if (Zip != null) {
				geoCoder.localZipDBDate = Zip.GetDatabaseDate();
				geoCoder.localZipDBExpiration = Zip.GetLicenseExpirationDate();
				geoCoder.localZipDBBuildNo = Zip.GetBuildNumber();
			}
			// Save the zip object
			checkData.Zip = Zip;
		} catch (UnsatisfiedLinkError e) {
			System.err.println(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NativeLibFail") + e);
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = true;
			// If anything unusual happened then return an initialization failure
			geoCoder.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			geoCoder.localException = new KettleException(geoCoder.localMsg, t);
			if (!testing) { throw geoCoder.localException; }
		}
	}

	/**
	 * Called to process address verifier requests
	 *
	 * @param requests
	 * @throws KettleValueException
	 */
	private void processAddrRequests(List<MDCheckRequest> requests) throws KettleValueException {
		if (stepData.getAddressVerify() != null) {
			stepData.getAddressVerify().doLocalRequests(checkData, requests);
		}
	}

	/**
	 * Called to process email requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processEmailRequests(List<MDCheckRequest> requests) throws KettleException {
		if (stepData.getEmailVerify() != null) {
			stepData.getEmailVerify().doLocalRequests(checkData, requests);
		}
	}

	/**
	 * Called to process geocoder requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processGeoRequests(List<MDCheckRequest> requests) throws KettleException {
		if (stepData.getGeoCoder() != null) {
			stepData.getGeoCoder().doLocalRequests(checkData, requests);
		}
	}

	/**
	 * Called to process IPLocator requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
//	private void processIPLocatorRequests(List<MDCheckRequest> requests) throws KettleException {
//		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject)){
//			return;
//		}
//		if (stepData.getIPLocator() != null) {
//			stepData.getIPLocator().doLocalRequests(checkData, requests);
//		}
//	}

	/**
	 * Called to process phone requests
	 *
	 * @param requests
	 * @throws KettleException
	 */
	private void processPhoneRequests(List<MDCheckRequest> requests) throws KettleValueException {
		if (stepData.getPhoneVerify() != null) {
			stepData.getPhoneVerify().doLocalRequests(checkData, requests);
		}
	}
}
