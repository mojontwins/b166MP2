package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityMinecart;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.Vec3D;

public class RenderMinecart extends Render {
	protected ModelBase modelMinecart;

	public RenderMinecart() {
		this.shadowSize = 0.5F;
		this.modelMinecart = new ModelMinecart();
	}

	public void func_152_a(EntityMinecart entityMinecart1, double d2, double d4, double d6, float f8, float f9) {
		GL11.glPushMatrix();
		long j10 = (long)entityMinecart1.entityId * 493286711L;
		j10 = j10 * j10 * 4392167121L + j10 * 98761L;
		float f12 = (((float)(j10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f13 = (((float)(j10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f14 = (((float)(j10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		GL11.glTranslatef(f12, f13, f14);
		double d15 = entityMinecart1.lastTickPosX + (entityMinecart1.posX - entityMinecart1.lastTickPosX) * (double)f9;
		double d17 = entityMinecart1.lastTickPosY + (entityMinecart1.posY - entityMinecart1.lastTickPosY) * (double)f9;
		double d19 = entityMinecart1.lastTickPosZ + (entityMinecart1.posZ - entityMinecart1.lastTickPosZ) * (double)f9;
		double d21 = (double)0.3F;
		Vec3D vec3D23 = entityMinecart1.func_514_g(d15, d17, d19);
		float f24 = entityMinecart1.prevRotationPitch + (entityMinecart1.rotationPitch - entityMinecart1.prevRotationPitch) * f9;
		if(vec3D23 != null) {
			Vec3D vec3D25 = entityMinecart1.func_515_a(d15, d17, d19, d21);
			Vec3D vec3D26 = entityMinecart1.func_515_a(d15, d17, d19, -d21);
			if(vec3D25 == null) {
				vec3D25 = vec3D23;
			}

			if(vec3D26 == null) {
				vec3D26 = vec3D23;
			}

			d2 += vec3D23.xCoord - d15;
			d4 += (vec3D25.yCoord + vec3D26.yCoord) / 2.0D - d17;
			d6 += vec3D23.zCoord - d19;
			Vec3D vec3D27 = vec3D26.addVector(-vec3D25.xCoord, -vec3D25.yCoord, -vec3D25.zCoord);
			if(vec3D27.lengthVector() != 0.0D) {
				vec3D27 = vec3D27.normalize();
				f8 = (float)(Math.atan2(vec3D27.zCoord, vec3D27.xCoord) * 180.0D / Math.PI);
				f24 = (float)(Math.atan(vec3D27.yCoord) * 73.0D);
			}
		}

		GL11.glTranslatef((float)d2, (float)d4, (float)d6);
		GL11.glRotatef(180.0F - f8, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-f24, 0.0F, 0.0F, 1.0F);
		float f28 = (float)entityMinecart1.func_41023_l() - f9;
		float f29 = (float)entityMinecart1.func_41025_i() - f9;
		if(f29 < 0.0F) {
			f29 = 0.0F;
		}

		if(f28 > 0.0F) {
			GL11.glRotatef(MathHelper.sin(f28) * f28 * f29 / 10.0F * (float)entityMinecart1.func_41030_m(), 1.0F, 0.0F, 0.0F);
		}

		if(entityMinecart1.minecartType != 0) {
			this.loadTexture("/terrain.png");
			float f30 = 0.75F;
			GL11.glScalef(f30, f30, f30);
			if(entityMinecart1.minecartType == 1) {
				GL11.glTranslatef(-0.5F, 0.0F, 0.5F);
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				(new RenderBlocks()).renderBlockAsItem(Block.chest, 0, entityMinecart1.getBrightness(f9));
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.5F, 0.0F, -0.5F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			} else if(entityMinecart1.minecartType == 2) {
				GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				(new RenderBlocks()).renderBlockAsItem(Block.stoneOvenIdle, 0, entityMinecart1.getBrightness(f9));
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, -0.3125F, 0.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

			GL11.glScalef(1.0F / f30, 1.0F / f30, 1.0F / f30);
		}

		this.loadTexture("/item/cart.png");
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		this.modelMinecart.render(entityMinecart1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.func_152_a((EntityMinecart)entity1, d2, d4, d6, f8, f9);
	}
}
