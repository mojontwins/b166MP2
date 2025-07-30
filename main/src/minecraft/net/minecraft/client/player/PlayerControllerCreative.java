package net.minecraft.client.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class PlayerControllerCreative extends PlayerController {
	private int field_35647_c;

	public PlayerControllerCreative(Minecraft mc) {
		super(mc);
		this.isInTestMode = true;
	}

	public static void enableAbilities(EntityPlayer entityPlayer0) {
		entityPlayer0.capabilities.allowFlying = true;
		entityPlayer0.capabilities.isCreativeMode = true;
		entityPlayer0.capabilities.disableDamage = true;
	}

	public static void disableAbilities(EntityPlayer entityPlayer0) {
		entityPlayer0.capabilities.allowFlying = false;
		entityPlayer0.capabilities.isFlying = false;
		entityPlayer0.capabilities.isCreativeMode = false;
		entityPlayer0.capabilities.disableDamage = false;
	}

	public void func_6473_b(EntityPlayer entityPlayer1) {
		enableAbilities(entityPlayer1);

		for(int i2 = 0; i2 < 9; ++i2) {
			if(entityPlayer1.inventory.mainInventory[i2] == null) {
				entityPlayer1.inventory.mainInventory[i2] = new ItemStack((Block)User.registeredBlocksList.get(i2));
			}
		}

	}

	public static void clickBlockCreative(Minecraft minecraft0, PlayerController playerController1, int i2, int i3, int i4, int i5) {
		if(!minecraft0.theWorld.onBlockHit(minecraft0.thePlayer, i2, i3, i4, i5)) {
			playerController1.onPlayerDestroyBlock(i2, i3, i4, i5);
		}

	}

	public boolean onPlayerRightClick(EntityPlayer entityPlayer, World world, ItemStack heldStack, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace) {
		boolean keyPressed = Keyboard.getEventKey() == Keyboard.KEY_LCONTROL;
		int blockID = world.getBlockId(x, y, z);
		if(blockID > 0 && (Keyboard.getEventKey() != Keyboard.KEY_LSHIFT && Block.blocksList[blockID].blockActivated(world, x, y, z, entityPlayer, face, xWithinFace, yWithinFace, zWithinFace))) {
			return true;
		} else if(heldStack == null) {
			return false;
		} else {
			int i9 = heldStack.getItemDamage();
			int i10 = heldStack.stackSize;
			boolean z11 = heldStack.useItem(entityPlayer, world, x, y, z, face, xWithinFace, yWithinFace, zWithinFace, keyPressed);
			heldStack.setItemDamage(i9);
			heldStack.stackSize = i10;
			return z11;
		}
	}

	public void clickBlock(int i1, int i2, int i3, int i4) {
		clickBlockCreative(this.mc, this, i1, i2, i3, i4);
		this.field_35647_c = 5;
	}

	public void onPlayerDamageBlock(int i1, int i2, int i3, int i4) {
		--this.field_35647_c;
		if(this.field_35647_c <= 0) {
			this.field_35647_c = 5;
			clickBlockCreative(this.mc, this, i1, i2, i3, i4);
		}

	}

	public void resetBlockRemoving() {
	}

	public boolean shouldDrawHUD() {
		return false;
	}

	public void onWorldChange(World world1) {
		super.onWorldChange(world1);
	}

	public float getBlockReachDistance() {
		return 5.0F;
	}

	public boolean isNotCreative() {
		return false;
	}

	public boolean isInCreativeMode() {
		return true;
	}

	public boolean extendedReach() {
		return true;
	}
}
