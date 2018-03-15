package com.melissadata.kettle.support;

import org.pentaho.di.trans.step.StepMeta;

public class FilterTarget {
	private String		name;
	private String		rule;
	private String		targetStepname;
	private StepMeta	targetStep;

	public FilterTarget() {
		name = "";
		rule = "";
		targetStepname = "";
	}

	public String getName() {
		return name;
	}

	public String getRule() {
		if (rule == null) {
			rule = "";
		}
		return rule;
	}

	public StepMeta getTargetStep() {
		return targetStep;
	}

	public String getTargetStepname() {
		return targetStepname;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public void setTargetStep(StepMeta targetStep) {
		this.targetStep = targetStep;
	}

	public void setTargetStepname(String targetStepname) {
		this.targetStepname = targetStepname;
	}
}
