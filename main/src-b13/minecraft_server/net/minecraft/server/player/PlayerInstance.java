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
	
	// Churrent chunk coordinates
	private int chunkX;
	private int chunkZ;
	private ChunkCoordIntPair currentChunk;
	
	// Array of encoded block coordinates.
	private short[] blocksToUpdate;
	
	// Count of number of blocks needing to be sent over to the client
	private int numBlocksToUpdate;
	
	// updateHash is a bitfield, with 1 bit per chunk subsection which must be updated.
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

	public void markBlockNeedsUpdate(int x, int y, int z) {
		if(this.numBlocksToUpdate == 0) {
			PlayerManager.getPlayerInstancesToUpdate(this.playerManager).add(this);
		}

		// updateHash is a bitfield, with 1 bit per chunk subsection which must be updated.
		this.updateHash |= 1 << (y >> 4);
		if(this.numBlocksToUpdate < 64) {
			short eCoord = (short)(x << 12 | z << 8 | y);

			for(int i = 0; i < this.numBlocksToUpdate; ++i) {
				if(this.blocksToUpdate[i] == eCoord) {
					return;
				}
			}

			this.blocksToUpdate[this.numBlocksToUpdate++] = eCoord;
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
		WorldServer world = this.playerManager.getMinecraftServer();
		if(this.numBlocksToUpdate != 0) {
			int x;
			int y;
			int z;

			if(this.numBlocksToUpdate == 1) {
				// Only one block, use Packet 53.
				
				x = this.chunkX * 16 + (this.blocksToUpdate[0] >> 12 & 15);
				y = this.blocksToUpdate[0] & 255;
				z = this.chunkZ * 16 + (this.blocksToUpdate[0] >> 8 & 15);
				
				this.sendPacketToPlayersInInstance(new Packet53BlockChange(x, y, z, world));
				if(world.hasTileEntity(x, y, z)) {
					this.updateTileEntity(world.getBlockTileEntity(x, y, z));
				}
			} else {
				if(this.numBlocksToUpdate == 64) {
					// 64: Send the whole chunk, Packet 51.

					x = this.chunkX * 16;
					z = this.chunkZ * 16;
					this.sendPacketToPlayersInInstance(new Packet51MapChunk(world.getChunkFromChunkCoords(this.chunkX, this.chunkZ), false, this.updateHash));

					for(int i = 0; i < 16; ++i) {
						if((this.updateHash & 1 << i) != 0) {
							y = i << 4;
							List<?> tel = world.getTileEntityList(x, y, z, x + 16, y + 16, z + 16);

							for(int j = 0; j < tel.size(); ++j) {
								this.updateTileEntity((TileEntity)tel.get(j));
							}
						}
					}
				} else {
					// Less than 64, use an encoded block changes list, Packet 52.

					this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.chunkX, this.chunkZ, this.blocksToUpdate, this.numBlocksToUpdate, world));

					for(int i = 0; i < this.numBlocksToUpdate; ++i) {
						x = this.chunkX * 16 + (this.blocksToUpdate[i] >> 12 & 15);
						y = this.blocksToUpdate[i] & 255;
						z = this.chunkZ * 16 + (this.blocksToUpdate[i] >> 8 & 15);
						if(world.hasTileEntity(x, y, z)) {
							this.updateTileEntity(world.getBlockTileEntity(x, y, z));
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
