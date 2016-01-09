package com.edu.binghamton.cs571;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
public class GrammarStats {

    private final Scanner _scanner;
    private Token _lookahead;
    private Integer terminals = 0;
	private Integer nonTerminals = 0;
	private Integer ruleSets = 0;
	private List<String> nonTerminalLeftHandRuleList= new ArrayList<String>();
	private Set<String> nonTerminalRightHandRuleList= new LinkedHashSet<String>();

  GrammarStats(String fileName) {
    _scanner = new Scanner(fileName, PATTERNS_MAP);
    nextToken();
  }

  /** Recognize a grammar specification.  Return silently if ok, else
   *  signal an error.
   */
  Stats getStats() {
    Stats stats = null;
    try {
      //Calling the top level parsing function
    	try {
			stats = program();
		} catch (ArithmeticParserException e) {
			System.out.println("Error :"+e.getMessage());
		}
    }
    catch (GrammarParseException e) {
      System.err.println(e.getMessage());
    }
    return stats;
  }

  // Parsing function logic code goes here
	public Stats program() throws ArithmeticParserException {
		String tempTerminal = "";
		if (_lookahead.kind == TokenKind.NON_TERMINAL) {
			String tempNonTerminal = _lookahead.lexeme;
			// System.out.println(_lookahead.kind);
			nonTerminals = nonTerminals + 1;
			match(TokenKind.NON_TERMINAL);
			if (_lookahead.kind == TokenKind.COLON) {
				if (!nonTerminalLeftHandRuleList.contains(tempNonTerminal))
					nonTerminalLeftHandRuleList.add(tempNonTerminal);
				else
					throw new ArithmeticParserException(
							"There are multiple rule-set's for the same non-terminal i.e "
									+ "'" + tempNonTerminal + "'");
			}else {
			nonTerminalRightHandRuleList.add(tempNonTerminal);
			}
		} else if (_lookahead.kind == TokenKind.TERMINAL
				|| _lookahead.kind == TokenKind.PIPE
				|| _lookahead.kind == TokenKind.SEMI
				) {
			if (_lookahead.kind == TokenKind.TERMINAL) {
				terminals = terminals + 1;
			}
			 if (_lookahead.kind == TokenKind.SEMI){
				 tempTerminal = _lookahead.lexeme;
			 }
			TokenKind x = (TokenKind) _lookahead.kind;
			match((TokenKind) _lookahead.kind);
			String tempTerminalAfterMatching = _lookahead.lexeme;
			if (tempTerminalAfterMatching.equalsIgnoreCase(tempTerminal)){
				throw new ArithmeticParserException("'" + _lookahead.lexeme
						+ "'" + " in line number " + _lookahead.coords.lineN);
			}
		} else if (_lookahead.kind == TokenKind.COLON) {
			ruleSets = ruleSets + 1;
			match(TokenKind.COLON);
			if (_lookahead.kind != TokenKind.TERMINAL
					&& _lookahead.kind != TokenKind.NON_TERMINAL) {
				throw new ArithmeticParserException("'" + _lookahead.lexeme
						+ "'" + " in line number " + _lookahead.coords.lineN);
			}
		} else if (_lookahead.kind == TokenKind.ERROR) {
			throw new ArithmeticParserException("'" + _lookahead.lexeme + "'"
					+ " in line number " + _lookahead.coords.lineN);
		}
		if (_lookahead.kind != TokenKind.EOF) {
			program();
		}

		RuleSetInfo ruleSetInfo = checkIfAllNonTerminalRulesAreDefined(
				nonTerminalLeftHandRuleList, nonTerminalRightHandRuleList);
		if (!ruleSetInfo.isAllRulesAreDefined()) {
			throw new ArithmeticParserException(
					"There is no rule-set's defined for the non-terminal i.e "
							+ "'" + ruleSetInfo.getNonTerminalValue() + "'");
		}
		RuleSetInfo ruleInfo = checkIfAnyUselessNonTerminalRule(nonTerminalLeftHandRuleList, nonTerminalRightHandRuleList);
		if (!ruleInfo.isAllRulesAreDefined()) {
			throw new ArithmeticParserException(
					"There is no use of the non-terminal i.e "
							+ "'" + ruleInfo.getNonTerminalValue() + "'" + " that is declared");
		}
		Stats stats = new Stats(ruleSets, nonTerminals, terminals);
		return stats;
	}


	private RuleSetInfo checkIfAnyUselessNonTerminalRule(
			List<String> nonTerminalLeftHandRules,
			Set<String> nonTerminalRightHandRules) {
		RuleSetInfo info = new RuleSetInfo();
		for (String leftNonTerminal : nonTerminalLeftHandRules) {
			if (!nonTerminalRightHandRules.contains(leftNonTerminal)) {
				info.setNonTerminalValue(leftNonTerminal);
				info.setAllRulesAreDefined(false);
				return info;
			} else {
				info.setAllRulesAreDefined(true);
				info.setNonTerminalValue(null);
			}
		}
		return info;
	}

	private RuleSetInfo checkIfAllNonTerminalRulesAreDefined(
			List<String> leftHandRuleList, Set<String> rightHandRuleList) {
		RuleSetInfo ruleInfo = new RuleSetInfo();
		for (String nonTerminal : rightHandRuleList) {
			if (!leftHandRuleList.contains(nonTerminal)) {

				ruleInfo.setAllRulesAreDefined(false);
				ruleInfo.setNonTerminalValue(nonTerminal);
				return ruleInfo;
			}
			else {
				ruleInfo.setAllRulesAreDefined(true);
				ruleInfo.setNonTerminalValue(null);
			}
		}
		return ruleInfo;
	}

	class RuleSetInfo {
		private boolean allRulesAreDefined;
		private String nonTerminalValue;

		public boolean isAllRulesAreDefined() {
			return allRulesAreDefined;
		}

		public void setAllRulesAreDefined(boolean allRulesAreDefined) {
			this.allRulesAreDefined = allRulesAreDefined;
		}

		public String getNonTerminalValue() {
			return nonTerminalValue;
		}

		public void setNonTerminalValue(String nonTerminalValue) {
			this.nonTerminalValue = nonTerminalValue;
		}
	}
//We extend RuntimeException since Java's checked exceptions are
  //very cumbersome
  private static class GrammarParseException extends RuntimeException {
    GrammarParseException(String message) {
      super(message);
    }
  }

  private void match(TokenKind kind) {
    if (kind != _lookahead.kind) {
      syntaxError();
    }
    if (kind != TokenKind.EOF) {
      nextToken();
    }
  }

  /** Skip to end of current line and then throw exception */
  private void syntaxError() {
    String message = String.format("%s: syntax error at '%s'",
                                   _lookahead.coords, _lookahead.lexeme);
    throw new GrammarParseException(message);
  }

  private static final boolean DO_TOKEN_TRACE = false;

  private void nextToken() {
    _lookahead = _scanner.nextToken();
    if (DO_TOKEN_TRACE) 
    	System.err.println("token: " + _lookahead);
  }

  /** token kinds for grammar tokens*/
  private static enum TokenKind {
    EOF,
    COLON,
    PIPE,
    SEMI,
    NON_TERMINAL,
    TERMINAL,
    ERROR,
	NL, EXP_OP, ADD_OP, SUB_OP, MUL_OP, DIV_OP, INTEGER, // @tokenKind1@
	LPAREN, RPAREN,
  }

  /** Simple structure to collect grammar statistics */
  private static class Stats {
    final int nRuleSets;
    final int nNonTerminals;
    final int nTerminals;
    Stats(int nRuleSets, int nNonTerminals, int nTerminals) {
      this.nRuleSets = nRuleSets;
      this.nNonTerminals = nNonTerminals;
      this.nTerminals = nTerminals;
    }
    public String toString() {
      return String.format("%d %d %d", nRuleSets, nNonTerminals, nTerminals);
    }
  }

  /** Map from regex to token-kind */
  private static final LinkedHashMap<String, Enum> PATTERNS_MAP =
 new LinkedHashMap<String, Enum>() {
		{
			put("", TokenKind.EOF);
			put("\\s+", null); // ignore whitespace.
			put("\\//.*", null); // ignore // comments
			put("\\:", TokenKind.COLON);
			put("\\|", TokenKind.PIPE);
			put("\\;", TokenKind.SEMI);
			put("[a-z]\\w*", TokenKind.NON_TERMINAL);
			put("[A-Z]\\w*", TokenKind.TERMINAL);
			put(".", TokenKind.ERROR); // catch lexical error in parser
			put("[ \\t]+", null); // ignore linear whitespace.
			put("\n", TokenKind.NL);
			put("\\*\\*", TokenKind.EXP_OP);
			put("\\+", TokenKind.ADD_OP);
			put("\\-", TokenKind.SUB_OP);
			put("\\*", TokenKind.MUL_OP);
			put("\\/", TokenKind.DIV_OP);
			put("\\d+", TokenKind.INTEGER); // @tokenMap1@
			put("\\(", TokenKind.LPAREN);
			put("\\)", TokenKind.RPAREN);

		}};


  private static final String USAGE =
    String.format("usage: java %s GRAMMAR_FILE",
                  GrammarStats.class.getName());

  /** Main program for testing */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println(USAGE);
      System.exit(1);
    }
    GrammarStats grammarStats = new GrammarStats(args[0]);
    //Call getStats method which in turn calls the top level parsing function
    Stats stats = grammarStats.getStats();
    if (stats != null) {
    	// Printing the output to console
      System.out.println(stats);
    }
  }



}