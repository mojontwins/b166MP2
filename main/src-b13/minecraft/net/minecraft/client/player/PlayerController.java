package net.minecraft.client.player;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public abstract class PlayerController {
	protected final Minecraft mc;
	public boolean isInTestMode = false;

	public PlayerController(Minecraft mc) {
		this.mc = mc;
	}

	public void onWorldChange(World world1) {
	}

	public abstract void clickBlock(int i1, int i2, int i3, int i4);

	public boolean onPlayerDestroyBlock(int i1, int i2, int i3, int i4) {
		World world5 = this.mc.theWorld;
		Block block6 = Block.blocksList[world5.getBlockId(i1, i2, i3)];
		if(block6 == null) {
			return false;
		} else {
			world5.playAuxSFX(2001, i1, i2, i3, block6.blockID + (world5.getBlockMetadata(i1, i2, i3) << 12));
			int i7 = world5.getBlockMetadata(i1, i2, i3);
			
			block6.onBlockHarvested(this.mc.theWorld, i1, i2, i3, i7, this.mc.thePlayer);
			
			boolean z8 = world5.setBlockWithNotify(i1, i2, i3, 0);
			if(z8) {
				block6.onBlockDestroyedByPlayer(world5, i1, i2, i3, i7);
			}

			return z8;
		}
	}

	public abstract void onPlayerDamageBlock(int i1, int i2, int i3, int i4);

	public abstract void resetBlockRemoving();

	public void setPartialTime(float f1) {
	}

	public abstract float getBlockReachDistance();

	public boolean sendUseItem(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3) {
		int i4 = itemStack3.stackSize;
		ItemStack itemStack5 = itemStack3.useItemRightClick(world2, entityPlayer1);
		if(itemStack5 != itemStack3 || itemStack5 != null && itemStack5.stackSize != i4) {
			entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentItem] = itemStack5;
			if(itemStack5.stackSize == 0) {
				entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentItem] = null;
			}

			return true;
		} else {
			return false;
		}
	}

	public void flipPlayer(EntityPlayer entityPlayer1) {
	}

	public void updateController() {
	}

	public abstract boolean shouldDrawHUD();

	public void func_6473_b(EntityPlayer entityPlayer1) {
		PlayerControllerCreative.disableAbilities(entityPlayer1);
	}

	public abstract boolean onPlayerRightClick(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3, int i4, int i5, int i6, int i7, float xWithinFace, float yWithinFace, float zWithinFace);

	public EntityPlayer createPlayer(World world1) {
		return new EntityPlayerSP(this.mc, world1, this.mc.session, world1.worldProvider.worldType);
	}

	public void interactWithEntity(EntityPlayer entityPlayer1, Entity entity2) {
		entityPlayer1.useCurrentItemOnEntity(entity2);
	}

	public void attackEntity(EntityPlayer entityPlayer1, Entity entity2) {
		entityPlayer1.attackTargetEntityWithCurrentItem(entity2);
	}

	public ItemStack windowClick(int i1, int i2, int i3, int var4, EntityPlayer entityPlayer5) {
		return entityPlayer5.craftingInventory.slotClick(i2, i3, var4, entityPlayer5);
	}

	public void handleCloseInventory(int i1, EntityPlayer entityPlayer2) {
		entityPlayer2.craftingInventory.onCraftGuiClosed(entityPlayer2);
		entityPlayer2.craftingInventory = entityPlayer2.inventorySlots;
	}

	public void sendEnchant(int i1, int i2) {
	}

	public boolean func_35643_e() {
		return false;
	}

	public void onStoppedUsingItem(EntityPlayer entityPlayer1) {
		entityPlayer1.stopUsingItem();
	}

	public boolean func_35642_f() {
		return false;
	}

	public boolean isNotCreative() {
		return true;
	}

	public boolean isInCreativeMode() {
		return false;
	}

	public boolean extendedReach() {
		return false;
	}

	public void sendSlotPacket(ItemStack itemStack1, int i2) {
	}

	public void func_35639_a(ItemStack itemStack1) {
	}
}
