package org.windning.lang.bridge.model;

import java.lang.reflect.Field;

import org.windning.lang.bridge.BridgeContext;
import org.windning.lang.bridge.exception.InvalidNativeValueException;

public class ReferenceField implements NativeField{

	private BridgeContext mContext = null;
	private String mFieldName = "";
	
	private ReferenceField(BridgeContext context, String fieldName){
		mContext = context;
		mFieldName = fieldName;
	}
	
	public static ReferenceField newInstance(BridgeContext context, String fieldName) {
		if(context == null || fieldName == null || fieldName.length() == 0) {
			return null;
		}
		ReferenceField inst = new ReferenceField(context, fieldName);
		return inst;
	} 
	
	@Override
	public Object getValue() throws InvalidNativeValueException{
		String[] fieldNames = mFieldName.split("\\.");
		Object resValue = mContext.getNativeOwner();
		for(String fieldName : fieldNames) {
			if(resValue == null) {
				throw new InvalidNativeValueException("Accessing to ["+fieldName+ "] encounters a null owner.");
			}
			try{
				Field field = resValue.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				resValue = field.get(resValue);
			} catch(Exception e) {
				throw new InvalidNativeValueException("Native owner "+resValue.getClass() + 
						" has not field names " + fieldName);
			}
		}
		return resValue;
	}
	
}
