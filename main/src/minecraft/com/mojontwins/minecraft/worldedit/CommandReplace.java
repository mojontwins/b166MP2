package com.mojontwins.minecraft.worldedit;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandReplace extends CommandWorldEdit {

	@Override
	public String getString() {
		return "replace";
	}

	@Override
	public int getMinParams() {
		return 4;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int existingBlockID = -1;
		int existingMeta = -1;
		int blockID = 0;
		int meta = 0;
		
		try {
			existingBlockID = Integer.parseInt(tokens[1]);
			existingMeta = Integer.parseInt(tokens[2]);
			blockID = Integer.parseInt(tokens[3]);
			if(idx > 4) meta = Integer.parseInt(tokens[4]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(this.checkCorners(theWorld)) {
			int cleared = WorldEdit.substitute(theWorld, existingBlockID, existingMeta, blockID, meta);
			this.theCommandSender.printMessage(theWorld, cleared + " blocks filled.");
			
			return cleared;
		} 

		return 0;
	}

	@Override
	public String getHelp() {
		return "Replaces blocks in the active area\n/replace <existingBlockID> <existingMeta> <blockID> <meta>\nmeta == -1 means `existing meta` i.e. any\nReturns: blocks replaced.";
	}

}
