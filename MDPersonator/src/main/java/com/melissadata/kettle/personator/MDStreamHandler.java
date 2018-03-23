package com.melissadata.kettle.personator;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.errorhandling.Stream;
import org.pentaho.di.trans.step.errorhandling.StreamIcon;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface.StreamType;

public class MDStreamHandler {
	/**
	 * Called to get an optional new target stream
	 *
	 * @return
	 */
	public static List<StreamInterface> getOptionalStreams(List<FilterTarget> filterTargets) {
		// If there are free filter target spots then add an optional stream for creating a new one
		List<StreamInterface> list = new ArrayList<StreamInterface>();
		if (filterTargets.size() < MAX_TARGETS) {
			list.add(newFilterTargetStream);
		}
		return list;
	}

	/**
	 * Called to define the meta data for the output targets
	 *
	 * @param ioMeta
	 */
	public static void getStepIOMeta(StepIOMetaInterface ioMeta, List<FilterTarget> filterTargets) {
		// Add the targets...
		//
		if (filterTargets == null) {
			filterTargets = new ArrayList<FilterTarget>();
		}
		for (FilterTarget target : filterTargets) {
			if (!Const.isEmpty(target.getName())) {
				String description = BaseMessages.getString(PKG, "MDPersonatorMeta.TargetStream.FilterTarget.Description", Const.NVL(target.getName(), ""));
				StreamInterface stream = new Stream(StreamType.TARGET, target.getTargetStep(), description, StreamIcon.TARGET, target);
				ioMeta.addStream(stream);
			}
		}
	}

	/**
	 * Called to handle external stream selection
	 *
	 * @param stream
	 */
	public static void handleStreamSelection(StreamInterface stream, List<FilterTarget> filterTargets) {
		// They selected the new filter target...
		if (stream == newFilterTargetStream) {
			// Create a new filter target step
			FilterTarget target = new FilterTarget();
			target.setTargetStep(stream.getStepMeta());
			target.setName(stream.getStepMeta().getName());
			filterTargets.add(target);
		}
	}
	public static final int			MAX_TARGETS				= 4;
	private static Class<?>			PKG						= MDStreamHandler.class;
	private static StreamInterface	newFilterTargetStream	= new Stream(StreamType.TARGET, null, BaseMessages.getString(PKG, "MDPersonatorMeta.TargetStream.NewFilterTarget.Description"), StreamIcon.TARGET, null);
}
