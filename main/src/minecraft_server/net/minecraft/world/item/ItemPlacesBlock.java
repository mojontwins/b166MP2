package net.minecraft.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.IPlant;

public class ItemPlacesBlock extends Item {
	private int blockID;

	public ItemPlacesBlock(int id, Block block) {
		super(id);
		this.blockID = block.blockID;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		int blockID = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockID];
		
		boolean isPlant = false;
		boolean isGroundCover = false;
		
		if (block != null) {
			isPlant = block instanceof IPlant;
			isGroundCover = block.blockMaterial.isGroundCover();
		}
		
		if (blockID == Block.snow.blockID) {
			side = 1;
		} else if(!isPlant && !isGroundCover) {
			if (side == 0) {
				--y;
			}

			if (side == 1) {
				++y;
			}

			if (side == 2) {
				--z;
			}

			if (side == 3) {
				++z;
			}

			if (side == 4) {
				--x;
			}

			if (side == 5) {
				++x;
			}
		}

		if (itemStack.stackSize == 0) {
			return false;
		} else if (!entityPlayer.canPlayerEdit(x, y, z)) {
			return false;
		} else {
			// if(world.canBlockBePlacedAt(this.blockID, x, y, z, false, side)) {
			if (world.canPlaceEntityOnSide(this.blockID, x, y, z, false, side, (Entity) null)) {
				block = Block.blocksList[this.blockID];
				
				if (world.setBlockWithNotify(x, y, z, this.blockID)) {
					if (world.getBlockId(x, y, z) == this.blockID) {
						Block.blocksList[this.blockID].onBlockPlaced(world, x, y, z, side);
						Block.blocksList[this.blockID].onBlockPlacedBy(world, x, y, z, entityPlayer);
					}

					world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F),
							(double) ((float) z + 0.5F), block.stepSound.getStepSound(),
							(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
					--itemStack.stackSize;
				}
			}

			return true;
		}
	}


}
