package net.minecraft.server;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.mojontwins.minecraft.commands.CommandBase;
import com.mojontwins.minecraft.commands.CommandProcessor;
import com.mojontwins.minecraft.commands.ComplexCommand;

import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.world.ICommandSender;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;

public class ConsoleCommandHandler implements ICommandSender {
	private static Logger minecraftLogger = Logger.getLogger("Minecraft");
	private MinecraftServer minecraftServer;
	private ComplexCommand latestCommand;
	private ICommandListener commandListener;

	public ConsoleCommandHandler(MinecraftServer minecraftServer1) {
		this.minecraftServer = minecraftServer1;
	}

	public synchronized void handleCommand(ServerCommand serverCommand) {
		this.latestCommand = serverCommand.command;
		String commandLine = this.latestCommand.command;
		
		String[] tokens = commandLine.split(" ");
		String command = tokens[0];
		String parameters = commandLine.substring(command.length()).trim();
		this.commandListener = serverCommand.commandListener;
		String username = this.commandListener.getUsername();
		ServerConfigurationManager serverConfigurationManager = this.minecraftServer.configManager;
		WorldServer worldServer;
		EntityPlayerMP thePlayerMP;
		String sParam;
		
		if(!commandLine.equalsIgnoreCase("help") && !command.equalsIgnoreCase("?")) {
			if(command.equalsIgnoreCase("list")) {
				this.commandListener.log("Connected players: " + serverConfigurationManager.getPlayerList());
				
			} else if(command.equalsIgnoreCase("stop")) {
				this.sendNoticeToOps(username, "Stopping the server..");
				this.minecraftServer.initiateShutdown();
				
			} else if(command.equalsIgnoreCase("save-all")) {
				this.sendNoticeToOps(username, "Forcing save..");
				if(serverConfigurationManager != null) {
					serverConfigurationManager.savePlayerStates();
				}

				for(int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
					worldServer = this.minecraftServer.worldMngr[i];
					boolean z11 = worldServer.levelSaving;
					worldServer.levelSaving = false;
					worldServer.saveWorld(true, (IProgressUpdate)null);
					worldServer.levelSaving = z11;
				}

				this.sendNoticeToOps(username, "Save complete.");
				
			} else if(command.equalsIgnoreCase("save-off")) {
				this.sendNoticeToOps(username, "Disabling level saving..");

				for(int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
					worldServer = this.minecraftServer.worldMngr[i];
					worldServer.levelSaving = true;
				}
				
			} else if(command.equalsIgnoreCase("save-on")) {
				this.sendNoticeToOps(username, "Enabling level saving..");

				for(int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
					worldServer = this.minecraftServer.worldMngr[i];
					worldServer.levelSaving = false;
				}
				
			} else if(command.equalsIgnoreCase("op")) {
				serverConfigurationManager.addOp(parameters);
				this.sendNoticeToOps(username, "Opping " + parameters);
				serverConfigurationManager.sendChatMessageToPlayer(parameters, "\u00a7eYou are now op!");
				
			} else if(command.equalsIgnoreCase("deop")) {
				serverConfigurationManager.removeOp(parameters);
				serverConfigurationManager.sendChatMessageToPlayer(parameters, "\u00a7eYou are no longer op!");
				this.sendNoticeToOps(username, "De-opping " + parameters);
				
			} else if(command.equalsIgnoreCase("ban-ip")) {
				serverConfigurationManager.banIP(parameters);
				this.sendNoticeToOps(username, "Banning ip " + parameters);
				
			} else if(command.equalsIgnoreCase("pardon-ip")) {
				serverConfigurationManager.pardonIP(parameters);
				this.sendNoticeToOps(username, "Pardoning ip " + parameters);
				
			} else if(command.equalsIgnoreCase("ban")) {
				serverConfigurationManager.banPlayer(parameters);
				this.sendNoticeToOps(username, "Banning " + parameters);
				thePlayerMP = serverConfigurationManager.getPlayerEntity(parameters);
				if(thePlayerMP != null) {
					thePlayerMP.playerNetServerHandler.kickPlayer("Banned by admin");
				}
				
			} else if(command.equalsIgnoreCase("pardon")) {
				serverConfigurationManager.pardonPlayer(parameters);
				this.sendNoticeToOps(username, "Pardoning " + parameters);
				
			} else if(command.equalsIgnoreCase("kick")) {
				sParam = parameters;
				thePlayerMP = null;

				for(int i = 0; i < serverConfigurationManager.playerEntities.size(); ++i) {
					EntityPlayerMP entityPlayerMP12 = (EntityPlayerMP)serverConfigurationManager.playerEntities.get(i);
					if(entityPlayerMP12.username.equalsIgnoreCase(sParam)) {
						thePlayerMP = entityPlayerMP12;
					}
				}

				if(thePlayerMP != null) {
					thePlayerMP.playerNetServerHandler.kickPlayer("Kicked by admin");
					this.sendNoticeToOps(username, "Kicking " + thePlayerMP.username);
				} else {
					this.commandListener.log("Can\'t find user " + sParam + ". No kick.");
				}
			} else if(command.equalsIgnoreCase("say") && parameters.length() > 0) {
				minecraftLogger.info("[" + username + "] " + parameters);
				serverConfigurationManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7d[Server] " + parameters));
				
			} else if(command.equalsIgnoreCase("tell")) {
				if(tokens.length >= 3) {
					commandLine = commandLine.substring(commandLine.indexOf(" ")).trim();
					commandLine = commandLine.substring(commandLine.indexOf(" ")).trim();
					minecraftLogger.info("[" + username + "->" + tokens[1] + "] " + commandLine);
					commandLine = "\u00a77" + username + " whispers " + commandLine;
					minecraftLogger.info(commandLine);
					if(!serverConfigurationManager.sendPacketToPlayer(tokens[1], new Packet3Chat(commandLine))) {
						this.commandListener.log("There\'s no player by that name online.");
					}
				}
				
			} else if(command.equalsIgnoreCase("whitelist")) {
				this.handleWhitelist(username, commandLine, this.commandListener);
				
			} else if(command.equalsIgnoreCase("banlist")) {
				if(tokens.length == 2) {
					if(tokens[1].equals("ips")) {
						this.commandListener.log("IP Ban list:" + this.s_func_40648_a(this.minecraftServer.getBannedIPsList(), ", "));
					}
				} else {
					this.commandListener.log("Ban list:" + this.s_func_40648_a(this.minecraftServer.getBannedPlayersList(), ", "));
				}
				
			} /*else if(command.equalsIgnoreCase("tp")) {
				if(tokens.length == 3) {
					EntityPlayerMP entityPlayerMP21 = serverConfigurationManager.getPlayerEntity(tokens[1]);
					thePlayerMP = serverConfigurationManager.getPlayerEntity(tokens[2]);
					if(entityPlayerMP21 == null) {
						this.commandListener.log("Can\'t find user " + tokens[1] + ". No tp.");
					} else if(thePlayerMP == null) {
						this.commandListener.log("Can\'t find user " + tokens[2] + ". No tp.");
					} else if(entityPlayerMP21.dimension != thePlayerMP.dimension) {
						this.commandListener.log("User " + tokens[1] + " and " + tokens[2] + " are in different dimensions. No tp.");
					} else {
						entityPlayerMP21.playerNetServerHandler.teleportTo(thePlayerMP.posX, thePlayerMP.posY, thePlayerMP.posZ, thePlayerMP.rotationYaw, thePlayerMP.rotationPitch);
						this.sendNoticeToOps(username, "Teleporting " + tokens[1] + " to " + tokens[2] + ".");
					}
				} else {
					this.commandListener.log("Syntax error, please provide a source and a target.");
				}
			} else if(command.equalsIgnoreCase("give")) {
				if(tokens.length != 3 && tokens.length != 4 && tokens.length != 5) {
					return;
				}

				sParam = tokens[1];
				thePlayerMP = serverConfigurationManager.getPlayerEntity(sParam);
				if(thePlayerMP != null) {
					try {
						i20 = Integer.parseInt(tokens[2]);
						if(Item.itemsList[i20] != null) {
							this.sendNoticeToOps(username, "Giving " + thePlayerMP.username + " some " + i20);
							int i22 = 1;
							int i13 = 0;
							if(tokens.length > 3) {
								i22 = this.tryParse(tokens[3], 1);
							}

							if(tokens.length > 4) {
								i13 = this.tryParse(tokens[4], 1);
							}

							if(i22 < 1) {
								i22 = 1;
							}

							if(i22 > 64) {
								i22 = 64;
							}

							thePlayerMP.dropPlayerItem(new ItemStack(i20, i22, i13));
						} else {
							this.commandListener.log("There\'s no item with id " + i20);
						}
					} catch (NumberFormatException numberFormatException16) {
						this.commandListener.log("There\'s no item with id " + tokens[2]);
					}
				} else {
					this.commandListener.log("Can\'t find user " + sParam);
				}
			} else if(command.equalsIgnoreCase("gamemode")) {
				String who = username, gameMode = "0";
				if(tokens.length == 2) {
					gameMode = tokens[1];
				} else if(tokens.length == 3) {
					who = tokens[1];
					gameMode = tokens[2];
				} else {
					return;
				}

				thePlayerMP = serverConfigurationManager.getPlayerEntity(who);
				if(thePlayerMP != null) {
					try {
						i20 = Integer.parseInt(gameMode);
						i20 = WorldSettings.validGameType(i20);
						if(thePlayerMP.itemInWorldManager.getGameType() != i20) {
							this.sendNoticeToOps(username, "Setting " + thePlayerMP.username + " to game mode " + i20);
							thePlayerMP.itemInWorldManager.toggleGameType(i20);
							thePlayerMP.playerNetServerHandler.sendPacket(new Packet70GameEvent(3, i20));
						} else {
							this.sendNoticeToOps(username, thePlayerMP.username + " already has game mode " + i20);
						}
					} catch (NumberFormatException numberFormatException14) {
						this.commandListener.log("There\'s no game mode with id " + gameMode);
					}
				} else {
					this.commandListener.log("Can\'t find user " + who);
				}
			} else if(command.equalsIgnoreCase("time")) {
				if(tokens.length != 3) {
					return;
				}

				sParam = tokens[1];

				try {
					int armorValue = Integer.parseInt(tokens[2]);
					WorldServer worldServer24;
					if("add".equalsIgnoreCase(sParam)) {
						for(i20 = 0; i20 < this.minecraftServer.worldMngr.length; ++i20) {
							worldServer24 = this.minecraftServer.worldMngr[i20];
							worldServer24.advanceTime(worldServer24.getWorldTime() + (long)armorValue);
						}

						this.sendNoticeToOps(username, "Added " + armorValue + " to time");
					} else if("set".equalsIgnoreCase(sParam)) {
						for(i20 = 0; i20 < this.minecraftServer.worldMngr.length; ++i20) {
							worldServer24 = this.minecraftServer.worldMngr[i20];
							worldServer24.advanceTime((long)armorValue);
						}

						this.sendNoticeToOps(username, "Set time to " + armorValue);
					} else {
						this.commandListener.log("Unknown method, use either \"add\" or \"set\"");
					}
				} catch (NumberFormatException numberFormatException17) {
					this.commandListener.log("Unable to convert time value, " + tokens[2]);
				}
			} else if (command.toLowerCase().startsWith("summon")) {
				worldServer = this.minecraftServer.worldMngr[0];
				boolean spawned = false;
				EntityLiving entity = (EntityLiving) EntityList.createEntityByName(parameters, worldServer);
				System.out.println (">" + entity);
				if (entity != null) {
					EntityPlayerMP playerEntity = serverConfigurationManager.getPlayerEntity(username);
					int x = (int)playerEntity.posX + worldServer.rand.nextInt(8) - 4;
					int y = (int)playerEntity.posY + worldServer.rand.nextInt(4) + 1;
					int z = (int)playerEntity.posZ + worldServer.rand.nextInt(8) - 4;
					this.commandListener.log ("Attempting to spawn @ " + x + " " + y + " " + z);
					entity.setLocationAndAngles((double)x, (double)y, (double)z, worldServer.rand.nextFloat() * 360.0F, 0.0F);

					// If entity supports levels, set level with metadata
					if(entity instanceof IMobWithLevel) {
						((IMobWithLevel)entity).setLevel(worldServer.rand.nextInt(4));
					}
					
					this.commandListener.log("Spawned " + parameters + " @ " + x + " " + y + " " + z);
					worldServer.spawnEntityInWorld(entity);
					spawned = true;
				}
				
				if (!spawned) {
					this.commandListener.log("Could not spawn " + parameters + ".");
				} 
			} else if(command.equalsIgnoreCase("toggledownfall")) {
				this.minecraftServer.worldMngr[0].commandToggleDownfall();
				this.commandListener.log("Toggling rain and snow, hold on...");
				
			} */else {
				thePlayerMP = serverConfigurationManager.getPlayerEntity(username);
				worldServer = this.minecraftServer.worldMngr[thePlayerMP.dimension];
				
				CommandProcessor.withCommandSender(this);
				CommandProcessor.withServerConfigManager(serverConfigurationManager);
				int res = CommandProcessor.executeCommand("/" + commandLine, worldServer, null, thePlayerMP, thePlayerMP.getPlayerCoordinates());
				
				if(res == CommandBase.NOT_FOUND) this.commandListener.log("Unknown console command. Type \"help\" for help.");
			}
		} else {
			this.printHelp();
		}

	}

	private void handleWhitelist(String string1, String string2, ICommandListener iCommandListener3) {
		String[] string4 = string2.split(" ");
		if(string4.length >= 2) {
			String string5 = string4[1].toLowerCase();
			if("on".equals(string5)) {
				this.sendNoticeToOps(string1, "Turned on white-listing");
				this.minecraftServer.propertyManagerObj.setProperty("white-list", true);
			} else if("off".equals(string5)) {
				this.sendNoticeToOps(string1, "Turned off white-listing");
				this.minecraftServer.propertyManagerObj.setProperty("white-list", false);
			} else if("list".equals(string5)) {
				Set<String> set6 = this.minecraftServer.configManager.getWhiteListedIPs();
				String string7 = "";

				String string9;
				for(Iterator<String> iterator8 = set6.iterator(); iterator8.hasNext(); string7 = string7 + string9 + " ") {
					string9 = (String)iterator8.next();
				}

				iCommandListener3.log("White-listed players: " + string7);
			} else {
				String string10;
				if("add".equals(string5) && string4.length == 3) {
					string10 = string4[2].toLowerCase();
					this.minecraftServer.configManager.addToWhiteList(string10);
					this.sendNoticeToOps(string1, "Added " + string10 + " to white-list");
				} else if("remove".equals(string5) && string4.length == 3) {
					string10 = string4[2].toLowerCase();
					this.minecraftServer.configManager.removeFromWhiteList(string10);
					this.sendNoticeToOps(string1, "Removed " + string10 + " from white-list");
				} else if("reload".equals(string5)) {
					this.minecraftServer.configManager.reloadWhiteList();
					this.sendNoticeToOps(string1, "Reloaded white-list from file");
				}
			}

		}
	}

	private void printHelp() {
		this.commandListener.log("Server commands:");
		this.commandListener.log("   help  or  ?               shows this message");
		this.commandListener.log("   kick <player>             removes a player from the server");
		this.commandListener.log("   ban <player>              bans a player from the server");
		this.commandListener.log("   pardon <player>           pardons a banned player so that they can connect again");
		this.commandListener.log("   ban-ip <ip>               bans an IP address from the server");
		this.commandListener.log("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
		this.commandListener.log("   op <player>               turns a player into an op");
		this.commandListener.log("   deop <player>             removes op status from a player");
		this.commandListener.log("   tell <player> <message>   sends a private message to a player");
		this.commandListener.log("   stop                      gracefully stops the server");
		this.commandListener.log("   save-all                  forces a server-wide level save");
		this.commandListener.log("   save-off                  disables terrain saving (useful for backup scripts)");
		this.commandListener.log("   save-on                   re-enables terrain saving");
		this.commandListener.log("   list                      lists all currently connected players");
		this.commandListener.log("   say <message>             broadcasts a message to all players");
		this.commandListener.log("Extra commands:");
		this.commandListener.log("   " + String.join(", ",  CommandProcessor.commandsMap.keySet()));
	}

	private void sendNoticeToOps(String string1, String string2) {
		String string3 = string1 + ": " + string2;
		this.minecraftServer.configManager.sendChatMessageToAllOps("\u00a77(" + string3 + ")");
		minecraftLogger.info(string3);
	}

	public int tryParse(String string1, int i2) {
		try {
			return Integer.parseInt(string1);
		} catch (NumberFormatException numberFormatException4) {
			return i2;
		}
	}

	private String s_func_40648_a(String[] string1, String string2) {
		int i3 = string1.length;
		if(0 == i3) {
			return "";
		} else {
			StringBuilder stringBuilder4 = new StringBuilder();
			stringBuilder4.append(string1[0]);

			for(int i5 = 1; i5 < i3; ++i5) {
				stringBuilder4.append(string2).append(string1[i5]);
			}

			return stringBuilder4.toString();
		}
	}

	@Override
	public void printMessage(World world, String message) {
		this.commandListener.log(message);
	}

	@Override
	public BlockPos getMouseOverCoordinates() {
		return this.latestCommand != null ? this.latestCommand.mousePos : null;
	}
}
