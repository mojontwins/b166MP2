package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderFishy extends RenderLiving {
	public RenderFishy(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityFishy entityFishy10 = (EntityFishy)entityLiving1;
		if(!entityFishy10.typechosen) {
			entityFishy10.chooseType();
		}

		super.doRenderLiving(entityFishy10, d2, d4, d6, f8, f9);
	}

	protected void stretch(EntityFishy entityFishy1) {
		GL11.glScalef(entityFishy1.b, entityFishy1.b, entityFishy1.b);
	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		EntityFishy entityFishy3 = (EntityFishy)entityLiving1;
		if(!entityFishy3.adult) {
			this.stretch(entityFishy3);
		}

		return (float)entityLiving1.ticksExisted + f2;
	}
}
