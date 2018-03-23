package com.melissadata.kettle.propertywebservice.data;

import java.util.HashMap;

import com.melissadata.cz.support.MetaVal;

public class PropertyWebServiceFields {

	public static final String TAG_PROPERTY_WEB_SERVICE_OPTIONS                   = "property_web_service_options";
	public static final String TAG_PROPERTY_WEB_SERVICE_INPUT                     = "property_web_service_input";
	public static final String TAG_PROPERTY_WEB_SERVICE_OUTPUT                    = "property_web_service_output";
	// include options
	public static       String TAG_OPTION_INCLUDE_PROPERTY_ADDRESS                = "option_property_address";
	public static       String TAG_OPTION_INCLUDE_PARSED_PROPERTY_ADDRESS         = "option_parsed_property_address";
	public static       String TAG_OPTION_INCLUDE_PARCEL                          = "option_parcel";
	public static       String TAG_OPTION_INCLUDE_LEGAL                           = "option_legal";
	public static       String TAG_OPTION_INCLUDE_PRIMARY_OWNER                   = "option_primary_owner";
	public static       String TAG_OPTION_INCLUDE_SECONDARY_OWNER                 = "option_secondary_owner";
	public static       String TAG_OPTION_INCLUDE_OWNER_ADDRESS                   = "option_owner_address";
	public static       String TAG_OPTION_INCLUDE_LAST_DEED_OWNER                 = "option_last_deed_owner";
	public static       String TAG_OPTION_INCLUDE_CURRENT_DEED                    = "option_current_deed";
	public static       String TAG_OPTION_INCLUDE_TAX                             = "option_tax";
	public static       String TAG_OPTION_INCLUDE_ESTIMATED_VALUE                 = "option_estimated_value";
	public static       String TAG_OPTION_INCLUDE_PROPERTY_USE_INFO               = "option_proerty_use_info";
	public static       String TAG_OPTION_INCLUDE_SALE_INFO                       = "option_sale_info";
	public static       String TAG_OPTION_INCLUDE_PROPERTY_SIZE                   = "option_property_size";
	public static       String TAG_OPTION_INCLUDE_POOL                            = "option_pool";
	public static       String TAG_OPTION_INCLUDE_INT_STRUCT_INFO                 = "option_int_struct_info";
	public static       String TAG_OPTION_INCLUDE_INT_ROOM_INFO                   = "option_int_room_info";
	public static       String TAG_OPTION_INCLUDE_INT_AMENITIES                   = "option_int_amenities";
	public static       String TAG_OPTION_INCLUDE_EXT_STRUCT_INFO                 = "option_ext_struct_info";
	public static       String TAG_OPTION_INCLUDE_EXT_AMENITIES                   = "option_ext_amenities";
	public static       String TAG_OPTION_INCLUDE_EXT_BUILDINGS                   = "option_ext_buildings";
	public static       String TAG_OPTION_INCLUDE_UTILITIES                       = "option_utilities";
	public static       String TAG_OPTION_INCLUDE_PARKING                         = "option_parking";
	public static       String TAG_OPTION_INCLUDE_YARD_GARDEN_INFO                = "option_yard_garden_info";
	public static       String TAG_OPTION_INCLUDE_SHAPE                           = "option_shape";
	// INPUT FIELDS
	public static       String TAG_INPUT_ADDRESS_KEY                              = "input_address_key";
	public static       String TAG_INPUT_APN                                      = "input_apn";
	public static       String TAG_INPUT_FIPS                                     = "input_fips";
	public static       String TAG_INPUT_FREE_FORM                                = "input_free_form";
	// OUTPUT FIELDS
	public static       String TAG_OUTPUT_RESULTS                                 = "output_results";
	// Parcel ***
	public static       String TAG_OUTPUT_PARCEL_PREFIX                           = "output_parcel_";
	public static       String TAG_OUTPUT_FIPSCODE                                = TAG_OUTPUT_PARCEL_PREFIX + "fips_code";
	public static       String TAG_OUTPUT_COUNTY                                  = TAG_OUTPUT_PARCEL_PREFIX + "County";
	public static       String TAG_OUTPUT_UNFORMATTEDAPN                          = TAG_OUTPUT_PARCEL_PREFIX + "unformatted_apn";
	public static       String TAG_OUTPUT_FORMATTEDAPN                            = TAG_OUTPUT_PARCEL_PREFIX + "formatted_apn";
	public static       String TAG_OUTPUT_ALTERNATEAPN                            = TAG_OUTPUT_PARCEL_PREFIX + "alternate_apn";
	public static       String TAG_OUTPUT_APN_YEAR_CHANGE                         = TAG_OUTPUT_PARCEL_PREFIX + "apn_year_change";
	public static       String TAG_OUTPUT_PREVIOUS_APN                            = TAG_OUTPUT_PARCEL_PREFIX + "previous_apn";
	public static       String TAG_OUTPUT_ACCOUNT_NUMBER                          = TAG_OUTPUT_PARCEL_PREFIX + "account_number";
	public static       String TAG_OUTPUT_YEAR_ADDED                              = TAG_OUTPUT_PARCEL_PREFIX + "year_added";
	public static       String TAG_OUTPUT_MAP_BOOK                                = TAG_OUTPUT_PARCEL_PREFIX + "map_book";
	public static       String TAG_OUTPUT_MAP_PAGE                                = TAG_OUTPUT_PARCEL_PREFIX + "map_page";
	// Legal
	public static       String TAG_OUTPUT_LEGAL_PREFIX                            = "output_legal_";
	public static       String TAG_OUTPUT_LEGAL_DESCRIPTION                       = TAG_OUTPUT_LEGAL_PREFIX + "legal_description";
	public static       String TAG_OUTPUT_RANGE                                   = TAG_OUTPUT_LEGAL_PREFIX + "range";
	public static       String TAG_OUTPUT_TOWNSHIP                                = TAG_OUTPUT_LEGAL_PREFIX + "township";
	public static       String TAG_OUTPUT_SECTION                                 = TAG_OUTPUT_LEGAL_PREFIX + "Section";
	public static       String TAG_OUTPUT_QUARTER                                 = TAG_OUTPUT_LEGAL_PREFIX + "quarter";
	public static       String TAG_OUTPUT_QUARTER_QUATER                          = TAG_OUTPUT_LEGAL_PREFIX + "quarter_quarter";
	public static       String TAG_OUTPUT_SUBDIVISION                             = TAG_OUTPUT_LEGAL_PREFIX + "subdivision";
	public static       String TAG_OUTPUT_PHASE                                   = TAG_OUTPUT_LEGAL_PREFIX + "phase";
	public static       String TAG_OUTPUT_TRACT_NUMBER                            = TAG_OUTPUT_LEGAL_PREFIX + "tract_number";
	public static       String TAG_OUTPUT_BLOCK_1                                 = TAG_OUTPUT_LEGAL_PREFIX + "block_1";
	public static       String TAG_OUTPUT_BLOCK_2                                 = TAG_OUTPUT_LEGAL_PREFIX + "block_2";
	public static       String TAG_OUTPUT_LOT_NUMBER_1                            = TAG_OUTPUT_LEGAL_PREFIX + "lot_number_1";
	public static       String TAG_OUTPUT_LOT_NUMBER_2                            = TAG_OUTPUT_LEGAL_PREFIX + "lot_number_2";
	public static       String TAG_OUTPUT_LOT_NUMBER_3                            = TAG_OUTPUT_LEGAL_PREFIX + "lot_number_3";
	public static       String TAG_OUTPUT_UNIT                                    = TAG_OUTPUT_LEGAL_PREFIX + "unit";
	// Property Address  ***
	public static       String TAG_OUTPUT_PROPERTY_PREFIX                         = "output_property_";
	public static       String TAG_OUTPUT_PROPERTY_ADDRESS                        = TAG_OUTPUT_PROPERTY_PREFIX + "property_address";
	public static       String TAG_OUTPUT_PROPERTY_CITY                           = TAG_OUTPUT_PROPERTY_PREFIX + "property_city";
	public static       String TAG_OUTPUT_PROPERTY_STATE                          = TAG_OUTPUT_PROPERTY_PREFIX + "property_state";
	public static       String TAG_OUTPUT_PROPERTY_ZIP                            = TAG_OUTPUT_PROPERTY_PREFIX + "property_zip";
	public static       String TAG_OUTPUT_PROPERTY_ADDRESSKEY                     = TAG_OUTPUT_PROPERTY_PREFIX + "property_address_key";
	public static       String TAG_OUTPUT_PROPERTY_MAK                            = TAG_OUTPUT_PROPERTY_PREFIX + "property_MAK";
	public static       String TAG_OUTPUT_PROPERTY_BASE_MAK                       = TAG_OUTPUT_PROPERTY_PREFIX + "property_BaseMAK";
	public static       String TAG_OUTPUT_PROPERTY_LATITUDE                       = TAG_OUTPUT_PROPERTY_PREFIX + "property_latitude";
	public static       String TAG_OUTPUT_PROPERTY_LONGITUDE                      = TAG_OUTPUT_PROPERTY_PREFIX + "property_longitude";
	// Parsed Property Address
	public static       String TAG_OUTPUT_PARSED_PROPERTY_PREFIX                  = "output_parsed_property_";
	public static       String TAG_OUTPUT_PARSED_RANGE                            = TAG_OUTPUT_PARSED_PROPERTY_PREFIX + "parsed_range";
	public static       String TAG_OUTPUT_PARSED_PREDIRECTIONAL                   = TAG_OUTPUT_PARSED_PROPERTY_PREFIX + "pre_directional";
	public static       String TAG_OUTPUT_PARSED_STREETNAME                       = TAG_OUTPUT_PARSED_PROPERTY_PREFIX + "street_name";
	public static       String TAG_OUTPUT_PARSED_SUFFIX                           = TAG_OUTPUT_PARSED_PROPERTY_PREFIX + "suffix";
	public static       String TAG_OUTPUT_PARSED_POSTDIRECTIONAL                  = TAG_OUTPUT_PARSED_PROPERTY_PREFIX + "post-directional";
	public static       String TAG_OUTPUT_PARSED_SUITERANGE                       = TAG_OUTPUT_PARSED_PROPERTY_PREFIX + "suite_range";
	public static       String TAG_OUTPUT_PARSED_SUITENAME                        = TAG_OUTPUT_PARSED_PROPERTY_PREFIX + "suite_name";
	// Primary Owner
	public static       String TAG_OUTPUT_PRIMARY_OWNER_PREFIX                    = "output_primary_owner_";
	public static       String TAG_OUTPUT_PRIMARY_NAME_1_FULL                     = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name1_full";
	public static       String TAG_OUTPUT_PRIMARY_NAME_1_FIRST                    = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name1_first";
	public static       String TAG_OUTPUT_PRIMARY_NAME_1_MIDDLE                   = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name1_middle";
	public static       String TAG_OUTPUT_PRIMARY_NAME_1_LAST                     = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name1_last";
	public static       String TAG_OUTPUT_PRIMARY_NAME_1_SUFFIX                   = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name1_suffix";
	public static       String TAG_OUTPUT_PRIMARY_TRUST_FLAG                      = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_trust_flag";
	public static       String TAG_OUTPUT_PRIMARY_COMPANY_FLAG                    = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_company_flag";
	public static       String TAG_OUTPUT_PRIMARY_NAME_2_FULL                     = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name2_full";
	public static       String TAG_OUTPUT_PRIMARY_NAME_2_FIRST                    = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name2_first";
	public static       String TAG_OUTPUT_PRIMARY_NAME_2_MIDDLE                   = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name2_middle";
	public static       String TAG_OUTPUT_PRIMARY_NAME_2_LAST                     = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name2_last";
	public static       String TAG_OUTPUT_PRIMARY_NAME_2_SUFFIX                   = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_name2_suffix";
	public static       String TAG_OUTPUT_PRIMARY_TYPE                            = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_type";
	public static       String TAG_OUTPUT_PRIMARY_VESTING_TYPE                    = TAG_OUTPUT_PRIMARY_OWNER_PREFIX + "primary_vesting_type";
	// Secondary Owner
	public static       String TAG_OUTPUT_SECONDARY_OWNER_PREFIX                  = "output_secondary_owner_";
	public static       String TAG_OUTPUT_SECONDARY_NAME_3_FULL                   = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name3_full";
	public static       String TAG_OUTPUT_SECONDARY_NAME_3_FIRST                  = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name3_first";
	public static       String TAG_OUTPUT_SECONDARY_NAME_3_MIDDLE                 = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name3_middle";
	public static       String TAG_OUTPUT_SECONDARY_NAME_3_LAST                   = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name3_last";
	public static       String TAG_OUTPUT_SECONDARY_NAME_3_SUFFIX                 = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name3_suffix";
	public static       String TAG_OUTPUT_SECONDARY_NAME_4_FULL                   = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name4_full";
	public static       String TAG_OUTPUT_SECONDARY_NAME_4_FIRST                  = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name4_first";
	public static       String TAG_OUTPUT_SECONDARY_NAME_4_MIDDLE                 = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name4_middle";
	public static       String TAG_OUTPUT_SECONDARY_NAME_4_LAST                   = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name4_last";
	public static       String TAG_OUTPUT_SECONDARY_NAME_4_SUFFIX                 = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_name4_suffix";
	public static       String TAG_OUTPUT_SECONDARY_TYPE                          = TAG_OUTPUT_SECONDARY_OWNER_PREFIX + "secondary_type";
	// Owner Addres ****
	public static       String TAG_OUTPUT_OWNER_ADDRESS_PREFIX                    = "output_address_owner_";
	public static       String TAG_OUTPUT_OWNER_ADDRESS                           = TAG_OUTPUT_OWNER_ADDRESS_PREFIX + "address";
	public static       String TAG_OUTPUT_OWNER_CITY                              = TAG_OUTPUT_OWNER_ADDRESS_PREFIX + "city";
	public static       String TAG_OUTPUT_OWNER_STATE                             = TAG_OUTPUT_OWNER_ADDRESS_PREFIX + "state";
	public static       String TAG_OUTPUT_OWNER_ZIP                               = TAG_OUTPUT_OWNER_ADDRESS_PREFIX + "zip";
	public static       String TAG_OUTPUT_OWNER_CARRIERROUTE                      = TAG_OUTPUT_OWNER_ADDRESS_PREFIX + "carrier_route";
	public static       String TAG_OUTPUT_OWNER_MAK                               = TAG_OUTPUT_OWNER_ADDRESS_PREFIX + "mak";
	public static       String TAG_OUTPUT_OWNER_BASE_MAK                          = TAG_OUTPUT_OWNER_ADDRESS_PREFIX + "base_mak";
	// Last Deed Owner
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_PREFIX                  = "output_last_deed_owner_";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FULL             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name1_full";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FIRST            = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name1_first";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_MIDDLE           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name1_middle";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_LAST             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name1_last";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_SUFFIX           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name1_suffix";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FULL             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name2_full";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FIRST            = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name2_first";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_MIDDLE           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name2_middle";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_LAST             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name2_last";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_SUFFIX           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name2_suffix";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FULL             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name3_full";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FIRST            = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name3_first";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_MIDDLE           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name3_middle";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_LAST             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name3_last";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_SUFFIX           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name3_suffix";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FULL             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name4_full";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FIRST            = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name4_first";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_MIDDLE           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name4_middle";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_LAST             = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name4_last";
	public static       String TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_SUFFIX           = TAG_OUTPUT_LAST_DEED_OWNER_PREFIX + "last_deed_name4_suffix";
	// Current Deed
	public static       String TAG_OUTPUT_CURRENT_DEED_PREFIX                     = "output_current_deed_";
	public static       String TAG_OUTPUT_MORTGAGE_AMOUNT                         = TAG_OUTPUT_CURRENT_DEED_PREFIX + "mortgage_amount";
	public static       String TAG_OUTPUT_MORTGAGE_DATE                           = TAG_OUTPUT_CURRENT_DEED_PREFIX + "mortgage_date";
	public static       String TAG_OUTPUT_MORTGAGE_LOAN_TYPE_CODE                 = TAG_OUTPUT_CURRENT_DEED_PREFIX + "mortgage_loan_type_code";
	public static       String TAG_OUTPUT_MORTGAGE_TERM_CODE                      = TAG_OUTPUT_CURRENT_DEED_PREFIX + "mortgage_term_code";
	public static       String TAG_OUTPUT_MORTGAGE_TERM                           = TAG_OUTPUT_CURRENT_DEED_PREFIX + "mortgage_term";
	public static       String TAG_OUTPUT_MORTGAGE_DUE_DATE                       = TAG_OUTPUT_CURRENT_DEED_PREFIX + "mortgage_due_date";
	public static       String TAG_OUTPUT_LENDER_CODE                             = TAG_OUTPUT_CURRENT_DEED_PREFIX + "lender_code";
	public static       String TAG_OUTPUT_LENDER_NAME                             = TAG_OUTPUT_CURRENT_DEED_PREFIX + "lender_name";
	public static       String TAG_OUTPUT_SECOND_MORTGAGE_AMOUNT                  = TAG_OUTPUT_CURRENT_DEED_PREFIX + "second_mortgage_amount";
	public static       String TAG_OUTPUT_SECOND_MORTGAGE_LOAN_TYPE_CODE          = TAG_OUTPUT_CURRENT_DEED_PREFIX + "second_mortgage_loan_type_code";
	// Tax
	public static       String TAG_OUTPUT_TAX_PREFIX                              = "output_tax_";
	public static       String TAG_OUTPUT_YEAR_ASSESSED                           = TAG_OUTPUT_TAX_PREFIX + "YearAssessed";
	public static       String TAG_OUTPUT_ASSESSED_VALUE_TOTAL                    = TAG_OUTPUT_TAX_PREFIX + "AssessedValueTotal";
	public static       String TAG_OUTPUT_ASSESSED_VALUE_IMPROVEMENTS             = TAG_OUTPUT_TAX_PREFIX + "AssessedValueImprovements";
	public static       String TAG_OUTPUT_ASSESSED_VALUE_LAND                     = TAG_OUTPUT_TAX_PREFIX + "AssessedValueLand";
	public static       String TAG_OUTPUT_ASSESSED_IMPROVEMENTS_PERC              = TAG_OUTPUT_TAX_PREFIX + "AssessedImprovementsPerc";
	public static       String TAG_OUTPUT_PREVIOUS_ASSESSED_VALUE                 = TAG_OUTPUT_TAX_PREFIX + "PreviousAssessedValue";
	public static       String TAG_OUTPUT_MARKET_VALUE_YEAR                       = TAG_OUTPUT_TAX_PREFIX + "MarketValueYear";
	public static       String TAG_OUTPUT_MARKET_VALUE_TOTAL                      = TAG_OUTPUT_TAX_PREFIX + "MarketValueTotal";
	public static       String TAG_OUTPUT_MARKET_VALUE_IMPROVEMENTS               = TAG_OUTPUT_TAX_PREFIX + "MarketValueImprovements";
	public static       String TAG_OUTPUT_MARKET_VALUE_LAND                       = TAG_OUTPUT_TAX_PREFIX + "MarketValueLand";
	public static       String TAG_OUTPUT_MARKET_IMPROVEMENT_PREC                 = TAG_OUTPUT_TAX_PREFIX + "MarketImprovementsPerc";
	public static       String TAG_OUTPUT_TAX_FISCAL_YEAR                         = TAG_OUTPUT_TAX_PREFIX + "TaxFiscalYear";
	public static       String TAG_OUTPUT_TAX_RATE_AREA                           = TAG_OUTPUT_TAX_PREFIX + "TaxRateArea";
	public static       String TAG_OUTPUT_TAX_BILL_AMOUNT                         = TAG_OUTPUT_TAX_PREFIX + "TaxBilledAmount";
	public static       String TAG_OUTPUT_TAX_DELINQUENT_YEAR                     = TAG_OUTPUT_TAX_PREFIX + "TaxDelinquentYear";
	public static       String TAG_OUTPUT_LAST_TAX_ROLL_UPDATE                    = TAG_OUTPUT_TAX_PREFIX + "LastTaxRollUpdate";
	public static       String TAG_OUTPUT_ASSR_LAST_UPDATED                       = TAG_OUTPUT_TAX_PREFIX + "AssrLastUpdated";
	public static       String TAG_OUTPUT_TAX_EXEMPTION_HOMEOWNER                 = TAG_OUTPUT_TAX_PREFIX + "TaxExemptionHomeowner";
	public static       String TAG_OUTPUT_TAX_EXEMPTION_DISABLED                  = TAG_OUTPUT_TAX_PREFIX + "TaxExemptionDisabled";
	public static       String TAG_OUTPUT_TAX_EXEMPTION_SENIOR                    = TAG_OUTPUT_TAX_PREFIX + "TaxExemptionSenior";
	public static       String TAG_OUTPUT_TAX_EXEMPTION_VETERAN                   = TAG_OUTPUT_TAX_PREFIX + "TaxExemptionVeteran";
	public static       String TAG_OUTPUT_TAX_EXEMPTION_WIDOW                     = TAG_OUTPUT_TAX_PREFIX + "TaxExemptionWidow";
	public static       String TAG_OUTPUT_TAX_EXEMPTION_ADDITIONAL                = TAG_OUTPUT_TAX_PREFIX + "TaxExemptionAdditional";
	// PropertyUseInfo
	public static       String TAG_OUTPUT_PROPERTY_USE_PREFIX                     = "output_use_property";
	public static       String TAG_OUTPUT_YEAR_BUILT                              = TAG_OUTPUT_PROPERTY_USE_PREFIX + "YearBuilt";
	public static       String TAG_OUTPUT_YEAR_BUILT_EFFECTIVE                    = TAG_OUTPUT_PROPERTY_USE_PREFIX + "YearBuiltEffective";
	public static       String TAG_OUTPUT_ZONED_CODE_LOCAL                        = TAG_OUTPUT_PROPERTY_USE_PREFIX + "ZonedCodeLocal";
	public static       String TAG_OUTPUT_PROPERTY_USE_MUNI                       = TAG_OUTPUT_PROPERTY_USE_PREFIX + "PropertyUseMuni";
	public static       String TAG_OUTPUT_PROPERTY_USE_GROUP                      = TAG_OUTPUT_PROPERTY_USE_PREFIX + "PropertyUseGroup";
	public static       String TAG_OUTPUT_PROPERTY_USE_STANDARDIZED               = TAG_OUTPUT_PROPERTY_USE_PREFIX + "PropertyUseStandardized";
	// SaleInfo
	public static       String TAG_OUTPUT_SALE_INFO_PREFIX                        = "output_sales_info_";
	public static       String TAG_OUTPUT_ASSESSORS_LAST_SALE_DATE                = TAG_OUTPUT_SALE_INFO_PREFIX + "AssessorLastSaleDate";
	public static       String TAG_OUTPUT_ASSESSORS_LAST_SALE_AMMOUNT             = TAG_OUTPUT_SALE_INFO_PREFIX + "AssessorLastSaleAmount";
	public static       String TAG_OUTPUT_ASSESSORS_PRIOR_SALE_DATE               = TAG_OUTPUT_SALE_INFO_PREFIX + "AssessorPriorSaleDate";
	public static       String TAG_OUTPUT_ASSESSORS_PRIOR_SALE_AMOUNT             = TAG_OUTPUT_SALE_INFO_PREFIX + "AssessorPriorSaleAmount";
	public static       String TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DATE            = TAG_OUTPUT_SALE_INFO_PREFIX + "LastOwnershipTransferDate";
	public static       String TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DOCUMENT_NUMBER = TAG_OUTPUT_SALE_INFO_PREFIX + "LastOwnershipTransferDocumentNumber";
	public static       String TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_TAX_ID          = TAG_OUTPUT_SALE_INFO_PREFIX + "LastOwnershipTransferTxID";
	public static       String TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_BOOK            = TAG_OUTPUT_SALE_INFO_PREFIX + "DeedLastSaleDocumentBook";
	public static       String TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_PAGE            = TAG_OUTPUT_SALE_INFO_PREFIX + "DeedLastSaleDocumentPage";
	public static       String TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_NUMBER          = TAG_OUTPUT_SALE_INFO_PREFIX + "DeedLastDocumentNumber";
	public static       String TAG_OUTPUT_DEED_LAST_SALE_DATE                     = TAG_OUTPUT_SALE_INFO_PREFIX + "DeedLastSaleDate";
	public static       String TAG_OUTPUT_DEED_LAST_SALE_PRICE                    = TAG_OUTPUT_SALE_INFO_PREFIX + "DeedLastSalePrice";
	public static       String TAG_OUTPUT_DEED_LAST_SALE_TAX_ID                   = TAG_OUTPUT_SALE_INFO_PREFIX + "DeedLastSaleTxID";
	// PropertySize
	public static       String TAG_OUTPUT_PROPERTY_SIZE_PREFIX                    = "output_size_property";
	public static       String TAG_OUTPUT_AREA_BUILDING                           = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AreaBuilding";
	public static       String TAG_OUTPUT_AREA_BUILDING_DEFINITION_CODE           = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AreaBuildingDefinitionCode";
	public static       String TAG_OUTPUT_AREA_GROSS                              = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AreaGross";
	public static       String TAG_OUTPUT_AREA_1ST_FLOOR                          = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "Area1stFloor";
	public static       String TAG_OUTPUT_AREA_2ND_FLOOR                          = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "Area2ndFloor";
	public static       String TAG_OUTPUT_AREA_UPPER_FLOORS                       = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AreaUpperFloors";
	public static       String TAG_OUTPUT_AREA_LOT_ACRES                          = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AreaLotAcres";
	public static       String TAG_OUTPUT_AREA_LOT_SF                             = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AreaLotSF";
	public static       String TAG_OUTPUT_LOT_DEPTH                               = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "LotDepth";
	public static       String TAG_OUTPUT_LOT_WIDTH                               = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "LotWidth";
	public static       String TAG_OUTPUT_ATTIC_AREA                              = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AtticArea";
	public static       String TAG_OUTPUT_ATTIC_FLAG                              = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "AtticFlag";
	public static       String TAG_OUTPUT_BASEMENT_AREA                           = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "BasementArea";
	public static       String TAG_OUTPUT_BASEMENT_AREA_FINISHED                  = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "BasementAreaFinished";
	public static       String TAG_OUTPUT_BASEMENT_AREA_UNFINISHED                = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "BasementAreaUnfinished";
	public static       String TAG_OUTPUT_PARKING_GARAGE                          = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "ParkingGarage";
	public static       String TAG_OUTPUT_PARKING_GARAGE_AREA                     = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "ParkingGarageArea";
	public static       String TAG_OUTPUT_PARKING_CARPORT                         = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "ParkingCarport";
	public static       String TAG_OUTPUT_PARKING_CARPORT_AREA                    = TAG_OUTPUT_PROPERTY_SIZE_PREFIX + "ParkingCarportArea";
	// Pool
	public static       String TAG_OUTPUT_POOL_PREFIX                             = "output_pool_";
	public static       String TAG_OUTPUT_POOL                                    = TAG_OUTPUT_POOL_PREFIX + "Pool";
	public static       String TAG_OUTPUT_POOL_AREA                               = TAG_OUTPUT_POOL_PREFIX + "PoolArea";
	public static       String TAG_OUTPUT_SAUNA_FLAG                              = TAG_OUTPUT_POOL_PREFIX + "SaunaFlag";
	// IntStructInfo
	public static       String TAG_OUTPUT_INTER_STRUCT_PREFIX                     = "output_inter_struct_";
	public static       String TAG_OUTPUT_FOUNDATION                              = TAG_OUTPUT_INTER_STRUCT_PREFIX + "Foundation";
	public static       String TAG_OUTPUT_CONSTRUCTION                            = TAG_OUTPUT_INTER_STRUCT_PREFIX + "Construction";
	public static       String TAG_OUTPUT_INTERIOR_STRUCTURE                      = TAG_OUTPUT_INTER_STRUCT_PREFIX + "InteriorStructure";
	public static       String TAG_OUTPUT_PLUMBING_FIXTURE                        = TAG_OUTPUT_INTER_STRUCT_PREFIX + "PlumbingFixturesCount";
	public static       String TAG_OUTPUT_CONSTRUCT_FIRE_RESISTANCE_CLASS         = TAG_OUTPUT_INTER_STRUCT_PREFIX + "ConstructionFireResistanceClass";
	public static       String TAG_OUTPUT_SAFETY_FIRE_SPRINKLER_FLAG              = TAG_OUTPUT_INTER_STRUCT_PREFIX + "SafetyFireSprinklersFlag";
	public static       String TAG_OUTPUT_FLOORING_MATERIAL_PRIMARY               = TAG_OUTPUT_INTER_STRUCT_PREFIX + "FlooringMaterialPrimary";
	// IntRoomInfo
	public static       String TAG_OUTPUT_INTER_ROOM_PREFIX                       = "output_inter_room_";
	public static       String TAG_OUTPUT_BATH_COUNT                              = TAG_OUTPUT_INTER_ROOM_PREFIX + "BathCount";
	public static       String TAG_OUTPUT_BATH_PARTIAL_COUNT                      = TAG_OUTPUT_INTER_ROOM_PREFIX + "BathPartialCount";
	public static       String TAG_OUTPUT_BEDROOMS_COUNT                          = TAG_OUTPUT_INTER_ROOM_PREFIX + "BedroomsCount";
	public static       String TAG_OUTPUT_ROOMS_COUNT                             = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsCount";
	public static       String TAG_OUTPUT_STORIES_COUNT                           = TAG_OUTPUT_INTER_ROOM_PREFIX + "StoriesCount";
	public static       String TAG_OUTPUT_UNITS_COUNT                             = TAG_OUTPUT_INTER_ROOM_PREFIX + "UnitsCount";
	public static       String TAG_OUTPUT_BONUS_ROOM_FLAG                         = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsBonusRoomFlag";
	public static       String TAG_OUTPUT_BREAKFAST_NOOK_FLAG                     = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsBreakfastNookFlag";
	public static       String TAG_OUTPUT_CELLAR_FLAG                             = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsCellarFlag";
	public static       String TAG_OUTPUT_WINE_CELLAR_FLAG                        = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsCellarWineFlag";
	public static       String TAG_OUTPUT_EXERCISE_ROOM_FLAG                      = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsExcerciseFlag";
	public static       String TAG_OUTPUT_FAMILY_ROOM_FLAG                        = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsFamilyCode";
	public static       String TAG_OUTPUT_GAME_ROOM_FLAG                          = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsGameFlag";
	public static       String TAG_OUTPUT_GREAT_ROOM_FLAG                         = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsGreatFlag";
	public static       String TAG_OUTPUT_HOBBY_ROOM_FLAG                         = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsHobbyFlag";
	public static       String TAG_OUTPUT_LAUNDRY_ROOM_FLAG                       = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsLaundryFlag";
	public static       String TAG_OUTPUT_MEDIA_ROOM_FLAG                         = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsMediaFlag";
	public static       String TAG_OUTPUT_MUD_ROOM_FLAG                           = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsMudFlag";
	public static       String TAG_OUTPUT_OFFICE_AREA                             = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsOfficeArea";
	public static       String TAG_OUTPUT_OFFICE_ROOM_FLAG                        = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsOfficeFlag";
	public static       String TAG_OUTPUT_SAFE_ROOM_FLAG                          = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsSafeRoomFlag";
	public static       String TAG_OUTPUT_SITTING_ROOM_FLAG                       = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsSittingFlag";
	public static       String TAG_OUTPUT_STORM_SHELTER                           = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsStormShelter";
	public static       String TAG_OUTPUT_STUDY_ROOM_FLAG                         = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsStudyFlag";
	public static       String TAG_OUTPUT_SUN_ROOM_FLAG                           = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsSunroomFlag";
	public static       String TAG_OUTPUT_UTILITY_ROOM_AREA                       = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsUtilityArea";
	public static       String TAG_OUTPUT_UTILITY_ROOM_CODE                       = TAG_OUTPUT_INTER_ROOM_PREFIX + "RoomsUtilityCode";
	// IntAmmenities
	public static       String TAG_OUTPUT_INT_AMMENITIES_PREFIX                   = "output_int_ammenities_";
	public static       String TAG_OUTPUT_FIREPLACE                               = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "Fireplace";
	public static       String TAG_OUTPUT_FIREPLACE_COUNT                         = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "FireplaceCount";
	public static       String TAG_OUTPUT_ELEVATOR_FLAG                           = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "AccessabilityElevatorFlag";
	public static       String TAG_OUTPUT_HANDICAP_FLAG                           = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "AccessabilityHandicapFlag";
	public static       String TAG_OUTPUT_ESCALATOR_FLAG                          = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "EscalatorFlag";
	public static       String TAG_OUTPUT_CENTRAL_VACUUM_FLAG                     = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "CentralVacuumFlag";
	public static       String TAG_OUTPUT_INTERCOM_FLAG                           = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "IntercomFlag";
	public static       String TAG_OUTPUT_SOUND_SYSTEM_FLAG                       = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "SoundSystemFlag";
	public static       String TAG_OUTPUT_WET_BAR_FLAG                            = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "WetBarFlag";
	public static       String TAG_OUTPUT_SECURITY_ALARM_FLAG                     = TAG_OUTPUT_INT_AMMENITIES_PREFIX + "SecurityAlarmFlag";
	// ExtStructInfo
	public static       String TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX                  = "output_ext_struct_info_";
	public static       String TAG_OUTPUT_STRUCTURE_STYLE                         = TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX + "StructureStyle";
	public static       String TAG_OUTPUT_EXTERIOR_1_CODE                         = TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX + "Exterior1Code";
	public static       String TAG_OUTPUT_ROOF_MATERIAL                           = TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX + "RoofMaterial";
	public static       String TAG_OUTPUT_ROOF_CONSTRUCTION                       = TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX + "RoofConstruction";
	public static       String TAG_OUTPUT_STORM_SHUTTER_FLAG                      = TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX + "StormShutterFlag";
	public static       String TAG_OUTPUT_OVERHEAD_DOOR_FLAG                      = TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX + "OverheadDoorFlag";
	// ExtAmmenities
	public static       String TAG_OUTPUT_EXT_AMMENITIES_PREFIX                   = "output_ext_ammenities_";
	public static       String TAG_OUTPUT_VIEW_DESCRIPTION                        = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "ViewDescription";
	public static       String TAG_OUTPUT_PORCH_CODE                              = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "PorchCode";
	public static       String TAG_OUTPUT_PORCH_AREA                              = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "PorchArea";
	public static       String TAG_OUTPUT_PATIO_AREA                              = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "PatioArea";
	public static       String TAG_OUTPUT_DECK_FLAG                               = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "DeckFlag";
	public static       String TAG_OUTPUT_DECK_AREA                               = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "DeckArea";
	public static       String TAG_OUTPUT_BALCONY_FLAG                            = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "FeatureBalconyFlag";
	public static       String TAG_OUTPUT_BALCONY_AREA                            = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "BalconyArea";
	public static       String TAG_OUTPUT_BREEZEWAY_FLAG                          = TAG_OUTPUT_EXT_AMMENITIES_PREFIX + "BreezewayFlag";
	//	 ExtBuildings
	public static       String TAG_OUTPUT_EXT_BUILDINGS_PREFIX                    = "output_ext_buildings_";
	public static       String TAG_OUTPUT_BUILDINGS_COUNT                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "BuildingsCount";
	public static       String TAG_OUTPUT_BATH_HOUSE_AREA                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "BathHouseArea";
	public static       String TAG_OUTPUT_BATH_HOUSE_FLAG                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "BathHouseFlag";
	public static       String TAG_OUTPUT_BOAT_ACCESS_FLAG                        = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "BoatAccessFlag";
	public static       String TAG_OUTPUT_BOAT_HOUSE_AREA                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "BoatHouseArea";
	public static       String TAG_OUTPUT_BOAT_HOUSE_FLAG                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "BoatHouseFlag";
	public static       String TAG_OUTPUT_CABIN_AREA                              = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "CabinArea";
	public static       String TAG_OUTPUT_CABIN_FLAG                              = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "CabinFlag";
	public static       String TAG_OUTPUT_CANOPY_AREA                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "CanopyArea";
	public static       String TAG_OUTPUT_CANOPY_FLAG                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "CanopyFlag";
	public static       String TAG_OUTPUT_GAZEBO_AREA                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GazeboArea";
	public static       String TAG_OUTPUT_GAZEBO_FLAG                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GazeboFlag";
	public static       String TAG_OUTPUT_GRAINERY_AREA                           = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GranaryArea";
	public static       String TAG_OUTPUT_GRAINERY_FLAG                           = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GranaryFlag";
	public static       String TAG_OUTPUT_GREEN_HOUSE_AREA                        = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GreenHouseArea";
	public static       String TAG_OUTPUT_GREEN_HOUSE_FLAG                        = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GreenHouseFlag";
	public static       String TAG_OUTPUT_GUEST_HOUSE_AREA                        = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GuestHouseArea";
	public static       String TAG_OUTPUT_GUEST_HOUSE_FLAG                        = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "GuestHouseFlag";
	public static       String TAG_OUTPUT_KENNEL_AREA                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "KennelArea";
	public static       String TAG_OUTPUT_KENNEL_FLAG                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "KennelFlag";
	public static       String TAG_OUTPUT_LEAN_TO_AREA                            = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "LeanToArea";
	public static       String TAG_OUTPUT_LEAN_TO_FLAG                            = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "LeanToFlag";
	public static       String TAG_OUTPUT_LOADING_PLATFORM_AREA                   = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "LoadingPlatformArea";
	public static       String TAG_OUTPUT_LOADING_PLATFORM_FLAG                   = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "LoadingPlatformFlag";
	public static       String TAG_OUTPUT_MILK_HOUSE_AREA                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "MilkHouseArea";
	public static       String TAG_OUTPUT_MILK_HOUSE_FLAG                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "MilkHouseFlag";
	public static       String TAG_OUTPUT_OUTDOOR_KITCHEN_FIREPLACE_FLAG          = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "OutdoorKitchenFireplaceFlag";
	public static       String TAG_OUTPUT_POOL_HOUSE_AREA                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "PoolHouseArea";
	public static       String TAG_OUTPUT_POOL_HOUSE_FLAG                         = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "PoolHouseFlag";
	public static       String TAG_OUTPUT_POULTRY_HOUSE_AREA                      = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "PoultryHouseArea";
	public static       String TAG_OUTPUT_POULTRY_HOUSE_FLAG                      = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "PoultryHouseFlag";
	public static       String TAG_OUTPUT_QUONSET_AREA                            = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "QuonsetArea";
	public static       String TAG_OUTPUT_QUONSET_FLAG                            = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "QuonsetFlag";
	public static       String TAG_OUTPUT_SHED_AREA                               = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "ShedArea";
	public static       String TAG_OUTPUT_SHED_CODE                               = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "ShedCode";
	public static       String TAG_OUTPUT_SILO_AREA                               = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "SiloArea";
	public static       String TAG_OUTPUT_SILO_FLAG                               = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "SiloFlag";
	public static       String TAG_OUTPUT_STABLE_AREA                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "StableArea";
	public static       String TAG_OUTPUT_STABLE_FLAG                             = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "StableFlag";
	public static       String TAG_OUTPUT_STORAGE_BUILDING_AREA                   = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "StorageBuildingArea";
	public static       String TAG_OUTPUT_STORAGE_BUILDING_FLAG                   = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "StorageBuildingFlag";
	public static       String TAG_OUTPUT_UTILITY_BUILDING_AREA                   = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "UtilityBuildingArea";
	public static       String TAG_OUTPUT_UTILITY_BUILDING_FLAG                   = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "UtilityBuildingFlag";
	public static       String TAG_OUTPUT_POLE_STRUCTURE_AREA                     = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "PoleStructureArea";
	public static       String TAG_OUTPUT_POLE_STRUCTURE_FLAG                     = TAG_OUTPUT_EXT_BUILDINGS_PREFIX + "PoleStructureFlag";
	//	Utilities
	public static       String TAG_OUTPUT_UTILITIES_PREFIX                        = "output_utilities_";
	public static       String TAG_OUTPUT_HVAC_COOLING_DETAIL                     = TAG_OUTPUT_UTILITIES_PREFIX + "HVACCoolingDetail";
	public static       String TAG_OUTPUT_HVAC_HEATING_DETAIL                     = TAG_OUTPUT_UTILITIES_PREFIX + "HVACHeatingDetail";
	public static       String TAG_OUTPUT_HVAC_HEATING_FULE                       = TAG_OUTPUT_UTILITIES_PREFIX + "HVACHeatingFuel";
	public static       String TAG_OUTPUT_SEWAGE_USAGE                            = TAG_OUTPUT_UTILITIES_PREFIX + "SewageUsage";
	public static       String TAG_OUTPUT_WATER_SOURCE                            = TAG_OUTPUT_UTILITIES_PREFIX + "WaterSource";
	public static       String TAG_OUTPUT_MOBIL_HOME_HOOKUP_FLAG                  = TAG_OUTPUT_UTILITIES_PREFIX + "MobileHomeHookupFlag";
	//	Parking
	public static       String TAG_OUTPUT_PARKING_PREFIX                          = "output_parking_";
	public static       String TAG_OUTPUT_RV_PARKING_FLAG                         = TAG_OUTPUT_PARKING_PREFIX + "RVParkingFlag";
	public static       String TAG_OUTPUT_PARKING_SPACE_COUNT                     = TAG_OUTPUT_PARKING_PREFIX + "ParkingSpaceCount";
	public static       String TAG_OUTPUT_DRIVEWAY_AREA                           = TAG_OUTPUT_PARKING_PREFIX + "DrivewayArea";
	public static       String TAG_OUTPUT_DRIVEWAY_MATERIAL                       = TAG_OUTPUT_PARKING_PREFIX + "DrivewayMaterial";
	//	YardGardenInfo
	public static       String TAG_OUTPUT_YARD_GARDEN_PREFIX                      = "output_yard_garden_";
	public static       String TAG_OUTPUT_TOPOGRAPHY_CODE                         = TAG_OUTPUT_YARD_GARDEN_PREFIX + "TopographyCode";
	public static       String TAG_OUTPUT_FENCE_CODE                              = TAG_OUTPUT_YARD_GARDEN_PREFIX + "FenceCode";
	public static       String TAG_OUTPUT_FENCE_AREA                              = TAG_OUTPUT_YARD_GARDEN_PREFIX + "FenceArea";
	public static       String TAG_OUTPUT_COURTYARD_FLAG                          = TAG_OUTPUT_YARD_GARDEN_PREFIX + "CourtyardFlag";
	public static       String TAG_OUTPUT_COURTYARD_AREA                          = TAG_OUTPUT_YARD_GARDEN_PREFIX + "CourtyardArea";
	public static       String TAG_OUTPUT_ARBOR_PERGOLA_FLAG                      = TAG_OUTPUT_YARD_GARDEN_PREFIX + "ArborPergolaFlag";
	public static       String TAG_OUTPUT_SPRINKLERS_FLAG                         = TAG_OUTPUT_YARD_GARDEN_PREFIX + "SprinklersFlag";
	public static       String TAG_OUTPUT_GOLF_COURSE_GREEN_FLAG                  = TAG_OUTPUT_YARD_GARDEN_PREFIX + "GolfCourseGreenFlag";
	public static       String TAG_OUTPUT_TENNIS_COURT_FLAG                       = TAG_OUTPUT_YARD_GARDEN_PREFIX + "TennisCourtFlag";
	public static       String TAG_OUTPUT_SPORTS_COURSE_FLAG                      = TAG_OUTPUT_YARD_GARDEN_PREFIX + "SportsCourtFlag";
	public static       String TAG_OUTPUT_ARENA_FLAG                              = TAG_OUTPUT_YARD_GARDEN_PREFIX + "ArenaFlag";
	public static       String TAG_OUTPUT_WATER_FEATURE_FLAG                      = TAG_OUTPUT_YARD_GARDEN_PREFIX + "WaterFeatureFlag";
	public static       String TAG_OUTPUT_POND_FLAG                               = TAG_OUTPUT_YARD_GARDEN_PREFIX + "PondFlag";
	public static       String TAG_OUTPUT_BOAT_LIFT_FLAG                          = TAG_OUTPUT_YARD_GARDEN_PREFIX + "BoatLiftFlag";
	// Estimated Value
	public static       String TAG_OUTPUT_ESTIMATED_VALUE_PREFIX                  = "output_estimated_value_";
	public static       String TAG_OUTPUT_ESTIMATED_VALUE                         = TAG_OUTPUT_ESTIMATED_VALUE_PREFIX + "EstimatedValue";
	public static       String TAG_OUTPUT_ESTIMATED_MIN_VALUE                     = TAG_OUTPUT_ESTIMATED_VALUE_PREFIX + "EstimatedMinValue";
	public static       String TAG_OUTPUT_ESTIMATED_MAX_VALUE                     = TAG_OUTPUT_ESTIMATED_VALUE_PREFIX + "EstimatedMaxValue";
	public static       String TAG_OUTPUT_CONFIDENCE_SCORE                        = TAG_OUTPUT_ESTIMATED_VALUE_PREFIX + "ConfidenceScore";
	public static       String TAG_OUTPUT_VALUATION_DATE                          = TAG_OUTPUT_ESTIMATED_VALUE_PREFIX + "ValuationDate";
	//	Shape
	public static       String TAG_OUTPUT_SHAPE_PREFIX                            = "output_shape_";
	public static       String TAG_OUTPUT_WELL_KNOWN_TEXT                         = TAG_OUTPUT_SHAPE_PREFIX + "WellKnownText";
	public HashMap<String, MetaVal> optionFields;
	public HashMap<String, MetaVal> inputFields;
	public HashMap<String, MetaVal> outputFields;
	public int                      fieldsAdded;
	public String webVersion = "";

	/**
	 * checks to see if the minimum requirements to run are met
	 *
	 * @return
	 */
	public boolean hasMinRequirements() {

		// FIXME set minimum requirements
		return true;
	}

	public boolean included(String key) {

		// Determines if a group output is included or excluded as a whole
		if (key.startsWith(TAG_OUTPUT_PARCEL_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_PARCEL).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_PROPERTY_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_PROPERTY_ADDRESS).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_PARSED_PROPERTY_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_PARSED_PROPERTY_ADDRESS).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_LEGAL_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_LEGAL).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_PRIMARY_OWNER_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_PRIMARY_OWNER).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_SECONDARY_OWNER_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_SECONDARY_OWNER).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_OWNER_ADDRESS_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_OWNER_ADDRESS).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_LAST_DEED_OWNER_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_LAST_DEED_OWNER).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_CURRENT_DEED_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_CURRENT_DEED).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_TAX_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_TAX).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_PROPERTY_USE_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_PROPERTY_USE_INFO).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_SALE_INFO_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_SALE_INFO).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_SHAPE_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_SHAPE).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_PROPERTY_SIZE_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_PROPERTY_SIZE).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_POOL_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_POOL).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_INTER_STRUCT_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_INT_STRUCT_INFO).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_INTER_ROOM_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_INT_ROOM_INFO).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_INT_AMMENITIES_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_INT_AMENITIES).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_EXT_STRUCT_INFO_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_EXT_STRUCT_INFO).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_EXT_AMMENITIES_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_EXT_AMENITIES).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_EXT_BUILDINGS_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_EXT_BUILDINGS).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_UTILITIES_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_UTILITIES).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_PARKING_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_PARKING).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_YARD_GARDEN_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_YARD_GARDEN_INFO).metaValue);
		}
		if (key.startsWith(TAG_OUTPUT_ESTIMATED_VALUE_PREFIX)) {
			return Boolean.valueOf(optionFields.get(TAG_OPTION_INCLUDE_ESTIMATED_VALUE).metaValue);
		}

		return false;
	}

	public String getGroups() {

		return "GrpAll";
	}

	/**
	 * initializes all the default value
	 */
	public void init() {

		if (optionFields == null) {
			optionFields = new HashMap<String, MetaVal>();
		}
		if (inputFields == null) {
			inputFields = new HashMap<String, MetaVal>();
		}
		if (outputFields == null) {
			outputFields = new HashMap<String, MetaVal>();
		}

		// repository xml key tag, metavalue default, web tag, and size are set here
		// OPTIONS
		optionFields.put(TAG_OPTION_INCLUDE_PARCEL, new MetaVal("true", TAG_OPTION_INCLUDE_PARCEL, 5));
		optionFields.put(TAG_OPTION_INCLUDE_LEGAL, new MetaVal("true", TAG_OPTION_INCLUDE_LEGAL, 5));
		optionFields.put(TAG_OPTION_INCLUDE_PROPERTY_ADDRESS, new MetaVal("true", TAG_OPTION_INCLUDE_PROPERTY_ADDRESS, 5));
		optionFields.put(TAG_OPTION_INCLUDE_PARSED_PROPERTY_ADDRESS, new MetaVal("true", TAG_OPTION_INCLUDE_PARSED_PROPERTY_ADDRESS, 5));
		optionFields.put(TAG_OPTION_INCLUDE_PRIMARY_OWNER, new MetaVal("true", TAG_OPTION_INCLUDE_PRIMARY_OWNER, 5));
		optionFields.put(TAG_OPTION_INCLUDE_SECONDARY_OWNER, new MetaVal("true", TAG_OPTION_INCLUDE_SECONDARY_OWNER, 5));
		optionFields.put(TAG_OPTION_INCLUDE_OWNER_ADDRESS, new MetaVal("true", TAG_OPTION_INCLUDE_OWNER_ADDRESS, 5));
		optionFields.put(TAG_OPTION_INCLUDE_LAST_DEED_OWNER, new MetaVal("true", TAG_OPTION_INCLUDE_LAST_DEED_OWNER, 5));
		optionFields.put(TAG_OPTION_INCLUDE_CURRENT_DEED, new MetaVal("true", TAG_OPTION_INCLUDE_CURRENT_DEED, 5));
		optionFields.put(TAG_OPTION_INCLUDE_TAX, new MetaVal("true", TAG_OPTION_INCLUDE_TAX, 5));
		optionFields.put(TAG_OPTION_INCLUDE_ESTIMATED_VALUE, new MetaVal("true", TAG_OPTION_INCLUDE_ESTIMATED_VALUE, 5));
		optionFields.put(TAG_OPTION_INCLUDE_SALE_INFO, new MetaVal("true", TAG_OPTION_INCLUDE_SALE_INFO, 5));
		optionFields.put(TAG_OPTION_INCLUDE_PROPERTY_USE_INFO, new MetaVal("true", TAG_OPTION_INCLUDE_PROPERTY_USE_INFO, 5));
		optionFields.put(TAG_OPTION_INCLUDE_PROPERTY_SIZE, new MetaVal("true", TAG_OPTION_INCLUDE_PROPERTY_SIZE, 5));
		optionFields.put(TAG_OPTION_INCLUDE_POOL, new MetaVal("true", TAG_OPTION_INCLUDE_POOL, 5));
		optionFields.put(TAG_OPTION_INCLUDE_INT_STRUCT_INFO, new MetaVal("true", TAG_OPTION_INCLUDE_INT_STRUCT_INFO, 5));
		optionFields.put(TAG_OPTION_INCLUDE_INT_ROOM_INFO, new MetaVal("true", TAG_OPTION_INCLUDE_INT_ROOM_INFO, 5));
		optionFields.put(TAG_OPTION_INCLUDE_INT_AMENITIES, new MetaVal("true", TAG_OPTION_INCLUDE_INT_AMENITIES, 5));
		optionFields.put(TAG_OPTION_INCLUDE_EXT_STRUCT_INFO, new MetaVal("true", TAG_OPTION_INCLUDE_EXT_STRUCT_INFO, 5));
		optionFields.put(TAG_OPTION_INCLUDE_EXT_AMENITIES, new MetaVal("true", TAG_OPTION_INCLUDE_EXT_AMENITIES, 5));
		optionFields.put(TAG_OPTION_INCLUDE_EXT_BUILDINGS, new MetaVal("true", TAG_OPTION_INCLUDE_EXT_BUILDINGS, 5));
		optionFields.put(TAG_OPTION_INCLUDE_UTILITIES, new MetaVal("true", TAG_OPTION_INCLUDE_UTILITIES, 5));
		optionFields.put(TAG_OPTION_INCLUDE_PARKING, new MetaVal("true", TAG_OPTION_INCLUDE_PARKING, 5));
		optionFields.put(TAG_OPTION_INCLUDE_YARD_GARDEN_INFO, new MetaVal("true", TAG_OPTION_INCLUDE_YARD_GARDEN_INFO, 5));
		optionFields.put(TAG_OPTION_INCLUDE_SHAPE, new MetaVal("true", TAG_OPTION_INCLUDE_SHAPE, 5));

// INPUTS
		inputFields.put(TAG_INPUT_ADDRESS_KEY, new MetaVal("", "AddressKey", 50));
		inputFields.put(TAG_INPUT_APN, new MetaVal("", "APN", 50));
		inputFields.put(TAG_INPUT_FIPS, new MetaVal("", "FIPS", 50));
		inputFields.put(TAG_INPUT_FREE_FORM, new MetaVal("", "FreeForm", 50));

// OUTPUTS
		outputFields.put(TAG_OUTPUT_RESULTS, new MetaVal("MD_Results", "Results", 50));

// Parcel

		outputFields.put(TAG_OUTPUT_FIPSCODE, new MetaVal("MD_FIPS_Code", "FIPSCode", 50));
		outputFields.put(TAG_OUTPUT_COUNTY, new MetaVal("MD_County", "County", 50));
		outputFields.put(TAG_OUTPUT_UNFORMATTEDAPN, new MetaVal("MD_Unformatted_APN", "UnformattedAPN", 50));
		outputFields.put(TAG_OUTPUT_FORMATTEDAPN, new MetaVal("MD_Formatted_APN", "FormattedAPN", 50));
		outputFields.put(TAG_OUTPUT_ALTERNATEAPN, new MetaVal("MD_Alternate_APN", "AlternateAPN", 50));
		outputFields.put(TAG_OUTPUT_APN_YEAR_CHANGE, new MetaVal("MD_APNYearChange", "APNYearChange", 50));
		outputFields.put(TAG_OUTPUT_PREVIOUS_APN, new MetaVal("MD_Previous_APN", "PreviousAPN", 50));
		outputFields.put(TAG_OUTPUT_ACCOUNT_NUMBER, new MetaVal("MD_Account_Number", "AccountNumber", 50));
		outputFields.put(TAG_OUTPUT_YEAR_ADDED, new MetaVal("MD_Year_Added", "YearAdded", 50));
		outputFields.put(TAG_OUTPUT_MAP_BOOK, new MetaVal("MD_Map_Book", "MapBook", 50));
		outputFields.put(TAG_OUTPUT_MAP_PAGE, new MetaVal("MD_MapPage", "MapPage", 50));

// Legal
		outputFields.put(TAG_OUTPUT_LEGAL_DESCRIPTION, new MetaVal("MD_Legal_Description", "LegalDescription", 50));
		outputFields.put(TAG_OUTPUT_RANGE, new MetaVal("MD_Range", "Range", 50));
		outputFields.put(TAG_OUTPUT_TOWNSHIP, new MetaVal("MD_Township", "Township", 50));
		outputFields.put(TAG_OUTPUT_SECTION, new MetaVal("MD_Section", "Section", 50));
		outputFields.put(TAG_OUTPUT_QUARTER, new MetaVal("MD_Quarter", "Quarter", 50));
		outputFields.put(TAG_OUTPUT_QUARTER_QUATER, new MetaVal("MD_Quarter_Quarter", "QuarterQuarter", 50));
		outputFields.put(TAG_OUTPUT_SUBDIVISION, new MetaVal("MD_Subdivision", "Subdivision", 50));
		outputFields.put(TAG_OUTPUT_PHASE, new MetaVal("MD_Phase", "Phase", 50));
		outputFields.put(TAG_OUTPUT_TRACT_NUMBER, new MetaVal("MD_Tract_Number", "TractNumber", 50));
		outputFields.put(TAG_OUTPUT_BLOCK_1, new MetaVal("MD_Block_1", "Block1", 50));
		outputFields.put(TAG_OUTPUT_BLOCK_2, new MetaVal("MD_Block_2", "Block2", 50));
		outputFields.put(TAG_OUTPUT_LOT_NUMBER_1, new MetaVal("MD_Lot_Number_1", "LotNumber1", 50));
		outputFields.put(TAG_OUTPUT_LOT_NUMBER_2, new MetaVal("MD_Lot_Number_2", "LotNumber2", 50));
		outputFields.put(TAG_OUTPUT_LOT_NUMBER_3, new MetaVal("MD_Lot_Number_3", "LotNumber3", 50));
		outputFields.put(TAG_OUTPUT_UNIT, new MetaVal("MD_Unit", "Unit", 50));

// Property Address
		outputFields.put(TAG_OUTPUT_PROPERTY_ADDRESS, new MetaVal("MD_Address", "Address", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_CITY, new MetaVal("MD_City", "City", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_STATE, new MetaVal("MD_State", "State", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_ZIP, new MetaVal("MD_Zip", "Zip", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_ADDRESSKEY, new MetaVal("MD_Address_Key", "AddressKey", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_MAK, new MetaVal("MD_MAK", "MAK", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_BASE_MAK, new MetaVal("MD_Base_MAK", "BaseMAK", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_LATITUDE, new MetaVal("MD_Latitude", "Latitude", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_LONGITUDE, new MetaVal("MD_Longitude", "Longitude", 50));

// Parsed Property Address
		outputFields.put(TAG_OUTPUT_PARSED_RANGE, new MetaVal("MD_Parsed_Range", "Range", 50));
		outputFields.put(TAG_OUTPUT_PARSED_PREDIRECTIONAL, new MetaVal("MD_Parsed_PreDirectional", "PreDirectional", 50));
		outputFields.put(TAG_OUTPUT_PARSED_STREETNAME, new MetaVal("MD_Parsed_Street_Name", "StreetName", 50));
		outputFields.put(TAG_OUTPUT_PARSED_SUFFIX, new MetaVal("MD_Parsed_Suffix", "Suffix", 50));
		outputFields.put(TAG_OUTPUT_PARSED_POSTDIRECTIONAL, new MetaVal("MD_Parsed_Post_Directional", "PostDirectional", 50));
		outputFields.put(TAG_OUTPUT_PARSED_SUITERANGE, new MetaVal("MD_Parsed_Suite_Range", "SuiteRange", 50));
		outputFields.put(TAG_OUTPUT_PARSED_SUITENAME, new MetaVal("MD_Parsed_Suite_Name", "SuiteName", 50));

// Primary Owner
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_1_FULL, new MetaVal("MD_Primary_Owner_Name_1_Full", "Name1Full", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_1_FIRST, new MetaVal("MD_Primary_Owner_Name_1_First", "Name1First", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_1_MIDDLE, new MetaVal("MD_Primary_Owner_Name_1_Middle", "Name1Middle", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_1_LAST, new MetaVal("MD_Primary_Owner_Name_1_Last", "Name1Last", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_1_SUFFIX, new MetaVal("MD_Primary_Owner_Name_1_Suffix", "Name1Suffix", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_TRUST_FLAG, new MetaVal("MD_Trust_Flag", "TrustFlag", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_COMPANY_FLAG, new MetaVal("MD_Company_Flag", "CompanyFlag", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_2_FULL, new MetaVal("MD_Primary_Owner_Name_2_Full", "Name2Full", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_2_FIRST, new MetaVal("MD_Primary_Owner_Name_2_First", "Name2First", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_2_MIDDLE, new MetaVal("MD_Primary_Owner_Name_2_Middle", "Name2Middle", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_2_LAST, new MetaVal("MD_Primary_Owner_Name_2_Last", "Name2Last", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_NAME_2_SUFFIX, new MetaVal("MD_Primary_Owner_Name_2_Sufix", "Name2Suffix", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_TYPE, new MetaVal("MD_Primary_Owner_Type", "Type", 50));
		outputFields.put(TAG_OUTPUT_PRIMARY_VESTING_TYPE, new MetaVal("MD_Primary_Owner_Vesting_Type", "VestingType", 50));

// Secondary Owner
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_3_FULL, new MetaVal("MD_Secondary_Owner_Name_3_Full", "Name3Full", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_3_FIRST, new MetaVal("MD_Secondary_Owner_Name_3_First", "Name3First", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_3_MIDDLE, new MetaVal("MD_Secondary_Owner_Name_3_Middle", "Name3Middle", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_3_LAST, new MetaVal("MD_Secondary_Owner_Name_3_Last", "Name3Last", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_3_SUFFIX, new MetaVal("MD_Secondary_Owner_Name_3_Suffix", "Name3Suffix", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_4_FULL, new MetaVal("MD_Secondary_Owner_Name_4_Full", "Name4Full", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_4_FIRST, new MetaVal("MD_Secondary_Owner_Name_4_First", "Name4First", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_4_MIDDLE, new MetaVal("MD_Secondary_Owner_Name_4_Middle", "Name4Middle", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_4_LAST, new MetaVal("MD_Secondary_Owner_Name_4_Last", "Name4Last", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_NAME_4_SUFFIX, new MetaVal("MD_Secondary_Owner_Name_4_Suffix", "Name4Suffix", 50));
		outputFields.put(TAG_OUTPUT_SECONDARY_TYPE, new MetaVal("MD_Secondary_Owner_Type", "Type", 50));

// Owner Addres
		outputFields.put(TAG_OUTPUT_OWNER_ADDRESS, new MetaVal("MD_Owner_Address", "Address", 50));
		outputFields.put(TAG_OUTPUT_OWNER_CITY, new MetaVal("MD_Owner_City", "City", 50));
		outputFields.put(TAG_OUTPUT_OWNER_STATE, new MetaVal("MD_Owner_State", "State", 50));
		outputFields.put(TAG_OUTPUT_OWNER_ZIP, new MetaVal("MD_Owner_Zip", "Zip", 50));
		outputFields.put(TAG_OUTPUT_OWNER_CARRIERROUTE, new MetaVal("MD_Owner_Carrier_Route", "CarrierRoute", 50));
		outputFields.put(TAG_OUTPUT_OWNER_MAK, new MetaVal("MD_Owner_MAK", "MAK", 50));
		outputFields.put(TAG_OUTPUT_OWNER_BASE_MAK, new MetaVal("MD_Owner_Base_MAK", "BaseMAK", 50));

// Last Deed Owner
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FULL, new MetaVal("MD_Last_Deed_Owner_Name_1_Full", "Name1Full", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_FIRST, new MetaVal("MD_Last_Deed_Owner_Name_1_First", "Name1First", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_MIDDLE, new MetaVal("MD_Last_Deed_Owner_Name_1_Middle", "Name1Middle", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_LAST, new MetaVal("MD_Last_Deed_Owner_Name_1_Last", "Name1Last", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_1_SUFFIX, new MetaVal("MD_Last_Deed_Owner_Name_1_Suffix", "Name1Suffix", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FULL, new MetaVal("MD_Last_Deed_Owner_Name_2_Full", "Name2Full", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_FIRST, new MetaVal("MD_Last_Deed_Owner_Name_2_First", "Name2First", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_MIDDLE, new MetaVal("MD_Last_Deed_Owner_Name_2_Middle", "Name2Middle", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_LAST, new MetaVal("MD_Last_Deed_Owner_Name_2_Last", "Name2Last", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_2_SUFFIX, new MetaVal("MD_Last_Deed_Owner_Name_2_Suffix", "Name2Suffix", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FULL, new MetaVal("MD_Last_Deed_Owner_Name_3_Full", "Name3Full", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_FIRST, new MetaVal("MD_Last_Deed_Owner_Name_3_First", "Name3First", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_MIDDLE, new MetaVal("MD_Last_Deed_Owner_Name_3_Middle", "Name3Middle", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_LAST, new MetaVal("MD_Last_Deed_Owner_Name_3_Last", "Name3Last", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_3_SUFFIX, new MetaVal("MD_Last_Deed_Owner_Name_3_Suffix", "Name3Suffix", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FULL, new MetaVal("MD_Last_Deed_Owner_Name_4_Full", "Name4Full", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_FIRST, new MetaVal("MD_Last_Deed_Owner_Name_4_First", "Name4First", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_MIDDLE, new MetaVal("MD_Last_Deed_Owner_Name_4_Middle", "Name4Middle", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_LAST, new MetaVal("MD_Last_Deed_Owner_Name_4_Last", "Name4Last", 50));
		outputFields.put(TAG_OUTPUT_LAST_DEED_OWNER_NAME_4_SUFFIX, new MetaVal("MD_Last_Deed_Owner_Name_4_Suffix", "Name4Suffix", 50));

// Current Deed
		outputFields.put(TAG_OUTPUT_MORTGAGE_AMOUNT, new MetaVal("MD_Mortgage_Amount", "MortgageAmount", 50));
		outputFields.put(TAG_OUTPUT_MORTGAGE_DATE, new MetaVal("MD_Mortgage_Date", "MortgageDate", 50));
		outputFields.put(TAG_OUTPUT_MORTGAGE_LOAN_TYPE_CODE, new MetaVal("MD_Mortgage_Loan_Type_Code", "MortgageLoanTypeCode", 50));
		outputFields.put(TAG_OUTPUT_MORTGAGE_TERM_CODE, new MetaVal("MD_Mortgage_Term_Code", "MortgageTermCode", 50));
		outputFields.put(TAG_OUTPUT_MORTGAGE_TERM, new MetaVal("MD_Mortgage_Term", "MortgageTerm", 50));
		outputFields.put(TAG_OUTPUT_MORTGAGE_DUE_DATE, new MetaVal("MD_Mortgage_Due_Date", "MortgageDueDate", 50));
		outputFields.put(TAG_OUTPUT_LENDER_CODE, new MetaVal("MD_Lender_Code", "LenderCode", 50));
		outputFields.put(TAG_OUTPUT_LENDER_NAME, new MetaVal("MD_Lender_Name", "LenderName", 50));
		outputFields.put(TAG_OUTPUT_SECOND_MORTGAGE_AMOUNT, new MetaVal("MD_Second_Mortgage_Amount", "SecondMortgageAmount", 50));
		outputFields.put(TAG_OUTPUT_SECOND_MORTGAGE_LOAN_TYPE_CODE, new MetaVal("MD_Second_Mortgage_Type_Code", "SecondMortgageLoanTypeCode", 50));

// Tax
		outputFields.put(TAG_OUTPUT_YEAR_ASSESSED, new MetaVal("MD_Year_Assessed", "YearAssessed", 50));
		outputFields.put(TAG_OUTPUT_ASSESSED_VALUE_TOTAL, new MetaVal("MD_Assessed_Value_Total", "AssessedValueTotal", 50));
		outputFields.put(TAG_OUTPUT_ASSESSED_VALUE_IMPROVEMENTS, new MetaVal("MD_Assessed_Value_Improvements", "AssessedValueImprovements", 50));
		outputFields.put(TAG_OUTPUT_ASSESSED_VALUE_LAND, new MetaVal("MD_Assessed_Value_Land", "AssessedValueLand", 50));
		outputFields.put(TAG_OUTPUT_ASSESSED_IMPROVEMENTS_PERC, new MetaVal("MD_Assessed_Improvements_Perc", "AssessedImprovementsPerc", 50));
		outputFields.put(TAG_OUTPUT_PREVIOUS_ASSESSED_VALUE, new MetaVal("MD_Previous_Assessed_Value", "PreviousAssessedValue", 50));
		outputFields.put(TAG_OUTPUT_MARKET_VALUE_YEAR, new MetaVal("MD_Market_Value_Year", "MarketValueYear", 50));
		outputFields.put(TAG_OUTPUT_MARKET_VALUE_TOTAL, new MetaVal("MD_Market_Value_Total", "MarketValueTotal", 50));
		outputFields.put(TAG_OUTPUT_MARKET_VALUE_IMPROVEMENTS, new MetaVal("MD_Market_Value_Improvements", "MarketValueImprovements", 50));
		outputFields.put(TAG_OUTPUT_MARKET_VALUE_LAND, new MetaVal("MD_Market_Value_Land", "MarketValueLand", 50));
		outputFields.put(TAG_OUTPUT_MARKET_IMPROVEMENT_PREC, new MetaVal("MD_Market_Improvements_Perc", "MarketImprovementsPerc", 50));
		outputFields.put(TAG_OUTPUT_TAX_FISCAL_YEAR, new MetaVal("MD_Tax_Fiscal_Year", "TaxFiscalYear", 50));
		outputFields.put(TAG_OUTPUT_TAX_RATE_AREA, new MetaVal("MD_Tax_Rate_Area", "TaxRateArea", 50));
		outputFields.put(TAG_OUTPUT_TAX_BILL_AMOUNT, new MetaVal("MD_Tax_Billed_Amount", "TaxBilledAmount", 50));
		outputFields.put(TAG_OUTPUT_TAX_DELINQUENT_YEAR, new MetaVal("MD_Tax_Delinquent_Year", "TaxDelinquentYear", 50));
		outputFields.put(TAG_OUTPUT_LAST_TAX_ROLL_UPDATE, new MetaVal("MD_Last_Tax_Roll_Update", "LastTaxRollUpdate", 50));
		outputFields.put(TAG_OUTPUT_ASSR_LAST_UPDATED, new MetaVal("MD_Assr_Last_Updated", "AssrLastUpdated", 50));
		outputFields.put(TAG_OUTPUT_TAX_EXEMPTION_HOMEOWNER, new MetaVal("MD_Tax_Exemption_Homeowner", "TaxExemptionHomeowner", 50));
		outputFields.put(TAG_OUTPUT_TAX_EXEMPTION_DISABLED, new MetaVal("MD_Tax_Exemption_Disabled", "TaxExemptionDisabled", 50));
		outputFields.put(TAG_OUTPUT_TAX_EXEMPTION_SENIOR, new MetaVal("MD_Tax_Exemption_Senior", "TaxExemptionSenior", 50));
		outputFields.put(TAG_OUTPUT_TAX_EXEMPTION_VETERAN, new MetaVal("MD_Tax_Exemption_Veteran", "TaxExemptionVeteran", 50));
		outputFields.put(TAG_OUTPUT_TAX_EXEMPTION_WIDOW, new MetaVal("MD_Tax_Exemption_Widow", "TaxExemptionWidow", 50));
		outputFields.put(TAG_OUTPUT_TAX_EXEMPTION_ADDITIONAL, new MetaVal("MD_Tax_Exemption_Additional", "TaxExemptionAdditional", 50));

// PropertyUseInfo
		outputFields.put(TAG_OUTPUT_YEAR_BUILT, new MetaVal("MD_Year_Built", "YearBuilt", 50));
		outputFields.put(TAG_OUTPUT_YEAR_BUILT_EFFECTIVE, new MetaVal("MD_Year_Built_Effective", "YearBuiltEffective", 50));
		outputFields.put(TAG_OUTPUT_ZONED_CODE_LOCAL, new MetaVal("MD_Zoned_Code_Local", "ZonedCodeLocal", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_USE_MUNI, new MetaVal("MD_Property_Use_Muni", "PropertyUseMuni", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_USE_GROUP, new MetaVal("MD_Property_Use_Group", "PropertyUseGroup", 50));
		outputFields.put(TAG_OUTPUT_PROPERTY_USE_STANDARDIZED, new MetaVal("MD_Property_Use_Standardized", "PropertyUseStandardized", 50));

// SaleInfo
		outputFields.put(TAG_OUTPUT_ASSESSORS_LAST_SALE_DATE, new MetaVal("MD_Assessors_Last_Sale_Date", "AssessorLastSaleDate", 50));
		outputFields.put(TAG_OUTPUT_ASSESSORS_LAST_SALE_AMMOUNT, new MetaVal("MD_Assessor_Last_Sale_Amount", "AssessorLastSaleAmount", 50));
		outputFields.put(TAG_OUTPUT_ASSESSORS_PRIOR_SALE_DATE, new MetaVal("MD_Assessor_Prior_Sale_Date", "AssessorPriorSaleDate", 50));
		outputFields.put(TAG_OUTPUT_ASSESSORS_PRIOR_SALE_AMOUNT, new MetaVal("MD_Assessor_Prior_Sale_Amount", "AssessorPriorSaleAmount", 50));
		outputFields.put(TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DATE, new MetaVal("MD_Last_Ownership_Transfer_Date", "LastOwnershipTransferDate", 50));
		outputFields.put(TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_DOCUMENT_NUMBER, new MetaVal("MD_Last_Ownership_Transfer_Document_Number", "LastOwnershipTransferDocumentNumber", 50));
		outputFields.put(TAG_OUTPUT_LAST_OWNERSHIP_TRANSFER_TAX_ID, new MetaVal("MD_Last_Ownership_Transfer_TxID", "LastOwnershipTransferTxID", 50));
		outputFields.put(TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_BOOK, new MetaVal("MD_Deed_Last_Sale_Document_Book", "DeedLastSaleDocumentBook", 50));
		outputFields.put(TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_PAGE, new MetaVal("MD_Deed_Last_Sale_Document_Page", "DeedLastSaleDocumentPage", 50));
		outputFields.put(TAG_OUTPUT_DEED_LAST_SALE_DOCUMENT_NUMBER, new MetaVal("MD_Deed_Last_Document_Number", "DeedLastDocumentNumber", 50));
		outputFields.put(TAG_OUTPUT_DEED_LAST_SALE_DATE, new MetaVal("MD_Deed_Last_Sale_Date", "DeedLastSaleDate", 50));
		outputFields.put(TAG_OUTPUT_DEED_LAST_SALE_PRICE, new MetaVal("MD_Deed_Last_Sale_Price", "DeedLastSalePrice", 50));
		outputFields.put(TAG_OUTPUT_DEED_LAST_SALE_TAX_ID, new MetaVal("MD_Deed_Last_Sale_TxID", "DeedLastSaleTxID", 50));

// PropertySize
		outputFields.put(TAG_OUTPUT_AREA_BUILDING, new MetaVal("MD_Building_Area", "AreaBuilding", 50));
		outputFields.put(TAG_OUTPUT_AREA_BUILDING_DEFINITION_CODE, new MetaVal("MD_Building_Definition_Code", "AreaBuildingDefinitionCode", 50));
		outputFields.put(TAG_OUTPUT_AREA_GROSS, new MetaVal("MD_Gross_Area", "AreaGross", 50));
		outputFields.put(TAG_OUTPUT_AREA_1ST_FLOOR, new MetaVal("MD_1st Floor Area", "Area1stFloor", 50));
		outputFields.put(TAG_OUTPUT_AREA_2ND_FLOOR, new MetaVal("MD_2nd Floor Area", "Area2ndFloor", 50));
		outputFields.put(TAG_OUTPUT_AREA_UPPER_FLOORS, new MetaVal("MD_Upper_Floor_Area", "AreaUpperFloors", 50));
		outputFields.put(TAG_OUTPUT_AREA_LOT_ACRES, new MetaVal("MD_Lot_Acres", "AreaLotAcres", 50));
		outputFields.put(TAG_OUTPUT_AREA_LOT_SF, new MetaVal("MD_Lot_Square_Footage", "AreaLotSF", 50));
		outputFields.put(TAG_OUTPUT_LOT_DEPTH, new MetaVal("MD_Lot_Depth", "LotDepth", 50));
		outputFields.put(TAG_OUTPUT_LOT_WIDTH, new MetaVal("MD_Lot_Width", "LotWidth", 50));
		outputFields.put(TAG_OUTPUT_ATTIC_AREA, new MetaVal("MD_Attic_Area", "AtticArea", 50));
		outputFields.put(TAG_OUTPUT_ATTIC_FLAG, new MetaVal("MD_Attic_Flag", "AtticFlag", 50));
		outputFields.put(TAG_OUTPUT_BASEMENT_AREA, new MetaVal("MD_Basement_Area", "BasementArea", 50));
		outputFields.put(TAG_OUTPUT_BASEMENT_AREA_FINISHED, new MetaVal("MD_Basement_Area_Finished", "BasementAreaFinished", 50));
		outputFields.put(TAG_OUTPUT_BASEMENT_AREA_UNFINISHED, new MetaVal("MD_Basement_Area_Unfinished", "BasementAreaUnfinished", 50));
		outputFields.put(TAG_OUTPUT_PARKING_GARAGE, new MetaVal("MD_Parking_Garage", "ParkingGarage", 50));
		outputFields.put(TAG_OUTPUT_PARKING_GARAGE_AREA, new MetaVal("MD_Parking_Garage_Area", "ParkingGarageArea", 50));
		outputFields.put(TAG_OUTPUT_PARKING_CARPORT, new MetaVal("MD_Parking_Carport", "ParkingCarport", 50));
		outputFields.put(TAG_OUTPUT_PARKING_CARPORT_AREA, new MetaVal("MD_Parking_Carport_Area", "ParkingCarportArea", 50));

// Pool
		outputFields.put(TAG_OUTPUT_POOL, new MetaVal("MD_Pool", "Pool", 50));
		outputFields.put(TAG_OUTPUT_POOL_AREA, new MetaVal("MD_Pool_Area", "PoolArea", 50));
		outputFields.put(TAG_OUTPUT_SAUNA_FLAG, new MetaVal("MD_SaunaFlag", "SaunaFlag", 50));

// IntStructInfo
		outputFields.put(TAG_OUTPUT_FOUNDATION, new MetaVal("MD_Foundation", "Foundation", 50));
		outputFields.put(TAG_OUTPUT_CONSTRUCTION, new MetaVal("MD_Construction", "Construction", 50));
		outputFields.put(TAG_OUTPUT_INTERIOR_STRUCTURE, new MetaVal("MD_Interior_Structure", "InteriorStructure", 50));
		outputFields.put(TAG_OUTPUT_PLUMBING_FIXTURE, new MetaVal("MD_Plumbing_Fixtures_Count", "PlumbingFixturesCount", 50));
		outputFields.put(TAG_OUTPUT_CONSTRUCT_FIRE_RESISTANCE_CLASS, new MetaVal("MD_Construction_Fire_Resistance_Class", "ConstructionFireResistanceClass", 50));
		outputFields.put(TAG_OUTPUT_SAFETY_FIRE_SPRINKLER_FLAG, new MetaVal("MD_Safety_Fire_Sprinklers_Flag", "SafetyFireSprinklersFlag", 50));
		outputFields.put(TAG_OUTPUT_FLOORING_MATERIAL_PRIMARY, new MetaVal("MD_Flooring_Material_Primary", "FlooringMaterialPrimary", 50));

// IntRoomInfo
		outputFields.put(TAG_OUTPUT_BATH_COUNT, new MetaVal("MD_Bath_Count", "BathCount", 50));
		outputFields.put(TAG_OUTPUT_BATH_PARTIAL_COUNT, new MetaVal("MD_Bath_Partial_Count", "BathPartialCount", 50));
		outputFields.put(TAG_OUTPUT_BEDROOMS_COUNT, new MetaVal("MD_Bedrooms_Count", "BedroomsCount", 50));
		outputFields.put(TAG_OUTPUT_ROOMS_COUNT, new MetaVal("MD_Rooms_Count", "RoomsCount", 50));
		outputFields.put(TAG_OUTPUT_STORIES_COUNT, new MetaVal("MD_Stories_Count", "StoriesCount", 50));
		outputFields.put(TAG_OUTPUT_UNITS_COUNT, new MetaVal("MD_Units_Count", "UnitsCount", 50));
		outputFields.put(TAG_OUTPUT_BONUS_ROOM_FLAG, new MetaVal("MD_Bonus_Room_Flag", "BonusRoomFlag", 50));
		outputFields.put(TAG_OUTPUT_BREAKFAST_NOOK_FLAG, new MetaVal("MD_Breakfast_Nook_Flag", "BreakfastNookFlag", 50));
		outputFields.put(TAG_OUTPUT_CELLAR_FLAG, new MetaVal("MD_Cellar_Flag", "CellarFlag", 50));
		outputFields.put(TAG_OUTPUT_WINE_CELLAR_FLAG, new MetaVal("MD_Cellar_Wine_Flag", "CellarWineFlag", 50));
		outputFields.put(TAG_OUTPUT_EXERCISE_ROOM_FLAG, new MetaVal("MD_Excercise_Flag", "ExcerciseFlag", 50));
		outputFields.put(TAG_OUTPUT_FAMILY_ROOM_FLAG, new MetaVal("MD_Family_Code", "FamilyCode", 50));
		outputFields.put(TAG_OUTPUT_GAME_ROOM_FLAG, new MetaVal("MD_Game_Flag", "GameFlag", 50));
		outputFields.put(TAG_OUTPUT_GREAT_ROOM_FLAG, new MetaVal("MD_Great_Flag", "GreatFlag", 50));
		outputFields.put(TAG_OUTPUT_HOBBY_ROOM_FLAG, new MetaVal("MD_Hobby_Flag", "HobbyFlag", 50));
		outputFields.put(TAG_OUTPUT_LAUNDRY_ROOM_FLAG, new MetaVal("MD_Laundry_Flag", "LaundryFlag", 50));
		outputFields.put(TAG_OUTPUT_MEDIA_ROOM_FLAG, new MetaVal("MD_Media_Flag", "MediaFlag", 50));
		outputFields.put(TAG_OUTPUT_MUD_ROOM_FLAG, new MetaVal("MD_Mud_Flag", "MudFlag", 50));
		outputFields.put(TAG_OUTPUT_OFFICE_AREA, new MetaVal("MD_Office_Area", "OfficeArea", 50));
		outputFields.put(TAG_OUTPUT_OFFICE_ROOM_FLAG, new MetaVal("MD_Office_Flag", "OfficeFlag", 50));
		outputFields.put(TAG_OUTPUT_SAFE_ROOM_FLAG, new MetaVal("MD_Safe_Room_Flag", "SafeRoomFlag", 50));
		outputFields.put(TAG_OUTPUT_SITTING_ROOM_FLAG, new MetaVal("MD_Sitting_Flag", "SittingFlag", 50));
		outputFields.put(TAG_OUTPUT_STORM_SHELTER, new MetaVal("MD_Storm_Shelter", "StormShelter", 50));
		outputFields.put(TAG_OUTPUT_STUDY_ROOM_FLAG, new MetaVal("MD_Study_Flag", "StudyFlag", 50));
		outputFields.put(TAG_OUTPUT_SUN_ROOM_FLAG, new MetaVal("MD_Sunroom_Flag", "SunroomFlag", 50));
		outputFields.put(TAG_OUTPUT_UTILITY_ROOM_AREA, new MetaVal("MD_Utility_Area", "UtilityArea", 50));
		outputFields.put(TAG_OUTPUT_UTILITY_ROOM_CODE, new MetaVal("MD_Utility_Code", "UtilityCode", 50));

// IntAmmenities
		outputFields.put(TAG_OUTPUT_FIREPLACE, new MetaVal("MD_Fireplace", "Fireplace", 50));
		outputFields.put(TAG_OUTPUT_FIREPLACE_COUNT, new MetaVal("MD_Fireplace_Count", "FireplaceCount", 50));
		outputFields.put(TAG_OUTPUT_ELEVATOR_FLAG, new MetaVal("MD_Accessability_Elevator_Flag", "AccessabilityElevatorFlag", 50));
		outputFields.put(TAG_OUTPUT_HANDICAP_FLAG, new MetaVal("MD_Accessability_Handicap_Flag", "AccessabilityHandicapFlag", 50));
		outputFields.put(TAG_OUTPUT_ESCALATOR_FLAG, new MetaVal("MD_Escalator_Flag", "EscalatorFlag", 50));
		outputFields.put(TAG_OUTPUT_CENTRAL_VACUUM_FLAG, new MetaVal("MD_Central_Vacuum_Flag", "CentralVacuumFlag", 50));
		outputFields.put(TAG_OUTPUT_INTERCOM_FLAG, new MetaVal("MD_Intercom_Flag", "IntercomFlag", 50));
		outputFields.put(TAG_OUTPUT_SOUND_SYSTEM_FLAG, new MetaVal("MD_Sound_System_Flag", "SoundSystemFlag", 50));
		outputFields.put(TAG_OUTPUT_WET_BAR_FLAG, new MetaVal("MD_Wet_Bar_Flag", "WetBarFlag", 50));
		outputFields.put(TAG_OUTPUT_SECURITY_ALARM_FLAG, new MetaVal("MD_Security_Alarm_Flag", "SecurityAlarmFlag", 50));

// ExtStructInfo
		outputFields.put(TAG_OUTPUT_STRUCTURE_STYLE, new MetaVal("MD_Structure_Style", "StructureStyle", 50));
		outputFields.put(TAG_OUTPUT_EXTERIOR_1_CODE, new MetaVal("MD_Exterior_1_Code", "Exterior1Code", 50));
		outputFields.put(TAG_OUTPUT_ROOF_MATERIAL, new MetaVal("MD_Roof_Material", "RoofMaterial", 50));
		outputFields.put(TAG_OUTPUT_ROOF_CONSTRUCTION, new MetaVal("MD_Roof_Construction", "RoofConstruction", 50));
		outputFields.put(TAG_OUTPUT_STORM_SHUTTER_FLAG, new MetaVal("MD_Storm_Shutter_Flag", "StormShutterFlag", 50));
		outputFields.put(TAG_OUTPUT_OVERHEAD_DOOR_FLAG, new MetaVal("MD_Overhead_Door_Flag", "OverheadDoorFlag", 50));

// ExtAmmenities
		outputFields.put(TAG_OUTPUT_VIEW_DESCRIPTION, new MetaVal("MD_View_Description", "ViewDescription", 50));
		outputFields.put(TAG_OUTPUT_PORCH_CODE, new MetaVal("MD_Porch_Code", "PorchCode", 50));
		outputFields.put(TAG_OUTPUT_PORCH_AREA, new MetaVal("MD_Porch_Area", "PorchArea", 50));
		outputFields.put(TAG_OUTPUT_PATIO_AREA, new MetaVal("MD_Patio_Area", "PatioArea", 50));
		outputFields.put(TAG_OUTPUT_DECK_FLAG, new MetaVal("MD_Deck_Flag", "DeckFlag", 50));
		outputFields.put(TAG_OUTPUT_DECK_AREA, new MetaVal("MD_Deck_Area", "DeckArea", 50));
		outputFields.put(TAG_OUTPUT_BALCONY_FLAG, new MetaVal("MD_Feature_Balcony_Flag", "FeatureBalconyFlag", 50));
		outputFields.put(TAG_OUTPUT_BALCONY_AREA, new MetaVal("MD_Balcony_Area", "BalconyArea", 50));
		outputFields.put(TAG_OUTPUT_BREEZEWAY_FLAG, new MetaVal("MD_Breezeway_Flag", "BreezewayFlag", 50));

// ExtBuildings
		outputFields.put(TAG_OUTPUT_BUILDINGS_COUNT, new MetaVal("MD_Buildings_Count", "BuildingsCount", 50));
		outputFields.put(TAG_OUTPUT_BATH_HOUSE_AREA, new MetaVal("MD_BathHouse_Area", "BathHouseArea", 50));
		outputFields.put(TAG_OUTPUT_BATH_HOUSE_FLAG, new MetaVal("MD_BathHouse_Flag", "BathHouseFlag", 50));
		outputFields.put(TAG_OUTPUT_BOAT_ACCESS_FLAG, new MetaVal("MD_Boat_Access_Flag", "BoatAccessFlag", 50));
		outputFields.put(TAG_OUTPUT_BOAT_HOUSE_AREA, new MetaVal("MD_Boat_House_Area", "BoatHouseArea", 50));
		outputFields.put(TAG_OUTPUT_BOAT_HOUSE_FLAG, new MetaVal("MD_Boat_House_Flag", "BoatHouseFlag", 50));
		outputFields.put(TAG_OUTPUT_CABIN_AREA, new MetaVal("MD_Cabin_Area", "CabinArea", 50));
		outputFields.put(TAG_OUTPUT_CABIN_FLAG, new MetaVal("MD_Cabin_Flag", "CabinFlag", 50));
		outputFields.put(TAG_OUTPUT_CANOPY_AREA, new MetaVal("MD_Canopy_Area", "CanopyArea", 50));
		outputFields.put(TAG_OUTPUT_CANOPY_FLAG, new MetaVal("MD_Canopy_Flag", "CanopyFlag", 50));
		outputFields.put(TAG_OUTPUT_GAZEBO_AREA, new MetaVal("MD_Gazebo_Area", "GazeboArea", 50));
		outputFields.put(TAG_OUTPUT_GAZEBO_FLAG, new MetaVal("MD_Gazebo_Flag", "GazeboFlag", 50));
		outputFields.put(TAG_OUTPUT_GRAINERY_AREA, new MetaVal("MD_Granary_Area", "GranaryArea", 50));
		outputFields.put(TAG_OUTPUT_GRAINERY_FLAG, new MetaVal("MD_Granary_Flag", "GranaryFlag", 50));
		outputFields.put(TAG_OUTPUT_GREEN_HOUSE_AREA, new MetaVal("MD_GreenHouse_Area", "GreenHouseArea", 50));
		outputFields.put(TAG_OUTPUT_GREEN_HOUSE_FLAG, new MetaVal("MD_GreenHouse_Flag", "GreenHouseFlag", 50));
		outputFields.put(TAG_OUTPUT_GUEST_HOUSE_AREA, new MetaVal("MD_Guest_House_Area", "GuestHouseArea", 50));
		outputFields.put(TAG_OUTPUT_GUEST_HOUSE_FLAG, new MetaVal("MD_Guest_House_Flag", "GuestHouseFlag", 50));
		outputFields.put(TAG_OUTPUT_KENNEL_AREA, new MetaVal("MD_Kennel_Area", "KennelArea", 50));
		outputFields.put(TAG_OUTPUT_KENNEL_FLAG, new MetaVal("MD_Kennel_Flag", "KennelFlag", 50));
		outputFields.put(TAG_OUTPUT_LEAN_TO_AREA, new MetaVal("MD_LeanTo_Area", "LeanToArea", 50));
		outputFields.put(TAG_OUTPUT_LEAN_TO_FLAG, new MetaVal("MD_LeanTo_Flag", "LeanToFlag", 50));
		outputFields.put(TAG_OUTPUT_LOADING_PLATFORM_AREA, new MetaVal("MD_Loading_Platform_Area", "LoadingPlatformArea", 50));
		outputFields.put(TAG_OUTPUT_LOADING_PLATFORM_FLAG, new MetaVal("MD_Loading_Platform_Flag", "LoadingPlatformFlag", 50));
		outputFields.put(TAG_OUTPUT_MILK_HOUSE_AREA, new MetaVal("MD_Milk_House_Area", "MilkHouseArea", 50));
		outputFields.put(TAG_OUTPUT_MILK_HOUSE_FLAG, new MetaVal("MD_Milk_House_Flag", "MilkHouseFlag", 50));
		outputFields.put(TAG_OUTPUT_OUTDOOR_KITCHEN_FIREPLACE_FLAG, new MetaVal("MD_Outdoor_Kitchen_Fireplace_Flag", "OutdoorKitchenFireplaceFlag", 50));
		outputFields.put(TAG_OUTPUT_POOL_HOUSE_AREA, new MetaVal("MD_Pool_House_Area", "PoolHouseArea", 50));
		outputFields.put(TAG_OUTPUT_POOL_HOUSE_FLAG, new MetaVal("MD_Pool_House_Flag", "PoolHouseFlag", 50));
		outputFields.put(TAG_OUTPUT_POULTRY_HOUSE_AREA, new MetaVal("MD_Poultry_House_Area", "PoultryHouseArea", 50));
		outputFields.put(TAG_OUTPUT_POULTRY_HOUSE_FLAG, new MetaVal("MD_Poultry_House_Flag", "PoultryHouseFlag", 50));
		outputFields.put(TAG_OUTPUT_QUONSET_AREA, new MetaVal("MD_Quonset_Area", "QuonsetArea", 50));
		outputFields.put(TAG_OUTPUT_QUONSET_FLAG, new MetaVal("MD_Quonset_Flag", "QuonsetFlag", 50));
		outputFields.put(TAG_OUTPUT_SHED_AREA, new MetaVal("MD_Shed_Area", "ShedArea", 50));
		outputFields.put(TAG_OUTPUT_SHED_CODE, new MetaVal("MD_Shed_Code", "ShedCode", 50));
		outputFields.put(TAG_OUTPUT_SILO_AREA, new MetaVal("MD_Silo_Area", "SiloArea", 50));
		outputFields.put(TAG_OUTPUT_SILO_FLAG, new MetaVal("MD_Silo_Flag", "SiloFlag", 50));
		outputFields.put(TAG_OUTPUT_STABLE_AREA, new MetaVal("MD_Stable_Area", "StableArea", 50));
		outputFields.put(TAG_OUTPUT_STABLE_FLAG, new MetaVal("MD_Stable_Flag", "StableFlag", 50));
		outputFields.put(TAG_OUTPUT_STORAGE_BUILDING_AREA, new MetaVal("MD_Storage_Building_Area", "StorageBuildingArea", 50));
		outputFields.put(TAG_OUTPUT_STORAGE_BUILDING_FLAG, new MetaVal("MD_Storage_Building_Flag", "StorageBuildingFlag", 50));
		outputFields.put(TAG_OUTPUT_UTILITY_BUILDING_AREA, new MetaVal("MD_Utility_Building_Area", "UtilityBuildingArea", 50));
		outputFields.put(TAG_OUTPUT_UTILITY_BUILDING_FLAG, new MetaVal("MD_Utility_Building_Flag", "UtilityBuildingFlag", 50));
		outputFields.put(TAG_OUTPUT_POLE_STRUCTURE_AREA, new MetaVal("MD_Pole_Structure_Area", "PoleStructureArea", 50));
		outputFields.put(TAG_OUTPUT_POLE_STRUCTURE_FLAG, new MetaVal("MD_Pole_Structure_Flag", "PoleStructureFlag", 50));

// Utilities
		outputFields.put(TAG_OUTPUT_HVAC_COOLING_DETAIL, new MetaVal("MD_HVAC_Cooling_Detail", "HVACCoolingDetail", 50));
		outputFields.put(TAG_OUTPUT_HVAC_HEATING_DETAIL, new MetaVal("MD_HVAC_Heating_Detail", "HVACHeatingDetail", 50));
		outputFields.put(TAG_OUTPUT_HVAC_HEATING_FULE, new MetaVal("MD_HVAC_Heating_Fuel", "HVACHeatingFuel", 50));
		outputFields.put(TAG_OUTPUT_SEWAGE_USAGE, new MetaVal("MD_Sewage_Usage", "SewageUsage", 50));
		outputFields.put(TAG_OUTPUT_WATER_SOURCE, new MetaVal("MD_Water_Source", "WaterSource", 50));
		outputFields.put(TAG_OUTPUT_MOBIL_HOME_HOOKUP_FLAG, new MetaVal("MD_Mobile_Home_Hookup_Flag", "MobileHomeHookupFlag", 50));

// Parking
		outputFields.put(TAG_OUTPUT_RV_PARKING_FLAG, new MetaVal("MD_RV_Parking_Flag", "RVParkingFlag", 50));
		outputFields.put(TAG_OUTPUT_PARKING_SPACE_COUNT, new MetaVal("MD_Parking_Space_Count", "ParkingSpaceCount", 50));
		outputFields.put(TAG_OUTPUT_DRIVEWAY_AREA, new MetaVal("MD_Driveway_Area", "DrivewayArea", 50));
		outputFields.put(TAG_OUTPUT_DRIVEWAY_MATERIAL, new MetaVal("MD_Driveway_Material", "DrivewayMaterial", 50));

// YardGardenInfo
		outputFields.put(TAG_OUTPUT_TOPOGRAPHY_CODE, new MetaVal("MD_Topography_Code", "TopographyCode", 50));
		outputFields.put(TAG_OUTPUT_FENCE_CODE, new MetaVal("MD_Fence_Code", "FenceCode", 50));
		outputFields.put(TAG_OUTPUT_FENCE_AREA, new MetaVal("MD_Fence_Area", "FenceArea", 50));
		outputFields.put(TAG_OUTPUT_COURTYARD_FLAG, new MetaVal("MD_Courtyard_Flag", "CourtyardFlag", 50));
		outputFields.put(TAG_OUTPUT_COURTYARD_AREA, new MetaVal("MD_Courtyard_Area", "CourtyardArea", 50));
		outputFields.put(TAG_OUTPUT_ARBOR_PERGOLA_FLAG, new MetaVal("MD_Arbor_Pergola_Flag", "ArborPergolaFlag", 50));
		outputFields.put(TAG_OUTPUT_SPRINKLERS_FLAG, new MetaVal("MD_Sprinklers_Flag", "SprinklersFlag", 50));
		outputFields.put(TAG_OUTPUT_GOLF_COURSE_GREEN_FLAG, new MetaVal("MD_Golf_Course_Green_Flag", "GolfCourseGreenFlag", 50));
		outputFields.put(TAG_OUTPUT_TENNIS_COURT_FLAG, new MetaVal("MD_Tennis_Court_Flag", "TennisCourtFlag", 50));
		outputFields.put(TAG_OUTPUT_SPORTS_COURSE_FLAG, new MetaVal("MD_Sports_Court_Flag", "SportsCourtFlag", 50));
		outputFields.put(TAG_OUTPUT_ARENA_FLAG, new MetaVal("MD_Arena_Flag", "ArenaFlag", 50));
		outputFields.put(TAG_OUTPUT_WATER_FEATURE_FLAG, new MetaVal("MD_Water_Feature_Flag", "WaterFeatureFlag", 50));
		outputFields.put(TAG_OUTPUT_POND_FLAG, new MetaVal("MD_Pond_Flag", "PondFlag", 50));
		outputFields.put(TAG_OUTPUT_BOAT_LIFT_FLAG, new MetaVal("MD_Boat_Lift_Flag", "BoatLiftFlag", 50));

// Estimated Value
		outputFields.put(TAG_OUTPUT_ESTIMATED_VALUE, new MetaVal("MD_Estimated_Value", "EstimatedValue", 50));
		outputFields.put(TAG_OUTPUT_ESTIMATED_MIN_VALUE, new MetaVal("MD_Estimated_Min_Value", "EstimatedMinValue", 50));
		outputFields.put(TAG_OUTPUT_ESTIMATED_MAX_VALUE, new MetaVal("MD_Estimated_Max_Value", "EstimatedMaxValue", 50));
		outputFields.put(TAG_OUTPUT_CONFIDENCE_SCORE, new MetaVal("MD_Confidence_Score", "ConfidenceScore", 50));
		outputFields.put(TAG_OUTPUT_VALUATION_DATE, new MetaVal("MD_Valuation_Date", "ValuationDate", 50));

// Shape
		outputFields.put(TAG_OUTPUT_WELL_KNOWN_TEXT, new MetaVal("MD_Lot_Shape", "WellKnownText", -1));
	}
}
