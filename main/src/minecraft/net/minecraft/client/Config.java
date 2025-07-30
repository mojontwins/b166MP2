package net.minecraft.client;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Config {
	
	private static int iconWidthTerrain = 16;
	private static int iconWidthItems = 16;
	private static long textureUpdateTime = 0L;

	public static int getIconWidthTerrain() {
		return iconWidthTerrain;
	}

	public static int getIconWidthItems() {
		return iconWidthItems;
	}

	public static void setIconWidthItems(int iconWidth) {
		iconWidthItems = iconWidth;
	}

	public static void setIconWidthTerrain(int iconWidth) {
		iconWidthTerrain = iconWidth;
	}


	public static boolean between(int val, int min, int max) {
		return val >= min && val <= max;
	}
	public static int parseInt(String str, int defVal) {
		try {
			return str == null ? defVal : Integer.parseInt(str);
		} catch (NumberFormatException numberFormatException3) {
			return defVal;
		}
	}

	public static float parseFloat(String str, float defVal) {
		try {
			return str == null ? defVal : Float.parseFloat(str);
		} catch (NumberFormatException numberFormatException3) {
			return defVal;
		}
	}
	
	public static String[] tokenize(String str, String delim) {
		StringTokenizer tok = new StringTokenizer(str, delim);
		ArrayList<String> list = new ArrayList<String>();

		while(tok.hasMoreTokens()) {
			String strs = tok.nextToken();
			list.add(strs);
		}

		String[] strs1 = (String[])((String[])list.toArray(new String[list.size()]));
		return strs1;
	}

	public static long getTextureUpdateTime() {
		return textureUpdateTime;
	}

	public static void setTextureUpdateTime(long fontRendererUpdateTime) {
		textureUpdateTime = fontRendererUpdateTime;
	}

	public static boolean isCustomColors() {
		return false;
	}

	public static int getUpdatesPerFrame() {
		return 3;
	}

	public static boolean isDynamicUpdates() {
		return true;
	}
	
}
