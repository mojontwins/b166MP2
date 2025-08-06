package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockStone extends Block implements IGround {
	public BlockStone(int i1, int i2) {
		super(i1, i2, Material.rock);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.cobblestone.blockID;
	}
}
