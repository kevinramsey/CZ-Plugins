package com.melissadata.kettle.validation;

import java.util.Calendar;

import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.support.MDCheckHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.ui.MDAbstractDialog;

public class ValidationsDialog extends MDAbstractDialog {
	public static boolean isDataExpiring(String Date) {
		Calendar today = Calendar.getInstance();
		Calendar expDate;
		boolean expiring = false;
		if (!Const.isEmpty(Date) && !Date.equals("N/A")) {
			expDate = stringToDate(Date);
			today.add(Calendar.DAY_OF_YEAR, 14);
			expiring = today.after(expDate);
		}
		return expiring;
	}

	private static Calendar stringToDate(String sDate) {
		Calendar date = Calendar.getInstance();
		String[] YMD = new String[3];
		int year = 2011;
		int month = 5;
		int dayOfMonth = 15;
		if (sDate != null) {
			if (sDate.indexOf("-") == 4) {
				YMD = sDate.split("-");
				year = Integer.parseInt(YMD[0]);
				month = Integer.parseInt(YMD[1]) - 1;
				dayOfMonth = Integer.parseInt(YMD[2]);
				date.set(year, month, dayOfMonth);
			} else if (sDate.indexOf("-") == 2) {
				YMD = sDate.split("-");
				month = Integer.parseInt(YMD[0]) - 1;
				dayOfMonth = Integer.parseInt(YMD[1]);
				year = Integer.parseInt(YMD[2]);
				date.set(year, month, dayOfMonth);
			}
		}
		return date;
	}
	private static Class<?>		PKG			= ValidationsDialog.class;
	private static final String	ID_LICENSE	= "LIC";
	private static final String	ID_ADDR		= "ADDR";
	private static final String	ID_CA_ADDR	= "CAADDR";
	private static final String	ID_GEO		= "GEO";
	private static final String	ID_NAME		= "NAME";
	private static final String	ID_PHONE	= "PHONE";
	private static final String	ID_EMAIL	= "EMAIL";
	private MDCheckHelper helper;

	public ValidationsDialog(Shell parent, MDCheckDialog dialog, Validations checker) {
		super(parent, dialog, checker, SWT.NONE);
	}

	private Control addMessage(Composite parent, Control last, String msg, String key) {
		Label lMessage = new Label(parent, SWT.NONE);
		lMessage.setText(msg);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		lMessage.setLayoutData(fd);
		helper.setLook(lMessage);
		Label remind = new Label(parent, SWT.NONE);
		remind.setText(getString("Remind.Label"));
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lMessage, helper.margin);
		remind.setLayoutData(fd);
		helper.setLook(remind);
		final Button remindWeek = new Button(parent, SWT.CHECK);
		remindWeek.setText(getString("InWeek.Label"));
		fd = new FormData();
		fd.left = new FormAttachment(remind, helper.margin);
		fd.top = new FormAttachment(lMessage, helper.margin);
		remindWeek.setLayoutData(fd);
		remindWeek.setData(key);
		helper.setLook(remindWeek);
		final Button remindNever = new Button(parent, SWT.CHECK);
		remindNever.setText(getString("Never.Label"));
		fd = new FormData();
		fd.left = new FormAttachment(remindWeek, helper.margin);
		fd.top = new FormAttachment(lMessage, helper.margin);
		remindNever.setLayoutData(fd);
		remindNever.setData(key);
		helper.setLook(remindNever);
		remindWeek.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Validations checker = (Validations) data;
				if (remindWeek.getData().toString().equalsIgnoreCase(ID_LICENSE)) {
					checker.setLicenseTimeStamp(getStampDateWeek());
				}
				if (remindWeek.getData().toString().equalsIgnoreCase(ID_ADDR)) {
					checker.setAddressTimeStamp(getStampDateWeek());
				}
				if (remindWeek.getData().toString().equalsIgnoreCase(ID_CA_ADDR)) {
					checker.setCaAddressTimeStamp(getStampDateWeek());
				}
				if (remindWeek.getData().toString().equalsIgnoreCase(ID_GEO)) {
					checker.setGeoTimeStamp(getStampDateWeek());
				}
				if (remindWeek.getData().toString().equalsIgnoreCase(ID_NAME)) {
					checker.setNameTimeStamp(getStampDateWeek());
				}
				if (remindWeek.getData().toString().equalsIgnoreCase(ID_PHONE)) {
					checker.setPhoneTimeStamp(getStampDateWeek());
				}
				if (remindWeek.getData().toString().equalsIgnoreCase(ID_EMAIL)) {
					checker.setEmailTimeStamp(getStampDateWeek());
				}
			}
		});
		remindNever.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Validations checker = (Validations) data;
				if (remindNever.getData().toString().equalsIgnoreCase(ID_LICENSE)) {
					checker.setLicenseTimeStamp("NEVER");
				}
				if (remindNever.getData().toString().equalsIgnoreCase(ID_ADDR)) {
					checker.setAddressTimeStamp("NEVER");
				}
				if (remindNever.getData().toString().equalsIgnoreCase(ID_CA_ADDR)) {
					checker.setCaAddressTimeStamp("NEVER");
				}
				if (remindNever.getData().toString().equalsIgnoreCase(ID_GEO)) {
					checker.setGeoTimeStamp("NEVER");
				}
				if (remindNever.getData().toString().equalsIgnoreCase(ID_NAME)) {
					checker.setNameTimeStamp("NEVER");
				}
				if (remindNever.getData().toString().equalsIgnoreCase(ID_PHONE)) {
					checker.setPhoneTimeStamp("NEVER");
				}
				if (remindNever.getData().toString().equalsIgnoreCase(ID_EMAIL)) {
					checker.setEmailTimeStamp("NEVER");
				}
			}
		});
		return remind;
	}

	private String dateToString(Calendar date) {
		String sDate = "";
		if ((date.get(Calendar.MONTH) + 1) < 10) {
			sDate = "0" + String.valueOf(date.get(Calendar.MONTH) + 1);
		} else {
			sDate = String.valueOf(date.get(Calendar.MONTH) + 1);
		}
		if (date.get(Calendar.DAY_OF_MONTH) < 10) {
			sDate += "-0" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		} else {
			sDate += "-" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		}
		sDate += "-" + String.valueOf(date.get(Calendar.YEAR));
		return sDate;
	}

	private String getStampDateWeek() {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_YEAR, 7);
		dateToString(today);
		return dateToString(today);
	}

	private String getString(String key) {
		return BaseMessages.getString(PKG, "MDCheckDialog.ValidationDialog." + key);
	}

	@Override
	protected void createContents(Composite parent, Object data) {
		helper = new MDCheckHelper((MDCheckDialog) dialog);
		MDCheckStepData stepData = (MDCheckStepData) dialog.getData();
		String message;
		Control last;
		Label title = new Label(parent, SWT.NONE);
		title.setText(getString("ServicesNotAvailable"));
		helper.setLook(title);
		last = title;
		Label spacer = new Label(parent, SWT.NONE);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		spacer.setLayoutData(fd);
		helper.setLook(spacer);
		last = spacer;
		if (isDataExpiring(stepData.getAdvancedConfiguration().getLicenceExpiration())) {
			last = addMessage(parent, last, getString("LicenseExpires") + stepData.getAdvancedConfiguration().getLicenceExpiration(), ID_LICENSE);
		}
		spacer = new Label(parent, SWT.NONE);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		spacer.setLayoutData(fd);
		helper.setLook(spacer);
		last = spacer;
		Validations checker = (Validations) data;
		if (checker.showErrors) {
			Label errors = new Label(parent, SWT.NONE);
			errors.setText(getString("InitializeErrors.Header"));
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(last, helper.margin);
			errors.setLayoutData(fd);
			helper.setLook(errors);
			last = errors;
			message = stepData.getAddressVerify().getInitializeError();
			if (!Const.isEmpty(message)) {
				message = stepData.getAddressVerify().getInitializeZipError();
			}
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_ADDR);
			}
			message = stepData.getAddressVerify().getInitializeCaError();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_CA_ADDR);
			}
			message = stepData.getGeoCoder().getInitializeError();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_GEO);
			}
			message = stepData.getNameParse().getInitializeError();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_NAME);
			}
			message = stepData.getPhoneVerify().getInitializeError();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_PHONE);
			}
			message = stepData.getEmailVerify().getInitializeError();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_EMAIL);
			}
			spacer = new Label(parent, SWT.NONE);
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(last, helper.margin);
			spacer.setLayoutData(fd);
			helper.setLook(spacer);
			last = spacer;
		}
		if (checker.showWarnings) {
			Label warrnings = new Label(parent, SWT.NONE);
			warrnings.setText(getString("InitializeWarn.Header"));
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(last, helper.margin);
			warrnings.setLayoutData(fd);
			helper.setLook(warrnings);
			last = warrnings;
			message = stepData.getAddressVerify().getInitializeWarn();
			if (!Const.isEmpty(message)) {
				message = stepData.getAddressVerify().getInitializeZipWarn();
			}
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_ADDR);
			}
			message = stepData.getAddressVerify().getInitializeCaWarn();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_CA_ADDR);
			}
			message = stepData.getGeoCoder().getInitializeWarn();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_GEO);
			}
			message = stepData.getNameParse().getInitializeWarn();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_NAME);
			}
			message = stepData.getPhoneVerify().getInitializeWarn();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_PHONE);
			}
			message = stepData.getEmailVerify().getInitializeWarn();
			if (!Const.isEmpty(message)) {
				last = addMessage(parent, last, message, ID_EMAIL);
			}
		}
		getShell().pack();
	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		return false;
	}

	@Override
	protected Button[] getDialogButtons() {
		Button wValOK = new Button(getShell(), SWT.PUSH);
		wValOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		Button wValHelp = new Button(getShell(), SWT.PUSH);
		wValHelp.setText(BaseMessages.getString(PKG, "MDCheckDialog.Button.Help"));
		wValOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				dispose();
			}
		});
		wValHelp.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				MessageBox box = new MessageBox(dialog.getShell(), SWT.OK);
				box.setText(getString("Help.Title"));
				box.setMessage(getString("Help.Message"));
				box.open();
			}
		});
		return new Button[] { wValOK, wValHelp };
	}

	@Override
	protected String getHelpURLKey() {
		// not sure if we will need this
		// this is just a fancy message box
		return null;
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.ValidationDialog";
	}

	@Override
	protected void init(Object data) {
		// do nothing for now
	}
}
