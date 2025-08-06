package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase {
	public float data;

	public NBTTagFloat(String string1) {
		super(string1);
	}

	public NBTTagFloat(String string1, float f2) {
		super(string1);
		this.data = f2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeFloat(this.data);
	}

	void load(DataInput dataInput1) throws IOException {
		this.data = dataInput1.readFloat();
	}

	public byte getId() {
		return (byte)5;
	}

	public String toString() {
		return "" + this.data;
	}

	public NBTBase copy() {
		return new NBTTagFloat(this.getName(), this.data);
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagFloat nBTTagFloat2 = (NBTTagFloat)object1;
			return this.data == nBTTagFloat2.data;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return super.hashCode() ^ Float.floatToIntBits(this.data);
	}
}
