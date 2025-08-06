package net.minecraft.client.particle;

import net.minecraft.world.level.World;

public class EntityEnchantmentTableParticleFX extends EntityFX {
	private double field_40109_aw;
	private double field_40108_ax;
	private double field_40106_ay;

	public EntityEnchantmentTableParticleFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, d8, d10, d12);
		this.motionX = d8;
		this.motionY = d10;
		this.motionZ = d12;
		this.field_40109_aw = this.posX = d2;
		this.field_40108_ax = this.posY = d4;
		this.field_40106_ay = this.posZ = d6;
		float f14 = this.rand.nextFloat() * 0.6F + 0.4F;
		this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f14;
		this.particleGreen *= 0.9F;
		this.particleRed *= 0.9F;
		this.particleMaxAge = (int)(Math.random() * 10.0D) + 30;
		this.noClip = true;
		this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
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
		f3 *= f3;
		f3 *= f3;
		return f2 * (1.0F - f3) + f3;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		float f1 = (float)this.particleAge / (float)this.particleMaxAge;
		f1 = 1.0F - f1;
		float f2 = 1.0F - f1;
		f2 *= f2;
		f2 *= f2;
		this.posX = this.field_40109_aw + this.motionX * (double)f1;
		this.posY = this.field_40108_ax + this.motionY * (double)f1 - (double)(f2 * 1.2F);
		this.posZ = this.field_40106_ay + this.motionZ * (double)f1;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

	}
}
