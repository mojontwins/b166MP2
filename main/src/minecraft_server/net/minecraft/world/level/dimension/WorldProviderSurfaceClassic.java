package net.minecraft.world.level.dimension;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class WorldProviderSurfaceClassic extends WorldProvider {
	
	// This world provider provides stuff like classic fog, sky color, brightness table or 
	// grayscale lightmap.
	
	public WorldProviderSurfaceClassic() {
	}

	public Vec3D getFogColor(Entity entityPlayer, float celestialAngle, float partialTick) {
		float intensity = MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(intensity < 0.0F) {
			intensity = 0.0F;
		}

		if(intensity > 1.0F) {
			intensity = 1.0F;
		}

		float r = 0.7529412F;
		float g = 0.84705883F;
		float b = 1.0F;
		r *= intensity * 0.94F + 0.06F;
		g *= intensity * 0.94F + 0.06F;
		b *= intensity * 0.91F + 0.09F;
		return Vec3D.createVector((double)r, (double)g, (double)b);
	}
	
	public float getCloudHeight() {
		return 108;
	}
	
	public boolean isSkyColored() {
		return true;
	}
	
	public int getAverageGroundLevel() {
		return 64;
	}
	
	public boolean getWorldHasNoSky() {
		return false;
	}
	
	@Override
	public int[] updateLightmap(float torchFlicker, float gamma, EntityPlayer thePlayer) {
		int[] lightmapColors = new int[256];
		World world = this.worldObj;
		
		if(world != null) {
			// Simpler, gray, faster version with no flicker
			float sb = world.getSunBrightness(1.0F);
			for(int idx = 0; idx < 256; ++idx) {
				float sunBrightness = sb * 0.95F + 0.05F;
				float lightCoarse =this.lightBrightnessTable[idx / 16] * sunBrightness;
				float lightFine = this.lightBrightnessTable[idx % 16];
				if(world.lightningFlash > 0) {
					lightCoarse = this.lightBrightnessTable[idx / 16];
				}
				
				float lightCoarseNorm = lightCoarse * (sb * 0.8F + 0.2F); 	// This is darker than Release Vanilla
				float component = lightCoarseNorm + lightFine; 				// Grey only needs one component for rgb
				
				component = component * 0.8F + 0.02F;						// Don't let it get pitch black
				
				if (component > 1.0F) component = 1.0F;
				
				float componentI = 1.0F - component;
				componentI = 1.0F - componentI * componentI * componentI * componentI;
				component = component * (1.0F - gamma) + componentI * component;
				
				component = component * 0.96F + 0.03F;						// Don't let it get pitch black
				
				if (component > 1.0F) component = 1.0F;
				if (component < 0.0F) component = 0.0F;
				
				short alpha = 255;
				int cI = (int)(component * 255.0F);
				
				lightmapColors[idx] = alpha << 24 | cI << 16 | cI << 8 | cI;
			}	
		}
		
		return lightmapColors;
	}
	
	public int getRainTimeToStop(Random rand) {
		if(this.worldObj.getWorldInfo().isSnowCovered()) return rand.nextInt(24000) + 12000;
		return rand.nextInt(12000) + 12000;
	}

	public int getRainTimeToStart(Random rand) {
		if(this.worldObj.getWorldInfo().isSnowCovered()) return rand.nextInt(24000) + 12000;
		return rand.nextInt(168000) + 12000;
	}
	
	public void generateSpawnPoint(World world, Random rand) {
		world.findingSpawnPoint = true;
		int spawnX = 0;
		int spawnY = this.getAverageGroundLevel();
		int spawnZ = 0;

		while (!this.findSpawn(world, spawnX, spawnZ)) {
			spawnX += rand.nextInt(64) - rand.nextInt(64);
			spawnZ += rand.nextInt(64) - rand.nextInt(64);
		}
	
		world.worldInfo.setSpawnPosition(spawnX, spawnY, spawnZ);
	}
	
	
}
