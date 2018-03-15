package com.melissadata.kettle.ui;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDCustomDefaults;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

public class RunOptionsTab implements MDTab {
	private static Class<?> PKG = MDCheckMeta.class;
	private MDCheckDialog             dialog;
	private MDCheckStepData           mdcStepData;
	private MDCheckHelper             helper;
	// private static final boolean isLinux = System.getProperty("os.name").contains("Linux");
	private Button                    wLocalButton;
	private Button                    wWebButton;
	private Button                    wCVSButton;
	private Group                     wGroup;
	private AdvancedConfigurationMeta acMeta;

	public RunOptionsTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		mdcStepData = dialog.getData();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Create a line of buttons that toggle between service states (not for smart mover or matchup)
		Composite wToggleComp = createToggleContents(wComp, description);
		createSaveDefaultsGroup(wComp, wToggleComp);
		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		// Pack the composite and get its size
		wComp.pack();
		Rectangle bounds = wComp.getBounds();
		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		wSComp.setMinWidth(bounds.width);
		wSComp.setMinHeight(bounds.height);
		// Initialzie the tab with the scrolled composite
		wTab.setControl(wSComp);
	}

	public void advancedConfigChanged() {
		// no used
	}

	public void dispose() {
		// Nothing to do
	}

	public void getData(MDCheckStepData data) {
		// Determine the service type
		ServiceType serviceType;
		if (/* !isLinux && */wLocalButton != null) {
			if (wLocalButton.getSelection()) {
				serviceType = ServiceType.Local;
			} else if (wWebButton.getSelection()) {
				serviceType = ServiceType.Web;
			} else {
				/* if (wCVSButton.getSelection()) */
				serviceType = ServiceType.CVS;
			}
		} else {
			if (wWebButton.getSelection()) {
				serviceType = ServiceType.Web;
			} else {
				/* if (wCVSButton.getSelection()) */
				serviceType = ServiceType.CVS;
			}
		}
		// Save service type
		data.getAdvancedConfiguration().setServiceType(serviceType);
	}

	public String getHelpURLKey() {
		String endString = "CVLocal";
		if ((mdcStepData.getCheckTypes() & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			endString = "IpLocator";
		}
		return "MDCheck.Help.RunOptionsTab." + endString;
	}

	public boolean init(MDCheckStepData data) {
		mdcStepData = data;
		// Local version of input that is Advanced Configuration specific
		acMeta = mdcStepData.getAdvancedConfiguration();
		wCVSButton.setEnabled(true);
		wWebButton.setEnabled(true);
		wLocalButton.setEnabled(true);
		// Get the service type
		switch (acMeta.getServiceType()) {
			case Local:
				if (/* !isLinux */true) {
					wLocalButton.setSelection(true);
				} else {
					wWebButton.setSelection(true);
				}
				break;
			case Web:
				wWebButton.setSelection(true);
				break;
			case CVS:
				wCVSButton.setSelection(true);
				break;
		}

		if (!AdvancedConfigurationMeta.isEnterprise(mdcStepData.getProductCode())) {
			if (AdvancedConfigurationMeta.isCommunity()) {
				wLocalButton.setSelection(true);
				wWebButton.setSelection(false);
				wCVSButton.setSelection(false);

				wCVSButton.setEnabled(false);
				wWebButton.setEnabled(false);
			}
		} else {
			//wResultCodes.setEnabled(true);
		}

		return false;
	}

	private Group createSaveDefaultsGroup(Composite parent, Control wLast) {
		// Group for save defaults
		// Group wGroup = addGroup(parent, wLast, "SaveDefaultsGroup");
		wGroup = new Group(parent, SWT.NONE);
		wGroup.setText(getString("DefaultsGroup"));
		helper.setLook(wGroup);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wLast, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		wGroup.setLayoutData(fd);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		Label defaultsDescription = helper.addLabel(wGroup, null, "RunOptionsTab.SaveDefaultsDescription");
		Button bSaveDefaults = new Button(wGroup, SWT.PUSH);
		helper.setLook(bSaveDefaults);
		bSaveDefaults.setText(getString("SaveDefaults"));
		// Lay it out
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(defaultsDescription, helper.margin);
		bSaveDefaults.setLayoutData(fd);
		// add label to display info, hide until defaults are saved
		final Label lDefaultsSaved = helper.addLabel(wGroup, defaultsDescription, "RunOptionsTab.DefaultsSaved");
		((FormData) lDefaultsSaved.getLayoutData()).left = new FormAttachment(bSaveDefaults, 20);
		lDefaultsSaved.setVisible(false);
		Listener lsSaveDefaults = new Listener() {
			public void handleEvent(Event arg0) {
				// obj for checking default values
				MDCustomDefaults mdcd = new MDCustomDefaults(mdcStepData.getAdvancedConfiguration(), dialog);
				try {
					mdcd.saveDefaults();
					lDefaultsSaved.setVisible(true);
				} catch (KettleException e) {
					lDefaultsSaved.setText(getString("DefaultsSaveError"));
					lDefaultsSaved.setVisible(true);
				}
			}
		};
		bSaveDefaults.addListener(SWT.Selection, lsSaveDefaults);
		// Return save defaults group
		return wGroup;
	}

	/**
	 * Called to create a line of toggle buttons for selecting the service state.
	 *
	 * @param parent
	 * @param last
	 */
	private Composite createToggleContents(Composite parent, Control last) {
		// Service Type toggle buttons are in their own composite so we can give them radio like behavior
		final Composite wToggleComp = new Composite(parent, 0);
		helper.setLook(wToggleComp);
		wToggleComp.setLayout(new FormLayout());
		// Lay out the toggle composite
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(last, 0);
		fd.right = new FormAttachment(100, 0);
		wToggleComp.setLayoutData(fd);
		// Create group to contain the toggle buttons
		wGroup = new Group(wToggleComp, SWT.NONE);
		wGroup.setText(getString("ServiceTypeGroup"));
		helper.setLook(wGroup);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		wGroup.setLayoutData(fd);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		FontData newfd = new FontData("Arial", 10, SWT.BOLD);
		Font newFont = new Font(wToggleComp.getDisplay(), newfd);
		if (!((mdcStepData.getCheckTypes() & MDCheckMeta.MDCHECK_SMARTMOVER) != 0)) {
			wLocalButton = helper.addRadioButton(wGroup, null, "RunOptionsTab.LocalServiceType");
			wLocalButton.setFont(newFont);
			Label localDescription = helper.addLabel(wGroup, wLocalButton, "RunOptionsTab.LocalServiceDescription");
			wWebButton = helper.addRadioButton(wGroup, localDescription, "RunOptionsTab.WebServiceType");
			((FormData) localDescription.getLayoutData()).left.offset += 3;
		} else {
			wWebButton = helper.addRadioButton(wGroup, null, "RunOptionsTab.WebServiceType");
		}
		wWebButton.setFont(newFont);
		Label webDescription = helper.addLabel(wGroup, wWebButton, "RunOptionsTab.WebServiceDescription");
		wCVSButton = helper.addRadioButton(wGroup, webDescription, "RunOptionsTab.CVSServiceType");
		wCVSButton.setFont(newFont);
		Label cvsDescription = helper.addLabel(wGroup, wCVSButton, "RunOptionsTab.CVSServiceDescription");
		// Clean up the layout
		((FormData) webDescription.getLayoutData()).left.offset += 3;
		((FormData) cvsDescription.getLayoutData()).left.offset += 3;
		// Add listeners that make sure only one of the service composites is visible
		if (/* !isLinux */true) {
			if (wLocalButton != null) {
				wLocalButton.addSelectionListener(new SelectionAdapter() {
					@Override public void widgetSelected(SelectionEvent e) {
						getData(mdcStepData);
						enable();
					}
				});
			}
		}
		wWebButton.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				getData(mdcStepData);
				enable();
			}
		});
		wCVSButton.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				getData(mdcStepData);
				enable();
			}
		});
		return wToggleComp;
	}

	private void enable() {
		dialog.advancedChange();
	}

	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.RunOptionsTab." + key + ".Label", args);
	}
}
