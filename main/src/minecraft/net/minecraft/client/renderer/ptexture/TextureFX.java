package net.minecraft.client.renderer.ptexture;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderEngine;

public class TextureFX {
	public byte[] imageData = new byte[1024];
	public int iconIndex;
	public int textureId = 0;
	public int tileSize = 1;
	public int tileImage = 0;

	public TextureFX(int i1) {
		this.iconIndex = i1;
	}

	public void onTick() {
	}

	public void bindImage(RenderEngine renderEngine1) {
		if(this.tileImage == 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderEngine1.getTexture("/terrain.png"));
		} else if(this.tileImage == 1) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderEngine1.getTexture("/gui/items.png"));
		}

	}
}
