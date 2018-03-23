package com.melissadata.kettle.personator.mapping;


import java.util.SortedMap;

import org.eclipse.swt.custom.CCombo;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.data.PersonatorFields;

public class FieldMapping {
	
	private static final Class<?> PKG = MDPersonatorMeta.class;
	private MDPersonatorMeta pMeta;
	private FieldGuesser fieldGuesser;
	private SortedMap<String, SourceFieldInfo> sourceFields;
	
	//NAME 
	public final String fieldCompanyName = getString("MDPersonatorDialog.InputTab.Company.Label");
	public final String fieldFullName = getString("MDPersonatorDialog.InputTab.FullName.Label");
	public final String fieldFirstName = getString("MDPersonatorDialog.InputTab.FirstName.Label");
	public final String fieldLastName = getString("MDPersonatorDialog.InputTab.LastName.Label");
	
	public String inputCompanyName = "";
	public String inputFullName = "";
	public String inputFirstName = "";
	public String inputLastName = "";

	// ADDRESS
	public final String fieldAddressLine1 = getString("MDPersonatorDialog.InputTab.AddressLine1.Label");
	public final String fieldAddressLine2 = getString("MDPersonatorDialog.InputTab.AddressLine2.Label");
	public final String fieldCity = getString("MDPersonatorDialog.InputTab.City.Label");
	public final String fieldState = getString("MDPersonatorDialog.InputTab.State.Label");
	public final String fieldZip = getString("MDPersonatorDialog.InputTab.Zip.Label");
	public final String fieldCountry = getString("MDPersonatorDialog.InputTab.Country.Label");
	public final String fieldFreeForm = getString("MDPersonatorDialog.InputTab.FreeForm.Label");

	public String inputAddressLine1 = "";
	public String inputAddressLine2 = "";
	public String inputCity = "";
	public String inputState = "";
	public String inputZip = "";
	public String inputCountry = "";
	public String inputFreeForm = "";

	//PHONE 
	public final String fieldPhone = getString("MDPersonatorDialog.InputTab.Phone.Label");
	public String inputPhone = "";
	
	//EMAIL 
	public final String fieldEmail = getString("MDPersonatorDialog.InputTab.Email.Label");
	public String inputEmail = "";



	public FieldMapping() {
		
	}
	
	public void mapFields() {
		fieldGuesser = new FieldGuesser(sourceFields);
		mapPersonatorFields();
		
	}

	private void mapPersonatorFields() {

		//TODO do we need to check license here 
		if (pMeta.isLicensed()) {

		}
		inputCompanyName = fieldGuesser.guessCompanyNameInput();
		inputFullName = fieldGuesser.guessFullNameInput();
		inputFirstName = fieldGuesser.guessFirstNameInput();
		inputLastName = fieldGuesser.guessLastNameInput();

		inputAddressLine1 = fieldGuesser.guessAddressLine1Input();
		inputAddressLine2 = fieldGuesser.guessAddressLine2Input();
		inputCity = fieldGuesser.guessCityInput();
		inputState = fieldGuesser.guessStateInput();
		inputZip = fieldGuesser.guessZipInput();
		inputCountry = fieldGuesser.guessCountryInput();

		inputPhone = fieldGuesser.guessPhoneInput();

		inputEmail = fieldGuesser.guessEmailInput();

	}

	public void updatePersonatorInput(SortedMap<String, CCombo> lCombos){
		
		inputCompanyName = lCombos.get(fieldCompanyName).getText();
		inputFullName = lCombos.get(fieldFullName).getText();
		inputLastName = lCombos.get(fieldLastName).getText();
		inputFirstName = lCombos.get(fieldFirstName).getText();
		
		inputAddressLine1 = lCombos.get(fieldAddressLine1).getText();
		inputAddressLine2 = lCombos.get(fieldAddressLine2).getText();
		inputCity = lCombos.get(fieldCity).getText();
		inputState = lCombos.get(fieldState).getText();
		inputZip = lCombos.get(fieldZip).getText();
		inputCountry = lCombos.get(fieldCountry).getText();
		
		inputPhone = lCombos.get(fieldPhone).getText();
		
		inputEmail = lCombos.get(fieldEmail).getText();
	}
	
	public void setPersonatorMapping() {

		if (pMeta.isLicensed()) {

		}
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COMPANY).metaValue = inputCompanyName;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FULL_NAME).metaValue = inputFullName;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FIRST_NAME).metaValue = inputFirstName;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_LAST_NAME).metaValue = inputLastName;

		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue = inputAddressLine1;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue = inputAddressLine2;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_CITY).metaValue = inputCity;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_STATE).metaValue = inputState;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ZIP).metaValue = inputZip;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COUNTRY).metaValue = inputCountry;
		
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_PHONE).metaValue = inputPhone;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_EMAIL).metaValue = inputEmail;
		pMeta.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue = inputFreeForm;

	}

	public FieldGuesser getFieldGuesser() {
		return fieldGuesser;
	}

	public SortedMap<String, SourceFieldInfo> getSourceFields() {
		return sourceFields;
	}

	public void setSourceFields(SortedMap<String, SourceFieldInfo> sourceFields) {
		this.sourceFields = sourceFields;
	}

	public String getInputFullName() {
		return inputFullName;
	}

	public void setInputFullName(String inputFullName) {
		this.inputFullName = inputFullName;
	}

	public String getInputCompanyName() {
		return inputCompanyName;
	}

	public void setInputCompanyName(String inputCompanyName) {
		this.inputCompanyName = inputCompanyName;
	}

	public String getInputLastName() {
		return inputLastName;
	}

	public void setInputLastName(String inputLastName) {
		this.inputLastName = inputLastName;
	}

	public String getInputAddressLine1() {
		return inputAddressLine1;
	}

	public void setInputAddressLine1(String inputAddressLine1) {
		this.inputAddressLine1 = inputAddressLine1;
	}

	public String getInputAddressLine2() {
		return inputAddressLine2;
	}

	public void setInputAddressLine2(String inputAddressLine2) {
		this.inputAddressLine2 = inputAddressLine2;
	}

	public String getInputCity() {
		return inputCity;
	}

	public void setInputCity(String inputCity) {
		this.inputCity = inputCity;
	}

	public String getInputState() {
		return inputState;
	}

	public void setInputState(String inputState) {
		this.inputState = inputState;
	}

	public String getInputZip() {
		return inputZip;
	}

	public void setInputZip(String inputZip) {
		this.inputZip = inputZip;
	}

	public String getInputCountry() {
		return inputCountry;
	}

	public void setInputCountry(String inputCountry) {
		this.inputCountry = inputCountry;
	}

	public String getInputPhone() {
		return inputPhone;
	}

	public void setInputPhone(String inputPhone) {
		this.inputPhone = inputPhone;
	}

	public String getInputEmail() {
		return inputEmail;
	}

	public void setInputEmail(String inputEmail) {
		this.inputEmail = inputEmail;
	}

	public String getInputFirstName() {
		return inputFirstName;
	}

	public void setInputFirstName(String inputFirstName) {
		this.inputFirstName = inputFirstName;
	}

	public MDPersonatorMeta getData() {
		return pMeta;
	}

	public void setData(MDPersonatorMeta meta) {
		this.pMeta = meta;
	}
	
	private String getString(String key){
		return BaseMessages.getString(getPackage(), key);
	}

	private Class<?> getPackage() {
		return PKG;
	}
	
}
