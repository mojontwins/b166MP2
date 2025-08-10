package net.minecraft.src;

public class RenderWWolf extends RenderLiving {
	public RenderWWolf(ModelBase modelBase1, ModelBase modelBase2, float f3) {
		super(modelBase1, f3);
		this.setRenderPassModel(modelBase2);
	}

	protected boolean a(EntityWWolf entityWWolf1, int i2) {
		this.loadTexture("/mob/wolfb.png");
		return i2 == 0 && !entityWWolf1.wolfboolean;
	}

	protected boolean shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.a((EntityWWolf)entityLiving1, i2);
	}
}
