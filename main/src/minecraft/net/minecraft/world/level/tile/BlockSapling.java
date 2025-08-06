package net.minecraft.world.level.tile;

import java.util.List;
import java.util.Random;

import com.mojontwins.utils.WorldGenUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.levelgen.feature.WorldGenBigTree;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenForest;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenHugeTrees;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTrees;

public class BlockSapling extends BlockFlower {
	protected BlockSapling(int i1, int i2) {
		super(i1, i2);
		float f3 = 0.4F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, f3 * 2.0F, 0.5F + f3);
		
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(!world.isRemote) {
			super.updateTick(world, x, y, z, rand);
			
			int chance = 7;
			if(Seasons.activated()) {
				if(Seasons.currentSeason == Seasons.WINTER) {
					chance = 10;
				} else if(Seasons.currentSeason == Seasons.SUMMER) {
					chance = 4;
				}
			}
			
			if(world.getBlockLightValue(x, y + 1, z) >= 9 && rand.nextInt(chance) == 0) {
				int meta = world.getBlockMetadata(x, y, z);
				if((meta & 8) == 0) {
					world.setBlockMetadataWithNotify(x, y, z, meta | 8);
				} else {
					this.growTree(world, x, y, z, rand);
				}
			}

		}
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		i2 &= 3;
		return i2 == 1 ? 63 : (i2 == 2 ? 79 : (i2 == 3 ? 30 : super.getBlockTextureFromSideAndMetadata(i1, i2)));
	}

	public void growTree(World world1, int i2, int i3, int i4, Random random5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4) & 3;
		Object object7 = null;
		int i8 = 0;
		int i9 = 0;
		boolean z10 = false;
		if(i6 == 1) {
			object7 = WorldGenUtils.getWorldGenTaiga(random5);
		} else if(i6 == 2) {
			object7 = new WorldGenForest(true);
		} else if(i6 == 3) {
			for(i8 = 0; i8 >= -1; --i8) {
				for(i9 = 0; i9 >= -1; --i9) {
					if(this.func_50076_f(world1, i2 + i8, i3, i4 + i9, 3) && this.func_50076_f(world1, i2 + i8 + 1, i3, i4 + i9, 3) && this.func_50076_f(world1, i2 + i8, i3, i4 + i9 + 1, 3) && this.func_50076_f(world1, i2 + i8 + 1, i3, i4 + i9 + 1, 3)) {
						object7 = new WorldGenHugeTrees(true, 10 + random5.nextInt(20), 3, 3);
						z10 = true;
						break;
					}
				}

				if(object7 != null) {
					break;
				}
			}

			if(object7 == null) {
				i9 = 0;
				i8 = 0;
				object7 = new WorldGenTrees(true, 4 + random5.nextInt(7), 3, 3, false);
			}
		} else {
			object7 = new WorldGenTrees(true);
			if(random5.nextInt(10) == 0) {
				object7 = new WorldGenBigTree(true);
			}
		}

		if(z10) {
			world1.setBlock(i2 + i8, i3, i4 + i9, 0);
			world1.setBlock(i2 + i8 + 1, i3, i4 + i9, 0);
			world1.setBlock(i2 + i8, i3, i4 + i9 + 1, 0);
			world1.setBlock(i2 + i8 + 1, i3, i4 + i9 + 1, 0);
		} else {
			world1.setBlock(i2, i3, i4, 0);
		}

		if(!((WorldGenerator)object7).generate(world1, random5, i2 + i8, i3, i4 + i9)) {
			if(z10) {
				world1.setBlockAndMetadata(i2 + i8, i3, i4 + i9, this.blockID, i6);
				world1.setBlockAndMetadata(i2 + i8 + 1, i3, i4 + i9, this.blockID, i6);
				world1.setBlockAndMetadata(i2 + i8, i3, i4 + i9 + 1, this.blockID, i6);
				world1.setBlockAndMetadata(i2 + i8 + 1, i3, i4 + i9 + 1, this.blockID, i6);
			} else {
				world1.setBlockAndMetadata(i2, i3, i4, this.blockID, i6);
			}
		}

	}

	public boolean func_50076_f(World world1, int i2, int i3, int i4, int i5) {
		return world1.getBlockId(i2, i3, i4) == this.blockID && (world1.getBlockMetadata(i2, i3, i4) & 3) == i5;
	}

	public int damageDropped(int i1) {
		return i1 & 3;
	}
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
	}
	
	protected boolean canThisPlantGrowOnThisBlockID(int i1) {
		return super.canThisPlantGrowOnThisBlockID(i1);
	}
}
