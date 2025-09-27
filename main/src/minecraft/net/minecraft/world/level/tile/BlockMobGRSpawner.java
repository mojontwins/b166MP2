package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityMobGRSpawner;

public class BlockMobGRSpawner extends BlockContainer {
	protected BlockMobGRSpawner(int i, int j) {
		super(i, j, Material.rock);
	}

	public TileEntity getBlockEntity() {
		return new TileEntityMobGRSpawner();
	}

	public int idDropped(int i, Random random) {
		return Block.cobblestone.blockID;
	}

	public int quantityDropped(Random random) {
		return 0;
	}

	public boolean isOpaqueCube() {
		return false;
	}
}
