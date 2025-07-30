package net.minecraft.client.model;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntitySheep;

public class ModelSheep1 extends ModelQuadruped {
	private float field_44016_o;

	public ModelSheep1() {
		super(12, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
		this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.body = new ModelRenderer(this, 28, 8);
		this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
		float f1 = 0.5F;
		this.leg1 = new ModelRenderer(this, 0, 16);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f1);
		this.leg1.setRotationPoint(-3.0F, 12.0F, 7.0F);
		this.leg2 = new ModelRenderer(this, 0, 16);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f1);
		this.leg2.setRotationPoint(3.0F, 12.0F, 7.0F);
		this.leg3 = new ModelRenderer(this, 0, 16);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f1);
		this.leg3.setRotationPoint(-3.0F, 12.0F, -5.0F);
		this.leg4 = new ModelRenderer(this, 0, 16);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f1);
		this.leg4.setRotationPoint(3.0F, 12.0F, -5.0F);
	}

	public void setLivingAnimations(EntityLiving entityLiving1, float f2, float f3, float f4) {
		super.setLivingAnimations(entityLiving1, f2, f3, f4);
		this.head.rotationPointY = 6.0F + ((EntitySheep)entityLiving1).func_44003_c(f4) * 9.0F;
		this.field_44016_o = ((EntitySheep)entityLiving1).func_44002_d(f4);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.head.rotateAngleX = this.field_44016_o;
	}
}
