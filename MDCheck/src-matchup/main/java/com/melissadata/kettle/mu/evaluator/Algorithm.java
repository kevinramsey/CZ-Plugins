package com.melissadata.kettle.mu.evaluator;

import org.pentaho.di.core.exception.KettleException;



public class Algorithm  implements Cloneable, Comparable<Algorithm>{

	private int index;
	public AlgorithmType algorithmType;
	private String expression;
	private String option; // i.e highest or Lowest
	private boolean selected;
	
	
	
	public enum AlgorithmType {
		LASTUPDATED("Last Updated"),
		MOSTCOMPLETE("Most Complete"),
		DATAQUALITYSCORE("Data Quality Score"),
		CUSTOM("Custom")
		;
		
		private String description;

		private AlgorithmType(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return getDescription();
		}

		public String getDescription() {
			return description;
		}

		public static AlgorithmType decode(String value) throws KettleException {
			try {
				return AlgorithmType.valueOf(value);
			} catch (Exception e) {
				throw new KettleException("Algorythm Unknown: "/*BaseMessages.getString(PKG, "MDCheckMeta.Countries.Unknown")*/ + value, e);
			}
		}

		public String encode() {
			return name();
		}
	}
	
	
	public Algorithm(AlgorithmType type, String exp, String opt, int index, boolean selected){
		this.expression = exp;
		this.algorithmType = type;
		this.index = index;
		this.option = opt;
		this.selected = selected;
		
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public AlgorithmType getAlgoType() {
		return algorithmType;
	}

	public void setAlgoType(AlgorithmType algoType) {
		this.algorithmType = algoType;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int compareTo(Algorithm compAlg) {
		int compIndex = compAlg.getIndex();
		return this.getIndex() - compIndex;
	}
	
	@Override
	public Algorithm clone() throws CloneNotSupportedException{
		
		 return (Algorithm)super.clone();
	
	}

	public String toString(){
		String alString = "";
		alString = "Algorithim=" + algorithmType.getDescription() + " : Expression=" + expression+ " : Option=" + option+ " : Index=" + index+ " : Selected=" + selected;

		return alString;
	}

	public boolean isValid(){
		if(isSelected() && (expression == null || expression.isEmpty())){
			return false;
		}
		return true;
	}

}
