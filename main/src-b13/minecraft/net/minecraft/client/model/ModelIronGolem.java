package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityIronGolem;

public class ModelIronGolem extends ModelBase {
	public ModelRenderer field_48234_a;
	public ModelRenderer field_48232_b;
	public ModelRenderer field_48233_c;
	public ModelRenderer field_48230_d;
	public ModelRenderer field_48231_e;
	public ModelRenderer field_48229_f;

	public ModelIronGolem() {
		this(0.0F);
	}

	public ModelIronGolem(float f1) {
		this(f1, -7.0F);
	}

	public ModelIronGolem(float f1, float f2) {
		short s3 = 128;
		short s4 = 128;
		this.field_48234_a = (new ModelRenderer(this)).setTextureSize(s3, s4);
		this.field_48234_a.setRotationPoint(0.0F, 0.0F + f2, -2.0F);
		this.field_48234_a.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, f1);
		this.field_48234_a.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, f1);
		this.field_48232_b = (new ModelRenderer(this)).setTextureSize(s3, s4);
		this.field_48232_b.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
		this.field_48232_b.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, f1);
		this.field_48232_b.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, f1 + 0.5F);
		this.field_48233_c = (new ModelRenderer(this)).setTextureSize(s3, s4);
		this.field_48233_c.setRotationPoint(0.0F, -7.0F, 0.0F);
		this.field_48233_c.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, f1);
		this.field_48230_d = (new ModelRenderer(this)).setTextureSize(s3, s4);
		this.field_48230_d.setRotationPoint(0.0F, -7.0F, 0.0F);
		this.field_48230_d.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, f1);
		this.field_48231_e = (new ModelRenderer(this, 0, 22)).setTextureSize(s3, s4);
		this.field_48231_e.setRotationPoint(-4.0F, 18.0F + f2, 0.0F);
		this.field_48231_e.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f1);
		this.field_48229_f = (new ModelRenderer(this, 0, 22)).setTextureSize(s3, s4);
		this.field_48229_f.mirror = true;
		this.field_48229_f.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + f2, 0.0F);
		this.field_48229_f.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f1);
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		this.field_48234_a.render(f7);
		this.field_48232_b.render(f7);
		this.field_48231_e.render(f7);
		this.field_48229_f.render(f7);
		this.field_48233_c.render(f7);
		this.field_48230_d.render(f7);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.field_48234_a.rotateAngleY = f4 / 57.295776F;
		this.field_48234_a.rotateAngleX = f5 / 57.295776F;
		this.field_48231_e.rotateAngleX = -1.5F * this.func_48228_a(f1, 13.0F) * f2;
		this.field_48229_f.rotateAngleX = 1.5F * this.func_48228_a(f1, 13.0F) * f2;
		this.field_48231_e.rotateAngleY = 0.0F;
		this.field_48229_f.rotateAngleY = 0.0F;
	}

	public void setLivingAnimations(EntityLiving entityLiving1, float f2, float f3, float f4) {
		EntityIronGolem entityIronGolem5 = (EntityIronGolem)entityLiving1;
		int i6 = entityIronGolem5.func_48114_ab();
		if(i6 > 0) {
			this.field_48233_c.rotateAngleX = -2.0F + 1.5F * this.func_48228_a((float)i6 - f4, 10.0F);
			this.field_48230_d.rotateAngleX = -2.0F + 1.5F * this.func_48228_a((float)i6 - f4, 10.0F);
		} else {
			int i7 = entityIronGolem5.func_48117_D_();
			if(i7 > 0) {
				this.field_48233_c.rotateAngleX = -0.8F + 0.025F * this.func_48228_a((float)i7, 70.0F);
				this.field_48230_d.rotateAngleX = 0.0F;
			} else {
				this.field_48233_c.rotateAngleX = (-0.2F + 1.5F * this.func_48228_a(f2, 13.0F)) * f3;
				this.field_48230_d.rotateAngleX = (-0.2F - 1.5F * this.func_48228_a(f2, 13.0F)) * f3;
			}
		}

	}

	private float func_48228_a(float f1, float f2) {
		return (Math.abs(f1 % f2 - f2 * 0.5F) - f2 * 0.25F) / (f2 * 0.25F);
	}
}
