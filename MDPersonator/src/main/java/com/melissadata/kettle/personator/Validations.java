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
package com.melissadata.kettle.personator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;

import com.melissadata.kettle.personator.data.PersonatorFields;

public class Validations {
	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {
		return BaseMessages.getString(PKG, "MDPersonatorDialog.Validation." + name, args);
	}
	private static Class<?>							PKG						= Validations.class;
	private static final Pattern					RESULT_WILD_CODE_PAT	= Pattern.compile("[V][RS]?[0-4]?['*']$|[D][A]?[0-4]?['*']$|[N][SE]?[09]?['*']$|[A][SEC]?[0-2]?['*']$|[G][SE]?[0]?['*']$|[P][SE]?[01]?['*']$|[E][SE]?[01]?['*']$");
	public static boolean							checkDone;
	private static final HashMap<String, String>	outFieldNames			= new HashMap<String, String>();
	private static final HashMap<String, String>	InFieldNames			= new HashMap<String, String>();

	public Validations() {
	}

	// Check needed input for address processing and duplicate input columns
	public void checkAddressInputFields(MDPersonatorMeta meta, List<String> warnings, List<String> errors) {
		String centricHint = meta.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue;
		boolean hasAddr = true;
		String missingFields = "";
		if (centricHint.equals("Address")) {
			hasAddr = false;
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue) || !Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue))
					&& (!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ZIP).metaValue))) {

				hasAddr = true;
			} else if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue) || !Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue))
					&& (!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_CITY).metaValue) && !Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_STATE).metaValue))) {
				hasAddr = true;
			}
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue))) {
				hasAddr = true;
			}
//			 if(Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue)){
//
//			 }
//			if(Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue)){
//
//			}
//			if(Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue)){
//
//			}

			if (!hasAddr) {
				errors.add(BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.MissingAddress", ""));
			}
		}
	}

	public void checkAutoInputFields(MDPersonatorMeta meta, List<String> warnings, List<String> errors) {
		String centricHint = meta.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue;
		boolean hasAuto = true;

		if (centricHint.equals("Auto")) {
			hasAuto = false;
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue) || !Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue))
					&& (!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ZIP).metaValue))) {
				hasAuto = true;
			} else if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue) || !Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue))
					&& (!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_CITY).metaValue) && !Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_STATE).metaValue))) {
				hasAuto = true;
			}
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_EMAIL).metaValue))) {
				hasAuto = true;
			}
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_PHONE).metaValue))) {
				hasAuto = true;
			}
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue))) {
				hasAuto = true;
			}
			if (!hasAuto) {
				errors.add(BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.MissingAuto"));
			}
		}
	}

	public void checkEmailInputFields(MDPersonatorMeta meta, List<String> warnings, List<String> errors) {
		String centricHint = meta.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue;
		boolean hasEmail = true;
		if (centricHint.equals("Email")) {
			hasEmail = false;
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_EMAIL).metaValue))) {
				hasEmail = true;
			}
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue))) {
				hasEmail = true;
			}
			if (!hasEmail) {
				errors.add(BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.MissingEmail"));
			}
		}
	}

	// check filter rule order and target
	public void checkFilterOrder(MDPersonatorMeta meta, List<String> warnings, List<String> errors) {
		String tabMarker = BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.OutputFilterTab.Marker");
		List<FilterTarget> filterTargets = meta.oFilterFields.filterTargets;
		for (int index = 0; index < filterTargets.size(); index++) {
			if (Const.isEmpty(filterTargets.get(index).getTargetStepname())) {
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.NoTargetSelected", filterTargets.get(index).getName()));
			}
			if (Const.isEmpty(filterTargets.get(index).getRule())) {
				for (int i = index + 1; i < filterTargets.size(); i++) {
					if (!Const.isEmpty(filterTargets.get(i).getRule())) {
						warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.BadFilterOrder"));
					}
				}
			}
		}
	}

	// check address output
	public void checkOutputFields(MDPersonatorMeta meta, List<String> warnings, List<String> errors) {
		boolean allEmpty = false;
		allEmpty = (meta.personatorFields.outputFields.size() <= 1);
		if (allEmpty) {
			errors.add(BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.MissingOutput"));
		}
	}

	public void checkPhoneInputFields(MDPersonatorMeta meta, List<String> warnings, List<String> errors) {
		String centricHint = meta.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue;
		boolean hasPhone = true;
		if (centricHint.equals("Phone")) {
			hasPhone = false;
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_PHONE).metaValue))) {
				hasPhone = true;
			}
			if ((!Const.isEmpty(meta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue))) {
				hasPhone = true;
			}
			if (!hasPhone) {
				errors.add(BaseMessages.getString(PKG, "MDPersonatorDialog.Validation.MissingPhone"));
			}
		}
	}

	public void clearInputList() {
		InFieldNames.clear();
	}

	public void clearOutputList() {
		outFieldNames.clear();
	}

	/*
	 * Returns the defined error codes that begin with the given prefix
	 */
	private Collection<String> getResultCodes(String category, String prefix) {
		Set<String> rcs = new TreeSet<String>();
		ResourceBundle bundle = GlobalMessages.getBundle(PKG.getPackage().getName() + ".messages.messages", PKG);
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String rc = keys.nextElement();
				String categoryPrefix = "MDPersonator.ResultCode." + category + ".";
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

	// Generates a list of result codes -- uses getResultCodes(String category,
	// String prefix)
	// Slightly modified from MDControlSpaceKeyAdapter.java to get result code
	// without message
	private Collection<String> GetResultCodes() {
		List<String> rcs = new ArrayList<String>();
		rcs.addAll(getResultCodes("Verify", "VR"));
		rcs.addAll(getResultCodes("Verify", "VS"));
		rcs.addAll(getResultCodes("Append", "DA"));
		rcs.addAll(getResultCodes("Name", "NS")); // address success
		rcs.addAll(getResultCodes("Name", "NE")); // address error
		rcs.addAll(getResultCodes("Addr", "AS")); // address success
		rcs.addAll(getResultCodes("Addr", "AE")); // address error
		rcs.addAll(getResultCodes("Addr", "AC")); // address check
		rcs.addAll(getResultCodes("Geo", "GS")); // address success
		rcs.addAll(getResultCodes("Geo", "GE")); // address error
		rcs.addAll(getResultCodes("Phone", "PS")); // address success
		rcs.addAll(getResultCodes("Phone", "PE")); // address error
		rcs.addAll(getResultCodes("Email", "ES")); // address success
		rcs.addAll(getResultCodes("Email", "EE")); // address error
		return rcs;
	}

	public void validate(MDPersonatorMeta meta, List<String> warnings, List<String> errors) {
		checkAddressInputFields(meta, warnings, errors);
		checkPhoneInputFields(meta, warnings, errors);
		checkEmailInputFields(meta, warnings, errors);
		checkAutoInputFields(meta, warnings, errors);
		checkOutputFields(meta, warnings, errors);
	}

	// verify if given result code matches a known result code
	//
	public boolean verifyResultCode(String resultCode) {
		Collection<String> knownCodesList = new ArrayList<String>();
		Matcher matcher = RESULT_WILD_CODE_PAT.matcher(resultCode.trim());
		if (matcher.matches())
			return true;
		knownCodesList = GetResultCodes();
		for (String kc : knownCodesList) {
			if (resultCode.equalsIgnoreCase(kc))
				return true;
		}
		return false;
	}
}
