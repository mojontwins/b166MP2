package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntitySquid;

public class RenderSquid extends RenderLiving {
	public RenderSquid(ModelBase theModel, float f2) {
		super(theModel, f2);
	}

	public void renderSquid(EntitySquid theSquid, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(theSquid, d2, d4, d6, f8, f9);
	}

	protected void setupRotations(EntitySquid theSquid, float f2, float f3, float f4) {
		float f5 = theSquid.xBodyRotO + (theSquid.xBodyRot - theSquid.xBodyRotO) * f4;
		float f6 = theSquid.zBodyRotO + (theSquid.zBodyRot - theSquid.zBodyRotO) * f4;
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		GL11.glRotatef(180.0F - f3, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(f5, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(f6, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, -1.2F, 0.0F);
	}

	protected void func_21005_a(EntitySquid theSquid, float f2) {
	}

	protected float handleRotationFloat(EntitySquid theSquid, float f2) {
		float flap = theSquid.oldTentacleAngle + (theSquid.tentacleAngle - theSquid.oldTentacleAngle) * f2;
		return flap;
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.func_21005_a((EntitySquid)entityLiving1, f2);
	}

	protected float handleRotationFloat(EntityLiving entityLiving1, float f2) {
		return this.handleRotationFloat((EntitySquid)entityLiving1, f2);
	}

	protected void rotateCorpse(EntityLiving entityLiving1, float f2, float f3, float f4) {
		this.setupRotations((EntitySquid)entityLiving1, f2, f3, f4);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.renderSquid((EntitySquid)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.renderSquid((EntitySquid)entity1, d2, d4, d6, f8, f9);
	}
}
