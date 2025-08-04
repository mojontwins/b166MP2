package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.LootItem;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class WorldGenDungeonsWater extends WorldGenDungeons {

	public WorldGenDungeonsWater(BiomeGenBase biomeGen) {
		super(biomeGen);

		this.possibleLootItems.add(new LootItem (Block.sponge.blockID, 0, 1, false, 0));
	}

	// Water dungeons bases must be completely sunk under at least 6 liquid blocks
	public boolean validLocation(World world, int x, int y, int z, int width, int height, int depth) {
		if(y > 60) return false;
	
		int validWater = 0;
		for(int xx = x - width - 1; xx <= x + width + 1; ++xx) {
			for(int zz = z - depth - 1; zz <= z + depth + 1; ++zz) {
				// For every x, z in the base plane
				boolean valid = true;
				for(int yy = y; yy < y + 6 && valid; yy ++) {
					valid &= world.getBlockMaterial(xx, yy, zz) == Material.water;
				}
				if(valid) validWater ++;
			}
		}
		
		return validWater > 1;
	}
	
	public void drawRoom(World world, Random rand, int x, int y, int z, int width, int height, int depth) {
		for(int xx = x - width - 1; xx <= x + width + 1; ++xx) {
			for(int yy = y + height + 1; yy >= y - 1; --yy) {
				for(int zz = z - depth - 1; zz <= z + depth + 1; ++zz) {
					if(xx != x - width - 1 && yy != y - 1 && zz != z - depth - 1 && xx != x + width + 1 && yy != y + height + 1 && zz != z + depth + 1) {
						// If not in the borders, clear.
						world.setBlockWithNotify(xx, yy, zz, 0);
					} else {
						// If in the borders, set brick
						world.setBlockWithNotify(xx, yy, zz, Block.brick.blockID);
					}
				}
			}
		}
	}
	
	public int getFillerId() {
		return 0;
	}
	
	public void extraDecos (World world, int x, int y, int z, Random rand) {
		world.setBlockWithNotify(x, y - 1, z, Block.sponge.blockID);	
	}
}
