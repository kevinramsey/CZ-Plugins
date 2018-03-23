package com.melissadata.kettle.propertywebservice.error;

import org.pentaho.di.core.exception.KettleException;

public class MDAbortException extends KettleException {
	private static final long serialVersionUID = 2807207016085188267L;

	public MDAbortException(String message) {
		super(message);
	}
}