package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;

public class ThreadDownloadImageData {
	public BufferedImage image;
	public int referenceCount = 1;
	public int textureName = -1;
	public boolean textureSetupComplete = false;

	public ThreadDownloadImageData(String string1, HttpTextureProcessor imageBuffer2) {
		(new ThreadDownloadImage(this, string1, imageBuffer2)).start();
	}
}
