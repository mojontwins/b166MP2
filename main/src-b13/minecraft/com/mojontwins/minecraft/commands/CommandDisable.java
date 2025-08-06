package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandDisable extends CommandBase {

	public CommandDisable() {
	}

	@Override
	public String getString() {
		return "disable";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		/*
		if ("beds".equals(tokens [1])) {
			this.theCommandSender.printMessage(theWorld, "Beds are not longer crafteable.");
			CraftingManager.findFirstRecipeToMake(Item.bed.shiftedIndex).setEnabled(false);
		}
		*/
		return 0;
	}

	@Override
	public String getHelp() {
		return "disables stuff\n/enable beds\nReturns: 0";
	}

}
