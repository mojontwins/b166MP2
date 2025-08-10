package net.minecraft.src;

public class ModelSharkEgg extends ModelBase {
	public ModelRenderer Egg = new ModelRenderer(0, 0);

	public ModelSharkEgg() {
		this.Egg.addBox(0.0F, 0.0F, 0.0F, 3, 1, 6, 0.0F);
		this.Egg.setRotationPoint(-2.0F, 23.0F, -2.0F);
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Egg.render(f6);
	}
}
