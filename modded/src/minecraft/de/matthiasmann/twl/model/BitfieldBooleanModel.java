package de.matthiasmann.twl.model;

public class BitfieldBooleanModel extends HasCallback implements BooleanModel {
	private final IntegerModel bitfield;
	private final int bitmask;

	public BitfieldBooleanModel(IntegerModel bitfield, int bit) {
		if(bitfield == null) {
			throw new NullPointerException("bitfield");
		} else if(bit >= 0 && bit <= 30) {
			if(bitfield.getMinValue() != 0) {
				throw new IllegalArgumentException("bitfield.getMinValue() != 0");
			} else {
				int bitfieldMax = bitfield.getMaxValue();
				if((bitfieldMax & bitfieldMax + 1) != 0) {
					throw new IllegalArgumentException("bitfield.getmaxValue() must eb 2^x");
				} else if(bitfieldMax < 1 << bit) {
					throw new IllegalArgumentException("bit index outside of bitfield range");
				} else {
					this.bitfield = bitfield;
					this.bitmask = 1 << bit;
					bitfield.addCallback(new BitfieldBooleanModel.CB());
				}
			}
		} else {
			throw new IllegalArgumentException("invalid bit index");
		}
	}

	public boolean getValue() {
		return (this.bitfield.getValue() & this.bitmask) != 0;
	}

	public void setValue(boolean value) {
		int oldBFValue = this.bitfield.getValue();
		int newBFValue = value ? oldBFValue | this.bitmask : oldBFValue & ~this.bitmask;
		if(oldBFValue != newBFValue) {
			this.bitfield.setValue(newBFValue);
		}

	}

	class CB implements Runnable {
		public void run() {
			BitfieldBooleanModel.this.doCallback();
		}
	}
}
