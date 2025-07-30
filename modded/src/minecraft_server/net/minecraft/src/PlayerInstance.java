package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

class PlayerInstance {
	private List players;
	private int chunkX;
	private int chunkY;
	private ChunkCoordIntPair currentChunk;
	private short[] blocksToUpdate;
	private int numBlocksToUpdate;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	final PlayerManager playerManager;

	public PlayerInstance(PlayerManager playerManager1, int i2, int i3) {
		this.playerManager = playerManager1;
		this.players = new ArrayList();
		this.blocksToUpdate = new short[10];
		this.numBlocksToUpdate = 0;
		this.chunkX = i2;
		this.chunkY = i3;
		this.currentChunk = new ChunkCoordIntPair(i2, i3);
		playerManager1.getMinecraftServer().chunkProviderServer.loadChunk(i2, i3);
	}

	public void addPlayer(EntityPlayerMP entityPlayerMP1) {
		if(this.players.contains(entityPlayerMP1)) {
			throw new IllegalStateException("Failed to add player. " + entityPlayerMP1 + " already is in chunk " + this.chunkX + ", " + this.chunkY);
		} else {
			entityPlayerMP1.field_420_ah.add(this.currentChunk);
			entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos, true));
			this.players.add(entityPlayerMP1);
			entityPlayerMP1.loadedChunks.add(this.currentChunk);
		}
	}

	public void removePlayer(EntityPlayerMP entityPlayerMP1) {
		if(this.players.contains(entityPlayerMP1)) {
			this.players.remove(entityPlayerMP1);
			if(this.players.size() == 0) {
				long j2 = (long)this.chunkX + 2147483647L | (long)this.chunkY + 2147483647L << 32;
				PlayerManager.getPlayerInstances(this.playerManager).remove(j2);
				if(this.numBlocksToUpdate > 0) {
					PlayerManager.getPlayerInstancesToUpdate(this.playerManager).remove(this);
				}

				this.playerManager.getMinecraftServer().chunkProviderServer.func_374_c(this.chunkX, this.chunkY);
			}

			entityPlayerMP1.loadedChunks.remove(this.currentChunk);
			if(entityPlayerMP1.field_420_ah.contains(this.currentChunk)) {
				entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkY, false));
			}

		}
	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3) {
		if(this.numBlocksToUpdate == 0) {
			PlayerManager.getPlayerInstancesToUpdate(this.playerManager).add(this);
			this.minX = this.maxX = i1;
			this.minY = this.maxY = i2;
			this.minZ = this.maxZ = i3;
		}

		if(this.minX > i1) {
			this.minX = i1;
		}

		if(this.maxX < i1) {
			this.maxX = i1;
		}

		if(this.minY > i2) {
			this.minY = i2;
		}

		if(this.maxY < i2) {
			this.maxY = i2;
		}

		if(this.minZ > i3) {
			this.minZ = i3;
		}

		if(this.maxZ < i3) {
			this.maxZ = i3;
		}

		if(this.numBlocksToUpdate < 10) {
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
			if(entityPlayerMP3.field_420_ah.contains(this.currentChunk)) {
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
				i2 = this.chunkX * 16 + this.minX;
				i3 = this.minY;
				i4 = this.chunkY * 16 + this.minZ;
				this.sendPacketToPlayersInInstance(new Packet53BlockChange(i2, i3, i4, worldServer1));
				if(Block.isBlockContainer[worldServer1.getBlockId(i2, i3, i4)]) {
					this.updateTileEntity(worldServer1.getBlockTileEntity(i2, i3, i4));
				}
			} else {
				int i5;
				if(this.numBlocksToUpdate == 10) {
					this.minY = this.minY / 2 * 2;
					this.maxY = (this.maxY / 2 + 1) * 2;
					i2 = this.minX + this.chunkX * 16;
					i3 = this.minY;
					i4 = this.minZ + this.chunkY * 16;
					i5 = this.maxX - this.minX + 1;
					int i6 = this.maxY - this.minY + 2;
					int i7 = this.maxZ - this.minZ + 1;
					this.sendPacketToPlayersInInstance(new Packet51MapChunk(i2, i3, i4, i5, i6, i7, worldServer1));
					List list8 = worldServer1.getTileEntityList(i2, i3, i4, i2 + i5, i3 + i6, i4 + i7);

					for(int i9 = 0; i9 < list8.size(); ++i9) {
						this.updateTileEntity((TileEntity)list8.get(i9));
					}
				} else {
					this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.chunkX, this.chunkY, this.blocksToUpdate, this.numBlocksToUpdate, worldServer1));

					for(i2 = 0; i2 < this.numBlocksToUpdate; ++i2) {
						i3 = this.chunkX * 16 + (this.numBlocksToUpdate >> 12 & 15);
						i4 = this.numBlocksToUpdate & 255;
						i5 = this.chunkY * 16 + (this.numBlocksToUpdate >> 8 & 15);
						if(Block.isBlockContainer[worldServer1.getBlockId(i3, i4, i5)]) {
							System.out.println("Sending!");
							this.updateTileEntity(worldServer1.getBlockTileEntity(i3, i4, i5));
						}
					}
				}
			}

			this.numBlocksToUpdate = 0;
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
