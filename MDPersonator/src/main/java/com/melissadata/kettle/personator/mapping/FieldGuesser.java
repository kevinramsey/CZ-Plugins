package com.melissadata.kettle.personator.mapping;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.melissadata.cz.SourceFieldInfo;

public class FieldGuesser {

	private SortedMap<String, SourceFieldInfo> sourceFields;

	private static final Pattern FULL_NAME_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Nn][Aa][Mm][Ee][^A-Z^a-z^^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Ff][Uu][Ll][Ll].?[Nn][Aa][Mm][Ee][^A-Z^a-z^1-9]?[1]?");
	private static final Pattern COMPANY_NAME_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Cc][Oo][Mm]?[Pp]?[Aa]?[Nn]?[Yy]?");
	private static final Pattern FIRST_NAME_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ff][Ii]?[Rr]?[Ss]?[Tt]?[^A-Z^a-z^1-9]?[Nn][Aa]?[Mm][Ee][^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Nn]?[Aa]?[Mm]?[Ee]?[^A-Z^a-z^1-9]?[Ff][Ii]?[Rr]?[Ss]?[Tt]?[^A-Z^a-z^1-9]?[1]?");
	private static final Pattern LAST_NAME_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ll][Aa]?[Ss]?[Tt]?.?[Nn][Aa][Mm][Ee][^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Nn]?[Aa]?[Mm]?[Ee]?.?[Ll][Aa]?[Ss]?[Tt]?[^A-Z^a-z^1-9]?[1]?");

	private static final Pattern ADDRESS1_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr]?[Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt][Rr][Ee][Ee][Tt][^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr][Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[Ll][Ii]?[Nn][Ee][^A-Z^a-z^1-9]?[1]?");
	private static final Pattern ADDRESS2_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr]?[Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[2]|(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt][Rr][Ee][Ee][Tt][^A-Z^a-z^1-9]?[2]|(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr][Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[Ll][Ii]?[Nn][Ee][^A-Z^a-z^1-9]?[2]");
	private static final Pattern CITY_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Cc][Ii][Tt][Yy]|[Cc][Tt][Yy]");
	private static final Pattern STATE_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt][Aa][Tt][Ee]|(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt]|(.?.?.?[^A-Z^a-z^0-9])?[Pp][Rr][Oo][Vv][Ii][Nn][Cc][Ee]|(.?.?.?[^A-Z^a-z^0-9])?[Pp][Rr][Oo][Vv]");
	private static final Pattern ZIP_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Zz][Ii][Pp].?[Cc]?[Oo]?[Dd]?[Ee]?|(.?.?.?[^A-Z^a-z^0-9])?[Pp][Oo][Ss][Tt][Aa]?[Ll]?.?[Cc][Oo][Dd][Ee]");
	private static final Pattern COUNTRY_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Cc][Oo][Uu][Nn][Tt][Rr][Yy]");

	private static final Pattern PHONE_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Pp][Hh][Oo]?[Nn][Ee1]?[^A-Z^a-z^2-9]?[Nn01]?[_OoUu]?[Mm1]?[Bb1]?[Ee]?[Rr]?[1]?");

	private static final Pattern EMAIL_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ee][Mm][Aa][Ii][Ll][^A-Z^a-z^2-9]?[01]?");

	
	public FieldGuesser(SortedMap<String, SourceFieldInfo> sourceFields) {
		this.sourceFields = sourceFields;
	}

	public String guessFullNameInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = FULL_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessCompanyNameInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = COMPANY_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessFirstNameInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = FIRST_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessLastNameInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = LAST_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessAddressLine1Input() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = ADDRESS1_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessAddressLine2Input() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = ADDRESS2_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessCityInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = CITY_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessStateInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = STATE_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessZipInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = ZIP_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessCountryInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = COUNTRY_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessPhoneInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = PHONE_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

	public String guessEmailInput() {

		Matcher matcher;
		String sourceName = "";
		String value = "";
		Iterator<Entry<String, SourceFieldInfo>> it = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = EMAIL_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		
		return value;
	}

}
