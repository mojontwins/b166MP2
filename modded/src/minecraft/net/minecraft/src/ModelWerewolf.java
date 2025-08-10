package net.minecraft.src;

public class ModelWerewolf extends ModelBase {
	public ModelRenderer Head = new ModelRenderer(0, 0);
	public ModelRenderer Torso;
	public ModelRenderer Torso2;
	public ModelRenderer Abdomen;
	public ModelRenderer RightArm;
	public ModelRenderer LeftArm;
	public ModelRenderer RightTigh;
	public ModelRenderer LeftTigh;
	public ModelRenderer RightLeg;
	public ModelRenderer LeftLeg;
	public ModelRenderer Tail;
	public ModelRenderer RightEar;
	public ModelRenderer LeftEar;

	public ModelWerewolf() {
		this.Head.addBox(-0.5F, -8.5F, -8.0F, 3, 3, 4, 1.6F);
		this.Head.setRotationPoint(0.0F, 2.0F, -1.0F);
		this.RightEar = new ModelRenderer(15, 0);
		this.RightEar.addBox(-0.5F, -8.5F, -8.0F, 2, 4, 1, 0.0F);
		this.RightEar.setRotationPoint(3.5F, -4.0F, 3.5F);
		this.RightEar.rotateAngleZ = -0.7853981F;
		this.RightEar.rotateAngleX = -0.5235988F;
		this.LeftEar = new ModelRenderer(15, 0);
		this.LeftEar.addBox(-0.5F, -8.5F, -8.0F, 2, 4, 1, 0.0F);
		this.LeftEar.setRotationPoint(-2.5F, -4.5F, 3.5F);
		this.LeftEar.mirror = true;
		this.LeftEar.rotateAngleZ = 0.7853981F;
		this.LeftEar.rotateAngleX = -0.5235988F;
		this.Torso = new ModelRenderer(0, 7);
		this.Torso.addBox(-1.5F, -8.0F, -3.5F, 5, 4, 5, 2.4F);
		this.Torso.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.Torso.rotateAngleX = 1.047198F;
		this.Torso2 = new ModelRenderer(20, 7);
		this.Torso2.addBox(-1.5F, -8.0F, -3.5F, 5, 4, 5, 2.4F);
		this.Torso2.setRotationPoint(0.0F, 3.5F, 12.0F);
		this.Torso2.rotateAngleX = 0.6108652F;
		this.Abdomen = new ModelRenderer(0, 16);
		this.Abdomen.addBox(-1.0F, 2.5F, 6.0F, 4, 5, 4, 2.4F);
		this.Abdomen.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.Abdomen.rotateAngleX = 0.4363323F;
		this.RightArm = new ModelRenderer(48, 0);
		this.RightArm.addBox(-8.0F, 0.0F, 0.0F, 4, 16, 4, 0.0F);
		this.RightArm.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.LeftArm = new ModelRenderer(48, 0);
		this.LeftArm.addBox(6.0F, 0.0F, 0.0F, 4, 16, 4, 0.0F);
		this.LeftArm.setRotationPoint(0.0F, -6.0F, 0.0F);
		this.LeftArm.mirror = true;
		this.RightTigh = new ModelRenderer(32, 20);
		this.RightTigh.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4, 0.0F);
		this.RightTigh.setRotationPoint(-3.5F, 8.0F, 9.0F);
		this.RightTigh.rotateAngleX = -0.7853981F;
		this.LeftTigh = new ModelRenderer(32, 20);
		this.LeftTigh.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4, 0.0F);
		this.LeftTigh.setRotationPoint(1.5F, 8.0F, 9.0F);
		this.LeftTigh.mirror = true;
		this.LeftTigh.rotateAngleX = -0.7853981F;
		this.RightLeg = new ModelRenderer(48, 20);
		this.RightLeg.addBox(2.0F, 8.0F, 0.0F, 4, 8, 4, 0.0F);
		this.RightLeg.setRotationPoint(-5.5F, 6.0F, -3.0F);
		this.LeftLeg = new ModelRenderer(48, 20);
		this.LeftLeg.addBox(2.0F, 8.0F, 0.0F, 4, 8, 4, 0.0F);
		this.LeftLeg.setRotationPoint(-0.5F, 6.0F, -3.0F);
		this.LeftLeg.mirror = true;
		this.Tail = new ModelRenderer(9, 22);
		this.Tail.addBox(0.0F, -2.0F, 16.0F, 2, 2, 8, 1.0F);
		this.Tail.setRotationPoint(0.0F, 2.0F, 0.0F);
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6, boolean z7) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6, z7);
		this.Head.render(f6);
		this.RightEar.render(f6);
		this.LeftEar.render(f6);
		this.Torso.render(f6);
		this.Torso2.render(f6);
		this.Abdomen.render(f6);
		this.RightArm.render(f6);
		this.LeftArm.render(f6);
		this.RightTigh.render(f6);
		this.LeftTigh.render(f6);
		this.RightLeg.render(f6);
		this.LeftLeg.render(f6);
		this.Tail.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6, boolean z7) {
		this.Head.rotateAngleY = f4 / 57.29578F / 2.0F;
		this.Head.rotateAngleX = f5 / 57.29578F;
		this.RightEar.rotateAngleY = this.Head.rotateAngleX;
		this.RightEar.rotateAngleX = -0.5235988F - this.Head.rotateAngleY / 4.0F;
		this.RightEar.rotationPointX = 3.5F - this.Head.rotateAngleY * 2.0F;
		this.LeftEar.rotateAngleY = this.Head.rotateAngleX;
		this.LeftEar.rotateAngleX = -0.5235988F + this.Head.rotateAngleY / 4.0F;
		this.LeftEar.rotationPointX = -2.5F - this.Head.rotateAngleY * 2.0F;
		this.RightArm.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 2.0F * f2 * 0.5F;
		this.LeftArm.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 2.0F * f2 * 0.5F;
		this.RightArm.rotateAngleZ = 0.0F;
		this.LeftArm.rotateAngleZ = 0.0F;
		this.RightTigh.rotateAngleX = -0.7853981F + MathHelper.cos(f1 * 0.6662F) * 1.4F * f2 / 3.0F;
		this.LeftTigh.rotateAngleX = -0.7853981F + MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2 / 3.0F;
		this.RightLeg.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2 / 2.0F;
		this.RightLeg.rotationPointZ = 3.0F + MathHelper.cos(f1 * 0.6662F) * 1.4F * f2 / 10.0F;
		this.RightLeg.rotationPointY = 7.5F + MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
		this.LeftLeg.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2 / 2.0F;
		this.LeftLeg.rotationPointZ = 3.0F + MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2 / 10.0F;
		this.LeftLeg.rotationPointY = 7.5F + MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.RightLeg.rotateAngleY = 0.0F;
		this.LeftLeg.rotateAngleY = 0.0F;
		if(z7) {
			this.Tail.rotateAngleX = 0.261799F;
			this.Tail.rotationPointY = 10.0F;
			this.Tail.rotationPointZ = -2.0F;
			this.Abdomen.rotateAngleX = 1.308997F;
			this.Abdomen.rotationPointY = 13.0F;
			this.Abdomen.rotationPointZ = 0.0F;
			this.Torso.rotateAngleX = 1.919862F;
			this.Torso.rotationPointY = 4.0F;
			this.Torso.rotationPointZ = 1.0F;
			this.Torso2.rotateAngleX = 1.570796F;
			this.Torso2.rotationPointY = 5.0F;
			this.Torso2.rotationPointZ = 8.5F;
			this.RightArm.rotationPointY = 8.0F;
			this.RightArm.rotationPointZ = -6.0F;
			this.LeftArm.rotationPointY = 8.0F;
			this.LeftArm.rotationPointZ = -6.0F;
			this.Head.rotationPointY = 16.0F;
			this.Head.rotationPointZ = -7.0F;
			this.RightEar.rotationPointY = 12.5F;
			this.RightEar.rotationPointZ = -6.0F - this.Head.rotateAngleY * 2.0F;
			this.LeftEar.rotationPointY = 12.0F;
			this.LeftEar.rotationPointZ = -6.0F - this.Head.rotateAngleY * 2.0F;
		} else {
			this.Tail.rotateAngleX = -0.4363323F;
			this.Tail.rotationPointY = 1.0F;
			this.Tail.rotationPointZ = -1.0F;
			this.Abdomen.rotateAngleX = 0.4363323F;
			this.Abdomen.rotationPointY = 2.0F;
			this.Abdomen.rotationPointZ = 0.0F;
			this.Torso.rotateAngleX = 1.047198F;
			this.Torso.rotationPointY = -3.0F;
			this.Torso.rotationPointZ = 9.0F;
			this.Torso2.rotateAngleX = 0.6108652F;
			this.Torso2.rotationPointY = 3.5F;
			this.Torso2.rotationPointZ = 12.0F;
			this.RightArm.rotationPointY = -7.0F;
			this.RightArm.rotationPointZ = 0.0F;
			this.LeftArm.rotationPointY = -7.0F;
			this.LeftArm.rotationPointZ = 0.0F;
			this.Head.rotationPointY = -0.5F;
			this.Head.rotationPointZ = 1.5F;
			this.RightEar.rotationPointY = -4.0F;
			this.RightEar.rotationPointZ = 2.5F - this.Head.rotateAngleY * 2.0F;
			this.LeftEar.rotationPointY = -4.5F;
			this.LeftEar.rotationPointZ = 2.5F - this.Head.rotateAngleY * 2.0F;
		}

		this.RightArm.rotateAngleZ += MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F;
		this.LeftArm.rotateAngleZ -= MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F;
		this.RightArm.rotateAngleX += MathHelper.sin(f3 * 0.067F) * 0.05F;
		this.LeftArm.rotateAngleX -= MathHelper.sin(f3 * 0.067F) * 0.05F;
	}
}
