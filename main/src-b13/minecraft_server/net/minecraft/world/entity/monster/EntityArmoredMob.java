package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.inventory.InventoryMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntityArmoredMob extends EntityMob implements IArmoredMob {
	// Added a proper inventory to manage actual armor and stuff
	public InventoryMob inventory;
	protected int carryoverDamage = 0;
		
	public EntityArmoredMob(World var1) {
		super(var1);
		this.inventory = new InventoryMob(this, this.getSecondaryInventorySize()); 
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		NBTTagCompound var2 = var1.getCompoundTag("Inventory");
		this.inventory.readFromNBT(var2);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setTag("Inventory", this.inventory.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public IInventory getIInventory() {
		return this.inventory;
	}
	
	public void setInventory(IInventory inventory) {
		this.inventory = (InventoryMob) inventory;
	}
	
	@Override
	protected void damageEntity(DamageSource damageSource, int var1) {
		int var2 = 25 - this.inventory.getTotalArmorValue();
		int var3 = var1 * var2 + this.carryoverDamage;
		int strength = var3 / 25;
		this.carryoverDamage = var3 % 25;

		super.damageEntity(damageSource, strength);
	}
	
	public int getSecondaryInventorySize() {
		return 9;
	}

	public void setArmor(int type, ItemStack itemStack) {
		this.inventory.setArmorItemInSlot(3 - type, itemStack);
	}
	
	public ItemStack getArmor(int type) {
		return this.inventory.getArmorItemInSlot(3 - type);
	}
	
	@Override
	public ItemStack getHeldItem() {
		return this.inventory.getHeldItem();
	}
	
	@Override
	public boolean setHeldItem(ItemStack itemStack) {
		this.inventory.setHeldItem(itemStack);
		return true;
	}
	
	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
