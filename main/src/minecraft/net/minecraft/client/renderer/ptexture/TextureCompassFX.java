package net.minecraft.client.renderer.ptexture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class TextureCompassFX extends TextureFX {
	private Minecraft mc;
	private int[] compassIconImageData = new int[256];
	private double field_4229_i;
	private double field_4228_j;

	public TextureCompassFX(Minecraft mc) {
		super(Item.compass.getIconFromDamage(0));
		this.mc = mc;
		this.tileImage = 1;

		try {
			BufferedImage bufferedImage2 = ImageIO.read(Minecraft.class.getResource("/gui/items.png"));
			int i3 = this.iconIndex % 16 * 16;
			int i4 = this.iconIndex / 16 * 16;
			bufferedImage2.getRGB(i3, i4, 16, 16, this.compassIconImageData, 0, 16);
		} catch (IOException iOException5) {
			iOException5.printStackTrace();
		}

	}

	public void onTick() {
		for(int i1 = 0; i1 < 256; ++i1) {
			int i2 = this.compassIconImageData[i1] >> 24 & 255;
			int i3 = this.compassIconImageData[i1] >> 16 & 255;
			int i4 = this.compassIconImageData[i1] >> 8 & 255;
			int i5 = this.compassIconImageData[i1] >> 0 & 255;

			this.imageData[i1 * 4 + 0] = (byte)i3;
			this.imageData[i1 * 4 + 1] = (byte)i4;
			this.imageData[i1 * 4 + 2] = (byte)i5;
			this.imageData[i1 * 4 + 3] = (byte)i2;
		}

		double d20 = 0.0D;
		if(this.mc.theWorld != null && this.mc.thePlayer != null) {
			ChunkCoordinates chunkCoordinates21 = this.mc.theWorld.getSpawnPoint();
			double d23 = (double)chunkCoordinates21.posX - this.mc.thePlayer.posX;
			double d25 = (double)chunkCoordinates21.posZ - this.mc.thePlayer.posZ;
			d20 = (double)(this.mc.thePlayer.rotationYaw - 90.0F) * Math.PI / 180.0D - Math.atan2(d25, d23);
			if(!this.mc.theWorld.worldProvider.canSleepHere()) {
				d20 = Math.random() * (double)(float)Math.PI * 2.0D;
			}
		}

		double d22;
		for(d22 = d20 - this.field_4229_i; d22 < -3.141592653589793D; d22 += Math.PI * 2D) {
		}

		while(d22 >= Math.PI) {
			d22 -= Math.PI * 2D;
		}

		if(d22 < -1.0D) {
			d22 = -1.0D;
		}

		if(d22 > 1.0D) {
			d22 = 1.0D;
		}

		this.field_4228_j += d22 * 0.1D;
		this.field_4228_j *= 0.8D;
		this.field_4229_i += this.field_4228_j;
		double d24 = Math.sin(this.field_4229_i);
		double d26 = Math.cos(this.field_4229_i);

		int i9;
		int i10;
		int i11;
		int i12;
		int i13;
		int i14;
		int i15;
		short s16;
		for(i9 = -4; i9 <= 4; ++i9) {
			i10 = (int)(8.5D + d26 * (double)i9 * 0.3D);
			i11 = (int)(7.5D - d24 * (double)i9 * 0.3D * 0.5D);
			i12 = i11 * 16 + i10;
			i13 = 100;
			i14 = 100;
			i15 = 100;
			s16 = 255;

			this.imageData[i12 * 4 + 0] = (byte)i13;
			this.imageData[i12 * 4 + 1] = (byte)i14;
			this.imageData[i12 * 4 + 2] = (byte)i15;
			this.imageData[i12 * 4 + 3] = (byte)s16;
		}

		for(i9 = -8; i9 <= 16; ++i9) {
			i10 = (int)(8.5D + d24 * (double)i9 * 0.3D);
			i11 = (int)(7.5D + d26 * (double)i9 * 0.3D * 0.5D);
			i12 = i11 * 16 + i10;
			i13 = i9 >= 0 ? 255 : 100;
			i14 = i9 >= 0 ? 20 : 100;
			i15 = i9 >= 0 ? 20 : 100;
			s16 = 255;

			this.imageData[i12 * 4 + 0] = (byte)i13;
			this.imageData[i12 * 4 + 1] = (byte)i14;
			this.imageData[i12 * 4 + 2] = (byte)i15;
			this.imageData[i12 * 4 + 3] = (byte)s16;
		}

	}
}
