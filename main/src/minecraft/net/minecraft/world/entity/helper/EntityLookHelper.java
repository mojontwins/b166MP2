package net.minecraft.world.entity.helper;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

public class EntityLookHelper {
	private EntityLiving entity;
	private float field_46149_b;
	private float field_46150_c;
	private boolean field_46147_d = false;
	private double posX;
	private double posY;
	private double posZ;

	public EntityLookHelper(EntityLiving entityLiving1) {
		this.entity = entityLiving1;
	}

	public void setLookPositionWithEntity(Entity entity1, float f2, float f3) {
		this.posX = entity1.posX;
		if(entity1 instanceof EntityLiving) {
			this.posY = entity1.posY + (double)((EntityLiving)entity1).getEyeHeight();
		} else {
			this.posY = (entity1.boundingBox.minY + entity1.boundingBox.maxY) / 2.0D;
		}

		this.posZ = entity1.posZ;
		this.field_46149_b = f2;
		this.field_46150_c = f3;
		this.field_46147_d = true;
	}

	public void setLookPosition(double d1, double d3, double d5, float f7, float f8) {
		this.posX = d1;
		this.posY = d3;
		this.posZ = d5;
		this.field_46149_b = f7;
		this.field_46150_c = f8;
		this.field_46147_d = true;
	}

	public void onUpdateLook() {
		this.entity.rotationPitch = 0.0F;
		if(this.field_46147_d) {
			this.field_46147_d = false;
			double d1 = this.posX - this.entity.posX;
			double d3 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
			double d5 = this.posZ - this.entity.posZ;
			double d7 = (double)MathHelper.sqrt_double(d1 * d1 + d5 * d5);
			float f9 = (float)(Math.atan2(d5, d1) * 180.0D / (double)(float)Math.PI) - 90.0F;
			float f10 = (float)(-(Math.atan2(d3, d7) * 180.0D / (double)(float)Math.PI));
			this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f10, this.field_46150_c);
			this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f9, this.field_46149_b);
		} else {
			this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
		}

		float f11;
		for(f11 = this.entity.rotationYawHead - this.entity.renderYawOffset; f11 < -180.0F; f11 += 360.0F) {
		}

		while(f11 >= 180.0F) {
			f11 -= 360.0F;
		}

		if(!this.entity.getNavigator().noPath()) {
			if(f11 < -75.0F) {
				this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
			}

			if(f11 > 75.0F) {
				this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
			}
		}

	}

	private float updateRotation(float f1, float f2, float f3) {
		float f4;
		for(f4 = f2 - f1; f4 < -180.0F; f4 += 360.0F) {
		}

		while(f4 >= 180.0F) {
			f4 -= 360.0F;
		}

		if(f4 > f3) {
			f4 = f3;
		}

		if(f4 < -f3) {
			f4 = -f3;
		}

		return f1 + f4;
	}
}
