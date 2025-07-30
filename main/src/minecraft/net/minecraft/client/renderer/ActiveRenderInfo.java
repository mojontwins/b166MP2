package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFluid;
import net.minecraft.world.phys.Vec3D;

public class ActiveRenderInfo {
	public static float objectX = 0.0F;
	public static float objectY = 0.0F;
	public static float objectZ = 0.0F;
	private static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
	private static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
	private static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
	private static FloatBuffer objectCoords = GLAllocation.createDirectFloatBuffer(3);
	public static float rotationX;
	public static float rotationXZ;
	public static float rotationZ;
	public static float rotationYZ;
	public static float rotationXY;

	public static void updateRenderInfo(EntityPlayer entityPlayer0, boolean z1) {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
		float f2 = (float)((viewport.get(0) + viewport.get(2)) / 2);
		float f3 = (float)((viewport.get(1) + viewport.get(3)) / 2);
		GLU.gluUnProject(f2, f3, 0.0F, modelview, projection, viewport, objectCoords);
		objectX = objectCoords.get(0);
		objectY = objectCoords.get(1);
		objectZ = objectCoords.get(2);
		int i4 = z1 ? 1 : 0;
		float f5 = entityPlayer0.rotationPitch;
		float f6 = entityPlayer0.rotationYaw;
		rotationX = MathHelper.cos(f6 * (float)Math.PI / 180.0F) * (float)(1 - i4 * 2);
		rotationZ = MathHelper.sin(f6 * (float)Math.PI / 180.0F) * (float)(1 - i4 * 2);
		rotationYZ = -rotationZ * MathHelper.sin(f5 * (float)Math.PI / 180.0F) * (float)(1 - i4 * 2);
		rotationXY = rotationX * MathHelper.sin(f5 * (float)Math.PI / 180.0F) * (float)(1 - i4 * 2);
		rotationXZ = MathHelper.cos(f5 * (float)Math.PI / 180.0F);
	}

	public static Vec3D projectViewFromEntity(EntityLiving entityLiving0, double d1) {
		double d3 = entityLiving0.prevPosX + (entityLiving0.posX - entityLiving0.prevPosX) * d1;
		double d5 = entityLiving0.prevPosY + (entityLiving0.posY - entityLiving0.prevPosY) * d1 + (double)entityLiving0.getEyeHeight();
		double d7 = entityLiving0.prevPosZ + (entityLiving0.posZ - entityLiving0.prevPosZ) * d1;
		double d9 = d3 + (double)(objectX * 1.0F);
		double d11 = d5 + (double)(objectY * 1.0F);
		double d13 = d7 + (double)(objectZ * 1.0F);
		return Vec3D.createVector(d9, d11, d13);
	}

	public static int getBlockIdAtEntityViewpoint(World world0, EntityLiving entityLiving1, float f2) {
		Vec3D vec3D3 = projectViewFromEntity(entityLiving1, (double)f2);
		ChunkPosition chunkPosition4 = new ChunkPosition(vec3D3);
		int i5 = world0.getBlockId(chunkPosition4.x, chunkPosition4.y, chunkPosition4.z);
		if(i5 != 0 && Block.blocksList[i5].blockMaterial.isLiquid()) {
			float f6 = BlockFluid.getFluidHeightPercent(world0.getBlockMetadata(chunkPosition4.x, chunkPosition4.y, chunkPosition4.z)) - 0.11111111F;
			float f7 = (float)(chunkPosition4.y + 1) - f6;
			if(vec3D3.yCoord >= (double)f7) {
				i5 = world0.getBlockId(chunkPosition4.x, chunkPosition4.y + 1, chunkPosition4.z);
			}
		}

		return i5;
	}
}
