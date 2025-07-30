package net.minecraft.client.model;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;

public class ModelSnowMan extends ModelBase {
	public ModelRenderer field_40306_a;
	public ModelRenderer field_40304_b;
	public ModelRenderer field_40305_c;
	public ModelRenderer field_40302_d;
	public ModelRenderer field_40303_e;

	public ModelSnowMan() {
		float f1 = 4.0F;
		float f2 = 0.0F;
		this.field_40305_c = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
		this.field_40305_c.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f2 - 0.5F);
		this.field_40305_c.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
		this.field_40302_d = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
		this.field_40302_d.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, f2 - 0.5F);
		this.field_40302_d.setRotationPoint(0.0F, 0.0F + f1 + 9.0F - 7.0F, 0.0F);
		this.field_40303_e = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
		this.field_40303_e.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, f2 - 0.5F);
		this.field_40303_e.setRotationPoint(0.0F, 0.0F + f1 + 9.0F - 7.0F, 0.0F);
		this.field_40306_a = (new ModelRenderer(this, 0, 16)).setTextureSize(64, 64);
		this.field_40306_a.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, f2 - 0.5F);
		this.field_40306_a.setRotationPoint(0.0F, 0.0F + f1 + 9.0F, 0.0F);
		this.field_40304_b = (new ModelRenderer(this, 0, 36)).setTextureSize(64, 64);
		this.field_40304_b.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, f2 - 0.5F);
		this.field_40304_b.setRotationPoint(0.0F, 0.0F + f1 + 20.0F, 0.0F);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.field_40305_c.rotateAngleY = f4 / 57.295776F;
		this.field_40305_c.rotateAngleX = f5 / 57.295776F;
		this.field_40306_a.rotateAngleY = f4 / 57.295776F * 0.25F;
		float f7 = MathHelper.sin(this.field_40306_a.rotateAngleY);
		float f8 = MathHelper.cos(this.field_40306_a.rotateAngleY);
		this.field_40302_d.rotateAngleZ = 1.0F;
		this.field_40303_e.rotateAngleZ = -1.0F;
		this.field_40302_d.rotateAngleY = 0.0F + this.field_40306_a.rotateAngleY;
		this.field_40303_e.rotateAngleY = (float)Math.PI + this.field_40306_a.rotateAngleY;
		this.field_40302_d.rotationPointX = f8 * 5.0F;
		this.field_40302_d.rotationPointZ = -f7 * 5.0F;
		this.field_40303_e.rotationPointX = -f8 * 5.0F;
		this.field_40303_e.rotationPointZ = f7 * 5.0F;
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		this.field_40306_a.render(f7);
		this.field_40304_b.render(f7);
		this.field_40305_c.render(f7);
		this.field_40302_d.render(f7);
		this.field_40303_e.render(f7);
	}
}
