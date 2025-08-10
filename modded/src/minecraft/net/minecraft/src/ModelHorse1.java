package net.minecraft.src;

public class ModelHorse1 extends ModelBase {
	public ModelRenderer Head = new ModelRenderer(0, 0);
	public ModelRenderer Ear1;
	public ModelRenderer Ear2;
	public ModelRenderer Neck;
	public ModelRenderer RightWing;
	public ModelRenderer LeftWing;
	public ModelRenderer Unicorn;
	public ModelRenderer Bag1;
	public ModelRenderer Bag2;

	public ModelHorse1() {
		this.Head.addBox(-2.5F, 0.0F, -5.5F, 5, 5, 11, 0.0F);
		this.Head.setRotationPoint(0.0F, -5.5F, -13.8F);
		this.Head.rotateAngleX = 0.3490658F;
		this.Unicorn = new ModelRenderer(50, 0);
		this.Unicorn.addBox(-2.5F, 0.0F, -5.5F, 0, 8, 3, 0.0F);
		this.Unicorn.setRotationPoint(2.5F, -15.0F, -11.0F);
		this.Unicorn.rotateAngleX = 0.3490658F;
		this.Ear1 = new ModelRenderer(0, 0);
		this.Ear1.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 2, 0.0F);
		this.Ear1.setRotationPoint(1.8F, -8.0F, -11.0F);
		this.Ear1.rotateAngleX = 0.3490658F;
		this.Ear2 = new ModelRenderer(0, 0);
		this.Ear2.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 2, 0.0F);
		this.Ear2.setRotationPoint(-1.8F, -8.0F, -11.0F);
		this.Ear2.mirror = true;
		this.Ear2.rotateAngleX = 0.3490658F;
		this.Neck = new ModelRenderer(0, 10);
		this.Neck.addBox(-2.0F, 0.0F, -4.0F, 4, 14, 8, 0.0F);
		this.Neck.setRotationPoint(0.0F, -5.0F, -12.0F);
		this.Neck.rotateAngleX = 0.5235988F;
		this.RightWing = new ModelRenderer(34, 0);
		this.RightWing.addBox(0.0F, 0.0F, -5.0F, 1, 20, 12, 0.5F);
		this.RightWing.setRotationPoint(-6.6F, 2.5F, 0.0F);
		this.LeftWing = new ModelRenderer(34, 0);
		this.LeftWing.addBox(-1.0F, 0.0F, -5.0F, 1, 20, 12, 0.5F);
		this.LeftWing.setRotationPoint(6.6F, 2.5F, 0.0F);
		this.LeftWing.mirror = true;
		this.Bag1 = new ModelRenderer(22, 0);
		this.Bag1.addBox(0.0F, 0.0F, 0.0F, 8, 8, 3, -0.5F);
		this.Bag1.setRotationPoint(-7.5F, 3.0F, 10.0F);
		this.Bag1.rotateAngleY = 1.570796F;
		this.Bag2 = new ModelRenderer(22, 0);
		this.Bag2.addBox(0.0F, 0.0F, 0.0F, 8, 8, 3, -0.5F);
		this.Bag2.setRotationPoint(4.5F, 3.0F, 10.0F);
		this.Bag2.rotateAngleY = 1.570796F;
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.Head.render(f6);
		this.Ear1.render(f6);
		this.Ear2.render(f6);
		this.Neck.render(f6);
		this.Unicorn.render(f6);
		this.RightWing.render(f6);
		this.LeftWing.render(f6);
		this.Bag1.render(f6);
		this.Bag2.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.RightWing.rotateAngleZ = f3;
		this.LeftWing.rotateAngleZ = -f3;
		this.Bag1.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f3 / 10.0F;
		this.Bag2.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f3 / 10.0F;
	}
}
