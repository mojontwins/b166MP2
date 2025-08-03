package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockGlowStone extends Block {
	public BlockGlowStone(int i1, int i2, Material material3) {
		super(i1, i2, material3);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	@Override
	public int quantityDroppedWithBonus(int i1, Random rand) {
		return MathHelper.clamp_int(this.quantityDropped(rand) + rand.nextInt(i1 + 1), 1, 4);
	}

	@Override
	public int quantityDropped(Random rand) {
		return 2 + rand.nextInt(3);
	}

	@Override
	public int idDropped(int i1, Random rand, int i3) {
		return Item.lightStoneDust.shiftedIndex;
	}
	
	@Override
	public boolean hasSolidTop(int meta) {
		return true;
	}
}
	