package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.Translator;
import net.minecraft.world.inventory.ContainerWorkbench;
import net.minecraft.world.inventory.InventoryPlayer;
import net.minecraft.world.level.World;

public class GuiCrafting extends GuiContainer {
	public GuiCrafting(InventoryPlayer inventoryPlayer1, World world2, int i3, int i4, int i5) {
		super(new ContainerWorkbench(inventoryPlayer1, world2, i3, i4, i5));
	}

	public void onGuiClosed() {
		super.onGuiClosed();
	}

	protected void drawGuiContainerForegroundLayer() { 
		this.fontRenderer.drawString(Translator.translateToLocal("container.crafting"), 28, 6, 4210752);
		this.fontRenderer.drawString(Translator.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float f1, int i2, int i3) {
		int i4 = this.mc.renderEngine.getTexture("/gui/crafting.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i4);
		int i5 = (this.width - this.xSize) / 2;
		int i6 = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i5, i6, 0, 0, this.xSize, this.ySize);
	}
}
