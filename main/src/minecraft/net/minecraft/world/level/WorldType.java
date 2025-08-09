package net.minecraft.world.level;

import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.levelgen.ChunkProviderAmplified;
import net.minecraft.world.level.levelgen.ChunkProviderBeta;
import net.minecraft.world.level.levelgen.ChunkProviderFlat;
import net.minecraft.world.level.levelgen.ChunkProviderOcean;
import net.minecraft.world.level.levelgen.ChunkProviderSky;

public class WorldType {
	public static final WorldType[] worldTypes = new WorldType[16];
	public static final WorldType DEFAULT = (new WorldType(0, "default", 1, 0)).setVersioned();
	public static final WorldType FLAT = new WorldType(1, "flat");
	public static final WorldType SKY = new WorldType(2, "sky", 1, 0);
	public static final WorldType AMPLIFIED = new WorldType(3, "amplified", 1, 0);	
	public static final WorldType HELL = (new WorldType(4, "hell", 1, -1)).setCanBeCreated(false).disableCreatePortalToTheNether();
	public static final WorldType DEBUG = new WorldType(9, "debug", 1, 0).setCanBeCreated(false);
	public static final WorldType OCEAN = new WorldType(10, "ocean", 1, 0).setCanBeCreated(false);
	
	private final String worldType;
	private final int generatorVersion;
	private boolean canBeCreated;
	private boolean isWorldTypeVersioned;
	public int defaultDimension;
	public boolean canCreatePortalToTheNether = true;

	protected BiomeGenBase[] biomesForWorldType;
	
	private WorldType(int i1, String string2) {
		this(i1, string2, 0, 0);
	}

	private WorldType(int i1, String string2, int i3, int defaultDimension) {
		this.worldType = string2;
		this.generatorVersion = i3;
		this.canBeCreated = true;
		worldTypes[i1] = this;
		this.defaultDimension = defaultDimension;
		
		// This is not used in this build but I'll leave it just in case.
		switch(i1) {
		case 0:
		case 1:
		case 2:
		case 3:
			// This is only used so world type PLAIN doesn't break...
			this.biomesForWorldType = new BiomeGenBase[] {
					BiomeGenBase.plains,
					BiomeGenBase.forest,
					BiomeGenBase.savanna,
					BiomeGenBase.rainforest,
					BiomeGenBase.shrubland,
					BiomeGenBase.taiga,
					BiomeGenBase.seasonalForest,
					BiomeGenBase.iceDesert,
					BiomeGenBase.tundra,
					BiomeGenBase.swampland
			};
			break;
		case 4:
			// This is hell. Add hell biomes here! They will be picked up at random by teh Genlayer
			this.biomesForWorldType = new BiomeGenBase[] {
					BiomeGenBase.hell
			};
			break;
		case 6:
			this.biomesForWorldType = new BiomeGenBase[] { 
					BiomeGenBase.alpha 
			};
			break;
		case 7:
			this.biomesForWorldType = new BiomeGenBase[] { 
					BiomeGenBase.alphaCold
			};
			break;
		case 8:
			this.biomesForWorldType = new BiomeGenBase[] { 
					BiomeGenBase.alpha
				};
			break;
		case 9:
			this.biomesForWorldType = new BiomeGenBase[] { 
					BiomeGenBase.alpha
			};
			break;
		default:
			this.biomesForWorldType = new BiomeGenBase[] { 
					BiomeGenBase.alpha
				};
			
		}
	}

	public BiomeGenBase[] getBiomesForWorldType() {
		return biomesForWorldType;
	}

	public String getWorldTypeName() {
		return this.worldType;
	}

	public String getTranslateName() {
		return "generator." + this.worldType;
	}

	public int getGeneratorVersion() {
		return this.generatorVersion;
	}

	public WorldType func_48629_a(int i1) {
		return this;
	}

	private WorldType setCanBeCreated(boolean z1) {
		this.canBeCreated = z1;
		return this;
	}

	public boolean getCanBeCreated() {
		return this.canBeCreated;
	}

	private WorldType setVersioned() {
		this.isWorldTypeVersioned = true;
		return this;
	}

	public boolean func_48626_e() {
		return this.isWorldTypeVersioned;
	}

	public static WorldType parseWorldType(String string0) {
		
		for(int i1 = 0; i1 < worldTypes.length; ++i1) {
			if(worldTypes[i1] != null && worldTypes[i1].worldType.equalsIgnoreCase(string0)) {
				return worldTypes[i1];
			}
		}

		return null;
	}
	
	public String toString() {
		return /*"WorldType " + */this.worldType;
	}
	
	public WorldChunkManager getChunkManager(World world) {
		return (WorldChunkManager)(this == FLAT ? 
				new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F, 0.5F) 
			: 
				new WorldChunkManagerBeta(world));
	}

	public IChunkProvider getChunkGenerator(World world) {
		return (IChunkProvider)(this == FLAT ? 
				new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled())
			: 	
					this == AMPLIFIED ?
							new ChunkProviderAmplified(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled())
						:
							this == SKY ? 
									new ChunkProviderSky(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled())
								:
									this == OCEAN ? 
											new ChunkProviderOcean(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled())
										:
											new ChunkProviderBeta(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled()));
	}

	public int getSeaLevel(World world) {
		return this == FLAT ? 
				4 
			: 
				64;
	}
	
	public int getMaxTerrainHeight(World world) {
		return this == AMPLIFIED ? 256 : 128;
		
	}

	public boolean hasVoidParticles(boolean var0) {
		return this != FLAT && !var0;
	}

	public double voidFadeMagnitude() {
		return this == FLAT ? 1.0D : 8.0D / 256D;
	}

	public WorldType disableCreatePortalToTheNether() {
		this.canCreatePortalToTheNether = false;
		return this;
	}
}
