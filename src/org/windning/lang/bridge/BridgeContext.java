package org.windning.lang.bridge;

import java.util.HashMap;
import java.util.Map;

import org.windning.lang.bridge.exception.InvalidNativeValueException;
import org.windning.lang.bridge.model.NativeField;
import org.windning.lang.bridge.model.ReferenceField;
import org.windning.lang.bridge.model.ValueField;

public class BridgeContext {
	
	private Object mNativeOwner = null;
	
	private Map<String, NativeField> mFieldMap = new HashMap<String, NativeField>();
	
	public void setNativeOwner(Object owner) {
		mNativeOwner = owner;
	}
	public Object getNativeOwner() {
		return mNativeOwner;
	}
	
	public void setField(String fieldName, String valueStr) throws InvalidNativeValueException {
		NativeField field = ValueField.newInstance(valueStr);
		if(field == null) {
			field = ReferenceField.newInstance(this, valueStr);
		}
		if(field != null) {
			mFieldMap.put(fieldName, field);
		} else {
			throw new InvalidNativeValueException("Field \""+fieldName+
					"\" has an invalid value ["+valueStr+"].");
		}
	}
	
	public NativeField getField(String fieldName) {
		return mFieldMap.get(fieldName);
	}
}
