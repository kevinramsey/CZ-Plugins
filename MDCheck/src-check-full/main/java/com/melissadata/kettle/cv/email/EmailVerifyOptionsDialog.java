package com.melissadata.kettle.cv.email;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.cz.support.MDPropTags;
import org.eclipse.swt.SWT;
// import org.eclipse.swt.layout.FormAttachment;
// import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
// import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
// import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.exception.KettleException;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class EmailVerifyOptionsDialog extends MDAbstractDialog {
	private static final Class<?>	PKG	= EmailVerifyOptionsDialog.class;
	private MDCheckDialog   dialog;
	private boolean					isLocalService;
	private Button					wCorrectEmailSyntax;
	private Button					wPerformDBLookup;
	private Button					wPerformDNSLookup;
	private Button					wStandardizeCasing;
	private Button					wUpdateDomains;
	private Button					wFuzzyLookup;
	private Button					wWebServiceLookup;
	private Text					wOutputMailboxname;
	private Text					wOutputDomain;
	private Text					wOutputTLD;
	private Text					wOutputTLDDescription;

	public EmailVerifyOptionsDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
		this.dialog = dialog;
	}

	/**
	 * Handles enablement of controls based on current context
	 */
	private void enable() {
		// Disable the email options if we are not doing local service processing
		boolean enabled = isLocalService;
		wCorrectEmailSyntax.setEnabled(enabled);
		wPerformDBLookup.setEnabled(enabled);
		wStandardizeCasing.setEnabled(enabled);
		wFuzzyLookup.setEnabled(enabled);
		wWebServiceLookup.setEnabled(enabled);
		if (!wPerformDBLookup.getSelection() && !wFuzzyLookup.getSelection() && !wWebServiceLookup.getSelection()) {
			wUpdateDomains.setEnabled(false);
			wUpdateDomains.setSelection(false);
		}
		if (!wPerformDBLookup.getSelection()) {
			wUpdateDomains.setEnabled(false);
			wUpdateDomains.setSelection(false);
		} else {
			wUpdateDomains.setEnabled(enabled);
		}
		if (!wUpdateDomains.getSelection()) {
			wPerformDNSLookup.setSelection(false);
			wPerformDNSLookup.setEnabled(false);
		} else {
			wPerformDNSLookup.setEnabled(enabled);
		}

		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_EmailObject)){
			wCorrectEmailSyntax.setEnabled(false);
			wPerformDBLookup.setEnabled(false);
			wStandardizeCasing.setEnabled(false);
			wFuzzyLookup.setEnabled(false);
			wWebServiceLookup.setEnabled(false);
			wUpdateDomains.setEnabled(false);
			wPerformDNSLookup.setEnabled(false);

			wOutputMailboxname.setEnabled(false);
			wOutputDomain.setEnabled(false);
			wOutputTLD.setEnabled(false);
			wOutputTLDDescription.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		if(dialog == null){
			dialog = (MDCheckDialog)super.dialog;
		}
		isLocalService = (dialog.getAdvancedConfigMeta().getServiceType() == ServiceType.Local);
		Group emailOptionsGroup = addGroup(parent, null, "EmailOptionsGroup");
		Control wLast = null;
		if (!isLocalService) {
			wLast = addLabel(emailOptionsGroup, null, "EmailOptionsWarning");
		}
		wStandardizeCasing = addCheckBox(emailOptionsGroup, wLast, "StandardizeCasing");
		wCorrectEmailSyntax = addCheckBox(emailOptionsGroup, wStandardizeCasing, "CorrectEmailSyntax");
		wWebServiceLookup = addCheckBox(emailOptionsGroup, wCorrectEmailSyntax, "WebServiceLookup");
		wWebServiceLookup.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}

			public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		wPerformDBLookup = addCheckBox(emailOptionsGroup, wWebServiceLookup, "PerformDBLookup");
		wPerformDBLookup.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}

			public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		wFuzzyLookup = addCheckBox(emailOptionsGroup, wPerformDBLookup, "FuzzyLookup");
		wFuzzyLookup.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}

			public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		wUpdateDomains = addCheckBox(emailOptionsGroup, wFuzzyLookup, "UpdateDomains");
		wUpdateDomains.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}

			public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		wPerformDNSLookup = addCheckBox(emailOptionsGroup, wUpdateDomains, "PerformDNSLookup");
		Group emailOutputGroup = addGroup(parent, emailOptionsGroup, "EmailOutputGroup");
		wOutputMailboxname = addTextBox(emailOutputGroup, null, "PhoneEmailTab.OutputMailboxName");
		wOutputDomain = addTextBox(emailOutputGroup, wOutputMailboxname, "PhoneEmailTab.OutputDomain");
		wOutputTLD = addTextBox(emailOutputGroup, wOutputDomain, "PhoneEmailTab.OutputTLD");
		wOutputTLDDescription = addTextBox(emailOutputGroup, wOutputTLD, "PhoneEmailTab.OutputTLDName");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) throws KettleException {
		// Local version of input that is dialog specific
		EmailVerifyMeta evMeta = ((MDCheckStepData) data).getEmailVerify();
		evMeta.setOptionCorrectEmailSyntax(wCorrectEmailSyntax.getSelection());
		evMeta.setOptionPerformDBLookup(wPerformDBLookup.getSelection());
		evMeta.setOptionPerformDNSLookup(wPerformDNSLookup.getSelection());
		evMeta.setOptionStandardizeCasing(wStandardizeCasing.getSelection());
		evMeta.setOptionUpdateDomains(wUpdateDomains.getSelection());
		evMeta.setOptionFuzzyLookup(wFuzzyLookup.getSelection());
		evMeta.setOptionWebServiceLookup(wWebServiceLookup.getSelection());
		evMeta.setOutputMailboxName(wOutputMailboxname.getText());
		evMeta.setOutputDomain(wOutputDomain.getText());
		evMeta.setOutputTLD(wOutputTLD.getText());
		evMeta.setOutputTLDDescription(wOutputTLDDescription.getText());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
			return "MDCheck.Help.EmailVerifyOptionsDialog";
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
		return "MDCheckDialog.EmailVerifyOptionsDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// Local version of input that is dialog specific
		EmailVerifyMeta evMeta = ((MDCheckStepData) data).getEmailVerify();
		wCorrectEmailSyntax.setSelection(evMeta.getOptionCorrectEmailSyntax());
		wPerformDBLookup.setSelection(evMeta.getOptionPerformDBLookup());
		wPerformDNSLookup.setSelection(evMeta.getOptionPerformDNSLookup());
		wStandardizeCasing.setSelection(evMeta.getOptionStandardizeCasing());
		wUpdateDomains.setSelection(evMeta.getOptionUpdateDomains());
		wFuzzyLookup.setSelection(evMeta.getOptionFuzzyLookup());
		wWebServiceLookup.setSelection(evMeta.getOptionWebServiceLookup());
		wOutputMailboxname.setText(evMeta.getOutputMailboxName());
		wOutputDomain.setText(evMeta.getOutputDomain());
		wOutputTLD.setText(evMeta.getOutputTLD());
		wOutputTLDDescription.setText(evMeta.getOutputTLDDescription());
		// Handle control enablement
		enable();
		getShell().pack();
	}
}
