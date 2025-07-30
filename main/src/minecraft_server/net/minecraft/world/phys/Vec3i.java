package net.minecraft.world.phys;

import net.minecraft.src.MathHelper;

public class Vec3i {
	public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
	public int x;
	public int y;
	public int z;
	public int r;
	public int g;
	public int b;

	public Vec3i(int xIn, int yIn, int zIn) {
		this.x = this.r = xIn;
		this.y = this.g = yIn;
		this.z = this.b = zIn;
	}

	public Vec3i(double xIn, double yIn, double zIn) {
		this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof Vec3i)) {
			return false;
		} else {
			Vec3i vec3i = (Vec3i) other;
			return this.getX() != vec3i.getX() ? false
					: (this.getY() != vec3i.getY() ? false : this.getZ() == vec3i.getZ());
		}
	}

	@Override
	public int hashCode() {
		return (this.getY() + this.getZ() * 31) * 31 + this.getX();
	}

	public int compareTo(Vec3i other) {
		return this.getY() == other.getY()
				? (this.getZ() == other.getZ() ? this.getX() - other.getX() : this.getZ() - other.getZ())
				: this.getY() - other.getY();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public Vec3i crossProduct(Vec3i vec) {
		return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(),
				this.getZ() * vec.getX() - this.getX() * vec.getZ(),
				this.getX() * vec.getY() - this.getY() * vec.getX());
	}

	public double distanceSq(double toX, double toY, double toZ) {
		double d0 = (double) this.getX() - toX;
		double d1 = (double) this.getY() - toY;
		double d2 = (double) this.getZ() - toZ;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public double distanceSqToCenter(double xIn, double yIn, double zIn) {
		double d0 = (double) this.getX() + 0.5D - xIn;
		double d1 = (double) this.getY() + 0.5D - yIn;
		double d2 = (double) this.getZ() + 0.5D - zIn;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public double distanceSq(Vec3i to) {
		return this.distanceSq((double) to.getX(), (double) to.getY(), (double) to.getZ());
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public Vec3i copy() {
		return new Vec3i(this.x, this.y, this.z);
	}

}
