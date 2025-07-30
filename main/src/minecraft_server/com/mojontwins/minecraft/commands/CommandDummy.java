package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandDummy extends CommandBase {

	public CommandDummy() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getString() {
		return "dummy";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		return 0;
	}

	@Override
	public String getHelp() {
		return "";
	}

}
