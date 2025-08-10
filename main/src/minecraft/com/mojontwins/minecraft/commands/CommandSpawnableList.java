package com.mojontwins.minecraft.commands;

import java.util.List;

import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandSpawnableList extends CommandBase {

	public CommandSpawnableList() {
	}

	@Override
	public String getString() {
		return "creatureList";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		BiomeGenBase biome = theWorld.getBiomeGenForCoords((int)(thePlayer.posX + .5), (int)(thePlayer.posZ + .5));
		int total = 0;
		
		total += this.getList("Monster: ", theWorld, biome.getSpawnableList(EnumCreatureType.monster));
		total += this.getList("Animal: ", theWorld, biome.getSpawnableList(EnumCreatureType.creature));
		total += this.getList("Water: ", theWorld, biome.getSpawnableList(EnumCreatureType.waterCreature));
		total += this.getList("Cave: ", theWorld, biome.getSpawnableList(EnumCreatureType.cave));
		
		return total;
	}

	@Override
	public String getHelp() {
		return "Print a list of creatures that may spawn in this biome.\nReturns: list size";
	}

	private int getList(String caption, World theWorld, List<SpawnListEntry> list) {
		boolean first = true;
		for(SpawnListEntry sle : list) {
			if(first) first = false; else caption += ", ";
			caption += EntityList.getNameByClass(sle.entityClass); 
		}
		this.theCommandSender.printMessage(theWorld, caption);
		
		return list.size();
	}
}
