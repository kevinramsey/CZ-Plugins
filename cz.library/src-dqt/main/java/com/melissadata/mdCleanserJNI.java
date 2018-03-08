package com.melissadata;

public class mdCleanserJNI {
	static {
//		try {
//			System.loadLibrary("mdCleanser");
//		} catch (UnsatisfiedLinkError ule) {
//			System.out.println(ule);
//			System.out.println("java.library.path="+System.getProperty("java.library.path"));
//		}
	}

	public final static native long mdCleanserCreate();
	public final static native void mdCleanserDestroy(long I);
	public final static native int SetLicenseString(long I,String license);
	public final static native void SetPathToCleanserDataFiles(long I,String path);
	public final static native int SetEncoding(long I,String EncodeName);
	public final static native void SetTriggerExpression(long I,String expression);
	public final static native void SetTriggerRegularExpression(long I,String expression);
	public final static native void SetOperationMode(long I,int mode);
	public final static native void SetFieldDataType(long I,int fldDataType);
	public final static native void SetCleanseOption(long I,int opt,String optVal);
	public final static native void SetCasingMode(long I,int casingMode);
	public final static native void SetPunctuationMode(long I,int punctuationMode);
	public final static native void SetAbbreviationMode(long I,int abbreviationMode);
	public final static native void SetTransformExpression(long I,String expression);
	public final static native void SetStringColumnValue(long I,String fieldname,String value);
	public final static native void SetIntegerColumnValue(long I,String fieldName,int value);
	public final static native void SetFloatColumnValue(long I,String fieldname,double value);
	public final static native void SetDateTimeColumnValue(long I,String fieldname,String value);
	public final static native void SetBooleanColumnValue(long I,String fieldname,int value);
	public final static native void SetNullColumnValue(long I,String fieldname);
	public final static native void AddRegularExpression(long I,String searchRegEx,String replace);
	public final static native int SetRegularExpressionTable(long I,String path);
	public final static native void AddSearchReplace(long I,String search,String replace);
	public final static native int SetSearchReplaceTable(long I,String path);
	public final static native int InitializeDataFiles(long I);
	public final static native String GetInitializeErrorString(long I);
	public final static native String GetBuildNumber(long I);
	public final static native String GetDatabaseDate(long I);
	public final static native String GetDatabaseExpirationDate(long I);
	public final static native String GetLicenseExpirationDate(long I);
	public final static native String Cleanse(long I,String fieldStr);
	public final static native String GetResultCode(long I);
	public final static native String GetResultCodeDescription(long I,String resultStr);
}
