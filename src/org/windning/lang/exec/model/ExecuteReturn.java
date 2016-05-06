package org.windning.lang.exec.model;

public class ExecuteReturn extends Statement {
	public boolean result;
	
	public ExecuteReturn() {
		result = false;
	}
	public ExecuteReturn(boolean res) {
		result = res;
	}
	
}
