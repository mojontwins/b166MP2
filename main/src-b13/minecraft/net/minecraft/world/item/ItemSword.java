package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemSword extends Item {
	protected int weaponDamage;
	protected final EnumToolMaterial toolMaterial;

	public ItemSword(int i1, EnumToolMaterial enumToolMaterial2) {
		super(i1);
		this.toolMaterial = enumToolMaterial2;
		this.maxStackSize = 1;
		this.setMaxDamage(enumToolMaterial2.getMaxUses());
		this.weaponDamage = 4 + enumToolMaterial2.getDamageVsEntity();
		
		this.displayOnCreativeTab = CreativeTabs.tabCombat;
	}

	public float getStrVsBlock(ItemStack itemStack1, Block block2) {
		return block2.blockID == Block.web.blockID ? 15.0F : 1.5F;
	}

	public boolean hitEntity(ItemStack itemStack1, EntityLiving entityLiving2, EntityLiving entityLiving3) {
		itemStack1.damageItem(1, entityLiving3);
		return true;
	}

	public boolean onBlockDestroyed(ItemStack itemStack1, int i2, int i3, int i4, int i5, EntityLiving entityLiving6) {
		itemStack1.damageItem(2, entityLiving6);
		return true;
	}

	public int getDamageVsEntity(Entity entity1) {
		return this.weaponDamage;
	}

	public boolean isFull3D() {
		return true;
	}

	public EnumAction getItemUseAction(ItemStack itemStack1) {
		return EnumAction.block;
	}

	public int getMaxItemUseDuration(ItemStack itemStack1) {
		return 72000;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		entityPlayer3.setItemInUse(itemStack1, this.getMaxItemUseDuration(itemStack1));
		return itemStack1;
	}

	public boolean canHarvestBlock(Block block1) {
		return block1.blockID == Block.web.blockID;
	}

	public int getItemEnchantability() {
		return this.toolMaterial.getEnchantability();
	}
}
