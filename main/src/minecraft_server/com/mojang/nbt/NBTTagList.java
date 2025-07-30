package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NBTTagList extends NBTBase {
	private List<NBTBase> tagList = new ArrayList<NBTBase>();
	private byte tagType;

	public NBTTagList() {
		super("");
	}

	public NBTTagList(String string1) {
		super(string1);
	}

	void write(DataOutput dataOutput1) throws IOException {
		if(this.tagList.size() > 0) {
			this.tagType = ((NBTBase)this.tagList.get(0)).getId();
		} else {
			this.tagType = 1;
		}

		dataOutput1.writeByte(this.tagType);
		dataOutput1.writeInt(this.tagList.size());

		for(int i2 = 0; i2 < this.tagList.size(); ++i2) {
			((NBTBase)this.tagList.get(i2)).write(dataOutput1);
		}

	}

	void load(DataInput dataInput1) throws IOException {
		this.tagType = dataInput1.readByte();
		int i2 = dataInput1.readInt();
		this.tagList = new ArrayList<NBTBase>();

		for(int i3 = 0; i3 < i2; ++i3) {
			NBTBase nBTBase4 = NBTBase.newTag(this.tagType, (String)null);
			nBTBase4.load(dataInput1);
			this.tagList.add(nBTBase4);
		}

	}

	public byte getId() {
		return (byte)9;
	}

	public String toString() {
		return "" + this.tagList.size() + " entries of type " + NBTBase.getTagName(this.tagType);
	}

	public void appendTag(NBTBase nBTBase1) {
		this.tagType = nBTBase1.getId();
		this.tagList.add(nBTBase1);
	}

	public NBTBase tagAt(int i1) {
		return (NBTBase)this.tagList.get(i1);
	}

	public int tagCount() {
		return this.tagList.size();
	}

	public NBTBase copy() {
		NBTTagList nBTTagList1 = new NBTTagList(this.getName());
		nBTTagList1.tagType = this.tagType;
		Iterator<NBTBase> iterator2 = this.tagList.iterator();

		while(iterator2.hasNext()) {
			NBTBase nBTBase3 = (NBTBase)iterator2.next();
			NBTBase nBTBase4 = nBTBase3.copy();
			nBTTagList1.tagList.add(nBTBase4);
		}

		return nBTTagList1;
	}

	public boolean equals(Object object1) {
		if(super.equals(object1)) {
			NBTTagList nBTTagList2 = (NBTTagList)object1;
			if(this.tagType == nBTTagList2.tagType) {
				return this.tagList.equals(nBTTagList2.tagList);
			}
		}

		return false;
	}

	public int hashCode() {
		return super.hashCode() ^ this.tagList.hashCode();
	}
}
