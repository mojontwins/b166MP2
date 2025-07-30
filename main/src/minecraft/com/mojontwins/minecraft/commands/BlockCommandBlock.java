package com.mojontwins.minecraft.commands;

import java.util.Random;

import net.minecraft.world.ICommandSender;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.BlockContainer;
import net.minecraft.world.level.tile.entity.TileEntity;

public class BlockCommandBlock extends BlockContainer implements ICommandSender {
	private boolean debug = true;
	private String outputMessage;

	public BlockCommandBlock(int id) {
		super(id, 161, Material.circuits);
	}

	@Override
	public void printMessage(World world, String message) {
		if(this.debug) System.out.println ("Command Block: " + message);
		outputMessage = message;
	}

	@Override
	public BlockPos getMouseOverCoordinates() {
		return null;
	}

	@Override
	public TileEntity getBlockEntity() {
		return new TileEntityCommandBlock();
	}
	
	@Override
	public int tickRate() {
		return 1;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		if(!world.isRemote) {
			if(world.isBlockIndirectlyGettingPowered(x, y, z)) {
				world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate());
			}

			if(this.surroundingCommandBlockSucceeded(world, x, y, z)) {
				this.updateTick(world, x, y, z, world.rand);
			}
		}
	}
	
	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		/*
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if(tileEntity != null && tileEntity instanceof TileEntityCommandBlock) {
			TileEntityCommandBlock tileEntityCommandBlock = (TileEntityCommandBlock) tileEntity;
			player.displayGUICommandBlock(tileEntityCommandBlock);
		}
		*/
		return true;
	}

	@Override 
	public void updateTick(World world, int x, int y, int z, Random rand) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if(tileEntity != null && tileEntity instanceof TileEntityCommandBlock) {
			TileEntityCommandBlock tileEntityCommandBlock = (TileEntityCommandBlock) tileEntity;
			
			/*if(world.isBlockIndirectlyGettingPowered(x, y, z) || this.surroundingCommandBlockSucceeded(world, x, y, z))*/ {
				if(!tileEntityCommandBlock.blocked) {
					String command = tileEntityCommandBlock.command;
					
					if(command != null && command != "") {
						CommandProcessor.withCommandSender(this);
						this.outputMessage = "";;
						
						int result = CommandProcessor.executeCommand(
								command.trim(), 
								world, 
								world.getPlayersInRangeFrom(x, y, z, 16), 
								null, 
								new ChunkCoordinates(x, y, z)
							);
						
						if(result <= 0) {
							tileEntityCommandBlock.commandResults = this.outputMessage;
						} else {
							if(tileEntityCommandBlock.looper && world.isBlockIndirectlyGettingPowered(x, y, z)) {
								world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate());
							}
							
							// Notify
							int meta = world.getBlockMetadata(x, y, z);
							world.setBlockMetadata(x, y, z, meta | 1);
							this.notifySurroundingCommandBlocks(world, x, y, z, this.blockID);
							world.setBlockMetadata(x, y, z, meta & 0xfe); // SAFE
							
							tileEntityCommandBlock.commandResults = "Executed OK! [" + result + "]";
						}
					}
				}
				
			} 
			
		}

	}

	private void notifySurroundingCommandBlocks(World world, int x, int y, int z, int blockID) {
		this.notifyCommandBlock(world, x - 1, y, z);
		this.notifyCommandBlock(world, x + 1, y, z);
		this.notifyCommandBlock(world, x, y, z - 1);
		this.notifyCommandBlock(world, x, y, z + 1);
		this.notifyCommandBlock(world, x, y - 1, z);
		this.notifyCommandBlock(world, x, y + 1, z);
	}

	private void notifyCommandBlock(World world, int x, int y, int z) {
		if(world.getBlockId(x, y, z) == this.blockID) {
			
			// Only notify if it didn't just run.
			if((world.getBlockMetadata(x, y, z) & 1) == 0) {
				world.notifyBlockOfNeighborChange(x, y, z, this.blockID);
			}
		}
		
	}

	private boolean surroundingCommandBlockSucceeded(World world, int x, int y, int z) {
		return this.commandBlockSucceeded(world, x - 1, y, z) ||
				this.commandBlockSucceeded(world, x + 1, y, z) ||
				this.commandBlockSucceeded(world, x, y, z - 1) ||
				this.commandBlockSucceeded(world, x, y, z + 1) ||
				this.commandBlockSucceeded(world, x, y - 1, z) ||
				this.commandBlockSucceeded(world, x, y + 1, z); 
	}

	private boolean commandBlockSucceeded(World world, int x, int y, int z) {
		return world.getBlockId(x, y, z) == this.blockID &&
				(world.getBlockMetadata(x, y, z) & 1) != 0;
	}
	
}
