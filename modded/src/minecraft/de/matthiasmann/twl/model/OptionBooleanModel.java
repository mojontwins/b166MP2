package de.matthiasmann.twl.model;

public class OptionBooleanModel extends AbstractOptionModel {
	private final IntegerModel optionState;
	private final int optionCode;

	public OptionBooleanModel(IntegerModel optionState, int optionCode) {
		if(optionState == null) {
			throw new NullPointerException("optionState");
		} else if(optionCode >= optionState.getMinValue() && optionCode <= optionState.getMaxValue()) {
			this.optionState = optionState;
			this.optionCode = optionCode;
		} else {
			throw new IllegalArgumentException("optionCode");
		}
	}

	public boolean getValue() {
		return this.optionState.getValue() == this.optionCode;
	}

	public void setValue(boolean value) {
		if(value) {
			this.optionState.setValue(this.optionCode);
		}

	}

	protected void installSrcCallback(Runnable cb) {
		this.optionState.addCallback(cb);
	}

	protected void removeSrcCallback(Runnable cb) {
		this.optionState.removeCallback(cb);
	}
}
