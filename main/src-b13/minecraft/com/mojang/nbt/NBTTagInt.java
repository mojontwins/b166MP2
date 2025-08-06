package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase {
	public int data;

	public NBTTagInt(String string1) {
		super(string1);
	}

	public NBTTagInt(String string1, int i2) {
		super(string1);
		this.data = i2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeInt(this.data);
	}

	void load(DataInput dataInput1) throws IOException {
		this.data = dataInput1.readInt();
	}

	public byte getId() {
		return (byte)3;
	}

	public String toString() {
		return "" + this.data;
	}

	public NBTBase copy() {
		return new NBTTagInt(this.getName(), this.data);
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagInt nBTTagInt2 = (NBTTagInt)object1;
			return this.data == nBTTagInt2.data;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return super.hashCode() ^ this.data;
	}
}
