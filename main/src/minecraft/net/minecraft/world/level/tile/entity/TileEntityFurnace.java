package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.FurnaceRecipes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFurnace;

public class TileEntityFurnace extends TileEntity implements IInventory {
	private ItemStack[] furnaceItemStacks = new ItemStack[3];
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;

	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	public ItemStack getStackInSlot(int i1) {
		return this.furnaceItemStacks[i1];
	}

	public ItemStack decrStackSize(int i1, int i2) {
		if(this.furnaceItemStacks[i1] != null) {
			ItemStack itemStack3;
			if(this.furnaceItemStacks[i1].stackSize <= i2) {
				itemStack3 = this.furnaceItemStacks[i1];
				this.furnaceItemStacks[i1] = null;
				return itemStack3;
			} else {
				itemStack3 = this.furnaceItemStacks[i1].splitStack(i2);
				if(this.furnaceItemStacks[i1].stackSize == 0) {
					this.furnaceItemStacks[i1] = null;
				}

				return itemStack3;
			}
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int i1) {
		if(this.furnaceItemStacks[i1] != null) {
			ItemStack itemStack2 = this.furnaceItemStacks[i1];
			this.furnaceItemStacks[i1] = null;
			return itemStack2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i1, ItemStack itemStack2) {
		this.furnaceItemStacks[i1] = itemStack2;
		if(itemStack2 != null && itemStack2.stackSize > this.getInventoryStackLimit()) {
			itemStack2.stackSize = this.getInventoryStackLimit();
		}

	}

	public String getInvName() {
		return "container.furnace";
	}

	public void readFromNBT(NBTTagCompound compoundTag) {
		super.readFromNBT(compoundTag);
		NBTTagList nBTTagList2 = compoundTag.getTagList("Items");
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

		for(int i3 = 0; i3 < nBTTagList2.tagCount(); ++i3) {
			NBTTagCompound nBTTagCompound4 = (NBTTagCompound)nBTTagList2.tagAt(i3);
			byte b5 = nBTTagCompound4.getByte("Slot");
			if(b5 >= 0 && b5 < this.furnaceItemStacks.length) {
				this.furnaceItemStacks[b5] = ItemStack.loadItemStackFromNBT(nBTTagCompound4);
			}
		}

		this.furnaceBurnTime = compoundTag.getShort("BurnTime");
		this.furnaceCookTime = compoundTag.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
	}

	public void writeToNBT(NBTTagCompound compoundTag) {
		super.writeToNBT(compoundTag);
		compoundTag.setShort("BurnTime", (short)this.furnaceBurnTime);
		compoundTag.setShort("CookTime", (short)this.furnaceCookTime);
		NBTTagList nBTTagList2 = new NBTTagList();

		for(int i3 = 0; i3 < this.furnaceItemStacks.length; ++i3) {
			if(this.furnaceItemStacks[i3] != null) {
				NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
				nBTTagCompound4.setByte("Slot", (byte)i3);
				this.furnaceItemStacks[i3].writeToNBT(nBTTagCompound4);
				nBTTagList2.appendTag(nBTTagCompound4);
			}
		}

		compoundTag.setTag("Items", nBTTagList2);
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public int getCookProgressScaled(int i1) {
		return this.furnaceCookTime * i1 / 200;
	}

	public int getBurnTimeRemainingScaled(int i1) {
		if(this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 200;
		}

		return this.furnaceBurnTime * i1 / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	public void updateEntity() {
		boolean z1 = this.furnaceBurnTime > 0;
		boolean z2 = false;
		if(this.furnaceBurnTime > 0) {
			--this.furnaceBurnTime;
		}

		if(!this.worldObj.isRemote) {
			if(this.furnaceBurnTime == 0 && this.canSmelt()) {
				this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
				if(this.furnaceBurnTime > 0) {
					z2 = true;
					if(this.furnaceItemStacks[1] != null) {
						--this.furnaceItemStacks[1].stackSize;
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
					z2 = true;
				}
			} else {
				this.furnaceCookTime = 0;
			}

			if(z1 != this.furnaceBurnTime > 0) {
				z2 = true;
				BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if(z2) {
			this.onInventoryChanged();
		}

	}

	private boolean canSmelt() {
		if(this.furnaceItemStacks[0] == null) {
			return false;
		} else {
			ItemStack itemStack1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
			return itemStack1 == null ? false : (this.furnaceItemStacks[2] == null ? true : (!this.furnaceItemStacks[2].isItemEqual(itemStack1) ? false : (this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit() && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize() ? true : this.furnaceItemStacks[2].stackSize < itemStack1.getMaxStackSize())));
		}
	}

	public void smeltItem() {
		if(this.canSmelt()) {
			ItemStack itemStack1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
			if(this.furnaceItemStacks[2] == null) {
				this.furnaceItemStacks[2] = itemStack1.copy();
			} else if(this.furnaceItemStacks[2].itemID == itemStack1.itemID) {
				++this.furnaceItemStacks[2].stackSize;
			}

			--this.furnaceItemStacks[0].stackSize;
			if(this.furnaceItemStacks[0].stackSize <= 0) {
				this.furnaceItemStacks[0] = null;
			}

		}
	}

	public static int getItemBurnTime(ItemStack itemStack0) {
		if(itemStack0 == null) {
			return 0;
		} else {
			/*
			int i1 = itemStack0.getItem().shiftedIndex;
			return i1 < 256 && Block.blocksList[i1].blockMaterial == Material.wood ? 300 : (i1 == Item.stick.shiftedIndex ? 100 : (i1 == Item.coal.shiftedIndex ? 1600 : (i1 == Item.bucketLava.shiftedIndex ? 20000 : (i1 == Block.sapling.blockID ? 100 : (i1 == Item.blazeRod.shiftedIndex ? 2400 : 0)))));
			*/
			int i2 = itemStack0.getItem().shiftedIndex;
			
			if(i2 == Block.sapling.blockID) return 100;
			//if(i2 == Block.deadBush.blockID) return 200;
			if(i2 < 256 && Block.blocksList[i2].blockMaterial == Material.wood) return 300;
			if(i2 == Item.stick.shiftedIndex) return 100;
			if(i2 == Item.coal.shiftedIndex) return 1600;
			if(i2 == Item.bucketLava.shiftedIndex) return 20000;
			if(i2 == Block.blockCoal.blockID) return 16000;
			//if(i2 == Block.driedKelpBlock.blockID) return 1200;
			return 0;
		}
	}

	public static boolean isItemFuel(ItemStack itemStack0) {
		return getItemBurnTime(itemStack0) > 0;
	}

	public boolean isUseableByPlayer(EntityPlayer entityPlayer1) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public void openChest() {
	}

	public void closeChest() {
	}
}
