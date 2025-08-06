package net.minecraft.world.entity;

public interface IDyeableEntity {
	public boolean admitsDyeing();
	public int getDyeColor();
	public void setDyeColor(int dyeColor);
}
