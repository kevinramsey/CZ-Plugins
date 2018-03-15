package com.melissadata.kettle;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import com.melissadata.kettle.support.FilterTarget;
import com.melissadata.kettle.mu.LookupTarget;
import com.melissadata.kettle.mu.MDCheckMatchup;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepIOMeta;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.w3c.dom.Node;

public abstract class MDCheckMeta extends BaseStepMeta implements StepMetaInterface {

	/**
	 * @param row
	 * @param originName
	 * @param space
	 * @param fieldValue
	 * @param fieldLength
	 */
	public static void getStringField(RowMetaInterface row, String originName, VariableSpace space, String fieldValue, int fieldLength) {
		// Don't add it if it is blank
		if (fieldValue.trim().length() == 0) {
			return;
		}
		// Build meta description of field
		ValueMetaInterface vq = row.getValueMeta(0);
		if (vq != null) {

		}
		ValueMetaInterface v = new ValueMeta(space.environmentSubstitute(fieldValue), ValueMetaInterface.TYPE_STRING);
		v.setOrigin(originName);
		v.setLength(fieldLength);
		row.addValueMeta(v);
	}

	public static boolean isSpoon() {

		return isSpoon;
	}

	public static void setSpoon(boolean isSpoon) {

		MDCheckMeta.isSpoon = isSpoon;
	}

	public static boolean isPentahoPlugin() {

		return isPentahoPlugin;
	}

	public static void setPentahoPlugin(boolean isPentahoPlugin) {

		MDCheckMeta.isPentahoPlugin = isPentahoPlugin;
	}

	private static      Class<?>        PKG                       = MDCheckMeta.class;
	public static final int             MDCHECK_NAME              = 0x01;
	public static final int             MDCHECK_ADDRESS           = 0x02;
	public static final int             MDCHECK_PHONE             = 0x04;
	public static final int             MDCHECK_EMAIL             = 0x08;
	public static final int             MDCHECK_FULL              = MDCHECK_NAME | MDCHECK_ADDRESS | MDCHECK_PHONE | MDCHECK_EMAIL;
	public static final int             MDCHECK_SMARTMOVER        = 0x10;
	// report stats
	public static final int             MDCHECK_MATCHUP           = 0x20;
	public static final int             MDCHECK_IPLOCATOR         = 0x40;
	public static final int             MDCHECK_MATCHUP_GLOBAL    = 0x80;
	public static final String          NOT_DEFINED               = BaseMessages.getString(PKG, "MDCheckMeta.InputData.NotDefined");
	public static       String          MDCHECK_REPORT_ARRAY      = "MDCheckMeta.ReportData.";
	public              String[]        geoOverviewReportCStat    = null;
	public              String[]        nameOverviewFields        = null;
	public              String[]        nameFormulaFields         = null;
	public              String[]        emailFormulaFields        = null;
	public              String[]        emailOverviewReportCStat  = null;
	public              String[]        phoneFormulaFields        = null;
	public              String[]        phoneOverviewReportCStat  = null;
	public              String[]        addrValidationReportCStat = null;
	public              String[]        addrOverviewReportCStat   = null;
	public              String[]        addrChangeReportCStat     = null;
	public              String[]        errorReportCStat          = null;
	private             MDCheckStepData data                      = null;
	private static      boolean         isSpoon                   = false;
	private static      boolean         isPentahoPlugin           = true;
	public static boolean isTest = false;

	/**
	 * @param checkTypes The types of checks we are running
	 * @throws KettleException
	 */
	public MDCheckMeta(int checkTypes) throws KettleException {

		if (Props.isInitialized()) {
			if (Props.getInstance().getType() == 1) {
				isSpoon = true;
			}
		}
		// This holds the actual data of the step
		data = new MDCheckStepData(checkTypes, this);
		addrOverviewReportCStat = getReportArray("Addr", "Overview");
		geoOverviewReportCStat = getReportArray("Geo", "Overview");
		nameOverviewFields = getReportArray("Name", "Overview");
		emailOverviewReportCStat = getReportArray("Email", "Overview");
		phoneOverviewReportCStat = getReportArray("Phone", "Overview");
		nameFormulaFields = getReportArray("Name", "Formula");
		emailFormulaFields = getReportArray("Email", "Formula");
		phoneFormulaFields = getReportArray("Phone", "Formula");
		addrValidationReportCStat = getReportArray("Addr", "Validation");
		addrChangeReportCStat = getReportArray("Addr", "Changes");
		errorReportCStat = getReportArray("Addr", "Errors");
	}


	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#check(java.util.List, org.pentaho.di.trans.TransMeta,
	 * org.pentaho.di.trans.step.StepMeta, org.pentaho.di.core.row.RowMetaInterface, java.lang.String[], java.lang.String[],
	 * org.pentaho.di.core.row.RowMetaInterface)
	 */
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// See if we have an input stream leading to this step!
		if (input.length == 0) {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, getCheckString("NoInputReceivedError"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		} else {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, getCheckString("InputOK"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		}
		// Check the data
		data.check(remarks, transMeta, stepMeta, prev, input, output, info);
		// Check target steps
		List<StreamInterface> targetStreams = getStepIOMeta().getTargetStreams();
		StreamInterface       validTarget   = targetStreams.get(0);
		StreamInterface       invalidTarget = null;
		if (targetStreams.size() > 1) {
			invalidTarget = targetStreams.get(1);
		}
		if ((validTarget.getStepname() != null) && (invalidTarget != null) && (invalidTarget.getStepname() != null)) {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, getCheckString("BothValidAndInvalidStepSpecified"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		} else if ((validTarget.getStepname() == null) && (invalidTarget != null) && (invalidTarget.getStepname() == null)) {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, getCheckString("NeitherValidAndInvalidStepSpecified"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		} else {
			CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, getCheckString("PlsSpecifyBothValidAndInvalidStep"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		}
		if (validTarget.getStepname() != null) {
			int targetIdx = Const.indexOfString(validTarget.getStepname(), output);
			if (targetIdx < 0) {
				CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, getCheckString("TargetStepInvalid", "valid", validTarget.getStepname()), stepMeta);
				remarks.add(cr);
			}
		}
		if ((invalidTarget != null) && (invalidTarget.getStepname() != null)) {
			int targetIdx = Const.indexOfString(invalidTarget.getStepname(), output);
			if (targetIdx < 0) {
				CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, getCheckString("TargetStepInvalid", "invalid", invalidTarget.getStepname()), stepMeta);
				remarks.add(cr);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#excludeFromCopyDistributeVerification()
	 */
	@Override
	public boolean excludeFromCopyDistributeVerification() {

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#excludeFromRowLayoutVerification()
	 */
	@Override
	public boolean excludeFromRowLayoutVerification() {
		// If doing matchup then disable the row layout checking code. This code normally causes
		// a complaint when two inputs streams to a step have different row layouts. This can be
		// problem when hooking up the lookup stream since it will likely be a different format
		// yet it hasn't yet been configured as the lookup info step.
		if (data.getMatchUp() != null) {
			return true;
		}
		// Call parent handler instead
		return super.excludeFromRowLayoutVerification();
	}

	/**
	 * Used by MDCheckDialog to gain access to the step's data
	 *
	 * @return the data for this step
	 */
	public MDCheckStepData getData() {

		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getFields(org.pentaho.di.core.row.RowMetaInterface, java.lang.String,
	 * org.pentaho.di.core.row.RowMetaInterface[], org.pentaho.di.trans.step.StepMeta,
	 * org.pentaho.di.core.variables.VariableSpace)
	 */
	@Override
	public void getFields(RowMetaInterface row, String originName, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		// Get fields defined by the data.
		// This must match the order in which fields are added in the processing step
		data.getFields(row, originName, nextStep, space);
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getOptionalStreams()
	 */
	@Override
	public List<StreamInterface> getOptionalStreams() {

		return data.getOutputFilter().getOptionalStreams();
	}

	public String[] getReportArray(String category, String prefix) {

		SortedMap<Integer, String> rcs    = new TreeMap<Integer, String>();
		ResourceBundle             bundle = GlobalMessages.getBundle(PKG.getPackage().getName() + ".messages.messages", PKG);
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String rc             = keys.nextElement();
				String categoryPrefix = MDCHECK_REPORT_ARRAY + category + ".";
				if (rc.startsWith(categoryPrefix + prefix)) {
					rc = rc.substring(categoryPrefix.length());
					int i = rc.indexOf(".");
					if (i != -1) {
						rc = rc.substring(0, i);
					}
					Integer newInt = new Integer(rc.substring(rc.length() - 1));
					newInt = newInt - 1;
					String msg = BaseMessages.getString(PKG, categoryPrefix + rc);
					rcs.put(newInt, msg);
				}
			}
		}
		String[] returnArray = new String[rcs.size()];
		for (Integer i : rcs.keySet()) {
			returnArray[i] = rcs.get(i);
		}
		return returnArray;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStep(org.pentaho.di.trans.step.StepMeta,
	 * org.pentaho.di.trans.step.StepDataInterface, int, org.pentaho.di.trans.TransMeta, org.pentaho.di.trans.Trans)
	 */
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		// We use a different processing path for Matchup
		if (getData().getMatchUp() != null) {
			return new MDCheckMatchup(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		} else {
			return new MDCheck(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#getStepData()
	 */
	//@Override
	public StepDataInterface getStepData() {

		return new MDCheckData();
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getStepIOMeta()
	 */
	@Override
	public StepIOMetaInterface getStepIOMeta() {

		if (ioMeta == null) {
			ioMeta = new StepIOMeta(true, false, false, false, false, true);
			// Add filter targets
			data.getOutputFilter().getStepIOMeta(ioMeta);
			// If matchup then we need to add the (optional) lookup info source
			if (data.getMatchUp() != null) {
				data.getMatchUp().getStepIOMeta(ioMeta);
			}
		}
		return ioMeta;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#getXML()
	 */
	@Override
	public String getXML() throws KettleException {
		// Save data to XML
		StringBuilder retval = new StringBuilder(200);
		retval.append(data.getXML());
		return retval.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pentaho.di.trans.step.BaseStepMeta#handleStreamSelection(org.pentaho.di.trans.step.errorhandling.StreamInterface)
	 */
	@Override
	public void handleStreamSelection(StreamInterface stream) {

		data.getOutputFilter().handleStreamSelection(stream);
		List<StreamInterface> targetStreams = getStepIOMeta().getTargetStreams();
		for (int i = 0; i < targetStreams.size(); i++) {
			if (stream == targetStreams.get(i)) {
				if (stream.getSubject() instanceof FilterTarget) {
					FilterTarget target = (FilterTarget) stream.getSubject();
					target.setTargetStep(stream.getStepMeta());
				} else if (stream.getSubject() instanceof LookupTarget) {
					LookupTarget target = (LookupTarget) stream.getSubject();
					target.setTargetStep(stream.getStepMeta());
				}
			}
		}
		resetStepIoMeta(); // force stepIo to be recreated when it is next needed.
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#loadXML(org.w3c.dom.Node, java.util.List, java.util.Map)
	 */
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {

		readData(stepnode);
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#readRep(org.pentaho.di.repository.Repository,
	 * org.pentaho.di.repository.ObjectId, java.util.List, java.util.Map)
	 */
	@Override
	public void readRep(Repository rep, ObjectId idStep, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {

		try {
			// Initialize data from repository
			data.readRep(rep, idStep);
		} catch (KettleException e) {
			throw e;
		} catch (Exception e) {
			throw new KettleException(getExceptionString("UnexpectedErrorInReadingStepInfoFromRepository"), e); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#saveRep(org.pentaho.di.repository.Repository,
	 * org.pentaho.di.repository.ObjectId, org.pentaho.di.repository.ObjectId)
	 */
	@Override
	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {

		try {
			// Save data to repository
			data.saveRep(rep, idTransformation, idStep);
		} catch (KettleException e) {
			throw e;
		} catch (Exception e) {
			throw new KettleException(getExceptionString("UnableToSaveStepInfoToRepository") + idStep, e); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#searchInfoAndTargetSteps(java.util.List)
	 */
	@Override
	public void searchInfoAndTargetSteps(List<StepMeta> steps) {
		// De-reference the filter target steps
		for (StreamInterface stream : getStepIOMeta().getTargetStreams()) {
			if (stream.getSubject() instanceof FilterTarget) {
				FilterTarget target   = (FilterTarget) stream.getSubject();
				StepMeta     stepMeta = StepMeta.findStep(steps, target.getTargetStepname());
				target.setTargetStep(stepMeta);
			} else if (stream.getSubject() instanceof LookupTarget) {
				LookupTarget target   = (LookupTarget) stream.getSubject();
				StepMeta     stepMeta = StepMeta.findStep(steps, target.getTargetStepname());
				target.setTargetStep(stepMeta);
			}
		}
		// De-reference info steps (e.g. lookup step for matchup
		for (StreamInterface stream : getStepIOMeta().getInfoStreams()) {
			stream.setStepMeta(StepMeta.findStep(steps, (String) stream.getSubject()));
		}
		resetStepIoMeta();
	}

	/**
	 * Used by MDCheckDialog to update the step's data
	 *
	 * @param data The new data
	 */
	public void setData(MDCheckStepData data) {

		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.StepMetaInterface#setDefault()
	 */
	public void setDefault() {
		// Set data defaults
		try {
			data.setDefault();
		} catch (KettleException e) {
			// TODO Handel this or ignore ?
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pentaho.di.trans.step.BaseStepMeta#supportsErrorHandling()
	 */
	@Override
	public boolean supportsErrorHandling() {

		return true;
	}

	/**
	 * @param name
	 * @param args
	 * @return
	 */
	private String getCheckString(String name, String... args) {

		return BaseMessages.getString(PKG, "MDCheckMeta.CheckResult." + name, args);
	}

	/**
	 * @param name
	 * @return
	 */
	private String getExceptionString(String name) {

		return BaseMessages.getString(PKG, "MDCheckMeta.Exception." + name); //$NON-NLS-1$
	}

	/**
	 * Called to read meta data from Document node
	 *
	 * @param node
	 * @throws KettleXMLException
	 */
	private void readData(Node node) throws KettleXMLException {

		try {
			// Initialize data from node
			data.readData(node);
		} catch (KettleXMLException e) {
			throw e;
		} catch (Exception e) {
			throw new KettleXMLException(getExceptionString("UnexpectedErrorInReadingStepInfoFromRepository"), e); //$NON-NLS-1$
		}
	}
}
