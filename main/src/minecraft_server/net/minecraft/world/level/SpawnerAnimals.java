package net.minecraft.world.level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.monster.EntityClassicSkeleton;
import net.minecraft.world.entity.monster.EntityClassicZombie;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.monster.IMobWithLevel;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockBed;

public final class SpawnerAnimals {
	private static HashMap<ChunkCoordIntPair, Boolean> eligibleChunksForSpawning = new HashMap<ChunkCoordIntPair, Boolean>();
	@SuppressWarnings("rawtypes")
	protected static final Class[] nightSpawnEntities = new Class[]{EntitySpider.class, EntityZombie.class, EntitySkeleton.class};

	protected static ChunkPosition getRandomSpawningPointInChunk(World world, int chunkX, int chunkZ) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		int x = chunkX * 16 + world.rand.nextInt(16);
		int y = world.rand.nextInt(chunk == null ? 128 : Math.max(128, chunk.getTopFilledSegment()));
		int z = chunkZ * 16 + world.rand.nextInt(16);
		return new ChunkPosition(x, y, z);
	}

	public static final int performSpawning(World world, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
		if(!spawnHostileMobs && !spawnPeacefulMobs) {
			return 0;
		} else {
			eligibleChunksForSpawning.clear();

			for(int i = 0; i < world.playerEntities.size(); ++i) {
				EntityPlayer thePlayer = (EntityPlayer)world.playerEntities.get(i);
				int xChunk = MathHelper.floor_double(thePlayer.posX / 16.0D);
				int zChunk = MathHelper.floor_double(thePlayer.posZ / 16.0D);
				byte radius = 8;

				for(int xc = -radius; xc <= radius; ++xc) {
					for(int zc = -radius; zc <= radius; ++zc) {
						boolean onBorders = xc == -radius || xc == radius || zc == -radius || zc == radius;
						ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(xc + xChunk, zc + zChunk);
						if(!onBorders) {
							eligibleChunksForSpawning.put(chunkCoords, false);
						} else if(!eligibleChunksForSpawning.containsKey(chunkCoords)) {
							eligibleChunksForSpawning.put(chunkCoords, true);
						}
					}
				}
			}

			int totalSpawned = 0;
			ChunkCoordinates spawnPoint = world.getSpawnPoint();
			EnumCreatureType[] creatureTypes = EnumCreatureType.values();
			int numCreatureTypes = creatureTypes.length;

			// Iterate all creature types (animals, mobs, water, cave)
			label126:
			for(int typeIdx = 0; typeIdx < numCreatureTypes; ++typeIdx) {
				EnumCreatureType creatureType = creatureTypes[typeIdx];

				if(
						(!creatureType.getPeacefulCreature() || spawnPeacefulMobs) && 
						(creatureType.getPeacefulCreature() || spawnHostileMobs) && 
						world.countEntities(creatureType.getCreatureClass()) <= creatureType.getMaxNumberOfCreature(world) * eligibleChunksForSpawning.size() / 256
					) {
					Iterator<ChunkCoordIntPair> chunkIt = eligibleChunksForSpawning.keySet().iterator();

					label123:
					while(true) {
						int sX0;
						int sY0;
						int sZ0;
						do {
							do {
								ChunkCoordIntPair curChunkCoords;
								do {
									if(!chunkIt.hasNext()) {
										continue label126;
									}

									curChunkCoords = (ChunkCoordIntPair)chunkIt.next();
								} while(((Boolean)eligibleChunksForSpawning.get(curChunkCoords)).booleanValue());

								ChunkPosition spawnCoordinates = getRandomSpawningPointInChunk(world, curChunkCoords.chunkXPos, curChunkCoords.chunkZPos);
								sX0 = spawnCoordinates.x;
								sY0 = spawnCoordinates.y;
								sZ0 = spawnCoordinates.z;
							} while(world.isBlockNormalCube(sX0, sY0, sZ0));
						} while(world.getBlockMaterial(sX0, sY0, sZ0) != creatureType.getCreatureMaterial());

						int spawned = 0;

						for(int spawningAttempts = 0; spawningAttempts < 3; ++spawningAttempts) {
							int cX = sX0;
							int cY = sY0;
							int cZ = sZ0;
							byte maxD = 6;
							SpawnListEntry spawnClass = null;

							for(int i = 0; i < 4; ++i) {
								cX += world.rand.nextInt(maxD) - world.rand.nextInt(maxD);
								cY += world.rand.nextInt(1) - world.rand.nextInt(1);
								cZ += world.rand.nextInt(maxD) - world.rand.nextInt(maxD);
								
								if(canCreatureTypeSpawnAtLocation(creatureType, world, cX, cY, cZ)) {
									float posX = (float)cX + 0.5F;
									float posY = (float)cY;
									float posZ = (float)cZ + 0.5F;

									// Only if closest player is more than 24 blocks away
									if(world.getClosestPlayer((double)posX, (double)posY, (double)posZ, 24.0D) == null) {
										float dx = posX - (float)spawnPoint.posX;
										float dy = posY - (float)spawnPoint.posY;
										float dz = posZ - (float)spawnPoint.posZ;
										float dSq = dx * dx + dy * dy + dz * dz;

										// More than 24 blocks away from the spawn point
										if(dSq >= 576.0F) {
											if(spawnClass == null) {
												spawnClass = world.getRandomMob(creatureType, cX, cY, cZ);
											
												if(spawnClass == null) {
													break;
												}
												
												// AI based skeletons & zombies?
												if(!GameRules.boolRule("smarterMobs")) {
													if(spawnClass.entityClass == EntityZombie.class) spawnClass.entityClass = EntityClassicZombie.class;
													else if(spawnClass.entityClass == EntitySkeleton.class) spawnClass.entityClass = EntityClassicSkeleton.class; 
												}
											}

											EntityLiving entityToSpawn;
											try {
												entityToSpawn = (EntityLiving)spawnClass.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
											} catch (Exception e) {
												e.printStackTrace();
												return totalSpawned;
											}

											{
	
												entityToSpawn.setLocationAndAngles((double)posX, (double)posY, (double)posZ, world.rand.nextFloat() * 360.0F, 0.0F);
												if(entityToSpawn.getCanSpawnHere()) {
	
													++spawned;
													world.spawnEntityInWorld(entityToSpawn);
													creatureSpecificInit(entityToSpawn, world, posX, posY, posZ);
													if(spawned >= entityToSpawn.getMaxSpawnedInChunk()) {
													continue label123;
												}
											}

												totalSpawned += spawned;
											}
										}
									}
								}
							}
						}
					}
				}
			}

			return totalSpawned;
		}
	}

	public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y, int z) { 
		if(creatureType == EnumCreatureType.cave) {
			return 	!world.canBlockSeeTheSky(x, y, z) &&
					!world.isBlockNormalCube(x, y, z) && 
					!world.getBlockMaterial(x, y, z).isLiquid() && 
					!world.isBlockNormalCube(x, y + 1, z);			
		} else if(creatureType.getCreatureMaterial() == Material.water) {
			return world.getBlockMaterial(x, y, z).isLiquid() && world.getBlockMaterial(x, y - 1, z).isLiquid() && !world.isBlockNormalCube(x, y + 1, z);
		} else if(!world.doesBlockHaveSolidTopSurface(x, y - 1, z)) {
			return false;
		} else {
			int block = world.getBlockId(x, y - 1, z);
			boolean canSpawnAtLocation = Block.isNormalCube(block) && block != Block.bedrock.blockID && !world.isBlockNormalCube(x, y, z) && !world.getBlockMaterial(x, y, z).isLiquid() && !world.isBlockNormalCube(x, y + 1, z);
			
			return canSpawnAtLocation;
		}
	}

	public static void creatureSpecificInit(EntityLiving entityLiving0, World world, float f2, float f3, float f4) {
		if(entityLiving0 instanceof EntitySpider && world.rand.nextInt(100) == 0) {
			EntitySkeleton entitySkeleton7 = new EntitySkeleton(world);
			entitySkeleton7.setLocationAndAngles((double)f2, (double)f3, (double)f4, entityLiving0.rotationYaw, 0.0F);
			world.spawnEntityInWorld(entitySkeleton7);
			entitySkeleton7.mountEntity(entityLiving0);
		} else if(entityLiving0 instanceof EntitySheep) {
			((EntitySheep)entityLiving0).setFleeceColor(EntitySheep.getRandomFleeceColor(world.rand));
		} else if(entityLiving0 instanceof EntityOcelot && world.rand.nextInt(7) == 0) {
			for(int i5 = 0; i5 < 2; ++i5) {
				EntityOcelot entityOcelot6 = new EntityOcelot(world);
				entityOcelot6.setLocationAndAngles((double)f2, (double)f3, (double)f4, entityLiving0.rotationYaw, 0.0F);
				entityOcelot6.setGrowingAge(-24000);
				world.spawnEntityInWorld(entityOcelot6);
			}
		} else if(entityLiving0 instanceof IMobWithLevel) {
			IMobWithLevel mobWithLevel = (IMobWithLevel)entityLiving0;
			mobWithLevel.setLevel(world.rand.nextInt(mobWithLevel.getMaxLevel()));
		}

	}

	public static void performWorldGenSpawning(World world, BiomeGenBase biomeGenBase1, int i2, int i3, int i4, int i5, Random random6) {
		List<?> list7 = biomeGenBase1.getSpawnableList(EnumCreatureType.creature);
		if(!list7.isEmpty()) {
			while(random6.nextFloat() < biomeGenBase1.getSpawningChance()) {
				SpawnListEntry spawnListEntry8 = (SpawnListEntry)WeightedRandom.getRandomItem(world.rand, (Collection<?>)list7);
				int i9 = spawnListEntry8.minGroupCount + random6.nextInt(1 + spawnListEntry8.maxGroupCount - spawnListEntry8.minGroupCount);
				int i10 = i2 + random6.nextInt(i4);
				int i11 = i3 + random6.nextInt(i5);
				int i12 = i10;
				int i13 = i11;

				for(int i14 = 0; i14 < i9; ++i14) {
					boolean z15 = false;

					for(int i16 = 0; !z15 && i16 < 4; ++i16) {
						int i17 = world.getTopSolidOrLiquidBlock(i10, i11);
						if(canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, world, i10, i17, i11)) {
							float f18 = (float)i10 + 0.5F;
							float f19 = (float)i17;
							float f20 = (float)i11 + 0.5F;

							EntityLiving entityLiving21;
							try {
								entityLiving21 = (EntityLiving)spawnListEntry8.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
							} catch (Exception exception23) {
								exception23.printStackTrace();
								continue;
							}

							entityLiving21.setLocationAndAngles((double)f18, (double)f19, (double)f20, random6.nextFloat() * 360.0F, 0.0F);
							world.spawnEntityInWorld(entityLiving21);
							creatureSpecificInit(entityLiving21, world, f18, f19, f20);
							z15 = true;
						}

						i10 += random6.nextInt(5) - random6.nextInt(5);

						for(i11 += random6.nextInt(5) - random6.nextInt(5); i10 < i2 || i10 >= i2 + i4 || i11 < i3 || i11 >= i3 + i4; i11 = i13 + random6.nextInt(5) - random6.nextInt(5)) {
							i10 = i12 + random6.nextInt(5) - random6.nextInt(5);
						}
					}
				}
			}
		}
	}
	
	// Originally, this method was bugged. The path calculation used the n-w corner of the block
	// For measurements rather than the center. This has been fixed in this version.
	public static boolean performSleepSpawning(World world, List<EntityPlayer> listOfPlayers) {
		boolean success = false;
		PathFinder pathFinder = new PathFinder(world, false, false, false, true);
		Iterator<EntityPlayer> itPlayers = listOfPlayers.iterator();

		while(true) {
			EntityPlayer thePlayer;
			Class<?>[] entityClasses;
			do {
				do {
					if(!itPlayers.hasNext()) {
						return success;
					}

					thePlayer = (EntityPlayer)itPlayers.next();
					entityClasses = nightSpawnEntities;
				} while(entityClasses == null);
			} while(entityClasses.length == 0);

			boolean spawned = false;

			for(int attempt = 0; attempt < 20 && !spawned; ++attempt) {

				// Picks up a random position surrounding the player, radius 32 horizontally, 16 vertically.
				int x = MathHelper.floor_double(thePlayer.posX) + world.rand.nextInt(32) - world.rand.nextInt(32);
				int z = MathHelper.floor_double(thePlayer.posZ) + world.rand.nextInt(32) - world.rand.nextInt(32);
				int y0 = MathHelper.floor_double(thePlayer.posY) + world.rand.nextInt(16) - world.rand.nextInt(16);
				if(y0 < 1) {
					y0 = 1;
				} else if(y0 > 128) {
					y0 = 128;
				}

				// Pick up a monster at random
				int entityClassIdx = world.rand.nextInt(entityClasses.length);

				// Sink until hit floor
				int y;
				for(y = y0; y > 2 && !world.isBlockOpaqueCube(x, y - 1, z); --y) {
				}

				while(!canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, world, x, y, z) && y < y0 + 16 && y < 128) {
					++y;
				}

				if(y < y0 + 16 && y < 128) {
					float posX = (float)x + 0.5F;
					float posY = (float)y;
					float posZ = (float)z + 0.5F;

					// Instantiate monster
					EntityLiving theMonster;
					try {
						theMonster = (EntityLiving)entityClasses[entityClassIdx].getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
					} catch (Exception e) {
						e.printStackTrace();
						return success;
					}

					theMonster.setLocationAndAngles((double)posX, (double)posY, (double)posZ, world.rand.nextFloat() * 360.0F, 0.0F);
					if(theMonster.getCanSpawnHere()) {

						// Attempt to create a path from the monster to the player, max 32 blocks.
						PathEntity path = pathFinder.createEntityPathTo(theMonster, thePlayer, 32.0F);

						// If succeeded . . .
						if(path != null && path.pathLength > 1) {
							// Get the final path point 
							PathPoint pathPoint = path.getFinalPathPoint();

							// If the distance to the player is less than 1.5 in any axis...
							// Adding .5 fixes the bug in vanilla
							if(
									Math.abs((double)pathPoint.xCoord + .5 - thePlayer.posX) < 1.5D && 
									Math.abs((double)pathPoint.zCoord + .5 - thePlayer.posZ) < 1.5D && 
									Math.abs((double)pathPoint.yCoord + .5 - thePlayer.posY) < 1.5D
							) {
								ChunkCoordinates posNearBed = BlockBed.getNearestEmptyChunkCoordinates(world, MathHelper.floor_double(thePlayer.posX), MathHelper.floor_double(thePlayer.posY), MathHelper.floor_double(thePlayer.posZ), 1);
								if(posNearBed == null) {
									posNearBed = new ChunkCoordinates(x, y + 1, z);
								}

								// Spawn the monster near the player and wake up player
								theMonster.setLocationAndAngles((double)((float)posNearBed.posX + 0.5F), (double)posNearBed.posY, (double)((float)posNearBed.posZ + 0.5F), 0.0F, 0.0F);
								world.spawnEntityInWorld(theMonster);
								creatureSpecificInit(theMonster, world, (float)posNearBed.posX + 0.5F, (float)posNearBed.posY, (float)posNearBed.posZ + 0.5F);
								thePlayer.wakeUpPlayer(true, false, false);
								theMonster.playLivingSound();
								success = true;
								spawned = true;
							}
						}
					}
				}
			}
		}
	}
}
