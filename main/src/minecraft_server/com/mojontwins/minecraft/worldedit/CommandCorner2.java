package com.mojontwins.minecraft.worldedit;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandCorner2 extends CommandWorldEdit {

	@Override
	public String getString() {
		return "corner2";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int x = 0, y = -1, z = 0;
		BlockPos blockPos = this.theCommandSender.getMouseOverCoordinates();
		if(blockPos != null) {
			x = blockPos.x; y = blockPos.y; z = blockPos.z;
		}
		
		if (idx == 4) {
			x = Integer.parseInt(tokens[1]);
			z = Integer.parseInt(tokens[2]);
			y = Integer.parseInt(tokens[3]);
		}
		
		if(y == -1) {
			this.theCommandSender.printMessage(theWorld, "No block in range");		
			return 0;
		} else {
			WorldEdit.setCorner2(x, y, z);
			this.theCommandSender.printMessage(theWorld, "1st point set to " + x + ", " + y + ", " + z);		
			return 1;
		}
	}

	@Override
	public String getHelp() {
		return "Sets corner 2\n/corner2 [<x> <y> <z>]";
	}

}
