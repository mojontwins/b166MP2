package net.minecraft.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityWolf;

public class ModelWolf extends ModelBase {
	public ModelRenderer wolfHeadMain;
	public ModelRenderer wolfBody;
	public ModelRenderer wolfLeg1;
	public ModelRenderer wolfLeg2;
	public ModelRenderer wolfLeg3;
	public ModelRenderer wolfLeg4;
	ModelRenderer wolfTail;
	ModelRenderer wolfMane;

	public ModelWolf() {
		float f1 = 0.0F;
		float f2 = 13.5F;
		this.wolfHeadMain = new ModelRenderer(this, 0, 0);
		this.wolfHeadMain.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, f1);
		this.wolfHeadMain.setRotationPoint(-1.0F, f2, -7.0F);
		this.wolfBody = new ModelRenderer(this, 18, 14);
		this.wolfBody.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, f1);
		this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
		this.wolfMane = new ModelRenderer(this, 21, 0);
		this.wolfMane.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, f1);
		this.wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
		this.wolfLeg1 = new ModelRenderer(this, 0, 18);
		this.wolfLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f1);
		this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
		this.wolfLeg2 = new ModelRenderer(this, 0, 18);
		this.wolfLeg2.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f1);
		this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
		this.wolfLeg3 = new ModelRenderer(this, 0, 18);
		this.wolfLeg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f1);
		this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
		this.wolfLeg4 = new ModelRenderer(this, 0, 18);
		this.wolfLeg4.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f1);
		this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
		this.wolfTail = new ModelRenderer(this, 9, 18);
		this.wolfTail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f1);
		this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
		this.wolfHeadMain.setTextureOffset(16, 14).addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, f1);
		this.wolfHeadMain.setTextureOffset(16, 14).addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, f1);
		this.wolfHeadMain.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, f1);
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		super.render(entity1, f2, f3, f4, f5, f6, f7);
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		if(this.isChild) {
			float f8 = 2.0F;
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 5.0F * f7, 2.0F * f7);
			this.wolfHeadMain.renderWithRotation(f7);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / f8, 1.0F / f8, 1.0F / f8);
			GL11.glTranslatef(0.0F, 24.0F * f7, 0.0F);
			this.wolfBody.render(f7);
			this.wolfLeg1.render(f7);
			this.wolfLeg2.render(f7);
			this.wolfLeg3.render(f7);
			this.wolfLeg4.render(f7);
			this.wolfTail.renderWithRotation(f7);
			this.wolfMane.render(f7);
			GL11.glPopMatrix();
		} else {
			this.wolfHeadMain.renderWithRotation(f7);
			this.wolfBody.render(f7);
			this.wolfLeg1.render(f7);
			this.wolfLeg2.render(f7);
			this.wolfLeg3.render(f7);
			this.wolfLeg4.render(f7);
			this.wolfTail.renderWithRotation(f7);
			this.wolfMane.render(f7);
		}

	}

	public void setLivingAnimations(EntityLiving entityLiving1, float f2, float f3, float f4) {
		EntityWolf entityWolf5 = (EntityWolf)entityLiving1;
		if(entityWolf5.isAngry()) {
			this.wolfTail.rotateAngleY = 0.0F;
		} else {
			this.wolfTail.rotateAngleY = MathHelper.cos(f2 * 0.6662F) * 1.4F * f3;
		}

		if(entityWolf5.isSitting()) {
			this.wolfMane.setRotationPoint(-1.0F, 16.0F, -3.0F);
			this.wolfMane.rotateAngleX = 1.2566371F;
			this.wolfMane.rotateAngleY = 0.0F;
			this.wolfBody.setRotationPoint(0.0F, 18.0F, 0.0F);
			this.wolfBody.rotateAngleX = 0.7853982F;
			this.wolfTail.setRotationPoint(-1.0F, 21.0F, 6.0F);
			this.wolfLeg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
			this.wolfLeg1.rotateAngleX = 4.712389F;
			this.wolfLeg2.setRotationPoint(0.5F, 22.0F, 2.0F);
			this.wolfLeg2.rotateAngleX = 4.712389F;
			this.wolfLeg3.rotateAngleX = 5.811947F;
			this.wolfLeg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
			this.wolfLeg4.rotateAngleX = 5.811947F;
			this.wolfLeg4.setRotationPoint(0.51F, 17.0F, -4.0F);
		} else {
			this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
			this.wolfBody.rotateAngleX = (float)Math.PI / 2F;
			this.wolfMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
			this.wolfMane.rotateAngleX = this.wolfBody.rotateAngleX;
			this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
			this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
			this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
			this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
			this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
			this.wolfLeg1.rotateAngleX = MathHelper.cos(f2 * 0.6662F) * 1.4F * f3;
			this.wolfLeg2.rotateAngleX = MathHelper.cos(f2 * 0.6662F + (float)Math.PI) * 1.4F * f3;
			this.wolfLeg3.rotateAngleX = MathHelper.cos(f2 * 0.6662F + (float)Math.PI) * 1.4F * f3;
			this.wolfLeg4.rotateAngleX = MathHelper.cos(f2 * 0.6662F) * 1.4F * f3;
		}

		this.wolfHeadMain.rotateAngleZ = entityWolf5.getInterestedAngle(f4) + entityWolf5.getShakeAngle(f4, 0.0F);
		this.wolfMane.rotateAngleZ = entityWolf5.getShakeAngle(f4, -0.08F);
		this.wolfBody.rotateAngleZ = entityWolf5.getShakeAngle(f4, -0.16F);
		this.wolfTail.rotateAngleZ = entityWolf5.getShakeAngle(f4, -0.2F);
		if(entityWolf5.getWolfShaking()) {
			float f6 = entityWolf5.getBrightness(f4) * entityWolf5.getShadingWhileShaking(f4);
			GL11.glColor3f(f6, f6, f6);
		}

	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.wolfHeadMain.rotateAngleX = f5 / 57.295776F;
		this.wolfHeadMain.rotateAngleY = f4 / 57.295776F;
		this.wolfTail.rotateAngleX = f3;
	}
}
