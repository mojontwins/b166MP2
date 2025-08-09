package net.minecraft.world.level.dimension;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldChunkManagerHellWithBiomes;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.levelgen.ChunkProviderHell;
import net.minecraft.world.phys.Vec3D;

public class WorldProviderHell extends WorldProvider {
	public void registerWorldChunkManager() {
		// this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 1.0F,
		// 0.0F);
		this.worldChunkMgr = new WorldChunkManagerHellWithBiomes(this.worldObj);
		this.isHellWorld = true;
		this.isCaveWorld = true;
		this.hasNoSky = true;
		this.worldType = -1;
	}

	/*
	 * public Vec3D getFogColor(Entity entityPlayer, float f1, float f2) { //return
	 * Vec3D.createVector((double)0.2F, (double)0.03F, (double)0.03F); return
	 * Vec3D.createVector((double)0.5F, (double)0.07F, (double)0.07F); }
	 */

	public Vec3D getFogColor(Entity entityPlayer, float f1, float f2) {
		int posX = (int) entityPlayer.posX;
		int posZ = (int) entityPlayer.posZ;

		int i6 = 0;
		int i7 = 0;
		int i8 = 0;
		float f4, f5, f6;

		// TODO:: Add option for colored fog

		// Trying to get it right, mark 2
		for (int i9 = -8; i9 < 8; i9++) {
			for (int i10 = -8; i10 < 8; i10++) {
				// 256 iterations of:
				int i11 = this.worldObj.getBiomeGenForCoords(posX + i10, posZ + i9).getBiomeFogColor(); 
				i6 += (i11 & 16711680) >> 16;
				i7 += (i11 & 65280) >> 8;
				i8 += i11 & 255;
			}
		}

		f4 = (i6 >> 8) / 256F;
		f5 = (i7 >> 8) / 256F;
		f6 = (i8 >> 8) / 256F;

		return Vec3D.createVector((double) f4, (double) f5, (double) f6);
	}

	protected void generateLightBrightnessTable() {
		float f1 = 0.1F;

		for (int i2 = 0; i2 <= 15; ++i2) {
			float f3 = 1.0F - (float) i2 / 15.0F;
			this.lightBrightnessTable[i2] = (1.0F - f3) / (f3 * 3.0F + 1.0F) * (1.0F - f1) + f1;
		}

	}
	
	@Override
	public int[] updateLightmap(float torchFlicker, float gamma, EntityPlayer thePlayer) {
		int[] lightmapColors = new int[256];
		World world = this.worldObj;
		
		if(world != null) {
			float sb = world.getSunBrightness(1.0F);
			for(int idx = 0; idx < 256; ++idx) {
				float sunBrightness = sb * 0.95F + 0.05F;
				float lightCoarse =this.lightBrightnessTable[idx / 16] * sunBrightness;
				float lightFine = this.lightBrightnessTable[idx % 16];
				if(world.lightningFlash > 0) {
					lightCoarse = this.lightBrightnessTable[idx / 16];
				}
				
				float lightCoarseNorm = lightCoarse * sb; 	// This is darker than Release Vanilla
				float component = lightCoarseNorm + lightFine; 				// Grey only needs one component for rgb
				
				component = component * 0.96F + 0.03F;						// Don't let it get pitch black
				
				if (component > 1.0F) component = 1.0F;
				
				float componentI = 1.0F - component;
				componentI = 1.0F - componentI * componentI * componentI * componentI;
				component = component * (1.0F - gamma) + componentI * component;
				
				component = component * 0.98F + 0.01F;						// Don't let it get pitch black
				component = component * 0.98F + 0.01F;						// Don't let it get pitch black
				
				float componentB = component * 0.96F + 0.03F;
				
				if (component > 1.0F) component = 1.0F;
				if (component < 0.0F) component = 0.0F;
				
				if (componentB > 1.0F) componentB = 1.0F;
				if (componentB < 0.0F) componentB = 0.0F;
				
				short alpha = 255;
				int cI = (int)(component * 255.0F);
				int cB = (int)(componentB * 255.0F);
				
				lightmapColors[idx] = alpha << 24 | cB << 16 | cI << 8 | cI;
			}	
		}
		
		return lightmapColors;
	}

	public IChunkProvider getChunkProvider() {
		return new ChunkProviderHell(this.worldObj, this.worldObj.getSeed());
	}

	public boolean canSleepHere() {
		return false;
	}

	public boolean canCoordinateBeSpawn(int i1, int i2) {
		return false;
	}

	public float calculateCelestialAngle(long j1, float f3) {
		//return 0.27F; // 0.5F;
		return 0.35F;
	}

	public boolean canRespawnHere() {
		return false;
	}

	public boolean func_48218_b(int i1, int i2) {
		return true;
	}
	
	public String getSaveFolder() {
		return "DIM-1";
	}
	
	public String getWelcomeMessage() {
		return "Entering the Nether";
	}

	public String getDepartMessage() {
		return "Leaving the Nether";
	}
}
