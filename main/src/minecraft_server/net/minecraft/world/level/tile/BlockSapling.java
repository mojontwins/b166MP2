package net.minecraft.world.level.tile;

import java.util.List;
import java.util.Random;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.EnumTreeType;

public class BlockSapling extends BlockFlower {
	
	public int textureId[] = new int[] {
			15, 63, 79, 260
	};
	
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

	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return this.textureId[meta & 3];
	}

	public void growTree(World world, int x, int y, int z, Random rand) {
		int saplingId = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z) & 3;
		
		EnumTreeType tree = EnumTreeType.findTreeTypeFromSapling(new BlockState(Block.blocksList[saplingId], meta));
		WorldGenerator worldGen = tree.getGen(rand);
		
		if (tree.needsFourSaplings) {
			// Check for 4 saplings
			for(int dx = 0; dx >= -1; dx --) {
				for(int dz = 0; dz >= -1; dz --) {
					if(
						this.sameSapling(world, x + dx, y, z + dz, saplingId, meta) &&
						this.sameSapling(world, x + dx + 1, y, z + dz, saplingId, meta) &&
						this.sameSapling(world, x + dx, y, z + dz + 1, saplingId, meta) && 
						this.sameSapling(world, x + dx + 1, y, z + dz + 1, saplingId, meta)) {
						
						world.setBlock(x + dx, y, z + dz, 0);
						world.setBlock(x + dx + 1, y, z + dz, 0);
						world.setBlock(x + dx, y, z + dz + 1, 0);
						world.setBlock(x + dx + 1, y, z + dz + 1, 0);
						
						if(worldGen == null || !worldGen.generate(world, rand, x + dx, y, z + dz)) {
							world.setBlockAndMetadata(x + dx, y, z + dz, saplingId, meta);
							world.setBlockAndMetadata(x + dx + 1, y, z + dz, saplingId, meta);
							world.setBlockAndMetadata(x + dx, y, z + dz + 1, saplingId, meta);
							world.setBlockAndMetadata(x + dx + 1, y, z + dz + 1, saplingId, meta);
						}
						
						break;
					}
				}
			}
		} else {
			world.setBlock(x, y, z, 0);
			if(worldGen == null || !worldGen.generate(world, rand, x, y, z)) {
				world.setBlockAndMetadata(x, y, z, saplingId, meta);
			}
		}

	}

	public boolean sameSapling(World world, int x, int y, int z, int saplingId, int meta) {
		return world.getBlockId(x, y, z) == saplingId &&
				world.getBlockMetadata(x, y, z) == meta;
	}

	public int damageDropped(int i1) {
		return i1 & 3;
	}
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 3));
	}
	
	protected boolean canThisPlantGrowOnThisBlockID(int i1) {
		return super.canThisPlantGrowOnThisBlockID(i1);
	}
}
