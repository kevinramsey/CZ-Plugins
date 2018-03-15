package com.melissadata.kettle.mu;

import org.pentaho.di.trans.step.StepMeta;

public class LookupTarget {
	private String		targetStepname;
	private StepMeta	targetStep;

	public StepMeta getTargetStep() {
		return targetStep;
	}

	public String getTargetStepname() {
		return targetStepname;
	}

	public void setTargetStep(StepMeta stepMeta) {
		targetStep = stepMeta;
	}

	public void setTargetStepname(String targetStepname) {
		this.targetStepname = targetStepname;
	}
}
