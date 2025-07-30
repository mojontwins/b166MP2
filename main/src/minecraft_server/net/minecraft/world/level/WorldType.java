package net.minecraft.world.level;

import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.levelgen.ChunkProviderAlpha;
import net.minecraft.world.level.levelgen.ChunkProviderAmplified;
import net.minecraft.world.level.levelgen.ChunkProviderFlat;
import net.minecraft.world.level.levelgen.ChunkProviderGenerate;
import net.minecraft.world.level.levelgen.ChunkProviderInfdev;
import net.minecraft.world.level.levelgen.ChunkProviderOcean;
import net.minecraft.world.level.levelgen.ChunkProviderSky;

public class WorldType {
	public static final WorldType[] worldTypes = new WorldType[16];
	public static final WorldType DEFAULT = (new WorldType(0, "default", 1, 0)).setVersioned();
	public static final WorldType FLAT = new WorldType(1, "flat");
	public static final WorldType SKY = new WorldType(2, "sky", 1, 0);
	public static final WorldType AMPLIFIED = new WorldType(3, "amplified", 1, 0);	
	public static final WorldType HELL = (new WorldType(4, "hell", 1, -1)).setCanBeCreated(false).disableCreatePortalToTheNether();
	public static final WorldType INFDEV = new WorldType(5, "infdev", 1, 0);
	public static final WorldType ALPHA = new WorldType(6, "alpha", 1, 0);
	public static final WorldType ALPHA_SNOW = new WorldType(7, "alpha_snow", 1, 0);
	public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0, 0)).setCanBeCreated(false);
	public static final WorldType DEBUG = new WorldType(9, "debug", 1, 0).setCanBeCreated(false);
	public static final WorldType OCEAN = new WorldType(10, "ocean", 1, 0);
	
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
		
		switch(i1) {
		case 4:
			this.biomesForWorldType = new BiomeGenBase[] {
					BiomeGenBase.alpha
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
		return this == DEFAULT && i1 == 0 ? DEFAULT_1_1 : this;
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
		return "WorldType " + this.worldType;
	}
	
	public WorldChunkManager getChunkManager(World world) {
		return (WorldChunkManager)(this == FLAT ? 
				new WorldChunkManagerHell(BiomeGenBase.alpha, 0.5F, 0.5F) 
			: 
				new WorldChunkManager(world));
	}

	public IChunkProvider getChunkGenerator(World world) {
		return (IChunkProvider)(this == FLAT ? 
				new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled())
			: 	
					this == INFDEV ?
							new ChunkProviderInfdev(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled())
						:
							this == ALPHA || this == ALPHA_SNOW ?
									new ChunkProviderAlpha(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled())
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
														new ChunkProviderGenerate(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled()));
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
