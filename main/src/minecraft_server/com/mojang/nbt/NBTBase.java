package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {
	private String name;

	abstract void write(DataOutput dataOutput1) throws IOException;

	abstract void load(DataInput dataInput1) throws IOException;

	public abstract byte getId();

	protected NBTBase(String string1) {
		if(string1 == null) {
			this.name = "";
		} else {
			this.name = string1;
		}

	}

	public NBTBase setName(String string1) {
		if(string1 == null) {
			this.name = "";
		} else {
			this.name = string1;
		}

		return this;
	}

	public String getName() {
		return this.name == null ? "" : this.name;
	}

	public static NBTBase readNamedTag(DataInput dataInput0) throws IOException {
		byte b1 = dataInput0.readByte();
		if(b1 == 0) {
			return new NBTTagEnd();
		} else {
			String string2 = dataInput0.readUTF();
			NBTBase nBTBase3 = newTag(b1, string2);
			nBTBase3.load(dataInput0);
			return nBTBase3;
		}
	}

	public static void writeNamedTag(NBTBase nBTBase0, DataOutput dataOutput1) throws IOException {
		dataOutput1.writeByte(nBTBase0.getId());
		if(nBTBase0.getId() != 0) {
			dataOutput1.writeUTF(nBTBase0.getName());
			nBTBase0.write(dataOutput1);
		}
	}

	public static NBTBase newTag(byte b0, String string1) {
		switch(b0) {
		case 0:
			return new NBTTagEnd();
		case 1:
			return new NBTTagByte(string1);
		case 2:
			return new NBTTagShort(string1);
		case 3:
			return new NBTTagInt(string1);
		case 4:
			return new NBTTagLong(string1);
		case 5:
			return new NBTTagFloat(string1);
		case 6:
			return new NBTTagDouble(string1);
		case 7:
			return new NBTTagByteArray(string1);
		case 8:
			return new NBTTagString(string1);
		case 9:
			return new NBTTagList(string1);
		case 10:
			return new NBTTagCompound(string1);
		case 11:
			return new NBTTagIntArray(string1);
		default:
			return null;
		}
	}

	public static String getTagName(byte b0) {
		switch(b0) {
		case 0:
			return "TAG_End";
		case 1:
			return "TAG_Byte";
		case 2:
			return "TAG_Short";
		case 3:
			return "TAG_Int";
		case 4:
			return "TAG_Long";
		case 5:
			return "TAG_Float";
		case 6:
			return "TAG_Double";
		case 7:
			return "TAG_Byte_Array";
		case 8:
			return "TAG_String";
		case 9:
			return "TAG_List";
		case 10:
			return "TAG_Compound";
		case 11:
			return "TAG_Int_Array";
		default:
			return "UNKNOWN";
		}
	}

	public abstract NBTBase copy();

	public boolean equals(Object object1) {
		if(object1 != null && object1 instanceof NBTBase) {
			NBTBase nBTBase2 = (NBTBase)object1;
			return this.getId() != nBTBase2.getId() ? false : (this.name == null && nBTBase2.name != null || this.name != null && nBTBase2.name == null ? false : this.name == null || this.name.equals(nBTBase2.name));
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.name.hashCode() ^ this.getId();
	}
}
