package net.minecraft.client.skins;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.Minecraft;

public abstract class TexturePackBase {
	public String texturePackFileName;
	public String firstDescriptionLine;
	public String secondDescriptionLine;
	public String texturePackID;

	public void func_6482_a() {
	}

	public void closeTexturePackFile() {
	}

	public void func_6485_a(Minecraft mc) throws IOException {
	}

	public void unbindThumbnailTexture(Minecraft mc) {
	}

	public void bindThumbnailTexture(Minecraft mc) {
	}

	public InputStream getResourceAsStream(String string1) {
		return TexturePackBase.class.getResourceAsStream(string1);
	}
}
