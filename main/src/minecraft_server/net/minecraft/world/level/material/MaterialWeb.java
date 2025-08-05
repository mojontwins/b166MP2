package net.minecraft.world.level.material;

import net.minecraft.world.item.map.MapColor;

final class MaterialWeb extends Material {
	MaterialWeb(MapColor mapColor) {
		super(mapColor);
	}

	public boolean blocksMovement() {
		return false;
	}
}
