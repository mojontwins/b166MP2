package com.mojontwins.minecraft.worldedit;

import com.mojontwins.minecraft.commands.CommandBase;

import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;

public abstract class CommandWorldEdit extends CommandBase {
	public BlockPos pointingAt;
	
	public boolean checkCorners(World world) {
		if(!WorldEdit.checkCorners()) {
			this.theCommandSender.printMessage(world, "Set points first!");
			return false;
		}
		
		return true;
	}
	
}
