package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderBunny extends RenderLiving {
	public RenderBunny(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityBunny entityBunny10 = (EntityBunny)entityLiving1;
		if(!entityBunny10.typechosen) {
			entityBunny10.chooseType();
		}

		super.doRenderLiving(entityBunny10, d2, d4, d6, f8, f9);
	}

	protected float a(EntityBunny entityBunny1, float f2) {
		float f3 = entityBunny1.e + (entityBunny1.b - entityBunny1.e) * f2;
		float f4 = entityBunny1.d + (entityBunny1.c - entityBunny1.d) * f2;
		return (MathHelper.sin(f3) + 1.0F) * f4;
	}

	protected float func_167_c(EntityLiving entityLiving1, float f2) {
		return this.a((EntityBunny)entityLiving1, f2);
	}

	protected void rotBunny(EntityBunny entityBunny1) {
		if(!entityBunny1.onGround && entityBunny1.ridingEntity == null) {
			if(entityBunny1.motionY > 0.5D) {
				GL11.glRotatef(35.0F, -1.0F, 0.0F, 0.0F);
			} else if(entityBunny1.motionY < -0.5D) {
				GL11.glRotatef(-35.0F, -1.0F, 0.0F, 0.0F);
			} else {
				GL11.glRotatef((float)(entityBunny1.motionY * 70.0D), -1.0F, 0.0F, 0.0F);
			}
		}

	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		EntityBunny entityBunny3 = (EntityBunny)entityLiving1;
		if(!entityBunny3.adult) {
			this.stretch(entityBunny3);
		}

		return (float)entityLiving1.ticksExisted + f2;
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.rotBunny((EntityBunny)entityLiving1);
	}

	protected void stretch(EntityBunny entityBunny1) {
		float f2 = entityBunny1.edad;
		GL11.glScalef(f2, f2, f2);
	}
}
