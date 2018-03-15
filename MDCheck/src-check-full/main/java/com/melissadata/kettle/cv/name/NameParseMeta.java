package com.melissadata.kettle.cv.name;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.request.LocalRequestHandler;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.cv.MDCheckWebService;
import org.json.simple.JSONObject;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.melissadata.mdName;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class NameParseMeta implements WebRequestHandler, LocalRequestHandler, Cloneable {
	public enum GenderAggression {
		Aggressive(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Aggressive"), 1),
		Neutral(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Neutral"), 2),
		Conservative(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Conservative"), 3), ;
		public static GenderAggression decode(String value) throws KettleException {
			try {
				// Handle old method of storing name order hint by web ranking
				int rank = Integer.valueOf(value);
				for (GenderAggression genderAggression : GenderAggression.values()) {
					if (genderAggression.rank == rank) { return genderAggression; }
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return GenderAggression.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Unknown") + value, e);
			}
		}
		private String	description;
		private int		rank;

		private GenderAggression(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.Aggression getMDAggression() {
			switch (this) {
				case Aggressive:
					return mdName.Aggression.Aggressive;
				case Neutral:
					return mdName.Aggression.Neutral;
				case Conservative:
					return mdName.Aggression.Conservative;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Unknown") + this);
		}

		public int getRank() {
			return rank;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum GenderPopulation {
		BiasTowardMale(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.BiasTowardMale"), 1),
		EvenlySplit(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.EvenlySplit"), 2),
		BiasTowardFemale(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.BiasTowardFemale"), 3), ;
		public static GenderPopulation decode(String value) throws KettleException {
			// Handle old method of storing name order hint by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (GenderPopulation genderPopulation : GenderPopulation.values()) {
					if (genderPopulation.rank == rank) { return genderPopulation; }
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return GenderPopulation.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.Unknown") + value, e);
			}
		}
		private String	description;
		private int		rank;

		private GenderPopulation(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.Population getMDPopulation() {
			switch (this) {
				case BiasTowardMale:
					return mdName.Population.Male;
				case EvenlySplit:
					return mdName.Population.Mixed;
				case BiasTowardFemale:
					return mdName.Population.Female;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.Unknown") + this);
		}

		public int getRank() {
			return rank;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum MiddleNameLogic {
		ParseLogic(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.ParseLogic"), 1),
		HyphenatedLast(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.HyphenatedLast"), 2),
		MiddleName(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.MiddleName"), 3), ;
		public static MiddleNameLogic decode(String value) throws KettleException {
			// Handle old method of storing by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (MiddleNameLogic logic : MiddleNameLogic.values()) {
					if (logic.rank == rank) { return logic; }
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return MiddleNameLogic.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.Unknown") + value, e);
			}
		}
		private String	description;
		private int		rank;

		private MiddleNameLogic(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.MiddleNameLogic getMDMiddleNameLogic() {
			switch (this) {
				case ParseLogic:
					return mdName.MiddleNameLogic.ParseLogic;
				case HyphenatedLast:
					return mdName.MiddleNameLogic.HyphenatedLast;
				case MiddleName:
					return mdName.MiddleNameLogic.MiddleName;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.Unknown") + this);
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum NameOrderHint {
		DefinitelyFull(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.DefinitelyFull"), 1),
		VeryLikelyFull(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.VeryLikelyFull"), 2),
		ProbablyFull(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.ProbablyFull"), 3),
		Varying(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.Varying"), 4),
		ProbablyInverse(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.ProbablyInverse"), 5),
		VeryLikelyInverse(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.VeryLikelyInverse"), 6),
		DefinitelyInverse(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.DefinitelyInverse"), 7),
		MixedFirstName(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.MixedFirstName"), 8),
		MixedLastName(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.MixedLastName"), 9), ;
		public static NameOrderHint decode(String value) throws KettleException {
			// Handle old method of storing name order hint by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (NameOrderHint nameOrderHint : NameOrderHint.values()) {
					if (nameOrderHint.rank == rank) { return nameOrderHint; }
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderRank.Unknown") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return NameOrderHint.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.Unknown") + value, e);
			}
		}
		private String	description;
		private int		rank;

		private NameOrderHint(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.NameHints getMDNameHint() {
			switch (this) {
				case DefinitelyFull:
					return mdName.NameHints.DefinitelyFull;
				case VeryLikelyFull:
					return mdName.NameHints.VeryLikelyFull;
				case ProbablyFull:
					return mdName.NameHints.ProbablyFull;
				case Varying:
					return mdName.NameHints.Varying;
				case ProbablyInverse:
					return mdName.NameHints.ProbablyInverse;
				case VeryLikelyInverse:
					return mdName.NameHints.VeryLikelyInverse;
				case DefinitelyInverse:
					return mdName.NameHints.DefinitelyInverse;
				case MixedFirstName:
					return mdName.NameHints.MixedFirstName;
				case MixedLastName:
					return mdName.NameHints.MixedLastName;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.NameHint.Unknown") + this);
		}

		public int getRank() {
			return rank;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum Salutation {
		Formal(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Formal"), 1),
		Informal(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Informal"), 2),
		FirstLast(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.FirstLast"), 3),
		Slug(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Slug"), 4),
		Blank(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Blank"), 5), ;
		public static Salutation decode(String value) throws KettleException {
			// Handle old method of storing by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (Salutation salutation : Salutation.values()) {
					if (salutation.rank == rank) { return salutation; }
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return Salutation.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Unknown") + value, e);
			}
		}

		public static Salutation[] decodeArray(String value) throws KettleException {
			String values[] = value.split(",");
			Salutation[] salutations = new Salutation[values.length];
			for (int i = 0; i < values.length; i++) {
				salutations[i] = Salutation.decode(values[i]);
			}
			return salutations;
		}

		public static String encode(Salutation[] salutations) {
			StringBuffer buf = new StringBuffer();
			String sep = "";
			for (Salutation salutation : salutations) {
				buf.append(sep).append(salutation.encode());
				sep = ",";
			}
			return buf.toString();
		}
		private String	description;
		private int		rank;

		private Salutation(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String getDescription() {
			return description;
		}

		public mdName.Salutations getMDSalutation() {
			switch (this) {
				case Formal:
					return mdName.Salutations.Formal;
				case Informal:
					return mdName.Salutations.Informal;
				case FirstLast:
					return mdName.Salutations.FirstLast;
				case Slug:
					return mdName.Salutations.Slug;
				case Blank:
					return mdName.Salutations.Blank;
			}
			// This shouldn't happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Unknown") + this);
		}

		@Override
		public String toString() {
			return getDescription();
		}

		private String encode() {
			return name();
		}
	}
	private static Class<?>		PKG							= NameParseMeta.class;
	private static final String	TAG_NAME_PARSE				= "name_parse";
	public static final String	TAG_FULLNAME				= "full_name";
	public static final String	TAG_COMPANYNAME				= "company_name";
	public static final String	TAG_PREFIX					= "prefix";
	public static final String	TAG_FIRST_NAME				= "first_name";
	public static final String	TAG_MIDDLE_NAME				= "middle_name";
	public static final String	TAG_LAST_NAME				= "last_name";
	public static final String	TAG_SUFFIX					= "suffix";
	public static final String	TAG_GENDER					= "gender";
	public static final String	TAG_SALUTATION				= "salutation";
	public static final String	TAG_STANDARDIZED_COMPANY	= "standardized_company";
	public static final String	TAG_CORRECT_MISPELLINGS		= "correct_mispellings";
	public static final String	TAG_NAME_ORDER_HINT			= "name_order_hint";
	public static final String	TAG_GENDER_AGGRESSION		= "gender_aggression";
	public static final String	TAG_GENDER_POPULATION		= "gender_population";
	public static final String	TAG_SALUTATION_PREFIX		= "salutation_prefix";
	public static final String	TAG_SALUTATION_SUFFIX		= "salutation_suffix";
	public static final String	TAG_SALUTATION_SLUG			= "salutation_slug";
	public static final String	TAG_MIDDLE_NAME_LOGIC		= "middle_name_logic";
	public static final String	TAG_SALUTATION_ORDER		= "salutation_order";
	// Default values for Name Parse Tab
	public static final String	MD_PREFIX					= "MD_Prefix";
	public static final String	MD_FIRSTNAME				= "MD_FirstName";
	public static final String	MD_MIDDLENAME				= "MD_MiddleName";
	public static final String	MD_LASTNAME					= "MD_LastName";
	public static final String	MD_SUFFIX					= "MD_Suffix";
	public static final String	MD_GENDER					= "MD_Gender";
	public static final String	MD_SALUTATION				= "MD_Salutation";
	public static final String	MD_STANDARDIZED_COMPANY		= "MD_Standardized_Company";
	// Default lengths for output
	private static final int	MD_SIZE_PREFIX1				= 20;
	private static final int	MD_SIZE_FIRSTNAME1			= 35;
	private static final int	MD_SIZE_MIDDLENAME1			= 20;
	private static final int	MD_SIZE_LASTNAME1			= 35;
	private static final int	MD_SIZE_SUFFIX1				= 20;
	private static final int	MD_SIZE_GENDER1				= 1;
	private static final int	MD_SIZE_SALUTATION			= 50;
	private static final int	MD_SIZE_COMPANY				= 50;
	private MDCheckStepData data;
	private boolean         initializeOK;
	private String          initializeWarn;
	public  String          initializeError;
	private String          inputFullName;
	private String          inputCompanyName;
	private String[]			outputPrefix				= new String[2];
	private String[]			outputFirstName				= new String[2];
	private String[]			outputMiddleName			= new String[2];
	private String[]			outputLastName				= new String[2];
	private String[]			outputSuffix				= new String[2];
	private String[]			outputGender				= new String[2];
	private String				outputSalutation;
	private String				outputStandardizedCompany;
	private boolean				correctMispellings;
	private NameOrderHint		nameOrderHint;
	private GenderAggression	genderAggression;
	private GenderPopulation	genderPopulation;
	private String				salutationPrefix;
	private String				salutationSuffix;
	private String				salutationSlug;
	private MiddleNameLogic		middleNameLogic;
	private Salutation[]		salutationOrder;
	private int					fieldsAdded;
	// Info set during processing
	public String				localMsg					= "";
	public String				localDBDate					= "";
	public String				localDBExpiration			= "";
	public String				localDBBuildNo				= "";
	public String				webMsg						= "";
	public String				webVersion					= "";
	// Exceptions detected during processing
	public KettleException		localException;
	public KettleException		webException;

	public NameParseMeta(MDCheckStepData data) {
		this.data = data;
		setDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#buildWebRequest(org.w3c.dom.Document, com.melissadata.kettle.MDCheckData,
	 * boolean)
	 */
	public boolean buildWebRequest(Document xmlDoc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		Element root = xmlDoc.getDocumentElement();
		// Name parsing options
		MDCheckWebService.addTextNode(xmlDoc, root, "OptCorrectSpelling", correctMispellings ? "True" : "False");
		MDCheckWebService.addTextNode(xmlDoc, root, "OptNameHint", "" + nameOrderHint.getRank());
		MDCheckWebService.addTextNode(xmlDoc, root, "OptGenderAggression", "" + genderAggression.getRank());
		MDCheckWebService.addTextNode(xmlDoc, root, "OptGenderPopulation", "" + genderPopulation.getRank());
		MDCheckWebService.addTextNode(xmlDoc, root, "OptSalutationPrefix", salutationPrefix);
		MDCheckWebService.addTextNode(xmlDoc, root, "OptSalutationSuffix", salutationSuffix);
		MDCheckWebService.addTextNode(xmlDoc, root, "OptSalutationSlug", salutationSlug);
		if (false) {
			// These don't exist in web service (yet)
			MDCheckWebService.addTextNode(xmlDoc, root, "OptMiddleNameLogic", middleNameLogic.encode());
			MDCheckWebService.addTextNode(xmlDoc, root, "OptSalutationOrder", Salutation.encode(salutationOrder));
		}
		// Add records
		boolean sendRequest = false;
		// If testing then add a fake record
		if (testing) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			root.appendChild(record);
			MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "1");
			MDCheckWebService.addTextNode(xmlDoc, record, "FullName", "Thomas Jefferson");
			// If there is at least one record then send the request
			sendRequest = true;
		} else {
			// Otherwise, add real records
			for (int recordID = 0; recordID < requests.size(); recordID++) {
				MDCheckRequest request = requests.get(recordID);
				RowMetaInterface inputMeta = request.inputMeta;
				Object[] inputData = request.inputData;
				// Get fullname field (if defined)
				String fullName = null;
				String companyName = null;
				if (!Const.isEmpty(inputFullName)) {
					fullName = inputMeta.getString(inputData, inputFullName, "");
					// Track for reporting
					if (this.data.isReportEnabled()) {
						if (Const.isEmpty(fullName)) {
							data.numNameBlanks = data.numNameBlanks + 1;
						}
					}
				} else if (!Const.isEmpty(companyName)) {
					companyName = inputMeta.getString(inputData, inputCompanyName, "");
					// Track for reporting
					if (this.data.isReportEnabled()) {
						if (Const.isEmpty(companyName)) {
							data.numCompanyNameBlanks = data.numCompanyNameBlanks + 1;
						}
					}
				}
				if (Const.isEmpty(fullName)) {
					fullName = " ";
				}
				// Valid if there is a full name value
				boolean valid = !Const.isEmpty(fullName);
				// TODO: when web service has company name standardization modify it here
				// If valid then add it to the request
				if (valid) {
					// Create new record object
					Element record = xmlDoc.createElement("Record");
					root.appendChild(record);
					// Add unique record id
					MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);
					// Add request fields
					MDCheckWebService.addTextNode(xmlDoc, record, "FullName", fullName);
					// If there is at least one record then send the request
					sendRequest = true;
				}
			}
		}
		return sendRequest;
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
		// TODO: Do something here?
	}

	/**
	 * Called to validate initialization of the name parser
	 *
	 * @param checker
	 */
	public void checkInit(Validations checker) {
		boolean initialOK = true;
		initializeError = "";
		initializeWarn = "";
		mdName Name = null;
		try {
			// Create temporary name object
			Name = DQTObjectFactory.newName();
			// Configure it
			AdvancedConfigurationMeta advConfig = data.getAdvancedConfiguration();
			Name.SetLicenseString(getLicense());
			Name.SetPathToNameFiles(advConfig.getLocalDataPath());
			if (Name.InitializeDataFiles() == mdName.ProgramStatus.NoError) {
				if (Validations.isDataExpiring(Name.GetDatabaseExpirationDate())) {
					checker.warnings.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.NameMeta.Error.DataExpiring") + Name.GetDatabaseExpirationDate());
					if (checker.checkTimeStamp(checker.getNameTimeStamp())) {
						initializeWarn = BaseMessages.getString(MDCheck.class, "MDCheckMeta.NameMeta.Error.DataExpiring") + Name.GetDatabaseExpirationDate();
						checker.showWarnings = true;
						checker.somethingToShow = true;
					}
					checker.setNameTimeStamp(checker.getTodayStamp());
				}
			} else {
				if (isEnabled()) {
					checker.errors.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.NameMeta.Error.NameObject") + Name.GetInitializeErrorString());
				} else {
					checker.warnings.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.NameMeta.Error.NameObject") + Name.GetInitializeErrorString());
				}
				if (checker.checkTimeStamp(checker.getNameTimeStamp())) {
					initializeError = BaseMessages.getString(MDCheck.class, "MDCheckMeta.NameMeta.Error.NameObject") + Name.GetInitializeErrorString();
					checker.showErrors = true;
					checker.somethingToShow = true;
					checker.setNameTimeStamp(checker.getTodayStamp());
				}
				initialOK = false;
			}
			setInitializeOK(initialOK);
			// initializeOK = initialOK;
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			initializeError = BaseMessages.getString(MDCheck.class, "MDCheckMeta.NameMeta.Error.NameObject") + t.getLocalizedMessage();
			checker.showErrors = true;
			checker.somethingToShow = true;
			checker.setNameTimeStamp(checker.getTodayStamp());
		} finally {
			if (Name != null) {
				Name.delete();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public NameParseMeta clone() throws CloneNotSupportedException {
		NameParseMeta data = (NameParseMeta) super.clone();
		data.outputPrefix = data.outputPrefix.clone();
		data.outputFirstName = data.outputFirstName.clone();
		data.outputMiddleName = data.outputMiddleName.clone();
		data.outputLastName = data.outputLastName.clone();
		data.outputSuffix = data.outputSuffix.clone();
		data.outputGender = data.outputGender.clone();
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#doLocalRequest(com.melissadata.kettle.MDCheckData)
	 */
	public synchronized void doLocalRequests(MDCheckData data, List<MDCheckRequest> requests) throws KettleValueException {
		// Skip if not enabled
		if (!isEnabled() && !isCompanyEnabled()) { return; }
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Get the single request
			RowMetaInterface inputMeta = request.inputMeta;
			Object[] inputData = request.inputData;
			// Get the intermediate name results
			MDCheckCVRequest.NameResults nameResults = request.nameResults = new MDCheckCVRequest.NameResults();
			// Get fullname field (if defined)
			String fullName = "";
			String companyName = "";
			if (!Const.isEmpty(inputFullName)) {
				fullName = inputMeta.getString(inputData, inputFullName, "");
				// Track for reporting
				if (this.data.isReportEnabled()) {
					if (Const.isEmpty(fullName)) {
						data.numNameBlanks = data.numNameBlanks + 1;
					}
				}
			}
			if (!Const.isEmpty(inputCompanyName)) {
				companyName = inputMeta.getString(inputData, inputCompanyName, "");
				// Track for reporting
				if (this.data.isReportEnabled()) {
					if (Const.isEmpty(companyName)) {
						data.numCompanyNameBlanks = data.numCompanyNameBlanks + 1;
					}
				}
			}
			if (Const.isEmpty(fullName) && isEnabled()) {
				fullName = " ";
			}
			// Valid if there is a full name value
			boolean valid = !Const.isEmpty(fullName);
			boolean isCompany = !Const.isEmpty(companyName);
			// Get reference to name object
			mdName Name = data.Name;
			// If valid then add it to the request
			if (valid) {
				// initialize name object
				Name.ClearProperties();
				// Set request fields
				if (!Const.isEmpty(fullName)) {
					Name.SetFullName(fullName);
				}
				// Perform the request
				Name.Parse();
				// Always get result codes for the individual request
				nameResults.resultCodes.addAll(MDCheck.getResultCodes(Name.GetResults()));
			}
			if (isCompany) {
				Name.ClearProperties();
				if (!Const.isEmpty(companyName)) {
					String afterCoName = "";
					afterCoName = Name.StandardizeCompany(companyName);
					if (!afterCoName.equals(companyName)) {
						companyName = afterCoName;
						// fake result code
						nameResults.resultCodes.add("NS99");
					} else {
						nameResults.resultCodes.add("NE99");
					}
				}
			}
			// If request was valid then get the results
			if (valid) {
				// Extract results
				nameResults.Prefix = Name.GetPrefix();
				nameResults.First = Name.GetFirstName();
				nameResults.Middle = Name.GetMiddleName();
				nameResults.Last = Name.GetLastName();
				nameResults.Suffix = Name.GetSuffix();
				nameResults.Gender = Name.GetGender();
				nameResults.Prefix2 = Name.GetPrefix2();
				nameResults.First2 = Name.GetFirstName2();
				nameResults.Middle2 = Name.GetMiddleName2();
				nameResults.Last2 = Name.GetLastName2();
				nameResults.Suffix2 = Name.GetSuffix2();
				nameResults.Gender2 = Name.GetGender2();
				nameResults.Salutation = Name.GetSalutation();
				// TODO: More validity checks
				nameResults.valid = true;
			}
			if (isCompany) {
				nameResults.StandardCompanyName = companyName;
				nameResults.valid = true;
			}
		}
	}

	public boolean getCorrectMispellings() {
		return correctMispellings;
	}

	/**
	 * @return Name Parser Customer ID
	 */
	public String getCustomerID() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String customerID = acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_Name);
		return customerID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURLCVS(com.melissadata.kettle.MDCheckData)
	 */
	public URL getCVSURL(MDCheckData data) {
		return data.realCVSNameParserURL;
	}

	/**
	 * Called to determine the name parser output fields that will be included in the step outout record
	 *
	 * NOTE: Order of fields must match the order of fields in getResponseValues
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {
		int start = row.size();
		// Don't add output fields if name parsing is disabled
		if (isEnabled()) {
			for (int i = 0; i < 2; i++) {
				if (!Const.isEmpty(outputPrefix[i])) {
					MDCheckMeta.getStringField(row, originName, space, outputPrefix[i], MD_SIZE_PREFIX1);
				}
				if (!Const.isEmpty(outputFirstName[i])) {
					MDCheckMeta.getStringField(row, originName, space, outputFirstName[i], MD_SIZE_FIRSTNAME1);
				}
				if (!Const.isEmpty(outputMiddleName[i])) {
					MDCheckMeta.getStringField(row, originName, space, outputMiddleName[i], MD_SIZE_MIDDLENAME1);
				}
				if (!Const.isEmpty(outputLastName[i])) {
					MDCheckMeta.getStringField(row, originName, space, outputLastName[i], MD_SIZE_LASTNAME1);
				}
				if (!Const.isEmpty(outputSuffix[i])) {
					MDCheckMeta.getStringField(row, originName, space, outputSuffix[i], MD_SIZE_SUFFIX1);
				}
				if (!Const.isEmpty(outputGender[i])) {
					MDCheckMeta.getStringField(row, originName, space, outputGender[i], MD_SIZE_GENDER1);
				}
			}
			if (!Const.isEmpty(outputSalutation)) {
				MDCheckMeta.getStringField(row, originName, space, outputSalutation, MD_SIZE_SALUTATION);
			}
		}
		if (isCompanyEnabled()) {
			if (!Const.isEmpty(outputStandardizedCompany)) {
				MDCheckMeta.getStringField(row, originName, space, outputStandardizedCompany, MD_SIZE_COMPANY);
			}
		}
		// Keep a count of the number of fields we add
		fieldsAdded = row.size() - start;
	}

	public String getFirstName(int i) {
		return outputFirstName[i];
	}

	public String getFullName() {
		return inputFullName;
	}

	public String getGender(int i) {
		return outputGender[i];
	}

	public GenderAggression getGenderAggression() {
		return genderAggression;
	}

	public GenderPopulation getGenderPopulation() {
		return genderPopulation;
	}

	public String getInitializeError() {
		return initializeError;
	}

	public String getInitializeWarn() {
		return initializeWarn;
	}

	public String getInputCompanyName() {
		return inputCompanyName;
	}

	public String getLastName(int i) {
		return outputLastName[i];
	}

	/**
	 * @return Name Parser License
	 */
	public String getLicense() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String license = acMeta.getProdutLicense(AdvancedConfigurationMeta.MDLICENSE_Name);
		return license;
	}

	public String getMiddleName(int i) {
		return outputMiddleName[i];
	}

	public MiddleNameLogic getMiddleNameLogic() {
		return middleNameLogic;
	}

	public NameOrderHint getNameOrderHint() {
		return nameOrderHint;
	}

	public String getOutputStandardizedCompany() {
		return outputStandardizedCompany;
	}

	public String getPrefix(int i) {
		return outputPrefix[i];
	}

	public String getSalutation() {
		return outputSalutation;
	}

	public Salutation[] getSalutationOrder() {
		return salutationOrder;
	}

	public String getSalutationPrefix() {
		return salutationPrefix;
	}

	public String getSalutationSlug() {
		return salutationSlug;
	}

	public String getSalutationSuffix() {
		return salutationSuffix;
	}

	public String getServiceName() {
		return "Phone Verify";
	}

	public String getSuffix(int i) {
		return outputSuffix[i];
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDCheckData)
	 */
	public URL getWebURL(MDCheckData data, int queue) {
		return data.realWebNameParserURL;
	}

	/**
	 * Returns the XML representation of the Name Parsing meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_NAME_PARSE)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_FULLNAME, inputFullName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_COMPANYNAME, inputCompanyName));
		for (int i = 0; i < 2; i++) {
			retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_PREFIX + i, outputPrefix[i]));
			retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_FIRST_NAME + i, outputFirstName[i]));
			retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_MIDDLE_NAME + i, outputMiddleName[i]));
			retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_LAST_NAME + i, outputLastName[i]));
			retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SUFFIX + i, outputSuffix[i]));
			retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_GENDER + i, outputGender[i]));
		}
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SALUTATION, outputSalutation));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_STANDARDIZED_COMPANY, outputStandardizedCompany));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_CORRECT_MISPELLINGS, Boolean.toString(correctMispellings)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_NAME_ORDER_HINT, nameOrderHint.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_GENDER_AGGRESSION, genderAggression.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_GENDER_POPULATION, genderPopulation.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SALUTATION_PREFIX, salutationPrefix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SALUTATION_SUFFIX, salutationSuffix));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SALUTATION_SLUG, salutationSlug));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_MIDDLE_NAME_LOGIC, middleNameLogic.encode()));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_SALUTATION_ORDER, Salutation.encode(salutationOrder)));
		retval.append(tab).append(XMLHandler.closeTag(TAG_NAME_PARSE)).append(Const.CR);
		return retval.toString();
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isCompanyEnabled() {
		// Enabled only if it is licensed
		boolean isLicensed = isLicensed();
		// Enabled only if there is an input name
		boolean noInputFields = false;
		if (Const.isEmpty(inputCompanyName)) {
			noInputFields = true;
		}
		return isLicensed && !noInputFields;
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if it is licensed
		boolean isLicensed = isLicensed();
		// Enabled only if there is an input name
		boolean noInputFields = false;
		if (Const.isEmpty(inputFullName)) {
			noInputFields = true;
		}
		return isLicensed && !noInputFields;
	}

	public boolean isInitializeOK() {
		return initializeOK;
	}

	/**
	 * @return true if name parsing is licensed for this customer
	 */
	public boolean isLicensed() {
		// Licensed if it is not a DLL
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		if (acMeta.getServiceType() == ServiceType.CVS) { return true; }
		if ((acMeta.getServiceType() == ServiceType.Web) && !Const.isEmpty(getCustomerID())) { return true; }
		// Check product license
		if ((acMeta.getProducts() & MDPropTags.MDLICENSE_Name) != 0 || (acMeta.getProducts() & MDPropTags.MDLICENSE_Community) != 0) { return true; }
		return false;
	}

	/**
	 * Called to process the results of either the local or web services
	 *
	 * @param checkData
	 * @param requests
	 */
	public void outputData(MDCheckData checkData, List<MDCheckRequest> requests) {
		// Skip if not enabled
		if (!isEnabled() && !isCompanyEnabled()) { return; }

		boolean isEnterprise = AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_NameObject);

		// Output each request's results
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Output the email results
			MDCheckCVRequest.NameResults nameResults = request.nameResults;
			if(!isEnterprise && AdvancedConfigurationMeta.isCommunity()){
				nameResults = request.getNameCommunityResults(nameResults);
			}

			if ((nameResults != null) && nameResults.valid) {
				// Output results
				if (isEnabled()) {
					if (!Const.isEmpty(outputPrefix[0])) {
						request.addOutputData(nameResults.Prefix);
					}
					if (!Const.isEmpty(outputFirstName[0])) {
							request.addOutputData(nameResults.First);
					}
					if (!Const.isEmpty(outputMiddleName[0])) {
							request.addOutputData(nameResults.Middle);
					}
					if (!Const.isEmpty(outputLastName[0])) {
						request.addOutputData(nameResults.Last);
					}
					if (!Const.isEmpty(outputSuffix[0])) {
						request.addOutputData(nameResults.Suffix);
					}
					if (!Const.isEmpty(outputGender[0])) {
							request.addOutputData(nameResults.Gender);
					}
					if (!Const.isEmpty(outputPrefix[1])) {
						request.addOutputData(nameResults.Prefix2);
					}
					if (!Const.isEmpty(outputFirstName[1])) {
							request.addOutputData(nameResults.First2);
					}
					if (!Const.isEmpty(outputMiddleName[1])) {
							request.addOutputData(nameResults.Middle2);
					}
					if (!Const.isEmpty(outputLastName[1])) {
						request.addOutputData(nameResults.Last2);
					}
					if (!Const.isEmpty(outputSuffix[1])) {
						request.addOutputData(nameResults.Suffix2);
					}
					if (!Const.isEmpty(outputGender[1])) {
							request.addOutputData(nameResults.Gender2);
					}
					if (!Const.isEmpty(outputSalutation)) {
						request.addOutputData(nameResults.Salutation);
					}
				}
				if (isCompanyEnabled()) {
					if (!Const.isEmpty(outputStandardizedCompany)) {
						request.addOutputData(nameResults.StandardCompanyName);
					}
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
				// TODO: Reroute this record to the invalid output stream?
			}
			// Add the result codes to the overall result codes
			if (nameResults != null) {
				// If result fields are blank then strip extraneous NS01 from results codes.
				// This really shouldn't happen. But it has been reported so we take care of it here.
				if (Const.isEmpty(nameResults.Prefix) && Const.isEmpty(nameResults.First) && Const.isEmpty(nameResults.Middle) && Const.isEmpty(nameResults.Last) && Const.isEmpty(nameResults.Suffix)) {
					if (isEnabled()) {
						nameResults.resultCodes.remove("NS01");
						nameResults.resultCodes.add("NS02");  // we add this so it is counted in reporting
					}
				}
				// Don't return the DE code
				if (nameResults.resultCodes.contains("DE")) {
					nameResults.resultCodes.remove("DE");
				}
				// Add name result codes to over all results
				if(AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_NameObject)) {
					request.resultCodes.addAll(nameResults.resultCodes);
				}

				// Update reporting stats
				if (data.isReportEnabled()) {
					updateStats(nameResults, checkData);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#processWebResponse(org.dom4j.Document, com.melissadata.kettle.MDCheckData,
	 * boolean)
	 */
	public String processWebResponse(org.dom4j.Document doc, MDCheckData data, List<MDCheckRequest> requests, boolean testing) throws KettleException {
		// Get the response array
		org.dom4j.Element response = doc.getRootElement();
		if (!response.getName().equals("ResponseArray")) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.ResponseNotFound")); }
		// Check the general result
		String resultCodes = MDCheckWebService.getElementText(response, "Results");
		if (!Const.isEmpty(resultCodes)) { return resultCodes; }
		// Get the interface version
		webVersion = MDCheckWebService.getElementText(response, "Version");
		// Get the response records (ignore if testing)
		if (!testing) {
			@SuppressWarnings("unchecked")
			Iterator<org.dom4j.Element> i = response.elementIterator("Record");
			while (i.hasNext()) {
				org.dom4j.Element record = i.next();
				// This is used to index the request being processed
				int recordID = MDCheckWebService.getElementInteger(record, "RecordID");
				// Get the request object for the specified record id
				MDCheckCVRequest request = (MDCheckCVRequest) requests.get(recordID);
				// Get the intermediate name results
				MDCheckCVRequest.NameResults nameResults = request.nameResults = new MDCheckCVRequest.NameResults();
				// Result code for the individual request
				nameResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(record, "Results")));
				// Get the name result element
				org.dom4j.Element name = record.element("Name");
				if (name == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NameNotFoundInElement")); }
				// Extract results
				nameResults.Prefix = MDCheckWebService.getElementText(name, "Prefix");
				nameResults.First = MDCheckWebService.getElementText(name, "First");
				nameResults.Middle = MDCheckWebService.getElementText(name, "Middle");
				nameResults.Last = MDCheckWebService.getElementText(name, "Last");
				nameResults.Suffix = MDCheckWebService.getElementText(name, "Suffix");
				nameResults.Gender = MDCheckWebService.getElementText(name, "Gender");
				nameResults.Prefix2 = MDCheckWebService.getElementText(name, "Prefix2");
				nameResults.First2 = MDCheckWebService.getElementText(name, "First2");
				nameResults.Middle2 = MDCheckWebService.getElementText(name, "Middle2");
				nameResults.Last2 = MDCheckWebService.getElementText(name, "Last2");
				nameResults.Suffix2 = MDCheckWebService.getElementText(name, "Suffix2");
				nameResults.Gender2 = MDCheckWebService.getElementText(name, "Gender2");
				nameResults.Salutation = MDCheckWebService.getElementText(name, "Salutation");
				// TODO: additional validity checks
				nameResults.valid = true;
			}
		}
		return "";
	}

	/**
	 * Called to read name parsing meta data from a document node
	 *
	 * @param node
	 * @throws KettleException
	 * @throws NumberFormatException
	 */
	public void readData(Node node) throws NumberFormatException, KettleException {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_NAME_PARSE);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			inputFullName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_FULLNAME), inputFullName);
			inputCompanyName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_COMPANYNAME), inputCompanyName);
			for (int i = 0; i < 2; i++) {
				outputPrefix[i] = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_PREFIX + i), outputPrefix[i]);
				outputFirstName[i] = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_FIRST_NAME + i), outputFirstName[i]);
				outputMiddleName[i] = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_MIDDLE_NAME + i), outputMiddleName[i]);
				outputLastName[i] = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_LAST_NAME + i), outputLastName[i]);
				outputSuffix[i] = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SUFFIX + i), outputSuffix[i]);
				outputGender[i] = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_GENDER + i), outputGender[i]);
			}
			outputSalutation = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SALUTATION), outputSalutation);
			outputStandardizedCompany = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_STANDARDIZED_COMPANY), outputStandardizedCompany);
			String value = MDCheckStepData.getTagValue(node, TAG_CORRECT_MISPELLINGS);
			correctMispellings = (value != null) ? Boolean.valueOf(value) : correctMispellings;
			value = MDCheckStepData.getTagValue(node, TAG_NAME_ORDER_HINT);
			nameOrderHint = (value != null) ? NameOrderHint.decode(value) : nameOrderHint;
			value = MDCheckStepData.getTagValue(node, TAG_GENDER_AGGRESSION);
			genderAggression = (value != null) ? GenderAggression.decode(value) : genderAggression;
			value = MDCheckStepData.getTagValue(node, TAG_GENDER_POPULATION);
			genderPopulation = (value != null) ? GenderPopulation.decode(value) : genderPopulation;
			salutationPrefix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SALUTATION_PREFIX), salutationPrefix);
			salutationSuffix = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SALUTATION_SUFFIX), salutationSuffix);
			salutationSlug = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_SALUTATION_SLUG), salutationSlug);
			value = MDCheckStepData.getTagValue(node, TAG_MIDDLE_NAME_LOGIC);
			middleNameLogic = (value != null) ? MiddleNameLogic.decode(value) : middleNameLogic;
			value = MDCheckStepData.getTagValue(node, TAG_SALUTATION_ORDER);
			salutationOrder = (value != null) ? Salutation.decodeArray(value) : salutationOrder;
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
		inputFullName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_FULLNAME), inputFullName);
		inputCompanyName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_COMPANYNAME), inputCompanyName);
		for (int i = 0; i < 2; i++) {
			outputPrefix[i] = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_PREFIX + i), outputPrefix[i]);
			outputFirstName[i] = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_FIRST_NAME + i), outputFirstName[i]);
			outputMiddleName[i] = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_MIDDLE_NAME + i), outputMiddleName[i]);
			outputLastName[i] = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_LAST_NAME + i), outputLastName[i]);
			outputSuffix[i] = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_SUFFIX + i), outputSuffix[i]);
			outputGender[i] = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_GENDER + i), outputGender[i]);
		}
		outputSalutation = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION), outputSalutation);
		outputStandardizedCompany = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_STANDARDIZED_COMPANY), outputStandardizedCompany);
		correctMispellings = rep.getStepAttributeBoolean(idStep, TAG_NAME_PARSE + "." + TAG_CORRECT_MISPELLINGS);
		String value = rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_NAME_ORDER_HINT);
		nameOrderHint = (value != null) ? NameOrderHint.decode(value) : nameOrderHint;
		value = rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_GENDER_AGGRESSION);
		genderAggression = (value != null) ? GenderAggression.decode(value) : genderAggression;
		value = rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_GENDER_POPULATION);
		genderPopulation = (value != null) ? GenderPopulation.decode(value) : genderPopulation;
		salutationPrefix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_PREFIX), salutationPrefix);
		salutationSuffix = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_SUFFIX), salutationSuffix);
		salutationSlug = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_SLUG), salutationSlug);
		value = rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_MIDDLE_NAME_LOGIC);
		middleNameLogic = (value != null) ? MiddleNameLogic.decode(value) : middleNameLogic;
		value = rep.getStepAttributeString(idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_ORDER);
		salutationOrder = (value != null) ? Salutation.decodeArray(value) : salutationOrder;
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
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_FULLNAME, inputFullName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_COMPANYNAME, inputCompanyName);
		for (int i = 0; i < 2; i++) {
			rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_PREFIX + i, outputPrefix[i]);
			rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_FIRST_NAME + i, outputFirstName[i]);
			rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_MIDDLE_NAME + i, outputMiddleName[i]);
			rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_LAST_NAME + i, outputLastName[i]);
			rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_SUFFIX + i, outputSuffix[i]);
			rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_GENDER + i, outputGender[i]);
		}
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION, outputSalutation);
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_STANDARDIZED_COMPANY, outputStandardizedCompany);
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_CORRECT_MISPELLINGS, correctMispellings);
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_NAME_ORDER_HINT, nameOrderHint.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_GENDER_AGGRESSION, genderAggression.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_GENDER_POPULATION, genderPopulation.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_PREFIX, salutationPrefix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_SUFFIX, salutationSuffix);
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_SLUG, salutationSlug);
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_MIDDLE_NAME_LOGIC, middleNameLogic.encode());
		rep.saveStepAttribute(idTransformation, idStep, TAG_NAME_PARSE + "." + TAG_SALUTATION_ORDER, Salutation.encode(salutationOrder));
	}

	public void setCorrectMispellings(boolean b) {
		correctMispellings = b;
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault() {
		if (!AdvancedConfigurationMeta.isUseMapping()) {
			inputFullName = ""; // blank means not defined
		}
		inputCompanyName = ""; // blank means not defined
		// customer defined defaults
		if (MDCheckData.defaultsSet) {
			outputPrefix[0] = MDCheckData.prefix1;
			outputFirstName[0] = MDCheckData.firstName1;
			outputMiddleName[0] = MDCheckData.middleName1;
			outputLastName[0] = MDCheckData.lastName1;
			outputSuffix[0] = MDCheckData.suffix1;
			outputGender[0] = MDCheckData.gender1;
			outputPrefix[1] = MDCheckData.prefix2;
			outputFirstName[1] = MDCheckData.firstName2;
			outputMiddleName[1] = MDCheckData.middleName2;
			outputLastName[1] = MDCheckData.lastName2;
			outputSuffix[1] = MDCheckData.suffix2;
			outputGender[1] = MDCheckData.gender2;
			outputSalutation = MDCheckData.salutation;
			outputStandardizedCompany = MDCheckData.standardizedCompany;
		} else {
			for (int i = 0; i < 2; i++) {
				outputPrefix[i] = MD_PREFIX + (i + 1);
				outputFirstName[i] = MD_FIRSTNAME + (i + 1);
				outputMiddleName[i] = MD_MIDDLENAME + (i + 1);
				outputLastName[i] = MD_LASTNAME + (i + 1);
				outputSuffix[i] = MD_SUFFIX + (i + 1);
				outputGender[i] = MD_GENDER + (i + 1);
			}
			outputSalutation = MD_SALUTATION;
			outputStandardizedCompany = MD_STANDARDIZED_COMPANY;
		}
		correctMispellings = false;
		nameOrderHint = NameOrderHint.Varying;
		genderAggression = GenderAggression.Conservative;
		genderPopulation = GenderPopulation.EvenlySplit;
		salutationPrefix = BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Default.Prefix");
		salutationSuffix = BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Default.Suffix");
		salutationSlug = BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Default.Slug");
		middleNameLogic = MiddleNameLogic.ParseLogic;
		salutationOrder = Salutation.values();
	}

	public void setFirstName(int i, String s) {
		outputFirstName[i] = s;
	}

	public void setFullName(String s) {
		inputFullName = s;
	}

	public void setGender(int i, String s) {
		outputGender[i] = s;
	}

	public void setGenderAggression(GenderAggression aggression) {
		genderAggression = aggression;
	}

	public void setGenderPopulation(GenderPopulation population) {
		genderPopulation = population;
	}

	public void setInitializeOK(boolean initializeOK) {
		this.initializeOK = initializeOK;
	}

	public void setInputCompanyName(String inputCompanyName) {
		this.inputCompanyName = inputCompanyName;
	}

	public void setLastName(int i, String s) {
		outputLastName[i] = s;
	}

	public void setMiddleName(int i, String s) {
		outputMiddleName[i] = s;
	}

	public void setMiddleNameLogic(MiddleNameLogic logic) {
		middleNameLogic = logic;
	}

	public void setNameOrderHint(NameOrderHint hint) {
		nameOrderHint = hint;
	}

	public void setOutputStandardizedCompany(String outputStandardizedCompany) {
		this.outputStandardizedCompany = outputStandardizedCompany;
	}

	public void setPrefix(int i, String s) {
		outputPrefix[i] = s;
	}

	public void setSalutation(String s) {
		outputSalutation = s;
	}

	public void setSalutationOrder(Salutation[] a) {
		salutationOrder = a.clone();
	}

	public void setSalutationPrefix(String s) {
		salutationPrefix = s;
	}

	public void setSalutationSlug(String s) {
		salutationSlug = s;
	}

	public void setSalutationSuffix(String s) {
		salutationSuffix = s;
	}

	public void setSuffix(int i, String s) {
		outputSuffix[i] = s;
	}

	/**
	 * Called to validate settings before saving
	 *
	 * @param warnings
	 * @param errors
	 */
	public void validate(List<String> warnings, List<String> errors) {
		if (isLicensed()) {
			Validations checker = new Validations();
			checker.checkNameInputFields(data, warnings, errors);
		}
	}

	/**
	 * Called to update reporting stats for a given result
	 *
	 * @param nameResults
	 * @param checkData
	 */
	private synchronized void updateStats(MDCheckCVRequest.NameResults nameResults, MDCheckData checkData) {
		// TODO: Test and track specific result codes?
		// Add result codes to reporting result stats
		for (String resultCode : nameResults.resultCodes) {
			checkData.resultStats.inc(resultCode);
		}
	}
	
	public String processWebResponse(JSONObject jsonResponse, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Not used 
		return null;
	}
	
	public boolean buildWebRequest(JSONObject jsonRequest, MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Not used 
		return false;
	}
}
