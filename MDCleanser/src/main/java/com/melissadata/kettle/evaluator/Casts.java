package com.melissadata.kettle.evaluator;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;



import com.melissadata.kettle.evaluator.EvalItem.CastFunction;
import com.melissadata.kettle.evaluator.EvalItem.eDataType;
import org.pentaho.di.core.Const;

public class Casts {

	protected static class CastRec {
		public String CastType;
		public CastFunction Function;
		public CastFunction TestFunction;
		public int ParameterCount;

		public CastRec(String dataType, CastFunction evalFunction, CastFunction testFunction, int paramCount) {
			CastType = dataType;
			Function = evalFunction;
			TestFunction = testFunction;
			ParameterCount = paramCount;
		}
	}

	private static class EvalCastDT_DECIMAL extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if (param1.isString()) {
				try {
					return new EvalItem(Double.parseDouble(param1.getString()));
				} catch (NumberFormatException e) {
					System.out.println("Warn - Number Format Exception \"" + param1.value + "\" to Double defaults to 0");
					//throw new EvaluatorException("Error Number Format Exception \"" + param1.value + "\" to Double");
					return new EvalItem(Double.parseDouble("0"));
				}
			}
			if (param1.isInt())
				return new EvalItem((double) param1.getInt());
			if (param1.isDouble())
				return new EvalItem(param1.getDouble());
			if(param1.isBigNumber())
				return new EvalItem(param1.getDouble());

			throw new EvaluatorException("Error can not cast " + param1.dataType + " to Double");
		}
	}

	private static class EvalCastDT_NUMERIC extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if (param1.isString())
				try {
					return new EvalItem(Integer.parseInt(param1.getString()));
				} catch (NumberFormatException e) {
					System.out.println("Warn - Number Format Exception \"" + param1.value + "\" to Int default 0");
					return new EvalItem(Integer.parseInt("0"));
				}
			if (param1.isInt())
				return new EvalItem(param1.getInt());
			if (param1.isDouble())
				return new EvalItem(param1.getInt());
			if(param1.isBigNumber())
				return new EvalItem(param1.getInt());

			throw new EvaluatorException("Error can not cast " + param1.dataType + " to NUMERIC");
		}
	}


	private static class EvalCastDT_STR extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString()){
				return new EvalItem(param1.getString());
			}
			if(param1.isInt()){
				return new EvalItem(String.valueOf(param1.getInt()));
			}
			if(param1.isDouble()){
				return new EvalItem(String.valueOf(param1.getDouble()));
			}
			if(param1.isBigNumber()){
				return new EvalItem(String.valueOf(param1.getBigNumber().toString()));
			}
			if(param1.isBoolean()){
				return new EvalItem(String.valueOf(param1.getBool()));
			}
			if(param1.isTimeStamp()){
				return new EvalItem(param1.getTimestamp().toString());
			}
			if(param1.isDateTime()){
				return new EvalItem(param1.dateFormat.format(param1.getDate().getTime()));
			}
			if(param1.isInetAddress()){
				return new EvalItem(param1.getInetAddress().getHostName());
				//return new EvalItem(param1.getInetAddress().getCanonicalHostName());
			}
			if(param1.isNull()){
				return new EvalItem("");
			}
			
			
			
			throw new EvaluatorException("Error can not cast " + param1.dataType + " to String");
		}

	}

	private static class EvalCastDT_NULL extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			return new EvalItem();
		}

	}

	private static class EvalCastDT_DATE extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {

			if (param1.isString()) {
				Date date = new Date();;
				try {
					String rr = param1.getString();
				//	System.out.println(" -- PARSE : " + rr);
				//	System.out.println(" -- Format : " + param2.dateFormat.toPattern());
					 if(!Const.isEmpty(rr))
						 date = param2.dateFormat.parse(rr/*param1.getString()*/);

				} catch (ParseException e) {
					throw new EvaluatorException("Unable to parse Date " + e.toString());
				}

				return new EvalItem(date, param2.dateFormat.toPattern());

			} else if (param1.dataType == eDataType.DT_NUMERIC) {

				Date date;
				try {

					date = param2.dateFormat.parse(String.valueOf(param1.getInt()));
				} catch (ParseException e) {
					throw new EvaluatorException("Unable to parse Date " + e.getMessage());
				}

				return new EvalItem(date, param2.dateFormat.toPattern());
			} else {
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to Date");
			}
		}
	}

	private static class EvalCastDT_BOOL extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			
			if (param1.isString()) {
				return new EvalItem(Boolean.parseBoolean(param1.getString()));
			} else if (param1.isInt()) {
				return new EvalItem(param1.getInt() != 0);
			} else if (param1.isDouble() ) {
				return new EvalItem(param1.getDouble() != 0);
			} else if (param1.isBoolean() ) {
				return param1;
			} else if (param1.isBigNumber()) {
				return new EvalItem(param1.getBigNumber().doubleValue() != 0);
			} else {
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to Boolean");
			}
		}

	}
	
	private static class EvalCastDT_BIGNUMBER extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			
			if (param1.isString()) {
				return new EvalItem(new BigDecimal(param1.getString()));
			} else if (param1.isInt()) {
				return new EvalItem(new BigDecimal(String.valueOf(param1.getInt())));
			} else if (param1.isDouble() ) {
				return new EvalItem(new BigDecimal(String.valueOf(param1.getDouble())));
			} else if (param1.isBigNumber()) {
				return new EvalItem(param1.getBigNumber());
			} else {
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to BigNumber");
			}
		}

	}
	
	private static class EvalCastDT_INETADDRESS extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			
			if (param1.isString()) {
				try {
					return new EvalItem((Inet4Address) Inet4Address.getByName(param1.getString()));
				} catch (UnknownHostException e) {
					throw new EvaluatorException(" Can't cast " + param1.getString() + " to InetAddress: " + e.getMessage());
				}
			} else if (param1.isInetAddress()){
				return param1;
			} else {
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to BigNumber");
			}
		}

	}
	
	private static class EvalCastDT_TIMESTAMP extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {

			if (param1.isString()) {
				return new EvalItem(Timestamp.valueOf(param1.getString()));
			} else if (param1.isInt()) {
				return new EvalItem(new Timestamp(param1.getInt()));
			} else if (param1.isDateTime()) {
				return new EvalItem(new Timestamp(param1.getDate().getTime()));
			} else if(param1.isTimeStamp()){
				return param1;
			}
			
			throw new EvaluatorException(" Can't cast " + param1.dataType + " to TimeStamp");

		}

	}

	// TEST FUNCTIONS

	private static class TestCastDT_DECIMAL extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString() || param1.isNumeric())
				return new EvalItem((double)0);
			else
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to Double");
		}

	}

	private static class TestCastDT_NUMERIC extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString() || param1.isNumeric())
				return new EvalItem(0);
			else
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to NUMERIC");
		}

	}
	
	private static class TestCastDT_BIGNUMBER extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString() || param1.isNumeric())
				return new EvalItem(new BigDecimal("0"));
			else
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to BigNumber");
		}

	}

	private static class TestCastDT_COLUMN extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString() || param1.isNumeric())
				return new EvalItem("");
			else
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to Column");
		}

	}

	private static class TestCastDT_STR extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			return new EvalItem("");
		}

	}

	private static class TestCastDT_NULL extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			return new EvalItem();
		}

	}

	private static class TestCastDT_DATE extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString() || param1.isInt() || param1.isTimeStamp()){
				return new EvalItem(new Date(),"");
			}else{
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to Date");
			}
		}

	}

	private static class TestCastDT_BOOL extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString() || param1.isNumeric() || param1.isBoolean())
				return new EvalItem(true);
			else
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to Bool");
		}

	}
	
	private static class TestCastDT_TIMESTAMP extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString() || param1.isInt() || param1.isDateTime())
				return new EvalItem(new Timestamp(Calendar.getInstance().getTime().getTime()));
			else
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to TimeStamp");
		}

	}
	
	private static class TestCastDT_INETADDRESS extends CastFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2) throws EvaluatorException {
			if(param1.isString())
				try {
					return new EvalItem((Inet4Address) Inet4Address.getByName("127.0.0.1"));
				} catch (UnknownHostException e) {
					throw new EvaluatorException("Can't cast " + param1.dataType + " to InetAddress: UnknownHost" );
				}
			else
				throw new EvaluatorException(" Can't cast " + param1.dataType + " to InetAddress");
		}

	}

	public static EvalCastDT_DECIMAL EvalCastDT_DECIMAL = new EvalCastDT_DECIMAL();
	public static EvalCastDT_NUMERIC EvalCastDT_NUMERIC = new EvalCastDT_NUMERIC();
	public static EvalCastDT_STR EvalCastDT_STR = new EvalCastDT_STR();
	public static EvalCastDT_NULL EvalCastDT_NULL = new EvalCastDT_NULL();
	public static EvalCastDT_DATE EvalCastDT_DATE = new EvalCastDT_DATE();
	public static EvalCastDT_BOOL EvalCastDT_BOOL = new EvalCastDT_BOOL();
	public static EvalCastDT_BIGNUMBER EvalCastDT_BIGNUMBER = new EvalCastDT_BIGNUMBER();
	public static EvalCastDT_INETADDRESS EvalCastDT_INETADDRESS = new EvalCastDT_INETADDRESS();
	public static EvalCastDT_TIMESTAMP EvalCastDT_TIMESTAMP = new EvalCastDT_TIMESTAMP();

	public static TestCastDT_DECIMAL TestCastDT_DECIMAL = new TestCastDT_DECIMAL();
	public static TestCastDT_NUMERIC TestCastDT_NUMERIC = new TestCastDT_NUMERIC();
	public static TestCastDT_COLUMN TestCastDT_COLUMN = new TestCastDT_COLUMN();
	public static TestCastDT_STR TestCastDT_STR = new TestCastDT_STR();
	public static TestCastDT_NULL TestCastDT_NULL = new TestCastDT_NULL();
	public static TestCastDT_DATE TestCastDT_DATE = new TestCastDT_DATE();
	public static TestCastDT_BOOL TestCastDT_BOOL = new TestCastDT_BOOL();
	
	public static TestCastDT_BIGNUMBER TestCastDT_BIGNUMBER = new TestCastDT_BIGNUMBER();
	public static TestCastDT_TIMESTAMP TestCastDT_TIMESTAMP = new TestCastDT_TIMESTAMP();
	public static TestCastDT_INETADDRESS TestCastDT_INETADDRESS = new TestCastDT_INETADDRESS();

}
