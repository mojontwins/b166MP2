package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.entity.TileEntity;

public abstract class TileEntitySpecialRenderer {
	protected TileEntityRenderer tileEntityRenderer;

	public abstract void renderTileEntityAt(TileEntity tileEntity1, double d2, double d4, double d6, float f8);

	protected void bindTextureByName(String string1) {
		RenderEngine renderEngine2 = this.tileEntityRenderer.renderEngine;
		if(renderEngine2 != null) {
			renderEngine2.bindTexture(renderEngine2.getTexture(string1));
		}

	}

	public void setTileEntityRenderer(TileEntityRenderer tileEntityRenderer1) {
		this.tileEntityRenderer = tileEntityRenderer1;
	}

	public void cacheSpecialRenderInfo(World world1) {
	}

	public FontRenderer getFontRenderer() {
		return this.tileEntityRenderer.getFontRenderer();
	}
}
