package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LootItem;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntityChest;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawner;

public class WorldGenDungeons extends WorldGenerator {
	
	// Dehardcode possible loot (or at least be less cumbersome!)

	public static LootItem possibleLootItems [] = {
		new LootItem (Item.saddle.shiftedIndex, 0, 1, false, 0),
		new LootItem (Item.ingotIron.shiftedIndex, 0, 4, false, 0),
		new LootItem (Item.bread.shiftedIndex, 0, 1, false, 0),
		new LootItem (Item.wheat.shiftedIndex, 0, 6, false, 0),
		new LootItem (Item.gunpowder.shiftedIndex, 0, 4, false, 0),
		new LootItem (Item.silk.shiftedIndex, 0, 4, false, 0),
		new LootItem (Item.bucketEmpty.shiftedIndex, 0, 1, false, 0),
		new LootItem (Item.appleGold.shiftedIndex, 0, 1, true, 100),
		new LootItem (Item.diamond.shiftedIndex, 0, 1, true, 25),
		new LootItem (Item.redstone.shiftedIndex, 0, 4, true, 2),
		new LootItem (Item.record13.shiftedIndex, 0, 1, false, 0),
		new LootItem (Item.recordCat.shiftedIndex, 0, 1, false, 0),
		new LootItem (Block.sponge.blockID, 0, 1, false, 0)	
	};
	
	// Repeat to get more chance
	
	public static String possibleSpanwers [] = {
		"Skeleton", "Zombie", "Zombie", "Spider"
	};
	
	// Dungeon tier - if harder mobs, more loot
	public int minDungeonTier = 0;
	public int maxDungeonTier = 0;
	
	private BiomeGenBase biomeGen;
	
	public WorldGenDungeons(BiomeGenBase biomeGen) {
		this.biomeGen = biomeGen;
	}
	
	public boolean generate(World world, Random rand, int x, int y, int z) {
		this.maxDungeonTier = rand.nextInt(4);
		if (this.maxDungeonTier > 0) this.minDungeonTier = rand.nextInt(maxDungeonTier);
		
		byte height = 3;
		int width = rand.nextInt(4) + 2;
		int depth = rand.nextInt(4) + 2;
		int var9 = 0;

		int xx;
		int yy;
		int zz;
		
		int exposedCeilingBlocks = 0;
		
		for(xx = x - width - 1; xx <= x + width + 1; ++xx) {
			for(yy = y - 1; yy <= y + height + 1; ++yy) {
				for(zz = z - depth - 1; zz <= z + depth + 1; ++zz) {
					Material material = world.getBlockMaterial(xx, yy, zz);
					if(yy == y - 1 && !material.isSolid()) {
						return false;
					}

					if(yy == y + height + 1 && !material.isSolid()) {
						//return false;
						exposedCeilingBlocks ++;
					}

					if((xx == x - width - 1 || xx == x + width + 1 || zz == z - depth - 1 || zz == z + depth + 1) && yy == y) {
						int bid1 = world.getBlockId(xx, yy, zz); 
						int bid2 = world.getBlockId(xx, yy + 1, zz);
						if ((bid1 == 0 || bid1 == Block.waterStill.blockID) &&
							(bid2 == 0 || bid2 == Block.waterStill.blockID)) {
							++var9;
					}
				}
			}
		}
		}
		
		// more or less half the ceiling blocks must be covered
		if(exposedCeilingBlocks > 2 * width * depth) return false;

		if(var9 >= 1 && var9 <= 5) {
			//System.out.println ("Dungeon @ " + x + ", " + y + ", " + z + " (" + this.minDungeonTier + " - " + this.maxDungeonTier + ")");
			
			for(xx = x - width - 1; xx <= x + width + 1; ++xx) {
				for(yy = y + height; yy >= y - 1; --yy) {
					for(zz = z - depth - 1; zz <= z + depth + 1; ++zz) {
						if(xx != x - width - 1 && yy != y - 1 && zz != z - depth - 1 && xx != x + width + 1 && yy != y + height + 1 && zz != z + depth + 1) {
							world.setBlockWithNotify(xx, yy, zz, 0);
						} else if(yy >= 0 && !world.getBlockMaterial(xx, yy - 1, zz).isSolid()) {
							world.setBlockWithNotify(xx, yy, zz, 0);
						} else if(world.getBlockMaterial(xx, yy, zz).isSolid()) {
							if(yy == y - 1 && rand.nextInt(4) != 0) {
								world.setBlockWithNotify(xx, yy, zz, Block.cobblestoneMossy.blockID);
							} else {
								world.setBlockWithNotify(xx, yy, zz, Block.cobblestone.blockID);
							}
						}
					}
				}
			}

			int chests = 0; int maxChests = 2 + (this.maxDungeonTier > 2 ? 1 : 0);

			for(int i = 0; i < 2 + (this.maxDungeonTier > 3 ? 1 : 0); ++i) {
				for(yy = 0; yy < 3; ++yy) {
					xx = x + rand.nextInt(width * 2 + 1) - width;
					zz = z + rand.nextInt(depth * 2 + 1) - depth;
					
					if(world.getBlockId(xx, y, zz) == 0) {
						int adjacentSolidBlocks = 0;
						if(world.getBlockMaterial(xx - 1, y, zz).isSolid()) {
							++adjacentSolidBlocks;
						}

						if(world.getBlockMaterial(xx + 1, y, zz).isSolid()) {
							++adjacentSolidBlocks;
						}

						if(world.getBlockMaterial(xx, y, zz - 1).isSolid()) {
							++adjacentSolidBlocks;
						}

						if(world.getBlockMaterial(xx, y, zz + 1).isSolid()) {
							++adjacentSolidBlocks;
						}

						if(adjacentSolidBlocks == 1 && chests < maxChests) {
							world.setBlockWithNotify(xx, y, zz, Block.chest.blockID);
							TileEntityChest tileEntityChest = (TileEntityChest)world.getBlockTileEntity(xx, y, zz);

							if(tileEntityChest != null) {
								for (int j = 0; j < 8 + this.maxDungeonTier; j ++) {
									ItemStack var18 = this.pickChestLootItem(rand);
									if(var18 != null) {
										tileEntityChest.setInventorySlotContents(rand.nextInt(tileEntityChest.getSizeInventory()), var18);
									}
								}	

								++ chests;
							} else {
								world.setBlockWithNotify(xx, y, zz, 0);
							}
						}
					}
				}
			}

			world.setBlockWithNotify(x, y, z, Block.mobSpawner.blockID);
			TileEntityMobSpawner tileEntityMobSpawner = (TileEntityMobSpawner)world.getBlockTileEntity(x, y, z);
			if (tileEntityMobSpawner != null) {
				tileEntityMobSpawner.mobID = this.pickMobSpawner(rand); 
	
				tileEntityMobSpawner.setMinArmorTier(this.minDungeonTier);
				tileEntityMobSpawner.setMaxArmorTier(this.maxDungeonTier);
				
				// Generate cobwebs for spider dungeons
				if (tileEntityMobSpawner.mobID == "Spider") {
					WorldGenCobweb worldGenCobweb = new WorldGenCobweb();
					for (int i = 0; i < 4; i ++) {
						worldGenCobweb.generate(world, rand, x + rand.nextInt(8) - 4, y + rand.nextInt(3), z + rand.nextInt(8) - 4);
					}
				}
				//System.out.println ("Dungeon Spawner @ " + x + " " + y + " " + z + " tier " + this.minDungeonTier + "-" + this.maxDungeonTier);
				return true;
			} else return false;
		} else {
			return false;
		}
	}

	private ItemStack pickChestLootItem(Random rand) {	
		// Original code was RUBBISH, let's do this properly. 
		int selection = rand.nextInt (possibleLootItems.length + 2);
		if (selection < possibleLootItems.length) {
			LootItem item = possibleLootItems [selection];
			if (item.isRare && rand.nextInt (item.chance) != 0) return null;
			return new ItemStack (item.id, 1 + rand.nextInt (item.maxQuantity), item.damage);
		} else return null;
	}

	private String pickMobSpawner(Random rand) {
		// Original code was RUBBISH, let's do this properly. 
		// Also expand so we can generate biome-themed spawners
		if (this.biomeGen.getPreferedSpawner() != null && rand.nextInt (this.biomeGen.getPreferedSpawnerChance()) < this.biomeGen.getPreferedSpawnerChanceOffset()) {
			return this.biomeGen.getPreferedSpawner();
	}
		return possibleSpanwers [rand.nextInt (possibleSpanwers.length)];
	}
}
