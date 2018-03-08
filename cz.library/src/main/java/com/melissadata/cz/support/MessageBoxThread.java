package com.melissadata.cz.support;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Class to display message box separate thread.
 * Used to display mesages without interfering with the main processing thread.
 */

public class MessageBoxThread extends Thread {

	Shell  shell;
	String message  = "";
	String name     = "";
	String type     = "";

	public MessageBoxThread(Shell shl, String type, String name, String message){
		this.shell = shl;
		this.type = type;
		this.name = name;
		this.message = message;
		
	}

	/**
	 * Call to display message box
	 */
	public void run() {
		showMessage();
	}

	/**
	 * Determins what message to display.
	 */
	private void showMessage(){

		if(type == "sleep"){
			showLongSleepMsg();
			return;
		}

		if(type == "community"){
			showCommunityMessage();
		}

	}


	public void showLongSleepMsg() {
		MessageBox box = new MessageBox(shell, SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
		box.setText("MD Service " + name/* BaseMessages.getString(PKG, "MDCheck.Welcome.Linux.Title")*/);
		box.setMessage(message);
		box.open();

	}

	public void showCommunityMessage(){
		MessageBox communityMessageBox = new MessageBox(shell, SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION);
		communityMessageBox.setText(name +" Community Edition");
		communityMessageBox.setMessage(message);
		communityMessageBox.open();
	}



}
