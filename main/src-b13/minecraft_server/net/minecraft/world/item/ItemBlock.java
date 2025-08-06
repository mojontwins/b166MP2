package net.minecraft.world.item;

import java.util.List;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.IPlant;

public class ItemBlock extends Item {
	private int blockID;

	public ItemBlock(int i1) {
		super(i1);
		this.blockID = i1 + 256;
		this.setIconIndex(Block.blocksList[i1 + 256].getBlockTextureFromSide(2));
	}

	public int getBlockID() {
		return this.blockID;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		int blockID = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockID];
		
		boolean isPlant = false;
		boolean isGroundCover = false;
		
		if (block != null) {
			isPlant = block instanceof IPlant;
			isGroundCover = block.blockMaterial.isGroundCover();
		}
		
		if(blockID == Block.snow.blockID) {
			side = 1;
		} else if(!isPlant && !isGroundCover) {
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
		}

		if(itemStack.stackSize == 0) {
			return false;
		} else if(!entityPlayer.canPlayerEdit(x, y, z)) {
			return false;
		} else if(y == 255 && Block.blocksList[this.blockID].blockMaterial.isSolid()) {
			return false;
		} else if(world.canBlockBePlacedAt(this.blockID, x, y, z, false, side, itemStack)) {
			block = Block.blocksList[this.blockID];
			
			if(world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, this.getMetadata(itemStack.getItemDamage()))) {
				if(world.getBlockId(x, y, z) == this.blockID) {
					Block.blocksList[this.blockID].onBlockPlaced(world, x, y, z, side, xWithinFace, yWithinFace, zWithinFace, keyPressed);
					Block.blocksList[this.blockID].onBlockPlacedBy(world, x, y, z, entityPlayer);
				}

				world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
				--itemStack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}
	
	public String getItemNameIS(ItemStack itemStack1) {
		return Block.blocksList[this.blockID].getBlockName();
	}

	public String getItemName() {
		return Block.blocksList[this.blockID].getBlockName();
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return Block.blocksList[this.blockID].getCreativeTab();
	}
	
	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		Block.blocksList[this.blockID].getSubBlocks(par1, par2CreativeTabs, par3List);
	}
	
}
