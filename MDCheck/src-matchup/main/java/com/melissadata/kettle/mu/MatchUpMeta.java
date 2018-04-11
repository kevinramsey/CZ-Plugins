package com.melissadata.kettle.mu;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.kettle.mu.evaluator.Algorithm;
import com.melissadata.kettle.mu.evaluator.QualityScore;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.errorhandling.Stream;
import org.pentaho.di.trans.step.errorhandling.StreamIcon;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface.StreamType;
import org.w3c.dom.Node;

import com.melissadata.mdMUMatchcode;
import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;

public class MatchUpMeta implements Cloneable {
	public static class MapField {
		public String							input;
		public mdMUMatchcode.MatchcodeMapping	type;

		public MapField(String input, mdMUMatchcode.MatchcodeMapping type) {
			this.input = input;
			this.type = type;
		}

		public MapField(String input, String type) {
			this.input = input;
			this.type = MatchUpUtil.toMatchcodeMappingEnum(type);
		}
	}
	private static Class<?>		PKG								= MatchUpMeta.class;
	private static final String	TAG_MATCHUP						= "matchup";
	public static final String	TAG_LOOKUP_STEP					= "lookup_step";
	public static final String	TAG_SOURCE_MAPPING				= "source_mapping";
	public static final String	TAG_LOOKUP_MAPPING				= "lookup_mapping";
	public static final String	TAG_MAPPING						= "mapping";
	public static final String	TAG_MAPPING_INPUT				= "mapping_input";
	public static final String	TAG_MAPPING_TYPE				= "mapping_type";
	public static final String	TAG_MATCHCODE_NAME				= "matchcode_name";
	public static final String	TAG_ALGORYTHM_LIST				= "algorythm_list";
	public static final String	TAG_ALGORYTHM					= "algorythm";
	public static final String	TAG_ALGORYTHM_TYPE				= "algorythm_type";
	public static final String	TAG_ALGORYTHM_EXP				= "algorythm_exp";
	public static final String	TAG_ALGORYTHM_OPTION			= "algorythm_option";
	public static final String	TAG_ALGORYTHM_INDEX				= "algorythm_index";
	public static final String	TAG_ALGORYTHM_SELECTED			= "algorythm_selected";
	public static final String	TAG_QUALITYSCORE_LIST			= "qualityscore_list";
	public static final String	TAG_QUALITYSCORE				= "qualityscore";
	public static final String	TAG_QUALITYSCORE_TYPE			= "qualityscore_type";
	public static final String	TAG_QUALITYSCORE_INDEX			= "qualityscore_index";
	public static final String	TAG_QUALITYSCORE_SELECTED		= "qualityscore_selected";
	public static final String	TAG_QUALITYSCORE_RESULTFIELD	= "qualityscore_expression";
	public static final String	TAG_DUPE_GROUP					= "dupe_group";
	public static final String	TAG_DUPE_COUNT					= "dupe_count";
	public static final String	TAG_MATCHCODE_KEY				= "matchcode_key";
	public static final String	TAG_RESULT_CODES				= "result_codes";
	public static final String	TAG_LIST_SUPPRESS				= "list_suppress";
	public static final String	TAG_NO_PURGE					= "no_purge";
	// Default values for MatchUp Tab
	public static final String	MD_DUPE_GROUP					= "MU_DupeGroup";
	public static final String	MD_DUPE_COUNT					= "MU_DupeCount";
	public static final String	MD_MATCHCODE_KEY				= "MU_MatchcodeKey";
	public static final String	MD_RESULT_CODES					= "MU_ResultCodes";
	// Default lengths for output
	private static final int	MD_SIZE_DUPE_GROUP				= 8;
	private static final int	MD_SIZE_DUPE_COUNT				= 8;
	private static final int	MD_SIZE_MATCHCODE_KEY			= 1024;
	private static final int	MD_SIZE_RESULT_CODES			= 100;
	private MDCheckStepData    data;
	private String             optMatchcodeName;
	private List<Algorithm>    lsSortingAlgorithms;
	private List<QualityScore> lsQualityScores;
	private List<MapField>     sourceMappings;
	private List<MapField>     lookupMappings;
	private String             outputDupeGroup;
	private String             outputDupeCount;
	private String             outputMatchcodeKey;
	private String             outputResultCodes;
	private boolean            optListSuppress;
	private boolean            optNoPurge;
	public  boolean            muCommunity;
	public  boolean            muLite;
	public  boolean            muEnterprise;
	public static boolean		initOK							= true;
	private StreamInterface		lookupStream;
	@SuppressWarnings("unused")
	private int					fieldsAdded;
	// Info set during processing
	public String				localMsg						= "";
	// Exceptions detected during processing
	public KettleException		localException;

	public MatchUpMeta(MDCheckStepData data) {
		this.data = data;
		setDefault();
	}

	/**
	 * Checks the settings of this step and puts the findings in a remarks List.
	 *
	 * @param remarks
	 *            The list to put the remarks in @see org.pentaho.di.core.CheckResult
	 * @param stepMeta
	 *            The stepMeta to help checking
	 * @param prev
	 *            The fields coming from the previous step
	 * @param input
	 *            The input step names
	 * @param output
	 *            The output step names
	 * @param info
	 *            The fields that are used as information by the step
	 */
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		// TODO Matchup: Do something here?
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MatchUpMeta clone() throws CloneNotSupportedException {
		MatchUpMeta muClone = (MatchUpMeta) super.clone();
		List<Algorithm> clonedAlgs = new ArrayList<Algorithm>(muClone.getAlgorithms().size());
		for (Algorithm alg : muClone.getAlgorithms()) {
			clonedAlgs.add(alg.clone());
		}
		muClone.setAlgorithms(clonedAlgs);
		List<QualityScore> clonedQscores = new ArrayList<QualityScore>(muClone.getQualityScores().size());
		for (QualityScore qs : muClone.getQualityScores()) {
			clonedQscores.add(qs.clone());
		}
		muClone.setQualityScores(clonedQscores);
		return muClone;
	}

	public List<Algorithm> getAlgorithms() {
		return lsSortingAlgorithms;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURLCVS(com.melissadata.kettle.MDCheckData)
	 */
	public URL getCVSURL(MDCheckData data) {
		// Should never be called
		throw new RuntimeException("MatchUpMeta.getCVSURL() not implemented");
	}

	public String getDupeCount() {
		return outputDupeCount;
	}

	public String getDupeGroup() {
		return outputDupeGroup;
	}

	/**
	 * Called to determine the name parser output fields that will be included in the step outout record
	 *
	 * NOTE: Order of fields must match the order of fields in MDCheckMatchup.processCompleteMatchup
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {
		int start = row.size();
		// Don't add output fields if name parsing is disabled
		if (isEnabled()) {
			if (!Const.isEmpty(outputResultCodes)) {
				MDCheckMeta.getStringField(row, originName, space, outputResultCodes, MD_SIZE_RESULT_CODES);
			}
			if (!Const.isEmpty(outputDupeGroup)) {
				MDCheckMeta.getStringField(row, originName, space, outputDupeGroup, MD_SIZE_DUPE_GROUP);
			}
			if (!Const.isEmpty(outputDupeCount)) {
				MDCheckMeta.getStringField(row, originName, space, outputDupeCount, MD_SIZE_DUPE_COUNT);
			}
			// The matchcode key length depends on the configuration
			if (!Const.isEmpty(outputMatchcodeKey)) {
				AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
				String license = getLicense();
				String dataPath = acMeta.getLocalDataPath();
				// If matchcode defined then get its key length.
				int len = -1;
				if (!Const.isEmpty(optMatchcodeName)) {
					len = MatchUpUtil.getMatchcodeKeyLen(optMatchcodeName, license, dataPath);
				}
				if (len <= 0) {
					// Problem getting length, log it and return default
					data.getMeta().logBasic(MDCheck.getErrorString("BadMatchcodeKeyLen", "" + MD_SIZE_MATCHCODE_KEY));
					len = MD_SIZE_MATCHCODE_KEY;
				}
				MDCheckMeta.getStringField(row, originName, space, outputMatchcodeKey, len);
			}
		}
		// Keep a count of the number of fields we add
		fieldsAdded = row.size() - start;
		// TODO Matchup: do we need to add blank fields to output in special circumstances?
	}

	/**
	 * @return The license string for matchup
	 */
	public String getLicense() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String license = acMeta.getProdutLicense(AdvancedConfigurationMeta.MDLICENSE_MatchUp);
		// TODO get actual license for Lite
		// must be community or lite
		if (Const.isEmpty(license)) {
			license = "Z2lz/PlQ0vtEZFXvUycVvi==lj9xZarB6Gd7zQ4xOu9dOO==";
		}
		return license.trim();
	}

	public boolean getListSuppress() {
		return optListSuppress;
	}

	public List<MapField> getLookupMapping() {
		return lookupMappings;
	}

	/**
	 * Called to de-reference the lookup input stream
	 *
	 * @return
	 */
	public StreamInterface getLookupStream() {
		// Create a persistent lookup info stream if none exists
		if (lookupStream == null) {
			String description = BaseMessages.getString(PKG, "MDCheckMeta.InfoStream.Lookup.Description");
			lookupStream = new Stream(StreamType.INFO, null, description, StreamIcon.INFO, null);
		}
		return lookupStream;
	}

	public String getMatchcodeKey() {
		return outputMatchcodeKey;
	}

	public String getMatchcodeName() {
		return optMatchcodeName;
	}

	public boolean getNoPurge() {
		return optNoPurge;
	}

	public List<QualityScore> getQualityScores() {
		return lsQualityScores;
	}

	public String getResultCodes() {
		return outputResultCodes;
	}

	public List<MapField> getSourceMapping() {
		return sourceMappings;
	}

	/**
	 * Called to get the stream interface for the (optional) lookup stream
	 *
	 * @param ioMeta
	 */
	public void getStepIOMeta(StepIOMetaInterface ioMeta) {
		ioMeta.addStream(getLookupStream());
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDCheckData)
	 */
	public URL getWebURL(MDCheckData data) {
		// Should never be called
		throw new RuntimeException("MatchUpMeta.getWebURL() not implemented");
	}

	/**
	 * Returns the XML representation of the Name Parsing meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_MATCHUP)).append(Const.CR);
		// Get the name of the lookup step
		retval.append(tab).append("  ").append(XMLHandler.addTagValue(TAG_LOOKUP_STEP, getLookupStream().getStepname()));
		// Get other tags
		retval.append(tab).append("  ").append(XMLHandler.openTag(TAG_SOURCE_MAPPING));
		for (MapField entry : sourceMappings) {
			retval.append(tab).append("    ").append(XMLHandler.openTag(TAG_MAPPING));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_MAPPING_INPUT, entry.input));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_MAPPING_TYPE, entry.type.toString()));
			retval.append(tab).append("    ").append(XMLHandler.closeTag(TAG_MAPPING));
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(TAG_SOURCE_MAPPING));
		
		retval.append(tab).append("  ").append(XMLHandler.openTag(TAG_LOOKUP_MAPPING));
		for (MapField entry : lookupMappings) {
			retval.append(tab).append("    ").append(XMLHandler.openTag(TAG_MAPPING));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_MAPPING_INPUT, entry.input));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_MAPPING_TYPE, entry.type.toString()));
			retval.append(tab).append("    ").append(XMLHandler.closeTag(TAG_MAPPING));
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(TAG_LOOKUP_MAPPING));
		
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_MATCHCODE_NAME, optMatchcodeName));
		retval.append(tab).append("  ").append(XMLHandler.openTag(TAG_ALGORYTHM_LIST));
		for (Algorithm alg : lsSortingAlgorithms) {
			retval.append(tab).append("    ").append(XMLHandler.openTag(TAG_ALGORYTHM));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_ALGORYTHM_TYPE, alg.getAlgoType().encode()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_ALGORYTHM_EXP, alg.getExpression()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_ALGORYTHM_INDEX, String.valueOf(alg.getIndex())));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_ALGORYTHM_OPTION, alg.getOption()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_ALGORYTHM_SELECTED, Boolean.toString(alg.isSelected())));
			retval.append(tab).append("    ").append(XMLHandler.closeTag(TAG_ALGORYTHM));
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(TAG_ALGORYTHM_LIST));
		
		retval.append(tab).append("  ").append(XMLHandler.openTag(TAG_QUALITYSCORE_LIST));
		for (QualityScore qScore : lsQualityScores) {
			retval.append(tab).append("    ").append(XMLHandler.openTag(TAG_QUALITYSCORE));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_QUALITYSCORE_TYPE, qScore.getQualityScoreType().encode()));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_QUALITYSCORE_INDEX, String.valueOf(qScore.getIndex())));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_QUALITYSCORE_SELECTED, Boolean.toString(qScore.isSelected())));
			retval.append(tab).append("    ").append(MDCheckStepData.addTagValue(TAG_QUALITYSCORE_RESULTFIELD, qScore.getResultField()));
			retval.append(tab).append("    ").append(XMLHandler.closeTag(TAG_QUALITYSCORE));
		}
		retval.append(tab).append("  ").append(XMLHandler.closeTag(TAG_QUALITYSCORE_LIST));
		
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_DUPE_GROUP, outputDupeGroup));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_DUPE_COUNT, outputDupeCount));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_MATCHCODE_KEY, outputMatchcodeKey));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_RESULT_CODES, outputResultCodes));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_LIST_SUPPRESS, Boolean.toString(optListSuppress)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_NO_PURGE, Boolean.toString(optNoPurge)));
		retval.append(tab).append(XMLHandler.closeTag(TAG_MATCHUP)).append(Const.CR);
		return retval.toString();
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if it is licensed
		boolean isLicensed = isLicensed();
		// TODO Matchup: Enabled only if there is a source mapping?
// boolean noInputFields = sourceMappings.size() == 0;
		return isLicensed /* && !noInputFields */;
	}

	/**
	 * @return true if name parsing is licensed for this customer
	 */
	public boolean isLicensed() {
		// MatchUp is always licensed for community edition as a min
		return true;
	}

	/**
	 * Called to read name parsing meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		// Make sure the objects are loaded correctly before reading the data
		try {
			DQTObjectFactory.checkMatchup();
		} catch (DQTObjectException e) {
			e.printStackTrace(System.err);
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoMULib"), e);
		}

		// Read the data
		List<Node> nodes = XMLHandler.getNodes(node, TAG_MATCHUP);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Update info stream with lookup step name
			String lookupStepname = XMLHandler.getTagValue(node, TAG_LOOKUP_STEP);
			StreamInterface infoStream = data.getMeta().getStepIOMeta().getInfoStreams().get(0);
			infoStream.setSubject(lookupStepname);
			data.getMeta().resetStepIoMeta(); // Needed because output targets have not been defined yet
			// Read other tags
			Node mappingNodes = XMLHandler.getSubNode(node, TAG_SOURCE_MAPPING);
			int nrMappings = XMLHandler.countNodes(mappingNodes, TAG_MAPPING);
			sourceMappings = new ArrayList<MapField>(nrMappings);
			for (int i = 0; i < nrMappings; i++) {
				Node mappingNode = XMLHandler.getSubNodeByNr(mappingNodes, TAG_MAPPING, i);
				String input = MDCheckStepData.getTagValue(mappingNode, TAG_MAPPING_INPUT);
				String type = MDCheckStepData.getTagValue(mappingNode, TAG_MAPPING_TYPE);
				sourceMappings.add(new MapField(input, type));
			}
			mappingNodes = XMLHandler.getSubNode(node, TAG_LOOKUP_MAPPING);
			nrMappings = XMLHandler.countNodes(mappingNodes, TAG_MAPPING);
			lookupMappings = new ArrayList<MapField>(nrMappings);
			for (int i = 0; i < nrMappings; i++) {
				Node sourceNode = XMLHandler.getSubNodeByNr(mappingNodes, TAG_MAPPING, i);
				String input = MDCheckStepData.getTagValue(sourceNode, TAG_MAPPING_INPUT);
				String type = MDCheckStepData.getTagValue(sourceNode, TAG_MAPPING_TYPE);
				lookupMappings.add(new MapField(input, type));
			}
			Node algNodes = XMLHandler.getSubNode(node, TAG_ALGORYTHM_LIST);
			int nrAlgos = XMLHandler.countNodes(algNodes, TAG_ALGORYTHM);
			lsSortingAlgorithms = new ArrayList<Algorithm>(nrAlgos);
			for (int i = 0; i < nrAlgos; i++) {
				Node sourceNode = XMLHandler.getSubNodeByNr(algNodes, TAG_ALGORYTHM, i);
				Algorithm.AlgorithmType type = Algorithm.AlgorithmType.decode(MDCheckStepData.getTagValue(sourceNode, TAG_ALGORYTHM_TYPE));
				String exp = MDCheckStepData.getTagValue(sourceNode, TAG_ALGORYTHM_EXP);
				String option = MDCheckStepData.getTagValue(sourceNode, TAG_ALGORYTHM_OPTION);
				int index = Integer.parseInt(MDCheckStepData.getTagValue(sourceNode, TAG_ALGORYTHM_INDEX));
				boolean selected = Boolean.valueOf(MDCheckStepData.getTagValue(sourceNode, TAG_ALGORYTHM_SELECTED));
				lsSortingAlgorithms.add(new Algorithm(type, exp, option, index, selected));
			}
			if (lsSortingAlgorithms.size() == 0) {
				lsSortingAlgorithms = defaultAlgoList();
			}
			Collections.sort(lsSortingAlgorithms);
			Node qsNodes = XMLHandler.getSubNode(node, TAG_QUALITYSCORE_LIST);
			int nrQscores = XMLHandler.countNodes(qsNodes, TAG_QUALITYSCORE);
			lsQualityScores = new ArrayList<QualityScore>(nrQscores);
			for (int i = 0; i < nrQscores; i++) {
				Node sourceNode = XMLHandler.getSubNodeByNr(qsNodes, TAG_QUALITYSCORE, i);
				QualityScore.QualityScoreType type = QualityScore.QualityScoreType.decode(MDCheckStepData.getTagValue(sourceNode, TAG_QUALITYSCORE_TYPE));
				int index = Integer.parseInt(MDCheckStepData.getTagValue(sourceNode, TAG_QUALITYSCORE_INDEX));
				boolean selected = Boolean.valueOf(MDCheckStepData.getTagValue(sourceNode, TAG_QUALITYSCORE_SELECTED));
				String exp = MDCheckStepData.getTagValue(sourceNode, TAG_QUALITYSCORE_RESULTFIELD);
				lsQualityScores.add(new QualityScore(type, exp, index, selected));
			}
			if (lsQualityScores.size() == 0) {
				lsQualityScores = defaultQualityScoreList();
			}
			optMatchcodeName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_MATCHCODE_NAME), optMatchcodeName);
			outputDupeGroup = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_DUPE_GROUP), outputDupeGroup);
			outputDupeCount = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_DUPE_COUNT), outputDupeCount);
			outputMatchcodeKey = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_MATCHCODE_KEY), outputMatchcodeKey);
			outputResultCodes = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_RESULT_CODES), outputResultCodes);
			String value = MDCheckStepData.getTagValue(node, TAG_LIST_SUPPRESS);
			optListSuppress = (value != null) ? Boolean.valueOf(value) : optListSuppress;
			value = MDCheckStepData.getTagValue(node, TAG_NO_PURGE);
			optNoPurge = (value != null) ? Boolean.valueOf(value) : optNoPurge;
		} else {
			setDefault();
		}
	}

	/**
	 * Called to retrieve data from a repository
	 *
	 * @param rep
	 * @param idStep
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep) throws KettleException {
		// Make sure the objects are loaded correctly before reading the data
		try {
			DQTObjectFactory.checkMatchup();
		} catch (DQTObjectException e) {
			e.printStackTrace(System.err);
			throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoMULib"), e);
		}

		// Read lookup step name Update info stream with lookup step name
		String lookupStepname = rep.getStepAttributeString(idStep, TAG_MATCHUP + "." + TAG_LOOKUP_STEP);
		StreamInterface infoStream = data.getMeta().getStepIOMeta().getInfoStreams().get(0);
		infoStream.setSubject(lookupStepname);
		data.getMeta().resetStepIoMeta(); // Needed because output targets have not been defined yet
		// Read other tags
		String prefix = TAG_MATCHUP + "." + TAG_SOURCE_MAPPING + ".";
		int nrMappings = rep.countNrStepAttributes(idStep, prefix + TAG_MAPPING_INPUT);
		sourceMappings = new ArrayList<MapField>(nrMappings);
		for (int i = 0; i < nrMappings; i++) {
			String input = rep.getStepAttributeString(idStep, i, prefix + TAG_MAPPING_INPUT);
			String type = rep.getStepAttributeString(idStep, i, prefix + TAG_MAPPING_TYPE);
			sourceMappings.add(new MapField(input, type));
		}
		prefix = TAG_MATCHUP + "." + TAG_LOOKUP_MAPPING + ".";
		nrMappings = rep.countNrStepAttributes(idStep, prefix + TAG_MAPPING_INPUT);
		lookupMappings = new ArrayList<MapField>(nrMappings);
		for (int i = 0; i < nrMappings; i++) {
			String input = rep.getStepAttributeString(idStep, i, prefix + TAG_MAPPING_INPUT);
			String type = rep.getStepAttributeString(idStep, i, prefix + TAG_MAPPING_TYPE);
			lookupMappings.add(new MapField(input, type));
		}
		// ALGOS
		prefix = TAG_MATCHUP + "." + TAG_ALGORYTHM + ".";
		nrMappings = rep.countNrStepAttributes(idStep, prefix + TAG_ALGORYTHM_TYPE);
		lsSortingAlgorithms = new ArrayList<Algorithm>(nrMappings);
		for (int i = 0; i < nrMappings; i++) {
			Algorithm.AlgorithmType type = Algorithm.AlgorithmType.decode(rep.getStepAttributeString(idStep, i, prefix + TAG_ALGORYTHM_TYPE));
			String exp = rep.getStepAttributeString(idStep, i, prefix + TAG_ALGORYTHM_EXP);
			if (exp == null) {
				exp = "";
			}
			String option = rep.getStepAttributeString(idStep, i, prefix + TAG_ALGORYTHM_OPTION);
			if (option == null){
				option = "";
			}
			int index = Integer.parseInt(rep.getStepAttributeString(idStep, i, prefix + TAG_ALGORYTHM_INDEX));
			boolean selected = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + TAG_ALGORYTHM_SELECTED));
			lsSortingAlgorithms.add(new Algorithm(type, exp, option, index, selected));
		}
		if (lsSortingAlgorithms.size() == 0) {
			lsSortingAlgorithms = defaultAlgoList();
		}
		Collections.sort(lsSortingAlgorithms);
		prefix = TAG_MATCHUP + "." + TAG_QUALITYSCORE_LIST + ".";
		int nrQscores = rep.countNrStepAttributes(idStep, prefix + TAG_QUALITYSCORE_TYPE);
		lsQualityScores = new ArrayList<QualityScore>(nrQscores);
		for (int i = 0; i < nrQscores; i++) {
			QualityScore.QualityScoreType type = QualityScore.QualityScoreType.decode(rep.getStepAttributeString(idStep, i, prefix + TAG_QUALITYSCORE_TYPE));
			int index = Integer.parseInt(rep.getStepAttributeString(idStep, i, prefix + TAG_QUALITYSCORE_INDEX));
			boolean selected = Boolean.valueOf(rep.getStepAttributeString(idStep, i, prefix + TAG_QUALITYSCORE_SELECTED));
			String exp = rep.getStepAttributeString(idStep, i, prefix + TAG_QUALITYSCORE_RESULTFIELD);
			if(exp == null){
				exp = " ";
			}
			lsQualityScores.add(new QualityScore(type, exp, index, selected));
		}
		if (lsQualityScores.size() == 0) {
			lsQualityScores = defaultQualityScoreList();
		}
		
		optMatchcodeName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_MATCHUP + "." + TAG_MATCHCODE_NAME), optMatchcodeName);
		// output fields
		outputDupeGroup = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_MATCHUP + "." + TAG_DUPE_GROUP), outputDupeGroup);
		outputDupeCount = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_MATCHUP + "." + TAG_DUPE_COUNT), outputDupeCount);
		outputMatchcodeKey = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_MATCHUP + "." + TAG_MATCHCODE_KEY), outputMatchcodeKey);
		outputResultCodes = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_MATCHUP + "." + TAG_RESULT_CODES), outputResultCodes);
		optListSuppress = rep.getStepAttributeBoolean(idStep, TAG_MATCHUP + "." + TAG_LIST_SUPPRESS);
		optNoPurge = rep.getStepAttributeBoolean(idStep, TAG_MATCHUP + "." + TAG_NO_PURGE);
	}

	/**
	 * Called to store data in a repository
	 *
	 * @param rep
	 * @param idTransformation
	 * @param idStep
	 * @throws KettleException
	 */
	public void saveRep(Repository rep, ObjectId idTransformation, ObjectId idStep) throws KettleException {
		// Save the lookup step name
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_LOOKUP_STEP, getLookupStream().getStepname());
		// Save other tags
		String prefix = TAG_MATCHUP + "." + TAG_SOURCE_MAPPING + ".";
		int i = 0;
		for (MapField entry : sourceMappings) {
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_MAPPING_INPUT, entry.input);
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_MAPPING_TYPE, entry.type.toString());
			i++;
		}
		prefix = TAG_MATCHUP + "." + TAG_LOOKUP_MAPPING + ".";
		i = 0;
		for (MapField entry : sourceMappings) {
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_MAPPING_INPUT, entry.input);
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_MAPPING_TYPE, entry.type.toString());
			i++;
		}
		
		prefix = TAG_MATCHUP + "." + TAG_ALGORYTHM + ".";
		i = 0;
		for (Algorithm alg : lsSortingAlgorithms) {
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_ALGORYTHM_TYPE, alg.getAlgoType().encode());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_ALGORYTHM_EXP, alg.getExpression());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_ALGORYTHM_INDEX, String.valueOf(alg.getIndex()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_ALGORYTHM_OPTION, alg.getOption());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_ALGORYTHM_SELECTED, Boolean.toString(alg.isSelected()));
			i++;
		}
		
		prefix = TAG_MATCHUP + "." + TAG_QUALITYSCORE_LIST + ".";
		i = 0;
		for (QualityScore qScore : lsQualityScores) {
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_QUALITYSCORE_TYPE, qScore.getQualityScoreType().encode());
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_QUALITYSCORE_INDEX, String.valueOf(qScore.getIndex()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_QUALITYSCORE_SELECTED, Boolean.toString(qScore.isSelected()));
			rep.saveStepAttribute(idTransformation, idStep, i, prefix + TAG_QUALITYSCORE_RESULTFIELD, qScore.getResultField());
			i++;
		}
		
		
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_MATCHCODE_NAME, optMatchcodeName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_DUPE_GROUP, outputDupeGroup);
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_DUPE_COUNT, outputDupeCount);
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_MATCHCODE_KEY, outputMatchcodeKey);
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_RESULT_CODES, outputResultCodes);
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_LIST_SUPPRESS, optListSuppress);
		rep.saveStepAttribute(idTransformation, idStep, TAG_MATCHUP + "." + TAG_NO_PURGE, optNoPurge);
	}

	public void setAlgorithms(List<Algorithm> lsSortingAlgorithms) {
		this.lsSortingAlgorithms = lsSortingAlgorithms;
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	/**
	 * Called to initialized default values
	 *
	 * @throws KettleException
	 */
	public void setDefault() {
		optMatchcodeName = ""; // blank means not defined
		sourceMappings = new ArrayList<MapField>();
		lookupMappings = new ArrayList<MapField>();
		lsSortingAlgorithms = defaultAlgoList();
		lsQualityScores = defaultQualityScoreList();
		sortQualityScores();
		// customer defined defaults
		if (MDCheckData.muDefaultsSet) {
			outputDupeGroup = MDCheckData.mu_DupeGroup;
			outputDupeCount = MDCheckData.mu_DupeCount;
			outputMatchcodeKey = MDCheckData.mu_MatchcodeKey;
			outputResultCodes = MDCheckData.mu_ResultCodes;
		} else {
			outputDupeGroup = MD_DUPE_GROUP;
			outputDupeCount = MD_DUPE_COUNT;
			outputMatchcodeKey = MD_MATCHCODE_KEY;
			outputResultCodes = MD_RESULT_CODES;
		}
		// options
		optListSuppress = true;
		optNoPurge = false;
		try {
			DQTObjectFactory.checkMatchup();
		} catch (DQTObjectException e) {
			initOK = false;
			e.printStackTrace(System.err);
			// throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoMULib"), e);
		}
		int prod = data.getAdvancedConfiguration().getProducts(true);
		if ((prod & AdvancedConfigurationMeta.MDLICENSE_MatchUp) != 0) {
			MDCheckMatchup.hasLimit = false;
			MDCheckMatchup.recordLimit = 0;
		} else if ((prod & AdvancedConfigurationMeta.MDLICENSE_MatchUpLite) != 0) {
			MDCheckMatchup.hasLimit = true;
			MDCheckMatchup.recordLimit = 1000000;
		} else {
			MDCheckMatchup.hasLimit = true;
			MDCheckMatchup.recordLimit = 50000;
		}
	}

	public void setDupeCount(String s) {
		outputDupeCount = s;
	}

	public void setDupeGroup(String s) {
		outputDupeGroup = s;
	}

	public void setListSuppress(boolean b) {
		optListSuppress = b;
	}

	public void setLookupMapping(List<MapField> l) {
		lookupMappings = new ArrayList<MatchUpMeta.MapField>(l);
	}

	/**
	 * Called to define the lookup step
	 *
	 * @param lookupStep
	 */
	public void setLookupStep(StepMeta lookupStep) {
		getLookupStream().setStepMeta(lookupStep);
	}

	public void setMatchcodeKey(String s) {
		outputMatchcodeKey = s;
	}

	public void setMatchcodeName(String s) {
		optMatchcodeName = s;
	}

	public void setNoPurge(boolean b) {
		optNoPurge = b;
	}

	public void setQualityScores(List<QualityScore> qScores) {
		lsQualityScores = qScores;
	}

	public void setResultCodes(String s) {
		outputResultCodes = s;
	}

	public void setSourceMapping(List<MapField> l) {
		sourceMappings = new ArrayList<MatchUpMeta.MapField>(l);
	}

	public void sortQualityScores() {
		Collections.sort(lsQualityScores);
	}

	/**
	 * Called to validate settings before saving
	 *
	 * @param warnings
	 * @param errors
	 */
	public void validate(List<String> warnings, List<String> errors) {
		// Validate only if licensed and a matchcode is specified
		if (isLicensed() && !Const.isEmpty(getMatchcodeName())) {
			mdMUMatchcode mc = null;
			try {
				// Get a matchcode object to do validation with
				int rc[] = new int[1];
				AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
				try {
					mc = MatchUpUtil.getMatchcodeObject(getMatchcodeName(), acMeta.getLocalDataPath(), rc);
				} catch (DQTObjectException e) {
					e.printStackTrace(System.err);
					errors.add(BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeComponentProblem.Message", getMatchcodeName(), e.getMessage()));
					return;
				}
				if (rc[0] == 0) {
					errors.add(BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeComponentProblem.Message", getMatchcodeName(), mc.GetInitializeErrorString()));
					return;
				}
				// Validate the source mappings (if any) We do not validate Addr lines 2-8
				List<MapField> mapping = getSourceMapping();
				for (int i = 0; (i < mapping.size()) && (i < mc.GetMappingItemCount()); i++) {
					if ((mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address2Type)
							&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address3Type)
							&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address4Type)
							&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address5Type)
							&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address6Type)
							&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address7Type)
							&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address8Type)
							&& Const.isEmpty(mapping.get(i).input)) {
						errors.add(MDCheckDialog.getValidationMessage("MatchupNoSourceMappingSpecified", mc.GetMappingItemLabel(i + 1)));
					}
				}
				// TODO: Validate against the hybrid object
// hybrid.ClearMappings();
// for (int i = 0; i < SourceMappings.Count && i < matchcode.GetMappingItemCount(); i++)
// if (SourceMappings[i].InputField != "" && !hybrid.AddMapping((mdHybrid.eMatchcodeMapping)SourceMappings[i].DataType))
// retVal = AppendErrorInfo(retVal, new ErrorInfo(eErrorType.Error, "Field Mapping", string.Format("dgSourceMapping:{0}:2",
// i), string.Format("Invalid Data Type has been mapped to {0}.", matchcode.GetMappingItemLabel(i + 1)),
// "Invalid Mapping Specified"));
				// Validate the lookup mappings (if any)
				StreamInterface lookupStream = getLookupStream();
				String lookupName = (lookupStream != null) ? Const.NVL(lookupStream.getStepname(), "") : "";
				if (!Const.isEmpty(lookupName)) {
					mapping = getLookupMapping();
					for (int i = 0; (i < mapping.size()) && (i < mc.GetMappingItemCount()); i++) {
						if ((mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address2Type)
								&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address3Type)
								&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address4Type)
								&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address5Type)
								&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address6Type)
								&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address7Type)
								&& (mc.GetMappingItemType(i + 1) != mdMUMatchcode.MatchcodeMappingTarget.Address8Type)
								&& Const.isEmpty(mapping.get(i).input)) {
							warnings.add(MDCheckDialog.getValidationMessage("MatchupNoLookupMappingSpecified", mc.GetMappingItemLabel(i + 1)));
						}
					}
					// TODO: Validate against the hybrid object
// hybrid.ClearMappings();
// for (int i = 0; i < LookupMappings.Count && i < matchcode.GetMappingItemCount(); i++)
// if (LookupMappings[i].InputField != "" && !hybrid.AddMapping((mdHybrid.eMatchcodeMapping)LookupMappings[i].DataType))
// retVal = AppendErrorInfo(retVal, new ErrorInfo(eErrorType.Error, "Field Mapping", string.Format("dgLookupMapping:{0}:2",
// i), string.Format("Invalid Data Type has been mapped to {0}.", matchcode.GetMappingItemLabel(i + 1)),
// "Invalid Mapping Specified"));
				}
			} finally {
				if (mc != null) {
					mc.delete();
				}
			}
		}
	}

	// AlgorythmType type, String exp, boolean hiest, int index
	private List<Algorithm> defaultAlgoList() {
		List<Algorithm> dList = new ArrayList<Algorithm>();
		dList.add(new Algorithm(Algorithm.AlgorithmType.LASTUPDATED, "", "Latest", 1, false));
		dList.add(new Algorithm(Algorithm.AlgorithmType.MOSTCOMPLETE, "", " ", 2, false));
		dList.add(new Algorithm(Algorithm.AlgorithmType.DATAQUALITYSCORE, "", " ", 3, false));
		dList.add(new Algorithm(Algorithm.AlgorithmType.CUSTOM, "", "Highest", 4, false));
		return dList;
	}

	private List<QualityScore> defaultQualityScoreList() {
		List<QualityScore> dList = new ArrayList<QualityScore>();
		dList.add(new QualityScore(QualityScore.QualityScoreType.DataQualityScore, "", 1, false));
		dList.add(new QualityScore(QualityScore.QualityScoreType.AddressQualityScore, "", 2, false));
		dList.add(new QualityScore(QualityScore.QualityScoreType.NameQualityScore, "", 3, false));
		dList.add(new QualityScore(QualityScore.QualityScoreType.PhoneQualityScore, "", 4, false));
		dList.add(new QualityScore(QualityScore.QualityScoreType.EmailQualityScore, "", 5, false));
		dList.add(new QualityScore(QualityScore.QualityScoreType.GeoCodeQualityScore, "", 6, false));
		return dList;
	}
}
