/*
 * Created on Dec 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.windning.lang.rule.model;

import java.util.Iterator;

import org.windning.lang.bridge.BridgeContext;
import org.windning.lang.rule.exception.RuleException;

/**
 * @author cmayor
 *
 */
public class BasicExpression extends Expression {

	private BridgeContext mContext;
	private Argument lhs;
	private boolean isNot;
	private Argument rhs;
	private String operator;
	
	private static final String EQUAL = "==";
	private static final String NOT_EQUAL = "<>";
	private static final String GREATER_THAN = ">";
	private static final String LESS_THAN = "<";
	private static final String GREATER_THAN_EQUAL = ">=";
	private static final String LESS_THAN_EQUAL = "<=";
	
	private static final String CONTAINS = "contains";
	private static final String BEGINS_WITH = "begins";
	private static final String ENDS_WITH = "ends";
	private static final String REGEX = "regex";
	
	/**
	 * @param a
	 * @param string
	 * @param b
	 */
	protected BasicExpression(BridgeContext context, Argument a, String op, Argument b) {
		mContext = context;
		lhs = a;
		rhs = b;
		operator = op;
		
	}

	protected BasicExpression(BridgeContext context) {
		mContext = context;
		lhs = null;
		rhs = null;
		operator = null;
	}
	
	public String toHQL() {
		return "";
	}
	
	public boolean evaluate() throws RuleException {
		if( lhs == null )
			throw new RuleException( "No left operand" );
		
		if( rhs == null )
			throw new RuleException( "No right operand" );
		
		if( operator == null ) 
			throw new RuleException( "No operator" );
		
		
		Constant l = lhs.getValue(mContext);
		Constant r = rhs.getValue(mContext);
		
		if( l == null )
			l = new Constant( "null" );
		
		if( r == null )
			r = new Constant( "null");
		
		if( operator.equalsIgnoreCase( EQUAL ) ) {
			
			return l.equals( r );
			
		} else if( operator.equalsIgnoreCase( NOT_EQUAL ) ) {
			
			return l.notEquals( r );
			
		} else if( operator.equalsIgnoreCase( GREATER_THAN ) ) {
			
			return l.greaterThan( r );			
			
		} else if( operator.equalsIgnoreCase( LESS_THAN ) ) {
			
			return l.lessThan( r );
			
		} else if( operator.equalsIgnoreCase( GREATER_THAN_EQUAL ) ) {
			
			return l.greaterThanEq( r );	
			
		} else if( operator.equalsIgnoreCase( LESS_THAN_EQUAL ) ) {
			
			return l.lessThanEq( r );
			
		} else if( operator.equalsIgnoreCase( CONTAINS ) ) {
			
			return l.contains( r );
			
		} else if( operator.equalsIgnoreCase( BEGINS_WITH ) ) {
			
			return l.beginsWith( r );
			
		} else if( operator.equalsIgnoreCase( ENDS_WITH ) ) {
			
			return l.endsWith( r );
			
		} else if( operator.equalsIgnoreCase( REGEX) ) {
			
			return l.regex( r );
			
		} else {
			throw new RuleException( "Unrecognized operator: " + operator);
		}
		
	}
	
	public String toString() {
		String lhsString = null;
		String opString = null;
		String rhsString = null;
	
		if( lhs != null ) 
			lhsString = lhs.toString();
		
		if( operator != null ) 
			opString = operator.toString();
		
		if( rhs != null )
			rhsString = rhs.toString();
		
		
		return lhsString + " " + opString + " " + rhsString;
	}

	public static void main(String[] args) {
		try {
			BridgeContext context = new BridgeContext();
			Constant a = new Constant( "1");
			Constant b = new Constant( "1");
			String operator = "<";		
			evalAndPrint(context, a, operator, b );
			
			operator = ">";
			evalAndPrint(context, a, operator, b );

			operator = "==";
			evalAndPrint(context, a, operator, b );
			
			operator = "<";
			a = new Constant( "1" );
			b = new Constant( "2" );
			evalAndPrint(context, a, operator, b );
			
			operator = "begins";
			a = new Constant( "hello" );
			b = new Constant( "hell" );
			evalAndPrint(context, a, operator, b );	
			
			evalAndPrint(context, b, operator, a );
			
			a = new Constant( "hello" );
			b = new Constant( "1.1" );
			operator = ">";
			evalAndPrint(context, a, operator, b );

			a = new Constant( "rule.1000" );
			b = new Constant( "1000" );
			operator = "ends";
			evalAndPrint(context, a, operator, b );			
			
		} catch( Exception e ) {
			e.printStackTrace();
		
		}
		
	}
	
	private static void evalAndPrint(BridgeContext context, Constant a, String o, Constant b ) throws RuleException {
		BasicExpression be = new BasicExpression(context, a, o, b );
		System.out.println( a + " " + o + " " + b + " = " + be.evaluate());
	}
	/**
	 * @return Returns the lhs.
	 */
	public Argument getLhs() {
		return lhs;
	}

	/**
	 * @param lhs The lhs to set.
	 */
	public void setLhs(Argument lhs) {
		this.lhs = lhs;
	}

	/**
	 * @return Returns the operator.
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator The operator to set.
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @return Returns the rhs.
	 */
	public Argument getRhs() {
		return rhs;
	}

	/**
	 * @param rhs The rhs to set.
	 */
	public void setRhs(Argument rhs) {
		this.rhs = rhs;
	}

	/**
	 * Parse a BasicExpression give an iterator of Strings
	 * @param i
	 * @param tok
	 * @return
	 */
	public static BasicExpression parse(BridgeContext context, Iterator<String> i, String tok) throws RuleException{
		Argument lhs = null;
		Argument rhs = null;
		String operator = null;
		String s = tok;
		if(s == null && rhs == null) {
			s = i.next();
		}
		
		do {
			if (s.equals("(") || s.equals(")") || ((lhs != null) && (rhs != null) && ( operator != null))) {
				break;
			} else if( lhs == null ) {
				lhs = Argument.parse( s );
			} else if( operator == null ) {
				operator = s;
			} else {
				rhs = Argument.parse( s );
			}
			
			if(i.hasNext()) {
				s = i.next();
			} else {
				break;
			}
		} while((rhs == null));
		
		BasicExpression basic = new BasicExpression(context, lhs, operator, rhs);
		//basic.validate();
		//System.out.println( "Just parsed: "+basic);
		return basic;
	}

	/**
	 * 
	 */
	private void validate() throws RuleException {
		// TODO Auto-generated method stub
		if( lhs == null ) {
			throw new RuleException( "Left operand missing");
		}
		
		if( operator == null ) {
			throw new RuleException( "Operator missing" );
		}
		
		if( rhs == null ) {
			throw new RuleException( "Right operand missing" );
		}
		
	}

}
