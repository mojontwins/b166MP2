package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

public class TileEntityChest extends TileEntity implements IInventory {
	private ItemStack[] chestContents = new ItemStack[36];
	public boolean adjacentChestChecked = false;
	public TileEntityChest adjacentChestZNeg;
	public TileEntityChest adjacentChestXPos;
	public TileEntityChest adjacentChestXNeg;
	public TileEntityChest adjacentChestZPos;
	public float lidAngle;
	public float prevLidAngle;
	public int numUsingPlayers;
	private int ticksSinceSync;

	public int getSizeInventory() {
		return 27;
	}

	public ItemStack getStackInSlot(int i1) {
		return this.chestContents[i1];
	}

	public ItemStack decrStackSize(int i1, int i2) {
		if(this.chestContents[i1] != null) {
			ItemStack itemStack3;
			if(this.chestContents[i1].stackSize <= i2) {
				itemStack3 = this.chestContents[i1];
				this.chestContents[i1] = null;
				this.onInventoryChanged();
				return itemStack3;
			} else {
				itemStack3 = this.chestContents[i1].splitStack(i2);
				if(this.chestContents[i1].stackSize == 0) {
					this.chestContents[i1] = null;
				}

				this.onInventoryChanged();
				return itemStack3;
			}
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int i1) {
		if(this.chestContents[i1] != null) {
			ItemStack itemStack2 = this.chestContents[i1];
			this.chestContents[i1] = null;
			return itemStack2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i1, ItemStack itemStack2) {
		this.chestContents[i1] = itemStack2;
		if(itemStack2 != null && itemStack2.stackSize > this.getInventoryStackLimit()) {
			itemStack2.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public String getInvName() {
		return "container.chest";
	}

	public void readFromNBT(NBTTagCompound compoundTag) {
		super.readFromNBT(compoundTag);
		NBTTagList nBTTagList2 = compoundTag.getTagList("Items");
		this.chestContents = new ItemStack[this.getSizeInventory()];

		for(int i3 = 0; i3 < nBTTagList2.tagCount(); ++i3) {
			NBTTagCompound nBTTagCompound4 = (NBTTagCompound)nBTTagList2.tagAt(i3);
			int i5 = nBTTagCompound4.getByte("Slot") & 255;
			if(i5 >= 0 && i5 < this.chestContents.length) {
				this.chestContents[i5] = ItemStack.loadItemStackFromNBT(nBTTagCompound4);
			}
		}

	}

	public void writeToNBT(NBTTagCompound compoundTag) {
		super.writeToNBT(compoundTag);
		NBTTagList nBTTagList2 = new NBTTagList();

		for(int i3 = 0; i3 < this.chestContents.length; ++i3) {
			if(this.chestContents[i3] != null) {
				NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
				nBTTagCompound4.setByte("Slot", (byte)i3);
				this.chestContents[i3].writeToNBT(nBTTagCompound4);
				nBTTagList2.appendTag(nBTTagCompound4);
			}
		}

		compoundTag.setTag("Items", nBTTagList2);
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isUseableByPlayer(EntityPlayer entityPlayer1) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		this.adjacentChestChecked = false;
	}

	public void checkForAdjacentChests() {
		if(!this.adjacentChestChecked) {
			this.adjacentChestChecked = true;
			this.adjacentChestZNeg = null;
			this.adjacentChestXPos = null;
			this.adjacentChestXNeg = null;
			this.adjacentChestZPos = null;
			if(this.worldObj.getBlockId(this.xCoord - 1, this.yCoord, this.zCoord) == Block.chest.blockID) {
				this.adjacentChestXNeg = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
			}

			if(this.worldObj.getBlockId(this.xCoord + 1, this.yCoord, this.zCoord) == Block.chest.blockID) {
				this.adjacentChestXPos = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
			}

			if(this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord - 1) == Block.chest.blockID) {
				this.adjacentChestZNeg = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
			}

			if(this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord + 1) == Block.chest.blockID) {
				this.adjacentChestZPos = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
			}

			if(this.adjacentChestZNeg != null) {
				this.adjacentChestZNeg.updateContainingBlockInfo();
			}

			if(this.adjacentChestZPos != null) {
				this.adjacentChestZPos.updateContainingBlockInfo();
			}

			if(this.adjacentChestXPos != null) {
				this.adjacentChestXPos.updateContainingBlockInfo();
			}

			if(this.adjacentChestXNeg != null) {
				this.adjacentChestXNeg.updateContainingBlockInfo();
			}

		}
	}

	public void updateEntity() {
		super.updateEntity();
		this.checkForAdjacentChests();
		if(++this.ticksSinceSync % 20 * 4 == 0) {
			this.worldObj.playNoteAt(this.xCoord, this.yCoord, this.zCoord, 1, this.numUsingPlayers);
		}

		this.prevLidAngle = this.lidAngle;
		float f1 = 0.1F;
		double d4;
		if(this.numUsingPlayers > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
			double d2 = (double)this.xCoord + 0.5D;
			d4 = (double)this.zCoord + 0.5D;
			if(this.adjacentChestZPos != null) {
				d4 += 0.5D;
			}

			if(this.adjacentChestXPos != null) {
				d2 += 0.5D;
			}

			this.worldObj.playSoundEffect(d2, (double)this.yCoord + 0.5D, d4, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if(this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
			float f8 = this.lidAngle;
			if(this.numUsingPlayers > 0) {
				this.lidAngle += f1;
			} else {
				this.lidAngle -= f1;
			}

			if(this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			float f3 = 0.5F;
			if(this.lidAngle < f3 && f8 >= f3 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
				d4 = (double)this.xCoord + 0.5D;
				double d6 = (double)this.zCoord + 0.5D;
				if(this.adjacentChestZPos != null) {
					d6 += 0.5D;
				}

				if(this.adjacentChestXPos != null) {
					d4 += 0.5D;
				}

				this.worldObj.playSoundEffect(d4, (double)this.yCoord + 0.5D, d6, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if(this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}

	}

	public void onTileEntityPowered(int i1, int i2) {
		if(i1 == 1) {
			this.numUsingPlayers = i2;
		}

	}

	public void openChest() {
		++this.numUsingPlayers;
		this.worldObj.playNoteAt(this.xCoord, this.yCoord, this.zCoord, 1, this.numUsingPlayers);
	}

	public void closeChest() {
		--this.numUsingPlayers;
		this.worldObj.playNoteAt(this.xCoord, this.yCoord, this.zCoord, 1, this.numUsingPlayers);
	}

	public void invalidate() {
		this.updateContainingBlockInfo();
		this.checkForAdjacentChests();
		super.invalidate();
	}
}
