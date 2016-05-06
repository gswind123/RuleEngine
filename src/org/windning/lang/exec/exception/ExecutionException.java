package org.windning.lang.exec.exception;

public class ExecutionException extends Exception {
	public ExecutionException(String desc) {
		super(desc);
	}
	public ExecutionException(Exception e) {
		super(e);
	}
}
