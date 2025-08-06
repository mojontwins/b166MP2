package net.minecraft.world.level.biome;

public class RetroBiomeLookup {
	private static BiomeGenBase[] biomeLookupTable = new BiomeGenBase[4096];
	
	public static BiomeGenBase getBiome(float temperature, float humidity) {
		humidity *= temperature;
		return temperature < 0.1F ? 
				BiomeGenBase.tundra 
			: 
				(
						humidity < 0.2F ? 
					(
							temperature < 0.5F ? 
									BiomeGenBase.tundra 
								: 
									(
											temperature < 0.95F ? 
													BiomeGenBase.savanna 
												: 
													BiomeGenBase.desert
									)
					) 
				: 
					(
							humidity > 0.5F && temperature < 0.7F ? 
									BiomeGenBase.swampland 
								: 
									(
											temperature < 0.5F ? 
													BiomeGenBase.taiga 
												: 
													(
															temperature < 0.97F ? 
																	(
																			humidity < 0.35F ? 
																					BiomeGenBase.shrubland 
																				: 
																					BiomeGenBase.forest
																	) 
																: 
																	(
																			humidity < 0.45F ? 
																					BiomeGenBase.plains 
																				: 
																					(
																							humidity < 0.9F ? 
																									BiomeGenBase.seasonalForest 
																								: 
																									BiomeGenBase.rainforest
																					)
																	)
													)
										)
					)
		);
	}
	
	public static BiomeGenBase getBiomeFromLookup(double temperature, double humidity) {
		int i4 = (int)(temperature * 63.0D);
		int i5 = (int)(humidity * 63.0D);
		return biomeLookupTable[i4 + i5 * 64];
	}
	
	public static void generateBiomeLookup() {
		for(int i0 = 0; i0 < 64; ++i0) {
			for(int i1 = 0; i1 < 64; ++i1) {
				biomeLookupTable[i0 + i1 * 64] = getBiome((float)i0 / 63.0F, (float)i1 / 63.0F);
			}
		}
	}
	
	static {
		generateBiomeLookup();
	}
}
