package com.melissadata.kettle.evaluator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.melissadata.kettle.evaluator.EvalItem.EvalFunction;

public class Functions {

	protected static class FunctionRec {
		public String FunctionStr;
		public EvalFunction Function;
		public EvalFunction TestFunction;
		public int ParameterCount;

		public FunctionRec(String str, EvalFunction fun, EvalFunction testFun, int cnt) {
			FunctionStr = str;
			Function = fun;
			TestFunction = testFun;
			ParameterCount = cnt;
		}
	}

	// EVAL FUNCTIONS
	private static class EvalAbs extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: ABS(<numericExp>)");

			if (param1.isInt())
				return new EvalItem(Math.abs((Integer) param1.value));
			else if (param1.isDouble())
				return new EvalItem(Math.abs(param1.getDouble()));
			else if (param1.isBigNumber())
				return new EvalItem(param1.getBigNumber().abs());

			return new EvalItem();
		}
	}

	private static class EvalCeiling extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: CEILING(<numericExp>)");

			if (param1.isDouble())
				return new EvalItem(Math.ceil(param1.getDouble()));
			else if (param1.isBigNumber())
				return new EvalItem(param1.getBigNumber().setScale(0, RoundingMode.CEILING));
			else
				return param1;
		}
	}

	private static class EvalCodePoint extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) {
			// TODO Do we want to implement this ?
			return null;
		}
	}

	private static class EvalDateAdd extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isNumeric() || (!param1.isDateTime() || param1.isTimeStamp()))
				throw new EvaluatorException("Type error: DATEADD(<stringExp>, <numericExp>, <dateTimeExp>)");

			Calendar cal = Calendar.getInstance();

			if (param1.isDateTime()) {
				cal.setTimeInMillis(param1.getDate().getTime());

				if (param3.getString().equalsIgnoreCase("DAY"))
					cal.add(Calendar.DAY_OF_YEAR, param2.getInt());
				if (param3.getString().equalsIgnoreCase("MONTH"))
					cal.add(Calendar.MONTH, param2.getInt());
				if (param3.getString().equalsIgnoreCase("YEAR"))
					cal.add(Calendar.YEAR, param2.getInt());

				return new EvalItem(cal.getTime(), param1.dateFormat.toPattern());
			}

			if (param1.isTimeStamp()) {
				cal = Calendar.getInstance();
				cal.setTime(param1.getTimestamp());

				if (param3.getString().equalsIgnoreCase("DAY"))
					cal.add(Calendar.DAY_OF_YEAR, param2.getInt());
				if (param3.getString().equalsIgnoreCase("MONTH"))
					cal.add(Calendar.MONTH, param2.getInt());
				if (param3.getString().equalsIgnoreCase("YEAR"))
					cal.add(Calendar.YEAR, param2.getInt());

				return new EvalItem(new Timestamp(cal.getTime().getTime()));
			}

			return null;

		}
	}

	private static class EvalDateDiff extends EvalFunction {
		@SuppressWarnings("null")
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isCalendarType() || !param1.isCalendarType())
				throw new EvaluatorException("Type error: DATEDIFF(<stringExp>, <dateTimeExp>, <dateTimeExp>)");

			int dif = 0;

			Calendar cal1 = null;
			Calendar cal2 = null;

			if (param1.isDateTime())
				cal1.setTime(param1.getDate());
			else {
				cal1 = Calendar.getInstance();
				cal1.setTime(param1.getTimestamp());
			}

			if (param2.isDateTime())
				cal2.setTime(param2.getDate());
			else {
				cal2 = Calendar.getInstance();
				cal2.setTime(param2.getTimestamp());
			}

			if (param3.getString().equalsIgnoreCase("DAY"))
				dif = cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR);
			if (param3.getString().equalsIgnoreCase("MONTH"))
				dif = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
			if (param3.getString().equalsIgnoreCase("YEAR"))
				dif = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);

			return new EvalItem(dif);
		}
	}

	private static class EvalDatePart extends EvalFunction {
		@SuppressWarnings("null")
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isCalendarType())
				throw new EvaluatorException("Type error: DATEPART(<stringExp>, <dateTimeExp>)");

			Calendar cal = null;

			if (param1.isDateTime())
				cal.setTime(param1.getDate());
			else {
				cal = Calendar.getInstance();
				cal.setTime(param1.getTimestamp());
			}

			if (param2.getString().equalsIgnoreCase("DAY"))
				return new EvalItem(cal.get(Calendar.DAY_OF_YEAR));
			else if (param2.getString().equalsIgnoreCase("MONTH"))
				return new EvalItem(cal.get(Calendar.MONTH));
			else if (param2.getString().equalsIgnoreCase("YEAR"))
				return new EvalItem(cal.get(Calendar.YEAR));
			else
				throw new EvaluatorException("Date part error: Invalid date part \"" + param2.getString());

		}
	}

	private static class EvalDay extends EvalFunction {
		@SuppressWarnings("null")
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isCalendarType())
				throw new EvaluatorException("Type error: DAY(<dateTimeExp>)");

			Calendar cal = null;
			if (param1.isDateTime())
				cal.setTime(param1.getDate());
			else {
				cal = Calendar.getInstance();
				cal.setTime(param1.getTimestamp());
			}

			return new EvalItem(cal.get(Calendar.DAY_OF_MONTH));

		}
	}

	private static class EvalExp extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: EXP(<numericExp>)");

			return new EvalItem(Math.exp(param1.getDouble()));

		}
	}

	private static class EvalFindString extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: FINDSTRING(<stringExp>, <stringExp>, <numericExp>)");
			else if (param1.getInt() <= 0)
				throw new EvaluatorException("FINDSTRING: Occurrence must be greater than 0");

			return new EvalItem(param3.getString().indexOf(param2.getString(), param1.getInt() - 1) + 1);

		}
	}

	private static class EvalFloor extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: Floor(<numericExp>)");

			if (param1.isDouble())
				return new EvalItem(Math.floor((Double) param1.value));
			if (param1.isBigNumber()) {
				return new EvalItem(((BigDecimal) param1.value).setScale(0, RoundingMode.FLOOR));
			} else
				return param1;
		}
	}

	private static class EvalGetDate extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) {
			return new EvalItem(new Date(), "");
		}
	}

	private static class EvalGetUTCDate extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) {
			return new EvalItem(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime(), "");
		}
	}

	private static class EvalHex extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: HEX(<numericExp>)");

			if (param1.isInt())
				return new EvalItem(Integer.toHexString(param1.getInt()));
			if (param1.isDouble())
				return new EvalItem(Double.toHexString(param1.getDouble()));
			if (param1.isBigNumber())
				return new EvalItem(Double.toHexString(param1.getDouble()));

			return new EvalItem(Integer.toHexString(0));
		}
	}

	private static class EvalIsNull extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) {
			return new EvalItem(param1.isNull());
		}
	}
	
	private static class EvalIsIBAN extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) {
			return new EvalItem();
		}
	}

	private static class EvalLeft extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: LEFT(<stringExp>, <numericExp>)");
			else if (param1.getInt() < 0)
				throw new EvaluatorException("LEFT: Length must be greater than or equal to 0");

			int len = Math.min(param1.getInt(), ((String) param2.value).length());
			return new EvalItem(((String) param2.value).substring(0, len));
		}
	}

	private static class EvalLen extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (!param1.isString())
				throw new EvaluatorException("Type error: LEN(<stringExp>)  found: " + param1.dataType);

			return new EvalItem(param1.getString().length());
		}
	}

	private static class EvalLn extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: LN(<numericExp>)");
			else if (param1.getDouble() <= 0)
				throw new EvaluatorException("LN: Value must be greater than 0");
			return new EvalItem(Math.log(param1.getDouble()));
		}
	}

	private static class EvalLog extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: LOG(<numericExp>)");
			else if (param1.getDouble() <= 0)
				throw new EvaluatorException("LOG: Value must be greater than 0");
			return new EvalItem(Math.log10(param1.getDouble()));
		}
	}

	private static class EvalLower extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNumeric())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: LOWER(<stringExp>)");
			return new EvalItem(((String) param1.value).toLowerCase());
		}
	}

	private static class EvalLTrim extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			if (!param1.isString())
				throw new EvaluatorException("Type error: LTRIM(<stringExp>)");
			return new EvalItem(((String) param1.value).replaceAll("^\\s+", ""));
		}
	}

	private static class EvalMonth extends EvalFunction {
		@SuppressWarnings("null")
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isCalendarType())
				throw new EvaluatorException("Type error: Month(<dateTimeExp>)");

			Calendar cal = null;
			if (param1.isDateTime())
				cal.setTime(param1.getDate());
			else {
				cal = Calendar.getInstance();
				cal.setTime(param1.getTimestamp());
			}

			return new EvalItem(cal.get(Calendar.MONTH));
		}
	}

	private static class EvalNull extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) {
			return new EvalItem();
		}
	}

	private static class EvalPower extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isNumeric() || !param1.isNumeric())
				throw new EvaluatorException("Type error: POWER(<numericExp>, <numericExp>)");
			return new EvalItem(Math.pow(param2.getDouble(), param1.getDouble()));
		}
	}

	private static class EvalReplace extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isString() || !param1.isString())
				throw new EvaluatorException("Type error: REPLACE(<stringExp>, <stringExp>, <stringExp>)");
			return new EvalItem(param3.getString().replace(param2.getString(), param1.getString()));
		}
	}

	private static class EvalReplaceNull extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param2.isNull())
				return param1;
			else
				return param2;
		}
	}

	private static class EvalReplicate extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: REPLICATE(<stringExp>, <numericExp>)");
			else if (param1.getInt() < 0)
				throw new EvaluatorException("REPLICATE: Repeat value must be greater than or equal to 0");

			String retVal = "";
			for (int i = 0; i < param1.getInt(); i++)
				retVal += param2.getString();
			return new EvalItem(retVal);
		}
	}

	private static class EvalReverse extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: REVERSE(<stringExp>)");

			String retVal = "";
			for (int i = param1.getString().length() - 1; i >= 0; i--)
				retVal += param1.getString().toCharArray()[i];
			return new EvalItem(retVal);
		}
	}

	private static class EvalRight extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: RIGHT(<stringExp>, <numericExp>)");
			else if (param1.getInt() < 0)
				throw new EvaluatorException("RIGHT: Length must be greater than or equal to 0");

			int start = Math.max(0, Math.min(param2.getString().length(), param2.getString().length() - param1.getInt()));
			return new EvalItem(param2.getString().substring(start));
		}
	}

	private static class EvalRound extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isNumeric() || !param1.isNumeric())
				throw new EvaluatorException("Type error: ROUND(<numericExp>, <numericExp>)");

			double value = param2.getDouble();
			int precision = param1.getInt();

			int scale = (int) Math.pow(10, precision);
			if (param2.isDouble()) {

				return new EvalItem((double) Math.round(value * scale) / scale);
			} else {
				return new EvalItem((int) Math.round(value * scale) / scale);
			}

		}
	}

	private static class EvalRTrim extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			if (!param1.isString())
				throw new EvaluatorException("Type error: LTRIM(<stringExp>)");
			return new EvalItem(((String) param1.value).replaceAll("\\s+$", ""));
		}
	}

	private static class EvalSign extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: SIGN(<numericExp>)");

			if (param1.getDouble() > 0)
				return new EvalItem(1);
			else if (param1.getDouble() < 0)
				return new EvalItem(-1);
			else if (param1.getDouble() == 0)
				return new EvalItem(0);

			return null;
		}
	}

	private static class EvalSqrt extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: SQRT(<numericExp>)");
			else if (param1.getDouble() < 0)
				throw new EvaluatorException("SQRT: Value must be greater than or equal to 0");

			return new EvalItem(Math.sqrt(param1.getDouble()));
		}
	}

	private static class EvalSquare extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: SQUARE(<numericExp>)");
			return new EvalItem(param1.getDouble() * param1.getDouble());
		}
	}

	private static class EvalSubString extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isNumeric() || !param1.isNumeric())
				throw new EvaluatorException("Type error: SUBSTRING(<stringExp>, <numericExp>, <numericExp>)");
			else if (param2.getInt() <= 0)
				throw new EvaluatorException("SUBSTRING: Position must be greater than 0");
			else if (param1.getInt() < 0)
				throw new EvaluatorException("SUBSTRING: Length must be greater than or equal to 0");

			int start = Math.min(param2.getInt() - 1, ((String) param3.value).length());
			int len = Math.min(((String) param3.value).length() - start, param1.getInt());
			return new EvalItem(((String) param3.value).substring(start, len));
		}
	}

	private static class EvalToken extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			//TODO Figure out what this is supossed to do
			/*
			 * if (param1.isNull() || param2.isNull() || param3.isNull()) return
			 * new EvalItem(); else if (!param3.isString() || !param2.isString()
			 * || !param1.isNumeric()) throw new
			 * Exception("Type error: TOKEN(<stringExp>, <stringExp>, <numericExp>)"
			 * ); else if (param1.getInt() <= 0) throw new
			 * Exception("TOKEN: Occurrence must be greater than 0");
			 * 
			 * // String[] tokens =
			 * ((String)param3.Value).trim(((String)param2.Value
			 * ).toCharArray()).Split(((string)param2.Value).ToCharArray()); //
			 * if (param1.GetLong() > tokens.Length) //// return new EvalItem();
			 * // return new EvalItem(tokens[param1.GetLong() - 1],
			 * DataType.DT_WSTR);
			 */
			return null;
		}

	}

	private static class EvalTokenCount extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			//TODO Figure if this is used
			/*
			 * if (param1.IsNull() || param2.IsNull()) return new EvalItem();
			 * else if (!param2.IsString() || !param1.IsString()) throw new
			 * Exception("Type error: TOKENCOUNT(<stringExp>, <stringExp>)");
			 * 
			 * string[] tokens =
			 * ((string)param2.Value).Trim(((string)param1.Value
			 * ).ToCharArray()).Split(((string)param1.Value).ToCharArray());
			 * return new EvalItem(tokens.Length, DataType.DT_I4);
			 */
			return null;
		}
	}

	private static class EvalTrim extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: TRIM(<stringExp>)");
			return new EvalItem(((String) param1.value).trim());
		}
	}

	private static class EvalUpper extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNumeric())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: UPPER(<stringExp>)");
			return new EvalItem(((String) param1.value).toUpperCase());
		}
	}

	private static class EvalYear extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isCalendarType())
				throw new EvaluatorException("Type error: Year(<dateTimeExp>)");

			Calendar cal = null;
			if (param1.isDateTime()) {
				cal = Calendar.getInstance();
				cal.setTime(param1.getDate());
			} else {
				cal = Calendar.getInstance();
				cal.setTime(param1.getTimestamp());
			}

			return new EvalItem(cal.get(Calendar.YEAR));
		}
	}

	private static ConfidenceScore[] AddressConfidence = new ConfidenceScore[] { new ConfidenceScore("AS01", 100, true), new ConfidenceScore("AS02", 80, true), new ConfidenceScore("AE06", 50, false),
			new ConfidenceScore("AE02", 0, false), new ConfidenceScore("AE03", 0, false), new ConfidenceScore("AE05", 0, false) };
	private static ConfidenceDeduction[] AddressConfidenceDeduction = new ConfidenceDeduction[] { new ConfidenceDeduction("AC01", -10), new ConfidenceDeduction("AS02", -10),
			new ConfidenceDeduction("AC03", -7), new ConfidenceDeduction("AC06", -5), new ConfidenceDeduction("AC07", -5), new ConfidenceDeduction("AC08", -25), new ConfidenceDeduction("AC09", -30),
			new ConfidenceDeduction("AC10", -10), new ConfidenceDeduction("AC11", -2), new ConfidenceDeduction("AC12", -6), new ConfidenceDeduction("AC13", -1), new ConfidenceDeduction("AE14", -30),
			new ConfidenceDeduction("AS14", -5), new ConfidenceDeduction("AS15", -7), new ConfidenceDeduction("AS16", -60), new ConfidenceDeduction("AS17", -25) };

	private static class EvalAddressScore extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: ADDRESSSCORE(<stringExp>)");
			return new EvalItem(CalculateConfidence((String) param1.value, AddressConfidence, AddressConfidenceDeduction));
		}
	}

	private static class EvalDataQualityScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: DATAQUALITYSCORE(<stringExp>)");
			return new EvalItem(CalculateConfidence((String) param1.value, AddressConfidence, AddressConfidenceDeduction)
					+ CalculateConfidence((String) param1.value, EmailConfidence, EmailConfidenceDeduction) + CalculateConfidence((String) param1.value, GeoCodeConfidence, GeoCodeConfidenceDeduction)
					+ CalculateConfidence((String) param1.value, NameConfidence, NameConfidenceDeduction) + CalculateConfidence((String) param1.value, PhoneConfidence, PhoneConfidenceDeduction));
		}
	}

	private static ConfidenceScore[] EmailConfidence = new ConfidenceScore[] { new ConfidenceScore("ES01", 100, true), new ConfidenceScore("ES02", 10, true), new ConfidenceScore("ES03", 50, true) };
	private static ConfidenceDeduction[] EmailConfidenceDeduction = new ConfidenceDeduction[] { new ConfidenceDeduction("ES04", -30), new ConfidenceDeduction("ES10", -10),
			new ConfidenceDeduction("ES11", -10), new ConfidenceDeduction("ES12", -10), new ConfidenceDeduction("ES13", -10) };

	private static class EvalEmailScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {

			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: EMAILSCORE(<stringExp>)");
			return new EvalItem(CalculateConfidence((String) param1.value, EmailConfidence, EmailConfidenceDeduction));
		}
	}

	private static ConfidenceScore[] GeoCodeConfidence = new ConfidenceScore[] { new ConfidenceScore("GS05", 100, true), new ConfidenceScore("GS06", 90, true), new ConfidenceScore("GS01", 80, true),
			new ConfidenceScore("GS02", 70, true), new ConfidenceScore("GS03", 60, true) };
	private static ConfidenceDeduction[] GeoCodeConfidenceDeduction = new ConfidenceDeduction[] {};

	private static class EvalGeoCodeScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {

			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: GEOCODESCORE(<stringExp>)");
			return new EvalItem(CalculateConfidence((String) param1.value, GeoCodeConfidence, GeoCodeConfidenceDeduction));
		}
	}

	private static ConfidenceScore[] NameConfidence = new ConfidenceScore[] { new ConfidenceScore("NS01", 100, true), new ConfidenceScore("NS02", 50, true) };
	private static ConfidenceDeduction[] NameConfidenceDeduction = new ConfidenceDeduction[] { new ConfidenceDeduction("NS05", 5), new ConfidenceDeduction("NS06", 5),
			new ConfidenceDeduction("NS07", 5), new ConfidenceDeduction("NS08", 5), new ConfidenceDeduction("NE01", -20), new ConfidenceDeduction("NE03", -10), new ConfidenceDeduction("NE04", -10),
			new ConfidenceDeduction("NE05", -10), new ConfidenceDeduction("NE06", -20) };

	private static class EvalNameScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: NAMESCORE(<stringExp>)");
			return new EvalItem(CalculateConfidence((String) param1.value, NameConfidence, NameConfidenceDeduction));
		}
	}

	private static ConfidenceScore[] PhoneConfidence = new ConfidenceScore[] { new ConfidenceScore("PS01", 100, true), new ConfidenceScore("PS02", 50, true) };
	private static ConfidenceDeduction[] PhoneConfidenceDeduction = new ConfidenceDeduction[] {};

	private static class EvalPhoneScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: PHONESCORE(<stringExp>)");
			return new EvalItem(CalculateConfidence((String) param1.value, PhoneConfidence, PhoneConfidenceDeduction));
		}
	}

	public static EvalAbs EvalAbs = new EvalAbs();
	public static EvalCeiling EvalCeiling = new EvalCeiling();
	public static EvalCodePoint EvalCodePoint = new EvalCodePoint();
	public static EvalDateAdd EvalDateAdd = new EvalDateAdd();
	public static EvalDateDiff EvalDateDiff = new EvalDateDiff();
	public static EvalDatePart EvalDatePart = new EvalDatePart();
	public static EvalDay EvalDay = new EvalDay();
	public static EvalExp EvalExp = new EvalExp();
	public static EvalFindString EvalFindString = new EvalFindString();
	public static EvalFloor EvalFloor = new EvalFloor();
	public static EvalGetDate EvalGetDate = new EvalGetDate();
	public static EvalGetUTCDate EvalGetUTCDate = new EvalGetUTCDate();
	public static EvalHex EvalHex = new EvalHex();
	public static EvalIsNull EvalIsNull = new EvalIsNull();
	public static EvalIsIBAN EvalIsIBAN = new EvalIsIBAN();
	public static EvalLeft EvalLeft = new EvalLeft();
	public static EvalLen EvalLen = new EvalLen();
	public static EvalLn EvalLn = new EvalLn();
	public static EvalLog EvalLog = new EvalLog();
	public static EvalLower EvalLower = new EvalLower();
	public static EvalLTrim EvalLTrim = new EvalLTrim();
	public static EvalMonth EvalMonth = new EvalMonth();
	public static EvalNull EvalNull = new EvalNull();
	public static EvalPower EvalPower = new EvalPower();
	public static EvalReplace EvalReplace = new EvalReplace();
	public static EvalReplaceNull EvalReplaceNull = new EvalReplaceNull();
	public static EvalReplicate EvalReplicate = new EvalReplicate();
	public static EvalReverse EvalReverse = new EvalReverse();
	public static EvalRight EvalRight = new EvalRight();
	public static EvalRound EvalRound = new EvalRound();
	public static EvalRTrim EvalRTrim = new EvalRTrim();
	public static EvalSign EvalSign = new EvalSign();
	public static EvalSqrt EvalSqrt = new EvalSqrt();
	public static EvalSquare EvalSquare = new EvalSquare();
	public static EvalSubString EvalSubString = new EvalSubString();
	public static EvalToken EvalToken = new EvalToken();
	public static EvalTokenCount EvalTokenCount = new EvalTokenCount();
	public static EvalTrim EvalTrim = new EvalTrim();
	public static EvalUpper EvalUpper = new EvalUpper();
	public static EvalYear EvalYear = new EvalYear();

	public static EvalAddressScore EvalAddressScore = new EvalAddressScore();
	public static EvalDataQualityScore EvalDataQualityScore = new EvalDataQualityScore();
	public static EvalEmailScore EvalEmailScore = new EvalEmailScore();
	public static EvalGeoCodeScore EvalGeoCodeScore = new EvalGeoCodeScore();
	public static EvalNameScore EvalNameScore = new EvalNameScore();
	public static EvalPhoneScore EvalPhoneScore = new EvalPhoneScore();

	// TEST FUNCTIONS
	
	
	
	private static class TestAbs extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: ABS(<numericExp>)");
			return param1;
		}
	}

	private static class TestCeiling extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: CEILING(<numericExp>)");
			return param1;
		}
	}

	private static class TestCodePoint extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			throw new EvaluatorException("Type error: CODEPOINT not available )");
			/*
			 * if (param1.isNull()) return new EvalItem(); else if
			 * (!param1.isString()) throw new
			 * Exception("Type error: CODEPOINT(<stringExp>)"); return new
			 * EvalItem((ulong)0, DataType.DT_UI2);
			 */
		}
	}

	private static class TestDateAdd extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isNumeric() || !param1.isCalendarType())
				throw new EvaluatorException("Type error: DATEADD(<stringExp>, <numericExp>, <dateTimeExp>)");

			return new EvalItem(new Date(), "");
		}
	}

	private static class TestDateDiff extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isDateTime() || !param1.isDateTime())
				throw new EvaluatorException("Type error: DATEDIFF(<stringExp>, <dateTimeExp>, <dateTimeExp>)");

			return new EvalItem(0);
		}
	}

	private static class TestDatePart extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isDateTime())
				throw new EvaluatorException("Type error: DATEPART(<stringExp>, <dateTimeExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestDay extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isDateTime())
				throw new EvaluatorException("Type error: DAY(<dateTimeExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestExp extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: EXP(<numericExp>)");
			return new EvalItem((double) 0);
		}
	}

	private static class TestFindString extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: FINDSTRING(<stringExp>, <stringExp>, <numericExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestFloor extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: CEILING(<numericExp>)");
			return param1;
		}
	}

	private static class TestGetDate extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {

			return new EvalItem(Calendar.getInstance().getTime(), "");
		}
	}

	private static class TestGetUTCDate extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			return new EvalItem(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime(), "");
		}
	}

	private static class TestHex extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: HEX(<numericExp>)");
			return new EvalItem("");
		}
	}

	private static class TestIsNull extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			return new EvalItem(false);
		}
	}
	
	private static class TestIsIBAN extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if(!param1.isString()){
				throw new EvaluatorException("Type error: ISIBAN(<stringExp>)");
			}
			return new EvalItem(false);
		}
	}

	private static class TestLeft extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: LEFT(<stringExp>, <numericExp>)");
			return new EvalItem("");
		}
	}

	private static class TestLen extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: LEN(<stringExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestLn extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: LN(<numericExp>)");
			return new EvalItem((double) 0);
		}
	}

	private static class TestLog extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: LOG(<numericExp>)");
			return new EvalItem((double) 0);
		}
	}

	private static class TestLower extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNumeric())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: LOWER(<stringExp>)");
			return new EvalItem("");
		}
	}

	private static class TestLTrim extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			if (!param1.isString())
				throw new EvaluatorException("Type error: LTRIM(<stringExp>)");
			return new EvalItem("");
		}
	}

	private static class TestMonth extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isDateTime())
				throw new EvaluatorException("Type error: MONTH(<dateTimeExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestNull extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			return new EvalItem();
		}
	}

	private static class TestPower extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isNumeric() || !param1.isNumeric())
				throw new EvaluatorException("Type error: POWER(<numericExp>, <numericExp>)");
			return new EvalItem((double) 0);
		}
	}

	private static class TestReplace extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isString() || !param1.isString())
				throw new EvaluatorException("Type error: REPLACE(<stringExp>, <stringExp>, <stringExp>)");
			return new EvalItem("");
		}
	}

	private static class TestReplaceNull extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			return param2;
		}
	}

	private static class TestReplicate extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: REPLICATE(<stringExp>, <numericExp>)");
			return new EvalItem("");
		}
	}

	private static class TestReverse extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: REVERSE(<stringExp>)");

			return new EvalItem("");
		}
	}

	private static class TestRight extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: RIGHT(<stringExp>, <numericExp>)");
			return new EvalItem("");
		}
	}

	private static class TestRound extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isNumeric() || !param1.isNumeric())
				throw new EvaluatorException("Type error: ROUND(<numericExp>, <numericExp>)");

			return param1;
		}
	}

	private static class TestRTrim extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			if (!param1.isString())
				throw new EvaluatorException("Type error: RTRIM(<stringExp>)");
			return new EvalItem("");
		}
	}

	private static class TestSign extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: SIGN(<numericExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestSqrt extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: SQRT(<numericExp>)");
			return new EvalItem((double) 0);
		}
	}

	private static class TestSquare extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: SQUARE(<numericExp>)");
			return new EvalItem((double) 0);
		}
	}

	private static class TestSubString extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isNumeric() || !param1.isNumeric())
				throw new EvaluatorException("Type error: SUBSTRING(<stringExp>, <numericExp>, <numericExp>)");
			return new EvalItem("");
		}
	}

	private static class TestToken extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull() || param3.isNull())
				return new EvalItem();
			else if (!param3.isString() || !param2.isString() || !param1.isNumeric())
				throw new EvaluatorException("Type error: TOKEN(<stringExp>, <stringExp>, <numericExp>)");
			return new EvalItem("");
		}
	}

	private static class TestTokenCount extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param2.isString() || !param1.isString())
				throw new EvaluatorException("Type error: TOKENCOUNT(<stringExp>, <stringExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestTrim extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: TRIM(<stringExp>)");
			return new EvalItem("");
		}
	}

	private static class TestUpper extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNumeric())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: UPPER(<stringExp>)");
			return new EvalItem("");
		}
	}

	private static class TestYear extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isDateTime())
				throw new EvaluatorException("Type error: YEAR(<dateTimeExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestAddressScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: ADDRESSSCORE(<stringExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestNameScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: ADDRESSSCORE(<stringExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestPhoneScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: ADDRESSSCORE(<stringExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestEmailScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: ADDRESSSCORE(<stringExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestGeoCodeScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: ADDRESSSCORE(<stringExp>)");
			return new EvalItem(0);
		}
	}

	private static class TestDataQualityScore extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2, EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isString())
				throw new EvaluatorException("Type error: ADDRESSSCORE(<stringExp>)");
			return new EvalItem(0);
		}
	}

	
	public static TestAbs TestAbs = new TestAbs();
	public static TestCeiling TestCeiling = new TestCeiling();
	public static TestCodePoint TestCodePoint = new TestCodePoint();
	public static TestDateAdd TestDateAdd = new TestDateAdd();
	public static TestDateDiff TestDateDiff = new TestDateDiff();
	public static TestDatePart TestDatePart = new TestDatePart();
	public static TestDay TestDay = new TestDay();
	public static TestExp TestExp = new TestExp();
	public static TestFindString TestFindString = new TestFindString();
	public static TestFloor TestFloor = new TestFloor();
	public static TestGetDate TestGetDate = new TestGetDate();
	public static TestGetUTCDate TestGetUTCDate = new TestGetUTCDate();
	public static TestHex TestHex = new TestHex();
	public static TestIsNull TestIsNull = new TestIsNull();
	public static TestIsIBAN TestIsIBAN = new TestIsIBAN();
	public static TestLeft TestLeft = new TestLeft();
	public static TestLen TestLen = new TestLen();
	public static TestLn TestLn = new TestLn();
	public static TestLog TestLog = new TestLog();
	public static TestLower TestLower = new TestLower();
	public static TestLTrim TestLTrim = new TestLTrim();
	public static TestMonth TestMonth = new TestMonth();
	public static TestNull TestNull = new TestNull();
	public static TestPower TestPower = new TestPower();
	public static TestReplace TestReplace = new TestReplace();
	public static TestReplaceNull TestReplaceNull = new TestReplaceNull();
	public static TestReplicate TestReplicate = new TestReplicate();
	public static TestReverse TestReverse = new TestReverse();
	public static TestRight TestRight = new TestRight();
	public static TestRound TestRound = new TestRound();
	public static TestRTrim TestRTrim = new TestRTrim();
	public static TestSign TestSign = new TestSign();
	public static TestSqrt TestSqrt = new TestSqrt();
	public static TestSquare TestSquare = new TestSquare();
	public static TestSubString TestSubString = new TestSubString();
	public static TestToken TestToken = new TestToken();
	public static TestTokenCount TestTokenCount = new TestTokenCount();
	public static TestTrim TestTrim = new TestTrim();
	public static TestUpper TestUpper = new TestUpper();
	public static TestYear TestYear = new TestYear();

	public static TestAddressScore TestAddressScore = new TestAddressScore();
	public static TestDataQualityScore TestDataQualityScore = new TestDataQualityScore();
	public static TestEmailScore TestEmailScore = new TestEmailScore();
	public static TestGeoCodeScore TestGeoCodeScore = new TestGeoCodeScore();
	public static TestNameScore TestNameScore = new TestNameScore();
	public static TestPhoneScore TestPhoneScore = new TestPhoneScore();

	public static class ConfidenceScore {
		public String ResultCode;
		public int Score;
		public boolean UseDeductions;

		public ConfidenceScore(String resultCode, int score, boolean useDeductions) {
			ResultCode = resultCode;
			Score = score;
			UseDeductions = useDeductions;
		}
	}

	public static class ConfidenceDeduction {
		public String ResultCode;
		public int Deduction;

		public ConfidenceDeduction(String resultCode, int deduction) {
			ResultCode = resultCode;
			Deduction = deduction;
		}
	}

	private static int CalculateConfidence(String resultCode, ConfidenceScore[] confidence, ConfidenceDeduction[] deduction) {
		boolean useDeductions = false;
		int retVal = 0;

		// Figure out record's base confidence:
		for (int i = 0; i < confidence.length; i++) {
			if (resultCode.contains(confidence[i].ResultCode)) {
				retVal = confidence[i].Score;
				useDeductions = confidence[i].UseDeductions;
				break;
			}
		}

		// Calculate deductions (if desired):
		if (useDeductions) {
			for (int i = 0; i < deduction.length; i++) {
				if (resultCode.contains(deduction[i].ResultCode)) {
					retVal += deduction[i].Deduction;
				}
			}
		}
		return retVal;
	}

	/*
	 * private enum eDatePart { Year, Quarter, Month, DayOfYear, Day, Week,
	 * WeekDay, Hour, Minute, Second, Millisecond, Invalid }
	 */

}
