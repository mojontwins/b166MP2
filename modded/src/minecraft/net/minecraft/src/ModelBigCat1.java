package net.minecraft.src;

public class ModelBigCat1 extends ModelQuadruped {
	public ModelBigCat1() {
		super(12, 0.0F);
		this.head = new ModelRenderer(20, 0);
		this.head.addBox(-7.0F, -8.0F, -2.0F, 14, 14, 8, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.body = new ModelRenderer(20, 0);
		this.body.addBox(-6.0F, -11.0F, -8.0F, 12, 10, 10, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}
}
