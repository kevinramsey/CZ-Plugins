package com.melissadata.kettle.personator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.melissadata.cz.CZUtil;

public class PreBuiltRules {
	public class Rule {
		public String	name;
		public String	expression;

		public Rule(String name, String expression) {
			this.name = name;
			this.expression = expression;
		}

		@Override
		public String toString() {
			return name + " = '" + expression + "'";
		}
	}
	private static final String	PRE_BUILT_PERSONATOR_RULES_FILE	= "md_personator_pre_built_rules.xml";
	private static final String	TAG_PRE_BUILT_RULES				= "pre_built_rules";
	private static final String	TAG_RULE						= "rule";
	private static final String	TAG_NAME						= "name";
	private static final String	TAG_EXPRESSION					= "expression";
	private String				ruleFile;
	private String				rulesPath				=  "MD" + Const.FILE_SEPARATOR + "Rules" + Const.FILE_SEPARATOR;
	private List<Rule>			rules;

	public PreBuiltRules(MDPersonatorMeta meta) throws KettleException {
		ruleFile = PRE_BUILT_PERSONATOR_RULES_FILE;
		load();
	}

	/**
	 * Called to add a new rule to the list of rules
	 *
	 * @param name
	 * @param expression
	 * @throws KettleException
	 */
	public void addRule(String name, String expression) throws KettleException {
		// Add to list of rules
		rules.add(new Rule(name, expression));
		// Save it
		try {
			save();
		} catch (KettleException e) {
			// Back out the change
			rules.remove(rules.size() - 1);
			// re-throw
			throw e;
		}
	}

	/*
	 * Called to create the initial set of pre built rules.
	 */
	private void create() throws KettleException {
		// Fill in pre-defined rules
		rules = new ArrayList<Rule>();
		rules.add(new Rule("Address and Name Verified", "[VR01]"));
		rules.add(new Rule("Full Address and Full Name Verified", "[VR01] AND ! [VS02] AND ! [VS12]"));
		rules.add(new Rule("Full Address and Full Company Verified", " [VR07] AND ! [VS02] AND ! [VS22]"));
		rules.add(new Rule("Address, Name and Phone Verified", "([VR01] AND [VR04]) OR ([VR02] AND [VR04])"));
		rules.add(new Rule("One or more components verified", "[VR*]"));
		rules.add(new Rule("Appended one or more contact pieces", "[DA*]"));
		save();
	}

	/*
	 * Get the rule file, create it if it doesn't exist.
	 */
	private File getRuleFile() throws KettleException {
		File file;
		
		try {

			// First look in the old location
			file = new File(CZUtil.getCZWorkDirectory(), ruleFile);
			if(file.exists()){
				file = updateRulesLocation(file);
			} else {
				file =  new File(CZUtil.getCZWorkDirectory(), rulesPath +  ruleFile);
				if(!file.exists()){
					File rulesDir = new File(CZUtil.getCZWorkDirectory(), rulesPath);
					rulesDir.mkdirs();
					file =  new File(rulesDir,ruleFile);
				}
			}
			
			return file;
		} catch (IOException e) {
			throw new KettleException(BaseMessages.getString(MDPersonator.class, "MDPersonator.Error.PreBuiltRulesLocation"), e);
		}
	}
	
	private File updateRulesLocation(File oldFile) throws IOException{
		/* This was done to help cleanup the .kettle dir.
		*	it was getting very clutered with rules files.
		*/

		File newLocation = new File(CZUtil.getCZWorkDirectory(), rulesPath);
		FileUtils.moveFileToDirectory(oldFile, newLocation, true);
		
		return new File(newLocation, ruleFile);
	}

	/**
	 * @param filter
	 * @return The rule whose expression matches the given filter. null if none.
	 */
	public Rule getRuleFromFilter(String filter) {
		filter.trim().replaceAll("\\s+", "");
		for (Rule rule : rules) {
			if (rule.expression.equalsIgnoreCase(filter))
				return rule;
		}
		return null;
	}

	/**
	 * @return The list of currently defined rules
	 */
	public List<Rule> getRules() {
		return rules;
	}

	/*
	 * Called to load the rule file
	 */
	private void load() throws KettleException {
		// get the rules file
		File rulesFile = getRuleFile();
		if (!rulesFile.exists()) {
			create();
			return;
		}
		// load the rules file
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(rulesFile));
			StringBuffer xml = new StringBuffer();
			int c;
			while ((c = inputStream.read()) != -1) {
				xml.append((char) c);
			}
			inputStream.close();
			Document document = XMLHandler.loadXMLString(xml.toString());
			Node ruleNode = XMLHandler.getSubNode(document, TAG_PRE_BUILT_RULES);
			List<Node> ruleNodes = XMLHandler.getNodes(ruleNode, TAG_RULE);
			rules = new ArrayList<Rule>(ruleNodes.size());
			for (Node rule : ruleNodes) {
				String name = MDPersonatorData.getTagValue(rule, TAG_NAME);
				String expression = MDPersonatorData.getTagValue(rule, TAG_EXPRESSION);
				rules.add(new Rule(name, expression));
			}
		} catch (IOException e) {
			throw new KettleException("Problem loading pre-built filtering rules", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	/**
	 * Called to remove a rule of the specific name
	 *
	 * @param name
	 * @throws KettleException
	 */
	public void removeRule(String name) throws KettleException {
		// Find and remove it
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = rules.get(i);
			if (rule.name.equalsIgnoreCase(name)) {
				rules.remove(i);
				// Save it
				try {
					save();
				} catch (KettleException e) {
					// Back out the change
					rules.add(i, rule);
					// re-throw
					throw e;
				}
				return;
			}
		}
	}

	/*
	 * Called to save the pre-built rules in the configuration file
	 */
	private void save() throws KettleException {
		// Construct XML of the file
		StringBuffer xml = new StringBuffer(XMLHandler.getXMLHeader());
		xml.append("  ").append(XMLHandler.openTag(TAG_PRE_BUILT_RULES)).append(Const.CR);
		for (Rule rule : rules) {
			xml.append("    ").append(XMLHandler.openTag(TAG_RULE)).append(Const.CR);
			xml.append("    ").append(MDPersonatorData.addTagValue(TAG_NAME, rule.name)).append(Const.CR);
			xml.append("    ").append(MDPersonatorData.addTagValue(TAG_EXPRESSION, rule.expression)).append(Const.CR);
			xml.append("    ").append(XMLHandler.closeTag(TAG_RULE)).append(Const.CR);
		}
		xml.append("  ").append(XMLHandler.closeTag(TAG_PRE_BUILT_RULES)).append(Const.CR);
		// save the rules file
		try {
			File rulesFile = getRuleFile();
			OutputStream outputStream = null;
			try {
				outputStream = new BufferedOutputStream(new FileOutputStream(rulesFile));
				outputStream.write(xml.toString().getBytes());
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
			}
		} catch (IOException e) {
			throw new KettleException("Problem saving pre-built filtering rules", e);
		}
	}
}
