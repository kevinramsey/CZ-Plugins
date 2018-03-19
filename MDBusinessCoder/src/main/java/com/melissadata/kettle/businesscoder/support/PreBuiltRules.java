package com.melissadata.kettle.businesscoder.support;

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
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.MDBusinessCoderStep;

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

	private static final String	PRE_BUILT_AV_RULES_FILE	= "md_business_coder_pre_built_rules.xml";
	private static final String	TAG_PRE_BUILT_RULES		= "pre_built_rules";
	private static final String	TAG_RULE				= "rule";
	private static final String	TAG_NAME				= "name";

	private static final String	TAG_EXPRESSION			= "expression";

	private String				ruleFile;
	private String				rulesPath				=  "MD" + Const.FILE_SEPARATOR + "Rules" + Const.FILE_SEPARATOR;
	private List<Rule>			rules;

	public PreBuiltRules(MDBusinessCoderMeta meta) throws KettleException {

		ruleFile = PRE_BUILT_AV_RULES_FILE;
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

	/**
	 * @param filter
	 * @return The rule whose expression matches the given filter. null if none.
	 */
	public Rule getRuleFromFilter(String filter) {

		filter.trim().replaceAll("\\s+", "");
		for (Rule rule : rules) {
			if (rule.expression.equalsIgnoreCase(filter)) { return rule; }
		}
		return null;
	}

	/**
	 * @return The list of currently defined rules
	 */
	public List<Rule> getRules() {

		return rules;
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
	 * Called to create the initial set of pre built rules.
	 */
	private void create() throws KettleException {

		// Fill in pre-defined rules
		rules = new ArrayList<Rule>();

		rules.add(new Rule("A match was found in the Data [FS01]", "[FS01]"));
		rules.add(new Rule("A match was found for the Company and Address [FS02] AND [FS04]", "[FS02] AND [FS04]"));
		rules.add(new Rule("A match was found for the Company [FS02]", "[FS02]"));
		rules.add(new Rule("A match was found for the input address key [FS03]", "[FS03]"));
		rules.add(new Rule("A match was found for the input address [FS04]", "[FS04]"));
		rules.add(new Rule("A match was found for the input phone [FS05]", "[FS05]"));
		rules.add(new Rule("A match was found for the input Stock Ticker [FS06]", "[FS06]"));
		rules.add(new Rule("A match was found for the input Web Address [FS07]", "[FS07]"));
		rules.add(new Rule("No match was found [FE01]", "[FE01]"));

		// Save it
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
			//return new File(CZUtil.getCZWorkDirectory(), ruleFile);

		} catch (IOException e) {

			throw new KettleException(BaseMessages.getString(MDBusinessCoderStep.class, "MDBusinessCoder.Error.PreBuiltRulesLocation"), e);
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
				String name = MDBusinessCoderMeta.getTagValue(rule, TAG_NAME);
				String expression = MDBusinessCoderMeta.getTagValue(rule, TAG_EXPRESSION);
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

	/*
	 * Called to save the pre-built rules in the configuration file
	 */
	private void save() throws KettleException {

		// Construct XML of the file
		StringBuffer xml = new StringBuffer(XMLHandler.getXMLHeader());

		xml.append("  ").append(XMLHandler.openTag(TAG_PRE_BUILT_RULES)).append(Const.CR);

		for (Rule rule : rules) {
			xml.append("    ").append(XMLHandler.openTag(TAG_RULE)).append(Const.CR);

			xml.append("    ").append(MDBusinessCoderMeta.addTagValue(TAG_NAME, rule.name)).append(Const.CR);
			xml.append("    ").append(MDBusinessCoderMeta.addTagValue(TAG_EXPRESSION, rule.expression)).append(Const.CR);

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
