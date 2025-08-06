package net.minecraft.world.level.tile;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public abstract class BlockDirectional extends Block {
	public int[] metaToFaceSide = new int[] {3, 4, 2, 5};
	public int[] metaToBackSide = new int[] {2, 5, 3, 4};
	
	protected BlockDirectional(int blockID, int blockIndexInTexture, Material material) {
		super(blockID, blockIndexInTexture, material);
	}

	protected BlockDirectional(int blockID, Material material) {
		super(blockID, material);
	}
	
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		meta = BlockDirectional.getDirection(meta);
		
		if(side == 1) {
			return this.getTextureTop();
		} else if(side == 0) {
			return this.getTextureBottom();
		} else if(side == metaToFaceSide[meta]) {
			return this.getTextureFront();
		} else if(side == metaToBackSide[meta]) {
			return this.getTextureBack();
		} else {
			return this.getTextureSide();
		}
	}

	public int getBlockTextureFromSide(int side) {
		switch (side) {
		case 0: return this.getTextureBottom();
		case 1: return this.getTextureTop();
		case 2: return this.getTextureBack();
		case 3: return this.getTextureFront();
		default: return this.getTextureSide();
		}
	}

	public static int getDirection(int metadata) {
		return metadata & 3;
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving) {
		int i6 = MathHelper.floor_double((double)(entityLiving.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, i6);
	}
	
	// Your subclass must implement those:
	public abstract int getTextureTop();

	public abstract int getTextureBottom();

	public abstract int getTextureFront();

	public abstract int getTextureSide();

	public abstract int getTextureBack();
}
