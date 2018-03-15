package com.melissadata.kettle.businesscoder.support;

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
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;

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
		} else {
			// TODO handel null values ?
		}

		return date;
	}

	private static Class<?>			PKG	= ValidationsDialog.class;

	private MDBusinessCoderHelper	helper;

	@SuppressWarnings("unused")
	private Validations				checker;

	public ValidationsDialog(MDBusinessCoderDialog dialog) {

		super(dialog, SWT.NONE);
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

		System.out.println("Date is  .." + dateToString(today));

		return dateToString(today);
	}

	private String getString(String key) {

		return BaseMessages.getString(PKG, "MDBusinessCoderDialog.ValidationDialog." + key);
	}

	@Override
	protected void createContents(Composite parent, Object data) {

		checker = new Validations();
		helper = new MDBusinessCoderHelper((MDBusinessCoderDialog) dialog);
		// MDBusinessCoderMeta meta = ((MDBusinessCoderDialog) dialog).getStepMeta();
		// String message;
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

		getShell().pack();

	}

	@Override
	protected boolean getData(Object data) throws KettleException {

		// TODO Auto-generated method stub
		return false;
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

	@Override
	protected String getHelpURLKey() {

		return null;
	}

	@Override
	protected Class<?> getPackage() {

		return PKG;
	}

	@Override
	protected String getStringPrefix() {

		return "MDBusinessCoderDialog.ValidationsDialog";
	}

	@Override
	protected void init(Object data) {

		// TODO Auto-generated method stub

	}

}
