package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockLeafPile extends Block {

	public BlockLeafPile(int i1) {
		super(i1, 13 * 16 + 2, Material.leaves);
		this.setBlockBounds(0.0F, 0.05F, 0.0F, 1.0F, 0.06F, 1.0F);
		
		//this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.05F, 0.0F, 1.0F, 0.06F, 1.0F);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
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
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block block = Block.blocksList[world.getBlockId(x, y - 1, z)];
		if(block == null) return false;
		if(!block.isOpaqueCube()) return false;
		if(block == Block.sand ) return false;
		return world.getBlockMaterial(x, y - 1, z).isSolid();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		this.canLeavesStay(world, x, y, z);
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canPlaceBlockAt(world, x, y, z);
	}

	@Override
	public int getRenderType() {
		return 23;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(Seasons.activated() && Seasons.currentSeason != Seasons.AUTUMN) world.setBlock(x, y, z, 0);
	}
	
	private boolean canLeavesStay(World world, int x, int y, int z) {
		if(!this.canPlaceBlockAt(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockWithNotify(x, y, z, 0);
			return false;
		} else {
			return true;
		}
	}

}
