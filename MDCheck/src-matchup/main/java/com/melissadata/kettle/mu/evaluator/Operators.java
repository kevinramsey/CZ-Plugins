package com.melissadata.kettle.mu.evaluator;

import com.melissadata.kettle.mu.evaluator.EvalItem.eDataType;
import com.melissadata.kettle.mu.evaluator.EvalItem.EvalFunction;

public class Operators {

	public static class OperatorRec {
		public String OperatorStr;
		public EvalItem.eOperators Operator;
		public EvalFunction Function;
		public EvalFunction TestFunction;
		public int ParameterCount;

		public OperatorRec(String str, EvalItem.eOperators op,
				EvalFunction fun, EvalFunction testFun, int cnt) {
			OperatorStr = str;
			Operator = op;
			Function = fun;
			TestFunction = testFun;
			ParameterCount = cnt;
		}
	}

	// EVAL
	private static class EvalLogicalOr extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1 == null && param2 == null)
				return new EvalItem();
			else if ((!(param1 == null) && !param1.isBoolean())
					|| (!param2.isNull() && !param2.isBoolean()))
				throw new EvaluatorException(
						"Type error: <booleanExp> || <booleanExp>");
			else if (param1.isNull() && param2.isBoolean()
					&& !(Boolean) param2.value || param2.isNull()
					&& param1.isBoolean() && !(Boolean) param1.value)
				return new EvalItem(); // FALSE || NULL => NULL
			else if (param1.isBoolean() && (Boolean) param1.value
					|| param2.isBoolean() && (Boolean) param2.value)
				return new EvalItem(true); // TRUE || ? => TRUE
			else
				return new EvalItem(false); // FALSE || FALSE => FALSE
		}
	}

	private static class EvalLogicalAnd extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() && param2.isNull())
				return new EvalItem();
			else if (!param1.isNull() && !param1.isBoolean() || !param2.isNull() && !param2.isBoolean())
				throw new EvaluatorException("Type error: <booleanExp> && <booleanExp>");
			else if (param1.isNull() && param2.isBoolean() && (Boolean)param2.value || param2.isNull() && param1.isBoolean() && (Boolean)param1.value)
				return new EvalItem();                             // TRUE && NULL => NULL
			else if (param1.isBoolean() && (Boolean)param1.value && param2.isBoolean() && (Boolean)param2.value)
				return new EvalItem(true);       // TRUE && TRUE => TRUE
			else
				return new EvalItem(false);      // FALSE && ? => FALSE
		}

	}

	private static class EvalEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem(false);
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() && !param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> == <exp>");
		//	else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
		//		throw new EvaluatorException("Type error: <exp> == <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() == param1.getDouble());
			else if (param1.isBigNumber() || param2.isBigNumber())
				return new EvalItem(param2.getBigNumber() == param1.getBigNumber());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() == param1.getInt());
			else if (param1.isString() || param2.isString())
				return new EvalItem((String)param2.value == (String)param1.value);
			else if (param1.isCalendarType() || param2.isCalendarType())
				return new EvalItem(CompareDateTimes(param2, param1) == 0);
			else if (param1.isBoolean() || param2.isBoolean())
				return new EvalItem((Boolean)param2.value == (Boolean)param1.value);
			return new EvalItem();
		}

	}

	private static class EvalNotEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem(false);
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() && !param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> != <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> != <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() != param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() != param1.getInt());
			else if (param1.isBigNumber() || param2.isBigNumber())
				return new EvalItem(param2.getBigNumber() != param1.getBigNumber());
			else if (param1.isString() || param2.isString())
				return new EvalItem((String)param2.value != (String)param1.value);
			else if (param1.isCalendarType() || param2.isCalendarType())
				return new EvalItem(CompareDateTimes(param2, param1) != 0);
			else if (param1.isBoolean() || param2.isBoolean())
				return new EvalItem((Boolean)param2.value != (Boolean)param1.value);
			return new EvalItem();
		}

	}

	private static class EvalLessEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem(false);
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> <= <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> <= <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() <= param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() <= param1.getInt());
			else if (param1.isString() || param2.isString())
				return new EvalItem(CompareStrings((String)param2.value, (String)param1.value) <= 0);
			else if (param1.isDateTime() || param2.isDateTime())
				return new EvalItem(CompareDateTimes(param2, param1) <= 0);
			return new EvalItem();
		}

	}

	private static class EvalGreaterEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem(false);
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> >= <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> >= <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() >= param1.getDouble());
			//else if (param1.IsDecimal() || param2.IsDecimal())
			//	return new EvalItem(param2.GetDecimal() >= param1.GetDecimal(), DataType.DT_BOOL);
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() >= param1.getInt());
			else if (param1.isString() || param2.isString())
				return new EvalItem(CompareStrings((String)param2.value, (String)param1.value) >= 0);
			else if (param1.isDateTime() || param2.isDateTime())
				return new EvalItem(CompareDateTimes(param2, param1) >= 0);
			return new EvalItem();
		}

	}

	private static class EvalTernary1 extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			return param1;
		}

	}

	private static class EvalTernary3 extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param3.isBoolean())
				return ((Boolean)param3.value ? param2 : param1);
			throw new EvaluatorException("Type error: <boolExp> ? <exp> : <exp>");
		}

	}

	private static class EvalLogicalNot extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isBoolean())
				throw new EvaluatorException("Type error: ! <boolExp>");
			return new EvalItem(!(Boolean)param1.value);
		}

	}

	private static class EvalGreater extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem(false);
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error greater: <exp> > <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> > <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() > param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() > param1.getInt());
			else if (param1.isString() || param2.isString())
				return new EvalItem(CompareStrings((String)param2.value, (String)param1.value) > 0);
			else if (param1.isDateTime() || param2.isDateTime())
				return new EvalItem(CompareDateTimes(param2, param1) > 0);
			return new EvalItem();
		}

	}

	private static class EvalLess extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem(false);
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> < <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> < <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() < param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() < param1.getInt());
			else if (param1.isString() || param2.isString())
				return new EvalItem(CompareStrings((String)param2.value, (String)param1.value) < 0);
			else if (param1.isDateTime() || param2.isDateTime())
				return new EvalItem(CompareDateTimes(param2, param1) < 0);
			return new EvalItem();
		}

	}

	private static class EvalPlus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isString() && !param2.isString() || param1.isDateTime() || param2.isDateTime() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> + <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() + param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() + param1.getInt());
			else if (param1.isString() || param2.isString())
				return new EvalItem((String)param2.value + (String)param1.value);
			return new EvalItem();
		}

	}

	private static class EvalMinus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: <exp> - <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() - param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() - param1.getInt());
			return new EvalItem();
		}

	}

	private static class EvalMultiply extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: <exp> * <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() * param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() * param1.getInt());
			return new EvalItem();
		}

	}

	private static class EvalDivide extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: <exp> / <exp>");
			else if (param1.getDouble() == 0)
				throw new EvaluatorException("/: Division by 0");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() / param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() / param1.getInt());
			return new EvalItem();
		}

	}

	private static class EvalModulus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: <exp> % <exp>");
			else if (param1.getDouble() == 0)
				throw new EvaluatorException("%: Division by 0");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem(param2.getDouble() % param1.getDouble());
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(param2.getInt() % param1.getInt());
			return new EvalItem();
		}

	}

	private static class EvalUnaryMinus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: - <numericExp>");

			if (param1.isDouble())
				return new EvalItem(-param1.getDouble());
			else if (param1.isInt())
				return new EvalItem(-param1.getInt());
			return new EvalItem();
		}

	}

	// Test
	private static class TestLogicalOr extends EvalFunction {
		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() && param2.isNull())
				return new EvalItem();
			else if (!param1.isNull() && !param1.isBoolean()
					|| !param2.isNull() && !param2.isBoolean())
				throw new EvaluatorException("Type error: <booleanExp> || <booleanExp>");
			return new EvalItem(false);
		}
	}

	private static class TestLogicalAnd extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() && param2.isNull())
				return new EvalItem();
			else if (!param1.isNull() && !param1.isBoolean() || !param2.isNull() && !param2.isBoolean())
				throw new EvaluatorException("Type error: <booleanExp> && <booleanExp>");
			return new EvalItem(false);
		}

	}

	private static class TestEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() && !param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> == <exp>");
		//	else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
		//		throw new EvaluatorException("Type error: <exp> == <exp>");
			return new EvalItem(false);
		}

	}

	private static class TestNotEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() && !param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> != <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> != <exp>");
			return new EvalItem(false);
		}

	}

	private static class TestLessEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> <= <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> <= <exp>");
			return new EvalItem(false);
		}

	}

	private static class TestGreaterEqual extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> >= <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> >= <exp>");
			return new EvalItem(false);
		}

	}

	private static class TestTernary1 extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			return param1;
		}

	}

	private static class TestTernary3 extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param3.isBoolean())
				return param1;
			throw new EvaluatorException("Type error: <boolExp> ? <exp> : <exp>");
		}

	}

	private static class TestLogicalNot extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isBoolean())
				throw new EvaluatorException("Type error: ! <boolExp>");
			return new EvalItem(false);
		}

	}

	private static class TestGreater extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error test greater:" +  param1.value + " > " + param2.value);
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> > <exp>");
			return new EvalItem(false);
		}

	}

	private static class TestLess extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric() || param1.isDateTime() && !param2.isDateTime() || param1.isString() && !param2.isString() || param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> < <exp>");
			//else if (param1.isDateTime() && !CanDatesBeCompared(param1, param2))
			//	throw new EvaluatorException("Type error: <exp> < <exp>");
			return new EvalItem(false);
		}

	}

	private static class TestPlus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (param1.isNumeric() && !param2.isNumeric()
					|| param1.isString() && !param2.isString()
					|| param1.isDateTime() || param2.isDateTime()
					|| param1.isBoolean() || param2.isBoolean())
				throw new EvaluatorException("Type error: <exp> + <exp>");
			// throw new EvaluatorException("Type error: <exp> + <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem((double) 0);
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(0);
			else if (param1.isString() || param2.isString())
				return new EvalItem("");

			return new EvalItem();
		}

	}

	private static class TestMinus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: " + param1.dataType
						+ " - " + param2.dataType);

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem((double) 0);
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(0);
			// else if (param1.isInt() || param2.isInt())
			// return new EvalItem((long)0, DataType.DT_I8);
			return new EvalItem();
		}

	}

	private static class TestMultiply extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: <exp> * <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem((double) 0);
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(0);
			// else if (param1.IsLong() || param2.IsLong())
			// return new EvalItem((long)0, DataType.DT_I8);
			return new EvalItem();
		}

	}

	private static class TestDivide extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: <exp> / <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem((double)0);
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(0);
			return new EvalItem();
		}

	}

	private static class TestModulus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull() || param2.isNull())
				return new EvalItem();
			else if (!param1.isNumeric() || !param2.isNumeric())
				throw new EvaluatorException("Type error: <exp> % <exp>");

			if (param1.isDouble() || param2.isDouble())
				return new EvalItem((double)0);
			else if (param1.isInt() || param2.isInt())
				return new EvalItem(0);
			return new EvalItem();
		}

	}

	private static class TestUnaryMinus extends EvalFunction {

		@Override
		public EvalItem Function(EvalItem param1, EvalItem param2,
				EvalItem param3, EvalItem param4) throws EvaluatorException {
			if (param1.isNull())
				return new EvalItem();
			else if (!param1.isNumeric())
				throw new EvaluatorException("Type error: - <numericExp>");

			if (param1.isDouble())
				return new EvalItem((double)0);
			else if (param1.isInt())
				return new EvalItem(0);
			return new EvalItem();
		}

	}

	public static EvalLogicalOr EvalLogicalOr = new EvalLogicalOr();
	public static EvalLogicalAnd EvalLogicalAnd = new EvalLogicalAnd();
	public static EvalEqual EvalEqual = new EvalEqual();
	public static EvalNotEqual EvalNotEqual = new EvalNotEqual();
	public static EvalGreaterEqual EvalGreaterEqual = new EvalGreaterEqual();
	public static EvalLessEqual EvalLessEqual = new EvalLessEqual();
	public static EvalTernary1 EvalTernary1 = new EvalTernary1();
	public static EvalTernary3 EvalTernary3 = new EvalTernary3();
	public static EvalLogicalNot EvalLogicalNot = new EvalLogicalNot();
	public static EvalGreater EvalGreater = new EvalGreater();
	public static EvalLess EvalLess = new EvalLess();
	public static EvalPlus EvalPlus = new EvalPlus();
	public static EvalMinus EvalMinus = new EvalMinus();
	public static EvalMultiply EvalMultiply = new EvalMultiply();
	public static EvalDivide EvalDivide = new EvalDivide();
	public static EvalModulus EvalModulus = new EvalModulus();
	public static EvalUnaryMinus EvalUnaryMinus = new EvalUnaryMinus();
	
	public static TestLogicalOr TestLogicalOr = new TestLogicalOr();
	public static TestLogicalAnd TestLogicalAnd = new TestLogicalAnd();
	public static TestEqual TestEqual = new TestEqual();
	public static TestNotEqual TestNotEqual = new TestNotEqual();
	public static TestGreaterEqual TestGreaterEqual = new TestGreaterEqual();
	public static TestLessEqual TestLessEqual = new TestLessEqual();
	public static TestTernary1 TestTernary1 = new TestTernary1();
	public static TestTernary3 TestTernary3 = new TestTernary3();
	public static TestLogicalNot TestLogicalNot = new TestLogicalNot();
	public static TestGreater TestGreater = new TestGreater();
	public static TestLess TestLess = new TestLess();
	public static TestPlus TestPlus = new TestPlus();
	public static TestMinus TestMinus = new TestMinus();
	public static TestMultiply TestMultiply = new TestMultiply();
	public static TestDivide TestDivide = new TestDivide();
	public static TestModulus TestModulus = new TestModulus();
	public static TestUnaryMinus TestUnaryMinus = new TestUnaryMinus();
	
	
	private static int CompareStrings(String str1, String str2){

		return str1.compareTo(str2);

	}
	
	private static int CompareDateTimes(EvalItem param1, EvalItem param2) {
		int rr = 0;
			if (param1.dataType == eDataType.DT_DATE ){
				
				rr = param1.getDate().compareTo(param2.getDate());
			}

			return rr;
		
	}

}
