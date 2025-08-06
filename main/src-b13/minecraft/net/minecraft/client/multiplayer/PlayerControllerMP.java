package net.minecraft.client.multiplayer;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.PlayerController;
import net.minecraft.client.player.PlayerControllerCreative;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraft.network.packet.Packet107CreativeSetSlot;
import net.minecraft.network.packet.Packet108EnchantItem;
import net.minecraft.network.packet.Packet14BlockDig;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet7UseEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class PlayerControllerMP extends PlayerController {
	private int currentBlockX = -1;
	private int currentBlockY = -1;
	private int currentblockZ = -1;
	private float curBlockDamageMP = 0.0F;
	private float prevBlockDamageMP = 0.0F;
	private float stepSoundTickCounter = 0.0F;
	private int blockHitDelay = 0;
	private boolean isHittingBlock = false;
	private boolean creativeMode;
	private NetClientHandler netClientHandler;
	private int currentPlayerItem = 0;

	public PlayerControllerMP(Minecraft mc, NetClientHandler netClientHandler2) {
		super(mc);
		this.netClientHandler = netClientHandler2;
	}

	public void setCreative(boolean z1) {
		this.creativeMode = z1;
		if(this.creativeMode) {
			PlayerControllerCreative.enableAbilities(this.mc.thePlayer);
		} else {
			PlayerControllerCreative.disableAbilities(this.mc.thePlayer);
		}

	}

	public void flipPlayer(EntityPlayer entityPlayer1) {
		entityPlayer1.rotationYaw = -180.0F;
	}

	public boolean shouldDrawHUD() {
		return !this.creativeMode;
	}

	public boolean onPlayerDestroyBlock(int i1, int i2, int i3, int i4) {
		if(this.creativeMode) {
			return super.onPlayerDestroyBlock(i1, i2, i3, i4);
		} else {
			int i5 = this.mc.theWorld.getBlockId(i1, i2, i3);
			boolean z6 = super.onPlayerDestroyBlock(i1, i2, i3, i4);
			ItemStack itemStack7 = this.mc.thePlayer.getCurrentEquippedItem();
			if(itemStack7 != null) {
				itemStack7.onDestroyBlock(this.mc.theWorld, i5, i1, i2, i3, this.mc.thePlayer);
				if(itemStack7.stackSize == 0) {
					itemStack7.onItemDestroyedByUse(this.mc.thePlayer);
					this.mc.thePlayer.destroyCurrentEquippedItem();
				}
			}

			return z6;
		}
	}

	public void clickBlock(int x, int y, int z, int side) {
		if(this.creativeMode) {
			this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, x, y, z, side));
			PlayerControllerCreative.clickBlockCreative(this.mc, this, x, y, z, side);
			this.blockHitDelay = 5;
		} else if(!this.isHittingBlock || x != this.currentBlockX || y != this.currentBlockY || z != this.currentblockZ) {
			this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, x, y, z, side));
			int blockID = this.mc.theWorld.getBlockId(x, y, z);
			int metadata = this.mc.theWorld.getBlockMetadata(x, y, z);
			
			if(blockID > 0 && this.curBlockDamageMP == 0.0F) {
				Block.blocksList[blockID].onBlockClicked(this.mc.theWorld, x, y, z, this.mc.thePlayer);
			}

			if(blockID > 0 && Block.blocksList[blockID].blockStrength(this.mc.thePlayer, metadata) >= 1.0F) {
				this.onPlayerDestroyBlock(x, y, z, side);
			} else {
				this.isHittingBlock = true;
				this.currentBlockX = x;
				this.currentBlockY = y;
				this.currentblockZ = z;
				this.curBlockDamageMP = 0.0F;
				this.prevBlockDamageMP = 0.0F;
				this.stepSoundTickCounter = 0.0F;
			}
		}

	}

	public void resetBlockRemoving() {
		this.curBlockDamageMP = 0.0F;
		this.isHittingBlock = false;
	}

	public void onPlayerDamageBlock(int x, int y, int z, int side) {
		this.syncCurrentPlayItem();
		if(this.blockHitDelay > 0) {
			--this.blockHitDelay;
		} else if(this.creativeMode) {
			this.blockHitDelay = 5;
			this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, x, y, z, side));
			PlayerControllerCreative.clickBlockCreative(this.mc, this, x, y, z, side);
		} else {
			if(x == this.currentBlockX && y == this.currentBlockY && z == this.currentblockZ) {
				int blockID = this.mc.theWorld.getBlockId(x, y, z);
				int metadata = this.mc.theWorld.getBlockId(x, y, z);
				if(blockID == 0) {
					this.isHittingBlock = false;
					return;
				}

				Block block6 = Block.blocksList[blockID];
				this.curBlockDamageMP += block6.blockStrength(this.mc.thePlayer, metadata);
				if(this.stepSoundTickCounter % 4.0F == 0.0F && block6 != null) {
					this.mc.sndManager.playSound(block6.stepSound.getStepSound(), (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, (block6.stepSound.getVolume() + 1.0F) / 8.0F, block6.stepSound.getPitch() * 0.5F);
				}

				++this.stepSoundTickCounter;
				if(this.curBlockDamageMP >= 1.0F) {
					this.isHittingBlock = false;
					this.netClientHandler.addToSendQueue(new Packet14BlockDig(2, x, y, z, side));
					this.onPlayerDestroyBlock(x, y, z, side);
					this.curBlockDamageMP = 0.0F;
					this.prevBlockDamageMP = 0.0F;
					this.stepSoundTickCounter = 0.0F;
					this.blockHitDelay = 5;
				}
			} else {
				this.clickBlock(x, y, z, side);
			}

		}
	}

	public void setPartialTime(float f1) {
		if(this.curBlockDamageMP <= 0.0F) {
			this.mc.ingameGUI.damageGuiPartialTime = 0.0F;
			this.mc.renderGlobal.damagePartialTime = 0.0F;
		} else {
			float f2 = this.prevBlockDamageMP + (this.curBlockDamageMP - this.prevBlockDamageMP) * f1;
			this.mc.ingameGUI.damageGuiPartialTime = f2;
			this.mc.renderGlobal.damagePartialTime = f2;
		}

	}

	public float getBlockReachDistance() {
		return this.creativeMode ? 5.0F : 4.5F;
	}

	public void onWorldChange(World world1) {
		super.onWorldChange(world1);
	}

	public void updateController() {
		this.syncCurrentPlayItem();
		this.prevBlockDamageMP = this.curBlockDamageMP;
		this.mc.sndManager.playRandomMusicIfReady();
	}

	private void syncCurrentPlayItem() {
		int i1 = this.mc.thePlayer.inventory.currentItem;
		if(i1 != this.currentPlayerItem) {
			this.currentPlayerItem = i1;
			this.netClientHandler.addToSendQueue(new Packet16BlockItemSwitch(this.currentPlayerItem));
		}

	}

	public boolean onPlayerRightClick(EntityPlayer entityPlayer, World world, ItemStack heldStack, int x, int y, int z, int side, float xWithinFace, float yWithinFace, float zWithinFace) {
		this.syncCurrentPlayItem();
		boolean keyPressed = Keyboard.getEventKey() == Keyboard.KEY_LCONTROL;
		
		this.netClientHandler.addToSendQueue(new Packet15Place(x, y, z, side, entityPlayer.inventory.getCurrentItem(), xWithinFace, yWithinFace, zWithinFace, keyPressed));
		
		int blockID = world.getBlockId(x, y, z);
		if(blockID > 0 && 
				// Block activation is overriden by pressing LSHIFT.
				(Keyboard.getEventKey() != Keyboard.KEY_LSHIFT && Block.blocksList[blockID].blockActivated(world, x, y, z, entityPlayer, side, xWithinFace, yWithinFace, zWithinFace))
		) {
			return true;
		} else if(heldStack == null) {
			return false;
		} else if(this.creativeMode) {
			int damage = heldStack.getItemDamage();
			int size = heldStack.stackSize;
			boolean success = heldStack.useItem(entityPlayer, world, x, y, z, side, xWithinFace, yWithinFace, zWithinFace, keyPressed);
			heldStack.setItemDamage(damage);
			heldStack.stackSize = size;
			return success;
		} else {
			return heldStack.useItem(entityPlayer, world, x, y, z, side, xWithinFace, yWithinFace, zWithinFace, keyPressed);
		}
	}

	public boolean sendUseItem(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet15Place(-1, -1, -1, 255, entityPlayer1.inventory.getCurrentItem(), 0, 0, 0, false));
		boolean z4 = super.sendUseItem(entityPlayer1, world2, itemStack3);
		return z4;
	}

	public EntityPlayer createPlayer(World world1) {
		return new EntityClientPlayerMP(this.mc, world1, this.mc.session, this.netClientHandler);
	}

	public void attackEntity(EntityPlayer entityPlayer1, Entity entity2) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet7UseEntity(entityPlayer1.entityId, entity2.entityId, 1));
		entityPlayer1.attackTargetEntityWithCurrentItem(entity2);
	}

	public void interactWithEntity(EntityPlayer entityPlayer1, Entity entity2) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet7UseEntity(entityPlayer1.entityId, entity2.entityId, 0));
		entityPlayer1.useCurrentItemOnEntity(entity2);
	}

	public ItemStack windowClick(int i1, int i2, int i3, int z4, EntityPlayer entityPlayer5) {
		short s6 = entityPlayer5.craftingInventory.getNextTransactionID(entityPlayer5.inventory);
		ItemStack itemStack7 = super.windowClick(i1, i2, i3, z4, entityPlayer5);
		this.netClientHandler.addToSendQueue(new Packet102WindowClick(i1, i2, i3, z4, itemStack7, s6));
		return itemStack7;
	}

	public void sendEnchant(int i1, int i2) {
		this.netClientHandler.addToSendQueue(new Packet108EnchantItem(i1, i2));
	}

	public void sendSlotPacket(ItemStack itemStack1, int i2) {
		if(this.creativeMode) {
			this.netClientHandler.addToSendQueue(new Packet107CreativeSetSlot(i2, itemStack1));
		}

	}

	public void func_35639_a(ItemStack itemStack1) {
		if(this.creativeMode && itemStack1 != null) {
			this.netClientHandler.addToSendQueue(new Packet107CreativeSetSlot(-1, itemStack1));
		}

	}

	public void handleCloseInventory(int i1, EntityPlayer entityPlayer2) {
		if(i1 != -9999) {
			;
		}
	}

	public void onStoppedUsingItem(EntityPlayer entityPlayer1) {
		this.syncCurrentPlayItem();
		this.netClientHandler.addToSendQueue(new Packet14BlockDig(5, 0, 0, 0, 255));
		super.onStoppedUsingItem(entityPlayer1);
	}

	public boolean func_35642_f() {
		return true;
	}

	public boolean isNotCreative() {
		return !this.creativeMode;
	}

	public boolean isInCreativeMode() {
		return this.creativeMode;
	}

	public boolean extendedReach() {
		return this.creativeMode;
	}
}
