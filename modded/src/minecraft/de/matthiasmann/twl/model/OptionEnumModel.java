package de.matthiasmann.twl.model;

public class OptionEnumModel extends AbstractOptionModel {
	private final EnumModel optionState;
	private final Enum optionCode;

	public OptionEnumModel(EnumModel optionState, Enum optionCode) {
		if(optionState == null) {
			throw new NullPointerException("optionState");
		} else if(optionCode == null) {
			throw new NullPointerException("optionCode");
		} else {
			this.optionState = optionState;
			this.optionCode = optionCode;
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
