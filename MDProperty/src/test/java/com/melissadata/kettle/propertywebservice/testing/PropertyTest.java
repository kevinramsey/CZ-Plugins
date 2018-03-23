package com.melissadata.kettle.propertywebservice.testing;

import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceData;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceDialog;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceStep;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.ui.InteriorOutputTab;
import junit.framework.TestCase;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.core.PropsUI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 2/12/2018.
 */
public class PropertyTest extends TestCase {

	private TransMeta                transMeta                = null;
	private String                   fileName                 = null;
	private PropertyWebServiceFields propertyWebServiceFields = null;
	private MDPropertyWebServiceMeta propertyMeta             = null;
	private MDPropertyWebServiceData propertyWebServiceData   = null;
	private Node                     propertyNode             = null;
	private List<DatabaseMeta>       databases                = null;
	private Map<String, Counter>     counters                 = null;
	private int                      totalFields              = 0;
	private int                      totalGroups              = 25;
	private MDPropertyWebServiceDialog dialog;
	private PropsUI props;
	private InteriorOutputTab interiorOutputTab = null;

	protected void setUp() {

		System.out.println("\nSetUP: " + this.getName());
		fileName = "..\\MDProperty(6.3.1)\\testSamples\\Property.xml";
		//System.out.println("The File = " + fileName);
	}

	private void getTransMeta() {

		try {
			transMeta = new TransMeta(fileName);
			databases = transMeta.getDatabases();
			counters = transMeta.getCounters();
		} catch (KettleException ke) {
			System.out.println("Error creating TransMeta: " + ke.getCause());
		}
	}

	private void getStepMeta() {

		if (transMeta == null) {
			getTransMeta();
		}
		//stepMeta = transMeta.getStep(1);

	}

	private void getPropertyMeta() {

		try {
			propertyMeta = new MDPropertyWebServiceMeta(true);
		} catch (KettleException ke) {
			write("get meta error : " + ke.getCause());
		}
		propertyMeta.setDefault();
	}

	private void getPropertyData() {

		if (propertyMeta == null) {
			getPropertyMeta();
		}

		propertyWebServiceData = (MDPropertyWebServiceData) propertyMeta.getStepData();
	}

	private void getPropertyFields() {

		if (propertyMeta == null) {
			getPropertyMeta();
		}
		propertyWebServiceFields = propertyMeta.propertyWebServiceFields;
		propertyWebServiceFields.init();
	}

	private void getDialog(){

		getTransMeta();
		getPropertyMeta();
		Display display = new Display();
		Shell shell = new Shell(display);
		if(!PropsUI.isInitialized()){
			PropsUI.init(display,0);
		}

		dialog = new MDPropertyWebServiceDialog(shell,propertyMeta,transMeta, "test");
		dialog.initForTests(shell);

		System.out.println(" GOT DIALOG = " + dialog);
	}

	private  void getInteriorOutputTab(){

		if(dialog == null){
			getDialog();
		}

		interiorOutputTab = new InteriorOutputTab(dialog);

	}

	public void testGetTransMetaFromFile() {

		getTransMeta();
		assertTrue(transMeta != null);
	}

	public void testGetStepNode() {

		Document document = null;
		try {
			document = XMLHandler.loadXMLFile(fileName);
		} catch (Exception e) {
			System.out.println("Error getting document from file " + e.getCause());
		}

		Node       node  = XMLHandler.getSubNode(document, "transformation");
		List<Node> nodes = XMLHandler.getNodes(node, "step");
		for (Node nd : nodes) {
			if (XMLHandler.getTagValue(nd, "name").equals("MD Property Service")) {
				propertyNode = nd;
			}
			if (propertyNode != null) {
				break;
			}
		}
		assertNotNull(propertyNode);
	}

	public void testGetPropertyMeta() {

		if (propertyMeta == null) {
			getPropertyMeta();
		}

		assertNotNull(propertyMeta);
	}

	public void testGetXml() {

		if (propertyMeta == null) {
			getPropertyMeta();
		}

		String xml = null;
		propertyMeta.setDefault();
		try {
			xml = propertyMeta.getXML();
		} catch (KettleException ke) {
			System.out.println("Error getting xml: " + ke.getCause());
		}
		assertTrue(xml.length() > 15);
	}

	public void testLoadXml() {

		if (propertyMeta == null) {
			getPropertyMeta();
		}
		try {
			propertyMeta.loadXML(propertyNode, databases, counters);
		} catch (KettleException ke) {

		}
	}

	public void testGetStepData() {

		getPropertyData();

		assertNotNull(propertyWebServiceData);
	}

//	public void testFields() {
//
//		if (propertyWebServiceFields == null) {
//			getPropertyFields();
//		}
//
//		boolean hasDupe = false;
//
//		ArrayList<String> list = new ArrayList<String>();
//
//		for (String key : propertyWebServiceFields.optionFields.keySet()) {
//			if (!list.contains(key)) {
//				//System.out.println(" KEY = " + key);
//				list.add(key);
//			} else {
//				hasDupe = true;
//			}
//
//			assertFalse(hasDupe);
//		}
//	}

	public void testInteriorOutputTab(){
		getInteriorOutputTab();
		getPropertyMeta();
		interiorOutputTab.init(propertyMeta);
		interiorOutputTab.getData(propertyMeta);

		boolean hasDupe = false;

		ArrayList<String> list = new ArrayList<String>();

		for (MetaVal mv : propertyMeta.propertyWebServiceFields.outputFields.values()) {
			String outTag = mv.metaValue;
			if (!list.contains(outTag)) {
				System.out.println(" out = " + outTag);
				list.add(outTag);
			} else {
				hasDupe = true;
				System.out.println(" Dupe Found = " + outTag);
			}


		}
		assertFalse(hasDupe);

	}

	public void testIncluded() {

		getPropertyFields();
		boolean pass = true;

		HashMap<String, MetaVal> optionFields = propertyWebServiceFields.outputFields;

		for (String key : optionFields.keySet()) {

			if (!key.equals(PropertyWebServiceFields.TAG_OUTPUT_RESULTS)) {
				if (!propertyWebServiceFields.included(key)) {
					System.out.println("The key  = " + key + " is not included");
					pass = false;
				}
			}
		}

		assertTrue(pass);
	}

	public void testOutputNames(){

		if (propertyWebServiceFields == null) {
			getPropertyFields();
		}

		boolean hasDupe = false;

		ArrayList<String> list = new ArrayList<String>();

		for (MetaVal mv : propertyWebServiceFields.outputFields.values()) {
			String outTag = mv.metaValue;
			if (!list.contains(outTag)) {
				//System.out.println(" out = " + outTag);
				list.add(outTag);
			} else {
				hasDupe = true;
				System.out.println(" Dupe Found = " + outTag);
			}


		}
		assertFalse(hasDupe);

	}

	//execute before class
	@BeforeClass
	public static void beforeClass() {

		System.out.println("in before class");
	}

	//execute after class
	@AfterClass
	public static void afterClass() {

		System.out.println("in after class");
	}

	//execute before test
	@Before
	public void before() {

		System.out.println("in before");
	}

	//execute after test
	@After
	public void after() {

		System.out.println("in after");
	}

	//tearDown used to close the connection or clean up activities
	public void tearDown() {

	}

	public void write(String msg) {

		System.out.println(msg);
	}
}
