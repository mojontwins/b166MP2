package net.minecraft.src;

public class ModelWraith extends ModelBiped {
	public ModelWraith() {
		super(12.0F, 0.0F);
		this.field_1279_h = false;
		this.field_1278_i = false;
		this.isSneak = false;
		this.bipedHead = new ModelRenderer(0, 40);
		this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 1, 1, 1, 0.0F);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedHeadwear = new ModelRenderer(0, 0);
		this.bipedHeadwear.addBox(-5.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody = new ModelRenderer(36, 0);
		this.bipedBody.addBox(-6.0F, 0.0F, -2.0F, 10, 20, 4, 0.0F);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightArm = new ModelRenderer(16, 16);
		this.bipedRightArm.addBox(-5.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.bipedLeftArm = new ModelRenderer(16, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedRightLeg = new ModelRenderer(0, 16);
		this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(0, 16);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6);
		float f7 = MathHelper.sin(this.onGround * 3.141593F);
		float f8 = MathHelper.sin((1.0F - (1.0F - this.onGround) * (1.0F - this.onGround)) * 3.141593F);
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightArm.rotateAngleY = -(0.1F - f7 * 0.6F);
		this.bipedLeftArm.rotateAngleY = 0.1F - f7 * 0.6F;
		this.bipedRightArm.rotateAngleX = -1.570796F;
		this.bipedLeftArm.rotateAngleX = -1.570796F;
		this.bipedRightArm.rotateAngleX -= f7 * 1.2F - f8 * 0.4F;
		this.bipedLeftArm.rotateAngleX -= f7 * 1.2F - f8 * 0.4F;
		this.bipedRightArm.rotateAngleZ += MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(f3 * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(f3 * 0.067F) * 0.05F;
	}
}
