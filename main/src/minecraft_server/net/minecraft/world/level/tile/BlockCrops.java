package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockCrops extends BlockFlower {
	protected BlockCrops(int i1, int i2) {
		super(i1, i2);
		this.blockIndexInTexture = i2;
		this.setTickRandomly(true);
		float f3 = 0.5F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, 0.25F, 0.5F + f3);
	}

	protected boolean canThisPlantGrowOnThisBlockID(int i1) {
		return i1 == Block.tilledField.blockID;
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if(world.getBlockLightValue(x, y + 1, z) >= 9) {
			int meta = world.getBlockMetadata(x, y, z);
			if(meta < 7) {
				float gr = this.getGrowthRate(world, x, y, z); 	// max = 9
				
				if(Seasons.activated()) {
					if(Seasons.currentSeason == Seasons.WINTER) { 
						gr *= .8F;
					} else if(Seasons.currentSeason == Seasons.SUMMER) {
						gr *= 1.2F;
					}
				}
				
				if(rand.nextInt((int)(25.0F / gr) + 1) == 0) {
					++meta;
					world.setBlockMetadataWithNotify(x, y, z, meta);
				}
			}
		}

	}

	public void fertilize(World world1, int i2, int i3, int i4) {
		world1.setBlockMetadataWithNotify(i2, i3, i4, 7);
	}

	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB bb, ArrayList<AxisAlignedBB> bbArray, Entity entity) {
		this.getCollidingBoundingBoxes(world, x, y, z, bb, bbArray);
	}
	
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB bb, ArrayList<AxisAlignedBB> bbArray) {
		AxisAlignedBB axisAlignedBB7 = this.getCollisionBoundingBoxFromPool(world, x, y, z);
		if(axisAlignedBB7 != null && bb.intersectsWith(axisAlignedBB7)) {
			bbArray.add(axisAlignedBB7);
		}

	}	
	
	private float getGrowthRate(World world, int x, int y, int z) {
		float growthRate = 1.0F;
		int bn = world.getBlockId(x, y, z - 1);
		int bs = world.getBlockId(x, y, z + 1);
		int bw = world.getBlockId(x - 1, y, z);
		int be = world.getBlockId(x + 1, y, z);
		int bnw = world.getBlockId(x - 1, y, z - 1);
		int bne = world.getBlockId(x + 1, y, z - 1);
		int bse = world.getBlockId(x + 1, y, z + 1);
		int bsw = world.getBlockId(x - 1, y, z + 1);
		boolean nextz = bw == this.blockID || be == this.blockID;
		boolean nextx = bn == this.blockID || bs == this.blockID;
		boolean nextd = bnw == this.blockID || bne == this.blockID || bse == this.blockID || bsw == this.blockID;

		for(int xx = x - 1; xx <= x + 1; ++xx) {
			for(int zz = z - 1; zz <= z + 1; ++zz) {
				int blockID = world.getBlockId(xx, y - 1, zz);
				float growD = 0.0F;
				
				if(blockID == Block.tilledField.blockID) {
					growD = 1.0F;
					if(world.getBlockMetadata(xx, y - 1, zz) > 0) {
						growD = 3.0F;
					}
				}

				if(xx != x || zz != z) {
					growD /= 4.0F;
				}

				growthRate += growD;
			}
		}

		if(nextd || nextz && nextx) {
			growthRate /= 2.0F;
		}

		return growthRate;
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		if(i2 < 0) {
			i2 = 7;
		}

		return this.blockIndexInTexture + i2;
	}

	public int getRenderType() {
		return 6;
	}

	public void dropBlockAsItemWithChance(World world1, int i2, int i3, int i4, int i5, float f6, int i7) {
		super.dropBlockAsItemWithChance(world1, i2, i3, i4, i5, f6, 0);
		if(!world1.isRemote) {
			int i8 = 3 + i7;

			for(int i9 = 0; i9 < i8; ++i9) {
				if(world1.rand.nextInt(15) <= i5) {
					float f10 = 0.7F;
					float f11 = world1.rand.nextFloat() * f10 + (1.0F - f10) * 0.5F;
					float f12 = world1.rand.nextFloat() * f10 + (1.0F - f10) * 0.5F;
					float f13 = world1.rand.nextFloat() * f10 + (1.0F - f10) * 0.5F;
					EntityItem entityItem14 = new EntityItem(world1, (double)((float)i2 + f11), (double)((float)i3 + f12), (double)((float)i4 + f13), new ItemStack(Item.seeds));
					entityItem14.delayBeforeCanPickup = 10;
					world1.spawnEntityInWorld(entityItem14);
				}
			}

		}
	}

	public int idDropped(int i1, Random random2, int i3) {
		return i1 == 7 ? Item.wheat.shiftedIndex : -1;
	}

	public int quantityDropped(Random random1) {
		return 1;
	}
}
