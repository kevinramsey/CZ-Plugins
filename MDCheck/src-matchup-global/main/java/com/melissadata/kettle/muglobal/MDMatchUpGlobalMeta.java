package com.melissadata.kettle.muglobal;

import com.melissadata.kettle.MDCheckMeta;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;

@Step(id = "MDMatchUpGlobalPlugin",
		  image = "com/melissadata/kettle/images/PDI_MD_MatchUp_V1.svg",
		  description = "MDCheckPlugin.MatchUpGlobalStep.Description",
		  name = "MDCheckPlugin.MatchUpGlobalStep.Name",
		  categoryDescription = "MDCheckPlugin.Category",
		  i18nPackageName = "com.melissadata.kettle"
		  )
public class MDMatchUpGlobalMeta extends MDCheckMeta {

	public MDMatchUpGlobalMeta() throws KettleException {
		super(MDCHECK_MATCHUP_GLOBAL);
	}
}
