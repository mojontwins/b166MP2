package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSlotServer;
import net.minecraft.client.gui.ServerNBTStorage;

public class ThreadPollServers extends Thread {
	final ServerNBTStorage server;
	final GuiSlotServer serverSlotContainer;

	public ThreadPollServers(GuiSlotServer guiSlotServer1, ServerNBTStorage serverNBTStorage2) {
		this.serverSlotContainer = guiSlotServer1;
		this.server = serverNBTStorage2;
	}

	public void run() {
		boolean z27 = false;

		label183: {
			label184: {
				label185: {
					label186: {
						label187: {
							try {
								z27 = true;
								this.server.motd = "\u00a78Polling..";
								long j1 = System.nanoTime();
								GuiMultiplayer.pollServer(this.serverSlotContainer.parentGui, this.server);
								long j3 = System.nanoTime();
								this.server.lag = (j3 - j1) / 1000000L;
								z27 = false;
								break label183;
							} catch (UnknownHostException unknownHostException35) {
								this.server.lag = -1L;
								this.server.motd = "\u00a74Can\'t resolve hostname";
								z27 = false;
								break label184;
							} catch (SocketTimeoutException socketTimeoutException36) {
								this.server.lag = -1L;
								this.server.motd = "\u00a74Can\'t reach server";
								z27 = false;
							} catch (ConnectException connectException37) {
								this.server.lag = -1L;
								this.server.motd = "\u00a74Can\'t reach server";
								z27 = false;
								break label187;
							} catch (IOException iOException38) {
								this.server.lag = -1L;
								this.server.motd = "\u00a74Communication error";
								z27 = false;
								break label186;
							} catch (Exception exception39) {
								this.server.lag = -1L;
								this.server.motd = "ERROR: " + exception39.getClass();
								z27 = false;
								break label185;
							} finally {
								if(z27) {
									synchronized(GuiMultiplayer.getLock()) {
										GuiMultiplayer.decrementThreadsPending();
									}
								}
							}

							synchronized(GuiMultiplayer.getLock()) {
								GuiMultiplayer.decrementThreadsPending();
								return;
							}
						}

						synchronized(GuiMultiplayer.getLock()) {
							GuiMultiplayer.decrementThreadsPending();
							return;
						}
					}

					synchronized(GuiMultiplayer.getLock()) {
						GuiMultiplayer.decrementThreadsPending();
						return;
					}
				}

				synchronized(GuiMultiplayer.getLock()) {
					GuiMultiplayer.decrementThreadsPending();
					return;
				}
			}

			synchronized(GuiMultiplayer.getLock()) {
				GuiMultiplayer.decrementThreadsPending();
				return;
			}
		}

		synchronized(GuiMultiplayer.getLock()) {
			GuiMultiplayer.decrementThreadsPending();
		}

	}
}
