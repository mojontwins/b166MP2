package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockDeadBush extends BlockFlower {
	protected BlockDeadBush(int i1, int i2) {
		super(i1, i2, Material.vine);
		float f3 = 0.4F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, 0.8F, 0.5F + f3);
	}

	protected boolean canThisPlantGrowOnThisBlockID(int i1) {
		return i1 == Block.sand.blockID;
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return this.blockIndexInTexture;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return -1;
	}

	public void harvestBlock(World world1, EntityPlayer entityPlayer2, int i3, int i4, int i5, int i6) {
		if(!world1.isRemote && entityPlayer2.getCurrentEquippedItem() != null && entityPlayer2.getCurrentEquippedItem().itemID == Item.shears.shiftedIndex) {
			this.dropBlockAsItem_do(world1, i3, i4, i5, new ItemStack(Block.deadBush, 1, i6));
		} else {
			super.harvestBlock(world1, entityPlayer2, i3, i4, i5, i6);
		}

	}
}
