package net.minecraft.client.model;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;

public class ModelVillager extends ModelBase {
	public ModelRenderer field_40340_a;
	public ModelRenderer field_40338_b;
	public ModelRenderer field_40339_c;
	public ModelRenderer field_40336_d;
	public ModelRenderer field_40337_e;

	public ModelVillager(float f1) {
		this(f1, 0.0F);
	}

	public ModelVillager(float f1, float f2) {
		byte b3 = 64;
		byte b4 = 64;
		this.field_40340_a = (new ModelRenderer(this)).setTextureSize(b3, b4);
		this.field_40340_a.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
		this.field_40340_a.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f1);
		this.field_40340_a.setTextureOffset(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, f1);
		this.field_40338_b = (new ModelRenderer(this)).setTextureSize(b3, b4);
		this.field_40338_b.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
		this.field_40338_b.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, f1);
		this.field_40338_b.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, f1 + 0.5F);
		this.field_40339_c = (new ModelRenderer(this)).setTextureSize(b3, b4);
		this.field_40339_c.setRotationPoint(0.0F, 0.0F + f2 + 2.0F, 0.0F);
		this.field_40339_c.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, f1);
		this.field_40339_c.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, f1);
		this.field_40339_c.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, f1);
		this.field_40336_d = (new ModelRenderer(this, 0, 22)).setTextureSize(b3, b4);
		this.field_40336_d.setRotationPoint(-2.0F, 12.0F + f2, 0.0F);
		this.field_40336_d.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f1);
		this.field_40337_e = (new ModelRenderer(this, 0, 22)).setTextureSize(b3, b4);
		this.field_40337_e.mirror = true;
		this.field_40337_e.setRotationPoint(2.0F, 12.0F + f2, 0.0F);
		this.field_40337_e.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f1);
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		this.field_40340_a.render(f7);
		this.field_40338_b.render(f7);
		this.field_40336_d.render(f7);
		this.field_40337_e.render(f7);
		this.field_40339_c.render(f7);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.field_40340_a.rotateAngleY = f4 / 57.295776F;
		this.field_40340_a.rotateAngleX = f5 / 57.295776F;
		this.field_40339_c.rotationPointY = 3.0F;
		this.field_40339_c.rotationPointZ = -1.0F;
		this.field_40339_c.rotateAngleX = -0.75F;
		this.field_40336_d.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.4F * f2 * 0.5F;
		this.field_40337_e.rotateAngleX = MathHelper.cos(f1 * 0.6662F + (float)Math.PI) * 1.4F * f2 * 0.5F;
		this.field_40336_d.rotateAngleY = 0.0F;
		this.field_40337_e.rotateAngleY = 0.0F;
	}
}
