package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockSnow extends Block {
	protected BlockSnow(int blockID, int blockIndexInTexture) {
		super(blockID, blockIndexInTexture, Material.snow);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.setTickRandomly(true);
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int i5 = world.getBlockMetadata(x, y, z) & 7;
		return i5 >= 3 ? AxisAlignedBB.getBoundingBoxFromPool((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)((float)y + 0.5F), (double)z + this.maxZ) : null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z) & 7;
		float b = (float)(2 * (1 + meta)) / 16.0F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, b, 1.0F);
	}

	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		Block below = world.getBlock(x, y - 1, z);
		
		return 
				(block == null || block.blockMaterial.isGroundCover()) &&
				below != null && (below == Block.leaves || below.isOpaqueCube()) ? below.blockMaterial.blocksMovement() : false;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int i5) {
		this.canSnowStay(world, x, y, z);
	}

	private boolean canSnowStay(World world, int x, int y, int z) {
		if(!this.canPlaceBlockAt(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockWithNotify(x, y, z, 0);
			return false;
		} else {
			return true;
		}
	}

	public void harvestBlock(World world, EntityPlayer thePlayer, int x, int y, int z, int meta) {
		int snowballId = Item.snowball.shiftedIndex;
		float r = 0.7F;
		double dx = (double)(world.rand.nextFloat() * r) + (double)(1.0F - r) * 0.5D;
		double dy = (double)(world.rand.nextFloat() * r) + (double)(1.0F - r) * 0.5D;
		double dz = (double)(world.rand.nextFloat() * r) + (double)(1.0F - r) * 0.5D;
		EntityItem entityItem = new EntityItem(world, (double)x + dx, (double)y + dy, (double)z + dz, new ItemStack(snowballId, 1, 0));
		entityItem.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(entityItem);
		world.setBlockWithNotify(x, y, z, 0);
	}

	public int idDropped(int meta, Random rand, int fortune) {
		return Item.snowball.shiftedIndex;
	}

	public int quantityDropped(Random random1) {
		return 0;
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockWithNotify(x, y, z, 0);
		}

		// Seasonal
		if(Seasons.activated()) {
			BiomeGenBase biomeGen = world.getBiomeGenForCoords(x, z);
			if(biomeGen.weather != Weather.cold && (Seasons.currentSeason != Seasons.WINTER || biomeGen.weather == Weather.desert)) {
				world.setBlockWithNotify(x, y, z, 0);
				
				if(world.getBlockId(x - 1, y, z) == this.blockID) world.setBlockWithNotify(x, y, z, 0);
				if(world.getBlockId(x + 1, y, z) == this.blockID) world.setBlockWithNotify(x, y, z, 0);
				if(world.getBlockId(x, y, z) == this.blockID) world.setBlockWithNotify(x, y, z, 0);
				if(world.getBlockId(x, y, z - 1) == this.blockID) world.setBlockWithNotify(x, y, z, 0);
			}
		}
	}

	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return side == 1 ? true : super.shouldSideBeRendered(world, x, y, z, side);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		entity.setInSnow();
	}
}
