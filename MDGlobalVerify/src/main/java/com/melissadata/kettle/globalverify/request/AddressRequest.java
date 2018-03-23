package com.melissadata.kettle.globalverify.request;

import java.util.HashSet;
import java.util.Set;

import com.melissadata.cz.support.IOMetaHandler;

/**
 * Structure for contact verification requests
 */
public class AddressRequest {

	public static class AddrResults {

		public Set<String> resultCodes                        = new HashSet<String>();
		//2 Output Address Parameters
		public String      Organization                       = null;//2.1     web 3.4
		public String      Address1                           = null;//2.2     web 3.5
		public String      Address2                           = null;//2.3     web 3.6
		public String      Address3                           = null;//2.4     web 3.7
		public String      Address4                           = null;//2.5     web 3.8
		public String      Address5                           = null;//2.6     web 3.9
		public String      Address6                           = null;//2.7     web 3.10
		public String      Address7                           = null;//2.8     web 3.11
		public String      Address8                           = null;//2.9     web 3.12
		//
		public String      CountryName                        = null;//9.2     web 4.4
		public String      DependentLocality                  = null;//7.3     web 3.15
		public String      Locality                           = null;//7.5     web 3.16
		public String      AdministrativeArea                 = null;//7.1     web 3.18
		public String      PostalCode                         = null;//6.2     web 3.19
		public String      DeliveryLine                       = null;//2.10
		public String      FormattedAddress                   = null;//2.11     web 3.3
		public String      AddressTypeCode                    = null;//8.2
		public String      AddressKey                         = null;//        web 4.2
		// 3 Parsed Sub-Premises Parameters
		public String      Building                           = null;//3.1      web 4.21
		public String      SubBuilding                        = null;//3.2
		public String      SubBuildingNumber                  = null;//3.3
		public String      SubBuildingType                    = null;//3.4
		public String      SubPremises                        = null;//3.5     web 3.13
		public String      SubPremisesNumber                  = null;//3.6     web 4.25
		public String      SubPremisesType                    = null;//3.7     web 4.24
		public String      SubPremisesLevel                   = null;//3.8
		public String      SubPremisesLevelNumber             = null;//3.9
		public String      SubPremisesLevelType               = null;//3.10
		//4 Parsed Thoroughfare Parameters
		public String      Premises                           = null;//4.1
		public String      PremisesNumber                     = null;//4.2     web 4.23
		public String      PremisesType                       = null;//4.3     web 4.22
		public String      Thoroughfare                       = null;//4.4     web 4.9
		public String      ThoroughfareLeadingType            = null;//4.5     web 4.11
		public String      ThoroughfareName                   = null;//4.6     web 4.12
		public String      ThoroughfarePostDirection          = null;//4.7     web 4.14
		public String      ThoroughfarePreDirection           = null;//4.8     web 4.10
		public String      ThoroughfareTrailingType           = null;//4.9     web 4.13
		public String      ThoroughfareTypeAttached           = null;//4.10
		//5 Parsed Dependent Thoroughfare Columns
		public String      DependentThoroughfare              = null;//5.1     web 4.15
		public String      DependentThoroughfareLeadingType   = null;//5.2     web 4.17
		public String      DependentThoroughfareName          = null;//5.3     web 4.18
		public String      DependentThoroughfarePostDirection = null;//5.4     web 4.20
		public String      DependentThoroughfarePreDirection  = null;//5.5     web 4.16
		public String      DependentThoroughfareTrailingType  = null;//5.6     web 4.19
		public String      DependentThoroughfareTypeAttached  = null;//5.7
		//6 Parsed Postal Facility Columns
		public String      PostBox                            = null;//6.1     web 4.26
		public String      PersonalID                         = null;//6.3
		public String      PostOfficeLocation                 = null;//6.4
		//7 Parsed Regional Columns
		public String      CountyName                         = null;//7.2
		public String      DoubleDependentLocality            = null;//7.4     web 3.14
		public String      SubAdministrativeArea              = null;//7.6     web 3.17
		public String      SubNationalArea                    = null;//7.7     web 4.3
		//8 Extra Output Address Parameters
		public String      Latitude                           = null;//8.3     web 4.27
		public String      Longitude                          = null;//8.4     web 4.28
		//9 Extra Output CountryName Parameters
		public String      CountryCode                        = null;//9.1
		public String      CountrySubdivisionCode             = null;
		public String      CountryTimeZone                    = null;
		public String      CountryUTC                         = null;
		public String      CountryISOAlpha2                   = null;//9.3     web 4.5
		public String      CountryISOAlpha3                   = null;//9.4     web 4.6
		public String      CountryISONumeric                  = null;//9.5     web 4.7
		public String      CountryFormalName                  = null;//9.6
		public boolean     valid                              = false;

		public String toString() {

			String resultString = "Org:" + this.Organization + "|" + "Addr:" + this.Address1 + "|" + "CountryCode:" + this.CountryCode + "|" + "DeliveryLine:" + this.DeliveryLine + "|" + "Premises:" + this.Premises;

			return resultString;
		}
	}

	public AddrResults addrResults = null;

	public AddressRequest(IOMetaHandler ioData, Object[] inputData) {

	}
}
