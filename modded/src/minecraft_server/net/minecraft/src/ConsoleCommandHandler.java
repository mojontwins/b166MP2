package net.minecraft.src;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

public class ConsoleCommandHandler {
	private static Logger minecraftLogger = Logger.getLogger("Minecraft");
	private MinecraftServer minecraftServer;

	public ConsoleCommandHandler(MinecraftServer minecraftServer1) {
		this.minecraftServer = minecraftServer1;
	}

	public void handleCommand(ServerCommand serverCommand1) {
		String string2 = serverCommand1.command;
		ICommandListener iCommandListener3 = serverCommand1.commandListener;
		String string4 = iCommandListener3.getUsername();
		ServerConfigurationManager serverConfigurationManager5 = this.minecraftServer.configManager;
		if(!string2.toLowerCase().startsWith("help") && !string2.toLowerCase().startsWith("?")) {
			if(string2.toLowerCase().startsWith("list")) {
				iCommandListener3.log("Connected players: " + serverConfigurationManager5.getPlayerList());
			} else if(string2.toLowerCase().startsWith("stop")) {
				this.sendNoticeToOps(string4, "Stopping the server..");
				this.minecraftServer.initiateShutdown();
			} else {
				int i6;
				WorldServer worldServer7;
				if(string2.toLowerCase().startsWith("save-all")) {
					this.sendNoticeToOps(string4, "Forcing save..");
					if(serverConfigurationManager5 != null) {
						serverConfigurationManager5.savePlayerStates();
					}

					for(i6 = 0; i6 < this.minecraftServer.worldMngr.length; ++i6) {
						worldServer7 = this.minecraftServer.worldMngr[i6];
						worldServer7.saveWorld(true, (IProgressUpdate)null);
					}

					this.sendNoticeToOps(string4, "Save complete.");
				} else if(string2.toLowerCase().startsWith("save-off")) {
					this.sendNoticeToOps(string4, "Disabling level saving..");

					for(i6 = 0; i6 < this.minecraftServer.worldMngr.length; ++i6) {
						worldServer7 = this.minecraftServer.worldMngr[i6];
						worldServer7.levelSaving = true;
					}
				} else if(string2.toLowerCase().startsWith("save-on")) {
					this.sendNoticeToOps(string4, "Enabling level saving..");

					for(i6 = 0; i6 < this.minecraftServer.worldMngr.length; ++i6) {
						worldServer7 = this.minecraftServer.worldMngr[i6];
						worldServer7.levelSaving = false;
					}
				} else {
					String string13;
					if(string2.toLowerCase().startsWith("op ")) {
						string13 = string2.substring(string2.indexOf(" ")).trim();
						serverConfigurationManager5.opPlayer(string13);
						this.sendNoticeToOps(string4, "Opping " + string13);
						serverConfigurationManager5.sendChatMessageToPlayer(string13, "\u00a7eYou are now op!");
					} else if(string2.toLowerCase().startsWith("deop ")) {
						string13 = string2.substring(string2.indexOf(" ")).trim();
						serverConfigurationManager5.deopPlayer(string13);
						serverConfigurationManager5.sendChatMessageToPlayer(string13, "\u00a7eYou are no longer op!");
						this.sendNoticeToOps(string4, "De-opping " + string13);
					} else if(string2.toLowerCase().startsWith("ban-ip ")) {
						string13 = string2.substring(string2.indexOf(" ")).trim();
						serverConfigurationManager5.banIP(string13);
						this.sendNoticeToOps(string4, "Banning ip " + string13);
					} else if(string2.toLowerCase().startsWith("pardon-ip ")) {
						string13 = string2.substring(string2.indexOf(" ")).trim();
						serverConfigurationManager5.pardonIP(string13);
						this.sendNoticeToOps(string4, "Pardoning ip " + string13);
					} else {
						EntityPlayerMP entityPlayerMP14;
						if(string2.toLowerCase().startsWith("ban ")) {
							string13 = string2.substring(string2.indexOf(" ")).trim();
							serverConfigurationManager5.banPlayer(string13);
							this.sendNoticeToOps(string4, "Banning " + string13);
							entityPlayerMP14 = serverConfigurationManager5.getPlayerEntity(string13);
							if(entityPlayerMP14 != null) {
								entityPlayerMP14.playerNetServerHandler.kickPlayer("Banned by admin");
							}
						} else if(string2.toLowerCase().startsWith("pardon ")) {
							string13 = string2.substring(string2.indexOf(" ")).trim();
							serverConfigurationManager5.pardonPlayer(string13);
							this.sendNoticeToOps(string4, "Pardoning " + string13);
						} else {
							int i8;
							if(string2.toLowerCase().startsWith("kick ")) {
								string13 = string2.substring(string2.indexOf(" ")).trim();
								entityPlayerMP14 = null;

								for(i8 = 0; i8 < serverConfigurationManager5.playerEntities.size(); ++i8) {
									EntityPlayerMP entityPlayerMP9 = (EntityPlayerMP)serverConfigurationManager5.playerEntities.get(i8);
									if(entityPlayerMP9.username.equalsIgnoreCase(string13)) {
										entityPlayerMP14 = entityPlayerMP9;
									}
								}

								if(entityPlayerMP14 != null) {
									entityPlayerMP14.playerNetServerHandler.kickPlayer("Kicked by admin");
									this.sendNoticeToOps(string4, "Kicking " + entityPlayerMP14.username);
								} else {
									iCommandListener3.log("Can\'t find user " + string13 + ". No kick.");
								}
							} else {
								EntityPlayerMP entityPlayerMP15;
								String[] string18;
								if(string2.toLowerCase().startsWith("tp ")) {
									string18 = string2.split(" ");
									if(string18.length == 3) {
										entityPlayerMP14 = serverConfigurationManager5.getPlayerEntity(string18[1]);
										entityPlayerMP15 = serverConfigurationManager5.getPlayerEntity(string18[2]);
										if(entityPlayerMP14 == null) {
											iCommandListener3.log("Can\'t find user " + string18[1] + ". No tp.");
										} else if(entityPlayerMP15 == null) {
											iCommandListener3.log("Can\'t find user " + string18[2] + ". No tp.");
										} else if(entityPlayerMP14.dimension != entityPlayerMP15.dimension) {
											iCommandListener3.log("User " + string18[1] + " and " + string18[2] + " are in different dimensions. No tp.");
										} else {
											entityPlayerMP14.playerNetServerHandler.teleportTo(entityPlayerMP15.posX, entityPlayerMP15.posY, entityPlayerMP15.posZ, entityPlayerMP15.rotationYaw, entityPlayerMP15.rotationPitch);
											this.sendNoticeToOps(string4, "Teleporting " + string18[1] + " to " + string18[2] + ".");
										}
									} else {
										iCommandListener3.log("Syntax error, please provice a source and a target.");
									}
								} else {
									String string16;
									int i17;
									if(string2.toLowerCase().startsWith("give ")) {
										string18 = string2.split(" ");
										if(string18.length != 3 && string18.length != 4) {
											return;
										}

										string16 = string18[1];
										entityPlayerMP15 = serverConfigurationManager5.getPlayerEntity(string16);
										if(entityPlayerMP15 != null) {
											try {
												i17 = Integer.parseInt(string18[2]);
												if(Item.itemsList[i17] != null) {
													this.sendNoticeToOps(string4, "Giving " + entityPlayerMP15.username + " some " + i17);
													int i10 = 1;
													if(string18.length > 3) {
														i10 = this.tryParse(string18[3], 1);
													}

													if(i10 < 1) {
														i10 = 1;
													}

													if(i10 > 64) {
														i10 = 64;
													}

													entityPlayerMP15.dropPlayerItem(new ItemStack(i17, i10, 0));
												} else {
													iCommandListener3.log("There\'s no item with id " + i17);
												}
											} catch (NumberFormatException numberFormatException11) {
												iCommandListener3.log("There\'s no item with id " + string18[2]);
											}
										} else {
											iCommandListener3.log("Can\'t find user " + string16);
										}
									} else if(string2.toLowerCase().startsWith("time ")) {
										string18 = string2.split(" ");
										if(string18.length != 3) {
											return;
										}

										string16 = string18[1];

										try {
											i8 = Integer.parseInt(string18[2]);
											WorldServer worldServer19;
											if("add".equalsIgnoreCase(string16)) {
												for(i17 = 0; i17 < this.minecraftServer.worldMngr.length; ++i17) {
													worldServer19 = this.minecraftServer.worldMngr[i17];
													worldServer19.setWorldTime(worldServer19.getWorldTime() + (long)i8);
												}

												this.sendNoticeToOps(string4, "Added " + i8 + " to time");
											} else if("set".equalsIgnoreCase(string16)) {
												for(i17 = 0; i17 < this.minecraftServer.worldMngr.length; ++i17) {
													worldServer19 = this.minecraftServer.worldMngr[i17];
													worldServer19.setWorldTime((long)i8);
												}

												this.sendNoticeToOps(string4, "Set time to " + i8);
											} else {
												iCommandListener3.log("Unknown method, use either \"add\" or \"set\"");
											}
										} catch (NumberFormatException numberFormatException12) {
											iCommandListener3.log("Unable to convert time value, " + string18[2]);
										}
									} else if(string2.toLowerCase().startsWith("say ")) {
										string2 = string2.substring(string2.indexOf(" ")).trim();
										minecraftLogger.info("[" + string4 + "] " + string2);
										serverConfigurationManager5.sendPacketToAllPlayers(new Packet3Chat("\u00a7d[Server] " + string2));
									} else if(string2.toLowerCase().startsWith("tell ")) {
										string18 = string2.split(" ");
										if(string18.length >= 3) {
											string2 = string2.substring(string2.indexOf(" ")).trim();
											string2 = string2.substring(string2.indexOf(" ")).trim();
											minecraftLogger.info("[" + string4 + "->" + string18[1] + "] " + string2);
											string2 = "\u00a77" + string4 + " whispers " + string2;
											minecraftLogger.info(string2);
											if(!serverConfigurationManager5.sendPacketToPlayer(string18[1], new Packet3Chat(string2))) {
												iCommandListener3.log("There\'s no player by that name online.");
											}
										}
									} else if(string2.toLowerCase().startsWith("whitelist ")) {
										this.handleWhitelist(string4, string2, iCommandListener3);
									} else {
										minecraftLogger.info("Unknown console command. Type \"help\" for help.");
									}
								}
							}
						}
					}
				}
			}
		} else {
			this.printHelp(iCommandListener3);
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
				Set set6 = this.minecraftServer.configManager.getWhiteListedIPs();
				String string7 = "";

				String string9;
				for(Iterator iterator8 = set6.iterator(); iterator8.hasNext(); string7 = string7 + string9 + " ") {
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

	private void printHelp(ICommandListener iCommandListener1) {
		iCommandListener1.log("To run the server without a gui, start it like this:");
		iCommandListener1.log("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
		iCommandListener1.log("Console commands:");
		iCommandListener1.log("   help  or  ?               shows this message");
		iCommandListener1.log("   kick <player>             removes a player from the server");
		iCommandListener1.log("   ban <player>              bans a player from the server");
		iCommandListener1.log("   pardon <player>           pardons a banned player so that they can connect again");
		iCommandListener1.log("   ban-ip <ip>               bans an IP address from the server");
		iCommandListener1.log("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
		iCommandListener1.log("   op <player>               turns a player into an op");
		iCommandListener1.log("   deop <player>             removes op status from a player");
		iCommandListener1.log("   tp <player1> <player2>    moves one player to the same location as another player");
		iCommandListener1.log("   give <player> <id> [num]  gives a player a resource");
		iCommandListener1.log("   tell <player> <message>   sends a private message to a player");
		iCommandListener1.log("   stop                      gracefully stops the server");
		iCommandListener1.log("   save-all                  forces a server-wide level save");
		iCommandListener1.log("   save-off                  disables terrain saving (useful for backup scripts)");
		iCommandListener1.log("   save-on                   re-enables terrain saving");
		iCommandListener1.log("   list                      lists all currently connected players");
		iCommandListener1.log("   say <message>             broadcasts a message to all players");
		iCommandListener1.log("   time <add|set> <amount>   adds to or sets the world time (0-24000)");
	}

	private void sendNoticeToOps(String string1, String string2) {
		String string3 = string1 + ": " + string2;
		this.minecraftServer.configManager.sendChatMessageToAllOps("\u00a77(" + string3 + ")");
		minecraftLogger.info(string3);
	}

	private int tryParse(String string1, int i2) {
		try {
			return Integer.parseInt(string1);
		} catch (NumberFormatException numberFormatException4) {
			return i2;
		}
	}
}
