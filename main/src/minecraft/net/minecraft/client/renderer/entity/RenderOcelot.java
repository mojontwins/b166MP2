package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityOcelot;

public class RenderOcelot extends RenderLiving {
	public RenderOcelot(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void func_48424_a(EntityOcelot entityOcelot1, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(entityOcelot1, d2, d4, d6, f8, f9);
	}

	protected void func_48423_a(EntityOcelot entityOcelot1, float f2) {
		super.preRenderCallback(entityOcelot1, f2);
		if(entityOcelot1.isTamed()) {
			GL11.glScalef(0.8F, 0.8F, 0.8F);
		}

	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.func_48423_a((EntityOcelot)entityLiving1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.func_48424_a((EntityOcelot)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.func_48424_a((EntityOcelot)entity1, d2, d4, d6, f8, f9);
	}
}
