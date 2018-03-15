package com.melissadata.kettle.mu.evaluator;

import java.math.BigDecimal;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;

public class SurvivorField implements Cloneable {
	
	private static final String STACK_PREFIX = "[Stack Group";

	public enum ConsolidationMethod {

		FirstData("First Data") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				return values[0];
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return null;
			}
		},

		FirstNonEmpty("First Non Empty") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {

				for (Object obj : values) {
					if (obj != null) {
						return obj;
					}
				}
				return "";
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return null;
			}
		},

		Join("Join") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				String retVal = "";

				for (Object obj : values) {
					if(obj != null)
						retVal += obj.toString();
				}

				return retVal;
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return null;
			}
		},

		JoinWithSpaces("Join With Spaces") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {

				String retVal = "";
				for (Object obj : values) {
					if(obj != null)
						retVal += obj.toString() + " ";
				}

				return retVal.trim();
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return null;
			}
		},

		Add("Add") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				int intVal = 0;
				double dblVal = 0;
				BigDecimal bdVal = new BigDecimal(0);

				if (values[0] instanceof Integer || values[0] instanceof Long) {
					for (Object x : values) {
						if(x != null)
							intVal += (Integer) x;
					}
					return intVal;
				}

				if (values[0] instanceof Double) {
					for (Object x : values) {
						dblVal += (Double) x;
					}
					return dblVal;
				}

				if (values[0] instanceof BigDecimal) {
					for (Object x : values) {
						bdVal = bdVal.add((BigDecimal) x);

					}
					return bdVal;
				}
				return null;
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return null;
			}
		},

		Average("Average") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				int intVal = 0;
				double dblVal = 0;
				BigDecimal bdVal = new BigDecimal(0);

				if (values[0] instanceof Integer || values[0] instanceof Long) {
					for (Object x : values) {
						intVal += (Integer) x;
					}
					return intVal / values.length;
				}

				if (values[0] instanceof Double) {
					for (Object x : values) {
						dblVal += (Double) x;
					}
					return dblVal / values.length;
				}

				if (values[0] instanceof BigDecimal) {
					for (Object x : values) {
						bdVal = bdVal.add((BigDecimal) x);

					}
					return bdVal.divide(new BigDecimal(values.length));
				}
				throw new KettleException("Survivor Pass Thru \"Average\" can only be performd on Numeric expressions. Found: " + values[0].getClass());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return null;
			}
		},

		MostFrequent("Most Frequent") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {

				Object retVal = "";
				int count = 0;
				int highestCount = 0;

				for (Object val : values) {

					if (val != null) {
						count = 0;
						for (Object ck : values) {
							if (ck != null) {
								if (ck.equals(val)) {
									count++;
								}
							}
						}

						if (count > highestCount) {
							retVal = val;
							highestCount = count;
						}
					}
				}

				return retVal;
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return null;
			}
		},


		StackGroupA("Stack Group A") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		},

		StackGroupB("Stack Group B") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		},

		StackGroupC("Stack Group C") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		},

		StackGroupD("Stack Group D") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		},

		StackGroupE("Stack Group E") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		},

		StackGroupF("Stack Group F") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		},

		StackGroupG("Stack Group G") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		},

		StackGroupH("Stack Group H") {
			@Override
			public Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException {
				throw new KettleException("Invalid method call " + this.toString());
			}
			
			@Override
			public Object getStackGroup(Object[] values, int idx) throws KettleException {
				return values[idx];
			}
		};

		private String description;

		private ConsolidationMethod(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return getDescription();
		}

		public String getDescription() {
			return description;
		}

		public static ConsolidationMethod decode(String value) throws KettleException {
			try {
				return ConsolidationMethod.valueOf(value);
			} catch (Exception e) {
				throw new KettleException("ConsolidationMethod Unknown" + value, e);
			}
		}

		public static ConsolidationMethod fromString(String text) {
			if (text != null) {
				for (ConsolidationMethod b : ConsolidationMethod.values()) {
					if (text.equalsIgnoreCase(b.description)) {
						return b;
					}
				}
			}
			return null;
		}

		public String encode() {
			return name();
		}

		public abstract Object getConsolidatedField(Object[] values, EvalItem.eDataType dataType) throws KettleException;
		public abstract Object getStackGroup(Object[] values, int idx) throws KettleException;

	}

	private String              outputName;
	private ConsolidationMethod consolidationMethod;
	private String              source;
	private String              prioritization;
	private EvalItem.eDataType  dataType;
	private boolean             descending;
	private int stackIndex = -1;

	public SurvivorField(String outputName, ConsolidationMethod consolidationMethod, String source, EvalItem.eDataType dType, int stackIndex, String prioritization, boolean isDescending) {

		this.outputName = outputName;
		this.consolidationMethod = consolidationMethod;
		this.source = source;
		if (prioritization.isEmpty()) {
			this.prioritization = defaultPriortization();
		} else {
			this.prioritization = prioritization;
		}
		this.descending = isDescending;
		this.dataType = dType;
		this.stackIndex = stackIndex;
	}

	public SurvivorField(ValueMetaInterface vmi, int index) {

		this.outputName = vmi.getName();
		this.consolidationMethod = ConsolidationMethod.FirstData;
		this.source = vmi.getName();
		this.prioritization = defaultPriortization();
		this.dataType = typeFromVmi(vmi.getType());
		this.stackIndex = index;
	}

	public SurvivorField() {
		// Create empty survivor record
		this.outputName = "";
		this.consolidationMethod = ConsolidationMethod.FirstData;
		this.source = "";
		this.prioritization = defaultPriortization();
		this.dataType = EvalItem.eDataType.DT_STRING;

	}

	private EvalItem.eDataType typeFromVmi(int vmiType) {

		switch (vmiType) {
		case 1:
			return EvalItem.eDataType.DT_DOUBLE;
		case 2:
			return EvalItem.eDataType.DT_STRING;
		case 3:
			return EvalItem.eDataType.DT_DATE;
		case 4:
			return EvalItem.eDataType.DT_BOOL;
		case 5:
			return EvalItem.eDataType.DT_INT;
		case 6:
			return EvalItem.eDataType.DT_BIGNUMBER;
		case 8:
			return EvalItem.eDataType.DT_BINARY;
		case 9:
			return EvalItem.eDataType.DT_TIMESTAMP;
		case 10:
			return EvalItem.eDataType.DT_INTERNETADDRESS;
		default:
			return EvalItem.eDataType.DT_NULL;

		}

	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputNme) {
		this.outputName = outputNme;
	}

	public ConsolidationMethod getConsolidationMethod() {
		return consolidationMethod;
	}

	public void setConsolidationMethod(ConsolidationMethod consolidationMethod) {
		this.consolidationMethod = consolidationMethod;
	}

	public String getSource() {
		
		if(source.startsWith(STACK_PREFIX)){
			return source.substring(source.indexOf(":") + 1);
		}
		
		return source;
	}
	
	public String getDisplaySource() {
		if(source.startsWith(STACK_PREFIX)){
			return source.substring(0, source.indexOf(":"));
		}
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getPrioritization() {
		if(prioritization.startsWith(STACK_PREFIX)){
			return prioritization.substring(prioritization.indexOf(":") + 1);
		}
		
		return prioritization;
	}
	
	public String getDisplayPrioritization() {
		if(prioritization.startsWith(STACK_PREFIX)){
			return prioritization.substring(0, prioritization.indexOf(":"));
		}
		return prioritization;
	}

	public void setPrioritization(String prioritization) {
		this.prioritization = prioritization;
	}
	
	

	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean ascending) {
		this.descending = ascending;
	}

	public EvalItem.eDataType getDataType() {
		return dataType;
	}

	public int getValueMetaType() {

		switch (dataType) {
		case DT_BIGNUMBER:
			return 6;
		case DT_DOUBLE:
			return 1;
		case DT_INT:
			return 5;
		case DT_DATE:
			return 3;
		case DT_TIMESTAMP:
			return 9;
		case DT_STRING:
			return 2;
		case DT_BOOL:
			return 4;
		case DT_BINARY:
			return 8;
		case DT_INTERNETADDRESS:
			return 10;
		default:
			return -1;

		}

	}

	public void setDataType(EvalItem.eDataType dataType) {
		this.dataType = dataType;
	}

	public boolean isDateType() {
		return dataType == EvalItem.eDataType.DT_DATE || dataType == EvalItem.eDataType.DT_TIMESTAMP;
	}

	public boolean isStringType() {
		return dataType == EvalItem.eDataType.DT_STRING ;
	}


	@Override
	public SurvivorField clone() throws CloneNotSupportedException {
		return (SurvivorField) super.clone();
	}

	public String defaultPriortization() {
		return "Default(use GoldenRecord Selection Order)";
	}

	public int getStackIndex() {
		return stackIndex;
	}

	public void setStackIndex(int stackIndex) {
		this.stackIndex = stackIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurvivorField other = (SurvivorField) obj;
		if (outputName == null) {
			if (other.outputName != null)
				return false;
		} else if (!outputName.equals(other.outputName)){
			//System.out.println("THIS : " + outputName + "  Other : " + other.outputName );
			return false;
			
		}

		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;

		if (prioritization == null) {
			if (other.prioritization != null)
				return false;
		} else if (!prioritization.equals(other.prioritization))
			return false;

		if (consolidationMethod == null) {
			if (other.consolidationMethod != null)
				return false;
		} else if (!consolidationMethod.equals(other.consolidationMethod))
			return false;

		if (descending) {
			if (!other.descending)
				return false;
		} else {
			if (other.descending)
				return false;
		}

		return true;
	}

}
