package com.melissadata.kettle.sm;

import com.melissadata.kettle.MDCheckMeta;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;

@Step(id = "MDSmartMoverPlugin", 
		  image = "com/melissadata/kettle/images/PDI_MD_SmartMover_V1.svg",
		  description = "MDCheckPlugin.SmartMoverStep.Description",
		  name = "MDCheckPlugin.SmartMoverStep.Name", 
		  categoryDescription = "MDCheckPlugin.Category",
		  i18nPackageName = "com.melissadata.kettle"
		  )
public class MDSmartMoverMeta extends MDCheckMeta {

	public MDSmartMoverMeta() throws KettleException {
		super(MDCHECK_SMARTMOVER);
	}

}
