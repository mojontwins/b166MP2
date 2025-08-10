package de.matthiasmann.twl.renderer;

import java.nio.ByteBuffer;

public interface DynamicImage extends Image, Resource {
	void update(ByteBuffer byteBuffer1, DynamicImage.Format dynamicImage$Format2);

	void update(int i1, int i2, int i3, int i4, ByteBuffer byteBuffer5, DynamicImage.Format dynamicImage$Format6);

	public static enum Format {
		RGBA,
		BGRA;
	}
}
