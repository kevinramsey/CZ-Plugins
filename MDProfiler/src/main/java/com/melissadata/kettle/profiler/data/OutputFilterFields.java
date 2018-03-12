package com.melissadata.kettle.profiler.data;

import java.util.HashMap;
import java.util.Map;

import com.melissadata.kettle.profiler.FilterTarget;
import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;

public class OutputFilterFields implements Cloneable {
	public static final String			TAG_FILTER			= "filter_fields";
	public static final String			TAG_FILTER_NAME		= "filter_name";
	public static final String			TAG_FILTER_PIN		= "filter_pin";
	public static final String			TAG_FILTER_TARGET	= "filter_target";
	public static final String			TAG_FILTERS			= "filters";
	public static final String			TAG_OUTPUT_FILTER	= "output_filter";
	public Map<String, FilterTarget>	filterTargets;

	public void init() {
		filterTargets = new HashMap<String, FilterTarget>();
		FilterTarget target;
		for (OutputPin op : OutputPin.values()) {
			target = new FilterTarget();
			target.setName(op.encode());
			target.setOutputPin(op);
			filterTargets.put(op.encode(), target);
		}
	}
}
