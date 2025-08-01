package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityGiantZombie;

public class RenderGiantZombie extends RenderLiving {
	private float scale;

	public RenderGiantZombie(ModelBase modelBase1, float f2, float f3) {
		super(modelBase1, f2 * f3);
		this.scale = f3;
	}

	protected void preRenderScale(EntityGiantZombie entityGiantZombie1, float f2) {
		GL11.glScalef(this.scale, this.scale, this.scale);
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.preRenderScale((EntityGiantZombie)entityLiving1, f2);
	}
}
