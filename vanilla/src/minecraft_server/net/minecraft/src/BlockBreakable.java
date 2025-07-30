package net.minecraft.src;

public class BlockBreakable extends Block {
	private boolean field_6084_a;

	protected BlockBreakable(int i1, int i2, Material material3, boolean z4) {
		super(i1, i2, material3);
		this.field_6084_a = z4;
	}

	public boolean isOpaqueCube() {
		return false;
	}
}
