package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.ContainerCreative;
import net.minecraft.client.GameSettingsKeys;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.SlotCreativeInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.InventoryBasic;
import net.minecraft.world.inventory.InventoryPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.creative.CreativeTabs;

public class GuiContainerCreative extends GuiContainer {
	private static InventoryBasic inventory = new InventoryBasic("tmp", 45);
	private static int currentTab = CreativeTabs.tabBlock.getTabIndex();

	/** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
	private float currentScroll = 0.0F;

	/** True if the scrollbar is being dragged */
	private boolean isScrolling = false;

	/**
	 * True if the left mouse button was held down last time drawScreen was called.
	 */
	private boolean wasClicking;
	private GuiTextField searchField;
	private List<Slot> inventorySlotsCopy;
	private Slot recycleBin = null;
	private boolean field_74234_w = false;

	public GuiContainerCreative(EntityPlayer par1EntityPlayer) {
		super(new ContainerCreative(par1EntityPlayer));
		par1EntityPlayer.craftingInventory = this.inventorySlots;
		this.allowUserInput = true;
		this.ySize = 136;
		this.xSize = 195;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (!this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
		}
	}

	protected void handleMouseClick(Slot par1Slot, int slotNumberClicked, int mouseButton, int action) {
		this.field_74234_w = true;
		ItemStack itemStack;
		InventoryPlayer inventoryPlayer;

		if (par1Slot != null) {
			int var5;

			if (par1Slot == this.recycleBin && action == 1) {
				for (var5 = 0; var5 < this.mc.thePlayer.inventorySlots.getInventory().size(); ++var5) {
					this.mc.playerController.sendSlotPacket((ItemStack) null, var5);
				}
			} else {
				ItemStack var7;

				if (currentTab == CreativeTabs.tabInventory.getTabIndex()) {
					if (par1Slot == this.recycleBin) {
						this.mc.thePlayer.inventory.setItemStack((ItemStack) null);
					} else {
						var5 = SlotCreativeInventory.getSlot((SlotCreativeInventory) par1Slot).slotNumber;
						itemStack = this.mc.thePlayer.inventorySlots.getSlot(var5).getStack();

						if (action == 1 && (itemStack == null || !(itemStack.getItem() instanceof ItemArmor))) {
							this.mc.playerController.sendSlotPacket((ItemStack) null, var5);
						} else {
							this.mc.thePlayer.inventorySlots.slotClick(var5, mouseButton, action, this.mc.thePlayer);
							var7 = this.mc.thePlayer.inventorySlots.getSlot(var5).getStack();
							this.mc.playerController.sendSlotPacket(var7, var5);
						}
					}
				} else if (par1Slot.inventory == inventory) {
					inventoryPlayer = this.mc.thePlayer.inventory;
					itemStack = inventoryPlayer.getItemStack();
					var7 = par1Slot.getStack();

					if (itemStack != null && var7 != null && itemStack.isItemEqual(var7)) {
						if (mouseButton == 0) {
							if (action == 1) {
								itemStack.stackSize = itemStack.getMaxStackSize();
							} else if (itemStack.stackSize < itemStack.getMaxStackSize()) {
								++itemStack.stackSize;
							}
						} else if (itemStack.stackSize <= 1) {
							inventoryPlayer.setItemStack((ItemStack) null);
						} else {
							--itemStack.stackSize;
						}
					} else if (var7 != null && itemStack == null) {
						boolean var8 = false;

						if (!var8) {
							inventoryPlayer.setItemStack(ItemStack.copyItemStack(var7));
							itemStack = inventoryPlayer.getItemStack();

							if (action == 1) {
								itemStack.stackSize = itemStack.getMaxStackSize();
							}
						}
					} else {
						inventoryPlayer.setItemStack((ItemStack) null);
					}
				} else {
					this.inventorySlots.slotClick(par1Slot.slotNumber, mouseButton, action, this.mc.thePlayer);
					ItemStack var10 = this.inventorySlots.getSlot(par1Slot.slotNumber).getStack();
					this.mc.playerController.sendSlotPacket(var10,
							par1Slot.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
				}
			}
		} else {
			inventoryPlayer = this.mc.thePlayer.inventory;

			if (inventoryPlayer.getItemStack() != null) {
				if (mouseButton == 0) {
					this.mc.thePlayer.dropPlayerItem(inventoryPlayer.getItemStack());
					this.mc.playerController.func_35639_a(inventoryPlayer.getItemStack());
					inventoryPlayer.setItemStack((ItemStack) null);
				}

				if (mouseButton == 1) {
					itemStack = inventoryPlayer.getItemStack().splitStack(1);
					this.mc.thePlayer.dropPlayerItem(itemStack);
					this.mc.playerController.func_35639_a(itemStack);

					if (inventoryPlayer.getItemStack().stackSize == 0) {
						inventoryPlayer.setItemStack((ItemStack) null);
					}
				}
			}
		}
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		if (this.mc.playerController.isInCreativeMode()) {
			super.initGui();
			this.controlList.clear();
			Keyboard.enableRepeatEvents(true);
			this.searchField = new GuiTextField(this, this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 89,
					this.fontRenderer.FONT_HEIGHT);
			this.searchField.setMaxStringLength(15);
			this.searchField.setEnableBackgroundDrawing(false);
			this.searchField.func_73790_e(false);
			this.searchField.setBackgroundColor(16777215);
			int var1 = currentTab;
			currentTab = -1;
			this.displayTab(CreativeTabs.creativeTabList.get(var1));
		} else {
			this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
		}
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
		if (currentTab != CreativeTabs.tabAllSearch.getTabIndex()) {
			if (Keyboard.isKeyDown(GameSettingsKeys.keyBindChat.keyCode)) {
				this.displayTab(CreativeTabs.tabAllSearch);
			} else {
				super.keyTyped(par1, par2);
			}
		} else {
			if (this.field_74234_w) {
				this.field_74234_w = false;
				this.searchField.setText("");
			}

			if (this.searchField.textboxKeyTyped(par1, par2)) {
				this.doSearch();
			} else {
				super.keyTyped(par1, par2);
			}
		}
	}

	private void doSearch() {
		ContainerCreative inventory = (ContainerCreative) this.inventorySlots;
		inventory.itemList.clear();
		Item[] allItems = Item.itemsList;
		int numItems = allItems.length;

		for (int i = 0; i < numItems; ++i) {
			Item item = allItems[i];

			if (item != null && item.getCreativeTab() != null && !item.softlocked) {
				item.getSubItems(item.shiftedIndex, (CreativeTabs) null, inventory.itemList);
			}
		}

		Iterator<ItemStack> it = inventory.itemList.iterator();
		String searchString = this.searchField.getText().toLowerCase();

		while (it.hasNext()) {
			ItemStack stack = (ItemStack) it.next();
			boolean keep = false;
			Iterator<String> itInfo = stack.getItemNameandInformation().iterator();

			while (true) {
				if (itInfo.hasNext()) {
					String label = (String) itInfo.next();

					if (!label.toLowerCase().contains(searchString)) {
						continue;
					}

					keep = true;
				}

				if (!keep) {
					it.remove();
				}

				break;
			}
		}

		this.currentScroll = 0.0F;
		inventory.scrollTo(0.0F);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the
	 * items)
	 */
	protected void drawGuiContainerForegroundLayer() {
		CreativeTabs var1 = CreativeTabs.creativeTabList.get(currentTab);

		if (var1.drawInForegroundOfTab()) {
			this.fontRenderer.drawString(var1.getTranslatedTabLabel(), 8, 6, 4210752);
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseB) {
		if (mouseB == 0) {
			int xRel = mouseX - this.guiLeft;
			int yRel = mouseY - this.guiTop;
			
			for (int i = 0; i < CreativeTabs.creativeTabList.size(); ++i) {
				CreativeTabs creativeTab = CreativeTabs.creativeTabList.get(i);

					if (this.hoverTab(creativeTab, xRel, yRel)) {
						this.displayTab(creativeTab);
						return;
					}
				}
			}

		super.mouseClicked(mouseX, mouseY, mouseB);
	}


	/**
	 * returns (if you are not on the inventoryTab) and (the flag isn't set) and(
	 * you have more than 1 page of items)
	 */
	private boolean needsScrollBars() {
		return currentTab != CreativeTabs.tabInventory.getTabIndex()
				&& CreativeTabs.creativeTabList.get(currentTab).shouldHidePlayerInventory()
				&& ((ContainerCreative) this.inventorySlots).hasMoreThan1PageOfItemsInList();
	}

	private void displayTab(CreativeTabs par1CreativeTabs) {
		int var2 = currentTab;
		currentTab = par1CreativeTabs.getTabIndex();
		ContainerCreative var3 = (ContainerCreative) this.inventorySlots;
		var3.itemList.clear();
		par1CreativeTabs.displayAllReleventItems(var3.itemList);

		if (par1CreativeTabs == CreativeTabs.tabInventory) {
			Container var4 = this.mc.thePlayer.inventorySlots;

			if (this.inventorySlotsCopy == null) {
				this.inventorySlotsCopy = var3.inventorySlots;
			}

			var3.inventorySlots = new ArrayList<Slot>();

			for (int var5 = 0; var5 < var4.inventorySlots.size(); ++var5) {
				SlotCreativeInventory var6 = new SlotCreativeInventory(this, (Slot) var4.inventorySlots.get(var5),
						var5);
				var3.inventorySlots.add(var6);
				int var7;
				int var8;
				int var9;

				if (var5 >= 5 && var5 < 9) {
					var7 = var5 - 5;
					var8 = var7 / 2;
					var9 = var7 % 2;
					var6.xDisplayPosition = 9 + var8 * 54;
					var6.yDisplayPosition = 6 + var9 * 27;
				} else if (var5 >= 0 && var5 < 5) {
					var6.yDisplayPosition = -2000;
					var6.xDisplayPosition = -2000;
				} else if (var5 < var4.inventorySlots.size()) {
					var7 = var5 - 9;
					var8 = var7 % 9;
					var9 = var7 / 9;
					var6.xDisplayPosition = 9 + var8 * 18;

					if (var5 >= 36) {
						var6.yDisplayPosition = 112;
					} else {
						var6.yDisplayPosition = 54 + var9 * 18;
					}
				}
			}

			this.recycleBin = new Slot(inventory, 0, 173, 112);
			var3.inventorySlots.add(this.recycleBin);
		} else if (var2 == CreativeTabs.tabInventory.getTabIndex()) {
			var3.inventorySlots = this.inventorySlotsCopy;
			this.inventorySlotsCopy = null;
		}

		if (this.searchField != null) {
			if (par1CreativeTabs == CreativeTabs.tabAllSearch) {
				this.searchField.func_73790_e(true);
				this.searchField.setCanLoseFocus(false);
				this.searchField.setFocused(true);
				this.searchField.setText("");
				this.doSearch();
			} else {
				this.searchField.func_73790_e(false);
				this.searchField.setCanLoseFocus(true);
				this.searchField.setFocused(false);
			}
		}

		this.currentScroll = 0.0F;
		var3.scrollTo(0.0F);
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() {
		super.handleMouseInput();
		int var1 = Mouse.getEventDWheel();

		if (var1 != 0 && this.needsScrollBars()) {
			int var2 = ((ContainerCreative) this.inventorySlots).itemList.size() / 9 - 5 + 1;

			if (var1 > 0) {
				var1 = 1;
			}

			if (var1 < 0) {
				var1 = -1;
			}

			this.currentScroll = (float) ((double) this.currentScroll - (double) var1 / (double) var2);

			if (this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if (this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}

			((ContainerCreative) this.inventorySlots).scrollTo(this.currentScroll);
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		// Scroll bar click

		boolean mouseClicked = Mouse.isButtonDown(0);
		int x1 = this.guiLeft + 175;
		int y1 = this.guiTop + 18;
		int x2 = x1 + 14;
		int y2 = y1 + 112;

		if (!this.wasClicking && mouseClicked && mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2) {
			this.isScrolling = this.needsScrollBars();
		}

		if (!mouseClicked) {
			this.isScrolling = false;
		}

		this.wasClicking = mouseClicked;

		// Scroll

		if (this.isScrolling) {
			this.currentScroll = ((float) (mouseY - y1) - 7.5F) / ((float) (y2 - y1) - 15.0F);

			if (this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if (this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}

			((ContainerCreative) this.inventorySlots).scrollTo(this.currentScroll);
		}

		// Draw GUI

		super.drawScreen(mouseX, mouseY, partialTicks);

		for(int i = 0; i < CreativeTabs.creativeTabList.size(); i ++) {	
			if (this.hoverTabAndTooltip(CreativeTabs.creativeTabList.get(i), mouseX, mouseY)) {
				break;
			}
		}

		if (
			this.recycleBin != null && 
			currentTab == CreativeTabs.tabInventory.getTabIndex() && 
			this.mouseInsideRelative(this.recycleBin.xDisplayPosition, this.recycleBin.yDisplayPosition, 16, 16, mouseX, mouseY)
		) {
			this.toolTip(StringTranslate.getInstance().translateKey("inventory.binSlot"), mouseX, mouseY);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();

		int txItems = this.mc.renderEngine.getTexture("/gui/allitems.png");
		CreativeTabs theTab = CreativeTabs.creativeTabList.get(currentTab);
		int txBg = this.mc.renderEngine.getTexture("/gui/creative_inv/" + theTab.getBackgroundImageName());
		
		for (int i = 0; i < CreativeTabs.creativeTabList.size(); ++i) {
			CreativeTabs creativeTab = CreativeTabs.creativeTabList.get(i);
				this.mc.renderEngine.bindTexture(txItems);
				if (creativeTab.getTabIndex() != currentTab) {
					this.func_74233_a(creativeTab);
			}
		}

		this.mc.renderEngine.bindTexture(txBg);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		this.searchField.drawTextBox();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int scrollX = this.guiLeft + 175;
		int scrollY = this.guiTop + 18;
		int scrollBottom = scrollY + 112;
		this.mc.renderEngine.bindTexture(txItems);

		if (theTab.shouldHidePlayerInventory()) {
			this.drawTexturedModalRect(scrollX, scrollY + (int) ((float) (scrollBottom - scrollY - 17) * this.currentScroll),
					232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
		}

		this.func_74233_a(theTab);

		if (theTab == CreativeTabs.tabInventory) {
			GuiInventory.drawGuyInGui(this.mc, this.guiLeft + 43, this.guiTop + 45, 20,
					(float) (this.guiLeft + 43 - mouseX), (float) (this.guiTop + 45 - 30 - mouseY));
		}
	}

	protected boolean hoverTab(CreativeTabs theTab, int mouseX, int mouseY) {
		int column = theTab.getTabColumn();
		int x = 28 * column;

		if (column == 5) {
			x = this.xSize - 28 + 2;
		} else if (column > 0) {
			x += column;
		}

		int y;

		if (theTab.isTabInFirstRow()) {
			y = - 32;
		} else {
			y = this.ySize;
		}

		return mouseX >= x && mouseX <= x + 28 && mouseY >= y && mouseY <= y + 32;
	}

	protected boolean hoverTabAndTooltip(CreativeTabs theTab, int mouseX, int mouseY) {
		int column = theTab.getTabColumn();
		int x = 28 * column;
	
		if (column == 5) {
			x = this.xSize - 28 + 2;
		} else if (column > 0) {
			x += column;
		}

		int y;

		if (theTab.isTabInFirstRow()) {
			y = - 32;
		} else {
			y = this.ySize;
		}

		if (this.mouseInsideRelative(x + 3, y + 3, 23, 27, mouseX, mouseY)) {
			this.toolTip(theTab.getTranslatedTabLabel(), mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}
	
	protected void func_74233_a(CreativeTabs par1CreativeTabs) {
		boolean var2 = par1CreativeTabs.getTabIndex() == currentTab;
		boolean var3 = par1CreativeTabs.isTabInFirstRow();
		int var4 = par1CreativeTabs.getTabColumn();
		int var5 = var4 * 28;
		int var6 = 0;
		int var7 = this.guiLeft + 28 * var4;
		int var8 = this.guiTop;
		byte var9 = 32;

		if (var2) {
			var6 += 32;
		}

		if (var4 == 5) {
			var7 = this.guiLeft + this.xSize - 28;
		} else if (var4 > 0) {
			var7 += var4;
		}

		if (var3) {
			var8 -= 28;
		} else {
			var6 += 64;
			var8 += this.ySize - 4;
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		this.drawTexturedModalRect(var7, var8, var5, var6, 28, var9);
		this.zLevel = 100.0F;
		itemRenderer.zLevel = 100.0F;
		var7 += 6;
		var8 += 8 + (var3 ? 1 : -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		ItemStack var10 = new ItemStack(par1CreativeTabs.getTabIconItem());
		itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var7, var8);
		itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var7, var8);
		GL11.glDisable(GL11.GL_LIGHTING);
		itemRenderer.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	public int func_74230_h() {
		return currentTab;
	}

	/**
	 * Returns the creative inventory
	 */
	public static InventoryBasic getInventory() {
		return inventory;
	}
}
