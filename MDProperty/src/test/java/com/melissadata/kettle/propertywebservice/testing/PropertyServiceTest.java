package com.melissadata.kettle.propertywebservice.testing;

import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceData;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.request.MDPropertyWebServiceRequest;
import com.melissadata.kettle.propertywebservice.request.PropertyWebServiceRequestHandler;
import com.melissadata.kettle.propertywebservice.web.MDPropertyWebService;
import com.melissadata.kettle.propertywebservice.web.WebClient;
import junit.framework.TestCase;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.pentaho.di.core.exception.KettleException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Kevin on 2/13/2018.
 */
public class PropertyServiceTest extends TestCase {

	private URL                               propertyURL        = null;
	private String                            request            = "";
	private StringBuffer                      response           = null;
	private int                               attempts           = 1;
	private Document                          requestDoc         = null;
	private org.dom4j.Document                responseDoc        = null;
	private MDPropertyWebService              propertyWebService = null;
	private MDPropertyWebServiceData          propertyData       = null;
	private MDPropertyWebServiceMeta          propertyMeta       = null;
	private PropertyWebServiceRequestHandler  handler            = null;
	private List<MDPropertyWebServiceRequest> requests           = null;
	private SAXReader                         saxReader          = null;
	private int                               totalFields        = 0;

	protected void setUp() {

		System.out.println("SetUp: " + this.getName());
		getPropertyMeta();
		getPropertyData();
		createRequestDoc();
		saxReader = new SAXReader();
		try {
			propertyURL = new URL("https://property.melissadata.net/v4/WEB/LookupProperty");
		} catch (MalformedURLException me) {
			me.printStackTrace();
		}
	}

	public void testCall() {

		int status = 400;
		do {
			// sometimes we get a time out
			//System.out.println("Status 400 Retry");
			status = call();
		} while (status == 400);

		assertEquals(200, status);
	}

	private int call() {

		int status = 0;
		response = new StringBuffer();

		WebClient webClient = new WebClient(null);

		try {
			status = webClient.call(propertyURL, request, response, attempts);
		} catch (KettleException ke) {
			System.out.println("Error testing call: " + ke.getCause());
		}

		if (status == 200) {
			try {
				responseDoc = saxReader.read(new StringReader(response.toString()));
			} catch (DocumentException de) {
				System.out.println("Error reading DOC: " + de.getCause());
			}
		}

		return status;
	}

	public void testDocFields() {

		int     fieldCount = 0;
		int     status     = 400;
		boolean failed     = false;
		do {
			// sometimes we get a time out
			//System.out.println("Status 400 Retry");
			status = call();
		} while (status == 400);
		System.out.println("Checking Response");
		org.dom4j.Element       response = responseDoc.getRootElement();
		org.dom4j.Element       records  = response.element("Records");
		List<org.dom4j.Element> nodes    = records.elements();
		org.dom4j.Element       element  = nodes.get(0);

		PropertyWebServiceFields propertyWebServiceFields = new PropertyWebServiceFields();
		propertyWebServiceFields.init();

		org.dom4j.Element nodeHolder = null;
		String            tag        = null;
		String            val        = null;
		for (String key : propertyWebServiceFields.outputFields.keySet()) {
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PARCEL_PREFIX)) {
				nodeHolder = element.element("Parcel");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_LEGAL_PREFIX)) {
				nodeHolder = element.element("Legal");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_PREFIX)) {
				nodeHolder = element.element("PropertyAddress");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PARSED_PROPERTY_PREFIX)) {
				nodeHolder = element.element("ParsedPropertyAddress");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PRIMARY_OWNER_PREFIX)) {
				nodeHolder = element.element("PrimaryOwner");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_SECONDARY_OWNER_PREFIX)) {
				nodeHolder = element.element("SecondaryOwner");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_OWNER_ADDRESS_PREFIX)) {
				nodeHolder = element.element("OwnerAddress");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_LAST_DEED_OWNER_PREFIX)) {
				nodeHolder = element.element("LastDeedOwnerInfo");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_CURRENT_DEED_PREFIX)) {
				nodeHolder = element.element("CurrentDeed");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_TAX_PREFIX)) {
				nodeHolder = element.element("Tax");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_USE_PREFIX)) {
				nodeHolder = element.element("PropertyUseInfo");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_SALE_INFO_PREFIX)) {
				nodeHolder = element.element("SalesInfo");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_SIZE_PREFIX)) {
				nodeHolder = element.element("PropertySize");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_POOL_PREFIX)) {
				nodeHolder = element.element("Pool");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_INTER_STRUCT_PREFIX)) {
				nodeHolder = element.element("IntStructInfo");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_INTER_ROOM_PREFIX)) {
				nodeHolder = element.element("IntRoomInfo");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_INT_AMMENITIES_PREFIX)) {
				nodeHolder = element.element("IntAmenities");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX)) {
				nodeHolder = element.element("ExtStructInfo");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_EXT_AMMENITIES_PREFIX)) {
				nodeHolder = element.element("ExtAmenities");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_EXT_BUILDINGS_PREFIX)) {
				nodeHolder = element.element("ExtBuildings");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_UTILITIES_PREFIX)) {
				nodeHolder = element.element("Utilities");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_PARKING_PREFIX)) {
				nodeHolder = element.element("Parking");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_YARD_GARDEN_PREFIX)) {
				nodeHolder = element.element("YardGardenInfo");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}

			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_ESTIMATED_VALUE_PREFIX)) {
				nodeHolder = element.element("EstimatedValue");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}
			if (key.startsWith(PropertyWebServiceFields.TAG_OUTPUT_SHAPE_PREFIX)) {
				nodeHolder = element.element("Shape");
				tag = propertyWebServiceFields.outputFields.get(key).webTag;
				val = nodeHolder.elementText(tag);
				if (nodeHolder.elementText(tag) == null) {
					failed = true;
				}
			}


			System.out.println("Key: " + key + " | Tag: " + tag + " | Value: " + val);
		}

		assertFalse(failed);
	}

	private void getPropertyMeta() {

		try {
			propertyMeta = new MDPropertyWebServiceMeta(true);
		} catch (KettleException ke) {
			System.out.println("get meta error : " + ke.getCause());
		}
		propertyMeta.setDefault();
	}

	private void getPropertyData() {

		if (propertyMeta == null) {
			getPropertyMeta();
		}

		propertyData = (MDPropertyWebServiceData) propertyMeta.getStepData();
	}

	private void createRequestDoc() {

		try {
			requestDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation().createDocument(null, "LookupPropertyRequest", null);
			;
		} catch (ParserConfigurationException | TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		}
		MDPropertyWebService.addTextNode(requestDoc, requestDoc.getDocumentElement(), "TransmissionReference", "" + "Junit");

		// Add customer id
		MDPropertyWebService.addTextNode(requestDoc, requestDoc.getDocumentElement(), "CustomerId", "" + "WX76N+MsiA2qM5UTXCexMS==VCbJ0Enrx7KJXnEbJl9UUF==o9vKgGqwvXM/JfjrT4L2w4==Wmpf1ChruKVn3sURjzuNsz==VPByMXp89p1csHxtPUtApk==");

		MDPropertyWebService.addTextNode(requestDoc, requestDoc.getDocumentElement(), "Columns", "" + "GrpAll");

		MDPropertyWebService.addTextNode(requestDoc, requestDoc.getDocumentElement(), "TotalRecords", "" + "1");

		Element root          = requestDoc.getDocumentElement();
		Element records       = requestDoc.createElement("Records");
		Element requestRecord = requestDoc.createElement("RequestRecord");

		// Add unique record id
		MDPropertyWebService.addTextNode(requestDoc, requestRecord, "RecordID", "" + "0");
		MDPropertyWebService.addTextNode(requestDoc, requestRecord, "AddressKey", "" + "92037743712");
		records.appendChild(requestRecord);
		root.appendChild(records);

		// Convert to XML
		StringWriter sw        = new StringWriter();
		DOMSource    domSource = new DOMSource(requestDoc);
		try {
			TransformerFactory.newInstance().newTransformer().transform(domSource, new StreamResult(sw));
		} catch (TransformerException te) {
			te.printStackTrace();
		}
		request = sw.toString();
	}
}
