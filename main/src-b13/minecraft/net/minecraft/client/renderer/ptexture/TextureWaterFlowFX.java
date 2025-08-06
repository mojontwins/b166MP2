package net.minecraft.client.renderer.ptexture;

import net.minecraft.world.GameRules;
import net.minecraft.world.level.tile.Block;

public class TextureWaterFlowFX extends TextureFX {
	protected float[] field_1138_g = new float[256];
	protected float[] field_1137_h = new float[256];
	protected float[] field_1136_i = new float[256];
	protected float[] field_1135_j = new float[256];
	private int tickCounter = 0;

	public TextureWaterFlowFX() {
		super(Block.waterMoving.blockIndexInTexture + 1);
		this.tileSize = 2;
	}

	public void onTick() {
		++this.tickCounter;

		int i1;
		int i2;
		float f3;
		int i5;
		int i6;
		for(i1 = 0; i1 < 16; ++i1) {
			for(i2 = 0; i2 < 16; ++i2) {
				f3 = 0.0F;

				for(int i4 = i2 - 2; i4 <= i2; ++i4) {
					i5 = i1 & 15;
					i6 = i4 & 15;
					f3 += this.field_1138_g[i5 + i6 * 16];
				}

				this.field_1137_h[i1 + i2 * 16] = f3 / 3.2F + this.field_1136_i[i1 + i2 * 16] * 0.8F;
			}
		}

		for(i1 = 0; i1 < 16; ++i1) {
			for(i2 = 0; i2 < 16; ++i2) {
				this.field_1136_i[i1 + i2 * 16] += this.field_1135_j[i1 + i2 * 16] * 0.05F;
				if(this.field_1136_i[i1 + i2 * 16] < 0.0F) {
					this.field_1136_i[i1 + i2 * 16] = 0.0F;
				}

				this.field_1135_j[i1 + i2 * 16] -= 0.3F;
				if(Math.random() < 0.2D) {
					this.field_1135_j[i1 + i2 * 16] = 0.5F;
				}
			}
		}

		float[] f12 = this.field_1137_h;
		this.field_1137_h = this.field_1138_g;
		this.field_1138_g = f12;

		for(i2 = 0; i2 < 256; ++i2) {
			f3 = this.field_1138_g[i2 - this.tickCounter * 16 & 255];
			if(f3 > 1.0F) {
				f3 = 1.0F;
			}

			if(f3 < 0.0F) {
				f3 = 0.0F;
			}

			float f13 = f3 * f3;
			int i7, i8;
			
			if (!GameRules.boolRule("colouredWater")) {
				i5 = (int)(32.0F + f13 * 32.0F);
				i6 = (int)(50.0F + f13 * 64.0F);
				i7 = 255;
				i8 = (int)(146.0F + f13 * 50.0F);
			} else {
				i5 = (int)(128.0F + f13 * 64.0F);
				i6 = i5;
				i7 = i5;
				i8 = (int)(100.0F + f13 * 50.0F);
			}

			this.imageData[i2 * 4 + 0] = (byte)i5;
			this.imageData[i2 * 4 + 1] = (byte)i6;
			this.imageData[i2 * 4 + 2] = (byte)i7;
			this.imageData[i2 * 4 + 3] = (byte)i8;
		}

	}
}
