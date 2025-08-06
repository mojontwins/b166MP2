package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.level.creative.CreativeTabs;

public class ItemKnife extends ItemSword {

	public ItemKnife(int i1, EnumToolMaterial enumToolMaterial2) {
		super(i1, enumToolMaterial2);
		this.weaponDamage = 4 + enumToolMaterial2.getDamageVsEntity() - 2;
		this.displayOnCreativeTab = CreativeTabs.tabCombat;
	}

	/*
	 * Add knock back when hitting an entity.
	 */
	public float getExtraKnockbackVsEntity(Entity entity) {
		return 0.0F;
	}

	/*
	 * Default swinging speed = 6, less is faster. 
	 */
	public int getSwingSpeed() {
		return 3;
	}
}
