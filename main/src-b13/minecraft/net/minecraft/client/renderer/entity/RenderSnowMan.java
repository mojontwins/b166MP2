package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntitySnowman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

public class RenderSnowMan extends RenderLiving {
	private ModelSnowMan snowmanModel = (ModelSnowMan)this.mainModel;

	public RenderSnowMan() {
		super(new ModelSnowMan(), 0.5F);
		this.setRenderPassModel(this.snowmanModel);
	}

	protected void func_40288_a(EntitySnowman entitySnowman1, float f2) {
		super.renderEquippedItems(entitySnowman1, f2);
		ItemStack itemStack3 = new ItemStack(Block.pumpkin, 1);
		if(itemStack3 != null && itemStack3.getItem().shiftedIndex < 256) {
			GL11.glPushMatrix();
			this.snowmanModel.field_40305_c.postRender(0.0625F);
			if(RenderBlocks.renderItemIn3d(Block.blocksList[itemStack3.itemID].getRenderType())) {
				float f4 = 0.625F;
				GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
			}

			this.renderManager.itemRenderer.renderItem(entitySnowman1, itemStack3, 0);
			GL11.glPopMatrix();
		}

	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
		this.func_40288_a((EntitySnowman)entityLiving1, f2);
	}
}
