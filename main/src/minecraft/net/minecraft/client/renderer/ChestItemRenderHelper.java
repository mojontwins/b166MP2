package net.minecraft.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntityChest;

public class ChestItemRenderHelper {
	public static ChestItemRenderHelper instance = new ChestItemRenderHelper();
	private TileEntityChest tileEntityChest = new TileEntityChest();

	public void render(Block block1, int i2, float f3) {
		TileEntityRenderer.instance.renderTileEntityAt(this.tileEntityChest, 0.0D, 0.0D, 0.0D, 0.0F);
	}
}
