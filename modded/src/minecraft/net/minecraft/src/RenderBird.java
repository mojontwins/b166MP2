package net.minecraft.src;

public class RenderBird extends RenderLiving {
	public RenderBird(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityBird entityBird10 = (EntityBird)entityLiving1;
		if(!entityBird10.typechosen) {
			entityBird10.chooseType();
		}

		super.doRenderLiving(entityBird10, d2, d4, d6, f8, f9);
	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		EntityBird entityBird3 = (EntityBird)entityLiving1;
		float f4 = entityBird3.winge + (entityBird3.wingb - entityBird3.winge) * f2;
		float f5 = entityBird3.wingd + (entityBird3.wingc - entityBird3.wingd) * f2;
		return (MathHelper.sin(f4) + 1.0F) * f5;
	}
}
