package net.minecraft.world.item;

public enum EnumArmorMaterial {
	CLOTH(5, new int[]{1, 3, 2, 1}, 15),
	CHAIN(15, new int[]{2, 5, 4, 1}, 12),
	IRON(15, new int[]{2, 6, 5, 2}, 9),
	GOLD(7, new int[]{2, 5, 3, 1}, 25),
	DIAMOND(33, new int[]{3, 8, 6, 3}, 10),
	// 5
	ARMOR_NAGA(21, new int[]{2, 7, 6, 3}, 15),
	ARMOR_IRONWOOD(20, new int[]{2, 7, 5, 2}, 15),
	ARMOR_FIERY(25, new int[] {4, 9, 7, 4}, 10),
	ARMOR_STEELLEAF(10, new int[] {3, 8, 6, 3}, 9),
	// 9
	ARMOR_CRYSTAL(22, new int[] {3, 8, 6, 3}, 15),
	ARMOR_ARCTITE(17, new int[] {2, 6, 5, 2}, 11),
	ARMOR_PELT(11, new int[] {2, 4, 3, 2}, 15),
	ARMOR_COLDSTEEL(25, new int[] {3, 12, 8, 3}, 22),
	ARMOR_EPIC(22, new int[] {3, 8, 6, 3}, 24);
	
	private int maxDamageFactor;
	private int[] damageReductionAmountArray;
	private int enchantability;

	private EnumArmorMaterial(int i3, int[] i4, int i5) {
		this.maxDamageFactor = i3;
		this.damageReductionAmountArray = i4;
		this.enchantability = i5;
	}

	public int getDurability(int i1) {
		return ItemArmor.getMaxDamageArray()[i1] * this.maxDamageFactor;
	}

	public int getDamageReductionAmount(int i1) {
		return this.damageReductionAmountArray[i1];
	}

	public int getEnchantability() {
		return this.enchantability;
	}
}
