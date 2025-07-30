package net.minecraft.world.level;

import java.util.ArrayList;
import java.util.List;

public class IntCache {
	private static int intCacheSize = 256;
	private static List<int[]> freeSmallArrays = new ArrayList<int[]>();
	private static List<int[]> inUseSmallArrays = new ArrayList<int[]>();
	private static List<int[]> freeLargeArrays = new ArrayList<int[]>();
	private static List<int[]> inUseLargeArrays = new ArrayList<int[]>();

	public static int[] getIntCache(int i0) {
		int[] i1;
		if(i0 <= 256) {
			if(freeSmallArrays.size() == 0) {
				i1 = new int[256];
				inUseSmallArrays.add(i1);
				return i1;
			} else {
				i1 = (int[])freeSmallArrays.remove(freeSmallArrays.size() - 1);
				inUseSmallArrays.add(i1);
				return i1;
			}
		} else if(i0 > intCacheSize) {
			intCacheSize = i0;
			freeLargeArrays.clear();
			inUseLargeArrays.clear();
			i1 = new int[intCacheSize];
			inUseLargeArrays.add(i1);
			return i1;
		} else if(freeLargeArrays.size() == 0) {
			i1 = new int[intCacheSize];
			inUseLargeArrays.add(i1);
			return i1;
		} else {
			i1 = (int[])freeLargeArrays.remove(freeLargeArrays.size() - 1);
			inUseLargeArrays.add(i1);
			return i1;
		}
	}

	public static void resetIntCache() {
		if(freeLargeArrays.size() > 0) {
			freeLargeArrays.remove(freeLargeArrays.size() - 1);
		}

		if(freeSmallArrays.size() > 0) {
			freeSmallArrays.remove(freeSmallArrays.size() - 1);
		}

		freeLargeArrays.addAll(inUseLargeArrays);
		freeSmallArrays.addAll(inUseSmallArrays);
		inUseLargeArrays.clear();
		inUseSmallArrays.clear();
	}
}
