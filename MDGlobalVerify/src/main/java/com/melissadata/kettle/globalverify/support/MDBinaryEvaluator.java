package com.melissadata.kettle.globalverify.support;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.kettle.globalverify.MDGlobalMeta;

public class MDBinaryEvaluator {
	private static Class<?>	PKG	= MDGlobalMeta.class;

	public static class EvaluatorException extends KettleException {
		private static final long	serialVersionUID	= 3350852321563197648L;

		public EvaluatorException(String messageID, Object... args) {
			super(getMessage(messageID, args));
		}

		private static String getMessage(String messageID, Object... args) {
			return BaseMessages.getString(PKG, "MDCheck.BinaryEvaluator.Error." + messageID, args);
		}
	}

	public static class EvalItem {
		public static enum eType {
			ResultCode,
			WildResultCode,
			Operator,
			Value
		}

		public static enum eOperator {
			And,
			Or,
			Not,
			LeftParenthesis,
			RightParenthesis
		}
		private eType		type;
		private String		resultCode;
		private Pattern		wildCardPattern;
		private eOperator	operator;
		private boolean		value;

		public EvalItem(String resultCode) {
			type = eType.ResultCode;
			this.resultCode = resultCode;
		}

		public EvalItem(eOperator operator) {
			type = eType.Operator;
			this.operator = operator;
		}

		public EvalItem(boolean value) {
			type = eType.Value;
			this.value = value;
		}

		public EvalItem(Pattern wildCardPattern) {
			type = eType.WildResultCode;
			this.wildCardPattern = wildCardPattern;
		}
	}
	private static final Pattern	RESULT_CODE_PAT			= Pattern.compile("\\[" + "(" + "\\w+" + ")" + "\\]");
	private static final Pattern	RESULT_WILD_CODE_PAT	= Pattern.compile("\\[" + "(" + "[A-Za-z0-9_*?]+" + ")" + "\\]");

	public enum eTokenType {
		BeginningOfExpression,
		EndOfExpression,
		ResultCode,
		WildCardField,
		And,
		Or,
		Not,
		LeftParenthesis,
		RightParenthesis,
		InvalidToken, ;
		public boolean isOneOf(eTokenType... types) {
			for (eTokenType type : types) {
				if (this == type)
					return true;
			}
			return false;
		}
	}

	// Enumerations used for deciding what to do with a particular token:
	private enum eStackOperations {
		PushToken,		// Push new item onto TempStack
		MoveToken,		// Pop TempStack into NewStack, try again
		ForgetToken,	// Pop TempStack, delete, delete new item
		Error			// Unbalanced parenthesis or syntax error
	}
	private Stack<EvalItem>						evalStack		= new Stack<EvalItem>();
	private static final eStackOperations[][]	stackOperations	= {
																/* OR *//* AND *//* NOT *//* ( *//* ) */
/* OR */														{ eStackOperations.MoveToken, eStackOperations.PushToken, eStackOperations.PushToken, eStackOperations.PushToken, eStackOperations.MoveToken },
/* AND */														{ eStackOperations.MoveToken, eStackOperations.MoveToken, eStackOperations.PushToken, eStackOperations.PushToken, eStackOperations.MoveToken },
/* NOT */														{ eStackOperations.MoveToken, eStackOperations.MoveToken, eStackOperations.MoveToken, eStackOperations.PushToken, eStackOperations.MoveToken },
/* ( */															{ eStackOperations.PushToken, eStackOperations.PushToken, eStackOperations.PushToken, eStackOperations.PushToken, eStackOperations.ForgetToken },
/* ) */															{ eStackOperations.Error, eStackOperations.Error, eStackOperations.Error, eStackOperations.Error, eStackOperations.Error } };

	/**
	 * @param expression
	 *            The expression for the evaluator
	 * @throws KettleException
	 */
	public MDBinaryEvaluator(String expression) throws KettleException {
		stringToStack(expression);
	}

	/**
	 * (Code borrowed from SSIS MD component)
	 * 
	 * The StringToStack function takes a string and converts it into a Reverse-Polish
	 * notation (aka, converts infix to postfix notation). The Reverse-Polish notation
	 * is stored on a stack, which is later evaluated in the Evaluate method. The
	 * algorithm that performs this conversion is quite common, and probably the most
	 * widely used method. Simplified:
	 * 
	 * 1 Set up two LIFO stacks: EvalStack and TempStack.
	 * 2 Parse string for tokens (operator, field, etc), left to right.
	 * 3 Depending on what's at the top of each of the two stacks, perform one
	 * of these operations:
	 * A Push the new token on one of the stacks
	 * B Move a token from the top of one stack to the top of the other.
	 * C Remove (forget) the token.
	 * D Remove (delete) a token from the top of one stack.
	 *
	 * The StackOperations[,] table below drives this decision tree. The resultant
	 * decision is obtained by looking at what's at the top of the TempStack and
	 * the current token (NewItem). It is essentially what drives operator precedence
	 * and the magic of parenthesis.
	 * 4 When all done, TempStack should be empty, and the EvalStack contains
	 * a postfix notated expression.
	 *
	 * StringToStack is performed only once on an incoming expression. Once in Reverse-
	 * Polish notation, it can be evaluated as often as needed through the Evaluate
	 * method (even if field values change). This is much more efficient than
	 * performing this conversion every time the expression needs evalating.
	 * 
	 * @param expression
	 * @throws KettleException
	 */
	private void stringToStack(String expression) throws KettleException {
		// Check for balanced parenthesis:
		int parens = 0;
		for (char c : expression.toCharArray()) {
			if (c == '(')
				parens++;
			else if (c == ')')
				parens--;
		}
		if (parens != 0)
			throw new EvaluatorException("UnbalancedParenthesis", expression);
		// Pad parenthesis with spaces to ensure that they get tokenized properly:
		expression = expression.replace("(", " ( ").replace(")", " ) ").trim();
		String[] tokens = expression.split("\\s+");
		// Blank expression means always evaluate to true
		if (expression.trim().length() == 0 || tokens.length == 0) {
			evalStack.push(new EvalItem(true));
			return;
		}
		// Tokenize and push the string
		Stack<EvalItem> tempStack = new Stack<EvalItem>();
		eTokenType lastToken = eTokenType.BeginningOfExpression;
		for (int i = 0; i < tokens.length; i++) {
			// Tokenize
			String[] thisResultCode = new String[1];
			eTokenType thisToken = tokenType(tokens, i, thisResultCode);
			String[] nextResultCode = new String[1];
			eTokenType nextToken = tokenType(tokens, i + 1, nextResultCode);
			// Syntax checking. We're checking to ensure that a token is not
			// adjacent to an illegal one (ie "[AS01] and and [AS02]" is
			// a syntax error):
			if (thisToken == eTokenType.ResultCode && (lastToken.isOneOf(eTokenType.ResultCode, eTokenType.RightParenthesis) || nextToken.isOneOf(eTokenType.ResultCode, eTokenType.LeftParenthesis)))
				throw new EvaluatorException("SyntaxErrorInExpression", expression);
			else if (thisToken.isOneOf(eTokenType.And, eTokenType.Or)
					&& (lastToken.isOneOf(eTokenType.BeginningOfExpression, eTokenType.And, eTokenType.Or, eTokenType.Not, eTokenType.LeftParenthesis) || nextToken.isOneOf(eTokenType.EndOfExpression, eTokenType.And, eTokenType.Or,
							eTokenType.RightParenthesis)))
				throw new EvaluatorException("SyntaxErrorInExpression", expression);
			else if (thisToken == eTokenType.Not && (lastToken.isOneOf(eTokenType.ResultCode, eTokenType.RightParenthesis) || nextToken.isOneOf(eTokenType.EndOfExpression, eTokenType.And, eTokenType.Or, eTokenType.RightParenthesis)))
				throw new EvaluatorException("SyntaxErrorInExpression", expression);
			else if (thisToken == eTokenType.LeftParenthesis && (lastToken.isOneOf(eTokenType.ResultCode, eTokenType.RightParenthesis) || nextToken.isOneOf(eTokenType.EndOfExpression, eTokenType.And, eTokenType.Or, eTokenType.RightParenthesis)))
				throw new EvaluatorException("SyntaxErrorInExpression", expression);
			else if (thisToken == eTokenType.RightParenthesis
					&& (lastToken.isOneOf(eTokenType.BeginningOfExpression, eTokenType.And, eTokenType.Or, eTokenType.Not, eTokenType.LeftParenthesis) || nextToken.isOneOf(eTokenType.ResultCode, eTokenType.Not, eTokenType.LeftParenthesis)))
				throw new EvaluatorException("SyntaxErrorInExpression", expression);
			// Survived the syntax check, we create an EvalItem of the appropriate type:
			EvalItem newItem;
			switch (thisToken) {
				case ResultCode:
					newItem = new EvalItem(thisResultCode[0]);
					break;
				case WildCardField:
					newItem = new EvalItem(Pattern.compile(thisResultCode[0].replaceAll("\\*", ".*").replaceAll("\\?", ".")));
					break;
				case And:
					newItem = new EvalItem(EvalItem.eOperator.And);
					break;
				case Or:
					newItem = new EvalItem(EvalItem.eOperator.Or);
					break;
				case Not:
					newItem = new EvalItem(EvalItem.eOperator.Not);
					break;
				case LeftParenthesis:
					newItem = new EvalItem(EvalItem.eOperator.LeftParenthesis);
					break;
				case RightParenthesis:
					newItem = new EvalItem(EvalItem.eOperator.RightParenthesis);
					break;
				default:
					throw new EvaluatorException("UnidentifiedIdentifier", tokens[i]);
			}
			// Result codes always put onto EvalStack:
			if (newItem.type == EvalItem.eType.ResultCode || newItem.type == EvalItem.eType.WildResultCode)
				evalStack.push(newItem);
			else {
				while (true) {
					// If TempStack is empty, put the new item there:
					if (tempStack.size() == 0) {
						tempStack.push(newItem);
						// Not empty, look on top of TempStack and see what to do
						// based on what it is and what's in our hand (using the
						// StackOperations array above):
					} else {
						switch (stackOperations[tempStack.peek().operator.ordinal()][newItem.operator.ordinal()]) {
							case PushToken: 						// Push new item onto TempStack
								tempStack.push(newItem);
								break;
							case MoveToken: 						// Move top item from TempStack over to
								evalStack.push(tempStack.pop());	// EvalStack and try again
								continue; // Good god, a goto.
							case ForgetToken: 						// Delete top item from TempStack and NewItem
								tempStack.pop();
								break;
							case Error:
								throw new EvaluatorException("SyntaxErrorInExpression", expression);
						}
					}
					break;
				}
			}
			lastToken = thisToken;
		}
		// Move any remaining items from TempStack to EvalStack:
		while (tempStack.size() > 0)
			evalStack.push(tempStack.pop());
	}

	/**
	 * Called to tokenize a string
	 * 
	 * @param tokens
	 * @param i
	 * @param resultCode
	 * @return
	 */
	private eTokenType tokenType(String[] tokens, int index, String[] resultCode) {
		resultCode[0] = null;
		if (index >= tokens.length)
			return eTokenType.EndOfExpression;
		String token = tokens[index].trim().toUpperCase();
		if (isResultCode(token, resultCode))
			return eTokenType.ResultCode;
		else if (isWildcard(token, resultCode))
			return eTokenType.WildCardField;
		else if (token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("&&"))
			return eTokenType.And;
		else if (token.equalsIgnoreCase("OR") || token.equalsIgnoreCase("||"))
			return eTokenType.Or;
		else if (token.equalsIgnoreCase("NOT") || token.equalsIgnoreCase("!"))
			return eTokenType.Not;
		else if (token.equalsIgnoreCase("("))
			return eTokenType.LeftParenthesis;
		else if (token.equalsIgnoreCase(")"))
			return eTokenType.RightParenthesis;
		return eTokenType.InvalidToken;
	}

	/**
	 * Called to determine if a token is a result code string
	 * 
	 * @param token
	 * @param fldNo
	 * @return
	 */
	private boolean isResultCode(String token, String[] resultCode) {
		Matcher matcher = RESULT_CODE_PAT.matcher(token.trim());
		if (matcher.matches()) {
			resultCode[0] = matcher.group(1);
			return true;
		}
		return false;
	}

	private boolean isWildcard(String token, String[] resultCode) {
		Matcher matcher = RESULT_WILD_CODE_PAT.matcher(token.trim());
		if (matcher.matches()) {
			resultCode[0] = matcher.group(1);
			return true;
		}
		return false;
	}

	private boolean evaluateWildcard(Pattern wildCardPattern, List<String> resultCodes) {
		for (String code : resultCodes) {
			Matcher matcher = wildCardPattern.matcher(code);
			if (matcher.matches()) { return true; }
		}
		return false;
	}

	/**
	 * Called to evaluate the result codes and determine if they are valid according to
	 * the current validation expression
	 * 
	 * @param resultCodes
	 * @return
	 * @throws KettleException
	 */
	public boolean evaluate(List<String> resultCodes) throws KettleException {
		Stack<EvalItem> tempStack = new Stack<EvalItem>();
		EvalItem[] evalArray = evalStack.toArray(new EvalItem[evalStack.size()]);
		for (EvalItem item : evalArray) {
			// Value. Just push it
			if (item.type == EvalItem.eType.Value) {
				tempStack.push(item);
				// Result Code value, just look up and push it back onto the stack:
			} else if (item.type == EvalItem.eType.ResultCode) {
				tempStack.push(new EvalItem(resultCodes.contains(item.resultCode)));
			} else if (item.type == EvalItem.eType.WildResultCode) {
				tempStack.push(new EvalItem(evaluateWildcard(item.wildCardPattern, resultCodes)));
				// Unary operator, pop off a single parameter, evaluate and push result:
			} else if (item.operator == EvalItem.eOperator.Not) {
				if (tempStack.size() < 1)
					throw new EvaluatorException("UnaryOperatorNoValue");
				EvalItem item0 = tempStack.pop();
				item0.value = !item0.value;
				tempStack.push(item0);
				// Binary operator, pop off two parameters, evaluate and push result:
			} else if (item.operator == EvalItem.eOperator.And || item.operator == EvalItem.eOperator.Or) {
				if (tempStack.size() < 2)
					throw new EvaluatorException("BinaryOperatorLessThanTwoValues");
				EvalItem item1 = tempStack.pop();
				EvalItem item2 = tempStack.pop();
				if (item.operator == EvalItem.eOperator.And)
					item1.value = (item1.value && item2.value);
				else
					item1.value = (item1.value || item2.value);
				tempStack.push(item1);
			}
		}
		if (tempStack.size() != 1)
			throw new EvaluatorException("ExtraValues");
		return tempStack.pop().value;
	}
}
