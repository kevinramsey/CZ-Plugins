package com.melissadata.kettle.evaluator;

import org.pentaho.di.core.exception.KettleException;

public class EvaluatorException extends KettleException {
	private static final long serialVersionUID = 3350852321563197648L;

	public EvaluatorException(String messageID, Object... args) {
		super(getMessage(messageID, args));
	}

	private static String getMessage(String messageID, Object... args) {
		return messageID;
		//return BaseMessages.getString(PKG, "MDCheck.BinaryEvaluator.Error." + messageID, args);
	}
}


