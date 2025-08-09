package com.mojontwins.utils;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFlower;

public class BlockUtils {
	public static boolean isLeaves(int blockID) {
		return blockID == Block.leaves.blockID  ;
	}
	
	public static boolean isWood(int blockID) {
		return blockID == Block.wood.blockID;
	}
	
	public static boolean isSapling(int blockID) {
		return blockID == Block.sapling.blockID;
	}
	
	public static boolean isGrass(int blockID) {
		Block block = Block.blocksList[blockID];
		if(block == null) return false;
		return block instanceof BlockFlower; 
	}
	
	public static boolean isVegetation(int blockID) {
		return isLeaves(blockID) || isWood(blockID) || isSapling(blockID) || isGrass(blockID) || blockID == Block.cactus.blockID || blockID == Block.reed.blockID;
	}
	
	public static boolean canBeReplacedByLeaves(int blockID) {
		return blockID == 0 || isLeaves(blockID) || isGrass(blockID);
	}
	
	public static boolean canBeReplacedByWood(int blockID) {
		return blockID == 0 || isLeaves(blockID);
	}

	public static short metaBlockAsEncodedShort(MetaBlock metaBlock) {
		return (short)(metaBlock.blockID() | (metaBlock.metadata() << 8));
	}

	public static boolean isBlockFreezable(int blockID) {
		return blockID == Block.waterMoving.blockID || blockID == Block.waterStill.blockID;
	}

	public static boolean isNetherSoil(int blockID) {
		return false;
	}
	
	public static boolean isSoil(int blockID) {
		Block block = Block.blocksList[blockID];
		if(block == null) return false;
		return block.blockMaterial == Material.grass || block.blockMaterial == Material.ground || block.blockMaterial == Material.rock;
	}
	
	public static boolean isTree(int blockID) {
		return isLeaves(blockID) || isWood(blockID);
	}
	
	public static boolean isWater(int blockID) {
		return blockID == Block.waterStill.blockID || blockID == Block.waterMoving.blockID;
	}
}
