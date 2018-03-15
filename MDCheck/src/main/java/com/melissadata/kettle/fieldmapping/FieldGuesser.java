package com.melissadata.kettle.fieldmapping;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.melissadata.cz.SourceFieldInfo;

public class FieldGuesser {

	private SortedMap<String, SourceFieldInfo> sourceFields;
	private static final Pattern FULL_NAME_PAT      = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Nn][Aa][Mm][Ee][^A-Z^a-z^^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Ff][Uu][Ll][Ll].?[Nn][Aa][Mm][Ee][^A-Z^a-z^1-9]?[1]?");
	private static final Pattern COMPANY_NAME_PAT   = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Cc][Oo][Mm]?[Pp]?[Aa]?[Nn]?[Yy]?");
	private static final Pattern SM_PREFIX_NAME_PAT = Pattern
			.compile("(.?.?.?[^A-Z^a-z^0-9])?[Nn][Aa][Mm][Ee].?.?[EeRrPp][FfEeRr]?[IiFfEe1]?[FfIiXx]?[IiXx]?[Xx1]?|(.?.?.?[^A-Z^a-z^0-9])?[Pp][FfRr][EeXx].?[AaNnIi]?[AaMmXx]?[^2-9]?[AaNn]?[AaMm]?[MmEe]?[Ee1]?[1]?");
	private static final Pattern SM_FIRST_NAME_PAT  = Pattern
			.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ff][Ii]?[Rr]?[Ss]?[Tt]?[^A-Z^a-z^1-9]?[Nn][Aa]?[Mm][Ee][^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Nn]?[Aa]?[Mm]?[Ee]?[^A-Z^a-z^1-9]?[Ff][Ii]?[Rr]?[Ss]?[Tt]?[^A-Z^a-z^1-9]?[1]?");
	private static final Pattern SM_MIDDLE_NAME_PAT = Pattern
			.compile("(.?.?.?[^A-Z^a-z^0-9])?[Mm][Ii]?[Dd]?[Dd]?[Ll]?[Ee]?.?[Nn][Aa][Mm][Ee][^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Nn]?[Aa]?[Mm]?[Ee]?.?[Mm][Ii][Dd][Dd]?[Ll]?[Ee]?[^A-Z^a-z^1-9]?[1]?");
	private static final Pattern SM_SUFFIX_NAME_PAT = Pattern
			.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ss][FfUu][Ff]?[FfIiXx]?[IiXx1]?[Xx1]?[1]?.?.?[Nn]?[Aa]?[Mm]?[Ee]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Nn][Aa][Mm][Ee].?.?[FfSsUu]?[FfUu]?[FfIi]?[FfIiXx]?[IiXx1]?[FfXx1]?");
	private static final Pattern SM_FIND_SUFFIX_PAT = Pattern.compile("[Ss][FfUu][FfXx][FfIiXx]?[IiXx]?[Xx]?");
	private static final Pattern SM_PMB_PAT         = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Pp][Rr]?[Ii]?[Vv]?[Aa]?[Tt]?[Ee]?.?[Mm][Aa][Ii][Ll][Bb][Oo][Xx][1]?|(.?.?.?[^A-Z^a-z^0-9])?[Pp][Mm][Bb]");
	private static final Pattern ADDR_LAST_NAME_PAT = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ll][Aa]?[Ss]?[Tt]?.?[Nn][Aa][Mm][Ee][^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Nn]?[Aa]?[Mm]?[Ee]?.?[Ll][Aa]?[Ss]?[Tt]?[^A-Z^a-z^1-9]?[1]?");
	private static final Pattern ADDRESS1_PAT       = Pattern.compile(
			"(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr]?[Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt][Rr][Ee][Ee][Tt][^A-Z^a-z^1-9]?[1]?|(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr][Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[Ll][Ii]?[Nn][Ee][^A-Z^a-z^1-9]?[1]?");
	private static final Pattern ADDRESS2_PAT       = Pattern.compile(
			"(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr]?[Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[2]|(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt][Rr][Ee][Ee][Tt][^A-Z^a-z^1-9]?[2]|(.?.?.?[^A-Z^a-z^0-9])?[Aa][Dd][Dd][Rr][Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[Ll][Ii]?[Nn][Ee][^A-Z^a-z^1-9]?[2]");
	private static final Pattern CITY_PAT           = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Cc][Ii][Tt][Yy]|[Cc][Tt][Yy]");
	private static final Pattern STATE_PAT          = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt][Aa][Tt][Ee]|(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt]|(.?.?.?[^A-Z^a-z^0-9])?[Pp][Rr][Oo][Vv][Ii][Nn][Cc][Ee]|(.?.?.?[^A-Z^a-z^0-9])?[Pp][Rr][Oo][Vv]");
	private static final Pattern ZIP_PAT            = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Zz][Ii][Pp].?[Cc]?[Oo]?[Dd]?[Ee]?|(.?.?.?[^A-Z^a-z^0-9])?[Pp][Oo][Ss][Tt][Aa]?[Ll]?.?[Cc][Oo][Dd][Ee]");
	private static final Pattern COUNTRY_PAT        = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Cc][Oo][Uu][Nn][Tt][Rr][Yy]");
	private static final Pattern SUITE_PAT          = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ss][Uu][Ii][Tt][Ee]|(.?.?.?[^A-Z^a-z^0-9])?[Ss][Tt][Ee]|(.?.?.?[^A-Z^a-z^0-9])?[Aa][Pp][Tt]");
	private static final Pattern URBAN_PAT          = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Uu][Rr][Bb].?[Nn]?.?[AaMmNnRrZz]?[AaEeMmRr]?[AaEeMmTt]?[EeIi]?[Oo]?[Nn]?.?[AaNn]?[AaRr]?[EeMm]?[AaEe]?");
	private static final Pattern PLUS4_PAT          = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[PpZz][LlIi][UuPp].?.?[4]");
	private static final Pattern PHONE_PAT          = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Pp][Hh][Oo]?[Nn][Ee1]?[^A-Z^a-z^2-9]?[Nn01]?[_OoUu]?[Mm1]?[Bb1]?[Ee]?[Rr]?[1]?");
	private static final Pattern EMAIL_PAT          = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ee][Mm][Aa][Ii][Ll][^A-Z^a-z^2-9]?[01]?");
	private static final Pattern IP_PAT             = Pattern.compile("(.?.?.?[^A-Z^a-z^0-9])?[Ii][Pp][^A-Z^a-z^0-9]?[Aa][Dd][Dd]?[Rr]?[Ee]?[Ss]?[Ss]?[^A-Z^a-z^1-9]?[1]?|");

	public FieldGuesser(SortedMap<String, SourceFieldInfo> sourceFields) {

		this.sourceFields = sourceFields;
	}

	public String guessAddrCompanyNameInput(boolean doName) {

		if (doName) {
			return "[Name Parse Company]";
		}
		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessAddressLine1Input() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessAddrLastNameInput(boolean doName) {

		if (doName) {
			return "[Name Parse Last Name 1]";
		}
		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = ADDR_LAST_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessCityInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessCountryInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessEmailInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessIPInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = IP_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessNameParseCompanyNameInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessNameParseFullNameInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessPhoneInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessPluss4Input() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = PLUS4_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessSMFirstNameInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = SM_FIRST_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessSMMiddleNameInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = SM_MIDDLE_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessSMPMBInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = SM_PMB_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessSMPrefixInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = SM_PREFIX_NAME_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessSMSuffixInput() {

		Matcher                                  matcher;
		Matcher                                  finder;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = SM_SUFFIX_NAME_PAT.matcher(sourceName);
			finder = SM_FIND_SUFFIX_PAT.matcher(sourceName);
			if (matcher.matches() && finder.find()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessStateInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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

	public String guessSuiteInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = SUITE_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessUrbanInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
		while (it.hasNext()) {
			sourceName = it.next().getKey();
			matcher = URBAN_PAT.matcher(sourceName);
			if (matcher.matches()) {
				value = sourceName;
				break;
			}
		}
		return value;
	}

	public String guessZipInput() {

		Matcher                                  matcher;
		String                                   sourceName = "";
		String                                   value      = "";
		Iterator<Entry<String, SourceFieldInfo>> it         = sourceFields.entrySet().iterator();
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
}
