package net.minecraft.src;

public class MapInfo {
	public final EntityPlayer field_28120_a;
	public int[] field_28119_b;
	public int[] field_28125_c;
	private int field_28123_e;
	private int field_28122_f;
	private byte[] field_28121_g;
	final MapData field_28124_d;

	public MapInfo(MapData mapData1, EntityPlayer entityPlayer2) {
		this.field_28124_d = mapData1;
		this.field_28119_b = new int[128];
		this.field_28125_c = new int[128];
		this.field_28123_e = 0;
		this.field_28122_f = 0;
		this.field_28120_a = entityPlayer2;

		for(int i3 = 0; i3 < this.field_28119_b.length; ++i3) {
			this.field_28119_b[i3] = 0;
			this.field_28125_c[i3] = 127;
		}

	}

	public byte[] func_28118_a(ItemStack itemStack1) {
		int i3;
		int i10;
		if(--this.field_28122_f < 0) {
			this.field_28122_f = 4;
			byte[] b2 = new byte[this.field_28124_d.field_28157_i.size() * 3 + 1];
			b2[0] = 1;

			for(i3 = 0; i3 < this.field_28124_d.field_28157_i.size(); ++i3) {
				MapCoord mapCoord4 = (MapCoord)this.field_28124_d.field_28157_i.get(i3);
				b2[i3 * 3 + 1] = (byte)(mapCoord4.field_28202_a + (mapCoord4.field_28204_d & 15) * 16);
				b2[i3 * 3 + 2] = mapCoord4.field_28201_b;
				b2[i3 * 3 + 3] = mapCoord4.field_28205_c;
			}

			boolean z9 = true;
			if(this.field_28121_g != null && this.field_28121_g.length == b2.length) {
				for(i10 = 0; i10 < b2.length; ++i10) {
					if(b2[i10] != this.field_28121_g[i10]) {
						z9 = false;
						break;
					}
				}
			} else {
				z9 = false;
			}

			if(!z9) {
				this.field_28121_g = b2;
				return b2;
			}
		}

		for(int i8 = 0; i8 < 10; ++i8) {
			i3 = this.field_28123_e * 11 % 128;
			++this.field_28123_e;
			if(this.field_28119_b[i3] >= 0) {
				i10 = this.field_28125_c[i3] - this.field_28119_b[i3] + 1;
				int i5 = this.field_28119_b[i3];
				byte[] b6 = new byte[i10 + 3];
				b6[0] = 0;
				b6[1] = (byte)i3;
				b6[2] = (byte)i5;

				for(int i7 = 0; i7 < b6.length - 3; ++i7) {
					b6[i7 + 3] = this.field_28124_d.field_28160_f[(i7 + i5) * 128 + i3];
				}

				this.field_28125_c[i3] = -1;
				this.field_28119_b[i3] = -1;
				return b6;
			}
		}

		return null;
	}
}
