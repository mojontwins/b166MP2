package net.minecraft.client.renderer.tileentity;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawner;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer {
	private Map<String, Entity> entityHashMap = new HashMap<String, Entity>();

	public void renderTileEntityMobSpawner(TileEntityMobSpawner tileEntityMobSpawner1, double d2, double d4, double d6, float f8) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d2 + 0.5F, (float)d4, (float)d6 + 0.5F);
		Entity entity9 = (Entity)this.entityHashMap.get(tileEntityMobSpawner1.getMobID());
		if(entity9 == null) {
			entity9 = EntityList.createEntityByName(tileEntityMobSpawner1.getMobID(), (World)null);
			this.entityHashMap.put(tileEntityMobSpawner1.getMobID(), entity9);
		}

		if(entity9 != null) {
			entity9.setWorld(tileEntityMobSpawner1.worldObj);
			float f10 = 0.4375F;
			GL11.glTranslatef(0.0F, 0.4F, 0.0F);
			GL11.glRotatef((float)(tileEntityMobSpawner1.prevYaw + (tileEntityMobSpawner1.yaw - tileEntityMobSpawner1.prevYaw) * (double)f8) * 10.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.4F, 0.0F);
			GL11.glScalef(f10, f10, f10);
			entity9.setLocationAndAngles(d2, d4, d6, 0.0F, 0.0F);
			RenderManager.instance.renderEntityWithPosYaw(entity9, 0.0D, 0.0D, 0.0D, 0.0F, f8);
		}

		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntity tileEntity1, double d2, double d4, double d6, float f8) {
		this.renderTileEntityMobSpawner((TileEntityMobSpawner)tileEntity1, d2, d4, d6, f8);
	}
}
