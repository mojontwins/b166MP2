package net.minecraft.world.item;

import java.util.Comparator;

import net.minecraft.world.level.tile.Block;

public class ItemSorter implements Comparator<Object> {
	private int compareItems(ItemStack itemStack1, ItemStack itemStack2) {
		if(itemStack2 == null || itemStack2.getItem() == null) {
			if(itemStack1 == null || itemStack1.getItem() == null) {
				return 0;
			}
			return 1;
		}
		if(itemStack1 == null || itemStack1.getItem() == null) {
			return -1;
		}
		
		// Always blocks before items
		
		int itemId1 = itemStack1.itemID;
		int itemId2 = itemStack2.itemID;
		
		if(itemId1 < 256 && itemId2 < 256) {
						
			int res = Block.blocksList[itemId1].blockMaterial.toString().compareTo(
					Block.blocksList[itemId2].blockMaterial.toString());
			
			if(res != 0) return res;
		} else if(itemId1 < 256) {
			return 1;
		} else if(itemId2 < 256) {
			return -1;
		} else if(itemId1 >= 256 && itemId2 >= 256) {
			return 0;
		}
		
		int result;
		/*
		String classString1 = itemStack1.getItem().getClass().toString();
		String classString2 = itemStack2.getItem().getClass().toString();
		
		result = classString1.compareTo(classString2);
		if(result == 0)*/ {
			// Same class, compare by name. Using "itemname" will group blocks / items with same ID, different metadata / damage.
			String itemName1 = itemStack1.getItem().getItemName();
			String itemName2 = itemStack2.getItem().getItemName();
			
			if(itemName1 == null && itemName2 == null) {
				result = 0;
			} else if(itemName1 == null) {
				result = -1;
			} else if(itemName2 == null) {
				result = 1;
			} else {
				result = itemStack1.getItemName().compareTo(itemStack2.getItemName());
			}
			
			if(result == 0) {
				result = itemStack1.getItemDamage() >= itemStack2.getItemDamage() ? 1 : -1;
			}
		}
		
		return result;
	}
	
	@Override
	public int compare(Object o1, Object o2) {
		return this.compareItems((ItemStack)o1, (ItemStack)o2);
	}

}
