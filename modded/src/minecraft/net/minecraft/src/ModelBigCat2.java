package net.minecraft.src;

public class ModelBigCat2 extends ModelQuadruped {
	ModelRenderer snout;
	ModelRenderer Tail;
	ModelRenderer ears = new ModelRenderer(16, 25);

	public ModelBigCat2() {
		super(12, 0.0F);
		this.ears.addBox(-4.0F, -7.0F, -3.0F, 8, 4, 1, 0.0F);
		this.ears.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.head = new ModelRenderer(0, 0);
		this.head.addBox(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.snout = new ModelRenderer(14, 14);
		this.snout.addBox(-2.0F, 0.0F, -9.0F, 4, 4, 6, 0.0F);
		this.snout.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.body = new ModelRenderer(28, 0);
		this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 18, 8, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
		this.Tail = new ModelRenderer(26, 15);
		this.Tail.addBox(-5.0F, -5.0F, -2.0F, 3, 3, 14, 0.0F);
		this.Tail.setRotationPoint(3.5F, 9.3F, 9.0F);
		this.Tail.rotateAngleX = -0.5235988F;
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.render(f1, f2, f3, f4, f5, f6);
		this.snout.render(f6);
		this.Tail.render(f6);
		this.ears.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.snout.rotateAngleY = this.head.rotateAngleY;
		this.snout.rotateAngleX = this.head.rotateAngleX;
		this.ears.rotateAngleY = this.head.rotateAngleY;
		this.ears.rotateAngleX = this.head.rotateAngleX;
		this.Tail.rotateAngleY = MathHelper.cos(f1 * 0.6662F) * 0.7F * f2;
	}
}
