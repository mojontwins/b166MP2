package net.minecraft.src;

public class MaterialLiquid extends Material {
	public MaterialLiquid(MapColor mapColor1) {
		super(mapColor1);
		this.func_27284_f();
	}

	public boolean getIsLiquid() {
		return true;
	}

	public boolean getIsSolid() {
		return false;
	}

	public boolean isSolid() {
		return false;
	}
}
