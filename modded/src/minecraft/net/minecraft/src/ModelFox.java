package net.minecraft.src;

public class ModelFox extends ModelBase {
	public ModelRenderer Body;
	public ModelRenderer Leg1;
	public ModelRenderer Leg2;
	public ModelRenderer Leg3;
	public ModelRenderer Leg4;
	public ModelRenderer Snout;
	public ModelRenderer Head;
	public ModelRenderer Tail;
	public ModelRenderer Ears;

	public ModelFox() {
		byte b1 = 8;
		this.Body = new ModelRenderer(0, 0);
		this.Body.addBox(0.0F, 0.0F, 0.0F, 6, 6, 12, 0.0F);
		this.Body.setRotationPoint(-4.0F, 10.0F, -6.0F);
		this.Head = new ModelRenderer(0, 20);
		this.Head.addBox(-4.0F, -4.0F, -6.0F, 6, 6, 4, 0.0F);
		this.Head.setRotationPoint(0.0F, 12.0F, -4.0F);
		this.Snout = new ModelRenderer(20, 20);
		this.Snout.addBox(-2.0F, 1.0F, -10.0F, 2, 2, 4, 0.0F);
		this.Snout.setRotationPoint(0.0F, 11.0F, -3.5F);
		this.Ears = new ModelRenderer(50, 20);
		this.Ears.addBox(-4.0F, -4.0F, -6.0F, 6, 4, 1, 0.0F);
		this.Ears.setRotationPoint(0.0F, 9.0F, -2.0F);
		this.Tail = new ModelRenderer(32, 20);
		this.Tail.addBox(-5.0F, -5.0F, -2.0F, 3, 3, 8, 0.0F);
		this.Tail.setRotationPoint(2.5F, 15.0F, 5.0F);
		this.Tail.rotateAngleX = -0.5235988F;
		this.Leg1 = new ModelRenderer(0, 0);
		this.Leg1.addBox(-2.0F, 0.0F, -2.0F, 3, b1, 3, 0.0F);
		this.Leg1.setRotationPoint(-2.0F, (float)(24 - b1), 5.0F);
		this.Leg2 = new ModelRenderer(0, 0);
		this.Leg2.addBox(-2.0F, 0.0F, -2.0F, 3, b1, 3, 0.0F);
		this.Leg2.setRotationPoint(1.0F, (float)(24 - b1), 5.0F);
		this.Leg3 = new ModelRenderer(0, 0);
		this.Leg3.addBox(-2.0F, 0.0F, -2.0F, 3, b1, 3, 0.0F);
		this.Leg3.setRotationPoint(-2.0F, (float)(24 - b1), -4.0F);
		this.Leg4 = new ModelRenderer(0, 0);
		this.Leg4.addBox(-2.0F, 0.0F, -2.0F, 3, b1, 3, 0.0F);
		this.Leg4.setRotationPoint(1.0F, (float)(24 - b1), -4.0F);
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Body.render(f6);
		this.Leg1.render(f6);
		this.Leg2.render(f6);
		this.Leg3.render(f6);
		this.Leg4.render(f6);
		this.Head.render(f6);
		this.Snout.render(f6);
		this.Tail.render(f6);
		this.Ears.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.Head.rotateAngleY = f4 / 57.29578F;
		this.Head.rotateAngleX = f5 / 57.29578F;
		this.Snout.rotateAngleY = f4 / 57.29578F;
		this.Snout.rotateAngleX = f5 / 57.29578F;
		this.Snout.rotationPointX = 0.0F + f4 / 57.29578F * 0.8F;
		this.Ears.rotateAngleY = f4 / 57.29578F;
		this.Ears.rotateAngleX = f5 / 57.29578F;
		this.Ears.rotationPointX = 0.0F + f4 / 57.29578F * 2.5F;
		this.Leg1.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
		this.Leg2.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.Leg3.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.Leg4.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
	}
}
