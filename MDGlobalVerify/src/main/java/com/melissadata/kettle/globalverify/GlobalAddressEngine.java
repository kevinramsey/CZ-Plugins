package com.melissadata.kettle.globalverify;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.globalverify.data.AddressFields;
import com.melissadata.kettle.globalverify.request.AddressRequest.AddrResults;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowDataUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalAddressEngine {

	private static final String[]    CHANGECODES = { "AC01", "AC02", "AC03", "AC09", "AC10", "AC11", "AC12", "AC13", "AC14", "AC15", "AC16", "AC17" };
	private static final String[]    ERRORCODES  = { "AE01", "AE02", "AE03", "AE05", "AE08", "AE09", "AE10", "AE11", "AE12", "AE13", "AE14", "AE17" };
	private static final Set<String> changeCodes = new HashSet<String>();
	private static final Set<String> errorCodes  = new HashSet<String>();
	static {
		Collections.addAll(changeCodes, CHANGECODES);
		Collections.addAll(errorCodes, ERRORCODES);
	}
	private AddressFields       addrFields = null;
	private LogChannelInterface log        = null;

	public GlobalAddressEngine(AddressFields addrFields, LogChannelInterface log) {

		super();
		this.addrFields = addrFields;
		this.log = log;
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if licensed
		boolean isLicensed     = isLicensed();
		boolean minInputFields = true;
		// Enabled only if there are input fields
		if (Const.isEmpty(addrFields.inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE1).metaValue)) {
			minInputFields = false;
		}
		return isLicensed && minInputFields;
	}

	/**
	 * @return true if address verification is licensed
	 */
	public boolean isLicensed() {
		// Check if product is licensed
		int retVal = Integer.parseInt(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, "0"));
		if ((retVal & MDPropTags.MDLICENSE_GlobalVerify) != 0 || (retVal & MDPropTags.MDLICENSE_Community) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Called to process the results of either the local or web services
	 *
	 * @param checkData
	 * @param requests
	 */
	public void outputData(MDGlobalData checkData, List<MDGlobalRequest> requests) {
		// Skip if not enabled
		if (!isEnabled()) {
			return;
		}

		log.logDebug("Global Address engine output data. Request size = " + requests.size());
		// Output each request's results
		for (MDGlobalRequest request : requests) {
			// Output the address results
			AddrResults addrResults = request.addressRequest.addrResults;
			if ((addrResults != null) && addrResults.valid) {
				output(request, addrResults);

				if (addrResults != null) {
					request.resultCodes.addAll(addrResults.resultCodes);
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += addrFields.fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
			}
		}
	}

	private void output(MDGlobalRequest request, AddrResults addrResults) {

		log.logRowlevel("Global Address engine output record " + request.outputData.toString());
		for (String key : addrFields.webOutputFields.keySet()) {
			if ((key == AddressFields.TAG_OUTPUT_ORGANIZATION) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ORGANIZATION).metaValue)) {
				request.addOutputData(addrResults.Organization);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE1) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue)) {
				request.addOutputData(addrResults.Address1);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE2) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE2).metaValue)) {
				request.addOutputData(addrResults.Address2);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE3) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE3).metaValue)) {
				request.addOutputData(addrResults.Address3);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE4) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE4).metaValue)) {
				request.addOutputData(addrResults.Address4);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE5) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE5).metaValue)) {
				request.addOutputData(addrResults.Address5);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE6) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE6).metaValue)) {
				request.addOutputData(addrResults.Address6);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE7) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE7).metaValue)) {
				request.addOutputData(addrResults.Address7);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_LINE8) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE8).metaValue)) {
				request.addOutputData(addrResults.Address8);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_NAME) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_NAME).metaValue)) {
				// FIXME no Country name when US ?
				request.addOutputData(addrResults.CountryName);
			} else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_LOCALITY) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_LOCALITY).metaValue)) {
				request.addOutputData(addrResults.DependentLocality);
			} else if ((key == AddressFields.TAG_OUTPUT_LOCALITY) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LOCALITY).metaValue)) {
				request.addOutputData(addrResults.Locality);
			} else if ((key == AddressFields.TAG_OUTPUT_ADMINISTRATIVE_AREA) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADMINISTRATIVE_AREA).metaValue)) {
				request.addOutputData(addrResults.AdministrativeArea);
			} else if ((key == AddressFields.TAG_OUTPUT_POSTAL_CODE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTAL_CODE).metaValue)) {
				request.addOutputData(addrResults.PostalCode);
			} else if ((key == AddressFields.TAG_OUTPUT_DELIVERY_LINE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DELIVERY_LINE).metaValue)) {
				request.addOutputData(addrResults.DeliveryLine);
			} else if ((key == AddressFields.TAG_OUTPUT_FORMATED_ADDRESS) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_FORMATED_ADDRESS).metaValue)) {
				request.addOutputData(addrResults.FormattedAddress);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_TYPE).metaValue)) {
				request.addOutputData(addrResults.AddressTypeCode);
			} else if ((key == AddressFields.TAG_OUTPUT_ADDRESS_KEY) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_KEY).metaValue)) {
				request.addOutputData(addrResults.AddressKey);
			}
			// - parsed thoroughfare
			else if ((key == AddressFields.TAG_OUTPUT_PREMISES) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES).metaValue)) {
				request.addOutputData(addrResults.Premises);
			} else if ((key == AddressFields.TAG_OUTPUT_PREMISES_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_TYPE).metaValue)) {
				request.addOutputData(addrResults.PremisesType);
			} else if ((key == AddressFields.TAG_OUTPUT_PREMISES_NUMBER) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_NUMBER).metaValue)) {
				request.addOutputData(addrResults.PremisesNumber);
			} else if ((key == AddressFields.TAG_OUTPUT_THOROUGHFARE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE).metaValue)) {
				request.addOutputData(addrResults.Thoroughfare);
			} else if ((key == AddressFields.TAG_OUTPUT_THOROUGHFARE_NAME) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE_NAME).metaValue)) {
				request.addOutputData(addrResults.ThoroughfareName);
			} else if ((key == AddressFields.TAG_OUTPUT_PRE_DIRECTION) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PRE_DIRECTION).metaValue)) {
				request.addOutputData(addrResults.ThoroughfarePreDirection);
			} else if ((key == AddressFields.TAG_OUTPUT_LEADING_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LEADING_TYPE).metaValue)) {
				request.addOutputData(addrResults.ThoroughfareLeadingType);
			} else if ((key == AddressFields.TAG_OUTPUT_POST_DIRECTION) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_DIRECTION).metaValue)) {
				request.addOutputData(addrResults.ThoroughfarePostDirection);
			} else if ((key == AddressFields.TAG_OUTPUT_TRAILING_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TRAILING_TYPE).metaValue)) {
				request.addOutputData(addrResults.ThoroughfareTrailingType);
			} else if ((key == AddressFields.TAG_OUTPUT_TYPE_ATTACHED) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_TYPE_ATTACHED).metaValue)) {
				request.addOutputData(addrResults.ThoroughfareTypeAttached);
			}
			// - parsed dependant thoroughfare
			else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE).metaValue)) {
				request.addOutputData(addrResults.DependentThoroughfare);
			} else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME).metaValue)) {
				request.addOutputData(addrResults.DependentThoroughfareName);
			} else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE).metaValue)) {
				request.addOutputData(addrResults.DependentThoroughfareLeadingType);
			} else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).metaValue)) {
				request.addOutputData(addrResults.DependentThoroughfarePreDirection);
			} else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE).metaValue)) {
				request.addOutputData(addrResults.DependentThoroughfareTrailingType);
			} else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).metaValue)) {
				request.addOutputData(addrResults.DependentThoroughfarePostDirection);
			} else if ((key == AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TYPE_ATTACHED) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TYPE_ATTACHED).metaValue)) {
				request.addOutputData(addrResults.DependentThoroughfareTypeAttached);
			}
			// -- parsed sub premises
			else if ((key == AddressFields.TAG_OUTPUT_BUILDING) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_BUILDING).metaValue)) {
				request.addOutputData(addrResults.Building);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_BUILDING) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING).metaValue)) {
				request.addOutputData(addrResults.SubBuilding);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_BUILDING_NUMBER) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_NUMBER).metaValue)) {
				request.addOutputData(addrResults.SubBuildingNumber);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_BUILDING_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_BUILDING_TYPE).metaValue)) {
				request.addOutputData(addrResults.SubBuildingType);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_PREMISES) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES).metaValue)) {
				request.addOutputData(addrResults.SubPremises);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_PREMISES_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_TYPE).metaValue)) {
				request.addOutputData(addrResults.SubPremisesType);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_PREMISES_NUMBER) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_NUMBER).metaValue)) {
				request.addOutputData(addrResults.SubPremisesNumber);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL).metaValue)) {
				request.addOutputData(addrResults.SubPremisesLevel);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_TYPE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_TYPE).metaValue)) {
				request.addOutputData(addrResults.SubPremisesLevelType);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_NUMBER) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_LEVEL_NUMBER).metaValue)) {
				request.addOutputData(addrResults.SubPremisesLevelNumber);
			}
			// -- Extra Address output
			else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_FORMAL_NAME) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_FORMAL_NAME).metaValue)) {
				request.addOutputData(addrResults.CountryFormalName);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_CODE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_CODE).metaValue)) {
				request.addOutputData(addrResults.CountryCode);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE).metaValue)) {
				request.addOutputData(addrResults.CountrySubdivisionCode);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_TIMEZONE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_TIMEZONE).metaValue)) {
				request.addOutputData(addrResults.CountryTimeZone);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_UTC) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_UTC).metaValue)) {
				request.addOutputData(addrResults.CountryUTC);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA2) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA2).metaValue)) {
				request.addOutputData(addrResults.CountryISOAlpha2);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA3) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA3).metaValue)) {
				request.addOutputData(addrResults.CountryISOAlpha3);
			} else if ((key == AddressFields.TAG_OUTPUT_COUNTRY_ISO_NUMERIC) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_NUMERIC).metaValue)) {
				request.addOutputData(addrResults.CountryISONumeric);
			} else if ((key == AddressFields.TAG_OUTPUT_LATITUDE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LATITUDE).metaValue)) {
				request.addOutputData(addrResults.Latitude);
			} else if ((key == AddressFields.TAG_OUTPUT_LONGITUDE) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_LONGITUDE).metaValue)) {
				request.addOutputData(addrResults.Longitude);
			}
			// -- parsed Postal
			else if ((key == AddressFields.TAG_OUTPUT_PERSONAL_ID) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_PERSONAL_ID).metaValue)) {
				request.addOutputData(addrResults.PersonalID);
			} else if ((key == AddressFields.TAG_OUTPUT_POSTBOX) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POSTBOX).metaValue)) {
				request.addOutputData(addrResults.PostBox);
			} else if ((key == AddressFields.TAG_OUTPUT_POST_OFFICE_LOCATION) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_POST_OFFICE_LOCATION).metaValue)) {
				request.addOutputData(addrResults.PostOfficeLocation);
			}
			// -- Parsed regional
			else if ((key == AddressFields.TAG_OUTPUT_COUNTY_NAME) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTY_NAME).metaValue)) {
				request.addOutputData(addrResults.CountyName);
			} else if ((key == AddressFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue)) {
				request.addOutputData(addrResults.DoubleDependentLocality);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA).metaValue)) {
				request.addOutputData(addrResults.SubAdministrativeArea);
			} else if ((key == AddressFields.TAG_OUTPUT_SUB_NATIONAL_AREA) && !Const.isEmpty(addrFields.webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_NATIONAL_AREA).metaValue)) {
				request.addOutputData(addrResults.SubNationalArea);
			} else {
				// System.out.println(" ** ** ** ----  DATA LOSS " + key);
			}
		}
	}
}

