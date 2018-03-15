package com.melissadata.kettle.mu;

import com.melissadata.kettle.MDCheckMeta;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;

@Step(id = "MDMatchUpPlugin",
		  image = "com/melissadata/kettle/images/PDI_MD_MatchUp_V1.svg",
		  description = "MDCheckPlugin.MatchUpStep.Description",
		  name = "MDCheckPlugin.MatchUpStep.Name",
		  categoryDescription = "MDCheckPlugin.Category",
		  i18nPackageName = "com.melissadata.kettle"
		  )
public class MDMatchUpMeta extends MDCheckMeta {

	public MDMatchUpMeta() throws KettleException {
		super(MDCHECK_MATCHUP);
	}
}
