package com.mojontwins.utils;

import java.util.Random;

import net.minecraft.world.level.tile.Block;

public class VegetationUtils {
	private static final MetaBlock flowers [] = new MetaBlock [] {
		new MetaBlock(Block.plantYellow.blockID, 0),
		new MetaBlock(Block.plantRed.blockID, 0)
	};
	
	public static MetaBlock pickRandomFlower(Random rand) {
		return flowers[rand.nextInt(flowers.length)];
	}
}
