package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.IntCache;

public class GenLayerVoronoiZoom extends GenLayer {
	public GenLayerVoronoiZoom(long j1, GenLayer genLayer3) {
		super(j1);
		super.parent = genLayer3;
	}

	public int[] getInts(int i1, int i2, int i3, int i4) {
		i1 -= 2;
		i2 -= 2;
		byte b5 = 2;
		int i6 = 1 << b5;
		int i7 = i1 >> b5;
		int i8 = i2 >> b5;
		int i9 = (i3 >> b5) + 3;
		int i10 = (i4 >> b5) + 3;
		int[] i11 = this.parent.getInts(i7, i8, i9, i10);
		int i12 = i9 << b5;
		int i13 = i10 << b5;
		int[] i14 = IntCache.getIntCache(i12 * i13);

		int i16;
		for(int i15 = 0; i15 < i10 - 1; ++i15) {
			i16 = i11[0 + (i15 + 0) * i9];
			int i17 = i11[0 + (i15 + 1) * i9];

			for(int i18 = 0; i18 < i9 - 1; ++i18) {
				double d19 = (double)i6 * 0.9D;
				this.initChunkSeed((long)(i18 + i7 << b5), (long)(i15 + i8 << b5));
				double d21 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19;
				double d23 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19;
				this.initChunkSeed((long)(i18 + i7 + 1 << b5), (long)(i15 + i8 << b5));
				double d25 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19 + (double)i6;
				double d27 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19;
				this.initChunkSeed((long)(i18 + i7 << b5), (long)(i15 + i8 + 1 << b5));
				double d29 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19;
				double d31 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19 + (double)i6;
				this.initChunkSeed((long)(i18 + i7 + 1 << b5), (long)(i15 + i8 + 1 << b5));
				double d33 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19 + (double)i6;
				double d35 = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * d19 + (double)i6;
				int i37 = i11[i18 + 1 + (i15 + 0) * i9];
				int i38 = i11[i18 + 1 + (i15 + 1) * i9];

				for(int i39 = 0; i39 < i6; ++i39) {
					int i40 = ((i15 << b5) + i39) * i12 + (i18 << b5);

					for(int i41 = 0; i41 < i6; ++i41) {
						double d42 = ((double)i39 - d23) * ((double)i39 - d23) + ((double)i41 - d21) * ((double)i41 - d21);
						double d44 = ((double)i39 - d27) * ((double)i39 - d27) + ((double)i41 - d25) * ((double)i41 - d25);
						double d46 = ((double)i39 - d31) * ((double)i39 - d31) + ((double)i41 - d29) * ((double)i41 - d29);
						double d48 = ((double)i39 - d35) * ((double)i39 - d35) + ((double)i41 - d33) * ((double)i41 - d33);
						if(d42 < d44 && d42 < d46 && d42 < d48) {
							i14[i40++] = i16;
						} else if(d44 < d42 && d44 < d46 && d44 < d48) {
							i14[i40++] = i37;
						} else if(d46 < d42 && d46 < d44 && d46 < d48) {
							i14[i40++] = i17;
						} else {
							i14[i40++] = i38;
						}
					}
				}

				i16 = i37;
				i17 = i38;
			}
		}

		int[] i50 = IntCache.getIntCache(i3 * i4);

		for(i16 = 0; i16 < i4; ++i16) {
			System.arraycopy(i14, (i16 + (i2 & i6 - 1)) * (i9 << b5) + (i1 & i6 - 1), i50, i16 * i3, i3);
		}

		return i50;
	}
}
