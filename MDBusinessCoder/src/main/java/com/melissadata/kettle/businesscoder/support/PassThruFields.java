package com.melissadata.kettle.businesscoder.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;

import com.melissadata.kettle.businesscoder.MDBusinessCoderData;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.MDBusinessCoderStep;
import com.melissadata.cz.support.IOMetaHandler;

public class PassThruFields {

	private static Class<?>					PKG							= MDBusinessCoderMeta.class;

	public static final String				TAG_BUSINESS_CODER_FILTER	= "business_coder_filter";

	public static final String				TAG_PASS_THROUGH			= "pass_thru";
	public static final String				TAG_FILTER_OUT				= "filter_out";

	private List<String>					filterOut					= new ArrayList<String>();
	private List<String>					passThru					= new ArrayList<String>();

	public HashMap<String, List<String>>	passFilterFields			= new HashMap<String, List<String>>();

	/**
	 * Checks the settings of this step and puts the findings in a remarks List.
	 *
	 * @param remarks
	 *            The list to put the remarks in @see org.pentaho.di.core.CheckResult
	 * @param stepMeta
	 *            The stepMeta to help checking
	 * @param prev
	 *            The fields coming from the previous step
	 * @param input
	 *            The input step names
	 * @param output
	 *            The output step names
	 * @param info
	 *            The fields that are used as information by the step
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {

		CheckResult cr;

		if ((prev != null) && (prev.size() > 0)) {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "MDBusinessCoderMeta.CheckResult.StepReceivingFields", prev.size() + ""), stepMeta); //$NON-NLS-1$ //$NON-NLS-2$
			remarks.add(cr);

			// Search for missing fields
			String error_message = ""; //$NON-NLS-1$
			boolean error_found = false;

			// Search for filterOut fields in input stream
			for (String field : filterOut) {
				int idx = prev.indexOfValue(field);
				if (idx < 0) {
					error_message += "\t\t" + field + Const.CR; //$NON-NLS-1$
					error_found = true;
				}
			}
			if (error_found) {
				error_message = BaseMessages.getString(PKG, "MDBusinessCoderMeta.CheckResult.FilterOutFieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$

				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepMeta);
				remarks.add(cr);
			} else {
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "MDBusinessMeta.CheckResult.AllFilterOutFieldsFound"), stepMeta); //$NON-NLS-1$
				remarks.add(cr);
			}

			if (filterOut.size() > 0) {
				// Starting from prev...
				for (int i = 0; i < prev.size(); i++) {
					ValueMetaInterface pv = prev.getValueMeta(i);
					if (!filterOut.contains(pv.getName())) {
						error_message += "\t\t" + pv.getName() + " (" + pv.getTypeDesc() + ")" + Const.CR; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						error_found = true;
					}
				}
				if (error_found) {
					error_message = BaseMessages.getString(PKG, "MDBusinessCoderMeta.CheckResult.FieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$

					cr = new CheckResult(CheckResultInterface.TYPE_RESULT_COMMENT, error_message, stepMeta);
					remarks.add(cr);
				}
			}

			// Search for passThru fields in input stream
			error_message = ""; //$NON-NLS-1$
			error_found = false;

			for (String field : passThru) {
				int idx = prev.indexOfValue(field);
				if (idx < 0) {
					error_message += "\t\t" + field + Const.CR; //$NON-NLS-1$
					error_found = true;
				}
			}
			if (error_found) {
				error_message = BaseMessages.getString(PKG, "MDBusinessCoderMeta.CheckResult.PassThruFieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$

				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepMeta);
				remarks.add(cr);
			} else {
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "MDBusinessCoderMeta.CheckResult.AllFilterOutFieldsFound"), stepMeta); //$NON-NLS-1$
				remarks.add(cr);
			}

			if (passThru.size() > 0) {
				// Starting from prev...
				for (int i = 0; i < prev.size(); i++) {
					ValueMetaInterface pv = prev.getValueMeta(i);
					if (!passThru.contains(pv.getName())) {
						error_message += "\t\t" + pv.getName() + " (" + pv.getTypeDesc() + ")" + Const.CR; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						error_found = true;
					}
				}
				if (error_found) {
					error_message = BaseMessages.getString(PKG, "MDBusinessCoderMeta.CheckResult.FieldsNotFound") + Const.CR + Const.CR + error_message; //$NON-NLS-1$

					cr = new CheckResult(CheckResultInterface.TYPE_RESULT_COMMENT, error_message, stepMeta);
					remarks.add(cr);
				}
			}
		} else {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "MDBusinessCoderMeta.CheckResult.FieldsNotFound2"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		}
	}

	public void init() {

		passFilterFields.put(TAG_PASS_THROUGH, passThru);
		passFilterFields.put(TAG_FILTER_OUT, filterOut);
	}

	/**
	 * Called during processing before the first row. It will lookup the index of the pass thru fields and
	 * detect any problems.
	 * 
	 * @param ioMeta
	 *
	 * @return
	 */
	public boolean selectIndexes(MDBusinessCoderStep step, MDBusinessCoderData data, IOMetaHandler ioMeta) {

		step.logDebug("Selecting " + passThru.size() + " pass thru fields");

		// Find the indexes of the selected fields in the source row.
		int[] passThruFieldNrs = ioMeta.passThruFieldNrs = new int[passThru.size()];
		for (int i = 0; i < passThruFieldNrs.length; i++) {
			passThruFieldNrs[i] = ioMeta.inputMeta.indexOfValue(passThru.get(i));
			if (passThruFieldNrs[i] < 0) {
				step.logError(BaseMessages.getString(PKG, "MDBusinessCoder.Log.CouldNotFindField", passThru.get(i))); //$NON-NLS-1$
				step.setErrors(1);
				step.stopAll();
				return false;
			}
		}

		return true;
	}

}
