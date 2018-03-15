package com.melissadata.kettle;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.melissadata.kettle.iplocator.IPLocatorMeta;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.address.AddressVerifyMeta;
import com.melissadata.kettle.cv.address.RBDIndicatorMeta;
import com.melissadata.kettle.cv.email.EmailVerifyMeta;
import com.melissadata.kettle.fieldmapping.FieldMapping;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta;
import com.melissadata.kettle.cv.name.NameParseMeta;
import com.melissadata.kettle.cv.phone.PhoneVerifyMeta;
import com.melissadata.kettle.versioncheck.VersionsMeta;
import com.melissadata.kettle.mu.LookupTarget;
import com.melissadata.kettle.mu.MatchUpMeta;
import com.melissadata.kettle.report.ReportingMeta;
import com.melissadata.kettle.sm.SmartMoverMeta;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Node;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta.AddrKeySource;
import com.melissadata.kettle.MDSettings.AdvancedConfigInterface;

/**
 * Holds all the data managed by the steps
 */
public class MDCheckStepData implements Cloneable {

	/**
	 * Wraps the XMLHandler.addTagValue(String, String) method.
	 * If the value is a zero-length string then it will be padded to one character. This is done so that the
	 * generated XML will have some value instead of the null that is currenly returned when it is read back in.
	 *
	 * @param tag
	 * @param val
	 * @return
	 */
	public static String addTagValue(String tag, String val) {

		if ((val != null) && (val.length() == 0)) {
			val = " ";
		}
		return XMLHandler.addTagValue(tag, val);
	}

	/**
	 * Warps the XMLHandler.getTagValue(Node, String) method.
	 * Reverses the special handling done in the above addTagValue() method by triming values before returning them.
	 *
	 * @param n
	 * @param tag
	 * @return
	 */
	public static String getTagValue(Node n, String tag) {
		String value = XMLHandler.getTagValue(n, tag);
		if (value != null) {
			value = value.trim();
		}
		return value;
	}

	/**
	 * @param value
	 * @return empty string if null
	 */
	public static String safe(String value, String def) {

		return (value != null) ? value : def;
	}

	private static final String                    MDCHECK_RESULT_CODE    = "MDCheck.ResultCode.";
	private static       Class<?>                  PKG                    = MDCheckStepData.class;
	public               int                       checkTypes             = 0;
	private              MDCheckMeta               meta                   = null;
	private              AdvancedConfigurationMeta advConfMeta            = null;
	private              NameParseMeta             nameParseMeta          = null;
	private              AddressVerifyMeta         addrVerifyMeta         = null;
	private              GeoCoderMeta              geoCoderMeta           = null;
	private              RBDIndicatorMeta          rbdiMeta               = null;
	private              PhoneVerifyMeta           phoneVerifyMeta        = null;
	private              EmailVerifyMeta           emailVerifyMeta        = null;
	private              SmartMoverMeta            smartMoverMeta         = null;
	private              MatchUpMeta               matchUpMeta            = null;
	private              IPLocatorMeta             ipLocatorMeta          = null;
	private              PassThruMeta              sourcePassThruMeta     = null;
	private              PassThruMeta              lookupPassThruMeta     = null;
	private              OutputFilterMeta          outputFilterMeta       = null;
	private              ReportingMeta             reportMeta             = null;
	private              VersionsMeta              versionMeta            = null;
	private              AdvancedConfigInterface   mdInterface            = null;
	private              FieldMapping              fieldMapping           = null;
	// Info initialized during processing
	public static        String                    myProducts             = "";
	public               String                    localLicenseVersion    = "";
	public               String                    localLicenseExpiration = "";
	public               String                    localLicenseBuildNo    = "";
	public static        String                    webInitMsg             = "";
	public               KettleException           webInitException       = null;

	public MDCheckStepData(int checkTypes, MDCheckMeta meta) throws KettleException {

		this.checkTypes = checkTypes;
		this.meta = meta;
		// Allocate the sub-step meta structures. Some are optional.
		advConfMeta = new AdvancedConfigurationMeta(this);
		if ((checkTypes & MDCheckMeta.MDCHECK_NAME) != 0) {
			nameParseMeta = new NameParseMeta(this);
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_ADDRESS) != 0) {
			addrVerifyMeta = new AddressVerifyMeta(this);
			geoCoderMeta = new GeoCoderMeta(this);
			rbdiMeta = new RBDIndicatorMeta(this);
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_PHONE) != 0) {
			phoneVerifyMeta = new PhoneVerifyMeta(this);
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_EMAIL) != 0) {
			emailVerifyMeta = new EmailVerifyMeta(this);
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			smartMoverMeta = new SmartMoverMeta();
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			matchUpMeta = new MatchUpMeta(this);
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
			matchUpMeta = new MatchUpMeta(this);
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			ipLocatorMeta = new IPLocatorMeta(this);
		}
		sourcePassThruMeta = new PassThruMeta("pass_thru");
		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			lookupPassThruMeta = new PassThruMeta("lookup_pass_thru");
		}
		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
			lookupPassThruMeta = new PassThruMeta("lookup_pass_thru");
		}
		outputFilterMeta = new OutputFilterMeta(checkTypes, (checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0);
		reportMeta = new ReportingMeta(this);
		versionMeta = new VersionsMeta(this);
		fieldMapping = new FieldMapping(this);
		mdInterface = new AdvancedConfigInterface();
		mdInterface.setDataValues();
		setDefault();
	}

	/**
	 * Called to check the validity of the data
	 *
	 * @param remarks
	 * @param transMeta
	 * @param stepMeta
	 * @param prev
	 * @param input
	 * @param output
	 * @param info
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// Call sub-steps
		advConfMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		sourcePassThruMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		if (lookupPassThruMeta != null) {
			lookupPassThruMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (nameParseMeta != null) {
			nameParseMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (addrVerifyMeta != null) {
			addrVerifyMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (geoCoderMeta != null) {
			geoCoderMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (phoneVerifyMeta != null) {
			phoneVerifyMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (emailVerifyMeta != null) {
			emailVerifyMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (smartMoverMeta != null) {
			smartMoverMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (matchUpMeta != null) {
			matchUpMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		if (ipLocatorMeta != null) {
			ipLocatorMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		}
		outputFilterMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
		reportMeta.check(remarks, transMeta, stepMeta, prev, input, output, info);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MDCheckStepData clone() {

		try {
			MDCheckStepData data = (MDCheckStepData) super.clone();
			data.advConfMeta = data.advConfMeta.clone();
			data.advConfMeta.setData(data);
			if (data.nameParseMeta != null) {
				data.nameParseMeta = data.nameParseMeta.clone();
				data.nameParseMeta.setData(data);
			}
			if (data.addrVerifyMeta != null) {
				data.addrVerifyMeta = data.addrVerifyMeta.clone();
				data.addrVerifyMeta.setData(data);
			}
			if (data.geoCoderMeta != null) {
				data.geoCoderMeta = data.geoCoderMeta.clone();
				data.geoCoderMeta.setData(data);
			}
			if (data.phoneVerifyMeta != null) {
				data.phoneVerifyMeta = data.phoneVerifyMeta.clone();
				data.phoneVerifyMeta.setData(data);
			}
			if (data.emailVerifyMeta != null) {
				data.emailVerifyMeta = data.emailVerifyMeta.clone();
				data.emailVerifyMeta.setData(data);
			}
			if (data.smartMoverMeta != null) {
				data.smartMoverMeta = data.smartMoverMeta.clone();
			}
			if (data.matchUpMeta != null) {
				data.matchUpMeta = data.matchUpMeta.clone();
				data.matchUpMeta.setData(data);
			}
			if (data.ipLocatorMeta != null) {
				data.ipLocatorMeta = data.ipLocatorMeta.clone();
				data.ipLocatorMeta.setData(data);
			}
			data.sourcePassThruMeta = data.sourcePassThruMeta.clone();
			if (data.lookupPassThruMeta != null) {
				data.lookupPassThruMeta = data.lookupPassThruMeta.clone();
			}
			data.outputFilterMeta = data.outputFilterMeta.clone();
			data.reportMeta = data.reportMeta.clone();
			data.reportMeta.setData(data);
			return data;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected clone problem", e);
		}
	}

	/**
	 * Called to retrieve the address verifier meta data
	 *
	 * @return
	 */
	public AddressVerifyMeta getAddressVerify() {

		return addrVerifyMeta;
	}

	/**
	 * Called to retrieve the advanced configuration meta data
	 *
	 * @return
	 */
	public AdvancedConfigurationMeta getAdvancedConfiguration() {

		return advConfMeta;
	}

	/**
	 * @return The check types configuration flags
	 */
	public int getCheckTypes() {

		return checkTypes;
	}

	public String getProductCode() {

		String pCode = "";
//		if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
//			pCode = MDPropTags.MDLICENSE_PRODUCT_MatchUpObject;
//		} else if ((checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0) {
//			pCode = MDPropTags.MDLICENSE_PRODUCT_MatchUpIntl;
//		} else if ((checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
//			pCode = MDPropTags.MDLICENSE_PRODUCT_IpLocatorObject;
//		} else if ((checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
//			pCode = MDPropTags.MDLICENSE_PRODUCT_SmartMover;
//		} else if ((checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
//			/* There is no Product code for full.  we will just
//			*  use address productCode
//			*/
//			pCode = MDPropTags.MDLICENSE_PRODUCT_AddressObject;
//		}

		return pCode;
	}

	;

	/**
	 * Called to retrieve the email verifier meta data
	 *
	 * @return
	 */
	public EmailVerifyMeta getEmailVerify() {

		return emailVerifyMeta;
	}

	public FieldMapping getFieldMapping() {

		return fieldMapping;
	}

	/**
	 * Called to get the output fields defined by the data
	 *
	 * @param row
	 * @param originName
	 * @param nextStep
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, StepMeta nextStep, VariableSpace space) {
		// Special processing for matchup
		if (matchUpMeta != null) {
			// Different pass thru fields depending on target type
			LookupTarget lookupTarget = getOutputFilter().getLookupTarget();
			if ((lookupTarget != null) && !Const.isEmpty(lookupTarget.getTargetStepname()) && (nextStep != null) && lookupTarget.getTargetStepname().equals(nextStep.getName())) {
				// Add lookup pass thru fields first
				lookupPassThruMeta.getFields(row, originName, space, true);
			} else {
				// Add source pass thru fields first
				sourcePassThruMeta.getFields(row, originName, space, false);
			}
			// Add matchup fields
			matchUpMeta.getFields(row, originName, space);
		} else {
			// Add pass thru fields first
			sourcePassThruMeta.getFields(row, originName, space, false);
			if (nameParseMeta != null) {
				nameParseMeta.getFields(row, originName, space);
			}
			if (addrVerifyMeta != null) {
				addrVerifyMeta.getFields(row, originName, space);
			}
			if (geoCoderMeta != null) {
				geoCoderMeta.getFields(row, originName, space);
			}
			if (phoneVerifyMeta != null) {
				phoneVerifyMeta.getFields(row, originName, space);
			}
			if (emailVerifyMeta != null) {
				emailVerifyMeta.getFields(row, originName, space);
			}
			if (smartMoverMeta != null) {
				smartMoverMeta.getFields(row, originName, space);
			}
			if (ipLocatorMeta != null) {
				ipLocatorMeta.getFields(row, originName, space);
			}
			outputFilterMeta.getFields(row, originName, space);
		}
	}

	/**
	 * Called to retrieve the GeoCoder meta data
	 *
	 * @return
	 */
	public GeoCoderMeta getGeoCoder() {

		return geoCoderMeta;
	}

	/**
	 * Called to retrieve the IPLocator meta data
	 *
	 * @return
	 */
	public IPLocatorMeta getIPLocator() {

		return ipLocatorMeta;
	}

	/**
	 * Called to retrueve the lookup pass thru meta data
	 *
	 * @return
	 */
	public PassThruMeta getLookupPassThru() {

		return lookupPassThruMeta;
	}

	/**
	 * Called to retrieve the matchup meta data
	 *
	 * @return
	 */
	public MatchUpMeta getMatchUp() {

		return matchUpMeta;
	}

	public AdvancedConfigInterface getMdInterface() {

		return mdInterface;
	}

	public MDCheckMeta getMeta() {

		return meta;
	}

	/**
	 * Called to retrieve the name parse meta data
	 *
	 * @return
	 */
	public NameParseMeta getNameParse() {

		return nameParseMeta;
	}

	/**
	 * Called to retrieve the output filter meta data
	 *
	 * @return
	 */
	public OutputFilterMeta getOutputFilter() {

		return outputFilterMeta;
	}

	/**
	 * Called to retrieve the phone verifier meta data
	 *
	 * @return
	 */
	public PhoneVerifyMeta getPhoneVerify() {

		return phoneVerifyMeta;
	}

	/**
	 * Called to retrieve the RBDI meta data
	 *
	 * @return
	 */
	public RBDIndicatorMeta getRBDIndicator() {

		return rbdiMeta;
	}

	public ReportingMeta getReportMeta() {

		return reportMeta;
	}

	/*
	 * Returns the defined error codes that begin with the given prefix and their description
	 */
	public HashMap<String, String> getResultCodes(String category, String prefix) {

		HashMap<String, String> rcs    = new HashMap<String, String>();
		ResourceBundle          bundle = GlobalMessages.getBundle(PKG.getPackage().getName() + ".messages.messages", PKG);
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String rc             = keys.nextElement();
				String categoryPrefix = MDCHECK_RESULT_CODE + category + ".";
				if (rc.startsWith(categoryPrefix + prefix)) {
					rc = rc.substring(categoryPrefix.length());
					int i = rc.indexOf(".");
					if (i != -1) {
						rc = rc.substring(0, i);
					}
					String msg = BaseMessages.getString(PKG, categoryPrefix + rc);
					rcs.put(rc, msg);
				}
			}
		}
		return rcs;
	}

	// public void setVersionsMeta(VersionsMeta vrMeta){
	// this.versionMeta = vrMeta;
	// }

	/**
	 * Called to retrieve the smart mover meta data
	 *
	 * @return
	 */
	public SmartMoverMeta getSmartMover() {

		return smartMoverMeta;
	}

	/**
	 * Called to retrueve the source pass thru meta data
	 *
	 * @return
	 */
	public PassThruMeta getSourcePassThru() {

		return sourcePassThruMeta;
	}

	public VersionsMeta getVersionsMeta() {

		return versionMeta;
	}

	/**
	 * Called to convert data to XML
	 *
	 * @return
	 * @throws KettleException
	 */
	public String getXML() throws KettleException {

		StringBuilder retval = new StringBuilder(200);
		retval.append(advConfMeta.getXML("      "));
		if (nameParseMeta != null) {
			retval.append(nameParseMeta.getXML("      "));
		}
		if (addrVerifyMeta != null) {
			retval.append(addrVerifyMeta.getXML("      "));
		}
		if (geoCoderMeta != null) {
			retval.append(geoCoderMeta.getXML("      "));
		}
		if (phoneVerifyMeta != null) {
			retval.append(phoneVerifyMeta.getXML("      "));
		}
		if (emailVerifyMeta != null) {
			retval.append(emailVerifyMeta.getXML("      "));
		}
		if (smartMoverMeta != null) {
			retval.append(smartMoverMeta.getXML("      "));
		}
		if (matchUpMeta != null) {
			retval.append(matchUpMeta.getXML("      "));
		}
		if (ipLocatorMeta != null) {
			retval.append(ipLocatorMeta.getXML("	"));
		}
		retval.append(sourcePassThruMeta.getXML("      "));
		if (lookupPassThruMeta != null) {
			retval.append(lookupPassThruMeta.getXML("      "));
		}
		retval.append(outputFilterMeta.getXML("      "));
		retval.append(reportMeta.getXML("      "));
		return retval.toString();
	}

	/**
	 * Reporting is enabled if any portion of contact verify is configured
	 *
	 * @return
	 */
	public boolean isReportEnabled() {
		// See if reporting is disabled at a global level
		if (!advConfMeta.getBasicReporting()) {
			return false;
		}
		// Reporting is enabled only if one or more parts of contact verify is enabled
		if ((addrVerifyMeta != null) && addrVerifyMeta.isEnabled()) {
			return true;
		}
		if ((phoneVerifyMeta != null) && phoneVerifyMeta.isEnabled()) {
			return true;
		}
		if ((emailVerifyMeta != null) && emailVerifyMeta.isEnabled()) {
			return true;
		}
		if ((nameParseMeta != null) && (nameParseMeta.isEnabled() || nameParseMeta.isCompanyEnabled())) {
			return true;
		}
		if ((geoCoderMeta != null) && (geoCoderMeta.getAddrKeySource() != AddrKeySource.None) && geoCoderMeta.isEnabled()) {
			return true;
		}
		return false;
	}

	/**
	 * Called to initialize data from an XML node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {

		advConfMeta.readData(node);
		if (nameParseMeta != null) {
			nameParseMeta.readData(node);
		}
		if (addrVerifyMeta != null) {
			addrVerifyMeta.readData(node);
		}
		if (geoCoderMeta != null) {
			geoCoderMeta.readData(node);
		}
		if (phoneVerifyMeta != null) {
			phoneVerifyMeta.readData(node);
		}
		if (emailVerifyMeta != null) {
			emailVerifyMeta.readData(node);
		}
		if (smartMoverMeta != null) {
			smartMoverMeta.readData(node);
		}
		if (matchUpMeta != null) {
			matchUpMeta.readData(node);
		}
		if (ipLocatorMeta != null) {
			ipLocatorMeta.readData(node);
		}
		sourcePassThruMeta.readData(node);
		if (lookupPassThruMeta != null) {
			lookupPassThruMeta.readData(node);
		}
		outputFilterMeta.readData(node);
		reportMeta.readData(node);
	}

	/**
	 * Called to initialize data from repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep) throws KettleException {

		advConfMeta.readRep(rep, idStep);
		if (nameParseMeta != null) {
			nameParseMeta.readRep(rep, idStep);
		}
		if (addrVerifyMeta != null) {
			addrVerifyMeta.readRep(rep, idStep);
		}
		if (geoCoderMeta != null) {
			geoCoderMeta.readRep(rep, idStep);
		}
		if (phoneVerifyMeta != null) {
			phoneVerifyMeta.readRep(rep, idStep);
		}
		if (emailVerifyMeta != null) {
			emailVerifyMeta.readRep(rep, idStep);
		}
		if (smartMoverMeta != null) {
			smartMoverMeta.readRep(rep, idStep);
		}
		if (matchUpMeta != null) {
			matchUpMeta.readRep(rep, idStep);
		}
		if (ipLocatorMeta != null) {
			ipLocatorMeta.readRep(rep, idStep);
		}
		sourcePassThruMeta.readRep(rep, idStep);
		if (lookupPassThruMeta != null) {
			lookupPassThruMeta.readRep(rep, idStep);
		}
		outputFilterMeta.readRep(rep, idStep);
		reportMeta.readRep(rep, idStep);
	}

	/**
	 * Called to save data to a repository
	 *
	 * @param rep
	 * @param idTransformation
	 * @param idStep
	 * @throws KettleException
	 */
	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {

		advConfMeta.saveRep(rep, idTransformation, idStep);
		if (nameParseMeta != null) {
			nameParseMeta.saveRep(rep, idTransformation, idStep);
		}
		if (addrVerifyMeta != null) {
			addrVerifyMeta.saveRep(rep, idTransformation, idStep);
		}
		if (geoCoderMeta != null) {
			geoCoderMeta.saveRep(rep, idTransformation, idStep);
		}
		if (phoneVerifyMeta != null) {
			phoneVerifyMeta.saveRep(rep, idTransformation, idStep);
		}
		if (emailVerifyMeta != null) {
			emailVerifyMeta.saveRep(rep, idTransformation, idStep);
		}
		if (smartMoverMeta != null) {
			smartMoverMeta.saveRep(rep, idTransformation, idStep);
		}
		if (matchUpMeta != null) {
			matchUpMeta.saveRep(rep, idTransformation, idStep);
		}
		if (ipLocatorMeta != null) {
			ipLocatorMeta.saveRep(rep, idTransformation, idStep);
		}
		sourcePassThruMeta.saveRep(rep, idTransformation, idStep);
		if (lookupPassThruMeta != null) {
			lookupPassThruMeta.saveRep(rep, idTransformation, idStep);
		}
		outputFilterMeta.saveRep(rep, idTransformation, idStep);
		reportMeta.saveRep(rep, idTransformation, idStep);
	}

	/**
	 * Called to initialize default values for step data
	 *
	 * @throws KettleException
	 */
	public void setDefault() throws KettleException {
		// advConfMeta.setDefault();
		if (nameParseMeta != null) {
			nameParseMeta.setDefault();
		}
		if (addrVerifyMeta != null) {
			addrVerifyMeta.setDefault();
		}
		if (geoCoderMeta != null) {
			geoCoderMeta.setDefault();
		}
		if (phoneVerifyMeta != null) {
			phoneVerifyMeta.setDefault();
		}
		if (emailVerifyMeta != null) {
			emailVerifyMeta.setDefault();
		}
		if (smartMoverMeta != null) {
			smartMoverMeta.setDefault();
		}
		if (matchUpMeta != null) {
			matchUpMeta.setDefault();
		}
		if (ipLocatorMeta != null) {
			ipLocatorMeta.setDefault();
		}
		sourcePassThruMeta.setDefault();
		if (lookupPassThruMeta != null) {
			lookupPassThruMeta.setDefault();
		}
		outputFilterMeta.setDefault(checkTypes);
		reportMeta.setDefault();
		if (!VersionsMeta.isXmlSent()) {
			versionMeta.runThread();
		}
	}

	public void setReportMeta(ReportingMeta reportMeta) {

		this.reportMeta = reportMeta;
	}

	/**
	 * Called to validate settings.
	 */
	public void validate(List<String> warnings, List<String> errors) {

		advConfMeta.validate(warnings, errors);
		if (nameParseMeta != null) {
			nameParseMeta.validate(warnings, errors);
		}
		if (addrVerifyMeta != null) {
			addrVerifyMeta.validate(warnings, errors);
		}
		if (geoCoderMeta != null) {
			geoCoderMeta.validate(warnings, errors);
		}
		if (phoneVerifyMeta != null) {
			phoneVerifyMeta.validate(warnings, errors);
		}
		if (emailVerifyMeta != null) {
			emailVerifyMeta.validate(warnings, errors);
		}
		if (smartMoverMeta != null) {
			smartMoverMeta.validate(warnings, errors);
		}
		if (matchUpMeta != null) {
			matchUpMeta.validate(warnings, errors);
		}
		if (ipLocatorMeta != null) {
			ipLocatorMeta.validate(warnings, errors);
		}
		sourcePassThruMeta.validate(warnings, errors);
		if (advConfMeta.getBasicReporting()) {
			reportMeta.validate(warnings, errors);
		}
		if (lookupPassThruMeta != null) {
			lookupPassThruMeta.validate(warnings, errors);
		}
		outputFilterMeta.validate(this, warnings, errors);
	}
}
