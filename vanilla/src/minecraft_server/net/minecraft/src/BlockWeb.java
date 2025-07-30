package net.minecraft.src;

public class BlockWeb extends Block {
	public BlockWeb(int i1, int i2) {
		super(i1, i2, Material.cloth);
	}

	public void onEntityCollidedWithBlock(World world1, int i2, int i3, int i4, Entity entity5) {
		entity5.field_27012_bb = true;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}
}
