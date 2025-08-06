package net.minecraft.world.level.tile;

import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockNetherrack extends Block {
	public BlockNetherrack(int i1, int i2) {
		super(i1, i2, Material.rock);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}
}
