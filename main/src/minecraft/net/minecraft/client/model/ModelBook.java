package net.minecraft.client.model;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;

public class ModelBook extends ModelBase {
	public ModelRenderer coverRight = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	public ModelRenderer coverLeft = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
	public ModelRenderer pagesRight = (new ModelRenderer(this)).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
	public ModelRenderer pagesLeft = (new ModelRenderer(this)).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
	public ModelRenderer flippingPageRight = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
	public ModelRenderer flippingPageLeft = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
	public ModelRenderer bookSpine = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public ModelBook() {
		this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.bookSpine.rotateAngleY = (float)Math.PI / 2F;
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		this.coverRight.render(f7);
		this.coverLeft.render(f7);
		this.bookSpine.render(f7);
		this.pagesRight.render(f7);
		this.pagesLeft.render(f7);
		this.flippingPageRight.render(f7);
		this.flippingPageLeft.render(f7);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		float f7 = (MathHelper.sin(f1 * 0.02F) * 0.1F + 1.25F) * f4;
		this.coverRight.rotateAngleY = (float)Math.PI + f7;
		this.coverLeft.rotateAngleY = -f7;
		this.pagesRight.rotateAngleY = f7;
		this.pagesLeft.rotateAngleY = -f7;
		this.flippingPageRight.rotateAngleY = f7 - f7 * 2.0F * f2;
		this.flippingPageLeft.rotateAngleY = f7 - f7 * 2.0F * f3;
		this.pagesRight.rotationPointX = MathHelper.sin(f7);
		this.pagesLeft.rotationPointX = MathHelper.sin(f7);
		this.flippingPageRight.rotationPointX = MathHelper.sin(f7);
		this.flippingPageLeft.rotationPointX = MathHelper.sin(f7);
	}
}
