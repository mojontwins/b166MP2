package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;

public class ModelSquid extends ModelBase {
	ModelRenderer squidBody;
	ModelRenderer[] squidTentacles = new ModelRenderer[8];

	public ModelSquid() {
		byte b1 = -16;
		this.squidBody = new ModelRenderer(this, 0, 0);
		this.squidBody.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
		this.squidBody.rotationPointY += (float)(24 + b1);

		for(int i2 = 0; i2 < this.squidTentacles.length; ++i2) {
			this.squidTentacles[i2] = new ModelRenderer(this, 48, 0);
			double d3 = (double)i2 * Math.PI * 2.0D / (double)this.squidTentacles.length;
			float f5 = (float)Math.cos(d3) * 5.0F;
			float f6 = (float)Math.sin(d3) * 5.0F;
			this.squidTentacles[i2].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
			this.squidTentacles[i2].rotationPointX = f5;
			this.squidTentacles[i2].rotationPointZ = f6;
			this.squidTentacles[i2].rotationPointY = (float)(31 + b1);
			d3 = (double)i2 * Math.PI * -2.0D / (double)this.squidTentacles.length + Math.PI / 2D;
			this.squidTentacles[i2].rotateAngleY = (float)d3;
		}

	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		ModelRenderer[] modelRenderer7 = this.squidTentacles;
		int i8 = modelRenderer7.length;

		for(int i9 = 0; i9 < i8; ++i9) {
			ModelRenderer modelRenderer10 = modelRenderer7[i9];
			modelRenderer10.rotateAngleX = f3;
		}

	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		this.squidBody.render(f7);

		for(int i8 = 0; i8 < this.squidTentacles.length; ++i8) {
			this.squidTentacles[i8].render(f7);
		}

	}
}
