/*
 * Created on Dec 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.windning.lang.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.windning.lang.bridge.BridgeContext;
import org.windning.lang.rule.exception.RuleException;
import org.windning.lang.rule.model.BasicExpression;
import org.windning.lang.rule.model.BooleanExpression;
import org.windning.lang.rule.model.Expression;

/**
 * @author cmayor
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Rule {

	public static void main(String[] args) {
		try {
//			ArrayList<String> al = getTokens( "( rule = 100 ) and ( city = \"San Jose\"" );
//			System.out.println( al );
//			al = getTokens( "(velocitycheck_velo1_result == \"true\") OR (velocitycheck_velo2_result == \"true\") OR (velocitycheck_velo3_result == \"true\") OR (velocitycheck_velo4_result == \"true\")");
//			System.out.println( al );
			
			//String rule = "(rulecode  90 ) AND (bill_state = \"CA\")";
//	                String rule = "( rulecode >= 8000 AND rulecode < 9000 AND amount > 1000) OR (rulecode >= 7850 AND rulecode < 7900 AND amount < 1500) OR (8000 > 5)";
	                //String rule = "((rulecode >= 8000) AND (rulecode < 9000) AND (amount > 1000))";
	                //String rule = "(rulecode >= 8000 AND rulecode < 9000 AND amount > 1000)";
	                //String rule = "(((rulecode >= 8000) AND (rulecode < 9000) AND (amount > 1000)) OR ((rulecode >= 9000) AND (rulecode < 9500) AND (amount > 1500)))";
	                //String rule = "( (rulecode >= 8000) AND rulecode < 9000 AND amount > 1000) OR (rulecode >= 7850 AND (rulecode < 7900) AND amount < 1500)";
	        BridgeContext context = new BridgeContext();
	        context.setField("rulecode", "7000");
	        
			String rule = "rulecode <= 8000";
			//BooleanExpression bool = Rule.parse( "(rulecode > 500) AND (bill_state = \"CA\") OR (bill_state = \"TX\")");
			BooleanExpression bool = Rule.parse(context, rule);
			
			System.out.println( bool.evaluate() );
		} catch( Exception e ) {
			e.printStackTrace();
		}		
	}
	
	public static BooleanExpression parse(BridgeContext context, String s) throws RuleException {
		BooleanExpression be = null;
		
		ArrayList<String> tokens = getTokens(s);
		//crude check for parenthesis match
		int openparen = 0;
		int closeparen = 0;
		for (int j = 0 ; j < tokens.size(); j++) {
		    String tok = (String)tokens.get(j);
		    if (tok.equals("("))
		        openparen++;
		    if (tok.equals(")"))
		        closeparen++;
		}
		if (openparen != closeparen)
		    throw new RuleException("Malformed rule, parenthesis count mismatch");
		Iterator<String> i = tokens.iterator();
        return parse(context, i);
	}
	
    protected static BooleanExpression parse(BridgeContext context,  Iterator<String> i ) throws RuleException {
		BooleanExpression bexp = null;
        BooleanExpression be = null;
		Expression lhs = null;
		Expression rhs = null;
		BasicExpression basic = null;
		String operator = null;
		while( i.hasNext() ) {
			String item = i.next();
			if ( item.equals( "(" ) ) {
                bexp  = parse(context, i);
                if (be == null) {
                    be = new BooleanExpression(context, bexp);

                } else if (!be.hasOperand()) {
                }else {
                     if (operator == null)
                        throw new RuleException("Malformed rule");
                     be.addOperand(bexp, operator);
                }
			} else if( item.equals( ")" ) ){
				return be;
			} else if( isQuoted( item ) ) {
				//System.out.println( "Found quoted: "+item);
			} else if( isBoolean( item ) ) {
				//System.out.println( "Found boolean: "+item);
				operator = item;
			} else if( isOperator( item ) ) {
				// Operator
				//System.out.println( "Found operator: "+item);
				
			} else {
                basic = BasicExpression.parse(context, i, item);
                if (be == null) {
                    be = new BooleanExpression(context, basic);
                } else {
                    if (!be.hasOperand()) {
                        be = new BooleanExpression(context, basic);
                    } else {
                        be.addOperand(basic, operator);
                    }
                }
			}
		}
				
		return be;
    }
        
	/**
	 * @param item
	 * @return
	 */
	private static boolean isOperator(String item) {
		return item.equals( ">" ) || item.equals( ">=" ) || item.equals( "<" ) || item.equals( "<=" ) || item.equals( "==" ) || item.equals( "ends" ) || item.equals( "begins" ) || item.equals( "contains" );
	}

	/**
	 * @param item
	 * @return
	 */
	private static boolean isBoolean(String item) {
		return item.equals( "AND" ) || item.equals( "OR" );
	}

	/**
	 * @param item
	 * @return
	 */
	private static boolean isQuoted(String item) {
		return item.startsWith( "\"" ) && item.endsWith( "\"" );
	}

	public static ArrayList<String> getTokens( String s ) {
		StringTokenizer st = new StringTokenizer( s, "()<>= \t\n\r\f\"", true );
		ArrayList<String> al = new ArrayList<String>();
		
		String ntoken = null;
		while( st.hasMoreTokens() ) {
			String token = null;
			if (ntoken != null) {
				token = ntoken;
				ntoken = null;
		    }else {
			    token = st.nextToken();
			}
			String t = token;
			if( token.equals( "\"" ) ) {
				t = token; 
				while( st.hasMoreTokens() ) {
					token = st.nextToken();
					t = t + token;
					if( token.equals( "\"" ) )
						break;
				}
			}
			token = t;
			
			if (token.equals("<") || token.equals(">") || token.equals("=")) {
				if (st.hasMoreTokens()) {
				    ntoken = st.nextToken();
				    if (ntoken.equals("=")) {
				        token = token + ntoken;
				        ntoken = null;
				    } 
				}
			}
			
			if( token.trim().length() != 0 )
				al.add( token );
		}
		
		
		
		return al;
		
	}
	
	public static boolean isToken( String s ) {
		HashMap<String, String> tokens = new HashMap<String, String>();
		tokens.put( "(", "(" );
		tokens.put( ")", ")" );
		tokens.put( ">", ">" );
		tokens.put( "<", "<" );
		tokens.put( "<=", "<=" );
		tokens.put( ">=", ">=" );
		tokens.put( "==", "==" );
		tokens.put( "<>", "<>" );
		
		return tokens.get( s ) != null;
	}
	
	public static boolean isWhiteSpace( String s ) {
		return s.trim().length() == 0;
	}
	
}
