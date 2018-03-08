package com.melissadata.cz.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.SourceFieldInfo;

public class MDInputCombo {

	private static Class<?> PKG = MDInputCombo.class;

	private static final String NOT_DEFINED = BaseMessages.getString(PKG, "MDSupport.MDInputCombo.NotDefined");

	private Label wLabel;
	
	private CCombo wComboBox;

	private SortedMap<String, SourceFieldInfo> sourceFields;
	
	private String usageID;
	
	private List<String> specialValues = new ArrayList<String>();

	private String currentValue;

	public MDInputCombo(Control[] wControls, SortedMap<String, SourceFieldInfo> sourceFields, String usageID) {
		this.wLabel = (Label)wControls[0];
		this.wComboBox = (CCombo)wControls[1];
		this.sourceFields = sourceFields;
		this.usageID = usageID;
		
		// Make the combo box read-only
		wComboBox.setEditable(false);
		
		// Add possible source fields and to combo box
		for (String field : sourceFields.keySet())
			wComboBox.add(field);
		
		// Add a special NOT DEFINED value
		addSpecialValue(NOT_DEFINED, "");
		
		// Listen for changes to the combo box
		wComboBox.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				handleChange();
			}
			
		});
	}	
	
	/**
	 * When input combo changes we need to update source field usages
	 */
	protected void handleChange() {
		// Ignore if combo box is already disposed
		if (wComboBox.isDisposed())
			return;
		
		// See if the value has changed
		int index = wComboBox.getSelectionIndex();
		String newValue = wComboBox.getItem(index);
		if (currentValue == null || !newValue.equals(currentValue)) {
			// Remove old usage
			if (currentValue != null) {
				SourceFieldInfo currentField = sourceFields.get(currentValue);
				if (currentField != null)
					currentField.removeUsage(usageID);
			}
			
			// Add new usage
			SourceFieldInfo newField = sourceFields.get(newValue);
			if (newField != null)
				newField.addUsage(usageID);
		}
		
		// Remember current value;
		currentValue = newValue;
	}
	
	/**
	 * Called to add a special value to the input list (such as NOT DEFINED)
	 * @param description
	 */
	public void addSpecialValue(String description, String value) {
		wComboBox.add(description, specialValues.size());
		specialValues.add(value);
	}
	
	/**
	 * Called to select the value of the combo box that matches this value. If none is found then]
	 * the first entry is selected. This is the default 'not defined' value.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		// If special value given then select special value in combo box
		int specialIndex = specialValues.indexOf(value);
		if (specialIndex != -1) {
			wComboBox.select(specialIndex);
			handleChange();
			return;
		}
		
		// Look for the value in the combo box, select it if it is there
		String[] items = wComboBox.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(value)) {
				wComboBox.select(i);
				handleChange();
				return;
			}
		}
		
		// If it could not be found then select the first element (which is always <not defined>)
		wComboBox.select(0);
		handleChange();
	}

	/**
	 * @return The currently selected value
	 */
	public String getValue() {
		// Nothing if already disposed
		if (wComboBox.isDisposed())
			return null;
		
		// If the first value is selected then return an empty string
		int selectionIndex = wComboBox.getSelectionIndex();
		if (selectionIndex < specialValues.size())
			return specialValues.get(selectionIndex);
		
		if(selectionIndex < 0)
			return null;
		
		// Otherwise return the selection
		String value = wComboBox.getItems()[selectionIndex];
		return value;
	}

	/**
	 * @return label portion of combo box
	 */
	public Label getLabel() {
		return wLabel;
	}
	
	/**
	 * @return The underlying combo box control
	 */
	public CCombo getComboBox() {
		return wComboBox;
	}

	/**
	 * Add a listener to the combo box
	 * 
	 * @param listener
	 */
	public void addSelectionListener(SelectionListener listener) {
		wComboBox.addSelectionListener(listener);
	}

	/**
	 * Called to enable/disable the combo box and its label
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		wLabel.setEnabled(enabled);
		wComboBox.setEnabled(enabled);
	}

	/**
	 * Called to hide/unhide the combo box and its label
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		wLabel.setVisible(visible);
		wComboBox.setVisible(visible);
	}

}
