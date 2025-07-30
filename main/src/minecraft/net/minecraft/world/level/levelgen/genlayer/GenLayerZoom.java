package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.IntCache;

public class GenLayerZoom extends GenLayer {
	public GenLayerZoom(long j1, GenLayer genLayer3) {
		super(j1);
		super.parent = genLayer3;
	}

	public int[] getInts(int x, int z, int w, int h) {
		int srcX = x >> 1;
		int srcZ = z >> 1;
		int srcW = (w >> 1) + 3;
		int srcH = (h >> 1) + 3;
		int[] src = this.parent.getInts(srcX, srcZ, srcW, srcH);
		int[] dst = IntCache.getIntCache(srcW * 2 * srcH * 2);
		int dstW = srcW << 1;

		int dstIy;
		for(int srcIy = 0; srcIy < srcH - 1; ++srcIy) {
			dstIy = srcIy << 1;
			int dstIdx = dstIy * dstW;
			int srcT0 = src[0 + (srcIy + 0) * srcW];
			int srcB0 = src[0 + (srcIy + 1) * srcW];

			for(int srcIx = 0; srcIx < srcW - 1; ++srcIx) {
				this.initChunkSeed((long)(srcIx + srcX << 1), (long)(srcIy + srcZ << 1));
				
				int srcTx = src[srcIx + 1 + (srcIy + 0) * srcW];
				int srcBx = src[srcIx + 1 + (srcIy + 1) * srcW];
				
				dst[dstIdx] = srcT0;
				dst[dstIdx++ + dstW] = this.choose(srcT0, srcB0);

				dst[dstIdx] = this.choose(srcT0, srcTx);
				dst[dstIdx++ + dstW] = this.choose(srcT0, srcTx, srcB0, srcBx);
				
				srcT0 = srcTx;
				srcB0 = srcBx;
			}
		}

		int[] i20 = IntCache.getIntCache(w * h);

		for(dstIy = 0; dstIy < h; ++dstIy) {
			System.arraycopy(dst, (dstIy + (z & 1)) * (srcW << 1) + (x & 1), i20, dstIy * w, w);
		}

		return i20;
	}

	protected int choose(int i1, int i2) {
		return this.nextInt(2) == 0 ? i1 : i2;
	}

	protected int choose(int t0, int tx, int b0, int bx) {
		if(tx == b0 && b0 == bx) {
			return tx;
		} else if(t0 == tx && t0 == b0) {
			return t0;
		} else if(t0 == tx && t0 == bx) {
			return t0;
		} else if(t0 == b0 && t0 == bx) {
			return t0;
		} else if(t0 == tx && b0 != bx) {
			return t0;
		} else if(t0 == b0 && tx != bx) {
			return t0;
		} else if(t0 == bx && tx != b0) {
			return t0;
		} else if(tx == t0 && b0 != bx) {
			return tx;
		} else if(tx == b0 && t0 != bx) {
			return tx;
		} else if(tx == bx && t0 != b0) {
			return tx;
		} else if(b0 == t0 && tx != bx) {
			return b0;
		} else if(b0 == tx && t0 != bx) {
			return b0;
		} else if(b0 == bx && t0 != tx) {
			return b0;
		} else if(bx == t0 && tx != b0) {
			return b0;
		} else if(bx == tx && t0 != b0) {
			return b0;
		} else if(bx == b0 && t0 != tx) {
			return b0;
		} else {
			int i5 = this.nextInt(4);
			return i5 == 0 ? t0 : (i5 == 1 ? tx : (i5 == 2 ? b0 : bx));
		}
	}

	public static GenLayer zoomLayer(long j0, GenLayer genLayer2, int i3) {
		Object object4 = genLayer2;

		for(int i5 = 0; i5 < i3; ++i5) {
			object4 = new GenLayerZoom(j0 + (long)i5, (GenLayer)object4);
		}

		return (GenLayer)object4;
	}
}
