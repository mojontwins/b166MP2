package net.minecraft.server.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.world.IntHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPainting;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.IAnimals;
import net.minecraft.world.entity.item.EntityBoat;
import net.minecraft.world.entity.item.EntityFallingSand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityMinecart;
import net.minecraft.world.entity.item.EntityMovingPiston;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityEgg;
import net.minecraft.world.entity.projectile.EntityFireball;
import net.minecraft.world.entity.projectile.EntityFishHook;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import net.minecraft.world.entity.projectile.EntitySnowball;

public class EntityTracker {
	private Set<EntityTrackerEntry> trackedEntitySet = new HashSet<EntityTrackerEntry>();
	private IntHashMap trackedEntityHashTable = new IntHashMap();
	private MinecraftServer mcServer;
	private int maxTrackingDistanceThreshold;
	private int s_field_28113_e;

	public EntityTracker(MinecraftServer minecraftServer1, int i2) {
		this.mcServer = minecraftServer1;
		this.s_field_28113_e = i2;
		this.maxTrackingDistanceThreshold = minecraftServer1.configManager.getMaxTrackingDistance();
	}

	public void trackEntity(Entity entity1) {
		if(entity1 instanceof EntityPlayerMP) {
			this.trackEntity(entity1, 512, 2);
			EntityPlayerMP entityPlayerMP2 = (EntityPlayerMP)entity1;
			Iterator<EntityTrackerEntry> iterator3 = this.trackedEntitySet.iterator();

			while(iterator3.hasNext()) {
				EntityTrackerEntry entityTrackerEntry4 = (EntityTrackerEntry)iterator3.next();
				if(entityTrackerEntry4.trackedEntity != entityPlayerMP2) {
					entityTrackerEntry4.updatePlayerEntity(entityPlayerMP2);
				}
			}
		} else if(entity1 instanceof EntityFishHook) {
			this.trackEntity(entity1, 64, 5, true);
		} else if(entity1 instanceof EntityArrow) {
			this.trackEntity(entity1, 64, 20, false);
		} else if(entity1 instanceof EntitySmallFireball) {
			this.trackEntity(entity1, 64, 10, false);
		} else if(entity1 instanceof EntityFireball) {
			this.trackEntity(entity1, 64, 10, false);
		} else if(entity1 instanceof EntitySnowball) {
			this.trackEntity(entity1, 64, 10, true);
		} else if(entity1 instanceof EntityEgg) {
			this.trackEntity(entity1, 64, 10, true);
		} else if(entity1 instanceof EntityItem) {
			this.trackEntity(entity1, 64, 20, true);
		} else if(entity1 instanceof EntityMinecart) {
			this.trackEntity(entity1, 80, 3, true);
		} else if(entity1 instanceof EntityBoat) {
			this.trackEntity(entity1, 80, 3, true);
		} else if(entity1 instanceof EntitySquid) {
			this.trackEntity(entity1, 64, 3, true);
		} else if(entity1 instanceof IAnimals) {
			this.trackEntity(entity1, 80, 3, true);
		} else if(entity1 instanceof EntityTNTPrimed) {
			this.trackEntity(entity1, 160, 10, true);
		} else if(entity1 instanceof EntityFallingSand) {
			this.trackEntity(entity1, 160, 20, true);
		} else if(entity1 instanceof EntityPainting) {
			this.trackEntity(entity1, 160, Integer.MAX_VALUE, false);
		} else if(entity1 instanceof EntityMovingPiston) {
			this.trackEntity(entity1, 160, 20, true);
		} 
	}

	public void trackEntity(Entity entity1, int i2, int i3) {
		this.trackEntity(entity1, i2, i3, false);
	}

	public void trackEntity(Entity entity1, int i2, int i3, boolean z4) {
		if(i2 > this.maxTrackingDistanceThreshold) {
			i2 = this.maxTrackingDistanceThreshold;
		}

		if(this.trackedEntityHashTable.containsItem(entity1.entityId)) {
			throw new IllegalStateException("Entity is already tracked!");
		} else {
			EntityTrackerEntry entityTrackerEntry5 = new EntityTrackerEntry(entity1, i2, i3, z4);
			this.trackedEntitySet.add(entityTrackerEntry5);
			this.trackedEntityHashTable.addKey(entity1.entityId, entityTrackerEntry5);
			entityTrackerEntry5.updatePlayerEntities(this.mcServer.getWorldManager(this.s_field_28113_e).playerEntities);
		}
	}

	public void untrackEntity(Entity entity1) {
		if(entity1 instanceof EntityPlayerMP) {
			EntityPlayerMP entityPlayerMP2 = (EntityPlayerMP)entity1;
			Iterator<EntityTrackerEntry> iterator3 = this.trackedEntitySet.iterator();

			while(iterator3.hasNext()) {
				EntityTrackerEntry entityTrackerEntry4 = (EntityTrackerEntry)iterator3.next();
				entityTrackerEntry4.removeFromTrackedPlayers(entityPlayerMP2);
			}
		}

		EntityTrackerEntry entityTrackerEntry5 = (EntityTrackerEntry)this.trackedEntityHashTable.removeObject(entity1.entityId);
		if(entityTrackerEntry5 != null) {
			this.trackedEntitySet.remove(entityTrackerEntry5);
			entityTrackerEntry5.sendDestroyEntityPacketToTrackedPlayers();
		}

	}

	public void updateTrackedEntities() {
		ArrayList<EntityPlayerMP> arrayList1 = new ArrayList<EntityPlayerMP>();
		Iterator<EntityTrackerEntry> iterator2 = this.trackedEntitySet.iterator();

		while(iterator2.hasNext()) {
			EntityTrackerEntry entityTrackerEntry3 = (EntityTrackerEntry)iterator2.next();
			entityTrackerEntry3.updatePlayerList(this.mcServer.getWorldManager(this.s_field_28113_e).playerEntities);
			if(entityTrackerEntry3.playerEntitiesUpdated && entityTrackerEntry3.trackedEntity instanceof EntityPlayerMP) {
				arrayList1.add((EntityPlayerMP)entityTrackerEntry3.trackedEntity);
			}
		}

		for(int i6 = 0; i6 < arrayList1.size(); ++i6) {
			EntityPlayerMP entityPlayerMP7 = (EntityPlayerMP)arrayList1.get(i6);
			Iterator<EntityTrackerEntry> iterator4 = this.trackedEntitySet.iterator();

			while(iterator4.hasNext()) {
				EntityTrackerEntry entityTrackerEntry5 = (EntityTrackerEntry)iterator4.next();
				if(entityTrackerEntry5.trackedEntity != entityPlayerMP7) {
					entityTrackerEntry5.updatePlayerEntity(entityPlayerMP7);
				}
			}
		}

	}

	public void sendPacketToTrackedPlayers(Entity entity1, Packet packet2) {
		EntityTrackerEntry entityTrackerEntry3 = (EntityTrackerEntry)this.trackedEntityHashTable.lookup(entity1.entityId);
		if(entityTrackerEntry3 != null) {
			entityTrackerEntry3.sendPacketToTrackedPlayers(packet2);
		}

	}

	public void sendPacketToTrackedPlayersAndTrackedEntity(Entity entity1, Packet packet2) {
		EntityTrackerEntry entityTrackerEntry3 = (EntityTrackerEntry)this.trackedEntityHashTable.lookup(entity1.entityId);
		if(entityTrackerEntry3 != null) {
			entityTrackerEntry3.sendPacketToTrackedPlayersAndTrackedEntity(packet2);
		}

	}

	public void removeTrackedPlayerSymmetric(EntityPlayerMP entityPlayerMP1) {
		Iterator<EntityTrackerEntry> iterator2 = this.trackedEntitySet.iterator();

		while(iterator2.hasNext()) {
			EntityTrackerEntry entityTrackerEntry3 = (EntityTrackerEntry)iterator2.next();
			entityTrackerEntry3.removeTrackedPlayerSymmetric(entityPlayerMP1);
		}

	}
}
