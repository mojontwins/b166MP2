package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandLet extends CommandBase {

	public CommandLet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getString() {
		return "let";
	}

	@Override
	public int getMinParams() {
		return 2;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		try {
			int index = this.parseLValue(tokens[1]);
			int value = this.parseRValue(tokens[2]);
						
			CommandProcessor.flags[index] = value;
			
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			this.theCommandSender.printMessage(theWorld, "Wrong lvalue or rvalue");
			return 0;
		}
	}

	@Override
	public String getHelp() {
		return "Assigns a value to a flag\n/let <lvalue> <rvalue>\nReturns: <rvalue>";
	}

}
