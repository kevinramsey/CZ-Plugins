package com.melissadata.kettle.globalverify.support;

import com.melissadata.kettle.globalverify.MDGlobalMeta;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.i18n.BaseMessages;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Kevin on 9/13/2017.
 */

public class CountryUtil {

	private static Class<?> PKG          = MDGlobalMeta.class;;
	private static        String[] arFullNames  = new String[246];
	private static        String[] arAltNames   = new String[246];
	private static        String[] arISO2       = new String[246];
	private static        String[] arISO3       = new String[246];
	private static        String[] arISONumeric = new String[246];
	private static        int      countryIndex = -1;

	public CountryUtil() {

		setCountries();
	}

	/*
		Takes ISO_2, ISO_3, or ISONumeric
		Returns the Country Full Name
	 */
	public static String getCountryName(String iso){

		int index = getCountryIndex(iso);
		if(index < 0){
			return "";
		}

		return arFullNames[index];
	}

	public static String getCountryNameFromIndex(int index){
		if(index < 0){
			return "";
		}
		return arFullNames[index];
	}

	public static String getCountryISO2(String fullName) {

		int index = getCountryIndex(fullName);
		if(index < 0){
			return "";
		}
		return arISO2[index];
	}

	public static String getCountryISO2FromIndex(int index) {
		if(index < 0){
			return "";
		}
		return arISO2[index];
	}

	public static String getCountryISO3(String fullName) {

		int index = getCountryIndex(fullName);
		if(index < 0){
			return "";
		}
		return arISO3[index];
	}

	public static String getCountryISO3FromIndex(int index) {
		if(index < 0){
			return "";
		}
		return arISO3[index];
	}

	public static String getCountryISONumeric(String fullName) {

		int index = getCountryIndex(fullName);
		if(index < 0){
			return "";
		}
		return arISONumeric[index];
	}

	public static String getCountryISONumericFromIndex(int index) {
		if(index < 0){
			return "";
		}
		return arISONumeric[index];
	}

	public static int getCountryIndex(String country){
		int index = -1;
		if(!country.isEmpty() && country.length() == 2){
			index = getIndexFromISO2(country);
		} else if(country.length() == 3){
			if(StringUtils.isNumeric(country)){
				index = getIndexFromISONumeric(country);
			} else {
				index = getIndexFromISO3(country);
			}
		} else {
			index = getIndexFromName(country);
		}

		//System.out.println(" * * * Country Index for : " + country + " = " + index);
		return index;
	}

	private static int getIndexFromISO3(String iso3) {
		// short cut UAE is not an ISO code but is a common abbrivation,
		// so we will catch it here
		if(iso3.equalsIgnoreCase("UAE")){
			return 229;
		}

		for (int idx = 0; idx < arISO3.length; idx++) {
			if (arISO3[idx].equals(iso3)) {
				return idx;
			}
		}
		return -1;
	}

	private static int getIndexFromISO2(String iso2) {
		// short cut UK is not an ISO code but is a common abbrivation,
		// so we will catch it here
		if(iso2.equalsIgnoreCase("UK")){
			return 230;
		}

		for (int idx = 0; idx < arISO2.length; idx++) {
			if (arISO2[idx].equals(iso2)) {
				return idx;
			}
		}
		return -1;
	}

	private static int getIndexFromISONumeric(String isoN) {

		for (int idx = 0; idx < arISONumeric.length; idx++) {
			if (arISONumeric[idx].equals(isoN)) {
				return idx;
			}
		}
		return -1;
	}

	private static int getIndexFromName(String countryName) {

		if(countryName.isEmpty()){
			return -1;
		}
		for (int idx = 0; idx < arFullNames.length; idx++) {
			if(arFullNames[idx] != null) {
				if (countryNameMatch(arFullNames[idx], arAltNames[idx] , countryName)) {
					return idx;
				}
			}
		}
		return -1;
	}

	private static boolean countryNameMatch(String cFullName, String cAltName, String matchCountry){
		String cFname = cFullName.toLowerCase().replace(" ","").replace("republic","").replace("of","").replace("and","").replace("the","").replace(",","").replace("-","").replace("(","").replace(")","").trim();
		String cAlt   = cAltName.toLowerCase().replace(" ","").replace("republic","").replace("of","").replace("and","").replace("the","").replace(",","").replace("-","").replace("(","").replace(")","").trim();
		String cMatch = matchCountry.toLowerCase().replace(" ","").replace("republic","").replace("of","").replace("and","").replace("the","").replace(",","").replace("-","").replace("(","").replace(")","").trim();
		//	System.out.println(" - MATCH:" + cMatch + "     FULL:" + cFname + " ALT:" + cAlt);
		if(cMatch.equals(cFname)){
		//System.out.println(" - - - - - - - - - - -- - MATCHED on Full Name: " + cFullName  + " = " + cMatch );
			return true;
		} else if (cAlt.contains(cMatch)){
		//	System.out.println(" - - - - - - - - - - -- - MATCHED on Alt Name: " + cAltName + " = " + cMatch);
			return true;
		}

		return false;
	}

	public static SortedSet<String> getSortedSetCountryNames(){
		SortedSet<String> cuSet = new TreeSet<String>();
		for(String name : arFullNames){
			if(name != null && !name.isEmpty()){
				cuSet.add(name);
			}
		}

		return cuSet;
	}

	//@formatter:off
	private void setCountries(){
//http://www.nationsonline.org/oneworld/country_code_list.htm

		//Afghanistan                                                                                                                                   // alternate names can have more than one separate by ":"
		arISO3[0]   = "AFG";    arISO2[0]    = "AF";     arISONumeric[0]   = "004";    arFullNames[0]    = "Afghanistan";                                   arAltNames[0] = "";
		arISO3[1]   = "ALA";    arISO2[1]    = "AX";     arISONumeric[1]   = "248";    arFullNames[1]    = "Aland Islands";                                 arAltNames[1] = "Aland";
		arISO3[2]   = "ALB";    arISO2[2]    = "AL";     arISONumeric[2]   = "008";    arFullNames[2]    = "Albania";                                       arAltNames[2] = "";
		arISO3[3]   = "DZA";    arISO2[3]    = "DZ";     arISONumeric[3]   = "012";    arFullNames[3]    = "Algeria";                                       arAltNames[3] = "";
		arISO3[4]   = "ASM";    arISO2[4]    = "AS";     arISONumeric[4]   = "016";    arFullNames[4]    = "American Samoa";                                arAltNames[4] = "";
		arISO3[5]   = "AND";    arISO2[5]    = "AD";     arISONumeric[5]   = "020";    arFullNames[5]    = "Andorra";                                       arAltNames[5] = "";

		arISO3[6]   = "AGO";    arISO2[6]    = "AO";     arISONumeric[6]   = "024";    arFullNames[6]    = "Angola";                                        arAltNames[6] = "";
		arISO3[7]   = "AIA";    arISO2[7]    = "AI";     arISONumeric[7]   = "660";    arFullNames[7]    = "Anguilla";                                      arAltNames[7] = "";
		arISO3[8]   = "ATA";    arISO2[8]    = "AQ";     arISONumeric[8]   = "010";    arFullNames[8]    = "Antarctica";                                    arAltNames[8] = "";
		arISO3[9]   = "ATG";    arISO2[9]    = "AG";     arISONumeric[9]   = "028";    arFullNames[9]    = "Antigua and Barbuda";                           arAltNames[9] = "Antigua:Barbuda";
		arISO3[10]  = "ARG";    arISO2[10]   = "AR";     arISONumeric[10]  = "032";    arFullNames[10]   = "Argentina";                                     arAltNames[10] = "";

		arISO3[11]  = "ARM";    arISO2[11]   = "AM";     arISONumeric[11]  = "051";    arFullNames[11]   = "Armenia";                                       arAltNames[11] = "";
		arISO3[12]  = "ABW";    arISO2[12]   = "AW";     arISONumeric[12]  = "533";    arFullNames[12]   = "Aruba";                                         arAltNames[12] = "";
		arISO3[13]  = "AUS";    arISO2[13]   = "AU";     arISONumeric[13]  = "036";    arFullNames[13]   = "Australia";                                     arAltNames[13] = "";
		arISO3[14]  = "AUT";    arISO2[14]   = "AT";     arISONumeric[14]  = "040";    arFullNames[14]   = "Austria";                                       arAltNames[14] = "";
		arISO3[15]  = "AZE";    arISO2[15]   = "AZ";     arISONumeric[15]  = "031";    arFullNames[15]   = "Azerbaijan";                                    arAltNames[15] = "";

		arISO3[16]  = "BHS";     arISO2[16]  = "BS";     arISONumeric[16]  = "044";    arFullNames[16]   = "Bahamas";                                       arAltNames[16] = "";
		arISO3[17]  = "BHR";     arISO2[17]  = "BH";     arISONumeric[17]  = "048";    arFullNames[17]   = "Bahrain";                                       arAltNames[17] = "";
		arISO3[18]  = "BGD";     arISO2[18]  = "BD";     arISONumeric[18]  = "050";    arFullNames[18]   = "Bangladesh";                                    arAltNames[18] = "";
		arISO3[19]  = "BRB";     arISO2[19]  = "BB";     arISONumeric[19]  = "052";    arFullNames[19]   = "Barbados";                                      arAltNames[19] = "";
		arISO3[20]  = "BLR";     arISO2[20]  = "BY";     arISONumeric[20]  = "112";    arFullNames[20]   = "Belarus";                                       arAltNames[20] = "";

		arISO3[21]  = "BEL";     arISO2[21]  = "BE";     arISONumeric[21]  = "056";    arFullNames[21]   = "Belgium";                                       arAltNames[21] = "";
		arISO3[22]  = "BLZ";     arISO2[22]  = "BZ";     arISONumeric[22]  = "084";    arFullNames[22]   = "Belize";                                        arAltNames[22] = "";
		arISO3[23]  = "BEN";     arISO2[23]  = "BJ";     arISONumeric[23]  = "204";    arFullNames[23]   = "Benin";                                         arAltNames[23] = "";
		arISO3[24]  = "BMU";     arISO2[24]  = "BM";     arISONumeric[24]  = "060";    arFullNames[24]   = "Bermuda";                                       arAltNames[24] = "";
		arISO3[25]  = "BTN";     arISO2[25]  = "BT";     arISONumeric[25]  = "064";    arFullNames[25]   = "Bhutan";                                        arAltNames[25] = "";

		arISO3[26]  = "BOL";     arISO2[26]  = "BO";     arISONumeric[26]  = "068";    arFullNames[26]   = "Bolivia";                                       arAltNames[26] = "";
		arISO3[27]  = "BIH";     arISO2[27]  = "BA";     arISONumeric[27]  = "070";    arFullNames[27]   = "Bosnia and Herzegovina";                        arAltNames[27] = "Bosnia:Herzegovina";
		arISO3[28]  = "BWA";     arISO2[28]  = "BW";     arISONumeric[28]  = "072";    arFullNames[28]   = "Botswana";                                      arAltNames[28] = "";
		arISO3[29]  = "BRA";     arISO2[29]  = "BR";     arISONumeric[29]  = "076";    arFullNames[29]   = "Brazil";                                        arAltNames[29] = "";
		arISO3[30]  = "VGB";     arISO2[30]  = "VG";     arISONumeric[30]  = "092";    arFullNames[30]   = "British Virgin Islands";                        arAltNames[30] = "Virgin Islands";

		arISO3[31]  = "IOT";     arISO2[31]  = "IO";     arISONumeric[31]  = "086";    arFullNames[31]   = "British Indian Ocean Territory";                arAltNames[31] = "Indian Ocean Territory:BIOT";
		arISO3[32]  = "BRN";     arISO2[32]  = "BN";     arISONumeric[32]  = "096";    arFullNames[32]   = "Brunei Darussalam";                             arAltNames[32] = "";
		arISO3[33]  = "BGR";     arISO2[33]  = "BG";     arISONumeric[33]  = "100";    arFullNames[33]   = "Bulgaria";                                      arAltNames[33] = "";
		arISO3[34]  = "BFA";     arISO2[34]  = "BF";     arISONumeric[34]  = "854";    arFullNames[34]   = "Burkina Faso";                                  arAltNames[34] = "";
		arISO3[35]  = "BDI";     arISO2[35]  = "BI";     arISONumeric[35]  = "108";    arFullNames[35]   = "Burundi";                                       arAltNames[35] = "";

		arISO3[36]  = "KHM";     arISO2[36]  = "KH";     arISONumeric[36]  = "116";    arFullNames[36]   = "Cambodia";                                      arAltNames[36] = "";
		arISO3[37]  = "CMR";     arISO2[37]  = "CM";     arISONumeric[37]  = "120";    arFullNames[37]   = "Cameroon";                                      arAltNames[37] = "";
		arISO3[38]  = "CAN";     arISO2[38]  = "CA";     arISONumeric[38]  = "124";    arFullNames[38]   = "Canada";                                        arAltNames[38] = "";
		arISO3[39]  = "CPV";     arISO2[39]  = "CV";     arISONumeric[39]  = "132";    arFullNames[39]   = "Cape Verde";                                    arAltNames[39] = "";
		arISO3[40]  = "CYM";     arISO2[40]  = "KY";     arISONumeric[40]  = "136";    arFullNames[40]   = "Cayman Islands";                                arAltNames[40] = "";

		arISO3[41]  = "CAF";     arISO2[41]  = "CF";     arISONumeric[41]  = "140";    arFullNames[41]   = "Central African Republic";                      arAltNames[41] = "Central Africa";
		arISO3[42]  = "TCD";     arISO2[42]  = "TD";     arISONumeric[42]  = "148";    arFullNames[42]   = "Chad";                                          arAltNames[42] = "";
		arISO3[43]  = "CHL";     arISO2[43]  = "CL";     arISONumeric[43]  = "152";    arFullNames[43]   = "Chile";                                         arAltNames[43] = "";
		arISO3[44]  = "CHN";     arISO2[44]  = "CN";     arISONumeric[44]  = "156";    arFullNames[44]   = "China";                                         arAltNames[44] = "";
		arISO3[45]  = "CXR";     arISO2[45]  = "CX";     arISONumeric[45]  = "162";    arFullNames[45]   = "Christmas Island";                              arAltNames[45] = "";

		arISO3[46]  = "CCK";     arISO2[46]  = "CC";     arISONumeric[46]  = "166";    arFullNames[46]   = "Cocos (Keeling) Islands";                       arAltNames[46] = "Cocos Islands:Keeling Islands";
		arISO3[47]  = "COL";     arISO2[47]  = "CO";     arISONumeric[47]  = "170";    arFullNames[47]   = "Colombia";                                      arAltNames[47] = "";
		arISO3[48]  = "COM";     arISO2[48]  = "KM";     arISONumeric[48]  = "174";    arFullNames[48]   = "Comoros";                                       arAltNames[48] = "";
		arISO3[49]  = "COG";     arISO2[49]  = "CG";     arISONumeric[49]  = "178";    arFullNames[49]   = "Congo (Brazzaville)";                           arAltNames[49] = "Brazzaville";
		arISO3[50]  = "COK";     arISO2[50]  = "CK";     arISONumeric[50]  = "184";    arFullNames[50]   = "Cook Islands";                                  arAltNames[50] = "";
		
		arISO3[51]  = "CRI";     arISO2[51]  = "CR";     arISONumeric[51]  = "188";    arFullNames[51]   = "Costa Rica";                                    arAltNames[51] = "";
		arISO3[52]  = "HRV";     arISO2[52]  = "HR";     arISONumeric[52]  = "191";    arFullNames[52]   = "Croatia";                                       arAltNames[52] = "";
		arISO3[53]  = "CUB";     arISO2[53]  = "CU";     arISONumeric[53]  = "192";    arFullNames[53]   = "Cuba";                                          arAltNames[53] = "";
		arISO3[54]  = "CYP";     arISO2[54]  = "CY";     arISONumeric[54]  = "196";    arFullNames[54]   = "Cyprus";                                        arAltNames[54] = "";
		arISO3[55]  = "CZE";     arISO2[55]  = "CZ";     arISONumeric[55]  = "203";    arFullNames[55]   = "Czech Republic";                                arAltNames[55] = "Czechia";
		
		arISO3[56]  = "COD";     arISO2[56]  = "CD";     arISONumeric[56]  = "180";    arFullNames[56]   = "Congo (Kinshasa)";                              arAltNames[56] = "Kinshasa";
		arISO3[57]  = "DNK";     arISO2[57]  = "DK";     arISONumeric[57]  = "208";    arFullNames[57]   = "Denmark";                                       arAltNames[57] = "";
		arISO3[58]  = "DJI";     arISO2[58]  = "DJ";     arISONumeric[58]  = "262";    arFullNames[58]   = "Djibouti";                                      arAltNames[58] = "";
		arISO3[59]  = "DMA";     arISO2[59]  = "DM";     arISONumeric[59]  = "212";    arFullNames[59]   = "Dominica";                                      arAltNames[59] = "";
		arISO3[60]  = "DOM";     arISO2[60]  = "DO";     arISONumeric[60]  = "214";    arFullNames[60]   = "Dominican Republic";                            arAltNames[60] = "";

		arISO3[61]  = "ECU";     arISO2[61]  = "EC";     arISONumeric[61]  = "218";    arFullNames[61]   = "Ecuador";                                       arAltNames[61] = "";
		arISO3[62]  = "EGY";     arISO2[62]  = "EG";     arISONumeric[62]  = "818";    arFullNames[62]   = "Egypt";                                         arAltNames[62] = "";
		arISO3[63]  = "SLV";     arISO2[63]  = "SV";     arISONumeric[63]  = "222";    arFullNames[63]   = "El Salvador";                                   arAltNames[63] = "";
		arISO3[64]  = "GNQ";     arISO2[64]  = "GQ";     arISONumeric[64]  = "226";    arFullNames[64]   = "Equatorial Guinea";                             arAltNames[64] = "Equatorial Guinea";
		arISO3[65]  = "ERI";     arISO2[65]  = "ER";     arISONumeric[65]  = "232";    arFullNames[65]   = "Eritrea";                                       arAltNames[65] = "";
		
		arISO3[66]  = "EST";     arISO2[66]  = "EE";     arISONumeric[66]  = "233";    arFullNames[66]   = "Estonia";                                       arAltNames[66] = "";
		arISO3[67]  = "ETH";     arISO2[67]  = "ET";     arISONumeric[67]  = "231";    arFullNames[67]   = "Ethiopia";                                      arAltNames[67] = "";
		arISO3[68]  = "FLK";     arISO2[68]  = "FK";     arISONumeric[68]  = "238";    arFullNames[68]   = "Falkland Islands (Malvinas)";                   arAltNames[68] = "Falkland Islands:Malvinas";
		arISO3[69]  = "FRO";     arISO2[69]  = "FO";     arISONumeric[69]  = "234";    arFullNames[69]   = "Faroe Islands";                                 arAltNames[69] = "";
		arISO3[70]  = "FJI";     arISO2[70]  = "FJ";     arISONumeric[70]  = "242";    arFullNames[70]   = "Fiji";                                          arAltNames[70] = "";
		
		arISO3[71]  = "FIN";     arISO2[71]  = "FI";     arISONumeric[71]  = "246";    arFullNames[71]   = "Finland";                                       arAltNames[71] = "";
		arISO3[72]  = "FRA";     arISO2[72]  = "FR";     arISONumeric[72]  = "250";    arFullNames[72]   = "France";                                        arAltNames[72] = "";
		arISO3[73]  = "GUF";     arISO2[73]  = "GF";     arISONumeric[73]  = "254";    arFullNames[73]   = "French Guiana";                                 arAltNames[73] = "Guiana";
		arISO3[74]  = "PYF";     arISO2[74]  = "PF";     arISONumeric[74]  = "258";    arFullNames[74]   = "French Polynesia";                              arAltNames[74] = "Polynesia";
		arISO3[75]  = "ATF";     arISO2[75]  = "TF";     arISONumeric[75]  = "260";    arFullNames[75]   = "French Southern Territories";                   arAltNames[75] = "";

		arISO3[76]  = "GAB";     arISO2[76]  = "GA";     arISONumeric[76]  = "266";    arFullNames[76]   = "Gabon";                                         arAltNames[76] = "";
		arISO3[77]  = "GMB";     arISO2[77]  = "GM";     arISONumeric[77]  = "270";    arFullNames[77]   = "Gambia";                                        arAltNames[77] = "";
		arISO3[78]  = "GEO";     arISO2[78]  = "GE";     arISONumeric[78]  = "268";    arFullNames[78]   = "Georgia";                                       arAltNames[78] = "";
		arISO3[79]  = "DEU";     arISO2[79]  = "DE";     arISONumeric[79]  = "276";    arFullNames[79]   = "Germany";                                       arAltNames[79] = "";
		arISO3[80]  = "GHA";     arISO2[80]  = "GH";     arISONumeric[80]  = "288";    arFullNames[80]   = "Ghana";                                         arAltNames[80] = "";

		arISO3[81]  = "GIB";     arISO2[81]  = "GI";     arISONumeric[81]  = "292";    arFullNames[81]   = "Gibraltar";                                     arAltNames[81] = "";
		arISO3[82]  = "GRC";     arISO2[82]  = "GR";     arISONumeric[82]  = "300";    arFullNames[82]   = "Greece";                                        arAltNames[82] = "";
		arISO3[83]  = "GRL";     arISO2[83]  = "GL";     arISONumeric[83]  = "304";    arFullNames[83]   = "Greenland";                                     arAltNames[83] = "";
		arISO3[84]  = "GRD";     arISO2[84]  = "GD";     arISONumeric[84]  = "308";    arFullNames[84]   = "Grenada";                                       arAltNames[84] = "";
		arISO3[85]  = "GLP";     arISO2[85]  = "GP";     arISONumeric[85]  = "312";    arFullNames[85]   = "Guadeloupe";                                    arAltNames[85] = "";

		arISO3[86]  = "GUM";     arISO2[86]  = "GU";     arISONumeric[86]  = "316";    arFullNames[86]  = "Guam";                                           arAltNames[86] = "";
		arISO3[87]  = "GTM";     arISO2[87]  = "GT";     arISONumeric[87]  = "320";    arFullNames[87]  = "Guatemala";                                      arAltNames[87] = "";
		arISO3[88]  = "GGY";     arISO2[88]  = "GG";     arISONumeric[88]  = "831";    arFullNames[88]  = "Guernsey";                                       arAltNames[88] = "";
		arISO3[89]  = "GIN";     arISO2[89]  = "GN";     arISONumeric[89]  = "324";    arFullNames[89]  = "Guinea";                                         arAltNames[89] = "";
		arISO3[90]  = "GNB";     arISO2[90]  = "GW";     arISONumeric[90]  = "624";    arFullNames[90]  = "Guinea-Bissau";                                  arAltNames[90] = "";

		arISO3[91]  = "GUY";     arISO2[91]  = "GY";     arISONumeric[91]  = "328";    arFullNames[91]  = "Guyana";                                         arAltNames[91] = "";
		arISO3[92]  = "HTI";     arISO2[92]  = "HT";     arISONumeric[92]  = "332";    arFullNames[92]  = "Haiti";                                          arAltNames[92] = "";
		arISO3[93]  = "VAT";     arISO2[93]  = "VA";     arISONumeric[93]  = "336";    arFullNames[93]  = "Holy See (Vatican City State)";                  arAltNames[93] = "Holy See:Vatican City State";
		arISO3[94]  = "HND";     arISO2[94]  = "HN";     arISONumeric[94]  = "340";    arFullNames[94]  = "Honduras";                                       arAltNames[94] = "";
		arISO3[95]  = "HKG";     arISO2[95]  = "HK";     arISONumeric[95]  = "344";    arFullNames[95]  = "Hong Kong";                                      arAltNames[95] = "";

		arISO3[96]  = "HUN";     arISO2[96]  = "HU";     arISONumeric[96]  = "348";    arFullNames[96]  = "Hungary";                                        arAltNames[96] = "";
		arISO3[97]  = "ISL";     arISO2[97]  = "IS";     arISONumeric[97]  = "352";    arFullNames[97]  = "Iceland";                                        arAltNames[97] = "";
		arISO3[98]  = "IND";     arISO2[98]  = "IN";     arISONumeric[98]  = "356";    arFullNames[98]  = "India";                                          arAltNames[98] = "";
		arISO3[99]  = "IDN";     arISO2[99]  = "ID";     arISONumeric[99]  = "360";    arFullNames[99]  = "Indonesia";                                      arAltNames[99] = "";
		arISO3[100] = "IRN";     arISO2[100] = "IR";     arISONumeric[100] = "364";    arFullNames[100] = "Iran";                                           arAltNames[100] = "";

		arISO3[101] = "IRQ";     arISO2[101] = "IQ";     arISONumeric[101] = "368";    arFullNames[101] = "Iraq";                                           arAltNames[101] = "";
		arISO3[102] = "IRL";     arISO2[102] = "IE";     arISONumeric[102] = "372";    arFullNames[102] = "Ireland";                                        arAltNames[102] = "";
		arISO3[103] = "IMN";     arISO2[103] = "IM";     arISONumeric[103] = "833";    arFullNames[103] = "Isle of Man";                                    arAltNames[103] = "Mann";
		arISO3[104] = "ISR";     arISO2[104] = "IL";     arISONumeric[104] = "376";    arFullNames[104] = "Israel";                                         arAltNames[104] = "";
		arISO3[105] = "ITA";     arISO2[105] = "IT";     arISONumeric[105] = "380";    arFullNames[105] = "Italy";                                          arAltNames[105] = "";

		arISO3[106] = "CIV";     arISO2[106] = "CI";     arISONumeric[106] = "384";    arFullNames[106] = "Ivory Coast";                                    arAltNames[106] = "Côte d'Ivoire";
		arISO3[107] = "JAM";     arISO2[107] = "JM";     arISONumeric[107] = "388";    arFullNames[107] = "Jamaica";                                        arAltNames[107] = "";
		arISO3[108] = "JPN";     arISO2[108] = "JP";     arISONumeric[108] = "392";    arFullNames[108] = "Japan";                                          arAltNames[108] = "";
		arISO3[109] = "JEY";     arISO2[109] = "JE";     arISONumeric[109] = "832";    arFullNames[109] = "Jersey";                                         arAltNames[109] = "";
		arISO3[110] = "JOR";     arISO2[110] = "JO";     arISONumeric[110] = "400";    arFullNames[110] = "Jordan";                                         arAltNames[110] = "";

		arISO3[111] = "KAZ";     arISO2[111] = "KZ";     arISONumeric[111] = "398";    arFullNames[111] = "Kazakhstan";                                     arAltNames[111] = "";
		arISO3[112] = "KEN";     arISO2[112] = "KE";     arISONumeric[112] = "404";    arFullNames[112] = "Kenya";                                          arAltNames[112] = "";
		arISO3[113] = "KIR";     arISO2[113] = "KI";     arISONumeric[113] = "296";    arFullNames[113] = "Kiribati";                                       arAltNames[113] = "";
		arISO3[114] = "PRK";     arISO2[114] = "KP";     arISONumeric[114] = "408";    arFullNames[114] = "Korea (North)";                                  arAltNames[114] = "North Korea:N. Korea:DPRK";
		arISO3[115] = "KOR";     arISO2[115] = "KR";     arISONumeric[115] = "410";    arFullNames[115] = "Korea (South)";                                  arAltNames[115] = "South Korea:S. Korea";

		arISO3[116] = "KWT";     arISO2[116] = "KW";     arISONumeric[116] = "414";    arFullNames[116] = "Kuwait";                                         arAltNames[116] = "";
		arISO3[117] = "KGZ";     arISO2[117] = "KG";     arISONumeric[117] = "417";    arFullNames[117] = "Kyrgyzstan";                                     arAltNames[117] = "";
		arISO3[118] = "LAO";     arISO2[118] = "LA";     arISONumeric[118] = "418";    arFullNames[118] = "Lao People's Democratic Republic";               arAltNames[118] = "Laos:Muang Lao";
		arISO3[119] = "LVA";     arISO2[119] = "LV";     arISONumeric[119] = "428";    arFullNames[119] = "Latvia";                                         arAltNames[119] = "";
		arISO3[120] = "LBN";     arISO2[120] = "LB";     arISONumeric[120] = "422";    arFullNames[120] = "Lebanon";                                        arAltNames[120] = "";

		arISO3[121] = "LSO";     arISO2[121] = "LS";     arISONumeric[121] = "426";    arFullNames[121] = "Lesotho";                                        arAltNames[121] = "";
		arISO3[122] = "LBR";     arISO2[122] = "LR";     arISONumeric[122] = "430";    arFullNames[122] = "Liberia";                                        arAltNames[122] = "";
		arISO3[123] = "LBY";     arISO2[123] = "LY";     arISONumeric[123] = "434";    arFullNames[123] = "Libyan Arab Jamahiriya";                         arAltNames[123] = "Libya";
		arISO3[124] = "LIE";     arISO2[124] = "LI";     arISONumeric[124] = "438";    arFullNames[124] = "Liechtenstein";                                  arAltNames[124] = "";
		arISO3[125] = "LTU";     arISO2[125] = "LT";     arISONumeric[125] = "440";    arFullNames[125] = "Lithuania";                                      arAltNames[125] = "";

		arISO3[126] = "LUX";     arISO2[126] = "LU";     arISONumeric[126] = "442";    arFullNames[126] = "Luxembourg";                                     arAltNames[126] = "";
		arISO3[127] = "MAC";     arISO2[127] = "MO";     arISONumeric[127] = "446";    arFullNames[127] = "Macao";                                          arAltNames[127] = "";
		arISO3[128] = "MKD";     arISO2[128] = "MK";     arISONumeric[128] = "807";    arFullNames[128] = "Macedonia";                                      arAltNames[128] = "";
		arISO3[129] = "MDG";     arISO2[129] = "MG";     arISONumeric[129] = "450";    arFullNames[129] = "Madagascar";                                     arAltNames[129] = "";
		arISO3[130] = "MWI";     arISO2[130] = "MW";     arISONumeric[130] = "454";    arFullNames[130] = "Malawi";                                         arAltNames[130] = "";

		arISO3[131] = "MYS";     arISO2[131] = "MY";     arISONumeric[131] = "458";    arFullNames[131] = "Malaysia";                                       arAltNames[131] = "";
		arISO3[132] = "MDV";     arISO2[132] = "MV";     arISONumeric[132] = "462";    arFullNames[132] = "Maldives";                                       arAltNames[132] = "";
		arISO3[133] = "MLI";     arISO2[133] = "ML";     arISONumeric[133] = "466";    arFullNames[133] = "Mali";                                           arAltNames[133] = "";
		arISO3[134] = "MLT";     arISO2[134] = "MT";     arISONumeric[134] = "470";    arFullNames[134] = "Malta";                                          arAltNames[134] = "";
		arISO3[135] = "MHL";     arISO2[135] = "MH";     arISONumeric[135] = "584";    arFullNames[135] = "Marshall Islands";                               arAltNames[135] = "";

		arISO3[136] = "MTQ";     arISO2[136] = "MQ";     arISONumeric[136] = "474";    arFullNames[136] = "Martinique";                                     arAltNames[136] = "";
		arISO3[137] = "MRT";     arISO2[137] = "MR";     arISONumeric[137] = "478";    arFullNames[137] = "Mauritania";                                     arAltNames[137] = "";
		arISO3[138] = "MUS";     arISO2[138] = "MU";     arISONumeric[138] = "480";    arFullNames[138] = "Mauritius";                                      arAltNames[138] = "";
		arISO3[139] = "MYT";     arISO2[139] = "YT";     arISONumeric[139] = "175";    arFullNames[139] = "Mayotte";                                        arAltNames[139] = "";
		arISO3[140] = "MEX";     arISO2[140] = "MX";     arISONumeric[140] = "484";    arFullNames[140] = "Mexico";                                         arAltNames[140] = "";

		arISO3[141] = "FSM";     arISO2[141] = "FM";     arISONumeric[141] = "583";    arFullNames[141] = "Micronesia";                                     arAltNames[141] = "";
		arISO3[142] = "MDA";     arISO2[142] = "MD";     arISONumeric[142] = "498";    arFullNames[142] = "Moldova";                                        arAltNames[142] = "";
		arISO3[143] = "MCO";     arISO2[143] = "MC";     arISONumeric[143] = "492";    arFullNames[143] = "Monaco";                                         arAltNames[143] = "";
		arISO3[144] = "MNG";     arISO2[144] = "MN";     arISONumeric[144] = "496";    arFullNames[144] = "Mongolia";                                       arAltNames[144] = "";
		arISO3[145] = "MNE";     arISO2[145] = "ME";     arISONumeric[145] = "499";    arFullNames[145] = "Montenegro";                                     arAltNames[145] = "";

		arISO3[146] = "MSR";     arISO2[146] = "MS";     arISONumeric[146] = "500";    arFullNames[146] = "Montserrat";                                     arAltNames[146] = "";
		arISO3[147] = "MAR";     arISO2[147] = "MA";     arISONumeric[147] = "504";    arFullNames[147] = "Morocco";                                        arAltNames[147] = "";
		arISO3[148] = "MOZ";     arISO2[148] = "MZ";     arISONumeric[148] = "508";    arFullNames[148] = "Mozambique";                                     arAltNames[148] = "";
		arISO3[149] = "MMR";     arISO2[149] = "MM";     arISONumeric[149] = "104";    arFullNames[149] = "Myanmar";                                        arAltNames[149] = "";
		arISO3[150] = "NAM";     arISO2[150] = "NA";     arISONumeric[150] = "516";    arFullNames[150] = "Namibia";                                        arAltNames[150] = "";

		arISO3[151] = "NRU";     arISO2[151] = "NR";     arISONumeric[151] = "520";    arFullNames[151] = "Nauru";                                          arAltNames[151] = "";
		arISO3[152] = "NPL";     arISO2[152] = "NP";     arISONumeric[152] = "524";    arFullNames[152] = "Nepal";                                          arAltNames[152] = "";
		arISO3[153] = "NLD";     arISO2[153] = "NL";     arISONumeric[153] = "528";    arFullNames[153] = "Netherlands";                                    arAltNames[153] = "";
		arISO3[154] = "ANT";     arISO2[154] = "AN";     arISONumeric[154] = "530";    arFullNames[154] = "Netherlands Antilles";                           arAltNames[154] = "Dutch Antilles";
		arISO3[155] = "NCL";     arISO2[155] = "NC";     arISONumeric[155] = "540";    arFullNames[155] = "New Caledonia";                                  arAltNames[155] = "Caledonia";

		arISO3[156] = "NZL";     arISO2[156] = "NZ";     arISONumeric[156] = "554";    arFullNames[156] = "New Zealand";                                    arAltNames[156] = "";
		arISO3[157] = "NIC";     arISO2[157] = "NI";     arISONumeric[157] = "558";    arFullNames[157] = "Nicaragua";                                      arAltNames[157] = "";
		arISO3[158] = "NER";     arISO2[158] = "NE";     arISONumeric[158] = "562";    arFullNames[158] = "Niger";                                          arAltNames[158] = "";
		arISO3[159] = "NGA";     arISO2[159] = "NG";     arISONumeric[159] = "566";    arFullNames[159] = "Nigeria";                                        arAltNames[159] = "";
		arISO3[160] = "NIU";     arISO2[160] = "NU";     arISONumeric[160] = "570";    arFullNames[160] = "Niue";                                           arAltNames[160] = "";

		arISO3[161] = "NFK";     arISO2[161] = "NF";     arISONumeric[161] = "574";    arFullNames[161] = "Norfolk Island";                                 arAltNames[161] = "";
		arISO3[162] = "MNP";     arISO2[162] = "MP";     arISONumeric[162] = "580";    arFullNames[162] = "Northern Mariana Islands";                       arAltNames[162] = "Mariana Islands";
		arISO3[163] = "NOR";     arISO2[163] = "NO";     arISONumeric[163] = "578";    arFullNames[163] = "Norway";                                         arAltNames[163] = "";
		arISO3[164] = "OMN";     arISO2[164] = "OM";     arISONumeric[164] = "512";    arFullNames[164] = "Oman";                                           arAltNames[164] = "";
		arISO3[165] = "PAK";     arISO2[165] = "PK";     arISONumeric[165] = "586";    arFullNames[165] = "Pakistan";                                       arAltNames[165] = "";

		arISO3[166] = "PLW";     arISO2[166] = "PW";     arISONumeric[166] = "585";    arFullNames[166] = "Palau";                                          arAltNames[166] = "";
		arISO3[167] = "PSE";     arISO2[167] = "PS";     arISONumeric[167] = "275";    arFullNames[167] = "Palestinian Territory";                          arAltNames[167] = "Palestine:State of Palestine";
		arISO3[168] = "PAN";     arISO2[168] = "PA";     arISONumeric[168] = "591";    arFullNames[168] = "Panama";                                         arAltNames[168] = "";
		arISO3[169] = "PNG";     arISO2[169] = "PG";     arISONumeric[169] = "598";    arFullNames[169] = "Papua New Guinea";                               arAltNames[169] = "";
		arISO3[170] = "PRY";     arISO2[170] = "PY";     arISONumeric[170] = "600";    arFullNames[170] = "Paraguay";                                       arAltNames[170] = "";

		arISO3[171] = "PER";     arISO2[171] = "PE";     arISONumeric[171] = "604";    arFullNames[171] = "Peru";                                           arAltNames[171] = "";
		arISO3[172] = "PHL";     arISO2[172] = "PH";     arISONumeric[172] = "608";    arFullNames[172] = "Philippines";                                    arAltNames[172] = "";
		arISO3[173] = "PCN";     arISO2[173] = "PN";     arISONumeric[173] = "612";    arFullNames[173] = "Pitcairn Island";                                arAltNames[173] = "Pitcairn:Henderson:Ducie and Oeno Islands";
		arISO3[174] = "POL";     arISO2[174] = "PL";     arISONumeric[174] = "616";    arFullNames[174] = "Poland";                                         arAltNames[174] = "";
		arISO3[175] = "PRT";     arISO2[175] = "PT";     arISONumeric[175] = "620";    arFullNames[175] = "Portugal";                                       arAltNames[175] = "";

		arISO3[176] = "PRI";     arISO2[176] = "PR";     arISONumeric[176] = "630";    arFullNames[176] = "Puerto Rico";                                    arAltNames[176] = "Commonwealth of Puerto Rico";
		arISO3[177] = "QAT";     arISO2[177] = "QA";     arISONumeric[177] = "634";    arFullNames[177] = "Qatar";                                          arAltNames[177] = "";
		arISO3[178] = "REU";     arISO2[178] = "RE";     arISONumeric[178] = "638";    arFullNames[178] = "R" + '\u00e9' + "union"; /*display é correctly */arAltNames[178] = "Réunion:Reunion";
		arISO3[179] = "ROU";     arISO2[179] = "RO";     arISONumeric[179] = "642";    arFullNames[179] = "Romania";                                        arAltNames[179] = "";
		arISO3[180] = "RUS";     arISO2[180] = "RU";     arISONumeric[180] = "643";    arFullNames[180] = "Russian Federation";                             arAltNames[180] = "Russia";

		arISO3[181] = "RWA";     arISO2[181] = "RW";     arISONumeric[181] = "646";    arFullNames[181] = "Rwanda";                                         arAltNames[181] = "";
		arISO3[182] = "BLM";     arISO2[182] = "BL";     arISONumeric[182] = "652";    arFullNames[182] = "Saint Barth" + '\u00e9' + "lemy";                arAltNames[182] = "Saint Barthélemy:Saint Barthelemy";
		arISO3[183] = "SHN";     arISO2[183] = "SH";     arISONumeric[183] = "654";    arFullNames[183] = "Saint Helena";                                   arAltNames[183] = "";
		arISO3[184] = "KNA";     arISO2[184] = "KN";     arISONumeric[184] = "659";    arFullNames[184] = "Saint Kitts and Nevis";                          arAltNames[184] = "Federation of Saint Christopher and Nevis:Saint Christopher and Nevis";
		arISO3[185] = "LCA";     arISO2[185] = "LC";     arISONumeric[185] = "662";    arFullNames[185] = "Saint Lucia";                                    arAltNames[185] = "";

		arISO3[186] = "MAF";     arISO2[186] = "MF";     arISONumeric[186] = "663";    arFullNames[186] = "Saint Martin";                                   arAltNames[186] = "";
		arISO3[187] = "SPM";     arISO2[187] = "PM";     arISONumeric[187] = "666";    arFullNames[187] = "Saint Pierre and Miquelon";                      arAltNames[187] = "";
		arISO3[188] = "VCT";     arISO2[188] = "VC";     arISONumeric[188] = "670";    arFullNames[188] = "Saint Vincent and the Grenadines";               arAltNames[188] = "Saint Vincent";
		arISO3[189] = "WSM";     arISO2[189] = "WS";     arISONumeric[189] = "882";    arFullNames[189] = "Samoa";                                          arAltNames[189] = "Independent State of Samoa:Samoa:Western Samoa";
		arISO3[190] = "SMR";     arISO2[190] = "SM";     arISONumeric[190] = "674";    arFullNames[190] = "San Marino";                                     arAltNames[190] = "";

		arISO3[191] = "STP";     arISO2[191] = "ST";     arISONumeric[191] = "678";    arFullNames[191] = "Sao Tome and Principe";                          arAltNames[191] = "";
		arISO3[192] = "SAU";     arISO2[192] = "SA";     arISONumeric[192] = "682";    arFullNames[192] = "Saudi Arabia";                                   arAltNames[192] = "";
		arISO3[193] = "SEN";     arISO2[193] = "SN";     arISONumeric[193] = "686";    arFullNames[193] = "Senegal";                                        arAltNames[193] = "";
		arISO3[194] = "SRB";     arISO2[194] = "RS";     arISONumeric[194] = "688";    arFullNames[194] = "Serbia";                                         arAltNames[194] = "";
		arISO3[195] = "SYC";     arISO2[195] = "SC";     arISONumeric[195] = "690";    arFullNames[195] = "Seychelles";                                     arAltNames[195] = "";

		arISO3[196] = "SLE";     arISO2[196] = "SL";     arISONumeric[196] = "694";    arFullNames[196] = "Sierra Leone";                                   arAltNames[196] = "";
		arISO3[197] = "SGP";     arISO2[197] = "SG";     arISONumeric[197] = "702";    arFullNames[197] = "Singapore";                                      arAltNames[197] = "";
		arISO3[198] = "SVK";     arISO2[198] = "SK";     arISONumeric[198] = "703";    arFullNames[198] = "Slovakia";                                       arAltNames[198] = "";
		arISO3[199] = "SVN";     arISO2[199] = "SI";     arISONumeric[199] = "705";    arFullNames[199] = "Slovenia";                                       arAltNames[199] = "";
		arISO3[200] = "SLB";     arISO2[200] = "SB";     arISONumeric[200] = "090";    arFullNames[200] = "Solomon Islands";                                arAltNames[200] = "";

		arISO3[201] = "SOM";     arISO2[201] = "SO";     arISONumeric[201] = "706";    arFullNames[201] = "Somalia";                                        arAltNames[201] = "";
		arISO3[202] = "ZAF";     arISO2[202] = "ZA";     arISONumeric[202] = "710";    arFullNames[202] = "South Africa";                                   arAltNames[202] = "";
		arISO3[203] = "SGS";     arISO2[203] = "GS";     arISONumeric[203] = "239";    arFullNames[203] = "South George and the South Sandwich Islands";    arAltNames[203] = "South George:South Sandwich Islands";
		arISO3[204] = "ESP";     arISO2[204] = "ES";     arISONumeric[204] = "724";    arFullNames[204] = "Spain";                                          arAltNames[204] = "";
		arISO3[205] = "LKA";     arISO2[205] = "LK";     arISONumeric[205] = "144";    arFullNames[205] = "Sri Lanka";                                      arAltNames[205] = "";

		arISO3[206] = "SDN";     arISO2[206] = "SD";     arISONumeric[206] = "736";    arFullNames[206] = "Sudan";                                          arAltNames[206] = "";
		arISO3[207] = "SUR";     arISO2[207] = "SR";     arISONumeric[207] = "740";    arFullNames[207] = "Suriname";                                       arAltNames[207] = "";
		arISO3[208] = "SJM";     arISO2[208] = "SJ";     arISONumeric[208] = "744";    arFullNames[208] = "Svalbard and Jan Mayen";                         arAltNames[208] = "";
		arISO3[209] = "SWZ";     arISO2[209] = "SZ";     arISONumeric[209] = "748";    arFullNames[209] = "Swaziland";                                      arAltNames[209] = "";
		arISO3[210] = "SWE";     arISO2[210] = "SE";     arISONumeric[210] = "752";    arFullNames[210] = "Sweden";                                         arAltNames[210] = "";

		arISO3[211] = "CHE";     arISO2[211] = "CH";     arISONumeric[211] = "756";    arFullNames[211] = "Switzerland";                                    arAltNames[211] = "";
		arISO3[212] = "SYR";     arISO2[212] = "SY";     arISONumeric[212] = "760";    arFullNames[212] = "Syrian Arab Republic";                           arAltNames[212] = "Syria";
		arISO3[213] = "TWN";     arISO2[213] = "TW";     arISONumeric[213] = "158";    arFullNames[213] = "Taiwan";                                         arAltNames[213] = "";
		arISO3[214] = "TJK";     arISO2[214] = "TJ";     arISONumeric[214] = "762";    arFullNames[214] = "Tajikistan";                                     arAltNames[214] = "";
		arISO3[215] = "TZA";     arISO2[215] = "TZ";     arISONumeric[215] = "834";    arFullNames[215] = "Tanzania";                                       arAltNames[215] = "";

		arISO3[216] = "THA";     arISO2[216] = "TH";     arISONumeric[216] = "764";    arFullNames[216] = "Thailand";                                       arAltNames[216] = "";
		arISO3[217] = "TLS";     arISO2[217] = "TL";     arISONumeric[217] = "626";    arFullNames[217] = "Timor-Leste";                                    arAltNames[217] = "East Timor";
		arISO3[218] = "TGO";     arISO2[218] = "TG";     arISONumeric[218] = "768";    arFullNames[218] = "Togo";                                           arAltNames[218] = "";
		arISO3[219] = "TKL";     arISO2[219] = "TK";     arISONumeric[219] = "772";    arFullNames[219] = "Tokelau";                                        arAltNames[219] = "";
		arISO3[220] = "TON";     arISO2[220] = "TO";     arISONumeric[220] = "776";    arFullNames[220] = "Tonga";                                          arAltNames[220] = "";

		arISO3[221] = "TTO";     arISO2[221] = "TT";     arISONumeric[221] = "780";    arFullNames[221] = "Trinidad and Tobago";                            arAltNames[221] = "Trinidad:Tobago";
		arISO3[222] = "TUN";     arISO2[222] = "TN";     arISONumeric[222] = "788";    arFullNames[222] = "Tunisia";                                        arAltNames[222] = "";
		arISO3[223] = "TUR";     arISO2[223] = "TR";     arISONumeric[223] = "792";    arFullNames[223] = "Turkey";                                         arAltNames[223] = "";
		arISO3[224] = "TKM";     arISO2[224] = "TM";     arISONumeric[224] = "795";    arFullNames[224] = "Turkmenistan";                                   arAltNames[224] = "";
		arISO3[225] = "TCA";     arISO2[225] = "TC";     arISONumeric[225] = "796";    arFullNames[225] = "Turks and Caicos Islands";                       arAltNames[225] = "Caicos Islands:Turks Islands";

		arISO3[226] = "TUV";     arISO2[226] = "TV";     arISONumeric[226] = "798";    arFullNames[226] = "Tuvalu";                                         arAltNames[226] = "";
		arISO3[227] = "UGA";     arISO2[227] = "UG";     arISONumeric[227] = "800";    arFullNames[227] = "Uganda";                                         arAltNames[227] = "";
		arISO3[228] = "UKR";     arISO2[228] = "UA";     arISONumeric[228] = "804";    arFullNames[228] = "Ukraine";                                        arAltNames[228] = "";
		arISO3[229] = "ARE";     arISO2[229] = "AE";     arISONumeric[229] = "784";    arFullNames[229] = "United Arab Emirates";                           arAltNames[229] = "UAE:Emirates";
		arISO3[230] = "GBR";     arISO2[230] = "GB";     arISONumeric[230] = "826";    arFullNames[230] = "United Kingdom";                                 arAltNames[230] = "Great Britain:UK";

		arISO3[231] = "USA";     arISO2[231] = "US";     arISONumeric[231] = "840";    arFullNames[231] = "United States";                                  arAltNames[231] = "United States of America:America";
		arISO3[232] = "UMI";     arISO2[232] = "UM";     arISONumeric[232] = "581";    arFullNames[232] = "United States Minor Outlying Islands";           arAltNames[232] = "";
		arISO3[233] = "VIR";     arISO2[233] = "VI";     arISONumeric[233] = "850";    arFullNames[233] = "United States Virgin Islands";                   arAltNames[233] = "American Virgin Islands:Virgin Islands";
		arISO3[234] = "URY";     arISO2[234] = "UY";     arISONumeric[234] = "858";    arFullNames[234] = "Uruguay";                                        arAltNames[234] = "";
		arISO3[235] = "UZB";     arISO2[235] = "UZ";     arISONumeric[235] = "860";    arFullNames[235] = "Uzbekistan";                                     arAltNames[235] = "";

		arISO3[236] = "VUT";     arISO2[236] = "VU";     arISONumeric[236] = "548";    arFullNames[236] = "Vanuatu";                                        arAltNames[236] = "";
		arISO3[237] = "VEN";     arISO2[237] = "VE";     arISONumeric[237] = "862";    arFullNames[237] = "Venezuela";                                      arAltNames[237] = "";
		arISO3[238] = "VNM";     arISO2[238] = "VN";     arISONumeric[238] = "704";    arFullNames[238] = "Viet Nam";                                       arAltNames[238] = "";
		arISO3[239] = "WLF";     arISO2[239] = "WF";     arISONumeric[239] = "876";    arFullNames[239] = "Wallis and Futuna";                              arAltNames[239] = "";
		arISO3[240] = "ESH";     arISO2[240] = "EH";     arISONumeric[240] = "732";    arFullNames[240] = "Western Sahara";                                 arAltNames[240] = "";

		arISO3[241] = "YEM";     arISO2[241] = "YE";     arISONumeric[241] = "887";    arFullNames[241] = "Yemen";                                          arAltNames[241] = "";
		arISO3[242] = "ZMB";     arISO2[242] = "ZM";     arISONumeric[242] = "894";    arFullNames[242] = "Zambia";                                         arAltNames[242] = "";
		arISO3[243] = "ZWE";     arISO2[243] = "ZW";     arISONumeric[243] = "716";    arFullNames[243] = "Zimbabwe";                                       arAltNames[243] = "";

		arISO3[244] = "HMD";     arISO2[244] = "HM";     arISONumeric[244] = "334";    arFullNames[244] = "Heard and Mcdonald Islands";                     arAltNames[244] = "HIMI:Mcdonald and Heard Islands";
		arISO3[245] = "SSD";     arISO2[245] = "SS";     arISONumeric[245] = "728";    arFullNames[245] = "South Sudan";                                    arAltNames[245] = "";

	}
	//@formatter:on
}
