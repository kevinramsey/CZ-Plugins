package com.melissadata.kettle.profiler;

import org.pentaho.di.trans.step.StepMeta;

import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;

public class FilterTarget {
	private String		name;
	private String		targetStepname;
	private StepMeta	targetStep;
	private String		pin;
	private boolean		attached;

	public FilterTarget() {
		name = "";
	}

	public String getName() {
		return name;
	}

	public OutputPin getPin() {
		OutputPin tmpPin = OutputPin.valueOf(pin);
		return tmpPin;
	}

	public StepMeta getTargetStep() {
		return targetStep;
	}

	public String getTargetStepname() {
		if (targetStep != null) {
			targetStepname = targetStep.getName();
		}
		return targetStepname;
	}

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOutputPin(OutputPin outputPin) {
		pin = outputPin.encode();
	}

	public void setOutputPin(String pinName) {
		pin = pinName;
	}

	public void setTargetStep(StepMeta targetStep) {
		this.targetStep = targetStep;
	}

	public void setTargetStepname(String targetStepname) {
		this.targetStepname = targetStepname;
	}
}
