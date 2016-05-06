/*
 * Created on Dec 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.windning.lang.rule.exception;

/**
 * @author cmayor
 *
 */
public class RuleException extends Exception {

	public RuleException( String s ) {
		super( s );
	}
	public RuleException(Exception e) {
		super(e);
	}
	
	public static void main(String[] args) {
	}
}
