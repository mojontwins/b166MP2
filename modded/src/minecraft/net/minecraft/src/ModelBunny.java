package net.minecraft.src;

public class ModelBunny extends ModelBase {
	public ModelRenderer a;
	public ModelRenderer b;
	public ModelRenderer b2;
	public ModelRenderer e1;
	public ModelRenderer e2;
	public ModelRenderer l1;
	public ModelRenderer n2;
	public ModelRenderer m;
	public ModelRenderer g2;
	public ModelRenderer h;
	public ModelRenderer r2;

	public ModelBunny() {
		byte b1 = 16;
		this.a = new ModelRenderer(0, 0);
		this.a.addBox(-2.0F, -1.0F, -4.0F, 4, 4, 6, 0.0F);
		this.a.setRotationPoint(0.0F, (float)(-1 + b1), -4.0F);
		this.m = new ModelRenderer(14, 0);
		this.m.addBox(-2.0F, -5.0F, -3.0F, 1, 4, 2, 0.0F);
		this.m.setRotationPoint(0.0F, (float)(-1 + b1), -4.0F);
		this.g2 = new ModelRenderer(14, 0);
		this.g2.addBox(1.0F, -5.0F, -3.0F, 1, 4, 2, 0.0F);
		this.g2.setRotationPoint(0.0F, (float)(-1 + b1), -4.0F);
		this.h = new ModelRenderer(20, 0);
		this.h.addBox(-4.0F, 0.0F, -3.0F, 2, 3, 2, 0.0F);
		this.h.setRotationPoint(0.0F, (float)(-1 + b1), -4.0F);
		this.r2 = new ModelRenderer(20, 0);
		this.r2.addBox(2.0F, 0.0F, -3.0F, 2, 3, 2, 0.0F);
		this.r2.setRotationPoint(0.0F, (float)(-1 + b1), -4.0F);
		this.b = new ModelRenderer(0, 10);
		this.b.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
		this.b.setRotationPoint(0.0F, (float)(0 + b1), 0.0F);
		this.b2 = new ModelRenderer(0, 24);
		this.b2.addBox(-2.0F, 4.0F, -2.0F, 4, 3, 4, 0.0F);
		this.b2.setRotationPoint(0.0F, (float)(0 + b1), 0.0F);
		this.e1 = new ModelRenderer(24, 16);
		this.e1.addBox(-2.0F, 0.0F, -1.0F, 2, 2, 2);
		this.e1.setRotationPoint(3.0F, (float)(3 + b1), -3.0F);
		this.e2 = new ModelRenderer(24, 16);
		this.e2.addBox(0.0F, 0.0F, -1.0F, 2, 2, 2);
		this.e2.setRotationPoint(-3.0F, (float)(3 + b1), -3.0F);
		this.l1 = new ModelRenderer(16, 24);
		this.l1.addBox(-2.0F, 0.0F, -4.0F, 2, 2, 4);
		this.l1.setRotationPoint(3.0F, (float)(3 + b1), 4.0F);
		this.n2 = new ModelRenderer(16, 24);
		this.n2.addBox(0.0F, 0.0F, -4.0F, 2, 2, 4);
		this.n2.setRotationPoint(-3.0F, (float)(3 + b1), 4.0F);
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.a.render(f6);
		this.m.render(f6);
		this.g2.render(f6);
		this.h.render(f6);
		this.r2.render(f6);
		this.b.render(f6);
		this.b2.render(f6);
		this.e1.render(f6);
		this.e2.render(f6);
		this.l1.render(f6);
		this.n2.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.a.rotateAngleX = -(f5 / 57.29578F);
		this.a.rotateAngleY = f4 / 57.29578F;
		this.m.rotateAngleX = this.a.rotateAngleX;
		this.m.rotateAngleY = this.a.rotateAngleY;
		this.g2.rotateAngleX = this.a.rotateAngleX;
		this.g2.rotateAngleY = this.a.rotateAngleY;
		this.h.rotateAngleX = this.a.rotateAngleX;
		this.h.rotateAngleY = this.a.rotateAngleY;
		this.r2.rotateAngleX = this.a.rotateAngleX;
		this.r2.rotateAngleY = this.a.rotateAngleY;
		this.b.rotateAngleX = 1.570796F;
		this.b2.rotateAngleX = 1.570796F;
		this.e1.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.0F * f2;
		this.l1.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.2F * f2;
		this.e2.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.0F * f2;
		this.n2.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.2F * f2;
	}
}
