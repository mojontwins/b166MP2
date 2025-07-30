package net.minecraft.world.level.biome;

import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.level.SpawnListEntry;

public class BiomeGenHell extends BiomeGenBase {
	public BiomeGenHell(int biomeID) {
		super(biomeID);
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 10, 1, 1));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 10, 8, 8));
	}
}
