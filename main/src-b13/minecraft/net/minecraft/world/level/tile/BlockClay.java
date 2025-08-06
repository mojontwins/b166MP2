package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockClay extends Block {
	public BlockClay(int i1, int i2) {
		super(i1, i2, Material.clay);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Item.clay.shiftedIndex;
	}

	public int quantityDropped(Random random1) {
		return 4;
	}
}
