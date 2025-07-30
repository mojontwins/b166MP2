package net.minecraft.world.item;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class ItemDoor extends Item {
	private Material doorMaterial;

	public ItemDoor(int i1, Material material2) {
		super(i1);
		this.doorMaterial = material2;
		this.maxStackSize = 1;
		
		this.displayOnCreativeTab = CreativeTabs.tabRedstone;
	}

	public boolean onItemUse(ItemStack itemStack1, EntityPlayer entityPlayer2, World world3, int i4, int i5, int i6, int i7) {
		if(i7 != 1) {
			return false;
		} else {
			++i5;
			Block block8;
			if(this.doorMaterial == Material.wood) {
				block8 = Block.doorWood;
			} else {
				block8 = Block.doorSteel;
			}

			if(entityPlayer2.canPlayerEdit(i4, i5, i6) && entityPlayer2.canPlayerEdit(i4, i5 + 1, i6)) {
				if(!block8.canPlaceBlockAt(world3, i4, i5, i6)) {
					return false;
				} else {
					int i9 = MathHelper.floor_double((double)((entityPlayer2.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
					placeDoorBlock(world3, i4, i5, i6, i9, block8);
					--itemStack1.stackSize;
					return true;
				}
			} else {
				return false;
			}
		}
	}

	public static void placeDoorBlock(World world0, int i1, int i2, int i3, int i4, Block block5) {
		byte b6 = 0;
		byte b7 = 0;
		if(i4 == 0) {
			b7 = 1;
		}

		if(i4 == 1) {
			b6 = -1;
		}

		if(i4 == 2) {
			b7 = -1;
		}

		if(i4 == 3) {
			b6 = 1;
		}

		int i8 = (world0.isBlockNormalCube(i1 - b6, i2, i3 - b7) ? 1 : 0) + (world0.isBlockNormalCube(i1 - b6, i2 + 1, i3 - b7) ? 1 : 0);
		int i9 = (world0.isBlockNormalCube(i1 + b6, i2, i3 + b7) ? 1 : 0) + (world0.isBlockNormalCube(i1 + b6, i2 + 1, i3 + b7) ? 1 : 0);
		boolean z10 = world0.getBlockId(i1 - b6, i2, i3 - b7) == block5.blockID || world0.getBlockId(i1 - b6, i2 + 1, i3 - b7) == block5.blockID;
		boolean z11 = world0.getBlockId(i1 + b6, i2, i3 + b7) == block5.blockID || world0.getBlockId(i1 + b6, i2 + 1, i3 + b7) == block5.blockID;
		boolean z12 = false;
		if(z10 && !z11) {
			z12 = true;
		} else if(i9 > i8) {
			z12 = true;
		}

		world0.editingBlocks = true;
		world0.setBlockAndMetadataWithNotify(i1, i2, i3, block5.blockID, i4);
		world0.setBlockAndMetadataWithNotify(i1, i2 + 1, i3, block5.blockID, 8 | (z12 ? 1 : 0));
		world0.editingBlocks = false;
		world0.notifyBlocksOfNeighborChange(i1, i2, i3, block5.blockID);
		world0.notifyBlocksOfNeighborChange(i1, i2 + 1, i3, block5.blockID);
	}
}
