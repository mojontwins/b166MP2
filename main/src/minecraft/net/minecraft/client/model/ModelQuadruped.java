package net.minecraft.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;

public class ModelQuadruped extends ModelBase {
	public ModelRenderer head = new ModelRenderer(this, 0, 0);
	public ModelRenderer body;
	public ModelRenderer leg1;
	public ModelRenderer leg2;
	public ModelRenderer leg3;
	public ModelRenderer leg4;
	protected float headOffsetX = 8.0F;
	protected float headOffsetY = 4.0F;

	public ModelQuadruped(int i1, float f2) {
		this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, f2);
		this.head.setRotationPoint(0.0F, (float)(18 - i1), -6.0F);
		this.body = new ModelRenderer(this, 28, 8);
		this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, f2);
		this.body.setRotationPoint(0.0F, (float)(17 - i1), 2.0F);
		this.leg1 = new ModelRenderer(this, 0, 16);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, i1, 4, f2);
		this.leg1.setRotationPoint(-3.0F, (float)(24 - i1), 7.0F);
		this.leg2 = new ModelRenderer(this, 0, 16);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, i1, 4, f2);
		this.leg2.setRotationPoint(3.0F, (float)(24 - i1), 7.0F);
		this.leg3 = new ModelRenderer(this, 0, 16);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, i1, 4, f2);
		this.leg3.setRotationPoint(-3.0F, (float)(24 - i1), -5.0F);
		this.leg4 = new ModelRenderer(this, 0, 16);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, i1, 4, f2);
		this.leg4.setRotationPoint(3.0F, (float)(24 - i1), -5.0F);
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		if(this.isChild) {
			float f8 = 2.0F;
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, this.headOffsetX * f7, this.headOffsetY * f7);
			this.head.render(f7);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / f8, 1.0F / f8, 1.0F / f8);
			GL11.glTranslatef(0.0F, 24.0F * f7, 0.0F);
			this.body.render(f7);
			this.leg1.render(f7);
			this.leg2.render(f7);
			this.leg3.render(f7);
			this.leg4.render(f7);
			GL11.glPopMatrix();
		} else {
			this.head.render(f7);
			this.body.render(f7);
			this.leg1.render(f7);
			this.leg2.render(f7);
			this.leg3.render(f7);
			this.leg4.render(f7);
		}

	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.head.rotateAngleX = f5 / 57.295776F;
		this.head.rotateAngleY = f4 / 57.295776F;
		this.body.rotateAngleX = (float)Math.PI / 2F;
		this.leg1.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
		this.leg2.rotateAngleX = MathHelper.cos(f1 * 0.6662F + (float)Math.PI) * 1.4F * f2;
		this.leg3.rotateAngleX = MathHelper.cos(f1 * 0.6662F + (float)Math.PI) * 1.4F * f2;
		this.leg4.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
	}
}
