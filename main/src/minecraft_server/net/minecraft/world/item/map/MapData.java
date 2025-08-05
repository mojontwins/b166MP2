package net.minecraft.world.item.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldSavedData;

public class MapData extends WorldSavedData {
	public int xCenter;
	public int zCenter;
	public byte dimension;
	public byte scale;
	public byte[] colors = new byte[16384];
	public int field_28175_g;
	public List<MapInfo> field_28174_h = new ArrayList<MapInfo>();
	private Map<EntityPlayer, MapInfo> field_28172_j = new HashMap<EntityPlayer, MapInfo>();
	public List<MapCoord> playersVisibleOnMap = new ArrayList<MapCoord>();

	public MapData(String string1) {
		super(string1);
	}

	public void readFromNBT(NBTTagCompound nBTTagCompound1) {
		this.dimension = nBTTagCompound1.getByte("dimension");
		this.xCenter = nBTTagCompound1.getInteger("xCenter");
		this.zCenter = nBTTagCompound1.getInteger("zCenter");
		this.scale = nBTTagCompound1.getByte("scale");
		if(this.scale < 0) {
			this.scale = 0;
		}

		if(this.scale > 4) {
			this.scale = 4;
		}

		short s2 = nBTTagCompound1.getShort("width");
		short s3 = nBTTagCompound1.getShort("height");
		if(s2 == 128 && s3 == 128) {
			this.colors = nBTTagCompound1.getByteArray("colors");
		} else {
			byte[] b4 = nBTTagCompound1.getByteArray("colors");
			this.colors = new byte[16384];
			int i5 = (128 - s2) / 2;
			int i6 = (128 - s3) / 2;

			for(int i7 = 0; i7 < s3; ++i7) {
				int i8 = i7 + i6;
				if(i8 >= 0 || i8 < 128) {
					for(int i9 = 0; i9 < s2; ++i9) {
						int i10 = i9 + i5;
						if(i10 >= 0 || i10 < 128) {
							this.colors[i10 + i8 * 128] = b4[i9 + i7 * s2];
						}
					}
				}
			}
		}

	}

	public void writeToNBT(NBTTagCompound nBTTagCompound1) {
		nBTTagCompound1.setByte("dimension", this.dimension);
		nBTTagCompound1.setInteger("xCenter", this.xCenter);
		nBTTagCompound1.setInteger("zCenter", this.zCenter);
		nBTTagCompound1.setByte("scale", this.scale);
		nBTTagCompound1.setShort("width", (short)128);
		nBTTagCompound1.setShort("height", (short)128);
		nBTTagCompound1.setByteArray("colors", this.colors);
	}

	public void func_28169_a(EntityPlayer entityPlayer1, ItemStack itemStack2) {
		if(!this.field_28172_j.containsKey(entityPlayer1)) {
			MapInfo mapInfo3 = new MapInfo(this, entityPlayer1);
			this.field_28172_j.put(entityPlayer1, mapInfo3);
			this.field_28174_h.add(mapInfo3);
		}

		this.playersVisibleOnMap.clear();

		for(int i14 = 0; i14 < this.field_28174_h.size(); ++i14) {
			MapInfo mapInfo4 = (MapInfo)this.field_28174_h.get(i14);
			if(!mapInfo4.entityplayerObj.isDead && mapInfo4.entityplayerObj.inventory.hasItemStack(itemStack2)) {
				float f5 = (float)(mapInfo4.entityplayerObj.posX - (double)this.xCenter) / (float)(1 << this.scale);
				float f6 = (float)(mapInfo4.entityplayerObj.posZ - (double)this.zCenter) / (float)(1 << this.scale);
				byte b7 = 64;
				byte b8 = 64;
				if(f5 >= (float)(-b7) && f6 >= (float)(-b8) && f5 <= (float)b7 && f6 <= (float)b8) {
					byte b9 = 0;
					byte b10 = (byte)((int)((double)(f5 * 2.0F) + 0.5D));
					byte b11 = (byte)((int)((double)(f6 * 2.0F) + 0.5D));
					byte b12 = (byte)((int)((double)(entityPlayer1.rotationYaw * 16.0F / 360.0F) + 0.5D));
					if(this.dimension < 0) {
						int i13 = this.field_28175_g / 10;
						b12 = (byte)(i13 * i13 * 34187121 + i13 * 121 >> 15 & 15);
					}

					if(mapInfo4.entityplayerObj.dimension == this.dimension) {
						this.playersVisibleOnMap.add(new MapCoord(this, b9, b10, b11, b12));
					}
				}
			} else {
				this.field_28172_j.remove(mapInfo4.entityplayerObj);
				this.field_28174_h.remove(mapInfo4);
			}
		}

	}

	public byte[] s_func_28154_a(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		MapInfo mapInfo4 = (MapInfo)this.field_28172_j.get(entityPlayer3);
		if(mapInfo4 == null) {
			return null;
		} else {
			byte[] b5 = mapInfo4.func_28171_a(itemStack1);
			return b5;
		}
	}

	public void func_28170_a(int i1, int i2, int i3) {
		super.markDirty();

		for(int i4 = 0; i4 < this.field_28174_h.size(); ++i4) {
			MapInfo mapInfo5 = (MapInfo)this.field_28174_h.get(i4);
			if(mapInfo5.field_28119_b[i1] < 0 || mapInfo5.field_28119_b[i1] > i2) {
				mapInfo5.field_28119_b[i1] = i2;
			}

			if(mapInfo5.field_28124_c[i1] < 0 || mapInfo5.field_28124_c[i1] < i3) {
				mapInfo5.field_28124_c[i1] = i3;
			}
		}

	}

	public void func_28171_a(byte[] b1) {
		int i2;
		if(b1[0] == 0) {
			i2 = b1[1] & 255;
			int i3 = b1[2] & 255;

			for(int i4 = 0; i4 < b1.length - 3; ++i4) {
				this.colors[(i4 + i3) * 128 + i2] = b1[i4 + 3];
			}

			this.markDirty();
		} else if(b1[0] == 1) {
			this.playersVisibleOnMap.clear();

			for(i2 = 0; i2 < (b1.length - 1) / 3; ++i2) {
				byte b7 = (byte)(b1[i2 * 3 + 1] % 16);
				byte b8 = b1[i2 * 3 + 2];
				byte b5 = b1[i2 * 3 + 3];
				byte b6 = (byte)(b1[i2 * 3 + 1] / 16);
				this.playersVisibleOnMap.add(new MapCoord(this, b7, b8, b5, b6));
			}
		}

	}
}
