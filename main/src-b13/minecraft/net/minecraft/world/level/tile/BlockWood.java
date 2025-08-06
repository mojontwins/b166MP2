package net.minecraft.world.level.tile;

import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockWood extends Block {
	public BlockWood(int i1) {
		super(i1, 4, Material.wood);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

}
