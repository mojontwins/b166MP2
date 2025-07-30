package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
	public int[] intArray;

	public NBTTagIntArray(String string1) {
		super(string1);
	}

	public NBTTagIntArray(String string1, int[] i2) {
		super(string1);
		this.intArray = i2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeInt(this.intArray.length);

		for(int i2 = 0; i2 < this.intArray.length; ++i2) {
			dataOutput1.writeInt(this.intArray[i2]);
		}

	}

	void load(DataInput dataInput1) throws IOException {
		int i2 = dataInput1.readInt();
		this.intArray = new int[i2];

		for(int i3 = 0; i3 < i2; ++i3) {
			this.intArray[i3] = dataInput1.readInt();
		}

	}

	public byte getId() {
		return (byte)11;
	}

	public String toString() {
		return "[" + this.intArray.length + " bytes]";
	}

	public NBTBase copy() {
		int[] i1 = new int[this.intArray.length];
		System.arraycopy(this.intArray, 0, i1, 0, this.intArray.length);
		return new NBTTagIntArray(this.getName(), i1);
	}

	public boolean equals(Object object1) {
		if(!super.equals(object1)) {
			return false;
		} else {
			NBTTagIntArray nBTTagIntArray2 = (NBTTagIntArray)object1;
			return this.intArray == null && nBTTagIntArray2.intArray == null || this.intArray != null && this.intArray.equals(nBTTagIntArray2.intArray);
		}
	}

	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.intArray);
	}
}
