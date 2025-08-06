package net.minecraft.world.phys;

final class Vec3LocalPool extends ThreadLocal<Object> {
	protected Vec3Pool func_72342_a() {
		return new Vec3Pool(300, 2000);
	}

	protected Object initialValue() {
		return this.func_72342_a();
	}
}
