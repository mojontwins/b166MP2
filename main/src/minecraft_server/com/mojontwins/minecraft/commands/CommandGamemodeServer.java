package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandGamemodeServer extends CommandBase {

	@Override
	public String getString() {
		return "gamemode";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		if(thePlayer == null) return 0;
		
		String gameMode = tokens [1];
		
		int res = thePlayer.capabilities.isCreativeMode ? 1 : 0;
		
		if ("0".equals(gameMode) || "survival".equals(gameMode)) {
			if (thePlayer.capabilities.isCreativeMode) this.theCommandSender.printMessage(theWorld, "Game mode changed to survival");
			thePlayer.capabilities.isCreativeMode = false;
			thePlayer.capabilities.isFlying = false;
			res = 0;
		} else if ("1".equals(gameMode) || "creative".equals(gameMode)) {
			if (!thePlayer.capabilities.isCreativeMode) this.theCommandSender.printMessage(theWorld, "Game mode changed to creative");
			thePlayer.capabilities.isCreativeMode = true;
			res = 1;
		}
		
		return res;
	}

	@Override
	public String getHelp() {
		return "Sets the game mode for the current player\n/gamemode 0|1|survival|creative [<username>]\nReturns: game mode (0 or 1)";
	}

}
