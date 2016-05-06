package org.windning.lang.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.windning.lang.bridge.BridgeContext;
import org.windning.lang.bridge.exception.InvalidNativeValueException;
import org.windning.lang.bridge.model.NativeMethod;
import org.windning.lang.exec.ExecutionEngine;
import org.windning.lang.exec.exception.ExecutionException;
import org.windning.lang.exec.model.ExecuteReturn;
import org.windning.lang.exec.model.MethodInvocation;
import org.windning.lang.exec.model.Statement;
import org.windning.lang.rule.Rule;
import org.windning.lang.rule.exception.RuleException;
import org.windning.lang.rule.model.BooleanExpression;
import org.windning.lang.script.exception.ScriptException;
import org.windning.lang.script.model.OperationModel;

public class ScriptEngine {
	static private final String KeyOperates = "operations";
	
	static private final String KeyOptName = "name";
	static private final String KeyOptFields = "fields";
	static private final String KeyOptPosition = "position";
	static private final String KeyOptCond = "condition";
	static private final String KeyOptExec = "execution";
	
	static private final String KeyPosBefore = "before";
	static private final String KeyPosAfter = "after";
	
		
	public static ArrayList<OperationModel> parseScript(String script) throws ScriptException {
		JSONObject scriptObj = JSONObject.fromObject(script);
		ArrayList<OperationModel> operates = new ArrayList<OperationModel>();
		JSONArray jsonOperates;
		if(scriptObj == null || (jsonOperates = scriptObj.getJSONArray(KeyOperates)) == null) {
			return operates;
		}
		int optSize = jsonOperates.size();
		for(int i=0;i<optSize;i++) {
			JSONObject jsonOpt = jsonOperates.getJSONObject(i);
			OperationModel optModel = new OperationModel();
			
			String name = null;
			try{
				jsonOpt.getString(KeyOptName);
			}catch(JSONException e){}
			if((name = jsonOpt.getString(KeyOptName)) != null) {
				optModel.setName(name);
			}
			//fields must be parsed before condition and execution to ensure context
			JSONObject jsonFields = null;
			try{
				jsonFields = jsonOpt.getJSONObject(KeyOptFields);
			}catch(JSONException e){};
			if(jsonFields != null) {
				Set<String> keySet = jsonFields.keySet();
				for(String fieldName : keySet) {
					String value = jsonFields.getString(fieldName);
					if(value == null) {
						throw new ScriptException("Field " + fieldName + " in " + 
								jsonOpt.toString() + " should not be <null>");
					}
					try {
						optModel.setField(fieldName, value);
					} catch (InvalidNativeValueException e) {
						throw new ScriptException(e);
					}
				}
			}
			
			JSONObject jsonPosition = null;
			try{
				jsonPosition = jsonOpt.getJSONObject(KeyOptPosition);
			}catch(JSONException e){}
			if(jsonPosition != null) {
				String beforePos = null;
				String afterPos = null;
				try{
					beforePos = jsonPosition.getString(KeyPosBefore);
				}catch(JSONException e) {}
				try{
					afterPos = jsonPosition.getString(KeyPosAfter);
				}catch(JSONException e) {}
				
				optModel.setPosition(beforePos, afterPos);
			}
			
			String condition = null;
			try{
				condition = jsonOpt.getString(KeyOptCond);
			}catch(JSONException e) {}
			if(condition != null) {
				BooleanExpression condExp;
				try {
					condExp = Rule.parse(optModel.getContext(), condition);
					optModel.setCondition(condExp);
				} catch (RuleException e) {
					e.printStackTrace();
				}
			}
			
			String execution = null;
			try{
				execution = jsonOpt.getString(KeyOptExec);
			}catch(JSONException e) {}
			if(execution != null) {
				try {
					optModel.setExecution(ExecutionEngine.parse(execution, optModel.getContext()));
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			operates.add(optModel);
		}
		
		return operates;
	}
	
	/**
	 * Run a script model's operation
	 * @param operation
	 * @param runtime
	 * @param nativeOwner
	 * @return execution's return value ; true for default
	 * @throws ScriptException 
	 */
	public static boolean runOperation(OperationModel operation, ScriptContext runtime, Object nativeOwner) 
			throws ScriptException {
		BridgeContext bridgeCxt;
		if(operation == null || (bridgeCxt=operation.getContext()) == null) {
			return true;
		}
		bridgeCxt.setNativeOwner(nativeOwner);
		BooleanExpression condExp = operation.getCondition();
		if(condExp == null) {
			return true;
		}
		boolean cond = false;
		try {
			cond = condExp.evaluate();
		} catch (RuleException e) {
			throw new ScriptException(e);
		}
		if(cond) {
			ArrayList<Statement> statements = operation.getExecution();
			for(Statement statement : statements) {
				if(statement instanceof ExecuteReturn) {
					return ((ExecuteReturn) statement).result;
				} else if(statement instanceof MethodInvocation) {
					MethodInvocation invocation = (MethodInvocation) statement;
					NativeMethod method = runtime.getInterface(invocation.getMethodName());
					if(method != null) {
						try {
							method.execute(invocation.getArguments());
						} catch (InvalidNativeValueException e) {
							throw new ScriptException(e);
						}
					}
				}
			}
		}
		return true;
		
	}
	
	private static class TestModel {
		public String value = "";
		public TestModel childModel = null;
	}
	
	public static void main(String args[]) {
		InputStream in = ClassLoader.getSystemResourceAsStream("org/windning/lang/script/test/testScript.rule");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder scriptBuilder = new StringBuilder();
		String line;
		try {
			while((line = reader.readLine()) != null) {
				scriptBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		TestModel nativeOwner = new TestModel();
		nativeOwner.value = "value of TestModel";
		ScriptContext context = new ScriptContext();
		context.registerInterface("nativeAlert", new NativeMethod() {
			@Override
			public void execute(Object[] args) {
				for(int i=0;i<args.length;i++) {
					System.out.print(args[i]+"\t");
				}
				System.out.println("\n");
			}
		});
		context.registerInterface("nativeSum", new NativeMethod() {
			@Override
			public void execute(Object[] args) {
				Integer a = (Integer) args[0];
				Integer b = (Integer) args[1];
				System.out.println(a + b);
			}
		});
		
		String script = scriptBuilder.toString();
		try {
			ArrayList<OperationModel> optList = parseScript(script);
			for(OperationModel optModel : optList) {
				System.out.println("\n"+optModel.getName());
				runOperation(optModel, context, nativeOwner);
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
