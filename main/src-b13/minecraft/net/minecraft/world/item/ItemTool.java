package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemTool extends Item {
	private Block[] blocksEffectiveAgainst;
	protected float efficiencyOnProperMaterial = 4.0F;
	private int damageVsEntity;
	public EnumToolMaterial toolMaterial;

	protected ItemTool(int i1, int damageModifier, EnumToolMaterial enumToolMaterial3, Block[] block4) {
		super(i1);
		this.toolMaterial = enumToolMaterial3;
		this.blocksEffectiveAgainst = block4;
		this.maxStackSize = 1;
		this.setMaxDamage(enumToolMaterial3.getMaxUses());
		this.efficiencyOnProperMaterial = enumToolMaterial3.getEfficiencyOnProperMaterial();
		this.damageVsEntity = damageModifier + enumToolMaterial3.getDamageVsEntity();
		
		this.displayOnCreativeTab = CreativeTabs.tabTools;
	}

	public float getStrVsBlock(ItemStack itemStack1, Block block2) {
		for(int i3 = 0; i3 < this.blocksEffectiveAgainst.length; ++i3) {
			if(this.blocksEffectiveAgainst[i3] == block2) {
				return this.efficiencyOnProperMaterial;
			}
		}

		return 1.0F;
	}

	public boolean hitEntity(ItemStack itemStack1, EntityLiving entityLiving2, EntityLiving entityLiving3) {
		itemStack1.damageItem(2, entityLiving3);
		return true;
	}

	public boolean onBlockDestroyed(ItemStack itemStack1, int i2, int i3, int i4, int i5, EntityLiving entityLiving6) {
		itemStack1.damageItem(1, entityLiving6);
		return true;
	}

	public int getDamageVsEntity(Entity entity1) {
		return this.damageVsEntity;
	}

	public boolean isFull3D() {
		return true;
	}

	public int getItemEnchantability() {
		return this.toolMaterial.getEnchantability();
	}
}
