package com.mojontwins.minecraft.commands;

import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.client.player.PlayerControllerCreative;
import net.minecraft.client.player.PlayerControllerSP;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandGamemodeClient extends CommandBase {

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
			if(thePlayer instanceof EntityPlayerSP) {
				EntityPlayerSP playerSP = (EntityPlayerSP)thePlayer;
				playerSP.mc.playerController = new PlayerControllerSP(playerSP.mc);
				PlayerControllerCreative.disableAbilities(playerSP);
			}
			theWorld.getWorldInfo().setGameType(0);
			res = 0;
		} else if ("1".equals(gameMode) || "creative".equals(gameMode)) {
			if (!thePlayer.capabilities.isCreativeMode) this.theCommandSender.printMessage(theWorld, "Game mode changed to creative");
			if(thePlayer instanceof EntityPlayerSP) {
				EntityPlayerSP playerSP = (EntityPlayerSP)thePlayer;
				playerSP.mc.playerController = new PlayerControllerCreative(playerSP.mc);
				PlayerControllerCreative.enableAbilities(playerSP);
				playerSP.setAir(300);
				playerSP.setHealth(20);
				playerSP.getFoodStats().setFoodLevel(20);
				playerSP.getFoodStats().setFoodSaturationLevel(5F);
			}
			theWorld.getWorldInfo().setGameType(1);
			res = 1;
		}
		
		return res;
	}

	@Override
	public String getHelp() {
		return "Sets the game mode for the current player\n/gamemode 0|1|survival|creative [<username>]\nReturns: game mode (0 or 1)";
	}

}
