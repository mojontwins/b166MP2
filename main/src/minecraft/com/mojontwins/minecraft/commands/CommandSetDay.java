package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.IWorldAccess;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandSetDay extends CommandBase {

	public CommandSetDay() {
	}

	@Override
	public String getString() {
		return "setDay";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int res = -1;
		
		try {
			int day = Integer.parseInt(tokens[1]);
			int dayWithinYear = day % (4 * Seasons.SEASON_DURATION);
			long currentYear = theWorld.getWorldTime() / (4 * Seasons.SEASON_DURATION);
			int currentDayWithinYear = (int)(theWorld.getWorldTime() % (4 * Seasons.SEASON_DURATION));
			if(currentDayWithinYear > dayWithinYear) currentYear ++;
			
			theWorld.setWorldTime(currentYear * (4 * Seasons.SEASON_DURATION) + dayWithinYear);
			
			Seasons.dayOfTheYear = dayWithinYear;
			Seasons.updateSeasonCounters();
			
			for(int i5 = 0; i5 < theWorld.worldAccesses.size(); ++i5) {
				((IWorldAccess)theWorld.worldAccesses.get(i5)).updateAllRenderers();
			}
			
			this.theCommandSender.printMessage(theWorld, "Day set to " + dayWithinYear + ": " + Seasons.seasonNames[Seasons.currentSeason] + ", day " + Seasons.dayOfTheSeason);
			
			res = dayWithinYear;
		} catch (Exception e) {
			System.out.println (e);
			this.theCommandSender.printMessage(theWorld, "Wrong day");
		}
		
		return res;
	}

	@Override
	public String getHelp() {
		return "Sets the current day of the year (0 - " + (4 * Seasons.SEASON_DURATION - 1) + ")\n/setDay <day>\nReturns: day of the year";
	}

}
