package com.melissadata.kettle.cv.email;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.MDCheckCVRequest;
import com.melissadata.kettle.request.LocalRequestHandler;
import com.melissadata.kettle.request.WebRequestHandler;
import com.melissadata.kettle.validation.Validations;
import com.melissadata.kettle.cv.MDCheckWebService;
import com.melissadata.kettle.report.ReportStats;
import org.json.simple.JSONObject;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
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

import com.melissadata.mdEmail;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

public class EmailVerifyMeta implements WebRequestHandler, LocalRequestHandler, Cloneable {
	private static final String	TAG_EMAIL_VERIFY						= "email_verify";
	private static final String	TAG_OPT_CORRECT_EMAIL_SYNTAX			= "opt_correct_email_syntax";
	private static final String	TAG_OPT_PERFORM_DB_LOOKUP				= "opt_perform_db_lookup";
	private static final String	TAG_OPT_PERFORM_DNS_LOOKUP				= "opt_perform_dns_lookup";
	private static final String	TAG_OPT_STANDARDIZE_CASING				= "opt_standardize_casing";
	private static final String	TAG_OPT_UPDATE_DOMAINS					= "opt_update_domains";
	private static final String	TAG_OPT_FUZZY_LOOKUP					= "opt_fuzzy_lookup";
	private static final String	TAG_OPT_WEB_SERVICE_LOOKUP				= "opt_web_service_lookup";
	private static final String	TAG_INPUT_EMAIL							= "input_email";
	private static final String	TAG_OUTPUT_MAILBOX_NAME					= "output_mailbox_name";
	private static final String	TAG_OUTPUT_TLD							= "output_tld";
	private static final String	TAG_OUTPUT_TLD_DESCRIPTION				= "output_tld_description";
	private static final String	TAG_OUTPUT_DOMAIN						= "output_domain";
	private static final String	TAG_OUTPUT_EMAIL						= "output_email";
	// set the default output sizes
	private static final int	MD_SIZE_EMAIL							= 75;
	private static final int	MD_SIZE_MAILBOX							= 50;
	private static final int	MD_SIZE_DOMAIN							= 50;
	private static final int	MD_SIZE_TOP_LEVEL_DOMAIN				= 10;
	private static final int	MD_SIZE_TOP_LEVEL_DOMAIN_DESCRIPTION	= 50;
	private MDCheckStepData data;
	private boolean         initializeOK;
	private String          initializeWarn;
	private String          initializeError;
	private boolean         optCorrectEmailSyntax;
	private boolean         optPerformDBLookup;
	private boolean         optPerformDNSLookup;
	private boolean         optStandardizeCasing;
	private boolean         optUpdateDomains;
	private boolean         optFuzzyLookup;
	private boolean         optWebServiceLookup;
	private String          inputEmail;
	private String          outputDomain;
	private String          outputMailboxName;
	private String          outputTLD;
	private String				outputTLDDescription;
	private String				outputEmail;
	private int					fieldsAdded;
	// Info set during processing
	public String				localMsg								= "";
	public String				localDBDate								= "";
	public String				localDBExpiration						= "";
	public String				localDBBuildNo							= "";
	public String				webMsg									= "";
	public String				webVersion								= "";
	// Exceptions detected during processing
	public KettleException		localException;
	public KettleException		webException;

	public EmailVerifyMeta(MDCheckStepData data) {
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
		if (false) {
			// These don't have web service equivalents
			MDCheckWebService.addTextNode(xmlDoc, root, "OptCorrectEmailSyntax", optCorrectEmailSyntax ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptPerformDBLookup", optPerformDBLookup ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptPerformDNSLookup", optPerformDNSLookup ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptStandardizeCasing", optStandardizeCasing ? "True" : "False");
			MDCheckWebService.addTextNode(xmlDoc, root, "OptUpdateDomains", optUpdateDomains ? "True" : "False");
			// MDCheckWebService.addTextNode(xmlDoc, root, "OptFuzzyLookup", optFuzzyLookup ? "True" : "False");
			// MDCheckWebService.addTextNode(xmlDoc, root, "OptSWLookup", optWebServiceLookup ? "True" : "False");
		}
		// Add records
		boolean sendRequest = false;
		// If testing then create a fake record
		if (testing) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			root.appendChild(record);
			MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "0");
			MDCheckWebService.addTextNode(xmlDoc, record, "Email", "root@google.com");
			// There is at least one request
			sendRequest = true;
		} else {
			// Otherwise, add real records
			for (int recordID = 0; recordID < requests.size(); recordID++) {
				MDCheckRequest request = requests.get(recordID);
				RowMetaInterface inputMeta = request.inputMeta;
				Object[] inputData = request.inputData;
				// Get input email field (if defined)
				String email = null;
				if (!Const.isEmpty(inputEmail)) {
					email = inputMeta.getString(inputData, inputEmail, "");
				}
				if (Const.isEmpty(email)) {
					email = " ";
				}
				// Valid if there is an email value
				boolean valid = !Const.isEmpty(email);
				// Add request if it is valid
				if (valid) {
					// Create new record object
					Element record = xmlDoc.createElement("Record");
					root.appendChild(record);
					// Add unique record id
					MDCheckWebService.addTextNode(xmlDoc, record, "RecordID", "" + recordID);
					// Add request fields
					MDCheckWebService.addTextNode(xmlDoc, record, "Email", email);
					// There is at least one request
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
	 * Called to validate initialization of the email object
	 *
	 * @param checker
	 */
	public void checkInit(Validations checker) {
		boolean initialOK = true;
		initializeError = "";
		initializeWarn = "";
		mdEmail Email = null;
		try {
			// Get temporary email object
			Email = DQTObjectFactory.newEmail();
			// Configure the object
			Email.SetLicenseString(getLicense());
			Email.SetPathToEmailFiles(data.getAdvancedConfiguration().getLocalDataPath());
			// Initialize the object
			if (Email.InitializeDataFiles() == mdEmail.ProgramStatus.ErrorNone) {
				// See if the data is expiring soon
				if (Validations.isDataExpiring(Email.GetDatabaseExpirationDate())) {
					checker.warnings.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.EmailMeta.Error.DataExpiring") + Email.GetDatabaseExpirationDate());
					if (checker.checkTimeStamp(checker.getEmailTimeStamp())) {
						initializeWarn = BaseMessages.getString(MDCheck.class, "MDCheckMeta.EmailMeta.Error.DataExpiring") + Email.GetDatabaseExpirationDate();
						checker.showWarnings = true;
						checker.somethingToShow = true;
						checker.setEmailTimeStamp(checker.getTodayStamp());
					}
				}
			} else {
				if (isEnabled()) {
					checker.errors.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.EmailMeta.Error.EmailObject") + Email.GetInitializeErrorString());
				} else {
					checker.warnings.add(BaseMessages.getString(MDCheck.class, "MDCheckMeta.EmailMeta.Error.EmailObject") + Email.GetInitializeErrorString());
				}
				if (checker.checkTimeStamp(checker.getEmailTimeStamp())) {
					initializeError = BaseMessages.getString(MDCheck.class, "MDCheckMeta.EmailMeta.Error.EmailObject") + Email.GetInitializeErrorString();
					checker.showErrors = true;
					checker.somethingToShow = true;
					checker.setEmailTimeStamp(checker.getTodayStamp());
				}
				initialOK = false;
			}
			initializeOK = initialOK;
		} catch (Throwable t) {
			initializeError = BaseMessages.getString(MDCheck.class, "MDCheckMeta.EmailMeta.Error.EmailObject") + t.getLocalizedMessage();
			checker.showErrors = true;
			checker.somethingToShow = true;
			checker.setEmailTimeStamp(checker.getTodayStamp());
		} finally {
			if (Email != null) {
				Email.delete();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public EmailVerifyMeta clone() throws CloneNotSupportedException {
		return (EmailVerifyMeta) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#doLocalRequest(com.melissadata.kettle.MDCheckData)
	 */
	public synchronized void doLocalRequests(MDCheckData data, List<MDCheckRequest> requests) throws KettleException {
		// Skip if not enabled
		if (!isEnabled()) { return; }
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Process the single request
			RowMetaInterface inputMeta = request.inputMeta;
			Object[] inputData = request.inputData;
			// Get the intermediate email results
			MDCheckCVRequest.EmailResults emailResults = request.emailResults = new MDCheckCVRequest.EmailResults();
			// Get input email field (if defined)
			String email = null;
			if (!Const.isEmpty(inputEmail)) {
				email = inputMeta.getString(inputData, inputEmail, "");
			}
			// Valid if there is an email value
			if (Const.isEmpty(email)) {
				email = " ";
			}
			boolean valid = !Const.isEmpty(email);
			// Get reference to email object
			mdEmail Email = data.Email;
			// Add request if it is valid
			if (valid) {
				// Verify the email
				Email.VerifyEmail(email);
				// Always get result codes for the individual request
				emailResults.resultCodes.addAll(MDCheck.getResultCodes(Email.GetResults()));
			}
			// Process the results of a valid request
			if (valid) {
				// Extract results
				emailResults.MailboxName = Email.GetMailBoxName();
				emailResults.TopLevelDomainName = Email.GetTopLevelDomain();
				emailResults.TopLevelDomainDescription = Email.GetTopLevelDomainDescription();
				emailResults.DomainName = Email.GetDomainName();
				emailResults.EmailAddress = Email.GetEmailAddress();
				// TODO: more validity checks
				emailResults.valid = true;
			}
		}
	}

	/**
	 * @return The Email Verification Customer ID
	 */
	public String getCustomerID() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String customerID = acMeta.getCustomerID(AdvancedConfigurationMeta.MDLICENSE_Email);
		return customerID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURLCVS(com.melissadata.kettle.MDCheckData)
	 */
	public URL getCVSURL(MDCheckData data) {
		return data.realCVSEmailVerifierURL;
	}

	/**
	 * Called to determine the output fields that will be included in the step outout record
	 *
	 * NOTE: Order of fields must match the order of fields in processResponses
	 *
	 * @param row
	 * @param originName
	 * @param space
	 */
	public void getFields(RowMetaInterface row, String originName, VariableSpace space) {
		int start = row.size();
		// Don't add fields if input is not defined
		if (isEnabled()) {
			// General output fields
			if (!Const.isEmpty(outputMailboxName)) {
				MDCheckMeta.getStringField(row, originName, space, outputMailboxName, MD_SIZE_MAILBOX);
			}
			if (!Const.isEmpty(outputTLD)) {
				MDCheckMeta.getStringField(row, originName, space, outputTLD, MD_SIZE_TOP_LEVEL_DOMAIN);
			}
			if (!Const.isEmpty(outputTLDDescription)) {
				MDCheckMeta.getStringField(row, originName, space, outputTLDDescription, MD_SIZE_TOP_LEVEL_DOMAIN_DESCRIPTION);
			}
			if (!Const.isEmpty(outputDomain)) {
				MDCheckMeta.getStringField(row, originName, space, outputDomain, MD_SIZE_DOMAIN);
			}
			if (!Const.isEmpty(outputEmail)) {
				MDCheckMeta.getStringField(row, originName, space, outputEmail, MD_SIZE_EMAIL);
			}
		}
		// Keep a count of the number of fields we add
		fieldsAdded = row.size() - start;
	}

	public String getInitializeError() {
		return initializeError;
	}

	public String getInitializeWarn() {
		return initializeWarn;
	}

	public String getInputEmail() {
		return inputEmail;
	}

	/**
	 * @return The Email Verification License
	 */
	public String getLicense() {
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		String license = acMeta.getProdutLicense(AdvancedConfigurationMeta.MDLICENSE_Email);
		return license;
	}

	public boolean getOptionCorrectEmailSyntax() {
		return optCorrectEmailSyntax;
	}

	public boolean getOptionFuzzyLookup() {
		return optFuzzyLookup;
	}

	public boolean getOptionPerformDBLookup() {
		return optPerformDBLookup;
	}

	public boolean getOptionPerformDNSLookup() {
		return optPerformDNSLookup;
	}

	public boolean getOptionStandardizeCasing() {
		return optStandardizeCasing;
	}

	public boolean getOptionUpdateDomains() {
		return optUpdateDomains;
	}

	public boolean getOptionWebServiceLookup() {
		return optWebServiceLookup;
	}

	public String getOutputDomain() {
		return outputDomain;
	}

	public String getOutputEmail() {
		return outputEmail;
	}

	public String getOutputMailboxName() {
		return outputMailboxName;
	}

	public String getOutputTLD() {
		return outputTLD;
	}

	public String getOutputTLDDescription() {
		return outputTLDDescription;
	}

	public String getServiceName() {
		return "Email Verify";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.RequestHandler#getURL(com.melissadata.kettle.MDCheckData)
	 */
	public URL getWebURL(MDCheckData data, int queue) {
		return data.realWebEmailVerifierURL;
	}

	/**
	 * Returns the XML representation of the meta data
	 *
	 * @param tab
	 * @return
	 */
	public String getXML(String tab) {
		StringBuilder retval = new StringBuilder(200);
		retval.append(tab).append(XMLHandler.openTag(TAG_EMAIL_VERIFY)).append(Const.CR);
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_CORRECT_EMAIL_SYNTAX, Boolean.toString(optCorrectEmailSyntax)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PERFORM_DB_LOOKUP, Boolean.toString(optPerformDBLookup)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_PERFORM_DNS_LOOKUP, Boolean.toString(optPerformDNSLookup)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_STANDARDIZE_CASING, Boolean.toString(optStandardizeCasing)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_UPDATE_DOMAINS, Boolean.toString(optUpdateDomains)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_FUZZY_LOOKUP, Boolean.toString(optFuzzyLookup)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OPT_WEB_SERVICE_LOOKUP, Boolean.toString(optWebServiceLookup)));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_INPUT_EMAIL, inputEmail));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_MAILBOX_NAME, outputMailboxName));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_TLD, outputTLD));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_TLD_DESCRIPTION, outputTLDDescription));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_DOMAIN, outputDomain));
		retval.append(tab).append("  ").append(MDCheckStepData.addTagValue(TAG_OUTPUT_EMAIL, outputEmail));
		retval.append(tab).append(XMLHandler.closeTag(TAG_EMAIL_VERIFY)).append(Const.CR);
		return retval.toString();
	}

	/**
	 * @return true if the we should be called during processing
	 */
	public boolean isEnabled() {
		// Enabled only if licensed
		boolean isLicensed = isLicensed();
		// Enabled only if there is an input email
		boolean noInputFields = Const.isEmpty(inputEmail);
		return isLicensed && !noInputFields;
	}

	public boolean isInitializeOK() {
		return initializeOK;
	}

	/**
	 * @return
	 */
	public boolean isLicensed() {
		// Licensed if it is not a DLL
		AdvancedConfigurationMeta acMeta = data.getAdvancedConfiguration();
		if (acMeta.getServiceType() == ServiceType.CVS) {
			return true;
		}
		if ((acMeta.getServiceType() == ServiceType.Web) && !Const.isEmpty(getCustomerID())) {
			return true;
		}
		// check product license
		if ((acMeta.getProducts() & MDPropTags.MDLICENSE_Email) != 0 || (acMeta.getProducts() & MDPropTags.MDLICENSE_Community) != 0) {
			return true;
		}
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
		if (!isEnabled()) { return; }
		// Output each request's results
		for (MDCheckRequest mdCheckRequest : requests) {
			MDCheckCVRequest request = (MDCheckCVRequest) mdCheckRequest;
			// Output the email results
			MDCheckCVRequest.EmailResults emailResults = request.emailResults;
			if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_EmailObject)
					&& AdvancedConfigurationMeta.isCommunity()){
				emailResults = request.getEmailCommunityResults(emailResults);
			}
			if ((emailResults != null) && emailResults.valid) {
				// output results
				if (!Const.isEmpty(outputMailboxName)) {
					request.addOutputData(emailResults.MailboxName);
				}
				if (!Const.isEmpty(outputTLD)) {
					request.addOutputData(emailResults.TopLevelDomainName);
				}
				if (!Const.isEmpty(outputTLDDescription)) {
					request.addOutputData(emailResults.TopLevelDomainDescription);
				}
				if (!Const.isEmpty(outputDomain)) {
					request.addOutputData(emailResults.DomainName);
				}
				if (!Const.isEmpty(outputEmail)) {
					request.addOutputData(emailResults.EmailAddress);
				}
			} else {
				// If it was not valid then we need to add empty data to the output
				request.outputDataSize += fieldsAdded;
				request.outputData = RowDataUtil.resizeArray(request.outputData, request.outputDataSize);
				// TODO: Reroute this record to an invalid output stream?
			}
			if (emailResults != null) {
				// Change DE to EE01 and EE02
				if (emailResults.resultCodes.contains("DE")) {
					emailResults.resultCodes.remove("DE");
					emailResults.resultCodes.add("EE01");
					emailResults.resultCodes.add("ES02");
				}
				// Add the email result codes to the overall result codes
				if(AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_EmailObject)) {
					request.resultCodes.addAll(emailResults.resultCodes);
				}
				// Update reporting stats
				if (data.isReportEnabled()) {
					updateStats(emailResults, checkData);
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
				// Get the intermediate email results
				MDCheckCVRequest.EmailResults emailResults = request.emailResults = new MDCheckCVRequest.EmailResults();
				// Result code for the individual request
				emailResults.resultCodes.addAll(MDCheck.getResultCodes(MDCheckWebService.getElementText(record, "Results")));
				// Get the email result element
				org.dom4j.Element email = record.element("Email");
				if (email == null) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.EmailNotFoundInElement")); }
				// Get the email results
				emailResults.MailboxName = (MDCheckWebService.getElementText(email, "MailboxName"));
				emailResults.DomainName = MDCheckWebService.getElementText(email, "DomainName");
				emailResults.EmailAddress = MDCheckWebService.getElementText(email, "EmailAddress");
				org.dom4j.Element tld = MDCheckWebService.getElement(email, "TopLevelDomain");
				emailResults.TopLevelDomainName = MDCheckWebService.getElementText(tld, "Name");
				emailResults.TopLevelDomainDescription = MDCheckWebService.getElementText(tld, "Description");
				// TODO: more complete validity checks?
				emailResults.valid = true;
			}
		}
		return "";
	}

	/**
	 * Called to read meta data from a document node
	 *
	 * @param node
	 */
	public void readData(Node node) {
		List<Node> nodes = XMLHandler.getNodes(node, TAG_EMAIL_VERIFY);
		if ((nodes != null) && (nodes.size() > 0)) {
			node = nodes.get(0);
			// Option fields
			String value = MDCheckStepData.getTagValue(node, TAG_OPT_CORRECT_EMAIL_SYNTAX);
			optCorrectEmailSyntax = (value != null) ? Boolean.valueOf(value) : optCorrectEmailSyntax;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PERFORM_DB_LOOKUP);
			optPerformDBLookup = (value != null) ? Boolean.valueOf(value) : optPerformDBLookup;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_PERFORM_DNS_LOOKUP);
			optPerformDNSLookup = (value != null) ? Boolean.valueOf(value) : optPerformDNSLookup;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_STANDARDIZE_CASING);
			optStandardizeCasing = (value != null) ? Boolean.valueOf(value) : optStandardizeCasing;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_UPDATE_DOMAINS);
			optUpdateDomains = (value != null) ? Boolean.valueOf(value) : optUpdateDomains;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_FUZZY_LOOKUP);
			optFuzzyLookup = (value != null) ? Boolean.valueOf(value) : optFuzzyLookup;
			value = MDCheckStepData.getTagValue(node, TAG_OPT_WEB_SERVICE_LOOKUP);
			optWebServiceLookup = (value != null) ? Boolean.valueOf(value) : optWebServiceLookup;
			// Input fields
			inputEmail = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_INPUT_EMAIL), inputEmail);
			// Output fields
			outputMailboxName = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_MAILBOX_NAME), outputMailboxName);
			outputTLD = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_TLD), outputTLD);
			outputTLDDescription = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_TLD_DESCRIPTION), outputTLDDescription);
			outputDomain = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_DOMAIN), outputDomain);
			outputEmail = MDCheckStepData.safe(MDCheckStepData.getTagValue(node, TAG_OUTPUT_EMAIL), outputEmail);
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
		optCorrectEmailSyntax = rep.getStepAttributeBoolean(idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_CORRECT_EMAIL_SYNTAX);
		optPerformDBLookup = rep.getStepAttributeBoolean(idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_PERFORM_DB_LOOKUP);
		optPerformDNSLookup = rep.getStepAttributeBoolean(idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_PERFORM_DNS_LOOKUP);
		optStandardizeCasing = rep.getStepAttributeBoolean(idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_STANDARDIZE_CASING);
		optUpdateDomains = rep.getStepAttributeBoolean(idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_UPDATE_DOMAINS);
		optFuzzyLookup = rep.getStepAttributeBoolean(idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_FUZZY_LOOKUP);
		optWebServiceLookup = rep.getStepAttributeBoolean(idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_WEB_SERVICE_LOOKUP);
		inputEmail = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_EMAIL_VERIFY + "." + TAG_INPUT_EMAIL), inputEmail);
		outputMailboxName = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_MAILBOX_NAME), outputMailboxName);
		outputTLD = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_TLD), outputTLD);
		outputTLDDescription = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_TLD_DESCRIPTION), outputTLDDescription);
		outputDomain = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_DOMAIN), outputDomain);
		outputEmail = MDCheckStepData.safe(rep.getStepAttributeString(idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_EMAIL), outputEmail);
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
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_CORRECT_EMAIL_SYNTAX, optCorrectEmailSyntax);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_PERFORM_DB_LOOKUP, optPerformDBLookup);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_PERFORM_DNS_LOOKUP, optPerformDNSLookup);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_STANDARDIZE_CASING, optStandardizeCasing);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_UPDATE_DOMAINS, optUpdateDomains);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_FUZZY_LOOKUP, optFuzzyLookup);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OPT_WEB_SERVICE_LOOKUP, optWebServiceLookup);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_INPUT_EMAIL, inputEmail);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_MAILBOX_NAME, outputMailboxName);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_TLD, outputTLD);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_TLD_DESCRIPTION, outputTLDDescription);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_DOMAIN, outputDomain);
		rep.saveStepAttribute(idTransformation, idStep, TAG_EMAIL_VERIFY + "." + TAG_OUTPUT_EMAIL, outputEmail);
	}

	public void setData(MDCheckStepData data) {
		this.data = data;
	}

	/**
	 * Called to initialized default values
	 */
	public void setDefault() {
		// options
		optCorrectEmailSyntax = true;
		optPerformDBLookup = true;
		optPerformDNSLookup = true;
		optStandardizeCasing = true;
		optUpdateDomains = true;
		optFuzzyLookup = false;
		optWebServiceLookup = false;
		// input meta data (blank means not defined)
		inputEmail = "";
		// output meta data
		if (MDCheckData.defaultsSet) {
			outputEmail = MDCheckData.emailAddress;
			outputMailboxName = MDCheckData.emailMailBoxName;
			outputTLD = MDCheckData.emailTopLevelDomain;
			outputTLDDescription = MDCheckData.emailTopLevelDomainDescription;
			outputDomain = MDCheckData.emailDomainName;
		} else {
			outputEmail = "MD_EmailAddress";
			outputMailboxName = "MD_EmailBox";
			outputTLD = "MD_EmailTopLevel";
			outputTLDDescription = "MD_EmailTopLevelDesc";
			outputDomain = "MD_EmailDomain";
		}
	}

	public void setInitializeOK(boolean initializeOK) {
		this.initializeOK = initializeOK;
	}

	public void setInputEmail(String s) {
		inputEmail = s;
	}

	public void setOptionCorrectEmailSyntax(boolean b) {
		optCorrectEmailSyntax = b;
	}

	public void setOptionFuzzyLookup(boolean optFuzzyLookup) {
		this.optFuzzyLookup = optFuzzyLookup;
	}

	public void setOptionPerformDBLookup(boolean b) {
		optPerformDBLookup = b;
	}

	public void setOptionPerformDNSLookup(boolean b) {
		optPerformDNSLookup = b;
	}

	public void setOptionStandardizeCasing(boolean b) {
		optStandardizeCasing = b;
	}

	public void setOptionUpdateDomains(boolean b) {
		optUpdateDomains = b;
	}

	public void setOptionWebServiceLookup(boolean optSWLookup) {
		optWebServiceLookup = optSWLookup;
	}

	public void setOutputDomain(String s) {
		outputDomain = s;
	}

	public void setOutputEmail(String s) {
		outputEmail = s;
	}

	public void setOutputMailboxName(String s) {
		outputMailboxName = s;
	}

	public void setOutputTLD(String s) {
		outputTLD = s;
	}

	public void setOutputTLDDescription(String s) {
		outputTLDDescription = s;
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
			checker.checkEmailInputFields(data, warnings, errors);
		}
	}

	/**
	 * Called to update reporting stats for a given result
	 *
	 * @param emailResults
	 * @param checkData
	 */
	private synchronized void updateStats(MDCheckCVRequest.EmailResults emailResults, MDCheckData checkData) {
		// De-reference results and meta data
		Set<String> resultCodes = emailResults.resultCodes;
		ReportStats numEmailOverview = checkData.numEmailOverview;
		MDCheckMeta checkMeta = data.getMeta();
		// If email address result is blank then strip certain result codes
		// (For reporting purposes only. The result codes will still appear in the output.)
		if (Const.isEmpty(emailResults.EmailAddress) && resultCodes.contains("ES02") && resultCodes.contains("EE01")) {
			resultCodes.remove("ES02");
			resultCodes.remove("EE01");
		}
		// Test for specific result codes
		boolean validDomain = resultCodes.contains("ES01");
		boolean invalidDomain = resultCodes.contains("ES02");
		boolean unverifiedDomain = resultCodes.contains("ES03");
		boolean mobile = resultCodes.contains("ES04");
		boolean syntaxChanged = resultCodes.contains("ES10");
		boolean TLDChanged = resultCodes.contains("ES11");
		boolean domainChangedSpelling = resultCodes.contains("ES12");
		boolean domainChangedUpdate = resultCodes.contains("ES13");
		// Track number of valid, non-mobile domains
		if (validDomain && !mobile) {
			checkData.numValidDomain += 1;
		}
		// Track number of blank email addresses
		if (Const.isEmpty(emailResults.EmailAddress)) {
			checkData.numEmailBlanks += 1;
			numEmailOverview.inc(checkMeta.emailOverviewReportCStat[3]);
		}
		// Track valid domains...
		if (validDomain) {
			// ... that were not changed
			if (!(syntaxChanged || TLDChanged || domainChangedSpelling || domainChangedUpdate)) {
				numEmailOverview.inc(checkMeta.emailOverviewReportCStat[0]);
			} else {
				numEmailOverview.inc(checkMeta.emailOverviewReportCStat[1]);
			}
		}
		// Track unverified domains
		if (unverifiedDomain) {
			numEmailOverview.inc(checkMeta.emailOverviewReportCStat[2]);
		}
		// Track invalid domains
		if (invalidDomain) {
			numEmailOverview.inc(checkMeta.emailOverviewReportCStat[4]);
		}
		// Add result codes to reporting result stats
		for (String resultCode : resultCodes) {
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
