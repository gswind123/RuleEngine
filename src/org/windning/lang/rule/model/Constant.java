/*
 * Created on Dec 12, 2003
 *
 */
package org.windning.lang.rule.model;

import java.util.Date;

import org.windning.lang.bridge.BridgeContext;
import org.windning.lang.rule.exception.RuleException;


/**
 * @author cmayor
 *
 * This class 
 */
public class Constant extends Argument implements Comparable {

	private String value;
	
	/**
	 * @param fieldName
	 */
	public Constant(String s) {
		if( s == null )
			value = "";
		else
			value = s;
	}
	
	@Override
	public String toString() {
		return value;		
	}
	
	@Override
	public Constant getValue(BridgeContext context) throws RuleException {
		return this;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		int compareResult = 0;
		
		if( ! (arg0 instanceof Constant) ) {
			return -1;
		}
		
		try {
			compareResult = getComparable().compareTo( ((Constant)arg0).getComparable() );
		} catch( ClassCastException cce ) {
			compareResult = toString().compareTo( arg0.toString() );
		}

		return compareResult;
	}

	/**
	 * @return
	 */
	private Comparable getComparable() {
		
		
		// We opnly have three types - Double, Date, String
		try {
			Double dub = new Double( Double.parseDouble( value ) );
			return dub;
		} catch( NumberFormatException nfe ) {
		}
		
		try {
			// TODO - Parse date
		    Date d = null;
		} catch( Exception e ) {
		}
		
		return value;
	}

	
	public boolean lessThan( Constant c ) {
		return compareTo( c ) < 0;
	}
	
	public boolean greaterThan( Constant c ) {
		return compareTo( c ) > 0;
	}	
	
	public boolean greaterThanEq( Constant c ) {
		return compareTo( c ) >= 0;
	}
	
	public boolean lessThanEq( Constant c ) {
		return compareTo( c ) <= 0;
	}
	
	public boolean equals( Constant c ) {
		return compareTo( c ) == 0;
	}
	
	public boolean notEquals( Constant c ) {
		return !equals( c );
	}
	
	public boolean contains( Constant c ) {
		return toString().indexOf( c.toString() ) >= 0;		
	}
	
	public boolean endsWith( Constant c ) {
		return toString().endsWith( c.toString() );
	}
	
	public boolean beginsWith( Constant c ) {
		return toString().indexOf( c.toString() ) == 0;
	}
	
	public boolean regex( Constant c ) {
		return false;
	}
	

	
	
	public static void main( String[] args )  {
		
		Constant a = new Constant( "1");
		Constant b = new Constant( "1");
		System.out.println( "Should be 0: " + a.compareTo( b ) );
		
		a = new Constant( "1");
		b = new Constant( "1.1");
		System.out.println( "Should be -1: " + a.compareTo( b ));
		
		a = new Constant( "1");
		b = new Constant( ".1");
		System.out.println( "Should be 1: " + a.compareTo( b ));

		
		a = new Constant( "Hello" );
		b = new Constant( "Hell");
		System.out.println( "Should be true: " + a.contains( b ) );
		
		a = new Constant( "Hello" );
		b = new Constant( "Hell");
		System.out.println( "Should be false: " + b.contains( a ) );
		
		
	}

	
}
