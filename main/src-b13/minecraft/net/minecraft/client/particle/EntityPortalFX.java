package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.World;

public class EntityPortalFX extends EntityFX {
	private float portalParticleScale;
	private double portalPosX;
	private double portalPosY;
	private double portalPosZ;

	public EntityPortalFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, d8, d10, d12);
		this.motionX = d8;
		this.motionY = d10;
		this.motionZ = d12;
		this.portalPosX = this.posX = d2;
		this.portalPosY = this.posY = d4;
		this.portalPosZ = this.posZ = d6;
		float f14 = this.rand.nextFloat() * 0.6F + 0.4F;
		this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f14;
		this.particleGreen *= 0.3F;
		this.particleRed *= 0.9F;
		this.particleMaxAge = (int)(Math.random() * 10.0D) + 40;
		this.noClip = true;
		this.setParticleTextureIndex((int)(Math.random() * 8.0D));
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		float f8 = ((float)this.particleAge + f2) / (float)this.particleMaxAge;
		f8 = 1.0F - f8;
		f8 *= f8;
		f8 = 1.0F - f8;
		this.particleScale = this.portalParticleScale * f8;
		super.renderParticle(tessellator1, f2, f3, f4, f5, f6, f7);
	}

	public int getBrightnessForRender(float f1) {
		int i2 = super.getBrightnessForRender(f1);
		float f3 = (float)this.particleAge / (float)this.particleMaxAge;
		f3 *= f3;
		f3 *= f3;
		int i4 = i2 & 255;
		int i5 = i2 >> 16 & 255;
		i5 += (int)(f3 * 15.0F * 16.0F);
		if(i5 > 240) {
			i5 = 240;
		}

		return i4 | i5 << 16;
	}

	public float getBrightness(float f1) {
		float f2 = super.getBrightness(f1);
		float f3 = (float)this.particleAge / (float)this.particleMaxAge;
		f3 = f3 * f3 * f3 * f3;
		return f2 * (1.0F - f3) + f3;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		float f1 = (float)this.particleAge / (float)this.particleMaxAge;
		float f2 = f1;
		f1 = -f1 + f1 * f1 * 2.0F;
		f1 = 1.0F - f1;
		this.posX = this.portalPosX + this.motionX * (double)f1;
		this.posY = this.portalPosY + this.motionY * (double)f1 + (double)(1.0F - f2);
		this.posZ = this.portalPosZ + this.motionZ * (double)f1;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

	}
}
