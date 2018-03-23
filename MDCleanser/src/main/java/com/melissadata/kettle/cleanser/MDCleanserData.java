package com.melissadata.kettle.cleanser;

import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.w3c.dom.Node;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.support.IOMetaHandler;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cleanser.request.MDCleanserRequest;


public class MDCleanserData extends BaseStepData implements StepDataInterface {

	 public MDCleanserData(/*IOMetaHandler sourceIO*/) {

		super();
//		this.sourceIO = sourceIO;
//		this.inputMeta = sourceIO.inputMeta;
	}
	 private static final String	TAG_CLEANSER_OPERATION_PATH				= "cleanser_operations_path";

	// Structure for handling of input/output records
	public IOMetaHandler			sourceIO;
	// Structure of input and output records
	public RowMetaInterface			inputMeta;
	public RowMetaInterface			outputMeta;
	public List<MDCleanserRequest>	requests;
	public String					realLicense;
	public String					realDataPath;
	public int 						maxRequests;
	public String					cleanserOperationsPath;
	
	public HashMap<String, MDCleanserOperation> lsSavedOperations;
	
	public void init(){
		realLicense = MDProps.getProperty(MDPropTags.TAG_PRIMARY_LICENSE, "");
		cleanserOperationsPath = MDProps.getProperty(TAG_CLEANSER_OPERATION_PATH, Const.getKettleDirectory());
	}
	
}
