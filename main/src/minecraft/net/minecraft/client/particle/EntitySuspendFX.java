package net.minecraft.client.particle;

import net.minecraft.src.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class EntitySuspendFX extends EntityFX {
	public EntitySuspendFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4 - 0.125D, d6, d8, d10, d12);
		this.particleRed = 0.4F;
		this.particleGreen = 0.4F;
		this.particleBlue = 0.7F;
		this.setParticleTextureIndex(0);
		this.setSize(0.01F, 0.01F);
		this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
		this.motionX = d8 * 0.0D;
		this.motionY = d10 * 0.0D;
		this.motionZ = d12 * 0.0D;
		this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		if(this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) != Material.water) {
			this.setDead();
		}

		if(this.particleMaxAge-- <= 0) {
			this.setDead();
		}

	}
}
