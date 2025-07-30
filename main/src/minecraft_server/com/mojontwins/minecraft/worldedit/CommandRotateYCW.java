package com.mojontwins.minecraft.worldedit;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandRotateYCW extends CommandWorldEdit {

	@Override
	public String getString() {
		return "rotatey_cw";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		WorldEdit.rotate_cw();
		return 0;
	}

	@Override
	public String getHelp() {
		return "Rotates the clipboard clockwise around the Y axis";
	}

}
