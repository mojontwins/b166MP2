package com.mojontwins.minecraft.commands;

import net.minecraft.network.packet.Packet89SetArmor;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandSetHeldItemServer extends CommandBase {

	@Override
	public String getString() {
		return "setHeldItem";
	}

	@Override
	public int getMinParams() {
		return 2;
	}

	// TODO reuse code to make ItemStack with id:meta and / or by name
	
	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int entityId = this.toIntWithDefault(tokens[1], -1);
		ItemStack itemStack = this.parseItemOrBlock(tokens[2]);
		
		if(entityId >= 0 && itemStack != null && itemStack.itemID > 0) {
			Entity entity = theWorld.getEntityById(entityId);
			if(entity instanceof EntityLiving) {
				((EntityLiving) entity).setHeldItem(itemStack);
			}
		}
		
		if(itemStack != null) {
			((EntityPlayerMP)thePlayer).playerNetServerHandler.sendPacket(new Packet89SetArmor(entityId, 100, itemStack.itemID));
		}
		
		return entityId;
	}

	@Override
	public String getHelp() {
		return "Sets the held item for a living entity (if supported)\n/setHeldItem <entityId> <itemId>\nReturns: entityId";
	}

}
