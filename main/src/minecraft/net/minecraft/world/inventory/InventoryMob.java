package net.minecraft.world.inventory;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;

public class InventoryMob implements IInventory {
	private static final int sizeSecondaryInventory = 9;
	private ItemStack[] mainInventory = new ItemStack[1];
	private ItemStack[] armorInventory = new ItemStack[4];
	private ItemStack[] secondaryInventory;
	
	public boolean hasChanged = false;
	
	public EntityLiving entity = null;
	
	public InventoryMob(EntityLiving entity) {
		this(entity, sizeSecondaryInventory);
	}
	
	public InventoryMob(EntityLiving entity, int inventorySize) {
		this.entity = entity;
		this.secondaryInventory = new ItemStack[inventorySize];
	}
	
	@Override
	public int getSizeInventory() {
		return this.mainInventory.length + this.armorInventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slotN) {
		ItemStack[] subInventory = this.mainInventory;
		if(slotN >= subInventory.length) {
			slotN -= subInventory.length;
			subInventory = this.armorInventory;
			if(slotN >= subInventory.length) {
				slotN -= subInventory.length;
				subInventory = this.secondaryInventory;
			}
		}
		
		return subInventory[slotN];
	}

	public ItemStack armorItemInSlot(int slotN) {
		return this.armorInventory[slotN];
	}
	
	public ItemStack getCurrentItem() {
		return this.getHeldItem();
	}
	
	@Override
	public ItemStack decrStackSize(int slotN, int amount) {
		ItemStack[] subInventory = this.mainInventory;
		if(slotN >= subInventory.length) {
			slotN -= subInventory.length;
			subInventory = this.armorInventory;
			if(slotN >= subInventory.length) {
				slotN -= subInventory.length;
				subInventory = this.secondaryInventory;
			}
		}
		
		ItemStack itemStack = null;
		
		if (subInventory[slotN] != null) {
			if (subInventory[slotN].stackSize <= amount) {
				itemStack = subInventory[slotN];
				subInventory[slotN] = null;
			} else {
				itemStack = subInventory[slotN].splitStack(amount);
				if (subInventory[slotN].stackSize == 0) {
					subInventory[slotN] = null;
				}
			}
		}
		
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slotN, ItemStack itemStack) {
		ItemStack[] subInventory = this.mainInventory;
		if(slotN >= subInventory.length) {
			slotN -= subInventory.length;
			subInventory = this.armorInventory;
			if(slotN >= subInventory.length) {
				slotN -= subInventory.length;
				subInventory = this.secondaryInventory;
			}
		}
		
		subInventory[slotN] = itemStack;
	}
	
	public void setSecondaryInventorySlotContents(int slotN, ItemStack itemStack) {
		this.secondaryInventory[slotN] = itemStack;
	}
	
	public ItemStack getSecondaryInventorySlotContents(int slotN) {
		return this.secondaryInventory[slotN];
	}

	@Override
	public String getInvName() {
		return "Mob inventory";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {
		this.hasChanged = true;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound inventoryCompound /*NBTTagList nbtTagList*/) {
		NBTTagCompound nbtTagCompound;
		NBTTagList nbtTagList = new NBTTagList();
		
		inventoryCompound.setByte("SecondaryInventorySize", (byte) this.secondaryInventory.length);
		
		for(int i = 0; i < this.mainInventory.length; ++i) {
			if(this.mainInventory[i] != null) {
				nbtTagCompound = new NBTTagCompound();
				nbtTagCompound.setByte("Slot", (byte)i);
				this.mainInventory[i].writeToNBT(nbtTagCompound);
				nbtTagList.appendTag(nbtTagCompound);
			}
		}

		for(int i = 0; i < this.armorInventory.length; ++i) {
			if(this.armorInventory[i] != null) {
				nbtTagCompound = new NBTTagCompound();
				nbtTagCompound.setByte("Slot", (byte)(i + 100));
				this.armorInventory[i].writeToNBT(nbtTagCompound);
				nbtTagList.appendTag(nbtTagCompound);
			}
		}
		
		for(int i = 0; i < this.secondaryInventory.length; ++i) { 
			if(this.secondaryInventory[i] != null) { 
				nbtTagCompound = new NBTTagCompound();
				nbtTagCompound.setByte("Slot", (byte)(i + 200));
				this.secondaryInventory[i].writeToNBT(nbtTagCompound);
				nbtTagList.appendTag(nbtTagCompound);
			}
		}
		
		inventoryCompound.setTag("InventoryList", nbtTagList);

		return inventoryCompound;
	}
	
	public void readFromNBT(NBTTagCompound inventoryCompound /*NBTTagList nbtTagList*/) {
		byte secondaryInventorySize = inventoryCompound.getByte("SecondaryInventorySize");
		
		this.mainInventory = new ItemStack[this.mainInventory.length];
		this.armorInventory = new ItemStack[this.armorInventory.length];
		this.secondaryInventory = new ItemStack[secondaryInventorySize];
		
		NBTTagList nbtTagList = inventoryCompound.getTagList("InventoryList");
		
		for(int i = 0; i < nbtTagList.tagCount(); ++i) {
			NBTTagCompound nbtTagCompound = (NBTTagCompound)nbtTagList.tagAt(i);
			int slotN = nbtTagCompound.getByte("Slot") & 255;
						
			if(slotN >= 0 && slotN < this.mainInventory.length) {
				this.mainInventory[slotN] = new ItemStack(nbtTagCompound);
			}

			if(slotN >= 100 && slotN < this.armorInventory.length + 100) {
				this.armorInventory[slotN - 100] = new ItemStack(nbtTagCompound);
			}
			
			if(slotN >= 200 && slotN < secondaryInventorySize + 200) {
				this.secondaryInventory[slotN - 200] = new ItemStack(nbtTagCompound);				
			}
		}
	}

	public ItemStack getArmorItemInSlot(int slotN) {
		return this.armorInventory[slotN];
	}
	
	public String toString() {
		String s = "InventoryMob : \n";
		for(int i = 0; i < 4; i ++) {
			s += this.armorInventory[i] + "\n";
		}
		return s + this.mainInventory[0];
	}
	
	public void setArmorItemInSlot(int slotN, ItemStack armorItem) {
		this.armorInventory[slotN] = armorItem;
	}
	
	public ItemStack getCurrentMainItem() {
		return this.mainInventory[0];
	}

	public int getTotalArmorValue() {
		int var1 = 0;
		int var2 = 0;
		int var3 = 0;

		for(int var4 = 0; var4 < this.armorInventory.length; ++var4) {
			if(this.armorInventory[var4] != null && this.armorInventory[var4].getItem() instanceof ItemArmor) {
				int var5 = this.armorInventory[var4].getMaxDamage();
				int var6 = this.armorInventory[var4].itemDamage;
				int var7 = var5 - var6;
				var2 += var7;
				var3 += var5;
				int var8 = ((ItemArmor)this.armorInventory[var4].getItem()).damageReduceAmount;
				var1 += var8;
			}
		}

		if(var3 == 0) {
			return 0;
		} else {
			return (var1 - 1) * var2 / var3 + 1;
		}
	}

	public ItemStack getHeldItem() {
		return this.mainInventory[0];
	}
	
	public void setHeldItem(ItemStack itemStack) {
		this.mainInventory[0] = itemStack;
	}
	
	public boolean canInteractWith(EntityPlayer entityPlayer1) {
		return false;
	}
	
	private int getInventorySlotContainItem(int i1) {
		for(int i2 = 0; i2 < this.secondaryInventory.length; ++i2) {
			if(this.secondaryInventory[i2] != null && this.secondaryInventory[i2].itemID == i1) {
				return i2;
			}
		}

		return -1;
	}
	
	public boolean consumeInventoryItem(int i1) {
		int i2 = this.getInventorySlotContainItem(i1);
		if(i2 < 0) {
			return false;
		} else {
			if(--this.secondaryInventory[i2].stackSize <= 0) {
				this.secondaryInventory[i2] = null;
			}

			return true;
		}
	}

	public int getSizesecondaryinventory() {
		return this.secondaryInventory.length;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub
		
	}
}
