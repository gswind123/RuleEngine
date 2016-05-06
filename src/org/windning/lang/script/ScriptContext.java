package org.windning.lang.script;

import java.util.HashMap;
import java.util.Map;

import org.windning.lang.bridge.model.NativeMethod;

public class ScriptContext {
	private Map<String, NativeMethod> mMethodMap = new HashMap<String, NativeMethod>();
	
	public void registerInterface(String methodName, NativeMethod method) {
		mMethodMap.put(methodName, method);
	}
	public NativeMethod getInterface(String methodName) {
		return mMethodMap.get(methodName);
	}
}
