package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandRain extends CommandBase {

	public CommandRain() {
	}

	@Override
	public String getString() {
		return "rain";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		theWorld.getWorldInfo().setRainTime(0);
		return 0;
	}

	@Override
	public String getHelp() {
		return "Toggles rain on<->off\nReturns: 0";
	}

}
