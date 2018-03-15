package com.melissadata.kettle.mu.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;

import com.melissadata.kettle.mu.evaluator.MUEvalStack.VariableRec;

public class Evaluator {

	private MUEvalStack muEvalStack;
	private TestEvaluate testEvaluate;
	private Evaluate evaluate;

	private Stack<EvalItem> evalStack = new Stack<EvalItem>();
	public static List<VariableRec> ColumnValues = new ArrayList<VariableRec>();
	public static List<VariableRec> variableValues = new ArrayList<VariableRec>();

	private String expression = "";
	private RowMetaInterface rowMeta;
	private VariableSpace varSpace;

	public Evaluator(RowMetaInterface row, VariableSpace space) throws EvaluatorException {
		muEvalStack = new MUEvalStack();
		testEvaluate = new TestEvaluate();
		evaluate = new Evaluate();
		rowMeta = row;
		varSpace = space;
	}

	public String cleanExpression(String sExpression) throws EvaluatorException {

		String retVal = "";

		char[] expression = sExpression.toCharArray();

		// Strip all whitespace not contained in "" or [] delimiters
		int pLevel = 0;
		char delimiter = '\0';

		for (int i = 0; i < expression.length; i++) {
			if (delimiter == '\0' && expression[i] != ' ') { // Entering delimited segment
				retVal += expression[i];
				if (expression[i] == '"' || expression[i] == '[')
					delimiter = (expression[i] == '[' ? ']' : expression[i]);
				else if (expression[i] == '(')
					pLevel++;
				else if (expression[i] == ')')
					pLevel--;

			} else if (delimiter == expression[i]) { // Exiting delimited segment...
				retVal += expression[i];
				if (i > 0 && expression[i - 1] != '\\') //   (ignoring escaped delimiters)
					delimiter = '\0';

			} else if (delimiter == ']' && expression[i] == '[') { // Nested square brackets (not allowed)
				throw new EvaluatorException("Doubly-nested square brackets");

			} else if (delimiter != '\0') { // Inside delimiters, pass all text through
				retVal += expression[i];
			}
		}
		if (retVal == "")
			throw new EvaluatorException("Empty Expression");
		else if (delimiter != '\0')
			throw new EvaluatorException("Unbalanced Delimiters");
		else if (pLevel != 0)
			throw new EvaluatorException("Unbalanced Parenthesis");

		return retVal;

	}

	public void parseExpression() throws EvaluatorException {

		muEvalStack.setColNames(rowMeta);
		muEvalStack.setVariableSpace(varSpace);
		evalStack = muEvalStack.stringToStack(expression);
	}

	public void testExpression() throws EvaluatorException {
		testEvaluate.testExpression(evalStack);
	}

	public EvalItem evaluateExpression(Object[] recAr) throws EvaluatorException {

		EvalItem ei = (EvalItem) evaluate.EvaluateExp(evalStack, recAr, varSpace);
		return ei;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) throws EvaluatorException {
		this.expression = cleanExpression(expression);
	}

	public Stack<EvalItem> getEvalStack() {
		return evalStack;
	}

	public void setEvalStack(Stack<EvalItem> evalStack) {
		this.evalStack = evalStack;
	}

}
