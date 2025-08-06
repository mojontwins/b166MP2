package net.minecraft.world.level.chunk;

public class NibbleArray {
	public final byte[] data;
	private final int depthBits;
	private final int depthBitsPlusFour;

	public NibbleArray(int size, int bits) {
		this.data = new byte[size >> 1];
		this.depthBits = bits;
		this.depthBitsPlusFour = bits + 4;
	}

	public NibbleArray(byte[] byteArray, int bits) {
		this.data = byteArray;
		this.depthBits = bits;
		this.depthBitsPlusFour = bits + 4;
	}

	public int get(int x, int y, int z) {
		// x * 256 + z * 16 + y
		int index = x << this.depthBitsPlusFour | z << this.depthBits | y;
		// v = data [index / 2] -> AAAABBBB
		final byte value = this.data[index >>> 1];
		// if we are an even index, we want lower 4 bits
		// if we are an odd index, we want upper 4 bits
		
		// if index EVEN, index & 1 = 0; 0 << 2 = 0; v >> 0 = v; ret v & 15 -> BBBB
		// if index OFF,  index & 1 = 1, 1 << 2 = 4; v >> 4 = 0000AAAA -> AAAA
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
	
	/*         /--\-----------we want this as a NibbleArray
	 * In:  0: AAAABBBB
	 *      1: CCCCDDDD
	 *      2: EEEEFFFF
	 *      3: GGGGHHHH
	 *      ...
	 * Out: 0: CCCCAAAA
	 *      1: GGGGEEEE
	 *      ...
	 */
	public static NibbleArray byteArrayToUpperNibbleArray(byte [] byteArray) {
		NibbleArray n = new NibbleArray(byteArray.length, 4);
		
		for(int i = 0; i < byteArray.length; i += 2 ) {
			// Even : lower, odd : upper
			n.data[i >> 1] = (byte) ((byteArray[i] >> 4) | byteArray[i + 1] & 0xf0);
		}
		
		return n;
	}
	
	/*             /--\--------we want this as NibbleArray
	 * In:  0: AAAABBBB
	 *      1: CCCCDDDD
	 *      2: EEEEFFFF
	 *      3: GGGGHHHH
	 *      ...
	 * Out: 0: DDDDBBBB
	 *      1: HHHHFFFF
	 *      ...
	 */
	public static NibbleArray byteArrayToLowerNibbleArray(byte [] byteArray) {
		NibbleArray n = new NibbleArray(byteArray.length, 4);
		
		for(int i = 0; i < byteArray.length; i += 2 ) {
			// Even : lower, odd : upper
			n.data[i >> 1] = (byte) ((byteArray[i] & 0xf) | (byteArray[i + 1] & 15) << 4);
		}
		
		return n;
	}
	
	/*
	 * 	In: Upper: CCCCAAAA, GGGGEEEE, ...
	 *      Lower: DDDDBBBB, HHHHFFFF, ...
	 *  Out: AAAABBBB, CCCCDDDD, EEEEFFFF, GGGGHHHH
	 *       \UL/\LL/  \UU/\LU/  \UL/\LL/  \UU/\LU/ 
	 */
	public static byte [] combineTwo(NibbleArray lower, NibbleArray upper) {
		byte [] result = new byte [lower.data.length << 1];
		for(int i = 0; i < result.length; i += 2) {
			int j = i >> 1;
			int ub = upper.data[j] & 0xff;
			int lb = lower.data[j] & 0xff;
			
			// i even -> lower nibble
			result[i] = (byte) (((ub & 0x0f) << 4) | (lb & 0x0f));
		
			// i odd -> upper nibble
			result[i+1] = (byte) ((ub & 0xf0) | ((lb & 0xff) >> 4));
		}
		return result;
	}
	
	public byte[] asByteArray() {
		byte[] result = new byte[this.data.length * 2];
		int idx = 0;
		for(int i = 0; i < this.data.length; i ++) {
			result[idx++] = (byte)(this.data[i] & 15);
			result[idx++] = (byte)((this.data[i] >> 4) & 15);
		}
		
		return result;
	}
}
