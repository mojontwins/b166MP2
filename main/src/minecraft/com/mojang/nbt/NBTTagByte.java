package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase {
	public byte data;

	public NBTTagByte(String string1) {
		super(string1);
	}

	public NBTTagByte(String string1, byte b2) {
		super(string1);
		this.data = b2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeByte(this.data);
	}

	void load(DataInput dataInput1) throws IOException {
		this.data = dataInput1.readByte();
	}

	public byte getId() {
		return (byte)1;
	}

	public String toString() {
		return "" + this.data;
	}

	public NBTBase copy() {
		return new NBTTagByte(this.getName(), this.data);
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagByte nBTTagByte2 = (NBTTagByte)object1;
			return this.data == nBTTagByte2.data;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return super.hashCode() ^ this.data;
	}
}
