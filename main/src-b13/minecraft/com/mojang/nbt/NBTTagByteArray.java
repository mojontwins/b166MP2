package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
	public byte[] byteArray;

	public NBTTagByteArray(String string1) {
		super(string1);
	}

	public NBTTagByteArray(String string1, byte[] b2) {
		super(string1);
		this.byteArray = b2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeInt(this.byteArray.length);
		dataOutput1.write(this.byteArray);
	}

	void load(DataInput dataInput1) throws IOException {
		int i2 = dataInput1.readInt();
		this.byteArray = new byte[i2];
		dataInput1.readFully(this.byteArray);
	}

	public byte getId() {
		return (byte)7;
	}

	public String toString() {
		return "[" + this.byteArray.length + " bytes]";
	}

	public NBTBase copy() {
		byte[] b1 = new byte[this.byteArray.length];
		System.arraycopy(this.byteArray, 0, b1, 0, this.byteArray.length);
		return new NBTTagByteArray(this.getName(), b1);
	}

	public boolean equals(Object object1) {
		return super.equals(object1) ? Arrays.equals(this.byteArray, ((NBTTagByteArray)object1).byteArray) : false;
	}

	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.byteArray);
	}
}
