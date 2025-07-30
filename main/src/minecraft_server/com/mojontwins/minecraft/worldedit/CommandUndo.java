package com.mojontwins.minecraft.worldedit;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandUndo extends CommandWorldEdit {

	@Override
	public String getString() {
		return "undo";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		if(WorldEdit.hasUndo) {
			this.theCommandSender.printMessage(theWorld, "Undoing to " + WorldEdit.undoOrigin);
			WorldEdit.undo(theWorld);
			
			return 1;
		}
		
		this.theCommandSender.printMessage(theWorld, "Nothing to undo");
		return 0;
	}

	@Override
	public String getHelp() {
		return "Undoes last action.\nReturns: 1 on success.";
	}

}
