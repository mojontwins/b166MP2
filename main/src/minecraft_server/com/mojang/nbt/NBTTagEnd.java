package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {
	public NBTTagEnd() {
		super((String)null);
	}

	void load(DataInput dataInput1) throws IOException {
	}

	void write(DataOutput dataOutput1) throws IOException {
	}

	public byte getId() {
		return (byte)0;
	}

	public String toString() {
		return "END";
	}

	public NBTBase copy() {
		return new NBTTagEnd();
	}

	public boolean equals(Object object1) {
		return super.equals(object1);
	}
}
