package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.SupportsDrawRepeat;

import org.lwjgl.opengl.GL11;

public class TextureArea extends TextureAreaBase implements Image, SupportsDrawRepeat {
	private static final int REPEAT_CACHE_SIZE = 10;
	private final LWJGLTexture texture;
	private final Color tintColor;
	private final boolean tile;
	private int repeatCacheID = -1;

	public TextureArea(LWJGLTexture texture, int x, int y, int width, int height, Color tintColor, boolean tile) {
		super(x, y, width, height, (float)texture.getTexWidth(), (float)texture.getTexHeight());
		this.texture = texture;
		this.tintColor = tintColor == null ? Color.WHITE : tintColor;
		this.tile = tile;
	}

	TextureArea(TextureArea src, Color tintColor) {
		super(src);
		this.texture = src.texture;
		this.tintColor = tintColor;
		this.tile = src.tile;
	}

	public void draw(AnimationState as, int x, int y) {
		this.draw(as, x, y, this.width, this.height);
	}

	public void draw(AnimationState as, int x, int y, int w, int h) {
		if(this.texture.bind(this.tintColor)) {
			if(this.tile) {
				this.drawTiled(x, y, w, h);
			} else {
				GL11.glBegin(GL11.GL_QUADS);
				this.drawQuad(x, y, w, h);
				GL11.glEnd();
			}
		}

	}

	public void draw(AnimationState as, int x, int y, int width, int height, int repeatCountX, int repeatCountY) {
		if(this.texture.bind(this.tintColor)) {
			if(repeatCountX * this.width != width || repeatCountY * this.height != height) {
				this.drawRepeatSlow(x, y, width, height, repeatCountX, repeatCountY);
				return;
			}

			if(repeatCountX < 10 || repeatCountY < 10) {
				this.drawRepeat(x, y, repeatCountX, repeatCountY);
				return;
			}

			this.drawRepeatCached(x, y, repeatCountX, repeatCountY);
		}

	}

	private void drawRepeatSlow(int x, int y, int width, int height, int repeatCountX, int repeatCountY) {
		GL11.glBegin(GL11.GL_QUADS);

		while(repeatCountY > 0) {
			int rowHeight = height / repeatCountY;
			int cx = 0;

			int nx;
			for(int xi = 0; xi < repeatCountX; cx = nx) {
				++xi;
				nx = xi * width / repeatCountX;
				this.drawQuad(x + cx, y, nx - cx, rowHeight);
			}

			y += rowHeight;
			height -= rowHeight;
			--repeatCountY;
		}

		GL11.glEnd();
	}

	private void drawRepeat(int x, int y, int repeatCountX, int repeatCountY) {
		short w = this.width;
		short h = this.height;
		GL11.glBegin(GL11.GL_QUADS);

		while(repeatCountY-- > 0) {
			int curX = x;

			for(int cntX = repeatCountX; cntX-- > 0; curX += w) {
				this.drawQuad(curX, y, w, h);
			}

			y += h;
		}

		GL11.glEnd();
	}

	private void drawRepeatCached(int x, int y, int repeatCountX, int repeatCountY) {
		if(this.repeatCacheID < 0) {
			this.createRepeatCache();
		}

		int cacheBlocksX = repeatCountX / 10;
		int repeatsByCacheX = cacheBlocksX * 10;
		if(repeatCountX > repeatsByCacheX) {
			this.drawRepeat(x + this.width * repeatsByCacheX, y, repeatCountX - repeatsByCacheX, repeatCountY);
		}

		do {
			GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y, 0.0F);
			GL11.glCallList(this.repeatCacheID);

			for(int i = 1; i < cacheBlocksX; ++i) {
				GL11.glTranslatef((float)(this.width * 10), 0.0F, 0.0F);
				GL11.glCallList(this.repeatCacheID);
			}

			GL11.glPopMatrix();
			repeatCountY -= 10;
			y += this.height * 10;
		} while(repeatCountY >= 10);

		if(repeatCountY > 0) {
			this.drawRepeat(x, y, repeatsByCacheX, repeatCountY);
		}

	}

	private void drawTiled(int x, int y, int width, int height) {
		int repeatCountX = width / this.width;
		int repeatCountY = height / this.height;
		if(repeatCountX >= 10 && repeatCountY >= 10) {
			this.drawRepeatCached(x, y, repeatCountX, repeatCountY);
		} else {
			this.drawRepeat(x, y, repeatCountX, repeatCountY);
		}

		int drawnX = repeatCountX * this.width;
		int drawnY = repeatCountY * this.height;
		int restWidth = width - drawnX;
		int restHeight = height - drawnY;
		if(restWidth > 0 || restHeight > 0) {
			GL11.glBegin(GL11.GL_QUADS);
			if(restWidth > 0 && repeatCountY > 0) {
				this.drawClipped(x + drawnX, y, restWidth, this.height, 1, repeatCountY);
			}

			if(restHeight > 0) {
				if(repeatCountX > 0) {
					this.drawClipped(x, y + drawnY, this.width, restHeight, repeatCountX, 1);
				}

				if(restWidth > 0) {
					this.drawClipped(x + drawnX, y + drawnY, restWidth, restHeight, 1, 1);
				}
			}

			GL11.glEnd();
		}

	}

	private void drawClipped(int x, int y, int width, int height, int repeatCountX, int repeatCountY) {
		float ctx0 = this.tx0;
		float cty0 = this.ty0;
		float ctx1 = this.tx1;
		float cty1 = this.ty1;
		if(this.width > 1) {
			ctx1 = ctx0 + (float)width / (float)this.texture.getTexWidth();
		}

		if(this.height > 1) {
			cty1 = cty0 + (float)height / (float)this.texture.getTexHeight();
		}

		while(repeatCountY-- > 0) {
			int y1 = y + height;
			int x0 = x;

			int x1;
			for(int cx = repeatCountX; cx-- > 0; x0 = x1) {
				x1 = x0 + width;
				GL11.glTexCoord2f(ctx0, cty0);
				GL11.glVertex2i(x0, y);
				GL11.glTexCoord2f(ctx0, cty1);
				GL11.glVertex2i(x0, y1);
				GL11.glTexCoord2f(ctx1, cty1);
				GL11.glVertex2i(x1, y1);
				GL11.glTexCoord2f(ctx1, cty0);
				GL11.glVertex2i(x1, y);
			}

			y = y1;
		}

	}

	private void createRepeatCache() {
		this.repeatCacheID = GL11.glGenLists(1);
		this.texture.renderer.textureAreas.add(this);
		GL11.glNewList(this.repeatCacheID, GL11.GL_COMPILE);
		this.drawRepeat(0, 0, 10, 10);
		GL11.glEndList();
	}

	void destroyRepeatCache() {
		GL11.glDeleteLists(this.repeatCacheID, 1);
		this.repeatCacheID = -1;
	}

	int getX() {
		return (int)(this.tx0 * (float)this.texture.getTexWidth());
	}

	int getY() {
		return (int)(this.ty0 * (float)this.texture.getTexHeight());
	}

	public Image createTintedVersion(Color color) {
		if(color == null) {
			throw new NullPointerException("color");
		} else {
			Color newTintColor = this.tintColor.multiply(color);
			return newTintColor.equals(this.tintColor) ? this : new TextureArea(this, newTintColor);
		}
	}
}
