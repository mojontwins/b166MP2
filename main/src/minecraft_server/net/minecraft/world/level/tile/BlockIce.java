package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockIce extends BlockBreakable {
	public BlockIce(int blockID, int blockIndexInTexture) {
		super(blockID, blockIndexInTexture, Material.ice, false);
		this.slipperiness = 0.98F;
		this.setTickRandomly(true);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int getRenderBlockPass() {
		return 1;
	}

	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return super.shouldSideBeRendered(world, x, y, z, 1 - side);
	}

	public void harvestBlock(World world, EntityPlayer thePlayer, int x, int y, int z, int meta) {
		super.harvestBlock(world, thePlayer, x, y, z, meta);
		Material material = world.getBlockMaterial(x, y - 1, z);
		if(material.blocksMovement() || material.isLiquid()) {
			world.setBlockWithNotify(x, y, z, Block.waterMoving.blockID);
		}

	}

	public int quantityDropped(Random random1) {
		return 0;
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11 - Block.lightOpacity[this.blockID]) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
		}

		// Seasonal
		if(Seasons.activated()) {
			BiomeGenBase biomeGen = world.getBiomeGenForCoords(x, z);
			if(biomeGen.weather != Weather.cold && (Seasons.currentSeason != Seasons.WINTER || biomeGen.weather == Weather.desert)) {
				world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
				
				if(world.getBlockId(x - 1, y, z) == this.blockID) world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
				if(world.getBlockId(x + 1, y, z) == this.blockID) world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
				if(world.getBlockId(x, y, z) == this.blockID) world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
				if(world.getBlockId(x, y, z - 1) == this.blockID) world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
			}
		}
	}

	public int getMobilityFlag() {
		return 0;
	}

	protected ItemStack createStackedBlock(int damage) {
		return null;
	}
	
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return Block.waterStill.colorMultiplier(world, x, y, z);
	}
}
