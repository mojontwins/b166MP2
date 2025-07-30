package net.minecraft.client.particle;

import net.minecraft.world.level.World;

public class EntityAuraFX extends EntityFX {
	public EntityAuraFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, d8, d10, d12);
		float f14 = this.rand.nextFloat() * 0.1F + 0.2F;
		this.particleRed = f14;
		this.particleGreen = f14;
		this.particleBlue = f14;
		this.setParticleTextureIndex(0);
		this.setSize(0.02F, 0.02F);
		this.particleScale *= this.rand.nextFloat() * 0.6F + 0.5F;
		this.motionX *= (double)0.02F;
		this.motionY *= (double)0.02F;
		this.motionZ *= (double)0.02F;
		this.particleMaxAge = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
		this.noClip = true;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.99D;
		this.motionY *= 0.99D;
		this.motionZ *= 0.99D;
		if(this.particleMaxAge-- <= 0) {
			this.setDead();
		}

	}
}
