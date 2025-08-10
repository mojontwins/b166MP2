package net.minecraft.src;

public class ModelHorse2 extends ModelBase {
	public ModelRenderer Body = new ModelRenderer(0, 0);
	public ModelRenderer Tail;
	public ModelRenderer Leg1;
	public ModelRenderer Leg2;
	public ModelRenderer Leg3;
	public ModelRenderer Leg4;

	public ModelHorse2() {
		this.Body.addBox(-5.0F, 0.0F, -10.0F, 10, 10, 20, 0.0F);
		this.Body.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.Tail = new ModelRenderer(40, 0);
		this.Tail.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.Tail.setRotationPoint(0.0F, 2.0F, 8.0F);
		this.Tail.rotateAngleX = 0.5235988F;
		this.Leg1 = new ModelRenderer(0, 0);
		this.Leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.Leg1.setRotationPoint(3.0F, 12.0F, 8.0F);
		this.Leg2 = new ModelRenderer(0, 0);
		this.Leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.Leg2.setRotationPoint(-3.0F, 12.0F, 8.0F);
		this.Leg3 = new ModelRenderer(0, 0);
		this.Leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.Leg3.setRotationPoint(3.0F, 12.0F, -8.0F);
		this.Leg4 = new ModelRenderer(0, 0);
		this.Leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
		this.Leg4.setRotationPoint(-3.0F, 12.0F, -8.0F);
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Body.render(f6);
		this.Tail.render(f6);
		this.Leg1.render(f6);
		this.Leg2.render(f6);
		this.Leg3.render(f6);
		this.Leg4.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.Leg1.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
		this.Leg2.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.Leg3.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.Leg4.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
	}
}
