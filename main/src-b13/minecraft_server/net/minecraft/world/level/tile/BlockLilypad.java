package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityBoat;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockLilypad extends Block {

	public BlockLilypad(int id, int blockIndex) {
		super(id, blockIndex, Material.plants);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.015625F, 1.0F);
		
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

    public final boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }

    protected boolean canThisPlantGrowOnThisBlockID(int blockID) {
        return blockID == Block.waterStill.blockID;
    }
    
    // Adapted from Flower:
    public final void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
        super.onNeighborBlockChange(world, x, y, z, blockID);
        this.checkFlowerChange(world, x, y, z);
    }

    public void updateTick(World world, int x, int y, int z, Random rand) {
        this.checkFlowerChange(world, x, y, z);
    }

    private void checkFlowerChange(World world, int x, int y, int z) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockWithNotify(x, y, z, 0);
        }

    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        return this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }

    public final boolean isOpaqueCube() {
        return false;
    }

    public final boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
    	return 23;
    }
    
    public void getCollidingBoundingBoxes(World world1, int i2, int i3, int i4, AxisAlignedBB axisAlignedBB5, ArrayList<AxisAlignedBB> arrayList6, Entity entity) {
    	if(entity == null || !(entity instanceof EntityBoat)) {
			super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
		}
	}
}
