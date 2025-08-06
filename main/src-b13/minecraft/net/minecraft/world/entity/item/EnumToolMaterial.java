package net.minecraft.world.entity.item;

public enum EnumToolMaterial {
	WOOD(0, 59, 2.0F, 0, 15),
	STONE(1, 131, 4.0F, 1, 5),
	IRON(2, 250, 6.0F, 2, 14),
	EMERALD(3, 1561, 8.0F, 3, 10),
	GOLD(0, 32, 12.0F, 0, 22),
	TOOL_IRONWOOD(2, 512, 6.5F, 2, 25),
	TOOL_FIERY(4, 1024, 9.0F, 4, 10),
	TOOL_STEELEAF(3, 131, 8.0F, 3, 9);

	private final int harvestLevel;
	private final int maxUses;
	private final float efficiencyOnProperMaterial;
	private final int damageVsEntity;
	private final int enchantability;

	private EnumToolMaterial(int i3, int i4, float f5, int i6, int i7) {
		this.harvestLevel = i3;
		this.maxUses = i4;
		this.efficiencyOnProperMaterial = f5;
		this.damageVsEntity = i6;
		this.enchantability = i7;
	}

	public int getMaxUses() {
		return this.maxUses;
	}

	public float getEfficiencyOnProperMaterial() {
		return this.efficiencyOnProperMaterial;
	}

	public int getDamageVsEntity() {
		return this.damageVsEntity;
	}

	public int getHarvestLevel() {
		return this.harvestLevel;
	}

	public int getEnchantability() {
		return this.enchantability;
	}
}
