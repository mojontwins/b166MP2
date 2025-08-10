package de.matthiasmann.twl.model;

public class ColorSpaceHSL extends AbstractColorSpace {
	public ColorSpaceHSL() {
		super("HSL", new String[]{"Hue", "Saturation", "Lightness"});
	}

	public String getComponentShortName(int component) {
		return "HSL".substring(component, component + 1);
	}

	public float getMaxValue(int component) {
		return component == 0 ? 360.0F : 100.0F;
	}

	public float getDefaultValue(int component) {
		return component == 0 ? 0.0F : 50.0F;
	}

	public float[] fromRGB(int rgb) {
		float r = (float)(rgb >> 16 & 255) / 255.0F;
		float g = (float)(rgb >> 8 & 255) / 255.0F;
		float b = (float)(rgb & 255) / 255.0F;
		float max = Math.max(Math.max(r, g), b);
		float min = Math.min(Math.min(r, g), b);
		float summe = max + min;
		float saturation = max - min;
		if(saturation > 0.0F) {
			saturation /= summe > 1.0F ? 2.0F - summe : summe;
		}

		return new float[]{360.0F * getHue(r, g, b, max, min), 100.0F * saturation, 50.0F * summe};
	}

	public int toRGB(float[] color) {
		float hue = color[0] / 360.0F;
		float saturation = color[1] / 100.0F;
		float lightness = color[2] / 100.0F;
		float r;
		float g;
		float b;
		if(saturation > 0.0F) {
			hue = hue < 1.0F ? hue * 6.0F : 0.0F;
			float q = lightness + saturation * (lightness > 0.5F ? 1.0F - lightness : lightness);
			float p = 2.0F * lightness - q;
			r = normalize(q, p, hue < 4.0F ? hue + 2.0F : hue - 4.0F);
			g = normalize(q, p, hue);
			b = normalize(q, p, hue < 2.0F ? hue + 4.0F : hue - 2.0F);
		} else {
			b = lightness;
			g = lightness;
			r = lightness;
		}

		return toByte(r) << 16 | toByte(g) << 8 | toByte(b);
	}

	static float getHue(float red, float green, float blue, float max, float min) {
		float hue = max - min;
		if(hue > 0.0F) {
			if(max == red) {
				hue = (green - blue) / hue;
				if(hue < 0.0F) {
					hue += 6.0F;
				}
			} else if(max == green) {
				hue = 2.0F + (blue - red) / hue;
			} else {
				hue = 4.0F + (red - green) / hue;
			}

			hue /= 6.0F;
		}

		return hue;
	}

	private static float normalize(float q, float p, float color) {
		return color < 1.0F ? p + (q - p) * color : (color < 3.0F ? q : (color < 4.0F ? p + (q - p) * (4.0F - color) : p));
	}

	private static int toByte(float value) {
		return Math.max(0, Math.min(255, (int)(255.0F * value)));
	}
}
