package net.minecraft.src;

public class ModelDeer extends ModelBase {
	public ModelRenderer Body;
	public ModelRenderer Neck;
	public ModelRenderer Head = new ModelRenderer(0, 0);
	public ModelRenderer Leg1;
	public ModelRenderer Leg2;
	public ModelRenderer Leg3;
	public ModelRenderer Leg4;
	public ModelRenderer Tail;
	public ModelRenderer LEar;
	public ModelRenderer REar;
	public ModelRenderer LeftAntler;
	public ModelRenderer RightAntler;

	public ModelDeer() {
		this.Head.addBox(-1.5F, -6.0F, -9.5F, 3, 3, 6, 0.0F);
		this.Head.setRotationPoint(1.0F, 11.5F, -4.5F);
		this.Neck = new ModelRenderer(0, 9);
		this.Neck.addBox(-2.0F, -2.0F, -6.0F, 4, 4, 6, 0.0F);
		this.Neck.setRotationPoint(1.0F, 11.5F, -4.5F);
		this.Neck.rotateAngleX = -0.7853981F;
		this.LEar = new ModelRenderer(0, 0);
		this.LEar.addBox(-4.0F, -7.5F, -5.0F, 2, 3, 1, 0.0F);
		this.LEar.setRotationPoint(1.0F, 11.5F, -4.5F);
		this.LEar.rotateAngleZ = 0.7853981F;
		this.REar = new ModelRenderer(0, 0);
		this.REar.addBox(2.0F, -7.5F, -5.0F, 2, 3, 1, 0.0F);
		this.REar.setRotationPoint(1.0F, 11.5F, -4.5F);
		this.REar.rotateAngleZ = -0.7853981F;
		this.LeftAntler = new ModelRenderer(54, 0);
		this.LeftAntler.addBox(0.0F, -14.0F, -7.0F, 1, 8, 4, 0.0F);
		this.LeftAntler.setRotationPoint(1.0F, 11.5F, -4.5F);
		this.LeftAntler.rotateAngleZ = 0.2094395F;
		this.RightAntler = new ModelRenderer(54, 0);
		this.RightAntler.addBox(0.0F, -14.0F, -7.0F, 1, 8, 4, 0.0F);
		this.RightAntler.setRotationPoint(1.0F, 11.5F, -4.5F);
		this.RightAntler.rotateAngleZ = -0.2094395F;
		this.Body = new ModelRenderer(24, 12);
		this.Body.addBox(-2.0F, -3.0F, -6.0F, 6, 6, 14, 0.0F);
		this.Body.setRotationPoint(0.0F, 13.0F, 0.0F);
		this.Leg1 = new ModelRenderer(9, 20);
		this.Leg1.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.Leg1.setRotationPoint(3.0F, 16.0F, -4.0F);
		this.Leg2 = new ModelRenderer(0, 20);
		this.Leg2.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.Leg2.setRotationPoint(-1.0F, 16.0F, -4.0F);
		this.Leg3 = new ModelRenderer(9, 20);
		this.Leg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.Leg3.setRotationPoint(3.0F, 16.0F, 6.0F);
		this.Leg4 = new ModelRenderer(0, 20);
		this.Leg4.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.Leg4.setRotationPoint(-1.0F, 16.0F, 6.0F);
		this.Tail = new ModelRenderer(50, 20);
		this.Tail.addBox(-1.5F, -1.0F, 0.0F, 3, 2, 4, 0.0F);
		this.Tail.setRotationPoint(1.0F, 11.0F, 7.0F);
		this.Tail.rotateAngleX = 0.7854F;
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Body.render(f6);
		this.Neck.render(f6);
		this.Head.render(f6);
		this.Leg1.render(f6);
		this.Leg2.render(f6);
		this.Leg3.render(f6);
		this.Leg4.render(f6);
		this.Tail.render(f6);
		this.LEar.render(f6);
		this.REar.render(f6);
		this.LeftAntler.render(f6);
		this.RightAntler.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.Leg1.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
		this.Leg2.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.Leg3.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * 1.4F * f2;
		this.Leg4.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
	}
}
