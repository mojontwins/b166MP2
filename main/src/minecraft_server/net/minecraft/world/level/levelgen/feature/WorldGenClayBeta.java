package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class WorldGenClayBeta extends WorldGenerator {
	private int clayBlockId = Block.blockClay.blockID;
	private int numberOfBlocks;

	public WorldGenClayBeta(int i1) {
		this.numberOfBlocks = i1;
	}

	public boolean generate(World world, Random random2, int x0, int y0, int z0) {
		if(world.getBlockMaterial(x0, y0, z0) != Material.water) {
			return false;
		} else {
			float f6 = random2.nextFloat() * (float)Math.PI;
			double x1d = (double)((float)(x0 + 8) + MathHelper.sin(f6) * (float)this.numberOfBlocks / 8.0F);
			double x2d = (double)((float)(x0 + 8) - MathHelper.sin(f6) * (float)this.numberOfBlocks / 8.0F);
			double z1d = (double)((float)(z0 + 8) + MathHelper.cos(f6) * (float)this.numberOfBlocks / 8.0F);
			double z2d = (double)((float)(z0 + 8) - MathHelper.cos(f6) * (float)this.numberOfBlocks / 8.0F);
			double y1d = (double)(y0 + random2.nextInt(3) + 2);
			double y2d = (double)(y0 + random2.nextInt(3) + 2);

			for(int i = 0; i <= this.numberOfBlocks; ++i) {
				double radiusXa = x1d + (x2d - x1d) * (double)i / (double)this.numberOfBlocks;
				double radiusYa = y1d + (y2d - y1d) * (double)i / (double)this.numberOfBlocks;
				double radiusZa = z1d + (z2d - z1d) * (double)i / (double)this.numberOfBlocks;
				double blobSize = random2.nextDouble() * (double)this.numberOfBlocks / 16.0D;
				double radiusXZ = (double)(MathHelper.sin((float)i * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * blobSize + 1.0D;
				double radiusY = (double)(MathHelper.sin((float)i * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * blobSize + 1.0D;
				int x1 = MathHelper.floor_double(radiusXa - radiusXZ / 2.0D);
				int x2 = MathHelper.floor_double(radiusXa + radiusXZ / 2.0D);
				int y1 = MathHelper.floor_double(radiusYa - radiusY / 2.0D);
				int y2 = MathHelper.floor_double(radiusYa + radiusY / 2.0D);
				int z1 = MathHelper.floor_double(radiusXa - radiusXZ / 2.0D);
				int z2 = MathHelper.floor_double(radiusXa + radiusXZ / 2.0D);

				for(int x = x1; x <= x2; ++x) {
					for(int y = y1; y <= y2; ++y) {
						for(int z = z1; z <= z2; ++z) {
							double dx = ((double)x + 0.5D - radiusXa) / (radiusXZ / 2.0D);
							double dy = ((double)y + 0.5D - radiusYa) / (radiusY / 2.0D);
							double dz = ((double)z + 0.5D - radiusZa) / (radiusXZ / 2.0D);
							if(dx * dx + dy * dy + dz * dz < 1.0D) {
								int blockID = world.getBlockId(x, y, z);
								if(blockID == Block.sand.blockID) {
									world.setBlock(x, y, z, this.clayBlockId);
								}
							}
						}
					}
				}
			}

			return true;
		}
	}

}
