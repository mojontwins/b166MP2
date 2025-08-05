package net.minecraft.world.level.material;

import net.minecraft.world.item.map.MapColor;

public class MaterialTransparent extends Material {
	public MaterialTransparent(MapColor mapColor) {
		super(mapColor);
		this.setGroundCover();
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
