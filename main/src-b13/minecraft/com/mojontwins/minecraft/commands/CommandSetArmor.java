package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.IArmoredMob;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandSetArmor extends CommandBase {

	@Override
	public String getString() {
		return "setArmor";
	}

	@Override
	public int getMinParams() {
		return 3;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int entityId = this.toIntWithDefault(tokens[1], -1);
		int armorType = ItemArmor.toArmorType(tokens[2]);
		int itemId = ItemArmor.toItemId(armorType, tokens[3]);
		
		if(entityId >= 0 && armorType >= 0 && itemId >= 0) {
			Item item = Item.itemsList[itemId];
			if(item != null) {
				Entity entity = theWorld.getEntityById(entityId);
				if(entity instanceof IArmoredMob) {
					((IArmoredMob)entity).setArmor(armorType, new ItemStack(item));
				}
			}
		}
		
		return entityId;
	}

	@Override
	public String getHelp() {
		String s = "Adds an armor piece to a living entity (if supported)\n/setArmor <entityId> <armorType> <itemId>\n";
		s = s + "armorType: " + String.join(", ", ItemArmor.parts) + "\n";
		s = s + "itemId: block id/name, or " + String.join(", ", ItemArmor.armorFilenamePrefix) + "\n";
		s = s + "Blocks may only be used as helmets.\nReturns: entityId";
		return s;
	}

}
