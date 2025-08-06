package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.creative.CreativeTabs;

public class BlockObsidian extends BlockStone {
	public BlockObsidian(int i1, int i2) {
		super(i1, i2);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int quantityDropped(Random random1) {
		return 1;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.obsidian.blockID;
	}
}
