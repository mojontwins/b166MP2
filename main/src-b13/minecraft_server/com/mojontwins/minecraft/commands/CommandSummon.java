package com.mojontwins.minecraft.commands;

import java.util.List;

import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.IMobWithLevel;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.SpawnerAnimals;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandSummon extends CommandBase {

	@Override
	public String getString() {
		return "summon";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int res = -1;
		
		boolean spawned = false;
		EntityLiving entity = (EntityLiving) EntityList.createEntityByName(tokens [1], theWorld);
		System.out.println (">" + entity);
		if (entity != null) {
			
			int x, y, z;
			if(thePlayer != null) {
				x = (int)thePlayer.posX + theWorld.rand.nextInt(8) - 4;
				y = (int)thePlayer.posY + theWorld.rand.nextInt(4) + 1;
				z = (int)thePlayer.posZ + theWorld.rand.nextInt(8) - 4;
			} else {
				x = coordinates.posX;
				y = coordinates.posY;
				z = coordinates.posZ;
			}
		
			if(idx > 4) {
				x = this.parseTildeExpression(tokens[2], x);
				y = this.parseTildeExpression(tokens[3], y);
				z = this.parseTildeExpression(tokens[4], z);
			}
			
			System.out.println ("Attempting to spawn @ " + x + " " + y + " " + z);
			entity.setLocationAndAngles((double)x, (double)y, (double)z, theWorld.rand.nextFloat() * 360.0F, 0.0F);
			
			SpawnerAnimals.creatureSpecificInit(entity, theWorld, x, y, z);
			
			if(idx == 3 && entity instanceof IMobWithLevel) {
				try {
					int level = Integer.parseInt(tokens[2]);
					if(level < ((IMobWithLevel)entity).getMaxLevel()) ((IMobWithLevel)entity).setLevel(level);
				} catch (Exception e) { }
			}
			
			this.theCommandSender.printMessage(theWorld, "Spawned " + tokens [1] + " @ " + x + " " + y + " " + z + " id=" + entity.entityId);
			theWorld.spawnEntityInWorld(entity);
			spawned = true;
			res = entity.entityId;
		}
		
		if (!spawned) {
			this.theCommandSender.printMessage(theWorld, "Could not spawn " + tokens [1] + ".");
		} 
		
		return res;
	}

	@Override
	public String getHelp() {
		List<String> entityList = EntityList.getAllEntityStrings();
		String s = "Spawns a living entity.\n/summon <EntityName> [<x> <y> <z>]\nReturns: entityId";
		s = s + "\nAvailable entities: ";
		s = s + String.join(",", entityList);

		return s;
	}

}
