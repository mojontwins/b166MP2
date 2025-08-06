package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityPig;

public class RenderPig extends RenderLiving {
	public RenderPig(ModelBase modelBase1, ModelBase modelBase2, float f3) {
		super(modelBase1, f3);
		this.setRenderPassModel(modelBase2);
	}

	protected int renderSaddledPig(EntityPig entityPig1, int i2, float f3) {
		this.loadTexture("/mob/saddle.png");
		return i2 == 0 && entityPig1.getSaddled() ? 1 : -1;
	}

	public void func_40286_a(EntityPig entityPig1, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(entityPig1, d2, d4, d6, f8, f9);
	}

	protected int shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.renderSaddledPig((EntityPig)entityLiving1, i2, f3);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.func_40286_a((EntityPig)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.func_40286_a((EntityPig)entity1, d2, d4, d6, f8, f9);
	}
}
