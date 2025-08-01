package net.minecraft.src;

public class WorldProviderHell extends WorldProvider {
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 1.0D, 0.0D);
		this.field_6167_c = true;
		this.isHellWorld = true;
		this.field_4306_c = true;
		this.worldType = -1;
	}

	protected void generateLightBrightnessTable() {
		float f1 = 0.1F;

		for(int i2 = 0; i2 <= 15; ++i2) {
			float f3 = 1.0F - (float)i2 / 15.0F;
			this.lightBrightnessTable[i2] = (1.0F - f3) / (f3 * 3.0F + 1.0F) * (1.0F - f1) + f1;
		}

	}

	public IChunkProvider func_2938281_a() {
		return new ChunkProviderHell(this.worldObj, this.worldObj.getRandomSeed());
	}

	public boolean canCoordinateBeSpawn(int i1, int i2) {
		int i3 = this.worldObj.getFirstUncoveredBlock(i1, i2);
		return i3 == Block.bedrock.blockID ? false : (i3 == 0 ? false : Block.opaqueCubeLookup[i3]);
	}

	public float calculateCelestialAngle(long j1, float f3) {
		return 0.5F;
	}

	public boolean func_28108_d() {
		return false;
	}
}
