package com.melissadata.kettle.cv;

import com.melissadata.kettle.MDCheckMeta;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;

@Step(id = "MDCheckPlugin", 
		  image = "com/melissadata/kettle/images/PDI_MD_ContactVerify_V1.svg",
		  description = "MDCheckPlugin.FullStep.Description",
		  name = "MDCheckPlugin.FullStep.Name", 
		  categoryDescription = "MDCheckPlugin.Category",
		  i18nPackageName = "com.melissadata.kettle"
		  )
public class MDCheckFullMeta extends MDCheckMeta {

	public MDCheckFullMeta() throws KettleException {
		super(MDCHECK_FULL);
	}
}
