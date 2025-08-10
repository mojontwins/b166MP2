package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderBigCat extends RenderLiving {
	public RenderBigCat(ModelBase modelBase1, ModelBase modelBase2, float f3) {
		super(modelBase1, f3);
		this.setRenderPassModel(modelBase2);
	}

	protected boolean a(EntityBigCat entityBigCat1, int i2) {
		if(entityBigCat1.typeint == 2 && entityBigCat1.adult) {
			this.loadTexture("/mob/lionb.png");
		} else {
			this.loadTexture("/mob/lionc.png");
		}

		return i2 == 0 && !entityBigCat1.lionboolean;
	}

	protected boolean shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.a((EntityBigCat)entityLiving1, i2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityBigCat entityBigCat10 = (EntityBigCat)entityLiving1;
		if(!entityBigCat10.typechosen) {
			entityBigCat10.chooseType();
		}

		super.doRenderLiving(entityBigCat10, d2, d4, d6, f8, f9);
	}

	protected void stretch(EntityBigCat entityBigCat1) {
		float f2 = entityBigCat1.edad;
		if(entityBigCat1.adult) {
			f2 = 1.0F;
		}

		GL11.glScalef(f2 * entityBigCat1.widthF, f2 * entityBigCat1.heightF, f2 * entityBigCat1.lengthF);
	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		EntityBigCat entityBigCat3 = (EntityBigCat)entityLiving1;
		this.stretch(entityBigCat3);
		return (float)entityLiving1.ticksExisted + f2;
	}
}
