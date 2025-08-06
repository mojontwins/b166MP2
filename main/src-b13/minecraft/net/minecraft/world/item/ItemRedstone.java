package net.minecraft.world.item;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemRedstone extends Item {
	public ItemRedstone(int i1) {
		super(i1);
		
		this.displayOnCreativeTab = CreativeTabs.tabRedstone;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		
		if(world.getBlockId(x, y, z) != Block.snow.blockID) {
			if(side == 0) {
				--y;
			}

			if(side == 1) {
				++y;
			}

			if(side == 2) {
				--z;
			}

			if(side == 3) {
				++z;
			}

			if(side == 4) {
				--x;
			}

			if(side == 5) {
				++x;
			}

			if(!world.isAirBlock(x, y, z)) {
				return false;
			}
		}

		if(!entityPlayer.canPlayerEdit(x, y, z)) {
			return false;
		} else {
			if(Block.redstoneWire.canPlaceBlockAt(world, x, y, z)) {
				--itemStack.stackSize;
				world.setBlockWithNotify(x, y, z, Block.redstoneWire.blockID);
			}

			return true;
		}
	}
}
