package net.minecraft.src;

public class ModelWolf2 extends ModelQuadruped {
	ModelRenderer b;

	public ModelWolf2() {
		super(10, 0.0F);
		this.head = new ModelRenderer(0, 0);
		this.head.addBox(-4.0F, -2.0F, -6.0F, 8, 8, 6, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.b = new ModelRenderer(8, 15);
		this.b.addBox(-2.0F, 2.0F, -11.0F, 4, 4, 6, 0.0F);
		this.b.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.body = new ModelRenderer(28, 6);
		this.body.addBox(-5.0F, -8.0F, -9.0F, 10, 16, 6, 0.0F);
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
