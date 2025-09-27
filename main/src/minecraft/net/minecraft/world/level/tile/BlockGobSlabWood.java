package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.Facing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockGobSlabWood extends Block {
	public BlockGobSlabWood(int par1) {
		super(par1, 6, Material.wood);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		this.setLightOpacity(0);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		boolean flag = (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) != 0;
		if(flag) {
			this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	public void getCollidingBoundingBoxes(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, ArrayList<AxisAlignedBB> par6ArrayList) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public void onBlockPlaced(World par1World, int par2, int par3, int par4, int par5) {
		if(par5 == 0) {
			int i = par1World.getBlockMetadata(par2, par3, par4) & 7;
			par1World.setBlockMetadataWithNotify(par2, par3, par4, i | 8);
		}

	}

	public int quantityDropped(Random par1Random) {
		return 0;
	}

	public int damageDropped(int par1) {
		return par1 & 7;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		if(par5 != 1 && par5 != 0 && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5)) {
			return false;
		} else {
			int i = par2 + Facing.offsetsXForSide[Facing.faceToSide[par5]];
			int j = par3 + Facing.offsetsYForSide[Facing.faceToSide[par5]];
			int k = par4 + Facing.offsetsZForSide[Facing.faceToSide[par5]];
			boolean flag = (par1IBlockAccess.getBlockMetadata(i, j, k) & 8) != 0;
			return !flag ? (par5 == 1 ? true : (par5 == 0 && super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5) ? true : par1IBlockAccess.getBlockId(par2, par3, par4) != this.blockID || (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) != 0)) : (par5 == 0 ? true : (par5 == 1 && super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5) ? true : par1IBlockAccess.getBlockId(par2, par3, par4) != this.blockID || (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) == 0));
		}
	}

	protected ItemStack createStackedBlock(int par1) {
		return new ItemStack(Block.stairSingle.blockID, 1, par1 & 7);
	}
}
