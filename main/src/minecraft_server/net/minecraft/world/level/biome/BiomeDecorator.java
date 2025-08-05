package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.WorldProviderSkyClassic;
import net.minecraft.world.level.levelgen.feature.WorldGenBigMushroom;
import net.minecraft.world.level.levelgen.feature.WorldGenCactus;
import net.minecraft.world.level.levelgen.feature.WorldGenClay;
import net.minecraft.world.level.levelgen.feature.WorldGenDeadBush;
import net.minecraft.world.level.levelgen.feature.WorldGenFlowers;
import net.minecraft.world.level.levelgen.feature.WorldGenLiquids;
import net.minecraft.world.level.levelgen.feature.WorldGenMinable;
import net.minecraft.world.level.levelgen.feature.WorldGenPumpkin;
import net.minecraft.world.level.levelgen.feature.WorldGenReed;
import net.minecraft.world.level.levelgen.feature.WorldGenSand;
import net.minecraft.world.level.levelgen.feature.WorldGenSandSurface;
import net.minecraft.world.level.levelgen.feature.WorldGenWaterlily;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctavesAlpha;
import net.minecraft.world.level.tile.Block;

public class BiomeDecorator {
	protected World worldObj;
	protected Random rand;
	protected int chunkX;
	protected int chunkZ;
	protected BiomeGenBase biome;
	protected WorldGenerator clayGen = new WorldGenClay(4);
	protected WorldGenerator sandGen = new WorldGenSand(7, Block.sand.blockID);
	protected WorldGenerator surfaceSandGen = new WorldGenSandSurface(8, Block.sand.blockID);
	protected WorldGenerator gravelAsSandGen = new WorldGenSand(6, Block.gravel.blockID);
	protected WorldGenerator dirtGen = new WorldGenMinable(Block.dirt.blockID, 32);
	protected WorldGenerator gravelGen = new WorldGenMinable(Block.gravel.blockID, 32);
	protected WorldGenerator coalGen = new WorldGenMinable(Block.oreCoal.blockID, 16);
	protected WorldGenerator ironGen = new WorldGenMinable(Block.oreIron.blockID, 8);
	protected WorldGenerator goldGen = new WorldGenMinable(Block.oreGold.blockID, 8);
	protected WorldGenerator redstoneGen = new WorldGenMinable(Block.oreRedstone.blockID, 7);
	protected WorldGenerator diamondGen = new WorldGenMinable(Block.oreDiamond.blockID, 7);
	protected WorldGenerator lapisGen = new WorldGenMinable(Block.oreLapis.blockID, 6);
	protected WorldGenerator plantYellowGen = new WorldGenFlowers(Block.plantYellow.blockID);
	protected WorldGenerator plantRedGen = new WorldGenFlowers(Block.plantRed.blockID);
	protected WorldGenerator mushroomBrownGen = new WorldGenFlowers(Block.mushroomBrown.blockID);
	protected WorldGenerator mushroomRedGen = new WorldGenFlowers(Block.mushroomRed.blockID);
	protected WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
	protected WorldGenerator reedGen = new WorldGenReed();
	protected WorldGenerator cactusGen = new WorldGenCactus();
	protected WorldGenerator waterlilyGen = new WorldGenWaterlily();
	protected int waterlilyPerChunk = 0;
	public int extraTreesPerChunk = 0;
	public int flowersPerChunk = 2;
	public int grassPerChunk = 1;
	public int deadBushPerChunk = 0;
	public int mushroomsPerChunk = 0;
	public int reedsPerChunk = 0;
	public int cactiPerChunk = 0;
	public int sandPerChunk = 1;
	public int sandPerChunk2 = 3;
	public int surfaceSandChancePerChunk = 4;
	public int clayPerChunk = 1;
	public int bigMushroomsPerChunk = 0;
	public int pumpkinChance = 32;
	public boolean generateLakes = true;
	public boolean hadFeature = false;
	
	public int maxTerrainHeight = 128;
	
	public boolean skyDecorator = false;
	
	public NoiseGeneratorOctavesAlpha mobSpawnerNoise = null;

	public BiomeDecorator(BiomeGenBase biomeGenBase1) {
		this.biome = biomeGenBase1;
	}

	public void decorate(World world1, Random random2, int i3, int i4, boolean hadFeature) {
		if(this.mobSpawnerNoise == null) {
			this.mobSpawnerNoise = new NoiseGeneratorOctavesAlpha(random2, 8);
		}
		
		if(this.worldObj != null) {
			//throw new RuntimeException("Already decorating!! " + this.biome.getClass());
		} else {
			this.hadFeature = hadFeature;
			this.worldObj = world1;
			this.rand = random2;
			this.chunkX = i3;
			this.chunkZ = i4;
			this.decorate();
			this.worldObj = null;
			this.rand = null;
			this.maxTerrainHeight = world1.getWorldInfo().getTerrainType().getMaxTerrainHeight(world1);
			this.skyDecorator = world1.worldProvider instanceof WorldProviderSkyClassic;
		}
	}

	protected void decorate() {
		if(this.skyDecorator) {
			this.generateOresSky();
		} else {
			this.generateOres();
		}
		
		int i1;
		int i2;
		int i3;
		for(i1 = 0; i1 < this.sandPerChunk2; ++i1) {
			i2 = this.chunkX + this.rand.nextInt(16) + 8;
			i3 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.sandGen.generate(this.worldObj, this.rand, i2, this.worldObj.getTopSolidOrLiquidBlock(i2, i3), i3);
		}

		for(i1 = 0; i1 < this.clayPerChunk; ++i1) {
			i2 = this.chunkX + this.rand.nextInt(16) + 8;
			i3 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.clayGen.generate(this.worldObj, this.rand, i2, this.worldObj.getTopSolidOrLiquidBlock(i2, i3), i3);
		}

		for(i1 = 0; i1 < this.sandPerChunk; ++i1) {
			i2 = this.chunkX + this.rand.nextInt(16) + 8;
			i3 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.sandGen.generate(this.worldObj, this.rand, i2, this.worldObj.getTopSolidOrLiquidBlock(i2, i3), i3);
		}
		
		int i4;
		if(!this.hadFeature) {
			i1 = this.extraTreesPerChunk;
		
			// Here : Add tree noise, if needed (alpha/beta population).
			// For pure Alpha tree density leave extraTreesPerChunk = 0
			
			if(GameRules.boolRule("noiseTreeDensity")) {
				double noiseScaler = 0.5D;
				int treeBaseAttempts = (int)((this.mobSpawnerNoise.generateNoiseOctaves((double)this.chunkX * noiseScaler, (double)this.chunkZ * noiseScaler) / 8.0D + this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
				if(treeBaseAttempts < 0) {
					treeBaseAttempts = 0;
				}
				
				i1 += treeBaseAttempts;
			}
	
			if(this.rand.nextInt(10) == 0) {
				++i1;
			}
	
			for(i2 = 0; i2 < i1; ++i2) {
				i3 = this.chunkX + this.rand.nextInt(16) + 8;
				i4 = this.chunkZ + this.rand.nextInt(16) + 8;
				WorldGenerator worldGenerator5 = this.biome.getRandomWorldGenForTrees(this.rand);
				worldGenerator5.setScale(1.0D, 1.0D, 1.0D);
				worldGenerator5.generate(this.worldObj, this.rand, i3, this.worldObj.getHeightValue(i3, i4), i4);
			}
			
		}
	
		for(i2 = 0; i2 < this.bigMushroomsPerChunk; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.bigMushroomGen.generate(this.worldObj, this.rand, i3, this.worldObj.getHeightValue(i3, i4), i4);
		}

		int i7;
		for(i2 = 0; i2 < this.flowersPerChunk; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.rand.nextInt(this.maxTerrainHeight);
			i7 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.plantYellowGen.generate(this.worldObj, this.rand, i3, i4, i7);
			if(this.rand.nextInt(4) == 0) {
				i3 = this.chunkX + this.rand.nextInt(16) + 8;
				i4 = this.rand.nextInt(this.maxTerrainHeight);
				i7 = this.chunkZ + this.rand.nextInt(16) + 8;
				this.plantRedGen.generate(this.worldObj, this.rand, i3, i4, i7);
			}
		}

		for(i2 = 0; i2 < this.grassPerChunk; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.rand.nextInt(this.maxTerrainHeight);
			i7 = this.chunkZ + this.rand.nextInt(16) + 8;
			WorldGenerator worldGenerator6 = this.biome.getRandomWorldGenForGrass(this.rand);
			worldGenerator6.generate(this.worldObj, this.rand, i3, i4, i7);
		}

		for(i2 = 0; i2 < this.deadBushPerChunk; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.rand.nextInt(this.maxTerrainHeight);
			i7 = this.chunkZ + this.rand.nextInt(16) + 8;
			(new WorldGenDeadBush(Block.deadBush.blockID)).generate(this.worldObj, this.rand, i3, i4, i7);
		}

		for(i2 = 0; i2 < this.waterlilyPerChunk; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.chunkZ + this.rand.nextInt(16) + 8;

			for(i7 = this.rand.nextInt(this.maxTerrainHeight); i7 > 0 && this.worldObj.getBlockId(i3, i7 - 1, i4) == 0; --i7) {
			}

			this.waterlilyGen.generate(this.worldObj, this.rand, i3, i7, i4);
		}

		for(i2 = 0; i2 < this.mushroomsPerChunk; ++i2) {
			if(this.rand.nextInt(4) == 0) {
				i3 = this.chunkX + this.rand.nextInt(16) + 8;
				i4 = this.chunkZ + this.rand.nextInt(16) + 8;
				i7 = this.worldObj.getHeightValue(i3, i4);
				this.mushroomBrownGen.generate(this.worldObj, this.rand, i3, i7, i4);
			}

			if(this.rand.nextInt(8) == 0) {
				i3 = this.chunkX + this.rand.nextInt(16) + 8;
				i4 = this.chunkZ + this.rand.nextInt(16) + 8;
				i7 = this.rand.nextInt(this.maxTerrainHeight);
				this.mushroomRedGen.generate(this.worldObj, this.rand, i3, i7, i4);
			}
		}

		if(this.rand.nextInt(4) == 0) {
			i2 = this.chunkX + this.rand.nextInt(16) + 8;
			i3 = this.rand.nextInt(this.maxTerrainHeight);
			i4 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.mushroomBrownGen.generate(this.worldObj, this.rand, i2, i3, i4);
		}

		if(this.rand.nextInt(8) == 0) {
			i2 = this.chunkX + this.rand.nextInt(16) + 8;
			i3 = this.rand.nextInt(this.maxTerrainHeight);
			i4 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.mushroomRedGen.generate(this.worldObj, this.rand, i2, i3, i4);
		}

		for(i2 = 0; i2 < this.reedsPerChunk; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.chunkZ + this.rand.nextInt(16) + 8;
			i7 = this.rand.nextInt(this.maxTerrainHeight);
			this.reedGen.generate(this.worldObj, this.rand, i3, i7, i4);
		}

		for(i2 = 0; i2 < 10; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.rand.nextInt(this.maxTerrainHeight);
			i7 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.reedGen.generate(this.worldObj, this.rand, i3, i4, i7);
		}

		if(this.pumpkinChance > 0 && this.rand.nextInt(this.pumpkinChance) == 0) {
			i2 = this.chunkX + this.rand.nextInt(16) + 8;
			i3 = this.rand.nextInt(this.maxTerrainHeight);
			i4 = this.chunkZ + this.rand.nextInt(16) + 8;
			(new WorldGenPumpkin()).generate(this.worldObj, this.rand, i2, i3, i4);
		}

		for(i2 = 0; i2 < this.cactiPerChunk; ++i2) {
			i3 = this.chunkX + this.rand.nextInt(16) + 8;
			i4 = this.rand.nextInt(this.maxTerrainHeight);
			i7 = this.chunkZ + this.rand.nextInt(16) + 8;
			this.cactusGen.generate(this.worldObj, this.rand, i3, i4, i7);
		}

		if(this.generateLakes) {
			for(i2 = 0; i2 < 50; ++i2) {
				i3 = this.chunkX + this.rand.nextInt(16) + 8;
				i4 = this.rand.nextInt(this.rand.nextInt(this.maxTerrainHeight - 8) + 8);
				i7 = this.chunkZ + this.rand.nextInt(16) + 8;
				(new WorldGenLiquids(Block.waterMoving.blockID)).generate(this.worldObj, this.rand, i3, i4, i7);
			}

			for(i2 = 0; i2 < 20; ++i2) {
				i3 = this.chunkX + this.rand.nextInt(16) + 8;
				i4 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(this.maxTerrainHeight - 16) + 8) + 8);
				i7 = this.chunkZ + this.rand.nextInt(16) + 8;
				(new WorldGenLiquids(Block.lavaMoving.blockID)).generate(this.worldObj, this.rand, i3, i4, i7);
			}
		}

	}

	protected void genStandardOre1(int i1, WorldGenerator worldGenerator2, int i3, int i4) {
		for(int i5 = 0; i5 < i1; ++i5) {
			int i6 = this.chunkX + this.rand.nextInt(16);
			int i7 = this.rand.nextInt(i4 - i3) + i3;
			int i8 = this.chunkZ + this.rand.nextInt(16);
			worldGenerator2.generate(this.worldObj, this.rand, i6, i7, i8);
		}

	}

	protected void genStandardOre2(int i1, WorldGenerator worldGenerator2, int i3, int i4) {
		for(int i5 = 0; i5 < i1; ++i5) {
			int i6 = this.chunkX + this.rand.nextInt(16);
			int i7 = this.rand.nextInt(i4) + this.rand.nextInt(i4) + (i3 - i4);
			int i8 = this.chunkZ + this.rand.nextInt(16);
			worldGenerator2.generate(this.worldObj, this.rand, i6, i7, i8);
		}

	}

	protected void generateOres() {
		this.genStandardOre1(20, this.dirtGen, 0, 128);
		this.genStandardOre1(10, this.gravelGen, 0, 128);
		this.genStandardOre1(20, this.coalGen, 0, 128);
		this.genStandardOre1(20, this.ironGen, 0, 72);
		this.genStandardOre1(2, this.goldGen, 0, 32);
		this.genStandardOre1(8, this.redstoneGen, 0, 16);
		this.genStandardOre1(1, this.diamondGen, 0, 16);
		if(GameRules.boolRule("generateLapislazuli")) this.genStandardOre2(1, this.lapisGen, 16, 16);
	}
	
	protected void generateOresSky() {
		if(this.rand.nextInt(this.surfaceSandChancePerChunk) == 0) {
			int x = this.chunkX + this.rand.nextInt(16) + 8;
			int z = this.chunkZ + this.rand.nextInt(16) + 8;
			this.surfaceSandGen.generate(this.worldObj, this.rand, x, this.worldObj.getTopSolidOrLiquidBlock(x, z), z);
		}
		
		this.genStandardOre1(20, this.dirtGen, 0, 128);
		this.genStandardOre1(10, this.gravelGen, 0, 128);
		this.genStandardOre1(20, this.sandGen, 0, 128);
		this.genStandardOre1(20, this.coalGen, 0, 128);
		this.genStandardOre1(20, this.ironGen, 0, 72);
		this.genStandardOre1(2, this.goldGen, 0, 32);
		this.genStandardOre1(8, this.redstoneGen, 0, 32);
		this.genStandardOre1(2, this.diamondGen, 0, 32);
		if(GameRules.boolRule("generateLapislazuli")) this.genStandardOre2(1, this.lapisGen, 16, 16);
	}
}
