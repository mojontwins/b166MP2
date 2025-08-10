package net.minecraft.src;

public class ModelShark extends ModelBase {
	public ModelRenderer LHead;
	public ModelRenderer RHead;
	public ModelRenderer UHead;
	public ModelRenderer DHead;
	public ModelRenderer RTail;
	public ModelRenderer LTail;
	public ModelRenderer PTail;
	public ModelRenderer Body = new ModelRenderer(6, 6);
	public ModelRenderer UpperFin;
	public ModelRenderer UpperTailFin;
	public ModelRenderer LowerTailFin;
	public ModelRenderer RightFin;
	public ModelRenderer LeftFin;

	public ModelShark() {
		this.Body.addBox(0.0F, 0.0F, 0.0F, 6, 8, 18, 0.0F);
		this.Body.setRotationPoint(-4.0F, 17.0F, -10.0F);
		this.UHead = new ModelRenderer(0, 0);
		this.UHead.addBox(0.0F, 0.0F, 0.0F, 5, 2, 8, 0.0F);
		this.UHead.setRotationPoint(-3.5F, 21.0F, -16.5F);
		this.UHead.rotateAngleX = 0.5235988F;
		this.DHead = new ModelRenderer(44, 0);
		this.DHead.addBox(0.0F, 0.0F, 0.0F, 5, 2, 5, 0.0F);
		this.DHead.setRotationPoint(-3.5F, 21.5F, -13.5F);
		this.DHead.rotateAngleX = -0.261799F;
		this.RHead = new ModelRenderer(0, 3);
		this.RHead.addBox(0.0F, 0.0F, 0.0F, 1, 6, 6, 0.0F);
		this.RHead.setRotationPoint(-3.45F, 21.3F, -13.85F);
		this.RHead.rotateAngleX = 0.7853981F;
		this.LHead = new ModelRenderer(0, 3);
		this.LHead.addBox(0.0F, 0.0F, 0.0F, 1, 6, 6, 0.0F);
		this.LHead.setRotationPoint(0.45F, 21.3F, -13.8F);
		this.LHead.rotateAngleX = 0.7853981F;
		this.PTail = new ModelRenderer(36, 8);
		this.PTail.addBox(0.0F, 0.0F, 0.0F, 4, 6, 10, 0.0F);
		this.PTail.setRotationPoint(-3.0F, 18.0F, 8.0F);
		this.UpperFin = new ModelRenderer(6, 12);
		this.UpperFin.addBox(0.0F, 0.0F, 0.0F, 1, 4, 8, 0.0F);
		this.UpperFin.setRotationPoint(-1.5F, 17.0F, -1.0F);
		this.UpperFin.rotateAngleX = 0.7853981F;
		this.UpperTailFin = new ModelRenderer(6, 12);
		this.UpperTailFin.addBox(0.0F, 0.0F, 0.0F, 1, 4, 8, 0.0F);
		this.UpperTailFin.setRotationPoint(-1.5F, 18.0F, 16.0F);
		this.UpperTailFin.rotateAngleX = 0.5235988F;
		this.LowerTailFin = new ModelRenderer(8, 14);
		this.LowerTailFin.addBox(0.0F, 0.0F, 0.0F, 1, 4, 6, 0.0F);
		this.LowerTailFin.setRotationPoint(-1.5F, 21.0F, 18.0F);
		this.LowerTailFin.rotateAngleX = -0.7853981F;
		this.LeftFin = new ModelRenderer(18, 0);
		this.LeftFin.addBox(0.0F, 0.0F, 0.0F, 8, 1, 4, 0.0F);
		this.LeftFin.setRotationPoint(2.0F, 24.0F, -5.0F);
		this.LeftFin.rotateAngleY = -0.5235988F;
		this.LeftFin.rotateAngleZ = 0.5235988F;
		this.RightFin = new ModelRenderer(18, 0);
		this.RightFin.addBox(0.0F, 0.0F, 0.0F, 8, 1, 4, 0.0F);
		this.RightFin.setRotationPoint(-10.0F, 27.5F, -1.0F);
		this.RightFin.rotateAngleY = 0.5235988F;
		this.RightFin.rotateAngleZ = -0.5235988F;
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Body.render(f6);
		this.PTail.render(f6);
		this.UHead.render(f6);
		this.DHead.render(f6);
		this.RHead.render(f6);
		this.LHead.render(f6);
		this.UpperFin.render(f6);
		this.UpperTailFin.render(f6);
		this.LowerTailFin.render(f6);
		this.LeftFin.render(f6);
		this.RightFin.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.UpperTailFin.rotateAngleY = MathHelper.cos(f1 * 0.6662F) * f2;
		this.LowerTailFin.rotateAngleY = MathHelper.cos(f1 * 0.6662F) * f2;
	}
}
