package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.World;

public class EntityFlameFX extends EntityFX {
	private float flameScale;

	public EntityFlameFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, d8, d10, d12);
		this.motionX = this.motionX * (double)0.01F + d8;
		this.motionY = this.motionY * (double)0.01F + d10;
		this.motionZ = this.motionZ * (double)0.01F + d12;
		this.flameScale = this.particleScale;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
		this.noClip = true;
		this.setParticleTextureIndex(48);
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		float f8 = ((float)this.particleAge + f2) / (float)this.particleMaxAge;
		this.particleScale = this.flameScale * (1.0F - f8 * f8 * 0.5F);
		super.renderParticle(tessellator1, f2, f3, f4, f5, f6, f7);
	}

	public int getBrightnessForRender(float f1) {
		float f2 = ((float)this.particleAge + f1) / (float)this.particleMaxAge;
		if(f2 < 0.0F) {
			f2 = 0.0F;
		}

		if(f2 > 1.0F) {
			f2 = 1.0F;
		}

		int i3 = super.getBrightnessForRender(f1);
		int i4 = i3 & 255;
		int i5 = i3 >> 16 & 255;
		i4 += (int)(f2 * 15.0F * 16.0F);
		if(i4 > 240) {
			i4 = 240;
		}

		return i4 | i5 << 16;
	}

	public float getBrightness(float f1) {
		float f2 = ((float)this.particleAge + f1) / (float)this.particleMaxAge;
		if(f2 < 0.0F) {
			f2 = 0.0F;
		}

		if(f2 > 1.0F) {
			f2 = 1.0F;
		}

		float f3 = super.getBrightness(f1);
		return f3 * f2 + (1.0F - f2);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.96F;
		this.motionY *= (double)0.96F;
		this.motionZ *= (double)0.96F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
