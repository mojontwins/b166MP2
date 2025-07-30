package net.minecraft.server;

import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class ItemInWorldManager {
	public World thisWorld;
	public EntityPlayer thisPlayer;
	private int gameType = -1;
	private int initialDamage;
	private int curBlockX;
	private int curBlockY;
	private int curBlockZ;
	private int curblockDamage;
	private boolean isRemoving;
	private int lastBlockX;
	private int lastBlockY;
	private int lastBlockZ;
	private int lastDamage;

	public ItemInWorldManager(World world1) {
		this.thisWorld = world1;
	}

	public void toggleGameType(int i1) {
		this.gameType = i1;
		if(i1 == 0) {
			this.thisPlayer.capabilities.allowFlying = false;
			this.thisPlayer.capabilities.isFlying = false;
			this.thisPlayer.capabilities.isCreativeMode = false;
			this.thisPlayer.capabilities.disableDamage = false;
		} else {
			this.thisPlayer.capabilities.allowFlying = true;
			this.thisPlayer.capabilities.isCreativeMode = true;
			this.thisPlayer.capabilities.disableDamage = true;
		}

		this.thisPlayer.func_50009_aI();
	}

	public int getGameType() {
		return this.gameType;
	}

	public boolean isCreative() {
		return this.gameType == 1;
	}

	public void setGameMode(int i1) {
		if(this.gameType == -1) {
			this.gameType = i1;
		}

		this.toggleGameType(this.gameType);
	}

	public void updateBlockRemoving() {
		++this.curblockDamage;
		if(this.isRemoving) {
			int i1 = this.curblockDamage - this.lastDamage;
			int i2 = this.thisWorld.getBlockId(this.lastBlockX, this.lastBlockY, this.lastBlockZ);
			int meta = this.thisWorld.getBlockId(this.lastBlockX, this.lastBlockY, this.lastBlockZ);
			if(i2 != 0) {
				Block block3 = Block.blocksList[i2];
				float f4 = block3.blockStrength(this.thisPlayer, meta) * (float)(i1 + 1);
				if(f4 >= 1.0F) {
					this.isRemoving = false;
					this.blockHarvessted(this.lastBlockX, this.lastBlockY, this.lastBlockZ);
				}
			} else {
				this.isRemoving = false;
			}
		}

	}

	public void blockClicked(int i1, int i2, int i3, int i4) {
		if(this.isCreative()) {
			if(!this.thisWorld.onBlockHit((EntityPlayer)null, i1, i2, i3, i4)) {
				this.blockHarvessted(i1, i2, i3);
			}

		} else {
			this.thisWorld.onBlockHit((EntityPlayer)null, i1, i2, i3, i4);
			this.initialDamage = this.curblockDamage;
			int i5 = this.thisWorld.getBlockId(i1, i2, i3);
			int meta = this.thisWorld.getBlockMetadata(i1, i2, i3);
			if(i5 > 0) {
				Block.blocksList[i5].onBlockClicked(this.thisWorld, i1, i2, i3, this.thisPlayer);
			}

			if(i5 > 0 && Block.blocksList[i5].blockStrength(this.thisPlayer, meta) >= 1.0F) {
				this.blockHarvessted(i1, i2, i3);
			} else {
				this.curBlockX = i1;
				this.curBlockY = i2;
				this.curBlockZ = i3;
			}

		}
	}

	public void blockRemoving(int i1, int i2, int i3) {
		if(i1 == this.curBlockX && i2 == this.curBlockY && i3 == this.curBlockZ) {
			int i4 = this.curblockDamage - this.initialDamage;
			int i5 = this.thisWorld.getBlockId(i1, i2, i3);
			int meta = this.thisWorld.getBlockMetadata(i1, i2, i3);
			if(i5 != 0) {
				Block block6 = Block.blocksList[i5];
				float f7 = block6.blockStrength(this.thisPlayer, meta) * (float)(i4 + 1);
				if(f7 >= 0.7F) {
					this.blockHarvessted(i1, i2, i3);
				} else if(!this.isRemoving) {
					this.isRemoving = true;
					this.lastBlockX = i1;
					this.lastBlockY = i2;
					this.lastBlockZ = i3;
					this.lastDamage = this.initialDamage;
				}
			}
		}
	}

	public boolean removeBlock(int i1, int i2, int i3) {
		Block block4 = Block.blocksList[this.thisWorld.getBlockId(i1, i2, i3)];
		int i5 = this.thisWorld.getBlockMetadata(i1, i2, i3);
		boolean z6 = this.thisWorld.setBlockWithNotify(i1, i2, i3, 0);
		if(block4 != null && z6) {
			block4.onBlockDestroyedByPlayer(this.thisWorld, i1, i2, i3, i5);
		}

		return z6;
	}

	public boolean blockHarvessted(int i1, int i2, int i3) {
		int i4 = this.thisWorld.getBlockId(i1, i2, i3);
		int i5 = this.thisWorld.getBlockMetadata(i1, i2, i3);
		this.thisWorld.playAuxSFXAtEntity(this.thisPlayer, 2001, i1, i2, i3, i4 + (this.thisWorld.getBlockMetadata(i1, i2, i3) << 12));
		boolean z6 = this.removeBlock(i1, i2, i3);
		if(this.isCreative()) {
			((EntityPlayerMP)this.thisPlayer).playerNetServerHandler.sendPacket(new Packet53BlockChange(i1, i2, i3, this.thisWorld));
		} else {
			ItemStack itemStack7 = this.thisPlayer.getCurrentEquippedItem();
			boolean z8 = this.thisPlayer.canHarvestBlock(Block.blocksList[i4], i5);
			if(itemStack7 != null) {
				itemStack7.onDestroyBlock(this.thisWorld, i4, i1, i2, i3, this.thisPlayer);
				if(itemStack7.stackSize == 0) {
					itemStack7.onItemDestroyedByUse(this.thisPlayer);
					this.thisPlayer.destroyCurrentEquippedItem();
				}
			}

			if(z6 && z8) {
				Block.blocksList[i4].harvestBlock(this.thisWorld, this.thisPlayer, i1, i2, i3, i5);
			}
		}

		return z6;
	}

	public boolean itemUsed(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3) {
		int i4 = itemStack3.stackSize;
		int i5 = itemStack3.getItemDamage();
		ItemStack itemStack6 = itemStack3.useItemRightClick(world2, entityPlayer1);
		if(itemStack6 == itemStack3 && (itemStack6 == null || itemStack6.stackSize == i4) && (itemStack6 == null || itemStack6.getMaxItemUseDuration() <= 0)) {
			return false;
		} else {
			entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentItem] = itemStack6;
			if(this.isCreative()) {
				itemStack6.stackSize = i4;
				itemStack6.setItemDamage(i5);
			}

			if(itemStack6.stackSize == 0) {
				entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentItem] = null;
			}

			return true;
		}
	}

	public boolean activeBlockOrUseItem(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3, int i4, int i5, int i6, int i7, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		int i8 = world2.getBlockId(i4, i5, i6);
		if(i8 > 0 && Block.blocksList[i8].blockActivated(world2, i4, i5, i6, entityPlayer1, i7, xWithinFace, yWithinFace, zWithinFace)) {
			return true;
		} else if(itemStack3 == null) {
			return false;
		} else if(this.isCreative()) {
			int i9 = itemStack3.getItemDamage();
			int i10 = itemStack3.stackSize;
			boolean z11 = itemStack3.useItem(entityPlayer1, world2, i4, i5, i6, i7, xWithinFace, yWithinFace, zWithinFace, keyPressed);
			itemStack3.setItemDamage(i9);
			itemStack3.stackSize = i10;
			return z11;
		} else {
			return itemStack3.useItem(entityPlayer1, world2, i4, i5, i6, i7, xWithinFace, yWithinFace, zWithinFace, keyPressed);
		}
	}

	public void setWorld(WorldServer worldServer1) {
		this.thisWorld = worldServer1;
	}
}
