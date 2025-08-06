package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockFlower extends Block implements IPlant {
	protected BlockFlower(int i1, int i2, Material material3) {
		super(i1, material3);
		this.blockIndexInTexture = i2;
		//this.setTickRandomly(true);
		float f4 = 0.2F;
		this.setBlockBounds(0.5F - f4, 0.0F, 0.5F - f4, 0.5F + f4, f4 * 3.0F, 0.5F + f4);
		
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	protected BlockFlower(int i1, int i2) {
		this(i1, i2, Material.plants);
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return super.canPlaceBlockAt(world1, i2, i3, i4) && this.canThisPlantGrowOnThisBlockID(world1.getBlockId(i2, i3 - 1, i4));
	}

	protected boolean canThisPlantGrowOnThisBlockID(int i1) {
		if(i1 == Block.tilledField.blockID) return true;
		Block block = Block.blocksList[i1];
		return(block != null && block.canGrowPlants());
		//return i1 == Block.grass.blockID || i1 == Block.dirt.blockID || i1 == Block.tilledField.blockID;
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		super.onNeighborBlockChange(world1, i2, i3, i4, i5);
		this.checkFlowerChange(world1, i2, i3, i4);
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		this.checkFlowerChange(world1, i2, i3, i4);
	}

	protected final void checkFlowerChange(World world1, int i2, int i3, int i4) {
		if(!this.canBlockStay(world1, i2, i3, i4)) {
			this.dropBlockAsItem(world1, i2, i3, i4, world1.getBlockMetadata(i2, i3, i4), 0);
			world1.setBlockWithNotify(i2, i3, i4, 0);
		}

	}

	public boolean canBlockStay(World world1, int i2, int i3, int i4) {
		return (world1.getFullBlockLightValue(i2, i3, i4) >= 8 || world1.canBlockSeeTheSky(i2, i3, i4)) && this.canThisPlantGrowOnThisBlockID(world1.getBlockId(i2, i3 - 1, i4));
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 1;
	}
}
