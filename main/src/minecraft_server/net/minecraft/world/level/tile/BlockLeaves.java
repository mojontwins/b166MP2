package net.minecraft.world.level.tile;

import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.World;
import net.minecraft.world.level.colorizer.ColorizerFoliage;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockLeaves extends BlockLeavesBase {
	private int baseIndexInPNG;
	int[] surroundings;
	
	public static int decays = 0;
	
	public static final int OakMetadata = 0;
	public static final int SpruceMetadata = 1;
	public static final int BirchMetadata = 2;
	public static final int JungleMetadata = 3;

	protected BlockLeaves(int blockID, int blockIndexInTexture) {
		super(blockID, blockIndexInTexture, Material.leaves, false);
		
		this.baseIndexInPNG = blockIndexInTexture;
		this.setTickRandomly(true);
	}

	public int getBlockColor() {
		double d1 = 0.5D;
		double d3 = 1.0D;
		return ColorizerFoliage.getFoliageColor(d1, d3);
	}

	public int getRenderColor(int i1) {
		if(Seasons.activated()) return Seasons.getLeavesColorForToday();
		return (i1 & 3) == 1 ? ColorizerFoliage.getFoliageColorPine() : ((i1 & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : 0x5BFB3B);
	}

	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		/*
		int i5 = blockAccess.getBlockMetadata(x, y, z);

		if((i5 & 3) == 1) {
			return ColorizerFoliage.getFoliageColorPine();
		} else if((i5 & 3) == 2) {
			return ColorizerFoliage.getFoliageColorBirch();
		} else {
			int i6 = 0;
			int i7 = 0;
			int i8 = 0;

			for(int i9 = -1; i9 <= 1; ++i9) {
				for(int i10 = -1; i10 <= 1; ++i10) {
					int i11 = blockAccess.getBiomeGenForCoords(x + i10, z + i9).getBiomeFoliageColor();
					i6 += (i11 & 16711680) >> 16;
					i7 += (i11 & 65280) >> 8;
					i8 += i11 & 255;
				}
			}

			return (i6 / 9 & 255) << 16 | (i7 / 9 & 255) << 8 | i8 / 9 & 255;
		}
		*/
		
		return this.getRenderColor(blockAccess.getBlockMetadata(x, y, z));
	}

	public void onBlockRemoval(World world, int x, int y, int z) {
		int blockID = world.getBlockId(x, y, z);

		// Small optimization: When replaced with leaves or wood, surrounding leaves are
		// NOT affected
		if (blockID == Block.wood.blockID || blockID == Block.leaves.blockID)
			return;
		
		this.onBlockRemovalDo(world, x, y, z);
	}

	public void onBlockRemovalDo(World world, int x, int y, int z) {
		byte radius = 1;
		int range = radius + 1;

		if (world.checkChunksExist(x - range, y - range, z - range, x + range, y + range, z + range)) {
			for (int xx = -radius; xx <= radius; ++xx) {
				for (int yy = -radius; yy <= radius; ++yy) {
					for (int zz = -radius; zz <= radius; ++zz) {
						int i2 = world.getBlockId(x + xx, y + yy, z + zz);
						if (i2 == this.blockID) {
							int j2 = world.getBlockMetadata(x + xx, y + yy, z + zz);
							world.setBlockMetadata(x + xx, y + yy, z + zz, j2 | 8);
						}
					}
				}
			}
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(!world.isRemote) {
			if(Seasons.activated() && Seasons.currentSeason == Seasons.AUTUMN && rand.nextInt(128 - Seasons.dayOfTheSeason * 4) == 0) this.autumnShit(world, x, y, z);
			
			int metadata = world.getBlockMetadata(x, y, z);

			// Is leaf marked to be checked? Is this leaf not indestructible?
			if((metadata & 8) != 0 && (metadata & 4) == 0) {
				if (this.surroundings == null) {
					this.surroundings = new int[32768];
				}

				if (world.checkChunksExist(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5)) {

					for (int xx = -4; xx <= 4; ++xx) {
						for (int yy = -4; yy <= 4; ++yy) {
							for (int zz = -4; zz <= 4; ++zz) {
								Block block = Block.blocksList[world.getBlockId(x + xx, y + yy, z + zz)];

								if (block == null || block.blockMaterial != Material.wood) {
									if (block instanceof BlockLeaves) {
										this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5) + (zz + 16)] = -2;
									} else {
										this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5) + (zz + 16)] = -1;
									}
								} else {
									this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5) + (zz + 16)] = 0;
								}
							}
						}
					}

					for (int density = 1; density <= 4; ++density) {
						for (int xx = -4; xx <= 4; ++xx) {
							for (int yy = -4; yy <= 4; ++yy) {
								for (int zz = -4; zz <= 4; ++zz) {
									if (this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5) + zz + 16] == density
											- 1) {
										if (this.surroundings[((xx + 16 - 1) << 10) + ((yy + 16) << 5) + zz	+ 16] == -2) {
											this.surroundings[((xx + 16 - 1) << 10) + ((yy + 16) << 5) + zz	+ 16] = density;
										}

										if (this.surroundings[((xx + 16 + 1) << 10) + ((yy + 16) << 5) + zz + 16] == -2) {
											this.surroundings[((xx + 16 + 1) << 10) + ((yy + 16) << 5) + zz	+ 16] = density;
										}

										if (this.surroundings[((xx + 16) << 10) + ((yy + 16 - 1) << 5) + zz	+ 16] == -2) {
											this.surroundings[((xx + 16) << 10) + ((yy + 16 - 1) << 5) + zz	+ 16] = density;
										}

										if (this.surroundings[((xx + 16) << 10) + ((yy + 16 + 1) << 5) + zz	+ 16] == -2) {
											this.surroundings[((xx + 16) << 10) + ((yy + 16 + 1) << 5) + zz	+ 16] = density;
										}

										if (this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5)
												+ (zz + 16 - 1)] == -2) {
											this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5)
													+ (zz + 16 - 1)] = density;
										}

										if (this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5) + zz + 16 + 1] == -2) {
											this.surroundings[((xx + 16) << 10) + ((yy + 16) << 5) + zz + 16 + 1] = density;
										}
									}
								}
							}
						}
					}
				}

				int l2 = this.surroundings[16912];

				if (l2 >= 0) {
					world.setBlockMetadata(x, y, z, metadata & -9); // Clear bit 3
				} else {
					this.removeLeaves(world, x, y, z);
				}
			}
			
		}
	}
	
	private void autumnShit(World world, int x, int y, int z) {
		int i = 0; while (world.getBlockId(x, --y, z) != 0) {  i ++; if (i == 8) return; }
		if(world.isBlockOpaqueCube(x, y - 1, z)) world.setBlock(x, y, z, Block.leafPile.blockID);
	}

	private void removeLeaves(World world1, int i2, int i3, int i4) {
		this.dropBlockAsItem(world1, i2, i3, i4, world1.getBlockMetadata(i2, i3, i4), 0);
		world1.setBlockWithNotify(i2, i3, i4, 0);
		++decays;
	}

	public int quantityDropped(Random random1) {
		return random1.nextInt(20) == 0 ? 1 : 0;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.sapling.blockID;
	}

	public void dropBlockAsItemWithChance(World world1, int i2, int i3, int i4, int i5, float f6, int i7) {
		if(!world1.isRemote) {
			byte b8 = 20;
			if((i5 & 3) == 3) {
				b8 = 40;
			}

			if(world1.rand.nextInt(32) == 0) {
				this.dropBlockAsItem_do(world1, i2, i3, i4, new ItemStack(Item.stick, 1, this.damageDropped(i5)));
			}
			
			if(world1.rand.nextInt(b8) == 0) {
				int i9 = this.idDropped(i5, world1.rand, i7);
				this.dropBlockAsItem_do(world1, i2, i3, i4, new ItemStack(i9, 1, this.damageDropped(i5)));
			}

			if((i5 & 3) == 0 && world1.rand.nextInt(200) == 0) {
				this.dropBlockAsItem_do(world1, i2, i3, i4, new ItemStack(Item.appleRed, 1, 0));
			}
		}

	}

	public void harvestBlock(World world1, EntityPlayer entityPlayer2, int i3, int i4, int i5, int i6) {
		if(!world1.isRemote && entityPlayer2.getCurrentEquippedItem() != null && entityPlayer2.getCurrentEquippedItem().itemID == Item.shears.shiftedIndex) {
			this.dropBlockAsItem_do(world1, i3, i4, i5, new ItemStack(Block.leaves.blockID, 1, i6 & 3));
		} else {
			super.harvestBlock(world1, entityPlayer2, i3, i4, i5, i6);
		}

	}

	public int damageDropped(int i1) {
		return i1 & 3;
	}

	public boolean isOpaqueCube() {
		return !this.graphicsLevel;
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		switch (i2 & 3) {
		case 1: return 132;
		case 2: return 52;
		default: return this.blockIndexInTexture;
		}
	}

	public void setGraphicsLevel(boolean z1) {
		this.graphicsLevel = z1;
		this.blockIndexInTexture = this.baseIndexInPNG + (z1 ? 0 : 1);
	}

	public void onEntityWalking(World world1, int i2, int i3, int i4, Entity entity5) {
		super.onEntityWalking(world1, i2, i3, i4, entity5);
	}
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
	}
}
