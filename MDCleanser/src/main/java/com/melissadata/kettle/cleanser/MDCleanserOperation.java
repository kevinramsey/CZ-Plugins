package com.melissadata.kettle.cleanser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.kettle.cleanser.request.MDCleanserRequest;
import org.pentaho.di.core.variables.VariableSpace;

public class MDCleanserOperation implements Cloneable {

	private String                        sourceFieldName      = "";
	private String                        operationName        = "";
	private String                        operationDescription = "";
	private HashMap<String, CleanserRule> cleanerRules         = new HashMap<String, CleanserRule>();
	private boolean                       appendField          = false;
	private boolean                         passThrough = true;
	public  HashMap<String, String> columns     = new HashMap<String, String>();
	
	private LogChannelInterface log;


	public MDCleanserOperation(String sourceFieldName) {

		setSourceFieldName(sourceFieldName);
		setOperationName("");
		setOperationDescription("");
	}

	public synchronized String[] cleanse(String n_line, MDCleanserRequest request) throws KettleException {
		String cn_line = "";
		String[] resultAr = null;

		if (cleanerRules.size() < 1) {
			resultAr = new String[] { n_line, "" };
			return resultAr;
		}

		if (cleanerRules.size() > 0) {
			cn_line = n_line;
		}
		for (Entry<String, CleanserRule> entry : cleanerRules.entrySet()) {
			CleanserRule cleansingRule = entry.getValue();

			cleansingRule.columns = columns;
			try {
				resultAr = cleansingRule.doCleanse(cn_line);
				cn_line = resultAr[0];
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			if (!Const.isEmpty(resultAr[1])) {
			
				if(appendField){
					// We change the first "0" to the rule index
					request.resultCodes.add(sourceFieldName + "-" + resultAr[1].replace("0", entry.getKey()));
				} else {
					request.resultCodes.add(resultAr[1].replace("0", entry.getKey()));
				}
			}
		}

		return resultAr;
	}

	@Override
	public MDCleanserOperation clone() throws CloneNotSupportedException {
		MDCleanserOperation cloneCop = new MDCleanserOperation("");
		cloneCop.setOperationName(this.operationName);
		cloneCop.setOperationDescription(this.operationDescription);
		cloneCop.setSourceFieldName(this.sourceFieldName);
		cloneCop.setPassThrough(this.passThrough);
		HashMap<String, CleanserRule> clonedMap = new HashMap<String, CleanserRule>();
		
		for(Entry<String, CleanserRule> ruleEntry : this.cleanerRules.entrySet()){
			CleanserRule clonedRule = ruleEntry.getValue().clone();
			clonedMap.put(ruleEntry.getKey(), clonedRule);
			
		}
		
				//(MDCleanserOperation) super.clone();
		cloneCop.setClenserObjectsMap(clonedMap);
		
		
		return cloneCop;
	}

	public void dispose() {
		for (Entry<String, CleanserRule> entry : cleanerRules.entrySet()) {
			CleanserRule co = entry.getValue();
			co.deleteRuleObject();
		}
	}

	public HashMap<String, CleanserRule> getCleanserRuleMap() {

		return cleanerRules;
	}

	public String getOperationDescription() {
		if(operationDescription == null)
			return "";
		return operationDescription;
	}

	public String getOperationName() {

		return operationName;
	}

	public String getSourceFieldName() {

		return sourceFieldName;
	}

	public void init(LogChannelInterface log, boolean append, VariableSpace space) throws KettleException {
		this.log = log;
		this.appendField = append;
		if(log != null && log.isDebug()&& !cleanerRules.isEmpty())
			log.logDebug("Initializing Cleanser Rule objects for field: " + sourceFieldName);
		for (Entry<String, CleanserRule> entry : cleanerRules.entrySet()) {
			CleanserRule co = entry.getValue();
			
			try {
				co.initialize(log, space);
			} catch (DQTObjectException e) {
				throw new KettleException("Failed to initialize Operation: " + e.getMessage());
			}
		}
	}

	public void setClenserObjectsMap(HashMap<String, CleanserRule> mapObj) {
		cleanerRules = mapObj;
	}

	public void setColumnValues(MDCleanserRequest req) {

		String name = "";
		String value = null;
		columns.clear();

		for (int h = 0; h < req.inputDataSize; h++) {
			name = req.inputMeta.getValueMeta(h).getName();

			if(req.inputData[h] != null)
				value = String.valueOf(req.inputData[h]);
			else
				value = "";
			columns.put(name, value);
		}

	}

	public void setOperationDescription(String operationDescription) {

		this.operationDescription = operationDescription;
	}

	public void setOperationName(String operationName) {

		this.operationName = operationName;
	}

	public void setSourceFieldName(String sourceFieldName) {

		this.sourceFieldName = sourceFieldName;
	}

	public LogChannelInterface getLog() {

		return log;
	}

	public void setLog(LogChannelInterface log) {

		this.log = log;
	}
	
	
	public boolean isAppendField() {
	
		return appendField;
	}

	
	public void setAppendField(boolean appendField) {
	
		this.appendField = appendField;
	}

	public boolean isPassThrough() {

		return passThrough;
	}

	public void setPassThrough(boolean passThrough) {

		this.passThrough = passThrough;
	}
}
