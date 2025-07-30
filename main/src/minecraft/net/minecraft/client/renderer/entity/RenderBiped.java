package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.inventory.InventoryMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

public class RenderBiped extends RenderLiving {
	protected ModelBiped modelBipedMain;
	protected float field_40296_d;
	
	protected ModelBiped modelArmorChestplate;
	protected ModelBiped modelArmor;

	public RenderBiped(ModelBiped modelBiped1, float f2) {
		this(modelBiped1, f2, 1.0F);
		this.modelBipedMain = modelBiped1;
		this.modelArmorChestplate = new ModelBiped(1.0F);
		this.modelArmor = new ModelBiped(0.5F);
		this.modelArmorChestplate.aimedBow = this.modelBipedMain.aimedBow;
	}

	public RenderBiped(ModelBiped modelBiped1, float f2, float f3) {
		super(modelBiped1, f2);
		this.modelBipedMain = modelBiped1;
		this.field_40296_d = f3;
	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
		super.renderEquippedItems(entityLiving1, f2);
		ItemStack itemStack3 = entityLiving1.getHeldItem();
		if(itemStack3 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedRightArm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
			float f4;
			if(
					itemStack3.itemID < 4096 && Block.blocksList[itemStack3.itemID] != null && 
					RenderBlocks.renderItemIn3d(Block.blocksList[itemStack3.itemID].getRenderType())
				) {
				f4 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f4 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
			} else if(itemStack3.itemID == Item.bow.shiftedIndex) {
				f4 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if(Item.itemsList[itemStack3.itemID].isFull3D()) {
				f4 = 0.625F;
				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				f4 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f4, f4, f4);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.renderManager.itemRenderer.renderItem(entityLiving1, itemStack3, 0);
			if(itemStack3.getItem().requiresMultipleRenderPasses()) {
				this.renderManager.itemRenderer.renderItem(entityLiving1, itemStack3, 1);
			}

			GL11.glPopMatrix();
		}

	}
	
	protected int shouldRenderPass(EntityLiving entityLiving, int pass, float renderPartialTicks) {
		if(pass < 4) {
			
			IInventory inventory = entityLiving.getIInventory();
			if (inventory != null && inventory instanceof InventoryMob) {
				ItemStack itemStack = ((InventoryMob)inventory).getArmorItemInSlot(3 - pass);
	
				if(itemStack != null) { 
					Item item = itemStack.getItem(); 
					if(item instanceof ItemArmor) {
						ItemArmor armorPart = (ItemArmor)item;
						this.loadTexture(RenderPlayer.getArmorTexture(itemStack, armorPart.renderIndex, pass));
						this.loadTexture("/armor/" + ItemArmor.getArmorFilenamePrefix(armorPart.renderIndex) + "_" + (pass == 2 ? 2 : 1) + ".png");
						
						ModelBiped modelArmor = pass == 2 ? this.modelArmor : this.modelArmorChestplate;
						modelArmor.bipedHead.showModel = pass == 0;
						modelArmor.bipedHeadwear.showModel = pass == 0;
						modelArmor.bipedBody.showModel = pass == 1 || pass == 2;
						modelArmor.bipedRightArm.showModel = pass == 1;
						modelArmor.bipedLeftArm.showModel = pass == 1;
						modelArmor.bipedRightLeg.showModel = pass == 2 || pass == 3;
						modelArmor.bipedLeftLeg.showModel = pass == 2 || pass == 3;
						this.setRenderPassModel(modelArmor);
						return 1;
					} else if(item.shiftedIndex < 256) {
						GL11.glPushMatrix();
						this.modelBipedMain.bipedHead.postRender(0.0625F);
						if(RenderBlocks.renderItemIn3d(Block.blocksList[item.shiftedIndex].getRenderType())) {
							float f4 = 0.625F;
							GL11.glTranslatef(0.0F, -0.25F, 0.0F);
							GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
							GL11.glScalef(f4, -f4, f4);
						}

						this.renderManager.itemRenderer.renderItem(entityLiving, itemStack, pass);
						GL11.glPopMatrix();
					}
				}
			}
		}
		
		return -1;
	}
}
