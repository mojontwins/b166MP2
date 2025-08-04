package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class EntityAITempt extends EntityAIBase {
	private EntityCreature temptedEntity;
	private float pathStrength;
	private double destX;
	private double destY;
	private double destZ;
	private double towardsPitch;
	private double towardsYaw;
	private EntityPlayer temptingPlayer;
	private int delayTemptCounter = 0;
	private boolean tempted;
	private int breedingFood;
	private boolean scaredByPlayerMovement;
	private boolean avoidsWater;

	public EntityAITempt(EntityCreature entityCreature1, float f2, int i3, boolean z4) {
		this.temptedEntity = entityCreature1;
		this.pathStrength = f2;
		this.breedingFood = i3;
		this.scaredByPlayerMovement = z4;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		if(this.delayTemptCounter > 0) {
			--this.delayTemptCounter;
			return false;
		} else {
			this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, 10.0D);
			if(this.temptingPlayer == null) {
				return false;
			} else {
				ItemStack itemStack1 = this.temptingPlayer.getCurrentEquippedItem();
				return itemStack1 == null ? false : itemStack1.itemID == this.breedingFood;
			}
		}
	}

	public boolean continueExecuting() {
		if(this.scaredByPlayerMovement) {
			if(this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0D) {
				if(this.temptingPlayer.getDistanceSq(this.destX, this.destY, this.destZ) > 0.010000000000000002D) {
					return false;
				}

				if(Math.abs((double)this.temptingPlayer.rotationPitch - this.towardsPitch) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.towardsYaw) > 5.0D) {
					return false;
				}
			} else {
				this.destX = this.temptingPlayer.posX;
				this.destY = this.temptingPlayer.posY;
				this.destZ = this.temptingPlayer.posZ;
			}

			this.towardsPitch = (double)this.temptingPlayer.rotationPitch;
			this.towardsYaw = (double)this.temptingPlayer.rotationYaw;
		}

		return this.shouldExecute();
	}

	public void startExecuting() {
		this.destX = this.temptingPlayer.posX;
		this.destY = this.temptingPlayer.posY;
		this.destZ = this.temptingPlayer.posZ;
		this.tempted = true;
		this.avoidsWater = this.temptedEntity.getNavigator().getAvoidsWater();
		this.temptedEntity.getNavigator().setAvoidsWater(false);
	}

	public void resetTask() {
		this.temptingPlayer = null;
		this.temptedEntity.getNavigator().clearPathEntity();
		this.delayTemptCounter = 100;
		this.tempted = false;
		this.temptedEntity.getNavigator().setAvoidsWater(this.avoidsWater);
	}

	public void updateTask() {
		this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float)this.temptedEntity.getVerticalFaceSpeed());
		if(this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25D) {
			this.temptedEntity.getNavigator().clearPathEntity();
		} else {
			this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.pathStrength);
		}

	}

	public boolean isTempted() {
		return this.tempted;
	}
}
