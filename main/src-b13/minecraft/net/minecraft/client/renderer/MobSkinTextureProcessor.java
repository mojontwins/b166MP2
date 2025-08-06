package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class MobSkinTextureProcessor implements HttpTextureProcessor {
    private int[] pixels;
    private int width;
    private int height;

	public BufferedImage process(BufferedImage bufferedImage1) {
		if(bufferedImage1 == null) {
			return null;
		} else {
			this.width = 64;
			this.height = 32;
			BufferedImage bufferedImage2 = new BufferedImage(this.width, this.height, 2);
			Graphics graphics3 = bufferedImage2.getGraphics();
			graphics3.drawImage(bufferedImage1, 0, 0, (ImageObserver)null);
			graphics3.dispose();
			this.pixels = ((DataBufferInt)bufferedImage2.getRaster().getDataBuffer()).getData();
			this.setNoAlpha(0, 0, 32, 16);
			this.setForceAlpha(32, 0, 64, 32);
			this.setNoAlpha(0, 16, 64, 32);
			boolean z4 = false;

			int i5;
			int i6;
			int i7;
			for(i5 = 32; i5 < 64; ++i5) {
				for(i6 = 0; i6 < 16; ++i6) {
					i7 = this.pixels[i5 + i6 * 64];
					if((i7 >> 24 & 255) < 128) {
						z4 = true;
					}
				}
			}

			if(!z4) {
				for(i5 = 32; i5 < 64; ++i5) {
					for(i6 = 0; i6 < 16; ++i6) {
						i7 = this.pixels[i5 + i6 * 64];
						if((i7 >> 24 & 255) < 128) {
							z4 = true;
						}
					}
				}
			}

			return bufferedImage2;
		}
	}

    private void setForceAlpha(final int x0, final int y0, final int x1, final int y1) {
        if (this.hasAlpha(x0, y0, x1, y1)) {
            return;
        }
        for (int x2 = x0; x2 < x1; ++x2) {
            for (int y2 = y0; y2 < y1; ++y2) {
                final int[] pixels = this.pixels;
                final int n = x2 + y2 * this.width;
                pixels[n] &= 0xFFFFFF;
            }
        }
    }
    
    private void setNoAlpha(final int x0, final int y0, final int x1, final int y1) {
        for (int x2 = x0; x2 < x1; ++x2) {
            for (int y2 = y0; y2 < y1; ++y2) {
                final int[] pixels = this.pixels;
                final int n = x2 + y2 * this.width;
                pixels[n] |= 0xFF000000;
            }
        }
    }
    
    private boolean hasAlpha(final int x0, final int y0, final int x1, final int y1) {
        for (int x2 = x0; x2 < x1; ++x2) {
            for (int y2 = y0; y2 < y1; ++y2) {
                final int pix = this.pixels[x2 + y2 * this.width];
                if ((pix >> 24 & 0xFF) < 128) {
                    return true;
                }
            }
        }
        return false;
    }
}
