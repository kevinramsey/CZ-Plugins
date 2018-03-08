/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.melissadata;

public class mdName {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected mdName(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(mdName obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        mdNameJavaWrapperJNI.delete_mdName(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public mdName() {
    this(mdNameJavaWrapperJNI.new_mdName(), true);
  }

  public void SetPathToNameFiles(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetPathToNameFiles(swigCPtr, this, arg0);
  }

  public mdName.ProgramStatus InitializeDataFiles() {
    return mdName.ProgramStatus.swigToEnum(mdNameJavaWrapperJNI.mdName_InitializeDataFiles(swigCPtr, this));
  }

  public String GetInitializeErrorString() {
    return mdNameJavaWrapperJNI.mdName_GetInitializeErrorString(swigCPtr, this);
  }

  public int SetLicenseString(String arg0) {
    return mdNameJavaWrapperJNI.mdName_SetLicenseString(swigCPtr, this, arg0);
  }

  public String GetBuildNumber() {
    return mdNameJavaWrapperJNI.mdName_GetBuildNumber(swigCPtr, this);
  }

  public String GetDatabaseDate() {
    return mdNameJavaWrapperJNI.mdName_GetDatabaseDate(swigCPtr, this);
  }

  public String GetDatabaseExpirationDate() {
    return mdNameJavaWrapperJNI.mdName_GetDatabaseExpirationDate(swigCPtr, this);
  }

  public String GetLicenseExpirationDate() {
    return mdNameJavaWrapperJNI.mdName_GetLicenseExpirationDate(swigCPtr, this);
  }

  public int SetPrimaryNameHint(mdName.NameHints arg0) {
    return mdNameJavaWrapperJNI.mdName_SetPrimaryNameHint(swigCPtr, this, arg0.swigValue());
  }

  public int SetSecondaryNameHint(mdName.NameHints arg0) {
    return mdNameJavaWrapperJNI.mdName_SetSecondaryNameHint(swigCPtr, this, arg0.swigValue());
  }

  public int SetFirstNameSpellingCorrection(int arg0) {
    return mdNameJavaWrapperJNI.mdName_SetFirstNameSpellingCorrection(swigCPtr, this, arg0);
  }

  public int SetMiddleNameLogic(mdName.MiddleNameLogic arg0) {
    return mdNameJavaWrapperJNI.mdName_SetMiddleNameLogic(swigCPtr, this, arg0.swigValue());
  }

  public int SetGenderPopulation(mdName.Population arg0) {
    return mdNameJavaWrapperJNI.mdName_SetGenderPopulation(swigCPtr, this, arg0.swigValue());
  }

  public int SetGenderAggression(mdName.Aggression arg0) {
    return mdNameJavaWrapperJNI.mdName_SetGenderAggression(swigCPtr, this, arg0.swigValue());
  }

  public int AddSalutation(mdName.Salutations arg0) {
    return mdNameJavaWrapperJNI.mdName_AddSalutation(swigCPtr, this, arg0.swigValue());
  }

  public void SetSalutationPrefix(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetSalutationPrefix(swigCPtr, this, arg0);
  }

  public void SetSalutationSuffix(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetSalutationSuffix(swigCPtr, this, arg0);
  }

  public void SetSalutationSlug(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetSalutationSlug(swigCPtr, this, arg0);
  }

  public void ClearProperties() {
    mdNameJavaWrapperJNI.mdName_ClearProperties(swigCPtr, this);
  }

  public void SetFullName(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetFullName(swigCPtr, this, arg0);
  }

  public void SetPrefix(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetPrefix(swigCPtr, this, arg0);
  }

  public void SetPrefix2(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetPrefix2(swigCPtr, this, arg0);
  }

  public void SetFirstName(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetFirstName(swigCPtr, this, arg0);
  }

  public void SetFirstName2(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetFirstName2(swigCPtr, this, arg0);
  }

  public void SetMiddleName(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetMiddleName(swigCPtr, this, arg0);
  }

  public void SetMiddleName2(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetMiddleName2(swigCPtr, this, arg0);
  }

  public void SetSuffix(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetSuffix(swigCPtr, this, arg0);
  }

  public void SetSuffix2(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetSuffix2(swigCPtr, this, arg0);
  }

  public void SetLastName(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetLastName(swigCPtr, this, arg0);
  }

  public void SetLastName2(String arg0) {
    mdNameJavaWrapperJNI.mdName_SetLastName2(swigCPtr, this, arg0);
  }

  public int Parse() {
    return mdNameJavaWrapperJNI.mdName_Parse(swigCPtr, this);
  }

  public int Genderize() {
    return mdNameJavaWrapperJNI.mdName_Genderize(swigCPtr, this);
  }

  public int Salutate() {
    return mdNameJavaWrapperJNI.mdName_Salutate(swigCPtr, this);
  }

  public String GetStatusCode() {
    return mdNameJavaWrapperJNI.mdName_GetStatusCode(swigCPtr, this);
  }

  public String GetErrorCode() {
    return mdNameJavaWrapperJNI.mdName_GetErrorCode(swigCPtr, this);
  }

  public String GetChangeCode() {
    return mdNameJavaWrapperJNI.mdName_GetChangeCode(swigCPtr, this);
  }

  public String GetDebugInfo(String arg0) {
    return mdNameJavaWrapperJNI.mdName_GetDebugInfo(swigCPtr, this, arg0);
  }

  public String GetResults() {
    return mdNameJavaWrapperJNI.mdName_GetResults(swigCPtr, this);
  }

  public String GetResultCodeDescription(String arg0, mdName.ResultCdDescOpt opt) {
    return mdNameJavaWrapperJNI.mdName_GetResultCodeDescription(swigCPtr, this, arg0, opt.swigValue());
  }

  public String GetPrefix() {
    return mdNameJavaWrapperJNI.mdName_GetPrefix(swigCPtr, this);
  }

  public String GetPrefix2() {
    return mdNameJavaWrapperJNI.mdName_GetPrefix2(swigCPtr, this);
  }

  public String GetFirstName() {
    return mdNameJavaWrapperJNI.mdName_GetFirstName(swigCPtr, this);
  }

  public String GetFirstName2() {
    return mdNameJavaWrapperJNI.mdName_GetFirstName2(swigCPtr, this);
  }

  public String GetMiddleName() {
    return mdNameJavaWrapperJNI.mdName_GetMiddleName(swigCPtr, this);
  }

  public String GetMiddleName2() {
    return mdNameJavaWrapperJNI.mdName_GetMiddleName2(swigCPtr, this);
  }

  public String GetLastName() {
    return mdNameJavaWrapperJNI.mdName_GetLastName(swigCPtr, this);
  }

  public String GetLastName2() {
    return mdNameJavaWrapperJNI.mdName_GetLastName2(swigCPtr, this);
  }

  public String GetSuffix() {
    return mdNameJavaWrapperJNI.mdName_GetSuffix(swigCPtr, this);
  }

  public String GetSuffix2() {
    return mdNameJavaWrapperJNI.mdName_GetSuffix2(swigCPtr, this);
  }

  public String GetGender() {
    return mdNameJavaWrapperJNI.mdName_GetGender(swigCPtr, this);
  }

  public String GetGender2() {
    return mdNameJavaWrapperJNI.mdName_GetGender2(swigCPtr, this);
  }

  public String GetSalutation() {
    return mdNameJavaWrapperJNI.mdName_GetSalutation(swigCPtr, this);
  }

  public String StandardizeCompany(String arg0) {
    return mdNameJavaWrapperJNI.mdName_StandardizeCompany(swigCPtr, this, arg0);
  }

  public void SetReserved(String arg0, String arg1) {
    mdNameJavaWrapperJNI.mdName_SetReserved(swigCPtr, this, arg0, arg1);
  }

  public String GetReserved(String arg0) {
    return mdNameJavaWrapperJNI.mdName_GetReserved(swigCPtr, this, arg0);
  }

  public final static class ProgramStatus {
    public final static mdName.ProgramStatus NoError = new mdName.ProgramStatus("NoError", mdNameJavaWrapperJNI.mdName_NoError_get());
    public final static mdName.ProgramStatus ConfigFile = new mdName.ProgramStatus("ConfigFile");
    public final static mdName.ProgramStatus LicenseExpired = new mdName.ProgramStatus("LicenseExpired");
    public final static mdName.ProgramStatus DatabaseExpired = new mdName.ProgramStatus("DatabaseExpired");
    public final static mdName.ProgramStatus Unknown = new mdName.ProgramStatus("Unknown");

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

    private static ProgramStatus[] swigValues = { NoError, ConfigFile, LicenseExpired, DatabaseExpired, Unknown };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class NameHints {
    public final static mdName.NameHints DefinitelyFull = new mdName.NameHints("DefinitelyFull", mdNameJavaWrapperJNI.mdName_DefinitelyFull_get());
    public final static mdName.NameHints VeryLikelyFull = new mdName.NameHints("VeryLikelyFull");
    public final static mdName.NameHints ProbablyFull = new mdName.NameHints("ProbablyFull");
    public final static mdName.NameHints Varying = new mdName.NameHints("Varying");
    public final static mdName.NameHints ProbablyInverse = new mdName.NameHints("ProbablyInverse");
    public final static mdName.NameHints VeryLikelyInverse = new mdName.NameHints("VeryLikelyInverse");
    public final static mdName.NameHints DefinitelyInverse = new mdName.NameHints("DefinitelyInverse");
    public final static mdName.NameHints MixedFirstName = new mdName.NameHints("MixedFirstName");
    public final static mdName.NameHints MixedLastName = new mdName.NameHints("MixedLastName");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static NameHints swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + NameHints.class + " with value " + swigValue);
    }

    private NameHints(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private NameHints(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private NameHints(String swigName, NameHints swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static NameHints[] swigValues = { DefinitelyFull, VeryLikelyFull, ProbablyFull, Varying, ProbablyInverse, VeryLikelyInverse, DefinitelyInverse, MixedFirstName, MixedLastName };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class Population {
    public final static mdName.Population Male = new mdName.Population("Male", mdNameJavaWrapperJNI.mdName_Male_get());
    public final static mdName.Population Mixed = new mdName.Population("Mixed");
    public final static mdName.Population Female = new mdName.Population("Female");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Population swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Population.class + " with value " + swigValue);
    }

    private Population(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Population(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Population(String swigName, Population swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Population[] swigValues = { Male, Mixed, Female };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class Aggression {
    public final static mdName.Aggression Aggressive = new mdName.Aggression("Aggressive", mdNameJavaWrapperJNI.mdName_Aggressive_get());
    public final static mdName.Aggression Neutral = new mdName.Aggression("Neutral");
    public final static mdName.Aggression Conservative = new mdName.Aggression("Conservative");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Aggression swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Aggression.class + " with value " + swigValue);
    }

    private Aggression(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Aggression(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Aggression(String swigName, Aggression swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Aggression[] swigValues = { Aggressive, Neutral, Conservative };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class Salutations {
    public final static mdName.Salutations Formal = new mdName.Salutations("Formal", mdNameJavaWrapperJNI.mdName_Formal_get());
    public final static mdName.Salutations Informal = new mdName.Salutations("Informal");
    public final static mdName.Salutations FirstLast = new mdName.Salutations("FirstLast");
    public final static mdName.Salutations Slug = new mdName.Salutations("Slug");
    public final static mdName.Salutations Blank = new mdName.Salutations("Blank");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Salutations swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Salutations.class + " with value " + swigValue);
    }

    private Salutations(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Salutations(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Salutations(String swigName, Salutations swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Salutations[] swigValues = { Formal, Informal, FirstLast, Slug, Blank };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class MiddleNameLogic {
    public final static mdName.MiddleNameLogic ParseLogic = new mdName.MiddleNameLogic("ParseLogic", mdNameJavaWrapperJNI.mdName_ParseLogic_get());
    public final static mdName.MiddleNameLogic HyphenatedLast = new mdName.MiddleNameLogic("HyphenatedLast");
    public final static mdName.MiddleNameLogic MiddleName = new mdName.MiddleNameLogic("MiddleName");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static MiddleNameLogic swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + MiddleNameLogic.class + " with value " + swigValue);
    }

    private MiddleNameLogic(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private MiddleNameLogic(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private MiddleNameLogic(String swigName, MiddleNameLogic swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static MiddleNameLogic[] swigValues = { ParseLogic, HyphenatedLast, MiddleName };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static class ResultCdDescOpt {
    public final static mdName.ResultCdDescOpt ResultCodeDescriptionLong = new mdName.ResultCdDescOpt("ResultCodeDescriptionLong", mdNameJavaWrapperJNI.mdName_ResultCodeDescriptionLong_get());
    public final static mdName.ResultCdDescOpt ResultCodeDescriptionShort = new mdName.ResultCdDescOpt("ResultCodeDescriptionShort", mdNameJavaWrapperJNI.mdName_ResultCodeDescriptionShort_get());

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

}
