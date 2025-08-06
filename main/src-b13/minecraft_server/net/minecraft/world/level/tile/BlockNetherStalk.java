package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.biome.BiomeGenHell;

public class BlockNetherStalk extends BlockFlower {
	protected BlockNetherStalk(int i1) {
		super(i1, 226);
		this.setTickRandomly(true);
		float f2 = 0.5F;
		this.setBlockBounds(0.5F - f2, 0.0F, 0.5F - f2, 0.5F + f2, 0.25F, 0.5F + f2);
	}

	protected boolean canThisPlantGrowOnThisBlockID(int i1) {
		return i1 == Block.slowSand.blockID;
	}

	public boolean canBlockStay(World world1, int i2, int i3, int i4) {
		return this.canThisPlantGrowOnThisBlockID(world1.getBlockId(i2, i3 - 1, i4));
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		if(i6 < 3) {
			BiomeGenBase biomeGenBase7 = world1.getBiomeGenForCoords(i2, i4);
			if(biomeGenBase7 instanceof BiomeGenHell && random5.nextInt(10) == 0) {
				++i6;
				world1.setBlockMetadataWithNotify(i2, i3, i4, i6);
			}
		}

		super.updateTick(world1, i2, i3, i4, random5);
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return i2 >= 3 ? this.blockIndexInTexture + 2 : (i2 > 0 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture);
	}

	public int getRenderType() {
		return 6;
	}

	public void dropBlockAsItemWithChance(World world1, int i2, int i3, int i4, int i5, float f6, int i7) {
		if(!world1.isRemote) {
			int i8 = 1;
			if(i5 >= 3) {
				i8 = 2 + world1.rand.nextInt(3);
				if(i7 > 0) {
					i8 += world1.rand.nextInt(i7 + 1);
				}
			}

			for(int i9 = 0; i9 < i8; ++i9) {
				this.dropBlockAsItem_do(world1, i2, i3, i4, new ItemStack(Item.netherStalkSeeds));
			}

		}
	}

	public int idDropped(int i1, Random random2, int i3) {
		return 0;
	}

	public int quantityDropped(Random random1) {
		return 0;
	}
}
