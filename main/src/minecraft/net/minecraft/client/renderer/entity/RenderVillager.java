package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityVillager;

public class RenderVillager extends RenderLiving {
	protected ModelVillager mainModel = (ModelVillager)this.mainModel;

	public RenderVillager() {
		super(new ModelVillager(0.0F), 0.5F);
	}

	protected int func_40293_a(EntityVillager entityVillager1, int i2, float f3) {
		return -1;
	}

	public void renderVillager(EntityVillager entityVillager1, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(entityVillager1, d2, d4, d6, f8, f9);
	}

	protected void func_40290_a(EntityVillager entityVillager1, double d2, double d4, double d6) {
	}

	protected void func_40291_a(EntityVillager entityVillager1, float f2) {
		super.renderEquippedItems(entityVillager1, f2);
	}

	protected void func_40292_b(EntityVillager entityVillager1, float f2) {
		float f3 = 0.9375F;
		if(entityVillager1.getGrowingAge() < 0) {
			f3 = (float)((double)f3 * 0.5D);
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}

		GL11.glScalef(f3, f3, f3);
	}

	protected void passSpecialRender(EntityLiving entityLiving1, double d2, double d4, double d6) {
		this.func_40290_a((EntityVillager)entityLiving1, d2, d4, d6);
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.func_40292_b((EntityVillager)entityLiving1, f2);
	}

	protected int shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.func_40293_a((EntityVillager)entityLiving1, i2, f3);
	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
		this.func_40291_a((EntityVillager)entityLiving1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.renderVillager((EntityVillager)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.renderVillager((EntityVillager)entity1, d2, d4, d6, f8, f9);
	}
}
