package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.world.entity.EntityLiving;

public class RenderWraith extends RenderBiped {
	public RenderWraith(ModelBiped modelbiped, float f) {
		super(modelbiped, f, 1.0F);
		this.modelBipedMain = modelbiped;
	}

	public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2, float f, float f1) {
		boolean flag = false;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		if(!flag) {
			float transparency = 0.6F;
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(0.8F, 0.8F, 0.8F, transparency);
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}

		super.doRenderLiving(entityliving, d, d1, d2, f, f1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
