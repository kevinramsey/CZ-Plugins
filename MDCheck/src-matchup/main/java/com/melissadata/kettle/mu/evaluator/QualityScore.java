package com.melissadata.kettle.mu.evaluator;


import org.pentaho.di.core.exception.KettleException;



public class QualityScore  implements Cloneable, Comparable<QualityScore>{

	int index;
	QualityScoreType qualityType;
	boolean selected;
	String resultField;
	
	public enum QualityScoreType {
		DataQualityScore("Data Quality Score"){
			@Override
			public String getExpression(String field){
				return "DataQualityScore(" + field + ")";
			}
		},
		AddressQualityScore("Address Quality Score") {
			@Override
			public String getExpression(String field) {
				return  "AddressScore(" + field + ")";
			}
		},
		NameQualityScore("Name Quality Score") {
			@Override
			public String getExpression(String field) {
				return  "NameScore(" + field + ")";
			}
		},
		PhoneQualityScore("Phone Quality Score") {
			@Override
			public String getExpression(String field) {
				return  "PhoneScore(" + field + ")";
			}
		},
		EmailQualityScore("E-mail Quality Score") {
			@Override
			public String getExpression(String field) {
				return  "EmailScore(" + field + ")";
			}
		},
		GeoCodeQualityScore("GeoCode Quality Score") {
			@Override
			public String getExpression(String field) {
				return  "GeoCodeScore(" + field + ")";
			}
		}
		;
		
		private String description;

		private QualityScoreType(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return getDescription();
		}

		public String getDescription() {
			return description;
		}

		public static QualityScoreType decode(String value) throws KettleException {
			try {
				return QualityScoreType.valueOf(value);
			} catch (Exception e) {
				throw new KettleException("Quality Rule Unknown"/*BaseMessages.getString(PKG, "MDCheckMeta.Countries.Unknown")*/ + value, e);
			}
		}

		public String encode() {
			return name();
		}
		
		public abstract String getExpression(String field);
	}
	
	
	public QualityScore(QualityScoreType type, String resultField, int index, boolean selected){
		this.qualityType = type;
		this.index = index;
		this.selected = selected;
		this.resultField = resultField;
		
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public QualityScoreType getQualityScoreType() {
		return qualityType;
	}

	public void setQualityScoreType(QualityScoreType qualType) {
		this.qualityType = qualType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getExpression() {
		return qualityType.getExpression(resultField);
	}

	

	public String getResultField() {
		return resultField;
	}

	public void setResultField(String resultField) {
		this.resultField = resultField;
	}

	public int compareTo(QualityScore compScore) {
		int compIndex = compScore.getIndex();
		return this.getIndex() - compIndex;
	}

	public String toString(){
		String result = "";
		result = qualityType + " : Index=" + index + " : Selected=" + selected + " : Result Field=" + resultField;


		return result;
	}

	@Override
	public QualityScore clone() throws CloneNotSupportedException{
		
		return (QualityScore)super.clone();
	}

}
