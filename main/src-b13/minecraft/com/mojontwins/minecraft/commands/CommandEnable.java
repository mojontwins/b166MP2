package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandEnable extends CommandBase {

	public CommandEnable() {
	}

	@Override
	public String getString() {
		return "enable";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		/*
		if ("beds".equals(tokens [1])) {
			this.theCommandSender.printMessage(theWorld, "Beds are now crafteable.");
			CraftingManager.findFirstRecipeToMake(Item.bed.shiftedIndex).setEnabled(true);
		}
		*/
		return 0;
	}

	@Override
	public String getHelp() {
		return "enables stuff\n/enable beds\nReturns: 0";
	}

}
