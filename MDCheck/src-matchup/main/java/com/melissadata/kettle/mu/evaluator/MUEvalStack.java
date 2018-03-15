package com.melissadata.kettle.mu.evaluator;


import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import com.melissadata.kettle.mu.evaluator.Functions.FunctionRec;

public class MUEvalStack {
	
	private int tokenLength = 0;
	private RowMetaInterface rowMeta;
	private VariableSpace varSpace;

	private EvalItem newItem;
	private EvalItem peekItem;
	
	private enum eTokenTypes {
		BeginningOfExpression,
		EndOfExpression,
		Column,
		Variable,
		Constant,
		Operator,
		UnaryOperator,
		Cast,
		Function,
		LeftParenthesis,
		Comma,
		RightParenthesis,
		Value /* = (Column | Variable | Constant)*/
	}
	
	// Enumerations used for deciding what to dow with a particular token:
	private enum eStackOperations {
		PushToTemp, // Push new item into TempStack
		MoveAndRetry, // Pop TempStack into EvalStack, try again
		DeleteFromTemp, // Pop TempStack, delete it, delete new item
		MoveAndDelete, // Pop TempStack into EvalStack, delete new item
		IgnoreToken, // Delete new item
		Error // Unbalanced parenthesis or syntax error
	}
	
	

	private static Stack<EvalItem> evalStack = new Stack<EvalItem>();
	
	
	private static final eStackOperations[][] stackOperations = {
						/*Ternary*/                        /*LogicalOr*/                 /*LogicalAnd*/                /*BitwiseOr*/                 /*BitwiseExlusiveOr*/         /*BitwiseAnd*/                /*Equal*/                     /*NotEqual*/                  /*Greater*/                   /*GreaterEqual*/              /*Less*/                      /*LessEqual*/                 /*Plus*/                      /*Minus*/                     /*Multiply*/                  /*Divide*/                    /*Modulus*/                   /*LogicalNot*/                /*BitwiseNot*/                /*Negative*/                  /*Cast*/               /*LeftParenthesis*/         /*RightParenthesis*/            /*Comma*/                     /*Function*/
/* Ternary            */ {eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* LogicalOr          */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* LogicalAnd         */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* BitwiseOr          */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* BitwiseExclusiveOr */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* BitwiseAnd         */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Equal              */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* NotEqual           */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Greater            */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* GreaterEqual       */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Less               */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* LessEqual          */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Plus               */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Minus              */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Multiply           */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Divide             */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Modulus            */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* LogicalNot         */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* BitwiseNot         */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Negative           */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* Cast               */ {eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp,eStackOperations.MoveAndRetry  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp},
/* LeftParenthesis    */ {eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.DeleteFromTemp,eStackOperations.Error       ,eStackOperations.PushToTemp},
/* RightParenthesis   */ {eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.MoveAndRetry,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error     ,eStackOperations.Error         ,eStackOperations.Error       ,eStackOperations.Error     },
/* Comma              */ {eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.MoveAndRetry,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error       ,eStackOperations.Error     ,eStackOperations.Error         ,eStackOperations.Error       ,eStackOperations.Error     },
/* Function           */ {eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.MoveAndRetry,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp  ,eStackOperations.PushToTemp,eStackOperations.MoveAndDelete ,eStackOperations.IgnoreToken ,eStackOperations.PushToTemp}};

	


	/**
	 * (Code borrowed from SSIS MD component)
	 * 
	 * The StringToStack function takes a string and converts it into a Reverse-Polish 
	 * notation (aka, converts infix to postfix notation). The Reverse-Polish notation 
	 * is stored on a stack, which is later evaluated in the Evaluate method. The 
	 * algorithm that performs this conversion is quite common, and probably the most 
	 * widely used method. Simplified: 
	 * 
	 *	1 Set up two LIFO stacks: EvalStack and TempStack. 
	 *	2 Parse string for tokens (operator, field, etc), left to right. 
	 *	3 Depending on what's at the top of each of the two stacks, perform one 
	 *	  of these operations: 
	 *		A Push the new token on one of the stacks 
	 *		B Move a token from the top of one stack to the top of the other.
	 *		C Remove (forget) the token. 
	 *		D Remove (delete) a token from the top of one stack. 
	 *
	 *	  The StackOperations[,] table below drives this decision tree. The resultant 
	 *	  decision is obtained by looking at what's at the top of the TempStack and 
	 *	  the current token (NewItem). It is essentially what drives operator precedence 
	 *	  and the magic of parenthesis. 
	 *	4 When all done, TempStack should be empty, and the EvalStack contains 
	 *	  a postfix notated expression. 
	 *
	 * StringToStack is performed only once on an incoming expression. Once in Reverse- 
	 * Polish notation, it can be evaluated as often as needed through the Evaluate 
	 * method (even if field values change). This is much more efficient than 
	 * performing this conversion every time the expression needs evalating.
	 * 
	 *
	 * @throws EvaluatorException 
	 * @throws KettleException 
	 */
	public Stack<EvalItem> stringToStack(String cleanExp) throws EvaluatorException {
		Stack<EvalItem> tempStack = new Stack<EvalItem>();
		evalStack.clear();
		eTokenTypes lastToken, thisToken, nextToken;

		boolean unaryOk = true;

		lastToken = eTokenTypes.BeginningOfExpression;
		tokenLength = 0;
		newItem = null;
		for (int i = 0; i < cleanExp.length();) {
			newItem = null;

			thisToken = ParseToken(cleanExp.substring(i), unaryOk, false);
			nextToken = ParseTokenPeek(cleanExp.substring(/* i + */tokenLength), unaryOk);

			CheckSyntax(cleanExp.substring(i), lastToken, thisToken, nextToken);

			boolean stacked = false;
			while (!stacked) {
				// Determine what should be done with the newToken we just created...
				//   Column, constant, variables always go onto EvalStack:
				if (newItem.type == EvalItem.eTypes.Column || newItem.type == EvalItem.eTypes.Constant || newItem.type == EvalItem.eTypes.Variable) {
					evalStack.push(newItem);
					stacked = true;

					//   tempStack empty, put the newItem there:
				} else if (tempStack.size() == 0) {
					tempStack.push(newItem);
					stacked = true;

					//   else, look at top of tempStack and see what to do based on what it is and what's
					//   at the top of tempStack (using the eStackOperations above). This is what dictates
					//   parenthesis handling and operator precedence:
				} else {

					switch (stackOperations[tempStack.peek().operator.ordinal()][newItem.operator.ordinal()]) {
					case PushToTemp: // Push new item onto TempStack
						tempStack.push(newItem);
						stacked = true;
						break;
					case MoveAndRetry: // Move top item from tempStack over to EvalStack and try again
						evalStack.push(tempStack.pop());
						//	goto StackLoop;
						break;
					case DeleteFromTemp: // Delete top item from tempStak and forget about newItem
						tempStack.pop();
						stacked = true;
						break;
					case MoveAndDelete: // Move top item from tempStack over to EvalStack, forget about newItem
						evalStack.push(tempStack.pop());
						stacked = true;
						break;
					case IgnoreToken: // Forget about newItem
						stacked = true;
						break;
					case Error: // Error!
						throw new EvaluatorException("Syntax error at: StS 1");

					}
				}
			}
			lastToken = thisToken;
			i = tokenLength;

		}

		// Move any remaining items from tempStack to EvalStack:
		while (tempStack.size() > 0) {
			evalStack.push(tempStack.pop());
		}

		return evalStack;
	}
	
	// Syntax checking. We're checking to ensure that a token is not adjacent to an illegal one.
	//   Some syntax errors will slip right through the infix to postfix conversion.
	private void CheckSyntax(String expression, eTokenTypes prevTok, eTokenTypes thisTok, eTokenTypes nextTok) throws EvaluatorException {

		// Adjacent values (ie, "VALUE 5 > 20"):
		if ((thisTok == eTokenTypes.Value) && ((prevTok == eTokenTypes.Value) || (nextTok == eTokenTypes.Value)))
			throw new EvaluatorException("Syntax error at: CS 1");

		// Adjacent binary operators (ie, "VALUE && || VALUE"):
		else if (thisTok == eTokenTypes.Operator && (prevTok == eTokenTypes.Operator || nextTok == eTokenTypes.Operator))
			throw new EvaluatorException("Syntax error at: CS 2  " + thisTok.name());

		// Hanging binary operators (ie, "% VALUE"):
		else if (thisTok == eTokenTypes.Operator && (prevTok == eTokenTypes.BeginningOfExpression || nextTok == eTokenTypes.EndOfExpression))
			throw new EvaluatorException("Syntax error at: CS 3");

		// Adjacent binary and unary operators (ie, "-*VALUE):
		else if (thisTok == eTokenTypes.UnaryOperator && nextTok == eTokenTypes.Operator)
			throw new EvaluatorException("Syntax error at: CS 4");

		// Unexpected left parenthesis (ie, "VALUE ("):
		else if ((thisTok == eTokenTypes.LeftParenthesis)
				&& ((prevTok == eTokenTypes.Value || prevTok == eTokenTypes.RightParenthesis) || (nextTok == eTokenTypes.Operator || nextTok == eTokenTypes.RightParenthesis || nextTok == eTokenTypes.EndOfExpression)))
			throw new EvaluatorException("Syntax error at: CS 5");

		// Unexpected right parenthesis (ie, ") VALUE"):
		else if (thisTok == eTokenTypes.RightParenthesis
				&& ((prevTok == eTokenTypes.BeginningOfExpression || prevTok == eTokenTypes.Operator) || (nextTok == eTokenTypes.Value || nextTok == eTokenTypes.LeftParenthesis)))
			throw new EvaluatorException("Syntax error at: CS 6");

	}

	// Looks at the start of expression and returns back what expression element it
	//   is identified as (ie, constant, operator, etc). The length output parameter
	//   is used to shift ahead that many characters, that's where we'll start looking
	//   for the next token.
	private eTokenTypes ParseToken(String expression, boolean unaryOk,
			boolean peek) throws EvaluatorException {
		int lHold = 0;

		if (expression.equals("")) {
			return eTokenTypes.EndOfExpression;

		} else if ((lHold = ParseNumeric(expression, tokenLength, peek)) > 0) {
			if (!peek)
				tokenLength += lHold;
			unaryOk = false;
			return eTokenTypes.Constant;

		} else if ((lHold = ParseString(expression, peek)) > 0) {
			if (!peek)
				tokenLength += lHold;
			unaryOk = false;
			return eTokenTypes.Constant;

		} else if ((lHold = ParseCast(expression, peek)) > 0) {
			
			if (!peek)
				tokenLength += lHold;
			unaryOk = false;
			return eTokenTypes.Cast;

		} else if ((lHold = ParseOperator(expression, false, peek)) > 0) {
			if (!peek) {
				tokenLength += lHold;
				return OperatorTokenType(newItem);
			} else {
				return OperatorTokenType(peekItem);
			}

		} else if ((lHold = ParseFunction(expression, peek)) > 0) {
			if (!peek)
				tokenLength += lHold;
			unaryOk = true;
			return eTokenTypes.Function;

		} else if ((lHold = ParseMUFunction(expression, peek)) > 0){
			if (!peek)
				tokenLength += lHold;
			unaryOk = true;
			return eTokenTypes.Function;		

		}else if ((lHold = ParseColumnName(expression, peek)) > 0) {
		
			if (!peek)
				tokenLength += lHold;
			unaryOk = false;
			return eTokenTypes.Column;

		} else if ((lHold = ParseVariableName(expression, peek)) > 0) {
			if (!peek)
				tokenLength += lHold;
			unaryOk = false;
			return eTokenTypes.Constant;
		}
		throw new EvaluatorException("Could not parse token: " + expression);

	}

	// Same as above, in cases when we don't care about unaryOk (ie, we're just peeking ahead).
	private eTokenTypes ParseTokenPeek(String expression, boolean unaryOk)
			throws EvaluatorException {
		boolean unary = unaryOk;

		return ParseToken(expression, unary, true);
	}

	// Helper function used to equate certain operators into eTokenTypes (ie parenthesis and casts):
	private eTokenTypes OperatorTokenType(EvalItem item) {
		if (item.operator == EvalItem.eOperators.LeftParenthesis)
			return eTokenTypes.LeftParenthesis;
		else if (item.operator == EvalItem.eOperators.RightParenthesis)
			return eTokenTypes.RightParenthesis;
		else if (item.parameterCount == 1)
			return eTokenTypes.Cast;
		return eTokenTypes.Operator;
	}

	private Pattern decimalPattern = Pattern.compile("(?=^[0-9])\\b([0-9]+?\\.[0-9]+)\\b");
	private Pattern integerPattern = Pattern.compile("(?=^[0-9])\\b([0-9]+)\\b");

	Matcher numericMatcher;

	private int ParseNumeric(String str, int len, boolean peek)
			throws EvaluatorException {
		numericMatcher = decimalPattern.matcher(str);
		if (numericMatcher.find()) {
			String sDouble = numericMatcher.group();
			double dblValue;
			try {
				dblValue = Double.parseDouble(sDouble);
			} catch (NumberFormatException fe) {
				throw new EvaluatorException("Bad Double: " + sDouble);
			}
			if (peek)
				peekItem = new EvalItem(dblValue);
			else
				newItem = new EvalItem(dblValue);
			len = sDouble.length();
			return len;
		}
		
		numericMatcher = integerPattern.matcher(str);
		if (numericMatcher.find()) {
			String sInteger = numericMatcher.group();
			int intValue
			;
			try {
				intValue = Integer.parseInt(sInteger);
			} catch (NumberFormatException fe) {
				throw new EvaluatorException("Bad Int: " + sInteger);
			}
			if (peek)
				peekItem = new EvalItem(intValue);
			else
				newItem = new EvalItem(intValue);

			len = sInteger.length();
			return len;
		}

		return 0;
	}
	
	private int ParseString(String str, boolean peek) {
		if (str.startsWith("\"") && str.substring(1).contains("\"")) {

			int end = str.substring(1).indexOf("\"");
			String strValue = str.substring(1, end + 1);

			if (peek)
				peekItem = new EvalItem(strValue);
			else
				newItem = new EvalItem(strValue);
			return strValue.length() + 2;
		}

		return 0;
	}


	// Operators - longest ones come first (all 2 character ones first, then the singles):
	private static Operators.OperatorRec[] OperatorList = new Operators.OperatorRec[] {
		new Operators.OperatorRec("||",EvalItem.eOperators.LogicalOr, Operators.EvalLogicalOr, Operators.TestLogicalOr,2),new Operators.OperatorRec("&&",EvalItem.eOperators.LogicalAnd,Operators.EvalLogicalAnd,Operators.TestLogicalAnd,2),
		new Operators.OperatorRec("==",EvalItem.eOperators.Equal,Operators.EvalEqual,Operators.TestEqual,2),new Operators.OperatorRec("!=",EvalItem.eOperators.NotEqual,Operators.EvalNotEqual,Operators.TestNotEqual,2),
		new Operators.OperatorRec(">=",EvalItem.eOperators.GreaterEqual,Operators.EvalGreaterEqual,Operators.TestGreaterEqual,2),new Operators.OperatorRec("<=",EvalItem.eOperators.LessEqual,Operators.EvalLessEqual,Operators.TestLessEqual, 2),
		new Operators.OperatorRec("?",EvalItem.eOperators.Ternary,Operators.EvalTernary1,Operators.TestTernary1,1),new Operators.OperatorRec(":",EvalItem.eOperators.Ternary,Operators.EvalTernary3,Operators.TestTernary3,3),
		new Operators.OperatorRec("!",EvalItem.eOperators.LogicalNot,Operators.EvalLogicalNot,Operators.TestLogicalNot,1),/*new OperatorRec("|",EvalItem.eOperators.BitwiseOr,EvalBitwiseOr,TestBitwiseOr,2),
		new OperatorRec("&",EvalItem.eOperators.BitwiseAnd,EvalBitwiseAnd,TestBitwiseAnd,2),new OperatorRec("^",EvalItem.eOperators.BitwiseExclusiveOr,EvalBitwiseExclusiveOr,TestBitwiseExclusiveOr,2),*/
		new Operators.OperatorRec(">",EvalItem.eOperators.Greater,Operators.EvalGreater,Operators.TestGreater,2),new Operators.OperatorRec("<",EvalItem.eOperators.Less,Operators.EvalLess,Operators.TestLess,2),
		new Operators.OperatorRec("+",EvalItem.eOperators.Plus,Operators.EvalPlus,Operators.TestPlus,2),new Operators.OperatorRec("-",EvalItem.eOperators.Minus,Operators.EvalMinus,Operators.TestMinus,2),
		new Operators.OperatorRec("*",EvalItem.eOperators.Multiply,Operators.EvalMultiply,Operators.TestMultiply,2),new Operators.OperatorRec("/",EvalItem.eOperators.Divide,Operators.EvalDivide,Operators.TestDivide,2),
		new Operators.OperatorRec("%",EvalItem.eOperators.Modulus,Operators.EvalModulus,Operators.TestModulus,2),/*new OperatorRec("~",EvalItem.eOperators.BitwiseNot,EvalBitwiseNot,TestBitwiseNot,2),*/
		new Operators.OperatorRec("(",EvalItem.eOperators.LeftParenthesis,null,null,0),new Operators.OperatorRec(")",EvalItem.eOperators.RightParenthesis,null,null,2),
		new Operators.OperatorRec(",",EvalItem.eOperators.Comma,null,null,0)};
	
	private int ParseOperator(String str, boolean unaryOk, boolean peek) throws EvaluatorException {

		for (int i = 0; i < OperatorList.length; i++) {

			if (str.length() >= OperatorList[i].OperatorStr.length() && OperatorList[i].OperatorStr.equals(str.substring(0, OperatorList[i].OperatorStr.length()))) {

				if (!unaryOk && (OperatorList[i].Operator == EvalItem.eOperators.LogicalNot || OperatorList[i].Operator == EvalItem.eOperators.BitwiseNot))
					throw new EvaluatorException("Syntax Error at Pares Operator");

				if (unaryOk && OperatorList[i].Operator == EvalItem.eOperators.Minus) {
					if (peek)
						peekItem = new EvalItem(EvalItem.eOperators.Negative, Operators.EvalUnaryMinus, Operators.TestUnaryMinus, "-", 1);
					else
						newItem = new EvalItem(EvalItem.eOperators.Negative, Operators.EvalUnaryMinus, Operators.TestUnaryMinus, "-", 1);

				} else {
					if (peek)
						peekItem = new EvalItem(OperatorList[i].Operator, OperatorList[i].Function, OperatorList[i].TestFunction, OperatorList[i].OperatorStr, OperatorList[i].ParameterCount);
					else
						newItem = new EvalItem(OperatorList[i].Operator, OperatorList[i].Function, OperatorList[i].TestFunction, OperatorList[i].OperatorStr, OperatorList[i].ParameterCount);

				}

				EvalItem.eOperators oper;
				if (peek)
					oper = peekItem.operator;
				else
					oper = newItem.operator;

				unaryOk = !(oper == EvalItem.eOperators.LogicalNot || oper == EvalItem.eOperators.BitwiseNot || oper == EvalItem.eOperators.Negative || oper == EvalItem.eOperators.RightParenthesis);

				return OperatorList[i].OperatorStr.length();

			}
		}

		return 0;
	}

	// Functions - alphabetical order please:
	private static FunctionRec[] FunctionList = new FunctionRec[] { new FunctionRec("ABS(", Functions.EvalAbs, Functions.TestAbs, 1),
			new FunctionRec("CEILING(", Functions.EvalCeiling, Functions.TestCeiling, 1), new FunctionRec("CODEPOINT(", Functions.EvalCodePoint, Functions.TestCodePoint, 1),
			new FunctionRec("DATEADD(", Functions.EvalDateAdd, Functions.TestDateAdd, 3), new FunctionRec("DATEDIFF(", Functions.EvalDateDiff, Functions.TestDateDiff, 3),
			new FunctionRec("DATEPART(", Functions.EvalDatePart, Functions.TestDatePart, 2), new FunctionRec("DAY(", Functions.EvalDay, Functions.TestDay, 1),
			new FunctionRec("EXP(", Functions.EvalExp, Functions.TestExp, 1), new FunctionRec("FINDSTRING(", Functions.EvalFindString, Functions.TestFindString, 3),
			new FunctionRec("FLOOR(", Functions.EvalFloor, Functions.TestFloor, 1), new FunctionRec("GETDATE(", Functions.EvalGetDate, Functions.TestGetDate, 0),
			new FunctionRec("GETUTCDATE(", Functions.EvalGetUTCDate, Functions.TestGetUTCDate, 0), new FunctionRec("HEX(", Functions.EvalHex, Functions.TestHex, 1),
			new FunctionRec("ISNULL(", Functions.EvalIsNull, Functions.TestIsNull, 1), new FunctionRec("LEFT(", Functions.EvalLeft, Functions.TestLeft, 2),
			new FunctionRec("LEN(", Functions.EvalLen, Functions.TestLen, 1), new FunctionRec("LN(", Functions.EvalLn, Functions.TestLn, 1),
			new FunctionRec("LOG(", Functions.EvalLog, Functions.TestLog, 1), new FunctionRec("LOWER(", Functions.EvalLower, Functions.TestLower, 1),
			new FunctionRec("LTRIM(", Functions.EvalLTrim, Functions.TestLTrim, 1), new FunctionRec("MONTH(", Functions.EvalMonth, Functions.TestMonth, 1),
			new FunctionRec("NULL(", Functions.EvalNull, Functions.TestNull, 1), new FunctionRec("POWER(", Functions.EvalPower, Functions.TestPower, 2),
			new FunctionRec("REPLACE(", Functions.EvalReplace, Functions.TestReplace, 3), new FunctionRec("REPLACENULL(", Functions.EvalReplaceNull, Functions.TestReplaceNull, 2),
			new FunctionRec("REPLICATE(", Functions.EvalReplicate, Functions.TestReplicate, 2), new FunctionRec("REVERSE(", Functions.EvalReverse, Functions.TestReverse, 1),
			new FunctionRec("RIGHT(", Functions.EvalRight, Functions.TestRight, 2), new FunctionRec("ROUND(", Functions.EvalRound, Functions.TestRound, 2),
			new FunctionRec("RTRIM(", Functions.EvalRTrim, Functions.TestRTrim, 1), new FunctionRec("SIGN(", Functions.EvalSign, Functions.TestSign, 1),
			new FunctionRec("SQRT(", Functions.EvalSqrt, Functions.TestSqrt, 1), new FunctionRec("SQUARE(", Functions.EvalSquare, Functions.TestSquare, 1),
			new FunctionRec("SUBSTRING(", Functions.EvalSubString, Functions.TestSubString, 3), new FunctionRec("TOKEN(", Functions.EvalToken, Functions.TestToken, 3),
			new FunctionRec("TOKENCOUNT(", Functions.EvalTokenCount, Functions.TestTokenCount, 2), new FunctionRec("TRIM(", Functions.EvalTrim, Functions.TestTrim, 1),
			new FunctionRec("UPPER(", Functions.EvalUpper, Functions.TestUpper, 1), new FunctionRec("YEAR(", Functions.EvalYear, Functions.TestYear, 1) };

	private int ParseFunction(String str, boolean peek) {

		for (int i = 0; i < FunctionList.length; i++) {

			if (str.length() >= FunctionList[i].FunctionStr.length() && FunctionList[i].FunctionStr.equalsIgnoreCase(str.substring(0, FunctionList[i].FunctionStr.length()))) {
				if (peek)
					peekItem = new EvalItem(FunctionList[i].Function, FunctionList[i].TestFunction, FunctionList[i].FunctionStr.substring(0, FunctionList[i].FunctionStr.length() - 1),
							FunctionList[i].ParameterCount);
				else
					newItem = new EvalItem(FunctionList[i].Function, FunctionList[i].TestFunction, FunctionList[i].FunctionStr.substring(0, FunctionList[i].FunctionStr.length() - 1),
							FunctionList[i].ParameterCount);

				return FunctionList[i].FunctionStr.length();

			}
		}

		return 0;
	}

	private static FunctionRec[] MatchUPFunctions = new FunctionRec[] { new FunctionRec("ADDRESSSCORE(", Functions.EvalAddressScore, Functions.TestAddressScore, 1),
			new FunctionRec("DATAQUALITYSCORE(", Functions.EvalDataQualityScore, Functions.TestDataQualityScore, 1),
			new FunctionRec("EMAILSCORE(", Functions.EvalEmailScore, Functions.TestEmailScore, 1), new FunctionRec("GEOCODESCORE(", Functions.EvalGeoCodeScore, Functions.TestGeoCodeScore, 1),
			new FunctionRec("NAMESCORE(", Functions.EvalNameScore, Functions.TestNameScore, 1),
			//new FunctionRec("PADLEFT(",EvalPadLeft,TestPadLeft,3),
			//new FunctionRec("PADRIGHT(",EvalPadRight,TestPadRight,3),
			new FunctionRec("PHONESCORE(", Functions.EvalPhoneScore, Functions.TestPhoneScore, 1) };

	private int ParseMUFunction(String str, boolean peek) {

		for (int i = 0; i < MatchUPFunctions.length; i++) {

			if (str.length() >= MatchUPFunctions[i].FunctionStr.length() && MatchUPFunctions[i].FunctionStr.equalsIgnoreCase(str.substring(0, MatchUPFunctions[i].FunctionStr.length()))) {
				if (peek)
					peekItem = new EvalItem(MatchUPFunctions[i].Function, MatchUPFunctions[i].TestFunction, MatchUPFunctions[i].FunctionStr.substring(0, MatchUPFunctions[i].FunctionStr.length() - 1),
							MatchUPFunctions[i].ParameterCount);
				else
					newItem = new EvalItem(MatchUPFunctions[i].Function, MatchUPFunctions[i].TestFunction, MatchUPFunctions[i].FunctionStr.substring(0, MatchUPFunctions[i].FunctionStr.length() - 1),
							MatchUPFunctions[i].ParameterCount);

				return MatchUPFunctions[i].FunctionStr.length();

			}
		}

		return 0;
	}

	public void setColNames(RowMetaInterface colNames) {
		this.rowMeta = colNames;
	}

	public class VariableRec {
		public String Name = "";
		public EvalItem Value = null;

		public VariableRec(String name, EvalItem value) {
			Name = name;
			Value = value;
		}
	}

	private int ParseColumnName(String str, boolean peek) {

		for (int c = 0; c < rowMeta.size(); c++) {

			if (rowMeta.getValueMeta(c) != null) {
				String columnName = rowMeta.getValueMeta(c).getName();
				if (str.length() >= columnName.length() && str.substring(0, columnName.length()).equals(columnName)) {

					VariableRec colRec = new VariableRec(columnName, new EvalItem(""));
					Evaluator.ColumnValues.add(colRec);

					if (peek)
						peekItem = new EvalItem(EvalItem.eTypes.Column, columnName, rowMeta.getValueMeta(c).getType());
					else
						newItem = new EvalItem(EvalItem.eTypes.Column, columnName, rowMeta.getValueMeta(c).getType());

					return columnName.length();
				}
			}
		}

		return 0;
	}

	// Casts - 
	private static Casts.CastRec[] CastList = new Casts.CastRec[] {

	new Casts.CastRec("(DT_DOUBLE", Casts.EvalCastDT_DOUBLE, Casts.TestCastDT_DOUBLE, 1), new Casts.CastRec("(DT_INT", Casts.EvalCastDT_INT, Casts.TestCastDT_INT, 1),
			new Casts.CastRec("(DT_BIGNUMBER", Casts.EvalCastDT_BIGNUMBER, Casts.TestCastDT_BIGNUMBER, 1), new Casts.CastRec("(DT_STRING", Casts.EvalCastDT_STRING, Casts.TestCastDT_STRING, 1),
			new Casts.CastRec("(DT_NULL", Casts.EvalCastDT_NULL, Casts.TestCastDT_NULL, 1), new Casts.CastRec("(DT_DATE", Casts.EvalCastDT_DATE, Casts.TestCastDT_DATE, 1),
			new Casts.CastRec("(DT_TIMESTAMP", Casts.EvalCastDT_TIMESTAMP, Casts.TestCastDT_TIMESTAMP, 1), new Casts.CastRec("(DT_INETADDRESS", Casts.EvalCastDT_INETADDRESS, Casts.TestCastDT_INETADDRESS, 1),
			new Casts.CastRec("(DT_BOOL", Casts.EvalCastDT_BOOL, Casts.TestCastDT_BOOL, 1) };

	private int ParseCast(String str, boolean peek) throws EvaluatorException {

		for (int i = 0; i < CastList.length; i++) {
			if (str.length() >= CastList[i].CastType.length() && CastList[i].CastType.equalsIgnoreCase(str.substring(0, CastList[i].CastType.length()))) {

				if (CastList[i].CastType.equals("(DT_DATE")) {
					if (!str.contains(")"))
						throw new EvaluatorException("Syntax Error " + CastList[i].CastType);

					String[] paramList = str.substring(1, str.indexOf(')')/* - 1 */).split(",");
					if (paramList.length != 2)
						throw new EvaluatorException("Syntax Error " + CastList[i].CastType + " missing date format");

					if (peek)
						peekItem = new EvalItem(CastList[i].Function, CastList[i].TestFunction, CastList[i].CastType + ")", CastList[i].ParameterCount, paramList[1], 0);
					else
						newItem = new EvalItem(CastList[i].Function, CastList[i].TestFunction, CastList[i].CastType + ")", CastList[i].ParameterCount, paramList[1], 0);

					return CastList[i].CastType.length() + paramList[1].length() + 2;
				} else {

					if (peek)
						peekItem = new EvalItem(CastList[i].Function, CastList[i].TestFunction, CastList[i].CastType + ")", CastList[i].ParameterCount);
					else
						newItem = new EvalItem(CastList[i].Function, CastList[i].TestFunction, CastList[i].CastType + ")", CastList[i].ParameterCount);

					return CastList[i].CastType.length() + 1;
				}
			}
		}

		return 0;

	}

	public void setVariableSpace(VariableSpace space) {
		this.varSpace = space;
	}

	private int ParseVariableName(String str, boolean peek) {
		String[] varNames = varSpace.listVariables();
		for (int c = 0; c < varNames.length; c++) {

			if (varNames[c] != null) {
				if (str.length() >= varNames[c].length() && str.substring(0, varNames[c].length()).equals(varNames[c])) {

					VariableRec varRec = new VariableRec(varNames[c], new EvalItem(""));
					Evaluator.variableValues.add(varRec);

					if (peek)
						peekItem = new EvalItem(EvalItem.eTypes.Variable, varNames[c], EvalItem.eDataType.DT_STRING);
					else
						newItem = new EvalItem(EvalItem.eTypes.Variable, varNames[c], EvalItem.eDataType.DT_STRING);

					return varNames[c].length();
				}
			}
		}

		return 0;
	}

	public static Stack<EvalItem> getEvalStack() {
		return evalStack;
	}

}
