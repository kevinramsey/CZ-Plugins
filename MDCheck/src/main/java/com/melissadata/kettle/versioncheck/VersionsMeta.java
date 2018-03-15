package com.melissadata.kettle.versioncheck;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckStepData;
import org.pentaho.di.core.Const;
// import org.pentaho.pms.util.Const;
import org.pentaho.di.core.exception.KettleException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class VersionsMeta {
	public static boolean isXmlSent() {
		return xmlSent;
	}
	private static boolean                   xmlSent;
	private        AdvancedConfigurationMeta acMeta;
	private        DOMImplementation         domImplentation;
	private        Transformer               transform;

	public VersionsMeta(MDCheckStepData data) {
		acMeta = data.getAdvancedConfiguration();
	}

	// xml to request update information and form expirations
	public void runThread() {
		xmlSent = true;
		VersionCheckThread vct = new VersionCheckThread(this);
		vct.setPriority(1);
		vct.start();
	}

	public void sendUsageNotify() {
		try {
			URL url = new URL(acMeta.getSendLicenceURL());
			URLConnection con = url.openConnection();
			// specify that we will send output
			con.setDoOutput(true);
			con.setConnectTimeout(20000);  // long timeout, but not infinite
			con.setUseCaches(false);
			con.setDefaultUseCaches(false);
			// tell the web server what we are sending
			con.setRequestProperty("Content-Type", "text/xml");
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(buildNotifyXML());
			writer.flush();
			writer.close();
		} catch (Throwable t) {
			// Do nothing just die silently
		}
	}

	private String buildNotifyXML() throws KettleException, TransformerException, ParserConfigurationException {
		String xmlRequest;
		transform = TransformerFactory.newInstance().newTransformer();
		domImplentation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
		Document requestUpdateDoc = domImplentation.createDocument(null, "USAGENOTIFY", null);
		Element root = requestUpdateDoc.getDocumentElement();
		Element elLicence = requestUpdateDoc.createElement("LICENCE");
		root.appendChild(elLicence);
		String lic = !Const.isEmpty(acMeta.getLicense(AdvancedConfigurationMeta.TAG_PRIMARY_LICENSE)) ? acMeta.getLicense(AdvancedConfigurationMeta.TAG_PRIMARY_LICENSE) : acMeta.getLicense(AdvancedConfigurationMeta.TAG_TRIAL_LICENSE);
		Text sLicence = requestUpdateDoc.createTextNode(lic);
		elLicence.appendChild(sLicence);
		/*
		 * Element elVersion = requestUpdateDoc.createElement("VERSION");
		 * root.appendChild(elVersion);
		 * Text sVersion = requestUpdateDoc.createTextNode("2.1.0");
		 * elVersion.appendChild(sVersion);
		 * Element elBuild = requestUpdateDoc.createElement("BUILD");
		 * root.appendChild(elBuild);
		 * Text sBuild = requestUpdateDoc.createTextNode("1580");
		 * elBuild.appendChild(sBuild);
		 */
		StringWriter sw = new StringWriter();
		DOMSource domSource = new DOMSource(requestUpdateDoc);
		transform.transform(domSource, new StreamResult(sw));
		xmlRequest = sw.toString();
		return xmlRequest;
	}
}
