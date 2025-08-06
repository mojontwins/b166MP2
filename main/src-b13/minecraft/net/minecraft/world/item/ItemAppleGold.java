package net.minecraft.world.item;

public class ItemAppleGold extends ItemFood {
	public ItemAppleGold(int i1, int i2, float f3, boolean z4) {
		super(i1, i2, f3, z4);
	}

	public boolean hasEffect(ItemStack itemStack1) {
		return true;
	}

	public EnumRarity getRarity(ItemStack itemStack1) {
		return EnumRarity.epic;
	}
}
