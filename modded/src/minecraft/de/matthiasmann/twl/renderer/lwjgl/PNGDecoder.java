package de.matthiasmann.twl.renderer.lwjgl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PNGDecoder extends de.matthiasmann.twl.utils.PNGDecoder {
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$utils$PNGDecoder$Format;

	public PNGDecoder(InputStream input) throws IOException {
		super(input);
	}

	public LWJGLTexture.Format decideTextureFormat(LWJGLTexture.Format fmt) {
		if(fmt == LWJGLTexture.Format.COLOR) {
			fmt = this.autoColorFormat();
		}

		de.matthiasmann.twl.utils.PNGDecoder.Format pngFormat = super.decideTextureFormat(fmt.getPngFormat());
		if(fmt.pngFormat == pngFormat) {
			return fmt;
		} else {
			switch($SWITCH_TABLE$de$matthiasmann$twl$utils$PNGDecoder$Format()[pngFormat.ordinal()]) {
			case 1:
				return LWJGLTexture.Format.ALPHA;
			case 2:
				return LWJGLTexture.Format.LUMINANCE;
			case 3:
				return LWJGLTexture.Format.LUMINANCE_ALPHA;
			case 4:
				return LWJGLTexture.Format.RGB;
			case 5:
				return LWJGLTexture.Format.RGBA;
			case 6:
				return LWJGLTexture.Format.BGRA;
			case 7:
				return LWJGLTexture.Format.ABGR;
			default:
				throw new UnsupportedOperationException("PNGFormat not handled: " + pngFormat);
			}
		}
	}

	private LWJGLTexture.Format autoColorFormat() {
		return this.hasAlpha() ? (this.isRGB() ? LWJGLTexture.Format.ABGR : LWJGLTexture.Format.LUMINANCE_ALPHA) : (this.isRGB() ? LWJGLTexture.Format.ABGR : LWJGLTexture.Format.LUMINANCE);
	}

	public void decode(ByteBuffer buffer, int stride, LWJGLTexture.Format fmt) throws IOException {
		super.decode(buffer, stride, fmt.getPngFormat());
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$utils$PNGDecoder$Format() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$utils$PNGDecoder$Format;
		if($SWITCH_TABLE$de$matthiasmann$twl$utils$PNGDecoder$Format != null) {
			return i10000;
		} else {
			int[] i0 = new int[de.matthiasmann.twl.utils.PNGDecoder.Format.values().length];

			try {
				i0[de.matthiasmann.twl.utils.PNGDecoder.Format.ABGR.ordinal()] = 7;
			} catch (NoSuchFieldError noSuchFieldError7) {
			}

			try {
				i0[de.matthiasmann.twl.utils.PNGDecoder.Format.ALPHA.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError6) {
			}

			try {
				i0[de.matthiasmann.twl.utils.PNGDecoder.Format.BGRA.ordinal()] = 6;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[de.matthiasmann.twl.utils.PNGDecoder.Format.LUMINANCE.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[de.matthiasmann.twl.utils.PNGDecoder.Format.LUMINANCE_ALPHA.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[de.matthiasmann.twl.utils.PNGDecoder.Format.RGB.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[de.matthiasmann.twl.utils.PNGDecoder.Format.RGBA.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$utils$PNGDecoder$Format = i0;
			return i0;
		}
	}
}
