package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase {
	public short data;

	public NBTTagShort(String string1) {
		super(string1);
	}

	public NBTTagShort(String string1, short s2) {
		super(string1);
		this.data = s2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeShort(this.data);
	}

	void load(DataInput dataInput1) throws IOException {
		this.data = dataInput1.readShort();
	}

	public byte getId() {
		return (byte)2;
	}

	public String toString() {
		return "" + this.data;
	}

	public NBTBase copy() {
		return new NBTTagShort(this.getName(), this.data);
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagShort nBTTagShort2 = (NBTTagShort)object1;
			return this.data == nBTTagShort2.data;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return super.hashCode() ^ this.data;
	}
}
