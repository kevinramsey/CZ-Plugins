package com.melissadata.kettle.fieldmapping;

import java.util.SortedMap;

import com.melissadata.kettle.iplocator.IPLocatorMeta;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.sm.SmartMoverMeta;
import com.melissadata.kettle.cv.address.AddressVerifyMeta;
import com.melissadata.kettle.cv.email.EmailVerifyMeta;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta;
import com.melissadata.kettle.cv.name.NameParseMeta;
import com.melissadata.kettle.cv.phone.PhoneVerifyMeta;
import org.eclipse.swt.custom.CCombo;
// import org.pentaho.pms.util.Const;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta.AddrKeySource;

public class FieldMapping {

	private static final Class<?>                           PKG                  = FieldMapping.class;
	private              MDCheckStepData                    data                 = null;
	private              FieldGuesser                       fieldGuesser         = null;
	private              SortedMap<String, SourceFieldInfo> sourceFields         = null;
	// NAME PARSE
	public final         String                             fieldFullName        = getString("FieldMappingDialog.NameParse.FullName.Label");
	public final         String                             fieldCompanyName     = getString("FieldMappingDialog.NameParse.CompanyName.Label");
	public               String                             inputFullName        = "";
	public               String                             inputCompanyName     = "";
	// ADDRESS VERIFY and SmartMover
	public final         String                             fieldSMPrefix        = getString("FieldMappingDialog.SmartMoverInput.InputNamePrefix.Label");
	public final         String                             fieldSMFirst         = getString("FieldMappingDialog.SmartMoverInput.InputNameFirst.Label");
	public final         String                             fieldSMMiddle        = getString("FieldMappingDialog.SmartMoverInput.InputNameMiddle.Label");
	public final         String                             fieldSMLast          = getString("FieldMappingDialog.SmartMoverInput.InputNameLast.Label");
	public final         String                             fieldSMSuffix        = getString("FieldMappingDialog.SmartMoverInput.InputNameSuffix.Label");
	public final         String                             fieldPrivateMailbox  = getString("FieldMappingDialog.SmartMoverInput.InputAddrPMB.Label");
	public final         String                             fieldAddrLastName    = getString("FieldMappingDialog.AddressVerify.InputLastName.Label");
	public final         String                             fieldAddrCompanyName = getString("FieldMappingDialog.AddressVerify.InputCompany.Label");
	public final         String                             fieldAddressLine1    = getString("FieldMappingDialog.AddressVerify.InputAddressLine1.Label");
	public final         String                             fieldAddressLine2    = getString("FieldMappingDialog.AddressVerify.InputAddressLine2.Label");
	public final         String                             fieldCity            = getString("FieldMappingDialog.AddressVerify.InputCity.Label");
	public final         String                             fieldState           = getString("FieldMappingDialog.AddressVerify.InputState.Label");
	public final         String                             fieldZip             = getString("FieldMappingDialog.AddressVerify.InputZip.Label");
	public final         String                             fieldCountry         = getString("FieldMappingDialog.AddressVerify.InputCountry.Label");
	public final         String                             fieldSuite           = getString("FieldMappingDialog.AddressVerify.InputSuite.Label");
	public final         String                             fieldUrbanization    = getString("FieldMappingDialog.AddressVerify.InputUrbanization.Label");
	public final         String                             fieldPlus4           = getString("FieldMappingDialog.AddressVerify.InputPlus4.Label");
	public               String                             inputPrefix          = "";
	public               String                             inputFirstName       = "";
	public               String                             inputMiddleName      = "";
	public               String                             inputLastName        = "";
	public               String                             inputSuffix          = "";
	public               String                             inputCompany         = "";
	public               String                             inputAddressLine1    = "";
	public               String                             inputAddressLine2    = "";
	public               String                             inputCity            = "";
	public               String                             inputState           = "";
	public               String                             inputZip             = "";
	public               String                             inputCountry         = "";
	// additional Address input
	public               String                             inputSuite           = "";
	public               String                             inputUrbanization    = "";
	public               String                             inputPlus4           = "";
	public               String                             inputPrivateMailbox  = "";
	/*
	 * //not currently used
	 * //GEO CODER
	 * private String addrKey = "";
	 * private String addrCompLine1 = "";
	 * private String addrCompLine2 = "";
	 * private String addrCompCity = "";
	 * private String addrCompState = "";
	 * private String addrCompZip = "";
	 */
	// PHONE VERIFY
	public final         String                             fieldPhone           = getString("FieldMappingDialog.PhoneEmail.InputPhone.Label");
	public               String                             inputPhone           = "";
	// EMAIL VERIFY
	public final         String                             fieldEmail           = getString("FieldMappingDialog.PhoneEmail.InputEmail.Label");
	public               String                             inputEmail           = "";
	// IP Locator
	public final         String                             fieldIPAddress       = getString("FieldMappingDialog.IPLocator.IPInput.Label");
	public               String                             inputIPAddress       = "";

	public FieldMapping(MDCheckStepData data) {

		this.data = data;
	}

	public MDCheckStepData getData() {

		return data;
	}

	public FieldGuesser getFieldGuesser() {

		return fieldGuesser;
	}

	public String getInputAddressLine1() {

		return inputAddressLine1;
	}

	public String getInputAddressLine2() {

		return inputAddressLine2;
	}

	public String getInputCity() {

		return inputCity;
	}

	public String getInputCompany() {

		return inputCompany;
	}

	public String getInputCompanyName() {

		return inputCompanyName;
	}

	public String getInputCountry() {

		return inputCountry;
	}

	public String getInputEmail() {

		return inputEmail;
	}

	public String getInputFirstName() {

		return inputFirstName;
	}

	public String getInputFullName() {

		return inputFullName;
	}

	public String getInputIPAddress() {

		return inputIPAddress;
	}

	public String getInputLastName() {

		return inputLastName;
	}

	public String getInputMiddleName() {

		return inputMiddleName;
	}

	public String getInputPhone() {

		return inputPhone;
	}

	public String getInputPlus4() {

		return inputPlus4;
	}

	public String getInputPrefix() {

		return inputPrefix;
	}

	public String getInputPrivateMailbox() {

		return inputPrivateMailbox;
	}

	public String getInputState() {

		return inputState;
	}

	public String getInputSuffix() {

		return inputSuffix;
	}

	public String getInputSuite() {

		return inputSuite;
	}

	public String getInputUrbanization() {

		return inputUrbanization;
	}

	public String getInputZip() {

		return inputZip;
	}

	public SortedMap<String, SourceFieldInfo> getSourceFields() {

		return sourceFields;
	}

	public void mapFields() {

		fieldGuesser = new FieldGuesser(sourceFields);
		if ((data.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			mapCVFields();
		}
		if ((data.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			mapSMFields();
		}
		if ((data.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			mapIPFields();
		}
	}

	public void setData(MDCheckStepData data) {

		this.data = data;
	}

	public void setInputAddressLine1(String inputAddressLine1) {

		this.inputAddressLine1 = inputAddressLine1;
	}

	public void setInputAddressLine2(String inputAddressLine2) {

		this.inputAddressLine2 = inputAddressLine2;
	}

	public void setInputCity(String inputCity) {

		this.inputCity = inputCity;
	}

	public void setInputCompany(String inputCompany) {

		this.inputCompany = inputCompany;
	}

	public void setInputCompanyName(String inputCompanyName) {

		this.inputCompanyName = inputCompanyName;
	}

	public void setInputCountry(String inputCountry) {

		this.inputCountry = inputCountry;
	}

	public void setInputEmail(String inputEmail) {

		this.inputEmail = inputEmail;
	}

	public void setInputFirstName(String inputFirstName) {

		this.inputFirstName = inputFirstName;
	}

	public void setInputFullName(String inputFullName) {

		this.inputFullName = inputFullName;
	}

	public void setInputIPAddress(String inputIPAddress) {

		this.inputIPAddress = inputIPAddress;
	}

	public void setInputLastName(String inputLastName) {

		this.inputLastName = inputLastName;
	}

	public void setInputMiddleName(String inputMiddleName) {

		this.inputMiddleName = inputMiddleName;
	}

	public void setInputPhone(String inputPhone) {

		this.inputPhone = inputPhone;
	}

	public void setInputPlus4(String inputPlus4) {

		this.inputPlus4 = inputPlus4;
	}

	public void setInputPrefix(String inputPrefix) {

		this.inputPrefix = inputPrefix;
	}

	public void setInputPrivateMailbox(String inputPrivateMailbox) {

		this.inputPrivateMailbox = inputPrivateMailbox;
	}

	public void setInputState(String inputState) {

		this.inputState = inputState;
	}

	public void setInputSuffix(String inputSuffix) {

		this.inputSuffix = inputSuffix;
	}

	public void setInputSuite(String inputSuite) {

		this.inputSuite = inputSuite;
	}

	public void setInputUrbanization(String inputUrbanization) {

		this.inputUrbanization = inputUrbanization;
	}

	public void setInputZip(String inputZip) {

		this.inputZip = inputZip;
	}

	public void setMapping() {

		if ((data.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			setCVMapping();
		}
		if ((data.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			setSMMapping();
		}
		if ((data.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			setIPMapping();
		}
	}

	public void setSourceFields(SortedMap<String, SourceFieldInfo> sourceFields) {

		this.sourceFields = sourceFields;
	}

	public void updateInput(SortedMap<String, CCombo> lCombos) {

		if ((data.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			updateCVInput(lCombos);
		}
		if ((data.checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			updateSMInput(lCombos);
		}
		if ((data.checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			updateIPInput(lCombos);
		}
	}

	private Class<?> getPackage() {

		return PKG;
	}

	private String getString(String key) {

		return BaseMessages.getString(getPackage(), key);
	}

	private void mapCVFields() {

		if (data.getNameParse().isLicensed()) {
			inputFullName = fieldGuesser.guessNameParseFullNameInput();
			inputCompanyName = fieldGuesser.guessNameParseCompanyNameInput();
		}
		if (data.getAddressVerify().isLicensed()) {
			inputLastName = fieldGuesser.guessAddrLastNameInput(!Const.isEmpty(inputFullName));
			inputCompany = fieldGuesser.guessAddrCompanyNameInput(!Const.isEmpty(inputCompanyName));
			inputAddressLine1 = fieldGuesser.guessAddressLine1Input();
			inputAddressLine2 = fieldGuesser.guessAddressLine2Input();
			inputCity = fieldGuesser.guessCityInput();
			inputState = fieldGuesser.guessStateInput();
			inputZip = fieldGuesser.guessZipInput();
			inputCountry = fieldGuesser.guessCountryInput();
			inputSuite = fieldGuesser.guessSuiteInput();
			inputUrbanization = fieldGuesser.guessUrbanInput();
			inputPlus4 = fieldGuesser.guessPluss4Input();
		}
		if (data.getPhoneVerify().isLicensed()) {
			inputPhone = fieldGuesser.guessPhoneInput();
		}
		if (data.getEmailVerify().isLicensed()) {
			inputEmail = fieldGuesser.guessEmailInput();
		}
	}

	private void mapIPFields() {

		inputIPAddress = fieldGuesser.guessIPInput();
	}

	private void mapSMFields() {

		if (data.getSmartMover() != null) {
			inputFullName = fieldGuesser.guessNameParseFullNameInput();
			if (Const.isEmpty(inputFullName)) {
				inputPrefix = fieldGuesser.guessSMPrefixInput();
				inputFirstName = fieldGuesser.guessSMFirstNameInput();
				inputMiddleName = fieldGuesser.guessSMMiddleNameInput();
				inputLastName = fieldGuesser.guessAddrLastNameInput(false);
				inputSuffix = fieldGuesser.guessSMSuffixInput();
			}
			inputCompany = fieldGuesser.guessAddrCompanyNameInput(false);
			inputAddressLine1 = fieldGuesser.guessAddressLine1Input();
			inputAddressLine2 = fieldGuesser.guessAddressLine2Input();
			inputCity = fieldGuesser.guessCityInput();
			inputState = fieldGuesser.guessStateInput();
			inputZip = fieldGuesser.guessZipInput();
			inputCountry = fieldGuesser.guessCountryInput();
			inputSuite = fieldGuesser.guessSuiteInput();
			inputPrivateMailbox = fieldGuesser.guessSMPMBInput();
			inputUrbanization = fieldGuesser.guessUrbanInput();
			inputPlus4 = fieldGuesser.guessPluss4Input();
		}
	}

	private void setCVMapping() {

		AddressVerifyMeta addrMeta  = data.getAddressVerify();
		GeoCoderMeta      geoMeta   = data.getGeoCoder();
		NameParseMeta     nameMeta  = data.getNameParse();
		EmailVerifyMeta   emailMeta = data.getEmailVerify();
		PhoneVerifyMeta   phoneMeta = data.getPhoneVerify();
		// Name
		if (phoneMeta.isLicensed()) {
			nameMeta.setFullName(inputFullName);
			// if (!Const.isEmpty(inputCompanyName))
			nameMeta.setInputCompanyName(inputCompanyName);
		}
		// Address
		if (addrMeta.isLicensed()) {
			addrMeta.setInputLastName(inputLastName);
			addrMeta.setInputCompany(inputCompany);
			addrMeta.setInputAddressLine1(inputAddressLine1);
			addrMeta.setInputAddressLine2(inputAddressLine2);
			addrMeta.setInputCity(inputCity);
			addrMeta.setInputState(inputState);
			addrMeta.setInputZip(inputZip);
			addrMeta.setInputCountry(inputCountry);
			addrMeta.setInputSuite(inputSuite);
			addrMeta.setInputUrbanization(inputUrbanization);
			addrMeta.setInputPlus4(inputPlus4);
		}
		if (geoMeta.isLicensed()) {
			if ((!Const.isEmpty(inputZip) && (!Const.isEmpty(inputAddressLine1) || !Const.isEmpty(inputAddressLine1))) || (!Const.isEmpty(inputCity) && !Const.isEmpty(inputState) && (!Const.isEmpty(inputAddressLine1) || !Const
					.isEmpty(inputAddressLine1)))) {
				geoMeta.setAddrKeySource(AddrKeySource.AddrVerifyResults);
			} else {
				geoMeta.setAddrKeySource(AddrKeySource.None);
			}
		}
		// Phone
		if (phoneMeta.isLicensed()) {
			phoneMeta.setInputPhone(inputPhone);
		}
		// Email
		if (emailMeta.isLicensed()) {
			emailMeta.setInputEmail(inputEmail);
		}
	}

	private void setIPMapping() {

		IPLocatorMeta ipMeta = data.getIPLocator();
		if (ipMeta.isLicensed()) {
			ipMeta.setInIPAddress(inputIPAddress);
		}
	}

	private void setSMMapping() {

		SmartMoverMeta smMeta = data.getSmartMover();
		if (!Const.isEmpty(inputFullName)) {
			smMeta.setInputUseFullName(true);
			smMeta.setInputFullName(inputFullName);
		} else {
			smMeta.setInputUseFullName(false);
			smMeta.setInputNamePrefix(inputPrefix);
			smMeta.setInputNameFirst(inputFirstName);
			smMeta.setInputNameMiddle(inputMiddleName);
			smMeta.setInputNameLast(inputLastName);
			smMeta.setInputNameSuffix(inputSuffix);
		}
		smMeta.setInputAddrCompany(inputCompany);
		smMeta.setInputAddrLine(inputAddressLine1);
		smMeta.setInputAddrLine2(inputAddressLine2);
		smMeta.setInputAddrCity(inputCity);
		smMeta.setInputAddrState(inputState);
		smMeta.setInputAddrZip(inputZip);
		smMeta.setInputAddrCountry(inputCountry);
		smMeta.setInputAddrSuite(inputSuite);
		smMeta.setInputAddrPMB(inputPrivateMailbox);
		smMeta.setInputAddrUrbanization(inputUrbanization);
		smMeta.setInputAddrPlus4(inputPlus4);
	}

	private void updateCVInput(SortedMap<String, CCombo> lCombos) {

		inputFullName = lCombos.get(fieldFullName).getText();
		inputCompanyName = lCombos.get(fieldCompanyName).getText();
		inputLastName = lCombos.get(fieldAddrLastName).getText();
		inputCompany = lCombos.get(fieldAddrCompanyName).getText();
		inputAddressLine1 = lCombos.get(fieldAddressLine1).getText();
		inputAddressLine2 = lCombos.get(fieldAddressLine2).getText();
		inputCity = lCombos.get(fieldCity).getText();
		inputState = lCombos.get(fieldState).getText();
		inputZip = lCombos.get(fieldZip).getText();
		inputCountry = lCombos.get(fieldCountry).getText();
		inputSuite = lCombos.get(fieldSuite).getText();
		inputUrbanization = lCombos.get(fieldUrbanization).getText();
		inputPlus4 = lCombos.get(fieldPlus4).getText();
		inputPhone = lCombos.get(fieldPhone).getText();
		inputEmail = lCombos.get(fieldEmail).getText();
	}

	private void updateIPInput(SortedMap<String, CCombo> lCombos) {

		inputIPAddress = lCombos.get(fieldIPAddress).getText();
	}

	private void updateSMInput(SortedMap<String, CCombo> lCombos) {

		if (lCombos.get(fieldFullName) != null) {
			inputFullName = lCombos.get(fieldFullName).getText();
		} else {
			inputPrefix = lCombos.get(fieldSMPrefix).getText();
			inputFirstName = lCombos.get(fieldSMFirst).getText();
			inputMiddleName = lCombos.get(fieldSMMiddle).getText();
			inputLastName = lCombos.get(fieldAddrLastName).getText();
			inputSuffix = lCombos.get(fieldSMSuffix).getText();
		}
		inputCompany = lCombos.get(fieldAddrCompanyName).getText();
		inputAddressLine1 = lCombos.get(fieldAddressLine1).getText();
		inputAddressLine2 = lCombos.get(fieldAddressLine2).getText();
		inputCity = lCombos.get(fieldCity).getText();
		inputState = lCombos.get(fieldState).getText();
		inputZip = lCombos.get(fieldZip).getText();
		inputCountry = lCombos.get(fieldCountry).getText();
		inputSuite = lCombos.get(fieldSuite).getText();
		inputPrivateMailbox = lCombos.get(fieldPrivateMailbox).getText();
		inputUrbanization = lCombos.get(fieldUrbanization).getText();
		inputPlus4 = lCombos.get(fieldPlus4).getText();
	}
}
