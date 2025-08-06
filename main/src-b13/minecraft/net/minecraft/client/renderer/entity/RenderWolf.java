package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityWolf;

public class RenderWolf extends RenderLiving {
	public RenderWolf(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void renderWolf(EntityWolf entityWolf1, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(entityWolf1, d2, d4, d6, f8, f9);
	}

	protected float getTailRotation(EntityWolf entityWolf1, float f2) {
		return entityWolf1.getTailRotation();
	}

	protected void func_25006_b(EntityWolf entityWolf1, float f2) {
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.func_25006_b((EntityWolf)entityLiving1, f2);
	}

	protected float handleRotationFloat(EntityLiving entityLiving1, float f2) {
		return this.getTailRotation((EntityWolf)entityLiving1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.renderWolf((EntityWolf)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.renderWolf((EntityWolf)entity1, d2, d4, d6, f8, f9);
	}
}
