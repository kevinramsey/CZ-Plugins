package com.melissadata.kettle.profiler.data;

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;

import com.melissadata.mdProfiler;
import com.melissadata.mdProfiler.ProfilerColumnType;
import com.melissadata.mdProfiler.ProfilerDataType;

public class ProfilerEnum {
	public enum AppendMode {
		APPEND("Add new profiling information to any existing information.", 1),
		OVERWRITE("Open the profile table so that reporting may be done. No new records can be appended to the profile run.", 2),
		REPORT("Overwrite existing output profile table.", 3),
		MUST_NOT_EXIST("Output profile table must not exist.", 4), ;
		public static AppendMode decode(String value) throws KettleException {
			try {
				return AppendMode.valueOf(value);
			} catch (Exception e) {
				System.out.println("AppendMode Unknown: " + value.trim() + " - setting to " + AppendMode.OVERWRITE);
				return AppendMode.OVERWRITE;
			}
		}
		private String	description;
		private int		value;

		private AppendMode(String description, int value) {
			this.description = description;
			this.value = value;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public int getSwigValue() {
			return value;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum ColumnType {
		Integer("64 bit signed Integer", 4) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeInt8;
			}
		},
		Number("64 bit Floating Point Number", 10) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeReal8;
			}
		},
		BigNumber("An arbitrary (unlimited) precision number ", 12) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeNumeric;
			}
		},
		String("Variable-Length Unicode String", 17) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeVariableUnicodeString;
			}
		},
		Date("Date", 18) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeDate;
			}
		},
		TimeStamp("Time Stamp", 22) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeDBTimeStamp;
			}
		},
		Boolean("Boolean Value", 26) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeBoolean;
			}
		},
		Binary("Byte Array", 28) {
			@Override
			public ProfilerColumnType getColumnType() {
				return mdProfiler.ProfilerColumnType.ColumnTypeBytes;
			}
		};
		public static ColumnType decode(String value) {
			try {
				return ColumnType.valueOf(value);
			} catch (Exception e) {
				System.out.println("ERROR decoding ColumnT Type");
				return ColumnType.String;
			}
		}
		private String	description;
		private int		value;

		private ColumnType(String description, int value) {
			this.description = description;
			this.value = value;
		}

		public String encode() {
			return name();
		}

		public abstract ProfilerColumnType getColumnType();

		public String getDescription() {
			return description;
		}

		public int getSwigValue() {
			return value;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum ExpectedContent {
		FullName("Full Name", 1) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeFullName;
			}
		},
		InverseName("Inverse ordered name (Last , First)", 2) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeInverseName;
			}
		},
		NamePrefix("Name prefix", 3) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeNamePrefix;
			}
		},
		FirstName("First name", 4) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeFirstName;
			}
		},
		MiddleName("Middle name", 5) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeMiddleName;
			}
		},
		LastName("Last name", 6) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeLastName;
			}
		},
		NameSuffix("Name Suffix", 7) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeNameSuffix;
			}
		},
		Title("Title / Department", 8) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeTitle;
			}
		},
		Company("Company or Organization", 9) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeCompany;
			}
		},
		Address("Street address", 10) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeAddress;
			}
		},
		City("City Name", 11) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeCity;
			}
		},
		StateOrProvince("US state or Canadian province", 12) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeStateOrProvince;
			}
		},
		ZipOrPostalCode("US zip code or Canadian postal code", 13) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeZipOrPostalCode;
			}
		},
		CityStateZip("City/State/Zip combined", 14) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeCityStateZip;
			}
		},
		Country("Country Name", 15) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeCountry;
			}
		},
		Phone("Phone number", 16) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypePhone;
			}
		},
		Email("Email address", 17) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeEmail;
			}
		},
		String("Generic string value", 18) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeString;
			}
		},
		Numeric("Generic numeric value", 19) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeNumeric;
			}
		},
		DateMDY("Generic Date/Time(formatted Month Day Year)", 20) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeDateMDY;
			}
		},
		DateYMD("Generic Date/Time(formatted Year Month Day)", 21) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeDateYMD;
			}
		},
		DateDMY("Generic Date/Time(formatted Day Month Year)", 22) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeDateDMY;
			}
		},
		Boolean("Generic Boolean Value", 23) {
			@Override
			public ProfilerDataType getDataType() {
				return ProfilerDataType.DataTypeBoolean;
			}
		};
		public static ExpectedContent decode(String value) {
			try {
				return ExpectedContent.valueOf(value.trim());
			} catch (Exception e) {
				System.out.println("Data Type Unknown: " + value.trim() + " - setting to String");
				return ExpectedContent.String;
			}
		}
		private String	description;
		private int		value;

		private ExpectedContent(String description, int value) {
			this.description = description;
			this.value = value;
		}

		public String encode() {
			return name();
		}

		public abstract ProfilerDataType getDataType();

		public String getDescription() {
			return description;
		}

		public int getSwigValue() {
			return value;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum OutputPin {
		COLUMN_BASED_COUNTS("Output profile column counts.", 1) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "ColumnType", "DataType", "ColumnSize", "ColumnPrecision", "ColumnScale", "ValueRangeFrom", "ValueRangeTo", "DefaultValue", "CustomPatterns", "InferredDataType", "Sortation", "SortationPercent",
						"MostPopularCount", "DistinctCount", "UniqueCount", "DefaultValueCount", "BelowRangeCount", "AboveRangeCount", "AboveSizeCount", "AbovePrecisionCount", "AboveScaleCount", "InvalidRegExCount", "EmptyCount", "NullCount",
						"InvalidDataCount", "InvalidUTF8Count", "NonPrintingCharCount", "DiacriticCharCount", "ForeignCharCount", "AlphaOnlyCount", "NumericOnlyCount", "AlphaNumericCount", "UpperCaseOnlyCount", "LowerCaseOnlyCount",
						"MixedCaseCount", "SingleSpaceCount", "MultiSpaceCount", "LeadingSpaceCount", "TrailingSpaceCount", "MaxSpaces", "MinSpaces", "TotalSpaces", "TotalWordBreaks", "AvgSpaces", "DecorationCharCount", "ProfanityCount",
						"InconsistentDataCount", "StringMaxValue", "StringMinValue", "StringQ1Value", "StringMedValue", "StringQ3Value", "StringMaxLength", "StringMinLength", "StringAvgLength", "StringQ1Length", "StringMedLength", "StringQ3Length",
						"WordMaxValue", "WordMinValue", "WordQ1Value", "WordMedValue", "WordQ3Value", "WordMaxLength", "WordMinLength", "WordAvgLength", "WordQ1Length", "WordMedLength", "WordQ3Length", "MaxWords", "MinWords", "AvgWords",
						"NumericMaxValue", "NumericMinValue", "NumericAvgValue", "NumericQ1Value", "NumericQ1IntValue", "NumericMedValue", "NumericMedIntValue", "NumericQ3Value", "NumericQ3IntValue", "NumericStdDevValue", "DateMaxValue",
						"DateMinValue", "DateAvgValue", "DateQ1Value", "DateMedValue", "DateQ3Value", "TimeMaxValue", "TimeMinValue", "TimeAvgValue", "TimeQ1Value", "TimeMedValue", "TimeQ3Value", "DateTimeNoCenturyCount",
						"NameInconsistentOrderCount", "NameMultipleNameCount", "NameSuspiciousNameCount", "StateCount", "ProvinceCount", "StateProvinceNonStandardCount", "StateProvinceInvalidCount", "ZipCodeCount", "Plus4Count",
						"ZipCodeInvalidCount", "PostalCodeCount", "PostalCodeInvalidCount", "ZipCodePostalCodeInvalidCount", "StateZipCodeMismatchCount", "ProvincePostalCodeMismatchCount", "CountryNonStandardCount", "CountryInvalidCount",
						"EmailSyntaxCount", "EmailMobileDomainCount", "EmailMisspelledDomainCount", "EmailSpamtrapDomainCount", "EmailDisposableDomainCount", "PhoneInvalidCount");
			}
		},
		DATE_TIME_FREQUENCIES("distinct value for a column with Date/Time and the counts for each distinct value", 2) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "Value", "Count");
			}
		},
		LENGTH_FREQUENCIES("Output legnth frequencies", 3) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "Length", "Count");
			}
		},
		OVER_ALL_COUNTS("Output profile overall counts.", 4) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("RecordCount", "EmptyCount", "NullCount", "ExactMatchDistinctCount", "ExactMatchDupesCount", "ExactMatchLargestGroup", "ContactMatchDistinctCount", "ContactMatchDupesCount", "ContactMatchLargestGroup",
						"HouseholdMatchDistinctCount", "HouseholdMatchDupesCount", "HouseholdMatchLargestGroup", "AddressMatchDistinctCount", "AddressMatchDupesCount", "AddressMatchLargestGroup");
			}
		},
		PASSTHRU_RESULTCODE("Output passthru fields & Result codes.", 5) {
			@Override
			public List<String> getOutputFields() {
				// These fields are gathered from input selections
				// so there is nothing to return
				return null;
			}
		},
		PATTERN_REGEX_FREQUENCIES("Output regEx frequencies", 6) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "Value", "RegEx", "Example", "Count");
			}
		},
		PROFILE_SESSION("returns information about the processed job", 7) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("BuildNo", "ProfileStart", "ProfileEnd", "TableName", "UserName", "JobName", "JobDescription");
			}
		},
		SOUND_ALIKE_FREQUENCIES("", 8) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "Value", "Example", "Count");
			}
		},
		VALUE_FREQUENCIES("", 9) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "Value", "Count");
			}
		},
		WORD_LENGTH_FREQUENCIES("This output pin allows you to step through each distinct word length for a specific column. ", 10) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "Length", "Count");
			}
		},
		WORD_VALUE_FREQUENCIES("This output pin allows you to step through each distinct word value for a specific column. ", 11) {
			@Override
			public List<String> getOutputFields() {
				return Arrays.asList("ColumnName", "Value", "Count");
			}
		},
		;
		public static OutputPin decode(String value) throws KettleException {
			try {
				return OutputPin.valueOf(value);
			} catch (Exception e) {
				System.out.println("Output Pin Unknown: " + value.trim() + " - setting to " + OutputPin.PASSTHRU_RESULTCODE);
				return OutputPin.PASSTHRU_RESULTCODE;
			}
		}
		private String	description;
		private int		value;

		private OutputPin(String description, int value) {
			this.description = description;
			this.value = value;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public abstract List<String> getOutputFields();

		public int getSwigValue() {
			return value;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}
}
