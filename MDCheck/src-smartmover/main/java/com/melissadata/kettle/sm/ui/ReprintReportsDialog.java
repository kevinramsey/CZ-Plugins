package com.melissadata.kettle.sm.ui;

import java.lang.reflect.InvocationTargetException;

import com.melissadata.kettle.*;
import com.melissadata.kettle.cv.address.AdditionalInputDialog;
import com.melissadata.kettle.sm.SmartMoverMeta;
import com.melissadata.kettle.sm.service.MDCheckCOAService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;

public class ReprintReportsDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= AdditionalInputDialog.class;
	private Text           wJobId;
	private Text           wNCOASummary;
	private Text           wCASS;
	private SmartMoverMeta smMeta;

	/**
	 * Called to create additional input address fields dialog
	 *
	 * @param dialog
	 */
	public ReprintReportsDialog(MDCheckDialog dialog, String ncoaFile, String cassFile) {
		super(dialog, SWT.NONE);
		wNCOASummary.setText(ncoaFile);
		wCASS.setText(cassFile);
		changed = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#ok(java.lang.Object)
	 */
	@Override
	public void ok() {
		try {
			// Ignore changes
			changed = false;
			// Get field values
			String jobId = wJobId.getText().trim().replace("-", "");
			if (jobId.length() > 15) {
				jobId = jobId.substring(0, 15);
			}
			String cassFile = wCASS.getText().trim();
			String ncoaFile = wNCOASummary.getText().trim();
			// Validate fields
			if (jobId.length() == 0) {
				MessageDialog.openWarning(dialog.getShell(), getString("DownloadFail.Title"), getString("DownloadFail.Message.NoJobID"));
				return;
			}
			if ((cassFile.length() == 0) && (ncoaFile.length() == 0)) {
				MessageDialog.openWarning(dialog.getShell(), getString("DownloadFail.Title"), getString("DownloadFail.Message.NoReportFiles"));
				return;
			}
			// Dispose of this dialog
			dispose();
			// Reprint the reports in a safe manner
			safeReprintReport(jobId, cassFile, ncoaFile);
			// Inform the user that it succeeded
			if (smMeta.webSMException == null) {
				MessageDialog.openInformation(dialog.getShell(), getString("DownloadSuccess.Title"), getString("DownloadSuccess.Message"));
			} else {
				MessageDialog.openError(dialog.getShell(), getString("DownloadFail.Title"), getString("DownloadFail.Message.Error", smMeta.webSMException.getMessage()));
			}
		} catch (Throwable t) {
			MessageDialog.openError(dialog.getShell(), getString("DownloadFail.Title"), getString("DownloadFail.Message.Error", t.toString()));
		}
	}

	/**
	 * Called to reprint one report
	 *
	 * @param jobId
	 * @param cassFile
	 * @param ncoaFile
	 * @param monitor
	 * @throws KettleException
	 */
	private void reprintReport(String jobId, String cassFile, String ncoaFile, IProgressMonitor monitor) throws KettleException {
		// Local version of service that is dialog specific
		MDCheckCOAService coa = new MDCheckCOAService((MDCheckStepData) data, new MDCheckData(), space, log);
		coa.init();
		// Load data from controls
		coa.saveReports(jobId, ncoaFile, cassFile);
	}

	/**
	 * Called to reprint reports within a progress dialog
	 *
	 * @param ncoaFile
	 * @param cassFile
	 * @param jobId
	 * @throws InterruptedException
	 * @throws KettleException
	 */
	private void safeReprintReport(final String jobId, final String cassFile, final String ncoaFile) throws InterruptedException, KettleException {
		// Reprint the report inside a progress monitor dialog
		// TODO: Allow for cancellation.
		try {
			new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InterruptedException {
					// Start monitoring
					monitor.beginTask(getString("ProgressMessage"), IProgressMonitor.UNKNOWN);
					try {
						// Reprint the report
						reprintReport(jobId, cassFile, ncoaFile, monitor);
					} catch (KettleException e) {
						// Exceptions shold not be thrown in testing mode
						throw new RuntimeException(getString("Error"), e);
					} finally {
						// Stop monitoring
						monitor.done();
						if (monitor.isCanceled()) { throw new InterruptedException(getString("Canceled")); }
					}
				}
			});
		} catch (InvocationTargetException e) {
			// Something went wrong. Throw it back up.
			Throwable cause = e.getTargetException();
			if (cause instanceof RuntimeException) {
				if (cause.getCause() instanceof KettleException) { throw (KettleException) cause.getCause(); }
				throw (RuntimeException) cause;
			}
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#cancel()
	 */
	@Override
	protected boolean cancel() {
		// Ignore changes
		changed = false;
		return super.cancel();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		smMeta = ((MDCheckStepData) data).getSmartMover();
		// Create input Text Var Boxes
		wJobId = addTextBox(parent, null, "RJobId");
		wNCOASummary = addTextBox(parent, wJobId, "RNCOA");
		wCASS = addTextBox(parent, wNCOASummary, "RCASS");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) {
		// Nothing to return
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
		if (!MDCheckMeta.isPentahoPlugin()) {
			return "MDCheck.Help.ReprintReportsDialog";
		} else {
			return "MDCheck.Plugin.Help.ReprintReportsDialog";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getPackage()
	 */
	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getStringPrefix()
	 */
	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.ReprintReportsDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// Fields initialied in constructor
	}
}
