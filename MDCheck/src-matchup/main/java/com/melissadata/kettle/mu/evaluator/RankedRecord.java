package com.melissadata.kettle.mu.evaluator;

import java.util.Comparator;
import java.util.Date;

public class RankedRecord implements Comparable<RankedRecord> {

	public Object[] arRecord;
	public EvalItem sortValue;
	public String sortType;
	public int index;

	public RankedRecord(Object[] rec, EvalItem item, int index) {
		
		this.arRecord = rec;
		this.sortValue = item;
		this.sortType = getSortType(item);
		this.index = index;
	}

	public RankedRecord(Object fieldVal) {
		this.arRecord = new Object[] { fieldVal };
		this.sortValue = null;
		this.sortType = null;
		this.index = 0;
	}

	public RankedRecord(Object fieldVal, EvalItem item) {
		this.arRecord = new Object[] { fieldVal };
		this.sortValue = item;
		this.sortType = getSortType(item);
		this.index = 0;
	}

	public String getSortType(EvalItem item) {

		if (item.isNumeric())
			return "NUMERIC";
		else if (item.isString())
			return "STRING";
		else if (item.isCalendarType())
			return "DATE";
		else
			return "NONE";
	}

	public int compareTo(RankedRecord compareRec) {
		// Not used
		return 0;//
	}

	public static Comparator<RankedRecord> NumericDecendingComparator = new Comparator<RankedRecord>() {

		public int compare(RankedRecord rec1, RankedRecord rec2) {
			double rec1val = rec1.sortValue.getDouble();
			double rec2val = rec2.sortValue.getDouble();

			double diff = rec2val - rec1val;
			//descending order
			if(diff > 0)
				return 1;
			if(diff < 0)
				return -1;
			
			return 0;// rec2val - rec1val;
		}
	};

	public static Comparator<RankedRecord> NumericAscendingComparator = new Comparator<RankedRecord>() {

		public int compare(RankedRecord rec1, RankedRecord rec2) {
			
			double rec1val = rec1.sortValue.getDouble();
			double rec2val = rec2.sortValue.getDouble();

			double diff = rec1val - rec2val;
			//ascending order
			if(diff > 0)
				return 1;
			if(diff < 0)
				return -1;
			
			return 0;// rec2val - rec1val;
	
		}
	};

	public static Comparator<RankedRecord> LexicalAscendingComparator = new Comparator<RankedRecord>() {

		public int compare(RankedRecord rec1, RankedRecord rec2) {
			String recString1 = rec1.sortValue.getString();
			String recString2 = rec2.sortValue.getString();

			//ascending order
			return recString1.compareTo(recString2);
		}
	};

	public static Comparator<RankedRecord> LexicalDescendingComparator = new Comparator<RankedRecord>() {

		public int compare(RankedRecord rec1, RankedRecord rec2) {
			String recString1 = rec1.sortValue.getString();
			String recString2 = rec2.sortValue.getString();

			//descending order
			return recString2.compareTo(recString1);
		}
	};

	public static Comparator<RankedRecord> DateAscendingComparator = new Comparator<RankedRecord>() {

		public int compare(RankedRecord rec1, RankedRecord rec2) {

			Date cal1 = rec1.sortValue.getCalendarType();
			Date cal2 = rec2.sortValue.getCalendarType();


			//ascending order
			return cal1.compareTo(cal2);

		}
	};

	public static Comparator<RankedRecord> DateDescendingComparator = new Comparator<RankedRecord>() {

		public int compare(RankedRecord rec1, RankedRecord rec2) {
			Date cal1 = rec1.sortValue.getCalendarType();
			Date cal2 = rec2.sortValue.getCalendarType();
	

			//descending order
			return cal2.compareTo(cal1);

		}
	};

}
