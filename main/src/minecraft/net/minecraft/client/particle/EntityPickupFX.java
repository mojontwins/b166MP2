package net.minecraft.client.particle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public class EntityPickupFX extends EntityFX {
	private Entity entityToPickUp;
	private Entity entityPickingUp;
	private int age = 0;
	private int maxAge = 0;
	private float yOffs;

	public EntityPickupFX(World world1, Entity entity2, Entity entity3, float f4) {
		super(world1, entity2.posX, entity2.posY, entity2.posZ, entity2.motionX, entity2.motionY, entity2.motionZ);
		this.entityToPickUp = entity2;
		this.entityPickingUp = entity3;
		this.maxAge = 3;
		this.yOffs = f4;
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		float f8 = ((float)this.age + f2) / (float)this.maxAge;
		f8 *= f8;
		double d9 = this.entityToPickUp.posX;
		double d11 = this.entityToPickUp.posY;
		double d13 = this.entityToPickUp.posZ;
		double d15 = this.entityPickingUp.lastTickPosX + (this.entityPickingUp.posX - this.entityPickingUp.lastTickPosX) * (double)f2;
		double d17 = this.entityPickingUp.lastTickPosY + (this.entityPickingUp.posY - this.entityPickingUp.lastTickPosY) * (double)f2 + (double)this.yOffs;
		double d19 = this.entityPickingUp.lastTickPosZ + (this.entityPickingUp.posZ - this.entityPickingUp.lastTickPosZ) * (double)f2;
		double d21 = d9 + (d15 - d9) * (double)f8;
		double d23 = d11 + (d17 - d11) * (double)f8;
		double d25 = d13 + (d19 - d13) * (double)f8;

		int i30 = this.getBrightnessForRender(f2);
		int i31 = i30 % 65536;
		int i32 = i30 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)i31 / 1.0F, (float)i32 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		d21 -= interpPosX;
		d23 -= interpPosY;
		d25 -= interpPosZ;
		RenderManager.instance.renderEntityWithPosYaw(this.entityToPickUp, (double)((float)d21), (double)((float)d23), (double)((float)d25), this.entityToPickUp.rotationYaw, f2);
	}

	public void onUpdate() {
		++this.age;
		if(this.age == this.maxAge) {
			this.setDead();
		}

	}

	public int getFXLayer() {
		return 3;
	}
}
