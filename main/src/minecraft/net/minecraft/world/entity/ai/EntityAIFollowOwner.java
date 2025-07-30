package net.minecraft.world.entity.ai;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityTameable;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathNavigate;

public class EntityAIFollowOwner extends EntityAIBase {
	private EntityTameable thePet;
	private EntityLiving theOwner;
	World theWorld;
	private float field_48303_f;
	private PathNavigate petPathfinder;
	private int field_48310_h;
	float maxDist;
	float minDist;
	float warpDist;
	private boolean field_48311_i;

	public EntityAIFollowOwner(EntityTameable entityTameable1, float f2, float f3, float f4, float warpDist) {
		this.thePet = entityTameable1;
		this.theWorld = entityTameable1.worldObj;
		this.field_48303_f = f2;
		this.petPathfinder = entityTameable1.getNavigator();
		this.minDist = f3;
		this.maxDist = f4;
		this.warpDist = warpDist;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		EntityLiving entityLiving1 = this.thePet.getOwner();
		if(entityLiving1 == null) {
			return false;
		} else if(this.thePet.isSitting()) {
			return false;
		} else if(this.thePet.getDistanceSqToEntity(entityLiving1) < (double)(this.minDist * this.minDist)) {
			return false;
		} else {
			this.theOwner = entityLiving1;
			return true;
		}
	}

	public boolean continueExecuting() {
		return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !this.thePet.isSitting();
	}

	public void startExecuting() {
		this.field_48310_h = 0;
		this.field_48311_i = this.thePet.getNavigator().getAvoidsWater();
		this.thePet.getNavigator().setAvoidsWater(false);
	}

	public void resetTask() {
		this.theOwner = null;
		this.petPathfinder.clearPathEntity();
		this.thePet.getNavigator().setAvoidsWater(this.field_48311_i);
	}

	public void updateTask() {
		this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.thePet.getVerticalFaceSpeed());
		if(!this.thePet.isSitting()) {
			if(--this.field_48310_h <= 0) {
				this.field_48310_h = 10;
				if(!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.field_48303_f)) {
					if(this.warpDist > 0 && this.thePet.getDistanceSqToEntity(this.theOwner) >= this.warpDist) { 
						int i1 = MathHelper.floor_double(this.theOwner.posX) - 2;
						int i2 = MathHelper.floor_double(this.theOwner.posZ) - 2;
						int i3 = MathHelper.floor_double(this.theOwner.boundingBox.minY);

						for(int i4 = 0; i4 <= 4; ++i4) {
							for(int i5 = 0; i5 <= 4; ++i5) {
								if((i4 < 1 || i5 < 1 || i4 > 3 || i5 > 3) && this.theWorld.isBlockNormalCube(i1 + i4, i3 - 1, i2 + i5) && !this.theWorld.isBlockNormalCube(i1 + i4, i3, i2 + i5) && !this.theWorld.isBlockNormalCube(i1 + i4, i3 + 1, i2 + i5)) {
									this.thePet.setLocationAndAngles((double)((float)(i1 + i4) + 0.5F), (double)i3, (double)((float)(i2 + i5) + 0.5F), this.thePet.rotationYaw, this.thePet.rotationPitch);
									this.petPathfinder.clearPathEntity();
									return;
								}
							}
						}

					}
				}
			}
		}
	}
}
