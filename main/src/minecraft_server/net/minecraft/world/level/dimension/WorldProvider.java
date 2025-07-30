package net.minecraft.world.level.dimension;

import java.util.List;
import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldChunkManager;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFluid;
import net.minecraft.world.phys.Vec3D;

public abstract class WorldProvider {
	public World worldObj;
	public WorldType terrainType;
	public WorldChunkManager worldChunkMgr;
	public boolean isHellWorld = false;
	public boolean hasNoSky = false;
	public boolean isCaveWorld = false;
	public boolean hasSolidFloor = true;
	public float[] lightBrightnessTable = new float[16];
	public int worldType = 0;
	private float[] colorsSunriseSunset = new float[4];

	public final void registerWorld(World world1) {
		this.worldObj = world1;
		this.terrainType = world1.getWorldInfo().getTerrainType();
		this.registerWorldChunkManager();
		this.generateLightBrightnessTable();
	}

	protected void generateLightBrightnessTable() {
		float f1 = 0.0F;

		for(int i2 = 0; i2 <= 15; ++i2) {
			float f3 = 1.0F - (float)i2 / 15.0F;
			this.lightBrightnessTable[i2] = (1.0F - f3) / (f3 * 3.0F + 1.0F) * (1.0F - f1) + f1;
		}

	}

	protected void registerWorldChunkManager() {
		this.worldChunkMgr = this.worldObj.getWorldInfo().getTerrainType().getChunkManager(this.worldObj);
	}

	public IChunkProvider getChunkProvider() {
		return this.worldObj.getWorldInfo().getTerrainType().getChunkGenerator(this.worldObj);
	}

	public boolean canCoordinateBeSpawn(int i1, int i2) {
		int i3 = this.worldObj.getFirstUncoveredBlock(i1, i2);
		return i3 == Block.grass.blockID;
	}

	public float calculateCelestialAngle(long worldTime, float renderPartialTick) {
		float dayProgress;

		if(this.worldObj.worldInfo.isEnableSeasons()) {
			// Variable length day cycle. Thanks Jonkadelic!
			float tickWithinCycle = (int)(worldTime % 24000L) + renderPartialTick;
		
			boolean isDay = tickWithinCycle < Seasons.dayLengthTicks;
			
			float partProgress = isDay ? 
					(float)tickWithinCycle / (float)Seasons.dayLengthTicks
				:
					(float)(tickWithinCycle - Seasons.dayLengthTicks) / (float)Seasons.nightLengthTicks
			;
					
			dayProgress = isDay ? 
					partProgress / 2.0F
				:
					0.5F + partProgress / 2.0F
			;
			
			dayProgress -= 0.25F;
		} else {
			int tickWithinCycle = (int)(worldTime % 24000L);
			dayProgress = ((float)tickWithinCycle + renderPartialTick) / 24000.0F - 0.25F;
		}

		if(dayProgress < 0.0F) dayProgress ++;
		if(dayProgress > 1.0F) dayProgress --;

		float f2 = dayProgress;
		dayProgress = 1.0F - (float)((Math.cos((double)dayProgress * Math.PI) + 1.0D) / 2D);
		dayProgress = f2 + (dayProgress - f2) / 3F;

		return dayProgress;
	}

	public int getMoonPhase(long j1, float f3) {
		return (int)(j1 / 24000L) % 8;
	}

	public boolean canSleepHere() {
		return true;
	}

	public float[] calcSunriseSunsetColors(float f1, float f2) {
		float f3 = 0.4F;
		float f4 = MathHelper.cos(f1 * (float)Math.PI * 2.0F) - 0.0F;
		float f5 = -0.0F;
		if(f4 >= f5 - f3 && f4 <= f5 + f3) {
			float f6 = (f4 - f5) / f3 * 0.5F + 0.5F;
			float f7 = 1.0F - (1.0F - MathHelper.sin(f6 * (float)Math.PI)) * 0.99F;
			f7 *= f7;
			this.colorsSunriseSunset[0] = f6 * 0.3F + 0.7F;
			this.colorsSunriseSunset[1] = f6 * f6 * 0.7F + 0.2F;
			this.colorsSunriseSunset[2] = f6 * f6 * 0.0F + 0.2F;
			this.colorsSunriseSunset[3] = f7;
			return this.colorsSunriseSunset;
		} else {
			return null;
		}
	}

	public Vec3D getFogColor(Entity entityPlayer, float celestialAngle, float renderPartialTicks) {
		float sunHeight = MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(sunHeight < 0.0F) {
			sunHeight = 0.0F;
		}

		if(sunHeight > 1.0F) {
			sunHeight = 1.0F;
		}

		float r, g, b;
		if (GameRules.boolRule("colouredFog") && !this.worldObj.worldInfo.isEnableSeasons()) {
			int rgba = Seasons.getFogColorForToday();
			r = (float)(rgba >> 16 & 255L) / 255.0F;
			g = (float)(rgba >> 8 & 255L) / 255.0F;
			b = (float)(rgba & 255L) / 255.0F;
			
		} else {
			r = 0.7529412F;
			g = 0.84705883F;
			b = 1.0F;
			}
	
		r *= sunHeight * 0.94F + 0.06F;
		g *= sunHeight * 0.94F + 0.06F;
		b *= sunHeight * 0.91F + 0.09F;
		return Vec3D.createVector((double)r, (double)g, (double)b);
	
	}
	
	public Vec3D getSkyColor(Entity thePlayer, float renderPartialTick) { 

		float celestialAngle = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), renderPartialTick);
		float solarHeight = MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(solarHeight < 0.0F) {
			solarHeight = 0.0F;
		}

		if(solarHeight > 1.0F) {
			solarHeight = 1.0F;
		}

		int rgba;
		if(this.worldObj.worldInfo.isEnableSeasons()) {
			rgba = Seasons.getSkyColorForToday();
		} else {
			int x = MathHelper.floor_double(thePlayer.posX);
			int z = MathHelper.floor_double(thePlayer.posZ);
			BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x, z);
			float temp = biome.getFloatTemperature();
			rgba = biome.getSkyColorByTemp(temp);
		}
		
		float r = (float)(rgba >> 16 & 255) / 255.0F;
		float g = (float)(rgba >> 8 & 255) / 255.0F;
		float b = (float)(rgba & 255) / 255.0F;
		r *= solarHeight;
		g *= solarHeight;
		b *= solarHeight;
				
		float atenuationStrength = this.worldObj.getRainStrength(renderPartialTick) + this.worldObj.getWeightedThunderStrength(renderPartialTick) - this.worldObj.getSnowStrength(renderPartialTick);
		if(atenuationStrength >= 0.0F) {
			if(atenuationStrength >= 1.0F) atenuationStrength = 1.0F;
			float skyColorComponent = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;
			float skyColorAtenuation = 1.0F - atenuationStrength * 0.75F;
			r = r * skyColorAtenuation + skyColorComponent * (1.0F - skyColorAtenuation);
			g = g * skyColorAtenuation + skyColorComponent * (1.0F - skyColorAtenuation);
			b = b * skyColorAtenuation + skyColorComponent * (1.0F - skyColorAtenuation);
		}

		if(this.worldObj.lightningFlash > 0) {
			float lightning = (float)this.worldObj.lightningFlash - renderPartialTick;
			if(lightning > 1.0F) {
				lightning = 1.0F;
			}

			lightning *= 0.45F;
			r = r * (1.0F - lightning) + 0.8F * lightning;
			g = g * (1.0F - lightning) + 0.8F * lightning;
			b = b * (1.0F - lightning) + 1.0F * lightning;
		}

		return Vec3D.createVector((double)r, (double)g, (double)b);
	}
	
	public Vec3D getSkyColorBottom(Entity entity1, float f2) {
		float f3 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f2);
		float f4 = MathHelper.cos(f3 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(f4 < 0.0F) {
			f4 = 0.0F;
		}

		if(f4 > 1.0F) {
			f4 = 1.0F;
		}
		
		Vec3D vec3D = ((BlockFluid)Block.waterStill).colorMultiplierAsVec3D(this.worldObj, (int) entity1.posX, (int) entity1.posY, (int) entity1.posZ);
		float f10 = (float)vec3D.xCoord;
		float f11 = (float)vec3D.yCoord;
		float f12 = (float)vec3D.zCoord;
		
		f10 *= f4;
		f11 *= f4;
		f12 *= f4;
		float f13 = this.worldObj.getRainStrength(f2);
		float f14;
		float f15;
		if(f13 > 0.0F) {
			f14 = (f10 * 0.3F + f11 * 0.59F + f12 * 0.11F) * 0.6F;
			f15 = 1.0F - f13 * 0.75F;
			f10 = f10 * f15 + f14 * (1.0F - f15);
			f11 = f11 * f15 + f14 * (1.0F - f15);
			f12 = f12 * f15 + f14 * (1.0F - f15);
		}

		f14 = this.worldObj.getWeightedThunderStrength(f2);
		if(f14 > 0.0F) {
			f15 = (f10 * 0.3F + f11 * 0.59F + f12 * 0.11F) * 0.2F;
			float f16 = 1.0F - f14 * 0.75F;
			f10 = f10 * f16 + f15 * (1.0F - f16);
			f11 = f11 * f16 + f15 * (1.0F - f16);
			f12 = f12 * f16 + f15 * (1.0F - f16);
		}

		if(this.worldObj.lightningFlash > 0) {
			f15 = (float)this.worldObj.lightningFlash - f2;
			if(f15 > 1.0F) {
				f15 = 1.0F;
			}

			f15 *= 0.45F;
			f10 = f10 * (1.0F - f15) + 0.8F * f15;
			f11 = f11 * (1.0F - f15) + 0.8F * f15;
			f12 = f12 * (1.0F - f15) + 1.0F * f15;
		}

		return Vec3D.createVector((double)f10, (double)f11, (double)f12);
	}	
	
	public float getStarBrightness(float f1) {
		float f2 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f1);
		float f3 = 1.0F - (MathHelper.cos(f2 * (float)Math.PI * 2.0F) * 2.0F + 0.75F);
		if(f3 < 0.0F) {
			f3 = 0.0F;
		}

		if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		return f3 * f3 * 0.5F;
	}

	public boolean canRespawnHere() {
		return true;
	}

	public static WorldProvider getProviderForTerrainType(WorldType terrainType) {
		if(terrainType == WorldType.ALPHA || terrainType == WorldType.ALPHA_SNOW || terrainType == WorldType.INFDEV || terrainType == WorldType.OCEAN) return new WorldProviderSurfaceClassic();
		if(terrainType == WorldType.SKY)return new WorldProviderSkyClassic(); 
		return new WorldProviderSurface();
	}
	
	public static WorldProvider getProviderForDimension(int i0, WorldType terrainType) {
		// TODO: Hook new dimension logic here.
		switch(i0) {
		default: 
			// Default or dimension = 0!
			return getProviderForTerrainType(terrainType);
		}
	}

	public float getCloudHeight() {
		return this.terrainType != WorldType.AMPLIFIED ? 128.0F : 250F;
	}

	public boolean isSkyColored() {
		return true;
	}

	public ChunkCoordinates getEntrancePortalLocation() {
		return null;
	}

	public int getAverageGroundLevel() {
		return this.terrainType == WorldType.FLAT ? 
				4 
			: 
				64;
	}

	public boolean getWorldHasNoSky() {
		return this.terrainType != WorldType.FLAT && !this.hasNoSky;
	}

	public double getVoidFogYFactor() {
		return this.terrainType == WorldType.FLAT ? 1.0D : 8.0D / 256D;
	}
	
	public int calculateSkylightSubtracted(float f1) {
		//float f2 = this.getCelestialAngle(f1);
		float f2 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f1);
		float f3 = 1.0F - (MathHelper.cos(f2 * (float)Math.PI * 2.0F) * 2.0F + 0.5F);
		if(f3 < 0.0F) {
			f3 = 0.0F;
		}

		if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		f3 = 1.0F - f3;
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getRainStrength(f1) * 5.0F) / 16.0D));
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getWeightedThunderStrength(f1) * 5.0F) / 16.0D));
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getSandstormingStrength(f1) * 7.0F) / 16.0D));
		f3 = 1.0F - f3;
		return (int)(f3 * 11.0F);
	}
	
	public float getSunBrightness(float f1) {
		//float f2 = this.getCelestialAngle(f1);
		float f2 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f1);
		float f3 = 1.0F - (MathHelper.cos(f2 * (float)Math.PI * 2.0F) * 2.0F + 0.2F);
		if(f3 < 0.0F) {
			f3 = 0.0F;
		}

		if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		f3 = 1.0F - f3;
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getRainStrength(f1) * 5.0F) / 16.0D));
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getWeightedThunderStrength(f1) * 5.0F) / 16.0D));
		return f3 * 0.8F + 0.2F;
	}
	
	public String getSaveFolder() {
		return null;
	}
	
	public String getWelcomeMessage() {
		return null;
	}

	public String getDepartMessage() {
		return null;
	}
	
	public int getSeaLevel() {
		return 64;
	}

	public boolean noCelestials() {
		return false;
	}

	public boolean canCreatePortalToTheNether() {
		return this.terrainType.canCreatePortalToTheNether;
	}

	public int getMaxNumberOfCreature(EnumCreatureType enumCreatureType) {
		return enumCreatureType.getMaxNumberOfCreature();
	}

	public int getRainTimeToStop(Random rand) {
		return rand.nextInt(12000) + 12000;
	}

	public int getRainTimeToStart(Random rand) {
		return rand.nextInt(168000) + 12000;
	}
	
	public int[] updateLightmap(float torchFlicker, float gamma, EntityPlayer thePlayer) {
		int[] lightmapColors = new int[256];
		World world1 = this.worldObj;
		
		if(world1 != null) {
			float sb = world1.getSunBrightness(1.0F);
			
			for(int idx = 0; idx < 256; ++idx) {
				
				float sunBrightness = sb * 0.95F + 0.05F;
				float lightCoarse = this.lightBrightnessTable[idx / 16] * sunBrightness;
				float lightFine = this.lightBrightnessTable[idx % 16] * (torchFlicker * 0.1F + 1.5F);
				if(world1.lightningFlash > 0) {
					lightCoarse = this.lightBrightnessTable[idx / 16];
				}

				float lightCoarseNorm = lightCoarse * (sb * 0.65F + 0.35F);
				float lightCoarseNormG = lightFine * ((lightFine * 0.6F + 0.4F) * 0.6F + 0.4F);
				float lightCoarseNormB = lightFine * (lightFine * lightFine * 0.6F + 0.4F);
				
				// This is what makes everything yellowish!
				float r = lightCoarseNorm + lightFine;
				float g = lightCoarseNorm + lightCoarseNormG;
				float b = lightCoarse + lightCoarseNormB;
				
				r = r * 0.96F + 0.03F;
				g = g * 0.96F + 0.03F;
				b = b * 0.96F + 0.03F;

				if(this.worldType == 1) {
					r = 0.22F + lightFine * 0.75F;
					g = 0.28F + lightCoarseNormG * 0.75F;
					b = 0.25F + lightCoarseNormB * 0.75F;
				}

				// Set up night vision with code somewhat stolen from r1.5.2!
				/*
				if(thePlayer.divingHelmetOn() && thePlayer.isInsideOfMaterial(Material.water)) {
					float nVB = 0.5F;
					
					float fNV = 1.0F / r;
					if(fNV > 1.0F / g) fNV = 1.0F / g;
					if(fNV > 1.0F / b) fNV = 1.0F / b;
					
					r = r * (1.0F - nVB) + r * fNV * nVB;
					g = g * (1.0F - nVB) + g * fNV * nVB;
					b = b * (1.0F - nVB) + b * fNV * nVB;
				}
				*/

				if(r > 1.0F) {
					r = 1.0F;
				}

				if(g > 1.0F) {
					g = 1.0F;
				}

				if(b > 1.0F) {
					b = 1.0F;
				}

				float f16 = 1.0F - r;
				float f17 = 1.0F - g;
				float f18 = 1.0F - b;
				f16 = 1.0F - f16 * f16 * f16 * f16;
				f17 = 1.0F - f17 * f17 * f17 * f17;
				f18 = 1.0F - f18 * f18 * f18 * f18;
				r = r * (1.0F - gamma) + f16 * gamma;
				g = g * (1.0F - gamma) + f17 * gamma;
				b = b * (1.0F - gamma) + f18 * gamma;
				r = r * 0.96F + 0.03F;
				g = g * 0.96F + 0.03F;
				b = b * 0.96F + 0.03F;
				if(r > 1.0F) {
					r = 1.0F;
				}

				if(g > 1.0F) {
					g = 1.0F;
				}

				if(b > 1.0F) {
					b = 1.0F;
				}

				if(r < 0.0F) {
					r = 0.0F;
				}

				if(g < 0.0F) {
					g = 0.0F;
				}

				if(b < 0.0F) {
					b = 0.0F;
				}

				short alpha = 255;
				int rI = (int)(r * 255.0F);
				int gI = (int)(g * 255.0F);
				int bI = (int)(b * 255.0F);
				lightmapColors[idx] = alpha << 24 | rI << 16 | gI << 8 | bI;
			}
		}
		
		return lightmapColors;
	}

	public void generateSpawnPoint(World world, Random rand) {
		world.findingSpawnPoint = true;
		int spawnX = 0;
		int spawnY = this.getAverageGroundLevel();
		int spawnZ = 0;

		WorldChunkManager worldChunkManager1 = this.worldChunkMgr;
		List<BiomeGenBase> list2 = worldChunkManager1.getBiomesToSpawnIn();
		
		ChunkPosition chunkPosition4 = worldChunkManager1.findBiomePosition(0, 0, 256, list2, rand);
		
		if(chunkPosition4 != null) {
			spawnX = chunkPosition4.x;
			spawnZ = chunkPosition4.z;
		} else {
			System.out.println("Unable to find spawn biome");
		}

		int i8 = 0;

		while(!this.canCoordinateBeSpawn(spawnX, spawnZ)) {
			spawnX += rand.nextInt(64) - rand.nextInt(64);
			spawnZ += rand.nextInt(64) - rand.nextInt(64);
			++i8;
			if(i8 == 1000) {
				break;
			}
		}
		
		world.worldInfo.setSpawnPosition(spawnX, spawnY, spawnZ);
	}
	
	boolean findSpawn(World world, int x, int z) {
		int blockID = world.getFirstUncoveredBlock(x, z);
		return blockID == Block.sand.blockID;
	}

	public double getSeaLevelForRendering() {
		return this.terrainType == WorldType.FLAT ? 0.0F : 64.0F;
	}
}
