package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase {
	public double data;

	public NBTTagDouble(String string1) {
		super(string1);
	}

	public NBTTagDouble(String string1, double d2) {
		super(string1);
		this.data = d2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeDouble(this.data);
	}

	void load(DataInput dataInput1) throws IOException {
		this.data = dataInput1.readDouble();
	}

	public byte getId() {
		return (byte)6;
	}

	public String toString() {
		return "" + this.data;
	}

	public NBTBase copy() {
		return new NBTTagDouble(this.getName(), this.data);
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagDouble nBTTagDouble2 = (NBTTagDouble)object1;
			return this.data == nBTTagDouble2.data;
		} else {
			return false;
		}
	}

	public int hashCode() {
		long j1 = Double.doubleToLongBits(this.data);
		return super.hashCode() ^ (int)(j1 ^ j1 >>> 32);
	}
}
