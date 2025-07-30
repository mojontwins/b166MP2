package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class EntityAITempt extends EntityAIBase {
	private EntityCreature temptedEntity;
	private float field_48275_b;
	private double field_48276_c;
	private double field_48273_d;
	private double field_48274_e;
	private double field_48271_f;
	private double field_48272_g;
	private EntityPlayer temptingPlayer;
	private int delayTemptCounter = 0;
	private boolean field_48280_j;
	private int breedingFood;
	private boolean scaredByPlayerMovement;
	private boolean field_48279_m;

	public EntityAITempt(EntityCreature entityCreature1, float f2, int i3, boolean z4) {
		this.temptedEntity = entityCreature1;
		this.field_48275_b = f2;
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
				if(this.temptingPlayer.getDistanceSq(this.field_48276_c, this.field_48273_d, this.field_48274_e) > 0.010000000000000002D) {
					return false;
				}

				if(Math.abs((double)this.temptingPlayer.rotationPitch - this.field_48271_f) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.field_48272_g) > 5.0D) {
					return false;
				}
			} else {
				this.field_48276_c = this.temptingPlayer.posX;
				this.field_48273_d = this.temptingPlayer.posY;
				this.field_48274_e = this.temptingPlayer.posZ;
			}

			this.field_48271_f = (double)this.temptingPlayer.rotationPitch;
			this.field_48272_g = (double)this.temptingPlayer.rotationYaw;
		}

		return this.shouldExecute();
	}

	public void startExecuting() {
		this.field_48276_c = this.temptingPlayer.posX;
		this.field_48273_d = this.temptingPlayer.posY;
		this.field_48274_e = this.temptingPlayer.posZ;
		this.field_48280_j = true;
		this.field_48279_m = this.temptedEntity.getNavigator().getAvoidsWater();
		this.temptedEntity.getNavigator().setAvoidsWater(false);
	}

	public void resetTask() {
		this.temptingPlayer = null;
		this.temptedEntity.getNavigator().clearPathEntity();
		this.delayTemptCounter = 100;
		this.field_48280_j = false;
		this.temptedEntity.getNavigator().setAvoidsWater(this.field_48279_m);
	}

	public void updateTask() {
		this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float)this.temptedEntity.getVerticalFaceSpeed());
		if(this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25D) {
			this.temptedEntity.getNavigator().clearPathEntity();
		} else {
			this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.field_48275_b);
		}

	}

	public boolean func_48270_h() {
		return this.field_48280_j;
	}
}
