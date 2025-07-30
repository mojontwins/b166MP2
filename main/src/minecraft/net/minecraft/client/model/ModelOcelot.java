package net.minecraft.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityOcelot;

public class ModelOcelot extends ModelBase {
	ModelRenderer field_48225_a;
	ModelRenderer field_48223_b;
	ModelRenderer field_48224_c;
	ModelRenderer field_48221_d;
	ModelRenderer field_48222_e;
	ModelRenderer field_48219_f;
	ModelRenderer field_48220_g;
	ModelRenderer field_48226_n;
	int field_48227_o = 1;

	public ModelOcelot() {
		this.setTextureOffset("head.main", 0, 0);
		this.setTextureOffset("head.nose", 0, 24);
		this.setTextureOffset("head.ear1", 0, 10);
		this.setTextureOffset("head.ear2", 6, 10);
		this.field_48220_g = new ModelRenderer(this, "head");
		this.field_48220_g.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
		this.field_48220_g.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
		this.field_48220_g.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
		this.field_48220_g.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);
		this.field_48220_g.setRotationPoint(0.0F, 15.0F, -9.0F);
		this.field_48226_n = new ModelRenderer(this, 20, 0);
		this.field_48226_n.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F);
		this.field_48226_n.setRotationPoint(0.0F, 12.0F, -10.0F);
		this.field_48222_e = new ModelRenderer(this, 0, 15);
		this.field_48222_e.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
		this.field_48222_e.rotateAngleX = 0.9F;
		this.field_48222_e.setRotationPoint(0.0F, 15.0F, 8.0F);
		this.field_48219_f = new ModelRenderer(this, 4, 15);
		this.field_48219_f.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
		this.field_48219_f.setRotationPoint(0.0F, 20.0F, 14.0F);
		this.field_48225_a = new ModelRenderer(this, 8, 13);
		this.field_48225_a.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
		this.field_48225_a.setRotationPoint(1.1F, 18.0F, 5.0F);
		this.field_48223_b = new ModelRenderer(this, 8, 13);
		this.field_48223_b.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
		this.field_48223_b.setRotationPoint(-1.1F, 18.0F, 5.0F);
		this.field_48224_c = new ModelRenderer(this, 40, 0);
		this.field_48224_c.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
		this.field_48224_c.setRotationPoint(1.2F, 13.8F, -5.0F);
		this.field_48221_d = new ModelRenderer(this, 40, 0);
		this.field_48221_d.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
		this.field_48221_d.setRotationPoint(-1.2F, 13.8F, -5.0F);
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);
		if(this.isChild) {
			float f8 = 2.0F;
			GL11.glPushMatrix();
			GL11.glScalef(1.5F / f8, 1.5F / f8, 1.5F / f8);
			GL11.glTranslatef(0.0F, 10.0F * f7, 4.0F * f7);
			this.field_48220_g.render(f7);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / f8, 1.0F / f8, 1.0F / f8);
			GL11.glTranslatef(0.0F, 24.0F * f7, 0.0F);
			this.field_48226_n.render(f7);
			this.field_48225_a.render(f7);
			this.field_48223_b.render(f7);
			this.field_48224_c.render(f7);
			this.field_48221_d.render(f7);
			this.field_48222_e.render(f7);
			this.field_48219_f.render(f7);
			GL11.glPopMatrix();
		} else {
			this.field_48220_g.render(f7);
			this.field_48226_n.render(f7);
			this.field_48222_e.render(f7);
			this.field_48219_f.render(f7);
			this.field_48225_a.render(f7);
			this.field_48223_b.render(f7);
			this.field_48224_c.render(f7);
			this.field_48221_d.render(f7);
		}

	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.field_48220_g.rotateAngleX = f5 / 57.295776F;
		this.field_48220_g.rotateAngleY = f4 / 57.295776F;
		if(this.field_48227_o != 3) {
			this.field_48226_n.rotateAngleX = (float)Math.PI / 2F;
			if(this.field_48227_o == 2) {
				this.field_48225_a.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.0F * f2;
				this.field_48223_b.rotateAngleX = MathHelper.cos(f1 * 0.6662F + 0.3F) * 1.0F * f2;
				this.field_48224_c.rotateAngleX = MathHelper.cos(f1 * 0.6662F + (float)Math.PI + 0.3F) * 1.0F * f2;
				this.field_48221_d.rotateAngleX = MathHelper.cos(f1 * 0.6662F + (float)Math.PI) * 1.0F * f2;
				this.field_48219_f.rotateAngleX = 1.7278761F + 0.31415927F * MathHelper.cos(f1) * f2;
			} else {
				this.field_48225_a.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.0F * f2;
				this.field_48223_b.rotateAngleX = MathHelper.cos(f1 * 0.6662F + (float)Math.PI) * 1.0F * f2;
				this.field_48224_c.rotateAngleX = MathHelper.cos(f1 * 0.6662F + (float)Math.PI) * 1.0F * f2;
				this.field_48221_d.rotateAngleX = MathHelper.cos(f1 * 0.6662F) * 1.0F * f2;
				if(this.field_48227_o == 1) {
					this.field_48219_f.rotateAngleX = 1.7278761F + 0.7853982F * MathHelper.cos(f1) * f2;
				} else {
					this.field_48219_f.rotateAngleX = 1.7278761F + 0.47123894F * MathHelper.cos(f1) * f2;
				}
			}
		}

	}

	public void setLivingAnimations(EntityLiving entityLiving1, float f2, float f3, float f4) {
		EntityOcelot entityOcelot5 = (EntityOcelot)entityLiving1;
		this.field_48226_n.rotationPointY = 12.0F;
		this.field_48226_n.rotationPointZ = -10.0F;
		this.field_48220_g.rotationPointY = 15.0F;
		this.field_48220_g.rotationPointZ = -9.0F;
		this.field_48222_e.rotationPointY = 15.0F;
		this.field_48222_e.rotationPointZ = 8.0F;
		this.field_48219_f.rotationPointY = 20.0F;
		this.field_48219_f.rotationPointZ = 14.0F;
		this.field_48224_c.rotationPointY = this.field_48221_d.rotationPointY = 13.8F;
		this.field_48224_c.rotationPointZ = this.field_48221_d.rotationPointZ = -5.0F;
		this.field_48225_a.rotationPointY = this.field_48223_b.rotationPointY = 18.0F;
		this.field_48225_a.rotationPointZ = this.field_48223_b.rotationPointZ = 5.0F;
		this.field_48222_e.rotateAngleX = 0.9F;
		if(entityOcelot5.isSneaking()) {
			++this.field_48226_n.rotationPointY;
			this.field_48220_g.rotationPointY += 2.0F;
			++this.field_48222_e.rotationPointY;
			this.field_48219_f.rotationPointY += -4.0F;
			this.field_48219_f.rotationPointZ += 2.0F;
			this.field_48222_e.rotateAngleX = (float)Math.PI / 2F;
			this.field_48219_f.rotateAngleX = (float)Math.PI / 2F;
			this.field_48227_o = 0;
		} else if(entityOcelot5.isSprinting()) {
			this.field_48219_f.rotationPointY = this.field_48222_e.rotationPointY;
			this.field_48219_f.rotationPointZ += 2.0F;
			this.field_48222_e.rotateAngleX = (float)Math.PI / 2F;
			this.field_48219_f.rotateAngleX = (float)Math.PI / 2F;
			this.field_48227_o = 2;
		} else if(entityOcelot5.isSitting()) {
			this.field_48226_n.rotateAngleX = 0.7853982F;
			this.field_48226_n.rotationPointY += -4.0F;
			this.field_48226_n.rotationPointZ += 5.0F;
			this.field_48220_g.rotationPointY += -3.3F;
			++this.field_48220_g.rotationPointZ;
			this.field_48222_e.rotationPointY += 8.0F;
			this.field_48222_e.rotationPointZ += -2.0F;
			this.field_48219_f.rotationPointY += 2.0F;
			this.field_48219_f.rotationPointZ += -0.8F;
			this.field_48222_e.rotateAngleX = 1.7278761F;
			this.field_48219_f.rotateAngleX = 2.670354F;
			this.field_48224_c.rotateAngleX = this.field_48221_d.rotateAngleX = -0.15707964F;
			this.field_48224_c.rotationPointY = this.field_48221_d.rotationPointY = 15.8F;
			this.field_48224_c.rotationPointZ = this.field_48221_d.rotationPointZ = -7.0F;
			this.field_48225_a.rotateAngleX = this.field_48223_b.rotateAngleX = -1.5707964F;
			this.field_48225_a.rotationPointY = this.field_48223_b.rotationPointY = 21.0F;
			this.field_48225_a.rotationPointZ = this.field_48223_b.rotationPointZ = 1.0F;
			this.field_48227_o = 3;
		} else {
			this.field_48227_o = 1;
		}

	}
}
