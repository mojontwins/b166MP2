package net.minecraft.client.particle;

import com.mojontwins.utils.Idx2uvF;
import com.mojontwins.utils.Texels;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntityBreakingFX extends EntityFX {
	public EntityBreakingFX(World world1, double d2, double d4, double d6, Item item8) {
		super(world1, d2, d4, d6, 0.0D, 0.0D, 0.0D);
		this.setParticleTextureIndex(item8.getIconFromDamage(0));
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleGravity = Block.blockSnow.blockParticleGravity;
		this.particleScale /= 2.0F;
	}

	public EntityBreakingFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12, Item item14) {
		this(world1, d2, d4, d6, item14);
		this.motionX *= (double)0.1F;
		this.motionY *= (double)0.1F;
		this.motionZ *= (double)0.1F;
		this.motionX += d8;
		this.motionY += d10;
		this.motionZ += d12;
	}

	public int getFXLayer() {
		return 2;
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		/*
		float f8 = ((float)(this.getParticleTextureIndex() % 16) + this.particleTextureJitterX / 4.0F) / 16.0F;
		float f9 = f8 + 0.015609375F; // 4 texels
		float f10 = ((float)(this.getParticleTextureIndex() / 16) + this.particleTextureJitterY / 4.0F) / 16.0F;
		float f11 = f10 + 0.015609375F;
		*/
		Idx2uvF.calc(this.getParticleTextureIndex());
		double f8 = Idx2uvF.u1 + Texels.texelsU(this.particleTextureJitterX * 4.0F);
		double f9 = f8 + Texels.texelsU(4F);
		double f10 = Idx2uvF.v1 + Texels.texelsV(this.particleTextureJitterY * 4.0F);
		double f11 = f10 + Texels.texelsV(4F);
		
		float f12 = 0.1F * this.particleScale;
		float f13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)f2 - interpPosX);
		float f14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)f2 - interpPosY);
		float f15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)f2 - interpPosZ);
		float f16 = 1.0F;
		tessellator1.setColorOpaque_F(f16 * this.particleRed, f16 * this.particleGreen, f16 * this.particleBlue);
		tessellator1.addVertexWithUV((double)(f13 - f3 * f12 - f6 * f12), (double)(f14 - f4 * f12), (double)(f15 - f5 * f12 - f7 * f12), f8, f11);
		tessellator1.addVertexWithUV((double)(f13 - f3 * f12 + f6 * f12), (double)(f14 + f4 * f12), (double)(f15 - f5 * f12 + f7 * f12), f8, f10);
		tessellator1.addVertexWithUV((double)(f13 + f3 * f12 + f6 * f12), (double)(f14 + f4 * f12), (double)(f15 + f5 * f12 + f7 * f12), f9, f10);
		tessellator1.addVertexWithUV((double)(f13 + f3 * f12 - f6 * f12), (double)(f14 - f4 * f12), (double)(f15 + f5 * f12 - f7 * f12), f9, f11);
	}
}
