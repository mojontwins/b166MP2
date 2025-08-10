package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.DynamicImage;
import de.matthiasmann.twl.renderer.Image;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class LWJGLDynamicImage extends TextureAreaBase implements DynamicImage {
	private final LWJGLRenderer renderer;
	private final int target;
	private final Color tintColor;
	private int id;

	public LWJGLDynamicImage(LWJGLRenderer renderer, int target, int id, int width, int height, Color tintColor) {
		super(0, 0, width, height, target == 3553 ? (float)width : 1.0F, target == 3553 ? (float)height : 1.0F);
		this.renderer = renderer;
		this.tintColor = tintColor;
		this.target = target;
		this.id = id;
	}

	public void destroy() {
		if(this.id != 0) {
			this.renderer.glDeleteTexture(this.id);
			this.renderer.dynamicImages.remove(this);
		}

	}

	public void update(ByteBuffer data, DynamicImage.Format format) {
		this.update(0, 0, this.width, this.height, data, format);
	}

	public void update(int xoffset, int yoffset, int width, int height, ByteBuffer data, DynamicImage.Format format) {
		if(xoffset >= 0 && yoffset >= 0 && this.getWidth() > 0 && this.getHeight() > 0) {
			if(xoffset < this.getWidth() && yoffset < this.getHeight()) {
				if(width <= this.getWidth() - xoffset && height <= this.getHeight() - yoffset) {
					if(data == null) {
						throw new NullPointerException("data");
					} else if(format == null) {
						throw new NullPointerException("format");
					} else if(data.remaining() < width * height * 4) {
						throw new IllegalArgumentException("Not enough data remaining in the buffer");
					} else {
						int glFormat = format == DynamicImage.Format.RGBA ? 6408 : 32993;
						this.bind();
						GL11.glTexSubImage2D(this.target, 0, xoffset, yoffset, width, height, glFormat, GL11.GL_UNSIGNED_BYTE, data);
					}
				} else {
					throw new IllegalArgumentException("Rectangle outside of texture");
				}
			} else {
				throw new IllegalArgumentException("Offset outside of texture");
			}
		} else {
			throw new IllegalArgumentException("Negative offsets or size <= 0");
		}
	}

	public Image createTintedVersion(Color color) {
		if(color == null) {
			throw new NullPointerException("color");
		} else {
			Color newTintColor = this.tintColor.multiply(color);
			return newTintColor.equals(this.tintColor) ? this : new LWJGLDynamicImage(this.renderer, this.target, this.id, this.getWidth(), this.getHeight(), newTintColor);
		}
	}

	public void draw(AnimationState as, int x, int y) {
		this.draw(as, x, y, this.width, this.height);
	}

	public void draw(AnimationState as, int x, int y, int width, int height) {
		this.bind();
		this.renderer.tintStack.setColor(this.tintColor);
		if(this.target != 3553) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(this.target);
		}

		GL11.glBegin(GL11.GL_QUADS);
		this.drawQuad(x, y, width, height);
		GL11.glEnd();
		if(this.target != 3553) {
			GL11.glDisable(this.target);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

	}

	private void bind() {
		if(this.id == 0) {
			throw new IllegalStateException("destroyed");
		} else {
			GL11.glBindTexture(this.target, this.id);
		}
	}
}
