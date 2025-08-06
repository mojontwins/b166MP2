package net.minecraft.world.level;

import net.minecraft.src.MathHelper;
import net.minecraft.world.phys.Vec3D;

public class ChunkPosition {
	public final int x;
	public final int y;
	public final int z;

	public ChunkPosition(int i1, int i2, int i3) {
		this.x = i1;
		this.y = i2;
		this.z = i3;
	}

	public ChunkPosition(Vec3D vec3D1) {
		this(MathHelper.floor_double(vec3D1.xCoord), MathHelper.floor_double(vec3D1.yCoord), MathHelper.floor_double(vec3D1.zCoord));
	}

	public boolean equals(Object object1) {
		if(!(object1 instanceof ChunkPosition)) {
			return false;
		} else {
			ChunkPosition chunkPosition2 = (ChunkPosition)object1;
			return chunkPosition2.x == this.x && chunkPosition2.y == this.y && chunkPosition2.z == this.z;
		}
	}

	public int hashCode() {
		return this.x * 8976890 + this.y * 981131 + this.z;
	}

	public double distanceSq(double x, double y, double z) {
		double dx = x - (double)this.x;
		double dy = y - (double)this.y;
		double dz = z - (double)this.z;
		return dx * dx + dy * dy + dz * dz;
	}
}
