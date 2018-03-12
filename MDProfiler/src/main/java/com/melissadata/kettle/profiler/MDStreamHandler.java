package com.melissadata.kettle.profiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.errorhandling.Stream;
import org.pentaho.di.trans.step.errorhandling.StreamIcon;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface.StreamType;

public class MDStreamHandler {
	/**
	 * Called to get an optional new target stream
	 * @return
	 */
	public static List<StreamInterface> getOptionalStreams(Collection<FilterTarget> collection) {
		List<StreamInterface> list = new ArrayList<StreamInterface>();
		for (FilterTarget ft : collection) {
			if (ft.getTargetStep() == null) {
				String description = BaseMessages.getString(PKG, "MDProfilerMeta.TargetStream.FilterTarget.Description", Const.NVL(ft.getName(), ""));
				StreamInterface stream = new Stream(StreamType.TARGET, ft.getTargetStep(), description, StreamIcon.TARGET, ft);
				list.add(stream);
			}
		}
		return list;
	}

	/**
	 * Called to define the meta data for the output targets
	 *
	 * @param ioMeta
	 */
	public static void getStepIOMeta(StepIOMetaInterface ioMeta, Collection<FilterTarget> filterTargets, List<StepMeta> usedSteps) {
		// Add the targets...
		if (filterTargets == null) {
			filterTargets = new ArrayList<FilterTarget>();
		}
		for (FilterTarget target : filterTargets) {
			if (target.getTargetStep() != null) {
				String description = BaseMessages.getString(PKG, "MDProfilerMeta.TargetStream.FilterTarget.Description", Const.NVL(target.getName(), ""));
				StreamInterface stream = new Stream(StreamType.TARGET, target.getTargetStep(), description, StreamIcon.TARGET, target);
				ioMeta.addStream(stream);
			} else if (!Const.isEmpty(target.getTargetStepname())) {
				if (usedSteps != null) {
					StepMeta stm = StepMeta.findStep(usedSteps, target.getTargetStepname());
					if (stm != null) {
						target.setTargetStep(stm);
						String description = BaseMessages.getString(PKG, "MDProfilerMeta.TargetStream.FilterTarget.Description", Const.NVL(target.getName(), ""));
						StreamInterface stream = new Stream(StreamType.TARGET, target.getTargetStep(), description, StreamIcon.TARGET, target);
						ioMeta.addStream(stream);
					}
				}
			}
		}
	}
	/**
	 * Called to handle external stream selection
	 *
	 * @param stream
	 * @throws KettleException
	 */
	public static final int	MAX_TARGETS	= 1;
	private static Class<?>	PKG			= MDStreamHandler.class;
}
