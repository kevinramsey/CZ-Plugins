package com.melissadata.kettle.MDSettings;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.melissadata.cz.support.MDPropTags;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MDSettingsWebService {

	/**
	 * @param xmlDoc
	 * @param parent
	 * @param tagName
	 * @param data
	 * @throws DOMException
	 */
	public static void addTextNode(Document xmlDoc, Element parent, String tagName, String data) throws DOMException {

		Element e = xmlDoc.createElement(tagName);
		Node    n = xmlDoc.createTextNode(data);
		e.appendChild(n);
		parent.appendChild(e);
//		System.out.println("------- add Node --------------------------\n");
//		System.out.println("" + e.toString());
//		System.out.println("\n -------   " );
	}

	/**
	 * @param record
	 * @param name
	 * @return
	 * @throws KettleException
	 */
	public static int getElementInteger(org.dom4j.Element record, String name) throws KettleException {

		try {
			String text = record.elementText(name);
			if (text == null) {
				throw new KettleException("Could not find integer value for " + name);
			}
			int value = Integer.valueOf(text);
			return value;
		} catch (NumberFormatException e) {
			throw new KettleException("Problem getting integer value", e);
		}
	}

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	public static String getElementText(org.dom4j.Element element, String name) {

		String text = element.elementText(name);
		if (text == null) {
			return "";
		}
		return text.trim();
	}

	private DOMImplementation       domImplentation;
	private Transformer             serializer;
	private SAXReader               saxReader;
	private boolean                 processUsingWebService;
	private AdvancedConfigInterface acInterface;
	private MDSettingsData          settingData;
	private VariableSpace           space;
	private WebClient               webClient;
	private WebClient               cvsClient;
	private String                  executionID;
	private String                  jobID;
	private String                  listName;

	public MDSettingsWebService(AdvancedConfigInterface acInterface, MDSettingsData data) {

		settingData = data;
		this.acInterface = acInterface;
		space = acInterface.getSpace();
	}

	/**
	 * Constructs a new local appliance URL from a single server url by extracting its components and rebuilding it with the
	 * specified prefix and suffix
	 *
	 * @param url
	 * @param service
	 * @param suffix
	 * @return
	 * @throws MalformedURLException
	 */
	private URL buildCVSURL(URL url, String service, String suffix) throws MalformedURLException {

		String protocol = url.getProtocol();
		String host     = url.getHost();
		int    port     = url.getPort();
		String path     = url.getPath();
		String query    = url.getQuery();
		host = host + "/" + service + "/xml/service.svc";
		query = (query != null) ? "?" + query : "";
		String file = path + "/" + suffix + query;
		if (port == -1) {
			return new URL(protocol, host, file);
		} else {
			return new URL(protocol, host, port, file);
		}
	}

	public void buildWebRequest(Document xmlDoc, int nProduct, String gvName) throws KettleException {

		Element root  = xmlDoc.getDocumentElement();
		boolean name  = false;
		boolean addr  = false;
		boolean phone = false;
		boolean email = false;
		if (gvName != null) {
			name = gvName.equals("name");
			addr = gvName.equals("Addr");
			phone = gvName.equals("phone");
			email = gvName.equals("email");
		}
		// Address
		if (nProduct == MDPropTags.MDLICENSE_Address) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			addTextNode(xmlDoc, record, "RecordID", "1");
			addTextNode(xmlDoc, record, "AddressLine1", "1600 Pennsylvania Ave.");
			addTextNode(xmlDoc, record, "City", "Washington");
			addTextNode(xmlDoc, record, "State", "DC");
			root.appendChild(record);
		}
		// RBDI
		if (nProduct == MDPropTags.MDLICENSE_RBDI) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			addTextNode(xmlDoc, record, "RecordID", "1");
			addTextNode(xmlDoc, record, "AddressKey", "97223");
			root.appendChild(record);
		}
		// phone
		if (nProduct == MDPropTags.MDLICENSE_Phone) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			root.appendChild(record);
			addTextNode(xmlDoc, record, "RecordID", "1");
			addTextNode(xmlDoc, record, "Phone", "8005551212");
		}
		// name
		if (nProduct == MDPropTags.MDLICENSE_Name) {
			// Local only for now
			// Create new record object
			/*
			 * Element record = xmlDoc.createElement("Record"); addTextNode(xmlDoc, record, "RecordID", "1");
			 * addTextNode(xmlDoc, record, "AddressLine1", "1600 Pennsylvania Ave."); addTextNode(xmlDoc, record, "City",
			 * "Washington");
			 * addTextNode(xmlDoc, record, "State", "DC"); root.appendChild(record);
			 */
		}
		// Email
		if (nProduct == MDPropTags.MDLICENSE_Email) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			root.appendChild(record);
			addTextNode(xmlDoc, record, "RecordID", "0");
			addTextNode(xmlDoc, record, "Email", "root@google.com");
		}
		// geoCoder
		if (nProduct == MDPropTags.MDLICENSE_GeoCode) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			addTextNode(xmlDoc, record, "RecordID", "1");
			addTextNode(xmlDoc, record, "AddressKey", "97223");
			root.appendChild(record);
		}
		// IP locator
		if (nProduct == MDPropTags.MDLICENSE_IPLocator) {
			// Converted to Json

		}
		// GlobalVerify
		if (nProduct == MDPropTags.MDLICENSE_GlobalVerify) {
			if (name) {
				Element records = xmlDoc.createElement("Records");
				Element record  = xmlDoc.createElement("RequestRecord");
				addTextNode(xmlDoc, record, "RecordID", "1");
				addTextNode(xmlDoc, record, "FullName", "Kevin Ramsey");
				records.appendChild(record);
				root.appendChild(records);
			}
			if (addr) {
				// Create new record object
				addTextNode(xmlDoc, root, "LineSeparator", ",");
				addTextNode(xmlDoc, root, "CountryOfOrigin", "USA");
				addTextNode(xmlDoc, root, "OutputScript", "NOCHANGE");
				Element records = xmlDoc.createElement("Records");
				Element record  = xmlDoc.createElement("RequestRecord");
				addTextNode(xmlDoc, record, "RecordID", "0");
				addTextNode(xmlDoc, record, "SubNationalArea", "DC");
				addTextNode(xmlDoc, record, "AddressLine1", "1600 Pennsylvania Ave.");
				addTextNode(xmlDoc, record, "Locality", "Washington");
				addTextNode(xmlDoc, record, "PostalCode", "20500");
				addTextNode(xmlDoc, record, "Country", "USA");
				records.appendChild(record);
				root.appendChild(records);
			}
			if (phone) {
				Element records = xmlDoc.createElement("Records");
				Element record  = xmlDoc.createElement("RequestRecord");
				addTextNode(xmlDoc, record, "RecordID", "1");
				addTextNode(xmlDoc, record, "PhoneNumber", "8005551212");
				addTextNode(xmlDoc, record, "Country", "USA");
				addTextNode(xmlDoc, record, "CountryOfOrigin", "USA");
				records.appendChild(record);
				root.appendChild(records);
			}
			if (email) {
				Element records = xmlDoc.createElement("Records");
				Element record  = xmlDoc.createElement("RequestRecord");
				addTextNode(xmlDoc, record, "RecordID", "0");
				addTextNode(xmlDoc, record, "Email", "root@google.com");
				records.appendChild(record);
				root.appendChild(records);
			}
		}
		// Address
		if (nProduct == MDPropTags.MDLICENSE_Personator) {
			// Create new record object
			addTextNode(xmlDoc, root, "Actions", "Check");
			//addTextNode(xmlDoc, root, "Actions", "Check;Verify;Append");
			addTextNode(xmlDoc, root, "Options", "");
			addTextNode(xmlDoc, root, "Columns", "");
			Element records = xmlDoc.createElement("Records");
			Element record  = xmlDoc.createElement("RequestRecord");
			addTextNode(xmlDoc, record, "RecordID", "0");
			addTextNode(xmlDoc, record, "CompanyName", "");
			addTextNode(xmlDoc, record, "FullName", "John Smith");
			addTextNode(xmlDoc, record, "AddressLine1", "1600 Pennsylvania Ave.");
			addTextNode(xmlDoc, record, "AddressLine2", "");
			addTextNode(xmlDoc, record, "Suite", "");
			addTextNode(xmlDoc, record, "City", "Washington");
			addTextNode(xmlDoc, record, "State", "DC");
			addTextNode(xmlDoc, record, "PostalCode", "20500");
			addTextNode(xmlDoc, record, "Country", "USA");
			addTextNode(xmlDoc, record, "PhoneNumber", "8005551212");
			addTextNode(xmlDoc, record, "EmailAddress", "root@google.com");
			records.appendChild(record);
			root.appendChild(records);
		}
		// SmartMover
		if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
			// Create new record object
			Element records = xmlDoc.createElement("Records");
			Element record  = xmlDoc.createElement("RequestRecord");
			addTextNode(xmlDoc, record, "RecordID", "0");
			addTextNode(xmlDoc, record, "NameFull", "John Smith");
			addTextNode(xmlDoc, record, "CompanyName", "");
			addTextNode(xmlDoc, record, "AddressLine1", "1600 Pennsylvannia Ave");
			addTextNode(xmlDoc, record, "AddressLine2", "");
			addTextNode(xmlDoc, record, "City", "Washington");
			addTextNode(xmlDoc, record, "State", "DC");
			addTextNode(xmlDoc, record, "PostalCode", "20500");
			records.appendChild(record);
			root.appendChild(records);
		}

		if (nProduct == MDPropTags.MDLICENSE_Property) {
			// Create new record object
			Element record = xmlDoc.createElement("Record");
			addTextNode(xmlDoc, record, "RecordID", "1");
			addTextNode(xmlDoc, record, "AddressKey", "92688211282");
			addTextNode(xmlDoc, record, "FIPS", "");
			addTextNode(xmlDoc, record, "APN", "");
			root.appendChild(record);
		}
	}

	/**
	 * Called to convert a DOM object to XML in a thread safe manner
	 *
	 * @param requestDoc
	 * @return
	 * @throws TransformerException
	 */
	private synchronized String convertToXML(Document requestDoc) throws TransformerException {

		// Convert to XML
		StringWriter sw        = new StringWriter();
		DOMSource    domSource = new DOMSource(requestDoc);
		serializer.transform(domSource, new StreamResult(sw));
		String xmlRequest = sw.toString();
		return xmlRequest;
	}

	/**
	 * Creates a new document in a thread save manner
	 *
	 * @return
	 */
	private synchronized Document createDocument(boolean isAltType) {

		if (isAltType) {
			return domImplentation.createDocument(null, "Request", null);
		} else {
			return domImplentation.createDocument(null, "RequestArray", null);
		}
	}

	private void doWebRequests(int nProduct, String gvName) throws KettleException {

		try {
			// Create the request document
			boolean isGlobalVerify    = (nProduct == MDPropTags.MDLICENSE_GlobalVerify);
			boolean isPersonator      = (nProduct == MDPropTags.MDLICENSE_Personator);
			boolean isPersonatorWorld = (nProduct == MDPropTags.MDLICENSE_PersonatorWorld);
			boolean isSmartMover      = (nProduct == MDPropTags.MDLICENSE_SmartMover);
			boolean isBusCoder        = (nProduct == MDPropTags.MDLICENSE_BusinessCoder);
			boolean isProperty        = (nProduct == MDPropTags.MDLICENSE_Property);
			boolean altDoc            = (isGlobalVerify || isPersonator || isSmartMover);
			boolean name              = false;
			boolean addr              = false;
			boolean phone             = false;
			boolean email             = false;
			if (gvName != null) {
				name = gvName.equals("name");
				addr = gvName.equals("Addr");
				phone = gvName.equals("phone");
				email = gvName.equals("email");
			}
			Document requestDoc = createDocument(altDoc);
			// Add customer id
			if (isProperty) {
				requestDoc = domImplentation.createDocument(null, "LookupPropertyRequest", null);
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "TransmissionReference", "");
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "CustomerId", "" + settingData.primeLicense.licenseString);
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "Columns", "" + "GrpAll");
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "TotalRecords", "" + "1");

				Element recordsElement = requestDoc.createElement("Records");

				Element requestRecord = requestDoc.createElement("RequestRecord");
				addTextNode(requestDoc, requestRecord, "RecordID", "" + "0");
				addTextNode(requestDoc, requestRecord, "AddressKey", "" + "0");

				recordsElement.appendChild(requestRecord);
				requestDoc.getDocumentElement().appendChild(recordsElement);
			} else {
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "CustomerID", "" + getCustomerID(nProduct));
			}
			// http://wiki.melissadata.com/index.php?title=SmartMover_V3
			if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
				String opts = "ProcessingType:Standard," + "ListOwnerFreqProcessing:1,NumOfMonthRequested:24";
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "Options", opts);
				// not used leave blank
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "TransmissionReference", "");
				// we only do ncoa right now so I think it can be left blank
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "Actions", "");// NCOA,CCOA
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "Columns", "grpStandardized,grpName,grpParsed,Plus4,PrivateMailBox,Suite,DPVFootNotes,MoveReturnCode");
				// Leave blank this is retrieved by the service
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "PAFId", "");
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "JobID", jobID);
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "ExecutionID", executionID);
				addTextNode(requestDoc, requestDoc.getDocumentElement(), "OptSmartMoverListName", listName);
			}
			String xmlRequest = null;
			if (isBusCoder) {
				xmlRequest = "?comp=Melissadata&a1=&city=&state=&postal=&id=" + URLEncoder.encode(getCustomerID(nProduct), "UTF-8") + "&cols=GrpAll&t=1234&mak=&ctry=&opt=ReturnDominantBusiness:no";
			} else {
				// Build the request document from the request data
				buildWebRequest(requestDoc, nProduct, gvName);
				// Convert the document object to an XML request string
				xmlRequest = convertToXML(requestDoc);
			}
			// If there was at least one record then we send the request and process the response
			if (xmlRequest != null) {
				// Post the requests and get the responses
				StringBuffer xmlResponse = new StringBuffer();
				int          statusCode  = 0;
				if (nProduct == MDPropTags.MDLICENSE_Address) {
					// Call Adr web service
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_CONTACTVERIFY).call(settingData.realWebAddressVerifierURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realCVSAddressVerifierURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_RBDI) {

					// Call rbdi web service
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_CONTACTVERIFY).call(settingData.realWebRBDIndicatorURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realCVSRBDIndicatorURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_Phone) {
					// Call phone web service
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_CONTACTVERIFY).call(settingData.realWebPhoneVerifierURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realCVSPhoneVerifierURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_Name) {
					// Call name web service
					// statusCode = getWebClient().call(settingData.realWebNameVerifierURL, xmlRequest, xmlResponse);
				}
				if (nProduct == MDPropTags.MDLICENSE_Email) {
					// Call Email web service
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_CONTACTVERIFY).call(settingData.realWebEmailVerifierURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realCVSEmailVerifierURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_GeoCode) {
					// Call Geo web service
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_CONTACTVERIFY).call(settingData.realWebGeoCoderURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realCVSGeoCoderURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_IPLocator) {
					JSONObject mainObj = new JSONObject();
					mainObj.put("CustomerID", getCustomerID(nProduct));
					mainObj.put("TransmissionReference", "");
					mainObj.put("Options", "");
					JSONArray  jsonArray = new JSONArray();
					JSONObject jo        = null;
					jo = new JSONObject();
					jo.put("RecordID", "0");
					jo.put("IPAddress", "123.456.789.123");
					jsonArray.add(jo);

					mainObj.put("Records", jsonArray);

					// Convert the document object to an XML request string
					xmlRequest = mainObj.toJSONString();
					// Call IP web service
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_IPLOCATOR).call(settingData.realWebIPLocatorURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realCVSIPLocatorURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_GlobalVerify) {
					if (processUsingWebService) {
						if (name) {
							statusCode = getWebClient(MDPropTags.MENU_ID_GLOBALVERIFY).call(settingData.realGlobalNameURL, xmlRequest, xmlResponse);
						}
						if (addr) {
							statusCode = getWebClient(MDPropTags.MENU_ID_GLOBALVERIFY).call(settingData.realGlobalAddressURL, xmlRequest, xmlResponse);
						}
						if (phone) {
							JSONObject mainObj = new JSONObject();
							mainObj.put("CustomerID", getCustomerID(nProduct));
							mainObj.put("TransmissionReference", "");
							mainObj.put("Options", "");
							JSONArray  jsonArray = new JSONArray();
							JSONObject jo        = null;
							jo = new JSONObject();
							jo.put("RecordID", "0");
							jo.put("PhoneNumber", "8006354772");
							jsonArray.add(jo);

							mainObj.put("Records", jsonArray);

							// Convert the document object to an XML request string
							xmlRequest = mainObj.toJSONString();
							statusCode = getWebClient(MDPropTags.MENU_ID_GLOBALVERIFY).call(settingData.realGlobalPhoneURL, xmlRequest, xmlResponse);
						}
						if (email) {
							statusCode = getWebClient(MDPropTags.MENU_ID_GLOBALVERIFY).call(settingData.realGlobalEmailURL, xmlRequest, xmlResponse);
						}
					} else {
						statusCode = getCVSClient().call(settingData.realCVSGlobalVerifyURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_Personator) {
					if (processUsingWebService) {

						statusCode = getWebClient(MDPropTags.MENU_ID_PERSONATOR).call(settingData.realPersonatorURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realPersonatorURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_PersonatorWorld) {

					JSONObject mainObj = new JSONObject();
					mainObj.put("CustomerID", getCustomerID(nProduct));
					mainObj.put("TransmissionReference", "");
					mainObj.put("Options", "");

					mainObj.put("Actions", "Check");

					// Convert the document object to an XML request string
					xmlRequest = mainObj.toJSONString();
					// Call IP web service

					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_PERSONATOR_WORLD).call(settingData.realPersonatorWorldURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realPersonatorWorldURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_Property) {
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_PROPERTY).call(settingData.realPropertyURL, xmlRequest, xmlResponse);
					} else {
						statusCode = getCVSClient().call(settingData.realPropertyURL, xmlRequest, xmlResponse);
					}
				}
				if (nProduct == MDPropTags.MDLICENSE_BusinessCoder) {
					statusCode = getWebClient(MDPropTags.MENU_ID_BUSINESS_CODER).call(settingData.realWebBusCoderURL, xmlRequest, xmlResponse);
				}
				if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
					if (processUsingWebService) {
						statusCode = getWebClient(MDPropTags.MENU_ID_SMARTMOVER).call(settingData.realNCOAURL, xmlRequest, xmlResponse);
						// else No cvs for smartMover
						// statusCode = getCVSClient().call(settingData.realCVSGlobalVerifyURL, xmlRequest, xmlResponse);
					}
				}
				// Check for problem
				if (statusCode != 200) {
					if (nProduct == MDPropTags.MDLICENSE_Address) {
						settingData.webAddrMsg = "address " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_RBDI) {
						settingData.webRBDIMsg = "RBDI " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_Phone) {
						settingData.webPhoneMsg = "phone " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_Name) {
						settingData.webNameMsg = "name " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_GeoCode) {
						settingData.webGeoMsg = "Geo " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_Email) {
						settingData.webEmailMsg = "email " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_IPLocator) {
						settingData.webIPMsg = "IpLocator " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
						settingData.webSMMsg = "Smart Mover " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_GlobalVerify) {

						if (name) {
							settingData.webGlobalNameMsg = "Global Name: " + statusCode;
						}
						if (addr) {
							settingData.webGlobalAddressMsg = "Global Address: " + statusCode;
						}
						if (phone) {
							settingData.webGlobalPhoneMsg = "Global Phone: " + statusCode;
						}
						if (email) {
							settingData.webGlobalEmailMsg = "Global Email: " + statusCode;
						}
					}
					if (nProduct == MDPropTags.MDLICENSE_Personator) {
						settingData.webPersonatorMsg = "Personator " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_PersonatorWorld) {
						settingData.webPersonatorWorldMsg = "Personator World" + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_BusinessCoder) {
						settingData.webBusCoderMsg = "Business Coder " + statusCode;
					}
					if (nProduct == MDPropTags.MDLICENSE_Property) {
						settingData.webPropertyMsg = "Property Service " + statusCode;
					}
				} else {
					String resultCodes;
					// Process responses and extract results into request object
					if (nProduct == MDPropTags.MDLICENSE_BusinessCoder) {
						JSONObject jsonResponse = new JSONObject();
						JSONParser parser       = new JSONParser();
						jsonResponse = (JSONObject) parser.parse(xmlResponse.toString());
						resultCodes = (String) jsonResponse.get("TransmissionResults");
						settingData.webBusCoderVersion = (String) jsonResponse.get("Version");
						settingData.webBusCoderMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
					} else if (nProduct == MDPropTags.MDLICENSE_IPLocator) {

						JSONObject jsonResponse = new JSONObject();
						JSONParser parser       = new JSONParser();
						jsonResponse = (JSONObject) parser.parse(xmlResponse.toString());
						resultCodes = (String) jsonResponse.get("TransmissionResults");
						settingData.webIPVersion = (String) jsonResponse.get("Version");
						settingData.webIPMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
					} else if (nProduct == MDPropTags.MDLICENSE_GlobalVerify && phone) {

						JSONObject jsonResponse = new JSONObject();
						JSONParser parser       = new JSONParser();
						jsonResponse = (JSONObject) parser.parse(xmlResponse.toString());
						resultCodes = (String) jsonResponse.get("TransmissionResults");
						settingData.webGlobalPhoneVersion = (String) jsonResponse.get("Version");
						settingData.webGlobalPhoneMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
					} else if (nProduct == MDPropTags.MDLICENSE_PersonatorWorld) {
						// FIXME PW
						JSONParser parser       = new JSONParser();
						JSONObject jsonResponse = (JSONObject) parser.parse(xmlResponse.toString());
						//jsonResponse = (JSONObject) parser.parse(xmlResponse.toString());
						resultCodes = (String) jsonResponse.get("Results");
						settingData.webPersonatorWorldVersion = (String) jsonResponse.get("Version");
						settingData.webPersonatorWorldMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
					} else {
						org.dom4j.Document doc = readXML(xmlResponse);
						resultCodes = processWebResponse(doc, nProduct, gvName);
						if (nProduct == MDPropTags.MDLICENSE_Address) {
							settingData.webAddrMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						if (nProduct == MDPropTags.MDLICENSE_RBDI) {
							settingData.webRBDIMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						if (nProduct == MDPropTags.MDLICENSE_Phone) {
							settingData.webPhoneMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						if (nProduct == MDPropTags.MDLICENSE_Name) {
							settingData.webNameMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						if (nProduct == MDPropTags.MDLICENSE_GeoCode) {
							settingData.webGeoMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						if (nProduct == MDPropTags.MDLICENSE_Email) {
							settingData.webEmailMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						if (nProduct == MDPropTags.MDLICENSE_IPLocator) {
							settingData.webIPMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						if (nProduct == MDPropTags.MDLICENSE_GlobalVerify) {
							if (name) {
								settingData.webGlobalNameMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
							}
							if (addr) {
								settingData.webGlobalAddressMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
							}
							if (phone) {
								// settingData.webGlobalPhoneMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
							}
							if (email) {
								settingData.webGlobalEmailMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
							}
						}
						if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
							settingData.webSMMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}

						if (nProduct == MDPropTags.MDLICENSE_Personator) {
							settingData.webPersonatorMsg = Const.isEmpty(resultCodes) ? "Success " : getResultString(resultCodes);
							if (!isLicensed(MDPropTags.MDLICENSE_Personator)) {
								settingData.webPersonatorMsg = "Not Licensed";
							}
						}

//						if (nProduct == MDPropTags.MDLICENSE_PersonatorWorld) {
//							settingData.webPersonatorWorldMsg = Const.isEmpty(resultCodes) ? "Success " : getResultString(resultCodes);
//							if (!isLicensed(MDPropTags.MDLICENSE_PersonatorWorld)) {
//								settingData.webPersonatorMsg = "Not Licensed";
//							}
//						}

						if (nProduct == MDPropTags.MDLICENSE_Property) {
							settingData.webPropertyMsg = Const.isEmpty(resultCodes) ? "Success " : resultCodes;
						}
						// TODO: if result codes contains SE01 then retry request
						if (resultCodes.contains("SE01")) {
							// this is just to see where an error happens
							// sometimes we get no version# from web
							settingData.webAddrMsg = "Server Error SE01";
						}
					}
				}
			}
		} catch (KettleException e) {
			// Re-throw
			throw e;
		} catch (Throwable t) {
			// If anything unusual happened
			throw new KettleException("ProcessService Error" + t.toString());
		}
	}

	private String getCustomerID(int nProduct) {


		int addr = MDPropTags.MDLICENSE_Address;
		int ph = MDPropTags.MDLICENSE_Phone;
		int em = MDPropTags.MDLICENSE_Email;
		int go = MDPropTags.MDLICENSE_GeoCode;
		int rb = MDPropTags.MDLICENSE_RBDI;

		int    retVal = settingData.primeLicense.retVal;
		String id     = "";
		if (nProduct == MDPropTags.MDLICENSE_BusinessCoder) {
			return settingData.primeLicense.licenseString;
		}

		if ((retVal & nProduct) != 0) {
			if (nProduct == MDPropTags.MDLICENSE_Address || nProduct == MDPropTags.MDLICENSE_Address || nProduct == MDPropTags.MDLICENSE_Phone || nProduct == MDPropTags.MDLICENSE_Email || nProduct == MDPropTags.MDLICENSE_GeoCode
			    || nProduct == MDPropTags.MDLICENSE_Name || nProduct == MDPropTags.MDLICENSE_RBDI) {
				id = settingData.primeLicense.CustomerID;
			} else	{
				id = settingData.primeLicense.licenseString;
			}
		} else {
			retVal = settingData.trialLicense.retVal;
			if ((retVal & nProduct) != 0) {
				if (nProduct == MDPropTags.MDLICENSE_Address || nProduct == MDPropTags.MDLICENSE_Address || nProduct == MDPropTags.MDLICENSE_Phone || nProduct == MDPropTags.MDLICENSE_Email || nProduct == MDPropTags.MDLICENSE_GeoCode
				    || nProduct == MDPropTags.MDLICENSE_Name || nProduct == MDPropTags.MDLICENSE_RBDI) {
					id = settingData.trialLicense.CustomerID;
				} else	{
					id = settingData.trialLicense.licenseString;
				}
			}
		}

		return id;
	}

	/**
	 * @return The current instance of the local appliance client
	 */
	private WebClient getCVSClient() {

		// TODO: combine with getWebClient() for a single interface
		if (cvsClient == null) {
			// Create a web client
			cvsClient = new WebClient();
			cvsClient.setTimeout(settingData.realCVSTimeout);
			cvsClient.setRetries(settingData.realCVSRetries);
		}
		return cvsClient;
	}

	private String getResultString(String resultCode) {

		String resultString = "";
		if ("SE01".equals(resultCode)) {
			resultString = resultCode + " - " + "Web Service internal error";
		} else if ("GE01".equals(resultCode)) {
			resultString = resultCode + " - " + "Empty XML request structure";
		} else if ("GE02".equals(resultCode)) {
			resultString = resultCode + " - " + "Empty XML request record structure";
		} else if ("GE03".equals(resultCode)) {
			resultString = resultCode + " - " + "Counted records sent more than number of records allowed per request";
		} else if ("GE04".equals(resultCode)) {
			resultString = resultCode + " - " + "CustomerID is empty";
		} else if ("GE05".equals(resultCode)) {
			resultString = resultCode + " - " + "CustomerID is invalid";
		} else if ("GE06".equals(resultCode)) {
			resultString = resultCode + " - " + "CustomerID is disabled.";
		} else if ("GE07".equals(resultCode)) {
			resultString = resultCode + " - " + "XML request is invalid";
		} else if ("GE20".equals(resultCode)) {
			resultString = resultCode + " - " + "Verify is not activated on this account";
		} else if ("GE21".equals(resultCode)) {
			resultString = resultCode + " - " + "Append is not activated on this account";
		} else if ("GW01".equals(resultCode)) {
			resultString = resultCode + " - " + "The license will expire within 2 weeks";
		} else {
			resultString = resultCode;
		}
		return resultString;
	}

	public org.dom4j.Document getSummeryLink(String cuID, String pafId, String jobId) {

		int statCode = 0;
		URL sUrl     = null;
		try {
			sUrl = new URL("https://smartmover.melissadata.net/v3/WEB/SmartMover/GetSummaryReportLinks?&id=102214391&jobid=1234564&format=xml");
		} catch (MalformedURLException e1) {
			System.out.println("WARNING: Error Getting Report Summery Link: " + e1.getMessage());
		}

		String       request  = "<RequestSummaryReport><CustomerID>" + cuID + "</CustomerID><PAFId>" + pafId + "</PAFId><JobID>" + jobId + "</JobID></RequestSummaryReport>";
		StringBuffer response = new StringBuffer();
		try {
			statCode = webClient.call(sUrl, request, response);
		} catch (KettleException e) {
			System.out.println("WARNING: Error Getting Report Summery Link: " + e.getMessage());
		}
		if (statCode != 200) {
			System.out.println("WARNING: Error Getting Report Summery Link: status code " + statCode);
		}
		SAXReader          saxReader = new SAXReader();
		org.dom4j.Document doc       = null;
		if (Const.isEmpty(response)) {
			/*
			 * This is done so we have something to read and display on the other side
			 * So we can show things like 404 and 598 errors
			 */
			response = new StringBuffer("<Response xmlns=\"urn:mdSmartMover\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><TransmissionResults>SE" + statCode + "</TransmissionResults></Response>");
		}
		try {
			doc = saxReader.read(new StringReader(response.toString()));
		} catch (DocumentException e) {
			System.out.println("WARNING: Error Getting Report Summery Link: " + e.getMessage());
		}
		return doc;
	}

	/**
	 * @return The current instance of the web service client
	 */
	private WebClient getWebClient(String id) {

		// TODO: combine with getCVSClient() for a single interface
		if (webClient == null) {
			// Create a web client
			webClient = new WebClient();
			webClient.setTimeout(settingData.getTimeout(id));
			webClient.setRetries(settingData.getRetries(id));
			webClient.setProxy(settingData.realProxyHost, settingData.realProxyPort, settingData.realProxyUser, settingData.realProxyPass);
		}
		return webClient;
	}

	public void init() throws KettleException {

		try {
			// Create DOM object to build requests
			domImplentation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
			// Create Serializer to use to translate DOM into XML string
			serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
			// Create SAX reader to use when parsing responses
			saxReader = new SAXReader();
			// Get service URLs
			// no name service yet
			// settingData.realWebNameParserURL = new URL(space.environmentSubstitute(acMeta.getWebNameParserURL()));
			settingData.realNCOAURL = new URL(space.environmentSubstitute(acInterface.getWebNCOAURL()));
			settingData.realWebAddressVerifierURL = new URL(space.environmentSubstitute(acInterface.getWebAddressVerifierURL()));
			settingData.realWebGeoCoderURL = new URL(space.environmentSubstitute(acInterface.getWebGeoCoderURL()));
			settingData.realWebRBDIndicatorURL = new URL(space.environmentSubstitute(acInterface.getRBDIndicatorURL()));
			settingData.realWebPhoneVerifierURL = new URL(space.environmentSubstitute(acInterface.getWebPhoneVerifierURL()));
			settingData.realWebEmailVerifierURL = new URL(space.environmentSubstitute(acInterface.getWebEmailVerifierURL()));
			settingData.realWebIPLocatorURL = new URL(space.environmentSubstitute(acInterface.getIPLocatorURL()));
			settingData.realPersonatorURL = new URL(space.environmentSubstitute(acInterface.getWebPersonatorURL()));
			//FIXME Personator World
			//settingData.realPersonatorWorldURL = new URL(space.environmentSubstitute(acInterface.getWebPersonatorWorldURL()));
			settingData.realPropertyURL = new URL(space.environmentSubstitute(acInterface.getWebPropertyURL()));
			settingData.realWebBusCoderURL = new URL(space.environmentSubstitute(acInterface.getWebBusCoderURL()));
			settingData.realGlobalNameURL = new URL(space.environmentSubstitute(acInterface.getWebGlobalNameURL()));
			settingData.realGlobalAddressURL = new URL(space.environmentSubstitute(acInterface.getWebGlobalAddressURL()));
			settingData.realGlobalPhoneURL = new URL(space.environmentSubstitute(acInterface.getWebGlobalPhoneURL()));
			settingData.realGlobalEmailURL = new URL(space.environmentSubstitute(acInterface.getWebGlobalEmailURL()));

			// get proxy settings
			settingData.realProxyHost = space.environmentSubstitute(acInterface.getWebProxyHost());
			settingData.realProxyPort = acInterface.getWebProxyPort();
			settingData.realProxyUser = acInterface.getWebProxyUser();
			settingData.realProxyPass = acInterface.getWebProxyPass();
			setSMlistId();
			if ((acInterface.nTestType == 3)) {
				// Get the real CVS URLs
				URL url = new URL(space.environmentSubstitute(settingData.serverURL));
				// checkData.realCVSNameParserURL = buildCVSURL(url, "name", "doNameCheck");
				settingData.realCVSAddressVerifierURL = buildCVSURL(url, "addresscheck", "doAddressCheck");
				settingData.realCVSGeoCoderURL = buildCVSURL(url, "geocoder", "doGeoCode");
				settingData.realCVSRBDIndicatorURL = buildCVSURL(url, "rbdi", "doRBDI");
				settingData.realCVSPhoneVerifierURL = buildCVSURL(url, "phonecheck", "doPhoneCheck");
				settingData.realCVSEmailVerifierURL = buildCVSURL(url, "email", "doEmailCheck");
				settingData.realCVSIPLocatorURL = buildCVSURL(url, "iplocator", "doIPLocation");
			}
			// get processing and failover modes based on service settings
			if (acInterface.nTestType == 3) {
				if (settingData.cvsFailover) {
					if (settingData.retryAppliance) {
					}
				}
			} else {
				processUsingWebService = true;
			}
		} catch (Throwable t) {
			System.out.println("ERROR webService init " + t.getMessage());
		}
	}

	private boolean isLicensed(int nProduct) {

		int retVal = settingData.primeLicense.retVal;
		retVal |= settingData.trialLicense.retVal;
		if ((retVal & nProduct) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Called to process address requests
	 *
	 * @throws KettleException
	 */
	private void processAddrRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_Address, null);
	}

	private void processBusCoderRequest() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_BusinessCoder, null);
	}

	private void processPropertyRequest() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_Property, null);
	}

	/**
	 * Called to process email requests
	 *
	 * @throws KettleException
	 */
	private void processEmailRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_Email, null);
	}

	/**
	 * Called to process geocoder requests
	 *
	 * @throws KettleException
	 */
	private void processGeoRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_GeoCode, null);
	}

	private void processGlobalVerifyRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_GlobalVerify, "Addr");
		doWebRequests(MDPropTags.MDLICENSE_GlobalVerify, "name");
		doWebRequests(MDPropTags.MDLICENSE_GlobalVerify, "phone");
		doWebRequests(MDPropTags.MDLICENSE_GlobalVerify, "email");
		if (!isLicensed(MDPropTags.MDLICENSE_GlobalVerify)) {
			settingData.webGlobalAddressMsg = "GE04";
		}
	}

	private void processIPLocatorRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_IPLocator, null);
	}

	/**
	 * Called to process name requests
	 *
	 * @throws KettleException
	 */
	private void processNameRequests() throws KettleException {

		settingData.webNameMsg = "Using Local Component";
		settingData.webNameVersion = "";
		// we only have local for now
		// doWebRequests("P6");
	}

	private void processPersonatorRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_Personator, null);
	}

	private void processPersonatorWorldRequests() throws KettleException {

		System.out.println(" Processing Personator World ");
		doWebRequests(MDPropTags.MDLICENSE_PersonatorWorld, null);
	}

	/**
	 * Called to process phone requests
	 *
	 * @throws KettleException
	 */
	private void processPhoneRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_Phone, null);
	}

	/**
	 * Called to process RBDIndicator requests
	 *
	 * @throws KettleException
	 */
	private void processRBDIndicatorRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_RBDI, null);
	}

	public void processRequests(int testType) throws KettleException {

		int     attempt = 0;
		boolean reDo    = false;
		// Check
		if (!Const.isEmpty(settingData.realProxyHost)) {
			StringBuffer xmlResponse = new StringBuffer(); // this is just a place holder
			String       prMsg       = "";
			int          statCode    = 0;
			if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
				statCode = getWebClient(MDPropTags.MENU_ID_SMARTMOVER).checkProxy(settingData.realNCOAURL, "", xmlResponse);
			} else {
				statCode = getWebClient(MDPropTags.MENU_ID_CONTACTVERIFY).checkProxy(settingData.realWebAddressVerifierURL, "", xmlResponse);
			}
			if (statCode != 200) {
				switch (statCode) {
					case 599:
						prMsg = ": Could not locate proxy";
						break;
					case 407:
						prMsg = ": Could not authenticate with proxy";
						break;
					case 3:
						prMsg = ": Access denied to proxy";
						break;
					case 0:
						prMsg = MDSettingsData.proxyMsg;
						break;
					default:
						prMsg = ": Could not connect to proxy";
						break;
				}
				settingData.webAddrMsg = "address " + prMsg;
				settingData.webRBDIMsg = "RBDI " + prMsg;
				settingData.webPhoneMsg = "phone " + prMsg;
				settingData.webNameMsg = "name " + prMsg;
				settingData.webGeoMsg = "Geo " + prMsg;
				settingData.webEmailMsg = "email " + prMsg;
				settingData.webIPMsg = "IpLocator " + prMsg;
				settingData.webGlobalAddressMsg = "Global Verify " + prMsg;
				settingData.webPersonatorMsg = "Personator " + prMsg;
				settingData.webSMMsg = "Smart Mover " + prMsg;
				settingData.webBusCoderMsg = "Business Coder" + prMsg;
				return;
			}
		}
		do {
			try {
				if (MDPropTags.MENU_ID_CONTACTVERIFY.equals(acInterface.getMenuID())) {
					// Process the name parser requests
					processNameRequests();
					// Process the address verifier requests
					processAddrRequests();
					// Process the geo coder requests
					processGeoRequests();
					// Process RBDI
					processRBDIndicatorRequests();
					// Process the phone verifier requests
					processPhoneRequests();
					// Process the email verifier requests
					processEmailRequests();
				}
				if (MDPropTags.MENU_ID_IPLOCATOR.equals(acInterface.getMenuID())) {
					processIPLocatorRequests();
				}
				if (MDPropTags.MENU_ID_GLOBALVERIFY.equals(acInterface.getMenuID())) {
					processGlobalVerifyRequests();
				}
				if (MDPropTags.MENU_ID_PERSONATOR.equals(acInterface.getMenuID())) {
					processPersonatorRequests();
				}
				if (MDPropTags.MENU_ID_PERSONATOR_WORLD.equals(acInterface.getMenuID())) {
					processPersonatorWorldRequests();
				}
				if (MDPropTags.MENU_ID_SMARTMOVER.equals(acInterface.getMenuID())) {
					processSmartMoverRequests();
				}
				if (MDPropTags.MENU_ID_BUSINESS_CODER.equals(acInterface.getMenuID())) {
					processBusCoderRequest();
				}
				if (MDPropTags.MENU_ID_PROPERTY.equals(acInterface.getMenuID())) {
					processPropertyRequest();
				}
			} catch (KettleException e) {
				if (attempt > 0) {
					attempt--;
					reDo = true;
				} else {
					reDo = false;
					throw e;
				}
			}
		} while (reDo);
	}

	private void processSmartMoverRequests() throws KettleException {

		doWebRequests(MDPropTags.MDLICENSE_SmartMover, null);
	}

	public String processWebResponse(org.dom4j.Document doc, int nProduct, String gvName) throws KettleException {

		// Get the response array
		boolean name  = false;
		boolean addr  = false;
		boolean phone = false;
		boolean email = false;
		if (gvName != null) {
			name = gvName.equals("name");
			addr = gvName.equals("Addr");
			phone = gvName.equals("phone");
			email = gvName.equals("email");
		}
		org.dom4j.Element response = doc.getRootElement();
		if (nProduct == MDPropTags.MDLICENSE_GlobalVerify) {
			if (!response.getName().equals("Response")) {
				throw new KettleException("MDGlobalVerify: Response not found in XML response string");
			}
		} else if (nProduct == MDPropTags.MDLICENSE_Personator) {
			if (!response.getName().equals("Response")) {
				throw new KettleException("MDPersonator: Response not found in XML response string");
			}
		} else if (nProduct == MDPropTags.MDLICENSE_PersonatorWorld) {
			if (!response.getName().equals("Response")) {
				throw new KettleException("MDPersonator World: Response not found in XML response string");
			}
		} else if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
			if (!response.getName().equals("Response")) {
				throw new KettleException("MDSmartMover: Response not found in XML response string");
			}
		} else if (nProduct == MDPropTags.MDLICENSE_Property) {
			if (!response.getName().equals("LookupPropertyResponse")) {
				throw new KettleException("MDProperty Service: LookupPropertyResponse not found in XML response string");
			}
		} else {
			if (!response.getName().equals("ResponseArray")) {
				throw new KettleException("MDCheck: ResponseArray not found in XML response string");
			}
		}
		// Check the general result
		String resultCodes = getElementText(response, "Results");

		if ((nProduct == MDPropTags.MDLICENSE_GlobalVerify) || (nProduct == MDPropTags.MDLICENSE_Personator)) {
			resultCodes += getElementText(response, "TransmissionResults");
		}
		if (nProduct == MDPropTags.MDLICENSE_Address) {
			settingData.webAddrVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_RBDI) {
			settingData.webRBDIVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_Phone) {
			settingData.webPhoneVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_Name) {
			settingData.webNameVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_Email) {
			settingData.webEmailVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_GeoCode) {
			settingData.webGeoVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_IPLocator) {
			settingData.webIPVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_GlobalVerify) {
			if (name) {
				settingData.webGlobalNameVersion = getElementText(response, "Version");
			}
			if (addr) {
				settingData.webGlobalAddressVersion = getElementText(response, "Version");
			}
			if (phone) {
				settingData.webGlobalPhoneVersion = getElementText(response, "Version");
			}
			if (email) {
				settingData.webGlobalEmailVersion = getElementText(response, "Version");
			}
		}
		if (nProduct == MDPropTags.MDLICENSE_SmartMover) {
			settingData.webSMVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_Personator) {
			settingData.webPersonatorVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_PersonatorWorld) {
			settingData.webPersonatorWorldVersion = getElementText(response, "Version");
		}
		if (nProduct == MDPropTags.MDLICENSE_Property) {
			resultCodes = response.elementText("TransmissionResults");
			settingData.webPropertyVersion = response.elementText("Version");
		}

		if (!Const.isEmpty(resultCodes)) {
			return resultCodes;
		}
		return "";
	}

	/**
	 * Transform XML to as DOM object in a thread safe manner
	 *
	 * @param xmlResponse
	 * @return
	 * @throws DocumentException
	 */
	private synchronized org.dom4j.Document readXML(StringBuffer xmlResponse) throws DocumentException {

		return saxReader.read(new StringReader(xmlResponse.toString()));
	}

	public void saveReports() throws KettleException {

		settingData.webPAFMsg = "";
		WebClient webClient = null;
		try {
			// Initialize the web client
			webClient = new WebClient();
			webClient.setTimeout(webClient.getTimeout());
			webClient.setRetries(webClient.getRetries());
			webClient.setProxy(webClient.getProxyHost(), webClient.getProxyPort(), webClient.getProxyUser(), webClient.getProxyPass());
			// Get links to the report files
			org.dom4j.Document report = getSummeryLink(String.valueOf(getCustomerID(MDPropTags.MDLICENSE_SmartMover)), "", String.valueOf(jobID));
			String             result = "SE";
			if (report != null) {
				result = getElementText(report.getRootElement(), "TransmissionResults");
			}
			// System.out.println("SmartMOver save Reports return = " + result);
			// FIXME PAF here is where to catch paf fail
			// http://www.melissadata.com/webhelp/contactzone/mergedProjects/SmartMover/SmartMover.htm#SmartMover_Tabs/Input_Columns_Tab.htm
			if (result.startsWith("SE")) {
				settingData.webPAFMsg = result;
				System.out.println("WARRNING: Unable to get summery reports link for Jod ID " + jobID);
			}
		} finally {
			if (webClient != null) {
				webClient.close();
			}
		}
	}

	private void setSMlistId() {

		// A common job id and execution id used for interaction with the NCOA service
		UUID guid = UUID.randomUUID();
		jobID = guid.toString();
		jobID = jobID.replace("-", "");
		if (jobID.length() > 15) {
			jobID = jobID.substring(0, 15);
		}
		int exID = 0;
		// if (smartMover.isOptJobOverride() || smartMover.getOptionSummaryReports()) {
		String guidString = guid.toString().replace("-", "");
		for (int i = 0; i < 32; i += 4) {
			int guidNibble = Integer.parseInt(guidString.substring(i, i + 4), 16);
			exID ^= guidNibble;
		}
		// }
		executionID = String.valueOf(exID);
		listName = "settings_test";
	}
}
