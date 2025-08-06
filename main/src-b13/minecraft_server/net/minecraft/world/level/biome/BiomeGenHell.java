package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.tile.Block;

public class BiomeGenHell extends BiomeGenBase {
	public BiomeGenHell(int biomeID) {
		super(biomeID);
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		
		this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 50, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 100, 4, 4));
	}
	
	public byte getTopBlock(Random rand) {
		return (byte)Block.netherrack.blockID;
	}
	
	public byte getFillBlock(Random rand) {
		return (byte)Block.netherrack.blockID;
	}
	
	public int getBiomeFogColor() {
		return 0x330808;
	}
}
