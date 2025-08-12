package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldChunkManager;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFluid;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class MoCTools {

	public static boolean nearMaterialWithDistance(World world, Entity entity, Double double1, Material mat) {
		AxisAlignedBB axisalignedbb = entity.boundingBox.expand(double1.doubleValue(), double1.doubleValue(),
				double1.doubleValue());
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					int j2 = world.getBlockId(k1, l1, i2);
					if (j2 != 0 && Block.blocksList[j2].blockMaterial == mat) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isNearTorch(World world, Entity entity) {
		return isNearBlockName(world, entity, 8.0D, "tile.torch");
	}

	public static boolean isNearTorch(World world, Entity entity, Double dist) {
		return isNearBlockName(world, entity, dist, "tile.torch");
	}

	public static boolean isNearBlockName(World world, Entity entity, Double dist, String blockName) {
		AxisAlignedBB axisalignedbb = entity.boundingBox.expand(dist.doubleValue(), dist.doubleValue() / 2.0D,
				dist.doubleValue());
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					int j2 = world.getBlockId(k1, l1, i2);
					if (j2 != 0) {
						String nameToCheck = "";
						nameToCheck = Block.blocksList[j2].getBlockName();
						if (nameToCheck != null && nameToCheck != "" && nameToCheck.equals(blockName)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public static void checkForTwistedEntities(World world) {
		for (int l = 0; l < world.loadedEntityList.size(); ++l) {
			Entity entity = world.loadedEntityList.get(l);
			if (entity instanceof EntityLiving) {
				EntityLiving twisted = (EntityLiving) entity;
				if (twisted.deathTime > 0 && twisted.ridingEntity == null && twisted.health > 0) {
					twisted.deathTime = 0;
				}
			}
		}

	}

	public static double getSqDistanceTo(Entity entity, double i, double j, double k) {
		double l = entity.posX - i;
		double i1 = entity.posY - j;
		double j1 = entity.posZ - k;
		return Math.sqrt(l * l + i1 * i1 + j1 * j1);
	}

	public static int[] returnNearestMaterialCoord(World world, Entity entity, Material material, Double double1,
			Double yOff) {
		double shortestDistance = -1.0D;
		double distance = 0.0D;
		int x = -9999;
		int y = -1;
		int z = -1;
		AxisAlignedBB axisalignedbb = entity.boundingBox.expand(double1.doubleValue(), yOff.doubleValue(),
				double1.doubleValue());
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					int j2 = world.getBlockId(k1, l1, i2);
					if (j2 != 0 && Block.blocksList[j2].blockMaterial == material) {
						distance = getSqDistanceTo(entity, k1, l1, i2);
						if (shortestDistance == -1.0D) {
							x = k1;
							y = l1;
							z = i2;
							shortestDistance = distance;
						}

						if (distance < shortestDistance) {
							x = k1;
							y = l1;
							z = i2;
							shortestDistance = distance;
						}
					}
				}
			}
		}

		if (entity.posX > x) {
			x -= 2;
		} else {
			x += 2;
		}

		if (entity.posZ > z) {
			z -= 2;
		} else {
			z += 2;
		}

		return new int[] { x, y, z };
	}

	public static void MoveCreatureToXYZ(World world, EntityCreature movingEntity, int x, int y, int z, float f) {
		PathEntity pathentity = world.getEntityPathToXYZ(movingEntity, x, y, z, f, true, false, false, true);
		if (pathentity != null) {
			movingEntity.setPathToEntity(pathentity);
		}

	}

	public static void MoveToWater(World world, EntityCreature entity) {
		int[] ai = returnNearestMaterialCoord(world, entity, Material.water, 20.0D, 2.0D);
		if (ai[0] > -1000) {
			MoveCreatureToXYZ(world, entity, ai[0], ai[1], ai[2], 24.0F);
		}

	}

	public static float realAngle(float origAngle) {
		return origAngle % 360.0F;
	}

	public static void SlideEntityToXYZ(Entity entity, int x, int y, int z) {
		if (entity != null) {
			if (entity.posY < y) {
				entity.motionY += 0.15D;
			}

			double d3;
			if (entity.posX < x) {
				d3 = x - entity.posX;
				if (d3 > 0.5D) {
					entity.motionX += 0.05D;
				}
			} else {
				d3 = entity.posX - x;
				if (d3 > 0.5D) {
					entity.motionX -= 0.05D;
				}
			}

			if (entity.posZ < z) {
				d3 = z - entity.posZ;
				if (d3 > 0.5D) {
					entity.motionZ += 0.05D;
				}
			} else {
				d3 = entity.posZ - z;
				if (d3 > 0.5D) {
					entity.motionZ -= 0.05D;
				}
			}
		}

	}

	public static void destroyDrops(World world, Entity entity, double d) {
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(d, d, d));

		for (int i = 0; i < list.size(); ++i) {
			Entity entity1 = list.get(i);
			if (entity1 instanceof EntityItem) {
				EntityItem entityitem = (EntityItem) entity1;
				if (entityitem != null && entityitem.age < 50) {
					entityitem.setDead();
				}
			}
		}

	}

	public static void dropGoodies(World worldObj, Entity entity) {
		EntityItem entityitem = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ,
				new ItemStack(Block.wood, 64));
		entityitem.delayBeforeCanPickup = 10;
		worldObj.spawnEntityInWorld(entityitem);
		EntityItem entityitem2 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ,
				new ItemStack(Item.diamond, 64));
		entityitem2.delayBeforeCanPickup = 10;
		worldObj.spawnEntityInWorld(entityitem2);
		EntityItem entityitem3 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ,
				new ItemStack(Item.coal, 64));
		entityitem3.delayBeforeCanPickup = 10;
		worldObj.spawnEntityInWorld(entityitem3);
		EntityItem entityitem4 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ,
				new ItemStack(Block.stone, 64));
		entityitem4.delayBeforeCanPickup = 10;
		worldObj.spawnEntityInWorld(entityitem4);
	}

	public static float distanceToSurface(World world, Entity entity) {
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_double(entity.posY);
		int k = MathHelper.floor_double(entity.posZ);
		int l = world.getBlockId(i, j, k);
		if (l != 0 && Block.blocksList[l].blockMaterial == Material.water) {
			for (int x = 1; x < 64; ++x) {
				l = world.getBlockId(i, j + x, k);
				if (l == 0 || Block.blocksList[l].blockMaterial != Material.water) {
					return x;
				}
			}
		}

		return 0.0F;
	}

	public boolean isInsideOfMaterial(World world, Material material, Entity entity) {
		double d = entity.posY + entity.getEyeHeight();
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_float(MathHelper.floor_double(d));
		int k = MathHelper.floor_double(entity.posZ);
		int l = world.getBlockId(i, j, k);
		if (l != 0 && Block.blocksList[l].blockMaterial == material) {
			float f = BlockFluid.getFluidHeightPercent(world.getBlockMetadata(i, j, k)) - 0.1111111F;
			float f1 = j + 1 - f;
			return d < f1;
		} else {
			return false;
		}
	}

	public static void DestroyBlast(World world, Entity entity, double d, double d1, double d2, float f, boolean flag) {
		world.playSoundEffect(d, d1, d2, "destroy", 4.0F,
				(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
		HashSet<ChunkPosition> hashset = new HashSet<ChunkPosition>();
		float f1 = f;
		byte i = 16;

		int k;
		int i1;
		int k1;
		double i4;
		double i5;
		double d14;
		for (k = 0; k < i; ++k) {
			for (i1 = 0; i1 < i; ++i1) {
				for (k1 = 0; k1 < i; ++k1) {
					if (k == 0 || k == i - 1 || i1 == 0 || i1 == i - 1 || k1 == 0 || k1 == i - 1) {
						double l1 = k / (i - 1.0F) * 2.0F - 1.0F;
						double j2 = i1 / (i - 1.0F) * 2.0F - 1.0F;
						double vec3d = k1 / (i - 1.0F) * 2.0F - 1.0F;
						double i3 = Math.sqrt(l1 * l1 + j2 * j2 + vec3d * vec3d);
						l1 /= i3;
						j2 /= i3;
						vec3d /= i3;
						float k3 = f * (0.7F + world.rand.nextFloat() * 0.6F);
						i4 = d;
						i5 = d1;
						d14 = d2;
						float d16 = 0.3F;

						for (float f4 = 5.0F; k3 > 0.0F; k3 -= d16 * 0.75F) {
							int d18 = MathHelper.floor_double(i4);
							int l5 = MathHelper.floor_double(i5);
							int d20 = MathHelper.floor_double(d14);
							int j6 = world.getBlockId(d18, l5, d20);
							if (j6 > 0) {
								f4 = Block.blocksList[j6].blockHardness;
								k3 -= (Block.blocksList[j6].getExplosionResistance(entity) + 0.3F) * (d16 / 10.0F);
							}

							if (k3 > 0.0F && i5 > entity.posY && f4 < 3.0F) {
								hashset.add(new ChunkPosition(d18, l5, d20));
							}

							i4 += l1 * d16;
							i5 += j2 * d16;
							d14 += vec3d * d16;
						}
					}
				}
			}
		}

		f *= 2.0F;
		k = MathHelper.floor_double(d - f - 1.0D);
		i1 = MathHelper.floor_double(d + f + 1.0D);
		k1 = MathHelper.floor_double(d1 - f - 1.0D);
		int i44 = MathHelper.floor_double(d1 + f + 1.0D);
		int i2 = MathHelper.floor_double(d2 - f - 1.0D);
		int i45 = MathHelper.floor_double(d2 + f + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity,
				AxisAlignedBB.getBoundingBoxFromPool(k, k1, i2, i1, i44, i45));
		Vec3D vec3D46 = Vec3D.createVector(d, d1, d2);

		double d54;
		double d55;
		double d56;
		for (int arraylist = 0; arraylist < list.size(); ++arraylist) {
			Entity entity48 = list.get(arraylist);
			double chunkposition1 = entity48.getDistance(d, d1, d2) / f;
			if (chunkposition1 <= 1.0D) {
				i4 = entity48.posX - d;
				i5 = entity48.posY - d1;
				d14 = entity48.posZ - d2;
				d54 = MathHelper.sqrt_double(i4 * i4 + i5 * i5 + d14 * d14);
				i4 /= d54;
				i5 /= d54;
				d14 /= d54;
				d55 = world.getBlockDensity(vec3D46, entity48.boundingBox);
				d56 = (1.0D - chunkposition1) * d55;

				entity48.attackEntityFrom(DamageSource.explosion, (int) ((d56 * d56 + d56) / 2.0D * 3.0D * f + 1.0D));
				entity48.motionX += i4 * d56;
				entity48.motionY += i5 * d56;
				entity48.motionZ += d14 * d56;

			}
		}

		f = f1;
		ArrayList<ChunkPosition> arrayList47 = new ArrayList<ChunkPosition>();
		arrayList47.addAll(hashset);

		int k4;
		int i49;
		ChunkPosition chunkPosition50;
		int i51;
		int i52;
		int i53;
		for (i49 = arrayList47.size() - 1; i49 >= 0; --i49) {
			chunkPosition50 = arrayList47.get(i49);
			i51 = chunkPosition50.x;
			i52 = chunkPosition50.y;
			k4 = chunkPosition50.z;
			i53 = world.getBlockId(i51, i52, k4);

			for (int j5 = 0; j5 < 5; ++j5) {
				d14 = i51 + world.rand.nextFloat();
				d54 = i52 + world.rand.nextFloat();
				d55 = k4 + world.rand.nextFloat();
				d56 = d14 - d;
				double d22 = d54 - d1;
				double d23 = d55 - d2;
				double d24 = MathHelper.sqrt_double(d56 * d56 + d22 * d22 + d23 * d23);
				d56 /= d24;
				d22 /= d24;
				d23 /= d24;
				double d25 = 0.5D / (d24 / f + 0.1D);
				d25 *= world.rand.nextFloat() * world.rand.nextFloat() + 0.3F;
				--d25;
				d56 *= d25;
				d22 *= d25 - 1.0D;
				d23 *= d25;
				world.spawnParticle("explode", (d14 + d * 1.0D) / 2.0D, (d54 + d1 * 1.0D) / 2.0D,
						(d55 + d2 * 1.0D) / 2.0D, d56, d22, d23);
				entity.motionX -= 0.001000000047497451D;
				entity.motionY -= 0.001000000047497451D;
			}

			if (i53 > 0) {
				Block.blocksList[i53].dropBlockAsItemWithChance(world, i51, i52, k4,
						world.getBlockMetadata(i51, i52, k4), 0.3F, 0);
				world.setBlockWithNotify(i51, i52, k4, 0);
				Block.blocksList[i53].onBlockDestroyedByExplosion(world, i51, i52, k4);
			}
		}

		if (flag) {
			for (i49 = arrayList47.size() - 1; i49 >= 0; --i49) {
				chunkPosition50 = arrayList47.get(i49);
				i51 = chunkPosition50.x;
				i52 = chunkPosition50.y;
				k4 = chunkPosition50.z;
				i53 = world.getBlockId(i51, i52, k4);
				if (i53 == 0 && world.rand.nextInt(8) == 0) {
					world.setBlockWithNotify(i51, i52, k4, Block.fire.blockID);
				}
			}
		}

	}

	public static void disorientEntity(World world, Entity entity) {
		double rotD = 0.0D;
		double motD = 0.0D;
		double d = world.rand.nextGaussian();
		double d1 = 0.1D * d;
		motD = 0.2D * d1 + 0.8D * motD;
		entity.motionX += motD;
		entity.motionZ += motD;
		double d2 = 0.78D * d;
		rotD = 0.125D * d2 + 0.875D * rotD;
		entity.rotationYaw = (float) (entity.rotationYaw + rotD);
		entity.rotationPitch = (float) (entity.rotationPitch + rotD);
	}

	public static void slowEntity(Entity entity) {
		entity.motionX *= 0.8D;
		entity.motionZ *= 0.8D;
	}

	public static int colorize(int i) {
		return ~i & 15;
	}

	protected static int entityDespawnCheck(World worldObj, EntityLiving entityliving) {
		int count = 0;
		EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(entityliving, -1.0D);
		if (entityplayer != null) {
			double d = entityplayer.posX - entityliving.posX;
			double d1 = entityplayer.posY - entityliving.posY;
			double d2 = entityplayer.posZ - entityliving.posZ;
			double d3 = d * d + d1 * d1 + d2 * d2;
			if (d3 > 16384.0D) {
				entityliving.setDead();
				++count;
			}

			if (entityliving.entityAge > 600 && worldObj.rand.nextInt(800) == 0) {
				if (d3 < 1024.0D) {
					entityliving.entityAge = 0;
				} else {
					entityliving.setDead();
					++count;
				}
			}
		}

		return count;
	}

	public int countEntities(Class<?> class1, World worldObj) {
		int i = 0;

		for (int j = 0; j < worldObj.loadedEntityList.size(); ++j) {
			Entity entity = worldObj.loadedEntityList.get(j);
			if (class1.isAssignableFrom(entity.getClass())) {
				++i;
			}
		}

		return i;
	}

	public static int despawnVanillaAnimals(World worldObj) {
		return despawnVanillaAnimals(worldObj, (List<Class<?>>[]) null);
	}

	public static int despawnVanillaAnimals(World worldObj, List<Class<?>>[] classList) {
		int count = 0;

		for (int j = 0; j < worldObj.loadedEntityList.size(); ++j) {
			Entity entity = worldObj.loadedEntityList.get(j);
			if (entity instanceof EntityLiving && (entity instanceof EntityCow || entity instanceof EntitySheep
					|| entity instanceof EntityPig || entity instanceof EntityChicken || entity instanceof EntitySquid
					|| entity instanceof EntityWolf)) {
				count += entityDespawnCheck(worldObj, (EntityLiving) entity);
			}
		}

		return count;
	}

	public static List<SpawnListEntry> spawnList(World world, EnumCreatureType enumcreaturetype, int i, int j, int k) {
		WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
		if (worldchunkmanager == null) {
			return null;
		} else {
			BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(i >> 4, k >> 4);
			return biomegenbase == null ? null : biomegenbase.getSpawnableList(enumcreaturetype);
		}
	}

	public static BiomeGenBase whatBiome(World world, int i, int j, int k) {
		WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
		if (worldchunkmanager == null) {
			return null;
		} else {
			BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(i, k);
			return biomegenbase == null ? null : biomegenbase;
		}
	}

	public static float distToPlayer(Entity entity) {
		return 0.0F;
	}

	public static String BiomeName(World world, int i, int j, int k) {
		WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
		if (worldchunkmanager == null) {
			return null;
		} else {
			BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(i, k);
			return biomegenbase == null ? null : biomegenbase.biomeName;
		}
	}

	public static EntityItem getClosestEntityItem(World world, Entity entity, double d) {
		double d1 = -1D;
		EntityItem entityitem = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(d, d, d));
		for (int k = 0; k < list.size(); k++) {
			Entity entity1 = list.get(k);
			if (!(entity1 instanceof EntityItem)) {
				continue;
			}
			EntityItem entityitem1 = (EntityItem) entity1;
			double d2 = entityitem1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1))) {
				d1 = d2;
				entityitem = entityitem1;
			}
		}

		return entityitem;
	}

	public static void bigsmack(Entity entity, Entity entity1, float force) {
		double d = entity.posX - entity1.posX;
		double d1 = entity.posZ - entity1.posZ;
		for (d1 = entity.posZ - entity1.posZ; ((d * d) + (d1 * d1)) < 0.0001D; d1 = (Math.random() - Math.random())
				* 0.01D) {
			d = (Math.random() - Math.random()) * 0.01D;
		}

		float f = MathHelper.sqrt_double((d * d) + (d1 * d1));
		entity1.motionX /= 2D;
		entity1.motionY /= 2D;
		entity1.motionZ /= 2D;
		entity1.motionX -= (d / f) * force;
		entity1.motionY += force;
		entity1.motionZ -= (d1 / f) * force;
		if (entity1.motionY > force) {
			entity1.motionY = force;
		}
	}


}
