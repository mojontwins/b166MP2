package net.minecraft.world.level.tile;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockTotem extends Block {
	public BlockTotem(int i, int j) {
		super(i, Material.wood);
		this.blockIndexInTexture = j;
	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		return i < 2 ? this.blockIndexInTexture + 1: this.blockIndexInTexture;
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
	}

	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		int l = MathHelper.floor_double((double)(entityliving.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(i, j, k, l);
	}
}
