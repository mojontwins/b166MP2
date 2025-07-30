package net.minecraft.world.item;

import net.minecraft.world.GameRules;

public class ItemPorkCooked extends ItemFood {

	public ItemPorkCooked(int i1, int i2, float f3, boolean z4) {
		super(i1, i2, f3, z4);
		
	}

	@Override
	public Item fixForAlphaBeta() {
		if(!GameRules.boolRule("edibleCows")) this.iconIndex = 6*16+10;
		return this;
	}
}
