package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.World;

public class EntityLavaFX extends EntityFX {
	private float lavaParticleScale;

	public EntityLavaFX(World world1, double d2, double d4, double d6) {
		super(world1, d2, d4, d6, 0.0D, 0.0D, 0.0D);
		this.motionX *= (double)0.8F;
		this.motionY *= (double)0.8F;
		this.motionZ *= (double)0.8F;
		this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
		this.lavaParticleScale = this.particleScale;
		this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
		this.noClip = false;
		this.setParticleTextureIndex(49);
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
		short s4 = 240;
		int i5 = i3 >> 16 & 255;
		return s4 | i5 << 16;
	}

	public float getBrightness(float f1) {
		return 1.0F;
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		float f8 = ((float)this.particleAge + f2) / (float)this.particleMaxAge;
		this.particleScale = this.lavaParticleScale * (1.0F - f8 * f8);
		super.renderParticle(tessellator1, f2, f3, f4, f5, f6, f7);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		float f1 = (float)this.particleAge / (float)this.particleMaxAge;
		if(this.rand.nextFloat() > f1) {
			this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
		}

		this.motionY -= 0.03D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.999F;
		this.motionY *= (double)0.999F;
		this.motionZ *= (double)0.999F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
