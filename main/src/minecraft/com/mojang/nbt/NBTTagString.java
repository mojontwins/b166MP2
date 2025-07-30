package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
	public String data;

	public NBTTagString(String string1) {
		super(string1);
	}

	public NBTTagString(String string1, String string2) {
		super(string1);
		this.data = string2;
		if(string2 == null) {
			throw new IllegalArgumentException("Empty string not allowed");
		}
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeUTF(this.data);
	}

	void load(DataInput dataInput1) throws IOException {
		this.data = dataInput1.readUTF();
	}

	public byte getId() {
		return (byte)8;
	}

	public String toString() {
		return "" + this.data;
	}

	public NBTBase copy() {
		return new NBTTagString(this.getName(), this.data);
	}

	public boolean equals(Object object1) {
		if(!super.equals(object1)) {
			return false;
		} else {
			NBTTagString nBTTagString2 = (NBTTagString)object1;
			return this.data == null && nBTTagString2.data == null || this.data != null && this.data.equals(nBTTagString2.data);
		}
	}

	public int hashCode() {
		return super.hashCode() ^ this.data.hashCode();
	}
}
