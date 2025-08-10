package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.renderer.Resource;
import de.matthiasmann.twl.renderer.Texture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;
import org.lwjgl.opengl.GL12;

public class LWJGLTexture implements Texture, Resource {
	final LWJGLRenderer renderer;
	private int id;
	private final int width;
	private final int height;
	private final int texWidth;
	private final int texHeight;
	private ByteBuffer texData;
	private LWJGLTexture.Format texDataFmt;
	private ArrayList cursors;

	public LWJGLTexture(LWJGLRenderer renderer, int width, int height, ByteBuffer buf, LWJGLTexture.Format fmt, LWJGLTexture.Filter filter) {
		this.renderer = renderer;
		if(width > 0 && height > 0) {
			this.id = renderer.glGenTexture();
			if(this.id == 0) {
				throw new OpenGLException("failed to allocate texture ID");
			} else {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
				GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				if(GLContext.getCapabilities().OpenGL12) {
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
				} else {
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				}

				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter.glValue);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter.glValue);
				this.texWidth = roundUpPOT(width);
				this.texHeight = roundUpPOT(height);
				if(this.texWidth == width && this.texHeight == height) {
					GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, fmt.glInternalFormat, this.texWidth, this.texHeight, 0, fmt.glFormat, GL11.GL_UNSIGNED_BYTE, buf);
				} else {
					GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, fmt.glInternalFormat, this.texWidth, this.texHeight, 0, fmt.glFormat, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
					Util.checkGLError();
					GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, fmt.glFormat, GL11.GL_UNSIGNED_BYTE, buf);
				}

				Util.checkGLError();
				this.width = width;
				this.height = height;
				this.texData = buf;
				this.texDataFmt = fmt;
			}
		} else {
			throw new IllegalArgumentException("size <= 0");
		}
	}

	public void destroy() {
		if(this.id != 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			this.renderer.glDeleteTexture(this.id);
			this.id = 0;
		}

		if(this.cursors != null) {
			Iterator iterator2 = this.cursors.iterator();

			while(iterator2.hasNext()) {
				LWJGLCursor cursor = (LWJGLCursor)iterator2.next();
				cursor.destroy();
			}

			this.cursors.clear();
		}

	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getTexWidth() {
		return this.texWidth;
	}

	public int getTexHeight() {
		return this.texHeight;
	}

	boolean bind(Color color) {
		if(this.id != 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
			this.renderer.tintStack.setColor(color);
			return true;
		} else {
			return false;
		}
	}

	boolean bind() {
		if(this.id != 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
			return true;
		} else {
			return false;
		}
	}

	public Image getImage(int x, int y, int width, int height, Color tintColor, boolean tiled) {
		if(x >= 0 && x < this.getWidth()) {
			if(y >= 0 && y < this.getHeight()) {
				if(x + Math.abs(width) > this.getWidth()) {
					throw new IllegalArgumentException("width");
				} else if(y + Math.abs(height) > this.getHeight()) {
					throw new IllegalArgumentException("height");
				} else if(!tiled || width > 0 && height > 0) {
					return new TextureArea(this, x, y, width, height, tintColor, tiled);
				} else {
					throw new IllegalArgumentException("Tiled rendering requires positive width & height");
				}
			} else {
				throw new IllegalArgumentException("y");
			}
		} else {
			throw new IllegalArgumentException("x");
		}
	}

	public MouseCursor createCursor(int x, int y, int width, int height, int hotSpotX, int hotSpotY, Image imageRef) {
		if(!this.renderer.isUseSWMouseCursors() && imageRef == null) {
			if(this.texData != null) {
				LWJGLCursor cursor = new LWJGLCursor(this.texData, this.texDataFmt, this.texDataFmt.getPixelSize() * this.width, x, y, width, height, hotSpotX, hotSpotY);
				if(this.cursors == null) {
					this.cursors = new ArrayList();
				}

				this.cursors.add(cursor);
				return cursor;
			} else {
				return null;
			}
		} else {
			return new SWCursor(this, x, y, width, height, hotSpotX, hotSpotY, imageRef);
		}
	}

	public void themeLoadingDone() {
	}

	static int roundUpPOT(int value) {
		return 1 << 32 - Integer.numberOfLeadingZeros(value - 1);
	}

	public static enum Filter {
		NEAREST(9728),
		LINEAR(9729);

		final int glValue;

		private Filter(int value) {
			this.glValue = value;
		}
	}

	public static enum Format {
		ALPHA(6406, 32828, de.matthiasmann.twl.utils.PNGDecoder.Format.ALPHA),
		LUMINANCE(6409, 32832, de.matthiasmann.twl.utils.PNGDecoder.Format.LUMINANCE),
		LUMINANCE_ALPHA(6410, 32837, de.matthiasmann.twl.utils.PNGDecoder.Format.LUMINANCE_ALPHA),
		RGB(6407, 32849, de.matthiasmann.twl.utils.PNGDecoder.Format.RGB),
		RGB_SMALL(6407, 32855, de.matthiasmann.twl.utils.PNGDecoder.Format.RGB),
		RGBA(6408, 32856, de.matthiasmann.twl.utils.PNGDecoder.Format.RGBA),
		BGRA(32993, 32856, de.matthiasmann.twl.utils.PNGDecoder.Format.BGRA),
		ABGR(32768, 32856, de.matthiasmann.twl.utils.PNGDecoder.Format.ABGR),
		COLOR(-1, -1, (de.matthiasmann.twl.utils.PNGDecoder.Format)null);

		final int glFormat;
		final int glInternalFormat;
		final de.matthiasmann.twl.utils.PNGDecoder.Format pngFormat;

		private Format(int fmt, int ifmt, de.matthiasmann.twl.utils.PNGDecoder.Format pf) {
			this.glFormat = fmt;
			this.glInternalFormat = ifmt;
			this.pngFormat = pf;
		}

		public int getPixelSize() {
			return this.pngFormat.getNumComponents();
		}

		public de.matthiasmann.twl.utils.PNGDecoder.Format getPngFormat() {
			return this.pngFormat;
		}
	}
}
