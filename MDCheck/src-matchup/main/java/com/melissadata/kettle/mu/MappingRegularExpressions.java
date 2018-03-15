package com.melissadata.kettle.mu;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.melissadata.kettle.MDCheck;
import com.melissadata.kettle.MDCheckStepData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.melissadata.mdMUMatchcode;
import com.melissadata.cz.CZUtil;

public class MappingRegularExpressions {
	private static final String					MAP_REG_EXP_FILE					= "mapping_regular_expressions.xml";
	private static final String					TAG_MAP_REG_EXP						= "MapRegExp";
	private static final String					TAG_MAP								= "Map";
	private static final String					TAG_TYPE							= "Type";
	private static final String					TAG_EXPRESSION						= "Expression";
	private static Object[][]					DEFAULT_MAPPING_REGULAR_EXPRESSIONS	= new Object[][] { { mdMUMatchcode.MatchcodeMapping.Prefix, "Prefix,Pre.*" }, { mdMUMatchcode.MatchcodeMapping.Gender, "Gender,Gend.*" },
			{ mdMUMatchcode.MatchcodeMapping.First, "FirstName,FN,First,FName,F_N.*" }, { mdMUMatchcode.MatchcodeMapping.MixedFirst, "MixedFirst,MF,MFName,MF_N.*" }, { mdMUMatchcode.MatchcodeMapping.Middle, "MiddleName,MN,Middle,MName,M_N.*" },
			{ mdMUMatchcode.MatchcodeMapping.Last, "LastName,LN,Last,LName,L_N.*" }, { mdMUMatchcode.MatchcodeMapping.MixedLast, "MixedLast,ML,MLName,ML_N.*" }, { mdMUMatchcode.MatchcodeMapping.Suffix, "Suffix,Suf.*" },
			{ mdMUMatchcode.MatchcodeMapping.FullName, "FullName,NameFull,Full,Name,Contact,FullN.*,Full_N.*" },
			{ mdMUMatchcode.MatchcodeMapping.InverseName, "InverseName,InvName,Inverse,NameInverse,NameInv,InvN.*,InverseN.*,Inv_N.*,Inverse_N.*", }, { mdMUMatchcode.MatchcodeMapping.GovernmentInverseName, "GovtName,NameGovt" },
			{ mdMUMatchcode.MatchcodeMapping.Title, "Title,Department,Tit.*l,Dep*.t" }, { mdMUMatchcode.MatchcodeMapping.Company, "Company,Comp,Co,Co.*" }, { mdMUMatchcode.MatchcodeMapping.Address, "Address,Street,Add.*,Str.*" },
			{ mdMUMatchcode.MatchcodeMapping.City, "City,Town,Municipality,Muni.*" }, { mdMUMatchcode.MatchcodeMapping.State, "State,Province,St,Prov.*" }, { mdMUMatchcode.MatchcodeMapping.Zip9, "Zip,ZipCode,PostalCode,PostCode,PC,Zip.*" },
			{ mdMUMatchcode.MatchcodeMapping.Zip5, "Zip5" }, { mdMUMatchcode.MatchcodeMapping.Zip4, "Zip4,Plus4,ZipExt" }, { mdMUMatchcode.MatchcodeMapping.CityStZip, "CityStZip,CSZ,LastLine,Cit.*Z.*,CSt.*Z.*" },
			{ mdMUMatchcode.MatchcodeMapping.Country, "Country,Cntry,Ctry" }, { mdMUMatchcode.MatchcodeMapping.CanadianPostalCode, "PostalCode,PC,Can.*PC,Post.*Code" },/* { mdMUMatchcode.MatchcodeMapping.UKCity, "City,Town,Municipality,Muni.*" },
			{ mdMUMatchcode.MatchcodeMapping.UKCounty, "County,Cty,Cnty" }, { mdMUMatchcode.MatchcodeMapping.UKPostcode, "PostCode,PC" }, { mdMUMatchcode.MatchcodeMapping.UKCityCountyPC, "LastLine" },*/
			{ mdMUMatchcode.MatchcodeMapping.Phone, "Phone,Fax,.*Phone" }, { mdMUMatchcode.MatchcodeMapping.EMail, "Email" }, { mdMUMatchcode.MatchcodeMapping.CreditCard, "" }, { mdMUMatchcode.MatchcodeMapping.General, "" },
			{ mdMUMatchcode.MatchcodeMapping.Latitude, "Latitude" }, { mdMUMatchcode.MatchcodeMapping.Longitude, "Longitude" }, { mdMUMatchcode.MatchcodeMapping.Date, "" }, { mdMUMatchcode.MatchcodeMapping.Numeric, "" }, };
	private static final Map<String, String[]>	defaultExpMap;
	static {
		defaultExpMap = new HashMap<String, String[]>(DEFAULT_MAPPING_REGULAR_EXPRESSIONS.length);
		for (Object[] mapping : DEFAULT_MAPPING_REGULAR_EXPRESSIONS) {
			mdMUMatchcode.MatchcodeMapping eMapping = (mdMUMatchcode.MatchcodeMapping) mapping[0];
			String[] expressions = ((String) mapping[1]).split(",");
			defaultExpMap.put(eMapping.toString(), expressions);
		}
	}
	private Map<String, String[]>				expMap;

	public MappingRegularExpressions() {
		// Fill expression map with defaults
		expMap = new HashMap<String, String[]>(defaultExpMap);
		try {
			// Find and load the regular expression file (if any)
			File regexpFile;
			try {
				regexpFile = new File(CZUtil.getCZWorkDirectory(), MAP_REG_EXP_FILE);
			} catch (IOException e) {
				throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoRegExFile"), e);
			}
			if (regexpFile.exists()) {
				InputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(new FileInputStream(regexpFile));
					StringBuffer xml = new StringBuffer();
					int c;
					while ((c = inputStream.read()) != -1) {
						xml.append((char) c);
					}
					inputStream.close();
					Document document = XMLHandler.loadXMLString(xml.toString());
					Node ruleNode = XMLHandler.getSubNode(document, TAG_MAP_REG_EXP);
					List<Node> ruleNodes = XMLHandler.getNodes(ruleNode, TAG_MAP);
					for (Node rule : ruleNodes) {
						String type = MDCheckStepData.getTagValue(rule, TAG_TYPE);
						String[] expressions = MDCheckStepData.getTagValue(rule, TAG_EXPRESSION).split(",");
						expMap.put(type, expressions);
					}
				} catch (IOException e) {
					throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheck.Error.NoMapRegExFile"), e);
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException ignored) {
						}
					}
				}
			}
		} catch (KettleException e) {
			// Ignore for now
		}
	}

	public String[] get(mdMUMatchcode.MatchcodeMapping eMapping) {
		String expressions[] = expMap.get(eMapping.toString());
		return expressions;
	}
}
