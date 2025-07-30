package net.minecraft.world.entity.monster;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ISentient;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockDoor;

public class EntityClassicZombie extends EntityArmoredMob {
	
	protected int doorPosX;
	protected int doorPosY;
	protected int doorPosZ;
	
	protected float distanceX;
	protected float distanceZ;
	
	protected BlockDoor targetDoor = null;
	protected boolean chasingDoor = false;
	
	protected int doorBreakTime;
	protected String texturePrefix;
	
	public EntityClassicZombie(World world1) {
		super(world1);
		this.texture = "/mob/zombie.png";
		this.moveSpeed = 0.5F;
		this.attackStrength = 5;
		this.scoreValue = 15;
		this.health = this.getMaxHealth();
	}
	
	protected int getMaxTextureVariations() {
		return 5;
	}
	
	@Override
	public void onLivingUpdate() {
		if(this.worldObj.isDaytime() && !this.worldObj.isRemote) {
			float f1 = this.getBrightness(1.0F);
			if(f1 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (f1 - 0.4F) * 2.0F) {
				this.setFire(8);
			}
		}

		// Door interaction
		if (!this.chasingDoor) {
			if(rand.nextInt(4) == 0) {
				if(this.isCollidedHorizontally) {
					PathEntity pathEntity = this.pathToEntity;
					if(pathEntity != null && !pathEntity.isFinished()) {
						for (int i = 0; i < Math.min(pathEntity.getCurrentPathIndex() + 2, pathEntity.pathLength); i++) {
							PathPoint pathPoint = pathEntity.getPathPointFromIndex(i);
							int dpx = pathPoint.xCoord;
							int dpy = pathPoint.yCoord;
							int dpz = pathPoint.zCoord;
							
							if (this.getDistanceSq(dpx, dpy, dpz) <= 2.25D) {
								this.targetDoor = this.findDoorBlock(dpx, dpy, dpz);
								if (this.targetDoor != null) {
									this.doorPosX = dpx;
									this.doorPosY = dpy;
									this.doorPosZ = dpz;
									
									this.distanceX = (float)this.doorPosX + 0.5F - (float)this.posX;
									this.distanceZ = (float)this.doorPosZ + 0.5F - (float)this.posZ;
									this.chasingDoor = true;
									this.doorBreakTime = 240;
								}
							}
						}
					}		
				}
			}
		} else {
			float f1 = (float)this.doorPosX + 0.5F - (float)this.posX;
			float f2 = (float)this.doorPosZ + 0.5F - (float)this.posZ;
			float f = this.distanceX * f1 + this.distanceZ * f2;
			
			if (f < 0.0F) {
				this.chasingDoor = false;
			}
			
			if(this.rand.nextInt(20) == 0) {
				worldObj.playSoundAtEntity(this, "mob.zombie.wood", 2.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}
			
			if(--this.doorBreakTime == 0) {
				this.worldObj.setBlockWithNotify(doorPosX, doorPosY, doorPosZ, 0);
				worldObj.playSoundAtEntity(this, "mob.zombie.woodbreak", 2.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}
		}
		super.onLivingUpdate();
	}

	protected BlockDoor findDoorBlock(int x, int y, int z) {
		int blockID = this.worldObj.getBlockId(x, y, z);
		if (blockID != Block.doorWood.blockID) return null;
		BlockDoor blockDoor = (BlockDoor)Block.blocksList[blockID];
		return blockDoor;
	}
	
	@Override
	protected Entity findPlayerToAttack() {
			
		EntityPlayer entityPlayer1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D); 
		if(entityPlayer1 != null && this.canEntityBeSeen(entityPlayer1)) return entityPlayer1;
		
		// Find close entities of type EntityAmazon, EntityPigman/EntityCowman, etc
		List<Entity> list = this.worldObj.getEntitiesWithinAABB(ISentient.class, this.boundingBox.expand(16.0D, 4.0D, 16.0D));
		Collections.sort(list, new AttackableTargetSorter(this));
		
		// Return closest
		Iterator<Entity> iterator = list.iterator();		
		while(iterator.hasNext()) {
			EntityLiving entityLiving = (EntityLiving)iterator.next();
			if(this.isValidTarget(entityLiving)) return entityLiving;
		}
		
		return null;
	}
	
	public boolean isValidTarget(EntityLiving entityLiving) {
		if(entityLiving == null) return false;
		
		if(entityLiving == this) return false;
		
		if(!entityLiving.isEntityAlive()) return false;
		
		if(entityLiving.boundingBox.minY >= this.boundingBox.maxY || entityLiving.boundingBox.maxY <= this.boundingBox.minY) return false;
		
		return true;
	}
	
	public boolean burnsOnDaylight() {
		return true;
	}

	@Override
	protected String getLivingSound() {
		return "mob.zombie";
	}

	@Override
	protected String getHurtSound() {
		return "mob.zombiehurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.zombiedeath";
	}

	@Override
	protected int getDropItemId() {
		return Item.feather.shiftedIndex;
	}
	
	@Override
	public int getMaxHealth() {
		return 20;
	}
	
}
