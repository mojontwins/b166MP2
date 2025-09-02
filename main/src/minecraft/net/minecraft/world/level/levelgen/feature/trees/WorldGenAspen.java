package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.World;

public class WorldGenAspen extends WorldGenMojon {

	// Original code by smez1234

	EnumTreeType tree = EnumTreeType.ASPEN;

	private final int leavesID = tree.leaves.getBlock().blockID;
	private final int leavesMeta = tree.leaves.getMetadata();
	private final int trunkID = tree.wood.getBlock().blockID;
	private final int trunkMeta = tree.wood.getMetadata();

	int height;

	public WorldGenAspen(int height) {
		super(true);
		this.height = height;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if(!this.validGround(world, x, y - 1, z)) return false;

		for(int i = 0; i < this.height; i++) {
			if(!world.isAirBlock(x, y + i, z)) return false;
		}

		this.ellipsoid(world, rand, x, y + height - 4, z, 3, 1.75F, new BlockState(this.leavesID, this.leavesMeta));

		for(int i = 0; i < height; i++) {
			this.setBlockAndMetadata(world, x, y + i, z, this.trunkID, this.trunkMeta);
		}

		return true;
	}
}
