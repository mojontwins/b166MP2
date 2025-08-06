package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase {
	public long data;

	public NBTTagLong(String string1) {
		super(string1);
	}

	public NBTTagLong(String string1, long j2) {
		super(string1);
		this.data = j2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeLong(this.data);
	}

	void load(DataInput dataInput1) throws IOException {
		this.data = dataInput1.readLong();
	}

	public byte getId() {
		return (byte)4;
	}

	public String toString() {
		return "" + this.data;
	}

	public NBTBase copy() {
		return new NBTTagLong(this.getName(), this.data);
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagLong nBTTagLong2 = (NBTTagLong)object1;
			return this.data == nBTTagLong2.data;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return super.hashCode() ^ (int)(this.data ^ this.data >>> 32);
	}
}
