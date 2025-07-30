package net.minecraft.src;

import java.util.List;

public interface ICrafting {
	void updateCraftingInventory(Container container1, List list2);

	void updateCraftingInventorySlot(Container container1, int i2, ItemStack itemStack3);

	void updateCraftingInventoryInfo(Container container1, int i2, int i3);
}
