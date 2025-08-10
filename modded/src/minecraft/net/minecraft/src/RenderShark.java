package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderShark extends RenderLiving {
	public RenderShark(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityShark entityShark10 = (EntityShark)entityLiving1;
		super.doRenderLiving(entityShark10, d2, d4, d6, f8, f9);
	}

	protected void stretch(EntityShark entityShark1) {
		GL11.glScalef(entityShark1.b, entityShark1.b, entityShark1.b);
	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		this.stretch((EntityShark)entityLiving1);
		return (float)entityLiving1.ticksExisted + f2;
	}
}
