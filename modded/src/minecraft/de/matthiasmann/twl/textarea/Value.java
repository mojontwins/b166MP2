package de.matthiasmann.twl.textarea;

public final class Value {
	public final float value;
	public final Value.Unit unit;
	public static final Value ZERO_PX = new Value(0.0F, Value.Unit.PX);
	public static final Value AUTO = new Value(0.0F, Value.Unit.AUTO);

	public Value(float value, Value.Unit unit) {
		this.value = value;
		this.unit = unit;
		if(unit == null) {
			throw new NullPointerException("unit");
		} else if(unit == Value.Unit.AUTO && value != 0.0F) {
			throw new IllegalArgumentException("value must be 0 for Unit.AUTO");
		}
	}

	public String toString() {
		return this.unit == Value.Unit.AUTO ? this.unit.getPostfix() : this.value + this.unit.getPostfix();
	}

	public boolean equals(Object obj) {
		if(obj instanceof Value) {
			Value other = (Value)obj;
			return this.value == other.value && this.unit == other.unit;
		} else {
			return false;
		}
	}

	public int hashCode() {
		byte hash = 3;
		int hash1 = 17 * hash + Float.floatToIntBits(this.value);
		hash1 = 17 * hash1 + this.unit.hashCode();
		return hash1;
	}

	public static enum Unit {
		PX(false, "px"),
		EM(true, "em"),
		EX(true, "ex"),
		PERCENT(false, "%"),
		AUTO(false, "auto");

		final boolean fontBased;
		final String postfix;

		private Unit(boolean fontBased, String postfix) {
			this.fontBased = fontBased;
			this.postfix = postfix;
		}

		public boolean isFontBased() {
			return this.fontBased;
		}

		public String getPostfix() {
			return this.postfix;
		}
	}
}
