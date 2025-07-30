package net.minecraft.world.level.chunk;

public class NibbleArray {
	public final byte[] data;
	private final int depthBits;
	private final int depthBitsPlusFour;

	public NibbleArray(int i1, int i2) {
		this.data = new byte[i1 >> 1];
		this.depthBits = i2;
		this.depthBitsPlusFour = i2 + 4;
	}

	public NibbleArray(byte[] b1, int i2) {
		this.data = b1;
		this.depthBits = i2;
		this.depthBitsPlusFour = i2 + 4;
	}

	public int get(int x, int y, int z) {
		int index = x << this.depthBitsPlusFour | z << this.depthBits | y;
		final byte value = this.data[index >>> 1];
		// if we are an even index, we want lower 4 bits
		// if we are an odd index, we want upper 4 bits
		return ((value >>> ((index & 1) << 2)) & 0xF);
		// Canyon end - starlight
	}

	public void set(int x, int y, int z, int value) {
		int index = x << this.depthBitsPlusFour | z << this.depthBits | y;
		final int shift = (index & 1) << 2;
		final int i = index >>> 1;
		this.data[i] = (byte)((this.data[i] & (0xF0 >>> shift)) | (value << shift));
		// Canyon end - starlight
	}
	
	public boolean isValid() {
		return this.data != null;
	}
}
