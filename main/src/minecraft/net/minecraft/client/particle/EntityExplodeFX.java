package net.minecraft.client.particle;

import net.minecraft.world.level.World;

public class EntityExplodeFX extends EntityFX {
	public EntityExplodeFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, d8, d10, d12);
		this.motionX = d8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
		this.motionY = d10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
		this.motionZ = d12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
		this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.3F + 0.7F;
		this.particleScale = this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 1.0F;
		this.particleMaxAge = (int)(16.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D)) + 2;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
		this.motionY += 0.004D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.9F;
		this.motionY *= (double)0.9F;
		this.motionZ *= (double)0.9F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
