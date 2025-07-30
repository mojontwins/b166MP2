package net.minecraft.world.entity.helper;

import net.minecraft.world.entity.EntityLiving;

public class EntityBodyHelper {
	private EntityLiving theEntityLiving;
	private int field_48652_b = 0;
	private float field_48653_c = 0.0F;

	public EntityBodyHelper(EntityLiving entityLiving1) {
		this.theEntityLiving = entityLiving1;
	}

	public void func_48650_a() {
		double d1 = this.theEntityLiving.posX - this.theEntityLiving.prevPosX;
		double d3 = this.theEntityLiving.posZ - this.theEntityLiving.prevPosZ;
		if(d1 * d1 + d3 * d3 > 2.500000277905201E-7D) {
			this.theEntityLiving.renderYawOffset = this.theEntityLiving.rotationYaw;
			this.theEntityLiving.rotationYawHead = this.func_48651_a(this.theEntityLiving.renderYawOffset, this.theEntityLiving.rotationYawHead, 75.0F);
			this.field_48653_c = this.theEntityLiving.rotationYawHead;
			this.field_48652_b = 0;
		} else {
			float f5 = 75.0F;
			if(Math.abs(this.theEntityLiving.rotationYawHead - this.field_48653_c) > 15.0F) {
				this.field_48652_b = 0;
				this.field_48653_c = this.theEntityLiving.rotationYawHead;
			} else {
				++this.field_48652_b;
				if(this.field_48652_b > 10) {
					f5 = Math.max(1.0F - (float)(this.field_48652_b - 10) / 10.0F, 0.0F) * 75.0F;
				}
			}

			this.theEntityLiving.renderYawOffset = this.func_48651_a(this.theEntityLiving.rotationYawHead, this.theEntityLiving.renderYawOffset, f5);
		}
	}

	private float func_48651_a(float f1, float f2, float f3) {
		float f4;
		for(f4 = f1 - f2; f4 < -180.0F; f4 += 360.0F) {
		}

		while(f4 >= 180.0F) {
			f4 -= 360.0F;
		}

		if(f4 < -f3) {
			f4 = -f3;
		}

		if(f4 >= f3) {
			f4 = f3;
		}

		return f1 - f4;
	}
}
