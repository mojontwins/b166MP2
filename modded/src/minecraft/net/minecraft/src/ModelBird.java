package net.minecraft.src;

public class ModelBird extends ModelBase {
	public ModelRenderer head;
	public ModelRenderer body;
	public ModelRenderer leftleg;
	public ModelRenderer rightleg;
	public ModelRenderer rwing;
	public ModelRenderer lwing;
	public ModelRenderer beak;
	public ModelRenderer tail;

	public ModelBird() {
		byte b1 = 16;
		this.head = new ModelRenderer(0, 0);
		this.head.addBox(-1.5F, -3.0F, -2.0F, 3, 3, 3, 0.0F);
		this.head.setRotationPoint(0.0F, (float)(-1 + b1), -4.0F);
		this.beak = new ModelRenderer(14, 0);
		this.beak.addBox(-0.5F, -1.5F, -3.0F, 1, 1, 2, 0.0F);
		this.beak.setRotationPoint(0.0F, (float)(-1 + b1), -4.0F);
		this.body = new ModelRenderer(0, 9);
		this.body.addBox(-2.0F, -4.0F, -3.0F, 4, 8, 4, 0.0F);
		this.body.setRotationPoint(0.0F, (float)(0 + b1), 0.0F);
		this.body.rotateAngleX = 1.047198F;
		this.leftleg = new ModelRenderer(26, 0);
		this.leftleg.addBox(-1.0F, 0.0F, -4.0F, 3, 4, 3);
		this.leftleg.setRotationPoint(-2.0F, (float)(3 + b1), 1.0F);
		this.rightleg = new ModelRenderer(26, 0);
		this.rightleg.addBox(-1.0F, 0.0F, -4.0F, 3, 4, 3);
		this.rightleg.setRotationPoint(1.0F, (float)(3 + b1), 1.0F);
		this.rwing = new ModelRenderer(24, 13);
		this.rwing.addBox(-1.0F, 0.0F, -3.0F, 1, 5, 5);
		this.rwing.setRotationPoint(-2.0F, (float)(-2 + b1), 0.0F);
		this.lwing = new ModelRenderer(24, 13);
		this.lwing.addBox(0.0F, 0.0F, -3.0F, 1, 5, 5);
		this.lwing.setRotationPoint(2.0F, (float)(-2 + b1), 0.0F);
		this.tail = new ModelRenderer(0, 23);
		this.tail.addBox(-6.0F, 5.0F, 2.0F, 4, 1, 4, 0.0F);
		this.tail.setRotationPoint(4.0F, (float)(-3 + b1), 0.0F);
		this.tail.rotateAngleX = 0.261799F;
	}

	public void render(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.head.render(f6);
		this.beak.render(f6);
		this.body.render(f6);
		this.leftleg.render(f6);
		this.rightleg.render(f6);
		this.rwing.render(f6);
		this.lwing.render(f6);
		this.tail.render(f6);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.head.rotateAngleX = -(f5 / 2.0F / 57.29578F);
		this.head.rotateAngleY = f4 / 2.0F / 57.29578F;
		this.beak.rotateAngleY = this.head.rotateAngleY;
		this.leftleg.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * f2;
		this.rightleg.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 3.141593F) * f2;
		this.rwing.rotateAngleZ = f3;
		this.lwing.rotateAngleZ = -f3;
	}
}
