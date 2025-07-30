package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandTime extends CommandBase {

	@Override
	public String getString() {
		return "time";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer player) {
		if("set".equals(tokens [1])) {
			int timeSet = -1;
			if ("night".equals(tokens [2])) {
				timeSet = 14000;
			} else if ("day".equals(tokens [2])) {
				timeSet = 1000;
			} else {
				timeSet = this.toIntWithDefault(tokens [2], -1);
			}
			if(timeSet >= 0) {
				long timeBaseDay = theWorld.getWorldTime() / 24000L * 24000L;
				long elapsedDay = theWorld.getWorldTime() % 24000L;
				if (timeSet > elapsedDay) timeBaseDay += 24000L;
				theWorld.setWorldTime(timeBaseDay + timeSet);
				this.theCommandSender.printMessage(theWorld, "Time set to " + timeSet);
				return timeSet;
			} else {
				this.theCommandSender.printMessage(theWorld, "Wrong time!");
				return -1;
			}
		} else {
			return (int) (theWorld.getWorldTime() % 24000L);
		}
	}

	@Override
	public String getHelp() {
		return "Sets the world time (0-23999)\n/time [set <n>|day|night]\nReturns: time set";
	}

}
