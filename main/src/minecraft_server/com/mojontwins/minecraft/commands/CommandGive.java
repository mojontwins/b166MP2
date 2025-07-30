package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandGive extends CommandBase {

	public CommandGive() {
	}

	@Override
	public String getString() {
		return "give";
	}

	@Override
	public int getMinParams() {
		return 2;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		if(thePlayer == null) return 0;
		int res = 0;
		
		ItemStack itemStack = this.parseItemOrBlock(tokens [1]);
		
		if(itemStack != null) {
			
			int blockID = itemStack.itemID; 
			int metadata = itemStack.itemDamage;
			int quantity = 0;
			
			try {			
				quantity = Integer.parseInt(tokens[2]);
									
				if(quantity > 0 && blockID > 0) {
					float f6 = 0.7F;
					double d7 = (double)(theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
					double d9 = (double)(theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
					double d11 = (double)(theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
					EntityItem entityItem13 = new EntityItem(theWorld, thePlayer.posX + d7, thePlayer.posY + d9, thePlayer.posZ + d11, new ItemStack(blockID, quantity, metadata));
					entityItem13.delayBeforeCanPickup = 10;
					theWorld.spawnEntityInWorld(entityItem13);
					
					res = blockID;
				}
			} catch (Exception e) { }
			
		}
		
		return res;
	}

	@Override
	public String getHelp() {
		return "Gives an amount of block:meta or item:damage\n/give <blockID:meta>|<itemID:damage> <quantity>\nitemIDs must be shifted indexes.\nReturns: blockID/itemID";
	}

}
