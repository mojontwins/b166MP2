package net.minecraft.world.item.map;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class MapInfo {
	public final EntityPlayer entityplayerObj;
	public int[] field_28119_b;
	public int[] field_28124_c;
	private int field_28122_e;
	private int field_28121_f;
	private byte[] s_field_28121_g;
	final MapData mapDataObj;

	public MapInfo(MapData mapData1, EntityPlayer entityPlayer2) {
		this.mapDataObj = mapData1;
		this.field_28119_b = new int[128];
		this.field_28124_c = new int[128];
		this.field_28122_e = 0;
		this.field_28121_f = 0;
		this.entityplayerObj = entityPlayer2;

		for(int i3 = 0; i3 < this.field_28119_b.length; ++i3) {
			this.field_28119_b[i3] = 0;
			this.field_28124_c[i3] = 127;
		}

	}

	public byte[] func_28171_a(ItemStack itemStack1) {
		int i3;
		int i10;
		if(--this.field_28121_f < 0) {
			this.field_28121_f = 4;
			byte[] b2 = new byte[this.mapDataObj.playersVisibleOnMap.size() * 3 + 1];
			b2[0] = 1;

			for(i3 = 0; i3 < this.mapDataObj.playersVisibleOnMap.size(); ++i3) {
				MapCoord mapCoord4 = (MapCoord)this.mapDataObj.playersVisibleOnMap.get(i3);
				b2[i3 * 3 + 1] = (byte)(mapCoord4.field_28217_a + (mapCoord4.iconRotation & 15) * 16);
				b2[i3 * 3 + 2] = mapCoord4.centerX;
				b2[i3 * 3 + 3] = mapCoord4.centerZ;
			}

			boolean z9 = true;
			if(this.s_field_28121_g != null && this.s_field_28121_g.length == b2.length) {
				for(i10 = 0; i10 < b2.length; ++i10) {
					if(b2[i10] != this.s_field_28121_g[i10]) {
						z9 = false;
						break;
					}
				}
			} else {
				z9 = false;
			}

			if(!z9) {
				this.s_field_28121_g = b2;
				return b2;
			}
		}

		for(int i8 = 0; i8 < 10; ++i8) {
			i3 = this.field_28122_e * 11 % 128;
			++this.field_28122_e;
			if(this.field_28119_b[i3] >= 0) {
				i10 = this.field_28124_c[i3] - this.field_28119_b[i3] + 1;
				int i5 = this.field_28119_b[i3];
				byte[] b6 = new byte[i10 + 3];
				b6[0] = 0;
				b6[1] = (byte)i3;
				b6[2] = (byte)i5;

				for(int i7 = 0; i7 < b6.length - 3; ++i7) {
					b6[i7 + 3] = this.mapDataObj.colors[(i7 + i5) * 128 + i3];
				}

				this.field_28124_c[i3] = -1;
				this.field_28119_b[i3] = -1;
				return b6;
			}
		}

		return null;
	}
}
