package net.minecraft.src;

public class ModelBear1 extends ModelQuadruped {
	public ModelBear1() {
		super(12, 0.0F);
		this.head = new ModelRenderer(0, 0);
		this.head.addBox(-5.0F, -8.0F, -2.0F, 10, 4, 1, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.body = new ModelRenderer(20, 0);
		this.body.addBox(-2.0F, 9.0F, -4.0F, 4, 2, 4, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}
}
