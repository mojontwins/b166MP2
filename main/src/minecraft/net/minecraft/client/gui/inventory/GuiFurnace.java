package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.Translator;
import net.minecraft.world.inventory.ContainerFurnace;
import net.minecraft.world.inventory.InventoryPlayer;
import net.minecraft.world.level.tile.entity.TileEntityFurnace;

public class GuiFurnace extends GuiContainer {
	private TileEntityFurnace furnaceInventory;

	public GuiFurnace(InventoryPlayer inventoryPlayer1, TileEntityFurnace tileEntityFurnace2) {
		super(new ContainerFurnace(inventoryPlayer1, tileEntityFurnace2));
		this.furnaceInventory = tileEntityFurnace2;
	}

	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString(Translator.translateToLocal("container.furnace"), 60, 6, 4210752);
		this.fontRenderer.drawString(Translator.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float f1, int i2, int i3) {
		int i4 = this.mc.renderEngine.getTexture("/gui/furnace.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i4);
		int i5 = (this.width - this.xSize) / 2;
		int i6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i5, i6, 0, 0, this.xSize, this.ySize);
		int i7;
		if(this.furnaceInventory.isBurning()) {
			i7 = this.furnaceInventory.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(i5 + 56, i6 + 36 + 12 - i7, 176, 12 - i7, 14, i7 + 2);
		}

		i7 = this.furnaceInventory.getCookProgressScaled(24);
		this.drawTexturedModalRect(i5 + 79, i6 + 34, 176, 14, i7 + 1, 16);
	}
}
