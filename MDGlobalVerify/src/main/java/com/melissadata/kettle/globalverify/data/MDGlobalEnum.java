package com.melissadata.kettle.globalverify.data;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.mdName;
import com.melissadata.kettle.globalverify.MDGlobalMeta;

public class MDGlobalEnum {
	public enum GenderAggression {
		Aggressive(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Aggressive"), 1),
		Neutral(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Neutral"), 2),
		Conservative(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Conservative"), 3), ;
		public static GenderAggression decode(String value) throws KettleException {
			try {
				// Handle old method of storing name order hint by web ranking
				int rank = Integer.valueOf(value);
				for (GenderAggression genderAggression : GenderAggression.values()) {
					if (genderAggression.rank == rank)
						return genderAggression;
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return GenderAggression.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Unknown") + value, e);
			}
		}
		private String	description;

		private int		rank;

		private GenderAggression(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.Aggression getMDAggression() {
			switch (this) {
				case Aggressive:
					return mdName.Aggression.Aggressive;
				case Neutral:
					return mdName.Aggression.Neutral;
				case Conservative:
					return mdName.Aggression.Conservative;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.GenderAggression.Unknown") + this);
		}

		public int getRank() {
			return rank;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum GenderPopulation {
		BiasTowardMale(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.BiasTowardMale"), 1),
		EvenlySplit(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.EvenlySplit"), 2),
		BiasTowardFemale(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.BiasTowardFemale"), 3), ;
		public static GenderPopulation decode(String value) throws KettleException {
			// Handle old method of storing name order hint by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (GenderPopulation genderPopulation : GenderPopulation.values()) {
					if (genderPopulation.rank == rank)
						return genderPopulation;
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return GenderPopulation.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.Unknown") + value, e);
			}
		}
		private String	description;

		private int		rank;

		private GenderPopulation(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.Population getMDPopulation() {
			switch (this) {
				case BiasTowardMale:
					return mdName.Population.Male;
				case EvenlySplit:
					return mdName.Population.Mixed;
				case BiasTowardFemale:
					return mdName.Population.Female;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.GenderPopulation.Unknown") + this);
		}

		public int getRank() {
			return rank;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum MiddleNameLogic {
		ParseLogic(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.ParseLogic"), 1),
		HyphenatedLast(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.HyphenatedLast"), 2),
		MiddleName(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.MiddleName"), 3), ;
		public static MiddleNameLogic decode(String value) throws KettleException {
			// Handle old method of storing by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (MiddleNameLogic logic : MiddleNameLogic.values()) {
					if (logic.rank == rank)
						return logic;
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return MiddleNameLogic.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.Unknown") + value, e);
			}
		}
		private String	description;

		private int		rank;

		private MiddleNameLogic(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.MiddleNameLogic getMDMiddleNameLogic() {
			switch (this) {
				case ParseLogic:
					return mdName.MiddleNameLogic.ParseLogic;
				case HyphenatedLast:
					return mdName.MiddleNameLogic.HyphenatedLast;
				case MiddleName:
					return mdName.MiddleNameLogic.MiddleName;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.MiddleNameLogic.Unknown") + this);
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum NameOrderHint {
		DefinitelyFull(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.DefinitelyFull"), 1),
		VeryLikelyFull(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.VeryLikelyFull"), 2),
		ProbablyFull(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.ProbablyFull"), 3),
		Varying(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.Varying"), 4),
		ProbablyInverse(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.ProbablyInverse"), 5),
		VeryLikelyInverse(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.VeryLikelyInverse"), 6),
		DefinitelyInverse(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.DefinitelyInverse"), 7),
		MixedFirstName(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.MixedFirstName"), 8),
		MixedLastName(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.MixedLastName"), 9), ;
		public static NameOrderHint decode(String value) throws KettleException {
			// Handle old method of storing name order hint by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (NameOrderHint nameOrderHint : NameOrderHint.values()) {
					if (nameOrderHint.rank == rank)
						return nameOrderHint;
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderRank.Unknown") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return NameOrderHint.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.NameOrderHint.Unknown") + value, e);
			}
		}
		private String	description;

		private int		rank;

		private NameOrderHint(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		public String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.NameHints getMDNameHint() {
			switch (this) {
				case DefinitelyFull:
					return mdName.NameHints.DefinitelyFull;
				case VeryLikelyFull:
					return mdName.NameHints.VeryLikelyFull;
				case ProbablyFull:
					return mdName.NameHints.ProbablyFull;
				case Varying:
					return mdName.NameHints.Varying;
				case ProbablyInverse:
					return mdName.NameHints.ProbablyInverse;
				case VeryLikelyInverse:
					return mdName.NameHints.VeryLikelyInverse;
				case DefinitelyInverse:
					return mdName.NameHints.DefinitelyInverse;
				case MixedFirstName:
					return mdName.NameHints.MixedFirstName;
				case MixedLastName:
					return mdName.NameHints.MixedLastName;
			}
			// This should never happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.NameHint.Unknown") + this);
		}

		public int getRank() {
			return rank;
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	public enum OutputPhoneFormat {
		FORMAT1(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format1")),
		FORMAT2(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format2")),
		FORMAT3(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format3")),
		FORMAT4(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format4")),
		FORMAT5(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format5")),
		FORMAT6(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Format6")), ;
		public static OutputPhoneFormat decode(String value) throws KettleException {
			try {
				return OutputPhoneFormat.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.PhoneFormat.Unknown") + value, e);
			}
		}

		private String	format;

		private OutputPhoneFormat(String format) {
			this.format = format;
		}

		public String encode() {
			return name();
		}

		public String getFormat() {
			return format;
		}

		@Override
		public String toString() {
			return getFormat();
		}
	}

	public enum Salutation {
		Formal(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Formal"), 1),
		Informal(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Informal"), 2),
		FirstLast(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.FirstLast"), 3),
		Slug(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Slug"), 4),
		Blank(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Blank"), 5), ;
		public static Salutation decode(String value) throws KettleException {
			// Handle old method of storing by web ranking
			try {
				int rank = Integer.valueOf(value);
				for (Salutation salutation : Salutation.values()) {
					if (salutation.rank == rank)
						return salutation;
				}
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.UnknownRank") + rank);
			} catch (NumberFormatException e) {
				// To be expected
			}
			// Just decode the name
			try {
				return Salutation.valueOf(value);
			} catch (Exception e) {
				throw new KettleException(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Unknown") + value, e);
			}
		}

		public static Salutation[] decodeArray(String value) throws KettleException {
			String values[] = value.split(",");
			Salutation[] salutations = new Salutation[values.length];
			for (int i = 0; i < values.length; i++) {
				salutations[i] = Salutation.decode(values[i]);
			}
			return salutations;
		}

		public static String encode(Salutation[] salutations) {
			StringBuffer buf = new StringBuffer();
			String sep = "";
			for (Salutation salutation : salutations) {
				buf.append(sep).append(salutation.encode());
				sep = ",";
			}
			return buf.toString();
		}

		private String	description;

		private int		rank;

		private Salutation(String description, int rank) {
			this.description = description;
			this.rank = rank;
		}

		private String encode() {
			return name();
		}

		public String getDescription() {
			return description;
		}

		public mdName.Salutations getMDSalutation() {
			switch (this) {
				case Formal:
					return mdName.Salutations.Formal;
				case Informal:
					return mdName.Salutations.Informal;
				case FirstLast:
					return mdName.Salutations.FirstLast;
				case Slug:
					return mdName.Salutations.Slug;
				case Blank:
					return mdName.Salutations.Blank;
			}
			// This shouldn't happen
			throw new RuntimeException(BaseMessages.getString(PKG, "MDCheckMeta.SalutationOrder.Unknown") + this);
		}

		@Override
		public String toString() {
			return getDescription();
		}
	}

	private static Class<?>	PKG	= MDGlobalMeta.class;
}
