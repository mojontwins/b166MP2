package net.minecraft.src;

public class BlockLeavesBase extends Block {
	protected boolean graphicsLevel;

	protected BlockLeavesBase(int i1, int i2, Material material3, boolean z4) {
		super(i1, i2, material3);
		this.graphicsLevel = z4;
	}

	public boolean isOpaqueCube() {
		return false;
	}
}
