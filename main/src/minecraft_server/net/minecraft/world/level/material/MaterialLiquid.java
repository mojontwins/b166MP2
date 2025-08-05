package net.minecraft.world.level.material;

import net.minecraft.world.item.map.MapColor;

public class MaterialLiquid extends Material {
	public MaterialLiquid(MapColor mapColor) {
		super(mapColor);
		this.setGroundCover();
		this.setNoPushMobility();
	}

	public boolean isLiquid() {
		return true;
	}

	public boolean blocksMovement() {
		return false;
	}

	public boolean isSolid() {
		return false;
	}
}
