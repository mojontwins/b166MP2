package net.minecraft.world.item;

import net.minecraft.world.GameRules;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;

public class ItemBow extends Item {
	public ItemBow(int i1) {
		super(i1);
		this.maxStackSize = 1;
		this.setMaxDamage(384);
		
		this.displayOnCreativeTab = CreativeTabs.tabCombat;
	}

	public void onPlayerStoppedUsing(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3, int i4) {
		if (!GameRules.boolRule("classicBow")) {
			boolean z5 = entityPlayer3.capabilities.isCreativeMode;
			if(z5 || entityPlayer3.inventory.hasItem(Item.arrow.shiftedIndex)) {
				int i6 = this.getMaxItemUseDuration(itemStack1) - i4;
				float f7 = (float)i6 / 20.0F;
				f7 = (f7 * f7 + f7 * 2.0F) / 3.0F;
				if((double)f7 < 0.1D) {
					return;
				}
	
				if(f7 > 1.0F) {
					f7 = 1.0F;
				}
	
				EntityArrow entityArrow8 = new EntityArrow(world2, entityPlayer3, f7 * 2.0F);
				if(f7 == 1.0F) {
					entityArrow8.arrowCritical = true;
				}
				
				itemStack1.damageItem(1, entityPlayer3);
				world2.playSoundAtEntity(entityPlayer3, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f7 * 0.5F);
				if(!z5) {
					entityPlayer3.inventory.consumeInventoryItem(Item.arrow.shiftedIndex);
				} else {
					entityArrow8.doesArrowBelongToPlayer = false;
				}
	
				if(!world2.isRemote) {
					world2.spawnEntityInWorld(entityArrow8);
				}
			}
		}
	}

	public ItemStack onFoodEaten(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		return itemStack1;
	}

	public int getMaxItemUseDuration(ItemStack itemStack1) {
		if (!GameRules.boolRule("classicBow")) {
			return 72000;
		} else {
			return 0;
		}
	}

	public EnumAction getItemUseAction(ItemStack itemStack1) {
		if (!GameRules.boolRule("classicBow")) {
			return EnumAction.bow;
		} else {
			return EnumAction.none;
		}
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		if(entityPlayer.capabilities.isCreativeMode || entityPlayer.inventory.hasItem(Item.arrow.shiftedIndex)) {
			if (!GameRules.boolRule("classicBow")) {
				entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
			} else {
				world.playSoundAtEntity(entityPlayer, "random.bow", 1.0F, 1.0F / (Item.itemRand.nextFloat() * 0.4F + 0.8F));
				if(!world.isRemote) {
					world.spawnEntityInWorld(new EntityArrow(world, entityPlayer, 1.0F));
				}
			}
		}

		return itemStack;
	}

	public int getItemEnchantability() {
		return 1;
	}
}
