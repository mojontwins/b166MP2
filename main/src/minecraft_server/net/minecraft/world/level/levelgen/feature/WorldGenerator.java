package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public abstract class WorldGenerator {
	private final boolean doBlockNotify;

	public WorldGenerator() {
		this.doBlockNotify = false;
	}

	public WorldGenerator(boolean z1) {
		this.doBlockNotify = z1;
	}

	public abstract boolean generate(World world1, Random random2, int i3, int i4, int i5);

	public void setScale(double d1, double d3, double d5) {
	}

	protected void setBlock(World world1, int i2, int i3, int i4, int i5) {
		this.setBlockAndMetadata(world1, i2, i3, i4, i5, 0);
	}

	protected void setBlockAndMetadata(World world1, int i2, int i3, int i4, int i5, int i6) {
		if (this.doBlockNotify) {
			world1.setBlockAndMetadataWithNotify(i2, i3, i4, i5, i6);
		} else if (world1.blockExists(i2, i3, i4) && world1.getChunkFromBlockCoords(i2, i4).field_50120_o) {
			if (world1.setBlockAndMetadata(i2, i3, i4, i5, i6)) {
				world1.markBlockNeedsUpdate(i2, i3, i4);
			}
		} else {
			world1.setBlockAndMetadata(i2, i3, i4, i5, i6);
		}

	}

	protected void setBlockIfEmpty(int x, int y, int z, int blockID, int metadata, int what, World world) {
		if (world.isAirBlock(x, y, z)) {
			this.setBlockAndMetadata(world, x, y, z, blockID, metadata);
		}
	}

	protected boolean canBeReplacedByLeaves(Block block) {
		return block == null || block.blockMaterial == Material.air || block.blockMaterial == Material.leaves || block.blockID == Block.grass.blockID
				|| block.blockID == Block.dirt.blockID || block.blockID == Block.wood.blockID || block.blockID == Block.sapling.blockID
				/*|| block.blockID == Block.vine.blockID*/
				;
	}
}
