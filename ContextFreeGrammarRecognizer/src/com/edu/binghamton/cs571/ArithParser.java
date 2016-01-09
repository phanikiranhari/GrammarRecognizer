package com.edu.binghamton.cs571;
/*package com.plang.project1;

public class ArithParser {

	public static enum TokenKind {
		EOF, NL, EXP_OP, ADD_OP, SUB_OP, MUL_OP, DIV_OP, INTEGER, // @tokenKind1@
		LPAREN, RPAREN, ERROR,

	}

	private Scanner _scanner;
	private Token _lookahead;
	private static final boolean DO_TOKEN_TRACE = false;

	public ArithParser() {
		_scanner = new Scanner(Scanner.PATTERNS_MAP);
		_scanner.nextToken(); // prime lookahead
	}

	private void match(TokenKind kind) {
		if (kind != _lookahead.kind) {
			syntaxError();
		}
		if (kind != TokenKind.EOF) {
			nextToken();
		}
	}

	private void syntaxError() {
		String message = String.format("%s: syntax error at '%s'",
				_lookahead.coords, _lookahead.lexeme);
		System.out.println(message);
	}

	private void nextToken() {
		_lookahead = _scanner.nextToken();
		if (DO_TOKEN_TRACE)
			System.err.println("token: " + _lookahead);
	}

	
	 * Parser for program : EOF | expr ADD_OP program ;
	 
	public void program() throws ArithmeticParserException {

		if (_lookahead.kind == TokenKind.EOF) {
			match(TokenKind.EOF);
		} else {
			int value = expr();
			System.out.println("Value is " + value);
			match(TokenKind.ADD_OP);
			program();

		}
	}

	
	 * Parser for expr expr : term | exprRest ;
	 

	private int expr() {
		int t = term();
		return exprRest(t);
	}

	private int exprRest(int valueSoFar) {
		if (_lookahead.kind == TokenKind.MUL_OP) {
			match(TokenKind.MUL_OP);
			int t = term();
			return exprRest(valueSoFar * t);
		} else {
			// EMPTY
			return valueSoFar;
		}
	}

	private int term() {
		int value = 0;
		if (_lookahead.kind == TokenKind.INTEGER) {
			value = Integer.parseInt(_lookahead.lexeme);
			match(TokenKind.INTEGER);
			}

		return value;
	}
}
*/