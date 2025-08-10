package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderDeer extends RenderLiving {
	public RenderDeer(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityDeer entityDeer10 = (EntityDeer)entityLiving1;
		if(!entityDeer10.typechosen) {
			entityDeer10.chooseType();
		}

		super.doRenderLiving(entityDeer10, d2, d4, d6, f8, f9);
	}

	protected void stretch(EntityDeer entityDeer1) {
		float f2 = entityDeer1.edad;
		float f3 = 0.0F;
		if(entityDeer1.typeint == 1) {
			f3 = 2.0F;
		} else if(entityDeer1.typeint == 2) {
			f3 = 1.5F;
		} else {
			f3 = f2;
		}

		if(entityDeer1.adult) {
			f2 = 1.0F;
		}

		GL11.glScalef(f3, f3, f3);
	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		EntityDeer entityDeer3 = (EntityDeer)entityLiving1;
		this.stretch(entityDeer3);
		return (float)entityLiving1.ticksExisted + f2;
	}

	protected void rotateDeer(EntityDeer entityDeer1) {
		if(!entityDeer1.onGround && entityDeer1.moveSpeed > 2.0F) {
			if(entityDeer1.motionY > 0.5D) {
				GL11.glRotatef(20.0F, -1.0F, 0.0F, 0.0F);
			} else if(entityDeer1.motionY < -0.5D) {
				GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
			} else {
				GL11.glRotatef((float)(entityDeer1.motionY * 40.0D), -1.0F, 0.0F, 0.0F);
			}
		}

	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.rotateDeer((EntityDeer)entityLiving1);
	}
}
