package org.windning.lang.bridge.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.windning.lang.bridge.exception.InvalidNativeValueException;

public class ValueField implements NativeField{
	static public final Pattern StringPattern = Pattern.compile("^\"([^\"]*)\"$");
	
	private Object mValue = null;

	private ValueField(Object value) {
		mValue = value;
	}
	
	/**
	 * @param valueStr : String representing a constant value literally
	 * @return ValueField, null if valueStr is not a recognizable constant
	 */
	public static ValueField newInstance(String valueStr) {
		Matcher strMatcher = StringPattern.matcher(valueStr);
		Object value = null;
		//String
		if(strMatcher.find() && strMatcher.groupCount()>=1) {
			value = strMatcher.group(1);
		}
		//Integer
		if(value == null) {
			try{
				value = Integer.parseInt(valueStr);
			}catch(NumberFormatException e) {
				
			}
		}
		//Float
		if(value == null) {
			try{
				value = Double.parseDouble(valueStr);
			}catch(NumberFormatException e) {
				
			}
		}
		//Boolean
		if(value == null) {
			if(valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
				value = Boolean.parseBoolean(valueStr);
			}
		}
		
		if(value == null) {
			return null;
		} else {
			return new ValueField(value);
		}
	}
	
	@Override
	public Object getValue() throws InvalidNativeValueException{
		return mValue;
	}
}
