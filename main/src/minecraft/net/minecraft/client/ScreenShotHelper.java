package net.minecraft.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ScreenShotHelper {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static ByteBuffer buffer;
	private static byte[] pixelData;
	private static int[] imageData;

	public static String saveScreenshot(File file0, int i1, int i2) {
		return func_35879_a(file0, (String)null, i1, i2);
	}

	public static String func_35879_a(File file0, String string1, int i2, int i3) {
		try {
			File file4 = new File(file0, "screenshots");
			file4.mkdir();
			if(buffer == null || buffer.capacity() < i2 * i3) {
				buffer = BufferUtils.createByteBuffer(i2 * i3 * 3);
			}

			if(imageData == null || imageData.length < i2 * i3 * 3) {
				pixelData = new byte[i2 * i3 * 3];
				imageData = new int[i2 * i3];
			}

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			buffer.clear();
			GL11.glReadPixels(0, 0, i2, i3, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
			buffer.clear();
			String string5 = "" + dateFormat.format(new Date());
			File file6;
			int i7;
			if(string1 == null) {
				for(i7 = 1; (file6 = new File(file4, string5 + (i7 == 1 ? "" : "_" + i7) + ".png")).exists(); ++i7) {
				}
			} else {
				file6 = new File(file4, string1);
			}

			buffer.get(pixelData);

			for(i7 = 0; i7 < i2; ++i7) {
				for(int i8 = 0; i8 < i3; ++i8) {
					int i9 = i7 + (i3 - i8 - 1) * i2;
					int i10 = pixelData[i9 * 3 + 0] & 255;
					int i11 = pixelData[i9 * 3 + 1] & 255;
					int i12 = pixelData[i9 * 3 + 2] & 255;
					int i13 = 0xFF000000 | i10 << 16 | i11 << 8 | i12;
					imageData[i7 + i8 * i2] = i13;
				}
			}

			BufferedImage bufferedImage15 = new BufferedImage(i2, i3, 1);
			bufferedImage15.setRGB(0, 0, i2, i3, imageData, 0, i2);
			ImageIO.write(bufferedImage15, "png", file6);
			return "Saved screenshot as " + file6.getName();
		} catch (Exception exception14) {
			exception14.printStackTrace();
			return "Failed to save: " + exception14;
		}
	}
}
