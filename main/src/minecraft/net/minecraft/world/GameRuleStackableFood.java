package net.minecraft.world;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFood;

public class GameRuleStackableFood extends GameRule {
	public void setValue(boolean value) {
		this.value = value;
		
	
		for(Item item : Item.itemsList) {
			if(item instanceof ItemFood) {
				ItemFood itemFood = (ItemFood) item;
				if(this.value) {
					itemFood.setMaxStackSize(64);
				} else {
					itemFood.setMaxStackSize(1);
				}
			}
		}
	}
}
