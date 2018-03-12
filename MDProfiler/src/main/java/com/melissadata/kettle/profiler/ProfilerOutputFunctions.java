package com.melissadata.kettle.profiler;

import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.step.StepMeta;

import com.melissadata.mdProfiler;
import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;
import com.melissadata.kettle.profiler.ui.ProfileRecord;

public class ProfilerOutputFunctions {
	public static void getStringField(RowMetaInterface row, String originName, VariableSpace space, String fieldValue, int fieldLength) {
		if (fieldValue.trim().length() == 0)
			return;
		// Build meta description of field
		ValueMetaInterface v = new ValueMeta(space.environmentSubstitute(fieldValue), ValueMetaInterface.TYPE_STRING);
		v.setOrigin(originName);
		v.setLength(fieldLength);
		row.addValueMeta(v);
	}
	private mdProfiler						profiler;
	private MDProfilerMeta					profilerMeta;
	private MDProfilerData					profilerData;
	private HashMap<String, ProfileRecord>	profileRecords;
	private OutputPin						outputPin;
	private MDProfilerStep					profilerStep;

	public ProfilerOutputFunctions(MDProfilerMeta meta) {
		profilerMeta = meta;
	}

	/*
	 * Methods to get values from mdProfiler
	 */
	private Object getColumnCountValue(String pin, String columnNameStr) {
		if ("ColumnName".equals(pin))
			return columnNameStr;
		if ("ColumnType".equals(pin))
			return profiler.GetColumnColumnType(columnNameStr);
		if ("DataType".equals(pin))
			return profiler.GetColumnDataType(columnNameStr);
		if ("ColumnSize".equals(pin))
			return profiler.GetColumnSize(columnNameStr);
		if ("ColumnPrecision".equals(pin))
			return profiler.GetColumnPrecision(columnNameStr);
		if ("ColumnScale".equals(pin))
			return profiler.GetColumnScale(columnNameStr);
		if ("ValueRangeFrom".equals(pin))
			return profiler.GetColumnValueRangeFrom(columnNameStr);
		if ("ValueRangeTo".equals(pin))
			return profiler.GetColumnValueRangeTo(columnNameStr);
		if ("DefaultValue".equals(pin))
			return profiler.GetColumnDefaultValue(columnNameStr);
		if ("CustomPatterns".equals(pin))
			return profiler.GetColumnCustomPatterns(columnNameStr);
		if ("InferredDataType".equals(pin))
			return profiler.GetColumnInferredDataType(columnNameStr);
		if ("Sortation".equals(pin))
			return profiler.GetColumnSortation(columnNameStr);
		if ("SortationPercent".equals(pin))
			return profiler.GetColumnSortationPercent(columnNameStr);
		if ("MostPopularCount".equals(pin))
			return profiler.GetColumnMostPopularCount(columnNameStr);
		if ("DistinctCount".equals(pin))
			return profiler.GetColumnDistinctCount(columnNameStr);
		if ("UniqueCount".equals(pin))
			return profiler.GetColumnUniqueCount(columnNameStr);
		if ("DefaultValueCount".equals(pin))
			return profiler.GetColumnDefaultValueCount(columnNameStr);
		if ("BelowRangeCount".equals(pin))
			return profiler.GetColumnBelowRangeCount(columnNameStr);
		if ("AboveRangeCount".equals(pin))
			return profiler.GetColumnAboveRangeCount(columnNameStr);
		if ("AboveSizeCount".equals(pin))
			return profiler.GetColumnAboveSizeCount(columnNameStr);
		if ("AbovePrecisionCount".equals(pin))
			return profiler.GetColumnAbovePrecisionCount(columnNameStr);
		if ("AboveScaleCount".equals(pin))
			return profiler.GetColumnAboveScaleCount(columnNameStr);
		if ("InvalidRegExCount".equals(pin))
			return profiler.GetColumnInvalidRegExCount(columnNameStr);
		if ("EmptyCount".equals(pin))
			return profiler.GetColumnEmptyCount(columnNameStr);
		if ("NullCount".equals(pin))
			return profiler.GetColumnNullCount(columnNameStr);
		if ("InvalidDataCount".equals(pin))
			return profiler.GetColumnInvalidDataCount(columnNameStr);
		if ("InvalidUTF8Count".equals(pin))
			return profiler.GetColumnInvalidUTF8Count(columnNameStr);
		if ("NonPrintingCharCount".equals(pin))
			return profiler.GetColumnNonPrintingCharCount(columnNameStr);
		if ("DiacriticCharCount".equals(pin))
			return profiler.GetColumnDiacriticCharCount(columnNameStr);
		if ("ForeignCharCount".equals(pin))
			return profiler.GetColumnForeignCharCount(columnNameStr);
		if ("AlphaOnlyCount".equals(pin))
			return profiler.GetColumnAlphaOnlyCount(columnNameStr);
		if ("NumericOnlyCount".equals(pin))
			return profiler.GetColumnNumericOnlyCount(columnNameStr);
		if ("AlphaNumericCount".equals(pin))
			return profiler.GetColumnAlphaNumericCount(columnNameStr);
		if ("UpperCaseOnlyCount".equals(pin))
			return profiler.GetColumnUpperCaseOnlyCount(columnNameStr);
		if ("LowerCaseOnlyCount".equals(pin))
			return profiler.GetColumnLowerCaseOnlyCount(columnNameStr);
		if ("MixedCaseCount".equals(pin))
			return profiler.GetColumnMixedCaseCount(columnNameStr);
		if ("SingleSpaceCount".equals(pin))
			return profiler.GetColumnSingleSpaceCount(columnNameStr);
		if ("MultiSpaceCount".equals(pin))
			return profiler.GetColumnMultiSpaceCount(columnNameStr);
		if ("LeadingSpaceCount".equals(pin))
			return profiler.GetColumnLeadingSpaceCount(columnNameStr);
		if ("TrailingSpaceCount".equals(pin))
			return profiler.GetColumnTrailingSpaceCount(columnNameStr);
		if ("MaxSpaces".equals(pin))
			return profiler.GetColumnMaxSpaces(columnNameStr);
		if ("MinSpaces".equals(pin))
			return profiler.GetColumnMinSpaces(columnNameStr);
		if ("TotalSpaces".equals(pin))
			return profiler.GetColumnTotalSpaces(columnNameStr);
		if ("TotalWordBreaks".equals(pin))
			return profiler.GetColumnTotalWordBreaks(columnNameStr);
		if ("AvgSpaces".equals(pin))
			return profiler.GetColumnAvgSpaces(columnNameStr);
		if ("DecorationCharCount".equals(pin))
			return profiler.GetColumnDecorationCharCount(columnNameStr);
		if ("ProfanityCount".equals(pin))
			return profiler.GetColumnProfanityCount(columnNameStr);
		if ("InconsistentDataCount".equals(pin))
			return profiler.GetColumnInconsistentDataCount(columnNameStr);
		if ("StringMaxValue".equals(pin))
			return profiler.GetColumnStringMaxValue(columnNameStr);
		if ("StringMinValue".equals(pin))
			return profiler.GetColumnStringMinValue(columnNameStr);
		if ("StringQ1Value".equals(pin))
			return profiler.GetColumnStringQ1Value(columnNameStr);
		if ("StringMedValue".equals(pin))
			return profiler.GetColumnStringMedValue(columnNameStr);
		if ("StringQ3Value".equals(pin))
			return profiler.GetColumnStringQ3Value(columnNameStr);
		if ("StringMaxLength".equals(pin))
			return profiler.GetColumnStringMaxLength(columnNameStr);
		if ("StringMinLength".equals(pin))
			return profiler.GetColumnStringMinLength(columnNameStr);
		if ("StringAvgLength".equals(pin))
			return profiler.GetColumnStringAvgLength(columnNameStr);
		if ("StringQ1Length".equals(pin))
			return profiler.GetColumnStringQ1Length(columnNameStr);
		if ("StringMedLength".equals(pin))
			return profiler.GetColumnStringMedLength(columnNameStr);
		if ("StringQ3Length".equals(pin))
			return profiler.GetColumnStringQ3Length(columnNameStr);
		if ("WordMaxValue".equals(pin))
			return profiler.GetColumnWordMaxValue(columnNameStr);
		if ("WordMinValue".equals(pin))
			return profiler.GetColumnWordMinValue(columnNameStr);
		if ("WordQ1Value".equals(pin))
			return profiler.GetColumnWordQ1Value(columnNameStr);
		if ("WordMedValue".equals(pin))
			return profiler.GetColumnWordMedValue(columnNameStr);
		if ("WordQ3Value".equals(pin))
			return profiler.GetColumnWordQ3Value(columnNameStr);
		if ("WordMaxLength".equals(pin))
			return profiler.GetColumnWordMaxLength(columnNameStr);
		if ("WordMinLength".equals(pin))
			return profiler.GetColumnWordMinLength(columnNameStr);
		if ("WordAvgLength".equals(pin))
			return profiler.GetColumnWordAvgLength(columnNameStr);
		if ("WordQ1Length".equals(pin))
			return profiler.GetColumnWordQ1Length(columnNameStr);
		if ("WordMedLength".equals(pin))
			return profiler.GetColumnWordMedLength(columnNameStr);
		if ("WordQ3Length".equals(pin))
			return profiler.GetColumnWordQ3Length(columnNameStr);
		if ("MaxWords".equals(pin))
			return profiler.GetColumnMaxWords(columnNameStr);
		if ("MinWords".equals(pin))
			return profiler.GetColumnMinWords(columnNameStr);
		if ("AvgWords".equals(pin))
			return profiler.GetColumnAvgWords(columnNameStr);
		if ("NumericMaxValue".equals(pin))
			return profiler.GetColumnNumericMaxValue(columnNameStr);
		if ("NumericMinValue".equals(pin))
			return profiler.GetColumnNumericMinValue(columnNameStr);
		if ("NumericAvgValue".equals(pin))
			return profiler.GetColumnNumericAvgValue(columnNameStr);
		if ("NumericQ1Value".equals(pin))
			return profiler.GetColumnNumericQ1Value(columnNameStr);
		if ("NumericQ1IntValue".equals(pin))
			return profiler.GetColumnNumericQ1IntValue(columnNameStr);
		if ("NumericMedValue".equals(pin))
			return profiler.GetColumnNumericMedValue(columnNameStr);
		if ("NumericMedIntValue".equals(pin))
			return profiler.GetColumnNumericMedIntValue(columnNameStr);
		if ("NumericQ3Value".equals(pin))
			return profiler.GetColumnNumericQ3Value(columnNameStr);
		if ("NumericQ3IntValue".equals(pin))
			return profiler.GetColumnNumericQ3IntValue(columnNameStr);
		if ("NumericStdDevValue".equals(pin))
			return profiler.GetColumnNumericStdDevValue(columnNameStr);
		if ("DateMaxValue".equals(pin))
			return profiler.GetColumnDateMaxValue(columnNameStr);
		if ("DateMinValue".equals(pin))
			return profiler.GetColumnDateMinValue(columnNameStr);
		if ("DateAvgValue".equals(pin))
			return profiler.GetColumnDateAvgValue(columnNameStr);
		if ("DateQ1Value".equals(pin))
			return profiler.GetColumnDateQ1Value(columnNameStr);
		if ("DateMedValue".equals(pin))
			return profiler.GetColumnDateMedValue(columnNameStr);
		if ("DateQ3Value".equals(pin))
			return profiler.GetColumnDateQ3Value(columnNameStr);
		if ("TimeMaxValue".equals(pin))
			return profiler.GetColumnTimeMaxValue(columnNameStr);
		if ("TimeMinValue".equals(pin))
			return profiler.GetColumnTimeMinValue(columnNameStr);
		if ("TimeAvgValue".equals(pin))
			return profiler.GetColumnTimeAvgValue(columnNameStr);
		if ("TimeQ1Value".equals(pin))
			return profiler.GetColumnTimeQ1Value(columnNameStr);
		if ("TimeMedValue".equals(pin))
			return profiler.GetColumnTimeMedValue(columnNameStr);
		if ("TimeQ3Value".equals(pin))
			return profiler.GetColumnTimeQ3Value(columnNameStr);
		if ("DateTimeNoCenturyCount".equals(pin))
			return profiler.GetColumnDateNoCenturyCount(columnNameStr);
		if ("NameInconsistentOrderCount".equals(pin))
			return profiler.GetColumnNameInconsistentOrderCount(columnNameStr);
		if ("NameMultipleNameCount".equals(pin))
			return profiler.GetColumnNameMultipleNameCount(columnNameStr);
		if ("NameSuspiciousNameCount".equals(pin))
			return profiler.GetColumnNameSuspiciousNameCount(columnNameStr);
		if ("StateCount".equals(pin))
			return profiler.GetColumnStateCount(columnNameStr);
		if ("ProvinceCount".equals(pin))
			return profiler.GetColumnProvinceCount(columnNameStr);
		if ("StateProvinceNonStandardCount".equals(pin))
			return profiler.GetColumnStateProvinceNonStandardCount(columnNameStr);
		if ("StateProvinceInvalidCount".equals(pin))
			return profiler.GetColumnStateProvinceInvalidCount(columnNameStr);
		if ("ZipCodeCount".equals(pin))
			return profiler.GetColumnZipCodeCount(columnNameStr);
		if ("Plus4Count".equals(pin))
			return profiler.GetColumnPlus4Count(columnNameStr);
		if ("ZipCodeInvalidCount".equals(pin))
			return profiler.GetColumnZipCodeInvalidCount(columnNameStr);
		if ("PostalCodeCount".equals(pin))
			return profiler.GetColumnPostalCodeCount(columnNameStr);
		if ("PostalCodeInvalidCount".equals(pin))
			return profiler.GetColumnPostalCodeInvalidCount(columnNameStr);
		if ("ZipCodePostalCodeInvalidCount".equals(pin))
			return profiler.GetColumnZipCodePostalCodeInvalidCount(columnNameStr);
		if ("StateZipCodeMismatchCount".equals(pin))
			return profiler.GetColumnStateZipCodeMismatchCount(columnNameStr);
		if ("ProvincePostalCodeMismatchCount".equals(pin))
			return profiler.GetColumnProvincePostalCodeMismatchCount(columnNameStr);
		if ("CountryNonStandardCount".equals(pin))
			return profiler.GetColumnCountryNonStandardCount(columnNameStr);
		if ("CountryInvalidCount".equals(pin))
			return profiler.GetColumnCountryInvalidCount(columnNameStr);
		if ("EmailSyntaxCount".equals(pin))
			return profiler.GetColumnEmailSyntaxCount(columnNameStr);
		if ("EmailMobileDomainCount".equals(pin))
			return profiler.GetColumnEmailMobileDomainCount(columnNameStr);
		if ("EmailMisspelledDomainCount".equals(pin))
			return profiler.GetColumnEmailMisspelledDomainCount(columnNameStr);
		if ("EmailSpamtrapDomainCount".equals(pin))
			return profiler.GetColumnEmailSpamtrapDomainCount(columnNameStr);
		if ("EmailDisposableDomainCount".equals(pin))
			return profiler.GetColumnEmailDisposableDomainCount(columnNameStr);
		if ("PhoneInvalidCount".equals(pin))
			return profiler.GetColumnPhoneInvalidCount(columnNameStr);
		return "Column Count  Error:" + pin + " - " + columnNameStr;
	}

	private void getCountFields(RowMetaInterface row, RowMetaInterface cleanRow, String originName) {
		// create vmi
		ValueMetaInterface v = null;
		for (String pin : outputPin.getOutputFields()) {
			v = new ValueMeta(pin, ValueMetaInterface.TYPE_STRING);
			v.setOrigin(originName);
			cleanRow.addValueMeta(v);
		}
		row.clear();
		row.addRowMeta(cleanRow);
	}

	private Object getDataValues(String pin, String cName) {
		if ("ColumnName".equals(pin))
			return cName;
		if ("Value".equals(pin))
			return profiler.GetDataFrequencyValue(cName);
		if ("Count".equals(pin))
			return profiler.GetDataFrequencyCount(cName);
		return "Data value Error:" + pin + " - " + cName;
	}

	private Object getDateValues(String pin, String cName) {
		if ("ColumnName".equals(pin))
			return cName;
		if ("Value".equals(pin))
			return profiler.GetDateFrequencyValue(cName);
		if ("Count".equals(pin))
			return profiler.GetDateFrequencyCount(cName);
		return "date value  Error:" + pin + " - " + cName;
	}

	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) throws KettleException {
		RowMetaInterface cleanRow = new RowMeta();
		outputPin = null;
		for (FilterTarget ft : profilerMeta.oFilterFields.filterTargets.values()) {
			if ((ft.getTargetStep() != null) && ft.getTargetStepname().equals(nextStep.getName())) {
				outputPin = ft.getPin();
			}
		}
		if (outputPin == null)
			return;
		if (outputPin == OutputPin.PASSTHRU_RESULTCODE) {
			getPassThruResultsFields(row, cleanRow, originName, space);
		} else {
			getCountFields(row, cleanRow, originName);
		}
	}

	private Object getLengthValues(String pin, String cName) {
		if ("ColumnName".equals(pin))
			return cName;
		if ("Length".equals(pin))
			return profiler.GetLengthFrequencyValue(cName);
		if ("Count".equals(pin))
			return profiler.GetLengthFrequencyCount(cName);
		return "length  Error:" + pin + " - " + cName;
	}

	private Object getOverAllCountValue(String pin) {
		if ("RecordCount".equals(pin))
			return profiler.GetTableRecordCount();
		if ("EmptyCount".equals(pin))
			return profiler.GetTableRecordEmptyCount();
		if ("NullCount".equals(pin))
			return profiler.GetTableRecordNullCount();
		if ("ExactMatchDistinctCount".equals(pin))
			return profiler.GetTableExactMatchDistinctCount();
		if ("ExactMatchDupesCount".equals(pin))
			return profiler.GetTableExactMatchDupesCount();
		if ("ExactMatchLargestGroup".equals(pin))
			return profiler.GetTableExactMatchLargestGroup();
		if ("ContactMatchDistinctCount".equals(pin))
			return profiler.GetTableContactMatchDistinctCount();
		if ("ContactMatchDupesCount".equals(pin))
			return profiler.GetTableContactMatchDupesCount();
		if ("ContactMatchLargestGroup".equals(pin))
			return profiler.GetTableContactMatchLargestGroup();
		if ("HouseholdMatchDistinctCount".equals(pin))
			return profiler.GetTableHouseholdMatchDistinctCount();
		if ("HouseholdMatchDupesCount".equals(pin))
			return profiler.GetTableHouseholdMatchDupesCount();
		if ("HouseholdMatchLargestGroup".equals(pin))
			return profiler.GetTableHouseholdMatchLargestGroup();
		if ("AddressMatchDistinctCount".equals(pin))
			return profiler.GetTableAddressMatchDistinctCount();
		if ("AddressMatchDupesCount".equals(pin))
			return profiler.GetTableAddressMatchDupesCount();
		if ("AddressMatchLargestGroup".equals(pin))
			return profiler.GetTableAddressMatchLargestGroup();
		return "Over All  Error:" + pin;
	}

	/*
	 * Methods to get sets of field names for the output.
	 */
	private void getPassThruResultsFields(RowMetaInterface row, RowMetaInterface cleanRow, String originName, VariableSpace space) {
		profileRecords = profilerMeta.getProfileRecords();
		if ((profileRecords != null) && (profileRecords.size() > 0)) {
			//String fieldname = "";
			for(int i = 0; i < row.size(); i++){
				//fieldname = row.getValueMeta(i).getName();
				ProfileRecord field = profileRecords.get(row.getValueMeta(i).getName());
				// Look for that pass thru field in the input row meta
				if (field.isDoPassThrough()) {
					ValueMetaInterface v = row.searchValueMeta(field.getColumnName());
					if (v != null) {
						// We found a value. Clone it and add to the result row
						v = v.clone();
						cleanRow.addValueMeta(v);
					}
				}
			}

//			for (ProfileRecord field : profileRecords.values()) {
//				System.out.println("Check Field : " + field.getColumnName() + " isPass = " + field.isDoPassThrough());
//				// Look for that pass thru field in the input row meta
//				if (field.isDoPassThrough()) {
//					ValueMetaInterface v = row.searchValueMeta(field.getColumnName());
//					if (v != null) {
//						// We found a value. Clone it and add to the result row
//						v = v.clone();
//						cleanRow.addValueMeta(v);
//					}
//				}
//			}
		}
		row.clear();
		row.addRowMeta(cleanRow);
		// Get fields defined by the data.
		if ((profileRecords != null) && (profileRecords.size() > 0)) {
			for (ProfileRecord field : profileRecords.values()) {
				if (field.isDoResults()) {
					getStringField(row, originName, space, field.getResutName(), Integer.parseInt(field.getLength()));
				}
			}
		}
	}

	private Object getPatternValues(String pin, String cName) {
		if ("ColumnName".equals(pin))
			return cName;
		if ("Value".equals(pin))
			return profiler.GetPatternFrequencyValue(cName);
		if ("RegEx".equals(pin))
			return profiler.GetPatternFrequencyRegEx(cName);
		if ("Example".equals(pin))
			return profiler.GetPatternFrequencyExample(cName);
		if ("Count".equals(pin))
			return profiler.GetPatternFrequencyCount(cName);
		return "pattern values Error:" + pin + " - " + cName;
	}

	private RowSet getRow(List<RowSet> rowSets, FilterTarget target) {
		for (RowSet row : rowSets) {
			if (row.getDestinationStepName().equals(target.getTargetStepname()))
				return row;
		}
		return null;
	}

	private Object getSessionValues(String pin) {
		if ("BuildNo".equals(pin))
			return profiler.GetBuildNumber();
		if ("ProfileStart".equals(pin))
			return profiler.GetProfileStartDateTime();
		if ("ProfileEnd".equals(pin))
			return profiler.GetProfileEndDateTime();
		if ("TableName".equals(pin))
			return profiler.GetTableName();
		if ("UserName".equals(pin))
			return profiler.GetUserName();
		if ("JobName".equals(pin))
			return profiler.GetJobName();
		if ("JobDescription".equals(pin))
			return profiler.GetJobDescription();
		return "session value Error:" + pin;
	}

	private Object getSoundExValues(String pin, String cName) {
		if ("ColumnName".equals(pin))
			return cName;
		if ("Value".equals(pin))
			return profiler.GetSoundExFrequencyValue(cName);
		if ("Example".equals(pin))
			return profiler.GetSoundExFrequencyExample(cName);
		if ("Count".equals(pin))
			return profiler.GetSoundExFrequencyCount(cName);
		return "soundex  Error:" + pin + " - " + cName;
	}

	private Object getWordLengthValues(String pin, String cName) {
		if ("ColumnName".equals(pin))
			return cName;
		if ("Length".equals(pin))
			return profiler.GetWordLengthFrequencyValue(cName);
		if ("Count".equals(pin))
			return profiler.GetWordLengthFrequencyCount(cName);
		return "Word Length Error:" + pin + " - " + cName;
	}

	private Object getWordValues(String pin, String cName) {
		if ("ColumnName".equals(pin))
			return cName;
		if ("Value".equals(pin))
			return profiler.GetWordFrequencyValue(cName);
		if ("Count".equals(pin))
			return profiler.GetWordFrequencyCount(cName);
		return "Word Value Error:" + pin + " - " + cName;
	}

	/*
	 * Methods to output pin values
	 */
	private void handleColumnBasedCounts(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				if (profiler.StartDataFrequency(pr.getColumnName(), mdProfiler.Order.OrderCountAscending) > 0) {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.COLUMN_BASED_COUNTS, null);
					for (String pin : request.outputMeta.getFieldNames()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getColumnCountValue(pin, pr.getColumnName()));
					}
					requests.add(request);
				}
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.COLUMN_BASED_COUNTS.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	public boolean handleCompletedRequests(MDProfilerStep step, List<MDProfilerRequest> requests, MDProfilerMeta meta, MDProfilerData pData, boolean done, OutputPin pin) throws KettleException {
		profiler = meta.getProfiler();
		profilerStep = step;
		outputPin = pin;
		profilerData = pData;
		profilerStep.logBasic("get results for - " + outputPin.name());
		if (outputPin == OutputPin.COLUMN_BASED_COUNTS) {
			handleColumnBasedCounts(requests);
		} else if (outputPin == OutputPin.DATE_TIME_FREQUENCIES) {
			handleDateTimeFrequencies(requests);
		} else if (outputPin == OutputPin.LENGTH_FREQUENCIES) {
			handleLengthFrequencies(requests);
		} else if (outputPin == OutputPin.OVER_ALL_COUNTS) {
			handleOverAllCounts(requests);
		} else if (outputPin == OutputPin.PASSTHRU_RESULTCODE)
			return true;
		else if (outputPin == OutputPin.PATTERN_REGEX_FREQUENCIES) {
			handlePatternFrequencies(requests);
		} else if (outputPin == OutputPin.PROFILE_SESSION) {
			handleProfileSession(requests);
		} else if (outputPin == OutputPin.SOUND_ALIKE_FREQUENCIES) {
			handleSoundExFrequencies(requests);
		} else if (outputPin == OutputPin.VALUE_FREQUENCIES) {
			handleDataFrequencies(requests);
		} else if (outputPin == OutputPin.WORD_LENGTH_FREQUENCIES) {
			handleWordLengthFrequencies(requests);
		} else if (outputPin == OutputPin.WORD_VALUE_FREQUENCIES) {
			handleWordValueFrequencies(requests);
		}
		return true;
	}

	private void handleDataFrequencies(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				String cName = pr.getColumnName();
				profiler.StartDataFrequency(cName, mdProfiler.Order.OrderCountAscending);
				do {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.VALUE_FREQUENCIES, null);
					for (String pin : request.outputMeta.getFieldNames()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getDataValues(pin, cName));
					}
					requests.add(request);
				} while (profiler.GetNextDataFrequency(cName) == 1);
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.VALUE_FREQUENCIES.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handleDateTimeFrequencies(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				String cName = pr.getColumnName();
				profiler.StartDateFrequency(cName, mdProfiler.Order.OrderCountAscending, mdProfiler.DateSpan.DateSpanDate);
				do {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.DATE_TIME_FREQUENCIES, null);
					for (String pin : request.outputMeta.getFieldNames()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getDateValues(pin, cName));
					}
					requests.add(request);
				} while (profiler.GetNextDateFrequency(cName) == 1);
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.DATE_TIME_FREQUENCIES.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handleLengthFrequencies(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				String cName = pr.getColumnName();
				profiler.StartLengthFrequency(cName, mdProfiler.Order.OrderCountAscending);
				do {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.LENGTH_FREQUENCIES, null);
					for (String pin : OutputPin.LENGTH_FREQUENCIES.getOutputFields()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getLengthValues(pin, cName));
					}
					requests.add(request);
				} while (profiler.GetNextLengthFrequency(cName) == 1);
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.LENGTH_FREQUENCIES.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handleOverAllCounts(List<MDProfilerRequest> requests) throws KettleStepException {
		requests.add(profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.OVER_ALL_COUNTS, null));
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			for (String pin : OutputPin.OVER_ALL_COUNTS.getOutputFields()) {
				request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getOverAllCountValue(pin));
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.OVER_ALL_COUNTS.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handlePatternFrequencies(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				String cName = pr.getColumnName();
				profiler.StartPatternFrequency(cName, mdProfiler.Order.OrderCountAscending);
				do {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.PATTERN_REGEX_FREQUENCIES, null);
					for (String pin : request.outputMeta.getFieldNames()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getPatternValues(pin, cName));
					}
					requests.add(request);
				} while (profiler.GetNextPatternFrequency(cName) == 1);
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.PATTERN_REGEX_FREQUENCIES.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handleProfileSession(List<MDProfilerRequest> requests) throws KettleStepException {
		requests.add(profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.PROFILE_SESSION, null));
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			for (String pin : request.outputMeta.getFieldNames()) {
				String val = getSessionValues(pin).toString();
				request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getSessionValues(pin));
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.PROFILE_SESSION.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handleSoundExFrequencies(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				String cName = pr.getColumnName();
				profiler.StartSoundExFrequency(cName, mdProfiler.Order.OrderCountAscending);
				do {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.SOUND_ALIKE_FREQUENCIES, null);
					for (String pin : request.outputMeta.getFieldNames()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getSoundExValues(pin, cName));
					}
					requests.add(request);
				} while (profiler.GetNextSoundExFrequency(cName) == 1);
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.SOUND_ALIKE_FREQUENCIES.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handleWordLengthFrequencies(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				String cName = pr.getColumnName();
				profiler.StartWordLengthFrequency(cName, mdProfiler.Order.OrderCountAscending);
				do {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.WORD_LENGTH_FREQUENCIES, null);
					for (String pin : request.outputMeta.getFieldNames()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getWordLengthValues(pin, cName));
					}
					requests.add(request);
				} while (profiler.GetNextWordLengthFrequency(cName) == 1);
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.WORD_LENGTH_FREQUENCIES.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}

	private void handleWordValueFrequencies(List<MDProfilerRequest> requests) throws KettleStepException {
		if (profileRecords == null) {
			profileRecords = profilerMeta.getProfileRecords();
		}
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoProfile()) {
				String cName = pr.getColumnName();
				profiler.StartWordFrequency(cName, mdProfiler.Order.OrderCountAscending);
				do {
					MDProfilerRequest request = profilerStep.getService().buildRequest(profilerData.sourceIO, OutputPin.WORD_VALUE_FREQUENCIES, null);
					for (String pin : request.outputMeta.getFieldNames()) {
						request.outputData = RowDataUtil.addValueData(request.outputData, request.outputDataSize++, getWordValues(pin, cName));
					}
					requests.add(request);
				} while (profiler.GetNextWordFrequency(cName) == 1);
			}
		}
		for (MDProfilerRequest request : requests) {
			if (profilerStep.isStopped()) {
				break;
			}
			RowSet rowSet = getRow(request.ioMeta.targetRowSets, profilerMeta.oFilterFields.filterTargets.get(OutputPin.WORD_VALUE_FREQUENCIES.name()));
			if (rowSet != null) {
				profilerStep.putRowTo(request.outputMeta, request.outputData, rowSet);
			}
		}
	}
}
