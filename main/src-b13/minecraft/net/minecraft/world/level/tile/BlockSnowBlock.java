package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockSnowBlock extends Block {
	protected BlockSnowBlock(int i1, int i2) {
		super(i1, i2, Material.craftedSnow);
		this.setTickRandomly(true);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Item.snowball.shiftedIndex;
	}

	public int quantityDropped(Random random1) {
		return 4;
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(world1.getSavedLightValue(EnumSkyBlock.Block, i2, i3, i4) > 11) {
			this.dropBlockAsItem(world1, i2, i3, i4, world1.getBlockMetadata(i2, i3, i4), 0);
			world1.setBlockWithNotify(i2, i3, i4, 0);
		}

	}
}
