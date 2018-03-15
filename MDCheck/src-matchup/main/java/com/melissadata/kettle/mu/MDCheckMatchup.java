package com.melissadata.kettle.mu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.melissadata.kettle.MDCheck;
import com.melissadata.kettle.MDCheckData;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.mu.evaluator.Algorithm;
import com.melissadata.kettle.mu.evaluator.EvaluatorException;
import com.melissadata.kettle.support.FilterTarget;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.mu.evaluator.Evaluator;
import com.melissadata.kettle.mu.evaluator.QualityScore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import com.melissadata.mdMUHybrid;
import com.melissadata.mdMUReadWrite;
import com.melissadata.kettle.mu.MatchUpMeta.MapField;
import com.melissadata.kettle.mu.evaluator.EvalItem;
import com.melissadata.kettle.mu.evaluator.RankedRecord;
import com.melissadata.kettle.mu.evaluator.SurvivorField;

public class MDCheckMatchup extends MDCheck {

	// dupe records management
	public static class DupeRecord {

		public enum eStatus {
			Suppressed, Intersected, Unique, HasDupe, IsDupe, Suppressor, Intersector
		}

		;
		public String key;
		public int    combinations;
		public int    recordType;
		public long   offset;
		public eStatus status = eStatus.Suppressed;

		public DupeRecord(String key, int combinations, int recordType, long offset) {

			this.key = key;
			this.combinations = combinations;
			this.recordType = recordType;
			this.offset = offset;
		}
	}

	// List suppression behavior
	private enum LookupBehavior {
		Suppress, Intersect,
	}

	/*
	 * structure used to manage output data
	 */
	private class OutputData implements Cloneable {

		public Object[]     data;
		public int          size;
		public int          source;
		public int          dupeGroup;
		public int          nonLookupCount;
		public String       key;
		public List<String> resultCodes;

		public OutputData(int size, int src) {

			data = RowDataUtil.allocateRowData(size);
			size = 0;
			source = src;
		}

		@Override
		public OutputData clone() throws CloneNotSupportedException {

			OutputData clonedOut = (OutputData) super.clone();
			clonedOut.data = new Object[data.length];
			for (int cc = 0; cc < data.length; cc++) {
				clonedOut.data[cc] = data[cc];
			}
			return clonedOut;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			OutputData other = (OutputData) obj;
			if (!Arrays.equals(data, other.data)) {
				return false;
			}
			if (size != other.size) {
				return false;
			}
			if (source != other.source) {
				return false;
			}
			if (dupeGroup != other.dupeGroup) {
				return false;
			}
			if (nonLookupCount != other.nonLookupCount) {
				return false;
			}
			return true;
		}
	}

	// Used by the evaluator
	public static int getColumnIndex(String cName) {

		for (int t = 0; t < outputValueMeta.size(); t++) {
			if (outputValueMeta.get(t).getName().equals(cName)) {
				return t;
			}
		}
		int val = -1;
		for (int t = 0; t < outputColumnNames.length; t++) {
			if ((outputColumnNames[t] != null) && outputColumnNames[t].equals(cName)) {
				val = t;
			}
		}
		return val;
	}

	private static       Class<?>                 PKG                     = MDCheckMatchup.class;
	private static final int                      TYPE_SOURCE             = 0;
	private static final int                      TYPE_LOOKUP             = 1;
	private static final String                   STACK_GROUP             = "Stack Group";
	// pass thru cache file
	private              File                     passThruFile            = null;
	private              RandomAccessFile         passThruRAFile          = null;
	private              LookupBehavior           lookupBehavior          = null;
	private              boolean                  noPurge                 = false;
	private              boolean                  isGoldenRecordSelection = false;
	// current processing tracking
	private              int                      currentOutputRecord     = 0;
	private              boolean                  readingDone             = false;
	private              int                      reqsRead                = 0;
	public static        int                      recordLimit             = 0;
	public static        boolean                  hasLimit                = false;
	private static       String[]                 outputColumnNames       = null;
	private static       List<ValueMetaInterface> outputValueMeta         = null;
	private static       String[]                 lookUpColumns           = null;
	private              OutputData               outputDataHolder        = null;
	private              List<DupeRecord>         dupeBuffer              = null;
	// Output results
	private              List<String>             resultCodes             = null;
	private              int                      currentDupeGroup        = 0;
	private              int                      nonLookupCount          = 0;
	private              KettleException          kettleException         = null;

	public MDCheckMatchup(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {

		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/**
	 * Cleans up cache file
	 */
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		// Delete the pass thru cache file (if any)
		if (passThruRAFile != null) {
			try {
				passThruRAFile.close();
			} catch (IOException ignored) {
			}
		}
		if (passThruFile != null) {
			passThruFile.delete();
		}
		// call parent handler
		super.dispose(smi, sdi);
	}

	/**
	 * Overriden in order to process the lookup stream first
	 */
	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		MDCheckMeta meta = (MDCheckMeta) smi;
		MDCheckData data = (MDCheckData) sdi;
		// If this is the first time then we need to initialize the matchup process
		if (first) {
			// Initialize the pass thru cache file
			if (!processInitPassThru(data)) {
				return false;
			}
			// Process all the lookup rows
			if (!processLookup(meta, data)) {
				setOutputDone(); // signal end to receiver(s)
				return false;
			}
		}
		reqsRead++;
		if ((reqsRead > (recordLimit + 1)) && hasLimit) {
			String muMessage = "";
			String title     = "";
			stopAll();
			if (recordLimit < 100000) {
				title = BaseMessages.getString(PKG, "MDCheckMatchup.Community.Title");
				muMessage = BaseMessages.getString(PKG, "MDCheckMatchup.Comunity.RecordLimit", recordLimit);
			} else {
				title = BaseMessages.getString(PKG, "MDCheckMatchup.Lite.Title");
				muMessage = BaseMessages.getString(PKG, "MDCheckMatchup.Lite.RecordLimit", recordLimit);
			}
			logError(muMessage);
			if (MDCheckMeta.isSpoon()) {
				MessageBox box = new MessageBox(new Shell(new Display()), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText(title);
				box.setMessage(muMessage);
				box.open();
			}
			return false;
		}
		// Call parent handler for the source rows
		return super.processRow(smi, sdi);
	}

	/**
	 * Called to calucate the result code for a duplicate records
	 *
	 * @param dupeRecord
	 * @return
	 */
	private List<String> calculateResultCodes(DupeRecord dupeRecord) {

		List<String> resultCodes = new ArrayList<String>();
		switch (dupeRecord.status) {
			case Unique:
				resultCodes.add("MS01");
				break;
			case Suppressor:
				resultCodes.add("MS30");
				break;
			case Intersector:
				resultCodes.add("MS31");
				break;
			case HasDupe:
				resultCodes.add("MS02");
				break;
			case IsDupe:
				resultCodes.add("MS03");
				break;
			case Suppressed:
				resultCodes.add("MS04");
				break;
			case Intersected:
				resultCodes.add("MS05");
				break;
		}
		for (int i = 0; i < 16; i++) {
			if ((dupeRecord.combinations & (0x0001 << i)) != 0) {
				resultCodes.add("MS0" + (i + 6));
			}
		}
		return resultCodes;
	}

	/**
	 * Calculate the result code from a non-duplicate result
	 *
	 * @param statusCode
	 * @param combinations
	 * @return
	 */
	private List<String> calculateResultCodes(String statusCode, int combinations) {

		List<String> resultCodes = new ArrayList<String>();
		if (statusCode.equalsIgnoreCase("C")) {
			resultCodes.add("MS01");
		} else if (statusCode.equalsIgnoreCase("B")) {
			resultCodes.add("MS02");
		} else if (statusCode.equalsIgnoreCase("A")) {
			resultCodes.add("MS03");
		}
		for (int i = 0; i < 16; i++) {
			if ((combinations & (0x0001 << i)) != 0) {
				resultCodes.add("MS0" + (i + 6));
			}
		}
		return resultCodes;
	}

	private boolean doGRSelection(MDCheckMeta meta) {

		for (Algorithm alg : meta.getData().getMatchUp().getAlgorithms()) {
			if (alg.isSelected()) {
				return true;
			}
		}
		return false;
	}

	private List<OutputData> evaluateGoldenRecord(MDCheckMeta meta, Evaluator evaluator, List<OutputData> lDupes) throws KettleException {
		// Get the list of algorythms to use for goldenRecord selection
		boolean         endProcess   = false;
		List<Algorithm> lsAlgorithms = meta.getData().getMatchUp().getAlgorithms();
		Collections.sort(lsAlgorithms);
		List<RankedRecord> arEvaluatedRecords = new ArrayList<RankedRecord>();
		List<OutputData>   finalOutput        = new ArrayList<OutputData>();
		String             expression         = "";
		List<OutputData>   removeList         = new ArrayList<OutputData>();
		for (Algorithm algorithm : lsAlgorithms) {
			if (algorithm.isSelected()) {
				if (algorithm.algorithmType == Algorithm.AlgorithmType.DATAQUALITYSCORE) {
					// qualityScore is processed separately because depending on selections
					// it may have to run multiple evaluations.
					processQualityScores(meta, evaluator, lDupes, finalOutput);
				} else {
					expression = algorithm.getExpression();
					evaluator.setExpression(expression);
					evaluator.parseExpression();
					arEvaluatedRecords.clear();
					int dIndex = 0;
					removeList.clear();
					for (OutputData opd : lDupes) {
						try {
							if (opd.source == TYPE_SOURCE) {
								EvalItem ei = evaluator.evaluateExpression(opd.data);
								if (ei.value != null) {
									arEvaluatedRecords.add(new RankedRecord(opd.data, ei, dIndex));
								} else {
									finalOutput.add(opd);
									removeList.add(opd);
								}
							} else {
								finalOutput.add(opd);
								removeList.add(opd);
							}
							dIndex++;
						} catch (EvaluatorException e) {
							/* This happens inside a running thread. If we throw an error here it causes a crash in
							*  the native object.  So we break out and save exception to throw on the out side.
							*/
							endProcess = true;
							kettleException = new KettleException(e.getMessage());
							break;
						}
					}
					if (endProcess) {
						return null;
					}
					if (arEvaluatedRecords.size() > 0) {
						if (arEvaluatedRecords.get(0).sortType.equals("NUMERIC")) {
							if (algorithm.getOption().equals("Lowest") || algorithm.getOption().equals("Oldest")) {
								Collections.sort(arEvaluatedRecords, RankedRecord.NumericAscendingComparator);
							} else {
								Collections.sort(arEvaluatedRecords, RankedRecord.NumericDecendingComparator);
							}
						} else if (arEvaluatedRecords.get(0).sortType.equals("STRING")) {
							if (algorithm.getOption().equals("Lowest") || algorithm.getOption().equals("Oldest")) {
								Collections.sort(arEvaluatedRecords, RankedRecord.LexicalDescendingComparator);
							} else {
								Collections.sort(arEvaluatedRecords, RankedRecord.LexicalAscendingComparator);
							}
						} else if (arEvaluatedRecords.get(0).sortType.equals("DATE")) {
							if (algorithm.getOption().equals("Lowest") || algorithm.getOption().equals("Oldest")) {
								Collections.sort(arEvaluatedRecords, RankedRecord.DateAscendingComparator);
							} else {
								Collections.sort(arEvaluatedRecords, RankedRecord.DateDescendingComparator);
							}
						} else {
							logError("NO SORT TYPE FOR:" + arEvaluatedRecords.get(0).sortValue.dataType);
						}
					}
					for (RankedRecord raRec : arEvaluatedRecords) {
						if (arEvaluatedRecords.get(0).sortValue.value != raRec.sortValue.value) {
							int pos = lDupes.get(raRec.index).resultCodes.indexOf("MS02");
							if (pos >= 0) {
								lDupes.get(raRec.index).resultCodes.set(pos, "MS03");
							}
							finalOutput.add(lDupes.get(raRec.index));
							removeList.add(lDupes.get(raRec.index));
						}
					}
					// remove any LookUp sourced records
					for (OutputData rm : removeList) {
						lDupes.remove(rm);
					}
				}// End
				// Check if we are down to one record
				// if not we will continue
				if (lDupes.size() == 1) {
					int pos = lDupes.get(0).resultCodes.indexOf("MS03");
					if (pos >= 0) {
						lDupes.get(0).resultCodes.set(pos, "MS02");
					}
					finalOutput.add(0, lDupes.get(0));
					break;
				}
			}
		}
		// We still have a tie
		if (lDupes.size() > 1) {
			// Add to front of list
			for (OutputData od : lDupes) {
				// make sure they are all MS03 (is Dupe)
				int pos = od.resultCodes.indexOf("MS02");
				if (pos >= 0) {
					od.resultCodes.set(pos, "MS03");
				}
				finalOutput.add(0, od);
			}
			// set top one to MS02 (has Dupes)
			int pos = finalOutput.get(0).resultCodes.indexOf("MS03");
			if (pos >= 0) {
				finalOutput.get(0).resultCodes.set(pos, "MS02");
			}
		}
		return finalOutput;
	}

	private List<OutputData> evaluateSurvivorship(MDCheckData data, MDCheckMeta meta, Evaluator evaluator, List<OutputData> lDupes) throws KettleException, CloneNotSupportedException {

		List<SurvivorField> lsSurvivorFields   = meta.getData().getSourcePassThru().getSurvivorPassThru();
		List<RankedRecord>  lsEvaluatedRecords = new ArrayList<RankedRecord>();
		List<OutputData>    finalOutput        = new ArrayList<OutputData>();
		String              expression         = "";
		List<String>        lookUpColNames     = null;
		if (lookUpColumns != null) {
			lookUpColNames = Arrays.asList(lookUpColumns);
		}
		Set<Integer> rmIndexes    = new HashSet<Integer>();
		OutputData   topOutput    = null;
		int          topIndex     = 0;
		boolean      isDescending = false;
		int          dataSize     = 0;
		OutputData   odat         = null;
		// Step through duplicates getting values for any field that is not from
		// an input field, and get the top Record that will be used to
		// accept the survivor field values.
		for (int outIdx = 0; outIdx < lDupes.size(); outIdx++) {
			odat = lDupes.get(outIdx);
			if (odat.source == TYPE_SOURCE) {
				// if we havent set a top yet
				if (topOutput == null) {
					// since this will be the first Source record in the list
					topOutput = odat.clone();
					dataSize = odat.data.length;
					topIndex = outIdx;
				}
			} else {
				// Lookup
				finalOutput.add(odat);
				rmIndexes.add(outIdx);
			}
		}
		if (dataSize == 0) {
			// we only have lookup records so just return
			return lDupes;
		}
		/*
		 * Lookup records do not change, but because they can contribute to
		 * survivor ship we clone the record and remove it from the dupe list,
		 * and add the clone back at the same index. The original has already
		 * been added to finalOutput list
		 */
		int dupeIndex = -1;
		for (Integer indexToRemove : rmIndexes) {
			OutputData clonedOut = null;
			try {
				clonedOut = lDupes.get(indexToRemove).clone();
			} catch (CloneNotSupportedException e) {
				// should ever happen
				throw new KettleException("Error: " + e.getMessage());
			}
			dupeIndex = indexToRemove;
			lDupes.remove(indexToRemove.intValue());
			Object[] dataHolder = new Object[dataSize];
			/*
			 * Lookup input may have more or less fields than the source input
			 * if we can't find a lookup input for a given field we need to
			 * account for that index
			 */
			if (lookUpColNames != null) {
				for (int survivorIndex = 0; survivorIndex < lsSurvivorFields.size(); survivorIndex++) {
					SurvivorField currentSurvivorField = lsSurvivorFields.get(survivorIndex);
					int           lookUpFieldIndex     = lookUpColNames.indexOf(currentSurvivorField.getSource());
					if (lookUpFieldIndex >= 0) {
						dataHolder[survivorIndex] = clonedOut.data[lookUpFieldIndex];
					} else {
						// make fake field
						dataHolder[survivorIndex] = null;
					}
				}
				clonedOut.data = dataHolder;
				// to preserve order
				lDupes.add(dupeIndex, clonedOut);
			}
		}
		/*
		 * Now step through the fields and do any prioritization and sorting to
		 * come up with our final top record with survivors.
		 */
		List<Integer> cleanIndex = new ArrayList<Integer>();
		for (int survivorIndex = 0; survivorIndex < lsSurvivorFields.size(); survivorIndex++) {
			SurvivorField currentField = lsSurvivorFields.get(survivorIndex);
			isDescending = currentField.isDescending();
			boolean doSort;
			lsEvaluatedRecords.clear();
			// Default Prioritization use top record
			if (currentField.getPrioritization().equals(currentField.defaultPriortization())) {
				doSort = false;
				for (OutputData opd : lDupes) {
					lsEvaluatedRecords.add(new RankedRecord(opd.data[survivorIndex]));
				}
			} else {
				doSort = true;
				expression = currentField.getPrioritization();
				evaluator.setExpression(expression);
				evaluator.parseExpression();
				for (OutputData opd : lDupes) {
					lsEvaluatedRecords.add(new RankedRecord(opd.data[survivorIndex], evaluator.evaluateExpression(opd.data)));
				}
			}
			if ((lsEvaluatedRecords.size() > 0) && doSort) {
				if (lsEvaluatedRecords.get(0).sortType.equals("NUMERIC")) {
					if (!isDescending) {
						Collections.sort(lsEvaluatedRecords, RankedRecord.NumericAscendingComparator);
					} else {
						Collections.sort(lsEvaluatedRecords, RankedRecord.NumericDecendingComparator);
					}
				} else if (lsEvaluatedRecords.get(0).sortType.equals("STRING")) {
					if (isDescending) {
						Collections.sort(lsEvaluatedRecords, RankedRecord.LexicalDescendingComparator);
					} else {
						Collections.sort(lsEvaluatedRecords, RankedRecord.LexicalAscendingComparator);
					}
				} else if (lsEvaluatedRecords.get(0).sortType.equals("DATE")) {
					if (!isDescending) {
						Collections.sort(lsEvaluatedRecords, RankedRecord.DateDescendingComparator);
					} else {
						Collections.sort(lsEvaluatedRecords, RankedRecord.DateAscendingComparator);
					}
				} else {
					System.out.println("NO sort type for :" + lsEvaluatedRecords.get(0).sortType);
				}
			}
			// get survivor values in order
			Object[] values = new Object[lsEvaluatedRecords.size()];
			for (int c = 0; c < values.length; c++) {
				try {
					values[c] = lsEvaluatedRecords.get(c).arRecord[0];
				} catch (Exception cce) {
					throw new KettleException("Error getting string value of: " + lsEvaluatedRecords.get(c).arRecord[0] + " - " + cce.toString());
				}
			}
			if (currentField.getConsolidationMethod().toString().contains(STACK_GROUP)) {
				try {
					// Add to cleanIndex so we know what fields to clean
					cleanIndex.add(survivorIndex);
					topOutput.data[survivorIndex] = currentField.getConsolidationMethod().getStackGroup(values, currentField.getStackIndex());
				} catch (ArrayIndexOutOfBoundsException e) {
					topOutput.data[survivorIndex] = "";
				}
			} else {
				topOutput.data[survivorIndex] = currentField.getConsolidationMethod().getConsolidatedField(values, currentField.getDataType());
			}
		}// End lsSurvivorFields loop
		// remove the record that was the Golden Recored from previous steps
		// so we can add the altered version with survivors in it place
		lDupes.remove(topIndex);
		if (!cleanIndex.isEmpty()) {
			cleanIndex.remove(0);// Leave the first one alone
		}
		for (OutputData opd : lDupes) {
			// we already have Lookup in our final output so don't add again
			if (opd.source == TYPE_SOURCE) {
				// do some clean up
				for (int ci : cleanIndex) {
					opd.data[ci] = "";
				}
				finalOutput.add(opd);
			}
		}
		// put out newly created top record at the top of the list
		finalOutput.add(0, topOutput);
		return finalOutput;
	}

	/**
	 * This method adds the matchup result values to the output record
	 *
	 * @param data
	 * @param outputData
	 */
	private void getMatchUpOutput(MDCheckData data, OutputData outputData) {
		// Result codes
		StringBuffer rcBuffer = new StringBuffer();
		String       sep      = "";
		for (String rc : resultCodes) {
			rcBuffer.append(sep).append(rc);
			sep = ",";
		}
		outputData.resultCodes = resultCodes;
		// Suppress or intersect...
		if (data.UsingLookup) {
			if ((getRecordType(data) == TYPE_LOOKUP) && (lookupBehavior == LookupBehavior.Suppress) && resultCodes.contains("MS01")) {
				outputData.dupeGroup = 0;
				outputData.nonLookupCount = 0;
			} else if ((getRecordType(data) == TYPE_SOURCE) && noPurge && (lookupBehavior == LookupBehavior.Suppress) && resultCodes.contains("MS01")) {
				outputData.dupeGroup = 0;
				outputData.nonLookupCount = 1;
			} else if ((getRecordType(data) == TYPE_LOOKUP) && (lookupBehavior == LookupBehavior.Intersect) && resultCodes.contains("MS01")) {
				outputData.dupeGroup = 0;
				outputData.nonLookupCount = 0;
			} else if ((getRecordType(data) == TYPE_SOURCE) && noPurge && (lookupBehavior == LookupBehavior.Intersect) && resultCodes.contains("MS05")) {
				outputData.dupeGroup = 0;
				outputData.nonLookupCount = 1;
			} else {
				outputData.dupeGroup = currentDupeGroup;
				outputData.nonLookupCount = nonLookupCount;
			}
			// Matchcode key
			outputData.key = dupeBuffer.get(currentOutputRecord).key;
		}
		// Easy way:
		else {
			// Dupe Group
			outputData.dupeGroup = data.ReadWrite.GetDupeGroup();
			// Dupe Count
			outputData.nonLookupCount = data.ReadWrite.GetCount();
			// Matchcode key
			outputData.key = data.ReadWrite.GetKey();
		}
	}

	/**
	 * @param data
	 * @return The next result record from the matchup process
	 */
	private boolean getNextRecord(MDCheckData data) {
		// de-reference object
		mdMUReadWrite ReadWrite = data.ReadWrite;
		// Deduping with a Lookup, they must want to do a list suppress or intersect:
		if (data.UsingLookup) {
			// First time through, have to prime the pump:
			if (currentOutputRecord == -1) {
				readingDone = (ReadWrite.ReadRecord() == 0);
			}
			// No more new ones, we've spit out the last batch:
			currentOutputRecord++;
			if (readingDone && (currentOutputRecord == dupeBuffer.size())) {
				return false;
			}
			// Working our way through a dupe group:
			if ((currentOutputRecord > 0) && (currentOutputRecord < dupeBuffer.size())) {
				resultCodes = calculateResultCodes(dupeBuffer.get(currentOutputRecord));
				return true;
			}
			// Process a new batch of dupes:
			{
				currentDupeGroup = ReadWrite.GetDupeGroup();
				dupeBuffer.clear();
				do {
					// Get the record type and offset from the user info
					String[] userInfo = ReadWrite.GetUserInfo().split(",");
					int      recordType;
					try {
						recordType = Integer.valueOf(userInfo[0]);
					} catch (NumberFormatException e) {
						// TODO: Best way to handle this?
						recordType = TYPE_LOOKUP;
					}
					long offset;
					try {
						offset = Long.valueOf(userInfo[1]);
					} catch (NumberFormatException e) {
						// TODO: Best way to handle this?
						offset = -1;
					}
					// Track the duplicate record information
					dupeBuffer.add(new DupeRecord(ReadWrite.GetKey(), ReadWrite.GetCombinations(), recordType, offset));
					readingDone = (ReadWrite.ReadRecord() == 0);
				} while (!readingDone && (currentDupeGroup == ReadWrite.GetDupeGroup()));
				// Process the dupe group results
				processDupeGroup();
				// Start with first dupe record
				currentOutputRecord = 0;
				resultCodes = calculateResultCodes(dupeBuffer.get(currentOutputRecord));
				return true;
			}
		}
		// No Lookup, much easier:
		else {
			boolean gotRecord = ReadWrite.ReadRecord() != 0;
			if (gotRecord) {
				resultCodes = calculateResultCodes(ReadWrite.GetStatusCode(), ReadWrite.GetCombinations());
			}
			return gotRecord;
		}
	}

	/**
	 * Called to generate a completed output record
	 *
	 * @param data
	 * @param ioMeta
	 * @return
	 * @throws KettleException
	 */
	private OutputData getOurputRecord(MDCheckData data, IOMeta ioMeta) throws KettleException {
		// Allocate the output data
		OutputData outputData = new OutputData(ioMeta.outputMeta.size(), getRecordType(data));
		// Add the pass thru records to the output
		processCompletePassThru(data, outputData);
		// Add the result values to the output
		getMatchUpOutput(data, outputData);
		return outputData;
	}

	private List<OutputData> getOutputDataList(MDCheckData data) throws KettleException {

		List<OutputData> lOutputData    = new ArrayList<OutputData>();
		OutputData       nextOutputData = null;
		if (outputDataHolder == null) {
			return null;
		}
		lOutputData.add(outputDataHolder);
		outputDataHolder = null;
		// get the next so we can check
		do {
			if (!getNextRecord(data)) {
				break;
			}
			nextOutputData = null;
			if (getRecordType(data) == TYPE_SOURCE) {
				nextOutputData = getOurputRecord(data, data.sourceIO);
			} else {
				nextOutputData = getOurputRecord(data, data.lookupIO);
			}
			// if we are still on the same dupeGroup add it to the list
			if (lOutputData.get(0).dupeGroup == nextOutputData.dupeGroup) {
				lOutputData.add(nextOutputData);
			} else {
				outputDataHolder = nextOutputData;
			}
		} while (outputDataHolder == null);
		return lOutputData;
	}

	/**
	 * @param data
	 * @return The current result's record offset
	 */
	private long getRecordOffset(MDCheckData data) {
		// If lookup defined then get offset from dupe record
		if (data.UsingLookup) {
			return dupeBuffer.get(currentOutputRecord).offset;
		}
		// If no lookup used then get the offset directly from the object
		String[] userInfo = data.ReadWrite.GetUserInfo().split(",");
		try {
			return Long.valueOf(userInfo[1]);
		} catch (NumberFormatException e) {
			// TODO: Best way to handle this?
			return -1;
		}
	}

	/**
	 * @param data
	 * @return the current result's record type
	 */
	private int getRecordType(MDCheckData data) {
		// If lookup defined then we need to differentiate the source type
		if (data.UsingLookup) {
			return dupeBuffer.get(currentOutputRecord).recordType;
		}
		// If no lookup used then this is always a source record
		return TYPE_SOURCE;
	}

	/**
	 * This is the sister method to processRequestPassThru. It retrieves the
	 * original pass thru record fields to add to the output record.
	 *
	 * @param data
	 * @param outputData
	 * @throws KettleException
	 */
	private void processCompletePassThru(MDCheckData data, OutputData outputData) throws KettleException {

		try {
			// Get the offset of the current record
			long offset = getRecordOffset(data);
			// Seek to that record
			passThruRAFile.seek(offset);
			// Read the object data
			InputStream input = new InputStream() {
				@Override
				public int available() throws IOException {

					return (int) (passThruRAFile.length() - passThruRAFile.getFilePointer());
				}

				@Override
				public boolean markSupported() {

					return false;
				}

				@Override
				public int read() throws IOException {

					return passThruRAFile.read();
				}

				@Override
				public int read(byte[] b) throws IOException {

					return passThruRAFile.read(b);
				}

				@Override
				public int read(byte[] b, int off, int len) throws IOException {

					return passThruRAFile.read(b, off, len);
				}

				@Override
				public long skip(long n) throws IOException {

					return passThruRAFile.skipBytes((int) n);
				}
			};
			ObjectInputStream ois          = new ObjectInputStream(input);
			Object[]          passThruData = (Object[]) ois.readObject();
			ois.close();
			// Add it to the output data
			outputData.data = RowDataUtil.addRowData(outputData.data, outputData.size, passThruData);
			outputData.size += passThruData.length;
		} catch (IOException e) {
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoReadPassThruFile"), e);
		} catch (ClassNotFoundException e) {
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoReadPassThruFile"), e);
		}
	}

	/**
	 * Called to process a single group of duplicate records
	 */
	private void processDupeGroup() {
		// See if any Lookup records were found and count the non-Lookup records:
		boolean lookupFound = false;
		nonLookupCount = 0;
		for (DupeRecord record : dupeBuffer) {
			if (record.recordType == TYPE_LOOKUP) {
				lookupFound = true;
			} else {
				nonLookupCount++;
			}
		}
		// Suppression run, all lookup records, no source records:
		if ((lookupBehavior == LookupBehavior.Suppress) && lookupFound && (nonLookupCount == 0)) {
			for (DupeRecord record : dupeBuffer) {
				record.status = DupeRecord.eStatus.Unique;
				record.combinations = 0;
			}
		}
		// Suppression run, and we've found a Lookup record, suppress the whole group:
		else if ((lookupBehavior == LookupBehavior.Suppress) && lookupFound) {
			for (DupeRecord record : dupeBuffer) {
				if (record.recordType == TYPE_LOOKUP) {
					record.status = DupeRecord.eStatus.Suppressor;
				} else {
					record.status = DupeRecord.eStatus.Suppressed;
				}
			}
		}
		// Intersection run, all lookup records, no source records:
		else if ((lookupBehavior == LookupBehavior.Intersect) && lookupFound && (nonLookupCount == 0)) {
			for (DupeRecord record : dupeBuffer) {
				record.status = DupeRecord.eStatus.Unique;
				record.combinations = 0;
			}
		}
		// Intersection run, and we have not found a Lookup record, suppress the whole group:
		else if ((lookupBehavior == LookupBehavior.Intersect) && !lookupFound) {
			for (DupeRecord record : dupeBuffer) {
				record.status = DupeRecord.eStatus.Intersected;
				record.combinations = 0;
			}
		}
		// Must be okay, let's mark up the dupes:
		else if (nonLookupCount > 0) {
			boolean firstRegular = true;
			for (DupeRecord record : dupeBuffer) {
				if (record.recordType == TYPE_SOURCE) {
					// Only non-lookup record in set
					if ((nonLookupCount == 1) || noPurge) {
						record.status = DupeRecord.eStatus.Unique;
					} else if (firstRegular) {
						record.status = DupeRecord.eStatus.HasDupe;
					} else {
						record.status = DupeRecord.eStatus.IsDupe;
					}
					firstRegular = false;
					if (noPurge && (lookupBehavior != LookupBehavior.Intersect)) {
						record.combinations = 0;
					}
				} else {
					record.status = DupeRecord.eStatus.Intersector;
				}
			}
			if (noPurge) {
				nonLookupCount = 1;
			}
		}
	}

	/**
	 * Initializes the pass thru caching file
	 *
	 * @param data
	 * @return
	 * @throws KettleException
	 */
	private boolean processInitPassThru(MDCheckData data) throws KettleException {

		try {
			// Create the cache file name
			passThruFile = File.createTempFile("muPass", null, data.realWorkPath);
			passThruRAFile = new RandomAccessFile(passThruFile, "rw");
			return true;
		} catch (IOException e) {
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoPassThruFile"), e);
		}
	}

	/**
	 * Process the data from the lookup stream.
	 */
	private boolean processLookup(MDCheckMeta meta, MDCheckData data) throws KettleException {
		// De-reference the lookup stream.
		String   lookupStepname = meta.getStepIOMeta().getInfoStreams().get(0).getStepname();
		StepMeta stepMeta       = getTransMeta().findStep(lookupStepname);
		if (stepMeta == null) {
			// If it doesn't exist then we aren't doing lookup
			return true;
		}
		// Get the set of rows from the lookup step
		RowSet rowSet = findInputRowSet(lookupStepname);
		if (rowSet == null) {
			// If it doesn't exist then the connection to the lookup step has probably been broken
			// TODO: Issue a warning?
			return true;
		}
		// Wait for the first row of lookup data
		Object[] inputData = getRowFrom(rowSet);
		// If there was no input data on the lookup stream then we are done
		if (inputData == null) {
			return true;
		}
		// Define the lookup I/O data
		IOMeta lookupIO = data.lookupIO = new IOMeta();
		// Retrieve the source fields from the previous step
		lookupIO.inputMeta = rowSet.getRowMeta();
		// Get meta information for output row.
		// Start with a copy of the input meta.
		// The Pass Thru step will filter this down to the fields that should be passed thru.
		lookupIO.outputMeta = lookupIO.inputMeta.clone();
		// De-reference the lookup target step
		MDCheckStepData stepData         = meta.getData();
		LookupTarget    lookupTarget     = stepData.getOutputFilter().getLookupTarget();
		StepMeta        lookupTargetStep = null;
		if ((lookupTarget != null) && !Const.isEmpty(lookupTarget.getTargetStepname())) {
			List<StepMeta> steps = getTransMeta().getSteps();
			for (StepMeta step : steps) {
				if (step.getName().equals(lookupTarget.getTargetStepname())) {
					lookupTargetStep = step;
					break;
				}
			}
			// TODO: If not found then issue a warning?
		}
		// Get the fields added by this step
		meta.getFields(lookupIO.outputMeta, getStepname(), null, lookupTargetStep, this);
		// Retain a reference to the pass thru handler
		lookupIO.passThruMeta = stepData.getLookupPassThru();
		// Get the lookup pass thru fields
		if (!lookupIO.passThruMeta.selectIndexes(this, data, lookupIO)) {
			return false;
		}
		// Cache the position of the RowSet for the lookup output (if any)
		if (lookupTargetStep != null) {
			List<StreamInterface> targetStreams = meta.getStepIOMeta().getTargetStreams();
			for (StreamInterface targetStream : targetStreams) {
				if (lookupTargetStep.getName().equals(targetStream.getStepname())) {
					RowSet lookupTargetRowSet = findOutputRowSet(getStepname(), getCopy(), targetStream.getStepname(), 0);
					if (lookupTargetRowSet == null) {
						throw new KettleException(getErrorString("TargetStepInvalid", targetStream.getStepname()));
					}
					lookupIO.addTargetRow(lookupTargetRowSet, null);
					break;
				}
			}
		}
		// Process each row of lookup data
		while ((inputData != null) && !isStopped()) {
			if (!processRequest(inputData, meta, data, data.lookupIO)) {
				return false;
			}
			// Get next row of data
			inputData = getRowFrom(rowSet);
			reqsRead++;
		}
		return true;
	}

	private void processMatchUpOutput(MDCheckData data, OutputData outputData) {
		// Result codes
		StringBuffer rcBuffer = new StringBuffer();
		String       sep      = "";
		for (String rc : outputData.resultCodes) {
			rcBuffer.append(sep).append(rc);
			sep = ",";
		}
		outputData.data = RowDataUtil.addValueData(outputData.data, outputData.size++, rcBuffer.toString());
		// Dupe Group
		outputData.data = RowDataUtil.addValueData(outputData.data, outputData.size++, "" + outputData.dupeGroup);
		// Dupe Count
		outputData.data = RowDataUtil.addValueData(outputData.data, outputData.size++, "" + outputData.nonLookupCount);
		// Matchcode key
		outputData.data = RowDataUtil.addValueData(outputData.data, outputData.size++, outputData.key);
	}

	private void processQualityScores(MDCheckMeta meta, Evaluator evaluator, List<OutputData> lDupes, List<OutputData> finalOutput) throws KettleException {

		List<RankedRecord> arEvaluatedRecords = new ArrayList<RankedRecord>();
		String             expression         = "";
		List<OutputData>   removeList         = new ArrayList<OutputData>();
		List<QualityScore> qualityScores      = meta.getData().getMatchUp().getQualityScores();
		for (QualityScore qs : qualityScores) {
			if (qs.isSelected()) {
				expression = qs.getExpression();
				evaluator.setExpression(expression);
				evaluator.parseExpression();
				arEvaluatedRecords.clear();
				int dIndex = 0;
				removeList.clear();
				for (OutputData opd : lDupes) {
					try {
						if (opd.source == TYPE_SOURCE) {
							arEvaluatedRecords.add(new RankedRecord(opd.data, evaluator.evaluateExpression(opd.data), dIndex));
						} else {
							finalOutput.add(opd);
							removeList.add(opd);
						}
						dIndex++;
					} catch (EvaluatorException e) {
						throw new KettleException("EVALUATOR ERROR:" + e.getMessage());
					}
				}
				if (arEvaluatedRecords.size() > 0) {
					Collections.sort(arEvaluatedRecords, RankedRecord.NumericDecendingComparator);
				}
				for (RankedRecord raRec : arEvaluatedRecords) {
					if (arEvaluatedRecords.get(0).sortValue != raRec.sortValue) {
						int pos = lDupes.get(raRec.index).resultCodes.indexOf("MS02");
						if (pos >= 0) {
							lDupes.get(raRec.index).resultCodes.set(pos, "MS03");
						}
						finalOutput.add(0, lDupes.get(raRec.index));
						removeList.add(lDupes.get(raRec.index));
					}
				}
				// remove any records that have already been put in finalOutput
				for (OutputData rm : removeList) {
					lDupes.remove(rm);
				}
				if (lDupes.size() == 1) {
					break;
				}
			}
		}
	}

	/**
	 * This method is called to retrieve the pass thru data fields and store
	 * them in a temporary data file. They will be retrieved after matchup is
	 * complete when the output data is finally generated.
	 *
	 * @param inputData
	 * @param ioMeta
	 * @return
	 * @throws KettleException
	 */
	private long processRequestPassThru(Object[] inputData, IOMeta ioMeta) throws KettleException {

		try {
			// Allocate a temporary data array
			Object[] passThruData = new Object[ioMeta.passThruFieldNrs.length];
			// Get the passthru field values
			for (int i = 0; i < ioMeta.passThruFieldNrs.length; i++) {
				if ((ioMeta.passThruFieldNrs[i] < 0) || (ioMeta.passThruFieldNrs[i] >= inputData.length)) {
					passThruData[i] = null;
				} else {
					passThruData[i] = inputData[ioMeta.passThruFieldNrs[i]];
				}
			}
			// Get the current offset into the pass thru file.
			// This is the offset of the pass thru data in the cache file.
			long offset = passThruRAFile.getFilePointer();
			// Convert the data to a byte stream
			OutputStream output = new OutputStream() {
				@Override
				public void write(byte[] b) throws IOException {

					passThruRAFile.write(b);
				}

				@Override
				public void write(byte[] b, int off, int len) throws IOException {

					passThruRAFile.write(b, off, len);
				}

				@Override
				public void write(int b) throws IOException {

					passThruRAFile.write(b);
				}
			};
			ObjectOutputStream oos = new ObjectOutputStream(output);
			oos.writeObject(passThruData);
			oos.close();
			// Return the offset of the data in the cache file
			return offset;
		} catch (IOException e) {
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoWriteToPassThruFile"), e);
		}
	}

	/**
	 * At this point all the input data has been fed to the ReadWrite object. We
	 * therefore need to complete the matchup process and then write the results
	 * out.
	 */
	@Override
	protected boolean processComplete(MDCheckMeta meta, MDCheckData data) throws KettleException {// KettleException
		logDebug("MDCheckMatchup.processComplete");
		// Determine list processing behavior
		lookupBehavior = meta.getData().getMatchUp().getListSuppress() ? LookupBehavior.Suppress : LookupBehavior.Intersect;
		noPurge = meta.getData().getMatchUp().getNoPurge();
		isGoldenRecordSelection = doGRSelection(meta);
		IOMeta              ioMeta           = data.sourceIO;
		List<SurvivorField> lsSurvivorFields = meta.getData().getSourcePassThru().getSurvivorPassThru();
		List<String>        inputFields      = Arrays.asList(ioMeta.inputMeta.getFieldNames());
		// Just starting
		currentOutputRecord = -1;
		dupeBuffer = new ArrayList<DupeRecord>();
		// Start the processing
		// TODO: Interruptable?
		data.ReadWrite.Process();
		logDebug("MDCheckMatchup.processComplete: ReadWrite.Process returned");
		outputColumnNames = data.sourceIO.outputMeta.getFieldNames();
		if (meta.getData().getSourcePassThru().isSurvivorPass()) {
			int size = meta.getData().getSourcePassThru().getSurvivorPassThru().size();
			outputColumnNames = new String[size];
			for (int rr = 0; rr < size; rr++) {
				String tstSrc = meta.getData().getSourcePassThru().getSurvivorPassThru().get(rr).getSource();
				outputColumnNames[rr] = tstSrc;
			}
			// lookup map
			if (data.lookupIO != null) {
				lookUpColumns = data.lookupIO.outputMeta.getFieldNames();
			}
		}
		Evaluator grEvaluator = new Evaluator(/* columns */data.sourceIO.outputMeta, variableSpace);
//		inputValueMeta = data.sourceIO.inputMeta.getValueMetaList();
		outputValueMeta = data.sourceIO.outputMeta.getValueMetaList();
		// We leave out the !isStopped here because if we try to stop once we've entered this loop
		// we get an unhandled exception that we can't catch and handle on our own. Probably from JNI or dll
		// it gives no info other than unhandled exception and dies. When a transform is stopped by user
		// this allows the object to clean up properly befor delete is called. It still stops the output and
		// behaves as expected.
		// get first record to prime the pump
		getNextRecord(data);
		if (getRecordType(data) == TYPE_SOURCE) {
			outputDataHolder = getOurputRecord(data, data.sourceIO);
		} else {
			outputDataHolder = getOurputRecord(data, data.lookupIO);
		}
		List<OutputData> outputDataList = new ArrayList<OutputData>();
		while ((outputDataList = getOutputDataList(data)) != null) {
			if (meta.getData().getSourcePassThru().isSurvivorPass()) {
				OutputData odat = null;
				for (int outIdx = 0; outIdx < outputDataList.size(); outIdx++) {
					odat = outputDataList.get(outIdx);
					if (odat.source == TYPE_SOURCE) {
						for (int survivorIndex = 0; survivorIndex < lsSurvivorFields.size(); survivorIndex++) {
							SurvivorField currentField = lsSurvivorFields.get(survivorIndex);
							// If the source is not an input field we need to get a value
							if (!inputFields.contains(currentField.getSource())) {
								grEvaluator.setExpression(currentField.getSource());
								grEvaluator.parseExpression();
								try {
									EvalItem eit = grEvaluator.evaluateExpression(odat.data);
									odat.data[survivorIndex] = eit.value;
								} catch (EvaluatorException ex) {
									throw new KettleException("Unable to get field value for expression: " + currentField.getSource() + "  " + ex.getMessage());
								}
							}
						}
					} else {
						// Do something ?
					}
				}
			}
			if ((outputDataList.size() > 1) && isGoldenRecordSelection) {
				try {
					outputDataList = evaluateGoldenRecord(meta, grEvaluator, outputDataList);
					if (outputDataList == null) {
						/*
						 * Here is where we check for our exception that was saved by
						 * evaluateGoldenRecord.
						 */
						if (kettleException != null) {
							throw kettleException;
						}
					}
				} catch (EvaluatorException ee) {
					throw new KettleException(ee);
				}
			}
			if (meta.getData().getSourcePassThru().isSurvivorPass()) {
				try {
					outputDataList = evaluateSurvivorship(data, meta, grEvaluator, outputDataList);
				} catch (CloneNotSupportedException e) {
					// Shouldn't happen dcloning is implemented
				}
			}
			for (OutputData oData : outputDataList) {
				processMatchUpOutput(data, oData);
				// Figure out where this record is going to go:
				if (oData.source == TYPE_SOURCE) {
					Object[] outputData = oData.data;
					// Filter it and send it to the appropriate target step
					ioMeta = data.sourceIO;
					for (int i = 0; i < ioMeta.targetRowSets.size(); i++) {
						FilterTarget filter = ioMeta.filterTargets.get(i);
						boolean      valid  = isValidResult(filter, oData.resultCodes); // Check the results against the filter
						if (valid) {
							RowSet rowSet = ioMeta.targetRowSets.get(i);
							if (rowSet != null) {
								if (log.isRowLevel()) {
									logRowlevel("Sending row to :" + filter.getTargetStep().getName() + " : " + ioMeta.outputMeta.getString(outputData));
								}
								putRowTo(ioMeta.outputMeta, outputData, rowSet);
							}
							break;
						}
					}
				} else {
					// IOMeta ioMeta = data.lookupIO;
					ioMeta = data.lookupIO;
					// Is there a lookup target step?
					if ((ioMeta.targetRowSets != null) && (ioMeta.targetRowSets.size() > 0)) {
						RowSet   rowSet     = ioMeta.targetRowSets.get(0);
						Object[] outputData = oData.data;
						// Send it to the appropriate target step
						if (rowSet != null) {
							if (log.isRowLevel()) {
								logRowlevel("Sending row to :" + rowSet.getName() + " : " + ioMeta.outputMeta.getString(outputData));
							}
							putRowTo(ioMeta.outputMeta, outputData, rowSet);
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * The standard request processing model does not work for Matchup. The
	 * standard model processes batches of requests and immediately outputs
	 * their results. Matchup requires loading the requests into a database,
	 * triggering a processing operation, and then reading the result records
	 * and storing them back out into their appropriate targets.
	 *
	 * @throws KettleException
	 */
	@Override
	protected boolean processRequest(Object[] inputData, MDCheckMeta meta, MDCheckData data, IOMeta ioMeta) throws KettleException {

		if (ioMeta == null) {
			logError(getErrorString("NoInput"));
			return false;
		}
		if (inputData != null) {
			// Cache the pass thru data for subsequent retrieval
			long          offset    = processRequestPassThru(inputData, ioMeta);
			mdMUReadWrite ReadWrite = data.ReadWrite;
			mdMUHybrid    Hybrid    = data.Hybrid;
			// Create the matchcode key for source data
			int recordType;
			if (ioMeta == data.sourceIO) {
				recordType = TYPE_SOURCE;
				// The ReadWrite object creates its own keys for source data
				ReadWrite.ClearFields();
				List<MapField> sourceMapping = meta.getData().getMatchUp().getSourceMapping();
				for (MapField field : sourceMapping) {
					String value = MDCheck.getFieldString(ioMeta.inputMeta, inputData, field.input);
					if (value == null) {
						value = "";
					}
					ReadWrite.AddField(value);
				}
				ReadWrite.BuildKey();
				// Otherwise create the matchcode key for lookup data
			} else {
				recordType = TYPE_LOOKUP;
				// We use the Hybrid object to create keys for the lookup data
				Hybrid.ClearFields();
				List<MapField> lookupMapping = meta.getData().getMatchUp().getLookupMapping();
				for (MapField field : lookupMapping) {
					String value = MDCheck.getFieldString(ioMeta.inputMeta, inputData, field.input);
					if (value == null) {
						value = "";
					}
					Hybrid.AddField(value);
				}
				Hybrid.BuildKey();
				ReadWrite.SetKey(Hybrid.GetKey());
			}
			// Store the passthru offset and write the key to the deduper:
			ReadWrite.SetUserInfo(recordType + "," + offset);
			ReadWrite.WriteRecord();
		}
		return true;
	}
}
