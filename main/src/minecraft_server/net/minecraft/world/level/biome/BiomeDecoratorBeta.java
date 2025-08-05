package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenClayBeta;
import net.minecraft.world.level.levelgen.feature.WorldGenDeadBush;
import net.minecraft.world.level.levelgen.feature.WorldGenLiquids;
import net.minecraft.world.level.levelgen.feature.WorldGenMinable;
import net.minecraft.world.level.levelgen.feature.WorldGenPumpkin;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctavesBeta;
import net.minecraft.world.level.tile.Block;

public class BiomeDecoratorBeta extends BiomeDecorator {

	public BiomeDecoratorBeta(BiomeGenBase biome) {
		super(biome);
		this.clayGen = new WorldGenClayBeta(32);
	}

	public void decorate(World world, Random rand, int chunkX, int chunkZ, boolean hadFeature) {
		if(this.mobSpawnerNoise == null) {
			this.mobSpawnerNoise = new NoiseGeneratorOctavesBeta(rand, 8);
		}
		
		if(this.worldObj != null) {
			//throw new RuntimeException("Already decorating!! " + this.biome.getClass());
		} else {
			this.hadFeature = hadFeature;
			this.worldObj = world;
			this.rand = rand;
			this.chunkX = chunkX;
			this.chunkZ = chunkZ;
			this.decorate();
			this.worldObj = null;
			this.rand = null;
			this.maxTerrainHeight = world.getWorldInfo().getTerrainType().getMaxTerrainHeight(world);
		}
	}

	protected void decorate() {
		// In beta, clay is generated first
		int i, x, y, z;
		
		for(i = 0; i < this.clayPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.clayGen.generate(this.worldObj, this.rand, x, this.worldObj.getTopSolidOrLiquidBlock(x, z), z);
		}
		
		// Now blobs of dirt & gravel
		
		for(i = 0; i < 20; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(128);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.dirt.blockID, 32)).generate(this.worldObj, this.rand, x, y, z);
		}

		for(i = 0; i < 10; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(128);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.gravel.blockID, 32)).generate(this.worldObj, this.rand, x, y, z);
		}
		
		// Coal, iron, gold, redstone, diamond, lapìs
		
		for(i = 0; i < 20; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(128);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreCoal.blockID, 16)).generate(this.worldObj, this.rand, x, y, z);
		}

		for(i = 0; i < 20; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(64);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreIron.blockID, 8)).generate(this.worldObj, this.rand, x, y, z);
		}

		for(i = 0; i < 2; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(32);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreGold.blockID, 8)).generate(this.worldObj, this.rand, x, y, z);
		}

		for(i = 0; i < 8; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(16);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreRedstone.blockID, 7)).generate(this.worldObj, this.rand, x, y, z);
		}

		for(i = 0; i < 1; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(16);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreDiamond.blockID, 7)).generate(this.worldObj, this.rand, x, y, z);
		}

		for(i = 0; i < 1; ++i) {
			x = this.chunkX + this.rand.nextInt(16);
			y = this.rand.nextInt(16) + this.rand.nextInt(16);
			z = this.chunkZ + this.rand.nextInt(16);
			(new WorldGenMinable(Block.oreLapis.blockID, 6)).generate(this.worldObj, this.rand, x, y, z);
		}

		// Noise based tree density
		
		if(!this.hadFeature) {
			int extraTrees = this.extraTreesPerChunk;
		
			// Here : Add tree noise, if needed (alpha/beta population).
			// For pure Alpha tree density leave extraTreesPerChunk = 0
			
			if(GameRules.boolRule("noiseTreeDensity")) {
				double noiseScaler = 0.5D;
				int treeBaseAttempts = (int)((this.mobSpawnerNoise.getDensity((double)this.chunkX * noiseScaler, (double)this.chunkZ * noiseScaler) / 8.0D + this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
				if(treeBaseAttempts < 0) {
					treeBaseAttempts = 0;
				}
				
				extraTrees += treeBaseAttempts;
			}
	
			if(this.rand.nextInt(10) == 0) {
				++extraTrees;
			}
	
			for(i = 0; i < extraTrees; ++i) {
				x = this.chunkX + this.rand.nextInt(16) + 8;
				z = this.chunkZ + this.rand.nextInt(16) + 8;
				WorldGenerator treeGen = this.biome.getRandomWorldGenForTrees(this.rand);
				treeGen.setScale(1.0D, 1.0D, 1.0D);
				treeGen.generate(this.worldObj, this.rand, x, this.worldObj.getHeightValue(x, z), z);
			}
			
		}
		
		// Plant yellow/red
		
		for(i = 0; i < this.flowersPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.plantYellowGen.generate(this.worldObj, this.rand, x, y, z);
			if(this.rand.nextInt(4) == 0) {
				x = this.chunkX + this.rand.nextInt(16) + 8;
				y = this.rand.nextInt(this.maxTerrainHeight);
				z = this.chunkZ + this.rand.nextInt(16) + 8;
				this.plantRedGen.generate(this.worldObj, this.rand, x, y, z);
			}
		}
		
		// Tall grass

		for(i = 0; i < this.grassPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			WorldGenerator grassGen = this.biome.getRandomWorldGenForGrass(this.rand);
			grassGen.generate(this.worldObj, this.rand, x, y, z);
		}
	
		// Dead bush
		
		for(i = 0; i < this.deadBushPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			(new WorldGenDeadBush(Block.deadBush.blockID)).generate(this.worldObj, this.rand, x, y, z);
		}
		
		// Surface Mushroom Brown / Red
		
		for(i = 0; i < this.mushroomsPerChunk; ++i) {
			if(this.rand.nextInt(4) == 0) {
				x = this.chunkX + this.rand.nextInt(16) + 8;
				z = this.chunkZ + this.rand.nextInt(16) + 8;
				y = this.worldObj.getHeightValue(x, z);
				this.mushroomBrownGen.generate(this.worldObj, this.rand, x, y, z);
			}

			if(this.rand.nextInt(8) == 0) {
				x = this.chunkX + this.rand.nextInt(16) + 8;
				z = this.chunkZ + this.rand.nextInt(16) + 8;
				y = this.rand.nextInt(this.maxTerrainHeight);
				this.mushroomRedGen.generate(this.worldObj, this.rand, x, y, z);
			}
		}
		
		// Cave Mushrooms
		
		if(this.rand.nextInt(4) == 0) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.mushroomBrownGen.generate(this.worldObj, this.rand, x, y, z);
		}

		if(this.rand.nextInt(8) == 0) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.mushroomRedGen.generate(this.worldObj, this.rand, x, y, z);
		}
		
		// Reeds
		
		for(i = 0; i < this.reedsPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			this.reedGen.generate(this.worldObj, this.rand, x, y, z);
		}

		for(i = 0; i < 10; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.reedGen.generate(this.worldObj, this.rand, x, y, z);
		}
		
		// Pumpkin
		
		if(this.pumpkinChance > 0 && this.rand.nextInt(this.pumpkinChance) == 0) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			(new WorldGenPumpkin()).generate(this.worldObj, this.rand, x, y, z);
		}
		
		// Cacti
		
		for(i = 0; i < this.cactiPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.maxTerrainHeight);
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.cactusGen.generate(this.worldObj, this.rand, x, y, z);
		}
		
		// Big mushrooms
		
		for(i = 0; i < this.bigMushroomsPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.bigMushroomGen.generate(this.worldObj, this.rand, x, this.worldObj.getHeightValue(x, z), z);
		}

		// Waterlily

		for(i = 0; i < this.waterlilyPerChunk; ++i) {
			x = this.chunkX + this.rand.nextInt(16) + 8;
			z = this.chunkZ + this.rand.nextInt(16) + 8;

			for(y = this.rand.nextInt(this.maxTerrainHeight); y > 0 && this.worldObj.getBlockId(x, y - 1, z) == 0; --y) {
			}

			this.waterlilyGen.generate(this.worldObj, this.rand, x, y, z);
		}

		// Liquids

		if(this.generateLakes) {
			for(i = 0; i < 50; ++i) {
				x = this.chunkX + this.rand.nextInt(16) + 8;
				y = this.rand.nextInt(this.rand.nextInt(this.maxTerrainHeight - 8) + 8);
				z = this.chunkZ + this.rand.nextInt(16) + 8;
				(new WorldGenLiquids(Block.waterMoving.blockID)).generate(this.worldObj, this.rand, x, y, z);
			}

			for(i = 0; i < 20; ++i) {
				x = this.chunkX + this.rand.nextInt(16) + 8;
				y = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(this.maxTerrainHeight - 16) + 8) + 8);
				z = this.chunkZ + this.rand.nextInt(16) + 8;
				(new WorldGenLiquids(Block.lavaMoving.blockID)).generate(this.worldObj, this.rand, x, y, z);
			}
		}

	}
	
}
