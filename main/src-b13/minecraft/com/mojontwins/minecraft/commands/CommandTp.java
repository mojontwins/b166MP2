package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandTp extends CommandBase {

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
		
		thePlayer.setPosition(x + .5D, y, z + .5D);
		
		this.theCommandSender.printMessage(world, "Teleporting " + thePlayer.username + " to " + x + " " + y + " " + z);
		
		return 0;
	}

	@Override
	public String getHelp() {
		return "Teleports the player\n/tp [@a|@p] <x> <y> <z>\nReturns: 0";
	}

}
