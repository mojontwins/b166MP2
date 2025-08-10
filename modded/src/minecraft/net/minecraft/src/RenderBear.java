package net.minecraft.src;

public class RenderBear extends RenderLiving {
	public RenderBear(ModelBase modelBase1, ModelBase modelBase2, float f3) {
		super(modelBase1, f3);
		this.setRenderPassModel(modelBase2);
	}

	protected boolean c(EntityBear entityBear1, int i2) {
		this.loadTexture("/mob/bearb.png");
		return i2 == 0 && !entityBear1.bearboolean;
	}

	protected boolean shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.c((EntityBear)entityLiving1, i2);
	}
}
