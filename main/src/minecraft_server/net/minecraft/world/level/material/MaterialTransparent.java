package net.minecraft.world.level.material;

public class MaterialTransparent extends Material {
	public MaterialTransparent() {
		super();
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
