package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockFarmland extends Block {
	protected BlockFarmland(int i1) {
		super(i1, Material.ground);
		this.blockIndexInTexture = 87;
		this.setTickRandomly(true);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
		this.setLightOpacity(255);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return AxisAlignedBB.getBoundingBoxFromPool((double)(i2 + 0), (double)(i3 + 0), (double)(i4 + 0), (double)(i2 + 1), (double)(i3 + 1), (double)(i4 + 1));
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return i1 == 1 && i2 > 0 ? this.blockIndexInTexture - 1 : (i1 == 1 ? this.blockIndexInTexture : 2);
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(!this.isWaterNearby(world1, i2, i3, i4) && !world1.canLightningStrikeAt(i2, i3 + 1, i4)) {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			if(i6 > 0) {
				world1.setBlockMetadataWithNotify(i2, i3, i4, i6 - 1);
			} else if(!this.isCropsNearby(world1, i2, i3, i4)) {
				world1.setBlockWithNotify(i2, i3, i4, Block.dirt.blockID);
			}
		} else {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 7);
		}

	}

	public void onFallenUpon(World world1, int i2, int i3, int i4, Entity entity5, float f6) {
		if(world1.rand.nextFloat() < f6 - 0.5F) {
			world1.setBlockWithNotify(i2, i3, i4, Block.dirt.blockID);
		}

	}

	private boolean isCropsNearby(World world1, int i2, int i3, int i4) {
		byte b5 = 0;

		for(int i6 = i2 - b5; i6 <= i2 + b5; ++i6) {
			for(int i7 = i4 - b5; i7 <= i4 + b5; ++i7) {
				int i8 = world1.getBlockId(i6, i3 + 1, i7);
				if(i8 == Block.crops.blockID /*|| i8 == Block.melonStem.blockID || i8 == Block.pumpkinStem.blockID*/) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isWaterNearby(World world1, int i2, int i3, int i4) {
		for(int i5 = i2 - 4; i5 <= i2 + 4; ++i5) {
			for(int i6 = i3; i6 <= i3 + 1; ++i6) {
				for(int i7 = i4 - 4; i7 <= i4 + 4; ++i7) {
					if(world1.getBlockMaterial(i5, i6, i7) == Material.water) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		super.onNeighborBlockChange(world1, i2, i3, i4, i5);
		Material material6 = world1.getBlockMaterial(i2, i3 + 1, i4);
		if(material6.isSolid()) {
			world1.setBlockWithNotify(i2, i3, i4, Block.dirt.blockID);
		}

	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.dirt.idDropped(0, random2, i3);
	}
}
