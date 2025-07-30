package net.minecraft.client.model;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntitySheep;

public class ModelSheep2 extends ModelQuadruped {
	private float field_44017_o;

	public ModelSheep2() {
		super(12, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
		this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.body = new ModelRenderer(this, 28, 8);
		this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
	}

	public void setLivingAnimations(EntityLiving entityLiving1, float f2, float f3, float f4) {
		super.setLivingAnimations(entityLiving1, f2, f3, f4);
		this.head.rotationPointY = 6.0F + ((EntitySheep)entityLiving1).func_44003_c(f4) * 9.0F;
		this.field_44017_o = ((EntitySheep)entityLiving1).func_44002_d(f4);
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		super.setRotationAngles(f1, f2, f3, f4, f5, f6);
		this.head.rotateAngleX = this.field_44017_o;
	}
}
