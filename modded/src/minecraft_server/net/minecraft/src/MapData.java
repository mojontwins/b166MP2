package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData extends MapDataBase {
	public int field_28164_b;
	public int field_28163_c;
	public byte field_28162_d;
	public byte field_28161_e;
	public byte[] field_28160_f = new byte[16384];
	public int field_28159_g;
	public List field_28158_h = new ArrayList();
	private Map field_28156_j = new HashMap();
	public List field_28157_i = new ArrayList();

	public MapData(String string1) {
		super(string1);
	}

	public void func_28148_a(NBTTagCompound nBTTagCompound1) {
		this.field_28162_d = nBTTagCompound1.getByte("dimension");
		this.field_28164_b = nBTTagCompound1.getInteger("xCenter");
		this.field_28163_c = nBTTagCompound1.getInteger("zCenter");
		this.field_28161_e = nBTTagCompound1.getByte("scale");
		if(this.field_28161_e < 0) {
			this.field_28161_e = 0;
		}

		if(this.field_28161_e > 4) {
			this.field_28161_e = 4;
		}

		short s2 = nBTTagCompound1.getShort("width");
		short s3 = nBTTagCompound1.getShort("height");
		if(s2 == 128 && s3 == 128) {
			this.field_28160_f = nBTTagCompound1.getByteArray("colors");
		} else {
			byte[] b4 = nBTTagCompound1.getByteArray("colors");
			this.field_28160_f = new byte[16384];
			int i5 = (128 - s2) / 2;
			int i6 = (128 - s3) / 2;

			for(int i7 = 0; i7 < s3; ++i7) {
				int i8 = i7 + i6;
				if(i8 >= 0 || i8 < 128) {
					for(int i9 = 0; i9 < s2; ++i9) {
						int i10 = i9 + i5;
						if(i10 >= 0 || i10 < 128) {
							this.field_28160_f[i10 + i8 * 128] = b4[i9 + i7 * s2];
						}
					}
				}
			}
		}

	}

	public void func_28147_b(NBTTagCompound nBTTagCompound1) {
		nBTTagCompound1.setByte("dimension", this.field_28162_d);
		nBTTagCompound1.setInteger("xCenter", this.field_28164_b);
		nBTTagCompound1.setInteger("zCenter", this.field_28163_c);
		nBTTagCompound1.setByte("scale", this.field_28161_e);
		nBTTagCompound1.setShort("width", (short)128);
		nBTTagCompound1.setShort("height", (short)128);
		nBTTagCompound1.setByteArray("colors", this.field_28160_f);
	}

	public void func_28155_a(EntityPlayer entityPlayer1, ItemStack itemStack2) {
		if(!this.field_28156_j.containsKey(entityPlayer1)) {
			MapInfo mapInfo3 = new MapInfo(this, entityPlayer1);
			this.field_28156_j.put(entityPlayer1, mapInfo3);
			this.field_28158_h.add(mapInfo3);
		}

		this.field_28157_i.clear();

		for(int i14 = 0; i14 < this.field_28158_h.size(); ++i14) {
			MapInfo mapInfo4 = (MapInfo)this.field_28158_h.get(i14);
			if(!mapInfo4.field_28120_a.isDead && mapInfo4.field_28120_a.inventory.func_28010_c(itemStack2)) {
				float f5 = (float)(mapInfo4.field_28120_a.posX - (double)this.field_28164_b) / (float)(1 << this.field_28161_e);
				float f6 = (float)(mapInfo4.field_28120_a.posZ - (double)this.field_28163_c) / (float)(1 << this.field_28161_e);
				byte b7 = 64;
				byte b8 = 64;
				if(f5 >= (float)(-b7) && f6 >= (float)(-b8) && f5 <= (float)b7 && f6 <= (float)b8) {
					byte b9 = 0;
					byte b10 = (byte)((int)((double)(f5 * 2.0F) + 0.5D));
					byte b11 = (byte)((int)((double)(f6 * 2.0F) + 0.5D));
					byte b12 = (byte)((int)((double)(entityPlayer1.rotationYaw * 16.0F / 360.0F) + 0.5D));
					if(this.field_28162_d < 0) {
						int i13 = this.field_28159_g / 10;
						b12 = (byte)(i13 * i13 * 34187121 + i13 * 121 >> 15 & 15);
					}

					if(mapInfo4.field_28120_a.dimension == this.field_28162_d) {
						this.field_28157_i.add(new MapCoord(this, b9, b10, b11, b12));
					}
				}
			} else {
				this.field_28156_j.remove(mapInfo4.field_28120_a);
				this.field_28158_h.remove(mapInfo4);
			}
		}

	}

	public byte[] func_28154_a(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		MapInfo mapInfo4 = (MapInfo)this.field_28156_j.get(entityPlayer3);
		if(mapInfo4 == null) {
			return null;
		} else {
			byte[] b5 = mapInfo4.func_28118_a(itemStack1);
			return b5;
		}
	}

	public void func_28153_a(int i1, int i2, int i3) {
		super.func_28146_a();

		for(int i4 = 0; i4 < this.field_28158_h.size(); ++i4) {
			MapInfo mapInfo5 = (MapInfo)this.field_28158_h.get(i4);
			if(mapInfo5.field_28119_b[i1] < 0 || mapInfo5.field_28119_b[i1] > i2) {
				mapInfo5.field_28119_b[i1] = i2;
			}

			if(mapInfo5.field_28125_c[i1] < 0 || mapInfo5.field_28125_c[i1] < i3) {
				mapInfo5.field_28125_c[i1] = i3;
			}
		}

	}
}
