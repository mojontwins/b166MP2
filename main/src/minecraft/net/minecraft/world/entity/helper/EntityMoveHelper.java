package net.minecraft.world.entity.helper;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;

public class EntityMoveHelper {
	private EntityLiving entity;
	private double posX;
	private double posY;
	private double posZ;
	private float speed;
	private boolean hasPath = false;

	public EntityMoveHelper(EntityLiving entityLiving1) {
		this.entity = entityLiving1;
		this.posX = entityLiving1.posX;
		this.posY = entityLiving1.posY;
		this.posZ = entityLiving1.posZ;
	}

	public boolean getHasPath() {
		return this.hasPath;
	}

	public float getSpeed() {
		return this.speed;
	}

	public void setMoveTo(double d1, double d3, double d5, float f7) {
		this.posX = d1;
		this.posY = d3;
		this.posZ = d5;
		this.speed = f7;
		this.hasPath = true;
	}

	public void onUpdateMoveHelper() {
		this.entity.setMoveForward(0.0F);
		if(this.hasPath) {
			this.hasPath = false;
			int i1 = MathHelper.floor_double(this.entity.boundingBox.minY + 0.5D);
			double d2 = this.posX - this.entity.posX;
			double d4 = this.posZ - this.entity.posZ;
			double d6 = this.posY - (double)i1;
			double d8 = d2 * d2 + d6 * d6 + d4 * d4;
			if(d8 >= 2.500000277905201E-7D) {
				float f10 = (float)(Math.atan2(d4, d2) * 180.0D / (double)(float)Math.PI) - 90.0F;
				this.entity.rotationYaw = this.func_48185_a(this.entity.rotationYaw, f10, 30.0F);
				this.entity.func_48098_g(this.speed);
				if(d6 > 0.0D && d2 * d2 + d4 * d4 < 1.0D) {
					this.entity.getJumpHelper().setJumping();
				}

			}
		}
	}

	private float func_48185_a(float f1, float f2, float f3) {
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
