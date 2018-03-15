package com.melissadata.kettle.mu.evaluator;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Stack;

import com.melissadata.kettle.mu.MDCheckMatchup;
import org.pentaho.di.core.variables.VariableSpace;

public class Evaluate {
	
	
	
	// The Evaluate method takes the previously built Reverse-Polish expression and
			//   evaluates it. Its algorithm is fairly simple:
			//
			// 1 LIFO through the EvalStack.
			// 2 Depending on the popped value:
			//   A Constants, Columns and Variables: push onto TempStack.
			//   B Casts: pop required parameters off of EvalStack, evaluate and push result onto TempStack.
			//   C Operators: pop 1, 2 or 3 parameters off of EvalStack, evaluate & push result onto TempStack.
			//   D Functions: pop necessary parameters off of EvalStack, evaluate & push result onto TempStack.
			// 3 When you're all done, the final value remains on TempStack. Tada!
	public Object EvaluateExp(Stack<EvalItem> evalStack, Object[] recAr, VariableSpace space) throws EvaluatorException {
				
				if(evalStack == null){
					return new EvalItem();
				}
				Stack<EvalItem> tempStack = new Stack<EvalItem>();
				Object[] evalArray = evalStack.toArray();
				EvalItem[] paramList = new EvalItem[4];

				UpdateVariablesAndColumns(evalArray, recAr, space);

				
					for (int i = 0; i < evalArray.length; i++) {
					EvalItem item = (EvalItem)evalArray[i];

					// Column, variable or constant, just push it back onto the stack:
					if (item.type == EvalItem.eTypes.Column || item.type == EvalItem.eTypes.Variable || item.type == EvalItem.eTypes.Constant) {
						tempStack.push(item);

						// Cast Operator, pop off parameter, evaluate and push result:
					} else if (item.type == EvalItem.eTypes.Operator && item.operator == EvalItem.eOperators.Cast) {
						if (tempStack.size() < 1)
							throw new EvaluatorException("Invalid number of arguments");
						
						for (int j = 0; j < item.parameterCount; j++){
							paramList[j] = tempStack.pop();
						}
						
						tempStack.push(item.castFunction.Function(paramList[0], item));

						// Operator, pop off parameter(s), evaluate and push result:
					} else if (item.type == EvalItem.eTypes.Operator) {
						if (tempStack.size() < item.parameterCount)
							throw new EvaluatorException("Invalid number of arguments");

						for (int j = 0; j < item.parameterCount; j++)
							paramList[j] = tempStack.pop();

						tempStack.push(item.function.Function(paramList[0], paramList[1], paramList[2], paramList[3]));

						// Function, pop off parameter(s), evaluate and push result:
					} else if (item.type == EvalItem.eTypes.Function) {
						if (tempStack.size() < item.parameterCount)
							throw new EvaluatorException("Invalid number of arguments");

						for (int j = 0; j < item.parameterCount; j++)
							paramList[j] = tempStack.pop();

						try {
							tempStack.push(item.function.Function(paramList[0], paramList[1], paramList[2], paramList[3]));
						} catch (EvaluatorException ee){
							throw ee;
						}
					}
				}

				// Goodness, we better not have any left over!
				if (tempStack.size() != 1){
					EvalItem ee = tempStack.pop() ;
					System.out.println(" tempstack = " + ee.toString());
					 ee = tempStack.pop() ;
					System.out.println(" tempstack = " + ee.toString());
					throw new EvaluatorException("Syntax error stuff on stack: " + tempStack.size());
				}

				return (tempStack.pop().clone());
			}
			
	// Each time the expression is evaluated, Variable and Column EvalItem's are populated with their starting value.
	//   This is gotten from the ColumnValues array (for columns) and either the VariableValues array or
	//   Variable Dispenser (for variables). Again, variables are a PITA.
	private void UpdateVariablesAndColumns(Object[] evalArray, Object[] recArray, VariableSpace space) {
		for (int i = 0; i < evalArray.length; i++) {
			if (((EvalItem) evalArray[i]).type == EvalItem.eTypes.Variable) {
				((EvalItem) evalArray[i]).SetVarValue(space.getVariable(((EvalItem) evalArray[i]).variableName, ""));

			} else if (((EvalItem) evalArray[i]).type == EvalItem.eTypes.Column) {
				int index = MDCheckMatchup.getColumnIndex(((EvalItem) evalArray[i]).variableName);

				EvalItem.eDataType eType = getDataType(recArray[index]);
				if (eType == null) {
					eType = ((EvalItem) evalArray[i]).dataType;
				}

				if (recArray[index] == null) {
					((EvalItem) evalArray[i]).SetColValue(null, eType);
				} else {
					((EvalItem) evalArray[i]).SetColValue(recArray[index], eType);
				}

			}
		}
	}
	
	private EvalItem.eDataType getDataType(Object val){
		
		if(val instanceof String)
			return EvalItem.eDataType.DT_STRING;
		if(val instanceof Double)
			return EvalItem.eDataType.DT_DOUBLE;
		if(val instanceof Long)
			return EvalItem.eDataType.DT_INT;
		if(val instanceof BigDecimal)
			return EvalItem.eDataType.DT_BIGNUMBER;
		if(val instanceof Timestamp)
			return EvalItem.eDataType.DT_TIMESTAMP;
		if(val instanceof Date)
			return EvalItem.eDataType.DT_DATE;
		if(val instanceof Boolean)
			return EvalItem.eDataType.DT_BOOL;
		
		if(val instanceof Inet4Address)
			return EvalItem.eDataType.DT_INTERNETADDRESS;
		
		
		
		return null;
	}

}
