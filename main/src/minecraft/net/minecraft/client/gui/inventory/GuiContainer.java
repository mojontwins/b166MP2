package net.minecraft.client.gui.inventory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.GameSettingsKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.src.MathHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.InventoryPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class GuiContainer extends GuiScreen {
	protected static RenderItem itemRenderer = new RenderItem();
	protected int xSize = 176;
	protected int ySize = 166;
	public Container inventorySlots;
	protected int guiLeft;
	protected int guiTop;

	private Slot theSlot;
	private Slot clickedSlot = null;
	private boolean isRightMouseClick = false;
	private ItemStack draggedStack = null;
	protected boolean isDraggingStack;
	private Slot lastSlotUnderMouse = null;

	private int initialButton = 0;
	private int draggingButton = 0;
	private int currentStackSize;
	private long lastMouseClickTime = 0L;
	
	protected final Set<Slot> draggedOverSlots = new HashSet<Slot>();
	private int prevButtonState = 0;

	private boolean notMovedToAnotherSlot;

	private ItemStack field_94075_K = null;
	private boolean field_94068_E = false;

	private int lastSlotX = 0;
	private int lastSlotY = 0;
	private Slot returningStackDestSlot = null;
	private long returningStackTime = 0L;
	private ItemStack returningStack = null;

	public GuiContainer(Container container1) {
		this.inventorySlots = container1;
		this.field_94068_E = true;
	}

	public void initGui() {
		super.initGui();
		this.mc.thePlayer.craftingInventory = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		int var4 = this.guiLeft;
		int var5 = this.guiTop;
		this.drawGuiContainerBackgroundLayer(var3, var1, var2);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		super.drawScreen(var1, var2, var3);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var4, (float)var5, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		this.theSlot = null;
		short var6 = 240;
		short var7 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var6 / 1.0F, (float)var7 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int var9;
		for(int var13 = 0; var13 < this.inventorySlots.inventorySlots.size(); ++var13) {
			Slot var15 = (Slot)this.inventorySlots.inventorySlots.get(var13);
			this.drawSlotInventory(var15);
			if(this.isMouseOverSlot(var15, var1, var2)) {
				this.theSlot = var15;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				int var8 = var15.xDisplayPosition;
				var9 = var15.yDisplayPosition;
				this.drawGradientRect(var8, var9, var8 + 16, var9 + 16, -2130706433, -2130706433);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		this.drawGuiContainerForegroundLayer();
		InventoryPlayer var14 = this.mc.thePlayer.inventory;
		ItemStack var16 = this.draggedStack == null ? var14.getItemStack() : this.draggedStack;
		if(var16 != null) {
			byte var17 = 8;
			var9 = this.draggedStack == null ? 8 : 16;
			String var10 = null;
			if(this.draggedStack != null && this.isRightMouseClick) {
				var16 = var16.copy();
				var16.stackSize = MathHelper.ceiling_float_int((float)var16.stackSize / 2.0F);
			} else if(this.isDraggingStack && this.draggedOverSlots.size() > 1) {
				var16 = var16.copy();
				var16.stackSize = this.currentStackSize;
				if(var16.stackSize == 0) {
					var10 = "" + EnumChatFormatting.YELLOW + "0";
				}
			}

			this.drawItemStack(var16, var1 - var4 - var17, var2 - var5 - var9, var10);
		}

		if(this.returningStack != null) {
			float var18 = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
			if(var18 >= 1.0F) {
				var18 = 1.0F;
				this.returningStack = null;
			}

			var9 = this.returningStackDestSlot.xDisplayPosition - this.lastSlotX;
			int var20 = this.returningStackDestSlot.yDisplayPosition - this.lastSlotY;
			int var11 = this.lastSlotX + (int)((float)var9 * var18);
			int var12 = this.lastSlotY + (int)((float)var20 * var18);
			this.drawItemStack(this.returningStack, var11, var12, (String)null);
		}

		GL11.glPopMatrix();
		if(var14.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack()) {
			ItemStack var19 = this.theSlot.getStack();
				this.drawItemStackTooltip(var19, var1, var2);
			}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();	
	}

	private void drawItemStack(ItemStack var1, int var2, int var3, String var4) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRenderer.zLevel = 200.0F;
		itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var1, var2, var3);
		itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var1, var2, var3 - (this.draggedStack == null ? 0 : 8)/*, var4*/);
		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
	}
	
	protected void drawItemStackTooltip(ItemStack var1, int var2, int var3) {
		List<String> var4 = var1.getItemNameandInformation();

		for(int var5 = 0; var5 < var4.size(); ++var5) {
			if(var5 == 0) {
				var4.set(var5, "\u00a7" + Integer.toHexString(var1.getRarity().nameColor) + (String)var4.get(var5));
			} else {
				var4.set(var5, EnumChatFormatting.GRAY + (String)var4.get(var5));
			}
		}

		this.func_102021_a(var4, var2, var3);
	}

	protected void drawCreativeTabHoveringText(String var1, int var2, int var3) {
		this.func_102021_a(Arrays.asList(new String[]{var1}), var2, var3);
	}

	protected void func_102021_a(List<String> var1, int var2, int var3) {
		if(!var1.isEmpty()) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int var4 = 0;
			Iterator<String> var5 = var1.iterator();

			while(var5.hasNext()) {
				String var6 = (String)var5.next();
				int var7 = this.fontRenderer.getStringWidth(var6);
				if(var7 > var4) {
					var4 = var7;
				}
			}

			int var14 = var2 + 12;
			int var15 = var3 - 12;
			int var8 = 8;
			if(var1.size() > 1) {
				var8 += 2 + (var1.size() - 1) * 10;
			}

			if(var14 + var4 > this.width) {
				var14 -= 28 + var4;
			}

			if(var15 + var8 + 6 > this.height) {
				var15 = this.height - var8 - 6;
			}

			this.zLevel = 300.0F;
			itemRenderer.zLevel = 300.0F;
			int var9 = -267386864;
			this.drawGradientRect(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
			this.drawGradientRect(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
			this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
			this.drawGradientRect(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
			this.drawGradientRect(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
			int var10 = 1347420415;
			int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
			this.drawGradientRect(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
			this.drawGradientRect(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
			this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
			this.drawGradientRect(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

			for(int var12 = 0; var12 < var1.size(); ++var12) {
				String var13 = (String)var1.get(var12);
				this.fontRenderer.drawStringWithShadow(var13, var14, var15, -1);
				if(var12 == 0) {
					var15 += 2;
				}

				var15 += 10;
			}

			this.zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
	
	protected void toolTip(String tipStr, int x, int y) {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		int w = this.fontRenderer.getStringWidth(tipStr);
		int x0 = x + 12;
		int y0 = y - 12;
		
		byte fontH = 8;
		this.zLevel = 300.0F;
		itemRenderer.zLevel = 300.0F;
		int cBorder = -267386864;
		this.drawGradientRect(x0 - 3, y0 - 4, x0 + w + 3, y0 - 3, cBorder, cBorder);
		this.drawGradientRect(x0 - 3, y0 + fontH + 3, x0 + w + 3, y0 + fontH + 4, cBorder, cBorder);
		this.drawGradientRect(x0 - 3, y0 - 3, x0 + w + 3, y0 + fontH + 3, cBorder, cBorder);
		this.drawGradientRect(x0 - 4, y0 - 3, x0 - 3, y0 + fontH + 3, cBorder, cBorder);
		this.drawGradientRect(x0 + w + 3, y0 - 3, x0 + w + 4, y0 + fontH + 3, cBorder, cBorder);
		int cBg1 = 1347420415;
		int cBg2 = (cBg1 & 16711422) >> 1 | cBg1 & -16777216;
		this.drawGradientRect(x0 - 3, y0 - 3 + 1, x0 - 3 + 1, y0 + fontH + 3 - 1, cBg1, cBg2);
		this.drawGradientRect(x0 + w + 2, y0 - 3 + 1, x0 + w + 3, y0 + fontH + 3 - 1, cBg1, cBg2);
		this.drawGradientRect(x0 - 3, y0 - 3, x0 + w + 3, y0 - 3 + 1, cBg1, cBg1);
		this.drawGradientRect(x0 - 3, y0 + fontH + 2, x0 + w + 3, y0 + fontH + 3, cBg2, cBg2);
		this.fontRenderer.drawStringWithShadow(tipStr, x0, y0, -1);
		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
	}

	protected void drawGuiContainerForegroundLayer() {
	}

	protected abstract void drawGuiContainerBackgroundLayer(float f1, int i2, int i3);

	private void drawSlotInventory(Slot slot) {
		int x = slot.xDisplayPosition;
		int y = slot.yDisplayPosition;
		ItemStack theStack = slot.getStack();
		boolean hover = false;
		boolean var6 = slot == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
		ItemStack inventoryStack = this.mc.thePlayer.inventory.getItemStack();
		
		if(slot == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && theStack != null) {
			theStack = theStack.copy();
			theStack.stackSize /= 2;
		} else if(this.isDraggingStack && this.draggedOverSlots.contains(slot) && inventoryStack != null) {
			if(this.draggedOverSlots.size() == 1) {
				return;
			}

			if(Container.validDrag(slot, inventoryStack, true) && this.inventorySlots.func_94531_b(slot)) {
				theStack = inventoryStack.copy();
				hover = true;
				Container.divideStackInSlots(this.draggedOverSlots, this.initialButton, theStack, slot.getStack() == null ? 0 : slot.getStack().stackSize);
				if(theStack.stackSize > theStack.getMaxStackSize()) {
					theStack.stackSize = theStack.getMaxStackSize();
				}

				if(theStack.stackSize > slot.getSlotStackLimit()) {
					theStack.stackSize = slot.getSlotStackLimit();
				}
			} else {
				this.draggedOverSlots.remove(slot);
				this.func_94066_g();
			}
		}
		
		this.zLevel = 100.0F;
		itemRenderer.zLevel = 100.0F;
		if(theStack == null) {
			int i8 = slot.getBackgroundIconIndex();
			if (i8 >= 0) {
				GL11.glDisable(GL11.GL_LIGHTING);
				this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/items.png"));
				itemRenderer.renderTexturedQuad(x, y, i8 % 16 * 16, i8 / 16 * 16, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
				var6 = true;
			}
		}

		if(!var6) {
			if(hover) {
				drawRect(x, y, x + 16, y + 16, -2130706433);
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, theStack, x, y);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, theStack, x, y);
		}
		
		itemRenderer.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}
	
	private void func_94066_g() {
		ItemStack var1 = this.mc.thePlayer.inventory.getItemStack();
		if(var1 != null && this.isDraggingStack) {
			this.currentStackSize = var1.stackSize;

			ItemStack var4;
			int var5;
			for(Iterator<Slot> var2 = this.draggedOverSlots.iterator(); var2.hasNext(); this.currentStackSize -= var4.stackSize - var5) {
				Slot var3 = (Slot)var2.next();
				var4 = var1.copy();
				var5 = var3.getStack() == null ? 0 : var3.getStack().stackSize;
				Container.divideStackInSlots(this.draggedOverSlots, this.initialButton, var4, var5);
				if(var4.stackSize > var4.getMaxStackSize()) {
					var4.stackSize = var4.getMaxStackSize();
				}

				if(var4.stackSize > var3.getSlotStackLimit()) {
					var4.stackSize = var3.getSlotStackLimit();
				}
			}

		}
	}

	private Slot getSlotAtPosition(int i1, int i2) {
		for (int i3 = 0; i3 < this.inventorySlots.inventorySlots.size(); ++i3) {
			Slot slot4 = (Slot) this.inventorySlots.inventorySlots.get(i3);
			if (this.isMouseOverSlot(slot4, i1, i2)) {
				return slot4;
			}
		}

		return null;
	}

	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		boolean middleClick = button == GameSettingsKeys.keyBindPickBlock.keyCode + 100;
		Slot slot = this.getSlotAtPosition(x, y);
		long clickedTime = Minecraft.getSystemTime();

		this.notMovedToAnotherSlot = this.lastSlotUnderMouse == slot && clickedTime - this.lastMouseClickTime < 250L && this.prevButtonState == button;

		this.field_94068_E = false;
		
		if(button == 0 || button == 1 || middleClick) {
			int x0 = this.guiLeft;
			int y0 = this.guiTop;
			boolean clickedOutside = x < x0 || y < y0 || x >= x0 + this.xSize || y >= y0 + this.ySize;
			
			int slotNumberClicked = -1;

			if(slot != null) {
				slotNumberClicked = slot.slotNumber;
			}

			if(clickedOutside) {
				slotNumberClicked = -999;
			}
	
			if(slotNumberClicked != -1) {
				// Clicked on a slot, or outside.

				if(!this.isDraggingStack) {
					if(this.mc.thePlayer.inventory.getItemStack() == null) {
						if(button == GameSettingsKeys.keyBindPickBlock.keyCode + 100) {
							this.handleMouseClick(slot, slotNumberClicked, button, 3);
						} else {
							boolean shiftClickOnSlot = slotNumberClicked != -999 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));

							byte action = 0;

							if(shiftClickOnSlot) {
								this.field_94075_K = slot!= null && slot.getHasStack() ? slot.getStack() : null;
								action = 1;
							} else if(slotNumberClicked == -999) {
								action = 4;
							}

							this.handleMouseClick(slot, slotNumberClicked, button, action);
						}

						this.field_94068_E = true;
					} else {
						this.isDraggingStack = true;
						this.draggingButton = button;
						this.draggedOverSlots.clear();
						if(button == 0) {
							this.initialButton = 0;
						} else if(button == 1) {
							this.initialButton = 1;
						}
					}
				}
			}
		}

		this.lastSlotUnderMouse = slot;
		this.lastMouseClickTime = clickedTime;
		this.prevButtonState = button;
	}
	
	protected void func_85041_a(int x, int y, int button, long var4) {
		Slot var6 = this.getSlotAtPosition(x, y);
		ItemStack var7 = this.mc.thePlayer.inventory.getItemStack();
		if(this.isDraggingStack && var6 != null && var7 != null && var7.stackSize > this.draggedOverSlots.size() && Container.validDrag(var6, var7, true) && var6.isItemValid(var7) && this.inventorySlots.func_94531_b(var6)) {
			this.draggedOverSlots.add(var6);
			this.func_94066_g();
		}

	}
	
	protected void mouseMovedOrUp(int x, int y, int button) {
		
		Slot var4 = this.getSlotAtPosition(x, y);
		int var5 = this.guiLeft;
		int var6 = this.guiTop;
		boolean var7 = x < var5 || y < var6 || x >= var5 + this.xSize || y >= var6 + this.ySize;
		int var8 = -1;
		if(var4 != null) {
			var8 = var4.slotNumber;
		}

		if(var7) {
			var8 = -999;
		}

		Slot var10;
		Iterator<Slot> var11;
		if(this.notMovedToAnotherSlot && var4 != null && button == 0 && this.inventorySlots.func_94530_a((ItemStack)null, var4)) {
			if(shiftPressed()) {
				if(var4 != null && var4.inventory != null && this.field_94075_K != null) {
					var11 = this.inventorySlots.inventorySlots.iterator();

					while(var11.hasNext()) {
						var10 = (Slot)var11.next();
						if(var10 != null && var10.canTakeStack(this.mc.thePlayer) && var10.getHasStack() && var10.inventory == var4.inventory && Container.validDrag(var10, this.field_94075_K, true)) {
							this.handleMouseClick(var10, var10.slotNumber, button, 1);
						}
					}
				}
			} else {
				this.handleMouseClick(var4, var8, button, 6);
			}

			this.notMovedToAnotherSlot = false;
			this.lastMouseClickTime = 0L;
		} else {
			if(this.isDraggingStack && this.draggingButton != button) {
				this.isDraggingStack = false;
				this.draggedOverSlots.clear();
				this.field_94068_E = true;
				return;
			}

			if(this.field_94068_E) {
				this.field_94068_E = false;
				return;
			}
			boolean var9;
			
			if(this.isDraggingStack && !this.draggedOverSlots.isEmpty() && !(this instanceof GuiContainerCreative)) {
				this.handleMouseClick((Slot)null, -999, Container.encodeButtonAndState(0, this.initialButton), 5);
				var11 = this.draggedOverSlots.iterator();

				while(var11.hasNext()) {
					var10 = (Slot)var11.next();
					this.handleMouseClick(var10, var10.slotNumber, Container.encodeButtonAndState(1, this.initialButton), 5);
				}

				this.handleMouseClick((Slot)null, -999, Container.encodeButtonAndState(2, this.initialButton), 5);
			} else if(this.mc.thePlayer.inventory.getItemStack() != null) {
				if(button == GameSettingsKeys.keyBindPickBlock.keyCode + 100) {
					this.handleMouseClick(var4, var8, button, 3);
				} else {
					var9 = var8 != -999 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
					if(var9) {
						this.field_94075_K = var4 != null && var4.getHasStack() ? var4.getStack() : null;
					}
					this.handleMouseClick(var4, var8, button, var9 ? 1 : 0);
				}
			}
		}

		if(this.mc.thePlayer.inventory.getItemStack() == null) {
			this.lastMouseClickTime = 0L;
		}

		this.isDraggingStack = false;
	}

	private boolean isMouseOverSlot(Slot theSlot, int x, int y) {
		int x0 = this.guiLeft;
		int y0 = this.guiTop;
		x -= x0;
		y -= y0;
		
		boolean inside = x >= theSlot.xDisplayPosition - 1 && x < theSlot.xDisplayPosition + 16 + 1
				&& y >= theSlot.yDisplayPosition - 1 && y < theSlot.yDisplayPosition + 16 + 1;

		return inside;
	}

	protected boolean mouseInsideRelative(int x, int y, int w, int h, int mouseX, int mouseY) {
		mouseX -= this.guiLeft;
		mouseY -= this.guiTop;
		return mouseX >= x - 1 && mouseX < x + w + 1 && mouseY >= y - 1 && mouseY < y + h + 1;
	}

	protected void handleMouseClick(Slot var1, int var2, int var3, int var4) {
		if(var1 != null) {
			var2 = var1.slotNumber;
		}

		this.mc.playerController.windowClick(this.inventorySlots.windowId, var2, var3, var4, this.mc.thePlayer);
	}

	protected void keyTyped(char c1, int i2) {
		if (i2 == 1 || i2 == GameSettingsKeys.keyBindInventory.keyCode) {
			this.mc.thePlayer.closeScreen();
		}

	}

	public void onGuiClosed() {
		if (this.mc.thePlayer != null) {
			this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
			this.mc.playerController.handleCloseInventory(this.inventorySlots.windowId, this.mc.thePlayer);
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void updateScreen() {
		super.updateScreen();
		if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
			this.mc.thePlayer.closeScreen();
		}

	}
}
