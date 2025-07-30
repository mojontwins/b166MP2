package net.minecraft.src;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EntityTrackerEntry {
	public Entity trackedEntity;
	public int trackingDistanceThreshold;
	public int field_9234_e;
	public int encodedPosX;
	public int encodedPosY;
	public int encodedPosZ;
	public int encodedRotationYaw;
	public int encodedRotationPitch;
	public double lastTrackedEntityMotionX;
	public double lastTrackedEntityMotionY;
	public double lastTrackedEntityMotionZ;
	public int updateCounter = 0;
	private double lastTrackedEntityPosX;
	private double lastTrackedEntityPosY;
	private double lastTrackedEntityPosZ;
	private boolean firstUpdateDone = false;
	private boolean shouldSendMotionUpdates;
	private int field_28165_t = 0;
	public boolean playerEntitiesUpdated = false;
	public Set trackedPlayers = new HashSet();

	public EntityTrackerEntry(Entity entity1, int i2, int i3, boolean z4) {
		this.trackedEntity = entity1;
		this.trackingDistanceThreshold = i2;
		this.field_9234_e = i3;
		this.shouldSendMotionUpdates = z4;
		this.encodedPosX = MathHelper.floor_double(entity1.posX * 32.0D);
		this.encodedPosY = MathHelper.floor_double(entity1.posY * 32.0D);
		this.encodedPosZ = MathHelper.floor_double(entity1.posZ * 32.0D);
		this.encodedRotationYaw = MathHelper.floor_float(entity1.rotationYaw * 256.0F / 360.0F);
		this.encodedRotationPitch = MathHelper.floor_float(entity1.rotationPitch * 256.0F / 360.0F);
	}

	public boolean equals(Object object1) {
		return object1 instanceof EntityTrackerEntry ? ((EntityTrackerEntry)object1).trackedEntity.entityId == this.trackedEntity.entityId : false;
	}

	public int hashCode() {
		return this.trackedEntity.entityId;
	}

	public void updatePlayerList(List list1) {
		this.playerEntitiesUpdated = false;
		if(!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0D) {
			this.lastTrackedEntityPosX = this.trackedEntity.posX;
			this.lastTrackedEntityPosY = this.trackedEntity.posY;
			this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
			this.firstUpdateDone = true;
			this.playerEntitiesUpdated = true;
			this.updatePlayerEntities(list1);
		}

		++this.field_28165_t;
		if(++this.updateCounter % this.field_9234_e == 0) {
			int i2 = MathHelper.floor_double(this.trackedEntity.posX * 32.0D);
			int i3 = MathHelper.floor_double(this.trackedEntity.posY * 32.0D);
			int i4 = MathHelper.floor_double(this.trackedEntity.posZ * 32.0D);
			int i5 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
			int i6 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
			int i7 = i2 - this.encodedPosX;
			int i8 = i3 - this.encodedPosY;
			int i9 = i4 - this.encodedPosZ;
			Object object10 = null;
			boolean z11 = Math.abs(i2) >= 8 || Math.abs(i3) >= 8 || Math.abs(i4) >= 8;
			boolean z12 = Math.abs(i5 - this.encodedRotationYaw) >= 8 || Math.abs(i6 - this.encodedRotationPitch) >= 8;
			if(i7 >= -128 && i7 < 128 && i8 >= -128 && i8 < 128 && i9 >= -128 && i9 < 128 && this.field_28165_t <= 400) {
				if(z11 && z12) {
					object10 = new Packet33RelEntityMoveLook(this.trackedEntity.entityId, (byte)i7, (byte)i8, (byte)i9, (byte)i5, (byte)i6);
				} else if(z11) {
					object10 = new Packet31RelEntityMove(this.trackedEntity.entityId, (byte)i7, (byte)i8, (byte)i9);
				} else if(z12) {
					object10 = new Packet32EntityLook(this.trackedEntity.entityId, (byte)i5, (byte)i6);
				}
			} else {
				this.field_28165_t = 0;
				this.trackedEntity.posX = (double)i2 / 32.0D;
				this.trackedEntity.posY = (double)i3 / 32.0D;
				this.trackedEntity.posZ = (double)i4 / 32.0D;
				object10 = new Packet34EntityTeleport(this.trackedEntity.entityId, i2, i3, i4, (byte)i5, (byte)i6);
			}

			if(this.shouldSendMotionUpdates) {
				double d13 = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
				double d15 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
				double d17 = this.trackedEntity.motionZ - this.lastTrackedEntityMotionZ;
				double d19 = 0.02D;
				double d21 = d13 * d13 + d15 * d15 + d17 * d17;
				if(d21 > d19 * d19 || d21 > 0.0D && this.trackedEntity.motionX == 0.0D && this.trackedEntity.motionY == 0.0D && this.trackedEntity.motionZ == 0.0D) {
					this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
					this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
					this.lastTrackedEntityMotionZ = this.trackedEntity.motionZ;
					this.sendPacketToTrackedPlayers(new Packet28EntityVelocity(this.trackedEntity.entityId, this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.lastTrackedEntityMotionZ));
				}
			}

			if(object10 != null) {
				this.sendPacketToTrackedPlayers((Packet)object10);
			}

			DataWatcher dataWatcher23 = this.trackedEntity.getDataWatcher();
			if(dataWatcher23.hasObjectChanged()) {
				this.sendPacketToTrackedPlayersAndTrackedEntity(new Packet40EntityMetadata(this.trackedEntity.entityId, dataWatcher23));
			}

			if(z11) {
				this.encodedPosX = i2;
				this.encodedPosY = i3;
				this.encodedPosZ = i4;
			}

			if(z12) {
				this.encodedRotationYaw = i5;
				this.encodedRotationPitch = i6;
			}
		}

		if(this.trackedEntity.beenAttacked) {
			this.sendPacketToTrackedPlayersAndTrackedEntity(new Packet28EntityVelocity(this.trackedEntity));
			this.trackedEntity.beenAttacked = false;
		}

	}

	public void sendPacketToTrackedPlayers(Packet packet1) {
		Iterator iterator2 = this.trackedPlayers.iterator();

		while(iterator2.hasNext()) {
			EntityPlayerMP entityPlayerMP3 = (EntityPlayerMP)iterator2.next();
			entityPlayerMP3.playerNetServerHandler.sendPacket(packet1);
		}

	}

	public void sendPacketToTrackedPlayersAndTrackedEntity(Packet packet1) {
		this.sendPacketToTrackedPlayers(packet1);
		if(this.trackedEntity instanceof EntityPlayerMP) {
			((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packet1);
		}

	}

	public void sendDestroyEntityPacketToTrackedPlayers() {
		this.sendPacketToTrackedPlayers(new Packet29DestroyEntity(this.trackedEntity.entityId));
	}

	public void removeFromTrackedPlayers(EntityPlayerMP entityPlayerMP1) {
		if(this.trackedPlayers.contains(entityPlayerMP1)) {
			this.trackedPlayers.remove(entityPlayerMP1);
		}

	}

	public void updatePlayerEntity(EntityPlayerMP entityPlayerMP1) {
		if(entityPlayerMP1 != this.trackedEntity) {
			double d2 = entityPlayerMP1.posX - (double)(this.encodedPosX / 32);
			double d4 = entityPlayerMP1.posZ - (double)(this.encodedPosZ / 32);
			if(d2 >= (double)(-this.trackingDistanceThreshold) && d2 <= (double)this.trackingDistanceThreshold && d4 >= (double)(-this.trackingDistanceThreshold) && d4 <= (double)this.trackingDistanceThreshold) {
				if(!this.trackedPlayers.contains(entityPlayerMP1)) {
					this.trackedPlayers.add(entityPlayerMP1);
					entityPlayerMP1.playerNetServerHandler.sendPacket(this.getSpawnPacket());
					if(this.shouldSendMotionUpdates) {
						entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet28EntityVelocity(this.trackedEntity.entityId, this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ));
					}

					ItemStack[] itemStack6 = this.trackedEntity.getInventory();
					if(itemStack6 != null) {
						for(int i7 = 0; i7 < itemStack6.length; ++i7) {
							entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet5PlayerInventory(this.trackedEntity.entityId, i7, itemStack6[i7]));
						}
					}

					if(this.trackedEntity instanceof EntityPlayer) {
						EntityPlayer entityPlayer8 = (EntityPlayer)this.trackedEntity;
						if(entityPlayer8.func_30001_K()) {
							entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet17Sleep(this.trackedEntity, 0, MathHelper.floor_double(this.trackedEntity.posX), MathHelper.floor_double(this.trackedEntity.posY), MathHelper.floor_double(this.trackedEntity.posZ)));
						}
					}
				}
			} else if(this.trackedPlayers.contains(entityPlayerMP1)) {
				this.trackedPlayers.remove(entityPlayerMP1);
				entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet29DestroyEntity(this.trackedEntity.entityId));
			}

		}
	}

	public void updatePlayerEntities(List list1) {
		for(int i2 = 0; i2 < list1.size(); ++i2) {
			this.updatePlayerEntity((EntityPlayerMP)list1.get(i2));
		}

	}

	private Packet getSpawnPacket() {
		if(this.trackedEntity instanceof EntityItem) {
			EntityItem entityItem6 = (EntityItem)this.trackedEntity;
			Packet21PickupSpawn packet21PickupSpawn7 = new Packet21PickupSpawn(entityItem6);
			entityItem6.posX = (double)packet21PickupSpawn7.xPosition / 32.0D;
			entityItem6.posY = (double)packet21PickupSpawn7.yPosition / 32.0D;
			entityItem6.posZ = (double)packet21PickupSpawn7.zPosition / 32.0D;
			return packet21PickupSpawn7;
		} else if(this.trackedEntity instanceof EntityPlayerMP) {
			return new Packet20NamedEntitySpawn((EntityPlayer)this.trackedEntity);
		} else {
			if(this.trackedEntity instanceof EntityMinecart) {
				EntityMinecart entityMinecart1 = (EntityMinecart)this.trackedEntity;
				if(entityMinecart1.minecartType == 0) {
					return new Packet23VehicleSpawn(this.trackedEntity, 10);
				}

				if(entityMinecart1.minecartType == 1) {
					return new Packet23VehicleSpawn(this.trackedEntity, 11);
				}

				if(entityMinecart1.minecartType == 2) {
					return new Packet23VehicleSpawn(this.trackedEntity, 12);
				}
			}

			if(this.trackedEntity instanceof EntityBoat) {
				return new Packet23VehicleSpawn(this.trackedEntity, 1);
			} else if(this.trackedEntity instanceof IAnimals) {
				return new Packet24MobSpawn((EntityLiving)this.trackedEntity);
			} else if(this.trackedEntity instanceof EntityFish) {
				return new Packet23VehicleSpawn(this.trackedEntity, 90);
			} else if(this.trackedEntity instanceof EntityArrow) {
				EntityLiving entityLiving5 = ((EntityArrow)this.trackedEntity).owner;
				return new Packet23VehicleSpawn(this.trackedEntity, 60, entityLiving5 != null ? entityLiving5.entityId : this.trackedEntity.entityId);
			} else if(this.trackedEntity instanceof EntitySnowball) {
				return new Packet23VehicleSpawn(this.trackedEntity, 61);
			} else if(this.trackedEntity instanceof EntityFireball) {
				EntityFireball entityFireball4 = (EntityFireball)this.trackedEntity;
				Packet23VehicleSpawn packet23VehicleSpawn2 = new Packet23VehicleSpawn(this.trackedEntity, 63, ((EntityFireball)this.trackedEntity).owner.entityId);
				packet23VehicleSpawn2.field_28044_e = (int)(entityFireball4.field_9199_b * 8000.0D);
				packet23VehicleSpawn2.field_28043_f = (int)(entityFireball4.field_9198_c * 8000.0D);
				packet23VehicleSpawn2.field_28042_g = (int)(entityFireball4.field_9196_d * 8000.0D);
				return packet23VehicleSpawn2;
			} else if(this.trackedEntity instanceof EntityEgg) {
				return new Packet23VehicleSpawn(this.trackedEntity, 62);
			} else if(this.trackedEntity instanceof EntityTNTPrimed) {
				return new Packet23VehicleSpawn(this.trackedEntity, 50);
			} else {
				if(this.trackedEntity instanceof EntityFallingSand) {
					EntityFallingSand entityFallingSand3 = (EntityFallingSand)this.trackedEntity;
					if(entityFallingSand3.blockID == Block.sand.blockID) {
						return new Packet23VehicleSpawn(this.trackedEntity, 70);
					}

					if(entityFallingSand3.blockID == Block.gravel.blockID) {
						return new Packet23VehicleSpawn(this.trackedEntity, 71);
					}
				}

				if(this.trackedEntity instanceof EntityPainting) {
					return new Packet25EntityPainting((EntityPainting)this.trackedEntity);
				} else {
					throw new IllegalArgumentException("Don\'t know how to add " + this.trackedEntity.getClass() + "!");
				}
			}
		}
	}

	public void removeTrackedPlayerSymmetric(EntityPlayerMP entityPlayerMP1) {
		if(this.trackedPlayers.contains(entityPlayerMP1)) {
			this.trackedPlayers.remove(entityPlayerMP1);
			entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet29DestroyEntity(this.trackedEntity.entityId));
		}

	}
}
