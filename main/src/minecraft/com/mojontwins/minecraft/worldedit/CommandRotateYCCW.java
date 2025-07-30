package com.mojontwins.minecraft.worldedit;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandRotateYCCW extends CommandWorldEdit {

	@Override
	public String getString() {
		return "rotatey_ccw";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		WorldEdit.rotate_ccw();
		return 0;
	}

	@Override
	public String getHelp() {
		return "Rotates the clipboard counter-clockwise around the Y axis";
	}

}
