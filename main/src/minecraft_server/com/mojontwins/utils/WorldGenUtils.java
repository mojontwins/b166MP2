package com.mojontwins.utils;

import java.util.Random;

import net.minecraft.world.level.levelgen.feature.WorldGenTaiga1;
import net.minecraft.world.level.levelgen.feature.WorldGenTaiga2;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class WorldGenUtils {
	private static final WorldGenerator[] worldGenTaigas = new WorldGenerator[] {
		new WorldGenTaiga1(),
		new WorldGenTaiga2(),
	};
	
	public static WorldGenerator getWorldGenTaiga(Random rand) {
		return worldGenTaigas[rand.nextInt(worldGenTaigas.length)];
	}
}
