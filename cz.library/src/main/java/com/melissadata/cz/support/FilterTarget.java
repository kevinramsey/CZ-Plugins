package com.melissadata.cz.support;

import org.pentaho.di.trans.step.StepMeta;

/**
 *  Class to hold Filter target.
 *  A filter target is defined as the output destination of a given file.
 */
public class FilterTarget {

	/**
	 * Name identifying a filter target
	 */
	private String   name;
	/**
	 * The rule for target i.e.  [AS01] or [AS01]
	 */
	private String   rule;
	/**
	 * Name of the target step
	 */
	private String   targetStepname;
	/**
	 *  the stepMeta of the target.
	 */
	private StepMeta targetStep;

	public FilterTarget() {
		name = "";
		rule = "";
		targetStepname = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRule() {
		if (rule == null)
			rule = "";
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getTargetStepname() {
		return targetStepname;
	}

	public void setTargetStepname(String targetStepname) {
		this.targetStepname = targetStepname;
	}

	public StepMeta getTargetStep() {
		return targetStep;
	}

	public void setTargetStep(StepMeta targetStep) {
		this.targetStep = targetStep;
	}
}
