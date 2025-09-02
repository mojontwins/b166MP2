package net.minecraft.world.level.tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockFire extends Block {
	private Map<Class<? extends Block>, Integer> propagators = new HashMap<>();
	private Map<Class<? extends Block>, Integer> flamability = new HashMap<>();
	
	protected BlockFire(int i1, int i2) {
		super(i1, i2, Material.fire);
		this.setTickRandomly(true);
	}

	public void initializeBlock() {

		this.setBurnRate(BlockWood.class, 5, 20);
		this.setBurnRate(BlockFence.class, 5, 20);
		this.setBurnRate(BlockStep.class, 5, 20);
		this.setBurnRate(BlockStairs.class, 5, 20);
		this.setBurnRate(BlockLog.class, 5, 5);
		this.setBurnRate(BlockSapling.class, 5, 20);
		this.setBurnRate(BlockLeaves.class, 30, 60);
		this.setBurnRate(BlockBookshelf.class, 30, 20);
		this.setBurnRate(BlockTNT.class, 15, 100);
		this.setBurnRate(BlockTallGrass.class, 60, 100);
		this.setBurnRate(BlockCloth.class, 30, 60);
		this.setBurnRate(BlockVine.class, 15, 100);
		
	}
	
	/*
	private void setBurnRate(int i1, int i2, int i3) {
		this.chanceToEncourageFire[i1] = i2;
		this.abilityToCatchFire[i1] = i3;
	}
	*/
	private void setBurnRate(Class<? extends Block> blockClass, int propagativity, int flamability) {
		this.propagators.put(blockClass, Integer.valueOf(propagativity));
		this.flamability.put(blockClass, Integer.valueOf(flamability));
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i2, int i3, int i4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 3;
	}

	public int quantityDropped(Random random1) {
		return 0;
	}

	public int tickRate() {
		return 30;
	}

	public void updateTick(World world, int i2, int i3, int i4, Random random5) {
		int bid = world.getBlockId(i2, i3 - 1, i4);
		boolean z6 = bid == Block.netherrack.blockID || bid == Block.blockCoal.blockID;
		
		if(!this.canPlaceBlockAt(world, i2, i3, i4)) {
			world.setBlockWithNotify(i2, i3, i4, 0);
		}

		if(z6 || !world.isRaining() || !world.canLightningStrikeAt(i2, i3, i4) && !world.canLightningStrikeAt(i2 - 1, i3, i4) && !world.canLightningStrikeAt(i2 + 1, i3, i4) && !world.canLightningStrikeAt(i2, i3, i4 - 1) && !world.canLightningStrikeAt(i2, i3, i4 + 1)) {
			int i7 = world.getBlockMetadata(i2, i3, i4);
			if(i7 < 15) {
				world.setBlockMetadata(i2, i3, i4, i7 + random5.nextInt(3) / 2);
			}

			world.scheduleBlockUpdate(i2, i3, i4, this.blockID, this.tickRate() + random5.nextInt(10));
			if(!z6 && !this.canNeighborBurn(world, i2, i3, i4)) {
				if(!world.isBlockNormalCube(i2, i3 - 1, i4) || i7 > 3) {
					world.setBlockWithNotify(i2, i3, i4, 0);
				}

			} else if(!z6 && !this.canBlockCatchFire(world, i2, i3 - 1, i4) && i7 == 15 && random5.nextInt(4) == 0) {
				world.setBlockWithNotify(i2, i3, i4, 0);
			} else {
				boolean z8 = world.isBlockHighHumidity(i2, i3, i4);
				byte b9 = 0;
				if(z8) {
					b9 = -50;
				}

				this.tryToCatchBlockOnFire(world, i2 + 1, i3, i4, 300 + b9, random5, i7);
				this.tryToCatchBlockOnFire(world, i2 - 1, i3, i4, 300 + b9, random5, i7);
				this.tryToCatchBlockOnFire(world, i2, i3 - 1, i4, 250 + b9, random5, i7);
				this.tryToCatchBlockOnFire(world, i2, i3 + 1, i4, 250 + b9, random5, i7);
				this.tryToCatchBlockOnFire(world, i2, i3, i4 - 1, 300 + b9, random5, i7);
				this.tryToCatchBlockOnFire(world, i2, i3, i4 + 1, 300 + b9, random5, i7);

				for(int i10 = i2 - 1; i10 <= i2 + 1; ++i10) {
					for(int i11 = i4 - 1; i11 <= i4 + 1; ++i11) {
						for(int i12 = i3 - 1; i12 <= i3 + 4; ++i12) {
							if(i10 != i2 || i12 != i3 || i11 != i4) {
								int i13 = 100;
								if(i12 > i3 + 1) {
									i13 += (i12 - (i3 + 1)) * 100;
								}

								int i14 = this.getChanceOfNeighborsEncouragingFire(world, i10, i12, i11);
								if(i14 > 0) {
									int i15 = (i14 + 40) / (i7 + 30);
									if(z8) {
										i15 /= 2;
									}

									if(i15 > 0 && random5.nextInt(i13) <= i15 && (!world.isRaining() || !world.canLightningStrikeAt(i10, i12, i11)) && !world.canLightningStrikeAt(i10 - 1, i12, i4) && !world.canLightningStrikeAt(i10 + 1, i12, i11) && !world.canLightningStrikeAt(i10, i12, i11 - 1) && !world.canLightningStrikeAt(i10, i12, i11 + 1)) {
										int i16 = i7 + random5.nextInt(5) / 4;
										if(i16 > 15) {
											i16 = 15;
										}

										world.setBlockAndMetadataWithNotify(i10, i12, i11, this.blockID, i16);
									}
								}
							}
						}
					}
				}

			}
		} else {
			world.setBlockWithNotify(i2, i3, i4, 0);
		}
	}

	private void tryToCatchBlockOnFire(World world, int x, int y, int z, int chance, Random rand, int i7) {
		Block b = world.getBlock(x, y, z);
		if(b == null) return;
		
		// Find if the current block class extends any of the defined keys in our flamability map
		Set<Class<? extends Block>> l = this.flamability.keySet();
		for(Class<? extends Block> clazz : l) {
			if(clazz.isAssignableFrom(b.getClass())) {
				int i8 = this.flamability.get(clazz);
				
				if(rand.nextInt(chance) < i8) {
					boolean z9 = world.getBlockId(x, y, z) == Block.tnt.blockID;
					if(rand.nextInt(i7 + 10) < 5 && !world.canLightningStrikeAt(x, y, z)) {
						int i10 = i7 + rand.nextInt(5) / 4;
						if(i10 > 15) {
							i10 = 15;
						}

						world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, i10);
					} else {
						world.setBlockWithNotify(x, y, z, 0);
					}

					if(z9) {
						Block.tnt.onBlockDestroyedByPlayer(world, x, y, z, 1);
					}
				}
			}
		}
		
	}

	private boolean canNeighborBurn(World world, int i2, int i3, int i4) {
		return this.canBlockCatchFire(world, i2 + 1, i3, i4) ? true : (this.canBlockCatchFire(world, i2 - 1, i3, i4) ? true : (this.canBlockCatchFire(world, i2, i3 - 1, i4) ? true : (this.canBlockCatchFire(world, i2, i3 + 1, i4) ? true : (this.canBlockCatchFire(world, i2, i3, i4 - 1) ? true : this.canBlockCatchFire(world, i2, i3, i4 + 1)))));
	}

	private int getChanceOfNeighborsEncouragingFire(World world, int i2, int i3, int i4) {
		byte b5 = 0;
		if(!world.isAirBlock(i2, i3, i4)) {
			return 0;
		} else {
			int i6 = this.getChanceToEncourageFire(world, i2 + 1, i3, i4, b5);
			i6 = this.getChanceToEncourageFire(world, i2 - 1, i3, i4, i6);
			i6 = this.getChanceToEncourageFire(world, i2, i3 - 1, i4, i6);
			i6 = this.getChanceToEncourageFire(world, i2, i3 + 1, i4, i6);
			i6 = this.getChanceToEncourageFire(world, i2, i3, i4 - 1, i6);
			i6 = this.getChanceToEncourageFire(world, i2, i3, i4 + 1, i6);
			return i6;
		}
	}

	public boolean isCollidable() {
		return false;
	}

	public int getChanceToEncourageFire(Block b) {
		if(b == null) return 0;
		
		Set<Class<? extends Block>> l = this.propagators.keySet();
		for(Class<? extends Block> clazz : l) {
			if(clazz.isAssignableFrom(b.getClass())) {
				return this.propagators.get(clazz);
			}
		}
		
		return 0;
	}
	
	public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z) {
		return this.getChanceToEncourageFire(world.getBlock(x, y, z)) > 0;
	}

	public int getChanceToEncourageFire(World world, int x, int y, int z, int min) {
		int chance = this.getChanceToEncourageFire(world.getBlock(x, y, z));
		return chance > min ? chance : min;
	}

	public boolean canPlaceBlockAt(World world, int i2, int i3, int i4) {
		return world.isBlockNormalCube(i2, i3 - 1, i4) || this.canNeighborBurn(world, i2, i3, i4);
	}

	public void onNeighborBlockChange(World world, int i2, int i3, int i4, int i5) {
		if(!world.isBlockNormalCube(i2, i3 - 1, i4) && !this.canNeighborBurn(world, i2, i3, i4)) {
			world.setBlockWithNotify(i2, i3, i4, 0);
		}
	}

	public void onBlockAdded(World world, int i2, int i3, int i4) {
		// The commented part is what creates the portal.
		if(
				!world.worldProvider.canCreatePortalToTheNether() || 
				world.getBlockId(i2, i3 - 1, i4) != Block.obsidian.blockID || 
				!Block.portal.tryToCreatePortal(world, i2, i3, i4)
		) {
			if(!world.isBlockNormalCube(i2, i3 - 1, i4) && !this.canNeighborBurn(world, i2, i3, i4)) {
				world.setBlockWithNotify(i2, i3, i4, 0);
			} else {
				world.scheduleBlockUpdate(i2, i3, i4, this.blockID, this.tickRate() + world.rand.nextInt(10));
			}
		}

	}

	public void randomDisplayTick(World world, int i2, int i3, int i4, Random random5) {
		if(random5.nextInt(24) == 0) {
			world.playSoundEffect((double)((float)i2 + 0.5F), (double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F), "fire.fire", 1.0F + random5.nextFloat(), random5.nextFloat() * 0.7F + 0.3F);
		}

		int i6;
		float f7;
		float f8;
		float f9;
		if(!world.isBlockNormalCube(i2, i3 - 1, i4) && !Block.fire.canBlockCatchFire(world, i2, i3 - 1, i4)) {
			if(Block.fire.canBlockCatchFire(world, i2 - 1, i3, i4)) {
				for(i6 = 0; i6 < 2; ++i6) {
					f7 = (float)i2 + random5.nextFloat() * 0.1F;
					f8 = (float)i3 + random5.nextFloat();
					f9 = (float)i4 + random5.nextFloat();
					world.spawnParticle("largesmoke", (double)f7, (double)f8, (double)f9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(world, i2 + 1, i3, i4)) {
				for(i6 = 0; i6 < 2; ++i6) {
					f7 = (float)(i2 + 1) - random5.nextFloat() * 0.1F;
					f8 = (float)i3 + random5.nextFloat();
					f9 = (float)i4 + random5.nextFloat();
					world.spawnParticle("largesmoke", (double)f7, (double)f8, (double)f9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(world, i2, i3, i4 - 1)) {
				for(i6 = 0; i6 < 2; ++i6) {
					f7 = (float)i2 + random5.nextFloat();
					f8 = (float)i3 + random5.nextFloat();
					f9 = (float)i4 + random5.nextFloat() * 0.1F;
					world.spawnParticle("largesmoke", (double)f7, (double)f8, (double)f9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(world, i2, i3, i4 + 1)) {
				for(i6 = 0; i6 < 2; ++i6) {
					f7 = (float)i2 + random5.nextFloat();
					f8 = (float)i3 + random5.nextFloat();
					f9 = (float)(i4 + 1) - random5.nextFloat() * 0.1F;
					world.spawnParticle("largesmoke", (double)f7, (double)f8, (double)f9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(world, i2, i3 + 1, i4)) {
				for(i6 = 0; i6 < 2; ++i6) {
					f7 = (float)i2 + random5.nextFloat();
					f8 = (float)(i3 + 1) - random5.nextFloat() * 0.1F;
					f9 = (float)i4 + random5.nextFloat();
					world.spawnParticle("largesmoke", (double)f7, (double)f8, (double)f9, 0.0D, 0.0D, 0.0D);
				}
			}
		} else {
			for(i6 = 0; i6 < 3; ++i6) {
				f7 = (float)i2 + random5.nextFloat();
				f8 = (float)i3 + random5.nextFloat() * 0.5F + 0.5F;
				f9 = (float)i4 + random5.nextFloat();
				world.spawnParticle("largesmoke", (double)f7, (double)f8, (double)f9, 0.0D, 0.0D, 0.0D);
			}
		}

	}
}
