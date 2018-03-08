/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.melissadata;

public class mdAddr {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected mdAddr(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(mdAddr obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        mdAddrJavaWrapperJNI.delete_mdAddr(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public mdAddr() {
    this(mdAddrJavaWrapperJNI.new_mdAddr(), true);
  }

  public mdAddr.ProgramStatus Initialize(String arg0, String arg1, String arg2) {
    return mdAddr.ProgramStatus.swigToEnum(mdAddrJavaWrapperJNI.mdAddr_Initialize(swigCPtr, this, arg0, arg1, arg2));
  }

  public mdAddr.ProgramStatus InitializeDataFiles() {
    return mdAddr.ProgramStatus.swigToEnum(mdAddrJavaWrapperJNI.mdAddr_InitializeDataFiles(swigCPtr, this));
  }

  public String GetInitializeErrorString() {
    return mdAddrJavaWrapperJNI.mdAddr_GetInitializeErrorString(swigCPtr, this);
  }

  public boolean SetLicenseString(String arg0) {
    return mdAddrJavaWrapperJNI.mdAddr_SetLicenseString(swigCPtr, this, arg0);
  }

  public String GetBuildNumber() {
    return mdAddrJavaWrapperJNI.mdAddr_GetBuildNumber(swigCPtr, this);
  }

  public String GetDatabaseDate() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDatabaseDate(swigCPtr, this);
  }

  public String GetExpirationDate() {
    return mdAddrJavaWrapperJNI.mdAddr_GetExpirationDate(swigCPtr, this);
  }

  public String GetLicenseExpirationDate() {
    return mdAddrJavaWrapperJNI.mdAddr_GetLicenseExpirationDate(swigCPtr, this);
  }

  public String GetCanadianDatabaseDate() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCanadianDatabaseDate(swigCPtr, this);
  }

  public String GetCanadianExpirationDate() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCanadianExpirationDate(swigCPtr, this);
  }

  public void SetPathToUSFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToUSFiles(swigCPtr, this, arg0);
  }

  public void SetPathToCanadaFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToCanadaFiles(swigCPtr, this, arg0);
  }

  public void SetPathToDPVDataFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToDPVDataFiles(swigCPtr, this, arg0);
  }

  public void SetPathToLACSLinkDataFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToLACSLinkDataFiles(swigCPtr, this, arg0);
  }

  public void SetPathToSuiteLinkDataFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToSuiteLinkDataFiles(swigCPtr, this, arg0);
  }

  public void SetPathToSuiteFinderDataFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToSuiteFinderDataFiles(swigCPtr, this, arg0);
  }

  public void SetPathToRBDIFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToRBDIFiles(swigCPtr, this, arg0);
  }

  public String GetRBDIDatabaseDate() {
    return mdAddrJavaWrapperJNI.mdAddr_GetRBDIDatabaseDate(swigCPtr, this);
  }

  public void SetPathToAddrKeyDataFiles(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPathToAddrKeyDataFiles(swigCPtr, this, arg0);
  }

  public void ClearProperties() {
    mdAddrJavaWrapperJNI.mdAddr_ClearProperties(swigCPtr, this);
  }

  public void ResetDPV() {
    mdAddrJavaWrapperJNI.mdAddr_ResetDPV(swigCPtr, this);
  }

  public void SetCASSEnable(int arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetCASSEnable(swigCPtr, this, arg0);
  }

  public void SetUseUSPSPreferredCityNames(int arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetUseUSPSPreferredCityNames(swigCPtr, this, arg0);
  }

  public void SetDiacritics(mdAddr.DiacriticsMode arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetDiacritics(swigCPtr, this, arg0.swigValue());
  }

  public String GetStatusCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetStatusCode(swigCPtr, this);
  }

  public String GetErrorCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetErrorCode(swigCPtr, this);
  }

  public String GetErrorString() {
    return mdAddrJavaWrapperJNI.mdAddr_GetErrorString(swigCPtr, this);
  }

  public String GetResults() {
    return mdAddrJavaWrapperJNI.mdAddr_GetResults(swigCPtr, this);
  }

  public String GetResultCodeDescription(String resultCode, mdAddr.ResultCdDescOpt opt) {
    return mdAddrJavaWrapperJNI.mdAddr_GetResultCodeDescription__SWIG_0(swigCPtr, this, resultCode, opt.swigValue());
  }

  public String GetResultCodeDescription(String resultCode) {
    return mdAddrJavaWrapperJNI.mdAddr_GetResultCodeDescription__SWIG_1(swigCPtr, this, resultCode);
  }

  public void SetPS3553_B1_ProcessorName(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_B1_ProcessorName(swigCPtr, this, arg0);
  }

  public void SetPS3553_B4_ListName(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_B4_ListName(swigCPtr, this, arg0);
  }

  public void SetPS3553_D3_Name(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_D3_Name(swigCPtr, this, arg0);
  }

  public void SetPS3553_D3_Company(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_D3_Company(swigCPtr, this, arg0);
  }

  public void SetPS3553_D3_Address(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_D3_Address(swigCPtr, this, arg0);
  }

  public void SetPS3553_D3_City(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_D3_City(swigCPtr, this, arg0);
  }

  public void SetPS3553_D3_State(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_D3_State(swigCPtr, this, arg0);
  }

  public void SetPS3553_D3_ZIP(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPS3553_D3_ZIP(swigCPtr, this, arg0);
  }

  public String GetFormPS3553() {
    return mdAddrJavaWrapperJNI.mdAddr_GetFormPS3553(swigCPtr, this);
  }

  public boolean SaveFormPS3553(String arg0) {
    return mdAddrJavaWrapperJNI.mdAddr_SaveFormPS3553(swigCPtr, this, arg0);
  }

  public void ResetFormPS3553() {
    mdAddrJavaWrapperJNI.mdAddr_ResetFormPS3553(swigCPtr, this);
  }

  public void ResetFormPS3553Counter() {
    mdAddrJavaWrapperJNI.mdAddr_ResetFormPS3553Counter(swigCPtr, this);
  }

  public void SetStandardizationType(mdAddr.StandardizeMode mode) {
    mdAddrJavaWrapperJNI.mdAddr_SetStandardizationType(swigCPtr, this, mode.swigValue());
  }

  public void SetSuiteParseMode(mdAddr.SuiteParseMode mode) {
    mdAddrJavaWrapperJNI.mdAddr_SetSuiteParseMode(swigCPtr, this, mode.swigValue());
  }

  public void SetAliasMode(mdAddr.AliasPreserveMode mode) {
    mdAddrJavaWrapperJNI.mdAddr_SetAliasMode(swigCPtr, this, mode.swigValue());
  }

  public String GetFormSOA() {
    return mdAddrJavaWrapperJNI.mdAddr_GetFormSOA(swigCPtr, this);
  }

  public void SaveFormSOA(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SaveFormSOA(swigCPtr, this, arg0);
  }

  public void ResetFormSOA() {
    mdAddrJavaWrapperJNI.mdAddr_ResetFormSOA(swigCPtr, this);
  }

  public void SetSOACustomerInfo(String customerName, String customerAddress) {
    mdAddrJavaWrapperJNI.mdAddr_SetSOACustomerInfo(swigCPtr, this, customerName, customerAddress);
  }

  public void SetSOACPCNumber(String CPCNumber) {
    mdAddrJavaWrapperJNI.mdAddr_SetSOACPCNumber(swigCPtr, this, CPCNumber);
  }

  public String GetSOACustomerInfo() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSOACustomerInfo(swigCPtr, this);
  }

  public String GetSOACPCNumber() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSOACPCNumber(swigCPtr, this);
  }

  public int GetSOATotalRecords() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSOATotalRecords(swigCPtr, this);
  }

  public float GetSOAAAPercentage() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSOAAAPercentage(swigCPtr, this);
  }

  public String GetSOAAAExpiryDate() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSOAAAExpiryDate(swigCPtr, this);
  }

  public String GetSOASoftwareInfo() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSOASoftwareInfo(swigCPtr, this);
  }

  public String GetSOAErrorString() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSOAErrorString(swigCPtr, this);
  }

  public void SetCompany(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetCompany(swigCPtr, this, arg0);
  }

  public void SetLastName(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetLastName(swigCPtr, this, arg0);
  }

  public void SetAddress(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetAddress(swigCPtr, this, arg0);
  }

  public void SetAddress2(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetAddress2(swigCPtr, this, arg0);
  }

  public void SetLastLine(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetLastLine(swigCPtr, this, arg0);
  }

  public void SetSuite(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetSuite(swigCPtr, this, arg0);
  }

  public void SetCity(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetCity(swigCPtr, this, arg0);
  }

  public void SetState(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetState(swigCPtr, this, arg0);
  }

  public void SetZip(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetZip(swigCPtr, this, arg0);
  }

  public void SetPlus4(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetPlus4(swigCPtr, this, arg0);
  }

  public void SetUrbanization(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetUrbanization(swigCPtr, this, arg0);
  }

  public void SetParsedAddressRange(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedAddressRange(swigCPtr, this, arg0);
  }

  public void SetParsedPreDirection(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedPreDirection(swigCPtr, this, arg0);
  }

  public void SetParsedStreetName(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedStreetName(swigCPtr, this, arg0);
  }

  public void SetParsedSuffix(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedSuffix(swigCPtr, this, arg0);
  }

  public void SetParsedPostDirection(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedPostDirection(swigCPtr, this, arg0);
  }

  public void SetParsedSuiteName(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedSuiteName(swigCPtr, this, arg0);
  }

  public void SetParsedSuiteRange(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedSuiteRange(swigCPtr, this, arg0);
  }

  public void SetParsedRouteService(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedRouteService(swigCPtr, this, arg0);
  }

  public void SetParsedLockBox(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedLockBox(swigCPtr, this, arg0);
  }

  public void SetParsedDeliveryInstallation(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetParsedDeliveryInstallation(swigCPtr, this, arg0);
  }

  public void SetCountryCode(String arg0) {
    mdAddrJavaWrapperJNI.mdAddr_SetCountryCode(swigCPtr, this, arg0);
  }

  public boolean VerifyAddress() {
    return mdAddrJavaWrapperJNI.mdAddr_VerifyAddress(swigCPtr, this);
  }

  public String GetCompany() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCompany(swigCPtr, this);
  }

  public String GetLastName() {
    return mdAddrJavaWrapperJNI.mdAddr_GetLastName(swigCPtr, this);
  }

  public String GetAddress() {
    return mdAddrJavaWrapperJNI.mdAddr_GetAddress(swigCPtr, this);
  }

  public String GetAddress2() {
    return mdAddrJavaWrapperJNI.mdAddr_GetAddress2(swigCPtr, this);
  }

  public String GetSuite() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSuite(swigCPtr, this);
  }

  public String GetCity() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCity(swigCPtr, this);
  }

  public String GetCityAbbreviation() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCityAbbreviation(swigCPtr, this);
  }

  public String GetState() {
    return mdAddrJavaWrapperJNI.mdAddr_GetState(swigCPtr, this);
  }

  public String GetZip() {
    return mdAddrJavaWrapperJNI.mdAddr_GetZip(swigCPtr, this);
  }

  public String GetPlus4() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPlus4(swigCPtr, this);
  }

  public String GetCarrierRoute() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCarrierRoute(swigCPtr, this);
  }

  public String GetDeliveryPointCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDeliveryPointCode(swigCPtr, this);
  }

  public String GetDeliveryPointCheckDigit() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDeliveryPointCheckDigit(swigCPtr, this);
  }

  public String GetCountyFips() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCountyFips(swigCPtr, this);
  }

  public String GetCountyName() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCountyName(swigCPtr, this);
  }

  public String GetAddressTypeCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetAddressTypeCode(swigCPtr, this);
  }

  public String GetAddressTypeString() {
    return mdAddrJavaWrapperJNI.mdAddr_GetAddressTypeString(swigCPtr, this);
  }

  public String GetUrbanization() {
    return mdAddrJavaWrapperJNI.mdAddr_GetUrbanization(swigCPtr, this);
  }

  public String GetCongressionalDistrict() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCongressionalDistrict(swigCPtr, this);
  }

  public String GetLACS() {
    return mdAddrJavaWrapperJNI.mdAddr_GetLACS(swigCPtr, this);
  }

  public String GetLACSLinkIndicator() {
    return mdAddrJavaWrapperJNI.mdAddr_GetLACSLinkIndicator(swigCPtr, this);
  }

  public String GetPrivateMailbox() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPrivateMailbox(swigCPtr, this);
  }

  public String GetTimeZoneCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetTimeZoneCode(swigCPtr, this);
  }

  public String GetTimeZone() {
    return mdAddrJavaWrapperJNI.mdAddr_GetTimeZone(swigCPtr, this);
  }

  public String GetMsa() {
    return mdAddrJavaWrapperJNI.mdAddr_GetMsa(swigCPtr, this);
  }

  public String GetPmsa() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPmsa(swigCPtr, this);
  }

  public String GetDefaultFlagIndicator() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDefaultFlagIndicator(swigCPtr, this);
  }

  public String GetSuiteStatus() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSuiteStatus(swigCPtr, this);
  }

  public String GetEWSFlag() {
    return mdAddrJavaWrapperJNI.mdAddr_GetEWSFlag(swigCPtr, this);
  }

  public String GetCMRA() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCMRA(swigCPtr, this);
  }

  public String GetDsfNoStats() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDsfNoStats(swigCPtr, this);
  }

  public String GetDsfVacant() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDsfVacant(swigCPtr, this);
  }

  public String GetDsfDNA() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDsfDNA(swigCPtr, this);
  }

  public String GetCountryCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetCountryCode(swigCPtr, this);
  }

  public String GetZipType() {
    return mdAddrJavaWrapperJNI.mdAddr_GetZipType(swigCPtr, this);
  }

  public String GetFalseTable() {
    return mdAddrJavaWrapperJNI.mdAddr_GetFalseTable(swigCPtr, this);
  }

  public String GetDPVFootnotes() {
    return mdAddrJavaWrapperJNI.mdAddr_GetDPVFootnotes(swigCPtr, this);
  }

  public String GetLACSLinkReturnCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetLACSLinkReturnCode(swigCPtr, this);
  }

  public String GetSuiteLinkReturnCode() {
    return mdAddrJavaWrapperJNI.mdAddr_GetSuiteLinkReturnCode(swigCPtr, this);
  }

  public String GetRBDI() {
    return mdAddrJavaWrapperJNI.mdAddr_GetRBDI(swigCPtr, this);
  }

  public String GetELotNumber() {
    return mdAddrJavaWrapperJNI.mdAddr_GetELotNumber(swigCPtr, this);
  }

  public String GetELotOrder() {
    return mdAddrJavaWrapperJNI.mdAddr_GetELotOrder(swigCPtr, this);
  }

  public String GetAddressKey() {
    return mdAddrJavaWrapperJNI.mdAddr_GetAddressKey(swigCPtr, this);
  }

  public String GetMelissaAddressKey() {
    return mdAddrJavaWrapperJNI.mdAddr_GetMelissaAddressKey(swigCPtr, this);
  }

  public String GetMelissaAddressKeyBase() {
    return mdAddrJavaWrapperJNI.mdAddr_GetMelissaAddressKeyBase(swigCPtr, this);
  }

  public boolean FindSuggestion() {
    return mdAddrJavaWrapperJNI.mdAddr_FindSuggestion(swigCPtr, this);
  }

  public boolean FindSuggestionNext() {
    return mdAddrJavaWrapperJNI.mdAddr_FindSuggestionNext(swigCPtr, this);
  }

  public int GetPS3553_B6_TotalRecords() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_B6_TotalRecords(swigCPtr, this);
  }

  public int GetPS3553_C1a_ZIP4Coded() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_C1a_ZIP4Coded(swigCPtr, this);
  }

  public int GetPS3553_C1c_DPBCAssigned() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_C1c_DPBCAssigned(swigCPtr, this);
  }

  public int GetPS3553_C1d_FiveDigitCoded() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_C1d_FiveDigitCoded(swigCPtr, this);
  }

  public int GetPS3553_C1e_CRRTCoded() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_C1e_CRRTCoded(swigCPtr, this);
  }

  public int GetPS3553_C1f_eLOTAssigned() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_C1f_eLOTAssigned(swigCPtr, this);
  }

  public int GetPS3553_E_HighRiseDefault() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_E_HighRiseDefault(swigCPtr, this);
  }

  public int GetPS3553_E_HighRiseExact() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_E_HighRiseExact(swigCPtr, this);
  }

  public int GetPS3553_E_RuralRouteDefault() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_E_RuralRouteDefault(swigCPtr, this);
  }

  public int GetPS3553_E_RuralRouteExact() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_E_RuralRouteExact(swigCPtr, this);
  }

  public int GetZip4HRDefault() {
    return mdAddrJavaWrapperJNI.mdAddr_GetZip4HRDefault(swigCPtr, this);
  }

  public int GetZip4HRExact() {
    return mdAddrJavaWrapperJNI.mdAddr_GetZip4HRExact(swigCPtr, this);
  }

  public int GetZip4RRDefault() {
    return mdAddrJavaWrapperJNI.mdAddr_GetZip4RRDefault(swigCPtr, this);
  }

  public int GetZip4RRExact() {
    return mdAddrJavaWrapperJNI.mdAddr_GetZip4RRExact(swigCPtr, this);
  }

  public int GetPS3553_E_LACSCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_E_LACSCount(swigCPtr, this);
  }

  public int GetPS3553_E_EWSCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_E_EWSCount(swigCPtr, this);
  }

  public int GetPS3553_E_DPVCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_E_DPVCount(swigCPtr, this);
  }

  public int GetPS3553_X_POBoxCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_POBoxCount(swigCPtr, this);
  }

  public int GetPS3553_X_HCExactCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_HCExactCount(swigCPtr, this);
  }

  public int GetPS3553_X_FirmCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_FirmCount(swigCPtr, this);
  }

  public int GetPS3553_X_GenDeliveryCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_GenDeliveryCount(swigCPtr, this);
  }

  public int GetPS3553_X_MilitaryZipCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_MilitaryZipCount(swigCPtr, this);
  }

  public int GetPS3553_X_NonDeliveryCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_NonDeliveryCount(swigCPtr, this);
  }

  public int GetPS3553_X_StreetCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_StreetCount(swigCPtr, this);
  }

  public int GetPS3553_X_HCDefaultCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_HCDefaultCount(swigCPtr, this);
  }

  public int GetPS3553_X_OtherCount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_OtherCount(swigCPtr, this);
  }

  public int GetPS3553_X_LacsLinkCodeACount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_LacsLinkCodeACount(swigCPtr, this);
  }

  public int GetPS3553_X_LacsLinkCode00Count() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_LacsLinkCode00Count(swigCPtr, this);
  }

  public int GetPS3553_X_LacsLinkCode14Count() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_LacsLinkCode14Count(swigCPtr, this);
  }

  public int GetPS3553_X_LacsLinkCode92Count() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_LacsLinkCode92Count(swigCPtr, this);
  }

  public int GetPS3553_X_LacsLinkCode09Count() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_LacsLinkCode09Count(swigCPtr, this);
  }

  public int GetPS3553_X_SuiteLinkCodeACount() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_SuiteLinkCodeACount(swigCPtr, this);
  }

  public int GetPS3553_X_SuiteLinkCode00Count() {
    return mdAddrJavaWrapperJNI.mdAddr_GetPS3553_X_SuiteLinkCode00Count(swigCPtr, this);
  }

  public String GetParsedAddressRange() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedAddressRange(swigCPtr, this);
  }

  public String GetParsedPreDirection() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedPreDirection(swigCPtr, this);
  }

  public String GetParsedStreetName() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedStreetName(swigCPtr, this);
  }

  public String GetParsedSuffix() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedSuffix(swigCPtr, this);
  }

  public String GetParsedPostDirection() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedPostDirection(swigCPtr, this);
  }

  public String GetParsedSuiteName() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedSuiteName(swigCPtr, this);
  }

  public String GetParsedSuiteRange() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedSuiteRange(swigCPtr, this);
  }

  public String GetParsedPrivateMailboxName() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedPrivateMailboxName(swigCPtr, this);
  }

  public String GetParsedPrivateMailboxNumber() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedPrivateMailboxNumber(swigCPtr, this);
  }

  public String GetParsedGarbage() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedGarbage(swigCPtr, this);
  }

  public String GetParsedRouteService() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedRouteService(swigCPtr, this);
  }

  public String GetParsedLockBox() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedLockBox(swigCPtr, this);
  }

  public String GetParsedDeliveryInstallation() {
    return mdAddrJavaWrapperJNI.mdAddr_GetParsedDeliveryInstallation(swigCPtr, this);
  }

  public void SetReserved(String arg0, String arg1) {
    mdAddrJavaWrapperJNI.mdAddr_SetReserved(swigCPtr, this, arg0, arg1);
  }

  public String GetReserved(String arg0) {
    return mdAddrJavaWrapperJNI.mdAddr_GetReserved(swigCPtr, this, arg0);
  }

  public final static class ProgramStatus {
    public final static mdAddr.ProgramStatus ErrorNone = new mdAddr.ProgramStatus("ErrorNone", mdAddrJavaWrapperJNI.mdAddr_ErrorNone_get());
    public final static mdAddr.ProgramStatus ErrorOther = new mdAddr.ProgramStatus("ErrorOther");
    public final static mdAddr.ProgramStatus ErrorOutOfMemory = new mdAddr.ProgramStatus("ErrorOutOfMemory");
    public final static mdAddr.ProgramStatus ErrorRequiredFileNotFound = new mdAddr.ProgramStatus("ErrorRequiredFileNotFound");
    public final static mdAddr.ProgramStatus ErrorFoundOldFile = new mdAddr.ProgramStatus("ErrorFoundOldFile");
    public final static mdAddr.ProgramStatus ErrorDatabaseExpired = new mdAddr.ProgramStatus("ErrorDatabaseExpired");
    public final static mdAddr.ProgramStatus ErrorLicenseExpired = new mdAddr.ProgramStatus("ErrorLicenseExpired");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static ProgramStatus swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + ProgramStatus.class + " with value " + swigValue);
    }

    private ProgramStatus(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private ProgramStatus(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private ProgramStatus(String swigName, ProgramStatus swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static ProgramStatus[] swigValues = { ErrorNone, ErrorOther, ErrorOutOfMemory, ErrorRequiredFileNotFound, ErrorFoundOldFile, ErrorDatabaseExpired, ErrorLicenseExpired };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class AccessType {
    public final static mdAddr.AccessType Local = new mdAddr.AccessType("Local", mdAddrJavaWrapperJNI.mdAddr_Local_get());
    public final static mdAddr.AccessType Remote = new mdAddr.AccessType("Remote");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static AccessType swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + AccessType.class + " with value " + swigValue);
    }

    private AccessType(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private AccessType(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private AccessType(String swigName, AccessType swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static AccessType[] swigValues = { Local, Remote };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class DiacriticsMode {
    public final static mdAddr.DiacriticsMode Auto = new mdAddr.DiacriticsMode("Auto", mdAddrJavaWrapperJNI.mdAddr_Auto_get());
    public final static mdAddr.DiacriticsMode On = new mdAddr.DiacriticsMode("On");
    public final static mdAddr.DiacriticsMode Off = new mdAddr.DiacriticsMode("Off");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static DiacriticsMode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + DiacriticsMode.class + " with value " + swigValue);
    }

    private DiacriticsMode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private DiacriticsMode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private DiacriticsMode(String swigName, DiacriticsMode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static DiacriticsMode[] swigValues = { Auto, On, Off };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class StandardizeMode {
    public final static mdAddr.StandardizeMode ShortFormat = new mdAddr.StandardizeMode("ShortFormat", mdAddrJavaWrapperJNI.mdAddr_ShortFormat_get());
    public final static mdAddr.StandardizeMode LongFormat = new mdAddr.StandardizeMode("LongFormat");
    public final static mdAddr.StandardizeMode AutoFormat = new mdAddr.StandardizeMode("AutoFormat");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static StandardizeMode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + StandardizeMode.class + " with value " + swigValue);
    }

    private StandardizeMode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private StandardizeMode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private StandardizeMode(String swigName, StandardizeMode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static StandardizeMode[] swigValues = { ShortFormat, LongFormat, AutoFormat };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class SuiteParseMode {
    public final static mdAddr.SuiteParseMode ParseSuite = new mdAddr.SuiteParseMode("ParseSuite", mdAddrJavaWrapperJNI.mdAddr_ParseSuite_get());
    public final static mdAddr.SuiteParseMode CombineSuite = new mdAddr.SuiteParseMode("CombineSuite");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static SuiteParseMode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + SuiteParseMode.class + " with value " + swigValue);
    }

    private SuiteParseMode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private SuiteParseMode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private SuiteParseMode(String swigName, SuiteParseMode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static SuiteParseMode[] swigValues = { ParseSuite, CombineSuite };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class AliasPreserveMode {
    public final static mdAddr.AliasPreserveMode ConvertAlias = new mdAddr.AliasPreserveMode("ConvertAlias", mdAddrJavaWrapperJNI.mdAddr_ConvertAlias_get());
    public final static mdAddr.AliasPreserveMode PreserveAlias = new mdAddr.AliasPreserveMode("PreserveAlias");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static AliasPreserveMode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + AliasPreserveMode.class + " with value " + swigValue);
    }

    private AliasPreserveMode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private AliasPreserveMode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private AliasPreserveMode(String swigName, AliasPreserveMode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static AliasPreserveMode[] swigValues = { ConvertAlias, PreserveAlias };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class AutoCompletionMode {
    public final static mdAddr.AutoCompletionMode AutoCompleteSingleSuite = new mdAddr.AutoCompletionMode("AutoCompleteSingleSuite", mdAddrJavaWrapperJNI.mdAddr_AutoCompleteSingleSuite_get());
    public final static mdAddr.AutoCompletionMode AutoCompleteRangedSuite = new mdAddr.AutoCompletionMode("AutoCompleteRangedSuite");
    public final static mdAddr.AutoCompletionMode AutoCompletePlaceHolderSuite = new mdAddr.AutoCompletionMode("AutoCompletePlaceHolderSuite");
    public final static mdAddr.AutoCompletionMode AutoCompleteNoSuite = new mdAddr.AutoCompletionMode("AutoCompleteNoSuite");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static AutoCompletionMode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + AutoCompletionMode.class + " with value " + swigValue);
    }

    private AutoCompletionMode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private AutoCompletionMode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private AutoCompletionMode(String swigName, AutoCompletionMode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static AutoCompletionMode[] swigValues = { AutoCompleteSingleSuite, AutoCompleteRangedSuite, AutoCompletePlaceHolderSuite, AutoCompleteNoSuite };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class ResultCdDescOpt {
    public final static mdAddr.ResultCdDescOpt ResultCodeDescriptionLong = new mdAddr.ResultCdDescOpt("ResultCodeDescriptionLong", mdAddrJavaWrapperJNI.mdAddr_ResultCodeDescriptionLong_get());
    public final static mdAddr.ResultCdDescOpt ResultCodeDescriptionShort = new mdAddr.ResultCdDescOpt("ResultCodeDescriptionShort");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static ResultCdDescOpt swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + ResultCdDescOpt.class + " with value " + swigValue);
    }

    private ResultCdDescOpt(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private ResultCdDescOpt(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private ResultCdDescOpt(String swigName, ResultCdDescOpt swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static ResultCdDescOpt[] swigValues = { ResultCodeDescriptionLong, ResultCodeDescriptionShort };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class MailboxLookupMode {
    public final static mdAddr.MailboxLookupMode MailboxNone = new mdAddr.MailboxLookupMode("MailboxNone", mdAddrJavaWrapperJNI.mdAddr_MailboxNone_get());
    public final static mdAddr.MailboxLookupMode MailboxExpress = new mdAddr.MailboxLookupMode("MailboxExpress");
    public final static mdAddr.MailboxLookupMode MailboxPremium = new mdAddr.MailboxLookupMode("MailboxPremium");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static MailboxLookupMode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + MailboxLookupMode.class + " with value " + swigValue);
    }

    private MailboxLookupMode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private MailboxLookupMode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private MailboxLookupMode(String swigName, MailboxLookupMode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static MailboxLookupMode[] swigValues = { MailboxNone, MailboxExpress, MailboxPremium };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}
