package net.minecraft.client.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class PlayerControllerSP extends PlayerController {
	private int curBlockX = -1;
	private int curBlockY = -1;
	private int curBlockZ = -1;
	private float curBlockDamage = 0.0F;
	private float prevBlockDamage = 0.0F;
	private float blockDestroySoundCounter = 0.0F;
	private int blockHitWait = 0;

	public PlayerControllerSP(Minecraft mc) {
		super(mc);
	}

	public void flipPlayer(EntityPlayer entityPlayer) {
		entityPlayer.rotationYaw = -180.0F;
	}

	public boolean shouldDrawHUD() {
		return true;
	}

	// future equivalent : tryHarvestBlock
	public boolean onPlayerDestroyBlock(int x, int y, int z, int side) {
		int blockID = this.mc.theWorld.getBlockId(x, y, z);
		int meta = this.mc.theWorld.getBlockMetadata(x, y, z);
		boolean destroyed = super.onPlayerDestroyBlock(x, y, z, side);
		ItemStack theStack = this.mc.thePlayer.getCurrentEquippedItem();
		boolean canHarvest = this.mc.thePlayer.canHarvestBlock(Block.blocksList[blockID], meta);
		if(theStack != null) {
			theStack.onDestroyBlock(this.mc.theWorld, blockID, x, y, z, this.mc.thePlayer);
			if(theStack.stackSize == 0) {
				theStack.onItemDestroyedByUse(this.mc.thePlayer);
				this.mc.thePlayer.destroyCurrentEquippedItem();
			}
		}

		if(destroyed && canHarvest) {
			Block.blocksList[blockID].harvestBlock(this.mc.theWorld, this.mc.thePlayer, x, y, z, meta);
		}

		return destroyed;
	}

	public void clickBlock(int x, int y, int z, int side) {
		if(this.mc.thePlayer.canPlayerEdit(x, y, z)) {
			this.mc.theWorld.onBlockHit(this.mc.thePlayer, x, y, z, side);
			int blockID = this.mc.theWorld.getBlockId(x, y, z);
			int metadata = this.mc.theWorld.getBlockMetadata(x, y, z);
			if(blockID > 0 && this.curBlockDamage == 0.0F) {
				Block.blocksList[blockID].onBlockClicked(this.mc.theWorld, x, y, z, this.mc.thePlayer);
			}

			if(blockID > 0 && Block.blocksList[blockID].blockStrength(this.mc.thePlayer, metadata) >= 1.0F) {
				this.onPlayerDestroyBlock(x, y, z, side);
			}

		}
	}

	public void resetBlockRemoving() {
		this.curBlockDamage = 0.0F;
		this.blockHitWait = 0;
	}

	public void onPlayerDamageBlock(int x, int y, int z, int side) {
		if(this.blockHitWait > 0) {
			--this.blockHitWait;
		} else {
			if(x == this.curBlockX && y == this.curBlockY && z == this.curBlockZ) {
				int blockID = this.mc.theWorld.getBlockId(x, y, z);
				int metadata = this.mc.theWorld.getBlockMetadata(x, y, z);

				if(!this.mc.thePlayer.canPlayerEdit(x, y, z)) {
					return;
				}

				if(blockID == 0) {
					return;
				}

				Block block = Block.blocksList[blockID];
				this.curBlockDamage += block.blockStrength(this.mc.thePlayer, metadata);
				if(this.blockDestroySoundCounter % 4.0F == 0.0F && block != null) {
					this.mc.sndManager.playSound(block.stepSound.getStepSound(), (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getPitch() * 0.5F);
				}

				++this.blockDestroySoundCounter;
				if(this.curBlockDamage >= 1.0F) {
					this.onPlayerDestroyBlock(x, y, z, side);
					this.curBlockDamage = 0.0F;
					this.prevBlockDamage = 0.0F;
					this.blockDestroySoundCounter = 0.0F;
					this.blockHitWait = 5;
				}
			} else {
				this.curBlockDamage = 0.0F;
				this.prevBlockDamage = 0.0F;
				this.blockDestroySoundCounter = 0.0F;
				this.curBlockX = x;
				this.curBlockY = y;
				this.curBlockZ = z;
			}

		}
	}

	public void setPartialTime(float f1) {
		if(this.curBlockDamage <= 0.0F) {
			this.mc.ingameGUI.damageGuiPartialTime = 0.0F;
			this.mc.renderGlobal.damagePartialTime = 0.0F;
		} else {
			float f2 = this.prevBlockDamage + (this.curBlockDamage - this.prevBlockDamage) * f1;
			this.mc.ingameGUI.damageGuiPartialTime = f2;
			this.mc.renderGlobal.damagePartialTime = f2;
		}

	}

	public float getBlockReachDistance() {
		return 4.0F;
	}

	public void onWorldChange(World world) {
		super.onWorldChange(world);
	}

	public EntityPlayer createPlayer(World world) {
		EntityPlayer entityPlayer2 = super.createPlayer(world);
		return entityPlayer2;
	}

	public void updateController() {
		this.prevBlockDamage = this.curBlockDamage;
		this.mc.sndManager.playRandomMusicIfReady();
	}

	public boolean onPlayerRightClick(EntityPlayer entityPlayer, World world, ItemStack theStack, int x, int y, int z, int side, float xWithinFace, float yWithinFace, float zWithinFace) {
		int blockID = world.getBlockId(x, y, z);
		return blockID > 0 && 
				// Activation is overriden pressing LSHIFT.
				(Keyboard.getEventKey() != Keyboard.KEY_LSHIFT && Block.blocksList[blockID].blockActivated(world, x, y, z, entityPlayer, side, xWithinFace, yWithinFace, zWithinFace)) ?
						true 
					: 
						(theStack == null ? false : theStack.useItem(entityPlayer, world, x, y, z, side, xWithinFace, yWithinFace, zWithinFace, Keyboard.getEventKey() == Keyboard.KEY_LCONTROL));
	}

	public boolean func_35642_f() {
		return true;
	}
}
