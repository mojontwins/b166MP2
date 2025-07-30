package net.minecraft.server.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet50PreChunk;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet52MultiBlockChange;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.server.WorldServer;
import net.minecraft.world.level.chunk.ChunkCoordIntPair;
import net.minecraft.world.level.tile.entity.TileEntity;

class PlayerInstance {
	private List<EntityPlayerMP> players;
	private int chunkX;
	private int chunkZ;
	private ChunkCoordIntPair currentChunk;
	private short[] blocksToUpdate;
	private int numBlocksToUpdate;
	private int updateHash;
	final PlayerManager playerManager;

	public PlayerInstance(PlayerManager playerManager1, int i2, int i3) {
		this.playerManager = playerManager1;
		this.players = new ArrayList<EntityPlayerMP>();
		this.blocksToUpdate = new short[64];
		this.numBlocksToUpdate = 0;
		this.chunkX = i2;
		this.chunkZ = i3;
		this.currentChunk = new ChunkCoordIntPair(i2, i3);
		playerManager1.getMinecraftServer().chunkProviderServer.loadChunk(i2, i3);
	}

	public void addPlayer(EntityPlayerMP entityPlayerMP1) {
		if(this.players.contains(entityPlayerMP1)) {
			throw new IllegalStateException("Failed to add player. " + entityPlayerMP1 + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
		} else {
			entityPlayerMP1.listeningChunks.add(this.currentChunk);
			entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos, true));
			this.players.add(entityPlayerMP1);
			entityPlayerMP1.loadedChunks.add(this.currentChunk);
		}
	}

	public void removePlayer(EntityPlayerMP entityPlayerMP1) {
		if(this.players.contains(entityPlayerMP1)) {
			this.players.remove(entityPlayerMP1);
			if(this.players.size() == 0) {
				long j2 = (long)this.chunkX + 2147483647L | (long)this.chunkZ + 2147483647L << 32;
				PlayerManager.getPlayerInstances(this.playerManager).remove(j2);
				if(this.numBlocksToUpdate > 0) {
					PlayerManager.getPlayerInstancesToUpdate(this.playerManager).remove(this);
				}

				this.playerManager.getMinecraftServer().chunkProviderServer.dropChunk(this.chunkX, this.chunkZ);
			}

			entityPlayerMP1.loadedChunks.remove(this.currentChunk);
			if(entityPlayerMP1.listeningChunks.contains(this.currentChunk)) {
				entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkZ, false));
			}

		}
	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3) {
		if(this.numBlocksToUpdate == 0) {
			PlayerManager.getPlayerInstancesToUpdate(this.playerManager).add(this);
		}

		this.updateHash |= 1 << (i2 >> 4);
		if(this.numBlocksToUpdate < 64) {
			short s4 = (short)(i1 << 12 | i3 << 8 | i2);

			for(int i5 = 0; i5 < this.numBlocksToUpdate; ++i5) {
				if(this.blocksToUpdate[i5] == s4) {
					return;
				}
			}

			this.blocksToUpdate[this.numBlocksToUpdate++] = s4;
		}

	}

	public void sendPacketToPlayersInInstance(Packet packet1) {
		for(int i2 = 0; i2 < this.players.size(); ++i2) {
			EntityPlayerMP entityPlayerMP3 = (EntityPlayerMP)this.players.get(i2);
			if(entityPlayerMP3.listeningChunks.contains(this.currentChunk) && !entityPlayerMP3.loadedChunks.contains(this.currentChunk)) {
				entityPlayerMP3.playerNetServerHandler.sendPacket(packet1);
			}
		}

	}

	public void onUpdate() {
		WorldServer worldServer1 = this.playerManager.getMinecraftServer();
		if(this.numBlocksToUpdate != 0) {
			int i2;
			int i3;
			int i4;
			if(this.numBlocksToUpdate == 1) {
				i2 = this.chunkX * 16 + (this.blocksToUpdate[0] >> 12 & 15);
				i3 = this.blocksToUpdate[0] & 255;
				i4 = this.chunkZ * 16 + (this.blocksToUpdate[0] >> 8 & 15);
				this.sendPacketToPlayersInInstance(new Packet53BlockChange(i2, i3, i4, worldServer1));
				if(worldServer1.hasTileEntity(i2, i3, i4)) {
					this.updateTileEntity(worldServer1.getBlockTileEntity(i2, i3, i4));
				}
			} else {
				int i5;
				if(this.numBlocksToUpdate == 64) {
					i2 = this.chunkX * 16;
					i3 = this.chunkZ * 16;
					this.sendPacketToPlayersInInstance(new Packet51MapChunk(worldServer1.getChunkFromChunkCoords(this.chunkX, this.chunkZ), false, this.updateHash));

					for(i4 = 0; i4 < 16; ++i4) {
						if((this.updateHash & 1 << i4) != 0) {
							i5 = i4 << 4;
							List<?> list6 = worldServer1.getTileEntityList(i2, i5, i3, i2 + 16, i5 + 16, i3 + 16);

							for(int i7 = 0; i7 < list6.size(); ++i7) {
								this.updateTileEntity((TileEntity)list6.get(i7));
							}
						}
					}
				} else {
					this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.chunkX, this.chunkZ, this.blocksToUpdate, this.numBlocksToUpdate, worldServer1));

					for(i2 = 0; i2 < this.numBlocksToUpdate; ++i2) {
						i3 = this.chunkX * 16 + (this.blocksToUpdate[i2] >> 12 & 15);
						i4 = this.blocksToUpdate[i2] & 255;
						i5 = this.chunkZ * 16 + (this.blocksToUpdate[i2] >> 8 & 15);
						if(worldServer1.hasTileEntity(i3, i4, i5)) {
							this.updateTileEntity(worldServer1.getBlockTileEntity(i3, i4, i5));
						}
					}
				}
			}

			this.numBlocksToUpdate = 0;
			this.updateHash = 0;
		}
	}

	private void updateTileEntity(TileEntity tileEntity1) {
		if(tileEntity1 != null) {
			Packet packet2 = tileEntity1.getDescriptionPacket();
			if(packet2 != null) {
				this.sendPacketToPlayersInInstance(packet2);
			}
		}

	}
}
