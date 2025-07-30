package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public final class ItemStack {
	public int stackSize;
	public int animationsToGo;
	public int itemID;
	public NBTTagCompound stackTagCompound;
	public int itemDamage;

	public ItemStack(Block block1) {
		this((Block)block1, 1);
	}
	
	public ItemStack(NBTTagCompound nbtTagCompound) {
		this.stackSize = 0;
		this.readFromNBT(nbtTagCompound);
	}

	public ItemStack(Block block1, int i2) {
		this(block1.blockID, i2, 0);
	}

	public ItemStack(Block block1, int i2, int i3) {
		this(block1.blockID, i2, i3);
	}

	public ItemStack(Item item1) {
		this(item1.shiftedIndex, 1, 0);
	}

	public ItemStack(Item item1, int i2) {
		this(item1.shiftedIndex, i2, 0);
	}

	public ItemStack(Item item1, int i2, int i3) {
		this(item1.shiftedIndex, i2, i3);
	}

	public ItemStack(int i1, int i2, int i3) {
		this.itemID = i1;
		this.stackSize = i2;
		this.itemDamage = i3;
	}

	public static ItemStack loadItemStackFromNBT(NBTTagCompound nBTTagCompound0) {
		ItemStack itemStack1 = new ItemStack();
		itemStack1.readFromNBT(nBTTagCompound0);
		return itemStack1.getItem() != null ? itemStack1 : null;
	}

	private ItemStack() {
		this.stackSize = 0;
	}

	public ItemStack splitStack(int i1) {
		ItemStack itemStack2 = new ItemStack(this.itemID, i1, this.itemDamage);
		if(this.stackTagCompound != null) {
			itemStack2.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
		}

		this.stackSize -= i1;
		return itemStack2;
	}

	public Item getItem() {
		return Item.itemsList[this.itemID];
	}

	public int getIconIndex() {
		return this.getItem().getIconIndex(this);
	}

	public boolean useItem(EntityPlayer entityPlayer, World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		boolean z7 = this.getItem().onItemUse(this, entityPlayer, world, x, y, z, face, xWithinFace, yWithinFace, zWithinFace, keyPressed);

		return z7;
	}

	public float getStrVsBlock(Block block1) {
		return this.getItem().getStrVsBlock(this, block1);
	}

	public ItemStack useItemRightClick(World world1, EntityPlayer entityPlayer2) {
		return this.getItem().onItemRightClick(this, world1, entityPlayer2);
	}

	public ItemStack onFoodEaten(World world1, EntityPlayer entityPlayer2) {
		return this.getItem().onFoodEaten(this, world1, entityPlayer2);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compoundTag) {
		compoundTag.setShort("id", (short)this.itemID);
		compoundTag.setByte("Count", (byte)this.stackSize);
		compoundTag.setShort("Damage", (short)this.itemDamage);
		if(this.stackTagCompound != null) {
			compoundTag.setTag("tag", this.stackTagCompound);
		}

		return compoundTag;
	}

	public void readFromNBT(NBTTagCompound compoundTag) {
		this.itemID = compoundTag.getShort("id");
		this.stackSize = compoundTag.getByte("Count");
		this.itemDamage = compoundTag.getShort("Damage");
		if(compoundTag.hasKey("tag")) {
			this.stackTagCompound = compoundTag.getCompoundTag("tag");
		}

	}

	public int getMaxStackSize() {
		return this.getItem().getItemStackLimit();
	}

	public boolean isStackable() {
		return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
	}

	public boolean isItemStackDamageable() {
		return Item.itemsList[this.itemID].getMaxDamage() > 0;
	}

	public boolean getHasSubtypes() {
		return Item.itemsList[this.itemID].getHasSubtypes();
	}

	public boolean isItemDamaged() {
		return this.isItemStackDamageable() && this.itemDamage > 0;
	}

	public int getItemDamageForDisplay() {
		return this.itemDamage;
	}

	public int getItemDamage() {
		return this.itemDamage;
	}

	public void setItemDamage(int i1) {
		this.itemDamage = i1;
	}

	public int getMaxDamage() {
		return Item.itemsList[this.itemID].getMaxDamage();
	}

	public void damageItem(int i1, EntityLiving entityLiving2) {
		if(this.isItemStackDamageable()) {
			
			this.itemDamage += i1;
			if(this.itemDamage > this.getMaxDamage()) {
				entityLiving2.renderBrokenItemStack(this);

				--this.stackSize;
				if(this.stackSize < 0) {
					this.stackSize = 0;
				}

				this.itemDamage = 0;
			}

		}
	}

	public void hitEntity(EntityLiving entityLiving1, EntityPlayer entityPlayer2) {
		Item.itemsList[this.itemID].hitEntity(this, entityLiving1, entityPlayer2);

	}

	public void onDestroyBlock(World world, int i1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		Item.itemsList[this.itemID].onBlockDestroyed(this, world, i1, i2, i3, i4, entityPlayer5);
		
	}

	public int getDamageVsEntity(Entity entity1) {
		return Item.itemsList[this.itemID].getDamageVsEntity(entity1);
	}
	
	public float getExtraKnockbackVsEntity(Entity entity1) {
		return Item.itemsList[this.itemID].getExtraKnockbackVsEntity(entity1);
	}
	
	public int getSwingSpeed() {
		return Item.itemsList[this.itemID].getSwingSpeed();
	}

	public boolean canHarvestBlock(Block block1) {
		return Item.itemsList[this.itemID].canHarvestBlock(block1);
	}

	public void onItemDestroyedByUse(EntityPlayer entityPlayer1) {
	}

	public void useItemOnEntity(EntityLiving entityLiving1) {
		Item.itemsList[this.itemID].useItemOnEntity(this, entityLiving1);
	}

	public ItemStack copy() {
		ItemStack itemStack1 = new ItemStack(this.itemID, this.stackSize, this.itemDamage);
		if(this.stackTagCompound != null) {
			itemStack1.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
			if(!itemStack1.stackTagCompound.equals(this.stackTagCompound)) {
				return itemStack1;
			}
		}

		return itemStack1;
	}

	public static boolean areItemStacksFullyEqual(ItemStack itemStack0, ItemStack itemStack1) {
		return itemStack0 == null && itemStack1 == null ? true : (itemStack0 != null && itemStack1 != null ? (itemStack0.stackTagCompound == null && itemStack1.stackTagCompound != null ? false : itemStack0.stackTagCompound == null || itemStack0.stackTagCompound.equals(itemStack1.stackTagCompound)) : false);
	}

	public static boolean areItemStacksEqual(ItemStack itemStack0, ItemStack itemStack1) {
		return itemStack0 == null && itemStack1 == null ? true : (itemStack0 != null && itemStack1 != null ? itemStack0.isItemStackEqual(itemStack1) : false);
	}

	private boolean isItemStackEqual(ItemStack itemStack1) {
		return this.stackSize != itemStack1.stackSize ? false : (this.itemID != itemStack1.itemID ? false : (this.itemDamage != itemStack1.itemDamage ? false : (this.stackTagCompound == null && itemStack1.stackTagCompound != null ? false : this.stackTagCompound == null || this.stackTagCompound.equals(itemStack1.stackTagCompound))));
	}

	public boolean isItemEqual(ItemStack itemStack1) {
		return this.itemID == itemStack1.itemID && this.itemDamage == itemStack1.itemDamage;
	}

	public String getItemName() {
		return Item.itemsList[this.itemID].getItemNameIS(this);
	}

	public static ItemStack copyItemStack(ItemStack itemStack0) {
		return itemStack0 == null ? null : itemStack0.copy();
	}

	public String toString() {
		return this.stackSize + "x" + Item.itemsList[this.itemID].getItemName() + "@" + this.itemDamage;
	}

	public void updateAnimation(World world1, Entity entity2, int i3, boolean z4) {
		if(this.animationsToGo > 0) {
			--this.animationsToGo;
		}

		Item.itemsList[this.itemID].onUpdate(this, world1, entity2, i3, z4);
	}

	public void onCrafting(World world1, EntityPlayer entityPlayer2, int i3) {
		Item.itemsList[this.itemID].onCreated(this, world1, entityPlayer2);
	}

	public boolean isStackEqual(ItemStack itemStack1) {
		return this.itemID == itemStack1.itemID && this.stackSize == itemStack1.stackSize && this.itemDamage == itemStack1.itemDamage;
	}

	public int getMaxItemUseDuration() {
		return this.getItem().getMaxItemUseDuration(this);
	}

	public EnumAction getItemUseAction() {
		return this.getItem().getItemUseAction(this);
	}

	public void onPlayerStoppedUsing(World world1, EntityPlayer entityPlayer2, int i3) {
		this.getItem().onPlayerStoppedUsing(this, world1, entityPlayer2, i3);
	}

	public boolean hasTagCompound() {
		return this.stackTagCompound != null;
	}

	public NBTTagCompound getTagCompound() {
		return this.stackTagCompound;
	}

	public NBTTagList getEnchantmentTagList() {
		return this.stackTagCompound == null ? null : (NBTTagList)this.stackTagCompound.getTag("ench");
	}

	public void setTagCompound(NBTTagCompound compoundTag) {
		this.stackTagCompound = compoundTag;
	}

	public List<String> getItemNameandInformation() {
		ArrayList<String> arrayList1 = new ArrayList<String>();
		Item item2 = Item.itemsList[this.itemID];
		arrayList1.add(item2.getItemDisplayName(this));
		item2.addInformation(this, arrayList1);
		if(this.hasTagCompound()) {
			NBTTagList nBTTagList3 = this.getEnchantmentTagList();
			if(nBTTagList3 != null) {
			
			}
		}

		return arrayList1;
	}

	public boolean hasEffect() {
		return this.getItem().hasEffect(this);
	}

	public EnumRarity getRarity() {
		return this.getItem().getRarity(this);
	}

	public boolean isItemEnchantable() {
		return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
	}

	public boolean isItemEnchanted() {
		return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench");
	}
}
