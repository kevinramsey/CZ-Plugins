package com.melissadata;

public class mdCleanser {
	private long I;
	protected boolean ownMemory;

	protected static long getI(mdCleanser obj) {
		return (obj==null ? 0 : obj.I);
	}

	protected void finalize() {
		delete();
	}

public final static class ProgramStatus {
	public final static mdCleanser.ProgramStatus ErrorNone=new mdCleanser.ProgramStatus("ErrorNone",0);
	public final static mdCleanser.ProgramStatus ErrorConfigFile=new mdCleanser.ProgramStatus("ErrorConfigFile",1);
	public final static mdCleanser.ProgramStatus ErrorBothAddFuncAndTableSelected=new mdCleanser.ProgramStatus("ErrorBothAddFuncAndTableSelected",2);
	public final static mdCleanser.ProgramStatus ErrorRequireAddFuncOrTableFile=new mdCleanser.ProgramStatus("ErrorRequireAddFuncOrTableFile",3);
	public final static mdCleanser.ProgramStatus ErrorRequireTransformExpression=new mdCleanser.ProgramStatus("ErrorRequireTransformExpression",4);
	public final static mdCleanser.ProgramStatus ErrorLicenseInvalid=new mdCleanser.ProgramStatus("ErrorLicenseInvalid",5);
	public final static mdCleanser.ProgramStatus ErrorLicenseExpired=new mdCleanser.ProgramStatus("ErrorLicenseExpired",6);
	public final static mdCleanser.ProgramStatus ErrorDatabaseExpired=new mdCleanser.ProgramStatus("ErrorDatabaseExpired",7);
	public final static mdCleanser.ProgramStatus ErrorCustomMgs=new mdCleanser.ProgramStatus("ErrorCustomMgs",8);
	public final static mdCleanser.ProgramStatus ErrorUnknown=new mdCleanser.ProgramStatus("ErrorUnknown",9);

	private final String enumName;
	private final int enumValue;
	private static ProgramStatus[] enumValues={ErrorNone,ErrorConfigFile,ErrorBothAddFuncAndTableSelected,ErrorRequireAddFuncOrTableFile,ErrorRequireTransformExpression,ErrorLicenseInvalid,ErrorLicenseExpired,ErrorDatabaseExpired,ErrorCustomMgs,ErrorUnknown};

	private ProgramStatus(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static ProgramStatus toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+ProgramStatus.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

public final static class CleanseOperation {
	public final static mdCleanser.CleanseOperation OperationNone=new mdCleanser.CleanseOperation("OperationNone",0);
	public final static mdCleanser.CleanseOperation OperationCase=new mdCleanser.CleanseOperation("OperationCase",1);
	public final static mdCleanser.CleanseOperation OperationPunctuation=new mdCleanser.CleanseOperation("OperationPunctuation",2);
	public final static mdCleanser.CleanseOperation OperationAbbreviation=new mdCleanser.CleanseOperation("OperationAbbreviation",3);
	public final static mdCleanser.CleanseOperation OperationExpression=new mdCleanser.CleanseOperation("OperationExpression",4);
	public final static mdCleanser.CleanseOperation OperationRegularExpression=new mdCleanser.CleanseOperation("OperationRegularExpression",5);
	public final static mdCleanser.CleanseOperation OperationSearchReplace=new mdCleanser.CleanseOperation("OperationSearchReplace",6);

	private final String enumName;
	private final int enumValue;
	private static CleanseOperation[] enumValues={OperationNone,OperationCase,OperationPunctuation,OperationAbbreviation,OperationExpression,OperationRegularExpression,OperationSearchReplace};

	private CleanseOperation(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static CleanseOperation toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+CleanseOperation.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

public final static class CasingMode {
	public final static mdCleanser.CasingMode CaseUpper=new mdCleanser.CasingMode("CaseUpper",0);
	public final static mdCleanser.CasingMode CaseLower=new mdCleanser.CasingMode("CaseLower",1);
	public final static mdCleanser.CasingMode CaseMixed=new mdCleanser.CasingMode("CaseMixed",2);

	private final String enumName;
	private final int enumValue;
	private static CasingMode[] enumValues={CaseUpper,CaseLower,CaseMixed};

	private CasingMode(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static CasingMode toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+CasingMode.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

public final static class PunctuationMode {
	public final static mdCleanser.PunctuationMode PunctuationAdd=new mdCleanser.PunctuationMode("PunctuationAdd",0);
	public final static mdCleanser.PunctuationMode PunctuationStrip=new mdCleanser.PunctuationMode("PunctuationStrip",1);

	private final String enumName;
	private final int enumValue;
	private static PunctuationMode[] enumValues={PunctuationAdd,PunctuationStrip};

	private PunctuationMode(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static PunctuationMode toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+PunctuationMode.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

public final static class AbbreviationMode {
	public final static mdCleanser.AbbreviationMode AbbreviationExpand=new mdCleanser.AbbreviationMode("AbbreviationExpand",0);
	public final static mdCleanser.AbbreviationMode AbbreviationContractConservative=new mdCleanser.AbbreviationMode("AbbreviationContractConservative",1);
	public final static mdCleanser.AbbreviationMode AbbreviationContractAggressive=new mdCleanser.AbbreviationMode("AbbreviationContractAggressive",2);

	private final String enumName;
	private final int enumValue;
	private static AbbreviationMode[] enumValues={AbbreviationExpand,AbbreviationContractConservative,AbbreviationContractAggressive};

	private AbbreviationMode(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static AbbreviationMode toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+AbbreviationMode.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

public final static class FieldDataType {
	public final static mdCleanser.FieldDataType DataTypeGeneral=new mdCleanser.FieldDataType("DataTypeGeneral",0);
	public final static mdCleanser.FieldDataType DataTypeFullName=new mdCleanser.FieldDataType("DataTypeFullName",1);
	public final static mdCleanser.FieldDataType DataTypeInverseName=new mdCleanser.FieldDataType("DataTypeInverseName",2);
	public final static mdCleanser.FieldDataType DataTypePrefix=new mdCleanser.FieldDataType("DataTypePrefix",3);
	public final static mdCleanser.FieldDataType DataTypeFirstName=new mdCleanser.FieldDataType("DataTypeFirstName",4);
	public final static mdCleanser.FieldDataType DataTypeMiddleName=new mdCleanser.FieldDataType("DataTypeMiddleName",5);
	public final static mdCleanser.FieldDataType DataTypeLastName=new mdCleanser.FieldDataType("DataTypeLastName",6);
	public final static mdCleanser.FieldDataType DataTypeSuffix=new mdCleanser.FieldDataType("DataTypeSuffix",7);
	public final static mdCleanser.FieldDataType DataTypeTitleOrDepartment=new mdCleanser.FieldDataType("DataTypeTitleOrDepartment",8);
	public final static mdCleanser.FieldDataType DataTypeCompany=new mdCleanser.FieldDataType("DataTypeCompany",9);
	public final static mdCleanser.FieldDataType DataTypeAddress=new mdCleanser.FieldDataType("DataTypeAddress",10);
	public final static mdCleanser.FieldDataType DataTypeCity=new mdCleanser.FieldDataType("DataTypeCity",11);
	public final static mdCleanser.FieldDataType DataTypeStateOrProvince=new mdCleanser.FieldDataType("DataTypeStateOrProvince",12);
	public final static mdCleanser.FieldDataType DataTypeCountry=new mdCleanser.FieldDataType("DataTypeCountry",13);

	private final String enumName;
	private final int enumValue;
	private static FieldDataType[] enumValues={DataTypeGeneral,DataTypeFullName,DataTypeInverseName,DataTypePrefix,DataTypeFirstName,DataTypeMiddleName,DataTypeLastName,DataTypeSuffix,DataTypeTitleOrDepartment,DataTypeCompany,DataTypeAddress,DataTypeCity,DataTypeStateOrProvince,DataTypeCountry};

	private FieldDataType(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static FieldDataType toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+FieldDataType.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

public final static class CleanseOption {
	public final static mdCleanser.CleanseOption OptCasingAcronym=new mdCleanser.CleanseOption("OptCasingAcronym",0);
	public final static mdCleanser.CleanseOption OptAbbreviateTargetSize=new mdCleanser.CleanseOption("OptAbbreviateTargetSize",1);
	public final static mdCleanser.CleanseOption OptUseLookupCasing=new mdCleanser.CleanseOption("OptUseLookupCasing",2);
	public final static mdCleanser.CleanseOption OptCaseSensitive=new mdCleanser.CleanseOption("OptCaseSensitive",3);
	public final static mdCleanser.CleanseOption OptPartialWord=new mdCleanser.CleanseOption("OptPartialWord",4);
	public final static mdCleanser.CleanseOption OptSingleOccurrence=new mdCleanser.CleanseOption("OptSingleOccurrence",5);
	public final static mdCleanser.CleanseOption OptFuzzySearch=new mdCleanser.CleanseOption("OptFuzzySearch",6);

	private final String enumName;
	private final int enumValue;
	private static CleanseOption[] enumValues={OptCasingAcronym,OptAbbreviateTargetSize,OptUseLookupCasing,OptCaseSensitive,OptPartialWord,OptSingleOccurrence,OptFuzzySearch};

	private CleanseOption(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static CleanseOption toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+CleanseOption.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

public final static class ResultCode {
	public final static mdCleanser.ResultCode scAbbreviated=new mdCleanser.ResultCode("scAbbreviated",0);
	public final static mdCleanser.ResultCode scCase=new mdCleanser.ResultCode("scCase",1);
	public final static mdCleanser.ResultCode scEvalExpression=new mdCleanser.ResultCode("scEvalExpression",2);
	public final static mdCleanser.ResultCode scPunctuate=new mdCleanser.ResultCode("scPunctuate",3);
	public final static mdCleanser.ResultCode scRegularExpression=new mdCleanser.ResultCode("scRegularExpression",4);
	public final static mdCleanser.ResultCode scSearchReplace=new mdCleanser.ResultCode("scSearchReplace",5);
	public final static mdCleanser.ResultCode scNotAbbreviated=new mdCleanser.ResultCode("scNotAbbreviated",6);
	public final static mdCleanser.ResultCode scNotCase=new mdCleanser.ResultCode("scNotCase",7);
	public final static mdCleanser.ResultCode scNotEvalExpression=new mdCleanser.ResultCode("scNotEvalExpression",8);
	public final static mdCleanser.ResultCode scNotPunctuate=new mdCleanser.ResultCode("scNotPunctuate",9);
	public final static mdCleanser.ResultCode scNotRegularExpression=new mdCleanser.ResultCode("scNotRegularExpression",10);
	public final static mdCleanser.ResultCode scNotSearchReplace=new mdCleanser.ResultCode("scNotSearchReplace",11);
	public final static mdCleanser.ResultCode scTriggerAbbreviated=new mdCleanser.ResultCode("scTriggerAbbreviated",12);
	public final static mdCleanser.ResultCode scTriggerCase=new mdCleanser.ResultCode("scTriggerCase",13);
	public final static mdCleanser.ResultCode scTriggerExpression=new mdCleanser.ResultCode("scTriggerExpression",14);
	public final static mdCleanser.ResultCode scTriggerPunctuate=new mdCleanser.ResultCode("scTriggerPunctuate",15);
	public final static mdCleanser.ResultCode scTriggerRegularExpression=new mdCleanser.ResultCode("scTriggerRegularExpression",16);
	public final static mdCleanser.ResultCode scTriggerSearchReplace=new mdCleanser.ResultCode("scTriggerSearchReplace",17);
	public final static mdCleanser.ResultCode scNullAbbreviated=new mdCleanser.ResultCode("scNullAbbreviated",18);
	public final static mdCleanser.ResultCode scNullCase=new mdCleanser.ResultCode("scNullCase",19);
	public final static mdCleanser.ResultCode scNullPunctuate=new mdCleanser.ResultCode("scNullPunctuate",20);
	public final static mdCleanser.ResultCode scNullRegularExpression=new mdCleanser.ResultCode("scNullRegularExpression",21);
	public final static mdCleanser.ResultCode scNullSearchReplace=new mdCleanser.ResultCode("scNullSearchReplace",22);
	public final static mdCleanser.ResultCode scInvalidExpression=new mdCleanser.ResultCode("scInvalidExpression",23);

	private final String enumName;
	private final int enumValue;
	private static ResultCode[] enumValues={scAbbreviated,scCase,scEvalExpression,scPunctuate,scRegularExpression,scSearchReplace,scNotAbbreviated,scNotCase,scNotEvalExpression,scNotPunctuate,scNotRegularExpression,scNotSearchReplace,scTriggerAbbreviated,scTriggerCase,scTriggerExpression,scTriggerPunctuate,scTriggerRegularExpression,scTriggerSearchReplace,scNullAbbreviated,scNullCase,scNullPunctuate,scNullRegularExpression,scNullSearchReplace,scInvalidExpression};

	private ResultCode(String name,int val) {
		enumName=name;
		enumValue=val;
	}

	public static ResultCode toEnum(int val) {
		for (int i=0;i<enumValues.length;i++)
			if (enumValues[i].enumValue==val)
				return enumValues[i];
		throw new IllegalArgumentException("No enum "+ResultCode.class+" with value "+val+".");
	}

	public String toString() {
		return enumName;
	}

	public int toValue() {
		return enumValue;
	}
}

	protected mdCleanser(long i,boolean own) {
		ownMemory=own;
		I=i;
	}

	public mdCleanser() {
		this(mdCleanserJNI.mdCleanserCreate(),true);
	}

	public synchronized void delete() {
		if (I!=0) {
			if (ownMemory) {
				ownMemory=false;
				mdCleanserJNI.mdCleanserDestroy(I);
			}
			I=0;
		}
	}

	public int SetLicenseString(String license) {
		return mdCleanserJNI.SetLicenseString(I,license);
	}

	public void SetPathToCleanserDataFiles(String path) {
		mdCleanserJNI.SetPathToCleanserDataFiles(I,path);
	}

	public int SetEncoding(String EncodeName) {
		return mdCleanserJNI.SetEncoding(I,EncodeName);
	}

	public void SetTriggerExpression(String expression) {
		mdCleanserJNI.SetTriggerExpression(I,expression);
	}

	public void SetTriggerRegularExpression(String expression) {
		mdCleanserJNI.SetTriggerRegularExpression(I,expression);
	}

	public void SetOperationMode(mdCleanser.CleanseOperation mode) {
		mdCleanserJNI.SetOperationMode(I,mode.toValue());
	}

	public void SetFieldDataType(mdCleanser.FieldDataType fldDataType) {
		mdCleanserJNI.SetFieldDataType(I,fldDataType.toValue());
	}

	public void SetCleanseOption(mdCleanser.CleanseOption opt, String optVal) {
		mdCleanserJNI.SetCleanseOption(I,opt.toValue(),optVal);
	}

	public void SetCasingMode(mdCleanser.CasingMode casingMode) {
		mdCleanserJNI.SetCasingMode(I,casingMode.toValue());
	}

	public void SetPunctuationMode(mdCleanser.PunctuationMode punctuationMode) {
		mdCleanserJNI.SetPunctuationMode(I,punctuationMode.toValue());
	}

	public void SetAbbreviationMode(mdCleanser.AbbreviationMode abbreviationMode) {
		mdCleanserJNI.SetAbbreviationMode(I,abbreviationMode.toValue());
	}

	public void SetTransformExpression(String expression) {
		mdCleanserJNI.SetTransformExpression(I,expression);
	}

	public void SetStringColumnValue(String fieldname, String value) {
		mdCleanserJNI.SetStringColumnValue(I,fieldname,value);
	}

	public void SetIntegerColumnValue(String fieldName, int value) {
		mdCleanserJNI.SetIntegerColumnValue(I,fieldName,value);
	}

	public void SetFloatColumnValue(String fieldname, double value) {
		mdCleanserJNI.SetFloatColumnValue(I,fieldname,value);
	}

	public void SetDateTimeColumnValue(String fieldname, String value) {
		mdCleanserJNI.SetDateTimeColumnValue(I,fieldname,value);
	}

	public void SetBooleanColumnValue(String fieldname, int value) {
		mdCleanserJNI.SetBooleanColumnValue(I,fieldname,value);
	}

	public void SetNullColumnValue(String fieldname) {
		mdCleanserJNI.SetNullColumnValue(I,fieldname);
	}

	public void AddRegularExpression(String searchRegEx, String replace) {
		mdCleanserJNI.AddRegularExpression(I,searchRegEx,replace);
	}

	public int SetRegularExpressionTable(String path) {
		return mdCleanserJNI.SetRegularExpressionTable(I,path);
	}

	public void AddSearchReplace(String search, String replace) {
		mdCleanserJNI.AddSearchReplace(I,search,replace);
	}

	public int SetSearchReplaceTable(String path) {
		return mdCleanserJNI.SetSearchReplaceTable(I,path);
	}

	public ProgramStatus InitializeDataFiles() {
		return ProgramStatus.toEnum(mdCleanserJNI.InitializeDataFiles(I));
	}

	public String GetInitializeErrorString() {
		return mdCleanserJNI.GetInitializeErrorString(I);
	}

	public String GetBuildNumber() {
		return mdCleanserJNI.GetBuildNumber(I);
	}

	public String GetDatabaseDate() {
		return mdCleanserJNI.GetDatabaseDate(I);
	}

	public String GetDatabaseExpirationDate() {
		return mdCleanserJNI.GetDatabaseExpirationDate(I);
	}

	public String GetLicenseExpirationDate() {
		return mdCleanserJNI.GetLicenseExpirationDate(I);
	}

	public String Cleanse(String fieldStr) {
		return mdCleanserJNI.Cleanse(I,fieldStr);
	}

	public String GetResultCode() {
		return mdCleanserJNI.GetResultCode(I);
	}

	public String GetResultCodeDescription(String resultStr) {
		return mdCleanserJNI.GetResultCodeDescription(I,resultStr);
	}

}
