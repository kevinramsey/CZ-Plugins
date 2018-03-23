package com.melissadata.kettle.evaluator;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.melissadata.kettle.evaluator.EvaluatorException;


public class EvalItem implements Cloneable{

	
	public enum eTypes {
		Operator,
		Function,
		Constant,
		Column,
		Variable
	}
	
	public enum eDataType{
		DT_DECIMAL,
		DT_NUMERIC,
		DT_TIMESTAMP,
		DT_STR,
		DT_NULL,
		DT_DATE,
		DT_BOOL,
		DT_BINARY,
		DT_INTERNETADDRESS,
		DT_BIGNUMBER
		
		
	}

	public enum eOperators {
		Ternary,
		LogicalOr,
		LogicalAnd,
		BitwiseOr,
		BitwiseExclusiveOr,
		BitwiseAnd,
		Equal,
		NotEqual,
		Greater,
		GreaterEqual,
		Less,
		LessEqual,
		Plus,
		Minus,
		Multiply,
		Divide,
		Modulus,
		LogicalNot,
		BitwiseNot,
		Negative,
		Cast,
		LeftParenthesis,
		RightParenthesis,
		Comma,
		Function
	}

	public eTypes type;                        // Type of Item (Operator, Function, Constant, Column or Variable)
	public eOperators operator;                // [Operator] Operator type
	public eDataType dataType;                  // [Constant, Column, Variable] Type of data being stored 
	public Object value;                       // [Constant, Column, Variable] Value of data being stored
	public EvalFunction function;              // [Function, Operator] Evaluation function
	public EvalFunction testFunction;          // [Function, Operator] Test Evaluation function
	public int parameterCount;                 // [Function, Operator] Number of parameters
	public String variableName;                // [Variables, Columns] Name of variable

	public SimpleDateFormat dateFormat;// = new SimpleDateFormat("MMddyyyy");

	public CastFunction castFunction; // [Cast Operator] Evaluation function
	public CastFunction testCastFunction; // [Cast Operator] Test Evaluation function
	public String castParam1; // [Cast Operator] First cast parameter
	public int castParam2; // [Cast Operator] Second cast parameter
	
	@Override
	public String toString(){
		String eiString = "";
		
		eiString = "Type = " + type
				+ "\nOperator = " + operator 
				+ "\ndataType = " + dataType
				+ "\nvalue = " + value
				+ "\nfunction = " + function
				+ "\ntestFunction = " + testFunction
				+ "\nParamCount = " + parameterCount
				+ "\nvariable Name = " + variableName
				+ "\ndate Format = " + dateFormat
				+ "\ncast Function = " + castFunction
				+ "\ntest Cast Function = " + testCastFunction
				+ "\ncast Param 1 = " + castParam1
				+ "\ncast Param 2 = " + castParam2
				+ "\n"
				;
		
		
		return eiString;
	}
	// Constructors for various DataType's:
	public EvalItem() {
		this.type = eTypes.Constant;
		this.value = null;
		this.dataType = eDataType.DT_NULL;
	}

	public EvalItem(boolean boolVal) {
		this.type = eTypes.Constant;
		this.value = boolVal;
		this.dataType = eDataType.DT_BOOL;
	}

	public EvalItem(int intVal) {
		this.type = eTypes.Constant;
		this.value = intVal;
		this.dataType = eDataType.DT_NUMERIC;
	}

	public EvalItem(double dblVal) {
		this.type = eTypes.Constant;
		this.value = dblVal;
		this.dataType = eDataType.DT_DECIMAL;
	}

	public EvalItem(BigDecimal dblVal) {
		this.type = eTypes.Constant;
		this.value = dblVal;
		this.dataType = eDataType.DT_BIGNUMBER;
	}

	public EvalItem(String strVal) {
		this.type = eTypes.Constant;
		this.value = strVal;
		this.dataType = eDataType.DT_STR;
	}

	public EvalItem(Date dtVal, String dateFormat) {
		this.type = eTypes.Constant;
		this.value = dtVal;
		this.dateFormat = new SimpleDateFormat(dateFormat);
		this.dataType = eDataType.DT_DATE;
	}

	public EvalItem(Timestamp decVal) {
		this.type = eTypes.Constant;
		this.value = decVal;
		this.dataType = eDataType.DT_TIMESTAMP;
	}

	public EvalItem(Inet4Address decVal) {
		this.type = eTypes.Constant;
		this.value = decVal;
		this.dataType = eDataType.DT_INTERNETADDRESS;
	}

	public EvalItem(Byte[] decVal) {
		this.type = eTypes.Constant;
		this.value = decVal;
		this.dataType = eDataType.DT_BINARY;
	}

	// Constructors: Operator, Cast, Function:
	public EvalItem(eOperators oper, EvalFunction func, EvalFunction testFunc, String name, int cnt) {
		this.type = eTypes.Operator;
		this.operator = oper;
		this.function = func;
		this.testFunction = testFunc;
		this.value = name;
		this.parameterCount = cnt;
	}

	public EvalItem(CastFunction func, CastFunction testFunc, String name, int cnt, String param1, int param2) {
		this.type = eTypes.Operator;
		this.operator = eOperators.Cast;
		this.castFunction = func;
		this.testCastFunction = testFunc;
		this.value = name;
		this.parameterCount = cnt;
		this.castParam1 = param1;
		this.dateFormat = new SimpleDateFormat(param1);
		this.castParam2 = param2;
	}

	public EvalItem(CastFunction func, CastFunction testFunc, String name, int cnt) {
		this.type = eTypes.Operator;
		this.operator = eOperators.Cast;
		this.castFunction = func;
		this.testCastFunction = testFunc;
		this.value = name;
		this.parameterCount = cnt;
	}

	public EvalItem(EvalFunction func, EvalFunction testFunc, String name, int cnt) {
		this.type = eTypes.Function;
		this.operator = eOperators.Function;
		this.function = func;
		this.testFunction = testFunc;
		this.value = name;
		this.parameterCount = cnt;
	}

	// Constructor: Variable or Column:
	public EvalItem(eTypes type, String name, int dataType) {
		this.type = type;
		this.variableName = name;
		this.dataType = typeFromVmi(dataType);

	}

	private eDataType typeFromVmi(int vmiType) {

		switch (vmiType) {
		case 1:
			return eDataType.DT_DECIMAL;
		case 2:
			return eDataType.DT_STR;
		case 3:
			return eDataType.DT_DATE;
		case 4:
			return eDataType.DT_BOOL;
		case 5:
			return eDataType.DT_NUMERIC;
		case 6:
			return eDataType.DT_BIGNUMBER;
		case 8:
			return eDataType.DT_BINARY;
		case 9:
			return eDataType.DT_TIMESTAMP;
		case 10:
			return eDataType.DT_INTERNETADDRESS;
		default:
			return eDataType.DT_NULL;

		}

	}

	public EvalItem(eTypes type, String name, Object obj) {
		this.type = type;
		this.variableName = name;
		this.value = obj;

	}

	public void SetValue(Object value) {
		this.value = value;
	}

	public void SetColValue(Object value, eDataType type) {
		this.value = value;
		dataType = type;
	}

	public void SetVarValue(Object value) {
		this.value = value;
		dataType = eDataType.DT_STR;
	}

	public void setTestFunction(EvalFunction func) {
		testFunction = func;
	}

	public static abstract class EvalFunction {
		public abstract EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException;
	}

	public static abstract class CastFunction {
		public abstract EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException;
	}

	public boolean isNumeric() {
		return (isInt() || isDouble() || isBigNumber());
	}

	public boolean isBoolean() {
		return (dataType == eDataType.DT_BOOL);
	}

	public boolean isNull() {

		if (value == null)
			return true;
		else
			return false;

	}

	public boolean isInt() {
		return (dataType == eDataType.DT_NUMERIC);
	}

	public boolean isBinary() {
		return (dataType == eDataType.DT_BINARY);
	}

	public boolean isDouble() {
		return (dataType == eDataType.DT_DECIMAL);
	}

	public boolean isBigNumber() {
		return (dataType == eDataType.DT_BIGNUMBER);
	}

	public boolean isDateTime() {
		return (dataType == eDataType.DT_DATE);
	}

	public boolean isTimeStamp() {
		return (dataType == eDataType.DT_TIMESTAMP);
	}

	public boolean isCalendarType() {

		if (dataType == eDataType.DT_DATE || dataType == eDataType.DT_TIMESTAMP)
			return true;
		else
			return false;

	}

	public boolean isString() {
		return (dataType == eDataType.DT_STR);
	}

	public boolean isInetAddress() {
		return (dataType == eDataType.DT_INTERNETADDRESS);
	}

	public String getString() {

		if (isString() && value != null) {

			return value.toString();

		}

		return "";
	}

	public int getInt() {

		if(value == null)
			return 0;
		
		if (isInt()){
			
			if(value instanceof Long){
				return value != null ? ((Long) value).intValue() : 0;
			}
			
			return (Integer) value;
		}
		else if (isDouble()) {

			int d = ((Double) getDouble()).intValue();

			return d;
		} else if (isBigNumber())
			return ((BigDecimal) value).intValue();

		return 0;
	}

	public double getDouble() {
		if (isInt()) {
			int x = (Integer) value;
			return (double) x;
		} else if (isDouble())
			return (Double) value;
		else if (isBigNumber())
			return ((BigDecimal) value).doubleValue();

		return 0;
	}

	public BigDecimal getBigNumber() {
		if (isInt())
			return BigDecimal.valueOf((Integer) value);
		else if (isDouble())
			return BigDecimal.valueOf((Double) value);
		else if (isBigNumber())
			return (BigDecimal) value;

		return null;
	}

	public Timestamp getTimestamp() {
		if (isTimeStamp()) {

			return (Timestamp) value;
		} else
			return null;
	}

	public Date getCalendarType() {

		if (isTimeStamp()) {

			Timestamp ts = getTimestamp();
			if (ts == null)
				return null;

			return new Date(ts.getTime());

		} else if (isDateTime()) {

			return (Date) value;
		}

		return null;
	}

	public Date getDate() {
		if (!isDateTime())
			return null;

		return (Date) value;
	}

	public boolean getBool() {
		if (isBoolean()) {
			return (Boolean) value;
		}

		return false;
	}

	public Byte[] getBinary() {
		if (isBinary()) {
			return (Byte[]) value;
		}

		return null;
	}

	public Inet4Address getInetAddress() {
		if (isInetAddress()) {
			return (Inet4Address) value;
		}

		return null;
	}

	@Override
	public EvalItem clone() {
		EvalItem ei = null;
		try {
			ei = (EvalItem) super.clone();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return ei;
	}

}
