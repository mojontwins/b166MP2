package com.mojontwins.utils;

import java.util.Random;

import net.minecraft.world.level.World;

public class CoordUtils {
	public static int getRandomFloor (World world, Random rand, int x, int z) {
		int y = 16 + rand.nextInt (112);
		if (world.getBlockId(x, y, z) != 0) return 0;
		while (world.getBlockId(x, y - 1, z) == 0 && y > 0) {
			y --;
		}
		return y;
	}
}
