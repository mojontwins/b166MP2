package net.minecraft.world.level.tile;

import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;

public abstract class BlockContainer extends Block {
	protected BlockContainer(int i1, Material material2) {
		super(i1, material2);
		this.isBlockContainer = true;
	}

	protected BlockContainer(int i1, int i2, Material material3) {
		super(i1, i2, material3);
		this.isBlockContainer = true;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		world1.setBlockTileEntity(i2, i3, i4, this.getBlockEntity());
	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		super.onBlockRemoval(world1, i2, i3, i4);
		world1.removeBlockTileEntity(i2, i3, i4);
	}

	public abstract TileEntity getBlockEntity();

	public void powerBlock(World world1, int i2, int i3, int i4, int i5, int i6) {
		super.powerBlock(world1, i2, i3, i4, i5, i6);
		TileEntity tileEntity7 = world1.getBlockTileEntity(i2, i3, i4);
		if(tileEntity7 != null) {
			tileEntity7.onTileEntityPowered(i5, i6);
		}

	}
	
	public TileEntity getBlockEntity(int meta) {
		return this.getBlockEntity();
	}
}
