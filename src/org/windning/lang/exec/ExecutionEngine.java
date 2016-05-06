package org.windning.lang.exec;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.windning.lang.bridge.BridgeContext;
import org.windning.lang.bridge.exception.InvalidNativeValueException;
import org.windning.lang.bridge.model.NativeField;
import org.windning.lang.bridge.model.ValueField;
import org.windning.lang.exec.exception.ExecutionException;
import org.windning.lang.exec.model.ExecuteReturn;
import org.windning.lang.exec.model.MethodInvocation;
import org.windning.lang.exec.model.Statement;

public class ExecutionEngine {
	static private final Pattern MethodInvokePattern = Pattern.compile("^([a-zA-Z0-9$\\.]+)\\(([a-zA-Z0-9,\"\\s$\\.]*)\\)$");
	static public final Pattern ReturnPattern = Pattern.compile("^return[\\s]+([a-zA-Z0-9$]+)$");
	static public final Pattern StringPattern = Pattern.compile("^\"([^\"]*)\"$");
	
	public static ArrayList<Statement> parse(String execCode, BridgeContext context) 
			throws ExecutionException {
		String[] stateInsts = execCode.split(";+");
		boolean hasReturnInst = false;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		for(String inst : stateInsts) {
			Matcher methodMatcher = MethodInvokePattern.matcher(inst);
			if(methodMatcher.find() && methodMatcher.groupCount()>=2) {
				MethodInvocation state = new MethodInvocation();
				state.setMethodName(methodMatcher.group(1));
				String rawArgStr = methodMatcher.group(2);
				rawArgStr = rawArgStr.replaceAll("\\s+", "");
				String[] args = rawArgStr.split(",");
				state.addArguments(parseArguments(args, context));
				statements.add(state);
				continue;
			}
			Matcher returnMatcher = ReturnPattern.matcher(inst);
			if(returnMatcher.find() && returnMatcher.groupCount()>=1) {
				String value = returnMatcher.group(1);
				statements.add(new ExecuteReturn(Boolean.parseBoolean(value)));
				hasReturnInst = true;
			} else {
				throw new ExecutionException("Syntex Error: Invalid statement \""+inst+"\"");
			}
		}
		if(!hasReturnInst) {
			statements.add(new ExecuteReturn(true));
		}
		
		return statements;
	}
	
	private static ArrayList<NativeField> parseArguments(String[] args, BridgeContext context) 
			throws ExecutionException {
		ArrayList<NativeField> fieldArgs = new ArrayList<NativeField>();
		for(String arg : args) {
			NativeField value = ValueField.newInstance(arg);
			if(value == null) { //arg is a reference
				value = context.getField(arg);
			}
			if(value != null) {
				fieldArgs.add(value);
			} else {
				throw new ExecutionException("Syntax Error: Invalid argument \""+ arg + "\".");
			}
		}
		return fieldArgs;
	}
	
	private static class TestModel {
		public String value = "";
		public TestModel childModel = null;
	}
	
	static public void main(String args[]) {
		BridgeContext context = new BridgeContext();
		TestModel nativeOwner = new TestModel();
		nativeOwner.value = "a string in object";
		nativeOwner.childModel = new TestModel();
		nativeOwner.childModel.value = "a string in child model";
		context.setNativeOwner(nativeOwner);
		try {
			context.setField("content", "childModel.value");
		} catch (InvalidNativeValueException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String code = "nativeAlert(content ,100);childFind(0);return 1";
		ArrayList<Statement> statements;
		try {
			statements = parse(code, context);
			for(Statement statement : statements) {
				if(statement instanceof MethodInvocation) {
					MethodInvocation invoke = (MethodInvocation) statement;
					System.out.println(invoke.getMethodName());
					Object[] params = invoke.getArguments();
					for(Object param : params) {
						System.out.println("\t"+param.getClass().getName()+"\t"+param.toString());
					}
				} else if (statement instanceof ExecuteReturn) {
					System.out.println(((ExecuteReturn) statement).result);
				}
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InvalidNativeValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
