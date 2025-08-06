package net.minecraft.world.level.material;

public class MaterialPortal extends Material {
	public MaterialPortal() {
		super();
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
