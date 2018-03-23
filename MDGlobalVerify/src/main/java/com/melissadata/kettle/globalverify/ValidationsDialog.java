package com.melissadata.kettle.globalverify;

import java.util.Calendar;

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
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressHelper;

public class ValidationsDialog extends MDAbstractDialog {
	private static Class<?>			PKG	= ValidationsDialog.class;
	private MDGlobalAddressHelper	helper;
	@SuppressWarnings("unused")
	private Validations				checker;

	public ValidationsDialog(MDGlobalDialog dialog) {
		super(dialog, SWT.NONE);
	}

	@Override
	protected void createContents(Composite parent, Object data) {
		checker = new Validations();
		helper = new MDGlobalAddressHelper((MDGlobalDialog) dialog);
		Control last;
		Label title = new Label(parent, SWT.NONE);
		title.setText("Warning some services may not be available!\nCheck your configurations and output steps\nto accommodate necessary changes.");
		helper.setLook(title);
		last = title;
		Label spacer = new Label(parent, SWT.NONE);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		spacer.setLayoutData(fd);
		helper.setLook(spacer);
		last = spacer;
		spacer = new Label(parent, SWT.NONE);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		spacer.setLayoutData(fd);
		helper.setLook(spacer);
		last = spacer;
		if (Validations.showErrors) {
			Label errors = new Label(parent, SWT.NONE);
			errors.setText("INITIALIZATION ERRORS");
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(last, helper.margin);
			errors.setLayoutData(fd);
			helper.setLook(errors);
			last = errors;
			spacer = new Label(parent, SWT.NONE);
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(last, helper.margin);
			spacer.setLayoutData(fd);
			helper.setLook(spacer);
			last = spacer;
		}
		if (Validations.showWarn) {
			Label warrnings = new Label(parent, SWT.NONE);
			warrnings.setText("INITIALIZATION WARNINGS");
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(last, helper.margin);
			warrnings.setLayoutData(fd);
			helper.setLook(warrnings);
			last = warrnings;
		}
		this.getShell().pack();
	}

	@SuppressWarnings("unused")
	private Control addMessage(Composite parent, Control last, String msg, String key) {
		Label lMessage = new Label(parent, SWT.NONE);
		lMessage.setText(msg);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		lMessage.setLayoutData(fd);
		helper.setLook(lMessage);
		Label remind = new Label(parent, SWT.NONE);
		remind.setText("Remind");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lMessage, helper.margin);
		remind.setLayoutData(fd);
		helper.setLook(remind);
		final Button remindWeek = new Button(parent, SWT.CHECK);
		remindWeek.setText("in a week");
		fd = new FormData();
		fd.left = new FormAttachment(remind, helper.margin);
		fd.top = new FormAttachment(lMessage, helper.margin);
		remindWeek.setLayoutData(fd);
		remindWeek.setData(key);
		helper.setLook(remindWeek);
		final Button remindNever = new Button(parent, SWT.CHECK);
		remindNever.setText("Never");
		fd = new FormData();
		fd.left = new FormAttachment(remindWeek, helper.margin);
		fd.top = new FormAttachment(lMessage, helper.margin);
		remindNever.setLayoutData(fd);
		remindNever.setData(key);
		helper.setLook(remindNever);
		remindWeek.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (remindWeek.getData().toString().equalsIgnoreCase("LIC"))
					Validations.setLicenceTimeStamp(getStampDateWeek());
				if (remindWeek.getData().toString().equalsIgnoreCase("ADDR"))
					Validations.setAddressTimeStamp(getStampDateWeek());
				if (remindWeek.getData().toString().equalsIgnoreCase("CAADDR"))
					Validations.setCaAddressTimeStamp(getStampDateWeek());
				if (remindWeek.getData().toString().equalsIgnoreCase("GEO"))
					Validations.setGeoTimeStamp(getStampDateWeek());
				if (remindWeek.getData().toString().equalsIgnoreCase("NAME"))
					Validations.setNameTimeStamp(getStampDateWeek());
				if (remindWeek.getData().toString().equalsIgnoreCase("PHONE"))
					Validations.setPhoneTimeStamp(getStampDateWeek());
				if (remindWeek.getData().toString().equalsIgnoreCase("EMAIL"))
					Validations.setEmailTimeStamp(getStampDateWeek());
			}
		});
		remindNever.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (remindNever.getData().toString().equalsIgnoreCase("LIC"))
					Validations.setLicenceTimeStamp("NEVER");
				if (remindNever.getData().toString().equalsIgnoreCase("ADDR"))
					Validations.setAddressTimeStamp("NEVER");
				if (remindNever.getData().toString().equalsIgnoreCase("CAADDR"))
					Validations.setCaAddressTimeStamp("NEVER");
				if (remindNever.getData().toString().equalsIgnoreCase("GEO"))
					Validations.setGeoTimeStamp("NEVER");
				if (remindNever.getData().toString().equalsIgnoreCase("NAME"))
					Validations.setNameTimeStamp("NEVER");
				if (remindNever.getData().toString().equalsIgnoreCase("PHONE"))
					Validations.setPhoneTimeStamp("NEVER");
				if (remindNever.getData().toString().equalsIgnoreCase("EMAIL"))
					Validations.setEmailTimeStamp("NEVER");
			}
		});
		return remind;
	}

	private String getStampDateWeek() {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_YEAR, 7);
		dateToString(today);
		return dateToString(today);
	}

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

	private String dateToString(Calendar date) {
		String sDate = "";
		if ((date.get(Calendar.MONTH) + 1) < 10)
			sDate = "0" + String.valueOf(date.get(Calendar.MONTH) + 1);
		else
			sDate = String.valueOf(date.get(Calendar.MONTH) + 1);
		if (date.get(Calendar.DAY_OF_MONTH) < 10)
			sDate += "-0" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		else
			sDate += "-" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		sDate += "-" + String.valueOf(date.get(Calendar.YEAR));
		return sDate;
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

	@Override
	protected void init(Object data) {
		// nothing
	}

	@Override
	protected Button[] getDialogButtons() {
		// Call parent to get OK and Cancel buttons
		// Button[] buttons = super.getDialogButtons();
		Button wValOK = new Button(getShell(), SWT.PUSH);
		wValOK.setText("OK");
		Button wValHelp = new Button(getShell(), SWT.PUSH);
		wValHelp.setText("Help");
		wValOK.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				dispose();
			}
		});
		wValHelp.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				MessageBox box = new MessageBox(dialog.getShell(), SWT.OK);
				box.setText(getString("Help.Title"));
				box.setMessage(getString("Help.Message"));
				box.open();
			}
		});
		return new Button[] { wValOK, wValHelp };
	}

	private String getString(String key) {
		return BaseMessages.getString(PKG, "MDGlobalAddressDialog.ValidationDialog." + key);
	}

	@Override
	protected boolean getData(Object data) throws KettleException {
		return false;
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	@Override
	protected String getStringPrefix() {
		return "MDGlobalAddressDialog.ValidationsDialog";
	}

	@Override
	protected String getHelpURLKey() {
		return null;
	}
}
