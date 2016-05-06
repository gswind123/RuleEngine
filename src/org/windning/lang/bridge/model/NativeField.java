package org.windning.lang.bridge.model;

import org.windning.lang.bridge.exception.InvalidNativeValueException;

public interface NativeField {
	Object getValue() throws InvalidNativeValueException;
}
