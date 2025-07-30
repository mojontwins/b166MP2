package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;

public class EntityCloudFX extends EntityFX {
	float field_35135_a;

	public EntityCloudFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, 0.0D, 0.0D, 0.0D);
		float f14 = 2.5F;
		this.motionX *= (double)0.1F;
		this.motionY *= (double)0.1F;
		this.motionZ *= (double)0.1F;
		this.motionX += d8;
		this.motionY += d10;
		this.motionZ += d12;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F - (float)(Math.random() * (double)0.3F);
		this.particleScale *= 0.75F;
		this.particleScale *= f14;
		this.field_35135_a = this.particleScale;
		this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
		this.particleMaxAge = (int)((float)this.particleMaxAge * f14);
		this.noClip = false;
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		float f8 = ((float)this.particleAge + f2) / (float)this.particleMaxAge * 32.0F;
		if(f8 < 0.0F) {
			f8 = 0.0F;
		}

		if(f8 > 1.0F) {
			f8 = 1.0F;
		}

		this.particleScale = this.field_35135_a * f8;
		super.renderParticle(tessellator1, f2, f3, f4, f5, f6, f7);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.96F;
		this.motionY *= (double)0.96F;
		this.motionZ *= (double)0.96F;
		EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, 2.0D);
		if(entityPlayer1 != null && this.posY > entityPlayer1.boundingBox.minY) {
			this.posY += (entityPlayer1.boundingBox.minY - this.posY) * 0.2D;
			this.motionY += (entityPlayer1.motionY - this.motionY) * 0.2D;
			this.setPosition(this.posX, this.posY, this.posZ);
		}

		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
