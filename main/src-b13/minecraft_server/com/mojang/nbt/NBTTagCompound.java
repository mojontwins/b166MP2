package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NBTTagCompound extends NBTBase {
	private Map<String, NBTBase> tagMap = new HashMap<String, NBTBase>();

	public NBTTagCompound() {
		super("");
	}

	public NBTTagCompound(String string1) {
		super(string1);
	}

	void write(DataOutput dataOutput1) throws IOException {
		Iterator<NBTBase> iterator2 = this.tagMap.values().iterator();

		while(iterator2.hasNext()) {
			NBTBase nBTBase3 = (NBTBase)iterator2.next();
			NBTBase.writeNamedTag(nBTBase3, dataOutput1);
		}

		dataOutput1.writeByte(0);
	}

	void load(DataInput dataInput1) throws IOException {
		this.tagMap.clear();

		NBTBase nBTBase2;
		while((nBTBase2 = NBTBase.readNamedTag(dataInput1)).getId() != 0) {
			this.tagMap.put(nBTBase2.getName(), nBTBase2);
		}

	}

	public Collection<NBTBase> getTags() {
		return this.tagMap.values();
	}

	public byte getId() {
		return (byte)10;
	}

	public void setTag(String string1, NBTBase nBTBase2) {
		this.tagMap.put(string1, nBTBase2.setName(string1));
	}

	public void setByte(String string1, byte b2) {
		this.tagMap.put(string1, new NBTTagByte(string1, b2));
	}

	public void setShort(String string1, short s2) {
		this.tagMap.put(string1, new NBTTagShort(string1, s2));
	}

	public void setInteger(String string1, int i2) {
		this.tagMap.put(string1, new NBTTagInt(string1, i2));
	}

	public void setLong(String string1, long j2) {
		this.tagMap.put(string1, new NBTTagLong(string1, j2));
	}

	public void setFloat(String string1, float f2) {
		this.tagMap.put(string1, new NBTTagFloat(string1, f2));
	}

	public void setDouble(String string1, double d2) {
		this.tagMap.put(string1, new NBTTagDouble(string1, d2));
	}

	public void setString(String string1, String string2) {
		this.tagMap.put(string1, new NBTTagString(string1, string2));
	}

	public void setByteArray(String string1, byte[] b2) {
		this.tagMap.put(string1, new NBTTagByteArray(string1, b2));
	}

	public void func_48183_a(String string1, int[] i2) {
		this.tagMap.put(string1, new NBTTagIntArray(string1, i2));
	}

	public void setCompoundTag(String string1, NBTTagCompound nBTTagCompound2) {
		this.tagMap.put(string1, nBTTagCompound2.setName(string1));
	}

	public void setBoolean(String string1, boolean z2) {
		this.setByte(string1, (byte)(z2 ? 1 : 0));
	}

	public NBTBase getTag(String string1) {
		return (NBTBase)this.tagMap.get(string1);
	}

	public boolean hasKey(String string1) {
		return this.tagMap.containsKey(string1);
	}

	public byte getByte(String string1) {
		return !this.tagMap.containsKey(string1) ? 0 : ((NBTTagByte)this.tagMap.get(string1)).data;
	}

	public short getShort(String string1) {
		return !this.tagMap.containsKey(string1) ? 0 : ((NBTTagShort)this.tagMap.get(string1)).data;
	}

	public int getInteger(String string1) {
		return !this.tagMap.containsKey(string1) ? 0 : ((NBTTagInt)this.tagMap.get(string1)).data;
	}

	public long getLong(String string1) {
		return !this.tagMap.containsKey(string1) ? 0L : ((NBTTagLong)this.tagMap.get(string1)).data;
	}

	public float getFloat(String string1) {
		return !this.tagMap.containsKey(string1) ? 0.0F : ((NBTTagFloat)this.tagMap.get(string1)).data;
	}

	public double getDouble(String string1) {
		return !this.tagMap.containsKey(string1) ? 0.0D : ((NBTTagDouble)this.tagMap.get(string1)).data;
	}

	public String getString(String string1) {
		return !this.tagMap.containsKey(string1) ? "" : ((NBTTagString)this.tagMap.get(string1)).data;
	}

	public byte[] getByteArray(String string1) {
		return !this.tagMap.containsKey(string1) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(string1)).byteArray;
	}

	public int[] getIntArray(String string1) {
		return !this.tagMap.containsKey(string1) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(string1)).intArray;
	}

	public NBTTagCompound getCompoundTag(String string1) {
		return !this.tagMap.containsKey(string1) ? new NBTTagCompound(string1) : (NBTTagCompound)this.tagMap.get(string1);
	}

	public NBTTagList getTagList(String string1) {
		return !this.tagMap.containsKey(string1) ? new NBTTagList(string1) : (NBTTagList)this.tagMap.get(string1);
	}

	public boolean getBoolean(String string1) {
		return this.getByte(string1) != 0;
	}

	public String toString() {
		return "" + this.tagMap.size() + " entries";
	}

	public NBTBase copy() {
		NBTTagCompound compoundTag = new NBTTagCompound(this.getName());
		Iterator<String> iterator2 = this.tagMap.keySet().iterator();

		while(iterator2.hasNext()) {
			String string3 = (String)iterator2.next();
			compoundTag.setTag(string3, ((NBTBase)this.tagMap.get(string3)).copy());
		}

		return compoundTag;
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagCompound nBTTagCompound2 = (NBTTagCompound)object1;
			return this.tagMap.entrySet().equals(nBTTagCompound2.tagMap.entrySet());
		} else {
			return false;
		}
	}

	public int hashCode() {
		return super.hashCode() ^ this.tagMap.hashCode();
	}
}
