package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.ISurface;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.IGround;

public class WorldGenSand extends WorldGenerator {
	protected int sandID;
	protected int radius;

	public WorldGenSand(int radius, int blockID) {
		this.sandID = blockID;
		this.radius = radius;
	}

	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		if(world.getBlockMaterial(x0, y0, z0) != Material.water) {
			return false;
		} else {
			int realRadius = rand.nextInt(this.radius - 2) + 2;
			byte vRadius = 2;

			for(int x = x0 - realRadius; x <= x0 + realRadius; ++x) {
				for(int z = z0 - realRadius; z <= z0 + realRadius; ++z) {
					int dx = x - x0;
					int dz = z - z0;
					if(dx * dx + dz * dz <= realRadius * realRadius) {
						for(int y = y0 - vRadius; y <= y0 + vRadius; ++y) {
							Block block = world.getBlock(x, y, z);
							if((block instanceof ISurface) || (block instanceof IGround)) {
								world.setBlock(x, y, z, this.sandID);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
