package net.minecraft.client.skins;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class TexturePackFolder extends TexturePackBase {
	private int field_48191_e = -1;
	private BufferedImage field_48189_f;
	private File field_48190_g;

	public TexturePackFolder(File file1) {
		this.texturePackFileName = file1.getName();
		this.field_48190_g = file1;
	}

	private String func_48188_b(String string1) {
		if(string1 != null && string1.length() > 34) {
			string1 = string1.substring(0, 34);
		}

		return string1;
	}

	public void func_6485_a(Minecraft mc) throws IOException {
		InputStream inputStream2 = null;

		try {
			try {
				inputStream2 = this.getResourceAsStream("pack.txt");
				BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(inputStream2));
				this.firstDescriptionLine = this.func_48188_b(bufferedReader3.readLine());
				this.secondDescriptionLine = this.func_48188_b(bufferedReader3.readLine());
				bufferedReader3.close();
				inputStream2.close();
			} catch (Exception exception15) {
			}

			try {
				inputStream2 = this.getResourceAsStream("pack.png");
				this.field_48189_f = ImageIO.read(inputStream2);
				inputStream2.close();
			} catch (Exception exception14) {
			}
		} catch (Exception exception16) {
			exception16.printStackTrace();
		} finally {
			try {
				inputStream2.close();
			} catch (Exception exception13) {
			}

		}

	}

	public void unbindThumbnailTexture(Minecraft mc) {
		if(this.field_48189_f != null) {
			mc.renderEngine.deleteTexture(this.field_48191_e);
		}

		this.closeTexturePackFile();
	}

	public void bindThumbnailTexture(Minecraft mc) {
		if(this.field_48189_f != null && this.field_48191_e < 0) {
			this.field_48191_e = mc.renderEngine.allocateAndSetupTexture(this.field_48189_f);
		}

		if(this.field_48189_f != null) {
			mc.renderEngine.bindTexture(this.field_48191_e);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/unknown_pack.png"));
		}

	}

	public void func_6482_a() {
	}

	public void closeTexturePackFile() {
	}

	public InputStream getResourceAsStream(String string1) {
		try {
			File file2 = new File(this.field_48190_g, string1.substring(1));
			if(file2.exists()) {
				return new BufferedInputStream(new FileInputStream(file2));
			}
		} catch (Exception exception3) {
		}

		return TexturePackBase.class.getResourceAsStream(string1);
	}
}
