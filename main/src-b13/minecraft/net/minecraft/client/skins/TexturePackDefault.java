package net.minecraft.client.skins;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class TexturePackDefault extends TexturePackBase {
	private int texturePackName = -1;
	private BufferedImage texturePackThumbnail;

	public TexturePackDefault() {
		this.texturePackFileName = "Default";
		this.firstDescriptionLine = "The default look of Minecraft";

		try {
			this.texturePackThumbnail = ImageIO.read(TexturePackDefault.class.getResource("/pack.png"));
		} catch (IOException iOException2) {
			iOException2.printStackTrace();
		}

	}

	public void unbindThumbnailTexture(Minecraft mc) {
		if(this.texturePackThumbnail != null) {
			mc.renderEngine.deleteTexture(this.texturePackName);
		}

	}

	public void bindThumbnailTexture(Minecraft mc) {
		if(this.texturePackThumbnail != null && this.texturePackName < 0) {
			this.texturePackName = mc.renderEngine.allocateAndSetupTexture(this.texturePackThumbnail);
		}

		if(this.texturePackThumbnail != null) {
			mc.renderEngine.bindTexture(this.texturePackName);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/unknown_pack.png"));
		}

	}
}
