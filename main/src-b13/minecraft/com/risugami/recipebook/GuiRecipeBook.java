package com.risugami.recipebook;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiRecipeBook extends GuiContainer {
	private InventoryRecipeBook recipes;
	public static final int BORDER = 4;
	public static final int ROWS = 3;
	public static final int COLUMNS = 2;
	public static final int ENTRIES = ROWS * COLUMNS;
	public static final int GRIDX = 5;
	public static final int GRIDY = 6;
	public static final int CRAFTX = 99;
	public static final int CRAFTY = 24;
	public static final int IMGWIDTH = 176;
	public static final int IMGHEIGHT = 166;
	public static final int IMGMIDX = 29;
	public static final int IMGMIDY = 15;
	public static final int MIDWIDTH = 117;
	public static final int MIDHEIGHT = 55;
	private final InventoryRecipeBook inv;

	public GuiRecipeBook(InventoryRecipeBook inventory) {
		super(new CraftingInventoryRecipeBookCB(inventory));
		this.inv = inventory;
		this.xSize = 117 * COLUMNS + 8;
		this.ySize = 55 * ROWS + 8;
		this.recipes = this.inv;
	}

	protected void drawGuiContainerForegroundLayer() {
		String label = this.inv.getInvName();
		this.fontRenderer.drawString(label, this.xSize - this.fontRenderer.getStringWidth(label) - 3, 6, 4210752);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void drawGuiContainerBackgroundLayer(float paramFloat, int paramInt1, int paramInt2) {
		int image = this.mc.renderEngine.getTexture("/gui/crafting.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(image);
		int x = this.width - this.xSize >> 1;
		int y = this.height - this.ySize >> 1;
		int wheelDelta = Mouse.getDWheel();
		if(wheelDelta < 0) {
			this.recipes.decIndex();
		} else if(wheelDelta > 0) {
			this.recipes.incIndex();
		}

		if(this.inv.filter != null) {
			this.drawTexturedModalRect(x + this.xSize + 17, y, 172, 0, 4, 4);
			this.drawTexturedModalRect(x + this.xSize + 17, y + 21, 172, 162, 4, 4);
			this.drawTexturedModalRect(x + this.xSize + 17, y + 4, 172, 4, 4, 17);
			this.drawTexturedModalRect(x + this.xSize - 4, y, 4, 0, 22, 4);
			this.drawTexturedModalRect(x + this.xSize, y + 21, 4, 162, 18, 4);
			this.drawTexturedModalRect(x + this.xSize - 1, y + 3, 29, 16, 18, 18);
		}

		int w = (this.xSize - 8) / COLUMNS;

		int h;
		for(h = 0; h < COLUMNS; ++h) {
			this.drawTexturedModalRect(x + 4 + h * w, y, 4, 0, w, 4);
			this.drawTexturedModalRect(x + 4 + h * w, y + this.ySize - 4, 4, 162, w, 4);
		}

		h = (this.ySize - 8) / ROWS;

		int i;
		for(i = 0; i < ROWS; ++i) {
			this.drawTexturedModalRect(x, y + 4 + i * h, 0, 4, 4, h);
			this.drawTexturedModalRect(x + this.xSize - 4, y + 4 + i * h, 172, 4, 4, h);
		}

		this.drawTexturedModalRect(x, y, 0, 0, 4, 4);
		this.drawTexturedModalRect(x + 4 + 117 * COLUMNS, y, 172, 0, 4, 4);
		this.drawTexturedModalRect(x, y + this.ySize - 4, 0, 162, 4, 4);
		this.drawTexturedModalRect(x + this.xSize - 4, y + this.ySize - 4, 172, 162, 4, 4);

		for(i = 0; i < COLUMNS; ++i) {
			for(int j = 0; j < ROWS; ++j) {
				this.drawTexturedModalRect(x + 4 + i * 117, y + 4 + j * 55, 29, 15, 117, 55);
			}
		}

	}

	public boolean doesGuiPauseGame() {
		return true;
	}

	protected void keyTyped(char arg0, int arg1) {
		super.keyTyped(arg0, arg1);
		switch(arg1) {
		case 203:
			this.recipes.decIndex();
		case 204:
		default:
			break;
		case 205:
			this.recipes.incIndex();
		}

	}
}
