package com.melissadata.kettle.cleanser;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.mdCleanser;
import com.melissadata.mdCleanser.CleanseOperation;

public class MDCleanserEnum {

	private static Class<?>	PKG	= MDCleanserMeta.class;

	/*
	 * Program status
	 */
	public enum ProgramStatus {
		ErrorNone("None") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorNone;
			}
		},
		ErrorConfigFile("Config File Error") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorConfigFile;
			}
		},
		ErrorBothAddFuncAndTableSelected("Function and Table") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorBothAddFuncAndTableSelected;
			}
		},
		ErrorRequireAddFuncOrTableFile("Require Function or Table") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorRequireAddFuncOrTableFile;
			}
		},
		ErrorLicenseInvalid("Invalid License") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorLicenseInvalid;
			}
		},
		ErrorLicenseExpired("Expired License") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorLicenseExpired;
			}
		},
		ErrorDatabaseExpired("Data Expired") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorDatabaseExpired;
			}
		},
		ErrorCustomMgs("Custom Message") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorCustomMgs;
			}
		},
		ErrorUnknown("Unknown Error") {
			@Override
			public com.melissadata.mdCleanser.ProgramStatus getMDEnum() {
				return mdCleanser.ProgramStatus.ErrorUnknown;
			}
		};

		private String	value;

		private ProgramStatus(String value) {
			this.value = value;
		}

		public abstract mdCleanser.ProgramStatus getMDEnum();

		public static ProgramStatus decode(String value) throws KettleException {
			try {
				return ProgramStatus.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Unknown", "") + value, e);
			}
		}

		public String encode() {
			return name();
		}

		public String getValue() {
			return value;
		}

		public static String[] stringValues() {

			String[] sVals = new String[ProgramStatus.values().length];

			int i = 0;
			for (ProgramStatus ps : ProgramStatus.values()) {
				sVals[i] = ps.value;
				i++;
			}

			return sVals;
		}

		@Override
		public String toString() {
			return getValue();
		}

	};

	/*
	 * Rule Operations
	 */
	public enum RuleOperation {

		OperationCase("Case") {
			@Override
			public CleanseOperation getMDEnum() {
				return mdCleanser.CleanseOperation.OperationCase;
			}
		},
		OperationPunctuation("Punctuation") {
			@Override
			public CleanseOperation getMDEnum() {
				return mdCleanser.CleanseOperation.OperationPunctuation;
			}
		},
		OperationAbbreviation("Abbreviation") {
			@Override
			public CleanseOperation getMDEnum() {
				return mdCleanser.CleanseOperation.OperationAbbreviation;
			}
		},
		OperationExpression("Expression") {
			@Override
			public CleanseOperation getMDEnum() {
				return mdCleanser.CleanseOperation.OperationExpression;
			}
		},
		OperationRegularExpression("Reg Ex") {
			@Override
			public CleanseOperation getMDEnum() {
				return mdCleanser.CleanseOperation.OperationRegularExpression;
			}
		},
		OperationTextSearchReplace("Search and Replace") {
			@Override
			public CleanseOperation getMDEnum() {
				return mdCleanser.CleanseOperation.OperationSearchReplace;
			}
		};

		private String	strValue;

		private RuleOperation(String value) {
			this.strValue = value;
		}

		public abstract mdCleanser.CleanseOperation getMDEnum();

		public static RuleOperation decode(String value) throws KettleException {
			try {
				return RuleOperation.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCleanserMeta.Unknown", "") + value, e);
			} catch (NoClassDefFoundError e) {
				// handle carefully
				return null;
			}
		}

		public static RuleOperation fromString(String text) {
			if (text != null) {
				for (RuleOperation co : RuleOperation.values()) {
					if (text.equalsIgnoreCase(co.strValue)) { return co; }
				}
			}
			return null;
		}

		public String encode() {
			return name();
		}

		public String getValue() {
			return strValue;
		}

		public static String[] stringValues() {

			String[] sVals = new String[RuleOperation.values().length];

			int i = 0;
			for (RuleOperation ps : RuleOperation.values()) {
				sVals[i] = ps.strValue;
				i++;
			}

			return sVals;
		}

		@Override
		public String toString() {
			return getValue();
		}
	};

	/*
	 * Casing Mode
	 */
	public enum CasingMode {
		CaseUpper("Upper") {
			@Override
			public com.melissadata.mdCleanser.CasingMode getMDEnum() {
				return mdCleanser.CasingMode.CaseUpper;
			}
		},
		CaseLower("Lower") {
			@Override
			public com.melissadata.mdCleanser.CasingMode getMDEnum() {
				return mdCleanser.CasingMode.CaseLower;
			}
		},
		CaseMixed("Mixed") {
			@Override
			public com.melissadata.mdCleanser.CasingMode getMDEnum() {
				return mdCleanser.CasingMode.CaseMixed;
			}
		};

		private String	value;

		private CasingMode(String value) {
			this.value = value;
		}

		public abstract mdCleanser.CasingMode getMDEnum();

		public static CasingMode decode(String value) throws KettleException {
			try {
				return CasingMode.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Unknown", "") + value, e);
			} catch (NoClassDefFoundError e) {
				// handle carefully
				return null;
			}
		}

		public static CasingMode fromString(String text) {
			if (text != null) {
				for (CasingMode co : CasingMode.values()) {
					if (text.equalsIgnoreCase(co.value)) { return co; }
				}
			}
			return null;
		}

		public String encode() {
			return name();
		}

		public String getValue() {
			return value;
		}

		public static String[] stringValues() {

			String[] sVals = new String[CasingMode.values().length];

			int i = 0;
			for (CasingMode ps : CasingMode.values()) {
				sVals[i] = ps.value;
				i++;
			}

			return sVals;
		}

		@Override
		public String toString() {
			return getValue();
		}

	};

	/*
	 * Punctuation Mode
	 */
	public enum PunctuationMode {
		PunctuationAdd("Add") {
			@Override
			public com.melissadata.mdCleanser.PunctuationMode getMDEnum() {
				return mdCleanser.PunctuationMode.PunctuationAdd;
			}
		},
		PunctuationStrip("Strip") {
			@Override
			public com.melissadata.mdCleanser.PunctuationMode getMDEnum() {
				return mdCleanser.PunctuationMode.PunctuationStrip;
			}
		};

		private String	value;

		private PunctuationMode(String value) {
			this.value = value;
		}

		public abstract mdCleanser.PunctuationMode getMDEnum();

		public static PunctuationMode decode(String value) throws KettleException {
			try {
				return PunctuationMode.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Unknown", "") + value, e);
			} catch (NoClassDefFoundError e) {
				// handle carefully
				return null;
			}
		}

		public static PunctuationMode fromString(String text) {
			if (text != null) {
				for (PunctuationMode co : PunctuationMode.values()) {
					if (text.equalsIgnoreCase(co.value)) { return co; }
				}
			}
			return null;
		}

		public String encode() {
			return name();
		}

		public String getValue() {
			return value;
		}

		public static String[] stringValues() {

			String[] sVals = new String[PunctuationMode.values().length];

			int i = 0;
			for (PunctuationMode ps : PunctuationMode.values()) {
				sVals[i] = ps.value;
				i++;
			}

			return sVals;
		}

		@Override
		public String toString() {
			return getValue();
		}

	};

	/*
	 * Abbreviation Mode
	 */
	public enum AbbreviationMode {
	
		AbbreviationExpand("Expand") {
			@Override
			public com.melissadata.mdCleanser.AbbreviationMode getMDEnum() {
				return mdCleanser.AbbreviationMode.AbbreviationExpand;
			}
		},
		AbbreviationContractConservative("Contract Conservative") {
			@Override
			public com.melissadata.mdCleanser.AbbreviationMode getMDEnum() {
				return mdCleanser.AbbreviationMode.AbbreviationContractConservative;
			}
		},
		AbbreviationContractAggressive("Contract Aggressive") {
			@Override
			public com.melissadata.mdCleanser.AbbreviationMode getMDEnum() {
				return mdCleanser.AbbreviationMode.AbbreviationContractAggressive;
			}
		};

		private String	value;

		private AbbreviationMode(String value) {
			this.value = value;
		}

		public abstract mdCleanser.AbbreviationMode getMDEnum();

		public static AbbreviationMode decode(String value) throws KettleException {
			try {
				return AbbreviationMode.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Unknown", "") + value, e);
			} catch (NoClassDefFoundError e) {
				// handle carefully
				return null;
			}
		}

		public static AbbreviationMode fromString(String text) {
			if (text != null) {
				for (AbbreviationMode co : AbbreviationMode.values()) {
					if (text.equalsIgnoreCase(co.value)) { return co; }
				}
			}
			return null;
		}

		public String encode() {
			return name();
		}

		public String getValue() {
			return value;
		}

		public static String[] stringValues() {

			String[] sVals = new String[AbbreviationMode.values().length];

			int i = 0;
			for (AbbreviationMode ps : AbbreviationMode.values()) {
				sVals[i] = ps.value;
				i++;
			}

			return sVals;
		}

		@Override
		public String toString() {
			return getValue();
		}

	};

	/*
	 * Field Data Type
	 */
	public enum FieldDataType {
		DataTypeGeneral("General") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeGeneral;
			}
		},
		DataTypeFullName("Full Name") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeFullName;
			}
		},
		DataTypeInverseName("Inverse Name") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeInverseName;
			}
		},
		DataTypePrefix("Prefix") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypePrefix;
			}
		},
		DataTypeFirstName("First Name") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeFirstName;
			}
		},
		DataTypeMiddleName("Middle  Name") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeMiddleName;
			}
		},
		DataTypeLastName("Last Name") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeLastName;
			}
		},
		DataTypeSuffix("Suffix") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeSuffix;
			}
		},
		DataTypeTitleOrDepartment("Title Department") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeTitleOrDepartment;
			}
		},
		DataTypeCompany("Company") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeCompany;
			}
		},
		DataTypeAddress("Address") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeAddress;
			}
		},
		DataTypeCity("City") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeCity;
			}
		},
		DataTypeStateOrProvince("State") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeStateOrProvince;
			}
		},
		DataTypeCountry("Country") {
			@Override
			public com.melissadata.mdCleanser.FieldDataType getMDEnum() {
				return mdCleanser.FieldDataType.DataTypeCountry;
			}
		};

		private String	value;

		private FieldDataType(String value) {
			this.value = value;
		}

		public abstract mdCleanser.FieldDataType getMDEnum();

		public static FieldDataType decode(String value) throws KettleException {
			try {
				return FieldDataType.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Unknown", "") + value, e);
			} catch (NoClassDefFoundError e) {
				// handle carefully
				return null;
			}
		}

		public static FieldDataType fromString(String text) {
			if (text != null) {
				for (FieldDataType co : FieldDataType.values()) {
					if (text.equalsIgnoreCase(co.value)) { return co; }
				}
			}
			return null;
		}

		public String encode() {
			return name();
		}

		public String getValue() {
			return value;
		}

		public static String[] stringValues() {

			String[] sVals = new String[FieldDataType.values().length];

			int i = 0;
			for (FieldDataType ps : FieldDataType.values()) {
				sVals[i] = ps.value;
				i++;
			}

			return sVals;
		}

		@Override
		public String toString() {
			return getValue();
		}

	};

	/*
	 * Rule Option
	 */
	public enum RuleOption {
	
		OptNone("None") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return null;
			}
		},		
		OptAbbreviateTargetSize("Abbreviate Target Size") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return mdCleanser.CleanseOption.OptAbbreviateTargetSize;
			}
		},
		OptCasingAcronym("Casing Acronym") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return mdCleanser.CleanseOption.OptCasingAcronym;
			}
		},
		OptUseLookupCasing("Use Lookup Casing") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return mdCleanser.CleanseOption.OptUseLookupCasing;
			}
		},
		OptCaseSensitive("Case Sensitive") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return mdCleanser.CleanseOption.OptCaseSensitive;
			}
		},
		OptPartialWord("Partial Word") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return mdCleanser.CleanseOption.OptPartialWord;
			}
		},
		OptSingleOccurrence("Single Occurence") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return mdCleanser.CleanseOption.OptSingleOccurrence;
			}
		},
		OptFuzzySearch("Fuzzy Search") {
			@Override
			public com.melissadata.mdCleanser.CleanseOption getMDEnum() {
				return mdCleanser.CleanseOption.OptFuzzySearch;
			}
		};

		private String	value;

		private RuleOption(String value) {
			this.value = value;
		}

		public abstract mdCleanser.CleanseOption getMDEnum();

		public static RuleOption decode(String value) throws KettleException {
			try {
				return RuleOption.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDPersonatorMeta.PhoneFormat.Unknown", "") + value, e);
			} catch (NoClassDefFoundError e) {
				// handle carefully
				return null;
			}
		}

		public static String[] optionsFor(RuleOperation operation) {

			String[] caseAr = new String[] { "Casing Acronym" };
			String[] abrivAr = new String[] { "Abbreviate Target Size", "None"};
			String[] optionAr = new String[] { "Use Lookup Casing", "Case Sensitive", "Partial Word", "Single Occurence", "Fuzzy Search" };
			String[] regExAr = new String[] { "Use Lookup Casing", "Single Occurence" };

			if (operation == RuleOperation.OperationCase) {
				return caseAr;
			} else if (operation == RuleOperation.OperationAbbreviation) {
				return abrivAr;
			} else if (operation == RuleOperation.OperationRegularExpression) {
				return regExAr;
			} else {
				return optionAr;
			}
		}

		public static RuleOption fromString(String text) {
			if (text != null) {
				for (RuleOption co : RuleOption.values()) {
					if (text.equalsIgnoreCase(co.value)) { return co; }
				}
			}
			return null;
		}

		public String encode() {
			return name();
		}

		public String getValue() {
			return value;
		}

		public static String[] stringValues() {

			String[] sVals = new String[RuleOption.values().length];

			int i = 0;
			for (RuleOption ps : RuleOption.values()) {
				sVals[i] = ps.value;
				i++;
			}

			return sVals;
		}

		@Override
		public String toString() {
			return getValue();
		}

	};

}
