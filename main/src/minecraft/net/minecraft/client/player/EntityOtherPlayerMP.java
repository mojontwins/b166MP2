package net.minecraft.client.player;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntityOtherPlayerMP extends EntityPlayer {
	private boolean isItemInUse = false;
	private int otherPlayerMPPosRotationIncrements;
	private double otherPlayerMPX;
	private double otherPlayerMPY;
	private double otherPlayerMPZ;
	private double otherPlayerMPYaw;
	private double otherPlayerMPPitch;

	public EntityOtherPlayerMP(World world1, String string2) {
		super(world1);
		this.username = string2;
		this.yOffset = 0.0F;
		this.stepHeight = 0.0F;
		if(string2 != null && string2.length() > 0) {
			this.skinUrl = "http://s3.amazonaws.com/MinecraftSkins/" + string2 + ".png";
		}

		this.noClip = true;
		this.field_22062_y = 0.25F;
		this.renderDistanceWeight = 10.0D;
	}

	protected void resetHeight() {
		this.yOffset = 0.0F;
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		return true;
	}

	public void setPositionAndRotation2(double d1, double d3, double d5, float f7, float f8, int i9) {
		this.otherPlayerMPX = d1;
		this.otherPlayerMPY = d3;
		this.otherPlayerMPZ = d5;
		this.otherPlayerMPYaw = (double)f7;
		this.otherPlayerMPPitch = (double)f8;
		this.otherPlayerMPPosRotationIncrements = i9;
	}

	public void onUpdate() {
		this.field_22062_y = 0.0F;
		super.onUpdate();
		this.prevLegYaw = this.legYaw;
		double d1 = this.posX - this.prevPosX;
		double d3 = this.posZ - this.prevPosZ;
		float f5 = MathHelper.sqrt_double(d1 * d1 + d3 * d3) * 4.0F;
		if(f5 > 1.0F) {
			f5 = 1.0F;
		}

		this.legYaw += (f5 - this.legYaw) * 0.4F;
		this.field_703_S += this.legYaw;
		if(!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null) {
			ItemStack itemStack6 = this.inventory.mainInventory[this.inventory.currentItem];
			this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], Item.itemsList[itemStack6.itemID].getMaxItemUseDuration(itemStack6));
			this.isItemInUse = true;
		} else if(this.isItemInUse && !this.isEating()) {
			this.clearItemInUse();
			this.isItemInUse = false;
		}

	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void onLivingUpdate() {
		super.updateEntityActionState();
		if(this.otherPlayerMPPosRotationIncrements > 0) {
			double d1 = this.posX + (this.otherPlayerMPX - this.posX) / (double)this.otherPlayerMPPosRotationIncrements;
			double d3 = this.posY + (this.otherPlayerMPY - this.posY) / (double)this.otherPlayerMPPosRotationIncrements;
			double d5 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double)this.otherPlayerMPPosRotationIncrements;

			double d7;
			for(d7 = this.otherPlayerMPYaw - (double)this.rotationYaw; d7 < -180.0D; d7 += 360.0D) {
			}

			while(d7 >= 180.0D) {
				d7 -= 360.0D;
			}

			this.rotationYaw = (float)((double)this.rotationYaw + d7 / (double)this.otherPlayerMPPosRotationIncrements);
			this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
			--this.otherPlayerMPPosRotationIncrements;
			this.setPosition(d1, d3, d5);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}

		this.prevCameraYaw = this.cameraYaw;
		float f9 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f2 = (float)Math.atan(-this.motionY * (double)0.2F) * 15.0F;
		if(f9 > 0.1F) {
			f9 = 0.1F;
		}

		if(!this.onGround || this.getHealth() <= 0) {
			f9 = 0.0F;
		}

		if(this.onGround || this.getHealth() <= 0) {
			f2 = 0.0F;
		}

		this.cameraYaw += (f9 - this.cameraYaw) * 0.4F;
		this.cameraPitch += (f2 - this.cameraPitch) * 0.8F;
	}

	public void outfitWithItem(int i1, int i2, int i3) {
		ItemStack itemStack4 = null;
		if(i2 >= 0) {
			itemStack4 = new ItemStack(i2, 1, i3);
		}

		if(i1 == 0) {
			this.inventory.mainInventory[this.inventory.currentItem] = itemStack4;
		} else {
			this.inventory.armorInventory[i1 - 1] = itemStack4;
		}

	}

	public void func_6420_o() {
	}

	public float getEyeHeight() {
		return 1.82F;
	}
}
