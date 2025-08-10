package net.minecraft.src;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class ModTextureAnimation extends TextureFX {
	private final int tickRate;
	private final byte[][] images;
	private int index;
	private int ticks;

	public ModTextureAnimation(int slot, int dst, BufferedImage source, int rate) {
		this(slot, 1, dst, source, rate);
	}

	public ModTextureAnimation(int slot, int size, int dst, BufferedImage source, int rate) {
		super(slot);
		this.index = 0;
		this.ticks = 0;
		this.tileSize = size;
		this.tileImage = dst;
		this.tickRate = rate;
		this.ticks = rate;
		int width = source.getWidth();
		int height = source.getHeight();
		int images = (int)Math.floor((double)(height / width));
		if(images <= 0) {
			throw new IllegalArgumentException("source has no complete images");
		} else {
			int requiredsize = (int)Math.sqrt((double)(this.imageData.length / 4));
			this.images = new byte[images][];
			if(width != requiredsize) {
				BufferedImage i = new BufferedImage(requiredsize, requiredsize * images, 6);
				Graphics2D temp = i.createGraphics();
				temp.drawImage(source, 0, 0, requiredsize, requiredsize * images, 0, 0, width, height, (ImageObserver)null);
				temp.dispose();
				source = i;
			}

			for(int i17 = 0; i17 < images; ++i17) {
				int[] i18 = new int[requiredsize * requiredsize];
				source.getRGB(0, requiredsize * i17, requiredsize, requiredsize, i18, 0, requiredsize);
				this.images[i17] = new byte[requiredsize * requiredsize * 4];

				for(int j = 0; j < i18.length; ++j) {
					int a = i18[j] >> 24 & 255;
					int r = i18[j] >> 16 & 255;
					int g = i18[j] >> 8 & 255;
					int b = i18[j] >> 0 & 255;
					this.images[i17][j * 4 + 0] = (byte)r;
					this.images[i17][j * 4 + 1] = (byte)g;
					this.images[i17][j * 4 + 2] = (byte)b;
					this.images[i17][j * 4 + 3] = (byte)a;
				}
			}

		}
	}

	public void onTick() {
		if(this.ticks >= this.tickRate) {
			++this.index;
			if(this.index >= this.images.length) {
				this.index = 0;
			}

			this.imageData = this.images[this.index];
			this.ticks = 0;
		}

		++this.ticks;
	}
}
