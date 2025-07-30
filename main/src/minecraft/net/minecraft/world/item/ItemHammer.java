package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class ItemHammer extends ItemPickaxe {

	public ItemHammer(int i1, EnumToolMaterial enumToolMaterial2) {
		super(i1, 6, enumToolMaterial2);
		this.displayOnCreativeTab = CreativeTabs.tabCombat;
	}
	
	/*
	 * Add knock back when hitting an entity.
	 */
	public float getExtraKnockbackVsEntity(Entity entity) {
		return 1.0F;
	}

	/*
	 * Default swinging speed = 6, less is faster. 
	 */
	public int getSwingSpeed() {
		return 20;
	}
	
	public float getStrVsBlock(ItemStack itemStack1, Block block2) {
		return 1.5F * ( block2 == null || block2.blockMaterial != Material.iron && block2.blockMaterial != Material.rock ? super.getStrVsBlock(itemStack1, block2) : this.efficiencyOnProperMaterial );
	}
}
