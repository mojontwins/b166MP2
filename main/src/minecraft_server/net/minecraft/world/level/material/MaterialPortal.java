package net.minecraft.world.level.material;

import net.minecraft.world.item.map.MapColor;

public class MaterialPortal extends Material {
	public MaterialPortal(MapColor mapColor) {
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
