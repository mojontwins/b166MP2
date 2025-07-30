package com.mojontwins.minecraft.commands;

import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandTpServer extends CommandBase {

	@Override
	public String getString() {
		return "tp";
	}

	@Override
	public int getMinParams() {
		return 3;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World world, EntityPlayer thePlayer) {

		if(thePlayer == null) {
			this.theCommandSender.printMessage(world, "tp: No valid player!");
			return CommandBase.BREAK_AND_FAIL;
		}
		
		// Find if tokens[1] or tokens[2] is a player.
		if(tokens.length == 3) {
			EntityPlayerMP ep1 = CommandProcessor.serverConfigManager.getPlayerEntity(tokens[1]);
			EntityPlayerMP ep2 = CommandProcessor.serverConfigManager.getPlayerEntity(tokens[2]);
			
			if(ep1 == null) {
				this.theCommandSender.printMessage(world, "Can't find user " + tokens [1]);
				return CommandBase.BREAK_AND_FAIL;
			} else if(ep2 == null) {
				this.theCommandSender.printMessage(world, "Can't find user " + tokens [2]);
				return CommandBase.BREAK_AND_FAIL;
			} else if(ep1.dimension != ep2.dimension) {
				this.theCommandSender.printMessage(world, "Users are in different dimensions");
				return CommandBase.BREAK_AND_FAIL;
			}
			
			this.theCommandSender.printMessage(world, "Teleporting " + tokens [1] + " to " + tokens[2] + ".");
			ep1.playerNetServerHandler.teleportTo(ep2.posX, ep2.posY, ep2.posZ, ep2.rotationYaw, ep2.rotationPitch);			
			
			return 0;
		} else {
			
			EntityPlayerMP thePlayerMP = (EntityPlayerMP) thePlayer;
			
			float rotationYaw = thePlayerMP.rotationYaw;
			float rotationPitch = thePlayerMP.rotationPitch;
			
			/*
			double x = thePlayer.posX;
			double y = thePlayer.posY;
			double z = thePlayer.posZ;
			
			try {
				x = Double.parseDouble(tokens [1]);
			} catch (Exception e) { }
			
			try {
				y = Double.parseDouble(tokens [2]);
			} catch (Exception e) { }
			
			try {
				z = Double.parseDouble(tokens [3]);
			} catch (Exception e) { }
			
			*/
			
			int x = this.parseTildeExpression(tokens[1], (int) thePlayer.posX);
			int y = this.parseTildeExpression(tokens[2], (int) thePlayer.posY);
			int z = this.parseTildeExpression(tokens[3], (int) thePlayer.posZ);
			
			thePlayerMP.playerNetServerHandler.teleportTo(x + .5D, y, z + .5D, rotationYaw, rotationPitch);
			
			this.theCommandSender.printMessage(world, "Teleporting " + thePlayer.username + " to " + x + " " + y + " " + z);
			
			return 0;
		}
	}

	@Override
	public String getHelp() {
		return "Teleports the player\n/tp [@a|@p] <x> <y> <z>\nReturns: 0";
	}

}
