package com.melissadata.kettle.personator;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.melissadata.kettle.MDSettings.SettingsTags;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowDataUtil;

import com.melissadata.cz.MDProps;
import com.melissadata.kettle.personator.MDPersonatorCVRequest.PersonatorResults;
import com.melissadata.kettle.personator.MDPersonatorMeta.OutputPhoneFormat;
import com.melissadata.kettle.personator.MDPersonatorMeta.RowOutput;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.support.MDPropTags;

public class MDPersonatorEngine {
	PersonatorFields				personatorFields;
	private OutputPhoneFormat		optionPhoneFormat;
	private static final Pattern	RESULT_NO_AREA_PAT		= Pattern.compile("\\d\\d\\d\\d\\d\\d\\d");
	private static final Pattern	RESULT_AREA_PAT			= Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
	private static final Pattern	RESULT_NO_AREA_EXT_PAT	= Pattern.compile("\\d\\d\\d\\d\\d\\d\\d[A-Za-z].?.?.?.?.?.?.?.?.?|\\d\\d\\d\\d\\d\\d\\d\\d\\d?");
	private static final Pattern	RESULT_AREA_EXT_PAT		= Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d..?.?.?.?.?.?.?.?.?");

	public MDPersonatorEngine(PersonatorFields personatorFields) {
		super();
		this.personatorFields = personatorFields;
	}

	/**
	 * This will format a phone number according the specified format option
	 *
	 * @param phone
	 * @return
	 */
	private String formatPhone(String phone) {
		if (Const.isEmpty(phone))
			return "";
		boolean patFound = false;
		boolean noArea = true;
		String formatedPhone = "";
		String area = "";
		String prefix = "";
		String suffix = "";
		String ext = "";
		Matcher matcher = RESULT_NO_AREA_PAT.matcher(phone.trim());
		if (matcher.matches()) {
			prefix = phone.substring(0, 3);
			suffix = phone.substring(3);
			patFound = true;
		}
		matcher = RESULT_NO_AREA_EXT_PAT.matcher(phone.trim());
		if (matcher.matches()) {
			prefix = phone.substring(0, 3);
			suffix = phone.substring(3, 7);
			ext = " " + phone.substring(7);
			patFound = true;
		}
		matcher = RESULT_AREA_PAT.matcher(phone.trim());
		if (matcher.matches()) {
			area = phone.substring(0, 3);
			prefix = phone.substring(3, 6);
			suffix = phone.substring(6);
			patFound = true;
			noArea = false;
		}
		matcher = RESULT_AREA_EXT_PAT.matcher(phone.trim());
		if (matcher.matches()) {
			area = phone.substring(0, 3);
			prefix = phone.substring(3, 6);
			suffix = phone.substring(6, 10);
			ext = " " + phone.substring(10);
			patFound = true;
			noArea = false;
		}
		if (!patFound)
			return phone;
		switch (optionPhoneFormat) {
			case FORMAT1:
				if (noArea) {
					formatedPhone = prefix + "-" + suffix + ext;
				} else {
					formatedPhone = "(" + area + ") " + prefix + "-" + suffix + ext;
				}
				break;
			case FORMAT2:
				if (noArea) {
					formatedPhone = prefix + " " + suffix + ext;
				} else {
					formatedPhone = "(" + area + ") " + prefix + " " + suffix + ext;
				}
				break;
			case FORMAT3:
				if (noArea) {
					formatedPhone = prefix + "-" + suffix + ext;
				} else {
					formatedPhone = area + "-" + prefix + "-" + suffix + ext;
				}
				break;
			case FORMAT4:
				if (noArea) {
					formatedPhone = prefix + " " + suffix + ext;
				} else {
					formatedPhone = area + " " + prefix + " " + suffix + ext;
				}
				break;
			case FORMAT5:
				if (noArea) {
					formatedPhone = prefix + "." + suffix + ext;
				} else {
					formatedPhone = area + "." + prefix + "." + suffix + ext;
				}
				break;
			case FORMAT6:
				if (noArea) {
					formatedPhone = prefix + suffix + ext;
				} else {
					formatedPhone = area + prefix + suffix + ext;
				}
				break;
		}
		return formatedPhone;
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if licensed
		boolean isLicensed = isLicensed();
		boolean minInputFields = true;
		return isLicensed && minInputFields;
	}

	/**
	 * @return true if address verification is licensed
	 */
	public boolean isLicensed() {
		// Check if product is licensed
		int retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RETVAL, "0"));
		//retVal |= Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_TRIAL_RETVAL, "0"));
		if ((retVal & SettingsTags.MDLICENSE_Personator) != 0 || (retVal & SettingsTags.MDLICENSE_Community) != 0)
			return true;
		else
			return false;
	}

	/**
	 * Called to process the results of either the local or web services
	 *
	 * @param checkData
	 * @param requests
	 */
	public void outputData(MDPersonatorData checkData, List<MDPersonatorRequest> requests) {
		// Skip if not enabled
		if (!isEnabled())
			return;
		// Output each request's results
		for (MDPersonatorRequest mdPersonatorRequest : requests) {
			MDPersonatorCVRequest request = (MDPersonatorCVRequest) mdPersonatorRequest;
			// Output the address results
			PersonatorResults personatorResults = request.personatorResults;
			if ((personatorResults != null) && personatorResults.valid) {
				// Extract the rest of the results
				HashMap<Integer, RowOutput> selectedOutput = MDPersonatorMeta.getOutputgroups();
				RowOutput ro;
				for (int n = 0; n < selectedOutput.size(); n++) {
					ro = selectedOutput.get(n);
					if (ro.isAdded) {
						if ("PhoneNumber".equals(ro.outputName)) {
							optionPhoneFormat = MDPersonatorMeta.getPhoneFormat();
							request.addOutputData(formatPhone(personatorResults.outputFields.get(ro.outputName)));
						} else {
							request.addOutputData(personatorResults.outputFields.get(ro.outputName));
						}
					}
				}
				if (personatorResults != null) {
					request.resultCodes.addAll(personatorResults.resultCodes);
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += personatorFields.fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
			}
		}
	}
}
