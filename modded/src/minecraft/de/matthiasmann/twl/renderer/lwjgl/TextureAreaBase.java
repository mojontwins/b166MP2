package de.matthiasmann.twl.renderer.lwjgl;

import org.lwjgl.opengl.GL11;

public class TextureAreaBase {
	protected final float tx0;
	protected final float ty0;
	protected final float tx1;
	protected final float ty1;
	protected final short width;
	protected final short height;

	TextureAreaBase(int x, int y, int width, int height, float texWidth, float texHeight) {
		this.width = (short)Math.abs(width);
		this.height = (short)Math.abs(height);
		float fx = (float)x;
		float fy = (float)y;
		if(width == 1) {
			fx += 0.5F;
			width = 0;
		}

		if(height == 1) {
			fy += 0.5F;
			height = 0;
		}

		this.tx0 = fx / texWidth;
		this.ty0 = fy / texHeight;
		this.tx1 = this.tx0 + (float)width / texWidth;
		this.ty1 = this.ty0 + (float)height / texHeight;
	}

	TextureAreaBase(TextureAreaBase src) {
		this.tx0 = src.tx0;
		this.ty0 = src.ty0;
		this.tx1 = src.tx1;
		this.ty1 = src.ty1;
		this.width = src.width;
		this.height = src.height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	void drawQuad(int x, int y, int w, int h) {
		GL11.glTexCoord2f(this.tx0, this.ty0);
		GL11.glVertex2i(x, y);
		GL11.glTexCoord2f(this.tx0, this.ty1);
		GL11.glVertex2i(x, y + h);
		GL11.glTexCoord2f(this.tx1, this.ty1);
		GL11.glVertex2i(x + w, y + h);
		GL11.glTexCoord2f(this.tx1, this.ty0);
		GL11.glVertex2i(x + w, y);
	}
}
