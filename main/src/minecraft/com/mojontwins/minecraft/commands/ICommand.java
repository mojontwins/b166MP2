package com.mojontwins.minecraft.commands;

import net.minecraft.world.ICommandSender;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public interface ICommand {
	public String getString();
	
	public int getMinParams();
	
	public int execute(String [] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer);
	
	public String getHelp();
	
	public CommandBase withCommandSender(ICommandSender commandSender);
}
