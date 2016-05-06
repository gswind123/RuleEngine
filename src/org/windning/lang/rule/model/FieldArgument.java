package org.windning.lang.rule.model;

import org.windning.lang.bridge.BridgeContext;
import org.windning.lang.bridge.exception.InvalidNativeValueException;
import org.windning.lang.bridge.model.NativeField;
import org.windning.lang.rule.exception.RuleException;

public class FieldArgument extends Argument{

	private String mExp;
	
	public FieldArgument(String exp) {
		mExp = exp;
	}
	
	@Override
	public Constant getValue(BridgeContext context) throws RuleException {
		NativeField field = context.getField(mExp);
		if(field == null) {
			throw new RuleException("Field "+mExp+" not found.");
		}
		try{
			return new Constant(field.getValue().toString());
		}catch(InvalidNativeValueException e) {
			throw new RuleException(e);
		}catch(NullPointerException e) {
			throw new RuleException("Field \""+ mExp + "\" results to be null.");
		}
	}

}
