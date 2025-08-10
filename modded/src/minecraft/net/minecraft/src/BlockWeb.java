package net.minecraft.src;

public class BlockWeb extends Block {
	public BlockWeb(int i1, int i2) {
		super(i1, i2, Material.cloth);
	}

	public void onEntityCollidedWithBlock(World world1, int i2, int i3, int i4, Entity entity5) {
		entity5.isInWeb = true;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}

	public int getRenderType() {
		return 1;
	}
}
