package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntitySign;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockSign extends BlockContainer {
	private Class<?> signEntityClass;
	private boolean isFreestanding;

	protected BlockSign(int i1, Class<?> class2, boolean z3) {
		super(i1, Material.wood);
		this.isFreestanding = z3;
		this.blockIndexInTexture = 4;
		this.signEntityClass = class2;
		float f4 = 0.25F;
		float f5 = 1.0F;
		this.setBlockBounds(0.5F - f4, 0.0F, 0.5F - f4, 0.5F + f4, f5, 0.5F + f4);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		return super.getSelectedBoundingBoxFromPool(world1, i2, i3, i4);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		if(!this.isFreestanding) {
			int i5 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
			float f6 = 0.28125F;
			float f7 = 0.78125F;
			float f8 = 0.0F;
			float f9 = 1.0F;
			float f10 = 0.125F;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			if(i5 == 2) {
				this.setBlockBounds(f8, f6, 1.0F - f10, f9, f7, 1.0F);
			}

			if(i5 == 3) {
				this.setBlockBounds(f8, f6, 0.0F, f9, f7, f10);
			}

			if(i5 == 4) {
				this.setBlockBounds(1.0F - f10, f6, f8, 1.0F, f7, f9);
			}

			if(i5 == 5) {
				this.setBlockBounds(0.0F, f6, f8, f10, f7, f9);
			}

		}
	}

	public int getRenderType() {
		return -1;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return true;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public TileEntity getBlockEntity() {
		try {
			return (TileEntity)this.signEntityClass.newInstance();
		} catch (Exception exception2) {
			throw new RuntimeException(exception2);
		}
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Item.sign.shiftedIndex;
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		boolean z6 = false;
		if(this.isFreestanding) {
			if(!world1.getBlockMaterial(i2, i3 - 1, i4).isSolid()) {
				z6 = true;
			}
		} else {
			int i7 = world1.getBlockMetadata(i2, i3, i4);
			z6 = true;
			if(i7 == 2 && world1.getBlockMaterial(i2, i3, i4 + 1).isSolid()) {
				z6 = false;
			}

			if(i7 == 3 && world1.getBlockMaterial(i2, i3, i4 - 1).isSolid()) {
				z6 = false;
			}

			if(i7 == 4 && world1.getBlockMaterial(i2 + 1, i3, i4).isSolid()) {
				z6 = false;
			}

			if(i7 == 5 && world1.getBlockMaterial(i2 - 1, i3, i4).isSolid()) {
				z6 = false;
			}
		}

		if(z6) {
			this.dropBlockAsItem(world1, i2, i3, i4, world1.getBlockMetadata(i2, i3, i4), 0);
			world1.setBlockWithNotify(i2, i3, i4, 0);
		}

		super.onNeighborBlockChange(world1, i2, i3, i4, i5);
	}
	
	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		if (entityPlayer.getCurrentEquippedItem() != null && entityPlayer.getCurrentEquippedItem().itemID == Item.feather.shiftedIndex) {
			TileEntitySign sign = (TileEntitySign)world.getBlockTileEntity(x, y, z);
			if(sign != null) {
				entityPlayer.displayGUIEditSign(sign);
			}
		} 
		
		return true;
	}
}
