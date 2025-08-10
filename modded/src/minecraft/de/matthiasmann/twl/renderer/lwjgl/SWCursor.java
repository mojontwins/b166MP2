package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;

import org.lwjgl.opengl.GL11;

class SWCursor extends TextureAreaBase implements MouseCursor {
	private final LWJGLTexture texture;
	private final int hotSpotX;
	private final int hotSpotY;
	private final Image imageRef;

	SWCursor(LWJGLTexture texture, int x, int y, int width, int height, int hotSpotX, int hotSpotY, Image imageRef) {
		super(x, y, width, height, (float)texture.getTexWidth(), (float)texture.getTexHeight());
		this.texture = texture;
		this.hotSpotX = hotSpotX;
		this.hotSpotY = hotSpotY;
		this.imageRef = imageRef;
	}

	void render(int x, int y) {
		if(this.imageRef != null) {
			this.imageRef.draw(this.texture.renderer.swCursorAnimState, x - this.hotSpotX, y - this.hotSpotY);
		} else if(this.texture.bind()) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glBegin(GL11.GL_QUADS);
			this.drawQuad(x - this.hotSpotX, y - this.hotSpotY, this.width, this.height);
			GL11.glEnd();
		}

	}
}
