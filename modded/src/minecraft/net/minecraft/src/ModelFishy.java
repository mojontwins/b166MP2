package net.minecraft.src;

public class ModelFishy extends ModelBase {
	public ModelRenderer Body = new ModelRenderer(0, 0);
	public ModelRenderer UpperFin;
	public ModelRenderer LowerFin;
	public ModelRenderer RightFin;
	public ModelRenderer LeftFin;
	public ModelRenderer Tail;

	public ModelFishy() {
		this.Body.addBox(0.0F, 0.0F, 0.0F, 1, 5, 5, 0.0F);
		this.Body.setRotationPoint(0.0F, 19.0F, 0.0F);
		this.Body.rotateAngleX = 0.7853981F;
		this.Tail = new ModelRenderer(12, 0);
		this.Tail.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
		this.Tail.setRotationPoint(0.0F, 18.7F, 6.0F);
		this.Tail.rotateAngleX = 0.7853981F;
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Body.render(f6);
		this.Tail.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.Tail.rotateAngleY = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2;
	}
}
