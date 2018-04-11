package com.melissadata.kettle.cleanser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.melissadata.cz.support.MDPropTags;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;

import com.melissadata.mdCleanser;
import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cleanser.MDCleanserEnum.AbbreviationMode;
import com.melissadata.kettle.cleanser.MDCleanserEnum.CasingMode;
import com.melissadata.kettle.cleanser.MDCleanserEnum.FieldDataType;
import com.melissadata.kettle.cleanser.MDCleanserEnum.PunctuationMode;
import com.melissadata.kettle.cleanser.MDCleanserEnum.RuleOperation;
import com.melissadata.kettle.cleanser.MDCleanserEnum.RuleOption;
import org.pentaho.di.core.variables.VariableSpace;


public class CleanserRule implements Cloneable {

	private mdCleanser				cleanser;

	private String					index;
	private RuleOperation			operation;
	private CasingMode				caseMode;
	private PunctuationMode			punctMode;
	private AbbreviationMode		abrivMode;

	private FieldDataType			dataType;
	private RuleOption				optionX;
	private List<RuleOption>		 lOptions;
	private String					abrivTargetSize			= "50";

	private boolean					useTrigger;
	private boolean					useExpressionTrigger	= true;
	private boolean					useRegExTrigger			= false;
	private String					regExTrigger			= "";
	private String					expressionTrigger		= "";

	private String					transformExpression		= "";

	private boolean					useRegEx;
	private boolean					useRegExTable;
	private String					regExSearch				= "";
	private String					regExReplace			= "";
	private String					regExTablePath			= "";

	private boolean					useSearchTerm;
	private boolean					useSearchTable;
	private String					searchTerm				= "";
	private String					replaceTerm				= "";
	private String					searchReplaceTablePath	= "";
	private LogChannelInterface		log;

	public HashMap<String, String>	columns;
	private VariableSpace space;

	private boolean					isInitalized			= false;

	public CleanserRule(String idx) {

		if (idx != null) {
			index = idx;
		}



		operation = RuleOperation.OperationTextSearchReplace;
		caseMode = CasingMode.CaseMixed;
		punctMode = PunctuationMode.PunctuationAdd;
		abrivMode = AbbreviationMode.AbbreviationContractConservative;
		dataType = FieldDataType.DataTypeGeneral;
	//	option = RuleOption.OptSingleOccurrence;
		lOptions = new ArrayList<RuleOption>();
		useSearchTerm = true;
		useRegEx = true;
	}

	@Override
	public CleanserRule clone() throws CloneNotSupportedException {
		CleanserRule clonedCo = (CleanserRule) super.clone();
		return clonedCo;
	}

	public void deleteRuleObject() {
		if (cleanser != null) {
			if ((log != null) && log.isDebug()) {
				log.logDebug("Deleating Rule object");
			}
			cleanser.delete();
		}
	}

	public String[] doCleanse(String fVal) throws KettleException, UnsupportedEncodingException {
		String result = "";
		String resultCode = "";
		byte[] bytes = null;
		if ((cleanser != null) && isInitalized) {

			if (fVal == null) {
				fVal = "";
			}

			log.logDebug("Cleansing Input = " + fVal);
			if ((operation == RuleOperation.OperationExpression) || useExpressionTrigger) {
				log.logDebug("Cleanser Setting columns");
				for (Entry<String, String> entry : columns.entrySet()) {
					log.logDebug("Setting column: " + entry.getKey() + " = " + entry.getValue());
					cleanser.SetStringColumnValue(entry.getKey(), entry.getValue());
				}
			}

			try {
				result = cleanser.Cleanse(fVal);
				resultCode = cleanser.GetResultCode();
				log.logDebug("Cleansing Result = \'" + result + "\' Result Code = " + resultCode);
			} catch (Exception e) {
				System.out.println(" ERROR : " + e.toString());
			}

		} else
			throw new KettleException("Cleanser Rule not initialized");
		
		bytes = result.getBytes("UTF-8");

		//String[] resultAr = { result, resultCode };
		String[] resultAr = { new String(bytes, "UTF-8"), resultCode };
		return resultAr;
	}

	public AbbreviationMode getAbrivMode() {
		return abrivMode;
	}

	public String getAbrivTargetSize() {
		if (abrivTargetSize == null) {
			abrivTargetSize = "25";
		}
		return abrivTargetSize;
	}

	public CasingMode getCaseMode() {
		return caseMode;
	}

	public FieldDataType getDataType() {
		return dataType;
	}

	public String getExpressionTrigger() {
		return expressionTrigger != null ? expressionTrigger : "";
	}

	public String getIndex() {
		return index;
	}

	public RuleOperation getOperation() {
		return operation;
	}

	public List<RuleOption> getOptionList() {
		return lOptions;
	}

	public PunctuationMode getPunctMode() {
		return punctMode;
	}

	public String getRegExReplace() {
		return regExReplace != null ? regExReplace : "";
	}

	public String getRegExSearch() {
		return regExSearch != null ? regExSearch : "";
	}

	public String getRegExTablePath() {
		return regExTablePath != null ? regExTablePath : "";
	}

	public String getRegExTrigger() {
		return regExTrigger != null ? regExTrigger : "";
	}

	public String getSearch_ReplaceTerm() {
		return replaceTerm != null ? replaceTerm : "";
	}

	public String getSearch_SerchTerm() {
		return searchTerm != null ? searchTerm : "";
	}

	public String getSearch_TablePath() {
		return searchReplaceTablePath != null ? searchReplaceTablePath : "";
	}

	public String getTransformExpression() {
		return transformExpression != null ? transformExpression : "";
	}

	public boolean initialize(LogChannelInterface log, VariableSpace space) throws DQTObjectException, KettleException {

		this.log = log;
		this.space = space;

		cleanser = DQTObjectFactory.newCleanser();
		String lic = getLicenseString();
		cleanser.SetLicenseString(lic);
		String dataPath = MDProps.getProperty(MDCleanserMeta.TAG_LOCAL_DATA_PATH, "");
		cleanser.SetPathToCleanserDataFiles(dataPath);

		cleanser.SetOperationMode(operation.getMDEnum());
		cleanser.SetFieldDataType(dataType.getMDEnum());
		cleanser.SetCasingMode(caseMode.getMDEnum());
		cleanser.SetAbbreviationMode(abrivMode.getMDEnum());
		cleanser.SetPunctuationMode(punctMode.getMDEnum());

		// FIXME get options
		String optionsString = lOptions.toString();
		if ((log != null) && log.isDebug()) {
			log.logDebug("Initializing Cleanser Rule : " + " Operation=" + operation + ", Field Data Type=" + dataType + ", Abbreviation Mode=" + abrivMode + ", Casing Mode=" + caseMode + ", Punctuation Mode=" + punctMode + ", Cleanse Option=" + optionsString);
		}

		if ((log != null) && log.isDebug()) {
			log.logDebug("Using Trigger = " + useTrigger);
		}
		if (useTrigger) {
			if (useExpressionTrigger) {
				if ((log != null) && log.isDebug()) {
					log.logDebug("Using Expression Trigger = " + expressionTrigger);
				}
				cleanser.SetTriggerExpression(expressionTrigger);
			}
			if (useRegExTrigger) {
				if ((log != null) && log.isDebug()) {
					log.logDebug("Using RegEx Trigger = " + regExTrigger);
				}
				cleanser.SetTriggerRegularExpression(regExTrigger);
			}
		}

		if (operation == RuleOperation.OperationExpression) {
			if ((log != null) && log.isDebug()) {
				log.logDebug("Set Transform Expression = " + transformExpression);
			}

			transformExpression = space.environmentSubstitute(transformExpression);

			cleanser.SetTransformExpression(transformExpression);
		}

		if (operation == RuleOperation.OperationTextSearchReplace) {
			if (useSearchTerm) {
				if ((log != null) && log.isDebug()) {
					log.logDebug("Set Search Replace Term: Search = " + searchTerm + " <> Replace = " + replaceTerm);
				}

				cleanser.AddSearchReplace(searchTerm, replaceTerm);
			}

			if (useSearchTable) {
				if ((log != null) && log.isDebug()) {
					log.logDebug("Set Search table path = " + searchReplaceTablePath);
				}
				cleanser.SetSearchReplaceTable(searchReplaceTablePath);
			}
		}

		if (operation == RuleOperation.OperationRegularExpression) {
			if (useRegEx) {
				if ((log != null) && log.isDebug()) {
					log.logDebug("Set Search Replace RegEX: Search = " + regExSearch + " <> Replace = " + regExReplace);
				}
				cleanser.AddRegularExpression(regExSearch, regExReplace);
			}

			if (useRegExTable) {
				if ((log != null) && log.isDebug()) {
					log.logDebug("Set RegEx table path = " + regExTablePath);
				}
				cleanser.SetRegularExpressionTable(regExTablePath);
			}
		}

		for (RuleOption option : lOptions) {
			if ((log != null) && log.isDebug()) {
				// FIXME get options string
				log.logDebug("Set Rule Option = " + option);
			}
			// TODO can we set more than one option ??
			if (option == RuleOption.OptAbbreviateTargetSize) {

				cleanser.SetCleanseOption(option.getMDEnum(), abrivTargetSize);

			} else if (option != RuleOption.OptNone) {

				cleanser.SetCleanseOption(option.getMDEnum(), "true");
			}
		}

		mdCleanser.ProgramStatus programStatus = cleanser.InitializeDataFiles();

		if (programStatus != mdCleanser.ProgramStatus.ErrorNone)
			throw new KettleException("Failed to initialize Rule: " + cleanser.GetInitializeErrorString());
		else {
			isInitalized = true;
		}

		return isInitalized;
	}


	public boolean isUseExpressionTrigger() {
		return useExpressionTrigger;
	}

	// RegEx Search and Replcae
	public boolean isUseRegEx() {
		return useRegEx;
	}

	public boolean isUseRegExTable() {
		return useRegExTable;
	}

	// trigger reg Ex
	public boolean isUseRegExTrigger() {
		return useRegExTrigger;
	}

	public boolean isUseSearchTable() {
		return useSearchTable;
	}

	// String Search & Replace
	public boolean isUseSearchTerm() {
		return useSearchTerm;
	}

	// Triggers
	public boolean isUseTrigger() {
		return useTrigger;
	}

	public void setAbrivMode(AbbreviationMode abrivMode) {
		this.abrivMode = abrivMode;
	}

	public void setAbrivTargetSize(String abrivTargetSize) {
		this.abrivTargetSize = abrivTargetSize;
	}

	public void setCaseMode(CasingMode caseMode) {
		this.caseMode = caseMode;
	}

	public void setDataType(FieldDataType dataType) {
		this.dataType = dataType;
	}

	public void setExpressionTrigger(String expressionTrigger) {
		this.expressionTrigger = expressionTrigger;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setOperation(RuleOperation operation) {
		this.operation = operation;
	}

	public void setOptionsList(List<RuleOption> options) {
		this.lOptions = options;
	}

	public void setPunctMode(PunctuationMode punctMode) {
		this.punctMode = punctMode;
	}

	public void setRegExReplace(String regExReplace) {
		this.regExReplace = regExReplace;
	}

	public void setRegExSearch(String regExSearch) {
		this.regExSearch = regExSearch;
	}

	public void setRegExTablePath(String regExTablePath) {
		this.regExTablePath = regExTablePath;
	}

	public void setRegExTrigger(String regExTrigger) {
		this.regExTrigger = regExTrigger;
	}

	public void setSearch_ReplaceTerm(String search_ReplaceTerm) {
		replaceTerm = search_ReplaceTerm;
	}

	public void setSearch_SerchTerm(String search_SerchTerm) {
		searchTerm = search_SerchTerm;
	}

	public void setSearch_TablePath(String search_TablePath) {
		searchReplaceTablePath = search_TablePath;
	}

	public void setTransformExpression(String transformExpression) {
		this.transformExpression = transformExpression;
	}

	public void setUseExpressionTrigger(boolean useExpressionTrigger) {
		this.useExpressionTrigger = useExpressionTrigger;
	}

	// trigger expression

	public void setUseRegEx(boolean useRegEx) {
		this.useRegEx = useRegEx;
	}

	public void setUseRegExTable(boolean useRegExTable) {
		this.useRegExTable = useRegExTable;
	}

	public void setUseRegExTrigger(boolean useRegExTrigger) {
		this.useRegExTrigger = useRegExTrigger;
	}

	public void setUseSearchTable(boolean useSearchTable) {
		this.useSearchTable = useSearchTable;
	}

	public void setUseSearchTerm(boolean useSearchTerm) {
		this.useSearchTerm = useSearchTerm;
	}

	public void setUseTrigger(boolean useTrigger) {
		this.useTrigger = useTrigger;
	}

	private String getLicenseString(){

		String licString = "";
		int retVal = Integer.valueOf(MDProps.getProperty(MDPropTags.TAG_PRIMARY_RET_VAL, "-1"));

		if((retVal & MDPropTags.MDLICENSE_Cleanser) != 0) {
			licString = MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "");
		} else if((retVal & MDPropTags.MDLICENSE_Community) != 0) {
			licString = MDPropTags.MD_COMMUNITY_LICENSE;
		}

		return licString.trim();
	}
}
