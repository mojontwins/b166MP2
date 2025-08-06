package net.minecraft.server.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;
import net.minecraft.world.LongHashMap;
import net.minecraft.world.level.dimension.WorldProvider;

public class PlayerManager {
	public List<EntityPlayerMP> players = new ArrayList<EntityPlayerMP>();
	private LongHashMap playerInstances = new LongHashMap();
	private List<PlayerInstance> playerInstancesToUpdate = new ArrayList<PlayerInstance>();
	private MinecraftServer mcServer;
	private int playerDimension;
	private int playerViewRadius;
	private final int[][] xzDirectionsConst = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

	public PlayerManager(MinecraftServer minecraftServer1, int i2, int i3) {
		if(i3 > 15) {
			throw new IllegalArgumentException("Too big view radius!");
		} else if(i3 < 3) {
			throw new IllegalArgumentException("Too small view radius!");
		} else {
			this.playerViewRadius = i3;
			this.mcServer = minecraftServer1;
			this.playerDimension = i2;
		}
	}

	public WorldServer getMinecraftServer() {
		return this.mcServer.getWorldManager(this.playerDimension);
	}

	public void updatePlayerInstances() {
		for(int i1 = 0; i1 < this.playerInstancesToUpdate.size(); ++i1) {
			((PlayerInstance)this.playerInstancesToUpdate.get(i1)).onUpdate();
		}

		this.playerInstancesToUpdate.clear();
		if(this.players.isEmpty()) {
			WorldServer worldServer3 = this.mcServer.getWorldManager(this.playerDimension);
			WorldProvider worldProvider2 = worldServer3.worldProvider;
			if(!worldProvider2.canRespawnHere()) {
				worldServer3.chunkProviderServer.unloadAllChunks();
			}
		}

	}

	private PlayerInstance getPlayerInstance(int i1, int i2, boolean z3) {
		long j4 = (long)i1 + 2147483647L | (long)i2 + 2147483647L << 32;
		PlayerInstance playerInstance6 = (PlayerInstance)this.playerInstances.getValueByKey(j4);
		if(playerInstance6 == null && z3) {
			playerInstance6 = new PlayerInstance(this, i1, i2);
			this.playerInstances.add(j4, playerInstance6);
		}

		return playerInstance6;
	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3) {
		int i4 = i1 >> 4;
		int i5 = i3 >> 4;
		PlayerInstance playerInstance6 = this.getPlayerInstance(i4, i5, false);
		if(playerInstance6 != null) {
			playerInstance6.markBlockNeedsUpdate(i1 & 15, i2, i3 & 15);
		}

	}

	public void addPlayer(EntityPlayerMP entityPlayerMP1) {
		int i2 = (int)entityPlayerMP1.posX >> 4;
		int i3 = (int)entityPlayerMP1.posZ >> 4;
		entityPlayerMP1.managedPosX = entityPlayerMP1.posX;
		entityPlayerMP1.managedPosZ = entityPlayerMP1.posZ;
		int i4 = 0;
		int i5 = this.playerViewRadius;
		int i6 = 0;
		int i7 = 0;
		this.getPlayerInstance(i2, i3, true).addPlayer(entityPlayerMP1);

		int i8;
		for(i8 = 1; i8 <= i5 * 2; ++i8) {
			for(int i9 = 0; i9 < 2; ++i9) {
				int[] i10 = this.xzDirectionsConst[i4++ % 4];

				for(int i11 = 0; i11 < i8; ++i11) {
					i6 += i10[0];
					i7 += i10[1];
					this.getPlayerInstance(i2 + i6, i3 + i7, true).addPlayer(entityPlayerMP1);
				}
			}
		}

		i4 %= 4;

		for(i8 = 0; i8 < i5 * 2; ++i8) {
			i6 += this.xzDirectionsConst[i4][0];
			i7 += this.xzDirectionsConst[i4][1];
			this.getPlayerInstance(i2 + i6, i3 + i7, true).addPlayer(entityPlayerMP1);
		}

		this.players.add(entityPlayerMP1);
	}

	public void removePlayer(EntityPlayerMP entityPlayerMP1) {
		int i2 = (int)entityPlayerMP1.managedPosX >> 4;
		int i3 = (int)entityPlayerMP1.managedPosZ >> 4;

		for(int i4 = i2 - this.playerViewRadius; i4 <= i2 + this.playerViewRadius; ++i4) {
			for(int i5 = i3 - this.playerViewRadius; i5 <= i3 + this.playerViewRadius; ++i5) {
				PlayerInstance playerInstance6 = this.getPlayerInstance(i4, i5, false);
				if(playerInstance6 != null) {
					playerInstance6.removePlayer(entityPlayerMP1);
				}
			}
		}

		this.players.remove(entityPlayerMP1);
	}

	private boolean isOutsidePlayerViewRadius(int i1, int i2, int i3, int i4) {
		int i5 = i1 - i3;
		int i6 = i2 - i4;
		return i5 >= -this.playerViewRadius && i5 <= this.playerViewRadius ? i6 >= -this.playerViewRadius && i6 <= this.playerViewRadius : false;
	}

	public void updateMountedMovingPlayer(EntityPlayerMP entityPlayerMP1) {
		int i2 = (int)entityPlayerMP1.posX >> 4;
		int i3 = (int)entityPlayerMP1.posZ >> 4;
		double d4 = entityPlayerMP1.managedPosX - entityPlayerMP1.posX;
		double d6 = entityPlayerMP1.managedPosZ - entityPlayerMP1.posZ;
		double d8 = d4 * d4 + d6 * d6;
		if(d8 >= 64.0D) {
			int i10 = (int)entityPlayerMP1.managedPosX >> 4;
			int i11 = (int)entityPlayerMP1.managedPosZ >> 4;
			int i12 = i2 - i10;
			int i13 = i3 - i11;
			if(i12 != 0 || i13 != 0) {
				for(int i14 = i2 - this.playerViewRadius; i14 <= i2 + this.playerViewRadius; ++i14) {
					for(int i15 = i3 - this.playerViewRadius; i15 <= i3 + this.playerViewRadius; ++i15) {
						if(!this.isOutsidePlayerViewRadius(i14, i15, i10, i11)) {
							this.getPlayerInstance(i14, i15, true).addPlayer(entityPlayerMP1);
						}

						if(!this.isOutsidePlayerViewRadius(i14 - i12, i15 - i13, i2, i3)) {
							PlayerInstance playerInstance16 = this.getPlayerInstance(i14 - i12, i15 - i13, false);
							if(playerInstance16 != null) {
								playerInstance16.removePlayer(entityPlayerMP1);
							}
						}
					}
				}

				entityPlayerMP1.managedPosX = entityPlayerMP1.posX;
				entityPlayerMP1.managedPosZ = entityPlayerMP1.posZ;
			}
		}
	}

	public int getMaxTrackingDistance() {
		return this.playerViewRadius * 16 - 16;
	}

	static LongHashMap getPlayerInstances(PlayerManager playerManager0) {
		return playerManager0.playerInstances;
	}

	static List<PlayerInstance> getPlayerInstancesToUpdate(PlayerManager playerManager0) {
		return playerManager0.playerInstancesToUpdate;
	}
}
