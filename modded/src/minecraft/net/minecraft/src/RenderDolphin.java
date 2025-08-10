package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderDolphin extends RenderLiving {
	public RenderDolphin(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityDolphin entityDolphin10 = (EntityDolphin)entityLiving1;
		if(!entityDolphin10.typechosen) {
			entityDolphin10.chooseType();
		}

		super.doRenderLiving(entityDolphin10, d2, d4, d6, f8, f9);
	}

	protected void stretch(EntityDolphin entityDolphin1) {
		GL11.glScalef(entityDolphin1.b, entityDolphin1.b, entityDolphin1.b);
	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		this.stretch((EntityDolphin)entityLiving1);
		return (float)entityLiving1.ticksExisted + f2;
	}
}
