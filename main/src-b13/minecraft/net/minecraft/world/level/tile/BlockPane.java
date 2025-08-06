package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockPane extends Block {
	private int sideTextureIndex;
	private final boolean canDropItself;

	protected BlockPane(int id, int blockIndexInTexture, int sideTextureIndex, Material baseMaterial, boolean canDropItself) {
		super(id, blockIndexInTexture, baseMaterial);
		this.sideTextureIndex = sideTextureIndex;
		this.canDropItself = canDropItself;
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	@Override
	public int idDropped(int i1, Random random2, int i3) {
		return this.canDropItself ? this.blockID : -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 18;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		//if (this.blockID == Block.thinPlanks.blockID) return true;
		int i6 = iBlockAccess1.getBlockId(i2, i3, i4);
		return i6 == this.blockID ? false : super.shouldSideBeRendered(iBlockAccess1, i2, i3, i4, i5);
	}

	@Override
	public void getCollidingBoundingBoxes(World world1, int i2, int i3, int i4, AxisAlignedBB axisAlignedBB5, ArrayList<AxisAlignedBB> arrayList6) {
		boolean z7 = this.canThisPaneConnectToThisBlockID(world1.getBlockId(i2, i3, i4 - 1));
		boolean z8 = this.canThisPaneConnectToThisBlockID(world1.getBlockId(i2, i3, i4 + 1));
		boolean z9 = this.canThisPaneConnectToThisBlockID(world1.getBlockId(i2 - 1, i3, i4));
		boolean z10 = this.canThisPaneConnectToThisBlockID(world1.getBlockId(i2 + 1, i3, i4));
		if(z9 && z10 || !z9 && !z10 && !z7 && !z8) {
			this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
			super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
		} else if(z9 && !z10) {
			this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
			super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
		} else if(!z9 && z10) {
			this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
			super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
		}

		if((!z7 || !z8) && (z9 || z10 || z7 || z8)) {
			if(z7 && !z8) {
				this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
				super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
			} else if(!z7 && z8) {
				this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
			}
		} else {
			this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
			super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
		}

	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		float f5 = 0.4375F;
		float f6 = 0.5625F;
		float f7 = 0.4375F;
		float f8 = 0.5625F;
		boolean z9 = this.canThisPaneConnectToThisBlockID(iBlockAccess1.getBlockId(i2, i3, i4 - 1));
		boolean z10 = this.canThisPaneConnectToThisBlockID(iBlockAccess1.getBlockId(i2, i3, i4 + 1));
		boolean z11 = this.canThisPaneConnectToThisBlockID(iBlockAccess1.getBlockId(i2 - 1, i3, i4));
		boolean z12 = this.canThisPaneConnectToThisBlockID(iBlockAccess1.getBlockId(i2 + 1, i3, i4));
		if(z11 && z12 || !z11 && !z12 && !z9 && !z10) {
			f5 = 0.0F;
			f6 = 1.0F;
		} else if(z11 && !z12) {
			f5 = 0.0F;
		} else if(!z11 && z12) {
			f6 = 1.0F;
		}

		if((!z9 || !z10) && (z11 || z12 || z9 || z10)) {
			if(z9 && !z10) {
				f7 = 0.0F;
			} else if(!z9 && z10) {
				f8 = 1.0F;
			}
		} else {
			f7 = 0.0F;
			f8 = 1.0F;
		}

		this.setBlockBounds(f5, 0.0F, f7, f6, 1.0F, f8);
	}

	public int getSideTextureIndex() {
		return this.sideTextureIndex;
	}

	public final boolean canThisPaneConnectToThisBlockID(int i1) {
		return Block.opaqueCubeLookup[i1] || i1 == this.blockID || i1 == Block.glass.blockID;
	}
}
