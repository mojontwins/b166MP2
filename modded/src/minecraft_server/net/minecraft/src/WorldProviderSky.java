package net.minecraft.src;

public class WorldProviderSky extends WorldProvider {
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.field_28054_m, 0.5D, 0.0D);
		this.worldType = 1;
	}

	public IChunkProvider func_2938281_a() {
		return new ChunkProviderSky(this.worldObj, this.worldObj.getRandomSeed());
	}

	public float calculateCelestialAngle(long j1, float f3) {
		return 0.0F;
	}

	public boolean canCoordinateBeSpawn(int i1, int i2) {
		int i3 = this.worldObj.getFirstUncoveredBlock(i1, i2);
		return i3 == 0 ? false : Block.blocksList[i3].blockMaterial.getIsSolid();
	}
}
