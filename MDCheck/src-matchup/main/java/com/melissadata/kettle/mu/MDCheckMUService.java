package com.melissadata.kettle.mu;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.melissadata.cz.MDProps;
import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.mu.evaluator.Algorithm;
import com.melissadata.kettle.support.IOMeta;
import com.melissadata.kettle.support.IRequestManager;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

import com.melissadata.mdMUHybrid;
import com.melissadata.mdMUMatchcode;
import com.melissadata.mdMUReadWrite;
import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;

public class MDCheckMUService extends MDCheckService {
// private static final Class<?> PKG = MDCheckMUService.class;
	private File	keyFile;
	private String	muLicense;
	private LogChannelInterface log;
	private String  webEncoding = "ISO-8859-1";// default

	public MDCheckMUService(MDCheckStepData stepData, MDCheckData checkData, VariableSpace space, LogChannelInterface log) {
		super(stepData, checkData, space, log);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#buildRequest(com.melissadata.kettle.support.IOMeta, java.lang.Object[])
	 */
	@Override
	public MDCheckRequest buildRequest(IOMeta ioMeta, Object[] inputData) {
		// Should never be called
		throw new RuntimeException("MDCheckMUService.buildRequest() not implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#determineRequestRoute(com.melissadata.kettle.MDCheckRequest)
	 */
	@Override
	public int determineRequestRoute(MDCheckRequest request) {
		// Should never be called
		throw new RuntimeException("MDCheckMUService.determineRequestRoute() not implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#dispose()
	 */
	@Override
	public void dispose() {
		// Dispose of any objects created
		if (checkData.ReadWrite != null) {
			checkData.ReadWrite.delete();
		}
		if (checkData.Hybrid != null) {
			checkData.Hybrid.delete();
		}
		// Delete the key file
		if (keyFile != null) {
			keyFile.delete();
		}
		// Call parent last
		super.dispose();
	}

	/**
	 * Only the init and dispose methods of this service should ever be called. We initialize the
	 * matchup objects here instead of MDCheckMatchup so that the Test button can work on the
	 * advanced configuration dialog.
	 */
	@Override
	public void init() throws KettleException {
		this.log = new LogChannel(this);
		// Get the real license and data path
		muLicense = stepData.getMatchUp().getLicense();
		AdvancedConfigurationMeta acMeta = stepData.getAdvancedConfiguration();
		checkData.realDataPath = acMeta.getLocalDataPath();
		// Get the temporary work directory. If none defined then use system temp directory
		String workPath = acMeta.getWorkPath();
		if (Const.isEmpty(workPath)) {
			checkData.realWorkPath = null;
		} else {
			checkData.realWorkPath = new File(workPath);
		}
		webEncoding = MDProps.getProperty(MDPropTags.TAG_WEB_ENCODING, "ISO-8859-1");

		// Create matchup object
		initMatchUp();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#outputData(int)
	 */
	@Override
	public void outputData(List<MDCheckRequest> requests, int queue) {
		// Should never be called
		throw new RuntimeException("MDCheckMUService.outputData() not implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#processRequests()
	 */
	@Override
	public void processRequests(List<MDCheckRequest> requests, int queue, boolean chkXML, int attempts) throws KettleException {
		// Ignore if testing
		if (testing) { return; }
		// Should never be called
		throw new RuntimeException("MDCheckMUService.processRequests() not implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDCheckService#saveReports()
	 */
	@Override
	public void saveReports() throws KettleException {
		// Does nothing
	}


	private String algValid(){

		List<Algorithm> algs = stepData.getMatchUp().getAlgorithms();
		for(Algorithm a : algs){
			if(!a.isValid()){
				return "Algotithm " + a.getAlgoType() + " is selected, but the expression is empty";
			}
		}


		return "";
	}
	/**
	 * Called to initialize the matchup object
	 *
	 * @throws KettleException
	 */
	private void initMatchUp() throws KettleException {
		// Skip if not present
		MatchUpMeta matchup = stepData.getMatchUp();
		if (matchup == null) { return; }
		log.logDetailed("MatchUp Init ...");
		// Fail testing if not licensed
		if (!matchup.isLicensed()) {
			matchup.localMsg = MDCheck.getErrorString("NotLicensed");
			matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", matchup.localMsg));
			if (!testing && matchup.isEnabled()) { throw matchup.localException; }
			return;
		}
		String algError = algValid();
		if(!algError.isEmpty()){
			log.logError("ERROR - " + algError);
			matchup.localMsg = algError;// MDCheck.getErrorString("NotLicensed");
			matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", matchup.localMsg));
			if (!testing && matchup.isEnabled()) { throw matchup.localException; }
			return;
		}

		try {
			// De-reference source and lookup inputs
			List<MatchUpMeta.MapField> sourceMapping = matchup.getSourceMapping();
			List<MatchUpMeta.MapField> lookupMapping = matchup.getLookupMapping();
			checkData.UsingLookup = !Const.isEmpty(matchup.getLookupStream().getStepname());
			// Make sure the matchcode name is defiend
			if (Const.isEmpty(matchup.getMatchcodeName())) {
				initFailed = true;
				// Handle failure to initialize
				matchup.localMsg = MDCheck.getErrorString("NoMatchcode");
				matchup.localException = new KettleException(matchup.localMsg);
				if (!testing) { throw matchup.localException; }
				return;
			}
			// Initialize the Read/Write deduper - it will be used to create
			// keys from the Source pin and do the main deduping job:
			mdMUReadWrite ReadWrite;
			try {
				ReadWrite = checkData.ReadWrite = DQTObjectFactory.newMatchupReadWrite();
			} catch (DQTObjectException e) {
				initFailed = true;
				// Handle failure to initialize
				matchup.localMsg = e.getMessage();
				matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw matchup.localException; }
				return;
			}
			ReadWrite.SetLicenseString(muLicense);
			log.logDetailed("MatchUp set data path : " + checkData.realDataPath);
			ReadWrite.SetPathToMatchUpFiles(checkData.realDataPath);
			ReadWrite.SetMatchcodeName(matchup.getMatchcodeName());
			// Create a temporary key file
			try {
				keyFile = File.createTempFile("muKey", null, checkData.realWorkPath);
				log.logDetailed("MatchUp work path set to : " + checkData.realWorkPath);
			} catch (IOException e) {
				initFailed = true;
				// Handle failure to create key file
				matchup.localMsg = MDCheck.getErrorString("BadWorkPath", (checkData.realWorkPath != null) ? checkData.realWorkPath.toString() : "");
				matchup.localException = new KettleException(matchup.localMsg);
				if (!testing) { throw matchup.localException; }
				return;
			}
			keyFile.deleteOnExit();
			ReadWrite.SetKeyFile(keyFile.getAbsolutePath());
			// If we're using a suppression or intersection file, we use the 'edges' between
			// dupegroups as a signal to perform certain actions. For this to work properly, we
			// need to ensure that all records with the same dupe group are adjacent.
			// SetGroupSorting() does this. We don't do this for regular deduping, as it does
			// slow processing somewhat.
			if (checkData.UsingLookup) {
				ReadWrite.SetGroupSorting(true);
			}
			// Fix for large data files UserInfoSize
			ReadWrite.SetReserved("UserInfoSize", "32");

			if(webEncoding == "UTF-8"){
				ReadWrite.SetMaximumCharacterSize(4);
				ReadWrite.SetEncoding(webEncoding);
			}

			// Initialize
			if (ReadWrite.InitializeDataFiles() != mdMUReadWrite.ProgramStatus.ErrorNone) {
				initFailed = true;
				// Handle failure to initialize
				matchup.localMsg = MDCheck.getErrorString("InitializeService", ReadWrite.GetInitializeErrorString());
				matchup.localException = new KettleException(matchup.localMsg);
				// Sometimes when ReadWrite fails to initialize it can be put into a state where the delete()
				// method will crash the JVM. We de-reference the object so this won't happen.
				checkData.ReadWrite = null;
				// Termination handling
				if (!testing) { throw matchup.localException; }
				return;
			}
			// Establish the mappings:
			ReadWrite.ClearMappings();
			for (MatchUpMeta.MapField mapping : sourceMapping) {
				// Ignore undefined
				if (Const.isEmpty(mapping.input) /*|| (mapping.type == mdMUMatchcode.MatchcodeMapping.General)*/) {
					continue;
				}
				// Translate mapping enum between the matchcode object and the readwrite object
				mdMUReadWrite.MatchcodeMapping matchcodeMapping = MatchUpUtil.toReadWriteMatchcodeMapping(mapping.type);
				if (matchcodeMapping == null) {
					initFailed = true;
					// Handle failure to get a matchcode mapping
					matchup.localMsg = MDCheck.getErrorString("MissingMapping", mapping.type.toString());
					matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", matchup.localMsg));
					if (!testing) { throw matchup.localException; }
					return;
				}
				if (ReadWrite.AddMapping(matchcodeMapping) == 0) {
					initFailed = true;
					// Handle failure to add mapping
					matchup.localMsg = MDCheck.getErrorString("AddingMapping");
					matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", matchup.localMsg));
					if (!testing) { throw matchup.localException; }
					return;
				}
			}
			// Initialize the Hybrid deduper - it will be used to create keys
			// from the Lookup pin. This is needed because it is possible that
			// the Lookup and Source data type mappings could be quite different
			// and the keys would need to be built differently.
			//
			// An alternate to this would be to ClearMappings()/AddMapping() every
			// time a change in input pin is detected, but I think this is
			// simpler.
			mdMUHybrid Hybrid;
			try {
				Hybrid = checkData.Hybrid = DQTObjectFactory.newMatchupHybrid();
			} catch (DQTObjectException e) {
				initFailed = true;
				// Handle failure to initialize
				matchup.localMsg = e.getMessage();
				matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", e.getMessage()), e);
				if (!testing) { throw matchup.localException; }
				return;
			}
			Hybrid.SetLicenseString(muLicense);
			Hybrid.SetPathToMatchUpFiles(checkData.realDataPath);
			Hybrid.SetMatchcodeName(matchup.getMatchcodeName());
			if (Hybrid.InitializeDataFiles() != mdMUHybrid.ProgramStatus.ErrorNone) {
				initFailed = true;
				// Handle failure to initialize
				matchup.localMsg = MDCheck.getErrorString("InitializeService", Hybrid.GetInitializeErrorString());
				matchup.localException = new KettleException(matchup.localMsg);
				if (!testing) { throw matchup.localException; }
				return;
			}
			// Add lookup mappings to hybrid de-duper
			Hybrid.ClearMappings();
			if (checkData.UsingLookup) {
				for (MatchUpMeta.MapField mapping : lookupMapping) {
					// Ignore undefined
					if (Const.isEmpty(mapping.input) || (mapping.type == mdMUMatchcode.MatchcodeMapping.General)) {
						continue;
					}
					// Translate enum from matchcode object to hybrid object
					mdMUHybrid.MatchcodeMapping matchcodeMapping = MatchUpUtil.toHybridMatchcodeMapping(mapping.type);
					if (matchcodeMapping == null) {
						initFailed = true;
						// Handle failure to get a matchcode mapping
						matchup.localMsg = MDCheck.getErrorString("MissingMapping", mapping.type.toString());
						matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", matchup.localMsg));
						if (!testing) { throw matchup.localException; }
						return;
					}
					if (Hybrid.AddMapping(matchcodeMapping) == 0) {
						initFailed = true;
						// Handle failure to add mapping
						matchup.localMsg = MDCheck.getErrorString("AddingMapping");
						matchup.localException = new KettleException(MDCheck.getErrorString("InitializeService", matchup.localMsg));
						if (!testing) { throw matchup.localException; }
						return;
					}
				}
			}
		} catch (KettleException e) {
			// re-throw
			throw e;
		} catch (Throwable t) {
			initFailed = false;
			// If anything unusual happened then return an initialization failure
			matchup.localMsg = MDCheck.getErrorString("InitializeService", t.toString());
			matchup.localException = new KettleException(matchup.localMsg, t);
			if (!testing) { throw matchup.localException; }
		}
	}

	/**
	 * Matchup uses its own request manager
	 */
	@Override
	protected IRequestManager createRequestManager() {
		// No request manager here
		return null;
	}
}
