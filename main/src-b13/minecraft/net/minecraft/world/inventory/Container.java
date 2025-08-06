package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class Container {
	public List<ItemStack> inventoryItemStacks = new ArrayList<ItemStack>();
	public List<Slot> inventorySlots = new ArrayList<Slot>();
	public int windowId = 0;
	private short transactionID = 0;
	
	private int decodedMouseButton = -1;
	private int prevButtonState = 0;
	private final Set<Slot> activeSlots = new HashSet<Slot>();

	protected List<ICrafting> crafters = new ArrayList<ICrafting>();
	private Set<EntityPlayer> cantCraftPlayers = new HashSet<EntityPlayer>();

	protected void addSlot(Slot slot1) {
		slot1.slotNumber = this.inventorySlots.size();
		this.inventorySlots.add(slot1);
		this.inventoryItemStacks.add(null);
	}

	protected Slot addSlotToContainer(Slot par1Slot) {
		par1Slot.slotNumber = this.inventorySlots.size();
		this.inventorySlots.add(par1Slot);
		this.inventoryItemStacks.add(null);
		return par1Slot;
	}

	public void onCraftGuiOpened(ICrafting iCrafting1) {
		if (this.crafters.contains(iCrafting1)) {
			throw new IllegalArgumentException("Listener already listening");
		} else {
			this.crafters.add(iCrafting1);
			iCrafting1.updateCraftingInventory(this, this.getInventory());
			this.updateCraftingResults();
		}
	}

	public List<ItemStack> getInventory() {
		ArrayList<ItemStack> arrayList1 = new ArrayList<ItemStack>();

		for (int i2 = 0; i2 < this.inventorySlots.size(); ++i2) {
			arrayList1.add(((Slot) this.inventorySlots.get(i2)).getStack());
		}

		return arrayList1;
	}

	public void updateCraftingResults() {
		for (int i1 = 0; i1 < this.inventorySlots.size(); ++i1) {
			ItemStack itemStack2 = ((Slot) this.inventorySlots.get(i1)).getStack();
			ItemStack itemStack3 = (ItemStack) this.inventoryItemStacks.get(i1);
			if (!ItemStack.areItemStacksEqual(itemStack3, itemStack2)) {
				itemStack3 = itemStack2 == null ? null : itemStack2.copy();
				this.inventoryItemStacks.set(i1, itemStack3);

				for (int i4 = 0; i4 < this.crafters.size(); ++i4) {
					((ICrafting) this.crafters.get(i4)).updateCraftingInventorySlot(this, i1, itemStack3);
				}
			}
		}

	}

	public boolean enchantItem(EntityPlayer entityPlayer1, int i2) {
		return false;
	}

	public Slot getSlotFromInventory(IInventory iInventory1, int i2) {
		for (int i3 = 0; i3 < this.inventorySlots.size(); ++i3) {
			Slot slot4 = (Slot) this.inventorySlots.get(i3);
			if (slot4.isHere(iInventory1, i2)) {
				return slot4;
			}
		}

		return null;
	}

	public Slot getSlot(int i1) {
		return (Slot) this.inventorySlots.get(i1);
	}

	public ItemStack transferStackInSlot(int i1) {
		Slot slot2 = (Slot) this.inventorySlots.get(i1);
		return slot2 != null ? slot2.getStack() : null;
	}

	public ItemStack slotClick(int slotNum, int buttonPressed, int shiftPressed, EntityPlayer entityPlayer) {
		ItemStack var5 = null;
		InventoryPlayer var6 = entityPlayer.inventory;
		int var9;
		ItemStack var17;
		if(shiftPressed == 5) {
			int prevButton = this.prevButtonState;
			this.prevButtonState = getStateFromEncodedButton(buttonPressed);
			if((prevButton != 1 || this.prevButtonState != 2) && prevButton != this.prevButtonState) {
				this.clearMouseAction();
			} else if(var6.getItemStack() == null) {
				this.clearMouseAction();
			} else if(this.prevButtonState == 0) {
				this.decodedMouseButton = decodeButtonFromState(buttonPressed);
				if(isNormalClick(this.decodedMouseButton)) {
					this.prevButtonState = 1;
					this.activeSlots.clear();
				} else {
					this.clearMouseAction();
				}
			} else if(this.prevButtonState == 1) {
				Slot var8 = (Slot)this.inventorySlots.get(slotNum);
				if(var8 != null && validDrag(var8, var6.getItemStack(), true) && var8.isItemValid(var6.getItemStack()) && var6.getItemStack().stackSize > this.activeSlots.size() && this.func_94531_b(var8)) {
					this.activeSlots.add(var8);
				}
			} else if(this.prevButtonState == 2) {
				if(!this.activeSlots.isEmpty()) {
					var17 = var6.getItemStack().copy();
					var9 = var6.getItemStack().stackSize;
					Iterator<Slot> var10 = this.activeSlots.iterator();

					while(var10.hasNext()) {
						Slot var11 = (Slot)var10.next();
						if(var11 != null && validDrag(var11, var6.getItemStack(), true) && var11.isItemValid(var6.getItemStack()) && var6.getItemStack().stackSize >= this.activeSlots.size() && this.func_94531_b(var11)) {
							ItemStack var12 = var17.copy();
							int var13 = var11.getHasStack() ? var11.getStack().stackSize : 0;
							divideStackInSlots(this.activeSlots, this.decodedMouseButton, var12, var13);
							if(var12.stackSize > var12.getMaxStackSize()) {
								var12.stackSize = var12.getMaxStackSize();
							}

							if(var12.stackSize > var11.getSlotStackLimit()) {
								var12.stackSize = var11.getSlotStackLimit();
							}

							var9 -= var12.stackSize - var13;
							var11.putStack(var12);
						}
					}

					var17.stackSize = var9;
					if(var17.stackSize <= 0) {
						var17 = null;
					}

					var6.setItemStack(var17);
				}

				this.clearMouseAction();
			} else {
				this.clearMouseAction();
			}
		} else if(this.prevButtonState != 0) {
			this.clearMouseAction();
		} else {
			Slot var16;
			int var19;
			ItemStack var22;
			if((shiftPressed == 0 || shiftPressed == 1) && (buttonPressed == 0 || buttonPressed == 1)) {
				if(slotNum == -999) {
					if(var6.getItemStack() != null && slotNum == -999) {
						if(buttonPressed == 0) {
							entityPlayer.dropPlayerItem(var6.getItemStack());
							var6.setItemStack((ItemStack)null);
						}

						if(buttonPressed == 1) {
							entityPlayer.dropPlayerItem(var6.getItemStack().splitStack(1));
							if(var6.getItemStack().stackSize == 0) {
								var6.setItemStack((ItemStack)null);
							}
						}
					}
				} else if(shiftPressed == 1) {
					if(slotNum < 0) {
						return null;
					}

					// Get the slot #slotNum
					var16 = (Slot)this.inventorySlots.get(slotNum);
					
					if(var16 != null && var16.canTakeStack(entityPlayer)) {
						// Slot not null and player can take slot
						
						var17 = this.transferStackInSlot(slotNum);
						
						
						if(var17 != null) {
							var9 = var17.itemID;
							var5 = var17.copy();
							if(var16 != null && var16.getStack() != null && var16.getStack().itemID == var9) {
								this.retrySlotClick(slotNum, buttonPressed, true, entityPlayer);
							}
						}
					}
				} else {
					if(slotNum < 0) {
						return null;
					}

					var16 = (Slot)this.inventorySlots.get(slotNum);
					if(var16 != null) {
						var17 = var16.getStack();
						ItemStack var20 = var6.getItemStack();
						if(var17 != null) {
							var5 = var17.copy();
						}

						if(var17 == null) {
							if(var20 != null && var16.isItemValid(var20)) {
								var19 = buttonPressed == 0 ? var20.stackSize : 1;
								if(var19 > var16.getSlotStackLimit()) {
									var19 = var16.getSlotStackLimit();
								}

								var16.putStack(var20.splitStack(var19));
								if(var20.stackSize == 0) {
									var6.setItemStack((ItemStack)null);
								}
							}
						} else if(var16.canTakeStack(entityPlayer)) {
							if(var20 == null) {
								var19 = buttonPressed == 0 ? var17.stackSize : (var17.stackSize + 1) / 2;
								var22 = var16.decrStackSize(var19);
								var6.setItemStack(var22);
								if(var17.stackSize == 0) {
									var16.putStack((ItemStack)null);
								}

								var16.onPickupFromSlot(entityPlayer, var6.getItemStack());
							} else if(var16.isItemValid(var20)) {
								if(var17.itemID == var20.itemID && var17.getItemDamage() == var20.getItemDamage() /*&& ItemStack.areItemStackTagsEqual(var17, var20)*/) {
									var19 = buttonPressed == 0 ? var20.stackSize : 1;
									if(var19 > var16.getSlotStackLimit() - var17.stackSize) {
										var19 = var16.getSlotStackLimit() - var17.stackSize;
									}

									if(var19 > var20.getMaxStackSize() - var17.stackSize) {
										var19 = var20.getMaxStackSize() - var17.stackSize;
									}

									var20.splitStack(var19);
									if(var20.stackSize == 0) {
										var6.setItemStack((ItemStack)null);
									}

									var17.stackSize += var19;
								} else if(var20.stackSize <= var16.getSlotStackLimit()) {
									var16.putStack(var20);
									var6.setItemStack(var17);
								}
							} else if(var17.itemID == var20.itemID && var20.getMaxStackSize() > 1 && (!var17.getHasSubtypes() || var17.getItemDamage() == var20.getItemDamage()) /*&& ItemStack.areItemStackTagsEqual(var17, var20)*/) {
								var19 = var17.stackSize;
								if(var19 > 0 && var19 + var20.stackSize <= var20.getMaxStackSize()) {
									var20.stackSize += var19;
									var17 = var16.decrStackSize(var19);
									if(var17.stackSize == 0) {
										var16.putStack((ItemStack)null);
									}

									var16.onPickupFromSlot(entityPlayer, var6.getItemStack());
								}
							}
						}

						var16.onSlotChanged();
					}
				}
			} else if(shiftPressed == 2 && buttonPressed >= 0 && buttonPressed < 9) {
				var16 = (Slot)this.inventorySlots.get(slotNum);
				if(var16.canTakeStack(entityPlayer)) {
					var17 = var6.getStackInSlot(buttonPressed);
					boolean var18 = var17 == null || var16.inventory == var6 && var16.isItemValid(var17);
					var19 = -1;
					if(!var18) {
						var19 = var6.getFirstEmptyStack();
						var18 |= var19 > -1;
					}

					if(var16.getHasStack() && var18) {
						var22 = var16.getStack();
						var6.setInventorySlotContents(buttonPressed, var22.copy());
						if((var16.inventory != var6 || !var16.isItemValid(var17)) && var17 != null) {
							if(var19 > -1) {
								var6.addItemStackToInventory(var17);
								var16.decrStackSize(var22.stackSize);
								var16.putStack((ItemStack)null);
								var16.onPickupFromSlot(entityPlayer, var22);
							}
						} else {
							var16.decrStackSize(var22.stackSize);
							var16.putStack(var17);
							var16.onPickupFromSlot(entityPlayer, var22);
						}
					} else if(!var16.getHasStack() && var17 != null && var16.isItemValid(var17)) {
						var6.setInventorySlotContents(buttonPressed, (ItemStack)null);
						var16.putStack(var17);
					}
				}
			} else if(shiftPressed == 3 && entityPlayer.capabilities.isCreativeMode && var6.getItemStack() == null && slotNum >= 0) {
				var16 = (Slot)this.inventorySlots.get(slotNum);
				if(var16 != null && var16.getHasStack()) {
					var17 = var16.getStack().copy();
					var17.stackSize = var17.getMaxStackSize();
					var6.setItemStack(var17);
				}
			} else if(shiftPressed == 4 && var6.getItemStack() == null && slotNum >= 0) {
				var16 = (Slot)this.inventorySlots.get(slotNum);
				if(var16 != null && var16.getHasStack() && var16.canTakeStack(entityPlayer)) {
					var17 = var16.decrStackSize(buttonPressed == 0 ? 1 : var16.getStack().stackSize);
					var16.onPickupFromSlot(entityPlayer, var17);
					entityPlayer.dropPlayerItem(var17);
				}
			} else if(shiftPressed == 6 && slotNum >= 0) {
				var16 = (Slot)this.inventorySlots.get(slotNum);
				var17 = var6.getItemStack();
				if(var17 != null && (var16 == null || !var16.getHasStack() || !var16.canTakeStack(entityPlayer))) {
					var9 = buttonPressed == 0 ? 0 : this.inventorySlots.size() - 1;
					var19 = buttonPressed == 0 ? 1 : -1;

					for(int var21 = 0; var21 < 2; ++var21) {
						for(int var23 = var9; var23 >= 0 && var23 < this.inventorySlots.size() && var17.stackSize < var17.getMaxStackSize(); var23 += var19) {
							Slot var24 = (Slot)this.inventorySlots.get(var23);
							if(var24.getHasStack() && validDrag(var24, var17, true) && var24.canTakeStack(entityPlayer) && this.func_94530_a(var17, var24) && (var21 != 0 || var24.getStack().stackSize != var24.getStack().getMaxStackSize())) {
								int var14 = Math.min(var17.getMaxStackSize() - var17.stackSize, var24.getStack().stackSize);
								ItemStack var15 = var24.decrStackSize(var14);
								var17.stackSize += var14;
								if(var15.stackSize <= 0) {
									var24.putStack((ItemStack)null);
								}

								var24.onPickupFromSlot(entityPlayer, var15);
							}
						}
					}
				}

				this.updateCraftingResults();
			}
		}

		return var5;
	}
	
	public boolean func_94530_a(ItemStack var1, Slot var2) {
		return true;
	}

	protected void retrySlotClick(int i1, int i2, boolean z3, EntityPlayer entityPlayer4) {
		this.slotClick(i1, i2, 1, entityPlayer4);
	}

	public void onCraftGuiClosed(EntityPlayer entityPlayer1) {
		InventoryPlayer inventoryPlayer2 = entityPlayer1.inventory;
		if (inventoryPlayer2.getItemStack() != null) {
			entityPlayer1.dropPlayerItem(inventoryPlayer2.getItemStack());
			inventoryPlayer2.setItemStack((ItemStack) null);
		}

	}

	public void onCraftMatrixChanged(IInventory iInventory1) {
		this.updateCraftingResults();
	}

	public void putStackInSlot(int i1, ItemStack itemStack2) {
		this.getSlot(i1).putStack(itemStack2);
	}

	public void putStacksInSlots(ItemStack[] itemStack1) {
		for (int i2 = 0; i2 < itemStack1.length; ++i2) {
			this.getSlot(i2).putStack(itemStack1[i2]);
		}

	}

	public void updateProgressBar(int i1, int i2) {
	}

	public short getNextTransactionID(InventoryPlayer inventoryPlayer1) {
		++this.transactionID;
		return this.transactionID;
	}

	public void func_20113_a(short s1) {
	}

	public void func_20110_b(short s1) {
	}

	public boolean getCanCraft(EntityPlayer entityPlayer1) {
		return !this.cantCraftPlayers.contains(entityPlayer1);
	}

	public void setCanCraft(EntityPlayer entityPlayer1, boolean z2) {
		if (z2) {
			this.cantCraftPlayers.remove(entityPlayer1);
		} else {
			this.cantCraftPlayers.add(entityPlayer1);
		}

	}

	public abstract boolean canInteractWith(EntityPlayer entityPlayer1);

	protected boolean mergeItemStack(ItemStack itemStack1, int i2, int i3, boolean z4) {
		boolean z5 = false;
		int i6 = i2;
		if (z4) {
			i6 = i3 - 1;
		}

		Slot slot7;
		ItemStack itemStack8;
		if (itemStack1.isStackable()) {
			while (itemStack1.stackSize > 0 && (!z4 && i6 < i3 || z4 && i6 >= i2)) {
				slot7 = (Slot) this.inventorySlots.get(i6);
				itemStack8 = slot7.getStack();
				if (itemStack8 != null && itemStack8.itemID == itemStack1.itemID
						&& (!itemStack1.getHasSubtypes() || itemStack1.getItemDamage() == itemStack8.getItemDamage())
						&& ItemStack.areItemStacksFullyEqual(itemStack1, itemStack8)) {
					int i9 = itemStack8.stackSize + itemStack1.stackSize;
					if (i9 <= itemStack1.getMaxStackSize()) {
						itemStack1.stackSize = 0;
						itemStack8.stackSize = i9;
						slot7.onSlotChanged();
						z5 = true;
					} else if (itemStack8.stackSize < itemStack1.getMaxStackSize()) {
						itemStack1.stackSize -= itemStack1.getMaxStackSize() - itemStack8.stackSize;
						itemStack8.stackSize = itemStack1.getMaxStackSize();
						slot7.onSlotChanged();
						z5 = true;
					}
				}

				if (z4) {
					--i6;
				} else {
					++i6;
				}
			}
		}

		if (itemStack1.stackSize > 0) {
			if (z4) {
				i6 = i3 - 1;
			} else {
				i6 = i2;
			}

			while (!z4 && i6 < i3 || z4 && i6 >= i2) {
				slot7 = (Slot) this.inventorySlots.get(i6);
				itemStack8 = slot7.getStack();
				if (itemStack8 == null) {
					slot7.putStack(itemStack1.copy());
					slot7.onSlotChanged();
					itemStack1.stackSize = 0;
					z5 = true;
					break;
				}

				if (z4) {
					--i6;
				} else {
					++i6;
				}
			}
		}

		return z5;
	}
	
	public static int decodeButtonFromState(int var0) {
		return var0 >> 2 & 3;
	}

	public static int getStateFromEncodedButton(int var0) {
		return var0 & 3;
	}

	public static int encodeButtonAndState(int var0, int var1) {
		return var0 & 3 | (var1 & 3) << 2;
	}

	public static boolean isNormalClick(int var0) {
		return var0 == 0 || var0 == 1;
	}

	protected void clearMouseAction() {
		this.prevButtonState = 0;
		this.activeSlots.clear();
	}

	public static boolean validDrag(Slot theSlot, ItemStack theStack, boolean var2) {
		boolean slotEmpty = theSlot == null || !theSlot.getHasStack();
		
		if(theSlot != null && theSlot.getHasStack() && theStack != null && theStack.isItemEqual(theSlot.getStack()) /*&& ItemStack.areItemStackTagsEqual(theSlot.getStack(), theStack)*/) {
			slotEmpty |= theSlot.getStack().stackSize + (var2 ? 0 : theStack.stackSize) <= theStack.getMaxStackSize();
		}

		return slotEmpty;
	}

	public static void divideStackInSlots(Set<Slot> var0, int var1, ItemStack var2, int var3) {
		switch(var1) {
		case 0:
			var2.stackSize = MathHelper.floor_float((float)var2.stackSize / (float)var0.size());
			break;
		case 1:
			var2.stackSize = 1;
		}

		var2.stackSize += var3;
	}

	public boolean func_94531_b(Slot var1) {
		return true;
	}
}
