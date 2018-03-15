/*
 * Performs validation checks
 * Checks Performed:
 * Duplicate input column usage is not allowed. For example, if the user specified 'FullName' as an input for the Name
 * Splitter, he can't use it as an input to the Phone Verifier.
 * So far it's been a good rule, as 99% of the time this check exposes a mistake on the user's part.
 * Duplicate output column usage is also not allowed. Two outputs going to the same column is definitely a user error.
 * ** Filters - all expressions are checked to ensure that they are valid.
 * This is done in OutputFilterMeta using MDBinaryEvaluator.
 * Filters - checks against known result codes
 * Filters - no "(no filter)" filter should precede a specified filter.
 * Filters - check for output target.
 * Specific tests (CVC):
 * Name processing requires an input Full Name (Name processing will be skipped otherwise, no error reported).
 * Address processing requires: (Address OR Address2) AND ((City AND State) OR Zip)
 * GeoCoder processing requirements depend on the Input Mode:
 * o Address Object Results - that Address processing will be done
 * o Address Key - the AddressKey is required.
 * o Specified Fields - Same fields as Address Processing, above (but using the ones specified on the GeoCoder tab).
 * Phone validation requires an input Phone (otherwise, Phone processing is skipped, no error reported).
 * Email validation requires an input Email (otherwise, skipped, no error).
 */

package com.melissadata.kettle.businesscoder.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;

import com.melissadata.kettle.businesscoder.MDBusinessCoderData;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.cz.support.FilterTarget;

public class Validations {

	public static String getLicenceTimeStamp() {

		return licenceTimeStamp;
	}

	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {

		return BaseMessages.getString(PKG, "MDBusinessCoderDialog.Validation." + name, args);
	}

	public static boolean isDataExpiring(String Date) {

		Calendar today = Calendar.getInstance();
		Calendar expDate;
		boolean expiring = false;
		if (!Const.isEmpty(Date) && !Date.equals("N/A")) {
			expDate = stringToDate(Date);
			today.add(Calendar.DAY_OF_YEAR, 14);
			expiring = today.after(expDate);
		}
		return expiring;
	}

	public static void setLicenceTimeStamp(String licenceTimeStamp) {

		Validations.licenceTimeStamp = licenceTimeStamp;
	}

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
	 */
	private static File getVersionPropFile() {

		String propertyPath = MDBusinessCoderData.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		propertyPath = propertyPath.replaceAll("steps.*", "steps");
		File propFile = null;
		propFile = new File(new File(propertyPath), TAG_TIME_STAMP_FILE);
		return propFile;
	}

	/** method subtracts 1 from month to accommodate
	* Calendar starting at 0
	**/
	private static Calendar stringToDate(String sDate) {

		Calendar date = Calendar.getInstance();

		String[] YMD = new String[3];
		int year = 2011;
		int month = 5;
		int dayOfMonth = 15;

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
		} else {
			// TODO handel null values ?
		}

		if (sDate.equalsIgnoreCase("NEVER")) {
			date.add(Calendar.MONTH, 1);
		}

		return date;
	}

	private static Class<?>							PKG							= MDBusinessCoderMeta.class;
	private final String							TAG_LICENCE_TIME_STAMP		= "md_licence_timestamp";
	private final static String						TAG_TIME_STAMP_FILE			= "md_time_stamp.prop";
	private static final Pattern					RESULT_WILD_CODE_PAT		= Pattern.compile("[F]([SE|\\?][0-2|\\?][0-9|\\?])");//|[A][CEV|\\?][0-2|\\?]['*']$|[A][CEV|\\?]['*']$|[A]['*']");
	private static Properties						timeStampProps;
	private static String							licenceTimeStamp;
	public static boolean							checkDone					= false;
	private static List<String>						errors						= new ArrayList<String>();
	private static List<String>						warnings					= new ArrayList<String>();
	private boolean									somethingToShow				= false;
	public static boolean							showErrors					= false;
	public static boolean							showWarn					= false;
	private static final HashMap<String, String>	outFieldNames				= new HashMap<String, String>();
	private static final HashMap<String, String>	InFieldNames				= new HashMap<String, String>();

	public Validations() {

	}


	/** check filter rule order and target
	 * 
	 * @param meta
	 * @param warnings
	 * @param errors
	 */
	public void checkFilterOrder(MDBusinessCoderMeta meta, List<String> warnings, List<String> errors) {

		String tabMarker = BaseMessages.getString(PKG, "MDBusinessCoderDialog.Validation.OutputFilterTab.Marker");

		List<FilterTarget> filterTargets = meta.oFilterFields.filterTargets;

		for (int index = 0; index < filterTargets.size(); index++) {
			if (Const.isEmpty(filterTargets.get(index).getTargetStepname())) {
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDBusinessCoderDialog.Validation.NoTargetSelected", filterTargets.get(index).getName()));
			}
			if (Const.isEmpty(filterTargets.get(index).getRule())) {
				for (int i = index + 1; i < filterTargets.size(); i++) {
					if (!Const.isEmpty(filterTargets.get(i).getRule())) {
						warnings.add(tabMarker + BaseMessages.getString(PKG, "MDBusinessCoderDialog.Validation.BadFilterOrder"));
					}
				}
			}
		}
	}

	public void checkInitialization(MDBusinessCoderMeta meta) {
		//TODO 

	}

	public void clearInputList() {
		InFieldNames.clear();
	}

	public void clearOutputList() {
		outFieldNames.clear();
	}

	public boolean dateExpired(String date) {

		if (date.equalsIgnoreCase("EXPIRED")) { return true; }

		Calendar today = Calendar.getInstance();
		Calendar cDate = stringToDate(date);

		if (cDate.before(today)) { return true; }

		return false;
	}

	public void saveTimeProps() throws KettleException {

		// System.out.println("Saving props");
		// clear the properties object
		timeStampProps.clear();
		setProps(timeStampProps);
		// Save the new properties
		File defaultFile = getVersionPropFile();
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(defaultFile));
			timeStampProps.store(out, "MD Time Stamps");

		} catch (IOException e) {
			throw new KettleException("Problem reading " + defaultFile, e);

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public boolean showDialog() {

		return somethingToShow;
	}

	public void validate(MDBusinessCoderMeta meta, List<String> warnings, List<String> errors) {

		//checkAddressInputFields(meta, warnings, errors);
	}

	/**
	* verify if given result code matches a known result code
	**/
	public boolean verifyResultCode(String resultCode) {

		boolean codeMatch = false;  // Flag for if we have a match
		new ArrayList<String>();

		GetResultCodes();

		// for (String kc : knownCodesList){ // step through list and look for a match
		// if(resultCode.equalsIgnoreCase(kc)){
		// codeMatch = true; // if match found set flag to true
		// }

		Matcher matcher = RESULT_WILD_CODE_PAT.matcher(resultCode.trim());
		if (matcher.matches()) {
			// resultCode[0] = matcher.group(1);
			// return true;
			codeMatch = true;
		}
		// }

		return codeMatch;
	}

	public void writeLogMessage(LogChannelInterface log) {

		// write log message if any
		if ((warnings.size() > 0) || (errors.size() > 0)) {
			// Build the warning/error message
			StringBuffer message = new StringBuffer();
			message.append(getValidationMessage("MessageBoxPreamble")).append("\n");
			message.append("\n");
			if (warnings.size() > 0) {
				if (warnings.size() == 1) {
					message.append(getValidationMessage("MessageBoxWarning")).append("\n");
				} else {
					message.append(getValidationMessage("MessageBoxWarnings", "" + warnings.size())).append("\n");
				}
				for (String warning : warnings) {
					message.append("   -  ").append(warning).append("\n");
				}
				message.append("\n");

				log.logBasic(message.toString());
			}

			if (errors.size() > 0) {
				message.delete(0, message.length());
				if (errors.size() == 1) {
					message.append(getValidationMessage("MessageBoxError")).append("\n");
				} else {
					message.append(getValidationMessage("MessageBoxErrors", "" + errors.size())).append("\n");
				}
				for (String error : errors) {
					message.append("   -  ").append(error).append("\n");
				}

				log.logError(message.toString());
			}
		}
	}

	private Properties getProperties() {

		// Get the properties from the properties file
		File propFile = getVersionPropFile();
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(propFile));
			props.load(in);

		} catch (IOException e) {
			// Create a default
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
	 * Returns the defined error codes that begin with the given prefix
	 **/
	private Collection<String> getResultCodes(String category, String prefix) {

		Set<String> rcs = new TreeSet<String>();
		ResourceBundle bundle = GlobalMessages.getBundle(PKG.getPackage().getName() + ".messages.messages", PKG);
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String rc = keys.nextElement();
				String categoryPrefix = "MDBusinessCoder.ResultCode." + category + ".";
				if (rc.startsWith(categoryPrefix + prefix)) {
					rc = rc.substring(categoryPrefix.length());
					int i = rc.indexOf(".");
					if (i != -1) {
						rc = rc.substring(0, i);
					}

					rcs.add(rc);
				}
			}
		}
		return rcs;
	}

	/** 
	* Generates a list of result codes -- uses getResultCodes(String category, String prefix)
	* Slightly modified from MDControlSpaceKeyAdapter.java to get result code without message
	**/
	private Collection<String> GetResultCodes() {

		List<String> rcs = new ArrayList<String>();

		// Address verify is called as part of smart mover
		rcs.addAll(getResultCodes("Addr", "AV"));	// address success
		rcs.addAll(getResultCodes("Addr", "AE")); 	// address error
		rcs.addAll(getResultCodes("Addr", "AC"));	// address correction

		return rcs;
	}

	
	private void setProps(Properties prop) {
		prop.setProperty(TAG_LICENCE_TIME_STAMP, licenceTimeStamp);

	}

}
