package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.World;

public class EntityCritFX extends EntityFX {
	float field_35137_a;

	public EntityCritFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		this(world1, d2, d4, d6, d8, d10, d12, 1.0F);
	}

	public EntityCritFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12, float f14) {
		super(world1, d2, d4, d6, 0.0D, 0.0D, 0.0D);
		this.motionX *= (double)0.1F;
		this.motionY *= (double)0.1F;
		this.motionZ *= (double)0.1F;
		this.motionX += d8 * 0.4D;
		this.motionY += d10 * 0.4D;
		this.motionZ += d12 * 0.4D;
		this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * (double)0.3F + (double)0.6F);
		this.particleScale *= 0.75F;
		this.particleScale *= f14;
		this.field_35137_a = this.particleScale;
		this.particleMaxAge = (int)(6.0D / (Math.random() * 0.8D + 0.6D));
		this.particleMaxAge = (int)((float)this.particleMaxAge * f14);
		this.noClip = false;
		this.setParticleTextureIndex(65);
		this.onUpdate();
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		float f8 = ((float)this.particleAge + f2) / (float)this.particleMaxAge * 32.0F;
		if(f8 < 0.0F) {
			f8 = 0.0F;
		}

		if(f8 > 1.0F) {
			f8 = 1.0F;
		}

		this.particleScale = this.field_35137_a * f8;
		super.renderParticle(tessellator1, f2, f3, f4, f5, f6, f7);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.particleGreen = (float)((double)this.particleGreen * 0.96D);
		this.particleBlue = (float)((double)this.particleBlue * 0.9D);
		this.motionX *= (double)0.7F;
		this.motionY *= (double)0.7F;
		this.motionZ *= (double)0.7F;
		this.motionY -= (double)0.02F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
