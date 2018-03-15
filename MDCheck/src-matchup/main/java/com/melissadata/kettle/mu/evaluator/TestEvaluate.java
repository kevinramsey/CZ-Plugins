package com.melissadata.kettle.mu.evaluator;


import java.util.Stack;

public class TestEvaluate {
	
	// The TestEvaluate method works just like the Evaluate() function. However, variable content is not checked --
		//   a series of 'test' operators, functions and cast operators are used instead of the 'real' ones. These
		//   test ones test for correct syntax, data typing, etc, but do not actually plug in or return values. Thus,
		//   a test will not fail because of a divide by zero or parse error (which is what we want).
	public void testExpression(Stack<EvalItem> evalStack) throws EvaluatorException {
		Stack<EvalItem> tempStack = new Stack<EvalItem>();

		Object[] evalArray = evalStack.toArray();
		EvalItem[] paramList = new EvalItem[4];

		for (int i = 0; i < evalArray.length; i++) {

			EvalItem item = (EvalItem) evalArray[i];

			// Column, variable or constant, just push it back onto the stack:
			if (item.type == EvalItem.eTypes.Column || item.type == EvalItem.eTypes.Variable || item.type == EvalItem.eTypes.Constant) {
				tempStack.push(item);

				// Cast Operator, pop off parameter, evaluate and push result:
			} else if (item.type == EvalItem.eTypes.Operator && item.operator == EvalItem.eOperators.Cast) {

				if (tempStack.size() < 1)
					throw new EvaluatorException("Invalid number of arguments");

				for (int j = 0; j < item.parameterCount; j++) {
					paramList[j] = tempStack.pop();
				}

				tempStack.push(item.testCastFunction.Function(paramList[0], item));

				// Operator, pop off parameter(s), evaluate and push result:
			} else if (item.type == EvalItem.eTypes.Operator) {

				if (tempStack.size() < item.parameterCount)
					throw new EvaluatorException("OPER " + item.value + " Invalid number of arguments");

				for (int j = 0; j < item.parameterCount; j++) {
					paramList[j] = tempStack.pop();
				}

				tempStack.push(item.testFunction.Function(paramList[0], paramList[1], paramList[2], paramList[3]));

				// Function, pop off parameter(s), evaluate and push result:
			} else if (item.type == EvalItem.eTypes.Function) {
				if (tempStack.size() < item.parameterCount)
					throw new EvaluatorException("FUNCT Invalid number of arguments");

				for (int j = 0; j < item.parameterCount; j++)
					paramList[j] = tempStack.pop();

				tempStack.push(item.testFunction.Function(paramList[0], paramList[1], paramList[2], paramList[3]));
			}
		}

		if (tempStack.size() != 1)
			throw new EvaluatorException("Syntax Error");

	}

}
