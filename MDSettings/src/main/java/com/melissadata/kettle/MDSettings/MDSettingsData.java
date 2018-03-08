package com.melissadata.kettle.MDSettings;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import com.melissadata.cz.support.MDPropTags;

import java.net.URL;
import java.util.List;

public class MDSettingsData {

	public static String  proxyMsg;
	public        License primeLicense;
	public        License trialLicense;
	public        String  productList;
	public        String  realDataPath;
	public        String  cleanserOpertionsPath;
	// MU
	public        String  matchUpDataPath;
	public        String  matchUpWorkPath;
	public boolean muCommunity  = true;
	public boolean muLite       = false;
	public boolean muEnterprise = false;
	// Presort
	public String       preSortDataPath;
	// PAF id
	public String       realPAFID;
	// URL for the web services
	public URL          realWebNameParserURL;
	public URL          realWebAddressVerifierURL;
	public URL          realWebGeoCoderURL;
	public URL          realWebPhoneVerifierURL;
	public URL          realWebEmailVerifierURL;
	public URL          realWebRBDIndicatorURL;
	public URL          realWebIPLocatorURL;
	public URL          realWebBusCoderURL;
	public URL          realPersonatorURL;
	public URL          realPersonatorWorldURL;
	public URL          realPropertyURL;
	public URL          realGlobalNameURL;
	public URL          realGlobalAddressURL;
	public URL          realGlobalPhoneURL;
	public URL          realGlobalEmailURL;
	public URL          realCVSNameParserURL;
	public URL          realCVSAddressVerifierURL;
	public URL          realCVSGeoCoderURL;
	public URL          realCVSPhoneVerifierURL;
	public URL          realCVSEmailVerifierURL;
	public URL          realCVSRBDIndicatorURL;
	public URL          realCVSIPLocatorURL;
	public URL          realCVSGlobalVerifyURL;
	public URL          realNCOAURL;
	public URL          realCCOAURL;
	public String       serverURL;
	public String       webEncoding;
	/*
	 * TIMEOUT
	 */
	public int          realCVSTimeout;
	public int          realCVSRetries;
	public boolean      cvsFailover;
	public boolean      retryAppliance;
	/*
	 * REQUESTS
	 */
	public int          failoverInterval;
	// Abort handling
	public boolean      webAbortOnError;
	public boolean      cvsAbortOnError;
	// Optional proxy settings
	public String       realProxyHost;
	public int          realProxyPort;
	public String       realProxyPass;
	public String       realProxyUser;
	// local name data
	public String       nameMsg;
	public String       nameDate;
	public String       nameExpiration;
	public String       nameBuild;
	// local Address data
	public String       addrMsg;
	public String       localAddressDBDate;
	public String       localAddressBuild;
	public String       localAddressExpiration;
	public String       localAddressCADBDate;
	public String       localAddressCAExpiration;
	public String       RBDIDate;
	// localGeoCoder data
	public String       geoMsg;
	public String       geoDate;
	public String       geoExpiration;
	public String       geoBuild;
	// localphone data
	public String       phoneMsg;
	public String       phoneDate;
	public String       phoneExpiration;
	public String       phoneBuild;
	// localemail data
	public String       emailMsg;
	public String       emailDate;
	public String       emailExpiration;
	public String       emailBuild;
	// local IP data
	public String       ipLocMsg;
	public String       ipLocDate;
	public String       ipLocExpiration;
	public String       ipLocBuild;
	// local profiler data
	public String       profilerMsg;
	public String       profilerDate;
	public String       profilerExpiration;
	public String       profilerBuild;
	// local cleanser data
	public String       cleanserMsg;
	public String       cleanserDate;
	public String       cleanserExpiration;
	public String       cleanserBuild;
	// local PreSort data
	public String       preSortMsg;
	public String       preSortDate;
	public String       preSortExpiration;
	public String       preSortBuild;
	// local MatchUp data
	public String       matchUPMsg;
	public String       matchUPDate;
	public String       matchUPExpiration;
	public String       matchUPBuild;
	// web name data
	public String       webNameMsg;
	public String       webNameVersion;
	// local Address data
	public String       webAddrMsg;
	public String       webAddrVersion;
	public String       webRBDIMsg;
	public String       webRBDIVersion;
	// web GeoCoder data
	public String       webGeoMsg;
	public String       webGeoVersion;
	// web phone data
	public String       webPhoneMsg;
	public String       webPhoneVersion;
	// web email data
	public String       webEmailMsg;
	public String       webEmailVersion;
	// web IPLocator
	public String       webIPMsg;
	public String       webIPVersion;
	// web Smart Mover
	public String       webSMMsg;
	public String       webPAFMsg;
	public String       webSMVersion;
	// web Global verify
	public String       webGlobalAddressMsg;
	public String       webGlobalAddressVersion;
	public String       webGlobalNameMsg;
	public String       webGlobalNameVersion;
	public String       webGlobalPhoneMsg;
	public String       webGlobalPhoneVersion;
	public String       webGlobalEmailMsg;
	public String       webGlobalEmailVersion;
	// local Global Address data
	public String       globalAddrMsg;
	public String       globalAddressDBDate;
	public String       globalAddressBuild;
	public String       globalAddressExpiration;
	// web Personator
	public String       webPersonatorMsg;
	public String       webPersonatorVersion;
	// web Personator World
	public String       webPersonatorWorldMsg;
	public String       webPersonatorWorldVersion;
	// web PropertyService
	public String       webPropertyMsg;
	public String       webPropertyVersion;
	// web Business Coder
	public String       webBusCoderMsg;
	public String       webBusCoderVersion;
	public List<String> errorList;
	public List<String> warnList;
	//	public boolean          showCommunityMessage = false;
	public ProcessingType smProcessType = ProcessingType.Individual;
	// Component Web Settings
	// Global Verify
	private int globalVerifyRequests;
	private int globalVerifyThreads;
	private int globalVerifyWebTimeout;
	private int globalVerifyWebRetries;
	// Property Service
	private int propertyRequests;
	private int propertyThreads;
	private int propertyWebTimeout;
	private int propertyWebRetries;
	// Personator
	private int personatorRequests;
	private int personatorThreads;
	private int personatorWebTimeout;
	private int personatorWebRetries;
	// Personator World
	private int personatorWorldRequests;
	private int personatorWorldThreads;
	private int personatorWorldWebTimeout;
	private int personatorWorldWebRetries;
	// BusinessCoder
	private int businessCoderRequests;
	private int businessCoderThreads;
	private int businessCoderWebTimeout;
	private int businessCoderWebRetries;
	// MDCheck
	private int contactVerifyRequests;
	private int contactVerifyThreads;
	private int contactVerifyWebTimeout;
	private int contactVerifyWebRetries;
	// IP Locator
	private int ipLocatorRequests;
	private int ipLocatorThreads;
	private int ipLocatorWebTimeout;
	private int ipLocatorWebRetries;
	// SmartMover
	private int smartMoverRequests;
	private int smartMoverThreads;
	private int smartMoverWebTimeout;
	private int smartMoverWebRetries;

	public int getRequestLimit(String id) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			return MDPropTags.CONTACTVERIFY_REQUEST_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			return MDPropTags.IPLOCATOR_REQUEST_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			return MDPropTags.SMARTMOVER_REQUEST_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			return MDPropTags.GLOBALVERIFY_REQUEST_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			return MDPropTags.PROPERTY_REQUEST_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			return MDPropTags.PERSONATOR_REQUEST_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			return MDPropTags.PERSONATOR_WORLD_REQUEST_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			return MDPropTags.BUSINESS_CODER_REQUEST_LIMIT;
		}

		return 0;
	}

	public int getRequests(String id) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			return contactVerifyRequests;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			return ipLocatorRequests;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			return smartMoverRequests;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			return globalVerifyRequests;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			return propertyRequests;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			return personatorRequests;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			return personatorWorldRequests;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			return businessCoderRequests;
		}

		return 0;
	}

	public int getRetries(String id) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			return contactVerifyWebRetries;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			return ipLocatorWebRetries;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			return smartMoverWebRetries;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			return globalVerifyWebRetries;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			return propertyWebRetries;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			return personatorWebRetries;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			return personatorWorldWebRetries;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			return businessCoderWebRetries;
		}

		return 0;
	}

	public int getThreadLimit(String id) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			return MDPropTags.CONTACTVERIFY_THREAD_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			return MDPropTags.IPLOCATOR_THREAD_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			return MDPropTags.SMARTMOVER_THREAD_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			return MDPropTags.GLOBALVERIFY_THREAD_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			return MDPropTags.PROPERTY_THREAD_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			return MDPropTags.PERSONATOR_THREAD_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			return MDPropTags.PERSONATOR_WORLD_THREAD_LIMIT;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			return MDPropTags.BUSINESS_CODER_THREAD_LIMIT;
		}

		return 0;
	}

	public int getThreads(String id) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			return contactVerifyThreads;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			return ipLocatorThreads;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			return smartMoverThreads;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			return globalVerifyThreads;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			return propertyThreads;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			return personatorThreads;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			return personatorWorldThreads;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			return businessCoderThreads;
		}

		return 0;
	}

	public int getTimeout(String id) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			return contactVerifyWebTimeout;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			return ipLocatorWebTimeout;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			return smartMoverWebTimeout;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			return globalVerifyWebTimeout;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			return propertyWebTimeout;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			return personatorWebTimeout;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			return personatorWorldWebTimeout;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			return businessCoderWebTimeout;
		}

		return 0;
	}

	public void setRequests(String id, int val) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			contactVerifyRequests = val;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			ipLocatorRequests = val;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			smartMoverRequests = val;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			globalVerifyRequests = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			propertyRequests = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			personatorRequests = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			personatorWorldRequests = val;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			businessCoderRequests = val;
		}
	}

	public void setRetries(String id, int val) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			contactVerifyWebRetries = val;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			ipLocatorWebRetries = val;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			smartMoverWebRetries = val;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			globalVerifyWebRetries = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			propertyWebRetries = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			personatorWebRetries = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			personatorWorldWebRetries = val;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			businessCoderWebRetries = val;
		}
	}

	public String getWebEncoding() {

		return webEncoding;
	}

	public void setWebEncoding(String webEncoding) {

		this.webEncoding = webEncoding;
	}

	public void setThreads(String id, int val) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			contactVerifyThreads = val;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			ipLocatorThreads = val;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			smartMoverThreads = val;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			globalVerifyThreads = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			propertyThreads = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			personatorThreads = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			personatorWorldThreads = val;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			businessCoderThreads = val;
		}
	}

	public void setTimeout(String id, int val) {

		if (id.equals(MDPropTags.MENU_ID_CONTACTVERIFY)) {
			contactVerifyWebTimeout = val;
		}
		if (id.equals(MDPropTags.MENU_ID_IPLOCATOR)) {
			ipLocatorWebTimeout = val;
		}
		if (id.equals(MDPropTags.MENU_ID_SMARTMOVER)) {
			smartMoverWebTimeout = val;
		}
		if (id.equals(MDPropTags.MENU_ID_GLOBALVERIFY)) {
			globalVerifyWebTimeout = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PROPERTY)) {
			propertyWebTimeout = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR)) {
			personatorWebTimeout = val;
		}
		if (id.equals(MDPropTags.MENU_ID_PERSONATOR_WORLD)) {
			personatorWorldWebTimeout = val;
		}
		if (id.equals(MDPropTags.MENU_ID_BUSINESS_CODER)) {
			businessCoderWebTimeout = val;
		}
	}

	public enum Countries {
		US(BaseMessages.getString("MDSettings.Countries.US")), Canada(BaseMessages.getString("MDSettings.Countries.Canada")), USCanada(BaseMessages.getString("MDSettings.Countries.USCanada")),;
		private String description;

		private Countries(String description) {

			this.description = description;
		}

		public static Countries decode(String value) throws KettleException {

			try {
				return Countries.valueOf(value);
			} catch (Exception e) {
				throw new KettleException("Unknown Countries: " + value, e);
			}
		}

		public String encode() {

			return name();
		}

		public String getDescription() {

			return description;
		}

		@Override
		public String toString() {

			return getDescription();
		}
	}

	public enum ProcessingType {
		Standard(BaseMessages.getString("MDCheckMeta.ProcessingType.Standard")), IndividualAndBusiness(BaseMessages.getString("MDCheckMeta.ProcessingType.IndividualAndBusiness")), Individual(
				BaseMessages.getString("MDCheckMeta.ProcessingType.Individual")), Business(BaseMessages.getString("MDCheckMeta.ProcessingType.Business")), Residential(BaseMessages.getString("MDCheckMeta.ProcessingType.Residential")),;
		private String description;

		private ProcessingType(String description) {

			this.description = description;
		}

		public static ProcessingType decode(String value) throws KettleException {

			try {
				return ProcessingType.valueOf(value);
			} catch (Exception e) {
				throw new KettleException("Unknown Processing Type: " + value, e);
			}
		}

		public String encode() {

			return name();
		}

		public String getDescription() {

			return description;
		}

		@Override
		public String toString() {

			return getDescription();
		}
	}

	public class License {

		public String  licenseString;
		public String  testResult;
		public String  expiration;
		public String  options;
		public int     retVal;
		public int     geoLevel;
		public String  CustomerID;
		public String  products;
		public String  licCode;
		public boolean isNonExpiring;
		// BusCoder
		public String  hasBusCoder;

		public boolean isEnterprise(int nProduct) {

			if ((retVal & nProduct) != 0) {
				return true;
			}
			return false;
		}

		public boolean isCommunity(int nProduct) {

			if ((MDPropTags.MDLICENSE_Community & retVal) != 0) {
				return true;
			}

			return false;
		}
	}
}
