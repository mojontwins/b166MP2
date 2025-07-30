package net.minecraft.world.item;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockStep;
import net.minecraft.world.level.tile.entity.TileEntitySign;

public class ItemSign extends Item {
	public ItemSign(int i1) {
		super(i1);
		this.maxStackSize = 1;
		
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public boolean blockIsValid(World world, int x, int y, int z, int side) {
		int blockID = world.getBlockId(x, y, z);
		
		Block block = Block.blocksList[blockID];
		if(block == null) return false;
	
		int meta = world.getBlockMetadata(x, y, z);
		
		if(block instanceof BlockStep) {
			if((meta & 8) == 8) return true;
			return side != 1;
		}
				
		if (world.getBlockMaterial(x, y, z).isSolid()) return true;
		
		return false;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		if(side == 0) {
			return false;
		} else if(!this.blockIsValid(world, x, y, z, side)) {
			return false;
		} else {

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

			if(!entityPlayer.canPlayerEdit(x, y, z)) {
				return false;
			} else if(!Block.signPost.canPlaceBlockAt(world, x, y, z)) {
				return false;
			} else {
				if(side == 1) {
					int i8 = MathHelper.floor_double((double)((entityPlayer.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
					world.setBlockAndMetadataWithNotify(x, y, z, Block.signPost.blockID, i8);
				} else {
					world.setBlockAndMetadataWithNotify(x, y, z, Block.signWall.blockID, side);
				}

				--itemStack.stackSize;
				TileEntitySign tileEntitySign9 = (TileEntitySign)world.getBlockTileEntity(x, y, z);
				if(tileEntitySign9 != null) {
					entityPlayer.displayGUIEditSign(tileEntitySign9);
				}

				return true;
			}
		}
	}
}
