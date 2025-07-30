package com.mojontwins.minecraft.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.world.ICommandSender;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.tile.Block;

public abstract class CommandBase implements ICommand {
	public static final int BREAK_AND_FAIL = -99999999;
	public static final int NOT_FOUND      = -99999998;
	
	protected ICommandSender theCommandSender;
		
	public CommandBase() {
	}
	
	@Override
	public CommandBase withCommandSender(ICommandSender commandSender) {
		this.theCommandSender = commandSender;
		return this;
	}
	
	public int toIntWithDefault(String s, int defVal) {
		int res = defVal;
		try {
			res = Integer.parseInt(s);
		} catch (Exception e) {
			
		}
		return res;
	}
	
	public int parseLValue(String s) {
		if(s == null || !s.startsWith("$")) return -1;
		
		return this.parseRValue(s.substring(1));
	}
	
	public int parseRValue(String s) {
		int res = 0;
		
		if(s.startsWith("$")) {
			res = this.parseRValue(s.substring(1));
			if(res >= 0) res = CommandProcessor.flags[res];
		} else {
			res = this.toIntWithDefault(s, 0);
		}
		
		return res;
	}
	
	public int parseTildeExpression(String expression, int baseValue) {
		/*
		 * expression may be either \d+ or ~([+-]\d+)?
		 * which could be expressed as 
		 */
		
		// Detect tilde expression
		//                 /---group 1----\
		String pattern = "~(([\\+-])(\\d+))?";
		//                  \-grp2-/\-g3-/
		
		Pattern cPattern = Pattern.compile(pattern);
		Matcher matcher = cPattern.matcher(expression);
			
		if(matcher.find()) {
			int result = baseValue;
			int argument = this.toIntWithDefault(matcher.group(3), 0);
			String operand = matcher.group(2);
			
			if("-".equals(operand)) {
				result -= argument;
			} else if("+".equals(operand)) {
				result += argument;
			}
			
			return result;
		} else {
			return this.toIntWithDefault(expression, baseValue);
		}
	}
	
	public int parseItemOrBlockID(String command) {
		int res = 0;
		
		try {
			// Attempt to parse as a number
			res = Integer.parseInt(command);
		} catch (Exception e) {
			// If it fails, browse the items list
			for(Item item : Item.itemsList) {
				if(item != null) {
					if(item instanceof ItemBlock) {
						Block block = Block.blocksList[item.shiftedIndex];
						if(command.equals(block.getBlockName())) {
							res = block.blockID;
						}
					} else if(command.equals(item.getItemName())) {
						res = item.shiftedIndex;
						break;
					}
				}
			}
		}
		
		return res;
	}
	
	public ItemStack parseItemOrBlock(String command) {
		ItemStack itemStack = null;
		
		int itemID = 0; 
		int damage = 0;

		int dotdot = command.indexOf(':');
		
		if(dotdot >= 0) {
			itemID = this.parseItemOrBlockID(command.substring(0, dotdot));
			damage = Integer.parseInt(command.substring(dotdot + 1));
		} else {
			itemID = this.parseItemOrBlockID(command);
		}

		if(itemID > 0) itemStack = new ItemStack(itemID, 1, damage);
		
		return itemStack;
	}
	
	public void rehashArrayFrom(String array[], int idx) {
		for(int i = idx; i < array.length - 1; i ++) {
			array[i] = array[i + 1];
		}
	}
	
	public int executeCommand (String tokens[], int idx, ChunkCoordinates chunkCoordinates, World theWorld, EntityPlayer defaultPlayer) {
		int res = 0;
		
		// This method prerocess player selectors and calls `execute` with the right player object.
		// If there's a player username in the command it is always tokens [1];
		
		if(idx > 1) {
			String username = tokens[1];
			
			if("@p".equals(username) || "@a".equals(username)) {
				List<EntityPlayer> playersList = theWorld.findPlayers(chunkCoordinates, 0, username);
				
				if(playersList != null) {
					if("@p".equals(username)) {
						res = this.execute(tokens, idx, chunkCoordinates, theWorld, playersList.get(0));
						
					} else if("@a".equals(username)) {
						for(int i = 0; i < playersList.size(); i ++) {
							res = this.execute(tokens, idx, chunkCoordinates, theWorld, playersList.get(i));
						}
						
					}
				}
				
				return res;
			} 
			
			EntityPlayer entityPlayer = theWorld.getPlayerForUsername(username);
			
			if(entityPlayer != null) defaultPlayer = entityPlayer;
		}
		
		return this.execute(tokens, idx, chunkCoordinates, theWorld, defaultPlayer);
	}
	
	public boolean shouldList() {
		return true;
	}
}
