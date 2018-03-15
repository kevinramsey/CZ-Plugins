package com.melissadata.kettle.mu;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;

import com.melissadata.mdMUHybrid;
import com.melissadata.mdMUMatchcode;
import com.melissadata.mdMUMatchcode.MatchcodeMapping;
import com.melissadata.mdMUMatchcodeComponent;
import com.melissadata.mdMUReadWrite;
import com.melissadata.mdMatchUpJavaWrapperJNI;
import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;

public class MatchUpUtil {
	/*
	 * Code to find the best mapping for a target
	 */
	private static class AllowedMapping {
		private mdMUMatchcode.MatchcodeMappingTarget	target;
		private mdMUMatchcode.MatchcodeMapping[]		mappings;

		public AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget target, mdMUMatchcode.MatchcodeMapping[] mappings) {
			this.target = target;
			this.mappings = mappings;
		}
	}

	/*
	 * Code to retrieve a description of matchcode components
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	private static class Component {
		mdMUMatchcodeComponent.MatchcodeComponentType	type;
		String											description;

		public Component(mdMUMatchcodeComponent.MatchcodeComponentType type_, String description_) {
			type = type_;
			description = description_;
		}
	}

	/*
	 * Code to get a description of a fuzzy setting
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	private static class Fuzzy {
		private mdMUMatchcodeComponent.MatchcodeFuzzy	type;
		private String									description;
		private boolean									decorated;

		public Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy type_, String description_) {
			type = type_;
			description = description_;
			decorated = false;
		}

		public Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy type_, String description_, boolean decorated_) {
			type = type_;
			description = description_;
			decorated = decorated_;
		}
	}

	/*
	 * Code to find a label for a matchcode mapping data type
	 */
	private static class MappingDescription {
		private mdMUMatchcode.MatchcodeMapping	type;
		private String							label;

		public MappingDescription(mdMUMatchcode.MatchcodeMapping type, String label) {
			this.type = type;
			this.label = label;
		}
	}

	public static void copyMCfile(String dataPath, boolean isEnterprise) {
		// create a copy of the .mc file to use for lite and community
		File copyOfmcFile = new File(dataPath + Const.FILE_SEPARATOR + "mdMatchupLite.mc");
		File originalMcFile = new File(dataPath + Const.FILE_SEPARATOR + "mdMatchup.mc");
		if (!copyOfmcFile.exists()) {
			try {
				FileUtils.copyFile(originalMcFile, copyOfmcFile);
			} catch (IOException e) {
				System.out.println("IO error copying .mc file. " + e.getMessage());
			}
		}
		// make sure we are using limited list
		if (!isEnterprise) {
			revertMCfile(dataPath, isEnterprise);
		}
	}

	/**
	 * This method will determine the total number of allowed input mapping data types for a given item type
	 *
	 * @param mappingItemType
	 * @return
	 */
	public static int getAllowedInputMappingCount(mdMUMatchcode.MatchcodeMappingTarget mappingItemType) {
		for (AllowedMapping element : ALLOWED_MAPPINGS) {
			if (element.target == mappingItemType) { return element.mappings.length; }
		}
		return 0;
	}

	/**
	 * This method will determine the label for a given input mapping data type
	 *
	 * @param mappingItemType
	 * @param pos
	 * @return
	 */
	public static String getAllowedInputMappingLabel(mdMUMatchcode.MatchcodeMappingTarget mappingItemType, int pos) {
		return getInputMappingLabel(getAllowedInputMappingType(mappingItemType, pos));
	}

	/**
	 * This method will determine the allowed data types for a given input mapping data type
	 *
	 * @param mappingItemType
	 * @param pos
	 * @return
	 */
	public static mdMUMatchcode.MatchcodeMapping getAllowedInputMappingType(mdMUMatchcode.MatchcodeMappingTarget mappingItemType, int pos) {
		for (AllowedMapping element : ALLOWED_MAPPINGS) {
			if ((element.target == mappingItemType) && (pos <= element.mappings.length)) { return element.mappings[pos]; }
		}
		return mdMUMatchcode.MatchcodeMapping.General;
	}

	/**
	 * Determine the best mapping type for the given input
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 *
	 * @param target
	 * @return
	 */
	public static mdMUMatchcode.MatchcodeMapping getBestInputMappingType(mdMUMatchcode.MatchcodeMappingTarget target) {
		for (AllowedMapping element : ALLOWED_MAPPINGS) {
			if (element.target == target) { return element.mappings[0]; }
		}
		return mdMUMatchcode.MatchcodeMapping.General;
	}

	/**
	 * Code to retrieve descriptions of matchcode component combinations
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	public static String[] getCombinationDescriptions(mdMUMatchcodeComponent component) {
		// Call the JNI method directly to get the bit pattern
		long cPtr = mdMUMatchcodeComponent.getCPtr(component);
		int combination = mdMatchUpJavaWrapperJNI.mdMUMatchcodeComponent_GetCombination(cPtr, component);
		String combos[] = new String[16];
		for (int i = 0; i < 16; i++) {
			combos[i] = (((0x01 << i) & combination) != 0 ? "X" : " ");
		}
		return combos;
	}

	/**
	 * Code to retrieve a description of matchcode components
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	public static String getComponentDescription(mdMUMatchcodeComponent component, boolean useLabel) {
		if (useLabel && !Const.isEmpty(component.GetLabel())) { return component.GetLabel(); }
		for (Component comp : COMPONENT_DESCRIPTIONS) {
			if (comp.type == component.GetComponentType()) { return comp.description; }
		}
		return "";
	}

	/**
	 * Code to retrieve descriptions of matchcode component fields
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	public static String getFieldMatchDescription(mdMUMatchcodeComponent component) {
		// Check for single enum
		try {
			mdMUMatchcodeComponent.MatchcodeFieldMatch fieldMatch = component.GetFieldMatch();
			if (fieldMatch == mdMUMatchcodeComponent.MatchcodeFieldMatch.BothBlankMatch) {
				return "Both Fields";
			} else if (fieldMatch == mdMUMatchcodeComponent.MatchcodeFieldMatch.OneBlankMatch) {
				return "One Field";
			} else if (fieldMatch == mdMUMatchcodeComponent.MatchcodeFieldMatch.InitialMatch) {
				return "Initial";
			} else if (fieldMatch == mdMUMatchcodeComponent.MatchcodeFieldMatch.NoFieldMatch) { return "None"; }
		} catch (IllegalArgumentException e) {
			// Could not be resolved to a single enum
		}
		// Need to handle cases of multiple field match return values
		// We do this by calling the JNI method directly
		long cPtr = mdMUMatchcodeComponent.getCPtr(component);
		int fieldMatch = mdMatchUpJavaWrapperJNI.mdMUMatchcodeComponent_GetFieldMatch(cPtr, component);
		StringBuffer sb = new StringBuffer("");
		if ((fieldMatch & mdMUMatchcodeComponent.MatchcodeFieldMatch.BothBlankMatch.swigValue()) != 0) {
			sb.append("Both/");
		}
		if ((fieldMatch & mdMUMatchcodeComponent.MatchcodeFieldMatch.OneBlankMatch.swigValue()) != 0) {
			sb.append("One/");
		}
		if ((fieldMatch & mdMUMatchcodeComponent.MatchcodeFieldMatch.InitialMatch.swigValue()) != 0) {
			sb.append("Initial/");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * Code to get a description of a fuzzy setting
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	public static String getFuzzyDescription(mdMUMatchcodeComponent component, boolean decorated) {
		for (Fuzzy fuzzy : FUZZY_DESCRIPTIONS) {
			if ((component.GetFuzzy() == fuzzy.type) && decorated && fuzzy.decorated) {
				return fuzzy.description + ": " + component.GetNear();
			} else if (component.GetFuzzy() == fuzzy.type) { return fuzzy.description; }
		}
		return "";
	}

	/**
	 * This method is called to get a text description of a matchcode mapping data type
	 *
	 * @param type
	 * @return
	 */
	public static String getInputMappingLabel(mdMUMatchcode.MatchcodeMapping type) {
		for (MappingDescription element : MAPPING_DESCRIPTIONS) {
			if (type == element.type) { return element.label; }
		}
		return "";
	}

	/**
	 * Called to get the dynamic length of the matchcode key
	 *
	 * @return
	 */
	public static int getMatchcodeKeyLen(String matchcode, String license, String dataPath) {
		mdMUHybrid Hybrid = null;
		try {
			try {
				Hybrid = DQTObjectFactory.newMatchupHybrid();
			} catch (DQTObjectException e) {
				// TODO: Better error handling
				e.printStackTrace(System.err);
				return -1;
			}
			Hybrid.SetLicenseString(license);
			Hybrid.SetPathToMatchUpFiles(dataPath);
			Hybrid.SetMatchcodeName(matchcode);
			if (Hybrid.InitializeDataFiles() == mdMUHybrid.ProgramStatus.ErrorNone) {
				return Hybrid.GetKeySize();
			} else {
				return -1;
			}
		} finally {
			if (Hybrid != null) {
				Hybrid.delete();
			}
		}
	}

	/**
	 * This method will create a single matchcode object for the given matchcode.
	 *
	 * @param matchcode
	 * @param dataPath
	 * @return
	 * @throws DQTObjectException
	 */
	public static mdMUMatchcode getMatchcodeObject(String matchcode, String dataPath, int[] rc) throws DQTObjectException {
		rc[0] = 0;
		mdMUMatchcode Matchcode = DQTObjectFactory.newMatchcode();
		Matchcode.SetPathToMatchUpFiles(dataPath);
		if (Matchcode.InitializeDataFiles() == mdMUMatchcode.ProgramStatus.ErrorNone) {
			rc[0] = Matchcode.FindMatchcode(matchcode);
		}
		return Matchcode;
	}

	/**
	 * Code to retrieve descriptions of matchcode component sizes
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	public static String getSizeDescription(mdMUMatchcodeComponent component) {
		if (component.GetWordCount() == 1) {
			return component.GetSize() + " (" + component.GetWordCount() + " word)";
		} else if (component.GetWordCount() != 0) {
			return component.GetSize() + " (" + component.GetWordCount() + " words)";
		} else {
			return component.GetSize() + "";
		}
	}

	/**
	 * Code to retrieve descriptions of matchcode component starts
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	public static String getStartDescription(mdMUMatchcodeComponent component) {
		if (component.GetStart() == mdMUMatchcodeComponent.MatchcodeStart.Left) {
			return "Left";
		} else if (component.GetStart() == mdMUMatchcodeComponent.MatchcodeStart.Right) {
			return "Right";
		} else if (component.GetStart() == mdMUMatchcodeComponent.MatchcodeStart.StartAtPos) {
			return "Position: " + component.GetStartPos();
		} else {
			return "Word: " + component.GetStartPos();
		}
	}

	/**
	 * Code to retrieve descriptions of matchcode component swap values
	 *
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	public static String getSwapDescription(mdMUMatchcodeComponent component) {
		// Call the JNI method directly to get the bit pattern
		long cPtr = mdMUMatchcodeComponent.getCPtr(component);
		int swap = mdMatchUpJavaWrapperJNI.mdMUMatchcodeComponent_GetSwap(cPtr, component);
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < SWAP_VALUES.length; i++) {
			if ((swap & SWAP_VALUES[i].swigValue()) != 0) {
				sb.append((char) ('A' + i)).append("/");
			}
		}
		if (sb.length() != 0) { return sb.substring(0, sb.length() - 1); }
		return "None";
	}

	/**
	 * Retrieves the hidden "swigValues" field
	 *
	 * @param eClass
	 */
	public static Object[] getSwigValues(Class<?> eClass) {
		try {
			Field field = eClass.getDeclaredField("swigValues");
			field.setAccessible(true);
			return (Object[]) field.get(null);
		} catch (RuntimeException e) {
			throw e;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks to see if there is any conversion possible between two data types
	 *
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean IsConvertable(mdMUMatchcode.MatchcodeMapping source, mdMUMatchcode.MatchcodeMappingTarget target) {
		for (AllowedMapping element : ALLOWED_MAPPINGS) {
			if (element.target == target) {
				for (MatchcodeMapping mapping : element.mappings) {
					if (mapping == source) { return true; }
				}
			}
		}
		return false;
	}

	/**
	 * Checks to see if there is a direct conversion possible between two data types
	 *
	 * @param
	 * @param
	 * @param target
	 * @return
	 */
	public static boolean IsDirectConversion(mdMUMatchcode.MatchcodeMapping source, mdMUMatchcode.MatchcodeMappingTarget target) {
		for (AllowedMapping element : ALLOWED_MAPPINGS) {
			if (element.target == target) { return (element.mappings[0] == source); }
		}
		return false;
	}

	public static void revertMCfile(String dataPath, boolean isEnterprise) {
		// if we are running community or lite we rewrite the .mc file so they
		// can only use the limited
		// choices of match codes. We allow them to get here so they can see
		// what extras the match code editor offers.
		if (!isEnterprise) {
			File copyOfmcFile = new File(dataPath + Const.FILE_SEPARATOR + "mdMatchupLite.mc");
			File originalMcFile = new File(dataPath + Const.FILE_SEPARATOR + "mdMatchup.mc");
			try {
				FileUtils.copyFile(copyOfmcFile, originalMcFile);
			} catch (IOException e) {
				System.out.println("IO error revert .mc file. " + e.getMessage());
			}
		}
	}

	/**
	 * Generic method to convert a string to a matchup object enum class
	 *
	 * @param eString
	 * @param eClass
	 * @return
	 */
	public static Object toEnum(String eString, Class<?> eClass) {
		Object[] swigValues = getSwigValues(eClass);
		for (Object swigValue : swigValues) {
			if (swigValue.toString().equals(eString)) { return swigValue; }
		}
		return null;
//
//
// Field[] declaredFields = eClass.getDeclaredFields();
// for (Field field : declaredFields) {
// field.setAccessible(true);
// if (field.getType() == eClass && field.getName().equals(eString)) {
// try {
// return field.get(null);
// } catch (IllegalArgumentException ignored) {
// } catch (IllegalAccessException ignored) {
// }
// }
// }
// return null;
	}

	/**
	 * Converts a data type name to its corresponding Hybrid matchcode mapping enum.
	 *
	 * @param dataType
	 * @return
	 */
	public static mdMUHybrid.MatchcodeMapping toHybridMatchcodeMapping(mdMUMatchcode.MatchcodeMapping dataType) {
		Class<?> matchcodeMappingClass = mdMUHybrid.MatchcodeMapping.class;
		return (mdMUHybrid.MatchcodeMapping) toMatchcodeMapping(dataType.toString(), matchcodeMappingClass);
	}

	/**
	 * Converts a string to a matchcode mapping enum
	 *
	 * @param type
	 * @return
	 */
	public static mdMUMatchcode.MatchcodeMapping toMatchcodeMappingEnum(String type) {
		mdMUMatchcode.MatchcodeMapping eType = (mdMUMatchcode.MatchcodeMapping) MatchUpUtil.toEnum(type, mdMUMatchcode.MatchcodeMapping.class);
		if (eType == null) {
			eType = mdMUMatchcode.MatchcodeMapping.General; // TODO: is there a more robust response to this?
		}
		return eType;
	}

	/**
	 * Converts a MatchCode type to its corresponding ReadWrite mapping enum.
	 *
	 * @param dataType
	 * @return
	 */
	public static mdMUReadWrite.MatchcodeMapping toReadWriteMatchcodeMapping(mdMUMatchcode.MatchcodeMapping dataType) {
		Class<?> matchcodeMappingClass = mdMUReadWrite.MatchcodeMapping.class;
		return (mdMUReadWrite.MatchcodeMapping) toMatchcodeMapping(dataType.toString(), matchcodeMappingClass);
	}

	/**
	 * This method uses reflection to convert a string to a matchcode mapping enum.
	 *
	 * @param dataType
	 * @param matchcodeMappingClass
	 * @return
	 */
	private static Object toMatchcodeMapping(String dataType, Class<?> matchcodeMappingClass) {
		Object[] swigValues = MatchUpUtil.getSwigValues(matchcodeMappingClass);
		for (Object swigValue : swigValues) {
			if (swigValue.toString().equals(dataType)) { return swigValue; }
		}
		return null;
	}
	private static final List<Component>						COMPONENT_DESCRIPTIONS	= new ArrayList<Component>();
	static {
		// TODO: I wish these descriptions were not hard-coded
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.PrefixComp, "Prefix"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.FirstComp, "First Name"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.MiddleComp, "Middle Name"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.LastComp, "Last Name"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.SuffixComp, "Suffix"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.GenderComp, "Gender"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.FirstNicknameComp, "First Nickname"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.MiddleNicknameComp, "Middle Nickname"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.TitleComp, "Title/Department"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.CompanyComp, "Company"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.CompanyAcronymComp, "Company Acronym"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.StreetNumberComp, "Street Number"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.StreetPreDirComp, "Street Pre-Directional"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.StreetNameComp, "Street Name"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.StreetSuffixComp, "Street Suffix"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.StreetPostDirComp, "Street Post-Directional"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.POBoxComp, "PO Box"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.SecondaryComp, "Secondary"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.AddressComp, "Address"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.CityComp, "City"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.StateComp, "State"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.Zip9Comp, "Zip9"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.Zip5Comp, "Zip5"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.Zip4Comp, "Zip4"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.CountryComp, "Country"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.CanadianPCComp, "Postal Code (Canada)"));
//		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.UKCityComp, "City (UK)"));
//		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.UKCountyComp, "County (UK)"));
//		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.UKPCComp, "Postcode (UK)"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.PhoneComp, "Phone/Fax"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.EMailComp, "E-Mail Address"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.CreditCardComp, "Credit Card Number"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.GeneralComp, "General"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.GeoDistanceComp, "Geo-Distance"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.NumericComp, "Numeric Difference"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DateComp, "Date Difference"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.PremisesNumberComp, "Premises Number"));

		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.ThoroughfarePreDirComp, "Thoroughfare Pre directional"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.ThoroughfareLeadingTypeComp, "Thoroughfare Leading Type"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.ThoroughfareNameComp, "Thoroughfare Name"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.ThoroughfarePostDirComp, "Thoroughfare Post directional"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.ThoroughfareTrailingTypeComp, "Thoroughfare Trailing Type"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DepThoroughfarePreDirComp, "Dependent Thoroughfare Pre directional"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DepThoroughfareLeadingTypeComp, "Dependent Thoroughfare Leading Type"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DepThoroughfareNameComp, "Dependent Thoroughfare Name"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DepThoroughfarePostDirComp, "Dependent Thoroughfare Post directional"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DepThoroughfareTrailingTypeComp, "Dependent Thoroughfare Trailing Type"));

		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.LocalityComp, "Locality"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DependentLocalityComp, "Dependent Locality"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.DblDependentLocalityComp, "Double Dependent Locality"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.AdministrativeAreaComp, "Administrative Area"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.SubAdministrativeAreaComp, "Sub Administrative Area"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.PostalCodeComp, "Postal Code"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.SubNationalAreaComp, "Sub National Area"));
		COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.PostBoxComp, "Post Box"));

		//	COMPONENT_DESCRIPTIONS.add(new Component(mdMUMatchcodeComponent.MatchcodeComponentType.Prem, "Date Difference"));

	}
	private static final List<Fuzzy>							FUZZY_DESCRIPTIONS		= new ArrayList<Fuzzy>();
	static {
		// TODO: I wish these descriptions were not hard-coded
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Exact, "Exact"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.SoundEx, "SoundEx"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Phonetex, "Phonetex"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Containment, "Containment"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Frequency, "Frequency"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.FastNear, "Fast Near", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.AccurateNear, "Accurate Near", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.VowelsOnly, "Vowels Only"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.ConsonantsOnly, "Consonants Only"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.AlphasOnly, "Alphas Only"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.NumericsOnly, "Numerics Only"));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.FrequencyNear, "Frequency Near", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.NGram, "N-Gram", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Jaro, "Jaro", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.JaroWinkler, "Jaro-Winkler", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.LCS, "LCS", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.NeedlemanWunsch, "Needleman-Wunsch", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.MDKeyboard, "MD-Keyboard", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.SmithWatermanGotoh, "Smith-Waterman-Gotoh", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Dice, "Dice's Coefficient", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Jaccard, "Jaccard Similarity", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.Overlap, "Overlap Coefficient", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.DoubleMetaphone, "Double Metaphone", true));
		FUZZY_DESCRIPTIONS.add(new Fuzzy(mdMUMatchcodeComponent.MatchcodeFuzzy.UTF8Near, "UTF8 Near", true));
	}
	/*
	 * Code to retrieve descriptions of matchcode component swap values
	 * Note: this code is borrowed from the SSIS Object interface
	 */
	private static final mdMUMatchcodeComponent.MatchcodeSwap	SWAP_VALUES[]			= new mdMUMatchcodeComponent.MatchcodeSwap[] { mdMUMatchcodeComponent.MatchcodeSwap.SwapA, mdMUMatchcodeComponent.MatchcodeSwap.SwapB,
		mdMUMatchcodeComponent.MatchcodeSwap.SwapC, mdMUMatchcodeComponent.MatchcodeSwap.SwapD, mdMUMatchcodeComponent.MatchcodeSwap.SwapE, mdMUMatchcodeComponent.MatchcodeSwap.SwapF, mdMUMatchcodeComponent.MatchcodeSwap.SwapG,
		mdMUMatchcodeComponent.MatchcodeSwap.SwapH,	mdMUMatchcodeComponent.MatchcodeSwap.BothA,mdMUMatchcodeComponent.MatchcodeSwap.BothB, mdMUMatchcodeComponent.MatchcodeSwap.BothC, mdMUMatchcodeComponent.MatchcodeSwap.BothD,
			mdMUMatchcodeComponent.MatchcodeSwap.BothE, mdMUMatchcodeComponent.MatchcodeSwap.BothF, mdMUMatchcodeComponent.MatchcodeSwap.BothG, mdMUMatchcodeComponent.MatchcodeSwap.BothH ,mdMUMatchcodeComponent.MatchcodeSwap.NoSwap};


	private static final AllowedMapping[]						ALLOWED_MAPPINGS		= new AllowedMapping[] {
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.PrefixType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Prefix, mdMUMatchcode.MatchcodeMapping.MixedFirst, mdMUMatchcode.MatchcodeMapping.FullName,
				mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.FirstType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.First, mdMUMatchcode.MatchcodeMapping.MixedFirst, mdMUMatchcode.MatchcodeMapping.FullName,
						mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.MiddleType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Middle, mdMUMatchcode.MatchcodeMapping.MixedFirst, mdMUMatchcode.MatchcodeMapping.FullName,
								mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.LastType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Last, mdMUMatchcode.MatchcodeMapping.MixedLast, mdMUMatchcode.MatchcodeMapping.FullName,
										mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.SuffixType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Suffix, mdMUMatchcode.MatchcodeMapping.MixedLast, mdMUMatchcode.MatchcodeMapping.FullName,
												mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.GenderType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Gender, mdMUMatchcode.MatchcodeMapping.Prefix, mdMUMatchcode.MatchcodeMapping.First,
														mdMUMatchcode.MatchcodeMapping.FullName, mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.FirstNicknameType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.First, mdMUMatchcode.MatchcodeMapping.MixedFirst, mdMUMatchcode.MatchcodeMapping.FullName,
																mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.MiddleNicknameType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Middle, mdMUMatchcode.MatchcodeMapping.MixedFirst, mdMUMatchcode.MatchcodeMapping.FullName,
																		mdMUMatchcode.MatchcodeMapping.InverseName, mdMUMatchcode.MatchcodeMapping.GovernmentInverseName }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.TitleType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Title }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.CompanyType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Company }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.CompanyAcronymType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Company }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.AddressType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.CityType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.City, mdMUMatchcode.MatchcodeMapping.CityStZip }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.StateType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.State, mdMUMatchcode.MatchcodeMapping.CityStZip }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Zip9Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Zip9, mdMUMatchcode.MatchcodeMapping.CityStZip }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Zip5Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Zip5, mdMUMatchcode.MatchcodeMapping.Zip9, mdMUMatchcode.MatchcodeMapping.CityStZip }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Zip4Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Zip4, mdMUMatchcode.MatchcodeMapping.Zip9, mdMUMatchcode.MatchcodeMapping.CityStZip }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.CountryType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Country }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.CanadianPCType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.CanadianPostalCode }),
//			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Prem, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.UKCity, mdMUMatchcode.MatchcodeMapping.UKCityCountyPC }),
//			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.UKCountyType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.UKCounty, mdMUMatchcode.MatchcodeMapping.UKCityCountyPC }),
//			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.UKPCType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.UKPostcode, mdMUMatchcode.MatchcodeMapping.UKCityCountyPC }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.PhoneType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Phone }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.EMailType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.EMail }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.CreditCardType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.CreditCard }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.GeneralType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.General }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address1Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address2Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address3Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.DateType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Date }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.NumericType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Numeric }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.LatitudeType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Latitude }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.LongitudeType, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Longitude }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address4Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address5Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address6Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address7Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.Address8Type, new mdMUMatchcode.MatchcodeMapping[] { mdMUMatchcode.MatchcodeMapping.Address }),
			//
			//new AllowedMapping(mdMUMatchcode.MatchcodeMappingTarget.CityType, new mdMUMatchcode.MatchcodeMapping[] { MatchcodeMapping.City, mdMUMatchcode.MatchcodeMapping.CityStZip }),
			};

	private static final MappingDescription[]					MAPPING_DESCRIPTIONS	= new MappingDescription[] { new MappingDescription(mdMUMatchcode.MatchcodeMapping.Prefix, "Prefix"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Gender, "Gender"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.First, "First Name"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.MixedFirst, "Mixed First"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Middle, "Middle Name"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Last, "Last Name"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.MixedLast, "Mixed Last"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Suffix, "Suffix"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.FullName, "Full Name"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.InverseName, "Inverse Name"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.GovernmentInverseName, "Government Name"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Title, "Title"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Company, "Company"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Address, "Address Line"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.City, "City"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.State, "State/Postal Code"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Zip9, "Zip and +4"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Zip5, "Zip (no +4)"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Zip4, "+4 (without Zip)"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.CityStZip, "City/State/Zip"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Country, "Country"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.CanadianPostalCode, "Postal Code (Canada)"),
//			new MappingDescription(mdMUMatchcode.MatchcodeMapping.UKCity, "City (UK)"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.UKCounty, "County (UK)"),
//			new MappingDescription(mdMUMatchcode.MatchcodeMapping.UKPostcode, "Postcode (UK)"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.UKCityCountyPC, "City/County/PC (UK)"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Phone, "Phone/Fax"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.EMail, "E-Mail Address"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.CreditCard, "Credit Card Number"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.General, "General"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Date, "Date"),
			new MappingDescription(mdMUMatchcode.MatchcodeMapping.Numeric, "Numeric"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Latitude, "Location_La"), new MappingDescription(mdMUMatchcode.MatchcodeMapping.Longitude, "Location_Lo"), };
}
