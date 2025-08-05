package net.minecraft.world.level.biome;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.colorizer.ColorizerFog;
import net.minecraft.world.level.colorizer.ColorizerFoliage;
import net.minecraft.world.level.colorizer.ColorizerGrass;
import net.minecraft.world.level.colorizer.ColorizerWater;
import net.minecraft.world.level.levelgen.ChunkProviderSky;
import net.minecraft.world.level.levelgen.feature.WorldGenBigTree;
import net.minecraft.world.level.levelgen.feature.WorldGenForest;
import net.minecraft.world.level.levelgen.feature.WorldGenSwamp;
import net.minecraft.world.level.levelgen.feature.WorldGenTallGrass;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;

public abstract class BiomeGenBase {
	public static TreeMap<Integer,List<BiomeGenBase>> biomeStructure = null;
	
	public static final BiomeGenBase[] biomeList = new BiomeGenBase[256];

	public static final BiomeGenBase alpha = (new BiomeGenAlpha(23)).setBiomeType(BiomeType.forest).setColor(2250012).setBiomeName("Alpha").setEnableSnow().setMinMaxHeight(0.0F,2.0F).setTemperatureRainfall(0.5F,0.5F);
	public static final BiomeGenBase alphaCold = (new BiomeGenAlphaCold(24)).setBiomeType(BiomeType.veryCold).setColor(2250012).setBiomeName("Alpha Winter").setEnableSnow().setMinMaxHeight(0.0F,2.0F).setTemperatureRainfall(0.1F,0.5F);
	
	public static final BiomeGenBase rainforest = (new BiomeGenRainforest(1)).setColor(588342).setBiomeName("Rainforest").setGrassColor(2094168);
	public static final BiomeGenBase swampland = (new BiomeGenSwamp(2)).setColor(522674).setBiomeName("Swampland").setGrassColor(9154376);
	public static final BiomeGenBase seasonalForest = (new BiomeGenSeasonalForest(3)).setColor(10215459).setBiomeName("Seasonal Forest");
	public static final BiomeGenBase forest = (new BiomeGenForest(4)).setColor(353825).setBiomeName("Forest").setGrassColor(5159473);
	public static final BiomeGenBase savanna = (new BiomeGenSavanna(5)).setColor(14278691).setBiomeName("Savanna");
	public static final BiomeGenBase shrubland = (new BiomeGenShrubland(6)).setColor(10595616).setBiomeName("Shrubland");
	public static final BiomeGenBase taiga = (new BiomeGenTaiga(7)).setColor(3060051).setBiomeName("Taiga").setEnableSnow().setGrassColor(8107825);
	public static final BiomeGenBase desert = (new BiomeGenDesert(8)).setColor(16421912).setBiomeName("Desert").setDisableRain();
	public static final BiomeGenBase plains = (new BiomeGenPlains(9)).setColor(16767248).setBiomeName("Plains");
	public static final BiomeGenBase iceDesert = (new BiomeGenDesert(10)).setColor(16772499).setBiomeName("Ice Desert").setEnableSnow().setDisableRain().setGrassColor(12899129);
	public static final BiomeGenBase tundra = (new BiomeGenTundra(11)).setColor(5762041).setBiomeName("Tundra").setEnableSnow().setGrassColor(12899129);
	public static final BiomeGenBase hell = (new BiomeGenHell(12)).setColor(16711680).setBiomeName("Hell").setDisableRain();
	
	public String biomeName;
	public int color;
	public byte topBlock = (byte)Block.grass.blockID;
	public byte fillerBlock = (byte)Block.dirt.blockID;
	public int biomeColor = 5169201;
	public int unusedBetaGrassColor = 5169201;
	public float minHeight = 0.1F;
	public float maxHeight = 0.3F;
	public float temperature = 0.5F;
	public float rainfall = 0.5F;
	public int waterColorMultiplier = 0xFFFFFF;
	public BiomeDecorator biomeDecorator;
	protected List<SpawnListEntry> spawnableMonsterList = new ArrayList<SpawnListEntry>();
	protected List<SpawnListEntry> spawnableCreatureList = new ArrayList<SpawnListEntry>();
	protected List<SpawnListEntry> spawnableWaterCreatureList = new ArrayList<SpawnListEntry>();
	protected List<SpawnListEntry> spawnableCaveCreatureList = new ArrayList<SpawnListEntry>();
	private boolean enableSnow;
	private boolean enableRain = true;
	public final int biomeID;
	protected WorldGenTrees worldGenTrees = new WorldGenTrees(false);
	protected WorldGenBigTree worldGenBigTree = new WorldGenBigTree(false);
	protected WorldGenForest worldGenForest = new WorldGenForest(false);
	protected WorldGenSwamp worldGenSwamp = new WorldGenSwamp();
	public BiomeType biomeType;
	
	public Weather weather = Weather.normal;

	protected BiomeGenBase(int i1) {
		this.biomeID = i1;
		biomeList[i1] = this;
		
		this.biomeType = BiomeType.stitch;
		
		this.biomeDecorator = this.createBiomeDecorator();
		
		this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 12, 4, 4));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10, 4, 4));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10, 4, 4));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8, 4, 4));
		
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 10, 4, 4));
		
		this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 10, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));

		this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 10, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 4, 4));

		this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));
		
	}
	
	public byte getTopBlock(Random rand) {
		return this.topBlock;
	}
	
	public byte getFillBlock(Random rand) {
		return this.fillerBlock;
	}

	public BiomeGenBase setBiomeType(BiomeType biomeType) {
		this.biomeType = biomeType;
		return this;
	}
	
	protected BiomeDecorator createBiomeDecorator() {
		return new BiomeDecorator(this);
	}

	public BiomeGenBase setTemperatureRainfall(float f1, float f2) {
		/*if(f1 > 0.1F && f1 < 0.2F) {
			throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
		} else*/ {
			this.temperature = f1;
			this.rainfall = f2;
			return this;
		}
	}
	
	public float getTerrainHeightVariance() {
		return this.maxHeight - this.minHeight;
	}

	public BiomeGenBase setMinMaxHeight(float f1, float f2) {
		this.minHeight = f1;
		this.maxHeight = f2;
		return this;
	}

	public BiomeGenBase setDisableRain() {
		this.setEnableRain(false);
		return this;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(10) == 0 ? this.worldGenBigTree : this.worldGenTrees);
	}

	public WorldGenerator getRandomWorldGenForGrass(Random random1) {
		return new WorldGenTallGrass(Block.tallGrass.blockID, 1);
	}

	public BiomeGenBase setEnableSnow() {
		this.enableSnow = true;
		return this;
	}

	public BiomeGenBase setBiomeName(String string1) {
		this.biomeName = string1;
		return this;
	}

	public BiomeGenBase setBiomeColor(int i1) {
		this.biomeColor = i1;
		return this;
	}

	public BiomeGenBase setColor(int i1) {
		this.biomeColor = i1;
		this.color = i1;
		return this;
	}
	
	public BiomeGenBase setGrassColor(int color) {
		this.unusedBetaGrassColor = color;
		return this;
	}

	public int getSkyColorByTemp(float f1) {
		f1 /= 3.0F;
		if(f1 < -1.0F) {
			f1 = -1.0F;
		}

		if(f1 > 1.0F) {
			f1 = 1.0F;
		}

		return Color.getHSBColor(0.62222224F - f1 * 0.05F, 0.5F + f1 * 0.1F, 1.0F).getRGB();
	}

	public List<SpawnListEntry> getSpawnableList(EnumCreatureType enumCreatureType1) {
		return enumCreatureType1 == EnumCreatureType.monster ? 
				this.spawnableMonsterList : 
					(enumCreatureType1 == EnumCreatureType.creature ? 
							this.spawnableCreatureList : 
								(enumCreatureType1 == EnumCreatureType.waterCreature ? 
										this.spawnableWaterCreatureList : 
											(enumCreatureType1 == EnumCreatureType.cave ? 
													this.spawnableCaveCreatureList :
														null)));
	}

	public boolean getEnableSnow() {
		return this.enableSnow;
	}

	public boolean canSpawnLightningBolt() {
		return this.enableSnow ? false : this.isEnableRain();
	}

	public boolean isHighHumidity() {
		return this.rainfall > 0.85F;
	}

	public float getSpawningChance() {
		return 0.1F;
	}

	public final int getIntRainfall() {
		return (int)(this.rainfall * 65536.0F);
	}

	public final int getIntTemperature() {
		return (int)(this.temperature * 65536.0F);
	}

	public final float getFloatRainfall() {
		return this.rainfall;
	}

	public final float getFloatTemperature() {
		return this.temperature;
	}

	public void decorate(World world1, Random random2, int i3, int i4, boolean hadFeature) {
		this.biomeDecorator.decorate(world1, random2, i3, i4, hadFeature);
	}

	public int getBiomeGrassColor() {
		double d1 = (double)MathHelper.clamp_float(this.getFloatTemperature(), 0.0F, 1.0F);
		double d3 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
		return ColorizerGrass.getGrassColor(d1, d3);
	}

	public int getBiomeFoliageColor() {
		double d1 = (double)MathHelper.clamp_float(this.getFloatTemperature(), 0.0F, 1.0F);
		double d3 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
		return ColorizerFoliage.getFoliageColor(d1, d3);
	}

	public int getBiomeWaterColor() {
		double d1 = (double)MathHelper.clamp_float(this.getFloatTemperature(), 0.0F, 1.0F);
		double d3 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
		return ColorizerWater.getWaterColor(d1, d3);
	}
	
	public int getBiomeFogColor() {
		double d1 = (double)MathHelper.clamp_float(this.getFloatTemperature(), 0.0F, 1.0F);
		double d3 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
		return ColorizerFog.getFogColor(d1, d3);
	}
	
	public String toString() {
		return this.biomeName;
	}
	
	public static int makeBiomeHash(BiomeGenBase biome) {
		return (((int)(biome.temperature * 100.0F)) << 8) | (int)(biome.rainfall * 100.0F);
	}
	
	public static float getTemperatureFromHash(Integer hash) {
		return (float)(hash >> 8) / 100.0F;
	}
	
	public static float getRainfallFromHash(Integer hash) {
		return (float)(hash & 0xFF) / 100.0F;
	}

	public static void buildBiomeStructure(BiomeGenBase[] biomesForWorldType) {
		biomeStructure = new TreeMap<Integer,List<BiomeGenBase>> ();
		
		for(BiomeGenBase biome : biomesForWorldType) {
			Integer hash = Integer.valueOf(makeBiomeHash(biome));
			if(!biomeStructure.containsKey(hash)) {
				biomeStructure.put(hash, new ArrayList<BiomeGenBase> ());
			}
			
			biomeStructure.get(hash).add(biome);
		}
		
		printBiomeStructure();
	}
	
	public static void printBiomeStructure() {
		   for (Map.Entry<Integer,List<BiomeGenBase>> entry : biomeStructure.entrySet()) {
		        List<BiomeGenBase> biomeList = entry.getValue();
		        Integer hash= entry.getKey();
		        
		        System.out.print ("[" + hash + "] (" + getTemperatureFromHash(hash) + ", " + getRainfallFromHash(hash) + ") ");
		        for(BiomeGenBase biome : biomeList) {
		        	System.out.print("<" + biome + "> ");
		        }
		        System.out.println();
		   }
	}

	public static int getClosestMatch(float tem, float hum, Random rand) {
		// Finds the closest t/h match in biomeStructure using euclidean distance.
		float minDistanceSq = Float.MAX_VALUE;
		Integer pickedHash = null;
		
		for (Map.Entry<Integer,List<BiomeGenBase>> entry : biomeStructure.entrySet()) {
			Integer hash= entry.getKey();
	        
	        float currentTem = getTemperatureFromHash(hash);
	        float currentHum = getRainfallFromHash(hash);
	        
	        float dt = currentTem - tem;
	        float dh = currentHum - hum;
	        float distanceSq = dt * dt + dh * dh;
	        if(distanceSq < minDistanceSq) {
	        	minDistanceSq = distanceSq;
	        	pickedHash = hash;
	        }
		}
		
		if(pickedHash != null) {
			List<BiomeGenBase> biomeList = biomeStructure.get(pickedHash);
			return biomeList.get(rand.nextInt(biomeList.size())).biomeID;
		} else {
			return alpha.biomeID;
		}
	}
	
	public void replaceBlocksForBiome(
			IChunkProvider generator, World world, Random rand, 
			int chunkX, int chunkZ, int x, int z, 
			byte[] blocks, byte[] metadata, int seaLevel, 
			double sandNoise, double gravelNoise, double stoneNoise
	) {
		float temperature = this.getFloatTemperature();
		int height = (int)(stoneNoise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		
		int stoneHeight = -1;
		byte topBlock = this.getTopBlock(rand);
		byte fillerBlock = this.getFillBlock(rand);

		for(int y = 127; y >= 0; --y) {
			int index = x << 11 | z << 7 | y; // (x * 16 + z) * 128 + y;
			
			if(y <= 0 + rand.nextInt(5) && !(generator instanceof ChunkProviderSky)) {
				blocks[index] = (byte)Block.bedrock.blockID;
			} else {
				byte blockID = blocks[index];
				if(blockID == 0) {
					stoneHeight = -1;
				} else if(blockID == Block.stone.blockID) {
					if(stoneHeight == -1) {
						if(height <= 0) {
							topBlock = 0;
							fillerBlock = (byte)Block.stone.blockID;
						} else if(y >= seaLevel - 4 && y <= seaLevel + 1) {
							topBlock = this.getTopBlock(rand);
							fillerBlock = this.getFillBlock(rand);
						}

						if(y < seaLevel && topBlock == 0) {
							if(temperature < 0.15F) {
								topBlock = (byte)Block.ice.blockID;
							} else {
								topBlock = (byte)Block.waterStill.blockID;
							}
						}

						stoneHeight = height;
						if(y >= seaLevel - 1) {
							blocks[index] = topBlock;
						} else {
							blocks[index] = fillerBlock;
						}
					} else if(stoneHeight > 0) {
						--stoneHeight;
						blocks[index] = fillerBlock;
						if(stoneHeight == 0 && fillerBlock == this.sandstoneGenTriggerer()) {
							stoneHeight = rand.nextInt(4);
							fillerBlock = this.sandstoneGenBlock();
						}
					}
				}
			}
		}
	}
	
	public byte sandstoneGenTriggerer() {
		return (byte)Block.sand.blockID;
	}
	
	public byte sandstoneGenBlock() {
		return (byte)Block.sandStone.blockID;
	}

	public String getPreferedSpawner() {
		return null;
	}

	public int getPreferedSpawnerChance() {
		return 0;
	}

	public int getPreferedSpawnerChanceOffset() {
		return 0;
	}

	public boolean isEnableRain() {
		return enableRain;
	}

	public void setEnableRain(boolean enableRain) {
		this.enableRain = enableRain;
	}
}
