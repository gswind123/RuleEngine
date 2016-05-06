/*
 * Created on Dec 8, 2003
 *
 */
package org.windning.lang.rule.model;

import org.windning.lang.rule.exception.RuleException;


/**
 * @author cmayor
 *
 */
public class Expression {
	
	protected Expression(){}

	public boolean evaluate() throws RuleException {
		return false;
	}
	
	public String toHQL() {
		return "";
	}
	
	public static Expression parse( String s ) {
		
		return null;
	}
	
	
	
}
