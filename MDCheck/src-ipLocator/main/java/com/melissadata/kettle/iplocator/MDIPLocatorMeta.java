package com.melissadata.kettle.iplocator;

import com.melissadata.kettle.MDCheckMeta;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;

@Step(id = "MDIPLocatorPlugin", 
image = "com/melissadata/kettle/images/PDI_MD_IPLocator_V1.svg",
description = "MDCheckPlugin.IPLocatorStep.Description",
name = "MDCheckPlugin.IPLocatorStep.Name", 
categoryDescription = "MDCheckPlugin.Category",
i18nPackageName = "com.melissadata.kettle"
)

public class MDIPLocatorMeta extends MDCheckMeta {
	public MDIPLocatorMeta() throws KettleException{
		super(MDCHECK_IPLOCATOR);
	}

}
