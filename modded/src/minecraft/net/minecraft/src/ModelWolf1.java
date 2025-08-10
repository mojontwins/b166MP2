package net.minecraft.src;

public class ModelWolf1 extends ModelQuadruped {
	public ModelWolf1() {
		super(10, 0.0F);
		this.head = new ModelRenderer(12, 0);
		this.head.addBox(-5.0F, -5.0F, -2.0F, 10, 14, 3, 0.0F);
		this.head.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.body = new ModelRenderer(24, 16);
		this.body.addBox(-6.0F, -11.0F, -10.0F, 12, 8, 8, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}
}
