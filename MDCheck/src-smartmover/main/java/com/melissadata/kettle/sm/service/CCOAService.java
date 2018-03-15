package com.melissadata.kettle.sm.service;

import java.net.URL;
import java.util.List;

import com.melissadata.kettle.MDCheck;
import com.melissadata.kettle.MDCheckData;
import com.melissadata.kettle.MDCheckRequest;
import com.melissadata.kettle.MDCheckStepData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

public class CCOAService extends COAService {
	/*
	 * public enum NCOACustomerStatus {
	 * Unknown, Valid, Invalid
	 * }
	 * public enum NCOAPAFStatus {
	 * Unknown, Valid, NoPAF, PAFExpired, PAFInvalid, PAFIdRequired
	 * }
	 * public enum NCOAPackageStatus {
	 * Unknown, Valid, NoPackage
	 * }
	 */
	private String	version	= "";
	public boolean	chkXML	= false;

	public CCOAService(MDCheckStepData stepData, MDCheckData checkData, boolean testing, LogChannelInterface log, VariableSpace space) throws KettleException {
		super(stepData, checkData, testing, log, space);
		super.init();
	}

	/*
	 * (non-Javadoc)
	 * @see COAService#getVersion()
	 */
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void incrTimeout() {
		// do nothing here
	}

	/*
	 * (non-Javadoc)
	 * @see COAService#submitRecordSet(java.util.List)
	 */
	@Override
	public String submitRecordSet(List<MDCheckRequest> requests, int queue, int attempts) throws KettleException {
		try {
			// Done if there are no records or we are not testing
			int recordCount = testing ? 1 : requests.size();
			if (recordCount == 0) { return ""; }
			// 0 is the queue for ncoa
			processRequests(requests, queue, false, attempts);
			return "";
		} catch (KettleException e) {
			// Re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened
			throw new KettleException(MDCheck.getErrorString("ProcessService", t.toString()), t);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see COAService#getURL()
	 */
	@Override
	protected URL getURL() {
		return checkData.realCCOAURL;
	}
}
