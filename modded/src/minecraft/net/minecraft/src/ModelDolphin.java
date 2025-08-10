package net.minecraft.src;

public class ModelDolphin extends ModelBase {
	public ModelRenderer UHead;
	public ModelRenderer DHead;
	public ModelRenderer RTail;
	public ModelRenderer LTail;
	public ModelRenderer PTail;
	public ModelRenderer Body = new ModelRenderer(4, 6);
	public ModelRenderer UpperFin;
	public ModelRenderer RTailFin;
	public ModelRenderer LTailFin;
	public ModelRenderer LowerFin;
	public ModelRenderer RightFin;
	public ModelRenderer LeftFin;

	public ModelDolphin() {
		this.Body.addBox(0.0F, 0.0F, 0.0F, 6, 8, 18, 0.0F);
		this.Body.setRotationPoint(-4.0F, 17.0F, -10.0F);
		this.UHead = new ModelRenderer(0, 0);
		this.UHead.addBox(0.0F, 0.0F, 0.0F, 5, 7, 8, 0.0F);
		this.UHead.setRotationPoint(-3.5F, 18.0F, -16.5F);
		this.DHead = new ModelRenderer(50, 0);
		this.DHead.addBox(0.0F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
		this.DHead.setRotationPoint(-2.5F, 21.5F, -20.5F);
		this.PTail = new ModelRenderer(34, 9);
		this.PTail.addBox(0.0F, 0.0F, 0.0F, 5, 5, 10, 0.0F);
		this.PTail.setRotationPoint(-3.5F, 19.0F, 8.0F);
		this.UpperFin = new ModelRenderer(4, 12);
		this.UpperFin.addBox(0.0F, 0.0F, 0.0F, 1, 4, 8, 0.0F);
		this.UpperFin.setRotationPoint(-1.5F, 18.0F, -4.0F);
		this.UpperFin.rotateAngleX = 0.7853981F;
		this.LTailFin = new ModelRenderer(34, 0);
		this.LTailFin.addBox(0.0F, 0.0F, 0.0F, 4, 1, 8, 0.3F);
		this.LTailFin.setRotationPoint(-2.0F, 21.5F, 18.0F);
		this.LTailFin.rotateAngleY = 0.7853981F;
		this.RTailFin = new ModelRenderer(34, 0);
		this.RTailFin.addBox(0.0F, 0.0F, 0.0F, 4, 1, 8, 0.3F);
		this.RTailFin.setRotationPoint(-3.0F, 21.5F, 15.0F);
		this.RTailFin.rotateAngleY = -0.7853981F;
		this.LeftFin = new ModelRenderer(14, 0);
		this.LeftFin.addBox(0.0F, 0.0F, 0.0F, 8, 1, 4, 0.0F);
		this.LeftFin.setRotationPoint(2.0F, 24.0F, -7.0F);
		this.LeftFin.rotateAngleY = -0.5235988F;
		this.LeftFin.rotateAngleZ = 0.5235988F;
		this.RightFin = new ModelRenderer(14, 0);
		this.RightFin.addBox(0.0F, 0.0F, 0.0F, 8, 1, 4, 0.0F);
		this.RightFin.setRotationPoint(-10.0F, 27.5F, -3.0F);
		this.RightFin.rotateAngleY = 0.5235988F;
		this.RightFin.rotateAngleZ = -0.5235988F;
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Body.render(f6);
		this.PTail.render(f6);
		this.UHead.render(f6);
		this.DHead.render(f6);
		this.UpperFin.render(f6);
		this.LTailFin.render(f6);
		this.RTailFin.render(f6);
		this.LeftFin.render(f6);
		this.RightFin.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.RTailFin.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * f2;
		this.LTailFin.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * f2;
	}
}
