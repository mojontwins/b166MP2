package com.mojontwins.minecraft.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.server.IServerConfigManager;
import net.minecraft.world.ICommandSender;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.PlayerPositionComparator;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class CommandProcessor {
	public static Map<String,ICommand> commandsMap = new HashMap<String,ICommand>();	
	public static ICommandSender commandSender = null;
	public static int[] flags = new int[256]; 
	public static IServerConfigManager serverConfigManager = null;
	
	public static List<String> tokenizeWithBraces(String s) {
		// Cheap
		s = s + " ";
		
		// Tokenizes a space separated string of tokens with 1 level of curly brazes
		ArrayList<String> result = new ArrayList<String> ();
		
		char charArray[] = s.toCharArray();
		
		// Number of nested braces. Will be "in braces" until this is 0.
		int braces = 0;
		
		// Current token
		String curToken = "";
		
		for(int i = 0; i < charArray.length; i ++) {
			boolean addWord = false;
			char c = charArray[i];

			if (c == ' ' || c == '\t' || c == '\n') {
				if(braces == 0) {
					addWord = true;
				} else {
					curToken += String.valueOf(c);
				}
			} else {
				curToken += String.valueOf(c);
			
				if (c == '{') {
					braces ++;
				} else if (c == '}') {
					if(braces > 0) braces --;
					if(braces == 0) addWord = true;
				}
			} 
			
			if(addWord) {
				if(curToken.length() > 0) {
					result.add(curToken);
					curToken = "";
				}
			}
			
		}
		
		return result;
	}

	public static int executeCommand(String command, World theWorld, List<EntityPlayer> playersList, EntityPlayer thePlayer, ChunkCoordinates coords) {
		List<EntityPlayer> targetPlayers = new ArrayList<EntityPlayer> ();
		List<String> tokensRaw = tokenizeWithBraces(command.trim());
		
		CommandBase generalCommand = new CommandDummy();
		
		int res = 0;
		
		// First recur
		
		command = "";

		for(int i = 0; i < tokensRaw.size(); i ++) {
			if(i > 0) command += " ";
			String token = tokensRaw.get(i);
			if(token.indexOf("{") >= 0) {
				int commandResult = executeCommand(token.substring(1, token.length() - 1).trim(), theWorld, playersList, thePlayer, coords);
				
				if(commandResult == CommandBase.BREAK_AND_FAIL) {
					commandSender.printMessage(theWorld, "Reached Break & Fail!");
					return 0;
				}
				
				command = command + commandResult;
			} else {
				command = command + token;
			}
		}
		
		// Now command can be executed directly but may contain a target operand
		// cmd target arguments or cmd arguments.
		
		// This section prepares the execution and calls the command `execute`
		// with a EntityPlayer object.
		
		// If `playersList` is null we don't have to parse and we call it directly
		// using `thePlayer`
		
		StringTokenizer tokenizer = new StringTokenizer(command);
		
		int numTokens = tokenizer.countTokens();
		if (numTokens == 0) return res;
		
		String[] tokens = new String [numTokens];
		int idx = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens [idx++] = tokenizer.nextToken();
		}
		
		String cmd = tokens[0];
		
		if(cmd.startsWith("$")) {
			tokens [0] = cmd = "" + generalCommand.parseRValue(cmd);
		} else if(cmd.startsWith("/")) {
			cmd = cmd.substring(1);
		}

		// Override everything if cmd is a number
		if(cmd.matches("\\d+")) {
			int i = 0;
			while(i < numTokens && tokens[i].matches("\\d+")) {
				res = Integer.parseInt(tokens[i]);
				cmd = tokens[i];
				i ++;
			}
		} else {
			
			if("help".equals(cmd)) {
				if(idx == 1) {
					// Show all registered commands
					commandSender.printMessage(theWorld, "/help <command>");
					commandSender.printMessage(theWorld, String.join(", ", commandsMap.keySet()));
				} else {
					ICommand iCommand = commandsMap.get(tokens [1]);
					if(iCommand != null) {
						commandSender.printMessage(theWorld, iCommand.getHelp());
					} else {
						commandSender.printMessage(theWorld, "Command " + cmd + " not found.");
					}
				}
				
				return 0;
			}
			
			if(playersList != null && playersList.size() > 0 && idx > 1) {
				if("@a".equals(tokens[1])) {
					targetPlayers = playersList;
					
				} else if("@p".equals(tokens[1])) {
					// Find closest player
					Collections.sort(playersList, new PlayerPositionComparator(coords));
					targetPlayers.add(playersList.get(0));
					
				} else {
					// Find player by username
					String username = tokens[1];
					
					for(EntityPlayer curPlayer : playersList) {
						if(curPlayer.username.equals(username)) {
							targetPlayers.add(curPlayer);
							break;
							
						}	
					}
				}
			}
			
			// If no target could be added, target is default player:
			
			if(targetPlayers.size() == 0) {
				ICommand iCommand = commandsMap.get(cmd);
				
				if(iCommand != null) {
					if(idx <= iCommand.getMinParams()) {
						commandSender.printMessage(theWorld, "Not enough params. Use /help " + iCommand.getString());
						res = CommandBase.BREAK_AND_FAIL;
					} else {
						iCommand.withCommandSender(commandSender);
						res = iCommand.execute(tokens, idx, coords, theWorld, thePlayer);
					}
				} else res = CommandBase.NOT_FOUND;
	
			} else {
				
				// We picked a target from the command, so we need to rehash
				
				for(int i = 1; i < idx; i ++) {
					tokens[i] = tokens[i + 1];
					idx --;
				}
			
				for(EntityPlayer curPlayer : targetPlayers) {
					
					ICommand iCommand = commandsMap.get(cmd);
					if(iCommand != null) {
						if(idx <= iCommand.getMinParams()) {
							commandSender.printMessage(theWorld, "Not enough params. Use /help " + iCommand.getString());
							res = CommandBase.BREAK_AND_FAIL;
						} else {
							iCommand.withCommandSender(commandSender);
							res = iCommand.execute(tokens, idx, coords, theWorld, curPlayer);
						}
 					} else res = CommandBase.NOT_FOUND;
					
				}
				
			}
		}

		return res;
	}

	public static void registerCommands() {		
		registerCommand(CommandGamemodeServer.class);
		registerCommand(CommandSetArmorServer.class);
		registerCommand(CommandSetHeldItemServer.class);
		registerCommand(CommandSummon.class);
		registerCommand(CommandTimeServer.class);
		registerCommand(CommandTpServer.class);
		registerCommand(CommandSnow.class);
		registerCommand(CommandRain.class);
		registerCommand(CommandThunder.class);
		registerCommand(CommandSetDay.class);
		registerCommand(CommandGive.class);
		registerCommand(CommandSetTile.class);
		registerCommand(CommandStop.class);
		registerCommand(CommandIf.class);
		registerCommand(CommandPlayerHas.class);
		registerCommand(CommandLet.class);
		//registerCommand(CommandEnable.class);
		//registerCommand(CommandDisable.class);
	}

	public static void registerCommand(Class<?> commandClass) {
		try {
			ICommand command = (ICommand)commandClass.getConstructor().newInstance();
			commandsMap.put(command.getString(), command);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void withCommandSender(ICommandSender iCommandSender) {
		commandSender = iCommandSender;
	}
	
	public static void withServerConfigManager(IServerConfigManager iServerConfigManager) {
		serverConfigManager = iServerConfigManager;
	}
	
	public static void readFromNBT(NBTTagCompound nBTTagCompound) {
		NBTTagCompound flagsNBT = nBTTagCompound.getCompoundTag("CommandFlags");
		if (flagsNBT != null) {
			for(int i = 0; i < 256; i ++) {
				if(flagsNBT.hasKey("flag" + i)) {
					flags[i] = flagsNBT.getInteger("flag" + i);
				} else {
					flags[i] = 0;
				}
			}
		}
	}
	
	public static void writeToNBT(NBTTagCompound nBTTagCompound) {
		NBTTagCompound flagsNBT = new NBTTagCompound();
		for(int i = 0; i < 256; i ++) {
			if(flags[i] != 0) {
				flagsNBT.setInteger("flag" + i, flags[i]);
			}
		}
		
		nBTTagCompound.setCompoundTag("CommandFlags", flagsNBT);
	}
}
