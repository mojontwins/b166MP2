package net.minecraft.src;

public class TileEntityFurnace extends TileEntity implements IInventory {
	private ItemStack[] furnaceItemStacks = new ItemStack[3];
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;

	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	public ItemStack getStackInSlot(int paramInt) {
		return this.furnaceItemStacks[paramInt];
	}

	public ItemStack decrStackSize(int paramInt1, int paramInt2) {
		if(this.furnaceItemStacks[paramInt1] != null) {
			ItemStack localiw;
			if(this.furnaceItemStacks[paramInt1].stackSize <= paramInt2) {
				localiw = this.furnaceItemStacks[paramInt1];
				this.furnaceItemStacks[paramInt1] = null;
				return localiw;
			} else {
				localiw = this.furnaceItemStacks[paramInt1].splitStack(paramInt2);
				if(this.furnaceItemStacks[paramInt1].stackSize == 0) {
					this.furnaceItemStacks[paramInt1] = null;
				}

				return localiw;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int paramInt, ItemStack paramiw) {
		this.furnaceItemStacks[paramInt] = paramiw;
		if(paramiw != null && paramiw.stackSize > this.getInventoryStackLimit()) {
			paramiw.stackSize = this.getInventoryStackLimit();
		}

	}

	public String getInvName() {
		return "Furnace";
	}

	public void readFromNBT(NBTTagCompound paramnq) {
		super.readFromNBT(paramnq);
		NBTTagList localsk = paramnq.getTagList("Items");
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < localsk.tagCount(); ++i) {
			NBTTagCompound localnq = (NBTTagCompound)localsk.tagAt(i);
			byte j = localnq.getByte("Slot");
			if(j >= 0 && j < this.furnaceItemStacks.length) {
				this.furnaceItemStacks[j] = new ItemStack(localnq);
			}
		}

		this.furnaceBurnTime = paramnq.getShort("BurnTime");
		this.furnaceCookTime = paramnq.getShort("CookTime");
		this.currentItemBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
	}

	public void writeToNBT(NBTTagCompound paramnq) {
		super.writeToNBT(paramnq);
		paramnq.setShort("BurnTime", (short)this.furnaceBurnTime);
		paramnq.setShort("CookTime", (short)this.furnaceCookTime);
		NBTTagList localsk = new NBTTagList();

		for(int i = 0; i < this.furnaceItemStacks.length; ++i) {
			if(this.furnaceItemStacks[i] != null) {
				NBTTagCompound localnq = new NBTTagCompound();
				localnq.setByte("Slot", (byte)i);
				this.furnaceItemStacks[i].writeToNBT(localnq);
				localsk.setTag(localnq);
			}
		}

		paramnq.setTag("Items", localsk);
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public int getCookProgressScaled(int paramInt) {
		return this.furnaceCookTime * paramInt / 200;
	}

	public int getBurnTimeRemainingScaled(int paramInt) {
		if(this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 200;
		}

		return this.furnaceBurnTime * paramInt / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	public void updateEntity() {
		boolean i = this.furnaceBurnTime > 0;
		boolean j = false;
		if(this.furnaceBurnTime > 0) {
			--this.furnaceBurnTime;
		}

		if(!this.worldObj.multiplayerWorld) {
			if(this.furnaceBurnTime == 0 && this.canSmelt()) {
				this.currentItemBurnTime = this.furnaceBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
				if(this.furnaceBurnTime > 0) {
					j = true;
					if(this.furnaceItemStacks[1] != null) {
						if(this.furnaceItemStacks[1].getItem().hasContainerItem()) {
							this.furnaceItemStacks[1] = new ItemStack(this.furnaceItemStacks[1].getItem().getContainerItem());
						} else {
							--this.furnaceItemStacks[1].stackSize;
						}

						if(this.furnaceItemStacks[1].stackSize == 0) {
							this.furnaceItemStacks[1] = null;
						}
					}
				}
			}

			if(this.isBurning() && this.canSmelt()) {
				++this.furnaceCookTime;
				if(this.furnaceCookTime == 200) {
					this.furnaceCookTime = 0;
					this.smeltItem();
					j = true;
				}
			} else {
				this.furnaceCookTime = 0;
			}

			if(i != this.furnaceBurnTime > 0) {
				j = true;
				BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if(j) {
			this.onInventoryChanged();
		}

	}

	private boolean canSmelt() {
		if(this.furnaceItemStacks[0] == null) {
			return false;
		} else {
			ItemStack localiw = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
			return localiw == null ? false : (this.furnaceItemStacks[2] == null ? true : (!this.furnaceItemStacks[2].isItemEqual(localiw) ? false : (this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit() && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize() ? true : this.furnaceItemStacks[2].stackSize < localiw.getMaxStackSize())));
		}
	}

	public void smeltItem() {
		if(this.canSmelt()) {
			ItemStack localiw = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
			if(this.furnaceItemStacks[2] == null) {
				this.furnaceItemStacks[2] = localiw.copy();
			} else if(this.furnaceItemStacks[2].itemID == localiw.itemID) {
				this.furnaceItemStacks[2].stackSize += localiw.stackSize;
			}

			if(this.furnaceItemStacks[0].getItem().hasContainerItem()) {
				this.furnaceItemStacks[0] = new ItemStack(this.furnaceItemStacks[0].getItem().getContainerItem());
			} else {
				--this.furnaceItemStacks[0].stackSize;
			}

			if(this.furnaceItemStacks[0].stackSize <= 0) {
				this.furnaceItemStacks[0] = null;
			}

		}
	}

	private int getItemBurnTime(ItemStack paramiw) {
		if(paramiw == null) {
			return 0;
		} else {
			int i = paramiw.getItem().shiftedIndex;
			return i < 256 && Block.blocksList[i].blockMaterial == Material.wood ? 300 : (i == Item.stick.shiftedIndex ? 100 : (i == Item.coal.shiftedIndex ? 1600 : (i == Item.bucketLava.shiftedIndex ? 20000 : (i == Block.sapling.blockID ? 100 : ModLoader.AddAllFuel(i)))));
		}
	}

	public boolean canInteractWith(EntityPlayer paramgq) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : paramgq.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
}
