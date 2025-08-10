package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.renderer.MouseCursor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;

class LWJGLCursor implements MouseCursor {
	Cursor cursor;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$renderer$lwjgl$LWJGLTexture$Format;

	LWJGLCursor(ByteBuffer src, LWJGLTexture.Format srcFmt, int srcStride, int x, int y, int width, int height, int hotSpotX, int hotSpotY) {
		width = Math.min(Cursor.getMaxCursorSize(), width);
		height = Math.min(Cursor.getMaxCursorSize(), height);
		int dstSize = Math.max(Cursor.getMinCursorSize(), Math.max(width, height));
		IntBuffer buf = BufferUtils.createIntBuffer(dstSize * dstSize);
		int ex = height;

		label53:
		for(int dstPos = 0; ex-- > 0; dstPos += dstSize) {
			int offset = srcStride * (y + ex) + x * srcFmt.getPixelSize();
			buf.position(dstPos);
			int col;
			int r;
			int g;
			int b;
			int a;
			switch($SWITCH_TABLE$de$matthiasmann$twl$renderer$lwjgl$LWJGLTexture$Format()[srcFmt.ordinal()]) {
			case 4:
				col = 0;

				while(true) {
					if(col >= width) {
						continue label53;
					}

					r = src.get(offset + col * 3 + 0) & 255;
					g = src.get(offset + col * 3 + 1) & 255;
					b = src.get(offset + col * 3 + 2) & 255;
					buf.put(makeColor(r, g, b, 255));
					++col;
				}
			case 5:
			case 7:
			default:
				throw new IllegalStateException("Unsupported color format");
			case 6:
				col = 0;

				while(true) {
					if(col >= width) {
						continue label53;
					}

					r = src.get(offset + col * 4 + 0) & 255;
					g = src.get(offset + col * 4 + 1) & 255;
					b = src.get(offset + col * 4 + 2) & 255;
					a = src.get(offset + col * 4 + 3) & 255;
					buf.put(makeColor(r, g, b, a));
					++col;
				}
			case 8:
				for(col = 0; col < width; ++col) {
					r = src.get(offset + col * 4 + 3) & 255;
					g = src.get(offset + col * 4 + 2) & 255;
					b = src.get(offset + col * 4 + 1) & 255;
					a = src.get(offset + col * 4 + 0) & 255;
					buf.put(makeColor(r, g, b, a));
				}
			}
		}

		buf.clear();

		try {
			this.cursor = new Cursor(dstSize, dstSize, hotSpotX, Math.min(dstSize - 1, height - hotSpotY - 1), 1, buf, (IntBuffer)null);
		} catch (LWJGLException lWJGLException20) {
			lWJGLException20.printStackTrace();
		}

	}

	private static int makeColor(int r, int g, int b, int a) {
		a = a > 222 ? 255 : 0;
		return a << 24 | r << 16 | g << 8 | b;
	}

	void destroy() {
		if(this.cursor != null) {
			this.cursor.destroy();
			this.cursor = null;
		}

	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$renderer$lwjgl$LWJGLTexture$Format() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$renderer$lwjgl$LWJGLTexture$Format;
		if($SWITCH_TABLE$de$matthiasmann$twl$renderer$lwjgl$LWJGLTexture$Format != null) {
			return i10000;
		} else {
			int[] i0 = new int[LWJGLTexture.Format.values().length];

			try {
				i0[LWJGLTexture.Format.ABGR.ordinal()] = 8;
			} catch (NoSuchFieldError noSuchFieldError9) {
			}

			try {
				i0[LWJGLTexture.Format.ALPHA.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError8) {
			}

			try {
				i0[LWJGLTexture.Format.BGRA.ordinal()] = 7;
			} catch (NoSuchFieldError noSuchFieldError7) {
			}

			try {
				i0[LWJGLTexture.Format.COLOR.ordinal()] = 9;
			} catch (NoSuchFieldError noSuchFieldError6) {
			}

			try {
				i0[LWJGLTexture.Format.LUMINANCE.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[LWJGLTexture.Format.LUMINANCE_ALPHA.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[LWJGLTexture.Format.RGB.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[LWJGLTexture.Format.RGBA.ordinal()] = 6;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[LWJGLTexture.Format.RGB_SMALL.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$renderer$lwjgl$LWJGLTexture$Format = i0;
			return i0;
		}
	}
}
