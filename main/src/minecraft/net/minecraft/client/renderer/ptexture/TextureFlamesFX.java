package net.minecraft.client.renderer.ptexture;

import net.minecraft.world.level.tile.Block;

public class TextureFlamesFX extends TextureFX {
	protected float[] field_1133_g = new float[320];
	protected float[] field_1132_h = new float[320];

	public TextureFlamesFX(int i1) {
		super(Block.fire.blockIndexInTexture + i1 * 16);
	}

	public void onTick() {
		int i3;
		float f4;
		int i6;
		for(int i1 = 0; i1 < 16; ++i1) {
			for(int i2 = 0; i2 < 20; ++i2) {
				i3 = 18;
				f4 = this.field_1133_g[i1 + (i2 + 1) % 20 * 16] * (float)i3;

				for(int i5 = i1 - 1; i5 <= i1 + 1; ++i5) {
					for(i6 = i2; i6 <= i2 + 1; ++i6) {
						if(i5 >= 0 && i6 >= 0 && i5 < 16 && i6 < 20) {
							f4 += this.field_1133_g[i5 + i6 * 16];
						}

						++i3;
					}
				}

				this.field_1132_h[i1 + i2 * 16] = f4 / ((float)i3 * 1.0600001F);
				if(i2 >= 19) {
					this.field_1132_h[i1 + i2 * 16] = (float)(Math.random() * Math.random() * Math.random() * 4.0D + Math.random() * (double)0.1F + (double)0.2F);
				}
			}
		}

		float[] f13 = this.field_1132_h;
		this.field_1132_h = this.field_1133_g;
		this.field_1133_g = f13;

		for(i3 = 0; i3 < 256; ++i3) {
			f4 = this.field_1133_g[i3] * 1.8F;
			if(f4 > 1.0F) {
				f4 = 1.0F;
			}

			if(f4 < 0.0F) {
				f4 = 0.0F;
			}

			i6 = (int)(f4 * 155.0F + 100.0F);
			int i7 = (int)(f4 * f4 * 255.0F);
			int i8 = (int)(f4 * f4 * f4 * f4 * f4 * f4 * f4 * f4 * f4 * f4 * 255.0F);
			short s9 = 255;
			if(f4 < 0.5F) {
				s9 = 0;
			}

			this.imageData[i3 * 4 + 0] = (byte)i6;
			this.imageData[i3 * 4 + 1] = (byte)i7;
			this.imageData[i3 * 4 + 2] = (byte)i8;
			this.imageData[i3 * 4 + 3] = (byte)s9;
		}

	}
}
