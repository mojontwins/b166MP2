package com.mojontwins.minecraft.worldedit;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandClear extends CommandWorldEdit {

	@Override
	public String getString() {
		return "clear";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		if(this.checkCorners(theWorld)) {
			int cleared = WorldEdit.clear(theWorld);
			this.theCommandSender.printMessage(theWorld, cleared + " blocks clear.");
			
			return cleared;
		}
		
		return 0;
	}

	@Override
	public String getHelp() {
		return "Clears the active area.\nReturns: blocks cleared.";
	}

}
