package net.minecraft.world;

import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;

public interface ICommandSender {
	public void printMessage(World world, String message);

	BlockPos getMouseOverCoordinates();
}
