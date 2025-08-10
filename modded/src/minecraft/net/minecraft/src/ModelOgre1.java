package net.minecraft.src;

public class ModelOgre1 extends ModelBiped {
	public ModelOgre1() {
		super(0.0F, 0.0F);
		this.bipedHead = new ModelRenderer(0, 0);
		this.bipedHead.addBox(-4.0F, -25.0F, -6.0F, 8, 8, 8, 0.0F);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedEars = new ModelRenderer(41, 24);
		this.bipedEars.addBox(-4.0F, -31.0F, -3.0F, 8, 6, 1, 0.5F);
		this.bipedEars.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody = new ModelRenderer(16, 16);
		this.bipedBody.addBox(-4.0F, -14.0F, -2.0F, 8, 12, 4, 3.0F);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightArm = new ModelRenderer(0, 16);
		this.bipedRightArm.addBox(-7.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F);
		this.bipedRightArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedLeftArm = new ModelRenderer(0, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.bipedLeftArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightLeg = new ModelRenderer(40, 0);
		this.bipedRightLeg.addBox(-2.0F, -8.0F, -2.0F, 4, 18, 4, 1.5F);
		this.bipedRightLeg.setRotationPoint(-4.0F, 12.0F, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(40, 0);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.addBox(-2.0F, -8.0F, -2.0F, 4, 18, 4, 1.5F);
		this.bipedLeftLeg.setRotationPoint(4.0F, 12.0F, 0.0F);
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6, boolean z7) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.bipedHead.render(f6);
		this.bipedBody.render(f6);
		this.bipedRightArm.render(f6);
		this.bipedLeftArm.render(f6);
		this.bipedRightLeg.render(f6);
		this.bipedLeftLeg.render(f6);
		this.bipedEars.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.bipedHead.rotateAngleY = f4 / 57.29578F;
		this.bipedEars.rotateAngleY = this.bipedHead.rotateAngleY;
		this.bipedEars.rotateAngleX = this.bipedHead.rotateAngleX;
		this.bipedRightArm.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 2.0F * f2 * 0.5F;
		this.bipedLeftArm.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 2.0F * f2 * 0.5F;
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightLeg.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
		this.bipedLeftLeg.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.bipedRightLeg.rotateAngleY = 0.0F;
		this.bipedLeftLeg.rotateAngleY = 0.0F;
		if(this.isRiding) {
			this.bipedRightArm.rotateAngleX += -0.6283186F;
			this.bipedLeftArm.rotateAngleX += -0.6283186F;
			this.bipedRightLeg.rotateAngleX = -1.256637F;
			this.bipedLeftLeg.rotateAngleX = -1.256637F;
			this.bipedRightLeg.rotateAngleY = 0.314159F;
			this.bipedLeftLeg.rotateAngleY = -0.314159F;
		}

		if(this.field_1279_h) {
			this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.314159F;
		}

		if(this.field_1278_i) {
			this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.314159F;
		}

		this.bipedRightArm.rotateAngleY = 0.0F;
		this.bipedLeftArm.rotateAngleY = 0.0F;
		if(this.onGround > -9990.0F) {
			float f7 = this.onGround;
			this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f7) * 3.141593F * 2.0F) * 0.2F;
			this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
			this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
			this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
			f7 = 1.0F - this.onGround;
			f7 *= f7;
			f7 *= f7;
			f7 = 1.0F - f7;
			float f8 = MathHelper.sin(f7 * 3.141593F);
			float f9 = MathHelper.sin(this.onGround * 3.141593F) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
			ModelRenderer modelRenderer10 = this.bipedRightArm;
			modelRenderer10.rotateAngleX = (float)((double)modelRenderer10.rotateAngleX - ((double)f8 * 1.2D + (double)f9));
			this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
			this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.onGround * 3.141593F) * -0.4F;
		}

		if(this.isSneak) {
			this.bipedBody.rotateAngleX = 0.5F;
			this.bipedRightLeg.rotateAngleX -= 0.0F;
			this.bipedLeftLeg.rotateAngleX -= 0.0F;
			this.bipedRightArm.rotateAngleX += 0.4F;
			this.bipedLeftArm.rotateAngleX += 0.4F;
			this.bipedRightLeg.rotationPointZ = 4.0F;
			this.bipedLeftLeg.rotationPointZ = 4.0F;
			this.bipedRightLeg.rotationPointY = 9.0F;
			this.bipedLeftLeg.rotationPointY = 9.0F;
			this.bipedHead.rotationPointY = 1.0F;
		} else {
			this.bipedBody.rotateAngleX = 0.0F;
			this.bipedRightLeg.rotationPointZ = 0.0F;
			this.bipedLeftLeg.rotationPointZ = 0.0F;
			this.bipedRightLeg.rotationPointY = 12.0F;
			this.bipedLeftLeg.rotationPointY = 12.0F;
			this.bipedHead.rotationPointY = 0.0F;
		}

		this.bipedRightArm.rotateAngleZ += MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(f3 * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(f3 * 0.067F) * 0.05F;
	}
}
