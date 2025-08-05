package net.minecraft.world.level.material;

import net.minecraft.world.item.map.MapColor;

public class MaterialLogic extends Material {
	public MaterialLogic(MapColor mapColor) {
		super(mapColor);
	}

	public boolean isSolid() {
		return false;
	}

	public boolean getCanBlockGrass() {
		return false;
	}

	public boolean blocksMovement() {
		return false;
	}
}
