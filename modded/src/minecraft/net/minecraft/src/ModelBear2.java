package net.minecraft.src;

public class ModelBear2 extends ModelQuadruped {
	ModelRenderer b;

	public ModelBear2() {
		super(12, 0.8F);
		this.head = new ModelRenderer(0, 0);
		this.head.addBox(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.b = new ModelRenderer(23, 0);
		this.b.addBox(-2.0F, 0.0F, -9.0F, 4, 4, 6, 0.0F);
		this.b.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.body = new ModelRenderer(32, 10);
		this.body.addBox(-4.0F, -8.0F, -5.0F, 8, 14, 8, 3.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.render(f1, f2, f3, f4, f5, f6);
		this.b.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.b.rotateAngleY = this.head.rotateAngleY;
		this.b.rotateAngleX = this.head.rotateAngleX;
	}
}
