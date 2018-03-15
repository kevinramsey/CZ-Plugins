package com.melissadata.kettle.businesscoder.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;

import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;

/**
 * Borrowed and adapted from the kettle ControlSpaceKeyAdapter class
 */
public class MDControlSpaceKeyAdapter extends KeyAdapter {

	private static final String		BUSINESS_CODER_RESULT_CODE	= "MDBusinessCoderMeta.ResultCode.";

	private static Class<?>			PKG							= MDBusinessCoderMeta.class;

	private static final PropsUI	props						= PropsUI.getInstance();

	private Text					control;

	private Shell					shell;

	private List					list;

	private String[]				resultCodes;

	public MDControlSpaceKeyAdapter(Text control) {

		this.control = control;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		// CTRL-<SPACE> --> Display list of result codes
		if (isHotKey(e)) {
			e.doit = false;

			// textField.setData(TRUE) indicates we have transitioned from the textbox to list mode...
			// This will be set to false when the list selection has been processed
			// and the list is being disposed of.
			control.setData(Boolean.TRUE);

			// Drop down a list of result codes...
			Rectangle bounds = control.getBounds();
			Point location = GUIResource.calculateControlPosition(control);

			shell = new Shell(control.getShell(), SWT.NONE);
			shell.setSize(bounds.width, 200);
			shell.setLocation(location.x, location.y + bounds.height);
			shell.setLayout(new FillLayout());

			list = new List(shell, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
			props.setLook(list);
			list.setItems(getResultCodes());

			final DefaultToolTip toolTip = new DefaultToolTip(list, ToolTip.RECREATE, true);
// toolTip.setImage(GUIResource.getInstance().getImageSpoon());
			toolTip.setHideOnMouseDown(true);
			toolTip.setRespectMonitorBounds(true);
			toolTip.setRespectDisplayBounds(true);
			toolTip.setPopupDelay(350);

			list.addSelectionListener(new SelectionAdapter() {

				// Enter or double-click: picks the variable
				//
				@Override
				public synchronized void widgetDefaultSelected(SelectionEvent e) {

					applyChanges();
				}

				// Select a variable name: display the value in a tool tip
				//
				@Override
				public void widgetSelected(SelectionEvent event) {

					if (list.getSelectionCount() <= 0) { return; }
					Rectangle shellBounds = shell.getBounds();
					String rc = list.getSelection()[0];
					int i = rc.indexOf(" - ");
					if (i != 0) {
						rc = rc.substring(0, i);
					}
					String rcDescription = getResultCodeDetail(rc);
					if (rcDescription != null) {
						toolTip.setText(rcDescription);
						toolTip.hide();
						toolTip.show(new Point(shellBounds.width, 0));
					}
				}
			});

			list.addKeyListener(new KeyAdapter() {

				@Override
				public synchronized void keyPressed(KeyEvent e) {

					if ((e.keyCode == SWT.CR) && ((e.keyCode & SWT.CONTROL) == 0) && ((e.keyCode & SWT.SHIFT) == 0)) {
						applyChanges();
					}
				}

			});

			list.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent event) {

					shell.dispose();
					if (!control.isDisposed()) {
						control.setData(Boolean.FALSE);
					}
				}
			});

			shell.open();
		}
	}

	/*
	 * Add the text to the control
	 */
	private void applyChanges() {

		if (control.isDisposed()) { return; }

		if (list.getSelectionCount() <= 0) { return; }

		String rc = list.getSelection()[0];
		int i = rc.indexOf(" - ");
		if (i != 0) {
			rc = rc.substring(0, i);
		}
		i = rc.indexOf(".");
		if (i != 0) {
			rc = rc.substring(i + 1);
		}
		rc = "[" + rc + "]";

		control.insert(rc);

		if (!shell.isDisposed()) {
			shell.dispose();
		}

		if (!control.isDisposed()) {
			control.setData(Boolean.FALSE);
		}
	}

	/*
	 * Returns the description of a result code
	 */
	private String getResultCodeDetail(String rc) {

		String description = BaseMessages.getString(PKG, BUSINESS_CODER_RESULT_CODE + rc + ".Detail");
		if ((description != null) && description.startsWith("!")) {
			description = null;
		}
		return description;
	}

	/*
	 * Returns the list of currently defined result codes
	 */
	private String[] getResultCodes() {

		if ((resultCodes == null) || (resultCodes.length < 1)) {
			java.util.List<String> rcs = new ArrayList<String>();

			rcs.addAll(getResultCodes("Address", "AS"));
			rcs.addAll(getResultCodes("Address", "AE"));
			
			rcs.addAll(getResultCodes("GeoCode", "GS"));
			rcs.addAll(getResultCodes("GeoCode", "GE"));
			
			rcs.addAll(getResultCodes("Phone", "PS"));
			rcs.addAll(getResultCodes("Phone", "PE"));
			
			rcs.addAll(getResultCodes("Business/Firm", "FS"));	
			rcs.addAll(getResultCodes("Business/Firm", "FE")); 	

			resultCodes = rcs.toArray(new String[rcs.size()]);
		}
		return resultCodes;
	}

	/*
	 * Returns the defined error codes that begin with the given prefix
	 */
	private Collection<String> getResultCodes(String category, String prefix) {

		Set<String> rcs = new TreeSet<String>();

		ResourceBundle bundle = GlobalMessages.getBundle(PKG.getPackage().getName() + ".messages.messages", PKG);
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String rc = keys.nextElement();
				String categoryPrefix = BUSINESS_CODER_RESULT_CODE + category + ".";

				if (rc.startsWith(categoryPrefix + prefix)) {
					rc = rc.substring(categoryPrefix.length());
					int i = rc.indexOf(".");
					if (i != -1) {
						rc = rc.substring(0, i);
					}

					String msg = BaseMessages.getString(PKG, categoryPrefix + rc);
					rc = category + " " + rc + " - " + msg;
					rcs.add(rc);
				}
			}
		}
		return rcs;
	}

	/**
	 * Determines if this is the ctrl-space hot key. Handles language sensitivities.
	 *
	 * @param e
	 * @return
	 */
	private boolean isHotKey(KeyEvent e) {

		if (System.getProperty("user.language").equals("zh")) {
			return (e.character == ' ') && ((e.stateMask & SWT.CONTROL) != 0) && ((e.stateMask & SWT.ALT) != 0);
		} else if (System.getProperty("os.name").startsWith("Mac OS X")) {
			return (e.character == ' ') && ((e.stateMask & SWT.MOD1) != 0) && ((e.stateMask & SWT.ALT) == 0);
		} else {
			return (e.character == ' ') && ((e.stateMask & SWT.CONTROL) != 0) && ((e.stateMask & SWT.ALT) == 0);
		}
	}

}
