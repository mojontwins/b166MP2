package net.minecraft.world.phys;

import java.util.ArrayList;
import java.util.List;

public class Vec3Pool {
	private final int truncateArrayResetThreshold;
	private final int minimumSize;

	/** items at and above nextFreeSpace are assumed to be available */
	private final List<Vec3> vec3Cache = new ArrayList<Vec3>();
	private int nextFreeSpace = 0;
	private int maximumSizeSinceLastTruncation = 0;
	private int resetCount = 0;

	public Vec3Pool(int par1, int par2) {
		this.truncateArrayResetThreshold = par1;
		this.minimumSize = par2;
	}

	/**
	 * extends the pool if all vecs are currently "out"
	 */
	public Vec3 getVecFromPool(double par1, double par3, double par5) {
		Vec3 var7;

		if (this.nextFreeSpace >= this.vec3Cache.size()) {
			var7 = new Vec3(par1, par3, par5);
			this.vec3Cache.add(var7);
		} else {
			var7 = (Vec3) this.vec3Cache.get(this.nextFreeSpace);
			var7.setComponents(par1, par3, par5);
		}

		++this.nextFreeSpace;
		return var7;
	}

	/**
	 * will truncate the array everyN clears to the maximum size observed since the
	 * last truncation
	 */
	public void clear() {
		if (this.nextFreeSpace > this.maximumSizeSinceLastTruncation) {
			this.maximumSizeSinceLastTruncation = this.nextFreeSpace;
		}

		if (this.resetCount++ == this.truncateArrayResetThreshold) {
			int var1 = Math.max(this.maximumSizeSinceLastTruncation, this.vec3Cache.size() - this.minimumSize);

			while (this.vec3Cache.size() > var1) {
				this.vec3Cache.remove(var1);
			}

			this.maximumSizeSinceLastTruncation = 0;
			this.resetCount = 0;
		}

		this.nextFreeSpace = 0;
	}

	public void clearAndFreeCache() {
		this.nextFreeSpace = 0;
		this.vec3Cache.clear();
	}
}
